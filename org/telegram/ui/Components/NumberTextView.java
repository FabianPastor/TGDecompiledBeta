package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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
    if (this.letters.isEmpty()) {}
    for (;;)
    {
      return;
      float f1 = ((StaticLayout)this.letters.get(0)).getHeight();
      paramCanvas.save();
      paramCanvas.translate(getPaddingLeft(), (getMeasuredHeight() - f1) / 2.0F);
      int i = Math.max(this.letters.size(), this.oldLetters.size());
      int j = 0;
      if (j < i)
      {
        paramCanvas.save();
        StaticLayout localStaticLayout1;
        label108:
        StaticLayout localStaticLayout2;
        if (j < this.oldLetters.size())
        {
          localStaticLayout1 = (StaticLayout)this.oldLetters.get(j);
          if (j >= this.letters.size()) {
            break label271;
          }
          localStaticLayout2 = (StaticLayout)this.letters.get(j);
          label134:
          if (this.progress <= 0.0F) {
            break label290;
          }
          if (localStaticLayout1 == null) {
            break label277;
          }
          this.textPaint.setAlpha((int)(this.progress * 255.0F));
          paramCanvas.save();
          paramCanvas.translate(0.0F, (this.progress - 1.0F) * f1);
          localStaticLayout1.draw(paramCanvas);
          paramCanvas.restore();
          if (localStaticLayout2 != null)
          {
            this.textPaint.setAlpha((int)((1.0F - this.progress) * 255.0F));
            paramCanvas.translate(0.0F, this.progress * f1);
          }
          label224:
          if (localStaticLayout2 != null) {
            localStaticLayout2.draw(paramCanvas);
          }
          paramCanvas.restore();
          if (localStaticLayout2 == null) {
            break label428;
          }
        }
        label271:
        label277:
        label290:
        label428:
        for (float f2 = localStaticLayout2.getLineWidth(0);; f2 = localStaticLayout1.getLineWidth(0) + AndroidUtilities.dp(1.0F))
        {
          paramCanvas.translate(f2, 0.0F);
          j++;
          break;
          localStaticLayout1 = null;
          break label108;
          localStaticLayout2 = null;
          break label134;
          this.textPaint.setAlpha(255);
          break label224;
          if (this.progress < 0.0F)
          {
            if (localStaticLayout1 != null)
            {
              this.textPaint.setAlpha((int)(-this.progress * 255.0F));
              paramCanvas.save();
              paramCanvas.translate(0.0F, (this.progress + 1.0F) * f1);
              localStaticLayout1.draw(paramCanvas);
              paramCanvas.restore();
            }
            if (localStaticLayout2 == null) {
              break label224;
            }
            if ((j == i - 1) || (localStaticLayout1 != null))
            {
              this.textPaint.setAlpha((int)((this.progress + 1.0F) * 255.0F));
              paramCanvas.translate(0.0F, this.progress * f1);
              break label224;
            }
            this.textPaint.setAlpha(255);
            break label224;
          }
          if (localStaticLayout2 == null) {
            break label224;
          }
          this.textPaint.setAlpha(255);
          break label224;
        }
      }
      paramCanvas.restore();
    }
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
    label125:
    String str3;
    Object localObject;
    if (paramInt > this.currentNumber)
    {
      i = 1;
      this.currentNumber = paramInt;
      this.progress = 0.0F;
      paramInt = 0;
      if (paramInt >= str2.length()) {
        break label280;
      }
      str3 = str2.substring(paramInt, paramInt + 1);
      if ((this.oldLetters.isEmpty()) || (paramInt >= str1.length())) {
        break label226;
      }
      localObject = str1.substring(paramInt, paramInt + 1);
      label173:
      if ((localObject == null) || (!((String)localObject).equals(str3))) {
        break label232;
      }
      this.letters.add(this.oldLetters.get(paramInt));
      this.oldLetters.set(paramInt, null);
    }
    for (;;)
    {
      paramInt++;
      break label125;
      i = 0;
      break;
      label226:
      localObject = null;
      break label173;
      label232:
      localObject = new StaticLayout(str3, this.textPaint, (int)Math.ceil(this.textPaint.measureText(str3)), Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
      this.letters.add(localObject);
    }
    label280:
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
      this.animator.addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          NumberTextView.access$002(NumberTextView.this, null);
          NumberTextView.this.oldLetters.clear();
        }
      });
      this.animator.start();
      invalidate();
      break;
    }
  }
  
  public void setProgress(float paramFloat)
  {
    if (this.progress == paramFloat) {}
    for (;;)
    {
      return;
      this.progress = paramFloat;
      invalidate();
    }
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