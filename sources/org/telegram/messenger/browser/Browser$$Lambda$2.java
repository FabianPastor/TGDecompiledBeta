package org.telegram.messenger.browser;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;

final /* synthetic */ class Browser$$Lambda$2 implements OnCancelListener {
    private final int arg$1;

    Browser$$Lambda$2(int i) {
        this.arg$1 = i;
    }

    public void onCancel(DialogInterface dialogInterface) {
        ConnectionsManager.getInstance(UserConfig.selectedAccount).cancelRequest(this.arg$1, true);
    }
}
