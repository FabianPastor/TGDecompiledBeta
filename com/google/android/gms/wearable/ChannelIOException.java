package com.google.android.gms.wearable;

import java.io.IOException;

public class ChannelIOException extends IOException {
    private final int zzbQZ;
    private final int zzbRa;

    public ChannelIOException(String str, int i, int i2) {
        super(str);
        this.zzbQZ = i;
        this.zzbRa = i2;
    }

    public int getAppSpecificErrorCode() {
        return this.zzbRa;
    }

    public int getCloseReason() {
        return this.zzbQZ;
    }
}
