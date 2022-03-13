package org.telegram.ui.Components;

import android.view.MotionEvent;
import android.view.View;
import org.telegram.ui.ActionBar.Theme;

public final /* synthetic */ class ChatActivityEnterView$$ExternalSyntheticLambda26 implements View.OnTouchListener {
    public final /* synthetic */ ChatActivityEnterView f$0;
    public final /* synthetic */ Theme.ResourcesProvider f$1;

    public /* synthetic */ ChatActivityEnterView$$ExternalSyntheticLambda26(ChatActivityEnterView chatActivityEnterView, Theme.ResourcesProvider resourcesProvider) {
        this.f$0 = chatActivityEnterView;
        this.f$1 = resourcesProvider;
    }

    public final boolean onTouch(View view, MotionEvent motionEvent) {
        return this.f$0.lambda$new$23(this.f$1, view, motionEvent);
    }
}
