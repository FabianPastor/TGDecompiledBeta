package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class ArchiveHintInnerCell extends FrameLayout {
    private TextView headerTextView;
    private ImageView imageView;
    private ImageView imageView2;
    private TextView messageTextView;

    public ArchiveHintInnerCell(Context context, int i) {
        super(context);
        this.imageView = new ImageView(context);
        this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chats_nameMessage_threeLines"), PorterDuff.Mode.MULTIPLY));
        this.headerTextView = new TextView(context);
        this.headerTextView.setTextColor(Theme.getColor("chats_nameMessage_threeLines"));
        this.headerTextView.setTextSize(1, 20.0f);
        this.headerTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.headerTextView.setGravity(17);
        addView(this.headerTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 52.0f, 75.0f, 52.0f, 0.0f));
        this.messageTextView = new TextView(context);
        this.messageTextView.setTextColor(Theme.getColor("chats_message"));
        this.messageTextView.setTextSize(1, 14.0f);
        this.messageTextView.setGravity(17);
        addView(this.messageTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 52.0f, 110.0f, 52.0f, 0.0f));
        if (i == 0) {
            addView(this.imageView, LayoutHelper.createFrame(-2, -2.0f, 49, 0.0f, 20.0f, 8.0f, 0.0f));
            this.imageView2 = new ImageView(context);
            this.imageView2.setImageResource(NUM);
            this.imageView2.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chats_unreadCounter"), PorterDuff.Mode.MULTIPLY));
            addView(this.imageView2, LayoutHelper.createFrame(-2, -2.0f, 49, 0.0f, 20.0f, 8.0f, 0.0f));
            this.headerTextView.setText(LocaleController.getString("ArchiveHintHeader1", NUM));
            this.messageTextView.setText(LocaleController.getString("ArchiveHintText1", NUM));
            this.imageView.setImageResource(NUM);
        } else if (i == 1) {
            addView(this.imageView, LayoutHelper.createFrame(-2, -2.0f, 49, 0.0f, 18.0f, 0.0f, 0.0f));
            this.headerTextView.setText(LocaleController.getString("ArchiveHintHeader2", NUM));
            this.messageTextView.setText(LocaleController.getString("ArchiveHintText2", NUM));
            this.imageView.setImageResource(NUM);
        } else if (i == 2) {
            addView(this.imageView, LayoutHelper.createFrame(-2, -2.0f, 49, 0.0f, 18.0f, 0.0f, 0.0f));
            this.headerTextView.setText(LocaleController.getString("ArchiveHintHeader3", NUM));
            this.messageTextView.setText(LocaleController.getString("ArchiveHintText3", NUM));
            this.imageView.setImageResource(NUM);
        }
    }
}
