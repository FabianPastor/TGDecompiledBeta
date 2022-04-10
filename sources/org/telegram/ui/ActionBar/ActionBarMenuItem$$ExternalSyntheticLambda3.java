package org.telegram.ui.ActionBar;

import android.view.View;
import org.telegram.ui.ActionBar.ActionBarMenuItem;

public final /* synthetic */ class ActionBarMenuItem$$ExternalSyntheticLambda3 implements View.OnClickListener {
    public final /* synthetic */ ActionBarMenuItem f$0;
    public final /* synthetic */ ActionBarMenuItem.SearchFilterView f$1;

    public /* synthetic */ ActionBarMenuItem$$ExternalSyntheticLambda3(ActionBarMenuItem actionBarMenuItem, ActionBarMenuItem.SearchFilterView searchFilterView) {
        this.f$0 = actionBarMenuItem;
        this.f$1 = searchFilterView;
    }

    public final void onClick(View view) {
        this.f$0.lambda$onFiltersChanged$10(this.f$1, view);
    }
}
