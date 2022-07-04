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

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public InviteUserCell(Context context, boolean needCheck) {
        super(context);
        Context context2 = context;
        BackupImageView backupImageView = new BackupImageView(context2);
        this.avatarImageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(24.0f));
        int i = 5;
        addView(this.avatarImageView, LayoutHelper.createFrame(50, 50.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 11.0f, 11.0f, LocaleController.isRTL ? 11.0f : 0.0f, 0.0f));
        SimpleTextView simpleTextView = new SimpleTextView(context2);
        this.nameTextView = simpleTextView;
        simpleTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.nameTextView.setTextSize(17);
        this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(this.nameTextView, LayoutHelper.createFrame(-1, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 28.0f : 72.0f, 14.0f, LocaleController.isRTL ? 72.0f : 28.0f, 0.0f));
        SimpleTextView simpleTextView2 = new SimpleTextView(context2);
        this.statusTextView = simpleTextView2;
        simpleTextView2.setTextSize(16);
        this.statusTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(this.statusTextView, LayoutHelper.createFrame(-1, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 28.0f : 72.0f, 39.0f, LocaleController.isRTL ? 72.0f : 28.0f, 0.0f));
        if (needCheck) {
            CheckBox2 checkBox2 = new CheckBox2(context2, 21);
            this.checkBox = checkBox2;
            checkBox2.setColor((String) null, "windowBackgroundWhite", "checkboxCheck");
            this.checkBox.setDrawUnchecked(false);
            this.checkBox.setDrawBackgroundAsArc(3);
            addView(this.checkBox, LayoutHelper.createFrame(24, 24.0f, (!LocaleController.isRTL ? 3 : i) | 48, LocaleController.isRTL ? 0.0f : 40.0f, 40.0f, LocaleController.isRTL ? 39.0f : 0.0f, 0.0f));
        }
    }

    public void setUser(ContactsController.Contact contact, CharSequence name) {
        this.currentContact = contact;
        this.currentName = name;
        update(0);
    }

    public void setChecked(boolean checked, boolean animated) {
        this.checkBox.setChecked(checked, animated);
    }

    public ContactsController.Contact getContact() {
        return this.currentContact;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(72.0f), NUM));
    }

    public void recycle() {
        this.avatarImageView.getImageReceiver().cancelLoadImage();
    }

    public void update(int mask) {
        ContactsController.Contact contact = this.currentContact;
        if (contact != null) {
            this.avatarDrawable.setInfo((long) contact.contact_id, this.currentContact.first_name, this.currentContact.last_name);
            CharSequence charSequence = this.currentName;
            if (charSequence != null) {
                this.nameTextView.setText(charSequence, true);
            } else {
                this.nameTextView.setText(ContactsController.formatName(this.currentContact.first_name, this.currentContact.last_name));
            }
            this.statusTextView.setTag("windowBackgroundWhiteGrayText");
            this.statusTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText"));
            if (this.currentContact.imported > 0) {
                this.statusTextView.setText(LocaleController.formatPluralString("TelegramContacts", this.currentContact.imported, new Object[0]));
            } else {
                this.statusTextView.setText(this.currentContact.phones.get(0));
            }
            this.avatarImageView.setImageDrawable(this.avatarDrawable);
        }
    }

    public boolean hasOverlappingRendering() {
        return false;
    }
}
