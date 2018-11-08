package org.telegram.p005ui.Components;

import org.telegram.messenger.camera.CameraController.VideoTakeCallback;
import org.telegram.p005ui.Components.ChatAttachAlert.C122510;

/* renamed from: org.telegram.ui.Components.ChatAttachAlert$10$$Lambda$1 */
final /* synthetic */ class ChatAttachAlert$10$$Lambda$1 implements VideoTakeCallback {
    private final C122510 arg$1;

    ChatAttachAlert$10$$Lambda$1(C122510 c122510) {
        this.arg$1 = c122510;
    }

    public void onFinishVideoRecording(String str, long j) {
        this.arg$1.lambda$shutterLongPressed$1$ChatAttachAlert$10(str, j);
    }
}
