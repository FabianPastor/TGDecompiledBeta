package org.telegram.messenger.browser;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$Browser$gkq8pxwFDBSthMGmf2aFQ3i1KTE implements OnCancelListener {
    private final /* synthetic */ int f$0;

    public /* synthetic */ -$$Lambda$Browser$gkq8pxwFDBSthMGmf2aFQ3i1KTE(int i) {
        this.f$0 = i;
    }

    public final void onCancel(DialogInterface dialogInterface) {
        ConnectionsManager.getInstance(UserConfig.selectedAccount).cancelRequest(this.f$0, true);
    }
}
