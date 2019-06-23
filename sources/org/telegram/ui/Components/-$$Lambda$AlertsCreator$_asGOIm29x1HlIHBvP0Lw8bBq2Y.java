package org.telegram.ui.Components;

import android.view.View;
import android.view.View.OnClickListener;
import java.util.ArrayList;
import org.telegram.messenger.MessagesStorage.IntCallback;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AlertsCreator$_asGOIm29x1HlIHBvP0Lw8bBq2Y implements OnClickListener {
    private final /* synthetic */ long f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ boolean f$2;
    private final /* synthetic */ IntCallback f$3;
    private final /* synthetic */ int f$4;
    private final /* synthetic */ BaseFragment f$5;
    private final /* synthetic */ ArrayList f$6;
    private final /* synthetic */ IntCallback f$7;
    private final /* synthetic */ Builder f$8;

    public /* synthetic */ -$$Lambda$AlertsCreator$_asGOIm29x1HlIHBvP0Lw8bBq2Y(long j, int i, boolean z, IntCallback intCallback, int i2, BaseFragment baseFragment, ArrayList arrayList, IntCallback intCallback2, Builder builder) {
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
        AlertsCreator.lambda$showCustomNotificationsDialog$5(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, view);
    }
}
