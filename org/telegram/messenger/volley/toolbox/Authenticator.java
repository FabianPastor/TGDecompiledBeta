package org.telegram.messenger.volley.toolbox;

import org.telegram.messenger.volley.AuthFailureError;

public interface Authenticator {
    String getAuthToken() throws AuthFailureError;

    void invalidateAuthToken(String str);
}
