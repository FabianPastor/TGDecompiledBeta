package org.telegram.ui;

import java.io.File;
import org.telegram.messenger.FileLoader;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class PhotoViewer$$ExternalSyntheticLambda68 implements FileLoader.FileResolver {
    public final /* synthetic */ PhotoViewer f$0;
    public final /* synthetic */ TLRPC.Message f$1;

    public /* synthetic */ PhotoViewer$$ExternalSyntheticLambda68(PhotoViewer photoViewer, TLRPC.Message message) {
        this.f$0 = photoViewer;
        this.f$1 = message;
    }

    public final File getFile() {
        return this.f$0.m4233lambda$checkProgress$68$orgtelegramuiPhotoViewer(this.f$1);
    }
}
