package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$User;
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

    public AccountSelectCell(Context context, boolean z) {
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
        if (z) {
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
    public void onMeasure(int i, int i2) {
        if (this.checkImageView != null) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(56.0f), NUM));
        } else {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(56.0f), NUM));
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.infoTextView == null) {
            this.textView.setTextColor(Theme.getColor("chats_menuItemText"));
        }
    }

    public void setObject(TLObject tLObject) {
        TLObject tLObject2 = tLObject;
        if (tLObject2 instanceof TLRPC$User) {
            TLRPC$User tLRPC$User = (TLRPC$User) tLObject2;
            this.avatarDrawable.setInfo(tLRPC$User);
            this.infoTextView.setText(ContactsController.formatName(tLRPC$User.first_name, tLRPC$User.last_name));
            this.imageView.setImage(ImageLocation.getForUserOrChat(tLRPC$User, 1), "50_50", ImageLocation.getForUserOrChat(tLRPC$User, 2), "50_50", (Drawable) this.avatarDrawable, (Object) tLRPC$User);
            return;
        }
        TLRPC$Chat tLRPC$Chat = (TLRPC$Chat) tLObject2;
        this.avatarDrawable.setInfo(tLRPC$Chat);
        this.infoTextView.setText(tLRPC$Chat.title);
        this.imageView.setImage(ImageLocation.getForUserOrChat(tLRPC$Chat, 1), "50_50", ImageLocation.getForUserOrChat(tLRPC$Chat, 2), "50_50", (Drawable) this.avatarDrawable, (Object) tLRPC$Chat);
    }

    public void setAccount(int i, boolean z) {
        this.accountNumber = i;
        TLRPC$User currentUser = UserConfig.getInstance(i).getCurrentUser();
        this.avatarDrawable.setInfo(currentUser);
        this.textView.setText(ContactsController.formatName(currentUser.first_name, currentUser.last_name));
        this.imageView.getImageReceiver().setCurrentAccount(i);
        this.imageView.setImage(ImageLocation.getForUserOrChat(currentUser, 1), "50_50", ImageLocation.getForUserOrChat(currentUser, 2), "50_50", (Drawable) this.avatarDrawable, (Object) currentUser);
        this.checkImageView.setVisibility((!z || i != UserConfig.selectedAccount) ? 4 : 0);
    }

    public int getAccountNumber() {
        return this.accountNumber;
    }
}
