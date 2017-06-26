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
import com.google.android.gms.vision.barcode.Barcode;
import java.nio.ByteBuffer;

public final class ev extends fa<ew> {
    private final et zzbNf;

    public ev(Context context, et etVar) {
        super(context, "BarcodeNativeHandle");
        this.zzbNf = etVar;
        zzDQ();
    }

    protected final void zzDN() throws RemoteException {
        if (isOperational()) {
            ((ew) zzDQ()).zzDO();
        }
    }

    protected final /* synthetic */ Object zza(DynamiteModule dynamiteModule, Context context) throws RemoteException, zzc {
        ey eyVar;
        IBinder zzcV = dynamiteModule.zzcV("com.google.android.gms.vision.barcode.ChimeraNativeBarcodeDetectorCreator");
        if (zzcV == null) {
            eyVar = null;
        } else {
            IInterface queryLocalInterface = zzcV.queryLocalInterface("com.google.android.gms.vision.barcode.internal.client.INativeBarcodeDetectorCreator");
            eyVar = queryLocalInterface instanceof ey ? (ey) queryLocalInterface : new ez(zzcV);
        }
        return eyVar == null ? null : eyVar.zza(zzn.zzw(context), this.zzbNf);
    }

    public final Barcode[] zza(Bitmap bitmap, fb fbVar) {
        if (!isOperational()) {
            return new Barcode[0];
        }
        try {
            return ((ew) zzDQ()).zzb(zzn.zzw(bitmap), fbVar);
        } catch (Throwable e) {
            Log.e("BarcodeNativeHandle", "Error calling native barcode detector", e);
            return new Barcode[0];
        }
    }

    public final Barcode[] zza(ByteBuffer byteBuffer, fb fbVar) {
        if (!isOperational()) {
            return new Barcode[0];
        }
        try {
            return ((ew) zzDQ()).zza(zzn.zzw(byteBuffer), fbVar);
        } catch (Throwable e) {
            Log.e("BarcodeNativeHandle", "Error calling native barcode detector", e);
            return new Barcode[0];
        }
    }
}
