package org.telegram.ui.Adapters;

import android.location.Location;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.BotInlineMessage;
import org.telegram.tgnet.TLRPC.BotInlineResult;
import org.telegram.tgnet.TLRPC.TL_botInlineMessageMediaVenue;
import org.telegram.tgnet.TLRPC.TL_contacts_resolveUsername;
import org.telegram.tgnet.TLRPC.TL_contacts_resolvedPeer;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputGeoPoint;
import org.telegram.tgnet.TLRPC.TL_inputPeerEmpty;
import org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
import org.telegram.tgnet.TLRPC.TL_messages_getInlineBotResults;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.messages_BotResults;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public abstract class BaseLocationAdapter extends SelectionAdapter {
    private int currentAccount = UserConfig.selectedAccount;
    private int currentRequestNum;
    private BaseLocationAdapterDelegate delegate;
    private long dialogId;
    protected ArrayList<String> iconUrls = new ArrayList();
    private Location lastSearchLocation;
    private String lastSearchQuery;
    protected ArrayList<TL_messageMediaVenue> places = new ArrayList();
    private Timer searchTimer;
    protected boolean searching;
    private boolean searchingUser;

    public interface BaseLocationAdapterDelegate {
        void didLoadedSearchResult(ArrayList<TL_messageMediaVenue> arrayList);
    }

    public void destroy() {
        if (this.currentRequestNum != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.currentRequestNum, true);
            this.currentRequestNum = 0;
        }
    }

    public void setDelegate(long j, BaseLocationAdapterDelegate baseLocationAdapterDelegate) {
        this.dialogId = j;
        this.delegate = baseLocationAdapterDelegate;
    }

    public void searchDelayed(final String str, final Location location) {
        if (str == null || str.length() == 0) {
            this.places.clear();
            notifyDataSetChanged();
            return;
        }
        try {
            if (this.searchTimer != null) {
                this.searchTimer.cancel();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        this.searchTimer = new Timer();
        this.searchTimer.schedule(new TimerTask() {
            public void run() {
                try {
                    BaseLocationAdapter.this.searchTimer.cancel();
                    BaseLocationAdapter.this.searchTimer = null;
                } catch (Exception e) {
                    FileLog.e(e);
                }
                AndroidUtilities.runOnUIThread(new -$$Lambda$BaseLocationAdapter$1$kUOM7MJ1viSwJRv7kesEXXY9pCs(this, str, location));
            }

            public /* synthetic */ void lambda$run$0$BaseLocationAdapter$1(String str, Location location) {
                BaseLocationAdapter.this.lastSearchLocation = null;
                BaseLocationAdapter.this.searchPlacesWithQuery(str, location, true);
            }
        }, 200, 500);
    }

    private void searchBotUser() {
        if (!this.searchingUser) {
            this.searchingUser = true;
            TL_contacts_resolveUsername tL_contacts_resolveUsername = new TL_contacts_resolveUsername();
            tL_contacts_resolveUsername.username = MessagesController.getInstance(this.currentAccount).venueSearchBot;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_contacts_resolveUsername, new -$$Lambda$BaseLocationAdapter$OUu2tRVUfz8Mg0cIvwT-Hlk9dBk(this));
        }
    }

    public /* synthetic */ void lambda$searchBotUser$1$BaseLocationAdapter(TLObject tLObject, TL_error tL_error) {
        if (tLObject != null) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$BaseLocationAdapter$3QgE-sikMAhKXc3W4CzFrcyGhVs(this, tLObject));
        }
    }

    public /* synthetic */ void lambda$null$0$BaseLocationAdapter(TLObject tLObject) {
        TL_contacts_resolvedPeer tL_contacts_resolvedPeer = (TL_contacts_resolvedPeer) tLObject;
        MessagesController.getInstance(this.currentAccount).putUsers(tL_contacts_resolvedPeer.users, false);
        MessagesController.getInstance(this.currentAccount).putChats(tL_contacts_resolvedPeer.chats, false);
        MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(tL_contacts_resolvedPeer.users, tL_contacts_resolvedPeer.chats, true, true);
        Location location = this.lastSearchLocation;
        this.lastSearchLocation = null;
        searchPlacesWithQuery(this.lastSearchQuery, location, false);
    }

    public void searchPlacesWithQuery(String str, Location location, boolean z) {
        if (location != null) {
            Location location2 = this.lastSearchLocation;
            if (location2 == null || location.distanceTo(location2) >= 200.0f) {
                this.lastSearchLocation = location;
                this.lastSearchQuery = str;
                if (this.searching) {
                    this.searching = false;
                    if (this.currentRequestNum != 0) {
                        ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.currentRequestNum, true);
                        this.currentRequestNum = 0;
                    }
                }
                this.searching = true;
                TLObject userOrChat = MessagesController.getInstance(this.currentAccount).getUserOrChat(MessagesController.getInstance(this.currentAccount).venueSearchBot);
                if (userOrChat instanceof User) {
                    User user = (User) userOrChat;
                    TL_messages_getInlineBotResults tL_messages_getInlineBotResults = new TL_messages_getInlineBotResults();
                    String str2 = "";
                    if (str == null) {
                        str = str2;
                    }
                    tL_messages_getInlineBotResults.query = str;
                    tL_messages_getInlineBotResults.bot = MessagesController.getInstance(this.currentAccount).getInputUser(user);
                    tL_messages_getInlineBotResults.offset = str2;
                    tL_messages_getInlineBotResults.geo_point = new TL_inputGeoPoint();
                    tL_messages_getInlineBotResults.geo_point.lat = AndroidUtilities.fixLocationCoord(location.getLatitude());
                    tL_messages_getInlineBotResults.geo_point._long = AndroidUtilities.fixLocationCoord(location.getLongitude());
                    tL_messages_getInlineBotResults.flags |= 1;
                    int i = (int) this.dialogId;
                    if (i != 0) {
                        tL_messages_getInlineBotResults.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(i);
                    } else {
                        tL_messages_getInlineBotResults.peer = new TL_inputPeerEmpty();
                    }
                    this.currentRequestNum = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getInlineBotResults, new -$$Lambda$BaseLocationAdapter$qLcpONoyStiIgQpEIiVXznU9o5o(this));
                    notifyDataSetChanged();
                } else {
                    if (z) {
                        searchBotUser();
                    }
                }
            }
        }
    }

    public /* synthetic */ void lambda$searchPlacesWithQuery$3$BaseLocationAdapter(TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$BaseLocationAdapter$cZ-97u5GiOnofWCJZtoLbhQZqEs(this, tL_error, tLObject));
    }

    public /* synthetic */ void lambda$null$2$BaseLocationAdapter(TL_error tL_error, TLObject tLObject) {
        int i = 0;
        this.currentRequestNum = 0;
        this.searching = false;
        this.places.clear();
        this.iconUrls.clear();
        if (tL_error != null) {
            BaseLocationAdapterDelegate baseLocationAdapterDelegate = this.delegate;
            if (baseLocationAdapterDelegate != null) {
                baseLocationAdapterDelegate.didLoadedSearchResult(this.places);
            }
        } else {
            messages_BotResults messages_botresults = (messages_BotResults) tLObject;
            int size = messages_botresults.results.size();
            while (i < size) {
                BotInlineResult botInlineResult = (BotInlineResult) messages_botresults.results.get(i);
                if ("venue".equals(botInlineResult.type)) {
                    BotInlineMessage botInlineMessage = botInlineResult.send_message;
                    if (botInlineMessage instanceof TL_botInlineMessageMediaVenue) {
                        TL_botInlineMessageMediaVenue tL_botInlineMessageMediaVenue = (TL_botInlineMessageMediaVenue) botInlineMessage;
                        ArrayList arrayList = this.iconUrls;
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("https://ss3.4sqi.net/img/categories_v2/");
                        stringBuilder.append(tL_botInlineMessageMediaVenue.venue_type);
                        stringBuilder.append("_64.png");
                        arrayList.add(stringBuilder.toString());
                        TL_messageMediaVenue tL_messageMediaVenue = new TL_messageMediaVenue();
                        tL_messageMediaVenue.geo = tL_botInlineMessageMediaVenue.geo;
                        tL_messageMediaVenue.address = tL_botInlineMessageMediaVenue.address;
                        tL_messageMediaVenue.title = tL_botInlineMessageMediaVenue.title;
                        tL_messageMediaVenue.venue_type = tL_botInlineMessageMediaVenue.venue_type;
                        tL_messageMediaVenue.venue_id = tL_botInlineMessageMediaVenue.venue_id;
                        tL_messageMediaVenue.provider = tL_botInlineMessageMediaVenue.provider;
                        this.places.add(tL_messageMediaVenue);
                    }
                }
                i++;
            }
        }
        notifyDataSetChanged();
    }
}
