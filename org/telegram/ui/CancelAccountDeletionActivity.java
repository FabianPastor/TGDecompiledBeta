package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AnimatorListenerAdapterProxy;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_confirmPhone;
import org.telegram.tgnet.TLRPC.TL_account_sendConfirmPhoneCode;
import org.telegram.tgnet.TLRPC.TL_auth_codeTypeCall;
import org.telegram.tgnet.TLRPC.TL_auth_codeTypeFlashCall;
import org.telegram.tgnet.TLRPC.TL_auth_codeTypeSms;
import org.telegram.tgnet.TLRPC.TL_auth_resendCode;
import org.telegram.tgnet.TLRPC.TL_auth_sentCode;
import org.telegram.tgnet.TLRPC.TL_auth_sentCodeTypeApp;
import org.telegram.tgnet.TLRPC.TL_auth_sentCodeTypeCall;
import org.telegram.tgnet.TLRPC.TL_auth_sentCodeTypeFlashCall;
import org.telegram.tgnet.TLRPC.TL_auth_sentCodeTypeSms;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.auth_SentCodeType;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.SlideView;

public class CancelAccountDeletionActivity
  extends BaseFragment
{
  private static final int done_button = 1;
  private boolean checkPermissions = false;
  private int currentViewNum = 0;
  private View doneButton;
  private Dialog errorDialog;
  private String hash;
  private Dialog permissionsDialog;
  private ArrayList<String> permissionsItems = new ArrayList();
  private String phone;
  private ProgressDialog progressDialog;
  private SlideView[] views = new SlideView[5];
  
  public CancelAccountDeletionActivity(Bundle paramBundle)
  {
    super(paramBundle);
    this.hash = paramBundle.getString("hash");
    this.phone = paramBundle.getString("phone");
  }
  
  private void fillNextCodeParams(Bundle paramBundle, TLRPC.TL_auth_sentCode paramTL_auth_sentCode)
  {
    paramBundle.putString("phoneHash", paramTL_auth_sentCode.phone_code_hash);
    if ((paramTL_auth_sentCode.next_type instanceof TLRPC.TL_auth_codeTypeCall))
    {
      paramBundle.putInt("nextType", 4);
      if (!(paramTL_auth_sentCode.type instanceof TLRPC.TL_auth_sentCodeTypeApp)) {
        break label106;
      }
      paramBundle.putInt("type", 1);
      paramBundle.putInt("length", paramTL_auth_sentCode.type.length);
      setPage(1, true, paramBundle, false);
    }
    label106:
    do
    {
      return;
      if ((paramTL_auth_sentCode.next_type instanceof TLRPC.TL_auth_codeTypeFlashCall))
      {
        paramBundle.putInt("nextType", 3);
        break;
      }
      if (!(paramTL_auth_sentCode.next_type instanceof TLRPC.TL_auth_codeTypeSms)) {
        break;
      }
      paramBundle.putInt("nextType", 2);
      break;
      if (paramTL_auth_sentCode.timeout == 0) {
        paramTL_auth_sentCode.timeout = 60;
      }
      paramBundle.putInt("timeout", paramTL_auth_sentCode.timeout * 1000);
      if ((paramTL_auth_sentCode.type instanceof TLRPC.TL_auth_sentCodeTypeCall))
      {
        paramBundle.putInt("type", 4);
        paramBundle.putInt("length", paramTL_auth_sentCode.type.length);
        setPage(4, true, paramBundle, false);
        return;
      }
      if ((paramTL_auth_sentCode.type instanceof TLRPC.TL_auth_sentCodeTypeFlashCall))
      {
        paramBundle.putInt("type", 3);
        paramBundle.putString("pattern", paramTL_auth_sentCode.type.pattern);
        setPage(3, true, paramBundle, false);
        return;
      }
    } while (!(paramTL_auth_sentCode.type instanceof TLRPC.TL_auth_sentCodeTypeSms));
    paramBundle.putInt("type", 2);
    paramBundle.putInt("length", paramTL_auth_sentCode.type.length);
    setPage(2, true, paramBundle, false);
  }
  
  public View createView(Context paramContext)
  {
    this.actionBar.setTitle(LocaleController.getString("AppName", 2131165299));
    this.actionBar.setBackButtonImage(2130837700);
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (paramAnonymousInt == 1) {
          CancelAccountDeletionActivity.this.views[CancelAccountDeletionActivity.this.currentViewNum].onNextPressed();
        }
        while (paramAnonymousInt != -1) {
          return;
        }
        CancelAccountDeletionActivity.this.finishFragment();
      }
    });
    this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, 2130837720, AndroidUtilities.dp(56.0F));
    this.doneButton.setVisibility(8);
    this.fragmentView = new ScrollView(paramContext);
    ScrollView localScrollView = (ScrollView)this.fragmentView;
    localScrollView.setFillViewport(true);
    FrameLayout localFrameLayout = new FrameLayout(paramContext);
    localScrollView.addView(localFrameLayout, LayoutHelper.createScroll(-1, -2, 51));
    this.views[0] = new PhoneView(paramContext);
    this.views[1] = new LoginActivitySmsView(paramContext, 1);
    this.views[2] = new LoginActivitySmsView(paramContext, 2);
    this.views[3] = new LoginActivitySmsView(paramContext, 3);
    this.views[4] = new LoginActivitySmsView(paramContext, 4);
    int i = 0;
    if (i < this.views.length)
    {
      paramContext = this.views[i];
      int j;
      label229:
      float f1;
      label252:
      float f2;
      if (i == 0)
      {
        j = 0;
        paramContext.setVisibility(j);
        paramContext = this.views[i];
        if (i != 0) {
          break label309;
        }
        f1 = -2.0F;
        if (!AndroidUtilities.isTablet()) {
          break label316;
        }
        f2 = 26.0F;
        label262:
        if (!AndroidUtilities.isTablet()) {
          break label323;
        }
      }
      label309:
      label316:
      label323:
      for (float f3 = 26.0F;; f3 = 18.0F)
      {
        localFrameLayout.addView(paramContext, LayoutHelper.createFrame(-1, f1, 51, f2, 30.0F, f3, 0.0F));
        i += 1;
        break;
        j = 8;
        break label229;
        f1 = -1.0F;
        break label252;
        f2 = 18.0F;
        break label262;
      }
    }
    this.actionBar.setTitle(this.views[0].getHeaderName());
    return this.fragmentView;
  }
  
  public void needHideProgress()
  {
    if (this.progressDialog == null) {
      return;
    }
    try
    {
      this.progressDialog.dismiss();
      this.progressDialog = null;
      return;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e("tmessages", localException);
      }
    }
  }
  
  public Dialog needShowAlert(String paramString)
  {
    if ((paramString == null) || (getParentActivity() == null)) {
      return null;
    }
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(getParentActivity());
    localBuilder.setTitle(LocaleController.getString("AppName", 2131165299));
    localBuilder.setMessage(paramString);
    localBuilder.setPositiveButton(LocaleController.getString("OK", 2131166044), null);
    paramString = localBuilder.create();
    showDialog(paramString);
    return paramString;
  }
  
  public void needShowProgress()
  {
    if ((getParentActivity() == null) || (getParentActivity().isFinishing()) || (this.progressDialog != null)) {
      return;
    }
    this.progressDialog = new ProgressDialog(getParentActivity());
    this.progressDialog.setMessage(LocaleController.getString("Loading", 2131165834));
    this.progressDialog.setCanceledOnTouchOutside(false);
    this.progressDialog.setCancelable(false);
    this.progressDialog.show();
  }
  
  public boolean onBackPressed()
  {
    int i = 0;
    while (i < this.views.length)
    {
      if (this.views[i] != null) {
        this.views[i].onDestroyActivity();
      }
      i += 1;
    }
    return true;
  }
  
  protected void onDialogDismiss(Dialog paramDialog)
  {
    if ((Build.VERSION.SDK_INT >= 23) && (paramDialog == this.permissionsDialog) && (!this.permissionsItems.isEmpty())) {
      getParentActivity().requestPermissions((String[])this.permissionsItems.toArray(new String[this.permissionsItems.size()]), 6);
    }
    if (paramDialog == this.errorDialog) {
      finishFragment();
    }
  }
  
  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    int i = 0;
    while (i < this.views.length)
    {
      if (this.views[i] != null) {
        this.views[i].onDestroyActivity();
      }
      i += 1;
    }
    if (this.progressDialog != null) {}
    try
    {
      this.progressDialog.dismiss();
      this.progressDialog = null;
      AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
      return;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e("tmessages", localException);
      }
    }
  }
  
  public void onRequestPermissionsResultFragment(int paramInt, String[] paramArrayOfString, int[] paramArrayOfInt)
  {
    if (paramInt == 6)
    {
      this.checkPermissions = false;
      if (this.currentViewNum == 0) {
        this.views[this.currentViewNum].onNextPressed();
      }
    }
  }
  
  public void onResume()
  {
    super.onResume();
    AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
  }
  
  public void onTransitionAnimationEnd(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramBoolean1) {
      this.views[this.currentViewNum].onShow();
    }
  }
  
  public void setPage(int paramInt, boolean paramBoolean1, Bundle paramBundle, boolean paramBoolean2)
  {
    final SlideView localSlideView1;
    final SlideView localSlideView2;
    if ((paramInt == 3) || (paramInt == 0))
    {
      if (paramInt == 0) {}
      this.doneButton.setVisibility(8);
      localSlideView1 = this.views[this.currentViewNum];
      localSlideView2 = this.views[paramInt];
      this.currentViewNum = paramInt;
      localSlideView2.setParams(paramBundle);
      this.actionBar.setTitle(localSlideView2.getHeaderName());
      localSlideView2.onShow();
      if (!paramBoolean2) {
        break label209;
      }
      f = -AndroidUtilities.displaySize.x;
      label84:
      localSlideView2.setX(f);
      paramBundle = new AnimatorSet();
      paramBundle.setInterpolator(new AccelerateDecelerateInterpolator());
      paramBundle.setDuration(300L);
      if (!paramBoolean2) {
        break label221;
      }
    }
    label209:
    label221:
    for (float f = AndroidUtilities.displaySize.x;; f = -AndroidUtilities.displaySize.x)
    {
      paramBundle.playTogether(new Animator[] { ObjectAnimator.ofFloat(localSlideView1, "translationX", new float[] { f }), ObjectAnimator.ofFloat(localSlideView2, "translationX", new float[] { 0.0F }) });
      paramBundle.addListener(new AnimatorListenerAdapterProxy()
      {
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          localSlideView1.setVisibility(8);
          localSlideView1.setX(0.0F);
        }
        
        public void onAnimationStart(Animator paramAnonymousAnimator)
        {
          localSlideView2.setVisibility(0);
        }
      });
      paramBundle.start();
      return;
      this.doneButton.setVisibility(0);
      break;
      f = AndroidUtilities.displaySize.x;
      break label84;
    }
  }
  
  public class LoginActivitySmsView
    extends SlideView
    implements NotificationCenter.NotificationCenterDelegate
  {
    private EditText codeField;
    private volatile int codeTime = 15000;
    private Timer codeTimer;
    private TextView confirmTextView;
    private Bundle currentParams;
    private int currentType;
    private boolean ignoreOnTextChange;
    private double lastCodeTime;
    private double lastCurrentTime;
    private String lastError = "";
    private int length;
    private boolean nextPressed;
    private int nextType;
    private int openTime;
    private String pattern = "*";
    private String phone;
    private String phoneHash;
    private TextView problemText;
    private ProgressView progressView;
    private volatile int time = 60000;
    private TextView timeText;
    private Timer timeTimer;
    private int timeout;
    private final Object timerSync = new Object();
    private boolean waitingForEvent;
    
    public LoginActivitySmsView(Context paramContext, int paramInt)
    {
      super();
      this.currentType = paramInt;
      setOrientation(1);
      this.confirmTextView = new TextView(paramContext);
      this.confirmTextView.setTextColor(-9079435);
      this.confirmTextView.setTextSize(1, 14.0F);
      Object localObject1 = this.confirmTextView;
      Object localObject2;
      if (LocaleController.isRTL)
      {
        paramInt = 5;
        ((TextView)localObject1).setGravity(paramInt);
        this.confirmTextView.setLineSpacing(AndroidUtilities.dp(2.0F), 1.0F);
        if (this.currentType != 3) {
          break label772;
        }
        localObject1 = new FrameLayout(paramContext);
        localObject2 = new ImageView(paramContext);
        ((ImageView)localObject2).setImageResource(2130837877);
        if (!LocaleController.isRTL) {
          break label706;
        }
        ((FrameLayout)localObject1).addView((View)localObject2, LayoutHelper.createFrame(64, 76.0F, 19, 2.0F, 2.0F, 0.0F, 0.0F));
        localObject2 = this.confirmTextView;
        if (!LocaleController.isRTL) {
          break label701;
        }
        paramInt = 5;
        label195:
        ((FrameLayout)localObject1).addView((View)localObject2, LayoutHelper.createFrame(-1, -2.0F, paramInt, 82.0F, 0.0F, 0.0F, 0.0F));
        if (!LocaleController.isRTL) {
          break label767;
        }
        paramInt = 5;
        label222:
        addView((View)localObject1, LayoutHelper.createLinear(-2, -2, paramInt));
        this.codeField = new EditText(paramContext);
        this.codeField.setTextColor(-14606047);
        this.codeField.setHint(LocaleController.getString("Code", 2131165516));
        AndroidUtilities.clearCursorDrawable(this.codeField);
        this.codeField.setHintTextColor(-6842473);
        this.codeField.setImeOptions(268435461);
        this.codeField.setTextSize(1, 18.0F);
        this.codeField.setInputType(3);
        this.codeField.setMaxLines(1);
        this.codeField.setPadding(0, 0, 0, 0);
        addView(this.codeField, LayoutHelper.createLinear(-1, 36, 1, 0, 20, 0, 0));
        this.codeField.addTextChangedListener(new TextWatcher()
        {
          public void afterTextChanged(Editable paramAnonymousEditable)
          {
            if (CancelAccountDeletionActivity.LoginActivitySmsView.this.ignoreOnTextChange) {}
            while ((CancelAccountDeletionActivity.LoginActivitySmsView.this.length == 0) || (CancelAccountDeletionActivity.LoginActivitySmsView.this.codeField.length() != CancelAccountDeletionActivity.LoginActivitySmsView.this.length)) {
              return;
            }
            CancelAccountDeletionActivity.LoginActivitySmsView.this.onNextPressed();
          }
          
          public void beforeTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
          
          public void onTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
        });
        this.codeField.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
          public boolean onEditorAction(TextView paramAnonymousTextView, int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
          {
            if (paramAnonymousInt == 5)
            {
              CancelAccountDeletionActivity.LoginActivitySmsView.this.onNextPressed();
              return true;
            }
            return false;
          }
        });
        if (this.currentType == 3)
        {
          this.codeField.setEnabled(false);
          this.codeField.setInputType(0);
          this.codeField.setVisibility(8);
        }
        this.timeText = new TextView(paramContext);
        this.timeText.setTextSize(1, 14.0F);
        this.timeText.setTextColor(-9079435);
        this.timeText.setLineSpacing(AndroidUtilities.dp(2.0F), 1.0F);
        localObject1 = this.timeText;
        if (!LocaleController.isRTL) {
          break label808;
        }
        paramInt = 5;
        label476:
        ((TextView)localObject1).setGravity(paramInt);
        localObject1 = this.timeText;
        if (!LocaleController.isRTL) {
          break label813;
        }
        paramInt = 5;
        label496:
        addView((View)localObject1, LayoutHelper.createLinear(-2, -2, paramInt, 0, 30, 0, 0));
        if (this.currentType == 3)
        {
          this.progressView = new ProgressView(paramContext);
          addView(this.progressView, LayoutHelper.createLinear(-1, 3, 0.0F, 12.0F, 0.0F, 0.0F));
        }
        this.problemText = new TextView(paramContext);
        this.problemText.setText(LocaleController.getString("DidNotGetTheCode", 2131165584));
        paramContext = this.problemText;
        if (!LocaleController.isRTL) {
          break label818;
        }
        paramInt = 5;
        label593:
        paramContext.setGravity(paramInt);
        this.problemText.setTextSize(1, 14.0F);
        this.problemText.setTextColor(-11697229);
        this.problemText.setLineSpacing(AndroidUtilities.dp(2.0F), 1.0F);
        this.problemText.setPadding(0, AndroidUtilities.dp(2.0F), 0, AndroidUtilities.dp(12.0F));
        paramContext = this.problemText;
        if (!LocaleController.isRTL) {
          break label823;
        }
      }
      label701:
      label706:
      label767:
      label772:
      label808:
      label813:
      label818:
      label823:
      for (paramInt = 5;; paramInt = 3)
      {
        addView(paramContext, LayoutHelper.createLinear(-2, -2, paramInt, 0, 20, 0, 0));
        this.problemText.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            if (CancelAccountDeletionActivity.LoginActivitySmsView.this.nextPressed) {
              return;
            }
            if ((CancelAccountDeletionActivity.LoginActivitySmsView.this.nextType != 0) && (CancelAccountDeletionActivity.LoginActivitySmsView.this.nextType != 4))
            {
              CancelAccountDeletionActivity.LoginActivitySmsView.this.resendCode();
              return;
            }
            try
            {
              paramAnonymousView = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
              paramAnonymousView = String.format(Locale.US, "%s (%d)", new Object[] { paramAnonymousView.versionName, Integer.valueOf(paramAnonymousView.versionCode) });
              Intent localIntent = new Intent("android.intent.action.SEND");
              localIntent.setType("message/rfc822");
              localIntent.putExtra("android.intent.extra.EMAIL", new String[] { "sms@stel.com" });
              localIntent.putExtra("android.intent.extra.SUBJECT", "Android cancel account deletion issue " + paramAnonymousView + " " + CancelAccountDeletionActivity.LoginActivitySmsView.this.phone);
              localIntent.putExtra("android.intent.extra.TEXT", "Phone: " + CancelAccountDeletionActivity.LoginActivitySmsView.this.phone + "\nApp version: " + paramAnonymousView + "\nOS version: SDK " + Build.VERSION.SDK_INT + "\nDevice Name: " + Build.MANUFACTURER + Build.MODEL + "\nLocale: " + Locale.getDefault() + "\nError: " + CancelAccountDeletionActivity.LoginActivitySmsView.this.lastError);
              CancelAccountDeletionActivity.LoginActivitySmsView.this.getContext().startActivity(Intent.createChooser(localIntent, "Send email..."));
              return;
            }
            catch (Exception paramAnonymousView)
            {
              CancelAccountDeletionActivity.this.needShowAlert(LocaleController.getString("NoMailInstalled", 2131165935));
            }
          }
        });
        return;
        paramInt = 3;
        break;
        paramInt = 3;
        break label195;
        TextView localTextView = this.confirmTextView;
        if (LocaleController.isRTL) {}
        for (paramInt = 5;; paramInt = 3)
        {
          ((FrameLayout)localObject1).addView(localTextView, LayoutHelper.createFrame(-1, -2.0F, paramInt, 0.0F, 0.0F, 82.0F, 0.0F));
          ((FrameLayout)localObject1).addView((View)localObject2, LayoutHelper.createFrame(64, 76.0F, 21, 0.0F, 2.0F, 0.0F, 2.0F));
          break;
        }
        paramInt = 3;
        break label222;
        localObject1 = this.confirmTextView;
        if (LocaleController.isRTL) {}
        for (paramInt = 5;; paramInt = 3)
        {
          addView((View)localObject1, LayoutHelper.createLinear(-2, -2, paramInt));
          break;
        }
        paramInt = 3;
        break label476;
        paramInt = 3;
        break label496;
        paramInt = 3;
        break label593;
      }
    }
    
    private void createCodeTimer()
    {
      if (this.codeTimer != null) {
        return;
      }
      this.codeTime = 15000;
      this.codeTimer = new Timer();
      this.lastCodeTime = System.currentTimeMillis();
      this.codeTimer.schedule(new TimerTask()
      {
        public void run()
        {
          double d1 = System.currentTimeMillis();
          double d2 = CancelAccountDeletionActivity.LoginActivitySmsView.this.lastCodeTime;
          CancelAccountDeletionActivity.LoginActivitySmsView.access$1602(CancelAccountDeletionActivity.LoginActivitySmsView.this, (int)(CancelAccountDeletionActivity.LoginActivitySmsView.this.codeTime - (d1 - d2)));
          CancelAccountDeletionActivity.LoginActivitySmsView.access$1502(CancelAccountDeletionActivity.LoginActivitySmsView.this, d1);
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              if (CancelAccountDeletionActivity.LoginActivitySmsView.this.codeTime <= 1000)
              {
                CancelAccountDeletionActivity.LoginActivitySmsView.this.problemText.setVisibility(0);
                CancelAccountDeletionActivity.LoginActivitySmsView.this.destroyCodeTimer();
              }
            }
          });
        }
      }, 0L, 1000L);
    }
    
    private void createTimer()
    {
      if (this.timeTimer != null) {
        return;
      }
      this.timeTimer = new Timer();
      this.timeTimer.schedule(new TimerTask()
      {
        public void run()
        {
          if (CancelAccountDeletionActivity.LoginActivitySmsView.this.timeTimer == null) {
            return;
          }
          double d1 = System.currentTimeMillis();
          double d2 = CancelAccountDeletionActivity.LoginActivitySmsView.this.lastCurrentTime;
          CancelAccountDeletionActivity.LoginActivitySmsView.access$2102(CancelAccountDeletionActivity.LoginActivitySmsView.this, (int)(CancelAccountDeletionActivity.LoginActivitySmsView.this.time - (d1 - d2)));
          CancelAccountDeletionActivity.LoginActivitySmsView.access$2002(CancelAccountDeletionActivity.LoginActivitySmsView.this, d1);
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              int i;
              int j;
              if (CancelAccountDeletionActivity.LoginActivitySmsView.this.time >= 1000)
              {
                i = CancelAccountDeletionActivity.LoginActivitySmsView.this.time / 1000 / 60;
                j = CancelAccountDeletionActivity.LoginActivitySmsView.this.time / 1000 - i * 60;
                if ((CancelAccountDeletionActivity.LoginActivitySmsView.this.nextType == 4) || (CancelAccountDeletionActivity.LoginActivitySmsView.this.nextType == 3)) {
                  CancelAccountDeletionActivity.LoginActivitySmsView.this.timeText.setText(LocaleController.formatString("CallText", 2131165384, new Object[] { Integer.valueOf(i), Integer.valueOf(j) }));
                }
              }
              do
              {
                do
                {
                  for (;;)
                  {
                    if (CancelAccountDeletionActivity.LoginActivitySmsView.this.progressView != null) {
                      CancelAccountDeletionActivity.LoginActivitySmsView.this.progressView.setProgress(1.0F - CancelAccountDeletionActivity.LoginActivitySmsView.this.time / CancelAccountDeletionActivity.LoginActivitySmsView.this.timeout);
                    }
                    return;
                    if (CancelAccountDeletionActivity.LoginActivitySmsView.this.nextType == 2) {
                      CancelAccountDeletionActivity.LoginActivitySmsView.this.timeText.setText(LocaleController.formatString("SmsText", 2131166300, new Object[] { Integer.valueOf(i), Integer.valueOf(j) }));
                    }
                  }
                  if (CancelAccountDeletionActivity.LoginActivitySmsView.this.progressView != null) {
                    CancelAccountDeletionActivity.LoginActivitySmsView.this.progressView.setProgress(1.0F);
                  }
                  CancelAccountDeletionActivity.LoginActivitySmsView.this.destroyTimer();
                  if (CancelAccountDeletionActivity.LoginActivitySmsView.this.currentType == 3)
                  {
                    AndroidUtilities.setWaitingForCall(false);
                    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didReceiveCall);
                    CancelAccountDeletionActivity.LoginActivitySmsView.access$2702(CancelAccountDeletionActivity.LoginActivitySmsView.this, false);
                    CancelAccountDeletionActivity.LoginActivitySmsView.this.destroyCodeTimer();
                    CancelAccountDeletionActivity.LoginActivitySmsView.this.resendCode();
                    return;
                  }
                } while (CancelAccountDeletionActivity.LoginActivitySmsView.this.currentType != 2);
                if (CancelAccountDeletionActivity.LoginActivitySmsView.this.nextType == 4)
                {
                  CancelAccountDeletionActivity.LoginActivitySmsView.this.timeText.setText(LocaleController.getString("Calling", 2131165385));
                  CancelAccountDeletionActivity.LoginActivitySmsView.this.createCodeTimer();
                  TLRPC.TL_auth_resendCode localTL_auth_resendCode = new TLRPC.TL_auth_resendCode();
                  localTL_auth_resendCode.phone_number = CancelAccountDeletionActivity.LoginActivitySmsView.this.phone;
                  localTL_auth_resendCode.phone_code_hash = CancelAccountDeletionActivity.LoginActivitySmsView.this.phoneHash;
                  ConnectionsManager.getInstance().sendRequest(localTL_auth_resendCode, new RequestDelegate()
                  {
                    public void run(TLObject paramAnonymous3TLObject, final TLRPC.TL_error paramAnonymous3TL_error)
                    {
                      if ((paramAnonymous3TL_error != null) && (paramAnonymous3TL_error.text != null)) {
                        AndroidUtilities.runOnUIThread(new Runnable()
                        {
                          public void run()
                          {
                            CancelAccountDeletionActivity.LoginActivitySmsView.access$1402(CancelAccountDeletionActivity.LoginActivitySmsView.this, paramAnonymous3TL_error.text);
                          }
                        });
                      }
                    }
                  }, 2);
                  return;
                }
              } while (CancelAccountDeletionActivity.LoginActivitySmsView.this.nextType != 3);
              AndroidUtilities.setWaitingForSms(false);
              NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didReceiveSmsCode);
              CancelAccountDeletionActivity.LoginActivitySmsView.access$2702(CancelAccountDeletionActivity.LoginActivitySmsView.this, false);
              CancelAccountDeletionActivity.LoginActivitySmsView.this.destroyCodeTimer();
              CancelAccountDeletionActivity.LoginActivitySmsView.this.resendCode();
            }
          });
        }
      }, 0L, 1000L);
    }
    
    private void destroyCodeTimer()
    {
      try
      {
        synchronized (this.timerSync)
        {
          if (this.codeTimer != null)
          {
            this.codeTimer.cancel();
            this.codeTimer = null;
          }
          return;
        }
        return;
      }
      catch (Exception localException)
      {
        FileLog.e("tmessages", localException);
      }
    }
    
    private void destroyTimer()
    {
      try
      {
        synchronized (this.timerSync)
        {
          if (this.timeTimer != null)
          {
            this.timeTimer.cancel();
            this.timeTimer = null;
          }
          return;
        }
        return;
      }
      catch (Exception localException)
      {
        FileLog.e("tmessages", localException);
      }
    }
    
    private void resendCode()
    {
      final Bundle localBundle = new Bundle();
      localBundle.putString("phone", this.phone);
      this.nextPressed = true;
      CancelAccountDeletionActivity.this.needShowProgress();
      TLRPC.TL_auth_resendCode localTL_auth_resendCode = new TLRPC.TL_auth_resendCode();
      localTL_auth_resendCode.phone_number = this.phone;
      localTL_auth_resendCode.phone_code_hash = this.phoneHash;
      ConnectionsManager.getInstance().sendRequest(localTL_auth_resendCode, new RequestDelegate()
      {
        public void run(final TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
        {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              CancelAccountDeletionActivity.LoginActivitySmsView.access$1002(CancelAccountDeletionActivity.LoginActivitySmsView.this, false);
              if (paramAnonymousTL_error == null) {
                CancelAccountDeletionActivity.this.fillNextCodeParams(CancelAccountDeletionActivity.LoginActivitySmsView.4.this.val$params, (TLRPC.TL_auth_sentCode)paramAnonymousTLObject);
              }
              for (;;)
              {
                CancelAccountDeletionActivity.this.needHideProgress();
                return;
                if (paramAnonymousTL_error.text != null) {
                  if (paramAnonymousTL_error.text.contains("PHONE_CODE_EXPIRED")) {
                    CancelAccountDeletionActivity.this.needShowAlert(LocaleController.getString("CodeExpired", 2131165517));
                  } else if (paramAnonymousTL_error.text.startsWith("FLOOD_WAIT")) {
                    CancelAccountDeletionActivity.this.needShowAlert(LocaleController.getString("FloodWait", 2131165639));
                  } else if (paramAnonymousTL_error.code != 64536) {
                    CancelAccountDeletionActivity.this.needShowAlert(LocaleController.getString("ErrorOccurred", 2131165626) + "\n" + paramAnonymousTL_error.text);
                  }
                }
              }
            }
          });
        }
      }, 2);
    }
    
    public void didReceivedNotification(int paramInt, Object... paramVarArgs)
    {
      if ((!this.waitingForEvent) || (this.codeField == null)) {}
      do
      {
        do
        {
          return;
          if (paramInt == NotificationCenter.didReceiveSmsCode)
          {
            this.ignoreOnTextChange = true;
            this.codeField.setText("" + paramVarArgs[0]);
            this.ignoreOnTextChange = false;
            onNextPressed();
            return;
          }
        } while (paramInt != NotificationCenter.didReceiveCall);
        paramVarArgs = "" + paramVarArgs[0];
      } while ((!this.pattern.equals("*")) && (!paramVarArgs.contains(this.pattern.replace("*", ""))));
      this.ignoreOnTextChange = true;
      this.codeField.setText(paramVarArgs);
      this.ignoreOnTextChange = false;
      onNextPressed();
    }
    
    public String getHeaderName()
    {
      return LocaleController.getString("CancelAccountReset", 2131165387);
    }
    
    public void onDestroyActivity()
    {
      super.onDestroyActivity();
      if (this.currentType == 2)
      {
        AndroidUtilities.setWaitingForSms(false);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didReceiveSmsCode);
      }
      for (;;)
      {
        this.waitingForEvent = false;
        destroyTimer();
        destroyCodeTimer();
        return;
        if (this.currentType == 3)
        {
          AndroidUtilities.setWaitingForCall(false);
          NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didReceiveCall);
        }
      }
    }
    
    public void onNextPressed()
    {
      if (this.nextPressed) {
        return;
      }
      this.nextPressed = true;
      if (this.currentType == 2)
      {
        AndroidUtilities.setWaitingForSms(false);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didReceiveSmsCode);
      }
      for (;;)
      {
        this.waitingForEvent = false;
        TLRPC.TL_account_confirmPhone localTL_account_confirmPhone = new TLRPC.TL_account_confirmPhone();
        localTL_account_confirmPhone.phone_code = this.codeField.getText().toString();
        localTL_account_confirmPhone.phone_code_hash = this.phoneHash;
        destroyTimer();
        CancelAccountDeletionActivity.this.needShowProgress();
        ConnectionsManager.getInstance().sendRequest(localTL_account_confirmPhone, new RequestDelegate()
        {
          public void run(TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
          {
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                CancelAccountDeletionActivity.this.needHideProgress();
                CancelAccountDeletionActivity.LoginActivitySmsView.access$1002(CancelAccountDeletionActivity.LoginActivitySmsView.this, false);
                if (paramAnonymousTL_error == null)
                {
                  CancelAccountDeletionActivity.access$602(CancelAccountDeletionActivity.this, CancelAccountDeletionActivity.this.needShowAlert(LocaleController.formatString("CancelLinkSuccess", 2131165390, new Object[] { PhoneFormat.getInstance().format("+" + CancelAccountDeletionActivity.LoginActivitySmsView.this.phone) })));
                  return;
                }
                CancelAccountDeletionActivity.LoginActivitySmsView.access$1402(CancelAccountDeletionActivity.LoginActivitySmsView.this, paramAnonymousTL_error.text);
                if (((CancelAccountDeletionActivity.LoginActivitySmsView.this.currentType == 3) && ((CancelAccountDeletionActivity.LoginActivitySmsView.this.nextType == 4) || (CancelAccountDeletionActivity.LoginActivitySmsView.this.nextType == 2))) || ((CancelAccountDeletionActivity.LoginActivitySmsView.this.currentType == 2) && ((CancelAccountDeletionActivity.LoginActivitySmsView.this.nextType == 4) || (CancelAccountDeletionActivity.LoginActivitySmsView.this.nextType == 3)))) {
                  CancelAccountDeletionActivity.LoginActivitySmsView.this.createTimer();
                }
                if (CancelAccountDeletionActivity.LoginActivitySmsView.this.currentType == 2)
                {
                  AndroidUtilities.setWaitingForSms(true);
                  NotificationCenter.getInstance().addObserver(CancelAccountDeletionActivity.LoginActivitySmsView.this, NotificationCenter.didReceiveSmsCode);
                }
                for (;;)
                {
                  CancelAccountDeletionActivity.LoginActivitySmsView.access$2702(CancelAccountDeletionActivity.LoginActivitySmsView.this, true);
                  if (CancelAccountDeletionActivity.LoginActivitySmsView.this.currentType == 3) {
                    break;
                  }
                  if ((!paramAnonymousTL_error.text.contains("PHONE_CODE_EMPTY")) && (!paramAnonymousTL_error.text.contains("PHONE_CODE_INVALID"))) {
                    break label369;
                  }
                  CancelAccountDeletionActivity.this.needShowAlert(LocaleController.getString("InvalidCode", 2131165754));
                  return;
                  if (CancelAccountDeletionActivity.LoginActivitySmsView.this.currentType == 3)
                  {
                    AndroidUtilities.setWaitingForCall(true);
                    NotificationCenter.getInstance().addObserver(CancelAccountDeletionActivity.LoginActivitySmsView.this, NotificationCenter.didReceiveCall);
                  }
                }
                label369:
                if (paramAnonymousTL_error.text.contains("PHONE_CODE_EXPIRED"))
                {
                  CancelAccountDeletionActivity.this.needShowAlert(LocaleController.getString("CodeExpired", 2131165517));
                  return;
                }
                if (paramAnonymousTL_error.text.startsWith("FLOOD_WAIT"))
                {
                  CancelAccountDeletionActivity.this.needShowAlert(LocaleController.getString("FloodWait", 2131165639));
                  return;
                }
                CancelAccountDeletionActivity.this.needShowAlert(paramAnonymousTL_error.text);
              }
            });
          }
        }, 2);
        return;
        if (this.currentType == 3)
        {
          AndroidUtilities.setWaitingForCall(false);
          NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didReceiveCall);
        }
      }
    }
    
    public void onShow()
    {
      super.onShow();
      if (this.codeField != null)
      {
        this.codeField.requestFocus();
        this.codeField.setSelection(this.codeField.length());
      }
    }
    
    public void setParams(Bundle paramBundle)
    {
      int j = 0;
      if (paramBundle == null) {}
      int i;
      label167:
      label188:
      do
      {
        return;
        this.codeField.setText("");
        this.waitingForEvent = true;
        if (this.currentType != 2) {
          break;
        }
        AndroidUtilities.setWaitingForSms(true);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.didReceiveSmsCode);
        this.currentParams = paramBundle;
        this.phone = paramBundle.getString("phone");
        this.phoneHash = paramBundle.getString("phoneHash");
        i = paramBundle.getInt("timeout");
        this.time = i;
        this.timeout = i;
        this.openTime = ((int)(System.currentTimeMillis() / 1000L));
        this.nextType = paramBundle.getInt("nextType");
        this.pattern = paramBundle.getString("pattern");
        this.length = paramBundle.getInt("length");
        if (this.length == 0) {
          break label355;
        }
        paramBundle = new InputFilter.LengthFilter(this.length);
        this.codeField.setFilters(new InputFilter[] { paramBundle });
        if (this.progressView != null)
        {
          paramBundle = this.progressView;
          if (this.nextType == 0) {
            break label369;
          }
          i = 0;
          paramBundle.setVisibility(i);
        }
      } while (this.phone == null);
      paramBundle = PhoneFormat.getInstance().format(this.phone);
      paramBundle = AndroidUtilities.replaceTags(LocaleController.formatString("CancelAccountResetInfo", 2131165388, new Object[] { PhoneFormat.getInstance().format("+" + paramBundle) }));
      this.confirmTextView.setText(paramBundle);
      if (this.currentType != 3)
      {
        AndroidUtilities.showKeyboard(this.codeField);
        this.codeField.requestFocus();
      }
      for (;;)
      {
        destroyTimer();
        destroyCodeTimer();
        this.lastCurrentTime = System.currentTimeMillis();
        if (this.currentType != 1) {
          break label385;
        }
        this.problemText.setVisibility(0);
        this.timeText.setVisibility(8);
        return;
        if (this.currentType != 3) {
          break;
        }
        AndroidUtilities.setWaitingForCall(true);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.didReceiveCall);
        break;
        label355:
        this.codeField.setFilters(new InputFilter[0]);
        break label167;
        label369:
        i = 8;
        break label188;
        AndroidUtilities.hideKeyboard(this.codeField);
      }
      label385:
      if ((this.currentType == 3) && ((this.nextType == 4) || (this.nextType == 2)))
      {
        this.problemText.setVisibility(8);
        this.timeText.setVisibility(0);
        if (this.nextType == 4) {
          this.timeText.setText(LocaleController.formatString("CallText", 2131165384, new Object[] { Integer.valueOf(1), Integer.valueOf(0) }));
        }
        for (;;)
        {
          createTimer();
          return;
          if (this.nextType == 2) {
            this.timeText.setText(LocaleController.formatString("SmsText", 2131166300, new Object[] { Integer.valueOf(1), Integer.valueOf(0) }));
          }
        }
      }
      if ((this.currentType == 2) && ((this.nextType == 4) || (this.nextType == 3)))
      {
        this.timeText.setVisibility(0);
        this.timeText.setText(LocaleController.formatString("CallText", 2131165384, new Object[] { Integer.valueOf(2), Integer.valueOf(0) }));
        paramBundle = this.problemText;
        if (this.time < 1000) {}
        for (i = j;; i = 8)
        {
          paramBundle.setVisibility(i);
          createTimer();
          return;
        }
      }
      this.timeText.setVisibility(8);
      this.problemText.setVisibility(8);
      createCodeTimer();
    }
    
    private class ProgressView
      extends View
    {
      private Paint paint = new Paint();
      private Paint paint2 = new Paint();
      private float progress;
      
      public ProgressView(Context paramContext)
      {
        super();
        this.paint.setColor(-1971470);
        this.paint2.setColor(-10313520);
      }
      
      protected void onDraw(Canvas paramCanvas)
      {
        int i = (int)(getMeasuredWidth() * this.progress);
        paramCanvas.drawRect(0.0F, 0.0F, i, getMeasuredHeight(), this.paint2);
        paramCanvas.drawRect(i, 0.0F, getMeasuredWidth(), getMeasuredHeight(), this.paint);
      }
      
      public void setProgress(float paramFloat)
      {
        this.progress = paramFloat;
        invalidate();
      }
    }
  }
  
  public class PhoneView
    extends SlideView
  {
    private boolean nextPressed = false;
    
    public PhoneView(Context paramContext)
    {
      super();
      setOrientation(1);
      this$1 = new FrameLayout(paramContext);
      addView(CancelAccountDeletionActivity.this, LayoutHelper.createLinear(-1, 200));
      CancelAccountDeletionActivity.this.addView(new ProgressBar(paramContext), LayoutHelper.createFrame(-2, -2, 17));
    }
    
    public String getHeaderName()
    {
      return LocaleController.getString("CancelAccountReset", 2131165387);
    }
    
    public void onNextPressed()
    {
      if ((CancelAccountDeletionActivity.this.getParentActivity() == null) || (this.nextPressed)) {
        return;
      }
      final Object localObject = (TelephonyManager)ApplicationLoader.applicationContext.getSystemService("phone");
      int i;
      if ((((TelephonyManager)localObject).getSimState() != 1) && (((TelephonyManager)localObject).getPhoneType() != 0))
      {
        i = 1;
        if ((Build.VERSION.SDK_INT >= 23) && (i != 0)) {}
        localTL_account_sendConfirmPhoneCode = new TLRPC.TL_account_sendConfirmPhoneCode();
        localTL_account_sendConfirmPhoneCode.allow_flashcall = false;
        localTL_account_sendConfirmPhoneCode.hash = CancelAccountDeletionActivity.this.hash;
        if (!localTL_account_sendConfirmPhoneCode.allow_flashcall) {}
      }
      for (;;)
      {
        try
        {
          localObject = ((TelephonyManager)localObject).getLine1Number();
          if ((localObject == null) || (((String)localObject).length() == 0)) {
            continue;
          }
          if (CancelAccountDeletionActivity.this.phone.contains((CharSequence)localObject)) {
            continue;
          }
          if (!((String)localObject).contains(CancelAccountDeletionActivity.this.phone)) {
            continue;
          }
        }
        catch (Exception localException)
        {
          localTL_account_sendConfirmPhoneCode.allow_flashcall = false;
          FileLog.e("tmessages", localException);
          continue;
          boolean bool = true;
          continue;
        }
        localTL_account_sendConfirmPhoneCode.current_number = bool;
        localObject = new Bundle();
        ((Bundle)localObject).putString("phone", CancelAccountDeletionActivity.this.phone);
        this.nextPressed = true;
        ConnectionsManager.getInstance().sendRequest(localTL_account_sendConfirmPhoneCode, new RequestDelegate()
        {
          public void run(final TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
          {
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                CancelAccountDeletionActivity.PhoneView.access$402(CancelAccountDeletionActivity.PhoneView.this, false);
                if (paramAnonymousTL_error == null) {
                  CancelAccountDeletionActivity.this.fillNextCodeParams(CancelAccountDeletionActivity.PhoneView.1.this.val$params, (TLRPC.TL_auth_sentCode)paramAnonymousTLObject);
                }
                do
                {
                  return;
                  if (paramAnonymousTL_error.code == 400)
                  {
                    CancelAccountDeletionActivity.access$602(CancelAccountDeletionActivity.this, CancelAccountDeletionActivity.this.needShowAlert(LocaleController.getString("CancelLinkExpired", 2131165389)));
                    return;
                  }
                } while (paramAnonymousTL_error.text == null);
                if (paramAnonymousTL_error.text.startsWith("FLOOD_WAIT"))
                {
                  CancelAccountDeletionActivity.access$602(CancelAccountDeletionActivity.this, CancelAccountDeletionActivity.this.needShowAlert(LocaleController.getString("FloodWait", 2131165639)));
                  return;
                }
                CancelAccountDeletionActivity.access$602(CancelAccountDeletionActivity.this, CancelAccountDeletionActivity.this.needShowAlert(LocaleController.getString("ErrorOccurred", 2131165626)));
              }
            });
          }
        }, 2);
        return;
        i = 0;
        break;
        bool = false;
      }
    }
    
    public void onShow()
    {
      super.onShow();
      onNextPressed();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/CancelAccountDeletionActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */