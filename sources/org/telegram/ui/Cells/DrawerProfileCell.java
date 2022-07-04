package org.telegram.ui.Cells;

import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.Toast;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLRPC;
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
    private Paint backPaint = new Paint(1);
    private Integer currentColor;
    private Integer currentMoonColor;
    private int darkThemeBackgroundColor;
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
        AnonymousClass1 r2 = new RLottieImageView(context) {
            public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
                super.onInitializeAccessibilityNodeInfo(info);
                if (Theme.isCurrentThemeDark()) {
                    info.setText(LocaleController.getString("AccDescrSwitchToDayTheme", NUM));
                } else {
                    info.setText(LocaleController.getString("AccDescrSwitchToNightTheme", NUM));
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
            RLottieImageView rLottieImageView = this.darkThemeView;
            int color2 = Theme.getColor("listSelectorSDK21");
            this.darkThemeBackgroundColor = color2;
            rLottieImageView.setBackgroundDrawable(Theme.createSelectorDrawable(color2, 1, AndroidUtilities.dp(17.0f)));
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

    /* renamed from: lambda$new$0$org-telegram-ui-Cells-DrawerProfileCell  reason: not valid java name */
    public /* synthetic */ void m2796lambda$new$0$orgtelegramuiCellsDrawerProfileCell(View v) {
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

    static /* synthetic */ boolean lambda$new$1(DrawerLayoutContainer drawerLayoutContainer, View e) {
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
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
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
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (this.drawPremium) {
            if (this.starParticlesDrawable == null) {
                StarParticlesView.Drawable drawable = new StarParticlesView.Drawable(15);
                this.starParticlesDrawable = drawable;
                drawable.init();
                this.starParticlesDrawable.speedScale = 0.8f;
                this.starParticlesDrawable.minLifeTime = 3000;
            }
            this.starParticlesDrawable.rect.set((float) this.avatarImageView.getLeft(), (float) this.avatarImageView.getTop(), (float) this.avatarImageView.getRight(), (float) this.avatarImageView.getBottom());
            this.starParticlesDrawable.rect.inset((float) (-AndroidUtilities.dp(20.0f)), (float) (-AndroidUtilities.dp(20.0f)));
            this.starParticlesDrawable.resetPositions();
        }
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x01eb  */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x0262  */
    /* JADX WARNING: Removed duplicated region for block: B:83:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDraw(android.graphics.Canvas r26) {
        /*
            r25 = this;
            r1 = r25
            r8 = r26
            android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.getCachedWallpaper()
            r0 = 0
            java.lang.String r10 = r1.applyBackground(r0)
            java.lang.String r2 = "chats_menuTopBackground"
            boolean r2 = r10.equals(r2)
            if (r2 != 0) goto L_0x002d
            boolean r2 = org.telegram.ui.ActionBar.Theme.isCustomTheme()
            if (r2 == 0) goto L_0x002d
            boolean r2 = org.telegram.ui.ActionBar.Theme.isPatternWallpaper()
            if (r2 != 0) goto L_0x002d
            if (r9 == 0) goto L_0x002d
            boolean r2 = r9 instanceof android.graphics.drawable.ColorDrawable
            if (r2 != 0) goto L_0x002d
            boolean r2 = r9 instanceof android.graphics.drawable.GradientDrawable
            if (r2 != 0) goto L_0x002d
            r2 = 1
            goto L_0x002e
        L_0x002d:
            r2 = 0
        L_0x002e:
            r11 = r2
            r2 = 0
            r4 = 0
            if (r11 != 0) goto L_0x0042
            java.lang.String r5 = "chats_menuTopShadowCats"
            boolean r6 = org.telegram.ui.ActionBar.Theme.hasThemeKey(r5)
            if (r6 == 0) goto L_0x0042
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r2 = 1
            r12 = r2
            goto L_0x0058
        L_0x0042:
            java.lang.String r5 = "chats_menuTopShadow"
            boolean r6 = org.telegram.ui.ActionBar.Theme.hasThemeKey(r5)
            if (r6 == 0) goto L_0x0050
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r12 = r2
            goto L_0x0058
        L_0x0050:
            int r5 = org.telegram.ui.ActionBar.Theme.getServiceMessageColor()
            r6 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r5 = r5 | r6
            r12 = r2
        L_0x0058:
            java.lang.Integer r2 = r1.currentColor
            if (r2 == 0) goto L_0x0062
            int r2 = r2.intValue()
            if (r2 == r5) goto L_0x0078
        L_0x0062:
            java.lang.Integer r2 = java.lang.Integer.valueOf(r5)
            r1.currentColor = r2
            android.widget.ImageView r2 = r1.shadowView
            android.graphics.drawable.Drawable r2 = r2.getDrawable()
            android.graphics.PorterDuffColorFilter r6 = new android.graphics.PorterDuffColorFilter
            android.graphics.PorterDuff$Mode r7 = android.graphics.PorterDuff.Mode.MULTIPLY
            r6.<init>(r5, r7)
            r2.setColorFilter(r6)
        L_0x0078:
            java.lang.String r2 = "chats_menuName"
            int r13 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            java.lang.Integer r5 = r1.currentMoonColor
            if (r5 == 0) goto L_0x0088
            int r5 = r5.intValue()
            if (r5 == r13) goto L_0x00cc
        L_0x0088:
            java.lang.Integer r5 = java.lang.Integer.valueOf(r13)
            r1.currentMoonColor = r5
            org.telegram.ui.Components.RLottieDrawable r5 = r1.sunDrawable
            r5.beginApplyLayerColors()
            org.telegram.ui.Components.RLottieDrawable r5 = r1.sunDrawable
            java.lang.Integer r6 = r1.currentMoonColor
            int r6 = r6.intValue()
            java.lang.String r7 = "Sunny.**"
            r5.setLayerColor(r7, r6)
            org.telegram.ui.Components.RLottieDrawable r5 = r1.sunDrawable
            java.lang.Integer r6 = r1.currentMoonColor
            int r6 = r6.intValue()
            java.lang.String r7 = "Path 6.**"
            r5.setLayerColor(r7, r6)
            org.telegram.ui.Components.RLottieDrawable r5 = r1.sunDrawable
            java.lang.Integer r6 = r1.currentMoonColor
            int r6 = r6.intValue()
            java.lang.String r7 = "Path.**"
            r5.setLayerColor(r7, r6)
            org.telegram.ui.Components.RLottieDrawable r5 = r1.sunDrawable
            java.lang.Integer r6 = r1.currentMoonColor
            int r6 = r6.intValue()
            java.lang.String r7 = "Path 5.**"
            r5.setLayerColor(r7, r6)
            org.telegram.ui.Components.RLottieDrawable r5 = r1.sunDrawable
            r5.commitApplyLayerColors()
        L_0x00cc:
            android.widget.TextView r5 = r1.nameTextView
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r5.setTextColor(r2)
            java.lang.String r2 = "listSelectorSDK21"
            if (r11 == 0) goto L_0x0197
            android.widget.TextView r5 = r1.phoneTextView
            java.lang.String r6 = "chats_menuPhone"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r5.setTextColor(r6)
            android.widget.ImageView r5 = r1.shadowView
            int r5 = r5.getVisibility()
            if (r5 == 0) goto L_0x00f1
            android.widget.ImageView r5 = r1.shadowView
            r5.setVisibility(r0)
        L_0x00f1:
            boolean r5 = r9 instanceof android.graphics.drawable.ColorDrawable
            if (r5 != 0) goto L_0x017f
            boolean r5 = r9 instanceof android.graphics.drawable.GradientDrawable
            if (r5 == 0) goto L_0x00fd
            r17 = r4
            goto L_0x0181
        L_0x00fd:
            boolean r2 = r9 instanceof android.graphics.drawable.BitmapDrawable
            if (r2 == 0) goto L_0x017c
            r2 = r9
            android.graphics.drawable.BitmapDrawable r2 = (android.graphics.drawable.BitmapDrawable) r2
            android.graphics.Bitmap r2 = r2.getBitmap()
            int r5 = r25.getMeasuredWidth()
            float r5 = (float) r5
            int r6 = r2.getWidth()
            float r6 = (float) r6
            float r5 = r5 / r6
            int r6 = r25.getMeasuredHeight()
            float r6 = (float) r6
            int r7 = r2.getHeight()
            float r7 = (float) r7
            float r6 = r6 / r7
            float r7 = java.lang.Math.max(r5, r6)
            int r14 = r25.getMeasuredWidth()
            float r14 = (float) r14
            float r14 = r14 / r7
            int r14 = (int) r14
            int r15 = r25.getMeasuredHeight()
            float r15 = (float) r15
            float r15 = r15 / r7
            int r15 = (int) r15
            int r16 = r2.getWidth()
            int r16 = r16 - r14
            int r3 = r16 / 2
            int r16 = r2.getHeight()
            int r16 = r16 - r15
            r17 = r4
            int r4 = r16 / 2
            android.graphics.Rect r0 = r1.srcRect
            r18 = r5
            int r5 = r3 + r14
            r19 = r6
            int r6 = r4 + r15
            r0.set(r3, r4, r5, r6)
            android.graphics.Rect r0 = r1.destRect
            int r5 = r25.getMeasuredWidth()
            int r6 = r25.getMeasuredHeight()
            r20 = r3
            r3 = 0
            r0.set(r3, r3, r5, r6)
            android.graphics.Rect r0 = r1.srcRect     // Catch:{ all -> 0x0169 }
            android.graphics.Rect r3 = r1.destRect     // Catch:{ all -> 0x0169 }
            android.graphics.Paint r5 = r1.paint     // Catch:{ all -> 0x0169 }
            r8.drawBitmap(r2, r0, r3, r5)     // Catch:{ all -> 0x0169 }
            goto L_0x016d
        L_0x0169:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x016d:
            int r0 = org.telegram.ui.ActionBar.Theme.getServiceMessageColor()
            r3 = 16777215(0xffffff, float:2.3509886E-38)
            r0 = r0 & r3
            r3 = 1342177280(0x50000000, float:8.5899346E9)
            r4 = r0 | r3
            r17 = r4
            goto L_0x01c0
        L_0x017c:
            r17 = r4
            goto L_0x01c0
        L_0x017f:
            r17 = r4
        L_0x0181:
            int r0 = r25.getMeasuredWidth()
            int r3 = r25.getMeasuredHeight()
            r4 = 0
            r9.setBounds(r4, r4, r0, r3)
            r9.draw(r8)
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r17 = r4
            goto L_0x01c0
        L_0x0197:
            r17 = r4
            r4 = 0
            if (r12 == 0) goto L_0x019e
            r0 = 0
            goto L_0x019f
        L_0x019e:
            r0 = 4
        L_0x019f:
            android.widget.ImageView r3 = r1.shadowView
            int r3 = r3.getVisibility()
            if (r3 == r0) goto L_0x01ac
            android.widget.ImageView r3 = r1.shadowView
            r3.setVisibility(r0)
        L_0x01ac:
            android.widget.TextView r3 = r1.phoneTextView
            java.lang.String r4 = "chats_menuPhoneCats"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            r3.setTextColor(r4)
            super.onDraw(r26)
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r17 = r4
        L_0x01c0:
            boolean r0 = r1.drawPremium
            r2 = 1033171465(0x3d94var_, float:0.07272727)
            r3 = 1065353216(0x3var_, float:1.0)
            r4 = 0
            if (r0 == 0) goto L_0x01d4
            float r5 = r1.drawPremiumProgress
            int r6 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1))
            if (r6 == 0) goto L_0x01d4
            float r5 = r5 + r2
            r1.drawPremiumProgress = r5
            goto L_0x01df
        L_0x01d4:
            if (r0 != 0) goto L_0x01df
            float r0 = r1.drawPremiumProgress
            int r5 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r5 == 0) goto L_0x01df
            float r0 = r0 - r2
            r1.drawPremiumProgress = r0
        L_0x01df:
            float r0 = r1.drawPremiumProgress
            float r0 = org.telegram.messenger.Utilities.clamp(r0, r3, r4)
            r1.drawPremiumProgress = r0
            int r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r0 == 0) goto L_0x025e
            org.telegram.ui.Components.Premium.PremiumGradient$GradientTools r0 = r1.gradientTools
            if (r0 != 0) goto L_0x0218
            org.telegram.ui.Components.Premium.PremiumGradient$GradientTools r0 = new org.telegram.ui.Components.Premium.PremiumGradient$GradientTools
            r2 = 0
            java.lang.String r3 = "premiumGradientBottomSheet1"
            java.lang.String r5 = "premiumGradientBottomSheet2"
            java.lang.String r6 = "premiumGradientBottomSheet3"
            r0.<init>(r3, r5, r6, r2)
            r1.gradientTools = r0
            r0.x1 = r4
            org.telegram.ui.Components.Premium.PremiumGradient$GradientTools r0 = r1.gradientTools
            r2 = 1066192077(0x3f8ccccd, float:1.1)
            r0.y1 = r2
            org.telegram.ui.Components.Premium.PremiumGradient$GradientTools r0 = r1.gradientTools
            r2 = 1069547520(0x3fCLASSNAME, float:1.5)
            r0.x2 = r2
            org.telegram.ui.Components.Premium.PremiumGradient$GradientTools r0 = r1.gradientTools
            r2 = -1102263091(0xffffffffbe4ccccd, float:-0.2)
            r0.y2 = r2
            org.telegram.ui.Components.Premium.PremiumGradient$GradientTools r0 = r1.gradientTools
            r2 = 1
            r0.exactly = r2
        L_0x0218:
            org.telegram.ui.Components.Premium.PremiumGradient$GradientTools r0 = r1.gradientTools
            r19 = 0
            r20 = 0
            int r21 = r25.getMeasuredWidth()
            int r22 = r25.getMeasuredHeight()
            r23 = 0
            r24 = 0
            r18 = r0
            r18.gradientMatrix(r19, r20, r21, r22, r23, r24)
            org.telegram.ui.Components.Premium.PremiumGradient$GradientTools r0 = r1.gradientTools
            android.graphics.Paint r0 = r0.paint
            float r2 = r1.drawPremiumProgress
            r3 = 1132396544(0x437var_, float:255.0)
            float r2 = r2 * r3
            int r2 = (int) r2
            r0.setAlpha(r2)
            r3 = 0
            r4 = 0
            int r0 = r25.getMeasuredWidth()
            float r5 = (float) r0
            int r0 = r25.getMeasuredHeight()
            float r6 = (float) r0
            org.telegram.ui.Components.Premium.PremiumGradient$GradientTools r0 = r1.gradientTools
            android.graphics.Paint r7 = r0.paint
            r2 = r26
            r2.drawRect(r3, r4, r5, r6, r7)
            org.telegram.ui.Components.Premium.StarParticlesView$Drawable r0 = r1.starParticlesDrawable
            if (r0 == 0) goto L_0x025b
            float r2 = r1.drawPremiumProgress
            r0.onDraw(r8, r2)
        L_0x025b:
            r25.invalidate()
        L_0x025e:
            org.telegram.ui.Components.SnowflakesEffect r0 = r1.snowflakesEffect
            if (r0 == 0) goto L_0x0265
            r0.onDraw(r1, r8)
        L_0x0265:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.DrawerProfileCell.onDraw(android.graphics.Canvas):void");
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
            CharSequence text = UserObject.getUserName(user);
            try {
                text = Emoji.replaceEmoji(text, this.nameTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(22.0f), false);
            } catch (Exception e) {
            }
            this.drawPremium = false;
            if (text != null) {
            }
            this.nameTextView.setText(text);
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

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.emojiLoaded) {
            this.nameTextView.invalidate();
        }
    }
}
