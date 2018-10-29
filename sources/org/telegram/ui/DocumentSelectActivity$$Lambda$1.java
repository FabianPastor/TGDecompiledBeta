package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener;

final /* synthetic */ class DocumentSelectActivity$$Lambda$1 implements OnItemLongClickListener {
    private final DocumentSelectActivity arg$1;

    DocumentSelectActivity$$Lambda$1(DocumentSelectActivity documentSelectActivity) {
        this.arg$1 = documentSelectActivity;
    }

    public boolean onItemClick(View view, int i) {
        return this.arg$1.lambda$createView$1$DocumentSelectActivity(view, i);
    }
}
