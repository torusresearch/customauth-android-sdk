package org.torusresearch.torusdirect.handlers;

import android.content.Context;
import android.content.Intent;

import org.torusresearch.torusdirect.types.State;

import java.io.Serializable;

public interface ILoginHandler extends Serializable {
    String getScope();
    String getResponseType();
    String getState();
    String getFinalUrl();
    Intent handleLogin(Context ctx);
}
