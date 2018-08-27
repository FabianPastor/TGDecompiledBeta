package org.telegram.messenger;

final /* synthetic */ class ImageLoader$2$$Lambda$0 implements Runnable {
    private final int arg$1;
    private final String arg$2;
    private final float arg$3;
    private final boolean arg$4;

    ImageLoader$2$$Lambda$0(int i, String str, float f, boolean z) {
        this.arg$1 = i;
        this.arg$2 = str;
        this.arg$3 = f;
        this.arg$4 = z;
    }

    public void run() {
        NotificationCenter.getInstance(this.arg$1).postNotificationName(NotificationCenter.FileUploadProgressChanged, this.arg$2, Float.valueOf(this.arg$3), Boolean.valueOf(this.arg$4));
    }
}
