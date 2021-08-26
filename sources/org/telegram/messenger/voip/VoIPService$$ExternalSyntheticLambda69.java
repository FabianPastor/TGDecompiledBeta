package org.telegram.messenger.voip;

import org.telegram.messenger.voip.Instance;

public final /* synthetic */ class VoIPService$$ExternalSyntheticLambda69 implements Instance.OnStateUpdatedListener {
    public final /* synthetic */ VoIPService f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ VoIPService$$ExternalSyntheticLambda69(VoIPService voIPService, int i) {
        this.f$0 = voIPService;
        this.f$1 = i;
    }

    public final void onStateUpdated(int i, boolean z) {
        this.f$0.lambda$createGroupInstance$49(this.f$1, i, z);
    }
}
