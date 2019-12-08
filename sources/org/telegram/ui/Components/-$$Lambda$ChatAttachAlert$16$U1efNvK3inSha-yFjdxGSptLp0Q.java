package org.telegram.ui.Components;

import org.telegram.messenger.camera.CameraController.VideoTakeCallback;
import org.telegram.ui.Components.ChatAttachAlert.AnonymousClass16;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatAttachAlert$16$U1efNvK3inSha-yFjdxGSptLp0Q implements VideoTakeCallback {
    private final /* synthetic */ AnonymousClass16 f$0;

    public /* synthetic */ -$$Lambda$ChatAttachAlert$16$U1efNvK3inSha-yFjdxGSptLp0Q(AnonymousClass16 anonymousClass16) {
        this.f$0 = anonymousClass16;
    }

    public final void onFinishVideoRecording(String str, long j) {
        this.f$0.lambda$shutterLongPressed$1$ChatAttachAlert$16(str, j);
    }
}
