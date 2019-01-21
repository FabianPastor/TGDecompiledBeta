package org.telegram.messenger;

import android.view.View;
import android.view.View.OnClickListener;

final /* synthetic */ class AndroidUtilities$$Lambda$4 implements OnClickListener {
    private final String arg$1;
    private final String arg$2;
    private final String arg$3;
    private final String arg$4;
    private final String arg$5;
    private final Runnable arg$6;

    AndroidUtilities$$Lambda$4(String str, String str2, String str3, String str4, String str5, Runnable runnable) {
        this.arg$1 = str;
        this.arg$2 = str2;
        this.arg$3 = str3;
        this.arg$4 = str4;
        this.arg$5 = str5;
        this.arg$6 = runnable;
    }

    public void onClick(View view) {
        AndroidUtilities.lambda$showProxyAlert$4$AndroidUtilities(this.arg$1, this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, view);
    }
}
