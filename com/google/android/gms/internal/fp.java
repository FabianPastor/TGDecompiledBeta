package com.google.android.gms.internal;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.dynamic.zzn;
import com.google.android.gms.dynamite.DynamiteModule;
import com.google.android.gms.dynamite.DynamiteModule.zzc;

public final class fp extends fa<ff> {
    private final fq zzbNT;

    public fp(Context context, fq fqVar) {
        super(context, "TextNativeHandle");
        this.zzbNT = fqVar;
        zzDQ();
    }

    protected final void zzDN() throws RemoteException {
        ((ff) zzDQ()).zzDR();
    }

    protected final /* synthetic */ Object zza(DynamiteModule dynamiteModule, Context context) throws RemoteException, zzc {
        fh fhVar;
        IBinder zzcV = dynamiteModule.zzcV("com.google.android.gms.vision.text.ChimeraNativeTextRecognizerCreator");
        if (zzcV == null) {
            fhVar = null;
        } else {
            IInterface queryLocalInterface = zzcV.queryLocalInterface("com.google.android.gms.vision.text.internal.client.INativeTextRecognizerCreator");
            fhVar = queryLocalInterface instanceof fh ? (fh) queryLocalInterface : new fi(zzcV);
        }
        return fhVar.zza(zzn.zzw(context), this.zzbNT);
    }

    public final fj[] zza(Bitmap bitmap, fb fbVar, fl flVar) {
        if (!isOperational()) {
            return new fj[0];
        }
        try {
            return ((ff) zzDQ()).zza(zzn.zzw(bitmap), fbVar, flVar);
        } catch (Throwable e) {
            Log.e("TextNativeHandle", "Error calling native text recognizer", e);
            return new fj[0];
        }
    }
}
