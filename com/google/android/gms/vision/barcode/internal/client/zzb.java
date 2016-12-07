package com.google.android.gms.vision.barcode.internal.client;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.dynamic.zze;
import com.google.android.gms.internal.zzsu;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.internal.client.FrameMetadataParcel;
import com.google.android.gms.vision.internal.client.zza;
import java.nio.ByteBuffer;

public class zzb extends zza<zzc> {
    private final BarcodeDetectorOptions aKN;

    public zzb(Context context, BarcodeDetectorOptions barcodeDetectorOptions) {
        super(context, "BarcodeNativeHandle");
        this.aKN = barcodeDetectorOptions;
        zzclt();
    }

    protected zzc zza(zzsu com_google_android_gms_internal_zzsu, Context context) throws RemoteException, zzsu.zza {
        return zzd.zza.zzlk(com_google_android_gms_internal_zzsu.zzjd("com.google.android.gms.vision.barcode.ChimeraNativeBarcodeDetectorCreator")).zza(zze.zzac(context), this.aKN);
    }

    public Barcode[] zza(Bitmap bitmap, FrameMetadataParcel frameMetadataParcel) {
        if (!isOperational()) {
            return new Barcode[0];
        }
        try {
            return ((zzc) zzclt()).zzb(zze.zzac(bitmap), frameMetadataParcel);
        } catch (Throwable e) {
            Log.e("BarcodeNativeHandle", "Error calling native barcode detector", e);
            return new Barcode[0];
        }
    }

    public Barcode[] zza(ByteBuffer byteBuffer, FrameMetadataParcel frameMetadataParcel) {
        if (!isOperational()) {
            return new Barcode[0];
        }
        try {
            return ((zzc) zzclt()).zza(zze.zzac(byteBuffer), frameMetadataParcel);
        } catch (Throwable e) {
            Log.e("BarcodeNativeHandle", "Error calling native barcode detector", e);
            return new Barcode[0];
        }
    }

    protected /* synthetic */ Object zzb(zzsu com_google_android_gms_internal_zzsu, Context context) throws RemoteException, zzsu.zza {
        return zza(com_google_android_gms_internal_zzsu, context);
    }

    protected void zzclq() throws RemoteException {
        ((zzc) zzclt()).zzclr();
    }
}
