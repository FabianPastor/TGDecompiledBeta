package org.telegram.messenger.voip;

import org.telegram.messenger.voip.Instance;

public final /* synthetic */ class VoIPService$$ExternalSyntheticLambda65 implements Instance.OnSignalBarsUpdatedListener {
    public final /* synthetic */ VoIPService f$0;

    public /* synthetic */ VoIPService$$ExternalSyntheticLambda65(VoIPService voIPService) {
        this.f$0 = voIPService;
    }

    public final void onSignalBarsUpdated(int i) {
        this.f$0.onSignalBarCountChanged(i);
    }
}
