package com.google.android.gms.internal;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.dynamic.zzd;
import com.google.android.gms.dynamite.DynamiteModule;
import com.google.android.gms.dynamite.DynamiteModule.zza;

public class zzbkn extends zzbjz<zzbkf> {
    private final zzbko zzbPx;

    public zzbkn(Context context, zzbko com_google_android_gms_internal_zzbko) {
        super(context, "TextNativeHandle");
        this.zzbPx = com_google_android_gms_internal_zzbko;
        zzTU();
    }

    protected void zzTR() throws RemoteException {
        ((zzbkf) zzTU()).zzTY();
    }

    public zzbkh[] zza(Bitmap bitmap, zzbka com_google_android_gms_internal_zzbka, zzbkj com_google_android_gms_internal_zzbkj) {
        if (!isOperational()) {
            return new zzbkh[0];
        }
        try {
            return ((zzbkf) zzTU()).zza(zzd.zzA(bitmap), com_google_android_gms_internal_zzbka, com_google_android_gms_internal_zzbkj);
        } catch (Throwable e) {
            Log.e("TextNativeHandle", "Error calling native text recognizer", e);
            return new zzbkh[0];
        }
    }

    protected /* synthetic */ Object zzb(DynamiteModule dynamiteModule, Context context) throws RemoteException, zza {
        return zzd(dynamiteModule, context);
    }

    protected zzbkf zzd(DynamiteModule dynamiteModule, Context context) throws RemoteException, zza {
        return zzbkg.zza.zzfs(dynamiteModule.zzdT("com.google.android.gms.vision.text.ChimeraNativeTextRecognizerCreator")).zza(zzd.zzA(context), this.zzbPx);
    }
}
