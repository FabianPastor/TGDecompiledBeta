package org.telegram.messenger;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build.VERSION;

public class Bitmaps
{
  private static final ThreadLocal<byte[]> jpegData = new ThreadLocal()
  {
    protected byte[] initialValue()
    {
      return new byte[] { -1, -40, -1, -37, 0, 67, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -64, 0, 17, 8, 0, 0, 0, 0, 3, 1, 34, 0, 2, 17, 0, 3, 17, 0, -1, -60, 0, 31, 0, 0, 1, 5, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, -1, -60, 0, -75, 16, 0, 2, 1, 3, 3, 2, 4, 3, 5, 5, 4, 4, 0, 0, 1, 125, 1, 2, 3, 0, 4, 17, 5, 18, 33, 49, 65, 6, 19, 81, 97, 7, 34, 113, 20, 50, -127, -111, -95, 8, 35, 66, -79, -63, 21, 82, -47, -16, 36, 51, 98, 114, -126, 9, 10, 22, 23, 24, 25, 26, 37, 38, 39, 40, 41, 42, 52, 53, 54, 55, 56, 57, 58, 67, 68, 69, 70, 71, 72, 73, 74, 83, 84, 85, 86, 87, 88, 89, 90, 99, 100, 101, 102, 103, 104, 105, 106, 115, 116, 117, 118, 119, 120, 121, 122, -125, -124, -123, -122, -121, -120, -119, -118, -110, -109, -108, -107, -106, -105, -104, -103, -102, -94, -93, -92, -91, -90, -89, -88, -87, -86, -78, -77, -76, -75, -74, -73, -72, -71, -70, -62, -61, -60, -59, -58, -57, -56, -55, -54, -46, -45, -44, -43, -42, -41, -40, -39, -38, -31, -30, -29, -28, -27, -26, -25, -24, -23, -22, -15, -14, -13, -12, -11, -10, -9, -8, -7, -6, -1, -60, 0, 31, 1, 0, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, -1, -60, 0, -75, 17, 0, 2, 1, 2, 4, 4, 3, 4, 7, 5, 4, 4, 0, 1, 2, 119, 0, 1, 2, 3, 17, 4, 5, 33, 49, 6, 18, 65, 81, 7, 97, 113, 19, 34, 50, -127, 8, 20, 66, -111, -95, -79, -63, 9, 35, 51, 82, -16, 21, 98, 114, -47, 10, 22, 36, 52, -31, 37, -15, 23, 24, 25, 26, 38, 39, 40, 41, 42, 53, 54, 55, 56, 57, 58, 67, 68, 69, 70, 71, 72, 73, 74, 83, 84, 85, 86, 87, 88, 89, 90, 99, 100, 101, 102, 103, 104, 105, 106, 115, 116, 117, 118, 119, 120, 121, 122, -126, -125, -124, -123, -122, -121, -120, -119, -118, -110, -109, -108, -107, -106, -105, -104, -103, -102, -94, -93, -92, -91, -90, -89, -88, -87, -86, -78, -77, -76, -75, -74, -73, -72, -71, -70, -62, -61, -60, -59, -58, -57, -56, -55, -54, -46, -45, -44, -43, -42, -41, -40, -39, -38, -30, -29, -28, -27, -26, -25, -24, -23, -22, -14, -13, -12, -11, -10, -9, -8, -7, -6, -1, -38, 0, 12, 3, 1, 0, 2, 17, 3, 17, 0, 63, 0, -114, -118, 40, -96, 15, -1, -39 };
    }
  };
  private static volatile Matrix sScaleMatrix;
  
  private static void checkWidthHeight(int paramInt1, int paramInt2)
  {
    if (paramInt1 <= 0) {
      throw new IllegalArgumentException("width must be > 0");
    }
    if (paramInt2 <= 0) {
      throw new IllegalArgumentException("height must be > 0");
    }
  }
  
  private static void checkXYSign(int paramInt1, int paramInt2)
  {
    if (paramInt1 < 0) {
      throw new IllegalArgumentException("x must be >= 0");
    }
    if (paramInt2 < 0) {
      throw new IllegalArgumentException("y must be >= 0");
    }
  }
  
