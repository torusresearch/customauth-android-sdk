package org.torusresearch.torusdirect.types;

public class AggregateVerifierParams {
    private VerifierParams[] verify_params;
    private String[] sub_verifier_ids;
    private String verifier_id;

    public VerifierParams[] getVerify_params() {
        return verify_params;
    }

    public void setVerify_params(VerifierParams[] verify_params) {
        this.verify_params = verify_params;
    }

    public void setVerifyParamItem(VerifierParams verify_param, int index) {
        this.verify_params[index] = verify_param;
    }

    public String[] getSub_verifier_ids() {
        return sub_verifier_ids;
    }

    public void setSub_verifier_ids(String[] sub_verifier_ids) {
        this.sub_verifier_ids = sub_verifier_ids;
    }

    public void setSubVerifierIdItem(String sub_verifier_id, int index) {
        this.sub_verifier_ids[index] = sub_verifier_id;
    }

    public String getVerifier_id() {
        return verifier_id;
    }

    public void setVerifier_id(String verifier_id) {
        this.verifier_id = verifier_id;
    }

    public static class VerifierParams {
        private String verifier_id;
        private String idtoken;

        public VerifierParams(String verifier_id, String idtoken) {
            this.verifier_id = verifier_id;
            this.idtoken = idtoken;
        }

        public String getVerifier_id() {
            return verifier_id;
        }

        public String getIdtoken() {
            return idtoken;
        }
    }
}
