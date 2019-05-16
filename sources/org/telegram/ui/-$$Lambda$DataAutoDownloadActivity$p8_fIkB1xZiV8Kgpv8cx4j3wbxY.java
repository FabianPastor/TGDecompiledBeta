package org.telegram.ui;

import android.view.View;
import android.view.View.OnClickListener;
import org.telegram.ui.ActionBar.BottomSheet.Builder;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DataAutoDownloadActivity$p8_fIkB1xZiV8Kgpv8cx4j3wbxY implements OnClickListener {
    private final /* synthetic */ Builder f$0;

    public /* synthetic */ -$$Lambda$DataAutoDownloadActivity$p8_fIkB1xZiV8Kgpv8cx4j3wbxY(Builder builder) {
        this.f$0 = builder;
    }

    public final void onClick(View view) {
        this.f$0.getDismissRunnable().run();
    }
}
