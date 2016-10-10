package org.telegram.messenger;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.Spannable;
import android.text.Spannable.Factory;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Locale;

public class Emoji
{
  private static int bigImgSize = 0;
  private static final int[][] cols;
  private static int drawImgSize = 0;
  private static Bitmap[][] emojiBmp;
  private static boolean inited = false;
  private static boolean[][] loadingEmoji;
  private static Paint placeholderPaint;
  private static HashMap<CharSequence, DrawableInfo> rects = new HashMap();
  private static final int splitCount = 4;
  
  static
  {
    inited = false;
    emojiBmp = (Bitmap[][])Array.newInstance(Bitmap.class, new int[] { 5, 4 });
    loadingEmoji = (boolean[][])Array.newInstance(Boolean.TYPE, new int[] { 5, 4 });
    Object localObject = { 9, 9, 9, 9 };
    int[] arrayOfInt = { 8, 8, 8, 7 };
    cols = new int[][] { { 12, 12, 12, 12 }, { 6, 6, 6, 6 }, localObject, { 9, 9, 9, 9 }, arrayOfInt };
    int j = 2;
    int i;
    float f;
    label236:
    int k;
    if (AndroidUtilities.density <= 1.0F)
    {
      i = 32;
      j = 1;
      drawImgSize = AndroidUtilities.dp(20.0F);
      if (!AndroidUtilities.isTablet()) {
        break label449;
      }
      f = 40.0F;
      bigImgSize = AndroidUtilities.dp(f);
      k = 0;
    }
    for (;;)
    {
      if (k >= EmojiData.data.length) {
        break label462;
      }
      int n = (int)Math.ceil(EmojiData.data[k].length / 4.0F);
      int m = 0;
      for (;;)
      {
        if (m < EmojiData.data[k].length)
        {
          int i1 = m / n;
          int i3 = m - i1 * n;
          int i2 = i3 % cols[k][i1];
          i3 /= cols[k][i1];
          localObject = new Rect(i2 * i + i2 * j, i3 * i + i3 * j, (i2 + 1) * i + i2 * j, (i3 + 1) * i + i3 * j);
          rects.put(EmojiData.data[k][m], new DrawableInfo((Rect)localObject, (byte)k, (byte)i1));
          m += 1;
          continue;
          if (AndroidUtilities.density <= 1.5F)
          {
            i = 64;
            break;
          }
          if (AndroidUtilities.density <= 2.0F)
          {
            i = 64;
            break;
          }
          i = 64;
          break;
          label449:
          f = 32.0F;
          break label236;
        }
      }
      k += 1;
    }
    label462:
    placeholderPaint = new Paint();
    placeholderPaint.setColor(0);
  }
  
  public static String fixEmoji(String paramString)
  {
    int n = paramString.length();
    int k = 0;
    String str = paramString;
    int i;
    int j;
    int m;
    if (k < n)
    {
      i = str.charAt(k);
      if ((i >= 55356) && (i <= 55358)) {
        if ((i == 55356) && (k < n - 1))
        {
          j = str.charAt(k + 1);
          if ((j == 56879) || (j == 56324) || (j == 56858) || (j == 56703))
          {
            paramString = str.substring(0, k + 2) + "️" + str.substring(k + 2);
            m = n + 1;
            j = k + 2;
          }
        }
      }
    }
    for (;;)
    {
      k = j + 1;
      n = m;
      str = paramString;
      break;
      j = k + 1;
      m = n;
      paramString = str;
      continue;
      j = k + 1;
      m = n;
      paramString = str;
      continue;
      if (i == 8419) {
        return str;
      }
      j = k;
      m = n;
      paramString = str;
      if (i >= 8252)
      {
        j = k;
        m = n;
        paramString = str;
        if (i <= 12953)
        {
          j = k;
          m = n;
          paramString = str;
          if (EmojiData.emojiToFE0FMap.containsKey(Character.valueOf(i)))
          {
            paramString = str.substring(0, k + 1) + "️" + str.substring(k + 1);
            m = n + 1;
            j = k + 1;
          }
        }
      }
    }
  }
  
  public static Drawable getEmojiBigDrawable(String paramString)
  {
    paramString = getEmojiDrawable(paramString);
    if (paramString == null) {
      return null;
    }
    paramString.setBounds(0, 0, bigImgSize, bigImgSize);
    EmojiDrawable.access$102(paramString, true);
    return paramString;
  }
  
