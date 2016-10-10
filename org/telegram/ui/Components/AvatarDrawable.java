package org.telegram.ui.Components;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.User;

public class AvatarDrawable
  extends Drawable
{
  private static int[] arrColors;
  private static int[] arrColorsButtons = { -4437183, -1674199, -9216066, -12020419, -13007715, -11959891, -9216066, -11959891 };
  private static int[] arrColorsNames;
  private static int[] arrColorsProfiles;
  private static int[] arrColorsProfilesBack;
  private static int[] arrColorsProfilesText;
  private static Drawable broadcastDrawable;
  private static TextPaint namePaint;
  private static TextPaint namePaintSmall;
  private static Paint paint = new Paint(1);
  private static Drawable photoDrawable;
  private int color;
  private boolean drawBrodcast;
  private boolean drawPhoto;
  private boolean isProfile;
  private boolean smallStyle;
  private StringBuilder stringBuilder = new StringBuilder(5);
  private float textHeight;
  private StaticLayout textLayout;
  private float textLeft;
  private float textWidth;
  
  static
  {
    arrColors = new int[] { -1743531, -881592, -7436818, -8992691, -10502443, -11232035, -7436818, -887654 };
    arrColorsProfiles = new int[] { -2592923, -615071, -7570990, -9981091, -11099461, -11500111, -7570990, -819290 };
    arrColorsProfilesBack = new int[] { -3514282, -947900, -8557884, -11099828, -12283220, -10907718, -8557884, -11762506 };
    arrColorsProfilesText = new int[] { -406587, -139832, -3291923, -4133446, -4660496, -2626822, -3291923, -4990985 };
    arrColorsNames = new int[] { -3516848, -2589911, -11627828, -11488718, -12406360, -11627828, -11627828, -11627828 };
  }
  
  public AvatarDrawable()
  {
    if (namePaint == null)
    {
      namePaint = new TextPaint(1);
      namePaint.setColor(-1);
      namePaintSmall = new TextPaint(1);
      namePaintSmall.setColor(-1);
      broadcastDrawable = ApplicationLoader.applicationContext.getResources().getDrawable(2130837581);
    }
    namePaint.setTextSize(AndroidUtilities.dp(20.0F));
    namePaintSmall.setTextSize(AndroidUtilities.dp(14.0F));
  }
  
  public AvatarDrawable(TLRPC.Chat paramChat)
  {
    this(paramChat, false);
  }
  
  public AvatarDrawable(TLRPC.Chat paramChat, boolean paramBoolean)
  {
    this();
    this.isProfile = paramBoolean;
    int i;
    String str;
    if (paramChat != null)
    {
      i = paramChat.id;
      str = paramChat.title;
      if (paramChat.id >= 0) {
        break label44;
      }
    }
    label44:
    for (paramBoolean = true;; paramBoolean = false)
    {
      setInfo(i, str, null, paramBoolean, null);
      return;
    }
  }
  
  public AvatarDrawable(TLRPC.User paramUser)
  {
    this(paramUser, false);
  }
  
  public AvatarDrawable(TLRPC.User paramUser, boolean paramBoolean)
  {
    this();
    this.isProfile = paramBoolean;
    if (paramUser != null) {
      setInfo(paramUser.id, paramUser.first_name, paramUser.last_name, false, null);
    }
  }
  
  public static int getButtonColorForId(int paramInt)
  {
    return arrColorsButtons[getColorIndex(paramInt)];
  }
  
  public static int getColorForId(int paramInt)
  {
    return arrColors[getColorIndex(paramInt)];
  }
  
  public static int getColorIndex(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt < 8)) {
      return paramInt;
    }
    return Math.abs(paramInt % arrColors.length);
  }
  
  public static int getNameColorForId(int paramInt)
  {
    return arrColorsNames[getColorIndex(paramInt)];
  }
  
  public static int getProfileBackColorForId(int paramInt)
  {
    return arrColorsProfilesBack[getColorIndex(paramInt)];
  }
  
  public static int getProfileColorForId(int paramInt)
  {
    return arrColorsProfiles[getColorIndex(paramInt)];
  }
  
  public static int getProfileTextColorForId(int paramInt)
  {
    return arrColorsProfilesText[getColorIndex(paramInt)];
  }
  
  public void draw(Canvas paramCanvas)
  {
    Rect localRect = getBounds();
    if (localRect == null) {
      return;
    }
    int i = localRect.width();
    paint.setColor(this.color);
    paramCanvas.save();
    paramCanvas.translate(localRect.left, localRect.top);
    paramCanvas.drawCircle(i / 2, i / 2, i / 2, paint);
    int j;
    if ((this.drawBrodcast) && (broadcastDrawable != null))
    {
      j = (i - broadcastDrawable.getIntrinsicWidth()) / 2;
      i = (i - broadcastDrawable.getIntrinsicHeight()) / 2;
      broadcastDrawable.setBounds(j, i, broadcastDrawable.getIntrinsicWidth() + j, broadcastDrawable.getIntrinsicHeight() + i);
      broadcastDrawable.draw(paramCanvas);
    }
    for (;;)
    {
      paramCanvas.restore();
      return;
      if (this.textLayout != null)
      {
        paramCanvas.translate((i - this.textWidth) / 2.0F - this.textLeft, (i - this.textHeight) / 2.0F);
        this.textLayout.draw(paramCanvas);
      }
      else if ((this.drawPhoto) && (photoDrawable != null))
      {
        j = (i - photoDrawable.getIntrinsicWidth()) / 2;
        i = (i - photoDrawable.getIntrinsicHeight()) / 2;
        photoDrawable.setBounds(j, i, photoDrawable.getIntrinsicWidth() + j, photoDrawable.getIntrinsicHeight() + i);
        photoDrawable.draw(paramCanvas);
      }
    }
  }
  
  public int getIntrinsicHeight()
  {
    return 0;
  }
  
  public int getIntrinsicWidth()
  {
    return 0;
  }
  
  public int getOpacity()
  {
    return -2;
  }
  
  public void setAlpha(int paramInt) {}
  
  public void setColor(int paramInt)
  {
    this.color = paramInt;
  }
  
  public void setColorFilter(ColorFilter paramColorFilter) {}
  
  public void setDrawPhoto(boolean paramBoolean)
  {
    if ((paramBoolean) && (photoDrawable == null)) {
      photoDrawable = ApplicationLoader.applicationContext.getResources().getDrawable(2130837893);
    }
    this.drawPhoto = paramBoolean;
  }
  
  public void setInfo(int paramInt, String paramString1, String paramString2, boolean paramBoolean)
  {
    setInfo(paramInt, paramString1, paramString2, paramBoolean, null);
  }
  
  public void setInfo(int paramInt, String paramString1, String paramString2, boolean paramBoolean, String paramString3)
  {
    if (this.isProfile) {
      this.color = arrColorsProfiles[getColorIndex(paramInt)];
    }
    for (;;)
    {
      this.drawBrodcast = paramBoolean;
      String str2;
      String str1;
      if (paramString1 != null)
      {
        str2 = paramString1;
        str1 = paramString2;
        if (paramString1.length() != 0) {}
      }
      else
      {
        str1 = null;
        str2 = paramString2;
      }
      this.stringBuilder.setLength(0);
      if (paramString3 != null)
      {
        this.stringBuilder.append(paramString3);
        label71:
        if (this.stringBuilder.length() > 0) {
          paramString2 = this.stringBuilder.toString().toUpperCase();
        }
      }
      else
      {
        try
        {
          if (this.smallStyle) {}
          for (paramString1 = namePaintSmall;; paramString1 = namePaint)
          {
            this.textLayout = new StaticLayout(paramString2, paramString1, AndroidUtilities.dp(100.0F), Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
            if (this.textLayout.getLineCount() > 0)
            {
              this.textLeft = this.textLayout.getLineLeft(0);
              this.textWidth = this.textLayout.getLineWidth(0);
              this.textHeight = this.textLayout.getLineBottom(0);
            }
            return;
            this.color = arrColors[getColorIndex(paramInt)];
            break;
            if ((str2 != null) && (str2.length() > 0)) {
              this.stringBuilder.append(str2.substring(0, 1));
            }
            if ((str1 != null) && (str1.length() > 0))
            {
              paramString1 = null;
              paramInt = str1.length() - 1;
              for (;;)
              {
                if ((paramInt < 0) || ((paramString1 != null) && (str1.charAt(paramInt) == ' ')))
                {
                  if (Build.VERSION.SDK_INT >= 16) {
                    this.stringBuilder.append("‌");
                  }
                  this.stringBuilder.append(paramString1);
                  break;
                }
                paramString1 = str1.substring(paramInt, paramInt + 1);
                paramInt -= 1;
              }
            }
            if ((str2 == null) || (str2.length() <= 0)) {
              break label71;
            }
            paramInt = str2.length() - 1;
            while (paramInt >= 0)
            {
              if ((str2.charAt(paramInt) == ' ') && (paramInt != str2.length() - 1) && (str2.charAt(paramInt + 1) != ' '))
              {
                if (Build.VERSION.SDK_INT >= 16) {
                  this.stringBuilder.append("‌");
                }
                this.stringBuilder.append(str2.substring(paramInt + 1, paramInt + 2));
                break;
              }
              paramInt -= 1;
            }
          }
          this.textLayout = null;
        }
        catch (Exception paramString1)
        {
          FileLog.e("tmessages", paramString1);
          return;
        }
      }
    }
  }
  
  public void setInfo(TLRPC.Chat paramChat)
  {
    int i;
    String str;
    if (paramChat != null)
    {
      i = paramChat.id;
      str = paramChat.title;
      if (paramChat.id >= 0) {
        break label35;
      }
    }
    label35:
    for (boolean bool = true;; bool = false)
    {
      setInfo(i, str, null, bool, null);
      return;
    }
  }
  
  public void setInfo(TLRPC.User paramUser)
  {
    if (paramUser != null) {
      setInfo(paramUser.id, paramUser.first_name, paramUser.last_name, false, null);
    }
  }
  
  public void setProfile(boolean paramBoolean)
  {
    this.isProfile = paramBoolean;
  }
  
  public void setSmallStyle(boolean paramBoolean)
  {
    this.smallStyle = paramBoolean;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/AvatarDrawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */