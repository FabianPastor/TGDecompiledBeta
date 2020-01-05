package org.telegram.messenger;

import java.io.File;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaController$rEPWbo0gmy3DDvtD04KagC-xojo implements Runnable {
    private final /* synthetic */ MediaController f$0;
    private final /* synthetic */ boolean f$1;
    private final /* synthetic */ boolean f$2;
    private final /* synthetic */ VideoConvertMessage f$3;
    private final /* synthetic */ File f$4;
    private final /* synthetic */ float f$5;
    private final /* synthetic */ boolean f$6;
    private final /* synthetic */ long f$7;

    public /* synthetic */ -$$Lambda$MediaController$rEPWbo0gmy3DDvtD04KagC-xojo(MediaController mediaController, boolean z, boolean z2, VideoConvertMessage videoConvertMessage, File file, float f, boolean z3, long j) {
        this.f$0 = mediaController;
        this.f$1 = z;
        this.f$2 = z2;
        this.f$3 = videoConvertMessage;
        this.f$4 = file;
        this.f$5 = f;
        this.f$6 = z3;
        this.f$7 = j;
    }

    public final void run() {
        this.f$0.lambda$didWriteData$31$MediaController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
    }
}
