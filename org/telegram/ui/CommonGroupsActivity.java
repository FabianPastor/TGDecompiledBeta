package org.telegram.ui;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputUserEmpty;
import org.telegram.tgnet.TLRPC.TL_messages_getCommonChats;
import org.telegram.tgnet.TLRPC.messages_Chats;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Cells.ProfileSearchCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class CommonGroupsActivity
  extends BaseFragment
{
  private ArrayList<TLRPC.Chat> chats = new ArrayList();
  private EmptyTextProgressView emptyView;
  private boolean endReached;
  private boolean firstLoaded;
  private LinearLayoutManager layoutManager;
  private RecyclerListView listView;
  private ListAdapter listViewAdapter;
  private boolean loading;
  private int userId;
  
  public CommonGroupsActivity(int paramInt)
  {
    this.userId = paramInt;
  }
  
  private void getChats(int paramInt1, final int paramInt2)
  {
    if (this.loading) {}
    for (;;)
    {
      return;
      this.loading = true;
      if ((this.emptyView != null) && (!this.firstLoaded)) {
        this.emptyView.showProgress();
      }
      if (this.listViewAdapter != null) {
        this.listViewAdapter.notifyDataSetChanged();
      }
      TLRPC.TL_messages_getCommonChats localTL_messages_getCommonChats = new TLRPC.TL_messages_getCommonChats();
      localTL_messages_getCommonChats.user_id = MessagesController.getInstance(this.currentAccount).getInputUser(this.userId);
      if (!(localTL_messages_getCommonChats.user_id instanceof TLRPC.TL_inputUserEmpty))
      {
        localTL_messages_getCommonChats.limit = paramInt2;
        localTL_messages_getCommonChats.max_id = paramInt1;
        paramInt1 = ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_messages_getCommonChats, new RequestDelegate()
        {
          public void run(final TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
          {
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                boolean bool;
                if (paramAnonymousTL_error == null)
                {
                  TLRPC.messages_Chats localmessages_Chats = (TLRPC.messages_Chats)paramAnonymousTLObject;
                  MessagesController.getInstance(CommonGroupsActivity.this.currentAccount).putChats(localmessages_Chats.chats, false);
                  CommonGroupsActivity localCommonGroupsActivity = CommonGroupsActivity.this;
                  if ((localmessages_Chats.chats.isEmpty()) || (localmessages_Chats.chats.size() != CommonGroupsActivity.4.this.val$count))
                  {
                    bool = true;
                    CommonGroupsActivity.access$502(localCommonGroupsActivity, bool);
                    CommonGroupsActivity.this.chats.addAll(localmessages_Chats.chats);
                  }
                }
                for (;;)
                {
                  CommonGroupsActivity.access$602(CommonGroupsActivity.this, false);
                  CommonGroupsActivity.access$902(CommonGroupsActivity.this, true);
                  if (CommonGroupsActivity.this.emptyView != null) {
                    CommonGroupsActivity.this.emptyView.showTextView();
                  }
                  if (CommonGroupsActivity.this.listViewAdapter != null) {
                    CommonGroupsActivity.this.listViewAdapter.notifyDataSetChanged();
                  }
                  return;
                  bool = false;
                  break;
                  CommonGroupsActivity.access$502(CommonGroupsActivity.this, true);
                }
              }
            });
          }
        });
        ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(paramInt1, this.classGuid);
      }
    }
  }
  
  public View createView(Context paramContext)
  {
    int i = 1;
    this.actionBar.setBackButtonImage(NUM);
    this.actionBar.setAllowOverlayTitle(true);
    this.actionBar.setTitle(LocaleController.getString("GroupsInCommonTitle", NUM));
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (paramAnonymousInt == -1) {
          CommonGroupsActivity.this.finishFragment();
        }
      }
    });
    this.fragmentView = new FrameLayout(paramContext);
    this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
    FrameLayout localFrameLayout = (FrameLayout)this.fragmentView;
    this.emptyView = new EmptyTextProgressView(paramContext);
    this.emptyView.setText(LocaleController.getString("NoGroupsInCommon", NUM));
    localFrameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0F));
    this.listView = new RecyclerListView(paramContext);
    this.listView.setEmptyView(this.emptyView);
    RecyclerListView localRecyclerListView = this.listView;
    LinearLayoutManager localLinearLayoutManager = new LinearLayoutManager(paramContext, 1, false);
    this.layoutManager = localLinearLayoutManager;
    localRecyclerListView.setLayoutManager(localLinearLayoutManager);
    localRecyclerListView = this.listView;
    paramContext = new ListAdapter(paramContext);
    this.listViewAdapter = paramContext;
    localRecyclerListView.setAdapter(paramContext);
    paramContext = this.listView;
    if (LocaleController.isRTL)
    {
      paramContext.setVerticalScrollbarPosition(i);
      localFrameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0F));
      this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
      {
        public void onItemClick(View paramAnonymousView, int paramAnonymousInt)
        {
          if ((paramAnonymousInt < 0) || (paramAnonymousInt >= CommonGroupsActivity.this.chats.size())) {}
          for (;;)
          {
            return;
            paramAnonymousView = (TLRPC.Chat)CommonGroupsActivity.this.chats.get(paramAnonymousInt);
            Bundle localBundle = new Bundle();
            localBundle.putInt("chat_id", paramAnonymousView.id);
            if (MessagesController.getInstance(CommonGroupsActivity.this.currentAccount).checkCanOpenChat(localBundle, CommonGroupsActivity.this))
            {
              NotificationCenter.getInstance(CommonGroupsActivity.this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
              CommonGroupsActivity.this.presentFragment(new ChatActivity(localBundle), true);
            }
          }
        }
      });
      this.listView.setOnScrollListener(new RecyclerView.OnScrollListener()
      {
        public void onScrolled(RecyclerView paramAnonymousRecyclerView, int paramAnonymousInt1, int paramAnonymousInt2)
        {
          paramAnonymousInt2 = CommonGroupsActivity.this.layoutManager.findFirstVisibleItemPosition();
          if (paramAnonymousInt2 == -1) {}
          for (paramAnonymousInt1 = 0;; paramAnonymousInt1 = Math.abs(CommonGroupsActivity.this.layoutManager.findLastVisibleItemPosition() - paramAnonymousInt2) + 1)
          {
            if (paramAnonymousInt1 > 0)
            {
              int i = CommonGroupsActivity.this.listViewAdapter.getItemCount();
              if ((!CommonGroupsActivity.this.endReached) && (!CommonGroupsActivity.this.loading) && (!CommonGroupsActivity.this.chats.isEmpty()) && (paramAnonymousInt2 + paramAnonymousInt1 >= i - 5)) {
                CommonGroupsActivity.this.getChats(((TLRPC.Chat)CommonGroupsActivity.this.chats.get(CommonGroupsActivity.this.chats.size() - 1)).id, 100);
              }
            }
            return;
          }
        }
      });
      if (!this.loading) {
        break label285;
      }
      this.emptyView.showProgress();
    }
    for (;;)
    {
      return this.fragmentView;
      i = 2;
      break;
      label285:
      this.emptyView.showTextView();
    }
  }
  
  public ThemeDescription[] getThemeDescriptions()
  {
    ThemeDescription.ThemeDescriptionDelegate local5 = new ThemeDescription.ThemeDescriptionDelegate()
    {
      public void didSetColor()
      {
        if (CommonGroupsActivity.this.listView != null)
        {
          int i = CommonGroupsActivity.this.listView.getChildCount();
          for (int j = 0; j < i; j++)
          {
            View localView = CommonGroupsActivity.this.listView.getChildAt(j);
            if ((localView instanceof ProfileSearchCell)) {
              ((ProfileSearchCell)localView).update(0);
            }
          }
        }
      }
    };
    ThemeDescription localThemeDescription1 = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[] { LoadingCell.class, ProfileSearchCell.class }, null, null, null, "windowBackgroundWhite");
    ThemeDescription localThemeDescription2 = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray");
    ThemeDescription localThemeDescription3 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription4 = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription5 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
    ThemeDescription localThemeDescription6 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
    ThemeDescription localThemeDescription7 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
    ThemeDescription localThemeDescription8 = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
    Object localObject1 = this.listView;
    Object localObject2 = Theme.dividerPaint;
    localObject1 = new ThemeDescription((View)localObject1, 0, new Class[] { View.class }, (Paint)localObject2, null, null, "divider");
    ThemeDescription localThemeDescription9 = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "emptyListPlaceholder");
    ThemeDescription localThemeDescription10 = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, "progressCircle");
    ThemeDescription localThemeDescription11 = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { TextInfoPrivacyCell.class }, null, null, null, "windowBackgroundGrayShadow");
    ThemeDescription localThemeDescription12 = new ThemeDescription(this.listView, 0, new Class[] { TextInfoPrivacyCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteGrayText4");
    localObject2 = new ThemeDescription(this.listView, 0, new Class[] { LoadingCell.class }, new String[] { "progressBar" }, null, null, null, "progressCircle");
    RecyclerListView localRecyclerListView = this.listView;
    Object localObject3 = Theme.dialogs_namePaint;
    ThemeDescription localThemeDescription13 = new ThemeDescription(localRecyclerListView, 0, new Class[] { ProfileSearchCell.class }, (Paint)localObject3, null, null, "chats_name");
    localRecyclerListView = this.listView;
    localObject3 = Theme.avatar_photoDrawable;
    Drawable localDrawable1 = Theme.avatar_broadcastDrawable;
    Drawable localDrawable2 = Theme.avatar_savedDrawable;
    return new ThemeDescription[] { localThemeDescription1, localThemeDescription2, localThemeDescription3, localThemeDescription4, localThemeDescription5, localThemeDescription6, localThemeDescription7, localThemeDescription8, localObject1, localThemeDescription9, localThemeDescription10, localThemeDescription11, localThemeDescription12, localObject2, localThemeDescription13, new ThemeDescription(localRecyclerListView, 0, new Class[] { ProfileSearchCell.class }, null, new Drawable[] { localObject3, localDrawable1, localDrawable2 }, null, "avatar_text"), new ThemeDescription(null, 0, null, null, null, local5, "avatar_backgroundRed"), new ThemeDescription(null, 0, null, null, null, local5, "avatar_backgroundOrange"), new ThemeDescription(null, 0, null, null, null, local5, "avatar_backgroundViolet"), new ThemeDescription(null, 0, null, null, null, local5, "avatar_backgroundGreen"), new ThemeDescription(null, 0, null, null, null, local5, "avatar_backgroundCyan"), new ThemeDescription(null, 0, null, null, null, local5, "avatar_backgroundBlue"), new ThemeDescription(null, 0, null, null, null, local5, "avatar_backgroundPink") };
  }
  
  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    getChats(0, 50);
    return true;
  }
  
  public void onResume()
  {
    super.onResume();
    if (this.listViewAdapter != null) {
      this.listViewAdapter.notifyDataSetChanged();
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
      int i = CommonGroupsActivity.this.chats.size();
      int j = i;
      if (!CommonGroupsActivity.this.chats.isEmpty())
      {
        i++;
        j = i;
        if (!CommonGroupsActivity.this.endReached) {
          j = i + 1;
        }
      }
      return j;
    }
    
    public int getItemViewType(int paramInt)
    {
      if (paramInt < CommonGroupsActivity.this.chats.size()) {
        paramInt = 0;
      }
      for (;;)
      {
        return paramInt;
        if ((!CommonGroupsActivity.this.endReached) && (paramInt == CommonGroupsActivity.this.chats.size())) {
          paramInt = 1;
        } else {
          paramInt = 2;
        }
      }
    }
    
    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      if (paramViewHolder.getAdapterPosition() != CommonGroupsActivity.this.chats.size()) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      boolean bool = false;
      if (paramViewHolder.getItemViewType() == 0)
      {
        paramViewHolder = (ProfileSearchCell)paramViewHolder.itemView;
        paramViewHolder.setData((TLRPC.Chat)CommonGroupsActivity.this.chats.get(paramInt), null, null, null, false, false);
        if ((paramInt != CommonGroupsActivity.this.chats.size() - 1) || (!CommonGroupsActivity.this.endReached)) {
          bool = true;
        }
        paramViewHolder.useSeparator = bool;
      }
    }
    
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      switch (paramInt)
      {
      default: 
        paramViewGroup = new TextInfoPrivacyCell(this.mContext);
        paramViewGroup.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
      }
      for (;;)
      {
        return new RecyclerListView.Holder(paramViewGroup);
        paramViewGroup = new ProfileSearchCell(this.mContext);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        continue;
        paramViewGroup = new LoadingCell(this.mContext);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/CommonGroupsActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */