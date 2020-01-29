package org.telegram.ui.Cells;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.LayoutHelper;

public class InviteUserCell extends FrameLayout {
    private AvatarDrawable avatarDrawable = new AvatarDrawable();
    private BackupImageView avatarImageView;
    private CheckBox2 checkBox;
    private ContactsController.Contact currentContact;
    private CharSequence currentName;
    private SimpleTextView nameTextView;
    private SimpleTextView statusTextView;

    public boolean hasOverlappingRendering() {
        return false;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public InviteUserCell(Context context, boolean z) {
        super(context);
        Context context2 = context;
        this.avatarImageView = new BackupImageView(context2);
        this.avatarImageView.setRoundRadius(AndroidUtilities.dp(24.0f));
        int i = 5;
        addView(this.avatarImageView, LayoutHelper.createFrame(50, 50.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 11.0f, 11.0f, LocaleController.isRTL ? 11.0f : 0.0f, 0.0f));
        this.nameTextView = new SimpleTextView(context2);
        this.nameTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.nameTextView.setTextSize(17);
        this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(this.nameTextView, LayoutHelper.createFrame(-1, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 28.0f : 72.0f, 14.0f, LocaleController.isRTL ? 72.0f : 28.0f, 0.0f));
        this.statusTextView = new SimpleTextView(context2);
        this.statusTextView.setTextSize(16);
        this.statusTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(this.statusTextView, LayoutHelper.createFrame(-1, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 28.0f : 72.0f, 39.0f, LocaleController.isRTL ? 72.0f : 28.0f, 0.0f));
        if (z) {
            this.checkBox = new CheckBox2(context2, 21);
            this.checkBox.setColor((String) null, "windowBackgroundWhite", "checkboxCheck");
            this.checkBox.setDrawUnchecked(false);
            this.checkBox.setDrawBackgroundAsArc(3);
            addView(this.checkBox, LayoutHelper.createFrame(24, 24.0f, (!LocaleController.isRTL ? 3 : i) | 48, LocaleController.isRTL ? 0.0f : 40.0f, 40.0f, LocaleController.isRTL ? 39.0f : 0.0f, 0.0f));
        }
    }

    public void setUser(ContactsController.Contact contact, CharSequence charSequence) {
        this.currentContact = contact;
        this.currentName = charSequence;
        update(0);
    }

    public void setChecked(boolean z, boolean z2) {
        this.checkBox.setChecked(z, z2);
    }

    public ContactsController.Contact getContact() {
        return this.currentContact;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(72.0f), NUM));
    }

    public void recycle() {
        this.avatarImageView.getImageReceiver().cancelLoadImage();
    }

    public void update(int i) {
        ContactsController.Contact contact = this.currentContact;
        if (contact != null) {
            this.avatarDrawable.setInfo(contact.contact_id, contact.first_name, contact.last_name);
            CharSequence charSequence = this.currentName;
            if (charSequence != null) {
                this.nameTextView.setText(charSequence, true);
            } else {
                SimpleTextView simpleTextView = this.nameTextView;
                ContactsController.Contact contact2 = this.currentContact;
                simpleTextView.setText(ContactsController.formatName(contact2.first_name, contact2.last_name));
            }
            this.statusTextView.setTag("windowBackgroundWhiteGrayText");
            this.statusTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText"));
            ContactsController.Contact contact3 = this.currentContact;
            int i2 = contact3.imported;
            if (i2 > 0) {
                this.statusTextView.setText(LocaleController.formatPluralString("TelegramContacts", i2));
            } else {
                this.statusTextView.setText(contact3.phones.get(0));
            }
            this.avatarImageView.setImageDrawable(this.avatarDrawable);
        }
    }
}
