package org.telegram.ui.Adapters;

import android.location.Location;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
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
    private String lastFoundQuery;
    private Location lastSearchLocation;
    private String lastSearchQuery;
    protected ArrayList<TL_messageMediaVenue> places = new ArrayList();
    private boolean searchInProgress;
    private Runnable searchRunnable;
    protected boolean searching;
    private boolean searchingUser;

    public interface BaseLocationAdapterDelegate {
        void didLoadSearchResult(ArrayList<TL_messageMediaVenue> arrayList);
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

    public void searchDelayed(String str, Location location) {
        if (str == null || str.length() == 0) {
            this.places.clear();
            this.searchInProgress = false;
            notifyDataSetChanged();
            return;
        }
        if (this.searchRunnable != null) {
            Utilities.searchQueue.cancelRunnable(this.searchRunnable);
            this.searchRunnable = null;
        }
        this.searchInProgress = true;
        DispatchQueue dispatchQueue = Utilities.searchQueue;
        -$$Lambda$BaseLocationAdapter$T_85LSiVXaUUUWP3WNWn8qK1uUI -__lambda_baselocationadapter_t_85lsivxauuuwp3wnwn8qk1uui = new -$$Lambda$BaseLocationAdapter$T_85LSiVXaUUUWP3WNWn8qK1uUI(this, str, location);
        this.searchRunnable = -__lambda_baselocationadapter_t_85lsivxauuuwp3wnwn8qk1uui;
        dispatchQueue.postRunnable(-__lambda_baselocationadapter_t_85lsivxauuuwp3wnwn8qk1uui, 400);
    }

    public /* synthetic */ void lambda$searchDelayed$1$BaseLocationAdapter(String str, Location location) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$BaseLocationAdapter$7x00Ah4GCWHHyZQ38tHoBWV8Hyc(this, str, location));
    }

    public /* synthetic */ void lambda$null$0$BaseLocationAdapter(String str, Location location) {
        this.searchRunnable = null;
        this.lastSearchLocation = null;
        searchPlacesWithQuery(str, location, true);
    }

    private void searchBotUser() {
        if (!this.searchingUser) {
            this.searchingUser = true;
            TL_contacts_resolveUsername tL_contacts_resolveUsername = new TL_contacts_resolveUsername();
            tL_contacts_resolveUsername.username = MessagesController.getInstance(this.currentAccount).venueSearchBot;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_contacts_resolveUsername, new -$$Lambda$BaseLocationAdapter$EXf5flObCtbTzRIFqsolFWFtyyU(this));
        }
    }

    public /* synthetic */ void lambda$searchBotUser$3$BaseLocationAdapter(TLObject tLObject, TL_error tL_error) {
        if (tLObject != null) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$BaseLocationAdapter$8XXcylBQyklzy9fKLPpuyJeZxWw(this, tLObject));
        }
    }

    public /* synthetic */ void lambda$null$2$BaseLocationAdapter(TLObject tLObject) {
        TL_contacts_resolvedPeer tL_contacts_resolvedPeer = (TL_contacts_resolvedPeer) tLObject;
        MessagesController.getInstance(this.currentAccount).putUsers(tL_contacts_resolvedPeer.users, false);
        MessagesController.getInstance(this.currentAccount).putChats(tL_contacts_resolvedPeer.chats, false);
        MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(tL_contacts_resolvedPeer.users, tL_contacts_resolvedPeer.chats, true, true);
        Location location = this.lastSearchLocation;
        this.lastSearchLocation = null;
        searchPlacesWithQuery(this.lastSearchQuery, location, false);
    }

    public boolean isSearching() {
        return this.searchInProgress;
    }

    public String getLastSearchString() {
        return this.lastFoundQuery;
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
                    tL_messages_getInlineBotResults.query = str == null ? str2 : str;
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
                    this.currentRequestNum = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getInlineBotResults, new -$$Lambda$BaseLocationAdapter$Q_AKnGvZK4rCf-iCn8zQfX2dHBA(this, str));
                    notifyDataSetChanged();
                } else {
                    if (z) {
                        searchBotUser();
                    }
                }
            }
        }
    }

    public /* synthetic */ void lambda$searchPlacesWithQuery$5$BaseLocationAdapter(String str, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$BaseLocationAdapter$7gITzgpAhQdM5ZDr0Er_UR5wo2g(this, str, tL_error, tLObject));
    }

    public /* synthetic */ void lambda$null$4$BaseLocationAdapter(String str, TL_error tL_error, TLObject tLObject) {
        int i = 0;
        this.currentRequestNum = 0;
        this.searching = false;
        this.places.clear();
        this.iconUrls.clear();
        this.searchInProgress = false;
        this.lastFoundQuery = str;
        if (tL_error == null) {
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
        BaseLocationAdapterDelegate baseLocationAdapterDelegate = this.delegate;
        if (baseLocationAdapterDelegate != null) {
            baseLocationAdapterDelegate.didLoadSearchResult(this.places);
        }
        notifyDataSetChanged();
    }
}
