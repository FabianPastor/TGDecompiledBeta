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

public final class fq extends fb<fg> {
    private final fr zzbNV;

    public fq(Context context, fr frVar) {
        super(context, "TextNativeHandle");
        this.zzbNV = frVar;
        zzDR();
    }

    protected final void zzDO() throws RemoteException {
        ((fg) zzDR()).zzDS();
    }

    protected final /* synthetic */ Object zza(DynamiteModule dynamiteModule, Context context) throws RemoteException, zzc {
        fi fiVar;
        IBinder zzcV = dynamiteModule.zzcV("com.google.android.gms.vision.text.ChimeraNativeTextRecognizerCreator");
        if (zzcV == null) {
            fiVar = null;
        } else {
            IInterface queryLocalInterface = zzcV.queryLocalInterface("com.google.android.gms.vision.text.internal.client.INativeTextRecognizerCreator");
            fiVar = queryLocalInterface instanceof fi ? (fi) queryLocalInterface : new fj(zzcV);
        }
        return fiVar.zza(zzn.zzw(context), this.zzbNV);
    }

    public final fk[] zza(Bitmap bitmap, fc fcVar, fm fmVar) {
        if (!isOperational()) {
            return new fk[0];
        }
        try {
            return ((fg) zzDR()).zza(zzn.zzw(bitmap), fcVar, fmVar);
        } catch (Throwable e) {
            Log.e("TextNativeHandle", "Error calling native text recognizer", e);
            return new fk[0];
        }
    }
}
