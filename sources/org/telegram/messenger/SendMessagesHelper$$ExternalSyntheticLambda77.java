package org.telegram.messenger;

import android.graphics.Bitmap;
import java.util.HashMap;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.tgnet.TLRPC$TL_photo;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda77 implements Runnable {
    public final /* synthetic */ Bitmap[] f$0;
    public final /* synthetic */ String[] f$1;
    public final /* synthetic */ SendMessagesHelper.SendingMediaInfo f$10;
    public final /* synthetic */ boolean f$11;
    public final /* synthetic */ int f$12;
    public final /* synthetic */ MessageObject f$2;
    public final /* synthetic */ AccountInstance f$3;
    public final /* synthetic */ TLRPC$TL_photo f$4;
    public final /* synthetic */ HashMap f$5;
    public final /* synthetic */ String f$6;
    public final /* synthetic */ long f$7;
    public final /* synthetic */ MessageObject f$8;
    public final /* synthetic */ MessageObject f$9;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda77(Bitmap[] bitmapArr, String[] strArr, MessageObject messageObject, AccountInstance accountInstance, TLRPC$TL_photo tLRPC$TL_photo, HashMap hashMap, String str, long j, MessageObject messageObject2, MessageObject messageObject3, SendMessagesHelper.SendingMediaInfo sendingMediaInfo, boolean z, int i) {
        this.f$0 = bitmapArr;
        this.f$1 = strArr;
        this.f$2 = messageObject;
        this.f$3 = accountInstance;
        this.f$4 = tLRPC$TL_photo;
        this.f$5 = hashMap;
        this.f$6 = str;
        this.f$7 = j;
        this.f$8 = messageObject2;
        this.f$9 = messageObject3;
        this.f$10 = sendingMediaInfo;
        this.f$11 = z;
        this.f$12 = i;
    }

    public final void run() {
        SendMessagesHelper.lambda$prepareSendingMedia$90(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11, this.f$12);
    }
}
