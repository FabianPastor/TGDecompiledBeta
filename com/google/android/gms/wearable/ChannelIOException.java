package com.google.android.gms.wearable;

import java.io.IOException;

public class ChannelIOException extends IOException {
    private final int zzlgm;
    private final int zzlgn;

    public ChannelIOException(String str, int i, int i2) {
        super(str);
        this.zzlgm = i;
        this.zzlgn = i2;
    }
}
