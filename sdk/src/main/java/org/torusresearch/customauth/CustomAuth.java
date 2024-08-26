package org.torusresearch.customauth;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import org.torusresearch.customauth.handlers.HandlerFactory;
import org.torusresearch.customauth.interfaces.ILoginHandler;
import org.torusresearch.customauth.types.AggregateLoginParams;
import org.torusresearch.customauth.types.AggregateVerifierParams;
import org.torusresearch.customauth.types.AggregateVerifierType;
import org.torusresearch.customauth.types.CreateHandlerParams;
import org.torusresearch.customauth.types.CustomAuthArgs;
import org.torusresearch.customauth.types.LoginWindowResponse;
import org.torusresearch.customauth.types.SubVerifierDetails;
import org.torusresearch.customauth.types.TorusAggregateLoginResponse;
import org.torusresearch.customauth.types.TorusLoginResponse;
import org.torusresearch.customauth.types.TorusSubVerifierInfo;
import org.torusresearch.customauth.types.TorusVerifierResponse;
import org.torusresearch.customauth.types.TorusVerifierUnionResponse;
import org.torusresearch.customauth.utils.Helpers;
import org.torusresearch.customauth.utils.Triplet;
import org.torusresearch.fetchnodedetails.FetchNodeDetails;
import org.torusresearch.fetchnodedetails.types.NodeDetails;
import org.torusresearch.torusutils.TorusUtils;
import org.torusresearch.torusutils.helpers.TorusUtilError;
import org.torusresearch.torusutils.types.VerifierParams;
import org.torusresearch.torusutils.types.VerifyParams;
import org.torusresearch.torusutils.types.common.TorusKey;
import org.torusresearch.torusutils.types.common.TorusOptions;
import org.web3j.crypto.Hash;

import java.math.BigInteger;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

public class CustomAuth {
    public final FetchNodeDetails nodeDetailManager;
    public final TorusUtils torusUtils;
    private final CustomAuthArgs customAuthArgs;
    private final Context context;

    public CustomAuth(CustomAuthArgs customAuthArgs, Context context) {
        this.customAuthArgs = customAuthArgs;
        this.nodeDetailManager = new FetchNodeDetails(this.customAuthArgs.getNetwork());

        TorusOptions opts = new TorusOptions(this.customAuthArgs.getWeb3AuthClientId(), this.customAuthArgs.getNetwork(), null, this.customAuthArgs.getServerTimeOffset(),
                this.customAuthArgs.isEnableOneKey());
        try {
            this.torusUtils = new TorusUtils(opts);
            if (this.customAuthArgs.getApiKey() != null) {
                this.torusUtils.setApiKey(this.customAuthArgs.getApiKey());
            }
        } catch (TorusUtilError e) {
            throw new RuntimeException(e);
        }
        this.context = context;
    }

