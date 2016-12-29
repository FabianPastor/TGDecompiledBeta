package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.Theme;

public class AvatarDrawable extends Drawable {
    private static int[] arrColors = new int[]{-1743531, -881592, -7436818, -8992691, -10502443, -11232035, -7436818, -887654};
    private static int[] arrColorsButtons = new int[]{Theme.ACTION_BAR_RED_SELECTOR_COLOR, Theme.ACTION_BAR_ORANGE_SELECTOR_COLOR, Theme.ACTION_BAR_VIOLET_SELECTOR_COLOR, Theme.ACTION_BAR_GREEN_SELECTOR_COLOR, Theme.ACTION_BAR_CYAN_SELECTOR_COLOR, Theme.ACTION_BAR_BLUE_SELECTOR_COLOR, Theme.ACTION_BAR_VIOLET_SELECTOR_COLOR, Theme.ACTION_BAR_BLUE_SELECTOR_COLOR};
    private static int[] arrColorsNames = new int[]{-3516848, -2589911, -11627828, -11488718, -12406360, -11627828, -11627828, -11627828};
    private static int[] arrColorsProfiles = new int[]{-2592923, -615071, -7570990, -9981091, -11099461, Theme.ACTION_BAR_MAIN_AVATAR_COLOR, -7570990, -819290};
    private static int[] arrColorsProfilesBack = new int[]{-3514282, -947900, -8557884, -11099828, -12283220, Theme.ACTION_BAR_PROFILE_COLOR, -8557884, -11762506};
    private static int[] arrColorsProfilesText = new int[]{-406587, -139832, -3291923, -4133446, -4660496, Theme.ACTION_BAR_PROFILE_SUBTITLE_COLOR, -3291923, -4990985};
    private static int[] arrGroupCreateColors = new int[]{-136730, -267558, -1842948, -2296624, -2230789, -2888962, -1842948, -7189};
    private static Drawable broadcastDrawable;
    private static Paint paint = new Paint(1);
    private static Drawable photoDrawable;
    private int color;
    private boolean drawBrodcast;
    private boolean drawPhoto;
    private boolean isProfile;
    private TextPaint namePaint;
    private StringBuilder stringBuilder;
    private float textHeight;
    private StaticLayout textLayout;
    private float textLeft;
    private float textWidth;

    public AvatarDrawable() {
        this.stringBuilder = new StringBuilder(5);
        this.namePaint = new TextPaint(1);
        this.namePaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.namePaint.setColor(-1);
        this.namePaint.setTextSize((float) AndroidUtilities.dp(18.0f));
        if (broadcastDrawable == null) {
            broadcastDrawable = ApplicationLoader.applicationContext.getResources().getDrawable(R.drawable.broadcast_w);
        }
    }

    public AvatarDrawable(User user) {
        this(user, false);
    }

    public AvatarDrawable(Chat chat) {
        this(chat, false);
    }

    public AvatarDrawable(User user, boolean profile) {
        this();
        this.isProfile = profile;
        if (user != null) {
            setInfo(user.id, user.first_name, user.last_name, false, null);
        }
    }

    public AvatarDrawable(Chat chat, boolean profile) {
        this();
        this.isProfile = profile;
        if (chat != null) {
            setInfo(chat.id, chat.title, null, chat.id < 0, null);
        }
    }

    public void setProfile(boolean value) {
        this.isProfile = value;
    }

    public static int getColorIndex(int id) {
        return (id < 0 || id >= 8) ? Math.abs(id % arrColors.length) : id;
    }

    public static int getColorForId(int id) {
        return arrColors[getColorIndex(id)];
    }

    public static int getButtonColorForId(int id) {
        return arrColorsButtons[getColorIndex(id)];
    }

    public static int getProfileColorForId(int id) {
        return arrColorsProfiles[getColorIndex(id)];
    }

    public static int getProfileTextColorForId(int id) {
        return arrColorsProfilesText[getColorIndex(id)];
    }

    public static int getProfileBackColorForId(int id) {
        return arrColorsProfilesBack[getColorIndex(id)];
    }

    public static int getNameColorForId(int id) {
        return arrColorsNames[getColorIndex(id)];
    }

