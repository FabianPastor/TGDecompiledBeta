package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.ui.ChatActivity.ChatActivityAdapter.AnonymousClass1;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivity$ChatActivityAdapter$1$mvBU2f0l49KzASBTxcsB_CaLwnI implements OnClickListener {
    private final /* synthetic */ AnonymousClass1 f$0;
    private final /* synthetic */ String f$1;

    public /* synthetic */ -$$Lambda$ChatActivity$ChatActivityAdapter$1$mvBU2f0l49KzASBTxcsB_CaLwnI(AnonymousClass1 anonymousClass1, String str) {
        this.f$0 = anonymousClass1;
        this.f$1 = str;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$didPressUrl$0$ChatActivity$ChatActivityAdapter$1(this.f$1, dialogInterface, i);
    }
}
