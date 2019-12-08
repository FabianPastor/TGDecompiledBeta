package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.ui.LoginActivity.PhoneView;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LoginActivity$PhoneView$dpjhAtsGXirSFaKNuV4Bo6d-RDQ implements OnClickListener {
    private final /* synthetic */ PhoneView f$0;
    private final /* synthetic */ int f$1;

    public /* synthetic */ -$$Lambda$LoginActivity$PhoneView$dpjhAtsGXirSFaKNuV4Bo6d-RDQ(PhoneView phoneView, int i) {
        this.f$0 = phoneView;
        this.f$1 = i;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$onNextPressed$8$LoginActivity$PhoneView(this.f$1, dialogInterface, i);
    }
}
