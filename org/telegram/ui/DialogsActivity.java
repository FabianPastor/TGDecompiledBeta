package org.telegram.ui;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Configuration;
import android.graphics.Outline;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.query.SearchQuery;
import org.telegram.messenger.query.StickersQuery;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.InputChannel;
import org.telegram.tgnet.TLRPC.TL_dialog;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet.Builder;
import org.telegram.ui.ActionBar.DrawerLayoutContainer;
import org.telegram.ui.ActionBar.MenuDrawable;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.DialogsAdapter;
import org.telegram.ui.Adapters.DialogsSearchAdapter;
import org.telegram.ui.Adapters.DialogsSearchAdapter.DialogsSearchAdapterDelegate;
import org.telegram.ui.Cells.DialogCell;
import org.telegram.ui.Cells.HintDialogCell;
import org.telegram.ui.Cells.ProfileSearchCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PlayerView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener;

public class DialogsActivity
  extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  public static boolean dialogsLoaded;
  private String addToGroupAlertString;
  private boolean cantSendToChannels;
  private boolean checkPermission = true;
  private DialogsActivityDelegate delegate;
  private DialogsAdapter dialogsAdapter;
  private DialogsSearchAdapter dialogsSearchAdapter;
  private int dialogsType;
  private LinearLayout emptyView;
  private ImageView floatingButton;
  private boolean floatingHidden;
  private final AccelerateDecelerateInterpolator floatingInterpolator = new AccelerateDecelerateInterpolator();
  private LinearLayoutManager layoutManager;
  private RecyclerListView listView;
  private boolean onlySelect;
  private long openedDialogId;
  private ActionBarMenuItem passcodeItem;
  private AlertDialog permissionDialog;
  private int prevPosition;
  private int prevTop;
  private ProgressBar progressView;
  private boolean scrollUpdated;
  private EmptyTextProgressView searchEmptyView;
  private String searchString;
  private boolean searchWas;
  private boolean searching;
  private String selectAlertString;
  private String selectAlertStringGroup;
  private long selectedDialog;
  
  public DialogsActivity(Bundle paramBundle)
  {
    super(paramBundle);
  }
  
  @TargetApi(23)
  private void askForPermissons()
  {
    Activity localActivity = getParentActivity();
    if (localActivity == null) {
      return;
    }
    ArrayList localArrayList = new ArrayList();
    if (localActivity.checkSelfPermission("android.permission.READ_CONTACTS") != 0)
    {
      localArrayList.add("android.permission.READ_CONTACTS");
      localArrayList.add("android.permission.WRITE_CONTACTS");
      localArrayList.add("android.permission.GET_ACCOUNTS");
    }
    if (localActivity.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != 0)
    {
      localArrayList.add("android.permission.READ_EXTERNAL_STORAGE");
      localArrayList.add("android.permission.WRITE_EXTERNAL_STORAGE");
    }
    localActivity.requestPermissions((String[])localArrayList.toArray(new String[localArrayList.size()]), 1);
  }
  
  private void didSelectResult(final long paramLong, boolean paramBoolean1, boolean paramBoolean2)
  {
    AlertDialog.Builder localBuilder;
    if ((this.addToGroupAlertString == null) && ((int)paramLong < 0) && (ChatObject.isChannel(-(int)paramLong)) && ((this.cantSendToChannels) || (!ChatObject.isCanWriteToChannel(-(int)paramLong))))
    {
      localBuilder = new AlertDialog.Builder(getParentActivity());
      localBuilder.setTitle(LocaleController.getString("AppName", 2131165299));
      localBuilder.setMessage(LocaleController.getString("ChannelCantSendMessage", 2131165419));
      localBuilder.setNegativeButton(LocaleController.getString("OK", 2131166044), null);
      showDialog(localBuilder.create());
    }
    int i;
    int j;
    Object localObject;
    do
    {
      do
      {
        return;
        if ((!paramBoolean1) || (((this.selectAlertString == null) || (this.selectAlertStringGroup == null)) && (this.addToGroupAlertString == null))) {
          break;
        }
      } while (getParentActivity() == null);
      localBuilder = new AlertDialog.Builder(getParentActivity());
      localBuilder.setTitle(LocaleController.getString("AppName", 2131165299));
      i = (int)paramLong;
      j = (int)(paramLong >> 32);
      if (i == 0) {
        break label421;
      }
      if (j != 1) {
        break;
      }
      localObject = MessagesController.getInstance().getChat(Integer.valueOf(i));
    } while (localObject == null);
    localBuilder.setMessage(LocaleController.formatStringSimple(this.selectAlertStringGroup, new Object[] { ((TLRPC.Chat)localObject).title }));
    for (;;)
    {
      localBuilder.setPositiveButton(LocaleController.getString("OK", 2131166044), new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          DialogsActivity.this.didSelectResult(paramLong, false, false);
        }
      });
      localBuilder.setNegativeButton(LocaleController.getString("Cancel", 2131165386), null);
      showDialog(localBuilder.create());
      return;
      if (i > 0)
      {
        localObject = MessagesController.getInstance().getUser(Integer.valueOf(i));
        if (localObject == null) {
          break;
        }
        localBuilder.setMessage(LocaleController.formatStringSimple(this.selectAlertString, new Object[] { UserObject.getUserName((TLRPC.User)localObject) }));
        continue;
      }
      if (i < 0)
      {
        localObject = MessagesController.getInstance().getChat(Integer.valueOf(-i));
        if (localObject == null) {
          break;
        }
        if (this.addToGroupAlertString != null)
        {
          localBuilder.setMessage(LocaleController.formatStringSimple(this.addToGroupAlertString, new Object[] { ((TLRPC.Chat)localObject).title }));
        }
        else
        {
          localBuilder.setMessage(LocaleController.formatStringSimple(this.selectAlertStringGroup, new Object[] { ((TLRPC.Chat)localObject).title }));
          continue;
          label421:
          localObject = MessagesController.getInstance().getEncryptedChat(Integer.valueOf(j));
          localObject = MessagesController.getInstance().getUser(Integer.valueOf(((TLRPC.EncryptedChat)localObject).user_id));
          if (localObject == null) {
            break;
          }
          localBuilder.setMessage(LocaleController.formatStringSimple(this.selectAlertString, new Object[] { UserObject.getUserName((TLRPC.User)localObject) }));
        }
      }
    }
    if (this.delegate != null)
    {
      this.delegate.didSelectDialog(this, paramLong, paramBoolean2);
      this.delegate = null;
      return;
    }
    finishFragment();
  }
  
  private ArrayList<TLRPC.TL_dialog> getDialogsArray()
  {
    if (this.dialogsType == 0) {
      return MessagesController.getInstance().dialogs;
    }
    if (this.dialogsType == 1) {
      return MessagesController.getInstance().dialogsServerOnly;
    }
    if (this.dialogsType == 2) {
      return MessagesController.getInstance().dialogsGroupsOnly;
    }
    return null;
  }
  
  private void hideFloatingButton(boolean paramBoolean)
  {
    if (this.floatingHidden == paramBoolean) {
      return;
    }
    this.floatingHidden = paramBoolean;
    Object localObject = this.floatingButton;
    float f;
    ImageView localImageView;
    if (this.floatingHidden)
    {
      f = AndroidUtilities.dp(100.0F);
      localObject = ObjectAnimator.ofFloat(localObject, "translationY", new float[] { f }).setDuration(300L);
      ((ObjectAnimator)localObject).setInterpolator(this.floatingInterpolator);
      localImageView = this.floatingButton;
      if (paramBoolean) {
        break label91;
      }
    }
    label91:
    for (paramBoolean = true;; paramBoolean = false)
    {
      localImageView.setClickable(paramBoolean);
      ((ObjectAnimator)localObject).start();
      return;
      f = 0.0F;
      break;
    }
  }
  
  private void updatePasscodeButton()
  {
    if (this.passcodeItem == null) {
      return;
    }
    if ((UserConfig.passcodeHash.length() != 0) && (!this.searching))
    {
      this.passcodeItem.setVisibility(0);
      if (UserConfig.appLocked)
      {
        this.passcodeItem.setIcon(2130837807);
        return;
      }
      this.passcodeItem.setIcon(2130837808);
      return;
    }
    this.passcodeItem.setVisibility(8);
  }
  
  private void updateVisibleRows(int paramInt)
  {
    if (this.listView == null) {
      return;
    }
    int k = this.listView.getChildCount();
    int i = 0;
    label19:
    Object localObject;
    boolean bool;
    if (i < k)
    {
      localObject = this.listView.getChildAt(i);
      if (!(localObject instanceof DialogCell)) {
        break label188;
      }
      if (this.listView.getAdapter() != this.dialogsSearchAdapter)
      {
        localObject = (DialogCell)localObject;
        if ((paramInt & 0x800) == 0) {
          break label126;
        }
        ((DialogCell)localObject).checkCurrentDialogIndex();
        if ((this.dialogsType == 0) && (AndroidUtilities.isTablet()))
        {
          if (((DialogCell)localObject).getDialogId() != this.openedDialogId) {
            break label120;
          }
          bool = true;
          label106:
          ((DialogCell)localObject).setDialogSelected(bool);
        }
      }
    }
    for (;;)
    {
      i += 1;
      break label19;
      break;
      label120:
      bool = false;
      break label106;
      label126:
      if ((paramInt & 0x200) != 0)
      {
        if ((this.dialogsType == 0) && (AndroidUtilities.isTablet()))
        {
          if (((DialogCell)localObject).getDialogId() == this.openedDialogId) {}
          for (bool = true;; bool = false)
          {
            ((DialogCell)localObject).setDialogSelected(bool);
            break;
          }
        }
      }
      else
      {
        ((DialogCell)localObject).update(paramInt);
        continue;
        label188:
        if ((localObject instanceof UserCell))
        {
          ((UserCell)localObject).update(paramInt);
        }
        else if ((localObject instanceof ProfileSearchCell))
        {
          ((ProfileSearchCell)localObject).update(paramInt);
        }
        else if ((localObject instanceof RecyclerListView))
        {
          localObject = (RecyclerListView)localObject;
          int m = ((RecyclerListView)localObject).getChildCount();
          int j = 0;
          while (j < m)
          {
            View localView = ((RecyclerListView)localObject).getChildAt(j);
            if ((localView instanceof HintDialogCell)) {
              ((HintDialogCell)localView).checkUnreadCounter(paramInt);
            }
            j += 1;
          }
        }
      }
    }
  }
  
  public View createView(final Context paramContext)
  {
    this.searching = false;
    this.searchWas = false;
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        Theme.loadRecources(paramContext);
      }
    });
    Object localObject1 = this.actionBar.createMenu();
    if ((!this.onlySelect) && (this.searchString == null))
    {
      this.passcodeItem = ((ActionBarMenu)localObject1).addItem(1, 2130837807);
      updatePasscodeButton();
    }
    ((ActionBarMenu)localObject1).addItem(0, 2130837711).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener()
    {
      public boolean canCollapseSearch()
      {
        if (DialogsActivity.this.searchString != null)
        {
          DialogsActivity.this.finishFragment();
          return false;
        }
        return true;
      }
      
      public void onSearchCollapse()
      {
        DialogsActivity.access$002(DialogsActivity.this, false);
        DialogsActivity.access$902(DialogsActivity.this, false);
        if (DialogsActivity.this.listView != null)
        {
          DialogsActivity.this.searchEmptyView.setVisibility(8);
          if ((!MessagesController.getInstance().loadingDialogs) || (!MessagesController.getInstance().dialogs.isEmpty())) {
            break label220;
          }
          DialogsActivity.this.emptyView.setVisibility(8);
          DialogsActivity.this.listView.setEmptyView(DialogsActivity.this.progressView);
        }
        for (;;)
        {
          if (!DialogsActivity.this.onlySelect)
          {
            DialogsActivity.this.floatingButton.setVisibility(0);
            DialogsActivity.access$1002(DialogsActivity.this, true);
            DialogsActivity.this.floatingButton.setTranslationY(AndroidUtilities.dp(100.0F));
            DialogsActivity.this.hideFloatingButton(false);
          }
          if (DialogsActivity.this.listView.getAdapter() != DialogsActivity.this.dialogsAdapter)
          {
            DialogsActivity.this.listView.setAdapter(DialogsActivity.this.dialogsAdapter);
            DialogsActivity.this.dialogsAdapter.notifyDataSetChanged();
          }
          if (DialogsActivity.this.dialogsSearchAdapter != null) {
            DialogsActivity.this.dialogsSearchAdapter.searchDialogs(null);
          }
          DialogsActivity.this.updatePasscodeButton();
          return;
          label220:
          DialogsActivity.this.progressView.setVisibility(8);
          DialogsActivity.this.listView.setEmptyView(DialogsActivity.this.emptyView);
        }
      }
      
      public void onSearchExpand()
      {
        DialogsActivity.access$002(DialogsActivity.this, true);
        if (DialogsActivity.this.listView != null)
        {
          if (DialogsActivity.this.searchString != null)
          {
            DialogsActivity.this.listView.setEmptyView(DialogsActivity.this.searchEmptyView);
            DialogsActivity.this.progressView.setVisibility(8);
            DialogsActivity.this.emptyView.setVisibility(8);
          }
          if (!DialogsActivity.this.onlySelect) {
            DialogsActivity.this.floatingButton.setVisibility(8);
          }
        }
        DialogsActivity.this.updatePasscodeButton();
      }
      
      public void onTextChanged(EditText paramAnonymousEditText)
      {
        paramAnonymousEditText = paramAnonymousEditText.getText().toString();
        if ((paramAnonymousEditText.length() != 0) || ((DialogsActivity.this.dialogsSearchAdapter != null) && (DialogsActivity.this.dialogsSearchAdapter.hasRecentRearch())))
        {
          DialogsActivity.access$902(DialogsActivity.this, true);
          if ((DialogsActivity.this.dialogsSearchAdapter != null) && (DialogsActivity.this.listView.getAdapter() != DialogsActivity.this.dialogsSearchAdapter))
          {
            DialogsActivity.this.listView.setAdapter(DialogsActivity.this.dialogsSearchAdapter);
            DialogsActivity.this.dialogsSearchAdapter.notifyDataSetChanged();
          }
          if ((DialogsActivity.this.searchEmptyView != null) && (DialogsActivity.this.listView.getEmptyView() != DialogsActivity.this.searchEmptyView))
          {
            DialogsActivity.this.emptyView.setVisibility(8);
            DialogsActivity.this.progressView.setVisibility(8);
            DialogsActivity.this.searchEmptyView.showTextView();
            DialogsActivity.this.listView.setEmptyView(DialogsActivity.this.searchEmptyView);
          }
        }
        if (DialogsActivity.this.dialogsSearchAdapter != null) {
          DialogsActivity.this.dialogsSearchAdapter.searchDialogs(paramAnonymousEditText);
        }
      }
    }).getSearchField().setHint(LocaleController.getString("Search", 2131166206));
    FrameLayout localFrameLayout;
    int i;
    label275:
    label724:
    label921:
    float f1;
    label931:
    float f2;
    if (this.onlySelect)
    {
      this.actionBar.setBackButtonImage(2130837700);
      this.actionBar.setTitle(LocaleController.getString("SelectChat", 2131166230));
      this.actionBar.setAllowOverlayTitle(true);
      this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
      {
        public void onItemClick(int paramAnonymousInt)
        {
          boolean bool = true;
          if (paramAnonymousInt == -1) {
            if (DialogsActivity.this.onlySelect) {
              DialogsActivity.this.finishFragment();
            }
          }
          while (paramAnonymousInt != 1)
          {
            do
            {
              return;
            } while (DialogsActivity.this.parentLayout == null);
            DialogsActivity.this.parentLayout.getDrawerLayoutContainer().openDrawer(false);
            return;
          }
          if (!UserConfig.appLocked) {}
          for (;;)
          {
            UserConfig.appLocked = bool;
            UserConfig.saveConfig(false);
            DialogsActivity.this.updatePasscodeButton();
            return;
            bool = false;
          }
        }
      });
      localFrameLayout = new FrameLayout(paramContext);
      this.fragmentView = localFrameLayout;
      this.listView = new RecyclerListView(paramContext);
      this.listView.setVerticalScrollBarEnabled(true);
      this.listView.setItemAnimator(null);
      this.listView.setInstantClick(true);
      this.listView.setLayoutAnimation(null);
      this.listView.setTag(Integer.valueOf(4));
      this.layoutManager = new LinearLayoutManager(paramContext)
      {
        public boolean supportsPredictiveItemAnimations()
        {
          return false;
        }
      };
      this.layoutManager.setOrientation(1);
      this.listView.setLayoutManager(this.layoutManager);
      localObject1 = this.listView;
      if (!LocaleController.isRTL) {
        break label1295;
      }
      i = 1;
      ((RecyclerListView)localObject1).setVerticalScrollbarPosition(i);
      localFrameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0F));
      this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
      {
        public void onItemClick(View paramAnonymousView, int paramAnonymousInt)
        {
          if ((DialogsActivity.this.listView == null) || (DialogsActivity.this.listView.getAdapter() == null)) {}
          Object localObject1;
          label559:
          label599:
          label837:
          label860:
          do
          {
            long l1;
            int i;
            Object localObject2;
            for (;;)
            {
              return;
              long l2 = 0L;
              j = 0;
              paramAnonymousView = DialogsActivity.this.listView.getAdapter();
              if (paramAnonymousView == DialogsActivity.this.dialogsAdapter)
              {
                localObject1 = DialogsActivity.this.dialogsAdapter.getItem(paramAnonymousInt);
                if (localObject1 != null)
                {
                  l1 = ((TLRPC.TL_dialog)localObject1).id;
                  i = j;
                }
              }
              else
              {
                while (l1 != 0L)
                {
                  if (!DialogsActivity.this.onlySelect) {
                    break label559;
                  }
                  DialogsActivity.this.didSelectResult(l1, true, false);
                  return;
                  l1 = l2;
                  i = j;
                  if (paramAnonymousView == DialogsActivity.this.dialogsSearchAdapter)
                  {
                    localObject1 = DialogsActivity.this.dialogsSearchAdapter.getItem(paramAnonymousInt);
                    if ((localObject1 instanceof TLRPC.User))
                    {
                      l2 = ((TLRPC.User)localObject1).id;
                      if (DialogsActivity.this.dialogsSearchAdapter.isGlobalSearch(paramAnonymousInt))
                      {
                        localObject2 = new ArrayList();
                        ((ArrayList)localObject2).add((TLRPC.User)localObject1);
                        MessagesController.getInstance().putUsers((ArrayList)localObject2, false);
                        MessagesStorage.getInstance().putUsersAndChats((ArrayList)localObject2, null, false, true);
                      }
                      l1 = l2;
                      i = j;
                      if (!DialogsActivity.this.onlySelect)
                      {
                        DialogsActivity.this.dialogsSearchAdapter.putRecentSearch(l2, (TLRPC.User)localObject1);
                        l1 = l2;
                        i = j;
                      }
                    }
                    else
                    {
                      if ((localObject1 instanceof TLRPC.Chat))
                      {
                        if (DialogsActivity.this.dialogsSearchAdapter.isGlobalSearch(paramAnonymousInt))
                        {
                          localObject2 = new ArrayList();
                          ((ArrayList)localObject2).add((TLRPC.Chat)localObject1);
                          MessagesController.getInstance().putChats((ArrayList)localObject2, false);
                          MessagesStorage.getInstance().putUsersAndChats(null, (ArrayList)localObject2, false, true);
                        }
                        if (((TLRPC.Chat)localObject1).id > 0) {}
                        for (l2 = -((TLRPC.Chat)localObject1).id;; l2 = AndroidUtilities.makeBroadcastId(((TLRPC.Chat)localObject1).id))
                        {
                          l1 = l2;
                          i = j;
                          if (DialogsActivity.this.onlySelect) {
                            break;
                          }
                          DialogsActivity.this.dialogsSearchAdapter.putRecentSearch(l2, (TLRPC.Chat)localObject1);
                          l1 = l2;
                          i = j;
                          break;
                        }
                      }
                      if ((localObject1 instanceof TLRPC.EncryptedChat))
                      {
                        l2 = ((TLRPC.EncryptedChat)localObject1).id << 32;
                        l1 = l2;
                        i = j;
                        if (!DialogsActivity.this.onlySelect)
                        {
                          DialogsActivity.this.dialogsSearchAdapter.putRecentSearch(l2, (TLRPC.EncryptedChat)localObject1);
                          l1 = l2;
                          i = j;
                        }
                      }
                      else if ((localObject1 instanceof MessageObject))
                      {
                        localObject1 = (MessageObject)localObject1;
                        l1 = ((MessageObject)localObject1).getDialogId();
                        i = ((MessageObject)localObject1).getId();
                        DialogsActivity.this.dialogsSearchAdapter.addHashtagsFromMessage(DialogsActivity.this.dialogsSearchAdapter.getLastSearchString());
                      }
                      else
                      {
                        l1 = l2;
                        i = j;
                        if ((localObject1 instanceof String))
                        {
                          DialogsActivity.this.actionBar.openSearchField((String)localObject1);
                          l1 = l2;
                          i = j;
                        }
                      }
                    }
                  }
                }
              }
            }
            localObject1 = new Bundle();
            int j = (int)l1;
            paramAnonymousInt = (int)(l1 >> 32);
            if (j != 0) {
              if (paramAnonymousInt == 1)
              {
                ((Bundle)localObject1).putInt("chat_id", j);
                if (i == 0) {
                  break label837;
                }
                ((Bundle)localObject1).putInt("message_id", i);
              }
            }
            for (;;)
            {
              if (AndroidUtilities.isTablet())
              {
                if ((DialogsActivity.this.openedDialogId == l1) && (paramAnonymousView != DialogsActivity.this.dialogsSearchAdapter)) {
                  break;
                }
                if (DialogsActivity.this.dialogsAdapter != null)
                {
                  DialogsActivity.this.dialogsAdapter.setOpenedDialogId(DialogsActivity.access$2002(DialogsActivity.this, l1));
                  DialogsActivity.this.updateVisibleRows(512);
                }
              }
              if (DialogsActivity.this.searchString == null) {
                break label860;
              }
              if (!MessagesController.checkCanOpenChat((Bundle)localObject1, DialogsActivity.this)) {
                break;
              }
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
              DialogsActivity.this.presentFragment(new ChatActivity((Bundle)localObject1));
              return;
              if (j > 0)
              {
                ((Bundle)localObject1).putInt("user_id", j);
                break label599;
              }
              if (j >= 0) {
                break label599;
              }
              paramAnonymousInt = j;
              if (i != 0)
              {
                localObject2 = MessagesController.getInstance().getChat(Integer.valueOf(-j));
                paramAnonymousInt = j;
                if (localObject2 != null)
                {
                  paramAnonymousInt = j;
                  if (((TLRPC.Chat)localObject2).migrated_to != null)
                  {
                    ((Bundle)localObject1).putInt("migrated_to", j);
                    paramAnonymousInt = -((TLRPC.Chat)localObject2).migrated_to.channel_id;
                  }
                }
              }
              ((Bundle)localObject1).putInt("chat_id", -paramAnonymousInt);
              break label599;
              ((Bundle)localObject1).putInt("enc_id", paramAnonymousInt);
              break label599;
              if (DialogsActivity.this.actionBar != null) {
                DialogsActivity.this.actionBar.closeSearchField();
              }
            }
          } while (!MessagesController.checkCanOpenChat((Bundle)localObject1, DialogsActivity.this));
          DialogsActivity.this.presentFragment(new ChatActivity((Bundle)localObject1));
        }
      });
      this.listView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener()
      {
        public boolean onItemClick(View paramAnonymousView, int paramAnonymousInt)
        {
          if ((DialogsActivity.this.onlySelect) || ((DialogsActivity.this.searching) && (DialogsActivity.this.searchWas)) || (DialogsActivity.this.getParentActivity() == null))
          {
            if (((DialogsActivity.this.searchWas) && (DialogsActivity.this.searching)) || ((DialogsActivity.this.dialogsSearchAdapter.isRecentSearchDisplayed()) && (DialogsActivity.this.listView.getAdapter() == DialogsActivity.this.dialogsSearchAdapter) && (((DialogsActivity.this.dialogsSearchAdapter.getItem(paramAnonymousInt) instanceof String)) || (DialogsActivity.this.dialogsSearchAdapter.isRecentSearchDisplayed()))))
            {
              paramAnonymousView = new AlertDialog.Builder(DialogsActivity.this.getParentActivity());
              paramAnonymousView.setTitle(LocaleController.getString("AppName", 2131165299));
              paramAnonymousView.setMessage(LocaleController.getString("ClearSearch", 2131165514));
              paramAnonymousView.setPositiveButton(LocaleController.getString("ClearButton", 2131165508).toUpperCase(), new DialogInterface.OnClickListener()
              {
                public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
                {
                  if (DialogsActivity.this.dialogsSearchAdapter.isRecentSearchDisplayed())
                  {
                    DialogsActivity.this.dialogsSearchAdapter.clearRecentSearch();
                    return;
                  }
                  DialogsActivity.this.dialogsSearchAdapter.clearRecentHashtags();
                }
              });
              paramAnonymousView.setNegativeButton(LocaleController.getString("Cancel", 2131165386), null);
              DialogsActivity.this.showDialog(paramAnonymousView.create());
              return true;
            }
            return false;
          }
          paramAnonymousView = DialogsActivity.this.getDialogsArray();
          if ((paramAnonymousInt < 0) || (paramAnonymousInt >= paramAnonymousView.size())) {
            return false;
          }
          paramAnonymousView = (TLRPC.TL_dialog)paramAnonymousView.get(paramAnonymousInt);
          DialogsActivity.access$2302(DialogsActivity.this, paramAnonymousView.id);
          BottomSheet.Builder localBuilder = new BottomSheet.Builder(DialogsActivity.this.getParentActivity());
          paramAnonymousInt = (int)DialogsActivity.this.selectedDialog;
          int i = (int)(DialogsActivity.this.selectedDialog >> 32);
          final Object localObject2;
          Object localObject1;
          if (DialogObject.isChannel(paramAnonymousView))
          {
            localObject2 = MessagesController.getInstance().getChat(Integer.valueOf(-paramAnonymousInt));
            if ((localObject2 != null) && (((TLRPC.Chat)localObject2).megagroup))
            {
              localObject1 = new CharSequence[2];
              localObject1[0] = LocaleController.getString("ClearHistoryCache", 2131165510);
              if ((localObject2 == null) || (!((TLRPC.Chat)localObject2).creator)) {}
              for (paramAnonymousView = LocaleController.getString("LeaveMegaMenu", 2131165818);; paramAnonymousView = LocaleController.getString("DeleteMegaMenu", 2131165575))
              {
                localObject1[1] = paramAnonymousView;
                paramAnonymousView = (View)localObject1;
                localBuilder.setItems(paramAnonymousView, new DialogInterface.OnClickListener()
                {
                  public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
                  {
                    paramAnonymous2DialogInterface = new AlertDialog.Builder(DialogsActivity.this.getParentActivity());
                    paramAnonymous2DialogInterface.setTitle(LocaleController.getString("AppName", 2131165299));
                    if (paramAnonymous2Int == 0)
                    {
                      if ((localObject2 != null) && (localObject2.megagroup)) {
                        paramAnonymous2DialogInterface.setMessage(LocaleController.getString("AreYouSureClearHistorySuper", 2131165317));
                      }
                      for (;;)
                      {
                        paramAnonymous2DialogInterface.setPositiveButton(LocaleController.getString("OK", 2131166044), new DialogInterface.OnClickListener()
                        {
                          public void onClick(DialogInterface paramAnonymous3DialogInterface, int paramAnonymous3Int)
                          {
                            MessagesController.getInstance().deleteDialog(DialogsActivity.this.selectedDialog, 2);
                          }
                        });
                        paramAnonymous2DialogInterface.setNegativeButton(LocaleController.getString("Cancel", 2131165386), null);
                        DialogsActivity.this.showDialog(paramAnonymous2DialogInterface.create());
                        return;
                        paramAnonymous2DialogInterface.setMessage(LocaleController.getString("AreYouSureClearHistoryChannel", 2131165316));
                      }
                    }
                    if ((localObject2 != null) && (localObject2.megagroup)) {
                      if (!localObject2.creator) {
                        paramAnonymous2DialogInterface.setMessage(LocaleController.getString("MegaLeaveAlert", 2131165856));
                      }
                    }
                    for (;;)
                    {
                      paramAnonymous2DialogInterface.setPositiveButton(LocaleController.getString("OK", 2131166044), new DialogInterface.OnClickListener()
                      {
                        public void onClick(DialogInterface paramAnonymous3DialogInterface, int paramAnonymous3Int)
                        {
                          MessagesController.getInstance().deleteUserFromChat((int)-DialogsActivity.this.selectedDialog, UserConfig.getCurrentUser(), null);
                          if (AndroidUtilities.isTablet()) {
                            NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[] { Long.valueOf(DialogsActivity.this.selectedDialog) });
                          }
                        }
                      });
                      break;
                      paramAnonymous2DialogInterface.setMessage(LocaleController.getString("MegaDeleteAlert", 2131165854));
                      continue;
                      if ((localObject2 == null) || (!localObject2.creator)) {
                        paramAnonymous2DialogInterface.setMessage(LocaleController.getString("ChannelLeaveAlert", 2131165432));
                      } else {
                        paramAnonymous2DialogInterface.setMessage(LocaleController.getString("ChannelDeleteAlert", 2131165422));
                      }
                    }
                  }
                });
                DialogsActivity.this.showDialog(localBuilder.create());
                return true;
              }
            }
            localObject1 = new CharSequence[2];
            localObject1[0] = LocaleController.getString("ClearHistoryCache", 2131165510);
            if ((localObject2 == null) || (!((TLRPC.Chat)localObject2).creator)) {}
            for (paramAnonymousView = LocaleController.getString("LeaveChannelMenu", 2131165816);; paramAnonymousView = LocaleController.getString("ChannelDeleteMenu", 2131165424))
            {
              localObject1[1] = paramAnonymousView;
              paramAnonymousView = (View)localObject1;
              break;
            }
          }
          final boolean bool1;
          label488:
          final boolean bool2;
          if ((paramAnonymousInt < 0) && (i != 1))
          {
            bool1 = true;
            localObject1 = null;
            paramAnonymousView = (View)localObject1;
            if (!bool1)
            {
              paramAnonymousView = (View)localObject1;
              if (paramAnonymousInt > 0)
              {
                paramAnonymousView = (View)localObject1;
                if (i != 1) {
                  paramAnonymousView = MessagesController.getInstance().getUser(Integer.valueOf(paramAnonymousInt));
                }
              }
            }
            if ((paramAnonymousView == null) || (!paramAnonymousView.bot)) {
              break label618;
            }
            bool2 = true;
            label539:
            localObject1 = LocaleController.getString("ClearHistory", 2131165509);
            if (!bool1) {
              break label624;
            }
            paramAnonymousView = LocaleController.getString("DeleteChat", 2131165569);
          }
          for (;;)
          {
            localObject2 = new DialogInterface.OnClickListener()
            {
              public void onClick(DialogInterface paramAnonymous2DialogInterface, final int paramAnonymous2Int)
              {
                paramAnonymous2DialogInterface = new AlertDialog.Builder(DialogsActivity.this.getParentActivity());
                paramAnonymous2DialogInterface.setTitle(LocaleController.getString("AppName", 2131165299));
                if (paramAnonymous2Int == 0) {
                  paramAnonymous2DialogInterface.setMessage(LocaleController.getString("AreYouSureClearHistory", 2131165315));
                }
                for (;;)
                {
                  paramAnonymous2DialogInterface.setPositiveButton(LocaleController.getString("OK", 2131166044), new DialogInterface.OnClickListener()
                  {
                    public void onClick(DialogInterface paramAnonymous3DialogInterface, int paramAnonymous3Int)
                    {
                      if (paramAnonymous2Int != 0)
                      {
                        if (DialogsActivity.6.3.this.val$isChat)
                        {
                          paramAnonymous3DialogInterface = MessagesController.getInstance().getChat(Integer.valueOf((int)-DialogsActivity.this.selectedDialog));
                          if ((paramAnonymous3DialogInterface != null) && (ChatObject.isNotInChat(paramAnonymous3DialogInterface))) {
                            MessagesController.getInstance().deleteDialog(DialogsActivity.this.selectedDialog, 0);
                          }
                        }
                        for (;;)
                        {
                          if (DialogsActivity.6.3.this.val$isBot) {
                            MessagesController.getInstance().blockUser((int)DialogsActivity.this.selectedDialog);
                          }
                          if (AndroidUtilities.isTablet()) {
                            NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[] { Long.valueOf(DialogsActivity.this.selectedDialog) });
                          }
                          return;
                          MessagesController.getInstance().deleteUserFromChat((int)-DialogsActivity.this.selectedDialog, MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId())), null);
                          continue;
                          MessagesController.getInstance().deleteDialog(DialogsActivity.this.selectedDialog, 0);
                        }
                      }
                      MessagesController.getInstance().deleteDialog(DialogsActivity.this.selectedDialog, 1);
                    }
                  });
                  paramAnonymous2DialogInterface.setNegativeButton(LocaleController.getString("Cancel", 2131165386), null);
                  DialogsActivity.this.showDialog(paramAnonymous2DialogInterface.create());
                  return;
                  if (bool1) {
                    paramAnonymous2DialogInterface.setMessage(LocaleController.getString("AreYouSureDeleteAndExit", 2131165318));
                  } else {
                    paramAnonymous2DialogInterface.setMessage(LocaleController.getString("AreYouSureDeleteThisChat", 2131165322));
                  }
                }
              }
            };
            localBuilder.setItems(new CharSequence[] { localObject1, paramAnonymousView }, (DialogInterface.OnClickListener)localObject2);
            DialogsActivity.this.showDialog(localBuilder.create());
            break;
            bool1 = false;
            break label488;
            label618:
            bool2 = false;
            break label539;
            label624:
            if (bool2) {
              paramAnonymousView = LocaleController.getString("DeleteAndStop", 2131165567);
            } else {
              paramAnonymousView = LocaleController.getString("Delete", 2131165560);
            }
          }
        }
      });
      this.searchEmptyView = new EmptyTextProgressView(paramContext);
      this.searchEmptyView.setVisibility(8);
      this.searchEmptyView.setShowAtCenter(true);
      this.searchEmptyView.setText(LocaleController.getString("NoResult", 2131165949));
      localFrameLayout.addView(this.searchEmptyView, LayoutHelper.createFrame(-1, -1.0F));
      this.emptyView = new LinearLayout(paramContext);
      this.emptyView.setOrientation(1);
      this.emptyView.setVisibility(8);
      this.emptyView.setGravity(17);
      localFrameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0F));
      this.emptyView.setOnTouchListener(new View.OnTouchListener()
      {
        public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
        {
          return true;
        }
      });
      localObject1 = new TextView(paramContext);
      ((TextView)localObject1).setText(LocaleController.getString("NoChats", 2131165930));
      ((TextView)localObject1).setTextColor(-6974059);
      ((TextView)localObject1).setGravity(17);
      ((TextView)localObject1).setTextSize(1, 20.0F);
      this.emptyView.addView((View)localObject1, LayoutHelper.createLinear(-2, -2));
      TextView localTextView = new TextView(paramContext);
      Object localObject2 = LocaleController.getString("NoChatsHelp", 2131165931);
      localObject1 = localObject2;
      if (AndroidUtilities.isTablet())
      {
        localObject1 = localObject2;
        if (!AndroidUtilities.isSmallTablet()) {
          localObject1 = ((String)localObject2).replace('\n', ' ');
        }
      }
      localTextView.setText((CharSequence)localObject1);
      localTextView.setTextColor(-6974059);
      localTextView.setTextSize(1, 15.0F);
      localTextView.setGravity(17);
      localTextView.setPadding(AndroidUtilities.dp(8.0F), AndroidUtilities.dp(6.0F), AndroidUtilities.dp(8.0F), 0);
      localTextView.setLineSpacing(AndroidUtilities.dp(2.0F), 1.0F);
      this.emptyView.addView(localTextView, LayoutHelper.createLinear(-2, -2));
      this.progressView = new ProgressBar(paramContext);
      this.progressView.setVisibility(8);
      localFrameLayout.addView(this.progressView, LayoutHelper.createFrame(-2, -2, 17));
      this.floatingButton = new ImageView(paramContext);
      localObject1 = this.floatingButton;
      if (!this.onlySelect) {
        break label1301;
      }
      i = 8;
      ((ImageView)localObject1).setVisibility(i);
      this.floatingButton.setScaleType(ImageView.ScaleType.CENTER);
      this.floatingButton.setBackgroundResource(2130837684);
      this.floatingButton.setImageResource(2130837682);
      if (Build.VERSION.SDK_INT >= 21)
      {
        localObject1 = new StateListAnimator();
        localObject2 = ObjectAnimator.ofFloat(this.floatingButton, "translationZ", new float[] { AndroidUtilities.dp(2.0F), AndroidUtilities.dp(4.0F) }).setDuration(200L);
        ((StateListAnimator)localObject1).addState(new int[] { 16842919 }, (Animator)localObject2);
        localObject2 = ObjectAnimator.ofFloat(this.floatingButton, "translationZ", new float[] { AndroidUtilities.dp(4.0F), AndroidUtilities.dp(2.0F) }).setDuration(200L);
        ((StateListAnimator)localObject1).addState(new int[0], (Animator)localObject2);
        this.floatingButton.setStateListAnimator((StateListAnimator)localObject1);
        this.floatingButton.setOutlineProvider(new ViewOutlineProvider()
        {
          @SuppressLint({"NewApi"})
          public void getOutline(View paramAnonymousView, Outline paramAnonymousOutline)
          {
            paramAnonymousOutline.setOval(0, 0, AndroidUtilities.dp(56.0F), AndroidUtilities.dp(56.0F));
          }
        });
      }
      localObject1 = this.floatingButton;
      if (!LocaleController.isRTL) {
        break label1307;
      }
      i = 3;
      if (!LocaleController.isRTL) {
        break label1313;
      }
      f1 = 14.0F;
      if (!LocaleController.isRTL) {
        break label1318;
      }
      f2 = 0.0F;
      label939:
      localFrameLayout.addView((View)localObject1, LayoutHelper.createFrame(-2, -2.0F, i | 0x50, f1, 0.0F, f2, 14.0F));
      this.floatingButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          paramAnonymousView = new Bundle();
          paramAnonymousView.putBoolean("destroyAfterSelect", true);
          DialogsActivity.this.presentFragment(new ContactsActivity(paramAnonymousView));
        }
      });
      this.listView.setOnScrollListener(new RecyclerView.OnScrollListener()
      {
        public void onScrollStateChanged(RecyclerView paramAnonymousRecyclerView, int paramAnonymousInt)
        {
          if ((paramAnonymousInt == 1) && (DialogsActivity.this.searching) && (DialogsActivity.this.searchWas)) {
            AndroidUtilities.hideKeyboard(DialogsActivity.this.getParentActivity().getCurrentFocus());
          }
        }
        
        public void onScrolled(RecyclerView paramAnonymousRecyclerView, int paramAnonymousInt1, int paramAnonymousInt2)
        {
          int i = DialogsActivity.this.layoutManager.findFirstVisibleItemPosition();
          paramAnonymousInt1 = Math.abs(DialogsActivity.this.layoutManager.findLastVisibleItemPosition() - i) + 1;
          paramAnonymousInt2 = paramAnonymousRecyclerView.getAdapter().getItemCount();
          if ((DialogsActivity.this.searching) && (DialogsActivity.this.searchWas)) {
            if ((paramAnonymousInt1 > 0) && (DialogsActivity.this.layoutManager.findLastVisibleItemPosition() == paramAnonymousInt2 - 1) && (!DialogsActivity.this.dialogsSearchAdapter.isMessagesSearchEndReached())) {
              DialogsActivity.this.dialogsSearchAdapter.loadMoreSearchMessages();
            }
          }
          do
          {
            return;
            if ((paramAnonymousInt1 > 0) && (DialogsActivity.this.layoutManager.findLastVisibleItemPosition() >= DialogsActivity.this.getDialogsArray().size() - 10))
            {
              MessagesController localMessagesController = MessagesController.getInstance();
              if (MessagesController.getInstance().dialogsEndReached) {
                break;
              }
              bool = true;
              localMessagesController.loadDialogs(-1, 100, bool);
            }
          } while (DialogsActivity.this.floatingButton.getVisibility() == 8);
          paramAnonymousRecyclerView = paramAnonymousRecyclerView.getChildAt(0);
          paramAnonymousInt2 = 0;
          if (paramAnonymousRecyclerView != null) {
            paramAnonymousInt2 = paramAnonymousRecyclerView.getTop();
          }
          paramAnonymousInt1 = 1;
          if (DialogsActivity.this.prevPosition == i)
          {
            paramAnonymousInt1 = DialogsActivity.this.prevTop;
            if (paramAnonymousInt2 < DialogsActivity.this.prevTop)
            {
              bool = true;
              label228:
              if (Math.abs(paramAnonymousInt1 - paramAnonymousInt2) <= 1) {
                break label304;
              }
            }
            label304:
            for (paramAnonymousInt1 = 1;; paramAnonymousInt1 = 0)
            {
              if ((paramAnonymousInt1 != 0) && (DialogsActivity.this.scrollUpdated)) {
                DialogsActivity.this.hideFloatingButton(bool);
              }
              DialogsActivity.access$2502(DialogsActivity.this, i);
              DialogsActivity.access$2602(DialogsActivity.this, paramAnonymousInt2);
              DialogsActivity.access$2702(DialogsActivity.this, true);
              return;
              bool = false;
              break;
              bool = false;
              break label228;
            }
          }
          if (i > DialogsActivity.this.prevPosition) {}
          for (boolean bool = true;; bool = false) {
            break;
          }
        }
      });
      if (this.searchString == null)
      {
        this.dialogsAdapter = new DialogsAdapter(paramContext, this.dialogsType);
        if ((AndroidUtilities.isTablet()) && (this.openedDialogId != 0L)) {
          this.dialogsAdapter.setOpenedDialogId(this.openedDialogId);
        }
        this.listView.setAdapter(this.dialogsAdapter);
      }
      i = 0;
      if (this.searchString == null) {
        break label1325;
      }
      i = 2;
      label1068:
      this.dialogsSearchAdapter = new DialogsSearchAdapter(paramContext, i, this.dialogsType);
      this.dialogsSearchAdapter.setDelegate(new DialogsSearchAdapter.DialogsSearchAdapterDelegate()
      {
        public void didPressedOnSubDialog(int paramAnonymousInt)
        {
          if (DialogsActivity.this.onlySelect) {
            DialogsActivity.this.didSelectResult(paramAnonymousInt, true, false);
          }
          Bundle localBundle;
          label168:
          do
          {
            return;
            localBundle = new Bundle();
            if (paramAnonymousInt > 0) {
              localBundle.putInt("user_id", paramAnonymousInt);
            }
            for (;;)
            {
              if (DialogsActivity.this.actionBar != null) {
                DialogsActivity.this.actionBar.closeSearchField();
              }
              if ((AndroidUtilities.isTablet()) && (DialogsActivity.this.dialogsAdapter != null))
              {
                DialogsActivity.this.dialogsAdapter.setOpenedDialogId(DialogsActivity.access$2002(DialogsActivity.this, paramAnonymousInt));
                DialogsActivity.this.updateVisibleRows(512);
              }
              if (DialogsActivity.this.searchString == null) {
                break label168;
              }
              if (!MessagesController.checkCanOpenChat(localBundle, DialogsActivity.this)) {
                break;
              }
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
              DialogsActivity.this.presentFragment(new ChatActivity(localBundle));
              return;
              localBundle.putInt("chat_id", -paramAnonymousInt);
            }
          } while (!MessagesController.checkCanOpenChat(localBundle, DialogsActivity.this));
          DialogsActivity.this.presentFragment(new ChatActivity(localBundle));
        }
        
        public void needRemoveHint(final int paramAnonymousInt)
        {
          if (DialogsActivity.this.getParentActivity() == null) {}
          TLRPC.User localUser;
          do
          {
            return;
            localUser = MessagesController.getInstance().getUser(Integer.valueOf(paramAnonymousInt));
          } while (localUser == null);
          AlertDialog.Builder localBuilder = new AlertDialog.Builder(DialogsActivity.this.getParentActivity());
          localBuilder.setTitle(LocaleController.getString("AppName", 2131165299));
          localBuilder.setMessage(LocaleController.formatString("ChatHintsDelete", 2131165494, new Object[] { ContactsController.formatName(localUser.first_name, localUser.last_name) }));
          localBuilder.setPositiveButton(LocaleController.getString("OK", 2131166044), new DialogInterface.OnClickListener()
          {
            public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
            {
              SearchQuery.removePeer(paramAnonymousInt);
            }
          });
          localBuilder.setNegativeButton(LocaleController.getString("Cancel", 2131165386), null);
          DialogsActivity.this.showDialog(localBuilder.create());
        }
        
        public void searchStateChanged(boolean paramAnonymousBoolean)
        {
          if ((DialogsActivity.this.searching) && (DialogsActivity.this.searchWas) && (DialogsActivity.this.searchEmptyView != null))
          {
            if (paramAnonymousBoolean) {
              DialogsActivity.this.searchEmptyView.showProgress();
            }
          }
          else {
            return;
          }
          DialogsActivity.this.searchEmptyView.showTextView();
        }
      });
      if ((!MessagesController.getInstance().loadingDialogs) || (!MessagesController.getInstance().dialogs.isEmpty())) {
        break label1338;
      }
      this.searchEmptyView.setVisibility(8);
      this.emptyView.setVisibility(8);
      this.listView.setEmptyView(this.progressView);
    }
    for (;;)
    {
      if (this.searchString != null) {
        this.actionBar.openSearchField(this.searchString);
      }
      if ((!this.onlySelect) && (this.dialogsType == 0)) {
        localFrameLayout.addView(new PlayerView(paramContext, this), LayoutHelper.createFrame(-1, 39.0F, 51, 0.0F, -36.0F, 0.0F, 0.0F));
      }
      return this.fragmentView;
      if (this.searchString != null) {
        this.actionBar.setBackButtonImage(2130837700);
      }
      for (;;)
      {
        if (!BuildVars.DEBUG_VERSION) {
          break label1276;
        }
        this.actionBar.setTitle(LocaleController.getString("AppNameBeta", 2131165300));
        break;
        this.actionBar.setBackButtonDrawable(new MenuDrawable());
      }
      label1276:
      this.actionBar.setTitle(LocaleController.getString("AppName", 2131165299));
      break;
      label1295:
      i = 2;
      break label275;
      label1301:
      i = 0;
      break label724;
      label1307:
      i = 5;
      break label921;
      label1313:
      f1 = 0.0F;
      break label931;
      label1318:
      f2 = 14.0F;
      break label939;
      label1325:
      if (this.onlySelect) {
        break label1068;
      }
      i = 1;
      break label1068;
      label1338:
      this.searchEmptyView.setVisibility(8);
      this.progressView.setVisibility(8);
      this.listView.setEmptyView(this.emptyView);
    }
  }
  
  public void didReceivedNotification(int paramInt, Object... paramVarArgs)
  {
    if (paramInt == NotificationCenter.dialogsNeedReload) {
      if (this.dialogsAdapter != null)
      {
        if (this.dialogsAdapter.isDataSetChanged()) {
          this.dialogsAdapter.notifyDataSetChanged();
        }
      }
      else
      {
        if (this.dialogsSearchAdapter != null) {
          this.dialogsSearchAdapter.notifyDataSetChanged();
        }
        if (this.listView == null) {}
      }
    }
    label453:
    do
    {
      for (;;)
      {
        try
        {
          if ((MessagesController.getInstance().loadingDialogs) && (MessagesController.getInstance().dialogs.isEmpty()))
          {
            this.searchEmptyView.setVisibility(8);
            this.emptyView.setVisibility(8);
            this.listView.setEmptyView(this.progressView);
            if (paramInt != NotificationCenter.needReloadRecentDialogsSearch) {
              break label453;
            }
            if (this.dialogsSearchAdapter != null) {
              this.dialogsSearchAdapter.loadRecentSearch();
            }
            return;
            updateVisibleRows(2048);
            break;
          }
          this.progressView.setVisibility(8);
          if ((this.searching) && (this.searchWas))
          {
            this.emptyView.setVisibility(8);
            this.listView.setEmptyView(this.searchEmptyView);
            continue;
          }
        }
        catch (Exception paramVarArgs)
        {
          FileLog.e("tmessages", paramVarArgs);
          continue;
          this.searchEmptyView.setVisibility(8);
          this.listView.setEmptyView(this.emptyView);
          continue;
        }
        if (paramInt == NotificationCenter.emojiDidLoaded) {
          updateVisibleRows(0);
        } else if (paramInt == NotificationCenter.updateInterfaces) {
          updateVisibleRows(((Integer)paramVarArgs[0]).intValue());
        } else if (paramInt == NotificationCenter.appDidLogout) {
          dialogsLoaded = false;
        } else if (paramInt == NotificationCenter.encryptedChatUpdated) {
          updateVisibleRows(0);
        } else if (paramInt == NotificationCenter.contactsDidLoaded) {
          updateVisibleRows(0);
        } else if (paramInt == NotificationCenter.openedChatChanged)
        {
          if ((this.dialogsType == 0) && (AndroidUtilities.isTablet()))
          {
            boolean bool = ((Boolean)paramVarArgs[1]).booleanValue();
            long l = ((Long)paramVarArgs[0]).longValue();
            if (bool) {
              if (l != this.openedDialogId) {}
            }
            for (this.openedDialogId = 0L;; this.openedDialogId = l)
            {
              if (this.dialogsAdapter != null) {
                this.dialogsAdapter.setOpenedDialogId(this.openedDialogId);
              }
              updateVisibleRows(512);
              break;
            }
          }
        }
        else if (paramInt == NotificationCenter.notificationsSettingsUpdated) {
          updateVisibleRows(0);
        } else if ((paramInt == NotificationCenter.messageReceivedByAck) || (paramInt == NotificationCenter.messageReceivedByServer) || (paramInt == NotificationCenter.messageSendError)) {
          updateVisibleRows(4096);
        } else if (paramInt == NotificationCenter.didSetPasscode) {
          updatePasscodeButton();
        }
      }
      if (paramInt == NotificationCenter.didLoadedReplyMessages)
      {
        updateVisibleRows(0);
        return;
      }
    } while ((paramInt != NotificationCenter.reloadHints) || (this.dialogsSearchAdapter == null));
    this.dialogsSearchAdapter.notifyDataSetChanged();
  }
  
  public boolean isMainDialogList()
  {
    return (this.delegate == null) && (this.searchString == null);
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    if ((!this.onlySelect) && (this.floatingButton != null)) {
      this.floatingButton.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
      {
        public void onGlobalLayout()
        {
          ImageView localImageView = DialogsActivity.this.floatingButton;
          float f;
          if (DialogsActivity.this.floatingHidden)
          {
            f = AndroidUtilities.dp(100.0F);
            localImageView.setTranslationY(f);
            localImageView = DialogsActivity.this.floatingButton;
            if (DialogsActivity.this.floatingHidden) {
              break label93;
            }
          }
          label93:
          for (boolean bool = true;; bool = false)
          {
            localImageView.setClickable(bool);
            if (DialogsActivity.this.floatingButton != null)
            {
              if (Build.VERSION.SDK_INT >= 16) {
                break label98;
              }
              DialogsActivity.this.floatingButton.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
            return;
            f = 0.0F;
            break;
          }
          label98:
          DialogsActivity.this.floatingButton.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        }
      });
    }
  }
  
  protected void onDialogDismiss(Dialog paramDialog)
  {
    super.onDialogDismiss(paramDialog);
    if ((this.permissionDialog != null) && (paramDialog == this.permissionDialog) && (getParentActivity() != null)) {
      askForPermissons();
    }
  }
  
  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    if (getArguments() != null)
    {
      this.onlySelect = this.arguments.getBoolean("onlySelect", false);
      this.cantSendToChannels = this.arguments.getBoolean("cantSendToChannels", false);
      this.dialogsType = this.arguments.getInt("dialogsType", 0);
      this.selectAlertString = this.arguments.getString("selectAlertString");
      this.selectAlertStringGroup = this.arguments.getString("selectAlertStringGroup");
      this.addToGroupAlertString = this.arguments.getString("addToGroupAlertString");
    }
    if (this.searchString == null)
    {
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.dialogsNeedReload);
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.emojiDidLoaded);
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.encryptedChatUpdated);
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.contactsDidLoaded);
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.appDidLogout);
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.openedChatChanged);
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.notificationsSettingsUpdated);
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.messageReceivedByAck);
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.messageReceivedByServer);
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.messageSendError);
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.didSetPasscode);
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.needReloadRecentDialogsSearch);
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.didLoadedReplyMessages);
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.reloadHints);
    }
    if (!dialogsLoaded)
    {
      MessagesController.getInstance().loadDialogs(0, 100, true);
      ContactsController.getInstance().checkInviteText();
      StickersQuery.checkFeaturedStickers();
      dialogsLoaded = true;
    }
    return true;
  }
  
  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    if (this.searchString == null)
    {
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.dialogsNeedReload);
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.emojiDidLoaded);
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.encryptedChatUpdated);
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.contactsDidLoaded);
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.appDidLogout);
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.openedChatChanged);
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.notificationsSettingsUpdated);
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messageReceivedByAck);
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messageReceivedByServer);
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messageSendError);
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didSetPasscode);
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.needReloadRecentDialogsSearch);
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didLoadedReplyMessages);
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.reloadHints);
    }
    this.delegate = null;
  }
  
  public void onRequestPermissionsResultFragment(int paramInt, String[] paramArrayOfString, int[] paramArrayOfInt)
  {
    if (paramInt == 1)
    {
      int i = 0;
      if (i < paramArrayOfString.length)
      {
        if ((paramArrayOfInt.length <= i) || (paramArrayOfInt[i] != 0)) {}
        for (;;)
        {
          i += 1;
          break;
          String str = paramArrayOfString[i];
          paramInt = -1;
          switch (str.hashCode())
          {
          }
          for (;;)
          {
            switch (paramInt)
            {
            default: 
              break;
            case 0: 
              ContactsController.getInstance().readContacts();
              break;
              if (str.equals("android.permission.READ_CONTACTS"))
              {
                paramInt = 0;
                continue;
                if (str.equals("android.permission.WRITE_EXTERNAL_STORAGE")) {
                  paramInt = 1;
                }
              }
              break;
            }
          }
          ImageLoader.getInstance().checkMediaPaths();
        }
      }
    }
  }
  
  public void onResume()
  {
    super.onResume();
    if (this.dialogsAdapter != null) {
      this.dialogsAdapter.notifyDataSetChanged();
    }
    if (this.dialogsSearchAdapter != null) {
      this.dialogsSearchAdapter.notifyDataSetChanged();
    }
    Object localObject;
    if ((this.checkPermission) && (!this.onlySelect) && (Build.VERSION.SDK_INT >= 23))
    {
      localObject = getParentActivity();
      if (localObject != null)
      {
        this.checkPermission = false;
        if ((((Activity)localObject).checkSelfPermission("android.permission.READ_CONTACTS") != 0) || (((Activity)localObject).checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != 0))
        {
          if (!((Activity)localObject).shouldShowRequestPermissionRationale("android.permission.READ_CONTACTS")) {
            break label165;
          }
          localObject = new AlertDialog.Builder((Context)localObject);
          ((AlertDialog.Builder)localObject).setTitle(LocaleController.getString("AppName", 2131165299));
          ((AlertDialog.Builder)localObject).setMessage(LocaleController.getString("PermissionContacts", 2131166096));
          ((AlertDialog.Builder)localObject).setPositiveButton(LocaleController.getString("OK", 2131166044), null);
          localObject = ((AlertDialog.Builder)localObject).create();
          this.permissionDialog = ((AlertDialog)localObject);
          showDialog((Dialog)localObject);
        }
      }
    }
    return;
    label165:
    if (((Activity)localObject).shouldShowRequestPermissionRationale("android.permission.WRITE_EXTERNAL_STORAGE"))
    {
      localObject = new AlertDialog.Builder((Context)localObject);
      ((AlertDialog.Builder)localObject).setTitle(LocaleController.getString("AppName", 2131165299));
      ((AlertDialog.Builder)localObject).setMessage(LocaleController.getString("PermissionStorage", 2131166102));
      ((AlertDialog.Builder)localObject).setPositiveButton(LocaleController.getString("OK", 2131166044), null);
      localObject = ((AlertDialog.Builder)localObject).create();
      this.permissionDialog = ((AlertDialog)localObject);
      showDialog((Dialog)localObject);
      return;
    }
    askForPermissons();
  }
  
  public void setDelegate(DialogsActivityDelegate paramDialogsActivityDelegate)
  {
    this.delegate = paramDialogsActivityDelegate;
  }
  
  public void setSearchString(String paramString)
  {
    this.searchString = paramString;
  }
  
  public static abstract interface DialogsActivityDelegate
  {
    public abstract void didSelectDialog(DialogsActivity paramDialogsActivity, long paramLong, boolean paramBoolean);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/DialogsActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */