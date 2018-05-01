package org.telegram.ui;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Locale;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.DataQuery;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
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
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.StickerSetCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
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
  private int suggestInfoRow;
  private int suggestRow;
  
  public StickersActivity(int paramInt)
  {
    this.currentType = paramInt;
  }
  
  private void sendReorder()
  {
    if (!this.needReorder) {}
    for (;;)
    {
      return;
      DataQuery.getInstance(this.currentAccount).calcNewHash(this.currentType);
      this.needReorder = false;
      TLRPC.TL_messages_reorderStickerSets localTL_messages_reorderStickerSets = new TLRPC.TL_messages_reorderStickerSets();
      if (this.currentType == 1) {}
      for (boolean bool = true;; bool = false)
      {
        localTL_messages_reorderStickerSets.masks = bool;
        ArrayList localArrayList = DataQuery.getInstance(this.currentAccount).getStickerSets(this.currentType);
        for (int i = 0; i < localArrayList.size(); i++) {
          localTL_messages_reorderStickerSets.order.add(Long.valueOf(((TLRPC.TL_messages_stickerSet)localArrayList.get(i)).set.id));
        }
      }
      ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_messages_reorderStickerSets, new RequestDelegate()
      {
        public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error) {}
      });
      NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.stickersDidLoaded, new Object[] { Integer.valueOf(this.currentType) });
    }
  }
  
  private void updateRows()
  {
    this.rowCount = 0;
    int i;
    if (this.currentType == 0)
    {
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.suggestRow = i;
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
      if (DataQuery.getInstance(this.currentAccount).getArchivedStickersCount(this.currentType) == 0) {
        break label259;
      }
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.archivedRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.archivedInfoRow = i;
      label148:
      ArrayList localArrayList = DataQuery.getInstance(this.currentAccount).getStickerSets(this.currentType);
      if (localArrayList.isEmpty()) {
        break label272;
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
      break;
      label259:
      this.archivedRow = -1;
      this.archivedInfoRow = -1;
      break label148;
      label272:
      this.stickersStartRow = -1;
      this.stickersEndRow = -1;
    }
  }
  
  public View createView(Context paramContext)
  {
    this.actionBar.setBackButtonImage(NUM);
    this.actionBar.setAllowOverlayTitle(true);
    if (this.currentType == 0) {
      this.actionBar.setTitle(LocaleController.getString("StickersName", NUM));
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
      localFrameLayout.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
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
          Object localObject;
          if ((paramAnonymousInt >= StickersActivity.this.stickersStartRow) && (paramAnonymousInt < StickersActivity.this.stickersEndRow) && (StickersActivity.this.getParentActivity() != null))
          {
            StickersActivity.this.sendReorder();
            paramAnonymousView = (TLRPC.TL_messages_stickerSet)DataQuery.getInstance(StickersActivity.this.currentAccount).getStickerSets(StickersActivity.this.currentType).get(paramAnonymousInt - StickersActivity.this.stickersStartRow);
            localObject = paramAnonymousView.documents;
            if ((localObject != null) && (!((ArrayList)localObject).isEmpty())) {}
          }
          for (;;)
          {
            return;
            StickersActivity.this.showDialog(new StickersAlert(StickersActivity.this.getParentActivity(), StickersActivity.this, null, paramAnonymousView, null));
            continue;
            if (paramAnonymousInt == StickersActivity.this.featuredRow)
            {
              StickersActivity.this.sendReorder();
              StickersActivity.this.presentFragment(new FeaturedStickersActivity());
            }
            else if (paramAnonymousInt == StickersActivity.this.archivedRow)
            {
              StickersActivity.this.sendReorder();
              StickersActivity.this.presentFragment(new ArchivedStickersActivity(StickersActivity.this.currentType));
            }
            else if (paramAnonymousInt == StickersActivity.this.masksRow)
            {
              StickersActivity.this.presentFragment(new StickersActivity(1));
            }
            else if (paramAnonymousInt == StickersActivity.this.suggestRow)
            {
              AlertDialog.Builder localBuilder = new AlertDialog.Builder(StickersActivity.this.getParentActivity());
              localBuilder.setTitle(LocaleController.getString("SuggestStickers", NUM));
              String str1 = LocaleController.getString("SuggestStickersAll", NUM);
              String str2 = LocaleController.getString("SuggestStickersInstalled", NUM);
              localObject = LocaleController.getString("SuggestStickersNone", NUM);
              paramAnonymousView = new DialogInterface.OnClickListener()
              {
                public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
                {
                  SharedConfig.setSuggestStickers(paramAnonymous2Int);
                  StickersActivity.this.listAdapter.notifyItemChanged(StickersActivity.this.suggestRow);
                }
              };
              localBuilder.setItems(new CharSequence[] { str1, str2, localObject }, paramAnonymousView);
              StickersActivity.this.showDialog(localBuilder.create());
            }
          }
        }
      });
      return this.fragmentView;
      this.actionBar.setTitle(LocaleController.getString("Masks", NUM));
    }
  }
  
  public void didReceivedNotification(int paramInt1, int paramInt2, Object... paramVarArgs)
  {
    if (paramInt1 == NotificationCenter.stickersDidLoaded) {
      if (((Integer)paramVarArgs[0]).intValue() == this.currentType) {
        updateRows();
      }
    }
    for (;;)
    {
      return;
      if (paramInt1 == NotificationCenter.featuredStickersDidLoaded)
      {
        if (this.listAdapter != null) {
          this.listAdapter.notifyItemChanged(0);
        }
      }
      else if ((paramInt1 == NotificationCenter.archivedStickersCountDidLoaded) && (((Integer)paramVarArgs[0]).intValue() == this.currentType)) {
        updateRows();
      }
    }
  }
  
  public ThemeDescription[] getThemeDescriptions()
  {
    ThemeDescription localThemeDescription1 = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[] { StickerSetCell.class, TextSettingsCell.class }, null, null, null, "windowBackgroundWhite");
    ThemeDescription localThemeDescription2 = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray");
    ThemeDescription localThemeDescription3 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription4 = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription5 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
    ThemeDescription localThemeDescription6 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
    ThemeDescription localThemeDescription7 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
    ThemeDescription localThemeDescription8 = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
    RecyclerListView localRecyclerListView = this.listView;
    Paint localPaint = Theme.dividerPaint;
    return new ThemeDescription[] { localThemeDescription1, localThemeDescription2, localThemeDescription3, localThemeDescription4, localThemeDescription5, localThemeDescription6, localThemeDescription7, localThemeDescription8, new ThemeDescription(localRecyclerListView, 0, new Class[] { View.class }, localPaint, null, null, "divider"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { TextInfoPrivacyCell.class }, null, null, null, "windowBackgroundGrayShadow"), new ThemeDescription(this.listView, 0, new Class[] { TextInfoPrivacyCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteGrayText4"), new ThemeDescription(this.listView, ThemeDescription.FLAG_LINKCOLOR, new Class[] { TextInfoPrivacyCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteLinkText"), new ThemeDescription(this.listView, 0, new Class[] { TextSettingsCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.listView, 0, new Class[] { TextSettingsCell.class }, new String[] { "valueTextView" }, null, null, null, "windowBackgroundWhiteValueText"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { ShadowSectionCell.class }, null, null, null, "windowBackgroundGrayShadow"), new ThemeDescription(this.listView, 0, new Class[] { StickerSetCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.listView, 0, new Class[] { StickerSetCell.class }, new String[] { "valueTextView" }, null, null, null, "windowBackgroundWhiteGrayText2"), new ThemeDescription(this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[] { StickerSetCell.class }, new String[] { "optionsButton" }, null, null, null, "stickers_menuSelector"), new ThemeDescription(this.listView, 0, new Class[] { StickerSetCell.class }, new String[] { "optionsButton" }, null, null, null, "stickers_menu") };
  }
  
  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    DataQuery.getInstance(this.currentAccount).checkStickers(this.currentType);
    if (this.currentType == 0) {
      DataQuery.getInstance(this.currentAccount).checkFeaturedStickers();
    }
    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.stickersDidLoaded);
    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.archivedStickersCountDidLoaded);
    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.featuredStickersDidLoaded);
    updateRows();
    return true;
  }
  
  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.stickersDidLoaded);
    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.archivedStickersCountDidLoaded);
    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.featuredStickersDidLoaded);
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
    extends RecyclerListView.SelectionAdapter
  {
    private Context mContext;
    
    public ListAdapter(Context paramContext)
    {
      this.mContext = paramContext;
    }
    
    private void processSelectionOption(int paramInt, TLRPC.TL_messages_stickerSet paramTL_messages_stickerSet)
    {
      Object localObject1;
      Object localObject2;
      Object localObject3;
      if (paramInt == 0)
      {
        localObject1 = DataQuery.getInstance(StickersActivity.this.currentAccount);
        localObject2 = StickersActivity.this.getParentActivity();
        localObject3 = paramTL_messages_stickerSet.set;
        if (!paramTL_messages_stickerSet.set.archived)
        {
          paramInt = 1;
          ((DataQuery)localObject1).removeStickersSet((Context)localObject2, (TLRPC.StickerSet)localObject3, paramInt, StickersActivity.this, true);
        }
      }
      for (;;)
      {
        return;
        paramInt = 2;
        break;
        if (paramInt == 1) {
          DataQuery.getInstance(StickersActivity.this.currentAccount).removeStickersSet(StickersActivity.this.getParentActivity(), paramTL_messages_stickerSet.set, 0, StickersActivity.this, true);
        } else if (paramInt == 2) {
          try
          {
            localObject1 = new android/content/Intent;
            ((Intent)localObject1).<init>("android.intent.action.SEND");
            ((Intent)localObject1).setType("text/plain");
            localObject3 = Locale.US;
            localObject2 = new java/lang/StringBuilder;
            ((StringBuilder)localObject2).<init>();
            ((Intent)localObject1).putExtra("android.intent.extra.TEXT", String.format((Locale)localObject3, "https://" + MessagesController.getInstance(StickersActivity.this.currentAccount).linkPrefix + "/addstickers/%s", new Object[] { paramTL_messages_stickerSet.set.short_name }));
            StickersActivity.this.getParentActivity().startActivityForResult(Intent.createChooser((Intent)localObject1, LocaleController.getString("StickersShare", NUM)), 500);
          }
          catch (Exception paramTL_messages_stickerSet)
          {
            FileLog.e(paramTL_messages_stickerSet);
          }
        } else if (paramInt == 3) {
          try
          {
            localObject2 = (ClipboardManager)ApplicationLoader.applicationContext.getSystemService("clipboard");
            localObject3 = Locale.US;
            localObject1 = new java/lang/StringBuilder;
            ((StringBuilder)localObject1).<init>();
            ((ClipboardManager)localObject2).setPrimaryClip(ClipData.newPlainText("label", String.format((Locale)localObject3, "https://" + MessagesController.getInstance(StickersActivity.this.currentAccount).linkPrefix + "/addstickers/%s", new Object[] { paramTL_messages_stickerSet.set.short_name })));
            Toast.makeText(StickersActivity.this.getParentActivity(), LocaleController.getString("LinkCopied", NUM), 0).show();
          }
          catch (Exception paramTL_messages_stickerSet)
          {
            FileLog.e(paramTL_messages_stickerSet);
          }
        }
      }
    }
    
    public int getItemCount()
    {
      return StickersActivity.this.rowCount;
    }
    
    public long getItemId(int paramInt)
    {
      long l;
      if ((paramInt >= StickersActivity.this.stickersStartRow) && (paramInt < StickersActivity.this.stickersEndRow)) {
        l = ((TLRPC.TL_messages_stickerSet)DataQuery.getInstance(StickersActivity.this.currentAccount).getStickerSets(StickersActivity.this.currentType).get(paramInt - StickersActivity.this.stickersStartRow)).set.id;
      }
      for (;;)
      {
        return l;
        if ((paramInt == StickersActivity.this.suggestRow) || (paramInt == StickersActivity.this.suggestInfoRow) || (paramInt == StickersActivity.this.archivedRow) || (paramInt == StickersActivity.this.archivedInfoRow) || (paramInt == StickersActivity.this.featuredRow) || (paramInt == StickersActivity.this.featuredInfoRow) || (paramInt == StickersActivity.this.masksRow) || (paramInt == StickersActivity.this.masksInfoRow)) {
          l = -2147483648L;
        } else {
          l = paramInt;
        }
      }
    }
    
    public int getItemViewType(int paramInt)
    {
      int i = 0;
      if ((paramInt >= StickersActivity.this.stickersStartRow) && (paramInt < StickersActivity.this.stickersEndRow)) {}
      for (;;)
      {
        return i;
        if ((paramInt == StickersActivity.this.featuredInfoRow) || (paramInt == StickersActivity.this.archivedInfoRow) || (paramInt == StickersActivity.this.masksInfoRow)) {
          i = 1;
        } else if ((paramInt == StickersActivity.this.featuredRow) || (paramInt == StickersActivity.this.archivedRow) || (paramInt == StickersActivity.this.masksRow) || (paramInt == StickersActivity.this.suggestRow)) {
          i = 2;
        } else if ((paramInt == StickersActivity.this.stickersShadowRow) || (paramInt == StickersActivity.this.suggestInfoRow)) {
          i = 3;
        }
      }
    }
    
    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      int i = paramViewHolder.getItemViewType();
      if ((i == 0) || (i == 2)) {}
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
        Object localObject1 = DataQuery.getInstance(StickersActivity.this.currentAccount).getStickerSets(StickersActivity.this.currentType);
        paramInt -= StickersActivity.this.stickersStartRow;
        paramViewHolder = (StickerSetCell)paramViewHolder.itemView;
        Object localObject2 = (TLRPC.TL_messages_stickerSet)((ArrayList)localObject1).get(paramInt);
        if (paramInt != ((ArrayList)localObject1).size() - 1) {}
        for (boolean bool = true;; bool = false)
        {
          paramViewHolder.setStickersSet((TLRPC.TL_messages_stickerSet)localObject2, bool);
          break;
        }
        if (paramInt == StickersActivity.this.featuredInfoRow)
        {
          localObject1 = LocaleController.getString("FeaturedStickersInfo", NUM);
          paramInt = ((String)localObject1).indexOf("@stickers");
          if (paramInt != -1) {
            try
            {
              localObject2 = new android/text/SpannableStringBuilder;
              ((SpannableStringBuilder)localObject2).<init>((CharSequence)localObject1);
              URLSpanNoUnderline local1 = new org/telegram/ui/StickersActivity$ListAdapter$1;
              local1.<init>(this, "@stickers");
              ((SpannableStringBuilder)localObject2).setSpan(local1, paramInt, "@stickers".length() + paramInt, 18);
              ((TextInfoPrivacyCell)paramViewHolder.itemView).setText((CharSequence)localObject2);
            }
            catch (Exception localException)
            {
              FileLog.e(localException);
              ((TextInfoPrivacyCell)paramViewHolder.itemView).setText((CharSequence)localObject1);
            }
          } else {
            ((TextInfoPrivacyCell)paramViewHolder.itemView).setText((CharSequence)localObject1);
          }
        }
        else if (paramInt == StickersActivity.this.archivedInfoRow)
        {
          if (StickersActivity.this.currentType == 0) {
            ((TextInfoPrivacyCell)paramViewHolder.itemView).setText(LocaleController.getString("ArchivedStickersInfo", NUM));
          } else {
            ((TextInfoPrivacyCell)paramViewHolder.itemView).setText(LocaleController.getString("ArchivedMasksInfo", NUM));
          }
        }
        else if (paramInt == StickersActivity.this.masksInfoRow)
        {
          ((TextInfoPrivacyCell)paramViewHolder.itemView).setText(LocaleController.getString("MasksInfo", NUM));
          continue;
          if (paramInt == StickersActivity.this.featuredRow)
          {
            paramInt = DataQuery.getInstance(StickersActivity.this.currentAccount).getUnreadStickerSets().size();
            TextSettingsCell localTextSettingsCell = (TextSettingsCell)paramViewHolder.itemView;
            localObject1 = LocaleController.getString("FeaturedStickers", NUM);
            if (paramInt != 0) {}
            for (paramViewHolder = String.format("%d", new Object[] { Integer.valueOf(paramInt) });; paramViewHolder = "")
            {
              localTextSettingsCell.setTextAndValue((String)localObject1, paramViewHolder, false);
              break;
            }
          }
          if (paramInt == StickersActivity.this.archivedRow)
          {
            if (StickersActivity.this.currentType == 0) {
              ((TextSettingsCell)paramViewHolder.itemView).setText(LocaleController.getString("ArchivedStickers", NUM), false);
            } else {
              ((TextSettingsCell)paramViewHolder.itemView).setText(LocaleController.getString("ArchivedMasks", NUM), false);
            }
          }
          else if (paramInt == StickersActivity.this.masksRow)
          {
            ((TextSettingsCell)paramViewHolder.itemView).setText(LocaleController.getString("Masks", NUM), false);
          }
          else if (paramInt == StickersActivity.this.suggestRow)
          {
            switch (SharedConfig.suggestStickers)
            {
            default: 
              localObject1 = LocaleController.getString("SuggestStickersNone", NUM);
            }
            for (;;)
            {
              ((TextSettingsCell)paramViewHolder.itemView).setTextAndValue(LocaleController.getString("SuggestStickers", NUM), (String)localObject1, true);
              break;
              localObject1 = LocaleController.getString("SuggestStickersAll", NUM);
              continue;
              localObject1 = LocaleController.getString("SuggestStickersInstalled", NUM);
            }
            if (paramInt == StickersActivity.this.stickersShadowRow) {
              paramViewHolder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
            } else if (paramInt == StickersActivity.this.suggestInfoRow) {
              paramViewHolder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
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
        paramViewGroup = new StickerSetCell(this.mContext, 1);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
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
                paramAnonymousView[0] = LocaleController.getString("StickersHide", NUM);
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
              int[] tmp139_137 = arrayOfInt;
              tmp139_137[0] = 0;
              int[] tmp143_139 = tmp139_137;
              tmp143_139[1] = 1;
              int[] tmp147_143 = tmp143_139;
              tmp147_143[2] = 2;
              int[] tmp151_147 = tmp147_143;
              tmp151_147[3] = 3;
              tmp151_147;
              paramAnonymousView = new CharSequence[4];
              paramAnonymousView[0] = LocaleController.getString("StickersHide", NUM);
              paramAnonymousView[1] = LocaleController.getString("StickersRemove", NUM);
              paramAnonymousView[2] = LocaleController.getString("StickersShare", NUM);
              paramAnonymousView[3] = LocaleController.getString("StickersCopy", NUM);
              continue;
              if (localTL_messages_stickerSet.set.official)
              {
                arrayOfInt = new int[1];
                arrayOfInt[0] = 0;
                paramAnonymousView = new CharSequence[1];
                paramAnonymousView[0] = LocaleController.getString("StickersRemove", NUM);
              }
              else
              {
                arrayOfInt = new int[4];
                int[] tmp249_247 = arrayOfInt;
                tmp249_247[0] = 0;
                int[] tmp253_249 = tmp249_247;
                tmp253_249[1] = 1;
                int[] tmp257_253 = tmp253_249;
                tmp257_253[2] = 2;
                int[] tmp261_257 = tmp257_253;
                tmp261_257[3] = 3;
                tmp261_257;
                paramAnonymousView = new CharSequence[4];
                paramAnonymousView[0] = LocaleController.getString("StickersHide", NUM);
                paramAnonymousView[1] = LocaleController.getString("StickersRemove", NUM);
                paramAnonymousView[2] = LocaleController.getString("StickersShare", NUM);
                paramAnonymousView[3] = LocaleController.getString("StickersCopy", NUM);
              }
            }
          }
        });
        continue;
        paramViewGroup = new TextInfoPrivacyCell(this.mContext);
        paramViewGroup.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
        continue;
        paramViewGroup = new TextSettingsCell(this.mContext);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        continue;
        paramViewGroup = new ShadowSectionCell(this.mContext);
      }
    }
    
    public void swapElements(int paramInt1, int paramInt2)
    {
      if (paramInt1 != paramInt2) {
        StickersActivity.access$2602(StickersActivity.this, true);
      }
      ArrayList localArrayList = DataQuery.getInstance(StickersActivity.this.currentAccount).getStickerSets(StickersActivity.this.currentType);
      TLRPC.TL_messages_stickerSet localTL_messages_stickerSet = (TLRPC.TL_messages_stickerSet)localArrayList.get(paramInt1 - StickersActivity.this.stickersStartRow);
      localArrayList.set(paramInt1 - StickersActivity.this.stickersStartRow, localArrayList.get(paramInt2 - StickersActivity.this.stickersStartRow));
      localArrayList.set(paramInt2 - StickersActivity.this.stickersStartRow, localTL_messages_stickerSet);
      notifyItemMoved(paramInt1, paramInt2);
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
      if (paramViewHolder.getItemViewType() != 0) {}
      for (int i = makeMovementFlags(0, 0);; i = makeMovementFlags(3, 0)) {
        return i;
      }
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
      if (paramViewHolder1.getItemViewType() != paramViewHolder2.getItemViewType()) {}
      for (boolean bool = false;; bool = true)
      {
        return bool;
        StickersActivity.this.listAdapter.swapElements(paramViewHolder1.getAdapterPosition(), paramViewHolder2.getAdapterPosition());
      }
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