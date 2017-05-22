package com.google.android.gms.internal;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import java.util.Iterator;

public class zzato extends zza implements Iterable<String> {
    public static final Creator<zzato> CREATOR = new zzatp();
    private final Bundle zzbrE;

    zzato(Bundle bundle) {
        this.zzbrE = bundle;
    }

    Object get(String str) {
        return this.zzbrE.get(str);
    }

    public Iterator<String> iterator() {
        return new Iterator<String>(this) {
            Iterator<String> zzbrF = this.zzbrG.zzbrE.keySet().iterator();
            final /* synthetic */ zzato zzbrG;

            {
                this.zzbrG = r2;
            }

            public boolean hasNext() {
                return this.zzbrF.hasNext();
            }

            public String next() {
                return (String) this.zzbrF.next();
            }

            public void remove() {
                throw new UnsupportedOperationException("Remove not supported");
            }
        };
    }

    public int size() {
        return this.zzbrE.size();
    }

    public String toString() {
        return this.zzbrE.toString();
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzatp.zza(this, parcel, i);
    }

    public Bundle zzLW() {
        return new Bundle(this.zzbrE);
    }
}
