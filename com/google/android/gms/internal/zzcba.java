package com.google.android.gms.internal;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;

public final class zzcba extends zzeu implements zzcay {
    zzcba(IBinder iBinder) {
        super(iBinder, "com.google.android.gms.flags.IFlagProvider");
    }

    public final boolean getBooleanFlagValue(String str, boolean z, int i) throws RemoteException {
        Parcel zzbe = zzbe();
        zzbe.writeString(str);
        zzew.zza(zzbe, z);
        zzbe.writeInt(i);
        zzbe = zza(2, zzbe);
        boolean zza = zzew.zza(zzbe);
        zzbe.recycle();
        return zza;
    }

    public final int getIntFlagValue(String str, int i, int i2) throws RemoteException {
        Parcel zzbe = zzbe();
        zzbe.writeString(str);
        zzbe.writeInt(i);
        zzbe.writeInt(i2);
        zzbe = zza(3, zzbe);
        int readInt = zzbe.readInt();
        zzbe.recycle();
        return readInt;
    }

    public final long getLongFlagValue(String str, long j, int i) throws RemoteException {
        Parcel zzbe = zzbe();
        zzbe.writeString(str);
        zzbe.writeLong(j);
        zzbe.writeInt(i);
        zzbe = zza(4, zzbe);
        long readLong = zzbe.readLong();
        zzbe.recycle();
        return readLong;
    }

    public final String getStringFlagValue(String str, String str2, int i) throws RemoteException {
        Parcel zzbe = zzbe();
        zzbe.writeString(str);
        zzbe.writeString(str2);
        zzbe.writeInt(i);
        zzbe = zza(5, zzbe);
        String readString = zzbe.readString();
        zzbe.recycle();
        return readString;
    }

    public final void init(IObjectWrapper iObjectWrapper) throws RemoteException {
        Parcel zzbe = zzbe();
        zzew.zza(zzbe, (IInterface) iObjectWrapper);
        zzb(1, zzbe);
    }
}
