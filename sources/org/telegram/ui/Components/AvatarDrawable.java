package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;

public class AvatarDrawable extends Drawable {
    public static final int AVATAR_TYPE_ARCHIVED = 2;
    public static final int AVATAR_TYPE_FILTER_ARCHIVED = 11;
    public static final int AVATAR_TYPE_FILTER_BOTS = 8;
    public static final int AVATAR_TYPE_FILTER_CHANNELS = 7;
    public static final int AVATAR_TYPE_FILTER_CONTACTS = 4;
    public static final int AVATAR_TYPE_FILTER_GROUPS = 6;
    public static final int AVATAR_TYPE_FILTER_MUTED = 9;
    public static final int AVATAR_TYPE_FILTER_NON_CONTACTS = 5;
    public static final int AVATAR_TYPE_FILTER_READ = 10;
    public static final int AVATAR_TYPE_NORMAL = 0;
    public static final int AVATAR_TYPE_REGISTER = 13;
    public static final int AVATAR_TYPE_REPLIES = 12;
    public static final int AVATAR_TYPE_SAVED = 1;
    public static final int AVATAR_TYPE_SHARES = 3;
    private int alpha;
    private float archivedAvatarProgress;
    private int avatarType;
    private int color;
    private boolean drawDeleted;
    private boolean isProfile;
    private TextPaint namePaint;
    private boolean needApplyColorAccent;
    private Theme.ResourcesProvider resourcesProvider;
    private boolean smallSize;
    private StringBuilder stringBuilder;
    private float textHeight;
    private StaticLayout textLayout;
    private float textLeft;
    private float textWidth;

    public AvatarDrawable() {
        this((Theme.ResourcesProvider) null);
    }

    public AvatarDrawable(Theme.ResourcesProvider resourcesProvider2) {
        this.stringBuilder = new StringBuilder(5);
        this.alpha = 255;
        this.resourcesProvider = resourcesProvider2;
        TextPaint textPaint = new TextPaint(1);
        this.namePaint = textPaint;
        textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.namePaint.setTextSize((float) AndroidUtilities.dp(18.0f));
    }

    public AvatarDrawable(TLRPC.User user) {
        this(user, false);
    }

    public AvatarDrawable(TLRPC.Chat chat) {
        this(chat, false);
    }

    public AvatarDrawable(TLRPC.User user, boolean profile) {
        this();
        this.isProfile = profile;
        if (user != null) {
            setInfo(user.id, user.first_name, user.last_name, (String) null);
            this.drawDeleted = UserObject.isDeleted(user);
        }
    }

    public AvatarDrawable(TLRPC.Chat chat, boolean profile) {
        this();
        this.isProfile = profile;
        if (chat != null) {
            setInfo(chat.id, chat.title, (String) null, (String) null);
        }
    }

    public void setProfile(boolean value) {
        this.isProfile = value;
    }

    private static int getColorIndex(long id) {
        if (id < 0 || id >= 7) {
            return (int) Math.abs(id % ((long) Theme.keys_avatar_background.length));
        }
        return (int) id;
    }

    public static int getColorForId(long id) {
        return Theme.getColor(Theme.keys_avatar_background[getColorIndex(id)]);
    }

    public static int getButtonColorForId(long id, Theme.ResourcesProvider resourcesProvider2) {
        return Theme.getColor("avatar_actionBarSelectorBlue", resourcesProvider2);
    }

    public static int getIconColorForId(long id, Theme.ResourcesProvider resourcesProvider2) {
        return Theme.getColor("avatar_actionBarIconBlue", resourcesProvider2);
    }

    public static int getProfileColorForId(long id, Theme.ResourcesProvider resourcesProvider2) {
        return Theme.getColor(Theme.keys_avatar_background[getColorIndex(id)], resourcesProvider2);
    }

    public static int getProfileTextColorForId(long id, Theme.ResourcesProvider resourcesProvider2) {
        return Theme.getColor("avatar_subtitleInProfileBlue", resourcesProvider2);
    }

