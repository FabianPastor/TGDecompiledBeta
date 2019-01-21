package org.telegram.ui.Components;

import android.view.View;
import android.view.View.OnClickListener;
import java.util.ArrayList;
import org.telegram.messenger.MessagesStorage.IntCallback;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;

final /* synthetic */ class AlertsCreator$$Lambda$3 implements OnClickListener {
    private final long arg$1;
    private final int arg$2;
    private final boolean arg$3;
    private final IntCallback arg$4;
    private final int arg$5;
    private final BaseFragment arg$6;
    private final ArrayList arg$7;
    private final IntCallback arg$8;
    private final Builder arg$9;

    AlertsCreator$$Lambda$3(long j, int i, boolean z, IntCallback intCallback, int i2, BaseFragment baseFragment, ArrayList arrayList, IntCallback intCallback2, Builder builder) {
        this.arg$1 = j;
        this.arg$2 = i;
        this.arg$3 = z;
        this.arg$4 = intCallback;
        this.arg$5 = i2;
        this.arg$6 = baseFragment;
        this.arg$7 = arrayList;
        this.arg$8 = intCallback2;
        this.arg$9 = builder;
    }

    public void onClick(View view) {
        AlertsCreator.lambda$showCustomNotificationsDialog$3$AlertsCreator(this.arg$1, this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7, this.arg$8, this.arg$9, view);
    }
}
