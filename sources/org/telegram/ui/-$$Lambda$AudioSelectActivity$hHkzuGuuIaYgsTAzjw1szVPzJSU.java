package org.telegram.ui;

import org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AudioSelectActivity$hHkzuGuuIaYgsTAzjw1szVPzJSU implements ScheduleDatePickerDelegate {
    private final /* synthetic */ AudioSelectActivity f$0;

    public /* synthetic */ -$$Lambda$AudioSelectActivity$hHkzuGuuIaYgsTAzjw1szVPzJSU(AudioSelectActivity audioSelectActivity) {
        this.f$0 = audioSelectActivity;
    }

    public final void didSelectDate(boolean z, int i) {
        this.f$0.sendSelectedAudios(z, i);
    }
}
