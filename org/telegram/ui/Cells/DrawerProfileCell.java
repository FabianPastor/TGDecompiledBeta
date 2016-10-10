package org.telegram.ui.Cells;

import android.content.Context;
import android.content.res.Resources;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.UserProfilePhoto;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;

public class DrawerProfileCell
  extends FrameLayout
{
  private BackupImageView avatarImageView;
  private CloudView cloudView;
  private int currentColor;
  private Rect destRect = new Rect();
  private TextView nameTextView;
  private Paint paint = new Paint();
  private TextView phoneTextView;
  private ImageView shadowView;
  private Rect srcRect = new Rect();
  
  public DrawerProfileCell(Context paramContext)
  {
    super(paramContext);
    setBackgroundColor(-10907718);
    this.shadowView = new ImageView(paramContext);
    this.shadowView.setVisibility(4);
    this.shadowView.setScaleType(ImageView.ScaleType.FIT_XY);
    this.shadowView.setImageResource(2130837576);
    addView(this.shadowView, LayoutHelper.createFrame(-1, 70, 83));
    this.avatarImageView = new BackupImageView(paramContext);
    this.avatarImageView.getImageReceiver().setRoundRadius(AndroidUtilities.dp(32.0F));
    addView(this.avatarImageView, LayoutHelper.createFrame(64, 64.0F, 83, 16.0F, 0.0F, 0.0F, 67.0F));
    this.nameTextView = new TextView(paramContext);
    this.nameTextView.setTextColor(-1);
    this.nameTextView.setTextSize(1, 15.0F);
    this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    this.nameTextView.setLines(1);
    this.nameTextView.setMaxLines(1);
    this.nameTextView.setSingleLine(true);
    this.nameTextView.setGravity(3);
    this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
    addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0F, 83, 16.0F, 0.0F, 76.0F, 28.0F));
    this.phoneTextView = new TextView(paramContext);
    this.phoneTextView.setTextColor(-4004353);
    this.phoneTextView.setTextSize(1, 13.0F);
    this.phoneTextView.setLines(1);
    this.phoneTextView.setMaxLines(1);
    this.phoneTextView.setSingleLine(true);
    this.phoneTextView.setGravity(3);
    addView(this.phoneTextView, LayoutHelper.createFrame(-1, -2.0F, 83, 16.0F, 0.0F, 76.0F, 9.0F));
    this.cloudView = new CloudView(paramContext);
    addView(this.cloudView, LayoutHelper.createFrame(61, 61, 85));
  }
  
  public void invalidate()
  {
    super.invalidate();
    this.cloudView.invalidate();
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    Object localObject = ApplicationLoader.getCachedWallpaper();
    int i = ApplicationLoader.getServiceMessageColor();
    if (this.currentColor != i)
    {
      this.currentColor = i;
      this.shadowView.getDrawable().setColorFilter(new PorterDuffColorFilter(0xFF000000 | i, PorterDuff.Mode.MULTIPLY));
    }
    if ((ApplicationLoader.isCustomTheme()) && (localObject != null))
    {
      this.phoneTextView.setTextColor(-1);
      this.shadowView.setVisibility(0);
      if ((localObject instanceof ColorDrawable))
      {
        ((Drawable)localObject).setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
        ((Drawable)localObject).draw(paramCanvas);
      }
      while (!(localObject instanceof BitmapDrawable)) {
        return;
      }
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
        paramCanvas.drawBitmap((Bitmap)localObject, this.srcRect, this.destRect, this.paint);
        return;
      }
    }
    this.shadowView.setVisibility(4);
    this.phoneTextView.setTextColor(-4004353);
    super.onDraw(paramCanvas);
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    if (Build.VERSION.SDK_INT >= 21)
    {
      super.onMeasure(paramInt1, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(148.0F) + AndroidUtilities.statusBarHeight, 1073741824));
      return;
    }
    try
    {
      super.onMeasure(paramInt1, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(148.0F), 1073741824));
      return;
    }
    catch (Exception localException)
    {
      setMeasuredDimension(View.MeasureSpec.getSize(paramInt1), AndroidUtilities.dp(148.0F));
      FileLog.e("tmessages", localException);
    }
  }
  
  public void setUser(TLRPC.User paramUser)
  {
    if (paramUser == null) {
      return;
    }
    TLRPC.FileLocation localFileLocation = null;
    if (paramUser.photo != null) {
      localFileLocation = paramUser.photo.photo_small;
    }
    this.nameTextView.setText(UserObject.getUserName(paramUser));
    this.phoneTextView.setText(PhoneFormat.getInstance().format("+" + paramUser.phone));
    paramUser = new AvatarDrawable(paramUser);
    paramUser.setColor(-11500111);
    this.avatarImageView.setImage(localFileLocation, "50_50", paramUser);
  }
  
  private class CloudView
    extends View
  {
    private Drawable cloudDrawable = getResources().getDrawable(2130837599);
    private Paint paint = new Paint(1);
    
    public CloudView(Context paramContext)
    {
      super();
    }
    
    protected void onDraw(Canvas paramCanvas)
    {
      if ((ApplicationLoader.isCustomTheme()) && (ApplicationLoader.getCachedWallpaper() != null)) {
        this.paint.setColor(ApplicationLoader.getServiceMessageColor());
      }
      for (;;)
      {
        paramCanvas.drawCircle(getMeasuredWidth() / 2.0F, getMeasuredHeight() / 2.0F, AndroidUtilities.dp(34.0F) / 2.0F, this.paint);
        int i = (getMeasuredWidth() - AndroidUtilities.dp(33.0F)) / 2;
        int j = (getMeasuredHeight() - AndroidUtilities.dp(33.0F)) / 2;
        this.cloudDrawable.setBounds(i, j, AndroidUtilities.dp(33.0F) + i, AndroidUtilities.dp(33.0F) + j);
        this.cloudDrawable.draw(paramCanvas);
        return;
        this.paint.setColor(-12420183);
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/DrawerProfileCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */