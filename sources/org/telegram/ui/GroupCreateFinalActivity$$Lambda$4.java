package org.telegram.ui;

import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.PhotoSize;

final /* synthetic */ class GroupCreateFinalActivity$$Lambda$4 implements Runnable {
    private final GroupCreateFinalActivity arg$1;
    private final InputFile arg$2;
    private final PhotoSize arg$3;
    private final PhotoSize arg$4;

    GroupCreateFinalActivity$$Lambda$4(GroupCreateFinalActivity groupCreateFinalActivity, InputFile inputFile, PhotoSize photoSize, PhotoSize photoSize2) {
        this.arg$1 = groupCreateFinalActivity;
        this.arg$2 = inputFile;
        this.arg$3 = photoSize;
        this.arg$4 = photoSize2;
    }

    public void run() {
        this.arg$1.lambda$didUploadPhoto$5$GroupCreateFinalActivity(this.arg$2, this.arg$3, this.arg$4);
    }
}
