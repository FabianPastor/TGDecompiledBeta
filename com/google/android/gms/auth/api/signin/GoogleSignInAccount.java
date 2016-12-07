package com.google.android.gms.auth.api.signin;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.common.util.zze;
import com.google.android.gms.common.util.zzh;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GoogleSignInAccount extends AbstractSafeParcelable implements ReflectedParcelable {
    public static final Creator<GoogleSignInAccount> CREATOR = new zza();
    public static zze jf = zzh.zzayl();
    private static Comparator<Scope> jm = new Comparator<Scope>() {
        public /* synthetic */ int compare(Object obj, Object obj2) {
            return zza((Scope) obj, (Scope) obj2);
        }

        public int zza(Scope scope, Scope scope2) {
            return scope.zzari().compareTo(scope2.zzari());
        }
    };
    List<Scope> hR;
    private String iF;
    private String is;
    private String it;
    private String jg;
    private String jh;
    private Uri ji;
    private String jj;
    private long jk;
    private String jl;
    final int versionCode;
    private String zzboa;

    GoogleSignInAccount(int i, String str, String str2, String str3, String str4, Uri uri, String str5, long j, String str6, List<Scope> list, String str7, String str8) {
        this.versionCode = i;
        this.zzboa = str;
        this.iF = str2;
        this.jg = str3;
        this.jh = str4;
        this.ji = uri;
        this.jj = str5;
        this.jk = j;
        this.jl = str6;
        this.hR = list;
        this.is = str7;
        this.it = str8;
    }

    public static GoogleSignInAccount zza(@Nullable String str, @Nullable String str2, @Nullable String str3, @Nullable String str4, @Nullable String str5, @Nullable String str6, @Nullable Uri uri, @Nullable Long l, @NonNull String str7, @NonNull Set<Scope> set) {
        if (l == null) {
            l = Long.valueOf(jf.currentTimeMillis() / 1000);
        }
        return new GoogleSignInAccount(3, str, str2, str3, str4, uri, null, l.longValue(), zzaa.zzib(str7), new ArrayList((Collection) zzaa.zzy(set)), str5, str6);
    }

    private JSONObject zzais() {
        JSONObject jSONObject = new JSONObject();
        try {
            if (getId() != null) {
                jSONObject.put(TtmlNode.ATTR_ID, getId());
            }
            if (getIdToken() != null) {
                jSONObject.put("tokenId", getIdToken());
            }
            if (getEmail() != null) {
                jSONObject.put("email", getEmail());
            }
            if (getDisplayName() != null) {
                jSONObject.put("displayName", getDisplayName());
            }
            if (getGivenName() != null) {
                jSONObject.put("givenName", getGivenName());
            }
            if (getFamilyName() != null) {
                jSONObject.put("familyName", getFamilyName());
            }
            if (getPhotoUrl() != null) {
                jSONObject.put("photoUrl", getPhotoUrl().toString());
            }
            if (getServerAuthCode() != null) {
                jSONObject.put("serverAuthCode", getServerAuthCode());
            }
            jSONObject.put("expirationTime", this.jk);
            jSONObject.put("obfuscatedIdentifier", zzaip());
            JSONArray jSONArray = new JSONArray();
            Collections.sort(this.hR, jm);
            for (Scope zzari : this.hR) {
                jSONArray.put(zzari.zzari());
            }
            jSONObject.put("grantedScopes", jSONArray);
            return jSONObject;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    public static GoogleSignInAccount zzfz(@Nullable String str) throws JSONException {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        JSONObject jSONObject = new JSONObject(str);
        Object optString = jSONObject.optString("photoUrl", null);
        Uri parse = !TextUtils.isEmpty(optString) ? Uri.parse(optString) : null;
        long parseLong = Long.parseLong(jSONObject.getString("expirationTime"));
        Set hashSet = new HashSet();
        JSONArray jSONArray = jSONObject.getJSONArray("grantedScopes");
        int length = jSONArray.length();
        for (int i = 0; i < length; i++) {
            hashSet.add(new Scope(jSONArray.getString(i)));
        }
        return zza(jSONObject.optString(TtmlNode.ATTR_ID), jSONObject.optString("tokenId", null), jSONObject.optString("email", null), jSONObject.optString("displayName", null), jSONObject.optString("givenName", null), jSONObject.optString("familyName", null), parse, Long.valueOf(parseLong), jSONObject.getString("obfuscatedIdentifier"), hashSet).zzga(jSONObject.optString("serverAuthCode", null));
    }

    public boolean equals(Object obj) {
        return !(obj instanceof GoogleSignInAccount) ? false : ((GoogleSignInAccount) obj).zzaiq().equals(zzaiq());
    }

    @Nullable
    public String getDisplayName() {
        return this.jh;
    }

    @Nullable
    public String getEmail() {
        return this.jg;
    }

    @Nullable
    public String getFamilyName() {
        return this.it;
    }

    @Nullable
    public String getGivenName() {
        return this.is;
    }

    @NonNull
    public Set<Scope> getGrantedScopes() {
        return new HashSet(this.hR);
    }

    @Nullable
    public String getId() {
        return this.zzboa;
    }

    @Nullable
    public String getIdToken() {
        return this.iF;
    }

    @Nullable
    public Uri getPhotoUrl() {
        return this.ji;
    }

    @Nullable
    public String getServerAuthCode() {
        return this.jj;
    }

    public int hashCode() {
        return zzaiq().hashCode();
    }

    public void writeToParcel(Parcel parcel, int i) {
        zza.zza(this, parcel, i);
    }

    public boolean zza() {
        return jf.currentTimeMillis() / 1000 >= this.jk - 300;
    }

    public long zzaio() {
        return this.jk;
    }

    @NonNull
    public String zzaip() {
        return this.jl;
    }

    public String zzaiq() {
        return zzais().toString();
    }

    public String zzair() {
        JSONObject zzais = zzais();
        zzais.remove("serverAuthCode");
        return zzais.toString();
    }

    public GoogleSignInAccount zzga(String str) {
        this.jj = str;
        return this;
    }
}
