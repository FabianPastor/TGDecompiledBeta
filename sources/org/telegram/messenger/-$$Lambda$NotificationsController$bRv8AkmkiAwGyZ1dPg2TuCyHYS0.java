package org.telegram.messenger;

import android.util.LongSparseArray;
import java.util.ArrayList;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$NotificationsController$bRv8AkmkiAwGyZ1dPg2TuCyHYS0 implements Runnable {
    private final /* synthetic */ NotificationsController f$0;
    private final /* synthetic */ LongSparseArray f$1;
    private final /* synthetic */ ArrayList f$2;

    public /* synthetic */ -$$Lambda$NotificationsController$bRv8AkmkiAwGyZ1dPg2TuCyHYS0(NotificationsController notificationsController, LongSparseArray longSparseArray, ArrayList arrayList) {
        this.f$0 = notificationsController;
        this.f$1 = longSparseArray;
        this.f$2 = arrayList;
    }

    public final void run() {
        this.f$0.lambda$processDialogsUpdateRead$19$NotificationsController(this.f$1, this.f$2);
    }
}
