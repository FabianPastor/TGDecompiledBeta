package org.telegram.ui;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivity$n4HarHZ_JY6HBR1whyHld2Ayx08 implements RequestDelegate {
    public static final /* synthetic */ -$$Lambda$ChatActivity$n4HarHZ_JY6HBR1whyHld2Ayx08 INSTANCE = new -$$Lambda$ChatActivity$n4HarHZ_JY6HBR1whyHld2Ayx08();

    private /* synthetic */ -$$Lambda$ChatActivity$n4HarHZ_JY6HBR1whyHld2Ayx08() {
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$ChatActivity$NbVSYxX7JxoNrPr-Up1tHj-l9cc(tLObject));
    }
}
