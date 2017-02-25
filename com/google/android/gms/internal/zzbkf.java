package com.google.android.gms.internal;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;

public interface zzbkf extends IInterface {

    public static abstract class zza extends Binder implements zzbkf {

        private static class zza implements zzbkf {
            private IBinder zzrk;

            zza(IBinder iBinder) {
                this.zzrk = iBinder;
            }

            public IBinder asBinder() {
                return this.zzrk;
            }

            public void zzTV() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.gms.vision.text.internal.client.INativeTextRecognizer");
                    this.zzrk.transact(2, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public zzbkh[] zza(IObjectWrapper iObjectWrapper, zzbka com_google_android_gms_internal_zzbka, zzbkj com_google_android_gms_internal_zzbkj) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.gms.vision.text.internal.client.INativeTextRecognizer");
                    obtain.writeStrongBinder(iObjectWrapper != null ? iObjectWrapper.asBinder() : null);
                    if (com_google_android_gms_internal_zzbka != null) {
                        obtain.writeInt(1);
                        com_google_android_gms_internal_zzbka.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (com_google_android_gms_internal_zzbkj != null) {
                        obtain.writeInt(1);
                        com_google_android_gms_internal_zzbkj.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.zzrk.transact(3, obtain, obtain2, 0);
                    obtain2.readException();
                    zzbkh[] com_google_android_gms_internal_zzbkhArr = (zzbkh[]) obtain2.createTypedArray(zzbkh.CREATOR);
                    return com_google_android_gms_internal_zzbkhArr;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public zzbkh[] zzd(IObjectWrapper iObjectWrapper, zzbka com_google_android_gms_internal_zzbka) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.gms.vision.text.internal.client.INativeTextRecognizer");
                    obtain.writeStrongBinder(iObjectWrapper != null ? iObjectWrapper.asBinder() : null);
                    if (com_google_android_gms_internal_zzbka != null) {
                        obtain.writeInt(1);
                        com_google_android_gms_internal_zzbka.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.zzrk.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                    zzbkh[] com_google_android_gms_internal_zzbkhArr = (zzbkh[]) obtain2.createTypedArray(zzbkh.CREATOR);
                    return com_google_android_gms_internal_zzbkhArr;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public static zzbkf zzfr(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.vision.text.internal.client.INativeTextRecognizer");
            return (queryLocalInterface == null || !(queryLocalInterface instanceof zzbkf)) ? new zza(iBinder) : (zzbkf) queryLocalInterface;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            Parcelable[] zzd;
            switch (i) {
                case 1:
                    parcel.enforceInterface("com.google.android.gms.vision.text.internal.client.INativeTextRecognizer");
                    zzd = zzd(com.google.android.gms.dynamic.IObjectWrapper.zza.zzcd(parcel.readStrongBinder()), parcel.readInt() != 0 ? (zzbka) zzbka.CREATOR.createFromParcel(parcel) : null);
                    parcel2.writeNoException();
                    parcel2.writeTypedArray(zzd, 1);
                    return true;
                case 2:
                    parcel.enforceInterface("com.google.android.gms.vision.text.internal.client.INativeTextRecognizer");
                    zzTV();
                    parcel2.writeNoException();
                    return true;
                case 3:
                    parcel.enforceInterface("com.google.android.gms.vision.text.internal.client.INativeTextRecognizer");
                    zzd = zza(com.google.android.gms.dynamic.IObjectWrapper.zza.zzcd(parcel.readStrongBinder()), parcel.readInt() != 0 ? (zzbka) zzbka.CREATOR.createFromParcel(parcel) : null, parcel.readInt() != 0 ? (zzbkj) zzbkj.CREATOR.createFromParcel(parcel) : null);
                    parcel2.writeNoException();
                    parcel2.writeTypedArray(zzd, 1);
                    return true;
                case 1598968902:
                    parcel2.writeString("com.google.android.gms.vision.text.internal.client.INativeTextRecognizer");
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }
    }

    void zzTV() throws RemoteException;

    zzbkh[] zza(IObjectWrapper iObjectWrapper, zzbka com_google_android_gms_internal_zzbka, zzbkj com_google_android_gms_internal_zzbkj) throws RemoteException;

    zzbkh[] zzd(IObjectWrapper iObjectWrapper, zzbka com_google_android_gms_internal_zzbka) throws RemoteException;
}
