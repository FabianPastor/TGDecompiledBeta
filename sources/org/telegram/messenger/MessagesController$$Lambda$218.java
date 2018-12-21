package org.telegram.messenger;

import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.tgnet.TLRPC.messages_Messages;

final /* synthetic */ class MessagesController$$Lambda$218 implements Runnable {
    private final MessagesController arg$1;
    private final int arg$10;
    private final int arg$11;
    private final int arg$12;
    private final boolean arg$13;
    private final int arg$14;
    private final int arg$15;
    private final int arg$16;
    private final int arg$17;
    private final ArrayList arg$18;
    private final HashMap arg$19;
    private final messages_Messages arg$2;
    private final boolean arg$3;
    private final boolean arg$4;
    private final int arg$5;
    private final int arg$6;
    private final long arg$7;
    private final int arg$8;
    private final ArrayList arg$9;

    MessagesController$$Lambda$218(MessagesController messagesController, messages_Messages messages_messages, boolean z, boolean z2, int i, int i2, long j, int i3, ArrayList arrayList, int i4, int i5, int i6, boolean z3, int i7, int i8, int i9, int i10, ArrayList arrayList2, HashMap hashMap) {
        this.arg$1 = messagesController;
        this.arg$2 = messages_messages;
        this.arg$3 = z;
        this.arg$4 = z2;
        this.arg$5 = i;
        this.arg$6 = i2;
        this.arg$7 = j;
        this.arg$8 = i3;
        this.arg$9 = arrayList;
        this.arg$10 = i4;
        this.arg$11 = i5;
        this.arg$12 = i6;
        this.arg$13 = z3;
        this.arg$14 = i7;
        this.arg$15 = i8;
        this.arg$16 = i9;
        this.arg$17 = i10;
        this.arg$18 = arrayList2;
        this.arg$19 = hashMap;
    }

    public void run() {
        this.arg$1.lambda$null$96$MessagesController(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7, this.arg$8, this.arg$9, this.arg$10, this.arg$11, this.arg$12, this.arg$13, this.arg$14, this.arg$15, this.arg$16, this.arg$17, this.arg$18, this.arg$19);
    }
}
