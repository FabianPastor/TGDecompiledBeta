package com.google.android.gms.maps.model.internal;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper.zza;
import com.google.android.gms.internal.zzee;
import com.google.android.gms.internal.zzef;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PatternItem;
import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import java.util.List;

public abstract class zzt extends zzee implements zzs {
    public static zzs zzag(IBinder iBinder) {
        if (iBinder == null) {
            return null;
        }
        IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.maps.model.internal.IPolygonDelegate");
        return queryLocalInterface instanceof zzs ? (zzs) queryLocalInterface : new zzu(iBinder);
    }

    public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
        if (zza(i, parcel, parcel2, i2)) {
            return true;
        }
        List points;
        float strokeWidth;
        int strokeColor;
        boolean isVisible;
        IInterface queryLocalInterface;
        switch (i) {
            case 1:
                remove();
                parcel2.writeNoException();
                break;
            case 2:
                String id = getId();
                parcel2.writeNoException();
                parcel2.writeString(id);
                break;
            case 3:
                setPoints(parcel.createTypedArrayList(LatLng.CREATOR));
                parcel2.writeNoException();
                break;
            case 4:
                points = getPoints();
                parcel2.writeNoException();
                parcel2.writeTypedList(points);
                break;
            case 5:
                setHoles(zzef.zzb(parcel));
                parcel2.writeNoException();
                break;
            case 6:
                points = getHoles();
                parcel2.writeNoException();
                parcel2.writeList(points);
                break;
            case 7:
                setStrokeWidth(parcel.readFloat());
                parcel2.writeNoException();
                break;
            case 8:
                strokeWidth = getStrokeWidth();
                parcel2.writeNoException();
                parcel2.writeFloat(strokeWidth);
                break;
            case 9:
                setStrokeColor(parcel.readInt());
                parcel2.writeNoException();
                break;
            case 10:
                strokeColor = getStrokeColor();
                parcel2.writeNoException();
                parcel2.writeInt(strokeColor);
                break;
            case 11:
                setFillColor(parcel.readInt());
                parcel2.writeNoException();
                break;
            case 12:
                strokeColor = getFillColor();
                parcel2.writeNoException();
                parcel2.writeInt(strokeColor);
                break;
            case 13:
                setZIndex(parcel.readFloat());
                parcel2.writeNoException();
                break;
            case 14:
                strokeWidth = getZIndex();
                parcel2.writeNoException();
                parcel2.writeFloat(strokeWidth);
                break;
            case 15:
                setVisible(zzef.zza(parcel));
                parcel2.writeNoException();
                break;
            case 16:
                isVisible = isVisible();
                parcel2.writeNoException();
                zzef.zza(parcel2, isVisible);
                break;
            case 17:
                setGeodesic(zzef.zza(parcel));
                parcel2.writeNoException();
                break;
            case 18:
                isVisible = isGeodesic();
                parcel2.writeNoException();
                zzef.zza(parcel2, isVisible);
                break;
            case 19:
                zzs com_google_android_gms_maps_model_internal_zzs;
                IBinder readStrongBinder = parcel.readStrongBinder();
                if (readStrongBinder == null) {
                    com_google_android_gms_maps_model_internal_zzs = null;
                } else {
                    queryLocalInterface = readStrongBinder.queryLocalInterface("com.google.android.gms.maps.model.internal.IPolygonDelegate");
                    com_google_android_gms_maps_model_internal_zzs = queryLocalInterface instanceof zzs ? (zzs) queryLocalInterface : new zzu(readStrongBinder);
                }
                isVisible = zzb(com_google_android_gms_maps_model_internal_zzs);
                parcel2.writeNoException();
                zzef.zza(parcel2, isVisible);
                break;
            case 20:
                strokeColor = hashCodeRemote();
                parcel2.writeNoException();
                parcel2.writeInt(strokeColor);
                break;
            case 21:
                setClickable(zzef.zza(parcel));
                parcel2.writeNoException();
                break;
            case 22:
                isVisible = isClickable();
                parcel2.writeNoException();
                zzef.zza(parcel2, isVisible);
                break;
            case 23:
                setStrokeJointType(parcel.readInt());
                parcel2.writeNoException();
                break;
            case 24:
                strokeColor = getStrokeJointType();
                parcel2.writeNoException();
                parcel2.writeInt(strokeColor);
                break;
            case 25:
                setStrokePattern(parcel.createTypedArrayList(PatternItem.CREATOR));
                parcel2.writeNoException();
                break;
            case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                points = getStrokePattern();
                parcel2.writeNoException();
                parcel2.writeTypedList(points);
                break;
            case 27:
                setTag(zza.zzM(parcel.readStrongBinder()));
                parcel2.writeNoException();
                break;
            case 28:
                queryLocalInterface = getTag();
                parcel2.writeNoException();
                zzef.zza(parcel2, queryLocalInterface);
                break;
            default:
                return false;
        }
        return true;
    }
}
