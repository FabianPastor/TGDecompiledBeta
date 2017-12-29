package net.hockeyapp.android.objects;

import java.io.Serializable;

public class ErrorObject implements Serializable {
    private String mMessage;

    public String getMessage() {
        return this.mMessage;
    }

    public void setMessage(String message) {
        this.mMessage = message;
    }
}
