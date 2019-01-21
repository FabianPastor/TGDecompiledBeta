package org.telegram.ui.Adapters;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener;

final /* synthetic */ class DialogsSearchAdapter$$Lambda$7 implements OnItemLongClickListener {
    private final DialogsSearchAdapter arg$1;

    DialogsSearchAdapter$$Lambda$7(DialogsSearchAdapter dialogsSearchAdapter) {
        this.arg$1 = dialogsSearchAdapter;
    }

    public boolean onItemClick(View view, int i) {
        return this.arg$1.lambda$onCreateViewHolder$11$DialogsSearchAdapter(view, i);
    }
}
