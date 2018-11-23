package org.telegram.p005ui;

import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.PhotoSize;

/* renamed from: org.telegram.ui.ChannelEditActivity$$Lambda$15 */
final /* synthetic */ class ChannelEditActivity$$Lambda$15 implements Runnable {
    private final ChannelEditActivity arg$1;
    private final InputFile arg$2;
    private final PhotoSize arg$3;

    ChannelEditActivity$$Lambda$15(ChannelEditActivity channelEditActivity, InputFile inputFile, PhotoSize photoSize) {
        this.arg$1 = channelEditActivity;
        this.arg$2 = inputFile;
        this.arg$3 = photoSize;
    }

    public void run() {
        this.arg$1.lambda$didUploadedPhoto$19$ChannelEditActivity(this.arg$2, this.arg$3);
    }
}
