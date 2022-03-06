package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.ui.Components.AlertsCreator;

public final /* synthetic */ class DialogsActivity$$ExternalSyntheticLambda46 implements AlertsCreator.BlockDialogCallback {
    public final /* synthetic */ DialogsActivity f$0;
    public final /* synthetic */ ArrayList f$1;

    public /* synthetic */ DialogsActivity$$ExternalSyntheticLambda46(DialogsActivity dialogsActivity, ArrayList arrayList) {
        this.f$0 = dialogsActivity;
        this.f$1 = arrayList;
    }

    public final void run(boolean z, boolean z2) {
        this.f$0.lambda$performSelectedDialogsAction$29(this.f$1, z, z2);
    }
}
