package org.telegram.ui.Components;

import android.content.DialogInterface;
import org.telegram.tgnet.TLRPC$EncryptedChat;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda18 implements DialogInterface.OnClickListener {
    public final /* synthetic */ TLRPC$EncryptedChat f$0;
    public final /* synthetic */ NumberPicker f$1;

    public /* synthetic */ AlertsCreator$$ExternalSyntheticLambda18(TLRPC$EncryptedChat tLRPC$EncryptedChat, NumberPicker numberPicker) {
        this.f$0 = tLRPC$EncryptedChat;
        this.f$1 = numberPicker;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        AlertsCreator.lambda$createTTLAlert$88(this.f$0, this.f$1, dialogInterface, i);
    }
}
