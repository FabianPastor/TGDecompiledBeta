package org.telegram.p005ui;

import android.view.View;
import org.telegram.p005ui.Components.RecyclerListView.OnItemLongClickListener;

/* renamed from: org.telegram.ui.LanguageSelectActivity$$Lambda$1 */
final /* synthetic */ class LanguageSelectActivity$$Lambda$1 implements OnItemLongClickListener {
    private final LanguageSelectActivity arg$1;

    LanguageSelectActivity$$Lambda$1(LanguageSelectActivity languageSelectActivity) {
        this.arg$1 = languageSelectActivity;
    }

    public boolean onItemClick(View view, int i) {
        return this.arg$1.lambda$createView$2$LanguageSelectActivity(view, i);
    }
}
