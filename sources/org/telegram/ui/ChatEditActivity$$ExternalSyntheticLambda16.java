package org.telegram.ui;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ChatEditActivity$$ExternalSyntheticLambda16 implements Runnable {
    public final /* synthetic */ ChatEditActivity f$0;
    public final /* synthetic */ TLRPC.PhotoSize f$1;
    public final /* synthetic */ TLRPC.InputFile f$2;
    public final /* synthetic */ TLRPC.InputFile f$3;
    public final /* synthetic */ double f$4;
    public final /* synthetic */ String f$5;
    public final /* synthetic */ TLRPC.PhotoSize f$6;

    public /* synthetic */ ChatEditActivity$$ExternalSyntheticLambda16(ChatEditActivity chatEditActivity, TLRPC.PhotoSize photoSize, TLRPC.InputFile inputFile, TLRPC.InputFile inputFile2, double d, String str, TLRPC.PhotoSize photoSize2) {
        this.f$0 = chatEditActivity;
        this.f$1 = photoSize;
        this.f$2 = inputFile;
        this.f$3 = inputFile2;
        this.f$4 = d;
        this.f$5 = str;
        this.f$6 = photoSize2;
    }

    public final void run() {
        this.f$0.m1915lambda$didUploadPhoto$25$orgtelegramuiChatEditActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}
