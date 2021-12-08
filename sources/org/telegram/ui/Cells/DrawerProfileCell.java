package org.telegram.ui.Cells;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.SnowflakesEffect;

public class DrawerProfileCell extends FrameLayout {
    public static boolean switchingTheme;
    private boolean accountsShown;
    private ImageView arrowView;
    private BackupImageView avatarImageView;
    private Paint backPaint = new Paint(1);
    private Integer currentColor;
    private Integer currentMoonColor;
    private int darkThemeBackgroundColor;
    private RLottieImageView darkThemeView;
    private Rect destRect = new Rect();
    private TextView nameTextView;
    private Paint paint = new Paint();
    private TextView phoneTextView;
    private ImageView shadowView;
    private SnowflakesEffect snowflakesEffect;
    private Rect srcRect = new Rect();
    /* access modifiers changed from: private */
    public RLottieDrawable sunDrawable;

    public DrawerProfileCell(Context context) {
        super(context);
        ImageView imageView = new ImageView(context);
        this.shadowView = imageView;
        imageView.setVisibility(4);
        this.shadowView.setScaleType(ImageView.ScaleType.FIT_XY);
        this.shadowView.setImageResource(NUM);
        addView(this.shadowView, LayoutHelper.createFrame(-1, 70, 83));
        BackupImageView backupImageView = new BackupImageView(context);
        this.avatarImageView = backupImageView;
        backupImageView.getImageReceiver().setRoundRadius(AndroidUtilities.dp(32.0f));
        addView(this.avatarImageView, LayoutHelper.createFrame(64, 64.0f, 83, 16.0f, 0.0f, 0.0f, 67.0f));
        TextView textView = new TextView(context);
        this.nameTextView = textView;
        textView.setTextSize(1, 15.0f);
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.nameTextView.setLines(1);
        this.nameTextView.setMaxLines(1);
        this.nameTextView.setSingleLine(true);
        this.nameTextView.setGravity(3);
        this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
        addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0f, 83, 16.0f, 0.0f, 76.0f, 28.0f));
        TextView textView2 = new TextView(context);
        this.phoneTextView = textView2;
        textView2.setTextSize(1, 13.0f);
        this.phoneTextView.setLines(1);
        this.phoneTextView.setMaxLines(1);
        this.phoneTextView.setSingleLine(true);
        this.phoneTextView.setGravity(3);
        addView(this.phoneTextView, LayoutHelper.createFrame(-1, -2.0f, 83, 16.0f, 0.0f, 76.0f, 9.0f));
        ImageView imageView2 = new ImageView(context);
        this.arrowView = imageView2;
        imageView2.setScaleType(ImageView.ScaleType.CENTER);
        this.arrowView.setImageResource(NUM);
        addView(this.arrowView, LayoutHelper.createFrame(59, 59, 85));
        setArrowState(false);
        this.sunDrawable = new RLottieDrawable(NUM, "NUM", AndroidUtilities.dp(28.0f), AndroidUtilities.dp(28.0f), true, (int[]) null);
        if (Theme.isCurrentThemeDay()) {
            this.sunDrawable.setCustomEndFrame(36);
        } else {
            this.sunDrawable.setCustomEndFrame(0);
            this.sunDrawable.setCurrentFrame(36);
        }
        this.sunDrawable.setPlayInDirectionOfCustomEndFrame(true);
        this.darkThemeView = new RLottieImageView(context) {
            public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
                super.onInitializeAccessibilityNodeInfo(info);
                if (DrawerProfileCell.this.sunDrawable.getCustomEndFrame() != 0) {
                    info.setText(LocaleController.getString("AccDescrSwitchToNightTheme", NUM));
                } else {
                    info.setText(LocaleController.getString("AccDescrSwitchToDayTheme", NUM));
                }
            }
        };
        this.sunDrawable.beginApplyLayerColors();
        int color = Theme.getColor("chats_menuName");
        this.sunDrawable.setLayerColor("Sunny.**", color);
        this.sunDrawable.setLayerColor("Path 6.**", color);
        this.sunDrawable.setLayerColor("Path.**", color);
        this.sunDrawable.setLayerColor("Path 5.**", color);
        this.sunDrawable.commitApplyLayerColors();
        this.darkThemeView.setScaleType(ImageView.ScaleType.CENTER);
        this.darkThemeView.setAnimation(this.sunDrawable);
        if (Build.VERSION.SDK_INT >= 21) {
            RLottieImageView rLottieImageView = this.darkThemeView;
            int color2 = Theme.getColor("listSelectorSDK21");
            this.darkThemeBackgroundColor = color2;
            rLottieImageView.setBackgroundDrawable(Theme.createSelectorDrawable(color2, 1, AndroidUtilities.dp(17.0f)));
            Theme.setRippleDrawableForceSoftware((RippleDrawable) this.darkThemeView.getBackground());
        }
        this.darkThemeView.setOnClickListener(new DrawerProfileCell$$ExternalSyntheticLambda0(this));
        addView(this.darkThemeView, LayoutHelper.createFrame(48, 48.0f, 85, 0.0f, 0.0f, 6.0f, 90.0f));
        if (Theme.getEventType() == 0) {
            SnowflakesEffect snowflakesEffect2 = new SnowflakesEffect();
            this.snowflakesEffect = snowflakesEffect2;
            snowflakesEffect2.setColorKey("chats_menuName");
        }
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Cells-DrawerProfileCell  reason: not valid java name */
    public /* synthetic */ void m1532lambda$new$0$orgtelegramuiCellsDrawerProfileCell(View v) {
        Theme.ThemeInfo themeInfo;
        if (!switchingTheme) {
            switchingTheme = true;
            SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", 0);
            String dayThemeName = preferences.getString("lastDayTheme", "Blue");
            if (Theme.getTheme(dayThemeName) == null || Theme.getTheme(dayThemeName).isDark()) {
                dayThemeName = "Blue";
            }
            String nightThemeName = preferences.getString("lastDarkTheme", "Dark Blue");
            if (Theme.getTheme(nightThemeName) == null || !Theme.getTheme(nightThemeName).isDark()) {
                nightThemeName = "Dark Blue";
            }
            Theme.ThemeInfo themeInfo2 = Theme.getActiveTheme();
            if (dayThemeName.equals(nightThemeName)) {
                if (themeInfo2.isDark() || dayThemeName.equals("Dark Blue") || dayThemeName.equals("Night")) {
                    dayThemeName = "Blue";
                } else {
                    nightThemeName = "Dark Blue";
                }
            }
            boolean equals = dayThemeName.equals(themeInfo2.getKey());
            boolean toDark = equals;
            if (equals) {
                themeInfo = Theme.getTheme(nightThemeName);
                this.sunDrawable.setCustomEndFrame(36);
            } else {
                themeInfo = Theme.getTheme(dayThemeName);
                this.sunDrawable.setCustomEndFrame(0);
            }
            this.darkThemeView.playAnimation();
            if (Theme.selectedAutoNightType != 0) {
                Toast.makeText(getContext(), LocaleController.getString("AutoNightModeOff", NUM), 0).show();
                Theme.selectedAutoNightType = 0;
                Theme.saveAutoNightThemeConfig();
                Theme.cancelAutoNightThemeCallbacks();
            }
            switchTheme(themeInfo, toDark);
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v1, resolved type: java.lang.Object[]} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void switchTheme(org.telegram.ui.ActionBar.Theme.ThemeInfo r8, boolean r9) {
        /*
            r7 = this;
            r0 = 2
            int[] r1 = new int[r0]
            org.telegram.ui.Components.RLottieImageView r2 = r7.darkThemeView
            r2.getLocationInWindow(r1)
            r2 = 0
            r3 = r1[r2]
            org.telegram.ui.Components.RLottieImageView r4 = r7.darkThemeView
            int r4 = r4.getMeasuredWidth()
            int r4 = r4 / r0
            int r3 = r3 + r4
            r1[r2] = r3
            r3 = 1
            r4 = r1[r3]
            org.telegram.ui.Components.RLottieImageView r5 = r7.darkThemeView
            int r5 = r5.getMeasuredHeight()
            int r5 = r5 / r0
            int r4 = r4 + r5
            r1[r3] = r4
            org.telegram.messenger.NotificationCenter r4 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r5 = org.telegram.messenger.NotificationCenter.needSetDayNightTheme
            r6 = 6
            java.lang.Object[] r6 = new java.lang.Object[r6]
            r6[r2] = r8
            java.lang.Boolean r2 = java.lang.Boolean.valueOf(r2)
            r6[r3] = r2
            r6[r0] = r1
            r0 = -1
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            r2 = 3
            r6[r2] = r0
            java.lang.Boolean r0 = java.lang.Boolean.valueOf(r9)
            r2 = 4
            r6[r2] = r0
            org.telegram.ui.Components.RLottieImageView r0 = r7.darkThemeView
            r2 = 5
            r6[r2] = r0
            r4.postNotificationName(r5, r6)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.DrawerProfileCell.switchTheme(org.telegram.ui.ActionBar.Theme$ThemeInfo, boolean):void");
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        updateColors();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (Build.VERSION.SDK_INT >= 21) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(148.0f) + AndroidUtilities.statusBarHeight, NUM));
            return;
        }
        try {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(148.0f), NUM));
        } catch (Exception e) {
            setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), AndroidUtilities.dp(148.0f));
            FileLog.e((Throwable) e);
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int color;
        Canvas canvas2 = canvas;
        Drawable backgroundDrawable = Theme.getCachedWallpaper();
        String backgroundKey = applyBackground(false);
        boolean useImageBackground = !backgroundKey.equals("chats_menuTopBackground") && Theme.isCustomTheme() && !Theme.isPatternWallpaper() && backgroundDrawable != null && !(backgroundDrawable instanceof ColorDrawable) && !(backgroundDrawable instanceof GradientDrawable);
        boolean drawCatsShadow = false;
        int darkBackColor = 0;
        if (!useImageBackground && Theme.hasThemeKey("chats_menuTopShadowCats")) {
            color = Theme.getColor("chats_menuTopShadowCats");
            drawCatsShadow = true;
        } else if (Theme.hasThemeKey("chats_menuTopShadow")) {
            color = Theme.getColor("chats_menuTopShadow");
        } else {
            color = Theme.getServiceMessageColor() | -16777216;
        }
        Integer num = this.currentColor;
        if (num == null || num.intValue() != color) {
            this.currentColor = Integer.valueOf(color);
            this.shadowView.getDrawable().setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
        }
        int color2 = Theme.getColor("chats_menuName");
        Integer num2 = this.currentMoonColor;
        if (num2 == null || num2.intValue() != color2) {
            this.currentMoonColor = Integer.valueOf(color2);
            this.sunDrawable.beginApplyLayerColors();
            this.sunDrawable.setLayerColor("Sunny.**", this.currentMoonColor.intValue());
            this.sunDrawable.setLayerColor("Path 6.**", this.currentMoonColor.intValue());
            this.sunDrawable.setLayerColor("Path.**", this.currentMoonColor.intValue());
            this.sunDrawable.setLayerColor("Path 5.**", this.currentMoonColor.intValue());
            this.sunDrawable.commitApplyLayerColors();
        }
        this.nameTextView.setTextColor(Theme.getColor("chats_menuName"));
        if (useImageBackground) {
            this.phoneTextView.setTextColor(Theme.getColor("chats_menuPhone"));
            if (this.shadowView.getVisibility() != 0) {
                this.shadowView.setVisibility(0);
            }
            if (backgroundDrawable instanceof ColorDrawable) {
                int i = color2;
            } else if (backgroundDrawable instanceof GradientDrawable) {
                String str = backgroundKey;
                int i2 = color2;
            } else if (backgroundDrawable instanceof BitmapDrawable) {
                Bitmap bitmap = ((BitmapDrawable) backgroundDrawable).getBitmap();
                float scale = Math.max(((float) getMeasuredWidth()) / ((float) bitmap.getWidth()), ((float) getMeasuredHeight()) / ((float) bitmap.getHeight()));
                int width = (int) (((float) getMeasuredWidth()) / scale);
                int height = (int) (((float) getMeasuredHeight()) / scale);
                int x = (bitmap.getWidth() - width) / 2;
                String str2 = backgroundKey;
                int y = (bitmap.getHeight() - height) / 2;
                int i3 = color2;
                this.srcRect.set(x, y, x + width, y + height);
                int i4 = y;
                this.destRect.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
                try {
                    canvas2.drawBitmap(bitmap, this.srcRect, this.destRect, this.paint);
                } catch (Throwable e) {
                    FileLog.e(e);
                }
                darkBackColor = (Theme.getServiceMessageColor() & 16777215) | NUM;
            } else {
                int i5 = color2;
            }
            backgroundDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
            backgroundDrawable.draw(canvas2);
            darkBackColor = Theme.getColor("listSelectorSDK21");
        } else {
            int i6 = color2;
            int visibility = drawCatsShadow ? 0 : 4;
            if (this.shadowView.getVisibility() != visibility) {
                this.shadowView.setVisibility(visibility);
            }
            this.phoneTextView.setTextColor(Theme.getColor("chats_menuPhoneCats"));
            super.onDraw(canvas);
            darkBackColor = Theme.getColor("listSelectorSDK21");
        }
        if (darkBackColor != 0) {
            if (darkBackColor != this.darkThemeBackgroundColor) {
                Paint paint2 = this.backPaint;
                this.darkThemeBackgroundColor = darkBackColor;
                paint2.setColor(darkBackColor);
                if (Build.VERSION.SDK_INT >= 21) {
                    Drawable background = this.darkThemeView.getBackground();
                    this.darkThemeBackgroundColor = darkBackColor;
                    Theme.setSelectorDrawableColor(background, darkBackColor, true);
                }
            }
            if (useImageBackground && (backgroundDrawable instanceof BitmapDrawable)) {
                canvas2.drawCircle(this.darkThemeView.getX() + ((float) (this.darkThemeView.getMeasuredWidth() / 2)), this.darkThemeView.getY() + ((float) (this.darkThemeView.getMeasuredHeight() / 2)), (float) AndroidUtilities.dp(17.0f), this.backPaint);
            }
        }
        SnowflakesEffect snowflakesEffect2 = this.snowflakesEffect;
        if (snowflakesEffect2 != null) {
            snowflakesEffect2.onDraw(this, canvas2);
        }
    }

    public boolean isInAvatar(float x, float y) {
        return x >= ((float) this.avatarImageView.getLeft()) && x <= ((float) this.avatarImageView.getRight()) && y >= ((float) this.avatarImageView.getTop()) && y <= ((float) this.avatarImageView.getBottom());
    }

    public boolean hasAvatar() {
        return this.avatarImageView.getImageReceiver().hasNotThumb();
    }

    public boolean isAccountsShown() {
        return this.accountsShown;
    }

    public void setAccountsShown(boolean value, boolean animated) {
        if (this.accountsShown != value) {
            this.accountsShown = value;
            setArrowState(animated);
        }
    }

    public void setUser(TLRPC.User user, boolean accounts) {
        if (user != null) {
            this.accountsShown = accounts;
            setArrowState(false);
            this.nameTextView.setText(UserObject.getUserName(user));
            TextView textView = this.phoneTextView;
            PhoneFormat instance = PhoneFormat.getInstance();
            textView.setText(instance.format("+" + user.phone));
            AvatarDrawable avatarDrawable = new AvatarDrawable(user);
            avatarDrawable.setColor(Theme.getColor("avatar_backgroundInProfileBlue"));
            this.avatarImageView.setForUserOrChat(user, avatarDrawable);
            applyBackground(true);
        }
    }

    public String applyBackground(boolean force) {
        String currentTag = (String) getTag();
        String backgroundKey = "chats_menuTopBackground";
        if (!Theme.hasThemeKey(backgroundKey) || Theme.getColor(backgroundKey) == 0) {
            backgroundKey = "chats_menuTopBackgroundCats";
        }
        if (force || !backgroundKey.equals(currentTag)) {
            setBackgroundColor(Theme.getColor(backgroundKey));
            setTag(backgroundKey);
        }
        return backgroundKey;
    }

    public void updateColors() {
        SnowflakesEffect snowflakesEffect2 = this.snowflakesEffect;
        if (snowflakesEffect2 != null) {
            snowflakesEffect2.updateColors();
        }
    }

    private void setArrowState(boolean animated) {
        String str;
        int i;
        float rotation = this.accountsShown ? 180.0f : 0.0f;
        if (animated) {
            this.arrowView.animate().rotation(rotation).setDuration(220).setInterpolator(CubicBezierInterpolator.EASE_OUT).start();
        } else {
            this.arrowView.animate().cancel();
            this.arrowView.setRotation(rotation);
        }
        ImageView imageView = this.arrowView;
        if (this.accountsShown) {
            i = NUM;
            str = "AccDescrHideAccounts";
        } else {
            i = NUM;
            str = "AccDescrShowAccounts";
        }
        imageView.setContentDescription(LocaleController.getString(str, i));
    }
}
