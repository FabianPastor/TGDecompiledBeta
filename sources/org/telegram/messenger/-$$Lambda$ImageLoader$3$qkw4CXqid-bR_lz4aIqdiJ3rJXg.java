package org.telegram.messenger;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ImageLoader$3$qkw4CXqid-bR_lz4aIqdiJ3rJXg implements Runnable {
    private final /* synthetic */ int f$0;
    private final /* synthetic */ String f$1;
    private final /* synthetic */ long f$2;
    private final /* synthetic */ long f$3;
    private final /* synthetic */ boolean f$4;

    public /* synthetic */ -$$Lambda$ImageLoader$3$qkw4CXqid-bR_lz4aIqdiJ3rJXg(int i, String str, long j, long j2, boolean z) {
        this.f$0 = i;
        this.f$1 = str;
        this.f$2 = j;
        this.f$3 = j2;
        this.f$4 = z;
    }

    public final void run() {
        NotificationCenter.getInstance(this.f$0).postNotificationName(NotificationCenter.FileUploadProgressChanged, this.f$1, Long.valueOf(this.f$2), Long.valueOf(this.f$3), Boolean.valueOf(this.f$4));
    }
}