  public static Bitmap createBitmap(int paramInt1, int paramInt2, Bitmap.Config paramConfig)
  {
    Object localObject;
    if (Build.VERSION.SDK_INT < 21)
    {
      BitmapFactory.Options localOptions = new BitmapFactory.Options();
      localOptions.inDither = true;
      localOptions.inPreferredConfig = paramConfig;
      localOptions.inPurgeable = true;
      localOptions.inSampleSize = 1;
      localOptions.inMutable = true;
      localObject = (byte[])jpegData.get();
      localObject[76] = ((byte)(byte)(paramInt2 >> 8));
      localObject[77] = ((byte)(byte)(paramInt2 & 0xFF));
      localObject[78] = ((byte)(byte)(paramInt1 >> 8));
      localObject[79] = ((byte)(byte)(paramInt1 & 0xFF));
      localObject = BitmapFactory.decodeByteArray((byte[])localObject, 0, localObject.length, localOptions);
      Utilities.pinBitmap((Bitmap)localObject);
      ((Bitmap)localObject).setHasAlpha(true);
      ((Bitmap)localObject).eraseColor(0);
    }
    for (;;)
    {
      if ((paramConfig == Bitmap.Config.ARGB_8888) || (paramConfig == Bitmap.Config.ARGB_4444)) {
        ((Bitmap)localObject).eraseColor(0);
      }
      return (Bitmap)localObject;
      localObject = Bitmap.createBitmap(paramInt1, paramInt2, paramConfig);
    }
  }
  
  public static Bitmap createBitmap(Bitmap paramBitmap, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    return createBitmap(paramBitmap, paramInt1, paramInt2, paramInt3, paramInt4, null, false);
  }
  
