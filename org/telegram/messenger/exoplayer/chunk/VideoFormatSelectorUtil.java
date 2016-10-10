package org.telegram.messenger.exoplayer.chunk;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Display.Mode;
import android.view.WindowManager;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.exoplayer.MediaCodecUtil;
import org.telegram.messenger.exoplayer.MediaCodecUtil.DecoderQueryException;
import org.telegram.messenger.exoplayer.util.MimeTypes;
import org.telegram.messenger.exoplayer.util.Util;

public final class VideoFormatSelectorUtil
{
  private static final float FRACTION_TO_CONSIDER_FULLSCREEN = 0.98F;
  private static final String TAG = "VideoFormatSelectorUtil";
  
  private static Point getDisplaySize(Context paramContext)
  {
    if (Util.SDK_INT < 25)
    {
      if (("Sony".equals(Util.MANUFACTURER)) && (Util.MODEL != null) && (Util.MODEL.startsWith("BRAVIA")) && (paramContext.getPackageManager().hasSystemFeature("com.sony.dtv.hardware.panel.qfhd"))) {
        return new Point(3840, 2160);
      }
      if (("NVIDIA".equals(Util.MANUFACTURER)) && (Util.MODEL != null) && (Util.MODEL.contains("SHIELD"))) {
        localObject1 = null;
      }
    }
    try
    {
      localObject2 = Class.forName("android.os.SystemProperties");
      localObject2 = (String)((Class)localObject2).getMethod("get", new Class[] { String.class }).invoke(localObject2, new Object[] { "sys.display-size" });
      localObject1 = localObject2;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        Object localObject2;
        Log.e("VideoFormatSelectorUtil", "Failed to read sys.display-size", localException);
      }
      if (Util.SDK_INT < 17) {
        break label295;
      }
      getDisplaySizeV17(paramContext, (Point)localObject1);
      return (Point)localObject1;
      if (Util.SDK_INT < 16) {
        break label310;
      }
      getDisplaySizeV16(paramContext, (Point)localObject1);
      return (Point)localObject1;
      getDisplaySizeV9(paramContext, (Point)localObject1);
    }
    if (!TextUtils.isEmpty((CharSequence)localObject1)) {
      try
      {
        localObject2 = ((String)localObject1).trim().split("x");
        if (localObject2.length == 2)
        {
          int i = Integer.parseInt(localObject2[0]);
          int j = Integer.parseInt(localObject2[1]);
          if ((i > 0) && (j > 0))
          {
            localObject2 = new Point(i, j);
            return (Point)localObject2;
          }
        }
      }
      catch (NumberFormatException localNumberFormatException)
      {
        Log.e("VideoFormatSelectorUtil", "Invalid sys.display-size: " + (String)localObject1);
      }
    }
    paramContext = ((WindowManager)paramContext.getSystemService("window")).getDefaultDisplay();
    Object localObject1 = new Point();
    if (Util.SDK_INT >= 23)
    {
      getDisplaySizeV23(paramContext, (Point)localObject1);
      return (Point)localObject1;
    }
    label295:
    label310:
    return (Point)localObject1;
  }
  
  @TargetApi(16)
  private static void getDisplaySizeV16(Display paramDisplay, Point paramPoint)
  {
    paramDisplay.getSize(paramPoint);
  }
  
  @TargetApi(17)
  private static void getDisplaySizeV17(Display paramDisplay, Point paramPoint)
  {
    paramDisplay.getRealSize(paramPoint);
  }
  
  @TargetApi(23)
  private static void getDisplaySizeV23(Display paramDisplay, Point paramPoint)
  {
    paramDisplay = paramDisplay.getMode();
    paramPoint.x = paramDisplay.getPhysicalWidth();
    paramPoint.y = paramDisplay.getPhysicalHeight();
  }
  
  private static void getDisplaySizeV9(Display paramDisplay, Point paramPoint)
  {
    paramPoint.x = paramDisplay.getWidth();
    paramPoint.y = paramDisplay.getHeight();
  }
  
  private static Point getMaxVideoSizeInViewport(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int m = 1;
    int k = paramInt1;
    int j = paramInt2;
    int i;
    if (paramBoolean)
    {
      if (paramInt3 <= paramInt4) {
        break label77;
      }
      i = 1;
      if (paramInt1 <= paramInt2) {
        break label83;
      }
    }
    for (;;)
    {
      k = paramInt1;
      j = paramInt2;
      if (i != m)
      {
        j = paramInt1;
        k = paramInt2;
      }
      if (paramInt3 * j < paramInt4 * k) {
        break label89;
      }
      return new Point(k, Util.ceilDivide(k * paramInt4, paramInt3));
      label77:
      i = 0;
      break;
      label83:
      m = 0;
    }
    label89:
    return new Point(Util.ceilDivide(j * paramInt3, paramInt4), j);
  }
  
  private static boolean isFormatPlayable(Format paramFormat, String[] paramArrayOfString, boolean paramBoolean1, boolean paramBoolean2)
    throws MediaCodecUtil.DecoderQueryException
  {
    if ((paramArrayOfString != null) && (!Util.contains(paramArrayOfString, paramFormat.mimeType))) {}
    do
    {
      do
      {
        return false;
      } while ((paramBoolean1) && ((paramFormat.width >= 1280) || (paramFormat.height >= 720)));
      if ((paramFormat.width <= 0) || (paramFormat.height <= 0)) {
        break;
      }
      if (Util.SDK_INT >= 21)
      {
        String str = MimeTypes.getVideoMediaMimeType(paramFormat.codecs);
        paramArrayOfString = str;
        if ("video/x-unknown".equals(str)) {
          paramArrayOfString = "video/avc";
        }
        if (paramFormat.frameRate > 0.0F) {
          return MediaCodecUtil.isSizeAndRateSupportedV21(paramArrayOfString, paramBoolean2, paramFormat.width, paramFormat.height, paramFormat.frameRate);
        }
        return MediaCodecUtil.isSizeSupportedV21(paramArrayOfString, paramBoolean2, paramFormat.width, paramFormat.height);
      }
    } while (paramFormat.width * paramFormat.height > MediaCodecUtil.maxH264DecodableFrameSize());
    return true;
  }
  
  public static int[] selectVideoFormats(List<? extends FormatWrapper> paramList, String[] paramArrayOfString, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, int paramInt1, int paramInt2)
    throws MediaCodecUtil.DecoderQueryException
  {
    int i = Integer.MAX_VALUE;
    ArrayList localArrayList = new ArrayList();
    int n = paramList.size();
    int j = 0;
    while (j < n)
    {
      Format localFormat = ((FormatWrapper)paramList.get(j)).getFormat();
      int k = i;
      if (isFormatPlayable(localFormat, paramArrayOfString, paramBoolean1, paramBoolean3))
      {
        localArrayList.add(Integer.valueOf(j));
        k = i;
        if (localFormat.width > 0)
        {
          k = i;
          if (localFormat.height > 0)
          {
            k = i;
            if (paramInt1 > 0)
            {
              k = i;
              if (paramInt2 > 0)
              {
                Point localPoint = getMaxVideoSizeInViewport(paramBoolean2, paramInt1, paramInt2, localFormat.width, localFormat.height);
                int m = localFormat.width * localFormat.height;
                k = i;
                if (localFormat.width >= (int)(localPoint.x * 0.98F))
                {
                  k = i;
                  if (localFormat.height >= (int)(localPoint.y * 0.98F))
                  {
                    k = i;
                    if (m < i) {
                      k = m;
                    }
                  }
                }
              }
            }
          }
        }
      }
      j += 1;
      i = k;
    }
    if (i != Integer.MAX_VALUE)
    {
      paramInt1 = localArrayList.size() - 1;
      while (paramInt1 >= 0)
      {
        paramArrayOfString = ((FormatWrapper)paramList.get(((Integer)localArrayList.get(paramInt1)).intValue())).getFormat();
        if ((paramArrayOfString.width > 0) && (paramArrayOfString.height > 0) && (paramArrayOfString.width * paramArrayOfString.height > i)) {
          localArrayList.remove(paramInt1);
        }
        paramInt1 -= 1;
      }
    }
    return Util.toArray(localArrayList);
  }
  
  public static int[] selectVideoFormatsForDefaultDisplay(Context paramContext, List<? extends FormatWrapper> paramList, String[] paramArrayOfString, boolean paramBoolean)
    throws MediaCodecUtil.DecoderQueryException
  {
    paramContext = getDisplaySize(paramContext);
    return selectVideoFormats(paramList, paramArrayOfString, paramBoolean, true, false, paramContext.x, paramContext.y);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/chunk/VideoFormatSelectorUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */