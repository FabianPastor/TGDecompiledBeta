package org.telegram.p005ui.Components;

import org.telegram.p005ui.Components.InstantCameraView.VideoRecorder;

/* renamed from: org.telegram.ui.Components.InstantCameraView$VideoRecorder$$Lambda$2 */
final /* synthetic */ class InstantCameraView$VideoRecorder$$Lambda$2 implements Runnable {
    private final VideoRecorder arg$1;
    private final int arg$2;

    InstantCameraView$VideoRecorder$$Lambda$2(VideoRecorder videoRecorder, int i) {
        this.arg$1 = videoRecorder;
        this.arg$2 = i;
    }

    public void run() {
        this.arg$1.lambda$handleStopRecording$2$InstantCameraView$VideoRecorder(this.arg$2);
    }
}
