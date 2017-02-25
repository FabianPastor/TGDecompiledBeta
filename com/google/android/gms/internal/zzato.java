package com.google.android.gms.internal;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import java.util.Iterator;

public class zzato extends zza implements Iterable<String> {
    public static final Creator<zzato> CREATOR = new zzatp();
    private final Bundle zzbrH;

    zzato(Bundle bundle) {
        this.zzbrH = bundle;
    }

    Object get(String str) {
        return this.zzbrH.get(str);
    }

    public Iterator<String> iterator() {
        return new Iterator<String>(this) {
            Iterator<String> zzbrI = this.zzbrJ.zzbrH.keySet().iterator();
            final /* synthetic */ zzato zzbrJ;

            {
                this.zzbrJ = r2;
            }

            public boolean hasNext() {
                return this.zzbrI.hasNext();
            }

            public String next() {
                return (String) this.zzbrI.next();
            }

            public void remove() {
                throw new UnsupportedOperationException("Remove not supported");
            }
        };
    }

    public int size() {
        return this.zzbrH.size();
    }

    public String toString() {
        return this.zzbrH.toString();
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzatp.zza(this, parcel, i);
    }

    public Bundle zzLV() {
        return new Bundle(this.zzbrH);
    }
}
