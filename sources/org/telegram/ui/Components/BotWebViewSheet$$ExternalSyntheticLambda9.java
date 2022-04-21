package org.telegram.ui.Components;

import android.view.View;
import android.view.WindowInsets;

public final /* synthetic */ class BotWebViewSheet$$ExternalSyntheticLambda9 implements View.OnApplyWindowInsetsListener {
    public static final /* synthetic */ BotWebViewSheet$$ExternalSyntheticLambda9 INSTANCE = new BotWebViewSheet$$ExternalSyntheticLambda9();

    private /* synthetic */ BotWebViewSheet$$ExternalSyntheticLambda9() {
    }

    public final WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
        return view.setPadding(0, 0, 0, windowInsets.getSystemWindowInsetBottom());
    }
}
