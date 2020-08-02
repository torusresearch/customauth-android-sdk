package org.torusresearch.torusdirect;

import android.content.Context;

import org.torusresearch.fetchnodedetails.FetchNodeDetails;
import org.torusresearch.fetchnodedetails.types.EthereumNetwork;
import org.torusresearch.fetchnodedetails.types.NodeDetails;
import org.torusresearch.torusdirect.handlers.HandlerFactory;
import org.torusresearch.torusdirect.interfaces.ILoginHandler;
import org.torusresearch.torusdirect.types.CreateHandlerParams;
import org.torusresearch.torusdirect.types.DirectSdkArgs;
import org.torusresearch.torusdirect.types.LoginWindowResponse;
import org.torusresearch.torusdirect.types.SubVerifierDetails;
import org.torusresearch.torusdirect.types.TorusKey;
import org.torusresearch.torusdirect.types.TorusLoginResponse;
import org.torusresearch.torusdirect.types.TorusNetwork;
import org.torusresearch.torusdirect.types.TorusVerifierResponse;
import org.torusresearch.torusdirect.types.TorusVerifierUnionResponse;
import org.torusresearch.torusdirect.utils.Helpers;
import org.torusresearch.torusutils.TorusUtils;
import org.torusresearch.torusutils.types.TorusPublicKey;
import org.torusresearch.torusutils.types.VerifierArgs;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;


public class TorusDirectSdk {
    private final DirectSdkArgs directSdkArgs;
    private final FetchNodeDetails nodeDetailManager;
    private final Context context;

    public TorusDirectSdk(DirectSdkArgs _directSdkArgs, Context context) {
        this.directSdkArgs = _directSdkArgs;
        this.nodeDetailManager = new FetchNodeDetails(this.directSdkArgs.getNetwork() == TorusNetwork.TESTNET ? EthereumNetwork.ROPSTEN : EthereumNetwork.MAINNET,
                this.directSdkArgs.getProxyContractAddress());
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
            TorusVerifierUnionResponse response = new TorusVerifierUnionResponse(torusVerifierResponse.getEmail(), torusVerifierResponse.getName(), torusVerifierResponse.getProfileImage(),
                    torusVerifierResponse.getVerifier(), torusVerifierResponse.getVerifierId(), torusVerifierResponse.getTypeOfLogin());
            return new TorusLoginResponse(response, torusKey.getPrivateKey(), torusKey.getPublicAddress());
        });
    }

//    public CompletableFuture<TorusAggregateLoginResponse> triggerAggregateLogin(AggregateLoginParams aggregateLoginParams) {
//        AggregateVerifierType aggregateVerifierType = aggregateLoginParams.getAggregateVerifierType();
//        SubVerifierDetails[] subVerifierDetailsArray = aggregateLoginParams.getSubVerifierDetailsArray();
//        if (aggregateVerifierType == AggregateVerifierType.SINGLE_VERIFIER_ID && subVerifierDetailsArray.length != 1) {
//            throw new InvalidParameterException("Single id verifier can only have one sub verifier");
//        }
//    }

    public CompletableFuture<TorusKey> getTorusKey(String verifier, String verifierId, HashMap<String, Object> verifierParams, String idToken) {
        AtomicReference<NodeDetails> nodeDetailsAtomicReference = new AtomicReference<>();
        AtomicReference<TorusPublicKey> torusPublicKeyAtomicReference = new AtomicReference<>();
        return this.nodeDetailManager.getNodeDetails().thenComposeAsync((details) -> {
                    nodeDetailsAtomicReference.set(details);
                    return TorusUtils.getPublicAddress(details.getTorusNodeEndpoints(), details.getTorusNodePub(), new VerifierArgs(verifier, verifierId));
                }
        ).thenComposeAsync(torusPublicKey -> {
            NodeDetails details = nodeDetailsAtomicReference.get();
            torusPublicKeyAtomicReference.set(torusPublicKey);
            try {
                return TorusUtils.retrieveShares(details.getTorusNodeEndpoints(), details.getTorusIndexes(), verifier, verifierParams, idToken);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }).thenApplyAsync(shareResponse -> {
            if (shareResponse == null) return null;
            if (!shareResponse.getEthAddress().equals(torusPublicKeyAtomicReference.get().getAddress()))
                return null;
            return new TorusKey(shareResponse.getPrivKey(), shareResponse.getEthAddress());
        });
    }
}
