package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.ui.ChatActivity.ChatActivityAdapter.AnonymousClass1;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivity$ChatActivityAdapter$1$PQUAU82GV4yYs4_Ri00P3NgllDc implements OnClickListener {
    private final /* synthetic */ AnonymousClass1 f$0;
    private final /* synthetic */ String f$1;

    public /* synthetic */ -$$Lambda$ChatActivity$ChatActivityAdapter$1$PQUAU82GV4yYs4_Ri00P3NgllDc(AnonymousClass1 anonymousClass1, String str) {
        this.f$0 = anonymousClass1;
        this.f$1 = str;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$didPressUrl$1$ChatActivity$ChatActivityAdapter$1(this.f$1, dialogInterface, i);
    }
}
