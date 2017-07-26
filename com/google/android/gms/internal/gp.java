package com.google.android.gms.internal;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wallet.Wallet.zzb;

final class gp extends zzb {
    private /* synthetic */ int val$requestCode;
    private /* synthetic */ String zzbQB;
    private /* synthetic */ String zzbQC;

    gp(gl glVar, GoogleApiClient googleApiClient, String str, String str2, int i) {
        this.zzbQB = str;
        this.zzbQC = str2;
        this.val$requestCode = i;
        super(googleApiClient);
    }

    protected final void zza(gu guVar) {
        guVar.zzc(this.zzbQB, this.zzbQC, this.val$requestCode);
        setResult(Status.zzaBm);
    }
}
