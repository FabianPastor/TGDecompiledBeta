package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.DialogsActivity;

public final /* synthetic */ class DialogsActivity$$ExternalSyntheticLambda51 implements RecyclerListView.OnItemClickListener {
    public final /* synthetic */ DialogsActivity f$0;
    public final /* synthetic */ DialogsActivity.ViewPage f$1;

    public /* synthetic */ DialogsActivity$$ExternalSyntheticLambda51(DialogsActivity dialogsActivity, DialogsActivity.ViewPage viewPage) {
        this.f$0 = dialogsActivity;
        this.f$1 = viewPage;
    }

    public final void onItemClick(View view, int i) {
        this.f$0.lambda$createView$5(this.f$1, view, i);
    }
}