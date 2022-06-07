package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatPhoto;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$UserProfilePhoto;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;

public class MentionCell extends LinearLayout {
    private AvatarDrawable avatarDrawable;
    private BackupImageView imageView;
    private TextView nameTextView;
    private boolean needsDivider = false;
    private Theme.ResourcesProvider resourcesProvider;
    private TextView usernameTextView;

    public MentionCell(Context context, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        setOrientation(0);
        AvatarDrawable avatarDrawable2 = new AvatarDrawable();
        this.avatarDrawable = avatarDrawable2;
        avatarDrawable2.setTextSize(AndroidUtilities.dp(12.0f));
        BackupImageView backupImageView = new BackupImageView(context);
        this.imageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(14.0f));
        addView(this.imageView, LayoutHelper.createLinear(28, 28, 12.0f, 4.0f, 0.0f, 0.0f));
        TextView textView = new TextView(context);
        this.nameTextView = textView;
        textView.setTextColor(getThemedColor("windowBackgroundWhiteBlackText"));
        this.nameTextView.setTextSize(1, 15.0f);
        this.nameTextView.setSingleLine(true);
        this.nameTextView.setGravity(3);
        this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
        addView(this.nameTextView, LayoutHelper.createLinear(-2, -2, 16, 12, 0, 0, 0));
        TextView textView2 = new TextView(context);
        this.usernameTextView = textView2;
        textView2.setTextColor(getThemedColor("windowBackgroundWhiteGrayText3"));
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

    public void setUser(TLRPC$User tLRPC$User) {
        if (tLRPC$User == null) {
            this.nameTextView.setText("");
            this.usernameTextView.setText("");
            this.imageView.setImageDrawable((Drawable) null);
            return;
        }
        this.avatarDrawable.setInfo(tLRPC$User);
        TLRPC$UserProfilePhoto tLRPC$UserProfilePhoto = tLRPC$User.photo;
        if (tLRPC$UserProfilePhoto == null || tLRPC$UserProfilePhoto.photo_small == null) {
            this.imageView.setImageDrawable(this.avatarDrawable);
        } else {
            this.imageView.setForUserOrChat(tLRPC$User, this.avatarDrawable);
        }
        this.nameTextView.setText(UserObject.getUserName(tLRPC$User));
        if (tLRPC$User.username != null) {
            TextView textView = this.usernameTextView;
            textView.setText("@" + tLRPC$User.username);
        } else {
            this.usernameTextView.setText("");
        }
        this.imageView.setVisibility(0);
        this.usernameTextView.setVisibility(0);
    }

    public void setDivider(boolean z) {
        if (z != this.needsDivider) {
            this.needsDivider = z;
            setWillNotDraw(!z);
            invalidate();
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.needsDivider) {
            canvas.drawLine((float) AndroidUtilities.dp(52.0f), (float) (getHeight() - 1), (float) (getWidth() - AndroidUtilities.dp(8.0f)), (float) (getHeight() - 1), Theme.dividerPaint);
        }
    }

    public void setChat(TLRPC$Chat tLRPC$Chat) {
        if (tLRPC$Chat == null) {
            this.nameTextView.setText("");
            this.usernameTextView.setText("");
            this.imageView.setImageDrawable((Drawable) null);
            return;
        }
        this.avatarDrawable.setInfo(tLRPC$Chat);
        TLRPC$ChatPhoto tLRPC$ChatPhoto = tLRPC$Chat.photo;
        if (tLRPC$ChatPhoto == null || tLRPC$ChatPhoto.photo_small == null) {
            this.imageView.setImageDrawable(this.avatarDrawable);
        } else {
            this.imageView.setForUserOrChat(tLRPC$Chat, this.avatarDrawable);
        }
        this.nameTextView.setText(tLRPC$Chat.title);
        if (tLRPC$Chat.username != null) {
            TextView textView = this.usernameTextView;
            textView.setText("@" + tLRPC$Chat.username);
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

    public void setBotCommand(String str, String str2, TLRPC$User tLRPC$User) {
        if (tLRPC$User != null) {
            this.imageView.setVisibility(0);
            this.avatarDrawable.setInfo(tLRPC$User);
            TLRPC$UserProfilePhoto tLRPC$UserProfilePhoto = tLRPC$User.photo;
            if (tLRPC$UserProfilePhoto == null || tLRPC$UserProfilePhoto.photo_small == null) {
                this.imageView.setImageDrawable(this.avatarDrawable);
            } else {
                this.imageView.setForUserOrChat(tLRPC$User, this.avatarDrawable);
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
        this.nameTextView.setTextColor(getThemedColor("windowBackgroundWhiteBlackText"));
        this.usernameTextView.setTextColor(getThemedColor("windowBackgroundWhiteGrayText3"));
    }

    private int getThemedColor(String str) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(str) : null;
        return color != null ? color.intValue() : Theme.getColor(str);
    }
}
