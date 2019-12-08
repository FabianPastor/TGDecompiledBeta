package org.telegram.ui.Components;

import org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivityEnterView$HYedsDwyi9KUQvm0fCLASSNAMEO6pzfJ0 implements ScheduleDatePickerDelegate {
    private final /* synthetic */ ChatActivityEnterView f$0;

    public /* synthetic */ -$$Lambda$ChatActivityEnterView$HYedsDwyi9KUQvm0fCLASSNAMEO6pzfJ0(ChatActivityEnterView chatActivityEnterView) {
        this.f$0 = chatActivityEnterView;
    }

    public final void didSelectDate(boolean z, int i) {
        this.f$0.sendMessageInternal(z, i);
    }
}
