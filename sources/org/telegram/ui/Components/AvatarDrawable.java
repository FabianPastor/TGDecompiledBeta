package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.Theme;

public class AvatarDrawable extends Drawable {
    public static final int AVATAR_TYPE_ARCHIVED = 3;
    public static final int AVATAR_TYPE_NORMAL = 0;
    public static final int AVATAR_TYPE_SAVED = 1;
    public static final int AVATAR_TYPE_SAVED_SMALL = 2;
    private float archivedAvatarProgress;
    private int avatarType;
    private int color;
    private boolean drawBrodcast;
    private boolean drawDeleted;
    private boolean isProfile;
    private TextPaint namePaint;
    private StringBuilder stringBuilder;
    private float textHeight;
    private StaticLayout textLayout;
    private float textLeft;
    private float textWidth;

    public int getIntrinsicHeight() {
        return 0;
    }

    public int getIntrinsicWidth() {
        return 0;
    }

    public int getOpacity() {
        return -2;
    }

    public void setAlpha(int i) {
    }

    public void setColorFilter(ColorFilter colorFilter) {
    }

    public AvatarDrawable() {
        this.stringBuilder = new StringBuilder(5);
        this.namePaint = new TextPaint(1);
        this.namePaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.namePaint.setTextSize((float) AndroidUtilities.dp(18.0f));
    }

    public AvatarDrawable(User user) {
        this(user, false);
    }

    public AvatarDrawable(Chat chat) {
        this(chat, false);
    }

    public AvatarDrawable(User user, boolean z) {
        this();
        this.isProfile = z;
        if (user != null) {
            setInfo(user.id, user.first_name, user.last_name, false, null);
            this.drawDeleted = UserObject.isDeleted(user);
        }
    }

    public AvatarDrawable(Chat chat, boolean z) {
        this();
        this.isProfile = z;
        if (chat != null) {
            int i = chat.id;
            setInfo(i, chat.title, null, i < 0, null);
        }
    }

    public void setProfile(boolean z) {
        this.isProfile = z;
    }

    public static int getColorIndex(int i) {
        return (i < 0 || i >= 7) ? Math.abs(i % Theme.keys_avatar_background.length) : i;
    }

    public static int getColorForId(int i) {
        return Theme.getColor(Theme.keys_avatar_background[getColorIndex(i)]);
    }

    public static int getButtonColorForId(int i) {
        return Theme.getColor("avatar_actionBarSelectorBlue");
    }

    public static int getIconColorForId(int i) {
        return Theme.getColor("avatar_actionBarIconBlue");
    }

    public static int getProfileColorForId(int i) {
        return Theme.getColor(Theme.keys_avatar_background[getColorIndex(i)]);
    }

    public static int getProfileTextColorForId(int i) {
        return Theme.getColor("avatar_subtitleInProfileBlue");
    }

    public static int getProfileBackColorForId(int i) {
        return Theme.getColor("avatar_backgroundActionBarBlue");
    }

    public static int getNameColorForId(int i) {
        return Theme.getColor(Theme.keys_avatar_nameInMessage[getColorIndex(i)]);
    }

    public void setInfo(User user) {
        if (user != null) {
            setInfo(user.id, user.first_name, user.last_name, false, null);
            this.drawDeleted = UserObject.isDeleted(user);
        }
    }

    public void setAvatarType(int i) {
        this.avatarType = i;
        if (this.avatarType == 3) {
            this.color = Theme.getColor("avatar_backgroundArchivedHidden");
        } else {
            this.color = Theme.getColor("avatar_backgroundSaved");
        }
    }

    public void setArchivedAvatarHiddenProgress(float f) {
        this.archivedAvatarProgress = f;
    }

    public int getAvatarType() {
        return this.avatarType;
    }

    public void setInfo(Chat chat) {
        if (chat != null) {
            int i = chat.id;
            setInfo(i, chat.title, null, i < 0, null);
        }
    }

