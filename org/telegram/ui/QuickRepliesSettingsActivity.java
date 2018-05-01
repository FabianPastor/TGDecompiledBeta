package org.telegram.ui;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Paint;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.EditTextSettingsCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextDetailSettingsCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class QuickRepliesSettingsActivity
  extends BaseFragment
{
  private int explanationRow;
  private ListAdapter listAdapter;
  private RecyclerListView listView;
  private int reply1Row;
  private int reply2Row;
  private int reply3Row;
  private int reply4Row;
  private int rowCount;
  private int sectionHeaderRow;
  private EditTextSettingsCell[] textCells = new EditTextSettingsCell[4];
  
  public View createView(Context paramContext)
  {
    this.actionBar.setBackButtonImage(NUM);
    this.actionBar.setTitle(LocaleController.getString("VoipQuickReplies", NUM));
    if (AndroidUtilities.isTablet()) {
      this.actionBar.setOccupyStatusBar(false);
    }
    this.actionBar.setAllowOverlayTitle(true);
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (paramAnonymousInt == -1) {
          QuickRepliesSettingsActivity.this.finishFragment();
        }
      }
    });
    this.listAdapter = new ListAdapter(paramContext);
    this.fragmentView = new FrameLayout(paramContext);
    this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
    FrameLayout localFrameLayout = (FrameLayout)this.fragmentView;
    this.listView = new RecyclerListView(paramContext);
    this.listView.setVerticalScrollBarEnabled(false);
    this.listView.setLayoutManager(new LinearLayoutManager(paramContext, 1, false));
    localFrameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
    this.listView.setAdapter(this.listAdapter);
    this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
    {
      public void onItemClick(View paramAnonymousView, int paramAnonymousInt) {}
    });
    localFrameLayout.addView(this.actionBar);
    return this.fragmentView;
  }
  
  public ThemeDescription[] getThemeDescriptions()
  {
    ThemeDescription localThemeDescription1 = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[] { TextSettingsCell.class, TextSettingsCell.class, TextDetailSettingsCell.class }, null, null, null, "windowBackgroundWhite");
    ThemeDescription localThemeDescription2 = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray");
    ThemeDescription localThemeDescription3 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription4 = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription5 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
    ThemeDescription localThemeDescription6 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
    ThemeDescription localThemeDescription7 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
    ThemeDescription localThemeDescription8 = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
    RecyclerListView localRecyclerListView = this.listView;
    Paint localPaint = Theme.dividerPaint;
    return new ThemeDescription[] { localThemeDescription1, localThemeDescription2, localThemeDescription3, localThemeDescription4, localThemeDescription5, localThemeDescription6, localThemeDescription7, localThemeDescription8, new ThemeDescription(localRecyclerListView, 0, new Class[] { View.class }, localPaint, null, null, "divider"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { ShadowSectionCell.class }, null, null, null, "windowBackgroundGrayShadow"), new ThemeDescription(this.listView, 0, new Class[] { TextSettingsCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.listView, 0, new Class[] { TextSettingsCell.class }, new String[] { "valueTextView" }, null, null, null, "windowBackgroundWhiteValueText"), new ThemeDescription(this.listView, 0, new Class[] { HeaderCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlueHeader"), new ThemeDescription(this.listView, 0, new Class[] { TextDetailSettingsCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.listView, 0, new Class[] { TextDetailSettingsCell.class }, new String[] { "valueTextView" }, null, null, null, "windowBackgroundWhiteGrayText2") };
  }
  
  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    this.rowCount = 0;
    this.sectionHeaderRow = -1;
    int i = this.rowCount;
    this.rowCount = (i + 1);
    this.reply1Row = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.reply2Row = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.reply3Row = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.reply4Row = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.explanationRow = i;
    return true;
  }
  
  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    SharedPreferences.Editor localEditor = getParentActivity().getSharedPreferences("mainconfig", 0).edit();
    int i = 0;
    if (i < this.textCells.length)
    {
      if (this.textCells[i] != null)
      {
        String str = this.textCells[i].getTextView().getText().toString();
        if (TextUtils.isEmpty(str)) {
          break label102;
        }
        localEditor.putString("quick_reply_msg" + (i + 1), str);
      }
      for (;;)
      {
        i++;
        break;
        label102:
        localEditor.remove("quick_reply_msg" + (i + 1));
      }
    }
    localEditor.commit();
  }
  
  public void onResume()
  {
    super.onResume();
    if (this.listAdapter != null) {
      this.listAdapter.notifyDataSetChanged();
    }
  }
  
  private class ListAdapter
    extends RecyclerListView.SelectionAdapter
  {
    private Context mContext;
    
    public ListAdapter(Context paramContext)
    {
      this.mContext = paramContext;
    }
    
    public int getItemCount()
    {
      return QuickRepliesSettingsActivity.this.rowCount;
    }
    
    public int getItemViewType(int paramInt)
    {
      if (paramInt == QuickRepliesSettingsActivity.this.explanationRow) {
        paramInt = 0;
      }
      for (;;)
      {
        return paramInt;
        if ((paramInt == QuickRepliesSettingsActivity.this.reply1Row) || (paramInt == QuickRepliesSettingsActivity.this.reply2Row) || (paramInt == QuickRepliesSettingsActivity.this.reply3Row) || (paramInt == QuickRepliesSettingsActivity.this.reply4Row)) {
          paramInt = paramInt - QuickRepliesSettingsActivity.this.reply1Row + 9;
        } else if (paramInt == QuickRepliesSettingsActivity.this.sectionHeaderRow) {
          paramInt = 2;
        } else {
          paramInt = 1;
        }
      }
    }
    
    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      int i = paramViewHolder.getAdapterPosition();
      if ((i == QuickRepliesSettingsActivity.this.reply1Row) || (i == QuickRepliesSettingsActivity.this.reply2Row) || (i == QuickRepliesSettingsActivity.this.reply3Row) || (i == QuickRepliesSettingsActivity.this.reply4Row)) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      switch (paramViewHolder.getItemViewType())
      {
      }
      for (;;)
      {
        return;
        paramViewHolder = (TextInfoPrivacyCell)paramViewHolder.itemView;
        paramViewHolder.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
        paramViewHolder.setText(LocaleController.getString("VoipQuickRepliesExplain", NUM));
        continue;
        paramViewHolder = (TextSettingsCell)paramViewHolder.itemView;
        continue;
        paramViewHolder = (HeaderCell)paramViewHolder.itemView;
        if (paramInt == QuickRepliesSettingsActivity.this.sectionHeaderRow)
        {
          paramViewHolder.setText(LocaleController.getString("VoipQuickReplies", NUM));
          continue;
          EditTextSettingsCell localEditTextSettingsCell = (EditTextSettingsCell)paramViewHolder.itemView;
          String str = null;
          paramViewHolder = null;
          if (paramInt == QuickRepliesSettingsActivity.this.reply1Row)
          {
            str = "quick_reply_msg1";
            paramViewHolder = LocaleController.getString("QuickReplyDefault1", NUM);
          }
          for (;;)
          {
            localEditTextSettingsCell.setTextAndHint(QuickRepliesSettingsActivity.this.getParentActivity().getSharedPreferences("mainconfig", 0).getString(str, ""), paramViewHolder, true);
            break;
            if (paramInt == QuickRepliesSettingsActivity.this.reply2Row)
            {
              str = "quick_reply_msg2";
              paramViewHolder = LocaleController.getString("QuickReplyDefault2", NUM);
            }
            else if (paramInt == QuickRepliesSettingsActivity.this.reply3Row)
            {
              str = "quick_reply_msg3";
              paramViewHolder = LocaleController.getString("QuickReplyDefault3", NUM);
            }
            else if (paramInt == QuickRepliesSettingsActivity.this.reply4Row)
            {
              str = "quick_reply_msg4";
              paramViewHolder = LocaleController.getString("QuickReplyDefault4", NUM);
            }
          }
          ((TextCheckCell)paramViewHolder.itemView).setTextAndCheck(LocaleController.getString("AllowCustomQuickReply", NUM), QuickRepliesSettingsActivity.this.getParentActivity().getSharedPreferences("mainconfig", 0).getBoolean("quick_reply_allow_custom", true), false);
        }
      }
    }
    
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      Object localObject = null;
      paramViewGroup = (ViewGroup)localObject;
      switch (paramInt)
      {
      default: 
        paramViewGroup = (ViewGroup)localObject;
      }
      for (;;)
      {
        paramViewGroup.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
        return new RecyclerListView.Holder(paramViewGroup);
        paramViewGroup = new TextInfoPrivacyCell(this.mContext);
        continue;
        paramViewGroup = new TextSettingsCell(this.mContext);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        continue;
        paramViewGroup = new HeaderCell(this.mContext);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        continue;
        paramViewGroup = new EditTextSettingsCell(this.mContext);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        QuickRepliesSettingsActivity.this.textCells[(paramInt - 9)] = ((EditTextSettingsCell)paramViewGroup);
        continue;
        paramViewGroup = new TextCheckCell(this.mContext);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/QuickRepliesSettingsActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */