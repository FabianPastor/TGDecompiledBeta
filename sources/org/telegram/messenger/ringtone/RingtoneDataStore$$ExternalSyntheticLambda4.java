package org.telegram.messenger.ringtone;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class RingtoneDataStore$$ExternalSyntheticLambda4 implements RequestDelegate {
    public final /* synthetic */ RingtoneDataStore f$0;

    public /* synthetic */ RingtoneDataStore$$ExternalSyntheticLambda4(RingtoneDataStore ringtoneDataStore) {
        this.f$0 = ringtoneDataStore;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$loadUserRingtones$2(tLObject, tLRPC$TL_error);
    }
}
