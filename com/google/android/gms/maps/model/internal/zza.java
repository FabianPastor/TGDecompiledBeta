package com.google.android.gms.maps.model.internal;

import android.graphics.Bitmap;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.dynamic.zzd;

public interface zza extends IInterface {

    public static abstract class zza extends Binder implements zza {

        private static class zza implements zza {
            private IBinder zzajq;

            zza(IBinder iBinder) {
                this.zzajq = iBinder;
            }

            public IBinder asBinder() {
                return this.zzajq;
            }

            public zzd zzbtc() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.gms.maps.model.internal.IBitmapDescriptorFactoryDelegate");
                    this.zzajq.transact(4, obtain, obtain2, 0);
                    obtain2.readException();
                    zzd zzfd = com.google.android.gms.dynamic.zzd.zza.zzfd(obtain2.readStrongBinder());
                    return zzfd;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public zzd zze(Bitmap bitmap) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.gms.maps.model.internal.IBitmapDescriptorFactoryDelegate");
                    if (bitmap != null) {
                        obtain.writeInt(1);
                        bitmap.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.zzajq.transact(6, obtain, obtain2, 0);
                    obtain2.readException();
                    zzd zzfd = com.google.android.gms.dynamic.zzd.zza.zzfd(obtain2.readStrongBinder());
                    return zzfd;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public zzd zzi(float f) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.gms.maps.model.internal.IBitmapDescriptorFactoryDelegate");
                    obtain.writeFloat(f);
                    this.zzajq.transact(5, obtain, obtain2, 0);
                    obtain2.readException();
                    zzd zzfd = com.google.android.gms.dynamic.zzd.zza.zzfd(obtain2.readStrongBinder());
                    return zzfd;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public zzd zzlg(String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.gms.maps.model.internal.IBitmapDescriptorFactoryDelegate");
                    obtain.writeString(str);
                    this.zzajq.transact(2, obtain, obtain2, 0);
                    obtain2.readException();
                    zzd zzfd = com.google.android.gms.dynamic.zzd.zza.zzfd(obtain2.readStrongBinder());
                    return zzfd;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public zzd zzlh(String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.gms.maps.model.internal.IBitmapDescriptorFactoryDelegate");
                    obtain.writeString(str);
                    this.zzajq.transact(3, obtain, obtain2, 0);
                    obtain2.readException();
                    zzd zzfd = com.google.android.gms.dynamic.zzd.zza.zzfd(obtain2.readStrongBinder());
                    return zzfd;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public zzd zzli(String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.gms.maps.model.internal.IBitmapDescriptorFactoryDelegate");
                    obtain.writeString(str);
                    this.zzajq.transact(7, obtain, obtain2, 0);
                    obtain2.readException();
                    zzd zzfd = com.google.android.gms.dynamic.zzd.zza.zzfd(obtain2.readStrongBinder());
                    return zzfd;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public zzd zzxa(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.gms.maps.model.internal.IBitmapDescriptorFactoryDelegate");
                    obtain.writeInt(i);
                    this.zzajq.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                    zzd zzfd = com.google.android.gms.dynamic.zzd.zza.zzfd(obtain2.readStrongBinder());
                    return zzfd;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public static zza zzjf(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.maps.model.internal.IBitmapDescriptorFactoryDelegate");
            return (queryLocalInterface == null || !(queryLocalInterface instanceof zza)) ? new zza(iBinder) : (zza) queryLocalInterface;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            IBinder iBinder = null;
            zzd zzxa;
            switch (i) {
                case 1:
                    parcel.enforceInterface("com.google.android.gms.maps.model.internal.IBitmapDescriptorFactoryDelegate");
                    zzxa = zzxa(parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeStrongBinder(zzxa != null ? zzxa.asBinder() : null);
                    return true;
                case 2:
                    parcel.enforceInterface("com.google.android.gms.maps.model.internal.IBitmapDescriptorFactoryDelegate");
                    zzxa = zzlg(parcel.readString());
                    parcel2.writeNoException();
                    if (zzxa != null) {
                        iBinder = zzxa.asBinder();
                    }
                    parcel2.writeStrongBinder(iBinder);
                    return true;
                case 3:
                    parcel.enforceInterface("com.google.android.gms.maps.model.internal.IBitmapDescriptorFactoryDelegate");
                    zzxa = zzlh(parcel.readString());
                    parcel2.writeNoException();
                    if (zzxa != null) {
                        iBinder = zzxa.asBinder();
                    }
                    parcel2.writeStrongBinder(iBinder);
                    return true;
                case 4:
                    parcel.enforceInterface("com.google.android.gms.maps.model.internal.IBitmapDescriptorFactoryDelegate");
                    zzxa = zzbtc();
                    parcel2.writeNoException();
                    if (zzxa != null) {
                        iBinder = zzxa.asBinder();
                    }
                    parcel2.writeStrongBinder(iBinder);
                    return true;
                case 5:
                    parcel.enforceInterface("com.google.android.gms.maps.model.internal.IBitmapDescriptorFactoryDelegate");
                    zzxa = zzi(parcel.readFloat());
                    parcel2.writeNoException();
                    if (zzxa != null) {
                        iBinder = zzxa.asBinder();
                    }
                    parcel2.writeStrongBinder(iBinder);
                    return true;
                case 6:
                    parcel.enforceInterface("com.google.android.gms.maps.model.internal.IBitmapDescriptorFactoryDelegate");
                    zzxa = zze(parcel.readInt() != 0 ? (Bitmap) Bitmap.CREATOR.createFromParcel(parcel) : null);
                    parcel2.writeNoException();
                    if (zzxa != null) {
                        iBinder = zzxa.asBinder();
                    }
                    parcel2.writeStrongBinder(iBinder);
                    return true;
                case 7:
                    parcel.enforceInterface("com.google.android.gms.maps.model.internal.IBitmapDescriptorFactoryDelegate");
                    zzxa = zzli(parcel.readString());
                    parcel2.writeNoException();
                    if (zzxa != null) {
                        iBinder = zzxa.asBinder();
                    }
                    parcel2.writeStrongBinder(iBinder);
                    return true;
                case 1598968902:
                    parcel2.writeString("com.google.android.gms.maps.model.internal.IBitmapDescriptorFactoryDelegate");
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }
    }

    zzd zzbtc() throws RemoteException;

    zzd zze(Bitmap bitmap) throws RemoteException;

    zzd zzi(float f) throws RemoteException;

    zzd zzlg(String str) throws RemoteException;

    zzd zzlh(String str) throws RemoteException;

    zzd zzli(String str) throws RemoteException;

    zzd zzxa(int i) throws RemoteException;
}
