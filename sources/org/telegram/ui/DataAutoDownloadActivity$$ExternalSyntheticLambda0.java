package org.telegram.ui;

import android.view.View;
import org.telegram.ui.ActionBar.BottomSheet;

public final /* synthetic */ class DataAutoDownloadActivity$$ExternalSyntheticLambda0 implements View.OnClickListener {
    public final /* synthetic */ BottomSheet.Builder f$0;

    public /* synthetic */ DataAutoDownloadActivity$$ExternalSyntheticLambda0(BottomSheet.Builder builder) {
        this.f$0 = builder;
    }

    public final void onClick(View view) {
        this.f$0.getDismissRunnable().run();
    }
}
