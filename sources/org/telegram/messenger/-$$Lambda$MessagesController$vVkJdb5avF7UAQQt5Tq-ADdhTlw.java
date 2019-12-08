package org.telegram.messenger;

import android.util.LongSparseArray;
import android.util.SparseArray;
import java.util.ArrayList;
import org.telegram.tgnet.TLRPC.messages_Dialogs;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$vVkJdb5avF7UAQQt5Tq-ADdhTlw implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ boolean f$10;
    private final /* synthetic */ int f$11;
    private final /* synthetic */ ArrayList f$12;
    private final /* synthetic */ messages_Dialogs f$2;
    private final /* synthetic */ ArrayList f$3;
    private final /* synthetic */ boolean f$4;
    private final /* synthetic */ int f$5;
    private final /* synthetic */ LongSparseArray f$6;
    private final /* synthetic */ LongSparseArray f$7;
    private final /* synthetic */ SparseArray f$8;
    private final /* synthetic */ int f$9;

    public /* synthetic */ -$$Lambda$MessagesController$vVkJdb5avF7UAQQt5Tq-ADdhTlw(MessagesController messagesController, int i, messages_Dialogs messages_dialogs, ArrayList arrayList, boolean z, int i2, LongSparseArray longSparseArray, LongSparseArray longSparseArray2, SparseArray sparseArray, int i3, boolean z2, int i4, ArrayList arrayList2) {
        this.f$0 = messagesController;
        this.f$1 = i;
        this.f$2 = messages_dialogs;
        this.f$3 = arrayList;
        this.f$4 = z;
        this.f$5 = i2;
        this.f$6 = longSparseArray;
        this.f$7 = longSparseArray2;
        this.f$8 = sparseArray;
        this.f$9 = i3;
        this.f$10 = z2;
        this.f$11 = i4;
        this.f$12 = arrayList2;
    }

    public final void run() {
        this.f$0.lambda$null$143$MessagesController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11, this.f$12);
    }
}
