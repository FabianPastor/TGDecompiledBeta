package org.telegram.ui;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ChannelCreateActivity$$ExternalSyntheticLambda5 implements Runnable {
    public final /* synthetic */ ChannelCreateActivity f$0;
    public final /* synthetic */ TLRPC.InputFile f$1;
    public final /* synthetic */ TLRPC.InputFile f$2;
    public final /* synthetic */ String f$3;
    public final /* synthetic */ double f$4;
    public final /* synthetic */ TLRPC.PhotoSize f$5;
    public final /* synthetic */ TLRPC.PhotoSize f$6;

    public /* synthetic */ ChannelCreateActivity$$ExternalSyntheticLambda5(ChannelCreateActivity channelCreateActivity, TLRPC.InputFile inputFile, TLRPC.InputFile inputFile2, String str, double d, TLRPC.PhotoSize photoSize, TLRPC.PhotoSize photoSize2) {
        this.f$0 = channelCreateActivity;
        this.f$1 = inputFile;
        this.f$2 = inputFile2;
        this.f$3 = str;
        this.f$4 = d;
        this.f$5 = photoSize;
        this.f$6 = photoSize2;
    }

    public final void run() {
        this.f$0.m1608lambda$didUploadPhoto$12$orgtelegramuiChannelCreateActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}
