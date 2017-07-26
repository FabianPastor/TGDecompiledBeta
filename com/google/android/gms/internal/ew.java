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

public final class ew extends fb<ex> {
    private final eu zzbNh;

    public ew(Context context, eu euVar) {
        super(context, "BarcodeNativeHandle");
        this.zzbNh = euVar;
        zzDR();
    }

    protected final void zzDO() throws RemoteException {
        if (isOperational()) {
            ((ex) zzDR()).zzDP();
        }
    }

    protected final /* synthetic */ Object zza(DynamiteModule dynamiteModule, Context context) throws RemoteException, zzc {
        ez ezVar;
        IBinder zzcV = dynamiteModule.zzcV("com.google.android.gms.vision.barcode.ChimeraNativeBarcodeDetectorCreator");
        if (zzcV == null) {
            ezVar = null;
        } else {
            IInterface queryLocalInterface = zzcV.queryLocalInterface("com.google.android.gms.vision.barcode.internal.client.INativeBarcodeDetectorCreator");
            ezVar = queryLocalInterface instanceof ez ? (ez) queryLocalInterface : new fa(zzcV);
        }
        return ezVar == null ? null : ezVar.zza(zzn.zzw(context), this.zzbNh);
    }

    public final Barcode[] zza(Bitmap bitmap, fc fcVar) {
        if (!isOperational()) {
            return new Barcode[0];
        }
        try {
            return ((ex) zzDR()).zzb(zzn.zzw(bitmap), fcVar);
        } catch (Throwable e) {
            Log.e("BarcodeNativeHandle", "Error calling native barcode detector", e);
            return new Barcode[0];
        }
    }

    public final Barcode[] zza(ByteBuffer byteBuffer, fc fcVar) {
        if (!isOperational()) {
            return new Barcode[0];
        }
        try {
            return ((ex) zzDR()).zza(zzn.zzw(byteBuffer), fcVar);
        } catch (Throwable e) {
            Log.e("BarcodeNativeHandle", "Error calling native barcode detector", e);
            return new Barcode[0];
        }
    }
}
