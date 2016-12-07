package com.google.android.gms.internal;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import com.google.android.gms.dynamic.zzd;
import com.google.android.gms.vision.barcode.Barcode;

public interface zzbgr extends IInterface {

    public static abstract class zza extends Binder implements zzbgr {

        private static class zza implements zzbgr {
            private IBinder zzrp;

            zza(IBinder iBinder) {
                this.zzrp = iBinder;
            }

            public IBinder asBinder() {
                return this.zzrp;
            }

            public void zzSo() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.gms.vision.barcode.internal.client.INativeBarcodeDetector");
                    this.zzrp.transact(3, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public Barcode[] zza(zzd com_google_android_gms_dynamic_zzd, zzbhd com_google_android_gms_internal_zzbhd) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.gms.vision.barcode.internal.client.INativeBarcodeDetector");
                    obtain.writeStrongBinder(com_google_android_gms_dynamic_zzd != null ? com_google_android_gms_dynamic_zzd.asBinder() : null);
                    if (com_google_android_gms_internal_zzbhd != null) {
                        obtain.writeInt(1);
                        com_google_android_gms_internal_zzbhd.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.zzrp.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                    Barcode[] barcodeArr = (Barcode[]) obtain2.createTypedArray(Barcode.CREATOR);
                    return barcodeArr;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public Barcode[] zzb(zzd com_google_android_gms_dynamic_zzd, zzbhd com_google_android_gms_internal_zzbhd) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.gms.vision.barcode.internal.client.INativeBarcodeDetector");
                    obtain.writeStrongBinder(com_google_android_gms_dynamic_zzd != null ? com_google_android_gms_dynamic_zzd.asBinder() : null);
                    if (com_google_android_gms_internal_zzbhd != null) {
                        obtain.writeInt(1);
                        com_google_android_gms_internal_zzbhd.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.zzrp.transact(2, obtain, obtain2, 0);
                    obtain2.readException();
                    Barcode[] barcodeArr = (Barcode[]) obtain2.createTypedArray(Barcode.CREATOR);
                    return barcodeArr;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public static zzbgr zzfg(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.vision.barcode.internal.client.INativeBarcodeDetector");
            return (queryLocalInterface == null || !(queryLocalInterface instanceof zzbgr)) ? new zza(iBinder) : (zzbgr) queryLocalInterface;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            zzbhd com_google_android_gms_internal_zzbhd = null;
            zzd zzcd;
            Parcelable[] zza;
            switch (i) {
                case 1:
                    parcel.enforceInterface("com.google.android.gms.vision.barcode.internal.client.INativeBarcodeDetector");
                    zzcd = com.google.android.gms.dynamic.zzd.zza.zzcd(parcel.readStrongBinder());
                    if (parcel.readInt() != 0) {
                        com_google_android_gms_internal_zzbhd = (zzbhd) zzbhd.CREATOR.createFromParcel(parcel);
                    }
                    zza = zza(zzcd, com_google_android_gms_internal_zzbhd);
                    parcel2.writeNoException();
                    parcel2.writeTypedArray(zza, 1);
                    return true;
                case 2:
                    parcel.enforceInterface("com.google.android.gms.vision.barcode.internal.client.INativeBarcodeDetector");
                    zzcd = com.google.android.gms.dynamic.zzd.zza.zzcd(parcel.readStrongBinder());
                    if (parcel.readInt() != 0) {
                        com_google_android_gms_internal_zzbhd = (zzbhd) zzbhd.CREATOR.createFromParcel(parcel);
                    }
                    zza = zzb(zzcd, com_google_android_gms_internal_zzbhd);
                    parcel2.writeNoException();
                    parcel2.writeTypedArray(zza, 1);
                    return true;
                case 3:
                    parcel.enforceInterface("com.google.android.gms.vision.barcode.internal.client.INativeBarcodeDetector");
                    zzSo();
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

    void zzSo() throws RemoteException;

    Barcode[] zza(zzd com_google_android_gms_dynamic_zzd, zzbhd com_google_android_gms_internal_zzbhd) throws RemoteException;

    Barcode[] zzb(zzd com_google_android_gms_dynamic_zzd, zzbhd com_google_android_gms_internal_zzbhd) throws RemoteException;
}
