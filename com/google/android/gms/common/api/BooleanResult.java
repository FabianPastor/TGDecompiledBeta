package com.google.android.gms.common.api;

import com.google.android.gms.common.internal.zzac;

public class BooleanResult implements Result {
    private final Status fp;
    private final boolean vu;

    public BooleanResult(Status status, boolean z) {
        this.fp = (Status) zzac.zzb((Object) status, (Object) "Status must not be null");
        this.vu = z;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof BooleanResult)) {
            return false;
        }
        BooleanResult booleanResult = (BooleanResult) obj;
        return this.fp.equals(booleanResult.fp) && this.vu == booleanResult.vu;
    }

    public Status getStatus() {
        return this.fp;
    }

    public boolean getValue() {
        return this.vu;
    }

    public final int hashCode() {
        return (this.vu ? 1 : 0) + ((this.fp.hashCode() + 527) * 31);
    }
}
