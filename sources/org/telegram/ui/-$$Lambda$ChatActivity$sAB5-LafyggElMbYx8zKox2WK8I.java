package org.telegram.ui;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivity$sAB5-LafyggElMbYx8zKox2WK8I implements RequestDelegate {
    public static final /* synthetic */ -$$Lambda$ChatActivity$sAB5-LafyggElMbYx8zKox2WK8I INSTANCE = new -$$Lambda$ChatActivity$sAB5-LafyggElMbYx8zKox2WK8I();

    private /* synthetic */ -$$Lambda$ChatActivity$sAB5-LafyggElMbYx8zKox2WK8I() {
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$ChatActivity$sT9iz8nwXzbqpunvnyq4JjBxjAw(tLObject));
    }
}
