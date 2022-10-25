package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.tgnet.TLRPC$TL_forumTopic;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.Forum.ForumUtilities;
import org.telegram.ui.Components.LayoutHelper;
/* loaded from: classes3.dex */
public class TopicSearchCell extends FrameLayout {
    BackupImageView backupImageView;
    public boolean drawDivider;
    TextView textView;
    TLRPC$TL_forumTopic topic;

    public TopicSearchCell(Context context) {
        super(context);
        this.backupImageView = new BackupImageView(context);
        TextView textView = new TextView(context);
        this.textView = textView;
        textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.textView.setTextSize(1, 16.0f);
        this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        if (LocaleController.isRTL) {
            addView(this.backupImageView, LayoutHelper.createFrame(30, 30.0f, 21, 12.0f, 0.0f, 12.0f, 0.0f));
            addView(this.textView, LayoutHelper.createFrame(-1, -2.0f, 21, 12.0f, 0.0f, 56.0f, 0.0f));
            return;
        }
        addView(this.backupImageView, LayoutHelper.createFrame(30, 30.0f, 16, 12.0f, 0.0f, 12.0f, 0.0f));
        addView(this.textView, LayoutHelper.createFrame(-1, -2.0f, 16, 56.0f, 0.0f, 12.0f, 0.0f));
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), NUM));
    }

    public void setTopic(TLRPC$TL_forumTopic tLRPC$TL_forumTopic) {
        this.topic = tLRPC$TL_forumTopic;
        if (TextUtils.isEmpty(tLRPC$TL_forumTopic.searchQuery)) {
            this.textView.setText(tLRPC$TL_forumTopic.title);
        } else {
            this.textView.setText(AndroidUtilities.highlightText(tLRPC$TL_forumTopic.title, tLRPC$TL_forumTopic.searchQuery, (Theme.ResourcesProvider) null));
        }
        ForumUtilities.setTopicIcon(this.backupImageView, tLRPC$TL_forumTopic);
    }

    public TLRPC$TL_forumTopic getTopic() {
        return this.topic;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (this.drawDivider) {
            int dp = AndroidUtilities.dp(56.0f);
            if (LocaleController.isRTL) {
                canvas.drawLine(0.0f, getMeasuredHeight() - 1, getMeasuredWidth() - dp, getMeasuredHeight() - 1, Theme.dividerPaint);
            } else {
                canvas.drawLine(dp, getMeasuredHeight() - 1, getMeasuredWidth(), getMeasuredHeight() - 1, Theme.dividerPaint);
            }
        }
    }
}
