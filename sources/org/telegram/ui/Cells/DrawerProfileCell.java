package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.SnowflakesEffect;

public class DrawerProfileCell extends FrameLayout {
    private boolean accountsShowed;
    private ImageView arrowView;
    private BackupImageView avatarImageView;
    private Integer currentColor;
    private Rect destRect = new Rect();
    private TextView nameTextView;
    private Paint paint = new Paint();
    private TextView phoneTextView;
    private ImageView shadowView;
    private SnowflakesEffect snowflakesEffect;
    private Rect srcRect = new Rect();

    public DrawerProfileCell(Context context) {
        int i;
        String str;
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
        ImageView imageView = this.arrowView;
        if (this.accountsShowed) {
            i = NUM;
            str = "AccDescrHideAccounts";
        } else {
            i = NUM;
            str = "AccDescrShowAccounts";
        }
        imageView.setContentDescription(LocaleController.getString(str, i));
        addView(this.arrowView, LayoutHelper.createFrame(59, 59, 85));
        if (Theme.getEventType() == 0) {
            this.snowflakesEffect = new SnowflakesEffect();
        }
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
    /* JADX WARNING: Removed duplicated region for block: B:41:0x0104  */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x0074  */
    /* JADX WARNING: Removed duplicated region for block: B:51:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x0127  */
    public void onDraw(android.graphics.Canvas r9) {
        /*
        r8 = this;
        r0 = org.telegram.ui.ActionBar.Theme.getCachedWallpaper();
        r1 = r8.applyBackground();
        r2 = "chats_menuTopBackground";
        r1 = r1.equals(r2);
        r2 = 1;
        r3 = 0;
        if (r1 != 0) goto L_0x0022;
    L_0x0012:
        r1 = org.telegram.ui.ActionBar.Theme.isCustomTheme();
        if (r1 == 0) goto L_0x0022;
    L_0x0018:
        r1 = org.telegram.ui.ActionBar.Theme.isPatternWallpaper();
        if (r1 != 0) goto L_0x0022;
    L_0x001e:
        if (r0 == 0) goto L_0x0022;
    L_0x0020:
        r1 = 1;
        goto L_0x0023;
    L_0x0022:
        r1 = 0;
    L_0x0023:
        if (r1 != 0) goto L_0x0032;
    L_0x0025:
        r4 = "chats_menuTopShadowCats";
        r5 = org.telegram.ui.ActionBar.Theme.hasThemeKey(r4);
        if (r5 == 0) goto L_0x0032;
    L_0x002d:
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r4);
        goto L_0x0047;
    L_0x0032:
        r2 = "chats_menuTopShadow";
        r4 = org.telegram.ui.ActionBar.Theme.hasThemeKey(r2);
        if (r4 == 0) goto L_0x003f;
    L_0x003a:
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r2);
        goto L_0x0046;
    L_0x003f:
        r2 = org.telegram.ui.ActionBar.Theme.getServiceMessageColor();
        r4 = -16777216; // 0xfffffffffvar_ float:-1.7014118E38 double:NaN;
        r4 = r4 | r2;
    L_0x0046:
        r2 = 0;
    L_0x0047:
        r5 = r8.currentColor;
        if (r5 == 0) goto L_0x0051;
    L_0x004b:
        r5 = r5.intValue();
        if (r5 == r4) goto L_0x0067;
    L_0x0051:
        r5 = java.lang.Integer.valueOf(r4);
        r8.currentColor = r5;
        r5 = r8.shadowView;
        r5 = r5.getDrawable();
        r6 = new android.graphics.PorterDuffColorFilter;
        r7 = android.graphics.PorterDuff.Mode.MULTIPLY;
        r6.<init>(r4, r7);
        r5.setColorFilter(r6);
    L_0x0067:
        r4 = r8.nameTextView;
        r5 = "chats_menuName";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setTextColor(r5);
        if (r1 == 0) goto L_0x0104;
    L_0x0074:
        r1 = r8.phoneTextView;
        r2 = "chats_menuPhone";
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r2);
        r1.setTextColor(r2);
        r1 = r8.shadowView;
        r1 = r1.getVisibility();
        if (r1 == 0) goto L_0x008c;
    L_0x0087:
        r1 = r8.shadowView;
        r1.setVisibility(r3);
    L_0x008c:
        r1 = r0 instanceof android.graphics.drawable.ColorDrawable;
        if (r1 == 0) goto L_0x00a0;
    L_0x0090:
        r1 = r8.getMeasuredWidth();
        r2 = r8.getMeasuredHeight();
        r0.setBounds(r3, r3, r1, r2);
        r0.draw(r9);
        goto L_0x0123;
    L_0x00a0:
        r1 = r0 instanceof android.graphics.drawable.BitmapDrawable;
        if (r1 == 0) goto L_0x0123;
    L_0x00a4:
        r0 = (android.graphics.drawable.BitmapDrawable) r0;
        r0 = r0.getBitmap();
        r1 = r8.getMeasuredWidth();
        r1 = (float) r1;
        r2 = r0.getWidth();
        r2 = (float) r2;
        r1 = r1 / r2;
        r2 = r8.getMeasuredHeight();
        r2 = (float) r2;
        r4 = r0.getHeight();
        r4 = (float) r4;
        r2 = r2 / r4;
        r4 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1));
        if (r4 >= 0) goto L_0x00c5;
    L_0x00c4:
        r1 = r2;
    L_0x00c5:
        r2 = r8.getMeasuredWidth();
        r2 = (float) r2;
        r2 = r2 / r1;
        r2 = (int) r2;
        r4 = r8.getMeasuredHeight();
        r4 = (float) r4;
        r4 = r4 / r1;
        r1 = (int) r4;
        r4 = r0.getWidth();
        r4 = r4 - r2;
        r4 = r4 / 2;
        r5 = r0.getHeight();
        r5 = r5 - r1;
        r5 = r5 / 2;
        r6 = r8.srcRect;
        r2 = r2 + r4;
        r1 = r1 + r5;
        r6.set(r4, r5, r2, r1);
        r1 = r8.destRect;
        r2 = r8.getMeasuredWidth();
        r4 = r8.getMeasuredHeight();
        r1.set(r3, r3, r2, r4);
        r1 = r8.srcRect;	 Catch:{ Throwable -> 0x00ff }
        r2 = r8.destRect;	 Catch:{ Throwable -> 0x00ff }
        r3 = r8.paint;	 Catch:{ Throwable -> 0x00ff }
        r9.drawBitmap(r0, r1, r2, r3);	 Catch:{ Throwable -> 0x00ff }
        goto L_0x0123;
    L_0x00ff:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
        goto L_0x0123;
    L_0x0104:
        if (r2 == 0) goto L_0x0107;
    L_0x0106:
        goto L_0x0108;
    L_0x0107:
        r3 = 4;
    L_0x0108:
        r0 = r8.shadowView;
        r0 = r0.getVisibility();
        if (r0 == r3) goto L_0x0115;
    L_0x0110:
        r0 = r8.shadowView;
        r0.setVisibility(r3);
    L_0x0115:
        r0 = r8.phoneTextView;
        r1 = "chats_menuPhoneCats";
        r1 = org.telegram.ui.ActionBar.Theme.getColor(r1);
        r0.setTextColor(r1);
        super.onDraw(r9);
    L_0x0123:
        r0 = r8.snowflakesEffect;
        if (r0 == 0) goto L_0x012a;
    L_0x0127:
        r0.onDraw(r8, r9);
    L_0x012a:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.DrawerProfileCell.onDraw(android.graphics.Canvas):void");
    }

    public boolean isAccountsShowed() {
        return this.accountsShowed;
    }

    public void setAccountsShowed(boolean z) {
        if (this.accountsShowed != z) {
            this.accountsShowed = z;
            this.arrowView.setImageResource(this.accountsShowed ? NUM : NUM);
        }
    }

    public void setOnArrowClickListener(OnClickListener onClickListener) {
        this.arrowView.setOnClickListener(new -$$Lambda$DrawerProfileCell$E00gMmT74biKthBWyKI7QNe-uk4(this, onClickListener));
    }

    public /* synthetic */ void lambda$setOnArrowClickListener$0$DrawerProfileCell(OnClickListener onClickListener, View view) {
        int i;
        String str;
        this.accountsShowed ^= 1;
        this.arrowView.setImageResource(this.accountsShowed ? NUM : NUM);
        onClickListener.onClick(this);
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

    public void setUser(User user, boolean z) {
        if (user != null) {
            this.accountsShowed = z;
            this.arrowView.setImageResource(this.accountsShowed ? NUM : NUM);
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
            applyBackground();
        }
    }

    public String applyBackground() {
        String str = (String) getTag();
        String str2 = "chats_menuTopBackground";
        if (!Theme.hasThemeKey(str2) || Theme.getColor(str2) == 0) {
            str2 = "chats_menuTopBackgroundCats";
        }
        if (!str2.equals(str)) {
            setBackgroundColor(Theme.getColor(str2));
            setTag(str2);
        }
        return str2;
    }
}
