package org.torusresearch.torusdirect.types;

import org.torusresearch.torusdirect.utils.Helpers;

import java.util.HashMap;

public class Auth0ClientOptions {
    // required
    private String domain;
    // optional
    private String client_id;
    private String leeway;
    private String verifierIdField;
    private boolean isVerifierIdCaseSensitive;
    private Display display;
    private Prompt prompt;
    private String max_age;
    private String ui_locales;
    private String id_token_hint;
    private String login_hint;
    private String acr_values;
    private String scope;
    private String audience;
    private String connection;
    private final HashMap<String, String> additionalParams;
    // internal
    private String state;
    private String response_type;
    private String nonce;

    private Auth0ClientOptions(Auth0ClientOptionsBuilder builder) {
        this.domain = builder.domain;
        this.client_id = builder.client_id;
        this.leeway = builder.leeway;
        this.verifierIdField = builder.verifierIdField;
        this.isVerifierIdCaseSensitive = builder.isVerifierIdCaseSensitive;
        this.display = builder.display;
        this.prompt = builder.prompt;
        this.max_age = builder.max_age;
        this.ui_locales = builder.ui_locales;
        this.id_token_hint = builder.id_token_hint;
        this.login_hint = builder.login_hint;
        this.acr_values = builder.acr_values;
        this.scope = builder.scope;
        this.audience = builder.audience;
        this.connection = builder.connection;
        this.additionalParams = builder.additionalParams;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getResponse_type() {
        return response_type;
    }

    public void setResponse_type(String response_type) {
        this.response_type = response_type;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getDomain() {
        return domain;
    }

    public String getClient_id() {
        return client_id;
    }

    public String getLeeway() {
        return leeway;
    }

    public String getVerifierIdField() {
        return verifierIdField;
    }

    public boolean getVerifierIdCaseSensitive() {
        return isVerifierIdCaseSensitive;
    }

    public Display getDisplay() {
        return display;
    }

    public Prompt getPrompt() {
        return prompt;
    }

    public String getMax_age() {
        return max_age;
    }

    public String getUi_locales() {
        return ui_locales;
    }

    public String getId_token_hint() {
        return id_token_hint;
    }

    public String getLogin_hint() {
        return login_hint;
    }

    public String getAcr_values() {
        return acr_values;
    }

    public String getScope() {
        return scope;
    }

    public String getAudience() {
        return audience;
    }

    public String getConnection() {
        return connection;
    }

    public HashMap<String, String> getAdditionalParams() {
        return additionalParams;
    }


    public Auth0ClientOptions merge(Auth0ClientOptions options) {
        this.domain = Helpers.mergeValue(this.domain, options.getDomain());
        this.client_id = Helpers.mergeValue(this.client_id, options.getClient_id());
        this.leeway = Helpers.mergeValue(this.leeway, options.getLeeway());
        this.verifierIdField = Helpers.mergeValue(this.verifierIdField, options.getVerifierIdField());
        this.isVerifierIdCaseSensitive = options.getVerifierIdCaseSensitive();
        this.display = Helpers.mergeValue(this.display, options.getDisplay());
        this.prompt = Helpers.mergeValue(this.prompt, options.getPrompt());
        this.max_age = Helpers.mergeValue(this.max_age, options.getMax_age());
        this.ui_locales = Helpers.mergeValue(this.ui_locales, options.getUi_locales());
        this.id_token_hint = Helpers.mergeValue(this.id_token_hint, options.getId_token_hint());
        this.login_hint = Helpers.mergeValue(this.login_hint, options.getLogin_hint());
        this.acr_values = Helpers.mergeValue(this.acr_values, options.getAcr_values());
        this.scope = Helpers.mergeValue(this.scope, options.getScope());
        this.audience = Helpers.mergeValue(this.audience, options.getAudience());
        this.connection = Helpers.mergeValue(this.connection, options.getConnection());
        this.additionalParams.putAll(options.getAdditionalParams());
        return this;
    }

    public static class Auth0ClientOptionsBuilder {
        // required
        private final String domain;
        // optional
        private String client_id;
        private String leeway;
        private String verifierIdField;
        private boolean isVerifierIdCaseSensitive = true;
        private Display display;
        private Prompt prompt;
        private String max_age;
        private String ui_locales;
        private String id_token_hint;
        private String login_hint;
        private String acr_values;
        private String scope;
        private String audience;
        private String connection;
        private HashMap<String, String> additionalParams = new HashMap<>();

        public Auth0ClientOptionsBuilder(String domain) {
            this.domain = domain;
        }

        public Auth0ClientOptionsBuilder setClient_id(String client_id) {
            this.client_id = client_id;
            return this;
        }

        public Auth0ClientOptionsBuilder setLeeway(String leeway) {
            this.leeway = leeway;
            return this;
        }

        public Auth0ClientOptionsBuilder setVerifierIdField(String verifierIdField) {
            this.verifierIdField = verifierIdField;
            return this;
        }

        public Auth0ClientOptionsBuilder setVerifierIdCaseSensitive(boolean verifierIdCaseSensitive) {
            this.isVerifierIdCaseSensitive = verifierIdCaseSensitive;
            return this;
        }

        public Auth0ClientOptionsBuilder setDisplay(Display display) {
            this.display = display;
            return this;
        }

        public Auth0ClientOptionsBuilder setPrompt(Prompt prompt) {
            this.prompt = prompt;
            return this;
        }

        public Auth0ClientOptionsBuilder setMax_age(String max_age) {
            this.max_age = max_age;
            return this;
        }

        public Auth0ClientOptionsBuilder setUi_locales(String ui_locales) {
            this.ui_locales = ui_locales;
            return this;
        }

        public Auth0ClientOptionsBuilder setId_token_hint(String id_token_hint) {
            this.id_token_hint = id_token_hint;
            return this;
        }

        public Auth0ClientOptionsBuilder setLogin_hint(String login_hint) {
            this.login_hint = login_hint;
            return this;
        }

        public Auth0ClientOptionsBuilder setAcr_values(String acr_values) {
            this.acr_values = acr_values;
            return this;
        }

        public Auth0ClientOptionsBuilder setScope(String scope) {
            this.scope = scope;
            return this;
        }

        public Auth0ClientOptionsBuilder setAudience(String audience) {
            this.audience = audience;
            return this;
        }

        public Auth0ClientOptionsBuilder setConnection(String connection) {
            this.connection = connection;
            return this;
        }

        public Auth0ClientOptionsBuilder setAdditionalParams(HashMap<String, String> additionalParams) {
            this.additionalParams = additionalParams;
            return this;
        }

        public Auth0ClientOptions build() {
            return new Auth0ClientOptions(this);
        }
    }
}
