package com.google.android.gms.common.data;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.data.DataHolder.zza;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

public class zzd<T extends SafeParcelable> extends AbstractDataBuffer<T> {
    private static final String[] zzaDN = new String[]{"data"};
    private final Creator<T> zzaDO;

    public zzd(DataHolder dataHolder, Creator<T> creator) {
        super(dataHolder);
        this.zzaDO = creator;
    }

    public static <T extends SafeParcelable> void zza(zza com_google_android_gms_common_data_DataHolder_zza, T t) {
        Parcel obtain = Parcel.obtain();
        t.writeToParcel(obtain, 0);
        ContentValues contentValues = new ContentValues();
        contentValues.put("data", obtain.marshall());
        com_google_android_gms_common_data_DataHolder_zza.zza(contentValues);
        obtain.recycle();
    }

    public static zza zzxj() {
        return DataHolder.zzc(zzaDN);
    }

    public /* synthetic */ Object get(int i) {
        return zzcH(i);
    }

    public T zzcH(int i) {
        byte[] zzg = this.zzaBi.zzg("data", i, this.zzaBi.zzcI(i));
        Parcel obtain = Parcel.obtain();
        obtain.unmarshall(zzg, 0, zzg.length);
        obtain.setDataPosition(0);
        SafeParcelable safeParcelable = (SafeParcelable) this.zzaDO.createFromParcel(obtain);
        obtain.recycle();
        return safeParcelable;
    }
}
