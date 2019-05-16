package org.telegram.messenger;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ImageLoader$2$kx6bKZLf1Fl9Tfjq8OBUmPKsqGg implements Runnable {
    private final /* synthetic */ int f$0;
    private final /* synthetic */ String f$1;
    private final /* synthetic */ float f$2;
    private final /* synthetic */ boolean f$3;

    public /* synthetic */ -$$Lambda$ImageLoader$2$kx6bKZLf1Fl9Tfjq8OBUmPKsqGg(int i, String str, float f, boolean z) {
        this.f$0 = i;
        this.f$1 = str;
        this.f$2 = f;
        this.f$3 = z;
    }

    public final void run() {
        NotificationCenter.getInstance(this.f$0).postNotificationName(NotificationCenter.FileUploadProgressChanged, this.f$1, Float.valueOf(this.f$2), Boolean.valueOf(this.f$3));
    }
}
