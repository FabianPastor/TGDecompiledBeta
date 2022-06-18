package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
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
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.DrawerLayoutContainer;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Premium.PremiumGradient;
import org.telegram.ui.Components.Premium.StarParticlesView;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.SnowflakesEffect;
import org.telegram.ui.ThemeActivity;

public class DrawerProfileCell extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    public static boolean switchingTheme;
    private boolean accountsShown;
    private ImageView arrowView;
    private BackupImageView avatarImageView;
    private Integer currentColor;
    private Integer currentMoonColor;
    private RLottieImageView darkThemeView;
    private Rect destRect = new Rect();
    public boolean drawPremium;
    public float drawPremiumProgress;
    PremiumGradient.GradientTools gradientTools;
    private TextView nameTextView;
    private Paint paint = new Paint();
    private TextView phoneTextView;
    private ImageView shadowView;
    private SnowflakesEffect snowflakesEffect;
    private Rect srcRect = new Rect();
    StarParticlesView.Drawable starParticlesDrawable;
    private RLottieDrawable sunDrawable;

    public DrawerProfileCell(Context context, DrawerLayoutContainer drawerLayoutContainer) {
        super(context);
        new Paint(1);
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
        AnonymousClass1 r2 = new RLottieImageView(this, context) {
            public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
                super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
                if (Theme.isCurrentThemeDark()) {
                    accessibilityNodeInfo.setText(LocaleController.getString("AccDescrSwitchToDayTheme", NUM));
                } else {
                    accessibilityNodeInfo.setText(LocaleController.getString("AccDescrSwitchToNightTheme", NUM));
                }
            }
        };
        this.darkThemeView = r2;
        r2.setFocusable(true);
        this.darkThemeView.setBackground(Theme.createCircleSelectorDrawable(Theme.getColor("dialogButtonSelector"), 0, 0));
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
            this.darkThemeView.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("listSelectorSDK21"), 1, AndroidUtilities.dp(17.0f)));
            Theme.setRippleDrawableForceSoftware((RippleDrawable) this.darkThemeView.getBackground());
        }
        this.darkThemeView.setOnClickListener(new DrawerProfileCell$$ExternalSyntheticLambda0(this));
        this.darkThemeView.setOnLongClickListener(new DrawerProfileCell$$ExternalSyntheticLambda1(drawerLayoutContainer));
        addView(this.darkThemeView, LayoutHelper.createFrame(48, 48.0f, 85, 0.0f, 0.0f, 6.0f, 90.0f));
        if (Theme.getEventType() == 0) {
            SnowflakesEffect snowflakesEffect2 = new SnowflakesEffect(0);
            this.snowflakesEffect = snowflakesEffect2;
            snowflakesEffect2.setColorKey("chats_menuName");
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x006f  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x007b  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x008d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$new$0(android.view.View r7) {
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
            if (r3 == 0) goto L_0x0029
            org.telegram.ui.ActionBar.Theme$ThemeInfo r3 = org.telegram.ui.ActionBar.Theme.getTheme(r0)
            boolean r3 = r3.isDark()
            if (r3 == 0) goto L_0x002a
        L_0x0029:
            r0 = r2
        L_0x002a:
            java.lang.String r3 = "lastDarkTheme"
            java.lang.String r4 = "Dark Blue"
            java.lang.String r7 = r7.getString(r3, r4)
            org.telegram.ui.ActionBar.Theme$ThemeInfo r3 = org.telegram.ui.ActionBar.Theme.getTheme(r7)
            if (r3 == 0) goto L_0x0042
            org.telegram.ui.ActionBar.Theme$ThemeInfo r3 = org.telegram.ui.ActionBar.Theme.getTheme(r7)
            boolean r3 = r3.isDark()
            if (r3 != 0) goto L_0x0043
        L_0x0042:
            r7 = r4
        L_0x0043:
            org.telegram.ui.ActionBar.Theme$ThemeInfo r3 = org.telegram.ui.ActionBar.Theme.getActiveTheme()
            boolean r5 = r0.equals(r7)
            if (r5 == 0) goto L_0x0063
            boolean r5 = r3.isDark()
            if (r5 != 0) goto L_0x0061
            boolean r5 = r0.equals(r4)
            if (r5 != 0) goto L_0x0061
            java.lang.String r5 = "Night"
            boolean r5 = r0.equals(r5)
            if (r5 == 0) goto L_0x0064
        L_0x0061:
            r4 = r7
            goto L_0x0065
        L_0x0063:
            r4 = r7
        L_0x0064:
            r2 = r0
        L_0x0065:
            java.lang.String r7 = r3.getKey()
            boolean r7 = r2.equals(r7)
            if (r7 == 0) goto L_0x007b
            org.telegram.ui.ActionBar.Theme$ThemeInfo r0 = org.telegram.ui.ActionBar.Theme.getTheme(r4)
            org.telegram.ui.Components.RLottieDrawable r2 = r6.sunDrawable
            r3 = 36
            r2.setCustomEndFrame(r3)
            goto L_0x0084
        L_0x007b:
            org.telegram.ui.ActionBar.Theme$ThemeInfo r0 = org.telegram.ui.ActionBar.Theme.getTheme(r2)
            org.telegram.ui.Components.RLottieDrawable r2 = r6.sunDrawable
            r2.setCustomEndFrame(r1)
        L_0x0084:
            org.telegram.ui.Components.RLottieImageView r2 = r6.darkThemeView
            r2.playAnimation()
            int r2 = org.telegram.ui.ActionBar.Theme.selectedAutoNightType
            if (r2 == 0) goto L_0x00a9
            android.content.Context r2 = r6.getContext()
            r3 = 2131624608(0x7f0e02a0, float:1.88764E38)
            java.lang.String r4 = "AutoNightModeOff"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            android.widget.Toast r2 = android.widget.Toast.makeText(r2, r3, r1)
            r2.show()
            org.telegram.ui.ActionBar.Theme.selectedAutoNightType = r1
            org.telegram.ui.ActionBar.Theme.saveAutoNightThemeConfig()
            org.telegram.ui.ActionBar.Theme.cancelAutoNightThemeCallbacks()
        L_0x00a9:
            r6.switchTheme(r0, r7)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.DrawerProfileCell.lambda$new$0(android.view.View):void");
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$new$1(DrawerLayoutContainer drawerLayoutContainer, View view) {
        if (drawerLayoutContainer == null) {
            return false;
        }
        drawerLayoutContainer.presentFragment(new ThemeActivity(0));
        return true;
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

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        updateColors();
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
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
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        if (this.drawPremium) {
            if (this.starParticlesDrawable == null) {
                StarParticlesView.Drawable drawable = new StarParticlesView.Drawable(15);
                this.starParticlesDrawable = drawable;
                drawable.init();
                StarParticlesView.Drawable drawable2 = this.starParticlesDrawable;
                drawable2.speedScale = 0.8f;
                drawable2.minLifeTime = 3000;
            }
            this.starParticlesDrawable.rect.set((float) this.avatarImageView.getLeft(), (float) this.avatarImageView.getTop(), (float) this.avatarImageView.getRight(), (float) this.avatarImageView.getBottom());
            this.starParticlesDrawable.rect.inset((float) (-AndroidUtilities.dp(20.0f)), (float) (-AndroidUtilities.dp(20.0f)));
            this.starParticlesDrawable.resetPositions();
        }
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x01b7  */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x021e  */
    /* JADX WARNING: Removed duplicated region for block: B:78:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDraw(android.graphics.Canvas r12) {
        /*
            r11 = this;
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.getCachedWallpaper()
            r1 = 0
            java.lang.String r2 = r11.applyBackground(r1)
            java.lang.String r3 = "chats_menuTopBackground"
            boolean r2 = r2.equals(r3)
            r3 = 1
            if (r2 != 0) goto L_0x002a
            boolean r2 = org.telegram.ui.ActionBar.Theme.isCustomTheme()
            if (r2 == 0) goto L_0x002a
            boolean r2 = org.telegram.ui.ActionBar.Theme.isPatternWallpaper()
            if (r2 != 0) goto L_0x002a
            if (r0 == 0) goto L_0x002a
            boolean r2 = r0 instanceof android.graphics.drawable.ColorDrawable
            if (r2 != 0) goto L_0x002a
            boolean r2 = r0 instanceof android.graphics.drawable.GradientDrawable
            if (r2 != 0) goto L_0x002a
            r2 = 1
            goto L_0x002b
        L_0x002a:
            r2 = 0
        L_0x002b:
            if (r2 != 0) goto L_0x003b
            java.lang.String r4 = "chats_menuTopShadowCats"
            boolean r5 = org.telegram.ui.ActionBar.Theme.hasThemeKey(r4)
            if (r5 == 0) goto L_0x003b
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            r5 = 1
            goto L_0x0050
        L_0x003b:
            java.lang.String r4 = "chats_menuTopShadow"
            boolean r5 = org.telegram.ui.ActionBar.Theme.hasThemeKey(r4)
            if (r5 == 0) goto L_0x0048
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            goto L_0x004f
        L_0x0048:
            int r4 = org.telegram.ui.ActionBar.Theme.getServiceMessageColor()
            r5 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r4 = r4 | r5
        L_0x004f:
            r5 = 0
        L_0x0050:
            java.lang.Integer r6 = r11.currentColor
            if (r6 == 0) goto L_0x005a
            int r6 = r6.intValue()
            if (r6 == r4) goto L_0x0070
        L_0x005a:
            java.lang.Integer r6 = java.lang.Integer.valueOf(r4)
            r11.currentColor = r6
            android.widget.ImageView r6 = r11.shadowView
            android.graphics.drawable.Drawable r6 = r6.getDrawable()
            android.graphics.PorterDuffColorFilter r7 = new android.graphics.PorterDuffColorFilter
            android.graphics.PorterDuff$Mode r8 = android.graphics.PorterDuff.Mode.MULTIPLY
            r7.<init>(r4, r8)
            r6.setColorFilter(r7)
        L_0x0070:
            java.lang.String r4 = "chats_menuName"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            java.lang.Integer r7 = r11.currentMoonColor
            if (r7 == 0) goto L_0x0080
            int r7 = r7.intValue()
            if (r7 == r6) goto L_0x00c4
        L_0x0080:
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            r11.currentMoonColor = r6
            org.telegram.ui.Components.RLottieDrawable r6 = r11.sunDrawable
            r6.beginApplyLayerColors()
            org.telegram.ui.Components.RLottieDrawable r6 = r11.sunDrawable
            java.lang.Integer r7 = r11.currentMoonColor
            int r7 = r7.intValue()
            java.lang.String r8 = "Sunny.**"
            r6.setLayerColor(r8, r7)
            org.telegram.ui.Components.RLottieDrawable r6 = r11.sunDrawable
            java.lang.Integer r7 = r11.currentMoonColor
            int r7 = r7.intValue()
            java.lang.String r8 = "Path 6.**"
            r6.setLayerColor(r8, r7)
            org.telegram.ui.Components.RLottieDrawable r6 = r11.sunDrawable
            java.lang.Integer r7 = r11.currentMoonColor
            int r7 = r7.intValue()
            java.lang.String r8 = "Path.**"
            r6.setLayerColor(r8, r7)
            org.telegram.ui.Components.RLottieDrawable r6 = r11.sunDrawable
            java.lang.Integer r7 = r11.currentMoonColor
            int r7 = r7.intValue()
            java.lang.String r8 = "Path 5.**"
            r6.setLayerColor(r8, r7)
            org.telegram.ui.Components.RLottieDrawable r6 = r11.sunDrawable
            r6.commitApplyLayerColors()
        L_0x00c4:
            android.widget.TextView r6 = r11.nameTextView
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            r6.setTextColor(r4)
            java.lang.String r4 = "listSelectorSDK21"
            if (r2 == 0) goto L_0x016a
            android.widget.TextView r2 = r11.phoneTextView
            java.lang.String r5 = "chats_menuPhone"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r2.setTextColor(r5)
            android.widget.ImageView r2 = r11.shadowView
            int r2 = r2.getVisibility()
            if (r2 == 0) goto L_0x00e9
            android.widget.ImageView r2 = r11.shadowView
            r2.setVisibility(r1)
        L_0x00e9:
            boolean r2 = r0 instanceof android.graphics.drawable.ColorDrawable
            if (r2 != 0) goto L_0x0158
            boolean r2 = r0 instanceof android.graphics.drawable.GradientDrawable
            if (r2 == 0) goto L_0x00f2
            goto L_0x0158
        L_0x00f2:
            boolean r2 = r0 instanceof android.graphics.drawable.BitmapDrawable
            if (r2 == 0) goto L_0x018c
            android.graphics.drawable.BitmapDrawable r0 = (android.graphics.drawable.BitmapDrawable) r0
            android.graphics.Bitmap r0 = r0.getBitmap()
            int r2 = r11.getMeasuredWidth()
            float r2 = (float) r2
            int r4 = r0.getWidth()
            float r4 = (float) r4
            float r2 = r2 / r4
            int r4 = r11.getMeasuredHeight()
            float r4 = (float) r4
            int r5 = r0.getHeight()
            float r5 = (float) r5
            float r4 = r4 / r5
            float r2 = java.lang.Math.max(r2, r4)
            int r4 = r11.getMeasuredWidth()
            float r4 = (float) r4
            float r4 = r4 / r2
            int r4 = (int) r4
            int r5 = r11.getMeasuredHeight()
            float r5 = (float) r5
            float r5 = r5 / r2
            int r2 = (int) r5
            int r5 = r0.getWidth()
            int r5 = r5 - r4
            int r5 = r5 / 2
            int r6 = r0.getHeight()
            int r6 = r6 - r2
            int r6 = r6 / 2
            android.graphics.Rect r7 = r11.srcRect
            int r4 = r4 + r5
            int r2 = r2 + r6
            r7.set(r5, r6, r4, r2)
            android.graphics.Rect r2 = r11.destRect
            int r4 = r11.getMeasuredWidth()
            int r5 = r11.getMeasuredHeight()
            r2.set(r1, r1, r4, r5)
            android.graphics.Rect r1 = r11.srcRect     // Catch:{ all -> 0x0150 }
            android.graphics.Rect r2 = r11.destRect     // Catch:{ all -> 0x0150 }
            android.graphics.Paint r4 = r11.paint     // Catch:{ all -> 0x0150 }
            r12.drawBitmap(r0, r1, r2, r4)     // Catch:{ all -> 0x0150 }
            goto L_0x0154
        L_0x0150:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0154:
            org.telegram.ui.ActionBar.Theme.getServiceMessageColor()
            goto L_0x018c
        L_0x0158:
            int r2 = r11.getMeasuredWidth()
            int r5 = r11.getMeasuredHeight()
            r0.setBounds(r1, r1, r2, r5)
            r0.draw(r12)
            org.telegram.ui.ActionBar.Theme.getColor(r4)
            goto L_0x018c
        L_0x016a:
            if (r5 == 0) goto L_0x016d
            goto L_0x016e
        L_0x016d:
            r1 = 4
        L_0x016e:
            android.widget.ImageView r0 = r11.shadowView
            int r0 = r0.getVisibility()
            if (r0 == r1) goto L_0x017b
            android.widget.ImageView r0 = r11.shadowView
            r0.setVisibility(r1)
        L_0x017b:
            android.widget.TextView r0 = r11.phoneTextView
            java.lang.String r1 = "chats_menuPhoneCats"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setTextColor(r1)
            super.onDraw(r12)
            org.telegram.ui.ActionBar.Theme.getColor(r4)
        L_0x018c:
            boolean r0 = r11.drawPremium
            r1 = 1033171465(0x3d94var_, float:0.07272727)
            r2 = 1065353216(0x3var_, float:1.0)
            r4 = 0
            if (r0 == 0) goto L_0x01a0
            float r5 = r11.drawPremiumProgress
            int r6 = (r5 > r2 ? 1 : (r5 == r2 ? 0 : -1))
            if (r6 == 0) goto L_0x01a0
            float r5 = r5 + r1
            r11.drawPremiumProgress = r5
            goto L_0x01ab
        L_0x01a0:
            if (r0 != 0) goto L_0x01ab
            float r0 = r11.drawPremiumProgress
            int r5 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r5 == 0) goto L_0x01ab
            float r0 = r0 - r1
            r11.drawPremiumProgress = r0
        L_0x01ab:
            float r0 = r11.drawPremiumProgress
            float r0 = org.telegram.messenger.Utilities.clamp(r0, r2, r4)
            r11.drawPremiumProgress = r0
            int r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r0 == 0) goto L_0x021a
            org.telegram.ui.Components.Premium.PremiumGradient$GradientTools r0 = r11.gradientTools
            if (r0 != 0) goto L_0x01db
            org.telegram.ui.Components.Premium.PremiumGradient$GradientTools r0 = new org.telegram.ui.Components.Premium.PremiumGradient$GradientTools
            r1 = 0
            java.lang.String r2 = "premiumGradientBottomSheet1"
            java.lang.String r5 = "premiumGradientBottomSheet2"
            java.lang.String r6 = "premiumGradientBottomSheet3"
            r0.<init>(r2, r5, r6, r1)
            r11.gradientTools = r0
            r0.x1 = r4
            r1 = 1066192077(0x3f8ccccd, float:1.1)
            r0.y1 = r1
            r1 = 1069547520(0x3fCLASSNAME, float:1.5)
            r0.x2 = r1
            r1 = -1102263091(0xffffffffbe4ccccd, float:-0.2)
            r0.y2 = r1
            r0.exactly = r3
        L_0x01db:
            org.telegram.ui.Components.Premium.PremiumGradient$GradientTools r4 = r11.gradientTools
            r5 = 0
            r6 = 0
            int r7 = r11.getMeasuredWidth()
            int r8 = r11.getMeasuredHeight()
            r9 = 0
            r10 = 0
            r4.gradientMatrix(r5, r6, r7, r8, r9, r10)
            org.telegram.ui.Components.Premium.PremiumGradient$GradientTools r0 = r11.gradientTools
            android.graphics.Paint r0 = r0.paint
            float r1 = r11.drawPremiumProgress
            r2 = 1132396544(0x437var_, float:255.0)
            float r1 = r1 * r2
            int r1 = (int) r1
            r0.setAlpha(r1)
            r3 = 0
            r4 = 0
            int r0 = r11.getMeasuredWidth()
            float r5 = (float) r0
            int r0 = r11.getMeasuredHeight()
            float r6 = (float) r0
            org.telegram.ui.Components.Premium.PremiumGradient$GradientTools r0 = r11.gradientTools
            android.graphics.Paint r7 = r0.paint
            r2 = r12
            r2.drawRect(r3, r4, r5, r6, r7)
            org.telegram.ui.Components.Premium.StarParticlesView$Drawable r0 = r11.starParticlesDrawable
            if (r0 == 0) goto L_0x0217
            float r1 = r11.drawPremiumProgress
            r0.onDraw(r12, r1)
        L_0x0217:
            r11.invalidate()
        L_0x021a:
            org.telegram.ui.Components.SnowflakesEffect r0 = r11.snowflakesEffect
            if (r0 == 0) goto L_0x0221
            r0.onDraw(r11, r12)
        L_0x0221:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.DrawerProfileCell.onDraw(android.graphics.Canvas):void");
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
            CharSequence userName = UserObject.getUserName(tLRPC$User);
            try {
                userName = Emoji.replaceEmoji(userName, this.nameTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(22.0f), false);
            } catch (Exception unused) {
            }
            this.drawPremium = false;
            this.nameTextView.setText(userName);
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

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.emojiLoaded) {
            this.nameTextView.invalidate();
        }
    }
}
