package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;

final /* synthetic */ class DocumentSelectActivity$$Lambda$2 implements OnItemClickListener {
    private final DocumentSelectActivity arg$1;

    DocumentSelectActivity$$Lambda$2(DocumentSelectActivity documentSelectActivity) {
        this.arg$1 = documentSelectActivity;
    }

    public void onItemClick(View view, int i) {
        this.arg$1.lambda$createView$2$DocumentSelectActivity(view, i);
    }
}
