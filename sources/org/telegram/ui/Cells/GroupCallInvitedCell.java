package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;

public class GroupCallInvitedCell extends FrameLayout {
    private AvatarDrawable avatarDrawable;
    private BackupImageView avatarImageView;
    private TLRPC.User currentUser;
    private Paint dividerPaint;
    private String grayIconColor = "voipgroup_mutedIcon";
    private ImageView muteButton;
    private SimpleTextView nameTextView;
    private boolean needDivider;
    private SimpleTextView statusTextView;

    public GroupCallInvitedCell(Context context) {
        super(context);
        Paint paint = new Paint();
        this.dividerPaint = paint;
        paint.setColor(Theme.getColor("voipgroup_actionBar"));
        this.avatarDrawable = new AvatarDrawable();
        BackupImageView backupImageView = new BackupImageView(context);
        this.avatarImageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(24.0f));
        int i = 5;
        addView(this.avatarImageView, LayoutHelper.createFrame(46, 46.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 11.0f, 6.0f, LocaleController.isRTL ? 11.0f : 0.0f, 0.0f));
        SimpleTextView simpleTextView = new SimpleTextView(context);
        this.nameTextView = simpleTextView;
        simpleTextView.setTextColor(Theme.getColor("voipgroup_nameText"));
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.nameTextView.setTextSize(16);
        this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(this.nameTextView, LayoutHelper.createFrame(-1, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 54.0f : 67.0f, 10.0f, LocaleController.isRTL ? 67.0f : 54.0f, 0.0f));
        SimpleTextView simpleTextView2 = new SimpleTextView(context);
        this.statusTextView = simpleTextView2;
        simpleTextView2.setTextSize(15);
        this.statusTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        this.statusTextView.setTextColor(Theme.getColor(this.grayIconColor));
        this.statusTextView.setText(LocaleController.getString("Invited", NUM));
        addView(this.statusTextView, LayoutHelper.createFrame(-1, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 54.0f : 67.0f, 32.0f, LocaleController.isRTL ? 67.0f : 54.0f, 0.0f));
        ImageView imageView = new ImageView(context);
        this.muteButton = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        this.muteButton.setImageResource(NUM);
        this.muteButton.setImportantForAccessibility(2);
        this.muteButton.setPadding(0, 0, AndroidUtilities.dp(4.0f), 0);
        this.muteButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(this.grayIconColor), PorterDuff.Mode.MULTIPLY));
        addView(this.muteButton, LayoutHelper.createFrame(48, -1.0f, (LocaleController.isRTL ? 3 : i) | 16, 6.0f, 0.0f, 6.0f, 0.0f));
        setWillNotDraw(false);
        setFocusable(true);
    }

    public CharSequence getName() {
        return this.nameTextView.getText();
    }

    public void setData(int account, Long uid) {
        TLRPC.User user = MessagesController.getInstance(account).getUser(uid);
        this.currentUser = user;
        this.avatarDrawable.setInfo(user);
        this.nameTextView.setText(UserObject.getUserName(this.currentUser));
        this.avatarImageView.getImageReceiver().setCurrentAccount(account);
        this.avatarImageView.setForUserOrChat(this.currentUser, this.avatarDrawable);
    }

    public void setDrawDivider(boolean draw) {
        this.needDivider = draw;
        invalidate();
    }

    public void setGrayIconColor(String key, int value) {
        if (!this.grayIconColor.equals(key)) {
            this.grayIconColor = key;
        }
        this.muteButton.setColorFilter(new PorterDuffColorFilter(value, PorterDuff.Mode.MULTIPLY));
        this.statusTextView.setTextColor(value);
        Theme.setSelectorDrawableColor(this.muteButton.getDrawable(), NUM & value, true);
    }

    public TLRPC.User getUser() {
        return this.currentUser;
    }

    public boolean hasAvatarSet() {
        return this.avatarImageView.getImageReceiver().hasNotThumb();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(58.0f), NUM));
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(68.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(68.0f) : 0)), (float) (getMeasuredHeight() - 1), this.dividerPaint);
        }
        super.dispatchDraw(canvas);
    }
}
