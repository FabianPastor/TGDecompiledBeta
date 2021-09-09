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
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.Theme;

public class AvatarDrawable extends Drawable {
    private int alpha;
    private float archivedAvatarProgress;
    private int avatarType;
    private int color;
    private boolean drawDeleted;
    private boolean isProfile;
    private TextPaint namePaint;
    private boolean needApplyColorAccent;
    private boolean smallSize;
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

    public void setColorFilter(ColorFilter colorFilter) {
    }

    public AvatarDrawable() {
        this.stringBuilder = new StringBuilder(5);
        this.alpha = 255;
        TextPaint textPaint = new TextPaint(1);
        this.namePaint = textPaint;
        textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.namePaint.setTextSize((float) AndroidUtilities.dp(18.0f));
    }

    public AvatarDrawable(TLRPC$User tLRPC$User) {
        this(tLRPC$User, false);
    }

    public AvatarDrawable(TLRPC$Chat tLRPC$Chat) {
        this(tLRPC$Chat, false);
    }

    public AvatarDrawable(TLRPC$User tLRPC$User, boolean z) {
        this();
        this.isProfile = z;
        if (tLRPC$User != null) {
            setInfo(tLRPC$User.id, tLRPC$User.first_name, tLRPC$User.last_name, (String) null);
            this.drawDeleted = UserObject.isDeleted(tLRPC$User);
        }
    }

    public AvatarDrawable(TLRPC$Chat tLRPC$Chat, boolean z) {
        this();
        this.isProfile = z;
        if (tLRPC$Chat != null) {
            setInfo(tLRPC$Chat.id, tLRPC$Chat.title, (String) null, (String) null);
        }
    }

    public void setProfile(boolean z) {
        this.isProfile = z;
    }