    public static int getProfileBackColorForId(long id, Theme.ResourcesProvider resourcesProvider2) {
        return Theme.getColor("avatar_backgroundActionBarBlue", resourcesProvider2);
    }

    public static String getNameColorNameForId(long id) {
        return Theme.keys_avatar_nameInMessage[getColorIndex(id)];
    }

    public void setInfo(TLRPC.User user) {
        if (user != null) {
            setInfo(user.id, user.first_name, user.last_name, (String) null);
            this.drawDeleted = UserObject.isDeleted(user);
        }
    }

    public void setInfo(TLObject object) {
        if (object instanceof TLRPC.User) {
            setInfo((TLRPC.User) object);
        } else if (object instanceof TLRPC.Chat) {
            setInfo((TLRPC.Chat) object);
        } else if (object instanceof TLRPC.ChatInvite) {
            setInfo((TLRPC.ChatInvite) object);
        }
    }

    public void setSmallSize(boolean value) {
        this.smallSize = value;
    }

    public void setAvatarType(int value) {
        this.avatarType = value;
        boolean z = true;
        if (value == 13) {
            this.color = Theme.getColor("chats_actionBackground");
        } else if (value == 2) {
            this.color = getThemedColor("avatar_backgroundArchivedHidden");
        } else if (value == 12) {
            this.color = getThemedColor("avatar_backgroundSaved");
        } else if (value == 1) {
            this.color = getThemedColor("avatar_backgroundSaved");
        } else if (value == 3) {
            this.color = getThemedColor(Theme.keys_avatar_background[getColorIndex(5)]);
        } else if (value == 4) {
            this.color = getThemedColor(Theme.keys_avatar_background[getColorIndex(5)]);
        } else if (value == 5) {
            this.color = getThemedColor(Theme.keys_avatar_background[getColorIndex(4)]);
        } else if (value == 6) {
            this.color = getThemedColor(Theme.keys_avatar_background[getColorIndex(3)]);
        } else if (value == 7) {
            this.color = getThemedColor(Theme.keys_avatar_background[getColorIndex(1)]);
        } else if (value == 8) {
            this.color = getThemedColor(Theme.keys_avatar_background[getColorIndex(0)]);
        } else if (value == 9) {
            this.color = getThemedColor(Theme.keys_avatar_background[getColorIndex(6)]);
        } else if (value == 10) {
            this.color = getThemedColor(Theme.keys_avatar_background[getColorIndex(5)]);
        } else {
            this.color = getThemedColor(Theme.keys_avatar_background[getColorIndex(4)]);
        }
        int i = this.avatarType;
        if (i == 2 || i == 1 || i == 12) {
            z = false;
        }
        this.needApplyColorAccent = z;
    }

    public void setArchivedAvatarHiddenProgress(float progress) {
        this.archivedAvatarProgress = progress;
    }

    public int getAvatarType() {
        return this.avatarType;
    }

    public void setInfo(TLRPC.Chat chat) {
        if (chat != null) {
            setInfo(chat.id, chat.title, (String) null, (String) null);
        }
    }

    public void setInfo(TLRPC.ChatInvite chat) {
        if (chat != null) {
            setInfo(0, chat.title, (String) null, (String) null);
        }
    }

    public void setColor(int value) {
        this.color = value;
        this.needApplyColorAccent = false;
    }

    public void setTextSize(int size) {
        this.namePaint.setTextSize((float) size);
    }

    public void setInfo(long id, String firstName, String lastName) {
        setInfo(id, firstName, lastName, (String) null);
    }

    public int getColor() {
        return this.needApplyColorAccent ? Theme.changeColorAccent(this.color) : this.color;
    }

