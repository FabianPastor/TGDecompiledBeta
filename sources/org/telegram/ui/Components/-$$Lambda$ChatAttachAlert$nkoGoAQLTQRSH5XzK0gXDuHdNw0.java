package org.telegram.ui.Components;

import org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatAttachAlert$nkoGoAQLTQRSH5XzK0gXDuHdNw0 implements ScheduleDatePickerDelegate {
    private final /* synthetic */ ChatAttachAlert f$0;

    public /* synthetic */ -$$Lambda$ChatAttachAlert$nkoGoAQLTQRSH5XzK0gXDuHdNw0(ChatAttachAlert chatAttachAlert) {
        this.f$0 = chatAttachAlert;
    }

    public final void didSelectDate(boolean z, int i) {
        this.f$0.sendPressed(z, i);
    }
}
