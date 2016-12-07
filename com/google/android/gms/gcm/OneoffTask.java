package com.google.android.gms.gcm;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable.Creator;

public class OneoffTask extends Task {
    public static final Creator<OneoffTask> CREATOR = new Creator<OneoffTask>() {
        public /* synthetic */ Object createFromParcel(Parcel parcel) {
            return zzmz(parcel);
        }

        public /* synthetic */ Object[] newArray(int i) {
            return zzto(i);
        }

        public OneoffTask zzmz(Parcel parcel) {
            return new OneoffTask(parcel);
        }

        public OneoffTask[] zzto(int i) {
            return new OneoffTask[i];
        }
    };
    private final long ahi;
    private final long ahj;

    public static class Builder extends com.google.android.gms.gcm.Task.Builder {
        private long ahk;
        private long ahl;

        public Builder() {
            this.ahk = -1;
            this.ahl = -1;
            this.isPersisted = false;
        }

        public OneoffTask build() {
            checkConditions();
            return new OneoffTask();
        }

        protected void checkConditions() {
            super.checkConditions();
            if (this.ahk == -1 || this.ahl == -1) {
                throw new IllegalArgumentException("Must specify an execution window using setExecutionWindow.");
            } else if (this.ahk >= this.ahl) {
                throw new IllegalArgumentException("Window start must be shorter than window end.");
            }
        }

        public Builder setExecutionWindow(long j, long j2) {
            this.ahk = j;
            this.ahl = j2;
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
        this.ahi = parcel.readLong();
        this.ahj = parcel.readLong();
    }

    private OneoffTask(Builder builder) {
        super((com.google.android.gms.gcm.Task.Builder) builder);
        this.ahi = builder.ahk;
        this.ahj = builder.ahl;
    }

    public long getWindowEnd() {
        return this.ahj;
    }

    public long getWindowStart() {
        return this.ahi;
    }

    public void toBundle(Bundle bundle) {
        super.toBundle(bundle);
        bundle.putLong("window_start", this.ahi);
        bundle.putLong("window_end", this.ahj);
    }

    public String toString() {
        String valueOf = String.valueOf(super.toString());
        long windowStart = getWindowStart();
        return new StringBuilder(String.valueOf(valueOf).length() + 64).append(valueOf).append(" windowStart=").append(windowStart).append(" windowEnd=").append(getWindowEnd()).toString();
    }

    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeLong(this.ahi);
        parcel.writeLong(this.ahj);
    }
}
