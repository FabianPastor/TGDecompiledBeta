package org.telegram.ui;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
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
import android.graphics.Typeface;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextUtils.TruncateAt;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewPropertyAnimator;
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
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_deleteAccount;
import org.telegram.tgnet.TLRPC.TL_account_getPassword;
import org.telegram.tgnet.TLRPC.TL_account_password;
import org.telegram.tgnet.TLRPC.TL_auth_authorization;
import org.telegram.tgnet.TLRPC.TL_auth_cancelCode;
import org.telegram.tgnet.TLRPC.TL_auth_checkPassword;
import org.telegram.tgnet.TLRPC.TL_auth_codeTypeCall;
import org.telegram.tgnet.TLRPC.TL_auth_codeTypeFlashCall;
import org.telegram.tgnet.TLRPC.TL_auth_codeTypeSms;
import org.telegram.tgnet.TLRPC.TL_auth_passwordRecovery;
import org.telegram.tgnet.TLRPC.TL_auth_recoverPassword;
import org.telegram.tgnet.TLRPC.TL_auth_requestPasswordRecovery;
import org.telegram.tgnet.TLRPC.TL_auth_resendCode;
import org.telegram.tgnet.TLRPC.TL_auth_sendCode;
import org.telegram.tgnet.TLRPC.TL_auth_sentCode;
import org.telegram.tgnet.TLRPC.TL_auth_sentCodeTypeApp;
import org.telegram.tgnet.TLRPC.TL_auth_sentCodeTypeCall;
import org.telegram.tgnet.TLRPC.TL_auth_sentCodeTypeFlashCall;
import org.telegram.tgnet.TLRPC.TL_auth_sentCodeTypeSms;
import org.telegram.tgnet.TLRPC.TL_auth_signIn;
import org.telegram.tgnet.TLRPC.TL_auth_signUp;
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

