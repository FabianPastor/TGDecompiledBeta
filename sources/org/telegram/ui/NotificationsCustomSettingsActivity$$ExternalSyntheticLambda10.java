package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView;

public final /* synthetic */ class NotificationsCustomSettingsActivity$$ExternalSyntheticLambda10 implements RecyclerListView.OnItemClickListenerExtended {
    public final /* synthetic */ NotificationsCustomSettingsActivity f$0;

    public /* synthetic */ NotificationsCustomSettingsActivity$$ExternalSyntheticLambda10(NotificationsCustomSettingsActivity notificationsCustomSettingsActivity) {
        this.f$0 = notificationsCustomSettingsActivity;
    }

    public /* synthetic */ boolean hasDoubleTap(View view, int i) {
        return RecyclerListView.OnItemClickListenerExtended.CC.$default$hasDoubleTap(this, view, i);
    }

    public /* synthetic */ void onDoubleTap(View view, int i, float f, float f2) {
        RecyclerListView.OnItemClickListenerExtended.CC.$default$onDoubleTap(this, view, i, f, f2);
    }

    public final void onItemClick(View view, int i, float f, float f2) {
        this.f$0.lambda$createView$9(view, i, f, f2);
    }
}
