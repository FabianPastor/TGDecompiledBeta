package com.google.android.gms.maps.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.annotation.Nullable;
import android.util.Log;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.common.internal.zzac;
import java.util.ArrayList;
import java.util.List;

public class PatternItem extends zza {
    public static final Creator<PatternItem> CREATOR = new zzi();
    private static final String TAG = PatternItem.class.getSimpleName();
    private final int type;
    @Nullable
    private final Float zzbpJ;

    public PatternItem(int i, @Nullable Float f) {
        boolean z = true;
        if (i != 1 && (f == null || f.floatValue() < 0.0f)) {
            z = false;
        }
        String valueOf = String.valueOf(f);
        zzac.zzb(z, new StringBuilder(String.valueOf(valueOf).length() + 45).append("Invalid PatternItem: type=").append(i).append(" length=").append(valueOf).toString());
        this.type = i;
        this.zzbpJ = f;
    }

    @Nullable
    static List<PatternItem> zzI(@Nullable List<PatternItem> list) {
        if (list == null) {
            return null;
        }
        List<PatternItem> arrayList = new ArrayList(list.size());
        for (PatternItem patternItem : list) {
            arrayList.add(patternItem == null ? null : patternItem.zzJO());
        }
        return arrayList;
    }

    private PatternItem zzJO() {
        switch (this.type) {
            case 0:
                return new Dash(this.zzbpJ.floatValue());
            case 1:
                return new Dot();
            case 2:
                return new Gap(this.zzbpJ.floatValue());
            default:
                Log.w(TAG, "Unknown PatternItem type: " + this.type);
                return this;
        }
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof PatternItem)) {
            return false;
        }
        PatternItem patternItem = (PatternItem) obj;
        return this.type == patternItem.type && zzaa.equal(this.zzbpJ, patternItem.zzbpJ);
    }

    public int getType() {
        return this.type;
    }

    public int hashCode() {
        return zzaa.hashCode(Integer.valueOf(this.type), this.zzbpJ);
    }

    public String toString() {
        int i = this.type;
        String valueOf = String.valueOf(this.zzbpJ);
        return new StringBuilder(String.valueOf(valueOf).length() + 39).append("[PatternItem: type=").append(i).append(" length=").append(valueOf).append("]").toString();
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzi.zza(this, parcel, i);
    }

    @Nullable
    public Float zzJN() {
        return this.zzbpJ;
    }
}
