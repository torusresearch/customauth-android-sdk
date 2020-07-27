package org.torusresearch.torusdirect;

import java.util.UUID;

import okhttp3.HttpUrl;


public class TorusDirectSdk {
    //    private final DirectSdkArgs directSdkArgs;
//    private final FetchNodeDetails nodeDetailManager;
//    private final Context context;
    private final String instanceId = UUID.randomUUID().toString();

    public static void main(String[] args) {
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("www.google.com")
                .addPathSegment("search")
                .addQueryParameter("q", "polar bears")
                .build();
        System.out.println(url);
    }

//    public TorusDirectSdk(Context _context, DirectSdkArgs _directSdkArgs) {
////        context = _context;
////        this.directSdkArgs = _directSdkArgs;
////        if (this.directSdkArgs.getNetwork() != null && this.directSdkArgs.getProxyContractAddress() != null)
////            this.nodeDetailManager = new FetchNodeDetails(this.directSdkArgs.getNetwork(), this.directSdkArgs.getProxyContractAddress());
////        else
////            this.nodeDetailManager = new FetchNodeDetails();
//        HttpUrl url = new HttpUrl.Builder()
//                .scheme("https")
//                .host("www.google.com")
//                .addPathSegment("search")
//                .addQueryParameter("q", "polar bears")
//                .build();
//        System.out.println(url);
//    }

//    public Intent triggerLogin(String loginType, String verifier) {
//        ILoginHandler handler = new GoogleHandler(this.instanceId, this.directSdkArgs.getGoogleClientId(), this.directSdkArgs.getRedirectUri(), verifier);
//        return handler.handleLogin(this.context);
////        return CompletableFuture.supplyAsync(() -> new TorusLoginResponse(null, null, null, null, null, null, null));
//    }

//    public CompletableFuture<TorusKey> handleLogin(String verifier, String verifierId, HashMap<String, Object> verifierParams, String idToken) {
//        AtomicReference<NodeDetails> nodeDetailsAtomicReference = new AtomicReference<>();
//        AtomicReference<TorusPublicKey> torusPublicKeyAtomicReference = new AtomicReference<>();
//        return this.nodeDetailManager.getNodeDetails().thenComposeAsync((details) -> {
//            nodeDetailsAtomicReference.set(details);
//            return TorusUtils.getPublicAddress(details.getTorusNodeEndpoints(), details.getTorusNodePub(), new VerifierArgs(verifier, verifierId));
//        }
//        ).thenComposeAsync(torusPublicKey -> {
//            NodeDetails details = nodeDetailsAtomicReference.get();
//            torusPublicKeyAtomicReference.set(torusPublicKey);
//            try {
//                return TorusUtils.retrieveShares(details.getTorusNodeEndpoints(), details.getTorusIndexes(), verifier, verifierParams, idToken);
//            } catch (Exception e) {
//                e.printStackTrace();
//                return null;
//            }
//        }).thenComposeAsync(shareResponse -> {
//            if (shareResponse == null) return null;
//            if (!shareResponse.getEthAddress().equals(torusPublicKeyAtomicReference.get().getAddress())) return null;
//            return CompletableFuture.supplyAsync(() -> new TorusKey(shareResponse.getPrivKey(), shareResponse.getEthAddress()));
//        });
//    }
}
