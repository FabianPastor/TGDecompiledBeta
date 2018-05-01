package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.text.TextUtils.TruncateAt;
import android.view.View.MeasureSpec;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PhotoEditorSeekBar;
import org.telegram.ui.Components.PhotoEditorSeekBar.PhotoEditorSeekBarDelegate;

public class PhotoEditToolCell
  extends FrameLayout
{
  private Runnable hideValueRunnable = new Runnable()
  {
    public void run()
    {
      PhotoEditToolCell.this.valueTextView.setTag(null);
      PhotoEditToolCell.access$102(PhotoEditToolCell.this, new AnimatorSet());
      PhotoEditToolCell.this.valueAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(PhotoEditToolCell.this.valueTextView, "alpha", new float[] { 0.0F }), ObjectAnimator.ofFloat(PhotoEditToolCell.this.nameTextView, "alpha", new float[] { 1.0F }) });
      PhotoEditToolCell.this.valueAnimation.setDuration(180L);
      PhotoEditToolCell.this.valueAnimation.setInterpolator(new DecelerateInterpolator());
      PhotoEditToolCell.this.valueAnimation.addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnonymous2Animator)
        {
          if (paramAnonymous2Animator.equals(PhotoEditToolCell.this.valueAnimation)) {
            PhotoEditToolCell.access$102(PhotoEditToolCell.this, null);
          }
        }
      });
      PhotoEditToolCell.this.valueAnimation.start();
    }
  };
  private TextView nameTextView;
  private PhotoEditorSeekBar seekBar;
  private AnimatorSet valueAnimation;
  private TextView valueTextView;
  
  public PhotoEditToolCell(Context paramContext)
  {
    super(paramContext);
    this.nameTextView = new TextView(paramContext);
    this.nameTextView.setGravity(5);
    this.nameTextView.setTextColor(-1);
    this.nameTextView.setTextSize(1, 12.0F);
    this.nameTextView.setMaxLines(1);
    this.nameTextView.setSingleLine(true);
    this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
    addView(this.nameTextView, LayoutHelper.createFrame(80, -2.0F, 19, 0.0F, 0.0F, 0.0F, 0.0F));
    this.valueTextView = new TextView(paramContext);
    this.valueTextView.setTextColor(-9649153);
    this.valueTextView.setTextSize(1, 12.0F);
    this.valueTextView.setGravity(5);
    this.valueTextView.setSingleLine(true);
    addView(this.valueTextView, LayoutHelper.createFrame(80, -2.0F, 19, 0.0F, 0.0F, 0.0F, 0.0F));
    this.seekBar = new PhotoEditorSeekBar(paramContext);
    addView(this.seekBar, LayoutHelper.createFrame(-1, 40.0F, 19, 96.0F, 0.0F, 24.0F, 0.0F));
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(40.0F), NUM));
  }
  
  public void setIconAndTextAndValue(String paramString, float paramFloat, int paramInt1, int paramInt2)
  {
    if (this.valueAnimation != null)
    {
      this.valueAnimation.cancel();
      this.valueAnimation = null;
    }
    AndroidUtilities.cancelRunOnUIThread(this.hideValueRunnable);
    this.valueTextView.setTag(null);
    this.nameTextView.setText(paramString.substring(0, 1).toUpperCase() + paramString.substring(1).toLowerCase());
    if (paramFloat > 0.0F) {
      this.valueTextView.setText("+" + (int)paramFloat);
    }
    for (;;)
    {
      this.valueTextView.setAlpha(0.0F);
      this.nameTextView.setAlpha(1.0F);
      this.seekBar.setMinMax(paramInt1, paramInt2);
      this.seekBar.setProgress((int)paramFloat, false);
      return;
      this.valueTextView.setText("" + (int)paramFloat);
    }
  }
  
  public void setSeekBarDelegate(final PhotoEditorSeekBar.PhotoEditorSeekBarDelegate paramPhotoEditorSeekBarDelegate)
  {
    this.seekBar.setDelegate(new PhotoEditorSeekBar.PhotoEditorSeekBarDelegate()
    {
      public void onProgressChanged(int paramAnonymousInt1, int paramAnonymousInt2)
      {
        paramPhotoEditorSeekBarDelegate.onProgressChanged(paramAnonymousInt1, paramAnonymousInt2);
        if (paramAnonymousInt2 > 0)
        {
          PhotoEditToolCell.this.valueTextView.setText("+" + paramAnonymousInt2);
          if (PhotoEditToolCell.this.valueTextView.getTag() != null) {
            break label256;
          }
          if (PhotoEditToolCell.this.valueAnimation != null) {
            PhotoEditToolCell.this.valueAnimation.cancel();
          }
          PhotoEditToolCell.this.valueTextView.setTag(Integer.valueOf(1));
          PhotoEditToolCell.access$102(PhotoEditToolCell.this, new AnimatorSet());
          PhotoEditToolCell.this.valueAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(PhotoEditToolCell.this.valueTextView, "alpha", new float[] { 1.0F }), ObjectAnimator.ofFloat(PhotoEditToolCell.this.nameTextView, "alpha", new float[] { 0.0F }) });
          PhotoEditToolCell.this.valueAnimation.setDuration(180L);
          PhotoEditToolCell.this.valueAnimation.setInterpolator(new DecelerateInterpolator());
          PhotoEditToolCell.this.valueAnimation.addListener(new AnimatorListenerAdapter()
          {
            public void onAnimationEnd(Animator paramAnonymous2Animator)
            {
              AndroidUtilities.runOnUIThread(PhotoEditToolCell.this.hideValueRunnable, 1000L);
            }
          });
          PhotoEditToolCell.this.valueAnimation.start();
        }
        for (;;)
        {
          return;
          PhotoEditToolCell.this.valueTextView.setText("" + paramAnonymousInt2);
          break;
          label256:
          AndroidUtilities.cancelRunOnUIThread(PhotoEditToolCell.this.hideValueRunnable);
          AndroidUtilities.runOnUIThread(PhotoEditToolCell.this.hideValueRunnable, 1000L);
        }
      }
    });
  }
  
  public void setTag(Object paramObject)
  {
    super.setTag(paramObject);
    this.seekBar.setTag(paramObject);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/PhotoEditToolCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */