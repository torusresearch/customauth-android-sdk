package org.torusresearch.torusdirect;

import android.content.Context;
import android.content.Intent;

import org.torusresearch.fetchnodedetails.FetchNodeDetails;
import org.torusresearch.fetchnodedetails.types.NodeDetails;
import org.torusresearch.torusdirect.handlers.GoogleHandler;
import org.torusresearch.torusdirect.handlers.ILoginHandler;
import org.torusresearch.torusdirect.types.DirectSdkArgs;
import org.torusresearch.torusdirect.types.TorusKey;
import org.torusresearch.torusutils.TorusUtils;
import org.torusresearch.torusutils.types.RetrieveSharesResponse;
import org.torusresearch.torusutils.types.TorusPublicKey;
import org.torusresearch.torusutils.types.VerifierArgs;
import org.w3c.dom.Node;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicReference;


public class TorusDirectSdk {
    private final DirectSdkArgs directSdkArgs;
    private final FetchNodeDetails nodeDetailManager;
    private final Context context;
    private final String instanceId = UUID.randomUUID().toString();

    public TorusDirectSdk(Context _context, DirectSdkArgs _directSdkArgs) {
        context = _context;
        this.directSdkArgs = _directSdkArgs;
        if (this.directSdkArgs.getNetwork() != null && this.directSdkArgs.getProxyContractAddress() != null)
            this.nodeDetailManager = new FetchNodeDetails(this.directSdkArgs.getNetwork(), this.directSdkArgs.getProxyContractAddress());
        else
            this.nodeDetailManager = new FetchNodeDetails();
    }

    public Intent triggerLogin(String loginType, String verifier) {
        ILoginHandler handler = new GoogleHandler(this.instanceId, this.directSdkArgs.getGoogleClientId(), this.directSdkArgs.getRedirectUri(), verifier);
        return handler.handleLogin(this.context);
//        return CompletableFuture.supplyAsync(() -> new TorusLoginResponse(null, null, null, null, null, null, null));
    }

    public CompletableFuture<TorusKey> handleLogin(String verifier, String verifierId, HashMap<String, Object> verifierParams, String idToken) {
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
        }).thenComposeAsync(shareResponse -> {
            if (shareResponse == null) return null;
            if (!shareResponse.getEthAddress().equals(torusPublicKeyAtomicReference.get().getAddress())) return null;
            return CompletableFuture.supplyAsync(() -> new TorusKey(shareResponse.getPrivKey(), shareResponse.getEthAddress()));
        });
    }
}
