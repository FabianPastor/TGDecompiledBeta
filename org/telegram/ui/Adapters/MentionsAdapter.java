package org.telegram.ui.Adapters;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.location.Location;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.SendMessagesHelper.LocationProvider;
import org.telegram.messenger.SendMessagesHelper.LocationProvider.LocationProviderDelegate;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.query.SearchQuery;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.BotInfo;
import org.telegram.tgnet.TLRPC.BotInlineResult;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.ChatParticipant;
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
import org.telegram.ui.Adapters.SearchAdapterHelper.HashtagObject;
import org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate;
import org.telegram.ui.Cells.BotSwitchCell;
import org.telegram.ui.Cells.ContextLinkCell;
import org.telegram.ui.Cells.ContextLinkCell.ContextLinkCellDelegate;
import org.telegram.ui.Cells.MentionCell;

public class MentionsAdapter extends Adapter {
    private boolean allowNewMentions = true;
    private HashMap<Integer, BotInfo> botInfo;
    private int botsCount;
    private boolean contextMedia;
    private int contextQueryReqid;
    private Runnable contextQueryRunnable;
    private int contextUsernameReqid;
    private MentionsAdapterDelegate delegate;
    private long dialog_id;
    private User foundContextBot;
    private ChatFull info;
    private boolean isDarkTheme;
    private Location lastKnownLocation;
    private int lastPosition;
    private String lastText;
    private LocationProvider locationProvider = new LocationProvider(new LocationProviderDelegate() {
        public void onLocationAcquired(Location location) {
            if (MentionsAdapter.this.foundContextBot != null && MentionsAdapter.this.foundContextBot.bot_inline_geo) {
                MentionsAdapter.this.lastKnownLocation = location;
                MentionsAdapter.this.searchForContextBotResults(MentionsAdapter.this.foundContextBot, MentionsAdapter.this.searchingContextQuery, "");
            }
        }

        public void onUnableLocationAcquire() {
            MentionsAdapter.this.onLocationUnavailable();
        }
    }) {
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
    private BaseFragment parentFragment;
    private int resultLength;
    private int resultStartPosition;
    private SearchAdapterHelper searchAdapterHelper;
    private ArrayList<BotInlineResult> searchResultBotContext;
    private HashMap<String, BotInlineResult> searchResultBotContextById;
    private TL_inlineBotSwitchPM searchResultBotContextSwitch;
    private ArrayList<String> searchResultCommands;
    private ArrayList<String> searchResultCommandsHelp;
    private ArrayList<User> searchResultCommandsUsers;
    private ArrayList<String> searchResultHashtags;
    private ArrayList<User> searchResultUsernames;
    private String searchingContextQuery;
    private String searchingContextUsername;

    public interface MentionsAdapterDelegate {
        void needChangePanelVisibility(boolean z);

        void onContextClick(BotInlineResult botInlineResult);

        void onContextSearch(boolean z);
    }

    public class Holder extends ViewHolder {
        public Holder(View itemView) {
            super(itemView);
        }
    }

    public MentionsAdapter(Context context, boolean darkTheme, long did, MentionsAdapterDelegate mentionsAdapterDelegate) {
        this.mContext = context;
        this.delegate = mentionsAdapterDelegate;
        this.isDarkTheme = darkTheme;
        this.dialog_id = did;
        this.searchAdapterHelper = new SearchAdapterHelper();
        this.searchAdapterHelper.setDelegate(new SearchAdapterHelperDelegate() {
            public void onDataSetChanged() {
                MentionsAdapter.this.notifyDataSetChanged();
            }

            public void onSetHashtags(ArrayList<HashtagObject> arrayList, HashMap<String, HashtagObject> hashMap) {
                if (MentionsAdapter.this.lastText != null) {
                    MentionsAdapter.this.searchUsernameOrHashtag(MentionsAdapter.this.lastText, MentionsAdapter.this.lastPosition, MentionsAdapter.this.messages);
                }
            }
        });
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
            ConnectionsManager.getInstance().cancelRequest(this.contextUsernameReqid, true);
            this.contextUsernameReqid = 0;
        }
        if (this.contextQueryReqid != 0) {
            ConnectionsManager.getInstance().cancelRequest(this.contextQueryReqid, true);
            this.contextQueryReqid = 0;
        }
        this.foundContextBot = null;
        this.searchingContextUsername = null;
        this.searchingContextQuery = null;
        this.noUserName = false;
    }

