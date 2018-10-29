package org.telegram.ui.Adapters;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

final /* synthetic */ class MentionsAdapter$$Lambda$1 implements OnClickListener {
    private final MentionsAdapter arg$1;
    private final boolean[] arg$2;

    MentionsAdapter$$Lambda$1(MentionsAdapter mentionsAdapter, boolean[] zArr) {
        this.arg$1 = mentionsAdapter;
        this.arg$2 = zArr;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$processFoundUser$1$MentionsAdapter(this.arg$2, dialogInterface, i);
    }
}
