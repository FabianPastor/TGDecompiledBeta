package com.google.firebase;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzbe;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.common.internal.zzby;
import com.google.android.gms.common.util.zzt;
import java.util.Arrays;

public final class FirebaseOptions {
    private final String zzaoM;
    private final String zzbVh;
    private final String zzbVi;
    private final String zzbVj;
    private final String zzbVk;
    private final String zzbVl;
    private final String zzbVm;

    public static final class Builder {
        private String zzaoM;
        private String zzbVh;
        private String zzbVi;
        private String zzbVj;
        private String zzbVk;
        private String zzbVl;
        private String zzbVm;

        public Builder(FirebaseOptions firebaseOptions) {
            this.zzaoM = firebaseOptions.zzaoM;
            this.zzbVh = firebaseOptions.zzbVh;
            this.zzbVi = firebaseOptions.zzbVi;
            this.zzbVj = firebaseOptions.zzbVj;
            this.zzbVk = firebaseOptions.zzbVk;
            this.zzbVl = firebaseOptions.zzbVl;
            this.zzbVm = firebaseOptions.zzbVm;
        }

        public final FirebaseOptions build() {
            return new FirebaseOptions(this.zzaoM, this.zzbVh, this.zzbVi, this.zzbVj, this.zzbVk, this.zzbVl, this.zzbVm);
        }

        public final Builder setApiKey(@NonNull String str) {
            this.zzbVh = zzbo.zzh(str, "ApiKey must be set.");
            return this;
        }

        public final Builder setApplicationId(@NonNull String str) {
            this.zzaoM = zzbo.zzh(str, "ApplicationId must be set.");
            return this;
        }

        public final Builder setDatabaseUrl(@Nullable String str) {
            this.zzbVi = str;
            return this;
        }

        public final Builder setGcmSenderId(@Nullable String str) {
            this.zzbVk = str;
            return this;
        }

        public final Builder setProjectId(@Nullable String str) {
            this.zzbVm = str;
            return this;
        }

        public final Builder setStorageBucket(@Nullable String str) {
            this.zzbVl = str;
            return this;
        }
    }

    private FirebaseOptions(@NonNull String str, @NonNull String str2, @Nullable String str3, @Nullable String str4, @Nullable String str5, @Nullable String str6, @Nullable String str7) {
        zzbo.zza(!zzt.zzcL(str), (Object) "ApplicationId must be set.");
        this.zzaoM = str;
        this.zzbVh = str2;
        this.zzbVi = str3;
        this.zzbVj = str4;
        this.zzbVk = str5;
        this.zzbVl = str6;
        this.zzbVm = str7;
    }

    public static FirebaseOptions fromResource(Context context) {
        zzby com_google_android_gms_common_internal_zzby = new zzby(context);
        Object string = com_google_android_gms_common_internal_zzby.getString("google_app_id");
        return TextUtils.isEmpty(string) ? null : new FirebaseOptions(string, com_google_android_gms_common_internal_zzby.getString("google_api_key"), com_google_android_gms_common_internal_zzby.getString("firebase_database_url"), com_google_android_gms_common_internal_zzby.getString("ga_trackingId"), com_google_android_gms_common_internal_zzby.getString("gcm_defaultSenderId"), com_google_android_gms_common_internal_zzby.getString("google_storage_bucket"), com_google_android_gms_common_internal_zzby.getString("project_id"));
    }

    public final boolean equals(Object obj) {
        if (!(obj instanceof FirebaseOptions)) {
            return false;
        }
        FirebaseOptions firebaseOptions = (FirebaseOptions) obj;
        return zzbe.equal(this.zzaoM, firebaseOptions.zzaoM) && zzbe.equal(this.zzbVh, firebaseOptions.zzbVh) && zzbe.equal(this.zzbVi, firebaseOptions.zzbVi) && zzbe.equal(this.zzbVj, firebaseOptions.zzbVj) && zzbe.equal(this.zzbVk, firebaseOptions.zzbVk) && zzbe.equal(this.zzbVl, firebaseOptions.zzbVl) && zzbe.equal(this.zzbVm, firebaseOptions.zzbVm);
    }

    public final String getApiKey() {
        return this.zzbVh;
    }

    public final String getApplicationId() {
        return this.zzaoM;
    }

    public final String getDatabaseUrl() {
        return this.zzbVi;
    }

    public final String getGcmSenderId() {
        return this.zzbVk;
    }

    public final String getProjectId() {
        return this.zzbVm;
    }

    public final String getStorageBucket() {
        return this.zzbVl;
    }

    public final int hashCode() {
        return Arrays.hashCode(new Object[]{this.zzaoM, this.zzbVh, this.zzbVi, this.zzbVj, this.zzbVk, this.zzbVl, this.zzbVm});
    }

    public final String toString() {
        return zzbe.zzt(this).zzg("applicationId", this.zzaoM).zzg("apiKey", this.zzbVh).zzg("databaseUrl", this.zzbVi).zzg("gcmSenderId", this.zzbVk).zzg("storageBucket", this.zzbVl).zzg("projectId", this.zzbVm).toString();
    }
}
