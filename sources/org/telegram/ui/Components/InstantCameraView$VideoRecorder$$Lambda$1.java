package org.telegram.ui.Components;

final /* synthetic */ class InstantCameraView$VideoRecorder$$Lambda$1 implements Runnable {
    private final VideoRecorder arg$1;

    InstantCameraView$VideoRecorder$$Lambda$1(VideoRecorder videoRecorder) {
        this.arg$1 = videoRecorder;
    }

    public void run() {
        this.arg$1.lambda$handleVideoFrameAvailable$1$InstantCameraView$VideoRecorder();
    }
}
