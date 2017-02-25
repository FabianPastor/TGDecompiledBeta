package com.google.android.gms.internal;

import android.content.Context;
import android.graphics.PointF;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.dynamic.zzd;
import com.google.android.gms.dynamite.DynamiteModule;
import com.google.android.gms.dynamite.DynamiteModule.zza;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.Landmark;
import java.nio.ByteBuffer;

public class zzbjq extends zzbjz<zzbjv> {
    private final zzbjt zzbPb;

    public zzbjq(Context context, zzbjt com_google_android_gms_internal_zzbjt) {
        super(context, "FaceNativeHandle");
        this.zzbPb = com_google_android_gms_internal_zzbjt;
        zzTR();
    }

    private Face zza(zzbjr com_google_android_gms_internal_zzbjr) {
        return new Face(com_google_android_gms_internal_zzbjr.id, new PointF(com_google_android_gms_internal_zzbjr.centerX, com_google_android_gms_internal_zzbjr.centerY), com_google_android_gms_internal_zzbjr.width, com_google_android_gms_internal_zzbjr.height, com_google_android_gms_internal_zzbjr.zzbPc, com_google_android_gms_internal_zzbjr.zzbPd, zzb(com_google_android_gms_internal_zzbjr), com_google_android_gms_internal_zzbjr.zzbPf, com_google_android_gms_internal_zzbjr.zzbPg, com_google_android_gms_internal_zzbjr.zzbPh);
    }

    private Landmark zza(zzbjx com_google_android_gms_internal_zzbjx) {
        return new Landmark(new PointF(com_google_android_gms_internal_zzbjx.x, com_google_android_gms_internal_zzbjx.y), com_google_android_gms_internal_zzbjx.type);
    }

    private Landmark[] zzb(zzbjr com_google_android_gms_internal_zzbjr) {
        int i = 0;
        zzbjx[] com_google_android_gms_internal_zzbjxArr = com_google_android_gms_internal_zzbjr.zzbPe;
        if (com_google_android_gms_internal_zzbjxArr == null) {
            return new Landmark[0];
        }
        Landmark[] landmarkArr = new Landmark[com_google_android_gms_internal_zzbjxArr.length];
        while (i < com_google_android_gms_internal_zzbjxArr.length) {
            landmarkArr[i] = zza(com_google_android_gms_internal_zzbjxArr[i]);
            i++;
        }
        return landmarkArr;
    }

    protected void zzTO() throws RemoteException {
        ((zzbjv) zzTR()).zzTP();
    }

    protected /* synthetic */ Object zzb(DynamiteModule dynamiteModule, Context context) throws RemoteException, zza {
        return zzc(dynamiteModule, context);
    }

    public Face[] zzb(ByteBuffer byteBuffer, zzbka com_google_android_gms_internal_zzbka) {
        if (!isOperational()) {
            return new Face[0];
        }
        try {
            zzbjr[] zzc = ((zzbjv) zzTR()).zzc(zzd.zzA(byteBuffer), com_google_android_gms_internal_zzbka);
            Face[] faceArr = new Face[zzc.length];
            for (int i = 0; i < zzc.length; i++) {
                faceArr[i] = zza(zzc[i]);
            }
            return faceArr;
        } catch (Throwable e) {
            Log.e("FaceNativeHandle", "Could not call native face detector", e);
            return new Face[0];
        }
    }

    protected zzbjv zzc(DynamiteModule dynamiteModule, Context context) throws RemoteException, zza {
        return zzbjw.zza.zzfq(dynamiteModule.zzdT("com.google.android.gms.vision.face.ChimeraNativeFaceDetectorCreator")).zza(zzd.zzA(context), this.zzbPb);
    }

    public boolean zzoh(int i) {
        if (!isOperational()) {
            return false;
        }
        try {
            return ((zzbjv) zzTR()).zzoh(i);
        } catch (Throwable e) {
            Log.e("FaceNativeHandle", "Could not call native face detector", e);
            return false;
        }
    }
}
