package org.telegram.ui;

import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_secureFile;
import org.telegram.ui.Components.ImageUpdater.ImageUpdaterDelegate;

final /* synthetic */ class SettingsActivity$$Lambda$0 implements ImageUpdaterDelegate {
    private final SettingsActivity arg$1;

    SettingsActivity$$Lambda$0(SettingsActivity settingsActivity) {
        this.arg$1 = settingsActivity;
    }

    public void didUploadedPhoto(InputFile inputFile, PhotoSize photoSize, PhotoSize photoSize2, TL_secureFile tL_secureFile) {
        this.arg$1.lambda$onFragmentCreate$2$SettingsActivity(inputFile, photoSize, photoSize2, tL_secureFile);
    }
}
