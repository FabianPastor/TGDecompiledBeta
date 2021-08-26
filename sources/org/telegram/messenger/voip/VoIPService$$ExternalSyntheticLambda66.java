package org.telegram.messenger.voip;

import org.telegram.messenger.voip.Instance;

public final /* synthetic */ class VoIPService$$ExternalSyntheticLambda66 implements Instance.OnSignalBarsUpdatedListener {
    public final /* synthetic */ VoIPService f$0;

    public /* synthetic */ VoIPService$$ExternalSyntheticLambda66(VoIPService voIPService) {
        this.f$0 = voIPService;
    }

    public final void onSignalBarsUpdated(int i) {
        this.f$0.onSignalBarCountChanged(i);
    }
}
