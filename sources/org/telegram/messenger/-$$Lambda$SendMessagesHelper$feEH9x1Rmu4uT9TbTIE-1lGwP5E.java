package org.telegram.messenger;

import java.util.ArrayList;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$feEH9x1Rmu4uT9TbTIE-1lGwP5E implements Runnable {
    private final /* synthetic */ SendMessagesHelper f$0;
    private final /* synthetic */ ArrayList f$1;
    private final /* synthetic */ ArrayList f$2;
    private final /* synthetic */ ArrayList f$3;
    private final /* synthetic */ ArrayList f$4;
    private final /* synthetic */ ArrayList f$5;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$feEH9x1Rmu4uT9TbTIE-1lGwP5E(SendMessagesHelper sendMessagesHelper, ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, ArrayList arrayList4, ArrayList arrayList5) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = arrayList;
        this.f$2 = arrayList2;
        this.f$3 = arrayList3;
        this.f$4 = arrayList4;
        this.f$5 = arrayList5;
    }

    public final void run() {
        this.f$0.lambda$processUnsentMessages$49$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
