package org.telegram.ui;

import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.FiltersSetupActivity;

public final /* synthetic */ class FiltersSetupActivity$ListAdapter$$ExternalSyntheticLambda7 implements RequestDelegate {
    public final /* synthetic */ FiltersSetupActivity.ListAdapter f$0;
    public final /* synthetic */ AlertDialog f$1;
    public final /* synthetic */ MessagesController.DialogFilter f$2;

    public /* synthetic */ FiltersSetupActivity$ListAdapter$$ExternalSyntheticLambda7(FiltersSetupActivity.ListAdapter listAdapter, AlertDialog alertDialog, MessagesController.DialogFilter dialogFilter) {
        this.f$0 = listAdapter;
        this.f$1 = alertDialog;
        this.f$2 = dialogFilter;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m2939x669fd6a1(this.f$1, this.f$2, tLObject, tL_error);
    }
}
