package net.hockeyapp.android.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class ImageUtils
{
  private static int calculateInSampleSize(BitmapFactory.Options paramOptions, int paramInt1, int paramInt2)
  {
    int i = paramOptions.outHeight;
    int j = paramOptions.outWidth;
    int k = 1;
    int m = 1;
    if ((i > paramInt2) || (j > paramInt1))
    {
      i /= 2;
      j /= 2;
      for (;;)
      {
        k = m;
        if (i / m <= paramInt2) {
          break;
        }
        k = m;
        if (j / m <= paramInt1) {
          break;
        }
        m *= 2;
      }
    }
    return k;
  }
  
  public static Bitmap decodeSampledBitmap(Context paramContext, Uri paramUri, int paramInt1, int paramInt2)
    throws IOException
  {
    InputStream localInputStream1 = null;
    Object localObject1 = null;
    Object localObject2 = localObject1;
    InputStream localInputStream2 = localInputStream1;
    try
    {
      BitmapFactory.Options localOptions = new android/graphics/BitmapFactory$Options;
      localObject2 = localObject1;
      localInputStream2 = localInputStream1;
      localOptions.<init>();
      localObject2 = localObject1;
      localInputStream2 = localInputStream1;
      localOptions.inJustDecodeBounds = true;
      localObject2 = localObject1;
      localInputStream2 = localInputStream1;
      localInputStream1 = paramContext.getContentResolver().openInputStream(paramUri);
      localObject2 = localObject1;
      localInputStream2 = localInputStream1;
      BitmapFactory.decodeStream(localInputStream1, null, localOptions);
      localObject2 = localObject1;
      localInputStream2 = localInputStream1;
      localOptions.inSampleSize = calculateInSampleSize(localOptions, paramInt1, paramInt2);
      localObject2 = localObject1;
      localInputStream2 = localInputStream1;
      localOptions.inJustDecodeBounds = false;
      localObject2 = localObject1;
      localInputStream2 = localInputStream1;
      paramContext = paramContext.getContentResolver().openInputStream(paramUri);
      localObject2 = paramContext;
      localInputStream2 = localInputStream1;
      paramUri = BitmapFactory.decodeStream(paramContext, null, localOptions);
      if (localInputStream1 != null) {
        localInputStream1.close();
      }
      if (paramContext != null) {
        paramContext.close();
      }
      return paramUri;
    }
    finally
    {
      if (localInputStream2 != null) {
        localInputStream2.close();
      }
      if (localObject2 != null) {
        ((InputStream)localObject2).close();
      }
    }
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
  {
    Object localObject = null;
    Context localContext = null;
    for (;;)
    {
      try
      {
        paramContext = paramContext.getContentResolver().openInputStream(paramUri);
        localContext = paramContext;
        localObject = paramContext;
        i = determineOrientation(paramContext);
        j = i;
      }
      catch (IOException paramContext)
      {
        localObject = localContext;
        HockeyLog.error("Unable to determine necessary screen orientation.", paramContext);
        int i = 1;
        int j = i;
        if (localContext == null) {
          continue;
        }
        try
        {
          localContext.close();
          j = i;
        }
        catch (IOException paramContext)
        {
          HockeyLog.error("Unable to close input stream.", paramContext);
          j = i;
        }
        continue;
      }
      finally
      {
        if (localObject == null) {
          break label110;
        }
      }
      try
      {
        paramContext.close();
        j = i;
      }
      catch (IOException paramContext)
      {
        HockeyLog.error("Unable to close input stream.", paramContext);
        j = i;
      }
    }
    return j;
    try
    {
      ((InputStream)localObject).close();
      label110:
      throw paramContext;
    }
    catch (IOException paramUri)
    {
      for (;;)
      {
        HockeyLog.error("Unable to close input stream.", paramUri);
      }
    }
  }
  
  /* Error */
  public static int determineOrientation(File paramFile)
    throws IOException
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_1
    //   2: new 86	java/io/FileInputStream
    //   5: astore_2
    //   6: aload_2
    //   7: aload_0
    //   8: invokespecial 89	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   11: aload_2
    //   12: invokestatic 73	net/hockeyapp/android/utils/ImageUtils:determineOrientation	(Ljava/io/InputStream;)I
    //   15: istore_3
    //   16: aload_2
    //   17: ifnull +7 -> 24
    //   20: aload_2
    //   21: invokevirtual 56	java/io/InputStream:close	()V
    //   24: iload_3
    //   25: ireturn
    //   26: astore_0
    //   27: aload_1
    //   28: ifnull +7 -> 35
    //   31: aload_1
    //   32: invokevirtual 56	java/io/InputStream:close	()V
    //   35: aload_0
    //   36: athrow
    //   37: astore_0
    //   38: aload_2
    //   39: astore_1
    //   40: goto -13 -> 27
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	43	0	paramFile	File
    //   1	39	1	localObject	Object
    //   5	34	2	localFileInputStream	java.io.FileInputStream
    //   15	10	3	i	int
    // Exception table:
    //   from	to	target	type
    //   2	11	26	finally
    //   11	16	37	finally
  }
  
  public static int determineOrientation(InputStream paramInputStream)
  {
    int i = 1;
    BitmapFactory.Options localOptions = new BitmapFactory.Options();
    localOptions.inJustDecodeBounds = true;
    BitmapFactory.decodeStream(paramInputStream, null, localOptions);
    int j = i;
    if (localOptions.outWidth != -1)
    {
      if (localOptions.outHeight != -1) {
        break label44;
      }
      j = i;
    }
    for (;;)
    {
      return j;
      label44:
      j = i;
      if (localOptions.outWidth / localOptions.outHeight > 1.0F) {
        j = 0;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/utils/ImageUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */