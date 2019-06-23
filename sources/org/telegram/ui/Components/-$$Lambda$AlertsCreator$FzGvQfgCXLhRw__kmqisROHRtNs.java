package org.telegram.ui.Components;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.MessagesStorage.IntCallback;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.Cells.CheckBoxCell;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AlertsCreator$FzGvQfgCXLhRw__kmqisROHRtNs implements OnClickListener {
    private final /* synthetic */ User f$0;
    private final /* synthetic */ AccountInstance f$1;
    private final /* synthetic */ CheckBoxCell[] f$2;
    private final /* synthetic */ long f$3;
    private final /* synthetic */ Chat f$4;
    private final /* synthetic */ EncryptedChat f$5;
    private final /* synthetic */ boolean f$6;
    private final /* synthetic */ IntCallback f$7;

    public /* synthetic */ -$$Lambda$AlertsCreator$FzGvQfgCXLhRw__kmqisROHRtNs(User user, AccountInstance accountInstance, CheckBoxCell[] checkBoxCellArr, long j, Chat chat, EncryptedChat encryptedChat, boolean z, IntCallback intCallback) {
        this.f$0 = user;
        this.f$1 = accountInstance;
        this.f$2 = checkBoxCellArr;
        this.f$3 = j;
        this.f$4 = chat;
        this.f$5 = encryptedChat;
        this.f$6 = z;
        this.f$7 = intCallback;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        AlertsCreator.lambda$showBlockReportSpamAlert$4(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, dialogInterface, i);
    }
}
