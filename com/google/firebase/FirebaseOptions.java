package com.google.firebase;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.common.internal.zzah;
import com.google.android.gms.common.internal.zzz;
import com.google.android.gms.common.util.zzv;

public final class FirebaseOptions {
    private final String aWg;
    private final String aWh;
    private final String aWi;
    private final String aWj;
    private final String aWk;
    private final String lU;

    public static final class Builder {
        private String aWg;
        private String aWh;
        private String aWi;
        private String aWj;
        private String aWk;
        private String lU;

        public Builder(FirebaseOptions firebaseOptions) {
            this.lU = firebaseOptions.lU;
            this.aWg = firebaseOptions.aWg;
            this.aWh = firebaseOptions.aWh;
            this.aWi = firebaseOptions.aWi;
            this.aWj = firebaseOptions.aWj;
            this.aWk = firebaseOptions.aWk;
        }

        public FirebaseOptions build() {
            return new FirebaseOptions(this.lU, this.aWg, this.aWh, this.aWi, this.aWj, this.aWk);
        }

        public Builder setApiKey(@NonNull String str) {
            this.aWg = zzaa.zzh(str, "ApiKey must be set.");
            return this;
        }

        public Builder setApplicationId(@NonNull String str) {
            this.lU = zzaa.zzh(str, "ApplicationId must be set.");
            return this;
        }

        public Builder setDatabaseUrl(@Nullable String str) {
            this.aWh = str;
            return this;
        }

        public Builder setGcmSenderId(@Nullable String str) {
            this.aWj = str;
            return this;
        }

        public Builder setStorageBucket(@Nullable String str) {
            this.aWk = str;
            return this;
        }
    }

    private FirebaseOptions(@NonNull String str, @NonNull String str2, @Nullable String str3, @Nullable String str4, @Nullable String str5, @Nullable String str6) {
        zzaa.zza(!zzv.zzij(str), (Object) "ApplicationId must be set.");
        this.lU = str;
        this.aWg = str2;
        this.aWh = str3;
        this.aWi = str4;
        this.aWj = str5;
        this.aWk = str6;
    }

    public static FirebaseOptions fromResource(Context context) {
        zzah com_google_android_gms_common_internal_zzah = new zzah(context);
        Object string = com_google_android_gms_common_internal_zzah.getString("google_app_id");
        return TextUtils.isEmpty(string) ? null : new FirebaseOptions(string, com_google_android_gms_common_internal_zzah.getString("google_api_key"), com_google_android_gms_common_internal_zzah.getString("firebase_database_url"), com_google_android_gms_common_internal_zzah.getString("ga_trackingId"), com_google_android_gms_common_internal_zzah.getString("gcm_defaultSenderId"), com_google_android_gms_common_internal_zzah.getString("google_storage_bucket"));
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof FirebaseOptions)) {
            return false;
        }
        FirebaseOptions firebaseOptions = (FirebaseOptions) obj;
        return zzz.equal(this.lU, firebaseOptions.lU) && zzz.equal(this.aWg, firebaseOptions.aWg) && zzz.equal(this.aWh, firebaseOptions.aWh) && zzz.equal(this.aWi, firebaseOptions.aWi) && zzz.equal(this.aWj, firebaseOptions.aWj) && zzz.equal(this.aWk, firebaseOptions.aWk);
    }

    public String getApiKey() {
        return this.aWg;
    }

    public String getApplicationId() {
        return this.lU;
    }

    public String getDatabaseUrl() {
        return this.aWh;
    }

    public String getGcmSenderId() {
        return this.aWj;
    }

    public String getStorageBucket() {
        return this.aWk;
    }

    public int hashCode() {
        return zzz.hashCode(this.lU, this.aWg, this.aWh, this.aWi, this.aWj, this.aWk);
    }

    public String toString() {
        return zzz.zzx(this).zzg("applicationId", this.lU).zzg("apiKey", this.aWg).zzg("databaseUrl", this.aWh).zzg("gcmSenderId", this.aWj).zzg("storageBucket", this.aWk).toString();
    }
}
