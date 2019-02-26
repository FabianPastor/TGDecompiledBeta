package org.telegram.ui.Components;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.tgnet.TLRPC.EncryptedChat;

final /* synthetic */ class AlertsCreator$$Lambda$34 implements OnClickListener {
    private final EncryptedChat arg$1;
    private final NumberPicker arg$2;

    AlertsCreator$$Lambda$34(EncryptedChat encryptedChat, NumberPicker numberPicker) {
        this.arg$1 = encryptedChat;
        this.arg$2 = numberPicker;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        AlertsCreator.lambda$createTTLAlert$37$AlertsCreator(this.arg$1, this.arg$2, dialogInterface, i);
    }
}
