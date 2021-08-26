package org.telegram.ui.Components;

import org.telegram.ui.ActionBar.ThemeDescription;

public final /* synthetic */ class PollVotesAlert$$ExternalSyntheticLambda5 implements ThemeDescription.ThemeDescriptionDelegate {
    public final /* synthetic */ PollVotesAlert f$0;

    public /* synthetic */ PollVotesAlert$$ExternalSyntheticLambda5(PollVotesAlert pollVotesAlert) {
        this.f$0 = pollVotesAlert;
    }

    public final void didSetColor() {
        this.f$0.updatePlaceholder();
    }
}
