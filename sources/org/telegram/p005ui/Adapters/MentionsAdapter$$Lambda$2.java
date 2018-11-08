package org.telegram.p005ui.Adapters;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;

/* renamed from: org.telegram.ui.Adapters.MentionsAdapter$$Lambda$2 */
final /* synthetic */ class MentionsAdapter$$Lambda$2 implements OnDismissListener {
    private final MentionsAdapter arg$1;
    private final boolean[] arg$2;

    MentionsAdapter$$Lambda$2(MentionsAdapter mentionsAdapter, boolean[] zArr) {
        this.arg$1 = mentionsAdapter;
        this.arg$2 = zArr;
    }

    public void onDismiss(DialogInterface dialogInterface) {
        this.arg$1.lambda$processFoundUser$2$MentionsAdapter(this.arg$2, dialogInterface);
    }
}
