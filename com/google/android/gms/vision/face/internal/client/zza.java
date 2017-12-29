package com.google.android.gms.vision.face.internal.client;

import android.content.Context;
import android.graphics.PointF;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.dynamic.zzn;
import com.google.android.gms.dynamite.DynamiteModule;
import com.google.android.gms.dynamite.DynamiteModule.zzc;
import com.google.android.gms.internal.zzdjv;
import com.google.android.gms.internal.zzdjw;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.Landmark;
import java.nio.ByteBuffer;

public final class zza extends zzdjv<zze> {
    private final zzc zzkxg;

    public zza(Context context, zzc com_google_android_gms_vision_face_internal_client_zzc) {
        super(context, "FaceNativeHandle");
        this.zzkxg = com_google_android_gms_vision_face_internal_client_zzc;
        zzbjv();
    }

    private static Landmark[] zza(FaceParcel faceParcel) {
        int i = 0;
        LandmarkParcel[] landmarkParcelArr = faceParcel.zzkxj;
        if (landmarkParcelArr == null) {
            return new Landmark[0];
        }
        Landmark[] landmarkArr = new Landmark[landmarkParcelArr.length];
        while (i < landmarkParcelArr.length) {
            LandmarkParcel landmarkParcel = landmarkParcelArr[i];
            landmarkArr[i] = new Landmark(new PointF(landmarkParcel.x, landmarkParcel.y), landmarkParcel.type);
            i++;
        }
        return landmarkArr;
    }

    protected final /* synthetic */ Object zza(DynamiteModule dynamiteModule, Context context) throws RemoteException, zzc {
        zzg com_google_android_gms_vision_face_internal_client_zzg;
        IBinder zzhb = dynamiteModule.zzhb("com.google.android.gms.vision.face.ChimeraNativeFaceDetectorCreator");
        if (zzhb == null) {
            com_google_android_gms_vision_face_internal_client_zzg = null;
        } else {
            IInterface queryLocalInterface = zzhb.queryLocalInterface("com.google.android.gms.vision.face.internal.client.INativeFaceDetectorCreator");
            com_google_android_gms_vision_face_internal_client_zzg = queryLocalInterface instanceof zzg ? (zzg) queryLocalInterface : new zzh(zzhb);
        }
        return com_google_android_gms_vision_face_internal_client_zzg.zza(zzn.zzz(context), this.zzkxg);
    }

    public final Face[] zzb(ByteBuffer byteBuffer, zzdjw com_google_android_gms_internal_zzdjw) {
        if (!isOperational()) {
            return new Face[0];
        }
        try {
            FaceParcel[] zzc = ((zze) zzbjv()).zzc(zzn.zzz(byteBuffer), com_google_android_gms_internal_zzdjw);
            Face[] faceArr = new Face[zzc.length];
            for (int i = 0; i < zzc.length; i++) {
                FaceParcel faceParcel = zzc[i];
                faceArr[i] = new Face(faceParcel.id, new PointF(faceParcel.centerX, faceParcel.centerY), faceParcel.width, faceParcel.height, faceParcel.zzkxh, faceParcel.zzkxi, zza(faceParcel), faceParcel.zzkxk, faceParcel.zzkxl, faceParcel.zzkxm);
            }
            return faceArr;
        } catch (Throwable e) {
            Log.e("FaceNativeHandle", "Could not call native face detector", e);
            return new Face[0];
        }
    }

    protected final void zzbjs() throws RemoteException {
        ((zze) zzbjv()).zzbjt();
    }
}
