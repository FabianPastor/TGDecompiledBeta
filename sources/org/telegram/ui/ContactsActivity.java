package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.util.Property;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import androidx.collection.LongSparseArray;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SecretChatHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Adapters.ContactsAdapter;
import org.telegram.ui.Adapters.SearchAdapter;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.LetterSectionCell;
import org.telegram.ui.Cells.ProfileSearchCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.StickerEmptyView;

public class ContactsActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private boolean allowBots = true;
    private boolean allowSelf = true;
    private boolean allowUsernameSearch = true;
    /* access modifiers changed from: private */
    public int animationIndex = -1;
    private boolean askAboutContacts = true;
    /* access modifiers changed from: private */
    public AnimatorSet bounceIconAnimator;
    private long channelId;
    private long chatId;
    private boolean checkPermission = true;
    private boolean createSecretChat;
    private boolean creatingChat;
    private ContactsActivityDelegate delegate;
    private boolean destroyAfterSelect;
    private boolean disableSections;
    /* access modifiers changed from: private */
    public StickerEmptyView emptyView;
    /* access modifiers changed from: private */
    public RLottieImageView floatingButton;
    /* access modifiers changed from: private */
    public FrameLayout floatingButtonContainer;
    /* access modifiers changed from: private */
    public boolean floatingHidden;
    private AccelerateDecelerateInterpolator floatingInterpolator = new AccelerateDecelerateInterpolator();
    private boolean hasGps;
    private LongSparseArray<TLRPC$User> ignoreUsers;
    private String initialSearchString;
    /* access modifiers changed from: private */
    public LinearLayoutManager layoutManager;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    /* access modifiers changed from: private */
    public ContactsAdapter listViewAdapter;
    private boolean needFinishFragment = true;
    private boolean needForwardCount = true;
    /* access modifiers changed from: private */
    public boolean needPhonebook;
    private boolean onlyUsers;
    private AlertDialog permissionDialog;
    private long permissionRequestTime;
    /* access modifiers changed from: private */
    public int prevPosition;
    /* access modifiers changed from: private */
    public int prevTop;
    private boolean resetDelegate = true;
    private boolean returnAsResult;
    /* access modifiers changed from: private */
    public boolean scrollUpdated;
    /* access modifiers changed from: private */
    public SearchAdapter searchListViewAdapter;
    /* access modifiers changed from: private */
    public boolean searchWas;
    /* access modifiers changed from: private */
    public boolean searching;
    private String selectAlertString = null;
    /* access modifiers changed from: private */
    public boolean sortByName;
    /* access modifiers changed from: private */
    public ActionBarMenuItem sortItem;

    public interface ContactsActivityDelegate {
        void didSelectContact(TLRPC$User tLRPC$User, String str, ContactsActivity contactsActivity);
    }

    public ContactsActivity(Bundle bundle) {
        super(bundle);
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.contactsDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.encryptedChatCreated);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.closeChats);
        this.checkPermission = UserConfig.getInstance(this.currentAccount).syncContacts;
        Bundle bundle = this.arguments;
        if (bundle != null) {
            this.onlyUsers = bundle.getBoolean("onlyUsers", false);
            this.destroyAfterSelect = this.arguments.getBoolean("destroyAfterSelect", false);
            this.returnAsResult = this.arguments.getBoolean("returnAsResult", false);
            this.createSecretChat = this.arguments.getBoolean("createSecretChat", false);
            this.selectAlertString = this.arguments.getString("selectAlertString");
            this.allowUsernameSearch = this.arguments.getBoolean("allowUsernameSearch", true);
            this.needForwardCount = this.arguments.getBoolean("needForwardCount", true);
            this.allowBots = this.arguments.getBoolean("allowBots", true);
            this.allowSelf = this.arguments.getBoolean("allowSelf", true);
            this.channelId = this.arguments.getLong("channelId", 0);
            this.needFinishFragment = this.arguments.getBoolean("needFinishFragment", true);
            this.chatId = this.arguments.getLong("chat_id", 0);
            this.disableSections = this.arguments.getBoolean("disableSections", false);
            this.resetDelegate = this.arguments.getBoolean("resetDelegate", false);
        } else {
            this.needPhonebook = true;
        }
        if (!this.createSecretChat && !this.returnAsResult) {
            this.sortByName = SharedConfig.sortContactsByName;
        }
        getContactsController().checkInviteText();
        getContactsController().reloadContactsStatusesMaybe();
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.contactsDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.encryptedChatCreated);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
        this.delegate = null;
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
        getNotificationCenter().onAnimationFinish(this.animationIndex);
    }

    /* access modifiers changed from: protected */
    public void onTransitionAnimationProgress(boolean z, float f) {
        super.onTransitionAnimationProgress(z, f);
        View view = this.fragmentView;
        if (view != null) {
            view.invalidate();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:38:0x0141  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x0149  */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x020e  */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x0211  */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x0217  */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x021a  */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x0224  */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x022c  */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x022f  */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x0235  */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x0238  */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x0273  */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x02c4  */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x02c8  */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x02e0  */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x0343  */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x0346  */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x034a  */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x034d  */
    /* JADX WARNING: Removed duplicated region for block: B:86:0x0367  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.view.View createView(android.content.Context r24) {
        /*
            r23 = this;
            r11 = r23
            r12 = r24
            r13 = 0
            r11.searching = r13
            r11.searchWas = r13
            org.telegram.ui.ActionBar.ActionBar r0 = r11.actionBar
            r1 = 2131165487(0x7var_f, float:1.7945193E38)
            r0.setBackButtonImage(r1)
            org.telegram.ui.ActionBar.ActionBar r0 = r11.actionBar
            r14 = 1
            r0.setAllowOverlayTitle(r14)
            boolean r0 = r11.destroyAfterSelect
            if (r0 == 0) goto L_0x0050
            boolean r0 = r11.returnAsResult
            if (r0 == 0) goto L_0x002e
            org.telegram.ui.ActionBar.ActionBar r0 = r11.actionBar
            r1 = 2131627753(0x7f0e0ee9, float:1.888278E38)
            java.lang.String r2 = "SelectContact"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            goto L_0x005e
        L_0x002e:
            boolean r0 = r11.createSecretChat
            if (r0 == 0) goto L_0x0041
            org.telegram.ui.ActionBar.ActionBar r0 = r11.actionBar
            r1 = 2131626501(0x7f0e0a05, float:1.888024E38)
            java.lang.String r2 = "NewSecretChat"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            goto L_0x005e
        L_0x0041:
            org.telegram.ui.ActionBar.ActionBar r0 = r11.actionBar
            r1 = 2131626491(0x7f0e09fb, float:1.888022E38)
            java.lang.String r2 = "NewMessageTitle"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            goto L_0x005e
        L_0x0050:
            org.telegram.ui.ActionBar.ActionBar r0 = r11.actionBar
            r1 = 2131625080(0x7f0e0478, float:1.8877358E38)
            java.lang.String r2 = "Contacts"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
        L_0x005e:
            org.telegram.ui.ActionBar.ActionBar r0 = r11.actionBar
            org.telegram.ui.ContactsActivity$1 r1 = new org.telegram.ui.ContactsActivity$1
            r1.<init>()
            r0.setActionBarMenuOnItemClick(r1)
            org.telegram.ui.ActionBar.ActionBar r0 = r11.actionBar
            org.telegram.ui.ActionBar.ActionBarMenu r0 = r0.createMenu()
            r1 = 2131165497(0x7var_, float:1.7945213E38)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.addItem((int) r13, (int) r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r1.setIsSearchField(r14)
            org.telegram.ui.ContactsActivity$2 r2 = new org.telegram.ui.ContactsActivity$2
            r2.<init>()
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r1.setActionBarMenuItemSearchListener(r2)
            java.lang.String r2 = "Search"
            r3 = 2131627684(0x7f0e0ea4, float:1.888264E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r2, r3)
            r1.setSearchFieldHint(r4)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)
            r1.setContentDescription(r2)
            boolean r1 = r11.createSecretChat
            if (r1 != 0) goto L_0x00ba
            boolean r1 = r11.returnAsResult
            if (r1 != 0) goto L_0x00ba
            boolean r1 = r11.sortByName
            if (r1 == 0) goto L_0x00a5
            r1 = 2131165388(0x7var_cc, float:1.7944992E38)
            goto L_0x00a8
        L_0x00a5:
            r1 = 2131165387(0x7var_cb, float:1.794499E38)
        L_0x00a8:
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r0.addItem((int) r14, (int) r1)
            r11.sortItem = r0
            r1 = 2131623969(0x7f0e0021, float:1.8875104E38)
            java.lang.String r2 = "AccDescrContactSorting"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setContentDescription(r1)
        L_0x00ba:
            org.telegram.ui.ContactsActivity$3 r15 = new org.telegram.ui.ContactsActivity$3
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$User> r3 = r11.ignoreUsers
            boolean r4 = r11.allowUsernameSearch
            r5 = 0
            r6 = 0
            boolean r7 = r11.allowBots
            boolean r8 = r11.allowSelf
            r9 = 1
            r10 = 0
            r0 = r15
            r1 = r23
            r2 = r24
            r0.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10)
            r11.searchListViewAdapter = r15
            long r0 = r11.chatId
            r2 = 0
            r8 = 3
            r9 = 2
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 == 0) goto L_0x00f0
            org.telegram.messenger.MessagesController r0 = r23.getMessagesController()
            long r1 = r11.chatId
            java.lang.Long r1 = java.lang.Long.valueOf(r1)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r1)
            boolean r0 = org.telegram.messenger.ChatObject.canUserDoAdminAction(r0, r8)
        L_0x00ee:
            r10 = r0
            goto L_0x0117
        L_0x00f0:
            long r0 = r11.channelId
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 == 0) goto L_0x0116
            org.telegram.messenger.MessagesController r0 = r23.getMessagesController()
            long r1 = r11.channelId
            java.lang.Long r1 = java.lang.Long.valueOf(r1)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r1)
            boolean r1 = org.telegram.messenger.ChatObject.canUserDoAdminAction(r0, r8)
            if (r1 == 0) goto L_0x0114
            java.lang.String r0 = r0.username
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x0114
            r0 = 2
            goto L_0x00ee
        L_0x0114:
            r0 = 0
            goto L_0x00ee
        L_0x0116:
            r10 = 0
        L_0x0117:
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0126 }
            android.content.pm.PackageManager r0 = r0.getPackageManager()     // Catch:{ all -> 0x0126 }
            java.lang.String r1 = "android.hardware.location.gps"
            boolean r0 = r0.hasSystemFeature(r1)     // Catch:{ all -> 0x0126 }
            r11.hasGps = r0     // Catch:{ all -> 0x0126 }
            goto L_0x0128
        L_0x0126:
            r11.hasGps = r13
        L_0x0128:
            org.telegram.ui.ContactsActivity$4 r15 = new org.telegram.ui.ContactsActivity$4
            boolean r3 = r11.onlyUsers
            boolean r4 = r11.needPhonebook
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$User> r5 = r11.ignoreUsers
            boolean r7 = r11.hasGps
            r0 = r15
            r1 = r23
            r2 = r24
            r6 = r10
            r0.<init>(r2, r3, r4, r5, r6, r7)
            r11.listViewAdapter = r15
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r11.sortItem
            if (r0 == 0) goto L_0x0149
            boolean r0 = r11.sortByName
            if (r0 == 0) goto L_0x0147
            r0 = 1
            goto L_0x014a
        L_0x0147:
            r0 = 2
            goto L_0x014a
        L_0x0149:
            r0 = 0
        L_0x014a:
            r15.setSortType(r0, r13)
            org.telegram.ui.Adapters.ContactsAdapter r0 = r11.listViewAdapter
            boolean r1 = r11.disableSections
            r0.setDisableSections(r1)
            org.telegram.ui.ContactsActivity$5 r0 = new org.telegram.ui.ContactsActivity$5
            r0.<init>(r12)
            r11.fragmentView = r0
            android.widget.FrameLayout r0 = (android.widget.FrameLayout) r0
            org.telegram.ui.Components.FlickerLoadingView r1 = new org.telegram.ui.Components.FlickerLoadingView
            r1.<init>(r12)
            r2 = 6
            r1.setViewType(r2)
            r1.showDate(r13)
            org.telegram.ui.Components.StickerEmptyView r2 = new org.telegram.ui.Components.StickerEmptyView
            r2.<init>(r12, r1, r14)
            r11.emptyView = r2
            r2.addView(r1, r13)
            org.telegram.ui.Components.StickerEmptyView r1 = r11.emptyView
            r1.setAnimateLayoutChange(r14)
            org.telegram.ui.Components.StickerEmptyView r1 = r11.emptyView
            r1.showProgress(r14, r13)
            org.telegram.ui.Components.StickerEmptyView r1 = r11.emptyView
            android.widget.TextView r1 = r1.title
            r2 = 2131626565(0x7f0e0a45, float:1.888037E38)
            java.lang.String r3 = "NoResult"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            org.telegram.ui.Components.StickerEmptyView r1 = r11.emptyView
            android.widget.TextView r1 = r1.subtitle
            r2 = 2131627689(0x7f0e0ea9, float:1.888265E38)
            java.lang.String r3 = "SearchEmptyViewFilteredSubtitle2"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            org.telegram.ui.Components.StickerEmptyView r1 = r11.emptyView
            r2 = -1
            r3 = -1082130432(0xffffffffbvar_, float:-1.0)
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r2, r3)
            r0.addView(r1, r4)
            org.telegram.ui.ContactsActivity$6 r1 = new org.telegram.ui.ContactsActivity$6
            r1.<init>(r12)
            r11.listView = r1
            r1.setSectionsType(r14)
            org.telegram.ui.Components.RecyclerListView r1 = r11.listView
            r1.setVerticalScrollBarEnabled(r13)
            org.telegram.ui.Components.RecyclerListView r1 = r11.listView
            r1.setFastScrollEnabled(r13)
            org.telegram.ui.Components.RecyclerListView r1 = r11.listView
            androidx.recyclerview.widget.LinearLayoutManager r4 = new androidx.recyclerview.widget.LinearLayoutManager
            r4.<init>(r12, r14, r13)
            r11.layoutManager = r4
            r1.setLayoutManager(r4)
            org.telegram.ui.Components.RecyclerListView r1 = r11.listView
            org.telegram.ui.Adapters.ContactsAdapter r4 = r11.listViewAdapter
            r1.setAdapter(r4)
            org.telegram.ui.Components.RecyclerListView r1 = r11.listView
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r2, r3)
            r0.addView(r1, r2)
            org.telegram.ui.Components.RecyclerListView r1 = r11.listView
            org.telegram.ui.Components.StickerEmptyView r2 = r11.emptyView
            r1.setEmptyView(r2)
            org.telegram.ui.Components.RecyclerListView r1 = r11.listView
            r1.setAnimateEmptyView(r14, r13)
            org.telegram.ui.Components.RecyclerListView r1 = r11.listView
            org.telegram.ui.ContactsActivity$$ExternalSyntheticLambda9 r2 = new org.telegram.ui.ContactsActivity$$ExternalSyntheticLambda9
            r2.<init>(r11, r10)
            r1.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r2)
            org.telegram.ui.Components.RecyclerListView r1 = r11.listView
            org.telegram.ui.ContactsActivity$7 r2 = new org.telegram.ui.ContactsActivity$7
            r2.<init>()
            r1.setOnScrollListener(r2)
            boolean r1 = r11.createSecretChat
            if (r1 != 0) goto L_0x0363
            boolean r1 = r11.returnAsResult
            if (r1 != 0) goto L_0x0363
            android.widget.FrameLayout r1 = new android.widget.FrameLayout
            r1.<init>(r12)
            r11.floatingButtonContainer = r1
            int r2 = android.os.Build.VERSION.SDK_INT
            r5 = 21
            if (r2 < r5) goto L_0x0211
            r6 = 56
            goto L_0x0213
        L_0x0211:
            r6 = 60
        L_0x0213:
            int r15 = r6 + 20
            if (r2 < r5) goto L_0x021a
            r6 = 56
            goto L_0x021c
        L_0x021a:
            r6 = 60
        L_0x021c:
            int r6 = r6 + 20
            float r6 = (float) r6
            boolean r7 = org.telegram.messenger.LocaleController.isRTL
            if (r7 == 0) goto L_0x0224
            goto L_0x0225
        L_0x0224:
            r8 = 5
        L_0x0225:
            r17 = r8 | 80
            r8 = 0
            r10 = 1082130432(0x40800000, float:4.0)
            if (r7 == 0) goto L_0x022f
            r18 = 1082130432(0x40800000, float:4.0)
            goto L_0x0231
        L_0x022f:
            r18 = 0
        L_0x0231:
            r19 = 0
            if (r7 == 0) goto L_0x0238
            r20 = 0
            goto L_0x023a
        L_0x0238:
            r20 = 1082130432(0x40800000, float:4.0)
        L_0x023a:
            r21 = 0
            r16 = r6
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r15, r16, r17, r18, r19, r20, r21)
            r0.addView(r1, r6)
            android.widget.FrameLayout r0 = r11.floatingButtonContainer
            org.telegram.ui.ContactsActivity$$ExternalSyntheticLambda4 r1 = new org.telegram.ui.ContactsActivity$$ExternalSyntheticLambda4
            r1.<init>(r11)
            r0.setOnClickListener(r1)
            org.telegram.ui.Components.RLottieImageView r0 = new org.telegram.ui.Components.RLottieImageView
            r0.<init>(r12)
            r11.floatingButton = r0
            android.widget.ImageView$ScaleType r1 = android.widget.ImageView.ScaleType.CENTER
            r0.setScaleType(r1)
            r0 = 1113587712(0x42600000, float:56.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r0)
            java.lang.String r6 = "chats_actionBackground"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            java.lang.String r7 = "chats_actionPressedBackground"
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.createSimpleSelectorCircleDrawable(r1, r6, r7)
            if (r2 >= r5) goto L_0x029f
            android.content.res.Resources r6 = r24.getResources()
            r7 = 2131165437(0x7var_fd, float:1.7945091E38)
            android.graphics.drawable.Drawable r6 = r6.getDrawable(r7)
            android.graphics.drawable.Drawable r6 = r6.mutate()
            android.graphics.PorterDuffColorFilter r7 = new android.graphics.PorterDuffColorFilter
            r8 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            android.graphics.PorterDuff$Mode r12 = android.graphics.PorterDuff.Mode.MULTIPLY
            r7.<init>(r8, r12)
            r6.setColorFilter(r7)
            org.telegram.ui.Components.CombinedDrawable r7 = new org.telegram.ui.Components.CombinedDrawable
            r7.<init>(r6, r1, r13, r13)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r7.setIconSize(r1, r0)
            r1 = r7
        L_0x029f:
            org.telegram.ui.Components.RLottieImageView r0 = r11.floatingButton
            r0.setBackgroundDrawable(r1)
            org.telegram.ui.Components.RLottieImageView r0 = r11.floatingButton
            android.graphics.PorterDuffColorFilter r1 = new android.graphics.PorterDuffColorFilter
            java.lang.String r6 = "chats_actionIcon"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            android.graphics.PorterDuff$Mode r7 = android.graphics.PorterDuff.Mode.MULTIPLY
            r1.<init>(r6, r7)
            r0.setColorFilter(r1)
            android.content.SharedPreferences r0 = org.telegram.messenger.MessagesController.getGlobalMainSettings()
            java.lang.String r1 = "view_animations"
            boolean r0 = r0.getBoolean(r1, r14)
            org.telegram.ui.Components.RLottieImageView r1 = r11.floatingButton
            if (r0 == 0) goto L_0x02c8
            r0 = 2131558556(0x7f0d009c, float:1.8742431E38)
            goto L_0x02cb
        L_0x02c8:
            r0 = 2131558557(0x7f0d009d, float:1.8742433E38)
        L_0x02cb:
            r6 = 52
            r1.setAnimation(r0, r6, r6)
            android.widget.FrameLayout r0 = r11.floatingButtonContainer
            r1 = 2131625111(0x7f0e0497, float:1.887742E38)
            java.lang.String r6 = "CreateNewContact"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r6, r1)
            r0.setContentDescription(r1)
            if (r2 < r5) goto L_0x033d
            android.animation.StateListAnimator r0 = new android.animation.StateListAnimator
            r0.<init>()
            int[] r1 = new int[r14]
            r6 = 16842919(0x10100a7, float:2.3694026E-38)
            r1[r13] = r6
            org.telegram.ui.Components.RLottieImageView r6 = r11.floatingButton
            android.util.Property r7 = android.view.View.TRANSLATION_Z
            float[] r8 = new float[r9]
            r12 = 1073741824(0x40000000, float:2.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r15 = (float) r15
            r8[r13] = r15
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r15 = (float) r15
            r8[r14] = r15
            android.animation.ObjectAnimator r6 = android.animation.ObjectAnimator.ofFloat(r6, r7, r8)
            r3 = 200(0xc8, double:9.9E-322)
            android.animation.ObjectAnimator r6 = r6.setDuration(r3)
            r0.addState(r1, r6)
            int[] r1 = new int[r13]
            org.telegram.ui.Components.RLottieImageView r6 = r11.floatingButton
            float[] r9 = new float[r9]
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r10 = (float) r10
            r9[r13] = r10
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r10 = (float) r10
            r9[r14] = r10
            android.animation.ObjectAnimator r6 = android.animation.ObjectAnimator.ofFloat(r6, r7, r9)
            android.animation.ObjectAnimator r3 = r6.setDuration(r3)
            r0.addState(r1, r3)
            org.telegram.ui.Components.RLottieImageView r1 = r11.floatingButton
            r1.setStateListAnimator(r0)
            org.telegram.ui.Components.RLottieImageView r0 = r11.floatingButton
            org.telegram.ui.ContactsActivity$8 r1 = new org.telegram.ui.ContactsActivity$8
            r1.<init>(r11)
            r0.setOutlineProvider(r1)
        L_0x033d:
            android.widget.FrameLayout r0 = r11.floatingButtonContainer
            org.telegram.ui.Components.RLottieImageView r1 = r11.floatingButton
            if (r2 < r5) goto L_0x0346
            r16 = 56
            goto L_0x0348
        L_0x0346:
            r16 = 60
        L_0x0348:
            if (r2 < r5) goto L_0x034d
            r3 = 56
            goto L_0x034f
        L_0x034d:
            r3 = 60
        L_0x034f:
            float r2 = (float) r3
            r18 = 51
            r19 = 1092616192(0x41200000, float:10.0)
            r20 = 1086324736(0x40CLASSNAME, float:6.0)
            r21 = 1092616192(0x41200000, float:10.0)
            r22 = 0
            r17 = r2
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22)
            r0.addView(r1, r2)
        L_0x0363:
            java.lang.String r0 = r11.initialSearchString
            if (r0 == 0) goto L_0x036f
            org.telegram.ui.ActionBar.ActionBar r1 = r11.actionBar
            r1.openSearchField(r0, r13)
            r0 = 0
            r11.initialSearchString = r0
        L_0x036f:
            android.view.View r0 = r11.fragmentView
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ContactsActivity.createView(android.content.Context):android.view.View");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$1(int i, View view, int i2) {
        Activity parentActivity;
        RecyclerView.Adapter adapter = this.listView.getAdapter();
        SearchAdapter searchAdapter = this.searchListViewAdapter;
        boolean z = false;
        boolean z2 = true;
        if (adapter == searchAdapter) {
            Object item = searchAdapter.getItem(i2);
            if (item instanceof TLRPC$User) {
                TLRPC$User tLRPC$User = (TLRPC$User) item;
                if (this.searchListViewAdapter.isGlobalSearch(i2)) {
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(tLRPC$User);
                    getMessagesController().putUsers(arrayList, false);
                    MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(arrayList, (ArrayList<TLRPC$Chat>) null, false, true);
                }
                if (this.returnAsResult) {
                    LongSparseArray<TLRPC$User> longSparseArray = this.ignoreUsers;
                    if (longSparseArray == null || longSparseArray.indexOfKey(tLRPC$User.id) < 0) {
                        didSelectResult(tLRPC$User, true, (String) null);
                    }
                } else if (!this.createSecretChat) {
                    Bundle bundle = new Bundle();
                    bundle.putLong("user_id", tLRPC$User.id);
                    if (getMessagesController().checkCanOpenChat(bundle, this)) {
                        presentFragment(new ChatActivity(bundle), true);
                    }
                } else if (tLRPC$User.id != UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                    this.creatingChat = true;
                    SecretChatHelper.getInstance(this.currentAccount).startSecretChat(getParentActivity(), tLRPC$User);
                }
            } else if (item instanceof String) {
                String str = (String) item;
                if (!str.equals("section")) {
                    NewContactActivity newContactActivity = new NewContactActivity();
                    newContactActivity.setInitialPhoneNumber(str, true);
                    presentFragment(newContactActivity);
                }
            }
        } else {
            int sectionForPosition = this.listViewAdapter.getSectionForPosition(i2);
            int positionInSectionForPosition = this.listViewAdapter.getPositionInSectionForPosition(i2);
            if (positionInSectionForPosition >= 0 && sectionForPosition >= 0) {
                if ((this.onlyUsers && i == 0) || sectionForPosition != 0) {
                    Object item2 = this.listViewAdapter.getItem(sectionForPosition, positionInSectionForPosition);
                    if (item2 instanceof TLRPC$User) {
                        TLRPC$User tLRPC$User2 = (TLRPC$User) item2;
                        if (this.returnAsResult) {
                            LongSparseArray<TLRPC$User> longSparseArray2 = this.ignoreUsers;
                            if (longSparseArray2 == null || longSparseArray2.indexOfKey(tLRPC$User2.id) < 0) {
                                didSelectResult(tLRPC$User2, true, (String) null);
                            }
                        } else if (this.createSecretChat) {
                            this.creatingChat = true;
                            SecretChatHelper.getInstance(this.currentAccount).startSecretChat(getParentActivity(), tLRPC$User2);
                        } else {
                            Bundle bundle2 = new Bundle();
                            bundle2.putLong("user_id", tLRPC$User2.id);
                            if (getMessagesController().checkCanOpenChat(bundle2, this)) {
                                presentFragment(new ChatActivity(bundle2), true);
                            }
                        }
                    } else if (item2 instanceof ContactsController.Contact) {
                        ContactsController.Contact contact = (ContactsController.Contact) item2;
                        String str2 = !contact.phones.isEmpty() ? contact.phones.get(0) : null;
                        if (str2 != null && getParentActivity() != null) {
                            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                            builder.setMessage(LocaleController.getString("InviteUser", NUM));
                            builder.setTitle(LocaleController.getString("AppName", NUM));
                            builder.setPositiveButton(LocaleController.getString("OK", NUM), new ContactsActivity$$ExternalSyntheticLambda1(this, str2));
                            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                            showDialog(builder.create());
                        }
                    }
                } else if (this.needPhonebook) {
                    if (positionInSectionForPosition == 0) {
                        presentFragment(new InviteContactsActivity());
                    } else if (positionInSectionForPosition == 1 && this.hasGps) {
                        int i3 = Build.VERSION.SDK_INT;
                        if (i3 < 23 || (parentActivity = getParentActivity()) == null || parentActivity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == 0) {
                            if (i3 >= 28) {
                                z2 = ((LocationManager) ApplicationLoader.applicationContext.getSystemService("location")).isLocationEnabled();
                            } else if (i3 >= 19) {
                                try {
                                    if (Settings.Secure.getInt(ApplicationLoader.applicationContext.getContentResolver(), "location_mode", 0) != 0) {
                                        z = true;
                                    }
                                    z2 = z;
                                } catch (Throwable th) {
                                    FileLog.e(th);
                                }
                            }
                            if (!z2) {
                                presentFragment(new ActionIntroActivity(4));
                            } else {
                                presentFragment(new PeopleNearbyActivity());
                            }
                        } else {
                            presentFragment(new ActionIntroActivity(1));
                        }
                    }
                } else if (i != 0) {
                    if (positionInSectionForPosition == 0) {
                        long j = this.chatId;
                        if (j == 0) {
                            j = this.channelId;
                        }
                        presentFragment(new GroupInviteActivity(j));
                    }
                } else if (positionInSectionForPosition == 0) {
                    presentFragment(new GroupCreateActivity(new Bundle()), false);
                } else if (positionInSectionForPosition == 1) {
                    Bundle bundle3 = new Bundle();
                    bundle3.putBoolean("onlyUsers", true);
                    bundle3.putBoolean("destroyAfterSelect", true);
                    bundle3.putBoolean("createSecretChat", true);
                    bundle3.putBoolean("allowBots", false);
                    bundle3.putBoolean("allowSelf", false);
                    presentFragment(new ContactsActivity(bundle3), false);
                } else if (positionInSectionForPosition == 2) {
                    SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
                    if (BuildVars.DEBUG_VERSION || !globalMainSettings.getBoolean("channel_intro", false)) {
                        presentFragment(new ActionIntroActivity(0));
                        globalMainSettings.edit().putBoolean("channel_intro", true).commit();
                        return;
                    }
                    Bundle bundle4 = new Bundle();
                    bundle4.putInt("step", 0);
                    presentFragment(new ChannelCreateActivity(bundle4));
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$0(String str, DialogInterface dialogInterface, int i) {
        try {
            Intent intent = new Intent("android.intent.action.VIEW", Uri.fromParts("sms", str, (String) null));
            intent.putExtra("sms_body", ContactsController.getInstance(this.currentAccount).getInviteText(1));
            getParentActivity().startActivityForResult(intent, 500);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$2(View view) {
        presentFragment(new NewContactActivity());
    }

    private void didSelectResult(TLRPC$User tLRPC$User, boolean z, String str) {
        final EditTextBoldCursor editTextBoldCursor;
        if (!z || this.selectAlertString == null) {
            ContactsActivityDelegate contactsActivityDelegate = this.delegate;
            if (contactsActivityDelegate != null) {
                contactsActivityDelegate.didSelectContact(tLRPC$User, str, this);
                if (this.resetDelegate) {
                    this.delegate = null;
                }
            }
            if (this.needFinishFragment) {
                finishFragment();
            }
        } else if (getParentActivity() != null) {
            if (tLRPC$User.bot) {
                if (tLRPC$User.bot_nochats) {
                    try {
                        BulletinFactory.of(this).createErrorBulletin(LocaleController.getString("BotCantJoinGroups", NUM)).show();
                        return;
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                        return;
                    }
                } else if (this.channelId != 0) {
                    TLRPC$Chat chat = getMessagesController().getChat(Long.valueOf(this.channelId));
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                    if (ChatObject.canAddAdmins(chat)) {
                        builder.setTitle(LocaleController.getString("AppName", NUM));
                        builder.setMessage(LocaleController.getString("AddBotAsAdmin", NUM));
                        builder.setPositiveButton(LocaleController.getString("MakeAdmin", NUM), new ContactsActivity$$ExternalSyntheticLambda3(this, tLRPC$User, str));
                        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                    } else {
                        builder.setMessage(LocaleController.getString("CantAddBotAsAdmin", NUM));
                        builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
                    }
                    showDialog(builder.create());
                    return;
                }
            }
            AlertDialog.Builder builder2 = new AlertDialog.Builder((Context) getParentActivity());
            builder2.setTitle(LocaleController.getString("AppName", NUM));
            String formatStringSimple = LocaleController.formatStringSimple(this.selectAlertString, UserObject.getUserName(tLRPC$User));
            if (tLRPC$User.bot || !this.needForwardCount) {
                editTextBoldCursor = null;
            } else {
                formatStringSimple = String.format("%s\n\n%s", new Object[]{formatStringSimple, LocaleController.getString("AddToTheGroupForwardCount", NUM)});
                editTextBoldCursor = new EditTextBoldCursor(getParentActivity());
                editTextBoldCursor.setTextSize(1, 18.0f);
                editTextBoldCursor.setText("50");
                editTextBoldCursor.setTextColor(Theme.getColor("dialogTextBlack"));
                editTextBoldCursor.setGravity(17);
                editTextBoldCursor.setInputType(2);
                editTextBoldCursor.setImeOptions(6);
                editTextBoldCursor.setBackgroundDrawable(Theme.createEditTextDrawable(getParentActivity(), true));
                editTextBoldCursor.addTextChangedListener(new TextWatcher(this) {
                    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    }

                    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    }

                    public void afterTextChanged(Editable editable) {
                        try {
                            String obj = editable.toString();
                            if (obj.length() != 0) {
                                int intValue = Utilities.parseInt(obj).intValue();
                                if (intValue < 0) {
                                    editTextBoldCursor.setText("0");
                                    EditText editText = editTextBoldCursor;
                                    editText.setSelection(editText.length());
                                } else if (intValue > 300) {
                                    editTextBoldCursor.setText("300");
                                    EditText editText2 = editTextBoldCursor;
                                    editText2.setSelection(editText2.length());
                                } else {
                                    if (!obj.equals("" + intValue)) {
                                        EditText editText3 = editTextBoldCursor;
                                        editText3.setText("" + intValue);
                                        EditText editText4 = editTextBoldCursor;
                                        editText4.setSelection(editText4.length());
                                    }
                                }
                            }
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                        }
                    }
                });
                builder2.setView(editTextBoldCursor);
            }
            builder2.setMessage(formatStringSimple);
            builder2.setPositiveButton(LocaleController.getString("OK", NUM), new ContactsActivity$$ExternalSyntheticLambda2(this, tLRPC$User, editTextBoldCursor));
            builder2.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
            showDialog(builder2.create());
            if (editTextBoldCursor != null) {
                ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) editTextBoldCursor.getLayoutParams();
                if (marginLayoutParams != null) {
                    if (marginLayoutParams instanceof FrameLayout.LayoutParams) {
                        ((FrameLayout.LayoutParams) marginLayoutParams).gravity = 1;
                    }
                    int dp = AndroidUtilities.dp(24.0f);
                    marginLayoutParams.leftMargin = dp;
                    marginLayoutParams.rightMargin = dp;
                    marginLayoutParams.height = AndroidUtilities.dp(36.0f);
                    editTextBoldCursor.setLayoutParams(marginLayoutParams);
                }
                editTextBoldCursor.setSelection(editTextBoldCursor.getText().length());
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$didSelectResult$3(TLRPC$User tLRPC$User, String str, DialogInterface dialogInterface, int i) {
        ContactsActivityDelegate contactsActivityDelegate = this.delegate;
        if (contactsActivityDelegate != null) {
            contactsActivityDelegate.didSelectContact(tLRPC$User, str, this);
            this.delegate = null;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$didSelectResult$4(TLRPC$User tLRPC$User, EditText editText, DialogInterface dialogInterface, int i) {
        didSelectResult(tLRPC$User, false, editText != null ? editText.getText().toString() : "0");
    }

    public void onResume() {
        Activity parentActivity;
        super.onResume();
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        ContactsAdapter contactsAdapter = this.listViewAdapter;
        if (contactsAdapter != null) {
            contactsAdapter.notifyDataSetChanged();
        }
        if (this.checkPermission && Build.VERSION.SDK_INT >= 23 && (parentActivity = getParentActivity()) != null) {
            this.checkPermission = false;
            if (parentActivity.checkSelfPermission("android.permission.READ_CONTACTS") == 0) {
                return;
            }
            if (parentActivity.shouldShowRequestPermissionRationale("android.permission.READ_CONTACTS")) {
                AlertDialog create = AlertsCreator.createContactsPermissionDialog(parentActivity, new ContactsActivity$$ExternalSyntheticLambda7(this)).create();
                this.permissionDialog = create;
                showDialog(create);
                return;
            }
            askForPermissons(true);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onResume$5(int i) {
        this.askAboutContacts = i != 0;
        if (i != 0) {
            askForPermissons(false);
        }
    }

    /* access modifiers changed from: protected */
    public RecyclerListView getListView() {
        return this.listView;
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        FrameLayout frameLayout = this.floatingButtonContainer;
        if (frameLayout != null) {
            frameLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                public void onGlobalLayout() {
                    ContactsActivity.this.floatingButtonContainer.setTranslationY((float) (ContactsActivity.this.floatingHidden ? AndroidUtilities.dp(100.0f) : 0));
                    ContactsActivity.this.floatingButtonContainer.setClickable(!ContactsActivity.this.floatingHidden);
                    if (ContactsActivity.this.floatingButtonContainer != null) {
                        ContactsActivity.this.floatingButtonContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            });
        }
    }

    /* access modifiers changed from: protected */
    public void onDialogDismiss(Dialog dialog) {
        super.onDialogDismiss(dialog);
        AlertDialog alertDialog = this.permissionDialog;
        if (alertDialog != null && dialog == alertDialog && getParentActivity() != null && this.askAboutContacts) {
            askForPermissons(false);
        }
    }

    @TargetApi(23)
    private void askForPermissons(boolean z) {
        Activity parentActivity = getParentActivity();
        if (parentActivity != null && UserConfig.getInstance(this.currentAccount).syncContacts && parentActivity.checkSelfPermission("android.permission.READ_CONTACTS") != 0) {
            if (!z || !this.askAboutContacts) {
                this.permissionRequestTime = SystemClock.elapsedRealtime();
                ArrayList arrayList = new ArrayList();
                arrayList.add("android.permission.READ_CONTACTS");
                arrayList.add("android.permission.WRITE_CONTACTS");
                arrayList.add("android.permission.GET_ACCOUNTS");
                try {
                    parentActivity.requestPermissions((String[]) arrayList.toArray(new String[0]), 1);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            } else {
                showDialog(AlertsCreator.createContactsPermissionDialog(parentActivity, new ContactsActivity$$ExternalSyntheticLambda6(this)).create());
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$askForPermissons$6(int i) {
        this.askAboutContacts = i != 0;
        if (i != 0) {
            askForPermissons(false);
        }
    }

    public void onRequestPermissionsResultFragment(int i, String[] strArr, int[] iArr) {
        if (i == 1) {
            int i2 = 0;
            while (i2 < strArr.length) {
                if (iArr.length <= i2 || !"android.permission.READ_CONTACTS".equals(strArr[i2])) {
                    i2++;
                } else if (iArr[i2] == 0) {
                    ContactsController.getInstance(this.currentAccount).forceImportContacts();
                    return;
                } else {
                    SharedPreferences.Editor edit = MessagesController.getGlobalNotificationsSettings().edit();
                    this.askAboutContacts = false;
                    edit.putBoolean("askAboutContacts", false).commit();
                    if (SystemClock.elapsedRealtime() - this.permissionRequestTime < 200) {
                        try {
                            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                            intent.setData(Uri.fromParts("package", ApplicationLoader.applicationContext.getPackageName(), (String) null));
                            getParentActivity().startActivity(intent);
                            return;
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                            return;
                        }
                    } else {
                        return;
                    }
                }
            }
        }
    }

    public void onPause() {
        super.onPause();
        ActionBar actionBar = this.actionBar;
        if (actionBar != null) {
            actionBar.closeSearchField();
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        ContactsAdapter contactsAdapter;
        if (i == NotificationCenter.contactsDidLoad) {
            ContactsAdapter contactsAdapter2 = this.listViewAdapter;
            if (contactsAdapter2 != null) {
                if (!this.sortByName) {
                    contactsAdapter2.setSortType(2, true);
                }
                this.listViewAdapter.notifyDataSetChanged();
            }
        } else if (i == NotificationCenter.updateInterfaces) {
            int intValue = objArr[0].intValue();
            if (!((MessagesController.UPDATE_MASK_AVATAR & intValue) == 0 && (MessagesController.UPDATE_MASK_NAME & intValue) == 0 && (MessagesController.UPDATE_MASK_STATUS & intValue) == 0)) {
                updateVisibleRows(intValue);
            }
            if ((intValue & MessagesController.UPDATE_MASK_STATUS) != 0 && !this.sortByName && (contactsAdapter = this.listViewAdapter) != null) {
                contactsAdapter.sortOnlineContacts();
            }
        } else if (i == NotificationCenter.encryptedChatCreated) {
            if (this.createSecretChat && this.creatingChat) {
                Bundle bundle = new Bundle();
                bundle.putInt("enc_id", objArr[0].id);
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                presentFragment(new ChatActivity(bundle), true);
            }
        } else if (i == NotificationCenter.closeChats && !this.creatingChat) {
            removeSelfFromStack();
        }
    }

    private void updateVisibleRows(int i) {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int childCount = recyclerListView.getChildCount();
            for (int i2 = 0; i2 < childCount; i2++) {
                View childAt = this.listView.getChildAt(i2);
                if (childAt instanceof UserCell) {
                    ((UserCell) childAt).update(i);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void hideFloatingButton(boolean z) {
        if (this.floatingHidden != z) {
            this.floatingHidden = z;
            AnimatorSet animatorSet = new AnimatorSet();
            Animator[] animatorArr = new Animator[1];
            FrameLayout frameLayout = this.floatingButtonContainer;
            Property property = View.TRANSLATION_Y;
            float[] fArr = new float[1];
            fArr[0] = (float) (this.floatingHidden ? AndroidUtilities.dp(100.0f) : 0);
            animatorArr[0] = ObjectAnimator.ofFloat(frameLayout, property, fArr);
            animatorSet.playTogether(animatorArr);
            animatorSet.setDuration(300);
            animatorSet.setInterpolator(this.floatingInterpolator);
            this.floatingButtonContainer.setClickable(!z);
            animatorSet.start();
        }
    }

    public void setDelegate(ContactsActivityDelegate contactsActivityDelegate) {
        this.delegate = contactsActivityDelegate;
    }

    public void setInitialSearchString(String str) {
        this.initialSearchString = str;
    }

    /* access modifiers changed from: private */
    public void showItemsAnimated() {
        LinearLayoutManager linearLayoutManager = this.layoutManager;
        final int findLastVisibleItemPosition = linearLayoutManager == null ? 0 : linearLayoutManager.findLastVisibleItemPosition();
        this.listView.invalidate();
        this.listView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                ContactsActivity.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
                int childCount = ContactsActivity.this.listView.getChildCount();
                AnimatorSet animatorSet = new AnimatorSet();
                for (int i = 0; i < childCount; i++) {
                    View childAt = ContactsActivity.this.listView.getChildAt(i);
                    if (ContactsActivity.this.listView.getChildAdapterPosition(childAt) > findLastVisibleItemPosition) {
                        childAt.setAlpha(0.0f);
                        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(childAt, View.ALPHA, new float[]{0.0f, 1.0f});
                        ofFloat.setStartDelay((long) ((int) ((((float) Math.min(ContactsActivity.this.listView.getMeasuredHeight(), Math.max(0, childAt.getTop()))) / ((float) ContactsActivity.this.listView.getMeasuredHeight())) * 100.0f)));
                        ofFloat.setDuration(200);
                        animatorSet.playTogether(new Animator[]{ofFloat});
                    }
                }
                animatorSet.start();
                return true;
            }
        });
    }

    /* access modifiers changed from: protected */
    public AnimatorSet onCustomTransitionAnimation(final boolean z, final Runnable runnable) {
        ValueAnimator valueAnimator;
        BaseFragment baseFragment;
        float[] fArr = {0.0f, 1.0f};
        if (z) {
            // fill-array-data instruction
            fArr[0] = NUM;
            fArr[1] = 0;
            valueAnimator = ValueAnimator.ofFloat(fArr);
        } else {
            valueAnimator = ValueAnimator.ofFloat(fArr);
        }
        ViewGroup viewGroup = (ViewGroup) this.fragmentView.getParent();
        if (this.parentLayout.fragmentsStack.size() > 1) {
            ArrayList<BaseFragment> arrayList = this.parentLayout.fragmentsStack;
            baseFragment = arrayList.get(arrayList.size() - 2);
        } else {
            baseFragment = null;
        }
        DialogsActivity dialogsActivity = baseFragment instanceof DialogsActivity ? (DialogsActivity) baseFragment : null;
        if (dialogsActivity == null) {
            return null;
        }
        final RLottieImageView floatingButton2 = dialogsActivity.getFloatingButton();
        View view = floatingButton2.getParent() != null ? (View) floatingButton2.getParent() : null;
        if (this.floatingButtonContainer == null || view == null || floatingButton2.getVisibility() != 0 || Math.abs(view.getTranslationY()) > ((float) AndroidUtilities.dp(4.0f)) || Math.abs(this.floatingButtonContainer.getTranslationY()) > ((float) AndroidUtilities.dp(4.0f))) {
            return null;
        }
        floatingButton2.setVisibility(8);
        if (z) {
            viewGroup.setAlpha(0.0f);
        }
        valueAnimator.addUpdateListener(new ContactsActivity$$ExternalSyntheticLambda0(valueAnimator, viewGroup));
        FrameLayout frameLayout = this.floatingButtonContainer;
        if (frameLayout != null) {
            ((ViewGroup) this.fragmentView).removeView(frameLayout);
            ((FrameLayout) viewGroup.getParent()).addView(this.floatingButtonContainer);
        }
        valueAnimator.setDuration(150);
        valueAnimator.setInterpolator(new DecelerateInterpolator(1.5f));
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                if (ContactsActivity.this.floatingButtonContainer != null) {
                    if (ContactsActivity.this.floatingButtonContainer.getParent() instanceof ViewGroup) {
                        ((ViewGroup) ContactsActivity.this.floatingButtonContainer.getParent()).removeView(ContactsActivity.this.floatingButtonContainer);
                    }
                    ((ViewGroup) ContactsActivity.this.fragmentView).addView(ContactsActivity.this.floatingButtonContainer);
                    floatingButton2.setVisibility(0);
                    if (!z) {
                        floatingButton2.setAnimation(NUM, 52, 52);
                        floatingButton2.getAnimatedDrawable().setCurrentFrame(ContactsActivity.this.floatingButton.getAnimatedDrawable().getCurrentFrame());
                        floatingButton2.playAnimation();
                    }
                }
                runnable.run();
            }
        });
        animatorSet.playTogether(new Animator[]{valueAnimator});
        AndroidUtilities.runOnUIThread(new ContactsActivity$$ExternalSyntheticLambda5(this, animatorSet, z, floatingButton2), 50);
        return animatorSet;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$onCustomTransitionAnimation$7(ValueAnimator valueAnimator, ViewGroup viewGroup, ValueAnimator valueAnimator2) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        viewGroup.setTranslationX(((float) AndroidUtilities.dp(48.0f)) * floatValue);
        viewGroup.setAlpha(1.0f - floatValue);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onCustomTransitionAnimation$8(AnimatorSet animatorSet, boolean z, RLottieImageView rLottieImageView) {
        final RLottieImageView rLottieImageView2 = rLottieImageView;
        this.animationIndex = getNotificationCenter().setAnimationInProgress(this.animationIndex, (int[]) null);
        animatorSet.start();
        if (z) {
            this.floatingButton.setAnimation(NUM, 52, 52);
            this.floatingButton.playAnimation();
        } else {
            this.floatingButton.setAnimation(NUM, 52, 52);
            this.floatingButton.playAnimation();
        }
        AnimatorSet animatorSet2 = this.bounceIconAnimator;
        if (animatorSet2 != null) {
            animatorSet2.cancel();
        }
        this.bounceIconAnimator = new AnimatorSet();
        float duration = (float) this.floatingButton.getAnimatedDrawable().getDuration();
        long j = 0;
        int i = 4;
        if (z) {
            for (int i2 = 0; i2 < 6; i2++) {
                AnimatorSet animatorSet3 = new AnimatorSet();
                if (i2 == 0) {
                    animatorSet3.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_X, new float[]{1.0f, 0.9f}), ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_Y, new float[]{1.0f, 0.9f}), ObjectAnimator.ofFloat(rLottieImageView2, View.SCALE_X, new float[]{1.0f, 0.9f}), ObjectAnimator.ofFloat(rLottieImageView2, View.SCALE_Y, new float[]{1.0f, 0.9f})});
                    animatorSet3.setDuration((long) (0.12765957f * duration));
                    animatorSet3.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                } else if (i2 == 1) {
                    animatorSet3.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_X, new float[]{0.9f, 1.06f}), ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_Y, new float[]{0.9f, 1.06f}), ObjectAnimator.ofFloat(rLottieImageView2, View.SCALE_X, new float[]{0.9f, 1.06f}), ObjectAnimator.ofFloat(rLottieImageView2, View.SCALE_Y, new float[]{0.9f, 1.06f})});
                    animatorSet3.setDuration((long) (0.3617021f * duration));
                    animatorSet3.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
                } else if (i2 == 2) {
                    animatorSet3.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_X, new float[]{1.06f, 0.9f}), ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_Y, new float[]{1.06f, 0.9f}), ObjectAnimator.ofFloat(rLottieImageView2, View.SCALE_X, new float[]{1.06f, 0.9f}), ObjectAnimator.ofFloat(rLottieImageView2, View.SCALE_Y, new float[]{1.06f, 0.9f})});
                    animatorSet3.setDuration((long) (0.21276596f * duration));
                    animatorSet3.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
                } else if (i2 == 3) {
                    animatorSet3.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_X, new float[]{0.9f, 1.03f}), ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_Y, new float[]{0.9f, 1.03f}), ObjectAnimator.ofFloat(rLottieImageView2, View.SCALE_X, new float[]{0.9f, 1.03f}), ObjectAnimator.ofFloat(rLottieImageView2, View.SCALE_Y, new float[]{0.9f, 1.03f})});
                    animatorSet3.setDuration((long) (duration * 0.10638298f));
                    animatorSet3.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
                } else if (i2 == 4) {
                    animatorSet3.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_X, new float[]{1.03f, 0.98f}), ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_Y, new float[]{1.03f, 0.98f}), ObjectAnimator.ofFloat(rLottieImageView2, View.SCALE_X, new float[]{1.03f, 0.98f}), ObjectAnimator.ofFloat(rLottieImageView2, View.SCALE_Y, new float[]{1.03f, 0.98f})});
                    animatorSet3.setDuration((long) (duration * 0.10638298f));
                    animatorSet3.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
                } else {
                    animatorSet3.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_X, new float[]{0.98f, 1.0f}), ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_Y, new float[]{0.98f, 1.0f}), ObjectAnimator.ofFloat(rLottieImageView2, View.SCALE_X, new float[]{0.98f, 1.0f}), ObjectAnimator.ofFloat(rLottieImageView2, View.SCALE_Y, new float[]{0.98f, 1.0f})});
                    animatorSet3.setDuration((long) (0.08510638f * duration));
                    animatorSet3.setInterpolator(CubicBezierInterpolator.EASE_IN);
                }
                animatorSet3.setStartDelay(j);
                j += animatorSet3.getDuration();
                this.bounceIconAnimator.playTogether(new Animator[]{animatorSet3});
            }
        } else {
            for (int i3 = 0; i3 < 5; i3++) {
                AnimatorSet animatorSet4 = new AnimatorSet();
                if (i3 == 0) {
                    Animator[] animatorArr = new Animator[i];
                    animatorArr[0] = ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_X, new float[]{1.0f, 0.9f});
                    animatorArr[1] = ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_Y, new float[]{1.0f, 0.9f});
                    animatorArr[2] = ObjectAnimator.ofFloat(rLottieImageView2, View.SCALE_X, new float[]{1.0f, 0.9f});
                    animatorArr[3] = ObjectAnimator.ofFloat(rLottieImageView2, View.SCALE_Y, new float[]{1.0f, 0.9f});
                    animatorSet4.playTogether(animatorArr);
                    animatorSet4.setDuration((long) (0.19444445f * duration));
                    animatorSet4.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                } else if (i3 == 1) {
                    animatorSet4.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_X, new float[]{0.9f, 1.06f}), ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_Y, new float[]{0.9f, 1.06f}), ObjectAnimator.ofFloat(rLottieImageView2, View.SCALE_X, new float[]{0.9f, 1.06f}), ObjectAnimator.ofFloat(rLottieImageView2, View.SCALE_Y, new float[]{0.9f, 1.06f})});
                    animatorSet4.setDuration((long) (0.22222222f * duration));
                    animatorSet4.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
                } else if (i3 == 2) {
                    animatorSet4.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_X, new float[]{1.06f, 0.92f}), ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_Y, new float[]{1.06f, 0.92f}), ObjectAnimator.ofFloat(rLottieImageView2, View.SCALE_X, new float[]{1.06f, 0.92f}), ObjectAnimator.ofFloat(rLottieImageView2, View.SCALE_Y, new float[]{1.06f, 0.92f})});
                    animatorSet4.setDuration((long) (0.19444445f * duration));
                    animatorSet4.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
                } else if (i3 == 3) {
                    animatorSet4.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_X, new float[]{0.92f, 1.02f}), ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_Y, new float[]{0.92f, 1.02f}), ObjectAnimator.ofFloat(rLottieImageView2, View.SCALE_X, new float[]{0.92f, 1.02f}), ObjectAnimator.ofFloat(rLottieImageView2, View.SCALE_Y, new float[]{0.92f, 1.02f})});
                    animatorSet4.setDuration((long) (0.25f * duration));
                    animatorSet4.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
                } else {
                    i = 4;
                    animatorSet4.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_X, new float[]{1.02f, 1.0f}), ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_Y, new float[]{1.02f, 1.0f}), ObjectAnimator.ofFloat(rLottieImageView2, View.SCALE_X, new float[]{1.02f, 1.0f}), ObjectAnimator.ofFloat(rLottieImageView2, View.SCALE_Y, new float[]{1.02f, 1.0f})});
                    animatorSet4.setDuration((long) (duration * 0.10638298f));
                    animatorSet4.setInterpolator(CubicBezierInterpolator.EASE_IN);
                    animatorSet4.setStartDelay(j);
                    j += animatorSet4.getDuration();
                    this.bounceIconAnimator.playTogether(new Animator[]{animatorSet4});
                }
                i = 4;
                animatorSet4.setStartDelay(j);
                j += animatorSet4.getDuration();
                this.bounceIconAnimator.playTogether(new Animator[]{animatorSet4});
            }
        }
        this.bounceIconAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                ContactsActivity.this.floatingButton.setScaleX(1.0f);
                ContactsActivity.this.floatingButton.setScaleY(1.0f);
                rLottieImageView2.setScaleX(1.0f);
                rLottieImageView2.setScaleY(1.0f);
                AnimatorSet unused = ContactsActivity.this.bounceIconAnimator = null;
                ContactsActivity.this.getNotificationCenter().onAnimationFinish(ContactsActivity.this.animationIndex);
            }
        });
        this.bounceIconAnimator.start();
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        ContactsActivity$$ExternalSyntheticLambda8 contactsActivity$$ExternalSyntheticLambda8 = new ContactsActivity$$ExternalSyntheticLambda8(this);
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSearch"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSearchPlaceholder"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_SECTIONS, new Class[]{LetterSectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "fastScrollActive"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "fastScrollInactive"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "fastScrollText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        ContactsActivity$$ExternalSyntheticLambda8 contactsActivity$$ExternalSyntheticLambda82 = contactsActivity$$ExternalSyntheticLambda8;
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) contactsActivity$$ExternalSyntheticLambda82, "windowBackgroundWhiteGrayText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) contactsActivity$$ExternalSyntheticLambda82, "windowBackgroundWhiteBlueText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, (Paint) null, Theme.avatarDrawables, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_text"));
        ContactsActivity$$ExternalSyntheticLambda8 contactsActivity$$ExternalSyntheticLambda83 = contactsActivity$$ExternalSyntheticLambda8;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, contactsActivity$$ExternalSyntheticLambda83, "avatar_backgroundRed"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, contactsActivity$$ExternalSyntheticLambda83, "avatar_backgroundOrange"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, contactsActivity$$ExternalSyntheticLambda83, "avatar_backgroundViolet"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, contactsActivity$$ExternalSyntheticLambda83, "avatar_backgroundGreen"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, contactsActivity$$ExternalSyntheticLambda83, "avatar_backgroundCyan"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, contactsActivity$$ExternalSyntheticLambda83, "avatar_backgroundBlue"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, contactsActivity$$ExternalSyntheticLambda83, "avatar_backgroundPink"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText2"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"));
        arrayList.add(new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_actionIcon"));
        arrayList.add(new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_actionBackground"));
        arrayList.add(new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_actionPressedBackground"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{GraySectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "key_graySectionText"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GraySectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "graySection"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_groupDrawable, Theme.dialogs_broadcastDrawable, Theme.dialogs_botDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_nameIcon"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_verifiedCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_verifiedCheck"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_verifiedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_verifiedBackground"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, Theme.dialogs_offlinePaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText3"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, Theme.dialogs_onlinePaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText3"));
        TextPaint[] textPaintArr = Theme.dialogs_namePaint;
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{ProfileSearchCell.class}, (String[]) null, new Paint[]{textPaintArr[0], textPaintArr[1], Theme.dialogs_searchNamePaint}, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_name"));
        TextPaint[] textPaintArr2 = Theme.dialogs_nameEncryptedPaint;
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{ProfileSearchCell.class}, (String[]) null, new Paint[]{textPaintArr2[0], textPaintArr2[1], Theme.dialogs_searchNameEncryptedPaint}, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_secretName"));
        return arrayList;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$getThemeDescriptions$9() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int childCount = recyclerListView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.listView.getChildAt(i);
                if (childAt instanceof UserCell) {
                    ((UserCell) childAt).update(0);
                } else if (childAt instanceof ProfileSearchCell) {
                    ((ProfileSearchCell) childAt).update(0);
                }
            }
        }
    }
}
