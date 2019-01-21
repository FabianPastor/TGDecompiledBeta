package org.telegram.ui.Components;

import android.view.View;
import android.view.View.OnClickListener;
import org.telegram.ui.Components.PhotoFilterView.ToolsAdapter;

final /* synthetic */ class PhotoFilterView$ToolsAdapter$$Lambda$1 implements OnClickListener {
    private final ToolsAdapter arg$1;

    PhotoFilterView$ToolsAdapter$$Lambda$1(ToolsAdapter toolsAdapter) {
        this.arg$1 = toolsAdapter;
    }

    public void onClick(View view) {
        this.arg$1.lambda$onCreateViewHolder$1$PhotoFilterView$ToolsAdapter(view);
    }
}
