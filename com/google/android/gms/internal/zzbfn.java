package com.google.android.gms.internal;

import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.ArrayList;

public final class zzbfn {
    public static int zza(Parcel parcel, int i) {
        return (i & -65536) != -65536 ? (i >> 16) & 65535 : parcel.readInt();
    }

    public static <T extends Parcelable> T zza(Parcel parcel, int i, Creator<T> creator) {
        int zza = zza(parcel, i);
        int dataPosition = parcel.dataPosition();
        if (zza == 0) {
            return null;
        }
        Parcelable parcelable = (Parcelable) creator.createFromParcel(parcel);
        parcel.setDataPosition(zza + dataPosition);
        return parcelable;
    }

    private static void zza(Parcel parcel, int i, int i2) {
        int zza = zza(parcel, i);
        if (zza != i2) {
            String toHexString = Integer.toHexString(zza);
            throw new zzbfo(new StringBuilder(String.valueOf(toHexString).length() + 46).append("Expected size ").append(i2).append(" got ").append(zza).append(" (0x").append(toHexString).append(")").toString(), parcel);
        }
    }

    private static void zza(Parcel parcel, int i, int i2, int i3) {
        if (i2 != i3) {
            String toHexString = Integer.toHexString(i2);
            throw new zzbfo(new StringBuilder(String.valueOf(toHexString).length() + 46).append("Expected size ").append(i3).append(" got ").append(i2).append(" (0x").append(toHexString).append(")").toString(), parcel);
        }
    }

    public static String[] zzaa(Parcel parcel, int i) {
        int zza = zza(parcel, i);
        int dataPosition = parcel.dataPosition();
        if (zza == 0) {
            return null;
        }
        String[] createStringArray = parcel.createStringArray();
        parcel.setDataPosition(zza + dataPosition);
        return createStringArray;
    }

    public static ArrayList<Integer> zzab(Parcel parcel, int i) {
        int zza = zza(parcel, i);
        int dataPosition = parcel.dataPosition();
        if (zza == 0) {
            return null;
        }
        ArrayList<Integer> arrayList = new ArrayList();
        int readInt = parcel.readInt();
        for (int i2 = 0; i2 < readInt; i2++) {
            arrayList.add(Integer.valueOf(parcel.readInt()));
        }
        parcel.setDataPosition(dataPosition + zza);
        return arrayList;
    }

    public static void zzaf(Parcel parcel, int i) {
        if (parcel.dataPosition() != i) {
            throw new zzbfo("Overread allowed size end=" + i, parcel);
        }
    }

    public static void zzb(Parcel parcel, int i) {
        parcel.setDataPosition(zza(parcel, i) + parcel.dataPosition());
    }

    public static <T> T[] zzb(Parcel parcel, int i, Creator<T> creator) {
        int zza = zza(parcel, i);
        int dataPosition = parcel.dataPosition();
        if (zza == 0) {
            return null;
        }
        T[] createTypedArray = parcel.createTypedArray(creator);
        parcel.setDataPosition(zza + dataPosition);
        return createTypedArray;
    }

    public static <T> ArrayList<T> zzc(Parcel parcel, int i, Creator<T> creator) {
        int zza = zza(parcel, i);
        int dataPosition = parcel.dataPosition();
        if (zza == 0) {
            return null;
        }
        ArrayList<T> createTypedArrayList = parcel.createTypedArrayList(creator);
        parcel.setDataPosition(zza + dataPosition);
        return createTypedArrayList;
    }

    public static boolean zzc(Parcel parcel, int i) {
        zza(parcel, i, 4);
        return parcel.readInt() != 0;
    }

    public static int zzd(Parcel parcel) {
        int readInt = parcel.readInt();
        int zza = zza(parcel, readInt);
        int dataPosition = parcel.dataPosition();
        if ((65535 & readInt) != 20293) {
            String str = "Expected object header. Got 0x";
            String valueOf = String.valueOf(Integer.toHexString(readInt));
            throw new zzbfo(valueOf.length() != 0 ? str.concat(valueOf) : new String(str), parcel);
        }
        readInt = dataPosition + zza;
        if (readInt >= dataPosition && readInt <= parcel.dataSize()) {
            return readInt;
        }
        throw new zzbfo("Size read is invalid start=" + dataPosition + " end=" + readInt, parcel);
    }

    public static byte zze(Parcel parcel, int i) {
        zza(parcel, i, 4);
        return (byte) parcel.readInt();
    }

    public static int zzg(Parcel parcel, int i) {
        zza(parcel, i, 4);
        return parcel.readInt();
    }

    public static long zzi(Parcel parcel, int i) {
        zza(parcel, i, 8);
        return parcel.readLong();
    }

    public static Long zzj(Parcel parcel, int i) {
        int zza = zza(parcel, i);
        if (zza == 0) {
            return null;
        }
        zza(parcel, i, zza, 8);
        return Long.valueOf(parcel.readLong());
    }

    public static float zzl(Parcel parcel, int i) {
        zza(parcel, i, 4);
        return parcel.readFloat();
    }

    public static Float zzm(Parcel parcel, int i) {
        int zza = zza(parcel, i);
        if (zza == 0) {
            return null;
        }
        zza(parcel, i, zza, 4);
        return Float.valueOf(parcel.readFloat());
    }

    public static double zzn(Parcel parcel, int i) {
        zza(parcel, i, 8);
        return parcel.readDouble();
    }

    public static Double zzo(Parcel parcel, int i) {
        int zza = zza(parcel, i);
        if (zza == 0) {
            return null;
        }
        zza(parcel, i, zza, 8);
        return Double.valueOf(parcel.readDouble());
    }

    public static String zzq(Parcel parcel, int i) {
        int zza = zza(parcel, i);
        int dataPosition = parcel.dataPosition();
        if (zza == 0) {
            return null;
        }
        String readString = parcel.readString();
        parcel.setDataPosition(zza + dataPosition);
        return readString;
    }

    public static IBinder zzr(Parcel parcel, int i) {
        int zza = zza(parcel, i);
        int dataPosition = parcel.dataPosition();
        if (zza == 0) {
            return null;
        }
        IBinder readStrongBinder = parcel.readStrongBinder();
        parcel.setDataPosition(zza + dataPosition);
        return readStrongBinder;
    }

    public static Bundle zzs(Parcel parcel, int i) {
        int zza = zza(parcel, i);
        int dataPosition = parcel.dataPosition();
        if (zza == 0) {
            return null;
        }
        Bundle readBundle = parcel.readBundle();
        parcel.setDataPosition(zza + dataPosition);
        return readBundle;
    }

    public static byte[] zzt(Parcel parcel, int i) {
        int zza = zza(parcel, i);
        int dataPosition = parcel.dataPosition();
        if (zza == 0) {
            return null;
        }
        byte[] createByteArray = parcel.createByteArray();
        parcel.setDataPosition(zza + dataPosition);
        return createByteArray;
    }

    public static int[] zzw(Parcel parcel, int i) {
        int zza = zza(parcel, i);
        int dataPosition = parcel.dataPosition();
        if (zza == 0) {
            return null;
        }
        int[] createIntArray = parcel.createIntArray();
        parcel.setDataPosition(zza + dataPosition);
        return createIntArray;
    }
}
