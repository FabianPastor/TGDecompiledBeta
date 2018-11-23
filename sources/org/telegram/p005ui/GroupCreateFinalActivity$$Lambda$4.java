package org.telegram.p005ui;

import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.PhotoSize;

/* renamed from: org.telegram.ui.GroupCreateFinalActivity$$Lambda$4 */
final /* synthetic */ class GroupCreateFinalActivity$$Lambda$4 implements Runnable {
    private final GroupCreateFinalActivity arg$1;
    private final InputFile arg$2;
    private final PhotoSize arg$3;

    GroupCreateFinalActivity$$Lambda$4(GroupCreateFinalActivity groupCreateFinalActivity, InputFile inputFile, PhotoSize photoSize) {
        this.arg$1 = groupCreateFinalActivity;
        this.arg$2 = inputFile;
        this.arg$3 = photoSize;
    }

    public void run() {
        this.arg$1.lambda$didUploadedPhoto$5$GroupCreateFinalActivity(this.arg$2, this.arg$3);
    }
}
