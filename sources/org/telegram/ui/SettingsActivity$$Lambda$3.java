package org.telegram.ui;

import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.PhotoSize;

final /* synthetic */ class SettingsActivity$$Lambda$3 implements Runnable {
    private final SettingsActivity arg$1;
    private final InputFile arg$2;
    private final PhotoSize arg$3;
    private final PhotoSize arg$4;

    SettingsActivity$$Lambda$3(SettingsActivity settingsActivity, InputFile inputFile, PhotoSize photoSize, PhotoSize photoSize2) {
        this.arg$1 = settingsActivity;
        this.arg$2 = inputFile;
        this.arg$3 = photoSize;
        this.arg$4 = photoSize2;
    }

    public void run() {
        this.arg$1.lambda$didUploadPhoto$6$SettingsActivity(this.arg$2, this.arg$3, this.arg$4);
    }
}
