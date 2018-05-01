package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
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
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
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
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.EditTextBoldCursor;
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
  private AlertDialog progressDialog;
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
    for (;;)
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
      label106:
      if (paramTL_auth_sentCode.timeout == 0) {
        paramTL_auth_sentCode.timeout = 60;
      }
      paramBundle.putInt("timeout", paramTL_auth_sentCode.timeout * 1000);
      if ((paramTL_auth_sentCode.type instanceof TLRPC.TL_auth_sentCodeTypeCall))
      {
        paramBundle.putInt("type", 4);
        paramBundle.putInt("length", paramTL_auth_sentCode.type.length);
        setPage(4, true, paramBundle, false);
      }
      else if ((paramTL_auth_sentCode.type instanceof TLRPC.TL_auth_sentCodeTypeFlashCall))
      {
        paramBundle.putInt("type", 3);
        paramBundle.putString("pattern", paramTL_auth_sentCode.type.pattern);
        setPage(3, true, paramBundle, false);
      }
      else if ((paramTL_auth_sentCode.type instanceof TLRPC.TL_auth_sentCodeTypeSms))
      {
        paramBundle.putInt("type", 2);
        paramBundle.putInt("length", paramTL_auth_sentCode.type.length);
        setPage(2, true, paramBundle, false);
      }
    }
  }
  
  public View createView(Context paramContext)
  {
    this.actionBar.setTitle(LocaleController.getString("AppName", NUM));
    this.actionBar.setBackButtonImage(NUM);
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (paramAnonymousInt == 1) {
          ChangePhoneActivity.this.views[ChangePhoneActivity.this.currentViewNum].onNextPressed();
        }
        for (;;)
        {
          return;
          if (paramAnonymousInt == -1) {
            ChangePhoneActivity.this.finishFragment();
          }
        }
      }
    });
    this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, NUM, AndroidUtilities.dp(56.0F));
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
      label215:
      float f1;
      label239:
      float f2;
      if (i == 0)
      {
        j = 0;
        paramContext.setVisibility(j);
        paramContext = this.views[i];
        if (i != 0) {
          break label295;
        }
        f1 = -2.0F;
        if (!AndroidUtilities.isTablet()) {
          break label303;
        }
        f2 = 26.0F;
        label250:
        if (!AndroidUtilities.isTablet()) {
          break label311;
        }
      }
      label295:
      label303:
      label311:
      for (float f3 = 26.0F;; f3 = 18.0F)
      {
        localFrameLayout.addView(paramContext, LayoutHelper.createFrame(-1, f1, 51, f2, 30.0F, f3, 0.0F));
        i++;
        break;
        j = 8;
        break label215;
        f1 = -1.0F;
        break label239;
        f2 = 18.0F;
        break label250;
      }
    }
    this.actionBar.setTitle(this.views[0].getHeaderName());
    return this.fragmentView;
  }
  
  public ThemeDescription[] getThemeDescriptions()
  {
    PhoneView localPhoneView = (PhoneView)this.views[0];
    LoginActivitySmsView localLoginActivitySmsView1 = (LoginActivitySmsView)this.views[1];
    LoginActivitySmsView localLoginActivitySmsView2 = (LoginActivitySmsView)this.views[2];
    LoginActivitySmsView localLoginActivitySmsView3 = (LoginActivitySmsView)this.views[3];
    LoginActivitySmsView localLoginActivitySmsView4 = (LoginActivitySmsView)this.views[4];
    return new ThemeDescription[] { new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault"), new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector"), new ThemeDescription(localPhoneView.countryButton, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(localPhoneView.view, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhiteGrayLine"), new ThemeDescription(localPhoneView.textView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(localPhoneView.codeField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(localPhoneView.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField"), new ThemeDescription(localPhoneView.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"), new ThemeDescription(localPhoneView.phoneField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(localPhoneView.phoneField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"), new ThemeDescription(localPhoneView.phoneField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField"), new ThemeDescription(localPhoneView.phoneField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"), new ThemeDescription(localPhoneView.textView2, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"), new ThemeDescription(localLoginActivitySmsView1.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"), new ThemeDescription(localLoginActivitySmsView1.codeField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(localLoginActivitySmsView1.codeField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"), new ThemeDescription(localLoginActivitySmsView1.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField"), new ThemeDescription(localLoginActivitySmsView1.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"), new ThemeDescription(localLoginActivitySmsView1.timeText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"), new ThemeDescription(localLoginActivitySmsView1.problemText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlueText4"), new ThemeDescription(localLoginActivitySmsView1.wrongNumber, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlueText4"), new ThemeDescription(localLoginActivitySmsView1.progressView, 0, new Class[] { ProgressView.class }, new String[] { "paint" }, null, null, null, "login_progressInner"), new ThemeDescription(localLoginActivitySmsView1.progressView, 0, new Class[] { ProgressView.class }, new String[] { "paint" }, null, null, null, "login_progressOuter"), new ThemeDescription(localLoginActivitySmsView2.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"), new ThemeDescription(localLoginActivitySmsView2.codeField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(localLoginActivitySmsView2.codeField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"), new ThemeDescription(localLoginActivitySmsView2.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField"), new ThemeDescription(localLoginActivitySmsView2.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"), new ThemeDescription(localLoginActivitySmsView2.timeText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"), new ThemeDescription(localLoginActivitySmsView2.problemText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlueText4"), new ThemeDescription(localLoginActivitySmsView2.wrongNumber, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlueText4"), new ThemeDescription(localLoginActivitySmsView2.progressView, 0, new Class[] { ProgressView.class }, new String[] { "paint" }, null, null, null, "login_progressInner"), new ThemeDescription(localLoginActivitySmsView2.progressView, 0, new Class[] { ProgressView.class }, new String[] { "paint" }, null, null, null, "login_progressOuter"), new ThemeDescription(localLoginActivitySmsView3.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"), new ThemeDescription(localLoginActivitySmsView3.codeField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(localLoginActivitySmsView3.codeField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"), new ThemeDescription(localLoginActivitySmsView3.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField"), new ThemeDescription(localLoginActivitySmsView3.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"), new ThemeDescription(localLoginActivitySmsView3.timeText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"), new ThemeDescription(localLoginActivitySmsView3.problemText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlueText4"), new ThemeDescription(localLoginActivitySmsView3.wrongNumber, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlueText4"), new ThemeDescription(localLoginActivitySmsView3.progressView, 0, new Class[] { ProgressView.class }, new String[] { "paint" }, null, null, null, "login_progressInner"), new ThemeDescription(localLoginActivitySmsView3.progressView, 0, new Class[] { ProgressView.class }, new String[] { "paint" }, null, null, null, "login_progressOuter"), new ThemeDescription(localLoginActivitySmsView4.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"), new ThemeDescription(localLoginActivitySmsView4.codeField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(localLoginActivitySmsView4.codeField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"), new ThemeDescription(localLoginActivitySmsView1.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField"), new ThemeDescription(localLoginActivitySmsView4.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"), new ThemeDescription(localLoginActivitySmsView4.timeText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"), new ThemeDescription(localLoginActivitySmsView4.problemText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlueText4"), new ThemeDescription(localLoginActivitySmsView4.wrongNumber, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlueText4"), new ThemeDescription(localLoginActivitySmsView4.progressView, 0, new Class[] { ProgressView.class }, new String[] { "paint" }, null, null, null, "login_progressInner"), new ThemeDescription(localLoginActivitySmsView4.progressView, 0, new Class[] { ProgressView.class }, new String[] { "paint" }, null, null, null, "login_progressOuter") };
  }
  
  public void needHideProgress()
  {
    if (this.progressDialog == null) {}
    for (;;)
    {
      return;
      try
      {
        this.progressDialog.dismiss();
        this.progressDialog = null;
      }
      catch (Exception localException)
      {
        for (;;)
        {
          FileLog.e(localException);
        }
      }
    }
  }
  
  public void needShowProgress()
  {
    if ((getParentActivity() == null) || (getParentActivity().isFinishing()) || (this.progressDialog != null)) {}
    for (;;)
    {
      return;
      this.progressDialog = new AlertDialog(getParentActivity(), 1);
      this.progressDialog.setMessage(LocaleController.getString("Loading", NUM));
      this.progressDialog.setCanceledOnTouchOutside(false);
      this.progressDialog.setCancelable(false);
      this.progressDialog.show();
    }
  }
  
  public boolean onBackPressed()
  {
    boolean bool1 = true;
    if (this.currentViewNum == 0) {
      for (int i = 0;; i++)
      {
        bool2 = bool1;
        if (i >= this.views.length) {
          break;
        }
        if (this.views[i] != null) {
          this.views[i].onDestroyActivity();
        }
      }
    }
    this.views[this.currentViewNum].onBackPressed();
    setPage(0, true, null, true);
    boolean bool2 = false;
    return bool2;
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
    for (int i = 0; i < this.views.length; i++) {
      if (this.views[i] != null) {
        this.views[i].onDestroyActivity();
      }
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
        FileLog.e(localException);
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
      localSlideView2.setParams(paramBundle, false);
      this.actionBar.setTitle(localSlideView2.getHeaderName());
      localSlideView2.onShow();
      if (!paramBoolean2) {
        break label211;
      }
      f = -AndroidUtilities.displaySize.x;
      label77:
      localSlideView2.setX(f);
      paramBundle = new AnimatorSet();
      paramBundle.setInterpolator(new AccelerateDecelerateInterpolator());
      paramBundle.setDuration(300L);
      if (!paramBoolean2) {
        break label223;
      }
    }
    label211:
    label223:
    for (float f = AndroidUtilities.displaySize.x;; f = -AndroidUtilities.displaySize.x)
    {
      paramBundle.playTogether(new Animator[] { ObjectAnimator.ofFloat(localSlideView1, "translationX", new float[] { f }), ObjectAnimator.ofFloat(localSlideView2, "translationX", new float[] { 0.0F }) });
      paramBundle.addListener(new AnimatorListenerAdapter()
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
      break label77;
    }
  }
  
  public class LoginActivitySmsView
    extends SlideView
    implements NotificationCenter.NotificationCenterDelegate
  {
    private EditTextBoldCursor codeField;
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
    private ChangePhoneActivity.ProgressView progressView;
    private String requestPhone;
    private volatile int time = 60000;
    private TextView timeText;
    private Timer timeTimer;
    private int timeout;
    private final Object timerSync = new Object();
    private boolean waitingForEvent;
    private TextView wrongNumber;
    
    public LoginActivitySmsView(Context paramContext, int paramInt)
    {
      super();
      this.currentType = paramInt;
      setOrientation(1);
      this.confirmTextView = new TextView(paramContext);
      this.confirmTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
      this.confirmTextView.setTextSize(1, 14.0F);
      Object localObject1 = this.confirmTextView;
      Object localObject2;
      if (LocaleController.isRTL)
      {
        paramInt = 5;
        ((TextView)localObject1).setGravity(paramInt);
        this.confirmTextView.setLineSpacing(AndroidUtilities.dp(2.0F), 1.0F);
        if (this.currentType != 3) {
          break label1032;
        }
        localObject1 = new FrameLayout(paramContext);
        localObject2 = new ImageView(paramContext);
        ((ImageView)localObject2).setImageResource(NUM);
        if (!LocaleController.isRTL) {
          break label966;
        }
        ((FrameLayout)localObject1).addView((View)localObject2, LayoutHelper.createFrame(64, 76.0F, 19, 2.0F, 2.0F, 0.0F, 0.0F));
        localObject2 = this.confirmTextView;
        if (!LocaleController.isRTL) {
          break label961;
        }
        paramInt = 5;
        label198:
        ((FrameLayout)localObject1).addView((View)localObject2, LayoutHelper.createFrame(-1, -2.0F, paramInt, 82.0F, 0.0F, 0.0F, 0.0F));
        if (!LocaleController.isRTL) {
          break label1027;
        }
        paramInt = 5;
        label225:
        addView((View)localObject1, LayoutHelper.createLinear(-2, -2, paramInt));
        this.codeField = new EditTextBoldCursor(paramContext);
        this.codeField.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.codeField.setHint(LocaleController.getString("Code", NUM));
        this.codeField.setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.codeField.setCursorSize(AndroidUtilities.dp(20.0F));
        this.codeField.setCursorWidth(1.5F);
        this.codeField.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
        this.codeField.setBackgroundDrawable(Theme.createEditTextDrawable(paramContext, false));
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
            for (;;)
            {
              return;
              if ((ChangePhoneActivity.LoginActivitySmsView.this.length != 0) && (ChangePhoneActivity.LoginActivitySmsView.this.codeField.length() == ChangePhoneActivity.LoginActivitySmsView.this.length)) {
                ChangePhoneActivity.LoginActivitySmsView.this.onNextPressed();
              }
            }
          }
          
          public void beforeTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
          
          public void onTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
        });
        this.codeField.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
          public boolean onEditorAction(TextView paramAnonymousTextView, int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
          {
            if (paramAnonymousInt == 5) {
              ChangePhoneActivity.LoginActivitySmsView.this.onNextPressed();
            }
            for (boolean bool = true;; bool = false) {
              return bool;
            }
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
        this.timeText.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
        this.timeText.setLineSpacing(AndroidUtilities.dp(2.0F), 1.0F);
        localObject1 = this.timeText;
        if (!LocaleController.isRTL) {
          break label1068;
        }
        paramInt = 5;
        label526:
        ((TextView)localObject1).setGravity(paramInt);
        localObject1 = this.timeText;
        if (!LocaleController.isRTL) {
          break label1073;
        }
        paramInt = 5;
        label546:
        addView((View)localObject1, LayoutHelper.createLinear(-2, -2, paramInt, 0, 30, 0, 0));
        if (this.currentType == 3)
        {
          this.progressView = new ChangePhoneActivity.ProgressView(ChangePhoneActivity.this, paramContext);
          addView(this.progressView, LayoutHelper.createLinear(-1, 3, 0.0F, 12.0F, 0.0F, 0.0F));
        }
        this.problemText = new TextView(paramContext);
        this.problemText.setText(LocaleController.getString("DidNotGetTheCode", NUM));
        localObject1 = this.problemText;
        if (!LocaleController.isRTL) {
          break label1078;
        }
        paramInt = 5;
        label647:
        ((TextView)localObject1).setGravity(paramInt);
        this.problemText.setTextSize(1, 14.0F);
        this.problemText.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText4"));
        this.problemText.setLineSpacing(AndroidUtilities.dp(2.0F), 1.0F);
        this.problemText.setPadding(0, AndroidUtilities.dp(2.0F), 0, AndroidUtilities.dp(12.0F));
        localObject1 = this.problemText;
        if (!LocaleController.isRTL) {
          break label1083;
        }
        paramInt = 5;
        label722:
        addView((View)localObject1, LayoutHelper.createLinear(-2, -2, paramInt, 0, 20, 0, 0));
        this.problemText.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            if (ChangePhoneActivity.LoginActivitySmsView.this.nextPressed) {}
            for (;;)
            {
              return;
              if ((ChangePhoneActivity.LoginActivitySmsView.this.nextType != 0) && (ChangePhoneActivity.LoginActivitySmsView.this.nextType != 4)) {
                ChangePhoneActivity.LoginActivitySmsView.this.resendCode();
              } else {
                try
                {
                  paramAnonymousView = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
                  paramAnonymousView = String.format(Locale.US, "%s (%d)", new Object[] { paramAnonymousView.versionName, Integer.valueOf(paramAnonymousView.versionCode) });
                  Intent localIntent = new android/content/Intent;
                  localIntent.<init>("android.intent.action.SEND");
                  localIntent.setType("message/rfc822");
                  localIntent.putExtra("android.intent.extra.EMAIL", new String[] { "sms@stel.com" });
                  StringBuilder localStringBuilder = new java/lang/StringBuilder;
                  localStringBuilder.<init>();
                  localIntent.putExtra("android.intent.extra.SUBJECT", "Android registration/login issue " + paramAnonymousView + " " + ChangePhoneActivity.LoginActivitySmsView.this.emailPhone);
                  localStringBuilder = new java/lang/StringBuilder;
                  localStringBuilder.<init>();
                  localIntent.putExtra("android.intent.extra.TEXT", "Phone: " + ChangePhoneActivity.LoginActivitySmsView.this.requestPhone + "\nApp version: " + paramAnonymousView + "\nOS version: SDK " + Build.VERSION.SDK_INT + "\nDevice Name: " + Build.MANUFACTURER + Build.MODEL + "\nLocale: " + Locale.getDefault() + "\nError: " + ChangePhoneActivity.LoginActivitySmsView.this.lastError);
                  ChangePhoneActivity.LoginActivitySmsView.this.getContext().startActivity(Intent.createChooser(localIntent, "Send email..."));
                }
                catch (Exception paramAnonymousView)
                {
                  AlertsCreator.showSimpleAlert(ChangePhoneActivity.this, LocaleController.getString("NoMailInstalled", NUM));
                }
              }
            }
          }
        });
        localObject1 = new LinearLayout(paramContext);
        if (!LocaleController.isRTL) {
          break label1088;
        }
        paramInt = 5;
        label775:
        ((LinearLayout)localObject1).setGravity(paramInt | 0x10);
        if (!LocaleController.isRTL) {
          break label1093;
        }
        paramInt = 5;
        label792:
        addView((View)localObject1, LayoutHelper.createLinear(-1, -1, paramInt));
        this.wrongNumber = new TextView(paramContext);
        paramContext = this.wrongNumber;
        if (!LocaleController.isRTL) {
          break label1098;
        }
        paramInt = 5;
        label829:
        paramContext.setGravity(paramInt | 0x1);
        this.wrongNumber.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText4"));
        this.wrongNumber.setTextSize(1, 14.0F);
        this.wrongNumber.setLineSpacing(AndroidUtilities.dp(2.0F), 1.0F);
        this.wrongNumber.setPadding(0, AndroidUtilities.dp(24.0F), 0, 0);
        paramContext = this.wrongNumber;
        if (!LocaleController.isRTL) {
          break label1103;
        }
      }
      label961:
      label966:
      label1027:
      label1032:
      label1068:
      label1073:
      label1078:
      label1083:
      label1088:
      label1093:
      label1098:
      label1103:
      for (paramInt = 5;; paramInt = 3)
      {
        ((LinearLayout)localObject1).addView(paramContext, LayoutHelper.createLinear(-2, -2, paramInt | 0x50, 0, 0, 0, 10));
        this.wrongNumber.setText(LocaleController.getString("WrongNumber", NUM));
        this.wrongNumber.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            paramAnonymousView = new TLRPC.TL_auth_cancelCode();
            paramAnonymousView.phone_number = ChangePhoneActivity.LoginActivitySmsView.this.requestPhone;
            paramAnonymousView.phone_code_hash = ChangePhoneActivity.LoginActivitySmsView.this.phoneHash;
            ConnectionsManager.getInstance(ChangePhoneActivity.this.currentAccount).sendRequest(paramAnonymousView, new RequestDelegate()
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
        break label198;
        TextView localTextView = this.confirmTextView;
        if (LocaleController.isRTL) {}
        for (paramInt = 5;; paramInt = 3)
        {
          ((FrameLayout)localObject1).addView(localTextView, LayoutHelper.createFrame(-1, -2.0F, paramInt, 0.0F, 0.0F, 82.0F, 0.0F));
          ((FrameLayout)localObject1).addView((View)localObject2, LayoutHelper.createFrame(64, 76.0F, 21, 0.0F, 2.0F, 0.0F, 2.0F));
          break;
        }
        paramInt = 3;
        break label225;
        localObject1 = this.confirmTextView;
        if (LocaleController.isRTL) {}
        for (paramInt = 5;; paramInt = 3)
        {
          addView((View)localObject1, LayoutHelper.createLinear(-2, -2, paramInt));
          break;
        }
        paramInt = 3;
        break label526;
        paramInt = 3;
        break label546;
        paramInt = 3;
        break label647;
        paramInt = 3;
        break label722;
        paramInt = 3;
        break label775;
        paramInt = 3;
        break label792;
        paramInt = 3;
        break label829;
      }
    }
    
    private void createCodeTimer()
    {
      if (this.codeTimer != null) {}
      for (;;)
      {
        return;
        this.codeTime = 15000;
        this.codeTimer = new Timer();
        this.lastCodeTime = System.currentTimeMillis();
        this.codeTimer.schedule(new TimerTask()
        {
          public void run()
          {
            double d1 = System.currentTimeMillis();
            double d2 = ChangePhoneActivity.LoginActivitySmsView.this.lastCodeTime;
            ChangePhoneActivity.LoginActivitySmsView.access$3302(ChangePhoneActivity.LoginActivitySmsView.this, (int)(ChangePhoneActivity.LoginActivitySmsView.this.codeTime - (d1 - d2)));
            ChangePhoneActivity.LoginActivitySmsView.access$3202(ChangePhoneActivity.LoginActivitySmsView.this, d1);
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
    }
    
    private void createTimer()
    {
      if (this.timeTimer != null) {}
      for (;;)
      {
        return;
        this.timeTimer = new Timer();
        this.timeTimer.schedule(new TimerTask()
        {
          public void run()
          {
            if (ChangePhoneActivity.LoginActivitySmsView.this.timeTimer == null) {}
            for (;;)
            {
              return;
              double d1 = System.currentTimeMillis();
              double d2 = ChangePhoneActivity.LoginActivitySmsView.this.lastCurrentTime;
              ChangePhoneActivity.LoginActivitySmsView.access$3802(ChangePhoneActivity.LoginActivitySmsView.this, (int)(ChangePhoneActivity.LoginActivitySmsView.this.time - (d1 - d2)));
              ChangePhoneActivity.LoginActivitySmsView.access$3702(ChangePhoneActivity.LoginActivitySmsView.this, d1);
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
                      ChangePhoneActivity.LoginActivitySmsView.this.timeText.setText(LocaleController.formatString("CallText", NUM, new Object[] { Integer.valueOf(i), Integer.valueOf(j) }));
                    }
                  }
                  label515:
                  for (;;)
                  {
                    if (ChangePhoneActivity.LoginActivitySmsView.this.progressView != null) {
                      ChangePhoneActivity.LoginActivitySmsView.this.progressView.setProgress(1.0F - ChangePhoneActivity.LoginActivitySmsView.this.time / ChangePhoneActivity.LoginActivitySmsView.this.timeout);
                    }
                    for (;;)
                    {
                      return;
                      if (ChangePhoneActivity.LoginActivitySmsView.this.nextType != 2) {
                        break label515;
                      }
                      ChangePhoneActivity.LoginActivitySmsView.this.timeText.setText(LocaleController.formatString("SmsText", NUM, new Object[] { Integer.valueOf(i), Integer.valueOf(j) }));
                      break;
                      if (ChangePhoneActivity.LoginActivitySmsView.this.progressView != null) {
                        ChangePhoneActivity.LoginActivitySmsView.this.progressView.setProgress(1.0F);
                      }
                      ChangePhoneActivity.LoginActivitySmsView.this.destroyTimer();
                      if (ChangePhoneActivity.LoginActivitySmsView.this.currentType == 3)
                      {
                        AndroidUtilities.setWaitingForCall(false);
                        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveCall);
                        ChangePhoneActivity.LoginActivitySmsView.access$4402(ChangePhoneActivity.LoginActivitySmsView.this, false);
                        ChangePhoneActivity.LoginActivitySmsView.this.destroyCodeTimer();
                        ChangePhoneActivity.LoginActivitySmsView.this.resendCode();
                      }
                      else if (ChangePhoneActivity.LoginActivitySmsView.this.currentType == 2)
                      {
                        if (ChangePhoneActivity.LoginActivitySmsView.this.nextType == 4)
                        {
                          ChangePhoneActivity.LoginActivitySmsView.this.timeText.setText(LocaleController.getString("Calling", NUM));
                          ChangePhoneActivity.LoginActivitySmsView.this.createCodeTimer();
                          TLRPC.TL_auth_resendCode localTL_auth_resendCode = new TLRPC.TL_auth_resendCode();
                          localTL_auth_resendCode.phone_number = ChangePhoneActivity.LoginActivitySmsView.this.requestPhone;
                          localTL_auth_resendCode.phone_code_hash = ChangePhoneActivity.LoginActivitySmsView.this.phoneHash;
                          ConnectionsManager.getInstance(ChangePhoneActivity.this.currentAccount).sendRequest(localTL_auth_resendCode, new RequestDelegate()
                          {
                            public void run(TLObject paramAnonymous3TLObject, final TLRPC.TL_error paramAnonymous3TL_error)
                            {
                              if ((paramAnonymous3TL_error != null) && (paramAnonymous3TL_error.text != null)) {
                                AndroidUtilities.runOnUIThread(new Runnable()
                                {
                                  public void run()
                                  {
                                    ChangePhoneActivity.LoginActivitySmsView.access$2702(ChangePhoneActivity.LoginActivitySmsView.this, paramAnonymous3TL_error.text);
                                  }
                                });
                              }
                            }
                          }, 2);
                        }
                        else if (ChangePhoneActivity.LoginActivitySmsView.this.nextType == 3)
                        {
                          AndroidUtilities.setWaitingForSms(false);
                          NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveSmsCode);
                          ChangePhoneActivity.LoginActivitySmsView.access$4402(ChangePhoneActivity.LoginActivitySmsView.this, false);
                          ChangePhoneActivity.LoginActivitySmsView.this.destroyCodeTimer();
                          ChangePhoneActivity.LoginActivitySmsView.this.resendCode();
                        }
                      }
                    }
                  }
                }
              });
            }
          }
        }, 0L, 1000L);
      }
    }
    
    /* Error */
    private void destroyCodeTimer()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 93	org/telegram/ui/ChangePhoneActivity$LoginActivitySmsView:timerSync	Ljava/lang/Object;
      //   4: astore_1
      //   5: aload_1
      //   6: monitorenter
      //   7: aload_0
      //   8: getfield 389	org/telegram/ui/ChangePhoneActivity$LoginActivitySmsView:codeTimer	Ljava/util/Timer;
      //   11: ifnull +15 -> 26
      //   14: aload_0
      //   15: getfield 389	org/telegram/ui/ChangePhoneActivity$LoginActivitySmsView:codeTimer	Ljava/util/Timer;
      //   18: invokevirtual 412	java/util/Timer:cancel	()V
      //   21: aload_0
      //   22: aconst_null
      //   23: putfield 389	org/telegram/ui/ChangePhoneActivity$LoginActivitySmsView:codeTimer	Ljava/util/Timer;
      //   26: aload_1
      //   27: monitorexit
      //   28: return
      //   29: astore_2
      //   30: aload_1
      //   31: monitorexit
      //   32: aload_2
      //   33: athrow
      //   34: astore_1
      //   35: aload_1
      //   36: invokestatic 418	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
      //   39: goto -11 -> 28
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	42	0	this	LoginActivitySmsView
      //   34	2	1	localException	Exception
      //   29	4	2	localObject2	Object
      // Exception table:
      //   from	to	target	type
      //   7	26	29	finally
      //   26	28	29	finally
      //   30	32	29	finally
      //   0	7	34	java/lang/Exception
      //   32	34	34	java/lang/Exception
    }
    
    /* Error */
    private void destroyTimer()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 93	org/telegram/ui/ChangePhoneActivity$LoginActivitySmsView:timerSync	Ljava/lang/Object;
      //   4: astore_1
      //   5: aload_1
      //   6: monitorenter
      //   7: aload_0
      //   8: getfield 357	org/telegram/ui/ChangePhoneActivity$LoginActivitySmsView:timeTimer	Ljava/util/Timer;
      //   11: ifnull +15 -> 26
      //   14: aload_0
      //   15: getfield 357	org/telegram/ui/ChangePhoneActivity$LoginActivitySmsView:timeTimer	Ljava/util/Timer;
      //   18: invokevirtual 412	java/util/Timer:cancel	()V
      //   21: aload_0
      //   22: aconst_null
      //   23: putfield 357	org/telegram/ui/ChangePhoneActivity$LoginActivitySmsView:timeTimer	Ljava/util/Timer;
      //   26: aload_1
      //   27: monitorexit
      //   28: return
      //   29: astore_2
      //   30: aload_1
      //   31: monitorexit
      //   32: aload_2
      //   33: athrow
      //   34: astore_1
      //   35: aload_1
      //   36: invokestatic 418	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
      //   39: goto -11 -> 28
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	42	0	this	LoginActivitySmsView
      //   34	2	1	localException	Exception
      //   29	4	2	localObject2	Object
      // Exception table:
      //   from	to	target	type
      //   7	26	29	finally
      //   26	28	29	finally
      //   30	32	29	finally
      //   0	7	34	java/lang/Exception
      //   32	34	34	java/lang/Exception
    }
    
    private void resendCode()
    {
      final Bundle localBundle = new Bundle();
      localBundle.putString("phone", this.phone);
      localBundle.putString("ephone", this.emailPhone);
      localBundle.putString("phoneFormated", this.requestPhone);
      this.nextPressed = true;
      ChangePhoneActivity.this.needShowProgress();
      final TLRPC.TL_auth_resendCode localTL_auth_resendCode = new TLRPC.TL_auth_resendCode();
      localTL_auth_resendCode.phone_number = this.requestPhone;
      localTL_auth_resendCode.phone_code_hash = this.phoneHash;
      ConnectionsManager.getInstance(ChangePhoneActivity.this.currentAccount).sendRequest(localTL_auth_resendCode, new RequestDelegate()
      {
        public void run(final TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
        {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              ChangePhoneActivity.LoginActivitySmsView.access$2202(ChangePhoneActivity.LoginActivitySmsView.this, false);
              if (paramAnonymousTL_error == null) {
                ChangePhoneActivity.this.fillNextCodeParams(ChangePhoneActivity.LoginActivitySmsView.5.this.val$params, (TLRPC.TL_auth_sentCode)paramAnonymousTLObject);
              }
              for (;;)
              {
                ChangePhoneActivity.this.needHideProgress();
                return;
                AlertsCreator.processError(ChangePhoneActivity.this.currentAccount, paramAnonymousTL_error, ChangePhoneActivity.this, ChangePhoneActivity.LoginActivitySmsView.5.this.val$req, new Object[0]);
                if (paramAnonymousTL_error.text.contains("PHONE_CODE_EXPIRED"))
                {
                  ChangePhoneActivity.LoginActivitySmsView.this.onBackPressed();
                  ChangePhoneActivity.this.setPage(0, true, null, true);
                }
              }
            }
          });
        }
      }, 2);
    }
    
    public void didReceivedNotification(int paramInt1, int paramInt2, Object... paramVarArgs)
    {
      if ((!this.waitingForEvent) || (this.codeField == null)) {}
      for (;;)
      {
        return;
        if (paramInt1 == NotificationCenter.didReceiveSmsCode)
        {
          this.ignoreOnTextChange = true;
          this.codeField.setText("" + paramVarArgs[0]);
          this.ignoreOnTextChange = false;
          onNextPressed();
        }
        else if (paramInt1 == NotificationCenter.didReceiveCall)
        {
          paramVarArgs = "" + paramVarArgs[0];
          if (AndroidUtilities.checkPhonePattern(this.pattern, paramVarArgs))
          {
            this.ignoreOnTextChange = true;
            this.codeField.setText(paramVarArgs);
            this.ignoreOnTextChange = false;
            onNextPressed();
          }
        }
      }
    }
    
    public String getHeaderName()
    {
      return LocaleController.getString("YourCode", NUM);
    }
    
    public void onBackPressed()
    {
      destroyTimer();
      destroyCodeTimer();
      this.currentParams = null;
      if (this.currentType == 2)
      {
        AndroidUtilities.setWaitingForSms(false);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveSmsCode);
      }
      for (;;)
      {
        this.waitingForEvent = false;
        return;
        if (this.currentType == 3)
        {
          AndroidUtilities.setWaitingForCall(false);
          NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveCall);
        }
      }
    }
    
    public void onCancelPressed()
    {
      this.nextPressed = false;
    }
    
    public void onDestroyActivity()
    {
      super.onDestroyActivity();
      if (this.currentType == 2)
      {
        AndroidUtilities.setWaitingForSms(false);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveSmsCode);
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
          NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveCall);
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
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveSmsCode);
      }
      for (;;)
      {
        this.waitingForEvent = false;
        final TLRPC.TL_account_changePhone localTL_account_changePhone = new TLRPC.TL_account_changePhone();
        localTL_account_changePhone.phone_number = this.requestPhone;
        localTL_account_changePhone.phone_code = this.codeField.getText().toString();
        localTL_account_changePhone.phone_code_hash = this.phoneHash;
        destroyTimer();
        ChangePhoneActivity.this.needShowProgress();
        ConnectionsManager.getInstance(ChangePhoneActivity.this.currentAccount).sendRequest(localTL_account_changePhone, new RequestDelegate()
        {
          public void run(final TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
          {
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                ChangePhoneActivity.this.needHideProgress();
                ChangePhoneActivity.LoginActivitySmsView.access$2202(ChangePhoneActivity.LoginActivitySmsView.this, false);
                if (paramAnonymousTL_error == null)
                {
                  TLRPC.User localUser = (TLRPC.User)paramAnonymousTLObject;
                  ChangePhoneActivity.LoginActivitySmsView.this.destroyTimer();
                  ChangePhoneActivity.LoginActivitySmsView.this.destroyCodeTimer();
                  UserConfig.getInstance(ChangePhoneActivity.this.currentAccount).setCurrentUser(localUser);
                  UserConfig.getInstance(ChangePhoneActivity.this.currentAccount).saveConfig(true);
                  ArrayList localArrayList = new ArrayList();
                  localArrayList.add(localUser);
                  MessagesStorage.getInstance(ChangePhoneActivity.this.currentAccount).putUsersAndChats(localArrayList, null, true, true);
                  MessagesController.getInstance(ChangePhoneActivity.this.currentAccount).putUser(localUser, false);
                  ChangePhoneActivity.this.finishFragment();
                  NotificationCenter.getInstance(ChangePhoneActivity.this.currentAccount).postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
                  return;
                }
                ChangePhoneActivity.LoginActivitySmsView.access$2702(ChangePhoneActivity.LoginActivitySmsView.this, paramAnonymousTL_error.text);
                if (((ChangePhoneActivity.LoginActivitySmsView.this.currentType == 3) && ((ChangePhoneActivity.LoginActivitySmsView.this.nextType == 4) || (ChangePhoneActivity.LoginActivitySmsView.this.nextType == 2))) || ((ChangePhoneActivity.LoginActivitySmsView.this.currentType == 2) && ((ChangePhoneActivity.LoginActivitySmsView.this.nextType == 4) || (ChangePhoneActivity.LoginActivitySmsView.this.nextType == 3)))) {
                  ChangePhoneActivity.LoginActivitySmsView.this.createTimer();
                }
                if (ChangePhoneActivity.LoginActivitySmsView.this.currentType == 2)
                {
                  AndroidUtilities.setWaitingForSms(true);
                  NotificationCenter.getGlobalInstance().addObserver(ChangePhoneActivity.LoginActivitySmsView.this, NotificationCenter.didReceiveSmsCode);
                }
                for (;;)
                {
                  ChangePhoneActivity.LoginActivitySmsView.access$4402(ChangePhoneActivity.LoginActivitySmsView.this, true);
                  if (ChangePhoneActivity.LoginActivitySmsView.this.currentType == 3) {
                    break;
                  }
                  AlertsCreator.processError(ChangePhoneActivity.this.currentAccount, paramAnonymousTL_error, ChangePhoneActivity.this, ChangePhoneActivity.LoginActivitySmsView.8.this.val$req, new Object[0]);
                  break;
                  if (ChangePhoneActivity.LoginActivitySmsView.this.currentType == 3)
                  {
                    AndroidUtilities.setWaitingForCall(true);
                    NotificationCenter.getGlobalInstance().addObserver(ChangePhoneActivity.LoginActivitySmsView.this, NotificationCenter.didReceiveCall);
                  }
                }
              }
            });
          }
        }, 2);
        break;
        if (this.currentType == 3)
        {
          AndroidUtilities.setWaitingForCall(false);
          NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveCall);
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
    
    public void setParams(Bundle paramBundle, boolean paramBoolean)
    {
      int i = 0;
      if (paramBundle == null) {}
      for (;;)
      {
        return;
        this.codeField.setText("");
        this.waitingForEvent = true;
        label43:
        int j;
        label192:
        label214:
        String str;
        if (this.currentType == 2)
        {
          AndroidUtilities.setWaitingForSms(true);
          NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didReceiveSmsCode);
          this.currentParams = paramBundle;
          this.phone = paramBundle.getString("phone");
          this.emailPhone = paramBundle.getString("ephone");
          this.requestPhone = paramBundle.getString("phoneFormated");
          this.phoneHash = paramBundle.getString("phoneHash");
          j = paramBundle.getInt("timeout");
          this.time = j;
          this.timeout = j;
          this.openTime = ((int)(System.currentTimeMillis() / 1000L));
          this.nextType = paramBundle.getInt("nextType");
          this.pattern = paramBundle.getString("pattern");
          this.length = paramBundle.getInt("length");
          if (this.length == 0) {
            break label363;
          }
          paramBundle = new InputFilter.LengthFilter(this.length);
          this.codeField.setFilters(new InputFilter[] { paramBundle });
          if (this.progressView != null)
          {
            paramBundle = this.progressView;
            if (this.nextType == 0) {
              break label377;
            }
            j = 0;
            paramBundle.setVisibility(j);
          }
          if (this.phone == null) {
            continue;
          }
          str = PhoneFormat.getInstance().format(this.phone);
          paramBundle = "";
          if (this.currentType != 1) {
            break label384;
          }
          paramBundle = AndroidUtilities.replaceTags(LocaleController.getString("SentAppCode", NUM));
          label263:
          this.confirmTextView.setText(paramBundle);
          if (this.currentType == 3) {
            break label492;
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
            break label502;
          }
          this.problemText.setVisibility(0);
          this.timeText.setVisibility(8);
          break;
          if (this.currentType != 3) {
            break label43;
          }
          AndroidUtilities.setWaitingForCall(true);
          NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didReceiveCall);
          break label43;
          label363:
          this.codeField.setFilters(new InputFilter[0]);
          break label192;
          label377:
          j = 8;
          break label214;
          label384:
          if (this.currentType == 2)
          {
            paramBundle = AndroidUtilities.replaceTags(LocaleController.formatString("SentSmsCode", NUM, new Object[] { LocaleController.addNbsp(str) }));
            break label263;
          }
          if (this.currentType == 3)
          {
            paramBundle = AndroidUtilities.replaceTags(LocaleController.formatString("SentCallCode", NUM, new Object[] { LocaleController.addNbsp(str) }));
            break label263;
          }
          if (this.currentType != 4) {
            break label263;
          }
          paramBundle = AndroidUtilities.replaceTags(LocaleController.formatString("SentCallOnly", NUM, new Object[] { LocaleController.addNbsp(str) }));
          break label263;
          label492:
          AndroidUtilities.hideKeyboard(this.codeField);
        }
        label502:
        if ((this.currentType == 3) && ((this.nextType == 4) || (this.nextType == 2)))
        {
          this.problemText.setVisibility(8);
          this.timeText.setVisibility(0);
          if (this.nextType == 4) {
            this.timeText.setText(LocaleController.formatString("CallText", NUM, new Object[] { Integer.valueOf(1), Integer.valueOf(0) }));
          }
          for (;;)
          {
            createTimer();
            break;
            if (this.nextType == 2) {
              this.timeText.setText(LocaleController.formatString("SmsText", NUM, new Object[] { Integer.valueOf(1), Integer.valueOf(0) }));
            }
          }
        }
        if ((this.currentType == 2) && ((this.nextType == 4) || (this.nextType == 3)))
        {
          this.timeText.setVisibility(0);
          this.timeText.setText(LocaleController.formatString("CallText", NUM, new Object[] { Integer.valueOf(2), Integer.valueOf(0) }));
          paramBundle = this.problemText;
          if (this.time < 1000) {}
          for (j = i;; j = 8)
          {
            paramBundle.setVisibility(j);
            createTimer();
            break;
          }
        }
        this.timeText.setVisibility(8);
        this.problemText.setVisibility(8);
        createCodeTimer();
      }
    }
  }
  
  public class PhoneView
    extends SlideView
    implements AdapterView.OnItemSelectedListener
  {
    private EditTextBoldCursor codeField;
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
    private TextView textView;
    private TextView textView2;
    private View view;
    
    public PhoneView(Context paramContext)
    {
      super();
      setOrientation(1);
      this.countryButton = new TextView(paramContext);
      this.countryButton.setTextSize(1, 18.0F);
      this.countryButton.setPadding(AndroidUtilities.dp(12.0F), AndroidUtilities.dp(10.0F), AndroidUtilities.dp(12.0F), 0);
      this.countryButton.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
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
        this.countryButton.setBackgroundResource(NUM);
        addView(this.countryButton, LayoutHelper.createLinear(-1, 36, 0.0F, 0.0F, 0.0F, 14.0F));
        this.countryButton.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            paramAnonymousView = new CountrySelectActivity(true);
            paramAnonymousView.setCountrySelectActivityDelegate(new CountrySelectActivity.CountrySelectActivityDelegate()
            {
              public void didSelectCountry(String paramAnonymous2String1, String paramAnonymous2String2)
              {
                ChangePhoneActivity.PhoneView.this.selectCountry(paramAnonymous2String1);
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
        this.view = new View(paramContext);
        this.view.setPadding(AndroidUtilities.dp(12.0F), 0, AndroidUtilities.dp(12.0F), 0);
        this.view.setBackgroundColor(Theme.getColor("windowBackgroundWhiteGrayLine"));
        addView(this.view, LayoutHelper.createLinear(-1, 1, 4.0F, -17.5F, 4.0F, 0.0F));
        localObject2 = new LinearLayout(paramContext);
        ((LinearLayout)localObject2).setOrientation(0);
        addView((View)localObject2, LayoutHelper.createLinear(-1, -2, 0.0F, 20.0F, 0.0F, 0.0F));
        this.textView = new TextView(paramContext);
        this.textView.setText("+");
        this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.textView.setTextSize(1, 18.0F);
        ((LinearLayout)localObject2).addView(this.textView, LayoutHelper.createLinear(-2, -2));
        this.codeField = new EditTextBoldCursor(paramContext);
        this.codeField.setInputType(3);
        this.codeField.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.codeField.setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.codeField.setCursorSize(AndroidUtilities.dp(20.0F));
        this.codeField.setCursorWidth(1.5F);
        this.codeField.setBackgroundDrawable(Theme.createEditTextDrawable(paramContext, false));
        this.codeField.setPadding(AndroidUtilities.dp(10.0F), 0, 0, 0);
        this.codeField.setTextSize(1, 18.0F);
        this.codeField.setMaxLines(1);
        this.codeField.setGravity(19);
        this.codeField.setImeOptions(268435461);
        localObject1 = new InputFilter.LengthFilter(5);
        this.codeField.setFilters(new InputFilter[] { localObject1 });
        ((LinearLayout)localObject2).addView(this.codeField, LayoutHelper.createLinear(55, 36, -9.0F, 0.0F, 16.0F, 0.0F));
        this.codeField.addTextChangedListener(new TextWatcher()
        {
          public void afterTextChanged(Editable paramAnonymousEditable)
          {
            if (ChangePhoneActivity.PhoneView.this.ignoreOnTextChange) {}
            Object localObject1;
            for (;;)
            {
              return;
              ChangePhoneActivity.PhoneView.access$302(ChangePhoneActivity.PhoneView.this, true);
              localObject1 = PhoneFormat.stripExceptNumbers(ChangePhoneActivity.PhoneView.this.codeField.getText().toString());
              ChangePhoneActivity.PhoneView.this.codeField.setText((CharSequence)localObject1);
              if (((String)localObject1).length() != 0) {
                break;
              }
              ChangePhoneActivity.PhoneView.this.countryButton.setText(LocaleController.getString("ChooseCountry", NUM));
              ChangePhoneActivity.PhoneView.this.phoneField.setHintText(null);
              ChangePhoneActivity.PhoneView.access$602(ChangePhoneActivity.PhoneView.this, 1);
              ChangePhoneActivity.PhoneView.access$302(ChangePhoneActivity.PhoneView.this, false);
            }
            int i = 0;
            int j = 0;
            paramAnonymousEditable = null;
            Object localObject2 = null;
            Object localObject3 = localObject1;
            label136:
            int k;
            if (((String)localObject1).length() > 4)
            {
              ChangePhoneActivity.PhoneView.access$302(ChangePhoneActivity.PhoneView.this, true);
              i = 4;
              k = j;
              localObject4 = localObject1;
              paramAnonymousEditable = (Editable)localObject2;
              if (i >= 1)
              {
                paramAnonymousEditable = ((String)localObject1).substring(0, i);
                if ((String)ChangePhoneActivity.PhoneView.this.codesMap.get(paramAnonymousEditable) == null) {
                  break label538;
                }
                k = 1;
                localObject3 = ((String)localObject1).substring(i, ((String)localObject1).length()) + ChangePhoneActivity.PhoneView.this.phoneField.getText().toString();
                localObject1 = ChangePhoneActivity.PhoneView.this.codeField;
                localObject4 = paramAnonymousEditable;
                ((EditTextBoldCursor)localObject1).setText(paramAnonymousEditable);
                paramAnonymousEditable = (Editable)localObject3;
              }
              i = k;
              localObject3 = localObject4;
              if (k == 0)
              {
                ChangePhoneActivity.PhoneView.access$302(ChangePhoneActivity.PhoneView.this, true);
                paramAnonymousEditable = ((String)localObject4).substring(1, ((String)localObject4).length()) + ChangePhoneActivity.PhoneView.this.phoneField.getText().toString();
                localObject1 = ChangePhoneActivity.PhoneView.this.codeField;
                localObject3 = ((String)localObject4).substring(0, 1);
                ((EditTextBoldCursor)localObject1).setText((CharSequence)localObject3);
                i = k;
              }
            }
            Object localObject4 = (String)ChangePhoneActivity.PhoneView.this.codesMap.get(localObject3);
            if (localObject4 != null)
            {
              k = ChangePhoneActivity.PhoneView.this.countriesArray.indexOf(localObject4);
              if (k != -1)
              {
                ChangePhoneActivity.PhoneView.access$902(ChangePhoneActivity.PhoneView.this, true);
                ChangePhoneActivity.PhoneView.this.countryButton.setText((CharSequence)ChangePhoneActivity.PhoneView.this.countriesArray.get(k));
                localObject4 = (String)ChangePhoneActivity.PhoneView.this.phoneFormatMap.get(localObject3);
                localObject3 = ChangePhoneActivity.PhoneView.this.phoneField;
                if (localObject4 != null)
                {
                  localObject4 = ((String)localObject4).replace('X', '–');
                  label444:
                  ((HintEditText)localObject3).setHintText((String)localObject4);
                  ChangePhoneActivity.PhoneView.access$602(ChangePhoneActivity.PhoneView.this, 0);
                }
              }
            }
            for (;;)
            {
              if (i == 0) {
                ChangePhoneActivity.PhoneView.this.codeField.setSelection(ChangePhoneActivity.PhoneView.this.codeField.getText().length());
              }
              if (paramAnonymousEditable == null) {
                break;
              }
              ChangePhoneActivity.PhoneView.this.phoneField.requestFocus();
              ChangePhoneActivity.PhoneView.this.phoneField.setText(paramAnonymousEditable);
              ChangePhoneActivity.PhoneView.this.phoneField.setSelection(ChangePhoneActivity.PhoneView.this.phoneField.length());
              break;
              label538:
              i--;
              break label136;
              localObject4 = null;
              break label444;
              ChangePhoneActivity.PhoneView.this.countryButton.setText(LocaleController.getString("WrongCountry", NUM));
              ChangePhoneActivity.PhoneView.this.phoneField.setHintText(null);
              ChangePhoneActivity.PhoneView.access$602(ChangePhoneActivity.PhoneView.this, 2);
              continue;
              ChangePhoneActivity.PhoneView.this.countryButton.setText(LocaleController.getString("WrongCountry", NUM));
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
            }
            for (boolean bool = true;; bool = false) {
              return bool;
            }
          }
        });
        this.phoneField = new HintEditText(paramContext);
        this.phoneField.setInputType(3);
        this.phoneField.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.phoneField.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
        this.phoneField.setBackgroundDrawable(Theme.createEditTextDrawable(paramContext, false));
        this.phoneField.setPadding(0, 0, 0, 0);
        this.phoneField.setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.phoneField.setCursorSize(AndroidUtilities.dp(20.0F));
        this.phoneField.setCursorWidth(1.5F);
        this.phoneField.setTextSize(1, 18.0F);
        this.phoneField.setMaxLines(1);
        this.phoneField.setGravity(19);
        this.phoneField.setImeOptions(268435461);
        ((LinearLayout)localObject2).addView(this.phoneField, LayoutHelper.createFrame(-1, 36.0F));
        this.phoneField.addTextChangedListener(new TextWatcher()
        {
          private int actionPosition;
          private int characterAction = -1;
          
          public void afterTextChanged(Editable paramAnonymousEditable)
          {
            if (ChangePhoneActivity.PhoneView.this.ignoreOnPhoneChange) {
              return;
            }
            int i = ChangePhoneActivity.PhoneView.this.phoneField.getSelectionStart();
            Object localObject = ChangePhoneActivity.PhoneView.this.phoneField.getText().toString();
            int j = i;
            paramAnonymousEditable = (Editable)localObject;
            if (this.characterAction == 3)
            {
              paramAnonymousEditable = ((String)localObject).substring(0, this.actionPosition) + ((String)localObject).substring(this.actionPosition + 1, ((String)localObject).length());
              j = i - 1;
            }
            localObject = new StringBuilder(paramAnonymousEditable.length());
            for (i = 0; i < paramAnonymousEditable.length(); i++)
            {
              String str = paramAnonymousEditable.substring(i, i + 1);
              if ("0123456789".contains(str)) {
                ((StringBuilder)localObject).append(str);
              }
            }
            ChangePhoneActivity.PhoneView.access$1102(ChangePhoneActivity.PhoneView.this, true);
            paramAnonymousEditable = ChangePhoneActivity.PhoneView.this.phoneField.getHintText();
            i = j;
            if (paramAnonymousEditable != null)
            {
              int k = 0;
              for (;;)
              {
                i = j;
                if (k >= ((StringBuilder)localObject).length()) {
                  break label343;
                }
                if (k >= paramAnonymousEditable.length()) {
                  break;
                }
                int m = k;
                i = j;
                if (paramAnonymousEditable.charAt(k) == ' ')
                {
                  ((StringBuilder)localObject).insert(k, ' ');
                  k++;
                  m = k;
                  i = j;
                  if (j == k)
                  {
                    m = k;
                    i = j;
                    if (this.characterAction != 2)
                    {
                      m = k;
                      i = j;
                      if (this.characterAction != 3)
                      {
                        i = j + 1;
                        m = k;
                      }
                    }
                  }
                }
                k = m + 1;
                j = i;
              }
              ((StringBuilder)localObject).insert(k, ' ');
              i = j;
              if (j == k + 1)
              {
                i = j;
                if (this.characterAction != 2)
                {
                  i = j;
                  if (this.characterAction != 3) {
                    i = j + 1;
                  }
                }
              }
            }
            label343:
            ChangePhoneActivity.PhoneView.this.phoneField.setText((CharSequence)localObject);
            if (i >= 0)
            {
              paramAnonymousEditable = ChangePhoneActivity.PhoneView.this.phoneField;
              if (i > ChangePhoneActivity.PhoneView.this.phoneField.length()) {
                break label407;
              }
            }
            for (;;)
            {
              paramAnonymousEditable.setSelection(i);
              ChangePhoneActivity.PhoneView.this.phoneField.onTextChange();
              ChangePhoneActivity.PhoneView.access$1102(ChangePhoneActivity.PhoneView.this, false);
              break;
              label407:
              i = ChangePhoneActivity.PhoneView.this.phoneField.length();
            }
          }
          
          public void beforeTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3)
          {
            if ((paramAnonymousInt2 == 0) && (paramAnonymousInt3 == 1)) {
              this.characterAction = 1;
            }
            for (;;)
            {
              return;
              if ((paramAnonymousInt2 == 1) && (paramAnonymousInt3 == 0))
              {
                if ((paramAnonymousCharSequence.charAt(paramAnonymousInt1) == ' ') && (paramAnonymousInt1 > 0))
                {
                  this.characterAction = 3;
                  this.actionPosition = (paramAnonymousInt1 - 1);
                }
                else
                {
                  this.characterAction = 2;
                }
              }
              else {
                this.characterAction = -1;
              }
            }
          }
          
          public void onTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
        });
        this.phoneField.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
          public boolean onEditorAction(TextView paramAnonymousTextView, int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
          {
            if (paramAnonymousInt == 5) {
              ChangePhoneActivity.PhoneView.this.onNextPressed();
            }
            for (boolean bool = true;; bool = false) {
              return bool;
            }
          }
        });
        this.textView2 = new TextView(paramContext);
        this.textView2.setText(LocaleController.getString("ChangePhoneHelp", NUM));
        this.textView2.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
        this.textView2.setTextSize(1, 14.0F);
        paramContext = this.textView2;
        if (!LocaleController.isRTL) {
          break label1232;
        }
        i = 5;
        label844:
        paramContext.setGravity(i);
        this.textView2.setLineSpacing(AndroidUtilities.dp(2.0F), 1.0F);
        paramContext = this.textView2;
        if (!LocaleController.isRTL) {
          break label1238;
        }
        i = 5;
        label877:
        addView(paramContext, LayoutHelper.createLinear(-2, -2, i, 0, 28, 0, 10));
        localObject1 = new HashMap();
        try
        {
          paramContext = new java/io/BufferedReader;
          localObject2 = new java/io/InputStreamReader;
          ((InputStreamReader)localObject2).<init>(getResources().getAssets().open("countries.txt"));
          paramContext.<init>((Reader)localObject2);
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
          FileLog.e(paramContext);
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
            FileLog.e(ChangePhoneActivity.this);
            this$1 = paramContext;
            continue;
            AndroidUtilities.showKeyboard(this.codeField);
            this.codeField.requestFocus();
          }
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
          this.countryButton.setText(LocaleController.getString("ChooseCountry", NUM));
          this.phoneField.setHintText(null);
          this.countryState = 1;
        }
        if (this.codeField.length() == 0) {
          break label1261;
        }
        AndroidUtilities.showKeyboard(this.phoneField);
        this.phoneField.requestFocus();
        this.phoneField.setSelection(this.phoneField.length());
        return;
        i = 3;
        break;
        label1232:
        i = 3;
        break label844;
        label1238:
        i = 3;
        break label877;
        paramContext.close();
      }
    }
    
    public String getHeaderName()
    {
      return LocaleController.getString("ChangePhoneNewNumber", NUM);
    }
    
    public void onCancelPressed()
    {
      this.nextPressed = false;
    }
    
    public void onItemSelected(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong)
    {
      if (this.ignoreSelection) {
        this.ignoreSelection = false;
      }
      for (;;)
      {
        return;
        this.ignoreOnTextChange = true;
        paramAdapterView = (String)this.countriesArray.get(paramInt);
        this.codeField.setText((CharSequence)this.countriesMap.get(paramAdapterView));
        this.ignoreOnTextChange = false;
      }
    }
    
    public void onNextPressed()
    {
      if ((ChangePhoneActivity.this.getParentActivity() == null) || (this.nextPressed)) {}
      for (;;)
      {
        return;
        final Object localObject1 = (TelephonyManager)ApplicationLoader.applicationContext.getSystemService("phone");
        int i;
        label48:
        int j;
        int k;
        label86:
        int m;
        if ((((TelephonyManager)localObject1).getSimState() != 1) && (((TelephonyManager)localObject1).getPhoneType() != 0))
        {
          i = 1;
          j = 1;
          k = j;
          if (Build.VERSION.SDK_INT < 23) {
            break label473;
          }
          k = j;
          if (i == 0) {
            break label473;
          }
          if (ChangePhoneActivity.this.getParentActivity().checkSelfPermission("android.permission.READ_PHONE_STATE") != 0) {
            break label380;
          }
          j = 1;
          if (ChangePhoneActivity.this.getParentActivity().checkSelfPermission("android.permission.RECEIVE_SMS") != 0) {
            break label385;
          }
          m = 1;
          label105:
          k = j;
          if (!ChangePhoneActivity.this.checkPermissions) {
            break label473;
          }
          ChangePhoneActivity.this.permissionsItems.clear();
          if (j == 0) {
            ChangePhoneActivity.this.permissionsItems.add("android.permission.READ_PHONE_STATE");
          }
          if (m == 0)
          {
            ChangePhoneActivity.this.permissionsItems.add("android.permission.RECEIVE_SMS");
            if (Build.VERSION.SDK_INT >= 23) {
              ChangePhoneActivity.this.permissionsItems.add("android.permission.READ_SMS");
            }
          }
          k = j;
          if (ChangePhoneActivity.this.permissionsItems.isEmpty()) {
            break label473;
          }
          localObject2 = MessagesController.getGlobalMainSettings();
          if ((!((SharedPreferences)localObject2).getBoolean("firstlogin", true)) && (!ChangePhoneActivity.this.getParentActivity().shouldShowRequestPermissionRationale("android.permission.READ_PHONE_STATE")) && (!ChangePhoneActivity.this.getParentActivity().shouldShowRequestPermissionRationale("android.permission.RECEIVE_SMS"))) {
            break label432;
          }
          ((SharedPreferences)localObject2).edit().putBoolean("firstlogin", false).commit();
          localObject2 = new AlertDialog.Builder(ChangePhoneActivity.this.getParentActivity());
          ((AlertDialog.Builder)localObject2).setTitle(LocaleController.getString("AppName", NUM));
          ((AlertDialog.Builder)localObject2).setPositiveButton(LocaleController.getString("OK", NUM), null);
          if (ChangePhoneActivity.this.permissionsItems.size() != 2) {
            break label391;
          }
          ((AlertDialog.Builder)localObject2).setMessage(LocaleController.getString("AllowReadCallAndSms", NUM));
        }
        for (;;)
        {
          ChangePhoneActivity.access$1402(ChangePhoneActivity.this, ChangePhoneActivity.this.showDialog(((AlertDialog.Builder)localObject2).create()));
          break;
          i = 0;
          break label48;
          label380:
          j = 0;
          break label86;
          label385:
          m = 0;
          break label105;
          label391:
          if (m == 0) {
            ((AlertDialog.Builder)localObject2).setMessage(LocaleController.getString("AllowReadSms", NUM));
          } else {
            ((AlertDialog.Builder)localObject2).setMessage(LocaleController.getString("AllowReadCall", NUM));
          }
        }
        label432:
        ChangePhoneActivity.this.getParentActivity().requestPermissions((String[])ChangePhoneActivity.this.permissionsItems.toArray(new String[ChangePhoneActivity.this.permissionsItems.size()]), 6);
        continue;
        label473:
        if (this.countryState == 1)
        {
          AlertsCreator.showSimpleAlert(ChangePhoneActivity.this, LocaleController.getString("ChooseCountry", NUM));
          continue;
        }
        if ((this.countryState == 2) && (!BuildVars.DEBUG_VERSION))
        {
          AlertsCreator.showSimpleAlert(ChangePhoneActivity.this, LocaleController.getString("WrongCountry", NUM));
          continue;
        }
        if (this.codeField.length() == 0)
        {
          AlertsCreator.showSimpleAlert(ChangePhoneActivity.this, LocaleController.getString("InvalidPhoneNumber", NUM));
          continue;
        }
        final TLRPC.TL_account_sendChangePhoneCode localTL_account_sendChangePhoneCode = new TLRPC.TL_account_sendChangePhoneCode();
        Object localObject2 = PhoneFormat.stripExceptNumbers("" + this.codeField.getText() + this.phoneField.getText());
        localTL_account_sendChangePhoneCode.phone_number = ((String)localObject2);
        boolean bool;
        if ((i != 0) && (k != 0))
        {
          bool = true;
          label634:
          localTL_account_sendChangePhoneCode.allow_flashcall = bool;
          if (!localTL_account_sendChangePhoneCode.allow_flashcall) {}
        }
        try
        {
          localObject1 = ((TelephonyManager)localObject1).getLine1Number();
          if (!TextUtils.isEmpty((CharSequence)localObject1)) {
            if ((((String)localObject2).contains((CharSequence)localObject1)) || (((String)localObject1).contains((CharSequence)localObject2)))
            {
              bool = true;
              localTL_account_sendChangePhoneCode.current_number = bool;
              if (!localTL_account_sendChangePhoneCode.current_number) {
                localTL_account_sendChangePhoneCode.allow_flashcall = false;
              }
              localObject1 = new Bundle();
              ((Bundle)localObject1).putString("phone", "+" + this.codeField.getText() + this.phoneField.getText());
            }
          }
        }
        catch (Exception localException1)
        {
          try
          {
            for (;;)
            {
              StringBuilder localStringBuilder = new java/lang/StringBuilder;
              localStringBuilder.<init>();
              ((Bundle)localObject1).putString("ephone", "+" + PhoneFormat.stripExceptNumbers(this.codeField.getText().toString()) + " " + PhoneFormat.stripExceptNumbers(this.phoneField.getText().toString()));
              ((Bundle)localObject1).putString("phoneFormated", (String)localObject2);
              this.nextPressed = true;
              ChangePhoneActivity.this.needShowProgress();
              ConnectionsManager.getInstance(ChangePhoneActivity.this.currentAccount).sendRequest(localTL_account_sendChangePhoneCode, new RequestDelegate()
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
                        AlertsCreator.processError(ChangePhoneActivity.this.currentAccount, paramAnonymousTL_error, ChangePhoneActivity.this, ChangePhoneActivity.PhoneView.7.this.val$req, new Object[] { ChangePhoneActivity.PhoneView.7.this.val$params.getString("phone") });
                      }
                    }
                  });
                }
              }, 2);
              break;
              bool = false;
              break label634;
              bool = false;
              continue;
              localTL_account_sendChangePhoneCode.current_number = false;
            }
            localException1 = localException1;
            localTL_account_sendChangePhoneCode.allow_flashcall = false;
            FileLog.e(localException1);
          }
          catch (Exception localException2)
          {
            for (;;)
            {
              FileLog.e(localException2);
              localException1.putString("ephone", "+" + (String)localObject2);
            }
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
        if (this.codeField.length() == 0) {
          break label51;
        }
        AndroidUtilities.showKeyboard(this.phoneField);
        this.phoneField.requestFocus();
        this.phoneField.setSelection(this.phoneField.length());
      }
      for (;;)
      {
        return;
        label51:
        AndroidUtilities.showKeyboard(this.codeField);
        this.codeField.requestFocus();
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
  
  private class ProgressView
    extends View
  {
    private Paint paint = new Paint();
    private Paint paint2 = new Paint();
    private float progress;
    
    public ProgressView(Context paramContext)
    {
      super();
      this.paint.setColor(Theme.getColor("login_progressInner"));
      this.paint2.setColor(Theme.getColor("login_progressOuter"));
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


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/ChangePhoneActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */