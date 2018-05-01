package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.LocaleController;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBoxSquare;
import org.telegram.ui.Components.LayoutHelper;

public class CheckBoxUserCell extends FrameLayout {
    private AvatarDrawable avatarDrawable;
    private CheckBoxSquare checkBox;
    private User currentUser;
    private BackupImageView imageView;
    private boolean needDivider;
    private TextView textView;

    public CheckBoxUserCell(Context context, boolean z) {
        super(context);
        this.textView = new TextView(context);
        this.textView.setTextColor(Theme.getColor(z ? Theme.key_dialogTextBlack : Theme.key_windowBackgroundWhiteBlackText));
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setEllipsize(TruncateAt.END);
        int i = 3;
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        View view = this.textView;
        int i2 = (LocaleController.isRTL ? 5 : 3) | 48;
        int i3 = 94;
        int i4 = 17;
        float f = (float) (LocaleController.isRTL ? 17 : 94);
        if (!LocaleController.isRTL) {
            i3 = 17;
        }
        addView(view, LayoutHelper.createFrame(-1, -1.0f, i2, f, 0.0f, (float) i3, 0.0f));
        this.avatarDrawable = new AvatarDrawable();
        this.imageView = new BackupImageView(context);
        this.imageView.setRoundRadius(AndroidUtilities.dp(36.0f));
        addView(this.imageView, LayoutHelper.createFrame(36, 36.0f, (LocaleController.isRTL ? 5 : 3) | 48, 48.0f, 6.0f, 48.0f, 0.0f));
        this.checkBox = new CheckBoxSquare(context, z);
        context = this.checkBox;
        if (LocaleController.isRTL) {
            i = 5;
        }
        i2 = i | 48;
        float f2 = (float) (LocaleController.isRTL ? false : true);
        if (!LocaleController.isRTL) {
            i4 = 0;
        }
        addView(context, LayoutHelper.createFrame(18, 18.0f, i2, f2, 15.0f, (float) i4, 0.0f));
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f) + this.needDivider, NUM));
    }

    public void setTextColor(int i) {
        this.textView.setTextColor(i);
    }

    public User getCurrentUser() {
        return this.currentUser;
    }

    public void setUser(User user, boolean z, boolean z2) {
        this.currentUser = user;
        this.textView.setText(ContactsController.formatName(user.first_name, user.last_name));
        this.checkBox.setChecked(z, false);
        this.avatarDrawable.setInfo(user);
        TLObject tLObject = (user == null || !user.photo) ? null : user.photo.photo_small;
        this.imageView.setImage(tLObject, "50_50", this.avatarDrawable);
        this.needDivider = z2;
        setWillNotDraw(z2 ^ 1);
    }

    public void setChecked(boolean z, boolean z2) {
        this.checkBox.setChecked(z, z2);
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

    protected void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine((float) getPaddingLeft(), (float) (getHeight() - 1), (float) (getWidth() - getPaddingRight()), (float) (getHeight() - 1), Theme.dividerPaint);
        }
    }
}
