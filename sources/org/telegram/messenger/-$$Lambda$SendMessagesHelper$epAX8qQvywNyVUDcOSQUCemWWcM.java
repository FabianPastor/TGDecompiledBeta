package org.telegram.messenger;

import java.util.ArrayList;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$epAX8qQvywNyVUDcOSQUCemWWcM implements Runnable {
    private final /* synthetic */ VideoEditedInfo f$0;
    private final /* synthetic */ String f$1;
    private final /* synthetic */ MessageObject f$10;
    private final /* synthetic */ MessageObject f$11;
    private final /* synthetic */ ArrayList f$12;
    private final /* synthetic */ boolean f$13;
    private final /* synthetic */ int f$14;
    private final /* synthetic */ long f$2;
    private final /* synthetic */ long f$3;
    private final /* synthetic */ int f$4;
    private final /* synthetic */ AccountInstance f$5;
    private final /* synthetic */ int f$6;
    private final /* synthetic */ int f$7;
    private final /* synthetic */ long f$8;
    private final /* synthetic */ CharSequence f$9;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$epAX8qQvywNyVUDcOSQUCemWWcM(VideoEditedInfo videoEditedInfo, String str, long j, long j2, int i, AccountInstance accountInstance, int i2, int i3, long j3, CharSequence charSequence, MessageObject messageObject, MessageObject messageObject2, ArrayList arrayList, boolean z, int i4) {
        this.f$0 = videoEditedInfo;
        this.f$1 = str;
        this.f$2 = j;
        this.f$3 = j2;
        this.f$4 = i;
        this.f$5 = accountInstance;
        this.f$6 = i2;
        this.f$7 = i3;
        this.f$8 = j3;
        this.f$9 = charSequence;
        this.f$10 = messageObject;
        this.f$11 = messageObject2;
        this.f$12 = arrayList;
        this.f$13 = z;
        this.f$14 = i4;
    }

    public final void run() {
        VideoEditedInfo videoEditedInfo = this.f$0;
        VideoEditedInfo videoEditedInfo2 = videoEditedInfo;
        SendMessagesHelper.lambda$prepareSendingVideo$68(videoEditedInfo2, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11, this.f$12, this.f$13, this.f$14);
    }
}
