package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;

final /* synthetic */ class LanguageSelectActivity$$Lambda$0 implements OnItemClickListener {
    private final LanguageSelectActivity arg$1;

    LanguageSelectActivity$$Lambda$0(LanguageSelectActivity languageSelectActivity) {
        this.arg$1 = languageSelectActivity;
    }

    public void onItemClick(View view, int i) {
        this.arg$1.lambda$createView$0$LanguageSelectActivity(view, i);
    }
}
