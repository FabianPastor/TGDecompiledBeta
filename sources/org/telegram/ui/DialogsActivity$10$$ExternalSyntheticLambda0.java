package org.telegram.ui;

import org.telegram.ui.DialogsActivity;

public final /* synthetic */ class DialogsActivity$10$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ DialogsActivity.ViewPage f$0;

    public /* synthetic */ DialogsActivity$10$$ExternalSyntheticLambda0(DialogsActivity.ViewPage viewPage) {
        this.f$0 = viewPage;
    }

    public final void run() {
        this.f$0.dialogsAdapter.notifyDataSetChanged();
    }
}
