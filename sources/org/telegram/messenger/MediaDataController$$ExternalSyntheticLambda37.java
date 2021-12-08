package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda37 implements RequestDelegate {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ TLRPC.TL_messages_search f$2;
    public final /* synthetic */ long f$3;
    public final /* synthetic */ int f$4;
    public final /* synthetic */ int f$5;
    public final /* synthetic */ int f$6;
    public final /* synthetic */ TLRPC.User f$7;
    public final /* synthetic */ TLRPC.Chat f$8;
    public final /* synthetic */ boolean f$9;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda37(MediaDataController mediaDataController, long j, TLRPC.TL_messages_search tL_messages_search, long j2, int i, int i2, int i3, TLRPC.User user, TLRPC.Chat chat, boolean z) {
        this.f$0 = mediaDataController;
        this.f$1 = j;
        this.f$2 = tL_messages_search;
        this.f$3 = j2;
        this.f$4 = i;
        this.f$5 = i2;
        this.f$6 = i3;
        this.f$7 = user;
        this.f$8 = chat;
        this.f$9 = z;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m892x8a4ecd59(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, tLObject, tL_error);
    }
}
