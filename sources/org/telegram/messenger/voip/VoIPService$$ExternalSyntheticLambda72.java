package org.telegram.messenger.voip;

import org.telegram.messenger.voip.NativeInstance;

public final /* synthetic */ class VoIPService$$ExternalSyntheticLambda72 implements NativeInstance.AudioLevelsCallback {
    public final /* synthetic */ VoIPService f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ VoIPService$$ExternalSyntheticLambda72(VoIPService voIPService, int i) {
        this.f$0 = voIPService;
        this.f$1 = i;
    }

    public final void run(int[] iArr, float[] fArr, boolean[] zArr) {
        this.f$0.lambda$createGroupInstance$38(this.f$1, iArr, fArr, zArr);
    }
}
