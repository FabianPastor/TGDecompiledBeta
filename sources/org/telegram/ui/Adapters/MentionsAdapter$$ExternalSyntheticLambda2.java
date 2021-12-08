package org.telegram.ui.Adapters;

import android.content.DialogInterface;

public final /* synthetic */ class MentionsAdapter$$ExternalSyntheticLambda2 implements DialogInterface.OnDismissListener {
    public final /* synthetic */ MentionsAdapter f$0;
    public final /* synthetic */ boolean[] f$1;

    public /* synthetic */ MentionsAdapter$$ExternalSyntheticLambda2(MentionsAdapter mentionsAdapter, boolean[] zArr) {
        this.f$0 = mentionsAdapter;
        this.f$1 = zArr;
    }

    public final void onDismiss(DialogInterface dialogInterface) {
        this.f$0.m1376xecfbcccc(this.f$1, dialogInterface);
    }
}
