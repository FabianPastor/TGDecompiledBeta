package com.google.android.gms.common.internal;

import android.accounts.Account;
import android.os.Binder;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.common.zzo;

public final class zza extends zzam {
    private int zzaGG;

    public static Account zza(zzal com_google_android_gms_common_internal_zzal) {
        Account account = null;
        if (com_google_android_gms_common_internal_zzal != null) {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                account = com_google_android_gms_common_internal_zzal.getAccount();
            } catch (RemoteException e) {
                Log.w("AccountAccessor", "Remote account accessor probably died");
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
        return account;
    }

    public final boolean equals(Object obj) {
        Account account = null;
        return this == obj ? true : !(obj instanceof zza) ? false : account.equals(account);
    }

    public final Account getAccount() {
        int callingUid = Binder.getCallingUid();
        if (callingUid != this.zzaGG) {
            if (zzo.zzf(null, callingUid)) {
                this.zzaGG = callingUid;
            } else {
                throw new SecurityException("Caller is not GooglePlayServices");
            }
        }
        return null;
    }
}
