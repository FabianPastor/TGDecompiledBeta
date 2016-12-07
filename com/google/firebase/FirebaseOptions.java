package com.google.firebase;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzab;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.internal.zzaj;
import com.google.android.gms.common.util.zzw;

public final class FirebaseOptions {
    private final String aSU;
    private final String aSV;
    private final String jM;
    private final String yQ;
    private final String yT;
    private final String yU;

    public static final class Builder {
        private String aSU;
        private String aSV;
        private String jM;
        private String yQ;
        private String yT;
        private String yU;

        public Builder(FirebaseOptions firebaseOptions) {
            this.jM = firebaseOptions.jM;
            this.yQ = firebaseOptions.yQ;
            this.aSU = firebaseOptions.aSU;
            this.aSV = firebaseOptions.aSV;
            this.yT = firebaseOptions.yT;
            this.yU = firebaseOptions.yU;
        }

        public FirebaseOptions build() {
            return new FirebaseOptions(this.jM, this.yQ, this.aSU, this.aSV, this.yT, this.yU);
        }

        public Builder setApiKey(@NonNull String str) {
            this.yQ = zzac.zzh(str, "ApiKey must be set.");
            return this;
        }

        public Builder setApplicationId(@NonNull String str) {
            this.jM = zzac.zzh(str, "ApplicationId must be set.");
            return this;
        }

        public Builder setDatabaseUrl(@Nullable String str) {
            this.aSU = str;
            return this;
        }

        public Builder setGcmSenderId(@Nullable String str) {
            this.yT = str;
            return this;
        }

        public Builder setStorageBucket(@Nullable String str) {
            this.yU = str;
            return this;
        }
    }

    private FirebaseOptions(@NonNull String str, @NonNull String str2, @Nullable String str3, @Nullable String str4, @Nullable String str5, @Nullable String str6) {
        zzac.zza(!zzw.zzij(str), (Object) "ApplicationId must be set.");
        this.jM = str;
        this.yQ = str2;
        this.aSU = str3;
        this.aSV = str4;
        this.yT = str5;
        this.yU = str6;
    }

    public static FirebaseOptions fromResource(Context context) {
        zzaj com_google_android_gms_common_internal_zzaj = new zzaj(context);
        Object string = com_google_android_gms_common_internal_zzaj.getString("google_app_id");
        return TextUtils.isEmpty(string) ? null : new FirebaseOptions(string, com_google_android_gms_common_internal_zzaj.getString("google_api_key"), com_google_android_gms_common_internal_zzaj.getString("firebase_database_url"), com_google_android_gms_common_internal_zzaj.getString("ga_trackingId"), com_google_android_gms_common_internal_zzaj.getString("gcm_defaultSenderId"), com_google_android_gms_common_internal_zzaj.getString("google_storage_bucket"));
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof FirebaseOptions)) {
            return false;
        }
        FirebaseOptions firebaseOptions = (FirebaseOptions) obj;
        return zzab.equal(this.jM, firebaseOptions.jM) && zzab.equal(this.yQ, firebaseOptions.yQ) && zzab.equal(this.aSU, firebaseOptions.aSU) && zzab.equal(this.aSV, firebaseOptions.aSV) && zzab.equal(this.yT, firebaseOptions.yT) && zzab.equal(this.yU, firebaseOptions.yU);
    }

    public String getApiKey() {
        return this.yQ;
    }

    public String getApplicationId() {
        return this.jM;
    }

    public String getDatabaseUrl() {
        return this.aSU;
    }

    public String getGcmSenderId() {
        return this.yT;
    }

    public String getStorageBucket() {
        return this.yU;
    }

    public int hashCode() {
        return zzab.hashCode(this.jM, this.yQ, this.aSU, this.aSV, this.yT, this.yU);
    }

    public String toString() {
        return zzab.zzx(this).zzg("applicationId", this.jM).zzg("apiKey", this.yQ).zzg("databaseUrl", this.aSU).zzg("gcmSenderId", this.yT).zzg("storageBucket", this.yU).toString();
    }
}
