package com.google.android.gms.common.internal;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.v4.view.ViewCompat;
import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;

public interface zzv extends IInterface {

    public static abstract class zza extends Binder implements zzv {

        private static class zza implements zzv {
            private final IBinder zzrk;

            zza(IBinder iBinder) {
                this.zzrk = iBinder;
            }

            public IBinder asBinder() {
                return this.zzrk;
            }

            public void zza(zzu com_google_android_gms_common_internal_zzu, zzj com_google_android_gms_common_internal_zzj) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.gms.common.internal.IGmsServiceBroker");
                    obtain.writeStrongBinder(com_google_android_gms_common_internal_zzu != null ? com_google_android_gms_common_internal_zzu.asBinder() : null);
                    if (com_google_android_gms_common_internal_zzj != null) {
                        obtain.writeInt(1);
                        com_google_android_gms_common_internal_zzj.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.zzrk.transact(46, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public static zzv zzbu(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.common.internal.IGmsServiceBroker");
            return (queryLocalInterface == null || !(queryLocalInterface instanceof zzv)) ? new zza(iBinder) : (zzv) queryLocalInterface;
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i > ViewCompat.MEASURED_SIZE_MASK) {
                return super.onTransact(i, parcel, parcel2, i2);
            }
            parcel.enforceInterface("com.google.android.gms.common.internal.IGmsServiceBroker");
            zzu zzbt = com.google.android.gms.common.internal.zzu.zza.zzbt(parcel.readStrongBinder());
            if (i == 46) {
                zza(zzbt, parcel.readInt() != 0 ? (zzj) zzj.CREATOR.createFromParcel(parcel) : null);
            } else if (i == 47) {
                zza(zzbt, parcel.readInt() != 0 ? (zzan) zzan.CREATOR.createFromParcel(parcel) : null);
            } else {
                String readString;
                String[] createStringArray;
                String readString2;
                String str;
                IBinder iBinder;
                Bundle bundle;
                int readInt = parcel.readInt();
                String readString3 = i != 4 ? parcel.readString() : null;
                switch (i) {
                    case 1:
                        readString = parcel.readString();
                        createStringArray = parcel.createStringArray();
                        readString2 = parcel.readString();
                        if (parcel.readInt() == 0) {
                            str = null;
                            iBinder = null;
                            bundle = null;
                            break;
                        }
                        str = null;
                        iBinder = null;
                        bundle = (Bundle) Bundle.CREATOR.createFromParcel(parcel);
                        break;
                    case 2:
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                    case 11:
                    case 12:
                    case 13:
                    case 14:
                    case 15:
                    case 16:
                    case 17:
                    case 18:
                    case 23:
                    case 25:
                    case 27:
                    case 37:
                    case 38:
                    case 41:
                    case 43:
                        if (parcel.readInt() != 0) {
                            str = null;
                            readString = null;
                            iBinder = null;
                            bundle = (Bundle) Bundle.CREATOR.createFromParcel(parcel);
                            createStringArray = null;
                            readString2 = null;
                            break;
                        }
                    case 9:
                        readString2 = parcel.readString();
                        createStringArray = parcel.createStringArray();
                        readString = parcel.readString();
                        iBinder = parcel.readStrongBinder();
                        str = parcel.readString();
                        if (parcel.readInt() == 0) {
                            bundle = null;
                            break;
                        }
                        bundle = (Bundle) Bundle.CREATOR.createFromParcel(parcel);
                        break;
                    case 10:
                        readString2 = parcel.readString();
                        createStringArray = parcel.createStringArray();
                        str = null;
                        readString = null;
                        iBinder = null;
                        bundle = null;
                        break;
                    case 19:
                        iBinder = parcel.readStrongBinder();
                        if (parcel.readInt() == 0) {
                            str = null;
                            readString = null;
                            bundle = null;
                            createStringArray = null;
                            readString2 = null;
                            break;
                        }
                        str = null;
                        readString = null;
                        bundle = (Bundle) Bundle.CREATOR.createFromParcel(parcel);
                        createStringArray = null;
                        readString2 = null;
                        break;
                    case 20:
                    case NalUnitTypes.NAL_TYPE_RSV_VCL30 /*30*/:
                        createStringArray = parcel.createStringArray();
                        readString2 = parcel.readString();
                        if (parcel.readInt() == 0) {
                            str = null;
                            readString = null;
                            iBinder = null;
                            bundle = null;
                            break;
                        }
                        str = null;
                        readString = null;
                        iBinder = null;
                        bundle = (Bundle) Bundle.CREATOR.createFromParcel(parcel);
                        break;
                    case 34:
                        readString2 = parcel.readString();
                        str = null;
                        readString = null;
                        iBinder = null;
                        bundle = null;
                        createStringArray = null;
                        break;
                    default:
                        str = null;
                        readString = null;
                        iBinder = null;
                        bundle = null;
                        createStringArray = null;
                        readString2 = null;
                        break;
                }
                zza(i, zzbt, readInt, readString3, readString2, createStringArray, bundle, iBinder, readString, str);
            }
            parcel2.writeNoException();
            return true;
        }

        protected void zza(int i, zzu com_google_android_gms_common_internal_zzu, int i2, String str, String str2, String[] strArr, Bundle bundle, IBinder iBinder, String str3, String str4) throws RemoteException {
            throw new UnsupportedOperationException();
        }

        protected void zza(zzu com_google_android_gms_common_internal_zzu, zzan com_google_android_gms_common_internal_zzan) throws RemoteException {
            throw new UnsupportedOperationException();
        }
    }

    void zza(zzu com_google_android_gms_common_internal_zzu, zzj com_google_android_gms_common_internal_zzj) throws RemoteException;
}
