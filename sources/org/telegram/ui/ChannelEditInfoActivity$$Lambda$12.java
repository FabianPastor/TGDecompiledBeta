package org.telegram.ui;

import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.PhotoSize;

final /* synthetic */ class ChannelEditInfoActivity$$Lambda$12 implements Runnable {
    private final ChannelEditInfoActivity arg$1;
    private final InputFile arg$2;
    private final PhotoSize arg$3;

    ChannelEditInfoActivity$$Lambda$12(ChannelEditInfoActivity channelEditInfoActivity, InputFile inputFile, PhotoSize photoSize) {
        this.arg$1 = channelEditInfoActivity;
        this.arg$2 = inputFile;
        this.arg$3 = photoSize;
    }

    public void run() {
        this.arg$1.lambda$didUploadedPhoto$15$ChannelEditInfoActivity(this.arg$2, this.arg$3);
    }
}
