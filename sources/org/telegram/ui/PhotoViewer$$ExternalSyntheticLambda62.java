package org.telegram.ui;

import java.io.File;
import org.telegram.messenger.MessageObject;

public final /* synthetic */ class PhotoViewer$$ExternalSyntheticLambda62 implements Runnable {
    public final /* synthetic */ PhotoViewer f$0;
    public final /* synthetic */ File f$1;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ MessageObject f$3;
    public final /* synthetic */ boolean f$4;

    public /* synthetic */ PhotoViewer$$ExternalSyntheticLambda62(PhotoViewer photoViewer, File file, boolean z, MessageObject messageObject, boolean z2) {
        this.f$0 = photoViewer;
        this.f$1 = file;
        this.f$2 = z;
        this.f$3 = messageObject;
        this.f$4 = z2;
    }

    public final void run() {
        this.f$0.lambda$openCurrentPhotoInPaintModeForSelect$72(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
