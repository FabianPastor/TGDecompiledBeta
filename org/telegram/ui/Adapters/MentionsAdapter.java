package org.telegram.ui.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DataQuery;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.EmojiSuggestion;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.SendMessagesHelper.LocationProvider;
import org.telegram.messenger.SendMessagesHelper.LocationProvider.LocationProviderDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.BotInfo;
import org.telegram.tgnet.TLRPC.BotInlineResult;
import org.telegram.tgnet.TLRPC.ChannelParticipant;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.ChatParticipant;
import org.telegram.tgnet.TLRPC.ChatParticipants;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.TL_botCommand;
import org.telegram.tgnet.TLRPC.TL_botInlineMessageMediaAuto;
import org.telegram.tgnet.TLRPC.TL_channelBannedRights;
import org.telegram.tgnet.TLRPC.TL_channelFull;
import org.telegram.tgnet.TLRPC.TL_channelParticipantsSearch;
import org.telegram.tgnet.TLRPC.TL_channels_channelParticipants;
import org.telegram.tgnet.TLRPC.TL_channels_getParticipants;
import org.telegram.tgnet.TLRPC.TL_contacts_resolveUsername;
import org.telegram.tgnet.TLRPC.TL_contacts_resolvedPeer;
import org.telegram.tgnet.TLRPC.TL_document;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inlineBotSwitchPM;
import org.telegram.tgnet.TLRPC.TL_inputGeoPoint;
import org.telegram.tgnet.TLRPC.TL_inputPeerEmpty;
import org.telegram.tgnet.TLRPC.TL_messages_botResults;
import org.telegram.tgnet.TLRPC.TL_messages_getInlineBotResults;
import org.telegram.tgnet.TLRPC.TL_photo;
import org.telegram.tgnet.TLRPC.TL_topPeer;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.BotSwitchCell;
import org.telegram.ui.Cells.ContextLinkCell;
import org.telegram.ui.Cells.ContextLinkCell.ContextLinkCellDelegate;
import org.telegram.ui.Cells.MentionCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class MentionsAdapter
  extends RecyclerListView.SelectionAdapter
{
  private SparseArray<TLRPC.BotInfo> botInfo;
  private int botsCount;
  private int channelLastReqId;
  private int channelReqId;
  private boolean contextMedia;
  private int contextQueryReqid;
  private Runnable contextQueryRunnable;
  private int contextUsernameReqid;
  private int currentAccount = UserConfig.selectedAccount;
  private MentionsAdapterDelegate delegate;
  private long dialog_id;
  private TLRPC.User foundContextBot;
  private TLRPC.ChatFull info;
  private boolean inlineMediaEnabled = true;
  private boolean isDarkTheme;
  private boolean isSearchingMentions;
  private Location lastKnownLocation;
  private int lastPosition;
  private String lastText;
  private boolean lastUsernameOnly;
  private SendMessagesHelper.LocationProvider locationProvider = new SendMessagesHelper.LocationProvider(new SendMessagesHelper.LocationProvider.LocationProviderDelegate()
  {
    public void onLocationAcquired(Location paramAnonymousLocation)
    {
      if ((MentionsAdapter.this.foundContextBot != null) && (MentionsAdapter.this.foundContextBot.bot_inline_geo))
      {
        MentionsAdapter.access$102(MentionsAdapter.this, paramAnonymousLocation);
        MentionsAdapter.this.searchForContextBotResults(true, MentionsAdapter.this.foundContextBot, MentionsAdapter.this.searchingContextQuery, "");
      }
    }
    
    public void onUnableLocationAcquire()
    {
      MentionsAdapter.this.onLocationUnavailable();
    }
  })
  {
    public void stop()
    {
      super.stop();
      MentionsAdapter.access$102(MentionsAdapter.this, null);
    }
  };
  private Context mContext;
  private ArrayList<MessageObject> messages;
  private boolean needBotContext = true;
  private boolean needUsernames = true;
  private String nextQueryOffset;
  private boolean noUserName;
  private ChatActivity parentFragment;
  private int resultLength;
  private int resultStartPosition;
  private SearchAdapterHelper searchAdapterHelper;
  private Runnable searchGlobalRunnable;
  private ArrayList<TLRPC.BotInlineResult> searchResultBotContext;
  private HashMap<String, TLRPC.BotInlineResult> searchResultBotContextById;
  private TLRPC.TL_inlineBotSwitchPM searchResultBotContextSwitch;
  private ArrayList<String> searchResultCommands;
  private ArrayList<String> searchResultCommandsHelp;
  private ArrayList<TLRPC.User> searchResultCommandsUsers;
  private ArrayList<String> searchResultHashtags;
  private ArrayList<EmojiSuggestion> searchResultSuggestions;
  private ArrayList<TLRPC.User> searchResultUsernames;
  private SparseArray<TLRPC.User> searchResultUsernamesMap;
  private String searchingContextQuery;
  private String searchingContextUsername;
  
  public MentionsAdapter(Context paramContext, boolean paramBoolean, long paramLong, MentionsAdapterDelegate paramMentionsAdapterDelegate)
  {
    this.mContext = paramContext;
    this.delegate = paramMentionsAdapterDelegate;
    this.isDarkTheme = paramBoolean;
    this.dialog_id = paramLong;
    this.searchAdapterHelper = new SearchAdapterHelper(true);
    this.searchAdapterHelper.setDelegate(new SearchAdapterHelper.SearchAdapterHelperDelegate()
    {
      public void onDataSetChanged()
      {
        MentionsAdapter.this.notifyDataSetChanged();
      }
      
      public void onSetHashtags(ArrayList<SearchAdapterHelper.HashtagObject> paramAnonymousArrayList, HashMap<String, SearchAdapterHelper.HashtagObject> paramAnonymousHashMap)
      {
        if (MentionsAdapter.this.lastText != null) {
          MentionsAdapter.this.searchUsernameOrHashtag(MentionsAdapter.this.lastText, MentionsAdapter.this.lastPosition, MentionsAdapter.this.messages, MentionsAdapter.this.lastUsernameOnly);
        }
      }
    });
  }
  
  private void checkLocationPermissionsOrStart()
  {
    if ((this.parentFragment == null) || (this.parentFragment.getParentActivity() == null)) {}
    for (;;)
    {
      return;
      if ((Build.VERSION.SDK_INT >= 23) && (this.parentFragment.getParentActivity().checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") != 0)) {
        this.parentFragment.getParentActivity().requestPermissions(new String[] { "android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION" }, 2);
      } else if ((this.foundContextBot != null) && (this.foundContextBot.bot_inline_geo)) {
        this.locationProvider.start();
      }
    }
  }
  
  private void onLocationUnavailable()
  {
    if ((this.foundContextBot != null) && (this.foundContextBot.bot_inline_geo))
    {
      this.lastKnownLocation = new Location("network");
      this.lastKnownLocation.setLatitude(-1000.0D);
      this.lastKnownLocation.setLongitude(-1000.0D);
      searchForContextBotResults(true, this.foundContextBot, this.searchingContextQuery, "");
    }
  }
  
  private void processFoundUser(TLRPC.User paramUser)
  {
    this.contextUsernameReqid = 0;
    this.locationProvider.stop();
    if ((paramUser != null) && (paramUser.bot) && (paramUser.bot_inline_placeholder != null))
    {
      this.foundContextBot = paramUser;
      if (this.parentFragment != null)
      {
        paramUser = this.parentFragment.getCurrentChat();
        if (paramUser != null)
        {
          this.inlineMediaEnabled = ChatObject.canSendStickers(paramUser);
          if (!this.inlineMediaEnabled)
          {
            notifyDataSetChanged();
            this.delegate.needChangePanelVisibility(true);
          }
        }
      }
    }
    for (;;)
    {
      return;
      if (this.foundContextBot.bot_inline_geo)
      {
        if ((MessagesController.getNotificationsSettings(this.currentAccount).getBoolean("inlinegeo_" + this.foundContextBot.id, false)) || (this.parentFragment == null) || (this.parentFragment.getParentActivity() == null)) {
          break label288;
        }
        final TLRPC.User localUser = this.foundContextBot;
        paramUser = new AlertDialog.Builder(this.parentFragment.getParentActivity());
        paramUser.setTitle(LocaleController.getString("ShareYouLocationTitle", NUM));
        paramUser.setMessage(LocaleController.getString("ShareYouLocationInline", NUM));
        final boolean[] arrayOfBoolean = new boolean[1];
        paramUser.setPositiveButton(LocaleController.getString("OK", NUM), new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
          {
            arrayOfBoolean[0] = true;
            if (localUser != null)
            {
              MessagesController.getNotificationsSettings(MentionsAdapter.this.currentAccount).edit().putBoolean("inlinegeo_" + localUser.id, true).commit();
              MentionsAdapter.this.checkLocationPermissionsOrStart();
            }
          }
        });
        paramUser.setNegativeButton(LocaleController.getString("Cancel", NUM), new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
          {
            arrayOfBoolean[0] = true;
            MentionsAdapter.this.onLocationUnavailable();
          }
        });
        this.parentFragment.showDialog(paramUser.create(), new DialogInterface.OnDismissListener()
        {
          public void onDismiss(DialogInterface paramAnonymousDialogInterface)
          {
            if (arrayOfBoolean[0] == 0) {
              MentionsAdapter.this.onLocationUnavailable();
            }
          }
        });
      }
      for (;;)
      {
        if (this.foundContextBot != null) {
          break label308;
        }
        this.noUserName = true;
        break;
        label288:
        checkLocationPermissionsOrStart();
        continue;
        this.foundContextBot = null;
        this.inlineMediaEnabled = true;
      }
      label308:
      if (this.delegate != null) {
        this.delegate.onContextSearch(true);
      }
      searchForContextBotResults(true, this.foundContextBot, this.searchingContextQuery, "");
    }
  }
  
  private void searchForContextBot(final String paramString1, final String paramString2)
  {
    if ((this.foundContextBot != null) && (this.foundContextBot.username != null) && (this.foundContextBot.username.equals(paramString1)) && (this.searchingContextQuery != null) && (this.searchingContextQuery.equals(paramString2))) {}
    for (;;)
    {
      return;
      this.searchResultBotContext = null;
      this.searchResultBotContextById = null;
      this.searchResultBotContextSwitch = null;
      notifyDataSetChanged();
      if (this.foundContextBot != null)
      {
        if ((this.inlineMediaEnabled) || (paramString1 == null) || (paramString2 == null)) {
          this.delegate.needChangePanelVisibility(false);
        }
      }
      else
      {
        if (this.contextQueryRunnable != null)
        {
          AndroidUtilities.cancelRunOnUIThread(this.contextQueryRunnable);
          this.contextQueryRunnable = null;
        }
        if ((TextUtils.isEmpty(paramString1)) || ((this.searchingContextUsername != null) && (!this.searchingContextUsername.equals(paramString1))))
        {
          if (this.contextUsernameReqid != 0)
          {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.contextUsernameReqid, true);
            this.contextUsernameReqid = 0;
          }
          if (this.contextQueryReqid != 0)
          {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.contextQueryReqid, true);
            this.contextQueryReqid = 0;
          }
          this.foundContextBot = null;
          this.inlineMediaEnabled = true;
          this.searchingContextUsername = null;
          this.searchingContextQuery = null;
          this.locationProvider.stop();
          this.noUserName = false;
          if (this.delegate != null) {
            this.delegate.onContextSearch(false);
          }
          if ((paramString1 == null) || (paramString1.length() == 0)) {}
        }
        else
        {
          if (paramString2 != null) {
            break;
          }
          if (this.contextQueryReqid != 0)
          {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.contextQueryReqid, true);
            this.contextQueryReqid = 0;
          }
          this.searchingContextQuery = null;
          if (this.delegate != null) {
            this.delegate.onContextSearch(false);
          }
        }
      }
    }
    if (this.delegate != null)
    {
      if (this.foundContextBot == null) {
        break label391;
      }
      this.delegate.onContextSearch(true);
    }
    for (;;)
    {
      final MessagesController localMessagesController = MessagesController.getInstance(this.currentAccount);
      final MessagesStorage localMessagesStorage = MessagesStorage.getInstance(this.currentAccount);
      this.searchingContextQuery = paramString2;
      this.contextQueryRunnable = new Runnable()
      {
        public void run()
        {
          if (MentionsAdapter.this.contextQueryRunnable != this) {}
          for (;;)
          {
            return;
            MentionsAdapter.access$1102(MentionsAdapter.this, null);
            if ((MentionsAdapter.this.foundContextBot != null) || (MentionsAdapter.this.noUserName))
            {
              if (!MentionsAdapter.this.noUserName) {
                MentionsAdapter.this.searchForContextBotResults(true, MentionsAdapter.this.foundContextBot, paramString2, "");
              }
            }
            else
            {
              MentionsAdapter.access$1302(MentionsAdapter.this, paramString1);
              Object localObject = localMessagesController.getUserOrChat(MentionsAdapter.this.searchingContextUsername);
              if ((localObject instanceof TLRPC.User))
              {
                MentionsAdapter.this.processFoundUser((TLRPC.User)localObject);
              }
              else
              {
                localObject = new TLRPC.TL_contacts_resolveUsername();
                ((TLRPC.TL_contacts_resolveUsername)localObject).username = MentionsAdapter.this.searchingContextUsername;
                MentionsAdapter.access$1502(MentionsAdapter.this, ConnectionsManager.getInstance(MentionsAdapter.this.currentAccount).sendRequest((TLObject)localObject, new RequestDelegate()
                {
                  public void run(final TLObject paramAnonymous2TLObject, final TLRPC.TL_error paramAnonymous2TL_error)
                  {
                    AndroidUtilities.runOnUIThread(new Runnable()
                    {
                      public void run()
                      {
                        if ((MentionsAdapter.this.searchingContextUsername == null) || (!MentionsAdapter.this.searchingContextUsername.equals(MentionsAdapter.7.this.val$username))) {}
                        for (;;)
                        {
                          return;
                          Object localObject1 = null;
                          Object localObject2 = localObject1;
                          if (paramAnonymous2TL_error == null)
                          {
                            TLRPC.TL_contacts_resolvedPeer localTL_contacts_resolvedPeer = (TLRPC.TL_contacts_resolvedPeer)paramAnonymous2TLObject;
                            localObject2 = localObject1;
                            if (!localTL_contacts_resolvedPeer.users.isEmpty())
                            {
                              localObject2 = (TLRPC.User)localTL_contacts_resolvedPeer.users.get(0);
                              MentionsAdapter.7.this.val$messagesController.putUser((TLRPC.User)localObject2, false);
                              MentionsAdapter.7.this.val$messagesStorage.putUsersAndChats(localTL_contacts_resolvedPeer.users, null, true, true);
                            }
                          }
                          MentionsAdapter.this.processFoundUser((TLRPC.User)localObject2);
                        }
                      }
                    });
                  }
                }));
              }
            }
          }
        }
      };
      AndroidUtilities.runOnUIThread(this.contextQueryRunnable, 400L);
      break;
      label391:
      if (paramString1.equals("gif"))
      {
        this.searchingContextUsername = "gif";
        this.delegate.onContextSearch(false);
      }
    }
  }
  
  private void searchForContextBotResults(final boolean paramBoolean, final TLRPC.User paramUser, final String paramString1, final String paramString2)
  {
    if (this.contextQueryReqid != 0)
    {
      ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.contextQueryReqid, true);
      this.contextQueryReqid = 0;
    }
    if (!this.inlineMediaEnabled) {
      if (this.delegate != null) {
        this.delegate.onContextSearch(false);
      }
    }
    do
    {
      for (;;)
      {
        return;
        if ((paramString1 != null) && (paramUser != null)) {
          break;
        }
        this.searchingContextQuery = null;
      }
    } while ((paramUser.bot_inline_geo) && (this.lastKnownLocation == null));
    final Object localObject1 = new StringBuilder().append(this.dialog_id).append("_").append(paramString1).append("_").append(paramString2).append("_").append(this.dialog_id).append("_").append(paramUser.id).append("_");
    if ((paramUser.bot_inline_geo) && (this.lastKnownLocation != null) && (this.lastKnownLocation.getLatitude() != -1000.0D)) {}
    for (Object localObject2 = Double.valueOf(this.lastKnownLocation.getLatitude() + this.lastKnownLocation.getLongitude());; localObject2 = "")
    {
      final String str = localObject2;
      localObject1 = MessagesStorage.getInstance(this.currentAccount);
      localObject2 = new RequestDelegate()
      {
        public void run(final TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
        {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              boolean bool = false;
              if ((MentionsAdapter.this.searchingContextQuery == null) || (!MentionsAdapter.8.this.val$query.equals(MentionsAdapter.this.searchingContextQuery))) {}
              Object localObject1;
              Object localObject2;
              int j;
              for (;;)
              {
                return;
                MentionsAdapter.access$1602(MentionsAdapter.this, 0);
                if ((MentionsAdapter.8.this.val$cache) && (paramAnonymousTLObject == null)) {
                  MentionsAdapter.this.searchForContextBotResults(false, MentionsAdapter.8.this.val$user, MentionsAdapter.8.this.val$query, MentionsAdapter.8.this.val$offset);
                }
                while (paramAnonymousTLObject != null)
                {
                  localObject1 = (TLRPC.TL_messages_botResults)paramAnonymousTLObject;
                  if ((!MentionsAdapter.8.this.val$cache) && (((TLRPC.TL_messages_botResults)localObject1).cache_time != 0)) {
                    MentionsAdapter.8.this.val$messagesStorage.saveBotCache(MentionsAdapter.8.this.val$key, (TLObject)localObject1);
                  }
                  MentionsAdapter.access$1802(MentionsAdapter.this, ((TLRPC.TL_messages_botResults)localObject1).next_offset);
                  if (MentionsAdapter.this.searchResultBotContextById == null)
                  {
                    MentionsAdapter.access$1902(MentionsAdapter.this, new HashMap());
                    MentionsAdapter.access$2002(MentionsAdapter.this, ((TLRPC.TL_messages_botResults)localObject1).switch_pm);
                  }
                  for (i = 0; i < ((TLRPC.TL_messages_botResults)localObject1).results.size(); i = j + 1)
                  {
                    localObject2 = (TLRPC.BotInlineResult)((TLRPC.TL_messages_botResults)localObject1).results.get(i);
                    if (!MentionsAdapter.this.searchResultBotContextById.containsKey(((TLRPC.BotInlineResult)localObject2).id))
                    {
                      j = i;
                      if (!(((TLRPC.BotInlineResult)localObject2).document instanceof TLRPC.TL_document))
                      {
                        j = i;
                        if (!(((TLRPC.BotInlineResult)localObject2).photo instanceof TLRPC.TL_photo))
                        {
                          j = i;
                          if (((TLRPC.BotInlineResult)localObject2).content == null)
                          {
                            j = i;
                            if (!(((TLRPC.BotInlineResult)localObject2).send_message instanceof TLRPC.TL_botInlineMessageMediaAuto)) {}
                          }
                        }
                      }
                    }
                    else
                    {
                      ((TLRPC.TL_messages_botResults)localObject1).results.remove(i);
                      j = i - 1;
                    }
                    ((TLRPC.BotInlineResult)localObject2).query_id = ((TLRPC.TL_messages_botResults)localObject1).query_id;
                    MentionsAdapter.this.searchResultBotContextById.put(((TLRPC.BotInlineResult)localObject2).id, localObject2);
                  }
                  if (MentionsAdapter.this.delegate != null) {
                    MentionsAdapter.this.delegate.onContextSearch(false);
                  }
                }
              }
              int i = 0;
              if ((MentionsAdapter.this.searchResultBotContext == null) || (MentionsAdapter.8.this.val$offset.length() == 0))
              {
                MentionsAdapter.access$2102(MentionsAdapter.this, ((TLRPC.TL_messages_botResults)localObject1).results);
                MentionsAdapter.access$2202(MentionsAdapter.this, ((TLRPC.TL_messages_botResults)localObject1).gallery);
                label453:
                MentionsAdapter.access$2302(MentionsAdapter.this, null);
                MentionsAdapter.access$2402(MentionsAdapter.this, null);
                MentionsAdapter.access$2502(MentionsAdapter.this, null);
                MentionsAdapter.access$2602(MentionsAdapter.this, null);
                MentionsAdapter.access$2702(MentionsAdapter.this, null);
                MentionsAdapter.access$2802(MentionsAdapter.this, null);
                MentionsAdapter.access$2902(MentionsAdapter.this, null);
                if (i == 0) {
                  break label790;
                }
                if (MentionsAdapter.this.searchResultBotContextSwitch == null) {
                  break label774;
                }
                i = 1;
                label556:
                localObject2 = MentionsAdapter.this;
                int k = MentionsAdapter.this.searchResultBotContext.size();
                int m = ((TLRPC.TL_messages_botResults)localObject1).results.size();
                if (i == 0) {
                  break label779;
                }
                j = 1;
                label596:
                ((MentionsAdapter)localObject2).notifyItemChanged(j + (k - m) - 1);
                localObject2 = MentionsAdapter.this;
                j = MentionsAdapter.this.searchResultBotContext.size();
                k = ((TLRPC.TL_messages_botResults)localObject1).results.size();
                if (i == 0) {
                  break label785;
                }
                i = 1;
                label650:
                ((MentionsAdapter)localObject2).notifyItemRangeInserted(i + (j - k), ((TLRPC.TL_messages_botResults)localObject1).results.size());
              }
              for (;;)
              {
                localObject1 = MentionsAdapter.this.delegate;
                if ((!MentionsAdapter.this.searchResultBotContext.isEmpty()) || (MentionsAdapter.this.searchResultBotContextSwitch != null)) {
                  bool = true;
                }
                ((MentionsAdapter.MentionsAdapterDelegate)localObject1).needChangePanelVisibility(bool);
                break;
                j = 1;
                MentionsAdapter.this.searchResultBotContext.addAll(((TLRPC.TL_messages_botResults)localObject1).results);
                i = j;
                if (!((TLRPC.TL_messages_botResults)localObject1).results.isEmpty()) {
                  break label453;
                }
                MentionsAdapter.access$1802(MentionsAdapter.this, "");
                i = j;
                break label453;
                label774:
                i = 0;
                break label556;
                label779:
                j = 0;
                break label596;
                label785:
                i = 0;
                break label650;
                label790:
                MentionsAdapter.this.notifyDataSetChanged();
              }
            }
          });
        }
      };
      if (!paramBoolean) {
        break label263;
      }
      ((MessagesStorage)localObject1).getBotCache(str, (RequestDelegate)localObject2);
      break;
    }
    label263:
    localObject1 = new TLRPC.TL_messages_getInlineBotResults();
    ((TLRPC.TL_messages_getInlineBotResults)localObject1).bot = MessagesController.getInstance(this.currentAccount).getInputUser(paramUser);
    ((TLRPC.TL_messages_getInlineBotResults)localObject1).query = paramString1;
    ((TLRPC.TL_messages_getInlineBotResults)localObject1).offset = paramString2;
    if ((paramUser.bot_inline_geo) && (this.lastKnownLocation != null) && (this.lastKnownLocation.getLatitude() != -1000.0D))
    {
      ((TLRPC.TL_messages_getInlineBotResults)localObject1).flags |= 0x1;
      ((TLRPC.TL_messages_getInlineBotResults)localObject1).geo_point = new TLRPC.TL_inputGeoPoint();
      ((TLRPC.TL_messages_getInlineBotResults)localObject1).geo_point.lat = this.lastKnownLocation.getLatitude();
      ((TLRPC.TL_messages_getInlineBotResults)localObject1).geo_point._long = this.lastKnownLocation.getLongitude();
    }
    int i = (int)this.dialog_id;
    int j = (int)(this.dialog_id >> 32);
    if (i != 0) {}
    for (((TLRPC.TL_messages_getInlineBotResults)localObject1).peer = MessagesController.getInstance(this.currentAccount).getInputPeer(i);; ((TLRPC.TL_messages_getInlineBotResults)localObject1).peer = new TLRPC.TL_inputPeerEmpty())
    {
      this.contextQueryReqid = ConnectionsManager.getInstance(this.currentAccount).sendRequest((TLObject)localObject1, (RequestDelegate)localObject2, 2);
      break;
    }
  }
  
  public void addHashtagsFromMessage(CharSequence paramCharSequence)
  {
    this.searchAdapterHelper.addHashtagsFromMessage(paramCharSequence);
  }
  
  public void clearRecentHashtags()
  {
    this.searchAdapterHelper.clearRecentHashtags();
    this.searchResultHashtags.clear();
    notifyDataSetChanged();
    if (this.delegate != null) {
      this.delegate.needChangePanelVisibility(false);
    }
  }
  
  public String getBotCaption()
  {
    String str;
    if (this.foundContextBot != null) {
      str = this.foundContextBot.bot_inline_placeholder;
    }
    for (;;)
    {
      return str;
      if ((this.searchingContextUsername != null) && (this.searchingContextUsername.equals("gif"))) {
        str = "Search GIFs";
      } else {
        str = null;
      }
    }
  }
  
  public TLRPC.TL_inlineBotSwitchPM getBotContextSwitch()
  {
    return this.searchResultBotContextSwitch;
  }
  
  public int getContextBotId()
  {
    if (this.foundContextBot != null) {}
    for (int i = this.foundContextBot.id;; i = 0) {
      return i;
    }
  }
  
  public String getContextBotName()
  {
    if (this.foundContextBot != null) {}
    for (String str = this.foundContextBot.username;; str = "") {
      return str;
    }
  }
  
  public TLRPC.User getContextBotUser()
  {
    return this.foundContextBot;
  }
  
  public Object getItem(int paramInt)
  {
    Object localObject1 = null;
    int i;
    Object localObject2;
    if (this.searchResultBotContext != null)
    {
      i = paramInt;
      if (this.searchResultBotContextSwitch != null) {
        if (paramInt == 0) {
          localObject2 = this.searchResultBotContextSwitch;
        }
      }
    }
    for (;;)
    {
      return localObject2;
      i = paramInt - 1;
      localObject2 = localObject1;
      if (i >= 0)
      {
        localObject2 = localObject1;
        if (i < this.searchResultBotContext.size())
        {
          localObject2 = this.searchResultBotContext.get(i);
          continue;
          if (this.searchResultUsernames != null)
          {
            localObject2 = localObject1;
            if (paramInt >= 0)
            {
              localObject2 = localObject1;
              if (paramInt < this.searchResultUsernames.size()) {
                localObject2 = this.searchResultUsernames.get(paramInt);
              }
            }
          }
          else if (this.searchResultHashtags != null)
          {
            localObject2 = localObject1;
            if (paramInt >= 0)
            {
              localObject2 = localObject1;
              if (paramInt < this.searchResultHashtags.size()) {
                localObject2 = this.searchResultHashtags.get(paramInt);
              }
            }
          }
          else if (this.searchResultSuggestions != null)
          {
            localObject2 = localObject1;
            if (paramInt >= 0)
            {
              localObject2 = localObject1;
              if (paramInt < this.searchResultSuggestions.size()) {
                localObject2 = this.searchResultSuggestions.get(paramInt);
              }
            }
          }
          else
          {
            localObject2 = localObject1;
            if (this.searchResultCommands != null)
            {
              localObject2 = localObject1;
              if (paramInt >= 0)
              {
                localObject2 = localObject1;
                if (paramInt < this.searchResultCommands.size()) {
                  if ((this.searchResultCommandsUsers != null) && ((this.botsCount != 1) || ((this.info instanceof TLRPC.TL_channelFull))))
                  {
                    if (this.searchResultCommandsUsers.get(paramInt) != null)
                    {
                      localObject1 = this.searchResultCommands.get(paramInt);
                      if (this.searchResultCommandsUsers.get(paramInt) != null) {}
                      for (localObject2 = ((TLRPC.User)this.searchResultCommandsUsers.get(paramInt)).username;; localObject2 = "")
                      {
                        localObject2 = String.format("%s@%s", new Object[] { localObject1, localObject2 });
                        break;
                      }
                    }
                    localObject2 = String.format("%s", new Object[] { this.searchResultCommands.get(paramInt) });
                  }
                  else
                  {
                    localObject2 = this.searchResultCommands.get(paramInt);
                  }
                }
              }
            }
          }
        }
      }
    }
  }
  
  public int getItemCount()
  {
    int i = 1;
    int j = 1;
    if ((this.foundContextBot != null) && (!this.inlineMediaEnabled)) {
      i = j;
    }
    for (;;)
    {
      return i;
      if (this.searchResultBotContext != null)
      {
        j = this.searchResultBotContext.size();
        if (this.searchResultBotContextSwitch != null) {}
        for (;;)
        {
          i += j;
          break;
          i = 0;
        }
      }
      if (this.searchResultUsernames != null) {
        i = this.searchResultUsernames.size();
      } else if (this.searchResultHashtags != null) {
        i = this.searchResultHashtags.size();
      } else if (this.searchResultCommands != null) {
        i = this.searchResultCommands.size();
      } else if (this.searchResultSuggestions != null) {
        i = this.searchResultSuggestions.size();
      } else {
        i = 0;
      }
    }
  }
  
  public int getItemPosition(int paramInt)
  {
    int i = paramInt;
    if (this.searchResultBotContext != null)
    {
      i = paramInt;
      if (this.searchResultBotContextSwitch != null) {
        i = paramInt - 1;
      }
    }
    return i;
  }
  
  public int getItemViewType(int paramInt)
  {
    if ((this.foundContextBot != null) && (!this.inlineMediaEnabled)) {
      paramInt = 3;
    }
    for (;;)
    {
      return paramInt;
      if (this.searchResultBotContext != null)
      {
        if ((paramInt == 0) && (this.searchResultBotContextSwitch != null)) {
          paramInt = 2;
        } else {
          paramInt = 1;
        }
      }
      else {
        paramInt = 0;
      }
    }
  }
  
  public int getResultLength()
  {
    return this.resultLength;
  }
  
  public int getResultStartPosition()
  {
    return this.resultStartPosition;
  }
  
  public ArrayList<TLRPC.BotInlineResult> getSearchResultBotContext()
  {
    return this.searchResultBotContext;
  }
  
  public boolean isBannedInline()
  {
    if ((this.foundContextBot != null) && (!this.inlineMediaEnabled)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean isBotCommands()
  {
    if (this.searchResultCommands != null) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean isBotContext()
  {
    if (this.searchResultBotContext != null) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
  {
    if ((this.foundContextBot == null) || (this.inlineMediaEnabled)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean isLongClickEnabled()
  {
    if ((this.searchResultHashtags != null) || (this.searchResultCommands != null)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean isMediaLayout()
  {
    return this.contextMedia;
  }
  
  public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
  {
    boolean bool1 = true;
    if (paramViewHolder.getItemViewType() == 3)
    {
      localObject = (TextView)paramViewHolder.itemView;
      paramViewHolder = this.parentFragment.getCurrentChat();
      if (paramViewHolder != null)
      {
        if (!AndroidUtilities.isBannedForever(paramViewHolder.banned_rights.until_date)) {
          break label59;
        }
        ((TextView)localObject).setText(LocaleController.getString("AttachInlineRestrictedForever", NUM));
      }
    }
    label59:
    label150:
    label208:
    label238:
    do
    {
      for (;;)
      {
        return;
        ((TextView)localObject).setText(LocaleController.formatString("AttachInlineRestricted", NUM, new Object[] { LocaleController.formatDateForBan(paramViewHolder.banned_rights.until_date) }));
        continue;
        if (this.searchResultBotContext != null)
        {
          if (this.searchResultBotContextSwitch != null) {}
          for (int i = 1;; i = 0)
          {
            if (paramViewHolder.getItemViewType() != 2) {
              break label150;
            }
            if (i == 0) {
              break;
            }
            ((BotSwitchCell)paramViewHolder.itemView).setText(this.searchResultBotContextSwitch.text);
            break;
          }
          int j = paramInt;
          if (i != 0) {
            j = paramInt - 1;
          }
          paramViewHolder = (ContextLinkCell)paramViewHolder.itemView;
          localObject = (TLRPC.BotInlineResult)this.searchResultBotContext.get(j);
          boolean bool2 = this.contextMedia;
          boolean bool3;
          if (j != this.searchResultBotContext.size() - 1)
          {
            bool3 = true;
            if ((i == 0) || (j != 0)) {
              break label238;
            }
          }
          for (;;)
          {
            paramViewHolder.setLink((TLRPC.BotInlineResult)localObject, bool2, bool3, bool1);
            break;
            bool3 = false;
            break label208;
            bool1 = false;
          }
        }
        if (this.searchResultUsernames != null)
        {
          ((MentionCell)paramViewHolder.itemView).setUser((TLRPC.User)this.searchResultUsernames.get(paramInt));
        }
        else if (this.searchResultHashtags != null)
        {
          ((MentionCell)paramViewHolder.itemView).setText((String)this.searchResultHashtags.get(paramInt));
        }
        else
        {
          if (this.searchResultSuggestions == null) {
            break;
          }
          ((MentionCell)paramViewHolder.itemView).setEmojiSuggestion((EmojiSuggestion)this.searchResultSuggestions.get(paramInt));
        }
      }
    } while (this.searchResultCommands == null);
    MentionCell localMentionCell = (MentionCell)paramViewHolder.itemView;
    String str = (String)this.searchResultCommands.get(paramInt);
    Object localObject = (String)this.searchResultCommandsHelp.get(paramInt);
    if (this.searchResultCommandsUsers != null) {}
    for (paramViewHolder = (TLRPC.User)this.searchResultCommandsUsers.get(paramInt);; paramViewHolder = null)
    {
      localMentionCell.setBotCommand(str, (String)localObject, paramViewHolder);
      break;
    }
  }
  
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
  {
    switch (paramInt)
    {
    default: 
      paramViewGroup = new TextView(this.mContext);
      paramViewGroup.setPadding(AndroidUtilities.dp(8.0F), AndroidUtilities.dp(8.0F), AndroidUtilities.dp(8.0F), AndroidUtilities.dp(8.0F));
      paramViewGroup.setTextSize(1, 14.0F);
      paramViewGroup.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
    }
    for (;;)
    {
      return new RecyclerListView.Holder(paramViewGroup);
      paramViewGroup = new MentionCell(this.mContext);
      ((MentionCell)paramViewGroup).setIsDarkTheme(this.isDarkTheme);
      continue;
      paramViewGroup = new ContextLinkCell(this.mContext);
      ((ContextLinkCell)paramViewGroup).setDelegate(new ContextLinkCell.ContextLinkCellDelegate()
      {
        public void didPressedImage(ContextLinkCell paramAnonymousContextLinkCell)
        {
          MentionsAdapter.this.delegate.onContextClick(paramAnonymousContextLinkCell.getResult());
        }
      });
      continue;
      paramViewGroup = new BotSwitchCell(this.mContext);
    }
  }
  
  public void onDestroy()
  {
    if (this.locationProvider != null) {
      this.locationProvider.stop();
    }
    if (this.contextQueryRunnable != null)
    {
      AndroidUtilities.cancelRunOnUIThread(this.contextQueryRunnable);
      this.contextQueryRunnable = null;
    }
    if (this.contextUsernameReqid != 0)
    {
      ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.contextUsernameReqid, true);
      this.contextUsernameReqid = 0;
    }
    if (this.contextQueryReqid != 0)
    {
      ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.contextQueryReqid, true);
      this.contextQueryReqid = 0;
    }
    this.foundContextBot = null;
    this.inlineMediaEnabled = true;
    this.searchingContextUsername = null;
    this.searchingContextQuery = null;
    this.noUserName = false;
  }
  
  public void onRequestPermissionsResultFragment(int paramInt, String[] paramArrayOfString, int[] paramArrayOfInt)
  {
    if ((paramInt == 2) && (this.foundContextBot != null) && (this.foundContextBot.bot_inline_geo))
    {
      if ((paramArrayOfInt.length <= 0) || (paramArrayOfInt[0] != 0)) {
        break label41;
      }
      this.locationProvider.start();
    }
    for (;;)
    {
      return;
      label41:
      onLocationUnavailable();
    }
  }
  
  public void searchForContextBotForNextOffset()
  {
    if ((this.contextQueryReqid != 0) || (this.nextQueryOffset == null) || (this.nextQueryOffset.length() == 0) || (this.foundContextBot == null) || (this.searchingContextQuery == null)) {}
    for (;;)
    {
      return;
      searchForContextBotResults(true, this.foundContextBot, this.searchingContextQuery, this.nextQueryOffset);
    }
  }
  
  public void searchUsernameOrHashtag(final String paramString, int paramInt, ArrayList<MessageObject> paramArrayList, boolean paramBoolean)
  {
    if (this.channelReqId != 0)
    {
      ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.channelReqId, true);
      this.channelReqId = 0;
    }
    if (this.searchGlobalRunnable != null)
    {
      AndroidUtilities.cancelRunOnUIThread(this.searchGlobalRunnable);
      this.searchGlobalRunnable = null;
    }
    if (TextUtils.isEmpty(paramString))
    {
      searchForContextBot(null, null);
      this.delegate.needChangePanelVisibility(false);
      this.lastText = null;
    }
    for (;;)
    {
      return;
      int i = paramInt;
      int j = i;
      if (paramString.length() > 0) {
        j = i - 1;
      }
      this.lastText = null;
      this.lastUsernameOnly = paramBoolean;
      final Object localObject1 = new StringBuilder();
      int k = -1;
      int m = 0;
      int n = 0;
      int i1;
      final Object localObject2;
      final Object localObject3;
      label190:
      label207:
      final Object localObject4;
      label284:
      label292:
      int i2;
      if ((!paramBoolean) && (this.needBotContext) && (paramString.charAt(0) == '@'))
      {
        i1 = paramString.indexOf(' ');
        i = paramString.length();
        localObject2 = null;
        localObject3 = null;
        if (i1 > 0)
        {
          localObject2 = paramString.substring(1, i1);
          localObject3 = paramString.substring(i1 + 1);
          if ((localObject2 == null) || (((String)localObject2).length() < 1)) {
            break label436;
          }
          i = 1;
          localObject4 = localObject2;
          if (i < ((String)localObject2).length())
          {
            i1 = ((String)localObject2).charAt(i);
            if (((i1 >= 48) && (i1 <= 57)) || ((i1 >= 97) && (i1 <= 122)) || ((i1 >= 65) && (i1 <= 90)) || (i1 == 95)) {
              break label430;
            }
            localObject4 = "";
          }
          searchForContextBot((String)localObject4, (String)localObject3);
          if (this.foundContextBot != null) {
            break label451;
          }
          localObject2 = MessagesController.getInstance(this.currentAccount);
          i2 = -1;
          if (!paramBoolean) {
            break label453;
          }
          ((StringBuilder)localObject1).append(paramString.substring(1));
          this.resultStartPosition = 0;
          this.resultLength = ((StringBuilder)localObject1).length();
          j = 0;
          i1 = i2;
        }
      }
      for (;;)
      {
        label348:
        if (j == -1)
        {
          this.delegate.needChangePanelVisibility(false);
          break;
          if ((paramString.charAt(i - 1) == 't') && (paramString.charAt(i - 2) == 'o') && (paramString.charAt(i - 3) == 'b'))
          {
            localObject2 = paramString.substring(1);
            localObject3 = "";
            break label190;
          }
          searchForContextBot(null, null);
          break label190;
          label430:
          i++;
          break label207;
          label436:
          localObject4 = "";
          break label284;
          searchForContextBot(null, null);
          break label292;
          label451:
          break;
          label453:
          i = m;
          m = j;
          label461:
          i1 = i2;
          j = k;
          n = i;
          if (m >= 0)
          {
            if (m >= paramString.length()) {
              j = i;
            }
            for (;;)
            {
              m--;
              i = j;
              break label461;
              char c = paramString.charAt(m);
              if ((m == 0) || (paramString.charAt(m - 1) == ' ') || (paramString.charAt(m - 1) == '\n'))
              {
                if (c == '@')
                {
                  if ((!this.needUsernames) && ((!this.needBotContext) || (m != 0))) {
                    break label818;
                  }
                  if ((this.info == null) && (m != 0))
                  {
                    this.lastText = paramString;
                    this.lastPosition = paramInt;
                    this.messages = paramArrayList;
                    this.delegate.needChangePanelVisibility(false);
                    break;
                  }
                  i1 = m;
                  j = 0;
                  this.resultStartPosition = m;
                  this.resultLength = (((StringBuilder)localObject1).length() + 1);
                  n = i;
                  break label348;
                }
                if (c == '#')
                {
                  if (this.searchAdapterHelper.loadRecentHashtags())
                  {
                    j = 1;
                    this.resultStartPosition = m;
                    this.resultLength = (((StringBuilder)localObject1).length() + 1);
                    ((StringBuilder)localObject1).insert(0, c);
                    i1 = i2;
                    n = i;
                    break label348;
                  }
                  this.lastText = paramString;
                  this.lastPosition = paramInt;
                  this.messages = paramArrayList;
                  this.delegate.needChangePanelVisibility(false);
                  break;
                }
                if ((m == 0) && (this.botInfo != null) && (c == '/'))
                {
                  j = 2;
                  this.resultStartPosition = m;
                  this.resultLength = (((StringBuilder)localObject1).length() + 1);
                  i1 = i2;
                  n = i;
                  break label348;
                }
                if ((c == ':') && (((StringBuilder)localObject1).length() > 0))
                {
                  j = 3;
                  this.resultStartPosition = m;
                  this.resultLength = (((StringBuilder)localObject1).length() + 1);
                  i1 = i2;
                  n = i;
                  break label348;
                }
              }
              label818:
              if (c >= '0')
              {
                j = i;
                if (c <= '9') {}
              }
              else if (c >= 'a')
              {
                j = i;
                if (c <= 'z') {}
              }
              else if (c >= 'A')
              {
                j = i;
                if (c <= 'Z') {}
              }
              else
              {
                j = i;
                if (c != '_') {
                  j = 1;
                }
              }
              ((StringBuilder)localObject1).insert(0, c);
            }
          }
        }
      }
      Object localObject5;
      if (j == 0)
      {
        localObject3 = new ArrayList();
        for (paramInt = 0; paramInt < Math.min(100, paramArrayList.size()); paramInt++)
        {
          j = ((MessageObject)paramArrayList.get(paramInt)).messageOwner.from_id;
          if (!((ArrayList)localObject3).contains(Integer.valueOf(j))) {
            ((ArrayList)localObject3).add(Integer.valueOf(j));
          }
        }
        localObject1 = ((StringBuilder)localObject1).toString().toLowerCase();
        label1063:
        TLRPC.User localUser;
        if (((String)localObject1).indexOf(' ') >= 0)
        {
          i = 1;
          paramArrayList = new ArrayList();
          localObject4 = new SparseArray();
          localObject5 = new SparseArray();
          paramString = DataQuery.getInstance(this.currentAccount).inlineBots;
          if ((paramBoolean) || (!this.needBotContext) || (i1 != 0) || (paramString.isEmpty())) {
            break label1206;
          }
          j = 0;
          m = 0;
          if (m >= paramString.size()) {
            break label1206;
          }
          localUser = ((MessagesController)localObject2).getUser(Integer.valueOf(((TLRPC.TL_topPeer)paramString.get(m)).peer.user_id));
          if (localUser != null) {
            break label1114;
          }
        }
        label1114:
        do
        {
          m++;
          break label1063;
          i = 0;
          break;
          paramInt = j;
          if (localUser.username != null)
          {
            paramInt = j;
            if (localUser.username.length() > 0) {
              if ((((String)localObject1).length() <= 0) || (!localUser.username.toLowerCase().startsWith((String)localObject1)))
              {
                paramInt = j;
                if (((String)localObject1).length() != 0) {}
              }
              else
              {
                paramArrayList.add(localUser);
                ((SparseArray)localObject4).put(localUser.id, localUser);
                paramInt = j + 1;
              }
            }
          }
          j = paramInt;
        } while (paramInt != 5);
        label1206:
        if (this.parentFragment != null)
        {
          paramString = this.parentFragment.getCurrentChat();
          if ((paramString == null) || (this.info == null) || (this.info.participants == null) || ((ChatObject.isChannel(paramString)) && (!paramString.megagroup))) {
            break label1621;
          }
          paramInt = 0;
          label1258:
          if (paramInt >= this.info.participants.participants.size()) {
            break label1621;
          }
          localUser = ((MessagesController)localObject2).getUser(Integer.valueOf(((TLRPC.ChatParticipant)this.info.participants.participants.get(paramInt)).user_id));
          if ((localUser != null) && ((paramBoolean) || (!UserObject.isUserSelf(localUser))) && (((SparseArray)localObject4).indexOfKey(localUser.id) < 0)) {
            break label1373;
          }
        }
        for (;;)
        {
          paramInt++;
          break label1258;
          if (this.info != null)
          {
            paramString = ((MessagesController)localObject2).getChat(Integer.valueOf(this.info.id));
            break;
          }
          paramString = null;
          break;
          label1373:
          if (((String)localObject1).length() == 0)
          {
            if (!localUser.deleted) {
              paramArrayList.add(localUser);
            }
          }
          else if ((localUser.username != null) && (localUser.username.length() > 0) && (localUser.username.toLowerCase().startsWith((String)localObject1)))
          {
            paramArrayList.add(localUser);
            ((SparseArray)localObject5).put(localUser.id, localUser);
          }
          else if ((localUser.first_name != null) && (localUser.first_name.length() > 0) && (localUser.first_name.toLowerCase().startsWith((String)localObject1)))
          {
            paramArrayList.add(localUser);
            ((SparseArray)localObject5).put(localUser.id, localUser);
          }
          else if ((localUser.last_name != null) && (localUser.last_name.length() > 0) && (localUser.last_name.toLowerCase().startsWith((String)localObject1)))
          {
            paramArrayList.add(localUser);
            ((SparseArray)localObject5).put(localUser.id, localUser);
          }
          else if ((i != 0) && (ContactsController.formatName(localUser.first_name, localUser.last_name).toLowerCase().startsWith((String)localObject1)))
          {
            paramArrayList.add(localUser);
            ((SparseArray)localObject5).put(localUser.id, localUser);
          }
        }
        label1621:
        this.searchResultHashtags = null;
        this.searchResultCommands = null;
        this.searchResultCommandsHelp = null;
        this.searchResultCommandsUsers = null;
        this.searchResultSuggestions = null;
        this.searchResultUsernames = paramArrayList;
        this.searchResultUsernamesMap = ((SparseArray)localObject5);
        if ((paramString != null) && (paramString.megagroup) && (((String)localObject1).length() > 0))
        {
          paramString = new Runnable()
          {
            public void run()
            {
              if (MentionsAdapter.this.searchGlobalRunnable != this) {}
              for (;;)
              {
                return;
                TLRPC.TL_channels_getParticipants localTL_channels_getParticipants = new TLRPC.TL_channels_getParticipants();
                localTL_channels_getParticipants.channel = MessagesController.getInputChannel(paramString);
                localTL_channels_getParticipants.limit = 20;
                localTL_channels_getParticipants.offset = 0;
                localTL_channels_getParticipants.filter = new TLRPC.TL_channelParticipantsSearch();
                localTL_channels_getParticipants.filter.q = localObject1;
                final int i = MentionsAdapter.access$3104(MentionsAdapter.this);
                MentionsAdapter.access$3202(MentionsAdapter.this, ConnectionsManager.getInstance(MentionsAdapter.this.currentAccount).sendRequest(localTL_channels_getParticipants, new RequestDelegate()
                {
                  public void run(final TLObject paramAnonymous2TLObject, final TLRPC.TL_error paramAnonymous2TL_error)
                  {
                    AndroidUtilities.runOnUIThread(new Runnable()
                    {
                      public void run()
                      {
                        Object localObject;
                        if ((MentionsAdapter.this.channelReqId != 0) && (MentionsAdapter.9.1.this.val$currentReqId == MentionsAdapter.this.channelLastReqId) && (MentionsAdapter.this.searchResultUsernamesMap != null) && (MentionsAdapter.this.searchResultUsernames != null) && (paramAnonymous2TL_error == null))
                        {
                          TLRPC.TL_channels_channelParticipants localTL_channels_channelParticipants = (TLRPC.TL_channels_channelParticipants)paramAnonymous2TLObject;
                          MentionsAdapter.9.this.val$messagesController.putUsers(localTL_channels_channelParticipants.users, false);
                          if (!localTL_channels_channelParticipants.participants.isEmpty())
                          {
                            int i = UserConfig.getInstance(MentionsAdapter.this.currentAccount).getClientUserId();
                            int j = 0;
                            for (;;)
                            {
                              if (j < localTL_channels_channelParticipants.participants.size())
                              {
                                localObject = (TLRPC.ChannelParticipant)localTL_channels_channelParticipants.participants.get(j);
                                if ((MentionsAdapter.this.searchResultUsernamesMap.indexOfKey(((TLRPC.ChannelParticipant)localObject).user_id) >= 0) || ((!MentionsAdapter.this.isSearchingMentions) && (((TLRPC.ChannelParticipant)localObject).user_id == i)))
                                {
                                  j++;
                                }
                                else
                                {
                                  localObject = MentionsAdapter.9.this.val$messagesController.getUser(Integer.valueOf(((TLRPC.ChannelParticipant)localObject).user_id));
                                  if (localObject != null) {
                                    break;
                                  }
                                }
                              }
                            }
                          }
                        }
                        for (;;)
                        {
                          return;
                          MentionsAdapter.this.searchResultUsernames.add(localObject);
                          break;
                          MentionsAdapter.this.notifyDataSetChanged();
                          MentionsAdapter.access$3202(MentionsAdapter.this, 0);
                        }
                      }
                    });
                  }
                }));
              }
            }
          };
          this.searchGlobalRunnable = paramString;
          AndroidUtilities.runOnUIThread(paramString, 200L);
        }
        Collections.sort(this.searchResultUsernames, new Comparator()
        {
          public int compare(TLRPC.User paramAnonymousUser1, TLRPC.User paramAnonymousUser2)
          {
            int i = -1;
            int j;
            if ((localObject4.indexOfKey(paramAnonymousUser1.id) >= 0) && (localObject4.indexOfKey(paramAnonymousUser2.id) >= 0)) {
              j = 0;
            }
            for (;;)
            {
              return j;
              j = i;
              if (localObject4.indexOfKey(paramAnonymousUser1.id) < 0) {
                if (localObject4.indexOfKey(paramAnonymousUser2.id) >= 0)
                {
                  j = 1;
                }
                else
                {
                  int k = localObject3.indexOf(Integer.valueOf(paramAnonymousUser1.id));
                  int m = localObject3.indexOf(Integer.valueOf(paramAnonymousUser2.id));
                  if ((k != -1) && (m != -1))
                  {
                    j = i;
                    if (k >= m) {
                      if (k == m) {
                        j = 0;
                      } else {
                        j = 1;
                      }
                    }
                  }
                  else if (k != -1)
                  {
                    j = i;
                    if (m == -1) {}
                  }
                  else if ((k == -1) && (m != -1))
                  {
                    j = 1;
                  }
                  else
                  {
                    j = 0;
                  }
                }
              }
            }
          }
        });
        notifyDataSetChanged();
        paramString = this.delegate;
        if (!paramArrayList.isEmpty()) {}
        for (paramBoolean = true;; paramBoolean = false)
        {
          paramString.needChangePanelVisibility(paramBoolean);
          break;
        }
      }
      if (j == 1)
      {
        paramString = new ArrayList();
        paramArrayList = ((StringBuilder)localObject1).toString().toLowerCase();
        localObject2 = this.searchAdapterHelper.getHashtags();
        for (paramInt = 0; paramInt < ((ArrayList)localObject2).size(); paramInt++)
        {
          localObject3 = (SearchAdapterHelper.HashtagObject)((ArrayList)localObject2).get(paramInt);
          if ((localObject3 != null) && (((SearchAdapterHelper.HashtagObject)localObject3).hashtag != null) && (((SearchAdapterHelper.HashtagObject)localObject3).hashtag.startsWith(paramArrayList))) {
            paramString.add(((SearchAdapterHelper.HashtagObject)localObject3).hashtag);
          }
        }
        this.searchResultHashtags = paramString;
        this.searchResultUsernames = null;
        this.searchResultUsernamesMap = null;
        this.searchResultCommands = null;
        this.searchResultCommandsHelp = null;
        this.searchResultCommandsUsers = null;
        this.searchResultSuggestions = null;
        notifyDataSetChanged();
        paramArrayList = this.delegate;
        if (!paramString.isEmpty()) {}
        for (paramBoolean = true;; paramBoolean = false)
        {
          paramArrayList.needChangePanelVisibility(paramBoolean);
          break;
        }
      }
      if (j == 2)
      {
        paramString = new ArrayList();
        localObject3 = new ArrayList();
        paramArrayList = new ArrayList();
        localObject4 = ((StringBuilder)localObject1).toString().toLowerCase();
        for (paramInt = 0; paramInt < this.botInfo.size(); paramInt++)
        {
          localObject5 = (TLRPC.BotInfo)this.botInfo.valueAt(paramInt);
          for (j = 0; j < ((TLRPC.BotInfo)localObject5).commands.size(); j++)
          {
            localObject1 = (TLRPC.TL_botCommand)((TLRPC.BotInfo)localObject5).commands.get(j);
            if ((localObject1 != null) && (((TLRPC.TL_botCommand)localObject1).command != null) && (((TLRPC.TL_botCommand)localObject1).command.startsWith((String)localObject4)))
            {
              paramString.add("/" + ((TLRPC.TL_botCommand)localObject1).command);
              ((ArrayList)localObject3).add(((TLRPC.TL_botCommand)localObject1).description);
              paramArrayList.add(((MessagesController)localObject2).getUser(Integer.valueOf(((TLRPC.BotInfo)localObject5).user_id)));
            }
          }
        }
        this.searchResultHashtags = null;
        this.searchResultUsernames = null;
        this.searchResultUsernamesMap = null;
        this.searchResultSuggestions = null;
        this.searchResultCommands = paramString;
        this.searchResultCommandsHelp = ((ArrayList)localObject3);
        this.searchResultCommandsUsers = paramArrayList;
        notifyDataSetChanged();
        paramArrayList = this.delegate;
        if (!paramString.isEmpty()) {}
        for (paramBoolean = true;; paramBoolean = false)
        {
          paramArrayList.needChangePanelVisibility(paramBoolean);
          break;
        }
      }
      if (j == 3)
      {
        if (n == 0)
        {
          paramString = Emoji.getSuggestion(((StringBuilder)localObject1).toString());
          if (paramString != null)
          {
            this.searchResultSuggestions = new ArrayList();
            for (paramInt = 0; paramInt < paramString.length; paramInt++)
            {
              paramArrayList = (EmojiSuggestion)paramString[paramInt];
              paramArrayList.emoji = paramArrayList.emoji.replace("️", "");
              this.searchResultSuggestions.add(paramArrayList);
            }
            Emoji.loadRecentEmoji();
            Collections.sort(this.searchResultSuggestions, new Comparator()
            {
              public int compare(EmojiSuggestion paramAnonymousEmojiSuggestion1, EmojiSuggestion paramAnonymousEmojiSuggestion2)
              {
                Integer localInteger = (Integer)Emoji.emojiUseHistory.get(paramAnonymousEmojiSuggestion1.emoji);
                paramAnonymousEmojiSuggestion1 = localInteger;
                if (localInteger == null) {
                  paramAnonymousEmojiSuggestion1 = Integer.valueOf(0);
                }
                localInteger = (Integer)Emoji.emojiUseHistory.get(paramAnonymousEmojiSuggestion2.emoji);
                paramAnonymousEmojiSuggestion2 = localInteger;
                if (localInteger == null) {
                  paramAnonymousEmojiSuggestion2 = Integer.valueOf(0);
                }
                return paramAnonymousEmojiSuggestion2.compareTo(paramAnonymousEmojiSuggestion1);
              }
            });
          }
          this.searchResultHashtags = null;
          this.searchResultUsernames = null;
          this.searchResultUsernamesMap = null;
          this.searchResultCommands = null;
          this.searchResultCommandsHelp = null;
          this.searchResultCommandsUsers = null;
          notifyDataSetChanged();
          paramString = this.delegate;
          if (this.searchResultSuggestions != null) {}
          for (paramBoolean = true;; paramBoolean = false)
          {
            paramString.needChangePanelVisibility(paramBoolean);
            break;
          }
        }
        this.delegate.needChangePanelVisibility(false);
      }
    }
  }
  
  public void setBotInfo(SparseArray<TLRPC.BotInfo> paramSparseArray)
  {
    this.botInfo = paramSparseArray;
  }
  
  public void setBotsCount(int paramInt)
  {
    this.botsCount = paramInt;
  }
  
  public void setChatInfo(TLRPC.ChatFull paramChatFull)
  {
    this.currentAccount = UserConfig.selectedAccount;
    this.info = paramChatFull;
    if ((!this.inlineMediaEnabled) && (this.foundContextBot != null) && (this.parentFragment != null))
    {
      paramChatFull = this.parentFragment.getCurrentChat();
      if (paramChatFull != null)
      {
        this.inlineMediaEnabled = ChatObject.canSendStickers(paramChatFull);
        if (this.inlineMediaEnabled)
        {
          this.searchResultUsernames = null;
          notifyDataSetChanged();
          this.delegate.needChangePanelVisibility(false);
          processFoundUser(this.foundContextBot);
        }
      }
    }
    if (this.lastText != null) {
      searchUsernameOrHashtag(this.lastText, this.lastPosition, this.messages, this.lastUsernameOnly);
    }
  }
  
  public void setNeedBotContext(boolean paramBoolean)
  {
    this.needBotContext = paramBoolean;
  }
  
  public void setNeedUsernames(boolean paramBoolean)
  {
    this.needUsernames = paramBoolean;
  }
  
  public void setParentFragment(ChatActivity paramChatActivity)
  {
    this.parentFragment = paramChatActivity;
  }
  
  public void setSearchingMentions(boolean paramBoolean)
  {
    this.isSearchingMentions = paramBoolean;
  }
  
  public static abstract interface MentionsAdapterDelegate
  {
    public abstract void needChangePanelVisibility(boolean paramBoolean);
    
    public abstract void onContextClick(TLRPC.BotInlineResult paramBotInlineResult);
    
    public abstract void onContextSearch(boolean paramBoolean);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Adapters/MentionsAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */