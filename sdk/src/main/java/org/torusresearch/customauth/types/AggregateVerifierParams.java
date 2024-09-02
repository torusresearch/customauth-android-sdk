package org.torusresearch.customauth.types;

import org.jetbrains.annotations.NotNull;
import org.torusresearch.torusutils.types.VerifyParams;

public class AggregateVerifierParams {
    private VerifyParams[] verify_params;
    private String[] sub_verifier_ids;
    private String verifier_id;

    public AggregateVerifierParams(@NotNull VerifyParams[] verifyParams, @NotNull String[] subVerifierIds, @NotNull String verifierId) {
        this.verify_params = verifyParams;
        this.sub_verifier_ids = subVerifierIds;
        this.verifier_id = verifierId;
    }


    public VerifyParams[] getVerify_params() {
        return verify_params;
    }

    public String[] getSub_verifier_ids() {
        return sub_verifier_ids;
    }

    public String getVerifier_id() {
        return verifier_id;
    }

}
