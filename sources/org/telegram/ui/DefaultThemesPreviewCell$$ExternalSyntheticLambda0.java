package org.telegram.ui;

import android.view.View;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class DefaultThemesPreviewCell$$ExternalSyntheticLambda0 implements View.OnClickListener {
    public final /* synthetic */ BaseFragment f$0;

    public /* synthetic */ DefaultThemesPreviewCell$$ExternalSyntheticLambda0(BaseFragment baseFragment) {
        this.f$0 = baseFragment;
    }

    public final void onClick(View view) {
        this.f$0.presentFragment(new ThemeActivity(3));
    }
}
