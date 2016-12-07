package com.google.android.gms.internal;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.dynamic.zze;
import com.google.android.gms.dynamite.DynamiteModule;
import com.google.android.gms.dynamite.DynamiteModule.zza;

public class zzbhq extends zzbhc<zzbhi> {
    private final zzbhr zzbNz;

    public zzbhq(Context context, zzbhr com_google_android_gms_internal_zzbhr) {
        super(context, "TextNativeHandle");
        this.zzbNz = com_google_android_gms_internal_zzbhr;
        zzSq();
    }

    protected void zzSn() throws RemoteException {
        ((zzbhi) zzSq()).zzSu();
    }

    public zzbhk[] zza(Bitmap bitmap, zzbhd com_google_android_gms_internal_zzbhd, zzbhm com_google_android_gms_internal_zzbhm) {
        if (!isOperational()) {
            return new zzbhk[0];
        }
        try {
            return ((zzbhi) zzSq()).zza(zze.zzA(bitmap), com_google_android_gms_internal_zzbhd, com_google_android_gms_internal_zzbhm);
        } catch (Throwable e) {
            Log.e("TextNativeHandle", "Error calling native text recognizer", e);
            return new zzbhk[0];
        }
    }

    protected /* synthetic */ Object zzb(DynamiteModule dynamiteModule, Context context) throws RemoteException, zza {
        return zzd(dynamiteModule, context);
    }

    protected zzbhi zzd(DynamiteModule dynamiteModule, Context context) throws RemoteException, zza {
        return zzbhj.zza.zzfl(dynamiteModule.zzdX("com.google.android.gms.vision.text.ChimeraNativeTextRecognizerCreator")).zza(zze.zzA(context), this.zzbNz);
    }
}
