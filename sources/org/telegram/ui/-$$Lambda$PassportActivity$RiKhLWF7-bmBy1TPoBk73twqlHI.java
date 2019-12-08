package org.telegram.ui;

import android.view.View;
import android.view.View.OnLongClickListener;
import org.telegram.messenger.SecureDocument;
import org.telegram.ui.PassportActivity.SecureDocumentCell;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PassportActivity$RiKhLWF7-bmBy1TPoBk73twqlHI implements OnLongClickListener {
    private final /* synthetic */ PassportActivity f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ SecureDocument f$2;
    private final /* synthetic */ SecureDocumentCell f$3;
    private final /* synthetic */ String f$4;

    public /* synthetic */ -$$Lambda$PassportActivity$RiKhLWF7-bmBy1TPoBk73twqlHI(PassportActivity passportActivity, int i, SecureDocument secureDocument, SecureDocumentCell secureDocumentCell, String str) {
        this.f$0 = passportActivity;
        this.f$1 = i;
        this.f$2 = secureDocument;
        this.f$3 = secureDocumentCell;
        this.f$4 = str;
    }

    public final boolean onLongClick(View view) {
        return this.f$0.lambda$addDocumentView$57$PassportActivity(this.f$1, this.f$2, this.f$3, this.f$4, view);
    }
}
