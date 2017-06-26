package com.google.android.gms.internal;

import java.util.Iterator;

final class zzcew implements Iterator<String> {
    private Iterator<String> zzbpK = this.zzbpL.zzbpJ.keySet().iterator();
    private /* synthetic */ zzcev zzbpL;

    zzcew(zzcev com_google_android_gms_internal_zzcev) {
        this.zzbpL = com_google_android_gms_internal_zzcev;
    }

    public final boolean hasNext() {
        return this.zzbpK.hasNext();
    }

    public final /* synthetic */ Object next() {
        return (String) this.zzbpK.next();
    }

    public final void remove() {
        throw new UnsupportedOperationException("Remove not supported");
    }
}
