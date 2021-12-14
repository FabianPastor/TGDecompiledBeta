package org.telegram.ui.Components;

import org.telegram.ui.Components.InstantCameraView;

public final /* synthetic */ class InstantCameraView$VideoRecorder$$ExternalSyntheticLambda4 implements Runnable {
    public final /* synthetic */ InstantCameraView.VideoRecorder f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ InstantCameraView$VideoRecorder$$ExternalSyntheticLambda4(InstantCameraView.VideoRecorder videoRecorder, int i) {
        this.f$0 = videoRecorder;
        this.f$1 = i;
    }

    public final void run() {
        this.f$0.lambda$handleStopRecording$4(this.f$1);
    }
}
