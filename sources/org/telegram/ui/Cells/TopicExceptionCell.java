package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC$TL_forumTopic;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.Forum.ForumUtilities;
import org.telegram.ui.Components.LayoutHelper;
/* loaded from: classes3.dex */
public class TopicExceptionCell extends FrameLayout {
    BackupImageView backupImageView;
    public boolean drawDivider;
    TextView subtitle;
    TextView title;

    public TopicExceptionCell(Context context) {
        super(context);
        BackupImageView backupImageView = new BackupImageView(context);
        this.backupImageView = backupImageView;
        addView(backupImageView, LayoutHelper.createFrame(30, 30.0f, 16, 20.0f, 0.0f, 0.0f, 0.0f));
        TextView textView = new TextView(context);
        this.title = textView;
        textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.title.setTextSize(1, 16.0f);
        this.title.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.title.setMaxLines(1);
        addView(this.title, LayoutHelper.createFrame(-1, -2.0f, 0, 72.0f, 8.0f, 12.0f, 0.0f));
        TextView textView2 = new TextView(context);
        this.subtitle = textView2;
        textView2.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText"));
        this.subtitle.setTextSize(1, 14.0f);
        addView(this.subtitle, LayoutHelper.createFrame(-1, -2.0f, 0, 72.0f, 32.0f, 12.0f, 0.0f));
    }

    public void setTopic(long j, TLRPC$TL_forumTopic tLRPC$TL_forumTopic) {
        ForumUtilities.setTopicIcon(this.backupImageView, tLRPC$TL_forumTopic);
        this.title.setText(tLRPC$TL_forumTopic.title);
        this.subtitle.setText(MessagesController.getInstance(UserConfig.selectedAccount).getMutedString(j, tLRPC$TL_forumTopic.id));
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(56.0f), NUM));
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (this.drawDivider) {
            canvas.drawLine(AndroidUtilities.dp(72.0f), getMeasuredHeight() - 1, getMeasuredWidth(), getMeasuredHeight() - 1, Theme.dividerPaint);
        }
    }
}
