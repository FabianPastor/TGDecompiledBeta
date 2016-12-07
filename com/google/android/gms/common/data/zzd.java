package com.google.android.gms.common.data;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.data.DataHolder.zza;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

public class zzd<T extends SafeParcelable> extends AbstractDataBuffer<T> {
    private static final String[] BW = new String[]{"data"};
    private final Creator<T> BX;

    public zzd(DataHolder dataHolder, Creator<T> creator) {
        super(dataHolder);
        this.BX = creator;
    }

    public static <T extends SafeParcelable> void zza(zza com_google_android_gms_common_data_DataHolder_zza, T t) {
        Parcel obtain = Parcel.obtain();
        t.writeToParcel(obtain, 0);
        ContentValues contentValues = new ContentValues();
        contentValues.put("data", obtain.marshall());
        com_google_android_gms_common_data_DataHolder_zza.zza(contentValues);
        obtain.recycle();
    }

    public static zza zzaum() {
        return DataHolder.zzc(BW);
    }

    public /* synthetic */ Object get(int i) {
        return zzfz(i);
    }

    public T zzfz(int i) {
        byte[] zzg = this.zy.zzg("data", i, this.zy.zzga(i));
        Parcel obtain = Parcel.obtain();
        obtain.unmarshall(zzg, 0, zzg.length);
        obtain.setDataPosition(0);
        SafeParcelable safeParcelable = (SafeParcelable) this.BX.createFromParcel(obtain);
        obtain.recycle();
        return safeParcelable;
    }
}
