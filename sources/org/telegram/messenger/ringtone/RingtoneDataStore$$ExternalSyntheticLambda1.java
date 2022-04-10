package org.telegram.messenger.ringtone;

import org.telegram.tgnet.TLObject;

public final /* synthetic */ class RingtoneDataStore$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ RingtoneDataStore f$0;
    public final /* synthetic */ TLObject f$1;

    public /* synthetic */ RingtoneDataStore$$ExternalSyntheticLambda1(RingtoneDataStore ringtoneDataStore, TLObject tLObject) {
        this.f$0 = ringtoneDataStore;
        this.f$1 = tLObject;
    }

    public final void run() {
        this.f$0.lambda$loadUserRingtones$0(this.f$1);
    }
}
