package org.telegram.messenger;

import java.io.File;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ImageLoader$k74dlCvVooO9jw6g30Qy8tUSaQg implements Runnable {
    private final /* synthetic */ ImageLoader f$0;
    private final /* synthetic */ String f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ File f$3;

    public /* synthetic */ -$$Lambda$ImageLoader$k74dlCvVooO9jw6g30Qy8tUSaQg(ImageLoader imageLoader, String str, int i, File file) {
        this.f$0 = imageLoader;
        this.f$1 = str;
        this.f$2 = i;
        this.f$3 = file;
    }

    public final void run() {
        this.f$0.lambda$fileDidLoaded$8$ImageLoader(this.f$1, this.f$2, this.f$3);
    }
}
