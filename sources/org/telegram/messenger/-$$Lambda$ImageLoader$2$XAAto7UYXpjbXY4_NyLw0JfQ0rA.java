package org.telegram.messenger;

import java.io.File;
import org.telegram.messenger.ImageLoader.AnonymousClass2;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ImageLoader$2$XAAto7UYXpjbXY4_NyLw0JfQ0rA implements Runnable {
    private final /* synthetic */ AnonymousClass2 f$0;
    private final /* synthetic */ File f$1;
    private final /* synthetic */ String f$2;
    private final /* synthetic */ int f$3;
    private final /* synthetic */ int f$4;

    public /* synthetic */ -$$Lambda$ImageLoader$2$XAAto7UYXpjbXY4_NyLw0JfQ0rA(AnonymousClass2 anonymousClass2, File file, String str, int i, int i2) {
        this.f$0 = anonymousClass2;
        this.f$1 = file;
        this.f$2 = str;
        this.f$3 = i;
        this.f$4 = i2;
    }

    public final void run() {
        this.f$0.lambda$fileDidLoaded$5$ImageLoader$2(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
