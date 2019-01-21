package org.telegram.ui.Components;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.ThemeEditorView.EditorAlert;

final /* synthetic */ class ThemeEditorView$EditorAlert$$Lambda$0 implements OnItemClickListener {
    private final EditorAlert arg$1;

    ThemeEditorView$EditorAlert$$Lambda$0(EditorAlert editorAlert) {
        this.arg$1 = editorAlert;
    }

    public void onItemClick(View view, int i) {
        this.arg$1.lambda$new$0$ThemeEditorView$EditorAlert(view, i);
    }
}
