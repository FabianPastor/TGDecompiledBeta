package com.google.android.gms.location.places;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.common.internal.zzac;
import com.google.firebase.analytics.FirebaseAnalytics.Param;

public class PlaceReport extends zza implements ReflectedParcelable {
    public static final Creator<PlaceReport> CREATOR = new zzl();
    private final String mTag;
    private final String zzacO;
    final int zzaiI;
    private final String zzblD;

    PlaceReport(int i, String str, String str2, String str3) {
        this.zzaiI = i;
        this.zzblD = str;
        this.mTag = str2;
        this.zzacO = str3;
    }

    public static PlaceReport create(String str, String str2) {
        return zzj(str, str2, "unknown");
    }

    private static boolean zzeU(String str) {
        boolean z = true;
        switch (str.hashCode()) {
            case -1436706272:
                if (str.equals("inferredGeofencing")) {
                    z = true;
                    break;
                }
                break;
            case -1194968642:
                if (str.equals("userReported")) {
                    z = true;
                    break;
                }
                break;
            case -284840886:
                if (str.equals("unknown")) {
                    z = false;
                    break;
                }
                break;
            case -262743844:
                if (str.equals("inferredReverseGeocoding")) {
                    z = true;
                    break;
                }
                break;
            case 1164924125:
                if (str.equals("inferredSnappedToRoad")) {
                    z = true;
                    break;
                }
                break;
            case 1287171955:
                if (str.equals("inferredRadioSignals")) {
                    z = true;
                    break;
                }
                break;
        }
        switch (z) {
            case false:
            case true:
            case true:
            case true:
            case true:
            case true:
                return true;
            default:
                return false;
        }
    }

    public static PlaceReport zzj(String str, String str2, String str3) {
        zzac.zzw(str);
        zzac.zzdr(str2);
        zzac.zzdr(str3);
        zzac.zzb(zzeU(str3), (Object) "Invalid source");
        return new PlaceReport(1, str, str2, str3);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof PlaceReport)) {
            return false;
        }
        PlaceReport placeReport = (PlaceReport) obj;
        return zzaa.equal(this.zzblD, placeReport.zzblD) && zzaa.equal(this.mTag, placeReport.mTag) && zzaa.equal(this.zzacO, placeReport.zzacO);
    }

    public String getPlaceId() {
        return this.zzblD;
    }

    public String getSource() {
        return this.zzacO;
    }

    public String getTag() {
        return this.mTag;
    }

    public int hashCode() {
        return zzaa.hashCode(this.zzblD, this.mTag, this.zzacO);
    }

    public String toString() {
        zzaa.zza zzv = zzaa.zzv(this);
        zzv.zzg("placeId", this.zzblD);
        zzv.zzg("tag", this.mTag);
        if (!"unknown".equals(this.zzacO)) {
            zzv.zzg(Param.SOURCE, this.zzacO);
        }
        return zzv.toString();
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzl.zza(this, parcel, i);
    }
}
