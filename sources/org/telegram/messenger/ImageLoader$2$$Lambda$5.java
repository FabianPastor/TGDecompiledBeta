package org.telegram.messenger;

final /* synthetic */ class ImageLoader$2$$Lambda$5 implements Runnable {
    private final int arg$1;
    private final String arg$2;
    private final float arg$3;

    ImageLoader$2$$Lambda$5(int i, String str, float f) {
        this.arg$1 = i;
        this.arg$2 = str;
        this.arg$3 = f;
    }

    public void run() {
        NotificationCenter.getInstance(this.arg$1).postNotificationName(NotificationCenter.FileLoadProgressChanged, this.arg$2, Float.valueOf(this.arg$3));
    }
}
