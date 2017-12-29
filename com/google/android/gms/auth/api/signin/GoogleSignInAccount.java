package com.google.android.gms.auth.api.signin;

import android.accounts.Account;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.zzbq;
import com.google.android.gms.common.util.zzd;
import com.google.android.gms.common.util.zzh;
import com.google.android.gms.internal.zzbfm;
import com.google.android.gms.internal.zzbfp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GoogleSignInAccount extends zzbfm implements ReflectedParcelable {
    public static final Creator<GoogleSignInAccount> CREATOR = new zzb();
    private static zzd zzegr = zzh.zzamg();
    private int versionCode;
    private String zzbuz;
    private List<Scope> zzecp;
    private String zzefb;
    private String zzefc;
    private String zzefs;
    private String zzegs;
    private String zzegt;
    private Uri zzegu;
    private String zzegv;
    private long zzegw;
    private String zzegx;
    private Set<Scope> zzegy = new HashSet();

    GoogleSignInAccount(int i, String str, String str2, String str3, String str4, Uri uri, String str5, long j, String str6, List<Scope> list, String str7, String str8) {
        this.versionCode = i;
        this.zzbuz = str;
        this.zzefs = str2;
        this.zzegs = str3;
        this.zzegt = str4;
        this.zzegu = uri;
        this.zzegv = str5;
        this.zzegw = j;
        this.zzegx = str6;
        this.zzecp = list;
        this.zzefb = str7;
        this.zzefc = str8;
    }

    private static GoogleSignInAccount zza(String str, String str2, String str3, String str4, String str5, String str6, Uri uri, Long l, String str7, Set<Scope> set) {
        if (l == null) {
            l = Long.valueOf(zzegr.currentTimeMillis() / 1000);
        }
        return new GoogleSignInAccount(3, str, str2, str3, str4, uri, null, l.longValue(), zzbq.zzgm(str7), new ArrayList((Collection) zzbq.checkNotNull(set)), str5, str6);
    }

    public static GoogleSignInAccount zzeu(String str) throws JSONException {
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
        GoogleSignInAccount zza = zza(jSONObject.optString(TtmlNode.ATTR_ID), jSONObject.optString("tokenId", null), jSONObject.optString("email", null), jSONObject.optString("displayName", null), jSONObject.optString("givenName", null), jSONObject.optString("familyName", null), parse, Long.valueOf(parseLong), jSONObject.getString("obfuscatedIdentifier"), hashSet);
        zza.zzegv = jSONObject.optString("serverAuthCode", null);
        return zza;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof GoogleSignInAccount)) {
            return false;
        }
        GoogleSignInAccount googleSignInAccount = (GoogleSignInAccount) obj;
        return googleSignInAccount.zzegx.equals(this.zzegx) && googleSignInAccount.zzabb().equals(zzabb());
    }

    public Account getAccount() {
        return this.zzegs == null ? null : new Account(this.zzegs, "com.google");
    }

    public String getDisplayName() {
        return this.zzegt;
    }

    public String getEmail() {
        return this.zzegs;
    }

    public String getFamilyName() {
        return this.zzefc;
    }

    public String getGivenName() {
        return this.zzefb;
    }

    public String getId() {
        return this.zzbuz;
    }

    public String getIdToken() {
        return this.zzefs;
    }

    public Uri getPhotoUrl() {
        return this.zzegu;
    }

    public String getServerAuthCode() {
        return this.zzegv;
    }

    public int hashCode() {
        return ((this.zzegx.hashCode() + 527) * 31) + zzabb().hashCode();
    }

    public void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zzc(parcel, 1, this.versionCode);
        zzbfp.zza(parcel, 2, getId(), false);
        zzbfp.zza(parcel, 3, getIdToken(), false);
        zzbfp.zza(parcel, 4, getEmail(), false);
        zzbfp.zza(parcel, 5, getDisplayName(), false);
        zzbfp.zza(parcel, 6, getPhotoUrl(), i, false);
        zzbfp.zza(parcel, 7, getServerAuthCode(), false);
        zzbfp.zza(parcel, 8, this.zzegw);
        zzbfp.zza(parcel, 9, this.zzegx, false);
        zzbfp.zzc(parcel, 10, this.zzecp, false);
        zzbfp.zza(parcel, 11, getGivenName(), false);
        zzbfp.zza(parcel, 12, getFamilyName(), false);
        zzbfp.zzai(parcel, zze);
    }

    public final Set<Scope> zzabb() {
        Set<Scope> hashSet = new HashSet(this.zzecp);
        hashSet.addAll(this.zzegy);
        return hashSet;
    }
}
