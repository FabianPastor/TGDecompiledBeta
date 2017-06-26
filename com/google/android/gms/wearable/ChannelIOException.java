package com.google.android.gms.wearable;

import java.io.IOException;

public class ChannelIOException extends IOException {
    private final int zzbQX;
    private final int zzbQY;

    public ChannelIOException(String str, int i, int i2) {
        super(str);
        this.zzbQX = i;
        this.zzbQY = i2;
    }

    public int getAppSpecificErrorCode() {
        return this.zzbQY;
    }

    public int getCloseReason() {
        return this.zzbQX;
    }
}
