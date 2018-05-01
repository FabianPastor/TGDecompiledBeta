package org.telegram.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.InputPeer;
import org.telegram.tgnet.TLRPC.TL_account_reportPeer;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputReportReasonOther;
import org.telegram.tgnet.TLRPC.TL_messages_report;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;

public class ReportOtherActivity
  extends BaseFragment
{
  private static final int done_button = 1;
  private long dialog_id = getArguments().getLong("dialog_id", 0L);
  private View doneButton;
  private EditTextBoldCursor firstNameField;
  private View headerLabelView;
  private int message_id = getArguments().getInt("message_id", 0);
  
  public ReportOtherActivity(Bundle paramBundle)
  {
    super(paramBundle);
  }
  
  public View createView(Context paramContext)
  {
    int i = 3;
    this.actionBar.setBackButtonImage(NUM);
    this.actionBar.setAllowOverlayTitle(true);
    this.actionBar.setTitle(LocaleController.getString("ReportChat", NUM));
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (paramAnonymousInt == -1)
        {
          ReportOtherActivity.this.finishFragment();
          return;
        }
        Object localObject;
        if ((paramAnonymousInt == 1) && (ReportOtherActivity.this.firstNameField.getText().length() != 0))
        {
          TLRPC.InputPeer localInputPeer = MessagesController.getInstance(UserConfig.selectedAccount).getInputPeer((int)ReportOtherActivity.this.dialog_id);
          if (ReportOtherActivity.this.message_id == 0) {
            break label190;
          }
          localObject = new TLRPC.TL_messages_report();
          ((TLRPC.TL_messages_report)localObject).peer = localInputPeer;
          ((TLRPC.TL_messages_report)localObject).id.add(Integer.valueOf(ReportOtherActivity.this.message_id));
          ((TLRPC.TL_messages_report)localObject).reason = new TLRPC.TL_inputReportReasonOther();
        }
        for (((TLRPC.TL_messages_report)localObject).reason.text = ReportOtherActivity.this.firstNameField.getText().toString();; ((TLRPC.TL_account_reportPeer)localObject).reason.text = ReportOtherActivity.this.firstNameField.getText().toString())
        {
          ConnectionsManager.getInstance(ReportOtherActivity.this.currentAccount).sendRequest((TLObject)localObject, new RequestDelegate()
          {
            public void run(TLObject paramAnonymous2TLObject, TLRPC.TL_error paramAnonymous2TL_error) {}
          });
          if (ReportOtherActivity.this.getParentActivity() != null) {
            Toast.makeText(ReportOtherActivity.this.getParentActivity(), LocaleController.getString("ReportChatSent", NUM), 0).show();
          }
          ReportOtherActivity.this.finishFragment();
          break;
          break;
          label190:
          localObject = new TLRPC.TL_account_reportPeer();
          ((TLRPC.TL_account_reportPeer)localObject).peer = MessagesController.getInstance(ReportOtherActivity.this.currentAccount).getInputPeer((int)ReportOtherActivity.this.dialog_id);
          ((TLRPC.TL_account_reportPeer)localObject).reason = new TLRPC.TL_inputReportReasonOther();
        }
      }
    });
    this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, NUM, AndroidUtilities.dp(56.0F));
    LinearLayout localLinearLayout = new LinearLayout(paramContext);
    this.fragmentView = localLinearLayout;
    this.fragmentView.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
    ((LinearLayout)this.fragmentView).setOrientation(1);
    this.fragmentView.setOnTouchListener(new View.OnTouchListener()
    {
      public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
      {
        return true;
      }
    });
    this.firstNameField = new EditTextBoldCursor(paramContext);
    this.firstNameField.setTextSize(1, 18.0F);
    this.firstNameField.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
    this.firstNameField.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
    this.firstNameField.setBackgroundDrawable(Theme.createEditTextDrawable(paramContext, false));
    this.firstNameField.setMaxLines(3);
    this.firstNameField.setPadding(0, 0, 0, 0);
    paramContext = this.firstNameField;
    if (LocaleController.isRTL) {}
    for (int j = 5;; j = 3)
    {
      paramContext.setGravity(j);
      this.firstNameField.setInputType(180224);
      this.firstNameField.setImeOptions(6);
      paramContext = this.firstNameField;
      j = i;
      if (LocaleController.isRTL) {
        j = 5;
      }
      paramContext.setGravity(j);
      this.firstNameField.setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
      this.firstNameField.setCursorSize(AndroidUtilities.dp(20.0F));
      this.firstNameField.setCursorWidth(1.5F);
      this.firstNameField.setOnEditorActionListener(new TextView.OnEditorActionListener()
      {
        public boolean onEditorAction(TextView paramAnonymousTextView, int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
        {
          if ((paramAnonymousInt == 6) && (ReportOtherActivity.this.doneButton != null)) {
            ReportOtherActivity.this.doneButton.performClick();
          }
          for (boolean bool = true;; bool = false) {
            return bool;
          }
        }
      });
      localLinearLayout.addView(this.firstNameField, LayoutHelper.createLinear(-1, 36, 24.0F, 24.0F, 24.0F, 0.0F));
      this.firstNameField.setHint(LocaleController.getString("ReportChatDescription", NUM));
      this.firstNameField.setSelection(this.firstNameField.length());
      return this.fragmentView;
    }
  }
  
  public ThemeDescription[] getThemeDescriptions()
  {
    return new ThemeDescription[] { new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector"), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField"), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "windowBackgroundWhiteInputFieldActivated") };
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
    if (paramBoolean1) {
      AndroidUtilities.runOnUIThread(new Runnable()
      {
        public void run()
        {
          if (ReportOtherActivity.this.firstNameField != null)
          {
            ReportOtherActivity.this.firstNameField.requestFocus();
            AndroidUtilities.showKeyboard(ReportOtherActivity.this.firstNameField);
          }
        }
      }, 100L);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/ReportOtherActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */