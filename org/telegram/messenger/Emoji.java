package org.telegram.messenger;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
import android.text.TextPaint;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;

public class Emoji
{
  private static int bigImgSize = 0;
  private static final int[][] cols;
  private static int drawImgSize = 0;
  private static Bitmap[][] emojiBmp;
  public static HashMap<String, String> emojiColor;
  public static HashMap<String, Integer> emojiUseHistory;
  private static boolean inited = false;
  private static boolean[][] loadingEmoji;
  private static Paint placeholderPaint;
  public static ArrayList<String> recentEmoji;
  private static boolean recentEmojiLoaded = false;
  private static HashMap<CharSequence, DrawableInfo> rects = new HashMap();
  private static final int splitCount = 4;
  
  static
  {
    inited = false;
    emojiBmp = new Bitmap[5][4];
    loadingEmoji = new boolean[5][4];
    emojiUseHistory = new HashMap();
    recentEmoji = new ArrayList();
    emojiColor = new HashMap();
    Object localObject = { 16, 16, 16, 16 };
    int[] arrayOfInt1 = { 6, 6, 6, 6 };
    int[] arrayOfInt2 = { 9, 9, 9, 9 };
    int[] arrayOfInt3 = { 10, 10, 10, 10 };
    cols = new int[][] { localObject, arrayOfInt1, { 9, 9, 9, 9 }, arrayOfInt2, arrayOfInt3 };
    int i = 2;
    int j;
    float f;
    if (AndroidUtilities.density <= 1.0F)
    {
      j = 32;
      i = 1;
      drawImgSize = AndroidUtilities.dp(20.0F);
      if (!AndroidUtilities.isTablet()) {
        break label473;
      }
      f = 40.0F;
      label243:
      bigImgSize = AndroidUtilities.dp(f);
    }
    for (int k = 0;; k++)
    {
      if (k >= EmojiData.data.length) {
        break label486;
      }
      int m = (int)Math.ceil(EmojiData.data[k].length / 4.0F);
      int n = 0;
      for (;;)
      {
        if (n < EmojiData.data[k].length)
        {
          int i1 = n / m;
          int i2 = n - i1 * m;
          int i3 = i2 % cols[k][i1];
          i2 /= cols[k][i1];
          localObject = new Rect(i3 * j + i3 * i, i2 * j + i2 * i, (i3 + 1) * j + i3 * i, (i2 + 1) * j + i2 * i);
          rects.put(EmojiData.data[k][n], new DrawableInfo((Rect)localObject, (byte)k, (byte)i1, n));
          n++;
          continue;
          if (AndroidUtilities.density <= 1.5F)
          {
            j = 64;
            break;
          }
          if (AndroidUtilities.density <= 2.0F)
          {
            j = 64;
            break;
          }
          j = 64;
          break;
          label473:
          f = 32.0F;
          break label243;
        }
      }
    }
    label486:
    placeholderPaint = new Paint();
    placeholderPaint.setColor(0);
  }
  
  public static void addRecentEmoji(String paramString)
  {
    Object localObject1 = (Integer)emojiUseHistory.get(paramString);
    Object localObject2 = localObject1;
    if (localObject1 == null) {
      localObject2 = Integer.valueOf(0);
    }
    if ((((Integer)localObject2).intValue() == 0) && (emojiUseHistory.size() > 50)) {}
    for (int i = recentEmoji.size() - 1;; i--) {
      if (i >= 0)
      {
        localObject1 = (String)recentEmoji.get(i);
        emojiUseHistory.remove(localObject1);
        recentEmoji.remove(i);
        if (emojiUseHistory.size() > 50) {}
      }
      else
      {
        emojiUseHistory.put(paramString, Integer.valueOf(((Integer)localObject2).intValue() + 1));
        return;
      }
    }
  }
  
  public static void clearRecentEmoji()
  {
    MessagesController.getGlobalEmojiSettings().edit().putBoolean("filled_default", true).commit();
    emojiUseHistory.clear();
    recentEmoji.clear();
    saveRecentEmoji();
  }
  
