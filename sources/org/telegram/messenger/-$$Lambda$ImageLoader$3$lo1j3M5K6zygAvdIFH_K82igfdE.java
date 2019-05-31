package org.telegram.messenger;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ImageLoader$3$lo1j3M5K6zygAvdIFH_K82igfdE implements Runnable {
    private final /* synthetic */ int f$0;
    private final /* synthetic */ String f$1;
    private final /* synthetic */ boolean f$2;

    public /* synthetic */ -$$Lambda$ImageLoader$3$lo1j3M5K6zygAvdIFH_K82igfdE(int i, String str, boolean z) {
        this.f$0 = i;
        this.f$1 = str;
        this.f$2 = z;
    }

    public final void run() {
        NotificationCenter.getInstance(this.f$0).postNotificationName(NotificationCenter.FileDidFailUpload, this.f$1, Boolean.valueOf(this.f$2));
    }
}
