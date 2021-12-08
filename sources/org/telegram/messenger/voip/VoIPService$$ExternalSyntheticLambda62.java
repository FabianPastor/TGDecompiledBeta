package org.telegram.messenger.voip;

import org.telegram.messenger.voip.Instance;

public final /* synthetic */ class VoIPService$$ExternalSyntheticLambda62 implements Instance.OnSignalingDataListener {
    public final /* synthetic */ VoIPService f$0;

    public /* synthetic */ VoIPService$$ExternalSyntheticLambda62(VoIPService voIPService) {
        this.f$0 = voIPService;
    }

    public final void onSignalingData(byte[] bArr) {
        this.f$0.onSignalingData(bArr);
    }
}
