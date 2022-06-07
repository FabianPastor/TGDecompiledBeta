package org.telegram.messenger;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.SparseArray;
import androidx.collection.LongSparseArray;
import j$.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$InputUser;
import org.telegram.tgnet.TLRPC$PrivacyRule;
import org.telegram.tgnet.TLRPC$TL_accountDaysTTL;
import org.telegram.tgnet.TLRPC$TL_account_getAccountTTL;
import org.telegram.tgnet.TLRPC$TL_account_getGlobalPrivacySettings;
import org.telegram.tgnet.TLRPC$TL_account_getPrivacy;
import org.telegram.tgnet.TLRPC$TL_account_privacyRules;
import org.telegram.tgnet.TLRPC$TL_contact;
import org.telegram.tgnet.TLRPC$TL_contactStatus;
import org.telegram.tgnet.TLRPC$TL_contacts_addContact;
import org.telegram.tgnet.TLRPC$TL_contacts_contactsNotModified;
import org.telegram.tgnet.TLRPC$TL_contacts_deleteContacts;
import org.telegram.tgnet.TLRPC$TL_contacts_getContacts;
import org.telegram.tgnet.TLRPC$TL_contacts_getStatuses;
import org.telegram.tgnet.TLRPC$TL_contacts_importContacts;
import org.telegram.tgnet.TLRPC$TL_contacts_importedContacts;
import org.telegram.tgnet.TLRPC$TL_contacts_resetSaved;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_globalPrivacySettings;
import org.telegram.tgnet.TLRPC$TL_help_getInviteText;
import org.telegram.tgnet.TLRPC$TL_help_inviteText;
import org.telegram.tgnet.TLRPC$TL_inputPrivacyKeyAddedByPhone;
import org.telegram.tgnet.TLRPC$TL_inputPrivacyKeyChatInvite;
import org.telegram.tgnet.TLRPC$TL_inputPrivacyKeyForwards;
import org.telegram.tgnet.TLRPC$TL_inputPrivacyKeyPhoneCall;
import org.telegram.tgnet.TLRPC$TL_inputPrivacyKeyPhoneNumber;
import org.telegram.tgnet.TLRPC$TL_inputPrivacyKeyPhoneP2P;
import org.telegram.tgnet.TLRPC$TL_inputPrivacyKeyProfilePhoto;
import org.telegram.tgnet.TLRPC$TL_inputPrivacyKeyStatusTimestamp;
import org.telegram.tgnet.TLRPC$TL_popularContact;
import org.telegram.tgnet.TLRPC$TL_user;
import org.telegram.tgnet.TLRPC$TL_userStatusLastMonth;
import org.telegram.tgnet.TLRPC$TL_userStatusLastWeek;
import org.telegram.tgnet.TLRPC$TL_userStatusRecently;
import org.telegram.tgnet.TLRPC$Updates;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$UserStatus;
import org.telegram.tgnet.TLRPC$Vector;
import org.telegram.tgnet.TLRPC$contacts_Contacts;

