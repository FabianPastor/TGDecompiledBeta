package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.tgnet.TLRPC.TL_inputPhoneContact;
import org.telegram.ui.NewContactActivity.AnonymousClass1;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$NewContactActivity$1$D3bcNIiNfpYsfTq2W2DLeJJEU84 implements OnClickListener {
    private final /* synthetic */ AnonymousClass1 f$0;
    private final /* synthetic */ TL_inputPhoneContact f$1;

    public /* synthetic */ -$$Lambda$NewContactActivity$1$D3bcNIiNfpYsfTq2W2DLeJJEU84(AnonymousClass1 anonymousClass1, TL_inputPhoneContact tL_inputPhoneContact) {
        this.f$0 = anonymousClass1;
        this.f$1 = tL_inputPhoneContact;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$null$0$NewContactActivity$1(this.f$1, dialogInterface, i);
    }
}
