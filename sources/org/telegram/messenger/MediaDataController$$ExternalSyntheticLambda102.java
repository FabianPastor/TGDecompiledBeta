package org.telegram.messenger;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda102 implements Runnable {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ boolean f$10;
    public final /* synthetic */ TLObject f$2;
    public final /* synthetic */ TLRPC.TL_messages_search f$3;
    public final /* synthetic */ long f$4;
    public final /* synthetic */ int f$5;
    public final /* synthetic */ int f$6;
    public final /* synthetic */ int f$7;
    public final /* synthetic */ TLRPC.User f$8;
    public final /* synthetic */ TLRPC.Chat f$9;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda102(MediaDataController mediaDataController, long j, TLObject tLObject, TLRPC.TL_messages_search tL_messages_search, long j2, int i, int i2, int i3, TLRPC.User user, TLRPC.Chat chat, boolean z) {
        this.f$0 = mediaDataController;
        this.f$1 = j;
        this.f$2 = tLObject;
        this.f$3 = tL_messages_search;
        this.f$4 = j2;
        this.f$5 = i;
        this.f$6 = i2;
        this.f$7 = i3;
        this.f$8 = user;
        this.f$9 = chat;
        this.f$10 = z;
    }

    public final void run() {
        this.f$0.m827x9var_bd1c(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10);
    }
}
