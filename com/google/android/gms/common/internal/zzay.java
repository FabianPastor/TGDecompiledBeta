package com.google.android.gms.common.internal;

import android.os.IInterface;
import android.os.RemoteException;
import com.google.android.gms.common.zzm;
import com.google.android.gms.dynamic.IObjectWrapper;

public interface zzay extends IInterface {
    boolean zza(zzm com_google_android_gms_common_zzm, IObjectWrapper iObjectWrapper) throws RemoteException;

    boolean zze(String str, IObjectWrapper iObjectWrapper) throws RemoteException;

    boolean zzf(String str, IObjectWrapper iObjectWrapper) throws RemoteException;

    IObjectWrapper zzrF() throws RemoteException;

    IObjectWrapper zzrG() throws RemoteException;
}
