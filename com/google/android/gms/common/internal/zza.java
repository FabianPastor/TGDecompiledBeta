package com.google.android.gms.common.internal;

import android.accounts.Account;
import android.os.Binder;
import android.os.RemoteException;
import android.util.Log;

public final class zza extends zzao {
    public static Account zza(zzan com_google_android_gms_common_internal_zzan) {
        Account account = null;
        if (com_google_android_gms_common_internal_zzan != null) {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                account = com_google_android_gms_common_internal_zzan.getAccount();
            } catch (RemoteException e) {
                Log.w("AccountAccessor", "Remote account accessor probably died");
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
        return account;
    }

    public final boolean equals(Object obj) {
        throw new NoSuchMethodError();
    }

    public final Account getAccount() {
        throw new NoSuchMethodError();
    }
}