    private static int getColorIndex(int i) {
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

    public void setInfo(TLRPC$User tLRPC$User) {
        if (tLRPC$User != null) {
            setInfo(tLRPC$User.id, tLRPC$User.first_name, tLRPC$User.last_name, (String) null);
            this.drawDeleted = UserObject.isDeleted(tLRPC$User);
        }
    }

    public void setInfo(TLObject tLObject) {
        if (tLObject instanceof TLRPC$User) {
            setInfo((TLRPC$User) tLObject);
        } else if (tLObject instanceof TLRPC$Chat) {
            setInfo((TLRPC$Chat) tLObject);
        }
    }

    public void setSmallSize(boolean z) {
        this.smallSize = z;
    }

    public void setAvatarType(int i) {
        this.avatarType = i;
        boolean z = false;
        if (i == 2) {
            this.color = Theme.getColor("avatar_backgroundArchivedHidden");
        } else if (i == 12) {
            this.color = Theme.getColor("avatar_backgroundSaved");
        } else if (i == 1) {
            this.color = Theme.getColor("avatar_backgroundSaved");
        } else if (i == 3) {
            this.color = getColorForId(5);
        } else if (i == 4) {
            this.color = getColorForId(5);
        } else if (i == 5) {
            this.color = getColorForId(4);
        } else if (i == 6) {
            this.color = getColorForId(3);
        } else if (i == 7) {
            this.color = getColorForId(1);
        } else if (i == 8) {
            this.color = getColorForId(0);
        } else if (i == 9) {
            this.color = getColorForId(6);
        } else if (i == 10) {
            this.color = getColorForId(5);
        } else {
            this.color = getColorForId(4);
        }
        int i2 = this.avatarType;
        if (!(i2 == 2 || i2 == 1 || i2 == 12)) {
            z = true;
        }
        this.needApplyColorAccent = z;
    }

    public void setArchivedAvatarHiddenProgress(float f) {
        this.archivedAvatarProgress = f;
    }

    public int getAvatarType() {
        return this.avatarType;
    }

    public void setInfo(TLRPC$Chat tLRPC$Chat) {
        if (tLRPC$Chat != null) {
            setInfo(tLRPC$Chat.id, tLRPC$Chat.title, (String) null, (String) null);
        }
    }

    public void setColor(int i) {
        this.color = i;
        this.needApplyColorAccent = false;
    }

    public void setTextSize(int i) {
        this.namePaint.setTextSize((float) i);
    }

    public void setInfo(int i, String str, String str2) {
        setInfo(i, str, str2, (String) null);
    }

    public int getColor() {
        return this.needApplyColorAccent ? Theme.changeColorAccent(this.color) : this.color;
    }

    public void setInfo(int i, String str, String str2, String str3) {
        if (this.isProfile) {
            this.color = getProfileColorForId(i);
        } else {
            this.color = getColorForId(i);
        }
        this.needApplyColorAccent = i == 5;
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
            if (str2 != null && str2.length() > 0) {
                int length = str2.length() - 1;
                Integer num = null;
                while (length >= 0 && (num == null || str2.charAt(length) != ' ')) {
                    num = Integer.valueOf(str2.codePointAt(length));
                    length--;
                }
                if (Build.VERSION.SDK_INT > 17) {
                    this.stringBuilder.append("‌");
                }
                this.stringBuilder.appendCodePoint(num.intValue());
            } else if (str != null && str.length() > 0) {
                int length2 = str.length() - 1;
                while (true) {
                    if (length2 < 0) {
                        break;
                    }
                    if (str.charAt(length2) == ' ' && length2 != str.length() - 1) {
                        int i2 = length2 + 1;
                        if (str.charAt(i2) != ' ') {
                            if (Build.VERSION.SDK_INT > 17) {
                                this.stringBuilder.append("‌");
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
            int width = bounds.width();
            this.namePaint.setColor(ColorUtils.setAlphaComponent(Theme.getColor("avatar_text"), this.alpha));
            Theme.avatar_backgroundPaint.setColor(ColorUtils.setAlphaComponent(getColor(), this.alpha));
            canvas.save();
            canvas.translate((float) bounds.left, (float) bounds.top);
            float f = (float) width;
            float f2 = f / 2.0f;
            canvas.drawCircle(f2, f2, f2, Theme.avatar_backgroundPaint);
            int i = this.avatarType;
            if (i == 2) {
                if (this.archivedAvatarProgress != 0.0f) {
                    Theme.avatar_backgroundPaint.setColor(ColorUtils.setAlphaComponent(Theme.getColor("avatar_backgroundArchived"), this.alpha));
                    canvas.drawCircle(f2, f2, this.archivedAvatarProgress * f2, Theme.avatar_backgroundPaint);
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
                int intrinsicWidth = Theme.dialogs_archiveAvatarDrawable.getIntrinsicWidth();
                int intrinsicHeight = Theme.dialogs_archiveAvatarDrawable.getIntrinsicHeight();
                int i2 = (width - intrinsicWidth) / 2;
                int i3 = (width - intrinsicHeight) / 2;
                canvas.save();
                Theme.dialogs_archiveAvatarDrawable.setBounds(i2, i3, intrinsicWidth + i2, intrinsicHeight + i3);
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
                    int intrinsicWidth2 = drawable.getIntrinsicWidth();
                    int intrinsicHeight2 = drawable.getIntrinsicHeight();
                    if (this.smallSize) {
                        intrinsicWidth2 = (int) (((float) intrinsicWidth2) * 0.8f);
                        intrinsicHeight2 = (int) (((float) intrinsicHeight2) * 0.8f);
                    }
                    int i4 = (width - intrinsicWidth2) / 2;
                    int i5 = (width - intrinsicHeight2) / 2;
                    drawable.setBounds(i4, i5, intrinsicWidth2 + i4, intrinsicHeight2 + i5);
                    int i6 = this.alpha;
                    if (i6 != 255) {
                        drawable.setAlpha(i6);
                        drawable.draw(canvas);
                        drawable.setAlpha(255);
                    } else {
                        drawable.draw(canvas);
                    }
                }
            } else {
                if (this.drawDeleted) {
                    Drawable[] drawableArr = Theme.avatarDrawables;
                    if (drawableArr[1] != null) {
                        int intrinsicWidth3 = drawableArr[1].getIntrinsicWidth();
                        int intrinsicHeight3 = Theme.avatarDrawables[1].getIntrinsicHeight();
                        if (intrinsicWidth3 > width - AndroidUtilities.dp(6.0f) || intrinsicHeight3 > width - AndroidUtilities.dp(6.0f)) {
                            float dp = f / ((float) AndroidUtilities.dp(50.0f));
                            intrinsicWidth3 = (int) (((float) intrinsicWidth3) * dp);
                            intrinsicHeight3 = (int) (((float) intrinsicHeight3) * dp);
                        }
                        int i7 = (width - intrinsicWidth3) / 2;
                        int i8 = (width - intrinsicHeight3) / 2;
                        Theme.avatarDrawables[1].setBounds(i7, i8, intrinsicWidth3 + i7, intrinsicHeight3 + i8);
                        Theme.avatarDrawables[1].draw(canvas);
                    }
                }
                if (this.textLayout != null) {
                    float dp2 = f / ((float) AndroidUtilities.dp(50.0f));
                    canvas.scale(dp2, dp2, f2, f2);
                    canvas.translate(((f - this.textWidth) / 2.0f) - this.textLeft, (f - this.textHeight) / 2.0f);
                    this.textLayout.draw(canvas);
                }
            }
            canvas.restore();
        }
    }

    public void setAlpha(int i) {
        this.alpha = i;
    }
}
