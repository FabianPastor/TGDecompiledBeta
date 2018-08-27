package org.telegram.messenger;

import java.util.ArrayList;

final /* synthetic */ class SendMessagesHelper$$Lambda$20 implements Runnable {
    private final VideoEditedInfo arg$1;
    private final CharSequence arg$10;
    private final MessageObject arg$11;
    private final MessageObject arg$12;
    private final ArrayList arg$13;
    private final String arg$2;
    private final long arg$3;
    private final long arg$4;
    private final int arg$5;
    private final int arg$6;
    private final int arg$7;
    private final int arg$8;
    private final long arg$9;

    SendMessagesHelper$$Lambda$20(VideoEditedInfo videoEditedInfo, String str, long j, long j2, int i, int i2, int i3, int i4, long j3, CharSequence charSequence, MessageObject messageObject, MessageObject messageObject2, ArrayList arrayList) {
        this.arg$1 = videoEditedInfo;
        this.arg$2 = str;
        this.arg$3 = j;
        this.arg$4 = j2;
        this.arg$5 = i;
        this.arg$6 = i2;
        this.arg$7 = i3;
        this.arg$8 = i4;
        this.arg$9 = j3;
        this.arg$10 = charSequence;
        this.arg$11 = messageObject;
        this.arg$12 = messageObject2;
        this.arg$13 = arrayList;
    }

    public void run() {
        SendMessagesHelper.lambda$prepareSendingVideo$61$SendMessagesHelper(this.arg$1, this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7, this.arg$8, this.arg$9, this.arg$10, this.arg$11, this.arg$12, this.arg$13);
    }
}
