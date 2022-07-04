package org.telegram.messenger;

import android.graphics.Bitmap;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC$Document;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda72 implements Runnable {
    public final /* synthetic */ SendMessagesHelper f$0;
    public final /* synthetic */ Bitmap[] f$1;
    public final /* synthetic */ MessageObject.SendAnimationData f$10;
    public final /* synthetic */ String[] f$2;
    public final /* synthetic */ TLRPC$Document f$3;
    public final /* synthetic */ long f$4;
    public final /* synthetic */ MessageObject f$5;
    public final /* synthetic */ MessageObject f$6;
    public final /* synthetic */ boolean f$7;
    public final /* synthetic */ int f$8;
    public final /* synthetic */ Object f$9;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda72(SendMessagesHelper sendMessagesHelper, Bitmap[] bitmapArr, String[] strArr, TLRPC$Document tLRPC$Document, long j, MessageObject messageObject, MessageObject messageObject2, boolean z, int i, Object obj, MessageObject.SendAnimationData sendAnimationData) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = bitmapArr;
        this.f$2 = strArr;
        this.f$3 = tLRPC$Document;
        this.f$4 = j;
        this.f$5 = messageObject;
        this.f$6 = messageObject2;
        this.f$7 = z;
        this.f$8 = i;
        this.f$9 = obj;
        this.f$10 = sendAnimationData;
    }

    public final void run() {
        this.f$0.lambda$sendSticker$5(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10);
    }
}
