package org.telegram.ui;

import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.TLRPC$TL_dialogFilterSuggested;
import org.telegram.ui.FiltersSetupActivity;

public final /* synthetic */ class FiltersSetupActivity$ListAdapter$$ExternalSyntheticLambda5 implements Runnable {
    public final /* synthetic */ FiltersSetupActivity.ListAdapter f$0;
    public final /* synthetic */ TLRPC$TL_dialogFilterSuggested f$1;
    public final /* synthetic */ MessagesController.DialogFilter f$2;

    public /* synthetic */ FiltersSetupActivity$ListAdapter$$ExternalSyntheticLambda5(FiltersSetupActivity.ListAdapter listAdapter, TLRPC$TL_dialogFilterSuggested tLRPC$TL_dialogFilterSuggested, MessagesController.DialogFilter dialogFilter) {
        this.f$0 = listAdapter;
        this.f$1 = tLRPC$TL_dialogFilterSuggested;
        this.f$2 = dialogFilter;
    }

    public final void run() {
        this.f$0.lambda$onCreateViewHolder$6(this.f$1, this.f$2);
    }
}
