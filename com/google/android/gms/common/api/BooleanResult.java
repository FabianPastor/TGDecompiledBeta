package com.google.android.gms.common.api;

import com.google.android.gms.common.internal.zzac;

public class BooleanResult implements Result {
    private final Status zzair;
    private final boolean zzayS;

    public BooleanResult(Status status, boolean z) {
        this.zzair = (Status) zzac.zzb((Object) status, (Object) "Status must not be null");
        this.zzayS = z;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof BooleanResult)) {
            return false;
        }
        BooleanResult booleanResult = (BooleanResult) obj;
        return this.zzair.equals(booleanResult.zzair) && this.zzayS == booleanResult.zzayS;
    }

    public Status getStatus() {
        return this.zzair;
    }

    public boolean getValue() {
        return this.zzayS;
    }

    public final int hashCode() {
        return (this.zzayS ? 1 : 0) + ((this.zzair.hashCode() + 527) * 31);
    }
}
