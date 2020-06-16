package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
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
import android.provider.Settings;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.util.Property;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.RecyclerListView;

public class ContactsActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private boolean allowBots = true;
    private boolean allowSelf = true;
    private boolean allowUsernameSearch = true;
    private boolean askAboutContacts = true;
    private int channelId;
    private int chatId;
    private boolean checkPermission = true;
    private boolean createSecretChat;
    private boolean creatingChat;
    private ContactsActivityDelegate delegate;
    private boolean destroyAfterSelect;
    private boolean disableSections;
    /* access modifiers changed from: private */
    public EmptyTextProgressView emptyView;
    private ImageView floatingButton;
    /* access modifiers changed from: private */
    public FrameLayout floatingButtonContainer;
    /* access modifiers changed from: private */
    public boolean floatingHidden;
    private AccelerateDecelerateInterpolator floatingInterpolator = new AccelerateDecelerateInterpolator();
    private boolean hasGps;
    private SparseArray<TLRPC$User> ignoreUsers;
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
            this.channelId = this.arguments.getInt("channelId", 0);
            this.needFinishFragment = this.arguments.getBoolean("needFinishFragment", true);
            this.chatId = this.arguments.getInt("chat_id", 0);
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
    }

    /* JADX WARNING: Removed duplicated region for block: B:38:0x0144  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x014c  */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x01e6  */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x01e9  */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x01f1  */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x01f4  */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x01fd  */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x0200  */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x020c  */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x020f  */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x0217  */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x021a  */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x0257  */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x02b4  */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x031b  */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x031e  */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x0325  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.view.View createView(android.content.Context r25) {
        /*
            r24 = this;
            r8 = r24
            r7 = r25
            r6 = 0
            r8.searching = r6
            r8.searchWas = r6
            org.telegram.ui.ActionBar.ActionBar r0 = r8.actionBar
            r1 = 2131165432(0x7var_f8, float:1.794508E38)
            r0.setBackButtonImage(r1)
            org.telegram.ui.ActionBar.ActionBar r0 = r8.actionBar
            r5 = 1
            r0.setAllowOverlayTitle(r5)
            boolean r0 = r8.destroyAfterSelect
            if (r0 == 0) goto L_0x0050
            boolean r0 = r8.returnAsResult
            if (r0 == 0) goto L_0x002e
            org.telegram.ui.ActionBar.ActionBar r0 = r8.actionBar
            r1 = 2131626708(0x7f0e0ad4, float:1.888066E38)
            java.lang.String r2 = "SelectContact"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            goto L_0x005e
        L_0x002e:
            boolean r0 = r8.createSecretChat
            if (r0 == 0) goto L_0x0041
            org.telegram.ui.ActionBar.ActionBar r0 = r8.actionBar
            r1 = 2131625804(0x7f0e074c, float:1.8878826E38)
            java.lang.String r2 = "NewSecretChat"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            goto L_0x005e
        L_0x0041:
            org.telegram.ui.ActionBar.ActionBar r0 = r8.actionBar
            r1 = 2131625795(0x7f0e0743, float:1.8878808E38)
            java.lang.String r2 = "NewMessageTitle"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            goto L_0x005e
        L_0x0050:
            org.telegram.ui.ActionBar.ActionBar r0 = r8.actionBar
            r1 = 2131624795(0x7f0e035b, float:1.887678E38)
            java.lang.String r2 = "Contacts"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
        L_0x005e:
            org.telegram.ui.ActionBar.ActionBar r0 = r8.actionBar
            org.telegram.ui.ContactsActivity$1 r1 = new org.telegram.ui.ContactsActivity$1
            r1.<init>()
            r0.setActionBarMenuOnItemClick(r1)
            org.telegram.ui.ActionBar.ActionBar r0 = r8.actionBar
            org.telegram.ui.ActionBar.ActionBarMenu r0 = r0.createMenu()
            r1 = 2131165442(0x7var_, float:1.7945101E38)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.addItem((int) r6, (int) r1)
            r1.setIsSearchField(r5)
            org.telegram.ui.ContactsActivity$2 r2 = new org.telegram.ui.ContactsActivity$2
            r2.<init>()
            r1.setActionBarMenuItemSearchListener(r2)
            java.lang.String r2 = "Search"
            r3 = 2131626665(0x7f0e0aa9, float:1.8880573E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r2, r3)
            r1.setSearchFieldHint(r4)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)
            r1.setContentDescription(r2)
            boolean r1 = r8.createSecretChat
            if (r1 != 0) goto L_0x00b8
            boolean r1 = r8.returnAsResult
            if (r1 != 0) goto L_0x00b8
            boolean r1 = r8.sortByName
            if (r1 == 0) goto L_0x00a3
            r1 = 2131165353(0x7var_a9, float:1.794492E38)
            goto L_0x00a6
        L_0x00a3:
            r1 = 2131165352(0x7var_a8, float:1.7944919E38)
        L_0x00a6:
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r0.addItem((int) r5, (int) r1)
            r8.sortItem = r0
            r1 = 2131623967(0x7f0e001f, float:1.88751E38)
            java.lang.String r2 = "AccDescrContactSorting"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setContentDescription(r1)
        L_0x00b8:
            org.telegram.ui.Adapters.SearchAdapter r0 = new org.telegram.ui.Adapters.SearchAdapter
            android.util.SparseArray<org.telegram.tgnet.TLRPC$User> r11 = r8.ignoreUsers
            boolean r12 = r8.allowUsernameSearch
            r13 = 0
            r14 = 0
            boolean r15 = r8.allowBots
            boolean r1 = r8.allowSelf
            r17 = 1
            r18 = 0
            r9 = r0
            r10 = r25
            r16 = r1
            r9.<init>(r10, r11, r12, r13, r14, r15, r16, r17, r18)
            r8.searchListViewAdapter = r0
            int r0 = r8.chatId
            r9 = 3
            r10 = 2
            if (r0 == 0) goto L_0x00ee
            int r0 = r8.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            int r1 = r8.chatId
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r1)
            boolean r0 = org.telegram.messenger.ChatObject.canUserDoAdminAction(r0, r9)
        L_0x00ec:
            r11 = r0
            goto L_0x0115
        L_0x00ee:
            int r0 = r8.channelId
            if (r0 == 0) goto L_0x0114
            int r0 = r8.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            int r1 = r8.channelId
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r1)
            boolean r1 = org.telegram.messenger.ChatObject.canUserDoAdminAction(r0, r9)
            if (r1 == 0) goto L_0x0112
            java.lang.String r0 = r0.username
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x0112
            r0 = 2
            goto L_0x00ec
        L_0x0112:
            r0 = 0
            goto L_0x00ec
        L_0x0114:
            r11 = 0
        L_0x0115:
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0124 }
            android.content.pm.PackageManager r0 = r0.getPackageManager()     // Catch:{ all -> 0x0124 }
            java.lang.String r1 = "android.hardware.location.gps"
            boolean r0 = r0.hasSystemFeature(r1)     // Catch:{ all -> 0x0124 }
            r8.hasGps = r0     // Catch:{ all -> 0x0124 }
            goto L_0x0126
        L_0x0124:
            r8.hasGps = r6
        L_0x0126:
            org.telegram.ui.ContactsActivity$3 r12 = new org.telegram.ui.ContactsActivity$3
            boolean r3 = r8.onlyUsers
            boolean r4 = r8.needPhonebook
            android.util.SparseArray<org.telegram.tgnet.TLRPC$User> r13 = r8.ignoreUsers
            boolean r14 = r8.hasGps
            r0 = r12
            r1 = r24
            r2 = r25
            r15 = 1
            r5 = r13
            r13 = 0
            r6 = r11
            r9 = r7
            r7 = r14
            r0.<init>(r2, r3, r4, r5, r6, r7)
            r8.listViewAdapter = r12
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.sortItem
            if (r0 == 0) goto L_0x014c
            boolean r0 = r8.sortByName
            if (r0 == 0) goto L_0x014a
            r6 = 1
            goto L_0x014d
        L_0x014a:
            r6 = 2
            goto L_0x014d
        L_0x014c:
            r6 = 0
        L_0x014d:
            r12.setSortType(r6)
            org.telegram.ui.Adapters.ContactsAdapter r0 = r8.listViewAdapter
            boolean r1 = r8.disableSections
            r0.setDisableSections(r1)
            org.telegram.ui.ContactsActivity$4 r0 = new org.telegram.ui.ContactsActivity$4
            r0.<init>(r9)
            r8.fragmentView = r0
            android.widget.FrameLayout r0 = (android.widget.FrameLayout) r0
            org.telegram.ui.Components.EmptyTextProgressView r1 = new org.telegram.ui.Components.EmptyTextProgressView
            r1.<init>(r9)
            r8.emptyView = r1
            r1.setShowAtCenter(r15)
            org.telegram.ui.Components.EmptyTextProgressView r1 = r8.emptyView
            r2 = 2131625825(0x7f0e0761, float:1.8878869E38)
            java.lang.String r3 = "NoContacts"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            org.telegram.ui.Components.EmptyTextProgressView r1 = r8.emptyView
            r1.showTextView()
            org.telegram.ui.Components.EmptyTextProgressView r1 = r8.emptyView
            r2 = -1
            r3 = -1082130432(0xffffffffbvar_, float:-1.0)
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r2, r3)
            r0.addView(r1, r4)
            org.telegram.ui.ContactsActivity$5 r1 = new org.telegram.ui.ContactsActivity$5
            r1.<init>(r9)
            r8.listView = r1
            r1.setSectionsType(r15)
            org.telegram.ui.Components.RecyclerListView r1 = r8.listView
            r1.setVerticalScrollBarEnabled(r13)
            org.telegram.ui.Components.RecyclerListView r1 = r8.listView
            r1.setFastScrollEnabled()
            org.telegram.ui.Components.RecyclerListView r1 = r8.listView
            androidx.recyclerview.widget.LinearLayoutManager r4 = new androidx.recyclerview.widget.LinearLayoutManager
            r4.<init>(r9, r15, r13)
            r8.layoutManager = r4
            r1.setLayoutManager(r4)
            org.telegram.ui.Components.RecyclerListView r1 = r8.listView
            org.telegram.ui.Adapters.ContactsAdapter r4 = r8.listViewAdapter
            r1.setAdapter(r4)
            org.telegram.ui.Components.RecyclerListView r1 = r8.listView
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r2, r3)
            r0.addView(r1, r2)
            org.telegram.ui.Components.RecyclerListView r1 = r8.listView
            org.telegram.ui.-$$Lambda$ContactsActivity$gnTnCjocys4rzqdEYocufNT6sRA r2 = new org.telegram.ui.-$$Lambda$ContactsActivity$gnTnCjocys4rzqdEYocufNT6sRA
            r2.<init>(r11)
            r1.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r2)
            org.telegram.ui.Components.RecyclerListView r1 = r8.listView
            org.telegram.ui.ContactsActivity$6 r2 = new org.telegram.ui.ContactsActivity$6
            r2.<init>()
            r1.setOnScrollListener(r2)
            boolean r1 = r8.createSecretChat
            if (r1 != 0) goto L_0x0337
            boolean r1 = r8.returnAsResult
            if (r1 != 0) goto L_0x0337
            android.widget.FrameLayout r1 = new android.widget.FrameLayout
            r1.<init>(r9)
            r8.floatingButtonContainer = r1
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 56
            r4 = 60
            r5 = 21
            if (r2 < r5) goto L_0x01e9
            r2 = 56
            goto L_0x01eb
        L_0x01e9:
            r2 = 60
        L_0x01eb:
            int r17 = r2 + 20
            int r2 = android.os.Build.VERSION.SDK_INT
            if (r2 < r5) goto L_0x01f4
            r2 = 56
            goto L_0x01f6
        L_0x01f4:
            r2 = 60
        L_0x01f6:
            int r2 = r2 + 14
            float r2 = (float) r2
            boolean r6 = org.telegram.messenger.LocaleController.isRTL
            if (r6 == 0) goto L_0x0200
            r16 = 3
            goto L_0x0203
        L_0x0200:
            r6 = 5
            r16 = 5
        L_0x0203:
            r19 = r16 | 80
            boolean r6 = org.telegram.messenger.LocaleController.isRTL
            r7 = 0
            r11 = 1082130432(0x40800000, float:4.0)
            if (r6 == 0) goto L_0x020f
            r20 = 1082130432(0x40800000, float:4.0)
            goto L_0x0211
        L_0x020f:
            r20 = 0
        L_0x0211:
            r21 = 0
            boolean r6 = org.telegram.messenger.LocaleController.isRTL
            if (r6 == 0) goto L_0x021a
            r22 = 0
            goto L_0x021c
        L_0x021a:
            r22 = 1082130432(0x40800000, float:4.0)
        L_0x021c:
            r23 = 0
            r18 = r2
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r17, r18, r19, r20, r21, r22, r23)
            r0.addView(r1, r2)
            android.widget.FrameLayout r0 = r8.floatingButtonContainer
            org.telegram.ui.-$$Lambda$ContactsActivity$XQCfC9ukjnkgkb2J-v7QLg6mf6E r1 = new org.telegram.ui.-$$Lambda$ContactsActivity$XQCfC9ukjnkgkb2J-v7QLg6mf6E
            r1.<init>()
            r0.setOnClickListener(r1)
            android.widget.ImageView r0 = new android.widget.ImageView
            r0.<init>(r9)
            r8.floatingButton = r0
            android.widget.ImageView$ScaleType r1 = android.widget.ImageView.ScaleType.CENTER
            r0.setScaleType(r1)
            r0 = 1113587712(0x42600000, float:56.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r0)
            java.lang.String r2 = "chats_actionBackground"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            java.lang.String r6 = "chats_actionPressedBackground"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.createSimpleSelectorCircleDrawable(r1, r2, r6)
            int r2 = android.os.Build.VERSION.SDK_INT
            if (r2 >= r5) goto L_0x0283
            android.content.res.Resources r2 = r25.getResources()
            r6 = 2131165387(0x7var_cb, float:1.794499E38)
            android.graphics.drawable.Drawable r2 = r2.getDrawable(r6)
            android.graphics.drawable.Drawable r2 = r2.mutate()
            android.graphics.PorterDuffColorFilter r6 = new android.graphics.PorterDuffColorFilter
            r7 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            android.graphics.PorterDuff$Mode r9 = android.graphics.PorterDuff.Mode.MULTIPLY
            r6.<init>(r7, r9)
            r2.setColorFilter(r6)
            org.telegram.ui.Components.CombinedDrawable r6 = new org.telegram.ui.Components.CombinedDrawable
            r6.<init>(r2, r1, r13, r13)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r6.setIconSize(r1, r0)
            r1 = r6
        L_0x0283:
            android.widget.ImageView r0 = r8.floatingButton
            r0.setBackgroundDrawable(r1)
            android.widget.ImageView r0 = r8.floatingButton
            android.graphics.PorterDuffColorFilter r1 = new android.graphics.PorterDuffColorFilter
            java.lang.String r2 = "chats_actionIcon"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            android.graphics.PorterDuff$Mode r6 = android.graphics.PorterDuff.Mode.MULTIPLY
            r1.<init>(r2, r6)
            r0.setColorFilter(r1)
            android.widget.ImageView r0 = r8.floatingButton
            r1 = 2131165259(0x7var_b, float:1.794473E38)
            r0.setImageResource(r1)
            android.widget.FrameLayout r0 = r8.floatingButtonContainer
            r1 = 2131624824(0x7f0e0378, float:1.8876839E38)
            java.lang.String r2 = "CreateNewContact"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setContentDescription(r1)
            int r0 = android.os.Build.VERSION.SDK_INT
            if (r0 < r5) goto L_0x0313
            android.animation.StateListAnimator r0 = new android.animation.StateListAnimator
            r0.<init>()
            int[] r1 = new int[r15]
            r2 = 16842919(0x10100a7, float:2.3694026E-38)
            r1[r13] = r2
            android.widget.ImageView r2 = r8.floatingButton
            android.util.Property r6 = android.view.View.TRANSLATION_Z
            float[] r7 = new float[r10]
            r9 = 1073741824(0x40000000, float:2.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r9)
            float r12 = (float) r12
            r7[r13] = r12
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r12 = (float) r12
            r7[r15] = r12
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r2, r6, r7)
            r6 = 200(0xc8, double:9.9E-322)
            android.animation.ObjectAnimator r2 = r2.setDuration(r6)
            r0.addState(r1, r2)
            int[] r1 = new int[r13]
            android.widget.ImageView r2 = r8.floatingButton
            android.util.Property r12 = android.view.View.TRANSLATION_Z
            float[] r10 = new float[r10]
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r11 = (float) r11
            r10[r13] = r11
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            float r9 = (float) r9
            r10[r15] = r9
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r2, r12, r10)
            android.animation.ObjectAnimator r2 = r2.setDuration(r6)
            r0.addState(r1, r2)
            android.widget.ImageView r1 = r8.floatingButton
            r1.setStateListAnimator(r0)
            android.widget.ImageView r0 = r8.floatingButton
            org.telegram.ui.ContactsActivity$7 r1 = new org.telegram.ui.ContactsActivity$7
            r1.<init>(r8)
            r0.setOutlineProvider(r1)
        L_0x0313:
            android.widget.FrameLayout r0 = r8.floatingButtonContainer
            android.widget.ImageView r1 = r8.floatingButton
            int r2 = android.os.Build.VERSION.SDK_INT
            if (r2 < r5) goto L_0x031e
            r9 = 56
            goto L_0x0320
        L_0x031e:
            r9 = 60
        L_0x0320:
            int r2 = android.os.Build.VERSION.SDK_INT
            if (r2 < r5) goto L_0x0325
            goto L_0x0327
        L_0x0325:
            r3 = 60
        L_0x0327:
            float r10 = (float) r3
            r11 = 51
            r12 = 1092616192(0x41200000, float:10.0)
            r13 = 0
            r14 = 1092616192(0x41200000, float:10.0)
            r15 = 0
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r10, r11, r12, r13, r14, r15)
            r0.addView(r1, r2)
        L_0x0337:
            android.view.View r0 = r8.fragmentView
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ContactsActivity.createView(android.content.Context):android.view.View");
    }

    public /* synthetic */ void lambda$createView$1$ContactsActivity(int i, View view, int i2) {
        Activity parentActivity;
        boolean z = false;
        boolean z2 = true;
        if (!this.searching || !this.searchWas) {
            int sectionForPosition = this.listViewAdapter.getSectionForPosition(i2);
            int positionInSectionForPosition = this.listViewAdapter.getPositionInSectionForPosition(i2);
            if (positionInSectionForPosition >= 0 && sectionForPosition >= 0) {
                if ((this.onlyUsers && i == 0) || sectionForPosition != 0) {
                    Object item = this.listViewAdapter.getItem(sectionForPosition, positionInSectionForPosition);
                    if (item instanceof TLRPC$User) {
                        TLRPC$User tLRPC$User = (TLRPC$User) item;
                        if (this.returnAsResult) {
                            SparseArray<TLRPC$User> sparseArray = this.ignoreUsers;
                            if (sparseArray == null || sparseArray.indexOfKey(tLRPC$User.id) < 0) {
                                didSelectResult(tLRPC$User, true, (String) null);
                            }
                        } else if (this.createSecretChat) {
                            this.creatingChat = true;
                            SecretChatHelper.getInstance(this.currentAccount).startSecretChat(getParentActivity(), tLRPC$User);
                        } else {
                            Bundle bundle = new Bundle();
                            bundle.putInt("user_id", tLRPC$User.id);
                            if (MessagesController.getInstance(this.currentAccount).checkCanOpenChat(bundle, this)) {
                                presentFragment(new ChatActivity(bundle), true);
                            }
                        }
                    } else if (item instanceof ContactsController.Contact) {
                        ContactsController.Contact contact = (ContactsController.Contact) item;
                        String str = !contact.phones.isEmpty() ? contact.phones.get(0) : null;
                        if (str != null && getParentActivity() != null) {
                            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                            builder.setMessage(LocaleController.getString("InviteUser", NUM));
                            builder.setTitle(LocaleController.getString("AppName", NUM));
                            builder.setPositiveButton(LocaleController.getString("OK", NUM), new DialogInterface.OnClickListener(str) {
                                public final /* synthetic */ String f$1;

                                {
                                    this.f$1 = r2;
                                }

                                public final void onClick(DialogInterface dialogInterface, int i) {
                                    ContactsActivity.this.lambda$null$0$ContactsActivity(this.f$1, dialogInterface, i);
                                }
                            });
                            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                            showDialog(builder.create());
                        }
                    }
                } else if (this.needPhonebook) {
                    if (positionInSectionForPosition == 0) {
                        presentFragment(new InviteContactsActivity());
                    } else if (positionInSectionForPosition == 1 && this.hasGps) {
                        if (Build.VERSION.SDK_INT < 23 || (parentActivity = getParentActivity()) == null || parentActivity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == 0) {
                            int i3 = Build.VERSION.SDK_INT;
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
                        int i4 = this.chatId;
                        if (i4 == 0) {
                            i4 = this.channelId;
                        }
                        presentFragment(new GroupInviteActivity(i4));
                    }
                } else if (positionInSectionForPosition == 0) {
                    presentFragment(new GroupCreateActivity(new Bundle()), false);
                } else if (positionInSectionForPosition == 1) {
                    Bundle bundle2 = new Bundle();
                    bundle2.putBoolean("onlyUsers", true);
                    bundle2.putBoolean("destroyAfterSelect", true);
                    bundle2.putBoolean("createSecretChat", true);
                    bundle2.putBoolean("allowBots", false);
                    bundle2.putBoolean("allowSelf", false);
                    presentFragment(new ContactsActivity(bundle2), false);
                } else if (positionInSectionForPosition == 2) {
                    SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
                    if (BuildVars.DEBUG_VERSION || !globalMainSettings.getBoolean("channel_intro", false)) {
                        presentFragment(new ActionIntroActivity(0));
                        globalMainSettings.edit().putBoolean("channel_intro", true).commit();
                        return;
                    }
                    Bundle bundle3 = new Bundle();
                    bundle3.putInt("step", 0);
                    presentFragment(new ChannelCreateActivity(bundle3));
                }
            }
        } else {
            Object item2 = this.searchListViewAdapter.getItem(i2);
            if (item2 instanceof TLRPC$User) {
                TLRPC$User tLRPC$User2 = (TLRPC$User) item2;
                if (tLRPC$User2 != null) {
                    if (this.searchListViewAdapter.isGlobalSearch(i2)) {
                        ArrayList arrayList = new ArrayList();
                        arrayList.add(tLRPC$User2);
                        MessagesController.getInstance(this.currentAccount).putUsers(arrayList, false);
                        MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(arrayList, (ArrayList<TLRPC$Chat>) null, false, true);
                    }
                    if (this.returnAsResult) {
                        SparseArray<TLRPC$User> sparseArray2 = this.ignoreUsers;
                        if (sparseArray2 == null || sparseArray2.indexOfKey(tLRPC$User2.id) < 0) {
                            didSelectResult(tLRPC$User2, true, (String) null);
                        }
                    } else if (!this.createSecretChat) {
                        Bundle bundle4 = new Bundle();
                        bundle4.putInt("user_id", tLRPC$User2.id);
                        if (MessagesController.getInstance(this.currentAccount).checkCanOpenChat(bundle4, this)) {
                            presentFragment(new ChatActivity(bundle4), true);
                        }
                    } else if (tLRPC$User2.id != UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                        this.creatingChat = true;
                        SecretChatHelper.getInstance(this.currentAccount).startSecretChat(getParentActivity(), tLRPC$User2);
                    }
                }
            } else if (item2 instanceof String) {
                String str2 = (String) item2;
                if (!str2.equals("section")) {
                    NewContactActivity newContactActivity = new NewContactActivity();
                    newContactActivity.setInitialPhoneNumber(str2);
                    presentFragment(newContactActivity);
                }
            }
        }
    }

    public /* synthetic */ void lambda$null$0$ContactsActivity(String str, DialogInterface dialogInterface, int i) {
        try {
            Intent intent = new Intent("android.intent.action.VIEW", Uri.fromParts("sms", str, (String) null));
            intent.putExtra("sms_body", ContactsController.getInstance(this.currentAccount).getInviteText(1));
            getParentActivity().startActivityForResult(intent, 500);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public /* synthetic */ void lambda$createView$2$ContactsActivity(View view) {
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
                        Toast.makeText(getParentActivity(), LocaleController.getString("BotCantJoinGroups", NUM), 0).show();
                        return;
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                        return;
                    }
                } else if (this.channelId != 0) {
                    TLRPC$Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.channelId));
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                    if (ChatObject.canAddAdmins(chat)) {
                        builder.setTitle(LocaleController.getString("AppName", NUM));
                        builder.setMessage(LocaleController.getString("AddBotAsAdmin", NUM));
                        builder.setPositiveButton(LocaleController.getString("MakeAdmin", NUM), new DialogInterface.OnClickListener(tLRPC$User, str) {
                            public final /* synthetic */ TLRPC$User f$1;
                            public final /* synthetic */ String f$2;

                            {
                                this.f$1 = r2;
                                this.f$2 = r3;
                            }

                            public final void onClick(DialogInterface dialogInterface, int i) {
                                ContactsActivity.this.lambda$didSelectResult$3$ContactsActivity(this.f$1, this.f$2, dialogInterface, i);
                            }
                        });
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
                                    editTextBoldCursor.setSelection(editTextBoldCursor.length());
                                } else if (intValue > 300) {
                                    editTextBoldCursor.setText("300");
                                    editTextBoldCursor.setSelection(editTextBoldCursor.length());
                                } else {
                                    if (!obj.equals("" + intValue)) {
                                        EditText editText = editTextBoldCursor;
                                        editText.setText("" + intValue);
                                        editTextBoldCursor.setSelection(editTextBoldCursor.length());
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
            builder2.setPositiveButton(LocaleController.getString("OK", NUM), new DialogInterface.OnClickListener(tLRPC$User, editTextBoldCursor) {
                public final /* synthetic */ TLRPC$User f$1;
                public final /* synthetic */ EditText f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    ContactsActivity.this.lambda$didSelectResult$4$ContactsActivity(this.f$1, this.f$2, dialogInterface, i);
                }
            });
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

    public /* synthetic */ void lambda$didSelectResult$3$ContactsActivity(TLRPC$User tLRPC$User, String str, DialogInterface dialogInterface, int i) {
        ContactsActivityDelegate contactsActivityDelegate = this.delegate;
        if (contactsActivityDelegate != null) {
            contactsActivityDelegate.didSelectContact(tLRPC$User, str, this);
            this.delegate = null;
        }
    }

    public /* synthetic */ void lambda$didSelectResult$4$ContactsActivity(TLRPC$User tLRPC$User, EditText editText, DialogInterface dialogInterface, int i) {
        didSelectResult(tLRPC$User, false, editText != null ? editText.getText().toString() : "0");
    }

    public void onResume() {
        Activity parentActivity;
        super.onResume();
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
                AlertDialog create = AlertsCreator.createContactsPermissionDialog(parentActivity, new MessagesStorage.IntCallback() {
                    public final void run(int i) {
                        ContactsActivity.this.lambda$onResume$5$ContactsActivity(i);
                    }
                }).create();
                this.permissionDialog = create;
                showDialog(create);
                return;
            }
            askForPermissons(true);
        }
    }

    public /* synthetic */ void lambda$onResume$5$ContactsActivity(int i) {
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
                showDialog(AlertsCreator.createContactsPermissionDialog(parentActivity, new MessagesStorage.IntCallback() {
                    public final void run(int i) {
                        ContactsActivity.this.lambda$askForPermissons$6$ContactsActivity(i);
                    }
                }).create());
            }
        }
    }

    public /* synthetic */ void lambda$askForPermissons$6$ContactsActivity(int i) {
        this.askAboutContacts = i != 0;
        if (i != 0) {
            askForPermissons(false);
        }
    }

    public void onRequestPermissionsResultFragment(int i, String[] strArr, int[] iArr) {
        if (i == 1) {
            for (int i2 = 0; i2 < strArr.length; i2++) {
                if (iArr.length > i2 && "android.permission.READ_CONTACTS".equals(strArr[i2])) {
                    if (iArr[i2] == 0) {
                        ContactsController.getInstance(this.currentAccount).forceImportContacts();
                    } else {
                        SharedPreferences.Editor edit = MessagesController.getGlobalNotificationsSettings().edit();
                        this.askAboutContacts = false;
                        edit.putBoolean("askAboutContacts", false).commit();
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
                contactsAdapter2.notifyDataSetChanged();
            }
        } else if (i == NotificationCenter.updateInterfaces) {
            int intValue = objArr[0].intValue();
            if (!((intValue & 2) == 0 && (intValue & 1) == 0 && (intValue & 4) == 0)) {
                updateVisibleRows(intValue);
            }
            if ((intValue & 4) != 0 && !this.sortByName && (contactsAdapter = this.listViewAdapter) != null) {
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

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        $$Lambda$ContactsActivity$mX2AyzEZkfKNXN64OUntWgWNyBY r11 = new ThemeDescription.ThemeDescriptionDelegate() {
            public final void didSetColor() {
                ContactsActivity.this.lambda$getThemeDescriptions$7$ContactsActivity();
            }
        };
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
        arrayList.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "emptyListPlaceholder"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "fastScrollActive"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "fastScrollInactive"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "fastScrollText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        $$Lambda$ContactsActivity$mX2AyzEZkfKNXN64OUntWgWNyBY r9 = r11;
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) r9, "windowBackgroundWhiteGrayText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) r9, "windowBackgroundWhiteBlueText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, (Paint) null, Theme.avatarDrawables, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_text"));
        $$Lambda$ContactsActivity$mX2AyzEZkfKNXN64OUntWgWNyBY r8 = r11;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundRed"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundOrange"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundViolet"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundGreen"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundCyan"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundBlue"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundPink"));
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

    public /* synthetic */ void lambda$getThemeDescriptions$7$ContactsActivity() {
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
