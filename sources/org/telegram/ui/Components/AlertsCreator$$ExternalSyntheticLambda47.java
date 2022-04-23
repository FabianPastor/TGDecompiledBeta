package org.telegram.ui.Components;

import android.view.View;
import java.util.ArrayList;
import org.telegram.messenger.MessagesStorage;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda47 implements View.OnClickListener {
    public final /* synthetic */ long f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ MessagesStorage.IntCallback f$3;
    public final /* synthetic */ int f$4;
    public final /* synthetic */ BaseFragment f$5;
    public final /* synthetic */ ArrayList f$6;
    public final /* synthetic */ MessagesStorage.IntCallback f$7;
    public final /* synthetic */ AlertDialog.Builder f$8;

    public /* synthetic */ AlertsCreator$$ExternalSyntheticLambda47(long j, int i, boolean z, MessagesStorage.IntCallback intCallback, int i2, BaseFragment baseFragment, ArrayList arrayList, MessagesStorage.IntCallback intCallback2, AlertDialog.Builder builder) {
        this.f$0 = j;
        this.f$1 = i;
        this.f$2 = z;
        this.f$3 = intCallback;
        this.f$4 = i2;
        this.f$5 = baseFragment;
        this.f$6 = arrayList;
        this.f$7 = intCallback2;
        this.f$8 = builder;
    }

    public final void onClick(View view) {
        AlertsCreator.lambda$showCustomNotificationsDialog$15(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, view);
    }
}
