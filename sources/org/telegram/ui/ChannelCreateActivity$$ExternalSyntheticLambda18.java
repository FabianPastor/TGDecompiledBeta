package org.telegram.ui;

import org.telegram.tgnet.TLRPC$InputFile;
import org.telegram.tgnet.TLRPC$PhotoSize;

public final /* synthetic */ class ChannelCreateActivity$$ExternalSyntheticLambda18 implements Runnable {
    public final /* synthetic */ ChannelCreateActivity f$0;
    public final /* synthetic */ TLRPC$InputFile f$1;
    public final /* synthetic */ TLRPC$InputFile f$2;
    public final /* synthetic */ String f$3;
    public final /* synthetic */ double f$4;
    public final /* synthetic */ TLRPC$PhotoSize f$5;
    public final /* synthetic */ TLRPC$PhotoSize f$6;

    public /* synthetic */ ChannelCreateActivity$$ExternalSyntheticLambda18(ChannelCreateActivity channelCreateActivity, TLRPC$InputFile tLRPC$InputFile, TLRPC$InputFile tLRPC$InputFile2, String str, double d, TLRPC$PhotoSize tLRPC$PhotoSize, TLRPC$PhotoSize tLRPC$PhotoSize2) {
        this.f$0 = channelCreateActivity;
        this.f$1 = tLRPC$InputFile;
        this.f$2 = tLRPC$InputFile2;
        this.f$3 = str;
        this.f$4 = d;
        this.f$5 = tLRPC$PhotoSize;
        this.f$6 = tLRPC$PhotoSize2;
    }

    public final void run() {
        this.f$0.lambda$didUploadPhoto$15(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}
