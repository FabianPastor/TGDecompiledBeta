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
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.Theme;

public class AvatarDrawable extends Drawable {
    private int color;
    private boolean drawBrodcast;
    private boolean drawPhoto;
    private boolean isProfile;
    private TextPaint namePaint;
    private int savedMessages;
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
        }
    }

    public AvatarDrawable(Chat chat, boolean z) {
        this();
        this.isProfile = z;
        if (chat != null) {
            setInfo(chat.id, chat.title, null, chat.id < null ? true : null, null);
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
        return Theme.getColor(Theme.keys_avatar_actionBarSelector[getColorIndex(i)]);
    }

    public static int getIconColorForId(int i) {
        return Theme.getColor(Theme.keys_avatar_actionBarIcon[getColorIndex(i)]);
    }

    public static int getProfileColorForId(int i) {
        return Theme.getColor(Theme.keys_avatar_backgroundInProfile[getColorIndex(i)]);
    }

    public static int getProfileTextColorForId(int i) {
        return Theme.getColor(Theme.keys_avatar_subtitleInProfile[getColorIndex(i)]);
    }

    public static int getProfileBackColorForId(int i) {
        return Theme.getColor(Theme.keys_avatar_backgroundActionBar[getColorIndex(i)]);
    }

    public static int getNameColorForId(int i) {
        return Theme.getColor(Theme.keys_avatar_nameInMessage[getColorIndex(i)]);
    }

    public void setInfo(User user) {
        if (user != null) {
            setInfo(user.id, user.first_name, user.last_name, false, null);
        }
    }

    public void setSavedMessages(int i) {
        this.savedMessages = i;
        this.color = Theme.getColor(Theme.key_avatar_backgroundSaved);
    }

    public void setInfo(Chat chat) {
        if (chat != null) {
            setInfo(chat.id, chat.title, null, chat.id < null ? true : null, null);
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
        this.savedMessages = 0;
        if (str == null || str.length() == 0) {
            str = str2;
            str2 = null;
        }
        this.stringBuilder.setLength(0);
        if (str3 != null) {
            this.stringBuilder.append(str3);
        } else {
            if (str != null && str.length() > null) {
                this.stringBuilder.appendCodePoint(str.codePointAt(0));
            }
            if (str2 != null && str2.length() > 0) {
                str = str2.length() - 1;
                Integer num = null;
                while (str >= null) {
                    if (num != null && str2.charAt(str) == ' ') {
                        break;
                    }
                    num = Integer.valueOf(str2.codePointAt(str));
                    str--;
                }
                if (VERSION.SDK_INT >= 17) {
                    this.stringBuilder.append("\u200c");
                }
                this.stringBuilder.appendCodePoint(num.intValue());
            } else if (str != null && str.length() > null) {
                str2 = str.length() - 1;
                while (str2 >= null) {
                    if (str.charAt(str2) == ' ' && str2 != str.length() - 1) {
                        int i2 = str2 + 1;
                        if (str.charAt(i2) != ' ') {
                            if (VERSION.SDK_INT >= 17) {
                                this.stringBuilder.append("\u200c");
                            }
                            this.stringBuilder.appendCodePoint(str.codePointAt(i2));
                        }
                    }
                    str2--;
                }
            }
        }
        if (this.stringBuilder.length() > null) {
            try {
                this.textLayout = new StaticLayout(this.stringBuilder.toString().toUpperCase(), this.namePaint, AndroidUtilities.dp(100.0f), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                if (this.textLayout.getLineCount() > null) {
                    this.textLeft = this.textLayout.getLineLeft(0);
                    this.textWidth = this.textLayout.getLineWidth(0);
                    this.textHeight = (float) this.textLayout.getLineBottom(0);
                    return;
                }
                return;
            } catch (Throwable e) {
                FileLog.m3e(e);
                return;
            }
        }
        this.textLayout = null;
    }

    public void setDrawPhoto(boolean z) {
        this.drawPhoto = z;
    }

    public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        if (bounds != null) {
            int width = bounds.width();
            this.namePaint.setColor(Theme.getColor(Theme.key_avatar_text));
            Theme.avatar_backgroundPaint.setColor(this.color);
            canvas.save();
            canvas.translate((float) bounds.left, (float) bounds.top);
            float f = (float) width;
            float f2 = f / 2.0f;
            canvas.drawCircle(f2, f2, f2, Theme.avatar_backgroundPaint);
            int intrinsicWidth;
            if (this.savedMessages != 0 && Theme.avatar_savedDrawable != null) {
                intrinsicWidth = Theme.avatar_savedDrawable.getIntrinsicWidth();
                int intrinsicHeight = Theme.avatar_savedDrawable.getIntrinsicHeight();
                if (this.savedMessages == 2) {
                    intrinsicWidth = (int) (((float) intrinsicWidth) * 0.8f);
                    intrinsicHeight = (int) (((float) intrinsicHeight) * 0.8f);
                }
                int i = (width - intrinsicWidth) / 2;
                width = (width - intrinsicHeight) / 2;
                Theme.avatar_savedDrawable.setBounds(i, width, intrinsicWidth + i, intrinsicHeight + width);
                Theme.avatar_savedDrawable.draw(canvas);
            } else if (this.drawBrodcast && Theme.avatar_broadcastDrawable != null) {
                intrinsicWidth = (width - Theme.avatar_broadcastDrawable.getIntrinsicWidth()) / 2;
                width = (width - Theme.avatar_broadcastDrawable.getIntrinsicHeight()) / 2;
                Theme.avatar_broadcastDrawable.setBounds(intrinsicWidth, width, Theme.avatar_broadcastDrawable.getIntrinsicWidth() + intrinsicWidth, Theme.avatar_broadcastDrawable.getIntrinsicHeight() + width);
                Theme.avatar_broadcastDrawable.draw(canvas);
            } else if (this.textLayout != null) {
                canvas.translate(((f - this.textWidth) / 2.0f) - this.textLeft, (f - this.textHeight) / 2.0f);
                this.textLayout.draw(canvas);
            } else if (this.drawPhoto && Theme.avatar_photoDrawable != null) {
                intrinsicWidth = (width - Theme.avatar_photoDrawable.getIntrinsicWidth()) / 2;
                width = (width - Theme.avatar_photoDrawable.getIntrinsicHeight()) / 2;
                Theme.avatar_photoDrawable.setBounds(intrinsicWidth, width, Theme.avatar_photoDrawable.getIntrinsicWidth() + intrinsicWidth, Theme.avatar_photoDrawable.getIntrinsicHeight() + width);
                Theme.avatar_photoDrawable.draw(canvas);
            }
            canvas.restore();
        }
    }
}
