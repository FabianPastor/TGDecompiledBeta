package org.telegram.ui;

import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import org.telegram.messenger.AndroidUtilities;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ThemeActivity$UtOgQevuPsMcT5YWzA_L_XdJ6GE implements OnEditorActionListener {
    public static final /* synthetic */ -$$Lambda$ThemeActivity$UtOgQevuPsMcT5YWzA_L_XdJ6GE INSTANCE = new -$$Lambda$ThemeActivity$UtOgQevuPsMcT5YWzA_L_XdJ6GE();

    private /* synthetic */ -$$Lambda$ThemeActivity$UtOgQevuPsMcT5YWzA_L_XdJ6GE() {
    }

    public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return AndroidUtilities.hideKeyboard(textView);
    }
}
