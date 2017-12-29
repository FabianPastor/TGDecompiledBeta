package com.google.android.gms.common.internal;

import android.accounts.Account;
import android.support.v4.util.ArraySet;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.internal.zzcxe;
import java.util.Collection;

public final class zzs {
    private String zzebs;
    private Account zzebz;
    private int zzfmq = 0;
    private String zzfms;
    private zzcxe zzfzi = zzcxe.zzkbs;
    private ArraySet<Scope> zzfzk;

    public final zzr zzald() {
        return new zzr(this.zzebz, this.zzfzk, null, 0, null, this.zzebs, this.zzfms, this.zzfzi);
    }

    public final zzs zze(Account account) {
        this.zzebz = account;
        return this;
    }

    public final zzs zze(Collection<Scope> collection) {
        if (this.zzfzk == null) {
            this.zzfzk = new ArraySet();
        }
        this.zzfzk.addAll(collection);
        return this;
    }

    public final zzs zzgf(String str) {
        this.zzebs = str;
        return this;
    }

    public final zzs zzgg(String str) {
        this.zzfms = str;
        return this;
    }
}