  public static String fixEmoji(String paramString)
  {
    int i = paramString.length();
    int j = 0;
    String str = paramString;
    int k;
    int m;
    int n;
    if (j < i)
    {
      k = str.charAt(j);
      if ((k >= 55356) && (k <= 55358)) {
        if ((k == 55356) && (j < i - 1))
        {
          m = str.charAt(j + 1);
          if ((m == 56879) || (m == 56324) || (m == 56858) || (m == 56703))
          {
            paramString = str.substring(0, j + 2) + "️" + str.substring(j + 2);
            n = i + 1;
            m = j + 2;
          }
        }
      }
    }
    for (;;)
    {
      j = m + 1;
      i = n;
      str = paramString;
      break;
      m = j + 1;
      n = i;
      paramString = str;
      continue;
      m = j + 1;
      n = i;
      paramString = str;
      continue;
      if (k == 8419) {
        return str;
      }
      m = j;
      n = i;
      paramString = str;
      if (k >= 8252)
      {
        m = j;
        n = i;
        paramString = str;
        if (k <= 12953)
        {
          m = j;
          n = i;
          paramString = str;
          if (EmojiData.emojiToFE0FMap.containsKey(Character.valueOf(k)))
          {
            paramString = str.substring(0, j + 1) + "️" + str.substring(j + 1);
            n = i + 1;
            m = j + 1;
          }
        }
      }
    }
  }
  
  public static Drawable getEmojiBigDrawable(String paramString)
  {
    EmojiDrawable localEmojiDrawable1 = getEmojiDrawable(paramString);
    EmojiDrawable localEmojiDrawable2 = localEmojiDrawable1;
    if (localEmojiDrawable1 == null)
    {
      paramString = (CharSequence)EmojiData.emojiAliasMap.get(paramString);
      localEmojiDrawable2 = localEmojiDrawable1;
      if (paramString != null) {
        localEmojiDrawable2 = getEmojiDrawable(paramString);
      }
    }
    if (localEmojiDrawable2 == null) {
      localEmojiDrawable2 = null;
    }
    for (;;)
    {
      return localEmojiDrawable2;
      localEmojiDrawable2.setBounds(0, 0, bigImgSize, bigImgSize);
      EmojiDrawable.access$102(localEmojiDrawable2, true);
    }
  }
  
  public static EmojiDrawable getEmojiDrawable(CharSequence paramCharSequence)
  {
    DrawableInfo localDrawableInfo1 = (DrawableInfo)rects.get(paramCharSequence);
    DrawableInfo localDrawableInfo2 = localDrawableInfo1;
    if (localDrawableInfo1 == null)
    {
      CharSequence localCharSequence = (CharSequence)EmojiData.emojiAliasMap.get(paramCharSequence);
      localDrawableInfo2 = localDrawableInfo1;
      if (localCharSequence != null) {
        localDrawableInfo2 = (DrawableInfo)rects.get(localCharSequence);
      }
    }
    if (localDrawableInfo2 == null)
    {
      if (BuildVars.LOGS_ENABLED) {
        FileLog.d("No drawable for emoji " + paramCharSequence);
      }
      paramCharSequence = null;
    }
    for (;;)
    {
      return paramCharSequence;
      paramCharSequence = new EmojiDrawable(localDrawableInfo2);
      paramCharSequence.setBounds(0, 0, drawImgSize, drawImgSize);
    }
  }
  
  public static native Object[] getSuggestion(String paramString);
  
  private static boolean inArray(char paramChar, char[] paramArrayOfChar)
  {
    boolean bool1 = false;
    int i = paramArrayOfChar.length;
    for (int j = 0;; j++)
    {
      boolean bool2 = bool1;
      if (j < i)
      {
        if (paramArrayOfChar[j] == paramChar) {
          bool2 = true;
        }
      }
      else {
        return bool2;
      }
    }
  }
  
  public static void invalidateAll(View paramView)
  {
    if ((paramView instanceof ViewGroup))
    {
      paramView = (ViewGroup)paramView;
      for (int i = 0; i < paramView.getChildCount(); i++) {
        invalidateAll(paramView.getChildAt(i));
      }
    }
    if ((paramView instanceof TextView)) {
      paramView.invalidate();
    }
  }
  
