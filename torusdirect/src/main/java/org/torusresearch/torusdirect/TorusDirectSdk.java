package org.torusresearch.torusdirect;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import org.torusresearch.fetchnodedetails.FetchNodeDetails;
import org.torusresearch.fetchnodedetails.types.EthereumNetwork;
import org.torusresearch.fetchnodedetails.types.NodeDetails;
import org.torusresearch.torusdirect.handlers.HandlerFactory;
import org.torusresearch.torusdirect.interfaces.ILoginHandler;
import org.torusresearch.torusdirect.types.AggregateLoginParams;
import org.torusresearch.torusdirect.types.AggregateVerifierParams;
import org.torusresearch.torusdirect.types.AggregateVerifierType;
import org.torusresearch.torusdirect.types.CreateHandlerParams;
import org.torusresearch.torusdirect.types.DirectSdkArgs;
import org.torusresearch.torusdirect.types.LoginWindowResponse;
import org.torusresearch.torusdirect.types.SubVerifierDetails;
import org.torusresearch.torusdirect.types.TorusAggregateLoginResponse;
import org.torusresearch.torusdirect.types.TorusKey;
import org.torusresearch.torusdirect.types.TorusLoginResponse;
import org.torusresearch.torusdirect.types.TorusNetwork;
import org.torusresearch.torusdirect.types.TorusSubVerifierInfo;
import org.torusresearch.torusdirect.types.TorusVerifierResponse;
import org.torusresearch.torusdirect.types.TorusVerifierUnionResponse;
import org.torusresearch.torusdirect.utils.Helpers;
import org.torusresearch.torusdirect.utils.Triplet;
import org.torusresearch.torusutils.TorusUtils;
import org.torusresearch.torusutils.types.RetrieveSharesResponse;
import org.torusresearch.torusutils.types.TorusPublicKey;
import org.torusresearch.torusutils.types.VerifierArgs;
import org.web3j.crypto.Hash;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import java8.util.concurrent.CompletableFuture;
import java8.util.concurrent.ForkJoinPool;

public class TorusDirectSdk {
    private final DirectSdkArgs directSdkArgs;
    private final FetchNodeDetails nodeDetailManager;
    private final TorusUtils torusUtils;
    private final Context context;

    public TorusDirectSdk(DirectSdkArgs _directSdkArgs, Context context) {
        this.directSdkArgs = _directSdkArgs;
        this.nodeDetailManager = new FetchNodeDetails(this.directSdkArgs.getNetwork() == TorusNetwork.TESTNET ? EthereumNetwork.ROPSTEN : EthereumNetwork.MAINNET,
                this.directSdkArgs.getProxyContractAddress());
        this.torusUtils = new TorusUtils(context.getPackageName());
        this.context = context;
        // maybe do this for caching
        this.nodeDetailManager.getNodeDetails().thenRun(() -> Log.d("result:torus:nodedetail", "Fetched Node Details"));
    }

    public CompletableFuture<TorusLoginResponse> triggerLogin(SubVerifierDetails subVerifierDetails) {
        ILoginHandler handler = HandlerFactory.createHandler(new CreateHandlerParams(subVerifierDetails.getClientId(), subVerifierDetails.getVerifier(),
                this.directSdkArgs.getRedirectUri(), subVerifierDetails.getTypeOfLogin(), this.directSdkArgs.getBrowserRedirectUri(), subVerifierDetails.getJwtParams()));
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
                    TorusKey torusKey = triplet.third;
                    TorusVerifierUnionResponse response = new TorusVerifierUnionResponse(torusVerifierResponse.getEmail(), torusVerifierResponse.getName(), torusVerifierResponse.getProfileImage(),
                            torusVerifierResponse.getVerifier(), torusVerifierResponse.getVerifierId(), torusVerifierResponse.getTypeOfLogin());
                    response.setAccessToken(loginWindowResponse.getAccessToken());
                    response.setIdToken(loginWindowResponse.getIdToken());
                    return new TorusLoginResponse(response, torusKey.getPrivateKey(), torusKey.getPublicAddress());
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
                        this.directSdkArgs.getRedirectUri(), subVerifierDetails.getTypeOfLogin(), this.directSdkArgs.getBrowserRedirectUri(), subVerifierDetails.getJwtParams()));
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
                        this.directSdkArgs.getRedirectUri(), subVerifierDetails.getTypeOfLogin(), this.directSdkArgs.getBrowserRedirectUri(), subVerifierDetails.getJwtParams()));
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
            TorusKey torusKey = pair.second;
            List<TorusVerifierResponse> userInfoArray = pair.first;
            TorusVerifierUnionResponse[] unionResponses = new TorusVerifierUnionResponse[subVerifierDetailsArray.length];
            for (int i = 0; i < subVerifierDetailsArray.length; i++) {
                TorusVerifierResponse x = userInfoArray.get(i);
                LoginWindowResponse y = loginParamsArray.get(i);
                unionResponses[i] = new TorusVerifierUnionResponse(x.getEmail(), x.getName(), x.getProfileImage(), x.getVerifier(), x.getVerifierId(), x.getTypeOfLogin());
                unionResponses[i].setAccessToken(y.getAccessToken());
                unionResponses[i].setIdToken(y.getIdToken());
            }
            return new TorusAggregateLoginResponse(unionResponses, torusKey.getPrivateKey(), torusKey.getPublicAddress());
        });


    }

    public CompletableFuture<TorusKey> getTorusKey(String verifier, String verifierId, HashMap<String, Object> verifierParams, String idToken) {
        return this.nodeDetailManager.getNodeDetails().thenComposeAsync((details) -> torusUtils.getPublicAddress(details.getTorusNodeEndpoints(), details.getTorusNodePub(), new VerifierArgs(verifier, verifierId))
                .thenApply((torusPublicKey) -> Pair.create(details, torusPublicKey))
        ).thenComposeAsync(pair -> {
            NodeDetails details = pair.first;
            try {
                return torusUtils.retrieveShares(details.getTorusNodeEndpoints(), details.getTorusIndexes(), verifier, verifierParams, idToken).thenApply((shareResponse) -> Pair.create(pair.second, shareResponse));
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }).thenComposeAsync(pair -> {
            RetrieveSharesResponse shareResponse = pair.second;
            TorusPublicKey torusPublicKey = pair.first;
            CompletableFuture<TorusKey> response = new CompletableFuture<>();
            if (shareResponse == null) {
                response.completeExceptionally(new Exception("Invalid Share response"));
            } else if (!shareResponse.getEthAddress().toLowerCase().equals(torusPublicKey.getAddress().toLowerCase())) {
                response.completeExceptionally(new Exception("Share response doesn't match public key response"));
            } else {
                response.complete(new TorusKey(shareResponse.getPrivKey(), shareResponse.getEthAddress()));
            }
            return response;
        });
    }

    public CompletableFuture<TorusKey> getAggregateTorusKey(String verifier, String verifierId, TorusSubVerifierInfo[] subVerifierInfoArray) {
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
}
