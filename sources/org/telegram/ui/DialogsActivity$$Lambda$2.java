package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;

final /* synthetic */ class DialogsActivity$$Lambda$2 implements OnItemClickListener {
    private final DialogsActivity arg$1;

    DialogsActivity$$Lambda$2(DialogsActivity dialogsActivity) {
        this.arg$1 = dialogsActivity;
    }

    public void onItemClick(View view, int i) {
        this.arg$1.lambda$createView$2$DialogsActivity(view, i);
    }
}
