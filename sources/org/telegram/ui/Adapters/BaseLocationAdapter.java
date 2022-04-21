package org.telegram.ui.Adapters;

import android.location.Location;
import android.os.Build;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.RecyclerListView;

public abstract class BaseLocationAdapter extends RecyclerListView.SelectionAdapter {
    private int currentAccount = UserConfig.selectedAccount;
    private int currentRequestNum;
    private BaseLocationAdapterDelegate delegate;
    private long dialogId;
    protected ArrayList<String> iconUrls = new ArrayList<>();
    private String lastFoundQuery;
    private Location lastSearchLocation;
    private String lastSearchQuery;
    protected ArrayList<TLRPC.TL_messageMediaVenue> places = new ArrayList<>();
    private boolean searchInProgress;
    private Runnable searchRunnable;
    protected boolean searched = false;
    protected boolean searching;
    private boolean searchingUser;

    public interface BaseLocationAdapterDelegate {
        void didLoadSearchResult(ArrayList<TLRPC.TL_messageMediaVenue> arrayList);
    }

    public void destroy() {
        if (this.currentRequestNum != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.currentRequestNum, true);
            this.currentRequestNum = 0;
        }
    }

    public void setDelegate(long did, BaseLocationAdapterDelegate delegate2) {
        this.dialogId = did;
        this.delegate = delegate2;
    }

    public void searchDelayed(String query, Location coordinate) {
        if (query == null || query.length() == 0) {
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
        BaseLocationAdapter$$ExternalSyntheticLambda1 baseLocationAdapter$$ExternalSyntheticLambda1 = new BaseLocationAdapter$$ExternalSyntheticLambda1(this, query, coordinate);
        this.searchRunnable = baseLocationAdapter$$ExternalSyntheticLambda1;
        dispatchQueue.postRunnable(baseLocationAdapter$$ExternalSyntheticLambda1, 400);
    }

    /* renamed from: lambda$searchDelayed$1$org-telegram-ui-Adapters-BaseLocationAdapter  reason: not valid java name */
    public /* synthetic */ void m1305xdd58fbb0(String query, Location coordinate) {
        AndroidUtilities.runOnUIThread(new BaseLocationAdapter$$ExternalSyntheticLambda0(this, query, coordinate));
    }

    /* renamed from: lambda$searchDelayed$0$org-telegram-ui-Adapters-BaseLocationAdapter  reason: not valid java name */
    public /* synthetic */ void m1304x23e16e11(String query, Location coordinate) {
        this.searchRunnable = null;
        this.lastSearchLocation = null;
        searchPlacesWithQuery(query, coordinate, true);
    }

    private void searchBotUser() {
        if (!this.searchingUser) {
            this.searchingUser = true;
            TLRPC.TL_contacts_resolveUsername req = new TLRPC.TL_contacts_resolveUsername();
            req.username = MessagesController.getInstance(this.currentAccount).venueSearchBot;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new BaseLocationAdapter$$ExternalSyntheticLambda4(this));
        }
    }

    /* renamed from: lambda$searchBotUser$3$org-telegram-ui-Adapters-BaseLocationAdapter  reason: not valid java name */
    public /* synthetic */ void m1303x63b66c1e(TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            AndroidUtilities.runOnUIThread(new BaseLocationAdapter$$ExternalSyntheticLambda2(this, response));
        }
    }

    /* renamed from: lambda$searchBotUser$2$org-telegram-ui-Adapters-BaseLocationAdapter  reason: not valid java name */
    public /* synthetic */ void m1302xaa3ede7f(TLObject response) {
        TLRPC.TL_contacts_resolvedPeer res = (TLRPC.TL_contacts_resolvedPeer) response;
        MessagesController.getInstance(this.currentAccount).putUsers(res.users, false);
        MessagesController.getInstance(this.currentAccount).putChats(res.chats, false);
        MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(res.users, res.chats, true, true);
        Location coord = this.lastSearchLocation;
        this.lastSearchLocation = null;
        searchPlacesWithQuery(this.lastSearchQuery, coord, false);
    }

    public boolean isSearching() {
        return this.searchInProgress;
    }

    public String getLastSearchString() {
        return this.lastFoundQuery;
    }

    public void searchPlacesWithQuery(String query, Location coordinate, boolean searchUser) {
        searchPlacesWithQuery(query, coordinate, searchUser, false);
    }

    /* access modifiers changed from: protected */
    public void notifyStartSearch(boolean wasSearching, int oldItemCount, boolean animated) {
        if (!animated || Build.VERSION.SDK_INT < 19) {
            notifyDataSetChanged();
        } else if (!this.places.isEmpty() && !wasSearching) {
            int placesCount = this.places.size() + 3;
            int offset = oldItemCount - placesCount;
            notifyItemInserted(offset);
            notifyItemRangeRemoved(offset, placesCount);
        } else if (!wasSearching) {
            int fromIndex = Math.max(0, getItemCount() - 4);
            notifyItemRangeRemoved(fromIndex, getItemCount() - fromIndex);
        }
    }

    public void searchPlacesWithQuery(String query, Location coordinate, boolean searchUser, boolean animated) {
        if (coordinate != null) {
            Location location = this.lastSearchLocation;
            if (location == null || coordinate.distanceTo(location) >= 200.0f) {
                this.lastSearchLocation = new Location(coordinate);
                this.lastSearchQuery = query;
                if (this.searching) {
                    this.searching = false;
                    if (this.currentRequestNum != 0) {
                        ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.currentRequestNum, true);
                        this.currentRequestNum = 0;
                    }
                }
                int itemCount = getItemCount();
                boolean z = this.searching;
                this.searching = true;
                boolean z2 = this.searched;
                this.searched = true;
                TLObject object = MessagesController.getInstance(this.currentAccount).getUserOrChat(MessagesController.getInstance(this.currentAccount).venueSearchBot);
                if (object instanceof TLRPC.User) {
                    TLRPC.User user = (TLRPC.User) object;
                    TLRPC.TL_messages_getInlineBotResults req = new TLRPC.TL_messages_getInlineBotResults();
                    req.query = query == null ? "" : query;
                    req.bot = MessagesController.getInstance(this.currentAccount).getInputUser(user);
                    req.offset = "";
                    req.geo_point = new TLRPC.TL_inputGeoPoint();
                    req.geo_point.lat = AndroidUtilities.fixLocationCoord(coordinate.getLatitude());
                    req.geo_point._long = AndroidUtilities.fixLocationCoord(coordinate.getLongitude());
                    req.flags = 1 | req.flags;
                    if (DialogObject.isEncryptedDialog(this.dialogId)) {
                        req.peer = new TLRPC.TL_inputPeerEmpty();
                    } else {
                        req.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(this.dialogId);
                    }
                    this.currentRequestNum = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new BaseLocationAdapter$$ExternalSyntheticLambda5(this, query));
                    notifyDataSetChanged();
                } else if (searchUser) {
                    searchBotUser();
                }
            }
        }
    }

    /* renamed from: lambda$searchPlacesWithQuery$5$org-telegram-ui-Adapters-BaseLocationAdapter  reason: not valid java name */
    public /* synthetic */ void m1307xd7bd1b58(String query, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new BaseLocationAdapter$$ExternalSyntheticLambda3(this, error, query, response));
    }

    /* renamed from: lambda$searchPlacesWithQuery$4$org-telegram-ui-Adapters-BaseLocationAdapter  reason: not valid java name */
    public /* synthetic */ void m1306x1e458db9(TLRPC.TL_error error, String query, TLObject response) {
        if (error == null) {
            this.currentRequestNum = 0;
            this.searching = false;
            this.places.clear();
            this.iconUrls.clear();
            this.searchInProgress = false;
            this.lastFoundQuery = query;
            TLRPC.messages_BotResults res = (TLRPC.messages_BotResults) response;
            int size = res.results.size();
            for (int a = 0; a < size; a++) {
                TLRPC.BotInlineResult result = res.results.get(a);
                if ("venue".equals(result.type) && (result.send_message instanceof TLRPC.TL_botInlineMessageMediaVenue)) {
                    TLRPC.TL_botInlineMessageMediaVenue mediaVenue = (TLRPC.TL_botInlineMessageMediaVenue) result.send_message;
                    ArrayList<String> arrayList = this.iconUrls;
                    arrayList.add("https://ss3.4sqi.net/img/categories_v2/" + mediaVenue.venue_type + "_64.png");
                    TLRPC.TL_messageMediaVenue venue = new TLRPC.TL_messageMediaVenue();
                    venue.geo = mediaVenue.geo;
                    venue.address = mediaVenue.address;
                    venue.title = mediaVenue.title;
                    venue.venue_type = mediaVenue.venue_type;
                    venue.venue_id = mediaVenue.venue_id;
                    venue.provider = mediaVenue.provider;
                    this.places.add(venue);
                }
            }
        }
        BaseLocationAdapterDelegate baseLocationAdapterDelegate = this.delegate;
        if (baseLocationAdapterDelegate != null) {
            baseLocationAdapterDelegate.didLoadSearchResult(this.places);
        }
        notifyDataSetChanged();
    }
}
