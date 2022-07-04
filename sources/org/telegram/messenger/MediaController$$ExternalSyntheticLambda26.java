package org.telegram.messenger;

import java.io.File;
import org.telegram.messenger.MediaController;

public final /* synthetic */ class MediaController$$ExternalSyntheticLambda26 implements Runnable {
    public final /* synthetic */ MediaController f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ MediaController.VideoConvertMessage f$3;
    public final /* synthetic */ File f$4;
    public final /* synthetic */ float f$5;
    public final /* synthetic */ long f$6;
    public final /* synthetic */ boolean f$7;
    public final /* synthetic */ long f$8;

    public /* synthetic */ MediaController$$ExternalSyntheticLambda26(MediaController mediaController, boolean z, boolean z2, MediaController.VideoConvertMessage videoConvertMessage, File file, float f, long j, boolean z3, long j2) {
        this.f$0 = mediaController;
        this.f$1 = z;
        this.f$2 = z2;
        this.f$3 = videoConvertMessage;
        this.f$4 = file;
        this.f$5 = f;
        this.f$6 = j;
        this.f$7 = z3;
        this.f$8 = j2;
    }

    public final void run() {
        this.f$0.m88lambda$didWriteData$42$orgtelegrammessengerMediaController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8);
    }
}
