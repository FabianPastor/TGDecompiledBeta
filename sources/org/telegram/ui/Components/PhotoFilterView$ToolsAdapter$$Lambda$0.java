package org.telegram.ui.Components;

import org.telegram.ui.Components.PhotoEditorSeekBar.PhotoEditorSeekBarDelegate;
import org.telegram.ui.Components.PhotoFilterView.ToolsAdapter;

final /* synthetic */ class PhotoFilterView$ToolsAdapter$$Lambda$0 implements PhotoEditorSeekBarDelegate {
    private final ToolsAdapter arg$1;

    PhotoFilterView$ToolsAdapter$$Lambda$0(ToolsAdapter toolsAdapter) {
        this.arg$1 = toolsAdapter;
    }

    public void onProgressChanged(int i, int i2) {
        this.arg$1.lambda$onCreateViewHolder$0$PhotoFilterView$ToolsAdapter(i, i2);
    }
}
