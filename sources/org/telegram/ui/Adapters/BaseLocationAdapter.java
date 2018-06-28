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
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
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

    /* renamed from: org.telegram.ui.Adapters.BaseLocationAdapter$2 */
    class C09382 implements RequestDelegate {
        C09382() {
        }

        public void run(final TLObject response, TL_error error) {
            if (response != null) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        TL_contacts_resolvedPeer res = response;
                        MessagesController.getInstance(BaseLocationAdapter.this.currentAccount).putUsers(res.users, false);
                        MessagesController.getInstance(BaseLocationAdapter.this.currentAccount).putChats(res.chats, false);
                        MessagesStorage.getInstance(BaseLocationAdapter.this.currentAccount).putUsersAndChats(res.users, res.chats, true, true);
                        Location coord = BaseLocationAdapter.this.lastSearchLocation;
                        BaseLocationAdapter.this.lastSearchLocation = null;
                        BaseLocationAdapter.this.searchPlacesWithQuery(BaseLocationAdapter.this.lastSearchQuery, coord, false);
                    }
                });
            }
        }
    }

    /* renamed from: org.telegram.ui.Adapters.BaseLocationAdapter$3 */
    class C09403 implements RequestDelegate {
        C09403() {
        }

        public void run(final TLObject response, final TL_error error) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    BaseLocationAdapter.this.currentRequestNum = 0;
                    BaseLocationAdapter.this.searching = false;
                    BaseLocationAdapter.this.places.clear();
                    BaseLocationAdapter.this.iconUrls.clear();
                    if (error == null) {
                        messages_BotResults res = response;
                        int size = res.results.size();
                        for (int a = 0; a < size; a++) {
                            BotInlineResult result = (BotInlineResult) res.results.get(a);
                            if ("venue".equals(result.type) && (result.send_message instanceof TL_botInlineMessageMediaVenue)) {
                                TL_botInlineMessageMediaVenue mediaVenue = result.send_message;
                                BaseLocationAdapter.this.iconUrls.add("https://ss3.4sqi.net/img/categories_v2/" + mediaVenue.venue_type + "_64.png");
                                TL_messageMediaVenue venue = new TL_messageMediaVenue();
                                venue.geo = mediaVenue.geo;
                                venue.address = mediaVenue.address;
                                venue.title = mediaVenue.title;
                                venue.venue_type = mediaVenue.venue_type;
                                venue.venue_id = mediaVenue.venue_id;
                                venue.provider = mediaVenue.provider;
                                BaseLocationAdapter.this.places.add(venue);
                            }
                        }
                    } else if (BaseLocationAdapter.this.delegate != null) {
                        BaseLocationAdapter.this.delegate.didLoadedSearchResult(BaseLocationAdapter.this.places);
                    }
                    BaseLocationAdapter.this.notifyDataSetChanged();
                }
            });
        }
    }

    public interface BaseLocationAdapterDelegate {
        void didLoadedSearchResult(ArrayList<TL_messageMediaVenue> arrayList);
    }

    public void destroy() {
        if (this.currentRequestNum != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.currentRequestNum, true);
            this.currentRequestNum = 0;
        }
    }

    public void setDelegate(long did, BaseLocationAdapterDelegate delegate) {
        this.dialogId = did;
        this.delegate = delegate;
    }

    public void searchDelayed(final String query, final Location coordinate) {
        if (query == null || query.length() == 0) {
            this.places.clear();
            notifyDataSetChanged();
            return;
        }
        try {
            if (this.searchTimer != null) {
                this.searchTimer.cancel();
            }
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
        this.searchTimer = new Timer();
        this.searchTimer.schedule(new TimerTask() {

            /* renamed from: org.telegram.ui.Adapters.BaseLocationAdapter$1$1 */
            class C09351 implements Runnable {
                C09351() {
                }

                public void run() {
                    BaseLocationAdapter.this.lastSearchLocation = null;
                    BaseLocationAdapter.this.searchPlacesWithQuery(query, coordinate, true);
                }
            }

            public void run() {
                try {
                    BaseLocationAdapter.this.searchTimer.cancel();
                    BaseLocationAdapter.this.searchTimer = null;
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
                AndroidUtilities.runOnUIThread(new C09351());
            }
        }, 200, 500);
    }

    private void searchBotUser() {
        if (!this.searchingUser) {
            this.searchingUser = true;
            TL_contacts_resolveUsername req = new TL_contacts_resolveUsername();
            req.username = MessagesController.getInstance(this.currentAccount).venueSearchBot;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new C09382());
        }
    }

    public void searchPlacesWithQuery(String query, Location coordinate, boolean searchUser) {
        if (coordinate == null) {
            return;
        }
        if (this.lastSearchLocation == null || coordinate.distanceTo(this.lastSearchLocation) >= 200.0f) {
            this.lastSearchLocation = coordinate;
            this.lastSearchQuery = query;
            if (this.searching) {
                this.searching = false;
                if (this.currentRequestNum != 0) {
                    ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.currentRequestNum, true);
                    this.currentRequestNum = 0;
                }
            }
            this.searching = true;
            TLObject object = MessagesController.getInstance(this.currentAccount).getUserOrChat(MessagesController.getInstance(this.currentAccount).venueSearchBot);
            if (object instanceof User) {
                User user = (User) object;
                TL_messages_getInlineBotResults req = new TL_messages_getInlineBotResults();
                if (query == null) {
                    query = TtmlNode.ANONYMOUS_REGION_ID;
                }
                req.query = query;
                req.bot = MessagesController.getInstance(this.currentAccount).getInputUser(user);
                req.offset = TtmlNode.ANONYMOUS_REGION_ID;
                req.geo_point = new TL_inputGeoPoint();
                req.geo_point.lat = coordinate.getLatitude();
                req.geo_point._long = coordinate.getLongitude();
                req.flags |= 1;
                int lower_id = (int) this.dialogId;
                int high_id = (int) (this.dialogId >> 32);
                if (lower_id != 0) {
                    req.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(lower_id);
                } else {
                    req.peer = new TL_inputPeerEmpty();
                }
                this.currentRequestNum = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new C09403());
                notifyDataSetChanged();
            } else if (searchUser) {
                searchBotUser();
            }
        }
    }
}
