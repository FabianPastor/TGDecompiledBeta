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
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLRPC$User;
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
        if (isCurrentThemeDay()) {
            this.sunDrawable.setCustomEndFrame(36);
        } else {
            this.sunDrawable.setCustomEndFrame(0);
            this.sunDrawable.setCurrentFrame(36);
        }
        this.sunDrawable.setPlayInDirectionOfCustomEndFrame(true);
        this.darkThemeView = new RLottieImageView(context) {
            public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
                super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
                if (DrawerProfileCell.this.sunDrawable.getCustomEndFrame() != 0) {
                    accessibilityNodeInfo.setText(LocaleController.getString("AccDescrSwitchToNightTheme", NUM));
                } else {
                    accessibilityNodeInfo.setText(LocaleController.getString("AccDescrSwitchToDayTheme", NUM));
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
        this.darkThemeView.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                DrawerProfileCell.this.lambda$new$0$DrawerProfileCell(view);
            }
        });
        addView(this.darkThemeView, LayoutHelper.createFrame(48, 48.0f, 85, 0.0f, 0.0f, 6.0f, 90.0f));
        if (Theme.getEventType() == 0) {
            SnowflakesEffect snowflakesEffect2 = new SnowflakesEffect();
            this.snowflakesEffect = snowflakesEffect2;
            snowflakesEffect2.setColorKey("chats_menuName");
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x005b  */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x0067  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x0079  */
    /* renamed from: lambda$new$0 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$new$0$DrawerProfileCell(android.view.View r7) {
        /*
            r6 = this;
            boolean r7 = switchingTheme
            if (r7 == 0) goto L_0x0005
            return
        L_0x0005:
            r7 = 1
            switchingTheme = r7
            android.content.Context r7 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.String r0 = "themeconfig"
            r1 = 0
            android.content.SharedPreferences r7 = r7.getSharedPreferences(r0, r1)
            java.lang.String r0 = "lastDayTheme"
            java.lang.String r2 = "Blue"
            java.lang.String r0 = r7.getString(r0, r2)
            org.telegram.ui.ActionBar.Theme$ThemeInfo r3 = org.telegram.ui.ActionBar.Theme.getTheme(r0)
            if (r3 != 0) goto L_0x0020
            r0 = r2
        L_0x0020:
            java.lang.String r3 = "lastDarkTheme"
            java.lang.String r4 = "Dark Blue"
            java.lang.String r7 = r7.getString(r3, r4)
            org.telegram.ui.ActionBar.Theme$ThemeInfo r3 = org.telegram.ui.ActionBar.Theme.getTheme(r7)
            if (r3 != 0) goto L_0x002f
            r7 = r4
        L_0x002f:
            org.telegram.ui.ActionBar.Theme$ThemeInfo r3 = org.telegram.ui.ActionBar.Theme.getActiveTheme()
            boolean r5 = r0.equals(r7)
            if (r5 == 0) goto L_0x004f
            boolean r5 = r3.isDark()
            if (r5 != 0) goto L_0x004d
            boolean r5 = r0.equals(r4)
            if (r5 != 0) goto L_0x004d
            java.lang.String r5 = "Night"
            boolean r5 = r0.equals(r5)
            if (r5 == 0) goto L_0x0050
        L_0x004d:
            r4 = r7
            goto L_0x0051
        L_0x004f:
            r4 = r7
        L_0x0050:
            r2 = r0
        L_0x0051:
            java.lang.String r7 = r3.getKey()
            boolean r7 = r2.equals(r7)
            if (r7 == 0) goto L_0x0067
            org.telegram.ui.ActionBar.Theme$ThemeInfo r0 = org.telegram.ui.ActionBar.Theme.getTheme(r4)
            org.telegram.ui.Components.RLottieDrawable r2 = r6.sunDrawable
            r3 = 36
            r2.setCustomEndFrame(r3)
            goto L_0x0070
        L_0x0067:
            org.telegram.ui.ActionBar.Theme$ThemeInfo r0 = org.telegram.ui.ActionBar.Theme.getTheme(r2)
            org.telegram.ui.Components.RLottieDrawable r2 = r6.sunDrawable
            r2.setCustomEndFrame(r1)
        L_0x0070:
            org.telegram.ui.Components.RLottieImageView r2 = r6.darkThemeView
            r2.playAnimation()
            int r2 = org.telegram.ui.ActionBar.Theme.selectedAutoNightType
            if (r2 == 0) goto L_0x0095
            android.content.Context r2 = r6.getContext()
            r3 = 2131624498(0x7f0e0232, float:1.8876177E38)
            java.lang.String r4 = "AutoNightModeOff"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            android.widget.Toast r2 = android.widget.Toast.makeText(r2, r3, r1)
            r2.show()
            org.telegram.ui.ActionBar.Theme.selectedAutoNightType = r1
            org.telegram.ui.ActionBar.Theme.saveAutoNightThemeConfig()
            org.telegram.ui.ActionBar.Theme.cancelAutoNightThemeCallbacks()
        L_0x0095:
            r6.switchTheme(r0, r7)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.DrawerProfileCell.lambda$new$0$DrawerProfileCell(android.view.View):void");
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
            java.lang.Boolean r8 = java.lang.Boolean.FALSE
            r6[r3] = r8
            r6[r0] = r1
            r8 = -1
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            r0 = 3
            r6[r0] = r8
            java.lang.Boolean r8 = java.lang.Boolean.valueOf(r9)
            r9 = 4
            r6[r9] = r8
            org.telegram.ui.Components.RLottieImageView r8 = r7.darkThemeView
            r9 = 5
            r6[r9] = r8
            r4.postNotificationName(r5, r6)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.DrawerProfileCell.switchTheme(org.telegram.ui.ActionBar.Theme$ThemeInfo, boolean):void");
    }

    private boolean isCurrentThemeDay() {
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", 0);
        String str = "Blue";
        String string = sharedPreferences.getString("lastDayTheme", str);
        if (Theme.getTheme(string) == null) {
            string = str;
        }
        String str2 = "Dark Blue";
        String string2 = sharedPreferences.getString("lastDarkTheme", str2);
        if (Theme.getTheme(string2) != null) {
            str2 = string2;
        }
        Theme.ThemeInfo activeTheme = Theme.getActiveTheme();
        if (!string.equals(str2) || !activeTheme.isDark()) {
            str = string;
        }
        return str.equals(activeTheme.getKey());
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        updateColors();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        if (Build.VERSION.SDK_INT >= 21) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(148.0f) + AndroidUtilities.statusBarHeight, NUM));
            return;
        }
        try {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(148.0f), NUM));
        } catch (Exception e) {
            setMeasuredDimension(View.MeasureSpec.getSize(i), AndroidUtilities.dp(148.0f));
            FileLog.e((Throwable) e);
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        boolean z;
        int i;
        Drawable cachedWallpaper = Theme.getCachedWallpaper();
        int i2 = 0;
        boolean z2 = !applyBackground(false).equals("chats_menuTopBackground") && Theme.isCustomTheme() && !Theme.isPatternWallpaper() && cachedWallpaper != null && !(cachedWallpaper instanceof ColorDrawable) && !(cachedWallpaper instanceof GradientDrawable);
        if (z2 || !Theme.hasThemeKey("chats_menuTopShadowCats")) {
            if (Theme.hasThemeKey("chats_menuTopShadow")) {
                i = Theme.getColor("chats_menuTopShadow");
            } else {
                i = Theme.getServiceMessageColor() | -16777216;
            }
            z = false;
        } else {
            i = Theme.getColor("chats_menuTopShadowCats");
            z = true;
        }
        Integer num = this.currentColor;
        if (num == null || num.intValue() != i) {
            this.currentColor = Integer.valueOf(i);
            this.shadowView.getDrawable().setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
        }
        int color = Theme.getColor("chats_menuName");
        Integer num2 = this.currentMoonColor;
        if (num2 == null || num2.intValue() != color) {
            this.currentMoonColor = Integer.valueOf(color);
            this.sunDrawable.beginApplyLayerColors();
            this.sunDrawable.setLayerColor("Sunny.**", this.currentMoonColor.intValue());
            this.sunDrawable.setLayerColor("Path 6.**", this.currentMoonColor.intValue());
            this.sunDrawable.setLayerColor("Path.**", this.currentMoonColor.intValue());
            this.sunDrawable.setLayerColor("Path 5.**", this.currentMoonColor.intValue());
            this.sunDrawable.commitApplyLayerColors();
        }
        this.nameTextView.setTextColor(Theme.getColor("chats_menuName"));
        if (z2) {
            this.phoneTextView.setTextColor(Theme.getColor("chats_menuPhone"));
            if (this.shadowView.getVisibility() != 0) {
                this.shadowView.setVisibility(0);
            }
            if ((cachedWallpaper instanceof ColorDrawable) || (cachedWallpaper instanceof GradientDrawable)) {
                cachedWallpaper.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
                cachedWallpaper.draw(canvas);
                i2 = Theme.getColor("listSelectorSDK21");
            } else if (cachedWallpaper instanceof BitmapDrawable) {
                Bitmap bitmap = ((BitmapDrawable) cachedWallpaper).getBitmap();
                float max = Math.max(((float) getMeasuredWidth()) / ((float) bitmap.getWidth()), ((float) getMeasuredHeight()) / ((float) bitmap.getHeight()));
                int measuredWidth = (int) (((float) getMeasuredWidth()) / max);
                int measuredHeight = (int) (((float) getMeasuredHeight()) / max);
                int width = (bitmap.getWidth() - measuredWidth) / 2;
                int height = (bitmap.getHeight() - measuredHeight) / 2;
                this.srcRect.set(width, height, measuredWidth + width, measuredHeight + height);
                this.destRect.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
                try {
                    canvas.drawBitmap(bitmap, this.srcRect, this.destRect, this.paint);
                } catch (Throwable th) {
                    FileLog.e(th);
                }
                i2 = (Theme.getServiceMessageColor() & 16777215) | NUM;
            }
        } else {
            if (!z) {
                i2 = 4;
            }
            if (this.shadowView.getVisibility() != i2) {
                this.shadowView.setVisibility(i2);
            }
            this.phoneTextView.setTextColor(Theme.getColor("chats_menuPhoneCats"));
            super.onDraw(canvas);
            i2 = Theme.getColor("listSelectorSDK21");
        }
        if (i2 != 0) {
            if (i2 != this.darkThemeBackgroundColor) {
                Paint paint2 = this.backPaint;
                this.darkThemeBackgroundColor = i2;
                paint2.setColor(i2);
                if (Build.VERSION.SDK_INT >= 21) {
                    Drawable background = this.darkThemeView.getBackground();
                    this.darkThemeBackgroundColor = i2;
                    Theme.setSelectorDrawableColor(background, i2, true);
                }
            }
            if (z2 && (cachedWallpaper instanceof BitmapDrawable)) {
                canvas.drawCircle(this.darkThemeView.getX() + ((float) (this.darkThemeView.getMeasuredWidth() / 2)), this.darkThemeView.getY() + ((float) (this.darkThemeView.getMeasuredHeight() / 2)), (float) AndroidUtilities.dp(17.0f), this.backPaint);
            }
        }
        SnowflakesEffect snowflakesEffect2 = this.snowflakesEffect;
        if (snowflakesEffect2 != null) {
            snowflakesEffect2.onDraw(this, canvas);
        }
    }

    public boolean isInAvatar(float f, float f2) {
        return f >= ((float) this.avatarImageView.getLeft()) && f <= ((float) this.avatarImageView.getRight()) && f2 >= ((float) this.avatarImageView.getTop()) && f2 <= ((float) this.avatarImageView.getBottom());
    }

    public boolean hasAvatar() {
        return this.avatarImageView.getImageReceiver().hasNotThumb();
    }

    public void setAccountsShown(boolean z, boolean z2) {
        if (this.accountsShown != z) {
            this.accountsShown = z;
            setArrowState(z2);
        }
    }

    public void setUser(TLRPC$User tLRPC$User, boolean z) {
        if (tLRPC$User != null) {
            this.accountsShown = z;
            setArrowState(false);
            this.nameTextView.setText(UserObject.getUserName(tLRPC$User));
            TextView textView = this.phoneTextView;
            PhoneFormat instance = PhoneFormat.getInstance();
            textView.setText(instance.format("+" + tLRPC$User.phone));
            AvatarDrawable avatarDrawable = new AvatarDrawable(tLRPC$User);
            avatarDrawable.setColor(Theme.getColor("avatar_backgroundInProfileBlue"));
            this.avatarImageView.setForUserOrChat(tLRPC$User, avatarDrawable);
            applyBackground(true);
        }
    }

    public String applyBackground(boolean z) {
        String str = (String) getTag();
        String str2 = "chats_menuTopBackground";
        if (!Theme.hasThemeKey(str2) || Theme.getColor(str2) == 0) {
            str2 = "chats_menuTopBackgroundCats";
        }
        if (z || !str2.equals(str)) {
            setBackgroundColor(Theme.getColor(str2));
            setTag(str2);
        }
        return str2;
    }

    public void updateColors() {
        SnowflakesEffect snowflakesEffect2 = this.snowflakesEffect;
        if (snowflakesEffect2 != null) {
            snowflakesEffect2.updateColors();
        }
    }

    private void setArrowState(boolean z) {
        String str;
        int i;
        float f = this.accountsShown ? 180.0f : 0.0f;
        if (z) {
            this.arrowView.animate().rotation(f).setDuration(220).setInterpolator(CubicBezierInterpolator.EASE_OUT).start();
        } else {
            this.arrowView.animate().cancel();
            this.arrowView.setRotation(f);
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
