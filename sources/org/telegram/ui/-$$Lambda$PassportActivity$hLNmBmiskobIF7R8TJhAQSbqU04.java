package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.tgnet.TLRPC.TL_auth_passwordRecovery;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PassportActivity$hLNmBmiskobIF7R8TJhAQSbqU04 implements OnClickListener {
    private final /* synthetic */ PassportActivity f$0;
    private final /* synthetic */ TL_auth_passwordRecovery f$1;

    public /* synthetic */ -$$Lambda$PassportActivity$hLNmBmiskobIF7R8TJhAQSbqU04(PassportActivity passportActivity, TL_auth_passwordRecovery tL_auth_passwordRecovery) {
        this.f$0 = passportActivity;
        this.f$1 = tL_auth_passwordRecovery;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$null$8$PassportActivity(this.f$1, dialogInterface, i);
    }
}
