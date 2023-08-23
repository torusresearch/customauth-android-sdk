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
import org.torusresearch.torusutils.helpers.Utils;
import org.torusresearch.torusutils.types.RetrieveSharesResponse;
import org.torusresearch.torusutils.types.TorusCtorOptions;
import org.torusresearch.torusutils.types.TorusPublicKey;
import org.torusresearch.torusutils.types.VerifierArgs;
import org.web3j.crypto.Hash;

import java.math.BigInteger;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

public class CustomAuth {
    public final FetchNodeDetails nodeDetailManager;
    public final TorusUtils torusUtils;
    private final CustomAuthArgs customAuthArgs;
    private final Context context;

    public CustomAuth(CustomAuthArgs _customAuthArgs, Context context) {
        this.customAuthArgs = _customAuthArgs;
        if (Utils.isEmpty(_customAuthArgs.getNetworkUrl())) {
            this.nodeDetailManager = new FetchNodeDetails(_customAuthArgs.getNetwork(),
                    CustomAuthArgs.CONTRACT_MAP.get(_customAuthArgs.getNetwork()));
        } else {
            this.nodeDetailManager = new FetchNodeDetails(_customAuthArgs.getNetworkUrl(),
                    CustomAuthArgs.CONTRACT_MAP.get(_customAuthArgs.getNetwork()));
        }

        TorusCtorOptions opts = new TorusCtorOptions(context.getPackageName());
        opts.setClientId(_customAuthArgs.getClientId());
        opts.setEnableOneKey(_customAuthArgs.isEnableOneKey());
        opts.setNetwork(_customAuthArgs.getNetwork().toString());
        opts.setSignerHost(CustomAuthArgs.SIGNER_MAP.get(_customAuthArgs.getNetwork()) + "/api/sign");
        opts.setAllowHost(CustomAuthArgs.SIGNER_MAP.get(_customAuthArgs.getNetwork()) + "/api/allow");
        this.torusUtils = new TorusUtils(opts);
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
                    HashMap<String, Object> verifierParams = new HashMap<>();
                    verifierParams.put("verifier_id", userInfo.getVerifierId());
                    return this.getTorusKey(subVerifierDetails.getVerifier(), userInfo.getVerifierId(), verifierParams, !Helpers.isEmpty(response.getIdToken()) ? response.getIdToken() : response.getAccessToken())
                            .thenApply(torusKey -> Triplet.create(userInfo, response, torusKey));
                }).thenApplyAsync(triplet -> {
                    TorusVerifierResponse torusVerifierResponse = triplet.first;
                    LoginWindowResponse loginWindowResponse = triplet.second;
                    RetrieveSharesResponse retrieveSharesResponse = triplet.third;
                    TorusVerifierUnionResponse response = new TorusVerifierUnionResponse(torusVerifierResponse.getEmail(), torusVerifierResponse.getName(), torusVerifierResponse.getProfileImage(),
                            torusVerifierResponse.getVerifier(), torusVerifierResponse.getVerifierId(), torusVerifierResponse.getTypeOfLogin());
                    response.setAccessToken(loginWindowResponse.getAccessToken());
                    response.setIdToken(loginWindowResponse.getIdToken());
                    return new TorusLoginResponse(response, new BigInteger(retrieveSharesResponse.getFinalKeyData().getPrivKey(), 16), retrieveSharesResponse.getFinalKeyData().getEvmAddress(),
                            retrieveSharesResponse,
                            retrieveSharesResponse.getFinalKeyData(),
                            retrieveSharesResponse.getFinalKeyData(),
                            retrieveSharesResponse.getMetadata(),
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
            // userInfoPromises.stream().map(CompletableFuture::join).collect(Collectors.toList())
            AggregateVerifierParams aggregateVerifierParams = new AggregateVerifierParams();
            aggregateVerifierParams.setVerify_params(new AggregateVerifierParams.VerifierParams[subVerifierDetailsArray.length]);
            aggregateVerifierParams.setSub_verifier_ids(new String[subVerifierDetailsArray.length]);
            List<String> aggregateIdTokenSeeds = new ArrayList<>();
            String aggregateVerifierId = "";
            for (int i = 0; i < subVerifierDetailsArray.length; i++) {
                LoginWindowResponse loginParams = loginParamsArray.get(i);
                TorusVerifierResponse userInfo = userInfoArray.get(i);
                String finalToken = !Helpers.isEmpty(loginParams.getIdToken()) ? loginParams.getIdToken() : loginParams.getAccessToken();
                aggregateVerifierParams.setVerifyParamItem(new AggregateVerifierParams.VerifierParams(userInfo.getVerifierId(), finalToken), i);
                aggregateVerifierParams.setSubVerifierIdItem(userInfo.getVerifier(), i);
                aggregateIdTokenSeeds.add(finalToken);
                aggregateVerifierId = userInfo.getVerifierId();
            }
            Collections.sort(aggregateIdTokenSeeds);
            String aggregateTokenString = TextUtils.join(Character.toString((char) 29), aggregateIdTokenSeeds);
            String aggregateIdToken = Hash.sha3String(aggregateTokenString).substring(2);
            aggregateVerifierParams.setVerifier_id(aggregateVerifierId);
            HashMap<String, Object> aggregateVerifierParamsHashMap = new HashMap<>();
            aggregateVerifierParamsHashMap.put("verify_params", aggregateVerifierParams.getVerify_params());
            aggregateVerifierParamsHashMap.put("sub_verifier_ids", aggregateVerifierParams.getSub_verifier_ids());
            aggregateVerifierParamsHashMap.put("verifier_id", aggregateVerifierParams.getVerifier_id());
            return this.getTorusKey(aggregateLoginParams.getVerifierIdentifier(), aggregateVerifierId, aggregateVerifierParamsHashMap, aggregateIdToken).thenApply((torusKey) -> Pair.create(userInfoArray, torusKey));
        }).thenApplyAsync(pair -> {
            RetrieveSharesResponse retrieveSharesResponse = pair.second;
            List<TorusVerifierResponse> userInfoArray = pair.first;
            TorusVerifierUnionResponse[] unionResponses = new TorusVerifierUnionResponse[subVerifierDetailsArray.length];
            for (int i = 0; i < subVerifierDetailsArray.length; i++) {
                TorusVerifierResponse x = userInfoArray.get(i);
                LoginWindowResponse y = loginParamsArray.get(i);
                unionResponses[i] = new TorusVerifierUnionResponse(x.getEmail(), x.getName(), x.getProfileImage(), x.getVerifier(), x.getVerifierId(), x.getTypeOfLogin());
                unionResponses[i].setAccessToken(y.getAccessToken());
                unionResponses[i].setIdToken(y.getIdToken());
            }
            return new TorusAggregateLoginResponse(unionResponses, new BigInteger(retrieveSharesResponse.getFinalKeyData().getPrivKey(), 16),
                    retrieveSharesResponse.getFinalKeyData().getEvmAddress(),
                    retrieveSharesResponse);
        });


    }

    public CompletableFuture<RetrieveSharesResponse> getTorusKey(String verifier, String verifierId, HashMap<String, Object> verifierParams, String idToken) {
        return this.nodeDetailManager.getNodeDetails(verifier, verifierId).thenComposeAsync((details) -> torusUtils.getPublicAddress(getTorusNodeEndpoints(details), details.getTorusNodePub(), new VerifierArgs(verifier, verifierId))
                .thenApply((torusPublicKey) -> Pair.create(details, torusPublicKey))
        ).thenComposeAsync(pair -> {
            NodeDetails details = pair.first;
            return torusUtils.retrieveShares(getTorusNodeEndpoints(details), details.getTorusIndexes(), verifier, verifierParams, idToken).thenApply((shareResponse) -> Pair.create(pair.second, shareResponse));
        }).thenComposeAsync(pair -> {
            RetrieveSharesResponse shareResponse = pair.second;
            TorusPublicKey torusPublicKey = pair.first;
            CompletableFuture<RetrieveSharesResponse> response = new CompletableFuture<>();
            if (shareResponse == null) {
                response.completeExceptionally(new Exception("Invalid Share response"));
            } else if (!shareResponse.getFinalKeyData().getEvmAddress().equalsIgnoreCase(torusPublicKey.getFinalKeyData().getEvmAddress())) {
                response.completeExceptionally(new Exception("Share response doesn't match public key response"));
            } else {
                response.complete(shareResponse);
            }
            return response;
        });
    }

    public CompletableFuture<RetrieveSharesResponse> getAggregateTorusKey(String verifier, String verifierId, TorusSubVerifierInfo[] subVerifierInfoArray) {
        AggregateVerifierParams aggregateVerifierParams = new AggregateVerifierParams();
        aggregateVerifierParams.setVerify_params(new AggregateVerifierParams.VerifierParams[subVerifierInfoArray.length]);
        aggregateVerifierParams.setSub_verifier_ids(new String[subVerifierInfoArray.length]);
        List<String> aggregateIdTokenSeeds = new ArrayList<>();
        String aggregateVerifierId = "";
        for (int i = 0; i < subVerifierInfoArray.length; i++) {
            TorusSubVerifierInfo userInfo = subVerifierInfoArray[i];
            String finalToken = userInfo.getIdToken();
            aggregateVerifierParams.setVerifyParamItem(new AggregateVerifierParams.VerifierParams(verifierId, finalToken), i);
            aggregateVerifierParams.setSubVerifierIdItem(userInfo.getVerifier(), i);
            aggregateIdTokenSeeds.add(finalToken);
            aggregateVerifierId = verifierId;
        }
        Collections.sort(aggregateIdTokenSeeds);
        String aggregateTokenString = TextUtils.join(Character.toString((char) 29), aggregateIdTokenSeeds);
        String aggregateIdToken = Hash.sha3String(aggregateTokenString).substring(2);
        aggregateVerifierParams.setVerifier_id(aggregateVerifierId);
        HashMap<String, Object> aggregateVerifierParamsHashMap = new HashMap<>();
        aggregateVerifierParamsHashMap.put("verify_params", aggregateVerifierParams.getVerify_params());
        aggregateVerifierParamsHashMap.put("sub_verifier_ids", aggregateVerifierParams.getSub_verifier_ids());
        aggregateVerifierParamsHashMap.put("verifier_id", aggregateVerifierParams.getVerifier_id());
        return this.getTorusKey(verifier, aggregateVerifierId, aggregateVerifierParamsHashMap, aggregateIdToken);
    }

    private String[] getTorusNodeEndpoints(NodeDetails nodeDetails) {
        if(customAuthArgs.getNetwork().toString().contains("sapphire")) {
            return nodeDetails.getTorusNodeSSSEndpoints();
        } else {
            return nodeDetails.getTorusNodeEndpoints();
        }
    }
}
