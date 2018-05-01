package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class DataSettingsActivity
  extends BaseFragment
{
  private AnimatorSet animatorSet;
  private int autoDownloadMediaRow;
  private int callsSection2Row;
  private int callsSectionRow;
  private int enableAllStreamInfoRow;
  private int enableAllStreamRow;
  private int enableCacheStreamRow;
  private int enableStreamRow;
  private int filesRow;
  private int gifsRow;
  private ListAdapter listAdapter;
  private RecyclerListView listView;
  private int mediaDownloadSection2Row;
  private int mediaDownloadSectionRow;
  private int mobileUsageRow;
  private int musicRow;
  private int photosRow;
  private int proxyRow;
  private int proxySection2Row;
  private int proxySectionRow;
  private int quickRepliesRow;
  private int resetDownloadRow;
  private int roamingUsageRow;
  private int rowCount;
  private int storageUsageRow;
  private int streamSectionRow;
  private int usageSection2Row;
  private int usageSectionRow;
  private int useLessDataForCallsRow;
  private int videoMessagesRow;
  private int videosRow;
  private int voiceMessagesRow;
  private int wifiUsageRow;
  
  private void updateAutodownloadRows(boolean paramBoolean)
  {
    int i = this.listView.getChildCount();
    ArrayList localArrayList = new ArrayList();
    int j = 0;
    if (j < i)
    {
      Object localObject = this.listView.getChildAt(j);
      localObject = (RecyclerListView.Holder)this.listView.getChildViewHolder((View)localObject);
      ((RecyclerListView.Holder)localObject).getItemViewType();
      int k = ((RecyclerListView.Holder)localObject).getAdapterPosition();
      if ((k >= this.photosRow) && (k <= this.gifsRow)) {
        ((TextSettingsCell)((RecyclerListView.Holder)localObject).itemView).setEnabled(DownloadController.getInstance(this.currentAccount).globalAutodownloadEnabled, localArrayList);
      }
      for (;;)
      {
        j++;
        break;
        if ((paramBoolean) && (k == this.autoDownloadMediaRow)) {
          ((TextCheckCell)((RecyclerListView.Holder)localObject).itemView).setChecked(true);
        }
      }
    }
    if (!localArrayList.isEmpty())
    {
      if (this.animatorSet != null) {
        this.animatorSet.cancel();
      }
      this.animatorSet = new AnimatorSet();
      this.animatorSet.playTogether(localArrayList);
      this.animatorSet.addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          if (paramAnonymousAnimator.equals(DataSettingsActivity.this.animatorSet)) {
            DataSettingsActivity.access$3302(DataSettingsActivity.this, null);
          }
        }
      });
      this.animatorSet.setDuration(150L);
      this.animatorSet.start();
    }
  }
  
  public View createView(Context paramContext)
  {
    this.actionBar.setBackButtonImage(NUM);
    this.actionBar.setTitle(LocaleController.getString("DataSettings", NUM));
    if (AndroidUtilities.isTablet()) {
      this.actionBar.setOccupyStatusBar(false);
    }
    this.actionBar.setAllowOverlayTitle(true);
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (paramAnonymousInt == -1) {
          DataSettingsActivity.this.finishFragment();
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
      public void onItemClick(View paramAnonymousView, final int paramAnonymousInt)
      {
        if ((paramAnonymousInt == DataSettingsActivity.this.photosRow) || (paramAnonymousInt == DataSettingsActivity.this.voiceMessagesRow) || (paramAnonymousInt == DataSettingsActivity.this.videoMessagesRow) || (paramAnonymousInt == DataSettingsActivity.this.videosRow) || (paramAnonymousInt == DataSettingsActivity.this.filesRow) || (paramAnonymousInt == DataSettingsActivity.this.musicRow) || (paramAnonymousInt == DataSettingsActivity.this.gifsRow)) {
          if (DownloadController.getInstance(DataSettingsActivity.this.currentAccount).globalAutodownloadEnabled) {}
        }
        for (;;)
        {
          return;
          if (paramAnonymousInt == DataSettingsActivity.this.photosRow)
          {
            DataSettingsActivity.this.presentFragment(new DataAutoDownloadActivity(1));
          }
          else if (paramAnonymousInt == DataSettingsActivity.this.voiceMessagesRow)
          {
            DataSettingsActivity.this.presentFragment(new DataAutoDownloadActivity(2));
          }
          else if (paramAnonymousInt == DataSettingsActivity.this.videoMessagesRow)
          {
            DataSettingsActivity.this.presentFragment(new DataAutoDownloadActivity(64));
          }
          else if (paramAnonymousInt == DataSettingsActivity.this.videosRow)
          {
            DataSettingsActivity.this.presentFragment(new DataAutoDownloadActivity(4));
          }
          else if (paramAnonymousInt == DataSettingsActivity.this.filesRow)
          {
            DataSettingsActivity.this.presentFragment(new DataAutoDownloadActivity(8));
          }
          else if (paramAnonymousInt == DataSettingsActivity.this.musicRow)
          {
            DataSettingsActivity.this.presentFragment(new DataAutoDownloadActivity(16));
          }
          else if (paramAnonymousInt == DataSettingsActivity.this.gifsRow)
          {
            DataSettingsActivity.this.presentFragment(new DataAutoDownloadActivity(32));
            continue;
            if (paramAnonymousInt == DataSettingsActivity.this.resetDownloadRow)
            {
              if (DataSettingsActivity.this.getParentActivity() != null)
              {
                paramAnonymousView = new AlertDialog.Builder(DataSettingsActivity.this.getParentActivity());
                paramAnonymousView.setTitle(LocaleController.getString("AppName", NUM));
                paramAnonymousView.setMessage(LocaleController.getString("ResetAutomaticMediaDownloadAlert", NUM));
                paramAnonymousView.setPositiveButton(LocaleController.getString("OK", NUM), new DialogInterface.OnClickListener()
                {
                  public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
                  {
                    SharedPreferences.Editor localEditor = MessagesController.getMainSettings(DataSettingsActivity.this.currentAccount).edit();
                    DownloadController localDownloadController = DownloadController.getInstance(DataSettingsActivity.this.currentAccount);
                    paramAnonymous2Int = 0;
                    if (paramAnonymous2Int < 4)
                    {
                      localDownloadController.mobileDataDownloadMask[paramAnonymous2Int] = 115;
                      localDownloadController.wifiDownloadMask[paramAnonymous2Int] = 115;
                      localDownloadController.roamingDownloadMask[paramAnonymous2Int] = 0;
                      StringBuilder localStringBuilder = new StringBuilder().append("mobileDataDownloadMask");
                      if (paramAnonymous2Int != 0)
                      {
                        paramAnonymous2DialogInterface = Integer.valueOf(paramAnonymous2Int);
                        label90:
                        localEditor.putInt(paramAnonymous2DialogInterface, localDownloadController.mobileDataDownloadMask[paramAnonymous2Int]);
                        localStringBuilder = new StringBuilder().append("wifiDownloadMask");
                        if (paramAnonymous2Int == 0) {
                          break label217;
                        }
                        paramAnonymous2DialogInterface = Integer.valueOf(paramAnonymous2Int);
                        label136:
                        localEditor.putInt(paramAnonymous2DialogInterface, localDownloadController.wifiDownloadMask[paramAnonymous2Int]);
                        localStringBuilder = new StringBuilder().append("roamingDownloadMask");
                        if (paramAnonymous2Int == 0) {
                          break label223;
                        }
                      }
                      label217:
                      label223:
                      for (paramAnonymous2DialogInterface = Integer.valueOf(paramAnonymous2Int);; paramAnonymous2DialogInterface = "")
                      {
                        localEditor.putInt(paramAnonymous2DialogInterface, localDownloadController.roamingDownloadMask[paramAnonymous2Int]);
                        paramAnonymous2Int++;
                        break;
                        paramAnonymous2DialogInterface = "";
                        break label90;
                        paramAnonymous2DialogInterface = "";
                        break label136;
                      }
                    }
                    int i = 0;
                    if (i < 7)
                    {
                      if (i == 1) {
                        paramAnonymous2Int = 2097152;
                      }
                      for (;;)
                      {
                        localDownloadController.mobileMaxFileSize[i] = paramAnonymous2Int;
                        localDownloadController.wifiMaxFileSize[i] = paramAnonymous2Int;
                        localDownloadController.roamingMaxFileSize[i] = paramAnonymous2Int;
                        localEditor.putInt("mobileMaxDownloadSize" + i, paramAnonymous2Int);
                        localEditor.putInt("wifiMaxDownloadSize" + i, paramAnonymous2Int);
                        localEditor.putInt("roamingMaxDownloadSize" + i, paramAnonymous2Int);
                        i++;
                        break;
                        if (i == 6) {
                          paramAnonymous2Int = 5242880;
                        } else {
                          paramAnonymous2Int = 10485760;
                        }
                      }
                    }
                    if (!DownloadController.getInstance(DataSettingsActivity.this.currentAccount).globalAutodownloadEnabled)
                    {
                      DownloadController.getInstance(DataSettingsActivity.this.currentAccount).globalAutodownloadEnabled = true;
                      localEditor.putBoolean("globalAutodownloadEnabled", DownloadController.getInstance(DataSettingsActivity.this.currentAccount).globalAutodownloadEnabled);
                      DataSettingsActivity.this.updateAutodownloadRows(true);
                    }
                    localEditor.commit();
                    DownloadController.getInstance(DataSettingsActivity.this.currentAccount).checkAutodownloadSettings();
                  }
                });
                paramAnonymousView.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
                paramAnonymousView.show();
              }
            }
            else
            {
              Object localObject1;
              if (paramAnonymousInt == DataSettingsActivity.this.autoDownloadMediaRow)
              {
                localObject1 = DownloadController.getInstance(DataSettingsActivity.this.currentAccount);
                if (!DownloadController.getInstance(DataSettingsActivity.this.currentAccount).globalAutodownloadEnabled) {}
                for (boolean bool = true;; bool = false)
                {
                  ((DownloadController)localObject1).globalAutodownloadEnabled = bool;
                  MessagesController.getMainSettings(DataSettingsActivity.this.currentAccount).edit().putBoolean("globalAutodownloadEnabled", DownloadController.getInstance(DataSettingsActivity.this.currentAccount).globalAutodownloadEnabled).commit();
                  ((TextCheckCell)paramAnonymousView).setChecked(DownloadController.getInstance(DataSettingsActivity.this.currentAccount).globalAutodownloadEnabled);
                  DataSettingsActivity.this.updateAutodownloadRows(false);
                  break;
                }
              }
              if (paramAnonymousInt == DataSettingsActivity.this.storageUsageRow)
              {
                DataSettingsActivity.this.presentFragment(new CacheControlActivity());
              }
              else if (paramAnonymousInt == DataSettingsActivity.this.useLessDataForCallsRow)
              {
                final Object localObject2 = MessagesController.getGlobalMainSettings();
                Activity localActivity = DataSettingsActivity.this.getParentActivity();
                localObject1 = DataSettingsActivity.this;
                String str1 = LocaleController.getString("UseLessDataNever", NUM);
                String str2 = LocaleController.getString("UseLessDataOnMobile", NUM);
                paramAnonymousView = LocaleController.getString("UseLessDataAlways", NUM);
                String str3 = LocaleController.getString("VoipUseLessData", NUM);
                int i = ((SharedPreferences)localObject2).getInt("VoipDataSaving", 0);
                localObject2 = new DialogInterface.OnClickListener()
                {
                  public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
                  {
                    int i = -1;
                    switch (paramAnonymous2Int)
                    {
                    default: 
                      paramAnonymous2Int = i;
                    }
                    for (;;)
                    {
                      if (paramAnonymous2Int != -1) {
                        localObject2.edit().putInt("VoipDataSaving", paramAnonymous2Int).commit();
                      }
                      if (DataSettingsActivity.this.listAdapter != null) {
                        DataSettingsActivity.this.listAdapter.notifyItemChanged(paramAnonymousInt);
                      }
                      return;
                      paramAnonymous2Int = 0;
                      continue;
                      paramAnonymous2Int = 1;
                      continue;
                      paramAnonymous2Int = 2;
                    }
                  }
                };
                paramAnonymousView = AlertsCreator.createSingleChoiceDialog(localActivity, (BaseFragment)localObject1, new String[] { str1, str2, paramAnonymousView }, str3, i, (DialogInterface.OnClickListener)localObject2);
                DataSettingsActivity.this.setVisibleDialog(paramAnonymousView);
                paramAnonymousView.show();
              }
              else if (paramAnonymousInt == DataSettingsActivity.this.mobileUsageRow)
              {
                DataSettingsActivity.this.presentFragment(new DataUsageActivity(0));
              }
              else if (paramAnonymousInt == DataSettingsActivity.this.roamingUsageRow)
              {
                DataSettingsActivity.this.presentFragment(new DataUsageActivity(2));
              }
              else if (paramAnonymousInt == DataSettingsActivity.this.wifiUsageRow)
              {
                DataSettingsActivity.this.presentFragment(new DataUsageActivity(1));
              }
              else if (paramAnonymousInt == DataSettingsActivity.this.proxyRow)
              {
                DataSettingsActivity.this.presentFragment(new ProxySettingsActivity());
              }
              else if (paramAnonymousInt == DataSettingsActivity.this.enableStreamRow)
              {
                SharedConfig.toggleStreamMedia();
                ((TextCheckCell)paramAnonymousView).setChecked(SharedConfig.streamMedia);
              }
              else if (paramAnonymousInt == DataSettingsActivity.this.enableAllStreamRow)
              {
                SharedConfig.toggleStreamAllVideo();
                ((TextCheckCell)paramAnonymousView).setChecked(SharedConfig.streamAllVideo);
              }
              else if (paramAnonymousInt == DataSettingsActivity.this.enableCacheStreamRow)
              {
                SharedConfig.toggleSaveStreamMedia();
                ((TextCheckCell)paramAnonymousView).setChecked(SharedConfig.saveStreamMedia);
              }
              else if (paramAnonymousInt == DataSettingsActivity.this.quickRepliesRow)
              {
                DataSettingsActivity.this.presentFragment(new QuickRepliesSettingsActivity());
              }
            }
          }
        }
      }
    });
    localFrameLayout.addView(this.actionBar);
    return this.fragmentView;
  }
  
  public ThemeDescription[] getThemeDescriptions()
  {
    ThemeDescription localThemeDescription1 = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[] { TextSettingsCell.class, TextCheckCell.class, HeaderCell.class }, null, null, null, "windowBackgroundWhite");
    ThemeDescription localThemeDescription2 = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray");
    ThemeDescription localThemeDescription3 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription4 = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription5 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
    ThemeDescription localThemeDescription6 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
    ThemeDescription localThemeDescription7 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
    ThemeDescription localThemeDescription8 = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
    RecyclerListView localRecyclerListView = this.listView;
    Paint localPaint = Theme.dividerPaint;
    return new ThemeDescription[] { localThemeDescription1, localThemeDescription2, localThemeDescription3, localThemeDescription4, localThemeDescription5, localThemeDescription6, localThemeDescription7, localThemeDescription8, new ThemeDescription(localRecyclerListView, 0, new Class[] { View.class }, localPaint, null, null, "divider"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { ShadowSectionCell.class }, null, null, null, "windowBackgroundGrayShadow"), new ThemeDescription(this.listView, 0, new Class[] { TextSettingsCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.listView, 0, new Class[] { TextSettingsCell.class }, new String[] { "valueTextView" }, null, null, null, "windowBackgroundWhiteValueText"), new ThemeDescription(this.listView, 0, new Class[] { HeaderCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlueHeader"), new ThemeDescription(this.listView, 0, new Class[] { TextCheckCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.listView, 0, new Class[] { TextCheckCell.class }, new String[] { "valueTextView" }, null, null, null, "windowBackgroundWhiteGrayText2"), new ThemeDescription(this.listView, 0, new Class[] { TextCheckCell.class }, new String[] { "checkBox" }, null, null, null, "switchThumb"), new ThemeDescription(this.listView, 0, new Class[] { TextCheckCell.class }, new String[] { "checkBox" }, null, null, null, "switchTrack"), new ThemeDescription(this.listView, 0, new Class[] { TextCheckCell.class }, new String[] { "checkBox" }, null, null, null, "switchThumbChecked"), new ThemeDescription(this.listView, 0, new Class[] { TextCheckCell.class }, new String[] { "checkBox" }, null, null, null, "switchTrackChecked"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { TextInfoPrivacyCell.class }, null, null, null, "windowBackgroundGrayShadow"), new ThemeDescription(this.listView, 0, new Class[] { TextInfoPrivacyCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteGrayText4") };
  }
  
  protected void onDialogDismiss(Dialog paramDialog)
  {
    DownloadController.getInstance(this.currentAccount).checkAutodownloadSettings();
  }
  
  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    this.rowCount = 0;
    int i = this.rowCount;
    this.rowCount = (i + 1);
    this.usageSectionRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.storageUsageRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.mobileUsageRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.wifiUsageRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.roamingUsageRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.usageSection2Row = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.mediaDownloadSectionRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.autoDownloadMediaRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.photosRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.voiceMessagesRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.videoMessagesRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.videosRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.filesRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.musicRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.gifsRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.resetDownloadRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.mediaDownloadSection2Row = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.streamSectionRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.enableStreamRow = i;
    if (BuildVars.DEBUG_VERSION)
    {
      i = this.rowCount;
      this.rowCount = (i + 1);
    }
    for (this.enableAllStreamRow = i;; this.enableAllStreamRow = -1)
    {
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.enableAllStreamInfoRow = i;
      this.enableCacheStreamRow = -1;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.callsSectionRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.useLessDataForCallsRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.quickRepliesRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.callsSection2Row = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.proxySectionRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.proxyRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.proxySection2Row = i;
      return true;
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
      return DataSettingsActivity.this.rowCount;
    }
    
    public int getItemViewType(int paramInt)
    {
      if ((paramInt == DataSettingsActivity.this.mediaDownloadSection2Row) || (paramInt == DataSettingsActivity.this.usageSection2Row) || (paramInt == DataSettingsActivity.this.callsSection2Row) || (paramInt == DataSettingsActivity.this.proxySection2Row)) {
        paramInt = 0;
      }
      for (;;)
      {
        return paramInt;
        if ((paramInt == DataSettingsActivity.this.mediaDownloadSectionRow) || (paramInt == DataSettingsActivity.this.streamSectionRow) || (paramInt == DataSettingsActivity.this.callsSectionRow) || (paramInt == DataSettingsActivity.this.usageSectionRow) || (paramInt == DataSettingsActivity.this.proxySectionRow)) {
          paramInt = 2;
        } else if ((paramInt == DataSettingsActivity.this.autoDownloadMediaRow) || (paramInt == DataSettingsActivity.this.enableCacheStreamRow) || (paramInt == DataSettingsActivity.this.enableStreamRow) || (paramInt == DataSettingsActivity.this.enableAllStreamRow)) {
          paramInt = 3;
        } else if (paramInt == DataSettingsActivity.this.enableAllStreamInfoRow) {
          paramInt = 4;
        } else {
          paramInt = 1;
        }
      }
    }
    
    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      int i = paramViewHolder.getAdapterPosition();
      boolean bool;
      if ((i == DataSettingsActivity.this.photosRow) || (i == DataSettingsActivity.this.voiceMessagesRow) || (i == DataSettingsActivity.this.videoMessagesRow) || (i == DataSettingsActivity.this.videosRow) || (i == DataSettingsActivity.this.filesRow) || (i == DataSettingsActivity.this.musicRow) || (i == DataSettingsActivity.this.gifsRow)) {
        bool = DownloadController.getInstance(DataSettingsActivity.this.currentAccount).globalAutodownloadEnabled;
      }
      for (;;)
      {
        return bool;
        if ((i == DataSettingsActivity.this.storageUsageRow) || (i == DataSettingsActivity.this.useLessDataForCallsRow) || (i == DataSettingsActivity.this.mobileUsageRow) || (i == DataSettingsActivity.this.roamingUsageRow) || (i == DataSettingsActivity.this.wifiUsageRow) || (i == DataSettingsActivity.this.proxyRow) || (i == DataSettingsActivity.this.resetDownloadRow) || (i == DataSettingsActivity.this.autoDownloadMediaRow) || (i == DataSettingsActivity.this.enableCacheStreamRow) || (i == DataSettingsActivity.this.enableStreamRow) || (i == DataSettingsActivity.this.enableAllStreamRow) || (i == DataSettingsActivity.this.quickRepliesRow)) {
          bool = true;
        } else {
          bool = false;
        }
      }
    }
    
    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      boolean bool1 = true;
      switch (paramViewHolder.getItemViewType())
      {
      }
      for (;;)
      {
        return;
        if (paramInt == DataSettingsActivity.this.proxySection2Row)
        {
          paramViewHolder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
        }
        else
        {
          paramViewHolder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
          continue;
          Object localObject = (TextSettingsCell)paramViewHolder.itemView;
          ((TextSettingsCell)localObject).setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
          if (paramInt == DataSettingsActivity.this.storageUsageRow)
          {
            ((TextSettingsCell)localObject).setText(LocaleController.getString("StorageUsage", NUM), true);
          }
          else
          {
            if (paramInt == DataSettingsActivity.this.useLessDataForCallsRow)
            {
              SharedPreferences localSharedPreferences = MessagesController.getGlobalMainSettings();
              paramViewHolder = null;
              switch (localSharedPreferences.getInt("VoipDataSaving", 0))
              {
              }
              for (;;)
              {
                ((TextSettingsCell)localObject).setTextAndValue(LocaleController.getString("VoipUseLessData", NUM), paramViewHolder, true);
                break;
                paramViewHolder = LocaleController.getString("UseLessDataNever", NUM);
                continue;
                paramViewHolder = LocaleController.getString("UseLessDataOnMobile", NUM);
                continue;
                paramViewHolder = LocaleController.getString("UseLessDataAlways", NUM);
              }
            }
            if (paramInt == DataSettingsActivity.this.mobileUsageRow)
            {
              ((TextSettingsCell)localObject).setText(LocaleController.getString("MobileUsage", NUM), true);
            }
            else if (paramInt == DataSettingsActivity.this.roamingUsageRow)
            {
              ((TextSettingsCell)localObject).setText(LocaleController.getString("RoamingUsage", NUM), false);
            }
            else if (paramInt == DataSettingsActivity.this.wifiUsageRow)
            {
              ((TextSettingsCell)localObject).setText(LocaleController.getString("WiFiUsage", NUM), true);
            }
            else if (paramInt == DataSettingsActivity.this.proxyRow)
            {
              ((TextSettingsCell)localObject).setText(LocaleController.getString("ProxySettings", NUM), true);
            }
            else if (paramInt == DataSettingsActivity.this.resetDownloadRow)
            {
              ((TextSettingsCell)localObject).setTextColor(Theme.getColor("windowBackgroundWhiteRedText"));
              ((TextSettingsCell)localObject).setText(LocaleController.getString("ResetAutomaticMediaDownload", NUM), false);
            }
            else if (paramInt == DataSettingsActivity.this.photosRow)
            {
              ((TextSettingsCell)localObject).setText(LocaleController.getString("LocalPhotoCache", NUM), true);
            }
            else if (paramInt == DataSettingsActivity.this.voiceMessagesRow)
            {
              ((TextSettingsCell)localObject).setText(LocaleController.getString("AudioAutodownload", NUM), true);
            }
            else if (paramInt == DataSettingsActivity.this.videoMessagesRow)
            {
              ((TextSettingsCell)localObject).setText(LocaleController.getString("VideoMessagesAutodownload", NUM), true);
            }
            else if (paramInt == DataSettingsActivity.this.videosRow)
            {
              ((TextSettingsCell)localObject).setText(LocaleController.getString("LocalVideoCache", NUM), true);
            }
            else if (paramInt == DataSettingsActivity.this.filesRow)
            {
              ((TextSettingsCell)localObject).setText(LocaleController.getString("FilesDataUsage", NUM), true);
            }
            else if (paramInt == DataSettingsActivity.this.musicRow)
            {
              ((TextSettingsCell)localObject).setText(LocaleController.getString("AttachMusic", NUM), true);
            }
            else if (paramInt == DataSettingsActivity.this.gifsRow)
            {
              ((TextSettingsCell)localObject).setText(LocaleController.getString("LocalGifCache", NUM), true);
            }
            else if (paramInt == DataSettingsActivity.this.quickRepliesRow)
            {
              ((TextSettingsCell)localObject).setText(LocaleController.getString("VoipQuickReplies", NUM), false);
              continue;
              paramViewHolder = (HeaderCell)paramViewHolder.itemView;
              if (paramInt == DataSettingsActivity.this.mediaDownloadSectionRow)
              {
                paramViewHolder.setText(LocaleController.getString("AutomaticMediaDownload", NUM));
              }
              else if (paramInt == DataSettingsActivity.this.usageSectionRow)
              {
                paramViewHolder.setText(LocaleController.getString("DataUsage", NUM));
              }
              else if (paramInt == DataSettingsActivity.this.callsSectionRow)
              {
                paramViewHolder.setText(LocaleController.getString("Calls", NUM));
              }
              else if (paramInt == DataSettingsActivity.this.proxySectionRow)
              {
                paramViewHolder.setText(LocaleController.getString("Proxy", NUM));
              }
              else if (paramInt == DataSettingsActivity.this.streamSectionRow)
              {
                paramViewHolder.setText(LocaleController.getString("Streaming", NUM));
                continue;
                localObject = (TextCheckCell)paramViewHolder.itemView;
                if (paramInt == DataSettingsActivity.this.autoDownloadMediaRow)
                {
                  ((TextCheckCell)localObject).setTextAndCheck(LocaleController.getString("AutoDownloadMedia", NUM), DownloadController.getInstance(DataSettingsActivity.this.currentAccount).globalAutodownloadEnabled, true);
                }
                else
                {
                  if (paramInt == DataSettingsActivity.this.enableStreamRow)
                  {
                    paramViewHolder = LocaleController.getString("EnableStreaming", NUM);
                    boolean bool2 = SharedConfig.streamMedia;
                    if (DataSettingsActivity.this.enableAllStreamRow != -1) {}
                    for (;;)
                    {
                      ((TextCheckCell)localObject).setTextAndCheck(paramViewHolder, bool2, bool1);
                      break;
                      bool1 = false;
                    }
                  }
                  if ((paramInt != DataSettingsActivity.this.enableCacheStreamRow) && (paramInt == DataSettingsActivity.this.enableAllStreamRow))
                  {
                    ((TextCheckCell)localObject).setTextAndCheck("Try to Stream All Videos", SharedConfig.streamAllVideo, false);
                    continue;
                    paramViewHolder = (TextInfoPrivacyCell)paramViewHolder.itemView;
                    if (paramInt == DataSettingsActivity.this.enableAllStreamInfoRow) {
                      paramViewHolder.setText(LocaleController.getString("EnableAllStreamingInfo", NUM));
                    }
                  }
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
        paramViewGroup = new TextSettingsCell(this.mContext);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        continue;
        paramViewGroup = new HeaderCell(this.mContext);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        continue;
        paramViewGroup = new TextCheckCell(this.mContext);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        continue;
        paramViewGroup = new TextInfoPrivacyCell(this.mContext);
        paramViewGroup.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
      }
    }
    
    public void onViewAttachedToWindow(RecyclerView.ViewHolder paramViewHolder)
    {
      int i = paramViewHolder.getItemViewType();
      if (i == 1)
      {
        i = paramViewHolder.getAdapterPosition();
        paramViewHolder = (TextSettingsCell)paramViewHolder.itemView;
        if ((i >= DataSettingsActivity.this.photosRow) && (i <= DataSettingsActivity.this.gifsRow)) {
          paramViewHolder.setEnabled(DownloadController.getInstance(DataSettingsActivity.this.currentAccount).globalAutodownloadEnabled, null);
        }
      }
      for (;;)
      {
        return;
        paramViewHolder.setEnabled(true, null);
        continue;
        if (i == 3)
        {
          TextCheckCell localTextCheckCell = (TextCheckCell)paramViewHolder.itemView;
          i = paramViewHolder.getAdapterPosition();
          if (i == DataSettingsActivity.this.enableCacheStreamRow) {
            localTextCheckCell.setChecked(SharedConfig.saveStreamMedia);
          } else if (i == DataSettingsActivity.this.enableStreamRow) {
            localTextCheckCell.setChecked(SharedConfig.streamMedia);
          } else if (i == DataSettingsActivity.this.autoDownloadMediaRow) {
            localTextCheckCell.setChecked(DownloadController.getInstance(DataSettingsActivity.this.currentAccount).globalAutodownloadEnabled);
          } else if (i == DataSettingsActivity.this.enableAllStreamRow) {
            localTextCheckCell.setChecked(SharedConfig.streamAllVideo);
          }
        }
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/DataSettingsActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */