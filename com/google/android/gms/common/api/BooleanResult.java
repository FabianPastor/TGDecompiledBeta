package com.google.android.gms.common.api;

import com.google.android.gms.common.internal.zzbo;

public class BooleanResult implements Result {
    private final Status mStatus;
    private final boolean zzaAI;

    public BooleanResult(Status status, boolean z) {
        this.mStatus = (Status) zzbo.zzb((Object) status, (Object) "Status must not be null");
        this.zzaAI = z;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof BooleanResult)) {
            return false;
        }
        BooleanResult booleanResult = (BooleanResult) obj;
        return this.mStatus.equals(booleanResult.mStatus) && this.zzaAI == booleanResult.zzaAI;
    }

    public Status getStatus() {
        return this.mStatus;
    }

    public boolean getValue() {
        return this.zzaAI;
    }

    public final int hashCode() {
        return (this.zzaAI ? 1 : 0) + ((this.mStatus.hashCode() + 527) * 31);
    }
}
