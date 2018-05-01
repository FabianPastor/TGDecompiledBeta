package org.telegram.ui.Cells;

import android.content.Context;
import android.text.TextUtils.TruncateAt;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox;
import org.telegram.ui.Components.LayoutHelper;

public class ShareDialogCell extends FrameLayout {
    private AvatarDrawable avatarDrawable = new AvatarDrawable();
    private CheckBox checkBox;
    private int currentAccount = UserConfig.selectedAccount;
    private BackupImageView imageView;
    private TextView nameTextView;

    public ShareDialogCell(Context context) {
        super(context);
        this.imageView = new BackupImageView(context);
        this.imageView.setRoundRadius(AndroidUtilities.dp(27.0f));
        addView(this.imageView, LayoutHelper.createFrame(54, 54.0f, 49, 0.0f, 7.0f, 0.0f, 0.0f));
        this.nameTextView = new TextView(context);
        this.nameTextView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        this.nameTextView.setTextSize(1, 12.0f);
        this.nameTextView.setMaxLines(2);
        this.nameTextView.setGravity(49);
        this.nameTextView.setLines(2);
        this.nameTextView.setEllipsize(TruncateAt.END);
        addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 6.0f, 64.0f, 6.0f, 0.0f));
        this.checkBox = new CheckBox(context, C0446R.drawable.round_check2);
        this.checkBox.setSize(24);
        this.checkBox.setCheckOffset(AndroidUtilities.dp(1.0f));
        this.checkBox.setVisibility(0);
        this.checkBox.setColor(Theme.getColor(Theme.key_dialogRoundCheckBox), Theme.getColor(Theme.key_dialogRoundCheckBoxCheck));
        addView(this.checkBox, LayoutHelper.createFrame(24, 24.0f, 49, 17.0f, 39.0f, 0.0f, 0.0f));
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(NUM), NUM));
    }

    public void setDialog(int i, boolean z, CharSequence charSequence) {
        TLObject tLObject = null;
        if (i > 0) {
            User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(i));
            this.avatarDrawable.setInfo(user);
            if (UserObject.isUserSelf(user)) {
                this.nameTextView.setText(LocaleController.getString("SavedMessages", C0446R.string.SavedMessages));
                this.avatarDrawable.setSavedMessages(1);
            } else {
                if (charSequence != null) {
                    this.nameTextView.setText(charSequence);
                } else if (user != null) {
                    this.nameTextView.setText(ContactsController.formatName(user.first_name, user.last_name));
                } else {
                    this.nameTextView.setText(TtmlNode.ANONYMOUS_REGION_ID);
                }
                if (!(user == null || user.photo == null)) {
                    tLObject = user.photo.photo_small;
                }
            }
        } else {
            Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-i));
            if (charSequence != null) {
                this.nameTextView.setText(charSequence);
            } else if (chat != 0) {
                this.nameTextView.setText(chat.title);
            } else {
                this.nameTextView.setText(TtmlNode.ANONYMOUS_REGION_ID);
            }
            this.avatarDrawable.setInfo(chat);
            if (!(chat == null || chat.photo == null)) {
                tLObject = chat.photo.photo_small;
            }
        }
        this.imageView.setImage(tLObject, "50_50", this.avatarDrawable);
        this.checkBox.setChecked(z, null);
    }

    public void setChecked(boolean z, boolean z2) {
        this.checkBox.setChecked(z, z2);
    }
}
