package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;

public class MentionCell extends LinearLayout {
    private AvatarDrawable avatarDrawable = new AvatarDrawable();
    private BackupImageView imageView;
    private TextView nameTextView;
    private TextView usernameTextView;

    public MentionCell(Context context) {
        super(context);
        setOrientation(0);
        this.avatarDrawable.setTextSize(AndroidUtilities.dp(12.0f));
        this.imageView = new BackupImageView(context);
        this.imageView.setRoundRadius(AndroidUtilities.dp(14.0f));
        addView(this.imageView, LayoutHelper.createLinear(28, 28, 12.0f, 4.0f, 0.0f, 0.0f));
        this.nameTextView = new TextView(context);
        this.nameTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.nameTextView.setTextSize(1, 15.0f);
        this.nameTextView.setSingleLine(true);
        this.nameTextView.setGravity(3);
        this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
        addView(this.nameTextView, LayoutHelper.createLinear(-2, -2, 16, 12, 0, 0, 0));
        this.usernameTextView = new TextView(context);
        this.usernameTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText3"));
        this.usernameTextView.setTextSize(1, 15.0f);
        this.usernameTextView.setSingleLine(true);
        this.usernameTextView.setGravity(3);
        this.usernameTextView.setEllipsize(TextUtils.TruncateAt.END);
        addView(this.usernameTextView, LayoutHelper.createLinear(-2, -2, 16, 12, 0, 8, 0));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(36.0f), NUM));
    }

    public void setUser(TLRPC.User user) {
        if (user == null) {
            this.nameTextView.setText("");
            this.usernameTextView.setText("");
            this.imageView.setImageDrawable((Drawable) null);
            return;
        }
        this.avatarDrawable.setInfo(user);
        TLRPC.UserProfilePhoto userProfilePhoto = user.photo;
        if (userProfilePhoto == null || userProfilePhoto.photo_small == null) {
            this.imageView.setImageDrawable(this.avatarDrawable);
        } else {
            this.imageView.setImage(ImageLocation.getForUser(user, false), "50_50", (Drawable) this.avatarDrawable, (Object) user);
        }
        this.nameTextView.setText(UserObject.getUserName(user));
        if (user.username != null) {
            TextView textView = this.usernameTextView;
            textView.setText("@" + user.username);
        } else {
            this.usernameTextView.setText("");
        }
        this.imageView.setVisibility(0);
        this.usernameTextView.setVisibility(0);
    }

    public void setText(String str) {
        this.imageView.setVisibility(4);
        this.usernameTextView.setVisibility(4);
        this.nameTextView.setText(str);
    }

    public void invalidate() {
        super.invalidate();
        this.nameTextView.invalidate();
    }

    public void setEmojiSuggestion(MediaDataController.KeywordResult keywordResult) {
        this.imageView.setVisibility(4);
        this.usernameTextView.setVisibility(4);
        StringBuilder sb = new StringBuilder(keywordResult.emoji.length() + keywordResult.keyword.length() + 4);
        sb.append(keywordResult.emoji);
        sb.append("   :");
        sb.append(keywordResult.keyword);
        TextView textView = this.nameTextView;
        textView.setText(Emoji.replaceEmoji(sb, textView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false));
    }

    public void setBotCommand(String str, String str2, TLRPC.User user) {
        if (user != null) {
            this.imageView.setVisibility(0);
            this.avatarDrawable.setInfo(user);
            TLRPC.UserProfilePhoto userProfilePhoto = user.photo;
            if (userProfilePhoto == null || userProfilePhoto.photo_small == null) {
                this.imageView.setImageDrawable(this.avatarDrawable);
            } else {
                this.imageView.setImage(ImageLocation.getForUser(user, false), "50_50", (Drawable) this.avatarDrawable, (Object) user);
            }
        } else {
            this.imageView.setVisibility(4);
        }
        this.usernameTextView.setVisibility(0);
        this.nameTextView.setText(str);
        TextView textView = this.usernameTextView;
        textView.setText(Emoji.replaceEmoji(str2, textView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false));
    }

    public void setIsDarkTheme(boolean z) {
        if (z) {
            this.nameTextView.setTextColor(-1);
            this.usernameTextView.setTextColor(-4473925);
            return;
        }
        this.nameTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.usernameTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText3"));
    }
}
