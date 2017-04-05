package com.google.android.gms.internal;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.vision.barcode.Barcode;

public interface zzbjo extends IInterface {

    public static abstract class zza extends Binder implements zzbjo {

        private static class zza implements zzbjo {
            private IBinder zzrk;

            zza(IBinder iBinder) {
                this.zzrk = iBinder;
            }

            public IBinder asBinder() {
                return this.zzrk;
            }

            public void zzTQ() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.gms.vision.barcode.internal.client.INativeBarcodeDetector");
                    this.zzrk.transact(3, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public Barcode[] zza(IObjectWrapper iObjectWrapper, zzbka com_google_android_gms_internal_zzbka) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.gms.vision.barcode.internal.client.INativeBarcodeDetector");
                    obtain.writeStrongBinder(iObjectWrapper != null ? iObjectWrapper.asBinder() : null);
                    if (com_google_android_gms_internal_zzbka != null) {
                        obtain.writeInt(1);
                        com_google_android_gms_internal_zzbka.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.zzrk.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                    Barcode[] barcodeArr = (Barcode[]) obtain2.createTypedArray(Barcode.CREATOR);
                    return barcodeArr;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public Barcode[] zzb(IObjectWrapper iObjectWrapper, zzbka com_google_android_gms_internal_zzbka) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.gms.vision.barcode.internal.client.INativeBarcodeDetector");
                    obtain.writeStrongBinder(iObjectWrapper != null ? iObjectWrapper.asBinder() : null);
                    if (com_google_android_gms_internal_zzbka != null) {
                        obtain.writeInt(1);
                        com_google_android_gms_internal_zzbka.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.zzrk.transact(2, obtain, obtain2, 0);
                    obtain2.readException();
                    Barcode[] barcodeArr = (Barcode[]) obtain2.createTypedArray(Barcode.CREATOR);
                    return barcodeArr;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public static zzbjo zzfn(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.vision.barcode.internal.client.INativeBarcodeDetector");
            return (queryLocalInterface == null || !(queryLocalInterface instanceof zzbjo)) ? new zza(iBinder) : (zzbjo) queryLocalInterface;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            zzbka com_google_android_gms_internal_zzbka = null;
            IObjectWrapper zzcd;
            Parcelable[] zza;
            switch (i) {
                case 1:
                    parcel.enforceInterface("com.google.android.gms.vision.barcode.internal.client.INativeBarcodeDetector");
                    zzcd = com.google.android.gms.dynamic.IObjectWrapper.zza.zzcd(parcel.readStrongBinder());
                    if (parcel.readInt() != 0) {
                        com_google_android_gms_internal_zzbka = (zzbka) zzbka.CREATOR.createFromParcel(parcel);
                    }
                    zza = zza(zzcd, com_google_android_gms_internal_zzbka);
                    parcel2.writeNoException();
                    parcel2.writeTypedArray(zza, 1);
                    return true;
                case 2:
                    parcel.enforceInterface("com.google.android.gms.vision.barcode.internal.client.INativeBarcodeDetector");
                    zzcd = com.google.android.gms.dynamic.IObjectWrapper.zza.zzcd(parcel.readStrongBinder());
                    if (parcel.readInt() != 0) {
                        com_google_android_gms_internal_zzbka = (zzbka) zzbka.CREATOR.createFromParcel(parcel);
                    }
                    zza = zzb(zzcd, com_google_android_gms_internal_zzbka);
                    parcel2.writeNoException();
                    parcel2.writeTypedArray(zza, 1);
                    return true;
                case 3:
                    parcel.enforceInterface("com.google.android.gms.vision.barcode.internal.client.INativeBarcodeDetector");
                    zzTQ();
                    parcel2.writeNoException();
                    return true;
                case 1598968902:
                    parcel2.writeString("com.google.android.gms.vision.barcode.internal.client.INativeBarcodeDetector");
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }
    }

    void zzTQ() throws RemoteException;

    Barcode[] zza(IObjectWrapper iObjectWrapper, zzbka com_google_android_gms_internal_zzbka) throws RemoteException;

    Barcode[] zzb(IObjectWrapper iObjectWrapper, zzbka com_google_android_gms_internal_zzbka) throws RemoteException;
}
