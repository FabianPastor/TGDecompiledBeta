package org.telegram.ui.Components;

import android.view.View;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;

public final /* synthetic */ class FragmentContextView$$ExternalSyntheticLambda7 implements View.OnClickListener {
    public final /* synthetic */ FragmentContextView f$0;
    public final /* synthetic */ Theme.ResourcesProvider f$1;
    public final /* synthetic */ BaseFragment f$2;

    public /* synthetic */ FragmentContextView$$ExternalSyntheticLambda7(FragmentContextView fragmentContextView, Theme.ResourcesProvider resourcesProvider, BaseFragment baseFragment) {
        this.f$0 = fragmentContextView;
        this.f$1 = resourcesProvider;
        this.f$2 = baseFragment;
    }

    public final void onClick(View view) {
        this.f$0.lambda$new$10(this.f$1, this.f$2, view);
    }
}