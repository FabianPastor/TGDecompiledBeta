package org.telegram.ui.Components;

import android.content.DialogInterface;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda15 implements DialogInterface.OnClickListener {
    public final /* synthetic */ TLRPC.EncryptedChat f$0;
    public final /* synthetic */ NumberPicker f$1;

    public /* synthetic */ AlertsCreator$$ExternalSyntheticLambda15(TLRPC.EncryptedChat encryptedChat, NumberPicker numberPicker) {
        this.f$0 = encryptedChat;
        this.f$1 = numberPicker;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        AlertsCreator.lambda$createTTLAlert$86(this.f$0, this.f$1, dialogInterface, i);
    }
}
