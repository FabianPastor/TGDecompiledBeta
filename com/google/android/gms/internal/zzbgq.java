package com.google.android.gms.internal;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.dynamic.zze;
import com.google.android.gms.dynamite.DynamiteModule;
import com.google.android.gms.dynamite.DynamiteModule.zza;
import com.google.android.gms.vision.barcode.Barcode;
import java.nio.ByteBuffer;

public class zzbgq extends zzbhc<zzbgr> {
    private final zzbgo zzbMK;

    public zzbgq(Context context, zzbgo com_google_android_gms_internal_zzbgo) {
        super(context, "BarcodeNativeHandle");
        this.zzbMK = com_google_android_gms_internal_zzbgo;
        zzSq();
    }

    protected void zzSn() throws RemoteException {
        ((zzbgr) zzSq()).zzSo();
    }

    protected zzbgr zza(DynamiteModule dynamiteModule, Context context) throws RemoteException, zza {
        return zzbgs.zza.zzfh(dynamiteModule.zzdX("com.google.android.gms.vision.barcode.ChimeraNativeBarcodeDetectorCreator")).zza(zze.zzA(context), this.zzbMK);
    }

    public Barcode[] zza(Bitmap bitmap, zzbhd com_google_android_gms_internal_zzbhd) {
        if (!isOperational()) {
            return new Barcode[0];
        }
        try {
            return ((zzbgr) zzSq()).zzb(zze.zzA(bitmap), com_google_android_gms_internal_zzbhd);
        } catch (Throwable e) {
            Log.e("BarcodeNativeHandle", "Error calling native barcode detector", e);
            return new Barcode[0];
        }
    }

    public Barcode[] zza(ByteBuffer byteBuffer, zzbhd com_google_android_gms_internal_zzbhd) {
        if (!isOperational()) {
            return new Barcode[0];
        }
        try {
            return ((zzbgr) zzSq()).zza(zze.zzA(byteBuffer), com_google_android_gms_internal_zzbhd);
        } catch (Throwable e) {
            Log.e("BarcodeNativeHandle", "Error calling native barcode detector", e);
            return new Barcode[0];
        }
    }

    protected /* synthetic */ Object zzb(DynamiteModule dynamiteModule, Context context) throws RemoteException, zza {
        return zza(dynamiteModule, context);
    }
}
