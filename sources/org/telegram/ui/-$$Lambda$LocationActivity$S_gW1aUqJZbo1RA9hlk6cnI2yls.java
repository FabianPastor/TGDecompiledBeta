package org.telegram.ui;

import org.telegram.tgnet.TLRPC.TL_messageMediaGeo;
import org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LocationActivity$S_gW1aUqJZbo1RA9hlk6cnI2yls implements ScheduleDatePickerDelegate {
    private final /* synthetic */ LocationActivity f$0;
    private final /* synthetic */ TL_messageMediaGeo f$1;

    public /* synthetic */ -$$Lambda$LocationActivity$S_gW1aUqJZbo1RA9hlk6cnI2yls(LocationActivity locationActivity, TL_messageMediaGeo tL_messageMediaGeo) {
        this.f$0 = locationActivity;
        this.f$1 = tL_messageMediaGeo;
    }

    public final void didSelectDate(boolean z, int i) {
        this.f$0.lambda$null$3$LocationActivity(this.f$1, z, i);
    }
}