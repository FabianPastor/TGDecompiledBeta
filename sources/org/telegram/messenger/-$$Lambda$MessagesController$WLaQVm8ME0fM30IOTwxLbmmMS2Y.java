package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$WLaQVm8ME0fM30IOTwxLbmmMS2Y implements RequestDelegate {
    public static final /* synthetic */ -$$Lambda$MessagesController$WLaQVm8ME0fM30IOTwxLbmmMS2Y INSTANCE = new -$$Lambda$MessagesController$WLaQVm8ME0fM30IOTwxLbmmMS2Y();

    private /* synthetic */ -$$Lambda$MessagesController$WLaQVm8ME0fM30IOTwxLbmmMS2Y() {
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        MessagesController.lambda$markMentionsAsRead$166(tLObject, tL_error);
    }
}
