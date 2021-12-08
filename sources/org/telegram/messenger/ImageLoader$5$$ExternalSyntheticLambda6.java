package org.telegram.messenger;

import java.io.File;
import org.telegram.messenger.ImageLoader;

public final /* synthetic */ class ImageLoader$5$$ExternalSyntheticLambda6 implements Runnable {
    public final /* synthetic */ ImageLoader.AnonymousClass5 f$0;
    public final /* synthetic */ File f$1;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ int f$4;

    public /* synthetic */ ImageLoader$5$$ExternalSyntheticLambda6(ImageLoader.AnonymousClass5 r1, File file, String str, int i, int i2) {
        this.f$0 = r1;
        this.f$1 = file;
        this.f$2 = str;
        this.f$3 = i;
        this.f$4 = i2;
    }

    public final void run() {
        this.f$0.m699lambda$fileDidLoaded$5$orgtelegrammessengerImageLoader$5(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
