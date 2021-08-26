package org.telegram.ui.Components;

import org.telegram.messenger.camera.CameraController;
import org.telegram.ui.Components.ChatAttachAlertPhotoLayout;

public final /* synthetic */ class ChatAttachAlertPhotoLayout$10$$ExternalSyntheticLambda3 implements CameraController.VideoTakeCallback {
    public final /* synthetic */ ChatAttachAlertPhotoLayout.AnonymousClass10 f$0;

    public /* synthetic */ ChatAttachAlertPhotoLayout$10$$ExternalSyntheticLambda3(ChatAttachAlertPhotoLayout.AnonymousClass10 r1) {
        this.f$0 = r1;
    }

    public final void onFinishVideoRecording(String str, long j) {
        this.f$0.lambda$shutterLongPressed$1(str, j);
    }
}
