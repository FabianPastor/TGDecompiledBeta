package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.messenger.MessagesController;
import org.telegram.ui.FiltersSetupActivity;

public final /* synthetic */ class FiltersSetupActivity$ListAdapter$$ExternalSyntheticLambda1 implements DialogInterface.OnClickListener {
    public final /* synthetic */ FiltersSetupActivity.ListAdapter f$0;
    public final /* synthetic */ MessagesController.DialogFilter f$1;

    public /* synthetic */ FiltersSetupActivity$ListAdapter$$ExternalSyntheticLambda1(FiltersSetupActivity.ListAdapter listAdapter, MessagesController.DialogFilter dialogFilter) {
        this.f$0 = listAdapter;
        this.f$1 = dialogFilter;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.m3495x49var_df(this.f$1, dialogInterface, i);
    }
}
