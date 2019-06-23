package org.telegram.ui.Components;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.tgnet.TLRPC.EncryptedChat;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AlertsCreator$ATMX7ChpMezQfBhnCjkFjsc8yHA implements OnClickListener {
    private final /* synthetic */ EncryptedChat f$0;
    private final /* synthetic */ NumberPicker f$1;

    public /* synthetic */ -$$Lambda$AlertsCreator$ATMX7ChpMezQfBhnCjkFjsc8yHA(EncryptedChat encryptedChat, NumberPicker numberPicker) {
        this.f$0 = encryptedChat;
        this.f$1 = numberPicker;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        AlertsCreator.lambda$createTTLAlert$41(this.f$0, this.f$1, dialogInterface, i);
    }
}
