package com.google.android.gms.vision.text.internal.client;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import com.google.android.gms.dynamic.zzd;
import com.google.android.gms.vision.internal.client.FrameMetadataParcel;

public interface zzb extends IInterface {

    public static abstract class zza extends Binder implements zzb {

        private static class zza implements zzb {
            private IBinder zzajq;

            zza(IBinder iBinder) {
                this.zzajq = iBinder;
            }

            public IBinder asBinder() {
                return this.zzajq;
            }

            public LineBoxParcel[] zza(zzd com_google_android_gms_dynamic_zzd, FrameMetadataParcel frameMetadataParcel, RecognitionOptions recognitionOptions) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.gms.vision.text.internal.client.INativeTextRecognizer");
                    obtain.writeStrongBinder(com_google_android_gms_dynamic_zzd != null ? com_google_android_gms_dynamic_zzd.asBinder() : null);
                    if (frameMetadataParcel != null) {
                        obtain.writeInt(1);
                        frameMetadataParcel.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (recognitionOptions != null) {
                        obtain.writeInt(1);
                        recognitionOptions.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.zzajq.transact(3, obtain, obtain2, 0);
                    obtain2.readException();
                    LineBoxParcel[] lineBoxParcelArr = (LineBoxParcel[]) obtain2.createTypedArray(LineBoxParcel.CREATOR);
                    return lineBoxParcelArr;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void zzclw() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.gms.vision.text.internal.client.INativeTextRecognizer");
                    this.zzajq.transact(2, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public LineBoxParcel[] zzd(zzd com_google_android_gms_dynamic_zzd, FrameMetadataParcel frameMetadataParcel) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.gms.vision.text.internal.client.INativeTextRecognizer");
                    obtain.writeStrongBinder(com_google_android_gms_dynamic_zzd != null ? com_google_android_gms_dynamic_zzd.asBinder() : null);
                    if (frameMetadataParcel != null) {
                        obtain.writeInt(1);
                        frameMetadataParcel.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.zzajq.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                    LineBoxParcel[] lineBoxParcelArr = (LineBoxParcel[]) obtain2.createTypedArray(LineBoxParcel.CREATOR);
                    return lineBoxParcelArr;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public static zzb zzli(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.vision.text.internal.client.INativeTextRecognizer");
            return (queryLocalInterface == null || !(queryLocalInterface instanceof zzb)) ? new zza(iBinder) : (zzb) queryLocalInterface;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            Parcelable[] zzd;
            switch (i) {
                case 1:
                    parcel.enforceInterface("com.google.android.gms.vision.text.internal.client.INativeTextRecognizer");
                    zzd = zzd(com.google.android.gms.dynamic.zzd.zza.zzfd(parcel.readStrongBinder()), parcel.readInt() != 0 ? (FrameMetadataParcel) FrameMetadataParcel.CREATOR.createFromParcel(parcel) : null);
                    parcel2.writeNoException();
                    parcel2.writeTypedArray(zzd, 1);
                    return true;
                case 2:
                    parcel.enforceInterface("com.google.android.gms.vision.text.internal.client.INativeTextRecognizer");
                    zzclw();
                    parcel2.writeNoException();
                    return true;
                case 3:
                    parcel.enforceInterface("com.google.android.gms.vision.text.internal.client.INativeTextRecognizer");
                    zzd = zza(com.google.android.gms.dynamic.zzd.zza.zzfd(parcel.readStrongBinder()), parcel.readInt() != 0 ? (FrameMetadataParcel) FrameMetadataParcel.CREATOR.createFromParcel(parcel) : null, parcel.readInt() != 0 ? (RecognitionOptions) RecognitionOptions.CREATOR.createFromParcel(parcel) : null);
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

    LineBoxParcel[] zza(zzd com_google_android_gms_dynamic_zzd, FrameMetadataParcel frameMetadataParcel, RecognitionOptions recognitionOptions) throws RemoteException;

    void zzclw() throws RemoteException;

    LineBoxParcel[] zzd(zzd com_google_android_gms_dynamic_zzd, FrameMetadataParcel frameMetadataParcel) throws RemoteException;
}
