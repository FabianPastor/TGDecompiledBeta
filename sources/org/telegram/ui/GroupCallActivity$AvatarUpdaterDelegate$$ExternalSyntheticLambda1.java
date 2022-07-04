package org.telegram.ui;

import org.telegram.tgnet.TLRPC;
import org.telegram.ui.GroupCallActivity;

public final /* synthetic */ class GroupCallActivity$AvatarUpdaterDelegate$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ GroupCallActivity.AvatarUpdaterDelegate f$0;
    public final /* synthetic */ TLRPC.InputFile f$1;
    public final /* synthetic */ TLRPC.InputFile f$2;
    public final /* synthetic */ double f$3;
    public final /* synthetic */ String f$4;
    public final /* synthetic */ TLRPC.PhotoSize f$5;
    public final /* synthetic */ TLRPC.PhotoSize f$6;

    public /* synthetic */ GroupCallActivity$AvatarUpdaterDelegate$$ExternalSyntheticLambda1(GroupCallActivity.AvatarUpdaterDelegate avatarUpdaterDelegate, TLRPC.InputFile inputFile, TLRPC.InputFile inputFile2, double d, String str, TLRPC.PhotoSize photoSize, TLRPC.PhotoSize photoSize2) {
        this.f$0 = avatarUpdaterDelegate;
        this.f$1 = inputFile;
        this.f$2 = inputFile2;
        this.f$3 = d;
        this.f$4 = str;
        this.f$5 = photoSize;
        this.f$6 = photoSize2;
    }

    public final void run() {
        this.f$0.m3567xfd6CLASSNAMEa1(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}
