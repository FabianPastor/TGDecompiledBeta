package org.telegram.ui.Adapters;

import android.location.Location;
import android.os.Build;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$BotInlineMessage;
import org.telegram.tgnet.TLRPC$BotInlineResult;
import org.telegram.tgnet.TLRPC$TL_botInlineMessageMediaVenue;
import org.telegram.tgnet.TLRPC$TL_contacts_resolveUsername;
import org.telegram.tgnet.TLRPC$TL_contacts_resolvedPeer;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_inputGeoPoint;
import org.telegram.tgnet.TLRPC$TL_inputPeerEmpty;
import org.telegram.tgnet.TLRPC$TL_messageMediaVenue;
import org.telegram.tgnet.TLRPC$TL_messages_getInlineBotResults;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$messages_BotResults;
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
    protected ArrayList<TLRPC$TL_messageMediaVenue> places = new ArrayList<>();
    private boolean searchInProgress;
    private Runnable searchRunnable;
    protected boolean searching;
    private boolean searchingUser;

    public interface BaseLocationAdapterDelegate {
        void didLoadSearchResult(ArrayList<TLRPC$TL_messageMediaVenue> arrayList);
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
        $$Lambda$BaseLocationAdapter$M5pEnfvG1qE0SmAACEhWNyquPlU r1 = new Runnable(str, location) {
            public final /* synthetic */ String f$1;
            public final /* synthetic */ Location f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                BaseLocationAdapter.this.lambda$searchDelayed$1$BaseLocationAdapter(this.f$1, this.f$2);
            }
        };
        this.searchRunnable = r1;
        dispatchQueue.postRunnable(r1, 400);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$searchDelayed$1 */
    public /* synthetic */ void lambda$searchDelayed$1$BaseLocationAdapter(String str, Location location) {
        AndroidUtilities.runOnUIThread(new Runnable(str, location) {
            public final /* synthetic */ String f$1;
            public final /* synthetic */ Location f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                BaseLocationAdapter.this.lambda$null$0$BaseLocationAdapter(this.f$1, this.f$2);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$0 */
    public /* synthetic */ void lambda$null$0$BaseLocationAdapter(String str, Location location) {
        this.searchRunnable = null;
        this.lastSearchLocation = null;
        searchPlacesWithQuery(str, location, true);
    }

    private void searchBotUser() {
        if (!this.searchingUser) {
            this.searchingUser = true;
            TLRPC$TL_contacts_resolveUsername tLRPC$TL_contacts_resolveUsername = new TLRPC$TL_contacts_resolveUsername();
            tLRPC$TL_contacts_resolveUsername.username = MessagesController.getInstance(this.currentAccount).venueSearchBot;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_contacts_resolveUsername, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    BaseLocationAdapter.this.lambda$searchBotUser$3$BaseLocationAdapter(tLObject, tLRPC$TL_error);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$searchBotUser$3 */
    public /* synthetic */ void lambda$searchBotUser$3$BaseLocationAdapter(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            AndroidUtilities.runOnUIThread(new Runnable(tLObject) {
                public final /* synthetic */ TLObject f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    BaseLocationAdapter.this.lambda$null$2$BaseLocationAdapter(this.f$1);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$2 */
    public /* synthetic */ void lambda$null$2$BaseLocationAdapter(TLObject tLObject) {
        TLRPC$TL_contacts_resolvedPeer tLRPC$TL_contacts_resolvedPeer = (TLRPC$TL_contacts_resolvedPeer) tLObject;
        MessagesController.getInstance(this.currentAccount).putUsers(tLRPC$TL_contacts_resolvedPeer.users, false);
        MessagesController.getInstance(this.currentAccount).putChats(tLRPC$TL_contacts_resolvedPeer.chats, false);
        MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(tLRPC$TL_contacts_resolvedPeer.users, tLRPC$TL_contacts_resolvedPeer.chats, true, true);
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
        searchPlacesWithQuery(str, location, z, false);
    }

    public void searchPlacesWithQuery(String str, Location location, boolean z, boolean z2) {
        if (location != null) {
            Location location2 = this.lastSearchLocation;
            if (location2 == null || location.distanceTo(location2) >= 200.0f) {
                this.lastSearchLocation = new Location(location);
                this.lastSearchQuery = str;
                if (this.searching) {
                    this.searching = false;
                    if (this.currentRequestNum != 0) {
                        ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.currentRequestNum, true);
                        this.currentRequestNum = 0;
                    }
                }
                int itemCount = getItemCount();
                boolean z3 = this.searching;
                this.searching = true;
                TLObject userOrChat = MessagesController.getInstance(this.currentAccount).getUserOrChat(MessagesController.getInstance(this.currentAccount).venueSearchBot);
                if (userOrChat instanceof TLRPC$User) {
                    TLRPC$User tLRPC$User = (TLRPC$User) userOrChat;
                    TLRPC$TL_messages_getInlineBotResults tLRPC$TL_messages_getInlineBotResults = new TLRPC$TL_messages_getInlineBotResults();
                    tLRPC$TL_messages_getInlineBotResults.query = str == null ? "" : str;
                    tLRPC$TL_messages_getInlineBotResults.bot = MessagesController.getInstance(this.currentAccount).getInputUser(tLRPC$User);
                    tLRPC$TL_messages_getInlineBotResults.offset = "";
                    TLRPC$TL_inputGeoPoint tLRPC$TL_inputGeoPoint = new TLRPC$TL_inputGeoPoint();
                    tLRPC$TL_messages_getInlineBotResults.geo_point = tLRPC$TL_inputGeoPoint;
                    tLRPC$TL_inputGeoPoint.lat = AndroidUtilities.fixLocationCoord(location.getLatitude());
                    tLRPC$TL_messages_getInlineBotResults.geo_point._long = AndroidUtilities.fixLocationCoord(location.getLongitude());
                    tLRPC$TL_messages_getInlineBotResults.flags |= 1;
                    int i = (int) this.dialogId;
                    if (i != 0) {
                        tLRPC$TL_messages_getInlineBotResults.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(i);
                    } else {
                        tLRPC$TL_messages_getInlineBotResults.peer = new TLRPC$TL_inputPeerEmpty();
                    }
                    this.currentRequestNum = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_getInlineBotResults, new RequestDelegate(str) {
                        public final /* synthetic */ String f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                            BaseLocationAdapter.this.lambda$searchPlacesWithQuery$5$BaseLocationAdapter(this.f$1, tLObject, tLRPC$TL_error);
                        }
                    });
                    if (!z2 || Build.VERSION.SDK_INT < 19) {
                        notifyDataSetChanged();
                    } else if (!this.places.isEmpty() && !z3) {
                        int size = this.places.size() + 1;
                        int i2 = itemCount - size;
                        notifyItemInserted(i2);
                        notifyItemRangeRemoved(i2, size);
                    } else if (!z3) {
                        notifyItemChanged(getItemCount() - 1);
                    }
                } else if (z) {
                    searchBotUser();
                }
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$searchPlacesWithQuery$5 */
    public /* synthetic */ void lambda$searchPlacesWithQuery$5$BaseLocationAdapter(String str, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(str, tLRPC$TL_error, tLObject) {
            public final /* synthetic */ String f$1;
            public final /* synthetic */ TLRPC$TL_error f$2;
            public final /* synthetic */ TLObject f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                BaseLocationAdapter.this.lambda$null$4$BaseLocationAdapter(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$4 */
    public /* synthetic */ void lambda$null$4$BaseLocationAdapter(String str, TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
        this.currentRequestNum = 0;
        this.searching = false;
        this.places.clear();
        this.iconUrls.clear();
        this.searchInProgress = false;
        this.lastFoundQuery = str;
        if (tLRPC$TL_error == null) {
            TLRPC$messages_BotResults tLRPC$messages_BotResults = (TLRPC$messages_BotResults) tLObject;
            int size = tLRPC$messages_BotResults.results.size();
            for (int i = 0; i < size; i++) {
                TLRPC$BotInlineResult tLRPC$BotInlineResult = tLRPC$messages_BotResults.results.get(i);
                if ("venue".equals(tLRPC$BotInlineResult.type)) {
                    TLRPC$BotInlineMessage tLRPC$BotInlineMessage = tLRPC$BotInlineResult.send_message;
                    if (tLRPC$BotInlineMessage instanceof TLRPC$TL_botInlineMessageMediaVenue) {
                        TLRPC$TL_botInlineMessageMediaVenue tLRPC$TL_botInlineMessageMediaVenue = (TLRPC$TL_botInlineMessageMediaVenue) tLRPC$BotInlineMessage;
                        ArrayList<String> arrayList = this.iconUrls;
                        arrayList.add("https://ss3.4sqi.net/img/categories_v2/" + tLRPC$TL_botInlineMessageMediaVenue.venue_type + "_64.png");
                        TLRPC$TL_messageMediaVenue tLRPC$TL_messageMediaVenue = new TLRPC$TL_messageMediaVenue();
                        tLRPC$TL_messageMediaVenue.geo = tLRPC$TL_botInlineMessageMediaVenue.geo;
                        tLRPC$TL_messageMediaVenue.address = tLRPC$TL_botInlineMessageMediaVenue.address;
                        tLRPC$TL_messageMediaVenue.title = tLRPC$TL_botInlineMessageMediaVenue.title;
                        tLRPC$TL_messageMediaVenue.venue_type = tLRPC$TL_botInlineMessageMediaVenue.venue_type;
                        tLRPC$TL_messageMediaVenue.venue_id = tLRPC$TL_botInlineMessageMediaVenue.venue_id;
                        tLRPC$TL_messageMediaVenue.provider = tLRPC$TL_botInlineMessageMediaVenue.provider;
                        this.places.add(tLRPC$TL_messageMediaVenue);
                    }
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
