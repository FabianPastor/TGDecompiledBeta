package org.telegram.messenger;

import j$.util.function.Consumer;
import java.io.File;
import java.nio.file.Path;

public final /* synthetic */ class ImageLoader$$ExternalSyntheticLambda13 implements Consumer {
    public final /* synthetic */ File f$0;

    public /* synthetic */ ImageLoader$$ExternalSyntheticLambda13(File file) {
        this.f$0 = file;
    }

    public final void accept(Object obj) {
        ImageLoader.lambda$moveDirectory$2(this.f$0, (Path) obj);
    }

    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return Consumer.CC.$default$andThen(this, consumer);
    }
}
