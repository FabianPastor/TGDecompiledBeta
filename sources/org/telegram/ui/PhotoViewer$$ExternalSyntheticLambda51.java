package org.telegram.ui;

import java.io.File;

public final /* synthetic */ class PhotoViewer$$ExternalSyntheticLambda51 implements Runnable {
    public final /* synthetic */ PhotoViewer f$0;
    public final /* synthetic */ File f$1;
    public final /* synthetic */ File f$2;
    public final /* synthetic */ boolean f$3;
    public final /* synthetic */ boolean f$4;
    public final /* synthetic */ int f$5;
    public final /* synthetic */ boolean f$6;
    public final /* synthetic */ boolean f$7;
    public final /* synthetic */ boolean f$8;

    public /* synthetic */ PhotoViewer$$ExternalSyntheticLambda51(PhotoViewer photoViewer, File file, File file2, boolean z, boolean z2, int i, boolean z3, boolean z4, boolean z5) {
        this.f$0 = photoViewer;
        this.f$1 = file;
        this.f$2 = file2;
        this.f$3 = z;
        this.f$4 = z2;
        this.f$5 = i;
        this.f$6 = z3;
        this.f$7 = z4;
        this.f$8 = z5;
    }

    public final void run() {
        this.f$0.lambda$checkProgress$59(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8);
    }
}