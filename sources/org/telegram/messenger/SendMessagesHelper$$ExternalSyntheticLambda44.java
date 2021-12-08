package org.telegram.messenger;

import android.graphics.Bitmap;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda44 implements Runnable {
    public final /* synthetic */ Bitmap f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ MessageObject f$10;
    public final /* synthetic */ MessageObject f$11;
    public final /* synthetic */ String f$12;
    public final /* synthetic */ ArrayList f$13;
    public final /* synthetic */ boolean f$14;
    public final /* synthetic */ int f$15;
    public final /* synthetic */ int f$16;
    public final /* synthetic */ MessageObject f$2;
    public final /* synthetic */ AccountInstance f$3;
    public final /* synthetic */ VideoEditedInfo f$4;
    public final /* synthetic */ TLRPC.TL_document f$5;
    public final /* synthetic */ String f$6;
    public final /* synthetic */ HashMap f$7;
    public final /* synthetic */ String f$8;
    public final /* synthetic */ long f$9;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda44(Bitmap bitmap, String str, MessageObject messageObject, AccountInstance accountInstance, VideoEditedInfo videoEditedInfo, TLRPC.TL_document tL_document, String str2, HashMap hashMap, String str3, long j, MessageObject messageObject2, MessageObject messageObject3, String str4, ArrayList arrayList, boolean z, int i, int i2) {
        this.f$0 = bitmap;
        this.f$1 = str;
        this.f$2 = messageObject;
        this.f$3 = accountInstance;
        this.f$4 = videoEditedInfo;
        this.f$5 = tL_document;
        this.f$6 = str2;
        this.f$7 = hashMap;
        this.f$8 = str3;
        this.f$9 = j;
        this.f$10 = messageObject2;
        this.f$11 = messageObject3;
        this.f$12 = str4;
        this.f$13 = arrayList;
        this.f$14 = z;
        this.f$15 = i;
        this.f$16 = i2;
    }

    public final void run() {
        Bitmap bitmap = this.f$0;
        Bitmap bitmap2 = bitmap;
        SendMessagesHelper.lambda$prepareSendingVideo$90(bitmap2, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11, this.f$12, this.f$13, this.f$14, this.f$15, this.f$16);
    }
}
