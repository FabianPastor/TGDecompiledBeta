package org.telegram.ui;

import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.PhotoSize;

final /* synthetic */ class ChatEditActivity$$Lambda$13 implements Runnable {
    private final ChatEditActivity arg$1;
    private final InputFile arg$2;
    private final PhotoSize arg$3;
    private final PhotoSize arg$4;

    ChatEditActivity$$Lambda$13(ChatEditActivity chatEditActivity, InputFile inputFile, PhotoSize photoSize, PhotoSize photoSize2) {
        this.arg$1 = chatEditActivity;
        this.arg$2 = inputFile;
        this.arg$3 = photoSize;
        this.arg$4 = photoSize2;
    }

    public void run() {
        this.arg$1.lambda$didUploadPhoto$16$ChatEditActivity(this.arg$2, this.arg$3, this.arg$4);
    }
}
