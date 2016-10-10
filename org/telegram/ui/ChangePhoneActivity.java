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
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
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
import android.text.TextUtils.TruncateAt;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AnimatorListenerAdapterProxy;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_changePhone;
import org.telegram.tgnet.TLRPC.TL_account_sendChangePhoneCode;
import org.telegram.tgnet.TLRPC.TL_auth_cancelCode;
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
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.auth_SentCodeType;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Components.HintEditText;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.SlideView;

public class ChangePhoneActivity
  extends BaseFragment
{
  private static final int done_button = 1;
  private boolean checkPermissions = true;
  private int currentViewNum = 0;
  private View doneButton;
  private Dialog permissionsDialog;
  private ArrayList<String> permissionsItems = new ArrayList();
  private ProgressDialog progressDialog;
  private SlideView[] views = new SlideView[5];
  
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
          ChangePhoneActivity.this.views[ChangePhoneActivity.this.currentViewNum].onNextPressed();
        }
        while (paramAnonymousInt != -1) {
          return;
        }
        ChangePhoneActivity.this.finishFragment();
      }
    });
    this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, 2130837720, AndroidUtilities.dp(56.0F));
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
      label220:
      float f1;
      label243:
      float f2;
      if (i == 0)
      {
        j = 0;
        paramContext.setVisibility(j);
        paramContext = this.views[i];
        if (i != 0) {
          break label300;
        }
        f1 = -2.0F;
        if (!AndroidUtilities.isTablet()) {
          break label307;
        }
        f2 = 26.0F;
        label253:
        if (!AndroidUtilities.isTablet()) {
          break label314;
        }
      }
      label300:
      label307:
      label314:
      for (float f3 = 26.0F;; f3 = 18.0F)
      {
        localFrameLayout.addView(paramContext, LayoutHelper.createFrame(-1, f1, 51, f2, 30.0F, f3, 0.0F));
        i += 1;
        break;
        j = 8;
        break label220;
        f1 = -1.0F;
        break label243;
        f2 = 18.0F;
        break label253;
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
  
  public void needShowAlert(String paramString)
  {
    if ((paramString == null) || (getParentActivity() == null)) {
      return;
    }
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(getParentActivity());
    localBuilder.setTitle(LocaleController.getString("AppName", 2131165299));
    localBuilder.setMessage(paramString);
    localBuilder.setPositiveButton(LocaleController.getString("OK", 2131166044), null);
    showDialog(localBuilder.create());
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
    boolean bool2 = true;
    if (this.currentViewNum == 0)
    {
      int i = 0;
      for (;;)
      {
        bool1 = bool2;
        if (i >= this.views.length) {
          break;
        }
        if (this.views[i] != null) {
          this.views[i].onDestroyActivity();
        }
        i += 1;
      }
    }
    this.views[this.currentViewNum].onBackPressed();
    setPage(0, true, null, true);
    boolean bool1 = false;
    return bool1;
  }
  
  protected void onDialogDismiss(Dialog paramDialog)
  {
    if ((Build.VERSION.SDK_INT >= 23) && (paramDialog == this.permissionsDialog) && (!this.permissionsItems.isEmpty())) {
      getParentActivity().requestPermissions((String[])this.permissionsItems.toArray(new String[this.permissionsItems.size()]), 6);
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
    if (paramInt == 3)
    {
      this.doneButton.setVisibility(8);
      localSlideView1 = this.views[this.currentViewNum];
      localSlideView2 = this.views[paramInt];
      this.currentViewNum = paramInt;
      localSlideView2.setParams(paramBundle);
      this.actionBar.setTitle(localSlideView2.getHeaderName());
      localSlideView2.onShow();
      if (!paramBoolean2) {
        break label210;
      }
      f = -AndroidUtilities.displaySize.x;
      label76:
      localSlideView2.setX(f);
      paramBundle = new AnimatorSet();
      paramBundle.setInterpolator(new AccelerateDecelerateInterpolator());
      paramBundle.setDuration(300L);
      if (!paramBoolean2) {
        break label222;
      }
    }
    label210:
    label222:
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
      if (paramInt == 0) {
        this.checkPermissions = true;
      }
      this.doneButton.setVisibility(0);
      break;
      f = AndroidUtilities.displaySize.x;
      break label76;
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
    private String emailPhone;
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
    private String requestPhone;
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
          break label941;
        }
        localObject1 = new FrameLayout(paramContext);
        localObject2 = new ImageView(paramContext);
        ((ImageView)localObject2).setImageResource(2130837877);
        if (!LocaleController.isRTL) {
          break label875;
        }
        ((FrameLayout)localObject1).addView((View)localObject2, LayoutHelper.createFrame(64, 76.0F, 19, 2.0F, 2.0F, 0.0F, 0.0F));
        localObject2 = this.confirmTextView;
        if (!LocaleController.isRTL) {
          break label870;
        }
        paramInt = 5;
        label195:
        ((FrameLayout)localObject1).addView((View)localObject2, LayoutHelper.createFrame(-1, -2.0F, paramInt, 82.0F, 0.0F, 0.0F, 0.0F));
        if (!LocaleController.isRTL) {
          break label936;
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
            if (ChangePhoneActivity.LoginActivitySmsView.this.ignoreOnTextChange) {}
            while ((ChangePhoneActivity.LoginActivitySmsView.this.length == 0) || (ChangePhoneActivity.LoginActivitySmsView.this.codeField.length() != ChangePhoneActivity.LoginActivitySmsView.this.length)) {
              return;
            }
            ChangePhoneActivity.LoginActivitySmsView.this.onNextPressed();
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
              ChangePhoneActivity.LoginActivitySmsView.this.onNextPressed();
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
          break label977;
        }
        paramInt = 5;
        label476:
        ((TextView)localObject1).setGravity(paramInt);
        localObject1 = this.timeText;
        if (!LocaleController.isRTL) {
          break label982;
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
        localObject1 = this.problemText;
        if (!LocaleController.isRTL) {
          break label987;
        }
        paramInt = 5;
        label594:
        ((TextView)localObject1).setGravity(paramInt);
        this.problemText.setTextSize(1, 14.0F);
        this.problemText.setTextColor(-11697229);
        this.problemText.setLineSpacing(AndroidUtilities.dp(2.0F), 1.0F);
        this.problemText.setPadding(0, AndroidUtilities.dp(2.0F), 0, AndroidUtilities.dp(12.0F));
        localObject1 = this.problemText;
        if (!LocaleController.isRTL) {
          break label992;
        }
        paramInt = 5;
        label665:
        addView((View)localObject1, LayoutHelper.createLinear(-2, -2, paramInt, 0, 20, 0, 0));
        this.problemText.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            if (ChangePhoneActivity.LoginActivitySmsView.this.nextPressed) {
              return;
            }
            if ((ChangePhoneActivity.LoginActivitySmsView.this.nextType != 0) && (ChangePhoneActivity.LoginActivitySmsView.this.nextType != 4))
            {
              ChangePhoneActivity.LoginActivitySmsView.this.resendCode();
              return;
            }
            try
            {
              paramAnonymousView = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
              paramAnonymousView = String.format(Locale.US, "%s (%d)", new Object[] { paramAnonymousView.versionName, Integer.valueOf(paramAnonymousView.versionCode) });
              Intent localIntent = new Intent("android.intent.action.SEND");
              localIntent.setType("message/rfc822");
              localIntent.putExtra("android.intent.extra.EMAIL", new String[] { "sms@stel.com" });
              localIntent.putExtra("android.intent.extra.SUBJECT", "Android registration/login issue " + paramAnonymousView + " " + ChangePhoneActivity.LoginActivitySmsView.this.emailPhone);
              localIntent.putExtra("android.intent.extra.TEXT", "Phone: " + ChangePhoneActivity.LoginActivitySmsView.this.requestPhone + "\nApp version: " + paramAnonymousView + "\nOS version: SDK " + Build.VERSION.SDK_INT + "\nDevice Name: " + Build.MANUFACTURER + Build.MODEL + "\nLocale: " + Locale.getDefault() + "\nError: " + ChangePhoneActivity.LoginActivitySmsView.this.lastError);
              ChangePhoneActivity.LoginActivitySmsView.this.getContext().startActivity(Intent.createChooser(localIntent, "Send email..."));
              return;
            }
            catch (Exception paramAnonymousView)
            {
              ChangePhoneActivity.this.needShowAlert(LocaleController.getString("NoMailInstalled", 2131165935));
            }
          }
        });
        localObject1 = new LinearLayout(paramContext);
        if (!LocaleController.isRTL) {
          break label997;
        }
        paramInt = 5;
        label718:
        ((LinearLayout)localObject1).setGravity(paramInt | 0x10);
        if (!LocaleController.isRTL) {
          break label1002;
        }
        paramInt = 5;
        label735:
        addView((View)localObject1, LayoutHelper.createLinear(-1, -1, paramInt));
        paramContext = new TextView(paramContext);
        if (!LocaleController.isRTL) {
          break label1007;
        }
        paramInt = 5;
        label764:
        paramContext.setGravity(paramInt | 0x1);
        paramContext.setTextColor(-11697229);
        paramContext.setTextSize(1, 14.0F);
        paramContext.setLineSpacing(AndroidUtilities.dp(2.0F), 1.0F);
        paramContext.setPadding(0, AndroidUtilities.dp(24.0F), 0, 0);
        if (!LocaleController.isRTL) {
          break label1012;
        }
      }
      label870:
      label875:
      label936:
      label941:
      label977:
      label982:
      label987:
      label992:
      label997:
      label1002:
      label1007:
      label1012:
      for (paramInt = 5;; paramInt = 3)
      {
        ((LinearLayout)localObject1).addView(paramContext, LayoutHelper.createLinear(-2, -2, paramInt | 0x50, 0, 0, 0, 10));
        paramContext.setText(LocaleController.getString("WrongNumber", 2131166407));
        paramContext.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            paramAnonymousView = new TLRPC.TL_auth_cancelCode();
            paramAnonymousView.phone_number = ChangePhoneActivity.LoginActivitySmsView.this.requestPhone;
            paramAnonymousView.phone_code_hash = ChangePhoneActivity.LoginActivitySmsView.this.phoneHash;
            ConnectionsManager.getInstance().sendRequest(paramAnonymousView, new RequestDelegate()
            {
              public void run(TLObject paramAnonymous2TLObject, TLRPC.TL_error paramAnonymous2TL_error) {}
            }, 2);
            ChangePhoneActivity.LoginActivitySmsView.this.onBackPressed();
            ChangePhoneActivity.this.setPage(0, true, null, true);
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
        break label594;
        paramInt = 3;
        break label665;
        paramInt = 3;
        break label718;
        paramInt = 3;
        break label735;
        paramInt = 3;
        break label764;
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
          double d2 = ChangePhoneActivity.LoginActivitySmsView.this.lastCodeTime;
          ChangePhoneActivity.LoginActivitySmsView.access$2802(ChangePhoneActivity.LoginActivitySmsView.this, (int)(ChangePhoneActivity.LoginActivitySmsView.this.codeTime - (d1 - d2)));
          ChangePhoneActivity.LoginActivitySmsView.access$2702(ChangePhoneActivity.LoginActivitySmsView.this, d1);
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              if (ChangePhoneActivity.LoginActivitySmsView.this.codeTime <= 1000)
              {
                ChangePhoneActivity.LoginActivitySmsView.this.problemText.setVisibility(0);
                ChangePhoneActivity.LoginActivitySmsView.this.destroyCodeTimer();
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
          if (ChangePhoneActivity.LoginActivitySmsView.this.timeTimer == null) {
            return;
          }
          double d1 = System.currentTimeMillis();
          double d2 = ChangePhoneActivity.LoginActivitySmsView.this.lastCurrentTime;
          ChangePhoneActivity.LoginActivitySmsView.access$3302(ChangePhoneActivity.LoginActivitySmsView.this, (int)(ChangePhoneActivity.LoginActivitySmsView.this.time - (d1 - d2)));
          ChangePhoneActivity.LoginActivitySmsView.access$3202(ChangePhoneActivity.LoginActivitySmsView.this, d1);
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              int i;
              int j;
              if (ChangePhoneActivity.LoginActivitySmsView.this.time >= 1000)
              {
                i = ChangePhoneActivity.LoginActivitySmsView.this.time / 1000 / 60;
                j = ChangePhoneActivity.LoginActivitySmsView.this.time / 1000 - i * 60;
                if ((ChangePhoneActivity.LoginActivitySmsView.this.nextType == 4) || (ChangePhoneActivity.LoginActivitySmsView.this.nextType == 3)) {
                  ChangePhoneActivity.LoginActivitySmsView.this.timeText.setText(LocaleController.formatString("CallText", 2131165384, new Object[] { Integer.valueOf(i), Integer.valueOf(j) }));
                }
              }
              do
              {
                do
                {
                  for (;;)
                  {
                    if (ChangePhoneActivity.LoginActivitySmsView.this.progressView != null) {
                      ChangePhoneActivity.LoginActivitySmsView.this.progressView.setProgress(1.0F - ChangePhoneActivity.LoginActivitySmsView.this.time / ChangePhoneActivity.LoginActivitySmsView.this.timeout);
                    }
                    return;
                    if (ChangePhoneActivity.LoginActivitySmsView.this.nextType == 2) {
                      ChangePhoneActivity.LoginActivitySmsView.this.timeText.setText(LocaleController.formatString("SmsText", 2131166300, new Object[] { Integer.valueOf(i), Integer.valueOf(j) }));
                    }
                  }
                  if (ChangePhoneActivity.LoginActivitySmsView.this.progressView != null) {
                    ChangePhoneActivity.LoginActivitySmsView.this.progressView.setProgress(1.0F);
                  }
                  ChangePhoneActivity.LoginActivitySmsView.this.destroyTimer();
                  if (ChangePhoneActivity.LoginActivitySmsView.this.currentType == 3)
                  {
                    AndroidUtilities.setWaitingForCall(false);
                    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didReceiveCall);
                    ChangePhoneActivity.LoginActivitySmsView.access$3902(ChangePhoneActivity.LoginActivitySmsView.this, false);
                    ChangePhoneActivity.LoginActivitySmsView.this.destroyCodeTimer();
                    ChangePhoneActivity.LoginActivitySmsView.this.resendCode();
                    return;
                  }
                } while (ChangePhoneActivity.LoginActivitySmsView.this.currentType != 2);
                if (ChangePhoneActivity.LoginActivitySmsView.this.nextType == 4)
                {
                  ChangePhoneActivity.LoginActivitySmsView.this.timeText.setText(LocaleController.getString("Calling", 2131165385));
                  ChangePhoneActivity.LoginActivitySmsView.this.createCodeTimer();
                  TLRPC.TL_auth_resendCode localTL_auth_resendCode = new TLRPC.TL_auth_resendCode();
                  localTL_auth_resendCode.phone_number = ChangePhoneActivity.LoginActivitySmsView.this.requestPhone;
                  localTL_auth_resendCode.phone_code_hash = ChangePhoneActivity.LoginActivitySmsView.this.phoneHash;
                  ConnectionsManager.getInstance().sendRequest(localTL_auth_resendCode, new RequestDelegate()
                  {
                    public void run(TLObject paramAnonymous3TLObject, final TLRPC.TL_error paramAnonymous3TL_error)
                    {
                      if ((paramAnonymous3TL_error != null) && (paramAnonymous3TL_error.text != null)) {
                        AndroidUtilities.runOnUIThread(new Runnable()
                        {
                          public void run()
                          {
                            ChangePhoneActivity.LoginActivitySmsView.access$2502(ChangePhoneActivity.LoginActivitySmsView.this, paramAnonymous3TL_error.text);
                          }
                        });
                      }
                    }
                  }, 2);
                  return;
                }
              } while (ChangePhoneActivity.LoginActivitySmsView.this.nextType != 3);
              AndroidUtilities.setWaitingForSms(false);
              NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didReceiveSmsCode);
              ChangePhoneActivity.LoginActivitySmsView.access$3902(ChangePhoneActivity.LoginActivitySmsView.this, false);
              ChangePhoneActivity.LoginActivitySmsView.this.destroyCodeTimer();
              ChangePhoneActivity.LoginActivitySmsView.this.resendCode();
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
      localBundle.putString("ephone", this.emailPhone);
      localBundle.putString("phoneFormated", this.requestPhone);
      this.nextPressed = true;
      ChangePhoneActivity.this.needShowProgress();
      TLRPC.TL_auth_resendCode localTL_auth_resendCode = new TLRPC.TL_auth_resendCode();
      localTL_auth_resendCode.phone_number = this.requestPhone;
      localTL_auth_resendCode.phone_code_hash = this.phoneHash;
      ConnectionsManager.getInstance().sendRequest(localTL_auth_resendCode, new RequestDelegate()
      {
        public void run(final TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
        {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              ChangePhoneActivity.LoginActivitySmsView.access$2002(ChangePhoneActivity.LoginActivitySmsView.this, false);
              if (paramAnonymousTL_error == null) {
                ChangePhoneActivity.this.fillNextCodeParams(ChangePhoneActivity.LoginActivitySmsView.5.this.val$params, (TLRPC.TL_auth_sentCode)paramAnonymousTLObject);
              }
              for (;;)
              {
                ChangePhoneActivity.this.needHideProgress();
                return;
                if (paramAnonymousTL_error.text != null) {
                  if (paramAnonymousTL_error.text.contains("PHONE_NUMBER_INVALID"))
                  {
                    ChangePhoneActivity.this.needShowAlert(LocaleController.getString("InvalidPhoneNumber", 2131165758));
                  }
                  else if ((paramAnonymousTL_error.text.contains("PHONE_CODE_EMPTY")) || (paramAnonymousTL_error.text.contains("PHONE_CODE_INVALID")))
                  {
                    ChangePhoneActivity.this.needShowAlert(LocaleController.getString("InvalidCode", 2131165754));
                  }
                  else if (paramAnonymousTL_error.text.contains("PHONE_CODE_EXPIRED"))
                  {
                    ChangePhoneActivity.LoginActivitySmsView.this.onBackPressed();
                    ChangePhoneActivity.this.setPage(0, true, null, true);
                    ChangePhoneActivity.this.needShowAlert(LocaleController.getString("CodeExpired", 2131165517));
                  }
                  else if (paramAnonymousTL_error.text.startsWith("FLOOD_WAIT"))
                  {
                    ChangePhoneActivity.this.needShowAlert(LocaleController.getString("FloodWait", 2131165639));
                  }
                  else if (paramAnonymousTL_error.code != 64536)
                  {
                    ChangePhoneActivity.this.needShowAlert(LocaleController.getString("ErrorOccurred", 2131165626) + "\n" + paramAnonymousTL_error.text);
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
      return LocaleController.getString("YourCode", 2131166420);
    }
    
    public void onBackPressed()
    {
      destroyTimer();
      destroyCodeTimer();
      this.currentParams = null;
      if (this.currentType == 2)
      {
        AndroidUtilities.setWaitingForSms(false);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didReceiveSmsCode);
      }
      for (;;)
      {
        this.waitingForEvent = false;
        return;
        if (this.currentType == 3)
        {
          AndroidUtilities.setWaitingForCall(false);
          NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didReceiveCall);
        }
      }
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
        TLRPC.TL_account_changePhone localTL_account_changePhone = new TLRPC.TL_account_changePhone();
        localTL_account_changePhone.phone_number = this.requestPhone;
        localTL_account_changePhone.phone_code = this.codeField.getText().toString();
        localTL_account_changePhone.phone_code_hash = this.phoneHash;
        destroyTimer();
        ChangePhoneActivity.this.needShowProgress();
        ConnectionsManager.getInstance().sendRequest(localTL_account_changePhone, new RequestDelegate()
        {
          public void run(final TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
          {
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                ChangePhoneActivity.this.needHideProgress();
                ChangePhoneActivity.LoginActivitySmsView.access$2002(ChangePhoneActivity.LoginActivitySmsView.this, false);
                if (paramAnonymousTL_error == null)
                {
                  TLRPC.User localUser = (TLRPC.User)paramAnonymousTLObject;
                  ChangePhoneActivity.LoginActivitySmsView.this.destroyTimer();
                  ChangePhoneActivity.LoginActivitySmsView.this.destroyCodeTimer();
                  UserConfig.setCurrentUser(localUser);
                  UserConfig.saveConfig(true);
                  ArrayList localArrayList = new ArrayList();
                  localArrayList.add(localUser);
                  MessagesStorage.getInstance().putUsersAndChats(localArrayList, null, true, true);
                  MessagesController.getInstance().putUser(localUser, false);
                  ChangePhoneActivity.this.finishFragment();
                  NotificationCenter.getInstance().postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
                  return;
                }
                ChangePhoneActivity.LoginActivitySmsView.access$2502(ChangePhoneActivity.LoginActivitySmsView.this, paramAnonymousTL_error.text);
                if (((ChangePhoneActivity.LoginActivitySmsView.this.currentType == 3) && ((ChangePhoneActivity.LoginActivitySmsView.this.nextType == 4) || (ChangePhoneActivity.LoginActivitySmsView.this.nextType == 2))) || ((ChangePhoneActivity.LoginActivitySmsView.this.currentType == 2) && ((ChangePhoneActivity.LoginActivitySmsView.this.nextType == 4) || (ChangePhoneActivity.LoginActivitySmsView.this.nextType == 3)))) {
                  ChangePhoneActivity.LoginActivitySmsView.this.createTimer();
                }
                if (ChangePhoneActivity.LoginActivitySmsView.this.currentType == 2)
                {
                  AndroidUtilities.setWaitingForSms(true);
                  NotificationCenter.getInstance().addObserver(ChangePhoneActivity.LoginActivitySmsView.this, NotificationCenter.didReceiveSmsCode);
                }
                for (;;)
                {
                  ChangePhoneActivity.LoginActivitySmsView.access$3902(ChangePhoneActivity.LoginActivitySmsView.this, true);
                  if (ChangePhoneActivity.LoginActivitySmsView.this.currentType == 3) {
                    break;
                  }
                  if (!paramAnonymousTL_error.text.contains("PHONE_NUMBER_INVALID")) {
                    break label373;
                  }
                  ChangePhoneActivity.this.needShowAlert(LocaleController.getString("InvalidPhoneNumber", 2131165758));
                  return;
                  if (ChangePhoneActivity.LoginActivitySmsView.this.currentType == 3)
                  {
                    AndroidUtilities.setWaitingForCall(true);
                    NotificationCenter.getInstance().addObserver(ChangePhoneActivity.LoginActivitySmsView.this, NotificationCenter.didReceiveCall);
                  }
                }
                label373:
                if ((paramAnonymousTL_error.text.contains("PHONE_CODE_EMPTY")) || (paramAnonymousTL_error.text.contains("PHONE_CODE_INVALID")))
                {
                  ChangePhoneActivity.this.needShowAlert(LocaleController.getString("InvalidCode", 2131165754));
                  return;
                }
                if (paramAnonymousTL_error.text.contains("PHONE_CODE_EXPIRED"))
                {
                  ChangePhoneActivity.this.needShowAlert(LocaleController.getString("CodeExpired", 2131165517));
                  return;
                }
                if (paramAnonymousTL_error.text.startsWith("FLOOD_WAIT"))
                {
                  ChangePhoneActivity.this.needShowAlert(LocaleController.getString("FloodWait", 2131165639));
                  return;
                }
                ChangePhoneActivity.this.needShowAlert(paramAnonymousTL_error.text);
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
      label189:
      label210:
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
        this.emailPhone = paramBundle.getString("ephone");
        this.requestPhone = paramBundle.getString("phoneFormated");
        this.phoneHash = paramBundle.getString("phoneHash");
        i = paramBundle.getInt("timeout");
        this.time = i;
        this.timeout = i;
        this.openTime = ((int)(System.currentTimeMillis() / 1000L));
        this.nextType = paramBundle.getInt("nextType");
        this.pattern = paramBundle.getString("pattern");
        this.length = paramBundle.getInt("length");
        if (this.length == 0) {
          break label356;
        }
        paramBundle = new InputFilter.LengthFilter(this.length);
        this.codeField.setFilters(new InputFilter[] { paramBundle });
        if (this.progressView != null)
        {
          paramBundle = this.progressView;
          if (this.nextType == 0) {
            break label370;
          }
          i = 0;
          paramBundle.setVisibility(i);
        }
      } while (this.phone == null);
      String str = PhoneFormat.getInstance().format(this.phone);
      paramBundle = "";
      if (this.currentType == 1)
      {
        paramBundle = AndroidUtilities.replaceTags(LocaleController.getString("SentAppCode", 2131166252));
        label258:
        this.confirmTextView.setText(paramBundle);
        if (this.currentType == 3) {
          break label475;
        }
        AndroidUtilities.showKeyboard(this.codeField);
        this.codeField.requestFocus();
      }
      for (;;)
      {
        destroyTimer();
        destroyCodeTimer();
        this.lastCurrentTime = System.currentTimeMillis();
        if (this.currentType != 1) {
          break label485;
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
        label356:
        this.codeField.setFilters(new InputFilter[0]);
        break label189;
        label370:
        i = 8;
        break label210;
        if (this.currentType == 2)
        {
          paramBundle = AndroidUtilities.replaceTags(LocaleController.formatString("SentSmsCode", 2131166255, new Object[] { str }));
          break label258;
        }
        if (this.currentType == 3)
        {
          paramBundle = AndroidUtilities.replaceTags(LocaleController.formatString("SentCallCode", 2131166253, new Object[] { str }));
          break label258;
        }
        if (this.currentType != 4) {
          break label258;
        }
        paramBundle = AndroidUtilities.replaceTags(LocaleController.formatString("SentCallOnly", 2131166254, new Object[] { str }));
        break label258;
        label475:
        AndroidUtilities.hideKeyboard(this.codeField);
      }
      label485:
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
    implements AdapterView.OnItemSelectedListener
  {
    private EditText codeField;
    private HashMap<String, String> codesMap = new HashMap();
    private ArrayList<String> countriesArray = new ArrayList();
    private HashMap<String, String> countriesMap = new HashMap();
    private TextView countryButton;
    private int countryState = 0;
    private boolean ignoreOnPhoneChange = false;
    private boolean ignoreOnTextChange = false;
    private boolean ignoreSelection = false;
    private boolean nextPressed = false;
    private HintEditText phoneField;
    private HashMap<String, String> phoneFormatMap = new HashMap();
    
    public PhoneView(Context paramContext)
    {
      super();
      setOrientation(1);
      this.countryButton = new TextView(paramContext);
      this.countryButton.setTextSize(1, 18.0F);
      this.countryButton.setPadding(AndroidUtilities.dp(12.0F), AndroidUtilities.dp(10.0F), AndroidUtilities.dp(12.0F), 0);
      this.countryButton.setTextColor(-14606047);
      this.countryButton.setMaxLines(1);
      this.countryButton.setSingleLine(true);
      this.countryButton.setEllipsize(TextUtils.TruncateAt.END);
      Object localObject1 = this.countryButton;
      int i;
      Object localObject2;
      if (LocaleController.isRTL)
      {
        i = 5;
        ((TextView)localObject1).setGravity(i | 0x1);
        this.countryButton.setBackgroundResource(2130837987);
        addView(this.countryButton, LayoutHelper.createLinear(-1, 36, 0.0F, 0.0F, 0.0F, 14.0F));
        this.countryButton.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            paramAnonymousView = new CountrySelectActivity();
            paramAnonymousView.setCountrySelectActivityDelegate(new CountrySelectActivity.CountrySelectActivityDelegate()
            {
              public void didSelectCountry(String paramAnonymous2String)
              {
                ChangePhoneActivity.PhoneView.this.selectCountry(paramAnonymous2String);
                AndroidUtilities.runOnUIThread(new Runnable()
                {
                  public void run()
                  {
                    AndroidUtilities.showKeyboard(ChangePhoneActivity.PhoneView.this.phoneField);
                  }
                }, 300L);
                ChangePhoneActivity.PhoneView.this.phoneField.requestFocus();
                ChangePhoneActivity.PhoneView.this.phoneField.setSelection(ChangePhoneActivity.PhoneView.this.phoneField.length());
              }
            });
            ChangePhoneActivity.this.presentFragment(paramAnonymousView);
          }
        });
        localObject1 = new View(paramContext);
        ((View)localObject1).setPadding(AndroidUtilities.dp(12.0F), 0, AndroidUtilities.dp(12.0F), 0);
        ((View)localObject1).setBackgroundColor(-2368549);
        addView((View)localObject1, LayoutHelper.createLinear(-1, 1, 4.0F, -17.5F, 4.0F, 0.0F));
        localObject1 = new LinearLayout(paramContext);
        ((LinearLayout)localObject1).setOrientation(0);
        addView((View)localObject1, LayoutHelper.createLinear(-1, -2, 0.0F, 20.0F, 0.0F, 0.0F));
        localObject2 = new TextView(paramContext);
        ((TextView)localObject2).setText("+");
        ((TextView)localObject2).setTextColor(-14606047);
        ((TextView)localObject2).setTextSize(1, 18.0F);
        ((LinearLayout)localObject1).addView((View)localObject2, LayoutHelper.createLinear(-2, -2));
        this.codeField = new EditText(paramContext);
        this.codeField.setInputType(3);
        this.codeField.setTextColor(-14606047);
        AndroidUtilities.clearCursorDrawable(this.codeField);
        this.codeField.setPadding(AndroidUtilities.dp(10.0F), 0, 0, 0);
        this.codeField.setTextSize(1, 18.0F);
        this.codeField.setMaxLines(1);
        this.codeField.setGravity(19);
        this.codeField.setImeOptions(268435461);
        localObject2 = new InputFilter.LengthFilter(5);
        this.codeField.setFilters(new InputFilter[] { localObject2 });
        ((LinearLayout)localObject1).addView(this.codeField, LayoutHelper.createLinear(55, 36, -9.0F, 0.0F, 16.0F, 0.0F));
        this.codeField.addTextChangedListener(new TextWatcher()
        {
          public void afterTextChanged(Editable paramAnonymousEditable)
          {
            if (ChangePhoneActivity.PhoneView.this.ignoreOnTextChange) {
              return;
            }
            ChangePhoneActivity.PhoneView.access$302(ChangePhoneActivity.PhoneView.this, true);
            Object localObject3 = PhoneFormat.stripExceptNumbers(ChangePhoneActivity.PhoneView.this.codeField.getText().toString());
            ChangePhoneActivity.PhoneView.this.codeField.setText((CharSequence)localObject3);
            if (((String)localObject3).length() == 0)
            {
              ChangePhoneActivity.PhoneView.this.countryButton.setText(LocaleController.getString("ChooseCountry", 2131165505));
              ChangePhoneActivity.PhoneView.this.phoneField.setHintText(null);
              ChangePhoneActivity.PhoneView.access$602(ChangePhoneActivity.PhoneView.this, 1);
              ChangePhoneActivity.PhoneView.access$302(ChangePhoneActivity.PhoneView.this, false);
              return;
            }
            int j = 0;
            int k = 0;
            paramAnonymousEditable = null;
            Object localObject4 = null;
            Object localObject2 = localObject3;
            label139:
            int i;
            if (((String)localObject3).length() > 4)
            {
              ChangePhoneActivity.PhoneView.access$302(ChangePhoneActivity.PhoneView.this, true);
              j = 4;
              i = k;
              localObject1 = localObject3;
              paramAnonymousEditable = (Editable)localObject4;
              if (j >= 1)
              {
                paramAnonymousEditable = ((String)localObject3).substring(0, j);
                if ((String)ChangePhoneActivity.PhoneView.this.codesMap.get(paramAnonymousEditable) == null) {
                  break label541;
                }
                i = 1;
                localObject2 = ((String)localObject3).substring(j, ((String)localObject3).length()) + ChangePhoneActivity.PhoneView.this.phoneField.getText().toString();
                localObject3 = ChangePhoneActivity.PhoneView.this.codeField;
                localObject1 = paramAnonymousEditable;
                ((EditText)localObject3).setText(paramAnonymousEditable);
                paramAnonymousEditable = (Editable)localObject2;
              }
              j = i;
              localObject2 = localObject1;
              if (i == 0)
              {
                ChangePhoneActivity.PhoneView.access$302(ChangePhoneActivity.PhoneView.this, true);
                paramAnonymousEditable = ((String)localObject1).substring(1, ((String)localObject1).length()) + ChangePhoneActivity.PhoneView.this.phoneField.getText().toString();
                localObject3 = ChangePhoneActivity.PhoneView.this.codeField;
                localObject2 = ((String)localObject1).substring(0, 1);
                ((EditText)localObject3).setText((CharSequence)localObject2);
                j = i;
              }
            }
            Object localObject1 = (String)ChangePhoneActivity.PhoneView.this.codesMap.get(localObject2);
            if (localObject1 != null)
            {
              i = ChangePhoneActivity.PhoneView.this.countriesArray.indexOf(localObject1);
              if (i != -1)
              {
                ChangePhoneActivity.PhoneView.access$902(ChangePhoneActivity.PhoneView.this, true);
                ChangePhoneActivity.PhoneView.this.countryButton.setText((CharSequence)ChangePhoneActivity.PhoneView.this.countriesArray.get(i));
                localObject1 = (String)ChangePhoneActivity.PhoneView.this.phoneFormatMap.get(localObject2);
                localObject2 = ChangePhoneActivity.PhoneView.this.phoneField;
                if (localObject1 != null)
                {
                  localObject1 = ((String)localObject1).replace('X', '–');
                  label447:
                  ((HintEditText)localObject2).setHintText((String)localObject1);
                  ChangePhoneActivity.PhoneView.access$602(ChangePhoneActivity.PhoneView.this, 0);
                }
              }
            }
            for (;;)
            {
              if (j == 0) {
                ChangePhoneActivity.PhoneView.this.codeField.setSelection(ChangePhoneActivity.PhoneView.this.codeField.getText().length());
              }
              if (paramAnonymousEditable == null) {
                break;
              }
              ChangePhoneActivity.PhoneView.this.phoneField.requestFocus();
              ChangePhoneActivity.PhoneView.this.phoneField.setText(paramAnonymousEditable);
              ChangePhoneActivity.PhoneView.this.phoneField.setSelection(ChangePhoneActivity.PhoneView.this.phoneField.length());
              break;
              label541:
              j -= 1;
              break label139;
              localObject1 = null;
              break label447;
              ChangePhoneActivity.PhoneView.this.countryButton.setText(LocaleController.getString("WrongCountry", 2131166406));
              ChangePhoneActivity.PhoneView.this.phoneField.setHintText(null);
              ChangePhoneActivity.PhoneView.access$602(ChangePhoneActivity.PhoneView.this, 2);
              continue;
              ChangePhoneActivity.PhoneView.this.countryButton.setText(LocaleController.getString("WrongCountry", 2131166406));
              ChangePhoneActivity.PhoneView.this.phoneField.setHintText(null);
              ChangePhoneActivity.PhoneView.access$602(ChangePhoneActivity.PhoneView.this, 2);
            }
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
              ChangePhoneActivity.PhoneView.this.phoneField.requestFocus();
              ChangePhoneActivity.PhoneView.this.phoneField.setSelection(ChangePhoneActivity.PhoneView.this.phoneField.length());
              return true;
            }
            return false;
          }
        });
        this.phoneField = new HintEditText(paramContext);
        this.phoneField.setInputType(3);
        this.phoneField.setTextColor(-14606047);
        this.phoneField.setHintTextColor(-6842473);
        this.phoneField.setPadding(0, 0, 0, 0);
        AndroidUtilities.clearCursorDrawable(this.phoneField);
        this.phoneField.setTextSize(1, 18.0F);
        this.phoneField.setMaxLines(1);
        this.phoneField.setGravity(19);
        this.phoneField.setImeOptions(268435461);
        ((LinearLayout)localObject1).addView(this.phoneField, LayoutHelper.createFrame(-1, 36.0F));
        this.phoneField.addTextChangedListener(new TextWatcher()
        {
          private int actionPosition;
          private int characterAction = -1;
          
          public void afterTextChanged(Editable paramAnonymousEditable)
          {
            if (ChangePhoneActivity.PhoneView.this.ignoreOnPhoneChange) {
              return;
            }
            int j = ChangePhoneActivity.PhoneView.this.phoneField.getSelectionStart();
            Object localObject = ChangePhoneActivity.PhoneView.this.phoneField.getText().toString();
            int i = j;
            paramAnonymousEditable = (Editable)localObject;
            if (this.characterAction == 3)
            {
              paramAnonymousEditable = ((String)localObject).substring(0, this.actionPosition) + ((String)localObject).substring(this.actionPosition + 1, ((String)localObject).length());
              i = j - 1;
            }
            localObject = new StringBuilder(paramAnonymousEditable.length());
            j = 0;
            while (j < paramAnonymousEditable.length())
            {
              String str = paramAnonymousEditable.substring(j, j + 1);
              if ("0123456789".contains(str)) {
                ((StringBuilder)localObject).append(str);
              }
              j += 1;
            }
            ChangePhoneActivity.PhoneView.access$1102(ChangePhoneActivity.PhoneView.this, true);
            paramAnonymousEditable = ChangePhoneActivity.PhoneView.this.phoneField.getHintText();
            j = i;
            if (paramAnonymousEditable != null)
            {
              int k = 0;
              for (;;)
              {
                j = i;
                if (k >= ((StringBuilder)localObject).length()) {
                  break label341;
                }
                if (k >= paramAnonymousEditable.length()) {
                  break;
                }
                int m = k;
                j = i;
                if (paramAnonymousEditable.charAt(k) == ' ')
                {
                  ((StringBuilder)localObject).insert(k, ' ');
                  k += 1;
                  m = k;
                  j = i;
                  if (i == k)
                  {
                    m = k;
                    j = i;
                    if (this.characterAction != 2)
                    {
                      m = k;
                      j = i;
                      if (this.characterAction != 3)
                      {
                        j = i + 1;
                        m = k;
                      }
                    }
                  }
                }
                k = m + 1;
                i = j;
              }
              ((StringBuilder)localObject).insert(k, ' ');
              j = i;
              if (i == k + 1)
              {
                j = i;
                if (this.characterAction != 2)
                {
                  j = i;
                  if (this.characterAction != 3) {
                    j = i + 1;
                  }
                }
              }
            }
            label341:
            ChangePhoneActivity.PhoneView.this.phoneField.setText((CharSequence)localObject);
            if (j >= 0)
            {
              paramAnonymousEditable = ChangePhoneActivity.PhoneView.this.phoneField;
              if (j > ChangePhoneActivity.PhoneView.this.phoneField.length()) {
                break label404;
              }
            }
            for (;;)
            {
              paramAnonymousEditable.setSelection(j);
              ChangePhoneActivity.PhoneView.this.phoneField.onTextChange();
              ChangePhoneActivity.PhoneView.access$1102(ChangePhoneActivity.PhoneView.this, false);
              return;
              label404:
              j = ChangePhoneActivity.PhoneView.this.phoneField.length();
            }
          }
          
          public void beforeTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3)
          {
            if ((paramAnonymousInt2 == 0) && (paramAnonymousInt3 == 1))
            {
              this.characterAction = 1;
              return;
            }
            if ((paramAnonymousInt2 == 1) && (paramAnonymousInt3 == 0))
            {
              if ((paramAnonymousCharSequence.charAt(paramAnonymousInt1) == ' ') && (paramAnonymousInt1 > 0))
              {
                this.characterAction = 3;
                this.actionPosition = (paramAnonymousInt1 - 1);
                return;
              }
              this.characterAction = 2;
              return;
            }
            this.characterAction = -1;
          }
          
          public void onTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
        });
        this.phoneField.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
          public boolean onEditorAction(TextView paramAnonymousTextView, int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
          {
            if (paramAnonymousInt == 5)
            {
              ChangePhoneActivity.PhoneView.this.onNextPressed();
              return true;
            }
            return false;
          }
        });
        paramContext = new TextView(paramContext);
        paramContext.setText(LocaleController.getString("ChangePhoneHelp", 2131165397));
        paramContext.setTextColor(-9079435);
        paramContext.setTextSize(1, 14.0F);
        if (!LocaleController.isRTL) {
          break label1088;
        }
        i = 5;
        label711:
        paramContext.setGravity(i);
        paramContext.setLineSpacing(AndroidUtilities.dp(2.0F), 1.0F);
        if (!LocaleController.isRTL) {
          break label1093;
        }
        i = 5;
        label734:
        addView(paramContext, LayoutHelper.createLinear(-2, -2, i, 0, 28, 0, 10));
        localObject1 = new HashMap();
        try
        {
          paramContext = new BufferedReader(new InputStreamReader(getResources().getAssets().open("countries.txt")));
          for (;;)
          {
            localObject2 = paramContext.readLine();
            if (localObject2 == null) {
              break;
            }
            localObject2 = ((String)localObject2).split(";");
            this.countriesArray.add(0, localObject2[2]);
            this.countriesMap.put(localObject2[2], localObject2[0]);
            this.codesMap.put(localObject2[0], localObject2[2]);
            if (localObject2.length > 3) {
              this.phoneFormatMap.put(localObject2[0], localObject2[3]);
            }
            ((HashMap)localObject1).put(localObject2[1], localObject2[2]);
          }
          Collections.sort(this.countriesArray, new Comparator()
          {
            public int compare(String paramAnonymousString1, String paramAnonymousString2)
            {
              return paramAnonymousString1.compareTo(paramAnonymousString2);
            }
          });
        }
        catch (Exception paramContext)
        {
          FileLog.e("tmessages", paramContext);
        }
      }
      for (;;)
      {
        paramContext = null;
        try
        {
          localObject2 = (TelephonyManager)ApplicationLoader.applicationContext.getSystemService("phone");
          this$1 = paramContext;
          if (localObject2 != null) {
            this$1 = ((TelephonyManager)localObject2).getSimCountryIso().toUpperCase();
          }
        }
        catch (Exception this$1)
        {
          for (;;)
          {
            FileLog.e("tmessages", ChangePhoneActivity.this);
            this$1 = paramContext;
          }
          AndroidUtilities.showKeyboard(this.codeField);
          this.codeField.requestFocus();
        }
        if (ChangePhoneActivity.this != null)
        {
          this$1 = (String)((HashMap)localObject1).get(ChangePhoneActivity.this);
          if ((ChangePhoneActivity.this != null) && (this.countriesArray.indexOf(ChangePhoneActivity.this) != -1))
          {
            this.codeField.setText((CharSequence)this.countriesMap.get(ChangePhoneActivity.this));
            this.countryState = 0;
          }
        }
        if (this.codeField.length() == 0)
        {
          this.countryButton.setText(LocaleController.getString("ChooseCountry", 2131165505));
          this.phoneField.setHintText(null);
          this.countryState = 1;
        }
        if (this.codeField.length() == 0) {
          break label1118;
        }
        AndroidUtilities.showKeyboard(this.phoneField);
        this.phoneField.requestFocus();
        this.phoneField.setSelection(this.phoneField.length());
        return;
        i = 3;
        break;
        label1088:
        i = 3;
        break label711;
        label1093:
        i = 3;
        break label734;
        paramContext.close();
      }
      label1118:
    }
    
    public String getHeaderName()
    {
      return LocaleController.getString("ChangePhoneNewNumber", 2131165398);
    }
    
    public void onItemSelected(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong)
    {
      if (this.ignoreSelection)
      {
        this.ignoreSelection = false;
        return;
      }
      this.ignoreOnTextChange = true;
      paramAdapterView = (String)this.countriesArray.get(paramInt);
      this.codeField.setText((CharSequence)this.countriesMap.get(paramAdapterView));
      this.ignoreOnTextChange = false;
    }
    
    public void onNextPressed()
    {
      if ((ChangePhoneActivity.this.getParentActivity() == null) || (this.nextPressed)) {
        return;
      }
      final Object localObject2 = (TelephonyManager)ApplicationLoader.applicationContext.getSystemService("phone");
      int j;
      int i;
      int k;
      label87:
      int m;
      if ((((TelephonyManager)localObject2).getSimState() != 1) && (((TelephonyManager)localObject2).getPhoneType() != 0))
      {
        j = 1;
        i = 1;
        k = i;
        if (Build.VERSION.SDK_INT < 23) {
          break label453;
        }
        k = i;
        if (j == 0) {
          break label453;
        }
        if (ChangePhoneActivity.this.getParentActivity().checkSelfPermission("android.permission.READ_PHONE_STATE") != 0) {
          break label362;
        }
        i = 1;
        if (ChangePhoneActivity.this.getParentActivity().checkSelfPermission("android.permission.RECEIVE_SMS") != 0) {
          break label367;
        }
        m = 1;
        label106:
        k = i;
        if (!ChangePhoneActivity.this.checkPermissions) {
          break label453;
        }
        ChangePhoneActivity.this.permissionsItems.clear();
        if (i == 0) {
          ChangePhoneActivity.this.permissionsItems.add("android.permission.READ_PHONE_STATE");
        }
        if (m == 0) {
          ChangePhoneActivity.this.permissionsItems.add("android.permission.RECEIVE_SMS");
        }
        k = i;
        if (ChangePhoneActivity.this.permissionsItems.isEmpty()) {
          break label453;
        }
        localObject1 = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
        if ((!((SharedPreferences)localObject1).getBoolean("firstlogin", true)) && (!ChangePhoneActivity.this.getParentActivity().shouldShowRequestPermissionRationale("android.permission.READ_PHONE_STATE")) && (!ChangePhoneActivity.this.getParentActivity().shouldShowRequestPermissionRationale("android.permission.RECEIVE_SMS"))) {
          break label414;
        }
        ((SharedPreferences)localObject1).edit().putBoolean("firstlogin", false).commit();
        localObject1 = new AlertDialog.Builder(ChangePhoneActivity.this.getParentActivity());
        ((AlertDialog.Builder)localObject1).setTitle(LocaleController.getString("AppName", 2131165299));
        ((AlertDialog.Builder)localObject1).setPositiveButton(LocaleController.getString("OK", 2131166044), null);
        if (ChangePhoneActivity.this.permissionsItems.size() != 2) {
          break label373;
        }
        ((AlertDialog.Builder)localObject1).setMessage(LocaleController.getString("AllowReadCallAndSms", 2131165279));
      }
      for (;;)
      {
        ChangePhoneActivity.access$1402(ChangePhoneActivity.this, ChangePhoneActivity.this.showDialog(((AlertDialog.Builder)localObject1).create()));
        return;
        j = 0;
        break;
        label362:
        i = 0;
        break label87;
        label367:
        m = 0;
        break label106;
        label373:
        if (m == 0) {
          ((AlertDialog.Builder)localObject1).setMessage(LocaleController.getString("AllowReadSms", 2131165280));
        } else {
          ((AlertDialog.Builder)localObject1).setMessage(LocaleController.getString("AllowReadCall", 2131165278));
        }
      }
      label414:
      ChangePhoneActivity.this.getParentActivity().requestPermissions((String[])ChangePhoneActivity.this.permissionsItems.toArray(new String[ChangePhoneActivity.this.permissionsItems.size()]), 6);
      return;
      label453:
      if (this.countryState == 1)
      {
        ChangePhoneActivity.this.needShowAlert(LocaleController.getString("ChooseCountry", 2131165505));
        return;
      }
      if ((this.countryState == 2) && (!BuildVars.DEBUG_VERSION))
      {
        ChangePhoneActivity.this.needShowAlert(LocaleController.getString("WrongCountry", 2131166406));
        return;
      }
      if (this.codeField.length() == 0)
      {
        ChangePhoneActivity.this.needShowAlert(LocaleController.getString("InvalidPhoneNumber", 2131165758));
        return;
      }
      Object localObject1 = new TLRPC.TL_account_sendChangePhoneCode();
      String str = PhoneFormat.stripExceptNumbers("" + this.codeField.getText() + this.phoneField.getText());
      ((TLRPC.TL_account_sendChangePhoneCode)localObject1).phone_number = str;
      boolean bool;
      if ((j != 0) && (k != 0)) {
        bool = true;
      }
      for (;;)
      {
        ((TLRPC.TL_account_sendChangePhoneCode)localObject1).allow_flashcall = bool;
        if (((TLRPC.TL_account_sendChangePhoneCode)localObject1).allow_flashcall) {}
        try
        {
          localObject2 = ((TelephonyManager)localObject2).getLine1Number();
          if ((localObject2 != null) && (((String)localObject2).length() != 0))
          {
            if (str.contains((CharSequence)localObject2)) {
              break label897;
            }
            if (((String)localObject2).contains(str))
            {
              break label897;
              ((TLRPC.TL_account_sendChangePhoneCode)localObject1).current_number = bool;
              localObject2 = new Bundle();
              ((Bundle)localObject2).putString("phone", "+" + this.codeField.getText() + this.phoneField.getText());
            }
          }
        }
        catch (Exception localException1)
        {
          for (;;)
          {
            try
            {
              ((Bundle)localObject2).putString("ephone", "+" + PhoneFormat.stripExceptNumbers(this.codeField.getText().toString()) + " " + PhoneFormat.stripExceptNumbers(this.phoneField.getText().toString()));
              ((Bundle)localObject2).putString("phoneFormated", str);
              this.nextPressed = true;
              ChangePhoneActivity.this.needShowProgress();
              ConnectionsManager.getInstance().sendRequest((TLObject)localObject1, new RequestDelegate()
              {
                public void run(final TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
                {
                  AndroidUtilities.runOnUIThread(new Runnable()
                  {
                    public void run()
                    {
                      ChangePhoneActivity.PhoneView.access$1502(ChangePhoneActivity.PhoneView.this, false);
                      if (paramAnonymousTL_error == null) {
                        ChangePhoneActivity.this.fillNextCodeParams(ChangePhoneActivity.PhoneView.7.this.val$params, (TLRPC.TL_auth_sentCode)paramAnonymousTLObject);
                      }
                      for (;;)
                      {
                        ChangePhoneActivity.this.needHideProgress();
                        return;
                        if (paramAnonymousTL_error.text != null) {
                          if (paramAnonymousTL_error.text.contains("PHONE_NUMBER_INVALID")) {
                            ChangePhoneActivity.this.needShowAlert(LocaleController.getString("InvalidPhoneNumber", 2131165758));
                          } else if ((paramAnonymousTL_error.text.contains("PHONE_CODE_EMPTY")) || (paramAnonymousTL_error.text.contains("PHONE_CODE_INVALID"))) {
                            ChangePhoneActivity.this.needShowAlert(LocaleController.getString("InvalidCode", 2131165754));
                          } else if (paramAnonymousTL_error.text.contains("PHONE_CODE_EXPIRED")) {
                            ChangePhoneActivity.this.needShowAlert(LocaleController.getString("CodeExpired", 2131165517));
                          } else if (paramAnonymousTL_error.text.startsWith("FLOOD_WAIT")) {
                            ChangePhoneActivity.this.needShowAlert(LocaleController.getString("FloodWait", 2131165639));
                          } else if (paramAnonymousTL_error.text.startsWith("PHONE_NUMBER_OCCUPIED")) {
                            ChangePhoneActivity.this.needShowAlert(LocaleController.formatString("ChangePhoneNumberOccupied", 2131165399, new Object[] { ChangePhoneActivity.PhoneView.7.this.val$params.getString("phone") }));
                          } else {
                            ChangePhoneActivity.this.needShowAlert(LocaleController.getString("ErrorOccurred", 2131165626));
                          }
                        }
                      }
                    }
                  });
                }
              }, 2);
              return;
              bool = false;
              break;
              bool = false;
              continue;
              localException1 = localException1;
              ((TLRPC.TL_account_sendChangePhoneCode)localObject1).allow_flashcall = false;
              FileLog.e("tmessages", localException1);
            }
            catch (Exception localException2)
            {
              FileLog.e("tmessages", localException2);
              localException1.putString("ephone", "+" + str);
              continue;
            }
            label897:
            bool = true;
          }
        }
      }
    }
    
    public void onNothingSelected(AdapterView<?> paramAdapterView) {}
    
    public void onShow()
    {
      super.onShow();
      if (this.phoneField != null)
      {
        if (this.codeField.length() != 0)
        {
          AndroidUtilities.showKeyboard(this.phoneField);
          this.phoneField.requestFocus();
          this.phoneField.setSelection(this.phoneField.length());
        }
      }
      else {
        return;
      }
      AndroidUtilities.showKeyboard(this.codeField);
      this.codeField.requestFocus();
    }
    
    public void selectCountry(String paramString)
    {
      Object localObject;
      if (this.countriesArray.indexOf(paramString) != -1)
      {
        this.ignoreOnTextChange = true;
        localObject = (String)this.countriesMap.get(paramString);
        this.codeField.setText((CharSequence)localObject);
        this.countryButton.setText(paramString);
        paramString = (String)this.phoneFormatMap.get(localObject);
        localObject = this.phoneField;
        if (paramString == null) {
          break label92;
        }
      }
      label92:
      for (paramString = paramString.replace('X', '–');; paramString = null)
      {
        ((HintEditText)localObject).setHintText(paramString);
        this.countryState = 0;
        this.ignoreOnTextChange = false;
        return;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/ChangePhoneActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */