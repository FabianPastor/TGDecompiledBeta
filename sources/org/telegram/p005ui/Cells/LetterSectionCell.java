package org.telegram.p005ui.Cells;

import android.content.Context;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.p005ui.ActionBar.Theme;
import org.telegram.p005ui.Components.LayoutHelper;

/* renamed from: org.telegram.ui.Cells.LetterSectionCell */
public class LetterSectionCell extends FrameLayout {
    private TextView textView = new TextView(getContext());

    public LetterSectionCell(Context context) {
        super(context);
        setLayoutParams(new LayoutParams(AndroidUtilities.m9dp(54.0f), AndroidUtilities.m9dp(64.0f)));
        this.textView.setTextSize(1, 22.0f);
        this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText4));
        this.textView.setGravity(17);
        addView(this.textView, LayoutHelper.createFrame(-1, -1.0f));
    }

    public void setLetter(String letter) {
        this.textView.setText(letter.toUpperCase());
    }

    public void setCellHeight(int height) {
        setLayoutParams(new LayoutParams(AndroidUtilities.m9dp(54.0f), height));
    }
}
