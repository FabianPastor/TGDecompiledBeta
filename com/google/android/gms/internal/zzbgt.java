package com.google.android.gms.internal;

import android.content.Context;
import android.graphics.PointF;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.dynamic.zze;
import com.google.android.gms.dynamite.DynamiteModule;
import com.google.android.gms.dynamite.DynamiteModule.zza;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.Landmark;
import java.nio.ByteBuffer;

public class zzbgt extends zzbhc<zzbgy> {
    private final zzbgw zzbNc;

    public zzbgt(Context context, zzbgw com_google_android_gms_internal_zzbgw) {
        super(context, "FaceNativeHandle");
        this.zzbNc = com_google_android_gms_internal_zzbgw;
        zzSq();
    }

    private Face zza(zzbgu com_google_android_gms_internal_zzbgu) {
        return new Face(com_google_android_gms_internal_zzbgu.id, new PointF(com_google_android_gms_internal_zzbgu.centerX, com_google_android_gms_internal_zzbgu.centerY), com_google_android_gms_internal_zzbgu.width, com_google_android_gms_internal_zzbgu.height, com_google_android_gms_internal_zzbgu.zzbNd, com_google_android_gms_internal_zzbgu.zzbNe, zzb(com_google_android_gms_internal_zzbgu), com_google_android_gms_internal_zzbgu.zzbNg, com_google_android_gms_internal_zzbgu.zzbNh, com_google_android_gms_internal_zzbgu.zzbNi);
    }

    private Landmark zza(zzbha com_google_android_gms_internal_zzbha) {
        return new Landmark(new PointF(com_google_android_gms_internal_zzbha.x, com_google_android_gms_internal_zzbha.y), com_google_android_gms_internal_zzbha.type);
    }

    private Landmark[] zzb(zzbgu com_google_android_gms_internal_zzbgu) {
        int i = 0;
        zzbha[] com_google_android_gms_internal_zzbhaArr = com_google_android_gms_internal_zzbgu.zzbNf;
        if (com_google_android_gms_internal_zzbhaArr == null) {
            return new Landmark[0];
        }
        Landmark[] landmarkArr = new Landmark[com_google_android_gms_internal_zzbhaArr.length];
        while (i < com_google_android_gms_internal_zzbhaArr.length) {
            landmarkArr[i] = zza(com_google_android_gms_internal_zzbhaArr[i]);
            i++;
        }
        return landmarkArr;
    }

    protected void zzSn() throws RemoteException {
        ((zzbgy) zzSq()).zzSo();
    }

    protected /* synthetic */ Object zzb(DynamiteModule dynamiteModule, Context context) throws RemoteException, zza {
        return zzc(dynamiteModule, context);
    }

    public Face[] zzb(ByteBuffer byteBuffer, zzbhd com_google_android_gms_internal_zzbhd) {
        if (!isOperational()) {
            return new Face[0];
        }
        try {
            zzbgu[] zzc = ((zzbgy) zzSq()).zzc(zze.zzA(byteBuffer), com_google_android_gms_internal_zzbhd);
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

    protected zzbgy zzc(DynamiteModule dynamiteModule, Context context) throws RemoteException, zza {
        return zzbgz.zza.zzfj(dynamiteModule.zzdX("com.google.android.gms.vision.face.ChimeraNativeFaceDetectorCreator")).zza(zze.zzA(context), this.zzbNc);
    }

    public boolean zznw(int i) {
        if (!isOperational()) {
            return false;
        }
        try {
            return ((zzbgy) zzSq()).zznw(i);
        } catch (Throwable e) {
            Log.e("FaceNativeHandle", "Could not call native face detector", e);
            return false;
        }
    }
}
