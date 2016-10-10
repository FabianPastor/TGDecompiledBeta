package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.View;
import java.util.ArrayList;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AnimatorListenerAdapterProxy;

public class NumberTextView
  extends View
{
  private ObjectAnimator animator;
  private int currentNumber = 1;
  private ArrayList<StaticLayout> letters = new ArrayList();
  private ArrayList<StaticLayout> oldLetters = new ArrayList();
  private float progress = 0.0F;
  private TextPaint textPaint = new TextPaint(1);
  
  public NumberTextView(Context paramContext)
  {
    super(paramContext);
  }
  
  public float getProgress()
  {
    return this.progress;
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    if (this.letters.isEmpty()) {
      return;
    }
    float f2 = ((StaticLayout)this.letters.get(0)).getHeight();
    paramCanvas.save();
    paramCanvas.translate(getPaddingLeft(), (getMeasuredHeight() - f2) / 2.0F);
    int j = Math.max(this.letters.size(), this.oldLetters.size());
    int i = 0;
    if (i < j)
    {
      paramCanvas.save();
      StaticLayout localStaticLayout1;
      label110:
      StaticLayout localStaticLayout2;
      if (i < this.oldLetters.size())
      {
        localStaticLayout1 = (StaticLayout)this.oldLetters.get(i);
        if (i >= this.letters.size()) {
          break label274;
        }
        localStaticLayout2 = (StaticLayout)this.letters.get(i);
        label136:
        if (this.progress <= 0.0F) {
          break label293;
        }
        if (localStaticLayout1 == null) {
          break label280;
        }
        this.textPaint.setAlpha((int)(this.progress * 255.0F));
        paramCanvas.save();
        paramCanvas.translate(0.0F, (this.progress - 1.0F) * f2);
        localStaticLayout1.draw(paramCanvas);
        paramCanvas.restore();
        if (localStaticLayout2 != null)
        {
          this.textPaint.setAlpha((int)((1.0F - this.progress) * 255.0F));
          paramCanvas.translate(0.0F, this.progress * f2);
        }
        label226:
        if (localStaticLayout2 != null) {
          localStaticLayout2.draw(paramCanvas);
        }
        paramCanvas.restore();
        if (localStaticLayout2 == null) {
          break label432;
        }
      }
      label274:
      label280:
      label293:
      label432:
      for (float f1 = localStaticLayout2.getLineWidth(0);; f1 = localStaticLayout1.getLineWidth(0) + AndroidUtilities.dp(1.0F))
      {
        paramCanvas.translate(f1, 0.0F);
        i += 1;
        break;
        localStaticLayout1 = null;
        break label110;
        localStaticLayout2 = null;
        break label136;
        this.textPaint.setAlpha(255);
        break label226;
        if (this.progress < 0.0F)
        {
          if (localStaticLayout1 != null)
          {
            this.textPaint.setAlpha((int)(-this.progress * 255.0F));
            paramCanvas.save();
            paramCanvas.translate(0.0F, (this.progress + 1.0F) * f2);
            localStaticLayout1.draw(paramCanvas);
            paramCanvas.restore();
          }
          if (localStaticLayout2 == null) {
            break label226;
          }
          if ((i == j - 1) || (localStaticLayout1 != null))
          {
            this.textPaint.setAlpha((int)((this.progress + 1.0F) * 255.0F));
            paramCanvas.translate(0.0F, this.progress * f2);
            break label226;
          }
          this.textPaint.setAlpha(255);
          break label226;
        }
        if (localStaticLayout2 == null) {
          break label226;
        }
        this.textPaint.setAlpha(255);
        break label226;
      }
    }
    paramCanvas.restore();
  }
  
  public void setNumber(int paramInt, boolean paramBoolean)
  {
    if ((this.currentNumber == paramInt) && (paramBoolean)) {
      return;
    }
    if (this.animator != null)
    {
      this.animator.cancel();
      this.animator = null;
    }
    this.oldLetters.clear();
    this.oldLetters.addAll(this.letters);
    this.letters.clear();
    String str1 = String.format(Locale.US, "%d", new Object[] { Integer.valueOf(this.currentNumber) });
    String str2 = String.format(Locale.US, "%d", new Object[] { Integer.valueOf(paramInt) });
    int i;
    label126:
    String str3;
    Object localObject;
    if (paramInt > this.currentNumber)
    {
      i = 1;
      this.currentNumber = paramInt;
      this.progress = 0.0F;
      paramInt = 0;
      if (paramInt >= str2.length()) {
        break label284;
      }
      str3 = str2.substring(paramInt, paramInt + 1);
      if ((this.oldLetters.isEmpty()) || (paramInt >= str1.length())) {
        break label230;
      }
      localObject = str1.substring(paramInt, paramInt + 1);
      label176:
      if ((localObject == null) || (!((String)localObject).equals(str3))) {
        break label236;
      }
      this.letters.add(this.oldLetters.get(paramInt));
      this.oldLetters.set(paramInt, null);
    }
    for (;;)
    {
      paramInt += 1;
      break label126;
      i = 0;
      break;
      label230:
      localObject = null;
      break label176;
      label236:
      localObject = new StaticLayout(str3, this.textPaint, (int)Math.ceil(this.textPaint.measureText(str3)), Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
      this.letters.add(localObject);
    }
    label284:
    if ((paramBoolean) && (!this.oldLetters.isEmpty())) {
      if (i == 0) {
        break label365;
      }
    }
    label365:
    for (float f = -1.0F;; f = 1.0F)
    {
      this.animator = ObjectAnimator.ofFloat(this, "progress", new float[] { f, 0.0F });
      this.animator.setDuration(150L);
      this.animator.addListener(new AnimatorListenerAdapterProxy()
      {
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          NumberTextView.access$002(NumberTextView.this, null);
          NumberTextView.this.oldLetters.clear();
        }
      });
      this.animator.start();
      invalidate();
      return;
    }
  }
  
  public void setProgress(float paramFloat)
  {
    if (this.progress == paramFloat) {
      return;
    }
    this.progress = paramFloat;
    invalidate();
  }
  
  public void setTextColor(int paramInt)
  {
    this.textPaint.setColor(paramInt);
    invalidate();
  }
  
  public void setTextSize(int paramInt)
  {
    this.textPaint.setTextSize(AndroidUtilities.dp(paramInt));
    this.oldLetters.clear();
    this.letters.clear();
    setNumber(this.currentNumber, false);
  }
  
  public void setTypeface(Typeface paramTypeface)
  {
    this.textPaint.setTypeface(paramTypeface);
    this.oldLetters.clear();
    this.letters.clear();
    setNumber(this.currentNumber, false);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/NumberTextView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */