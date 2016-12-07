package com.google.android.gms.common.internal;

import android.accounts.Account;
import android.content.Context;
import android.view.View;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient.Builder;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.internal.zzxa;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class zzh {
    private final Set<Scope> BX;
    private final Map<Api<?>, zza> BY;
    private final zzxa BZ;
    private Integer Ca;
    private final Account ec;
    private final String fo;
    private final Set<Scope> vF;
    private final int vH;
    private final View vI;
    private final String vJ;

    public static final class zza {
        public final boolean Cb;
        public final Set<Scope> hm;

        public zza(Set<Scope> set, boolean z) {
            zzac.zzy(set);
            this.hm = Collections.unmodifiableSet(set);
            this.Cb = z;
        }
    }

    public zzh(Account account, Set<Scope> set, Map<Api<?>, zza> map, int i, View view, String str, String str2, zzxa com_google_android_gms_internal_zzxa) {
        Map map2;
        this.ec = account;
        this.vF = set == null ? Collections.EMPTY_SET : Collections.unmodifiableSet(set);
        if (map == null) {
            map2 = Collections.EMPTY_MAP;
        }
        this.BY = map2;
        this.vI = view;
        this.vH = i;
        this.fo = str;
        this.vJ = str2;
        this.BZ = com_google_android_gms_internal_zzxa;
        Set hashSet = new HashSet(this.vF);
        for (zza com_google_android_gms_common_internal_zzh_zza : this.BY.values()) {
            hashSet.addAll(com_google_android_gms_common_internal_zzh_zza.hm);
        }
        this.BX = Collections.unmodifiableSet(hashSet);
    }

    public static zzh zzcd(Context context) {
        return new Builder(context).zzaqd();
    }

    public Account getAccount() {
        return this.ec;
    }

    @Deprecated
    public String getAccountName() {
        return this.ec != null ? this.ec.name : null;
    }

    public Account zzatv() {
        return this.ec != null ? this.ec : new Account("<<default account>>", "com.google");
    }

    public int zzauf() {
        return this.vH;
    }

    public Set<Scope> zzaug() {
        return this.vF;
    }

    public Set<Scope> zzauh() {
        return this.BX;
    }

    public Map<Api<?>, zza> zzaui() {
        return this.BY;
    }

    public String zzauj() {
        return this.fo;
    }

    public String zzauk() {
        return this.vJ;
    }

    public View zzaul() {
        return this.vI;
    }

    public zzxa zzaum() {
        return this.BZ;
    }

    public Integer zzaun() {
        return this.Ca;
    }

    public Set<Scope> zzb(Api<?> api) {
        zza com_google_android_gms_common_internal_zzh_zza = (zza) this.BY.get(api);
        if (com_google_android_gms_common_internal_zzh_zza == null || com_google_android_gms_common_internal_zzh_zza.hm.isEmpty()) {
            return this.vF;
        }
        Set<Scope> hashSet = new HashSet(this.vF);
        hashSet.addAll(com_google_android_gms_common_internal_zzh_zza.hm);
        return hashSet;
    }

    public void zzc(Integer num) {
        this.Ca = num;
    }
}
