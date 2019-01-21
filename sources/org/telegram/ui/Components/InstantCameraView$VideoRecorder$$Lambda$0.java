package org.telegram.ui.Components;

final /* synthetic */ class InstantCameraView$VideoRecorder$$Lambda$0 implements Runnable {
    private final VideoRecorder arg$1;

    InstantCameraView$VideoRecorder$$Lambda$0(VideoRecorder videoRecorder) {
        this.arg$1 = videoRecorder;
    }

    public void run() {
        this.arg$1.lambda$handleVideoFrameAvailable$0$InstantCameraView$VideoRecorder();
    }
}
