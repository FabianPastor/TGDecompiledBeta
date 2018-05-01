package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.UserProfilePhoto;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;

public class DrawerProfileCell
  extends FrameLayout
{
  private boolean accountsShowed;
  private ImageView arrowView;
  private BackupImageView avatarImageView;
  private Integer currentColor;
  private Rect destRect = new Rect();
  private TextView nameTextView;
  private Paint paint = new Paint();
  private TextView phoneTextView;
  private ImageView shadowView;
  private Rect srcRect = new Rect();
  
  public DrawerProfileCell(Context paramContext)
  {
    super(paramContext);
    this.shadowView = new ImageView(paramContext);
    this.shadowView.setVisibility(4);
    this.shadowView.setScaleType(ImageView.ScaleType.FIT_XY);
    this.shadowView.setImageResource(NUM);
    addView(this.shadowView, LayoutHelper.createFrame(-1, 70, 83));
    this.avatarImageView = new BackupImageView(paramContext);
    this.avatarImageView.getImageReceiver().setRoundRadius(AndroidUtilities.dp(32.0F));
    addView(this.avatarImageView, LayoutHelper.createFrame(64, 64.0F, 83, 16.0F, 0.0F, 0.0F, 67.0F));
    this.nameTextView = new TextView(paramContext);
    this.nameTextView.setTextSize(1, 15.0F);
    this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    this.nameTextView.setLines(1);
    this.nameTextView.setMaxLines(1);
    this.nameTextView.setSingleLine(true);
    this.nameTextView.setGravity(3);
    this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
    addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0F, 83, 16.0F, 0.0F, 76.0F, 28.0F));
    this.phoneTextView = new TextView(paramContext);
    this.phoneTextView.setTextSize(1, 13.0F);
    this.phoneTextView.setLines(1);
    this.phoneTextView.setMaxLines(1);
    this.phoneTextView.setSingleLine(true);
    this.phoneTextView.setGravity(3);
    addView(this.phoneTextView, LayoutHelper.createFrame(-1, -2.0F, 83, 16.0F, 0.0F, 76.0F, 9.0F));
    this.arrowView = new ImageView(paramContext);
    this.arrowView.setScaleType(ImageView.ScaleType.CENTER);
    addView(this.arrowView, LayoutHelper.createFrame(59, 59, 85));
  }
  
  public boolean isAccountsShowed()
  {
    return this.accountsShowed;
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    Object localObject = Theme.getCachedWallpaper();
    int i;
    if (Theme.hasThemeKey("chats_menuTopShadow"))
    {
      i = Theme.getColor("chats_menuTopShadow");
      if ((this.currentColor == null) || (this.currentColor.intValue() != i))
      {
        this.currentColor = Integer.valueOf(i);
        this.shadowView.getDrawable().setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
      }
      this.nameTextView.setTextColor(Theme.getColor("chats_menuName"));
      if ((!Theme.isCustomTheme()) || (localObject == null)) {
        break label307;
      }
      this.phoneTextView.setTextColor(Theme.getColor("chats_menuPhone"));
      this.shadowView.setVisibility(0);
      if (!(localObject instanceof ColorDrawable)) {
        break label144;
      }
      ((Drawable)localObject).setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
      ((Drawable)localObject).draw(paramCanvas);
    }
    for (;;)
    {
      return;
      i = Theme.getServiceMessageColor() | 0xFF000000;
      break;
      label144:
      if ((localObject instanceof BitmapDrawable))
      {
        localObject = ((BitmapDrawable)localObject).getBitmap();
        float f1 = getMeasuredWidth() / ((Bitmap)localObject).getWidth();
        float f2 = getMeasuredHeight() / ((Bitmap)localObject).getHeight();
        if (f1 < f2) {
          f1 = f2;
        }
        for (;;)
        {
          i = (int)(getMeasuredWidth() / f1);
          int j = (int)(getMeasuredHeight() / f1);
          int k = (((Bitmap)localObject).getWidth() - i) / 2;
          int m = (((Bitmap)localObject).getHeight() - j) / 2;
          this.srcRect.set(k, m, k + i, m + j);
          this.destRect.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
          try
          {
            paramCanvas.drawBitmap((Bitmap)localObject, this.srcRect, this.destRect, this.paint);
          }
          catch (Throwable paramCanvas)
          {
            FileLog.e(paramCanvas);
          }
          break;
        }
        label307:
        this.shadowView.setVisibility(4);
        this.phoneTextView.setTextColor(Theme.getColor("chats_menuPhoneCats"));
        super.onDraw(paramCanvas);
      }
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    if (Build.VERSION.SDK_INT >= 21) {
      super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(148.0F) + AndroidUtilities.statusBarHeight, NUM));
    }
    for (;;)
    {
      return;
      try
      {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(148.0F), NUM));
      }
      catch (Exception localException)
      {
        setMeasuredDimension(View.MeasureSpec.getSize(paramInt1), AndroidUtilities.dp(148.0F));
        FileLog.e(localException);
      }
    }
  }
  
  public void setAccountsShowed(boolean paramBoolean)
  {
    if (this.accountsShowed == paramBoolean) {
      return;
    }
    this.accountsShowed = paramBoolean;
    ImageView localImageView = this.arrowView;
    if (this.accountsShowed) {}
    for (int i = NUM;; i = NUM)
    {
      localImageView.setImageResource(i);
      break;
    }
  }
  
  public void setOnArrowClickListener(final View.OnClickListener paramOnClickListener)
  {
    this.arrowView.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        paramAnonymousView = DrawerProfileCell.this;
        boolean bool;
        if (!DrawerProfileCell.this.accountsShowed)
        {
          bool = true;
          DrawerProfileCell.access$002(paramAnonymousView, bool);
          paramAnonymousView = DrawerProfileCell.this.arrowView;
          if (!DrawerProfileCell.this.accountsShowed) {
            break label68;
          }
        }
        label68:
        for (int i = NUM;; i = NUM)
        {
          paramAnonymousView.setImageResource(i);
          paramOnClickListener.onClick(DrawerProfileCell.this);
          return;
          bool = false;
          break;
        }
      }
    });
  }
  
  public void setUser(TLRPC.User paramUser, boolean paramBoolean)
  {
    if (paramUser == null) {
      return;
    }
    TLRPC.FileLocation localFileLocation = null;
    if (paramUser.photo != null) {
      localFileLocation = paramUser.photo.photo_small;
    }
    this.accountsShowed = paramBoolean;
    ImageView localImageView = this.arrowView;
    if (this.accountsShowed) {}
    for (int i = NUM;; i = NUM)
    {
      localImageView.setImageResource(i);
      this.nameTextView.setText(UserObject.getUserName(paramUser));
      this.phoneTextView.setText(PhoneFormat.getInstance().format("+" + paramUser.phone));
      paramUser = new AvatarDrawable(paramUser);
      paramUser.setColor(Theme.getColor("avatar_backgroundInProfileBlue"));
      this.avatarImageView.setImage(localFileLocation, "50_50", paramUser);
      break;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/DrawerProfileCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */