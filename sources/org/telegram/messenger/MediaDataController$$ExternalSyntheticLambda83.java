package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda83 implements Runnable {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ int f$10;
    public final /* synthetic */ TLRPC.User f$11;
    public final /* synthetic */ TLRPC.Chat f$12;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ TLObject f$3;
    public final /* synthetic */ TLRPC.TL_messages_search f$4;
    public final /* synthetic */ long f$5;
    public final /* synthetic */ long f$6;
    public final /* synthetic */ int f$7;
    public final /* synthetic */ ArrayList f$8;
    public final /* synthetic */ long f$9;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda83(MediaDataController mediaDataController, int i, boolean z, TLObject tLObject, TLRPC.TL_messages_search tL_messages_search, long j, long j2, int i2, ArrayList arrayList, long j3, int i3, TLRPC.User user, TLRPC.Chat chat) {
        this.f$0 = mediaDataController;
        this.f$1 = i;
        this.f$2 = z;
        this.f$3 = tLObject;
        this.f$4 = tL_messages_search;
        this.f$5 = j;
        this.f$6 = j2;
        this.f$7 = i2;
        this.f$8 = arrayList;
        this.f$9 = j3;
        this.f$10 = i3;
        this.f$11 = user;
        this.f$12 = chat;
    }

    public final void run() {
        this.f$0.m829x24ef8fda(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11, this.f$12);
    }
}