  public static EmojiDrawable getEmojiDrawable(CharSequence paramCharSequence)
  {
    DrawableInfo localDrawableInfo = (DrawableInfo)rects.get(paramCharSequence);
    if (localDrawableInfo == null)
    {
      FileLog.e("tmessages", "No drawable for emoji " + paramCharSequence);
      return null;
    }
    paramCharSequence = new EmojiDrawable(localDrawableInfo);
    paramCharSequence.setBounds(0, 0, drawImgSize, drawImgSize);
    return paramCharSequence;
  }
  
  private static boolean inArray(char paramChar, char[] paramArrayOfChar)
  {
    boolean bool2 = false;
    int j = paramArrayOfChar.length;
    int i = 0;
    for (;;)
    {
      boolean bool1 = bool2;
      if (i < j)
      {
        if (paramArrayOfChar[i] == paramChar) {
          bool1 = true;
        }
      }
      else {
        return bool1;
      }
      i += 1;
    }
  }
  
  public static void invalidateAll(View paramView)
  {
    if ((paramView instanceof ViewGroup))
    {
      paramView = (ViewGroup)paramView;
      int i = 0;
      while (i < paramView.getChildCount())
      {
        invalidateAll(paramView.getChildAt(i));
        i += 1;
      }
    }
    if ((paramView instanceof TextView)) {
      paramView.invalidate();
    }
  }
  
