package org.telegram.ui.Components;

import org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PollVotesAlert$duMvUriWRtWBbdEJKBEaIhPp5ng implements ThemeDescriptionDelegate {
    private final /* synthetic */ PollVotesAlert f$0;

    public /* synthetic */ -$$Lambda$PollVotesAlert$duMvUriWRtWBbdEJKBEaIhPp5ng(PollVotesAlert pollVotesAlert) {
        this.f$0 = pollVotesAlert;
    }

    public final void didSetColor() {
        this.f$0.updatePlaceholder();
    }
}
