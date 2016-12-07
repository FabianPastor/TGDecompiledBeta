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
import com.google.android.gms.common.internal.zzac;
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
    private static Comparator<Scope> hc = new Comparator<Scope>() {
        public /* synthetic */ int compare(Object obj, Object obj2) {
            return zza((Scope) obj, (Scope) obj2);
        }

        public int zza(Scope scope, Scope scope2) {
            return scope.zzaqg().compareTo(scope2.zzaqg());
        }
    };
    public static final Scope hd = new Scope(Scopes.PROFILE);
    public static final Scope he = new Scope("email");
    public static final Scope hf = new Scope("openid");
    private Account ec;
    private final ArrayList<Scope> hg;
    private boolean hh;
    private final boolean hi;
    private final boolean hj;
    private String hk;
    private String hl;
    final int versionCode;

    public static final class Builder {
        private Account ec;
        private boolean hh;
        private boolean hi;
        private boolean hj;
        private String hk;
        private String hl;
        private Set<Scope> hm = new HashSet();

        public Builder(@NonNull GoogleSignInOptions googleSignInOptions) {
            zzac.zzy(googleSignInOptions);
            this.hm = new HashSet(googleSignInOptions.hg);
            this.hi = googleSignInOptions.hi;
            this.hj = googleSignInOptions.hj;
            this.hh = googleSignInOptions.hh;
            this.hk = googleSignInOptions.hk;
            this.ec = googleSignInOptions.ec;
            this.hl = googleSignInOptions.hl;
        }

        private String zzfz(String str) {
            zzac.zzhz(str);
            boolean z = this.hk == null || this.hk.equals(str);
            zzac.zzb(z, (Object) "two different server client ids provided");
            return str;
        }

        public GoogleSignInOptions build() {
            if (this.hh && (this.ec == null || !this.hm.isEmpty())) {
                requestId();
            }
            return new GoogleSignInOptions(this.hm, this.ec, this.hh, this.hi, this.hj, this.hk, this.hl);
        }

        public Builder requestEmail() {
            this.hm.add(GoogleSignInOptions.he);
            return this;
        }

        public Builder requestId() {
            this.hm.add(GoogleSignInOptions.hf);
            return this;
        }

        public Builder requestIdToken(String str) {
            this.hh = true;
            this.hk = zzfz(str);
            return this;
        }

        public Builder requestProfile() {
            this.hm.add(GoogleSignInOptions.hd);
            return this;
        }

        public Builder requestScopes(Scope scope, Scope... scopeArr) {
            this.hm.add(scope);
            this.hm.addAll(Arrays.asList(scopeArr));
            return this;
        }

        public Builder requestServerAuthCode(String str) {
            return requestServerAuthCode(str, false);
        }

        public Builder requestServerAuthCode(String str, boolean z) {
            this.hi = true;
            this.hk = zzfz(str);
            this.hj = z;
            return this;
        }

        public Builder setAccountName(String str) {
            this.ec = new Account(zzac.zzhz(str), "com.google");
            return this;
        }

        public Builder setHostedDomain(String str) {
            this.hl = zzac.zzhz(str);
            return this;
        }
    }

    GoogleSignInOptions(int i, ArrayList<Scope> arrayList, Account account, boolean z, boolean z2, boolean z3, String str, String str2) {
        this.versionCode = i;
        this.hg = arrayList;
        this.ec = account;
        this.hh = z;
        this.hi = z2;
        this.hj = z3;
        this.hk = str;
        this.hl = str2;
    }

    private GoogleSignInOptions(Set<Scope> set, Account account, boolean z, boolean z2, boolean z3, String str, String str2) {
        this(2, new ArrayList(set), account, z, z2, z3, str, str2);
    }

    private JSONObject zzahi() {
        JSONObject jSONObject = new JSONObject();
        try {
            JSONArray jSONArray = new JSONArray();
            Collections.sort(this.hg, hc);
            Iterator it = this.hg.iterator();
            while (it.hasNext()) {
                jSONArray.put(((Scope) it.next()).zzaqg());
            }
            jSONObject.put("scopes", jSONArray);
            if (this.ec != null) {
                jSONObject.put("accountName", this.ec.name);
            }
            jSONObject.put("idTokenRequested", this.hh);
            jSONObject.put("forceCodeForRefreshToken", this.hj);
            jSONObject.put("serverAuthRequested", this.hi);
            if (!TextUtils.isEmpty(this.hk)) {
                jSONObject.put("serverClientId", this.hk);
            }
            if (!TextUtils.isEmpty(this.hl)) {
                jSONObject.put("hostedDomain", this.hl);
            }
            return jSONObject;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    public static GoogleSignInOptions zzfy(@Nullable String str) throws JSONException {
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
            if (this.hg.size() != googleSignInOptions.zzahj().size() || !this.hg.containsAll(googleSignInOptions.zzahj())) {
                return false;
            }
            if (this.ec == null) {
                if (googleSignInOptions.getAccount() != null) {
                    return false;
                }
            } else if (!this.ec.equals(googleSignInOptions.getAccount())) {
                return false;
            }
            if (TextUtils.isEmpty(this.hk)) {
                if (!TextUtils.isEmpty(googleSignInOptions.zzahn())) {
                    return false;
                }
            } else if (!this.hk.equals(googleSignInOptions.zzahn())) {
                return false;
            }
            return this.hj == googleSignInOptions.zzahm() && this.hh == googleSignInOptions.zzahk() && this.hi == googleSignInOptions.zzahl();
        } catch (ClassCastException e) {
            return false;
        }
    }

    public Account getAccount() {
        return this.ec;
    }

    public Scope[] getScopeArray() {
        return (Scope[]) this.hg.toArray(new Scope[this.hg.size()]);
    }

    public int hashCode() {
        List arrayList = new ArrayList();
        Iterator it = this.hg.iterator();
        while (it.hasNext()) {
            arrayList.add(((Scope) it.next()).zzaqg());
        }
        Collections.sort(arrayList);
        return new zze().zzq(arrayList).zzq(this.ec).zzq(this.hk).zzbd(this.hj).zzbd(this.hh).zzbd(this.hi).zzahv();
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzb.zza(this, parcel, i);
    }

    public String zzahg() {
        return zzahi().toString();
    }

    public ArrayList<Scope> zzahj() {
        return new ArrayList(this.hg);
    }

    public boolean zzahk() {
        return this.hh;
    }

    public boolean zzahl() {
        return this.hi;
    }

    public boolean zzahm() {
        return this.hj;
    }

    public String zzahn() {
        return this.hk;
    }

    public String zzaho() {
        return this.hl;
    }
}
