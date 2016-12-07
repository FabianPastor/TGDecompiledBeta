package org.telegram.messenger.volley.toolbox;

import java.io.IOException;
import java.util.Map;
import org.apache.http.HttpResponse;
import org.telegram.messenger.volley.AuthFailureError;
import org.telegram.messenger.volley.Request;

public interface HttpStack {
    HttpResponse performRequest(Request<?> request, Map<String, String> map) throws IOException, AuthFailureError;
}
