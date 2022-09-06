package org.telegram.ui.Components;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.core.content.ContextCompat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.Bulletin;

@SuppressLint({"ViewConstructor"})
public class SelectSendAsPremiumHintBulletinLayout extends Bulletin.MultiLineLayout {
    public SelectSendAsPremiumHintBulletinLayout(Context context, Theme.ResourcesProvider resourcesProvider, Runnable runnable) {
        super(context, resourcesProvider);
        this.imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.msg_premium_prolfilestar));
        this.textView.setText(AndroidUtilities.replaceTags(LocaleController.getString(R.string.SelectSendAsPeerPremiumHint)));
        Bulletin.UndoButton undoButton = new Bulletin.UndoButton(context, true, resourcesProvider);
        undoButton.setText(LocaleController.getString(R.string.SelectSendAsPeerPremiumOpen));
        undoButton.setUndoAction(runnable);
        setButton(undoButton);
    }
}
