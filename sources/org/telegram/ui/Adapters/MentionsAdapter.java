package org.telegram.ui.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Build;
import android.text.TextUtils;
import android.util.SparseArray;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$BotInfo;
import org.telegram.tgnet.TLRPC$BotInlineResult;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$TL_botInlineMessageMediaAuto;
import org.telegram.tgnet.TLRPC$TL_channelFull;
import org.telegram.tgnet.TLRPC$TL_chatBannedRights;
import org.telegram.tgnet.TLRPC$TL_contacts_resolveUsername;
import org.telegram.tgnet.TLRPC$TL_contacts_resolvedPeer;
import org.telegram.tgnet.TLRPC$TL_document;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_inlineBotSwitchPM;
import org.telegram.tgnet.TLRPC$TL_messages_botResults;
import org.telegram.tgnet.TLRPC$TL_photo;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.Adapters.MentionsAdapter;
import org.telegram.ui.Adapters.SearchAdapterHelper;
import org.telegram.ui.Cells.BotSwitchCell;
import org.telegram.ui.Cells.ContextLinkCell;
import org.telegram.ui.Cells.MentionCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.RecyclerListView;

public class MentionsAdapter extends RecyclerListView.SelectionAdapter {
    private SparseArray<TLRPC$BotInfo> botInfo;
    private int botsCount;
    private Runnable cancelDelayRunnable;
    /* access modifiers changed from: private */
    public int channelLastReqId;
    /* access modifiers changed from: private */
    public int channelReqId;
    private boolean contextMedia;
    private int contextQueryReqid;
    /* access modifiers changed from: private */
    public Runnable contextQueryRunnable;
    /* access modifiers changed from: private */
    public int contextUsernameReqid;
    /* access modifiers changed from: private */
    public int currentAccount = UserConfig.selectedAccount;
    /* access modifiers changed from: private */
    public MentionsAdapterDelegate delegate;
    private long dialog_id;
    /* access modifiers changed from: private */
    public TLRPC$User foundContextBot;
    private TLRPC$ChatFull info;
    private boolean inlineMediaEnabled = true;
    private boolean isDarkTheme;
    /* access modifiers changed from: private */
    public boolean isSearchingMentions;
    /* access modifiers changed from: private */
    public Location lastKnownLocation;
    /* access modifiers changed from: private */
    public int lastPosition;
    private String[] lastSearchKeyboardLanguage;
    /* access modifiers changed from: private */
    public String lastText;
    /* access modifiers changed from: private */
    public boolean lastUsernameOnly;
    private SendMessagesHelper.LocationProvider locationProvider = new SendMessagesHelper.LocationProvider(new SendMessagesHelper.LocationProvider.LocationProviderDelegate() {
        public void onLocationAcquired(Location location) {
            if (MentionsAdapter.this.foundContextBot != null && MentionsAdapter.this.foundContextBot.bot_inline_geo) {
                Location unused = MentionsAdapter.this.lastKnownLocation = location;
                MentionsAdapter mentionsAdapter = MentionsAdapter.this;
                mentionsAdapter.searchForContextBotResults(true, mentionsAdapter.foundContextBot, MentionsAdapter.this.searchingContextQuery, "");
            }
        }

        public void onUnableLocationAcquire() {
            MentionsAdapter.this.onLocationUnavailable();
        }
    }) {
        public void stop() {
            super.stop();
            Location unused = MentionsAdapter.this.lastKnownLocation = null;
        }
    };
    private Context mContext;
    /* access modifiers changed from: private */
    public ArrayList<MessageObject> messages;
    private boolean needBotContext = true;
    private boolean needUsernames = true;
    private String nextQueryOffset;
    /* access modifiers changed from: private */
    public boolean noUserName;
    private ChatActivity parentFragment;
    private int resultLength;
    private int resultStartPosition;
    private SearchAdapterHelper searchAdapterHelper;
    /* access modifiers changed from: private */
    public Runnable searchGlobalRunnable;
    private ArrayList<TLRPC$BotInlineResult> searchResultBotContext;
    private TLRPC$TL_inlineBotSwitchPM searchResultBotContextSwitch;
    private ArrayList<String> searchResultCommands;
    private ArrayList<String> searchResultCommandsHelp;
    private ArrayList<TLRPC$User> searchResultCommandsUsers;
    private ArrayList<String> searchResultHashtags;
    private ArrayList<MediaDataController.KeywordResult> searchResultSuggestions;
    /* access modifiers changed from: private */
    public ArrayList<TLObject> searchResultUsernames;
    /* access modifiers changed from: private */
    public SparseArray<TLObject> searchResultUsernamesMap;
    /* access modifiers changed from: private */
    public String searchingContextQuery;
    /* access modifiers changed from: private */
    public String searchingContextUsername;

    public interface MentionsAdapterDelegate {
        void needChangePanelVisibility(boolean z);

        void onContextClick(TLRPC$BotInlineResult tLRPC$BotInlineResult);

        void onContextSearch(boolean z);
    }

    static /* synthetic */ int access$1604(MentionsAdapter mentionsAdapter) {
        int i = mentionsAdapter.channelLastReqId + 1;
        mentionsAdapter.channelLastReqId = i;
        return i;
    }

    public MentionsAdapter(Context context, boolean z, long j, MentionsAdapterDelegate mentionsAdapterDelegate) {
        this.mContext = context;
        this.delegate = mentionsAdapterDelegate;
        this.isDarkTheme = z;
        this.dialog_id = j;
        SearchAdapterHelper searchAdapterHelper2 = new SearchAdapterHelper(true);
        this.searchAdapterHelper = searchAdapterHelper2;
        searchAdapterHelper2.setDelegate(new SearchAdapterHelper.SearchAdapterHelperDelegate() {
            public /* synthetic */ boolean canApplySearchResults(int i) {
                return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$canApplySearchResults(this, i);
            }

            public /* synthetic */ SparseArray getExcludeUsers() {
                return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$getExcludeUsers(this);
            }

            public void onDataSetChanged(int i) {
                MentionsAdapter.this.notifyDataSetChanged();
            }

            public void onSetHashtags(ArrayList<SearchAdapterHelper.HashtagObject> arrayList, HashMap<String, SearchAdapterHelper.HashtagObject> hashMap) {
                if (MentionsAdapter.this.lastText != null) {
                    MentionsAdapter mentionsAdapter = MentionsAdapter.this;
                    mentionsAdapter.searchUsernameOrHashtag(mentionsAdapter.lastText, MentionsAdapter.this.lastPosition, MentionsAdapter.this.messages, MentionsAdapter.this.lastUsernameOnly);
                }
            }
        });
    }

