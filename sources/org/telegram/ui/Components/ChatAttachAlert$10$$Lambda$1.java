package org.telegram.ui.Components;

import org.telegram.messenger.camera.CameraController.VideoTakeCallback;
import org.telegram.ui.Components.ChatAttachAlert.AnonymousClass10;

final /* synthetic */ class ChatAttachAlert$10$$Lambda$1 implements VideoTakeCallback {
    private final AnonymousClass10 arg$1;

    ChatAttachAlert$10$$Lambda$1(AnonymousClass10 anonymousClass10) {
        this.arg$1 = anonymousClass10;
    }

    public void onFinishVideoRecording(String str, long j) {
        this.arg$1.lambda$shutterLongPressed$1$ChatAttachAlert$10(str, j);
    }
}