  public static Bitmap createBitmap(Bitmap paramBitmap, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Matrix paramMatrix, boolean paramBoolean)
  {
    Object localObject1;
    if (Build.VERSION.SDK_INT >= 21) {
      localObject1 = Bitmap.createBitmap(paramBitmap, paramInt1, paramInt2, paramInt3, paramInt4, paramMatrix, paramBoolean);
    }
    for (;;)
    {
      return (Bitmap)localObject1;
      checkXYSign(paramInt1, paramInt2);
      checkWidthHeight(paramInt3, paramInt4);
      if (paramInt1 + paramInt3 > paramBitmap.getWidth()) {
        throw new IllegalArgumentException("x + width must be <= bitmap.width()");
      }
      if (paramInt2 + paramInt4 > paramBitmap.getHeight()) {
        throw new IllegalArgumentException("y + height must be <= bitmap.height()");
      }
      if ((!paramBitmap.isMutable()) && (paramInt1 == 0) && (paramInt2 == 0) && (paramInt3 == paramBitmap.getWidth()) && (paramInt4 == paramBitmap.getHeight()))
      {
        localObject1 = paramBitmap;
        if (paramMatrix == null) {
          continue;
        }
        localObject1 = paramBitmap;
        if (paramMatrix.isIdentity()) {
          continue;
        }
      }
      Canvas localCanvas = new Canvas();
      Rect localRect = new Rect(paramInt1, paramInt2, paramInt1 + paramInt3, paramInt2 + paramInt4);
      RectF localRectF = new RectF(0.0F, 0.0F, paramInt3, paramInt4);
      localObject1 = Bitmap.Config.ARGB_8888;
      Object localObject2 = paramBitmap.getConfig();
      if (localObject2 != null) {}
      switch (localObject2)
      {
      default: 
        localObject1 = Bitmap.Config.ARGB_8888;
        label225:
        if ((paramMatrix == null) || (paramMatrix.isIdentity()))
        {
          paramMatrix = createBitmap(paramInt3, paramInt4, (Bitmap.Config)localObject1);
          localObject1 = null;
          paramMatrix.setDensity(paramBitmap.getDensity());
          paramMatrix.setHasAlpha(paramBitmap.hasAlpha());
          if (Build.VERSION.SDK_INT >= 19) {
            paramMatrix.setPremultiplied(paramBitmap.isPremultiplied());
          }
          localCanvas.setBitmap(paramMatrix);
          localCanvas.drawBitmap(paramBitmap, localRect, localRectF, (Paint)localObject1);
        }
        break;
      }
      try
      {
        localCanvas.setBitmap(null);
        localObject1 = paramMatrix;
        continue;
        localObject1 = Bitmap.Config.ARGB_8888;
        break label225;
        localObject1 = Bitmap.Config.ALPHA_8;
        break label225;
        if (!paramMatrix.rectStaysRect()) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          Object localObject3 = new RectF();
          paramMatrix.mapRect((RectF)localObject3, localRectF);
          paramInt3 = Math.round(((RectF)localObject3).width());
          paramInt2 = Math.round(((RectF)localObject3).height());
          if (paramInt1 != 0) {
            localObject1 = Bitmap.Config.ARGB_8888;
          }
          localObject2 = createBitmap(paramInt3, paramInt2, (Bitmap.Config)localObject1);
          localCanvas.translate(-((RectF)localObject3).left, -((RectF)localObject3).top);
          localCanvas.concat(paramMatrix);
          localObject3 = new Paint();
          ((Paint)localObject3).setFilterBitmap(paramBoolean);
          paramMatrix = (Matrix)localObject2;
          localObject1 = localObject3;
          if (paramInt1 == 0) {
            break;
          }
          ((Paint)localObject3).setAntiAlias(true);
          paramMatrix = (Matrix)localObject2;
          localObject1 = localObject3;
          break;
        }
      }
      catch (Exception paramBitmap)
      {
        for (;;) {}
      }
    }
  }
  
  /* Error */
  public static Bitmap createScaledBitmap(Bitmap paramBitmap, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    // Byte code:
    //   0: getstatic 46	android/os/Build$VERSION:SDK_INT	I
    //   3: bipush 21
    //   5: if_icmplt +13 -> 18
    //   8: aload_0
    //   9: iload_1
    //   10: iload_2
    //   11: iload_3
    //   12: invokestatic 237	android/graphics/Bitmap:createScaledBitmap	(Landroid/graphics/Bitmap;IIZ)Landroid/graphics/Bitmap;
    //   15: astore_0
    //   16: aload_0
    //   17: areturn
    //   18: ldc 88
    //   20: monitorenter
    //   21: getstatic 239	org/telegram/messenger/Bitmaps:sScaleMatrix	Landroid/graphics/Matrix;
    //   24: astore 4
    //   26: aconst_null
    //   27: putstatic 239	org/telegram/messenger/Bitmaps:sScaleMatrix	Landroid/graphics/Matrix;
    //   30: ldc 88
    //   32: monitorexit
    //   33: aload 4
    //   35: astore 5
    //   37: aload 4
    //   39: ifnonnull +12 -> 51
    //   42: new 134	android/graphics/Matrix
    //   45: dup
    //   46: invokespecial 240	android/graphics/Matrix:<init>	()V
    //   49: astore 5
    //   51: aload_0
    //   52: invokevirtual 121	android/graphics/Bitmap:getWidth	()I
    //   55: istore 6
    //   57: aload_0
    //   58: invokevirtual 126	android/graphics/Bitmap:getHeight	()I
    //   61: istore 7
    //   63: aload 5
    //   65: iload_1
    //   66: i2f
    //   67: iload 6
    //   69: i2f
    //   70: fdiv
    //   71: iload_2
    //   72: i2f
    //   73: iload 7
    //   75: i2f
    //   76: fdiv
    //   77: invokevirtual 243	android/graphics/Matrix:setScale	(FF)V
    //   80: aload_0
    //   81: iconst_0
    //   82: iconst_0
    //   83: iload 6
    //   85: iload 7
    //   87: aload 5
    //   89: iload_3
    //   90: invokestatic 110	org/telegram/messenger/Bitmaps:createBitmap	(Landroid/graphics/Bitmap;IIIILandroid/graphics/Matrix;Z)Landroid/graphics/Bitmap;
    //   93: astore_0
    //   94: ldc 88
    //   96: monitorenter
    //   97: getstatic 239	org/telegram/messenger/Bitmaps:sScaleMatrix	Landroid/graphics/Matrix;
    //   100: ifnonnull +8 -> 108
    //   103: aload 5
    //   105: putstatic 239	org/telegram/messenger/Bitmaps:sScaleMatrix	Landroid/graphics/Matrix;
    //   108: ldc 88
    //   110: monitorexit
    //   111: goto -95 -> 16
    //   114: astore_0
    //   115: ldc 88
    //   117: monitorexit
    //   118: aload_0
    //   119: athrow
    //   120: astore_0
    //   121: ldc 88
    //   123: monitorexit
    //   124: aload_0
    //   125: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	126	0	paramBitmap	Bitmap
    //   0	126	1	paramInt1	int
    //   0	126	2	paramInt2	int
    //   0	126	3	paramBoolean	boolean
    //   24	14	4	localMatrix1	Matrix
    //   35	69	5	localMatrix2	Matrix
    //   55	29	6	i	int
    //   61	25	7	j	int
    // Exception table:
    //   from	to	target	type
    //   97	108	114	finally
    //   108	111	114	finally
    //   115	118	114	finally
    //   21	33	120	finally
    //   121	124	120	finally
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/Bitmaps.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */