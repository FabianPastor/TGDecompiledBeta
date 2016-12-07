package com.google.android.gms.vision.face.internal.client;

import android.content.Context;
import android.graphics.PointF;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.dynamic.zze;
import com.google.android.gms.internal.zzsu;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.Landmark;
import com.google.android.gms.vision.internal.client.FrameMetadataParcel;
import java.nio.ByteBuffer;

public class zza extends com.google.android.gms.vision.internal.client.zza<zzd> {
    private final FaceSettingsParcel aLf;

    public zza(Context context, FaceSettingsParcel faceSettingsParcel) {
        super(context, "FaceNativeHandle");
        this.aLf = faceSettingsParcel;
        zzclt();
    }

    private Face zza(FaceParcel faceParcel) {
        return new Face(faceParcel.id, new PointF(faceParcel.centerX, faceParcel.centerY), faceParcel.width, faceParcel.height, faceParcel.aLg, faceParcel.aLh, zzb(faceParcel), faceParcel.aLj, faceParcel.aLk, faceParcel.aLl);
    }

    private Landmark zza(LandmarkParcel landmarkParcel) {
        return new Landmark(new PointF(landmarkParcel.x, landmarkParcel.y), landmarkParcel.type);
    }

    private Landmark[] zzb(FaceParcel faceParcel) {
        int i = 0;
        LandmarkParcel[] landmarkParcelArr = faceParcel.aLi;
        if (landmarkParcelArr == null) {
            return new Landmark[0];
        }
        Landmark[] landmarkArr = new Landmark[landmarkParcelArr.length];
        while (i < landmarkParcelArr.length) {
            landmarkArr[i] = zza(landmarkParcelArr[i]);
            i++;
        }
        return landmarkArr;
    }

    public boolean zzabr(int i) {
        if (!isOperational()) {
            return false;
        }
        try {
            return ((zzd) zzclt()).zzabr(i);
        } catch (Throwable e) {
            Log.e("FaceNativeHandle", "Could not call native face detector", e);
            return false;
        }
    }

    protected /* synthetic */ Object zzb(zzsu com_google_android_gms_internal_zzsu, Context context) throws RemoteException, com.google.android.gms.internal.zzsu.zza {
        return zzc(com_google_android_gms_internal_zzsu, context);
    }

    public Face[] zzb(ByteBuffer byteBuffer, FrameMetadataParcel frameMetadataParcel) {
        if (!isOperational()) {
            return new Face[0];
        }
        try {
            FaceParcel[] zzc = ((zzd) zzclt()).zzc(zze.zzac(byteBuffer), frameMetadataParcel);
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

    protected zzd zzc(zzsu com_google_android_gms_internal_zzsu, Context context) throws RemoteException, com.google.android.gms.internal.zzsu.zza {
        return com.google.android.gms.vision.face.internal.client.zze.zza.zzlm(com_google_android_gms_internal_zzsu.zzjd("com.google.android.gms.vision.face.ChimeraNativeFaceDetectorCreator")).zza(zze.zzac(context), this.aLf);
    }

    protected void zzclq() throws RemoteException {
        ((zzd) zzclt()).zzclr();
    }
}
