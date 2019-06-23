package org.telegram.messenger;

import java.util.ArrayList;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$Z7xR6s7dmJd5tx9YQ_vwMv4VOvU implements Runnable {
    private final /* synthetic */ VideoEditedInfo f$0;
    private final /* synthetic */ String f$1;
    private final /* synthetic */ MessageObject f$10;
    private final /* synthetic */ MessageObject f$11;
    private final /* synthetic */ ArrayList f$12;
    private final /* synthetic */ long f$2;
    private final /* synthetic */ long f$3;
    private final /* synthetic */ int f$4;
    private final /* synthetic */ AccountInstance f$5;
    private final /* synthetic */ int f$6;
    private final /* synthetic */ int f$7;
    private final /* synthetic */ long f$8;
    private final /* synthetic */ CharSequence f$9;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$Z7xR6s7dmJd5tx9YQ_vwMv4VOvU(VideoEditedInfo videoEditedInfo, String str, long j, long j2, int i, AccountInstance accountInstance, int i2, int i3, long j3, CharSequence charSequence, MessageObject messageObject, MessageObject messageObject2, ArrayList arrayList) {
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
    }

    public final void run() {
        SendMessagesHelper.lambda$prepareSendingVideo$62(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11, this.f$12);
    }
}
