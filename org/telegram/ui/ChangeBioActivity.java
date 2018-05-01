package org.telegram.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_updateProfile;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_userFull;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;

public class ChangeBioActivity
  extends BaseFragment
{
  private static final int done_button = 1;
  private TextView checkTextView;
  private View doneButton;
  private EditTextBoldCursor firstNameField;
  private TextView helpTextView;
  
  private void saveName()
  {
    final TLRPC.TL_userFull localTL_userFull = MessagesController.getInstance(this.currentAccount).getUserFull(UserConfig.getInstance(this.currentAccount).getClientUserId());
    if ((getParentActivity() == null) || (localTL_userFull == null)) {}
    for (;;)
    {
      return;
      final String str = localTL_userFull.about;
      final Object localObject = str;
      if (str == null) {
        localObject = "";
      }
      str = this.firstNameField.getText().toString().replace("\n", "");
      if (((String)localObject).equals(str))
      {
        finishFragment();
      }
      else
      {
        final AlertDialog localAlertDialog = new AlertDialog(getParentActivity(), 1);
        localAlertDialog.setMessage(LocaleController.getString("Loading", NUM));
        localAlertDialog.setCanceledOnTouchOutside(false);
        localAlertDialog.setCancelable(false);
        localObject = new TLRPC.TL_account_updateProfile();
        ((TLRPC.TL_account_updateProfile)localObject).about = str;
        ((TLRPC.TL_account_updateProfile)localObject).flags |= 0x4;
        final int i = ConnectionsManager.getInstance(this.currentAccount).sendRequest((TLObject)localObject, new RequestDelegate()
        {
          public void run(final TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
          {
            if (paramAnonymousTL_error == null) {
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  try
                  {
                    ChangeBioActivity.6.this.val$progressDialog.dismiss();
                    ChangeBioActivity.6.this.val$userFull.about = ChangeBioActivity.6.this.val$newName;
                    NotificationCenter.getInstance(ChangeBioActivity.this.currentAccount).postNotificationName(NotificationCenter.userInfoDidLoaded, new Object[] { Integer.valueOf(paramAnonymousTLObject.id), ChangeBioActivity.6.this.val$userFull });
                    ChangeBioActivity.this.finishFragment();
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
              });
            }
            for (;;)
            {
              return;
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  try
                  {
                    ChangeBioActivity.6.this.val$progressDialog.dismiss();
                    AlertsCreator.processError(ChangeBioActivity.this.currentAccount, paramAnonymousTL_error, ChangeBioActivity.this, ChangeBioActivity.6.this.val$req, new Object[0]);
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
              });
            }
          }
        }, 2);
        ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(i, this.classGuid);
        localAlertDialog.setButton(-2, LocaleController.getString("Cancel", NUM), new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
          {
            ConnectionsManager.getInstance(ChangeBioActivity.this.currentAccount).cancelRequest(i, true);
            try
            {
              paramAnonymousDialogInterface.dismiss();
              return;
            }
            catch (Exception paramAnonymousDialogInterface)
            {
              for (;;)
              {
                FileLog.e(paramAnonymousDialogInterface);
              }
            }
          }
        });
        localAlertDialog.show();
      }
    }
  }
  
  public View createView(Context paramContext)
  {
    this.actionBar.setBackButtonImage(NUM);
    this.actionBar.setAllowOverlayTitle(true);
    this.actionBar.setTitle(LocaleController.getString("UserBio", NUM));
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (paramAnonymousInt == -1) {
          ChangeBioActivity.this.finishFragment();
        }
        for (;;)
        {
          return;
          if (paramAnonymousInt == 1) {
            ChangeBioActivity.this.saveName();
          }
        }
      }
    });
    this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, NUM, AndroidUtilities.dp(56.0F));
    this.fragmentView = new LinearLayout(paramContext);
    LinearLayout localLinearLayout = (LinearLayout)this.fragmentView;
    localLinearLayout.setOrientation(1);
    this.fragmentView.setOnTouchListener(new View.OnTouchListener()
    {
      public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
      {
        return true;
      }
    });
    FrameLayout localFrameLayout = new FrameLayout(paramContext);
    localLinearLayout.addView(localFrameLayout, LayoutHelper.createLinear(-1, -2, 24.0F, 24.0F, 20.0F, 0.0F));
    this.firstNameField = new EditTextBoldCursor(paramContext);
    this.firstNameField.setTextSize(1, 18.0F);
    this.firstNameField.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
    this.firstNameField.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
    this.firstNameField.setBackgroundDrawable(Theme.createEditTextDrawable(paramContext, false));
    this.firstNameField.setMaxLines(4);
    Object localObject = this.firstNameField;
    float f;
    if (LocaleController.isRTL)
    {
      f = 24.0F;
      i = AndroidUtilities.dp(f);
      if (!LocaleController.isRTL) {
        break label727;
      }
      f = 0.0F;
      label236:
      ((EditTextBoldCursor)localObject).setPadding(i, 0, AndroidUtilities.dp(f), AndroidUtilities.dp(6.0F));
      localObject = this.firstNameField;
      if (!LocaleController.isRTL) {
        break label734;
      }
      i = 5;
      label270:
      ((EditTextBoldCursor)localObject).setGravity(i);
      this.firstNameField.setImeOptions(268435456);
      this.firstNameField.setInputType(147457);
      this.firstNameField.setImeOptions(6);
      localObject = new InputFilter.LengthFilter(70)
      {
        public CharSequence filter(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, Spanned paramAnonymousSpanned, int paramAnonymousInt3, int paramAnonymousInt4)
        {
          if ((paramAnonymousCharSequence != null) && (TextUtils.indexOf(paramAnonymousCharSequence, '\n') != -1))
          {
            ChangeBioActivity.this.doneButton.performClick();
            paramAnonymousSpanned = "";
          }
          for (;;)
          {
            return paramAnonymousSpanned;
            CharSequence localCharSequence = super.filter(paramAnonymousCharSequence, paramAnonymousInt1, paramAnonymousInt2, paramAnonymousSpanned, paramAnonymousInt3, paramAnonymousInt4);
            paramAnonymousSpanned = localCharSequence;
            if (localCharSequence != null)
            {
              paramAnonymousSpanned = localCharSequence;
              if (paramAnonymousCharSequence != null)
              {
                paramAnonymousSpanned = localCharSequence;
                if (localCharSequence.length() != paramAnonymousCharSequence.length())
                {
                  paramAnonymousCharSequence = (Vibrator)ChangeBioActivity.this.getParentActivity().getSystemService("vibrator");
                  if (paramAnonymousCharSequence != null) {
                    paramAnonymousCharSequence.vibrate(200L);
                  }
                  AndroidUtilities.shakeView(ChangeBioActivity.this.checkTextView, 2.0F, 0);
                  paramAnonymousSpanned = localCharSequence;
                }
              }
            }
          }
        }
      };
      this.firstNameField.setFilters(new InputFilter[] { localObject });
      this.firstNameField.setMinHeight(AndroidUtilities.dp(36.0F));
      this.firstNameField.setHint(LocaleController.getString("UserBio", NUM));
      this.firstNameField.setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
      this.firstNameField.setCursorSize(AndroidUtilities.dp(20.0F));
      this.firstNameField.setCursorWidth(1.5F);
      this.firstNameField.setOnEditorActionListener(new TextView.OnEditorActionListener()
      {
        public boolean onEditorAction(TextView paramAnonymousTextView, int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
        {
          if ((paramAnonymousInt == 6) && (ChangeBioActivity.this.doneButton != null)) {
            ChangeBioActivity.this.doneButton.performClick();
          }
          for (boolean bool = true;; bool = false) {
            return bool;
          }
        }
      });
      this.firstNameField.addTextChangedListener(new TextWatcher()
      {
        public void afterTextChanged(Editable paramAnonymousEditable)
        {
          ChangeBioActivity.this.checkTextView.setText(String.format("%d", new Object[] { Integer.valueOf(70 - ChangeBioActivity.this.firstNameField.length()) }));
        }
        
        public void beforeTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
        
        public void onTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
      });
      localFrameLayout.addView(this.firstNameField, LayoutHelper.createFrame(-1, -2.0F, 51, 0.0F, 0.0F, 4.0F, 0.0F));
      this.checkTextView = new TextView(paramContext);
      this.checkTextView.setTextSize(1, 15.0F);
      this.checkTextView.setText(String.format("%d", new Object[] { Integer.valueOf(70) }));
      this.checkTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText4"));
      localObject = this.checkTextView;
      if (!LocaleController.isRTL) {
        break label740;
      }
      i = 3;
      label525:
      localFrameLayout.addView((View)localObject, LayoutHelper.createFrame(-2, -2.0F, i, 0.0F, 4.0F, 4.0F, 0.0F));
      this.helpTextView = new TextView(paramContext);
      this.helpTextView.setTextSize(1, 15.0F);
      this.helpTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText8"));
      paramContext = this.helpTextView;
      if (!LocaleController.isRTL) {
        break label746;
      }
      i = 5;
      label599:
      paramContext.setGravity(i);
      this.helpTextView.setText(AndroidUtilities.replaceTags(LocaleController.getString("UserBioInfo", NUM)));
      paramContext = this.helpTextView;
      if (!LocaleController.isRTL) {
        break label752;
      }
    }
    label727:
    label734:
    label740:
    label746:
    label752:
    for (int i = 5;; i = 3)
    {
      localLinearLayout.addView(paramContext, LayoutHelper.createLinear(-2, -2, i, 24, 10, 24, 0));
      paramContext = MessagesController.getInstance(this.currentAccount).getUserFull(UserConfig.getInstance(this.currentAccount).getClientUserId());
      if ((paramContext != null) && (paramContext.about != null))
      {
        this.firstNameField.setText(paramContext.about);
        this.firstNameField.setSelection(this.firstNameField.length());
      }
      return this.fragmentView;
      f = 0.0F;
      break;
      f = 24.0F;
      break label236;
      i = 3;
      break label270;
      i = 5;
      break label525;
      i = 3;
      break label599;
    }
  }
  
  public ThemeDescription[] getThemeDescriptions()
  {
    return new ThemeDescription[] { new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector"), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField"), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"), new ThemeDescription(this.helpTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText8"), new ThemeDescription(this.checkTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText4") };
  }
  
  public void onResume()
  {
    super.onResume();
    if (!MessagesController.getGlobalMainSettings().getBoolean("view_animations", true))
    {
      this.firstNameField.requestFocus();
      AndroidUtilities.showKeyboard(this.firstNameField);
    }
  }
  
  public void onTransitionAnimationEnd(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramBoolean1)
    {
      this.firstNameField.requestFocus();
      AndroidUtilities.showKeyboard(this.firstNameField);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/ChangeBioActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */