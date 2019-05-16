package org.telegram.messenger;

import android.util.SparseIntArray;
import java.util.ArrayList;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$NotificationsController$4ZPSiSXCXkKfxVPcPpmsFy8foEU implements Runnable {
    private final /* synthetic */ NotificationsController f$0;
    private final /* synthetic */ SparseIntArray f$1;
    private final /* synthetic */ ArrayList f$2;

    public /* synthetic */ -$$Lambda$NotificationsController$4ZPSiSXCXkKfxVPcPpmsFy8foEU(NotificationsController notificationsController, SparseIntArray sparseIntArray, ArrayList arrayList) {
        this.f$0 = notificationsController;
        this.f$1 = sparseIntArray;
        this.f$2 = arrayList;
    }

    public final void run() {
        this.f$0.lambda$removeDeletedHisoryFromNotifications$11$NotificationsController(this.f$1, this.f$2);
    }
}
