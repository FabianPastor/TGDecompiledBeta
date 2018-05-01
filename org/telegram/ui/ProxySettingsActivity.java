package org.telegram.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import java.net.URLEncoder;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;

public class ProxySettingsActivity
  extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  private static final int FIELD_IP = 0;
  private static final int FIELD_PASSWORD = 3;
  private static final int FIELD_PORT = 1;
  private static final int FIELD_USER = 2;
  private static final int share_item = 1;
  private TextInfoPrivacyCell bottomCell;
  private TextCheckCell checkCell1;
  private ArrayList<View> dividers = new ArrayList();
  private HeaderCell headerCell;
  private boolean ignoreOnTextChange;
  private EditTextBoldCursor[] inputFields;
  private LinearLayout linearLayout2;
  private ScrollView scrollView;
  private ShadowSectionCell sectionCell;
  private ActionBarMenuItem shareItem;
  private TextCheckCell useForCallsCell;
  private boolean useProxyForCalls;
  private boolean useProxySettings;
  
  private void checkShareButton()
  {
    if ((this.inputFields[0] == null) || (this.inputFields[1] == null)) {}
    for (;;)
    {
      return;
      if ((this.inputFields[0].length() != 0) && (Utilities.parseInt(this.inputFields[1].getText().toString()).intValue() != 0))
      {
        this.shareItem.setAlpha(1.0F);
        this.shareItem.setEnabled(true);
      }
      else
      {
        this.shareItem.setAlpha(0.5F);
        this.shareItem.setEnabled(false);
      }
    }
  }
  
  public View createView(Context paramContext)
  {
    final Object localObject1 = MessagesController.getGlobalMainSettings();
    this.useProxySettings = ((SharedPreferences)localObject1).getBoolean("proxy_enabled", false);
    this.useProxyForCalls = ((SharedPreferences)localObject1).getBoolean("proxy_enabled_calls", false);
    this.actionBar.setTitle(LocaleController.getString("ProxySettings", NUM));
    this.actionBar.setBackButtonImage(NUM);
    this.actionBar.setAllowOverlayTitle(true);
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (paramAnonymousInt == -1) {
          ProxySettingsActivity.this.finishFragment();
        }
        for (;;)
        {
          return;
          if ((paramAnonymousInt == 1) && (ProxySettingsActivity.this.getParentActivity() != null))
          {
            Object localObject1 = new StringBuilder("");
            String str1 = ProxySettingsActivity.this.inputFields[0].getText().toString();
            String str2 = ProxySettingsActivity.this.inputFields[3].getText().toString();
            String str3 = ProxySettingsActivity.this.inputFields[2].getText().toString();
            Object localObject2 = ProxySettingsActivity.this.inputFields[1].getText().toString();
            try
            {
              if (!TextUtils.isEmpty(str1)) {
                ((StringBuilder)localObject1).append("server=").append(URLEncoder.encode(str1, "UTF-8"));
              }
              if (!TextUtils.isEmpty((CharSequence)localObject2))
              {
                if (((StringBuilder)localObject1).length() != 0) {
                  ((StringBuilder)localObject1).append("&");
                }
                ((StringBuilder)localObject1).append("port=").append(URLEncoder.encode((String)localObject2, "UTF-8"));
              }
              if (!TextUtils.isEmpty(str3))
              {
                if (((StringBuilder)localObject1).length() != 0) {
                  ((StringBuilder)localObject1).append("&");
                }
                ((StringBuilder)localObject1).append("user=").append(URLEncoder.encode(str3, "UTF-8"));
              }
              if (!TextUtils.isEmpty(str2))
              {
                if (((StringBuilder)localObject1).length() != 0) {
                  ((StringBuilder)localObject1).append("&");
                }
                ((StringBuilder)localObject1).append("pass=").append(URLEncoder.encode(str2, "UTF-8"));
              }
              if (((StringBuilder)localObject1).length() == 0) {
                continue;
              }
              localObject2 = new Intent("android.intent.action.SEND");
              ((Intent)localObject2).setType("text/plain");
              ((Intent)localObject2).putExtra("android.intent.extra.TEXT", "https://t.me/socks?" + ((StringBuilder)localObject1).toString());
              localObject1 = Intent.createChooser((Intent)localObject2, LocaleController.getString("ShareLink", NUM));
              ((Intent)localObject1).setFlags(268435456);
              ProxySettingsActivity.this.getParentActivity().startActivity((Intent)localObject1);
            }
            catch (Exception localException) {}
          }
        }
      }
    });
    this.shareItem = this.actionBar.createMenu().addItem(1, NUM);
    this.fragmentView = new FrameLayout(paramContext);
    FrameLayout localFrameLayout = (FrameLayout)this.fragmentView;
    this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
    this.scrollView = new ScrollView(paramContext);
    this.scrollView.setFillViewport(true);
    AndroidUtilities.setScrollViewEdgeEffectColor(this.scrollView, Theme.getColor("actionBarDefault"));
    localFrameLayout.addView(this.scrollView, LayoutHelper.createFrame(-1, -1.0F));
    this.linearLayout2 = new LinearLayout(paramContext);
    this.linearLayout2.setOrientation(1);
    this.scrollView.addView(this.linearLayout2, new FrameLayout.LayoutParams(-1, -2));
    this.checkCell1 = new TextCheckCell(paramContext);
    this.checkCell1.setBackgroundDrawable(Theme.getSelectorDrawable(true));
    this.checkCell1.setTextAndCheck(LocaleController.getString("UseProxySettings", NUM), this.useProxySettings, false);
    this.linearLayout2.addView(this.checkCell1, LayoutHelper.createLinear(-1, -2));
    this.checkCell1.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        paramAnonymousView = ProxySettingsActivity.this;
        if (!ProxySettingsActivity.this.useProxySettings) {}
        for (boolean bool = true;; bool = false)
        {
          ProxySettingsActivity.access$102(paramAnonymousView, bool);
          ProxySettingsActivity.this.checkCell1.setChecked(ProxySettingsActivity.this.useProxySettings);
          if (!ProxySettingsActivity.this.useProxySettings)
          {
            ProxySettingsActivity.this.useForCallsCell.setChecked(false);
            localObject1.edit().putBoolean("proxy_enabled_calls", false).commit();
          }
          ProxySettingsActivity.this.useForCallsCell.setEnabled(ProxySettingsActivity.this.useProxySettings);
          return;
        }
      }
    });
    this.sectionCell = new ShadowSectionCell(paramContext);
    this.linearLayout2.addView(this.sectionCell, LayoutHelper.createLinear(-1, -2));
    this.inputFields = new EditTextBoldCursor[4];
    int i = 0;
    if (i < 4)
    {
      localFrameLayout = new FrameLayout(paramContext);
      this.linearLayout2.addView(localFrameLayout, LayoutHelper.createLinear(-1, 48));
      localFrameLayout.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
      label377:
      Object localObject2;
      if (i != 3)
      {
        j = 1;
        if (j != 0)
        {
          localObject2 = new View(paramContext);
          this.dividers.add(localObject2);
          ((View)localObject2).setBackgroundColor(Theme.getColor("divider"));
          localFrameLayout.addView((View)localObject2, new FrameLayout.LayoutParams(-1, 1, 83));
        }
        this.inputFields[i] = new EditTextBoldCursor(paramContext);
        this.inputFields[i].setTag(Integer.valueOf(i));
        this.inputFields[i].setTextSize(1, 16.0F);
        this.inputFields[i].setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
        this.inputFields[i].setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.inputFields[i].setBackgroundDrawable(null);
        this.inputFields[i].setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.inputFields[i].setCursorSize(AndroidUtilities.dp(20.0F));
        this.inputFields[i].setCursorWidth(1.5F);
        this.inputFields[i].setSingleLine(true);
        if (i != 0) {
          break label782;
        }
        this.inputFields[i].setInputType(524305);
        this.inputFields[i].addTextChangedListener(new TextWatcher()
        {
          public void afterTextChanged(Editable paramAnonymousEditable)
          {
            ProxySettingsActivity.this.checkShareButton();
          }
          
          public void beforeTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
          
          public void onTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
        });
        label609:
        this.inputFields[i].setImeOptions(268435461);
        switch (i)
        {
        default: 
          label656:
          this.inputFields[i].setSelection(this.inputFields[i].length());
          this.inputFields[i].setPadding(0, 0, 0, AndroidUtilities.dp(6.0F));
          localObject2 = this.inputFields[i];
          if (!LocaleController.isRTL) {
            break;
          }
        }
      }
      for (int j = 5;; j = 3)
      {
        ((EditTextBoldCursor)localObject2).setGravity(j);
        localFrameLayout.addView(this.inputFields[i], LayoutHelper.createFrame(-1, -2.0F, 51, 17.0F, 12.0F, 17.0F, 6.0F));
        this.inputFields[i].setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
          public boolean onEditorAction(TextView paramAnonymousTextView, int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
          {
            boolean bool;
            if (paramAnonymousInt == 5)
            {
              paramAnonymousInt = ((Integer)paramAnonymousTextView.getTag()).intValue();
              if (paramAnonymousInt + 1 < ProxySettingsActivity.this.inputFields.length) {
                ProxySettingsActivity.this.inputFields[(paramAnonymousInt + 1)].requestFocus();
              }
              bool = true;
            }
            for (;;)
            {
              return bool;
              if (paramAnonymousInt == 6)
              {
                ProxySettingsActivity.this.finishFragment();
                bool = true;
              }
              else
              {
                bool = false;
              }
            }
          }
        });
        i++;
        break;
        j = 0;
        break label377;
        label782:
        if (i == 1)
        {
          this.inputFields[i].setInputType(2);
          this.inputFields[i].addTextChangedListener(new TextWatcher()
          {
            public void afterTextChanged(Editable paramAnonymousEditable)
            {
              if (ProxySettingsActivity.this.ignoreOnTextChange) {
                return;
              }
              EditTextBoldCursor localEditTextBoldCursor = ProxySettingsActivity.this.inputFields[1];
              int i = localEditTextBoldCursor.getSelectionStart();
              paramAnonymousEditable = localEditTextBoldCursor.getText().toString();
              StringBuilder localStringBuilder = new StringBuilder(paramAnonymousEditable.length());
              for (int j = 0; j < paramAnonymousEditable.length(); j++)
              {
                String str = paramAnonymousEditable.substring(j, j + 1);
                if ("0123456789".contains(str)) {
                  localStringBuilder.append(str);
                }
              }
              ProxySettingsActivity.access$502(ProxySettingsActivity.this, true);
              j = Utilities.parseInt(localStringBuilder.toString()).intValue();
              if ((j < 0) || (j > 65535) || (!paramAnonymousEditable.equals(localStringBuilder.toString()))) {
                if (j < 0) {
                  localEditTextBoldCursor.setText("0");
                }
              }
              while (i < 0) {
                for (;;)
                {
                  ProxySettingsActivity.access$502(ProxySettingsActivity.this, false);
                  ProxySettingsActivity.this.checkShareButton();
                  break;
                  if (j > 65535) {
                    localEditTextBoldCursor.setText("65535");
                  } else {
                    localEditTextBoldCursor.setText(localStringBuilder.toString());
                  }
                }
              }
              if (i <= localEditTextBoldCursor.length()) {}
              for (j = i;; j = localEditTextBoldCursor.length())
              {
                localEditTextBoldCursor.setSelection(j);
                break;
              }
            }
            
            public void beforeTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
            
            public void onTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
          });
          break label609;
        }
        if (i == 3)
        {
          this.inputFields[i].setInputType(129);
          this.inputFields[i].setTypeface(Typeface.DEFAULT);
          this.inputFields[i].setTransformationMethod(PasswordTransformationMethod.getInstance());
          break label609;
        }
        this.inputFields[i].setInputType(524289);
        break label609;
        this.inputFields[i].setHint(LocaleController.getString("UseProxyAddress", NUM));
        this.inputFields[i].setText(((SharedPreferences)localObject1).getString("proxy_ip", ""));
        break label656;
        this.inputFields[i].setHint(LocaleController.getString("UseProxyPassword", NUM));
        this.inputFields[i].setText(((SharedPreferences)localObject1).getString("proxy_pass", ""));
        break label656;
        this.inputFields[i].setHint(LocaleController.getString("UseProxyPort", NUM));
        this.inputFields[i].setText("" + ((SharedPreferences)localObject1).getInt("proxy_port", 1080));
        break label656;
        this.inputFields[i].setHint(LocaleController.getString("UseProxyUsername", NUM));
        this.inputFields[i].setText(((SharedPreferences)localObject1).getString("proxy_user", ""));
        break label656;
      }
    }
    this.bottomCell = new TextInfoPrivacyCell(paramContext);
    this.bottomCell.setBackgroundDrawable(Theme.getThemedDrawable(paramContext, NUM, "windowBackgroundGrayShadow"));
    this.bottomCell.setText(LocaleController.getString("UseProxyInfo", NUM));
    this.linearLayout2.addView(this.bottomCell, LayoutHelper.createLinear(-1, -2));
    this.useForCallsCell = new TextCheckCell(paramContext);
    this.useForCallsCell.setBackgroundDrawable(Theme.getSelectorDrawable(true));
    this.useForCallsCell.setTextAndCheck(LocaleController.getString("UseProxyForCalls", NUM), this.useProxyForCalls, false);
    this.useForCallsCell.setEnabled(this.useProxySettings);
    this.linearLayout2.addView(this.useForCallsCell, LayoutHelper.createLinear(-1, -2));
    this.useForCallsCell.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        paramAnonymousView = ProxySettingsActivity.this;
        if (!ProxySettingsActivity.this.useProxyForCalls) {}
        for (boolean bool = true;; bool = false)
        {
          ProxySettingsActivity.access$602(paramAnonymousView, bool);
          ProxySettingsActivity.this.useForCallsCell.setChecked(ProxySettingsActivity.this.useProxyForCalls);
          return;
        }
      }
    });
    localObject1 = new TextInfoPrivacyCell(paramContext);
    ((TextInfoPrivacyCell)localObject1).setBackgroundDrawable(Theme.getThemedDrawable(paramContext, NUM, "windowBackgroundGrayShadow"));
    ((TextInfoPrivacyCell)localObject1).setText(LocaleController.getString("UseProxyForCallsInfo", NUM));
    this.linearLayout2.addView((View)localObject1, LayoutHelper.createLinear(-1, -2));
    checkShareButton();
    return this.fragmentView;
  }
  
  public void didReceivedNotification(int paramInt1, int paramInt2, Object... paramVarArgs)
  {
    if ((paramInt1 != NotificationCenter.proxySettingsChanged) || (this.checkCell1 == null)) {}
    do
    {
      for (;;)
      {
        return;
        paramVarArgs = MessagesController.getGlobalMainSettings();
        this.useProxySettings = paramVarArgs.getBoolean("proxy_enabled", false);
        if (this.useProxySettings) {
          break;
        }
        this.checkCell1.setChecked(false);
      }
      this.checkCell1.setChecked(true);
      paramInt1 = 0;
    } while (paramInt1 >= 4);
    switch (paramInt1)
    {
    }
    for (;;)
    {
      paramInt1++;
      break;
      this.inputFields[paramInt1].setText(paramVarArgs.getString("proxy_ip", ""));
      continue;
      this.inputFields[paramInt1].setText(paramVarArgs.getString("proxy_pass", ""));
      continue;
      this.inputFields[paramInt1].setText("" + paramVarArgs.getInt("proxy_port", 1080));
      continue;
      this.inputFields[paramInt1].setText(paramVarArgs.getString("proxy_user", ""));
    }
  }
  
  public ThemeDescription[] getThemeDescriptions()
  {
    ArrayList localArrayList = new ArrayList();
    localArrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray"));
    localArrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault"));
    localArrayList.add(new ThemeDescription(this.scrollView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault"));
    localArrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon"));
    localArrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle"));
    localArrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector"));
    localArrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, "actionBarDefaultSearch"));
    localArrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, "actionBarDefaultSearchPlaceholder"));
    LinearLayout localLinearLayout = this.linearLayout2;
    Paint localPaint = Theme.dividerPaint;
    localArrayList.add(new ThemeDescription(localLinearLayout, 0, new Class[] { View.class }, localPaint, null, null, "divider"));
    if (this.inputFields != null) {
      for (i = 0; i < this.inputFields.length; i++)
      {
        localArrayList.add(new ThemeDescription((View)this.inputFields[i].getParent(), ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"));
        localArrayList.add(new ThemeDescription(this.inputFields[i], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
        localArrayList.add(new ThemeDescription(this.inputFields[i], ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"));
      }
    }
    localArrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
    localArrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"));
    localArrayList.add(new ThemeDescription(this.headerCell, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"));
    localArrayList.add(new ThemeDescription(this.headerCell, 0, new Class[] { HeaderCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlueHeader"));
    localArrayList.add(new ThemeDescription(this.sectionCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { ShadowSectionCell.class }, null, null, null, "windowBackgroundGrayShadow"));
    localArrayList.add(new ThemeDescription(this.bottomCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { TextInfoPrivacyCell.class }, null, null, null, "windowBackgroundGrayShadow"));
    localArrayList.add(new ThemeDescription(this.bottomCell, 0, new Class[] { TextInfoPrivacyCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteGrayText4"));
    localArrayList.add(new ThemeDescription(this.bottomCell, ThemeDescription.FLAG_LINKCOLOR, new Class[] { TextInfoPrivacyCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteLinkText"));
    for (int i = 0; i < this.dividers.size(); i++) {
      localArrayList.add(new ThemeDescription((View)this.dividers.get(i), ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "divider"));
    }
    localArrayList.add(new ThemeDescription(this.checkCell1, 0, new Class[] { TextCheckCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"));
    localArrayList.add(new ThemeDescription(this.checkCell1, 0, new Class[] { TextCheckCell.class }, new String[] { "checkBox" }, null, null, null, "switchThumb"));
    localArrayList.add(new ThemeDescription(this.checkCell1, 0, new Class[] { TextCheckCell.class }, new String[] { "checkBox" }, null, null, null, "switchTrack"));
    localArrayList.add(new ThemeDescription(this.checkCell1, 0, new Class[] { TextCheckCell.class }, new String[] { "checkBox" }, null, null, null, "switchThumbChecked"));
    localArrayList.add(new ThemeDescription(this.checkCell1, 0, new Class[] { TextCheckCell.class }, new String[] { "checkBox" }, null, null, null, "switchTrackChecked"));
    localArrayList.add(new ThemeDescription(this.checkCell1, ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, "windowBackgroundWhite"));
    localArrayList.add(new ThemeDescription(this.checkCell1, ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, "listSelectorSDK21"));
    return (ThemeDescription[])localArrayList.toArray(new ThemeDescription[localArrayList.size()]);
  }
  
  public boolean onFragmentCreate()
  {
    NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.proxySettingsChanged);
    return super.onFragmentCreate();
  }
  
  public void onFragmentDestroy()
  {
    NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.proxySettingsChanged);
    SharedPreferences.Editor localEditor = MessagesController.getGlobalMainSettings().edit();
    localEditor.putBoolean("proxy_enabled", this.useProxySettings);
    localEditor.putBoolean("proxy_enabled_calls", this.useProxyForCalls);
    String str1 = this.inputFields[0].getText().toString();
    String str2 = this.inputFields[3].getText().toString();
    String str3 = this.inputFields[2].getText().toString();
    int i = Utilities.parseInt(this.inputFields[1].getText().toString()).intValue();
    localEditor.putString("proxy_ip", str1);
    localEditor.putString("proxy_pass", str2);
    localEditor.putString("proxy_user", str3);
    localEditor.putInt("proxy_port", i);
    localEditor.commit();
    int j = 0;
    if (j < 3)
    {
      if (this.useProxySettings) {
        ConnectionsManager.native_setProxySettings(j, str1, i, str3, str2);
      }
      for (;;)
      {
        j++;
        break;
        ConnectionsManager.native_setProxySettings(j, "", 0, "", "");
      }
    }
    super.onFragmentDestroy();
  }
  
  public void onResume()
  {
    super.onResume();
    AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
  }
  
  protected void onTransitionAnimationEnd(boolean paramBoolean1, boolean paramBoolean2)
  {
    if ((paramBoolean1) && (!paramBoolean2))
    {
      this.inputFields[0].requestFocus();
      AndroidUtilities.showKeyboard(this.inputFields[0]);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/ProxySettingsActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */