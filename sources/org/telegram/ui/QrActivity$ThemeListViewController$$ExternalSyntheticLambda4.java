package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.QrActivity;

public final /* synthetic */ class QrActivity$ThemeListViewController$$ExternalSyntheticLambda4 implements RecyclerListView.OnItemClickListener {
    public final /* synthetic */ QrActivity.ThemeListViewController f$0;

    public /* synthetic */ QrActivity$ThemeListViewController$$ExternalSyntheticLambda4(QrActivity.ThemeListViewController themeListViewController) {
        this.f$0 = themeListViewController;
    }

    public final void onItemClick(View view, int i) {
        this.f$0.onItemClicked(view, i);
    }
}
