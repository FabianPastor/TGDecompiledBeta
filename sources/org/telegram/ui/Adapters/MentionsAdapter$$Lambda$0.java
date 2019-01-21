package org.telegram.ui.Adapters;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.tgnet.TLRPC.User;

final /* synthetic */ class MentionsAdapter$$Lambda$0 implements OnClickListener {
    private final MentionsAdapter arg$1;
    private final boolean[] arg$2;
    private final User arg$3;

    MentionsAdapter$$Lambda$0(MentionsAdapter mentionsAdapter, boolean[] zArr, User user) {
        this.arg$1 = mentionsAdapter;
        this.arg$2 = zArr;
        this.arg$3 = user;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$processFoundUser$0$MentionsAdapter(this.arg$2, this.arg$3, dialogInterface, i);
    }
}
