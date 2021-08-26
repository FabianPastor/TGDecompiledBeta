package org.telegram.messenger;

import android.util.SparseIntArray;
import java.util.ArrayList;

public final /* synthetic */ class NotificationsController$$ExternalSyntheticLambda27 implements Runnable {
    public final /* synthetic */ NotificationsController f$0;
    public final /* synthetic */ SparseIntArray f$1;
    public final /* synthetic */ ArrayList f$2;

    public /* synthetic */ NotificationsController$$ExternalSyntheticLambda27(NotificationsController notificationsController, SparseIntArray sparseIntArray, ArrayList arrayList) {
        this.f$0 = notificationsController;
        this.f$1 = sparseIntArray;
        this.f$2 = arrayList;
    }

    public final void run() {
        this.f$0.lambda$removeDeletedHisoryFromNotifications$12(this.f$1, this.f$2);
    }
}
