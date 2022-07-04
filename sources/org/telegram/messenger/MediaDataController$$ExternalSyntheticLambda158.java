package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_search;
import org.telegram.tgnet.TLRPC$User;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda158 implements RequestDelegate {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ TLRPC$TL_messages_search f$2;
    public final /* synthetic */ long f$3;
    public final /* synthetic */ int f$4;
    public final /* synthetic */ int f$5;
    public final /* synthetic */ int f$6;
    public final /* synthetic */ TLRPC$User f$7;
    public final /* synthetic */ TLRPC$Chat f$8;
    public final /* synthetic */ boolean f$9;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda158(MediaDataController mediaDataController, long j, TLRPC$TL_messages_search tLRPC$TL_messages_search, long j2, int i, int i2, int i3, TLRPC$User tLRPC$User, TLRPC$Chat tLRPC$Chat, boolean z) {
        this.f$0 = mediaDataController;
        this.f$1 = j;
        this.f$2 = tLRPC$TL_messages_search;
        this.f$3 = j2;
        this.f$4 = i;
        this.f$5 = i2;
        this.f$6 = i3;
        this.f$7 = tLRPC$User;
        this.f$8 = tLRPC$Chat;
        this.f$9 = z;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$searchMessagesInChat$85(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, tLObject, tLRPC$TL_error);
    }
}
