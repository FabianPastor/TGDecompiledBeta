package org.telegram.ui.Components;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.util.SparseArray;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessageObject.GroupedMessages;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.User;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AlertsCreator$OgrqERKmCgdU4Q6tzVkBNsq-WCY implements OnClickListener {
    private final /* synthetic */ MessageObject f$0;
    private final /* synthetic */ GroupedMessages f$1;
    private final /* synthetic */ Runnable f$10;
    private final /* synthetic */ EncryptedChat f$2;
    private final /* synthetic */ int f$3;
    private final /* synthetic */ boolean[] f$4;
    private final /* synthetic */ SparseArray[] f$5;
    private final /* synthetic */ User f$6;
    private final /* synthetic */ boolean[] f$7;
    private final /* synthetic */ Chat f$8;
    private final /* synthetic */ ChatFull f$9;

    public /* synthetic */ -$$Lambda$AlertsCreator$OgrqERKmCgdU4Q6tzVkBNsq-WCY(MessageObject messageObject, GroupedMessages groupedMessages, EncryptedChat encryptedChat, int i, boolean[] zArr, SparseArray[] sparseArrayArr, User user, boolean[] zArr2, Chat chat, ChatFull chatFull, Runnable runnable) {
        this.f$0 = messageObject;
        this.f$1 = groupedMessages;
        this.f$2 = encryptedChat;
        this.f$3 = i;
        this.f$4 = zArr;
        this.f$5 = sparseArrayArr;
        this.f$6 = user;
        this.f$7 = zArr2;
        this.f$8 = chat;
        this.f$9 = chatFull;
        this.f$10 = runnable;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        AlertsCreator.lambda$createDeleteMessagesAlert$49(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, dialogInterface, i);
    }
}
