package org.telegram.ui.Components;

import android.view.KeyEvent;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;

/* renamed from: org.telegram.ui.Components.-$$Lambda$AlertsCreator$izh5T8NT1a5Hti3yAlqajT0QYLQ  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$AlertsCreator$izh5T8NT1a5Hti3yAlqajT0QYLQ implements TextView.OnEditorActionListener {
    public static final /* synthetic */ $$Lambda$AlertsCreator$izh5T8NT1a5Hti3yAlqajT0QYLQ INSTANCE = new $$Lambda$AlertsCreator$izh5T8NT1a5Hti3yAlqajT0QYLQ();

    private /* synthetic */ $$Lambda$AlertsCreator$izh5T8NT1a5Hti3yAlqajT0QYLQ() {
    }

    public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return AndroidUtilities.hideKeyboard(textView);
    }
}
