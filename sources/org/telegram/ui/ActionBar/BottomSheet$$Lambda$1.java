package org.telegram.ui.ActionBar;

import android.view.View;
import android.view.View.OnApplyWindowInsetsListener;
import android.view.WindowInsets;

final /* synthetic */ class BottomSheet$$Lambda$1 implements OnApplyWindowInsetsListener {
    private final BottomSheet arg$1;

    BottomSheet$$Lambda$1(BottomSheet bottomSheet) {
        this.arg$1 = bottomSheet;
    }

    public WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
        return this.arg$1.lambda$new$0$BottomSheet(view, windowInsets);
    }
}
