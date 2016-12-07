package com.google.android.gms.gcm;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable.Creator;

public class OneoffTask extends Task {
    public static final Creator<OneoffTask> CREATOR = new Creator<OneoffTask>() {
        public /* synthetic */ Object createFromParcel(Parcel parcel) {
            return zzmw(parcel);
        }

        public /* synthetic */ Object[] newArray(int i) {
            return zztn(i);
        }

        public OneoffTask zzmw(Parcel parcel) {
            return new OneoffTask(parcel);
        }

        public OneoffTask[] zztn(int i) {
            return new OneoffTask[i];
        }
    };
    private final long aeZ;
    private final long afa;

    public static class Builder extends com.google.android.gms.gcm.Task.Builder {
        private long afb;
        private long afc;

        public Builder() {
            this.afb = -1;
            this.afc = -1;
            this.isPersisted = false;
        }

        public OneoffTask build() {
            checkConditions();
            return new OneoffTask();
        }

        protected void checkConditions() {
            super.checkConditions();
            if (this.afb == -1 || this.afc == -1) {
                throw new IllegalArgumentException("Must specify an execution window using setExecutionWindow.");
            } else if (this.afb >= this.afc) {
                throw new IllegalArgumentException("Window start must be shorter than window end.");
            }
        }

        public Builder setExecutionWindow(long j, long j2) {
            this.afb = j;
            this.afc = j2;
            return this;
        }

        public Builder setExtras(Bundle bundle) {
            this.extras = bundle;
            return this;
        }

        public Builder setPersisted(boolean z) {
            this.isPersisted = z;
            return this;
        }

        public Builder setRequiredNetwork(int i) {
            this.requiredNetworkState = i;
            return this;
        }

        public Builder setRequiresCharging(boolean z) {
            this.requiresCharging = z;
            return this;
        }

        public Builder setService(Class<? extends GcmTaskService> cls) {
            this.gcmTaskService = cls.getName();
            return this;
        }

        public Builder setTag(String str) {
            this.tag = str;
            return this;
        }

        public Builder setUpdateCurrent(boolean z) {
            this.updateCurrent = z;
            return this;
        }
    }

    @Deprecated
    private OneoffTask(Parcel parcel) {
        super(parcel);
        this.aeZ = parcel.readLong();
        this.afa = parcel.readLong();
    }

    private OneoffTask(Builder builder) {
        super((com.google.android.gms.gcm.Task.Builder) builder);
        this.aeZ = builder.afb;
        this.afa = builder.afc;
    }

    public long getWindowEnd() {
        return this.afa;
    }

    public long getWindowStart() {
        return this.aeZ;
    }

    public void toBundle(Bundle bundle) {
        super.toBundle(bundle);
        bundle.putLong("window_start", this.aeZ);
        bundle.putLong("window_end", this.afa);
    }

    public String toString() {
        String valueOf = String.valueOf(super.toString());
        long windowStart = getWindowStart();
        return new StringBuilder(String.valueOf(valueOf).length() + 64).append(valueOf).append(" windowStart=").append(windowStart).append(" windowEnd=").append(getWindowEnd()).toString();
    }

    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeLong(this.aeZ);
        parcel.writeLong(this.afa);
    }
}
