package org.telegram.ui;

import android.view.View;
import org.telegram.messenger.MessagesController;
import org.telegram.ui.DialogsActivity;

public final /* synthetic */ class DialogsActivity$6$$ExternalSyntheticLambda1 implements View.OnClickListener {
    public final /* synthetic */ DialogsActivity.AnonymousClass6 f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ MessagesController.DialogFilter f$3;

    public /* synthetic */ DialogsActivity$6$$ExternalSyntheticLambda1(DialogsActivity.AnonymousClass6 r1, int i, int i2, MessagesController.DialogFilter dialogFilter) {
        this.f$0 = r1;
        this.f$1 = i;
        this.f$2 = i2;
        this.f$3 = dialogFilter;
    }

    public final void onClick(View view) {
        this.f$0.m2124lambda$didSelectTab$4$orgtelegramuiDialogsActivity$6(this.f$1, this.f$2, this.f$3, view);
    }
}
