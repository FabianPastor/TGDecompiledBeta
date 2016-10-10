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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_reportPeer;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputReportReasonOther;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Components.LayoutHelper;

public class ReportOtherActivity
  extends BaseFragment
{
  private static final int done_button = 1;
  private long dialog_id = getArguments().getLong("dialog_id", 0L);
  private View doneButton;
  private EditText firstNameField;
  private View headerLabelView;
  
  public ReportOtherActivity(Bundle paramBundle)
  {
    super(paramBundle);
  }
  
  public View createView(Context paramContext)
  {
    int j = 3;
    this.actionBar.setBackButtonImage(2130837700);
    this.actionBar.setAllowOverlayTitle(true);
    this.actionBar.setTitle(LocaleController.getString("ReportChat", 2131166159));
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (paramAnonymousInt == -1) {
          ReportOtherActivity.this.finishFragment();
        }
        while ((paramAnonymousInt != 1) || (ReportOtherActivity.this.firstNameField.getText().length() == 0)) {
          return;
        }
        TLRPC.TL_account_reportPeer localTL_account_reportPeer = new TLRPC.TL_account_reportPeer();
        localTL_account_reportPeer.peer = MessagesController.getInputPeer((int)ReportOtherActivity.this.dialog_id);
        localTL_account_reportPeer.reason = new TLRPC.TL_inputReportReasonOther();
        localTL_account_reportPeer.reason.text = ReportOtherActivity.this.firstNameField.getText().toString();
        ConnectionsManager.getInstance().sendRequest(localTL_account_reportPeer, new RequestDelegate()
        {
          public void run(TLObject paramAnonymous2TLObject, TLRPC.TL_error paramAnonymous2TL_error) {}
        });
        ReportOtherActivity.this.finishFragment();
      }
    });
    this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, 2130837720, AndroidUtilities.dp(56.0F));
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
    this.firstNameField = new EditText(paramContext);
    this.firstNameField.setTextSize(1, 18.0F);
    this.firstNameField.setHintTextColor(-6842473);
    this.firstNameField.setTextColor(-14606047);
    this.firstNameField.setMaxLines(3);
    this.firstNameField.setPadding(0, 0, 0, 0);
    paramContext = this.firstNameField;
    if (LocaleController.isRTL) {}
    for (int i = 5;; i = 3)
    {
      paramContext.setGravity(i);
      this.firstNameField.setInputType(180224);
      this.firstNameField.setImeOptions(6);
      paramContext = this.firstNameField;
      i = j;
      if (LocaleController.isRTL) {
        i = 5;
      }
      paramContext.setGravity(i);
      AndroidUtilities.clearCursorDrawable(this.firstNameField);
      this.firstNameField.setOnEditorActionListener(new TextView.OnEditorActionListener()
      {
        public boolean onEditorAction(TextView paramAnonymousTextView, int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
        {
          if ((paramAnonymousInt == 6) && (ReportOtherActivity.this.doneButton != null))
          {
            ReportOtherActivity.this.doneButton.performClick();
            return true;
          }
          return false;
        }
      });
      localLinearLayout.addView(this.firstNameField, LayoutHelper.createLinear(-1, 36, 24.0F, 24.0F, 24.0F, 0.0F));
      this.firstNameField.setHint(LocaleController.getString("ReportChatDescription", 2131166160));
      this.firstNameField.setSelection(this.firstNameField.length());
      return this.fragmentView;
    }
  }
  
  public void onResume()
  {
    super.onResume();
    if (!ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).getBoolean("view_animations", true))
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