package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.text.SpannableStringBuilder;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AnimatorListenerAdapterProxy;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.AudioPlayerActivity;

public class PlayerView
  extends FrameLayout
  implements NotificationCenter.NotificationCenterDelegate
{
  private AnimatorSet animatorSet;
  private BaseFragment fragment;
  private MessageObject lastMessageObject;
  private ImageView playButton;
  private TextView titleTextView;
  private float topPadding;
  private boolean visible;
  private float yPosition;
  
  public PlayerView(Context paramContext, BaseFragment paramBaseFragment)
  {
    super(paramContext);
    this.fragment = paramBaseFragment;
    this.visible = true;
    ((ViewGroup)this.fragment.getFragmentView()).setClipToPadding(false);
    setTag(Integer.valueOf(1));
    paramBaseFragment = new FrameLayout(paramContext);
    paramBaseFragment.setBackgroundColor(-1);
    addView(paramBaseFragment, LayoutHelper.createFrame(-1, 36.0F, 51, 0.0F, 0.0F, 0.0F, 0.0F));
    paramBaseFragment = new View(paramContext);
    paramBaseFragment.setBackgroundResource(2130837693);
    addView(paramBaseFragment, LayoutHelper.createFrame(-1, 3.0F, 51, 0.0F, 36.0F, 0.0F, 0.0F));
    this.playButton = new ImageView(paramContext);
    this.playButton.setScaleType(ImageView.ScaleType.CENTER);
    addView(this.playButton, LayoutHelper.createFrame(36, 36.0F, 51, 0.0F, 0.0F, 0.0F, 0.0F));
    this.playButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        if (MediaController.getInstance().isAudioPaused())
        {
          MediaController.getInstance().playAudio(MediaController.getInstance().getPlayingMessageObject());
          return;
        }
        MediaController.getInstance().pauseAudio(MediaController.getInstance().getPlayingMessageObject());
      }
    });
    this.titleTextView = new TextView(paramContext);
    this.titleTextView.setTextColor(-13683656);
    this.titleTextView.setMaxLines(1);
    this.titleTextView.setLines(1);
    this.titleTextView.setSingleLine(true);
    this.titleTextView.setEllipsize(TextUtils.TruncateAt.END);
    this.titleTextView.setTextSize(1, 15.0F);
    this.titleTextView.setGravity(19);
    addView(this.titleTextView, LayoutHelper.createFrame(-1, 36.0F, 51, 35.0F, 0.0F, 36.0F, 0.0F));
    paramContext = new ImageView(paramContext);
    paramContext.setImageResource(2130837829);
    paramContext.setScaleType(ImageView.ScaleType.CENTER);
    addView(paramContext, LayoutHelper.createFrame(36, 36, 53));
    paramContext.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        MediaController.getInstance().cleanupPlayer(true, true);
      }
    });
    setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        paramAnonymousView = MediaController.getInstance().getPlayingMessageObject();
        if ((paramAnonymousView != null) && (paramAnonymousView.isMusic()) && (PlayerView.this.fragment != null)) {
          PlayerView.this.fragment.presentFragment(new AudioPlayerActivity());
        }
      }
    });
  }
  
  private void checkPlayer(boolean paramBoolean)
  {
    MessageObject localMessageObject = MediaController.getInstance().getPlayingMessageObject();
    Object localObject = this.fragment.getFragmentView();
    boolean bool = paramBoolean;
    if (!paramBoolean)
    {
      bool = paramBoolean;
      if (localObject != null) {
        if (((View)localObject).getParent() != null)
        {
          bool = paramBoolean;
          if (((View)((View)localObject).getParent()).getVisibility() == 0) {}
        }
        else
        {
          bool = true;
        }
      }
    }
    if ((localMessageObject == null) || (localMessageObject.getId() == 0))
    {
      this.lastMessageObject = null;
      if (this.visible)
      {
        this.visible = false;
        if (bool)
        {
          if (getVisibility() != 8) {
            setVisibility(8);
          }
          setTopPadding(0.0F);
        }
      }
      else
      {
        return;
      }
      if (this.animatorSet != null)
      {
        this.animatorSet.cancel();
        this.animatorSet = null;
      }
      this.animatorSet = new AnimatorSet();
      this.animatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(this, "translationY", new float[] { -AndroidUtilities.dp(36.0F) }), ObjectAnimator.ofFloat(this, "topPadding", new float[] { 0.0F }) });
      this.animatorSet.setDuration(200L);
      this.animatorSet.addListener(new AnimatorListenerAdapterProxy()
      {
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          if ((PlayerView.this.animatorSet != null) && (PlayerView.this.animatorSet.equals(paramAnonymousAnimator)))
          {
            PlayerView.this.setVisibility(8);
            PlayerView.access$102(PlayerView.this, null);
          }
        }
      });
      this.animatorSet.start();
      return;
    }
    if ((bool) && (this.topPadding == 0.0F))
    {
      setTopPadding(AndroidUtilities.dp(36.0F));
      setTranslationY(0.0F);
      this.yPosition = 0.0F;
    }
    if (!this.visible)
    {
      if (!bool)
      {
        if (this.animatorSet != null)
        {
          this.animatorSet.cancel();
          this.animatorSet = null;
        }
        this.animatorSet = new AnimatorSet();
        this.animatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(this, "translationY", new float[] { -AndroidUtilities.dp(36.0F), 0.0F }), ObjectAnimator.ofFloat(this, "topPadding", new float[] { AndroidUtilities.dp(36.0F) }) });
        this.animatorSet.setDuration(200L);
        this.animatorSet.addListener(new AnimatorListenerAdapterProxy()
        {
          public void onAnimationEnd(Animator paramAnonymousAnimator)
          {
            if ((PlayerView.this.animatorSet != null) && (PlayerView.this.animatorSet.equals(paramAnonymousAnimator))) {
              PlayerView.access$102(PlayerView.this, null);
            }
          }
        });
        this.animatorSet.start();
      }
      this.visible = true;
      setVisibility(0);
    }
    if (MediaController.getInstance().isAudioPaused())
    {
      this.playButton.setImageResource(2130837831);
      label413:
      if (this.lastMessageObject == localMessageObject) {
        break label533;
      }
      this.lastMessageObject = localMessageObject;
      if (!this.lastMessageObject.isVoice()) {
        break label535;
      }
      localObject = new SpannableStringBuilder(String.format("%s %s", new Object[] { localMessageObject.getMusicAuthor(), localMessageObject.getMusicTitle() }));
      this.titleTextView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
    }
    for (;;)
    {
      ((SpannableStringBuilder)localObject).setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf"), 0, -13683656), 0, localMessageObject.getMusicAuthor().length(), 18);
      this.titleTextView.setText((CharSequence)localObject);
      return;
      this.playButton.setImageResource(2130837830);
      break label413;
      label533:
      break;
      label535:
      localObject = new SpannableStringBuilder(String.format("%s - %s", new Object[] { localMessageObject.getMusicAuthor(), localMessageObject.getMusicTitle() }));
      this.titleTextView.setEllipsize(TextUtils.TruncateAt.END);
    }
  }
  
  public void didReceivedNotification(int paramInt, Object... paramVarArgs)
  {
    if ((paramInt == NotificationCenter.audioDidStarted) || (paramInt == NotificationCenter.audioPlayStateChanged) || (paramInt == NotificationCenter.audioDidReset)) {
      checkPlayer(false);
    }
  }
  
  protected boolean drawChild(Canvas paramCanvas, View paramView, long paramLong)
  {
    int i = paramCanvas.save();
    if (this.yPosition < 0.0F) {
      paramCanvas.clipRect(0, (int)-this.yPosition, paramView.getMeasuredWidth(), AndroidUtilities.dp(39.0F));
    }
    boolean bool = super.drawChild(paramCanvas, paramView, paramLong);
    paramCanvas.restoreToCount(i);
    return bool;
  }
  
  public float getTopPadding()
  {
    return this.topPadding;
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioDidReset);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioPlayStateChanged);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioDidStarted);
    checkPlayer(true);
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    this.topPadding = 0.0F;
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioDidReset);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioPlayStateChanged);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioDidStarted);
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(paramInt1, AndroidUtilities.dp(39.0F));
  }
  
  public void setTopPadding(float paramFloat)
  {
    this.topPadding = paramFloat;
    if (this.fragment != null)
    {
      View localView = this.fragment.getFragmentView();
      if (localView != null) {
        localView.setPadding(0, (int)this.topPadding, 0, 0);
      }
    }
  }
  
  public void setTranslationY(float paramFloat)
  {
    super.setTranslationY(paramFloat);
    this.yPosition = paramFloat;
    invalidate();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/PlayerView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */