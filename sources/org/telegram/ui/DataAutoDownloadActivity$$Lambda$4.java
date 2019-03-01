package org.telegram.ui;

import android.view.View;
import android.view.View.OnClickListener;
import org.telegram.ui.ActionBar.BottomSheet.Builder;

final /* synthetic */ class DataAutoDownloadActivity$$Lambda$4 implements OnClickListener {
    private final Builder arg$1;

    DataAutoDownloadActivity$$Lambda$4(Builder builder) {
        this.arg$1 = builder;
    }

    public void onClick(View view) {
        this.arg$1.getDismissRunnable().run();
    }
}
