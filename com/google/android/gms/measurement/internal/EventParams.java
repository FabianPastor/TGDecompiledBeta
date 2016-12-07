package com.google.android.gms.measurement.internal;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.zzaa;
import java.util.Iterator;

public class EventParams extends AbstractSafeParcelable implements Iterable<String> {
    public static final Creator<EventParams> CREATOR = new zzj();
    private final Bundle arG;
    public final int versionCode;

    EventParams(int i, Bundle bundle) {
        this.versionCode = i;
        this.arG = bundle;
    }

    EventParams(Bundle bundle) {
        zzaa.zzy(bundle);
        this.arG = bundle;
        this.versionCode = 1;
    }

    Object get(String str) {
        return this.arG.get(str);
    }

    public Iterator<String> iterator() {
        return new Iterator<String>(this) {
            Iterator<String> arH = this.arI.arG.keySet().iterator();
            final /* synthetic */ EventParams arI;

            {
                this.arI = r2;
            }

            public boolean hasNext() {
                return this.arH.hasNext();
            }

            public String next() {
                return (String) this.arH.next();
            }

            public void remove() {
                throw new UnsupportedOperationException("Remove not supported");
            }
        };
    }

    public int size() {
        return this.arG.size();
    }

    public String toString() {
        return this.arG.toString();
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzj.zza(this, parcel, i);
    }

    public Bundle zzbww() {
        return new Bundle(this.arG);
    }
}
