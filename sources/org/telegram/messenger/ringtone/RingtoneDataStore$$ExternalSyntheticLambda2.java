package org.telegram.messenger.ringtone;

import org.telegram.tgnet.TLRPC$Document;

public final /* synthetic */ class RingtoneDataStore$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ RingtoneDataStore f$0;
    public final /* synthetic */ TLRPC$Document f$1;

    public /* synthetic */ RingtoneDataStore$$ExternalSyntheticLambda2(RingtoneDataStore ringtoneDataStore, TLRPC$Document tLRPC$Document) {
        this.f$0 = ringtoneDataStore;
        this.f$1 = tLRPC$Document;
    }

    public final void run() {
        this.f$0.lambda$checkRingtoneSoundsLoaded$2(this.f$1);
    }
}
