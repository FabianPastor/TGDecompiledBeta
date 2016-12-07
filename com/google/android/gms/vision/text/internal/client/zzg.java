package com.google.android.gms.vision.text.internal.client;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.dynamic.zze;
import com.google.android.gms.internal.zzsu;
import com.google.android.gms.vision.internal.client.FrameMetadataParcel;
import com.google.android.gms.vision.internal.client.zza;

public class zzg extends zza<zzb> {
    private final TextRecognizerOptions aLD;

    public zzg(Context context, TextRecognizerOptions textRecognizerOptions) {
        super(context, "TextNativeHandle");
        this.aLD = textRecognizerOptions;
        zzclt();
    }

    public LineBoxParcel[] zza(Bitmap bitmap, FrameMetadataParcel frameMetadataParcel, RecognitionOptions recognitionOptions) {
        if (!isOperational()) {
            return new LineBoxParcel[0];
        }
        try {
            return ((zzb) zzclt()).zza(zze.zzac(bitmap), frameMetadataParcel, recognitionOptions);
        } catch (Throwable e) {
            Log.e("TextNativeHandle", "Error calling native text recognizer", e);
            return new LineBoxParcel[0];
        }
    }

    protected /* synthetic */ Object zzb(zzsu com_google_android_gms_internal_zzsu, Context context) throws RemoteException, zzsu.zza {
        return zzd(com_google_android_gms_internal_zzsu, context);
    }

    protected void zzclq() throws RemoteException {
        ((zzb) zzclt()).zzclx();
    }

    protected zzb zzd(zzsu com_google_android_gms_internal_zzsu, Context context) throws RemoteException, zzsu.zza {
        return zzc.zza.zzlo(com_google_android_gms_internal_zzsu.zzjd("com.google.android.gms.vision.text.ChimeraNativeTextRecognizerCreator")).zza(zze.zzac(context), this.aLD);
    }
}
