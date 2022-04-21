package org.telegram.messenger.voip;

import org.telegram.messenger.voip.NativeInstance;

public final /* synthetic */ class VoIPService$$ExternalSyntheticLambda68 implements NativeInstance.AudioLevelsCallback {
    public final /* synthetic */ VoIPService f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ VoIPService$$ExternalSyntheticLambda68(VoIPService voIPService, int i) {
        this.f$0 = voIPService;
        this.f$1 = i;
    }

    public final void run(int[] iArr, float[] fArr, boolean[] zArr) {
        this.f$0.m1161x1906d24f(this.f$1, iArr, fArr, zArr);
    }
}
