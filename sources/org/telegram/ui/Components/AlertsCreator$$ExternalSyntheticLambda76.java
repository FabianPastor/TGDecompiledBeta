package org.telegram.ui.Components;

import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda76 implements View.OnTouchListener {
    public final /* synthetic */ ActionBarPopupWindow f$0;
    public final /* synthetic */ Rect f$1;

    public /* synthetic */ AlertsCreator$$ExternalSyntheticLambda76(ActionBarPopupWindow actionBarPopupWindow, Rect rect) {
        this.f$0 = actionBarPopupWindow;
        this.f$1 = rect;
    }

    public final boolean onTouch(View view, MotionEvent motionEvent) {
        return AlertsCreator.lambda$showPopupMenu$134(this.f$0, this.f$1, view, motionEvent);
    }
}
