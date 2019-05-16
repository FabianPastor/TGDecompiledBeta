package org.telegram.messenger;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ImageLoader$pOb77-ep1O4qdDkpbIPheznVQgg implements Runnable {
    private final /* synthetic */ ImageLoader f$0;
    private final /* synthetic */ String f$1;

    public /* synthetic */ -$$Lambda$ImageLoader$pOb77-ep1O4qdDkpbIPheznVQgg(ImageLoader imageLoader, String str) {
        this.f$0 = imageLoader;
        this.f$1 = str;
    }

    public final void run() {
        this.f$0.lambda$cancelForceLoadingForImageReceiver$4$ImageLoader(this.f$1);
    }
}
