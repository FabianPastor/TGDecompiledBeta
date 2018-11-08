package org.telegram.p005ui.Adapters;

import android.view.View;
import org.telegram.p005ui.Components.RecyclerListView.OnItemLongClickListener;

/* renamed from: org.telegram.ui.Adapters.DialogsSearchAdapter$$Lambda$7 */
final /* synthetic */ class DialogsSearchAdapter$$Lambda$7 implements OnItemLongClickListener {
    private final DialogsSearchAdapter arg$1;

    DialogsSearchAdapter$$Lambda$7(DialogsSearchAdapter dialogsSearchAdapter) {
        this.arg$1 = dialogsSearchAdapter;
    }

    public boolean onItemClick(View view, int i) {
        return this.arg$1.lambda$onCreateViewHolder$11$DialogsSearchAdapter(view, i);
    }
}
