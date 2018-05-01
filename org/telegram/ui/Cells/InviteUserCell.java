package org.telegram.ui.Cells;

import android.content.Context;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.ContactsController.Contact;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.GroupCreateCheckBox;
import org.telegram.ui.Components.LayoutHelper;

public class InviteUserCell
  extends FrameLayout
{
  private AvatarDrawable avatarDrawable = new AvatarDrawable();
  private BackupImageView avatarImageView;
  private GroupCreateCheckBox checkBox;
  private ContactsController.Contact currentContact;
  private CharSequence currentName;
  private SimpleTextView nameTextView;
  private SimpleTextView statusTextView;
  
  public InviteUserCell(Context paramContext, boolean paramBoolean)
  {
    super(paramContext);
    this.avatarImageView = new BackupImageView(paramContext);
    this.avatarImageView.setRoundRadius(AndroidUtilities.dp(24.0F));
    Object localObject = this.avatarImageView;
    int j;
    float f1;
    if (LocaleController.isRTL)
    {
      j = 5;
      if (!LocaleController.isRTL) {
        break label423;
      }
      f1 = 0.0F;
      label66:
      if (!LocaleController.isRTL) {
        break label430;
      }
      f2 = 11.0F;
      label76:
      addView((View)localObject, LayoutHelper.createFrame(50, 50.0F, j | 0x30, f1, 11.0F, f2, 0.0F));
      this.nameTextView = new SimpleTextView(paramContext);
      this.nameTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
      this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      this.nameTextView.setTextSize(17);
      localObject = this.nameTextView;
      if (!LocaleController.isRTL) {
        break label436;
      }
      j = 5;
      label161:
      ((SimpleTextView)localObject).setGravity(j | 0x30);
      localObject = this.nameTextView;
      if (!LocaleController.isRTL) {
        break label442;
      }
      j = 5;
      label186:
      if (!LocaleController.isRTL) {
        break label448;
      }
      f1 = 28.0F;
      label196:
      if (!LocaleController.isRTL) {
        break label455;
      }
      f2 = 72.0F;
      label206:
      addView((View)localObject, LayoutHelper.createFrame(-1, 20.0F, j | 0x30, f1, 14.0F, f2, 0.0F));
      this.statusTextView = new SimpleTextView(paramContext);
      this.statusTextView.setTextSize(16);
      localObject = this.statusTextView;
      if (!LocaleController.isRTL) {
        break label462;
      }
      j = 5;
      label266:
      ((SimpleTextView)localObject).setGravity(j | 0x30);
      localObject = this.statusTextView;
      if (!LocaleController.isRTL) {
        break label468;
      }
      j = 5;
      label291:
      if (!LocaleController.isRTL) {
        break label474;
      }
      f1 = 28.0F;
      label301:
      if (!LocaleController.isRTL) {
        break label481;
      }
      f2 = 72.0F;
      label311:
      addView((View)localObject, LayoutHelper.createFrame(-1, 20.0F, j | 0x30, f1, 39.0F, f2, 0.0F));
      if (paramBoolean)
      {
        this.checkBox = new GroupCreateCheckBox(paramContext);
        this.checkBox.setVisibility(0);
        paramContext = this.checkBox;
        if (!LocaleController.isRTL) {
          break label488;
        }
        j = i;
        label373:
        if (!LocaleController.isRTL) {
          break label494;
        }
        f1 = 0.0F;
        label382:
        if (!LocaleController.isRTL) {
          break label501;
        }
      }
    }
    label423:
    label430:
    label436:
    label442:
    label448:
    label455:
    label462:
    label468:
    label474:
    label481:
    label488:
    label494:
    label501:
    for (float f2 = 41.0F;; f2 = 0.0F)
    {
      addView(paramContext, LayoutHelper.createFrame(24, 24.0F, j | 0x30, f1, 41.0F, f2, 0.0F));
      return;
      j = 3;
      break;
      f1 = 11.0F;
      break label66;
      f2 = 0.0F;
      break label76;
      j = 3;
      break label161;
      j = 3;
      break label186;
      f1 = 72.0F;
      break label196;
      f2 = 28.0F;
      break label206;
      j = 3;
      break label266;
      j = 3;
      break label291;
      f1 = 72.0F;
      break label301;
      f2 = 28.0F;
      break label311;
      j = 3;
      break label373;
      f1 = 41.0F;
      break label382;
    }
  }
  
  public ContactsController.Contact getContact()
  {
    return this.currentContact;
  }
  
  public boolean hasOverlappingRendering()
  {
    return false;
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(72.0F), NUM));
  }
  
  public void recycle()
  {
    this.avatarImageView.getImageReceiver().cancelLoadImage();
  }
  
  public void setChecked(boolean paramBoolean1, boolean paramBoolean2)
  {
    this.checkBox.setChecked(paramBoolean1, paramBoolean2);
  }
  
  public void setUser(ContactsController.Contact paramContact, CharSequence paramCharSequence)
  {
    this.currentContact = paramContact;
    this.currentName = paramCharSequence;
    update(0);
  }
  
  public void update(int paramInt)
  {
    if (this.currentContact == null) {
      return;
    }
    this.avatarDrawable.setInfo(this.currentContact.contact_id, this.currentContact.first_name, this.currentContact.last_name, false);
    if (this.currentName != null)
    {
      this.nameTextView.setText(this.currentName, true);
      label56:
      this.statusTextView.setTag("groupcreate_offlineText");
      this.statusTextView.setTextColor(Theme.getColor("groupcreate_offlineText"));
      if (this.currentContact.imported <= 0) {
        break label147;
      }
      this.statusTextView.setText(LocaleController.formatPluralString("TelegramContacts", this.currentContact.imported));
    }
    for (;;)
    {
      this.avatarImageView.setImageDrawable(this.avatarDrawable);
      break;
      this.nameTextView.setText(ContactsController.formatName(this.currentContact.first_name, this.currentContact.last_name));
      break label56;
      label147:
      this.statusTextView.setText((CharSequence)this.currentContact.phones.get(0));
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/InviteUserCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */