package org.telegram.messenger;

import org.telegram.tgnet.TLObject;

final /* synthetic */ class ImageLoader$$Lambda$4 implements Runnable {
    private final ImageLoader arg$1;
    private final MessageObject arg$10;
    private final TLObject arg$11;
    private final boolean arg$12;
    private final int arg$13;
    private final int arg$14;
    private final String arg$15;
    private final int arg$16;
    private final int arg$2;
    private final String arg$3;
    private final String arg$4;
    private final int arg$5;
    private final ImageReceiver arg$6;
    private final String arg$7;
    private final String arg$8;
    private final boolean arg$9;

    ImageLoader$$Lambda$4(ImageLoader imageLoader, int i, String str, String str2, int i2, ImageReceiver imageReceiver, String str3, String str4, boolean z, MessageObject messageObject, TLObject tLObject, boolean z2, int i3, int i4, String str5, int i5) {
        this.arg$1 = imageLoader;
        this.arg$2 = i;
        this.arg$3 = str;
        this.arg$4 = str2;
        this.arg$5 = i2;
        this.arg$6 = imageReceiver;
        this.arg$7 = str3;
        this.arg$8 = str4;
        this.arg$9 = z;
        this.arg$10 = messageObject;
        this.arg$11 = tLObject;
        this.arg$12 = z2;
        this.arg$13 = i3;
        this.arg$14 = i4;
        this.arg$15 = str5;
        this.arg$16 = i5;
    }

    public void run() {
        this.arg$1.lambda$createLoadOperationForImageReceiver$5$ImageLoader(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7, this.arg$8, this.arg$9, this.arg$10, this.arg$11, this.arg$12, this.arg$13, this.arg$14, this.arg$15, this.arg$16);
    }
}
