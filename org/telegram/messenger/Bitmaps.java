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
      localObject = new BitmapFactory.Options();
      ((BitmapFactory.Options)localObject).inDither = true;
      ((BitmapFactory.Options)localObject).inPreferredConfig = paramConfig;
      ((BitmapFactory.Options)localObject).inPurgeable = true;
      ((BitmapFactory.Options)localObject).inSampleSize = 1;
      ((BitmapFactory.Options)localObject).inMutable = true;
      byte[] arrayOfByte = (byte[])jpegData.get();
      arrayOfByte[76] = ((byte)(paramInt2 >> 8));
      arrayOfByte[77] = ((byte)(paramInt2 & 0xFF));
      arrayOfByte[78] = ((byte)(paramInt1 >> 8));
      arrayOfByte[79] = ((byte)(paramInt1 & 0xFF));
      localObject = BitmapFactory.decodeByteArray(arrayOfByte, 0, arrayOfByte.length, (BitmapFactory.Options)localObject);
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
    checkXYSign(paramInt1, paramInt2);
    checkWidthHeight(paramInt3, paramInt4);
    if (paramInt1 + paramInt3 > paramBitmap.getWidth()) {
      throw new IllegalArgumentException("x + width must be <= bitmap.width()");
    }
    if (paramInt2 + paramInt4 > paramBitmap.getHeight()) {
      throw new IllegalArgumentException("y + height must be <= bitmap.height()");
    }
    if ((!paramBitmap.isMutable()) && (paramInt1 == 0) && (paramInt2 == 0) && (paramInt3 == paramBitmap.getWidth()) && (paramInt4 == paramBitmap.getHeight()) && ((paramMatrix == null) || (paramMatrix.isIdentity()))) {
      return paramBitmap;
    }
    Canvas localCanvas = new Canvas();
    Rect localRect = new Rect(paramInt1, paramInt2, paramInt1 + paramInt3, paramInt2 + paramInt4);
    RectF localRectF = new RectF(0.0F, 0.0F, paramInt3, paramInt4);
    Object localObject1 = Bitmap.Config.ARGB_8888;
    Object localObject2 = paramBitmap.getConfig();
    if (localObject2 != null) {
      switch (localObject2)
      {
      default: 
        localObject1 = Bitmap.Config.ARGB_8888;
      }
    }
    for (;;)
    {
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
      try
      {
        localCanvas.setBitmap(null);
        return paramMatrix;
        localObject1 = Bitmap.Config.RGB_565;
        continue;
        localObject1 = Bitmap.Config.ALPHA_8;
        continue;
        if (!paramMatrix.rectStaysRect()) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          Object localObject3 = new RectF();
          paramMatrix.mapRect((RectF)localObject3, localRectF);
          paramInt2 = Math.round(((RectF)localObject3).width());
          paramInt3 = Math.round(((RectF)localObject3).height());
          if (paramInt1 != 0) {
            localObject1 = Bitmap.Config.ARGB_8888;
          }
          localObject2 = createBitmap(paramInt2, paramInt3, (Bitmap.Config)localObject1);
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
    //   0: ldc 88
    //   2: monitorenter
    //   3: getstatic 239	org/telegram/messenger/Bitmaps:sScaleMatrix	Landroid/graphics/Matrix;
    //   6: astore 7
    //   8: aconst_null
    //   9: putstatic 239	org/telegram/messenger/Bitmaps:sScaleMatrix	Landroid/graphics/Matrix;
    //   12: ldc 88
    //   14: monitorexit
    //   15: aload 7
    //   17: astore 6
    //   19: aload 7
    //   21: ifnonnull +12 -> 33
    //   24: new 133	android/graphics/Matrix
    //   27: dup
    //   28: invokespecial 240	android/graphics/Matrix:<init>	()V
    //   31: astore 6
    //   33: aload_0
    //   34: invokevirtual 120	android/graphics/Bitmap:getWidth	()I
    //   37: istore 4
    //   39: aload_0
    //   40: invokevirtual 125	android/graphics/Bitmap:getHeight	()I
    //   43: istore 5
    //   45: aload 6
    //   47: iload_1
    //   48: i2f
    //   49: iload 4
    //   51: i2f
    //   52: fdiv
    //   53: iload_2
    //   54: i2f
    //   55: iload 5
    //   57: i2f
    //   58: fdiv
    //   59: invokevirtual 243	android/graphics/Matrix:setScale	(FF)V
    //   62: aload_0
    //   63: iconst_0
    //   64: iconst_0
    //   65: iload 4
    //   67: iload 5
    //   69: aload 6
    //   71: iload_3
    //   72: invokestatic 110	org/telegram/messenger/Bitmaps:createBitmap	(Landroid/graphics/Bitmap;IIIILandroid/graphics/Matrix;Z)Landroid/graphics/Bitmap;
    //   75: astore_0
    //   76: ldc 88
    //   78: monitorenter
    //   79: getstatic 239	org/telegram/messenger/Bitmaps:sScaleMatrix	Landroid/graphics/Matrix;
    //   82: ifnonnull +8 -> 90
    //   85: aload 6
    //   87: putstatic 239	org/telegram/messenger/Bitmaps:sScaleMatrix	Landroid/graphics/Matrix;
    //   90: ldc 88
    //   92: monitorexit
    //   93: aload_0
    //   94: areturn
    //   95: astore_0
    //   96: ldc 88
    //   98: monitorexit
    //   99: aload_0
    //   100: athrow
    //   101: astore_0
    //   102: ldc 88
    //   104: monitorexit
    //   105: aload_0
    //   106: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	107	0	paramBitmap	Bitmap
    //   0	107	1	paramInt1	int
    //   0	107	2	paramInt2	int
    //   0	107	3	paramBoolean	boolean
    //   37	29	4	i	int
    //   43	25	5	j	int
    //   17	69	6	localMatrix1	Matrix
    //   6	14	7	localMatrix2	Matrix
    // Exception table:
    //   from	to	target	type
    //   3	15	95	finally
    //   96	99	95	finally
    //   79	90	101	finally
    //   90	93	101	finally
    //   102	105	101	finally
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/Bitmaps.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */