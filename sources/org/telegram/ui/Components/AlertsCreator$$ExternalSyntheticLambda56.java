package org.telegram.ui.Components;

import android.content.DialogInterface;
import org.telegram.messenger.MessagesStorage;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda56 implements DialogInterface.OnClickListener {
    public final /* synthetic */ boolean f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ MessagesStorage.BooleanCallback f$10;
    public final /* synthetic */ Theme.ResourcesProvider f$11;
    public final /* synthetic */ boolean[] f$12;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ TLRPC.User f$3;
    public final /* synthetic */ BaseFragment f$4;
    public final /* synthetic */ boolean f$5;
    public final /* synthetic */ boolean f$6;
    public final /* synthetic */ TLRPC.Chat f$7;
    public final /* synthetic */ boolean f$8;
    public final /* synthetic */ boolean f$9;

    public /* synthetic */ AlertsCreator$$ExternalSyntheticLambda56(boolean z, boolean z2, boolean z3, TLRPC.User user, BaseFragment baseFragment, boolean z4, boolean z5, TLRPC.Chat chat, boolean z6, boolean z7, MessagesStorage.BooleanCallback booleanCallback, Theme.ResourcesProvider resourcesProvider, boolean[] zArr) {
        this.f$0 = z;
        this.f$1 = z2;
        this.f$2 = z3;
        this.f$3 = user;
        this.f$4 = baseFragment;
        this.f$5 = z4;
        this.f$6 = z5;
        this.f$7 = chat;
        this.f$8 = z6;
        this.f$9 = z7;
        this.f$10 = booleanCallback;
        this.f$11 = resourcesProvider;
        this.f$12 = zArr;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        AlertsCreator.lambda$createClearOrDeleteDialogAlert$25(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11, this.f$12, dialogInterface, i);
    }
}
