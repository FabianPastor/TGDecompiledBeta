package org.telegram.ui.Adapters;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.tgnet.TLRPC.User;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MentionsAdapter$3hBy3ePtH3hOR9uoZtzoQOcheTs implements OnClickListener {
    private final /* synthetic */ MentionsAdapter f$0;
    private final /* synthetic */ boolean[] f$1;
    private final /* synthetic */ User f$2;

    public /* synthetic */ -$$Lambda$MentionsAdapter$3hBy3ePtH3hOR9uoZtzoQOcheTs(MentionsAdapter mentionsAdapter, boolean[] zArr, User user) {
        this.f$0 = mentionsAdapter;
        this.f$1 = zArr;
        this.f$2 = user;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$processFoundUser$0$MentionsAdapter(this.f$1, this.f$2, dialogInterface, i);
    }
}
