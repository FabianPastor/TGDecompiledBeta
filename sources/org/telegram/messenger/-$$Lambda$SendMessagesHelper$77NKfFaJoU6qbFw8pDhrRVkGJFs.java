package org.telegram.messenger;

import java.util.ArrayList;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$77NKfFaJoU6qbFw8pDhrRVkGJFs implements Runnable {
    private final /* synthetic */ SendMessagesHelper f$0;
    private final /* synthetic */ ArrayList f$1;
    private final /* synthetic */ ArrayList f$2;
    private final /* synthetic */ ArrayList f$3;
    private final /* synthetic */ ArrayList f$4;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$77NKfFaJoU6qbFw8pDhrRVkGJFs(SendMessagesHelper sendMessagesHelper, ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, ArrayList arrayList4) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = arrayList;
        this.f$2 = arrayList2;
        this.f$3 = arrayList3;
        this.f$4 = arrayList4;
    }

    public final void run() {
        this.f$0.lambda$processUnsentMessages$44$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
