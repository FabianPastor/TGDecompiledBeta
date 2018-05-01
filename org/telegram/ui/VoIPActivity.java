package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.media.AudioManager;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.graphics.Palette.Builder;
import android.text.Editable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils.TruncateAt;
import android.text.TextWatcher;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewPropertyAnimator;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.io.ByteArrayOutputStream;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.ImageReceiver.BitmapHolder;
import org.telegram.messenger.ImageReceiver.ImageReceiverDelegate;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.voip.EncryptionKeyEmojifier;
import org.telegram.messenger.voip.VoIPBaseService;
import org.telegram.messenger.voip.VoIPBaseService.StateListener;
import org.telegram.messenger.voip.VoIPController;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.TL_encryptedChat;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.UserProfilePhoto;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.BottomSheet.BottomSheetCell;
import org.telegram.ui.ActionBar.DarkAlertDialog.Builder;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CorrectlyMeasuringTextView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.IdenticonDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.voip.CallSwipeView;
import org.telegram.ui.Components.voip.CallSwipeView.Listener;
import org.telegram.ui.Components.voip.CheckableImageView;
import org.telegram.ui.Components.voip.DarkTheme;
import org.telegram.ui.Components.voip.FabBackgroundDrawable;
import org.telegram.ui.Components.voip.VoIPHelper;

