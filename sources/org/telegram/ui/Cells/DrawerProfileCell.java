package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
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
    public void onDraw(Canvas canvas) {
        int color;
        Drawable cachedWallpaper = Theme.getCachedWallpaper();
        String str = "chats_menuTopShadow";
        if (Theme.hasThemeKey(str)) {
            color = Theme.getColor(str);
        } else {
            color = Theme.getServiceMessageColor() | -16777216;
        }
        Integer num = this.currentColor;
        if (num == null || num.intValue() != color) {
            this.currentColor = Integer.valueOf(color);
            this.shadowView.getDrawable().setColorFilter(new PorterDuffColorFilter(color, Mode.MULTIPLY));
        }
        this.nameTextView.setTextColor(Theme.getColor("chats_menuName"));
        if (!Theme.isCustomTheme() || Theme.isPatternWallpaper() || cachedWallpaper == null) {
            this.shadowView.setVisibility(4);
            this.phoneTextView.setTextColor(Theme.getColor("chats_menuPhoneCats"));
            super.onDraw(canvas);
        } else {
            this.phoneTextView.setTextColor(Theme.getColor("chats_menuPhone"));
            this.shadowView.setVisibility(0);
            if (cachedWallpaper instanceof ColorDrawable) {
                cachedWallpaper.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
                cachedWallpaper.draw(canvas);
            } else if (cachedWallpaper instanceof BitmapDrawable) {
                Bitmap bitmap = ((BitmapDrawable) cachedWallpaper).getBitmap();
                float measuredWidth = ((float) getMeasuredWidth()) / ((float) bitmap.getWidth());
                float measuredHeight = ((float) getMeasuredHeight()) / ((float) bitmap.getHeight());
                if (measuredWidth < measuredHeight) {
                    measuredWidth = measuredHeight;
                }
                int measuredWidth2 = (int) (((float) getMeasuredWidth()) / measuredWidth);
                color = (int) (((float) getMeasuredHeight()) / measuredWidth);
                int width = (bitmap.getWidth() - measuredWidth2) / 2;
                int height = (bitmap.getHeight() - color) / 2;
                this.srcRect.set(width, height, measuredWidth2 + width, color + height);
                this.destRect.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
                try {
                    canvas.drawBitmap(bitmap, this.srcRect, this.destRect, this.paint);
                } catch (Throwable th) {
                    FileLog.e(th);
                }
            }
        }
        SnowflakesEffect snowflakesEffect = this.snowflakesEffect;
        if (snowflakesEffect != null) {
            snowflakesEffect.onDraw(this, canvas);
        }
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
        }
    }
}
