package org.telegram.p005ui.Components;

import org.telegram.p005ui.Components.InstantCameraView.VideoRecorder;

/* renamed from: org.telegram.ui.Components.InstantCameraView$VideoRecorder$$Lambda$3 */
final /* synthetic */ class InstantCameraView$VideoRecorder$$Lambda$3 implements Runnable {
    private final VideoRecorder arg$1;

    InstantCameraView$VideoRecorder$$Lambda$3(VideoRecorder videoRecorder) {
        this.arg$1 = videoRecorder;
    }

    public void run() {
        this.arg$1.lambda$prepareEncoder$3$InstantCameraView$VideoRecorder();
    }
}
