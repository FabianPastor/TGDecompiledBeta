package com.google.firebase.iid;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

final class zzm extends Handler {
    private /* synthetic */ zzl zzckF;

    zzm(zzl com_google_firebase_iid_zzl, Looper looper) {
        this.zzckF = com_google_firebase_iid_zzl;
        super(looper);
    }

    public final void handleMessage(Message message) {
        this.zzckF.zzc(message);
    }
}