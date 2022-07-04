package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.LocaleController;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBoxSquare;
import org.telegram.ui.Components.LayoutHelper;

public class CheckBoxUserCell extends FrameLayout {
    private AvatarDrawable avatarDrawable;
    private CheckBoxSquare checkBox;
    private TLRPC.User currentUser;
    private BackupImageView imageView;
    private boolean needDivider;
    private TextView textView;

    public CheckBoxUserCell(Context context, boolean alert) {
        super(context);
        TextView textView2 = new TextView(context);
        this.textView = textView2;
        textView2.setTextColor(Theme.getColor(alert ? "dialogTextBlack" : "windowBackgroundWhiteBlackText"));
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setEllipsize(TextUtils.TruncateAt.END);
        int i = 5;
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        int i2 = 21;
        addView(this.textView, LayoutHelper.createFrame(-1, -1.0f, (LocaleController.isRTL ? 5 : 3) | 48, (float) (LocaleController.isRTL ? 21 : 94), 0.0f, (float) (!LocaleController.isRTL ? 21 : 94), 0.0f));
        this.avatarDrawable = new AvatarDrawable();
        BackupImageView backupImageView = new BackupImageView(context);
        this.imageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(36.0f));
        addView(this.imageView, LayoutHelper.createFrame(36, 36.0f, (LocaleController.isRTL ? 5 : 3) | 48, 48.0f, 7.0f, 48.0f, 0.0f));
        CheckBoxSquare checkBoxSquare = new CheckBoxSquare(context, alert, (Theme.ResourcesProvider) null);
        this.checkBox = checkBoxSquare;
        addView(checkBoxSquare, LayoutHelper.createFrame(18, 18.0f, (!LocaleController.isRTL ? 3 : i) | 48, (float) (LocaleController.isRTL ? 0 : 21), 16.0f, (float) (!LocaleController.isRTL ? 0 : i2), 0.0f));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f) + (this.needDivider ? 1 : 0), NUM));
    }

    public void setTextColor(int color) {
        this.textView.setTextColor(color);
    }

    public TLRPC.User getCurrentUser() {
        return this.currentUser;
    }

    public void setUser(TLRPC.User user, boolean checked, boolean divider) {
        this.currentUser = user;
        this.textView.setText(ContactsController.formatName(user.first_name, user.last_name));
        this.checkBox.setChecked(checked, false);
        this.avatarDrawable.setInfo(user);
        this.imageView.setForUserOrChat(user, this.avatarDrawable);
        this.needDivider = divider;
        setWillNotDraw(!divider);
    }

    public void setChecked(boolean checked, boolean animated) {
        this.checkBox.setChecked(checked, animated);
    }

    public boolean isChecked() {
        return this.checkBox.isChecked();
    }

    public TextView getTextView() {
        return this.textView;
    }

    public CheckBoxSquare getCheckBox() {
        return this.checkBox;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }
    }
}
