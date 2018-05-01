package org.telegram.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.MaxFileSizeCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckBoxCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class DataAutoDownloadActivity
  extends BaseFragment
{
  private static final int done_button = 1;
  private int currentType;
  private ListAdapter listAdapter;
  private RecyclerListView listView;
  private int mChannelsRow;
  private int mContactsRow;
  private int mGroupRow;
  private int mPrivateRow;
  private int mSizeRow;
  private long maxSize;
  private int mobileDataChannelDownloadMask;
  private int mobileDataDownloadMask;
  private int mobileDataGroupDownloadMask;
  private int mobileDataPrivateDownloadMask;
  private int mobileMaxSize;
  private int mobileSection2Row;
  private int mobileSectionRow;
  private int rChannelsRow;
  private int rContactsRow;
  private int rGroupRow;
  private int rPrivateRow;
  private int rSizeRow;
  private int roamingChannelDownloadMask;
  private int roamingDownloadMask;
  private int roamingGroupDownloadMask;
  private int roamingMaxSize;
  private int roamingPrivateDownloadMask;
  private int roamingSection2Row;
  private int roamingSectionRow;
  private int rowCount;
  private int wChannelsRow;
  private int wContactsRow;
  private int wGroupRow;
  private int wPrivateRow;
  private int wSizeRow;
  private int wifiChannelDownloadMask;
  private int wifiDownloadMask;
  private int wifiGroupDownloadMask;
  private int wifiMaxSize;
  private int wifiPrivateDownloadMask;
  private int wifiSection2Row;
  private int wifiSectionRow;
  
  public DataAutoDownloadActivity(int paramInt)
  {
    this.currentType = paramInt;
    if (this.currentType == 64) {
      this.maxSize = 8388608L;
    }
    for (;;)
    {
      this.mobileDataDownloadMask = DownloadController.getInstance(this.currentAccount).mobileDataDownloadMask[0];
      this.mobileDataPrivateDownloadMask = DownloadController.getInstance(this.currentAccount).mobileDataDownloadMask[1];
      this.mobileDataGroupDownloadMask = DownloadController.getInstance(this.currentAccount).mobileDataDownloadMask[2];
      this.mobileDataChannelDownloadMask = DownloadController.getInstance(this.currentAccount).mobileDataDownloadMask[3];
      this.wifiDownloadMask = DownloadController.getInstance(this.currentAccount).wifiDownloadMask[0];
      this.wifiPrivateDownloadMask = DownloadController.getInstance(this.currentAccount).wifiDownloadMask[1];
      this.wifiGroupDownloadMask = DownloadController.getInstance(this.currentAccount).wifiDownloadMask[2];
      this.wifiChannelDownloadMask = DownloadController.getInstance(this.currentAccount).wifiDownloadMask[3];
      this.roamingDownloadMask = DownloadController.getInstance(this.currentAccount).roamingDownloadMask[0];
      this.roamingPrivateDownloadMask = DownloadController.getInstance(this.currentAccount).roamingDownloadMask[1];
      this.roamingGroupDownloadMask = DownloadController.getInstance(this.currentAccount).roamingDownloadMask[2];
      this.roamingChannelDownloadMask = DownloadController.getInstance(this.currentAccount).roamingDownloadMask[2];
      this.mobileMaxSize = DownloadController.getInstance(this.currentAccount).mobileMaxFileSize[DownloadController.maskToIndex(this.currentType)];
      this.wifiMaxSize = DownloadController.getInstance(this.currentAccount).wifiMaxFileSize[DownloadController.maskToIndex(this.currentType)];
      this.roamingMaxSize = DownloadController.getInstance(this.currentAccount).roamingMaxFileSize[DownloadController.maskToIndex(this.currentType)];
      return;
      if (this.currentType == 32) {
        this.maxSize = 10485760L;
      } else {
        this.maxSize = 1610612736L;
      }
    }
  }
  
  private int getMaskForRow(int paramInt)
  {
    if (paramInt == this.mContactsRow) {
      paramInt = this.mobileDataDownloadMask;
    }
    for (;;)
    {
      return paramInt;
      if (paramInt == this.mPrivateRow) {
        paramInt = this.mobileDataPrivateDownloadMask;
      } else if (paramInt == this.mGroupRow) {
        paramInt = this.mobileDataGroupDownloadMask;
      } else if (paramInt == this.mChannelsRow) {
        paramInt = this.mobileDataChannelDownloadMask;
      } else if (paramInt == this.wContactsRow) {
        paramInt = this.wifiDownloadMask;
      } else if (paramInt == this.wPrivateRow) {
        paramInt = this.wifiPrivateDownloadMask;
      } else if (paramInt == this.wGroupRow) {
        paramInt = this.wifiGroupDownloadMask;
      } else if (paramInt == this.wChannelsRow) {
        paramInt = this.wifiChannelDownloadMask;
      } else if (paramInt == this.rContactsRow) {
        paramInt = this.roamingDownloadMask;
      } else if (paramInt == this.rPrivateRow) {
        paramInt = this.roamingPrivateDownloadMask;
      } else if (paramInt == this.rGroupRow) {
        paramInt = this.roamingGroupDownloadMask;
      } else if (paramInt == this.rChannelsRow) {
        paramInt = this.roamingChannelDownloadMask;
      } else {
        paramInt = 0;
      }
    }
  }
  
  private void setMaskForRow(int paramInt1, int paramInt2)
  {
    if (paramInt1 == this.mContactsRow) {
      this.mobileDataDownloadMask = paramInt2;
    }
    for (;;)
    {
      return;
      if (paramInt1 == this.mPrivateRow) {
        this.mobileDataPrivateDownloadMask = paramInt2;
      } else if (paramInt1 == this.mGroupRow) {
        this.mobileDataGroupDownloadMask = paramInt2;
      } else if (paramInt1 == this.mChannelsRow) {
        this.mobileDataChannelDownloadMask = paramInt2;
      } else if (paramInt1 == this.wContactsRow) {
        this.wifiDownloadMask = paramInt2;
      } else if (paramInt1 == this.wPrivateRow) {
        this.wifiPrivateDownloadMask = paramInt2;
      } else if (paramInt1 == this.wGroupRow) {
        this.wifiGroupDownloadMask = paramInt2;
      } else if (paramInt1 == this.wChannelsRow) {
        this.wifiChannelDownloadMask = paramInt2;
      } else if (paramInt1 == this.rContactsRow) {
        this.roamingDownloadMask = paramInt2;
      } else if (paramInt1 == this.rPrivateRow) {
        this.roamingPrivateDownloadMask = paramInt2;
      } else if (paramInt1 == this.rGroupRow) {
        this.roamingGroupDownloadMask = paramInt2;
      } else if (paramInt1 == this.rChannelsRow) {
        this.roamingChannelDownloadMask = paramInt2;
      }
    }
  }
  
  public View createView(Context paramContext)
  {
    this.actionBar.setBackButtonImage(NUM);
    if (this.currentType == 1) {
      this.actionBar.setTitle(LocaleController.getString("LocalPhotoCache", NUM));
    }
    for (;;)
    {
      if (AndroidUtilities.isTablet()) {
        this.actionBar.setOccupyStatusBar(false);
      }
      this.actionBar.setAllowOverlayTitle(true);
      this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
      {
        public void onItemClick(int paramAnonymousInt)
        {
          if (paramAnonymousInt == -1) {
            DataAutoDownloadActivity.this.finishFragment();
          }
          for (;;)
          {
            return;
            if (paramAnonymousInt == 1)
            {
              DownloadController.getInstance(DataAutoDownloadActivity.this.currentAccount).mobileDataDownloadMask[0] = DataAutoDownloadActivity.this.mobileDataDownloadMask;
              DownloadController.getInstance(DataAutoDownloadActivity.this.currentAccount).mobileDataDownloadMask[1] = DataAutoDownloadActivity.this.mobileDataPrivateDownloadMask;
              DownloadController.getInstance(DataAutoDownloadActivity.this.currentAccount).mobileDataDownloadMask[2] = DataAutoDownloadActivity.this.mobileDataGroupDownloadMask;
              DownloadController.getInstance(DataAutoDownloadActivity.this.currentAccount).mobileDataDownloadMask[3] = DataAutoDownloadActivity.this.mobileDataChannelDownloadMask;
              DownloadController.getInstance(DataAutoDownloadActivity.this.currentAccount).wifiDownloadMask[0] = DataAutoDownloadActivity.this.wifiDownloadMask;
              DownloadController.getInstance(DataAutoDownloadActivity.this.currentAccount).wifiDownloadMask[1] = DataAutoDownloadActivity.this.wifiPrivateDownloadMask;
              DownloadController.getInstance(DataAutoDownloadActivity.this.currentAccount).wifiDownloadMask[2] = DataAutoDownloadActivity.this.wifiGroupDownloadMask;
              DownloadController.getInstance(DataAutoDownloadActivity.this.currentAccount).wifiDownloadMask[3] = DataAutoDownloadActivity.this.wifiChannelDownloadMask;
              DownloadController.getInstance(DataAutoDownloadActivity.this.currentAccount).roamingDownloadMask[0] = DataAutoDownloadActivity.this.roamingDownloadMask;
              DownloadController.getInstance(DataAutoDownloadActivity.this.currentAccount).roamingDownloadMask[1] = DataAutoDownloadActivity.this.roamingPrivateDownloadMask;
              DownloadController.getInstance(DataAutoDownloadActivity.this.currentAccount).roamingDownloadMask[2] = DataAutoDownloadActivity.this.roamingGroupDownloadMask;
              DownloadController.getInstance(DataAutoDownloadActivity.this.currentAccount).roamingDownloadMask[3] = DataAutoDownloadActivity.this.roamingChannelDownloadMask;
              DownloadController.getInstance(DataAutoDownloadActivity.this.currentAccount).mobileMaxFileSize[DownloadController.maskToIndex(DataAutoDownloadActivity.this.currentType)] = DataAutoDownloadActivity.this.mobileMaxSize;
              DownloadController.getInstance(DataAutoDownloadActivity.this.currentAccount).wifiMaxFileSize[DownloadController.maskToIndex(DataAutoDownloadActivity.this.currentType)] = DataAutoDownloadActivity.this.wifiMaxSize;
              DownloadController.getInstance(DataAutoDownloadActivity.this.currentAccount).roamingMaxFileSize[DownloadController.maskToIndex(DataAutoDownloadActivity.this.currentType)] = DataAutoDownloadActivity.this.roamingMaxSize;
              SharedPreferences.Editor localEditor = MessagesController.getMainSettings(DataAutoDownloadActivity.this.currentAccount).edit();
              paramAnonymousInt = 0;
              if (paramAnonymousInt < 4)
              {
                StringBuilder localStringBuilder = new StringBuilder().append("mobileDataDownloadMask");
                if (paramAnonymousInt != 0)
                {
                  localObject = Integer.valueOf(paramAnonymousInt);
                  label421:
                  localEditor.putInt(localObject, DownloadController.getInstance(DataAutoDownloadActivity.this.currentAccount).mobileDataDownloadMask[paramAnonymousInt]);
                  localStringBuilder = new StringBuilder().append("wifiDownloadMask");
                  if (paramAnonymousInt == 0) {
                    break label573;
                  }
                  localObject = Integer.valueOf(paramAnonymousInt);
                  label475:
                  localEditor.putInt(localObject, DownloadController.getInstance(DataAutoDownloadActivity.this.currentAccount).wifiDownloadMask[paramAnonymousInt]);
                  localStringBuilder = new StringBuilder().append("roamingDownloadMask");
                  if (paramAnonymousInt == 0) {
                    break label580;
                  }
                }
                label573:
                label580:
                for (Object localObject = Integer.valueOf(paramAnonymousInt);; localObject = "")
                {
                  localEditor.putInt(localObject, DownloadController.getInstance(DataAutoDownloadActivity.this.currentAccount).roamingDownloadMask[paramAnonymousInt]);
                  paramAnonymousInt++;
                  break;
                  localObject = "";
                  break label421;
                  localObject = "";
                  break label475;
                }
              }
              localEditor.putInt("mobileMaxDownloadSize" + DownloadController.maskToIndex(DataAutoDownloadActivity.this.currentType), DataAutoDownloadActivity.this.mobileMaxSize);
              localEditor.putInt("wifiMaxDownloadSize" + DownloadController.maskToIndex(DataAutoDownloadActivity.this.currentType), DataAutoDownloadActivity.this.wifiMaxSize);
              localEditor.putInt("roamingMaxDownloadSize" + DownloadController.maskToIndex(DataAutoDownloadActivity.this.currentType), DataAutoDownloadActivity.this.roamingMaxSize);
              localEditor.commit();
              DownloadController.getInstance(DataAutoDownloadActivity.this.currentAccount).checkAutodownloadSettings();
              DataAutoDownloadActivity.this.finishFragment();
            }
          }
        }
      });
      this.actionBar.createMenu().addItemWithWidth(1, NUM, AndroidUtilities.dp(56.0F));
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
        public void onItemClick(View paramAnonymousView, int paramAnonymousInt)
        {
          if (!(paramAnonymousView instanceof TextCheckBoxCell)) {
            return;
          }
          int i = DataAutoDownloadActivity.this.getMaskForRow(paramAnonymousInt);
          paramAnonymousView = (TextCheckBoxCell)paramAnonymousView;
          boolean bool;
          if (!paramAnonymousView.isChecked())
          {
            bool = true;
            label32:
            if (!bool) {
              break label71;
            }
            i |= DataAutoDownloadActivity.this.currentType;
          }
          for (;;)
          {
            DataAutoDownloadActivity.this.setMaskForRow(paramAnonymousInt, i);
            paramAnonymousView.setChecked(bool);
            break;
            bool = false;
            break label32;
            label71:
            i &= (DataAutoDownloadActivity.this.currentType ^ 0xFFFFFFFF);
          }
        }
      });
      localFrameLayout.addView(this.actionBar);
      return this.fragmentView;
      if (this.currentType == 2) {
        this.actionBar.setTitle(LocaleController.getString("AudioAutodownload", NUM));
      } else if (this.currentType == 64) {
        this.actionBar.setTitle(LocaleController.getString("VideoMessagesAutodownload", NUM));
      } else if (this.currentType == 4) {
        this.actionBar.setTitle(LocaleController.getString("LocalVideoCache", NUM));
      } else if (this.currentType == 8) {
        this.actionBar.setTitle(LocaleController.getString("FilesDataUsage", NUM));
      } else if (this.currentType == 16) {
        this.actionBar.setTitle(LocaleController.getString("AttachMusic", NUM));
      } else if (this.currentType == 32) {
        this.actionBar.setTitle(LocaleController.getString("LocalGifCache", NUM));
      }
    }
  }
  
  public ThemeDescription[] getThemeDescriptions()
  {
    ThemeDescription localThemeDescription1 = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[] { TextCheckBoxCell.class, MaxFileSizeCell.class, HeaderCell.class }, null, null, null, "windowBackgroundWhite");
    ThemeDescription localThemeDescription2 = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray");
    ThemeDescription localThemeDescription3 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription4 = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription5 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
    ThemeDescription localThemeDescription6 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
    ThemeDescription localThemeDescription7 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
    ThemeDescription localThemeDescription8 = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
    RecyclerListView localRecyclerListView = this.listView;
    Paint localPaint = Theme.dividerPaint;
    return new ThemeDescription[] { localThemeDescription1, localThemeDescription2, localThemeDescription3, localThemeDescription4, localThemeDescription5, localThemeDescription6, localThemeDescription7, localThemeDescription8, new ThemeDescription(localRecyclerListView, 0, new Class[] { View.class }, localPaint, null, null, "divider"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { ShadowSectionCell.class }, null, null, null, "windowBackgroundGrayShadow"), new ThemeDescription(this.listView, 0, new Class[] { MaxFileSizeCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.listView, 0, new Class[] { MaxFileSizeCell.class }, new String[] { "sizeTextView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.listView, 0, new Class[] { TextCheckBoxCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.listView, 0, new Class[] { TextCheckBoxCell.class }, null, null, null, "checkboxSquareUnchecked"), new ThemeDescription(this.listView, 0, new Class[] { TextCheckBoxCell.class }, null, null, null, "checkboxSquareDisabled"), new ThemeDescription(this.listView, 0, new Class[] { TextCheckBoxCell.class }, null, null, null, "checkboxSquareBackground"), new ThemeDescription(this.listView, 0, new Class[] { TextCheckBoxCell.class }, null, null, null, "checkboxSquareCheck"), new ThemeDescription(this.listView, 0, new Class[] { HeaderCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlueHeader") };
  }
  
  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    this.rowCount = 0;
    int i = this.rowCount;
    this.rowCount = (i + 1);
    this.mobileSectionRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.mContactsRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.mPrivateRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.mGroupRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.mChannelsRow = i;
    if (this.currentType != 1)
    {
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.mSizeRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.mobileSection2Row = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.wifiSectionRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.wContactsRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.wPrivateRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.wGroupRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.wChannelsRow = i;
      if (this.currentType == 1) {
        break label401;
      }
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.wSizeRow = i;
      label247:
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.wifiSection2Row = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.roamingSectionRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.rContactsRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.rPrivateRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.rGroupRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.rChannelsRow = i;
      if (this.currentType == 1) {
        break label409;
      }
      i = this.rowCount;
      this.rowCount = (i + 1);
    }
    label401:
    label409:
    for (this.rSizeRow = i;; this.rSizeRow = -1)
    {
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.roamingSection2Row = i;
      return true;
      this.mSizeRow = -1;
      break;
      this.wSizeRow = -1;
      break label247;
    }
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
      return DataAutoDownloadActivity.this.rowCount;
    }
    
    public int getItemViewType(int paramInt)
    {
      if ((paramInt == DataAutoDownloadActivity.this.mobileSection2Row) || (paramInt == DataAutoDownloadActivity.this.wifiSection2Row) || (paramInt == DataAutoDownloadActivity.this.roamingSection2Row)) {
        paramInt = 0;
      }
      for (;;)
      {
        return paramInt;
        if ((paramInt == DataAutoDownloadActivity.this.mobileSectionRow) || (paramInt == DataAutoDownloadActivity.this.wifiSectionRow) || (paramInt == DataAutoDownloadActivity.this.roamingSectionRow)) {
          paramInt = 2;
        } else if ((paramInt == DataAutoDownloadActivity.this.wSizeRow) || (paramInt == DataAutoDownloadActivity.this.mSizeRow) || (paramInt == DataAutoDownloadActivity.this.rSizeRow)) {
          paramInt = 3;
        } else {
          paramInt = 1;
        }
      }
    }
    
    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      int i = paramViewHolder.getAdapterPosition();
      if ((i != DataAutoDownloadActivity.this.mSizeRow) && (i != DataAutoDownloadActivity.this.rSizeRow) && (i != DataAutoDownloadActivity.this.wSizeRow) && (i != DataAutoDownloadActivity.this.mobileSectionRow) && (i != DataAutoDownloadActivity.this.wifiSectionRow) && (i != DataAutoDownloadActivity.this.roamingSectionRow) && (i != DataAutoDownloadActivity.this.mobileSection2Row) && (i != DataAutoDownloadActivity.this.wifiSection2Row) && (i != DataAutoDownloadActivity.this.roamingSection2Row)) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      boolean bool1 = false;
      boolean bool2 = false;
      boolean bool3 = false;
      boolean bool4 = true;
      switch (paramViewHolder.getItemViewType())
      {
      }
      for (;;)
      {
        return;
        if ((paramInt == DataAutoDownloadActivity.this.mobileSection2Row) || (paramInt == DataAutoDownloadActivity.this.wifiSection2Row))
        {
          paramViewHolder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
        }
        else
        {
          paramViewHolder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
          continue;
          paramViewHolder = (TextCheckBoxCell)paramViewHolder.itemView;
          String str;
          if ((paramInt == DataAutoDownloadActivity.this.mContactsRow) || (paramInt == DataAutoDownloadActivity.this.wContactsRow) || (paramInt == DataAutoDownloadActivity.this.rContactsRow))
          {
            str = LocaleController.getString("AutodownloadContacts", NUM);
            if ((DataAutoDownloadActivity.this.getMaskForRow(paramInt) & DataAutoDownloadActivity.this.currentType) != 0) {
              bool3 = true;
            }
            paramViewHolder.setTextAndCheck(str, bool3, true);
          }
          else if ((paramInt == DataAutoDownloadActivity.this.mPrivateRow) || (paramInt == DataAutoDownloadActivity.this.wPrivateRow) || (paramInt == DataAutoDownloadActivity.this.rPrivateRow))
          {
            str = LocaleController.getString("AutodownloadPrivateChats", NUM);
            bool3 = bool1;
            if ((DataAutoDownloadActivity.this.getMaskForRow(paramInt) & DataAutoDownloadActivity.this.currentType) != 0) {
              bool3 = true;
            }
            paramViewHolder.setTextAndCheck(str, bool3, true);
          }
          else
          {
            if ((paramInt == DataAutoDownloadActivity.this.mChannelsRow) || (paramInt == DataAutoDownloadActivity.this.wChannelsRow) || (paramInt == DataAutoDownloadActivity.this.rChannelsRow))
            {
              str = LocaleController.getString("AutodownloadChannels", NUM);
              if ((DataAutoDownloadActivity.this.getMaskForRow(paramInt) & DataAutoDownloadActivity.this.currentType) != 0)
              {
                bool3 = true;
                label336:
                if (DataAutoDownloadActivity.this.mSizeRow == -1) {
                  break label366;
                }
              }
              for (;;)
              {
                paramViewHolder.setTextAndCheck(str, bool3, bool4);
                break;
                bool3 = false;
                break label336;
                label366:
                bool4 = false;
              }
            }
            if ((paramInt == DataAutoDownloadActivity.this.mGroupRow) || (paramInt == DataAutoDownloadActivity.this.wGroupRow) || (paramInt == DataAutoDownloadActivity.this.rGroupRow))
            {
              str = LocaleController.getString("AutodownloadGroupChats", NUM);
              bool3 = bool2;
              if ((DataAutoDownloadActivity.this.getMaskForRow(paramInt) & DataAutoDownloadActivity.this.currentType) != 0) {
                bool3 = true;
              }
              paramViewHolder.setTextAndCheck(str, bool3, true);
              continue;
              paramViewHolder = (HeaderCell)paramViewHolder.itemView;
              if (paramInt == DataAutoDownloadActivity.this.mobileSectionRow)
              {
                paramViewHolder.setText(LocaleController.getString("WhenUsingMobileData", NUM));
              }
              else if (paramInt == DataAutoDownloadActivity.this.wifiSectionRow)
              {
                paramViewHolder.setText(LocaleController.getString("WhenConnectedOnWiFi", NUM));
              }
              else if (paramInt == DataAutoDownloadActivity.this.roamingSectionRow)
              {
                paramViewHolder.setText(LocaleController.getString("WhenRoaming", NUM));
                continue;
                paramViewHolder = (MaxFileSizeCell)paramViewHolder.itemView;
                if (paramInt == DataAutoDownloadActivity.this.mSizeRow)
                {
                  paramViewHolder.setSize(DataAutoDownloadActivity.this.mobileMaxSize, DataAutoDownloadActivity.this.maxSize);
                  paramViewHolder.setTag(Integer.valueOf(0));
                }
                else if (paramInt == DataAutoDownloadActivity.this.wSizeRow)
                {
                  paramViewHolder.setSize(DataAutoDownloadActivity.this.wifiMaxSize, DataAutoDownloadActivity.this.maxSize);
                  paramViewHolder.setTag(Integer.valueOf(1));
                }
                else if (paramInt == DataAutoDownloadActivity.this.rSizeRow)
                {
                  paramViewHolder.setSize(DataAutoDownloadActivity.this.roamingMaxSize, DataAutoDownloadActivity.this.maxSize);
                  paramViewHolder.setTag(Integer.valueOf(2));
                }
              }
            }
          }
        }
      }
    }
    
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      paramViewGroup = null;
      switch (paramInt)
      {
      }
      for (;;)
      {
        paramViewGroup.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
        return new RecyclerListView.Holder(paramViewGroup);
        paramViewGroup = new ShadowSectionCell(this.mContext);
        continue;
        paramViewGroup = new TextCheckBoxCell(this.mContext);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        continue;
        paramViewGroup = new HeaderCell(this.mContext);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        continue;
        paramViewGroup = new MaxFileSizeCell(this.mContext)
        {
          protected void didChangedSizeValue(int paramAnonymousInt)
          {
            Integer localInteger = (Integer)getTag();
            if (localInteger.intValue() == 0) {
              DataAutoDownloadActivity.access$2602(DataAutoDownloadActivity.this, paramAnonymousInt);
            }
            for (;;)
            {
              return;
              if (localInteger.intValue() == 1) {
                DataAutoDownloadActivity.access$2802(DataAutoDownloadActivity.this, paramAnonymousInt);
              } else if (localInteger.intValue() == 2) {
                DataAutoDownloadActivity.access$3002(DataAutoDownloadActivity.this, paramAnonymousInt);
              }
            }
          }
        };
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/DataAutoDownloadActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */