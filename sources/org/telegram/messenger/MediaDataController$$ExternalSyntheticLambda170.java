package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_search;
import org.telegram.tgnet.TLRPC$User;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda170 implements RequestDelegate {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ TLRPC$User f$10;
    public final /* synthetic */ TLRPC$Chat f$11;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ boolean f$3;
    public final /* synthetic */ TLRPC$TL_messages_search f$4;
    public final /* synthetic */ long f$5;
    public final /* synthetic */ long f$6;
    public final /* synthetic */ int f$7;
    public final /* synthetic */ long f$8;
    public final /* synthetic */ int f$9;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda170(MediaDataController mediaDataController, String str, int i, boolean z, TLRPC$TL_messages_search tLRPC$TL_messages_search, long j, long j2, int i2, long j3, int i3, TLRPC$User tLRPC$User, TLRPC$Chat tLRPC$Chat) {
        this.f$0 = mediaDataController;
        this.f$1 = str;
        this.f$2 = i;
        this.f$3 = z;
        this.f$4 = tLRPC$TL_messages_search;
        this.f$5 = j;
        this.f$6 = j2;
        this.f$7 = i2;
        this.f$8 = j3;
        this.f$9 = i3;
        this.f$10 = tLRPC$User;
        this.f$11 = tLRPC$Chat;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$searchMessagesInChat$91(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11, tLObject, tLRPC$TL_error);
    }
}
