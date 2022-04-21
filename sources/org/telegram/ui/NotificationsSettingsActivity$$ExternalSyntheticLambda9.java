package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView;

public final /* synthetic */ class NotificationsSettingsActivity$$ExternalSyntheticLambda9 implements RecyclerListView.OnItemClickListenerExtended {
    public final /* synthetic */ NotificationsSettingsActivity f$0;

    public /* synthetic */ NotificationsSettingsActivity$$ExternalSyntheticLambda9(NotificationsSettingsActivity notificationsSettingsActivity) {
        this.f$0 = notificationsSettingsActivity;
    }

    public /* synthetic */ boolean hasDoubleTap(View view, int i) {
        return RecyclerListView.OnItemClickListenerExtended.CC.$default$hasDoubleTap(this, view, i);
    }

    public /* synthetic */ void onDoubleTap(View view, int i, float f, float f2) {
        RecyclerListView.OnItemClickListenerExtended.CC.$default$onDoubleTap(this, view, i, f, f2);
    }

    public final void onItemClick(View view, int i, float f, float f2) {
        this.f$0.m2668xbde8ae12(view, i, f, f2);
    }
}
