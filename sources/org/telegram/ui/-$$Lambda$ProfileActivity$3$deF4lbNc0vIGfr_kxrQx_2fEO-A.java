package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import org.telegram.ui.ProfileActivity.AnonymousClass3;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ProfileActivity$3$deF4lbNc0vIGfr_kxrQx_2fEO-A implements OnCancelListener {
    private final /* synthetic */ AnonymousClass3 f$0;
    private final /* synthetic */ int f$1;

    public /* synthetic */ -$$Lambda$ProfileActivity$3$deF4lbNc0vIGfr_kxrQx_2fEO-A(AnonymousClass3 anonymousClass3, int i) {
        this.f$0 = anonymousClass3;
        this.f$1 = i;
    }

    public final void onCancel(DialogInterface dialogInterface) {
        this.f$0.lambda$onItemClick$6$ProfileActivity$3(this.f$1, dialogInterface);
    }
}
