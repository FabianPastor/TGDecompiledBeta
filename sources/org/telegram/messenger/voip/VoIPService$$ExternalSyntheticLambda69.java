package org.telegram.messenger.voip;

import org.telegram.messenger.voip.NativeInstance;

public final /* synthetic */ class VoIPService$$ExternalSyntheticLambda69 implements NativeInstance.AudioLevelsCallback {
    public final /* synthetic */ VoIPService f$0;

    public /* synthetic */ VoIPService$$ExternalSyntheticLambda69(VoIPService voIPService) {
        this.f$0 = voIPService;
    }

    public final void run(int[] iArr, float[] fArr, boolean[] zArr) {
        this.f$0.lambda$initiateActualEncryptedCall$53(iArr, fArr, zArr);
    }
}
