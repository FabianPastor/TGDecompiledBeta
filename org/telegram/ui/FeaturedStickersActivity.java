package org.telegram.ui;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.query.StickersQuery;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
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
import org.telegram.ui.Cells.FeaturedStickerSetCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.StickersAlert;
import org.telegram.ui.Components.StickersAlert.StickersAlertInstallDelegate;

public class FeaturedStickersActivity
  extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  private HashMap<Long, TLRPC.StickerSetCovered> installingStickerSets = new HashMap();
  private LinearLayoutManager layoutManager;
  private ListAdapter listAdapter;
  private int rowCount;
  private int stickersEndRow;
  private int stickersShadowRow;
  private int stickersStartRow;
  private ArrayList<Long> unreadStickers = null;
  
  private void updateRows()
  {
    this.rowCount = 0;
    ArrayList localArrayList = StickersQuery.getFeaturedStickerSets();
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
      StickersQuery.markFaturedStickersAsRead(true);
      return;
      this.stickersStartRow = -1;
      this.stickersEndRow = -1;
    }
  }
  
  private void updateVisibleTrendingSets()
  {
    if (this.layoutManager == null) {}
    int i;
    int j;
    do
    {
      do
      {
        return;
        i = this.layoutManager.findFirstVisibleItemPosition();
      } while (i == -1);
      j = this.layoutManager.findLastVisibleItemPosition();
    } while (j == -1);
    this.listAdapter.notifyItemRangeChanged(i, j - i + 1);
  }
  
  public View createView(Context paramContext)
  {
    this.actionBar.setBackButtonImage(2130837700);
    this.actionBar.setAllowOverlayTitle(true);
    this.actionBar.setTitle(LocaleController.getString("FeaturedStickers", 2131165632));
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
    localFrameLayout.setBackgroundColor(-986896);
    RecyclerListView localRecyclerListView = new RecyclerListView(paramContext);
    localRecyclerListView.setItemAnimator(null);
    localRecyclerListView.setLayoutAnimation(null);
    localRecyclerListView.setFocusable(true);
    localRecyclerListView.setTag(Integer.valueOf(14));
    this.layoutManager = new LinearLayoutManager(paramContext)
    {
      public boolean supportsPredictiveItemAnimations()
      {
        return false;
      }
    };
    this.layoutManager.setOrientation(1);
    localRecyclerListView.setLayoutManager(this.layoutManager);
    localFrameLayout.addView(localRecyclerListView, LayoutHelper.createFrame(-1, -1.0F));
    localRecyclerListView.setAdapter(this.listAdapter);
    localRecyclerListView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
    {
      public void onItemClick(final View paramAnonymousView, int paramAnonymousInt)
      {
        final TLRPC.StickerSetCovered localStickerSetCovered;
        Object localObject;
        if ((paramAnonymousInt >= FeaturedStickersActivity.this.stickersStartRow) && (paramAnonymousInt < FeaturedStickersActivity.this.stickersEndRow) && (FeaturedStickersActivity.this.getParentActivity() != null))
        {
          localStickerSetCovered = (TLRPC.StickerSetCovered)StickersQuery.getFeaturedStickerSets().get(paramAnonymousInt);
          if (localStickerSetCovered.set.id == 0L) {
            break label136;
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
              FeaturedStickersActivity.this.installingStickerSets.put(Long.valueOf(localStickerSetCovered.set.id), localStickerSetCovered);
            }
            
            public void onStickerSetUninstalled() {}
          });
          FeaturedStickersActivity.this.showDialog((Dialog)localObject);
          return;
          label136:
          localObject = new TLRPC.TL_inputStickerSetShortName();
          ((TLRPC.InputStickerSet)localObject).short_name = localStickerSetCovered.set.short_name;
        }
      }
    });
    return this.fragmentView;
  }
  
  public void didReceivedNotification(int paramInt, Object... paramVarArgs)
  {
    if (paramInt == NotificationCenter.featuredStickersDidLoaded)
    {
      if (this.unreadStickers == null) {
        this.unreadStickers = StickersQuery.getUnreadStickerSets();
      }
      updateRows();
    }
    while (paramInt != NotificationCenter.stickersDidLoaded) {
      return;
    }
    updateVisibleTrendingSets();
  }
  
  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    StickersQuery.checkFeaturedStickers();
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.featuredStickersDidLoaded);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.stickersDidLoaded);
    ArrayList localArrayList = StickersQuery.getUnreadStickerSets();
    if (localArrayList != null) {
      this.unreadStickers = new ArrayList(localArrayList);
    }
    updateRows();
    return true;
  }
  
  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.featuredStickersDidLoaded);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.stickersDidLoaded);
  }
  
  public void onResume()
  {
    super.onResume();
    if (this.listAdapter != null) {
      this.listAdapter.notifyDataSetChanged();
    }
  }
  
  private class ListAdapter
    extends RecyclerView.Adapter
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
      if ((paramInt >= FeaturedStickersActivity.this.stickersStartRow) && (paramInt < FeaturedStickersActivity.this.stickersEndRow)) {}
      while (paramInt != FeaturedStickersActivity.this.stickersShadowRow) {
        return 0;
      }
      return 1;
    }
    
    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      boolean bool2 = true;
      TLRPC.StickerSetCovered localStickerSetCovered;
      boolean bool1;
      if (getItemViewType(paramInt) == 0)
      {
        ArrayList localArrayList = StickersQuery.getFeaturedStickerSets();
        paramViewHolder = (FeaturedStickerSetCell)paramViewHolder.itemView;
        paramViewHolder.setTag(Integer.valueOf(paramInt));
        localStickerSetCovered = (TLRPC.StickerSetCovered)localArrayList.get(paramInt);
        if (paramInt == localArrayList.size() - 1) {
          break label175;
        }
        bool1 = true;
        if ((FeaturedStickersActivity.this.unreadStickers == null) || (!FeaturedStickersActivity.this.unreadStickers.contains(Long.valueOf(localStickerSetCovered.set.id)))) {
          break label180;
        }
      }
      for (;;)
      {
        paramViewHolder.setStickersSet(localStickerSetCovered, bool1, bool2);
        bool2 = FeaturedStickersActivity.this.installingStickerSets.containsKey(Long.valueOf(localStickerSetCovered.set.id));
        bool1 = bool2;
        if (bool2)
        {
          bool1 = bool2;
          if (paramViewHolder.isInstalled())
          {
            FeaturedStickersActivity.this.installingStickerSets.remove(Long.valueOf(localStickerSetCovered.set.id));
            bool1 = false;
            paramViewHolder.setDrawProgress(false);
          }
        }
        paramViewHolder.setDrawProgress(bool1);
        return;
        label175:
        bool1 = false;
        break;
        label180:
        bool2 = false;
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
        return new Holder(paramViewGroup);
        paramViewGroup = new FeaturedStickerSetCell(this.mContext);
        paramViewGroup.setBackgroundResource(2130837799);
        ((FeaturedStickerSetCell)paramViewGroup).setAddOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            paramAnonymousView = (FeaturedStickerSetCell)paramAnonymousView.getParent();
            TLRPC.StickerSetCovered localStickerSetCovered = paramAnonymousView.getStickerSet();
            if (FeaturedStickersActivity.this.installingStickerSets.containsKey(Long.valueOf(localStickerSetCovered.set.id))) {
              return;
            }
            FeaturedStickersActivity.this.installingStickerSets.put(Long.valueOf(localStickerSetCovered.set.id), localStickerSetCovered);
            StickersQuery.removeStickersSet(FeaturedStickersActivity.this.getParentActivity(), localStickerSetCovered.set, 2, FeaturedStickersActivity.this, false);
            paramAnonymousView.setDrawProgress(true);
          }
        });
        continue;
        paramViewGroup = new TextInfoPrivacyCell(this.mContext);
        paramViewGroup.setBackgroundResource(2130837689);
      }
    }
    
    private class Holder
      extends RecyclerView.ViewHolder
    {
      public Holder(View paramView)
      {
        super();
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/FeaturedStickersActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */