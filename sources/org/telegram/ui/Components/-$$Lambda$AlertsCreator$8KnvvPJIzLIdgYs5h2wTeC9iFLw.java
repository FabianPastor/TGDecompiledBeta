package org.telegram.ui.Components;

import android.view.View;
import android.view.View.OnClickListener;
import org.telegram.ui.ActionBar.ActionBarMenuItem;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AlertsCreator$8KnvvPJIzLIdgYs5h2wTeC9iFLw implements OnClickListener {
    private final /* synthetic */ ActionBarMenuItem f$0;

    public /* synthetic */ -$$Lambda$AlertsCreator$8KnvvPJIzLIdgYs5h2wTeC9iFLw(ActionBarMenuItem actionBarMenuItem) {
        this.f$0 = actionBarMenuItem;
    }

    public final void onClick(View view) {
        this.f$0.toggleSubMenu();
    }
}
