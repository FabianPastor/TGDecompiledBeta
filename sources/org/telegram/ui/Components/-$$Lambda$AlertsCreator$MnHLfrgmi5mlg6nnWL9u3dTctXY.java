package org.telegram.ui.Components;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.messenger.MessagesStorage.BooleanCallback;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.BaseFragment;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AlertsCreator$MnHLfrgmi5mlg6nnWL9u3dTctXY implements OnClickListener {
    private final /* synthetic */ User f$0;
    private final /* synthetic */ boolean f$1;
    private final /* synthetic */ boolean f$2;
    private final /* synthetic */ boolean[] f$3;
    private final /* synthetic */ BaseFragment f$4;
    private final /* synthetic */ boolean f$5;
    private final /* synthetic */ boolean f$6;
    private final /* synthetic */ Chat f$7;
    private final /* synthetic */ boolean f$8;
    private final /* synthetic */ BooleanCallback f$9;

    public /* synthetic */ -$$Lambda$AlertsCreator$MnHLfrgmi5mlg6nnWL9u3dTctXY(User user, boolean z, boolean z2, boolean[] zArr, BaseFragment baseFragment, boolean z3, boolean z4, Chat chat, boolean z5, BooleanCallback booleanCallback) {
        this.f$0 = user;
        this.f$1 = z;
        this.f$2 = z2;
        this.f$3 = zArr;
        this.f$4 = baseFragment;
        this.f$5 = z3;
        this.f$6 = z4;
        this.f$7 = chat;
        this.f$8 = z5;
        this.f$9 = booleanCallback;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        AlertsCreator.lambda$createClearOrDeleteDialogAlert$13(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, dialogInterface, i);
    }
}