  public static boolean isValidEmoji(String paramString)
  {
    DrawableInfo localDrawableInfo1 = (DrawableInfo)rects.get(paramString);
    DrawableInfo localDrawableInfo2 = localDrawableInfo1;
    if (localDrawableInfo1 == null)
    {
      paramString = (CharSequence)EmojiData.emojiAliasMap.get(paramString);
      localDrawableInfo2 = localDrawableInfo1;
      if (paramString != null) {
        localDrawableInfo2 = (DrawableInfo)rects.get(paramString);
      }
    }
    if (localDrawableInfo2 != null) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  private static void loadEmoji(int paramInt1, int paramInt2)
  {
    int i = 1;
    for (;;)
    {
      try
      {
        float f = AndroidUtilities.density;
        if (f <= 1.0F)
        {
          i = 2;
          j = 4;
          if (j >= 7) {
            continue;
          }
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
          j++;
        }
        catch (Exception localException)
        {
          Object localObject1;
          FileLog.e(localException);
          localObject3 = null;
          localObject2 = localObject3;
        }
        if (AndroidUtilities.density <= 1.5F) {
          continue;
        }
        f = AndroidUtilities.density;
        if (f <= 2.0F) {
          continue;
        }
        continue;
        int j = 8;
        if (j < 12)
        {
          localObject1 = String.format(Locale.US, "v%d_emoji%.01fx_%d.png", new Object[] { Integer.valueOf(j), Float.valueOf(2.0F), Integer.valueOf(paramInt1) });
          localObject1 = ApplicationLoader.applicationContext.getFileStreamPath((String)localObject1);
          if (((File)localObject1).exists()) {
            ((File)localObject1).delete();
          }
          j++;
          continue;
        }
      }
      catch (Throwable localThrowable1)
      {
        Object localObject3;
        Object localObject2;
        Object localObject4;
        Object localObject5;
        if (!BuildVars.LOGS_ENABLED) {
          continue;
        }
        FileLog.e("Error loading emoji", localThrowable1);
        continue;
      }
      try
      {
        localObject4 = ApplicationLoader.applicationContext.getAssets();
        localObject2 = localObject3;
        localObject5 = new java/lang/StringBuilder;
        localObject2 = localObject3;
        ((StringBuilder)localObject5).<init>();
        localObject2 = localObject3;
        localObject4 = ((AssetManager)localObject4).open("emoji/" + String.format(Locale.US, "v12_emoji%.01fx_%d_%d.png", new Object[] { Float.valueOf(2.0F), Integer.valueOf(paramInt1), Integer.valueOf(paramInt2) }));
        localObject2 = localObject3;
        localObject5 = new android/graphics/BitmapFactory$Options;
        localObject2 = localObject3;
        ((BitmapFactory.Options)localObject5).<init>();
        localObject2 = localObject3;
        ((BitmapFactory.Options)localObject5).inJustDecodeBounds = false;
        localObject2 = localObject3;
        ((BitmapFactory.Options)localObject5).inSampleSize = i;
        localObject2 = localObject3;
        localObject3 = BitmapFactory.decodeStream((InputStream)localObject4, null, (BitmapFactory.Options)localObject5);
        localObject2 = localObject3;
        ((InputStream)localObject4).close();
        localObject2 = localObject3;
      }
      catch (Throwable localThrowable2)
      {
        FileLog.e(localThrowable2);
      }
    }
    localObject3 = new org/telegram/messenger/Emoji$1;
    ((1)localObject3).<init>(paramInt1, paramInt2, (Bitmap)localObject2);
    AndroidUtilities.runOnUIThread((Runnable)localObject3);
  }
  
  public static void loadRecentEmoji()
  {
    if (recentEmojiLoaded) {}
    for (;;)
    {
      return;
      recentEmojiLoaded = true;
      Object localObject1 = MessagesController.getGlobalEmojiSettings();
      try
      {
        emojiUseHistory.clear();
        String[] arrayOfString1;
        int k;
        if (((SharedPreferences)localObject1).contains("emojis"))
        {
          localObject2 = ((SharedPreferences)localObject1).getString("emojis", "");
          if ((localObject2 != null) && (((String)localObject2).length() > 0))
          {
            arrayOfString1 = ((String)localObject2).split(",");
            int i = arrayOfString1.length;
            j = 0;
            if (j < i)
            {
              String[] arrayOfString2 = arrayOfString1[j].split("=");
              long l = Utilities.parseLong(arrayOfString2[0]).longValue();
              localObject2 = new java/lang/StringBuilder;
              ((StringBuilder)localObject2).<init>();
              for (k = 0;; k++) {
                if (k < 4)
                {
                  ((StringBuilder)localObject2).insert(0, String.valueOf((char)(int)l));
                  l >>= 16;
                  if (l != 0L) {}
                }
                else
                {
                  if (((StringBuilder)localObject2).length() > 0) {
                    emojiUseHistory.put(((StringBuilder)localObject2).toString(), Utilities.parseInt(arrayOfString2[1]));
                  }
                  j++;
                  break;
                }
              }
            }
          }
          ((SharedPreferences)localObject1).edit().remove("emojis").commit();
          saveRecentEmoji();
        }
        while ((emojiUseHistory.isEmpty()) && (!((SharedPreferences)localObject1).getBoolean("filled_default", false)))
        {
          localObject2 = new String[34];
          localObject2[0] = "😂";
          localObject2[1] = "😘";
          localObject2[2] = "❤";
          localObject2[3] = "😍";
          localObject2[4] = "😊";
          localObject2[5] = "😁";
          localObject2[6] = "👍";
          localObject2[7] = "☺";
          localObject2[8] = "😔";
          localObject2[9] = "😄";
          localObject2[10] = "😭";
          localObject2[11] = "💋";
          localObject2[12] = "😒";
          localObject2[13] = "😳";
          localObject2[14] = "😜";
          localObject2[15] = "🙈";
          localObject2[16] = "😉";
          localObject2[17] = "😃";
          localObject2[18] = "😢";
          localObject2[19] = "😝";
          localObject2[20] = "😱";
          localObject2[21] = "😡";
          localObject2[22] = "😏";
          localObject2[23] = "😞";
          localObject2[24] = "😅";
          localObject2[25] = "😚";
          localObject2[26] = "🙊";
          localObject2[27] = "😌";
          localObject2[28] = "😀";
          localObject2[29] = "😋";
          localObject2[30] = "😆";
          localObject2[31] = "👌";
          localObject2[32] = "😐";
          localObject2[33] = "😕";
          j = 0;
          for (;;)
          {
            if (j < localObject2.length)
            {
              emojiUseHistory.put(localObject2[j], Integer.valueOf(localObject2.length - j));
              j++;
              continue;
              localObject2 = ((SharedPreferences)localObject1).getString("emojis2", "");
              if ((localObject2 == null) || (((String)localObject2).length() <= 0)) {
                break;
              }
              arrayOfString1 = ((String)localObject2).split(",");
              k = arrayOfString1.length;
              for (j = 0; j < k; j++)
              {
                localObject2 = arrayOfString1[j].split("=");
                emojiUseHistory.put(localObject2[0], Utilities.parseInt(localObject2[1]));
              }
              break;
            }
          }
          ((SharedPreferences)localObject1).edit().putBoolean("filled_default", true).commit();
          saveRecentEmoji();
        }
        sortEmoji();
      }
      catch (Exception localException2)
      {
        try
        {
          for (;;)
          {
            localObject1 = ((SharedPreferences)localObject1).getString("color", "");
            if ((localObject1 == null) || (((String)localObject1).length() <= 0)) {
              break;
            }
            Object localObject2 = ((String)localObject1).split(",");
            for (int j = 0; j < localObject2.length; j++)
            {
              localObject1 = localObject2[j].split("=");
              emojiColor.put(localObject1[0], localObject1[1]);
            }
            localException2 = localException2;
            FileLog.e(localException2);
          }
        }
        catch (Exception localException1)
        {
          FileLog.e(localException1);
        }
      }
    }
  }
  
  public static CharSequence replaceEmoji(CharSequence paramCharSequence, Paint.FontMetricsInt paramFontMetricsInt, int paramInt, boolean paramBoolean)
  {
    return replaceEmoji(paramCharSequence, paramFontMetricsInt, paramInt, paramBoolean, null);
  }
  
  public static CharSequence replaceEmoji(CharSequence paramCharSequence, Paint.FontMetricsInt paramFontMetricsInt, int paramInt, boolean paramBoolean, int[] paramArrayOfInt)
  {
    Object localObject1 = paramCharSequence;
    if (!SharedConfig.useSystemEmoji)
    {
      localObject1 = paramCharSequence;
      if (paramCharSequence != null)
      {
        if (paramCharSequence.length() != 0) {
          break label31;
        }
        localObject1 = paramCharSequence;
      }
    }
    for (;;)
    {
      return (CharSequence)localObject1;
      label31:
      long l1;
      int i;
      int j;
      int k;
      int m;
      StringBuilder localStringBuilder;
      int n;
      int i1;
      int i2;
      Object localObject2;
      if ((!paramBoolean) && ((paramCharSequence instanceof Spannable)))
      {
        localObject1 = (Spannable)paramCharSequence;
        l1 = 0L;
        i = 0;
        j = -1;
        k = 0;
        m = 0;
        localStringBuilder = new StringBuilder(16);
        new StringBuilder(2);
        n = paramCharSequence.length();
        i1 = 0;
        i2 = 0;
        localObject2 = paramArrayOfInt;
        if (i2 >= n) {
          break label1418;
        }
      }
      try
      {
        int i3 = paramCharSequence.charAt(i2);
        int i5;
        long l2;
        label226:
        label351:
        label358:
        int i4;
        if (((i3 >= 55356) && (i3 <= 55358)) || ((l1 != 0L) && ((0xFFFFFFFF00000000 & l1) == 0L) && ((0xFFFF & l1) == 55356L) && (i3 >= 56806) && (i3 <= 56831)))
        {
          i5 = j;
          if (j == -1) {
            i5 = i2;
          }
          localStringBuilder.append(i3);
          i6 = k + 1;
          l2 = l1 << 16 | i3;
          paramArrayOfInt = (int[])localObject2;
          i7 = i1;
          j = i2;
          k = i6;
          if (i7 != 0)
          {
            j = i2;
            k = i6;
            if (i2 + 2 < n)
            {
              i1 = paramCharSequence.charAt(i2 + 1);
              if (i1 != 55356) {
                break label948;
              }
              i1 = paramCharSequence.charAt(i2 + 2);
              j = i2;
              k = i6;
              if (i1 >= 57339)
              {
                j = i2;
                k = i6;
                if (i1 <= 57343)
                {
                  localStringBuilder.append(paramCharSequence.subSequence(i2 + 1, i2 + 3));
                  k = i6 + 2;
                  j = i2 + 2;
                }
              }
            }
          }
          m = j;
          i1 = 0;
          if (i1 >= 3) {
            break label1151;
          }
          i8 = i7;
          i2 = j;
          i6 = k;
          if (j + 1 < n)
          {
            i4 = paramCharSequence.charAt(j + 1);
            if (i1 != 1) {
              break label1092;
            }
            i8 = i7;
            i2 = j;
            i6 = k;
            if (i4 == 8205)
            {
              i8 = i7;
              i2 = j;
              i6 = k;
              if (localStringBuilder.length() > 0)
              {
                localStringBuilder.append(i4);
                i2 = j + 1;
                i6 = k + 1;
                i8 = 0;
              }
            }
          }
        }
        for (;;)
        {
          i1++;
          i7 = i8;
          j = i2;
          k = i6;
          break label358;
          localObject1 = Spannable.Factory.getInstance().newSpannable(paramCharSequence.toString());
          break;
          if ((localStringBuilder.length() > 0) && ((i4 == 9792) || (i4 == 9794) || (i4 == 9877)))
          {
            localStringBuilder.append(i4);
            i6 = k + 1;
            l2 = 0L;
            i7 = 1;
            i5 = j;
            paramArrayOfInt = (int[])localObject2;
            break label226;
          }
          if ((l1 > 0L) && ((0xF000 & i4) == 53248))
          {
            localStringBuilder.append(i4);
            i6 = k + 1;
            l2 = 0L;
            i7 = 1;
            i5 = j;
            paramArrayOfInt = (int[])localObject2;
            break label226;
          }
          if (i4 == 8419)
          {
            l2 = l1;
            i7 = i1;
            i5 = j;
            i6 = k;
            paramArrayOfInt = (int[])localObject2;
            if (i2 <= 0) {
              break label226;
            }
            char c = paramCharSequence.charAt(m);
            if (((c < '0') || (c > '9')) && (c != '#'))
            {
              l2 = l1;
              i7 = i1;
              i5 = j;
              i6 = k;
              paramArrayOfInt = (int[])localObject2;
              if (c != '*') {
                break label226;
              }
            }
            i5 = m;
            i6 = i2 - m + 1;
            localStringBuilder.append(c);
            localStringBuilder.append(i4);
            i7 = 1;
            l2 = l1;
            paramArrayOfInt = (int[])localObject2;
            break label226;
          }
          if (((i4 == 169) || (i4 == 174) || ((i4 >= 8252) && (i4 <= 12953))) && (EmojiData.dataCharsMap.containsKey(Character.valueOf(i4))))
          {
            i5 = j;
            if (j == -1) {
              i5 = i2;
            }
            i6 = k + 1;
            localStringBuilder.append(i4);
            i7 = 1;
            l2 = l1;
            paramArrayOfInt = (int[])localObject2;
            break label226;
          }
          if (j != -1)
          {
            localStringBuilder.setLength(0);
            i5 = -1;
            i6 = 0;
            i7 = 0;
            l2 = l1;
            paramArrayOfInt = (int[])localObject2;
            break label226;
          }
          l2 = l1;
          i7 = i1;
          i5 = j;
          i6 = k;
          paramArrayOfInt = (int[])localObject2;
          if (i4 == 65039) {
            break label226;
          }
          l2 = l1;
          i7 = i1;
          i5 = j;
          i6 = k;
          paramArrayOfInt = (int[])localObject2;
          if (localObject2 == null) {
            break label226;
          }
          localObject2[0] = 0;
          paramArrayOfInt = null;
          l2 = l1;
          i7 = i1;
          i5 = j;
          i6 = k;
          break label226;
          label948:
          j = i2;
          k = i6;
          if (localStringBuilder.length() < 2) {
            break label351;
          }
          j = i2;
          k = i6;
          if (localStringBuilder.charAt(0) != 55356) {
            break label351;
          }
          j = i2;
          k = i6;
          if (localStringBuilder.charAt(1) != 57332) {
            break label351;
          }
          j = i2;
          k = i6;
          if (i1 != 56128) {
            break label351;
          }
          i2++;
          do
          {
            localStringBuilder.append(paramCharSequence.subSequence(i2, i2 + 2));
            k = i6 + 2;
            j = i2 + 2;
            if (j >= paramCharSequence.length()) {
              break;
            }
            i2 = j;
            i6 = k;
          } while (paramCharSequence.charAt(j) == 56128);
          j--;
          break label351;
          label1092:
          i8 = i7;
          i2 = j;
          i6 = k;
          if (i4 >= 65024)
          {
            i8 = i7;
            i2 = j;
            i6 = k;
            if (i4 <= 65039)
            {
              i2 = j + 1;
              i6 = k + 1;
              i8 = i7;
            }
          }
        }
        label1151:
        int i8 = j;
        i2 = k;
        if (i7 != 0)
        {
          i8 = j;
          i2 = k;
          if (j + 2 < n)
          {
            i8 = j;
            i2 = k;
            if (paramCharSequence.charAt(j + 1) == 55356)
            {
              i6 = paramCharSequence.charAt(j + 2);
              i8 = j;
              i2 = k;
              if (i6 >= 57339)
              {
                i8 = j;
                i2 = k;
                if (i6 <= 57343)
                {
                  localStringBuilder.append(paramCharSequence.subSequence(j + 1, j + 3));
                  i2 = k + 2;
                  i8 = j + 2;
                }
              }
            }
          }
        }
        i1 = i7;
        k = i;
        j = i5;
        int i6 = i2;
        if (i7 != 0)
        {
          if (paramArrayOfInt != null) {
            paramArrayOfInt[0] += 1;
          }
          EmojiDrawable localEmojiDrawable = getEmojiDrawable(localStringBuilder.subSequence(0, localStringBuilder.length()));
          i7 = i;
          if (localEmojiDrawable != null)
          {
            localObject2 = new org/telegram/messenger/Emoji$EmojiSpan;
            ((EmojiSpan)localObject2).<init>(localEmojiDrawable, 0, paramInt, paramFontMetricsInt);
            ((Spannable)localObject1).setSpan(localObject2, i5, i5 + i2, 33);
            i7 = i + 1;
          }
          i6 = 0;
          j = -1;
          localStringBuilder.setLength(0);
          i1 = 0;
          k = i7;
        }
        int i7 = Build.VERSION.SDK_INT;
        if ((i7 >= 23) || (k < 50))
        {
          label1418:
          i2 = i8 + 1;
          l1 = l2;
          i = k;
          k = i6;
          localObject2 = paramArrayOfInt;
        }
      }
      catch (Exception paramFontMetricsInt)
      {
        FileLog.e(paramFontMetricsInt);
        localObject1 = paramCharSequence;
      }
    }
  }
  
  public static void saveEmojiColors()
  {
    SharedPreferences localSharedPreferences = MessagesController.getGlobalEmojiSettings();
    StringBuilder localStringBuilder = new StringBuilder();
    Iterator localIterator = emojiColor.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      if (localStringBuilder.length() != 0) {
        localStringBuilder.append(",");
      }
      localStringBuilder.append((String)localEntry.getKey());
      localStringBuilder.append("=");
      localStringBuilder.append((String)localEntry.getValue());
    }
    localSharedPreferences.edit().putString("color", localStringBuilder.toString()).commit();
  }
  
