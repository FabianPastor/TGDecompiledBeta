package org.telegram.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DataQuery;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.InputStickerSet;
import org.telegram.tgnet.TLRPC.StickerSet;
import org.telegram.tgnet.TLRPC.StickerSetCovered;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputStickerSetID;
import org.telegram.tgnet.TLRPC.TL_inputStickerSetShortName;
import org.telegram.tgnet.TLRPC.TL_messages_archivedStickers;
import org.telegram.tgnet.TLRPC.TL_messages_getArchivedStickers;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.ArchivedStickerSetCell;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.Components.StickersAlert;
import org.telegram.ui.Components.StickersAlert.StickersAlertInstallDelegate;

public class ArchivedStickersActivity
  extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  private int currentType;
  private EmptyTextProgressView emptyView;
  private boolean endReached;
  private boolean firstLoaded;
  private LinearLayoutManager layoutManager;
  private ListAdapter listAdapter;
  private RecyclerListView listView;
  private boolean loadingStickers;
  private int rowCount;
  private ArrayList<TLRPC.StickerSetCovered> sets = new ArrayList();
  private int stickersEndRow;
  private int stickersLoadingRow;
  private int stickersShadowRow;
  private int stickersStartRow;
  
  public ArchivedStickersActivity(int paramInt)
  {
    this.currentType = paramInt;
  }
  
  private void getStickers()
  {
    if ((this.loadingStickers) || (this.endReached)) {
      return;
    }
    this.loadingStickers = true;
    if ((this.emptyView != null) && (!this.firstLoaded)) {
      this.emptyView.showProgress();
    }
    if (this.listAdapter != null) {
      this.listAdapter.notifyDataSetChanged();
    }
    TLRPC.TL_messages_getArchivedStickers localTL_messages_getArchivedStickers = new TLRPC.TL_messages_getArchivedStickers();
    long l;
    if (this.sets.isEmpty())
    {
      l = 0L;
      label75:
      localTL_messages_getArchivedStickers.offset_id = l;
      localTL_messages_getArchivedStickers.limit = 15;
      if (this.currentType != 1) {
        break label172;
      }
    }
    label172:
    for (boolean bool = true;; bool = false)
    {
      localTL_messages_getArchivedStickers.masks = bool;
      int i = ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_messages_getArchivedStickers, new RequestDelegate()
      {
        public void run(final TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
        {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              ArchivedStickersActivity localArchivedStickersActivity;
              if (paramAnonymousTL_error == null)
              {
                TLRPC.TL_messages_archivedStickers localTL_messages_archivedStickers = (TLRPC.TL_messages_archivedStickers)paramAnonymousTLObject;
                ArchivedStickersActivity.this.sets.addAll(localTL_messages_archivedStickers.sets);
                localArchivedStickersActivity = ArchivedStickersActivity.this;
                if (localTL_messages_archivedStickers.sets.size() == 15) {
                  break label122;
                }
              }
              label122:
              for (boolean bool = true;; bool = false)
              {
                ArchivedStickersActivity.access$402(localArchivedStickersActivity, bool);
                ArchivedStickersActivity.access$302(ArchivedStickersActivity.this, false);
                ArchivedStickersActivity.access$802(ArchivedStickersActivity.this, true);
                if (ArchivedStickersActivity.this.emptyView != null) {
                  ArchivedStickersActivity.this.emptyView.showTextView();
                }
                ArchivedStickersActivity.this.updateRows();
                return;
              }
            }
          });
        }
      });
      ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(i, this.classGuid);
      break;
      l = ((TLRPC.StickerSetCovered)this.sets.get(this.sets.size() - 1)).set.id;
      break label75;
    }
  }
  
  private void updateRows()
  {
    this.rowCount = 0;
    int i;
    if (!this.sets.isEmpty())
    {
      this.stickersStartRow = this.rowCount;
      this.stickersEndRow = (this.rowCount + this.sets.size());
      this.rowCount += this.sets.size();
      if (!this.endReached)
      {
        i = this.rowCount;
        this.rowCount = (i + 1);
        this.stickersLoadingRow = i;
        this.stickersShadowRow = -1;
      }
    }
    for (;;)
    {
      if (this.listAdapter != null) {
        this.listAdapter.notifyDataSetChanged();
      }
      return;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.stickersShadowRow = i;
      this.stickersLoadingRow = -1;
      continue;
      this.stickersStartRow = -1;
      this.stickersEndRow = -1;
      this.stickersLoadingRow = -1;
      this.stickersShadowRow = -1;
    }
  }
  
  public View createView(Context paramContext)
  {
    this.actionBar.setBackButtonImage(NUM);
    this.actionBar.setAllowOverlayTitle(true);
    FrameLayout localFrameLayout;
    if (this.currentType == 0)
    {
      this.actionBar.setTitle(LocaleController.getString("ArchivedStickers", NUM));
      this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
      {
        public void onItemClick(int paramAnonymousInt)
        {
          if (paramAnonymousInt == -1) {
            ArchivedStickersActivity.this.finishFragment();
          }
        }
      });
      this.listAdapter = new ListAdapter(paramContext);
      this.fragmentView = new FrameLayout(paramContext);
      localFrameLayout = (FrameLayout)this.fragmentView;
      localFrameLayout.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
      this.emptyView = new EmptyTextProgressView(paramContext);
      if (this.currentType != 0) {
        break label292;
      }
      this.emptyView.setText(LocaleController.getString("ArchivedStickersEmpty", NUM));
      label128:
      localFrameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0F));
      if (!this.loadingStickers) {
        break label311;
      }
      this.emptyView.showProgress();
    }
    for (;;)
    {
      this.listView = new RecyclerListView(paramContext);
      this.listView.setFocusable(true);
      this.listView.setEmptyView(this.emptyView);
      RecyclerListView localRecyclerListView = this.listView;
      paramContext = new LinearLayoutManager(paramContext, 1, false);
      this.layoutManager = paramContext;
      localRecyclerListView.setLayoutManager(paramContext);
      localFrameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0F));
      this.listView.setAdapter(this.listAdapter);
      this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
      {
        public void onItemClick(final View paramAnonymousView, int paramAnonymousInt)
        {
          TLRPC.StickerSetCovered localStickerSetCovered;
          Object localObject;
          if ((paramAnonymousInt >= ArchivedStickersActivity.this.stickersStartRow) && (paramAnonymousInt < ArchivedStickersActivity.this.stickersEndRow) && (ArchivedStickersActivity.this.getParentActivity() != null))
          {
            localStickerSetCovered = (TLRPC.StickerSetCovered)ArchivedStickersActivity.this.sets.get(paramAnonymousInt);
            if (localStickerSetCovered.set.id == 0L) {
              break label141;
            }
            localObject = new TLRPC.TL_inputStickerSetID();
            ((TLRPC.InputStickerSet)localObject).id = localStickerSetCovered.set.id;
          }
          for (;;)
          {
            ((TLRPC.InputStickerSet)localObject).access_hash = localStickerSetCovered.set.access_hash;
            localObject = new StickersAlert(ArchivedStickersActivity.this.getParentActivity(), ArchivedStickersActivity.this, (TLRPC.InputStickerSet)localObject, null, null);
            ((StickersAlert)localObject).setInstallDelegate(new StickersAlert.StickersAlertInstallDelegate()
            {
              public void onStickerSetInstalled()
              {
                ((ArchivedStickerSetCell)paramAnonymousView).setChecked(true);
              }
              
              public void onStickerSetUninstalled()
              {
                ((ArchivedStickerSetCell)paramAnonymousView).setChecked(false);
              }
            });
            ArchivedStickersActivity.this.showDialog((Dialog)localObject);
            return;
            label141:
            localObject = new TLRPC.TL_inputStickerSetShortName();
            ((TLRPC.InputStickerSet)localObject).short_name = localStickerSetCovered.set.short_name;
          }
        }
      });
      this.listView.setOnScrollListener(new RecyclerView.OnScrollListener()
      {
        public void onScrolled(RecyclerView paramAnonymousRecyclerView, int paramAnonymousInt1, int paramAnonymousInt2)
        {
          if ((!ArchivedStickersActivity.this.loadingStickers) && (!ArchivedStickersActivity.this.endReached) && (ArchivedStickersActivity.this.layoutManager.findLastVisibleItemPosition() > ArchivedStickersActivity.this.stickersLoadingRow - 2)) {
            ArchivedStickersActivity.this.getStickers();
          }
        }
      });
      return this.fragmentView;
      this.actionBar.setTitle(LocaleController.getString("ArchivedMasks", NUM));
      break;
      label292:
      this.emptyView.setText(LocaleController.getString("ArchivedMasksEmpty", NUM));
      break label128;
      label311:
      this.emptyView.showTextView();
    }
  }
  
  public void didReceivedNotification(int paramInt1, int paramInt2, Object... paramVarArgs)
  {
    if (paramInt1 == NotificationCenter.needReloadArchivedStickers)
    {
      this.firstLoaded = false;
      this.endReached = false;
      this.sets.clear();
      updateRows();
      if (this.emptyView != null) {
        this.emptyView.showProgress();
      }
      getStickers();
    }
  }
  
  public ThemeDescription[] getThemeDescriptions()
  {
    ThemeDescription localThemeDescription1 = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[] { ArchivedStickerSetCell.class }, null, null, null, "windowBackgroundWhite");
    ThemeDescription localThemeDescription2 = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray");
    ThemeDescription localThemeDescription3 = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { LoadingCell.class, TextInfoPrivacyCell.class }, null, null, null, "windowBackgroundGrayShadow");
    ThemeDescription localThemeDescription4 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription5 = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription6 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
    ThemeDescription localThemeDescription7 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
    ThemeDescription localThemeDescription8 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
    ThemeDescription localThemeDescription9 = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
    RecyclerListView localRecyclerListView = this.listView;
    Paint localPaint = Theme.dividerPaint;
    return new ThemeDescription[] { localThemeDescription1, localThemeDescription2, localThemeDescription3, localThemeDescription4, localThemeDescription5, localThemeDescription6, localThemeDescription7, localThemeDescription8, localThemeDescription9, new ThemeDescription(localRecyclerListView, 0, new Class[] { View.class }, localPaint, null, null, "divider"), new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "emptyListPlaceholder"), new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, "progressCircle"), new ThemeDescription(this.listView, 0, new Class[] { LoadingCell.class }, new String[] { "progressBar" }, null, null, null, "progressCircle"), new ThemeDescription(this.listView, 0, new Class[] { ArchivedStickerSetCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.listView, 0, new Class[] { ArchivedStickerSetCell.class }, new String[] { "valueTextView" }, null, null, null, "windowBackgroundWhiteGrayText2"), new ThemeDescription(this.listView, 0, new Class[] { ArchivedStickerSetCell.class }, new String[] { "checkBox" }, null, null, null, "switchThumb"), new ThemeDescription(this.listView, 0, new Class[] { ArchivedStickerSetCell.class }, new String[] { "checkBox" }, null, null, null, "switchTrack"), new ThemeDescription(this.listView, 0, new Class[] { ArchivedStickerSetCell.class }, new String[] { "checkBox" }, null, null, null, "switchThumbChecked"), new ThemeDescription(this.listView, 0, new Class[] { ArchivedStickerSetCell.class }, new String[] { "checkBox" }, null, null, null, "switchTrackChecked") };
  }
  
  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    getStickers();
    updateRows();
    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.needReloadArchivedStickers);
    return true;
  }
  
  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.needReloadArchivedStickers);
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
      return ArchivedStickersActivity.this.rowCount;
    }
    
    public int getItemViewType(int paramInt)
    {
      int i = 0;
      if ((paramInt >= ArchivedStickersActivity.this.stickersStartRow) && (paramInt < ArchivedStickersActivity.this.stickersEndRow)) {}
      for (;;)
      {
        return i;
        if (paramInt == ArchivedStickersActivity.this.stickersLoadingRow) {
          i = 1;
        } else if (paramInt == ArchivedStickersActivity.this.stickersShadowRow) {
          i = 2;
        }
      }
    }
    
    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      if (paramViewHolder.getItemViewType() == 0) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      ArchivedStickerSetCell localArchivedStickerSetCell;
      if (getItemViewType(paramInt) == 0)
      {
        localArchivedStickerSetCell = (ArchivedStickerSetCell)paramViewHolder.itemView;
        localArchivedStickerSetCell.setTag(Integer.valueOf(paramInt));
        paramViewHolder = (TLRPC.StickerSetCovered)ArchivedStickersActivity.this.sets.get(paramInt);
        if (paramInt == ArchivedStickersActivity.this.sets.size() - 1) {
          break label90;
        }
      }
      label90:
      for (boolean bool = true;; bool = false)
      {
        localArchivedStickerSetCell.setStickersSet(paramViewHolder, bool);
        localArchivedStickerSetCell.setChecked(DataQuery.getInstance(ArchivedStickersActivity.this.currentAccount).isStickerPackInstalled(paramViewHolder.set.id));
        return;
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
        paramViewGroup = new ArchivedStickerSetCell(this.mContext, true);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        ((ArchivedStickerSetCell)paramViewGroup).setOnCheckClick(new CompoundButton.OnCheckedChangeListener()
        {
          public void onCheckedChanged(CompoundButton paramAnonymousCompoundButton, boolean paramAnonymousBoolean)
          {
            int i = ((Integer)((ArchivedStickerSetCell)paramAnonymousCompoundButton.getParent()).getTag()).intValue();
            if (i >= ArchivedStickersActivity.this.sets.size()) {
              return;
            }
            Object localObject = (TLRPC.StickerSetCovered)ArchivedStickersActivity.this.sets.get(i);
            paramAnonymousCompoundButton = DataQuery.getInstance(ArchivedStickersActivity.this.currentAccount);
            Activity localActivity = ArchivedStickersActivity.this.getParentActivity();
            localObject = ((TLRPC.StickerSetCovered)localObject).set;
            if (!paramAnonymousBoolean) {}
            for (i = 1;; i = 2)
            {
              paramAnonymousCompoundButton.removeStickersSet(localActivity, (TLRPC.StickerSet)localObject, i, ArchivedStickersActivity.this, false);
              break;
            }
          }
        });
        continue;
        paramViewGroup = new LoadingCell(this.mContext);
        paramViewGroup.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
        continue;
        paramViewGroup = new TextInfoPrivacyCell(this.mContext);
        paramViewGroup.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/ArchivedStickersActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */