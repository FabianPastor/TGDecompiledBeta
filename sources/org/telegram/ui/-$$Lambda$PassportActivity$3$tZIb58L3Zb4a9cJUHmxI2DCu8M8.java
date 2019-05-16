package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.ui.PassportActivity.AnonymousClass3;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PassportActivity$3$tZIb58L3Zb4a9cJUHmxI2DCu8M8 implements OnClickListener {
    private final /* synthetic */ AnonymousClass3 f$0;
    private final /* synthetic */ int f$1;

    public /* synthetic */ -$$Lambda$PassportActivity$3$tZIb58L3Zb4a9cJUHmxI2DCu8M8(AnonymousClass3 anonymousClass3, int i) {
        this.f$0 = anonymousClass3;
        this.f$1 = i;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$onIdentityDone$1$PassportActivity$3(this.f$1, dialogInterface, i);
    }
}
