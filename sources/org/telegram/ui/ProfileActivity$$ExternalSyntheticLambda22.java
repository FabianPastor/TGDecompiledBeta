package org.telegram.ui;

import org.telegram.tgnet.TLRPC$InputFile;
import org.telegram.tgnet.TLRPC$PhotoSize;

public final /* synthetic */ class ProfileActivity$$ExternalSyntheticLambda22 implements Runnable {
    public final /* synthetic */ ProfileActivity f$0;
    public final /* synthetic */ TLRPC$InputFile f$1;
    public final /* synthetic */ TLRPC$InputFile f$2;
    public final /* synthetic */ double f$3;
    public final /* synthetic */ String f$4;
    public final /* synthetic */ TLRPC$PhotoSize f$5;
    public final /* synthetic */ TLRPC$PhotoSize f$6;

    public /* synthetic */ ProfileActivity$$ExternalSyntheticLambda22(ProfileActivity profileActivity, TLRPC$InputFile tLRPC$InputFile, TLRPC$InputFile tLRPC$InputFile2, double d, String str, TLRPC$PhotoSize tLRPC$PhotoSize, TLRPC$PhotoSize tLRPC$PhotoSize2) {
        this.f$0 = profileActivity;
        this.f$1 = tLRPC$InputFile;
        this.f$2 = tLRPC$InputFile2;
        this.f$3 = d;
        this.f$4 = str;
        this.f$5 = tLRPC$PhotoSize;
        this.f$6 = tLRPC$PhotoSize2;
    }

    public final void run() {
        this.f$0.lambda$didUploadPhoto$33(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}
