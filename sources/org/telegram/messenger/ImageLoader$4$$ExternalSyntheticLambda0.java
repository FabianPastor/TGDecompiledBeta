package org.telegram.messenger;

public final /* synthetic */ class ImageLoader$4$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ int f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ long f$2;
    public final /* synthetic */ long f$3;

    public /* synthetic */ ImageLoader$4$$ExternalSyntheticLambda0(int i, String str, long j, long j2) {
        this.f$0 = i;
        this.f$1 = str;
        this.f$2 = j;
        this.f$3 = j2;
    }

    public final void run() {
        NotificationCenter.getInstance(this.f$0).postNotificationName(NotificationCenter.fileLoadProgressChanged, this.f$1, Long.valueOf(this.f$2), Long.valueOf(this.f$3));
    }
}
