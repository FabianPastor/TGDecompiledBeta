package org.telegram.ui;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.SpannableStringBuilder;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Locale;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.query.StickersQuery;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.StickerSet;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_reorderStickerSets;
import org.telegram.tgnet.TLRPC.TL_messages_stickerSet;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.StickerSetCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.StickersAlert;
import org.telegram.ui.Components.URLSpanNoUnderline;

public class StickersActivity
  extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  private int archivedInfoRow;
  private int archivedRow;
  private int currentType;
  private int featuredInfoRow;
  private int featuredRow;
  private ListAdapter listAdapter;
  private RecyclerListView listView;
  private int masksInfoRow;
  private int masksRow;
  private boolean needReorder;
  private int rowCount;
  private int stickersEndRow;
  private int stickersShadowRow;
  private int stickersStartRow;
  
  public StickersActivity(int paramInt)
  {
    this.currentType = paramInt;
  }
  
  private void sendReorder()
  {
    if (!this.needReorder) {
      return;
    }
    StickersQuery.calcNewHash(this.currentType);
    this.needReorder = false;
    TLRPC.TL_messages_reorderStickerSets localTL_messages_reorderStickerSets = new TLRPC.TL_messages_reorderStickerSets();
    ArrayList localArrayList = StickersQuery.getStickerSets(this.currentType);
    int i = 0;
    while (i < localArrayList.size())
    {
      localTL_messages_reorderStickerSets.order.add(Long.valueOf(((TLRPC.TL_messages_stickerSet)localArrayList.get(i)).set.id));
      i += 1;
    }
    ConnectionsManager.getInstance().sendRequest(localTL_messages_reorderStickerSets, new RequestDelegate()
    {
      public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error) {}
    });
    NotificationCenter.getInstance().postNotificationName(NotificationCenter.stickersDidLoaded, new Object[] { Integer.valueOf(this.currentType) });
  }
  
  private void updateRows()
  {
    this.rowCount = 0;
    int i;
    if (this.currentType == 0)
    {
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.featuredRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.featuredInfoRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.masksRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.masksInfoRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.archivedRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.archivedInfoRow = i;
      ArrayList localArrayList = StickersQuery.getStickerSets(this.currentType);
      if (localArrayList.isEmpty()) {
        break label252;
      }
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
      return;
      this.featuredRow = -1;
      this.featuredInfoRow = -1;
      this.masksRow = -1;
      this.masksInfoRow = -1;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.archivedRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.archivedInfoRow = i;
      break;
      label252:
      this.stickersStartRow = -1;
      this.stickersEndRow = -1;
    }
  }
  
  public View createView(Context paramContext)
  {
    this.actionBar.setBackButtonImage(2130837700);
    this.actionBar.setAllowOverlayTitle(true);
    if (this.currentType == 0) {
      this.actionBar.setTitle(LocaleController.getString("Stickers", 2131166310));
    }
    for (;;)
    {
      this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
      {
        public void onItemClick(int paramAnonymousInt)
        {
          if (paramAnonymousInt == -1) {
            StickersActivity.this.finishFragment();
          }
        }
      });
      this.listAdapter = new ListAdapter(paramContext);
      this.fragmentView = new FrameLayout(paramContext);
      FrameLayout localFrameLayout = (FrameLayout)this.fragmentView;
      localFrameLayout.setBackgroundColor(-986896);
      this.listView = new RecyclerListView(paramContext);
      this.listView.setFocusable(true);
      this.listView.setTag(Integer.valueOf(7));
      paramContext = new LinearLayoutManager(paramContext);
      paramContext.setOrientation(1);
      this.listView.setLayoutManager(paramContext);
      new ItemTouchHelper(new TouchHelperCallback()).attachToRecyclerView(this.listView);
      localFrameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0F));
      this.listView.setAdapter(this.listAdapter);
      this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
      {
        public void onItemClick(View paramAnonymousView, int paramAnonymousInt)
        {
          if ((paramAnonymousInt >= StickersActivity.this.stickersStartRow) && (paramAnonymousInt < StickersActivity.this.stickersEndRow) && (StickersActivity.this.getParentActivity() != null))
          {
            StickersActivity.this.sendReorder();
            paramAnonymousView = (TLRPC.TL_messages_stickerSet)StickersQuery.getStickerSets(StickersActivity.this.currentType).get(paramAnonymousInt - StickersActivity.this.stickersStartRow);
            ArrayList localArrayList = paramAnonymousView.documents;
            if ((localArrayList != null) && (!localArrayList.isEmpty())) {}
          }
          do
          {
            return;
            StickersActivity.this.showDialog(new StickersAlert(StickersActivity.this.getParentActivity(), StickersActivity.this, null, paramAnonymousView, null));
            return;
            if (paramAnonymousInt == StickersActivity.this.featuredRow)
            {
              StickersActivity.this.presentFragment(new FeaturedStickersActivity());
              return;
            }
            if (paramAnonymousInt == StickersActivity.this.archivedRow)
            {
              StickersActivity.this.presentFragment(new ArchivedStickersActivity(StickersActivity.this.currentType));
              return;
            }
          } while (paramAnonymousInt != StickersActivity.this.masksRow);
          StickersActivity.this.presentFragment(new StickersActivity(1));
        }
      });
      return this.fragmentView;
      this.actionBar.setTitle(LocaleController.getString("Masks", 2131165849));
    }
  }
  
  public void didReceivedNotification(int paramInt, Object... paramVarArgs)
  {
    if (paramInt == NotificationCenter.stickersDidLoaded) {
      if (((Integer)paramVarArgs[0]).intValue() == this.currentType) {
        updateRows();
      }
    }
    while ((paramInt != NotificationCenter.featuredStickersDidLoaded) || (this.listAdapter == null)) {
      return;
    }
    this.listAdapter.notifyItemChanged(0);
  }
  
  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    StickersQuery.checkStickers(this.currentType);
    if (this.currentType == 0) {
      StickersQuery.checkFeaturedStickers();
    }
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.stickersDidLoaded);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.featuredStickersDidLoaded);
    updateRows();
    return true;
  }
  
  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.stickersDidLoaded);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.featuredStickersDidLoaded);
    sendReorder();
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
    
    private void processSelectionOption(int paramInt, TLRPC.TL_messages_stickerSet paramTL_messages_stickerSet)
    {
      int i = 2;
      Object localObject;
      if (paramInt == 0)
      {
        localObject = StickersActivity.this.getParentActivity();
        TLRPC.StickerSet localStickerSet = paramTL_messages_stickerSet.set;
        paramInt = i;
        if (!paramTL_messages_stickerSet.set.archived) {
          paramInt = 1;
        }
        StickersQuery.removeStickersSet((Context)localObject, localStickerSet, paramInt, StickersActivity.this, true);
      }
      do
      {
        return;
        if (paramInt == 1)
        {
          StickersQuery.removeStickersSet(StickersActivity.this.getParentActivity(), paramTL_messages_stickerSet.set, 0, StickersActivity.this, true);
          return;
        }
        if (paramInt == 2) {
          try
          {
            localObject = new Intent("android.intent.action.SEND");
            ((Intent)localObject).setType("text/plain");
            ((Intent)localObject).putExtra("android.intent.extra.TEXT", String.format(Locale.US, "https://telegram.me/addstickers/%s", new Object[] { paramTL_messages_stickerSet.set.short_name }));
            StickersActivity.this.getParentActivity().startActivityForResult(Intent.createChooser((Intent)localObject, LocaleController.getString("StickersShare", 2131166315)), 500);
            return;
          }
          catch (Exception paramTL_messages_stickerSet)
          {
            FileLog.e("tmessages", paramTL_messages_stickerSet);
            return;
          }
        }
      } while (paramInt != 3);
      try
      {
        ((ClipboardManager)ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", String.format(Locale.US, "https://telegram.me/addstickers/%s", new Object[] { paramTL_messages_stickerSet.set.short_name })));
        Toast.makeText(StickersActivity.this.getParentActivity(), LocaleController.getString("LinkCopied", 2131165823), 0).show();
        return;
      }
      catch (Exception paramTL_messages_stickerSet)
      {
        FileLog.e("tmessages", paramTL_messages_stickerSet);
      }
    }
    
    public int getItemCount()
    {
      return StickersActivity.this.rowCount;
    }
    
    public long getItemId(int paramInt)
    {
      if ((paramInt >= StickersActivity.this.stickersStartRow) && (paramInt < StickersActivity.this.stickersEndRow)) {
        return ((TLRPC.TL_messages_stickerSet)StickersQuery.getStickerSets(StickersActivity.this.currentType).get(paramInt - StickersActivity.this.stickersStartRow)).set.id;
      }
      if ((paramInt == StickersActivity.this.archivedRow) || (paramInt == StickersActivity.this.archivedInfoRow) || (paramInt == StickersActivity.this.featuredRow) || (paramInt == StickersActivity.this.featuredInfoRow) || (paramInt == StickersActivity.this.masksRow) || (paramInt == StickersActivity.this.masksInfoRow)) {
        return -2147483648L;
      }
      return paramInt;
    }
    
    public int getItemViewType(int paramInt)
    {
      if ((paramInt >= StickersActivity.this.stickersStartRow) && (paramInt < StickersActivity.this.stickersEndRow)) {}
      do
      {
        return 0;
        if ((paramInt == StickersActivity.this.featuredInfoRow) || (paramInt == StickersActivity.this.archivedInfoRow) || (paramInt == StickersActivity.this.masksInfoRow)) {
          return 1;
        }
        if ((paramInt == StickersActivity.this.featuredRow) || (paramInt == StickersActivity.this.archivedRow) || (paramInt == StickersActivity.this.masksRow)) {
          return 2;
        }
      } while (paramInt != StickersActivity.this.stickersShadowRow);
      return 3;
    }
    
    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      switch (paramViewHolder.getItemViewType())
      {
      }
      do
      {
        Object localObject1;
        do
        {
          return;
          localObject1 = StickersQuery.getStickerSets(StickersActivity.this.currentType);
          paramInt -= StickersActivity.this.stickersStartRow;
          paramViewHolder = (StickerSetCell)paramViewHolder.itemView;
          Object localObject2 = (TLRPC.TL_messages_stickerSet)((ArrayList)localObject1).get(paramInt);
          if (paramInt != ((ArrayList)localObject1).size() - 1) {}
          for (boolean bool = true;; bool = false)
          {
            paramViewHolder.setStickersSet((TLRPC.TL_messages_stickerSet)localObject2, bool);
            return;
          }
          if (paramInt == StickersActivity.this.featuredInfoRow)
          {
            localObject1 = LocaleController.getString("FeaturedStickersInfo", 2131165633);
            paramInt = ((String)localObject1).indexOf("@stickers");
            if (paramInt != -1) {
              try
              {
                localObject2 = new SpannableStringBuilder((CharSequence)localObject1);
                ((SpannableStringBuilder)localObject2).setSpan(new URLSpanNoUnderline("@stickers")
                {
                  public void onClick(View paramAnonymousView)
                  {
                    MessagesController.openByUserName("stickers", StickersActivity.this, 1);
                  }
                }, paramInt, "@stickers".length() + paramInt, 18);
                ((TextInfoPrivacyCell)paramViewHolder.itemView).setText((CharSequence)localObject2);
                return;
              }
              catch (Exception localException)
              {
                FileLog.e("tmessages", localException);
                ((TextInfoPrivacyCell)paramViewHolder.itemView).setText((CharSequence)localObject1);
                return;
              }
            }
            ((TextInfoPrivacyCell)paramViewHolder.itemView).setText((CharSequence)localObject1);
            return;
          }
          if (paramInt == StickersActivity.this.archivedInfoRow)
          {
            if (StickersActivity.this.currentType == 0)
            {
              ((TextInfoPrivacyCell)paramViewHolder.itemView).setText(LocaleController.getString("ArchivedStickersInfo", 2131165311));
              return;
            }
            ((TextInfoPrivacyCell)paramViewHolder.itemView).setText(LocaleController.getString("ArchivedMasksInfo", 2131165306));
            return;
          }
        } while (paramInt != StickersActivity.this.masksInfoRow);
        ((TextInfoPrivacyCell)paramViewHolder.itemView).setText(LocaleController.getString("MasksInfo", 2131165850));
        return;
        if (paramInt == StickersActivity.this.featuredRow)
        {
          paramInt = StickersQuery.getUnreadStickerSets().size();
          localObject1 = (TextSettingsCell)paramViewHolder.itemView;
          String str = LocaleController.getString("FeaturedStickers", 2131165632);
          if (paramInt != 0) {}
          for (paramViewHolder = String.format("%d", new Object[] { Integer.valueOf(paramInt) });; paramViewHolder = "")
          {
            ((TextSettingsCell)localObject1).setTextAndValue(str, paramViewHolder, false);
            return;
          }
        }
        if (paramInt == StickersActivity.this.archivedRow)
        {
          if (StickersActivity.this.currentType == 0)
          {
            ((TextSettingsCell)paramViewHolder.itemView).setText(LocaleController.getString("ArchivedStickers", 2131165307), false);
            return;
          }
          ((TextSettingsCell)paramViewHolder.itemView).setText(LocaleController.getString("ArchivedMasks", 2131165302), false);
          return;
        }
      } while (paramInt != StickersActivity.this.masksRow);
      ((TextSettingsCell)paramViewHolder.itemView).setText(LocaleController.getString("Masks", 2131165849), true);
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
        paramViewGroup = new StickerSetCell(this.mContext);
        paramViewGroup.setBackgroundResource(2130837799);
        ((StickerSetCell)paramViewGroup).setOnOptionsClick(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            StickersActivity.this.sendReorder();
            final TLRPC.TL_messages_stickerSet localTL_messages_stickerSet = ((StickerSetCell)paramAnonymousView.getParent()).getStickersSet();
            AlertDialog.Builder localBuilder = new AlertDialog.Builder(StickersActivity.this.getParentActivity());
            localBuilder.setTitle(localTL_messages_stickerSet.set.title);
            final int[] arrayOfInt;
            if (StickersActivity.this.currentType == 0) {
              if (localTL_messages_stickerSet.set.official)
              {
                arrayOfInt = new int[1];
                arrayOfInt[0] = 0;
                paramAnonymousView = new CharSequence[1];
                paramAnonymousView[0] = LocaleController.getString("StickersHide", 2131166312);
              }
            }
            for (;;)
            {
              localBuilder.setItems(paramAnonymousView, new DialogInterface.OnClickListener()
              {
                public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
                {
                  StickersActivity.ListAdapter.this.processSelectionOption(arrayOfInt[paramAnonymous2Int], localTL_messages_stickerSet);
                }
              });
              StickersActivity.this.showDialog(localBuilder.create());
              return;
              arrayOfInt = new int[4];
              int[] tmp138_137 = arrayOfInt;
              tmp138_137[0] = 0;
              int[] tmp142_138 = tmp138_137;
              tmp142_138[1] = 1;
              int[] tmp146_142 = tmp142_138;
              tmp146_142[2] = 2;
              int[] tmp150_146 = tmp146_142;
              tmp150_146[3] = 3;
              tmp150_146;
              paramAnonymousView = new CharSequence[4];
              paramAnonymousView[0] = LocaleController.getString("StickersHide", 2131166312);
              paramAnonymousView[1] = LocaleController.getString("StickersRemove", 2131166313);
              paramAnonymousView[2] = LocaleController.getString("StickersShare", 2131166315);
              paramAnonymousView[3] = LocaleController.getString("StickersCopy", 2131166311);
              continue;
              if (localTL_messages_stickerSet.set.official)
              {
                arrayOfInt = new int[1];
                arrayOfInt[0] = 0;
                paramAnonymousView = new CharSequence[1];
                paramAnonymousView[0] = LocaleController.getString("StickersRemove", 2131166312);
              }
              else
              {
                arrayOfInt = new int[4];
                int[] tmp244_243 = arrayOfInt;
                tmp244_243[0] = 0;
                int[] tmp248_244 = tmp244_243;
                tmp248_244[1] = 1;
                int[] tmp252_248 = tmp248_244;
                tmp252_248[2] = 2;
                int[] tmp256_252 = tmp252_248;
                tmp256_252[3] = 3;
                tmp256_252;
                paramAnonymousView = new CharSequence[4];
                paramAnonymousView[0] = LocaleController.getString("StickersHide", 2131166312);
                paramAnonymousView[1] = LocaleController.getString("StickersRemove", 2131166313);
                paramAnonymousView[2] = LocaleController.getString("StickersShare", 2131166315);
                paramAnonymousView[3] = LocaleController.getString("StickersCopy", 2131166311);
              }
            }
          }
        });
        continue;
        paramViewGroup = new TextInfoPrivacyCell(this.mContext);
        paramViewGroup.setBackgroundResource(2130837689);
        continue;
        paramViewGroup = new TextSettingsCell(this.mContext)
        {
          public boolean onTouchEvent(MotionEvent paramAnonymousMotionEvent)
          {
            if ((Build.VERSION.SDK_INT >= 21) && (getBackground() != null) && ((paramAnonymousMotionEvent.getAction() == 0) || (paramAnonymousMotionEvent.getAction() == 2))) {
              getBackground().setHotspot(paramAnonymousMotionEvent.getX(), paramAnonymousMotionEvent.getY());
            }
            return super.onTouchEvent(paramAnonymousMotionEvent);
          }
        };
        paramViewGroup.setBackgroundResource(2130837799);
        continue;
        paramViewGroup = new ShadowSectionCell(this.mContext);
        paramViewGroup.setBackgroundResource(2130837689);
      }
    }
    
    public void swapElements(int paramInt1, int paramInt2)
    {
      if (paramInt1 != paramInt2) {
        StickersActivity.access$1502(StickersActivity.this, true);
      }
      ArrayList localArrayList = StickersQuery.getStickerSets(StickersActivity.this.currentType);
      TLRPC.TL_messages_stickerSet localTL_messages_stickerSet = (TLRPC.TL_messages_stickerSet)localArrayList.get(paramInt1 - StickersActivity.this.stickersStartRow);
      localArrayList.set(paramInt1 - StickersActivity.this.stickersStartRow, localArrayList.get(paramInt2 - StickersActivity.this.stickersStartRow));
      localArrayList.set(paramInt2 - StickersActivity.this.stickersStartRow, localTL_messages_stickerSet);
      notifyItemMoved(paramInt1, paramInt2);
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
  
  public class TouchHelperCallback
    extends ItemTouchHelper.Callback
  {
    public TouchHelperCallback() {}
    
    public void clearView(RecyclerView paramRecyclerView, RecyclerView.ViewHolder paramViewHolder)
    {
      super.clearView(paramRecyclerView, paramViewHolder);
      paramViewHolder.itemView.setPressed(false);
    }
    
    public int getMovementFlags(RecyclerView paramRecyclerView, RecyclerView.ViewHolder paramViewHolder)
    {
      if (paramViewHolder.getItemViewType() != 0) {
        return makeMovementFlags(0, 0);
      }
      return makeMovementFlags(3, 0);
    }
    
    public boolean isLongPressDragEnabled()
    {
      return true;
    }
    
    public void onChildDraw(Canvas paramCanvas, RecyclerView paramRecyclerView, RecyclerView.ViewHolder paramViewHolder, float paramFloat1, float paramFloat2, int paramInt, boolean paramBoolean)
    {
      super.onChildDraw(paramCanvas, paramRecyclerView, paramViewHolder, paramFloat1, paramFloat2, paramInt, paramBoolean);
    }
    
    public boolean onMove(RecyclerView paramRecyclerView, RecyclerView.ViewHolder paramViewHolder1, RecyclerView.ViewHolder paramViewHolder2)
    {
      if (paramViewHolder1.getItemViewType() != paramViewHolder2.getItemViewType()) {
        return false;
      }
      StickersActivity.this.listAdapter.swapElements(paramViewHolder1.getAdapterPosition(), paramViewHolder2.getAdapterPosition());
      return true;
    }
    
    public void onSelectedChanged(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      if (paramInt != 0)
      {
        StickersActivity.this.listView.cancelClickRunnables(false);
        paramViewHolder.itemView.setPressed(true);
      }
      super.onSelectedChanged(paramViewHolder, paramInt);
    }
    
    public void onSwiped(RecyclerView.ViewHolder paramViewHolder, int paramInt) {}
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/StickersActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */