package org.telegram.messenger.voip;

import org.telegram.messenger.voip.Instance;

public final /* synthetic */ class VoIPService$$ExternalSyntheticLambda63 implements Instance.OnStateUpdatedListener {
    public final /* synthetic */ VoIPService f$0;

    public /* synthetic */ VoIPService$$ExternalSyntheticLambda63(VoIPService voIPService) {
        this.f$0 = voIPService;
    }

    public final void onStateUpdated(int i, boolean z) {
        this.f$0.onConnectionStateChanged(i, z);
    }
}