    public static int getGroupCreateColor(int id) {
        return arrGroupCreateColors[getColorIndex(id)];
    }

    public void setInfo(User user) {
        if (user != null) {
            setInfo(user.id, user.first_name, user.last_name, false, null);
        }
    }

    public void setInfo(Chat chat) {
        if (chat != null) {
            setInfo(chat.id, chat.title, null, chat.id < 0, null);
        }
    }

    public void setColor(int value) {
        this.color = value;
    }

    public void setTextSize(int size) {
        this.namePaint.setTextSize((float) size);
    }

    public void setInfo(int id, String firstName, String lastName, boolean isBroadcast) {
        setInfo(id, firstName, lastName, isBroadcast, null);
    }

    public int getColor() {
        return this.color;
    }

    public void setInfo(int id, String firstName, String lastName, boolean isBroadcast, String custom) {
        if (this.isProfile) {
            this.color = arrColorsProfiles[getColorIndex(id)];
        } else {
            this.color = arrColors[getColorIndex(id)];
        }
        this.drawBrodcast = isBroadcast;
        if (firstName == null || firstName.length() == 0) {
            firstName = lastName;
            lastName = null;
        }
        this.stringBuilder.setLength(0);
        if (custom != null) {
            this.stringBuilder.append(custom);
        } else {
            if (firstName != null && firstName.length() > 0) {
                this.stringBuilder.append(firstName.substring(0, 1));
            }
            int a;
            if (lastName != null && lastName.length() > 0) {
                String lastch = null;
                a = lastName.length() - 1;
                while (a >= 0 && (lastch == null || lastName.charAt(a) != ' ')) {
                    lastch = lastName.substring(a, a + 1);
                    a--;
                }
                if (VERSION.SDK_INT >= 16) {
                    this.stringBuilder.append("‌");
                }
                this.stringBuilder.append(lastch);
            } else if (firstName != null && firstName.length() > 0) {
                a = firstName.length() - 1;
                while (a >= 0) {
                    if (firstName.charAt(a) != ' ' || a == firstName.length() - 1 || firstName.charAt(a + 1) == ' ') {
                        a--;
                    } else {
                        if (VERSION.SDK_INT >= 16) {
                            this.stringBuilder.append("‌");
                        }
                        this.stringBuilder.append(firstName.substring(a + 1, a + 2));
                    }
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
            } catch (Throwable e) {
                FileLog.e("tmessages", e);
                return;
            }
        }
        this.textLayout = null;
    }

    public void setDrawPhoto(boolean value) {
        if (value && photoDrawable == null) {
            photoDrawable = ApplicationLoader.applicationContext.getResources().getDrawable(R.drawable.photo_w);
        }
        this.drawPhoto = value;
    }

    public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        if (bounds != null) {
            int size = bounds.width();
            paint.setColor(this.color);
            canvas.save();
            canvas.translate((float) bounds.left, (float) bounds.top);
            canvas.drawCircle((float) (size / 2), (float) (size / 2), (float) (size / 2), paint);
            int x;
            int y;
            if (this.drawBrodcast && broadcastDrawable != null) {
                x = (size - broadcastDrawable.getIntrinsicWidth()) / 2;
                y = (size - broadcastDrawable.getIntrinsicHeight()) / 2;
                broadcastDrawable.setBounds(x, y, broadcastDrawable.getIntrinsicWidth() + x, broadcastDrawable.getIntrinsicHeight() + y);
                broadcastDrawable.draw(canvas);
            } else if (this.textLayout != null) {
                canvas.translate(((((float) size) - this.textWidth) / 2.0f) - this.textLeft, (((float) size) - this.textHeight) / 2.0f);
                this.textLayout.draw(canvas);
            } else if (this.drawPhoto && photoDrawable != null) {
                x = (size - photoDrawable.getIntrinsicWidth()) / 2;
                y = (size - photoDrawable.getIntrinsicHeight()) / 2;
                photoDrawable.setBounds(x, y, photoDrawable.getIntrinsicWidth() + x, photoDrawable.getIntrinsicHeight() + y);
                photoDrawable.draw(canvas);
            }
            canvas.restore();
        }
    }

    public void setAlpha(int alpha) {
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
}
