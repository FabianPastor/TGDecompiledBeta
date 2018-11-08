package org.telegram.p005ui;

import org.telegram.p005ui.Components.ImageUpdater.ImageUpdaterDelegate;
import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_secureFile;

/* renamed from: org.telegram.ui.ProfileActivity$$Lambda$1 */
final /* synthetic */ class ProfileActivity$$Lambda$1 implements ImageUpdaterDelegate {
    private final ProfileActivity arg$1;

    ProfileActivity$$Lambda$1(ProfileActivity profileActivity) {
        this.arg$1 = profileActivity;
    }

    public void didUploadedPhoto(InputFile inputFile, PhotoSize photoSize, PhotoSize photoSize2, TL_secureFile tL_secureFile) {
        this.arg$1.lambda$onFragmentCreate$1$ProfileActivity(inputFile, photoSize, photoSize2, tL_secureFile);
    }
}
