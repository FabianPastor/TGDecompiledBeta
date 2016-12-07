package org.telegram.messenger.volley;

public interface Network {
    NetworkResponse performRequest(Request<?> request) throws VolleyError;
}
