package org.torusresearch.torusdirect;

import android.content.Context;

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
import org.torusresearch.torusdirect.types.TorusVerifierResponse;
import org.torusresearch.torusdirect.types.TorusVerifierUnionResponse;
import org.torusresearch.torusdirect.utils.Helpers;
import org.torusresearch.torusutils.TorusUtils;
import org.torusresearch.torusutils.types.TorusPublicKey;
import org.torusresearch.torusutils.types.VerifierArgs;
import org.web3j.crypto.Hash;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import java8.util.concurrent.CompletableFuture;


public class TorusDirectSdk {
    private final DirectSdkArgs directSdkArgs;
    private final FetchNodeDetails nodeDetailManager;
    private final TorusUtils torusUtils;
    private final Context context;

    public TorusDirectSdk(DirectSdkArgs _directSdkArgs, Context context) {
        this.directSdkArgs = _directSdkArgs;
        this.nodeDetailManager = new FetchNodeDetails(this.directSdkArgs.getNetwork() == TorusNetwork.TESTNET ? EthereumNetwork.ROPSTEN : EthereumNetwork.MAINNET,
                this.directSdkArgs.getProxyContractAddress());
        this.torusUtils = new TorusUtils();
        this.context = context;
        // maybe do this for caching
        this.nodeDetailManager.getNodeDetails().thenRun(() -> System.out.println("Fetched Node Details"));
    }

    public CompletableFuture<TorusLoginResponse> triggerLogin(SubVerifierDetails subVerifierDetails) {
        ILoginHandler handler = HandlerFactory.createHandler(new CreateHandlerParams(subVerifierDetails.getClientId(), subVerifierDetails.getVerifier(),
                this.directSdkArgs.getRedirectUri(), subVerifierDetails.getTypeOfLogin(), this.directSdkArgs.getBrowserRedirectUri(), subVerifierDetails.getJwtParams()));
        AtomicReference<LoginWindowResponse> loginWindowResponseAtomicReference = new AtomicReference<>();
        AtomicReference<TorusVerifierResponse> torusVerifierResponseAtomicReference = new AtomicReference<>();
        return handler.handleLoginWindow(context).thenComposeAsync(loginWindowResponse -> {
            loginWindowResponseAtomicReference.set(loginWindowResponse);
            return handler.getUserInfo(loginWindowResponse);
        }).thenComposeAsync(userInfo -> {
            HashMap<String, Object> verifierParams = new HashMap<>();
            verifierParams.put("verifier_id", userInfo.getVerifierId());
            torusVerifierResponseAtomicReference.set(userInfo);
            LoginWindowResponse response = loginWindowResponseAtomicReference.get();
            return this.getTorusKey(subVerifierDetails.getVerifier(), userInfo.getVerifierId(), verifierParams, !Helpers.isEmpty(response.getIdToken()) ? response.getIdToken() : response.getAccessToken());
        }).thenApplyAsync(torusKey -> {
            TorusVerifierResponse torusVerifierResponse = torusVerifierResponseAtomicReference.get();
            LoginWindowResponse loginWindowResponse = loginWindowResponseAtomicReference.get();
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
        for (SubVerifierDetails subVerifierDetails : subVerifierDetailsArray) {
            ILoginHandler handler = HandlerFactory.createHandler(new CreateHandlerParams(subVerifierDetails.getClientId(), subVerifierDetails.getVerifier(),
                    this.directSdkArgs.getRedirectUri(), subVerifierDetails.getTypeOfLogin(), this.directSdkArgs.getBrowserRedirectUri(), subVerifierDetails.getJwtParams()));
            LoginWindowResponse loginParams = handler.handleLoginWindow(this.context).join();
            userInfoPromises.add(handler.getUserInfo(loginParams));
            loginParamsArray.add(loginParams);
        }
        List<TorusVerifierResponse> userInfoArray = userInfoPromises.stream().map(CompletableFuture::join).collect(Collectors.toList());
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
        String aggregateIdToken = Hash.sha3(String.join(Character.toString((char) 29), aggregateIdTokenSeeds));
        aggregateVerifierParams.setVerifier_id(aggregateVerifierId);
        HashMap<String, Object> aggregateVerifierParamsHashMap = new HashMap<>();
        aggregateVerifierParamsHashMap.put("verify_params", aggregateVerifierParams.getVerify_params());
        aggregateVerifierParamsHashMap.put("sub_verifier_ids", aggregateVerifierParams.getSub_verifier_ids());
        aggregateVerifierParamsHashMap.put("verifier_id", aggregateVerifierParams.getVerifier_id());
        return this.getTorusKey(aggregateLoginParams.getVerifierIdentifier(), aggregateVerifierId, aggregateVerifierParamsHashMap, aggregateIdToken)
                .thenApplyAsync(torusKey -> {
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
        AtomicReference<NodeDetails> nodeDetailsAtomicReference = new AtomicReference<>();
        AtomicReference<TorusPublicKey> torusPublicKeyAtomicReference = new AtomicReference<>();
        return this.nodeDetailManager.getNodeDetails().thenComposeAsync((details) -> {
                    nodeDetailsAtomicReference.set(details);
            return torusUtils.getPublicAddress(details.getTorusNodeEndpoints(), details.getTorusNodePub(), new VerifierArgs(verifier, verifierId));
                }
        ).thenComposeAsync(torusPublicKey -> {
            NodeDetails details = nodeDetailsAtomicReference.get();
            torusPublicKeyAtomicReference.set(torusPublicKey);
            try {
                return torusUtils.retrieveShares(details.getTorusNodeEndpoints(), details.getTorusIndexes(), verifier, verifierParams, idToken);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }).thenApplyAsync(shareResponse -> {
            if (shareResponse == null) return null;
            if (!shareResponse.getEthAddress().toLowerCase().equals(torusPublicKeyAtomicReference.get().getAddress().toLowerCase()))
                return null;
            return new TorusKey(shareResponse.getPrivKey(), shareResponse.getEthAddress());
        });
    }
}
