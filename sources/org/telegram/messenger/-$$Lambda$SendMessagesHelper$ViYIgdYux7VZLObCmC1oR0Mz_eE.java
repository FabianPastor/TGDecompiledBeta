package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC.Message;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$ViYIgdYux7VZLObCmC1oR0Mz_eE implements Runnable {
    private final /* synthetic */ SendMessagesHelper f$0;
    private final /* synthetic */ Message f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ boolean f$3;
    private final /* synthetic */ ArrayList f$4;
    private final /* synthetic */ int f$5;
    private final /* synthetic */ String f$6;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$ViYIgdYux7VZLObCmC1oR0Mz_eE(SendMessagesHelper sendMessagesHelper, Message message, int i, boolean z, ArrayList arrayList, int i2, String str) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = message;
        this.f$2 = i;
        this.f$3 = z;
        this.f$4 = arrayList;
        this.f$5 = i2;
        this.f$6 = str;
    }

    public final void run() {
        this.f$0.lambda$null$46$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}
