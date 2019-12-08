package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC.messages_Dialogs;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$zIeqUFvdYuVSOc1tPS9Cd_hXG40 implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ messages_Dialogs f$3;
    private final /* synthetic */ boolean f$4;
    private final /* synthetic */ int f$5;
    private final /* synthetic */ ArrayList f$6;
    private final /* synthetic */ int f$7;
    private final /* synthetic */ boolean f$8;
    private final /* synthetic */ boolean f$9;

    public /* synthetic */ -$$Lambda$MessagesController$zIeqUFvdYuVSOc1tPS9Cd_hXG40(MessagesController messagesController, int i, int i2, messages_Dialogs messages_dialogs, boolean z, int i3, ArrayList arrayList, int i4, boolean z2, boolean z3) {
        this.f$0 = messagesController;
        this.f$1 = i;
        this.f$2 = i2;
        this.f$3 = messages_dialogs;
        this.f$4 = z;
        this.f$5 = i3;
        this.f$6 = arrayList;
        this.f$7 = i4;
        this.f$8 = z2;
        this.f$9 = z3;
    }

    public final void run() {
        this.f$0.lambda$processLoadedDialogs$133$MessagesController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9);
    }
}
