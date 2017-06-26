package com.google.android.gms.maps.model.internal;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.dynamic.IObjectWrapper.zza;
import com.google.android.gms.internal.zzed;
import com.google.android.gms.internal.zzef;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PatternItem;
import java.util.List;

public final class zzu extends zzed implements zzs {
    zzu(IBinder iBinder) {
        super(iBinder, "com.google.android.gms.maps.model.internal.IPolygonDelegate");
    }

    public final int getFillColor() throws RemoteException {
        Parcel zza = zza(12, zzZ());
        int readInt = zza.readInt();
        zza.recycle();
        return readInt;
    }

    public final List getHoles() throws RemoteException {
        Parcel zza = zza(6, zzZ());
        List zzb = zzef.zzb(zza);
        zza.recycle();
        return zzb;
    }

    public final String getId() throws RemoteException {
        Parcel zza = zza(2, zzZ());
        String readString = zza.readString();
        zza.recycle();
        return readString;
    }

    public final List<LatLng> getPoints() throws RemoteException {
        Parcel zza = zza(4, zzZ());
        List createTypedArrayList = zza.createTypedArrayList(LatLng.CREATOR);
        zza.recycle();
        return createTypedArrayList;
    }

    public final int getStrokeColor() throws RemoteException {
        Parcel zza = zza(10, zzZ());
        int readInt = zza.readInt();
        zza.recycle();
        return readInt;
    }

    public final int getStrokeJointType() throws RemoteException {
        Parcel zza = zza(24, zzZ());
        int readInt = zza.readInt();
        zza.recycle();
        return readInt;
    }

    public final List<PatternItem> getStrokePattern() throws RemoteException {
        Parcel zza = zza(26, zzZ());
        List createTypedArrayList = zza.createTypedArrayList(PatternItem.CREATOR);
        zza.recycle();
        return createTypedArrayList;
    }

    public final float getStrokeWidth() throws RemoteException {
        Parcel zza = zza(8, zzZ());
        float readFloat = zza.readFloat();
        zza.recycle();
        return readFloat;
    }

    public final IObjectWrapper getTag() throws RemoteException {
        Parcel zza = zza(28, zzZ());
        IObjectWrapper zzM = zza.zzM(zza.readStrongBinder());
        zza.recycle();
        return zzM;
    }

    public final float getZIndex() throws RemoteException {
        Parcel zza = zza(14, zzZ());
        float readFloat = zza.readFloat();
        zza.recycle();
        return readFloat;
    }

    public final int hashCodeRemote() throws RemoteException {
        Parcel zza = zza(20, zzZ());
        int readInt = zza.readInt();
        zza.recycle();
        return readInt;
    }

    public final boolean isClickable() throws RemoteException {
        Parcel zza = zza(22, zzZ());
        boolean zza2 = zzef.zza(zza);
        zza.recycle();
        return zza2;
    }

    public final boolean isGeodesic() throws RemoteException {
        Parcel zza = zza(18, zzZ());
        boolean zza2 = zzef.zza(zza);
        zza.recycle();
        return zza2;
    }

    public final boolean isVisible() throws RemoteException {
        Parcel zza = zza(16, zzZ());
        boolean zza2 = zzef.zza(zza);
        zza.recycle();
        return zza2;
    }

    public final void remove() throws RemoteException {
        zzb(1, zzZ());
    }

    public final void setClickable(boolean z) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, z);
        zzb(21, zzZ);
    }

    public final void setFillColor(int i) throws RemoteException {
        Parcel zzZ = zzZ();
        zzZ.writeInt(i);
        zzb(11, zzZ);
    }

    public final void setGeodesic(boolean z) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, z);
        zzb(17, zzZ);
    }

    public final void setHoles(List list) throws RemoteException {
        Parcel zzZ = zzZ();
        zzZ.writeList(list);
        zzb(5, zzZ);
    }

    public final void setPoints(List<LatLng> list) throws RemoteException {
        Parcel zzZ = zzZ();
        zzZ.writeTypedList(list);
        zzb(3, zzZ);
    }

    public final void setStrokeColor(int i) throws RemoteException {
        Parcel zzZ = zzZ();
        zzZ.writeInt(i);
        zzb(9, zzZ);
    }

    public final void setStrokeJointType(int i) throws RemoteException {
        Parcel zzZ = zzZ();
        zzZ.writeInt(i);
        zzb(23, zzZ);
    }

    public final void setStrokePattern(List<PatternItem> list) throws RemoteException {
        Parcel zzZ = zzZ();
        zzZ.writeTypedList(list);
        zzb(25, zzZ);
    }

    public final void setStrokeWidth(float f) throws RemoteException {
        Parcel zzZ = zzZ();
        zzZ.writeFloat(f);
        zzb(7, zzZ);
    }

    public final void setTag(IObjectWrapper iObjectWrapper) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (IInterface) iObjectWrapper);
        zzb(27, zzZ);
    }

    public final void setVisible(boolean z) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, z);
        zzb(15, zzZ);
    }

    public final void setZIndex(float f) throws RemoteException {
        Parcel zzZ = zzZ();
        zzZ.writeFloat(f);
        zzb(13, zzZ);
    }

    public final boolean zzb(zzs com_google_android_gms_maps_model_internal_zzs) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (IInterface) com_google_android_gms_maps_model_internal_zzs);
        zzZ = zza(19, zzZ);
        boolean zza = zzef.zza(zzZ);
        zzZ.recycle();
        return zza;
    }
}
