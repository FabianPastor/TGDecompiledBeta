package org.telegram.ui.Adapters;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.SendMessagesHelper.LocationProvider;
import org.telegram.messenger.SendMessagesHelper.LocationProvider.LocationProviderDelegate;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.query.SearchQuery;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.BotInfo;
import org.telegram.tgnet.TLRPC.BotInlineResult;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.ChatParticipant;
import org.telegram.tgnet.TLRPC.ChatParticipants;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.TL_botCommand;
import org.telegram.tgnet.TLRPC.TL_botInlineMessageMediaAuto;
import org.telegram.tgnet.TLRPC.TL_channelFull;
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
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Cells.BotSwitchCell;
import org.telegram.ui.Cells.ContextLinkCell;
import org.telegram.ui.Cells.ContextLinkCell.ContextLinkCellDelegate;
import org.telegram.ui.Cells.MentionCell;

public class MentionsAdapter
  extends BaseSearchAdapterRecycler
{
  private boolean allowNewMentions = true;
  private HashMap<Integer, TLRPC.BotInfo> botInfo;
  private int botsCount;
  private boolean contextMedia;
  private int contextQueryReqid;
  private Runnable contextQueryRunnable;
  private int contextUsernameReqid;
  private MentionsAdapterDelegate delegate;
  private long dialog_id;
  private TLRPC.User foundContextBot;
  private TLRPC.ChatFull info;
  private boolean isDarkTheme;
  private Location lastKnownLocation;
  private int lastPosition;
  private String lastText;
  private SendMessagesHelper.LocationProvider locationProvider = new SendMessagesHelper.LocationProvider(new SendMessagesHelper.LocationProvider.LocationProviderDelegate()
  {
    public void onLocationAcquired(Location paramAnonymousLocation)
    {
      if ((MentionsAdapter.this.foundContextBot != null) && (MentionsAdapter.this.foundContextBot.bot_inline_geo))
      {
        MentionsAdapter.access$102(MentionsAdapter.this, paramAnonymousLocation);
        MentionsAdapter.this.searchForContextBotResults(MentionsAdapter.this.foundContextBot, MentionsAdapter.this.searchingContextQuery, "");
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
  private BaseFragment parentFragment;
  private int resultLength;
  private int resultStartPosition;
  private ArrayList<TLRPC.BotInlineResult> searchResultBotContext;
  private HashMap<String, TLRPC.BotInlineResult> searchResultBotContextById;
  private TLRPC.TL_inlineBotSwitchPM searchResultBotContextSwitch;
  private ArrayList<String> searchResultCommands;
  private ArrayList<String> searchResultCommandsHelp;
  private ArrayList<TLRPC.User> searchResultCommandsUsers;
  private ArrayList<String> searchResultHashtags;
  private ArrayList<TLRPC.User> searchResultUsernames;
  private String searchingContextQuery;
  private String searchingContextUsername;
  
  public MentionsAdapter(Context paramContext, boolean paramBoolean, long paramLong, MentionsAdapterDelegate paramMentionsAdapterDelegate)
  {
    this.mContext = paramContext;
    this.delegate = paramMentionsAdapterDelegate;
    this.isDarkTheme = paramBoolean;
    this.dialog_id = paramLong;
  }
  
  private void checkLocationPermissionsOrStart()
  {
    if ((this.parentFragment == null) || (this.parentFragment.getParentActivity() == null)) {}
    do
    {
      return;
      if ((Build.VERSION.SDK_INT >= 23) && (this.parentFragment.getParentActivity().checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") != 0))
      {
        this.parentFragment.getParentActivity().requestPermissions(new String[] { "android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION" }, 2);
        return;
      }
    } while ((this.foundContextBot == null) || (!this.foundContextBot.bot_inline_geo));
    this.locationProvider.start();
  }
  
  private void onLocationUnavailable()
  {
    if ((this.foundContextBot != null) && (this.foundContextBot.bot_inline_geo))
    {
      this.lastKnownLocation = new Location("network");
      this.lastKnownLocation.setLatitude(-1000.0D);
      this.lastKnownLocation.setLongitude(-1000.0D);
      searchForContextBotResults(this.foundContextBot, this.searchingContextQuery, "");
    }
  }
  
  private void searchForContextBot(final String paramString1, final String paramString2)
  {
    if ((this.foundContextBot != null) && (this.foundContextBot.username != null) && (this.foundContextBot.username.equals(paramString1)) && (this.searchingContextQuery != null) && (this.searchingContextQuery.equals(paramString2))) {}
    do
    {
      do
      {
        return;
        this.searchResultBotContext = null;
        this.searchResultBotContextById = null;
        this.searchResultBotContextSwitch = null;
        notifyDataSetChanged();
        if (this.foundContextBot != null) {
          this.delegate.needChangePanelVisibility(false);
        }
        if (this.contextQueryRunnable != null)
        {
          AndroidUtilities.cancelRunOnUIThread(this.contextQueryRunnable);
          this.contextQueryRunnable = null;
        }
        if ((!TextUtils.isEmpty(paramString1)) && ((this.searchingContextUsername == null) || (this.searchingContextUsername.equals(paramString1)))) {
          break;
        }
        if (this.contextUsernameReqid != 0)
        {
          ConnectionsManager.getInstance().cancelRequest(this.contextUsernameReqid, true);
          this.contextUsernameReqid = 0;
        }
        if (this.contextQueryReqid != 0)
        {
          ConnectionsManager.getInstance().cancelRequest(this.contextQueryReqid, true);
          this.contextQueryReqid = 0;
        }
        this.foundContextBot = null;
        this.searchingContextUsername = null;
        this.searchingContextQuery = null;
        this.locationProvider.stop();
        this.noUserName = false;
        if (this.delegate != null) {
          this.delegate.onContextSearch(false);
        }
      } while ((paramString1 == null) || (paramString1.length() == 0));
      if (paramString2 != null) {
        break;
      }
      if (this.contextQueryReqid != 0)
      {
        ConnectionsManager.getInstance().cancelRequest(this.contextQueryReqid, true);
        this.contextQueryReqid = 0;
      }
      this.searchingContextQuery = null;
    } while (this.delegate == null);
    this.delegate.onContextSearch(false);
    return;
    if (this.delegate != null)
    {
      if (this.foundContextBot == null) {
        break label335;
      }
      this.delegate.onContextSearch(true);
    }
    for (;;)
    {
      this.searchingContextQuery = paramString2;
      this.contextQueryRunnable = new Runnable()
      {
        public void run()
        {
          if (MentionsAdapter.this.contextQueryRunnable != this) {}
          do
          {
            return;
            MentionsAdapter.access$502(MentionsAdapter.this, null);
            if ((MentionsAdapter.this.foundContextBot == null) && (!MentionsAdapter.this.noUserName)) {
              break;
            }
          } while (MentionsAdapter.this.noUserName);
          MentionsAdapter.this.searchForContextBotResults(MentionsAdapter.this.foundContextBot, paramString2, "");
          return;
          TLRPC.TL_contacts_resolveUsername localTL_contacts_resolveUsername = new TLRPC.TL_contacts_resolveUsername();
          localTL_contacts_resolveUsername.username = MentionsAdapter.access$702(MentionsAdapter.this, paramString1);
          MentionsAdapter.access$802(MentionsAdapter.this, ConnectionsManager.getInstance().sendRequest(localTL_contacts_resolveUsername, new RequestDelegate()
          {
            public void run(final TLObject paramAnonymous2TLObject, final TLRPC.TL_error paramAnonymous2TL_error)
            {
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  if ((MentionsAdapter.this.searchingContextUsername == null) || (!MentionsAdapter.this.searchingContextUsername.equals(MentionsAdapter.3.this.val$username))) {
                    return;
                  }
                  MentionsAdapter.access$802(MentionsAdapter.this, 0);
                  MentionsAdapter.access$002(MentionsAdapter.this, null);
                  MentionsAdapter.this.locationProvider.stop();
                  if (paramAnonymous2TL_error == null)
                  {
                    final Object localObject1 = (TLRPC.TL_contacts_resolvedPeer)paramAnonymous2TLObject;
                    if (!((TLRPC.TL_contacts_resolvedPeer)localObject1).users.isEmpty())
                    {
                      Object localObject2 = (TLRPC.User)((TLRPC.TL_contacts_resolvedPeer)localObject1).users.get(0);
                      if ((((TLRPC.User)localObject2).bot) && (((TLRPC.User)localObject2).bot_inline_placeholder != null))
                      {
                        MessagesController.getInstance().putUser((TLRPC.User)localObject2, false);
                        MessagesStorage.getInstance().putUsersAndChats(((TLRPC.TL_contacts_resolvedPeer)localObject1).users, null, true, true);
                        MentionsAdapter.access$002(MentionsAdapter.this, (TLRPC.User)localObject2);
                        if (MentionsAdapter.this.foundContextBot.bot_inline_geo)
                        {
                          if ((ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).getBoolean("inlinegeo_" + MentionsAdapter.this.foundContextBot.id, false)) || (MentionsAdapter.this.parentFragment == null) || (MentionsAdapter.this.parentFragment.getParentActivity() == null)) {
                            break label442;
                          }
                          localObject1 = MentionsAdapter.this.foundContextBot;
                          localObject2 = new AlertDialog.Builder(MentionsAdapter.this.parentFragment.getParentActivity());
                          ((AlertDialog.Builder)localObject2).setTitle(LocaleController.getString("ShareYouLocationTitle", 2131166281));
                          ((AlertDialog.Builder)localObject2).setMessage(LocaleController.getString("ShareYouLocationInline", 2131166280));
                          ((AlertDialog.Builder)localObject2).setPositiveButton(LocaleController.getString("OK", 2131166044), new DialogInterface.OnClickListener()
                          {
                            public void onClick(DialogInterface paramAnonymous4DialogInterface, int paramAnonymous4Int)
                            {
                              if (localObject1 != null)
                              {
                                ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit().putBoolean("inlinegeo_" + localObject1.id, true).commit();
                                MentionsAdapter.this.checkLocationPermissionsOrStart();
                              }
                            }
                          });
                          ((AlertDialog.Builder)localObject2).setNegativeButton(LocaleController.getString("Cancel", 2131165386), new DialogInterface.OnClickListener()
                          {
                            public void onClick(DialogInterface paramAnonymous4DialogInterface, int paramAnonymous4Int)
                            {
                              MentionsAdapter.this.onLocationUnavailable();
                            }
                          });
                          MentionsAdapter.this.parentFragment.showDialog(((AlertDialog.Builder)localObject2).create());
                        }
                      }
                    }
                  }
                  while (MentionsAdapter.this.foundContextBot == null)
                  {
                    MentionsAdapter.access$602(MentionsAdapter.this, true);
                    return;
                    label442:
                    MentionsAdapter.this.checkLocationPermissionsOrStart();
                  }
                  if (MentionsAdapter.this.delegate != null) {
                    MentionsAdapter.this.delegate.onContextSearch(true);
                  }
                  MentionsAdapter.this.searchForContextBotResults(MentionsAdapter.this.foundContextBot, MentionsAdapter.this.searchingContextQuery, "");
                }
              });
            }
          }));
        }
      };
      AndroidUtilities.runOnUIThread(this.contextQueryRunnable, 400L);
      return;
      label335:
      if (paramString1.equals("gif"))
      {
        this.searchingContextUsername = "gif";
        this.delegate.onContextSearch(false);
      }
    }
  }
  
  private void searchForContextBotResults(TLRPC.User paramUser, final String paramString1, final String paramString2)
  {
    if (this.contextQueryReqid != 0)
    {
      ConnectionsManager.getInstance().cancelRequest(this.contextQueryReqid, true);
      this.contextQueryReqid = 0;
    }
    if ((paramString1 == null) || (paramUser == null)) {
      this.searchingContextQuery = null;
    }
    while ((paramUser.bot_inline_geo) && (this.lastKnownLocation == null)) {
      return;
    }
    TLRPC.TL_messages_getInlineBotResults localTL_messages_getInlineBotResults = new TLRPC.TL_messages_getInlineBotResults();
    localTL_messages_getInlineBotResults.bot = MessagesController.getInputUser(paramUser);
    localTL_messages_getInlineBotResults.query = paramString1;
    localTL_messages_getInlineBotResults.offset = paramString2;
    if ((paramUser.bot_inline_geo) && (this.lastKnownLocation != null) && (this.lastKnownLocation.getLatitude() != -1000.0D))
    {
      localTL_messages_getInlineBotResults.flags |= 0x1;
      localTL_messages_getInlineBotResults.geo_point = new TLRPC.TL_inputGeoPoint();
      localTL_messages_getInlineBotResults.geo_point.lat = this.lastKnownLocation.getLatitude();
      localTL_messages_getInlineBotResults.geo_point._long = this.lastKnownLocation.getLongitude();
    }
    int i = (int)this.dialog_id;
    int j = (int)(this.dialog_id >> 32);
    if (i != 0) {}
    for (localTL_messages_getInlineBotResults.peer = MessagesController.getInputPeer(i);; localTL_messages_getInlineBotResults.peer = new TLRPC.TL_inputPeerEmpty())
    {
      this.contextQueryReqid = ConnectionsManager.getInstance().sendRequest(localTL_messages_getInlineBotResults, new RequestDelegate()
      {
        public void run(final TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
        {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              boolean bool = false;
              if ((MentionsAdapter.this.searchingContextQuery == null) || (!MentionsAdapter.4.this.val$query.equals(MentionsAdapter.this.searchingContextQuery))) {}
              do
              {
                return;
                if (MentionsAdapter.this.delegate != null) {
                  MentionsAdapter.this.delegate.onContextSearch(false);
                }
                MentionsAdapter.access$1302(MentionsAdapter.this, 0);
              } while (paramAnonymousTL_error != null);
              Object localObject1 = (TLRPC.TL_messages_botResults)paramAnonymousTLObject;
              MentionsAdapter.access$1402(MentionsAdapter.this, ((TLRPC.TL_messages_botResults)localObject1).next_offset);
              if (MentionsAdapter.this.searchResultBotContextById == null)
              {
                MentionsAdapter.access$1502(MentionsAdapter.this, new HashMap());
                MentionsAdapter.access$1602(MentionsAdapter.this, ((TLRPC.TL_messages_botResults)localObject1).switch_pm);
              }
              Object localObject2;
              int j;
              for (int i = 0; i < ((TLRPC.TL_messages_botResults)localObject1).results.size(); i = j + 1)
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
                      if (((TLRPC.BotInlineResult)localObject2).content_url == null)
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
              i = 0;
              if ((MentionsAdapter.this.searchResultBotContext == null) || (MentionsAdapter.4.this.val$offset.length() == 0))
              {
                MentionsAdapter.access$1702(MentionsAdapter.this, ((TLRPC.TL_messages_botResults)localObject1).results);
                MentionsAdapter.access$1802(MentionsAdapter.this, ((TLRPC.TL_messages_botResults)localObject1).gallery);
                MentionsAdapter.access$1902(MentionsAdapter.this, null);
                MentionsAdapter.access$2002(MentionsAdapter.this, null);
                MentionsAdapter.access$2102(MentionsAdapter.this, null);
                MentionsAdapter.access$2202(MentionsAdapter.this, null);
                MentionsAdapter.access$2302(MentionsAdapter.this, null);
                if (i == 0) {
                  break label678;
                }
                if (MentionsAdapter.this.searchResultBotContextSwitch == null) {
                  break label663;
                }
                i = 1;
                label449:
                localObject2 = MentionsAdapter.this;
                int k = MentionsAdapter.this.searchResultBotContext.size();
                int m = ((TLRPC.TL_messages_botResults)localObject1).results.size();
                if (i == 0) {
                  break label668;
                }
                j = 1;
                label488:
                ((MentionsAdapter)localObject2).notifyItemChanged(j + (k - m) - 1);
                localObject2 = MentionsAdapter.this;
                j = MentionsAdapter.this.searchResultBotContext.size();
                k = ((TLRPC.TL_messages_botResults)localObject1).results.size();
                if (i == 0) {
                  break label673;
                }
                i = 1;
                label539:
                ((MentionsAdapter)localObject2).notifyItemRangeInserted(i + (j - k), ((TLRPC.TL_messages_botResults)localObject1).results.size());
              }
              for (;;)
              {
                localObject1 = MentionsAdapter.this.delegate;
                if ((!MentionsAdapter.this.searchResultBotContext.isEmpty()) || (MentionsAdapter.this.searchResultBotContextSwitch != null)) {
                  bool = true;
                }
                ((MentionsAdapter.MentionsAdapterDelegate)localObject1).needChangePanelVisibility(bool);
                return;
                j = 1;
                MentionsAdapter.this.searchResultBotContext.addAll(((TLRPC.TL_messages_botResults)localObject1).results);
                i = j;
                if (!((TLRPC.TL_messages_botResults)localObject1).results.isEmpty()) {
                  break;
                }
                MentionsAdapter.access$1402(MentionsAdapter.this, "");
                i = j;
                break;
                label663:
                i = 0;
                break label449;
                label668:
                j = 0;
                break label488;
                label673:
                i = 0;
                break label539;
                label678:
                MentionsAdapter.this.notifyDataSetChanged();
              }
            }
          });
        }
      }, 2);
      return;
    }
  }
  
  public void clearRecentHashtags()
  {
    super.clearRecentHashtags();
    this.searchResultHashtags.clear();
    notifyDataSetChanged();
    if (this.delegate != null) {
      this.delegate.needChangePanelVisibility(false);
    }
  }
  
  public String getBotCaption()
  {
    if (this.foundContextBot != null) {
      return this.foundContextBot.bot_inline_placeholder;
    }
    if ((this.searchingContextUsername != null) && (this.searchingContextUsername.equals("gif"))) {
      return "Search GIFs";
    }
    return null;
  }
  
  public TLRPC.TL_inlineBotSwitchPM getBotContextSwitch()
  {
    return this.searchResultBotContextSwitch;
  }
  
  public int getContextBotId()
  {
    if (this.foundContextBot != null) {
      return this.foundContextBot.id;
    }
    return 0;
  }
  
  public String getContextBotName()
  {
    if (this.foundContextBot != null) {
      return this.foundContextBot.username;
    }
    return "";
  }
  
  public TLRPC.User getContextBotUser()
  {
    if (this.foundContextBot != null) {
      return this.foundContextBot;
    }
    return null;
  }
  
  public Object getItem(int paramInt)
  {
    Object localObject2 = null;
    int i;
    Object localObject1;
    if (this.searchResultBotContext != null)
    {
      i = paramInt;
      if (this.searchResultBotContextSwitch != null) {
        if (paramInt == 0) {
          localObject1 = this.searchResultBotContextSwitch;
        }
      }
    }
    do
    {
      do
      {
        do
        {
          do
          {
            do
            {
              do
              {
                do
                {
                  do
                  {
                    do
                    {
                      return localObject1;
                      i = paramInt - 1;
                      localObject1 = localObject2;
                    } while (i < 0);
                    localObject1 = localObject2;
                  } while (i >= this.searchResultBotContext.size());
                  return this.searchResultBotContext.get(i);
                  if (this.searchResultUsernames == null) {
                    break;
                  }
                  localObject1 = localObject2;
                } while (paramInt < 0);
                localObject1 = localObject2;
              } while (paramInt >= this.searchResultUsernames.size());
              return this.searchResultUsernames.get(paramInt);
              if (this.searchResultHashtags == null) {
                break;
              }
              localObject1 = localObject2;
            } while (paramInt < 0);
            localObject1 = localObject2;
          } while (paramInt >= this.searchResultHashtags.size());
          return this.searchResultHashtags.get(paramInt);
          localObject1 = localObject2;
        } while (this.searchResultCommands == null);
        localObject1 = localObject2;
      } while (paramInt < 0);
      localObject1 = localObject2;
    } while (paramInt >= this.searchResultCommands.size());
    if ((this.searchResultCommandsUsers != null) && ((this.botsCount != 1) || ((this.info instanceof TLRPC.TL_channelFull))))
    {
      if (this.searchResultCommandsUsers.get(paramInt) != null)
      {
        localObject2 = this.searchResultCommands.get(paramInt);
        if (this.searchResultCommandsUsers.get(paramInt) != null) {}
        for (localObject1 = ((TLRPC.User)this.searchResultCommandsUsers.get(paramInt)).username;; localObject1 = "") {
          return String.format("%s@%s", new Object[] { localObject2, localObject1 });
        }
      }
      return String.format("%s", new Object[] { this.searchResultCommands.get(paramInt) });
    }
    return this.searchResultCommands.get(paramInt);
  }
  
  public int getItemCount()
  {
    int j = 0;
    int i = 0;
    if (this.searchResultBotContext != null)
    {
      j = this.searchResultBotContext.size();
      if (this.searchResultBotContextSwitch != null) {
        i = 1;
      }
      i += j;
    }
    do
    {
      return i;
      if (this.searchResultUsernames != null) {
        return this.searchResultUsernames.size();
      }
      if (this.searchResultHashtags != null) {
        return this.searchResultHashtags.size();
      }
      i = j;
    } while (this.searchResultCommands == null);
    return this.searchResultCommands.size();
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
    if (this.searchResultBotContext != null)
    {
      if ((paramInt == 0) && (this.searchResultBotContextSwitch != null)) {
        return 2;
      }
      return 1;
    }
    return 0;
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
  
  public boolean isBotCommands()
  {
    return this.searchResultCommands != null;
  }
  
  public boolean isBotContext()
  {
    return this.searchResultBotContext != null;
  }
  
  public boolean isLongClickEnabled()
  {
    return (this.searchResultHashtags != null) || (this.searchResultCommands != null);
  }
  
  public boolean isMediaLayout()
  {
    return this.contextMedia;
  }
  
  public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
  {
    boolean bool2 = true;
    int i;
    if (this.searchResultBotContext != null) {
      if (this.searchResultBotContextSwitch != null)
      {
        i = 1;
        if (paramViewHolder.getItemViewType() != 2) {
          break label54;
        }
        if (i != 0) {
          ((BotSwitchCell)paramViewHolder.itemView).setText(this.searchResultBotContextSwitch.text);
        }
      }
    }
    label54:
    label139:
    do
    {
      return;
      i = 0;
      break;
      int j = paramInt;
      if (i != 0) {
        j = paramInt - 1;
      }
      paramViewHolder = (ContextLinkCell)paramViewHolder.itemView;
      localObject = (TLRPC.BotInlineResult)this.searchResultBotContext.get(j);
      boolean bool3 = this.contextMedia;
      boolean bool1;
      if (j != this.searchResultBotContext.size() - 1)
      {
        bool1 = true;
        if ((i == 0) || (j != 0)) {
          break label139;
        }
      }
      for (;;)
      {
        paramViewHolder.setLink((TLRPC.BotInlineResult)localObject, bool3, bool1, bool2);
        return;
        bool1 = false;
        break;
        bool2 = false;
      }
      if (this.searchResultUsernames != null)
      {
        ((MentionCell)paramViewHolder.itemView).setUser((TLRPC.User)this.searchResultUsernames.get(paramInt));
        return;
      }
      if (this.searchResultHashtags != null)
      {
        ((MentionCell)paramViewHolder.itemView).setText((String)this.searchResultHashtags.get(paramInt));
        return;
      }
    } while (this.searchResultCommands == null);
    Object localObject = (MentionCell)paramViewHolder.itemView;
    String str1 = (String)this.searchResultCommands.get(paramInt);
    String str2 = (String)this.searchResultCommandsHelp.get(paramInt);
    if (this.searchResultCommandsUsers != null) {}
    for (paramViewHolder = (TLRPC.User)this.searchResultCommandsUsers.get(paramInt);; paramViewHolder = null)
    {
      ((MentionCell)localObject).setBotCommand(str1, str2, paramViewHolder);
      return;
    }
  }
  
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
  {
    if (paramInt == 1)
    {
      paramViewGroup = new ContextLinkCell(this.mContext);
      ((ContextLinkCell)paramViewGroup).setDelegate(new ContextLinkCell.ContextLinkCellDelegate()
      {
        public void didPressedImage(ContextLinkCell paramAnonymousContextLinkCell)
        {
          MentionsAdapter.this.delegate.onContextClick(paramAnonymousContextLinkCell.getResult());
        }
      });
    }
    for (;;)
    {
      return new Holder(paramViewGroup);
      if (paramInt == 2)
      {
        paramViewGroup = new BotSwitchCell(this.mContext);
      }
      else
      {
        paramViewGroup = new MentionCell(this.mContext);
        ((MentionCell)paramViewGroup).setIsDarkTheme(this.isDarkTheme);
      }
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
      ConnectionsManager.getInstance().cancelRequest(this.contextUsernameReqid, true);
      this.contextUsernameReqid = 0;
    }
    if (this.contextQueryReqid != 0)
    {
      ConnectionsManager.getInstance().cancelRequest(this.contextQueryReqid, true);
      this.contextQueryReqid = 0;
    }
    this.foundContextBot = null;
    this.searchingContextUsername = null;
    this.searchingContextQuery = null;
    this.noUserName = false;
  }
  
  public void onRequestPermissionsResultFragment(int paramInt, String[] paramArrayOfString, int[] paramArrayOfInt)
  {
    if ((paramInt == 2) && (this.foundContextBot != null) && (this.foundContextBot.bot_inline_geo))
    {
      if ((paramArrayOfInt.length > 0) && (paramArrayOfInt[0] == 0)) {
        this.locationProvider.start();
      }
    }
    else {
      return;
    }
    onLocationUnavailable();
  }
  
  public void searchForContextBotForNextOffset()
  {
    if ((this.contextQueryReqid != 0) || (this.nextQueryOffset == null) || (this.nextQueryOffset.length() == 0) || (this.foundContextBot == null) || (this.searchingContextQuery == null)) {
      return;
    }
    searchForContextBotResults(this.foundContextBot, this.searchingContextQuery, this.nextQueryOffset);
  }
  
  public void searchUsernameOrHashtag(final String paramString, int paramInt, ArrayList<MessageObject> paramArrayList)
  {
    if ((paramString == null) || (paramString.length() == 0))
    {
      searchForContextBot(null, null);
      this.delegate.needChangePanelVisibility(false);
      this.lastText = null;
    }
    int i;
    Object localObject4;
    label134:
    label151:
    label228:
    label236:
    label250:
    label352:
    label361:
    label376:
    label378:
    label521:
    label744:
    do
    {
      return;
      int j = paramInt;
      i = j;
      if (paramString.length() > 0) {
        i = j - 1;
      }
      this.lastText = null;
      localObject4 = new StringBuilder();
      int n = -1;
      int k = 0;
      int m;
      int i1;
      if ((this.needBotContext) && (paramString.charAt(0) == '@'))
      {
        j = paramString.indexOf(' ');
        m = paramString.length();
        localObject1 = null;
        localObject2 = null;
        if (j > 0)
        {
          localObject1 = paramString.substring(1, j);
          localObject2 = paramString.substring(j + 1);
          if ((localObject1 == null) || (((String)localObject1).length() < 1)) {
            break label361;
          }
          j = 1;
          localObject3 = localObject1;
          if (j < ((String)localObject1).length())
          {
            m = ((String)localObject1).charAt(j);
            if (((m >= 48) && (m <= 57)) || ((m >= 97) && (m <= 122)) || ((m >= 65) && (m <= 90)) || (m == 95)) {
              break label352;
            }
            localObject3 = "";
          }
          searchForContextBot((String)localObject3, (String)localObject2);
          if (this.foundContextBot != null) {
            break label376;
          }
          i1 = -1;
          j = i;
          m = i1;
          i = n;
          if (j < 0) {
            break label521;
          }
          if (j < paramString.length()) {
            break label378;
          }
          i = k;
        }
      }
      for (;;)
      {
        j -= 1;
        k = i;
        break label250;
        if ((paramString.charAt(m - 1) == 't') && (paramString.charAt(m - 2) == 'o') && (paramString.charAt(m - 3) == 'b'))
        {
          localObject1 = paramString.substring(1);
          localObject2 = "";
          break label134;
        }
        searchForContextBot(null, null);
        break label134;
        j += 1;
        break label151;
        localObject3 = "";
        break label228;
        searchForContextBot(null, null);
        break label236;
        break;
        char c = paramString.charAt(j);
        if ((j == 0) || (paramString.charAt(j - 1) == ' ') || (paramString.charAt(j - 1) == '\n')) {
          if (c == '@')
          {
            if ((this.needUsernames) || ((this.needBotContext) && (j == 0)))
            {
              if (k != 0)
              {
                this.delegate.needChangePanelVisibility(false);
                return;
              }
              if ((this.info == null) && (j != 0))
              {
                this.lastText = paramString;
                this.lastPosition = paramInt;
                this.messages = paramArrayList;
                this.delegate.needChangePanelVisibility(false);
                return;
              }
              m = j;
              i = 0;
              this.resultStartPosition = j;
              this.resultLength = (((StringBuilder)localObject4).length() + 1);
            }
          }
          else {
            for (;;)
            {
              if (i != -1) {
                break label744;
              }
              this.delegate.needChangePanelVisibility(false);
              return;
              if (c == '#')
              {
                if (!this.hashtagsLoadedFromDb)
                {
                  loadRecentHashtags();
                  this.lastText = paramString;
                  this.lastPosition = paramInt;
                  this.messages = paramArrayList;
                  this.delegate.needChangePanelVisibility(false);
                  return;
                }
                i = 1;
                this.resultStartPosition = j;
                this.resultLength = (((StringBuilder)localObject4).length() + 1);
                ((StringBuilder)localObject4).insert(0, c);
                m = i1;
              }
              else
              {
                if ((j != 0) || (this.botInfo == null) || (c != '/')) {
                  break;
                }
                i = 2;
                this.resultStartPosition = j;
                this.resultLength = (((StringBuilder)localObject4).length() + 1);
                m = i1;
              }
            }
          }
        }
        if (c >= '0')
        {
          i = k;
          if (c <= '9') {}
        }
        else if (c >= 'a')
        {
          i = k;
          if (c <= 'z') {}
        }
        else if (c >= 'A')
        {
          i = k;
          if (c <= 'Z') {}
        }
        else
        {
          i = k;
          if (c != '_') {
            i = 1;
          }
        }
        ((StringBuilder)localObject4).insert(0, c);
      }
      if (i == 0)
      {
        paramString = new ArrayList();
        paramInt = 0;
        while (paramInt < Math.min(100, paramArrayList.size()))
        {
          i = ((MessageObject)paramArrayList.get(paramInt)).messageOwner.from_id;
          if (!paramString.contains(Integer.valueOf(i))) {
            paramString.add(Integer.valueOf(i));
          }
          paramInt += 1;
        }
        localObject1 = ((StringBuilder)localObject4).toString().toLowerCase();
        paramArrayList = new ArrayList();
        localObject2 = new HashMap();
        if ((this.needBotContext) && (m == 0) && (!SearchQuery.inlineBots.isEmpty()))
        {
          paramInt = 0;
          j = 0;
          if (j < SearchQuery.inlineBots.size())
          {
            localObject3 = MessagesController.getInstance().getUser(Integer.valueOf(((TLRPC.TL_topPeer)SearchQuery.inlineBots.get(j)).peer.user_id));
            if (localObject3 == null) {}
            do
            {
              j += 1;
              break;
              i = paramInt;
              if (((TLRPC.User)localObject3).username != null)
              {
                i = paramInt;
                if (((TLRPC.User)localObject3).username.length() > 0) {
                  if ((((String)localObject1).length() <= 0) || (!((TLRPC.User)localObject3).username.toLowerCase().startsWith((String)localObject1)))
                  {
                    i = paramInt;
                    if (((String)localObject1).length() != 0) {}
                  }
                  else
                  {
                    paramArrayList.add(localObject3);
                    ((HashMap)localObject2).put(Integer.valueOf(((TLRPC.User)localObject3).id), localObject3);
                    i = paramInt + 1;
                  }
                }
              }
              paramInt = i;
            } while (i != 5);
          }
        }
        if ((this.info != null) && (this.info.participants != null))
        {
          paramInt = 0;
          if (paramInt < this.info.participants.participants.size())
          {
            localObject3 = (TLRPC.ChatParticipant)this.info.participants.participants.get(paramInt);
            localObject3 = MessagesController.getInstance().getUser(Integer.valueOf(((TLRPC.ChatParticipant)localObject3).user_id));
            if ((localObject3 == null) || (UserObject.isUserSelf((TLRPC.User)localObject3)) || (((HashMap)localObject2).containsKey(Integer.valueOf(((TLRPC.User)localObject3).id)))) {}
            for (;;)
            {
              paramInt += 1;
              break;
              if (((String)localObject1).length() == 0)
              {
                if ((!((TLRPC.User)localObject3).deleted) && ((this.allowNewMentions) || ((!this.allowNewMentions) && (((TLRPC.User)localObject3).username != null) && (((TLRPC.User)localObject3).username.length() != 0)))) {
                  paramArrayList.add(localObject3);
                }
              }
              else if ((((TLRPC.User)localObject3).username != null) && (((TLRPC.User)localObject3).username.length() > 0) && (((TLRPC.User)localObject3).username.toLowerCase().startsWith((String)localObject1))) {
                paramArrayList.add(localObject3);
              } else if ((this.allowNewMentions) || ((((TLRPC.User)localObject3).username != null) && (((TLRPC.User)localObject3).username.length() != 0))) {
                if ((((TLRPC.User)localObject3).first_name != null) && (((TLRPC.User)localObject3).first_name.length() > 0) && (((TLRPC.User)localObject3).first_name.toLowerCase().startsWith((String)localObject1))) {
                  paramArrayList.add(localObject3);
                } else if ((((TLRPC.User)localObject3).last_name != null) && (((TLRPC.User)localObject3).last_name.length() > 0) && (((TLRPC.User)localObject3).last_name.toLowerCase().startsWith((String)localObject1))) {
                  paramArrayList.add(localObject3);
                }
              }
            }
          }
        }
        this.searchResultHashtags = null;
        this.searchResultCommands = null;
        this.searchResultCommandsHelp = null;
        this.searchResultCommandsUsers = null;
        this.searchResultUsernames = paramArrayList;
        Collections.sort(this.searchResultUsernames, new Comparator()
        {
          public int compare(TLRPC.User paramAnonymousUser1, TLRPC.User paramAnonymousUser2)
          {
            int j = -1;
            int i;
            if ((localObject2.containsKey(Integer.valueOf(paramAnonymousUser1.id))) && (localObject2.containsKey(Integer.valueOf(paramAnonymousUser2.id)))) {
              i = 0;
            }
            int k;
            int m;
            do
            {
              do
              {
                do
                {
                  return i;
                  i = j;
                } while (localObject2.containsKey(Integer.valueOf(paramAnonymousUser1.id)));
                if (localObject2.containsKey(Integer.valueOf(paramAnonymousUser2.id))) {
                  return 1;
                }
                k = paramString.indexOf(Integer.valueOf(paramAnonymousUser1.id));
                m = paramString.indexOf(Integer.valueOf(paramAnonymousUser2.id));
                if ((k == -1) || (m == -1)) {
                  break;
                }
                i = j;
              } while (k < m);
              if (k == m) {
                return 0;
              }
              return 1;
              if (k == -1) {
                break;
              }
              i = j;
            } while (m == -1);
            if ((k == -1) && (m != -1)) {
              return 1;
            }
            return 0;
          }
        });
        notifyDataSetChanged();
        paramString = this.delegate;
        if (!paramArrayList.isEmpty()) {}
        for (bool = true;; bool = false)
        {
          paramString.needChangePanelVisibility(bool);
          return;
        }
      }
      if (i == 1)
      {
        paramString = new ArrayList();
        paramArrayList = ((StringBuilder)localObject4).toString().toLowerCase();
        paramInt = 0;
        while (paramInt < this.hashtags.size())
        {
          localObject1 = (BaseSearchAdapterRecycler.HashtagObject)this.hashtags.get(paramInt);
          if ((localObject1 != null) && (((BaseSearchAdapterRecycler.HashtagObject)localObject1).hashtag != null) && (((BaseSearchAdapterRecycler.HashtagObject)localObject1).hashtag.startsWith(paramArrayList))) {
            paramString.add(((BaseSearchAdapterRecycler.HashtagObject)localObject1).hashtag);
          }
          paramInt += 1;
        }
        this.searchResultHashtags = paramString;
        this.searchResultUsernames = null;
        this.searchResultCommands = null;
        this.searchResultCommandsHelp = null;
        this.searchResultCommandsUsers = null;
        notifyDataSetChanged();
        paramArrayList = this.delegate;
        if (!paramString.isEmpty()) {}
        for (bool = true;; bool = false)
        {
          paramArrayList.needChangePanelVisibility(bool);
          return;
        }
      }
    } while (i != 2);
    paramString = new ArrayList();
    paramArrayList = new ArrayList();
    Object localObject1 = new ArrayList();
    final Object localObject2 = ((StringBuilder)localObject4).toString().toLowerCase();
    Object localObject3 = this.botInfo.entrySet().iterator();
    while (((Iterator)localObject3).hasNext())
    {
      localObject4 = (TLRPC.BotInfo)((Map.Entry)((Iterator)localObject3).next()).getValue();
      paramInt = 0;
      while (paramInt < ((TLRPC.BotInfo)localObject4).commands.size())
      {
        TLRPC.TL_botCommand localTL_botCommand = (TLRPC.TL_botCommand)((TLRPC.BotInfo)localObject4).commands.get(paramInt);
        if ((localTL_botCommand != null) && (localTL_botCommand.command != null) && (localTL_botCommand.command.startsWith((String)localObject2)))
        {
          paramString.add("/" + localTL_botCommand.command);
          paramArrayList.add(localTL_botCommand.description);
          ((ArrayList)localObject1).add(MessagesController.getInstance().getUser(Integer.valueOf(((TLRPC.BotInfo)localObject4).user_id)));
        }
        paramInt += 1;
      }
    }
    this.searchResultHashtags = null;
    this.searchResultUsernames = null;
    this.searchResultCommands = paramString;
    this.searchResultCommandsHelp = paramArrayList;
    this.searchResultCommandsUsers = ((ArrayList)localObject1);
    notifyDataSetChanged();
    paramArrayList = this.delegate;
    if (!paramString.isEmpty()) {}
    for (boolean bool = true;; bool = false)
    {
      paramArrayList.needChangePanelVisibility(bool);
      return;
    }
  }
  
  public void setAllowNewMentions(boolean paramBoolean)
  {
    this.allowNewMentions = paramBoolean;
  }
  
  public void setBotInfo(HashMap<Integer, TLRPC.BotInfo> paramHashMap)
  {
    this.botInfo = paramHashMap;
  }
  
  public void setBotsCount(int paramInt)
  {
    this.botsCount = paramInt;
  }
  
  public void setChatInfo(TLRPC.ChatFull paramChatFull)
  {
    this.info = paramChatFull;
    if (this.lastText != null) {
      searchUsernameOrHashtag(this.lastText, this.lastPosition, this.messages);
    }
  }
  
  protected void setHashtags(ArrayList<BaseSearchAdapterRecycler.HashtagObject> paramArrayList, HashMap<String, BaseSearchAdapterRecycler.HashtagObject> paramHashMap)
  {
    super.setHashtags(paramArrayList, paramHashMap);
    if (this.lastText != null) {
      searchUsernameOrHashtag(this.lastText, this.lastPosition, this.messages);
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
  
  public void setParentFragment(BaseFragment paramBaseFragment)
  {
    this.parentFragment = paramBaseFragment;
  }
  
  public class Holder
    extends RecyclerView.ViewHolder
  {
    public Holder(View paramView)
    {
      super();
    }
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