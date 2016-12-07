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
import com.google.android.gms.common.internal.zzac;
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
    public static zze gT = zzh.zzaxj();
    private static Comparator<Scope> hc = new Comparator<Scope>() {
        public /* synthetic */ int compare(Object obj, Object obj2) {
            return zza((Scope) obj, (Scope) obj2);
        }

        public int zza(Scope scope, Scope scope2) {
            return scope.zzaqg().compareTo(scope2.zzaqg());
        }
    };
    List<Scope> fK;
    private String gU;
    private String gV;
    private Uri gW;
    private String gX;
    private long gY;
    private String gZ;
    private String gs;
    private String ha;
    private String hb;
    final int versionCode;
    private String zzbks;

    GoogleSignInAccount(int i, String str, String str2, String str3, String str4, Uri uri, String str5, long j, String str6, List<Scope> list, String str7, String str8) {
        this.versionCode = i;
        this.zzbks = str;
        this.gs = str2;
        this.gU = str3;
        this.gV = str4;
        this.gW = uri;
        this.gX = str5;
        this.gY = j;
        this.gZ = str6;
        this.fK = list;
        this.ha = str7;
        this.hb = str8;
    }

    public static GoogleSignInAccount zza(@Nullable String str, @Nullable String str2, @Nullable String str3, @Nullable String str4, @Nullable String str5, @Nullable String str6, @Nullable Uri uri, @Nullable Long l, @NonNull String str7, @NonNull Set<Scope> set) {
        if (l == null) {
            l = Long.valueOf(gT.currentTimeMillis() / 1000);
        }
        return new GoogleSignInAccount(3, str, str2, str3, str4, uri, null, l.longValue(), zzac.zzhz(str7), new ArrayList((Collection) zzac.zzy(set)), str5, str6);
    }

    private JSONObject zzahi() {
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
            jSONObject.put("expirationTime", this.gY);
            jSONObject.put("obfuscatedIdentifier", zzahf());
            JSONArray jSONArray = new JSONArray();
            Collections.sort(this.fK, hc);
            for (Scope zzaqg : this.fK) {
                jSONArray.put(zzaqg.zzaqg());
            }
            jSONObject.put("grantedScopes", jSONArray);
            return jSONObject;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    public static GoogleSignInAccount zzfw(@Nullable String str) throws JSONException {
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
        return zza(jSONObject.optString(TtmlNode.ATTR_ID), jSONObject.optString("tokenId", null), jSONObject.optString("email", null), jSONObject.optString("displayName", null), jSONObject.optString("givenName", null), jSONObject.optString("familyName", null), parse, Long.valueOf(parseLong), jSONObject.getString("obfuscatedIdentifier"), hashSet).zzfx(jSONObject.optString("serverAuthCode", null));
    }

    public boolean equals(Object obj) {
        return !(obj instanceof GoogleSignInAccount) ? false : ((GoogleSignInAccount) obj).zzahg().equals(zzahg());
    }

    @Nullable
    public String getDisplayName() {
        return this.gV;
    }

    @Nullable
    public String getEmail() {
        return this.gU;
    }

    @Nullable
    public String getFamilyName() {
        return this.hb;
    }

    @Nullable
    public String getGivenName() {
        return this.ha;
    }

    @NonNull
    public Set<Scope> getGrantedScopes() {
        return new HashSet(this.fK);
    }

    @Nullable
    public String getId() {
        return this.zzbks;
    }

    @Nullable
    public String getIdToken() {
        return this.gs;
    }

    @Nullable
    public Uri getPhotoUrl() {
        return this.gW;
    }

    @Nullable
    public String getServerAuthCode() {
        return this.gX;
    }

    public int hashCode() {
        return zzahg().hashCode();
    }

    public void writeToParcel(Parcel parcel, int i) {
        zza.zza(this, parcel, i);
    }

    public boolean zza() {
        return gT.currentTimeMillis() / 1000 >= this.gY - 300;
    }

    public long zzahe() {
        return this.gY;
    }

    @NonNull
    public String zzahf() {
        return this.gZ;
    }

    public String zzahg() {
        return zzahi().toString();
    }

    public String zzahh() {
        JSONObject zzahi = zzahi();
        zzahi.remove("serverAuthCode");
        return zzahi.toString();
    }

    public GoogleSignInAccount zzfx(String str) {
        this.gX = str;
        return this;
    }
}
