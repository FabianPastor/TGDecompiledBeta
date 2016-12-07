package com.google.android.gms.common.api;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.IntentSender.SendIntentException;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.annotation.Nullable;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.zzz;

public final class Status extends AbstractSafeParcelable implements Result, ReflectedParcelable {
    public static final Creator<Status> CREATOR = new zzg();
    public static final Status xZ = new Status(0);
    public static final Status ya = new Status(14);
    public static final Status yb = new Status(8);
    public static final Status yc = new Status(15);
    public static final Status yd = new Status(16);
    public static final Status ye = new Status(17);
    public static final Status yf = new Status(18);
    private final PendingIntent mPendingIntent;
    final int mVersionCode;
    private final int uo;
    private final String wP;

    public Status(int i) {
        this(i, null);
    }

    Status(int i, int i2, String str, PendingIntent pendingIntent) {
        this.mVersionCode = i;
        this.uo = i2;
        this.wP = str;
        this.mPendingIntent = pendingIntent;
    }

    public Status(int i, String str) {
        this(1, i, str, null);
    }

    public Status(int i, String str, PendingIntent pendingIntent) {
        this(1, i, str, pendingIntent);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Status)) {
            return false;
        }
        Status status = (Status) obj;
        return this.mVersionCode == status.mVersionCode && this.uo == status.uo && zzz.equal(this.wP, status.wP) && zzz.equal(this.mPendingIntent, status.mPendingIntent);
    }

    public PendingIntent getResolution() {
        return this.mPendingIntent;
    }

    public Status getStatus() {
        return this;
    }

    public int getStatusCode() {
        return this.uo;
    }

    @Nullable
    public String getStatusMessage() {
        return this.wP;
    }

    public boolean hasResolution() {
        return this.mPendingIntent != null;
    }

    public int hashCode() {
        return zzz.hashCode(Integer.valueOf(this.mVersionCode), Integer.valueOf(this.uo), this.wP, this.mPendingIntent);
    }

    public boolean isCanceled() {
        return this.uo == 16;
    }

    public boolean isInterrupted() {
        return this.uo == 14;
    }

    public boolean isSuccess() {
        return this.uo <= 0;
    }

    public void startResolutionForResult(Activity activity, int i) throws SendIntentException {
        if (hasResolution()) {
            activity.startIntentSenderForResult(this.mPendingIntent.getIntentSender(), i, null, 0, 0, 0);
        }
    }

    public String toString() {
        return zzz.zzx(this).zzg("statusCode", zzark()).zzg("resolution", this.mPendingIntent).toString();
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzg.zza(this, parcel, i);
    }

    PendingIntent zzarj() {
        return this.mPendingIntent;
    }

    public String zzark() {
        return this.wP != null ? this.wP : CommonStatusCodes.getStatusCodeString(this.uo);
    }
}
