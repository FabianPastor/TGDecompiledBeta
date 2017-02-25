package com.google.android.gms.internal;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;

public interface zzbkg extends IInterface {

    public static abstract class zza extends Binder implements zzbkg {

        private static class zza implements zzbkg {
            private IBinder zzrk;

            zza(IBinder iBinder) {
                this.zzrk = iBinder;
            }

            public IBinder asBinder() {
                return this.zzrk;
            }

            public zzbkf zza(IObjectWrapper iObjectWrapper, zzbko com_google_android_gms_internal_zzbko) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.gms.vision.text.internal.client.INativeTextRecognizerCreator");
                    obtain.writeStrongBinder(iObjectWrapper != null ? iObjectWrapper.asBinder() : null);
                    if (com_google_android_gms_internal_zzbko != null) {
                        obtain.writeInt(1);
                        com_google_android_gms_internal_zzbko.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.zzrk.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                    zzbkf zzfr = com.google.android.gms.internal.zzbkf.zza.zzfr(obtain2.readStrongBinder());
                    return zzfr;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public static zzbkg zzfs(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.vision.text.internal.client.INativeTextRecognizerCreator");
            return (queryLocalInterface == null || !(queryLocalInterface instanceof zzbkg)) ? new zza(iBinder) : (zzbkg) queryLocalInterface;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            IBinder iBinder = null;
            switch (i) {
                case 1:
                    parcel.enforceInterface("com.google.android.gms.vision.text.internal.client.INativeTextRecognizerCreator");
                    zzbkf zza = zza(com.google.android.gms.dynamic.IObjectWrapper.zza.zzcd(parcel.readStrongBinder()), parcel.readInt() != 0 ? (zzbko) zzbko.CREATOR.createFromParcel(parcel) : null);
                    parcel2.writeNoException();
                    if (zza != null) {
                        iBinder = zza.asBinder();
                    }
                    parcel2.writeStrongBinder(iBinder);
                    return true;
                case 1598968902:
                    parcel2.writeString("com.google.android.gms.vision.text.internal.client.INativeTextRecognizerCreator");
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }
    }

    zzbkf zza(IObjectWrapper iObjectWrapper, zzbko com_google_android_gms_internal_zzbko) throws RemoteException;
}
