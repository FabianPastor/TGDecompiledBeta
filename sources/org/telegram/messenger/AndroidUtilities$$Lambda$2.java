package org.telegram.messenger;

import android.view.View;
import android.view.View.OnClickListener;

final /* synthetic */ class AndroidUtilities$$Lambda$2 implements OnClickListener {
    private final Runnable arg$1;

    AndroidUtilities$$Lambda$2(Runnable runnable) {
        this.arg$1 = runnable;
    }

    public void onClick(View view) {
        this.arg$1.run();
    }
}
