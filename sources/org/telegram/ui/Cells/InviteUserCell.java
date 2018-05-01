package org.telegram.ui.Cells;

import android.content.Context;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.ContactsController.Contact;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.GroupCreateCheckBox;
import org.telegram.ui.Components.LayoutHelper;

public class InviteUserCell extends FrameLayout {
    private AvatarDrawable avatarDrawable = new AvatarDrawable();
    private BackupImageView avatarImageView;
    private GroupCreateCheckBox checkBox;
    private Contact currentContact;
    private CharSequence currentName;
    private SimpleTextView nameTextView;
    private SimpleTextView statusTextView;

    public boolean hasOverlappingRendering() {
        return false;
    }

    public InviteUserCell(Context context, boolean z) {
        Context context2 = context;
        super(context);
        this.avatarImageView = new BackupImageView(context2);
        this.avatarImageView.setRoundRadius(AndroidUtilities.dp(24.0f));
        int i = 3;
        addView(this.avatarImageView, LayoutHelper.createFrame(50, 50.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 11.0f, 11.0f, LocaleController.isRTL ? 11.0f : 0.0f, 0.0f));
        r0.nameTextView = new SimpleTextView(context2);
        r0.nameTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        r0.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        r0.nameTextView.setTextSize(17);
        r0.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(r0.nameTextView, LayoutHelper.createFrame(-1, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 28.0f : 72.0f, 14.0f, LocaleController.isRTL ? 72.0f : 28.0f, 0.0f));
        r0.statusTextView = new SimpleTextView(context2);
        r0.statusTextView.setTextSize(16);
        r0.statusTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(r0.statusTextView, LayoutHelper.createFrame(-1, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 28.0f : 72.0f, 39.0f, LocaleController.isRTL ? 72.0f : 28.0f, 0.0f));
        if (z) {
            r0.checkBox = new GroupCreateCheckBox(context2);
            r0.checkBox.setVisibility(0);
            View view = r0.checkBox;
            if (LocaleController.isRTL) {
                i = 5;
            }
            addView(view, LayoutHelper.createFrame(24, 24.0f, i | 48, LocaleController.isRTL ? 0.0f : 41.0f, 41.0f, LocaleController.isRTL ? 41.0f : 0.0f, 0.0f));
        }
    }

    public void setUser(Contact contact, CharSequence charSequence) {
        this.currentContact = contact;
        this.currentName = charSequence;
        update(null);
    }

    public void setChecked(boolean z, boolean z2) {
        this.checkBox.setChecked(z, z2);
    }

    public Contact getContact() {
        return this.currentContact;
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(72.0f), NUM));
    }

    public void recycle() {
        this.avatarImageView.getImageReceiver().cancelLoadImage();
    }

    public void update(int i) {
        if (this.currentContact != 0) {
            this.avatarDrawable.setInfo(this.currentContact.contact_id, this.currentContact.first_name, this.currentContact.last_name, false);
            if (this.currentName != 0) {
                this.nameTextView.setText(this.currentName, true);
            } else {
                this.nameTextView.setText(ContactsController.formatName(this.currentContact.first_name, this.currentContact.last_name));
            }
            this.statusTextView.setTag(Theme.key_groupcreate_offlineText);
            this.statusTextView.setTextColor(Theme.getColor(Theme.key_groupcreate_offlineText));
            if (this.currentContact.imported > 0) {
                this.statusTextView.setText(LocaleController.formatPluralString("TelegramContacts", this.currentContact.imported));
            } else {
                this.statusTextView.setText((CharSequence) this.currentContact.phones.get(0));
            }
            this.avatarImageView.setImageDrawable(this.avatarDrawable);
        }
    }
}
