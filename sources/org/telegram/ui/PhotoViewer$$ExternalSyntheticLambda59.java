package org.telegram.ui;

import java.io.File;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.MessageObject;

public final /* synthetic */ class PhotoViewer$$ExternalSyntheticLambda59 implements Runnable {
    public final /* synthetic */ PhotoViewer f$0;
    public final /* synthetic */ File f$1;
    public final /* synthetic */ File f$2;
    public final /* synthetic */ FileLoader.FileResolver f$3;
    public final /* synthetic */ int f$4;
    public final /* synthetic */ MessageObject f$5;
    public final /* synthetic */ boolean f$6;
    public final /* synthetic */ boolean f$7;
    public final /* synthetic */ boolean f$8;
    public final /* synthetic */ boolean f$9;

    public /* synthetic */ PhotoViewer$$ExternalSyntheticLambda59(PhotoViewer photoViewer, File file, File file2, FileLoader.FileResolver fileResolver, int i, MessageObject messageObject, boolean z, boolean z2, boolean z3, boolean z4) {
        this.f$0 = photoViewer;
        this.f$1 = file;
        this.f$2 = file2;
        this.f$3 = fileResolver;
        this.f$4 = i;
        this.f$5 = messageObject;
        this.f$6 = z;
        this.f$7 = z2;
        this.f$8 = z3;
        this.f$9 = z4;
    }

    public final void run() {
        this.f$0.lambda$checkProgress$69(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9);
    }
}
