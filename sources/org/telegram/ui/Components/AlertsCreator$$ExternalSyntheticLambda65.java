package org.telegram.ui.Components;

import android.view.KeyEvent;
import android.view.View;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda65 implements View.OnKeyListener {
    public final /* synthetic */ ActionBarPopupWindow f$0;

    public /* synthetic */ AlertsCreator$$ExternalSyntheticLambda65(ActionBarPopupWindow actionBarPopupWindow) {
        this.f$0 = actionBarPopupWindow;
    }

    public final boolean onKey(View view, int i, KeyEvent keyEvent) {
        return AlertsCreator.lambda$showPopupMenu$107(this.f$0, view, i, keyEvent);
    }
}
