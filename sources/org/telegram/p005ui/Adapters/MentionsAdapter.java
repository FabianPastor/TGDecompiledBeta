package org.telegram.p005ui.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collections;
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
import org.telegram.messenger.beta.R;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.p005ui.ActionBar.AlertDialog.Builder;
import org.telegram.p005ui.ActionBar.Theme;
import org.telegram.p005ui.Adapters.SearchAdapterHelper.HashtagObject;
import org.telegram.p005ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate;
import org.telegram.p005ui.Cells.BotSwitchCell;
import org.telegram.p005ui.Cells.ContextLinkCell;
import org.telegram.p005ui.Cells.MentionCell;
import org.telegram.p005ui.ChatActivity;
import org.telegram.p005ui.Components.RecyclerListView.Holder;
import org.telegram.p005ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.BotInfo;
import org.telegram.tgnet.TLRPC.BotInlineResult;
import org.telegram.tgnet.TLRPC.ChannelParticipant;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.ChatParticipant;
import org.telegram.tgnet.TLRPC.TL_botCommand;
import org.telegram.tgnet.TLRPC.TL_botInlineMessageMediaAuto;
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

/* renamed from: org.telegram.ui.Adapters.MentionsAdapter */
public class MentionsAdapter extends SelectionAdapter {
    private SparseArray<BotInfo> botInfo;
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
    private User foundContextBot;
    private ChatFull info;
    private boolean inlineMediaEnabled = true;
    private boolean isDarkTheme;
    private boolean isSearchingMentions;
    private Location lastKnownLocation;
    private int lastPosition;
    private String lastText;
    private boolean lastUsernameOnly;
    private LocationProvider locationProvider = new LocationProvider(new CLASSNAME()) {
        public void stop() {
            super.stop();
            MentionsAdapter.this.lastKnownLocation = null;
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
    private ArrayList<BotInlineResult> searchResultBotContext;
    private TL_inlineBotSwitchPM searchResultBotContextSwitch;
    private ArrayList<String> searchResultCommands;
    private ArrayList<String> searchResultCommandsHelp;
    private ArrayList<User> searchResultCommandsUsers;
    private ArrayList<String> searchResultHashtags;
    private ArrayList<EmojiSuggestion> searchResultSuggestions;
    private ArrayList<User> searchResultUsernames;
    private SparseArray<User> searchResultUsernamesMap;
    private String searchingContextQuery;
    private String searchingContextUsername;

    /* renamed from: org.telegram.ui.Adapters.MentionsAdapter$MentionsAdapterDelegate */
    public interface MentionsAdapterDelegate {
        void needChangePanelVisibility(boolean z);

        void onContextClick(BotInlineResult botInlineResult);

        void onContextSearch(boolean z);
    }

    /* renamed from: org.telegram.ui.Adapters.MentionsAdapter$1 */
    class CLASSNAME implements LocationProviderDelegate {
        CLASSNAME() {
        }

        public void onLocationAcquired(Location location) {
            if (MentionsAdapter.this.foundContextBot != null && MentionsAdapter.this.foundContextBot.bot_inline_geo) {
                MentionsAdapter.this.lastKnownLocation = location;
                MentionsAdapter.this.searchForContextBotResults(true, MentionsAdapter.this.foundContextBot, MentionsAdapter.this.searchingContextQuery, TtmlNode.ANONYMOUS_REGION_ID);
            }
        }

        public void onUnableLocationAcquire() {
            MentionsAdapter.this.onLocationUnavailable();
        }
    }

    /* renamed from: org.telegram.ui.Adapters.MentionsAdapter$3 */
    class CLASSNAME implements SearchAdapterHelperDelegate {
        public SparseArray getExcludeUsers() {
            return SearchAdapterHelper$SearchAdapterHelperDelegate$$CC.getExcludeUsers(this);
        }

        CLASSNAME() {
        }

        public void onDataSetChanged() {
            MentionsAdapter.this.notifyDataSetChanged();
        }

        public void onSetHashtags(ArrayList<HashtagObject> arrayList, HashMap<String, HashtagObject> hashMap) {
            if (MentionsAdapter.this.lastText != null) {
                MentionsAdapter.this.searchUsernameOrHashtag(MentionsAdapter.this.lastText, MentionsAdapter.this.lastPosition, MentionsAdapter.this.messages, MentionsAdapter.this.lastUsernameOnly);
            }
        }
    }

    static /* synthetic */ int access$1604(MentionsAdapter x0) {
        int i = x0.channelLastReqId + 1;
        x0.channelLastReqId = i;
        return i;
    }

    public MentionsAdapter(Context context, boolean darkTheme, long did, MentionsAdapterDelegate mentionsAdapterDelegate) {
        this.mContext = context;
        this.delegate = mentionsAdapterDelegate;
        this.isDarkTheme = darkTheme;
        this.dialog_id = did;
        this.searchAdapterHelper = new SearchAdapterHelper(true);
        this.searchAdapterHelper.setDelegate(new CLASSNAME());
    }

    public void onDestroy() {
        if (this.locationProvider != null) {
            this.locationProvider.stop();
        }
        if (this.contextQueryRunnable != null) {
            AndroidUtilities.cancelRunOnUIThread(this.contextQueryRunnable);
            this.contextQueryRunnable = null;
        }
        if (this.contextUsernameReqid != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.contextUsernameReqid, true);
            this.contextUsernameReqid = 0;
        }
        if (this.contextQueryReqid != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.contextQueryReqid, true);
            this.contextQueryReqid = 0;
        }
        this.foundContextBot = null;
        this.inlineMediaEnabled = true;
        this.searchingContextUsername = null;
        this.searchingContextQuery = null;
        this.noUserName = false;
    }

    public void setParentFragment(ChatActivity fragment) {
        this.parentFragment = fragment;
    }

    public void setChatInfo(ChatFull chatInfo) {
        this.currentAccount = UserConfig.selectedAccount;
        this.info = chatInfo;
        if (!(this.inlineMediaEnabled || this.foundContextBot == null || this.parentFragment == null)) {
            Chat chat = this.parentFragment.getCurrentChat();
            if (chat != null) {
                this.inlineMediaEnabled = ChatObject.canSendStickers(chat);
                if (this.inlineMediaEnabled) {
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

    public void setNeedUsernames(boolean value) {
        this.needUsernames = value;
    }

    public void setNeedBotContext(boolean value) {
        this.needBotContext = value;
    }

    public void setBotInfo(SparseArray<BotInfo> info) {
        this.botInfo = info;
    }

    public void setBotsCount(int count) {
        this.botsCount = count;
    }

    public void clearRecentHashtags() {
        this.searchAdapterHelper.clearRecentHashtags();
        this.searchResultHashtags.clear();
        notifyDataSetChanged();
        if (this.delegate != null) {
            this.delegate.needChangePanelVisibility(false);
        }
    }

    public TL_inlineBotSwitchPM getBotContextSwitch() {
        return this.searchResultBotContextSwitch;
    }

    public int getContextBotId() {
        return this.foundContextBot != null ? this.foundContextBot.var_id : 0;
    }

    public User getContextBotUser() {
        return this.foundContextBot;
    }

    public String getContextBotName() {
        return this.foundContextBot != null ? this.foundContextBot.username : TtmlNode.ANONYMOUS_REGION_ID;
    }

    private void processFoundUser(User user) {
        this.contextUsernameReqid = 0;
        this.locationProvider.stop();
        if (user == null || !user.bot || user.bot_inline_placeholder == null) {
            this.foundContextBot = null;
            this.inlineMediaEnabled = true;
        } else {
            this.foundContextBot = user;
            if (this.parentFragment != null) {
                Chat chat = this.parentFragment.getCurrentChat();
                if (chat != null) {
                    this.inlineMediaEnabled = ChatObject.canSendStickers(chat);
                    if (!this.inlineMediaEnabled) {
                        notifyDataSetChanged();
                        this.delegate.needChangePanelVisibility(true);
                        return;
                    }
                }
            }
            if (this.foundContextBot.bot_inline_geo) {
                if (MessagesController.getNotificationsSettings(this.currentAccount).getBoolean("inlinegeo_" + this.foundContextBot.var_id, false) || this.parentFragment == null || this.parentFragment.getParentActivity() == null) {
                    checkLocationPermissionsOrStart();
                } else {
                    User foundContextBotFinal = this.foundContextBot;
                    Builder builder = new Builder(this.parentFragment.getParentActivity());
                    builder.setTitle(LocaleController.getString("ShareYouLocationTitle", R.string.ShareYouLocationTitle));
                    builder.setMessage(LocaleController.getString("ShareYouLocationInline", R.string.ShareYouLocationInline));
                    boolean[] buttonClicked = new boolean[1];
                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new MentionsAdapter$$Lambda$0(this, buttonClicked, foundContextBotFinal));
                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), new MentionsAdapter$$Lambda$1(this, buttonClicked));
                    this.parentFragment.showDialog(builder.create(), new MentionsAdapter$$Lambda$2(this, buttonClicked));
                }
            }
        }
        if (this.foundContextBot == null) {
            this.noUserName = true;
            return;
        }
        if (this.delegate != null) {
            this.delegate.onContextSearch(true);
        }
        searchForContextBotResults(true, this.foundContextBot, this.searchingContextQuery, TtmlNode.ANONYMOUS_REGION_ID);
    }

    final /* synthetic */ void lambda$processFoundUser$0$MentionsAdapter(boolean[] buttonClicked, User foundContextBotFinal, DialogInterface dialogInterface, int i) {
        buttonClicked[0] = true;
        if (foundContextBotFinal != null) {
            MessagesController.getNotificationsSettings(this.currentAccount).edit().putBoolean("inlinegeo_" + foundContextBotFinal.var_id, true).commit();
            checkLocationPermissionsOrStart();
        }
    }

    final /* synthetic */ void lambda$processFoundUser$1$MentionsAdapter(boolean[] buttonClicked, DialogInterface dialog, int which) {
        buttonClicked[0] = true;
        onLocationUnavailable();
    }

    final /* synthetic */ void lambda$processFoundUser$2$MentionsAdapter(boolean[] buttonClicked, DialogInterface dialog) {
        if (!buttonClicked[0]) {
            onLocationUnavailable();
        }
    }

    private void searchForContextBot(String username, String query) {
        if (this.foundContextBot == null || this.foundContextBot.username == null || !this.foundContextBot.username.equals(username) || this.searchingContextQuery == null || !this.searchingContextQuery.equals(query)) {
            this.searchResultBotContext = null;
            this.searchResultBotContextSwitch = null;
            notifyDataSetChanged();
            if (this.foundContextBot != null) {
                if (this.inlineMediaEnabled || username == null || query == null) {
                    this.delegate.needChangePanelVisibility(false);
                } else {
                    return;
                }
            }
            if (this.contextQueryRunnable != null) {
                AndroidUtilities.cancelRunOnUIThread(this.contextQueryRunnable);
                this.contextQueryRunnable = null;
            }
            if (TextUtils.isEmpty(username) || !(this.searchingContextUsername == null || this.searchingContextUsername.equals(username))) {
                if (this.contextUsernameReqid != 0) {
                    ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.contextUsernameReqid, true);
                    this.contextUsernameReqid = 0;
                }
                if (this.contextQueryReqid != 0) {
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
                if (username == null || username.length() == 0) {
                    return;
                }
            }
            if (query == null) {
                if (this.contextQueryReqid != 0) {
                    ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.contextQueryReqid, true);
                    this.contextQueryReqid = 0;
                }
                this.searchingContextQuery = null;
                if (this.delegate != null) {
                    this.delegate.onContextSearch(false);
                    return;
                }
                return;
            }
            if (this.delegate != null) {
                if (this.foundContextBot != null) {
                    this.delegate.onContextSearch(true);
                } else if (username.equals("gif")) {
                    this.searchingContextUsername = "gif";
                    this.delegate.onContextSearch(false);
                }
            }
            final MessagesController messagesController = MessagesController.getInstance(this.currentAccount);
            final MessagesStorage messagesStorage = MessagesStorage.getInstance(this.currentAccount);
            this.searchingContextQuery = query;
            final String str = query;
            final String str2 = username;
            this.contextQueryRunnable = new Runnable() {
                public void run() {
                    if (MentionsAdapter.this.contextQueryRunnable == this) {
                        MentionsAdapter.this.contextQueryRunnable = null;
                        if (MentionsAdapter.this.foundContextBot == null && !MentionsAdapter.this.noUserName) {
                            MentionsAdapter.this.searchingContextUsername = str2;
                            TLObject object = messagesController.getUserOrChat(MentionsAdapter.this.searchingContextUsername);
                            if (object instanceof User) {
                                MentionsAdapter.this.processFoundUser((User) object);
                                return;
                            }
                            TL_contacts_resolveUsername req = new TL_contacts_resolveUsername();
                            req.username = MentionsAdapter.this.searchingContextUsername;
                            MentionsAdapter.this.contextUsernameReqid = ConnectionsManager.getInstance(MentionsAdapter.this.currentAccount).sendRequest(req, new MentionsAdapter$4$$Lambda$0(this, str2, messagesController, messagesStorage));
                        } else if (!MentionsAdapter.this.noUserName) {
                            MentionsAdapter.this.searchForContextBotResults(true, MentionsAdapter.this.foundContextBot, str, TtmlNode.ANONYMOUS_REGION_ID);
                        }
                    }
                }

                final /* synthetic */ void lambda$run$1$MentionsAdapter$4(String username, MessagesController messagesController, MessagesStorage messagesStorage, TLObject response, TL_error error) {
                    AndroidUtilities.runOnUIThread(new MentionsAdapter$4$$Lambda$1(this, username, error, response, messagesController, messagesStorage));
                }

                final /* synthetic */ void lambda$null$0$MentionsAdapter$4(String username, TL_error error, TLObject response, MessagesController messagesController, MessagesStorage messagesStorage) {
                    if (MentionsAdapter.this.searchingContextUsername != null && MentionsAdapter.this.searchingContextUsername.equals(username)) {
                        User user = null;
                        if (error == null) {
                            TL_contacts_resolvedPeer res = (TL_contacts_resolvedPeer) response;
                            if (!res.users.isEmpty()) {
                                user = (User) res.users.get(0);
                                messagesController.putUser(user, false);
                                messagesStorage.putUsersAndChats(res.users, null, true, true);
                            }
                        }
                        MentionsAdapter.this.processFoundUser(user);
                    }
                }
            };
            AndroidUtilities.runOnUIThread(this.contextQueryRunnable, 400);
        }
    }

    private void onLocationUnavailable() {
        if (this.foundContextBot != null && this.foundContextBot.bot_inline_geo) {
            this.lastKnownLocation = new Location("network");
            this.lastKnownLocation.setLatitude(-1000.0d);
            this.lastKnownLocation.setLongitude(-1000.0d);
            searchForContextBotResults(true, this.foundContextBot, this.searchingContextQuery, TtmlNode.ANONYMOUS_REGION_ID);
        }
    }

    private void checkLocationPermissionsOrStart() {
        if (this.parentFragment != null && this.parentFragment.getParentActivity() != null) {
            if (VERSION.SDK_INT >= 23 && this.parentFragment.getParentActivity().checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") != 0) {
                this.parentFragment.getParentActivity().requestPermissions(new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}, 2);
            } else if (this.foundContextBot != null && this.foundContextBot.bot_inline_geo) {
                this.locationProvider.start();
            }
        }
    }

    public void setSearchingMentions(boolean value) {
        this.isSearchingMentions = value;
    }

    public String getBotCaption() {
        if (this.foundContextBot != null) {
            return this.foundContextBot.bot_inline_placeholder;
        }
        if (this.searchingContextUsername == null || !this.searchingContextUsername.equals("gif")) {
            return null;
        }
        return "Search GIFs";
    }

    public void searchForContextBotForNextOffset() {
        if (this.contextQueryReqid == 0 && this.nextQueryOffset != null && this.nextQueryOffset.length() != 0 && this.foundContextBot != null && this.searchingContextQuery != null) {
            searchForContextBotResults(true, this.foundContextBot, this.searchingContextQuery, this.nextQueryOffset);
        }
    }

    private void searchForContextBotResults(boolean cache, User user, String query, String offset) {
        if (this.contextQueryReqid != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.contextQueryReqid, true);
            this.contextQueryReqid = 0;
        }
        if (this.inlineMediaEnabled) {
            if (query == null || user == null) {
                this.searchingContextQuery = null;
            } else if (!user.bot_inline_geo || this.lastKnownLocation != null) {
                StringBuilder append = new StringBuilder().append(this.dialog_id).append("_").append(query).append("_").append(offset).append("_").append(this.dialog_id).append("_").append(user.var_id).append("_");
                Object valueOf = (!user.bot_inline_geo || this.lastKnownLocation == null || this.lastKnownLocation.getLatitude() == -1000.0d) ? TtmlNode.ANONYMOUS_REGION_ID : Double.valueOf(this.lastKnownLocation.getLatitude() + this.lastKnownLocation.getLongitude());
                String key = append.append(valueOf).toString();
                MessagesStorage messagesStorage = MessagesStorage.getInstance(this.currentAccount);
                RequestDelegate requestDelegate = new MentionsAdapter$$Lambda$3(this, query, cache, user, offset, messagesStorage, key);
                if (cache) {
                    messagesStorage.getBotCache(key, requestDelegate);
                    return;
                }
                TL_messages_getInlineBotResults req = new TL_messages_getInlineBotResults();
                req.bot = MessagesController.getInstance(this.currentAccount).getInputUser(user);
                req.query = query;
                req.offset = offset;
                if (!(!user.bot_inline_geo || this.lastKnownLocation == null || this.lastKnownLocation.getLatitude() == -1000.0d)) {
                    req.flags |= 1;
                    req.geo_point = new TL_inputGeoPoint();
                    req.geo_point.lat = AndroidUtilities.fixLocationCoord(this.lastKnownLocation.getLatitude());
                    req.geo_point._long = AndroidUtilities.fixLocationCoord(this.lastKnownLocation.getLongitude());
                }
                int lower_id = (int) this.dialog_id;
                int high_id = (int) (this.dialog_id >> 32);
                if (lower_id != 0) {
                    req.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(lower_id);
                } else {
                    req.peer = new TL_inputPeerEmpty();
                }
                this.contextQueryReqid = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, requestDelegate, 2);
            }
        } else if (this.delegate != null) {
            this.delegate.onContextSearch(false);
        }
    }

    final /* synthetic */ void lambda$searchForContextBotResults$4$MentionsAdapter(String query, boolean cache, User user, String offset, MessagesStorage messagesStorage, String key, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new MentionsAdapter$$Lambda$7(this, query, cache, response, user, offset, messagesStorage, key));
    }

    final /* synthetic */ void lambda$null$3$MentionsAdapter(String query, boolean cache, TLObject response, User user, String offset, MessagesStorage messagesStorage, String key) {
        if (this.searchingContextQuery != null && query.equals(this.searchingContextQuery)) {
            this.contextQueryReqid = 0;
            if (cache && response == null) {
                searchForContextBotResults(false, user, query, offset);
            } else if (this.delegate != null) {
                this.delegate.onContextSearch(false);
            }
            if (response instanceof TL_messages_botResults) {
                boolean z;
                TL_messages_botResults res = (TL_messages_botResults) response;
                if (!(cache || res.cache_time == 0)) {
                    messagesStorage.saveBotCache(key, res);
                }
                this.nextQueryOffset = res.next_offset;
                if (this.searchResultBotContextSwitch == null) {
                    this.searchResultBotContextSwitch = res.switch_pm;
                }
                int a = 0;
                while (a < res.results.size()) {
                    BotInlineResult result = (BotInlineResult) res.results.get(a);
                    if (!(result.document instanceof TL_document) && !(result.photo instanceof TL_photo) && result.content == null && (result.send_message instanceof TL_botInlineMessageMediaAuto)) {
                        res.results.remove(a);
                        a--;
                    }
                    result.query_id = res.query_id;
                    a++;
                }
                boolean added = false;
                if (this.searchResultBotContext == null || offset.length() == 0) {
                    this.searchResultBotContext = res.results;
                    this.contextMedia = res.gallery;
                } else {
                    added = true;
                    this.searchResultBotContext.addAll(res.results);
                    if (res.results.isEmpty()) {
                        this.nextQueryOffset = TtmlNode.ANONYMOUS_REGION_ID;
                    }
                }
                this.searchResultHashtags = null;
                this.searchResultUsernames = null;
                this.searchResultUsernamesMap = null;
                this.searchResultCommands = null;
                this.searchResultSuggestions = null;
                this.searchResultCommandsHelp = null;
                this.searchResultCommandsUsers = null;
                if (added) {
                    boolean hasTop = this.searchResultBotContextSwitch != null;
                    notifyItemChanged(((hasTop ? 1 : 0) + (this.searchResultBotContext.size() - res.results.size())) - 1);
                    notifyItemRangeInserted((hasTop ? 1 : 0) + (this.searchResultBotContext.size() - res.results.size()), res.results.size());
                } else {
                    notifyDataSetChanged();
                }
                MentionsAdapterDelegate mentionsAdapterDelegate = this.delegate;
                if (this.searchResultBotContext.isEmpty() && this.searchResultBotContextSwitch == null) {
                    z = false;
                } else {
                    z = true;
                }
                mentionsAdapterDelegate.needChangePanelVisibility(z);
            }
        }
    }

    public void searchUsernameOrHashtag(String text, int position, ArrayList<MessageObject> messageObjects, boolean usernameOnly) {
        if (this.channelReqId != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.channelReqId, true);
            this.channelReqId = 0;
        }
        if (this.searchGlobalRunnable != null) {
            AndroidUtilities.cancelRunOnUIThread(this.searchGlobalRunnable);
            this.searchGlobalRunnable = null;
        }
        if (TextUtils.isEmpty(text)) {
            searchForContextBot(null, null);
            this.delegate.needChangePanelVisibility(false);
            this.lastText = null;
            return;
        }
        int a;
        char ch;
        int searchPostion = position;
        if (text.length() > 0) {
            searchPostion--;
        }
        this.lastText = null;
        this.lastUsernameOnly = usernameOnly;
        StringBuilder result = new StringBuilder();
        int foundType = -1;
        boolean hasIllegalUsernameCharacters = false;
        if (!usernameOnly && this.needBotContext && text.charAt(0) == '@') {
            int index = text.indexOf(32);
            int len = text.length();
            String username = null;
            String query = null;
            if (index > 0) {
                username = text.substring(1, index);
                query = text.substring(index + 1);
            } else if (text.charAt(len - 1) == 't' && text.charAt(len - 2) == 'o' && text.charAt(len - 3) == 'b') {
                username = text.substring(1);
                query = TtmlNode.ANONYMOUS_REGION_ID;
            } else {
                searchForContextBot(null, null);
            }
            if (username == null || username.length() < 1) {
                username = TtmlNode.ANONYMOUS_REGION_ID;
            } else {
                for (a = 1; a < username.length(); a++) {
                    ch = username.charAt(a);
                    if ((ch < '0' || ch > '9') && ((ch < 'a' || ch > 'z') && ((ch < 'A' || ch > 'Z') && ch != '_'))) {
                        username = TtmlNode.ANONYMOUS_REGION_ID;
                        break;
                    }
                }
            }
            searchForContextBot(username, query);
        } else {
            searchForContextBot(null, null);
        }
        if (this.foundContextBot == null) {
            MessagesController messagesController = MessagesController.getInstance(this.currentAccount);
            int dogPostion = -1;
            if (usernameOnly) {
                result.append(text.substring(1));
                this.resultStartPosition = 0;
                this.resultLength = result.length();
                foundType = 0;
            } else {
                a = searchPostion;
                while (a >= 0) {
                    if (a < text.length()) {
                        ch = text.charAt(a);
                        if (a == 0 || text.charAt(a - 1) == ' ' || text.charAt(a - 1) == 10) {
                            if (ch != '@') {
                                if (ch != '#') {
                                    if (a != 0 || this.botInfo == null || ch != '/') {
                                        if (ch == ':' && result.length() > 0) {
                                            foundType = 3;
                                            this.resultStartPosition = a;
                                            this.resultLength = result.length() + 1;
                                            break;
                                        }
                                    }
                                    foundType = 2;
                                    this.resultStartPosition = a;
                                    this.resultLength = result.length() + 1;
                                    break;
                                } else if (this.searchAdapterHelper.loadRecentHashtags()) {
                                    foundType = 1;
                                    this.resultStartPosition = a;
                                    this.resultLength = result.length() + 1;
                                    result.insert(0, ch);
                                } else {
                                    this.lastText = text;
                                    this.lastPosition = position;
                                    this.messages = messageObjects;
                                    this.delegate.needChangePanelVisibility(false);
                                    return;
                                }
                            } else if (this.needUsernames || (this.needBotContext && a == 0)) {
                                if (this.info != null || a == 0) {
                                    dogPostion = a;
                                    foundType = 0;
                                    this.resultStartPosition = a;
                                    this.resultLength = result.length() + 1;
                                } else {
                                    this.lastText = text;
                                    this.lastPosition = position;
                                    this.messages = messageObjects;
                                    this.delegate.needChangePanelVisibility(false);
                                    return;
                                }
                            }
                        }
                        if ((ch < '0' || ch > '9') && ((ch < 'a' || ch > 'z') && ((ch < 'A' || ch > 'Z') && ch != '_'))) {
                            hasIllegalUsernameCharacters = true;
                        }
                        result.insert(0, ch);
                    }
                    a--;
                }
            }
            ArrayList<String> newResult;
            if (foundType == -1) {
                this.delegate.needChangePanelVisibility(false);
            } else if (foundType == 0) {
                User user;
                Chat chat;
                ArrayList<Integer> users = new ArrayList();
                for (a = 0; a < Math.min(100, messageObjects.size()); a++) {
                    int from_id = ((MessageObject) messageObjects.get(a)).messageOwner.from_id;
                    if (!users.contains(Integer.valueOf(from_id))) {
                        users.add(Integer.valueOf(from_id));
                    }
                }
                String usernameString = result.toString().toLowerCase();
                boolean hasSpace = usernameString.indexOf(32) >= 0;
                ArrayList<User> newResult2 = new ArrayList();
                SparseArray<User> newResultsHashMap = new SparseArray();
                SparseArray<User> newMap = new SparseArray();
                ArrayList<TL_topPeer> inlineBots = DataQuery.getInstance(this.currentAccount).inlineBots;
                if (!usernameOnly && this.needBotContext && dogPostion == 0 && !inlineBots.isEmpty()) {
                    int count = 0;
                    for (a = 0; a < inlineBots.size(); a++) {
                        user = messagesController.getUser(Integer.valueOf(((TL_topPeer) inlineBots.get(a)).peer.user_id));
                        if (user != null) {
                            if (user.username != null && user.username.length() > 0 && ((usernameString.length() > 0 && user.username.toLowerCase().startsWith(usernameString)) || usernameString.length() == 0)) {
                                newResult2.add(user);
                                newResultsHashMap.put(user.var_id, user);
                                count++;
                            }
                            if (count == 5) {
                                break;
                            }
                        }
                    }
                }
                if (this.parentFragment != null) {
                    chat = this.parentFragment.getCurrentChat();
                } else if (this.info != null) {
                    chat = messagesController.getChat(Integer.valueOf(this.info.var_id));
                } else {
                    chat = null;
                }
                if (!(chat == null || this.info == null || this.info.participants == null || (ChatObject.isChannel(chat) && !chat.megagroup))) {
                    for (a = 0; a < this.info.participants.participants.size(); a++) {
                        user = messagesController.getUser(Integer.valueOf(((ChatParticipant) this.info.participants.participants.get(a)).user_id));
                        if (user != null && (usernameOnly || !UserObject.isUserSelf(user))) {
                            if (newResultsHashMap.indexOfKey(user.var_id) < 0) {
                                if (usernameString.length() == 0) {
                                    if (!user.deleted) {
                                        newResult2.add(user);
                                    }
                                } else if (user.username != null && user.username.length() > 0 && user.username.toLowerCase().startsWith(usernameString)) {
                                    newResult2.add(user);
                                    newMap.put(user.var_id, user);
                                } else if (user.first_name != null && user.first_name.length() > 0 && user.first_name.toLowerCase().startsWith(usernameString)) {
                                    newResult2.add(user);
                                    newMap.put(user.var_id, user);
                                } else if (user.last_name != null && user.last_name.length() > 0 && user.last_name.toLowerCase().startsWith(usernameString)) {
                                    newResult2.add(user);
                                    newMap.put(user.var_id, user);
                                } else if (hasSpace && ContactsController.formatName(user.first_name, user.last_name).toLowerCase().startsWith(usernameString)) {
                                    newResult2.add(user);
                                    newMap.put(user.var_id, user);
                                }
                            }
                        }
                    }
                }
                this.searchResultHashtags = null;
                this.searchResultCommands = null;
                this.searchResultCommandsHelp = null;
                this.searchResultCommandsUsers = null;
                this.searchResultSuggestions = null;
                this.searchResultUsernames = newResult2;
                this.searchResultUsernamesMap = newMap;
                if (chat != null && chat.megagroup && usernameString.length() > 0) {
                    final String str = usernameString;
                    final MessagesController messagesController2 = messagesController;
                    CLASSNAME CLASSNAME = new Runnable() {
                        public void run() {
                            if (MentionsAdapter.this.searchGlobalRunnable == this) {
                                TL_channels_getParticipants req = new TL_channels_getParticipants();
                                req.channel = MessagesController.getInputChannel(chat);
                                req.limit = 20;
                                req.offset = 0;
                                req.filter = new TL_channelParticipantsSearch();
                                req.filter.var_q = str;
                                MentionsAdapter.this.channelReqId = ConnectionsManager.getInstance(MentionsAdapter.this.currentAccount).sendRequest(req, new MentionsAdapter$5$$Lambda$0(this, MentionsAdapter.access$1604(MentionsAdapter.this), messagesController2));
                            }
                        }

                        final /* synthetic */ void lambda$run$1$MentionsAdapter$5(int currentReqId, MessagesController messagesController, TLObject response, TL_error error) {
                            AndroidUtilities.runOnUIThread(new MentionsAdapter$5$$Lambda$1(this, currentReqId, error, response, messagesController));
                        }

                        final /* synthetic */ void lambda$null$0$MentionsAdapter$5(int currentReqId, TL_error error, TLObject response, MessagesController messagesController) {
                            if (!(MentionsAdapter.this.channelReqId == 0 || currentReqId != MentionsAdapter.this.channelLastReqId || MentionsAdapter.this.searchResultUsernamesMap == null || MentionsAdapter.this.searchResultUsernames == null || error != null)) {
                                TL_channels_channelParticipants res = (TL_channels_channelParticipants) response;
                                messagesController.putUsers(res.users, false);
                                if (!res.participants.isEmpty()) {
                                    int currentUserId = UserConfig.getInstance(MentionsAdapter.this.currentAccount).getClientUserId();
                                    for (int a = 0; a < res.participants.size(); a++) {
                                        ChannelParticipant participant = (ChannelParticipant) res.participants.get(a);
                                        if (MentionsAdapter.this.searchResultUsernamesMap.indexOfKey(participant.user_id) < 0 && (MentionsAdapter.this.isSearchingMentions || participant.user_id != currentUserId)) {
                                            User user = messagesController.getUser(Integer.valueOf(participant.user_id));
                                            if (user != null) {
                                                MentionsAdapter.this.searchResultUsernames.add(user);
                                            } else {
                                                return;
                                            }
                                        }
                                    }
                                    MentionsAdapter.this.notifyDataSetChanged();
                                }
                            }
                            MentionsAdapter.this.channelReqId = 0;
                        }
                    };
                    this.searchGlobalRunnable = CLASSNAME;
                    AndroidUtilities.runOnUIThread(CLASSNAME, 200);
                }
                Collections.sort(this.searchResultUsernames, new MentionsAdapter$$Lambda$4(newResultsHashMap, users));
                notifyDataSetChanged();
                this.delegate.needChangePanelVisibility(!newResult2.isEmpty());
            } else if (foundType == 1) {
                newResult = new ArrayList();
                String hashtagString = result.toString().toLowerCase();
                ArrayList<HashtagObject> hashtags = this.searchAdapterHelper.getHashtags();
                for (a = 0; a < hashtags.size(); a++) {
                    HashtagObject hashtagObject = (HashtagObject) hashtags.get(a);
                    if (!(hashtagObject == null || hashtagObject.hashtag == null || !hashtagObject.hashtag.startsWith(hashtagString))) {
                        newResult.add(hashtagObject.hashtag);
                    }
                }
                this.searchResultHashtags = newResult;
                this.searchResultUsernames = null;
                this.searchResultUsernamesMap = null;
                this.searchResultCommands = null;
                this.searchResultCommandsHelp = null;
                this.searchResultCommandsUsers = null;
                this.searchResultSuggestions = null;
                notifyDataSetChanged();
                this.delegate.needChangePanelVisibility(!newResult.isEmpty());
            } else if (foundType == 2) {
                newResult = new ArrayList();
                ArrayList<String> newResultHelp = new ArrayList();
                ArrayList<User> newResultUsers = new ArrayList();
                String command = result.toString().toLowerCase();
                for (int b = 0; b < this.botInfo.size(); b++) {
                    BotInfo info = (BotInfo) this.botInfo.valueAt(b);
                    for (a = 0; a < info.commands.size(); a++) {
                        TL_botCommand botCommand = (TL_botCommand) info.commands.get(a);
                        if (!(botCommand == null || botCommand.command == null || !botCommand.command.startsWith(command))) {
                            newResult.add("/" + botCommand.command);
                            newResultHelp.add(botCommand.description);
                            newResultUsers.add(messagesController.getUser(Integer.valueOf(info.user_id)));
                        }
                    }
                }
                this.searchResultHashtags = null;
                this.searchResultUsernames = null;
                this.searchResultUsernamesMap = null;
                this.searchResultSuggestions = null;
                this.searchResultCommands = newResult;
                this.searchResultCommandsHelp = newResultHelp;
                this.searchResultCommandsUsers = newResultUsers;
                notifyDataSetChanged();
                this.delegate.needChangePanelVisibility(!newResult.isEmpty());
            } else if (foundType != 3) {
            } else {
                if (hasIllegalUsernameCharacters) {
                    this.delegate.needChangePanelVisibility(false);
                    return;
                }
                Object[] suggestions = Emoji.getSuggestion(result.toString());
                if (suggestions != null) {
                    this.searchResultSuggestions = new ArrayList();
                    for (EmojiSuggestion suggestion : suggestions) {
                        suggestion.emoji = suggestion.emoji.replace("\ufe0f", TtmlNode.ANONYMOUS_REGION_ID);
                        this.searchResultSuggestions.add(suggestion);
                    }
                    Emoji.loadRecentEmoji();
                    Collections.sort(this.searchResultSuggestions, MentionsAdapter$$Lambda$5.$instance);
                }
                this.searchResultHashtags = null;
                this.searchResultUsernames = null;
                this.searchResultUsernamesMap = null;
                this.searchResultCommands = null;
                this.searchResultCommandsHelp = null;
                this.searchResultCommandsUsers = null;
                notifyDataSetChanged();
                this.delegate.needChangePanelVisibility(this.searchResultSuggestions != null);
            }
        }
    }

    static final /* synthetic */ int lambda$searchUsernameOrHashtag$5$MentionsAdapter(SparseArray newResultsHashMap, ArrayList users, User lhs, User rhs) {
        if (newResultsHashMap.indexOfKey(lhs.var_id) >= 0 && newResultsHashMap.indexOfKey(rhs.var_id) >= 0) {
            return 0;
        }
        if (newResultsHashMap.indexOfKey(lhs.var_id) >= 0) {
            return -1;
        }
        if (newResultsHashMap.indexOfKey(rhs.var_id) >= 0) {
            return 1;
        }
        int lhsNum = users.indexOf(Integer.valueOf(lhs.var_id));
        int rhsNum = users.indexOf(Integer.valueOf(rhs.var_id));
        if (lhsNum == -1 || rhsNum == -1) {
            if (lhsNum != -1 && rhsNum == -1) {
                return -1;
            }
            if (lhsNum != -1 || rhsNum == -1) {
                return 0;
            }
            return 1;
        } else if (lhsNum >= rhsNum) {
            return lhsNum == rhsNum ? 0 : 1;
        } else {
            return -1;
        }
    }

    static final /* synthetic */ int lambda$searchUsernameOrHashtag$6$MentionsAdapter(EmojiSuggestion o1, EmojiSuggestion o2) {
        Integer n1 = (Integer) Emoji.emojiUseHistory.get(o1.emoji);
        if (n1 == null) {
            n1 = Integer.valueOf(0);
        }
        Integer n2 = (Integer) Emoji.emojiUseHistory.get(o2.emoji);
        if (n2 == null) {
            n2 = Integer.valueOf(0);
        }
        return n2.compareTo(n1);
    }

    public int getResultStartPosition() {
        return this.resultStartPosition;
    }

    public int getResultLength() {
        return this.resultLength;
    }

    public ArrayList<BotInlineResult> getSearchResultBotContext() {
        return this.searchResultBotContext;
    }

    public int getItemCount() {
        int i = 1;
        if (this.foundContextBot != null && !this.inlineMediaEnabled) {
            return 1;
        }
        if (this.searchResultBotContext != null) {
            int size = this.searchResultBotContext.size();
            if (this.searchResultBotContextSwitch == null) {
                i = 0;
            }
            return i + size;
        } else if (this.searchResultUsernames != null) {
            return this.searchResultUsernames.size();
        } else {
            if (this.searchResultHashtags != null) {
                return this.searchResultHashtags.size();
            }
            if (this.searchResultCommands != null) {
                return this.searchResultCommands.size();
            }
            return this.searchResultSuggestions != null ? this.searchResultSuggestions.size() : 0;
        }
    }

    public int getItemViewType(int position) {
        if (this.foundContextBot != null && !this.inlineMediaEnabled) {
            return 3;
        }
        if (this.searchResultBotContext == null) {
            return 0;
        }
        if (position != 0 || this.searchResultBotContextSwitch == null) {
            return 1;
        }
        return 2;
    }

    public void addHashtagsFromMessage(CharSequence message) {
        this.searchAdapterHelper.addHashtagsFromMessage(message);
    }

    public int getItemPosition(int i) {
        if (this.searchResultBotContext == null || this.searchResultBotContextSwitch == null) {
            return i;
        }
        return i - 1;
    }

    public Object getItem(int i) {
        if (this.searchResultBotContext != null) {
            if (this.searchResultBotContextSwitch != null) {
                if (i == 0) {
                    return this.searchResultBotContextSwitch;
                }
                i--;
            }
            if (i < 0 || i >= this.searchResultBotContext.size()) {
                return null;
            }
            return this.searchResultBotContext.get(i);
        } else if (this.searchResultUsernames != null) {
            if (i < 0 || i >= this.searchResultUsernames.size()) {
                return null;
            }
            return this.searchResultUsernames.get(i);
        } else if (this.searchResultHashtags != null) {
            if (i < 0 || i >= this.searchResultHashtags.size()) {
                return null;
            }
            return this.searchResultHashtags.get(i);
        } else if (this.searchResultSuggestions != null) {
            if (i < 0 || i >= this.searchResultSuggestions.size()) {
                return null;
            }
            return this.searchResultSuggestions.get(i);
        } else if (this.searchResultCommands == null || i < 0 || i >= this.searchResultCommands.size()) {
            return null;
        } else {
            if (this.searchResultCommandsUsers == null || (this.botsCount == 1 && !(this.info instanceof TL_channelFull))) {
                return this.searchResultCommands.get(i);
            }
            if (this.searchResultCommandsUsers.get(i) != null) {
                String str = "%s@%s";
                Object[] objArr = new Object[2];
                objArr[0] = this.searchResultCommands.get(i);
                objArr[1] = this.searchResultCommandsUsers.get(i) != null ? ((User) this.searchResultCommandsUsers.get(i)).username : TtmlNode.ANONYMOUS_REGION_ID;
                return String.format(str, objArr);
            }
            return String.format("%s", new Object[]{this.searchResultCommands.get(i)});
        }
    }

    public boolean isLongClickEnabled() {
        return (this.searchResultHashtags == null && this.searchResultCommands == null) ? false : true;
    }

    public boolean isBotCommands() {
        return this.searchResultCommands != null;
    }

    public boolean isBotContext() {
        return this.searchResultBotContext != null;
    }

    public boolean isBannedInline() {
        return (this.foundContextBot == null || this.inlineMediaEnabled) ? false : true;
    }

    public boolean isMediaLayout() {
        return this.contextMedia;
    }

    public boolean isEnabled(ViewHolder holder) {
        return this.foundContextBot == null || this.inlineMediaEnabled;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case 0:
                view = new MentionCell(this.mContext);
                ((MentionCell) view).setIsDarkTheme(this.isDarkTheme);
                break;
            case 1:
                view = new ContextLinkCell(this.mContext);
                ((ContextLinkCell) view).setDelegate(new MentionsAdapter$$Lambda$6(this));
                break;
            case 2:
                view = new BotSwitchCell(this.mContext);
                break;
            default:
                View textView = new TextView(this.mContext);
                textView.setPadding(AndroidUtilities.m9dp(8.0f), AndroidUtilities.m9dp(8.0f), AndroidUtilities.m9dp(8.0f), AndroidUtilities.m9dp(8.0f));
                textView.setTextSize(1, 14.0f);
                textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
                view = textView;
                break;
        }
        return new Holder(view);
    }

    final /* synthetic */ void lambda$onCreateViewHolder$7$MentionsAdapter(ContextLinkCell cell) {
        this.delegate.onContextClick(cell.getResult());
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        boolean z = true;
        if (holder.getItemViewType() == 3) {
            TextView textView = holder.itemView;
            Chat chat = this.parentFragment.getCurrentChat();
            if (chat == null) {
                return;
            }
            if (AndroidUtilities.isBannedForever(chat.banned_rights.until_date)) {
                textView.setText(LocaleController.getString("AttachInlineRestrictedForever", R.string.AttachInlineRestrictedForever));
            } else {
                textView.setText(LocaleController.formatString("AttachInlineRestricted", R.string.AttachInlineRestricted, LocaleController.formatDateForBan((long) chat.banned_rights.until_date)));
            }
        } else if (this.searchResultBotContext != null) {
            boolean hasTop;
            if (this.searchResultBotContextSwitch != null) {
                hasTop = true;
            } else {
                hasTop = false;
            }
            if (holder.getItemViewType() != 2) {
                if (hasTop) {
                    position--;
                }
                ContextLinkCell contextLinkCell = (ContextLinkCell) holder.itemView;
                BotInlineResult botInlineResult = (BotInlineResult) this.searchResultBotContext.get(position);
                boolean z2 = this.contextMedia;
                boolean z3 = position != this.searchResultBotContext.size() + -1;
                if (!(hasTop && position == 0)) {
                    z = false;
                }
                contextLinkCell.setLink(botInlineResult, z2, z3, z);
            } else if (hasTop) {
                ((BotSwitchCell) holder.itemView).setText(this.searchResultBotContextSwitch.text);
            }
        } else if (this.searchResultUsernames != null) {
            ((MentionCell) holder.itemView).setUser((User) this.searchResultUsernames.get(position));
        } else if (this.searchResultHashtags != null) {
            ((MentionCell) holder.itemView).setText((String) this.searchResultHashtags.get(position));
        } else if (this.searchResultSuggestions != null) {
            ((MentionCell) holder.itemView).setEmojiSuggestion((EmojiSuggestion) this.searchResultSuggestions.get(position));
        } else if (this.searchResultCommands != null) {
            ((MentionCell) holder.itemView).setBotCommand((String) this.searchResultCommands.get(position), (String) this.searchResultCommandsHelp.get(position), this.searchResultCommandsUsers != null ? (User) this.searchResultCommandsUsers.get(position) : null);
        }
    }

    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != 2 || this.foundContextBot == null || !this.foundContextBot.bot_inline_geo) {
            return;
        }
        if (grantResults.length <= 0 || grantResults[0] != 0) {
            onLocationUnavailable();
        } else {
            this.locationProvider.start();
        }
    }
}