public class VoIPActivity
  extends Activity
  implements NotificationCenter.NotificationCenterDelegate, VoIPBaseService.StateListener
{
  private static final String TAG = "tg-voip-ui";
  private View acceptBtn;
  private CallSwipeView acceptSwipe;
  private TextView accountNameText;
  private ImageView addMemberBtn;
  private ImageView blurOverlayView1;
  private ImageView blurOverlayView2;
  private Bitmap blurredPhoto1;
  private Bitmap blurredPhoto2;
  private LinearLayout bottomButtons;
  private TextView brandingText;
  private int callState;
  private View cancelBtn;
  private ImageView chatBtn;
  private FrameLayout content;
  private Animator currentAcceptAnim;
  private int currentAccount = -1;
  private Animator currentDeclineAnim;
  private View declineBtn;
  private CallSwipeView declineSwipe;
  private boolean didAcceptFromHere = false;
  private TextView durationText;
  private AnimatorSet ellAnimator;
  private TextAlphaSpan[] ellSpans;
  private AnimatorSet emojiAnimator;
  boolean emojiExpanded;
  private TextView emojiExpandedText;
  boolean emojiTooltipVisible;
  private LinearLayout emojiWrap;
  private View endBtn;
  private FabBackgroundDrawable endBtnBg;
  private View endBtnIcon;
  private boolean firstStateChange = true;
  private TextView hintTextView;
  private boolean isIncomingWaiting;
  private ImageView[] keyEmojiViews = new ImageView[4];
  private boolean keyEmojiVisible;
  private String lastStateText;
  private CheckableImageView micToggle;
  private TextView nameText;
  private BackupImageView photoView;
  private AnimatorSet retryAnim;
  private boolean retrying;
  private int signalBarsCount;
  private SignalBarsDrawable signalBarsDrawable;
  private CheckableImageView spkToggle;
  private TextView stateText;
  private TextView stateText2;
  private LinearLayout swipeViewsWrap;
  private Animator textChangingAnim;
  private Animator tooltipAnim;
  private Runnable tooltipHider;
  private TLRPC.User user;
  
  private void callAccepted()
  {
    this.endBtn.setVisibility(0);
    Object localObject;
    label83:
    AnimatorSet localAnimatorSet1;
    AnimatorSet localAnimatorSet2;
    if (VoIPService.getSharedInstance().hasEarpiece())
    {
      this.spkToggle.setVisibility(0);
      this.bottomButtons.setVisibility(0);
      if (!this.didAcceptFromHere) {
        break label332;
      }
      this.acceptBtn.setVisibility(8);
      if (Build.VERSION.SDK_INT < 21) {
        break label292;
      }
      localObject = ObjectAnimator.ofArgb(this.endBtnBg, "color", new int[] { -12207027, -1696188 });
      localAnimatorSet1 = new AnimatorSet();
      localAnimatorSet2 = new AnimatorSet();
      localAnimatorSet2.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.endBtnIcon, "rotation", new float[] { -135.0F, 0.0F }), localObject });
      localAnimatorSet2.setInterpolator(CubicBezierInterpolator.EASE_OUT);
      localAnimatorSet2.setDuration(500L);
      localObject = new AnimatorSet();
      ((AnimatorSet)localObject).playTogether(new Animator[] { ObjectAnimator.ofFloat(this.swipeViewsWrap, "alpha", new float[] { 1.0F, 0.0F }), ObjectAnimator.ofFloat(this.declineBtn, "alpha", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.accountNameText, "alpha", new float[] { 0.0F }) });
      ((AnimatorSet)localObject).setInterpolator(CubicBezierInterpolator.EASE_IN);
      ((AnimatorSet)localObject).setDuration(125L);
      localAnimatorSet1.playTogether(new Animator[] { localAnimatorSet2, localObject });
      localAnimatorSet1.addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          VoIPActivity.this.swipeViewsWrap.setVisibility(8);
          VoIPActivity.this.declineBtn.setVisibility(8);
          VoIPActivity.this.accountNameText.setVisibility(8);
        }
      });
      localAnimatorSet1.start();
    }
    for (;;)
    {
      return;
      this.spkToggle.setVisibility(8);
      break;
      label292:
      localObject = ObjectAnimator.ofInt(this.endBtnBg, "color", new int[] { -12207027, -1696188 });
      ((ObjectAnimator)localObject).setEvaluator(new ArgbEvaluator());
      break label83;
      label332:
      localAnimatorSet2 = new AnimatorSet();
      localObject = new AnimatorSet();
      ((AnimatorSet)localObject).playTogether(new Animator[] { ObjectAnimator.ofFloat(this.bottomButtons, "alpha", new float[] { 0.0F, 1.0F }) });
      ((AnimatorSet)localObject).setInterpolator(CubicBezierInterpolator.EASE_OUT);
      ((AnimatorSet)localObject).setDuration(500L);
      localAnimatorSet1 = new AnimatorSet();
      localAnimatorSet1.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.swipeViewsWrap, "alpha", new float[] { 1.0F, 0.0F }), ObjectAnimator.ofFloat(this.declineBtn, "alpha", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.acceptBtn, "alpha", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.accountNameText, "alpha", new float[] { 0.0F }) });
      localAnimatorSet1.setInterpolator(CubicBezierInterpolator.EASE_IN);
      localAnimatorSet1.setDuration(125L);
      localAnimatorSet2.playTogether(new Animator[] { localObject, localAnimatorSet1 });
      localAnimatorSet2.addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          VoIPActivity.this.swipeViewsWrap.setVisibility(8);
          VoIPActivity.this.declineBtn.setVisibility(8);
          VoIPActivity.this.acceptBtn.setVisibility(8);
          VoIPActivity.this.accountNameText.setVisibility(8);
        }
      });
      localAnimatorSet2.start();
    }
  }
  
  @SuppressLint({"ObjectAnimatorBinding"})
  private ObjectAnimator createAlphaAnimator(Object paramObject, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramObject = ObjectAnimator.ofInt(paramObject, "alpha", new int[] { paramInt1, paramInt2 });
    ((ObjectAnimator)paramObject).setDuration(paramInt4);
    ((ObjectAnimator)paramObject).setStartDelay(paramInt3);
    ((ObjectAnimator)paramObject).setInterpolator(CubicBezierInterpolator.DEFAULT);
    return (ObjectAnimator)paramObject;
  }
  
  private View createContentView()
  {
    FrameLayout local10 = new FrameLayout(this)
    {
      private void setNegativeMargins(Rect paramAnonymousRect, FrameLayout.LayoutParams paramAnonymousLayoutParams)
      {
        paramAnonymousLayoutParams.topMargin = (-paramAnonymousRect.top);
        paramAnonymousLayoutParams.bottomMargin = (-paramAnonymousRect.bottom);
        paramAnonymousLayoutParams.leftMargin = (-paramAnonymousRect.left);
        paramAnonymousLayoutParams.rightMargin = (-paramAnonymousRect.right);
      }
      
      protected boolean fitSystemWindows(Rect paramAnonymousRect)
      {
        setNegativeMargins(paramAnonymousRect, (FrameLayout.LayoutParams)VoIPActivity.this.photoView.getLayoutParams());
        setNegativeMargins(paramAnonymousRect, (FrameLayout.LayoutParams)VoIPActivity.this.blurOverlayView1.getLayoutParams());
        setNegativeMargins(paramAnonymousRect, (FrameLayout.LayoutParams)VoIPActivity.this.blurOverlayView2.getLayoutParams());
        return super.fitSystemWindows(paramAnonymousRect);
      }
    };
    local10.setBackgroundColor(0);
    local10.setFitsSystemWindows(true);
    local10.setClipToPadding(false);
    Object localObject1 = new BackupImageView(this)
    {
      private Drawable bottomGradient = getResources().getDrawable(NUM);
      private Paint paint = new Paint();
      private Drawable topGradient = getResources().getDrawable(NUM);
      
      protected void onDraw(Canvas paramAnonymousCanvas)
      {
        super.onDraw(paramAnonymousCanvas);
        this.paint.setColor(NUM);
        paramAnonymousCanvas.drawRect(0.0F, 0.0F, getWidth(), getHeight(), this.paint);
        this.topGradient.setBounds(0, 0, getWidth(), AndroidUtilities.dp(170.0F));
        this.topGradient.setAlpha(128);
        this.topGradient.draw(paramAnonymousCanvas);
        this.bottomGradient.setBounds(0, getHeight() - AndroidUtilities.dp(220.0F), getWidth(), getHeight());
        this.bottomGradient.setAlpha(178);
        this.bottomGradient.draw(paramAnonymousCanvas);
      }
    };
    this.photoView = ((BackupImageView)localObject1);
    local10.addView((View)localObject1);
    this.blurOverlayView1 = new ImageView(this);
    this.blurOverlayView1.setScaleType(ImageView.ScaleType.CENTER_CROP);
    this.blurOverlayView1.setAlpha(0.0F);
    local10.addView(this.blurOverlayView1);
    this.blurOverlayView2 = new ImageView(this);
    this.blurOverlayView2.setScaleType(ImageView.ScaleType.CENTER_CROP);
    this.blurOverlayView2.setAlpha(0.0F);
    local10.addView(this.blurOverlayView2);
    Object localObject2 = new TextView(this);
    ((TextView)localObject2).setTextColor(-855638017);
    ((TextView)localObject2).setText(LocaleController.getString("VoipInCallBranding", NUM));
    localObject1 = getResources().getDrawable(NUM).mutate();
    ((Drawable)localObject1).setAlpha(204);
    ((Drawable)localObject1).setBounds(0, 0, AndroidUtilities.dp(15.0F), AndroidUtilities.dp(15.0F));
    this.signalBarsDrawable = new SignalBarsDrawable(null);
    this.signalBarsDrawable.setBounds(0, 0, this.signalBarsDrawable.getIntrinsicWidth(), this.signalBarsDrawable.getIntrinsicHeight());
    Object localObject3;
    if (LocaleController.isRTL)
    {
      localObject3 = this.signalBarsDrawable;
      if (!LocaleController.isRTL) {
        break label1879;
      }
      label243:
      ((TextView)localObject2).setCompoundDrawables((Drawable)localObject3, null, (Drawable)localObject1, null);
      ((TextView)localObject2).setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      if (!LocaleController.isRTL) {
        break label1887;
      }
      i = 5;
      label271:
      ((TextView)localObject2).setGravity(i);
      ((TextView)localObject2).setCompoundDrawablePadding(AndroidUtilities.dp(5.0F));
      ((TextView)localObject2).setTextSize(1, 14.0F);
      if (!LocaleController.isRTL) {
        break label1893;
      }
      i = 5;
      label304:
      local10.addView((View)localObject2, LayoutHelper.createFrame(-2, -2.0F, i | 0x30, 18.0F, 18.0F, 18.0F, 0.0F));
      this.brandingText = ((TextView)localObject2);
      localObject1 = new TextView(this);
      ((TextView)localObject1).setSingleLine();
      ((TextView)localObject1).setTextColor(-1);
      ((TextView)localObject1).setTextSize(1, 40.0F);
      ((TextView)localObject1).setEllipsize(TextUtils.TruncateAt.END);
      if (!LocaleController.isRTL) {
        break label1899;
      }
      i = 5;
      label379:
      ((TextView)localObject1).setGravity(i);
      ((TextView)localObject1).setShadowLayer(AndroidUtilities.dp(3.0F), 0.0F, AndroidUtilities.dp(0.6666667F), NUM);
      ((TextView)localObject1).setTypeface(Typeface.create("sans-serif-light", 0));
      this.nameText = ((TextView)localObject1);
      local10.addView((View)localObject1, LayoutHelper.createFrame(-1, -2.0F, 51, 16.0F, 43.0F, 18.0F, 0.0F));
      localObject1 = new TextView(this);
      ((TextView)localObject1).setTextColor(-855638017);
      ((TextView)localObject1).setSingleLine();
      ((TextView)localObject1).setEllipsize(TextUtils.TruncateAt.END);
      ((TextView)localObject1).setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      ((TextView)localObject1).setShadowLayer(AndroidUtilities.dp(3.0F), 0.0F, AndroidUtilities.dp(0.6666667F), NUM);
      ((TextView)localObject1).setTextSize(1, 15.0F);
      if (!LocaleController.isRTL) {
        break label1905;
      }
      i = 5;
      label523:
      ((TextView)localObject1).setGravity(i);
      this.stateText = ((TextView)localObject1);
      local10.addView((View)localObject1, LayoutHelper.createFrame(-1, -2.0F, 51, 18.0F, 98.0F, 18.0F, 0.0F));
      this.durationText = ((TextView)localObject1);
      localObject1 = new TextView(this);
      ((TextView)localObject1).setTextColor(-855638017);
      ((TextView)localObject1).setSingleLine();
      ((TextView)localObject1).setEllipsize(TextUtils.TruncateAt.END);
      ((TextView)localObject1).setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      ((TextView)localObject1).setShadowLayer(AndroidUtilities.dp(3.0F), 0.0F, AndroidUtilities.dp(0.6666667F), NUM);
      ((TextView)localObject1).setTextSize(1, 15.0F);
      if (!LocaleController.isRTL) {
        break label1911;
      }
      i = 5;
      label639:
      ((TextView)localObject1).setGravity(i);
      ((TextView)localObject1).setVisibility(8);
      this.stateText2 = ((TextView)localObject1);
      local10.addView((View)localObject1, LayoutHelper.createFrame(-1, -2.0F, 51, 18.0F, 98.0F, 18.0F, 0.0F));
      this.ellSpans = new TextAlphaSpan[] { new TextAlphaSpan(), new TextAlphaSpan(), new TextAlphaSpan() };
      localObject1 = new LinearLayout(this);
      ((LinearLayout)localObject1).setOrientation(0);
      local10.addView((View)localObject1, LayoutHelper.createFrame(-1, -2, 80));
      localObject3 = new TextView(this);
      ((TextView)localObject3).setTextColor(-855638017);
      ((TextView)localObject3).setSingleLine();
      ((TextView)localObject3).setEllipsize(TextUtils.TruncateAt.END);
      ((TextView)localObject3).setShadowLayer(AndroidUtilities.dp(3.0F), 0.0F, AndroidUtilities.dp(0.6666667F), NUM);
      ((TextView)localObject3).setTextSize(1, 15.0F);
      if (!LocaleController.isRTL) {
        break label1917;
      }
      i = 5;
      label820:
      ((TextView)localObject3).setGravity(i);
      this.accountNameText = ((TextView)localObject3);
      local10.addView((View)localObject3, LayoutHelper.createFrame(-1, -2.0F, 51, 18.0F, 120.0F, 18.0F, 0.0F));
      localObject3 = new CheckableImageView(this);
      ((CheckableImageView)localObject3).setBackgroundResource(NUM);
      localObject2 = getResources().getDrawable(NUM).mutate();
      ((CheckableImageView)localObject3).setAlpha(204);
      ((CheckableImageView)localObject3).setImageDrawable((Drawable)localObject2);
      ((CheckableImageView)localObject3).setScaleType(ImageView.ScaleType.CENTER);
      localObject2 = new FrameLayout(this);
      this.micToggle = ((CheckableImageView)localObject3);
      ((FrameLayout)localObject2).addView((View)localObject3, LayoutHelper.createFrame(38, 38.0F, 81, 0.0F, 0.0F, 0.0F, 10.0F));
      ((LinearLayout)localObject1).addView((View)localObject2, LayoutHelper.createLinear(0, -2, 1.0F));
      localObject3 = new ImageView(this);
      localObject2 = getResources().getDrawable(NUM).mutate();
      ((Drawable)localObject2).setAlpha(204);
      ((ImageView)localObject3).setImageDrawable((Drawable)localObject2);
      ((ImageView)localObject3).setScaleType(ImageView.ScaleType.CENTER);
      localObject2 = new FrameLayout(this);
      this.chatBtn = ((ImageView)localObject3);
      ((FrameLayout)localObject2).addView((View)localObject3, LayoutHelper.createFrame(38, 38.0F, 81, 0.0F, 0.0F, 0.0F, 10.0F));
      ((LinearLayout)localObject1).addView((View)localObject2, LayoutHelper.createLinear(0, -2, 1.0F));
      localObject3 = new CheckableImageView(this);
      ((CheckableImageView)localObject3).setBackgroundResource(NUM);
      localObject2 = getResources().getDrawable(NUM).mutate();
      ((CheckableImageView)localObject3).setAlpha(204);
      ((CheckableImageView)localObject3).setImageDrawable((Drawable)localObject2);
      ((CheckableImageView)localObject3).setScaleType(ImageView.ScaleType.CENTER);
      localObject2 = new FrameLayout(this);
      this.spkToggle = ((CheckableImageView)localObject3);
      ((FrameLayout)localObject2).addView((View)localObject3, LayoutHelper.createFrame(38, 38.0F, 81, 0.0F, 0.0F, 0.0F, 10.0F));
      ((LinearLayout)localObject1).addView((View)localObject2, LayoutHelper.createLinear(0, -2, 1.0F));
      this.bottomButtons = ((LinearLayout)localObject1);
      localObject2 = new LinearLayout(this);
      ((LinearLayout)localObject2).setOrientation(0);
      localObject1 = new CallSwipeView(this);
      ((CallSwipeView)localObject1).setColor(-12207027);
      this.acceptSwipe = ((CallSwipeView)localObject1);
      ((LinearLayout)localObject2).addView((View)localObject1, LayoutHelper.createLinear(-1, 70, 1.0F, 4, 4, -35, 4));
      localObject3 = new CallSwipeView(this);
      ((CallSwipeView)localObject3).setColor(-1696188);
      this.declineSwipe = ((CallSwipeView)localObject3);
      ((LinearLayout)localObject2).addView((View)localObject3, LayoutHelper.createLinear(-1, 70, 1.0F, -35, 4, 4, 4));
      this.swipeViewsWrap = ((LinearLayout)localObject2);
      local10.addView((View)localObject2, LayoutHelper.createFrame(-1, -2.0F, 80, 20.0F, 0.0F, 20.0F, 68.0F));
      localObject2 = new ImageView(this);
      Object localObject4 = new FabBackgroundDrawable();
      ((FabBackgroundDrawable)localObject4).setColor(-12207027);
      ((ImageView)localObject2).setBackgroundDrawable((Drawable)localObject4);
      ((ImageView)localObject2).setImageResource(NUM);
      ((ImageView)localObject2).setScaleType(ImageView.ScaleType.MATRIX);
      localObject4 = new Matrix();
      ((Matrix)localObject4).setTranslate(AndroidUtilities.dp(17.0F), AndroidUtilities.dp(17.0F));
      ((Matrix)localObject4).postRotate(-135.0F, AndroidUtilities.dp(35.0F), AndroidUtilities.dp(35.0F));
      ((ImageView)localObject2).setImageMatrix((Matrix)localObject4);
      this.acceptBtn = ((View)localObject2);
      local10.addView((View)localObject2, LayoutHelper.createFrame(78, 78.0F, 83, 20.0F, 0.0F, 0.0F, 68.0F));
      ImageView localImageView = new ImageView(this);
      localObject4 = new FabBackgroundDrawable();
      ((FabBackgroundDrawable)localObject4).setColor(-1696188);
      localImageView.setBackgroundDrawable((Drawable)localObject4);
      localImageView.setImageResource(NUM);
      localImageView.setScaleType(ImageView.ScaleType.CENTER);
      this.declineBtn = localImageView;
      local10.addView(localImageView, LayoutHelper.createFrame(78, 78.0F, 85, 0.0F, 0.0F, 20.0F, 68.0F));
      ((CallSwipeView)localObject1).setViewToDrag((View)localObject2, false);
      ((CallSwipeView)localObject3).setViewToDrag(localImageView, true);
      localObject1 = new FrameLayout(this);
      localObject3 = new FabBackgroundDrawable();
      ((FabBackgroundDrawable)localObject3).setColor(-1696188);
      this.endBtnBg = ((FabBackgroundDrawable)localObject3);
      ((FrameLayout)localObject1).setBackgroundDrawable((Drawable)localObject3);
      localObject3 = new ImageView(this);
      ((ImageView)localObject3).setImageResource(NUM);
      ((ImageView)localObject3).setScaleType(ImageView.ScaleType.CENTER);
      this.endBtnIcon = ((View)localObject3);
      ((FrameLayout)localObject1).addView((View)localObject3, LayoutHelper.createFrame(70, 70.0F));
      ((FrameLayout)localObject1).setForeground(getResources().getDrawable(NUM));
      this.endBtn = ((View)localObject1);
      local10.addView((View)localObject1, LayoutHelper.createFrame(78, 78.0F, 81, 0.0F, 0.0F, 0.0F, 68.0F));
      localObject3 = new ImageView(this);
      localObject1 = new FabBackgroundDrawable();
      ((FabBackgroundDrawable)localObject1).setColor(-1);
      ((ImageView)localObject3).setBackgroundDrawable((Drawable)localObject1);
      ((ImageView)localObject3).setImageResource(NUM);
      ((ImageView)localObject3).setColorFilter(-NUM);
      ((ImageView)localObject3).setScaleType(ImageView.ScaleType.CENTER);
      ((ImageView)localObject3).setVisibility(8);
      this.cancelBtn = ((View)localObject3);
      local10.addView((View)localObject3, LayoutHelper.createFrame(78, 78.0F, 83, 52.0F, 0.0F, 0.0F, 68.0F));
      this.emojiWrap = new LinearLayout(this);
      this.emojiWrap.setOrientation(0);
      this.emojiWrap.setClipToPadding(false);
      this.emojiWrap.setPivotX(0.0F);
      this.emojiWrap.setPivotY(0.0F);
      this.emojiWrap.setPadding(AndroidUtilities.dp(14.0F), AndroidUtilities.dp(10.0F), AndroidUtilities.dp(14.0F), AndroidUtilities.dp(10.0F));
      i = 0;
      label1803:
      if (i >= 4) {
        break label1931;
      }
      localObject3 = new ImageView(this);
      ((ImageView)localObject3).setScaleType(ImageView.ScaleType.FIT_XY);
      localObject1 = this.emojiWrap;
      if (i != 0) {
        break label1923;
      }
    }
    label1879:
    label1887:
    label1893:
    label1899:
    label1905:
    label1911:
    label1917:
    label1923:
    for (float f = 0.0F;; f = 4.0F)
    {
      ((LinearLayout)localObject1).addView((View)localObject3, LayoutHelper.createLinear(22, 22, f, 0.0F, 0.0F, 0.0F));
      this.keyEmojiViews[i] = localObject3;
      i++;
      break label1803;
      localObject3 = localObject1;
      break;
      localObject1 = this.signalBarsDrawable;
      break label243;
      i = 3;
      break label271;
      i = 3;
      break label304;
      i = 3;
      break label379;
      i = 3;
      break label523;
      i = 3;
      break label639;
      i = 3;
      break label820;
    }
    label1931:
    this.emojiWrap.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        boolean bool = false;
        if (VoIPActivity.this.emojiTooltipVisible)
        {
          VoIPActivity.this.setEmojiTooltipVisible(false);
          if (VoIPActivity.this.tooltipHider != null)
          {
            VoIPActivity.this.hintTextView.removeCallbacks(VoIPActivity.this.tooltipHider);
            VoIPActivity.access$2302(VoIPActivity.this, null);
          }
        }
        paramAnonymousView = VoIPActivity.this;
        if (!VoIPActivity.this.emojiExpanded) {
          bool = true;
        }
        paramAnonymousView.setEmojiExpanded(bool);
      }
    });
    localObject1 = this.emojiWrap;
    if (LocaleController.isRTL) {}
    for (int i = 3;; i = 5)
    {
      local10.addView((View)localObject1, LayoutHelper.createFrame(-2, -2, i | 0x30));
      this.emojiWrap.setOnLongClickListener(new View.OnLongClickListener()
      {
        public boolean onLongClick(View paramAnonymousView)
        {
          boolean bool1 = false;
          boolean bool2 = false;
          if (VoIPActivity.this.emojiExpanded) {}
          for (;;)
          {
            return bool2;
            if (VoIPActivity.this.tooltipHider != null)
            {
              VoIPActivity.this.hintTextView.removeCallbacks(VoIPActivity.this.tooltipHider);
              VoIPActivity.access$2302(VoIPActivity.this, null);
            }
            paramAnonymousView = VoIPActivity.this;
            bool2 = bool1;
            if (!VoIPActivity.this.emojiTooltipVisible) {
              bool2 = true;
            }
            paramAnonymousView.setEmojiTooltipVisible(bool2);
            if (VoIPActivity.this.emojiTooltipVisible) {
              VoIPActivity.this.hintTextView.postDelayed(VoIPActivity.access$2302(VoIPActivity.this, new Runnable()
              {
                public void run()
                {
                  VoIPActivity.access$2302(VoIPActivity.this, null);
                  VoIPActivity.this.setEmojiTooltipVisible(false);
                }
              }), 5000L);
            }
            bool2 = true;
          }
        }
      });
      this.emojiExpandedText = new TextView(this);
      this.emojiExpandedText.setTextSize(1, 16.0F);
      this.emojiExpandedText.setTextColor(-1);
      this.emojiExpandedText.setGravity(17);
      this.emojiExpandedText.setAlpha(0.0F);
      local10.addView(this.emojiExpandedText, LayoutHelper.createFrame(-1, -2.0F, 17, 10.0F, 32.0F, 10.0F, 0.0F));
      this.hintTextView = new CorrectlyMeasuringTextView(this);
      this.hintTextView.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(3.0F), -231525581));
      this.hintTextView.setTextColor(Theme.getColor("chat_gifSaveHintText"));
      this.hintTextView.setTextSize(1, 14.0F);
      this.hintTextView.setPadding(AndroidUtilities.dp(10.0F), AndroidUtilities.dp(10.0F), AndroidUtilities.dp(10.0F), AndroidUtilities.dp(10.0F));
      this.hintTextView.setGravity(17);
      this.hintTextView.setMaxWidth(AndroidUtilities.dp(300.0F));
      this.hintTextView.setAlpha(0.0F);
      local10.addView(this.hintTextView, LayoutHelper.createFrame(-2, -2.0F, 53, 0.0F, 42.0F, 10.0F, 0.0F));
      i = this.stateText.getPaint().getAlpha();
      this.ellAnimator = new AnimatorSet();
      this.ellAnimator.playTogether(new Animator[] { createAlphaAnimator(this.ellSpans[0], 0, i, 0, 300), createAlphaAnimator(this.ellSpans[1], 0, i, 150, 300), createAlphaAnimator(this.ellSpans[2], 0, i, 300, 300), createAlphaAnimator(this.ellSpans[0], i, 0, 1000, 400), createAlphaAnimator(this.ellSpans[1], i, 0, 1000, 400), createAlphaAnimator(this.ellSpans[2], i, 0, 1000, 400) });
      this.ellAnimator.addListener(new AnimatorListenerAdapter()
      {
        private Runnable restarter = new Runnable()
        {
          public void run()
          {
            if (!VoIPActivity.this.isFinishing()) {
              VoIPActivity.this.ellAnimator.start();
            }
          }
        };
        
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          if (!VoIPActivity.this.isFinishing()) {
            VoIPActivity.this.content.postDelayed(this.restarter, 300L);
          }
        }
      });
      local10.setClipChildren(false);
      this.content = local10;
      return local10;
    }
  }
  
  private CharSequence getFormattedDebugString()
  {
    String str1 = VoIPService.getSharedInstance().getDebugString();
    SpannableString localSpannableString = new SpannableString(str1);
    int i = 0;
    int j = str1.indexOf('\n', i + 1);
    int k = j;
    if (j == -1) {
      k = str1.length();
    }
    String str2 = str1.substring(i, k);
    if (str2.contains("IN_USE")) {
      localSpannableString.setSpan(new ForegroundColorSpan(-16711936), i, k, 0);
    }
    for (;;)
    {
      k = str1.indexOf('\n', i + 1);
      i = k;
      if (k != -1) {
        break;
      }
      return localSpannableString;
      if (str2.contains(": ")) {
        localSpannableString.setSpan(new ForegroundColorSpan(-NUM), i, str2.indexOf(':') + i + 1, 0);
      }
    }
  }
  
  private void hideRetry()
  {
    if (this.retryAnim != null) {
      this.retryAnim.cancel();
    }
    this.retrying = false;
    ObjectAnimator localObjectAnimator;
    if (Build.VERSION.SDK_INT >= 21) {
      localObjectAnimator = ObjectAnimator.ofArgb(this.endBtnBg, "color", new int[] { -12207027, -1696188 });
    }
    for (;;)
    {
      AnimatorSet localAnimatorSet = new AnimatorSet();
      localAnimatorSet.playTogether(new Animator[] { localObjectAnimator, ObjectAnimator.ofFloat(this.endBtnIcon, "rotation", new float[] { -135.0F, 0.0F }), ObjectAnimator.ofFloat(this.endBtn, "translationX", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.cancelBtn, "alpha", new float[] { 0.0F }) });
      localAnimatorSet.setStartDelay(200L);
      localAnimatorSet.setDuration(300L);
      localAnimatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
      localAnimatorSet.addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          VoIPActivity.this.cancelBtn.setVisibility(8);
          VoIPActivity.this.endBtn.setEnabled(true);
          VoIPActivity.access$3302(VoIPActivity.this, null);
        }
      });
      this.retryAnim = localAnimatorSet;
      localAnimatorSet.start();
      return;
      localObjectAnimator = ObjectAnimator.ofInt(this.endBtnBg, "color", new int[] { -12207027, -1696188 });
      localObjectAnimator.setEvaluator(new ArgbEvaluator());
    }
  }
  
  private void sendTextMessage(final String paramString)
  {
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        SendMessagesHelper.getInstance(VoIPActivity.this.currentAccount).sendMessage(paramString, VoIPActivity.this.user.id, null, null, false, null, null, null);
      }
    });
  }
  
  private void setEmojiExpanded(boolean paramBoolean)
  {
    if (this.emojiExpanded == paramBoolean) {}
    for (;;)
    {
      return;
      this.emojiExpanded = paramBoolean;
      if (this.emojiAnimator != null) {
        this.emojiAnimator.cancel();
      }
      Object localObject;
      if (paramBoolean)
      {
        localObject = new int[2];
        Object tmp37_36 = localObject;
        tmp37_36[0] = 0;
        Object tmp41_37 = tmp37_36;
        tmp41_37[1] = 0;
        tmp41_37;
        int[] arrayOfInt = new int[2];
        int[] tmp51_50 = arrayOfInt;
        tmp51_50[0] = 0;
        int[] tmp55_51 = tmp51_50;
        tmp55_51[1] = 0;
        tmp55_51;
        this.emojiWrap.getLocationInWindow((int[])localObject);
        this.emojiExpandedText.getLocationInWindow(arrayOfInt);
        Rect localRect = new Rect();
        getWindow().getDecorView().getGlobalVisibleRect(localRect);
        int i = arrayOfInt[1];
        int j = localObject[1];
        int k = this.emojiWrap.getHeight();
        int m = AndroidUtilities.dp(32.0F);
        int n = this.emojiWrap.getHeight();
        int i1 = localRect.width() / 2;
        int i2 = Math.round(this.emojiWrap.getWidth() * 2.5F) / 2;
        int i3 = localObject[0];
        localObject = new AnimatorSet();
        ((AnimatorSet)localObject).playTogether(new Animator[] { ObjectAnimator.ofFloat(this.emojiWrap, "translationY", new float[] { i - (j + k) - m - n }), ObjectAnimator.ofFloat(this.emojiWrap, "translationX", new float[] { i1 - i2 - i3 }), ObjectAnimator.ofFloat(this.emojiWrap, "scaleX", new float[] { 2.5F }), ObjectAnimator.ofFloat(this.emojiWrap, "scaleY", new float[] { 2.5F }), ObjectAnimator.ofFloat(this.blurOverlayView1, "alpha", new float[] { this.blurOverlayView1.getAlpha(), 1.0F, 1.0F }), ObjectAnimator.ofFloat(this.blurOverlayView2, "alpha", new float[] { this.blurOverlayView2.getAlpha(), this.blurOverlayView2.getAlpha(), 1.0F }), ObjectAnimator.ofFloat(this.emojiExpandedText, "alpha", new float[] { 1.0F }) });
        ((AnimatorSet)localObject).setDuration(300L);
        ((AnimatorSet)localObject).setInterpolator(CubicBezierInterpolator.DEFAULT);
        this.emojiAnimator = ((AnimatorSet)localObject);
        ((AnimatorSet)localObject).addListener(new AnimatorListenerAdapter()
        {
          public void onAnimationEnd(Animator paramAnonymousAnimator)
          {
            VoIPActivity.access$5002(VoIPActivity.this, null);
          }
        });
        ((AnimatorSet)localObject).start();
      }
      else
      {
        localObject = new AnimatorSet();
        ((AnimatorSet)localObject).playTogether(new Animator[] { ObjectAnimator.ofFloat(this.emojiWrap, "translationX", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.emojiWrap, "translationY", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.emojiWrap, "scaleX", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.emojiWrap, "scaleY", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.blurOverlayView1, "alpha", new float[] { this.blurOverlayView1.getAlpha(), this.blurOverlayView1.getAlpha(), 0.0F }), ObjectAnimator.ofFloat(this.blurOverlayView2, "alpha", new float[] { this.blurOverlayView2.getAlpha(), 0.0F, 0.0F }), ObjectAnimator.ofFloat(this.emojiExpandedText, "alpha", new float[] { 0.0F }) });
        ((AnimatorSet)localObject).setDuration(300L);
        ((AnimatorSet)localObject).setInterpolator(CubicBezierInterpolator.DEFAULT);
        this.emojiAnimator = ((AnimatorSet)localObject);
        ((AnimatorSet)localObject).addListener(new AnimatorListenerAdapter()
        {
          public void onAnimationEnd(Animator paramAnonymousAnimator)
          {
            VoIPActivity.access$5002(VoIPActivity.this, null);
          }
        });
        ((AnimatorSet)localObject).start();
      }
    }
  }
  
  private void setEmojiTooltipVisible(boolean paramBoolean)
  {
    this.emojiTooltipVisible = paramBoolean;
    if (this.tooltipAnim != null) {
      this.tooltipAnim.cancel();
    }
    this.hintTextView.setVisibility(0);
    Object localObject = this.hintTextView;
    if (paramBoolean) {}
    for (float f = 1.0F;; f = 0.0F)
    {
      localObject = ObjectAnimator.ofFloat(localObject, "alpha", new float[] { f });
      ((ObjectAnimator)localObject).setDuration(300L);
      ((ObjectAnimator)localObject).setInterpolator(CubicBezierInterpolator.DEFAULT);
      ((ObjectAnimator)localObject).addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          VoIPActivity.access$4902(VoIPActivity.this, null);
        }
      });
      this.tooltipAnim = ((Animator)localObject);
      ((ObjectAnimator)localObject).start();
      return;
    }
  }
  
  private void setStateTextAnimated(String paramString, boolean paramBoolean)
  {
    if (paramString.equals(this.lastStateText)) {
      return;
    }
    this.lastStateText = paramString;
    if (this.textChangingAnim != null) {
      this.textChangingAnim.cancel();
    }
    if (paramBoolean)
    {
      if (!this.ellAnimator.isRunning()) {
        this.ellAnimator.start();
      }
      paramString = new SpannableStringBuilder(paramString.toUpperCase());
      Object localObject = this.ellSpans;
      int i = localObject.length;
      for (int j = 0; j < i; j++) {
        localObject[j].setAlpha(0);
      }
      localObject = new SpannableString("...");
      ((SpannableString)localObject).setSpan(this.ellSpans[0], 0, 1, 0);
      ((SpannableString)localObject).setSpan(this.ellSpans[1], 1, 2, 0);
      ((SpannableString)localObject).setSpan(this.ellSpans[2], 2, 3, 0);
      paramString.append((CharSequence)localObject);
      label153:
      this.stateText2.setText(paramString);
      this.stateText2.setVisibility(0);
      paramString = this.stateText;
      if (!LocaleController.isRTL) {
        break label567;
      }
      f = this.stateText.getWidth();
      label190:
      paramString.setPivotX(f);
      this.stateText.setPivotY(this.stateText.getHeight() / 2);
      paramString = this.stateText2;
      if (!LocaleController.isRTL) {
        break label573;
      }
    }
    label567:
    label573:
    for (float f = this.stateText.getWidth();; f = 0.0F)
    {
      paramString.setPivotX(f);
      this.stateText2.setPivotY(this.stateText.getHeight() / 2);
      this.durationText = this.stateText2;
      paramString = new AnimatorSet();
      paramString.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.stateText2, "alpha", new float[] { 0.0F, 1.0F }), ObjectAnimator.ofFloat(this.stateText2, "translationY", new float[] { this.stateText.getHeight() / 2, 0.0F }), ObjectAnimator.ofFloat(this.stateText2, "scaleX", new float[] { 0.7F, 1.0F }), ObjectAnimator.ofFloat(this.stateText2, "scaleY", new float[] { 0.7F, 1.0F }), ObjectAnimator.ofFloat(this.stateText, "alpha", new float[] { 1.0F, 0.0F }), ObjectAnimator.ofFloat(this.stateText, "translationY", new float[] { 0.0F, -this.stateText.getHeight() / 2 }), ObjectAnimator.ofFloat(this.stateText, "scaleX", new float[] { 1.0F, 0.7F }), ObjectAnimator.ofFloat(this.stateText, "scaleY", new float[] { 1.0F, 0.7F }) });
      paramString.setDuration(200L);
      paramString.setInterpolator(CubicBezierInterpolator.DEFAULT);
      paramString.addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          VoIPActivity.access$4702(VoIPActivity.this, null);
          VoIPActivity.this.stateText2.setVisibility(8);
          VoIPActivity.access$3002(VoIPActivity.this, VoIPActivity.this.stateText);
          VoIPActivity.this.stateText.setTranslationY(0.0F);
          VoIPActivity.this.stateText.setScaleX(1.0F);
          VoIPActivity.this.stateText.setScaleY(1.0F);
          VoIPActivity.this.stateText.setAlpha(1.0F);
          VoIPActivity.this.stateText.setText(VoIPActivity.this.stateText2.getText());
        }
      });
      this.textChangingAnim = paramString;
      paramString.start();
      break;
      if (this.ellAnimator.isRunning()) {
        this.ellAnimator.cancel();
      }
      paramString = paramString.toUpperCase();
      break label153;
      f = 0.0F;
      break label190;
    }
  }
  
  private void showDebugAlert()
  {
    if (VoIPService.getSharedInstance() == null) {}
    for (;;)
    {
      return;
      VoIPService.getSharedInstance().forceRating();
      final LinearLayout localLinearLayout = new LinearLayout(this);
      localLinearLayout.setOrientation(1);
      localLinearLayout.setBackgroundColor(-872415232);
      int i = AndroidUtilities.dp(16.0F);
      localLinearLayout.setPadding(i, i * 2, i, i * 2);
      final TextView localTextView = new TextView(this);
      localTextView.setTextColor(-1);
      localTextView.setTextSize(1, 15.0F);
      localTextView.setTypeface(Typeface.DEFAULT_BOLD);
      localTextView.setGravity(17);
      localTextView.setText("libtgvoip v" + VoIPController.getVersion());
      localLinearLayout.addView(localTextView, LayoutHelper.createLinear(-1, -2, 0.0F, 0.0F, 0.0F, 16.0F));
      Object localObject = new ScrollView(this);
      localTextView = new TextView(this);
      localTextView.setTypeface(Typeface.MONOSPACE);
      localTextView.setTextSize(1, 11.0F);
      localTextView.setMaxWidth(AndroidUtilities.dp(350.0F));
      localTextView.setTextColor(-1);
      localTextView.setText(getFormattedDebugString());
      ((ScrollView)localObject).addView(localTextView);
      localLinearLayout.addView((View)localObject, LayoutHelper.createLinear(-1, -1, 1.0F));
      localObject = new TextView(this);
      ((TextView)localObject).setBackgroundColor(-1);
      ((TextView)localObject).setTextColor(-16777216);
      ((TextView)localObject).setPadding(i, i, i, i);
      ((TextView)localObject).setTextSize(1, 15.0F);
      ((TextView)localObject).setText(LocaleController.getString("Close", NUM));
      localLinearLayout.addView((View)localObject, LayoutHelper.createLinear(-2, -2, 1, 0, 16, 0, 0));
      final WindowManager localWindowManager = (WindowManager)getSystemService("window");
      localWindowManager.addView(localLinearLayout, new WindowManager.LayoutParams(-1, -1, 1000, 0, -3));
      ((TextView)localObject).setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          localWindowManager.removeView(localLinearLayout);
        }
      });
      localLinearLayout.postDelayed(new Runnable()
      {
        public void run()
        {
          if ((VoIPActivity.this.isFinishing()) || (VoIPService.getSharedInstance() == null)) {}
          for (;;)
          {
            return;
            localTextView.setText(VoIPActivity.this.getFormattedDebugString());
            localLinearLayout.postDelayed(this, 500L);
          }
        }
      }, 500L);
    }
  }
  
  private void showErrorDialog(CharSequence paramCharSequence)
  {
    paramCharSequence = new DarkAlertDialog.Builder(this).setTitle(LocaleController.getString("VoipFailed", NUM)).setMessage(paramCharSequence).setPositiveButton(LocaleController.getString("OK", NUM), null).show();
    paramCharSequence.setCanceledOnTouchOutside(true);
    paramCharSequence.setOnDismissListener(new DialogInterface.OnDismissListener()
    {
      public void onDismiss(DialogInterface paramAnonymousDialogInterface)
      {
        VoIPActivity.this.finish();
      }
    });
  }
  
  private void showInviteFragment() {}
  
  private void showMessagesSheet()
  {
    if (VoIPService.getSharedInstance() != null) {
      VoIPService.getSharedInstance().stopRinging();
    }
    Object localObject1 = getSharedPreferences("mainconfig", 0);
    final Object localObject2 = new String[4];
    localObject2[0] = ((SharedPreferences)localObject1).getString("quick_reply_msg1", LocaleController.getString("QuickReplyDefault1", NUM));
    localObject2[1] = ((SharedPreferences)localObject1).getString("quick_reply_msg2", LocaleController.getString("QuickReplyDefault2", NUM));
    localObject2[2] = ((SharedPreferences)localObject1).getString("quick_reply_msg3", LocaleController.getString("QuickReplyDefault3", NUM));
    localObject2[3] = ((SharedPreferences)localObject1).getString("quick_reply_msg4", LocaleController.getString("QuickReplyDefault4", NUM));
    localObject1 = new LinearLayout(this);
    ((LinearLayout)localObject1).setOrientation(1);
    final BottomSheet localBottomSheet = new BottomSheet(this, true);
    if (Build.VERSION.SDK_INT >= 21)
    {
      getWindow().setNavigationBarColor(-13948117);
      localBottomSheet.setOnDismissListener(new DialogInterface.OnDismissListener()
      {
        public void onDismiss(DialogInterface paramAnonymousDialogInterface)
        {
          VoIPActivity.this.getWindow().setNavigationBarColor(0);
        }
      });
    }
    Object localObject3 = new View.OnClickListener()
    {
      public void onClick(final View paramAnonymousView)
      {
        localBottomSheet.dismiss();
        if (VoIPService.getSharedInstance() != null) {
          VoIPService.getSharedInstance().declineIncomingCall(4, new Runnable()
          {
            public void run()
            {
              VoIPActivity.this.sendTextMessage((String)paramAnonymousView.getTag());
            }
          });
        }
      }
    };
    int i = localObject2.length;
    final Object localObject4;
    for (int j = 0; j < i; j++)
    {
      localObject4 = localObject2[j];
      localObject5 = new BottomSheet.BottomSheetCell(this, 0);
      ((BottomSheet.BottomSheetCell)localObject5).setTextAndIcon((CharSequence)localObject4, 0);
      ((BottomSheet.BottomSheetCell)localObject5).setTextColor(-1);
      ((BottomSheet.BottomSheetCell)localObject5).setTag(localObject4);
      ((BottomSheet.BottomSheetCell)localObject5).setOnClickListener((View.OnClickListener)localObject3);
      ((LinearLayout)localObject1).addView((View)localObject5);
    }
    localObject3 = new FrameLayout(this);
    final BottomSheet.BottomSheetCell localBottomSheetCell = new BottomSheet.BottomSheetCell(this, 0);
    localBottomSheetCell.setTextAndIcon(LocaleController.getString("QuickReplyCustom", NUM), 0);
    localBottomSheetCell.setTextColor(-1);
    ((FrameLayout)localObject3).addView(localBottomSheetCell);
    final FrameLayout localFrameLayout = new FrameLayout(this);
    final Object localObject5 = new EditText(this);
    ((EditText)localObject5).setTextSize(1, 16.0F);
    ((EditText)localObject5).setTextColor(-1);
    ((EditText)localObject5).setHintTextColor(DarkTheme.getColor("chat_messagePanelHint"));
    ((EditText)localObject5).setBackgroundDrawable(null);
    ((EditText)localObject5).setPadding(AndroidUtilities.dp(16.0F), AndroidUtilities.dp(11.0F), AndroidUtilities.dp(16.0F), AndroidUtilities.dp(12.0F));
    ((EditText)localObject5).setHint(LocaleController.getString("QuickReplyCustom", NUM));
    ((EditText)localObject5).setMinHeight(AndroidUtilities.dp(48.0F));
    ((EditText)localObject5).setGravity(80);
    ((EditText)localObject5).setMaxLines(4);
    ((EditText)localObject5).setSingleLine(false);
    ((EditText)localObject5).setInputType(((EditText)localObject5).getInputType() | 0x4000 | 0x20000);
    float f1;
    label458:
    float f2;
    if (LocaleController.isRTL)
    {
      j = 5;
      if (!LocaleController.isRTL) {
        break label753;
      }
      f1 = 48.0F;
      if (!LocaleController.isRTL) {
        break label759;
      }
      f2 = 0.0F;
      label467:
      localFrameLayout.addView((View)localObject5, LayoutHelper.createFrame(-1, -2.0F, j, f1, 0.0F, f2, 0.0F));
      localObject4 = new ImageView(this);
      ((ImageView)localObject4).setScaleType(ImageView.ScaleType.CENTER);
      ((ImageView)localObject4).setImageDrawable(DarkTheme.getThemedDrawable(this, NUM, "chat_messagePanelSend"));
      if (!LocaleController.isRTL) {
        break label767;
      }
      ((ImageView)localObject4).setScaleX(-0.1F);
      label536:
      ((ImageView)localObject4).setScaleY(0.1F);
      ((ImageView)localObject4).setAlpha(0.0F);
      if (!LocaleController.isRTL) {
        break label778;
      }
      j = 3;
      label559:
      localFrameLayout.addView((View)localObject4, LayoutHelper.createFrame(48, 48, j | 0x50));
      ((ImageView)localObject4).setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          if (localObject5.length() == 0) {}
          for (;;)
          {
            return;
            localBottomSheet.dismiss();
            if (VoIPService.getSharedInstance() != null) {
              VoIPService.getSharedInstance().declineIncomingCall(4, new Runnable()
              {
                public void run()
                {
                  VoIPActivity.this.sendTextMessage(VoIPActivity.34.this.val$field.getText().toString());
                }
              });
            }
          }
        }
      });
      ((ImageView)localObject4).setVisibility(4);
      localObject2 = new ImageView(this);
      ((ImageView)localObject2).setScaleType(ImageView.ScaleType.CENTER);
      ((ImageView)localObject2).setImageDrawable(DarkTheme.getThemedDrawable(this, NUM, "chat_messagePanelIcons"));
      if (!LocaleController.isRTL) {
        break label784;
      }
    }
    label753:
    label759:
    label767:
    label778:
    label784:
    for (j = 3;; j = 5)
    {
      localFrameLayout.addView((View)localObject2, LayoutHelper.createFrame(48, 48, j | 0x50));
      ((ImageView)localObject2).setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          localFrameLayout.setVisibility(8);
          localBottomSheetCell.setVisibility(0);
          localObject5.setText("");
          ((InputMethodManager)VoIPActivity.this.getSystemService("input_method")).hideSoftInputFromWindow(localObject5.getWindowToken(), 0);
        }
      });
      ((EditText)localObject5).addTextChangedListener(new TextWatcher()
      {
        boolean prevState = false;
        
        public void afterTextChanged(Editable paramAnonymousEditable)
        {
          boolean bool;
          if (paramAnonymousEditable.length() > 0)
          {
            bool = true;
            if (this.prevState != bool)
            {
              this.prevState = bool;
              if (!bool) {
                break label139;
              }
              localObject4.setVisibility(0);
              paramAnonymousEditable = localObject4.animate().alpha(1.0F);
              if (!LocaleController.isRTL) {
                break label134;
              }
            }
          }
          label134:
          for (float f = -1.0F;; f = 1.0F)
          {
            paramAnonymousEditable.scaleX(f).scaleY(1.0F).setDuration(200L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
            localObject2.animate().alpha(0.0F).scaleX(0.1F).scaleY(0.1F).setInterpolator(CubicBezierInterpolator.DEFAULT).setDuration(200L).withEndAction(new Runnable()
            {
              public void run()
              {
                VoIPActivity.36.this.val$cancelBtn.setVisibility(4);
              }
            }).start();
            return;
            bool = false;
            break;
          }
          label139:
          localObject2.setVisibility(0);
          localObject2.animate().alpha(1.0F).scaleX(1.0F).scaleY(1.0F).setDuration(200L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
          paramAnonymousEditable = localObject4.animate().alpha(0.0F);
          if (LocaleController.isRTL) {}
          for (f = -0.1F;; f = 0.1F)
          {
            paramAnonymousEditable.scaleX(f).scaleY(0.1F).setInterpolator(CubicBezierInterpolator.DEFAULT).setDuration(200L).withEndAction(new Runnable()
            {
              public void run()
              {
                VoIPActivity.36.this.val$sendBtn.setVisibility(4);
              }
            }).start();
            break;
          }
        }
        
        public void beforeTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
        
        public void onTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
      });
      localFrameLayout.setVisibility(8);
      ((FrameLayout)localObject3).addView(localFrameLayout);
      localBottomSheetCell.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          localFrameLayout.setVisibility(0);
          localBottomSheetCell.setVisibility(4);
          localObject5.requestFocus();
          ((InputMethodManager)VoIPActivity.this.getSystemService("input_method")).showSoftInput(localObject5, 0);
        }
      });
      ((LinearLayout)localObject1).addView((View)localObject3);
      localBottomSheet.setCustomView((View)localObject1);
      localBottomSheet.setBackgroundColor(-13948117);
      localBottomSheet.show();
      return;
      j = 3;
      break;
      f1 = 0.0F;
      break label458;
      f2 = 48.0F;
      break label467;
      ((ImageView)localObject4).setScaleX(0.1F);
      break label536;
      j = 5;
      break label559;
    }
  }
  
  private void showRetry()
  {
    if (this.retryAnim != null) {
      this.retryAnim.cancel();
    }
    this.endBtn.setEnabled(false);
    this.retrying = true;
    this.cancelBtn.setVisibility(0);
    this.cancelBtn.setAlpha(0.0F);
    AnimatorSet localAnimatorSet = new AnimatorSet();
    ObjectAnimator localObjectAnimator;
    if (Build.VERSION.SDK_INT >= 21) {
      localObjectAnimator = ObjectAnimator.ofArgb(this.endBtnBg, "color", new int[] { -1696188, -12207027 });
    }
    for (;;)
    {
      localAnimatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.cancelBtn, "alpha", new float[] { 0.0F, 1.0F }), ObjectAnimator.ofFloat(this.endBtn, "translationX", new float[] { 0.0F, this.content.getWidth() / 2 - AndroidUtilities.dp(52.0F) - this.endBtn.getWidth() / 2 }), localObjectAnimator, ObjectAnimator.ofFloat(this.endBtnIcon, "rotation", new float[] { 0.0F, -135.0F }) });
      localAnimatorSet.setStartDelay(200L);
      localAnimatorSet.setDuration(300L);
      localAnimatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
      localAnimatorSet.addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          VoIPActivity.access$3302(VoIPActivity.this, null);
          VoIPActivity.this.endBtn.setEnabled(true);
        }
      });
      this.retryAnim = localAnimatorSet;
      localAnimatorSet.start();
      return;
      localObjectAnimator = ObjectAnimator.ofInt(this.endBtnBg, "color", new int[] { -1696188, -12207027 });
      localObjectAnimator.setEvaluator(new ArgbEvaluator());
    }
  }
  
  private void startUpdatingCallDuration()
  {
    new Runnable()
    {
      public void run()
      {
        if ((VoIPActivity.this.isFinishing()) || (VoIPService.getSharedInstance() == null)) {
          return;
        }
        long l;
        TextView localTextView;
        if ((VoIPActivity.this.callState == 3) || (VoIPActivity.this.callState == 5))
        {
          l = VoIPService.getSharedInstance().getCallDuration() / 1000L;
          localTextView = VoIPActivity.this.durationText;
          if (l <= 3600L) {
            break label138;
          }
        }
        label138:
        for (String str = String.format("%d:%02d:%02d", new Object[] { Long.valueOf(l / 3600L), Long.valueOf(l % 3600L / 60L), Long.valueOf(l % 60L) });; str = String.format("%d:%02d", new Object[] { Long.valueOf(l / 60L), Long.valueOf(l % 60L) }))
        {
          localTextView.setText(str);
          VoIPActivity.this.durationText.postDelayed(this, 500L);
          break;
          break;
        }
      }
    }.run();
  }
  
  private void updateBlurredPhotos(final ImageReceiver.BitmapHolder paramBitmapHolder)
  {
    new Thread(new Runnable()
    {
      public void run()
      {
        Bitmap localBitmap = Bitmap.createBitmap(150, 150, Bitmap.Config.ARGB_8888);
        Canvas localCanvas = new Canvas(localBitmap);
        localCanvas.drawBitmap(paramBitmapHolder.bitmap, null, new Rect(0, 0, 150, 150), new Paint(2));
        Utilities.blurBitmap(localBitmap, 3, 0, localBitmap.getWidth(), localBitmap.getHeight(), localBitmap.getRowBytes());
        Object localObject = Palette.from(paramBitmapHolder.bitmap).generate();
        Paint localPaint = new Paint();
        localPaint.setColor(((Palette)localObject).getDarkMutedColor(-11242343) & 0xFFFFFF | 0x44000000);
        localCanvas.drawColor(637534208);
        localCanvas.drawRect(0.0F, 0.0F, localCanvas.getWidth(), localCanvas.getHeight(), localPaint);
        localObject = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888);
        localCanvas = new Canvas((Bitmap)localObject);
        localCanvas.drawBitmap(paramBitmapHolder.bitmap, null, new Rect(0, 0, 50, 50), new Paint(2));
        Utilities.blurBitmap(localObject, 3, 0, ((Bitmap)localObject).getWidth(), ((Bitmap)localObject).getHeight(), ((Bitmap)localObject).getRowBytes());
        localPaint.setAlpha(102);
        localCanvas.drawRect(0.0F, 0.0F, localCanvas.getWidth(), localCanvas.getHeight(), localPaint);
        VoIPActivity.access$5102(VoIPActivity.this, localBitmap);
        VoIPActivity.access$5202(VoIPActivity.this, (Bitmap)localObject);
        VoIPActivity.this.runOnUiThread(new Runnable()
        {
          public void run()
          {
            VoIPActivity.this.blurOverlayView1.setImageBitmap(VoIPActivity.this.blurredPhoto1);
            VoIPActivity.this.blurOverlayView2.setImageBitmap(VoIPActivity.this.blurredPhoto2);
            VoIPActivity.30.this.val$src.release();
          }
        });
      }
    }).start();
  }
  
  private void updateKeyView()
  {
    if (VoIPService.getSharedInstance() == null) {}
    for (;;)
    {
      return;
      new IdenticonDrawable().setColors(new int[] { 16777215, -1, -NUM, 872415231 });
      Object localObject1 = new TLRPC.TL_encryptedChat();
      try
      {
        Object localObject2 = new java/io/ByteArrayOutputStream;
        ((ByteArrayOutputStream)localObject2).<init>();
        ((ByteArrayOutputStream)localObject2).write(VoIPService.getSharedInstance().getEncryptionKey());
        ((ByteArrayOutputStream)localObject2).write(VoIPService.getSharedInstance().getGA());
        ((TLRPC.EncryptedChat)localObject1).auth_key = ((ByteArrayOutputStream)localObject2).toByteArray();
        localObject2 = EncryptionKeyEmojifier.emojifyForCall(Utilities.computeSHA256(((TLRPC.EncryptedChat)localObject1).auth_key, 0, ((TLRPC.EncryptedChat)localObject1).auth_key.length));
        for (int i = 0; i < 4; i++)
        {
          localObject1 = Emoji.getEmojiDrawable(localObject2[i]);
          if (localObject1 != null)
          {
            ((Drawable)localObject1).setBounds(0, 0, AndroidUtilities.dp(22.0F), AndroidUtilities.dp(22.0F));
            this.keyEmojiViews[i].setImageDrawable((Drawable)localObject1);
          }
        }
      }
      catch (Exception localException)
      {
        for (;;) {}
      }
    }
  }
  
  public void didReceivedNotification(int paramInt1, int paramInt2, Object... paramVarArgs)
  {
    if (paramInt1 == NotificationCenter.emojiDidLoaded)
    {
      paramVarArgs = this.keyEmojiViews;
      int i = paramVarArgs.length;
      for (paramInt2 = 0; paramInt2 < i; paramInt2++) {
        paramVarArgs[paramInt2].invalidate();
      }
    }
    if (paramInt1 == NotificationCenter.closeInCallActivity) {
      finish();
    }
  }
  
  public void onAudioSettingsChanged()
  {
    VoIPBaseService localVoIPBaseService = VoIPBaseService.getSharedInstance();
    if (localVoIPBaseService == null) {}
    for (;;)
    {
      return;
      this.micToggle.setChecked(localVoIPBaseService.isMicMute());
      if ((!localVoIPBaseService.hasEarpiece()) && (!localVoIPBaseService.isBluetoothHeadsetConnected()))
      {
        this.spkToggle.setVisibility(4);
      }
      else
      {
        this.spkToggle.setVisibility(0);
        if (!localVoIPBaseService.hasEarpiece())
        {
          this.spkToggle.setImageResource(NUM);
          this.spkToggle.setChecked(localVoIPBaseService.isSpeakerphoneOn());
        }
        else
        {
          if (localVoIPBaseService.isBluetoothHeadsetConnected())
          {
            switch (localVoIPBaseService.getCurrentAudioRoute())
            {
            }
            for (;;)
            {
              this.spkToggle.setChecked(false);
              break;
              this.spkToggle.setImageResource(NUM);
              continue;
              this.spkToggle.setImageResource(NUM);
              continue;
              this.spkToggle.setImageResource(NUM);
            }
          }
          this.spkToggle.setImageResource(NUM);
          this.spkToggle.setChecked(localVoIPBaseService.isSpeakerphoneOn());
        }
      }
    }
  }
  
  public void onBackPressed()
  {
    if (this.emojiExpanded) {
      setEmojiExpanded(false);
    }
    for (;;)
    {
      return;
      if (!this.isIncomingWaiting) {
        super.onBackPressed();
      }
    }
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    requestWindowFeature(1);
    getWindow().addFlags(524288);
    super.onCreate(paramBundle);
    if (VoIPService.getSharedInstance() == null) {
      finish();
    }
    for (;;)
    {
      return;
      this.currentAccount = VoIPService.getSharedInstance().getAccount();
      if (this.currentAccount != -1) {
        break;
      }
      finish();
    }
    if ((getResources().getConfiguration().screenLayout & 0xF) < 3) {
      setRequestedOrientation(1);
    }
    paramBundle = createContentView();
    setContentView(paramBundle);
    if (Build.VERSION.SDK_INT >= 21)
    {
      getWindow().addFlags(Integer.MIN_VALUE);
      getWindow().setStatusBarColor(0);
      getWindow().setNavigationBarColor(0);
      getWindow().getDecorView().setSystemUiVisibility(1792);
      label136:
      this.user = VoIPService.getSharedInstance().getUser();
      if (this.user.photo == null) {
        break label547;
      }
      this.photoView.getImageReceiver().setDelegate(new ImageReceiver.ImageReceiverDelegate()
      {
        public void didSetImage(ImageReceiver paramAnonymousImageReceiver, boolean paramAnonymousBoolean1, boolean paramAnonymousBoolean2)
        {
          paramAnonymousImageReceiver = paramAnonymousImageReceiver.getBitmapSafe();
          if (paramAnonymousImageReceiver != null) {
            VoIPActivity.this.updateBlurredPhotos(paramAnonymousImageReceiver);
          }
        }
      });
      this.photoView.setImage(this.user.photo.photo_big, null, new ColorDrawable(-16777216));
      this.photoView.setLayerType(2, null);
    }
    for (;;)
    {
      getWindow().setBackgroundDrawable(new ColorDrawable(0));
      setVolumeControlStream(0);
      this.nameText.setOnClickListener(new View.OnClickListener()
      {
        private int tapCount = 0;
        
        public void onClick(View paramAnonymousView)
        {
          if ((BuildVars.DEBUG_VERSION) || (this.tapCount == 9)) {
            VoIPActivity.this.showDebugAlert();
          }
          for (this.tapCount = 0;; this.tapCount += 1) {
            return;
          }
        }
      });
      this.endBtn.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          VoIPActivity.this.endBtn.setEnabled(false);
          if (VoIPActivity.this.retrying)
          {
            paramAnonymousView = new Intent(VoIPActivity.this, VoIPService.class);
            paramAnonymousView.putExtra("user_id", VoIPActivity.this.user.id);
            paramAnonymousView.putExtra("is_outgoing", true);
            paramAnonymousView.putExtra("start_incall_activity", false);
            paramAnonymousView.putExtra("account", VoIPActivity.this.currentAccount);
          }
          for (;;)
          {
            try
            {
              VoIPActivity.this.startService(paramAnonymousView);
              VoIPActivity.this.hideRetry();
              VoIPActivity.this.endBtn.postDelayed(new Runnable()
              {
                public void run()
                {
                  if ((VoIPService.getSharedInstance() == null) && (!VoIPActivity.this.isFinishing())) {
                    VoIPActivity.this.endBtn.postDelayed(this, 100L);
                  }
                  for (;;)
                  {
                    return;
                    if (VoIPService.getSharedInstance() != null) {
                      VoIPService.getSharedInstance().registerStateListener(VoIPActivity.this);
                    }
                  }
                }
              }, 100L);
              return;
            }
            catch (Throwable paramAnonymousView)
            {
              FileLog.e(paramAnonymousView);
              continue;
            }
            if (VoIPService.getSharedInstance() != null) {
              VoIPService.getSharedInstance().hangUp();
            }
          }
        }
      });
      this.spkToggle.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          paramAnonymousView = VoIPService.getSharedInstance();
          if (paramAnonymousView == null) {}
          for (;;)
          {
            return;
            paramAnonymousView.toggleSpeakerphoneOrShowRouteSheet(VoIPActivity.this);
          }
        }
      });
      this.micToggle.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          if (VoIPService.getSharedInstance() == null)
          {
            VoIPActivity.this.finish();
            return;
          }
          if (!VoIPActivity.this.micToggle.isChecked()) {}
          for (boolean bool = true;; bool = false)
          {
            VoIPActivity.this.micToggle.setChecked(bool);
            VoIPService.getSharedInstance().setMicMute(bool);
            break;
          }
        }
      });
      this.chatBtn.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          if (VoIPActivity.this.isIncomingWaiting) {
            VoIPActivity.this.showMessagesSheet();
          }
          for (;;)
          {
            return;
            paramAnonymousView = new Intent(ApplicationLoader.applicationContext, LaunchActivity.class);
            paramAnonymousView.setAction("com.tmessages.openchat" + Math.random() + Integer.MAX_VALUE);
            paramAnonymousView.putExtra("currentAccount", VoIPActivity.this.currentAccount);
            paramAnonymousView.setFlags(32768);
            paramAnonymousView.putExtra("userId", VoIPActivity.this.user.id);
            VoIPActivity.this.startActivity(paramAnonymousView);
            VoIPActivity.this.finish();
          }
        }
      });
      this.spkToggle.setChecked(((AudioManager)getSystemService("audio")).isSpeakerphoneOn());
      this.micToggle.setChecked(VoIPService.getSharedInstance().isMicMute());
      onAudioSettingsChanged();
      this.nameText.setText(ContactsController.formatName(this.user.first_name, this.user.last_name));
      VoIPService.getSharedInstance().registerStateListener(this);
      this.acceptSwipe.setListener(new CallSwipeView.Listener()
      {
        public void onDragCancel()
        {
          if (VoIPActivity.this.currentDeclineAnim != null) {
            VoIPActivity.this.currentDeclineAnim.cancel();
          }
          AnimatorSet localAnimatorSet = new AnimatorSet();
          localAnimatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(VoIPActivity.this.declineSwipe, "alpha", new float[] { 1.0F }), ObjectAnimator.ofFloat(VoIPActivity.this.declineBtn, "alpha", new float[] { 1.0F }) });
          localAnimatorSet.setDuration(200L);
          localAnimatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
          localAnimatorSet.addListener(new AnimatorListenerAdapter()
          {
            public void onAnimationEnd(Animator paramAnonymous2Animator)
            {
              VoIPActivity.access$1402(VoIPActivity.this, null);
            }
          });
          VoIPActivity.access$1402(VoIPActivity.this, localAnimatorSet);
          localAnimatorSet.start();
          VoIPActivity.this.declineSwipe.startAnimatingArrows();
        }
        
        public void onDragComplete()
        {
          VoIPActivity.this.acceptSwipe.setEnabled(false);
          VoIPActivity.this.declineSwipe.setEnabled(false);
          if (VoIPService.getSharedInstance() == null) {
            VoIPActivity.this.finish();
          }
          for (;;)
          {
            return;
            VoIPActivity.access$1202(VoIPActivity.this, true);
            if ((Build.VERSION.SDK_INT >= 23) && (VoIPActivity.this.checkSelfPermission("android.permission.RECORD_AUDIO") != 0))
            {
              VoIPActivity.this.requestPermissions(new String[] { "android.permission.RECORD_AUDIO" }, 101);
            }
            else
            {
              VoIPService.getSharedInstance().acceptIncomingCall();
              VoIPActivity.this.callAccepted();
            }
          }
        }
        
        public void onDragStart()
        {
          if (VoIPActivity.this.currentDeclineAnim != null) {
            VoIPActivity.this.currentDeclineAnim.cancel();
          }
          AnimatorSet localAnimatorSet = new AnimatorSet();
          localAnimatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(VoIPActivity.this.declineSwipe, "alpha", new float[] { 0.2F }), ObjectAnimator.ofFloat(VoIPActivity.this.declineBtn, "alpha", new float[] { 0.2F }) });
          localAnimatorSet.setDuration(200L);
          localAnimatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
          localAnimatorSet.addListener(new AnimatorListenerAdapter()
          {
            public void onAnimationEnd(Animator paramAnonymous2Animator)
            {
              VoIPActivity.access$1402(VoIPActivity.this, null);
            }
          });
          VoIPActivity.access$1402(VoIPActivity.this, localAnimatorSet);
          localAnimatorSet.start();
          VoIPActivity.this.declineSwipe.stopAnimatingArrows();
        }
      });
      this.declineSwipe.setListener(new CallSwipeView.Listener()
      {
        public void onDragCancel()
        {
          if (VoIPActivity.this.currentAcceptAnim != null) {
            VoIPActivity.this.currentAcceptAnim.cancel();
          }
          AnimatorSet localAnimatorSet = new AnimatorSet();
          localAnimatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(VoIPActivity.this.acceptSwipe, "alpha", new float[] { 1.0F }), ObjectAnimator.ofFloat(VoIPActivity.this.acceptBtn, "alpha", new float[] { 1.0F }) });
          localAnimatorSet.setDuration(200L);
          localAnimatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
          localAnimatorSet.addListener(new AnimatorListenerAdapter()
          {
            public void onAnimationEnd(Animator paramAnonymous2Animator)
            {
              VoIPActivity.access$1602(VoIPActivity.this, null);
            }
          });
          VoIPActivity.access$1602(VoIPActivity.this, localAnimatorSet);
          localAnimatorSet.start();
          VoIPActivity.this.acceptSwipe.startAnimatingArrows();
        }
        
        public void onDragComplete()
        {
          VoIPActivity.this.acceptSwipe.setEnabled(false);
          VoIPActivity.this.declineSwipe.setEnabled(false);
          if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().declineIncomingCall(4, null);
          }
          for (;;)
          {
            return;
            VoIPActivity.this.finish();
          }
        }
        
        public void onDragStart()
        {
          if (VoIPActivity.this.currentAcceptAnim != null) {
            VoIPActivity.this.currentAcceptAnim.cancel();
          }
          AnimatorSet localAnimatorSet = new AnimatorSet();
          localAnimatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(VoIPActivity.this.acceptSwipe, "alpha", new float[] { 0.2F }), ObjectAnimator.ofFloat(VoIPActivity.this.acceptBtn, "alpha", new float[] { 0.2F }) });
          localAnimatorSet.setDuration(200L);
          localAnimatorSet.setInterpolator(new DecelerateInterpolator());
          localAnimatorSet.addListener(new AnimatorListenerAdapter()
          {
            public void onAnimationEnd(Animator paramAnonymous2Animator)
            {
              VoIPActivity.access$1602(VoIPActivity.this, null);
            }
          });
          VoIPActivity.access$1602(VoIPActivity.this, localAnimatorSet);
          localAnimatorSet.start();
          VoIPActivity.this.acceptSwipe.stopAnimatingArrows();
        }
      });
      this.cancelBtn.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          VoIPActivity.this.finish();
        }
      });
      getWindow().getDecorView().setKeepScreenOn(true);
      NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiDidLoaded);
      NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.closeInCallActivity);
      this.hintTextView.setText(LocaleController.formatString("CallEmojiKeyTooltip", NUM, new Object[] { this.user.first_name }));
      this.emojiExpandedText.setText(LocaleController.formatString("CallEmojiKeyTooltip", NUM, new Object[] { this.user.first_name }));
      break;
      if (Build.VERSION.SDK_INT < 19) {
        break label136;
      }
      getWindow().addFlags(201326592);
      getWindow().getDecorView().setSystemUiVisibility(1792);
      break label136;
      label547:
      this.photoView.setVisibility(8);
      paramBundle.setBackgroundDrawable(new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[] { -14994098, -14328963 }));
    }
  }
  
  protected void onDestroy()
  {
    NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiDidLoaded);
    NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.closeInCallActivity);
    if (VoIPService.getSharedInstance() != null) {
      VoIPService.getSharedInstance().unregisterStateListener(this);
    }
    super.onDestroy();
  }
  
  public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
  {
    if ((this.isIncomingWaiting) && ((paramInt == 25) || (paramInt == 24))) {
      if (VoIPService.getSharedInstance() != null) {
        VoIPService.getSharedInstance().stopRinging();
      }
    }
    for (boolean bool = true;; bool = super.onKeyDown(paramInt, paramKeyEvent))
    {
      return bool;
      finish();
      break;
    }
  }
  
  protected void onPause()
  {
    super.onPause();
    if (this.retrying) {
      finish();
    }
    if (VoIPService.getSharedInstance() != null) {
      VoIPService.getSharedInstance().onUIForegroundStateChanged(false);
    }
  }
  
  @TargetApi(23)
  public void onRequestPermissionsResult(int paramInt, String[] paramArrayOfString, int[] paramArrayOfInt)
  {
    if (paramInt == 101)
    {
      if (VoIPService.getSharedInstance() != null) {
        break label17;
      }
      finish();
    }
    for (;;)
    {
      return;
      label17:
      if ((paramArrayOfInt.length > 0) && (paramArrayOfInt[0] == 0))
      {
        VoIPService.getSharedInstance().acceptIncomingCall();
        callAccepted();
      }
      else if (!shouldShowRequestPermissionRationale("android.permission.RECORD_AUDIO"))
      {
        VoIPService.getSharedInstance().declineIncomingCall();
        VoIPHelper.permissionDenied(this, new Runnable()
        {
          public void run()
          {
            VoIPActivity.this.finish();
          }
        });
      }
      else
      {
        this.acceptSwipe.reset();
      }
    }
  }
  
  protected void onResume()
  {
    super.onResume();
    if (VoIPService.getSharedInstance() != null) {
      VoIPService.getSharedInstance().onUIForegroundStateChanged(true);
    }
  }
  
  public void onSignalBarsCountChanged(final int paramInt)
  {
    runOnUiThread(new Runnable()
    {
      public void run()
      {
        VoIPActivity.access$4602(VoIPActivity.this, paramInt);
        VoIPActivity.this.brandingText.invalidate();
      }
    });
  }
  
  public void onStateChanged(final int paramInt)
  {
    final int i = this.callState;
    this.callState = paramInt;
    runOnUiThread(new Runnable()
    {
      public void run()
      {
        boolean bool1 = VoIPActivity.this.firstStateChange;
        boolean bool2;
        if (VoIPActivity.this.firstStateChange)
        {
          VoIPActivity.this.spkToggle.setChecked(((AudioManager)VoIPActivity.this.getSystemService("audio")).isSpeakerphoneOn());
          Object localObject = VoIPActivity.this;
          if (paramInt == 15)
          {
            bool2 = true;
            if (!VoIPActivity.access$802((VoIPActivity)localObject, bool2)) {
              break label337;
            }
            VoIPActivity.this.swipeViewsWrap.setVisibility(0);
            VoIPActivity.this.endBtn.setVisibility(8);
            VoIPActivity.this.acceptSwipe.startAnimatingArrows();
            VoIPActivity.this.declineSwipe.startAnimatingArrows();
            if (UserConfig.getActivatedAccountsCount() <= 1) {
              break label322;
            }
            localObject = UserConfig.getInstance(VoIPActivity.this.currentAccount).getCurrentUser();
            VoIPActivity.this.accountNameText.setText(LocaleController.formatString("VoipAnsweringAsAccount", NUM, new Object[] { ContactsController.formatName(((TLRPC.User)localObject).first_name, ((TLRPC.User)localObject).last_name) }));
            label166:
            VoIPActivity.this.getWindow().addFlags(2097152);
            label178:
            if (paramInt != 3) {
              VoIPActivity.this.emojiWrap.setVisibility(8);
            }
            VoIPActivity.access$3502(VoIPActivity.this, false);
          }
        }
        else
        {
          if ((VoIPActivity.this.isIncomingWaiting) && (paramInt != 15) && (paramInt != 11) && (paramInt != 10))
          {
            VoIPActivity.access$802(VoIPActivity.this, false);
            if (!VoIPActivity.this.didAcceptFromHere) {
              VoIPActivity.this.callAccepted();
            }
          }
          if (paramInt != 15) {
            break label400;
          }
          VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipIncoming", NUM), false);
          VoIPActivity.this.getWindow().addFlags(2097152);
        }
        for (;;)
        {
          VoIPActivity.this.brandingText.invalidate();
          return;
          bool2 = false;
          break;
          label322:
          VoIPActivity.this.accountNameText.setVisibility(8);
          break label166;
          label337:
          VoIPActivity.this.swipeViewsWrap.setVisibility(8);
          VoIPActivity.this.acceptBtn.setVisibility(8);
          VoIPActivity.this.declineBtn.setVisibility(8);
          VoIPActivity.this.accountNameText.setVisibility(8);
          VoIPActivity.this.getWindow().clearFlags(2097152);
          break label178;
          label400:
          if ((paramInt == 1) || (paramInt == 2))
          {
            VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipConnecting", NUM), true);
          }
          else if (paramInt == 12)
          {
            VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipExchangingKeys", NUM), true);
          }
          else if (paramInt == 13)
          {
            VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipWaiting", NUM), true);
          }
          else if (paramInt == 16)
          {
            VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipRinging", NUM), true);
          }
          else if (paramInt == 14)
          {
            VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipRequesting", NUM), true);
          }
          else if (paramInt == 10)
          {
            VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipHangingUp", NUM), true);
            VoIPActivity.this.endBtnIcon.setAlpha(0.5F);
            VoIPActivity.this.endBtn.setEnabled(false);
          }
          else if (paramInt == 11)
          {
            VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipCallEnded", NUM), false);
            VoIPActivity.this.stateText.postDelayed(new Runnable()
            {
              public void run()
              {
                VoIPActivity.this.finish();
              }
            }, 200L);
          }
          else if (paramInt == 17)
          {
            VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipBusy", NUM), false);
            VoIPActivity.this.showRetry();
          }
          else
          {
            int i;
            if ((paramInt == 3) || (paramInt == 5))
            {
              if ((!bool1) && (paramInt == 3))
              {
                i = MessagesController.getGlobalMainSettings().getInt("call_emoji_tooltip_count", 0);
                if (i < 3)
                {
                  VoIPActivity.this.setEmojiTooltipVisible(true);
                  VoIPActivity.this.hintTextView.postDelayed(VoIPActivity.access$2302(VoIPActivity.this, new Runnable()
                  {
                    public void run()
                    {
                      VoIPActivity.access$2302(VoIPActivity.this, null);
                      VoIPActivity.this.setEmojiTooltipVisible(false);
                    }
                  }), 5000L);
                  MessagesController.getGlobalMainSettings().edit().putInt("call_emoji_tooltip_count", i + 1).commit();
                }
              }
              if ((i != 3) && (i != 5))
              {
                VoIPActivity.this.setStateTextAnimated("0:00", false);
                VoIPActivity.this.startUpdatingCallDuration();
                VoIPActivity.this.updateKeyView();
                if (VoIPActivity.this.emojiWrap.getVisibility() != 0)
                {
                  VoIPActivity.this.emojiWrap.setVisibility(0);
                  VoIPActivity.this.emojiWrap.setAlpha(0.0F);
                  VoIPActivity.this.emojiWrap.animate().alpha(1.0F).setDuration(200L).setInterpolator(new DecelerateInterpolator()).start();
                }
              }
            }
            else if (paramInt == 4)
            {
              VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipFailed", NUM), false);
              if (VoIPService.getSharedInstance() != null) {}
              for (i = VoIPService.getSharedInstance().getLastError();; i = 0)
              {
                if (i != 1) {
                  break label1001;
                }
                VoIPActivity.this.showErrorDialog(AndroidUtilities.replaceTags(LocaleController.formatString("VoipPeerIncompatible", NUM, new Object[] { ContactsController.formatName(VoIPActivity.this.user.first_name, VoIPActivity.this.user.last_name) })));
                break;
              }
              label1001:
              if (i == -1) {
                VoIPActivity.this.showErrorDialog(AndroidUtilities.replaceTags(LocaleController.formatString("VoipPeerOutdated", NUM, new Object[] { ContactsController.formatName(VoIPActivity.this.user.first_name, VoIPActivity.this.user.last_name) })));
              } else if (i == -2) {
                VoIPActivity.this.showErrorDialog(AndroidUtilities.replaceTags(LocaleController.formatString("CallNotAvailable", NUM, new Object[] { ContactsController.formatName(VoIPActivity.this.user.first_name, VoIPActivity.this.user.last_name) })));
              } else if (i == 3) {
                VoIPActivity.this.showErrorDialog("Error initializing audio hardware");
              } else if (i == -3) {
                VoIPActivity.this.finish();
              } else if (i == -5) {
                VoIPActivity.this.showErrorDialog(LocaleController.getString("VoipErrorUnknown", NUM));
              } else {
                VoIPActivity.this.stateText.postDelayed(new Runnable()
                {
                  public void run()
                  {
                    VoIPActivity.this.finish();
                  }
                }, 1000L);
              }
            }
          }
        }
      }
    });
  }
  
  private class SignalBarsDrawable
    extends Drawable
  {
    private int[] barHeights = { AndroidUtilities.dp(3.0F), AndroidUtilities.dp(6.0F), AndroidUtilities.dp(9.0F), AndroidUtilities.dp(12.0F) };
    private int offsetStart = 6;
    private Paint paint = new Paint(1);
    private RectF rect = new RectF();
    
    private SignalBarsDrawable() {}
    
    public void draw(Canvas paramCanvas)
    {
      if ((VoIPActivity.this.callState != 3) && (VoIPActivity.this.callState != 5)) {
        return;
      }
      this.paint.setColor(-1);
      int i = getBounds().left;
      float f;
      label47:
      int j;
      int k;
      label66:
      Paint localPaint;
      if (LocaleController.isRTL)
      {
        f = 0.0F;
        j = i + AndroidUtilities.dp(f);
        k = getBounds().top;
        i = 0;
        if (i < 4)
        {
          localPaint = this.paint;
          if (i + 1 > VoIPActivity.this.signalBarsCount) {
            break label199;
          }
        }
      }
      label199:
      for (int m = 242;; m = 102)
      {
        localPaint.setAlpha(m);
        this.rect.set(AndroidUtilities.dp(i * 4) + j, getIntrinsicHeight() + k - this.barHeights[i], AndroidUtilities.dp(4.0F) * i + j + AndroidUtilities.dp(3.0F), getIntrinsicHeight() + k);
        paramCanvas.drawRoundRect(this.rect, AndroidUtilities.dp(0.3F), AndroidUtilities.dp(0.3F), this.paint);
        i++;
        break label66;
        break;
        f = this.offsetStart;
        break label47;
      }
    }
    
    public int getIntrinsicHeight()
    {
      return AndroidUtilities.dp(12.0F);
    }
    
    public int getIntrinsicWidth()
    {
      return AndroidUtilities.dp(this.offsetStart + 15);
    }
    
    public int getOpacity()
    {
      return -3;
    }
    
    public void setAlpha(int paramInt) {}
    
    public void setColorFilter(ColorFilter paramColorFilter) {}
  }
  
  private class TextAlphaSpan
    extends CharacterStyle
  {
    private int alpha = 0;
    
    public TextAlphaSpan() {}
    
    public int getAlpha()
    {
      return this.alpha;
    }
    
    public void setAlpha(int paramInt)
    {
      this.alpha = paramInt;
      VoIPActivity.this.stateText.invalidate();
      VoIPActivity.this.stateText2.invalidate();
    }
    
    public void updateDrawState(TextPaint paramTextPaint)
    {
      paramTextPaint.setAlpha(this.alpha);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/VoIPActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */