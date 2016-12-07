package com.google.android.gms.common.api;

import com.google.android.gms.common.internal.zzac;

public class BooleanResult implements Result {
    private final Status zzahq;
    private final boolean zzaxF;

    public BooleanResult(Status status, boolean z) {
        this.zzahq = (Status) zzac.zzb((Object) status, (Object) "Status must not be null");
        this.zzaxF = z;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof BooleanResult)) {
            return false;
        }
        BooleanResult booleanResult = (BooleanResult) obj;
        return this.zzahq.equals(booleanResult.zzahq) && this.zzaxF == booleanResult.zzaxF;
    }

    public Status getStatus() {
        return this.zzahq;
    }

    public boolean getValue() {
        return this.zzaxF;
    }

    public final int hashCode() {
        return (this.zzaxF ? 1 : 0) + ((this.zzahq.hashCode() + 527) * 31);
    }
}
