package org.telegram.ui.Components;

import org.telegram.messenger.camera.CameraController.VideoTakeCallback;
import org.telegram.ui.Components.ChatAttachAlert.AnonymousClass17;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatAttachAlert$17$iW4W4z9AioXMZkciLqiTLvcHdXo implements VideoTakeCallback {
    private final /* synthetic */ AnonymousClass17 f$0;

    public /* synthetic */ -$$Lambda$ChatAttachAlert$17$iW4W4z9AioXMZkciLqiTLvcHdXo(AnonymousClass17 anonymousClass17) {
        this.f$0 = anonymousClass17;
    }

    public final void onFinishVideoRecording(String str, long j) {
        this.f$0.lambda$shutterLongPressed$1$ChatAttachAlert$17(str, j);
    }
}
