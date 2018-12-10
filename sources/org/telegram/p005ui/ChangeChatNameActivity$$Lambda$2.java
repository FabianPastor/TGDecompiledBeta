package org.telegram.p005ui;

import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.PhotoSize;

/* renamed from: org.telegram.ui.ChangeChatNameActivity$$Lambda$2 */
final /* synthetic */ class ChangeChatNameActivity$$Lambda$2 implements Runnable {
    private final ChangeChatNameActivity arg$1;
    private final InputFile arg$2;
    private final PhotoSize arg$3;
    private final PhotoSize arg$4;

    ChangeChatNameActivity$$Lambda$2(ChangeChatNameActivity changeChatNameActivity, InputFile inputFile, PhotoSize photoSize, PhotoSize photoSize2) {
        this.arg$1 = changeChatNameActivity;
        this.arg$2 = inputFile;
        this.arg$3 = photoSize;
        this.arg$4 = photoSize2;
    }

    public void run() {
        this.arg$1.lambda$didUploadPhoto$3$ChangeChatNameActivity(this.arg$2, this.arg$3, this.arg$4);
    }
}
