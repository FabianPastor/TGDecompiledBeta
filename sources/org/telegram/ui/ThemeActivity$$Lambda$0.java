package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;

final /* synthetic */ class ThemeActivity$$Lambda$0 implements OnItemClickListener {
    private final ThemeActivity arg$1;

    ThemeActivity$$Lambda$0(ThemeActivity themeActivity) {
        this.arg$1 = themeActivity;
    }

    public void onItemClick(View view, int i) {
        this.arg$1.lambda$createView$4$ThemeActivity(view, i);
    }
}