public class ContactsController extends BaseController {
    private static volatile ContactsController[] Instance = new ContactsController[4];
    public static final int PRIVACY_RULES_TYPE_ADDED_BY_PHONE = 7;
    public static final int PRIVACY_RULES_TYPE_CALLS = 2;
    public static final int PRIVACY_RULES_TYPE_COUNT = 8;
    public static final int PRIVACY_RULES_TYPE_FORWARDS = 5;
    public static final int PRIVACY_RULES_TYPE_INVITE = 1;
    public static final int PRIVACY_RULES_TYPE_LASTSEEN = 0;
    public static final int PRIVACY_RULES_TYPE_P2P = 3;
    public static final int PRIVACY_RULES_TYPE_PHONE = 6;
    public static final int PRIVACY_RULES_TYPE_PHOTO = 4;
    private ArrayList<TLRPC$PrivacyRule> addedByPhonePrivacyRules;
    private ArrayList<TLRPC$PrivacyRule> callPrivacyRules;
    private int completedRequestsCount;
    public ArrayList<TLRPC$TL_contact> contacts = new ArrayList<>();
    public HashMap<String, Contact> contactsBook = new HashMap<>();
    private boolean contactsBookLoaded;
    public HashMap<String, Contact> contactsBookSPhones = new HashMap<>();
    public HashMap<String, TLRPC$TL_contact> contactsByPhone = new HashMap<>();
    public HashMap<String, TLRPC$TL_contact> contactsByShortPhone = new HashMap<>();
    public ConcurrentHashMap<Long, TLRPC$TL_contact> contactsDict = new ConcurrentHashMap<>(20, 1.0f, 2);
    public boolean contactsLoaded;
    private boolean contactsSyncInProgress;
    private ArrayList<Long> delayedContactsUpdate = new ArrayList<>();
    private int deleteAccountTTL;
    public boolean doneLoadingContacts;
    private ArrayList<TLRPC$PrivacyRule> forwardsPrivacyRules;
    private TLRPC$TL_globalPrivacySettings globalPrivacySettings;
    private ArrayList<TLRPC$PrivacyRule> groupPrivacyRules;
    /* access modifiers changed from: private */
    public boolean ignoreChanges;
    private String inviteLink;
    private String lastContactsVersions = "";
    private ArrayList<TLRPC$PrivacyRule> lastseenPrivacyRules;
    private final Object loadContactsSync = new Object();
    private boolean loadingContacts;
    private int loadingDeleteInfo;
    private int loadingGlobalSettings;
    private int[] loadingPrivacyInfo = new int[8];
    private boolean migratingContacts;
    /* access modifiers changed from: private */
    public final Object observerLock = new Object();
    private ArrayList<TLRPC$PrivacyRule> p2pPrivacyRules;
    public ArrayList<Contact> phoneBookContacts = new ArrayList<>();
    public ArrayList<String> phoneBookSectionsArray = new ArrayList<>();
    public HashMap<String, ArrayList<Object>> phoneBookSectionsDict = new HashMap<>();
    private ArrayList<TLRPC$PrivacyRule> phonePrivacyRules;
    private ArrayList<TLRPC$PrivacyRule> profilePhotoPrivacyRules;
    private String[] projectionNames = {"lookup", "data2", "data3", "data5"};
    private String[] projectionPhones = {"lookup", "data1", "data2", "data3", "display_name", "account_type"};
    private HashMap<String, String> sectionsToReplace = new HashMap<>();
    public ArrayList<String> sortedUsersMutualSectionsArray = new ArrayList<>();
    public ArrayList<String> sortedUsersSectionsArray = new ArrayList<>();
    private Account systemAccount;
    private boolean updatingInviteLink;
    public HashMap<String, ArrayList<TLRPC$TL_contact>> usersMutualSectionsDict = new HashMap<>();
    public HashMap<String, ArrayList<TLRPC$TL_contact>> usersSectionsDict = new HashMap<>();

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$resetImportedContacts$9(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    private class MyContentObserver extends ContentObserver {
        private Runnable checkRunnable = ContactsController$MyContentObserver$$ExternalSyntheticLambda0.INSTANCE;

        public boolean deliverSelfNotifications() {
            return false;
        }

        /* access modifiers changed from: private */
        public static /* synthetic */ void lambda$new$0() {
            for (int i = 0; i < 4; i++) {
                if (UserConfig.getInstance(i).isClientActivated()) {
                    ConnectionsManager.getInstance(i).resumeNetworkMaybe();
                    ContactsController.getInstance(i).checkContacts();
                }
            }
        }

        public MyContentObserver() {
            super((Handler) null);
        }

        public void onChange(boolean z) {
            super.onChange(z);
            synchronized (ContactsController.this.observerLock) {
                if (!ContactsController.this.ignoreChanges) {
                    Utilities.globalQueue.cancelRunnable(this.checkRunnable);
                    Utilities.globalQueue.postRunnable(this.checkRunnable, 500);
                }
            }
        }
    }

    public static class Contact {
        public int contact_id;
        public String first_name;
        public int imported;
        public boolean isGoodProvider;
        public String key;
        public String last_name;
        public boolean namesFilled;
        public ArrayList<Integer> phoneDeleted = new ArrayList<>(4);
        public ArrayList<String> phoneTypes = new ArrayList<>(4);
        public ArrayList<String> phones = new ArrayList<>(4);
        public String provider;
        public ArrayList<String> shortPhones = new ArrayList<>(4);
        public TLRPC$User user;

        public String getLetter() {
            return getLetter(this.first_name, this.last_name);
        }

        public static String getLetter(String str, String str2) {
            if (!TextUtils.isEmpty(str)) {
                return str.substring(0, 1);
            }
            return !TextUtils.isEmpty(str2) ? str2.substring(0, 1) : "#";
        }
    }

    public static ContactsController getInstance(int i) {
        ContactsController contactsController = Instance[i];
        if (contactsController == null) {
            synchronized (ContactsController.class) {
                contactsController = Instance[i];
                if (contactsController == null) {
                    ContactsController[] contactsControllerArr = Instance;
                    ContactsController contactsController2 = new ContactsController(i);
                    contactsControllerArr[i] = contactsController2;
                    contactsController = contactsController2;
                }
            }
        }
        return contactsController;
    }

    public ContactsController(int i) {
        super(i);
        if (MessagesController.getMainSettings(this.currentAccount).getBoolean("needGetStatuses", false)) {
            reloadContactsStatuses();
        }
        this.sectionsToReplace.put("À", "A");
        this.sectionsToReplace.put("Á", "A");
        this.sectionsToReplace.put("Ä", "A");
        this.sectionsToReplace.put("Ù", "U");
        this.sectionsToReplace.put("Ú", "U");
        this.sectionsToReplace.put("Ü", "U");
        this.sectionsToReplace.put("Ì", "I");
        this.sectionsToReplace.put("Í", "I");
        this.sectionsToReplace.put("Ï", "I");
        this.sectionsToReplace.put("È", "E");
        this.sectionsToReplace.put("É", "E");
        this.sectionsToReplace.put("Ê", "E");
        this.sectionsToReplace.put("Ë", "E");
        this.sectionsToReplace.put("Ò", "O");
        this.sectionsToReplace.put("Ó", "O");
        this.sectionsToReplace.put("Ö", "O");
        this.sectionsToReplace.put("Ç", "C");
        this.sectionsToReplace.put("Ñ", "N");
        this.sectionsToReplace.put("Ÿ", "Y");
        this.sectionsToReplace.put("Ý", "Y");
        this.sectionsToReplace.put("Ţ", "Y");
        if (i == 0) {
            Utilities.globalQueue.postRunnable(new ContactsController$$ExternalSyntheticLambda3(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        try {
            if (hasContactsPermission()) {
                ApplicationLoader.applicationContext.getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true, new MyContentObserver());
            }
        } catch (Throwable unused) {
        }
    }

    public void cleanup() {
        this.contactsBook.clear();
        this.contactsBookSPhones.clear();
        this.phoneBookContacts.clear();
        this.contacts.clear();
        this.contactsDict.clear();
        this.usersSectionsDict.clear();
        this.usersMutualSectionsDict.clear();
        this.sortedUsersSectionsArray.clear();
        this.sortedUsersMutualSectionsArray.clear();
        this.delayedContactsUpdate.clear();
        this.contactsByPhone.clear();
        this.contactsByShortPhone.clear();
        this.phoneBookSectionsDict.clear();
        this.phoneBookSectionsArray.clear();
        this.loadingContacts = false;
        this.contactsSyncInProgress = false;
        this.doneLoadingContacts = false;
        this.contactsLoaded = false;
        this.contactsBookLoaded = false;
        this.lastContactsVersions = "";
        this.loadingGlobalSettings = 0;
        this.loadingDeleteInfo = 0;
        this.deleteAccountTTL = 0;
        Arrays.fill(this.loadingPrivacyInfo, 0);
        this.lastseenPrivacyRules = null;
        this.groupPrivacyRules = null;
        this.callPrivacyRules = null;
        this.p2pPrivacyRules = null;
        this.profilePhotoPrivacyRules = null;
        this.forwardsPrivacyRules = null;
        this.phonePrivacyRules = null;
        Utilities.globalQueue.postRunnable(new ContactsController$$ExternalSyntheticLambda5(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$cleanup$1() {
        this.migratingContacts = false;
        this.completedRequestsCount = 0;
    }

    public void checkInviteText() {
        SharedPreferences mainSettings = MessagesController.getMainSettings(this.currentAccount);
        this.inviteLink = mainSettings.getString("invitelink", (String) null);
        int i = mainSettings.getInt("invitelinktime", 0);
        if (this.updatingInviteLink) {
            return;
        }
        if (this.inviteLink == null || Math.abs((System.currentTimeMillis() / 1000) - ((long) i)) >= 86400) {
            this.updatingInviteLink = true;
            getConnectionsManager().sendRequest(new TLRPC$TL_help_getInviteText(), new ContactsController$$ExternalSyntheticLambda54(this), 2);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$checkInviteText$3(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            TLRPC$TL_help_inviteText tLRPC$TL_help_inviteText = (TLRPC$TL_help_inviteText) tLObject;
            if (tLRPC$TL_help_inviteText.message.length() != 0) {
                AndroidUtilities.runOnUIThread(new ContactsController$$ExternalSyntheticLambda40(this, tLRPC$TL_help_inviteText));
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$checkInviteText$2(TLRPC$TL_help_inviteText tLRPC$TL_help_inviteText) {
        this.updatingInviteLink = false;
        SharedPreferences.Editor edit = MessagesController.getMainSettings(this.currentAccount).edit();
        String str = tLRPC$TL_help_inviteText.message;
        this.inviteLink = str;
        edit.putString("invitelink", str);
        edit.putInt("invitelinktime", (int) (System.currentTimeMillis() / 1000));
        edit.commit();
    }

    public String getInviteText(int i) {
        String str = this.inviteLink;
        if (str == null) {
            str = "https://telegram.org/dl";
        }
        if (i <= 1) {
            return LocaleController.formatString("InviteText2", NUM, str);
        }
        try {
            return String.format(LocaleController.getPluralString("InviteTextNum", i), new Object[]{Integer.valueOf(i), str});
        } catch (Exception unused) {
            return LocaleController.formatString("InviteText2", NUM, str);
        }
    }

    public void checkAppAccount() {
        boolean z;
        AccountManager accountManager = AccountManager.get(ApplicationLoader.applicationContext);
        try {
            Account[] accountsByType = accountManager.getAccountsByType("org.telegram.messenger");
            this.systemAccount = null;
            for (int i = 0; i < accountsByType.length; i++) {
                Account account = accountsByType[i];
                int i2 = 0;
                while (true) {
                    if (i2 >= 4) {
                        z = false;
                        break;
                    }
                    TLRPC$User currentUser = UserConfig.getInstance(i2).getCurrentUser();
                    if (currentUser != null) {
                        String str = account.name;
                        if (str.equals("" + currentUser.id)) {
                            if (i2 == this.currentAccount) {
                                this.systemAccount = account;
                            }
                            z = true;
                        }
                    }
                    i2++;
                }
                if (!z) {
                    try {
                        accountManager.removeAccount(accountsByType[i], (AccountManagerCallback) null, (Handler) null);
                    } catch (Exception unused) {
                    }
                }
            }
        } catch (Throwable unused2) {
        }
        if (getUserConfig().isClientActivated()) {
            readContacts();
            if (this.systemAccount == null) {
                try {
                    Account account2 = new Account("" + getUserConfig().getClientUserId(), "org.telegram.messenger");
                    this.systemAccount = account2;
                    accountManager.addAccountExplicitly(account2, "", (Bundle) null);
                } catch (Exception unused3) {
                }
            }
        }
    }

    public void deleteUnknownAppAccounts() {
        boolean z;
        try {
            this.systemAccount = null;
            AccountManager accountManager = AccountManager.get(ApplicationLoader.applicationContext);
            Account[] accountsByType = accountManager.getAccountsByType("org.telegram.messenger");
            for (int i = 0; i < accountsByType.length; i++) {
                Account account = accountsByType[i];
                int i2 = 0;
                while (true) {
                    if (i2 >= 4) {
                        z = false;
                        break;
                    }
                    TLRPC$User currentUser = UserConfig.getInstance(i2).getCurrentUser();
                    if (currentUser != null) {
                        String str = account.name;
                        if (str.equals("" + currentUser.id)) {
                            z = true;
                            break;
                        }
                    }
                    i2++;
                }
                if (!z) {
                    try {
                        accountManager.removeAccount(accountsByType[i], (AccountManagerCallback) null, (Handler) null);
                    } catch (Exception unused) {
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void checkContacts() {
        Utilities.globalQueue.postRunnable(new ContactsController$$ExternalSyntheticLambda10(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$checkContacts$4() {
        if (checkContactsInternal()) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("detected contacts change");
            }
            performSyncPhoneBook(getContactsCopy(this.contactsBook), true, false, true, false, true, false);
        }
    }

    public void forceImportContacts() {
        Utilities.globalQueue.postRunnable(new ContactsController$$ExternalSyntheticLambda6(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$forceImportContacts$5() {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("force import contacts");
        }
        performSyncPhoneBook(new HashMap(), true, true, true, true, false, false);
    }

    public void syncPhoneBookByAlert(HashMap<String, Contact> hashMap, boolean z, boolean z2, boolean z3) {
        Utilities.globalQueue.postRunnable(new ContactsController$$ExternalSyntheticLambda35(this, hashMap, z, z2, z3));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$syncPhoneBookByAlert$6(HashMap hashMap, boolean z, boolean z2, boolean z3) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("sync contacts by alert");
        }
        performSyncPhoneBook(hashMap, true, z, z2, false, false, z3);
    }

    public void deleteAllContacts(Runnable runnable) {
        resetImportedContacts();
        TLRPC$TL_contacts_deleteContacts tLRPC$TL_contacts_deleteContacts = new TLRPC$TL_contacts_deleteContacts();
        int size = this.contacts.size();
        for (int i = 0; i < size; i++) {
            tLRPC$TL_contacts_deleteContacts.id.add(getMessagesController().getInputUser(this.contacts.get(i).user_id));
        }
        getConnectionsManager().sendRequest(tLRPC$TL_contacts_deleteContacts, new ContactsController$$ExternalSyntheticLambda58(this, runnable));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$deleteAllContacts$8(Runnable runnable, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            this.contactsBookSPhones.clear();
            this.contactsBook.clear();
            this.completedRequestsCount = 0;
            this.migratingContacts = false;
            this.contactsSyncInProgress = false;
            this.contactsLoaded = false;
            this.loadingContacts = false;
            this.contactsBookLoaded = false;
            this.lastContactsVersions = "";
            AndroidUtilities.runOnUIThread(new ContactsController$$ExternalSyntheticLambda16(this, runnable));
            return;
        }
        AndroidUtilities.runOnUIThread(runnable);
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Can't wrap try/catch for region: R(8:0|1|2|(4:5|(2:6|(1:20)(3:8|(2:10|(3:22|12|21)(1:24))(1:23)|13))|14|3)|15|16|17|19) */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:15:0x0049 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$deleteAllContacts$7(java.lang.Runnable r15) {
        /*
            r14 = this;
            java.lang.String r0 = "org.telegram.messenger"
            java.lang.String r1 = ""
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext
            android.accounts.AccountManager r2 = android.accounts.AccountManager.get(r2)
            r3 = 0
            r4 = 0
            android.accounts.Account[] r5 = r2.getAccountsByType(r0)     // Catch:{ all -> 0x0049 }
            r14.systemAccount = r3     // Catch:{ all -> 0x0049 }
            r6 = 0
        L_0x0013:
            int r7 = r5.length     // Catch:{ all -> 0x0049 }
            if (r6 >= r7) goto L_0x0049
            r7 = r5[r6]     // Catch:{ all -> 0x0049 }
            r8 = 0
        L_0x0019:
            r9 = 4
            if (r8 >= r9) goto L_0x0046
            org.telegram.messenger.UserConfig r9 = org.telegram.messenger.UserConfig.getInstance(r8)     // Catch:{ all -> 0x0049 }
            org.telegram.tgnet.TLRPC$User r9 = r9.getCurrentUser()     // Catch:{ all -> 0x0049 }
            if (r9 == 0) goto L_0x0043
            java.lang.String r10 = r7.name     // Catch:{ all -> 0x0049 }
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ all -> 0x0049 }
            r11.<init>()     // Catch:{ all -> 0x0049 }
            r11.append(r1)     // Catch:{ all -> 0x0049 }
            long r12 = r9.id     // Catch:{ all -> 0x0049 }
            r11.append(r12)     // Catch:{ all -> 0x0049 }
            java.lang.String r9 = r11.toString()     // Catch:{ all -> 0x0049 }
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x0049 }
            if (r9 == 0) goto L_0x0043
            r2.removeAccount(r7, r3, r3)     // Catch:{ all -> 0x0049 }
            goto L_0x0046
        L_0x0043:
            int r8 = r8 + 1
            goto L_0x0019
        L_0x0046:
            int r6 = r6 + 1
            goto L_0x0013
        L_0x0049:
            android.accounts.Account r5 = new android.accounts.Account     // Catch:{ Exception -> 0x006a }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x006a }
            r6.<init>()     // Catch:{ Exception -> 0x006a }
            r6.append(r1)     // Catch:{ Exception -> 0x006a }
            org.telegram.messenger.UserConfig r7 = r14.getUserConfig()     // Catch:{ Exception -> 0x006a }
            long r7 = r7.getClientUserId()     // Catch:{ Exception -> 0x006a }
            r6.append(r7)     // Catch:{ Exception -> 0x006a }
            java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x006a }
            r5.<init>(r6, r0)     // Catch:{ Exception -> 0x006a }
            r14.systemAccount = r5     // Catch:{ Exception -> 0x006a }
            r2.addAccountExplicitly(r5, r1, r3)     // Catch:{ Exception -> 0x006a }
        L_0x006a:
            org.telegram.messenger.MessagesStorage r0 = r14.getMessagesStorage()
            java.util.HashMap r1 = new java.util.HashMap
            r1.<init>()
            r2 = 1
            r0.putCachedPhoneBook(r1, r4, r2)
            org.telegram.messenger.MessagesStorage r0 = r14.getMessagesStorage()
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            r0.putContacts(r1, r2)
            java.util.ArrayList<org.telegram.messenger.ContactsController$Contact> r0 = r14.phoneBookContacts
            r0.clear()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_contact> r0 = r14.contacts
            r0.clear()
            j$.util.concurrent.ConcurrentHashMap<java.lang.Long, org.telegram.tgnet.TLRPC$TL_contact> r0 = r14.contactsDict
            r0.clear()
            java.util.HashMap<java.lang.String, java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_contact>> r0 = r14.usersSectionsDict
            r0.clear()
            java.util.HashMap<java.lang.String, java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_contact>> r0 = r14.usersMutualSectionsDict
            r0.clear()
            java.util.ArrayList<java.lang.String> r0 = r14.sortedUsersSectionsArray
            r0.clear()
            java.util.HashMap<java.lang.String, java.util.ArrayList<java.lang.Object>> r0 = r14.phoneBookSectionsDict
            r0.clear()
            java.util.ArrayList<java.lang.String> r0 = r14.phoneBookSectionsArray
            r0.clear()
            java.util.ArrayList<java.lang.Long> r0 = r14.delayedContactsUpdate
            r0.clear()
            java.util.ArrayList<java.lang.String> r0 = r14.sortedUsersMutualSectionsArray
            r0.clear()
            java.util.HashMap<java.lang.String, org.telegram.tgnet.TLRPC$TL_contact> r0 = r14.contactsByPhone
            r0.clear()
            java.util.HashMap<java.lang.String, org.telegram.tgnet.TLRPC$TL_contact> r0 = r14.contactsByShortPhone
            r0.clear()
            org.telegram.messenger.NotificationCenter r0 = r14.getNotificationCenter()
            int r1 = org.telegram.messenger.NotificationCenter.contactsDidLoad
            java.lang.Object[] r2 = new java.lang.Object[r4]
            r0.postNotificationName(r1, r2)
            r0 = 0
            r14.loadContacts(r4, r0)
            r15.run()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ContactsController.lambda$deleteAllContacts$7(java.lang.Runnable):void");
    }

    public void resetImportedContacts() {
        getConnectionsManager().sendRequest(new TLRPC$TL_contacts_resetSaved(), ContactsController$$ExternalSyntheticLambda62.INSTANCE);
    }

    /* JADX WARNING: Missing exception handler attribute for start block: B:23:0x0053 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean checkContactsInternal() {
        /*
            r9 = this;
            java.lang.String r0 = "version"
            r1 = 0
            boolean r2 = r9.hasContactsPermission()     // Catch:{ Exception -> 0x005f }
            if (r2 != 0) goto L_0x000a
            return r1
        L_0x000a:
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x005f }
            android.content.ContentResolver r3 = r2.getContentResolver()     // Catch:{ Exception -> 0x005f }
            android.net.Uri r4 = android.provider.ContactsContract.RawContacts.CONTENT_URI     // Catch:{ Exception -> 0x005a }
            r2 = 1
            java.lang.String[] r5 = new java.lang.String[r2]     // Catch:{ Exception -> 0x005a }
            r5[r1] = r0     // Catch:{ Exception -> 0x005a }
            r6 = 0
            r7 = 0
            r8 = 0
            android.database.Cursor r3 = r3.query(r4, r5, r6, r7, r8)     // Catch:{ Exception -> 0x005a }
            if (r3 == 0) goto L_0x0054
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x004f }
            r4.<init>()     // Catch:{ all -> 0x004f }
        L_0x0025:
            boolean r5 = r3.moveToNext()     // Catch:{ all -> 0x004f }
            if (r5 == 0) goto L_0x0037
            int r5 = r3.getColumnIndex(r0)     // Catch:{ all -> 0x004f }
            java.lang.String r5 = r3.getString(r5)     // Catch:{ all -> 0x004f }
            r4.append(r5)     // Catch:{ all -> 0x004f }
            goto L_0x0025
        L_0x0037:
            java.lang.String r0 = r4.toString()     // Catch:{ all -> 0x004f }
            java.lang.String r4 = r9.lastContactsVersions     // Catch:{ all -> 0x004f }
            int r4 = r4.length()     // Catch:{ all -> 0x004f }
            if (r4 == 0) goto L_0x004c
            java.lang.String r4 = r9.lastContactsVersions     // Catch:{ all -> 0x004f }
            boolean r4 = r4.equals(r0)     // Catch:{ all -> 0x004f }
            if (r4 != 0) goto L_0x004c
            r1 = 1
        L_0x004c:
            r9.lastContactsVersions = r0     // Catch:{ all -> 0x004f }
            goto L_0x0054
        L_0x004f:
            r0 = move-exception
            r3.close()     // Catch:{ all -> 0x0053 }
        L_0x0053:
            throw r0     // Catch:{ Exception -> 0x005a }
        L_0x0054:
            if (r3 == 0) goto L_0x0063
            r3.close()     // Catch:{ Exception -> 0x005a }
            goto L_0x0063
        L_0x005a:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x005f }
            goto L_0x0063
        L_0x005f:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0063:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ContactsController.checkContactsInternal():boolean");
    }

    public void readContacts() {
        synchronized (this.loadContactsSync) {
            if (!this.loadingContacts) {
                this.loadingContacts = true;
                Utilities.stageQueue.postRunnable(new ContactsController$$ExternalSyntheticLambda1(this));
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$readContacts$10() {
        if (!this.contacts.isEmpty() || this.contactsLoaded) {
            synchronized (this.loadContactsSync) {
                this.loadingContacts = false;
            }
            return;
        }
        loadContacts(true, 0);
    }

    private boolean isNotValidNameString(String str) {
        if (TextUtils.isEmpty(str)) {
            return true;
        }
        int length = str.length();
        int i = 0;
        for (int i2 = 0; i2 < length; i2++) {
            char charAt = str.charAt(i2);
            if (charAt >= '0' && charAt <= '9') {
                i++;
            }
        }
        if (i > 3) {
            return true;
        }
        return false;
    }

    /* JADX INFO: finally extract failed */
    /* JADX WARNING: Removed duplicated region for block: B:195:0x032d A[Catch:{ all -> 0x0345 }] */
    /* JADX WARNING: Removed duplicated region for block: B:197:0x0332 A[SYNTHETIC, Splitter:B:197:0x0332] */
    /* JADX WARNING: Removed duplicated region for block: B:203:0x033f  */
    /* JADX WARNING: Removed duplicated region for block: B:224:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.util.HashMap<java.lang.String, org.telegram.messenger.ContactsController.Contact> readContactsFromPhoneBook() {
        /*
            r20 = this;
            r1 = r20
            org.telegram.messenger.UserConfig r0 = r20.getUserConfig()
            boolean r0 = r0.syncContacts
            if (r0 != 0) goto L_0x0019
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0013
            java.lang.String r0 = "contacts sync disabled"
            org.telegram.messenger.FileLog.d(r0)
        L_0x0013:
            java.util.HashMap r0 = new java.util.HashMap
            r0.<init>()
            return r0
        L_0x0019:
            boolean r0 = r20.hasContactsPermission()
            if (r0 != 0) goto L_0x002e
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0028
            java.lang.String r0 = "app has no contacts permissions"
            org.telegram.messenger.FileLog.d(r0)
        L_0x0028:
            java.util.HashMap r0 = new java.util.HashMap
            r0.<init>()
            return r0
        L_0x002e:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x0324 }
            r0.<init>()     // Catch:{ all -> 0x0324 }
            android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0324 }
            android.content.ContentResolver r3 = r3.getContentResolver()     // Catch:{ all -> 0x0324 }
            java.util.HashMap r10 = new java.util.HashMap     // Catch:{ all -> 0x0324 }
            r10.<init>()     // Catch:{ all -> 0x0324 }
            java.util.ArrayList r11 = new java.util.ArrayList     // Catch:{ all -> 0x0324 }
            r11.<init>()     // Catch:{ all -> 0x0324 }
            android.net.Uri r5 = android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI     // Catch:{ all -> 0x0324 }
            java.lang.String[] r6 = r1.projectionPhones     // Catch:{ all -> 0x0324 }
            r7 = 0
            r8 = 0
            r9 = 0
            r4 = r3
            android.database.Cursor r4 = r4.query(r5, r6, r7, r8, r9)     // Catch:{ all -> 0x0324 }
            r14 = 0
            java.lang.String r15 = ""
            r9 = 1
            if (r4 == 0) goto L_0x01fa
            int r5 = r4.getCount()     // Catch:{ all -> 0x01f4 }
            if (r5 <= 0) goto L_0x01e3
            java.util.HashMap r6 = new java.util.HashMap     // Catch:{ all -> 0x01f4 }
            r6.<init>(r5)     // Catch:{ all -> 0x01f4 }
            r5 = 1
        L_0x0061:
            boolean r7 = r4.moveToNext()     // Catch:{ all -> 0x01eb }
            if (r7 == 0) goto L_0x01df
            java.lang.String r7 = r4.getString(r9)     // Catch:{ all -> 0x01eb }
            r8 = 5
            java.lang.String r8 = r4.getString(r8)     // Catch:{ all -> 0x01eb }
            if (r8 != 0) goto L_0x0073
            r8 = r15
        L_0x0073:
            java.lang.String r2 = ".sim"
            int r2 = r8.indexOf(r2)     // Catch:{ all -> 0x01eb }
            if (r2 == 0) goto L_0x007d
            r2 = 1
            goto L_0x007e
        L_0x007d:
            r2 = 0
        L_0x007e:
            boolean r16 = android.text.TextUtils.isEmpty(r7)     // Catch:{ all -> 0x01eb }
            if (r16 == 0) goto L_0x0085
            goto L_0x00df
        L_0x0085:
            java.lang.String r7 = org.telegram.PhoneFormat.PhoneFormat.stripExceptNumbers(r7, r9)     // Catch:{ all -> 0x01eb }
            boolean r16 = android.text.TextUtils.isEmpty(r7)     // Catch:{ all -> 0x01eb }
            if (r16 == 0) goto L_0x0090
            goto L_0x00df
        L_0x0090:
            java.lang.String r12 = "+"
            boolean r12 = r7.startsWith(r12)     // Catch:{ all -> 0x01eb }
            if (r12 == 0) goto L_0x00a1
            java.lang.String r12 = r7.substring(r9)     // Catch:{ all -> 0x009d }
            goto L_0x00a2
        L_0x009d:
            r0 = move-exception
            r10 = r1
            goto L_0x01ee
        L_0x00a1:
            r12 = r7
        L_0x00a2:
            java.lang.String r9 = r4.getString(r14)     // Catch:{ all -> 0x01eb }
            r0.setLength(r14)     // Catch:{ all -> 0x01eb }
            android.database.DatabaseUtils.appendEscapedSQLString(r0, r9)     // Catch:{ all -> 0x01eb }
            java.lang.String r13 = r0.toString()     // Catch:{ all -> 0x01eb }
            java.lang.Object r17 = r10.get(r12)     // Catch:{ all -> 0x01eb }
            r14 = r17
            org.telegram.messenger.ContactsController$Contact r14 = (org.telegram.messenger.ContactsController.Contact) r14     // Catch:{ all -> 0x01eb }
            if (r14 == 0) goto L_0x00e3
            boolean r7 = r14.isGoodProvider     // Catch:{ all -> 0x009d }
            if (r7 != 0) goto L_0x00df
            java.lang.String r7 = r14.provider     // Catch:{ all -> 0x009d }
            boolean r7 = r8.equals(r7)     // Catch:{ all -> 0x009d }
            if (r7 != 0) goto L_0x00df
            r7 = 0
            r0.setLength(r7)     // Catch:{ all -> 0x009d }
            java.lang.String r7 = r14.key     // Catch:{ all -> 0x009d }
            android.database.DatabaseUtils.appendEscapedSQLString(r0, r7)     // Catch:{ all -> 0x009d }
            java.lang.String r7 = r0.toString()     // Catch:{ all -> 0x009d }
            r11.remove(r7)     // Catch:{ all -> 0x009d }
            r11.add(r13)     // Catch:{ all -> 0x009d }
            r14.key = r9     // Catch:{ all -> 0x009d }
            r14.isGoodProvider = r2     // Catch:{ all -> 0x009d }
            r14.provider = r8     // Catch:{ all -> 0x009d }
        L_0x00df:
            r9 = 1
            r14 = 0
            goto L_0x0061
        L_0x00e3:
            boolean r14 = r11.contains(r13)     // Catch:{ all -> 0x01eb }
            if (r14 != 0) goto L_0x00ec
            r11.add(r13)     // Catch:{ all -> 0x009d }
        L_0x00ec:
            r13 = 2
            int r14 = r4.getInt(r13)     // Catch:{ all -> 0x01eb }
            java.lang.Object r13 = r6.get(r9)     // Catch:{ all -> 0x01eb }
            org.telegram.messenger.ContactsController$Contact r13 = (org.telegram.messenger.ContactsController.Contact) r13     // Catch:{ all -> 0x01eb }
            if (r13 != 0) goto L_0x0150
            org.telegram.messenger.ContactsController$Contact r13 = new org.telegram.messenger.ContactsController$Contact     // Catch:{ all -> 0x01eb }
            r13.<init>()     // Catch:{ all -> 0x01eb }
            r17 = r0
            r0 = 4
            java.lang.String r0 = r4.getString(r0)     // Catch:{ all -> 0x01eb }
            if (r0 != 0) goto L_0x0109
            r0 = r15
            goto L_0x010d
        L_0x0109:
            java.lang.String r0 = r0.trim()     // Catch:{ all -> 0x01eb }
        L_0x010d:
            boolean r18 = r1.isNotValidNameString(r0)     // Catch:{ all -> 0x01eb }
            if (r18 == 0) goto L_0x011a
            r13.first_name = r0     // Catch:{ all -> 0x009d }
            r13.last_name = r15     // Catch:{ all -> 0x009d }
            r18 = r3
            goto L_0x0141
        L_0x011a:
            r18 = r3
            r3 = 32
            int r3 = r0.lastIndexOf(r3)     // Catch:{ all -> 0x01eb }
            r1 = -1
            if (r3 == r1) goto L_0x013d
            r1 = 0
            java.lang.String r19 = r0.substring(r1, r3)     // Catch:{ all -> 0x01eb }
            java.lang.String r1 = r19.trim()     // Catch:{ all -> 0x01eb }
            r13.first_name = r1     // Catch:{ all -> 0x01eb }
            int r3 = r3 + 1
            java.lang.String r0 = r0.substring(r3)     // Catch:{ all -> 0x01eb }
            java.lang.String r0 = r0.trim()     // Catch:{ all -> 0x01eb }
            r13.last_name = r0     // Catch:{ all -> 0x01eb }
            goto L_0x0141
        L_0x013d:
            r13.first_name = r0     // Catch:{ all -> 0x01eb }
            r13.last_name = r15     // Catch:{ all -> 0x01eb }
        L_0x0141:
            r13.provider = r8     // Catch:{ all -> 0x01eb }
            r13.isGoodProvider = r2     // Catch:{ all -> 0x01eb }
            r13.key = r9     // Catch:{ all -> 0x01eb }
            int r0 = r5 + 1
            r13.contact_id = r5     // Catch:{ all -> 0x01eb }
            r6.put(r9, r13)     // Catch:{ all -> 0x01eb }
            r5 = r0
            goto L_0x0154
        L_0x0150:
            r17 = r0
            r18 = r3
        L_0x0154:
            java.util.ArrayList<java.lang.String> r0 = r13.shortPhones     // Catch:{ all -> 0x01eb }
            r0.add(r12)     // Catch:{ all -> 0x01eb }
            java.util.ArrayList<java.lang.String> r0 = r13.phones     // Catch:{ all -> 0x01eb }
            r0.add(r7)     // Catch:{ all -> 0x01eb }
            java.util.ArrayList<java.lang.Integer> r0 = r13.phoneDeleted     // Catch:{ all -> 0x01eb }
            r1 = 0
            java.lang.Integer r2 = java.lang.Integer.valueOf(r1)     // Catch:{ all -> 0x01eb }
            r0.add(r2)     // Catch:{ all -> 0x01eb }
            r0 = 2131627481(0x7f0e0dd9, float:1.8882228E38)
            java.lang.String r1 = "PhoneMobile"
            if (r14 != 0) goto L_0x0182
            r2 = 3
            java.lang.String r3 = r4.getString(r2)     // Catch:{ all -> 0x01eb }
            java.util.ArrayList<java.lang.String> r2 = r13.phoneTypes     // Catch:{ all -> 0x01eb }
            if (r3 == 0) goto L_0x0179
            goto L_0x017d
        L_0x0179:
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r1, r0)     // Catch:{ all -> 0x01eb }
        L_0x017d:
            r2.add(r3)     // Catch:{ all -> 0x01eb }
            r2 = 1
            goto L_0x01d4
        L_0x0182:
            r2 = 1
            if (r14 != r2) goto L_0x0194
            java.util.ArrayList<java.lang.String> r0 = r13.phoneTypes     // Catch:{ all -> 0x01eb }
            java.lang.String r1 = "PhoneHome"
            r3 = 2131627479(0x7f0e0dd7, float:1.8882224E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r3)     // Catch:{ all -> 0x01eb }
            r0.add(r1)     // Catch:{ all -> 0x01eb }
            goto L_0x01d4
        L_0x0194:
            r3 = 2
            if (r14 != r3) goto L_0x01a1
            java.util.ArrayList<java.lang.String> r3 = r13.phoneTypes     // Catch:{ all -> 0x01eb }
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)     // Catch:{ all -> 0x01eb }
            r3.add(r0)     // Catch:{ all -> 0x01eb }
            goto L_0x01d4
        L_0x01a1:
            r0 = 3
            if (r14 != r0) goto L_0x01b3
            java.util.ArrayList<java.lang.String> r0 = r13.phoneTypes     // Catch:{ all -> 0x01eb }
            java.lang.String r1 = "PhoneWork"
            r3 = 2131627491(0x7f0e0de3, float:1.8882248E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r3)     // Catch:{ all -> 0x01eb }
            r0.add(r1)     // Catch:{ all -> 0x01eb }
            goto L_0x01d4
        L_0x01b3:
            r0 = 12
            if (r14 != r0) goto L_0x01c6
            java.util.ArrayList<java.lang.String> r0 = r13.phoneTypes     // Catch:{ all -> 0x01eb }
            java.lang.String r1 = "PhoneMain"
            r3 = 2131627480(0x7f0e0dd8, float:1.8882226E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r3)     // Catch:{ all -> 0x01eb }
            r0.add(r1)     // Catch:{ all -> 0x01eb }
            goto L_0x01d4
        L_0x01c6:
            java.util.ArrayList<java.lang.String> r0 = r13.phoneTypes     // Catch:{ all -> 0x01eb }
            java.lang.String r1 = "PhoneOther"
            r3 = 2131627490(0x7f0e0de2, float:1.8882246E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r3)     // Catch:{ all -> 0x01eb }
            r0.add(r1)     // Catch:{ all -> 0x01eb }
        L_0x01d4:
            r10.put(r12, r13)     // Catch:{ all -> 0x01eb }
            r1 = r20
            r0 = r17
            r3 = r18
            goto L_0x00df
        L_0x01df:
            r18 = r3
            r2 = 1
            goto L_0x01e7
        L_0x01e3:
            r18 = r3
            r2 = 1
            r6 = 0
        L_0x01e7:
            r4.close()     // Catch:{ Exception -> 0x01f1 }
            goto L_0x01f1
        L_0x01eb:
            r0 = move-exception
            r10 = r20
        L_0x01ee:
            r2 = r6
            goto L_0x0328
        L_0x01f1:
            r3 = r6
            r1 = 0
            goto L_0x01ff
        L_0x01f4:
            r0 = move-exception
            r2 = 0
            r10 = r20
            goto L_0x0328
        L_0x01fa:
            r18 = r3
            r2 = 1
            r1 = r4
            r3 = 0
        L_0x01ff:
            java.lang.String r0 = ","
            java.lang.String r0 = android.text.TextUtils.join(r0, r11)     // Catch:{ all -> 0x031e }
            android.net.Uri r5 = android.provider.ContactsContract.Data.CONTENT_URI     // Catch:{ all -> 0x031e }
            r10 = r20
            java.lang.String[] r6 = r10.projectionNames     // Catch:{ all -> 0x031c }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x031c }
            r4.<init>()     // Catch:{ all -> 0x031c }
            java.lang.String r7 = "lookup IN ("
            r4.append(r7)     // Catch:{ all -> 0x031c }
            r4.append(r0)     // Catch:{ all -> 0x031c }
            java.lang.String r0 = ") AND "
            r4.append(r0)     // Catch:{ all -> 0x031c }
            java.lang.String r0 = "mimetype"
            r4.append(r0)     // Catch:{ all -> 0x031c }
            java.lang.String r0 = " = '"
            r4.append(r0)     // Catch:{ all -> 0x031c }
            java.lang.String r0 = "vnd.android.cursor.item/name"
            r4.append(r0)     // Catch:{ all -> 0x031c }
            java.lang.String r0 = "'"
            r4.append(r0)     // Catch:{ all -> 0x031c }
            java.lang.String r7 = r4.toString()     // Catch:{ all -> 0x031c }
            r8 = 0
            r9 = 0
            r4 = r18
            r0 = 1
            android.database.Cursor r1 = r4.query(r5, r6, r7, r8, r9)     // Catch:{ all -> 0x031c }
            if (r1 == 0) goto L_0x030f
        L_0x0240:
            boolean r2 = r1.moveToNext()     // Catch:{ all -> 0x031c }
            if (r2 == 0) goto L_0x030a
            r2 = 0
            java.lang.String r4 = r1.getString(r2)     // Catch:{ all -> 0x031c }
            java.lang.String r5 = r1.getString(r0)     // Catch:{ all -> 0x031c }
            r6 = 2
            java.lang.String r7 = r1.getString(r6)     // Catch:{ all -> 0x031c }
            r8 = 3
            java.lang.String r9 = r1.getString(r8)     // Catch:{ all -> 0x031c }
            if (r3 == 0) goto L_0x0262
            java.lang.Object r4 = r3.get(r4)     // Catch:{ all -> 0x031c }
            org.telegram.messenger.ContactsController$Contact r4 = (org.telegram.messenger.ContactsController.Contact) r4     // Catch:{ all -> 0x031c }
            goto L_0x0263
        L_0x0262:
            r4 = 0
        L_0x0263:
            if (r4 == 0) goto L_0x0240
            boolean r11 = r4.namesFilled     // Catch:{ all -> 0x031c }
            if (r11 != 0) goto L_0x0240
            boolean r11 = r4.isGoodProvider     // Catch:{ all -> 0x031c }
            java.lang.String r12 = " "
            if (r11 == 0) goto L_0x02a5
            if (r5 == 0) goto L_0x0274
            r4.first_name = r5     // Catch:{ all -> 0x031c }
            goto L_0x0276
        L_0x0274:
            r4.first_name = r15     // Catch:{ all -> 0x031c }
        L_0x0276:
            if (r7 == 0) goto L_0x027b
            r4.last_name = r7     // Catch:{ all -> 0x031c }
            goto L_0x027d
        L_0x027b:
            r4.last_name = r15     // Catch:{ all -> 0x031c }
        L_0x027d:
            boolean r5 = android.text.TextUtils.isEmpty(r9)     // Catch:{ all -> 0x031c }
            if (r5 != 0) goto L_0x0306
            java.lang.String r5 = r4.first_name     // Catch:{ all -> 0x031c }
            boolean r5 = android.text.TextUtils.isEmpty(r5)     // Catch:{ all -> 0x031c }
            if (r5 != 0) goto L_0x02a2
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x031c }
            r5.<init>()     // Catch:{ all -> 0x031c }
            java.lang.String r7 = r4.first_name     // Catch:{ all -> 0x031c }
            r5.append(r7)     // Catch:{ all -> 0x031c }
            r5.append(r12)     // Catch:{ all -> 0x031c }
            r5.append(r9)     // Catch:{ all -> 0x031c }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x031c }
            r4.first_name = r5     // Catch:{ all -> 0x031c }
            goto L_0x0306
        L_0x02a2:
            r4.first_name = r9     // Catch:{ all -> 0x031c }
            goto L_0x0306
        L_0x02a5:
            boolean r11 = r10.isNotValidNameString(r5)     // Catch:{ all -> 0x031c }
            if (r11 != 0) goto L_0x02bb
            java.lang.String r11 = r4.first_name     // Catch:{ all -> 0x031c }
            boolean r11 = r11.contains(r5)     // Catch:{ all -> 0x031c }
            if (r11 != 0) goto L_0x02d1
            java.lang.String r11 = r4.first_name     // Catch:{ all -> 0x031c }
            boolean r11 = r5.contains(r11)     // Catch:{ all -> 0x031c }
            if (r11 != 0) goto L_0x02d1
        L_0x02bb:
            boolean r11 = r10.isNotValidNameString(r7)     // Catch:{ all -> 0x031c }
            if (r11 != 0) goto L_0x0306
            java.lang.String r11 = r4.last_name     // Catch:{ all -> 0x031c }
            boolean r11 = r11.contains(r7)     // Catch:{ all -> 0x031c }
            if (r11 != 0) goto L_0x02d1
            java.lang.String r11 = r4.last_name     // Catch:{ all -> 0x031c }
            boolean r11 = r5.contains(r11)     // Catch:{ all -> 0x031c }
            if (r11 == 0) goto L_0x0306
        L_0x02d1:
            if (r5 == 0) goto L_0x02d6
            r4.first_name = r5     // Catch:{ all -> 0x031c }
            goto L_0x02d8
        L_0x02d6:
            r4.first_name = r15     // Catch:{ all -> 0x031c }
        L_0x02d8:
            boolean r5 = android.text.TextUtils.isEmpty(r9)     // Catch:{ all -> 0x031c }
            if (r5 != 0) goto L_0x02ff
            java.lang.String r5 = r4.first_name     // Catch:{ all -> 0x031c }
            boolean r5 = android.text.TextUtils.isEmpty(r5)     // Catch:{ all -> 0x031c }
            if (r5 != 0) goto L_0x02fd
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x031c }
            r5.<init>()     // Catch:{ all -> 0x031c }
            java.lang.String r11 = r4.first_name     // Catch:{ all -> 0x031c }
            r5.append(r11)     // Catch:{ all -> 0x031c }
            r5.append(r12)     // Catch:{ all -> 0x031c }
            r5.append(r9)     // Catch:{ all -> 0x031c }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x031c }
            r4.first_name = r5     // Catch:{ all -> 0x031c }
            goto L_0x02ff
        L_0x02fd:
            r4.first_name = r9     // Catch:{ all -> 0x031c }
        L_0x02ff:
            if (r7 == 0) goto L_0x0304
            r4.last_name = r7     // Catch:{ all -> 0x031c }
            goto L_0x0306
        L_0x0304:
            r4.last_name = r15     // Catch:{ all -> 0x031c }
        L_0x0306:
            r4.namesFilled = r0     // Catch:{ all -> 0x031c }
            goto L_0x0240
        L_0x030a:
            r1.close()     // Catch:{ Exception -> 0x030d }
        L_0x030d:
            r2 = 0
            goto L_0x0310
        L_0x030f:
            r2 = r1
        L_0x0310:
            if (r2 == 0) goto L_0x033c
            r2.close()     // Catch:{ Exception -> 0x0316 }
            goto L_0x033c
        L_0x0316:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
            goto L_0x033c
        L_0x031c:
            r0 = move-exception
            goto L_0x0321
        L_0x031e:
            r0 = move-exception
            r10 = r20
        L_0x0321:
            r4 = r1
            r2 = r3
            goto L_0x0328
        L_0x0324:
            r0 = move-exception
            r10 = r1
            r2 = 0
            r4 = 0
        L_0x0328:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x0345 }
            if (r2 == 0) goto L_0x0330
            r2.clear()     // Catch:{ all -> 0x0345 }
        L_0x0330:
            if (r4 == 0) goto L_0x033b
            r4.close()     // Catch:{ Exception -> 0x0336 }
            goto L_0x033b
        L_0x0336:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x033b:
            r3 = r2
        L_0x033c:
            if (r3 == 0) goto L_0x033f
            goto L_0x0344
        L_0x033f:
            java.util.HashMap r3 = new java.util.HashMap
            r3.<init>()
        L_0x0344:
            return r3
        L_0x0345:
            r0 = move-exception
            r1 = r0
            if (r4 == 0) goto L_0x0352
            r4.close()     // Catch:{ Exception -> 0x034d }
            goto L_0x0352
        L_0x034d:
            r0 = move-exception
            r2 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
        L_0x0352:
            goto L_0x0354
        L_0x0353:
            throw r1
        L_0x0354:
            goto L_0x0353
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ContactsController.readContactsFromPhoneBook():java.util.HashMap");
    }

    public HashMap<String, Contact> getContactsCopy(HashMap<String, Contact> hashMap) {
        HashMap<String, Contact> hashMap2 = new HashMap<>();
        for (Map.Entry<String, Contact> value : hashMap.entrySet()) {
            Contact contact = new Contact();
            Contact contact2 = (Contact) value.getValue();
            contact.phoneDeleted.addAll(contact2.phoneDeleted);
            contact.phones.addAll(contact2.phones);
            contact.phoneTypes.addAll(contact2.phoneTypes);
            contact.shortPhones.addAll(contact2.shortPhones);
            contact.first_name = contact2.first_name;
            contact.last_name = contact2.last_name;
            contact.contact_id = contact2.contact_id;
            String str = contact2.key;
            contact.key = str;
            hashMap2.put(str, contact);
        }
        return hashMap2;
    }

    /* access modifiers changed from: protected */
    public void migratePhoneBookToV7(SparseArray<Contact> sparseArray) {
        Utilities.globalQueue.postRunnable(new ContactsController$$ExternalSyntheticLambda14(this, sparseArray));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$migratePhoneBookToV7$11(SparseArray sparseArray) {
        if (!this.migratingContacts) {
            this.migratingContacts = true;
            HashMap hashMap = new HashMap();
            HashMap<String, Contact> readContactsFromPhoneBook = readContactsFromPhoneBook();
            HashMap hashMap2 = new HashMap();
            Iterator<Map.Entry<String, Contact>> it = readContactsFromPhoneBook.entrySet().iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                Contact contact = (Contact) it.next().getValue();
                for (int i = 0; i < contact.shortPhones.size(); i++) {
                    hashMap2.put(contact.shortPhones.get(i), contact.key);
                }
            }
            for (int i2 = 0; i2 < sparseArray.size(); i2++) {
                Contact contact2 = (Contact) sparseArray.valueAt(i2);
                int i3 = 0;
                while (true) {
                    if (i3 >= contact2.shortPhones.size()) {
                        break;
                    }
                    String str = (String) hashMap2.get(contact2.shortPhones.get(i3));
                    if (str != null) {
                        contact2.key = str;
                        hashMap.put(str, contact2);
                        break;
                    }
                    i3++;
                }
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("migrated contacts " + hashMap.size() + " of " + sparseArray.size());
            }
            getMessagesStorage().putCachedPhoneBook(hashMap, true, false);
        }
    }

    /* access modifiers changed from: protected */
    public void performSyncPhoneBook(HashMap<String, Contact> hashMap, boolean z, boolean z2, boolean z3, boolean z4, boolean z5, boolean z6) {
        if (z2 || this.contactsBookLoaded) {
            Utilities.globalQueue.postRunnable(new ContactsController$$ExternalSyntheticLambda36(this, hashMap, z3, z, z2, z4, z5, z6));
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:208:0x04c3, code lost:
        if ((r14.contactsByPhone.size() - r0) > ((r14.contactsByPhone.size() / 3) * 2)) goto L_0x04c7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x0134, code lost:
        if (r2.first_name.equals(r4.first_name) != false) goto L_0x0139;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:47:0x0149, code lost:
        if (r2.last_name.equals(r4.last_name) == false) goto L_0x014b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x014b, code lost:
        r0 = true;
     */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x0273  */
    /* JADX WARNING: Removed duplicated region for block: B:212:0x04cb  */
    /* JADX WARNING: Removed duplicated region for block: B:214:0x04f7  */
    /* JADX WARNING: Removed duplicated region for block: B:216:0x0509  */
    /* JADX WARNING: Removed duplicated region for block: B:80:0x01f0  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$performSyncPhoneBook$24(java.util.HashMap r29, boolean r30, boolean r31, boolean r32, boolean r33, boolean r34, boolean r35) {
        /*
            r28 = this;
            r13 = r28
            r3 = r29
            java.util.HashMap r0 = new java.util.HashMap
            r0.<init>()
            java.util.Set r1 = r29.entrySet()
            java.util.Iterator r1 = r1.iterator()
        L_0x0011:
            boolean r2 = r1.hasNext()
            r8 = 0
            if (r2 == 0) goto L_0x003a
            java.lang.Object r2 = r1.next()
            java.util.Map$Entry r2 = (java.util.Map.Entry) r2
            java.lang.Object r2 = r2.getValue()
            org.telegram.messenger.ContactsController$Contact r2 = (org.telegram.messenger.ContactsController.Contact) r2
        L_0x0024:
            java.util.ArrayList<java.lang.String> r4 = r2.shortPhones
            int r4 = r4.size()
            if (r8 >= r4) goto L_0x0011
            java.util.ArrayList<java.lang.String> r4 = r2.shortPhones
            java.lang.Object r4 = r4.get(r8)
            java.lang.String r4 = (java.lang.String) r4
            r0.put(r4, r2)
            int r8 = r8 + 1
            goto L_0x0024
        L_0x003a:
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r1 == 0) goto L_0x0043
            java.lang.String r1 = "start read contacts from phone"
            org.telegram.messenger.FileLog.d(r1)
        L_0x0043:
            if (r30 != 0) goto L_0x0048
            r28.checkContactsInternal()
        L_0x0048:
            java.util.HashMap r14 = r28.readContactsFromPhoneBook()
            java.util.HashMap r15 = new java.util.HashMap
            r15.<init>()
            java.util.HashMap r12 = new java.util.HashMap
            r12.<init>()
            java.util.ArrayList r11 = new java.util.ArrayList
            r11.<init>()
            java.util.Set r1 = r14.entrySet()
            java.util.Iterator r1 = r1.iterator()
        L_0x0063:
            boolean r2 = r1.hasNext()
            if (r2 == 0) goto L_0x00b5
            java.lang.Object r2 = r1.next()
            java.util.Map$Entry r2 = (java.util.Map.Entry) r2
            java.lang.Object r2 = r2.getValue()
            org.telegram.messenger.ContactsController$Contact r2 = (org.telegram.messenger.ContactsController.Contact) r2
            java.util.ArrayList<java.lang.String> r4 = r2.shortPhones
            int r4 = r4.size()
            r5 = 0
        L_0x007c:
            if (r5 >= r4) goto L_0x009a
            java.util.ArrayList<java.lang.String> r6 = r2.shortPhones
            java.lang.Object r6 = r6.get(r5)
            java.lang.String r6 = (java.lang.String) r6
            int r7 = r6.length()
            int r7 = r7 + -7
            int r7 = java.lang.Math.max(r8, r7)
            java.lang.String r6 = r6.substring(r7)
            r12.put(r6, r2)
            int r5 = r5 + 1
            goto L_0x007c
        L_0x009a:
            java.lang.String r4 = r2.getLetter()
            java.lang.Object r5 = r15.get(r4)
            java.util.ArrayList r5 = (java.util.ArrayList) r5
            if (r5 != 0) goto L_0x00b1
            java.util.ArrayList r5 = new java.util.ArrayList
            r5.<init>()
            r15.put(r4, r5)
            r11.add(r4)
        L_0x00b1:
            r5.add(r2)
            goto L_0x0063
        L_0x00b5:
            java.util.HashMap r10 = new java.util.HashMap
            r10.<init>()
            int r1 = r29.size()
            java.util.ArrayList r9 = new java.util.ArrayList
            r9.<init>()
            boolean r2 = r29.isEmpty()
            java.lang.String r5 = ""
            if (r2 != 0) goto L_0x03a7
            java.util.Set r2 = r14.entrySet()
            java.util.Iterator r2 = r2.iterator()
            r7 = 0
            r16 = 0
        L_0x00d6:
            boolean r17 = r2.hasNext()
            if (r17 == 0) goto L_0x035a
            java.lang.Object r17 = r2.next()
            java.util.Map$Entry r17 = (java.util.Map.Entry) r17
            java.lang.Object r18 = r17.getKey()
            r6 = r18
            java.lang.String r6 = (java.lang.String) r6
            java.lang.Object r17 = r17.getValue()
            r4 = r17
            org.telegram.messenger.ContactsController$Contact r4 = (org.telegram.messenger.ContactsController.Contact) r4
            java.lang.Object r17 = r3.get(r6)
            org.telegram.messenger.ContactsController$Contact r17 = (org.telegram.messenger.ContactsController.Contact) r17
            r33 = r2
            if (r17 != 0) goto L_0x0118
        L_0x00fc:
            java.util.ArrayList<java.lang.String> r2 = r4.shortPhones
            int r2 = r2.size()
            if (r8 >= r2) goto L_0x0118
            java.util.ArrayList<java.lang.String> r2 = r4.shortPhones
            java.lang.Object r2 = r2.get(r8)
            java.lang.Object r2 = r0.get(r2)
            org.telegram.messenger.ContactsController$Contact r2 = (org.telegram.messenger.ContactsController.Contact) r2
            if (r2 == 0) goto L_0x0115
            java.lang.String r6 = r2.key
            goto L_0x011a
        L_0x0115:
            int r8 = r8 + 1
            goto L_0x00fc
        L_0x0118:
            r2 = r17
        L_0x011a:
            if (r2 == 0) goto L_0x0120
            int r8 = r2.imported
            r4.imported = r8
        L_0x0120:
            if (r2 == 0) goto L_0x014d
            java.lang.String r8 = r4.first_name
            boolean r8 = android.text.TextUtils.isEmpty(r8)
            if (r8 != 0) goto L_0x0137
            java.lang.String r8 = r2.first_name
            r17 = r0
            java.lang.String r0 = r4.first_name
            boolean r0 = r8.equals(r0)
            if (r0 == 0) goto L_0x014b
            goto L_0x0139
        L_0x0137:
            r17 = r0
        L_0x0139:
            java.lang.String r0 = r4.last_name
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x014f
            java.lang.String r0 = r2.last_name
            java.lang.String r8 = r4.last_name
            boolean r0 = r0.equals(r8)
            if (r0 != 0) goto L_0x014f
        L_0x014b:
            r0 = 1
            goto L_0x0150
        L_0x014d:
            r17 = r0
        L_0x014f:
            r0 = 0
        L_0x0150:
            if (r2 == 0) goto L_0x02b9
            if (r0 == 0) goto L_0x0156
            goto L_0x02b9
        L_0x0156:
            r0 = 0
        L_0x0157:
            java.util.ArrayList<java.lang.String> r8 = r4.phones
            int r8 = r8.size()
            if (r0 >= r8) goto L_0x02a2
            java.util.ArrayList<java.lang.String> r8 = r4.shortPhones
            java.lang.Object r8 = r8.get(r0)
            java.lang.String r8 = (java.lang.String) r8
            int r22 = r8.length()
            r23 = r5
            int r5 = r22 + -7
            r22 = r12
            r12 = 0
            int r5 = java.lang.Math.max(r12, r5)
            java.lang.String r5 = r8.substring(r5)
            r10.put(r8, r4)
            java.util.ArrayList<java.lang.String> r12 = r2.shortPhones
            int r12 = r12.indexOf(r8)
            if (r31 == 0) goto L_0x01e2
            r24 = r12
            java.util.HashMap<java.lang.String, org.telegram.tgnet.TLRPC$TL_contact> r12 = r13.contactsByPhone
            java.lang.Object r12 = r12.get(r8)
            org.telegram.tgnet.TLRPC$TL_contact r12 = (org.telegram.tgnet.TLRPC$TL_contact) r12
            if (r12 == 0) goto L_0x01d1
            r25 = r11
            org.telegram.messenger.MessagesController r11 = r28.getMessagesController()
            r26 = r14
            r27 = r15
            long r14 = r12.user_id
            java.lang.Long r12 = java.lang.Long.valueOf(r14)
            org.telegram.tgnet.TLRPC$User r11 = r11.getUser(r12)
            if (r11 == 0) goto L_0x01cd
            int r16 = r16 + 1
            java.lang.String r12 = r11.first_name
            boolean r12 = android.text.TextUtils.isEmpty(r12)
            if (r12 == 0) goto L_0x01cd
            java.lang.String r11 = r11.last_name
            boolean r11 = android.text.TextUtils.isEmpty(r11)
            if (r11 == 0) goto L_0x01cd
            java.lang.String r11 = r4.first_name
            boolean r11 = android.text.TextUtils.isEmpty(r11)
            if (r11 == 0) goto L_0x01c9
            java.lang.String r11 = r4.last_name
            boolean r11 = android.text.TextUtils.isEmpty(r11)
            if (r11 != 0) goto L_0x01cd
        L_0x01c9:
            r11 = 1
            r24 = -1
            goto L_0x01ce
        L_0x01cd:
            r11 = 0
        L_0x01ce:
            r12 = r24
            goto L_0x01ed
        L_0x01d1:
            r25 = r11
            r26 = r14
            r27 = r15
            java.util.HashMap<java.lang.String, org.telegram.tgnet.TLRPC$TL_contact> r11 = r13.contactsByShortPhone
            boolean r11 = r11.containsKey(r5)
            if (r11 == 0) goto L_0x01ea
            int r16 = r16 + 1
            goto L_0x01ea
        L_0x01e2:
            r25 = r11
            r24 = r12
            r26 = r14
            r27 = r15
        L_0x01ea:
            r12 = r24
            r11 = 0
        L_0x01ed:
            r14 = -1
            if (r12 != r14) goto L_0x0273
            if (r31 == 0) goto L_0x0294
            if (r11 != 0) goto L_0x024c
            java.util.HashMap<java.lang.String, org.telegram.tgnet.TLRPC$TL_contact> r11 = r13.contactsByPhone
            java.lang.Object r8 = r11.get(r8)
            org.telegram.tgnet.TLRPC$TL_contact r8 = (org.telegram.tgnet.TLRPC$TL_contact) r8
            if (r8 == 0) goto L_0x0242
            org.telegram.messenger.MessagesController r5 = r28.getMessagesController()
            long r11 = r8.user_id
            java.lang.Long r8 = java.lang.Long.valueOf(r11)
            org.telegram.tgnet.TLRPC$User r5 = r5.getUser(r8)
            if (r5 == 0) goto L_0x023f
            int r16 = r16 + 1
            java.lang.String r8 = r5.first_name
            if (r8 == 0) goto L_0x0215
            goto L_0x0217
        L_0x0215:
            r8 = r23
        L_0x0217:
            java.lang.String r5 = r5.last_name
            if (r5 == 0) goto L_0x021c
            goto L_0x021e
        L_0x021c:
            r5 = r23
        L_0x021e:
            java.lang.String r11 = r4.first_name
            boolean r8 = r8.equals(r11)
            if (r8 == 0) goto L_0x022e
            java.lang.String r8 = r4.last_name
            boolean r5 = r5.equals(r8)
            if (r5 != 0) goto L_0x0294
        L_0x022e:
            java.lang.String r5 = r4.first_name
            boolean r5 = android.text.TextUtils.isEmpty(r5)
            if (r5 == 0) goto L_0x024c
            java.lang.String r5 = r4.last_name
            boolean r5 = android.text.TextUtils.isEmpty(r5)
            if (r5 == 0) goto L_0x024c
            goto L_0x0294
        L_0x023f:
            int r7 = r7 + 1
            goto L_0x024c
        L_0x0242:
            java.util.HashMap<java.lang.String, org.telegram.tgnet.TLRPC$TL_contact> r8 = r13.contactsByShortPhone
            boolean r5 = r8.containsKey(r5)
            if (r5 == 0) goto L_0x024c
            int r16 = r16 + 1
        L_0x024c:
            org.telegram.tgnet.TLRPC$TL_inputPhoneContact r5 = new org.telegram.tgnet.TLRPC$TL_inputPhoneContact
            r5.<init>()
            int r8 = r4.contact_id
            long r11 = (long) r8
            r5.client_id = r11
            long r14 = (long) r0
            r8 = 32
            long r14 = r14 << r8
            long r11 = r11 | r14
            r5.client_id = r11
            java.lang.String r8 = r4.first_name
            r5.first_name = r8
            java.lang.String r8 = r4.last_name
            r5.last_name = r8
            java.util.ArrayList<java.lang.String> r8 = r4.phones
            java.lang.Object r8 = r8.get(r0)
            java.lang.String r8 = (java.lang.String) r8
            r5.phone = r8
            r9.add(r5)
            goto L_0x0294
        L_0x0273:
            java.util.ArrayList<java.lang.Integer> r5 = r4.phoneDeleted
            java.util.ArrayList<java.lang.Integer> r8 = r2.phoneDeleted
            java.lang.Object r8 = r8.get(r12)
            java.lang.Integer r8 = (java.lang.Integer) r8
            r5.set(r0, r8)
            java.util.ArrayList<java.lang.String> r5 = r2.phones
            r5.remove(r12)
            java.util.ArrayList<java.lang.String> r5 = r2.shortPhones
            r5.remove(r12)
            java.util.ArrayList<java.lang.Integer> r5 = r2.phoneDeleted
            r5.remove(r12)
            java.util.ArrayList<java.lang.String> r5 = r2.phoneTypes
            r5.remove(r12)
        L_0x0294:
            int r0 = r0 + 1
            r12 = r22
            r5 = r23
            r11 = r25
            r14 = r26
            r15 = r27
            goto L_0x0157
        L_0x02a2:
            r23 = r5
            r25 = r11
            r22 = r12
            r26 = r14
            r27 = r15
            java.util.ArrayList<java.lang.String> r0 = r2.phones
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x0347
            r3.remove(r6)
            goto L_0x0347
        L_0x02b9:
            r23 = r5
            r25 = r11
            r22 = r12
            r26 = r14
            r27 = r15
            r5 = 0
        L_0x02c4:
            java.util.ArrayList<java.lang.String> r8 = r4.phones
            int r8 = r8.size()
            if (r5 >= r8) goto L_0x0342
            java.util.ArrayList<java.lang.String> r8 = r4.shortPhones
            java.lang.Object r8 = r8.get(r5)
            java.lang.String r8 = (java.lang.String) r8
            int r11 = r8.length()
            int r11 = r11 + -7
            r12 = 0
            int r11 = java.lang.Math.max(r12, r11)
            r8.substring(r11)
            r10.put(r8, r4)
            if (r2 == 0) goto L_0x0305
            java.util.ArrayList<java.lang.String> r11 = r2.shortPhones
            int r11 = r11.indexOf(r8)
            r12 = -1
            if (r11 == r12) goto L_0x0306
            java.util.ArrayList<java.lang.Integer> r14 = r2.phoneDeleted
            java.lang.Object r11 = r14.get(r11)
            java.lang.Integer r11 = (java.lang.Integer) r11
            java.util.ArrayList<java.lang.Integer> r14 = r4.phoneDeleted
            r14.set(r5, r11)
            int r11 = r11.intValue()
            r14 = 1
            if (r11 != r14) goto L_0x0306
            goto L_0x033d
        L_0x0305:
            r12 = -1
        L_0x0306:
            if (r31 == 0) goto L_0x033d
            if (r0 != 0) goto L_0x0317
            java.util.HashMap<java.lang.String, org.telegram.tgnet.TLRPC$TL_contact> r11 = r13.contactsByPhone
            boolean r8 = r11.containsKey(r8)
            if (r8 == 0) goto L_0x0315
            int r16 = r16 + 1
            goto L_0x033d
        L_0x0315:
            int r7 = r7 + 1
        L_0x0317:
            org.telegram.tgnet.TLRPC$TL_inputPhoneContact r8 = new org.telegram.tgnet.TLRPC$TL_inputPhoneContact
            r8.<init>()
            int r11 = r4.contact_id
            long r14 = (long) r11
            r8.client_id = r14
            long r12 = (long) r5
            r11 = 32
            long r12 = r12 << r11
            long r12 = r12 | r14
            r8.client_id = r12
            java.lang.String r11 = r4.first_name
            r8.first_name = r11
            java.lang.String r11 = r4.last_name
            r8.last_name = r11
            java.util.ArrayList<java.lang.String> r11 = r4.phones
            java.lang.Object r11 = r11.get(r5)
            java.lang.String r11 = (java.lang.String) r11
            r8.phone = r11
            r9.add(r8)
        L_0x033d:
            int r5 = r5 + 1
            r13 = r28
            goto L_0x02c4
        L_0x0342:
            if (r2 == 0) goto L_0x0347
            r3.remove(r6)
        L_0x0347:
            r13 = r28
            r2 = r33
            r0 = r17
            r12 = r22
            r5 = r23
            r11 = r25
            r14 = r26
            r15 = r27
            r8 = 0
            goto L_0x00d6
        L_0x035a:
            r25 = r11
            r22 = r12
            r26 = r14
            r27 = r15
            if (r32 != 0) goto L_0x0380
            boolean r0 = r29.isEmpty()
            if (r0 == 0) goto L_0x0380
            boolean r0 = r9.isEmpty()
            if (r0 == 0) goto L_0x0380
            int r0 = r26.size()
            if (r1 != r0) goto L_0x0380
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x037f
            java.lang.String r0 = "contacts not changed!"
            org.telegram.messenger.FileLog.d(r0)
        L_0x037f:
            return
        L_0x0380:
            if (r31 == 0) goto L_0x039f
            boolean r0 = r29.isEmpty()
            if (r0 != 0) goto L_0x039f
            boolean r0 = r26.isEmpty()
            if (r0 != 0) goto L_0x039f
            boolean r0 = r9.isEmpty()
            if (r0 == 0) goto L_0x039f
            org.telegram.messenger.MessagesStorage r0 = r28.getMessagesStorage()
            r13 = r26
            r2 = 0
            r0.putCachedPhoneBook(r13, r2, r2)
            goto L_0x03a1
        L_0x039f:
            r13 = r26
        L_0x03a1:
            r14 = r28
            r0 = r16
            goto L_0x0489
        L_0x03a7:
            r23 = r5
            r25 = r11
            r22 = r12
            r13 = r14
            r27 = r15
            if (r31 == 0) goto L_0x0485
            java.util.Set r0 = r13.entrySet()
            java.util.Iterator r0 = r0.iterator()
            r16 = 0
        L_0x03bc:
            boolean r2 = r0.hasNext()
            if (r2 == 0) goto L_0x0480
            java.lang.Object r2 = r0.next()
            java.util.Map$Entry r2 = (java.util.Map.Entry) r2
            java.lang.Object r4 = r2.getValue()
            org.telegram.messenger.ContactsController$Contact r4 = (org.telegram.messenger.ContactsController.Contact) r4
            java.lang.Object r2 = r2.getKey()
            java.lang.String r2 = (java.lang.String) r2
            r2 = 0
        L_0x03d5:
            java.util.ArrayList<java.lang.String> r5 = r4.phones
            int r5 = r5.size()
            if (r2 >= r5) goto L_0x047c
            if (r33 != 0) goto L_0x0450
            java.util.ArrayList<java.lang.String> r5 = r4.shortPhones
            java.lang.Object r5 = r5.get(r2)
            java.lang.String r5 = (java.lang.String) r5
            int r6 = r5.length()
            int r6 = r6 + -7
            r7 = 0
            int r6 = java.lang.Math.max(r7, r6)
            java.lang.String r6 = r5.substring(r6)
            r14 = r28
            java.util.HashMap<java.lang.String, org.telegram.tgnet.TLRPC$TL_contact> r7 = r14.contactsByPhone
            java.lang.Object r5 = r7.get(r5)
            org.telegram.tgnet.TLRPC$TL_contact r5 = (org.telegram.tgnet.TLRPC$TL_contact) r5
            if (r5 == 0) goto L_0x0445
            org.telegram.messenger.MessagesController r6 = r28.getMessagesController()
            long r7 = r5.user_id
            java.lang.Long r5 = java.lang.Long.valueOf(r7)
            org.telegram.tgnet.TLRPC$User r5 = r6.getUser(r5)
            if (r5 == 0) goto L_0x0452
            int r16 = r16 + 1
            java.lang.String r6 = r5.first_name
            if (r6 == 0) goto L_0x0419
            goto L_0x041b
        L_0x0419:
            r6 = r23
        L_0x041b:
            java.lang.String r5 = r5.last_name
            if (r5 == 0) goto L_0x0420
            goto L_0x0422
        L_0x0420:
            r5 = r23
        L_0x0422:
            java.lang.String r7 = r4.first_name
            boolean r6 = r6.equals(r7)
            if (r6 == 0) goto L_0x0432
            java.lang.String r6 = r4.last_name
            boolean r5 = r5.equals(r6)
            if (r5 != 0) goto L_0x0442
        L_0x0432:
            java.lang.String r5 = r4.first_name
            boolean r5 = android.text.TextUtils.isEmpty(r5)
            if (r5 == 0) goto L_0x0452
            java.lang.String r5 = r4.last_name
            boolean r5 = android.text.TextUtils.isEmpty(r5)
            if (r5 == 0) goto L_0x0452
        L_0x0442:
            r8 = 32
            goto L_0x0478
        L_0x0445:
            java.util.HashMap<java.lang.String, org.telegram.tgnet.TLRPC$TL_contact> r5 = r14.contactsByShortPhone
            boolean r5 = r5.containsKey(r6)
            if (r5 == 0) goto L_0x0452
            int r16 = r16 + 1
            goto L_0x0452
        L_0x0450:
            r14 = r28
        L_0x0452:
            org.telegram.tgnet.TLRPC$TL_inputPhoneContact r5 = new org.telegram.tgnet.TLRPC$TL_inputPhoneContact
            r5.<init>()
            int r6 = r4.contact_id
            long r6 = (long) r6
            r5.client_id = r6
            long r11 = (long) r2
            r8 = 32
            long r11 = r11 << r8
            long r6 = r6 | r11
            r5.client_id = r6
            java.lang.String r6 = r4.first_name
            r5.first_name = r6
            java.lang.String r6 = r4.last_name
            r5.last_name = r6
            java.util.ArrayList<java.lang.String> r6 = r4.phones
            java.lang.Object r6 = r6.get(r2)
            java.lang.String r6 = (java.lang.String) r6
            r5.phone = r6
            r9.add(r5)
        L_0x0478:
            int r2 = r2 + 1
            goto L_0x03d5
        L_0x047c:
            r14 = r28
            goto L_0x03bc
        L_0x0480:
            r14 = r28
            r0 = r16
            goto L_0x0488
        L_0x0485:
            r14 = r28
            r0 = 0
        L_0x0488:
            r7 = 0
        L_0x0489:
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r2 == 0) goto L_0x0492
            java.lang.String r2 = "done processing contacts"
            org.telegram.messenger.FileLog.d(r2)
        L_0x0492:
            if (r31 == 0) goto L_0x05ec
            boolean r2 = r9.isEmpty()
            if (r2 != 0) goto L_0x05cf
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r2 == 0) goto L_0x04a3
            java.lang.String r2 = "start import contacts"
            org.telegram.messenger.FileLog.e((java.lang.String) r2)
        L_0x04a3:
            r2 = 2
            if (r34 == 0) goto L_0x04c6
            if (r7 == 0) goto L_0x04c6
            r4 = 30
            if (r7 < r4) goto L_0x04ae
            r2 = 1
            goto L_0x04c7
        L_0x04ae:
            if (r32 == 0) goto L_0x04c6
            if (r1 != 0) goto L_0x04c6
            java.util.HashMap<java.lang.String, org.telegram.tgnet.TLRPC$TL_contact> r1 = r14.contactsByPhone
            int r1 = r1.size()
            int r1 = r1 - r0
            java.util.HashMap<java.lang.String, org.telegram.tgnet.TLRPC$TL_contact> r4 = r14.contactsByPhone
            int r4 = r4.size()
            int r4 = r4 / 3
            int r4 = r4 * 2
            if (r1 <= r4) goto L_0x04c6
            goto L_0x04c7
        L_0x04c6:
            r2 = 0
        L_0x04c7:
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r1 == 0) goto L_0x04f5
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r4 = "new phone book contacts "
            r1.append(r4)
            r1.append(r7)
            java.lang.String r4 = " serverContactsInPhonebook "
            r1.append(r4)
            r1.append(r0)
            java.lang.String r0 = " totalContacts "
            r1.append(r0)
            java.util.HashMap<java.lang.String, org.telegram.tgnet.TLRPC$TL_contact> r0 = r14.contactsByPhone
            int r0 = r0.size()
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            org.telegram.messenger.FileLog.d(r0)
        L_0x04f5:
            if (r2 == 0) goto L_0x0509
            org.telegram.messenger.ContactsController$$ExternalSyntheticLambda12 r6 = new org.telegram.messenger.ContactsController$$ExternalSyntheticLambda12
            r0 = r6
            r1 = r28
            r3 = r29
            r4 = r32
            r5 = r30
            r0.<init>(r1, r2, r3, r4, r5)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r6)
            return
        L_0x0509:
            if (r35 == 0) goto L_0x0523
            org.telegram.messenger.DispatchQueue r8 = org.telegram.messenger.Utilities.stageQueue
            org.telegram.messenger.ContactsController$$ExternalSyntheticLambda32 r9 = new org.telegram.messenger.ContactsController$$ExternalSyntheticLambda32
            r0 = r9
            r1 = r28
            r2 = r10
            r3 = r13
            r4 = r32
            r5 = r27
            r6 = r25
            r7 = r22
            r0.<init>(r1, r2, r3, r4, r5, r6, r7)
            r8.postRunnable(r9)
            return
        L_0x0523:
            r0 = 1
            boolean[] r15 = new boolean[r0]
            r0 = 0
            r15[r0] = r0
            java.util.HashMap r12 = new java.util.HashMap
            r12.<init>(r13)
            android.util.SparseArray r11 = new android.util.SparseArray
            r11.<init>()
            java.util.Set r0 = r12.entrySet()
            java.util.Iterator r0 = r0.iterator()
        L_0x053b:
            boolean r1 = r0.hasNext()
            if (r1 == 0) goto L_0x0555
            java.lang.Object r1 = r0.next()
            java.util.Map$Entry r1 = (java.util.Map.Entry) r1
            java.lang.Object r1 = r1.getValue()
            org.telegram.messenger.ContactsController$Contact r1 = (org.telegram.messenger.ContactsController.Contact) r1
            int r2 = r1.contact_id
            java.lang.String r1 = r1.key
            r11.put(r2, r1)
            goto L_0x053b
        L_0x0555:
            r1 = 0
            r14.completedRequestsCount = r1
            int r0 = r9.size()
            double r0 = (double) r0
            r2 = 4647503709213818880(0x407fNUM, double:500.0)
            java.lang.Double.isNaN(r0)
            double r0 = r0 / r2
            double r0 = java.lang.Math.ceil(r0)
            int r8 = (int) r0
            r7 = 0
        L_0x056c:
            if (r7 >= r8) goto L_0x0616
            org.telegram.tgnet.TLRPC$TL_contacts_importContacts r6 = new org.telegram.tgnet.TLRPC$TL_contacts_importContacts
            r6.<init>()
            int r0 = r7 * 500
            int r1 = r0 + 500
            int r2 = r9.size()
            int r1 = java.lang.Math.min(r1, r2)
            java.util.ArrayList r2 = new java.util.ArrayList
            java.util.List r0 = r9.subList(r0, r1)
            r2.<init>(r0)
            r6.contacts = r2
            org.telegram.tgnet.ConnectionsManager r5 = r28.getConnectionsManager()
            org.telegram.messenger.ContactsController$$ExternalSyntheticLambda60 r4 = new org.telegram.messenger.ContactsController$$ExternalSyntheticLambda60
            r0 = r4
            r1 = r28
            r2 = r12
            r3 = r11
            r14 = r4
            r4 = r15
            r16 = r15
            r15 = r5
            r5 = r13
            r29 = r6
            r20 = r7
            r7 = r8
            r17 = r8
            r8 = r10
            r18 = r9
            r9 = r32
            r19 = r10
            r10 = r27
            r23 = r11
            r21 = r25
            r11 = r21
            r24 = r12
            r12 = r22
            r0.<init>(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12)
            r0 = 6
            r1 = r29
            r15.sendRequest(r1, r14, r0)
            int r7 = r20 + 1
            r14 = r28
            r15 = r16
            r8 = r17
            r9 = r18
            r10 = r19
            r11 = r23
            r12 = r24
            goto L_0x056c
        L_0x05cf:
            r19 = r10
            r21 = r25
            org.telegram.messenger.DispatchQueue r8 = org.telegram.messenger.Utilities.stageQueue
            org.telegram.messenger.ContactsController$$ExternalSyntheticLambda31 r9 = new org.telegram.messenger.ContactsController$$ExternalSyntheticLambda31
            r0 = r9
            r1 = r28
            r2 = r19
            r3 = r13
            r4 = r32
            r5 = r27
            r6 = r21
            r7 = r22
            r0.<init>(r1, r2, r3, r4, r5, r6, r7)
            r8.postRunnable(r9)
            goto L_0x0616
        L_0x05ec:
            r19 = r10
            r21 = r25
            org.telegram.messenger.DispatchQueue r8 = org.telegram.messenger.Utilities.stageQueue
            org.telegram.messenger.ContactsController$$ExternalSyntheticLambda33 r9 = new org.telegram.messenger.ContactsController$$ExternalSyntheticLambda33
            r0 = r9
            r1 = r28
            r2 = r19
            r3 = r13
            r4 = r32
            r5 = r27
            r6 = r21
            r7 = r22
            r0.<init>(r1, r2, r3, r4, r5, r6, r7)
            r8.postRunnable(r9)
            boolean r0 = r13.isEmpty()
            if (r0 != 0) goto L_0x0616
            org.telegram.messenger.MessagesStorage r0 = r28.getMessagesStorage()
            r1 = 0
            r0.putCachedPhoneBook(r13, r1, r1)
        L_0x0616:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ContactsController.lambda$performSyncPhoneBook$24(java.util.HashMap, boolean, boolean, boolean, boolean, boolean, boolean):void");
    }

    private /* synthetic */ void lambda$performSyncPhoneBook$12(HashMap hashMap) {
        ArrayList arrayList = new ArrayList();
        if (hashMap != null && !hashMap.isEmpty()) {
            try {
                HashMap hashMap2 = new HashMap();
                for (int i = 0; i < this.contacts.size(); i++) {
                    TLRPC$User user = getMessagesController().getUser(Long.valueOf(this.contacts.get(i).user_id));
                    if (user != null) {
                        if (!TextUtils.isEmpty(user.phone)) {
                            hashMap2.put(user.phone, user);
                        }
                    }
                }
                for (Map.Entry value : hashMap.entrySet()) {
                    Contact contact = (Contact) value.getValue();
                    int i2 = 0;
                    boolean z = false;
                    while (i2 < contact.shortPhones.size()) {
                        TLRPC$User tLRPC$User = (TLRPC$User) hashMap2.get(contact.shortPhones.get(i2));
                        if (tLRPC$User != null) {
                            arrayList.add(tLRPC$User);
                            contact.shortPhones.remove(i2);
                            i2--;
                            z = true;
                        }
                        i2++;
                    }
                    if (z) {
                        int size = contact.shortPhones.size();
                    }
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
        if (!arrayList.isEmpty()) {
            deleteContact(arrayList, false);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$performSyncPhoneBook$13(int i, HashMap hashMap, boolean z, boolean z2) {
        getNotificationCenter().postNotificationName(NotificationCenter.hasNewContactsToImport, Integer.valueOf(i), hashMap, Boolean.valueOf(z), Boolean.valueOf(z2));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$performSyncPhoneBook$15(HashMap hashMap, HashMap hashMap2, boolean z, HashMap hashMap3, ArrayList arrayList, HashMap hashMap4) {
        this.contactsBookSPhones = hashMap;
        this.contactsBook = hashMap2;
        this.contactsSyncInProgress = false;
        this.contactsBookLoaded = true;
        if (z) {
            this.contactsLoaded = true;
        }
        if (!this.delayedContactsUpdate.isEmpty() && this.contactsLoaded) {
            applyContactsUpdates(this.delayedContactsUpdate, (ConcurrentHashMap<Long, TLRPC$User>) null, (ArrayList<TLRPC$TL_contact>) null, (ArrayList<Long>) null);
            this.delayedContactsUpdate.clear();
        }
        getMessagesStorage().putCachedPhoneBook(hashMap2, false, false);
        AndroidUtilities.runOnUIThread(new ContactsController$$ExternalSyntheticLambda27(this, hashMap3, arrayList, hashMap4));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$performSyncPhoneBook$14(HashMap hashMap, ArrayList arrayList, HashMap hashMap2) {
        lambda$performSyncPhoneBook$22(hashMap, arrayList, hashMap2);
        updateUnregisteredContacts();
        getNotificationCenter().postNotificationName(NotificationCenter.contactsDidLoad, new Object[0]);
        getNotificationCenter().postNotificationName(NotificationCenter.contactsImported, new Object[0]);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$performSyncPhoneBook$19(HashMap hashMap, SparseArray sparseArray, boolean[] zArr, HashMap hashMap2, TLRPC$TL_contacts_importContacts tLRPC$TL_contacts_importContacts, int i, HashMap hashMap3, boolean z, HashMap hashMap4, ArrayList arrayList, HashMap hashMap5, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        HashMap hashMap6 = hashMap;
        SparseArray sparseArray2 = sparseArray;
        TLRPC$TL_contacts_importContacts tLRPC$TL_contacts_importContacts2 = tLRPC$TL_contacts_importContacts;
        TLRPC$TL_error tLRPC$TL_error2 = tLRPC$TL_error;
        this.completedRequestsCount++;
        if (tLRPC$TL_error2 == null) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("contacts imported");
            }
            TLRPC$TL_contacts_importedContacts tLRPC$TL_contacts_importedContacts = (TLRPC$TL_contacts_importedContacts) tLObject;
            if (!tLRPC$TL_contacts_importedContacts.retry_contacts.isEmpty()) {
                for (int i2 = 0; i2 < tLRPC$TL_contacts_importedContacts.retry_contacts.size(); i2++) {
                    hashMap.remove(sparseArray.get((int) tLRPC$TL_contacts_importedContacts.retry_contacts.get(i2).longValue()));
                }
                zArr[0] = true;
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("result has retry contacts");
                }
            }
            for (int i3 = 0; i3 < tLRPC$TL_contacts_importedContacts.popular_invites.size(); i3++) {
                TLRPC$TL_popularContact tLRPC$TL_popularContact = tLRPC$TL_contacts_importedContacts.popular_invites.get(i3);
                Contact contact = (Contact) hashMap2.get(sparseArray.get((int) tLRPC$TL_popularContact.client_id));
                if (contact != null) {
                    contact.imported = tLRPC$TL_popularContact.importers;
                }
            }
            HashMap hashMap7 = hashMap2;
            getMessagesStorage().putUsersAndChats(tLRPC$TL_contacts_importedContacts.users, (ArrayList<TLRPC$Chat>) null, true, true);
            ArrayList arrayList2 = new ArrayList();
            for (int i4 = 0; i4 < tLRPC$TL_contacts_importedContacts.imported.size(); i4++) {
                TLRPC$TL_contact tLRPC$TL_contact = new TLRPC$TL_contact();
                tLRPC$TL_contact.user_id = tLRPC$TL_contacts_importedContacts.imported.get(i4).user_id;
                arrayList2.add(tLRPC$TL_contact);
            }
            processLoadedContacts(arrayList2, tLRPC$TL_contacts_importedContacts.users, 2);
        } else {
            HashMap hashMap8 = hashMap2;
            for (int i5 = 0; i5 < tLRPC$TL_contacts_importContacts2.contacts.size(); i5++) {
                hashMap.remove(sparseArray.get((int) tLRPC$TL_contacts_importContacts2.contacts.get(i5).client_id));
            }
            zArr[0] = true;
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("import contacts error " + tLRPC$TL_error2.text);
            }
        }
        if (this.completedRequestsCount == i) {
            if (!hashMap.isEmpty()) {
                getMessagesStorage().putCachedPhoneBook(hashMap, false, false);
            }
            Utilities.stageQueue.postRunnable(new ContactsController$$ExternalSyntheticLambda34(this, hashMap3, hashMap2, z, hashMap4, arrayList, hashMap5, zArr));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$performSyncPhoneBook$18(HashMap hashMap, HashMap hashMap2, boolean z, HashMap hashMap3, ArrayList arrayList, HashMap hashMap4, boolean[] zArr) {
        this.contactsBookSPhones = hashMap;
        this.contactsBook = hashMap2;
        this.contactsSyncInProgress = false;
        this.contactsBookLoaded = true;
        if (z) {
            this.contactsLoaded = true;
        }
        if (!this.delayedContactsUpdate.isEmpty() && this.contactsLoaded) {
            applyContactsUpdates(this.delayedContactsUpdate, (ConcurrentHashMap<Long, TLRPC$User>) null, (ArrayList<TLRPC$TL_contact>) null, (ArrayList<Long>) null);
            this.delayedContactsUpdate.clear();
        }
        AndroidUtilities.runOnUIThread(new ContactsController$$ExternalSyntheticLambda28(this, hashMap3, arrayList, hashMap4));
        if (zArr[0]) {
            Utilities.globalQueue.postRunnable(new ContactsController$$ExternalSyntheticLambda7(this), 300000);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$performSyncPhoneBook$16(HashMap hashMap, ArrayList arrayList, HashMap hashMap2) {
        lambda$performSyncPhoneBook$22(hashMap, arrayList, hashMap2);
        getNotificationCenter().postNotificationName(NotificationCenter.contactsImported, new Object[0]);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$performSyncPhoneBook$17() {
        getMessagesStorage().getCachedPhoneBook(true);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$performSyncPhoneBook$21(HashMap hashMap, HashMap hashMap2, boolean z, HashMap hashMap3, ArrayList arrayList, HashMap hashMap4) {
        this.contactsBookSPhones = hashMap;
        this.contactsBook = hashMap2;
        this.contactsSyncInProgress = false;
        this.contactsBookLoaded = true;
        if (z) {
            this.contactsLoaded = true;
        }
        if (!this.delayedContactsUpdate.isEmpty() && this.contactsLoaded) {
            applyContactsUpdates(this.delayedContactsUpdate, (ConcurrentHashMap<Long, TLRPC$User>) null, (ArrayList<TLRPC$TL_contact>) null, (ArrayList<Long>) null);
            this.delayedContactsUpdate.clear();
        }
        AndroidUtilities.runOnUIThread(new ContactsController$$ExternalSyntheticLambda26(this, hashMap3, arrayList, hashMap4));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$performSyncPhoneBook$20(HashMap hashMap, ArrayList arrayList, HashMap hashMap2) {
        lambda$performSyncPhoneBook$22(hashMap, arrayList, hashMap2);
        updateUnregisteredContacts();
        getNotificationCenter().postNotificationName(NotificationCenter.contactsDidLoad, new Object[0]);
        getNotificationCenter().postNotificationName(NotificationCenter.contactsImported, new Object[0]);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$performSyncPhoneBook$23(HashMap hashMap, HashMap hashMap2, boolean z, HashMap hashMap3, ArrayList arrayList, HashMap hashMap4) {
        this.contactsBookSPhones = hashMap;
        this.contactsBook = hashMap2;
        this.contactsSyncInProgress = false;
        this.contactsBookLoaded = true;
        if (z) {
            this.contactsLoaded = true;
        }
        if (!this.delayedContactsUpdate.isEmpty() && this.contactsLoaded) {
            applyContactsUpdates(this.delayedContactsUpdate, (ConcurrentHashMap<Long, TLRPC$User>) null, (ArrayList<TLRPC$TL_contact>) null, (ArrayList<Long>) null);
            this.delayedContactsUpdate.clear();
        }
        AndroidUtilities.runOnUIThread(new ContactsController$$ExternalSyntheticLambda25(this, hashMap3, arrayList, hashMap4));
    }

    public boolean isLoadingContacts() {
        boolean z;
        synchronized (this.loadContactsSync) {
            z = this.loadingContacts;
        }
        return z;
    }

    private long getContactsHash(ArrayList<TLRPC$TL_contact> arrayList) {
        ArrayList arrayList2 = new ArrayList(arrayList);
        Collections.sort(arrayList2, ContactsController$$ExternalSyntheticLambda50.INSTANCE);
        int size = arrayList2.size();
        long j = 0;
        for (int i = -1; i < size; i++) {
            if (i == -1) {
                j = MediaDataController.calcHash(j, (long) getUserConfig().contactsSavedCount);
            } else {
                j = MediaDataController.calcHash(j, ((TLRPC$TL_contact) arrayList2.get(i)).user_id);
            }
        }
        return j;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ int lambda$getContactsHash$25(TLRPC$TL_contact tLRPC$TL_contact, TLRPC$TL_contact tLRPC$TL_contact2) {
        long j = tLRPC$TL_contact.user_id;
        long j2 = tLRPC$TL_contact2.user_id;
        if (j > j2) {
            return 1;
        }
        return j < j2 ? -1 : 0;
    }

    public void loadContacts(boolean z, long j) {
        synchronized (this.loadContactsSync) {
            this.loadingContacts = true;
        }
        if (z) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("load contacts from cache");
            }
            getMessagesStorage().getContacts();
            return;
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("load contacts from server");
        }
        TLRPC$TL_contacts_getContacts tLRPC$TL_contacts_getContacts = new TLRPC$TL_contacts_getContacts();
        tLRPC$TL_contacts_getContacts.hash = j;
        getConnectionsManager().sendRequest(tLRPC$TL_contacts_getContacts, new ContactsController$$ExternalSyntheticLambda56(this, j));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadContacts$27(long j, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            TLRPC$contacts_Contacts tLRPC$contacts_Contacts = (TLRPC$contacts_Contacts) tLObject;
            if (j == 0 || !(tLRPC$contacts_Contacts instanceof TLRPC$TL_contacts_contactsNotModified)) {
                getUserConfig().contactsSavedCount = tLRPC$contacts_Contacts.saved_count;
                getUserConfig().saveConfig(false);
                processLoadedContacts(tLRPC$contacts_Contacts.contacts, tLRPC$contacts_Contacts.users, 0);
                return;
            }
            this.contactsLoaded = true;
            if (!this.delayedContactsUpdate.isEmpty() && this.contactsBookLoaded) {
                applyContactsUpdates(this.delayedContactsUpdate, (ConcurrentHashMap<Long, TLRPC$User>) null, (ArrayList<TLRPC$TL_contact>) null, (ArrayList<Long>) null);
                this.delayedContactsUpdate.clear();
            }
            getUserConfig().lastContactsSyncTime = (int) (System.currentTimeMillis() / 1000);
            getUserConfig().saveConfig(false);
            AndroidUtilities.runOnUIThread(new ContactsController$$ExternalSyntheticLambda8(this));
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("load contacts don't change");
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadContacts$26() {
        synchronized (this.loadContactsSync) {
            this.loadingContacts = false;
        }
        getNotificationCenter().postNotificationName(NotificationCenter.contactsDidLoad, new Object[0]);
    }

    public void processLoadedContacts(ArrayList<TLRPC$TL_contact> arrayList, ArrayList<TLRPC$User> arrayList2, int i) {
        AndroidUtilities.runOnUIThread(new ContactsController$$ExternalSyntheticLambda19(this, arrayList2, i, arrayList));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processLoadedContacts$37(ArrayList arrayList, int i, ArrayList arrayList2) {
        getMessagesController().putUsers(arrayList, i == 1);
        LongSparseArray longSparseArray = new LongSparseArray();
        boolean isEmpty = arrayList2.isEmpty();
        if (i == 2 && !this.contacts.isEmpty()) {
            int i2 = 0;
            while (i2 < arrayList2.size()) {
                if (this.contactsDict.get(Long.valueOf(((TLRPC$TL_contact) arrayList2.get(i2)).user_id)) != null) {
                    arrayList2.remove(i2);
                    i2--;
                }
                i2++;
            }
            arrayList2.addAll(this.contacts);
        }
        for (int i3 = 0; i3 < arrayList2.size(); i3++) {
            TLRPC$User user = getMessagesController().getUser(Long.valueOf(((TLRPC$TL_contact) arrayList2.get(i3)).user_id));
            if (user != null) {
                longSparseArray.put(user.id, user);
            }
        }
        Utilities.stageQueue.postRunnable(new ContactsController$$ExternalSyntheticLambda11(this, i, arrayList2, longSparseArray, arrayList, isEmpty));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processLoadedContacts$36(int i, ArrayList arrayList, LongSparseArray longSparseArray, ArrayList arrayList2, boolean z) {
        HashMap hashMap;
        HashMap hashMap2;
        int i2;
        String str;
        int i3 = i;
        ArrayList arrayList3 = arrayList;
        LongSparseArray longSparseArray2 = longSparseArray;
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("done loading contacts");
        }
        if (i3 == 1 && (arrayList.isEmpty() || Math.abs((System.currentTimeMillis() / 1000) - ((long) getUserConfig().lastContactsSyncTime)) >= 86400)) {
            loadContacts(false, getContactsHash(arrayList3));
            if (arrayList.isEmpty()) {
                AndroidUtilities.runOnUIThread(new ContactsController$$ExternalSyntheticLambda9(this));
                return;
            }
        }
        if (i3 == 0) {
            getUserConfig().lastContactsSyncTime = (int) (System.currentTimeMillis() / 1000);
            getUserConfig().saveConfig(false);
        }
        int i4 = 0;
        while (i4 < arrayList.size()) {
            TLRPC$TL_contact tLRPC$TL_contact = (TLRPC$TL_contact) arrayList3.get(i4);
            if (longSparseArray2.get(tLRPC$TL_contact.user_id) != null || tLRPC$TL_contact.user_id == getUserConfig().getClientUserId()) {
                i4++;
            } else {
                loadContacts(false, 0);
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("contacts are broken, load from server");
                }
                AndroidUtilities.runOnUIThread(new ContactsController$$ExternalSyntheticLambda4(this));
                return;
            }
        }
        if (i3 != 1) {
            getMessagesStorage().putUsersAndChats(arrayList2, (ArrayList<TLRPC$Chat>) null, true, true);
            getMessagesStorage().putContacts(arrayList3, i3 != 2);
        }
        Collections.sort(arrayList3, new ContactsController$$ExternalSyntheticLambda43(longSparseArray2));
        ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap(20, 1.0f, 2);
        HashMap hashMap3 = new HashMap();
        HashMap hashMap4 = new HashMap();
        ArrayList arrayList4 = new ArrayList();
        ArrayList arrayList5 = new ArrayList();
        if (!this.contactsBookLoaded) {
            HashMap hashMap5 = new HashMap();
            hashMap = new HashMap();
            hashMap2 = hashMap5;
        } else {
            hashMap2 = null;
            hashMap = null;
        }
        int i5 = 0;
        while (i5 < arrayList.size()) {
            TLRPC$TL_contact tLRPC$TL_contact2 = (TLRPC$TL_contact) arrayList3.get(i5);
            TLRPC$User tLRPC$User = (TLRPC$User) longSparseArray2.get(tLRPC$TL_contact2.user_id);
            if (tLRPC$User != null) {
                concurrentHashMap.put(Long.valueOf(tLRPC$TL_contact2.user_id), tLRPC$TL_contact2);
                if (hashMap2 == null || TextUtils.isEmpty(tLRPC$User.phone)) {
                    i2 = 0;
                } else {
                    hashMap2.put(tLRPC$User.phone, tLRPC$TL_contact2);
                    String str2 = tLRPC$User.phone;
                    i2 = 0;
                    hashMap.put(str2.substring(Math.max(0, str2.length() - 7)), tLRPC$TL_contact2);
                }
                String firstName = UserObject.getFirstName(tLRPC$User);
                if (firstName.length() > 1) {
                    firstName = firstName.substring(i2, 1);
                }
                if (firstName.length() == 0) {
                    str = "#";
                } else {
                    str = firstName.toUpperCase();
                }
                String str3 = this.sectionsToReplace.get(str);
                if (str3 != null) {
                    str = str3;
                }
                ArrayList arrayList6 = (ArrayList) hashMap3.get(str);
                if (arrayList6 == null) {
                    arrayList6 = new ArrayList();
                    hashMap3.put(str, arrayList6);
                    arrayList4.add(str);
                }
                arrayList6.add(tLRPC$TL_contact2);
                if (tLRPC$User.mutual_contact) {
                    ArrayList arrayList7 = (ArrayList) hashMap4.get(str);
                    if (arrayList7 == null) {
                        arrayList7 = new ArrayList();
                        hashMap4.put(str, arrayList7);
                        arrayList5.add(str);
                    }
                    arrayList7.add(tLRPC$TL_contact2);
                }
            }
            i5++;
            arrayList3 = arrayList;
            longSparseArray2 = longSparseArray;
        }
        Collections.sort(arrayList4, ContactsController$$ExternalSyntheticLambda47.INSTANCE);
        Collections.sort(arrayList5, ContactsController$$ExternalSyntheticLambda46.INSTANCE);
        AndroidUtilities.runOnUIThread(new ContactsController$$ExternalSyntheticLambda23(this, arrayList, concurrentHashMap, hashMap3, hashMap4, arrayList4, arrayList5, i, z));
        if (!this.delayedContactsUpdate.isEmpty() && this.contactsLoaded && this.contactsBookLoaded) {
            applyContactsUpdates(this.delayedContactsUpdate, (ConcurrentHashMap<Long, TLRPC$User>) null, (ArrayList<TLRPC$TL_contact>) null, (ArrayList<Long>) null);
            this.delayedContactsUpdate.clear();
        }
        if (hashMap2 != null) {
            AndroidUtilities.runOnUIThread(new ContactsController$$ExternalSyntheticLambda30(this, hashMap2, hashMap));
        } else {
            this.contactsLoaded = true;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processLoadedContacts$28() {
        this.doneLoadingContacts = true;
        getNotificationCenter().postNotificationName(NotificationCenter.contactsDidLoad, new Object[0]);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processLoadedContacts$29() {
        this.doneLoadingContacts = true;
        getNotificationCenter().postNotificationName(NotificationCenter.contactsDidLoad, new Object[0]);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ int lambda$processLoadedContacts$31(String str, String str2) {
        char charAt = str.charAt(0);
        char charAt2 = str2.charAt(0);
        if (charAt == '#') {
            return 1;
        }
        if (charAt2 == '#') {
            return -1;
        }
        return str.compareTo(str2);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ int lambda$processLoadedContacts$32(String str, String str2) {
        char charAt = str.charAt(0);
        char charAt2 = str2.charAt(0);
        if (charAt == '#') {
            return 1;
        }
        if (charAt2 == '#') {
            return -1;
        }
        return str.compareTo(str2);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processLoadedContacts$33(ArrayList arrayList, ConcurrentHashMap concurrentHashMap, HashMap hashMap, HashMap hashMap2, ArrayList arrayList2, ArrayList arrayList3, int i, boolean z) {
        this.contacts = arrayList;
        this.contactsDict = concurrentHashMap;
        this.usersSectionsDict = hashMap;
        this.usersMutualSectionsDict = hashMap2;
        this.sortedUsersSectionsArray = arrayList2;
        this.sortedUsersMutualSectionsArray = arrayList3;
        this.doneLoadingContacts = true;
        if (i != 2) {
            synchronized (this.loadContactsSync) {
                this.loadingContacts = false;
            }
        }
        performWriteContactsToPhoneBook();
        updateUnregisteredContacts();
        getNotificationCenter().postNotificationName(NotificationCenter.contactsDidLoad, new Object[0]);
        if (i == 1 || z) {
            reloadContactsStatusesMaybe();
        } else {
            saveContactsLoadTime();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processLoadedContacts$35(HashMap hashMap, HashMap hashMap2) {
        Utilities.globalQueue.postRunnable(new ContactsController$$ExternalSyntheticLambda29(this, hashMap, hashMap2));
        if (!this.contactsSyncInProgress) {
            this.contactsSyncInProgress = true;
            getMessagesStorage().getCachedPhoneBook(false);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processLoadedContacts$34(HashMap hashMap, HashMap hashMap2) {
        this.contactsByPhone = hashMap;
        this.contactsByShortPhone = hashMap2;
    }

    public boolean isContact(long j) {
        return this.contactsDict.get(Long.valueOf(j)) != null;
    }

    public void reloadContactsStatusesMaybe() {
        try {
            if (MessagesController.getMainSettings(this.currentAccount).getLong("lastReloadStatusTime", 0) < System.currentTimeMillis() - 10800000) {
                reloadContactsStatuses();
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    private void saveContactsLoadTime() {
        try {
            MessagesController.getMainSettings(this.currentAccount).edit().putLong("lastReloadStatusTime", System.currentTimeMillis()).commit();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: mergePhonebookAndTelegramContacts */
    public void lambda$performSyncPhoneBook$22(HashMap<String, ArrayList<Object>> hashMap, ArrayList<String> arrayList, HashMap<String, Contact> hashMap2) {
        Utilities.globalQueue.postRunnable(new ContactsController$$ExternalSyntheticLambda22(this, new ArrayList(this.contacts), hashMap2, hashMap, arrayList));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$mergePhonebookAndTelegramContacts$41(ArrayList arrayList, HashMap hashMap, HashMap hashMap2, ArrayList arrayList2) {
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            TLRPC$User user = getMessagesController().getUser(Long.valueOf(((TLRPC$TL_contact) arrayList.get(i)).user_id));
            if (user != null && !TextUtils.isEmpty(user.phone)) {
                String str = user.phone;
                Contact contact = (Contact) hashMap.get(str.substring(Math.max(0, str.length() - 7)));
                if (contact == null) {
                    String letter = Contact.getLetter(user.first_name, user.last_name);
                    ArrayList arrayList3 = (ArrayList) hashMap2.get(letter);
                    if (arrayList3 == null) {
                        arrayList3 = new ArrayList();
                        hashMap2.put(letter, arrayList3);
                        arrayList2.add(letter);
                    }
                    arrayList3.add(user);
                } else if (contact.user == null) {
                    contact.user = user;
                }
            }
        }
        for (ArrayList sort : hashMap2.values()) {
            Collections.sort(sort, ContactsController$$ExternalSyntheticLambda51.INSTANCE);
        }
        Collections.sort(arrayList2, ContactsController$$ExternalSyntheticLambda45.INSTANCE);
        AndroidUtilities.runOnUIThread(new ContactsController$$ExternalSyntheticLambda21(this, arrayList2, hashMap2));
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ int lambda$mergePhonebookAndTelegramContacts$38(Object obj, Object obj2) {
        String str;
        String str2;
        String str3 = "";
        if (obj instanceof TLRPC$User) {
            TLRPC$User tLRPC$User = (TLRPC$User) obj;
            str = formatName(tLRPC$User.first_name, tLRPC$User.last_name);
        } else if (obj instanceof Contact) {
            Contact contact = (Contact) obj;
            TLRPC$User tLRPC$User2 = contact.user;
            str = tLRPC$User2 != null ? formatName(tLRPC$User2.first_name, tLRPC$User2.last_name) : formatName(contact.first_name, contact.last_name);
        } else {
            str = str3;
        }
        if (obj2 instanceof TLRPC$User) {
            TLRPC$User tLRPC$User3 = (TLRPC$User) obj2;
            str3 = formatName(tLRPC$User3.first_name, tLRPC$User3.last_name);
        } else if (obj2 instanceof Contact) {
            Contact contact2 = (Contact) obj2;
            TLRPC$User tLRPC$User4 = contact2.user;
            if (tLRPC$User4 != null) {
                str2 = formatName(tLRPC$User4.first_name, tLRPC$User4.last_name);
            } else {
                str2 = formatName(contact2.first_name, contact2.last_name);
            }
            str3 = str2;
        }
        return str.compareTo(str3);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ int lambda$mergePhonebookAndTelegramContacts$39(String str, String str2) {
        char charAt = str.charAt(0);
        char charAt2 = str2.charAt(0);
        if (charAt == '#') {
            return 1;
        }
        if (charAt2 == '#') {
            return -1;
        }
        return str.compareTo(str2);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$mergePhonebookAndTelegramContacts$40(ArrayList arrayList, HashMap hashMap) {
        this.phoneBookSectionsArray = arrayList;
        this.phoneBookSectionsDict = hashMap;
    }

    private void updateUnregisteredContacts() {
        boolean z;
        HashMap hashMap = new HashMap();
        int size = this.contacts.size();
        for (int i = 0; i < size; i++) {
            TLRPC$TL_contact tLRPC$TL_contact = this.contacts.get(i);
            TLRPC$User user = getMessagesController().getUser(Long.valueOf(tLRPC$TL_contact.user_id));
            if (user != null && !TextUtils.isEmpty(user.phone)) {
                hashMap.put(user.phone, tLRPC$TL_contact);
            }
        }
        ArrayList<Contact> arrayList = new ArrayList<>();
        for (Map.Entry<String, Contact> value : this.contactsBook.entrySet()) {
            Contact contact = (Contact) value.getValue();
            int i2 = 0;
            while (true) {
                z = true;
                if (i2 < contact.phones.size()) {
                    if (hashMap.containsKey(contact.shortPhones.get(i2)) || contact.phoneDeleted.get(i2).intValue() == 1) {
                        break;
                    }
                    i2++;
                } else {
                    z = false;
                    break;
                }
            }
            if (!z) {
                arrayList.add(contact);
            }
        }
        Collections.sort(arrayList, ContactsController$$ExternalSyntheticLambda49.INSTANCE);
        this.phoneBookContacts = arrayList;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ int lambda$updateUnregisteredContacts$42(Contact contact, Contact contact2) {
        String str = contact.first_name;
        if (str.length() == 0) {
            str = contact.last_name;
        }
        String str2 = contact2.first_name;
        if (str2.length() == 0) {
            str2 = contact2.last_name;
        }
        return str.compareTo(str2);
    }

    private void buildContactsSectionsArrays(boolean z) {
        String str;
        if (z) {
            Collections.sort(this.contacts, new ContactsController$$ExternalSyntheticLambda44(this));
        }
        HashMap<String, ArrayList<TLRPC$TL_contact>> hashMap = new HashMap<>();
        ArrayList<String> arrayList = new ArrayList<>();
        for (int i = 0; i < this.contacts.size(); i++) {
            TLRPC$TL_contact tLRPC$TL_contact = this.contacts.get(i);
            TLRPC$User user = getMessagesController().getUser(Long.valueOf(tLRPC$TL_contact.user_id));
            if (user != null) {
                String firstName = UserObject.getFirstName(user);
                if (firstName.length() > 1) {
                    firstName = firstName.substring(0, 1);
                }
                if (firstName.length() == 0) {
                    str = "#";
                } else {
                    str = firstName.toUpperCase();
                }
                String str2 = this.sectionsToReplace.get(str);
                if (str2 != null) {
                    str = str2;
                }
                ArrayList arrayList2 = hashMap.get(str);
                if (arrayList2 == null) {
                    arrayList2 = new ArrayList();
                    hashMap.put(str, arrayList2);
                    arrayList.add(str);
                }
                arrayList2.add(tLRPC$TL_contact);
            }
        }
        Collections.sort(arrayList, ContactsController$$ExternalSyntheticLambda48.INSTANCE);
        this.usersSectionsDict = hashMap;
        this.sortedUsersSectionsArray = arrayList;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ int lambda$buildContactsSectionsArrays$43(TLRPC$TL_contact tLRPC$TL_contact, TLRPC$TL_contact tLRPC$TL_contact2) {
        return UserObject.getFirstName(getMessagesController().getUser(Long.valueOf(tLRPC$TL_contact.user_id))).compareTo(UserObject.getFirstName(getMessagesController().getUser(Long.valueOf(tLRPC$TL_contact2.user_id))));
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ int lambda$buildContactsSectionsArrays$44(String str, String str2) {
        char charAt = str.charAt(0);
        char charAt2 = str2.charAt(0);
        if (charAt == '#') {
            return 1;
        }
        if (charAt2 == '#') {
            return -1;
        }
        return str.compareTo(str2);
    }

    private boolean hasContactsPermission() {
        if (Build.VERSION.SDK_INT < 23) {
            Cursor cursor = null;
            try {
                Cursor query = ApplicationLoader.applicationContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, this.projectionPhones, (String) null, (String[]) null, (String) null);
                if (query == null || query.getCount() == 0) {
                    if (query != null) {
                        try {
                            query.close();
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                        }
                    }
                    return false;
                }
                try {
                    query.close();
                } catch (Exception e2) {
                    FileLog.e((Throwable) e2);
                }
                return true;
            } catch (Throwable th) {
                if (cursor != null) {
                    try {
                        cursor.close();
                    } catch (Exception e3) {
                        FileLog.e((Throwable) e3);
                    }
                }
                throw th;
            }
        } else if (ApplicationLoader.applicationContext.checkSelfPermission("android.permission.READ_CONTACTS") == 0) {
            return true;
        } else {
            return false;
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x00bc  */
    /* JADX WARNING: Removed duplicated region for block: B:46:? A[RETURN, SYNTHETIC] */
    /* renamed from: performWriteContactsToPhoneBookInternal */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void lambda$performWriteContactsToPhoneBook$45(java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_contact> r13) {
        /*
            r12 = this;
            java.lang.String r0 = "contacts_updated_v7"
            r1 = 0
            boolean r2 = r12.hasContactsPermission()     // Catch:{ Exception -> 0x00b0 }
            if (r2 != 0) goto L_0x000a
            return
        L_0x000a:
            int r2 = r12.currentAccount     // Catch:{ Exception -> 0x00b0 }
            android.content.SharedPreferences r2 = org.telegram.messenger.MessagesController.getMainSettings(r2)     // Catch:{ Exception -> 0x00b0 }
            r3 = 0
            boolean r4 = r2.getBoolean(r0, r3)     // Catch:{ Exception -> 0x00b0 }
            r5 = 1
            r4 = r4 ^ r5
            if (r4 == 0) goto L_0x0024
            android.content.SharedPreferences$Editor r2 = r2.edit()     // Catch:{ Exception -> 0x00b0 }
            android.content.SharedPreferences$Editor r0 = r2.putBoolean(r0, r5)     // Catch:{ Exception -> 0x00b0 }
            r0.commit()     // Catch:{ Exception -> 0x00b0 }
        L_0x0024:
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x00b0 }
            android.content.ContentResolver r6 = r0.getContentResolver()     // Catch:{ Exception -> 0x00b0 }
            android.net.Uri r0 = android.provider.ContactsContract.RawContacts.CONTENT_URI     // Catch:{ Exception -> 0x00b0 }
            android.net.Uri$Builder r0 = r0.buildUpon()     // Catch:{ Exception -> 0x00b0 }
            java.lang.String r2 = "account_name"
            android.accounts.Account r7 = r12.systemAccount     // Catch:{ Exception -> 0x00b0 }
            java.lang.String r7 = r7.name     // Catch:{ Exception -> 0x00b0 }
            android.net.Uri$Builder r0 = r0.appendQueryParameter(r2, r7)     // Catch:{ Exception -> 0x00b0 }
            java.lang.String r2 = "account_type"
            android.accounts.Account r7 = r12.systemAccount     // Catch:{ Exception -> 0x00b0 }
            java.lang.String r7 = r7.type     // Catch:{ Exception -> 0x00b0 }
            android.net.Uri$Builder r0 = r0.appendQueryParameter(r2, r7)     // Catch:{ Exception -> 0x00b0 }
            android.net.Uri r7 = r0.build()     // Catch:{ Exception -> 0x00b0 }
            r0 = 2
            java.lang.String[] r8 = new java.lang.String[r0]     // Catch:{ Exception -> 0x00b0 }
            java.lang.String r0 = "_id"
            r8[r3] = r0     // Catch:{ Exception -> 0x00b0 }
            java.lang.String r0 = "sync2"
            r8[r5] = r0     // Catch:{ Exception -> 0x00b0 }
            r9 = 0
            r10 = 0
            r11 = 0
            android.database.Cursor r0 = r6.query(r7, r8, r9, r10, r11)     // Catch:{ Exception -> 0x00b0 }
            androidx.collection.LongSparseArray r2 = new androidx.collection.LongSparseArray     // Catch:{ Exception -> 0x00ab, all -> 0x00a8 }
            r2.<init>()     // Catch:{ Exception -> 0x00ab, all -> 0x00a8 }
            if (r0 == 0) goto L_0x00a4
        L_0x0061:
            boolean r6 = r0.moveToNext()     // Catch:{ Exception -> 0x00ab, all -> 0x00a8 }
            if (r6 == 0) goto L_0x0077
            long r6 = r0.getLong(r5)     // Catch:{ Exception -> 0x00ab, all -> 0x00a8 }
            long r8 = r0.getLong(r3)     // Catch:{ Exception -> 0x00ab, all -> 0x00a8 }
            java.lang.Long r8 = java.lang.Long.valueOf(r8)     // Catch:{ Exception -> 0x00ab, all -> 0x00a8 }
            r2.put(r6, r8)     // Catch:{ Exception -> 0x00ab, all -> 0x00a8 }
            goto L_0x0061
        L_0x0077:
            r0.close()     // Catch:{ Exception -> 0x00ab, all -> 0x00a8 }
        L_0x007a:
            int r0 = r13.size()     // Catch:{ Exception -> 0x00b0 }
            if (r3 >= r0) goto L_0x00a5
            java.lang.Object r0 = r13.get(r3)     // Catch:{ Exception -> 0x00b0 }
            org.telegram.tgnet.TLRPC$TL_contact r0 = (org.telegram.tgnet.TLRPC$TL_contact) r0     // Catch:{ Exception -> 0x00b0 }
            if (r4 != 0) goto L_0x0090
            long r5 = r0.user_id     // Catch:{ Exception -> 0x00b0 }
            int r5 = r2.indexOfKey(r5)     // Catch:{ Exception -> 0x00b0 }
            if (r5 >= 0) goto L_0x00a1
        L_0x0090:
            org.telegram.messenger.MessagesController r5 = r12.getMessagesController()     // Catch:{ Exception -> 0x00b0 }
            long r6 = r0.user_id     // Catch:{ Exception -> 0x00b0 }
            java.lang.Long r0 = java.lang.Long.valueOf(r6)     // Catch:{ Exception -> 0x00b0 }
            org.telegram.tgnet.TLRPC$User r0 = r5.getUser(r0)     // Catch:{ Exception -> 0x00b0 }
            r12.addContactToPhoneBook(r0, r4)     // Catch:{ Exception -> 0x00b0 }
        L_0x00a1:
            int r3 = r3 + 1
            goto L_0x007a
        L_0x00a4:
            r1 = r0
        L_0x00a5:
            if (r1 == 0) goto L_0x00b9
            goto L_0x00b6
        L_0x00a8:
            r13 = move-exception
            r1 = r0
            goto L_0x00ba
        L_0x00ab:
            r13 = move-exception
            r1 = r0
            goto L_0x00b1
        L_0x00ae:
            r13 = move-exception
            goto L_0x00ba
        L_0x00b0:
            r13 = move-exception
        L_0x00b1:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r13)     // Catch:{ all -> 0x00ae }
            if (r1 == 0) goto L_0x00b9
        L_0x00b6:
            r1.close()
        L_0x00b9:
            return
        L_0x00ba:
            if (r1 == 0) goto L_0x00bf
            r1.close()
        L_0x00bf:
            goto L_0x00c1
        L_0x00c0:
            throw r13
        L_0x00c1:
            goto L_0x00c0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ContactsController.lambda$performWriteContactsToPhoneBook$45(java.util.ArrayList):void");
    }

    private void performWriteContactsToPhoneBook() {
        Utilities.phoneBookQueue.postRunnable(new ContactsController$$ExternalSyntheticLambda18(this, new ArrayList(this.contacts)));
    }

    private void applyContactsUpdates(ArrayList<Long> arrayList, ConcurrentHashMap<Long, TLRPC$User> concurrentHashMap, ArrayList<TLRPC$TL_contact> arrayList2, ArrayList<Long> arrayList3) {
        int indexOf;
        int indexOf2;
        if (arrayList2 == null || arrayList3 == null) {
            arrayList2 = new ArrayList<>();
            arrayList3 = new ArrayList<>();
            for (int i = 0; i < arrayList.size(); i++) {
                Long l = arrayList.get(i);
                if (l.longValue() > 0) {
                    TLRPC$TL_contact tLRPC$TL_contact = new TLRPC$TL_contact();
                    tLRPC$TL_contact.user_id = l.longValue();
                    arrayList2.add(tLRPC$TL_contact);
                } else if (l.longValue() < 0) {
                    arrayList3.add(Long.valueOf(-l.longValue()));
                }
            }
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("process update - contacts add = " + arrayList2.size() + " delete = " + arrayList3.size());
        }
        StringBuilder sb = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        int i2 = 0;
        boolean z = false;
        while (true) {
            TLRPC$User tLRPC$User = null;
            if (i2 >= arrayList2.size()) {
                break;
            }
            TLRPC$TL_contact tLRPC$TL_contact2 = arrayList2.get(i2);
            if (concurrentHashMap != null) {
                tLRPC$User = concurrentHashMap.get(Long.valueOf(tLRPC$TL_contact2.user_id));
            }
            if (tLRPC$User == null) {
                tLRPC$User = getMessagesController().getUser(Long.valueOf(tLRPC$TL_contact2.user_id));
            } else {
                getMessagesController().putUser(tLRPC$User, true);
            }
            if (tLRPC$User == null || TextUtils.isEmpty(tLRPC$User.phone)) {
                z = true;
            } else {
                Contact contact = this.contactsBookSPhones.get(tLRPC$User.phone);
                if (!(contact == null || (indexOf2 = contact.shortPhones.indexOf(tLRPC$User.phone)) == -1)) {
                    contact.phoneDeleted.set(indexOf2, 0);
                }
                if (sb.length() != 0) {
                    sb.append(",");
                }
                sb.append(tLRPC$User.phone);
            }
            i2++;
        }
        for (int i3 = 0; i3 < arrayList3.size(); i3++) {
            Long l2 = arrayList3.get(i3);
            Utilities.phoneBookQueue.postRunnable(new ContactsController$$ExternalSyntheticLambda15(this, l2));
            TLRPC$User tLRPC$User2 = concurrentHashMap != null ? concurrentHashMap.get(l2) : null;
            if (tLRPC$User2 == null) {
                tLRPC$User2 = getMessagesController().getUser(l2);
            } else {
                getMessagesController().putUser(tLRPC$User2, true);
            }
            if (tLRPC$User2 == null) {
                z = true;
            } else if (!TextUtils.isEmpty(tLRPC$User2.phone)) {
                Contact contact2 = this.contactsBookSPhones.get(tLRPC$User2.phone);
                if (!(contact2 == null || (indexOf = contact2.shortPhones.indexOf(tLRPC$User2.phone)) == -1)) {
                    contact2.phoneDeleted.set(indexOf, 1);
                }
                if (sb2.length() != 0) {
                    sb2.append(",");
                }
                sb2.append(tLRPC$User2.phone);
            }
        }
        if (!(sb.length() == 0 && sb2.length() == 0)) {
            getMessagesStorage().applyPhoneBookUpdates(sb.toString(), sb2.toString());
        }
        if (z) {
            Utilities.stageQueue.postRunnable(new ContactsController$$ExternalSyntheticLambda2(this));
        } else {
            AndroidUtilities.runOnUIThread(new ContactsController$$ExternalSyntheticLambda20(this, arrayList2, arrayList3));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$applyContactsUpdates$46(Long l) {
        deleteContactFromPhoneBook(l.longValue());
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$applyContactsUpdates$47() {
        loadContacts(false, 0);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$applyContactsUpdates$48(ArrayList arrayList, ArrayList arrayList2) {
        for (int i = 0; i < arrayList.size(); i++) {
            TLRPC$TL_contact tLRPC$TL_contact = (TLRPC$TL_contact) arrayList.get(i);
            if (this.contactsDict.get(Long.valueOf(tLRPC$TL_contact.user_id)) == null) {
                this.contacts.add(tLRPC$TL_contact);
                this.contactsDict.put(Long.valueOf(tLRPC$TL_contact.user_id), tLRPC$TL_contact);
            }
        }
        for (int i2 = 0; i2 < arrayList2.size(); i2++) {
            Long l = (Long) arrayList2.get(i2);
            TLRPC$TL_contact tLRPC$TL_contact2 = this.contactsDict.get(l);
            if (tLRPC$TL_contact2 != null) {
                this.contacts.remove(tLRPC$TL_contact2);
                this.contactsDict.remove(l);
            }
        }
        if (!arrayList.isEmpty()) {
            updateUnregisteredContacts();
            performWriteContactsToPhoneBook();
        }
        performSyncPhoneBook(getContactsCopy(this.contactsBook), false, false, false, false, true, false);
        buildContactsSectionsArrays(!arrayList.isEmpty());
        getNotificationCenter().postNotificationName(NotificationCenter.contactsDidLoad, new Object[0]);
    }

    public void processContactsUpdates(ArrayList<Long> arrayList, ConcurrentHashMap<Long, TLRPC$User> concurrentHashMap) {
        int indexOf;
        int indexOf2;
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        Iterator<Long> it = arrayList.iterator();
        while (it.hasNext()) {
            Long next = it.next();
            if (next.longValue() > 0) {
                TLRPC$TL_contact tLRPC$TL_contact = new TLRPC$TL_contact();
                tLRPC$TL_contact.user_id = next.longValue();
                arrayList2.add(tLRPC$TL_contact);
                if (!this.delayedContactsUpdate.isEmpty() && (indexOf2 = this.delayedContactsUpdate.indexOf(Long.valueOf(-next.longValue()))) != -1) {
                    this.delayedContactsUpdate.remove(indexOf2);
                }
            } else if (next.longValue() < 0) {
                arrayList3.add(Long.valueOf(-next.longValue()));
                if (!this.delayedContactsUpdate.isEmpty() && (indexOf = this.delayedContactsUpdate.indexOf(Long.valueOf(-next.longValue()))) != -1) {
                    this.delayedContactsUpdate.remove(indexOf);
                }
            }
        }
        if (!arrayList3.isEmpty()) {
            getMessagesStorage().deleteContacts(arrayList3);
        }
        if (!arrayList2.isEmpty()) {
            getMessagesStorage().putContacts(arrayList2, false);
        }
        if (!this.contactsLoaded || !this.contactsBookLoaded) {
            this.delayedContactsUpdate.addAll(arrayList);
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("delay update - contacts add = " + arrayList2.size() + " delete = " + arrayList3.size());
                return;
            }
            return;
        }
        applyContactsUpdates(arrayList, concurrentHashMap, arrayList2, arrayList3);
    }

    public long addContactToPhoneBook(TLRPC$User tLRPC$User, boolean z) {
        String str;
        long j = -1;
        if (this.systemAccount == null || tLRPC$User == null || !hasContactsPermission()) {
            return -1;
        }
        synchronized (this.observerLock) {
            this.ignoreChanges = true;
        }
        ContentResolver contentResolver = ApplicationLoader.applicationContext.getContentResolver();
        if (z) {
            try {
                contentResolver.delete(ContactsContract.RawContacts.CONTENT_URI.buildUpon().appendQueryParameter("caller_is_syncadapter", "true").appendQueryParameter("account_name", this.systemAccount.name).appendQueryParameter("account_type", this.systemAccount.type).build(), "sync2 = " + tLRPC$User.id, (String[]) null);
            } catch (Exception unused) {
            }
        }
        ArrayList arrayList = new ArrayList();
        ContentProviderOperation.Builder newInsert = ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI);
        newInsert.withValue("account_name", this.systemAccount.name);
        newInsert.withValue("account_type", this.systemAccount.type);
        newInsert.withValue("sync1", TextUtils.isEmpty(tLRPC$User.phone) ? "" : tLRPC$User.phone);
        newInsert.withValue("sync2", Long.valueOf(tLRPC$User.id));
        arrayList.add(newInsert.build());
        ContentProviderOperation.Builder newInsert2 = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI);
        newInsert2.withValueBackReference("raw_contact_id", 0);
        newInsert2.withValue("mimetype", "vnd.android.cursor.item/name");
        newInsert2.withValue("data2", tLRPC$User.first_name);
        newInsert2.withValue("data3", tLRPC$User.last_name);
        arrayList.add(newInsert2.build());
        if (TextUtils.isEmpty(tLRPC$User.phone)) {
            str = formatName(tLRPC$User.first_name, tLRPC$User.last_name);
        } else {
            str = "+" + tLRPC$User.phone;
        }
        ContentProviderOperation.Builder newInsert3 = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI);
        newInsert3.withValueBackReference("raw_contact_id", 0);
        newInsert3.withValue("mimetype", "vnd.android.cursor.item/vnd.org.telegram.messenger.android.profile");
        newInsert3.withValue("data1", Long.valueOf(tLRPC$User.id));
        newInsert3.withValue("data2", "Telegram Profile");
        newInsert3.withValue("data3", LocaleController.formatString("ContactShortcutMessage", NUM, str));
        newInsert3.withValue("data4", Long.valueOf(tLRPC$User.id));
        arrayList.add(newInsert3.build());
        ContentProviderOperation.Builder newInsert4 = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI);
        newInsert4.withValueBackReference("raw_contact_id", 0);
        newInsert4.withValue("mimetype", "vnd.android.cursor.item/vnd.org.telegram.messenger.android.call");
        newInsert4.withValue("data1", Long.valueOf(tLRPC$User.id));
        newInsert4.withValue("data2", "Telegram Voice Call");
        newInsert4.withValue("data3", LocaleController.formatString("ContactShortcutVoiceCall", NUM, str));
        newInsert4.withValue("data4", Long.valueOf(tLRPC$User.id));
        arrayList.add(newInsert4.build());
        ContentProviderOperation.Builder newInsert5 = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI);
        newInsert5.withValueBackReference("raw_contact_id", 0);
        newInsert5.withValue("mimetype", "vnd.android.cursor.item/vnd.org.telegram.messenger.android.call.video");
        newInsert5.withValue("data1", Long.valueOf(tLRPC$User.id));
        newInsert5.withValue("data2", "Telegram Video Call");
        newInsert5.withValue("data3", LocaleController.formatString("ContactShortcutVideoCall", NUM, str));
        newInsert5.withValue("data4", Long.valueOf(tLRPC$User.id));
        arrayList.add(newInsert5.build());
        try {
            ContentProviderResult[] applyBatch = contentResolver.applyBatch("com.android.contacts", arrayList);
            if (!(applyBatch == null || applyBatch.length <= 0 || applyBatch[0].uri == null)) {
                j = Long.parseLong(applyBatch[0].uri.getLastPathSegment());
            }
        } catch (Exception unused2) {
        }
        synchronized (this.observerLock) {
            this.ignoreChanges = false;
        }
        return j;
    }

    private void deleteContactFromPhoneBook(long j) {
        if (hasContactsPermission()) {
            synchronized (this.observerLock) {
                this.ignoreChanges = true;
            }
            try {
                ContentResolver contentResolver = ApplicationLoader.applicationContext.getContentResolver();
                Uri build = ContactsContract.RawContacts.CONTENT_URI.buildUpon().appendQueryParameter("caller_is_syncadapter", "true").appendQueryParameter("account_name", this.systemAccount.name).appendQueryParameter("account_type", this.systemAccount.type).build();
                contentResolver.delete(build, "sync2 = " + j, (String[]) null);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            synchronized (this.observerLock) {
                this.ignoreChanges = false;
            }
        }
    }

    /* access modifiers changed from: protected */
    public void markAsContacted(String str) {
        if (str != null) {
            Utilities.phoneBookQueue.postRunnable(new ContactsController$$ExternalSyntheticLambda0(str));
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$markAsContacted$49(String str) {
        Uri parse = Uri.parse(str);
        ContentValues contentValues = new ContentValues();
        contentValues.put("last_time_contacted", Long.valueOf(System.currentTimeMillis()));
        ApplicationLoader.applicationContext.getContentResolver().update(parse, contentValues, (String) null, (String[]) null);
    }

    public void addContact(TLRPC$User tLRPC$User, boolean z) {
        if (tLRPC$User != null) {
            TLRPC$TL_contacts_addContact tLRPC$TL_contacts_addContact = new TLRPC$TL_contacts_addContact();
            tLRPC$TL_contacts_addContact.id = getMessagesController().getInputUser(tLRPC$User);
            tLRPC$TL_contacts_addContact.first_name = tLRPC$User.first_name;
            tLRPC$TL_contacts_addContact.last_name = tLRPC$User.last_name;
            String str = tLRPC$User.phone;
            tLRPC$TL_contacts_addContact.phone = str;
            tLRPC$TL_contacts_addContact.add_phone_privacy_exception = z;
            if (str == null) {
                tLRPC$TL_contacts_addContact.phone = "";
            } else if (str.length() > 0 && !tLRPC$TL_contacts_addContact.phone.startsWith("+")) {
                tLRPC$TL_contacts_addContact.phone = "+" + tLRPC$TL_contacts_addContact.phone;
            }
            getConnectionsManager().sendRequest(tLRPC$TL_contacts_addContact, new ContactsController$$ExternalSyntheticLambda61(this, tLRPC$User), 6);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$addContact$52(TLRPC$User tLRPC$User, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        int indexOf;
        if (tLRPC$TL_error == null) {
            TLRPC$Updates tLRPC$Updates = (TLRPC$Updates) tLObject;
            getMessagesController().processUpdates(tLRPC$Updates, false);
            for (int i = 0; i < tLRPC$Updates.users.size(); i++) {
                TLRPC$User tLRPC$User2 = tLRPC$Updates.users.get(i);
                if (tLRPC$User2.id == tLRPC$User.id) {
                    Utilities.phoneBookQueue.postRunnable(new ContactsController$$ExternalSyntheticLambda42(this, tLRPC$User2));
                    TLRPC$TL_contact tLRPC$TL_contact = new TLRPC$TL_contact();
                    tLRPC$TL_contact.user_id = tLRPC$User2.id;
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(tLRPC$TL_contact);
                    getMessagesStorage().putContacts(arrayList, false);
                    if (!TextUtils.isEmpty(tLRPC$User2.phone)) {
                        formatName(tLRPC$User2.first_name, tLRPC$User2.last_name);
                        getMessagesStorage().applyPhoneBookUpdates(tLRPC$User2.phone, "");
                        Contact contact = this.contactsBookSPhones.get(tLRPC$User2.phone);
                        if (!(contact == null || (indexOf = contact.shortPhones.indexOf(tLRPC$User2.phone)) == -1)) {
                            contact.phoneDeleted.set(indexOf, 0);
                        }
                    }
                }
            }
            AndroidUtilities.runOnUIThread(new ContactsController$$ExternalSyntheticLambda41(this, tLRPC$Updates));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$addContact$50(TLRPC$User tLRPC$User) {
        addContactToPhoneBook(tLRPC$User, true);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$addContact$51(TLRPC$Updates tLRPC$Updates) {
        for (int i = 0; i < tLRPC$Updates.users.size(); i++) {
            TLRPC$User tLRPC$User = tLRPC$Updates.users.get(i);
            if (tLRPC$User.contact && this.contactsDict.get(Long.valueOf(tLRPC$User.id)) == null) {
                TLRPC$TL_contact tLRPC$TL_contact = new TLRPC$TL_contact();
                tLRPC$TL_contact.user_id = tLRPC$User.id;
                this.contacts.add(tLRPC$TL_contact);
                this.contactsDict.put(Long.valueOf(tLRPC$TL_contact.user_id), tLRPC$TL_contact);
            }
        }
        buildContactsSectionsArrays(true);
        getNotificationCenter().postNotificationName(NotificationCenter.contactsDidLoad, new Object[0]);
    }

    public void deleteContact(ArrayList<TLRPC$User> arrayList, boolean z) {
        if (arrayList != null && !arrayList.isEmpty()) {
            TLRPC$TL_contacts_deleteContacts tLRPC$TL_contacts_deleteContacts = new TLRPC$TL_contacts_deleteContacts();
            ArrayList arrayList2 = new ArrayList();
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                TLRPC$User tLRPC$User = arrayList.get(i);
                TLRPC$InputUser inputUser = getMessagesController().getInputUser(tLRPC$User);
                if (inputUser != null) {
                    tLRPC$User.contact = false;
                    arrayList2.add(Long.valueOf(tLRPC$User.id));
                    tLRPC$TL_contacts_deleteContacts.id.add(inputUser);
                }
            }
            getConnectionsManager().sendRequest(tLRPC$TL_contacts_deleteContacts, new ContactsController$$ExternalSyntheticLambda59(this, arrayList2, arrayList, z, arrayList.get(0).first_name));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$deleteContact$55(ArrayList arrayList, ArrayList arrayList2, boolean z, String str, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        int indexOf;
        if (tLRPC$TL_error == null) {
            getMessagesController().processUpdates((TLRPC$Updates) tLObject, false);
            getMessagesStorage().deleteContacts(arrayList);
            Utilities.phoneBookQueue.postRunnable(new ContactsController$$ExternalSyntheticLambda17(this, arrayList2));
            for (int i = 0; i < arrayList2.size(); i++) {
                TLRPC$User tLRPC$User = (TLRPC$User) arrayList2.get(i);
                if (!TextUtils.isEmpty(tLRPC$User.phone)) {
                    getMessagesStorage().applyPhoneBookUpdates(tLRPC$User.phone, "");
                    Contact contact = this.contactsBookSPhones.get(tLRPC$User.phone);
                    if (!(contact == null || (indexOf = contact.shortPhones.indexOf(tLRPC$User.phone)) == -1)) {
                        contact.phoneDeleted.set(indexOf, 1);
                    }
                }
            }
            AndroidUtilities.runOnUIThread(new ContactsController$$ExternalSyntheticLambda24(this, arrayList2, z, str));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$deleteContact$53(ArrayList arrayList) {
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            deleteContactFromPhoneBook(((TLRPC$User) it.next()).id);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$deleteContact$54(ArrayList arrayList, boolean z, String str) {
        Iterator it = arrayList.iterator();
        boolean z2 = false;
        while (it.hasNext()) {
            TLRPC$User tLRPC$User = (TLRPC$User) it.next();
            TLRPC$TL_contact tLRPC$TL_contact = this.contactsDict.get(Long.valueOf(tLRPC$User.id));
            if (tLRPC$TL_contact != null) {
                this.contacts.remove(tLRPC$TL_contact);
                this.contactsDict.remove(Long.valueOf(tLRPC$User.id));
                z2 = true;
            }
        }
        if (z2) {
            buildContactsSectionsArrays(false);
        }
        getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(MessagesController.UPDATE_MASK_NAME));
        getNotificationCenter().postNotificationName(NotificationCenter.contactsDidLoad, new Object[0]);
        if (z) {
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showBulletin, 1, LocaleController.formatString("DeletedFromYourContacts", NUM, str));
        }
    }

    private void reloadContactsStatuses() {
        saveContactsLoadTime();
        getMessagesController().clearFullUsers();
        SharedPreferences.Editor edit = MessagesController.getMainSettings(this.currentAccount).edit();
        edit.putBoolean("needGetStatuses", true).commit();
        getConnectionsManager().sendRequest(new TLRPC$TL_contacts_getStatuses(), new ContactsController$$ExternalSyntheticLambda57(this, edit));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$reloadContactsStatuses$57(SharedPreferences.Editor editor, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            AndroidUtilities.runOnUIThread(new ContactsController$$ExternalSyntheticLambda13(this, editor, tLObject));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$reloadContactsStatuses$56(SharedPreferences.Editor editor, TLObject tLObject) {
        editor.remove("needGetStatuses").commit();
        TLRPC$Vector tLRPC$Vector = (TLRPC$Vector) tLObject;
        if (!tLRPC$Vector.objects.isEmpty()) {
            ArrayList arrayList = new ArrayList();
            Iterator<Object> it = tLRPC$Vector.objects.iterator();
            while (it.hasNext()) {
                Object next = it.next();
                TLRPC$TL_user tLRPC$TL_user = new TLRPC$TL_user();
                TLRPC$TL_contactStatus tLRPC$TL_contactStatus = (TLRPC$TL_contactStatus) next;
                if (tLRPC$TL_contactStatus != null) {
                    TLRPC$UserStatus tLRPC$UserStatus = tLRPC$TL_contactStatus.status;
                    if (tLRPC$UserStatus instanceof TLRPC$TL_userStatusRecently) {
                        tLRPC$UserStatus.expires = -100;
                    } else if (tLRPC$UserStatus instanceof TLRPC$TL_userStatusLastWeek) {
                        tLRPC$UserStatus.expires = -101;
                    } else if (tLRPC$UserStatus instanceof TLRPC$TL_userStatusLastMonth) {
                        tLRPC$UserStatus.expires = -102;
                    }
                    TLRPC$User user = getMessagesController().getUser(Long.valueOf(tLRPC$TL_contactStatus.user_id));
                    if (user != null) {
                        user.status = tLRPC$TL_contactStatus.status;
                    }
                    tLRPC$TL_user.status = tLRPC$TL_contactStatus.status;
                    arrayList.add(tLRPC$TL_user);
                }
            }
            getMessagesStorage().updateUsers(arrayList, true, true, true);
        }
        getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(MessagesController.UPDATE_MASK_STATUS));
    }

    public void loadPrivacySettings() {
        if (this.loadingDeleteInfo == 0) {
            this.loadingDeleteInfo = 1;
            getConnectionsManager().sendRequest(new TLRPC$TL_account_getAccountTTL(), new ContactsController$$ExternalSyntheticLambda53(this));
        }
        if (this.loadingGlobalSettings == 0) {
            this.loadingGlobalSettings = 1;
            getConnectionsManager().sendRequest(new TLRPC$TL_account_getGlobalPrivacySettings(), new ContactsController$$ExternalSyntheticLambda52(this));
        }
        int i = 0;
        while (true) {
            int[] iArr = this.loadingPrivacyInfo;
            if (i < iArr.length) {
                if (iArr[i] == 0) {
                    iArr[i] = 1;
                    TLRPC$TL_account_getPrivacy tLRPC$TL_account_getPrivacy = new TLRPC$TL_account_getPrivacy();
                    switch (i) {
                        case 0:
                            tLRPC$TL_account_getPrivacy.key = new TLRPC$TL_inputPrivacyKeyStatusTimestamp();
                            break;
                        case 1:
                            tLRPC$TL_account_getPrivacy.key = new TLRPC$TL_inputPrivacyKeyChatInvite();
                            break;
                        case 2:
                            tLRPC$TL_account_getPrivacy.key = new TLRPC$TL_inputPrivacyKeyPhoneCall();
                            break;
                        case 3:
                            tLRPC$TL_account_getPrivacy.key = new TLRPC$TL_inputPrivacyKeyPhoneP2P();
                            break;
                        case 4:
                            tLRPC$TL_account_getPrivacy.key = new TLRPC$TL_inputPrivacyKeyProfilePhoto();
                            break;
                        case 5:
                            tLRPC$TL_account_getPrivacy.key = new TLRPC$TL_inputPrivacyKeyForwards();
                            break;
                        case 6:
                            tLRPC$TL_account_getPrivacy.key = new TLRPC$TL_inputPrivacyKeyPhoneNumber();
                            break;
                        default:
                            tLRPC$TL_account_getPrivacy.key = new TLRPC$TL_inputPrivacyKeyAddedByPhone();
                            break;
                    }
                    getConnectionsManager().sendRequest(tLRPC$TL_account_getPrivacy, new ContactsController$$ExternalSyntheticLambda55(this, i));
                }
                i++;
            } else {
                getNotificationCenter().postNotificationName(NotificationCenter.privacyRulesUpdated, new Object[0]);
                return;
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadPrivacySettings$59(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new ContactsController$$ExternalSyntheticLambda38(this, tLRPC$TL_error, tLObject));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadPrivacySettings$58(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
        if (tLRPC$TL_error == null) {
            this.deleteAccountTTL = ((TLRPC$TL_accountDaysTTL) tLObject).days;
            this.loadingDeleteInfo = 2;
        } else {
            this.loadingDeleteInfo = 0;
        }
        getNotificationCenter().postNotificationName(NotificationCenter.privacyRulesUpdated, new Object[0]);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadPrivacySettings$61(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new ContactsController$$ExternalSyntheticLambda37(this, tLRPC$TL_error, tLObject));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadPrivacySettings$60(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
        if (tLRPC$TL_error == null) {
            this.globalPrivacySettings = (TLRPC$TL_globalPrivacySettings) tLObject;
            this.loadingGlobalSettings = 2;
        } else {
            this.loadingGlobalSettings = 0;
        }
        getNotificationCenter().postNotificationName(NotificationCenter.privacyRulesUpdated, new Object[0]);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadPrivacySettings$63(int i, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new ContactsController$$ExternalSyntheticLambda39(this, tLRPC$TL_error, tLObject, i));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadPrivacySettings$62(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, int i) {
        if (tLRPC$TL_error == null) {
            TLRPC$TL_account_privacyRules tLRPC$TL_account_privacyRules = (TLRPC$TL_account_privacyRules) tLObject;
            getMessagesController().putUsers(tLRPC$TL_account_privacyRules.users, false);
            getMessagesController().putChats(tLRPC$TL_account_privacyRules.chats, false);
            switch (i) {
                case 0:
                    this.lastseenPrivacyRules = tLRPC$TL_account_privacyRules.rules;
                    break;
                case 1:
                    this.groupPrivacyRules = tLRPC$TL_account_privacyRules.rules;
                    break;
                case 2:
                    this.callPrivacyRules = tLRPC$TL_account_privacyRules.rules;
                    break;
                case 3:
                    this.p2pPrivacyRules = tLRPC$TL_account_privacyRules.rules;
                    break;
                case 4:
                    this.profilePhotoPrivacyRules = tLRPC$TL_account_privacyRules.rules;
                    break;
                case 5:
                    this.forwardsPrivacyRules = tLRPC$TL_account_privacyRules.rules;
                    break;
                case 6:
                    this.phonePrivacyRules = tLRPC$TL_account_privacyRules.rules;
                    break;
                default:
                    this.addedByPhonePrivacyRules = tLRPC$TL_account_privacyRules.rules;
                    break;
            }
            this.loadingPrivacyInfo[i] = 2;
        } else {
            this.loadingPrivacyInfo[i] = 0;
        }
        getNotificationCenter().postNotificationName(NotificationCenter.privacyRulesUpdated, new Object[0]);
    }

    public void setDeleteAccountTTL(int i) {
        this.deleteAccountTTL = i;
    }

    public int getDeleteAccountTTL() {
        return this.deleteAccountTTL;
    }

    public boolean getLoadingDeleteInfo() {
        return this.loadingDeleteInfo != 2;
    }

    public boolean getLoadingGlobalSettings() {
        return this.loadingGlobalSettings != 2;
    }

    public boolean getLoadingPrivicyInfo(int i) {
        return this.loadingPrivacyInfo[i] != 2;
    }

    public TLRPC$TL_globalPrivacySettings getGlobalPrivacySettings() {
        return this.globalPrivacySettings;
    }

    public ArrayList<TLRPC$PrivacyRule> getPrivacyRules(int i) {
        switch (i) {
            case 0:
                return this.lastseenPrivacyRules;
            case 1:
                return this.groupPrivacyRules;
            case 2:
                return this.callPrivacyRules;
            case 3:
                return this.p2pPrivacyRules;
            case 4:
                return this.profilePhotoPrivacyRules;
            case 5:
                return this.forwardsPrivacyRules;
            case 6:
                return this.phonePrivacyRules;
            case 7:
                return this.addedByPhonePrivacyRules;
            default:
                return null;
        }
    }

    public void setPrivacyRules(ArrayList<TLRPC$PrivacyRule> arrayList, int i) {
        switch (i) {
            case 0:
                this.lastseenPrivacyRules = arrayList;
                break;
            case 1:
                this.groupPrivacyRules = arrayList;
                break;
            case 2:
                this.callPrivacyRules = arrayList;
                break;
            case 3:
                this.p2pPrivacyRules = arrayList;
                break;
            case 4:
                this.profilePhotoPrivacyRules = arrayList;
                break;
            case 5:
                this.forwardsPrivacyRules = arrayList;
                break;
            case 6:
                this.phonePrivacyRules = arrayList;
                break;
            case 7:
                this.addedByPhonePrivacyRules = arrayList;
                break;
        }
        getNotificationCenter().postNotificationName(NotificationCenter.privacyRulesUpdated, new Object[0]);
        reloadContactsStatuses();
    }

    /* JADX WARNING: Removed duplicated region for block: B:13:0x00d0 A[Catch:{ Exception -> 0x0286 }] */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x0120 A[SYNTHETIC, Splitter:B:17:0x0120] */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x027b A[Catch:{ Exception -> 0x0286 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void createOrUpdateConnectionServiceContact(long r25, java.lang.String r27, java.lang.String r28) {
        /*
            r24 = this;
            r1 = r24
            r2 = r25
            r0 = r27
            r4 = r28
            java.lang.String r5 = "raw_contact_id=? AND mimetype=?"
            java.lang.String r6 = "vnd.android.cursor.item/group_membership"
            java.lang.String r7 = "TelegramConnectionService"
            java.lang.String r8 = "true"
            java.lang.String r9 = "caller_is_syncadapter"
            java.lang.String r10 = "mimetype"
            java.lang.String r11 = ""
            java.lang.String r12 = "raw_contact_id"
            boolean r13 = r24.hasContactsPermission()
            if (r13 != 0) goto L_0x001f
            return
        L_0x001f:
            android.content.Context r13 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0286 }
            android.content.ContentResolver r13 = r13.getContentResolver()     // Catch:{ Exception -> 0x0286 }
            java.util.ArrayList r15 = new java.util.ArrayList     // Catch:{ Exception -> 0x0286 }
            r15.<init>()     // Catch:{ Exception -> 0x0286 }
            android.net.Uri r14 = android.provider.ContactsContract.Groups.CONTENT_URI     // Catch:{ Exception -> 0x0286 }
            android.net.Uri$Builder r14 = r14.buildUpon()     // Catch:{ Exception -> 0x0286 }
            android.net.Uri$Builder r14 = r14.appendQueryParameter(r9, r8)     // Catch:{ Exception -> 0x0286 }
            android.net.Uri r14 = r14.build()     // Catch:{ Exception -> 0x0286 }
            android.net.Uri r16 = android.provider.ContactsContract.RawContacts.CONTENT_URI     // Catch:{ Exception -> 0x0286 }
            r17 = r14
            android.net.Uri$Builder r14 = r16.buildUpon()     // Catch:{ Exception -> 0x0286 }
            android.net.Uri$Builder r8 = r14.appendQueryParameter(r9, r8)     // Catch:{ Exception -> 0x0286 }
            android.net.Uri r8 = r8.build()     // Catch:{ Exception -> 0x0286 }
            r9 = 1
            java.lang.String[] r14 = new java.lang.String[r9]     // Catch:{ Exception -> 0x0286 }
            java.lang.String r16 = "_id"
            r9 = 0
            r14[r9] = r16     // Catch:{ Exception -> 0x0286 }
            java.lang.String r18 = "title=? AND account_type=? AND account_name=?"
            r9 = 3
            r20 = r10
            java.lang.String[] r10 = new java.lang.String[r9]     // Catch:{ Exception -> 0x0286 }
            r16 = 0
            r10[r16] = r7     // Catch:{ Exception -> 0x0286 }
            android.accounts.Account r9 = r1.systemAccount     // Catch:{ Exception -> 0x0286 }
            r16 = r14
            java.lang.String r14 = r9.type     // Catch:{ Exception -> 0x0286 }
            r19 = 1
            r10[r19] = r14     // Catch:{ Exception -> 0x0286 }
            java.lang.String r9 = r9.name     // Catch:{ Exception -> 0x0286 }
            r14 = 2
            r10[r14] = r9     // Catch:{ Exception -> 0x0286 }
            r19 = 0
            r9 = r17
            r4 = 2
            r14 = r13
            r21 = r15
            r15 = r9
            r17 = r18
            r18 = r10
            android.database.Cursor r10 = r14.query(r15, r16, r17, r18, r19)     // Catch:{ Exception -> 0x0286 }
            java.lang.String r15 = "account_name"
            java.lang.String r14 = "account_type"
            if (r10 == 0) goto L_0x0091
            boolean r16 = r10.moveToFirst()     // Catch:{ Exception -> 0x0286 }
            if (r16 == 0) goto L_0x0091
            r4 = 0
            int r7 = r10.getInt(r4)     // Catch:{ Exception -> 0x0286 }
            r17 = r14
            r16 = r15
            goto L_0x00ce
        L_0x0091:
            android.content.ContentValues r4 = new android.content.ContentValues     // Catch:{ Exception -> 0x0286 }
            r4.<init>()     // Catch:{ Exception -> 0x0286 }
            android.accounts.Account r0 = r1.systemAccount     // Catch:{ Exception -> 0x0286 }
            java.lang.String r0 = r0.type     // Catch:{ Exception -> 0x0286 }
            r4.put(r14, r0)     // Catch:{ Exception -> 0x0286 }
            android.accounts.Account r0 = r1.systemAccount     // Catch:{ Exception -> 0x0286 }
            java.lang.String r0 = r0.name     // Catch:{ Exception -> 0x0286 }
            r4.put(r15, r0)     // Catch:{ Exception -> 0x0286 }
            java.lang.String r0 = "group_visible"
            r17 = r14
            r16 = 0
            java.lang.Integer r14 = java.lang.Integer.valueOf(r16)     // Catch:{ Exception -> 0x0286 }
            r4.put(r0, r14)     // Catch:{ Exception -> 0x0286 }
            java.lang.String r0 = "group_is_read_only"
            r16 = r15
            r14 = 1
            java.lang.Integer r15 = java.lang.Integer.valueOf(r14)     // Catch:{ Exception -> 0x0286 }
            r4.put(r0, r15)     // Catch:{ Exception -> 0x0286 }
            java.lang.String r0 = "title"
            r4.put(r0, r7)     // Catch:{ Exception -> 0x0286 }
            android.net.Uri r0 = r13.insert(r9, r4)     // Catch:{ Exception -> 0x0286 }
            java.lang.String r0 = r0.getLastPathSegment()     // Catch:{ Exception -> 0x0286 }
            int r7 = java.lang.Integer.parseInt(r0)     // Catch:{ Exception -> 0x0286 }
        L_0x00ce:
            if (r10 == 0) goto L_0x00d3
            r10.close()     // Catch:{ Exception -> 0x0286 }
        L_0x00d3:
            android.net.Uri r15 = android.provider.ContactsContract.Data.CONTENT_URI     // Catch:{ Exception -> 0x0286 }
            r0 = 1
            java.lang.String[] r4 = new java.lang.String[r0]     // Catch:{ Exception -> 0x0286 }
            r0 = 0
            r4[r0] = r12     // Catch:{ Exception -> 0x0286 }
            java.lang.String r9 = "mimetype=? AND data1=?"
            r10 = 2
            java.lang.String[] r14 = new java.lang.String[r10]     // Catch:{ Exception -> 0x0286 }
            r14[r0] = r6     // Catch:{ Exception -> 0x0286 }
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0286 }
            r0.<init>()     // Catch:{ Exception -> 0x0286 }
            r0.append(r7)     // Catch:{ Exception -> 0x0286 }
            r0.append(r11)     // Catch:{ Exception -> 0x0286 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x0286 }
            r10 = 1
            r14[r10] = r0     // Catch:{ Exception -> 0x0286 }
            r19 = 0
            r0 = r14
            r10 = r17
            r14 = r13
            r22 = r13
            r13 = r16
            r16 = r4
            r17 = r9
            r18 = r0
            android.database.Cursor r0 = r14.query(r15, r16, r17, r18, r19)     // Catch:{ Exception -> 0x0286 }
            int r4 = r21.size()     // Catch:{ Exception -> 0x0286 }
            java.lang.String r9 = "+99084"
            java.lang.String r14 = "vnd.android.cursor.item/phone_v2"
            java.lang.String r15 = "data3"
            r16 = r7
            java.lang.String r7 = "data2"
            r17 = r6
            java.lang.String r6 = "vnd.android.cursor.item/name"
            r18 = r4
            java.lang.String r4 = "data1"
            if (r0 == 0) goto L_0x01d4
            boolean r19 = r0.moveToFirst()     // Catch:{ Exception -> 0x0286 }
            if (r19 == 0) goto L_0x01d4
            r12 = 0
            int r10 = r0.getInt(r12)     // Catch:{ Exception -> 0x0286 }
            android.content.ContentProviderOperation$Builder r8 = android.content.ContentProviderOperation.newUpdate(r8)     // Catch:{ Exception -> 0x0286 }
            java.lang.String r12 = "_id=?"
            r23 = r0
            r13 = 1
            java.lang.String[] r0 = new java.lang.String[r13]     // Catch:{ Exception -> 0x0286 }
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0286 }
            r13.<init>()     // Catch:{ Exception -> 0x0286 }
            r13.append(r10)     // Catch:{ Exception -> 0x0286 }
            r13.append(r11)     // Catch:{ Exception -> 0x0286 }
            java.lang.String r13 = r13.toString()     // Catch:{ Exception -> 0x0286 }
            r16 = 0
            r0[r16] = r13     // Catch:{ Exception -> 0x0286 }
            android.content.ContentProviderOperation$Builder r0 = r8.withSelection(r12, r0)     // Catch:{ Exception -> 0x0286 }
            java.lang.String r8 = "deleted"
            java.lang.Integer r12 = java.lang.Integer.valueOf(r16)     // Catch:{ Exception -> 0x0286 }
            android.content.ContentProviderOperation$Builder r0 = r0.withValue(r8, r12)     // Catch:{ Exception -> 0x0286 }
            android.content.ContentProviderOperation r0 = r0.build()     // Catch:{ Exception -> 0x0286 }
            r12 = r21
            r12.add(r0)     // Catch:{ Exception -> 0x0286 }
            android.net.Uri r0 = android.provider.ContactsContract.Data.CONTENT_URI     // Catch:{ Exception -> 0x0286 }
            android.content.ContentProviderOperation$Builder r0 = android.content.ContentProviderOperation.newUpdate(r0)     // Catch:{ Exception -> 0x0286 }
            r8 = 2
            java.lang.String[] r13 = new java.lang.String[r8]     // Catch:{ Exception -> 0x0286 }
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0286 }
            r8.<init>()     // Catch:{ Exception -> 0x0286 }
            r8.append(r10)     // Catch:{ Exception -> 0x0286 }
            r8.append(r11)     // Catch:{ Exception -> 0x0286 }
            java.lang.String r8 = r8.toString()     // Catch:{ Exception -> 0x0286 }
            r16 = 0
            r13[r16] = r8     // Catch:{ Exception -> 0x0286 }
            r8 = 1
            r13[r8] = r14     // Catch:{ Exception -> 0x0286 }
            android.content.ContentProviderOperation$Builder r0 = r0.withSelection(r5, r13)     // Catch:{ Exception -> 0x0286 }
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0286 }
            r8.<init>()     // Catch:{ Exception -> 0x0286 }
            r8.append(r9)     // Catch:{ Exception -> 0x0286 }
            r8.append(r2)     // Catch:{ Exception -> 0x0286 }
            java.lang.String r2 = r8.toString()     // Catch:{ Exception -> 0x0286 }
            android.content.ContentProviderOperation$Builder r0 = r0.withValue(r4, r2)     // Catch:{ Exception -> 0x0286 }
            android.content.ContentProviderOperation r0 = r0.build()     // Catch:{ Exception -> 0x0286 }
            r12.add(r0)     // Catch:{ Exception -> 0x0286 }
            android.net.Uri r0 = android.provider.ContactsContract.Data.CONTENT_URI     // Catch:{ Exception -> 0x0286 }
            android.content.ContentProviderOperation$Builder r0 = android.content.ContentProviderOperation.newUpdate(r0)     // Catch:{ Exception -> 0x0286 }
            r2 = 2
            java.lang.String[] r2 = new java.lang.String[r2]     // Catch:{ Exception -> 0x0286 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0286 }
            r3.<init>()     // Catch:{ Exception -> 0x0286 }
            r3.append(r10)     // Catch:{ Exception -> 0x0286 }
            r3.append(r11)     // Catch:{ Exception -> 0x0286 }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x0286 }
            r4 = 0
            r2[r4] = r3     // Catch:{ Exception -> 0x0286 }
            r3 = 1
            r2[r3] = r6     // Catch:{ Exception -> 0x0286 }
            android.content.ContentProviderOperation$Builder r0 = r0.withSelection(r5, r2)     // Catch:{ Exception -> 0x0286 }
            r5 = r27
            android.content.ContentProviderOperation$Builder r0 = r0.withValue(r7, r5)     // Catch:{ Exception -> 0x0286 }
            r11 = r28
            android.content.ContentProviderOperation$Builder r0 = r0.withValue(r15, r11)     // Catch:{ Exception -> 0x0286 }
            android.content.ContentProviderOperation r0 = r0.build()     // Catch:{ Exception -> 0x0286 }
            r12.add(r0)     // Catch:{ Exception -> 0x0286 }
            goto L_0x0279
        L_0x01d4:
            r5 = r27
            r11 = r28
            r23 = r0
            r19 = r12
            r12 = r21
            android.content.ContentProviderOperation$Builder r0 = android.content.ContentProviderOperation.newInsert(r8)     // Catch:{ Exception -> 0x0286 }
            android.accounts.Account r8 = r1.systemAccount     // Catch:{ Exception -> 0x0286 }
            java.lang.String r8 = r8.type     // Catch:{ Exception -> 0x0286 }
            android.content.ContentProviderOperation$Builder r0 = r0.withValue(r10, r8)     // Catch:{ Exception -> 0x0286 }
            android.accounts.Account r8 = r1.systemAccount     // Catch:{ Exception -> 0x0286 }
            java.lang.String r8 = r8.name     // Catch:{ Exception -> 0x0286 }
            android.content.ContentProviderOperation$Builder r0 = r0.withValue(r13, r8)     // Catch:{ Exception -> 0x0286 }
            java.lang.String r8 = "raw_contact_is_read_only"
            r10 = 1
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)     // Catch:{ Exception -> 0x0286 }
            android.content.ContentProviderOperation$Builder r0 = r0.withValue(r8, r10)     // Catch:{ Exception -> 0x0286 }
            java.lang.String r8 = "aggregation_mode"
            r10 = 3
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)     // Catch:{ Exception -> 0x0286 }
            android.content.ContentProviderOperation$Builder r0 = r0.withValue(r8, r10)     // Catch:{ Exception -> 0x0286 }
            android.content.ContentProviderOperation r0 = r0.build()     // Catch:{ Exception -> 0x0286 }
            r12.add(r0)     // Catch:{ Exception -> 0x0286 }
            android.net.Uri r0 = android.provider.ContactsContract.Data.CONTENT_URI     // Catch:{ Exception -> 0x0286 }
            android.content.ContentProviderOperation$Builder r0 = android.content.ContentProviderOperation.newInsert(r0)     // Catch:{ Exception -> 0x0286 }
            r8 = r18
            r10 = r19
            android.content.ContentProviderOperation$Builder r0 = r0.withValueBackReference(r10, r8)     // Catch:{ Exception -> 0x0286 }
            r13 = r20
            android.content.ContentProviderOperation$Builder r0 = r0.withValue(r13, r6)     // Catch:{ Exception -> 0x0286 }
            android.content.ContentProviderOperation$Builder r0 = r0.withValue(r7, r5)     // Catch:{ Exception -> 0x0286 }
            android.content.ContentProviderOperation$Builder r0 = r0.withValue(r15, r11)     // Catch:{ Exception -> 0x0286 }
            android.content.ContentProviderOperation r0 = r0.build()     // Catch:{ Exception -> 0x0286 }
            r12.add(r0)     // Catch:{ Exception -> 0x0286 }
            android.net.Uri r0 = android.provider.ContactsContract.Data.CONTENT_URI     // Catch:{ Exception -> 0x0286 }
            android.content.ContentProviderOperation$Builder r0 = android.content.ContentProviderOperation.newInsert(r0)     // Catch:{ Exception -> 0x0286 }
            android.content.ContentProviderOperation$Builder r0 = r0.withValueBackReference(r10, r8)     // Catch:{ Exception -> 0x0286 }
            android.content.ContentProviderOperation$Builder r0 = r0.withValue(r13, r14)     // Catch:{ Exception -> 0x0286 }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0286 }
            r5.<init>()     // Catch:{ Exception -> 0x0286 }
            r5.append(r9)     // Catch:{ Exception -> 0x0286 }
            r5.append(r2)     // Catch:{ Exception -> 0x0286 }
            java.lang.String r2 = r5.toString()     // Catch:{ Exception -> 0x0286 }
            android.content.ContentProviderOperation$Builder r0 = r0.withValue(r4, r2)     // Catch:{ Exception -> 0x0286 }
            android.content.ContentProviderOperation r0 = r0.build()     // Catch:{ Exception -> 0x0286 }
            r12.add(r0)     // Catch:{ Exception -> 0x0286 }
            android.net.Uri r0 = android.provider.ContactsContract.Data.CONTENT_URI     // Catch:{ Exception -> 0x0286 }
            android.content.ContentProviderOperation$Builder r0 = android.content.ContentProviderOperation.newInsert(r0)     // Catch:{ Exception -> 0x0286 }
            android.content.ContentProviderOperation$Builder r0 = r0.withValueBackReference(r10, r8)     // Catch:{ Exception -> 0x0286 }
            r2 = r17
            android.content.ContentProviderOperation$Builder r0 = r0.withValue(r13, r2)     // Catch:{ Exception -> 0x0286 }
            java.lang.Integer r2 = java.lang.Integer.valueOf(r16)     // Catch:{ Exception -> 0x0286 }
            android.content.ContentProviderOperation$Builder r0 = r0.withValue(r4, r2)     // Catch:{ Exception -> 0x0286 }
            android.content.ContentProviderOperation r0 = r0.build()     // Catch:{ Exception -> 0x0286 }
            r12.add(r0)     // Catch:{ Exception -> 0x0286 }
        L_0x0279:
            if (r23 == 0) goto L_0x027e
            r23.close()     // Catch:{ Exception -> 0x0286 }
        L_0x027e:
            java.lang.String r0 = "com.android.contacts"
            r2 = r22
            r2.applyBatch(r0, r12)     // Catch:{ Exception -> 0x0286 }
            goto L_0x028a
        L_0x0286:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x028a:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ContactsController.createOrUpdateConnectionServiceContact(long, java.lang.String, java.lang.String):void");
    }

    public void deleteConnectionServiceContact() {
        if (hasContactsPermission()) {
            try {
                ContentResolver contentResolver = ApplicationLoader.applicationContext.getContentResolver();
                Account account = this.systemAccount;
                Cursor query = contentResolver.query(ContactsContract.Groups.CONTENT_URI, new String[]{"_id"}, "title=? AND account_type=? AND account_name=?", new String[]{"TelegramConnectionService", account.type, account.name}, (String) null);
                if (query != null && query.moveToFirst()) {
                    int i = query.getInt(0);
                    query.close();
                    Cursor query2 = contentResolver.query(ContactsContract.Data.CONTENT_URI, new String[]{"raw_contact_id"}, "mimetype=? AND data1=?", new String[]{"vnd.android.cursor.item/group_membership", i + ""}, (String) null);
                    if (query2 != null && query2.moveToFirst()) {
                        int i2 = query2.getInt(0);
                        query2.close();
                        Uri uri = ContactsContract.RawContacts.CONTENT_URI;
                        contentResolver.delete(uri, "_id=?", new String[]{i2 + ""});
                    } else if (query2 != null) {
                        query2.close();
                    }
                } else if (query != null) {
                    query.close();
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public static String formatName(String str, String str2) {
        return formatName(str, str2, 0);
    }

    public static String formatName(String str, String str2, int i) {
        if (str != null) {
            str = str.trim();
        }
        if (str2 != null) {
            str2 = str2.trim();
        }
        StringBuilder sb = new StringBuilder((str != null ? str.length() : 0) + (str2 != null ? str2.length() : 0) + 1);
        if (LocaleController.nameDisplayOrder == 1) {
            if (str == null || str.length() <= 0) {
                if (str2 != null && str2.length() > 0) {
                    if (i > 0 && str2.length() > i + 2) {
                        return str2.substring(0, i);
                    }
                    sb.append(str2);
                }
            } else if (i > 0 && str.length() > i + 2) {
                return str.substring(0, i);
            } else {
                sb.append(str);
                if (str2 != null && str2.length() > 0) {
                    sb.append(" ");
                    if (i <= 0 || sb.length() + str2.length() <= i) {
                        sb.append(str2);
                    } else {
                        sb.append(str2.charAt(0));
                    }
                }
            }
        } else if (str2 == null || str2.length() <= 0) {
            if (str != null && str.length() > 0) {
                if (i > 0 && str.length() > i + 2) {
                    return str.substring(0, i);
                }
                sb.append(str);
            }
        } else if (i > 0 && str2.length() > i + 2) {
            return str2.substring(0, i);
        } else {
            sb.append(str2);
            if (str != null && str.length() > 0) {
                sb.append(" ");
                if (i <= 0 || sb.length() + str.length() <= i) {
                    sb.append(str);
                } else {
                    sb.append(str.charAt(0));
                }
            }
        }
        return sb.toString();
    }
}
