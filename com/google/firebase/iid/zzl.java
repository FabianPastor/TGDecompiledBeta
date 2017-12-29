package com.google.firebase.iid;

import android.os.Handler.Callback;
import android.os.Message;

final /* synthetic */ class zzl implements Callback {
    private final zzk zznzg;

    zzl(zzk com_google_firebase_iid_zzk) {
        this.zznzg = com_google_firebase_iid_zzk;
    }

    public final boolean handleMessage(Message message) {
        return this.zznzg.zzd(message);
    }
}
