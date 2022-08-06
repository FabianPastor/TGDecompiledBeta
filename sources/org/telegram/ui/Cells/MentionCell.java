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
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatPhoto;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$UserProfilePhoto;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;

public class MentionCell extends LinearLayout {
    private boolean attached;
    private AvatarDrawable avatarDrawable;
    private Drawable emojiDrawable;
    private BackupImageView imageView;
    private TextView nameTextView;
    private boolean needsDivider = false;
    private Theme.ResourcesProvider resourcesProvider;
    private TextView usernameTextView;

    public MentionCell(Context context, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        this.resourcesProvider = resourcesProvider2;
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
        resetEmojiSuggestion();
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
        resetEmojiSuggestion();
        this.imageView.setVisibility(4);
        this.usernameTextView.setVisibility(4);
        this.nameTextView.setText(str);
    }

    public void invalidate() {
        super.invalidate();
        this.nameTextView.invalidate();
    }

    public void resetEmojiSuggestion() {
        this.nameTextView.setPadding(0, 0, 0, 0);
        Drawable drawable = this.emojiDrawable;
        if (drawable instanceof AnimatedEmojiDrawable) {
            ((AnimatedEmojiDrawable) drawable).removeView((View) this);
            this.emojiDrawable = null;
        }
    }

    public void setEmojiSuggestion(MediaDataController.KeywordResult keywordResult) {
        this.imageView.setVisibility(4);
        this.usernameTextView.setVisibility(4);
        String str = keywordResult.emoji;
        if (str == null || !str.startsWith("animated_")) {
            this.emojiDrawable = Emoji.getEmojiDrawable(keywordResult.emoji);
        } else {
            try {
                Drawable drawable = this.emojiDrawable;
                if (drawable instanceof AnimatedEmojiDrawable) {
                    ((AnimatedEmojiDrawable) drawable).removeView((View) this);
                    this.emojiDrawable = null;
                }
                AnimatedEmojiDrawable make = AnimatedEmojiDrawable.make(UserConfig.selectedAccount, 0, Long.parseLong(keywordResult.emoji.substring(9)));
                this.emojiDrawable = make;
                if (this.attached) {
                    make.addView((View) this);
                }
            } catch (Exception unused) {
                this.emojiDrawable = Emoji.getEmojiDrawable(keywordResult.emoji);
            }
        }
        if (this.emojiDrawable == null) {
            this.nameTextView.setPadding(0, 0, 0, 0);
            TextView textView = this.nameTextView;
            StringBuilder sb = new StringBuilder();
            sb.append(keywordResult.emoji);
            sb.append(":  ");
            sb.append(keywordResult.keyword);
            textView.setText(sb);
            return;
        }
        this.nameTextView.setPadding(AndroidUtilities.dp(22.0f), 0, 0, 0);
        TextView textView2 = this.nameTextView;
        StringBuilder sb2 = new StringBuilder();
        sb2.append(":  ");
        sb2.append(keywordResult.keyword);
        textView2.setText(sb2);
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        Drawable drawable = this.emojiDrawable;
        if (drawable != null) {
            int dp = AndroidUtilities.dp(drawable instanceof AnimatedEmojiDrawable ? 24.0f : 20.0f);
            int dp2 = AndroidUtilities.dp(this.emojiDrawable instanceof AnimatedEmojiDrawable ? -2.0f : 0.0f);
            this.emojiDrawable.setBounds(this.nameTextView.getLeft() + dp2, ((this.nameTextView.getTop() + this.nameTextView.getBottom()) - dp) / 2, this.nameTextView.getLeft() + dp2 + dp, ((this.nameTextView.getTop() + this.nameTextView.getBottom()) + dp) / 2);
            Drawable drawable2 = this.emojiDrawable;
            if (drawable2 instanceof AnimatedEmojiDrawable) {
                ((AnimatedEmojiDrawable) drawable2).setTime(System.currentTimeMillis());
            }
            this.emojiDrawable.draw(canvas);
        }
    }

    public void setBotCommand(String str, String str2, TLRPC$User tLRPC$User) {
        resetEmojiSuggestion();
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

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.attached = false;
        Drawable drawable = this.emojiDrawable;
        if (drawable instanceof AnimatedEmojiDrawable) {
            ((AnimatedEmojiDrawable) drawable).removeView((View) this);
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.attached = true;
        Drawable drawable = this.emojiDrawable;
        if (drawable instanceof AnimatedEmojiDrawable) {
            ((AnimatedEmojiDrawable) drawable).addView((View) this);
        }
    }
}
