package org.telegram.ui.Components;

import android.os.Build.VERSION;
import android.text.Layout.Alignment;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextDirectionHeuristic;
import android.text.TextDirectionHeuristics;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import org.telegram.messenger.FileLog;

public class StaticLayoutEx
{
  private static final String TEXT_DIRS_CLASS = "android.text.TextDirectionHeuristics";
  private static final String TEXT_DIR_CLASS = "android.text.TextDirectionHeuristic";
  private static final String TEXT_DIR_FIRSTSTRONG_LTR = "FIRSTSTRONG_LTR";
  private static boolean initialized;
  private static Constructor<StaticLayout> sConstructor;
  private static Object[] sConstructorArgs;
  private static Object sTextDirection;
  
  public static StaticLayout createStaticLayout(CharSequence paramCharSequence, int paramInt1, int paramInt2, TextPaint paramTextPaint, int paramInt3, Layout.Alignment paramAlignment, float paramFloat1, float paramFloat2, boolean paramBoolean, TextUtils.TruncateAt paramTruncateAt, int paramInt4, int paramInt5)
  {
    if (paramInt5 == 1) {
      f = paramInt4;
    }
    try
    {
      paramCharSequence = TextUtils.ellipsize(paramCharSequence, paramTextPaint, f, TextUtils.TruncateAt.END);
      return new StaticLayout(paramCharSequence, 0, paramCharSequence.length(), paramTextPaint, paramInt3, paramAlignment, paramFloat1, paramFloat2, paramBoolean);
    }
    catch (Exception paramCharSequence)
    {
      FileLog.e("tmessages", paramCharSequence);
    }
    paramTruncateAt = new StaticLayout(paramCharSequence, paramTextPaint, paramInt3, paramAlignment, paramFloat1, paramFloat2, paramBoolean);
    if (paramTruncateAt.getLineCount() <= paramInt5) {
      return paramTruncateAt;
    }
    float f = paramTruncateAt.getLineLeft(paramInt5 - 1);
    if (f != 0.0F) {}
    for (paramInt1 = paramTruncateAt.getOffsetForHorizontal(paramInt5 - 1, f);; paramInt1 = paramTruncateAt.getOffsetForHorizontal(paramInt5 - 1, paramTruncateAt.getLineWidth(paramInt5 - 1)))
    {
      paramCharSequence = new SpannableStringBuilder(paramCharSequence.subSequence(0, Math.max(0, paramInt1 - 1)));
      paramCharSequence.append("…");
      return new StaticLayout(paramCharSequence, paramTextPaint, paramInt3, paramAlignment, paramFloat1, paramFloat2, paramBoolean);
    }
    return null;
  }
  
  public static StaticLayout createStaticLayout(CharSequence paramCharSequence, TextPaint paramTextPaint, int paramInt1, Layout.Alignment paramAlignment, float paramFloat1, float paramFloat2, boolean paramBoolean, TextUtils.TruncateAt paramTruncateAt, int paramInt2, int paramInt3)
  {
    return createStaticLayout(paramCharSequence, 0, paramCharSequence.length(), paramTextPaint, paramInt1, paramAlignment, paramFloat1, paramFloat2, paramBoolean, paramTruncateAt, paramInt2, paramInt3);
  }
  
  public static void init()
  {
    if (initialized) {
      return;
    }
    for (;;)
    {
      try
      {
        if (Build.VERSION.SDK_INT >= 18)
        {
          Class localClass1 = TextDirectionHeuristic.class;
          sTextDirection = TextDirectionHeuristics.FIRSTSTRONG_LTR;
          localObject = new Class[13];
          localObject[0] = CharSequence.class;
          localObject[1] = Integer.TYPE;
          localObject[2] = Integer.TYPE;
          localObject[3] = TextPaint.class;
          localObject[4] = Integer.TYPE;
          localObject[5] = Layout.Alignment.class;
          localObject[6] = localClass1;
          localObject[7] = Float.TYPE;
          localObject[8] = Float.TYPE;
          localObject[9] = Boolean.TYPE;
          localObject[10] = TextUtils.TruncateAt.class;
          localObject[11] = Integer.TYPE;
          localObject[12] = Integer.TYPE;
          sConstructor = StaticLayout.class.getDeclaredConstructor((Class[])localObject);
          sConstructor.setAccessible(true);
          sConstructorArgs = new Object[localObject.length];
          initialized = true;
          return;
        }
      }
      catch (Throwable localThrowable)
      {
        FileLog.e("tmessages", localThrowable);
        return;
      }
      Object localObject = StaticLayoutEx.class.getClassLoader();
      Class localClass2 = ((ClassLoader)localObject).loadClass("android.text.TextDirectionHeuristic");
      localObject = ((ClassLoader)localObject).loadClass("android.text.TextDirectionHeuristics");
      sTextDirection = ((Class)localObject).getField("FIRSTSTRONG_LTR").get(localObject);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/StaticLayoutEx.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */