package org.telegram.ui;

import org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;

final /* synthetic */ class MediaActivity$$Lambda$4 implements ThemeDescriptionDelegate {
    private final MediaActivity arg$1;
    private final int arg$2;

    MediaActivity$$Lambda$4(MediaActivity mediaActivity, int i) {
        this.arg$1 = mediaActivity;
        this.arg$2 = i;
    }

    public void didSetColor() {
        this.arg$1.lambda$getThemeDescriptions$4$MediaActivity(this.arg$2);
    }
}
