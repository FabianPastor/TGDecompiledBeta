package com.google.android.gms.internal;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wallet.Wallet.zzb;

final class go extends zzb {
    private /* synthetic */ int val$requestCode;
    private /* synthetic */ String zzbQA;
    private /* synthetic */ String zzbQz;

    go(gk gkVar, GoogleApiClient googleApiClient, String str, String str2, int i) {
        this.zzbQz = str;
        this.zzbQA = str2;
        this.val$requestCode = i;
        super(googleApiClient);
    }

    protected final void zza(gt gtVar) {
        gtVar.zzc(this.zzbQz, this.zzbQA, this.val$requestCode);
        setResult(Status.zzaBm);
    }
}
