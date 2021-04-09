package org.telegram.ui.Components;

import android.view.KeyEvent;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;

/* renamed from: org.telegram.ui.Components.-$$Lambda$AlertsCreator$ZgMxl23nenNha4qfoLubvhV6Gu0  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$AlertsCreator$ZgMxl23nenNha4qfoLubvhV6Gu0 implements TextView.OnEditorActionListener {
    public static final /* synthetic */ $$Lambda$AlertsCreator$ZgMxl23nenNha4qfoLubvhV6Gu0 INSTANCE = new $$Lambda$AlertsCreator$ZgMxl23nenNha4qfoLubvhV6Gu0();

    private /* synthetic */ $$Lambda$AlertsCreator$ZgMxl23nenNha4qfoLubvhV6Gu0() {
    }

    public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return AndroidUtilities.hideKeyboard(textView);
    }
}
