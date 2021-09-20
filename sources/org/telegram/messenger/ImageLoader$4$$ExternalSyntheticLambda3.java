package org.telegram.messenger;

public final /* synthetic */ class ImageLoader$4$$ExternalSyntheticLambda3 implements Runnable {
    public final /* synthetic */ int f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ boolean f$2;

    public /* synthetic */ ImageLoader$4$$ExternalSyntheticLambda3(int i, String str, boolean z) {
        this.f$0 = i;
        this.f$1 = str;
        this.f$2 = z;
    }

    public final void run() {
        NotificationCenter.getInstance(this.f$0).postNotificationName(NotificationCenter.fileUploadFailed, this.f$1, Boolean.valueOf(this.f$2));
    }
}