    public void setInfo(long id, String firstName, String lastName, String custom) {
        String lastName2;
        String firstName2;
        String str = custom;
        this.color = getThemedColor(Theme.keys_avatar_background[getColorIndex(id)]);
        this.needApplyColorAccent = id == 5;
        this.avatarType = 0;
        this.drawDeleted = false;
        if (firstName == null || firstName.length() == 0) {
            firstName2 = lastName;
            lastName2 = null;
        } else {
            firstName2 = firstName;
            lastName2 = lastName;
        }
        this.stringBuilder.setLength(0);
        if (str != null) {
            this.stringBuilder.append(str);
        } else {
            if (firstName2 != null && firstName2.length() > 0) {
                this.stringBuilder.appendCodePoint(firstName2.codePointAt(0));
            }
            if (lastName2 != null && lastName2.length() > 0) {
                Integer lastch = null;
                int a = lastName2.length() - 1;
                while (a >= 0 && (lastch == null || lastName2.charAt(a) != ' ')) {
                    lastch = Integer.valueOf(lastName2.codePointAt(a));
                    a--;
                }
                if (Build.VERSION.SDK_INT > 17) {
                    this.stringBuilder.append("‌");
                }
                this.stringBuilder.appendCodePoint(lastch.intValue());
            } else if (firstName2 != null && firstName2.length() > 0) {
                int a2 = firstName2.length() - 1;
                while (true) {
                    if (a2 < 0) {
                        break;
                    } else if (firstName2.charAt(a2) != ' ' || a2 == firstName2.length() - 1 || firstName2.charAt(a2 + 1) == ' ') {
                        a2--;
                    } else {
                        if (Build.VERSION.SDK_INT > 17) {
                            this.stringBuilder.append("‌");
                        }
                        this.stringBuilder.appendCodePoint(firstName2.codePointAt(a2 + 1));
                    }
                }
            }
        }
        if (this.stringBuilder.length() > 0) {
            try {
                StaticLayout staticLayout = new StaticLayout(this.stringBuilder.toString().toUpperCase(), this.namePaint, AndroidUtilities.dp(100.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                this.textLayout = staticLayout;
                if (staticLayout.getLineCount() > 0) {
                    this.textLeft = this.textLayout.getLineLeft(0);
                    this.textWidth = this.textLayout.getLineWidth(0);
                    this.textHeight = (float) this.textLayout.getLineBottom(0);
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        } else {
            this.textLayout = null;
        }
    }

    public void draw(Canvas canvas) {
        Drawable drawable;
        Rect bounds = getBounds();
        if (bounds != null) {
            int size = bounds.width();
            this.namePaint.setColor(ColorUtils.setAlphaComponent(getThemedColor("avatar_text"), this.alpha));
            Theme.avatar_backgroundPaint.setColor(ColorUtils.setAlphaComponent(getColor(), this.alpha));
            canvas.save();
            canvas.translate((float) bounds.left, (float) bounds.top);
            canvas.drawCircle(((float) size) / 2.0f, ((float) size) / 2.0f, ((float) size) / 2.0f, Theme.avatar_backgroundPaint);
            int i = this.avatarType;
            if (i == 2) {
                if (this.archivedAvatarProgress != 0.0f) {
                    Theme.avatar_backgroundPaint.setColor(ColorUtils.setAlphaComponent(getThemedColor("avatar_backgroundArchived"), this.alpha));
                    canvas.drawCircle(((float) size) / 2.0f, ((float) size) / 2.0f, (((float) size) / 2.0f) * this.archivedAvatarProgress, Theme.avatar_backgroundPaint);
                    if (Theme.dialogs_archiveAvatarDrawableRecolored) {
                        Theme.dialogs_archiveAvatarDrawable.beginApplyLayerColors();
                        Theme.dialogs_archiveAvatarDrawable.setLayerColor("Arrow1.**", Theme.getNonAnimatedColor("avatar_backgroundArchived"));
                        Theme.dialogs_archiveAvatarDrawable.setLayerColor("Arrow2.**", Theme.getNonAnimatedColor("avatar_backgroundArchived"));
                        Theme.dialogs_archiveAvatarDrawable.commitApplyLayerColors();
                        Theme.dialogs_archiveAvatarDrawableRecolored = false;
                    }
                } else if (!Theme.dialogs_archiveAvatarDrawableRecolored) {
                    Theme.dialogs_archiveAvatarDrawable.beginApplyLayerColors();
                    Theme.dialogs_archiveAvatarDrawable.setLayerColor("Arrow1.**", this.color);
                    Theme.dialogs_archiveAvatarDrawable.setLayerColor("Arrow2.**", this.color);
                    Theme.dialogs_archiveAvatarDrawable.commitApplyLayerColors();
                    Theme.dialogs_archiveAvatarDrawableRecolored = true;
                }
                int w = Theme.dialogs_archiveAvatarDrawable.getIntrinsicWidth();
                int h = Theme.dialogs_archiveAvatarDrawable.getIntrinsicHeight();
                int x = (size - w) / 2;
                int y = (size - h) / 2;
                canvas.save();
                Theme.dialogs_archiveAvatarDrawable.setBounds(x, y, x + w, y + h);
                Theme.dialogs_archiveAvatarDrawable.draw(canvas);
                canvas.restore();
            } else if (i != 0) {
                if (i == 12) {
                    drawable = Theme.avatarDrawables[11];
                } else if (i == 1) {
                    drawable = Theme.avatarDrawables[0];
                } else if (i == 3) {
                    drawable = Theme.avatarDrawables[10];
                } else if (i == 4) {
                    drawable = Theme.avatarDrawables[2];
                } else if (i == 5) {
                    drawable = Theme.avatarDrawables[3];
                } else if (i == 6) {
                    drawable = Theme.avatarDrawables[4];
                } else if (i == 7) {
                    drawable = Theme.avatarDrawables[5];
                } else if (i == 8) {
                    drawable = Theme.avatarDrawables[6];
                } else if (i == 9) {
                    drawable = Theme.avatarDrawables[7];
                } else if (i == 10) {
                    drawable = Theme.avatarDrawables[8];
                } else {
                    drawable = Theme.avatarDrawables[9];
                }
                if (drawable != null) {
                    int w2 = drawable.getIntrinsicWidth();
                    int h2 = drawable.getIntrinsicHeight();
                    if (this.smallSize) {
                        w2 = (int) (((float) w2) * 0.8f);
                        h2 = (int) (((float) h2) * 0.8f);
                    }
                    int x2 = (size - w2) / 2;
                    int y2 = (size - h2) / 2;
                    drawable.setBounds(x2, y2, x2 + w2, y2 + h2);
                    int i2 = this.alpha;
                    if (i2 != 255) {
                        drawable.setAlpha(i2);
                        drawable.draw(canvas);
                        drawable.setAlpha(255);
                    } else {
                        drawable.draw(canvas);
                    }
                }
            } else if (this.drawDeleted && Theme.avatarDrawables[1] != null) {
                int w3 = Theme.avatarDrawables[1].getIntrinsicWidth();
                int h3 = Theme.avatarDrawables[1].getIntrinsicHeight();
                if (w3 > size - AndroidUtilities.dp(6.0f) || h3 > size - AndroidUtilities.dp(6.0f)) {
                    float scale = ((float) size) / ((float) AndroidUtilities.dp(50.0f));
                    w3 = (int) (((float) w3) * scale);
                    h3 = (int) (((float) h3) * scale);
                }
                int x3 = (size - w3) / 2;
                int y3 = (size - h3) / 2;
                Theme.avatarDrawables[1].setBounds(x3, y3, x3 + w3, y3 + h3);
                Theme.avatarDrawables[1].draw(canvas);
            } else if (this.textLayout != null) {
                float scale2 = ((float) size) / ((float) AndroidUtilities.dp(50.0f));
                canvas.scale(scale2, scale2, ((float) size) / 2.0f, ((float) size) / 2.0f);
                canvas.translate(((((float) size) - this.textWidth) / 2.0f) - this.textLeft, (((float) size) - this.textHeight) / 2.0f);
                this.textLayout.draw(canvas);
            }
            canvas.restore();
        }
    }

    public void setAlpha(int alpha2) {
        this.alpha = alpha2;
    }

    public void setColorFilter(ColorFilter cf) {
    }

    public int getOpacity() {
        return -2;
    }

    public int getIntrinsicWidth() {
        return 0;
    }

    public int getIntrinsicHeight() {
        return 0;
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color2 = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
        return color2 != null ? color2.intValue() : Theme.getColor(key);
    }
}