    public void setAllowNewMentions(boolean value) {
        this.allowNewMentions = value;
    }

    public void setParentFragment(BaseFragment fragment) {
        this.parentFragment = fragment;
    }

    public void setChatInfo(ChatFull chatParticipants) {
        this.info = chatParticipants;
        if (this.lastText != null) {
            searchUsernameOrHashtag(this.lastText, this.lastPosition, this.messages);
        }
    }

    public void setNeedUsernames(boolean value) {
        this.needUsernames = value;
    }

    public void setNeedBotContext(boolean value) {
        this.needBotContext = value;
    }

    public void setBotInfo(HashMap<Integer, BotInfo> info) {
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
        return this.foundContextBot != null ? this.foundContextBot : null;
    }

    public String getContextBotName() {
        return this.foundContextBot != null ? this.foundContextBot.username : "";
    }

    private void processFoundUser(User user) {
        this.contextUsernameReqid = 0;
        this.locationProvider.stop();
        if (user == null || !user.bot || user.bot_inline_placeholder == null) {
            this.foundContextBot = null;
        } else {
            this.foundContextBot = user;
            if (this.foundContextBot.bot_inline_geo) {
                if (ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).getBoolean("inlinegeo_" + this.foundContextBot.id, false) || this.parentFragment == null || this.parentFragment.getParentActivity() == null) {
                    checkLocationPermissionsOrStart();
                } else {
                    final User foundContextBotFinal = this.foundContextBot;
                    Builder builder = new Builder(this.parentFragment.getParentActivity());
                    builder.setTitle(LocaleController.getString("ShareYouLocationTitle", R.string.ShareYouLocationTitle));
                    builder.setMessage(LocaleController.getString("ShareYouLocationInline", R.string.ShareYouLocationInline));
                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (foundContextBotFinal != null) {
                                ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit().putBoolean("inlinegeo_" + foundContextBotFinal.id, true).commit();
                                MentionsAdapter.this.checkLocationPermissionsOrStart();
                            }
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), new OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            MentionsAdapter.this.onLocationUnavailable();
                        }
                    });
                    this.parentFragment.showDialog(builder.create());
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
        searchForContextBotResults(this.foundContextBot, this.searchingContextQuery, "");
    }

    private void searchForContextBot(final String username, final String query) {
        if (this.foundContextBot == null || this.foundContextBot.username == null || !this.foundContextBot.username.equals(username) || this.searchingContextQuery == null || !this.searchingContextQuery.equals(query)) {
            this.searchResultBotContext = null;
            this.searchResultBotContextById = null;
            this.searchResultBotContextSwitch = null;
            notifyDataSetChanged();
            if (this.foundContextBot != null) {
                this.delegate.needChangePanelVisibility(false);
            }
            if (this.contextQueryRunnable != null) {
                AndroidUtilities.cancelRunOnUIThread(this.contextQueryRunnable);
                this.contextQueryRunnable = null;
            }
            if (TextUtils.isEmpty(username) || !(this.searchingContextUsername == null || this.searchingContextUsername.equals(username))) {
                if (this.contextUsernameReqid != 0) {
                    ConnectionsManager.getInstance().cancelRequest(this.contextUsernameReqid, true);
                    this.contextUsernameReqid = 0;
                }
                if (this.contextQueryReqid != 0) {
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
                if (username == null || username.length() == 0) {
                    return;
                }
            }
            if (query == null) {
                if (this.contextQueryReqid != 0) {
                    ConnectionsManager.getInstance().cancelRequest(this.contextQueryReqid, true);
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
            this.searchingContextQuery = query;
            this.contextQueryRunnable = new Runnable() {
                public void run() {
                    if (MentionsAdapter.this.contextQueryRunnable == this) {
                        MentionsAdapter.this.contextQueryRunnable = null;
                        if (MentionsAdapter.this.foundContextBot == null && !MentionsAdapter.this.noUserName) {
                            MentionsAdapter.this.searchingContextUsername = username;
                            User user = MessagesController.getInstance().getUser(MentionsAdapter.this.searchingContextUsername);
                            if (user != null) {
                                MentionsAdapter.this.processFoundUser(user);
                                return;
                            }
                            TL_contacts_resolveUsername req = new TL_contacts_resolveUsername();
                            req.username = MentionsAdapter.this.searchingContextUsername;
                            MentionsAdapter.this.contextUsernameReqid = ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
                                public void run(final TLObject response, final TL_error error) {
                                    AndroidUtilities.runOnUIThread(new Runnable() {
                                        public void run() {
                                            if (MentionsAdapter.this.searchingContextUsername != null && MentionsAdapter.this.searchingContextUsername.equals(username)) {
                                                User user = null;
                                                if (error == null) {
                                                    TL_contacts_resolvedPeer res = response;
                                                    if (!res.users.isEmpty()) {
                                                        user = (User) res.users.get(0);
                                                        MessagesController.getInstance().putUser(user, false);
                                                        MessagesStorage.getInstance().putUsersAndChats(res.users, null, true, true);
                                                    }
                                                }
                                                MentionsAdapter.this.processFoundUser(user);
                                            }
                                        }
                                    });
                                }
                            });
                        } else if (!MentionsAdapter.this.noUserName) {
                            MentionsAdapter.this.searchForContextBotResults(MentionsAdapter.this.foundContextBot, query, "");
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
            searchForContextBotResults(this.foundContextBot, this.searchingContextQuery, "");
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
            searchForContextBotResults(this.foundContextBot, this.searchingContextQuery, this.nextQueryOffset);
        }
    }

    private void searchForContextBotResults(User user, final String query, final String offset) {
        if (this.contextQueryReqid != 0) {
            ConnectionsManager.getInstance().cancelRequest(this.contextQueryReqid, true);
            this.contextQueryReqid = 0;
        }
        if (query == null || user == null) {
            this.searchingContextQuery = null;
        } else if (!user.bot_inline_geo || this.lastKnownLocation != null) {
            TL_messages_getInlineBotResults req = new TL_messages_getInlineBotResults();
            req.bot = MessagesController.getInputUser(user);
            req.query = query;
            req.offset = offset;
            if (!(!user.bot_inline_geo || this.lastKnownLocation == null || this.lastKnownLocation.getLatitude() == -1000.0d)) {
                req.flags |= 1;
                req.geo_point = new TL_inputGeoPoint();
                req.geo_point.lat = this.lastKnownLocation.getLatitude();
                req.geo_point._long = this.lastKnownLocation.getLongitude();
            }
            int lower_id = (int) this.dialog_id;
            int high_id = (int) (this.dialog_id >> 32);
            if (lower_id != 0) {
                req.peer = MessagesController.getInputPeer(lower_id);
            } else {
                req.peer = new TL_inputPeerEmpty();
            }
            this.contextQueryReqid = ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
                public void run(final TLObject response, final TL_error error) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            boolean z = false;
                            if (MentionsAdapter.this.searchingContextQuery != null && query.equals(MentionsAdapter.this.searchingContextQuery)) {
                                if (MentionsAdapter.this.delegate != null) {
                                    MentionsAdapter.this.delegate.onContextSearch(false);
                                }
                                MentionsAdapter.this.contextQueryReqid = 0;
                                if (error == null) {
                                    TL_messages_botResults res = response;
                                    MentionsAdapter.this.nextQueryOffset = res.next_offset;
                                    if (MentionsAdapter.this.searchResultBotContextById == null) {
                                        MentionsAdapter.this.searchResultBotContextById = new HashMap();
                                        MentionsAdapter.this.searchResultBotContextSwitch = res.switch_pm;
                                    }
                                    int a = 0;
                                    while (a < res.results.size()) {
                                        BotInlineResult result = (BotInlineResult) res.results.get(a);
                                        if (MentionsAdapter.this.searchResultBotContextById.containsKey(result.id) || (!(result.document instanceof TL_document) && !(result.photo instanceof TL_photo) && result.content_url == null && (result.send_message instanceof TL_botInlineMessageMediaAuto))) {
                                            res.results.remove(a);
                                            a--;
                                        }
                                        result.query_id = res.query_id;
                                        MentionsAdapter.this.searchResultBotContextById.put(result.id, result);
                                        a++;
                                    }
                                    boolean added = false;
                                    if (MentionsAdapter.this.searchResultBotContext == null || offset.length() == 0) {
                                        MentionsAdapter.this.searchResultBotContext = res.results;
                                        MentionsAdapter.this.contextMedia = res.gallery;
                                    } else {
                                        added = true;
                                        MentionsAdapter.this.searchResultBotContext.addAll(res.results);
                                        if (res.results.isEmpty()) {
                                            MentionsAdapter.this.nextQueryOffset = "";
                                        }
                                    }
                                    MentionsAdapter.this.searchResultHashtags = null;
                                    MentionsAdapter.this.searchResultUsernames = null;
                                    MentionsAdapter.this.searchResultCommands = null;
                                    MentionsAdapter.this.searchResultCommandsHelp = null;
                                    MentionsAdapter.this.searchResultCommandsUsers = null;
                                    if (added) {
                                        boolean hasTop;
                                        int i;
                                        if (MentionsAdapter.this.searchResultBotContextSwitch != null) {
                                            hasTop = true;
                                        } else {
                                            hasTop = false;
                                        }
                                        MentionsAdapter mentionsAdapter = MentionsAdapter.this;
                                        int size = MentionsAdapter.this.searchResultBotContext.size() - res.results.size();
                                        if (hasTop) {
                                            i = 1;
                                        } else {
                                            i = 0;
                                        }
                                        mentionsAdapter.notifyItemChanged((i + size) - 1);
                                        mentionsAdapter = MentionsAdapter.this;
                                        size = MentionsAdapter.this.searchResultBotContext.size() - res.results.size();
                                        if (hasTop) {
                                            i = 1;
                                        } else {
                                            i = 0;
                                        }
                                        mentionsAdapter.notifyItemRangeInserted(i + size, res.results.size());
                                    } else {
                                        MentionsAdapter.this.notifyDataSetChanged();
                                    }
                                    MentionsAdapterDelegate access$1400 = MentionsAdapter.this.delegate;
                                    if (!(MentionsAdapter.this.searchResultBotContext.isEmpty() && MentionsAdapter.this.searchResultBotContextSwitch == null)) {
                                        z = true;
                                    }
                                    access$1400.needChangePanelVisibility(z);
                                }
                            }
                        }
                    });
                }
            }, 2);
        }
    }

    public void searchUsernameOrHashtag(String text, int position, ArrayList<MessageObject> messageObjects) {
        if (text == null || text.length() == 0) {
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
        StringBuilder result = new StringBuilder();
        int foundType = -1;
        boolean hasIllegalUsernameCharacters = false;
        if (this.needBotContext && text.charAt(0) == '@') {
            int index = text.indexOf(32);
            int len = text.length();
            String username = null;
            String query = null;
            if (index > 0) {
                username = text.substring(1, index);
                query = text.substring(index + 1);
            } else if (text.charAt(len - 1) == 't' && text.charAt(len - 2) == 'o' && text.charAt(len - 3) == 'b') {
                username = text.substring(1);
                query = "";
            } else {
                searchForContextBot(null, null);
            }
            if (username == null || username.length() < 1) {
                username = "";
            } else {
                for (a = 1; a < username.length(); a++) {
                    ch = username.charAt(a);
                    if ((ch < '0' || ch > '9') && ((ch < 'a' || ch > 'z') && ((ch < 'A' || ch > 'Z') && ch != '_'))) {
                        username = "";
                        break;
                    }
                }
            }
            searchForContextBot(username, query);
        } else {
            searchForContextBot(null, null);
        }
        if (this.foundContextBot == null) {
            int dogPostion = -1;
            a = searchPostion;
            while (a >= 0) {
                if (a < text.length()) {
                    ch = text.charAt(a);
                    if (a == 0 || text.charAt(a - 1) == ' ' || text.charAt(a - 1) == '\n') {
                        if (ch != '@') {
                            if (ch != '#') {
                                if (a == 0 && this.botInfo != null && ch == '/') {
                                    foundType = 2;
                                    this.resultStartPosition = a;
                                    this.resultLength = result.length() + 1;
                                    break;
                                }
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
                            if (hasIllegalUsernameCharacters) {
                                this.delegate.needChangePanelVisibility(false);
                                return;
                            } else if (this.info != null || a == 0) {
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
            if (foundType == -1) {
                this.delegate.needChangePanelVisibility(false);
            } else if (foundType == 0) {
                User user;
                ArrayList<Integer> users = new ArrayList();
                for (a = 0; a < Math.min(100, messageObjects.size()); a++) {
                    int from_id = ((MessageObject) messageObjects.get(a)).messageOwner.from_id;
                    if (!users.contains(Integer.valueOf(from_id))) {
                        users.add(Integer.valueOf(from_id));
                    }
                }
                String usernameString = result.toString().toLowerCase();
                ArrayList<User> newResult = new ArrayList();
                HashMap<Integer, User> newResultsHashMap = new HashMap();
                if (this.needBotContext && dogPostion == 0 && !SearchQuery.inlineBots.isEmpty()) {
                    int count = 0;
                    for (a = 0; a < SearchQuery.inlineBots.size(); a++) {
                        user = MessagesController.getInstance().getUser(Integer.valueOf(((TL_topPeer) SearchQuery.inlineBots.get(a)).peer.user_id));
                        if (user != null) {
                            if (user.username != null && user.username.length() > 0 && ((usernameString.length() > 0 && user.username.toLowerCase().startsWith(usernameString)) || usernameString.length() == 0)) {
                                newResult.add(user);
                                newResultsHashMap.put(Integer.valueOf(user.id), user);
                                count++;
                            }
                            if (count == 5) {
                                break;
                            }
                        }
                    }
                }
                if (!(this.info == null || this.info.participants == null)) {
                    for (a = 0; a < this.info.participants.participants.size(); a++) {
                        user = MessagesController.getInstance().getUser(Integer.valueOf(((ChatParticipant) this.info.participants.participants.get(a)).user_id));
                        if (!(user == null || UserObject.isUserSelf(user) || newResultsHashMap.containsKey(Integer.valueOf(user.id)))) {
                            if (usernameString.length() == 0) {
                                if (!user.deleted && (this.allowNewMentions || !(this.allowNewMentions || user.username == null || user.username.length() == 0))) {
                                    newResult.add(user);
                                }
                            } else if (user.username != null && user.username.length() > 0 && user.username.toLowerCase().startsWith(usernameString)) {
                                newResult.add(user);
                            } else if (this.allowNewMentions || !(user.username == null || user.username.length() == 0)) {
                                if (user.first_name != null && user.first_name.length() > 0 && user.first_name.toLowerCase().startsWith(usernameString)) {
                                    newResult.add(user);
                                } else if (user.last_name != null && user.last_name.length() > 0 && user.last_name.toLowerCase().startsWith(usernameString)) {
                                    newResult.add(user);
                                }
                            }
                        }
                    }
                }
                this.searchResultHashtags = null;
                this.searchResultCommands = null;
                this.searchResultCommandsHelp = null;
                this.searchResultCommandsUsers = null;
                this.searchResultUsernames = newResult;
                final HashMap<Integer, User> hashMap = newResultsHashMap;
                final ArrayList<Integer> arrayList = users;
                Collections.sort(this.searchResultUsernames, new Comparator<User>() {
                    public int compare(User lhs, User rhs) {
                        if (hashMap.containsKey(Integer.valueOf(lhs.id)) && hashMap.containsKey(Integer.valueOf(rhs.id))) {
                            return 0;
                        }
                        if (hashMap.containsKey(Integer.valueOf(lhs.id))) {
                            return -1;
                        }
                        if (hashMap.containsKey(Integer.valueOf(rhs.id))) {
                            return 1;
                        }
                        int lhsNum = arrayList.indexOf(Integer.valueOf(lhs.id));
                        int rhsNum = arrayList.indexOf(Integer.valueOf(rhs.id));
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
                });
                notifyDataSetChanged();
                this.delegate.needChangePanelVisibility(!newResult.isEmpty());
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
                this.searchResultCommands = null;
                this.searchResultCommandsHelp = null;
                this.searchResultCommandsUsers = null;
                notifyDataSetChanged();
                this.delegate.needChangePanelVisibility(!newResult.isEmpty());
            } else if (foundType == 2) {
                newResult = new ArrayList();
                ArrayList<String> newResultHelp = new ArrayList();
                ArrayList<User> newResultUsers = new ArrayList();
                String command = result.toString().toLowerCase();
                for (Entry<Integer, BotInfo> entry : this.botInfo.entrySet()) {
                    BotInfo botInfo = (BotInfo) entry.getValue();
                    for (a = 0; a < botInfo.commands.size(); a++) {
                        TL_botCommand botCommand = (TL_botCommand) botInfo.commands.get(a);
                        if (!(botCommand == null || botCommand.command == null || !botCommand.command.startsWith(command))) {
                            newResult.add("/" + botCommand.command);
                            newResultHelp.add(botCommand.description);
                            newResultUsers.add(MessagesController.getInstance().getUser(Integer.valueOf(botInfo.user_id)));
                        }
                    }
                }
                this.searchResultHashtags = null;
                this.searchResultUsernames = null;
                this.searchResultCommands = newResult;
                this.searchResultCommandsHelp = newResultHelp;
                this.searchResultCommandsUsers = newResultUsers;
                notifyDataSetChanged();
                this.delegate.needChangePanelVisibility(!newResult.isEmpty());
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
        int i = 0;
        if (this.searchResultBotContext != null) {
            int size = this.searchResultBotContext.size();
            if (this.searchResultBotContextSwitch != null) {
                i = 1;
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
            return 0;
        }
    }

    public int getItemViewType(int position) {
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
                objArr[1] = this.searchResultCommandsUsers.get(i) != null ? ((User) this.searchResultCommandsUsers.get(i)).username : "";
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

    public boolean isMediaLayout() {
        return this.contextMedia;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == 1) {
            view = new ContextLinkCell(this.mContext);
            ((ContextLinkCell) view).setDelegate(new ContextLinkCellDelegate() {
                public void didPressedImage(ContextLinkCell cell) {
                    MentionsAdapter.this.delegate.onContextClick(cell.getResult());
                }
            });
        } else if (viewType == 2) {
            view = new BotSwitchCell(this.mContext);
        } else {
            view = new MentionCell(this.mContext);
            ((MentionCell) view).setIsDarkTheme(this.isDarkTheme);
        }
        return new Holder(view);
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        boolean z = true;
        if (this.searchResultBotContext != null) {
            boolean hasTop = this.searchResultBotContextSwitch != null;
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
