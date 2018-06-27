package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.text.TextUtils.TruncateAt;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;

public class AccountSelectCell extends FrameLayout {
    private int accountNumber;
    private AvatarDrawable avatarDrawable = new AvatarDrawable();
    private ImageView checkImageView;
    private BackupImageView imageView;
    private TextView textView;

    public AccountSelectCell(Context context) {
        super(context);
        this.avatarDrawable.setTextSize(AndroidUtilities.dp(12.0f));
        this.imageView = new BackupImageView(context);
        this.imageView.setRoundRadius(AndroidUtilities.dp(18.0f));
        addView(this.imageView, LayoutHelper.createFrame(36, 36.0f, 51, 10.0f, 10.0f, 0.0f, 0.0f));
        this.textView = new TextView(context);
        this.textView.setTextColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuItem));
        this.textView.setTextSize(1, 15.0f);
        this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setGravity(19);
        this.textView.setEllipsize(TruncateAt.END);
        addView(this.textView, LayoutHelper.createFrame(-1, -1.0f, 51, 61.0f, 0.0f, 56.0f, 0.0f));
        this.checkImageView = new ImageView(context);
        this.checkImageView.setImageResource(R.drawable.account_check);
        this.checkImageView.setScaleType(ScaleType.CENTER);
        this.checkImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chats_menuItemCheck), Mode.MULTIPLY));
        addView(this.checkImageView, LayoutHelper.createFrame(40, -1.0f, 53, 0.0f, 0.0f, 6.0f, 0.0f));
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(56.0f), NUM));
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.textView.setTextColor(Theme.getColor(Theme.key_chats_menuItemText));
    }

    public void setAccount(int account, boolean check) {
        TLObject avatar;
        this.accountNumber = account;
        User user = UserConfig.getInstance(this.accountNumber).getCurrentUser();
        this.avatarDrawable.setInfo(user);
        this.textView.setText(ContactsController.formatName(user.first_name, user.last_name));
        if (user.photo == null || user.photo.photo_small == null || user.photo.photo_small.volume_id == 0 || user.photo.photo_small.local_id == 0) {
            avatar = null;
        } else {
            avatar = user.photo.photo_small;
        }
        this.imageView.getImageReceiver().setCurrentAccount(account);
        this.imageView.setImage(avatar, "50_50", this.avatarDrawable);
        ImageView imageView = this.checkImageView;
        int i = (check && account == UserConfig.selectedAccount) ? 0 : 4;
        imageView.setVisibility(i);
    }

    public int getAccountNumber() {
        return this.accountNumber;
    }
}
