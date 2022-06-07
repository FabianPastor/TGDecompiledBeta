package org.telegram.ui.Components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.AppIconsSelectorCell;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.LauncherIconController;

@SuppressLint({"ViewConstructor"})
public class AppIconBulletinLayout extends Bulletin.ButtonLayout {
    public final AppIconsSelectorCell.AdaptiveIconImageView imageView;
    public final TextView textView;

    public AppIconBulletinLayout(Context context, LauncherIconController.LauncherIcon launcherIcon, Theme.ResourcesProvider resourcesProvider) {
        super(context, resourcesProvider);
        AppIconsSelectorCell.AdaptiveIconImageView adaptiveIconImageView = new AppIconsSelectorCell.AdaptiveIconImageView(getContext());
        this.imageView = adaptiveIconImageView;
        TextView textView2 = new TextView(getContext());
        this.textView = textView2;
        addView(adaptiveIconImageView, LayoutHelper.createFrameRelatively(30.0f, 30.0f, 8388627, 12.0f, 8.0f, 12.0f, 8.0f));
        textView2.setGravity(8388611);
        textView2.setPadding(0, AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f));
        textView2.setTextColor(getThemedColor("undo_infoColor"));
        textView2.setTextSize(1, 15.0f);
        textView2.setTypeface(Typeface.SANS_SERIF);
        addView(textView2, LayoutHelper.createFrameRelatively(-1.0f, -2.0f, 8388627, 56.0f, 0.0f, 16.0f, 0.0f));
        adaptiveIconImageView.setImageDrawable(ContextCompat.getDrawable(context, launcherIcon.background));
        adaptiveIconImageView.setOuterPadding(AndroidUtilities.dp(8.0f));
        adaptiveIconImageView.setBackgroundOuterPadding(AndroidUtilities.dp(24.0f));
        adaptiveIconImageView.setForeground(launcherIcon.foreground);
        textView2.setText(AndroidUtilities.replaceTags(LocaleController.formatString(NUM, LocaleController.getString(launcherIcon.title))));
    }
}
