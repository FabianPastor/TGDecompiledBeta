package org.telegram.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Paint;
import android.util.LongSparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import java.util.ArrayList;
import org.telegram.messenger.DataQuery;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.TLRPC.InputStickerSet;
import org.telegram.tgnet.TLRPC.StickerSet;
import org.telegram.tgnet.TLRPC.StickerSetCovered;
import org.telegram.tgnet.TLRPC.TL_inputStickerSetID;
import org.telegram.tgnet.TLRPC.TL_inputStickerSetShortName;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.FeaturedStickerSetCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.Components.StickersAlert;
import org.telegram.ui.Components.StickersAlert.StickersAlertInstallDelegate;

public class FeaturedStickersActivity
  extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  private LongSparseArray<TLRPC.StickerSetCovered> installingStickerSets = new LongSparseArray();
  private LinearLayoutManager layoutManager;
  private ListAdapter listAdapter;
  private RecyclerListView listView;
  private int rowCount;
  private int stickersEndRow;
  private int stickersShadowRow;
  private int stickersStartRow;
  private ArrayList<Long> unreadStickers = null;
  
  private void updateRows()
  {
    this.rowCount = 0;
    ArrayList localArrayList = DataQuery.getInstance(this.currentAccount).getFeaturedStickerSets();
    int i;
    if (!localArrayList.isEmpty())
    {
      this.stickersStartRow = this.rowCount;
      this.stickersEndRow = (this.rowCount + localArrayList.size());
      this.rowCount += localArrayList.size();
      i = this.rowCount;
      this.rowCount = (i + 1);
    }
    for (this.stickersShadowRow = i;; this.stickersShadowRow = -1)
    {
      if (this.listAdapter != null) {
        this.listAdapter.notifyDataSetChanged();
      }
      DataQuery.getInstance(this.currentAccount).markFaturedStickersAsRead(true);
      return;
      this.stickersStartRow = -1;
      this.stickersEndRow = -1;
    }
  }
  
  private void updateVisibleTrendingSets()
  {
    if (this.layoutManager == null) {}
    for (;;)
    {
      return;
      int i = this.layoutManager.findFirstVisibleItemPosition();
      if (i != -1)
      {
        int j = this.layoutManager.findLastVisibleItemPosition();
        if (j != -1) {
          this.listAdapter.notifyItemRangeChanged(i, j - i + 1);
        }
      }
    }
  }
  
  public View createView(Context paramContext)
  {
    this.actionBar.setBackButtonImage(NUM);
    this.actionBar.setAllowOverlayTitle(true);
    this.actionBar.setTitle(LocaleController.getString("FeaturedStickers", NUM));
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (paramAnonymousInt == -1) {
          FeaturedStickersActivity.this.finishFragment();
        }
      }
    });
    this.listAdapter = new ListAdapter(paramContext);
    this.fragmentView = new FrameLayout(paramContext);
    FrameLayout localFrameLayout = (FrameLayout)this.fragmentView;
    localFrameLayout.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
    this.listView = new RecyclerListView(paramContext);
    this.listView.setItemAnimator(null);
    this.listView.setLayoutAnimation(null);
    this.listView.setFocusable(true);
    this.listView.setTag(Integer.valueOf(14));
    this.layoutManager = new LinearLayoutManager(paramContext)
    {
      public boolean supportsPredictiveItemAnimations()
      {
        return false;
      }
    };
    this.layoutManager.setOrientation(1);
    this.listView.setLayoutManager(this.layoutManager);
    localFrameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0F));
    this.listView.setAdapter(this.listAdapter);
    this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
    {
      public void onItemClick(final View paramAnonymousView, int paramAnonymousInt)
      {
        final TLRPC.StickerSetCovered localStickerSetCovered;
        Object localObject;
        if ((paramAnonymousInt >= FeaturedStickersActivity.this.stickersStartRow) && (paramAnonymousInt < FeaturedStickersActivity.this.stickersEndRow) && (FeaturedStickersActivity.this.getParentActivity() != null))
        {
          localStickerSetCovered = (TLRPC.StickerSetCovered)DataQuery.getInstance(FeaturedStickersActivity.this.currentAccount).getFeaturedStickerSets().get(paramAnonymousInt);
          if (localStickerSetCovered.set.id == 0L) {
            break label148;
          }
          localObject = new TLRPC.TL_inputStickerSetID();
          ((TLRPC.InputStickerSet)localObject).id = localStickerSetCovered.set.id;
        }
        for (;;)
        {
          ((TLRPC.InputStickerSet)localObject).access_hash = localStickerSetCovered.set.access_hash;
          localObject = new StickersAlert(FeaturedStickersActivity.this.getParentActivity(), FeaturedStickersActivity.this, (TLRPC.InputStickerSet)localObject, null, null);
          ((StickersAlert)localObject).setInstallDelegate(new StickersAlert.StickersAlertInstallDelegate()
          {
            public void onStickerSetInstalled()
            {
              ((FeaturedStickerSetCell)paramAnonymousView).setDrawProgress(true);
              FeaturedStickersActivity.this.installingStickerSets.put(localStickerSetCovered.set.id, localStickerSetCovered);
            }
            
            public void onStickerSetUninstalled() {}
          });
          FeaturedStickersActivity.this.showDialog((Dialog)localObject);
          return;
          label148:
          localObject = new TLRPC.TL_inputStickerSetShortName();
          ((TLRPC.InputStickerSet)localObject).short_name = localStickerSetCovered.set.short_name;
        }
      }
    });
    return this.fragmentView;
  }
  
  public void didReceivedNotification(int paramInt1, int paramInt2, Object... paramVarArgs)
  {
    if (paramInt1 == NotificationCenter.featuredStickersDidLoaded)
    {
      if (this.unreadStickers == null) {
        this.unreadStickers = DataQuery.getInstance(this.currentAccount).getUnreadStickerSets();
      }
      updateRows();
    }
    for (;;)
    {
      return;
      if (paramInt1 == NotificationCenter.stickersDidLoaded) {
        updateVisibleTrendingSets();
      }
    }
  }
  
  public ThemeDescription[] getThemeDescriptions()
  {
    ThemeDescription localThemeDescription1 = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[] { FeaturedStickerSetCell.class }, null, null, null, "windowBackgroundWhite");
    ThemeDescription localThemeDescription2 = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray");
    ThemeDescription localThemeDescription3 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription4 = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription5 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
    ThemeDescription localThemeDescription6 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
    ThemeDescription localThemeDescription7 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
    ThemeDescription localThemeDescription8 = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
    RecyclerListView localRecyclerListView = this.listView;
    Paint localPaint = Theme.dividerPaint;
    return new ThemeDescription[] { localThemeDescription1, localThemeDescription2, localThemeDescription3, localThemeDescription4, localThemeDescription5, localThemeDescription6, localThemeDescription7, localThemeDescription8, new ThemeDescription(localRecyclerListView, 0, new Class[] { View.class }, localPaint, null, null, "divider"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { TextInfoPrivacyCell.class }, null, null, null, "windowBackgroundGrayShadow"), new ThemeDescription(this.listView, 0, new Class[] { FeaturedStickerSetCell.class }, new String[] { "progressPaint" }, null, null, null, "featuredStickers_buttonProgress"), new ThemeDescription(this.listView, 0, new Class[] { FeaturedStickerSetCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.listView, 0, new Class[] { FeaturedStickerSetCell.class }, new String[] { "valueTextView" }, null, null, null, "windowBackgroundWhiteGrayText2"), new ThemeDescription(this.listView, 0, new Class[] { FeaturedStickerSetCell.class }, new String[] { "addButton" }, null, null, null, "featuredStickers_buttonText"), new ThemeDescription(this.listView, 0, new Class[] { FeaturedStickerSetCell.class }, new String[] { "checkImage" }, null, null, null, "featuredStickers_addedIcon"), new ThemeDescription(this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, new Class[] { FeaturedStickerSetCell.class }, new String[] { "addButton" }, null, null, null, "featuredStickers_addButton"), new ThemeDescription(this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[] { FeaturedStickerSetCell.class }, new String[] { "addButton" }, null, null, null, "featuredStickers_addButtonPressed") };
  }
  
  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    DataQuery.getInstance(this.currentAccount).checkFeaturedStickers();
    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.featuredStickersDidLoaded);
    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.stickersDidLoaded);
    ArrayList localArrayList = DataQuery.getInstance(this.currentAccount).getUnreadStickerSets();
    if (localArrayList != null) {
      this.unreadStickers = new ArrayList(localArrayList);
    }
    updateRows();
    return true;
  }
  
  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.featuredStickersDidLoaded);
    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.stickersDidLoaded);
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
      return FeaturedStickersActivity.this.rowCount;
    }
    
    public int getItemViewType(int paramInt)
    {
      int i = 0;
      if ((paramInt >= FeaturedStickersActivity.this.stickersStartRow) && (paramInt < FeaturedStickersActivity.this.stickersEndRow)) {}
      for (;;)
      {
        return i;
        if (paramInt == FeaturedStickersActivity.this.stickersShadowRow) {
          i = 1;
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
      TLRPC.StickerSetCovered localStickerSetCovered;
      boolean bool2;
      if (getItemViewType(paramInt) == 0)
      {
        ArrayList localArrayList = DataQuery.getInstance(FeaturedStickersActivity.this.currentAccount).getFeaturedStickerSets();
        paramViewHolder = (FeaturedStickerSetCell)paramViewHolder.itemView;
        paramViewHolder.setTag(Integer.valueOf(paramInt));
        localStickerSetCovered = (TLRPC.StickerSetCovered)localArrayList.get(paramInt);
        if (paramInt == localArrayList.size() - 1) {
          break label185;
        }
        bool1 = true;
        if ((FeaturedStickersActivity.this.unreadStickers == null) || (!FeaturedStickersActivity.this.unreadStickers.contains(Long.valueOf(localStickerSetCovered.set.id)))) {
          break label191;
        }
        bool2 = true;
        label98:
        paramViewHolder.setStickersSet(localStickerSetCovered, bool1, bool2);
        if (FeaturedStickersActivity.this.installingStickerSets.indexOfKey(localStickerSetCovered.set.id) < 0) {
          break label197;
        }
      }
      label185:
      label191:
      label197:
      for (boolean bool1 = true;; bool1 = false)
      {
        bool2 = bool1;
        if (bool1)
        {
          bool2 = bool1;
          if (paramViewHolder.isInstalled())
          {
            FeaturedStickersActivity.this.installingStickerSets.remove(localStickerSetCovered.set.id);
            bool2 = false;
            paramViewHolder.setDrawProgress(false);
          }
        }
        paramViewHolder.setDrawProgress(bool2);
        return;
        bool1 = false;
        break;
        bool2 = false;
        break label98;
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
        paramViewGroup = new FeaturedStickerSetCell(this.mContext);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        ((FeaturedStickerSetCell)paramViewGroup).setAddOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            paramAnonymousView = (FeaturedStickerSetCell)paramAnonymousView.getParent();
            TLRPC.StickerSetCovered localStickerSetCovered = paramAnonymousView.getStickerSet();
            if (FeaturedStickersActivity.this.installingStickerSets.indexOfKey(localStickerSetCovered.set.id) >= 0) {}
            for (;;)
            {
              return;
              FeaturedStickersActivity.this.installingStickerSets.put(localStickerSetCovered.set.id, localStickerSetCovered);
              DataQuery.getInstance(FeaturedStickersActivity.this.currentAccount).removeStickersSet(FeaturedStickersActivity.this.getParentActivity(), localStickerSetCovered.set, 2, FeaturedStickersActivity.this, false);
              paramAnonymousView.setDrawProgress(true);
            }
          }
        });
        continue;
        paramViewGroup = new TextInfoPrivacyCell(this.mContext);
        paramViewGroup.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/FeaturedStickersActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */