package com.google.android.gms.common.internal;

import android.accounts.Account;
import android.os.IInterface;
import android.os.RemoteException;

public interface zzal extends IInterface {
    Account getAccount() throws RemoteException;
}
