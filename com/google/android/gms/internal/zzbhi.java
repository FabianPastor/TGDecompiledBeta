package com.google.android.gms.internal;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import com.google.android.gms.dynamic.zzd;

public interface zzbhi extends IInterface {

    public static abstract class zza extends Binder implements zzbhi {

        private static class zza implements zzbhi {
            private IBinder zzrp;

            zza(IBinder iBinder) {
                this.zzrp = iBinder;
            }

            public IBinder asBinder() {
                return this.zzrp;
            }

            public void zzSu() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.gms.vision.text.internal.client.INativeTextRecognizer");
                    this.zzrp.transact(2, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public zzbhk[] zza(zzd com_google_android_gms_dynamic_zzd, zzbhd com_google_android_gms_internal_zzbhd, zzbhm com_google_android_gms_internal_zzbhm) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.gms.vision.text.internal.client.INativeTextRecognizer");
                    obtain.writeStrongBinder(com_google_android_gms_dynamic_zzd != null ? com_google_android_gms_dynamic_zzd.asBinder() : null);
                    if (com_google_android_gms_internal_zzbhd != null) {
                        obtain.writeInt(1);
                        com_google_android_gms_internal_zzbhd.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (com_google_android_gms_internal_zzbhm != null) {
                        obtain.writeInt(1);
                        com_google_android_gms_internal_zzbhm.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.zzrp.transact(3, obtain, obtain2, 0);
                    obtain2.readException();
                    zzbhk[] com_google_android_gms_internal_zzbhkArr = (zzbhk[]) obtain2.createTypedArray(zzbhk.CREATOR);
                    return com_google_android_gms_internal_zzbhkArr;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public zzbhk[] zzd(zzd com_google_android_gms_dynamic_zzd, zzbhd com_google_android_gms_internal_zzbhd) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.gms.vision.text.internal.client.INativeTextRecognizer");
                    obtain.writeStrongBinder(com_google_android_gms_dynamic_zzd != null ? com_google_android_gms_dynamic_zzd.asBinder() : null);
                    if (com_google_android_gms_internal_zzbhd != null) {
                        obtain.writeInt(1);
                        com_google_android_gms_internal_zzbhd.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.zzrp.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                    zzbhk[] com_google_android_gms_internal_zzbhkArr = (zzbhk[]) obtain2.createTypedArray(zzbhk.CREATOR);
                    return com_google_android_gms_internal_zzbhkArr;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public static zzbhi zzfk(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.vision.text.internal.client.INativeTextRecognizer");
            return (queryLocalInterface == null || !(queryLocalInterface instanceof zzbhi)) ? new zza(iBinder) : (zzbhi) queryLocalInterface;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            Parcelable[] zzd;
            switch (i) {
                case 1:
                    parcel.enforceInterface("com.google.android.gms.vision.text.internal.client.INativeTextRecognizer");
                    zzd = zzd(com.google.android.gms.dynamic.zzd.zza.zzcd(parcel.readStrongBinder()), parcel.readInt() != 0 ? (zzbhd) zzbhd.CREATOR.createFromParcel(parcel) : null);
                    parcel2.writeNoException();
                    parcel2.writeTypedArray(zzd, 1);
                    return true;
                case 2:
                    parcel.enforceInterface("com.google.android.gms.vision.text.internal.client.INativeTextRecognizer");
                    zzSu();
                    parcel2.writeNoException();
                    return true;
                case 3:
                    parcel.enforceInterface("com.google.android.gms.vision.text.internal.client.INativeTextRecognizer");
                    zzd = zza(com.google.android.gms.dynamic.zzd.zza.zzcd(parcel.readStrongBinder()), parcel.readInt() != 0 ? (zzbhd) zzbhd.CREATOR.createFromParcel(parcel) : null, parcel.readInt() != 0 ? (zzbhm) zzbhm.CREATOR.createFromParcel(parcel) : null);
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

    void zzSu() throws RemoteException;

    zzbhk[] zza(zzd com_google_android_gms_dynamic_zzd, zzbhd com_google_android_gms_internal_zzbhd, zzbhm com_google_android_gms_internal_zzbhm) throws RemoteException;

    zzbhk[] zzd(zzd com_google_android_gms_dynamic_zzd, zzbhd com_google_android_gms_internal_zzbhd) throws RemoteException;
}
