package org.telegram.ui.Components;

import android.view.KeyEvent;
import android.view.View;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda62 implements View.OnKeyListener {
    public final /* synthetic */ ActionBarPopupWindow f$0;

    public /* synthetic */ AlertsCreator$$ExternalSyntheticLambda62(ActionBarPopupWindow actionBarPopupWindow) {
        this.f$0 = actionBarPopupWindow;
    }

    public final boolean onKey(View view, int i, KeyEvent keyEvent) {
        return AlertsCreator.lambda$showPopupMenu$104(this.f$0, view, i, keyEvent);
    }
}
