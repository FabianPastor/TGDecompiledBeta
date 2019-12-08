package org.telegram.messenger;

import drinkless.org.ton.Client;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AndroidUtilities$WThGqTP3imQHn0t9VPFazPVrxWk implements RequestDelegate {
    private final /* synthetic */ Client f$0;
    private final /* synthetic */ long f$1;

    public /* synthetic */ -$$Lambda$AndroidUtilities$WThGqTP3imQHn0t9VPFazPVrxWk(Client client, long j) {
        this.f$0 = client;
        this.f$1 = j;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.lambda$processTonUpdate$8(this.f$0, this.f$1, tLObject, tL_error);
    }
}
