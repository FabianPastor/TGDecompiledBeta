package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;

public class JoinSheetUserCell extends FrameLayout {
    private AvatarDrawable avatarDrawable = new AvatarDrawable();
    private BackupImageView imageView;
    private TextView nameTextView;
    private int[] result = new int[1];

    public JoinSheetUserCell(Context context) {
        super(context);
        BackupImageView backupImageView = new BackupImageView(context);
        this.imageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(27.0f));
        addView(this.imageView, LayoutHelper.createFrame(54, 54.0f, 49, 0.0f, 7.0f, 0.0f, 0.0f));
        TextView textView = new TextView(context);
        this.nameTextView = textView;
        textView.setTextColor(Theme.getColor("dialogTextBlack"));
        this.nameTextView.setTextSize(1, 12.0f);
        this.nameTextView.setMaxLines(1);
        this.nameTextView.setGravity(49);
        this.nameTextView.setLines(1);
        this.nameTextView.setSingleLine(true);
        this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
        addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 6.0f, 65.0f, 6.0f, 0.0f));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(100.0f), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(90.0f), NUM));
    }

    public void setUser(TLRPC.User user) {
        this.nameTextView.setText(ContactsController.formatName(user.first_name, user.last_name));
        this.avatarDrawable.setInfo(user);
        this.imageView.setForUserOrChat(user, this.avatarDrawable);
    }

    public void setCount(int count) {
        this.nameTextView.setText("");
        AvatarDrawable avatarDrawable2 = this.avatarDrawable;
        avatarDrawable2.setInfo(0, (String) null, (String) null, "+" + LocaleController.formatShortNumber(count, this.result));
        this.imageView.setImage((ImageLocation) null, "50_50", (Drawable) this.avatarDrawable, (Object) null);
    }
}
