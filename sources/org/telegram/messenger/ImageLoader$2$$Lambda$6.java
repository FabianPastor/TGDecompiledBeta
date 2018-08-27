package org.telegram.messenger;

final /* synthetic */ class ImageLoader$2$$Lambda$6 implements Runnable {
    private final int arg$1;
    private final String arg$2;
    private final boolean arg$3;

    ImageLoader$2$$Lambda$6(int i, String str, boolean z) {
        this.arg$1 = i;
        this.arg$2 = str;
        this.arg$3 = z;
    }

    public void run() {
        NotificationCenter.getInstance(this.arg$1).postNotificationName(NotificationCenter.FileDidFailUpload, this.arg$2, Boolean.valueOf(this.arg$3));
    }
}
