package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.util.Property;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.ContactsController.Contact;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.SecretChatHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
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

public class ContactsActivity extends BaseFragment implements NotificationCenterDelegate {
    private static final int search_button = 0;
    private static final int sort_button = 1;
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
    private EmptyTextProgressView emptyView;
    private ImageView floatingButton;
    private FrameLayout floatingButtonContainer;
    private boolean floatingHidden;
    private AccelerateDecelerateInterpolator floatingInterpolator = new AccelerateDecelerateInterpolator();
    private boolean hasGps;
    private SparseArray<User> ignoreUsers;
    private LinearLayoutManager layoutManager;
    private RecyclerListView listView;
    private ContactsAdapter listViewAdapter;
    private boolean needFinishFragment = true;
    private boolean needForwardCount = true;
    private boolean needPhonebook;
    private boolean onlyUsers;
    private AlertDialog permissionDialog;
    private int prevPosition;
    private int prevTop;
    private boolean resetDelegate = true;
    private boolean returnAsResult;
    private boolean scrollUpdated;
    private SearchAdapter searchListViewAdapter;
    private boolean searchWas;
    private boolean searching;
    private String selectAlertString = null;
    private boolean sortByName;
    private ActionBarMenuItem sortItem;

    public interface ContactsActivityDelegate {
        void didSelectContact(User user, String str, ContactsActivity contactsActivity);
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
        if (!(this.createSecretChat || this.returnAsResult)) {
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

    /* JADX WARNING: Removed duplicated region for block: B:42:0x0152  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x014a  */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x01f7  */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x01f4  */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x0202  */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x01ff  */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x020e  */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x020b  */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x021d  */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x021a  */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x0228  */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x0225  */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x0267  */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x02c4  */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x0330  */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x032d  */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x0337  */
    public android.view.View createView(android.content.Context r25) {
        /*
        r24 = this;
        r8 = r24;
        r7 = r25;
        r6 = 0;
        r8.searching = r6;
        r8.searchWas = r6;
        r0 = r8.actionBar;
        r1 = NUM; // 0x7var_f3 float:1.794507E38 double:1.052935623E-314;
        r0.setBackButtonImage(r1);
        r0 = r8.actionBar;
        r5 = 1;
        r0.setAllowOverlayTitle(r5);
        r0 = r8.destroyAfterSelect;
        if (r0 == 0) goto L_0x0050;
    L_0x001b:
        r0 = r8.returnAsResult;
        if (r0 == 0) goto L_0x002e;
    L_0x001f:
        r0 = r8.actionBar;
        r1 = NUM; // 0x7f0e09ff float:1.8880228E38 double:1.053163421E-314;
        r2 = "SelectContact";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r0.setTitle(r1);
        goto L_0x005e;
    L_0x002e:
        r0 = r8.createSecretChat;
        if (r0 == 0) goto L_0x0041;
    L_0x0032:
        r0 = r8.actionBar;
        r1 = NUM; // 0x7f0e069b float:1.8878467E38 double:1.053162992E-314;
        r2 = "NewSecretChat";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r0.setTitle(r1);
        goto L_0x005e;
    L_0x0041:
        r0 = r8.actionBar;
        r1 = NUM; // 0x7f0e0693 float:1.8878451E38 double:1.053162988E-314;
        r2 = "NewMessageTitle";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r0.setTitle(r1);
        goto L_0x005e;
    L_0x0050:
        r0 = r8.actionBar;
        r1 = NUM; // 0x7f0e0334 float:1.88767E38 double:1.053162562E-314;
        r2 = "Contacts";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r0.setTitle(r1);
    L_0x005e:
        r0 = r8.actionBar;
        r1 = new org.telegram.ui.ContactsActivity$1;
        r1.<init>();
        r0.setActionBarMenuOnItemClick(r1);
        r0 = r8.actionBar;
        r0 = r0.createMenu();
        r1 = NUM; // 0x7var_fd float:1.7945091E38 double:1.052935628E-314;
        r1 = r0.addItem(r6, r1);
        r1 = r1.setIsSearchField(r5);
        r2 = new org.telegram.ui.ContactsActivity$2;
        r2.<init>();
        r1 = r1.setActionBarMenuItemSearchListener(r2);
        r2 = NUM; // 0x7f0e09d6 float:1.8880145E38 double:1.0531634007E-314;
        r3 = "Search";
        r4 = org.telegram.messenger.LocaleController.getString(r3, r2);
        r1.setSearchFieldHint(r4);
        r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
        r1.setContentDescription(r2);
        r1 = r8.createSecretChat;
        if (r1 != 0) goto L_0x00bc;
    L_0x0099:
        r1 = r8.returnAsResult;
        if (r1 != 0) goto L_0x00bc;
    L_0x009d:
        r1 = r8.sortByName;
        if (r1 == 0) goto L_0x00a5;
    L_0x00a1:
        r1 = NUM; // 0x7var_ad float:1.7944929E38 double:1.0529355885E-314;
        goto L_0x00a8;
    L_0x00a5:
        r1 = NUM; // 0x7var_ac float:1.7944927E38 double:1.052935588E-314;
    L_0x00a8:
        r0 = r0.addItem(r5, r1);
        r8.sortItem = r0;
        r0 = r8.sortItem;
        r1 = NUM; // 0x7f0e001e float:1.8875098E38 double:1.0531621715E-314;
        r2 = "AccDescrContactSorting";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r0.setContentDescription(r1);
    L_0x00bc:
        r0 = new org.telegram.ui.Adapters.SearchAdapter;
        r11 = r8.ignoreUsers;
        r12 = r8.allowUsernameSearch;
        r13 = 0;
        r14 = 0;
        r15 = r8.allowBots;
        r1 = r8.allowSelf;
        r17 = 1;
        r18 = 0;
        r9 = r0;
        r10 = r25;
        r16 = r1;
        r9.<init>(r10, r11, r12, r13, r14, r15, r16, r17, r18);
        r8.searchListViewAdapter = r0;
        r0 = r8.chatId;
        r9 = 3;
        r10 = 2;
        if (r0 == 0) goto L_0x00f2;
    L_0x00dc:
        r0 = r8.currentAccount;
        r0 = org.telegram.messenger.MessagesController.getInstance(r0);
        r1 = r8.chatId;
        r1 = java.lang.Integer.valueOf(r1);
        r0 = r0.getChat(r1);
        r0 = org.telegram.messenger.ChatObject.canUserDoAdminAction(r0, r9);
    L_0x00f0:
        r11 = r0;
        goto L_0x0119;
    L_0x00f2:
        r0 = r8.channelId;
        if (r0 == 0) goto L_0x0118;
    L_0x00f6:
        r0 = r8.currentAccount;
        r0 = org.telegram.messenger.MessagesController.getInstance(r0);
        r1 = r8.channelId;
        r1 = java.lang.Integer.valueOf(r1);
        r0 = r0.getChat(r1);
        r1 = org.telegram.messenger.ChatObject.canUserDoAdminAction(r0, r9);
        if (r1 == 0) goto L_0x0116;
    L_0x010c:
        r0 = r0.username;
        r0 = android.text.TextUtils.isEmpty(r0);
        if (r0 == 0) goto L_0x0116;
    L_0x0114:
        r0 = 2;
        goto L_0x00f0;
    L_0x0116:
        r0 = 0;
        goto L_0x00f0;
    L_0x0118:
        r11 = 0;
    L_0x0119:
        r0 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ all -> 0x0128 }
        r0 = r0.getPackageManager();	 Catch:{ all -> 0x0128 }
        r1 = "android.hardware.location.gps";
        r0 = r0.hasSystemFeature(r1);	 Catch:{ all -> 0x0128 }
        r8.hasGps = r0;	 Catch:{ all -> 0x0128 }
        goto L_0x012a;
    L_0x0128:
        r8.hasGps = r6;
    L_0x012a:
        r12 = new org.telegram.ui.ContactsActivity$3;
        r3 = r8.onlyUsers;
        r4 = r8.needPhonebook;
        r13 = r8.ignoreUsers;
        r14 = r8.hasGps;
        r0 = r12;
        r1 = r24;
        r2 = r25;
        r15 = 1;
        r5 = r13;
        r13 = 0;
        r6 = r11;
        r9 = r7;
        r7 = r14;
        r0.<init>(r2, r3, r4, r5, r6, r7);
        r8.listViewAdapter = r12;
        r0 = r8.listViewAdapter;
        r1 = r8.sortItem;
        if (r1 == 0) goto L_0x0152;
    L_0x014a:
        r1 = r8.sortByName;
        if (r1 == 0) goto L_0x0150;
    L_0x014e:
        r1 = 1;
        goto L_0x0153;
    L_0x0150:
        r1 = 2;
        goto L_0x0153;
    L_0x0152:
        r1 = 0;
    L_0x0153:
        r0.setSortType(r1);
        r0 = r8.listViewAdapter;
        r1 = r8.disableSections;
        r0.setDisableSections(r1);
        r0 = new org.telegram.ui.ContactsActivity$4;
        r0.<init>(r9);
        r8.fragmentView = r0;
        r0 = r8.fragmentView;
        r0 = (android.widget.FrameLayout) r0;
        r1 = new org.telegram.ui.Components.EmptyTextProgressView;
        r1.<init>(r9);
        r8.emptyView = r1;
        r1 = r8.emptyView;
        r1.setShowAtCenter(r15);
        r1 = r8.emptyView;
        r2 = NUM; // 0x7f0e06b0 float:1.887851E38 double:1.0531630025E-314;
        r3 = "NoContacts";
        r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
        r1.setText(r2);
        r1 = r8.emptyView;
        r1.showTextView();
        r1 = r8.emptyView;
        r2 = -NUM; // 0xffffffffbvar_ float:-1.0 double:NaN;
        r3 = -1;
        r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r3, r2);
        r0.addView(r1, r4);
        r1 = new org.telegram.ui.ContactsActivity$5;
        r1.<init>(r9);
        r8.listView = r1;
        r1 = r8.listView;
        r1.setSectionsType(r15);
        r1 = r8.listView;
        r1.setVerticalScrollBarEnabled(r13);
        r1 = r8.listView;
        r1.setFastScrollEnabled();
        r1 = r8.listView;
        r4 = new androidx.recyclerview.widget.LinearLayoutManager;
        r4.<init>(r9, r15, r13);
        r8.layoutManager = r4;
        r1.setLayoutManager(r4);
        r1 = r8.listView;
        r4 = r8.listViewAdapter;
        r1.setAdapter(r4);
        r1 = r8.listView;
        r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r3, r2);
        r0.addView(r1, r2);
        r1 = r8.listView;
        r2 = new org.telegram.ui.-$$Lambda$ContactsActivity$gnTnCjocys4rzqdEYocufNT6sRA;
        r2.<init>(r8, r11);
        r1.setOnItemClickListener(r2);
        r1 = r8.listView;
        r2 = new org.telegram.ui.ContactsActivity$6;
        r2.<init>();
        r1.setOnScrollListener(r2);
        r1 = r8.createSecretChat;
        if (r1 != 0) goto L_0x0349;
    L_0x01dd:
        r1 = r8.returnAsResult;
        if (r1 != 0) goto L_0x0349;
    L_0x01e1:
        r1 = new android.widget.FrameLayout;
        r1.<init>(r9);
        r8.floatingButtonContainer = r1;
        r1 = r8.floatingButtonContainer;
        r2 = android.os.Build.VERSION.SDK_INT;
        r3 = 56;
        r4 = 60;
        r5 = 21;
        if (r2 < r5) goto L_0x01f7;
    L_0x01f4:
        r2 = 56;
        goto L_0x01f9;
    L_0x01f7:
        r2 = 60;
    L_0x01f9:
        r17 = r2 + 20;
        r2 = android.os.Build.VERSION.SDK_INT;
        if (r2 < r5) goto L_0x0202;
    L_0x01ff:
        r2 = 56;
        goto L_0x0204;
    L_0x0202:
        r2 = 60;
    L_0x0204:
        r2 = r2 + 14;
        r2 = (float) r2;
        r6 = org.telegram.messenger.LocaleController.isRTL;
        if (r6 == 0) goto L_0x020e;
    L_0x020b:
        r16 = 3;
        goto L_0x0211;
    L_0x020e:
        r6 = 5;
        r16 = 5;
    L_0x0211:
        r19 = r16 | 80;
        r6 = org.telegram.messenger.LocaleController.isRTL;
        r7 = 0;
        r11 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        if (r6 == 0) goto L_0x021d;
    L_0x021a:
        r20 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        goto L_0x021f;
    L_0x021d:
        r20 = 0;
    L_0x021f:
        r21 = 0;
        r6 = org.telegram.messenger.LocaleController.isRTL;
        if (r6 == 0) goto L_0x0228;
    L_0x0225:
        r22 = 0;
        goto L_0x022a;
    L_0x0228:
        r22 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
    L_0x022a:
        r23 = 0;
        r18 = r2;
        r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r17, r18, r19, r20, r21, r22, r23);
        r0.addView(r1, r2);
        r0 = r8.floatingButtonContainer;
        r1 = new org.telegram.ui.-$$Lambda$ContactsActivity$XQCfC9ukjnkgkb2J-v7QLg6mf6E;
        r1.<init>(r8);
        r0.setOnClickListener(r1);
        r0 = new android.widget.ImageView;
        r0.<init>(r9);
        r8.floatingButton = r0;
        r0 = r8.floatingButton;
        r1 = android.widget.ImageView.ScaleType.CENTER;
        r0.setScaleType(r1);
        r0 = NUM; // 0x42600000 float:56.0 double:5.50185432E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r2 = "chats_actionBackground";
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r2);
        r6 = "chats_actionPressedBackground";
        r6 = org.telegram.ui.ActionBar.Theme.getColor(r6);
        r1 = org.telegram.ui.ActionBar.Theme.createSimpleSelectorCircleDrawable(r1, r2, r6);
        r2 = android.os.Build.VERSION.SDK_INT;
        if (r2 >= r5) goto L_0x0293;
    L_0x0267:
        r2 = r25.getResources();
        r6 = NUM; // 0x7var_ce float:1.7944996E38 double:1.052935605E-314;
        r2 = r2.getDrawable(r6);
        r2 = r2.mutate();
        r6 = new android.graphics.PorterDuffColorFilter;
        r7 = -16777216; // 0xfffffffffvar_ float:-1.7014118E38 double:NaN;
        r9 = android.graphics.PorterDuff.Mode.MULTIPLY;
        r6.<init>(r7, r9);
        r2.setColorFilter(r6);
        r6 = new org.telegram.ui.Components.CombinedDrawable;
        r6.<init>(r2, r1, r13, r13);
        r1 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r6.setIconSize(r1, r0);
        r1 = r6;
    L_0x0293:
        r0 = r8.floatingButton;
        r0.setBackgroundDrawable(r1);
        r0 = r8.floatingButton;
        r1 = new android.graphics.PorterDuffColorFilter;
        r2 = "chats_actionIcon";
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r2);
        r6 = android.graphics.PorterDuff.Mode.MULTIPLY;
        r1.<init>(r2, r6);
        r0.setColorFilter(r1);
        r0 = r8.floatingButton;
        r1 = NUM; // 0x7var_ float:1.7944724E38 double:1.0529355386E-314;
        r0.setImageResource(r1);
        r0 = r8.floatingButtonContainer;
        r1 = NUM; // 0x7f0e0352 float:1.8876762E38 double:1.0531625766E-314;
        r2 = "CreateNewContact";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r0.setContentDescription(r1);
        r0 = android.os.Build.VERSION.SDK_INT;
        if (r0 < r5) goto L_0x0325;
    L_0x02c4:
        r0 = new android.animation.StateListAnimator;
        r0.<init>();
        r1 = new int[r15];
        r2 = 16842919; // 0x10100a7 float:2.3694026E-38 double:8.3215077E-317;
        r1[r13] = r2;
        r2 = r8.floatingButton;
        r6 = android.view.View.TRANSLATION_Z;
        r7 = new float[r10];
        r9 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r12 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r12 = (float) r12;
        r7[r13] = r12;
        r12 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r12 = (float) r12;
        r7[r15] = r12;
        r2 = android.animation.ObjectAnimator.ofFloat(r2, r6, r7);
        r6 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;
        r2 = r2.setDuration(r6);
        r0.addState(r1, r2);
        r1 = new int[r13];
        r2 = r8.floatingButton;
        r6 = android.view.View.TRANSLATION_Z;
        r7 = new float[r10];
        r10 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r10 = (float) r10;
        r7[r13] = r10;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r9 = (float) r9;
        r7[r15] = r9;
        r2 = android.animation.ObjectAnimator.ofFloat(r2, r6, r7);
        r6 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;
        r2 = r2.setDuration(r6);
        r0.addState(r1, r2);
        r1 = r8.floatingButton;
        r1.setStateListAnimator(r0);
        r0 = r8.floatingButton;
        r1 = new org.telegram.ui.ContactsActivity$7;
        r1.<init>();
        r0.setOutlineProvider(r1);
    L_0x0325:
        r0 = r8.floatingButtonContainer;
        r1 = r8.floatingButton;
        r2 = android.os.Build.VERSION.SDK_INT;
        if (r2 < r5) goto L_0x0330;
    L_0x032d:
        r9 = 56;
        goto L_0x0332;
    L_0x0330:
        r9 = 60;
    L_0x0332:
        r2 = android.os.Build.VERSION.SDK_INT;
        if (r2 < r5) goto L_0x0337;
    L_0x0336:
        goto L_0x0339;
    L_0x0337:
        r3 = 60;
    L_0x0339:
        r10 = (float) r3;
        r11 = 51;
        r12 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r13 = 0;
        r14 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r15 = 0;
        r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r10, r11, r12, r13, r14, r15);
        r0.addView(r1, r2);
    L_0x0349:
        r0 = r8.fragmentView;
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ContactsActivity.createView(android.content.Context):android.view.View");
    }

    public /* synthetic */ void lambda$createView$1$ContactsActivity(int i, View view, int i2) {
        String str = "user_id";
        boolean z = true;
        Object item;
        User user;
        SparseArray sparseArray;
        Bundle bundle;
        String str2;
        if (this.searching && this.searchWas) {
            item = this.searchListViewAdapter.getItem(i2);
            if (item instanceof User) {
                user = (User) item;
                if (user != null) {
                    if (this.searchListViewAdapter.isGlobalSearch(i2)) {
                        ArrayList arrayList = new ArrayList();
                        arrayList.add(user);
                        MessagesController.getInstance(this.currentAccount).putUsers(arrayList, false);
                        MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(arrayList, null, false, true);
                    }
                    if (this.returnAsResult) {
                        sparseArray = this.ignoreUsers;
                        if (sparseArray == null || sparseArray.indexOfKey(user.id) < 0) {
                            didSelectResult(user, true, null);
                        }
                    } else if (!this.createSecretChat) {
                        bundle = new Bundle();
                        bundle.putInt(str, user.id);
                        if (MessagesController.getInstance(this.currentAccount).checkCanOpenChat(bundle, this)) {
                            presentFragment(new ChatActivity(bundle), true);
                        }
                    } else if (user.id != UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                        this.creatingChat = true;
                        SecretChatHelper.getInstance(this.currentAccount).startSecretChat(getParentActivity(), user);
                    }
                }
            } else if (item instanceof String) {
                str2 = (String) item;
                if (!str2.equals("section")) {
                    NewContactActivity newContactActivity = new NewContactActivity();
                    newContactActivity.setInitialPhoneNumber(str2);
                    presentFragment(newContactActivity);
                }
            }
        } else {
            int sectionForPosition = this.listViewAdapter.getSectionForPosition(i2);
            i2 = this.listViewAdapter.getPositionInSectionForPosition(i2);
            if (i2 >= 0 && sectionForPosition >= 0) {
                Bundle bundle2;
                if ((this.onlyUsers && i == 0) || sectionForPosition != 0) {
                    item = this.listViewAdapter.getItem(sectionForPosition, i2);
                    if (item instanceof User) {
                        user = (User) item;
                        if (this.returnAsResult) {
                            sparseArray = this.ignoreUsers;
                            if (sparseArray == null || sparseArray.indexOfKey(user.id) < 0) {
                                didSelectResult(user, true, null);
                            }
                        } else if (this.createSecretChat) {
                            this.creatingChat = true;
                            SecretChatHelper.getInstance(this.currentAccount).startSecretChat(getParentActivity(), user);
                        } else {
                            bundle = new Bundle();
                            bundle.putInt(str, user.id);
                            if (MessagesController.getInstance(this.currentAccount).checkCanOpenChat(bundle, this)) {
                                presentFragment(new ChatActivity(bundle), true);
                            }
                        }
                    } else if (item instanceof Contact) {
                        Contact contact = (Contact) item;
                        str2 = !contact.phones.isEmpty() ? (String) contact.phones.get(0) : null;
                        if (!(str2 == null || getParentActivity() == null)) {
                            Builder builder = new Builder(getParentActivity());
                            builder.setMessage(LocaleController.getString("InviteUser", NUM));
                            builder.setTitle(LocaleController.getString("AppName", NUM));
                            builder.setPositiveButton(LocaleController.getString("OK", NUM), new -$$Lambda$ContactsActivity$UtCNWtIF8nw25DCjjOFgiXVoPRQ(this, str2));
                            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
                            showDialog(builder.create());
                        }
                    }
                } else if (this.needPhonebook) {
                    if (i2 == 0) {
                        presentFragment(new InviteContactsActivity());
                    } else if (i2 == 1 && this.hasGps) {
                        if (VERSION.SDK_INT >= 23) {
                            Activity parentActivity = getParentActivity();
                            if (!(parentActivity == null || parentActivity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == 0)) {
                                presentFragment(new ActionIntroActivity(1));
                                return;
                            }
                        }
                        i = VERSION.SDK_INT;
                        if (i >= 28) {
                            z = ((LocationManager) ApplicationLoader.applicationContext.getSystemService("location")).isLocationEnabled();
                        } else if (i >= 19) {
                            try {
                                if (Secure.getInt(ApplicationLoader.applicationContext.getContentResolver(), "location_mode", 0) == 0) {
                                    z = false;
                                }
                            } catch (Throwable th) {
                                FileLog.e(th);
                            }
                        }
                        if (z) {
                            presentFragment(new PeopleNearbyActivity());
                        } else {
                            presentFragment(new ActionIntroActivity(4));
                        }
                    }
                } else if (i != 0) {
                    if (i2 == 0) {
                        sectionForPosition = this.chatId;
                        if (sectionForPosition == 0) {
                            sectionForPosition = this.channelId;
                        }
                        presentFragment(new GroupInviteActivity(sectionForPosition));
                    }
                } else if (i2 == 0) {
                    presentFragment(new GroupCreateActivity(new Bundle()), false);
                } else if (i2 == 1) {
                    bundle2 = new Bundle();
                    bundle2.putBoolean("onlyUsers", true);
                    bundle2.putBoolean("destroyAfterSelect", true);
                    bundle2.putBoolean("createSecretChat", true);
                    bundle2.putBoolean("allowBots", false);
                    bundle2.putBoolean("allowSelf", false);
                    presentFragment(new ContactsActivity(bundle2), false);
                } else if (i2 == 2) {
                    SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
                    String str3 = "channel_intro";
                    if (BuildVars.DEBUG_VERSION || !globalMainSettings.getBoolean(str3, false)) {
                        presentFragment(new ActionIntroActivity(0));
                        globalMainSettings.edit().putBoolean(str3, true).commit();
                    } else {
                        bundle2 = new Bundle();
                        bundle2.putInt("step", 0);
                        presentFragment(new ChannelCreateActivity(bundle2));
                    }
                }
            }
        }
    }

    public /* synthetic */ void lambda$null$0$ContactsActivity(String str, DialogInterface dialogInterface, int i) {
        try {
            Intent intent = new Intent("android.intent.action.VIEW", Uri.fromParts("sms", str, null));
            intent.putExtra("sms_body", ContactsController.getInstance(this.currentAccount).getInviteText(1));
            getParentActivity().startActivityForResult(intent, 500);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$createView$2$ContactsActivity(View view) {
        presentFragment(new NewContactActivity());
    }

    private void didSelectResult(User user, boolean z, String str) {
        if (!z || this.selectAlertString == null) {
            ContactsActivityDelegate contactsActivityDelegate = this.delegate;
            if (contactsActivityDelegate != null) {
                contactsActivityDelegate.didSelectContact(user, str, this);
                if (this.resetDelegate) {
                    this.delegate = null;
                }
            }
            if (this.needFinishFragment) {
                finishFragment();
            }
        } else if (getParentActivity() != null) {
            EditText editText;
            String str2 = "Cancel";
            String str3 = "OK";
            String str4 = "AppName";
            if (user.bot) {
                if (user.bot_nochats) {
                    try {
                        Toast.makeText(getParentActivity(), LocaleController.getString("BotCantJoinGroups", NUM), 0).show();
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                    return;
                } else if (this.channelId != 0) {
                    Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.channelId));
                    Builder builder = new Builder(getParentActivity());
                    if (ChatObject.canAddAdmins(chat)) {
                        builder.setTitle(LocaleController.getString(str4, NUM));
                        builder.setMessage(LocaleController.getString("AddBotAsAdmin", NUM));
                        builder.setPositiveButton(LocaleController.getString("MakeAdmin", NUM), new -$$Lambda$ContactsActivity$mmdvMVULktgHXYl_8FoV1fI5DjI(this, user, str));
                        builder.setNegativeButton(LocaleController.getString(str2, NUM), null);
                    } else {
                        builder.setMessage(LocaleController.getString("CantAddBotAsAdmin", NUM));
                        builder.setPositiveButton(LocaleController.getString(str3, NUM), null);
                    }
                    showDialog(builder.create());
                    return;
                }
            }
            Builder builder2 = new Builder(getParentActivity());
            builder2.setTitle(LocaleController.getString(str4, NUM));
            CharSequence formatStringSimple = LocaleController.formatStringSimple(this.selectAlertString, UserObject.getUserName(user));
            if (user.bot || !this.needForwardCount) {
                editText = null;
            } else {
                formatStringSimple = String.format("%s\n\n%s", new Object[]{formatStringSimple, LocaleController.getString("AddToTheGroupForwardCount", NUM)});
                editText = new EditTextBoldCursor(getParentActivity());
                editText.setTextSize(1, 18.0f);
                editText.setText("50");
                editText.setTextColor(Theme.getColor("dialogTextBlack"));
                editText.setGravity(17);
                editText.setInputType(2);
                editText.setImeOptions(6);
                editText.setBackgroundDrawable(Theme.createEditTextDrawable(getParentActivity(), true));
                editText.addTextChangedListener(new TextWatcher() {
                    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    }

                    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    }

                    public void afterTextChanged(Editable editable) {
                        String str = "";
                        try {
                            String obj = editable.toString();
                            if (obj.length() != 0) {
                                int intValue = Utilities.parseInt(obj).intValue();
                                if (intValue < 0) {
                                    editText.setText("0");
                                    editText.setSelection(editText.length());
                                } else if (intValue > 300) {
                                    editText.setText("300");
                                    editText.setSelection(editText.length());
                                } else {
                                    StringBuilder stringBuilder = new StringBuilder();
                                    stringBuilder.append(str);
                                    stringBuilder.append(intValue);
                                    if (!obj.equals(stringBuilder.toString())) {
                                        EditText editText = editText;
                                        stringBuilder = new StringBuilder();
                                        stringBuilder.append(str);
                                        stringBuilder.append(intValue);
                                        editText.setText(stringBuilder.toString());
                                        editText.setSelection(editText.length());
                                    }
                                }
                            }
                        } catch (Exception e) {
                            FileLog.e(e);
                        }
                    }
                });
                builder2.setView(editText);
            }
            builder2.setMessage(formatStringSimple);
            builder2.setPositiveButton(LocaleController.getString(str3, NUM), new -$$Lambda$ContactsActivity$YV_dxfbBQkZGaBy_yZbP6YhG1n0(this, user, editText));
            builder2.setNegativeButton(LocaleController.getString(str2, NUM), null);
            showDialog(builder2.create());
            if (editText != null) {
                MarginLayoutParams marginLayoutParams = (MarginLayoutParams) editText.getLayoutParams();
                if (marginLayoutParams != null) {
                    if (marginLayoutParams instanceof LayoutParams) {
                        ((LayoutParams) marginLayoutParams).gravity = 1;
                    }
                    int dp = AndroidUtilities.dp(24.0f);
                    marginLayoutParams.leftMargin = dp;
                    marginLayoutParams.rightMargin = dp;
                    marginLayoutParams.height = AndroidUtilities.dp(36.0f);
                    editText.setLayoutParams(marginLayoutParams);
                }
                editText.setSelection(editText.getText().length());
            }
        }
    }

    public /* synthetic */ void lambda$didSelectResult$3$ContactsActivity(User user, String str, DialogInterface dialogInterface, int i) {
        ContactsActivityDelegate contactsActivityDelegate = this.delegate;
        if (contactsActivityDelegate != null) {
            contactsActivityDelegate.didSelectContact(user, str, this);
            this.delegate = null;
        }
    }

    public /* synthetic */ void lambda$didSelectResult$4$ContactsActivity(User user, EditText editText, DialogInterface dialogInterface, int i) {
        didSelectResult(user, false, editText != null ? editText.getText().toString() : "0");
    }

    public void onResume() {
        super.onResume();
        ContactsAdapter contactsAdapter = this.listViewAdapter;
        if (contactsAdapter != null) {
            contactsAdapter.notifyDataSetChanged();
        }
        if (this.checkPermission && VERSION.SDK_INT >= 23) {
            Activity parentActivity = getParentActivity();
            if (parentActivity != null) {
                this.checkPermission = false;
                String str = "android.permission.READ_CONTACTS";
                if (parentActivity.checkSelfPermission(str) == 0) {
                    return;
                }
                if (parentActivity.shouldShowRequestPermissionRationale(str)) {
                    AlertDialog create = AlertsCreator.createContactsPermissionDialog(parentActivity, new -$$Lambda$ContactsActivity$xFVgeouE3JWG-nldltsKWVnF4Hg(this)).create();
                    this.permissionDialog = create;
                    showDialog(create);
                    return;
                }
                askForPermissons(true);
            }
        }
    }

    public /* synthetic */ void lambda$onResume$5$ContactsActivity(int i) {
        this.askAboutContacts = i != 0;
        if (i != 0) {
            askForPermissons(false);
        }
    }

    /* Access modifiers changed, original: protected */
    public RecyclerListView getListView() {
        return this.listView;
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        FrameLayout frameLayout = this.floatingButtonContainer;
        if (frameLayout != null) {
            frameLayout.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                public void onGlobalLayout() {
                    ContactsActivity.this.floatingButtonContainer.setTranslationY((float) (ContactsActivity.this.floatingHidden ? AndroidUtilities.dp(100.0f) : 0));
                    ContactsActivity.this.floatingButtonContainer.setClickable(ContactsActivity.this.floatingHidden ^ 1);
                    if (ContactsActivity.this.floatingButtonContainer != null) {
                        ContactsActivity.this.floatingButtonContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            });
        }
    }

    /* Access modifiers changed, original: protected */
    public void onDialogDismiss(Dialog dialog) {
        super.onDialogDismiss(dialog);
        Dialog dialog2 = this.permissionDialog;
        if (dialog2 != null && dialog == dialog2 && getParentActivity() != null && this.askAboutContacts) {
            askForPermissons(false);
        }
    }

    @TargetApi(23)
    private void askForPermissons(boolean z) {
        Activity parentActivity = getParentActivity();
        if (parentActivity != null && UserConfig.getInstance(this.currentAccount).syncContacts) {
            String str = "android.permission.READ_CONTACTS";
            if (parentActivity.checkSelfPermission(str) != 0) {
                if (z && this.askAboutContacts) {
                    showDialog(AlertsCreator.createContactsPermissionDialog(parentActivity, new -$$Lambda$ContactsActivity$mwsX5pHa6b8khBUhCBIBphXHFEY(this)).create());
                    return;
                }
                ArrayList arrayList = new ArrayList();
                arrayList.add(str);
                arrayList.add("android.permission.WRITE_CONTACTS");
                arrayList.add("android.permission.GET_ACCOUNTS");
                try {
                    parentActivity.requestPermissions((String[]) arrayList.toArray(new String[0]), 1);
                } catch (Exception e) {
                    FileLog.e(e);
                }
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
                if (iArr.length > i2) {
                    if ("android.permission.READ_CONTACTS".equals(strArr[i2])) {
                        if (iArr[i2] == 0) {
                            ContactsController.getInstance(this.currentAccount).forceImportContacts();
                        } else {
                            Editor edit = MessagesController.getGlobalNotificationsSettings().edit();
                            this.askAboutContacts = false;
                            edit.putBoolean("askAboutContacts", false).commit();
                        }
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
            contactsAdapter = this.listViewAdapter;
            if (contactsAdapter != null) {
                contactsAdapter.notifyDataSetChanged();
            }
        } else if (i == NotificationCenter.updateInterfaces) {
            i = ((Integer) objArr[0]).intValue();
            if (!((i & 2) == 0 && (i & 1) == 0 && (i & 4) == 0)) {
                updateVisibleRows(i);
            }
            if ((i & 4) != 0 && !this.sortByName) {
                contactsAdapter = this.listViewAdapter;
                if (contactsAdapter != null) {
                    contactsAdapter.sortOnlineContacts();
                }
            }
        } else if (i == NotificationCenter.encryptedChatCreated) {
            if (this.createSecretChat && this.creatingChat) {
                EncryptedChat encryptedChat = (EncryptedChat) objArr[0];
                Bundle bundle = new Bundle();
                bundle.putInt("enc_id", encryptedChat.id);
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

    private void hideFloatingButton(boolean z) {
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
            this.floatingButtonContainer.setClickable(z ^ 1);
            animatorSet.start();
        }
    }

    public void setDelegate(ContactsActivityDelegate contactsActivityDelegate) {
        this.delegate = contactsActivityDelegate;
    }

    public void setIgnoreUsers(SparseArray<User> sparseArray) {
        this.ignoreUsers = sparseArray;
    }

    public ThemeDescription[] getThemeDescriptions() {
        -$$Lambda$ContactsActivity$mX2AyzEZkfKNXN64OUntWgWNyBY -__lambda_contactsactivity_mx2ayzezkfknxn64ountwgwnyby = new -$$Lambda$ContactsActivity$mX2AyzEZkfKNXN64OUntWgWNyBY(this);
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[41];
        themeDescriptionArr[0] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite");
        themeDescriptionArr[1] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
        themeDescriptionArr[2] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
        themeDescriptionArr[3] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
        themeDescriptionArr[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, "actionBarDefaultSearch");
        themeDescriptionArr[7] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, "actionBarDefaultSearchPlaceholder");
        themeDescriptionArr[8] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        View view = this.listView;
        int i = ThemeDescription.FLAG_SECTIONS;
        Class[] clsArr = new Class[]{LetterSectionCell.class};
        String[] strArr = new String[1];
        strArr[0] = "textView";
        themeDescriptionArr[9] = new ThemeDescription(view, i, clsArr, strArr, null, null, null, "windowBackgroundWhiteGrayText4");
        themeDescriptionArr[10] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider");
        themeDescriptionArr[11] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "emptyListPlaceholder");
        themeDescriptionArr[12] = new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, "fastScrollActive");
        themeDescriptionArr[13] = new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, "fastScrollInactive");
        themeDescriptionArr[14] = new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, "fastScrollText");
        themeDescriptionArr[15] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"nameTextView"}, null, null, null, "windowBackgroundWhiteBlackText");
        ThemeDescriptionDelegate themeDescriptionDelegate = -__lambda_contactsactivity_mx2ayzezkfknxn64ountwgwnyby;
        themeDescriptionArr[16] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusColor"}, null, null, themeDescriptionDelegate, "windowBackgroundWhiteGrayText");
        themeDescriptionArr[17] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusOnlineColor"}, null, null, themeDescriptionDelegate, "windowBackgroundWhiteBlueText");
        themeDescriptionArr[18] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, null, new Drawable[]{Theme.avatar_savedDrawable}, null, "avatar_text");
        -$$Lambda$ContactsActivity$mX2AyzEZkfKNXN64OUntWgWNyBY -__lambda_contactsactivity_mx2ayzezkfknxn64ountwgwnyby2 = -__lambda_contactsactivity_mx2ayzezkfknxn64ountwgwnyby;
        themeDescriptionArr[19] = new ThemeDescription(null, 0, null, null, null, -__lambda_contactsactivity_mx2ayzezkfknxn64ountwgwnyby2, "avatar_backgroundRed");
        themeDescriptionArr[20] = new ThemeDescription(null, 0, null, null, null, -__lambda_contactsactivity_mx2ayzezkfknxn64ountwgwnyby2, "avatar_backgroundOrange");
        themeDescriptionArr[21] = new ThemeDescription(null, 0, null, null, null, -__lambda_contactsactivity_mx2ayzezkfknxn64ountwgwnyby2, "avatar_backgroundViolet");
        themeDescriptionArr[22] = new ThemeDescription(null, 0, null, null, null, -__lambda_contactsactivity_mx2ayzezkfknxn64ountwgwnyby2, "avatar_backgroundGreen");
        themeDescriptionArr[23] = new ThemeDescription(null, 0, null, null, null, -__lambda_contactsactivity_mx2ayzezkfknxn64ountwgwnyby2, "avatar_backgroundCyan");
        themeDescriptionArr[24] = new ThemeDescription(null, 0, null, null, null, -__lambda_contactsactivity_mx2ayzezkfknxn64ountwgwnyby2, "avatar_backgroundBlue");
        themeDescriptionArr[25] = new ThemeDescription(null, 0, null, null, null, -__lambda_contactsactivity_mx2ayzezkfknxn64ountwgwnyby2, "avatar_backgroundPink");
        View view2 = this.listView;
        themeDescriptionArr[26] = new ThemeDescription(view2, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        view2 = this.listView;
        themeDescriptionArr[27] = new ThemeDescription(view2, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlueText2");
        themeDescriptionArr[28] = new ThemeDescription(this.listView, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, null, null, null, "windowBackgroundWhiteGrayIcon");
        themeDescriptionArr[29] = new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "chats_actionIcon");
        themeDescriptionArr[30] = new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "chats_actionBackground");
        themeDescriptionArr[31] = new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "chats_actionPressedBackground");
        themeDescriptionArr[32] = new ThemeDescription(this.listView, 0, new Class[]{GraySectionCell.class}, new String[]{"textView"}, null, null, null, "key_graySectionText");
        themeDescriptionArr[33] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GraySectionCell.class}, null, null, null, "graySection");
        themeDescriptionArr[34] = new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, null, new Drawable[]{Theme.dialogs_groupDrawable, Theme.dialogs_broadcastDrawable, Theme.dialogs_botDrawable}, null, "chats_nameIcon");
        themeDescriptionArr[35] = new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, null, new Drawable[]{Theme.dialogs_verifiedCheckDrawable}, null, "chats_verifiedCheck");
        themeDescriptionArr[36] = new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, null, new Drawable[]{Theme.dialogs_verifiedDrawable}, null, "chats_verifiedBackground");
        themeDescriptionArr[37] = new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, Theme.dialogs_offlinePaint, null, null, "windowBackgroundWhiteGrayText3");
        themeDescriptionArr[38] = new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, Theme.dialogs_onlinePaint, null, null, "windowBackgroundWhiteBlueText3");
        view = this.listView;
        clsArr = new Class[]{ProfileSearchCell.class};
        r4 = new Paint[3];
        TextPaint[] textPaintArr = Theme.dialogs_namePaint;
        r4[0] = textPaintArr[0];
        r4[1] = textPaintArr[1];
        r4[2] = Theme.dialogs_searchNamePaint;
        themeDescriptionArr[39] = new ThemeDescription(view, 0, clsArr, null, r4, null, null, "chats_name");
        view = this.listView;
        clsArr = new Class[]{ProfileSearchCell.class};
        r4 = new Paint[3];
        textPaintArr = Theme.dialogs_nameEncryptedPaint;
        r4[0] = textPaintArr[0];
        r4[1] = textPaintArr[1];
        r4[2] = Theme.dialogs_searchNameEncryptedPaint;
        themeDescriptionArr[40] = new ThemeDescription(view, 0, clsArr, null, r4, null, null, "chats_secretName");
        return themeDescriptionArr;
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
