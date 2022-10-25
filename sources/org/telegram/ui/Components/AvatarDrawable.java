package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.RectF;
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
import org.telegram.tgnet.TLRPC$ChatInvite;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes3.dex */
public class AvatarDrawable extends Drawable {
    private int alpha;
    private float archivedAvatarProgress;
    private int avatarType;
    private int color;
    private boolean drawDeleted;
    private TextPaint namePaint;
    private boolean needApplyColorAccent;
    private Theme.ResourcesProvider resourcesProvider;
    private int roundRadius;
    private boolean smallSize;
    private StringBuilder stringBuilder;
    private float textHeight;
    private StaticLayout textLayout;
    private float textLeft;
    private float textWidth;

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        return 0;
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        return 0;
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -2;
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
    }

    public void setProfile(boolean z) {
    }

    public AvatarDrawable() {
        this((Theme.ResourcesProvider) null);
    }

    public AvatarDrawable(Theme.ResourcesProvider resourcesProvider) {
        this.stringBuilder = new StringBuilder(5);
        this.roundRadius = -1;
        this.alpha = 255;
        this.resourcesProvider = resourcesProvider;
        TextPaint textPaint = new TextPaint(1);
        this.namePaint = textPaint;
        textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.namePaint.setTextSize(AndroidUtilities.dp(18.0f));
    }

    public AvatarDrawable(TLRPC$User tLRPC$User) {
        this(tLRPC$User, false);
    }

    public AvatarDrawable(TLRPC$Chat tLRPC$Chat) {
        this(tLRPC$Chat, false);
    }

    public AvatarDrawable(TLRPC$User tLRPC$User, boolean z) {
        this();
        if (tLRPC$User != null) {
            setInfo(tLRPC$User.id, tLRPC$User.first_name, tLRPC$User.last_name, null);
            this.drawDeleted = UserObject.isDeleted(tLRPC$User);
        }
    }

    public AvatarDrawable(TLRPC$Chat tLRPC$Chat, boolean z) {
        this();
        if (tLRPC$Chat != null) {
            setInfo(tLRPC$Chat.id, tLRPC$Chat.title, null, null);
        }
    }

    private static int getColorIndex(long j) {
        return (j < 0 || j >= 7) ? (int) Math.abs(j % Theme.keys_avatar_background.length) : (int) j;
    }

    public static int getColorForId(long j) {
        return Theme.getColor(Theme.keys_avatar_background[getColorIndex(j)]);
    }

    public static int getIconColorForId(long j, Theme.ResourcesProvider resourcesProvider) {
        return Theme.getColor("avatar_actionBarIconBlue", resourcesProvider);
    }

    public static int getProfileColorForId(long j, Theme.ResourcesProvider resourcesProvider) {
        return Theme.getColor(Theme.keys_avatar_background[getColorIndex(j)], resourcesProvider);
    }

    public static int getProfileTextColorForId(long j, Theme.ResourcesProvider resourcesProvider) {
        return Theme.getColor("avatar_subtitleInProfileBlue", resourcesProvider);
    }

    public static int getProfileBackColorForId(long j, Theme.ResourcesProvider resourcesProvider) {
        return Theme.getColor("avatar_backgroundActionBarBlue", resourcesProvider);
    }

    public static String getNameColorNameForId(long j) {
        return Theme.keys_avatar_nameInMessage[getColorIndex(j)];
    }

    public void setInfo(TLRPC$User tLRPC$User) {
        if (tLRPC$User != null) {
            setInfo(tLRPC$User.id, tLRPC$User.first_name, tLRPC$User.last_name, null);
            this.drawDeleted = UserObject.isDeleted(tLRPC$User);
        }
    }

    public void setInfo(TLObject tLObject) {
        if (tLObject instanceof TLRPC$User) {
            setInfo((TLRPC$User) tLObject);
        } else if (tLObject instanceof TLRPC$Chat) {
            setInfo((TLRPC$Chat) tLObject);
        } else if (!(tLObject instanceof TLRPC$ChatInvite)) {
        } else {
            setInfo((TLRPC$ChatInvite) tLObject);
        }
    }

    public void setSmallSize(boolean z) {
        this.smallSize = z;
    }

    public void setAvatarType(int i) {
        this.avatarType = i;
        boolean z = true;
        if (i == 13) {
            this.color = Theme.getColor("chats_actionBackground");
        } else if (i == 2) {
            this.color = getThemedColor("avatar_backgroundArchivedHidden");
        } else if (i == 12) {
            this.color = getThemedColor("avatar_backgroundSaved");
        } else if (i == 1) {
            this.color = getThemedColor("avatar_backgroundSaved");
        } else if (i == 3) {
            this.color = getThemedColor(Theme.keys_avatar_background[getColorIndex(5L)]);
        } else if (i == 4) {
            this.color = getThemedColor(Theme.keys_avatar_background[getColorIndex(5L)]);
        } else if (i == 5) {
            this.color = getThemedColor(Theme.keys_avatar_background[getColorIndex(4L)]);
        } else if (i == 6) {
            this.color = getThemedColor(Theme.keys_avatar_background[getColorIndex(3L)]);
        } else if (i == 7) {
            this.color = getThemedColor(Theme.keys_avatar_background[getColorIndex(1L)]);
        } else if (i == 8) {
            this.color = getThemedColor(Theme.keys_avatar_background[getColorIndex(0L)]);
        } else if (i == 9) {
            this.color = getThemedColor(Theme.keys_avatar_background[getColorIndex(6L)]);
        } else if (i == 10) {
            this.color = getThemedColor(Theme.keys_avatar_background[getColorIndex(5L)]);
        } else {
            this.color = getThemedColor(Theme.keys_avatar_background[getColorIndex(4L)]);
        }
        int i2 = this.avatarType;
        if (i2 == 2 || i2 == 1 || i2 == 12) {
            z = false;
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
            setInfo(tLRPC$Chat.id, tLRPC$Chat.title, null, null);
        }
    }

    public void setInfo(TLRPC$ChatInvite tLRPC$ChatInvite) {
        if (tLRPC$ChatInvite != null) {
            setInfo(0L, tLRPC$ChatInvite.title, null, null);
        }
    }

    public void setColor(int i) {
        this.color = i;
        this.needApplyColorAccent = false;
    }

    public void setTextSize(int i) {
        this.namePaint.setTextSize(i);
    }

    public void setInfo(long j, String str, String str2) {
        setInfo(j, str, str2, null);
    }

    public int getColor() {
        return this.needApplyColorAccent ? Theme.changeColorAccent(this.color) : this.color;
    }

    public void setInfo(long j, String str, String str2, String str3) {
        this.color = getThemedColor(Theme.keys_avatar_background[getColorIndex(j)]);
        this.needApplyColorAccent = j == 5;
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
                Integer num = null;
                for (int length = str2.length() - 1; length >= 0 && (num == null || str2.charAt(length) != ' '); length--) {
                    num = Integer.valueOf(str2.codePointAt(length));
                }
                if (Build.VERSION.SDK_INT > 17) {
                    this.stringBuilder.append("\u200c");
                }
                this.stringBuilder.appendCodePoint(num.intValue());
            } else if (str != null && str.length() > 0) {
                int length2 = str.length() - 1;
                while (true) {
                    if (length2 < 0) {
                        break;
                    }
                    if (str.charAt(length2) == ' ' && length2 != str.length() - 1) {
                        int i = length2 + 1;
                        if (str.charAt(i) != ' ') {
                            if (Build.VERSION.SDK_INT > 17) {
                                this.stringBuilder.append("\u200c");
                            }
                            this.stringBuilder.appendCodePoint(str.codePointAt(i));
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
                if (staticLayout.getLineCount() <= 0) {
                    return;
                }
                this.textLeft = this.textLayout.getLineLeft(0);
                this.textWidth = this.textLayout.getLineWidth(0);
                this.textHeight = this.textLayout.getLineBottom(0);
                return;
            } catch (Exception e) {
                FileLog.e(e);
                return;
            }
        }
        this.textLayout = null;
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        Drawable drawable;
        android.graphics.Rect bounds = getBounds();
        if (bounds == null) {
            return;
        }
        int width = bounds.width();
        this.namePaint.setColor(ColorUtils.setAlphaComponent(getThemedColor("avatar_text"), this.alpha));
        Theme.avatar_backgroundPaint.setColor(ColorUtils.setAlphaComponent(getColor(), this.alpha));
        canvas.save();
        canvas.translate(bounds.left, bounds.top);
        if (this.roundRadius > 0) {
            RectF rectF = AndroidUtilities.rectTmp;
            float f = width;
            rectF.set(0.0f, 0.0f, f, f);
            int i = this.roundRadius;
            canvas.drawRoundRect(rectF, i, i, Theme.avatar_backgroundPaint);
        } else {
            float f2 = width / 2.0f;
            canvas.drawCircle(f2, f2, f2, Theme.avatar_backgroundPaint);
        }
        int i2 = this.avatarType;
        if (i2 == 2) {
            if (this.archivedAvatarProgress != 0.0f) {
                Theme.avatar_backgroundPaint.setColor(ColorUtils.setAlphaComponent(getThemedColor("avatar_backgroundArchived"), this.alpha));
                float f3 = width / 2.0f;
                canvas.drawCircle(f3, f3, this.archivedAvatarProgress * f3, Theme.avatar_backgroundPaint);
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
            int i3 = (width - intrinsicWidth) / 2;
            int i4 = (width - intrinsicHeight) / 2;
            canvas.save();
            Theme.dialogs_archiveAvatarDrawable.setBounds(i3, i4, intrinsicWidth + i3, intrinsicHeight + i4);
            Theme.dialogs_archiveAvatarDrawable.draw(canvas);
            canvas.restore();
        } else if (i2 != 0) {
            if (i2 == 12) {
                drawable = Theme.avatarDrawables[11];
            } else if (i2 == 1) {
                drawable = Theme.avatarDrawables[0];
            } else if (i2 == 3) {
                drawable = Theme.avatarDrawables[10];
            } else if (i2 == 4) {
                drawable = Theme.avatarDrawables[2];
            } else if (i2 == 5) {
                drawable = Theme.avatarDrawables[3];
            } else if (i2 == 6) {
                drawable = Theme.avatarDrawables[4];
            } else if (i2 == 7) {
                drawable = Theme.avatarDrawables[5];
            } else if (i2 == 8) {
                drawable = Theme.avatarDrawables[6];
            } else if (i2 == 9) {
                drawable = Theme.avatarDrawables[7];
            } else if (i2 == 10) {
                drawable = Theme.avatarDrawables[8];
            } else {
                drawable = Theme.avatarDrawables[9];
            }
            if (drawable != null) {
                int intrinsicWidth2 = drawable.getIntrinsicWidth();
                int intrinsicHeight2 = drawable.getIntrinsicHeight();
                if (this.smallSize) {
                    intrinsicWidth2 = (int) (intrinsicWidth2 * 0.8f);
                    intrinsicHeight2 = (int) (intrinsicHeight2 * 0.8f);
                }
                int i5 = (width - intrinsicWidth2) / 2;
                int i6 = (width - intrinsicHeight2) / 2;
                drawable.setBounds(i5, i6, intrinsicWidth2 + i5, intrinsicHeight2 + i6);
                int i7 = this.alpha;
                if (i7 != 255) {
                    drawable.setAlpha(i7);
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
                        float dp = width / AndroidUtilities.dp(50.0f);
                        intrinsicWidth3 = (int) (intrinsicWidth3 * dp);
                        intrinsicHeight3 = (int) (intrinsicHeight3 * dp);
                    }
                    int i8 = (width - intrinsicWidth3) / 2;
                    int i9 = (width - intrinsicHeight3) / 2;
                    Theme.avatarDrawables[1].setBounds(i8, i9, intrinsicWidth3 + i8, intrinsicHeight3 + i9);
                    Theme.avatarDrawables[1].draw(canvas);
                }
            }
            if (this.textLayout != null) {
                float f4 = width;
                float dp2 = f4 / AndroidUtilities.dp(50.0f);
                float f5 = f4 / 2.0f;
                canvas.scale(dp2, dp2, f5, f5);
                canvas.translate(((f4 - this.textWidth) / 2.0f) - this.textLeft, (f4 - this.textHeight) / 2.0f);
                this.textLayout.draw(canvas);
            }
        }
        canvas.restore();
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int i) {
        this.alpha = i;
    }

    private int getThemedColor(String str) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(str) : null;
        return color != null ? color.intValue() : Theme.getColor(str);
    }

    public void setRoundRadius(int i) {
        this.roundRadius = i;
    }
}
