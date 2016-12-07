package com.google.android.gms.auth.api.signin;

import android.accounts.Account;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.google.android.gms.auth.api.signin.internal.zze;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.Api.ApiOptions.Optional;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.zzaa;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GoogleSignInOptions extends AbstractSafeParcelable implements Optional, ReflectedParcelable {
    public static final Creator<GoogleSignInOptions> CREATOR = new zzb();
    public static final GoogleSignInOptions DEFAULT_SIGN_IN = new Builder().requestId().requestProfile().build();
    private static Comparator<Scope> jm = new Comparator<Scope>() {
        public /* synthetic */ int compare(Object obj, Object obj2) {
            return zza((Scope) obj, (Scope) obj2);
        }

        public int zza(Scope scope, Scope scope2) {
            return scope.zzari().compareTo(scope2.zzari());
        }
    };
    public static final Scope jn = new Scope(Scopes.PROFILE);
    public static final Scope jo = new Scope("email");
    public static final Scope jp = new Scope("openid");
    private Account gj;
    private final ArrayList<Scope> jq;
    private boolean jr;
    private final boolean js;
    private final boolean jt;
    private String ju;
    private String jv;
    final int versionCode;

    public static final class Builder {
        private Account gj;
        private boolean jr;
        private boolean js;
        private boolean jt;
        private String ju;
        private String jv;
        private Set<Scope> jw = new HashSet();

        public Builder(@NonNull GoogleSignInOptions googleSignInOptions) {
            zzaa.zzy(googleSignInOptions);
            this.jw = new HashSet(googleSignInOptions.jq);
            this.js = googleSignInOptions.js;
            this.jt = googleSignInOptions.jt;
            this.jr = googleSignInOptions.jr;
            this.ju = googleSignInOptions.ju;
            this.gj = googleSignInOptions.gj;
            this.jv = googleSignInOptions.jv;
        }

        private String zzgc(String str) {
            zzaa.zzib(str);
            boolean z = this.ju == null || this.ju.equals(str);
            zzaa.zzb(z, (Object) "two different server client ids provided");
            return str;
        }

        public GoogleSignInOptions build() {
            if (this.jr && (this.gj == null || !this.jw.isEmpty())) {
                requestId();
            }
            return new GoogleSignInOptions(this.jw, this.gj, this.jr, this.js, this.jt, this.ju, this.jv);
        }

        public Builder requestEmail() {
            this.jw.add(GoogleSignInOptions.jo);
            return this;
        }

        public Builder requestId() {
            this.jw.add(GoogleSignInOptions.jp);
            return this;
        }

        public Builder requestIdToken(String str) {
            this.jr = true;
            this.ju = zzgc(str);
            return this;
        }

        public Builder requestProfile() {
            this.jw.add(GoogleSignInOptions.jn);
            return this;
        }

        public Builder requestScopes(Scope scope, Scope... scopeArr) {
            this.jw.add(scope);
            this.jw.addAll(Arrays.asList(scopeArr));
            return this;
        }

        public Builder requestServerAuthCode(String str) {
            return requestServerAuthCode(str, false);
        }

        public Builder requestServerAuthCode(String str, boolean z) {
            this.js = true;
            this.ju = zzgc(str);
            this.jt = z;
            return this;
        }

        public Builder setAccountName(String str) {
            this.gj = new Account(zzaa.zzib(str), "com.google");
            return this;
        }

        public Builder setHostedDomain(String str) {
            this.jv = zzaa.zzib(str);
            return this;
        }
    }

    GoogleSignInOptions(int i, ArrayList<Scope> arrayList, Account account, boolean z, boolean z2, boolean z3, String str, String str2) {
        this.versionCode = i;
        this.jq = arrayList;
        this.gj = account;
        this.jr = z;
        this.js = z2;
        this.jt = z3;
        this.ju = str;
        this.jv = str2;
    }

    private GoogleSignInOptions(Set<Scope> set, Account account, boolean z, boolean z2, boolean z3, String str, String str2) {
        this(2, new ArrayList(set), account, z, z2, z3, str, str2);
    }

    private JSONObject zzais() {
        JSONObject jSONObject = new JSONObject();
        try {
            JSONArray jSONArray = new JSONArray();
            Collections.sort(this.jq, jm);
            Iterator it = this.jq.iterator();
            while (it.hasNext()) {
                jSONArray.put(((Scope) it.next()).zzari());
            }
            jSONObject.put("scopes", jSONArray);
            if (this.gj != null) {
                jSONObject.put("accountName", this.gj.name);
            }
            jSONObject.put("idTokenRequested", this.jr);
            jSONObject.put("forceCodeForRefreshToken", this.jt);
            jSONObject.put("serverAuthRequested", this.js);
            if (!TextUtils.isEmpty(this.ju)) {
                jSONObject.put("serverClientId", this.ju);
            }
            if (!TextUtils.isEmpty(this.jv)) {
                jSONObject.put("hostedDomain", this.jv);
            }
            return jSONObject;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    public static GoogleSignInOptions zzgb(@Nullable String str) throws JSONException {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        JSONObject jSONObject = new JSONObject(str);
        Set hashSet = new HashSet();
        JSONArray jSONArray = jSONObject.getJSONArray("scopes");
        int length = jSONArray.length();
        for (int i = 0; i < length; i++) {
            hashSet.add(new Scope(jSONArray.getString(i)));
        }
        Object optString = jSONObject.optString("accountName", null);
        return new GoogleSignInOptions(hashSet, !TextUtils.isEmpty(optString) ? new Account(optString, "com.google") : null, jSONObject.getBoolean("idTokenRequested"), jSONObject.getBoolean("serverAuthRequested"), jSONObject.getBoolean("forceCodeForRefreshToken"), jSONObject.optString("serverClientId", null), jSONObject.optString("hostedDomain", null));
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        try {
            GoogleSignInOptions googleSignInOptions = (GoogleSignInOptions) obj;
            if (this.jq.size() != googleSignInOptions.zzait().size() || !this.jq.containsAll(googleSignInOptions.zzait())) {
                return false;
            }
            if (this.gj == null) {
                if (googleSignInOptions.getAccount() != null) {
                    return false;
                }
            } else if (!this.gj.equals(googleSignInOptions.getAccount())) {
                return false;
            }
            if (TextUtils.isEmpty(this.ju)) {
                if (!TextUtils.isEmpty(googleSignInOptions.zzaix())) {
                    return false;
                }
            } else if (!this.ju.equals(googleSignInOptions.zzaix())) {
                return false;
            }
            return this.jt == googleSignInOptions.zzaiw() && this.jr == googleSignInOptions.zzaiu() && this.js == googleSignInOptions.zzaiv();
        } catch (ClassCastException e) {
            return false;
        }
    }

    public Account getAccount() {
        return this.gj;
    }

    public Scope[] getScopeArray() {
        return (Scope[]) this.jq.toArray(new Scope[this.jq.size()]);
    }

    public int hashCode() {
        List arrayList = new ArrayList();
        Iterator it = this.jq.iterator();
        while (it.hasNext()) {
            arrayList.add(((Scope) it.next()).zzari());
        }
        Collections.sort(arrayList);
        return new zze().zzq(arrayList).zzq(this.gj).zzq(this.ju).zzbe(this.jt).zzbe(this.jr).zzbe(this.js).zzajf();
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzb.zza(this, parcel, i);
    }

    public String zzaiq() {
        return zzais().toString();
    }

    public ArrayList<Scope> zzait() {
        return new ArrayList(this.jq);
    }

    public boolean zzaiu() {
        return this.jr;
    }

    public boolean zzaiv() {
        return this.js;
    }

    public boolean zzaiw() {
        return this.jt;
    }

    public String zzaix() {
        return this.ju;
    }

    public String zzaiy() {
        return this.jv;
    }
}
