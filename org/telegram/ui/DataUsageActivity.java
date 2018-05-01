package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.StatsController;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.messenger.time.FastDateFormat;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class DataUsageActivity
  extends BaseFragment
{
  private int audiosBytesReceivedRow;
  private int audiosBytesSentRow;
  private int audiosReceivedRow;
  private int audiosSection2Row;
  private int audiosSectionRow;
  private int audiosSentRow;
  private int callsBytesReceivedRow;
  private int callsBytesSentRow;
  private int callsReceivedRow;
  private int callsSection2Row;
  private int callsSectionRow;
  private int callsSentRow;
  private int callsTotalTimeRow;
  private int currentType;
  private int filesBytesReceivedRow;
  private int filesBytesSentRow;
  private int filesReceivedRow;
  private int filesSection2Row;
  private int filesSectionRow;
  private int filesSentRow;
  private ListAdapter listAdapter;
  private RecyclerListView listView;
  private int messagesBytesReceivedRow;
  private int messagesBytesSentRow;
  private int messagesReceivedRow = -1;
  private int messagesSection2Row;
  private int messagesSectionRow;
  private int messagesSentRow = -1;
  private int photosBytesReceivedRow;
  private int photosBytesSentRow;
  private int photosReceivedRow;
  private int photosSection2Row;
  private int photosSectionRow;
  private int photosSentRow;
  private int resetRow;
  private int resetSection2Row;
  private int rowCount;
  private int totalBytesReceivedRow;
  private int totalBytesSentRow;
  private int totalSection2Row;
  private int totalSectionRow;
  private int videosBytesReceivedRow;
  private int videosBytesSentRow;
  private int videosReceivedRow;
  private int videosSection2Row;
  private int videosSectionRow;
  private int videosSentRow;
  
  public DataUsageActivity(int paramInt)
  {
    this.currentType = paramInt;
  }
  
  public View createView(Context paramContext)
  {
    this.actionBar.setBackButtonImage(NUM);
    if (this.currentType == 0) {
      this.actionBar.setTitle(LocaleController.getString("MobileUsage", NUM));
    }
    for (;;)
    {
      if (AndroidUtilities.isTablet()) {
        this.actionBar.setOccupyStatusBar(false);
      }
      this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
      {
        public void onItemClick(int paramAnonymousInt)
        {
          if (paramAnonymousInt == -1) {
            DataUsageActivity.this.finishFragment();
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
        public void onItemClick(View paramAnonymousView, int paramAnonymousInt)
        {
          if (DataUsageActivity.this.getParentActivity() == null) {}
          for (;;)
          {
            return;
            if (paramAnonymousInt == DataUsageActivity.this.resetRow)
            {
              paramAnonymousView = new AlertDialog.Builder(DataUsageActivity.this.getParentActivity());
              paramAnonymousView.setTitle(LocaleController.getString("AppName", NUM));
              paramAnonymousView.setMessage(LocaleController.getString("ResetStatisticsAlert", NUM));
              paramAnonymousView.setPositiveButton(LocaleController.getString("Reset", NUM), new DialogInterface.OnClickListener()
              {
                public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
                {
                  StatsController.getInstance(DataUsageActivity.this.currentAccount).resetStats(DataUsageActivity.this.currentType);
                  DataUsageActivity.this.listAdapter.notifyDataSetChanged();
                }
              });
              paramAnonymousView.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
              DataUsageActivity.this.showDialog(paramAnonymousView.create());
            }
          }
        }
      });
      localFrameLayout.addView(this.actionBar);
      return this.fragmentView;
      if (this.currentType == 1) {
        this.actionBar.setTitle(LocaleController.getString("WiFiUsage", NUM));
      } else if (this.currentType == 2) {
        this.actionBar.setTitle(LocaleController.getString("RoamingUsage", NUM));
      }
    }
  }
  
  public ThemeDescription[] getThemeDescriptions()
  {
    ThemeDescription localThemeDescription1 = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[] { TextSettingsCell.class, HeaderCell.class }, null, null, null, "windowBackgroundWhite");
    ThemeDescription localThemeDescription2 = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray");
    ThemeDescription localThemeDescription3 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription4 = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription5 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
    ThemeDescription localThemeDescription6 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
    ThemeDescription localThemeDescription7 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
    ThemeDescription localThemeDescription8 = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
    RecyclerListView localRecyclerListView = this.listView;
    Paint localPaint = Theme.dividerPaint;
    return new ThemeDescription[] { localThemeDescription1, localThemeDescription2, localThemeDescription3, localThemeDescription4, localThemeDescription5, localThemeDescription6, localThemeDescription7, localThemeDescription8, new ThemeDescription(localRecyclerListView, 0, new Class[] { View.class }, localPaint, null, null, "divider"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { ShadowSectionCell.class }, null, null, null, "windowBackgroundGrayShadow"), new ThemeDescription(this.listView, 0, new Class[] { HeaderCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlueHeader"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { TextInfoPrivacyCell.class }, null, null, null, "windowBackgroundGrayShadow"), new ThemeDescription(this.listView, 0, new Class[] { TextInfoPrivacyCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteGrayText4"), new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[] { TextSettingsCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.listView, 0, new Class[] { TextSettingsCell.class }, new String[] { "valueTextView" }, null, null, null, "windowBackgroundWhiteValueText"), new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[] { TextSettingsCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteRedText2") };
  }
  
  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    this.rowCount = 0;
    int i = this.rowCount;
    this.rowCount = (i + 1);
    this.photosSectionRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.photosSentRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.photosReceivedRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.photosBytesSentRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.photosBytesReceivedRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.photosSection2Row = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.videosSectionRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.videosSentRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.videosReceivedRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.videosBytesSentRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.videosBytesReceivedRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.videosSection2Row = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.audiosSectionRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.audiosSentRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.audiosReceivedRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.audiosBytesSentRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.audiosBytesReceivedRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.audiosSection2Row = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.filesSectionRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.filesSentRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.filesReceivedRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.filesBytesSentRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.filesBytesReceivedRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.filesSection2Row = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.callsSectionRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.callsSentRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.callsReceivedRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.callsBytesSentRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.callsBytesReceivedRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.callsTotalTimeRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.callsSection2Row = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.messagesSectionRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.messagesBytesSentRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.messagesBytesReceivedRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.messagesSection2Row = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.totalSectionRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.totalBytesSentRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.totalBytesReceivedRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.totalSection2Row = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.resetRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.resetSection2Row = i;
    return true;
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
      return DataUsageActivity.this.rowCount;
    }
    
    public int getItemViewType(int paramInt)
    {
      if (paramInt == DataUsageActivity.this.resetSection2Row) {
        paramInt = 3;
      }
      for (;;)
      {
        return paramInt;
        if ((paramInt == DataUsageActivity.this.resetSection2Row) || (paramInt == DataUsageActivity.this.callsSection2Row) || (paramInt == DataUsageActivity.this.filesSection2Row) || (paramInt == DataUsageActivity.this.audiosSection2Row) || (paramInt == DataUsageActivity.this.videosSection2Row) || (paramInt == DataUsageActivity.this.photosSection2Row) || (paramInt == DataUsageActivity.this.messagesSection2Row) || (paramInt == DataUsageActivity.this.totalSection2Row)) {
          paramInt = 0;
        } else if ((paramInt == DataUsageActivity.this.totalSectionRow) || (paramInt == DataUsageActivity.this.callsSectionRow) || (paramInt == DataUsageActivity.this.filesSectionRow) || (paramInt == DataUsageActivity.this.audiosSectionRow) || (paramInt == DataUsageActivity.this.videosSectionRow) || (paramInt == DataUsageActivity.this.photosSectionRow) || (paramInt == DataUsageActivity.this.messagesSectionRow)) {
          paramInt = 2;
        } else {
          paramInt = 1;
        }
      }
    }
    
    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      if (paramViewHolder.getAdapterPosition() == DataUsageActivity.this.resetRow) {}
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
        if (paramInt == DataUsageActivity.this.resetSection2Row)
        {
          paramViewHolder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
        }
        else
        {
          paramViewHolder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
          continue;
          TextSettingsCell localTextSettingsCell = (TextSettingsCell)paramViewHolder.itemView;
          if (paramInt == DataUsageActivity.this.resetRow)
          {
            localTextSettingsCell.setTag("windowBackgroundWhiteRedText2");
            localTextSettingsCell.setText(LocaleController.getString("ResetStatistics", NUM), false);
            localTextSettingsCell.setTextColor(Theme.getColor("windowBackgroundWhiteRedText2"));
          }
          else
          {
            localTextSettingsCell.setTag("windowBackgroundWhiteBlackText");
            localTextSettingsCell.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            int i;
            if ((paramInt == DataUsageActivity.this.callsSentRow) || (paramInt == DataUsageActivity.this.callsReceivedRow) || (paramInt == DataUsageActivity.this.callsBytesSentRow) || (paramInt == DataUsageActivity.this.callsBytesReceivedRow)) {
              i = 0;
            }
            for (;;)
            {
              if (paramInt != DataUsageActivity.this.callsSentRow) {
                break label521;
              }
              localTextSettingsCell.setTextAndValue(LocaleController.getString("OutgoingCalls", NUM), String.format("%d", new Object[] { Integer.valueOf(StatsController.getInstance(DataUsageActivity.this.currentAccount).getSentItemsCount(DataUsageActivity.this.currentType, i)) }), true);
              break;
              if ((paramInt == DataUsageActivity.this.messagesSentRow) || (paramInt == DataUsageActivity.this.messagesReceivedRow) || (paramInt == DataUsageActivity.this.messagesBytesSentRow) || (paramInt == DataUsageActivity.this.messagesBytesReceivedRow)) {
                i = 1;
              } else if ((paramInt == DataUsageActivity.this.photosSentRow) || (paramInt == DataUsageActivity.this.photosReceivedRow) || (paramInt == DataUsageActivity.this.photosBytesSentRow) || (paramInt == DataUsageActivity.this.photosBytesReceivedRow)) {
                i = 4;
              } else if ((paramInt == DataUsageActivity.this.audiosSentRow) || (paramInt == DataUsageActivity.this.audiosReceivedRow) || (paramInt == DataUsageActivity.this.audiosBytesSentRow) || (paramInt == DataUsageActivity.this.audiosBytesReceivedRow)) {
                i = 3;
              } else if ((paramInt == DataUsageActivity.this.videosSentRow) || (paramInt == DataUsageActivity.this.videosReceivedRow) || (paramInt == DataUsageActivity.this.videosBytesSentRow) || (paramInt == DataUsageActivity.this.videosBytesReceivedRow)) {
                i = 2;
              } else if ((paramInt == DataUsageActivity.this.filesSentRow) || (paramInt == DataUsageActivity.this.filesReceivedRow) || (paramInt == DataUsageActivity.this.filesBytesSentRow) || (paramInt == DataUsageActivity.this.filesBytesReceivedRow)) {
                i = 5;
              } else {
                i = 6;
              }
            }
            label521:
            if (paramInt == DataUsageActivity.this.callsReceivedRow)
            {
              localTextSettingsCell.setTextAndValue(LocaleController.getString("IncomingCalls", NUM), String.format("%d", new Object[] { Integer.valueOf(StatsController.getInstance(DataUsageActivity.this.currentAccount).getRecivedItemsCount(DataUsageActivity.this.currentType, i)) }), true);
            }
            else
            {
              if (paramInt == DataUsageActivity.this.callsTotalTimeRow)
              {
                i = StatsController.getInstance(DataUsageActivity.this.currentAccount).getCallsTotalTime(DataUsageActivity.this.currentType);
                paramInt = i / 3600;
                int j = i - paramInt * 3600;
                i = j / 60;
                j -= i * 60;
                if (paramInt != 0) {}
                for (paramViewHolder = String.format("%d:%02d:%02d", new Object[] { Integer.valueOf(paramInt), Integer.valueOf(i), Integer.valueOf(j) });; paramViewHolder = String.format("%d:%02d", new Object[] { Integer.valueOf(i), Integer.valueOf(j) }))
                {
                  localTextSettingsCell.setTextAndValue(LocaleController.getString("CallsTotalTime", NUM), paramViewHolder, false);
                  break;
                }
              }
              if ((paramInt == DataUsageActivity.this.messagesSentRow) || (paramInt == DataUsageActivity.this.photosSentRow) || (paramInt == DataUsageActivity.this.videosSentRow) || (paramInt == DataUsageActivity.this.audiosSentRow) || (paramInt == DataUsageActivity.this.filesSentRow))
              {
                localTextSettingsCell.setTextAndValue(LocaleController.getString("CountSent", NUM), String.format("%d", new Object[] { Integer.valueOf(StatsController.getInstance(DataUsageActivity.this.currentAccount).getSentItemsCount(DataUsageActivity.this.currentType, i)) }), true);
              }
              else if ((paramInt == DataUsageActivity.this.messagesReceivedRow) || (paramInt == DataUsageActivity.this.photosReceivedRow) || (paramInt == DataUsageActivity.this.videosReceivedRow) || (paramInt == DataUsageActivity.this.audiosReceivedRow) || (paramInt == DataUsageActivity.this.filesReceivedRow))
              {
                localTextSettingsCell.setTextAndValue(LocaleController.getString("CountReceived", NUM), String.format("%d", new Object[] { Integer.valueOf(StatsController.getInstance(DataUsageActivity.this.currentAccount).getRecivedItemsCount(DataUsageActivity.this.currentType, i)) }), true);
              }
              else if ((paramInt == DataUsageActivity.this.messagesBytesSentRow) || (paramInt == DataUsageActivity.this.photosBytesSentRow) || (paramInt == DataUsageActivity.this.videosBytesSentRow) || (paramInt == DataUsageActivity.this.audiosBytesSentRow) || (paramInt == DataUsageActivity.this.filesBytesSentRow) || (paramInt == DataUsageActivity.this.callsBytesSentRow) || (paramInt == DataUsageActivity.this.totalBytesSentRow))
              {
                localTextSettingsCell.setTextAndValue(LocaleController.getString("BytesSent", NUM), AndroidUtilities.formatFileSize(StatsController.getInstance(DataUsageActivity.this.currentAccount).getSentBytesCount(DataUsageActivity.this.currentType, i)), true);
              }
              else if ((paramInt == DataUsageActivity.this.messagesBytesReceivedRow) || (paramInt == DataUsageActivity.this.photosBytesReceivedRow) || (paramInt == DataUsageActivity.this.videosBytesReceivedRow) || (paramInt == DataUsageActivity.this.audiosBytesReceivedRow) || (paramInt == DataUsageActivity.this.filesBytesReceivedRow) || (paramInt == DataUsageActivity.this.callsBytesReceivedRow) || (paramInt == DataUsageActivity.this.totalBytesReceivedRow))
              {
                paramViewHolder = LocaleController.getString("BytesReceived", NUM);
                String str = AndroidUtilities.formatFileSize(StatsController.getInstance(DataUsageActivity.this.currentAccount).getReceivedBytesCount(DataUsageActivity.this.currentType, i));
                if (paramInt != DataUsageActivity.this.totalBytesReceivedRow) {}
                for (boolean bool = true;; bool = false)
                {
                  localTextSettingsCell.setTextAndValue(paramViewHolder, str, bool);
                  break;
                }
                paramViewHolder = (HeaderCell)paramViewHolder.itemView;
                if (paramInt == DataUsageActivity.this.totalSectionRow)
                {
                  paramViewHolder.setText(LocaleController.getString("TotalDataUsage", NUM));
                }
                else if (paramInt == DataUsageActivity.this.callsSectionRow)
                {
                  paramViewHolder.setText(LocaleController.getString("CallsDataUsage", NUM));
                }
                else if (paramInt == DataUsageActivity.this.filesSectionRow)
                {
                  paramViewHolder.setText(LocaleController.getString("FilesDataUsage", NUM));
                }
                else if (paramInt == DataUsageActivity.this.audiosSectionRow)
                {
                  paramViewHolder.setText(LocaleController.getString("LocalAudioCache", NUM));
                }
                else if (paramInt == DataUsageActivity.this.videosSectionRow)
                {
                  paramViewHolder.setText(LocaleController.getString("LocalVideoCache", NUM));
                }
                else if (paramInt == DataUsageActivity.this.photosSectionRow)
                {
                  paramViewHolder.setText(LocaleController.getString("LocalPhotoCache", NUM));
                }
                else if (paramInt == DataUsageActivity.this.messagesSectionRow)
                {
                  paramViewHolder.setText(LocaleController.getString("MessagesDataUsage", NUM));
                  continue;
                  paramViewHolder = (TextInfoPrivacyCell)paramViewHolder.itemView;
                  paramViewHolder.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                  paramViewHolder.setText(LocaleController.formatString("NetworkUsageSince", NUM, new Object[] { LocaleController.getInstance().formatterStats.format(StatsController.getInstance(DataUsageActivity.this.currentAccount).getResetStatsDate(DataUsageActivity.this.currentType)) }));
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
        paramViewGroup = new TextInfoPrivacyCell(this.mContext);
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/DataUsageActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */