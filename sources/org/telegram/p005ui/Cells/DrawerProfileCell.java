package org.telegram.p005ui.Cells;

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
import org.telegram.PhoneFormat.C0195PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.beta.R;
import org.telegram.p005ui.ActionBar.Theme;
import org.telegram.p005ui.Components.AvatarDrawable;
import org.telegram.p005ui.Components.BackupImageView;
import org.telegram.p005ui.Components.LayoutHelper;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.User;

/* renamed from: org.telegram.ui.Cells.DrawerProfileCell */
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
    private Rect srcRect = new Rect();

    public DrawerProfileCell(Context context) {
        super(context);
        this.shadowView = new ImageView(context);
        this.shadowView.setVisibility(4);
        this.shadowView.setScaleType(ScaleType.FIT_XY);
        this.shadowView.setImageResource(R.drawable.bottom_shadow);
        addView(this.shadowView, LayoutHelper.createFrame(-1, 70, 83));
        this.avatarImageView = new BackupImageView(context);
        this.avatarImageView.getImageReceiver().setRoundRadius(AndroidUtilities.m10dp(32.0f));
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
        addView(this.arrowView, LayoutHelper.createFrame(59, 59, 85));
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (VERSION.SDK_INT >= 21) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.m10dp(148.0f) + AndroidUtilities.statusBarHeight, NUM));
            return;
        }
        try {
            super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.m10dp(148.0f), NUM));
        } catch (Throwable e) {
            setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), AndroidUtilities.m10dp(148.0f));
            FileLog.m14e(e);
        }
    }

    protected void onDraw(Canvas canvas) {
        int color;
        Drawable backgroundDrawable = Theme.getCachedWallpaper();
        if (Theme.hasThemeKey(Theme.key_chats_menuTopShadow)) {
            color = Theme.getColor(Theme.key_chats_menuTopShadow);
        } else {
            color = Theme.getServiceMessageColor() | Theme.ACTION_BAR_VIDEO_EDIT_COLOR;
        }
        if (this.currentColor == null || this.currentColor.intValue() != color) {
            this.currentColor = Integer.valueOf(color);
            this.shadowView.getDrawable().setColorFilter(new PorterDuffColorFilter(color, Mode.MULTIPLY));
        }
        this.nameTextView.setTextColor(Theme.getColor(Theme.key_chats_menuName));
        if (!Theme.isCustomTheme() || backgroundDrawable == null) {
            this.shadowView.setVisibility(4);
            this.phoneTextView.setTextColor(Theme.getColor(Theme.key_chats_menuPhoneCats));
            super.onDraw(canvas);
            return;
        }
        this.phoneTextView.setTextColor(Theme.getColor(Theme.key_chats_menuPhone));
        this.shadowView.setVisibility(0);
        if (backgroundDrawable instanceof ColorDrawable) {
            backgroundDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
            backgroundDrawable.draw(canvas);
        } else if (backgroundDrawable instanceof BitmapDrawable) {
            float scale;
            Bitmap bitmap = ((BitmapDrawable) backgroundDrawable).getBitmap();
            float scaleX = ((float) getMeasuredWidth()) / ((float) bitmap.getWidth());
            float scaleY = ((float) getMeasuredHeight()) / ((float) bitmap.getHeight());
            if (scaleX < scaleY) {
                scale = scaleY;
            } else {
                scale = scaleX;
            }
            int width = (int) (((float) getMeasuredWidth()) / scale);
            int height = (int) (((float) getMeasuredHeight()) / scale);
            int x = (bitmap.getWidth() - width) / 2;
            int y = (bitmap.getHeight() - height) / 2;
            this.srcRect.set(x, y, x + width, y + height);
            this.destRect.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
            try {
                canvas.drawBitmap(bitmap, this.srcRect, this.destRect, this.paint);
            } catch (Throwable e) {
                FileLog.m14e(e);
            }
        }
    }

    public boolean isAccountsShowed() {
        return this.accountsShowed;
    }

    public void setAccountsShowed(boolean value) {
        if (this.accountsShowed != value) {
            this.accountsShowed = value;
            this.arrowView.setImageResource(this.accountsShowed ? R.drawable.collapse_up : R.drawable.collapse_down);
        }
    }

    public void setOnArrowClickListener(final OnClickListener onClickListener) {
        this.arrowView.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                DrawerProfileCell.this.accountsShowed = !DrawerProfileCell.this.accountsShowed;
                DrawerProfileCell.this.arrowView.setImageResource(DrawerProfileCell.this.accountsShowed ? R.drawable.collapse_up : R.drawable.collapse_down);
                onClickListener.onClick(DrawerProfileCell.this);
            }
        });
    }

    public void setUser(User user, boolean accounts) {
        if (user != null) {
            TLObject photo = null;
            if (user.photo != null) {
                photo = user.photo.photo_small;
            }
            this.accountsShowed = accounts;
            this.arrowView.setImageResource(this.accountsShowed ? R.drawable.collapse_up : R.drawable.collapse_down);
            this.nameTextView.setText(UserObject.getUserName(user));
            this.phoneTextView.setText(C0195PhoneFormat.getInstance().format("+" + user.phone));
            Drawable avatarDrawable = new AvatarDrawable(user);
            avatarDrawable.setColor(Theme.getColor(Theme.key_avatar_backgroundInProfileBlue));
            this.avatarImageView.setImage(photo, "50_50", avatarDrawable);
        }
    }
}
