package org.telegram.p005ui.Components;

import org.telegram.messenger.camera.CameraController.VideoTakeCallback;
import org.telegram.p005ui.Components.ChatAttachAlert.CLASSNAME;

/* renamed from: org.telegram.ui.Components.ChatAttachAlert$10$$Lambda$1 */
final /* synthetic */ class ChatAttachAlert$10$$Lambda$1 implements VideoTakeCallback {
    private final CLASSNAME arg$1;

    ChatAttachAlert$10$$Lambda$1(CLASSNAME CLASSNAME) {
        this.arg$1 = CLASSNAME;
    }

    public void onFinishVideoRecording(String str, long j) {
        this.arg$1.lambda$shutterLongPressed$1$ChatAttachAlert$10(str, j);
    }
}
