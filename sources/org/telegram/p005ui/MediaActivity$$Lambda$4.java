package org.telegram.p005ui;

import org.telegram.p005ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;

/* renamed from: org.telegram.ui.MediaActivity$$Lambda$4 */
final /* synthetic */ class MediaActivity$$Lambda$4 implements ThemeDescriptionDelegate {
    private final MediaActivity arg$1;
    private final int arg$2;

    MediaActivity$$Lambda$4(MediaActivity mediaActivity, int i) {
        this.arg$1 = mediaActivity;
        this.arg$2 = i;
    }

    public void didSetColor() {
        this.arg$1.lambda$getThemeDescriptions$5$MediaActivity(this.arg$2);
    }
}
