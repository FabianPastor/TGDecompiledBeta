package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class InstrumentInfo extends zza {
    public static final int CARD_CLASS_CREDIT = 1;
    public static final int CARD_CLASS_DEBIT = 2;
    public static final int CARD_CLASS_PREPAID = 3;
    public static final int CARD_CLASS_UNKNOWN = 0;
    public static final Creator<InstrumentInfo> CREATOR = new zzj();
    private String zzbOI;
    private String zzbOJ;
    private int zzbOK;

    @Retention(RetentionPolicy.SOURCE)
    public @interface CardClass {
    }

    private InstrumentInfo() {
    }

    public InstrumentInfo(String str, String str2, int i) {
        this.zzbOI = str;
        this.zzbOJ = str2;
        this.zzbOK = i;
    }

    public final int getCardClass() {
        switch (this.zzbOK) {
            case 1:
            case 2:
            case 3:
                return this.zzbOK;
            default:
                return 0;
        }
    }

    public final String getInstrumentDetails() {
        return this.zzbOJ;
    }

    public final String getInstrumentType() {
        return this.zzbOI;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zza(parcel, 2, getInstrumentType(), false);
        zzd.zza(parcel, 3, getInstrumentDetails(), false);
        zzd.zzc(parcel, 4, getCardClass());
        zzd.zzI(parcel, zze);
    }
}