    public void onDestroy() {
        SendMessagesHelper.LocationProvider locationProvider2 = this.locationProvider;
        if (locationProvider2 != null) {
            locationProvider2.stop();
        }
        Runnable runnable = this.contextQueryRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
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

    public void setChatInfo(TLRPC$ChatFull tLRPC$ChatFull) {
        ChatActivity chatActivity;
        TLRPC$Chat currentChat;
        this.currentAccount = UserConfig.selectedAccount;
        this.info = tLRPC$ChatFull;
        if (!(this.inlineMediaEnabled || this.foundContextBot == null || (chatActivity = this.parentFragment) == null || (currentChat = chatActivity.getCurrentChat()) == null)) {
            boolean canSendStickers = ChatObject.canSendStickers(currentChat);
            this.inlineMediaEnabled = canSendStickers;
            if (canSendStickers) {
                this.searchResultUsernames = null;
                notifyDataSetChanged();
                this.delegate.needChangePanelVisibility(false);
                processFoundUser(this.foundContextBot);
            }
        }
        String str = this.lastText;
        if (str != null) {
            searchUsernameOrHashtag(str, this.lastPosition, this.messages, this.lastUsernameOnly);
        }
    }

    public void setNeedUsernames(boolean z) {
        this.needUsernames = z;
    }

    public void setNeedBotContext(boolean z) {
        this.needBotContext = z;
    }

    public void setBotInfo(SparseArray<TLRPC$BotInfo> sparseArray) {
        this.botInfo = sparseArray;
    }

    public void setBotsCount(int i) {
        this.botsCount = i;
    }

    public void clearRecentHashtags() {
        this.searchAdapterHelper.clearRecentHashtags();
        this.searchResultHashtags.clear();
        notifyDataSetChanged();
        MentionsAdapterDelegate mentionsAdapterDelegate = this.delegate;
        if (mentionsAdapterDelegate != null) {
            mentionsAdapterDelegate.needChangePanelVisibility(false);
        }
    }

    public TLRPC$TL_inlineBotSwitchPM getBotContextSwitch() {
        return this.searchResultBotContextSwitch;
    }

    public int getContextBotId() {
        TLRPC$User tLRPC$User = this.foundContextBot;
        if (tLRPC$User != null) {
            return tLRPC$User.id;
        }
        return 0;
    }

    public TLRPC$User getContextBotUser() {
        return this.foundContextBot;
    }

    public String getContextBotName() {
        TLRPC$User tLRPC$User = this.foundContextBot;
        return tLRPC$User != null ? tLRPC$User.username : "";
    }

    /* access modifiers changed from: private */
    public void processFoundUser(TLRPC$User tLRPC$User) {
        ChatActivity chatActivity;
        TLRPC$Chat currentChat;
        this.contextUsernameReqid = 0;
        this.locationProvider.stop();
        if (tLRPC$User == null || !tLRPC$User.bot || tLRPC$User.bot_inline_placeholder == null) {
            this.foundContextBot = null;
            this.inlineMediaEnabled = true;
        } else {
            this.foundContextBot = tLRPC$User;
            ChatActivity chatActivity2 = this.parentFragment;
            if (!(chatActivity2 == null || (currentChat = chatActivity2.getCurrentChat()) == null)) {
                boolean canSendStickers = ChatObject.canSendStickers(currentChat);
                this.inlineMediaEnabled = canSendStickers;
                if (!canSendStickers) {
                    notifyDataSetChanged();
                    this.delegate.needChangePanelVisibility(true);
                    return;
                }
            }
            if (this.foundContextBot.bot_inline_geo) {
                SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(this.currentAccount);
                if (notificationsSettings.getBoolean("inlinegeo_" + this.foundContextBot.id, false) || (chatActivity = this.parentFragment) == null || chatActivity.getParentActivity() == null) {
                    checkLocationPermissionsOrStart();
                } else {
                    TLRPC$User tLRPC$User2 = this.foundContextBot;
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.parentFragment.getParentActivity());
                    builder.setTitle(LocaleController.getString("ShareYouLocationTitle", NUM));
                    builder.setMessage(LocaleController.getString("ShareYouLocationInline", NUM));
                    boolean[] zArr = new boolean[1];
                    builder.setPositiveButton(LocaleController.getString("OK", NUM), new DialogInterface.OnClickListener(zArr, tLRPC$User2) {
                        public final /* synthetic */ boolean[] f$1;
                        public final /* synthetic */ TLRPC$User f$2;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void onClick(DialogInterface dialogInterface, int i) {
                            MentionsAdapter.this.lambda$processFoundUser$0$MentionsAdapter(this.f$1, this.f$2, dialogInterface, i);
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString("Cancel", NUM), new DialogInterface.OnClickListener(zArr) {
                        public final /* synthetic */ boolean[] f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void onClick(DialogInterface dialogInterface, int i) {
                            MentionsAdapter.this.lambda$processFoundUser$1$MentionsAdapter(this.f$1, dialogInterface, i);
                        }
                    });
                    this.parentFragment.showDialog(builder.create(), new DialogInterface.OnDismissListener(zArr) {
                        public final /* synthetic */ boolean[] f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void onDismiss(DialogInterface dialogInterface) {
                            MentionsAdapter.this.lambda$processFoundUser$2$MentionsAdapter(this.f$1, dialogInterface);
                        }
                    });
                }
            }
        }
        if (this.foundContextBot == null) {
            this.noUserName = true;
            return;
        }
        MentionsAdapterDelegate mentionsAdapterDelegate = this.delegate;
        if (mentionsAdapterDelegate != null) {
            mentionsAdapterDelegate.onContextSearch(true);
        }
        searchForContextBotResults(true, this.foundContextBot, this.searchingContextQuery, "");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$processFoundUser$0 */
    public /* synthetic */ void lambda$processFoundUser$0$MentionsAdapter(boolean[] zArr, TLRPC$User tLRPC$User, DialogInterface dialogInterface, int i) {
        zArr[0] = true;
        if (tLRPC$User != null) {
            SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(this.currentAccount).edit();
            edit.putBoolean("inlinegeo_" + tLRPC$User.id, true).commit();
            checkLocationPermissionsOrStart();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$processFoundUser$1 */
    public /* synthetic */ void lambda$processFoundUser$1$MentionsAdapter(boolean[] zArr, DialogInterface dialogInterface, int i) {
        zArr[0] = true;
        onLocationUnavailable();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$processFoundUser$2 */
    public /* synthetic */ void lambda$processFoundUser$2$MentionsAdapter(boolean[] zArr, DialogInterface dialogInterface) {
        if (!zArr[0]) {
            onLocationUnavailable();
        }
    }

    private void searchForContextBot(String str, String str2) {
        String str3;
        String str4;
        String str5;
        TLRPC$User tLRPC$User = this.foundContextBot;
        if (tLRPC$User == null || (str4 = tLRPC$User.username) == null || !str4.equals(str) || (str5 = this.searchingContextQuery) == null || !str5.equals(str2)) {
            this.searchResultBotContext = null;
            this.searchResultBotContextSwitch = null;
            notifyDataSetChanged();
            if (this.foundContextBot != null) {
                if (this.inlineMediaEnabled || str == null || str2 == null) {
                    this.delegate.needChangePanelVisibility(false);
                } else {
                    return;
                }
            }
            Runnable runnable = this.contextQueryRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.contextQueryRunnable = null;
            }
            if (TextUtils.isEmpty(str) || ((str3 = this.searchingContextUsername) != null && !str3.equals(str))) {
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
                MentionsAdapterDelegate mentionsAdapterDelegate = this.delegate;
                if (mentionsAdapterDelegate != null) {
                    mentionsAdapterDelegate.onContextSearch(false);
                }
                if (str == null || str.length() == 0) {
                    return;
                }
            }
            if (str2 == null) {
                if (this.contextQueryReqid != 0) {
                    ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.contextQueryReqid, true);
                    this.contextQueryReqid = 0;
                }
                this.searchingContextQuery = null;
                MentionsAdapterDelegate mentionsAdapterDelegate2 = this.delegate;
                if (mentionsAdapterDelegate2 != null) {
                    mentionsAdapterDelegate2.onContextSearch(false);
                    return;
                }
                return;
            }
            MentionsAdapterDelegate mentionsAdapterDelegate3 = this.delegate;
            if (mentionsAdapterDelegate3 != null) {
                if (this.foundContextBot != null) {
                    mentionsAdapterDelegate3.onContextSearch(true);
                } else if (str.equals("gif")) {
                    this.searchingContextUsername = "gif";
                    this.delegate.onContextSearch(false);
                }
            }
            final MessagesController instance = MessagesController.getInstance(this.currentAccount);
            final MessagesStorage instance2 = MessagesStorage.getInstance(this.currentAccount);
            this.searchingContextQuery = str2;
            final String str6 = str2;
            final String str7 = str;
            AnonymousClass4 r1 = new Runnable() {
                public void run() {
                    if (MentionsAdapter.this.contextQueryRunnable == this) {
                        Runnable unused = MentionsAdapter.this.contextQueryRunnable = null;
                        if (MentionsAdapter.this.foundContextBot == null && !MentionsAdapter.this.noUserName) {
                            String unused2 = MentionsAdapter.this.searchingContextUsername = str7;
                            TLObject userOrChat = instance.getUserOrChat(MentionsAdapter.this.searchingContextUsername);
                            if (userOrChat instanceof TLRPC$User) {
                                MentionsAdapter.this.processFoundUser((TLRPC$User) userOrChat);
                                return;
                            }
                            TLRPC$TL_contacts_resolveUsername tLRPC$TL_contacts_resolveUsername = new TLRPC$TL_contacts_resolveUsername();
                            tLRPC$TL_contacts_resolveUsername.username = MentionsAdapter.this.searchingContextUsername;
                            MentionsAdapter mentionsAdapter = MentionsAdapter.this;
                            int unused3 = mentionsAdapter.contextUsernameReqid = ConnectionsManager.getInstance(mentionsAdapter.currentAccount).sendRequest(tLRPC$TL_contacts_resolveUsername, new RequestDelegate(str7, instance, instance2) {
                                public final /* synthetic */ String f$1;
                                public final /* synthetic */ MessagesController f$2;
                                public final /* synthetic */ MessagesStorage f$3;

                                {
                                    this.f$1 = r2;
                                    this.f$2 = r3;
                                    this.f$3 = r4;
                                }

                                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                                    MentionsAdapter.AnonymousClass4.this.lambda$run$1$MentionsAdapter$4(this.f$1, this.f$2, this.f$3, tLObject, tLRPC$TL_error);
                                }
                            });
                        } else if (!MentionsAdapter.this.noUserName) {
                            MentionsAdapter mentionsAdapter2 = MentionsAdapter.this;
                            mentionsAdapter2.searchForContextBotResults(true, mentionsAdapter2.foundContextBot, str6, "");
                        }
                    }
                }

                /* access modifiers changed from: private */
                /* renamed from: lambda$run$1 */
                public /* synthetic */ void lambda$run$1$MentionsAdapter$4(String str, MessagesController messagesController, MessagesStorage messagesStorage, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    AndroidUtilities.runOnUIThread(new Runnable(str, tLRPC$TL_error, tLObject, messagesController, messagesStorage) {
                        public final /* synthetic */ String f$1;
                        public final /* synthetic */ TLRPC$TL_error f$2;
                        public final /* synthetic */ TLObject f$3;
                        public final /* synthetic */ MessagesController f$4;
                        public final /* synthetic */ MessagesStorage f$5;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                            this.f$3 = r4;
                            this.f$4 = r5;
                            this.f$5 = r6;
                        }

                        public final void run() {
                            MentionsAdapter.AnonymousClass4.this.lambda$null$0$MentionsAdapter$4(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
                        }
                    });
                }

                /* access modifiers changed from: private */
                /* renamed from: lambda$null$0 */
                public /* synthetic */ void lambda$null$0$MentionsAdapter$4(String str, TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, MessagesController messagesController, MessagesStorage messagesStorage) {
                    if (MentionsAdapter.this.searchingContextUsername != null && MentionsAdapter.this.searchingContextUsername.equals(str)) {
                        TLRPC$User tLRPC$User = null;
                        if (tLRPC$TL_error == null) {
                            TLRPC$TL_contacts_resolvedPeer tLRPC$TL_contacts_resolvedPeer = (TLRPC$TL_contacts_resolvedPeer) tLObject;
                            if (!tLRPC$TL_contacts_resolvedPeer.users.isEmpty()) {
                                TLRPC$User tLRPC$User2 = tLRPC$TL_contacts_resolvedPeer.users.get(0);
                                messagesController.putUser(tLRPC$User2, false);
                                messagesStorage.putUsersAndChats(tLRPC$TL_contacts_resolvedPeer.users, (ArrayList<TLRPC$Chat>) null, true, true);
                                tLRPC$User = tLRPC$User2;
                            }
                        }
                        MentionsAdapter.this.processFoundUser(tLRPC$User);
                    }
                }
            };
            this.contextQueryRunnable = r1;
            AndroidUtilities.runOnUIThread(r1, 400);
        }
    }

    /* access modifiers changed from: private */
    public void onLocationUnavailable() {
        TLRPC$User tLRPC$User = this.foundContextBot;
        if (tLRPC$User != null && tLRPC$User.bot_inline_geo) {
            Location location = new Location("network");
            this.lastKnownLocation = location;
            location.setLatitude(-1000.0d);
            this.lastKnownLocation.setLongitude(-1000.0d);
            searchForContextBotResults(true, this.foundContextBot, this.searchingContextQuery, "");
        }
    }

    private void checkLocationPermissionsOrStart() {
        ChatActivity chatActivity = this.parentFragment;
        if (chatActivity != null && chatActivity.getParentActivity() != null) {
            if (Build.VERSION.SDK_INT < 23 || this.parentFragment.getParentActivity().checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == 0) {
                TLRPC$User tLRPC$User = this.foundContextBot;
                if (tLRPC$User != null && tLRPC$User.bot_inline_geo) {
                    this.locationProvider.start();
                    return;
                }
                return;
            }
            this.parentFragment.getParentActivity().requestPermissions(new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}, 2);
        }
    }

    public void setSearchingMentions(boolean z) {
        this.isSearchingMentions = z;
    }

    public String getBotCaption() {
        TLRPC$User tLRPC$User = this.foundContextBot;
        if (tLRPC$User != null) {
            return tLRPC$User.bot_inline_placeholder;
        }
        String str = this.searchingContextUsername;
        if (str == null || !str.equals("gif")) {
            return null;
        }
        return "Search GIFs";
    }

    public void searchForContextBotForNextOffset() {
        String str;
        TLRPC$User tLRPC$User;
        String str2;
        if (this.contextQueryReqid == 0 && (str = this.nextQueryOffset) != null && str.length() != 0 && (tLRPC$User = this.foundContextBot) != null && (str2 = this.searchingContextQuery) != null) {
            searchForContextBotResults(true, tLRPC$User, str2, this.nextQueryOffset);
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x006a, code lost:
        r1 = r8.lastKnownLocation;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void searchForContextBotResults(boolean r17, org.telegram.tgnet.TLRPC$User r18, java.lang.String r19, java.lang.String r20) {
        /*
            r16 = this;
            r8 = r16
            r9 = r18
            r10 = r19
            r11 = r20
            int r0 = r8.contextQueryReqid
            r1 = 0
            r12 = 1
            if (r0 == 0) goto L_0x001b
            int r0 = r8.currentAccount
            org.telegram.tgnet.ConnectionsManager r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r0)
            int r2 = r8.contextQueryReqid
            r0.cancelRequest(r2, r12)
            r8.contextQueryReqid = r1
        L_0x001b:
            boolean r0 = r8.inlineMediaEnabled
            if (r0 != 0) goto L_0x0027
            org.telegram.ui.Adapters.MentionsAdapter$MentionsAdapterDelegate r0 = r8.delegate
            if (r0 == 0) goto L_0x0026
            r0.onContextSearch(r1)
        L_0x0026:
            return
        L_0x0027:
            if (r10 == 0) goto L_0x012a
            if (r9 != 0) goto L_0x002d
            goto L_0x012a
        L_0x002d:
            boolean r0 = r9.bot_inline_geo
            if (r0 == 0) goto L_0x0036
            android.location.Location r0 = r8.lastKnownLocation
            if (r0 != 0) goto L_0x0036
            return
        L_0x0036:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            long r1 = r8.dialog_id
            r0.append(r1)
            java.lang.String r1 = "_"
            r0.append(r1)
            r0.append(r10)
            r0.append(r1)
            r0.append(r11)
            r0.append(r1)
            long r2 = r8.dialog_id
            r0.append(r2)
            r0.append(r1)
            int r2 = r9.id
            r0.append(r2)
            r0.append(r1)
            boolean r1 = r9.bot_inline_geo
            r13 = -4571364728013586432(0xCLASSNAMEfNUM, double:-1000.0)
            if (r1 == 0) goto L_0x0088
            android.location.Location r1 = r8.lastKnownLocation
            if (r1 == 0) goto L_0x0088
            double r1 = r1.getLatitude()
            int r3 = (r1 > r13 ? 1 : (r1 == r13 ? 0 : -1))
            if (r3 == 0) goto L_0x0088
            android.location.Location r1 = r8.lastKnownLocation
            double r1 = r1.getLatitude()
            android.location.Location r3 = r8.lastKnownLocation
            double r3 = r3.getLongitude()
            double r1 = r1 + r3
            java.lang.Double r1 = java.lang.Double.valueOf(r1)
            goto L_0x008a
        L_0x0088:
            java.lang.String r1 = ""
        L_0x008a:
            r0.append(r1)
            java.lang.String r15 = r0.toString()
            int r0 = r8.currentAccount
            org.telegram.messenger.MessagesStorage r7 = org.telegram.messenger.MessagesStorage.getInstance(r0)
            org.telegram.ui.Adapters.-$$Lambda$MentionsAdapter$IL5-OQAQtFsbdezrCB664Fy_L14 r6 = new org.telegram.ui.Adapters.-$$Lambda$MentionsAdapter$IL5-OQAQtFsbdezrCB664Fy_L14
            r0 = r6
            r1 = r16
            r2 = r19
            r3 = r17
            r4 = r18
            r5 = r20
            r12 = r6
            r6 = r7
            r13 = r7
            r7 = r15
            r0.<init>(r2, r3, r4, r5, r6, r7)
            if (r17 == 0) goto L_0x00b2
            r13.getBotCache(r15, r12)
            goto L_0x0129
        L_0x00b2:
            org.telegram.tgnet.TLRPC$TL_messages_getInlineBotResults r0 = new org.telegram.tgnet.TLRPC$TL_messages_getInlineBotResults
            r0.<init>()
            int r1 = r8.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            org.telegram.tgnet.TLRPC$InputUser r1 = r1.getInputUser((org.telegram.tgnet.TLRPC$User) r9)
            r0.bot = r1
            r0.query = r10
            r0.offset = r11
            boolean r1 = r9.bot_inline_geo
            if (r1 == 0) goto L_0x0103
            android.location.Location r1 = r8.lastKnownLocation
            if (r1 == 0) goto L_0x0103
            double r1 = r1.getLatitude()
            r3 = -4571364728013586432(0xCLASSNAMEfNUM, double:-1000.0)
            int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r5 == 0) goto L_0x0103
            int r1 = r0.flags
            r2 = 1
            r1 = r1 | r2
            r0.flags = r1
            org.telegram.tgnet.TLRPC$TL_inputGeoPoint r1 = new org.telegram.tgnet.TLRPC$TL_inputGeoPoint
            r1.<init>()
            r0.geo_point = r1
            android.location.Location r2 = r8.lastKnownLocation
            double r2 = r2.getLatitude()
            double r2 = org.telegram.messenger.AndroidUtilities.fixLocationCoord(r2)
            r1.lat = r2
            org.telegram.tgnet.TLRPC$InputGeoPoint r1 = r0.geo_point
            android.location.Location r2 = r8.lastKnownLocation
            double r2 = r2.getLongitude()
            double r2 = org.telegram.messenger.AndroidUtilities.fixLocationCoord(r2)
            r1._long = r2
        L_0x0103:
            long r1 = r8.dialog_id
            int r2 = (int) r1
            if (r2 == 0) goto L_0x0115
            int r1 = r8.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            org.telegram.tgnet.TLRPC$InputPeer r1 = r1.getInputPeer((int) r2)
            r0.peer = r1
            goto L_0x011c
        L_0x0115:
            org.telegram.tgnet.TLRPC$TL_inputPeerEmpty r1 = new org.telegram.tgnet.TLRPC$TL_inputPeerEmpty
            r1.<init>()
            r0.peer = r1
        L_0x011c:
            int r1 = r8.currentAccount
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r1)
            r2 = 2
            int r0 = r1.sendRequest(r0, r12, r2)
            r8.contextQueryReqid = r0
        L_0x0129:
            return
        L_0x012a:
            r0 = 0
            r8.searchingContextQuery = r0
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.MentionsAdapter.searchForContextBotResults(boolean, org.telegram.tgnet.TLRPC$User, java.lang.String, java.lang.String):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$searchForContextBotResults$4 */
    public /* synthetic */ void lambda$searchForContextBotResults$4$MentionsAdapter(String str, boolean z, TLRPC$User tLRPC$User, String str2, MessagesStorage messagesStorage, String str3, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(str, z, tLObject, tLRPC$User, str2, messagesStorage, str3) {
            public final /* synthetic */ String f$1;
            public final /* synthetic */ boolean f$2;
            public final /* synthetic */ TLObject f$3;
            public final /* synthetic */ TLRPC$User f$4;
            public final /* synthetic */ String f$5;
            public final /* synthetic */ MessagesStorage f$6;
            public final /* synthetic */ String f$7;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
                this.f$6 = r7;
                this.f$7 = r8;
            }

            public final void run() {
                MentionsAdapter.this.lambda$null$3$MentionsAdapter(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$3 */
    public /* synthetic */ void lambda$null$3$MentionsAdapter(String str, boolean z, TLObject tLObject, TLRPC$User tLRPC$User, String str2, MessagesStorage messagesStorage, String str3) {
        boolean z2;
        if (str.equals(this.searchingContextQuery)) {
            boolean z3 = false;
            this.contextQueryReqid = 0;
            if (!z || tLObject != null) {
                MentionsAdapterDelegate mentionsAdapterDelegate = this.delegate;
                if (mentionsAdapterDelegate != null) {
                    mentionsAdapterDelegate.onContextSearch(false);
                }
            } else {
                searchForContextBotResults(false, tLRPC$User, str, str2);
            }
            if (tLObject instanceof TLRPC$TL_messages_botResults) {
                TLRPC$TL_messages_botResults tLRPC$TL_messages_botResults = (TLRPC$TL_messages_botResults) tLObject;
                if (!z && tLRPC$TL_messages_botResults.cache_time != 0) {
                    messagesStorage.saveBotCache(str3, tLRPC$TL_messages_botResults);
                }
                this.nextQueryOffset = tLRPC$TL_messages_botResults.next_offset;
                if (this.searchResultBotContextSwitch == null) {
                    this.searchResultBotContextSwitch = tLRPC$TL_messages_botResults.switch_pm;
                }
                int i = 0;
                while (i < tLRPC$TL_messages_botResults.results.size()) {
                    TLRPC$BotInlineResult tLRPC$BotInlineResult = tLRPC$TL_messages_botResults.results.get(i);
                    if (!(tLRPC$BotInlineResult.document instanceof TLRPC$TL_document) && !(tLRPC$BotInlineResult.photo instanceof TLRPC$TL_photo) && !"game".equals(tLRPC$BotInlineResult.type) && tLRPC$BotInlineResult.content == null && (tLRPC$BotInlineResult.send_message instanceof TLRPC$TL_botInlineMessageMediaAuto)) {
                        tLRPC$TL_messages_botResults.results.remove(i);
                        i--;
                    }
                    tLRPC$BotInlineResult.query_id = tLRPC$TL_messages_botResults.query_id;
                    i++;
                }
                if (this.searchResultBotContext == null || str2.length() == 0) {
                    this.searchResultBotContext = tLRPC$TL_messages_botResults.results;
                    this.contextMedia = tLRPC$TL_messages_botResults.gallery;
                    z2 = false;
                } else {
                    this.searchResultBotContext.addAll(tLRPC$TL_messages_botResults.results);
                    if (tLRPC$TL_messages_botResults.results.isEmpty()) {
                        this.nextQueryOffset = "";
                    }
                    z2 = true;
                }
                Runnable runnable = this.cancelDelayRunnable;
                if (runnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(runnable);
                    this.cancelDelayRunnable = null;
                }
                this.searchResultHashtags = null;
                this.searchResultUsernames = null;
                this.searchResultUsernamesMap = null;
                this.searchResultCommands = null;
                this.searchResultSuggestions = null;
                this.searchResultCommandsHelp = null;
                this.searchResultCommandsUsers = null;
                if (z2) {
                    int i2 = this.searchResultBotContextSwitch != null ? 1 : 0;
                    notifyItemChanged(((this.searchResultBotContext.size() - tLRPC$TL_messages_botResults.results.size()) + i2) - 1);
                    notifyItemRangeInserted((this.searchResultBotContext.size() - tLRPC$TL_messages_botResults.results.size()) + i2, tLRPC$TL_messages_botResults.results.size());
                } else {
                    notifyDataSetChanged();
                }
                MentionsAdapterDelegate mentionsAdapterDelegate2 = this.delegate;
                if (!this.searchResultBotContext.isEmpty() || this.searchResultBotContextSwitch != null) {
                    z3 = true;
                }
                mentionsAdapterDelegate2.needChangePanelVisibility(z3);
            }
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v5, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v26, resolved type: org.telegram.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v7, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v8, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:118:0x01ce  */
    /* JADX WARNING: Removed duplicated region for block: B:120:0x01d4  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void searchUsernameOrHashtag(java.lang.String r21, int r22, java.util.ArrayList<org.telegram.messenger.MessageObject> r23, boolean r24) {
        /*
            r20 = this;
            r8 = r20
            r0 = r21
            r1 = r22
            r2 = r23
            r3 = r24
            java.lang.Runnable r4 = r8.cancelDelayRunnable
            r5 = 0
            if (r4 == 0) goto L_0x0014
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r4)
            r8.cancelDelayRunnable = r5
        L_0x0014:
            int r4 = r8.channelReqId
            r6 = 0
            r7 = 1
            if (r4 == 0) goto L_0x0027
            int r4 = r8.currentAccount
            org.telegram.tgnet.ConnectionsManager r4 = org.telegram.tgnet.ConnectionsManager.getInstance(r4)
            int r9 = r8.channelReqId
            r4.cancelRequest(r9, r7)
            r8.channelReqId = r6
        L_0x0027:
            java.lang.Runnable r4 = r8.searchGlobalRunnable
            if (r4 == 0) goto L_0x0030
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r4)
            r8.searchGlobalRunnable = r5
        L_0x0030:
            boolean r4 = android.text.TextUtils.isEmpty(r21)
            if (r4 == 0) goto L_0x0041
            r8.searchForContextBot(r5, r5)
            org.telegram.ui.Adapters.MentionsAdapter$MentionsAdapterDelegate r0 = r8.delegate
            r0.needChangePanelVisibility(r6)
            r8.lastText = r5
            return
        L_0x0041:
            int r4 = r21.length()
            if (r4 <= 0) goto L_0x004a
            int r4 = r1 + -1
            goto L_0x004b
        L_0x004a:
            r4 = r1
        L_0x004b:
            r8.lastText = r5
            r8.lastUsernameOnly = r3
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            r10 = 64
            r11 = 3
            r12 = 32
            if (r3 != 0) goto L_0x00dd
            boolean r13 = r8.needBotContext
            if (r13 == 0) goto L_0x00dd
            char r13 = r0.charAt(r6)
            if (r13 != r10) goto L_0x00dd
            int r13 = r0.indexOf(r12)
            int r14 = r21.length()
            java.lang.String r15 = ""
            if (r13 <= 0) goto L_0x007b
            java.lang.String r14 = r0.substring(r7, r13)
            int r13 = r13 + r7
            java.lang.String r13 = r0.substring(r13)
            goto L_0x00a3
        L_0x007b:
            int r13 = r14 + -1
            char r13 = r0.charAt(r13)
            r10 = 116(0x74, float:1.63E-43)
            if (r13 != r10) goto L_0x009e
            int r10 = r14 + -2
            char r10 = r0.charAt(r10)
            r13 = 111(0x6f, float:1.56E-43)
            if (r10 != r13) goto L_0x009e
            int r14 = r14 - r11
            char r10 = r0.charAt(r14)
            r13 = 98
            if (r10 != r13) goto L_0x009e
            java.lang.String r14 = r0.substring(r7)
            r13 = r15
            goto L_0x00a3
        L_0x009e:
            r8.searchForContextBot(r5, r5)
            r13 = r5
            r14 = r13
        L_0x00a3:
            if (r14 == 0) goto L_0x00d9
            int r10 = r14.length()
            if (r10 < r7) goto L_0x00d9
            r10 = 1
        L_0x00ac:
            int r11 = r14.length()
            if (r10 >= r11) goto L_0x00d8
            char r11 = r14.charAt(r10)
            r12 = 48
            if (r11 < r12) goto L_0x00be
            r12 = 57
            if (r11 <= r12) goto L_0x00d3
        L_0x00be:
            r12 = 97
            if (r11 < r12) goto L_0x00c6
            r12 = 122(0x7a, float:1.71E-43)
            if (r11 <= r12) goto L_0x00d3
        L_0x00c6:
            r12 = 65
            if (r11 < r12) goto L_0x00ce
            r12 = 90
            if (r11 <= r12) goto L_0x00d3
        L_0x00ce:
            r12 = 95
            if (r11 == r12) goto L_0x00d3
            goto L_0x00d9
        L_0x00d3:
            int r10 = r10 + 1
            r12 = 32
            goto L_0x00ac
        L_0x00d8:
            r15 = r14
        L_0x00d9:
            r8.searchForContextBot(r15, r13)
            goto L_0x00e0
        L_0x00dd:
            r8.searchForContextBot(r5, r5)
        L_0x00e0:
            org.telegram.tgnet.TLRPC$User r10 = r8.foundContextBot
            if (r10 == 0) goto L_0x00e5
            return
        L_0x00e5:
            int r10 = r8.currentAccount
            org.telegram.messenger.MessagesController r10 = org.telegram.messenger.MessagesController.getInstance(r10)
            r12 = -1
            if (r3 == 0) goto L_0x0101
            java.lang.String r0 = r0.substring(r7)
            r9.append(r0)
            r8.resultStartPosition = r6
            int r0 = r9.length()
            r8.resultLength = r0
            r0 = 0
        L_0x00fe:
            r4 = -1
            goto L_0x01cc
        L_0x0101:
            if (r4 < 0) goto L_0x01c9
            int r13 = r21.length()
            if (r4 < r13) goto L_0x010d
            r5 = 64
            goto L_0x01c4
        L_0x010d:
            char r13 = r0.charAt(r4)
            r14 = 58
            if (r4 == 0) goto L_0x012e
            int r15 = r4 + -1
            char r11 = r0.charAt(r15)
            r5 = 32
            if (r11 == r5) goto L_0x012e
            char r5 = r0.charAt(r15)
            r11 = 10
            if (r5 == r11) goto L_0x012e
            if (r13 != r14) goto L_0x012a
            goto L_0x012e
        L_0x012a:
            r5 = 64
            goto L_0x01c1
        L_0x012e:
            r5 = 64
            if (r13 != r5) goto L_0x015a
            boolean r11 = r8.needUsernames
            if (r11 != 0) goto L_0x013c
            boolean r11 = r8.needBotContext
            if (r11 == 0) goto L_0x01c1
            if (r4 != 0) goto L_0x01c1
        L_0x013c:
            org.telegram.tgnet.TLRPC$ChatFull r5 = r8.info
            if (r5 != 0) goto L_0x014e
            if (r4 == 0) goto L_0x014e
            r8.lastText = r0
            r8.lastPosition = r1
            r8.messages = r2
            org.telegram.ui.Adapters.MentionsAdapter$MentionsAdapterDelegate r0 = r8.delegate
            r0.needChangePanelVisibility(r6)
            return
        L_0x014e:
            r8.resultStartPosition = r4
            int r0 = r9.length()
            int r0 = r0 + r7
            r8.resultLength = r0
            r0 = 0
            goto L_0x01cc
        L_0x015a:
            r11 = 35
            if (r13 != r11) goto L_0x0180
            org.telegram.ui.Adapters.SearchAdapterHelper r5 = r8.searchAdapterHelper
            boolean r5 = r5.loadRecentHashtags()
            if (r5 == 0) goto L_0x0174
            r8.resultStartPosition = r4
            int r0 = r9.length()
            int r0 = r0 + r7
            r8.resultLength = r0
            r9.insert(r6, r13)
            r0 = 1
            goto L_0x00fe
        L_0x0174:
            r8.lastText = r0
            r8.lastPosition = r1
            r8.messages = r2
            org.telegram.ui.Adapters.MentionsAdapter$MentionsAdapterDelegate r0 = r8.delegate
            r0.needChangePanelVisibility(r6)
            return
        L_0x0180:
            if (r4 != 0) goto L_0x0196
            android.util.SparseArray<org.telegram.tgnet.TLRPC$BotInfo> r11 = r8.botInfo
            if (r11 == 0) goto L_0x0196
            r11 = 47
            if (r13 != r11) goto L_0x0196
            r8.resultStartPosition = r4
            int r0 = r9.length()
            int r0 = r0 + r7
            r8.resultLength = r0
            r0 = 2
            goto L_0x00fe
        L_0x0196:
            if (r13 != r14) goto L_0x01c1
            int r11 = r9.length()
            if (r11 <= 0) goto L_0x01c1
            char r11 = r9.charAt(r6)
            java.lang.String r14 = " !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~\n"
            int r11 = r14.indexOf(r11)
            if (r11 < 0) goto L_0x01ac
            r11 = 1
            goto L_0x01ad
        L_0x01ac:
            r11 = 0
        L_0x01ad:
            if (r11 == 0) goto L_0x01b5
            int r11 = r9.length()
            if (r11 <= r7) goto L_0x01c1
        L_0x01b5:
            r8.resultStartPosition = r4
            int r0 = r9.length()
            int r0 = r0 + r7
            r8.resultLength = r0
            r0 = 3
            goto L_0x00fe
        L_0x01c1:
            r9.insert(r6, r13)
        L_0x01c4:
            int r4 = r4 + -1
            r5 = 0
            goto L_0x0101
        L_0x01c9:
            r0 = -1
            goto L_0x00fe
        L_0x01cc:
            if (r0 != r12) goto L_0x01d4
            org.telegram.ui.Adapters.MentionsAdapter$MentionsAdapterDelegate r0 = r8.delegate
            r0.needChangePanelVisibility(r6)
            return
        L_0x01d4:
            if (r0 != 0) goto L_0x03e1
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r1 = 0
        L_0x01dc:
            r5 = 100
            int r11 = r23.size()
            int r5 = java.lang.Math.min(r5, r11)
            if (r1 >= r5) goto L_0x0208
            java.lang.Object r5 = r2.get(r1)
            org.telegram.messenger.MessageObject r5 = (org.telegram.messenger.MessageObject) r5
            int r5 = r5.getFromChatId()
            if (r5 <= 0) goto L_0x0205
            java.lang.Integer r11 = java.lang.Integer.valueOf(r5)
            boolean r11 = r0.contains(r11)
            if (r11 != 0) goto L_0x0205
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r0.add(r5)
        L_0x0205:
            int r1 = r1 + 1
            goto L_0x01dc
        L_0x0208:
            java.lang.String r1 = r9.toString()
            java.lang.String r5 = r1.toLowerCase()
            r1 = 32
            int r1 = r5.indexOf(r1)
            if (r1 < 0) goto L_0x021a
            r1 = 1
            goto L_0x021b
        L_0x021a:
            r1 = 0
        L_0x021b:
            java.util.ArrayList r9 = new java.util.ArrayList
            r9.<init>()
            android.util.SparseArray r2 = new android.util.SparseArray
            r2.<init>()
            android.util.SparseArray r11 = new android.util.SparseArray
            r11.<init>()
            int r13 = r8.currentAccount
            org.telegram.messenger.MediaDataController r13 = org.telegram.messenger.MediaDataController.getInstance(r13)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_topPeer> r13 = r13.inlineBots
            r14 = 5
            if (r3 != 0) goto L_0x0296
            boolean r15 = r8.needBotContext
            if (r15 == 0) goto L_0x0296
            if (r4 != 0) goto L_0x0296
            boolean r4 = r13.isEmpty()
            if (r4 != 0) goto L_0x0296
            r4 = 0
            r15 = 0
        L_0x0243:
            int r6 = r13.size()
            if (r4 >= r6) goto L_0x0296
            java.lang.Object r6 = r13.get(r4)
            org.telegram.tgnet.TLRPC$TL_topPeer r6 = (org.telegram.tgnet.TLRPC$TL_topPeer) r6
            org.telegram.tgnet.TLRPC$Peer r6 = r6.peer
            int r6 = r6.user_id
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            org.telegram.tgnet.TLRPC$User r6 = r10.getUser(r6)
            if (r6 != 0) goto L_0x025e
            goto L_0x0292
        L_0x025e:
            java.lang.String r7 = r6.username
            if (r7 == 0) goto L_0x028f
            int r7 = r7.length()
            if (r7 <= 0) goto L_0x028f
            int r7 = r5.length()
            if (r7 <= 0) goto L_0x027a
            java.lang.String r7 = r6.username
            java.lang.String r7 = r7.toLowerCase()
            boolean r7 = r7.startsWith(r5)
            if (r7 != 0) goto L_0x0280
        L_0x027a:
            int r7 = r5.length()
            if (r7 != 0) goto L_0x028f
        L_0x0280:
            r9.add(r6)
            int r7 = r6.id
            r2.put(r7, r6)
            int r7 = r6.id
            r11.put(r7, r6)
            int r15 = r15 + 1
        L_0x028f:
            if (r15 != r14) goto L_0x0292
            goto L_0x0296
        L_0x0292:
            int r4 = r4 + 1
            r7 = 1
            goto L_0x0243
        L_0x0296:
            org.telegram.ui.ChatActivity r4 = r8.parentFragment
            if (r4 == 0) goto L_0x02a7
            org.telegram.tgnet.TLRPC$Chat r4 = r4.getCurrentChat()
            org.telegram.ui.ChatActivity r6 = r8.parentFragment
            int r6 = r6.getThreadId()
            r16 = r6
            goto L_0x02b9
        L_0x02a7:
            org.telegram.tgnet.TLRPC$ChatFull r4 = r8.info
            if (r4 == 0) goto L_0x02b6
            int r4 = r4.id
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            org.telegram.tgnet.TLRPC$Chat r4 = r10.getChat(r4)
            goto L_0x02b7
        L_0x02b6:
            r4 = 0
        L_0x02b7:
            r16 = 0
        L_0x02b9:
            if (r4 == 0) goto L_0x038c
            org.telegram.tgnet.TLRPC$ChatFull r6 = r8.info
            if (r6 == 0) goto L_0x038c
            org.telegram.tgnet.TLRPC$ChatParticipants r6 = r6.participants
            if (r6 == 0) goto L_0x038c
            boolean r6 = org.telegram.messenger.ChatObject.isChannel(r4)
            if (r6 == 0) goto L_0x02cd
            boolean r6 = r4.megagroup
            if (r6 == 0) goto L_0x038c
        L_0x02cd:
            r6 = -1
        L_0x02ce:
            org.telegram.tgnet.TLRPC$ChatFull r7 = r8.info
            org.telegram.tgnet.TLRPC$ChatParticipants r7 = r7.participants
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChatParticipant> r7 = r7.participants
            int r7 = r7.size()
            if (r6 >= r7) goto L_0x038c
            if (r6 != r12) goto L_0x02f5
            if (r4 != 0) goto L_0x02e0
            goto L_0x0386
        L_0x02e0:
            int r7 = r5.length()
            if (r7 != 0) goto L_0x02eb
            r9.add(r4)
            goto L_0x0386
        L_0x02eb:
            java.lang.String r7 = r4.title
            java.lang.String r13 = r4.username
            int r15 = r4.id
            int r15 = -r15
            r12 = r4
            r14 = 0
            goto L_0x0340
        L_0x02f5:
            org.telegram.tgnet.TLRPC$ChatFull r7 = r8.info
            org.telegram.tgnet.TLRPC$ChatParticipants r7 = r7.participants
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChatParticipant> r7 = r7.participants
            java.lang.Object r7 = r7.get(r6)
            org.telegram.tgnet.TLRPC$ChatParticipant r7 = (org.telegram.tgnet.TLRPC$ChatParticipant) r7
            int r7 = r7.user_id
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            org.telegram.tgnet.TLRPC$User r7 = r10.getUser(r7)
            if (r7 == 0) goto L_0x0386
            if (r3 != 0) goto L_0x0315
            boolean r13 = org.telegram.messenger.UserObject.isUserSelf(r7)
            if (r13 != 0) goto L_0x0386
        L_0x0315:
            int r13 = r7.id
            int r13 = r2.indexOfKey(r13)
            if (r13 < 0) goto L_0x031f
            goto L_0x0386
        L_0x031f:
            int r13 = r5.length()
            if (r13 != 0) goto L_0x032d
            boolean r13 = r7.deleted
            if (r13 != 0) goto L_0x032d
            r9.add(r7)
            goto L_0x0386
        L_0x032d:
            java.lang.String r13 = r7.first_name
            java.lang.String r15 = r7.last_name
            java.lang.String r12 = r7.username
            int r14 = r7.id
            r18 = r12
            r12 = r7
            r7 = r13
            r13 = r18
            r19 = r15
            r15 = r14
            r14 = r19
        L_0x0340:
            boolean r17 = android.text.TextUtils.isEmpty(r13)
            if (r17 != 0) goto L_0x0350
            java.lang.String r13 = r13.toLowerCase()
            boolean r13 = r13.startsWith(r5)
            if (r13 != 0) goto L_0x0380
        L_0x0350:
            boolean r13 = android.text.TextUtils.isEmpty(r7)
            if (r13 != 0) goto L_0x0360
            java.lang.String r13 = r7.toLowerCase()
            boolean r13 = r13.startsWith(r5)
            if (r13 != 0) goto L_0x0380
        L_0x0360:
            boolean r13 = android.text.TextUtils.isEmpty(r14)
            if (r13 != 0) goto L_0x0370
            java.lang.String r13 = r14.toLowerCase()
            boolean r13 = r13.startsWith(r5)
            if (r13 != 0) goto L_0x0380
        L_0x0370:
            if (r1 == 0) goto L_0x0386
            java.lang.String r7 = org.telegram.messenger.ContactsController.formatName(r7, r14)
            java.lang.String r7 = r7.toLowerCase()
            boolean r7 = r7.startsWith(r5)
            if (r7 == 0) goto L_0x0386
        L_0x0380:
            r9.add(r12)
            r11.put(r15, r12)
        L_0x0386:
            int r6 = r6 + 1
            r12 = -1
            r14 = 5
            goto L_0x02ce
        L_0x038c:
            org.telegram.ui.Adapters.MentionsAdapter$5 r1 = new org.telegram.ui.Adapters.MentionsAdapter$5
            r1.<init>(r8, r11, r0)
            java.util.Collections.sort(r9, r1)
            r0 = 0
            r8.searchResultHashtags = r0
            r8.searchResultCommands = r0
            r8.searchResultCommandsHelp = r0
            r8.searchResultCommandsUsers = r0
            r8.searchResultSuggestions = r0
            if (r4 == 0) goto L_0x03db
            boolean r0 = r4.megagroup
            if (r0 == 0) goto L_0x03db
            int r0 = r5.length()
            if (r0 <= 0) goto L_0x03db
            int r0 = r9.size()
            r1 = 5
            if (r0 >= r1) goto L_0x03bf
            org.telegram.ui.Adapters.-$$Lambda$MentionsAdapter$pvr9tzj2yJuJolzxxTPD9oiIQ_8 r0 = new org.telegram.ui.Adapters.-$$Lambda$MentionsAdapter$pvr9tzj2yJuJolzxxTPD9oiIQ_8
            r0.<init>(r9, r11)
            r8.cancelDelayRunnable = r0
            r1 = 1000(0x3e8, double:4.94E-321)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0, r1)
            goto L_0x03c3
        L_0x03bf:
            r0 = 1
            r8.showUsersResult(r9, r11, r0)
        L_0x03c3:
            org.telegram.ui.Adapters.MentionsAdapter$6 r12 = new org.telegram.ui.Adapters.MentionsAdapter$6
            r0 = r12
            r1 = r20
            r2 = r4
            r3 = r5
            r4 = r16
            r5 = r9
            r6 = r11
            r7 = r10
            r0.<init>(r2, r3, r4, r5, r6, r7)
            r8.searchGlobalRunnable = r12
            r0 = 200(0xc8, double:9.9E-322)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r12, r0)
            goto L_0x04fa
        L_0x03db:
            r1 = 1
            r8.showUsersResult(r9, r11, r1)
            goto L_0x04fa
        L_0x03e1:
            r1 = 1
            if (r0 != r1) goto L_0x0437
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            java.lang.String r1 = r9.toString()
            java.lang.String r1 = r1.toLowerCase()
            org.telegram.ui.Adapters.SearchAdapterHelper r2 = r8.searchAdapterHelper
            java.util.ArrayList r2 = r2.getHashtags()
            r6 = 0
        L_0x03f8:
            int r3 = r2.size()
            if (r6 >= r3) goto L_0x0418
            java.lang.Object r3 = r2.get(r6)
            org.telegram.ui.Adapters.SearchAdapterHelper$HashtagObject r3 = (org.telegram.ui.Adapters.SearchAdapterHelper.HashtagObject) r3
            if (r3 == 0) goto L_0x0415
            java.lang.String r4 = r3.hashtag
            if (r4 == 0) goto L_0x0415
            boolean r4 = r4.startsWith(r1)
            if (r4 == 0) goto L_0x0415
            java.lang.String r3 = r3.hashtag
            r0.add(r3)
        L_0x0415:
            int r6 = r6 + 1
            goto L_0x03f8
        L_0x0418:
            r8.searchResultHashtags = r0
            r1 = 0
            r8.searchResultUsernames = r1
            r8.searchResultUsernamesMap = r1
            r8.searchResultCommands = r1
            r8.searchResultCommandsHelp = r1
            r8.searchResultCommandsUsers = r1
            r8.searchResultSuggestions = r1
            r20.notifyDataSetChanged()
            org.telegram.ui.Adapters.MentionsAdapter$MentionsAdapterDelegate r1 = r8.delegate
            boolean r0 = r0.isEmpty()
            r2 = 1
            r0 = r0 ^ r2
            r1.needChangePanelVisibility(r0)
            goto L_0x04fa
        L_0x0437:
            r1 = 2
            if (r0 != r1) goto L_0x04cb
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            java.lang.String r3 = r9.toString()
            java.lang.String r3 = r3.toLowerCase()
            r4 = 0
        L_0x0452:
            android.util.SparseArray<org.telegram.tgnet.TLRPC$BotInfo> r5 = r8.botInfo
            int r5 = r5.size()
            if (r4 >= r5) goto L_0x04ad
            android.util.SparseArray<org.telegram.tgnet.TLRPC$BotInfo> r5 = r8.botInfo
            java.lang.Object r5 = r5.valueAt(r4)
            org.telegram.tgnet.TLRPC$BotInfo r5 = (org.telegram.tgnet.TLRPC$BotInfo) r5
            r6 = 0
        L_0x0463:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_botCommand> r7 = r5.commands
            int r7 = r7.size()
            if (r6 >= r7) goto L_0x04aa
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_botCommand> r7 = r5.commands
            java.lang.Object r7 = r7.get(r6)
            org.telegram.tgnet.TLRPC$TL_botCommand r7 = (org.telegram.tgnet.TLRPC$TL_botCommand) r7
            if (r7 == 0) goto L_0x04a7
            java.lang.String r9 = r7.command
            if (r9 == 0) goto L_0x04a7
            boolean r9 = r9.startsWith(r3)
            if (r9 == 0) goto L_0x04a7
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r11 = "/"
            r9.append(r11)
            java.lang.String r11 = r7.command
            r9.append(r11)
            java.lang.String r9 = r9.toString()
            r0.add(r9)
            java.lang.String r7 = r7.description
            r1.add(r7)
            int r7 = r5.user_id
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            org.telegram.tgnet.TLRPC$User r7 = r10.getUser(r7)
            r2.add(r7)
        L_0x04a7:
            int r6 = r6 + 1
            goto L_0x0463
        L_0x04aa:
            int r4 = r4 + 1
            goto L_0x0452
        L_0x04ad:
            r4 = 0
            r8.searchResultHashtags = r4
            r8.searchResultUsernames = r4
            r8.searchResultUsernamesMap = r4
            r8.searchResultSuggestions = r4
            r8.searchResultCommands = r0
            r8.searchResultCommandsHelp = r1
            r8.searchResultCommandsUsers = r2
            r20.notifyDataSetChanged()
            org.telegram.ui.Adapters.MentionsAdapter$MentionsAdapterDelegate r1 = r8.delegate
            boolean r0 = r0.isEmpty()
            r2 = 1
            r0 = r0 ^ r2
            r1.needChangePanelVisibility(r0)
            goto L_0x04fa
        L_0x04cb:
            r1 = 3
            if (r0 != r1) goto L_0x04fa
            java.lang.String[] r0 = org.telegram.messenger.AndroidUtilities.getCurrentKeyboardLanguage()
            java.lang.String[] r1 = r8.lastSearchKeyboardLanguage
            boolean r1 = java.util.Arrays.equals(r0, r1)
            if (r1 != 0) goto L_0x04e3
            int r1 = r8.currentAccount
            org.telegram.messenger.MediaDataController r1 = org.telegram.messenger.MediaDataController.getInstance(r1)
            r1.fetchNewEmojiKeywords(r0)
        L_0x04e3:
            r8.lastSearchKeyboardLanguage = r0
            int r0 = r8.currentAccount
            org.telegram.messenger.MediaDataController r0 = org.telegram.messenger.MediaDataController.getInstance(r0)
            java.lang.String[] r1 = r8.lastSearchKeyboardLanguage
            java.lang.String r2 = r9.toString()
            org.telegram.ui.Adapters.-$$Lambda$MentionsAdapter$BPDwlLrrTYvBx2slW-DPtWy21rg r3 = new org.telegram.ui.Adapters.-$$Lambda$MentionsAdapter$BPDwlLrrTYvBx2slW-DPtWy21rg
            r3.<init>()
            r4 = 0
            r0.getEmojiSuggestions(r1, r2, r4, r3)
        L_0x04fa:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.MentionsAdapter.searchUsernameOrHashtag(java.lang.String, int, java.util.ArrayList, boolean):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$searchUsernameOrHashtag$5 */
    public /* synthetic */ void lambda$searchUsernameOrHashtag$5$MentionsAdapter(ArrayList arrayList, SparseArray sparseArray) {
        this.cancelDelayRunnable = null;
        showUsersResult(arrayList, sparseArray, true);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$searchUsernameOrHashtag$6 */
    public /* synthetic */ void lambda$searchUsernameOrHashtag$6$MentionsAdapter(ArrayList arrayList, String str) {
        this.searchResultSuggestions = arrayList;
        this.searchResultHashtags = null;
        this.searchResultUsernames = null;
        this.searchResultUsernamesMap = null;
        this.searchResultCommands = null;
        this.searchResultCommandsHelp = null;
        this.searchResultCommandsUsers = null;
        notifyDataSetChanged();
        MentionsAdapterDelegate mentionsAdapterDelegate = this.delegate;
        ArrayList<MediaDataController.KeywordResult> arrayList2 = this.searchResultSuggestions;
        mentionsAdapterDelegate.needChangePanelVisibility(arrayList2 != null && !arrayList2.isEmpty());
    }

    /* access modifiers changed from: private */
    public void showUsersResult(ArrayList<TLObject> arrayList, SparseArray<TLObject> sparseArray, boolean z) {
        this.searchResultUsernames = arrayList;
        this.searchResultUsernamesMap = sparseArray;
        Runnable runnable = this.cancelDelayRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.cancelDelayRunnable = null;
        }
        if (z) {
            notifyDataSetChanged();
            this.delegate.needChangePanelVisibility(!this.searchResultUsernames.isEmpty());
        }
    }

    public int getResultStartPosition() {
        return this.resultStartPosition;
    }

    public int getResultLength() {
        return this.resultLength;
    }

    public ArrayList<TLRPC$BotInlineResult> getSearchResultBotContext() {
        return this.searchResultBotContext;
    }

    public int getItemCount() {
        int i = 1;
        if (this.foundContextBot != null && !this.inlineMediaEnabled) {
            return 1;
        }
        ArrayList<TLRPC$BotInlineResult> arrayList = this.searchResultBotContext;
        if (arrayList != null) {
            int size = arrayList.size();
            if (this.searchResultBotContextSwitch == null) {
                i = 0;
            }
            return size + i;
        }
        ArrayList<TLObject> arrayList2 = this.searchResultUsernames;
        if (arrayList2 != null) {
            return arrayList2.size();
        }
        ArrayList<String> arrayList3 = this.searchResultHashtags;
        if (arrayList3 != null) {
            return arrayList3.size();
        }
        ArrayList<String> arrayList4 = this.searchResultCommands;
        if (arrayList4 != null) {
            return arrayList4.size();
        }
        ArrayList<MediaDataController.KeywordResult> arrayList5 = this.searchResultSuggestions;
        if (arrayList5 != null) {
            return arrayList5.size();
        }
        return 0;
    }

    public int getItemViewType(int i) {
        if (this.foundContextBot != null && !this.inlineMediaEnabled) {
            return 3;
        }
        if (this.searchResultBotContext != null) {
            return (i != 0 || this.searchResultBotContextSwitch == null) ? 1 : 2;
        }
        return 0;
    }

    public void addHashtagsFromMessage(CharSequence charSequence) {
        this.searchAdapterHelper.addHashtagsFromMessage(charSequence);
    }

    public int getItemPosition(int i) {
        return (this.searchResultBotContext == null || this.searchResultBotContextSwitch == null) ? i : i - 1;
    }

    public Object getItem(int i) {
        ArrayList<TLRPC$BotInlineResult> arrayList = this.searchResultBotContext;
        if (arrayList != null) {
            TLRPC$TL_inlineBotSwitchPM tLRPC$TL_inlineBotSwitchPM = this.searchResultBotContextSwitch;
            if (tLRPC$TL_inlineBotSwitchPM != null) {
                if (i == 0) {
                    return tLRPC$TL_inlineBotSwitchPM;
                }
                i--;
            }
            if (i < 0 || i >= arrayList.size()) {
                return null;
            }
            return this.searchResultBotContext.get(i);
        }
        ArrayList<TLObject> arrayList2 = this.searchResultUsernames;
        if (arrayList2 == null) {
            ArrayList<String> arrayList3 = this.searchResultHashtags;
            if (arrayList3 == null) {
                ArrayList<MediaDataController.KeywordResult> arrayList4 = this.searchResultSuggestions;
                if (arrayList4 == null) {
                    ArrayList<String> arrayList5 = this.searchResultCommands;
                    if (arrayList5 == null || i < 0 || i >= arrayList5.size()) {
                        return null;
                    }
                    ArrayList<TLRPC$User> arrayList6 = this.searchResultCommandsUsers;
                    if (arrayList6 == null || (this.botsCount == 1 && !(this.info instanceof TLRPC$TL_channelFull))) {
                        return this.searchResultCommands.get(i);
                    }
                    if (arrayList6.get(i) != null) {
                        Object[] objArr = new Object[2];
                        objArr[0] = this.searchResultCommands.get(i);
                        objArr[1] = this.searchResultCommandsUsers.get(i) != null ? this.searchResultCommandsUsers.get(i).username : "";
                        return String.format("%s@%s", objArr);
                    }
                    return String.format("%s", new Object[]{this.searchResultCommands.get(i)});
                } else if (i < 0 || i >= arrayList4.size()) {
                    return null;
                } else {
                    return this.searchResultSuggestions.get(i);
                }
            } else if (i < 0 || i >= arrayList3.size()) {
                return null;
            } else {
                return this.searchResultHashtags.get(i);
            }
        } else if (i < 0 || i >= arrayList2.size()) {
            return null;
        } else {
            return this.searchResultUsernames.get(i);
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
        return this.foundContextBot != null && !this.inlineMediaEnabled;
    }

    public boolean isMediaLayout() {
        return this.contextMedia;
    }

    public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
        return this.foundContextBot == null || this.inlineMediaEnabled;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreateViewHolder$7 */
    public /* synthetic */ void lambda$onCreateViewHolder$7$MentionsAdapter(ContextLinkCell contextLinkCell) {
        this.delegate.onContextClick(contextLinkCell.getResult());
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v1, resolved type: org.telegram.ui.Cells.MentionCell} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v3, resolved type: org.telegram.ui.Cells.ContextLinkCell} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v6, resolved type: org.telegram.ui.Cells.MentionCell} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v7, resolved type: org.telegram.ui.Cells.MentionCell} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v8, resolved type: org.telegram.ui.Cells.BotSwitchCell} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v9, resolved type: android.widget.TextView} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r5, int r6) {
        /*
            r4 = this;
            if (r6 == 0) goto L_0x004c
            r5 = 1
            if (r6 == r5) goto L_0x003c
            r0 = 2
            if (r6 == r0) goto L_0x0034
            android.widget.TextView r6 = new android.widget.TextView
            android.content.Context r0 = r4.mContext
            r6.<init>(r0)
            r0 = 1090519040(0x41000000, float:8.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r6.setPadding(r1, r2, r3, r0)
            r0 = 1096810496(0x41600000, float:14.0)
            r6.setTextSize(r5, r0)
            java.lang.String r5 = "windowBackgroundWhiteGrayText2"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r6.setTextColor(r5)
            goto L_0x0058
        L_0x0034:
            org.telegram.ui.Cells.BotSwitchCell r6 = new org.telegram.ui.Cells.BotSwitchCell
            android.content.Context r5 = r4.mContext
            r6.<init>(r5)
            goto L_0x0058
        L_0x003c:
            org.telegram.ui.Cells.ContextLinkCell r6 = new org.telegram.ui.Cells.ContextLinkCell
            android.content.Context r5 = r4.mContext
            r6.<init>(r5)
            org.telegram.ui.Adapters.-$$Lambda$MentionsAdapter$Rs_gCeAnt0gqLkmAQV_mZgJj8Js r5 = new org.telegram.ui.Adapters.-$$Lambda$MentionsAdapter$Rs_gCeAnt0gqLkmAQV_mZgJj8Js
            r5.<init>()
            r6.setDelegate(r5)
            goto L_0x0058
        L_0x004c:
            org.telegram.ui.Cells.MentionCell r6 = new org.telegram.ui.Cells.MentionCell
            android.content.Context r5 = r4.mContext
            r6.<init>(r5)
            boolean r5 = r4.isDarkTheme
            r6.setIsDarkTheme(r5)
        L_0x0058:
            org.telegram.ui.Components.RecyclerListView$Holder r5 = new org.telegram.ui.Components.RecyclerListView$Holder
            r5.<init>(r6)
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.MentionsAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
    }

    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights;
        if (viewHolder.getItemViewType() == 3) {
            TextView textView = (TextView) viewHolder.itemView;
            TLRPC$Chat currentChat = this.parentFragment.getCurrentChat();
            if (currentChat == null) {
                return;
            }
            if (!ChatObject.hasAdminRights(currentChat) && (tLRPC$TL_chatBannedRights = currentChat.default_banned_rights) != null && tLRPC$TL_chatBannedRights.send_inline) {
                textView.setText(LocaleController.getString("GlobalAttachInlineRestricted", NUM));
            } else if (AndroidUtilities.isBannedForever(currentChat.banned_rights)) {
                textView.setText(LocaleController.getString("AttachInlineRestrictedForever", NUM));
            } else {
                textView.setText(LocaleController.formatString("AttachInlineRestricted", NUM, LocaleController.formatDateForBan((long) currentChat.banned_rights.until_date)));
            }
        } else if (this.searchResultBotContext != null) {
            boolean z = this.searchResultBotContextSwitch != null;
            if (viewHolder.getItemViewType() != 2) {
                if (z) {
                    i--;
                }
                ((ContextLinkCell) viewHolder.itemView).setLink(this.searchResultBotContext.get(i), this.foundContextBot, this.contextMedia, i != this.searchResultBotContext.size() - 1, z && i == 0, "gif".equals(this.searchingContextUsername));
            } else if (z) {
                ((BotSwitchCell) viewHolder.itemView).setText(this.searchResultBotContextSwitch.text);
            }
        } else {
            ArrayList<TLObject> arrayList = this.searchResultUsernames;
            if (arrayList != null) {
                TLObject tLObject = arrayList.get(i);
                if (tLObject instanceof TLRPC$User) {
                    ((MentionCell) viewHolder.itemView).setUser((TLRPC$User) tLObject);
                } else if (tLObject instanceof TLRPC$Chat) {
                    ((MentionCell) viewHolder.itemView).setChat((TLRPC$Chat) tLObject);
                }
            } else {
                ArrayList<String> arrayList2 = this.searchResultHashtags;
                if (arrayList2 != null) {
                    ((MentionCell) viewHolder.itemView).setText(arrayList2.get(i));
                    return;
                }
                ArrayList<MediaDataController.KeywordResult> arrayList3 = this.searchResultSuggestions;
                if (arrayList3 != null) {
                    ((MentionCell) viewHolder.itemView).setEmojiSuggestion(arrayList3.get(i));
                    return;
                }
                ArrayList<String> arrayList4 = this.searchResultCommands;
                if (arrayList4 != null) {
                    MentionCell mentionCell = (MentionCell) viewHolder.itemView;
                    String str = arrayList4.get(i);
                    String str2 = this.searchResultCommandsHelp.get(i);
                    ArrayList<TLRPC$User> arrayList5 = this.searchResultCommandsUsers;
                    mentionCell.setBotCommand(str, str2, arrayList5 != null ? arrayList5.get(i) : null);
                }
            }
        }
    }

    public void onRequestPermissionsResultFragment(int i, String[] strArr, int[] iArr) {
        TLRPC$User tLRPC$User;
        if (i == 2 && (tLRPC$User = this.foundContextBot) != null && tLRPC$User.bot_inline_geo) {
            if (iArr.length <= 0 || iArr[0] != 0) {
                onLocationUnavailable();
            } else {
                this.locationProvider.start();
            }
        }
    }
}
