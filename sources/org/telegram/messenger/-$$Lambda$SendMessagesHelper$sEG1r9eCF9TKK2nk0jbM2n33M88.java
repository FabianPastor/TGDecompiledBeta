package org.telegram.messenger;

import android.graphics.Bitmap;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.tgnet.TLRPC.TL_document;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$sEG1r9eCF9TKK2nk0jbM2n33M88 implements Runnable {
    private final /* synthetic */ Bitmap f$0;
    private final /* synthetic */ String f$1;
    private final /* synthetic */ MessageObject f$10;
    private final /* synthetic */ String f$11;
    private final /* synthetic */ ArrayList f$12;
    private final /* synthetic */ boolean f$13;
    private final /* synthetic */ int f$14;
    private final /* synthetic */ int f$15;
    private final /* synthetic */ MessageObject f$2;
    private final /* synthetic */ AccountInstance f$3;
    private final /* synthetic */ VideoEditedInfo f$4;
    private final /* synthetic */ TL_document f$5;
    private final /* synthetic */ String f$6;
    private final /* synthetic */ HashMap f$7;
    private final /* synthetic */ String f$8;
    private final /* synthetic */ long f$9;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$sEG1r9eCF9TKK2nk0jbM2n33M88(Bitmap bitmap, String str, MessageObject messageObject, AccountInstance accountInstance, VideoEditedInfo videoEditedInfo, TL_document tL_document, String str2, HashMap hashMap, String str3, long j, MessageObject messageObject2, String str4, ArrayList arrayList, boolean z, int i, int i2) {
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
        this.f$11 = str4;
        this.f$12 = arrayList;
        this.f$13 = z;
        this.f$14 = i;
        this.f$15 = i2;
    }

    public final void run() {
        Bitmap bitmap = this.f$0;
        Bitmap bitmap2 = bitmap;
        SendMessagesHelper.lambda$null$73(bitmap2, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11, this.f$12, this.f$13, this.f$14, this.f$15);
    }
}
