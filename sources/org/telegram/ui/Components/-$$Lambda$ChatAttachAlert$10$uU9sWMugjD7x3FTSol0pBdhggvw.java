package org.telegram.ui.Components;

import org.telegram.messenger.camera.CameraController.VideoTakeCallback;
import org.telegram.ui.Components.ChatAttachAlert.AnonymousClass10;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatAttachAlert$10$uU9sWMugjD7x3FTSol0pBdhggvw implements VideoTakeCallback {
    private final /* synthetic */ AnonymousClass10 f$0;

    public /* synthetic */ -$$Lambda$ChatAttachAlert$10$uU9sWMugjD7x3FTSol0pBdhggvw(AnonymousClass10 anonymousClass10) {
        this.f$0 = anonymousClass10;
    }

    public final void onFinishVideoRecording(String str, long j) {
        this.f$0.lambda$shutterLongPressed$1$ChatAttachAlert$10(str, j);
    }
}
