package com.google.android.gms.auth.api.signin;

import android.accounts.Account;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.common.util.zze;
import com.google.android.gms.common.util.zzi;
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

public class GoogleSignInAccount extends zza implements ReflectedParcelable {
    public static final Creator<GoogleSignInAccount> CREATOR = new zzb();
    private static zze zzalN = zzi.zzrY();
    private static Comparator<Scope> zzalU = new zza();
    private int versionCode;
    private String zzIi;
    private String zzakX;
    private String zzakY;
    private List<Scope> zzakz;
    private String zzalO;
    private String zzalP;
    private Uri zzalQ;
    private String zzalR;
    private long zzalS;
    private String zzalT;
    private String zzaln;

    GoogleSignInAccount(int i, String str, String str2, String str3, String str4, Uri uri, String str5, long j, String str6, List<Scope> list, String str7, String str8) {
        this.versionCode = i;
        this.zzIi = str;
        this.zzaln = str2;
        this.zzalO = str3;
        this.zzalP = str4;
        this.zzalQ = uri;
        this.zzalR = str5;
        this.zzalS = j;
        this.zzalT = str6;
        this.zzakz = list;
        this.zzakX = str7;
        this.zzakY = str8;
    }

    @Nullable
    public static GoogleSignInAccount zzbP(@Nullable String str) throws JSONException {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        JSONObject jSONObject = new JSONObject(str);
        Uri uri = null;
        Object optString = jSONObject.optString("photoUrl", null);
        if (!TextUtils.isEmpty(optString)) {
            uri = Uri.parse(optString);
        }
        long parseLong = Long.parseLong(jSONObject.getString("expirationTime"));
        Set hashSet = new HashSet();
        JSONArray jSONArray = jSONObject.getJSONArray("grantedScopes");
        int length = jSONArray.length();
        for (int i = 0; i < length; i++) {
            hashSet.add(new Scope(jSONArray.getString(i)));
        }
        String optString2 = jSONObject.optString(TtmlNode.ATTR_ID);
        String optString3 = jSONObject.optString("tokenId", null);
        String optString4 = jSONObject.optString("email", null);
        String optString5 = jSONObject.optString("displayName", null);
        String optString6 = jSONObject.optString("givenName", null);
        String optString7 = jSONObject.optString("familyName", null);
        Long valueOf = Long.valueOf(parseLong);
        GoogleSignInAccount googleSignInAccount = new GoogleSignInAccount(3, optString2, optString3, optString4, optString5, uri, null, (valueOf == null ? Long.valueOf(zzalN.currentTimeMillis() / 1000) : valueOf).longValue(), zzbo.zzcF(jSONObject.getString("obfuscatedIdentifier")), new ArrayList((Collection) zzbo.zzu(hashSet)), optString6, optString7);
        googleSignInAccount.zzalR = jSONObject.optString("serverAuthCode", null);
        return googleSignInAccount;
    }

    private final JSONObject zzmz() {
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
            jSONObject.put("expirationTime", this.zzalS);
            jSONObject.put("obfuscatedIdentifier", this.zzalT);
            JSONArray jSONArray = new JSONArray();
            Collections.sort(this.zzakz, zzalU);
            for (Scope zzpp : this.zzakz) {
                jSONArray.put(zzpp.zzpp());
            }
            jSONObject.put("grantedScopes", jSONArray);
            return jSONObject;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public boolean equals(Object obj) {
        return !(obj instanceof GoogleSignInAccount) ? false : ((GoogleSignInAccount) obj).zzmz().toString().equals(zzmz().toString());
    }

    @Nullable
    public Account getAccount() {
        return this.zzalO == null ? null : new Account(this.zzalO, "com.google");
    }

    @Nullable
    public String getDisplayName() {
        return this.zzalP;
    }

    @Nullable
    public String getEmail() {
        return this.zzalO;
    }

    @Nullable
    public String getFamilyName() {
        return this.zzakY;
    }

    @Nullable
    public String getGivenName() {
        return this.zzakX;
    }

    @NonNull
    public Set<Scope> getGrantedScopes() {
        return new HashSet(this.zzakz);
    }

    @Nullable
    public String getId() {
        return this.zzIi;
    }

    @Nullable
    public String getIdToken() {
        return this.zzaln;
    }

    @Nullable
    public Uri getPhotoUrl() {
        return this.zzalQ;
    }

    @Nullable
    public String getServerAuthCode() {
        return this.zzalR;
    }

    public int hashCode() {
        return zzmz().toString().hashCode();
    }

    public void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zzc(parcel, 1, this.versionCode);
        zzd.zza(parcel, 2, getId(), false);
        zzd.zza(parcel, 3, getIdToken(), false);
        zzd.zza(parcel, 4, getEmail(), false);
        zzd.zza(parcel, 5, getDisplayName(), false);
        zzd.zza(parcel, 6, getPhotoUrl(), i, false);
        zzd.zza(parcel, 7, getServerAuthCode(), false);
        zzd.zza(parcel, 8, this.zzalS);
        zzd.zza(parcel, 9, this.zzalT, false);
        zzd.zzc(parcel, 10, this.zzakz, false);
        zzd.zza(parcel, 11, getGivenName(), false);
        zzd.zza(parcel, 12, getFamilyName(), false);
        zzd.zzI(parcel, zze);
    }

    public final boolean zzmw() {
        return zzalN.currentTimeMillis() / 1000 >= this.zzalS - 300;
    }

    @NonNull
    public final String zzmx() {
        return this.zzalT;
    }

    public final String zzmy() {
        JSONObject zzmz = zzmz();
        zzmz.remove("serverAuthCode");
        return zzmz.toString();
    }
}
