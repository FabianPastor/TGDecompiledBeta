package com.google.firebase.iid;

import java.util.concurrent.TimeUnit;

final /* synthetic */ class zzn implements Runnable {
    private final zzk zznzg;

    zzn(zzk com_google_firebase_iid_zzk) {
        this.zznzg = com_google_firebase_iid_zzk;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void run() {
        zzk com_google_firebase_iid_zzk = this.zznzg;
        while (true) {
            synchronized (com_google_firebase_iid_zzk) {
                if (com_google_firebase_iid_zzk.state != 2) {
                    return;
                } else if (com_google_firebase_iid_zzk.zznzd.isEmpty()) {
                    com_google_firebase_iid_zzk.zzcjc();
                    return;
                } else {
                    zzr com_google_firebase_iid_zzr = (zzr) com_google_firebase_iid_zzk.zznzd.poll();
                    com_google_firebase_iid_zzk.zznze.put(com_google_firebase_iid_zzr.zzift, com_google_firebase_iid_zzr);
                    com_google_firebase_iid_zzk.zznzf.zznyy.schedule(new zzo(com_google_firebase_iid_zzk, com_google_firebase_iid_zzr), 30, TimeUnit.SECONDS);
                }
            }
        }
    }
}
