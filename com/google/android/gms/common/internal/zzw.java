package com.google.android.gms.common.internal;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.dynamic.zzd;

public interface zzw extends IInterface {

    public static abstract class zza extends Binder implements zzw {

        private static class zza implements zzw {
            private IBinder zzajf;

            zza(IBinder iBinder) {
                this.zzajf = iBinder;
            }

            public IBinder asBinder() {
                return this.zzajf;
            }

            public zzd zzauz() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.gms.common.internal.IGoogleCertificatesApi");
                    this.zzajf.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                    zzd zzfe = com.google.android.gms.dynamic.zzd.zza.zzfe(obtain2.readStrongBinder());
                    return zzfe;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public zzd zzava() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.gms.common.internal.IGoogleCertificatesApi");
                    this.zzajf.transact(2, obtain, obtain2, 0);
                    obtain2.readException();
                    zzd zzfe = com.google.android.gms.dynamic.zzd.zza.zzfe(obtain2.readStrongBinder());
                    return zzfe;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public static zzw zzdw(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.common.internal.IGoogleCertificatesApi");
            return (queryLocalInterface == null || !(queryLocalInterface instanceof zzw)) ? new zza(iBinder) : (zzw) queryLocalInterface;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            IBinder iBinder = null;
            zzd zzauz;
            switch (i) {
                case 1:
                    parcel.enforceInterface("com.google.android.gms.common.internal.IGoogleCertificatesApi");
                    zzauz = zzauz();
                    parcel2.writeNoException();
                    if (zzauz != null) {
                        iBinder = zzauz.asBinder();
                    }
                    parcel2.writeStrongBinder(iBinder);
                    return true;
                case 2:
                    parcel.enforceInterface("com.google.android.gms.common.internal.IGoogleCertificatesApi");
                    zzauz = zzava();
                    parcel2.writeNoException();
                    if (zzauz != null) {
                        iBinder = zzauz.asBinder();
                    }
                    parcel2.writeStrongBinder(iBinder);
                    return true;
                case 1598968902:
                    parcel2.writeString("com.google.android.gms.common.internal.IGoogleCertificatesApi");
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }
    }

    zzd zzauz() throws RemoteException;

    zzd zzava() throws RemoteException;
}
