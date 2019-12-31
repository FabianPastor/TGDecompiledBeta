package org.telegram.ui.Cells;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build.VERSION;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.Toast;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.Theme.ThemeInfo;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.SnowflakesEffect;

public class DrawerProfileCell extends FrameLayout {
    private boolean accountsShowed;
    private ImageView arrowView;
    private BackupImageView avatarImageView;
    private Integer currentColor;
    private Integer currentMoonColor;
    private ImageView darkThemeView;
    private Rect destRect = new Rect();
    private TextView nameTextView;
    private Paint paint = new Paint();
    private TextView phoneTextView;
    private ImageView shadowView;
    private SnowflakesEffect snowflakesEffect;
    private Rect srcRect = new Rect();

    public DrawerProfileCell(Context context) {
        super(context);
        this.shadowView = new ImageView(context);
        this.shadowView.setVisibility(4);
        this.shadowView.setScaleType(ScaleType.FIT_XY);
        this.shadowView.setImageResource(NUM);
        addView(this.shadowView, LayoutHelper.createFrame(-1, 70, 83));
        this.avatarImageView = new BackupImageView(context);
        this.avatarImageView.getImageReceiver().setRoundRadius(AndroidUtilities.dp(32.0f));
        addView(this.avatarImageView, LayoutHelper.createFrame(64, 64.0f, 83, 16.0f, 0.0f, 0.0f, 67.0f));
        this.nameTextView = new TextView(context);
        this.nameTextView.setTextSize(1, 15.0f);
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.nameTextView.setLines(1);
        this.nameTextView.setMaxLines(1);
        this.nameTextView.setSingleLine(true);
        this.nameTextView.setGravity(3);
        this.nameTextView.setEllipsize(TruncateAt.END);
        addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0f, 83, 16.0f, 0.0f, 76.0f, 28.0f));
        this.phoneTextView = new TextView(context);
        this.phoneTextView.setTextSize(1, 13.0f);
        this.phoneTextView.setLines(1);
        this.phoneTextView.setMaxLines(1);
        this.phoneTextView.setSingleLine(true);
        this.phoneTextView.setGravity(3);
        addView(this.phoneTextView, LayoutHelper.createFrame(-1, -2.0f, 83, 16.0f, 0.0f, 76.0f, 9.0f));
        this.arrowView = new ImageView(context);
        this.arrowView.setScaleType(ScaleType.CENTER);
        this.arrowView.setImageResource(NUM);
        addView(this.arrowView, LayoutHelper.createFrame(59, 59, 85));
        setArrowState(false);
        this.darkThemeView = new ImageView(context);
        this.darkThemeView.setScaleType(ScaleType.CENTER);
        this.darkThemeView.setImageResource(NUM);
        String str = "chats_menuName";
        this.darkThemeView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str), Mode.MULTIPLY));
        if (VERSION.SDK_INT >= 21) {
            this.darkThemeView.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("listSelectorSDK21")));
            Theme.setRippleDrawableForceSoftware((RippleDrawable) this.darkThemeView.getBackground());
        }
        this.darkThemeView.setOnClickListener(new -$$Lambda$DrawerProfileCell$pk7OtSp6Yma6Wl0p63ZBsLElrNY(this));
        addView(this.darkThemeView, LayoutHelper.createFrame(48, 48.0f, 85, 0.0f, 0.0f, 6.0f, 90.0f));
        if (Theme.getEventType() == 0) {
            this.snowflakesEffect = new SnowflakesEffect();
            this.snowflakesEffect.setColorKey(str);
        }
    }

    public /* synthetic */ void lambda$new$0$DrawerProfileCell(View view) {
        ThemeInfo theme;
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", 0);
        String str = "Blue";
        String string = sharedPreferences.getString("lastDayTheme", str);
        if (Theme.getTheme(string) == null) {
            string = str;
        }
        String str2 = "Dark Blue";
        String string2 = sharedPreferences.getString("lastDarkTheme", str2);
        if (Theme.getTheme(string2) == null) {
            string2 = str2;
        }
        ThemeInfo activeTheme = Theme.getActiveTheme();
        if (string.equals(string2)) {
            if (activeTheme.isDark()) {
                string = str;
            } else {
                string2 = str2;
            }
        }
        if (string.equals(activeTheme.getKey())) {
            theme = Theme.getTheme(string2);
        } else {
            theme = Theme.getTheme(string);
        }
        if (Theme.selectedAutoNightType != 0) {
            Toast.makeText(getContext(), LocaleController.getString("AutoNightModeOff", NUM), 0).show();
            Theme.selectedAutoNightType = 0;
            Theme.saveAutoNightThemeConfig();
            Theme.cancelAutoNightThemeCallbacks();
        }
        r2 = new int[2];
        this.darkThemeView.getLocationInWindow(r2);
        r2[0] = r2[0] + (this.darkThemeView.getMeasuredWidth() / 2);
        r2[1] = r2[1] + (this.darkThemeView.getMeasuredHeight() / 2);
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needSetDayNightTheme, theme, Boolean.valueOf(false), r2, Integer.valueOf(-1));
    }

    /* Access modifiers changed, original: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        updateColors();
    }

    /* Access modifiers changed, original: protected */
    public void onMeasure(int i, int i2) {
        if (VERSION.SDK_INT >= 21) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(148.0f) + AndroidUtilities.statusBarHeight, NUM));
            return;
        }
        try {
            super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(148.0f), NUM));
        } catch (Exception e) {
            setMeasuredDimension(MeasureSpec.getSize(i), AndroidUtilities.dp(148.0f));
            FileLog.e(e);
        }
    }

    /* Access modifiers changed, original: protected */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x0136  */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x00a2  */
    /* JADX WARNING: Removed duplicated region for block: B:62:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x0159  */
    public void onDraw(android.graphics.Canvas r10) {
        /*
        r9 = this;
        r0 = org.telegram.ui.ActionBar.Theme.getCachedWallpaper();
        r1 = 0;
        r2 = r9.applyBackground(r1);
        r3 = "chats_menuTopBackground";
        r2 = r2.equals(r3);
        r3 = 1;
        if (r2 != 0) goto L_0x002a;
    L_0x0012:
        r2 = org.telegram.ui.ActionBar.Theme.isCustomTheme();
        if (r2 == 0) goto L_0x002a;
    L_0x0018:
        r2 = org.telegram.ui.ActionBar.Theme.isPatternWallpaper();
        if (r2 != 0) goto L_0x002a;
    L_0x001e:
        if (r0 == 0) goto L_0x002a;
    L_0x0020:
        r2 = r0 instanceof android.graphics.drawable.ColorDrawable;
        if (r2 != 0) goto L_0x002a;
    L_0x0024:
        r2 = r0 instanceof android.graphics.drawable.GradientDrawable;
        if (r2 != 0) goto L_0x002a;
    L_0x0028:
        r2 = 1;
        goto L_0x002b;
    L_0x002a:
        r2 = 0;
    L_0x002b:
        if (r2 != 0) goto L_0x003a;
    L_0x002d:
        r4 = "chats_menuTopShadowCats";
        r5 = org.telegram.ui.ActionBar.Theme.hasThemeKey(r4);
        if (r5 == 0) goto L_0x003a;
    L_0x0035:
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r4);
        goto L_0x004f;
    L_0x003a:
        r3 = "chats_menuTopShadow";
        r4 = org.telegram.ui.ActionBar.Theme.hasThemeKey(r3);
        if (r4 == 0) goto L_0x0047;
    L_0x0042:
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r3);
        goto L_0x004e;
    L_0x0047:
        r3 = org.telegram.ui.ActionBar.Theme.getServiceMessageColor();
        r4 = -16777216; // 0xfffffffffvar_ float:-1.7014118E38 double:NaN;
        r4 = r4 | r3;
    L_0x004e:
        r3 = 0;
    L_0x004f:
        r5 = r9.currentColor;
        if (r5 == 0) goto L_0x0059;
    L_0x0053:
        r5 = r5.intValue();
        if (r5 == r4) goto L_0x006f;
    L_0x0059:
        r5 = java.lang.Integer.valueOf(r4);
        r9.currentColor = r5;
        r5 = r9.shadowView;
        r5 = r5.getDrawable();
        r6 = new android.graphics.PorterDuffColorFilter;
        r7 = android.graphics.PorterDuff.Mode.MULTIPLY;
        r6.<init>(r4, r7);
        r5.setColorFilter(r6);
    L_0x006f:
        r4 = "chats_menuName";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r4);
        r6 = r9.currentMoonColor;
        if (r6 == 0) goto L_0x0081;
    L_0x0079:
        r6 = r9.currentColor;
        r6 = r6.intValue();
        if (r6 == r5) goto L_0x0097;
    L_0x0081:
        r6 = java.lang.Integer.valueOf(r5);
        r9.currentMoonColor = r6;
        r6 = r9.darkThemeView;
        r6 = r6.getDrawable();
        r7 = new android.graphics.PorterDuffColorFilter;
        r8 = android.graphics.PorterDuff.Mode.MULTIPLY;
        r7.<init>(r5, r8);
        r6.setColorFilter(r7);
    L_0x0097:
        r5 = r9.nameTextView;
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r4);
        r5.setTextColor(r4);
        if (r2 == 0) goto L_0x0136;
    L_0x00a2:
        r2 = r9.phoneTextView;
        r3 = "chats_menuPhone";
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r3);
        r2.setTextColor(r3);
        r2 = r9.shadowView;
        r2 = r2.getVisibility();
        if (r2 == 0) goto L_0x00ba;
    L_0x00b5:
        r2 = r9.shadowView;
        r2.setVisibility(r1);
    L_0x00ba:
        r2 = r0 instanceof android.graphics.drawable.ColorDrawable;
        if (r2 != 0) goto L_0x0127;
    L_0x00be:
        r2 = r0 instanceof android.graphics.drawable.GradientDrawable;
        if (r2 == 0) goto L_0x00c3;
    L_0x00c2:
        goto L_0x0127;
    L_0x00c3:
        r2 = r0 instanceof android.graphics.drawable.BitmapDrawable;
        if (r2 == 0) goto L_0x0155;
    L_0x00c7:
        r0 = (android.graphics.drawable.BitmapDrawable) r0;
        r0 = r0.getBitmap();
        r2 = r9.getMeasuredWidth();
        r2 = (float) r2;
        r3 = r0.getWidth();
        r3 = (float) r3;
        r2 = r2 / r3;
        r3 = r9.getMeasuredHeight();
        r3 = (float) r3;
        r4 = r0.getHeight();
        r4 = (float) r4;
        r3 = r3 / r4;
        r4 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1));
        if (r4 >= 0) goto L_0x00e8;
    L_0x00e7:
        r2 = r3;
    L_0x00e8:
        r3 = r9.getMeasuredWidth();
        r3 = (float) r3;
        r3 = r3 / r2;
        r3 = (int) r3;
        r4 = r9.getMeasuredHeight();
        r4 = (float) r4;
        r4 = r4 / r2;
        r2 = (int) r4;
        r4 = r0.getWidth();
        r4 = r4 - r3;
        r4 = r4 / 2;
        r5 = r0.getHeight();
        r5 = r5 - r2;
        r5 = r5 / 2;
        r6 = r9.srcRect;
        r3 = r3 + r4;
        r2 = r2 + r5;
        r6.set(r4, r5, r3, r2);
        r2 = r9.destRect;
        r3 = r9.getMeasuredWidth();
        r4 = r9.getMeasuredHeight();
        r2.set(r1, r1, r3, r4);
        r1 = r9.srcRect;	 Catch:{ all -> 0x0122 }
        r2 = r9.destRect;	 Catch:{ all -> 0x0122 }
        r3 = r9.paint;	 Catch:{ all -> 0x0122 }
        r10.drawBitmap(r0, r1, r2, r3);	 Catch:{ all -> 0x0122 }
        goto L_0x0155;
    L_0x0122:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
        goto L_0x0155;
    L_0x0127:
        r2 = r9.getMeasuredWidth();
        r3 = r9.getMeasuredHeight();
        r0.setBounds(r1, r1, r2, r3);
        r0.draw(r10);
        goto L_0x0155;
    L_0x0136:
        if (r3 == 0) goto L_0x0139;
    L_0x0138:
        goto L_0x013a;
    L_0x0139:
        r1 = 4;
    L_0x013a:
        r0 = r9.shadowView;
        r0 = r0.getVisibility();
        if (r0 == r1) goto L_0x0147;
    L_0x0142:
        r0 = r9.shadowView;
        r0.setVisibility(r1);
    L_0x0147:
        r0 = r9.phoneTextView;
        r1 = "chats_menuPhoneCats";
        r1 = org.telegram.ui.ActionBar.Theme.getColor(r1);
        r0.setTextColor(r1);
        super.onDraw(r10);
    L_0x0155:
        r0 = r9.snowflakesEffect;
        if (r0 == 0) goto L_0x015c;
    L_0x0159:
        r0.onDraw(r9, r10);
    L_0x015c:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.DrawerProfileCell.onDraw(android.graphics.Canvas):void");
    }

    public boolean isAccountsShowed() {
        return this.accountsShowed;
    }

    public void setAccountsShowed(boolean z, boolean z2) {
        if (this.accountsShowed != z) {
            this.accountsShowed = z;
            setArrowState(z2);
        }
    }

    public void setUser(User user, boolean z) {
        if (user != null) {
            this.accountsShowed = z;
            setArrowState(false);
            this.nameTextView.setText(UserObject.getUserName(user));
            TextView textView = this.phoneTextView;
            PhoneFormat instance = PhoneFormat.getInstance();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("+");
            stringBuilder.append(user.phone);
            textView.setText(instance.format(stringBuilder.toString()));
            Drawable avatarDrawable = new AvatarDrawable(user);
            avatarDrawable.setColor(Theme.getColor("avatar_backgroundInProfileBlue"));
            this.avatarImageView.setImage(ImageLocation.getForUser(user, false), "50_50", avatarDrawable, (Object) user);
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
        SnowflakesEffect snowflakesEffect = this.snowflakesEffect;
        if (snowflakesEffect != null) {
            snowflakesEffect.updateColors();
        }
    }

    private void setArrowState(boolean z) {
        int i;
        String str;
        float f = this.accountsShowed ? 180.0f : 0.0f;
        if (z) {
            this.arrowView.animate().rotation(f).setDuration(220).setInterpolator(CubicBezierInterpolator.EASE_OUT).start();
        } else {
            this.arrowView.animate().cancel();
            this.arrowView.setRotation(f);
        }
        ImageView imageView = this.arrowView;
        if (this.accountsShowed) {
            i = NUM;
            str = "AccDescrHideAccounts";
        } else {
            i = NUM;
            str = "AccDescrShowAccounts";
        }
        imageView.setContentDescription(LocaleController.getString(str, i));
    }
}
