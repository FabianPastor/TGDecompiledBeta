package org.telegram.messenger;

import java.io.File;
import org.telegram.messenger.ImageLoader.AnonymousClass3;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ImageLoader$3$5Bs2fzd8mEyFObZ9x2J38wn4r-c implements Runnable {
    private final /* synthetic */ AnonymousClass3 f$0;
    private final /* synthetic */ File f$1;
    private final /* synthetic */ String f$2;
    private final /* synthetic */ int f$3;
    private final /* synthetic */ int f$4;

    public /* synthetic */ -$$Lambda$ImageLoader$3$5Bs2fzd8mEyFObZ9x2J38wn4r-c(AnonymousClass3 anonymousClass3, File file, String str, int i, int i2) {
        this.f$0 = anonymousClass3;
        this.f$1 = file;
        this.f$2 = str;
        this.f$3 = i;
        this.f$4 = i2;
    }

    public final void run() {
        this.f$0.lambda$fileDidLoaded$5$ImageLoader$3(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
