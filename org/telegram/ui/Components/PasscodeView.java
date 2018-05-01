package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Vibrator;
import android.support.v4.os.CancellationSignal;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import java.util.ArrayList;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.support.fingerprint.FingerprintManagerCompat.AuthenticationCallback;
import org.telegram.messenger.support.fingerprint.FingerprintManagerCompat.AuthenticationResult;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;

public class PasscodeView
  extends FrameLayout
{
  private static final int id_fingerprint_imageview = 1001;
  private static final int id_fingerprint_textview = 1000;
  private Drawable backgroundDrawable;
  private FrameLayout backgroundFrameLayout;
  private CancellationSignal cancellationSignal;
  private ImageView checkImage;
  private PasscodeViewDelegate delegate;
  private ImageView eraseView;
  private AlertDialog fingerprintDialog;
  private ImageView fingerprintImageView;
  private TextView fingerprintStatusTextView;
  private int keyboardHeight = 0;
  private ArrayList<TextView> lettersTextViews;
  private ArrayList<FrameLayout> numberFrameLayouts;
  private ArrayList<TextView> numberTextViews;
  private FrameLayout numbersFrameLayout;
  private TextView passcodeTextView;
  private EditTextBoldCursor passwordEditText;
  private AnimatingTextView passwordEditText2;
  private FrameLayout passwordFrameLayout;
  private Rect rect = new Rect();
  private boolean selfCancelled;
  
  public PasscodeView(Context paramContext)
  {
    super(paramContext);
    setWillNotDraw(false);
    setVisibility(8);
    this.backgroundFrameLayout = new FrameLayout(paramContext);
    addView(this.backgroundFrameLayout);
    Object localObject1 = (FrameLayout.LayoutParams)this.backgroundFrameLayout.getLayoutParams();
    ((FrameLayout.LayoutParams)localObject1).width = -1;
    ((FrameLayout.LayoutParams)localObject1).height = -1;
    this.backgroundFrameLayout.setLayoutParams((ViewGroup.LayoutParams)localObject1);
    this.passwordFrameLayout = new FrameLayout(paramContext);
    addView(this.passwordFrameLayout);
    localObject1 = (FrameLayout.LayoutParams)this.passwordFrameLayout.getLayoutParams();
    ((FrameLayout.LayoutParams)localObject1).width = -1;
    ((FrameLayout.LayoutParams)localObject1).height = -1;
    ((FrameLayout.LayoutParams)localObject1).gravity = 51;
    this.passwordFrameLayout.setLayoutParams((ViewGroup.LayoutParams)localObject1);
    localObject1 = new ImageView(paramContext);
    ((ImageView)localObject1).setScaleType(ImageView.ScaleType.FIT_XY);
    ((ImageView)localObject1).setImageResource(NUM);
    this.passwordFrameLayout.addView((View)localObject1);
    Object localObject2 = (FrameLayout.LayoutParams)((ImageView)localObject1).getLayoutParams();
    if (AndroidUtilities.density < 1.0F)
    {
      ((FrameLayout.LayoutParams)localObject2).width = AndroidUtilities.dp(30.0F);
      ((FrameLayout.LayoutParams)localObject2).height = AndroidUtilities.dp(30.0F);
      ((FrameLayout.LayoutParams)localObject2).gravity = 81;
      ((FrameLayout.LayoutParams)localObject2).bottomMargin = AndroidUtilities.dp(100.0F);
      ((ImageView)localObject1).setLayoutParams((ViewGroup.LayoutParams)localObject2);
      this.passcodeTextView = new TextView(paramContext);
      this.passcodeTextView.setTextColor(-1);
      this.passcodeTextView.setTextSize(1, 14.0F);
      this.passcodeTextView.setGravity(1);
      this.passwordFrameLayout.addView(this.passcodeTextView);
      localObject1 = (FrameLayout.LayoutParams)this.passcodeTextView.getLayoutParams();
      ((FrameLayout.LayoutParams)localObject1).width = -2;
      ((FrameLayout.LayoutParams)localObject1).height = -2;
      ((FrameLayout.LayoutParams)localObject1).bottomMargin = AndroidUtilities.dp(62.0F);
      ((FrameLayout.LayoutParams)localObject1).gravity = 81;
      this.passcodeTextView.setLayoutParams((ViewGroup.LayoutParams)localObject1);
      this.passwordEditText2 = new AnimatingTextView(paramContext);
      this.passwordFrameLayout.addView(this.passwordEditText2);
      localObject1 = (FrameLayout.LayoutParams)this.passwordEditText2.getLayoutParams();
      ((FrameLayout.LayoutParams)localObject1).height = -2;
      ((FrameLayout.LayoutParams)localObject1).width = -1;
      ((FrameLayout.LayoutParams)localObject1).leftMargin = AndroidUtilities.dp(70.0F);
      ((FrameLayout.LayoutParams)localObject1).rightMargin = AndroidUtilities.dp(70.0F);
      ((FrameLayout.LayoutParams)localObject1).bottomMargin = AndroidUtilities.dp(6.0F);
      ((FrameLayout.LayoutParams)localObject1).gravity = 81;
      this.passwordEditText2.setLayoutParams((ViewGroup.LayoutParams)localObject1);
      this.passwordEditText = new EditTextBoldCursor(paramContext);
      this.passwordEditText.setTextSize(1, 36.0F);
      this.passwordEditText.setTextColor(-1);
      this.passwordEditText.setMaxLines(1);
      this.passwordEditText.setLines(1);
      this.passwordEditText.setGravity(1);
      this.passwordEditText.setSingleLine(true);
      this.passwordEditText.setImeOptions(6);
      this.passwordEditText.setTypeface(Typeface.DEFAULT);
      this.passwordEditText.setBackgroundDrawable(null);
      this.passwordEditText.setCursorColor(-1);
      this.passwordEditText.setCursorSize(AndroidUtilities.dp(32.0F));
      this.passwordFrameLayout.addView(this.passwordEditText);
      localObject1 = (FrameLayout.LayoutParams)this.passwordEditText.getLayoutParams();
      ((FrameLayout.LayoutParams)localObject1).height = -2;
      ((FrameLayout.LayoutParams)localObject1).width = -1;
      ((FrameLayout.LayoutParams)localObject1).leftMargin = AndroidUtilities.dp(70.0F);
      ((FrameLayout.LayoutParams)localObject1).rightMargin = AndroidUtilities.dp(70.0F);
      ((FrameLayout.LayoutParams)localObject1).gravity = 81;
      this.passwordEditText.setLayoutParams((ViewGroup.LayoutParams)localObject1);
      this.passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener()
      {
        public boolean onEditorAction(TextView paramAnonymousTextView, int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
        {
          boolean bool = false;
          if (paramAnonymousInt == 6)
          {
            PasscodeView.this.processDone(false);
            bool = true;
          }
          return bool;
        }
      });
      this.passwordEditText.addTextChangedListener(new TextWatcher()
      {
        public void afterTextChanged(Editable paramAnonymousEditable)
        {
          if ((PasscodeView.this.passwordEditText.length() == 4) && (SharedConfig.passcodeType == 0)) {
            PasscodeView.this.processDone(false);
          }
        }
        
        public void beforeTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
        
        public void onTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
      });
      this.passwordEditText.setCustomSelectionActionModeCallback(new ActionMode.Callback()
      {
        public boolean onActionItemClicked(ActionMode paramAnonymousActionMode, MenuItem paramAnonymousMenuItem)
        {
          return false;
        }
        
        public boolean onCreateActionMode(ActionMode paramAnonymousActionMode, Menu paramAnonymousMenu)
        {
          return false;
        }
        
        public void onDestroyActionMode(ActionMode paramAnonymousActionMode) {}
        
        public boolean onPrepareActionMode(ActionMode paramAnonymousActionMode, Menu paramAnonymousMenu)
        {
          return false;
        }
      });
      this.checkImage = new ImageView(paramContext);
      this.checkImage.setImageResource(NUM);
      this.checkImage.setScaleType(ImageView.ScaleType.CENTER);
      this.checkImage.setBackgroundResource(NUM);
      this.passwordFrameLayout.addView(this.checkImage);
      localObject1 = (FrameLayout.LayoutParams)this.checkImage.getLayoutParams();
      ((FrameLayout.LayoutParams)localObject1).width = AndroidUtilities.dp(60.0F);
      ((FrameLayout.LayoutParams)localObject1).height = AndroidUtilities.dp(60.0F);
      ((FrameLayout.LayoutParams)localObject1).bottomMargin = AndroidUtilities.dp(4.0F);
      ((FrameLayout.LayoutParams)localObject1).rightMargin = AndroidUtilities.dp(10.0F);
      ((FrameLayout.LayoutParams)localObject1).gravity = 85;
      this.checkImage.setLayoutParams((ViewGroup.LayoutParams)localObject1);
      this.checkImage.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          PasscodeView.this.processDone(false);
        }
      });
      localObject2 = new FrameLayout(paramContext);
      ((FrameLayout)localObject2).setBackgroundColor(654311423);
      this.passwordFrameLayout.addView((View)localObject2);
      localObject1 = (FrameLayout.LayoutParams)((FrameLayout)localObject2).getLayoutParams();
      ((FrameLayout.LayoutParams)localObject1).width = -1;
      ((FrameLayout.LayoutParams)localObject1).height = AndroidUtilities.dp(1.0F);
      ((FrameLayout.LayoutParams)localObject1).gravity = 83;
      ((FrameLayout.LayoutParams)localObject1).leftMargin = AndroidUtilities.dp(20.0F);
      ((FrameLayout.LayoutParams)localObject1).rightMargin = AndroidUtilities.dp(20.0F);
      ((FrameLayout)localObject2).setLayoutParams((ViewGroup.LayoutParams)localObject1);
      this.numbersFrameLayout = new FrameLayout(paramContext);
      addView(this.numbersFrameLayout);
      localObject1 = (FrameLayout.LayoutParams)this.numbersFrameLayout.getLayoutParams();
      ((FrameLayout.LayoutParams)localObject1).width = -1;
      ((FrameLayout.LayoutParams)localObject1).height = -1;
      ((FrameLayout.LayoutParams)localObject1).gravity = 51;
      this.numbersFrameLayout.setLayoutParams((ViewGroup.LayoutParams)localObject1);
      this.lettersTextViews = new ArrayList(10);
      this.numberTextViews = new ArrayList(10);
      this.numberFrameLayouts = new ArrayList(10);
      i = 0;
      label927:
      if (i >= 10) {
        break label1304;
      }
      localObject1 = new TextView(paramContext);
      ((TextView)localObject1).setTextColor(-1);
      ((TextView)localObject1).setTextSize(1, 36.0F);
      ((TextView)localObject1).setGravity(17);
      ((TextView)localObject1).setText(String.format(Locale.US, "%d", new Object[] { Integer.valueOf(i) }));
      this.numbersFrameLayout.addView((View)localObject1);
      localObject2 = (FrameLayout.LayoutParams)((TextView)localObject1).getLayoutParams();
      ((FrameLayout.LayoutParams)localObject2).width = AndroidUtilities.dp(50.0F);
      ((FrameLayout.LayoutParams)localObject2).height = AndroidUtilities.dp(50.0F);
      ((FrameLayout.LayoutParams)localObject2).gravity = 51;
      ((TextView)localObject1).setLayoutParams((ViewGroup.LayoutParams)localObject2);
      this.numberTextViews.add(localObject1);
      localObject2 = new TextView(paramContext);
      ((TextView)localObject2).setTextSize(1, 12.0F);
      ((TextView)localObject2).setTextColor(Integer.MAX_VALUE);
      ((TextView)localObject2).setGravity(17);
      this.numbersFrameLayout.addView((View)localObject2);
      localObject1 = (FrameLayout.LayoutParams)((TextView)localObject2).getLayoutParams();
      ((FrameLayout.LayoutParams)localObject1).width = AndroidUtilities.dp(50.0F);
      ((FrameLayout.LayoutParams)localObject1).height = AndroidUtilities.dp(20.0F);
      ((FrameLayout.LayoutParams)localObject1).gravity = 51;
      ((TextView)localObject2).setLayoutParams((ViewGroup.LayoutParams)localObject1);
      switch (i)
      {
      }
    }
    for (;;)
    {
      this.lettersTextViews.add(localObject2);
      i++;
      break label927;
      ((FrameLayout.LayoutParams)localObject2).width = AndroidUtilities.dp(40.0F);
      ((FrameLayout.LayoutParams)localObject2).height = AndroidUtilities.dp(40.0F);
      break;
      ((TextView)localObject2).setText("+");
      continue;
      ((TextView)localObject2).setText("ABC");
      continue;
      ((TextView)localObject2).setText("DEF");
      continue;
      ((TextView)localObject2).setText("GHI");
      continue;
      ((TextView)localObject2).setText("JKL");
      continue;
      ((TextView)localObject2).setText("MNO");
      continue;
      ((TextView)localObject2).setText("PQRS");
      continue;
      ((TextView)localObject2).setText("TUV");
      continue;
      ((TextView)localObject2).setText("WXYZ");
    }
    label1304:
    this.eraseView = new ImageView(paramContext);
    this.eraseView.setScaleType(ImageView.ScaleType.CENTER);
    this.eraseView.setImageResource(NUM);
    this.numbersFrameLayout.addView(this.eraseView);
    localObject1 = (FrameLayout.LayoutParams)this.eraseView.getLayoutParams();
    ((FrameLayout.LayoutParams)localObject1).width = AndroidUtilities.dp(50.0F);
    ((FrameLayout.LayoutParams)localObject1).height = AndroidUtilities.dp(50.0F);
    ((FrameLayout.LayoutParams)localObject1).gravity = 51;
    this.eraseView.setLayoutParams((ViewGroup.LayoutParams)localObject1);
    for (int i = 0; i < 11; i++)
    {
      localObject1 = new FrameLayout(paramContext);
      ((FrameLayout)localObject1).setBackgroundResource(NUM);
      ((FrameLayout)localObject1).setTag(Integer.valueOf(i));
      if (i == 10) {
        ((FrameLayout)localObject1).setOnLongClickListener(new View.OnLongClickListener()
        {
          public boolean onLongClick(View paramAnonymousView)
          {
            PasscodeView.this.passwordEditText.setText("");
            PasscodeView.AnimatingTextView.access$700(PasscodeView.this.passwordEditText2, true);
            return true;
          }
        });
      }
      ((FrameLayout)localObject1).setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          switch (((Integer)paramAnonymousView.getTag()).intValue())
          {
          }
          for (;;)
          {
            if (PasscodeView.this.passwordEditText2.lenght() == 4) {
              PasscodeView.this.processDone(false);
            }
            return;
            PasscodeView.this.passwordEditText2.appendCharacter("0");
            continue;
            PasscodeView.this.passwordEditText2.appendCharacter("1");
            continue;
            PasscodeView.this.passwordEditText2.appendCharacter("2");
            continue;
            PasscodeView.this.passwordEditText2.appendCharacter("3");
            continue;
            PasscodeView.this.passwordEditText2.appendCharacter("4");
            continue;
            PasscodeView.this.passwordEditText2.appendCharacter("5");
            continue;
            PasscodeView.this.passwordEditText2.appendCharacter("6");
            continue;
            PasscodeView.this.passwordEditText2.appendCharacter("7");
            continue;
            PasscodeView.this.passwordEditText2.appendCharacter("8");
            continue;
            PasscodeView.this.passwordEditText2.appendCharacter("9");
            continue;
            PasscodeView.this.passwordEditText2.eraseLastCharacter();
          }
        }
      });
      this.numberFrameLayouts.add(localObject1);
    }
    for (i = 10; i >= 0; i--)
    {
      paramContext = (FrameLayout)this.numberFrameLayouts.get(i);
      this.numbersFrameLayout.addView(paramContext);
      localObject1 = (FrameLayout.LayoutParams)paramContext.getLayoutParams();
      ((FrameLayout.LayoutParams)localObject1).width = AndroidUtilities.dp(100.0F);
      ((FrameLayout.LayoutParams)localObject1).height = AndroidUtilities.dp(100.0F);
      ((FrameLayout.LayoutParams)localObject1).gravity = 51;
      paramContext.setLayoutParams((ViewGroup.LayoutParams)localObject1);
    }
  }
  
  /* Error */
  private void checkFingerprint()
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 415	org/telegram/ui/Components/PasscodeView:getContext	()Landroid/content/Context;
    //   4: checkcast 417	android/app/Activity
    //   7: astore_1
    //   8: getstatic 422	android/os/Build$VERSION:SDK_INT	I
    //   11: bipush 23
    //   13: if_icmplt +38 -> 51
    //   16: aload_1
    //   17: ifnull +34 -> 51
    //   20: getstatic 427	org/telegram/messenger/SharedConfig:useFingerprint	Z
    //   23: ifeq +28 -> 51
    //   26: getstatic 432	org/telegram/messenger/ApplicationLoader:mainInterfacePaused	Z
    //   29: ifne +22 -> 51
    //   32: aload_0
    //   33: getfield 383	org/telegram/ui/Components/PasscodeView:fingerprintDialog	Lorg/telegram/ui/ActionBar/AlertDialog;
    //   36: ifnull +21 -> 57
    //   39: aload_0
    //   40: getfield 383	org/telegram/ui/Components/PasscodeView:fingerprintDialog	Lorg/telegram/ui/ActionBar/AlertDialog;
    //   43: invokevirtual 438	org/telegram/ui/ActionBar/AlertDialog:isShowing	()Z
    //   46: istore_2
    //   47: iload_2
    //   48: ifeq +9 -> 57
    //   51: return
    //   52: astore_1
    //   53: aload_1
    //   54: invokestatic 444	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   57: getstatic 448	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   60: invokestatic 454	org/telegram/messenger/support/fingerprint/FingerprintManagerCompat:from	(Landroid/content/Context;)Lorg/telegram/messenger/support/fingerprint/FingerprintManagerCompat;
    //   63: astore_1
    //   64: aload_1
    //   65: invokevirtual 457	org/telegram/messenger/support/fingerprint/FingerprintManagerCompat:isHardwareDetected	()Z
    //   68: ifeq -17 -> 51
    //   71: aload_1
    //   72: invokevirtual 460	org/telegram/messenger/support/fingerprint/FingerprintManagerCompat:hasEnrolledFingerprints	()Z
    //   75: ifeq -24 -> 51
    //   78: new 462	android/widget/RelativeLayout
    //   81: astore_3
    //   82: aload_3
    //   83: aload_0
    //   84: invokevirtual 415	org/telegram/ui/Components/PasscodeView:getContext	()Landroid/content/Context;
    //   87: invokespecial 463	android/widget/RelativeLayout:<init>	(Landroid/content/Context;)V
    //   90: aload_3
    //   91: ldc_w 464
    //   94: invokestatic 161	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   97: iconst_0
    //   98: ldc_w 464
    //   101: invokestatic 161	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   104: iconst_0
    //   105: invokevirtual 468	android/widget/RelativeLayout:setPadding	(IIII)V
    //   108: new 168	android/widget/TextView
    //   111: astore 4
    //   113: aload 4
    //   115: aload_0
    //   116: invokevirtual 415	org/telegram/ui/Components/PasscodeView:getContext	()Landroid/content/Context;
    //   119: invokespecial 169	android/widget/TextView:<init>	(Landroid/content/Context;)V
    //   122: aload 4
    //   124: sipush 1000
    //   127: invokevirtual 471	android/widget/TextView:setId	(I)V
    //   130: aload 4
    //   132: ldc_w 472
    //   135: invokevirtual 475	android/widget/TextView:setTextAppearance	(I)V
    //   138: aload 4
    //   140: ldc_w 477
    //   143: invokestatic 483	org/telegram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   146: invokevirtual 174	android/widget/TextView:setTextColor	(I)V
    //   149: aload 4
    //   151: ldc_w 485
    //   154: ldc_w 486
    //   157: invokestatic 492	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   160: invokevirtual 322	android/widget/TextView:setText	(Ljava/lang/CharSequence;)V
    //   163: aload_3
    //   164: aload 4
    //   166: invokevirtual 493	android/widget/RelativeLayout:addView	(Landroid/view/View;)V
    //   169: bipush -2
    //   171: bipush -2
    //   173: invokestatic 499	org/telegram/ui/Components/LayoutHelper:createRelative	(II)Landroid/widget/RelativeLayout$LayoutParams;
    //   176: astore 5
    //   178: aload 5
    //   180: bipush 10
    //   182: invokevirtual 504	android/widget/RelativeLayout$LayoutParams:addRule	(I)V
    //   185: aload 5
    //   187: bipush 20
    //   189: invokevirtual 504	android/widget/RelativeLayout$LayoutParams:addRule	(I)V
    //   192: aload 4
    //   194: aload 5
    //   196: invokevirtual 185	android/widget/TextView:setLayoutParams	(Landroid/view/ViewGroup$LayoutParams;)V
    //   199: new 133	android/widget/ImageView
    //   202: astore 5
    //   204: aload 5
    //   206: aload_0
    //   207: invokevirtual 415	org/telegram/ui/Components/PasscodeView:getContext	()Landroid/content/Context;
    //   210: invokespecial 134	android/widget/ImageView:<init>	(Landroid/content/Context;)V
    //   213: aload_0
    //   214: aload 5
    //   216: putfield 506	org/telegram/ui/Components/PasscodeView:fingerprintImageView	Landroid/widget/ImageView;
    //   219: aload_0
    //   220: getfield 506	org/telegram/ui/Components/PasscodeView:fingerprintImageView	Landroid/widget/ImageView;
    //   223: ldc_w 507
    //   226: invokevirtual 148	android/widget/ImageView:setImageResource	(I)V
    //   229: aload_0
    //   230: getfield 506	org/telegram/ui/Components/PasscodeView:fingerprintImageView	Landroid/widget/ImageView;
    //   233: sipush 1001
    //   236: invokevirtual 508	android/widget/ImageView:setId	(I)V
    //   239: aload_3
    //   240: aload_0
    //   241: getfield 506	org/telegram/ui/Components/PasscodeView:fingerprintImageView	Landroid/widget/ImageView;
    //   244: ldc_w 509
    //   247: ldc_w 509
    //   250: iconst_0
    //   251: bipush 20
    //   253: iconst_0
    //   254: iconst_0
    //   255: bipush 20
    //   257: iconst_3
    //   258: sipush 1000
    //   261: invokestatic 512	org/telegram/ui/Components/LayoutHelper:createRelative	(FFIIIIIII)Landroid/widget/RelativeLayout$LayoutParams;
    //   264: invokevirtual 515	android/widget/RelativeLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   267: new 168	android/widget/TextView
    //   270: astore 5
    //   272: aload 5
    //   274: aload_0
    //   275: invokevirtual 415	org/telegram/ui/Components/PasscodeView:getContext	()Landroid/content/Context;
    //   278: invokespecial 169	android/widget/TextView:<init>	(Landroid/content/Context;)V
    //   281: aload_0
    //   282: aload 5
    //   284: putfield 517	org/telegram/ui/Components/PasscodeView:fingerprintStatusTextView	Landroid/widget/TextView;
    //   287: aload_0
    //   288: getfield 517	org/telegram/ui/Components/PasscodeView:fingerprintStatusTextView	Landroid/widget/TextView;
    //   291: bipush 16
    //   293: invokevirtual 182	android/widget/TextView:setGravity	(I)V
    //   296: aload_0
    //   297: getfield 517	org/telegram/ui/Components/PasscodeView:fingerprintStatusTextView	Landroid/widget/TextView;
    //   300: ldc_w 519
    //   303: ldc_w 520
    //   306: invokestatic 492	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   309: invokevirtual 322	android/widget/TextView:setText	(Ljava/lang/CharSequence;)V
    //   312: aload_0
    //   313: getfield 517	org/telegram/ui/Components/PasscodeView:fingerprintStatusTextView	Landroid/widget/TextView;
    //   316: ldc_w 521
    //   319: invokevirtual 475	android/widget/TextView:setTextAppearance	(I)V
    //   322: aload_0
    //   323: getfield 517	org/telegram/ui/Components/PasscodeView:fingerprintStatusTextView	Landroid/widget/TextView;
    //   326: ldc_w 477
    //   329: invokestatic 483	org/telegram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   332: ldc_w 522
    //   335: iand
    //   336: invokevirtual 174	android/widget/TextView:setTextColor	(I)V
    //   339: aload_3
    //   340: aload_0
    //   341: getfield 517	org/telegram/ui/Components/PasscodeView:fingerprintStatusTextView	Landroid/widget/TextView;
    //   344: invokevirtual 493	android/widget/RelativeLayout:addView	(Landroid/view/View;)V
    //   347: bipush -2
    //   349: bipush -2
    //   351: invokestatic 499	org/telegram/ui/Components/LayoutHelper:createRelative	(II)Landroid/widget/RelativeLayout$LayoutParams;
    //   354: astore 5
    //   356: aload 5
    //   358: ldc_w 523
    //   361: invokestatic 161	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   364: invokevirtual 526	android/widget/RelativeLayout$LayoutParams:setMarginStart	(I)V
    //   367: aload 5
    //   369: bipush 8
    //   371: sipush 1001
    //   374: invokevirtual 529	android/widget/RelativeLayout$LayoutParams:addRule	(II)V
    //   377: aload 5
    //   379: bipush 6
    //   381: sipush 1001
    //   384: invokevirtual 529	android/widget/RelativeLayout$LayoutParams:addRule	(II)V
    //   387: aload 5
    //   389: bipush 17
    //   391: sipush 1001
    //   394: invokevirtual 529	android/widget/RelativeLayout$LayoutParams:addRule	(II)V
    //   397: aload_0
    //   398: getfield 517	org/telegram/ui/Components/PasscodeView:fingerprintStatusTextView	Landroid/widget/TextView;
    //   401: aload 5
    //   403: invokevirtual 185	android/widget/TextView:setLayoutParams	(Landroid/view/ViewGroup$LayoutParams;)V
    //   406: new 531	org/telegram/ui/ActionBar/AlertDialog$Builder
    //   409: astore 5
    //   411: aload 5
    //   413: aload_0
    //   414: invokevirtual 415	org/telegram/ui/Components/PasscodeView:getContext	()Landroid/content/Context;
    //   417: invokespecial 532	org/telegram/ui/ActionBar/AlertDialog$Builder:<init>	(Landroid/content/Context;)V
    //   420: aload 5
    //   422: ldc_w 534
    //   425: ldc_w 535
    //   428: invokestatic 492	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   431: invokevirtual 539	org/telegram/ui/ActionBar/AlertDialog$Builder:setTitle	(Ljava/lang/CharSequence;)Lorg/telegram/ui/ActionBar/AlertDialog$Builder;
    //   434: pop
    //   435: aload 5
    //   437: aload_3
    //   438: invokevirtual 543	org/telegram/ui/ActionBar/AlertDialog$Builder:setView	(Landroid/view/View;)Lorg/telegram/ui/ActionBar/AlertDialog$Builder;
    //   441: pop
    //   442: aload 5
    //   444: ldc_w 545
    //   447: ldc_w 546
    //   450: invokestatic 492	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   453: aconst_null
    //   454: invokevirtual 550	org/telegram/ui/ActionBar/AlertDialog$Builder:setNegativeButton	(Ljava/lang/CharSequence;Landroid/content/DialogInterface$OnClickListener;)Lorg/telegram/ui/ActionBar/AlertDialog$Builder;
    //   457: pop
    //   458: new 8	org/telegram/ui/Components/PasscodeView$10
    //   461: astore_3
    //   462: aload_3
    //   463: aload_0
    //   464: invokespecial 551	org/telegram/ui/Components/PasscodeView$10:<init>	(Lorg/telegram/ui/Components/PasscodeView;)V
    //   467: aload 5
    //   469: aload_3
    //   470: invokevirtual 555	org/telegram/ui/ActionBar/AlertDialog$Builder:setOnDismissListener	(Landroid/content/DialogInterface$OnDismissListener;)Lorg/telegram/ui/ActionBar/AlertDialog$Builder;
    //   473: pop
    //   474: aload_0
    //   475: getfield 383	org/telegram/ui/Components/PasscodeView:fingerprintDialog	Lorg/telegram/ui/ActionBar/AlertDialog;
    //   478: astore_3
    //   479: aload_3
    //   480: ifnull +20 -> 500
    //   483: aload_0
    //   484: getfield 383	org/telegram/ui/Components/PasscodeView:fingerprintDialog	Lorg/telegram/ui/ActionBar/AlertDialog;
    //   487: invokevirtual 438	org/telegram/ui/ActionBar/AlertDialog:isShowing	()Z
    //   490: ifeq +10 -> 500
    //   493: aload_0
    //   494: getfield 383	org/telegram/ui/Components/PasscodeView:fingerprintDialog	Lorg/telegram/ui/ActionBar/AlertDialog;
    //   497: invokevirtual 558	org/telegram/ui/ActionBar/AlertDialog:dismiss	()V
    //   500: aload_0
    //   501: aload 5
    //   503: invokevirtual 562	org/telegram/ui/ActionBar/AlertDialog$Builder:show	()Lorg/telegram/ui/ActionBar/AlertDialog;
    //   506: putfield 383	org/telegram/ui/Components/PasscodeView:fingerprintDialog	Lorg/telegram/ui/ActionBar/AlertDialog;
    //   509: new 564	android/support/v4/os/CancellationSignal
    //   512: astore_3
    //   513: aload_3
    //   514: invokespecial 565	android/support/v4/os/CancellationSignal:<init>	()V
    //   517: aload_0
    //   518: aload_3
    //   519: putfield 404	org/telegram/ui/Components/PasscodeView:cancellationSignal	Landroid/support/v4/os/CancellationSignal;
    //   522: aload_0
    //   523: iconst_0
    //   524: putfield 372	org/telegram/ui/Components/PasscodeView:selfCancelled	Z
    //   527: aload_0
    //   528: getfield 404	org/telegram/ui/Components/PasscodeView:cancellationSignal	Landroid/support/v4/os/CancellationSignal;
    //   531: astore 5
    //   533: new 10	org/telegram/ui/Components/PasscodeView$11
    //   536: astore_3
    //   537: aload_3
    //   538: aload_0
    //   539: invokespecial 566	org/telegram/ui/Components/PasscodeView$11:<init>	(Lorg/telegram/ui/Components/PasscodeView;)V
    //   542: aload_1
    //   543: aconst_null
    //   544: iconst_0
    //   545: aload 5
    //   547: aload_3
    //   548: aconst_null
    //   549: invokevirtual 570	org/telegram/messenger/support/fingerprint/FingerprintManagerCompat:authenticate	(Lorg/telegram/messenger/support/fingerprint/FingerprintManagerCompat$CryptoObject;ILandroid/support/v4/os/CancellationSignal;Lorg/telegram/messenger/support/fingerprint/FingerprintManagerCompat$AuthenticationCallback;Landroid/os/Handler;)V
    //   552: goto -501 -> 51
    //   555: astore_1
    //   556: goto -505 -> 51
    //   559: astore_3
    //   560: aload_3
    //   561: invokestatic 444	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   564: goto -64 -> 500
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	567	0	this	PasscodeView
    //   7	10	1	localActivity	Activity
    //   52	2	1	localException1	Exception
    //   63	480	1	localFingerprintManagerCompat	org.telegram.messenger.support.fingerprint.FingerprintManagerCompat
    //   555	1	1	localThrowable	Throwable
    //   46	2	2	bool	boolean
    //   81	467	3	localObject1	Object
    //   559	2	3	localException2	Exception
    //   111	82	4	localTextView	TextView
    //   176	370	5	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   32	47	52	java/lang/Exception
    //   57	479	555	java/lang/Throwable
    //   483	500	555	java/lang/Throwable
    //   500	552	555	java/lang/Throwable
    //   560	564	555	java/lang/Throwable
    //   483	500	559	java/lang/Exception
  }
  
  private void onPasscodeError()
  {
    Vibrator localVibrator = (Vibrator)getContext().getSystemService("vibrator");
    if (localVibrator != null) {
      localVibrator.vibrate(200L);
    }
    shakeTextView(2.0F, 0);
  }
  
  private void processDone(boolean paramBoolean)
  {
    Object localObject;
    if (!paramBoolean)
    {
      localObject = "";
      if (SharedConfig.passcodeType == 0) {
        localObject = this.passwordEditText2.getString();
      }
    }
    label227:
    for (;;)
    {
      if (((String)localObject).length() == 0) {
        onPasscodeError();
      }
      for (;;)
      {
        return;
        if (SharedConfig.passcodeType != 1) {
          break label227;
        }
        localObject = this.passwordEditText.getText().toString();
        break;
        if (!SharedConfig.checkPasscode((String)localObject))
        {
          this.passwordEditText.setText("");
          this.passwordEditText2.eraseAllCharacters(true);
          onPasscodeError();
        }
        else
        {
          this.passwordEditText.clearFocus();
          AndroidUtilities.hideKeyboard(this.passwordEditText);
          localObject = new AnimatorSet();
          ((AnimatorSet)localObject).setDuration(200L);
          ((AnimatorSet)localObject).playTogether(new Animator[] { ObjectAnimator.ofFloat(this, "translationY", new float[] { AndroidUtilities.dp(20.0F) }), ObjectAnimator.ofFloat(this, "alpha", new float[] { AndroidUtilities.dp(0.0F) }) });
          ((AnimatorSet)localObject).addListener(new AnimatorListenerAdapter()
          {
            public void onAnimationEnd(Animator paramAnonymousAnimator)
            {
              PasscodeView.this.setVisibility(8);
            }
          });
          ((AnimatorSet)localObject).start();
          SharedConfig.appLocked = false;
          SharedConfig.saveConfig();
          NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetPasscode, new Object[0]);
          setOnTouchListener(null);
          if (this.delegate != null) {
            this.delegate.didAcceptedPassword();
          }
        }
      }
    }
  }
  
  private void shakeTextView(final float paramFloat, final int paramInt)
  {
    if (paramInt == 6) {}
    for (;;)
    {
      return;
      AnimatorSet localAnimatorSet = new AnimatorSet();
      localAnimatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.passcodeTextView, "translationX", new float[] { AndroidUtilities.dp(paramFloat) }) });
      localAnimatorSet.setDuration(50L);
      localAnimatorSet.addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          paramAnonymousAnimator = PasscodeView.this;
          if (paramInt == 5) {}
          for (float f = 0.0F;; f = -paramFloat)
          {
            paramAnonymousAnimator.shakeTextView(f, paramInt + 1);
            return;
          }
        }
      });
      localAnimatorSet.start();
    }
  }
  
  private void showFingerprintError(CharSequence paramCharSequence)
  {
    this.fingerprintImageView.setImageResource(NUM);
    this.fingerprintStatusTextView.setText(paramCharSequence);
    this.fingerprintStatusTextView.setTextColor(-765666);
    paramCharSequence = (Vibrator)getContext().getSystemService("vibrator");
    if (paramCharSequence != null) {
      paramCharSequence.vibrate(200L);
    }
    AndroidUtilities.shakeView(this.fingerprintStatusTextView, 2.0F, 0);
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    if (getVisibility() != 0) {}
    for (;;)
    {
      return;
      if (this.backgroundDrawable != null)
      {
        if ((this.backgroundDrawable instanceof ColorDrawable))
        {
          this.backgroundDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
          this.backgroundDrawable.draw(paramCanvas);
        }
        else
        {
          float f1 = getMeasuredWidth() / this.backgroundDrawable.getIntrinsicWidth();
          float f2 = (getMeasuredHeight() + this.keyboardHeight) / this.backgroundDrawable.getIntrinsicHeight();
          if (f1 < f2) {}
          for (;;)
          {
            int i = (int)Math.ceil(this.backgroundDrawable.getIntrinsicWidth() * f2);
            int j = (int)Math.ceil(this.backgroundDrawable.getIntrinsicHeight() * f2);
            int k = (getMeasuredWidth() - i) / 2;
            int m = (getMeasuredHeight() - j + this.keyboardHeight) / 2;
            this.backgroundDrawable.setBounds(k, m, k + i, m + j);
            this.backgroundDrawable.draw(paramCanvas);
            break;
            f2 = f1;
          }
        }
      }
      else {
        super.onDraw(paramCanvas);
      }
    }
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    Object localObject = getRootView();
    int i = ((View)localObject).getHeight();
    int j = AndroidUtilities.statusBarHeight;
    int k = AndroidUtilities.getViewInset((View)localObject);
    getWindowVisibleDisplayFrame(this.rect);
    this.keyboardHeight = (i - j - k - (this.rect.bottom - this.rect.top));
    int m;
    if ((SharedConfig.passcodeType == 1) && ((AndroidUtilities.isTablet()) || (getContext().getResources().getConfiguration().orientation != 2)))
    {
      j = 0;
      if (this.passwordFrameLayout.getTag() != null) {
        j = ((Integer)this.passwordFrameLayout.getTag()).intValue();
      }
      localObject = (FrameLayout.LayoutParams)this.passwordFrameLayout.getLayoutParams();
      m = ((FrameLayout.LayoutParams)localObject).height;
      i = this.keyboardHeight / 2;
      if (Build.VERSION.SDK_INT < 21) {
        break label196;
      }
    }
    label196:
    for (k = AndroidUtilities.statusBarHeight;; k = 0)
    {
      ((FrameLayout.LayoutParams)localObject).topMargin = (m + j - i - k);
      this.passwordFrameLayout.setLayoutParams((ViewGroup.LayoutParams)localObject);
      super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
      return;
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int i = View.MeasureSpec.getSize(paramInt1);
    int j = AndroidUtilities.displaySize.y;
    int k;
    int m;
    FrameLayout.LayoutParams localLayoutParams1;
    label77:
    label177:
    int n;
    label216:
    label231:
    int i1;
    int i2;
    Object localObject;
    FrameLayout.LayoutParams localLayoutParams2;
    if (Build.VERSION.SDK_INT >= 21)
    {
      k = 0;
      m = j - k;
      if ((AndroidUtilities.isTablet()) || (getContext().getResources().getConfiguration().orientation != 2)) {
        break label467;
      }
      localLayoutParams1 = (FrameLayout.LayoutParams)this.passwordFrameLayout.getLayoutParams();
      if (SharedConfig.passcodeType != 0) {
        break label461;
      }
      k = i / 2;
      localLayoutParams1.width = k;
      localLayoutParams1.height = AndroidUtilities.dp(140.0F);
      localLayoutParams1.topMargin = ((m - AndroidUtilities.dp(140.0F)) / 2);
      this.passwordFrameLayout.setLayoutParams(localLayoutParams1);
      localLayoutParams1 = (FrameLayout.LayoutParams)this.numbersFrameLayout.getLayoutParams();
      localLayoutParams1.height = m;
      localLayoutParams1.leftMargin = (i / 2);
      localLayoutParams1.topMargin = (m - localLayoutParams1.height);
      localLayoutParams1.width = (i / 2);
      this.numbersFrameLayout.setLayoutParams(localLayoutParams1);
      n = (localLayoutParams1.width - AndroidUtilities.dp(50.0F) * 3) / 4;
      j = (localLayoutParams1.height - AndroidUtilities.dp(50.0F) * 4) / 5;
      k = 0;
      if (k >= 11) {
        break label813;
      }
      if (k != 0) {
        break label710;
      }
      i = 10;
      i1 = i / 3;
      i2 = i % 3;
      if (k >= 10) {
        break label731;
      }
      localObject = (TextView)this.numberTextViews.get(k);
      TextView localTextView = (TextView)this.lettersTextViews.get(k);
      localLayoutParams1 = (FrameLayout.LayoutParams)((TextView)localObject).getLayoutParams();
      localLayoutParams2 = (FrameLayout.LayoutParams)localTextView.getLayoutParams();
      i = j + (AndroidUtilities.dp(50.0F) + j) * i1;
      localLayoutParams1.topMargin = i;
      localLayoutParams2.topMargin = i;
      i2 = (AndroidUtilities.dp(50.0F) + n) * i2 + n;
      localLayoutParams1.leftMargin = i2;
      localLayoutParams2.leftMargin = i2;
      localLayoutParams2.topMargin += AndroidUtilities.dp(40.0F);
      ((TextView)localObject).setLayoutParams(localLayoutParams1);
      localTextView.setLayoutParams(localLayoutParams2);
    }
    for (;;)
    {
      localObject = (FrameLayout)this.numberFrameLayouts.get(k);
      localLayoutParams2 = (FrameLayout.LayoutParams)((FrameLayout)localObject).getLayoutParams();
      localLayoutParams2.topMargin = (i - AndroidUtilities.dp(17.0F));
      localLayoutParams1.leftMargin -= AndroidUtilities.dp(25.0F);
      ((FrameLayout)localObject).setLayoutParams(localLayoutParams2);
      k++;
      break label216;
      k = AndroidUtilities.statusBarHeight;
      break;
      label461:
      k = i;
      break label77;
      label467:
      int i3 = 0;
      int i4 = 0;
      j = 0;
      i2 = m;
      i1 = i3;
      n = i;
      if (AndroidUtilities.isTablet())
      {
        k = i;
        if (i > AndroidUtilities.dp(498.0F))
        {
          j = (i - AndroidUtilities.dp(498.0F)) / 2;
          k = AndroidUtilities.dp(498.0F);
        }
        i2 = m;
        i4 = j;
        i1 = i3;
        n = k;
        if (m > AndroidUtilities.dp(528.0F))
        {
          i1 = (m - AndroidUtilities.dp(528.0F)) / 2;
          i2 = AndroidUtilities.dp(528.0F);
          n = k;
          i4 = j;
        }
      }
      localLayoutParams1 = (FrameLayout.LayoutParams)this.passwordFrameLayout.getLayoutParams();
      localLayoutParams1.height = (i2 / 3);
      localLayoutParams1.width = n;
      localLayoutParams1.topMargin = i1;
      localLayoutParams1.leftMargin = i4;
      this.passwordFrameLayout.setTag(Integer.valueOf(i1));
      this.passwordFrameLayout.setLayoutParams(localLayoutParams1);
      localLayoutParams1 = (FrameLayout.LayoutParams)this.numbersFrameLayout.getLayoutParams();
      localLayoutParams1.height = (i2 / 3 * 2);
      localLayoutParams1.leftMargin = i4;
      localLayoutParams1.topMargin = (i2 - localLayoutParams1.height + i1);
      localLayoutParams1.width = n;
      this.numbersFrameLayout.setLayoutParams(localLayoutParams1);
      break label177;
      label710:
      if (k == 10)
      {
        i = 11;
        break label231;
      }
      i = k - 1;
      break label231;
      label731:
      localLayoutParams1 = (FrameLayout.LayoutParams)this.eraseView.getLayoutParams();
      i = (AndroidUtilities.dp(50.0F) + j) * i1 + j + AndroidUtilities.dp(8.0F);
      localLayoutParams1.topMargin = i;
      localLayoutParams1.leftMargin = ((AndroidUtilities.dp(50.0F) + n) * i2 + n);
      i -= AndroidUtilities.dp(8.0F);
      this.eraseView.setLayoutParams(localLayoutParams1);
    }
    label813:
    super.onMeasure(paramInt1, paramInt2);
  }
  
  /* Error */
  public void onPause()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 383	org/telegram/ui/Components/PasscodeView:fingerprintDialog	Lorg/telegram/ui/ActionBar/AlertDialog;
    //   4: ifnull +25 -> 29
    //   7: aload_0
    //   8: getfield 383	org/telegram/ui/Components/PasscodeView:fingerprintDialog	Lorg/telegram/ui/ActionBar/AlertDialog;
    //   11: invokevirtual 438	org/telegram/ui/ActionBar/AlertDialog:isShowing	()Z
    //   14: ifeq +10 -> 24
    //   17: aload_0
    //   18: getfield 383	org/telegram/ui/Components/PasscodeView:fingerprintDialog	Lorg/telegram/ui/ActionBar/AlertDialog;
    //   21: invokevirtual 558	org/telegram/ui/ActionBar/AlertDialog:dismiss	()V
    //   24: aload_0
    //   25: aconst_null
    //   26: putfield 383	org/telegram/ui/Components/PasscodeView:fingerprintDialog	Lorg/telegram/ui/ActionBar/AlertDialog;
    //   29: getstatic 422	android/os/Build$VERSION:SDK_INT	I
    //   32: bipush 23
    //   34: if_icmplt +22 -> 56
    //   37: aload_0
    //   38: getfield 404	org/telegram/ui/Components/PasscodeView:cancellationSignal	Landroid/support/v4/os/CancellationSignal;
    //   41: ifnull +15 -> 56
    //   44: aload_0
    //   45: getfield 404	org/telegram/ui/Components/PasscodeView:cancellationSignal	Landroid/support/v4/os/CancellationSignal;
    //   48: invokevirtual 818	android/support/v4/os/CancellationSignal:cancel	()V
    //   51: aload_0
    //   52: aconst_null
    //   53: putfield 404	org/telegram/ui/Components/PasscodeView:cancellationSignal	Landroid/support/v4/os/CancellationSignal;
    //   56: return
    //   57: astore_1
    //   58: aload_1
    //   59: invokestatic 444	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   62: goto -33 -> 29
    //   65: astore_1
    //   66: aload_1
    //   67: invokestatic 444	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   70: goto -14 -> 56
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	73	0	this	PasscodeView
    //   57	2	1	localException1	Exception
    //   65	2	1	localException2	Exception
    // Exception table:
    //   from	to	target	type
    //   7	24	57	java/lang/Exception
    //   24	29	57	java/lang/Exception
    //   29	56	65	java/lang/Exception
  }
  
  public void onResume()
  {
    if (SharedConfig.passcodeType == 1)
    {
      if (this.passwordEditText != null)
      {
        this.passwordEditText.requestFocus();
        AndroidUtilities.showKeyboard(this.passwordEditText);
      }
      AndroidUtilities.runOnUIThread(new Runnable()
      {
        public void run()
        {
          if (PasscodeView.this.passwordEditText != null)
          {
            PasscodeView.this.passwordEditText.requestFocus();
            AndroidUtilities.showKeyboard(PasscodeView.this.passwordEditText);
          }
        }
      }, 200L);
    }
    checkFingerprint();
  }
  
  public void onShow()
  {
    Object localObject = (Activity)getContext();
    if (SharedConfig.passcodeType == 1) {
      if (this.passwordEditText != null)
      {
        this.passwordEditText.requestFocus();
        AndroidUtilities.showKeyboard(this.passwordEditText);
      }
    }
    for (;;)
    {
      checkFingerprint();
      if (getVisibility() != 0) {
        break;
      }
      label48:
      return;
      if (localObject == null) {
        break label217;
      }
      localObject = ((Activity)localObject).getCurrentFocus();
      if (localObject != null)
      {
        ((View)localObject).clearFocus();
        AndroidUtilities.hideKeyboard(((Activity)getContext()).getCurrentFocus());
      }
    }
    setAlpha(1.0F);
    setTranslationY(0.0F);
    if (Theme.isCustomTheme())
    {
      this.backgroundDrawable = Theme.getCachedWallpaper();
      this.backgroundFrameLayout.setBackgroundColor(-NUM);
      label115:
      this.passcodeTextView.setText(LocaleController.getString("EnterYourPasscode", NUM));
      if (SharedConfig.passcodeType != 0) {
        break label292;
      }
      this.numbersFrameLayout.setVisibility(0);
      this.passwordEditText.setVisibility(8);
      this.passwordEditText2.setVisibility(0);
      this.checkImage.setVisibility(8);
    }
    for (;;)
    {
      setVisibility(0);
      this.passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
      this.passwordEditText.setText("");
      this.passwordEditText2.eraseAllCharacters(false);
      setOnTouchListener(new View.OnTouchListener()
      {
        public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
        {
          return true;
        }
      });
      break label48;
      label217:
      break;
      if (MessagesController.getGlobalMainSettings().getInt("selectedBackground", 1000001) == 1000001)
      {
        this.backgroundFrameLayout.setBackgroundColor(-11436898);
        break label115;
      }
      this.backgroundDrawable = Theme.getCachedWallpaper();
      if (this.backgroundDrawable != null)
      {
        this.backgroundFrameLayout.setBackgroundColor(-NUM);
        break label115;
      }
      this.backgroundFrameLayout.setBackgroundColor(-11436898);
      break label115;
      label292:
      if (SharedConfig.passcodeType == 1)
      {
        this.passwordEditText.setFilters(new InputFilter[0]);
        this.passwordEditText.setInputType(129);
        this.numbersFrameLayout.setVisibility(8);
        this.passwordEditText.setFocusable(true);
        this.passwordEditText.setFocusableInTouchMode(true);
        this.passwordEditText.setVisibility(0);
        this.passwordEditText2.setVisibility(8);
        this.checkImage.setVisibility(0);
      }
    }
  }
  
  public void setDelegate(PasscodeViewDelegate paramPasscodeViewDelegate)
  {
    this.delegate = paramPasscodeViewDelegate;
  }
  
  private class AnimatingTextView
    extends FrameLayout
  {
    private String DOT = "•";
    private ArrayList<TextView> characterTextViews = new ArrayList(4);
    private AnimatorSet currentAnimation;
    private Runnable dotRunnable;
    private ArrayList<TextView> dotTextViews = new ArrayList(4);
    private StringBuilder stringBuilder = new StringBuilder(4);
    
    public AnimatingTextView(Context paramContext)
    {
      super();
      for (int i = 0; i < 4; i++)
      {
        this$1 = new TextView(paramContext);
        PasscodeView.this.setTextColor(-1);
        PasscodeView.this.setTextSize(1, 36.0F);
        PasscodeView.this.setGravity(17);
        PasscodeView.this.setAlpha(0.0F);
        PasscodeView.this.setPivotX(AndroidUtilities.dp(25.0F));
        PasscodeView.this.setPivotY(AndroidUtilities.dp(25.0F));
        addView(PasscodeView.this);
        FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)PasscodeView.this.getLayoutParams();
        localLayoutParams.width = AndroidUtilities.dp(50.0F);
        localLayoutParams.height = AndroidUtilities.dp(50.0F);
        localLayoutParams.gravity = 51;
        PasscodeView.this.setLayoutParams(localLayoutParams);
        this.characterTextViews.add(PasscodeView.this);
        this$1 = new TextView(paramContext);
        PasscodeView.this.setTextColor(-1);
        PasscodeView.this.setTextSize(1, 36.0F);
        PasscodeView.this.setGravity(17);
        PasscodeView.this.setAlpha(0.0F);
        PasscodeView.this.setText(this.DOT);
        PasscodeView.this.setPivotX(AndroidUtilities.dp(25.0F));
        PasscodeView.this.setPivotY(AndroidUtilities.dp(25.0F));
        addView(PasscodeView.this);
        localLayoutParams = (FrameLayout.LayoutParams)PasscodeView.this.getLayoutParams();
        localLayoutParams.width = AndroidUtilities.dp(50.0F);
        localLayoutParams.height = AndroidUtilities.dp(50.0F);
        localLayoutParams.gravity = 51;
        PasscodeView.this.setLayoutParams(localLayoutParams);
        this.dotTextViews.add(PasscodeView.this);
      }
    }
    
    private void eraseAllCharacters(boolean paramBoolean)
    {
      if (this.stringBuilder.length() == 0) {}
      for (;;)
      {
        return;
        if (this.dotRunnable != null)
        {
          AndroidUtilities.cancelRunOnUIThread(this.dotRunnable);
          this.dotRunnable = null;
        }
        if (this.currentAnimation != null)
        {
          this.currentAnimation.cancel();
          this.currentAnimation = null;
        }
        this.stringBuilder.delete(0, this.stringBuilder.length());
        int i;
        if (paramBoolean)
        {
          ArrayList localArrayList = new ArrayList();
          for (i = 0; i < 4; i++)
          {
            TextView localTextView = (TextView)this.characterTextViews.get(i);
            if (localTextView.getAlpha() != 0.0F)
            {
              localArrayList.add(ObjectAnimator.ofFloat(localTextView, "scaleX", new float[] { 0.0F }));
              localArrayList.add(ObjectAnimator.ofFloat(localTextView, "scaleY", new float[] { 0.0F }));
              localArrayList.add(ObjectAnimator.ofFloat(localTextView, "alpha", new float[] { 0.0F }));
            }
            localTextView = (TextView)this.dotTextViews.get(i);
            if (localTextView.getAlpha() != 0.0F)
            {
              localArrayList.add(ObjectAnimator.ofFloat(localTextView, "scaleX", new float[] { 0.0F }));
              localArrayList.add(ObjectAnimator.ofFloat(localTextView, "scaleY", new float[] { 0.0F }));
              localArrayList.add(ObjectAnimator.ofFloat(localTextView, "alpha", new float[] { 0.0F }));
            }
          }
          this.currentAnimation = new AnimatorSet();
          this.currentAnimation.setDuration(150L);
          this.currentAnimation.playTogether(localArrayList);
          this.currentAnimation.addListener(new AnimatorListenerAdapter()
          {
            public void onAnimationEnd(Animator paramAnonymousAnimator)
            {
              if ((PasscodeView.AnimatingTextView.this.currentAnimation != null) && (PasscodeView.AnimatingTextView.this.currentAnimation.equals(paramAnonymousAnimator))) {
                PasscodeView.AnimatingTextView.access$302(PasscodeView.AnimatingTextView.this, null);
              }
            }
          });
          this.currentAnimation.start();
        }
        else
        {
          for (i = 0; i < 4; i++)
          {
            ((TextView)this.characterTextViews.get(i)).setAlpha(0.0F);
            ((TextView)this.dotTextViews.get(i)).setAlpha(0.0F);
          }
        }
      }
    }
    
    private int getXForTextView(int paramInt)
    {
      return (getMeasuredWidth() - this.stringBuilder.length() * AndroidUtilities.dp(30.0F)) / 2 + AndroidUtilities.dp(30.0F) * paramInt - AndroidUtilities.dp(10.0F);
    }
    
    public void appendCharacter(String paramString)
    {
      if (this.stringBuilder.length() == 4) {}
      for (;;)
      {
        return;
        try
        {
          performHapticFeedback(3);
          ArrayList localArrayList = new ArrayList();
          i = this.stringBuilder.length();
          this.stringBuilder.append(paramString);
          TextView localTextView = (TextView)this.characterTextViews.get(i);
          localTextView.setText(paramString);
          localTextView.setTranslationX(getXForTextView(i));
          localArrayList.add(ObjectAnimator.ofFloat(localTextView, "scaleX", new float[] { 0.0F, 1.0F }));
          localArrayList.add(ObjectAnimator.ofFloat(localTextView, "scaleY", new float[] { 0.0F, 1.0F }));
          localArrayList.add(ObjectAnimator.ofFloat(localTextView, "alpha", new float[] { 0.0F, 1.0F }));
          localArrayList.add(ObjectAnimator.ofFloat(localTextView, "translationY", new float[] { AndroidUtilities.dp(20.0F), 0.0F }));
          paramString = (TextView)this.dotTextViews.get(i);
          paramString.setTranslationX(getXForTextView(i));
          paramString.setAlpha(0.0F);
          localArrayList.add(ObjectAnimator.ofFloat(paramString, "scaleX", new float[] { 0.0F, 1.0F }));
          localArrayList.add(ObjectAnimator.ofFloat(paramString, "scaleY", new float[] { 0.0F, 1.0F }));
          localArrayList.add(ObjectAnimator.ofFloat(paramString, "translationY", new float[] { AndroidUtilities.dp(20.0F), 0.0F }));
          for (j = i + 1; j < 4; j++)
          {
            paramString = (TextView)this.characterTextViews.get(j);
            if (paramString.getAlpha() != 0.0F)
            {
              localArrayList.add(ObjectAnimator.ofFloat(paramString, "scaleX", new float[] { 0.0F }));
              localArrayList.add(ObjectAnimator.ofFloat(paramString, "scaleY", new float[] { 0.0F }));
              localArrayList.add(ObjectAnimator.ofFloat(paramString, "alpha", new float[] { 0.0F }));
            }
            paramString = (TextView)this.dotTextViews.get(j);
            if (paramString.getAlpha() != 0.0F)
            {
              localArrayList.add(ObjectAnimator.ofFloat(paramString, "scaleX", new float[] { 0.0F }));
              localArrayList.add(ObjectAnimator.ofFloat(paramString, "scaleY", new float[] { 0.0F }));
              localArrayList.add(ObjectAnimator.ofFloat(paramString, "alpha", new float[] { 0.0F }));
            }
          }
        }
        catch (Exception localException)
        {
          final int i;
          for (;;)
          {
            FileLog.e(localException);
          }
          if (this.dotRunnable != null) {
            AndroidUtilities.cancelRunOnUIThread(this.dotRunnable);
          }
          this.dotRunnable = new Runnable()
          {
            public void run()
            {
              if (PasscodeView.AnimatingTextView.this.dotRunnable != this) {}
              for (;;)
              {
                return;
                ArrayList localArrayList = new ArrayList();
                TextView localTextView = (TextView)PasscodeView.AnimatingTextView.this.characterTextViews.get(i);
                localArrayList.add(ObjectAnimator.ofFloat(localTextView, "scaleX", new float[] { 0.0F }));
                localArrayList.add(ObjectAnimator.ofFloat(localTextView, "scaleY", new float[] { 0.0F }));
                localArrayList.add(ObjectAnimator.ofFloat(localTextView, "alpha", new float[] { 0.0F }));
                localTextView = (TextView)PasscodeView.AnimatingTextView.this.dotTextViews.get(i);
                localArrayList.add(ObjectAnimator.ofFloat(localTextView, "scaleX", new float[] { 1.0F }));
                localArrayList.add(ObjectAnimator.ofFloat(localTextView, "scaleY", new float[] { 1.0F }));
                localArrayList.add(ObjectAnimator.ofFloat(localTextView, "alpha", new float[] { 1.0F }));
                PasscodeView.AnimatingTextView.access$302(PasscodeView.AnimatingTextView.this, new AnimatorSet());
                PasscodeView.AnimatingTextView.this.currentAnimation.setDuration(150L);
                PasscodeView.AnimatingTextView.this.currentAnimation.playTogether(localArrayList);
                PasscodeView.AnimatingTextView.this.currentAnimation.addListener(new AnimatorListenerAdapter()
                {
                  public void onAnimationEnd(Animator paramAnonymous2Animator)
                  {
                    if ((PasscodeView.AnimatingTextView.this.currentAnimation != null) && (PasscodeView.AnimatingTextView.this.currentAnimation.equals(paramAnonymous2Animator))) {
                      PasscodeView.AnimatingTextView.access$302(PasscodeView.AnimatingTextView.this, null);
                    }
                  }
                });
                PasscodeView.AnimatingTextView.this.currentAnimation.start();
              }
            }
          };
          AndroidUtilities.runOnUIThread(this.dotRunnable, 1500L);
          for (int j = 0; j < i; j++)
          {
            paramString = (TextView)this.characterTextViews.get(j);
            localException.add(ObjectAnimator.ofFloat(paramString, "translationX", new float[] { getXForTextView(j) }));
            localException.add(ObjectAnimator.ofFloat(paramString, "scaleX", new float[] { 0.0F }));
            localException.add(ObjectAnimator.ofFloat(paramString, "scaleY", new float[] { 0.0F }));
            localException.add(ObjectAnimator.ofFloat(paramString, "alpha", new float[] { 0.0F }));
            localException.add(ObjectAnimator.ofFloat(paramString, "translationY", new float[] { 0.0F }));
            paramString = (TextView)this.dotTextViews.get(j);
            localException.add(ObjectAnimator.ofFloat(paramString, "translationX", new float[] { getXForTextView(j) }));
            localException.add(ObjectAnimator.ofFloat(paramString, "scaleX", new float[] { 1.0F }));
            localException.add(ObjectAnimator.ofFloat(paramString, "scaleY", new float[] { 1.0F }));
            localException.add(ObjectAnimator.ofFloat(paramString, "alpha", new float[] { 1.0F }));
            localException.add(ObjectAnimator.ofFloat(paramString, "translationY", new float[] { 0.0F }));
          }
          if (this.currentAnimation != null) {
            this.currentAnimation.cancel();
          }
          this.currentAnimation = new AnimatorSet();
          this.currentAnimation.setDuration(150L);
          this.currentAnimation.playTogether(localException);
          this.currentAnimation.addListener(new AnimatorListenerAdapter()
          {
            public void onAnimationEnd(Animator paramAnonymousAnimator)
            {
              if ((PasscodeView.AnimatingTextView.this.currentAnimation != null) && (PasscodeView.AnimatingTextView.this.currentAnimation.equals(paramAnonymousAnimator))) {
                PasscodeView.AnimatingTextView.access$302(PasscodeView.AnimatingTextView.this, null);
              }
            }
          });
          this.currentAnimation.start();
        }
      }
    }
    
    public void eraseLastCharacter()
    {
      if (this.stringBuilder.length() == 0) {}
      for (;;)
      {
        return;
        try
        {
          performHapticFeedback(3);
          ArrayList localArrayList = new ArrayList();
          i = this.stringBuilder.length() - 1;
          if (i != 0) {
            this.stringBuilder.deleteCharAt(i);
          }
          for (j = i; j < 4; j++)
          {
            TextView localTextView = (TextView)this.characterTextViews.get(j);
            if (localTextView.getAlpha() != 0.0F)
            {
              localArrayList.add(ObjectAnimator.ofFloat(localTextView, "scaleX", new float[] { 0.0F }));
              localArrayList.add(ObjectAnimator.ofFloat(localTextView, "scaleY", new float[] { 0.0F }));
              localArrayList.add(ObjectAnimator.ofFloat(localTextView, "alpha", new float[] { 0.0F }));
              localArrayList.add(ObjectAnimator.ofFloat(localTextView, "translationY", new float[] { 0.0F }));
              localArrayList.add(ObjectAnimator.ofFloat(localTextView, "translationX", new float[] { getXForTextView(j) }));
            }
            localTextView = (TextView)this.dotTextViews.get(j);
            if (localTextView.getAlpha() != 0.0F)
            {
              localArrayList.add(ObjectAnimator.ofFloat(localTextView, "scaleX", new float[] { 0.0F }));
              localArrayList.add(ObjectAnimator.ofFloat(localTextView, "scaleY", new float[] { 0.0F }));
              localArrayList.add(ObjectAnimator.ofFloat(localTextView, "alpha", new float[] { 0.0F }));
              localArrayList.add(ObjectAnimator.ofFloat(localTextView, "translationY", new float[] { 0.0F }));
              localArrayList.add(ObjectAnimator.ofFloat(localTextView, "translationX", new float[] { getXForTextView(j) }));
            }
          }
        }
        catch (Exception localException)
        {
          int i;
          for (;;)
          {
            FileLog.e(localException);
          }
          if (i == 0) {
            this.stringBuilder.deleteCharAt(i);
          }
          for (int j = 0; j < i; j++)
          {
            localException.add(ObjectAnimator.ofFloat((TextView)this.characterTextViews.get(j), "translationX", new float[] { getXForTextView(j) }));
            localException.add(ObjectAnimator.ofFloat((TextView)this.dotTextViews.get(j), "translationX", new float[] { getXForTextView(j) }));
          }
          if (this.dotRunnable != null)
          {
            AndroidUtilities.cancelRunOnUIThread(this.dotRunnable);
            this.dotRunnable = null;
          }
          if (this.currentAnimation != null) {
            this.currentAnimation.cancel();
          }
          this.currentAnimation = new AnimatorSet();
          this.currentAnimation.setDuration(150L);
          this.currentAnimation.playTogether(localException);
          this.currentAnimation.addListener(new AnimatorListenerAdapter()
          {
            public void onAnimationEnd(Animator paramAnonymousAnimator)
            {
              if ((PasscodeView.AnimatingTextView.this.currentAnimation != null) && (PasscodeView.AnimatingTextView.this.currentAnimation.equals(paramAnonymousAnimator))) {
                PasscodeView.AnimatingTextView.access$302(PasscodeView.AnimatingTextView.this, null);
              }
            }
          });
          this.currentAnimation.start();
        }
      }
    }
    
    public String getString()
    {
      return this.stringBuilder.toString();
    }
    
    public int lenght()
    {
      return this.stringBuilder.length();
    }
    
    protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      if (this.dotRunnable != null)
      {
        AndroidUtilities.cancelRunOnUIThread(this.dotRunnable);
        this.dotRunnable = null;
      }
      if (this.currentAnimation != null)
      {
        this.currentAnimation.cancel();
        this.currentAnimation = null;
      }
      int i = 0;
      if (i < 4)
      {
        if (i < this.stringBuilder.length())
        {
          TextView localTextView = (TextView)this.characterTextViews.get(i);
          localTextView.setAlpha(0.0F);
          localTextView.setScaleX(1.0F);
          localTextView.setScaleY(1.0F);
          localTextView.setTranslationY(0.0F);
          localTextView.setTranslationX(getXForTextView(i));
          localTextView = (TextView)this.dotTextViews.get(i);
          localTextView.setAlpha(1.0F);
          localTextView.setScaleX(1.0F);
          localTextView.setScaleY(1.0F);
          localTextView.setTranslationY(0.0F);
          localTextView.setTranslationX(getXForTextView(i));
        }
        for (;;)
        {
          i++;
          break;
          ((TextView)this.characterTextViews.get(i)).setAlpha(0.0F);
          ((TextView)this.dotTextViews.get(i)).setAlpha(0.0F);
        }
      }
      super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    }
  }
  
  public static abstract interface PasscodeViewDelegate
  {
    public abstract void didAcceptedPassword();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/PasscodeView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */