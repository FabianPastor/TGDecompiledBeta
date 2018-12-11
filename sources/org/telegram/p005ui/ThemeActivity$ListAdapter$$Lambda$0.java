package org.telegram.p005ui;

import android.view.View;
import android.view.View.OnClickListener;
import org.telegram.p005ui.ThemeActivity.ListAdapter;

/* renamed from: org.telegram.ui.ThemeActivity$ListAdapter$$Lambda$0 */
final /* synthetic */ class ThemeActivity$ListAdapter$$Lambda$0 implements OnClickListener {
    private final ListAdapter arg$1;

    ThemeActivity$ListAdapter$$Lambda$0(ListAdapter listAdapter) {
        this.arg$1 = listAdapter;
    }

    public void onClick(View view) {
        this.arg$1.lambda$onCreateViewHolder$2$ThemeActivity$ListAdapter(view);
    }
}
