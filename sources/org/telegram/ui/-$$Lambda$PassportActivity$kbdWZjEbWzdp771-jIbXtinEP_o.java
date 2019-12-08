package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.tgnet.TLRPC.TL_secureRequiredType;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PassportActivity$kbdWZjEbWzdp771-jIbXtinEP_o implements OnClickListener {
    private final /* synthetic */ PassportActivity f$0;
    private final /* synthetic */ TL_secureRequiredType f$1;
    private final /* synthetic */ boolean f$2;

    public /* synthetic */ -$$Lambda$PassportActivity$kbdWZjEbWzdp771-jIbXtinEP_o(PassportActivity passportActivity, TL_secureRequiredType tL_secureRequiredType, boolean z) {
        this.f$0 = passportActivity;
        this.f$1 = tL_secureRequiredType;
        this.f$2 = z;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$null$63$PassportActivity(this.f$1, this.f$2, dialogInterface, i);
    }
}
