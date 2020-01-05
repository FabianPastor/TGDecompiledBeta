package org.telegram.ui;

import java.io.File;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PhotoViewer$H2PHjD-wDvar_PO1pgdyyIyx1aoE implements Runnable {
    private final /* synthetic */ PhotoViewer f$0;
    private final /* synthetic */ File f$1;
    private final /* synthetic */ File f$2;
    private final /* synthetic */ boolean f$3;
    private final /* synthetic */ boolean f$4;
    private final /* synthetic */ int f$5;
    private final /* synthetic */ boolean f$6;

    public /* synthetic */ -$$Lambda$PhotoViewer$H2PHjD-wDvar_PO1pgdyyIyx1aoE(PhotoViewer photoViewer, File file, File file2, boolean z, boolean z2, int i, boolean z3) {
        this.f$0 = photoViewer;
        this.f$1 = file;
        this.f$2 = file2;
        this.f$3 = z;
        this.f$4 = z2;
        this.f$5 = i;
        this.f$6 = z3;
    }

    public final void run() {
        this.f$0.lambda$checkProgress$45$PhotoViewer(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}
