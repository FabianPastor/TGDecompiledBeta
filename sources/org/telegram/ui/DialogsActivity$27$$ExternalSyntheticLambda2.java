package org.telegram.ui;

import org.telegram.messenger.MessagesController;
import org.telegram.ui.Components.FiltersListBottomSheet;
import org.telegram.ui.DialogsActivity;

public final /* synthetic */ class DialogsActivity$27$$ExternalSyntheticLambda2 implements FiltersListBottomSheet.FiltersListBottomSheetDelegate {
    public final /* synthetic */ DialogsActivity.AnonymousClass27 f$0;

    public /* synthetic */ DialogsActivity$27$$ExternalSyntheticLambda2(DialogsActivity.AnonymousClass27 r1) {
        this.f$0 = r1;
    }

    public final void didSelectFilter(MessagesController.DialogFilter dialogFilter) {
        this.f$0.m2882lambda$onItemClick$2$orgtelegramuiDialogsActivity$27(dialogFilter);
    }
}