    public CompletableFuture<TorusLoginResponse> triggerLogin(SubVerifierDetails subVerifierDetails) {
        ILoginHandler handler = HandlerFactory.createHandler(new CreateHandlerParams(subVerifierDetails.getClientId(), subVerifierDetails.getVerifier(),
                this.customAuthArgs.getRedirectUri(), subVerifierDetails.getTypeOfLogin(), this.customAuthArgs.getBrowserRedirectUri(), subVerifierDetails.getJwtParams()));
        return handler.handleLoginWindow(context, subVerifierDetails.getIsNewActivity(), subVerifierDetails.getPreferCustomTabs(), subVerifierDetails.getAllowedBrowsers())
                .thenComposeAsync(loginWindowResponse -> handler.getUserInfo(loginWindowResponse).thenApply((userInfo) -> Pair.create(userInfo, loginWindowResponse)))
                .thenComposeAsync(pair -> {
                    TorusVerifierResponse userInfo = pair.first;
                    LoginWindowResponse response = pair.second;
                    VerifierParams verifierParams = new VerifierParams(userInfo.getVerifierId(), null, null, null);
                    try {
                        TorusKey torusKey = this.getTorusKey(subVerifierDetails.getVerifier(), userInfo.getVerifierId(), verifierParams, !Helpers.isEmpty(response.getIdToken()) ?
                                response.getIdToken() : response.getAccessToken());
                        return CompletableFuture.completedFuture(torusKey).thenApplyAsync(torusKey1 -> Triplet.create(userInfo, response, torusKey));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }).thenApplyAsync(triplet -> {
                    TorusVerifierResponse torusVerifierResponse = triplet.first;
                    LoginWindowResponse loginWindowResponse = triplet.second;
                    TorusKey retrieveKeyResponse = triplet.third;
                    TorusVerifierUnionResponse response = new TorusVerifierUnionResponse(torusVerifierResponse.getEmail(), torusVerifierResponse.getName(), torusVerifierResponse.getProfileImage(),
                            torusVerifierResponse.getVerifier(), torusVerifierResponse.getVerifierId(), torusVerifierResponse.getTypeOfLogin());
                    response.setAccessToken(loginWindowResponse.getAccessToken());
                    response.setIdToken(loginWindowResponse.getIdToken());
                    return new TorusLoginResponse(response, new BigInteger(retrieveKeyResponse.getFinalKeyData().getPrivKey(), 16), retrieveKeyResponse.getFinalKeyData().getWalletAddress(),
                            retrieveKeyResponse,
                            retrieveKeyResponse.getFinalKeyData(),
                            retrieveKeyResponse.getFinalKeyData(),
                            retrieveKeyResponse.getMetadata(),
                            null);
                });
    }


    public CompletableFuture<TorusAggregateLoginResponse> triggerAggregateLogin(AggregateLoginParams aggregateLoginParams) {
        AggregateVerifierType aggregateVerifierType = aggregateLoginParams.getAggregateVerifierType();
        SubVerifierDetails[] subVerifierDetailsArray = aggregateLoginParams.getSubVerifierDetailsArray();
        if (aggregateVerifierType == AggregateVerifierType.SINGLE_VERIFIER_ID && subVerifierDetailsArray.length != 1) {
            throw new InvalidParameterException("Single id verifier can only have one sub verifier");
        }
        List<CompletableFuture<TorusVerifierResponse>> userInfoPromises = new ArrayList<>();
        List<LoginWindowResponse> loginParamsArray = new ArrayList<>();
        CompletableFuture<Void> loginWindowsCompletePromise = new CompletableFuture<>();
        List<LoginWindowResponse> loginWindowResponses = new ArrayList<>();
        ForkJoinPool.commonPool().execute(() -> {
            for (SubVerifierDetails subVerifierDetails : subVerifierDetailsArray) {
                ILoginHandler handler = HandlerFactory.createHandler(new CreateHandlerParams(subVerifierDetails.getClientId(), subVerifierDetails.getVerifier(),
                        this.customAuthArgs.getRedirectUri(), subVerifierDetails.getTypeOfLogin(), this.customAuthArgs.getBrowserRedirectUri(), subVerifierDetails.getJwtParams()));
                // The login windows need to be handled sequentially
                // Shouldn't open two login windows at once
                try {
                    // Cannot wait on main thread
                    LoginWindowResponse resp = handler.handleLoginWindow(context, subVerifierDetails.getIsNewActivity(), subVerifierDetails.getPreferCustomTabs(), subVerifierDetails.getAllowedBrowsers()).join();
                    // Thread safe
                    loginWindowResponses.add(resp);
                } catch (Exception e) {
                    e.printStackTrace();
                    loginWindowsCompletePromise.completeExceptionally(e);
                }
            }
            loginWindowsCompletePromise.complete(null);
        });
        return loginWindowsCompletePromise.thenComposeAsync((v) -> {
            Log.d("result:loginhandlers", "completed");
            for (int i = 0; i < loginWindowResponses.size(); i++) {
                LoginWindowResponse loginResponse = loginWindowResponses.get(i);
                SubVerifierDetails subVerifierDetails = subVerifierDetailsArray[i];
                ILoginHandler handler = HandlerFactory.createHandler(new CreateHandlerParams(subVerifierDetails.getClientId(), subVerifierDetails.getVerifier(),
                        this.customAuthArgs.getRedirectUri(), subVerifierDetails.getTypeOfLogin(), this.customAuthArgs.getBrowserRedirectUri(), subVerifierDetails.getJwtParams()));
                userInfoPromises.add(handler.getUserInfo(loginResponse));
                loginParamsArray.add(loginResponse);
            }
            return CompletableFuture.allOf(userInfoPromises.toArray(new CompletableFuture[0]));
        }).thenComposeAsync((v) -> {
            List<TorusVerifierResponse> userInfoArray = new ArrayList<>();
            for (CompletableFuture<TorusVerifierResponse> userInfoPromise :
                    userInfoPromises) {
                // All promises would have been resolved correctly by here
                userInfoArray.add(userInfoPromise.join());
            }
            AggregateVerifierParams aggregateVerifierParams = new AggregateVerifierParams();
            aggregateVerifierParams.setVerify_params(new VerifyParams[subVerifierDetailsArray.length]);
            aggregateVerifierParams.setSub_verifier_ids(new String[subVerifierDetailsArray.length]);
            List<String> aggregateIdTokenSeeds = new ArrayList<>();
            String aggregateVerifierId = "";
            for (int i = 0; i < subVerifierDetailsArray.length; i++) {
                LoginWindowResponse loginParams = loginParamsArray.get(i);
                TorusVerifierResponse userInfo = userInfoArray.get(i);
                String finalToken = !Helpers.isEmpty(loginParams.getIdToken()) ? loginParams.getIdToken() : loginParams.getAccessToken();
                aggregateVerifierParams.setVerifyParamItem(new VerifyParams(userInfo.getVerifierId(), finalToken), i);
                aggregateVerifierParams.setSubVerifierIdItem(userInfo.getVerifier(), i);
                aggregateIdTokenSeeds.add(finalToken);
                aggregateVerifierId = userInfo.getVerifierId();
            }
            Collections.sort(aggregateIdTokenSeeds);
            String aggregateTokenString = TextUtils.join(Character.toString((char) 29), aggregateIdTokenSeeds);
            String aggregateIdToken = Hash.sha3String(aggregateTokenString).substring(2);
            aggregateVerifierParams.setVerifier_id(aggregateVerifierId);
            VerifierParams verifierParams = new VerifierParams(aggregateVerifierParams.getVerifier_id(), null,
                    aggregateVerifierParams.getSub_verifier_ids(), aggregateVerifierParams.getVerify_params());
            try {
                TorusKey torusKey = this.getTorusKey(aggregateLoginParams.getVerifierIdentifier(), aggregateVerifierId, verifierParams, aggregateIdToken);
                return CompletableFuture.completedFuture(Pair.create(userInfoArray, torusKey));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).thenApplyAsync(pair -> {
            TorusKey retrieveKeyResponse = pair.second;
            List<TorusVerifierResponse> userInfoArray = pair.first;
            TorusVerifierUnionResponse[] unionResponses = new TorusVerifierUnionResponse[subVerifierDetailsArray.length];
            for (int i = 0; i < subVerifierDetailsArray.length; i++) {
                TorusVerifierResponse x = userInfoArray.get(i);
                LoginWindowResponse y = loginParamsArray.get(i);
                unionResponses[i] = new TorusVerifierUnionResponse(x.getEmail(), x.getName(), x.getProfileImage(), x.getVerifier(), x.getVerifierId(), x.getTypeOfLogin());
                unionResponses[i].setAccessToken(y.getAccessToken());
                unionResponses[i].setIdToken(y.getIdToken());
            }
            return new TorusAggregateLoginResponse(unionResponses, new BigInteger(retrieveKeyResponse.getFinalKeyData().getPrivKey(), 16),
                    retrieveKeyResponse.getFinalKeyData().getWalletAddress(),
                    retrieveKeyResponse);
        });
    }

    public TorusKey getTorusKey(String verifier, String verifierId, VerifierParams verifierParams, String idToken) throws Exception {
        NodeDetails details = this.nodeDetailManager.getNodeDetails(verifier, verifierId).get();
        return torusUtils.retrieveShares(getTorusNodeEndpoints(details), verifier, verifierParams, idToken, null);
    }

    public TorusKey getAggregateTorusKey(String verifier, String verifierId, TorusSubVerifierInfo[] subVerifierInfoArray) throws Exception {
        AggregateVerifierParams aggregateVerifierParams = new AggregateVerifierParams();
        aggregateVerifierParams.setVerify_params(new VerifyParams[subVerifierInfoArray.length]);
        aggregateVerifierParams.setSub_verifier_ids(new String[subVerifierInfoArray.length]);
        List<String> aggregateIdTokenSeeds = new ArrayList<>();
        String aggregateVerifierId = "";
        for (int i = 0; i < subVerifierInfoArray.length; i++) {
            TorusSubVerifierInfo userInfo = subVerifierInfoArray[i];
            String finalToken = userInfo.getIdToken();
            aggregateVerifierParams.setVerifyParamItem(new VerifyParams(verifierId, finalToken), i);
            aggregateVerifierParams.setSubVerifierIdItem(userInfo.getVerifier(), i);
            aggregateIdTokenSeeds.add(finalToken);
            aggregateVerifierId = verifierId;
        }
        Collections.sort(aggregateIdTokenSeeds);
        String aggregateTokenString = TextUtils.join(Character.toString((char) 29), aggregateIdTokenSeeds);
        String aggregateIdToken = Hash.sha3String(aggregateTokenString).substring(2);
        aggregateVerifierParams.setVerifier_id(aggregateVerifierId);
        VerifierParams verifierParams = new VerifierParams(aggregateVerifierParams.getVerifier_id(), null,
                aggregateVerifierParams.getSub_verifier_ids(), aggregateVerifierParams.getVerify_params());
        return this.getTorusKey(verifier, aggregateVerifierId, verifierParams, aggregateIdToken);
    }

    private String[] getTorusEndpoints(NodeDetails nodeDetails) {
        if(customAuthArgs.getNetwork().toString().contains("sapphire")) {
            return getTorusNodeSSSEndpoints(nodeDetails);
        } else {
            return getTorusNodeEndpoints(nodeDetails);
        }
    }

    private String[] getTorusNodeEndpoints(NodeDetails nodeDetails) {
        return nodeDetails.getTorusNodeEndpoints();
    }

    private String[] getTorusNodeSSSEndpoints(NodeDetails nodeDetails) {
        return nodeDetails.getTorusNodeSSSEndpoints();
    }
}
