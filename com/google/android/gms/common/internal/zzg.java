package com.google.android.gms.common.internal;

import android.accounts.Account;
import android.content.Context;
import android.view.View;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient.Builder;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.internal.zzaxo;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class zzg {
    private final Set<Scope> zzaEb;
    private final Map<Api<?>, zza> zzaEc;
    private final zzaxo zzaEd;
    private Integer zzaEe;
    private final Account zzagg;
    private final String zzahp;
    private final Set<Scope> zzaxN;
    private final int zzaxP;
    private final View zzaxQ;
    private final String zzaxR;

    public static final class zza {
        public final boolean zzaEf;
        public final Set<Scope> zzajm;

        public zza(Set<Scope> set, boolean z) {
            zzac.zzw(set);
            this.zzajm = Collections.unmodifiableSet(set);
            this.zzaEf = z;
        }
    }

    public zzg(Account account, Set<Scope> set, Map<Api<?>, zza> map, int i, View view, String str, String str2, zzaxo com_google_android_gms_internal_zzaxo) {
        Map map2;
        this.zzagg = account;
        this.zzaxN = set == null ? Collections.EMPTY_SET : Collections.unmodifiableSet(set);
        if (map == null) {
            map2 = Collections.EMPTY_MAP;
        }
        this.zzaEc = map2;
        this.zzaxQ = view;
        this.zzaxP = i;
        this.zzahp = str;
        this.zzaxR = str2;
        this.zzaEd = com_google_android_gms_internal_zzaxo;
        Set hashSet = new HashSet(this.zzaxN);
        for (zza com_google_android_gms_common_internal_zzg_zza : this.zzaEc.values()) {
            hashSet.addAll(com_google_android_gms_common_internal_zzg_zza.zzajm);
        }
        this.zzaEb = Collections.unmodifiableSet(hashSet);
    }

    public static zzg zzaA(Context context) {
        return new Builder(context).zzuP();
    }

    public Account getAccount() {
        return this.zzagg;
    }

    @Deprecated
    public String getAccountName() {
        return this.zzagg != null ? this.zzagg.name : null;
    }

    public Set<Scope> zzc(Api<?> api) {
        zza com_google_android_gms_common_internal_zzg_zza = (zza) this.zzaEc.get(api);
        if (com_google_android_gms_common_internal_zzg_zza == null || com_google_android_gms_common_internal_zzg_zza.zzajm.isEmpty()) {
            return this.zzaxN;
        }
        Set<Scope> hashSet = new HashSet(this.zzaxN);
        hashSet.addAll(com_google_android_gms_common_internal_zzg_zza.zzajm);
        return hashSet;
    }

    public void zzc(Integer num) {
        this.zzaEe = num;
    }

    public Account zzwU() {
        return this.zzagg != null ? this.zzagg : new Account("<<default account>>", "com.google");
    }

    public int zzxd() {
        return this.zzaxP;
    }

    public Set<Scope> zzxe() {
        return this.zzaxN;
    }

    public Set<Scope> zzxf() {
        return this.zzaEb;
    }

    public Map<Api<?>, zza> zzxg() {
        return this.zzaEc;
    }

    public String zzxh() {
        return this.zzahp;
    }

    public String zzxi() {
        return this.zzaxR;
    }

    public View zzxj() {
        return this.zzaxQ;
    }

    public zzaxo zzxk() {
        return this.zzaEd;
    }

    public Integer zzxl() {
        return this.zzaEe;
    }
}
