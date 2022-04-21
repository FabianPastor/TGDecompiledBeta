package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;

public class AccountSelectCell extends FrameLayout {
    private int accountNumber;
    private AvatarDrawable avatarDrawable;
    private ImageView checkImageView;
    private BackupImageView imageView;
    private TextView infoTextView;
    private TextView textView;

    public AccountSelectCell(Context context, boolean hasInfo) {
        super(context);
        AvatarDrawable avatarDrawable2 = new AvatarDrawable();
        this.avatarDrawable = avatarDrawable2;
        avatarDrawable2.setTextSize(AndroidUtilities.dp(12.0f));
        BackupImageView backupImageView = new BackupImageView(context);
        this.imageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(18.0f));
        addView(this.imageView, LayoutHelper.createFrame(36, 36.0f, 51, 10.0f, 10.0f, 0.0f, 0.0f));
        TextView textView2 = new TextView(context);
        this.textView = textView2;
        textView2.setTextSize(1, 15.0f);
        this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setGravity(19);
        this.textView.setEllipsize(TextUtils.TruncateAt.END);
        if (hasInfo) {
            addView(this.textView, LayoutHelper.createFrame(-2, -2.0f, 51, 61.0f, 7.0f, 8.0f, 0.0f));
            this.textView.setTextColor(Theme.getColor("voipgroup_nameText"));
            this.textView.setText(LocaleController.getString("VoipGroupDisplayAs", NUM));
            TextView textView3 = new TextView(context);
            this.infoTextView = textView3;
            textView3.setTextColor(Theme.getColor("voipgroup_lastSeenText"));
            this.infoTextView.setTextSize(1, 15.0f);
            this.infoTextView.setLines(1);
            this.infoTextView.setMaxLines(1);
            this.infoTextView.setSingleLine(true);
            this.infoTextView.setMaxWidth(AndroidUtilities.dp(320.0f));
            this.infoTextView.setGravity(51);
            this.infoTextView.setEllipsize(TextUtils.TruncateAt.END);
            addView(this.infoTextView, LayoutHelper.createFrame(-2, -2.0f, 51, 61.0f, 27.0f, 8.0f, 0.0f));
            return;
        }
        addView(this.textView, LayoutHelper.createFrame(-1, -1.0f, 51, 61.0f, 0.0f, 56.0f, 0.0f));
        this.textView.setTextColor(Theme.getColor("actionBarDefaultSubmenuItem"));
        ImageView imageView2 = new ImageView(context);
        this.checkImageView = imageView2;
        imageView2.setImageResource(NUM);
        this.checkImageView.setScaleType(ImageView.ScaleType.CENTER);
        this.checkImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chats_menuItemCheck"), PorterDuff.Mode.MULTIPLY));
        addView(this.checkImageView, LayoutHelper.createFrame(40, -1.0f, 53, 0.0f, 0.0f, 6.0f, 0.0f));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (this.checkImageView == null && (this.infoTextView == null || getLayoutParams().width == -2)) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(56.0f), NUM));
        } else {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(56.0f), NUM));
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.infoTextView == null) {
            this.textView.setTextColor(Theme.getColor("chats_menuItemText"));
        }
    }

    public void setObject(TLObject object) {
        if (object instanceof TLRPC.User) {
            TLRPC.User user = (TLRPC.User) object;
            this.avatarDrawable.setInfo(user);
            this.infoTextView.setText(ContactsController.formatName(user.first_name, user.last_name));
            this.imageView.setForUserOrChat(user, this.avatarDrawable);
            return;
        }
        TLRPC.Chat chat = (TLRPC.Chat) object;
        this.avatarDrawable.setInfo(chat);
        this.infoTextView.setText(chat.title);
        this.imageView.setForUserOrChat(chat, this.avatarDrawable);
    }

    public void setAccount(int account, boolean check) {
        this.accountNumber = account;
        TLRPC.User user = UserConfig.getInstance(account).getCurrentUser();
        this.avatarDrawable.setInfo(user);
        this.textView.setText(ContactsController.formatName(user.first_name, user.last_name));
        this.imageView.getImageReceiver().setCurrentAccount(account);
        this.imageView.setForUserOrChat(user, this.avatarDrawable);
        this.checkImageView.setVisibility((!check || account != UserConfig.selectedAccount) ? 4 : 0);
    }

    public int getAccountNumber() {
        return this.accountNumber;
    }
}
