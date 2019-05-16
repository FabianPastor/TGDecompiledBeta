package org.telegram.ui.Components;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.tgnet.TLRPC.EncryptedChat;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AlertsCreator$aIsTuran9wL7MnwA9zbq1Azf_9g implements OnClickListener {
    private final /* synthetic */ EncryptedChat f$0;
    private final /* synthetic */ NumberPicker f$1;

    public /* synthetic */ -$$Lambda$AlertsCreator$aIsTuran9wL7MnwA9zbq1Azf_9g(EncryptedChat encryptedChat, NumberPicker numberPicker) {
        this.f$0 = encryptedChat;
        this.f$1 = numberPicker;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        AlertsCreator.lambda$createTTLAlert$38(this.f$0, this.f$1, dialogInterface, i);
    }
}
