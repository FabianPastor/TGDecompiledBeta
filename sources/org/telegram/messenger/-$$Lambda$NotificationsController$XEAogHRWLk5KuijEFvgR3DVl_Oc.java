package org.telegram.messenger;

import android.util.LongSparseArray;
import java.util.ArrayList;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$NotificationsController$XEAogHRWLk5KuijEFvgR3DVl_Oc implements Runnable {
    private final /* synthetic */ NotificationsController f$0;
    private final /* synthetic */ ArrayList f$1;
    private final /* synthetic */ LongSparseArray f$2;
    private final /* synthetic */ ArrayList f$3;

    public /* synthetic */ -$$Lambda$NotificationsController$XEAogHRWLk5KuijEFvgR3DVl_Oc(NotificationsController notificationsController, ArrayList arrayList, LongSparseArray longSparseArray, ArrayList arrayList2) {
        this.f$0 = notificationsController;
        this.f$1 = arrayList;
        this.f$2 = longSparseArray;
        this.f$3 = arrayList2;
    }

    public final void run() {
        this.f$0.lambda$processLoadedUnreadMessages$21$NotificationsController(this.f$1, this.f$2, this.f$3);
    }
}
