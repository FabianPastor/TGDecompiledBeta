package org.telegram.ui.Components;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$VideoSeekPreviewImage$Os3KazHQPmAT5faayq_HBEMpGLU implements Runnable {
    private final /* synthetic */ VideoSeekPreviewImage f$0;
    private final /* synthetic */ float f$1;
    private final /* synthetic */ long f$2;

    public /* synthetic */ -$$Lambda$VideoSeekPreviewImage$Os3KazHQPmAT5faayq_HBEMpGLU(VideoSeekPreviewImage videoSeekPreviewImage, float f, long j) {
        this.f$0 = videoSeekPreviewImage;
        this.f$1 = f;
        this.f$2 = j;
    }

    public final void run() {
        this.f$0.lambda$setProgress$1$VideoSeekPreviewImage(this.f$1, this.f$2);
    }
}
