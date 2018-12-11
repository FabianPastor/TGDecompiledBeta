package org.telegram.p005ui;

import android.view.View;
import org.telegram.p005ui.Components.RecyclerListView.OnItemClickListener;

/* renamed from: org.telegram.ui.ArchivedStickersActivity$$Lambda$0 */
final /* synthetic */ class ArchivedStickersActivity$$Lambda$0 implements OnItemClickListener {
    private final ArchivedStickersActivity arg$1;

    ArchivedStickersActivity$$Lambda$0(ArchivedStickersActivity archivedStickersActivity) {
        this.arg$1 = archivedStickersActivity;
    }

    public void onItemClick(View view, int i) {
        this.arg$1.lambda$createView$0$ArchivedStickersActivity(view, i);
    }
}