public class LoginActivity
  extends BaseFragment
{
  private static final int done_button = 1;
  private boolean checkPermissions = true;
  private int currentViewNum = 0;
  private View doneButton;
  private Dialog permissionsDialog;
  private ArrayList<String> permissionsItems = new ArrayList();
  private ProgressDialog progressDialog;
  private SlideView[] views = new SlideView[9];
  
  private void clearCurrentState()
  {
    SharedPreferences.Editor localEditor = ApplicationLoader.applicationContext.getSharedPreferences("logininfo2", 0).edit();
    localEditor.clear();
    localEditor.commit();
  }
  
  private void fillNextCodeParams(Bundle paramBundle, TLRPC.TL_auth_sentCode paramTL_auth_sentCode)
  {
    paramBundle.putString("phoneHash", paramTL_auth_sentCode.phone_code_hash);
    if ((paramTL_auth_sentCode.next_type instanceof TLRPC.TL_auth_codeTypeCall))
    {
      paramBundle.putInt("nextType", 4);
      if (!(paramTL_auth_sentCode.type instanceof TLRPC.TL_auth_sentCodeTypeApp)) {
        break label111;
      }
      paramBundle.putInt("type", 1);
      paramBundle.putInt("length", paramTL_auth_sentCode.type.length);
      setPage(1, true, paramBundle, false);
    }
    label111:
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
  
  private Bundle loadCurrentState()
  {
    for (;;)
    {
      Bundle localBundle;
      Object localObject3;
      Object localObject4;
      String[] arrayOfString;
      Object localObject2;
      try
      {
        localBundle = new Bundle();
        Iterator localIterator = ApplicationLoader.applicationContext.getSharedPreferences("logininfo2", 0).getAll().entrySet().iterator();
        Object localObject1 = localBundle;
        if (localIterator.hasNext())
        {
          localObject3 = (Map.Entry)localIterator.next();
          localObject1 = (String)((Map.Entry)localObject3).getKey();
          localObject4 = ((Map.Entry)localObject3).getValue();
          arrayOfString = ((String)localObject1).split("_\\|_");
          if (arrayOfString.length != 1) {
            break label148;
          }
          if (!(localObject4 instanceof String)) {
            break label124;
          }
          localBundle.putString((String)localObject1, (String)localObject4);
        }
        if (!(localObject4 instanceof Integer)) {
          continue;
        }
      }
      catch (Exception localException)
      {
        FileLog.e("tmessages", localException);
        localObject2 = null;
        return (Bundle)localObject2;
      }
      label124:
      localBundle.putInt((String)localObject2, ((Integer)localObject4).intValue());
      continue;
      label148:
      if (arrayOfString.length == 2)
      {
        localObject3 = localBundle.getBundle(arrayOfString[0]);
        localObject2 = localObject3;
        if (localObject3 == null)
        {
          localObject2 = new Bundle();
          localBundle.putBundle(arrayOfString[0], (Bundle)localObject2);
        }
        if ((localObject4 instanceof String)) {
          ((Bundle)localObject2).putString(arrayOfString[1], (String)localObject4);
        } else if ((localObject4 instanceof Integer)) {
          ((Bundle)localObject2).putInt(arrayOfString[1], ((Integer)localObject4).intValue());
        }
      }
    }
  }
  
  private void needFinishActivity()
  {
    clearCurrentState();
    presentFragment(new DialogsActivity(null), true);
    NotificationCenter.getInstance().postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
  }
  
  private void needShowAlert(String paramString1, String paramString2)
  {
    if ((paramString2 == null) || (getParentActivity() == null)) {
      return;
    }
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(getParentActivity());
    localBuilder.setTitle(paramString1);
    localBuilder.setMessage(paramString2);
    localBuilder.setPositiveButton(LocaleController.getString("OK", 2131166044), null);
    showDialog(localBuilder.create());
  }
  
  private void needShowInvalidAlert(final String paramString, final boolean paramBoolean)
  {
    if (getParentActivity() == null) {
      return;
    }
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(getParentActivity());
    localBuilder.setTitle(LocaleController.getString("AppName", 2131165299));
    if (paramBoolean) {
      localBuilder.setMessage(LocaleController.getString("BannedPhoneNumber", 2131165358));
    }
    for (;;)
    {
      localBuilder.setNeutralButton(LocaleController.getString("BotHelp", 2131165367), new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          try
          {
            paramAnonymousDialogInterface = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
            paramAnonymousDialogInterface = String.format(Locale.US, "%s (%d)", new Object[] { paramAnonymousDialogInterface.versionName, Integer.valueOf(paramAnonymousDialogInterface.versionCode) });
            Intent localIntent = new Intent("android.intent.action.SEND");
            localIntent.setType("message/rfc822");
            localIntent.putExtra("android.intent.extra.EMAIL", new String[] { "login@stel.com" });
            if (paramBoolean)
            {
              localIntent.putExtra("android.intent.extra.SUBJECT", "Banned phone number: " + paramString);
              localIntent.putExtra("android.intent.extra.TEXT", "I'm trying to use my mobile phone number: " + paramString + "\nBut Telegram says it's banned. Please help.\n\nApp version: " + paramAnonymousDialogInterface + "\nOS version: SDK " + Build.VERSION.SDK_INT + "\nDevice Name: " + Build.MANUFACTURER + Build.MODEL + "\nLocale: " + Locale.getDefault());
            }
            for (;;)
            {
              LoginActivity.this.getParentActivity().startActivity(Intent.createChooser(localIntent, "Send email..."));
              return;
              localIntent.putExtra("android.intent.extra.SUBJECT", "Invalid phone number: " + paramString);
              localIntent.putExtra("android.intent.extra.TEXT", "I'm trying to use my mobile phone number: " + paramString + "\nBut Telegram says it's invalid. Please help.\n\nApp version: " + paramAnonymousDialogInterface + "\nOS version: SDK " + Build.VERSION.SDK_INT + "\nDevice Name: " + Build.MANUFACTURER + Build.MODEL + "\nLocale: " + Locale.getDefault());
            }
            return;
          }
          catch (Exception paramAnonymousDialogInterface)
          {
            LoginActivity.this.needShowAlert(LocaleController.getString("AppName", 2131165299), LocaleController.getString("NoMailInstalled", 2131165935));
          }
        }
      });
      localBuilder.setPositiveButton(LocaleController.getString("OK", 2131166044), null);
      showDialog(localBuilder.create());
      return;
      localBuilder.setMessage(LocaleController.getString("InvalidPhoneNumber", 2131165758));
    }
  }
  
  private void needShowProgress()
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
  
  private void putBundleToEditor(Bundle paramBundle, SharedPreferences.Editor paramEditor, String paramString)
  {
    Iterator localIterator = paramBundle.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      Object localObject = paramBundle.get(str);
      if ((localObject instanceof String))
      {
        if (paramString != null) {
          paramEditor.putString(paramString + "_|_" + str, (String)localObject);
        } else {
          paramEditor.putString(str, (String)localObject);
        }
      }
      else if ((localObject instanceof Integer))
      {
        if (paramString != null) {
          paramEditor.putInt(paramString + "_|_" + str, ((Integer)localObject).intValue());
        } else {
          paramEditor.putInt(str, ((Integer)localObject).intValue());
        }
      }
      else if ((localObject instanceof Bundle)) {
        putBundleToEditor((Bundle)localObject, paramEditor, str);
      }
    }
  }
  
  public View createView(Context paramContext)
  {
    this.actionBar.setTitle(LocaleController.getString("AppName", 2131165299));
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (paramAnonymousInt == 1) {
          LoginActivity.this.views[LoginActivity.this.currentViewNum].onNextPressed();
        }
        while (paramAnonymousInt != -1) {
          return;
        }
        LoginActivity.this.onBackPressed();
      }
    });
    this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, 2130837720, AndroidUtilities.dp(56.0F));
    this.fragmentView = new ScrollView(paramContext);
    ScrollView localScrollView = (ScrollView)this.fragmentView;
    localScrollView.setFillViewport(true);
    Object localObject = new FrameLayout(paramContext);
    localScrollView.addView((View)localObject, LayoutHelper.createScroll(-1, -2, 51));
    this.views[0] = new PhoneView(paramContext);
    this.views[1] = new LoginActivitySmsView(paramContext, 1);
    this.views[2] = new LoginActivitySmsView(paramContext, 2);
    this.views[3] = new LoginActivitySmsView(paramContext, 3);
    this.views[4] = new LoginActivitySmsView(paramContext, 4);
    this.views[5] = new LoginActivityRegisterView(paramContext);
    this.views[6] = new LoginActivityPasswordView(paramContext);
    this.views[7] = new LoginActivityRecoverView(paramContext);
    this.views[8] = new LoginActivityResetWaitView(paramContext);
    int i = 0;
    int j;
    if (i < this.views.length)
    {
      paramContext = this.views[i];
      label278:
      float f1;
      label301:
      float f2;
      if (i == 0)
      {
        j = 0;
        paramContext.setVisibility(j);
        paramContext = this.views[i];
        if (i != 0) {
          break label358;
        }
        f1 = -2.0F;
        if (!AndroidUtilities.isTablet()) {
          break label365;
        }
        f2 = 26.0F;
        label311:
        if (!AndroidUtilities.isTablet()) {
          break label372;
        }
      }
      label358:
      label365:
      label372:
      for (float f3 = 26.0F;; f3 = 18.0F)
      {
        ((FrameLayout)localObject).addView(paramContext, LayoutHelper.createFrame(-1, f1, 51, f2, 30.0F, f3, 0.0F));
        i += 1;
        break;
        j = 8;
        break label278;
        f1 = -1.0F;
        break label301;
        f2 = 18.0F;
        break label311;
      }
    }
    localObject = loadCurrentState();
    paramContext = (Context)localObject;
    if (localObject != null)
    {
      this.currentViewNum = ((Bundle)localObject).getInt("currentViewNum", 0);
      paramContext = (Context)localObject;
      if (this.currentViewNum >= 1)
      {
        paramContext = (Context)localObject;
        if (this.currentViewNum <= 4)
        {
          i = ((Bundle)localObject).getInt("open");
          paramContext = (Context)localObject;
          if (i != 0)
          {
            paramContext = (Context)localObject;
            if (Math.abs(System.currentTimeMillis() / 1000L - i) >= 86400L)
            {
              this.currentViewNum = 0;
              paramContext = null;
              clearCurrentState();
            }
          }
        }
      }
    }
    this.actionBar.setTitle(this.views[this.currentViewNum].getHeaderName());
    i = 0;
    if (i < this.views.length)
    {
      if (paramContext != null)
      {
        if ((i < 1) || (i > 4)) {
          break label642;
        }
        if (i == this.currentViewNum) {
          this.views[i].restoreStateParams(paramContext);
        }
      }
      label550:
      if (this.currentViewNum == i)
      {
        localObject = this.actionBar;
        if (this.views[i].needBackButton())
        {
          j = 2130837700;
          label583:
          ((ActionBar)localObject).setBackButtonImage(j);
          this.views[i].setVisibility(0);
          this.views[i].onShow();
          if ((i == 3) || (i == 8)) {
            this.doneButton.setVisibility(8);
          }
        }
      }
      for (;;)
      {
        i += 1;
        break;
        label642:
        this.views[i].restoreStateParams(paramContext);
        break label550;
        j = 0;
        break label583;
        this.views[i].setVisibility(8);
      }
    }
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
  
  public boolean onBackPressed()
  {
    if (this.currentViewNum == 0)
    {
      int i = 0;
      while (i < this.views.length)
      {
        if (this.views[i] != null) {
          this.views[i].onDestroyActivity();
        }
        i += 1;
      }
      clearCurrentState();
      return true;
    }
    if (this.currentViewNum == 6)
    {
      this.views[this.currentViewNum].onBackPressed();
      setPage(0, true, null, true);
    }
    for (;;)
    {
      return false;
      if ((this.currentViewNum == 7) || (this.currentViewNum == 8))
      {
        this.views[this.currentViewNum].onBackPressed();
        setPage(6, true, null, true);
      }
    }
  }
  
  protected void onDialogDismiss(Dialog paramDialog)
  {
    if ((Build.VERSION.SDK_INT >= 23) && (paramDialog == this.permissionsDialog) && (!this.permissionsItems.isEmpty()) && (getParentActivity() != null)) {
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
  
  public void onPause()
  {
    super.onPause();
    AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
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
    try
    {
      if ((this.currentViewNum >= 1) && (this.currentViewNum <= 4) && ((this.views[this.currentViewNum] instanceof LoginActivitySmsView)))
      {
        int i = ((LoginActivitySmsView)this.views[this.currentViewNum]).openTime;
        if ((i != 0) && (Math.abs(System.currentTimeMillis() / 1000L - i) >= 86400L))
        {
          this.views[this.currentViewNum].onBackPressed();
          setPage(0, false, null, true);
        }
      }
      return;
    }
    catch (Exception localException)
    {
      FileLog.e("tmessages", localException);
    }
  }
  
  public void saveSelfArgs(Bundle paramBundle)
  {
    for (;;)
    {
      int i;
      try
      {
        paramBundle = new Bundle();
        paramBundle.putInt("currentViewNum", this.currentViewNum);
        i = 0;
        Object localObject;
        if (i <= this.currentViewNum)
        {
          localObject = this.views[i];
          if (localObject != null) {
            ((SlideView)localObject).saveStateParams(paramBundle);
          }
        }
        else
        {
          localObject = ApplicationLoader.applicationContext.getSharedPreferences("logininfo2", 0).edit();
          ((SharedPreferences.Editor)localObject).clear();
          putBundleToEditor(paramBundle, (SharedPreferences.Editor)localObject, null);
          ((SharedPreferences.Editor)localObject).commit();
          return;
        }
      }
      catch (Exception paramBundle)
      {
        FileLog.e("tmessages", paramBundle);
        return;
      }
      i += 1;
    }
  }
  
  public void setPage(int paramInt, boolean paramBoolean1, Bundle paramBundle, boolean paramBoolean2)
  {
    int i = 2130837700;
    if ((paramInt == 3) || (paramInt == 8))
    {
      this.doneButton.setVisibility(8);
      if (!paramBoolean1) {
        break label270;
      }
      final SlideView localSlideView = this.views[this.currentViewNum];
      localObject = this.views[paramInt];
      this.currentViewNum = paramInt;
      ActionBar localActionBar = this.actionBar;
      if (!((SlideView)localObject).needBackButton()) {
        break label239;
      }
      label67:
      localActionBar.setBackButtonImage(i);
      ((SlideView)localObject).setParams(paramBundle);
      this.actionBar.setTitle(((SlideView)localObject).getHeaderName());
      ((SlideView)localObject).onShow();
      if (!paramBoolean2) {
        break label245;
      }
      f = -AndroidUtilities.displaySize.x;
      label112:
      ((SlideView)localObject).setX(f);
      paramBundle = localSlideView.animate().setInterpolator(new AccelerateDecelerateInterpolator()).setListener(new Animator.AnimatorListener()
      {
        public void onAnimationCancel(Animator paramAnonymousAnimator) {}
        
        @SuppressLint({"NewApi"})
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          localSlideView.setVisibility(8);
          localSlideView.setX(0.0F);
        }
        
        public void onAnimationRepeat(Animator paramAnonymousAnimator) {}
        
        public void onAnimationStart(Animator paramAnonymousAnimator) {}
      }).setDuration(300L);
      if (!paramBoolean2) {
        break label257;
      }
    }
    label239:
    label245:
    label257:
    for (float f = AndroidUtilities.displaySize.x;; f = -AndroidUtilities.displaySize.x)
    {
      paramBundle.translationX(f).start();
      ((SlideView)localObject).animate().setInterpolator(new AccelerateDecelerateInterpolator()).setListener(new Animator.AnimatorListener()
      {
        public void onAnimationCancel(Animator paramAnonymousAnimator) {}
        
        public void onAnimationEnd(Animator paramAnonymousAnimator) {}
        
        public void onAnimationRepeat(Animator paramAnonymousAnimator) {}
        
        public void onAnimationStart(Animator paramAnonymousAnimator)
        {
          localObject.setVisibility(0);
        }
      }).setDuration(300L).translationX(0.0F).start();
      return;
      if (paramInt == 0) {
        this.checkPermissions = true;
      }
      this.doneButton.setVisibility(0);
      break;
      i = 0;
      break label67;
      f = AndroidUtilities.displaySize.x;
      break label112;
    }
    label270:
    final Object localObject = this.actionBar;
    if (this.views[paramInt].needBackButton()) {}
    for (;;)
    {
      ((ActionBar)localObject).setBackButtonImage(i);
      this.views[this.currentViewNum].setVisibility(8);
      this.currentViewNum = paramInt;
      this.views[paramInt].setParams(paramBundle);
      this.views[paramInt].setVisibility(0);
      this.actionBar.setTitle(this.views[paramInt].getHeaderName());
      this.views[paramInt].onShow();
      return;
      i = 0;
    }
  }
  
  public class LoginActivityPasswordView
    extends SlideView
  {
    private EditText codeField;
    private TextView confirmTextView;
    private Bundle currentParams;
    private byte[] current_salt;
    private String email_unconfirmed_pattern;
    private boolean has_recovery;
    private String hint;
    private boolean nextPressed;
    private String phoneCode;
    private String phoneHash;
    private String requestPhone;
    private TextView resetAccountButton;
    private TextView resetAccountText;
    
    public LoginActivityPasswordView(Context paramContext)
    {
      super();
      setOrientation(1);
      this.confirmTextView = new TextView(paramContext);
      this.confirmTextView.setTextColor(-9079435);
      this.confirmTextView.setTextSize(1, 14.0F);
      Object localObject = this.confirmTextView;
      if (LocaleController.isRTL)
      {
        i = 5;
        ((TextView)localObject).setGravity(i);
        this.confirmTextView.setLineSpacing(AndroidUtilities.dp(2.0F), 1.0F);
        this.confirmTextView.setText(LocaleController.getString("LoginPasswordText", 2131165846));
        localObject = this.confirmTextView;
        if (!LocaleController.isRTL) {
          break label711;
        }
        i = 5;
        label107:
        addView((View)localObject, LayoutHelper.createLinear(-2, -2, i));
        this.codeField = new EditText(paramContext);
        this.codeField.setTextColor(-14606047);
        AndroidUtilities.clearCursorDrawable(this.codeField);
        this.codeField.setHintTextColor(-6842473);
        this.codeField.setHint(LocaleController.getString("LoginPassword", 2131165845));
        this.codeField.setImeOptions(268435461);
        this.codeField.setTextSize(1, 18.0F);
        this.codeField.setMaxLines(1);
        this.codeField.setPadding(0, 0, 0, 0);
        this.codeField.setInputType(129);
        this.codeField.setTransformationMethod(PasswordTransformationMethod.getInstance());
        this.codeField.setTypeface(Typeface.DEFAULT);
        localObject = this.codeField;
        if (!LocaleController.isRTL) {
          break label716;
        }
        i = 5;
        label254:
        ((EditText)localObject).setGravity(i);
        addView(this.codeField, LayoutHelper.createLinear(-1, 36, 1, 0, 20, 0, 0));
        this.codeField.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
          public boolean onEditorAction(TextView paramAnonymousTextView, int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
          {
            if (paramAnonymousInt == 5)
            {
              LoginActivity.LoginActivityPasswordView.this.onNextPressed();
              return true;
            }
            return false;
          }
        });
        localObject = new TextView(paramContext);
        if (!LocaleController.isRTL) {
          break label721;
        }
        i = 5;
        label314:
        ((TextView)localObject).setGravity(i | 0x30);
        ((TextView)localObject).setTextColor(-11697229);
        ((TextView)localObject).setText(LocaleController.getString("ForgotPassword", 2131165642));
        ((TextView)localObject).setTextSize(1, 14.0F);
        ((TextView)localObject).setLineSpacing(AndroidUtilities.dp(2.0F), 1.0F);
        ((TextView)localObject).setPadding(0, AndroidUtilities.dp(14.0F), 0, 0);
        if (!LocaleController.isRTL) {
          break label726;
        }
        i = 5;
        label382:
        addView((View)localObject, LayoutHelper.createLinear(-2, -2, i | 0x30));
        ((TextView)localObject).setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            if (LoginActivity.LoginActivityPasswordView.this.has_recovery)
            {
              LoginActivity.this.needShowProgress();
              paramAnonymousView = new TLRPC.TL_auth_requestPasswordRecovery();
              ConnectionsManager.getInstance().sendRequest(paramAnonymousView, new RequestDelegate()
              {
                public void run(final TLObject paramAnonymous2TLObject, final TLRPC.TL_error paramAnonymous2TL_error)
                {
                  AndroidUtilities.runOnUIThread(new Runnable()
                  {
                    public void run()
                    {
                      LoginActivity.this.needHideProgress();
                      final Object localObject;
                      if (paramAnonymous2TL_error == null)
                      {
                        localObject = (TLRPC.TL_auth_passwordRecovery)paramAnonymous2TLObject;
                        AlertDialog.Builder localBuilder = new AlertDialog.Builder(LoginActivity.this.getParentActivity());
                        localBuilder.setMessage(LocaleController.formatString("RestoreEmailSent", 2131166183, new Object[] { ((TLRPC.TL_auth_passwordRecovery)localObject).email_pattern }));
                        localBuilder.setTitle(LocaleController.getString("AppName", 2131165299));
                        localBuilder.setPositiveButton(LocaleController.getString("OK", 2131166044), new DialogInterface.OnClickListener()
                        {
                          public void onClick(DialogInterface paramAnonymous4DialogInterface, int paramAnonymous4Int)
                          {
                            paramAnonymous4DialogInterface = new Bundle();
                            paramAnonymous4DialogInterface.putString("email_unconfirmed_pattern", localObject.email_pattern);
                            LoginActivity.this.setPage(7, true, paramAnonymous4DialogInterface, false);
                          }
                        });
                        localObject = LoginActivity.this.showDialog(localBuilder.create());
                        if (localObject != null)
                        {
                          ((Dialog)localObject).setCanceledOnTouchOutside(false);
                          ((Dialog)localObject).setCancelable(false);
                        }
                        return;
                      }
                      if (paramAnonymous2TL_error.text.startsWith("FLOOD_WAIT"))
                      {
                        int i = Utilities.parseInt(paramAnonymous2TL_error.text).intValue();
                        if (i < 60) {}
                        for (localObject = LocaleController.formatPluralString("Seconds", i);; localObject = LocaleController.formatPluralString("Minutes", i / 60))
                        {
                          LoginActivity.this.needShowAlert(LocaleController.getString("AppName", 2131165299), LocaleController.formatString("FloodWaitTime", 2131165640, new Object[] { localObject }));
                          return;
                        }
                      }
                      LoginActivity.this.needShowAlert(LocaleController.getString("AppName", 2131165299), paramAnonymous2TL_error.text);
                    }
                  });
                }
              }, 10);
              return;
            }
            LoginActivity.LoginActivityPasswordView.this.resetAccountText.setVisibility(0);
            LoginActivity.LoginActivityPasswordView.this.resetAccountButton.setVisibility(0);
            AndroidUtilities.hideKeyboard(LoginActivity.LoginActivityPasswordView.this.codeField);
            LoginActivity.this.needShowAlert(LocaleController.getString("RestorePasswordNoEmailTitle", 2131166188), LocaleController.getString("RestorePasswordNoEmailText", 2131166187));
          }
        });
        this.resetAccountButton = new TextView(paramContext);
        localObject = this.resetAccountButton;
        if (!LocaleController.isRTL) {
          break label731;
        }
        i = 5;
        label439:
        ((TextView)localObject).setGravity(i | 0x30);
        this.resetAccountButton.setTextColor(-39322);
        this.resetAccountButton.setVisibility(8);
        this.resetAccountButton.setText(LocaleController.getString("ResetMyAccount", 2131166177));
        this.resetAccountButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.resetAccountButton.setTextSize(1, 14.0F);
        this.resetAccountButton.setLineSpacing(AndroidUtilities.dp(2.0F), 1.0F);
        this.resetAccountButton.setPadding(0, AndroidUtilities.dp(14.0F), 0, 0);
        localObject = this.resetAccountButton;
        if (!LocaleController.isRTL) {
          break label736;
        }
        i = 5;
        label544:
        addView((View)localObject, LayoutHelper.createLinear(-2, -2, i | 0x30, 0, 34, 0, 0));
        this.resetAccountButton.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            paramAnonymousView = new AlertDialog.Builder(LoginActivity.this.getParentActivity());
            paramAnonymousView.setMessage(LocaleController.getString("ResetMyAccountWarningText", 2131166181));
            paramAnonymousView.setTitle(LocaleController.getString("ResetMyAccountWarning", 2131166179));
            paramAnonymousView.setPositiveButton(LocaleController.getString("ResetMyAccountWarningReset", 2131166180), new DialogInterface.OnClickListener()
            {
              public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
              {
                LoginActivity.this.needShowProgress();
                paramAnonymous2DialogInterface = new TLRPC.TL_account_deleteAccount();
                paramAnonymous2DialogInterface.reason = "Forgot password";
                ConnectionsManager.getInstance().sendRequest(paramAnonymous2DialogInterface, new RequestDelegate()
                {
                  public void run(TLObject paramAnonymous3TLObject, final TLRPC.TL_error paramAnonymous3TL_error)
                  {
                    AndroidUtilities.runOnUIThread(new Runnable()
                    {
                      public void run()
                      {
                        LoginActivity.this.needHideProgress();
                        Bundle localBundle;
                        if (paramAnonymous3TL_error == null)
                        {
                          localBundle = new Bundle();
                          localBundle.putString("phoneFormated", LoginActivity.LoginActivityPasswordView.this.requestPhone);
                          localBundle.putString("phoneHash", LoginActivity.LoginActivityPasswordView.this.phoneHash);
                          localBundle.putString("code", LoginActivity.LoginActivityPasswordView.this.phoneCode);
                          LoginActivity.this.setPage(5, true, localBundle, false);
                          return;
                        }
                        if (paramAnonymous3TL_error.text.equals("2FA_RECENT_CONFIRM"))
                        {
                          LoginActivity.this.needShowAlert(LocaleController.getString("AppName", 2131165299), LocaleController.getString("ResetAccountCancelledAlert", 2131166173));
                          return;
                        }
                        if (paramAnonymous3TL_error.text.startsWith("2FA_CONFIRM_WAIT_"))
                        {
                          localBundle = new Bundle();
                          localBundle.putString("phoneFormated", LoginActivity.LoginActivityPasswordView.this.requestPhone);
                          localBundle.putString("phoneHash", LoginActivity.LoginActivityPasswordView.this.phoneHash);
                          localBundle.putString("code", LoginActivity.LoginActivityPasswordView.this.phoneCode);
                          localBundle.putInt("startTime", ConnectionsManager.getInstance().getCurrentTime());
                          localBundle.putInt("waitTime", Utilities.parseInt(paramAnonymous3TL_error.text.replace("2FA_CONFIRM_WAIT_", "")).intValue());
                          LoginActivity.this.setPage(8, true, localBundle, false);
                          return;
                        }
                        LoginActivity.this.needShowAlert(LocaleController.getString("AppName", 2131165299), paramAnonymous3TL_error.text);
                      }
                    });
                  }
                }, 10);
              }
            });
            paramAnonymousView.setNegativeButton(LocaleController.getString("Cancel", 2131165386), null);
            LoginActivity.this.showDialog(paramAnonymousView.create());
          }
        });
        this.resetAccountText = new TextView(paramContext);
        this$1 = this.resetAccountText;
        if (!LocaleController.isRTL) {
          break label741;
        }
        i = 5;
        label607:
        LoginActivity.this.setGravity(i | 0x30);
        this.resetAccountText.setVisibility(8);
        this.resetAccountText.setTextColor(-9079435);
        this.resetAccountText.setText(LocaleController.getString("ResetMyAccountText", 2131166178));
        this.resetAccountText.setTextSize(1, 14.0F);
        this.resetAccountText.setLineSpacing(AndroidUtilities.dp(2.0F), 1.0F);
        this$1 = this.resetAccountText;
        if (!LocaleController.isRTL) {
          break label746;
        }
      }
      label711:
      label716:
      label721:
      label726:
      label731:
      label736:
      label741:
      label746:
      for (int i = 5;; i = 3)
      {
        addView(LoginActivity.this, LayoutHelper.createLinear(-2, -2, i | 0x30, 0, 7, 0, 14));
        return;
        i = 3;
        break;
        i = 3;
        break label107;
        i = 3;
        break label254;
        i = 3;
        break label314;
        i = 3;
        break label382;
        i = 3;
        break label439;
        i = 3;
        break label544;
        i = 3;
        break label607;
      }
    }
    
    private void onPasscodeError(boolean paramBoolean)
    {
      if (LoginActivity.this.getParentActivity() == null) {
        return;
      }
      Vibrator localVibrator = (Vibrator)LoginActivity.this.getParentActivity().getSystemService("vibrator");
      if (localVibrator != null) {
        localVibrator.vibrate(200L);
      }
      if (paramBoolean) {
        this.codeField.setText("");
      }
      AndroidUtilities.shakeView(this.confirmTextView, 2.0F, 0);
    }
    
    public String getHeaderName()
    {
      return LocaleController.getString("LoginPassword", 2131165845);
    }
    
    public boolean needBackButton()
    {
      return true;
    }
    
    public void onBackPressed()
    {
      this.currentParams = null;
    }
    
    public void onNextPressed()
    {
      if (this.nextPressed) {
        return;
      }
      Object localObject2 = this.codeField.getText().toString();
      if (((String)localObject2).length() == 0)
      {
        onPasscodeError(false);
        return;
      }
      this.nextPressed = true;
      Object localObject1 = null;
      try
      {
        localObject2 = ((String)localObject2).getBytes("UTF-8");
        localObject1 = localObject2;
      }
      catch (Exception localException)
      {
        for (;;)
        {
          FileLog.e("tmessages", localException);
        }
      }
      LoginActivity.this.needShowProgress();
      localObject2 = new byte[this.current_salt.length * 2 + localObject1.length];
      System.arraycopy(this.current_salt, 0, localObject2, 0, this.current_salt.length);
      System.arraycopy(localObject1, 0, localObject2, this.current_salt.length, localObject1.length);
      System.arraycopy(this.current_salt, 0, localObject2, localObject2.length - this.current_salt.length, this.current_salt.length);
      localObject1 = new TLRPC.TL_auth_checkPassword();
      ((TLRPC.TL_auth_checkPassword)localObject1).password_hash = Utilities.computeSHA256((byte[])localObject2, 0, localObject2.length);
      ConnectionsManager.getInstance().sendRequest((TLObject)localObject1, new RequestDelegate()
      {
        public void run(final TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
        {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              LoginActivity.this.needHideProgress();
              LoginActivity.LoginActivityPasswordView.access$5402(LoginActivity.LoginActivityPasswordView.this, false);
              Object localObject;
              if (paramAnonymousTL_error == null)
              {
                localObject = (TLRPC.TL_auth_authorization)paramAnonymousTLObject;
                ConnectionsManager.getInstance().setUserId(((TLRPC.TL_auth_authorization)localObject).user.id);
                UserConfig.clearConfig();
                MessagesController.getInstance().cleanup();
                UserConfig.setCurrentUser(((TLRPC.TL_auth_authorization)localObject).user);
                UserConfig.saveConfig(true);
                MessagesStorage.getInstance().cleanup(true);
                ArrayList localArrayList = new ArrayList();
                localArrayList.add(((TLRPC.TL_auth_authorization)localObject).user);
                MessagesStorage.getInstance().putUsersAndChats(localArrayList, null, true, true);
                MessagesController.getInstance().putUser(((TLRPC.TL_auth_authorization)localObject).user, false);
                ContactsController.getInstance().checkAppAccount();
                MessagesController.getInstance().getBlockedUsers(true);
                LoginActivity.this.needFinishActivity();
                return;
              }
              if (paramAnonymousTL_error.text.equals("PASSWORD_HASH_INVALID"))
              {
                LoginActivity.LoginActivityPasswordView.this.onPasscodeError(true);
                return;
              }
              if (paramAnonymousTL_error.text.startsWith("FLOOD_WAIT"))
              {
                int i = Utilities.parseInt(paramAnonymousTL_error.text).intValue();
                if (i < 60) {}
                for (localObject = LocaleController.formatPluralString("Seconds", i);; localObject = LocaleController.formatPluralString("Minutes", i / 60))
                {
                  LoginActivity.this.needShowAlert(LocaleController.getString("AppName", 2131165299), LocaleController.formatString("FloodWaitTime", 2131165640, new Object[] { localObject }));
                  return;
                }
              }
              LoginActivity.this.needShowAlert(LocaleController.getString("AppName", 2131165299), paramAnonymousTL_error.text);
            }
          });
        }
      }, 10);
    }
    
    public void onShow()
    {
      super.onShow();
      if (this.codeField != null)
      {
        this.codeField.requestFocus();
        this.codeField.setSelection(this.codeField.length());
        AndroidUtilities.showKeyboard(this.codeField);
      }
    }
    
    public void restoreStateParams(Bundle paramBundle)
    {
      this.currentParams = paramBundle.getBundle("passview_params");
      if (this.currentParams != null) {
        setParams(this.currentParams);
      }
      paramBundle = paramBundle.getString("passview_code");
      if (paramBundle != null) {
        this.codeField.setText(paramBundle);
      }
    }
    
    public void saveStateParams(Bundle paramBundle)
    {
      String str = this.codeField.getText().toString();
      if (str.length() != 0) {
        paramBundle.putString("passview_code", str);
      }
      if (this.currentParams != null) {
        paramBundle.putBundle("passview_params", this.currentParams);
      }
    }
    
    public void setParams(Bundle paramBundle)
    {
      boolean bool = true;
      if (paramBundle == null) {
        return;
      }
      if (paramBundle.isEmpty())
      {
        this.resetAccountButton.setVisibility(0);
        this.resetAccountText.setVisibility(0);
        AndroidUtilities.hideKeyboard(this.codeField);
        return;
      }
      this.resetAccountButton.setVisibility(8);
      this.resetAccountText.setVisibility(8);
      this.codeField.setText("");
      this.currentParams = paramBundle;
      this.current_salt = Utilities.hexToBytes(this.currentParams.getString("current_salt"));
      this.hint = this.currentParams.getString("hint");
      if (this.currentParams.getInt("has_recovery") == 1) {}
      for (;;)
      {
        this.has_recovery = bool;
        this.email_unconfirmed_pattern = this.currentParams.getString("email_unconfirmed_pattern");
        this.requestPhone = paramBundle.getString("phoneFormated");
        this.phoneHash = paramBundle.getString("phoneHash");
        this.phoneCode = paramBundle.getString("code");
        if ((this.hint == null) || (this.hint.length() <= 0)) {
          break;
        }
        this.codeField.setHint(this.hint);
        return;
        bool = false;
      }
      this.codeField.setHint(LocaleController.getString("LoginPassword", 2131165845));
    }
  }
  
  public class LoginActivityRecoverView
    extends SlideView
  {
    private TextView cancelButton;
    private EditText codeField;
    private TextView confirmTextView;
    private Bundle currentParams;
    private String email_unconfirmed_pattern;
    private boolean nextPressed;
    
    public LoginActivityRecoverView(Context paramContext)
    {
      super();
      setOrientation(1);
      this.confirmTextView = new TextView(paramContext);
      this.confirmTextView.setTextColor(-9079435);
      this.confirmTextView.setTextSize(1, 14.0F);
      Object localObject = this.confirmTextView;
      if (LocaleController.isRTL)
      {
        i = 5;
        ((TextView)localObject).setGravity(i);
        this.confirmTextView.setLineSpacing(AndroidUtilities.dp(2.0F), 1.0F);
        this.confirmTextView.setText(LocaleController.getString("RestoreEmailSentInfo", 2131166184));
        localObject = this.confirmTextView;
        if (!LocaleController.isRTL) {
          break label434;
        }
        i = 5;
        label110:
        addView((View)localObject, LayoutHelper.createLinear(-2, -2, i));
        this.codeField = new EditText(paramContext);
        this.codeField.setTextColor(-14606047);
        AndroidUtilities.clearCursorDrawable(this.codeField);
        this.codeField.setHintTextColor(-6842473);
        this.codeField.setHint(LocaleController.getString("PasswordCode", 2131166088));
        this.codeField.setImeOptions(268435461);
        this.codeField.setTextSize(1, 18.0F);
        this.codeField.setMaxLines(1);
        this.codeField.setPadding(0, 0, 0, 0);
        this.codeField.setInputType(3);
        this.codeField.setTransformationMethod(PasswordTransformationMethod.getInstance());
        this.codeField.setTypeface(Typeface.DEFAULT);
        localObject = this.codeField;
        if (!LocaleController.isRTL) {
          break label439;
        }
        i = 5;
        label255:
        ((EditText)localObject).setGravity(i);
        addView(this.codeField, LayoutHelper.createLinear(-1, 36, 1, 0, 20, 0, 0));
        this.codeField.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
          public boolean onEditorAction(TextView paramAnonymousTextView, int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
          {
            if (paramAnonymousInt == 5)
            {
              LoginActivity.LoginActivityRecoverView.this.onNextPressed();
              return true;
            }
            return false;
          }
        });
        this.cancelButton = new TextView(paramContext);
        paramContext = this.cancelButton;
        if (!LocaleController.isRTL) {
          break label444;
        }
        i = 5;
        label322:
        paramContext.setGravity(i | 0x50);
        this.cancelButton.setTextColor(-11697229);
        this.cancelButton.setTextSize(1, 14.0F);
        this.cancelButton.setLineSpacing(AndroidUtilities.dp(2.0F), 1.0F);
        this.cancelButton.setPadding(0, AndroidUtilities.dp(14.0F), 0, 0);
        paramContext = this.cancelButton;
        if (!LocaleController.isRTL) {
          break label449;
        }
      }
      label434:
      label439:
      label444:
      label449:
      for (int i = j;; i = 3)
      {
        addView(paramContext, LayoutHelper.createLinear(-2, -2, i | 0x50, 0, 0, 0, 14));
        this.cancelButton.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            paramAnonymousView = new AlertDialog.Builder(LoginActivity.this.getParentActivity());
            paramAnonymousView.setMessage(LocaleController.getString("RestoreEmailTroubleText", 2131166186));
            paramAnonymousView.setTitle(LocaleController.getString("RestorePasswordNoEmailTitle", 2131166188));
            paramAnonymousView.setPositiveButton(LocaleController.getString("OK", 2131166044), new DialogInterface.OnClickListener()
            {
              public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
              {
                LoginActivity.this.setPage(6, true, new Bundle(), true);
              }
            });
            paramAnonymousView = LoginActivity.this.showDialog(paramAnonymousView.create());
            if (paramAnonymousView != null)
            {
              paramAnonymousView.setCanceledOnTouchOutside(false);
              paramAnonymousView.setCancelable(false);
            }
          }
        });
        return;
        i = 3;
        break;
        i = 3;
        break label110;
        i = 3;
        break label255;
        i = 3;
        break label322;
      }
    }
    
    private void onPasscodeError(boolean paramBoolean)
    {
      if (LoginActivity.this.getParentActivity() == null) {
        return;
      }
      Vibrator localVibrator = (Vibrator)LoginActivity.this.getParentActivity().getSystemService("vibrator");
      if (localVibrator != null) {
        localVibrator.vibrate(200L);
      }
      if (paramBoolean) {
        this.codeField.setText("");
      }
      AndroidUtilities.shakeView(this.confirmTextView, 2.0F, 0);
    }
    
    public String getHeaderName()
    {
      return LocaleController.getString("LoginPassword", 2131165845);
    }
    
    public boolean needBackButton()
    {
      return true;
    }
    
    public void onBackPressed()
    {
      this.currentParams = null;
    }
    
    public void onNextPressed()
    {
      if (this.nextPressed) {
        return;
      }
      if (this.codeField.getText().toString().length() == 0)
      {
        onPasscodeError(false);
        return;
      }
      this.nextPressed = true;
      String str = this.codeField.getText().toString();
      if (str.length() == 0)
      {
        onPasscodeError(false);
        return;
      }
      LoginActivity.this.needShowProgress();
      TLRPC.TL_auth_recoverPassword localTL_auth_recoverPassword = new TLRPC.TL_auth_recoverPassword();
      localTL_auth_recoverPassword.code = str;
      ConnectionsManager.getInstance().sendRequest(localTL_auth_recoverPassword, new RequestDelegate()
      {
        public void run(final TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
        {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              LoginActivity.this.needHideProgress();
              LoginActivity.LoginActivityRecoverView.access$6302(LoginActivity.LoginActivityRecoverView.this, false);
              Object localObject;
              if (paramAnonymousTL_error == null)
              {
                localObject = (TLRPC.TL_auth_authorization)paramAnonymousTLObject;
                ConnectionsManager.getInstance().setUserId(((TLRPC.TL_auth_authorization)localObject).user.id);
                UserConfig.clearConfig();
                MessagesController.getInstance().cleanup();
                UserConfig.setCurrentUser(((TLRPC.TL_auth_authorization)localObject).user);
                UserConfig.saveConfig(true);
                MessagesStorage.getInstance().cleanup(true);
                ArrayList localArrayList = new ArrayList();
                localArrayList.add(((TLRPC.TL_auth_authorization)localObject).user);
                MessagesStorage.getInstance().putUsersAndChats(localArrayList, null, true, true);
                MessagesController.getInstance().putUser(((TLRPC.TL_auth_authorization)localObject).user, false);
                ContactsController.getInstance().checkAppAccount();
                MessagesController.getInstance().getBlockedUsers(true);
                LoginActivity.this.needFinishActivity();
                return;
              }
              if (paramAnonymousTL_error.text.startsWith("CODE_INVALID"))
              {
                LoginActivity.LoginActivityRecoverView.this.onPasscodeError(true);
                return;
              }
              if (paramAnonymousTL_error.text.startsWith("FLOOD_WAIT"))
              {
                int i = Utilities.parseInt(paramAnonymousTL_error.text).intValue();
                if (i < 60) {}
                for (localObject = LocaleController.formatPluralString("Seconds", i);; localObject = LocaleController.formatPluralString("Minutes", i / 60))
                {
                  LoginActivity.this.needShowAlert(LocaleController.getString("AppName", 2131165299), LocaleController.formatString("FloodWaitTime", 2131165640, new Object[] { localObject }));
                  return;
                }
              }
              LoginActivity.this.needShowAlert(LocaleController.getString("AppName", 2131165299), paramAnonymousTL_error.text);
            }
          });
        }
      }, 10);
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
    
    public void restoreStateParams(Bundle paramBundle)
    {
      this.currentParams = paramBundle.getBundle("recoveryview_params");
      if (this.currentParams != null) {
        setParams(this.currentParams);
      }
      paramBundle = paramBundle.getString("recoveryview_code");
      if (paramBundle != null) {
        this.codeField.setText(paramBundle);
      }
    }
    
    public void saveStateParams(Bundle paramBundle)
    {
      String str = this.codeField.getText().toString();
      if ((str != null) && (str.length() != 0)) {
        paramBundle.putString("recoveryview_code", str);
      }
      if (this.currentParams != null) {
        paramBundle.putBundle("recoveryview_params", this.currentParams);
      }
    }
    
    public void setParams(Bundle paramBundle)
    {
      if (paramBundle == null) {
        return;
      }
      this.codeField.setText("");
      this.currentParams = paramBundle;
      this.email_unconfirmed_pattern = this.currentParams.getString("email_unconfirmed_pattern");
      this.cancelButton.setText(LocaleController.formatString("RestoreEmailTrouble", 2131166185, new Object[] { this.email_unconfirmed_pattern }));
      AndroidUtilities.showKeyboard(this.codeField);
      this.codeField.requestFocus();
    }
  }
  
  public class LoginActivityRegisterView
    extends SlideView
  {
    private Bundle currentParams;
    private EditText firstNameField;
    private EditText lastNameField;
    private boolean nextPressed = false;
    private String phoneCode;
    private String phoneHash;
    private String requestPhone;
    
    public LoginActivityRegisterView(Context paramContext)
    {
      super();
      setOrientation(1);
      Object localObject = new TextView(paramContext);
      ((TextView)localObject).setText(LocaleController.getString("RegisterText", 2131166151));
      ((TextView)localObject).setTextColor(-9079435);
      if (LocaleController.isRTL)
      {
        i = 5;
        ((TextView)localObject).setGravity(i);
        ((TextView)localObject).setTextSize(1, 14.0F);
        if (!LocaleController.isRTL) {
          break label475;
        }
        i = 5;
        label79:
        addView((View)localObject, LayoutHelper.createLinear(-2, -2, i, 0, 8, 0, 0));
        this.firstNameField = new EditText(paramContext);
        this.firstNameField.setHintTextColor(-6842473);
        this.firstNameField.setTextColor(-14606047);
        AndroidUtilities.clearCursorDrawable(this.firstNameField);
        this.firstNameField.setHint(LocaleController.getString("FirstName", 2131165638));
        this.firstNameField.setImeOptions(268435461);
        this.firstNameField.setTextSize(1, 18.0F);
        this.firstNameField.setMaxLines(1);
        this.firstNameField.setInputType(8192);
        addView(this.firstNameField, LayoutHelper.createLinear(-1, 36, 0.0F, 26.0F, 0.0F, 0.0F));
        this.firstNameField.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
          public boolean onEditorAction(TextView paramAnonymousTextView, int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
          {
            if (paramAnonymousInt == 5)
            {
              LoginActivity.LoginActivityRegisterView.this.lastNameField.requestFocus();
              return true;
            }
            return false;
          }
        });
        this.lastNameField = new EditText(paramContext);
        this.lastNameField.setHint(LocaleController.getString("LastName", 2131165788));
        this.lastNameField.setHintTextColor(-6842473);
        this.lastNameField.setTextColor(-14606047);
        AndroidUtilities.clearCursorDrawable(this.lastNameField);
        this.lastNameField.setImeOptions(268435461);
        this.lastNameField.setTextSize(1, 18.0F);
        this.lastNameField.setMaxLines(1);
        this.lastNameField.setInputType(8192);
        addView(this.lastNameField, LayoutHelper.createLinear(-1, 36, 0.0F, 10.0F, 0.0F, 0.0F));
        localObject = new LinearLayout(paramContext);
        ((LinearLayout)localObject).setGravity(80);
        addView((View)localObject, LayoutHelper.createLinear(-1, -1));
        paramContext = new TextView(paramContext);
        paramContext.setText(LocaleController.getString("CancelRegistration", 2131165391));
        if (!LocaleController.isRTL) {
          break label480;
        }
        i = 5;
        label384:
        paramContext.setGravity(i | 0x1);
        paramContext.setTextColor(-11697229);
        paramContext.setTextSize(1, 14.0F);
        paramContext.setLineSpacing(AndroidUtilities.dp(2.0F), 1.0F);
        paramContext.setPadding(0, AndroidUtilities.dp(24.0F), 0, 0);
        if (!LocaleController.isRTL) {
          break label485;
        }
      }
      label475:
      label480:
      label485:
      for (int i = 5;; i = 3)
      {
        ((LinearLayout)localObject).addView(paramContext, LayoutHelper.createLinear(-2, -2, i | 0x50, 0, 0, 0, 10));
        paramContext.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            paramAnonymousView = new AlertDialog.Builder(LoginActivity.this.getParentActivity());
            paramAnonymousView.setTitle(LocaleController.getString("AppName", 2131165299));
            paramAnonymousView.setMessage(LocaleController.getString("AreYouSureRegistration", 2131165325));
            paramAnonymousView.setPositiveButton(LocaleController.getString("OK", 2131166044), new DialogInterface.OnClickListener()
            {
              public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
              {
                LoginActivity.LoginActivityRegisterView.this.onBackPressed();
                LoginActivity.this.setPage(0, true, null, true);
              }
            });
            paramAnonymousView.setNegativeButton(LocaleController.getString("Cancel", 2131165386), null);
            LoginActivity.this.showDialog(paramAnonymousView.create());
          }
        });
        return;
        i = 3;
        break;
        i = 3;
        break label79;
        i = 3;
        break label384;
      }
    }
    
    public String getHeaderName()
    {
      return LocaleController.getString("YourName", 2131166428);
    }
    
    public void onBackPressed()
    {
      this.currentParams = null;
    }
    
    public void onNextPressed()
    {
      if (this.nextPressed) {
        return;
      }
      this.nextPressed = true;
      TLRPC.TL_auth_signUp localTL_auth_signUp = new TLRPC.TL_auth_signUp();
      localTL_auth_signUp.phone_code = this.phoneCode;
      localTL_auth_signUp.phone_code_hash = this.phoneHash;
      localTL_auth_signUp.phone_number = this.requestPhone;
      localTL_auth_signUp.first_name = this.firstNameField.getText().toString();
      localTL_auth_signUp.last_name = this.lastNameField.getText().toString();
      LoginActivity.this.needShowProgress();
      ConnectionsManager.getInstance().sendRequest(localTL_auth_signUp, new RequestDelegate()
      {
        public void run(final TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
        {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              LoginActivity.LoginActivityRegisterView.access$6602(LoginActivity.LoginActivityRegisterView.this, false);
              LoginActivity.this.needHideProgress();
              if (paramAnonymousTL_error == null)
              {
                TLRPC.TL_auth_authorization localTL_auth_authorization = (TLRPC.TL_auth_authorization)paramAnonymousTLObject;
                ConnectionsManager.getInstance().setUserId(localTL_auth_authorization.user.id);
                UserConfig.clearConfig();
                MessagesController.getInstance().cleanup();
                UserConfig.setCurrentUser(localTL_auth_authorization.user);
                UserConfig.saveConfig(true);
                MessagesStorage.getInstance().cleanup(true);
                ArrayList localArrayList = new ArrayList();
                localArrayList.add(localTL_auth_authorization.user);
                MessagesStorage.getInstance().putUsersAndChats(localArrayList, null, true, true);
                MessagesController.getInstance().putUser(localTL_auth_authorization.user, false);
                ContactsController.getInstance().checkAppAccount();
                MessagesController.getInstance().getBlockedUsers(true);
                LoginActivity.this.needFinishActivity();
                return;
              }
              if (paramAnonymousTL_error.text.contains("PHONE_NUMBER_INVALID"))
              {
                LoginActivity.this.needShowAlert(LocaleController.getString("AppName", 2131165299), LocaleController.getString("InvalidPhoneNumber", 2131165758));
                return;
              }
              if ((paramAnonymousTL_error.text.contains("PHONE_CODE_EMPTY")) || (paramAnonymousTL_error.text.contains("PHONE_CODE_INVALID")))
              {
                LoginActivity.this.needShowAlert(LocaleController.getString("AppName", 2131165299), LocaleController.getString("InvalidCode", 2131165754));
                return;
              }
              if (paramAnonymousTL_error.text.contains("PHONE_CODE_EXPIRED"))
              {
                LoginActivity.this.needShowAlert(LocaleController.getString("AppName", 2131165299), LocaleController.getString("CodeExpired", 2131165517));
                return;
              }
              if (paramAnonymousTL_error.text.contains("FIRSTNAME_INVALID"))
              {
                LoginActivity.this.needShowAlert(LocaleController.getString("AppName", 2131165299), LocaleController.getString("InvalidFirstName", 2131165755));
                return;
              }
              if (paramAnonymousTL_error.text.contains("LASTNAME_INVALID"))
              {
                LoginActivity.this.needShowAlert(LocaleController.getString("AppName", 2131165299), LocaleController.getString("InvalidLastName", 2131165756));
                return;
              }
              LoginActivity.this.needShowAlert(LocaleController.getString("AppName", 2131165299), paramAnonymousTL_error.text);
            }
          });
        }
      }, 10);
    }
    
    public void onShow()
    {
      super.onShow();
      if (this.firstNameField != null)
      {
        this.firstNameField.requestFocus();
        this.firstNameField.setSelection(this.firstNameField.length());
      }
    }
    
    public void restoreStateParams(Bundle paramBundle)
    {
      this.currentParams = paramBundle.getBundle("registerview_params");
      if (this.currentParams != null) {
        setParams(this.currentParams);
      }
      String str = paramBundle.getString("registerview_first");
      if (str != null) {
        this.firstNameField.setText(str);
      }
      paramBundle = paramBundle.getString("registerview_last");
      if (paramBundle != null) {
        this.lastNameField.setText(paramBundle);
      }
    }
    
    public void saveStateParams(Bundle paramBundle)
    {
      String str = this.firstNameField.getText().toString();
      if (str.length() != 0) {
        paramBundle.putString("registerview_first", str);
      }
      str = this.lastNameField.getText().toString();
      if (str.length() != 0) {
        paramBundle.putString("registerview_last", str);
      }
      if (this.currentParams != null) {
        paramBundle.putBundle("registerview_params", this.currentParams);
      }
    }
    
    public void setParams(Bundle paramBundle)
    {
      if (paramBundle == null) {
        return;
      }
      this.firstNameField.setText("");
      this.lastNameField.setText("");
      this.requestPhone = paramBundle.getString("phoneFormated");
      this.phoneHash = paramBundle.getString("phoneHash");
      this.phoneCode = paramBundle.getString("code");
      this.currentParams = paramBundle;
    }
  }
  
  public class LoginActivityResetWaitView
    extends SlideView
  {
    private TextView confirmTextView;
    private Bundle currentParams;
    private String phoneCode;
    private String phoneHash;
    private String requestPhone;
    private TextView resetAccountButton;
    private TextView resetAccountTime;
    private int startTime;
    private Runnable timeRunnable;
    private int waitTime;
    
    public LoginActivityResetWaitView(Context paramContext)
    {
      super();
      setOrientation(1);
      this.confirmTextView = new TextView(paramContext);
      this.confirmTextView.setTextColor(-9079435);
      this.confirmTextView.setTextSize(1, 14.0F);
      TextView localTextView = this.confirmTextView;
      if (LocaleController.isRTL)
      {
        i = 5;
        localTextView.setGravity(i);
        this.confirmTextView.setLineSpacing(AndroidUtilities.dp(2.0F), 1.0F);
        localTextView = this.confirmTextView;
        if (!LocaleController.isRTL) {
          break label461;
        }
        i = 5;
        label96:
        addView(localTextView, LayoutHelper.createLinear(-2, -2, i));
        localTextView = new TextView(paramContext);
        if (!LocaleController.isRTL) {
          break label466;
        }
        i = 5;
        label128:
        localTextView.setGravity(i | 0x30);
        localTextView.setTextColor(-9079435);
        localTextView.setText(LocaleController.getString("ResetAccountStatus", 2131166175));
        localTextView.setTextSize(1, 14.0F);
        localTextView.setLineSpacing(AndroidUtilities.dp(2.0F), 1.0F);
        if (!LocaleController.isRTL) {
          break label471;
        }
        i = 5;
        label183:
        addView(localTextView, LayoutHelper.createLinear(-2, -2, i | 0x30, 0, 24, 0, 0));
        this.resetAccountTime = new TextView(paramContext);
        localTextView = this.resetAccountTime;
        if (!LocaleController.isRTL) {
          break label476;
        }
        i = 5;
        label231:
        localTextView.setGravity(i | 0x30);
        this.resetAccountTime.setTextColor(-9079435);
        this.resetAccountTime.setTextSize(1, 14.0F);
        this.resetAccountTime.setLineSpacing(AndroidUtilities.dp(2.0F), 1.0F);
        localTextView = this.resetAccountTime;
        if (!LocaleController.isRTL) {
          break label481;
        }
        i = 5;
        label286:
        addView(localTextView, LayoutHelper.createLinear(-2, -2, i | 0x30, 0, 2, 0, 0));
        this.resetAccountButton = new TextView(paramContext);
        paramContext = this.resetAccountButton;
        if (!LocaleController.isRTL) {
          break label486;
        }
        i = 5;
        label332:
        paramContext.setGravity(i | 0x30);
        this.resetAccountButton.setText(LocaleController.getString("ResetAccountButton", 2131166172));
        this.resetAccountButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.resetAccountButton.setTextSize(1, 14.0F);
        this.resetAccountButton.setLineSpacing(AndroidUtilities.dp(2.0F), 1.0F);
        this.resetAccountButton.setPadding(0, AndroidUtilities.dp(14.0F), 0, 0);
        paramContext = this.resetAccountButton;
        if (!LocaleController.isRTL) {
          break label491;
        }
      }
      label461:
      label466:
      label471:
      label476:
      label481:
      label486:
      label491:
      for (int i = j;; i = 3)
      {
        addView(paramContext, LayoutHelper.createLinear(-2, -2, i | 0x30, 0, 7, 0, 0));
        this.resetAccountButton.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            if (Math.abs(ConnectionsManager.getInstance().getCurrentTime() - LoginActivity.LoginActivityResetWaitView.this.startTime) < LoginActivity.LoginActivityResetWaitView.this.waitTime) {
              return;
            }
            paramAnonymousView = new AlertDialog.Builder(LoginActivity.this.getParentActivity());
            paramAnonymousView.setMessage(LocaleController.getString("ResetMyAccountWarningText", 2131166181));
            paramAnonymousView.setTitle(LocaleController.getString("ResetMyAccountWarning", 2131166179));
            paramAnonymousView.setPositiveButton(LocaleController.getString("ResetMyAccountWarningReset", 2131166180), new DialogInterface.OnClickListener()
            {
              public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
              {
                LoginActivity.this.needShowProgress();
                paramAnonymous2DialogInterface = new TLRPC.TL_account_deleteAccount();
                paramAnonymous2DialogInterface.reason = "Forgot password";
                ConnectionsManager.getInstance().sendRequest(paramAnonymous2DialogInterface, new RequestDelegate()
                {
                  public void run(TLObject paramAnonymous3TLObject, final TLRPC.TL_error paramAnonymous3TL_error)
                  {
                    AndroidUtilities.runOnUIThread(new Runnable()
                    {
                      public void run()
                      {
                        LoginActivity.this.needHideProgress();
                        if (paramAnonymous3TL_error == null)
                        {
                          Bundle localBundle = new Bundle();
                          localBundle.putString("phoneFormated", LoginActivity.LoginActivityResetWaitView.this.requestPhone);
                          localBundle.putString("phoneHash", LoginActivity.LoginActivityResetWaitView.this.phoneHash);
                          localBundle.putString("code", LoginActivity.LoginActivityResetWaitView.this.phoneCode);
                          LoginActivity.this.setPage(5, true, localBundle, false);
                          return;
                        }
                        if (paramAnonymous3TL_error.text.equals("2FA_RECENT_CONFIRM"))
                        {
                          LoginActivity.this.needShowAlert(LocaleController.getString("AppName", 2131165299), LocaleController.getString("ResetAccountCancelledAlert", 2131166173));
                          return;
                        }
                        LoginActivity.this.needShowAlert(LocaleController.getString("AppName", 2131165299), paramAnonymous3TL_error.text);
                      }
                    });
                  }
                }, 10);
              }
            });
            paramAnonymousView.setNegativeButton(LocaleController.getString("Cancel", 2131165386), null);
            LoginActivity.this.showDialog(paramAnonymousView.create());
          }
        });
        return;
        i = 3;
        break;
        i = 3;
        break label96;
        i = 3;
        break label128;
        i = 3;
        break label183;
        i = 3;
        break label231;
        i = 3;
        break label286;
        i = 3;
        break label332;
      }
    }
    
    private void updateTimeText()
    {
      int i = Math.max(0, this.waitTime - (ConnectionsManager.getInstance().getCurrentTime() - this.startTime));
      int j = i / 86400;
      int k = (i - j * 86400) / 3600;
      int m = (i - j * 86400 - k * 3600) / 60;
      if (j != 0) {
        this.resetAccountTime.setText(AndroidUtilities.replaceTags(LocaleController.formatPluralString("DaysBold", j) + " " + LocaleController.formatPluralString("HoursBold", k) + " " + LocaleController.formatPluralString("MinutesBold", m)));
      }
      while (i > 0)
      {
        this.resetAccountButton.setTextColor(-2004318072);
        return;
        this.resetAccountTime.setText(AndroidUtilities.replaceTags(LocaleController.formatPluralString("HoursBold", k) + " " + LocaleController.formatPluralString("MinutesBold", m) + " " + LocaleController.formatPluralString("SecondsBold", i % 60)));
      }
      this.resetAccountButton.setTextColor(-39322);
    }
    
    public String getHeaderName()
    {
      return LocaleController.getString("ResetAccount", 2131166171);
    }
    
    public boolean needBackButton()
    {
      return true;
    }
    
    public void onBackPressed()
    {
      AndroidUtilities.cancelRunOnUIThread(this.timeRunnable);
      this.timeRunnable = null;
      this.currentParams = null;
    }
    
    public void restoreStateParams(Bundle paramBundle)
    {
      this.currentParams = paramBundle.getBundle("resetview_params");
      if (this.currentParams != null) {
        setParams(this.currentParams);
      }
    }
    
    public void saveStateParams(Bundle paramBundle)
    {
      if (this.currentParams != null) {
        paramBundle.putBundle("resetview_params", this.currentParams);
      }
    }
    
    public void setParams(Bundle paramBundle)
    {
      if (paramBundle == null) {
        return;
      }
      this.currentParams = paramBundle;
      this.requestPhone = paramBundle.getString("phoneFormated");
      this.phoneHash = paramBundle.getString("phoneHash");
      this.phoneCode = paramBundle.getString("code");
      this.startTime = paramBundle.getInt("startTime");
      this.waitTime = paramBundle.getInt("waitTime");
      this.confirmTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("ResetAccountInfo", 2131166174, new Object[] { PhoneFormat.getInstance().format("+" + this.requestPhone) })));
      updateTimeText();
      this.timeRunnable = new Runnable()
      {
        public void run()
        {
          if (LoginActivity.LoginActivityResetWaitView.this.timeRunnable != this) {
            return;
          }
          LoginActivity.LoginActivityResetWaitView.this.updateTimeText();
          AndroidUtilities.runOnUIThread(LoginActivity.LoginActivityResetWaitView.this.timeRunnable, 1000L);
        }
      };
      AndroidUtilities.runOnUIThread(this.timeRunnable, 1000L);
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
          break label943;
        }
        localObject1 = new FrameLayout(paramContext);
        localObject2 = new ImageView(paramContext);
        ((ImageView)localObject2).setImageResource(2130837877);
        if (!LocaleController.isRTL) {
          break label877;
        }
        ((FrameLayout)localObject1).addView((View)localObject2, LayoutHelper.createFrame(64, 76.0F, 19, 2.0F, 2.0F, 0.0F, 0.0F));
        localObject2 = this.confirmTextView;
        if (!LocaleController.isRTL) {
          break label872;
        }
        paramInt = 5;
        label195:
        ((FrameLayout)localObject1).addView((View)localObject2, LayoutHelper.createFrame(-1, -2.0F, paramInt, 82.0F, 0.0F, 0.0F, 0.0F));
        if (!LocaleController.isRTL) {
          break label938;
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
            if (LoginActivity.LoginActivitySmsView.this.ignoreOnTextChange) {}
            while ((LoginActivity.LoginActivitySmsView.this.length == 0) || (LoginActivity.LoginActivitySmsView.this.codeField.length() != LoginActivity.LoginActivitySmsView.this.length)) {
              return;
            }
            LoginActivity.LoginActivitySmsView.this.onNextPressed();
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
              LoginActivity.LoginActivitySmsView.this.onNextPressed();
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
          break label979;
        }
        paramInt = 5;
        label476:
        ((TextView)localObject1).setGravity(paramInt);
        localObject1 = this.timeText;
        if (!LocaleController.isRTL) {
          break label984;
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
          break label989;
        }
        paramInt = 5;
        label596:
        ((TextView)localObject1).setGravity(paramInt);
        this.problemText.setTextSize(1, 14.0F);
        this.problemText.setTextColor(-11697229);
        this.problemText.setLineSpacing(AndroidUtilities.dp(2.0F), 1.0F);
        this.problemText.setPadding(0, AndroidUtilities.dp(2.0F), 0, AndroidUtilities.dp(12.0F));
        localObject1 = this.problemText;
        if (!LocaleController.isRTL) {
          break label994;
        }
        paramInt = 5;
        label667:
        addView((View)localObject1, LayoutHelper.createLinear(-2, -2, paramInt, 0, 20, 0, 0));
        this.problemText.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            if (LoginActivity.LoginActivitySmsView.this.nextPressed) {
              return;
            }
            if ((LoginActivity.LoginActivitySmsView.this.nextType != 0) && (LoginActivity.LoginActivitySmsView.this.nextType != 4))
            {
              LoginActivity.LoginActivitySmsView.this.resendCode();
              return;
            }
            try
            {
              paramAnonymousView = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
              paramAnonymousView = String.format(Locale.US, "%s (%d)", new Object[] { paramAnonymousView.versionName, Integer.valueOf(paramAnonymousView.versionCode) });
              Intent localIntent = new Intent("android.intent.action.SEND");
              localIntent.setType("message/rfc822");
              localIntent.putExtra("android.intent.extra.EMAIL", new String[] { "sms@stel.com" });
              localIntent.putExtra("android.intent.extra.SUBJECT", "Android registration/login issue " + paramAnonymousView + " " + LoginActivity.LoginActivitySmsView.this.emailPhone);
              localIntent.putExtra("android.intent.extra.TEXT", "Phone: " + LoginActivity.LoginActivitySmsView.this.requestPhone + "\nApp version: " + paramAnonymousView + "\nOS version: SDK " + Build.VERSION.SDK_INT + "\nDevice Name: " + Build.MANUFACTURER + Build.MODEL + "\nLocale: " + Locale.getDefault() + "\nError: " + LoginActivity.LoginActivitySmsView.this.lastError);
              LoginActivity.LoginActivitySmsView.this.getContext().startActivity(Intent.createChooser(localIntent, "Send email..."));
              return;
            }
            catch (Exception paramAnonymousView)
            {
              LoginActivity.this.needShowAlert(LocaleController.getString("AppName", 2131165299), LocaleController.getString("NoMailInstalled", 2131165935));
            }
          }
        });
        localObject1 = new LinearLayout(paramContext);
        if (!LocaleController.isRTL) {
          break label999;
        }
        paramInt = 5;
        label720:
        ((LinearLayout)localObject1).setGravity(paramInt | 0x10);
        if (!LocaleController.isRTL) {
          break label1004;
        }
        paramInt = 5;
        label737:
        addView((View)localObject1, LayoutHelper.createLinear(-1, -1, paramInt));
        paramContext = new TextView(paramContext);
        if (!LocaleController.isRTL) {
          break label1009;
        }
        paramInt = 5;
        label766:
        paramContext.setGravity(paramInt | 0x1);
        paramContext.setTextColor(-11697229);
        paramContext.setTextSize(1, 14.0F);
        paramContext.setLineSpacing(AndroidUtilities.dp(2.0F), 1.0F);
        paramContext.setPadding(0, AndroidUtilities.dp(24.0F), 0, 0);
        if (!LocaleController.isRTL) {
          break label1014;
        }
      }
      label872:
      label877:
      label938:
      label943:
      label979:
      label984:
      label989:
      label994:
      label999:
      label1004:
      label1009:
      label1014:
      for (paramInt = 5;; paramInt = 3)
      {
        ((LinearLayout)localObject1).addView(paramContext, LayoutHelper.createLinear(-2, -2, paramInt | 0x50, 0, 0, 0, 10));
        paramContext.setText(LocaleController.getString("WrongNumber", 2131166407));
        paramContext.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            paramAnonymousView = new TLRPC.TL_auth_cancelCode();
            paramAnonymousView.phone_number = LoginActivity.LoginActivitySmsView.this.requestPhone;
            paramAnonymousView.phone_code_hash = LoginActivity.LoginActivitySmsView.this.phoneHash;
            ConnectionsManager.getInstance().sendRequest(paramAnonymousView, new RequestDelegate()
            {
              public void run(TLObject paramAnonymous2TLObject, TLRPC.TL_error paramAnonymous2TL_error) {}
            }, 10);
            LoginActivity.LoginActivitySmsView.this.onBackPressed();
            LoginActivity.this.setPage(0, true, null, true);
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
        break label596;
        paramInt = 3;
        break label667;
        paramInt = 3;
        break label720;
        paramInt = 3;
        break label737;
        paramInt = 3;
        break label766;
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
          double d2 = LoginActivity.LoginActivitySmsView.this.lastCodeTime;
          LoginActivity.LoginActivitySmsView.access$3202(LoginActivity.LoginActivitySmsView.this, (int)(LoginActivity.LoginActivitySmsView.this.codeTime - (d1 - d2)));
          LoginActivity.LoginActivitySmsView.access$3102(LoginActivity.LoginActivitySmsView.this, d1);
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              if (LoginActivity.LoginActivitySmsView.this.codeTime <= 1000)
              {
                LoginActivity.LoginActivitySmsView.this.problemText.setVisibility(0);
                LoginActivity.LoginActivitySmsView.this.destroyCodeTimer();
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
          if (LoginActivity.LoginActivitySmsView.this.timeTimer == null) {
            return;
          }
          double d1 = System.currentTimeMillis();
          double d2 = LoginActivity.LoginActivitySmsView.this.lastCurrentTime;
          LoginActivity.LoginActivitySmsView.access$3702(LoginActivity.LoginActivitySmsView.this, (int)(LoginActivity.LoginActivitySmsView.this.time - (d1 - d2)));
          LoginActivity.LoginActivitySmsView.access$3602(LoginActivity.LoginActivitySmsView.this, d1);
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              int i;
              int j;
              if (LoginActivity.LoginActivitySmsView.this.time >= 1000)
              {
                i = LoginActivity.LoginActivitySmsView.this.time / 1000 / 60;
                j = LoginActivity.LoginActivitySmsView.this.time / 1000 - i * 60;
                if ((LoginActivity.LoginActivitySmsView.this.nextType == 4) || (LoginActivity.LoginActivitySmsView.this.nextType == 3)) {
                  LoginActivity.LoginActivitySmsView.this.timeText.setText(LocaleController.formatString("CallText", 2131165384, new Object[] { Integer.valueOf(i), Integer.valueOf(j) }));
                }
              }
              do
              {
                do
                {
                  for (;;)
                  {
                    if (LoginActivity.LoginActivitySmsView.this.progressView != null) {
                      LoginActivity.LoginActivitySmsView.this.progressView.setProgress(1.0F - LoginActivity.LoginActivitySmsView.this.time / LoginActivity.LoginActivitySmsView.this.timeout);
                    }
                    return;
                    if (LoginActivity.LoginActivitySmsView.this.nextType == 2) {
                      LoginActivity.LoginActivitySmsView.this.timeText.setText(LocaleController.formatString("SmsText", 2131166300, new Object[] { Integer.valueOf(i), Integer.valueOf(j) }));
                    }
                  }
                  if (LoginActivity.LoginActivitySmsView.this.progressView != null) {
                    LoginActivity.LoginActivitySmsView.this.progressView.setProgress(1.0F);
                  }
                  LoginActivity.LoginActivitySmsView.this.destroyTimer();
                  if (LoginActivity.LoginActivitySmsView.this.currentType == 3)
                  {
                    AndroidUtilities.setWaitingForCall(false);
                    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didReceiveCall);
                    LoginActivity.LoginActivitySmsView.access$4302(LoginActivity.LoginActivitySmsView.this, false);
                    LoginActivity.LoginActivitySmsView.this.destroyCodeTimer();
                    LoginActivity.LoginActivitySmsView.this.resendCode();
                    return;
                  }
                } while (LoginActivity.LoginActivitySmsView.this.currentType != 2);
                if (LoginActivity.LoginActivitySmsView.this.nextType == 4)
                {
                  LoginActivity.LoginActivitySmsView.this.timeText.setText(LocaleController.getString("Calling", 2131165385));
                  LoginActivity.LoginActivitySmsView.this.createCodeTimer();
                  TLRPC.TL_auth_resendCode localTL_auth_resendCode = new TLRPC.TL_auth_resendCode();
                  localTL_auth_resendCode.phone_number = LoginActivity.LoginActivitySmsView.this.requestPhone;
                  localTL_auth_resendCode.phone_code_hash = LoginActivity.LoginActivitySmsView.this.phoneHash;
                  ConnectionsManager.getInstance().sendRequest(localTL_auth_resendCode, new RequestDelegate()
                  {
                    public void run(TLObject paramAnonymous3TLObject, final TLRPC.TL_error paramAnonymous3TL_error)
                    {
                      if ((paramAnonymous3TL_error != null) && (paramAnonymous3TL_error.text != null)) {
                        AndroidUtilities.runOnUIThread(new Runnable()
                        {
                          public void run()
                          {
                            LoginActivity.LoginActivitySmsView.access$2902(LoginActivity.LoginActivitySmsView.this, paramAnonymous3TL_error.text);
                          }
                        });
                      }
                    }
                  }, 10);
                  return;
                }
              } while (LoginActivity.LoginActivitySmsView.this.nextType != 3);
              AndroidUtilities.setWaitingForSms(false);
              NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didReceiveSmsCode);
              LoginActivity.LoginActivitySmsView.access$4302(LoginActivity.LoginActivitySmsView.this, false);
              LoginActivity.LoginActivitySmsView.this.destroyCodeTimer();
              LoginActivity.LoginActivitySmsView.this.resendCode();
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
      LoginActivity.this.needShowProgress();
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
              LoginActivity.LoginActivitySmsView.access$2402(LoginActivity.LoginActivitySmsView.this, false);
              if (paramAnonymousTL_error == null) {
                LoginActivity.this.fillNextCodeParams(LoginActivity.LoginActivitySmsView.5.this.val$params, (TLRPC.TL_auth_sentCode)paramAnonymousTLObject);
              }
              for (;;)
              {
                LoginActivity.this.needHideProgress();
                return;
                if (paramAnonymousTL_error.text != null) {
                  if (paramAnonymousTL_error.text.contains("PHONE_NUMBER_INVALID"))
                  {
                    LoginActivity.this.needShowAlert(LocaleController.getString("AppName", 2131165299), LocaleController.getString("InvalidPhoneNumber", 2131165758));
                  }
                  else if ((paramAnonymousTL_error.text.contains("PHONE_CODE_EMPTY")) || (paramAnonymousTL_error.text.contains("PHONE_CODE_INVALID")))
                  {
                    LoginActivity.this.needShowAlert(LocaleController.getString("AppName", 2131165299), LocaleController.getString("InvalidCode", 2131165754));
                  }
                  else if (paramAnonymousTL_error.text.contains("PHONE_CODE_EXPIRED"))
                  {
                    LoginActivity.LoginActivitySmsView.this.onBackPressed();
                    LoginActivity.this.setPage(0, true, null, true);
                    LoginActivity.this.needShowAlert(LocaleController.getString("AppName", 2131165299), LocaleController.getString("CodeExpired", 2131165517));
                  }
                  else if (paramAnonymousTL_error.text.startsWith("FLOOD_WAIT"))
                  {
                    LoginActivity.this.needShowAlert(LocaleController.getString("AppName", 2131165299), LocaleController.getString("FloodWait", 2131165639));
                  }
                  else if (paramAnonymousTL_error.code != 64536)
                  {
                    LoginActivity.this.needShowAlert(LocaleController.getString("AppName", 2131165299), LocaleController.getString("ErrorOccurred", 2131165626) + "\n" + paramAnonymousTL_error.text);
                  }
                }
              }
            }
          });
        }
      }, 10);
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
        final TLRPC.TL_auth_signIn localTL_auth_signIn = new TLRPC.TL_auth_signIn();
        localTL_auth_signIn.phone_number = this.requestPhone;
        localTL_auth_signIn.phone_code = this.codeField.getText().toString();
        localTL_auth_signIn.phone_code_hash = this.phoneHash;
        destroyTimer();
        LoginActivity.this.needShowProgress();
        ConnectionsManager.getInstance().sendRequest(localTL_auth_signIn, new RequestDelegate()
        {
          public void run(final TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
          {
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                LoginActivity.LoginActivitySmsView.access$2402(LoginActivity.LoginActivitySmsView.this, false);
                Object localObject;
                if (paramAnonymousTL_error == null)
                {
                  LoginActivity.this.needHideProgress();
                  localObject = (TLRPC.TL_auth_authorization)paramAnonymousTLObject;
                  ConnectionsManager.getInstance().setUserId(((TLRPC.TL_auth_authorization)localObject).user.id);
                  LoginActivity.LoginActivitySmsView.this.destroyTimer();
                  LoginActivity.LoginActivitySmsView.this.destroyCodeTimer();
                  UserConfig.clearConfig();
                  MessagesController.getInstance().cleanup();
                  UserConfig.setCurrentUser(((TLRPC.TL_auth_authorization)localObject).user);
                  UserConfig.saveConfig(true);
                  MessagesStorage.getInstance().cleanup(true);
                  ArrayList localArrayList = new ArrayList();
                  localArrayList.add(((TLRPC.TL_auth_authorization)localObject).user);
                  MessagesStorage.getInstance().putUsersAndChats(localArrayList, null, true, true);
                  MessagesController.getInstance().putUser(((TLRPC.TL_auth_authorization)localObject).user, false);
                  ContactsController.getInstance().checkAppAccount();
                  MessagesController.getInstance().getBlockedUsers(true);
                  LoginActivity.this.needFinishActivity();
                  return;
                }
                LoginActivity.LoginActivitySmsView.access$2902(LoginActivity.LoginActivitySmsView.this, paramAnonymousTL_error.text);
                if (paramAnonymousTL_error.text.contains("PHONE_NUMBER_UNOCCUPIED"))
                {
                  LoginActivity.this.needHideProgress();
                  localObject = new Bundle();
                  ((Bundle)localObject).putString("phoneFormated", LoginActivity.LoginActivitySmsView.this.requestPhone);
                  ((Bundle)localObject).putString("phoneHash", LoginActivity.LoginActivitySmsView.this.phoneHash);
                  ((Bundle)localObject).putString("code", LoginActivity.LoginActivitySmsView.8.this.val$req.phone_code);
                  LoginActivity.this.setPage(5, true, (Bundle)localObject, false);
                  LoginActivity.LoginActivitySmsView.this.destroyTimer();
                  LoginActivity.LoginActivitySmsView.this.destroyCodeTimer();
                  return;
                }
                if (paramAnonymousTL_error.text.contains("SESSION_PASSWORD_NEEDED"))
                {
                  localObject = new TLRPC.TL_account_getPassword();
                  ConnectionsManager.getInstance().sendRequest((TLObject)localObject, new RequestDelegate()
                  {
                    public void run(final TLObject paramAnonymous3TLObject, final TLRPC.TL_error paramAnonymous3TL_error)
                    {
                      AndroidUtilities.runOnUIThread(new Runnable()
                      {
                        public void run()
                        {
                          LoginActivity.this.needHideProgress();
                          if (paramAnonymous3TL_error == null)
                          {
                            TLRPC.TL_account_password localTL_account_password = (TLRPC.TL_account_password)paramAnonymous3TLObject;
                            Bundle localBundle = new Bundle();
                            localBundle.putString("current_salt", Utilities.bytesToHex(localTL_account_password.current_salt));
                            localBundle.putString("hint", localTL_account_password.hint);
                            localBundle.putString("email_unconfirmed_pattern", localTL_account_password.email_unconfirmed_pattern);
                            localBundle.putString("phoneFormated", LoginActivity.LoginActivitySmsView.this.requestPhone);
                            localBundle.putString("phoneHash", LoginActivity.LoginActivitySmsView.this.phoneHash);
                            localBundle.putString("code", LoginActivity.LoginActivitySmsView.8.this.val$req.phone_code);
                            if (localTL_account_password.has_recovery) {}
                            for (int i = 1;; i = 0)
                            {
                              localBundle.putInt("has_recovery", i);
                              LoginActivity.this.setPage(6, true, localBundle, false);
                              return;
                            }
                          }
                          LoginActivity.this.needShowAlert(LocaleController.getString("AppName", 2131165299), paramAnonymous3TL_error.text);
                        }
                      });
                    }
                  }, 10);
                  LoginActivity.LoginActivitySmsView.this.destroyTimer();
                  LoginActivity.LoginActivitySmsView.this.destroyCodeTimer();
                  return;
                }
                LoginActivity.this.needHideProgress();
                if (((LoginActivity.LoginActivitySmsView.this.currentType == 3) && ((LoginActivity.LoginActivitySmsView.this.nextType == 4) || (LoginActivity.LoginActivitySmsView.this.nextType == 2))) || ((LoginActivity.LoginActivitySmsView.this.currentType == 2) && ((LoginActivity.LoginActivitySmsView.this.nextType == 4) || (LoginActivity.LoginActivitySmsView.this.nextType == 3)))) {
                  LoginActivity.LoginActivitySmsView.this.createTimer();
                }
                if (LoginActivity.LoginActivitySmsView.this.currentType == 2)
                {
                  AndroidUtilities.setWaitingForSms(true);
                  NotificationCenter.getInstance().addObserver(LoginActivity.LoginActivitySmsView.this, NotificationCenter.didReceiveSmsCode);
                }
                for (;;)
                {
                  LoginActivity.LoginActivitySmsView.access$4302(LoginActivity.LoginActivitySmsView.this, true);
                  if (LoginActivity.LoginActivitySmsView.this.currentType == 3) {
                    break;
                  }
                  if (!paramAnonymousTL_error.text.contains("PHONE_NUMBER_INVALID")) {
                    break label615;
                  }
                  LoginActivity.this.needShowAlert(LocaleController.getString("AppName", 2131165299), LocaleController.getString("InvalidPhoneNumber", 2131165758));
                  return;
                  if (LoginActivity.LoginActivitySmsView.this.currentType == 3)
                  {
                    AndroidUtilities.setWaitingForCall(true);
                    NotificationCenter.getInstance().addObserver(LoginActivity.LoginActivitySmsView.this, NotificationCenter.didReceiveCall);
                  }
                }
                label615:
                if ((paramAnonymousTL_error.text.contains("PHONE_CODE_EMPTY")) || (paramAnonymousTL_error.text.contains("PHONE_CODE_INVALID")))
                {
                  LoginActivity.this.needShowAlert(LocaleController.getString("AppName", 2131165299), LocaleController.getString("InvalidCode", 2131165754));
                  return;
                }
                if (paramAnonymousTL_error.text.contains("PHONE_CODE_EXPIRED"))
                {
                  LoginActivity.LoginActivitySmsView.this.onBackPressed();
                  LoginActivity.this.setPage(0, true, null, true);
                  LoginActivity.this.needShowAlert(LocaleController.getString("AppName", 2131165299), LocaleController.getString("CodeExpired", 2131165517));
                  return;
                }
                if (paramAnonymousTL_error.text.startsWith("FLOOD_WAIT"))
                {
                  LoginActivity.this.needShowAlert(LocaleController.getString("AppName", 2131165299), LocaleController.getString("FloodWait", 2131165639));
                  return;
                }
                LoginActivity.this.needShowAlert(LocaleController.getString("AppName", 2131165299), LocaleController.getString("ErrorOccurred", 2131165626) + "\n" + paramAnonymousTL_error.text);
              }
            });
          }
        }, 10);
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
    
    public void restoreStateParams(Bundle paramBundle)
    {
      this.currentParams = paramBundle.getBundle("smsview_params_" + this.currentType);
      if (this.currentParams != null) {
        setParams(this.currentParams);
      }
      String str = paramBundle.getString("smsview_code_" + this.currentType);
      if (str != null) {
        this.codeField.setText(str);
      }
      int i = paramBundle.getInt("time");
      if (i != 0) {
        this.time = i;
      }
      i = paramBundle.getInt("open");
      if (i != 0) {
        this.openTime = i;
      }
    }
    
    public void saveStateParams(Bundle paramBundle)
    {
      String str = this.codeField.getText().toString();
      if (str.length() != 0) {
        paramBundle.putString("smsview_code_" + this.currentType, str);
      }
      if (this.currentParams != null) {
        paramBundle.putBundle("smsview_params_" + this.currentType, this.currentParams);
      }
      if (this.time != 0) {
        paramBundle.putInt("time", this.time);
      }
      if (this.openTime != 0) {
        paramBundle.putInt("open", this.openTime);
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
                LoginActivity.PhoneView.this.selectCountry(paramAnonymous2String);
                AndroidUtilities.runOnUIThread(new Runnable()
                {
                  public void run()
                  {
                    AndroidUtilities.showKeyboard(LoginActivity.PhoneView.this.phoneField);
                  }
                }, 300L);
                LoginActivity.PhoneView.this.phoneField.requestFocus();
                LoginActivity.PhoneView.this.phoneField.setSelection(LoginActivity.PhoneView.this.phoneField.length());
              }
            });
            LoginActivity.this.presentFragment(paramAnonymousView);
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
            if (LoginActivity.PhoneView.this.ignoreOnTextChange) {
              return;
            }
            LoginActivity.PhoneView.access$502(LoginActivity.PhoneView.this, true);
            Object localObject3 = PhoneFormat.stripExceptNumbers(LoginActivity.PhoneView.this.codeField.getText().toString());
            LoginActivity.PhoneView.this.codeField.setText((CharSequence)localObject3);
            if (((String)localObject3).length() == 0)
            {
              LoginActivity.PhoneView.this.countryButton.setText(LocaleController.getString("ChooseCountry", 2131165505));
              LoginActivity.PhoneView.this.phoneField.setHintText(null);
              LoginActivity.PhoneView.access$802(LoginActivity.PhoneView.this, 1);
              LoginActivity.PhoneView.access$502(LoginActivity.PhoneView.this, false);
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
              LoginActivity.PhoneView.access$502(LoginActivity.PhoneView.this, true);
              j = 4;
              i = k;
              localObject1 = localObject3;
              paramAnonymousEditable = (Editable)localObject4;
              if (j >= 1)
              {
                paramAnonymousEditable = ((String)localObject3).substring(0, j);
                if ((String)LoginActivity.PhoneView.this.codesMap.get(paramAnonymousEditable) == null) {
                  break label541;
                }
                i = 1;
                localObject2 = ((String)localObject3).substring(j, ((String)localObject3).length()) + LoginActivity.PhoneView.this.phoneField.getText().toString();
                localObject3 = LoginActivity.PhoneView.this.codeField;
                localObject1 = paramAnonymousEditable;
                ((EditText)localObject3).setText(paramAnonymousEditable);
                paramAnonymousEditable = (Editable)localObject2;
              }
              j = i;
              localObject2 = localObject1;
              if (i == 0)
              {
                LoginActivity.PhoneView.access$502(LoginActivity.PhoneView.this, true);
                paramAnonymousEditable = ((String)localObject1).substring(1, ((String)localObject1).length()) + LoginActivity.PhoneView.this.phoneField.getText().toString();
                localObject3 = LoginActivity.PhoneView.this.codeField;
                localObject2 = ((String)localObject1).substring(0, 1);
                ((EditText)localObject3).setText((CharSequence)localObject2);
                j = i;
              }
            }
            Object localObject1 = (String)LoginActivity.PhoneView.this.codesMap.get(localObject2);
            if (localObject1 != null)
            {
              i = LoginActivity.PhoneView.this.countriesArray.indexOf(localObject1);
              if (i != -1)
              {
                LoginActivity.PhoneView.access$1102(LoginActivity.PhoneView.this, true);
                LoginActivity.PhoneView.this.countryButton.setText((CharSequence)LoginActivity.PhoneView.this.countriesArray.get(i));
                localObject1 = (String)LoginActivity.PhoneView.this.phoneFormatMap.get(localObject2);
                localObject2 = LoginActivity.PhoneView.this.phoneField;
                if (localObject1 != null)
                {
                  localObject1 = ((String)localObject1).replace('X', '–');
                  label447:
                  ((HintEditText)localObject2).setHintText((String)localObject1);
                  LoginActivity.PhoneView.access$802(LoginActivity.PhoneView.this, 0);
                }
              }
            }
            for (;;)
            {
              if (j == 0) {
                LoginActivity.PhoneView.this.codeField.setSelection(LoginActivity.PhoneView.this.codeField.getText().length());
              }
              if (paramAnonymousEditable == null) {
                break;
              }
              LoginActivity.PhoneView.this.phoneField.requestFocus();
              LoginActivity.PhoneView.this.phoneField.setText(paramAnonymousEditable);
              LoginActivity.PhoneView.this.phoneField.setSelection(LoginActivity.PhoneView.this.phoneField.length());
              break;
              label541:
              j -= 1;
              break label139;
              localObject1 = null;
              break label447;
              LoginActivity.PhoneView.this.countryButton.setText(LocaleController.getString("WrongCountry", 2131166406));
              LoginActivity.PhoneView.this.phoneField.setHintText(null);
              LoginActivity.PhoneView.access$802(LoginActivity.PhoneView.this, 2);
              continue;
              LoginActivity.PhoneView.this.countryButton.setText(LocaleController.getString("WrongCountry", 2131166406));
              LoginActivity.PhoneView.this.phoneField.setHintText(null);
              LoginActivity.PhoneView.access$802(LoginActivity.PhoneView.this, 2);
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
              LoginActivity.PhoneView.this.phoneField.requestFocus();
              LoginActivity.PhoneView.this.phoneField.setSelection(LoginActivity.PhoneView.this.phoneField.length());
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
            if (LoginActivity.PhoneView.this.ignoreOnPhoneChange) {
              return;
            }
            int j = LoginActivity.PhoneView.this.phoneField.getSelectionStart();
            Object localObject = LoginActivity.PhoneView.this.phoneField.getText().toString();
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
            LoginActivity.PhoneView.access$1302(LoginActivity.PhoneView.this, true);
            paramAnonymousEditable = LoginActivity.PhoneView.this.phoneField.getHintText();
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
            LoginActivity.PhoneView.this.phoneField.setText((CharSequence)localObject);
            if (j >= 0)
            {
              paramAnonymousEditable = LoginActivity.PhoneView.this.phoneField;
              if (j > LoginActivity.PhoneView.this.phoneField.length()) {
                break label404;
              }
            }
            for (;;)
            {
              paramAnonymousEditable.setSelection(j);
              LoginActivity.PhoneView.this.phoneField.onTextChange();
              LoginActivity.PhoneView.access$1302(LoginActivity.PhoneView.this, false);
              return;
              label404:
              j = LoginActivity.PhoneView.this.phoneField.length();
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
              LoginActivity.PhoneView.this.onNextPressed();
              return true;
            }
            return false;
          }
        });
        paramContext = new TextView(paramContext);
        paramContext.setText(LocaleController.getString("StartText", 2131166309));
        paramContext.setTextColor(-9079435);
        paramContext.setTextSize(1, 14.0F);
        if (!LocaleController.isRTL) {
          break label1081;
        }
        i = 5;
        label711:
        paramContext.setGravity(i);
        paramContext.setLineSpacing(AndroidUtilities.dp(2.0F), 1.0F);
        if (!LocaleController.isRTL) {
          break label1086;
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
            FileLog.e("tmessages", LoginActivity.this);
            this$1 = paramContext;
          }
          this.codeField.requestFocus();
        }
        if (LoginActivity.this != null)
        {
          this$1 = (String)((HashMap)localObject1).get(LoginActivity.this);
          if ((LoginActivity.this != null) && (this.countriesArray.indexOf(LoginActivity.this) != -1))
          {
            this.codeField.setText((CharSequence)this.countriesMap.get(LoginActivity.this));
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
          break label1111;
        }
        this.phoneField.requestFocus();
        this.phoneField.setSelection(this.phoneField.length());
        return;
        i = 3;
        break;
        label1081:
        i = 3;
        break label711;
        label1086:
        i = 3;
        break label734;
        paramContext.close();
      }
      label1111:
    }
    
    public String getHeaderName()
    {
      return LocaleController.getString("YourPhone", 2131166432);
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
      if ((LoginActivity.this.getParentActivity() == null) || (this.nextPressed)) {
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
        if (LoginActivity.this.getParentActivity().checkSelfPermission("android.permission.READ_PHONE_STATE") != 0) {
          break label362;
        }
        i = 1;
        if (LoginActivity.this.getParentActivity().checkSelfPermission("android.permission.RECEIVE_SMS") != 0) {
          break label367;
        }
        m = 1;
        label106:
        k = i;
        if (!LoginActivity.this.checkPermissions) {
          break label453;
        }
        LoginActivity.this.permissionsItems.clear();
        if (i == 0) {
          LoginActivity.this.permissionsItems.add("android.permission.READ_PHONE_STATE");
        }
        if (m == 0) {
          LoginActivity.this.permissionsItems.add("android.permission.RECEIVE_SMS");
        }
        k = i;
        if (LoginActivity.this.permissionsItems.isEmpty()) {
          break label453;
        }
        localObject1 = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
        if ((!((SharedPreferences)localObject1).getBoolean("firstlogin", true)) && (!LoginActivity.this.getParentActivity().shouldShowRequestPermissionRationale("android.permission.READ_PHONE_STATE")) && (!LoginActivity.this.getParentActivity().shouldShowRequestPermissionRationale("android.permission.RECEIVE_SMS"))) {
          break label414;
        }
        ((SharedPreferences)localObject1).edit().putBoolean("firstlogin", false).commit();
        localObject1 = new AlertDialog.Builder(LoginActivity.this.getParentActivity());
        ((AlertDialog.Builder)localObject1).setTitle(LocaleController.getString("AppName", 2131165299));
        ((AlertDialog.Builder)localObject1).setPositiveButton(LocaleController.getString("OK", 2131166044), null);
        if (LoginActivity.this.permissionsItems.size() != 2) {
          break label373;
        }
        ((AlertDialog.Builder)localObject1).setMessage(LocaleController.getString("AllowReadCallAndSms", 2131165279));
      }
      for (;;)
      {
        LoginActivity.access$1602(LoginActivity.this, LoginActivity.this.showDialog(((AlertDialog.Builder)localObject1).create()));
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
      LoginActivity.this.getParentActivity().requestPermissions((String[])LoginActivity.this.permissionsItems.toArray(new String[LoginActivity.this.permissionsItems.size()]), 6);
      return;
      label453:
      if (this.countryState == 1)
      {
        LoginActivity.this.needShowAlert(LocaleController.getString("AppName", 2131165299), LocaleController.getString("ChooseCountry", 2131165505));
        return;
      }
      if ((this.countryState == 2) && (!BuildVars.DEBUG_VERSION) && (!this.codeField.getText().toString().equals("999")))
      {
        LoginActivity.this.needShowAlert(LocaleController.getString("AppName", 2131165299), LocaleController.getString("WrongCountry", 2131166406));
        return;
      }
      if (this.codeField.length() == 0)
      {
        LoginActivity.this.needShowAlert(LocaleController.getString("AppName", 2131165299), LocaleController.getString("InvalidPhoneNumber", 2131165758));
        return;
      }
      ConnectionsManager.getInstance().cleanup();
      final Object localObject1 = new TLRPC.TL_auth_sendCode();
      String str = PhoneFormat.stripExceptNumbers("" + this.codeField.getText() + this.phoneField.getText());
      ConnectionsManager.getInstance().applyCountryPortNumber(str);
      ((TLRPC.TL_auth_sendCode)localObject1).api_hash = BuildVars.APP_HASH;
      ((TLRPC.TL_auth_sendCode)localObject1).api_id = BuildVars.APP_ID;
      ((TLRPC.TL_auth_sendCode)localObject1).phone_number = str;
      boolean bool;
      if ((j != 0) && (k != 0)) {
        bool = true;
      }
      for (;;)
      {
        ((TLRPC.TL_auth_sendCode)localObject1).allow_flashcall = bool;
        if (((TLRPC.TL_auth_sendCode)localObject1).allow_flashcall) {}
        try
        {
          localObject2 = ((TelephonyManager)localObject2).getLine1Number();
          if ((localObject2 != null) && (((String)localObject2).length() != 0))
          {
            if (str.contains((CharSequence)localObject2)) {
              break label976;
            }
            if (((String)localObject2).contains(str))
            {
              break label976;
              ((TLRPC.TL_auth_sendCode)localObject1).current_number = bool;
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
              LoginActivity.this.needShowProgress();
              ConnectionsManager.getInstance().sendRequest((TLObject)localObject1, new RequestDelegate()
              {
                public void run(final TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
                {
                  AndroidUtilities.runOnUIThread(new Runnable()
                  {
                    public void run()
                    {
                      LoginActivity.PhoneView.access$1802(LoginActivity.PhoneView.this, false);
                      if (paramAnonymousTL_error == null) {
                        LoginActivity.this.fillNextCodeParams(LoginActivity.PhoneView.7.this.val$params, (TLRPC.TL_auth_sentCode)paramAnonymousTLObject);
                      }
                      for (;;)
                      {
                        LoginActivity.this.needHideProgress();
                        return;
                        if (paramAnonymousTL_error.text != null) {
                          if (paramAnonymousTL_error.text.contains("PHONE_NUMBER_INVALID")) {
                            LoginActivity.this.needShowInvalidAlert(LoginActivity.PhoneView.7.this.val$req.phone_number, false);
                          } else if (paramAnonymousTL_error.text.contains("PHONE_NUMBER_BANNED")) {
                            LoginActivity.this.needShowInvalidAlert(LoginActivity.PhoneView.7.this.val$req.phone_number, true);
                          } else if ((paramAnonymousTL_error.text.contains("PHONE_CODE_EMPTY")) || (paramAnonymousTL_error.text.contains("PHONE_CODE_INVALID"))) {
                            LoginActivity.this.needShowAlert(LocaleController.getString("AppName", 2131165299), LocaleController.getString("InvalidCode", 2131165754));
                          } else if (paramAnonymousTL_error.text.contains("PHONE_CODE_EXPIRED")) {
                            LoginActivity.this.needShowAlert(LocaleController.getString("AppName", 2131165299), LocaleController.getString("CodeExpired", 2131165517));
                          } else if (paramAnonymousTL_error.text.startsWith("FLOOD_WAIT")) {
                            LoginActivity.this.needShowAlert(LocaleController.getString("AppName", 2131165299), LocaleController.getString("FloodWait", 2131165639));
                          } else if (paramAnonymousTL_error.code != 64536) {
                            LoginActivity.this.needShowAlert(LocaleController.getString("AppName", 2131165299), paramAnonymousTL_error.text);
                          }
                        }
                      }
                    }
                  });
                }
              }, 27);
              return;
              bool = false;
              break;
              bool = false;
              continue;
              localException1 = localException1;
              ((TLRPC.TL_auth_sendCode)localObject1).allow_flashcall = false;
              FileLog.e("tmessages", localException1);
            }
            catch (Exception localException2)
            {
              FileLog.e("tmessages", localException2);
              localException1.putString("ephone", "+" + str);
              continue;
            }
            label976:
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
    
    public void restoreStateParams(Bundle paramBundle)
    {
      String str = paramBundle.getString("phoneview_code");
      if (str != null) {
        this.codeField.setText(str);
      }
      paramBundle = paramBundle.getString("phoneview_phone");
      if (paramBundle != null) {
        this.phoneField.setText(paramBundle);
      }
    }
    
    public void saveStateParams(Bundle paramBundle)
    {
      String str = this.codeField.getText().toString();
      if (str.length() != 0) {
        paramBundle.putString("phoneview_code", str);
      }
      str = this.phoneField.getText().toString();
      if (str.length() != 0) {
        paramBundle.putString("phoneview_phone", str);
      }
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


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/LoginActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */