package org.telegram.p005ui;

import android.view.View;
import android.view.View.OnClickListener;
import org.telegram.p005ui.MediaActivity.CLASSNAME;

/* renamed from: org.telegram.ui.MediaActivity$4$$Lambda$0 */
final /* synthetic */ class MediaActivity$4$$Lambda$0 implements OnClickListener {
    private final boolean[] arg$1;

    MediaActivity$4$$Lambda$0(boolean[] zArr) {
        this.arg$1 = zArr;
    }

    public void onClick(View view) {
        CLASSNAME.lambda$onItemClick$0$MediaActivity$4(this.arg$1, view);
    }
}
