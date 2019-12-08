package org.telegram.ui.Components;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.tgnet.TLRPC.EncryptedChat;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AlertsCreator$Ky57ZWK-GIOKEvj3_MJdqhC5X9o implements OnClickListener {
    private final /* synthetic */ EncryptedChat f$0;
    private final /* synthetic */ NumberPicker f$1;

    public /* synthetic */ -$$Lambda$AlertsCreator$Ky57ZWK-GIOKEvj3_MJdqhC5X9o(EncryptedChat encryptedChat, NumberPicker numberPicker) {
        this.f$0 = encryptedChat;
        this.f$1 = numberPicker;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        AlertsCreator.lambda$createTTLAlert$48(this.f$0, this.f$1, dialogInterface, i);
    }
}
