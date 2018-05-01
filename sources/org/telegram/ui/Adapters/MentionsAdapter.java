package org.telegram.ui.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
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
import org.telegram.messenger.C0446R;
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
    private LocationProvider locationProvider = new LocationProvider(new C18931()) {
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
    class C18931 implements LocationProviderDelegate {
        C18931() {
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
    class C18953 implements SearchAdapterHelperDelegate {
        C18953() {
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

    static /* synthetic */ int access$3104(MentionsAdapter mentionsAdapter) {
        int i = mentionsAdapter.channelLastReqId + 1;
        mentionsAdapter.channelLastReqId = i;
        return i;
    }

    public MentionsAdapter(Context context, boolean z, long j, MentionsAdapterDelegate mentionsAdapterDelegate) {
        this.mContext = context;
        this.delegate = mentionsAdapterDelegate;
        this.isDarkTheme = z;
        this.dialog_id = j;
        this.searchAdapterHelper = new SearchAdapterHelper(true);
        this.searchAdapterHelper.setDelegate(new C18953());
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

    public void setParentFragment(ChatActivity chatActivity) {
        this.parentFragment = chatActivity;
    }

    public void setChatInfo(ChatFull chatFull) {
        this.currentAccount = UserConfig.selectedAccount;
        this.info = chatFull;
        if (!(this.inlineMediaEnabled != null || this.foundContextBot == null || this.parentFragment == null)) {
            chatFull = this.parentFragment.getCurrentChat();
            if (chatFull != null) {
                this.inlineMediaEnabled = ChatObject.canSendStickers(chatFull);
                if (this.inlineMediaEnabled != null) {
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

    public void setNeedUsernames(boolean z) {
        this.needUsernames = z;
    }

    public void setNeedBotContext(boolean z) {
        this.needBotContext = z;
    }

    public void setBotInfo(SparseArray<BotInfo> sparseArray) {
        this.botInfo = sparseArray;
    }

    public void setBotsCount(int i) {
        this.botsCount = i;
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
                user = this.parentFragment.getCurrentChat();
                if (user != null) {
                    this.inlineMediaEnabled = ChatObject.canSendStickers(user);
                    if (this.inlineMediaEnabled == null) {
                        notifyDataSetChanged();
                        this.delegate.needChangePanelVisibility(true);
                        return;
                    }
                }
            }
            if (this.foundContextBot.bot_inline_geo != null) {
                user = MessagesController.getNotificationsSettings(this.currentAccount);
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("inlinegeo_");
                stringBuilder.append(this.foundContextBot.id);
                if (user.getBoolean(stringBuilder.toString(), false) != null || this.parentFragment == null || this.parentFragment.getParentActivity() == null) {
                    checkLocationPermissionsOrStart();
                } else {
                    user = this.foundContextBot;
                    Builder builder = new Builder(this.parentFragment.getParentActivity());
                    builder.setTitle(LocaleController.getString("ShareYouLocationTitle", C0446R.string.ShareYouLocationTitle));
                    builder.setMessage(LocaleController.getString("ShareYouLocationInline", C0446R.string.ShareYouLocationInline));
                    final boolean[] zArr = new boolean[1];
                    builder.setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), new OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            zArr[0] = 1;
                            if (user != null) {
                                dialogInterface = MessagesController.getNotificationsSettings(MentionsAdapter.this.currentAccount).edit();
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append("inlinegeo_");
                                stringBuilder.append(user.id);
                                dialogInterface.putBoolean(stringBuilder.toString(), true).commit();
                                MentionsAdapter.this.checkLocationPermissionsOrStart();
                            }
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString("Cancel", C0446R.string.Cancel), new OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            zArr[0] = true;
                            MentionsAdapter.this.onLocationUnavailable();
                        }
                    });
                    this.parentFragment.showDialog(builder.create(), new OnDismissListener() {
                        public void onDismiss(DialogInterface dialogInterface) {
                            if (zArr[0] == null) {
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

    private void searchForContextBot(String str, String str2) {
        if (this.foundContextBot == null || this.foundContextBot.username == null || !this.foundContextBot.username.equals(str) || this.searchingContextQuery == null || !this.searchingContextQuery.equals(str2)) {
            this.searchResultBotContext = null;
            this.searchResultBotContextById = null;
            this.searchResultBotContextSwitch = null;
            notifyDataSetChanged();
            if (this.foundContextBot != null) {
                if (this.inlineMediaEnabled || str == null || str2 == null) {
                    this.delegate.needChangePanelVisibility(false);
                } else {
                    return;
                }
            }
            if (this.contextQueryRunnable != null) {
                AndroidUtilities.cancelRunOnUIThread(this.contextQueryRunnable);
                this.contextQueryRunnable = null;
            }
            if (TextUtils.isEmpty(str) || !(this.searchingContextUsername == null || this.searchingContextUsername.equals(str))) {
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
                if (str != null) {
                    if (str.length() == 0) {
                    }
                }
                return;
            }
            if (str2 == null) {
                if (this.contextQueryReqid != null) {
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
                } else if (str.equals("gif")) {
                    this.searchingContextUsername = "gif";
                    this.delegate.onContextSearch(false);
                }
            }
            final MessagesController instance = MessagesController.getInstance(this.currentAccount);
            final MessagesStorage instance2 = MessagesStorage.getInstance(this.currentAccount);
            this.searchingContextQuery = str2;
            final String str3 = str2;
            final String str4 = str;
            this.contextQueryRunnable = new Runnable() {

                /* renamed from: org.telegram.ui.Adapters.MentionsAdapter$7$1 */
                class C18961 implements RequestDelegate {
                    C18961() {
                    }

                    public void run(final TLObject tLObject, final TL_error tL_error) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                if (MentionsAdapter.this.searchingContextUsername != null) {
                                    if (MentionsAdapter.this.searchingContextUsername.equals(str4)) {
                                        User user = null;
                                        if (tL_error == null) {
                                            TL_contacts_resolvedPeer tL_contacts_resolvedPeer = (TL_contacts_resolvedPeer) tLObject;
                                            if (!tL_contacts_resolvedPeer.users.isEmpty()) {
                                                User user2 = (User) tL_contacts_resolvedPeer.users.get(0);
                                                instance.putUser(user2, false);
                                                instance2.putUsersAndChats(tL_contacts_resolvedPeer.users, null, true, true);
                                                user = user2;
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
                                MentionsAdapter.this.searchingContextUsername = str4;
                                TLObject userOrChat = instance.getUserOrChat(MentionsAdapter.this.searchingContextUsername);
                                if (userOrChat instanceof User) {
                                    MentionsAdapter.this.processFoundUser((User) userOrChat);
                                } else {
                                    userOrChat = new TL_contacts_resolveUsername();
                                    userOrChat.username = MentionsAdapter.this.searchingContextUsername;
                                    MentionsAdapter.this.contextUsernameReqid = ConnectionsManager.getInstance(MentionsAdapter.this.currentAccount).sendRequest(userOrChat, new C18961());
                                }
                            }
                        }
                        if (!MentionsAdapter.this.noUserName) {
                            MentionsAdapter.this.searchForContextBotResults(true, MentionsAdapter.this.foundContextBot, str3, TtmlNode.ANONYMOUS_REGION_ID);
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

    public void setSearchingMentions(boolean z) {
        this.isSearchingMentions = z;
    }

    public String getBotCaption() {
        if (this.foundContextBot != null) {
            return this.foundContextBot.bot_inline_placeholder;
        }
        return (this.searchingContextUsername == null || !this.searchingContextUsername.equals("gif")) ? null : "Search GIFs";
    }

    public void searchForContextBotForNextOffset() {
        if (!(this.contextQueryReqid != 0 || this.nextQueryOffset == null || this.nextQueryOffset.length() == 0 || this.foundContextBot == null)) {
            if (this.searchingContextQuery != null) {
                searchForContextBotResults(true, this.foundContextBot, this.searchingContextQuery, this.nextQueryOffset);
            }
        }
    }

    private void searchForContextBotResults(boolean z, User user, String str, String str2) {
        User user2 = user;
        String str3 = str;
        String str4 = str2;
        if (this.contextQueryReqid != 0) {
            ConnectionsManager.getInstance(r8.currentAccount).cancelRequest(r8.contextQueryReqid, true);
            r8.contextQueryReqid = 0;
        }
        if (r8.inlineMediaEnabled) {
            if (str3 != null) {
                if (user2 != null) {
                    if (!user2.bot_inline_geo || r8.lastKnownLocation != null) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(r8.dialog_id);
                        stringBuilder.append("_");
                        stringBuilder.append(str3);
                        stringBuilder.append("_");
                        stringBuilder.append(str4);
                        stringBuilder.append("_");
                        stringBuilder.append(r8.dialog_id);
                        stringBuilder.append("_");
                        stringBuilder.append(user2.id);
                        stringBuilder.append("_");
                        Object valueOf = (!user2.bot_inline_geo || r8.lastKnownLocation == null || r8.lastKnownLocation.getLatitude() == -1000.0d) ? TtmlNode.ANONYMOUS_REGION_ID : Double.valueOf(r8.lastKnownLocation.getLatitude() + r8.lastKnownLocation.getLongitude());
                        stringBuilder.append(valueOf);
                        String stringBuilder2 = stringBuilder.toString();
                        MessagesStorage instance = MessagesStorage.getInstance(r8.currentAccount);
                        final String str5 = str3;
                        final boolean z2 = z;
                        final User user3 = user2;
                        final String str6 = str4;
                        C18978 c18978 = r0;
                        final MessagesStorage messagesStorage = instance;
                        MessagesStorage messagesStorage2 = instance;
                        final String str7 = stringBuilder2;
                        C18978 c189782 = new RequestDelegate() {
                            public void run(final TLObject tLObject, TL_error tL_error) {
                                AndroidUtilities.runOnUIThread(new Runnable() {
                                    public void run() {
                                        if (MentionsAdapter.this.searchingContextQuery != null) {
                                            if (str5.equals(MentionsAdapter.this.searchingContextQuery)) {
                                                boolean z = false;
                                                MentionsAdapter.this.contextQueryReqid = 0;
                                                if (z2 && tLObject == null) {
                                                    MentionsAdapter.this.searchForContextBotResults(false, user3, str5, str6);
                                                } else if (MentionsAdapter.this.delegate != null) {
                                                    MentionsAdapter.this.delegate.onContextSearch(false);
                                                }
                                                if (tLObject != null) {
                                                    MentionsAdapterDelegate access$1700;
                                                    TL_messages_botResults tL_messages_botResults = (TL_messages_botResults) tLObject;
                                                    if (!(z2 || tL_messages_botResults.cache_time == 0)) {
                                                        messagesStorage.saveBotCache(str7, tL_messages_botResults);
                                                    }
                                                    MentionsAdapter.this.nextQueryOffset = tL_messages_botResults.next_offset;
                                                    if (MentionsAdapter.this.searchResultBotContextById == null) {
                                                        MentionsAdapter.this.searchResultBotContextById = new HashMap();
                                                        MentionsAdapter.this.searchResultBotContextSwitch = tL_messages_botResults.switch_pm;
                                                    }
                                                    int i = 0;
                                                    while (i < tL_messages_botResults.results.size()) {
                                                        BotInlineResult botInlineResult = (BotInlineResult) tL_messages_botResults.results.get(i);
                                                        if (MentionsAdapter.this.searchResultBotContextById.containsKey(botInlineResult.id) || (!(botInlineResult.document instanceof TL_document) && !(botInlineResult.photo instanceof TL_photo) && botInlineResult.content == null && (botInlineResult.send_message instanceof TL_botInlineMessageMediaAuto))) {
                                                            tL_messages_botResults.results.remove(i);
                                                            i--;
                                                        }
                                                        botInlineResult.query_id = tL_messages_botResults.query_id;
                                                        MentionsAdapter.this.searchResultBotContextById.put(botInlineResult.id, botInlineResult);
                                                        i++;
                                                    }
                                                    if (MentionsAdapter.this.searchResultBotContext != null) {
                                                        if (str6.length() != 0) {
                                                            MentionsAdapter.this.searchResultBotContext.addAll(tL_messages_botResults.results);
                                                            if (tL_messages_botResults.results.isEmpty()) {
                                                                MentionsAdapter.this.nextQueryOffset = TtmlNode.ANONYMOUS_REGION_ID;
                                                            }
                                                            i = 1;
                                                            MentionsAdapter.this.searchResultHashtags = null;
                                                            MentionsAdapter.this.searchResultUsernames = null;
                                                            MentionsAdapter.this.searchResultUsernamesMap = null;
                                                            MentionsAdapter.this.searchResultCommands = null;
                                                            MentionsAdapter.this.searchResultSuggestions = null;
                                                            MentionsAdapter.this.searchResultCommandsHelp = null;
                                                            MentionsAdapter.this.searchResultCommandsUsers = null;
                                                            if (i == 0) {
                                                                i = MentionsAdapter.this.searchResultBotContextSwitch == null ? 1 : 0;
                                                                MentionsAdapter.this.notifyItemChanged(((MentionsAdapter.this.searchResultBotContext.size() - tL_messages_botResults.results.size()) + i) - 1);
                                                                MentionsAdapter.this.notifyItemRangeInserted((MentionsAdapter.this.searchResultBotContext.size() - tL_messages_botResults.results.size()) + i, tL_messages_botResults.results.size());
                                                            } else {
                                                                MentionsAdapter.this.notifyDataSetChanged();
                                                            }
                                                            access$1700 = MentionsAdapter.this.delegate;
                                                            if (!(MentionsAdapter.this.searchResultBotContext.isEmpty() && MentionsAdapter.this.searchResultBotContextSwitch == null)) {
                                                                z = true;
                                                            }
                                                            access$1700.needChangePanelVisibility(z);
                                                        }
                                                    }
                                                    MentionsAdapter.this.searchResultBotContext = tL_messages_botResults.results;
                                                    MentionsAdapter.this.contextMedia = tL_messages_botResults.gallery;
                                                    i = false;
                                                    MentionsAdapter.this.searchResultHashtags = null;
                                                    MentionsAdapter.this.searchResultUsernames = null;
                                                    MentionsAdapter.this.searchResultUsernamesMap = null;
                                                    MentionsAdapter.this.searchResultCommands = null;
                                                    MentionsAdapter.this.searchResultSuggestions = null;
                                                    MentionsAdapter.this.searchResultCommandsHelp = null;
                                                    MentionsAdapter.this.searchResultCommandsUsers = null;
                                                    if (i == 0) {
                                                        MentionsAdapter.this.notifyDataSetChanged();
                                                    } else {
                                                        if (MentionsAdapter.this.searchResultBotContextSwitch == null) {
                                                        }
                                                        MentionsAdapter.this.notifyItemChanged(((MentionsAdapter.this.searchResultBotContext.size() - tL_messages_botResults.results.size()) + i) - 1);
                                                        MentionsAdapter.this.notifyItemRangeInserted((MentionsAdapter.this.searchResultBotContext.size() - tL_messages_botResults.results.size()) + i, tL_messages_botResults.results.size());
                                                    }
                                                    access$1700 = MentionsAdapter.this.delegate;
                                                    z = true;
                                                    access$1700.needChangePanelVisibility(z);
                                                }
                                            }
                                        }
                                    }
                                });
                            }
                        };
                        if (z) {
                            messagesStorage2.getBotCache(stringBuilder2, c18978);
                        } else {
                            TLObject tL_messages_getInlineBotResults = new TL_messages_getInlineBotResults();
                            tL_messages_getInlineBotResults.bot = MessagesController.getInstance(r8.currentAccount).getInputUser(user2);
                            tL_messages_getInlineBotResults.query = str3;
                            tL_messages_getInlineBotResults.offset = str4;
                            if (!(!user2.bot_inline_geo || r8.lastKnownLocation == null || r8.lastKnownLocation.getLatitude() == -1000.0d)) {
                                tL_messages_getInlineBotResults.flags |= 1;
                                tL_messages_getInlineBotResults.geo_point = new TL_inputGeoPoint();
                                tL_messages_getInlineBotResults.geo_point.lat = r8.lastKnownLocation.getLatitude();
                                tL_messages_getInlineBotResults.geo_point._long = r8.lastKnownLocation.getLongitude();
                            }
                            int i = (int) r8.dialog_id;
                            long j = r8.dialog_id;
                            if (i != 0) {
                                tL_messages_getInlineBotResults.peer = MessagesController.getInstance(r8.currentAccount).getInputPeer(i);
                            } else {
                                tL_messages_getInlineBotResults.peer = new TL_inputPeerEmpty();
                            }
                            r8.contextQueryReqid = ConnectionsManager.getInstance(r8.currentAccount).sendRequest(tL_messages_getInlineBotResults, c18978, 2);
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

    public void searchUsernameOrHashtag(String str, int i, ArrayList<MessageObject> arrayList, boolean z) {
        String str2 = str;
        int i2 = i;
        ArrayList<MessageObject> arrayList2 = arrayList;
        boolean z2 = z;
        boolean z3 = false;
        if (this.channelReqId != 0) {
            ConnectionsManager.getInstance(r0.currentAccount).cancelRequest(r0.channelReqId, true);
            r0.channelReqId = 0;
        }
        if (r0.searchGlobalRunnable != null) {
            AndroidUtilities.cancelRunOnUIThread(r0.searchGlobalRunnable);
            r0.searchGlobalRunnable = null;
        }
        if (TextUtils.isEmpty(str)) {
            searchForContextBot(null, null);
            r0.delegate.needChangePanelVisibility(false);
            r0.lastText = null;
            return;
        }
        int i3;
        int i4 = str.length() > 0 ? i2 - 1 : i2;
        r0.lastText = null;
        r0.lastUsernameOnly = z2;
        StringBuilder stringBuilder = new StringBuilder();
        char c = '9';
        if (!z2 && r0.needBotContext && str2.charAt(0) == '@') {
            String substring;
            String str3;
            int indexOf = str2.indexOf(32);
            int length = str.length();
            if (indexOf > 0) {
                String substring2 = str2.substring(1, indexOf);
                substring = str2.substring(indexOf + 1);
                str3 = substring2;
            } else if (str2.charAt(length - 1) == 't' && str2.charAt(length - 2) == 'o' && str2.charAt(length - 3) == 'b') {
                str3 = str2.substring(1);
                substring = TtmlNode.ANONYMOUS_REGION_ID;
            } else {
                searchForContextBot(null, null);
                str3 = null;
                substring = str3;
            }
            if (str3 == null || str3.length() < 1) {
                str3 = TtmlNode.ANONYMOUS_REGION_ID;
            } else {
                i3 = 1;
                while (i3 < str3.length()) {
                    char charAt = str3.charAt(i3);
                    if ((charAt < '0' || charAt > r15) && ((charAt < 'a' || charAt > 'z') && ((charAt < 'A' || charAt > 'Z') && charAt != '_'))) {
                        str3 = TtmlNode.ANONYMOUS_REGION_ID;
                        break;
                    } else {
                        i3++;
                        c = '9';
                    }
                }
            }
            searchForContextBot(str3, substring);
        } else {
            searchForContextBot(null, null);
        }
        if (r0.foundContextBot == null) {
            boolean z4;
            boolean z5;
            int i5;
            boolean z6;
            final ArrayList arrayList3;
            final String toLowerCase;
            boolean z7;
            ArrayList arrayList4;
            final SparseArray sparseArray;
            SparseArray sparseArray2;
            ArrayList arrayList5;
            int i6;
            User user;
            Chat currentChat;
            int i7;
            User user2;
            Runnable c07949;
            ArrayList hashtags;
            HashtagObject hashtagObject;
            ArrayList arrayList6;
            String toLowerCase2;
            BotInfo botInfo;
            int i8;
            TL_botCommand tL_botCommand;
            StringBuilder stringBuilder2;
            Object[] suggestion;
            EmojiSuggestion emojiSuggestion;
            MentionsAdapterDelegate mentionsAdapterDelegate;
            final MessagesController instance = MessagesController.getInstance(r0.currentAccount);
            if (z2) {
                stringBuilder.append(str2.substring(1));
                r0.resultStartPosition = 0;
                r0.resultLength = stringBuilder.length();
                z4 = false;
                z5 = z4;
            } else {
                z5 = false;
                while (i4 >= 0) {
                    if (i4 < str.length()) {
                        c = str2.charAt(i4);
                        if (i4 != 0) {
                            i3 = i4 - 1;
                            if (str2.charAt(i3) != ' ') {
                                if (str2.charAt(i3) != '\n') {
                                    if (c >= '0') {
                                        if (c > '9') {
                                            stringBuilder.insert(0, c);
                                        }
                                    }
                                    if (c >= 'a') {
                                        if (c > 'z') {
                                        }
                                        stringBuilder.insert(0, c);
                                    }
                                    if (c < 'A' || c > 'Z') {
                                        if (c != '_') {
                                            z5 = true;
                                        }
                                        stringBuilder.insert(0, c);
                                    }
                                    stringBuilder.insert(0, c);
                                }
                            }
                        }
                        if (c != '@') {
                            if (c != '#') {
                                if (i4 != 0 || r0.botInfo == null || c != '/') {
                                    if (c == ':' && stringBuilder.length() > 0) {
                                        r0.resultStartPosition = i4;
                                        r0.resultLength = stringBuilder.length() + 1;
                                        z4 = true;
                                        break;
                                    }
                                }
                                r0.resultStartPosition = i4;
                                r0.resultLength = stringBuilder.length() + 1;
                                z4 = true;
                                break;
                            } else if (r0.searchAdapterHelper.loadRecentHashtags()) {
                                r0.resultStartPosition = i4;
                                r0.resultLength = stringBuilder.length() + 1;
                                stringBuilder.insert(0, c);
                                z4 = true;
                            } else {
                                r0.lastText = str2;
                                r0.lastPosition = i2;
                                r0.messages = arrayList2;
                                r0.delegate.needChangePanelVisibility(false);
                                return;
                            }
                        } else if (r0.needUsernames || (r0.needBotContext && i4 == 0)) {
                            if (r0.info != null || i4 == 0) {
                                r0.resultStartPosition = i4;
                                r0.resultLength = stringBuilder.length() + 1;
                                i5 = i4;
                                z4 = false;
                                z6 = true;
                                if (z4 != z6) {
                                    r0.delegate.needChangePanelVisibility(false);
                                }
                                if (!z4) {
                                    arrayList3 = new ArrayList();
                                    for (i2 = 0; i2 < Math.min(100, arrayList.size()); i2++) {
                                        i4 = ((MessageObject) arrayList2.get(i2)).messageOwner.from_id;
                                        if (!arrayList3.contains(Integer.valueOf(i4))) {
                                            arrayList3.add(Integer.valueOf(i4));
                                        }
                                    }
                                    toLowerCase = stringBuilder.toString().toLowerCase();
                                    z7 = toLowerCase.indexOf(32) < 0;
                                    arrayList4 = new ArrayList();
                                    sparseArray = new SparseArray();
                                    sparseArray2 = new SparseArray();
                                    arrayList5 = DataQuery.getInstance(r0.currentAccount).inlineBots;
                                    if (!z2 && r0.needBotContext && r17 == 0 && !arrayList5.isEmpty()) {
                                        i3 = 0;
                                        i6 = i3;
                                        while (i3 < arrayList5.size()) {
                                            user = instance.getUser(Integer.valueOf(((TL_topPeer) arrayList5.get(i3)).peer.user_id));
                                            if (user == null) {
                                                if (user.username != null && user.username.length() > 0 && ((toLowerCase.length() > 0 && user.username.toLowerCase().startsWith(toLowerCase)) || toLowerCase.length() == 0)) {
                                                    arrayList4.add(user);
                                                    sparseArray.put(user.id, user);
                                                    i6++;
                                                }
                                                if (i6 == 5) {
                                                    break;
                                                }
                                            }
                                            i3++;
                                        }
                                    }
                                    currentChat = r0.parentFragment == null ? r0.parentFragment.getCurrentChat() : r0.info == null ? instance.getChat(Integer.valueOf(r0.info.id)) : null;
                                    if (!(currentChat == null || r0.info == null || r0.info.participants == null || (ChatObject.isChannel(currentChat) && !currentChat.megagroup))) {
                                        while (i7 < r0.info.participants.participants.size()) {
                                            user2 = instance.getUser(Integer.valueOf(((ChatParticipant) r0.info.participants.participants.get(i7)).user_id));
                                            if (user2 != null && (z2 || !UserObject.isUserSelf(user2))) {
                                                if (sparseArray.indexOfKey(user2.id) >= 0) {
                                                    if (toLowerCase.length() != 0) {
                                                        if (!user2.deleted) {
                                                            arrayList4.add(user2);
                                                        }
                                                    } else if (user2.username == null && user2.username.length() > 0 && user2.username.toLowerCase().startsWith(toLowerCase)) {
                                                        arrayList4.add(user2);
                                                        sparseArray2.put(user2.id, user2);
                                                    } else if (user2.first_name == null && user2.first_name.length() > 0 && user2.first_name.toLowerCase().startsWith(toLowerCase)) {
                                                        arrayList4.add(user2);
                                                        sparseArray2.put(user2.id, user2);
                                                    } else if (user2.last_name == null && user2.last_name.length() > 0 && user2.last_name.toLowerCase().startsWith(toLowerCase)) {
                                                        arrayList4.add(user2);
                                                        sparseArray2.put(user2.id, user2);
                                                    } else if (z7 && ContactsController.formatName(user2.first_name, user2.last_name).toLowerCase().startsWith(toLowerCase)) {
                                                        arrayList4.add(user2);
                                                        sparseArray2.put(user2.id, user2);
                                                    }
                                                }
                                            }
                                            i7++;
                                        }
                                    }
                                    r0.searchResultHashtags = null;
                                    r0.searchResultCommands = null;
                                    r0.searchResultCommandsHelp = null;
                                    r0.searchResultCommandsUsers = null;
                                    r0.searchResultSuggestions = null;
                                    r0.searchResultUsernames = arrayList4;
                                    r0.searchResultUsernamesMap = sparseArray2;
                                    if (currentChat != null && currentChat.megagroup && toLowerCase.length() > 0) {
                                        c07949 = new Runnable() {
                                            public void run() {
                                                if (MentionsAdapter.this.searchGlobalRunnable == this) {
                                                    TLObject tL_channels_getParticipants = new TL_channels_getParticipants();
                                                    tL_channels_getParticipants.channel = MessagesController.getInputChannel(currentChat);
                                                    tL_channels_getParticipants.limit = 20;
                                                    tL_channels_getParticipants.offset = 0;
                                                    tL_channels_getParticipants.filter = new TL_channelParticipantsSearch();
                                                    tL_channels_getParticipants.filter.f32q = toLowerCase;
                                                    final int access$3104 = MentionsAdapter.access$3104(MentionsAdapter.this);
                                                    MentionsAdapter.this.channelReqId = ConnectionsManager.getInstance(MentionsAdapter.this.currentAccount).sendRequest(tL_channels_getParticipants, new RequestDelegate() {
                                                        public void run(final TLObject tLObject, final TL_error tL_error) {
                                                            AndroidUtilities.runOnUIThread(new Runnable() {
                                                                public void run() {
                                                                    if (!(MentionsAdapter.this.channelReqId == 0 || access$3104 != MentionsAdapter.this.channelLastReqId || MentionsAdapter.this.searchResultUsernamesMap == null || MentionsAdapter.this.searchResultUsernames == null || tL_error != null)) {
                                                                        TL_channels_channelParticipants tL_channels_channelParticipants = (TL_channels_channelParticipants) tLObject;
                                                                        instance.putUsers(tL_channels_channelParticipants.users, false);
                                                                        if (!tL_channels_channelParticipants.participants.isEmpty()) {
                                                                            int clientUserId = UserConfig.getInstance(MentionsAdapter.this.currentAccount).getClientUserId();
                                                                            for (int i = 0; i < tL_channels_channelParticipants.participants.size(); i++) {
                                                                                ChannelParticipant channelParticipant = (ChannelParticipant) tL_channels_channelParticipants.participants.get(i);
                                                                                if (MentionsAdapter.this.searchResultUsernamesMap.indexOfKey(channelParticipant.user_id) < 0) {
                                                                                    if (MentionsAdapter.this.isSearchingMentions || channelParticipant.user_id != clientUserId) {
                                                                                        User user = instance.getUser(Integer.valueOf(channelParticipant.user_id));
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
                                        r0.searchGlobalRunnable = c07949;
                                        AndroidUtilities.runOnUIThread(c07949, 200);
                                    }
                                    Collections.sort(r0.searchResultUsernames, new Comparator<User>() {
                                        public int compare(User user, User user2) {
                                            int i = 0;
                                            if (sparseArray.indexOfKey(user.id) >= 0 && sparseArray.indexOfKey(user2.id) >= 0) {
                                                return 0;
                                            }
                                            if (sparseArray.indexOfKey(user.id) >= 0) {
                                                return -1;
                                            }
                                            if (sparseArray.indexOfKey(user2.id) >= 0) {
                                                return 1;
                                            }
                                            user = arrayList3.indexOf(Integer.valueOf(user.id));
                                            user2 = arrayList3.indexOf(Integer.valueOf(user2.id));
                                            if (user != -1 && user2 != -1) {
                                                if (user < user2) {
                                                    i = -1;
                                                } else if (user != user2) {
                                                    i = 1;
                                                }
                                                return i;
                                            } else if (user != -1 && user2 == -1) {
                                                return -1;
                                            } else {
                                                if (user != -1 || user2 == -1) {
                                                    return 0;
                                                }
                                                return 1;
                                            }
                                        }
                                    });
                                    notifyDataSetChanged();
                                    r0.delegate.needChangePanelVisibility(arrayList4.isEmpty() ^ true);
                                } else if (z4) {
                                    arrayList3 = new ArrayList();
                                    toLowerCase = stringBuilder.toString().toLowerCase();
                                    hashtags = r0.searchAdapterHelper.getHashtags();
                                    while (i7 < hashtags.size()) {
                                        hashtagObject = (HashtagObject) hashtags.get(i7);
                                        if (!(hashtagObject == null || hashtagObject.hashtag == null || !hashtagObject.hashtag.startsWith(toLowerCase))) {
                                            arrayList3.add(hashtagObject.hashtag);
                                        }
                                        i7++;
                                    }
                                    r0.searchResultHashtags = arrayList3;
                                    r0.searchResultUsernames = null;
                                    r0.searchResultUsernamesMap = null;
                                    r0.searchResultCommands = null;
                                    r0.searchResultCommandsHelp = null;
                                    r0.searchResultCommandsUsers = null;
                                    r0.searchResultSuggestions = null;
                                    notifyDataSetChanged();
                                    r0.delegate.needChangePanelVisibility(arrayList3.isEmpty() ^ true);
                                } else if (z4) {
                                    arrayList3 = new ArrayList();
                                    arrayList6 = new ArrayList();
                                    hashtags = new ArrayList();
                                    toLowerCase2 = stringBuilder.toString().toLowerCase();
                                    for (i4 = 0; i4 < r0.botInfo.size(); i4++) {
                                        botInfo = (BotInfo) r0.botInfo.valueAt(i4);
                                        for (i8 = 0; i8 < botInfo.commands.size(); i8++) {
                                            tL_botCommand = (TL_botCommand) botInfo.commands.get(i8);
                                            if (!(tL_botCommand == null || tL_botCommand.command == null || !tL_botCommand.command.startsWith(toLowerCase2))) {
                                                stringBuilder2 = new StringBuilder();
                                                stringBuilder2.append("/");
                                                stringBuilder2.append(tL_botCommand.command);
                                                arrayList3.add(stringBuilder2.toString());
                                                arrayList6.add(tL_botCommand.description);
                                                hashtags.add(instance.getUser(Integer.valueOf(botInfo.user_id)));
                                            }
                                        }
                                    }
                                    r0.searchResultHashtags = null;
                                    r0.searchResultUsernames = null;
                                    r0.searchResultUsernamesMap = null;
                                    r0.searchResultSuggestions = null;
                                    r0.searchResultCommands = arrayList3;
                                    r0.searchResultCommandsHelp = arrayList6;
                                    r0.searchResultCommandsUsers = hashtags;
                                    notifyDataSetChanged();
                                    r0.delegate.needChangePanelVisibility(arrayList3.isEmpty() ^ true);
                                } else if (z4) {
                                    if (z5) {
                                        suggestion = Emoji.getSuggestion(stringBuilder.toString());
                                        if (suggestion != null) {
                                            r0.searchResultSuggestions = new ArrayList();
                                            for (Object obj : suggestion) {
                                                emojiSuggestion = (EmojiSuggestion) obj;
                                                emojiSuggestion.emoji = emojiSuggestion.emoji.replace("\ufe0f", TtmlNode.ANONYMOUS_REGION_ID);
                                                r0.searchResultSuggestions.add(emojiSuggestion);
                                            }
                                            Emoji.loadRecentEmoji();
                                            Collections.sort(r0.searchResultSuggestions, new Comparator<EmojiSuggestion>() {
                                                public int compare(EmojiSuggestion emojiSuggestion, EmojiSuggestion emojiSuggestion2) {
                                                    emojiSuggestion = (Integer) Emoji.emojiUseHistory.get(emojiSuggestion.emoji);
                                                    if (emojiSuggestion == null) {
                                                        emojiSuggestion = Integer.valueOf(0);
                                                    }
                                                    emojiSuggestion2 = (Integer) Emoji.emojiUseHistory.get(emojiSuggestion2.emoji);
                                                    if (emojiSuggestion2 == null) {
                                                        emojiSuggestion2 = Integer.valueOf(0);
                                                    }
                                                    return emojiSuggestion2.compareTo(emojiSuggestion);
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
                                        mentionsAdapterDelegate = r0.delegate;
                                        if (r0.searchResultSuggestions != null) {
                                            z3 = true;
                                        }
                                        mentionsAdapterDelegate.needChangePanelVisibility(z3);
                                    } else {
                                        r0.delegate.needChangePanelVisibility(false);
                                    }
                                }
                                return;
                            }
                            r0.lastText = str2;
                            r0.lastPosition = i2;
                            r0.messages = arrayList2;
                            r0.delegate.needChangePanelVisibility(false);
                            return;
                        }
                        if (c >= '0') {
                            if (c > '9') {
                                stringBuilder.insert(0, c);
                            }
                        }
                        if (c >= 'a') {
                            if (c > 'z') {
                            }
                            stringBuilder.insert(0, c);
                        }
                        if (c != '_') {
                            z5 = true;
                        }
                        stringBuilder.insert(0, c);
                    }
                    i4--;
                }
                z4 = true;
            }
            z6 = true;
            i5 = -1;
            if (z4 != z6) {
                if (!z4) {
                    arrayList3 = new ArrayList();
                    for (i2 = 0; i2 < Math.min(100, arrayList.size()); i2++) {
                        i4 = ((MessageObject) arrayList2.get(i2)).messageOwner.from_id;
                        if (!arrayList3.contains(Integer.valueOf(i4))) {
                            arrayList3.add(Integer.valueOf(i4));
                        }
                    }
                    toLowerCase = stringBuilder.toString().toLowerCase();
                    if (toLowerCase.indexOf(32) < 0) {
                    }
                    arrayList4 = new ArrayList();
                    sparseArray = new SparseArray();
                    sparseArray2 = new SparseArray();
                    arrayList5 = DataQuery.getInstance(r0.currentAccount).inlineBots;
                    i3 = 0;
                    i6 = i3;
                    while (i3 < arrayList5.size()) {
                        user = instance.getUser(Integer.valueOf(((TL_topPeer) arrayList5.get(i3)).peer.user_id));
                        if (user == null) {
                            arrayList4.add(user);
                            sparseArray.put(user.id, user);
                            i6++;
                            if (i6 == 5) {
                                break;
                            }
                        }
                        i3++;
                    }
                    if (r0.parentFragment == null) {
                        if (r0.info == null) {
                        }
                    }
                    while (i7 < r0.info.participants.participants.size()) {
                        user2 = instance.getUser(Integer.valueOf(((ChatParticipant) r0.info.participants.participants.get(i7)).user_id));
                        if (sparseArray.indexOfKey(user2.id) >= 0) {
                            if (toLowerCase.length() != 0) {
                                if (user2.username == null) {
                                }
                                if (user2.first_name == null) {
                                }
                                if (user2.last_name == null) {
                                }
                                arrayList4.add(user2);
                                sparseArray2.put(user2.id, user2);
                            } else if (!user2.deleted) {
                                arrayList4.add(user2);
                            }
                        }
                        i7++;
                    }
                    r0.searchResultHashtags = null;
                    r0.searchResultCommands = null;
                    r0.searchResultCommandsHelp = null;
                    r0.searchResultCommandsUsers = null;
                    r0.searchResultSuggestions = null;
                    r0.searchResultUsernames = arrayList4;
                    r0.searchResultUsernamesMap = sparseArray2;
                    c07949 = /* anonymous class already generated */;
                    r0.searchGlobalRunnable = c07949;
                    AndroidUtilities.runOnUIThread(c07949, 200);
                    Collections.sort(r0.searchResultUsernames, /* anonymous class already generated */);
                    notifyDataSetChanged();
                    r0.delegate.needChangePanelVisibility(arrayList4.isEmpty() ^ true);
                } else if (z4) {
                    arrayList3 = new ArrayList();
                    toLowerCase = stringBuilder.toString().toLowerCase();
                    hashtags = r0.searchAdapterHelper.getHashtags();
                    while (i7 < hashtags.size()) {
                        hashtagObject = (HashtagObject) hashtags.get(i7);
                        arrayList3.add(hashtagObject.hashtag);
                        i7++;
                    }
                    r0.searchResultHashtags = arrayList3;
                    r0.searchResultUsernames = null;
                    r0.searchResultUsernamesMap = null;
                    r0.searchResultCommands = null;
                    r0.searchResultCommandsHelp = null;
                    r0.searchResultCommandsUsers = null;
                    r0.searchResultSuggestions = null;
                    notifyDataSetChanged();
                    r0.delegate.needChangePanelVisibility(arrayList3.isEmpty() ^ true);
                } else if (z4) {
                    arrayList3 = new ArrayList();
                    arrayList6 = new ArrayList();
                    hashtags = new ArrayList();
                    toLowerCase2 = stringBuilder.toString().toLowerCase();
                    for (i4 = 0; i4 < r0.botInfo.size(); i4++) {
                        botInfo = (BotInfo) r0.botInfo.valueAt(i4);
                        for (i8 = 0; i8 < botInfo.commands.size(); i8++) {
                            tL_botCommand = (TL_botCommand) botInfo.commands.get(i8);
                            stringBuilder2 = new StringBuilder();
                            stringBuilder2.append("/");
                            stringBuilder2.append(tL_botCommand.command);
                            arrayList3.add(stringBuilder2.toString());
                            arrayList6.add(tL_botCommand.description);
                            hashtags.add(instance.getUser(Integer.valueOf(botInfo.user_id)));
                        }
                    }
                    r0.searchResultHashtags = null;
                    r0.searchResultUsernames = null;
                    r0.searchResultUsernamesMap = null;
                    r0.searchResultSuggestions = null;
                    r0.searchResultCommands = arrayList3;
                    r0.searchResultCommandsHelp = arrayList6;
                    r0.searchResultCommandsUsers = hashtags;
                    notifyDataSetChanged();
                    r0.delegate.needChangePanelVisibility(arrayList3.isEmpty() ^ true);
                } else if (z4) {
                    if (z5) {
                        r0.delegate.needChangePanelVisibility(false);
                    } else {
                        suggestion = Emoji.getSuggestion(stringBuilder.toString());
                        if (suggestion != null) {
                            r0.searchResultSuggestions = new ArrayList();
                            while (i2 < suggestion.length) {
                                emojiSuggestion = (EmojiSuggestion) obj;
                                emojiSuggestion.emoji = emojiSuggestion.emoji.replace("\ufe0f", TtmlNode.ANONYMOUS_REGION_ID);
                                r0.searchResultSuggestions.add(emojiSuggestion);
                            }
                            Emoji.loadRecentEmoji();
                            Collections.sort(r0.searchResultSuggestions, /* anonymous class already generated */);
                        }
                        r0.searchResultHashtags = null;
                        r0.searchResultUsernames = null;
                        r0.searchResultUsernamesMap = null;
                        r0.searchResultCommands = null;
                        r0.searchResultCommandsHelp = null;
                        r0.searchResultCommandsUsers = null;
                        notifyDataSetChanged();
                        mentionsAdapterDelegate = r0.delegate;
                        if (r0.searchResultSuggestions != null) {
                            z3 = true;
                        }
                        mentionsAdapterDelegate.needChangePanelVisibility(z3);
                    }
                }
                return;
            }
            r0.delegate.needChangePanelVisibility(false);
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

    public int getItemViewType(int i) {
        if (this.foundContextBot != null && !this.inlineMediaEnabled) {
            return 3;
        }
        if (this.searchResultBotContext != null) {
            return (i != 0 || this.searchResultBotContextSwitch == 0) ? 1 : 2;
        } else {
            return 0;
        }
    }

    public void addHashtagsFromMessage(CharSequence charSequence) {
        this.searchAdapterHelper.addHashtagsFromMessage(charSequence);
    }

    public int getItemPosition(int i) {
        return (this.searchResultBotContext == null || this.searchResultBotContextSwitch == null) ? i : i - 1;
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

    public boolean isEnabled(ViewHolder viewHolder) {
        if (this.foundContextBot != null) {
            if (this.inlineMediaEnabled == null) {
                return null;
            }
        }
        return true;
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        switch (i) {
            case 0:
                viewGroup = new MentionCell(this.mContext);
                ((MentionCell) viewGroup).setIsDarkTheme(this.isDarkTheme);
                break;
            case 1:
                viewGroup = new ContextLinkCell(this.mContext);
                ((ContextLinkCell) viewGroup).setDelegate(new ContextLinkCellDelegate() {
                    public void didPressedImage(ContextLinkCell contextLinkCell) {
                        MentionsAdapter.this.delegate.onContextClick(contextLinkCell.getResult());
                    }
                });
                break;
            case 2:
                viewGroup = new BotSwitchCell(this.mContext);
                break;
            default:
                viewGroup = new TextView(this.mContext);
                viewGroup.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
                viewGroup.setTextSize(1, 14.0f);
                viewGroup.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
                break;
        }
        return new Holder(viewGroup);
    }

    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        boolean z = false;
        if (viewHolder.getItemViewType() == 3) {
            TextView textView = (TextView) viewHolder.itemView;
            i = this.parentFragment.getCurrentChat();
            if (i == 0) {
                return;
            }
            if (AndroidUtilities.isBannedForever(i.banned_rights.until_date)) {
                textView.setText(LocaleController.getString("AttachInlineRestrictedForever", C0446R.string.AttachInlineRestrictedForever));
            } else {
                textView.setText(LocaleController.formatString("AttachInlineRestricted", C0446R.string.AttachInlineRestricted, LocaleController.formatDateForBan((long) i.banned_rights.until_date)));
            }
        } else if (this.searchResultBotContext != null) {
            int i2 = this.searchResultBotContextSwitch != null ? 1 : false;
            if (viewHolder.getItemViewType() != 2) {
                if (i2 != 0) {
                    i--;
                }
                ContextLinkCell contextLinkCell = (ContextLinkCell) viewHolder.itemView;
                BotInlineResult botInlineResult = (BotInlineResult) this.searchResultBotContext.get(i);
                boolean z2 = this.contextMedia;
                boolean z3 = i != this.searchResultBotContext.size() - 1;
                if (i2 != 0 && i == 0) {
                    z = true;
                }
                contextLinkCell.setLink(botInlineResult, z2, z3, z);
            } else if (i2 != 0) {
                ((BotSwitchCell) viewHolder.itemView).setText(this.searchResultBotContextSwitch.text);
            }
        } else if (this.searchResultUsernames != null) {
            ((MentionCell) viewHolder.itemView).setUser((User) this.searchResultUsernames.get(i));
        } else if (this.searchResultHashtags != null) {
            ((MentionCell) viewHolder.itemView).setText((String) this.searchResultHashtags.get(i));
        } else if (this.searchResultSuggestions != null) {
            ((MentionCell) viewHolder.itemView).setEmojiSuggestion((EmojiSuggestion) this.searchResultSuggestions.get(i));
        } else if (this.searchResultCommands != null) {
            ((MentionCell) viewHolder.itemView).setBotCommand((String) this.searchResultCommands.get(i), (String) this.searchResultCommandsHelp.get(i), this.searchResultCommandsUsers != null ? (User) this.searchResultCommandsUsers.get(i) : 0);
        }
    }

    public void onRequestPermissionsResultFragment(int i, String[] strArr, int[] iArr) {
        if (i == 2 && this.foundContextBot != 0 && this.foundContextBot.bot_inline_geo != 0) {
            if (iArr.length <= 0 || iArr[0] != 0) {
                onLocationUnavailable();
            } else {
                this.locationProvider.start();
            }
        }
    }
}
