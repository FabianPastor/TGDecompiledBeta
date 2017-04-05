package com.google.android.gms.internal;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.dynamic.zzd;
import com.google.android.gms.dynamite.DynamiteModule;
import com.google.android.gms.dynamite.DynamiteModule.zza;
import com.google.android.gms.vision.barcode.Barcode;
import java.nio.ByteBuffer;

public class zzbjn extends zzbjz<zzbjo> {
    private final zzbjl zzbOF;

    public zzbjn(Context context, zzbjl com_google_android_gms_internal_zzbjl) {
        super(context, "BarcodeNativeHandle");
        this.zzbOF = com_google_android_gms_internal_zzbjl;
        zzTS();
    }

    protected void zzTP() throws RemoteException {
        ((zzbjo) zzTS()).zzTQ();
    }

    protected zzbjo zza(DynamiteModule dynamiteModule, Context context) throws RemoteException, zza {
        return zzbjp.zza.zzfo(dynamiteModule.zzdT("com.google.android.gms.vision.barcode.ChimeraNativeBarcodeDetectorCreator")).zza(zzd.zzA(context), this.zzbOF);
    }

    public Barcode[] zza(Bitmap bitmap, zzbka com_google_android_gms_internal_zzbka) {
        if (!isOperational()) {
            return new Barcode[0];
        }
        try {
            return ((zzbjo) zzTS()).zzb(zzd.zzA(bitmap), com_google_android_gms_internal_zzbka);
        } catch (Throwable e) {
            Log.e("BarcodeNativeHandle", "Error calling native barcode detector", e);
            return new Barcode[0];
        }
    }

    public Barcode[] zza(ByteBuffer byteBuffer, zzbka com_google_android_gms_internal_zzbka) {
        if (!isOperational()) {
            return new Barcode[0];
        }
        try {
            return ((zzbjo) zzTS()).zza(zzd.zzA(byteBuffer), com_google_android_gms_internal_zzbka);
        } catch (Throwable e) {
            Log.e("BarcodeNativeHandle", "Error calling native barcode detector", e);
            return new Barcode[0];
        }
    }

    protected /* synthetic */ Object zzb(DynamiteModule dynamiteModule, Context context) throws RemoteException, zza {
        return zza(dynamiteModule, context);
    }
}
