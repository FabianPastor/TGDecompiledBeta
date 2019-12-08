package org.telegram.ui.Components;

import android.view.View;
import android.view.View.OnClickListener;
import org.telegram.ui.ActionBar.BottomSheet.Builder;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$BiometricPromtHelper$R_6nsmpBN5MdmARTMuTi25WvKIw implements OnClickListener {
    private final /* synthetic */ Builder f$0;

    public /* synthetic */ -$$Lambda$BiometricPromtHelper$R_6nsmpBN5MdmARTMuTi25WvKIw(Builder builder) {
        this.f$0 = builder;
    }

    public final void onClick(View view) {
        this.f$0.getDismissRunnable().run();
    }
}
