package org.telegram.ui.ActionBar;

import android.view.View;
import android.view.View.OnApplyWindowInsetsListener;
import android.view.WindowInsets;

final /* synthetic */ class DrawerLayoutContainer$$Lambda$0 implements OnApplyWindowInsetsListener {
    private final DrawerLayoutContainer arg$1;

    DrawerLayoutContainer$$Lambda$0(DrawerLayoutContainer drawerLayoutContainer) {
        this.arg$1 = drawerLayoutContainer;
    }

    public WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
        return this.arg$1.lambda$new$0$DrawerLayoutContainer(view, windowInsets);
    }
}
