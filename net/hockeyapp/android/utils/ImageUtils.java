package net.hockeyapp.android.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageUtils
{
  public static final int ORIENTATION_LANDSCAPE = 1;
  public static final int ORIENTATION_PORTRAIT = 0;
  
  private static int calculateInSampleSize(BitmapFactory.Options paramOptions, int paramInt1, int paramInt2)
  {
    int k = paramOptions.outHeight;
    int m = paramOptions.outWidth;
    int j = 1;
    int i = 1;
    if ((k > paramInt2) || (m > paramInt1))
    {
      k /= 2;
      m /= 2;
      for (;;)
      {
        j = i;
        if (k / i <= paramInt2) {
          break;
        }
        j = i;
        if (m / i <= paramInt1) {
          break;
        }
        i *= 2;
      }
    }
    return j;
  }
  
  public static Bitmap decodeSampledBitmap(Context paramContext, Uri paramUri, int paramInt1, int paramInt2)
    throws IOException
  {
    BitmapFactory.Options localOptions = new BitmapFactory.Options();
    localOptions.inJustDecodeBounds = true;
    BitmapFactory.decodeStream(paramContext.getContentResolver().openInputStream(paramUri), null, localOptions);
    localOptions.inSampleSize = calculateInSampleSize(localOptions, paramInt1, paramInt2);
    localOptions.inJustDecodeBounds = false;
    return BitmapFactory.decodeStream(paramContext.getContentResolver().openInputStream(paramUri), null, localOptions);
  }
  
  public static Bitmap decodeSampledBitmap(File paramFile, int paramInt1, int paramInt2)
    throws IOException
  {
    BitmapFactory.Options localOptions = new BitmapFactory.Options();
    localOptions.inJustDecodeBounds = true;
    BitmapFactory.decodeFile(paramFile.getAbsolutePath(), localOptions);
    localOptions.inSampleSize = calculateInSampleSize(localOptions, paramInt1, paramInt2);
    localOptions.inJustDecodeBounds = false;
    return BitmapFactory.decodeFile(paramFile.getAbsolutePath(), localOptions);
  }
  
  public static int determineOrientation(Context paramContext, Uri paramUri)
    throws IOException
  {
    Context localContext = null;
    try
    {
      paramContext = paramContext.getContentResolver().openInputStream(paramUri);
      localContext = paramContext;
      int i = determineOrientation(paramContext);
      return i;
    }
    finally
    {
      if (localContext != null) {
        localContext.close();
      }
    }
  }
  
  public static int determineOrientation(File paramFile)
    throws IOException
  {
    Object localObject3 = null;
    try
    {
      paramFile = new FileInputStream(paramFile);
      int i;
      if (paramFile == null) {
        break label37;
      }
    }
    finally
    {
      try
      {
        i = determineOrientation(paramFile);
        if (paramFile != null) {
          paramFile.close();
        }
        return i;
      }
      finally {}
      localObject1 = finally;
      paramFile = (File)localObject3;
    }
    paramFile.close();
    label37:
    throw ((Throwable)localObject1);
  }
  
  public static int determineOrientation(InputStream paramInputStream)
  {
    int i = 1;
    BitmapFactory.Options localOptions = new BitmapFactory.Options();
    localOptions.inJustDecodeBounds = true;
    BitmapFactory.decodeStream(paramInputStream, null, localOptions);
    if ((localOptions.outWidth == -1) || (localOptions.outHeight == -1)) {
      i = 0;
    }
    while (localOptions.outWidth / localOptions.outHeight > 1.0F) {
      return i;
    }
    return 0;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/utils/ImageUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */