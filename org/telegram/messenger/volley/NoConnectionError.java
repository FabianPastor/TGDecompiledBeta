package org.telegram.messenger.volley;

public class NoConnectionError extends NetworkError {
    public NoConnectionError(Throwable reason) {
        super(reason);
    }
}