    public void setColor(int i) {
        this.color = i;
    }

    public void setTextSize(int i) {
        this.namePaint.setTextSize((float) i);
    }

    public void setInfo(int i, String str, String str2, boolean z) {
        setInfo(i, str, str2, z, null);
    }

    public int getColor() {
        return this.color;
    }

    public void setInfo(int i, String str, String str2, boolean z, String str3) {
        if (this.isProfile) {
            this.color = getProfileColorForId(i);
        } else {
            this.color = getColorForId(i);
        }
        this.drawBrodcast = z;
        this.avatarType = 0;
        this.drawDeleted = false;
        if (str == null || str.length() == 0) {
            str = str2;
            str2 = null;
        }
        this.stringBuilder.setLength(0);
        if (str3 != null) {
            this.stringBuilder.append(str3);
        } else {
            if (str != null && str.length() > 0) {
                this.stringBuilder.appendCodePoint(str.codePointAt(0));
            }
            str3 = "‌";
            if (str2 != null && str2.length() > 0) {
                int length = str2.length() - 1;
                Integer num = null;
                while (length >= 0 && (num == null || str2.charAt(length) != ' ')) {
                    num = Integer.valueOf(str2.codePointAt(length));
                    length--;
                }
                if (VERSION.SDK_INT > 17) {
                    this.stringBuilder.append(str3);
                }
                this.stringBuilder.appendCodePoint(num.intValue());
            } else if (str != null && str.length() > 0) {
                int length2 = str.length() - 1;
                while (length2 >= 0) {
                    if (str.charAt(length2) == ' ' && length2 != str.length() - 1) {
                        int i2 = length2 + 1;
                        if (str.charAt(i2) != ' ') {
                            if (VERSION.SDK_INT > 17) {
                                this.stringBuilder.append(str3);
                            }
                            this.stringBuilder.appendCodePoint(str.codePointAt(i2));
                        }
                    }
                    length2--;
                }
            }
        }
        if (this.stringBuilder.length() > 0) {
            try {
                this.textLayout = new StaticLayout(this.stringBuilder.toString().toUpperCase(), this.namePaint, AndroidUtilities.dp(100.0f), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                if (this.textLayout.getLineCount() > 0) {
                    this.textLeft = this.textLayout.getLineLeft(0);
                    this.textWidth = this.textLayout.getLineWidth(0);
                    this.textHeight = (float) this.textLayout.getLineBottom(0);
                    return;
                }
                return;
            } catch (Exception e) {
                FileLog.e(e);
                return;
            }
        }
        this.textLayout = null;
    }

    public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        if (bounds != null) {
            int width = bounds.width();
            this.namePaint.setColor(Theme.getColor("avatar_text"));
            Theme.avatar_backgroundPaint.setColor(this.color);
            canvas.save();
            canvas.translate((float) bounds.left, (float) bounds.top);
            float f = (float) width;
            float f2 = f / 2.0f;
            canvas.drawCircle(f2, f2, f2, Theme.avatar_backgroundPaint);
            int i = this.avatarType;
            int intrinsicWidth;
            int intrinsicHeight;
            int i2;
            if (i == 3) {
                String str = "Arrow2.**";
                String str2 = "Arrow1.**";
                String str3;
                if (this.archivedAvatarProgress != 0.0f) {
                    str3 = "avatar_backgroundArchived";
                    Theme.avatar_backgroundPaint.setColor(Theme.getColor(str3));
                    canvas.drawCircle(f2, f2, this.archivedAvatarProgress * f2, Theme.avatar_backgroundPaint);
                    if (Theme.dialogs_archiveAvatarDrawableRecolored) {
                        Theme.dialogs_archiveAvatarDrawable.beginApplyLayerColors();
                        Theme.dialogs_archiveAvatarDrawable.setLayerColor(str2, Theme.getColor(str3));
                        Theme.dialogs_archiveAvatarDrawable.setLayerColor(str, Theme.getColor(str3));
                        Theme.dialogs_archiveAvatarDrawable.commitApplyLayerColors();
                        Theme.dialogs_archiveAvatarDrawableRecolored = false;
                    }
                } else if (!Theme.dialogs_archiveAvatarDrawableRecolored) {
                    Theme.dialogs_archiveAvatarDrawable.beginApplyLayerColors();
                    str3 = "avatar_backgroundArchivedHidden";
                    Theme.dialogs_archiveAvatarDrawable.setLayerColor(str2, Theme.getColor(str3));
                    Theme.dialogs_archiveAvatarDrawable.setLayerColor(str, Theme.getColor(str3));
                    Theme.dialogs_archiveAvatarDrawable.commitApplyLayerColors();
                    Theme.dialogs_archiveAvatarDrawableRecolored = true;
                }
                intrinsicWidth = Theme.dialogs_archiveAvatarDrawable.getIntrinsicWidth();
                intrinsicHeight = Theme.dialogs_archiveAvatarDrawable.getIntrinsicHeight();
                i2 = (width - intrinsicWidth) / 2;
                width = (width - intrinsicHeight) / 2;
                canvas.save();
                Theme.dialogs_archiveAvatarDrawable.setBounds(i2, width, intrinsicWidth + i2, intrinsicHeight + width);
                Theme.dialogs_archiveAvatarDrawable.draw(canvas);
                canvas.restore();
            } else {
                Drawable drawable;
                Drawable drawable2;
                if (i != 0) {
                    drawable = Theme.avatar_savedDrawable;
                    if (drawable != null) {
                        intrinsicWidth = drawable.getIntrinsicWidth();
                        intrinsicHeight = Theme.avatar_savedDrawable.getIntrinsicHeight();
                        if (this.avatarType == 2) {
                            intrinsicWidth = (int) (((float) intrinsicWidth) * 0.8f);
                            intrinsicHeight = (int) (((float) intrinsicHeight) * 0.8f);
                        }
                        i2 = (width - intrinsicWidth) / 2;
                        width = (width - intrinsicHeight) / 2;
                        Theme.avatar_savedDrawable.setBounds(i2, width, intrinsicWidth + i2, intrinsicHeight + width);
                        Theme.avatar_savedDrawable.draw(canvas);
                    }
                }
                if (this.drawBrodcast) {
                    drawable = Theme.avatar_broadcastDrawable;
                    if (drawable != null) {
                        intrinsicWidth = (width - drawable.getIntrinsicWidth()) / 2;
                        width = (width - Theme.avatar_broadcastDrawable.getIntrinsicHeight()) / 2;
                        drawable2 = Theme.avatar_broadcastDrawable;
                        drawable2.setBounds(intrinsicWidth, width, drawable2.getIntrinsicWidth() + intrinsicWidth, Theme.avatar_broadcastDrawable.getIntrinsicHeight() + width);
                        Theme.avatar_broadcastDrawable.draw(canvas);
                    }
                }
                if (this.drawDeleted) {
                    drawable = Theme.avatar_ghostDrawable;
                    if (drawable != null) {
                        intrinsicWidth = (width - drawable.getIntrinsicWidth()) / 2;
                        width = (width - Theme.avatar_ghostDrawable.getIntrinsicHeight()) / 2;
                        drawable2 = Theme.avatar_ghostDrawable;
                        drawable2.setBounds(intrinsicWidth, width, drawable2.getIntrinsicWidth() + intrinsicWidth, Theme.avatar_ghostDrawable.getIntrinsicHeight() + width);
                        Theme.avatar_ghostDrawable.draw(canvas);
                    }
                }
                if (this.textLayout != null) {
                    canvas.translate(((f - this.textWidth) / 2.0f) - this.textLeft, (f - this.textHeight) / 2.0f);
                    this.textLayout.draw(canvas);
                }
            }
            canvas.restore();
        }
    }
}