  private static void loadEmoji(int paramInt1, final int paramInt2)
  {
    int i = 1;
    try
    {
      float f = AndroidUtilities.density;
      if (f <= 1.0F) {
        i = 2;
      }
      for (;;)
      {
        j = 4;
        for (;;)
        {
          if (j >= 7) {
            break label183;
          }
          try
          {
            localObject1 = String.format(Locale.US, "v%d_emoji%.01fx_%d.jpg", new Object[] { Integer.valueOf(j), Float.valueOf(2.0F), Integer.valueOf(paramInt1) });
            localObject1 = ApplicationLoader.applicationContext.getFileStreamPath((String)localObject1);
            if (((File)localObject1).exists()) {
              ((File)localObject1).delete();
            }
            localObject1 = String.format(Locale.US, "v%d_emoji%.01fx_a_%d.jpg", new Object[] { Integer.valueOf(j), Float.valueOf(2.0F), Integer.valueOf(paramInt1) });
            localObject1 = ApplicationLoader.applicationContext.getFileStreamPath((String)localObject1);
            if (((File)localObject1).exists()) {
              ((File)localObject1).delete();
            }
            j += 1;
          }
          catch (Exception localException)
          {
            Object localObject1;
            FileLog.e("tmessages", localException);
            Bitmap localBitmap = null;
            final Object localObject2 = localBitmap;
            try
            {
              InputStream localInputStream = ApplicationLoader.applicationContext.getAssets().open("emoji/" + String.format(Locale.US, "v10_emoji%.01fx_%d_%d.png", new Object[] { Float.valueOf(2.0F), Integer.valueOf(paramInt1), Integer.valueOf(paramInt2) }));
              localObject2 = localBitmap;
              BitmapFactory.Options localOptions = new BitmapFactory.Options();
              localObject2 = localBitmap;
              localOptions.inJustDecodeBounds = false;
              localObject2 = localBitmap;
              localOptions.inSampleSize = i;
              localObject2 = localBitmap;
              localBitmap = BitmapFactory.decodeStream(localInputStream, null, localOptions);
              localObject2 = localBitmap;
              localInputStream.close();
              localObject2 = localBitmap;
            }
            catch (Throwable localThrowable2)
            {
              for (;;)
              {
                FileLog.e("tmessages", localThrowable2);
              }
            }
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                Emoji.emojiBmp[this.val$page][paramInt2] = localObject2;
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.emojiDidLoaded, new Object[0]);
              }
            });
            return;
          }
        }
        if (AndroidUtilities.density > 1.5F)
        {
          f = AndroidUtilities.density;
          if (f <= 2.0F) {}
        }
      }
      label183:
      int j = 8;
      while (j < 10)
      {
        localObject1 = String.format(Locale.US, "v%d_emoji%.01fx_%d.png", new Object[] { Integer.valueOf(j), Float.valueOf(2.0F), Integer.valueOf(paramInt1) });
        localObject1 = ApplicationLoader.applicationContext.getFileStreamPath((String)localObject1);
        if (((File)localObject1).exists()) {
          ((File)localObject1).delete();
        }
        j += 1;
      }
      return;
    }
    catch (Throwable localThrowable1)
    {
      FileLog.e("tmessages", "Error loading emoji", localThrowable1);
    }
  }
  
  public static CharSequence replaceEmoji(CharSequence paramCharSequence, Paint.FontMetricsInt paramFontMetricsInt, int paramInt, boolean paramBoolean)
  {
    if ((paramCharSequence == null) || (paramCharSequence.length() == 0)) {
      return paramCharSequence;
    }
    Spannable localSpannable;
    long l2;
    int i3;
    int i2;
    int i4;
    int i6;
    StringBuilder localStringBuilder;
    int i9;
    int i5;
    int i1;
    if ((!paramBoolean) && ((paramCharSequence instanceof Spannable)))
    {
      localSpannable = (Spannable)paramCharSequence;
      l2 = 0L;
      i3 = 0;
      i2 = -1;
      i4 = 0;
      i6 = 0;
      localStringBuilder = new StringBuilder(16);
      i9 = paramCharSequence.length();
      i5 = 0;
      i1 = 0;
      if (i1 >= i9) {
        break label931;
      }
    }
    label106:
    int i7;
    try
    {
      i = paramCharSequence.charAt(i1);
      if (i < 55356) {
        break label968;
      }
      if (i <= 55358) {
        break label1015;
      }
    }
    catch (Exception paramFontMetricsInt)
    {
      int i;
      FileLog.e("tmessages", paramFontMetricsInt);
      return paramCharSequence;
    }
    localStringBuilder.append(i);
    int k = i4 + 1;
    long l1 = l2 << 16 | i;
    int m = i5;
    break label1032;
    label138:
    int j;
    if (i5 < 3)
    {
      i7 = i2;
      m = i1;
      i4 = k;
      if (i1 + 1 < i9)
      {
        j = paramCharSequence.charAt(i1 + 1);
        if (i5 != 1) {
          break label1114;
        }
        i7 = i2;
        m = i1;
        i4 = k;
        if (j == 8205)
        {
          i7 = i2;
          m = i1;
          i4 = k;
          if (localStringBuilder.length() > 0)
          {
            localStringBuilder.append(j);
            m = i1 + 1;
            i4 = k + 1;
            i7 = 0;
          }
        }
      }
    }
    for (;;)
    {
      i5 += 1;
      i2 = i7;
      i1 = m;
      k = i4;
      break label138;
      localSpannable = Spannable.Factory.getInstance().newSpannable(paramCharSequence.toString());
      break;
      label284:
      int n;
      char c;
      if ((localStringBuilder.length() > 0) && ((j == 9792) || (j == 9794)))
      {
        localStringBuilder.append(j);
        k = i4 + 1;
        l1 = 0L;
        m = 1;
        n = i2;
      }
      else if ((l2 > 0L) && ((0xF000 & j) == 53248))
      {
        localStringBuilder.append(j);
        k = i4 + 1;
        l1 = 0L;
        m = 1;
        n = i2;
      }
      else
      {
        if (j != 8419) {
          break label1079;
        }
        l1 = l2;
        m = i5;
        n = i2;
        k = i4;
        if (i1 > 0)
        {
          c = paramCharSequence.charAt(i6);
          if ((c < '0') || (c > '9')) {
            break label1046;
          }
          label437:
          n = i6;
          k = i1 - i6 + 1;
          localStringBuilder.append(c);
          localStringBuilder.append(j);
          m = 1;
          l1 = l2;
        }
      }
      for (;;)
      {
        label476:
        if (EmojiData.dataCharsMap.containsKey(Character.valueOf(j)))
        {
          n = i2;
          if (i2 == -1) {
            n = i1;
          }
          k = i4 + 1;
          localStringBuilder.append(j);
          m = 1;
          l1 = l2;
        }
        label931:
        label968:
        label1015:
        label1032:
        label1046:
        label1079:
        do
        {
          l1 = l2;
          m = i5;
          n = i2;
          k = i4;
          if (i2 != -1)
          {
            localStringBuilder.setLength(0);
            n = -1;
            k = 0;
            m = 0;
            l1 = l2;
            break label1032;
            i5 = i2;
            i7 = i3;
            int i8 = i1;
            i4 = n;
            m = k;
            if (i2 != 0)
            {
              m = i1;
              i2 = k;
              if (i1 + 2 < i9)
              {
                m = i1;
                i2 = k;
                if (paramCharSequence.charAt(i1 + 1) == 55356)
                {
                  m = i1;
                  i2 = k;
                  if (paramCharSequence.charAt(i1 + 2) >= 57339)
                  {
                    m = i1;
                    i2 = k;
                    if (paramCharSequence.charAt(i1 + 2) <= 57343)
                    {
                      localStringBuilder.append(paramCharSequence.subSequence(i1 + 1, i1 + 3));
                      i2 = k + 2;
                      m = i1 + 2;
                    }
                  }
                }
              }
              k = m;
              i4 = i2;
              if (m + 2 < i9)
              {
                k = m;
                i4 = i2;
                if (paramCharSequence.charAt(m + 1) == '‍') {
                  if (paramCharSequence.charAt(m + 2) != '♀')
                  {
                    k = m;
                    i4 = i2;
                    if (paramCharSequence.charAt(m + 2) != '♂') {}
                  }
                  else
                  {
                    localStringBuilder.append(paramCharSequence.subSequence(m + 1, m + 3));
                    i4 = i2 + 2;
                    k = m + 2;
                  }
                }
              }
              EmojiDrawable localEmojiDrawable = getEmojiDrawable(localStringBuilder.subSequence(0, localStringBuilder.length()));
              i1 = i3;
              if (localEmojiDrawable != null)
              {
                localSpannable.setSpan(new EmojiSpan(localEmojiDrawable, 0, paramInt, paramFontMetricsInt), n, n + i4, 33);
                i1 = i3 + 1;
              }
              m = 0;
              i4 = -1;
              localStringBuilder.setLength(0);
              i5 = 0;
              i8 = k;
              i7 = i1;
            }
            k = Build.VERSION.SDK_INT;
            if ((k < 23) && (i7 >= 50)) {
              return localSpannable;
            }
            i1 = i8 + 1;
            l2 = l1;
            i3 = i7;
            i2 = i4;
            i4 = m;
            break;
            if ((l2 == 0L) || ((0xFFFFFFFF00000000 & l2) != 0L) || ((0xFFFF & l2) != 55356L) || (j < 56806) || (j > 56831)) {
              break label284;
            }
            n = i2;
            if (i2 != -1) {
              break label106;
            }
            n = i1;
            break label106;
          }
          do
          {
            i6 = i1;
            i5 = 0;
            i2 = m;
            break;
            if (c == '#') {
              break label437;
            }
            l1 = l2;
            m = i5;
            n = i2;
            k = i4;
          } while (c != '*');
          break label437;
          if ((j == 169) || (j == 174)) {
            break label476;
          }
        } while ((j < 8252) || (j > 12953));
      }
      label1114:
      i7 = i2;
      m = i1;
      i4 = k;
      if (j >= 65024)
      {
        i7 = i2;
        m = i1;
        i4 = k;
        if (j <= 65039)
        {
          m = i1 + 1;
          i4 = k + 1;
          i7 = i2;
        }
      }
    }
  }
  
  private static class DrawableInfo
  {
    public byte page;
    public byte page2;
    public Rect rect;
    
    public DrawableInfo(Rect paramRect, byte paramByte1, byte paramByte2)
    {
      this.rect = paramRect;
      this.page = paramByte1;
      this.page2 = paramByte2;
    }
  }
  
  public static class EmojiDrawable
    extends Drawable
  {
    private static Paint paint = new Paint(2);
    private static Rect rect = new Rect();
    private boolean fullSize = false;
    private Emoji.DrawableInfo info;
    
    public EmojiDrawable(Emoji.DrawableInfo paramDrawableInfo)
    {
      this.info = paramDrawableInfo;
    }
    
    public void draw(Canvas paramCanvas)
    {
      if (Emoji.emojiBmp[this.info.page][this.info.page2] == null)
      {
        if (Emoji.loadingEmoji[this.info.page][this.info.page2] != 0) {
          return;
        }
        Emoji.loadingEmoji[this.info.page][this.info.page2] = 1;
        Utilities.globalQueue.postRunnable(new Runnable()
        {
          public void run()
          {
            Emoji.loadEmoji(Emoji.EmojiDrawable.this.info.page, Emoji.EmojiDrawable.this.info.page2);
            Emoji.loadingEmoji[Emoji.EmojiDrawable.this.info.page][Emoji.EmojiDrawable.this.info.page2] = 0;
          }
        });
        paramCanvas.drawRect(getBounds(), Emoji.placeholderPaint);
        return;
      }
      if (this.fullSize) {}
      for (Rect localRect = getDrawRect();; localRect = getBounds())
      {
        paramCanvas.drawBitmap(Emoji.emojiBmp[this.info.page][this.info.page2], this.info.rect, localRect, paint);
        return;
      }
    }
    
    public Rect getDrawRect()
    {
      Rect localRect = getBounds();
      int k = localRect.centerX();
      int j = localRect.centerY();
      localRect = rect;
      if (this.fullSize)
      {
        i = Emoji.bigImgSize;
        localRect.left = (k - i / 2);
        localRect = rect;
        if (!this.fullSize) {
          break label133;
        }
        i = Emoji.bigImgSize;
        label60:
        localRect.right = (i / 2 + k);
        localRect = rect;
        if (!this.fullSize) {
          break label140;
        }
        i = Emoji.bigImgSize;
        label86:
        localRect.top = (j - i / 2);
        localRect = rect;
        if (!this.fullSize) {
          break label147;
        }
      }
      label133:
      label140:
      label147:
      for (int i = Emoji.bigImgSize;; i = Emoji.drawImgSize)
      {
        localRect.bottom = (i / 2 + j);
        return rect;
        i = Emoji.drawImgSize;
        break;
        i = Emoji.drawImgSize;
        break label60;
        i = Emoji.drawImgSize;
        break label86;
      }
    }
    
    public Emoji.DrawableInfo getDrawableInfo()
    {
      return this.info;
    }
    
    public int getOpacity()
    {
      return -2;
    }
    
    public void setAlpha(int paramInt) {}
    
    public void setColorFilter(ColorFilter paramColorFilter) {}
  }
  
  public static class EmojiSpan
    extends ImageSpan
  {
    private Paint.FontMetricsInt fontMetrics = null;
    private int size = AndroidUtilities.dp(20.0F);
    
    public EmojiSpan(Emoji.EmojiDrawable paramEmojiDrawable, int paramInt1, int paramInt2, Paint.FontMetricsInt paramFontMetricsInt)
    {
      super(paramInt1);
      this.fontMetrics = paramFontMetricsInt;
      if (paramFontMetricsInt != null)
      {
        this.size = (Math.abs(this.fontMetrics.descent) + Math.abs(this.fontMetrics.ascent));
        if (this.size == 0) {
          this.size = AndroidUtilities.dp(20.0F);
        }
      }
    }
    
    public int getSize(Paint paramPaint, CharSequence paramCharSequence, int paramInt1, int paramInt2, Paint.FontMetricsInt paramFontMetricsInt)
    {
      Paint.FontMetricsInt localFontMetricsInt = paramFontMetricsInt;
      if (paramFontMetricsInt == null) {
        localFontMetricsInt = new Paint.FontMetricsInt();
      }
      if (this.fontMetrics == null)
      {
        paramInt1 = super.getSize(paramPaint, paramCharSequence, paramInt1, paramInt2, localFontMetricsInt);
        paramInt2 = AndroidUtilities.dp(8.0F);
        int i = AndroidUtilities.dp(10.0F);
        localFontMetricsInt.top = (-i - paramInt2);
        localFontMetricsInt.bottom = (i - paramInt2);
        localFontMetricsInt.ascent = (-i - paramInt2);
        localFontMetricsInt.leading = 0;
        localFontMetricsInt.descent = (i - paramInt2);
        return paramInt1;
      }
      if (localFontMetricsInt != null)
      {
        localFontMetricsInt.ascent = this.fontMetrics.ascent;
        localFontMetricsInt.descent = this.fontMetrics.descent;
        localFontMetricsInt.top = this.fontMetrics.top;
        localFontMetricsInt.bottom = this.fontMetrics.bottom;
      }
      if (getDrawable() != null) {
        getDrawable().setBounds(0, 0, this.size, this.size);
      }
      return this.size;
    }
    
    public void replaceFontMetrics(Paint.FontMetricsInt paramFontMetricsInt, int paramInt)
    {
      this.fontMetrics = paramFontMetricsInt;
      this.size = paramInt;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/Emoji.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */