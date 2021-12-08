package org.telegram.messenger.browser;

import android.content.DialogInterface;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;

public final /* synthetic */ class Browser$$ExternalSyntheticLambda0 implements DialogInterface.OnCancelListener {
    public final /* synthetic */ int f$0;

    public /* synthetic */ Browser$$ExternalSyntheticLambda0(int i) {
        this.f$0 = i;
    }

    public final void onCancel(DialogInterface dialogInterface) {
        ConnectionsManager.getInstance(UserConfig.selectedAccount).cancelRequest(this.f$0, true);
    }
}
