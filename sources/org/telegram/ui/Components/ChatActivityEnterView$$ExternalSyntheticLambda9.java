package org.telegram.ui.Components;

import android.content.DialogInterface;

public final /* synthetic */ class ChatActivityEnterView$$ExternalSyntheticLambda9 implements DialogInterface.OnClickListener {
    public final /* synthetic */ ChatActivityEnterView f$0;
    public final /* synthetic */ Runnable f$1;
    public final /* synthetic */ long f$2;

    public /* synthetic */ ChatActivityEnterView$$ExternalSyntheticLambda9(ChatActivityEnterView chatActivityEnterView, Runnable runnable, long j) {
        this.f$0 = chatActivityEnterView;
        this.f$1 = runnable;
        this.f$2 = j;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$didPressedBotButton$47(this.f$1, this.f$2, dialogInterface, i);
    }
}
