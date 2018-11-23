package org.telegram.p005ui;

import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.PhotoSize;

/* renamed from: org.telegram.ui.ChannelCreateActivity$$Lambda$8 */
final /* synthetic */ class ChannelCreateActivity$$Lambda$8 implements Runnable {
    private final ChannelCreateActivity arg$1;
    private final InputFile arg$2;
    private final PhotoSize arg$3;

    ChannelCreateActivity$$Lambda$8(ChannelCreateActivity channelCreateActivity, InputFile inputFile, PhotoSize photoSize) {
        this.arg$1 = channelCreateActivity;
        this.arg$2 = inputFile;
        this.arg$3 = photoSize;
    }

    public void run() {
        this.arg$1.lambda$didUploadedPhoto$11$ChannelCreateActivity(this.arg$2, this.arg$3);
    }
}
