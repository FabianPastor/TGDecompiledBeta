package org.telegram.ui;

import android.view.View;
import android.view.View.OnClickListener;

final /* synthetic */ class PhotoViewer$ListAdapter$$Lambda$0 implements OnClickListener {
    private final ListAdapter arg$1;

    PhotoViewer$ListAdapter$$Lambda$0(ListAdapter listAdapter) {
        this.arg$1 = listAdapter;
    }

    public void onClick(View view) {
        this.arg$1.lambda$onCreateViewHolder$0$PhotoViewer$ListAdapter(view);
    }
}