  public static void saveRecentEmoji()
  {
    SharedPreferences localSharedPreferences = MessagesController.getGlobalEmojiSettings();
    StringBuilder localStringBuilder = new StringBuilder();
    Iterator localIterator = emojiUseHistory.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      if (localStringBuilder.length() != 0) {
        localStringBuilder.append(",");
      }
      localStringBuilder.append((String)localEntry.getKey());
      localStringBuilder.append("=");
      localStringBuilder.append(localEntry.getValue());
    }
    localSharedPreferences.edit().putString("emojis2", localStringBuilder.toString()).commit();
  }
  
  public static void sortEmoji()
  {
    recentEmoji.clear();
    Iterator localIterator = emojiUseHistory.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      recentEmoji.add(localEntry.getKey());
    }
    Collections.sort(recentEmoji, new Comparator()
    {
      public int compare(String paramAnonymousString1, String paramAnonymousString2)
      {
        int i = 0;
        Integer localInteger1 = (Integer)Emoji.emojiUseHistory.get(paramAnonymousString1);
        Integer localInteger2 = (Integer)Emoji.emojiUseHistory.get(paramAnonymousString2);
        paramAnonymousString1 = localInteger1;
        if (localInteger1 == null) {
          paramAnonymousString1 = Integer.valueOf(0);
        }
        paramAnonymousString2 = localInteger2;
        if (localInteger2 == null) {
          paramAnonymousString2 = Integer.valueOf(0);
        }
        if (paramAnonymousString1.intValue() > paramAnonymousString2.intValue()) {
          i = -1;
        }
        for (;;)
        {
          return i;
          if (paramAnonymousString1.intValue() < paramAnonymousString2.intValue()) {
            i = 1;
          }
        }
      }
    });
    while (recentEmoji.size() > 50) {
      recentEmoji.remove(recentEmoji.size() - 1);
    }
  }
  
  private static class DrawableInfo
  {
    public int emojiIndex;
    public byte page;
    public byte page2;
    public Rect rect;
    
    public DrawableInfo(Rect paramRect, byte paramByte1, byte paramByte2, int paramInt)
    {
      this.rect = paramRect;
      this.page = ((byte)paramByte1);
      this.page2 = ((byte)paramByte2);
      this.emojiIndex = paramInt;
    }
  }
  
  public static class EmojiDrawable
    extends Drawable
  {
    private static Paint paint = new Paint(2);
    private static Rect rect = new Rect();
    private static TextPaint textPaint = new TextPaint(1);
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
        if (Emoji.loadingEmoji[this.info.page][this.info.page2] != 0) {}
        for (;;)
        {
          return;
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
        }
      }
      if (this.fullSize) {}
      for (Rect localRect = getDrawRect();; localRect = getBounds())
      {
        paramCanvas.drawBitmap(Emoji.emojiBmp[this.info.page][this.info.page2], this.info.rect, localRect, paint);
        break;
      }
    }
    
    public Rect getDrawRect()
    {
      Rect localRect = getBounds();
      int i = localRect.centerX();
      int j = localRect.centerY();
      localRect = rect;
      if (this.fullSize)
      {
        k = Emoji.bigImgSize;
        localRect.left = (i - k / 2);
        localRect = rect;
        if (!this.fullSize) {
          break label131;
        }
        k = Emoji.bigImgSize;
        label57:
        localRect.right = (k / 2 + i);
        localRect = rect;
        if (!this.fullSize) {
          break label139;
        }
        k = Emoji.bigImgSize;
        label83:
        localRect.top = (j - k / 2);
        localRect = rect;
        if (!this.fullSize) {
          break label147;
        }
      }
      label131:
      label139:
      label147:
      for (int k = Emoji.bigImgSize;; k = Emoji.drawImgSize)
      {
        localRect.bottom = (k / 2 + j);
        return rect;
        k = Emoji.drawImgSize;
        break;
        k = Emoji.drawImgSize;
        break label57;
        k = Emoji.drawImgSize;
        break label83;
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
      }
      for (;;)
      {
        return paramInt1;
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
        paramInt1 = this.size;
      }
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