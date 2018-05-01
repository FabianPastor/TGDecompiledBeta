package org.telegram.ui.Components;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController.Contact;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.UserProfilePhoto;
import org.telegram.ui.ActionBar.Theme;

public class GroupCreateSpan
  extends View
{
  private static Paint backPaint = new Paint(1);
  private static TextPaint textPaint = new TextPaint(1);
  private AvatarDrawable avatarDrawable;
  private int[] colors = new int[6];
  private ContactsController.Contact currentContact;
  private Drawable deleteDrawable;
  private boolean deleting;
  private ImageReceiver imageReceiver;
  private String key;
  private long lastUpdateTime;
  private StaticLayout nameLayout;
  private float progress;
  private RectF rect = new RectF();
  private int textWidth;
  private float textX;
  private int uid;
  
  public GroupCreateSpan(Context paramContext, ContactsController.Contact paramContact)
  {
    this(paramContext, null, paramContact);
  }
  
  public GroupCreateSpan(Context paramContext, TLRPC.User paramUser)
  {
    this(paramContext, paramUser, null);
  }
  
  public GroupCreateSpan(Context paramContext, TLRPC.User paramUser, ContactsController.Contact paramContact)
  {
    super(paramContext);
    this.currentContact = paramContact;
    this.deleteDrawable = getResources().getDrawable(NUM);
    textPaint.setTextSize(AndroidUtilities.dp(14.0F));
    this.avatarDrawable = new AvatarDrawable();
    this.avatarDrawable.setTextSize(AndroidUtilities.dp(12.0F));
    int i;
    if (paramUser != null)
    {
      this.avatarDrawable.setInfo(paramUser);
      this.uid = paramUser.id;
      this.imageReceiver = new ImageReceiver();
      this.imageReceiver.setRoundRadius(AndroidUtilities.dp(16.0F));
      this.imageReceiver.setParentView(this);
      this.imageReceiver.setImageCoords(0, 0, AndroidUtilities.dp(32.0F), AndroidUtilities.dp(32.0F));
      if (!AndroidUtilities.isTablet()) {
        break label340;
      }
      i = AndroidUtilities.dp(366.0F) / 2;
      label162:
      if (paramUser == null) {
        break label368;
      }
      paramContext = UserObject.getFirstName(paramUser);
    }
    for (;;)
    {
      this.nameLayout = new StaticLayout(TextUtils.ellipsize(paramContext.replace('\n', ' '), textPaint, i, TextUtils.TruncateAt.END), textPaint, 1000, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
      if (this.nameLayout.getLineCount() > 0)
      {
        this.textWidth = ((int)Math.ceil(this.nameLayout.getLineWidth(0)));
        this.textX = (-this.nameLayout.getLineLeft(0));
      }
      paramContact = null;
      paramContext = paramContact;
      if (paramUser != null)
      {
        paramContext = paramContact;
        if (paramUser.photo != null) {
          paramContext = paramUser.photo.photo_small;
        }
      }
      this.imageReceiver.setImage(paramContext, null, "50_50", this.avatarDrawable, null, null, 0, null, 1);
      updateColors();
      return;
      this.avatarDrawable.setInfo(0, paramContact.first_name, paramContact.last_name, false);
      this.uid = paramContact.contact_id;
      this.key = paramContact.key;
      break;
      label340:
      i = (Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) - AndroidUtilities.dp(164.0F)) / 2;
      break label162;
      label368:
      if (!TextUtils.isEmpty(paramContact.first_name)) {
        paramContext = paramContact.first_name;
      } else {
        paramContext = paramContact.last_name;
      }
    }
  }
  
  public void cancelDeleteAnimation()
  {
    if (!this.deleting) {}
    for (;;)
    {
      return;
      this.deleting = false;
      this.lastUpdateTime = System.currentTimeMillis();
      invalidate();
    }
  }
  
  public ContactsController.Contact getContact()
  {
    return this.currentContact;
  }
  
  public String getKey()
  {
    return this.key;
  }
  
  public int getUid()
  {
    return this.uid;
  }
  
  public boolean isDeleting()
  {
    return this.deleting;
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    long l2;
    if (((this.deleting) && (this.progress != 1.0F)) || ((!this.deleting) && (this.progress != 0.0F)))
    {
      long l1 = System.currentTimeMillis() - this.lastUpdateTime;
      if (l1 >= 0L)
      {
        l2 = l1;
        if (l1 <= 17L) {}
      }
      else
      {
        l2 = 17L;
      }
      if (!this.deleting) {
        break label442;
      }
      this.progress += (float)l2 / 120.0F;
      if (this.progress >= 1.0F) {
        this.progress = 1.0F;
      }
    }
    for (;;)
    {
      invalidate();
      paramCanvas.save();
      this.rect.set(0.0F, 0.0F, getMeasuredWidth(), AndroidUtilities.dp(32.0F));
      backPaint.setColor(Color.argb(255, this.colors[0] + (int)((this.colors[1] - this.colors[0]) * this.progress), this.colors[2] + (int)((this.colors[3] - this.colors[2]) * this.progress), this.colors[4] + (int)((this.colors[5] - this.colors[4]) * this.progress)));
      paramCanvas.drawRoundRect(this.rect, AndroidUtilities.dp(16.0F), AndroidUtilities.dp(16.0F), backPaint);
      this.imageReceiver.draw(paramCanvas);
      if (this.progress != 0.0F)
      {
        backPaint.setColor(this.avatarDrawable.getColor());
        backPaint.setAlpha((int)(255.0F * this.progress));
        paramCanvas.drawCircle(AndroidUtilities.dp(16.0F), AndroidUtilities.dp(16.0F), AndroidUtilities.dp(16.0F), backPaint);
        paramCanvas.save();
        paramCanvas.rotate(45.0F * (1.0F - this.progress), AndroidUtilities.dp(16.0F), AndroidUtilities.dp(16.0F));
        this.deleteDrawable.setBounds(AndroidUtilities.dp(11.0F), AndroidUtilities.dp(11.0F), AndroidUtilities.dp(21.0F), AndroidUtilities.dp(21.0F));
        this.deleteDrawable.setAlpha((int)(255.0F * this.progress));
        this.deleteDrawable.draw(paramCanvas);
        paramCanvas.restore();
      }
      paramCanvas.translate(this.textX + AndroidUtilities.dp(41.0F), AndroidUtilities.dp(8.0F));
      this.nameLayout.draw(paramCanvas);
      paramCanvas.restore();
      return;
      label442:
      this.progress -= (float)l2 / 120.0F;
      if (this.progress < 0.0F) {
        this.progress = 0.0F;
      }
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    setMeasuredDimension(AndroidUtilities.dp(57.0F) + this.textWidth, AndroidUtilities.dp(32.0F));
  }
  
  public void startDeleteAnimation()
  {
    if (this.deleting) {}
    for (;;)
    {
      return;
      this.deleting = true;
      this.lastUpdateTime = System.currentTimeMillis();
      invalidate();
    }
  }
  
  public void updateColors()
  {
    int i = Theme.getColor("avatar_backgroundGroupCreateSpanBlue");
    int j = Theme.getColor("groupcreate_spanBackground");
    int k = Theme.getColor("groupcreate_spanText");
    this.colors[0] = Color.red(j);
    this.colors[1] = Color.red(i);
    this.colors[2] = Color.green(j);
    this.colors[3] = Color.green(i);
    this.colors[4] = Color.blue(j);
    this.colors[5] = Color.blue(i);
    textPaint.setColor(k);
    this.deleteDrawable.setColorFilter(new PorterDuffColorFilter(k, PorterDuff.Mode.MULTIPLY));
    backPaint.setColor(j);
    this.avatarDrawable.setColor(AvatarDrawable.getColorForId(5));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/GroupCreateSpan.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */