package org.telegram.messenger.voip;

import org.telegram.messenger.voip.NativeInstance;

public final /* synthetic */ class VoIPService$$ExternalSyntheticLambda75 implements NativeInstance.RequestCurrentTimeCallback {
    public final /* synthetic */ VoIPService f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ VoIPService$$ExternalSyntheticLambda75(VoIPService voIPService, int i) {
        this.f$0 = voIPService;
        this.f$1 = i;
    }

    public final void run(long j) {
        this.f$0.lambda$createGroupInstance$49(this.f$1, j);
    }
}
