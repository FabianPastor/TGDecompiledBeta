package org.telegram.messenger.voip;

import org.telegram.messenger.voip.Instance;

public final /* synthetic */ class VoIPService$$ExternalSyntheticLambda65 implements Instance.OnRemoteMediaStateUpdatedListener {
    public final /* synthetic */ VoIPService f$0;

    public /* synthetic */ VoIPService$$ExternalSyntheticLambda65(VoIPService voIPService) {
        this.f$0 = voIPService;
    }

    public final void onMediaStateUpdated(int i, int i2) {
        this.f$0.lambda$initiateActualEncryptedCall$57(i, i2);
    }
}
