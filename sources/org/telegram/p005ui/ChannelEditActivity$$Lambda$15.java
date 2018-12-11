package org.telegram.p005ui;

import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.PhotoSize;

/* renamed from: org.telegram.ui.ChannelEditActivity$$Lambda$15 */
final /* synthetic */ class ChannelEditActivity$$Lambda$15 implements Runnable {
    private final ChannelEditActivity arg$1;
    private final InputFile arg$2;
    private final PhotoSize arg$3;
    private final PhotoSize arg$4;

    ChannelEditActivity$$Lambda$15(ChannelEditActivity channelEditActivity, InputFile inputFile, PhotoSize photoSize, PhotoSize photoSize2) {
        this.arg$1 = channelEditActivity;
        this.arg$2 = inputFile;
        this.arg$3 = photoSize;
        this.arg$4 = photoSize2;
    }

    public void run() {
        this.arg$1.lambda$didUploadPhoto$19$ChannelEditActivity(this.arg$2, this.arg$3, this.arg$4);
    }
}
