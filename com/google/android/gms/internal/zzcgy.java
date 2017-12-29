package com.google.android.gms.internal;

import java.util.Iterator;

final class zzcgy implements Iterator<String> {
    private Iterator<String> zzizr = this.zzizs.zzebe.keySet().iterator();
    private /* synthetic */ zzcgx zzizs;

    zzcgy(zzcgx com_google_android_gms_internal_zzcgx) {
        this.zzizs = com_google_android_gms_internal_zzcgx;
    }

    public final boolean hasNext() {
        return this.zzizr.hasNext();
    }

    public final /* synthetic */ Object next() {
        return (String) this.zzizr.next();
    }

    public final void remove() {
        throw new UnsupportedOperationException("Remove not supported");
    }
}
