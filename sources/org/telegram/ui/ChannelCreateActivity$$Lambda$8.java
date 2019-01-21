package org.telegram.ui;

import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.PhotoSize;

final /* synthetic */ class ChannelCreateActivity$$Lambda$8 implements Runnable {
    private final ChannelCreateActivity arg$1;
    private final InputFile arg$2;
    private final PhotoSize arg$3;
    private final PhotoSize arg$4;

    ChannelCreateActivity$$Lambda$8(ChannelCreateActivity channelCreateActivity, InputFile inputFile, PhotoSize photoSize, PhotoSize photoSize2) {
        this.arg$1 = channelCreateActivity;
        this.arg$2 = inputFile;
        this.arg$3 = photoSize;
        this.arg$4 = photoSize2;
    }

    public void run() {
        this.arg$1.lambda$didUploadPhoto$11$ChannelCreateActivity(this.arg$2, this.arg$3, this.arg$4);
    }
}
