package android.support.v4.animation;

import android.annotation.TargetApi;
import android.support.annotation.RequiresApi;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

@TargetApi(9)
@RequiresApi(9)
class GingerbreadAnimatorCompatProvider
  implements AnimatorProvider
{
  public void clearInterpolator(View paramView) {}
  
  public ValueAnimatorCompat emptyValueAnimator()
  {
    return new GingerbreadFloatValueAnimator();
  }
  
  private static class GingerbreadFloatValueAnimator
    implements ValueAnimatorCompat
  {
    private long mDuration = 200L;
    private boolean mEnded = false;
    private float mFraction = 0.0F;
    List<AnimatorListenerCompat> mListeners = new ArrayList();
    private Runnable mLoopRunnable = new Runnable()
    {
      public void run()
      {
        float f = (float)(GingerbreadAnimatorCompatProvider.GingerbreadFloatValueAnimator.this.getTime() - GingerbreadAnimatorCompatProvider.GingerbreadFloatValueAnimator.this.mStartTime) * 1.0F / (float)GingerbreadAnimatorCompatProvider.GingerbreadFloatValueAnimator.this.mDuration;
        if ((f > 1.0F) || (GingerbreadAnimatorCompatProvider.GingerbreadFloatValueAnimator.this.mTarget.getParent() == null)) {
          f = 1.0F;
        }
        GingerbreadAnimatorCompatProvider.GingerbreadFloatValueAnimator.access$302(GingerbreadAnimatorCompatProvider.GingerbreadFloatValueAnimator.this, f);
        GingerbreadAnimatorCompatProvider.GingerbreadFloatValueAnimator.this.notifyUpdateListeners();
        if (GingerbreadAnimatorCompatProvider.GingerbreadFloatValueAnimator.this.mFraction >= 1.0F)
        {
          GingerbreadAnimatorCompatProvider.GingerbreadFloatValueAnimator.this.dispatchEnd();
          return;
        }
        GingerbreadAnimatorCompatProvider.GingerbreadFloatValueAnimator.this.mTarget.postDelayed(GingerbreadAnimatorCompatProvider.GingerbreadFloatValueAnimator.this.mLoopRunnable, 16L);
      }
    };
    private long mStartTime;
    private boolean mStarted = false;
    View mTarget;
    List<AnimatorUpdateListenerCompat> mUpdateListeners = new ArrayList();
    
    private void dispatchCancel()
    {
      int i = this.mListeners.size() - 1;
      while (i >= 0)
      {
        ((AnimatorListenerCompat)this.mListeners.get(i)).onAnimationCancel(this);
        i -= 1;
      }
    }
    
    private void dispatchEnd()
    {
      int i = this.mListeners.size() - 1;
      while (i >= 0)
      {
        ((AnimatorListenerCompat)this.mListeners.get(i)).onAnimationEnd(this);
        i -= 1;
      }
    }
    
    private void dispatchStart()
    {
      int i = this.mListeners.size() - 1;
      while (i >= 0)
      {
        ((AnimatorListenerCompat)this.mListeners.get(i)).onAnimationStart(this);
        i -= 1;
      }
    }
    
    private long getTime()
    {
      return this.mTarget.getDrawingTime();
    }
    
    private void notifyUpdateListeners()
    {
      int i = this.mUpdateListeners.size() - 1;
      while (i >= 0)
      {
        ((AnimatorUpdateListenerCompat)this.mUpdateListeners.get(i)).onAnimationUpdate(this);
        i -= 1;
      }
    }
    
    public void addListener(AnimatorListenerCompat paramAnimatorListenerCompat)
    {
      this.mListeners.add(paramAnimatorListenerCompat);
    }
    
    public void addUpdateListener(AnimatorUpdateListenerCompat paramAnimatorUpdateListenerCompat)
    {
      this.mUpdateListeners.add(paramAnimatorUpdateListenerCompat);
    }
    
    public void cancel()
    {
      if (this.mEnded) {
        return;
      }
      this.mEnded = true;
      if (this.mStarted) {
        dispatchCancel();
      }
      dispatchEnd();
    }
    
    public float getAnimatedFraction()
    {
      return this.mFraction;
    }
    
    public void setDuration(long paramLong)
    {
      if (!this.mStarted) {
        this.mDuration = paramLong;
      }
    }
    
    public void setTarget(View paramView)
    {
      this.mTarget = paramView;
    }
    
    public void start()
    {
      if (this.mStarted) {
        return;
      }
      this.mStarted = true;
      dispatchStart();
      this.mFraction = 0.0F;
      this.mStartTime = getTime();
      this.mTarget.postDelayed(this.mLoopRunnable, 16L);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v4/animation/GingerbreadAnimatorCompatProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */