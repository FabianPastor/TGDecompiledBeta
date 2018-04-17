package org.telegram.ui.Adapters;

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
import android.view.View;
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
import org.telegram.messenger.beta.R;
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
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.SearchAdapterHelper.HashtagObject;
import org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate;
import org.telegram.ui.Cells.BotSwitchCell;
import org.telegram.ui.Cells.ContextLinkCell;
import org.telegram.ui.Cells.ContextLinkCell.ContextLinkCellDelegate;
import org.telegram.ui.Cells.MentionCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

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
    private LocationProvider locationProvider = new LocationProvider(new C18871()) {
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
    private HashMap<String, BotInlineResult> searchResultBotContextById;
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

    public interface MentionsAdapterDelegate {
        void needChangePanelVisibility(boolean z);

        void onContextClick(BotInlineResult botInlineResult);

        void onContextSearch(boolean z);
    }

    /* renamed from: org.telegram.ui.Adapters.MentionsAdapter$1 */
    class C18871 implements LocationProviderDelegate {
        C18871() {
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
    class C18893 implements SearchAdapterHelperDelegate {
        C18893() {
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

    static /* synthetic */ int access$3104(MentionsAdapter x0) {
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
        this.searchAdapterHelper.setDelegate(new C18893());
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
        return this.foundContextBot != null ? this.foundContextBot.id : 0;
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
                SharedPreferences preferences = MessagesController.getNotificationsSettings(this.currentAccount);
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("inlinegeo_");
                stringBuilder.append(this.foundContextBot.id);
                if (preferences.getBoolean(stringBuilder.toString(), false) || this.parentFragment == null || this.parentFragment.getParentActivity() == null) {
                    checkLocationPermissionsOrStart();
                } else {
                    final User foundContextBotFinal = this.foundContextBot;
                    Builder builder = new Builder(this.parentFragment.getParentActivity());
                    builder.setTitle(LocaleController.getString("ShareYouLocationTitle", R.string.ShareYouLocationTitle));
                    builder.setMessage(LocaleController.getString("ShareYouLocationInline", R.string.ShareYouLocationInline));
                    final boolean[] buttonClicked = new boolean[1];
                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            buttonClicked[0] = true;
                            if (foundContextBotFinal != null) {
                                Editor edit = MessagesController.getNotificationsSettings(MentionsAdapter.this.currentAccount).edit();
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append("inlinegeo_");
                                stringBuilder.append(foundContextBotFinal.id);
                                edit.putBoolean(stringBuilder.toString(), true).commit();
                                MentionsAdapter.this.checkLocationPermissionsOrStart();
                            }
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), new OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            buttonClicked[0] = true;
                            MentionsAdapter.this.onLocationUnavailable();
                        }
                    });
                    this.parentFragment.showDialog(builder.create(), new OnDismissListener() {
                        public void onDismiss(DialogInterface dialog) {
                            if (!buttonClicked[0]) {
                                MentionsAdapter.this.onLocationUnavailable();
                            }
                        }
                    });
                }
            }
        }
        if (this.foundContextBot == null) {
            this.noUserName = true;
        } else {
            if (this.delegate != null) {
                this.delegate.onContextSearch(true);
            }
            searchForContextBotResults(true, this.foundContextBot, this.searchingContextQuery, TtmlNode.ANONYMOUS_REGION_ID);
        }
    }

    private void searchForContextBot(String username, String query) {
        if (this.foundContextBot == null || this.foundContextBot.username == null || !this.foundContextBot.username.equals(username) || this.searchingContextQuery == null || !this.searchingContextQuery.equals(query)) {
            this.searchResultBotContext = null;
            this.searchResultBotContextById = null;
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
                if (username != null) {
                    if (username.length() == 0) {
                    }
                }
                return;
            }
            if (query == null) {
                if (this.contextQueryReqid != 0) {
                    ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.contextQueryReqid, true);
                    this.contextQueryReqid = 0;
                }
                this.searchingContextQuery = null;
                if (this.delegate != null) {
                    this.delegate.onContextSearch(false);
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
            MessagesController messagesController = MessagesController.getInstance(this.currentAccount);
            MessagesStorage messagesStorage = MessagesStorage.getInstance(this.currentAccount);
            this.searchingContextQuery = query;
            final String str = query;
            final String str2 = username;
            final MessagesController messagesController2 = messagesController;
            final MessagesStorage messagesStorage2 = messagesStorage;
            this.contextQueryRunnable = new Runnable() {

                /* renamed from: org.telegram.ui.Adapters.MentionsAdapter$7$1 */
                class C18901 implements RequestDelegate {
                    C18901() {
                    }

                    public void run(final TLObject response, final TL_error error) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                if (MentionsAdapter.this.searchingContextUsername != null) {
                                    if (MentionsAdapter.this.searchingContextUsername.equals(str2)) {
                                        User user = null;
                                        if (error == null) {
                                            TL_contacts_resolvedPeer res = response;
                                            if (!res.users.isEmpty()) {
                                                user = (User) res.users.get(0);
                                                messagesController2.putUser(user, false);
                                                messagesStorage2.putUsersAndChats(res.users, null, true, true);
                                            }
                                        }
                                        MentionsAdapter.this.processFoundUser(user);
                                    }
                                }
                            }
                        });
                    }
                }

                public void run() {
                    if (MentionsAdapter.this.contextQueryRunnable == this) {
                        MentionsAdapter.this.contextQueryRunnable = null;
                        if (MentionsAdapter.this.foundContextBot == null) {
                            if (!MentionsAdapter.this.noUserName) {
                                MentionsAdapter.this.searchingContextUsername = str2;
                                TLObject object = messagesController2.getUserOrChat(MentionsAdapter.this.searchingContextUsername);
                                if (object instanceof User) {
                                    MentionsAdapter.this.processFoundUser((User) object);
                                } else {
                                    TL_contacts_resolveUsername req = new TL_contacts_resolveUsername();
                                    req.username = MentionsAdapter.this.searchingContextUsername;
                                    MentionsAdapter.this.contextUsernameReqid = ConnectionsManager.getInstance(MentionsAdapter.this.currentAccount).sendRequest(req, new C18901());
                                }
                            }
                        }
                        if (!MentionsAdapter.this.noUserName) {
                            MentionsAdapter.this.searchForContextBotResults(true, MentionsAdapter.this.foundContextBot, str, TtmlNode.ANONYMOUS_REGION_ID);
                        }
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
        if (this.parentFragment != null) {
            if (this.parentFragment.getParentActivity() != null) {
                if (VERSION.SDK_INT < 23 || this.parentFragment.getParentActivity().checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == 0) {
                    if (this.foundContextBot != null && this.foundContextBot.bot_inline_geo) {
                        this.locationProvider.start();
                    }
                    return;
                }
                this.parentFragment.getParentActivity().requestPermissions(new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}, 2);
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
        if (!(this.contextQueryReqid != 0 || this.nextQueryOffset == null || this.nextQueryOffset.length() == 0 || this.foundContextBot == null)) {
            if (this.searchingContextQuery != null) {
                searchForContextBotResults(true, this.foundContextBot, this.searchingContextQuery, this.nextQueryOffset);
            }
        }
    }

    private void searchForContextBotResults(boolean cache, User user, String query, String offset) {
        User user2 = user;
        String str = query;
        String str2 = offset;
        if (this.contextQueryReqid != 0) {
            ConnectionsManager.getInstance(r8.currentAccount).cancelRequest(r8.contextQueryReqid, true);
            r8.contextQueryReqid = 0;
        }
        if (r8.inlineMediaEnabled) {
            if (str != null) {
                if (user2 != null) {
                    if (!user2.bot_inline_geo || r8.lastKnownLocation != null) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(r8.dialog_id);
                        stringBuilder.append("_");
                        stringBuilder.append(str);
                        stringBuilder.append("_");
                        stringBuilder.append(str2);
                        stringBuilder.append("_");
                        stringBuilder.append(r8.dialog_id);
                        stringBuilder.append("_");
                        stringBuilder.append(user2.id);
                        stringBuilder.append("_");
                        Object valueOf = (!user2.bot_inline_geo || r8.lastKnownLocation == null || r8.lastKnownLocation.getLatitude() == -1000.0d) ? TtmlNode.ANONYMOUS_REGION_ID : Double.valueOf(r8.lastKnownLocation.getLatitude() + r8.lastKnownLocation.getLongitude());
                        stringBuilder.append(valueOf);
                        String key = stringBuilder.toString();
                        MessagesStorage messagesStorage = MessagesStorage.getInstance(r8.currentAccount);
                        final String str3 = str;
                        final boolean z = cache;
                        final User user3 = user2;
                        final String str4 = str2;
                        final MessagesStorage messagesStorage2 = messagesStorage;
                        MessagesStorage messagesStorage3 = messagesStorage;
                        final String messagesStorage4 = key;
                        C18918 requestDelegate = new RequestDelegate() {
                            public void run(final TLObject response, TL_error error) {
                                AndroidUtilities.runOnUIThread(new Runnable() {
                                    public void run() {
                                        if (MentionsAdapter.this.searchingContextQuery != null) {
                                            if (str3.equals(MentionsAdapter.this.searchingContextQuery)) {
                                                boolean z = false;
                                                MentionsAdapter.this.contextQueryReqid = 0;
                                                if (z && response == null) {
                                                    MentionsAdapter.this.searchForContextBotResults(false, user3, str3, str4);
                                                } else if (MentionsAdapter.this.delegate != null) {
                                                    MentionsAdapter.this.delegate.onContextSearch(false);
                                                }
                                                if (response != null) {
                                                    boolean hasTop;
                                                    MentionsAdapterDelegate access$1700;
                                                    TL_messages_botResults res = response;
                                                    if (!(z || res.cache_time == 0)) {
                                                        messagesStorage2.saveBotCache(messagesStorage4, res);
                                                    }
                                                    MentionsAdapter.this.nextQueryOffset = res.next_offset;
                                                    if (MentionsAdapter.this.searchResultBotContextById == null) {
                                                        MentionsAdapter.this.searchResultBotContextById = new HashMap();
                                                        MentionsAdapter.this.searchResultBotContextSwitch = res.switch_pm;
                                                    }
                                                    int a = 0;
                                                    while (a < res.results.size()) {
                                                        BotInlineResult result = (BotInlineResult) res.results.get(a);
                                                        if (MentionsAdapter.this.searchResultBotContextById.containsKey(result.id) || (!(result.document instanceof TL_document) && !(result.photo instanceof TL_photo) && result.content == null && (result.send_message instanceof TL_botInlineMessageMediaAuto))) {
                                                            res.results.remove(a);
                                                            a--;
                                                        }
                                                        result.query_id = res.query_id;
                                                        MentionsAdapter.this.searchResultBotContextById.put(result.id, result);
                                                        a++;
                                                    }
                                                    boolean added = false;
                                                    if (MentionsAdapter.this.searchResultBotContext != null) {
                                                        if (str4.length() != 0) {
                                                            added = true;
                                                            MentionsAdapter.this.searchResultBotContext.addAll(res.results);
                                                            if (res.results.isEmpty()) {
                                                                MentionsAdapter.this.nextQueryOffset = TtmlNode.ANONYMOUS_REGION_ID;
                                                            }
                                                            MentionsAdapter.this.searchResultHashtags = null;
                                                            MentionsAdapter.this.searchResultUsernames = null;
                                                            MentionsAdapter.this.searchResultUsernamesMap = null;
                                                            MentionsAdapter.this.searchResultCommands = null;
                                                            MentionsAdapter.this.searchResultSuggestions = null;
                                                            MentionsAdapter.this.searchResultCommandsHelp = null;
                                                            MentionsAdapter.this.searchResultCommandsUsers = null;
                                                            if (added) {
                                                                MentionsAdapter.this.notifyDataSetChanged();
                                                            } else {
                                                                hasTop = MentionsAdapter.this.searchResultBotContextSwitch == null;
                                                                MentionsAdapter.this.notifyItemChanged(((MentionsAdapter.this.searchResultBotContext.size() - res.results.size()) + (hasTop ? 1 : 0)) - 1);
                                                                MentionsAdapter.this.notifyItemRangeInserted((MentionsAdapter.this.searchResultBotContext.size() - res.results.size()) + (hasTop ? 1 : 0), res.results.size());
                                                            }
                                                            access$1700 = MentionsAdapter.this.delegate;
                                                            if (MentionsAdapter.this.searchResultBotContext.isEmpty()) {
                                                                if (MentionsAdapter.this.searchResultBotContextSwitch != null) {
                                                                    access$1700.needChangePanelVisibility(z);
                                                                }
                                                            }
                                                            z = true;
                                                            access$1700.needChangePanelVisibility(z);
                                                        }
                                                    }
                                                    MentionsAdapter.this.searchResultBotContext = res.results;
                                                    MentionsAdapter.this.contextMedia = res.gallery;
                                                    MentionsAdapter.this.searchResultHashtags = null;
                                                    MentionsAdapter.this.searchResultUsernames = null;
                                                    MentionsAdapter.this.searchResultUsernamesMap = null;
                                                    MentionsAdapter.this.searchResultCommands = null;
                                                    MentionsAdapter.this.searchResultSuggestions = null;
                                                    MentionsAdapter.this.searchResultCommandsHelp = null;
                                                    MentionsAdapter.this.searchResultCommandsUsers = null;
                                                    if (added) {
                                                        MentionsAdapter.this.notifyDataSetChanged();
                                                    } else {
                                                        if (MentionsAdapter.this.searchResultBotContextSwitch == null) {
                                                        }
                                                        if (hasTop) {
                                                        }
                                                        MentionsAdapter.this.notifyItemChanged(((MentionsAdapter.this.searchResultBotContext.size() - res.results.size()) + (hasTop ? 1 : 0)) - 1);
                                                        if (hasTop) {
                                                        }
                                                        MentionsAdapter.this.notifyItemRangeInserted((MentionsAdapter.this.searchResultBotContext.size() - res.results.size()) + (hasTop ? 1 : 0), res.results.size());
                                                    }
                                                    access$1700 = MentionsAdapter.this.delegate;
                                                    if (MentionsAdapter.this.searchResultBotContext.isEmpty()) {
                                                        if (MentionsAdapter.this.searchResultBotContextSwitch != null) {
                                                            access$1700.needChangePanelVisibility(z);
                                                        }
                                                    }
                                                    z = true;
                                                    access$1700.needChangePanelVisibility(z);
                                                }
                                            }
                                        }
                                    }
                                });
                            }
                        };
                        if (cache) {
                            messagesStorage3.getBotCache(key, requestDelegate);
                        } else {
                            TL_messages_getInlineBotResults req = new TL_messages_getInlineBotResults();
                            req.bot = MessagesController.getInstance(r8.currentAccount).getInputUser(user2);
                            req.query = str;
                            req.offset = str2;
                            if (!(!user2.bot_inline_geo || r8.lastKnownLocation == null || r8.lastKnownLocation.getLatitude() == -1000.0d)) {
                                req.flags |= 1;
                                req.geo_point = new TL_inputGeoPoint();
                                req.geo_point.lat = r8.lastKnownLocation.getLatitude();
                                req.geo_point._long = r8.lastKnownLocation.getLongitude();
                            }
                            int lower_id = (int) r8.dialog_id;
                            int high_id = (int) (r8.dialog_id >> 32);
                            if (lower_id != 0) {
                                req.peer = MessagesController.getInstance(r8.currentAccount).getInputPeer(lower_id);
                            } else {
                                req.peer = new TL_inputPeerEmpty();
                            }
                            r8.contextQueryReqid = ConnectionsManager.getInstance(r8.currentAccount).sendRequest(req, requestDelegate, 2);
                        }
                        return;
                    }
                    return;
                }
            }
            r8.searchingContextQuery = null;
            return;
        }
        if (r8.delegate != null) {
            r8.delegate.onContextSearch(false);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void searchUsernameOrHashtag(String text, int position, ArrayList<MessageObject> messageObjects, boolean usernameOnly) {
        String str = text;
        int i = position;
        ArrayList<MessageObject> arrayList = messageObjects;
        boolean z = usernameOnly;
        if (this.channelReqId != 0) {
            ConnectionsManager.getInstance(r0.currentAccount).cancelRequest(r0.channelReqId, true);
            r0.channelReqId = 0;
        }
        if (r0.searchGlobalRunnable != null) {
            AndroidUtilities.cancelRunOnUIThread(r0.searchGlobalRunnable);
            r0.searchGlobalRunnable = null;
        }
        if (TextUtils.isEmpty(text)) {
            searchForContextBot(null, null);
            r0.delegate.needChangePanelVisibility(false);
            r0.lastText = null;
            return;
        }
        int index;
        int searchPostion = i;
        if (text.length() > 0) {
            searchPostion--;
        }
        r0.lastText = null;
        r0.lastUsernameOnly = z;
        StringBuilder result = new StringBuilder();
        int foundType = -1;
        char c = '0';
        if (!z && r0.needBotContext && str.charAt(0) == '@') {
            index = str.indexOf(32);
            int len = text.length();
            String username = null;
            String query = null;
            if (index > 0) {
                username = str.substring(1, index);
                query = str.substring(index + 1);
            } else if (str.charAt(len - 1) == 't' && str.charAt(len - 2) == 'o' && str.charAt(len - 3) == 'b') {
                username = str.substring(1);
                query = TtmlNode.ANONYMOUS_REGION_ID;
            } else {
                searchForContextBot(null, null);
            }
            String username2 = username;
            String query2 = query;
            if (username2 == null || username2.length() < 1) {
                username2 = TtmlNode.ANONYMOUS_REGION_ID;
            } else {
                int a = 1;
                while (a < username2.length()) {
                    char ch = username2.charAt(a);
                    if ((ch < c || ch > '9') && ((ch < 'a' || ch > 'z') && ((ch < 'A' || ch > 'Z') && ch != '_'))) {
                        username2 = TtmlNode.ANONYMOUS_REGION_ID;
                        break;
                    } else {
                        a++;
                        c = '0';
                    }
                }
            }
            searchForContextBot(username2, query2);
        } else {
            searchForContextBot(null, null);
        }
        if (r0.foundContextBot == null) {
            boolean hasIllegalUsernameCharacters;
            final MessagesController messagesController = MessagesController.getInstance(r0.currentAccount);
            int dogPostion = -1;
            if (z) {
                result.append(str.substring(1));
                r0.resultStartPosition = 0;
                r0.resultLength = result.length();
                foundType = 0;
                hasIllegalUsernameCharacters = false;
            } else {
                hasIllegalUsernameCharacters = false;
                int a2 = searchPostion;
                while (a2 >= 0) {
                    if (a2 < text.length()) {
                        char ch2 = str.charAt(a2);
                        if (!(a2 == 0 || str.charAt(a2 - 1) == ' ')) {
                            if (str.charAt(a2 - 1) != '\n') {
                                if (ch2 >= '0') {
                                    if (ch2 >= 'a') {
                                        if (ch2 > 'z') {
                                        }
                                    }
                                    if (ch2 >= 'A') {
                                        if (ch2 > 'Z') {
                                        }
                                    }
                                    if (ch2 != '_') {
                                        hasIllegalUsernameCharacters = true;
                                    }
                                    result.insert(0, ch2);
                                } else if (ch2 > '9') {
                                    if (ch2 >= 'a') {
                                        if (ch2 > 'z') {
                                        }
                                    }
                                    if (ch2 >= 'A') {
                                        if (ch2 > 'Z') {
                                        }
                                    }
                                    if (ch2 != '_') {
                                        hasIllegalUsernameCharacters = true;
                                    }
                                    result.insert(0, ch2);
                                }
                                result.insert(0, ch2);
                            }
                        }
                        if (ch2 != '@') {
                            if (ch2 != '#') {
                                if (a2 == 0 && r0.botInfo != null && ch2 == '/') {
                                    foundType = 2;
                                    r0.resultStartPosition = a2;
                                    r0.resultLength = result.length() + 1;
                                    break;
                                }
                                if (ch2 == ':' && result.length() > 0) {
                                    foundType = 3;
                                    r0.resultStartPosition = a2;
                                    r0.resultLength = result.length() + 1;
                                    break;
                                }
                                if (ch2 >= '0') {
                                    if (ch2 >= 'a') {
                                        if (ch2 > 'z') {
                                        }
                                    }
                                    if (ch2 >= 'A') {
                                        if (ch2 > 'Z') {
                                        }
                                    }
                                    if (ch2 != '_') {
                                        hasIllegalUsernameCharacters = true;
                                    }
                                    result.insert(0, ch2);
                                } else if (ch2 > '9') {
                                    if (ch2 >= 'a') {
                                        if (ch2 > 'z') {
                                        }
                                    }
                                    if (ch2 >= 'A') {
                                        if (ch2 > 'Z') {
                                        }
                                    }
                                    if (ch2 != '_') {
                                        hasIllegalUsernameCharacters = true;
                                    }
                                    result.insert(0, ch2);
                                }
                                result.insert(0, ch2);
                            } else if (r0.searchAdapterHelper.loadRecentHashtags()) {
                                foundType = 1;
                                r0.resultStartPosition = a2;
                                r0.resultLength = result.length() + 1;
                                result.insert(0, ch2);
                            } else {
                                r0.lastText = str;
                                r0.lastPosition = i;
                                r0.messages = arrayList;
                                r0.delegate.needChangePanelVisibility(false);
                                return;
                            }
                        }
                        if (r0.needUsernames || (r0.needBotContext && a2 == 0)) {
                            if (r0.info != null || a2 == 0) {
                                dogPostion = a2;
                                foundType = 0;
                                r0.resultStartPosition = a2;
                                r0.resultLength = result.length() + 1;
                            } else {
                                r0.lastText = str;
                                r0.lastPosition = i;
                                r0.messages = arrayList;
                                r0.delegate.needChangePanelVisibility(false);
                                return;
                            }
                        }
                        if (ch2 >= '0') {
                            if (ch2 >= 'a') {
                                if (ch2 > 'z') {
                                }
                            }
                            if (ch2 >= 'A') {
                                if (ch2 > 'Z') {
                                }
                            }
                            if (ch2 != '_') {
                                hasIllegalUsernameCharacters = true;
                            }
                            result.insert(0, ch2);
                        } else if (ch2 > '9') {
                            if (ch2 >= 'a') {
                                if (ch2 > 'z') {
                                }
                            }
                            if (ch2 >= 'A') {
                                if (ch2 > 'Z') {
                                }
                            }
                            if (ch2 != '_') {
                                hasIllegalUsernameCharacters = true;
                            }
                            result.insert(0, ch2);
                        }
                        result.insert(0, ch2);
                    }
                    a2--;
                }
            }
            if (foundType == -1) {
                r0.delegate.needChangePanelVisibility(false);
                return;
            }
            int i2;
            int a3;
            if (foundType == 0) {
                int a4;
                Chat chat;
                Runnable c07879;
                ChatParticipant searchPostion2;
                User user;
                ChatParticipant chatParticipant;
                final ArrayList<Integer> users = new ArrayList();
                for (index = 0; index < Math.min(100, messageObjects.size()); index++) {
                    int from_id = ((MessageObject) arrayList.get(index)).messageOwner.from_id;
                    if (!users.contains(Integer.valueOf(from_id))) {
                        users.add(Integer.valueOf(from_id));
                    }
                }
                final String usernameString = result.toString().toLowerCase();
                boolean hasSpace = usernameString.indexOf(32) >= 0;
                ArrayList<User> newResult = new ArrayList();
                final SparseArray<User> newResultsHashMap = new SparseArray();
                SparseArray<User> newMap = new SparseArray();
                ArrayList<TL_topPeer> inlineBots = DataQuery.getInstance(r0.currentAccount).inlineBots;
                int searchPostion3;
                if (!z && r0.needBotContext && dogPostion == 0 && !inlineBots.isEmpty()) {
                    int count = 0;
                    a4 = 0;
                    while (true) {
                        searchPostion3 = searchPostion;
                        if (a4 >= inlineBots.size()) {
                            break;
                        }
                        ArrayList<TL_topPeer> inlineBots2;
                        searchPostion = messagesController.getUser(Integer.valueOf(((TL_topPeer) inlineBots.get(a4)).peer.user_id));
                        if (searchPostion == 0) {
                            inlineBots2 = inlineBots;
                        } else {
                            inlineBots2 = inlineBots;
                            if (searchPostion.username != null && searchPostion.username.length() > null && ((usernameString.length() > null && searchPostion.username.toLowerCase().startsWith(usernameString) != null) || usernameString.length() == null)) {
                                newResult.add(searchPostion);
                                newResultsHashMap.put(searchPostion.id, searchPostion);
                                count++;
                            }
                            inlineBots = count;
                            User user2 = searchPostion;
                            if (inlineBots == 5) {
                                break;
                            }
                            count = inlineBots;
                        }
                        a4++;
                        searchPostion = searchPostion3;
                        inlineBots = inlineBots2;
                    }
                } else {
                    searchPostion3 = searchPostion;
                }
                if (r0.parentFragment != null) {
                    chat = r0.parentFragment.getCurrentChat();
                } else if (r0.info != null) {
                    chat = messagesController.getChat(Integer.valueOf(r0.info.id));
                } else {
                    chat = null;
                    if (chat != null && r0.info != null && r0.info.participants != null) {
                        if (ChatObject.isChannel(chat)) {
                            if (chat.megagroup) {
                                i2 = dogPostion;
                                r0.searchResultHashtags = null;
                                r0.searchResultCommands = null;
                                r0.searchResultCommandsHelp = null;
                                r0.searchResultCommandsUsers = null;
                                r0.searchResultSuggestions = null;
                                r0.searchResultUsernames = newResult;
                                r0.searchResultUsernamesMap = newMap;
                                if (chat != null && chat.megagroup && usernameString.length() > 0) {
                                    c07879 = new Runnable() {
                                        public void run() {
                                            if (MentionsAdapter.this.searchGlobalRunnable == this) {
                                                TL_channels_getParticipants req = new TL_channels_getParticipants();
                                                req.channel = MessagesController.getInputChannel(chat);
                                                req.limit = 20;
                                                req.offset = 0;
                                                req.filter = new TL_channelParticipantsSearch();
                                                req.filter.f32q = usernameString;
                                                final int currentReqId = MentionsAdapter.access$3104(MentionsAdapter.this);
                                                MentionsAdapter.this.channelReqId = ConnectionsManager.getInstance(MentionsAdapter.this.currentAccount).sendRequest(req, new RequestDelegate() {
                                                    public void run(final TLObject response, final TL_error error) {
                                                        AndroidUtilities.runOnUIThread(new Runnable() {
                                                            public void run() {
                                                                if (!(MentionsAdapter.this.channelReqId == 0 || currentReqId != MentionsAdapter.this.channelLastReqId || MentionsAdapter.this.searchResultUsernamesMap == null || MentionsAdapter.this.searchResultUsernames == null || error != null)) {
                                                                    TL_channels_channelParticipants res = response;
                                                                    messagesController.putUsers(res.users, false);
                                                                    if (!res.participants.isEmpty()) {
                                                                        int currentUserId = UserConfig.getInstance(MentionsAdapter.this.currentAccount).getClientUserId();
                                                                        for (int a = 0; a < res.participants.size(); a++) {
                                                                            ChannelParticipant participant = (ChannelParticipant) res.participants.get(a);
                                                                            if (MentionsAdapter.this.searchResultUsernamesMap.indexOfKey(participant.user_id) < 0) {
                                                                                if (MentionsAdapter.this.isSearchingMentions || participant.user_id != currentUserId) {
                                                                                    User user = messagesController.getUser(Integer.valueOf(participant.user_id));
                                                                                    if (user != null) {
                                                                                        MentionsAdapter.this.searchResultUsernames.add(user);
                                                                                    } else {
                                                                                        return;
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                        MentionsAdapter.this.notifyDataSetChanged();
                                                                    }
                                                                }
                                                                MentionsAdapter.this.channelReqId = 0;
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        }
                                    };
                                    r0.searchGlobalRunnable = c07879;
                                    AndroidUtilities.runOnUIThread(c07879, 200);
                                }
                                Collections.sort(r0.searchResultUsernames, new Comparator<User>() {
                                    public int compare(User lhs, User rhs) {
                                        int i = 0;
                                        if (newResultsHashMap.indexOfKey(lhs.id) >= 0 && newResultsHashMap.indexOfKey(rhs.id) >= 0) {
                                            return 0;
                                        }
                                        if (newResultsHashMap.indexOfKey(lhs.id) >= 0) {
                                            return -1;
                                        }
                                        if (newResultsHashMap.indexOfKey(rhs.id) >= 0) {
                                            return 1;
                                        }
                                        int lhsNum = users.indexOf(Integer.valueOf(lhs.id));
                                        int rhsNum = users.indexOf(Integer.valueOf(rhs.id));
                                        if (lhsNum != -1 && rhsNum != -1) {
                                            if (lhsNum < rhsNum) {
                                                i = -1;
                                            } else if (lhsNum != rhsNum) {
                                                i = 1;
                                            }
                                            return i;
                                        } else if (lhsNum != -1 && rhsNum == -1) {
                                            return -1;
                                        } else {
                                            if (lhsNum != -1 || rhsNum == -1) {
                                                return 0;
                                            }
                                            return 1;
                                        }
                                    }
                                });
                                notifyDataSetChanged();
                                r0.delegate.needChangePanelVisibility(newResult.isEmpty() ^ true);
                            }
                        }
                        a3 = 0;
                        while (true) {
                            a4 = a3;
                            if (a4 < r0.info.participants.participants.size()) {
                                break;
                            }
                            searchPostion2 = (ChatParticipant) r0.info.participants.participants.get(a4);
                            i2 = dogPostion;
                            user = messagesController.getUser(Integer.valueOf(searchPostion2.user_id));
                            if (user != null && (r4 || !UserObject.isUserSelf(user))) {
                                if (newResultsHashMap.indexOfKey(user.id) >= 0) {
                                    if (usernameString.length() != 0) {
                                        if (!user.deleted) {
                                            newResult.add(user);
                                        }
                                    } else if (user.username == null && user.username.length() > 0 && user.username.toLowerCase().startsWith(usernameString)) {
                                        newResult.add(user);
                                        newMap.put(user.id, user);
                                    } else if (user.first_name == null && user.first_name.length() > 0 && user.first_name.toLowerCase().startsWith(usernameString)) {
                                        newResult.add(user);
                                        newMap.put(user.id, user);
                                    } else if (user.last_name == null && user.last_name.length() > 0 && user.last_name.toLowerCase().startsWith(usernameString)) {
                                        newResult.add(user);
                                        newMap.put(user.id, user);
                                    } else if (hasSpace) {
                                        chatParticipant = searchPostion2;
                                        if (ContactsController.formatName(user.first_name, user.last_name).toLowerCase().startsWith(usernameString)) {
                                            newResult.add(user);
                                            newMap.put(user.id, user);
                                        }
                                    }
                                }
                            }
                            a3 = a4 + 1;
                            dogPostion = i2;
                            z = usernameOnly;
                        }
                    }
                    r0.searchResultHashtags = null;
                    r0.searchResultCommands = null;
                    r0.searchResultCommandsHelp = null;
                    r0.searchResultCommandsUsers = null;
                    r0.searchResultSuggestions = null;
                    r0.searchResultUsernames = newResult;
                    r0.searchResultUsernamesMap = newMap;
                    c07879 = /* anonymous class already generated */;
                    r0.searchGlobalRunnable = c07879;
                    AndroidUtilities.runOnUIThread(c07879, 200);
                    Collections.sort(r0.searchResultUsernames, /* anonymous class already generated */);
                    notifyDataSetChanged();
                    r0.delegate.needChangePanelVisibility(newResult.isEmpty() ^ true);
                }
                if (ChatObject.isChannel(chat)) {
                    if (chat.megagroup) {
                        i2 = dogPostion;
                        r0.searchResultHashtags = null;
                        r0.searchResultCommands = null;
                        r0.searchResultCommandsHelp = null;
                        r0.searchResultCommandsUsers = null;
                        r0.searchResultSuggestions = null;
                        r0.searchResultUsernames = newResult;
                        r0.searchResultUsernamesMap = newMap;
                        c07879 = /* anonymous class already generated */;
                        r0.searchGlobalRunnable = c07879;
                        AndroidUtilities.runOnUIThread(c07879, 200);
                        Collections.sort(r0.searchResultUsernames, /* anonymous class already generated */);
                        notifyDataSetChanged();
                        r0.delegate.needChangePanelVisibility(newResult.isEmpty() ^ true);
                    }
                }
                a3 = 0;
                while (true) {
                    a4 = a3;
                    if (a4 < r0.info.participants.participants.size()) {
                        break;
                    }
                    searchPostion2 = (ChatParticipant) r0.info.participants.participants.get(a4);
                    i2 = dogPostion;
                    user = messagesController.getUser(Integer.valueOf(searchPostion2.user_id));
                    if (newResultsHashMap.indexOfKey(user.id) >= 0) {
                        if (usernameString.length() != 0) {
                            if (user.username == null) {
                            }
                            if (user.first_name == null) {
                            }
                            if (user.last_name == null) {
                            }
                            if (hasSpace) {
                                chatParticipant = searchPostion2;
                                if (ContactsController.formatName(user.first_name, user.last_name).toLowerCase().startsWith(usernameString)) {
                                    newResult.add(user);
                                    newMap.put(user.id, user);
                                }
                            }
                        } else if (!user.deleted) {
                            newResult.add(user);
                        }
                    }
                    a3 = a4 + 1;
                    dogPostion = i2;
                    z = usernameOnly;
                }
                r0.searchResultHashtags = null;
                r0.searchResultCommands = null;
                r0.searchResultCommandsHelp = null;
                r0.searchResultCommandsUsers = null;
                r0.searchResultSuggestions = null;
                r0.searchResultUsernames = newResult;
                r0.searchResultUsernamesMap = newMap;
                c07879 = /* anonymous class already generated */;
                r0.searchGlobalRunnable = c07879;
                AndroidUtilities.runOnUIThread(c07879, 200);
                Collections.sort(r0.searchResultUsernames, /* anonymous class already generated */);
                notifyDataSetChanged();
                r0.delegate.needChangePanelVisibility(newResult.isEmpty() ^ true);
            } else {
                i2 = dogPostion;
                ArrayList<String> newResult2;
                if (foundType == 1) {
                    newResult2 = new ArrayList();
                    String hashtagString = result.toString().toLowerCase();
                    ArrayList<HashtagObject> hashtags = r0.searchAdapterHelper.getHashtags();
                    a3 = 0;
                    while (true) {
                        int a5 = a3;
                        if (a5 >= hashtags.size()) {
                            break;
                        }
                        HashtagObject hashtagObject = (HashtagObject) hashtags.get(a5);
                        if (!(hashtagObject == null || hashtagObject.hashtag == null || !hashtagObject.hashtag.startsWith(hashtagString))) {
                            newResult2.add(hashtagObject.hashtag);
                        }
                        a3 = a5 + 1;
                    }
                    r0.searchResultHashtags = newResult2;
                    r0.searchResultUsernames = null;
                    r0.searchResultUsernamesMap = null;
                    r0.searchResultCommands = null;
                    r0.searchResultCommandsHelp = null;
                    r0.searchResultCommandsUsers = null;
                    r0.searchResultSuggestions = null;
                    notifyDataSetChanged();
                    r0.delegate.needChangePanelVisibility(newResult2.isEmpty() ^ true);
                } else if (foundType == 2) {
                    newResult2 = new ArrayList();
                    ArrayList<String> newResultHelp = new ArrayList();
                    ArrayList<User> newResultUsers = new ArrayList();
                    String command = result.toString().toLowerCase();
                    for (searchPostion = 0; searchPostion < r0.botInfo.size(); searchPostion++) {
                        BotInfo info = (BotInfo) r0.botInfo.valueAt(searchPostion);
                        for (int a6 = 0; a6 < info.commands.size(); a6++) {
                            TL_botCommand botCommand = (TL_botCommand) info.commands.get(a6);
                            if (!(botCommand == null || botCommand.command == null || !botCommand.command.startsWith(command))) {
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append("/");
                                stringBuilder.append(botCommand.command);
                                newResult2.add(stringBuilder.toString());
                                newResultHelp.add(botCommand.description);
                                newResultUsers.add(messagesController.getUser(Integer.valueOf(info.user_id)));
                            }
                        }
                    }
                    r0.searchResultHashtags = null;
                    r0.searchResultUsernames = null;
                    r0.searchResultUsernamesMap = null;
                    r0.searchResultSuggestions = null;
                    r0.searchResultCommands = newResult2;
                    r0.searchResultCommandsHelp = newResultHelp;
                    r0.searchResultCommandsUsers = newResultUsers;
                    notifyDataSetChanged();
                    r0.delegate.needChangePanelVisibility(newResult2.isEmpty() ^ true);
                } else {
                    boolean z2 = true;
                    if (foundType == 3) {
                        if (hasIllegalUsernameCharacters) {
                            r0.delegate.needChangePanelVisibility(false);
                        } else {
                            Object[] suggestions = Emoji.getSuggestion(result.toString());
                            if (suggestions != null) {
                                r0.searchResultSuggestions = new ArrayList();
                                for (EmojiSuggestion suggestion : suggestions) {
                                    suggestion.emoji = suggestion.emoji.replace("\ufe0f", TtmlNode.ANONYMOUS_REGION_ID);
                                    r0.searchResultSuggestions.add(suggestion);
                                }
                                Emoji.loadRecentEmoji();
                                Collections.sort(r0.searchResultSuggestions, new Comparator<EmojiSuggestion>() {
                                    public int compare(EmojiSuggestion o1, EmojiSuggestion o2) {
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
                                });
                            }
                            r0.searchResultHashtags = null;
                            r0.searchResultUsernames = null;
                            r0.searchResultUsernamesMap = null;
                            r0.searchResultCommands = null;
                            r0.searchResultCommandsHelp = null;
                            r0.searchResultCommandsUsers = null;
                            notifyDataSetChanged();
                            MentionsAdapterDelegate mentionsAdapterDelegate = r0.delegate;
                            if (r0.searchResultSuggestions == null) {
                                z2 = false;
                            }
                            mentionsAdapterDelegate.needChangePanelVisibility(z2);
                        }
                    }
                }
            }
        }
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
            return size + i;
        } else if (this.searchResultUsernames != null) {
            return this.searchResultUsernames.size();
        } else {
            if (this.searchResultHashtags != null) {
                return this.searchResultHashtags.size();
            }
            if (this.searchResultCommands != null) {
                return this.searchResultCommands.size();
            }
            if (this.searchResultSuggestions != null) {
                return this.searchResultSuggestions.size();
            }
            return 0;
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
            if (i >= 0) {
                if (i < this.searchResultBotContext.size()) {
                    return this.searchResultBotContext.get(i);
                }
            }
            return null;
        } else if (this.searchResultUsernames != null) {
            if (i >= 0) {
                if (i < this.searchResultUsernames.size()) {
                    return this.searchResultUsernames.get(i);
                }
            }
            return null;
        } else if (this.searchResultHashtags != null) {
            if (i >= 0) {
                if (i < this.searchResultHashtags.size()) {
                    return this.searchResultHashtags.get(i);
                }
            }
            return null;
        } else if (this.searchResultSuggestions != null) {
            if (i >= 0) {
                if (i < this.searchResultSuggestions.size()) {
                    return this.searchResultSuggestions.get(i);
                }
            }
            return null;
        } else {
            if (this.searchResultCommands != null && i >= 0) {
                if (i < this.searchResultCommands.size()) {
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
            return null;
        }
    }

    public boolean isLongClickEnabled() {
        if (this.searchResultHashtags == null) {
            if (this.searchResultCommands == null) {
                return false;
            }
        }
        return true;
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
        if (this.foundContextBot != null) {
            if (!this.inlineMediaEnabled) {
                return false;
            }
        }
        return true;
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
                ((ContextLinkCell) view).setDelegate(new ContextLinkCellDelegate() {
                    public void didPressedImage(ContextLinkCell cell) {
                        MentionsAdapter.this.delegate.onContextClick(cell.getResult());
                    }
                });
                break;
            case 2:
                view = new BotSwitchCell(this.mContext);
                break;
            default:
                view = new TextView(this.mContext);
                view.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
                view.setTextSize(1, 14.0f);
                view.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
                break;
        }
        return new Holder(view);
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        boolean z = false;
        if (holder.getItemViewType() == 3) {
            TextView textView = holder.itemView;
            Chat chat = this.parentFragment.getCurrentChat();
            if (chat != null) {
                if (AndroidUtilities.isBannedForever(chat.banned_rights.until_date)) {
                    textView.setText(LocaleController.getString("AttachInlineRestrictedForever", R.string.AttachInlineRestrictedForever));
                } else {
                    textView.setText(LocaleController.formatString("AttachInlineRestricted", R.string.AttachInlineRestricted, LocaleController.formatDateForBan((long) chat.banned_rights.until_date)));
                }
            }
        } else if (this.searchResultBotContext != null) {
            boolean hasTop = this.searchResultBotContextSwitch != null;
            if (holder.getItemViewType() != 2) {
                if (hasTop) {
                    position--;
                }
                ContextLinkCell contextLinkCell = (ContextLinkCell) holder.itemView;
                BotInlineResult botInlineResult = (BotInlineResult) this.searchResultBotContext.get(position);
                boolean z2 = this.contextMedia;
                boolean z3 = position != this.searchResultBotContext.size() - 1;
                if (hasTop && position == 0) {
                    z = true;
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
