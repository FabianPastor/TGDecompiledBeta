package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.ui.PassportActivity.AnonymousClass3;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PassportActivity$3$hBvwZ-d4QGDnNuXdFnmSB9952Bs implements OnClickListener {
    private final /* synthetic */ AnonymousClass3 f$0;
    private final /* synthetic */ String f$1;
    private final /* synthetic */ String f$2;
    private final /* synthetic */ String f$3;
    private final /* synthetic */ Runnable f$4;
    private final /* synthetic */ ErrorRunnable f$5;

    public /* synthetic */ -$$Lambda$PassportActivity$3$hBvwZ-d4QGDnNuXdFnmSB9952Bs(AnonymousClass3 anonymousClass3, String str, String str2, String str3, Runnable runnable, ErrorRunnable errorRunnable) {
        this.f$0 = anonymousClass3;
        this.f$1 = str;
        this.f$2 = str2;
        this.f$3 = str3;
        this.f$4 = runnable;
        this.f$5 = errorRunnable;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$onIdentityDone$0$PassportActivity$3(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, dialogInterface, i);
    }
}
