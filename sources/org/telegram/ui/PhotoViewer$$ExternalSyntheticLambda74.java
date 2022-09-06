package org.telegram.ui;

import java.io.File;
import org.telegram.messenger.FileLoader;
import org.telegram.tgnet.TLObject;

public final /* synthetic */ class PhotoViewer$$ExternalSyntheticLambda74 implements FileLoader.FileResolver {
    public final /* synthetic */ PhotoViewer f$0;
    public final /* synthetic */ TLObject f$1;

    public /* synthetic */ PhotoViewer$$ExternalSyntheticLambda74(PhotoViewer photoViewer, TLObject tLObject) {
        this.f$0 = photoViewer;
        this.f$1 = tLObject;
    }

    public final File getFile() {
        return this.f$0.lambda$checkProgress$68(this.f$1);
    }
}
