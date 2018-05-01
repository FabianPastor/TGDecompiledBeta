package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.LocaleController;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.UserProfilePhoto;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBoxSquare;
import org.telegram.ui.Components.LayoutHelper;

public class CheckBoxUserCell
  extends FrameLayout
{
  private AvatarDrawable avatarDrawable;
  private CheckBoxSquare checkBox;
  private TLRPC.User currentUser;
  private BackupImageView imageView;
  private boolean needDivider;
  private TextView textView;
  
  public CheckBoxUserCell(Context paramContext, boolean paramBoolean)
  {
    super(paramContext);
    this.textView = new TextView(paramContext);
    TextView localTextView = this.textView;
    Object localObject;
    int i;
    label98:
    label123:
    label133:
    float f;
    if (paramBoolean)
    {
      localObject = "dialogTextBlack";
      localTextView.setTextColor(Theme.getColor((String)localObject));
      this.textView.setTextSize(1, 16.0F);
      this.textView.setLines(1);
      this.textView.setMaxLines(1);
      this.textView.setSingleLine(true);
      this.textView.setEllipsize(TextUtils.TruncateAt.END);
      localObject = this.textView;
      if (!LocaleController.isRTL) {
        break label331;
      }
      i = 5;
      ((TextView)localObject).setGravity(i | 0x10);
      localObject = this.textView;
      if (!LocaleController.isRTL) {
        break label337;
      }
      i = 5;
      if (!LocaleController.isRTL) {
        break label343;
      }
      j = 17;
      f = j;
      if (!LocaleController.isRTL) {
        break label350;
      }
      j = 94;
      label148:
      addView((View)localObject, LayoutHelper.createFrame(-1, -1.0F, i | 0x30, f, 0.0F, j, 0.0F));
      this.avatarDrawable = new AvatarDrawable();
      this.imageView = new BackupImageView(paramContext);
      this.imageView.setRoundRadius(AndroidUtilities.dp(36.0F));
      localObject = this.imageView;
      if (!LocaleController.isRTL) {
        break label357;
      }
      i = 5;
      label222:
      addView((View)localObject, LayoutHelper.createFrame(36, 36.0F, i | 0x30, 48.0F, 6.0F, 48.0F, 0.0F));
      this.checkBox = new CheckBoxSquare(paramContext, paramBoolean);
      paramContext = this.checkBox;
      if (!LocaleController.isRTL) {
        break label363;
      }
      i = 5;
      label274:
      if (!LocaleController.isRTL) {
        break label369;
      }
      j = 0;
      label283:
      f = j;
      if (!LocaleController.isRTL) {
        break label376;
      }
    }
    label331:
    label337:
    label343:
    label350:
    label357:
    label363:
    label369:
    label376:
    for (int j = 17;; j = 0)
    {
      addView(paramContext, LayoutHelper.createFrame(18, 18.0F, i | 0x30, f, 15.0F, j, 0.0F));
      return;
      localObject = "windowBackgroundWhiteBlackText";
      break;
      i = 3;
      break label98;
      i = 3;
      break label123;
      j = 94;
      break label133;
      j = 17;
      break label148;
      i = 3;
      break label222;
      i = 3;
      break label274;
      j = 17;
      break label283;
    }
  }
  
  public CheckBoxSquare getCheckBox()
  {
    return this.checkBox;
  }
  
  public TLRPC.User getCurrentUser()
  {
    return this.currentUser;
  }
  
  public TextView getTextView()
  {
    return this.textView;
  }
  
  public boolean isChecked()
  {
    return this.checkBox.isChecked();
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    if (this.needDivider) {
      paramCanvas.drawLine(getPaddingLeft(), getHeight() - 1, getWidth() - getPaddingRight(), getHeight() - 1, Theme.dividerPaint);
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int i = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), NUM);
    paramInt2 = AndroidUtilities.dp(48.0F);
    if (this.needDivider) {}
    for (paramInt1 = 1;; paramInt1 = 0)
    {
      super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(paramInt1 + paramInt2, NUM));
      return;
    }
  }
  
  public void setChecked(boolean paramBoolean1, boolean paramBoolean2)
  {
    this.checkBox.setChecked(paramBoolean1, paramBoolean2);
  }
  
  public void setTextColor(int paramInt)
  {
    this.textView.setTextColor(paramInt);
  }
  
  public void setUser(TLRPC.User paramUser, boolean paramBoolean1, boolean paramBoolean2)
  {
    boolean bool = false;
    this.currentUser = paramUser;
    this.textView.setText(ContactsController.formatName(paramUser.first_name, paramUser.last_name));
    this.checkBox.setChecked(paramBoolean1, false);
    Object localObject1 = null;
    this.avatarDrawable.setInfo(paramUser);
    Object localObject2 = localObject1;
    if (paramUser != null)
    {
      localObject2 = localObject1;
      if (paramUser.photo != null) {
        localObject2 = paramUser.photo.photo_small;
      }
    }
    this.imageView.setImage((TLObject)localObject2, "50_50", this.avatarDrawable);
    this.needDivider = paramBoolean2;
    paramBoolean1 = bool;
    if (!paramBoolean2) {
      paramBoolean1 = true;
    }
    setWillNotDraw(paramBoolean1);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/CheckBoxUserCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */