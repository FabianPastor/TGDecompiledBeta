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
import j$.util.Comparator;
import j$.util.concurrent.ConcurrentHashMap;
import j$.util.function.Function;
import j$.util.function.ToDoubleFunction;
import j$.util.function.ToIntFunction;
import j$.util.function.ToLongFunction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
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
    private static volatile ContactsController[] Instance = new ContactsController[3];
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
    public ConcurrentHashMap<Integer, TLRPC$TL_contact> contactsDict = new ConcurrentHashMap<>(20, 1.0f, 2);
    public boolean contactsLoaded;
    private boolean contactsSyncInProgress;
    private ArrayList<Integer> delayedContactsUpdate = new ArrayList<>();
    private int deleteAccountTTL;
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

    static /* synthetic */ void lambda$resetImportedContacts$9(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    private class MyContentObserver extends ContentObserver {
        private Runnable checkRunnable = $$Lambda$ContactsController$MyContentObserver$vyGJAhCUHIHralUsOZp6otsI86w.INSTANCE;

        public boolean deliverSelfNotifications() {
            return false;
        }

        static /* synthetic */ void lambda$new$0() {
            for (int i = 0; i < 3; i++) {
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
            Utilities.globalQueue.postRunnable(new Runnable() {
                public final void run() {
                    ContactsController.this.lambda$new$0$ContactsController();
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$ContactsController() {
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
        Utilities.globalQueue.postRunnable(new Runnable() {
            public final void run() {
                ContactsController.this.lambda$cleanup$1$ContactsController();
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$cleanup$1 */
    public /* synthetic */ void lambda$cleanup$1$ContactsController() {
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
            getConnectionsManager().sendRequest(new TLRPC$TL_help_getInviteText(), new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    ContactsController.this.lambda$checkInviteText$3$ContactsController(tLObject, tLRPC$TL_error);
                }
            }, 2);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$checkInviteText$3 */
    public /* synthetic */ void lambda$checkInviteText$3$ContactsController(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            TLRPC$TL_help_inviteText tLRPC$TL_help_inviteText = (TLRPC$TL_help_inviteText) tLObject;
            if (tLRPC$TL_help_inviteText.message.length() != 0) {
                AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_help_inviteText) {
                    public final /* synthetic */ TLRPC$TL_help_inviteText f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        ContactsController.this.lambda$null$2$ContactsController(this.f$1);
                    }
                });
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$2 */
    public /* synthetic */ void lambda$null$2$ContactsController(TLRPC$TL_help_inviteText tLRPC$TL_help_inviteText) {
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
                    if (i2 >= 3) {
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
                    if (i2 >= 3) {
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
        Utilities.globalQueue.postRunnable(new Runnable() {
            public final void run() {
                ContactsController.this.lambda$checkContacts$4$ContactsController();
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$checkContacts$4 */
    public /* synthetic */ void lambda$checkContacts$4$ContactsController() {
        if (checkContactsInternal()) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("detected contacts change");
            }
            performSyncPhoneBook(getContactsCopy(this.contactsBook), true, false, true, false, true, false);
        }
    }

    public void forceImportContacts() {
        Utilities.globalQueue.postRunnable(new Runnable() {
            public final void run() {
                ContactsController.this.lambda$forceImportContacts$5$ContactsController();
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$forceImportContacts$5 */
    public /* synthetic */ void lambda$forceImportContacts$5$ContactsController() {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("force import contacts");
        }
        performSyncPhoneBook(new HashMap(), true, true, true, true, false, false);
    }

    public void syncPhoneBookByAlert(HashMap<String, Contact> hashMap, boolean z, boolean z2, boolean z3) {
        Utilities.globalQueue.postRunnable(new Runnable(hashMap, z, z2, z3) {
            public final /* synthetic */ HashMap f$1;
            public final /* synthetic */ boolean f$2;
            public final /* synthetic */ boolean f$3;
            public final /* synthetic */ boolean f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void run() {
                ContactsController.this.lambda$syncPhoneBookByAlert$6$ContactsController(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$syncPhoneBookByAlert$6 */
    public /* synthetic */ void lambda$syncPhoneBookByAlert$6$ContactsController(HashMap hashMap, boolean z, boolean z2, boolean z3) {
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
        getConnectionsManager().sendRequest(tLRPC$TL_contacts_deleteContacts, new RequestDelegate(runnable) {
            public final /* synthetic */ Runnable f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                ContactsController.this.lambda$deleteAllContacts$8$ContactsController(this.f$1, tLObject, tLRPC$TL_error);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$deleteAllContacts$8 */
    public /* synthetic */ void lambda$deleteAllContacts$8$ContactsController(Runnable runnable, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
            AndroidUtilities.runOnUIThread(new Runnable(runnable) {
                public final /* synthetic */ Runnable f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    ContactsController.this.lambda$null$7$ContactsController(this.f$1);
                }
            });
            return;
        }
        AndroidUtilities.runOnUIThread(runnable);
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Can't wrap try/catch for region: R(8:0|1|2|(4:5|(2:6|(1:20)(3:8|(2:10|(3:22|12|21)(1:24))(1:23)|13))|14|3)|15|16|17|19) */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:15:0x0049 */
    /* renamed from: lambda$null$7 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$null$7$ContactsController(java.lang.Runnable r13) {
        /*
            r12 = this;
            java.lang.String r0 = "org.telegram.messenger"
            java.lang.String r1 = ""
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext
            android.accounts.AccountManager r2 = android.accounts.AccountManager.get(r2)
            r3 = 0
            r4 = 0
            android.accounts.Account[] r5 = r2.getAccountsByType(r0)     // Catch:{ all -> 0x0049 }
            r12.systemAccount = r3     // Catch:{ all -> 0x0049 }
            r6 = 0
        L_0x0013:
            int r7 = r5.length     // Catch:{ all -> 0x0049 }
            if (r6 >= r7) goto L_0x0049
            r7 = r5[r6]     // Catch:{ all -> 0x0049 }
            r8 = 0
        L_0x0019:
            r9 = 3
            if (r8 >= r9) goto L_0x0046
            org.telegram.messenger.UserConfig r9 = org.telegram.messenger.UserConfig.getInstance(r8)     // Catch:{ all -> 0x0049 }
            org.telegram.tgnet.TLRPC$User r9 = r9.getCurrentUser()     // Catch:{ all -> 0x0049 }
            if (r9 == 0) goto L_0x0043
            java.lang.String r10 = r7.name     // Catch:{ all -> 0x0049 }
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ all -> 0x0049 }
            r11.<init>()     // Catch:{ all -> 0x0049 }
            r11.append(r1)     // Catch:{ all -> 0x0049 }
            int r9 = r9.id     // Catch:{ all -> 0x0049 }
            r11.append(r9)     // Catch:{ all -> 0x0049 }
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
            org.telegram.messenger.UserConfig r7 = r12.getUserConfig()     // Catch:{ Exception -> 0x006a }
            int r7 = r7.getClientUserId()     // Catch:{ Exception -> 0x006a }
            r6.append(r7)     // Catch:{ Exception -> 0x006a }
            java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x006a }
            r5.<init>(r6, r0)     // Catch:{ Exception -> 0x006a }
            r12.systemAccount = r5     // Catch:{ Exception -> 0x006a }
            r2.addAccountExplicitly(r5, r1, r3)     // Catch:{ Exception -> 0x006a }
        L_0x006a:
            org.telegram.messenger.MessagesStorage r0 = r12.getMessagesStorage()
            java.util.HashMap r1 = new java.util.HashMap
            r1.<init>()
            r2 = 1
            r0.putCachedPhoneBook(r1, r4, r2)
            org.telegram.messenger.MessagesStorage r0 = r12.getMessagesStorage()
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            r0.putContacts(r1, r2)
            java.util.ArrayList<org.telegram.messenger.ContactsController$Contact> r0 = r12.phoneBookContacts
            r0.clear()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_contact> r0 = r12.contacts
            r0.clear()
            j$.util.concurrent.ConcurrentHashMap<java.lang.Integer, org.telegram.tgnet.TLRPC$TL_contact> r0 = r12.contactsDict
            r0.clear()
            java.util.HashMap<java.lang.String, java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_contact>> r0 = r12.usersSectionsDict
            r0.clear()
            java.util.HashMap<java.lang.String, java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_contact>> r0 = r12.usersMutualSectionsDict
            r0.clear()
            java.util.ArrayList<java.lang.String> r0 = r12.sortedUsersSectionsArray
            r0.clear()
            java.util.HashMap<java.lang.String, java.util.ArrayList<java.lang.Object>> r0 = r12.phoneBookSectionsDict
            r0.clear()
            java.util.ArrayList<java.lang.String> r0 = r12.phoneBookSectionsArray
            r0.clear()
            java.util.ArrayList<java.lang.Integer> r0 = r12.delayedContactsUpdate
            r0.clear()
            java.util.ArrayList<java.lang.String> r0 = r12.sortedUsersMutualSectionsArray
            r0.clear()
            java.util.HashMap<java.lang.String, org.telegram.tgnet.TLRPC$TL_contact> r0 = r12.contactsByPhone
            r0.clear()
            java.util.HashMap<java.lang.String, org.telegram.tgnet.TLRPC$TL_contact> r0 = r12.contactsByShortPhone
            r0.clear()
            org.telegram.messenger.NotificationCenter r0 = r12.getNotificationCenter()
            int r1 = org.telegram.messenger.NotificationCenter.contactsDidLoad
            java.lang.Object[] r2 = new java.lang.Object[r4]
            r0.postNotificationName(r1, r2)
            r12.loadContacts(r4, r4)
            r13.run()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ContactsController.lambda$null$7$ContactsController(java.lang.Runnable):void");
    }

    public void resetImportedContacts() {
        getConnectionsManager().sendRequest(new TLRPC$TL_contacts_resetSaved(), $$Lambda$ContactsController$21fDIh5PkZMGrZvoXXeEuxK5Cns.INSTANCE);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0052, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0053, code lost:
        if (r3 != null) goto L_0x0055;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:?, code lost:
        r3.close();
     */
    /* JADX WARNING: Missing exception handler attribute for start block: B:27:0x0058 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean checkContactsInternal() {
        /*
            r9 = this;
            java.lang.String r0 = "version"
            r1 = 0
            boolean r2 = r9.hasContactsPermission()     // Catch:{ Exception -> 0x0064 }
            if (r2 != 0) goto L_0x000b
            return r1
        L_0x000b:
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0064 }
            android.content.ContentResolver r3 = r2.getContentResolver()     // Catch:{ Exception -> 0x0064 }
            android.net.Uri r4 = android.provider.ContactsContract.RawContacts.CONTENT_URI     // Catch:{ Exception -> 0x005f }
            r2 = 1
            java.lang.String[] r5 = new java.lang.String[r2]     // Catch:{ Exception -> 0x005f }
            r5[r1] = r0     // Catch:{ Exception -> 0x005f }
            r6 = 0
            r7 = 0
            r8 = 0
            android.database.Cursor r3 = r3.query(r4, r5, r6, r7, r8)     // Catch:{ Exception -> 0x005f }
            if (r3 == 0) goto L_0x0059
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0050 }
            r4.<init>()     // Catch:{ all -> 0x0050 }
        L_0x0026:
            boolean r5 = r3.moveToNext()     // Catch:{ all -> 0x0050 }
            if (r5 == 0) goto L_0x0038
            int r5 = r3.getColumnIndex(r0)     // Catch:{ all -> 0x0050 }
            java.lang.String r5 = r3.getString(r5)     // Catch:{ all -> 0x0050 }
            r4.append(r5)     // Catch:{ all -> 0x0050 }
            goto L_0x0026
        L_0x0038:
            java.lang.String r0 = r4.toString()     // Catch:{ all -> 0x0050 }
            java.lang.String r4 = r9.lastContactsVersions     // Catch:{ all -> 0x0050 }
            int r4 = r4.length()     // Catch:{ all -> 0x0050 }
            if (r4 == 0) goto L_0x004d
            java.lang.String r4 = r9.lastContactsVersions     // Catch:{ all -> 0x0050 }
            boolean r4 = r4.equals(r0)     // Catch:{ all -> 0x0050 }
            if (r4 != 0) goto L_0x004d
            r1 = 1
        L_0x004d:
            r9.lastContactsVersions = r0     // Catch:{ all -> 0x0050 }
            goto L_0x0059
        L_0x0050:
            r0 = move-exception
            throw r0     // Catch:{ all -> 0x0052 }
        L_0x0052:
            r0 = move-exception
            if (r3 == 0) goto L_0x0058
            r3.close()     // Catch:{ all -> 0x0058 }
        L_0x0058:
            throw r0     // Catch:{ Exception -> 0x005f }
        L_0x0059:
            if (r3 == 0) goto L_0x0068
            r3.close()     // Catch:{ Exception -> 0x005f }
            goto L_0x0068
        L_0x005f:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x0064 }
            goto L_0x0068
        L_0x0064:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0068:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ContactsController.checkContactsInternal():boolean");
    }

    public void readContacts() {
        synchronized (this.loadContactsSync) {
            if (!this.loadingContacts) {
                this.loadingContacts = true;
                Utilities.stageQueue.postRunnable(new Runnable() {
                    public final void run() {
                        ContactsController.this.lambda$readContacts$10$ContactsController();
                    }
                });
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$readContacts$10 */
    public /* synthetic */ void lambda$readContacts$10$ContactsController() {
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
    /* JADX WARNING: Removed duplicated region for block: B:192:0x032a A[Catch:{ all -> 0x0342 }] */
    /* JADX WARNING: Removed duplicated region for block: B:194:0x032f A[SYNTHETIC, Splitter:B:194:0x032f] */
    /* JADX WARNING: Removed duplicated region for block: B:200:0x033c  */
    /* JADX WARNING: Removed duplicated region for block: B:221:? A[RETURN, SYNTHETIC] */
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
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x0321 }
            r0.<init>()     // Catch:{ all -> 0x0321 }
            android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0321 }
            android.content.ContentResolver r3 = r3.getContentResolver()     // Catch:{ all -> 0x0321 }
            java.util.HashMap r10 = new java.util.HashMap     // Catch:{ all -> 0x0321 }
            r10.<init>()     // Catch:{ all -> 0x0321 }
            java.util.ArrayList r11 = new java.util.ArrayList     // Catch:{ all -> 0x0321 }
            r11.<init>()     // Catch:{ all -> 0x0321 }
            android.net.Uri r5 = android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI     // Catch:{ all -> 0x0321 }
            java.lang.String[] r6 = r1.projectionPhones     // Catch:{ all -> 0x0321 }
            r7 = 0
            r8 = 0
            r9 = 0
            r4 = r3
            android.database.Cursor r4 = r4.query(r5, r6, r7, r8, r9)     // Catch:{ all -> 0x0321 }
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
            r0 = 2131626893(0x7f0e0b8d, float:1.8881035E38)
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
            r3 = 2131626891(0x7f0e0b8b, float:1.888103E38)
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
            r3 = 2131626901(0x7f0e0b95, float:1.8881051E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r3)     // Catch:{ all -> 0x01eb }
            r0.add(r1)     // Catch:{ all -> 0x01eb }
            goto L_0x01d4
        L_0x01b3:
            r0 = 12
            if (r14 != r0) goto L_0x01c6
            java.util.ArrayList<java.lang.String> r0 = r13.phoneTypes     // Catch:{ all -> 0x01eb }
            java.lang.String r1 = "PhoneMain"
            r3 = 2131626892(0x7f0e0b8c, float:1.8881033E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r3)     // Catch:{ all -> 0x01eb }
            r0.add(r1)     // Catch:{ all -> 0x01eb }
            goto L_0x01d4
        L_0x01c6:
            java.util.ArrayList<java.lang.String> r0 = r13.phoneTypes     // Catch:{ all -> 0x01eb }
            java.lang.String r1 = "PhoneOther"
            r3 = 2131626900(0x7f0e0b94, float:1.888105E38)
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
            goto L_0x0325
        L_0x01f1:
            r3 = r6
            r1 = 0
            goto L_0x01ff
        L_0x01f4:
            r0 = move-exception
            r2 = 0
            r10 = r20
            goto L_0x0325
        L_0x01fa:
            r18 = r3
            r2 = 1
            r1 = r4
            r3 = 0
        L_0x01ff:
            java.lang.String r0 = ","
            java.lang.String r0 = android.text.TextUtils.join(r0, r11)     // Catch:{ all -> 0x031b }
            android.net.Uri r5 = android.provider.ContactsContract.Data.CONTENT_URI     // Catch:{ all -> 0x031b }
            r10 = r20
            java.lang.String[] r6 = r10.projectionNames     // Catch:{ all -> 0x0319 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0319 }
            r4.<init>()     // Catch:{ all -> 0x0319 }
            java.lang.String r7 = "lookup IN ("
            r4.append(r7)     // Catch:{ all -> 0x0319 }
            r4.append(r0)     // Catch:{ all -> 0x0319 }
            java.lang.String r0 = ") AND "
            r4.append(r0)     // Catch:{ all -> 0x0319 }
            java.lang.String r0 = "mimetype"
            r4.append(r0)     // Catch:{ all -> 0x0319 }
            java.lang.String r0 = " = '"
            r4.append(r0)     // Catch:{ all -> 0x0319 }
            java.lang.String r0 = "vnd.android.cursor.item/name"
            r4.append(r0)     // Catch:{ all -> 0x0319 }
            java.lang.String r0 = "'"
            r4.append(r0)     // Catch:{ all -> 0x0319 }
            java.lang.String r7 = r4.toString()     // Catch:{ all -> 0x0319 }
            r8 = 0
            r9 = 0
            r4 = r18
            r0 = 1
            android.database.Cursor r1 = r4.query(r5, r6, r7, r8, r9)     // Catch:{ all -> 0x0319 }
            if (r1 == 0) goto L_0x030c
        L_0x0241:
            boolean r2 = r1.moveToNext()     // Catch:{ all -> 0x0319 }
            if (r2 == 0) goto L_0x0307
            r2 = 0
            java.lang.String r4 = r1.getString(r2)     // Catch:{ all -> 0x0319 }
            java.lang.String r5 = r1.getString(r0)     // Catch:{ all -> 0x0319 }
            r6 = 2
            java.lang.String r7 = r1.getString(r6)     // Catch:{ all -> 0x0319 }
            r8 = 3
            java.lang.String r9 = r1.getString(r8)     // Catch:{ all -> 0x0319 }
            java.lang.Object r4 = r3.get(r4)     // Catch:{ all -> 0x0319 }
            org.telegram.messenger.ContactsController$Contact r4 = (org.telegram.messenger.ContactsController.Contact) r4     // Catch:{ all -> 0x0319 }
            if (r4 == 0) goto L_0x0241
            boolean r11 = r4.namesFilled     // Catch:{ all -> 0x0319 }
            if (r11 != 0) goto L_0x0241
            boolean r11 = r4.isGoodProvider     // Catch:{ all -> 0x0319 }
            java.lang.String r12 = " "
            if (r11 == 0) goto L_0x02a2
            if (r5 == 0) goto L_0x0271
            r4.first_name = r5     // Catch:{ all -> 0x0319 }
            goto L_0x0273
        L_0x0271:
            r4.first_name = r15     // Catch:{ all -> 0x0319 }
        L_0x0273:
            if (r7 == 0) goto L_0x0278
            r4.last_name = r7     // Catch:{ all -> 0x0319 }
            goto L_0x027a
        L_0x0278:
            r4.last_name = r15     // Catch:{ all -> 0x0319 }
        L_0x027a:
            boolean r5 = android.text.TextUtils.isEmpty(r9)     // Catch:{ all -> 0x0319 }
            if (r5 != 0) goto L_0x0303
            java.lang.String r5 = r4.first_name     // Catch:{ all -> 0x0319 }
            boolean r5 = android.text.TextUtils.isEmpty(r5)     // Catch:{ all -> 0x0319 }
            if (r5 != 0) goto L_0x029f
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0319 }
            r5.<init>()     // Catch:{ all -> 0x0319 }
            java.lang.String r7 = r4.first_name     // Catch:{ all -> 0x0319 }
            r5.append(r7)     // Catch:{ all -> 0x0319 }
            r5.append(r12)     // Catch:{ all -> 0x0319 }
            r5.append(r9)     // Catch:{ all -> 0x0319 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x0319 }
            r4.first_name = r5     // Catch:{ all -> 0x0319 }
            goto L_0x0303
        L_0x029f:
            r4.first_name = r9     // Catch:{ all -> 0x0319 }
            goto L_0x0303
        L_0x02a2:
            boolean r11 = r10.isNotValidNameString(r5)     // Catch:{ all -> 0x0319 }
            if (r11 != 0) goto L_0x02b8
            java.lang.String r11 = r4.first_name     // Catch:{ all -> 0x0319 }
            boolean r11 = r11.contains(r5)     // Catch:{ all -> 0x0319 }
            if (r11 != 0) goto L_0x02ce
            java.lang.String r11 = r4.first_name     // Catch:{ all -> 0x0319 }
            boolean r11 = r5.contains(r11)     // Catch:{ all -> 0x0319 }
            if (r11 != 0) goto L_0x02ce
        L_0x02b8:
            boolean r11 = r10.isNotValidNameString(r7)     // Catch:{ all -> 0x0319 }
            if (r11 != 0) goto L_0x0303
            java.lang.String r11 = r4.last_name     // Catch:{ all -> 0x0319 }
            boolean r11 = r11.contains(r7)     // Catch:{ all -> 0x0319 }
            if (r11 != 0) goto L_0x02ce
            java.lang.String r11 = r4.last_name     // Catch:{ all -> 0x0319 }
            boolean r11 = r5.contains(r11)     // Catch:{ all -> 0x0319 }
            if (r11 == 0) goto L_0x0303
        L_0x02ce:
            if (r5 == 0) goto L_0x02d3
            r4.first_name = r5     // Catch:{ all -> 0x0319 }
            goto L_0x02d5
        L_0x02d3:
            r4.first_name = r15     // Catch:{ all -> 0x0319 }
        L_0x02d5:
            boolean r5 = android.text.TextUtils.isEmpty(r9)     // Catch:{ all -> 0x0319 }
            if (r5 != 0) goto L_0x02fc
            java.lang.String r5 = r4.first_name     // Catch:{ all -> 0x0319 }
            boolean r5 = android.text.TextUtils.isEmpty(r5)     // Catch:{ all -> 0x0319 }
            if (r5 != 0) goto L_0x02fa
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0319 }
            r5.<init>()     // Catch:{ all -> 0x0319 }
            java.lang.String r11 = r4.first_name     // Catch:{ all -> 0x0319 }
            r5.append(r11)     // Catch:{ all -> 0x0319 }
            r5.append(r12)     // Catch:{ all -> 0x0319 }
            r5.append(r9)     // Catch:{ all -> 0x0319 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x0319 }
            r4.first_name = r5     // Catch:{ all -> 0x0319 }
            goto L_0x02fc
        L_0x02fa:
            r4.first_name = r9     // Catch:{ all -> 0x0319 }
        L_0x02fc:
            if (r7 == 0) goto L_0x0301
            r4.last_name = r7     // Catch:{ all -> 0x0319 }
            goto L_0x0303
        L_0x0301:
            r4.last_name = r15     // Catch:{ all -> 0x0319 }
        L_0x0303:
            r4.namesFilled = r0     // Catch:{ all -> 0x0319 }
            goto L_0x0241
        L_0x0307:
            r1.close()     // Catch:{ Exception -> 0x030a }
        L_0x030a:
            r2 = 0
            goto L_0x030d
        L_0x030c:
            r2 = r1
        L_0x030d:
            if (r2 == 0) goto L_0x0339
            r2.close()     // Catch:{ Exception -> 0x0313 }
            goto L_0x0339
        L_0x0313:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
            goto L_0x0339
        L_0x0319:
            r0 = move-exception
            goto L_0x031e
        L_0x031b:
            r0 = move-exception
            r10 = r20
        L_0x031e:
            r4 = r1
            r2 = r3
            goto L_0x0325
        L_0x0321:
            r0 = move-exception
            r10 = r1
            r2 = 0
            r4 = 0
        L_0x0325:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x0342 }
            if (r2 == 0) goto L_0x032d
            r2.clear()     // Catch:{ all -> 0x0342 }
        L_0x032d:
            if (r4 == 0) goto L_0x0338
            r4.close()     // Catch:{ Exception -> 0x0333 }
            goto L_0x0338
        L_0x0333:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x0338:
            r3 = r2
        L_0x0339:
            if (r3 == 0) goto L_0x033c
            goto L_0x0341
        L_0x033c:
            java.util.HashMap r3 = new java.util.HashMap
            r3.<init>()
        L_0x0341:
            return r3
        L_0x0342:
            r0 = move-exception
            r1 = r0
            if (r4 == 0) goto L_0x034f
            r4.close()     // Catch:{ Exception -> 0x034a }
            goto L_0x034f
        L_0x034a:
            r0 = move-exception
            r2 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
        L_0x034f:
            goto L_0x0351
        L_0x0350:
            throw r1
        L_0x0351:
            goto L_0x0350
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
        Utilities.globalQueue.postRunnable(new Runnable(sparseArray) {
            public final /* synthetic */ SparseArray f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                ContactsController.this.lambda$migratePhoneBookToV7$11$ContactsController(this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$migratePhoneBookToV7$11 */
    public /* synthetic */ void lambda$migratePhoneBookToV7$11$ContactsController(SparseArray sparseArray) {
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
            Utilities.globalQueue.postRunnable(new Runnable(hashMap, z3, z, z2, z4, z5, z6) {
                public final /* synthetic */ HashMap f$1;
                public final /* synthetic */ boolean f$2;
                public final /* synthetic */ boolean f$3;
                public final /* synthetic */ boolean f$4;
                public final /* synthetic */ boolean f$5;
                public final /* synthetic */ boolean f$6;
                public final /* synthetic */ boolean f$7;

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
                    ContactsController.this.lambda$performSyncPhoneBook$24$ContactsController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:119:0x02f2, code lost:
        if (r11.intValue() == 1) goto L_0x0304;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:210:0x04b2, code lost:
        if ((r13.contactsByPhone.size() - r0) > ((r13.contactsByPhone.size() / 3) * 2)) goto L_0x04b6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x0132, code lost:
        if (r2.first_name.equals(r4.first_name) != false) goto L_0x0137;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:47:0x0147, code lost:
        if (r2.last_name.equals(r4.last_name) == false) goto L_0x0149;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x0149, code lost:
        r0 = true;
     */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x026a  */
    /* JADX WARNING: Removed duplicated region for block: B:114:0x02bc  */
    /* JADX WARNING: Removed duplicated region for block: B:132:0x033a  */
    /* JADX WARNING: Removed duplicated region for block: B:214:0x04ba  */
    /* JADX WARNING: Removed duplicated region for block: B:216:0x04e6  */
    /* JADX WARNING: Removed duplicated region for block: B:218:0x04f8  */
    /* JADX WARNING: Removed duplicated region for block: B:241:0x033d A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x011a  */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x0120  */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x014b  */
    /* JADX WARNING: Removed duplicated region for block: B:80:0x01e4  */
    /* renamed from: lambda$performSyncPhoneBook$24 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$performSyncPhoneBook$24$ContactsController(java.util.HashMap r27, boolean r28, boolean r29, boolean r30, boolean r31, boolean r32, boolean r33) {
        /*
            r26 = this;
            r13 = r26
            r3 = r27
            java.util.HashMap r0 = new java.util.HashMap
            r0.<init>()
            java.util.Set r1 = r27.entrySet()
            java.util.Iterator r1 = r1.iterator()
        L_0x0011:
            boolean r2 = r1.hasNext()
            r8 = 0
            if (r2 == 0) goto L_0x0038
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
            r0.put(r4, r2)
            int r8 = r8 + 1
            goto L_0x0024
        L_0x0038:
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r1 == 0) goto L_0x0041
            java.lang.String r1 = "start read contacts from phone"
            org.telegram.messenger.FileLog.d(r1)
        L_0x0041:
            if (r28 != 0) goto L_0x0046
            r26.checkContactsInternal()
        L_0x0046:
            java.util.HashMap r14 = r26.readContactsFromPhoneBook()
            java.util.HashMap r15 = new java.util.HashMap
            r15.<init>()
            java.util.HashMap r12 = new java.util.HashMap
            r12.<init>()
            java.util.ArrayList r11 = new java.util.ArrayList
            r11.<init>()
            java.util.Set r1 = r14.entrySet()
            java.util.Iterator r1 = r1.iterator()
        L_0x0061:
            boolean r2 = r1.hasNext()
            if (r2 == 0) goto L_0x00b3
            java.lang.Object r2 = r1.next()
            java.util.Map$Entry r2 = (java.util.Map.Entry) r2
            java.lang.Object r2 = r2.getValue()
            org.telegram.messenger.ContactsController$Contact r2 = (org.telegram.messenger.ContactsController.Contact) r2
            java.util.ArrayList<java.lang.String> r4 = r2.shortPhones
            int r4 = r4.size()
            r5 = 0
        L_0x007a:
            if (r5 >= r4) goto L_0x0098
            java.util.ArrayList<java.lang.String> r6 = r2.shortPhones
            java.lang.Object r6 = r6.get(r5)
            java.lang.String r6 = (java.lang.String) r6
            int r7 = r6.length()
            int r7 = r7 + -7
            int r7 = java.lang.Math.max(r8, r7)
            java.lang.String r6 = r6.substring(r7)
            r12.put(r6, r2)
            int r5 = r5 + 1
            goto L_0x007a
        L_0x0098:
            java.lang.String r4 = r2.getLetter()
            java.lang.Object r5 = r15.get(r4)
            java.util.ArrayList r5 = (java.util.ArrayList) r5
            if (r5 != 0) goto L_0x00af
            java.util.ArrayList r5 = new java.util.ArrayList
            r5.<init>()
            r15.put(r4, r5)
            r11.add(r4)
        L_0x00af:
            r5.add(r2)
            goto L_0x0061
        L_0x00b3:
            java.util.HashMap r10 = new java.util.HashMap
            r10.<init>()
            int r1 = r27.size()
            java.util.ArrayList r9 = new java.util.ArrayList
            r9.<init>()
            boolean r2 = r27.isEmpty()
            java.lang.String r5 = ""
            if (r2 != 0) goto L_0x0396
            java.util.Set r2 = r14.entrySet()
            java.util.Iterator r2 = r2.iterator()
            r7 = 0
            r16 = 0
        L_0x00d4:
            boolean r17 = r2.hasNext()
            if (r17 == 0) goto L_0x034f
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
            r31 = r2
            if (r17 != 0) goto L_0x0116
        L_0x00fa:
            java.util.ArrayList<java.lang.String> r2 = r4.shortPhones
            int r2 = r2.size()
            if (r8 >= r2) goto L_0x0116
            java.util.ArrayList<java.lang.String> r2 = r4.shortPhones
            java.lang.Object r2 = r2.get(r8)
            java.lang.Object r2 = r0.get(r2)
            org.telegram.messenger.ContactsController$Contact r2 = (org.telegram.messenger.ContactsController.Contact) r2
            if (r2 == 0) goto L_0x0113
            java.lang.String r6 = r2.key
            goto L_0x0118
        L_0x0113:
            int r8 = r8 + 1
            goto L_0x00fa
        L_0x0116:
            r2 = r17
        L_0x0118:
            if (r2 == 0) goto L_0x011e
            int r8 = r2.imported
            r4.imported = r8
        L_0x011e:
            if (r2 == 0) goto L_0x014b
            java.lang.String r8 = r4.first_name
            boolean r8 = android.text.TextUtils.isEmpty(r8)
            if (r8 != 0) goto L_0x0135
            java.lang.String r8 = r2.first_name
            r17 = r0
            java.lang.String r0 = r4.first_name
            boolean r0 = r8.equals(r0)
            if (r0 == 0) goto L_0x0149
            goto L_0x0137
        L_0x0135:
            r17 = r0
        L_0x0137:
            java.lang.String r0 = r4.last_name
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x014d
            java.lang.String r0 = r2.last_name
            java.lang.String r8 = r4.last_name
            boolean r0 = r0.equals(r8)
            if (r0 != 0) goto L_0x014d
        L_0x0149:
            r0 = 1
            goto L_0x014e
        L_0x014b:
            r17 = r0
        L_0x014d:
            r0 = 0
        L_0x014e:
            if (r2 == 0) goto L_0x02ab
            if (r0 == 0) goto L_0x0154
            goto L_0x02ab
        L_0x0154:
            r0 = 0
        L_0x0155:
            java.util.ArrayList<java.lang.String> r8 = r4.phones
            int r8 = r8.size()
            if (r0 >= r8) goto L_0x0295
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
            if (r29 == 0) goto L_0x01d8
            r24 = r12
            java.util.HashMap<java.lang.String, org.telegram.tgnet.TLRPC$TL_contact> r12 = r13.contactsByPhone
            java.lang.Object r12 = r12.get(r8)
            org.telegram.tgnet.TLRPC$TL_contact r12 = (org.telegram.tgnet.TLRPC$TL_contact) r12
            if (r12 == 0) goto L_0x01cb
            r25 = r11
            org.telegram.messenger.MessagesController r11 = r26.getMessagesController()
            int r12 = r12.user_id
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            org.telegram.tgnet.TLRPC$User r11 = r11.getUser(r12)
            if (r11 == 0) goto L_0x01c7
            int r16 = r16 + 1
            java.lang.String r12 = r11.first_name
            boolean r12 = android.text.TextUtils.isEmpty(r12)
            if (r12 == 0) goto L_0x01c7
            java.lang.String r11 = r11.last_name
            boolean r11 = android.text.TextUtils.isEmpty(r11)
            if (r11 == 0) goto L_0x01c7
            java.lang.String r11 = r4.first_name
            boolean r11 = android.text.TextUtils.isEmpty(r11)
            if (r11 == 0) goto L_0x01c3
            java.lang.String r11 = r4.last_name
            boolean r11 = android.text.TextUtils.isEmpty(r11)
            if (r11 != 0) goto L_0x01c7
        L_0x01c3:
            r11 = 1
            r24 = -1
            goto L_0x01c8
        L_0x01c7:
            r11 = 0
        L_0x01c8:
            r12 = r24
            goto L_0x01df
        L_0x01cb:
            r25 = r11
            java.util.HashMap<java.lang.String, org.telegram.tgnet.TLRPC$TL_contact> r11 = r13.contactsByShortPhone
            boolean r11 = r11.containsKey(r5)
            if (r11 == 0) goto L_0x01dc
            int r16 = r16 + 1
            goto L_0x01dc
        L_0x01d8:
            r25 = r11
            r24 = r12
        L_0x01dc:
            r12 = r24
            r11 = 0
        L_0x01df:
            r24 = r15
            r15 = -1
            if (r12 != r15) goto L_0x026a
            if (r29 == 0) goto L_0x0289
            if (r11 != 0) goto L_0x0240
            java.util.HashMap<java.lang.String, org.telegram.tgnet.TLRPC$TL_contact> r11 = r13.contactsByPhone
            java.lang.Object r8 = r11.get(r8)
            org.telegram.tgnet.TLRPC$TL_contact r8 = (org.telegram.tgnet.TLRPC$TL_contact) r8
            if (r8 == 0) goto L_0x0236
            org.telegram.messenger.MessagesController r5 = r26.getMessagesController()
            int r8 = r8.user_id
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            org.telegram.tgnet.TLRPC$User r5 = r5.getUser(r8)
            if (r5 == 0) goto L_0x0233
            int r16 = r16 + 1
            java.lang.String r8 = r5.first_name
            if (r8 == 0) goto L_0x0209
            goto L_0x020b
        L_0x0209:
            r8 = r23
        L_0x020b:
            java.lang.String r5 = r5.last_name
            if (r5 == 0) goto L_0x0210
            goto L_0x0212
        L_0x0210:
            r5 = r23
        L_0x0212:
            java.lang.String r11 = r4.first_name
            boolean r8 = r8.equals(r11)
            if (r8 == 0) goto L_0x0222
            java.lang.String r8 = r4.last_name
            boolean r5 = r5.equals(r8)
            if (r5 != 0) goto L_0x0289
        L_0x0222:
            java.lang.String r5 = r4.first_name
            boolean r5 = android.text.TextUtils.isEmpty(r5)
            if (r5 == 0) goto L_0x0240
            java.lang.String r5 = r4.last_name
            boolean r5 = android.text.TextUtils.isEmpty(r5)
            if (r5 == 0) goto L_0x0240
            goto L_0x0289
        L_0x0233:
            int r7 = r7 + 1
            goto L_0x0240
        L_0x0236:
            java.util.HashMap<java.lang.String, org.telegram.tgnet.TLRPC$TL_contact> r8 = r13.contactsByShortPhone
            boolean r5 = r8.containsKey(r5)
            if (r5 == 0) goto L_0x0240
            int r16 = r16 + 1
        L_0x0240:
            org.telegram.tgnet.TLRPC$TL_inputPhoneContact r5 = new org.telegram.tgnet.TLRPC$TL_inputPhoneContact
            r5.<init>()
            int r8 = r4.contact_id
            long r11 = (long) r8
            r5.client_id = r11
            r15 = r7
            long r7 = (long) r0
            r18 = 32
            long r7 = r7 << r18
            long r7 = r7 | r11
            r5.client_id = r7
            java.lang.String r7 = r4.first_name
            r5.first_name = r7
            java.lang.String r7 = r4.last_name
            r5.last_name = r7
            java.util.ArrayList<java.lang.String> r7 = r4.phones
            java.lang.Object r7 = r7.get(r0)
            java.lang.String r7 = (java.lang.String) r7
            r5.phone = r7
            r9.add(r5)
            r7 = r15
            goto L_0x0289
        L_0x026a:
            java.util.ArrayList<java.lang.Integer> r5 = r4.phoneDeleted
            java.util.ArrayList<java.lang.Integer> r8 = r2.phoneDeleted
            java.lang.Object r8 = r8.get(r12)
            r5.set(r0, r8)
            java.util.ArrayList<java.lang.String> r5 = r2.phones
            r5.remove(r12)
            java.util.ArrayList<java.lang.String> r5 = r2.shortPhones
            r5.remove(r12)
            java.util.ArrayList<java.lang.Integer> r5 = r2.phoneDeleted
            r5.remove(r12)
            java.util.ArrayList<java.lang.String> r5 = r2.phoneTypes
            r5.remove(r12)
        L_0x0289:
            int r0 = r0 + 1
            r12 = r22
            r5 = r23
            r15 = r24
            r11 = r25
            goto L_0x0155
        L_0x0295:
            r23 = r5
            r25 = r11
            r22 = r12
            r24 = r15
            java.util.ArrayList<java.lang.String> r0 = r2.phones
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x02a8
            r3.remove(r6)
        L_0x02a8:
            r15 = r10
            goto L_0x033d
        L_0x02ab:
            r23 = r5
            r25 = r11
            r22 = r12
            r24 = r15
            r5 = 0
        L_0x02b4:
            java.util.ArrayList<java.lang.String> r8 = r4.phones
            int r8 = r8.size()
            if (r5 >= r8) goto L_0x0337
            java.util.ArrayList<java.lang.String> r8 = r4.shortPhones
            java.lang.Object r8 = r8.get(r5)
            java.lang.String r8 = (java.lang.String) r8
            int r11 = r8.length()
            int r11 = r11 + -7
            r12 = 0
            int r11 = java.lang.Math.max(r12, r11)
            r8.substring(r11)
            r10.put(r8, r4)
            if (r2 == 0) goto L_0x02f5
            java.util.ArrayList<java.lang.String> r11 = r2.shortPhones
            int r11 = r11.indexOf(r8)
            r12 = -1
            if (r11 == r12) goto L_0x02f6
            java.util.ArrayList<java.lang.Integer> r15 = r2.phoneDeleted
            java.lang.Object r11 = r15.get(r11)
            java.lang.Integer r11 = (java.lang.Integer) r11
            java.util.ArrayList<java.lang.Integer> r15 = r4.phoneDeleted
            r15.set(r5, r11)
            int r11 = r11.intValue()
            r15 = 1
            if (r11 != r15) goto L_0x02f6
            goto L_0x0304
        L_0x02f5:
            r12 = -1
        L_0x02f6:
            if (r29 == 0) goto L_0x0304
            if (r0 != 0) goto L_0x0308
            java.util.HashMap<java.lang.String, org.telegram.tgnet.TLRPC$TL_contact> r11 = r13.contactsByPhone
            boolean r8 = r11.containsKey(r8)
            if (r8 == 0) goto L_0x0306
            int r16 = r16 + 1
        L_0x0304:
            r15 = r10
            goto L_0x0330
        L_0x0306:
            int r7 = r7 + 1
        L_0x0308:
            org.telegram.tgnet.TLRPC$TL_inputPhoneContact r8 = new org.telegram.tgnet.TLRPC$TL_inputPhoneContact
            r8.<init>()
            int r11 = r4.contact_id
            long r12 = (long) r11
            r8.client_id = r12
            r15 = r10
            long r10 = (long) r5
            r18 = 32
            long r10 = r10 << r18
            long r10 = r10 | r12
            r8.client_id = r10
            java.lang.String r10 = r4.first_name
            r8.first_name = r10
            java.lang.String r10 = r4.last_name
            r8.last_name = r10
            java.util.ArrayList<java.lang.String> r10 = r4.phones
            java.lang.Object r10 = r10.get(r5)
            java.lang.String r10 = (java.lang.String) r10
            r8.phone = r10
            r9.add(r8)
        L_0x0330:
            int r5 = r5 + 1
            r13 = r26
            r10 = r15
            goto L_0x02b4
        L_0x0337:
            r15 = r10
            if (r2 == 0) goto L_0x033d
            r3.remove(r6)
        L_0x033d:
            r13 = r26
            r2 = r31
            r10 = r15
            r0 = r17
            r12 = r22
            r5 = r23
            r15 = r24
            r11 = r25
            r8 = 0
            goto L_0x00d4
        L_0x034f:
            r25 = r11
            r22 = r12
            r24 = r15
            r15 = r10
            if (r30 != 0) goto L_0x0374
            boolean r0 = r27.isEmpty()
            if (r0 == 0) goto L_0x0374
            boolean r0 = r9.isEmpty()
            if (r0 == 0) goto L_0x0374
            int r0 = r14.size()
            if (r1 != r0) goto L_0x0374
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0373
            java.lang.String r0 = "contacts not changed!"
            org.telegram.messenger.FileLog.d(r0)
        L_0x0373:
            return
        L_0x0374:
            if (r29 == 0) goto L_0x0390
            boolean r0 = r27.isEmpty()
            if (r0 != 0) goto L_0x0390
            boolean r0 = r14.isEmpty()
            if (r0 != 0) goto L_0x0390
            boolean r0 = r9.isEmpty()
            if (r0 == 0) goto L_0x0390
            org.telegram.messenger.MessagesStorage r0 = r26.getMessagesStorage()
            r2 = 0
            r0.putCachedPhoneBook(r14, r2, r2)
        L_0x0390:
            r13 = r26
            r0 = r16
            goto L_0x0478
        L_0x0396:
            r23 = r5
            r25 = r11
            r22 = r12
            r24 = r15
            r15 = r10
            if (r29 == 0) goto L_0x0474
            java.util.Set r0 = r14.entrySet()
            java.util.Iterator r0 = r0.iterator()
            r16 = 0
        L_0x03ab:
            boolean r2 = r0.hasNext()
            if (r2 == 0) goto L_0x046f
            java.lang.Object r2 = r0.next()
            java.util.Map$Entry r2 = (java.util.Map.Entry) r2
            java.lang.Object r4 = r2.getValue()
            org.telegram.messenger.ContactsController$Contact r4 = (org.telegram.messenger.ContactsController.Contact) r4
            java.lang.Object r2 = r2.getKey()
            java.lang.String r2 = (java.lang.String) r2
            r2 = 0
        L_0x03c4:
            java.util.ArrayList<java.lang.String> r5 = r4.phones
            int r5 = r5.size()
            if (r2 >= r5) goto L_0x046b
            if (r31 != 0) goto L_0x043f
            java.util.ArrayList<java.lang.String> r5 = r4.shortPhones
            java.lang.Object r5 = r5.get(r2)
            java.lang.String r5 = (java.lang.String) r5
            int r6 = r5.length()
            int r6 = r6 + -7
            r7 = 0
            int r6 = java.lang.Math.max(r7, r6)
            java.lang.String r6 = r5.substring(r6)
            r13 = r26
            java.util.HashMap<java.lang.String, org.telegram.tgnet.TLRPC$TL_contact> r7 = r13.contactsByPhone
            java.lang.Object r5 = r7.get(r5)
            org.telegram.tgnet.TLRPC$TL_contact r5 = (org.telegram.tgnet.TLRPC$TL_contact) r5
            if (r5 == 0) goto L_0x0434
            org.telegram.messenger.MessagesController r6 = r26.getMessagesController()
            int r5 = r5.user_id
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            org.telegram.tgnet.TLRPC$User r5 = r6.getUser(r5)
            if (r5 == 0) goto L_0x0441
            int r16 = r16 + 1
            java.lang.String r6 = r5.first_name
            if (r6 == 0) goto L_0x0408
            goto L_0x040a
        L_0x0408:
            r6 = r23
        L_0x040a:
            java.lang.String r5 = r5.last_name
            if (r5 == 0) goto L_0x040f
            goto L_0x0411
        L_0x040f:
            r5 = r23
        L_0x0411:
            java.lang.String r7 = r4.first_name
            boolean r6 = r6.equals(r7)
            if (r6 == 0) goto L_0x0421
            java.lang.String r6 = r4.last_name
            boolean r5 = r5.equals(r6)
            if (r5 != 0) goto L_0x0431
        L_0x0421:
            java.lang.String r5 = r4.first_name
            boolean r5 = android.text.TextUtils.isEmpty(r5)
            if (r5 == 0) goto L_0x0441
            java.lang.String r5 = r4.last_name
            boolean r5 = android.text.TextUtils.isEmpty(r5)
            if (r5 == 0) goto L_0x0441
        L_0x0431:
            r8 = 32
            goto L_0x0467
        L_0x0434:
            java.util.HashMap<java.lang.String, org.telegram.tgnet.TLRPC$TL_contact> r5 = r13.contactsByShortPhone
            boolean r5 = r5.containsKey(r6)
            if (r5 == 0) goto L_0x0441
            int r16 = r16 + 1
            goto L_0x0441
        L_0x043f:
            r13 = r26
        L_0x0441:
            org.telegram.tgnet.TLRPC$TL_inputPhoneContact r5 = new org.telegram.tgnet.TLRPC$TL_inputPhoneContact
            r5.<init>()
            int r6 = r4.contact_id
            long r6 = (long) r6
            r5.client_id = r6
            long r10 = (long) r2
            r8 = 32
            long r10 = r10 << r8
            long r6 = r6 | r10
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
        L_0x0467:
            int r2 = r2 + 1
            goto L_0x03c4
        L_0x046b:
            r13 = r26
            goto L_0x03ab
        L_0x046f:
            r13 = r26
            r0 = r16
            goto L_0x0477
        L_0x0474:
            r13 = r26
            r0 = 0
        L_0x0477:
            r7 = 0
        L_0x0478:
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r2 == 0) goto L_0x0481
            java.lang.String r2 = "done processing contacts"
            org.telegram.messenger.FileLog.d(r2)
        L_0x0481:
            if (r29 == 0) goto L_0x05da
            boolean r2 = r9.isEmpty()
            if (r2 != 0) goto L_0x05c0
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r2 == 0) goto L_0x0492
            java.lang.String r2 = "start import contacts"
            org.telegram.messenger.FileLog.e((java.lang.String) r2)
        L_0x0492:
            r2 = 2
            if (r32 == 0) goto L_0x04b5
            if (r7 == 0) goto L_0x04b5
            r4 = 30
            if (r7 < r4) goto L_0x049d
            r2 = 1
            goto L_0x04b6
        L_0x049d:
            if (r30 == 0) goto L_0x04b5
            if (r1 != 0) goto L_0x04b5
            java.util.HashMap<java.lang.String, org.telegram.tgnet.TLRPC$TL_contact> r1 = r13.contactsByPhone
            int r1 = r1.size()
            int r1 = r1 - r0
            java.util.HashMap<java.lang.String, org.telegram.tgnet.TLRPC$TL_contact> r4 = r13.contactsByPhone
            int r4 = r4.size()
            int r4 = r4 / 3
            int r4 = r4 * 2
            if (r1 <= r4) goto L_0x04b5
            goto L_0x04b6
        L_0x04b5:
            r2 = 0
        L_0x04b6:
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r1 == 0) goto L_0x04e4
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
            java.util.HashMap<java.lang.String, org.telegram.tgnet.TLRPC$TL_contact> r0 = r13.contactsByPhone
            int r0 = r0.size()
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            org.telegram.messenger.FileLog.d(r0)
        L_0x04e4:
            if (r2 == 0) goto L_0x04f8
            org.telegram.messenger.-$$Lambda$ContactsController$iSjRBxIazQK6ZkyWs4bu1H2sTho r6 = new org.telegram.messenger.-$$Lambda$ContactsController$iSjRBxIazQK6ZkyWs4bu1H2sTho
            r0 = r6
            r1 = r26
            r3 = r27
            r4 = r30
            r5 = r28
            r0.<init>(r2, r3, r4, r5)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r6)
            return
        L_0x04f8:
            if (r33 == 0) goto L_0x0512
            org.telegram.messenger.DispatchQueue r8 = org.telegram.messenger.Utilities.stageQueue
            org.telegram.messenger.-$$Lambda$ContactsController$LMZjImnukMH1AYxFCLMOgW2BFrM r9 = new org.telegram.messenger.-$$Lambda$ContactsController$LMZjImnukMH1AYxFCLMOgW2BFrM
            r0 = r9
            r1 = r26
            r2 = r15
            r3 = r14
            r4 = r30
            r5 = r24
            r6 = r25
            r7 = r22
            r0.<init>(r2, r3, r4, r5, r6, r7)
            r8.postRunnable(r9)
            return
        L_0x0512:
            r0 = 1
            boolean[] r12 = new boolean[r0]
            r0 = 0
            r12[r0] = r0
            java.util.HashMap r11 = new java.util.HashMap
            r11.<init>(r14)
            android.util.SparseArray r10 = new android.util.SparseArray
            r10.<init>()
            java.util.Set r0 = r11.entrySet()
            java.util.Iterator r0 = r0.iterator()
        L_0x052a:
            boolean r1 = r0.hasNext()
            if (r1 == 0) goto L_0x0544
            java.lang.Object r1 = r0.next()
            java.util.Map$Entry r1 = (java.util.Map.Entry) r1
            java.lang.Object r1 = r1.getValue()
            org.telegram.messenger.ContactsController$Contact r1 = (org.telegram.messenger.ContactsController.Contact) r1
            int r2 = r1.contact_id
            java.lang.String r1 = r1.key
            r10.put(r2, r1)
            goto L_0x052a
        L_0x0544:
            r1 = 0
            r13.completedRequestsCount = r1
            int r0 = r9.size()
            double r0 = (double) r0
            r2 = 4647503709213818880(0x407fNUM, double:500.0)
            java.lang.Double.isNaN(r0)
            double r0 = r0 / r2
            double r0 = java.lang.Math.ceil(r0)
            int r8 = (int) r0
            r7 = 0
        L_0x055b:
            if (r7 >= r8) goto L_0x0601
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
            org.telegram.tgnet.ConnectionsManager r5 = r26.getConnectionsManager()
            org.telegram.messenger.-$$Lambda$ContactsController$ooON_q7n0JbU8KZjSximI7nfqz4 r4 = new org.telegram.messenger.-$$Lambda$ContactsController$ooON_q7n0JbU8KZjSximI7nfqz4
            r0 = r4
            r1 = r26
            r2 = r11
            r3 = r10
            r13 = r4
            r4 = r12
            r16 = r13
            r13 = r5
            r5 = r14
            r27 = r6
            r20 = r7
            r7 = r8
            r17 = r8
            r8 = r15
            r18 = r9
            r9 = r30
            r19 = r10
            r10 = r24
            r23 = r11
            r21 = r25
            r11 = r21
            r25 = r12
            r12 = r22
            r0.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12)
            r0 = 6
            r1 = r27
            r2 = r16
            r13.sendRequest(r1, r2, r0)
            int r7 = r20 + 1
            r13 = r26
            r8 = r17
            r9 = r18
            r10 = r19
            r11 = r23
            r12 = r25
            r25 = r21
            goto L_0x055b
        L_0x05c0:
            r21 = r25
            org.telegram.messenger.DispatchQueue r8 = org.telegram.messenger.Utilities.stageQueue
            org.telegram.messenger.-$$Lambda$ContactsController$1YcZDPkn8DFWAaM5JXSPSkNYSJg r9 = new org.telegram.messenger.-$$Lambda$ContactsController$1YcZDPkn8DFWAaM5JXSPSkNYSJg
            r0 = r9
            r1 = r26
            r2 = r15
            r3 = r14
            r4 = r30
            r5 = r24
            r6 = r21
            r7 = r22
            r0.<init>(r2, r3, r4, r5, r6, r7)
            r8.postRunnable(r9)
            goto L_0x0601
        L_0x05da:
            r21 = r25
            org.telegram.messenger.DispatchQueue r8 = org.telegram.messenger.Utilities.stageQueue
            org.telegram.messenger.-$$Lambda$ContactsController$kF4M-wOUmX8Biz3_1Yekn5W70m8 r9 = new org.telegram.messenger.-$$Lambda$ContactsController$kF4M-wOUmX8Biz3_1Yekn5W70m8
            r0 = r9
            r1 = r26
            r2 = r15
            r3 = r14
            r4 = r30
            r5 = r24
            r6 = r21
            r7 = r22
            r0.<init>(r2, r3, r4, r5, r6, r7)
            r8.postRunnable(r9)
            boolean r0 = r14.isEmpty()
            if (r0 != 0) goto L_0x0601
            org.telegram.messenger.MessagesStorage r0 = r26.getMessagesStorage()
            r1 = 0
            r0.putCachedPhoneBook(r14, r1, r1)
        L_0x0601:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ContactsController.lambda$performSyncPhoneBook$24$ContactsController(java.util.HashMap, boolean, boolean, boolean, boolean, boolean, boolean):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$13 */
    public /* synthetic */ void lambda$null$13$ContactsController(int i, HashMap hashMap, boolean z, boolean z2) {
        getNotificationCenter().postNotificationName(NotificationCenter.hasNewContactsToImport, Integer.valueOf(i), hashMap, Boolean.valueOf(z), Boolean.valueOf(z2));
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$15 */
    public /* synthetic */ void lambda$null$15$ContactsController(HashMap hashMap, HashMap hashMap2, boolean z, HashMap hashMap3, ArrayList arrayList, HashMap hashMap4) {
        this.contactsBookSPhones = hashMap;
        this.contactsBook = hashMap2;
        this.contactsSyncInProgress = false;
        this.contactsBookLoaded = true;
        if (z) {
            this.contactsLoaded = true;
        }
        if (!this.delayedContactsUpdate.isEmpty() && this.contactsLoaded) {
            applyContactsUpdates(this.delayedContactsUpdate, (ConcurrentHashMap<Integer, TLRPC$User>) null, (ArrayList<TLRPC$TL_contact>) null, (ArrayList<Integer>) null);
            this.delayedContactsUpdate.clear();
        }
        getMessagesStorage().putCachedPhoneBook(hashMap2, false, false);
        AndroidUtilities.runOnUIThread(new Runnable(hashMap3, arrayList, hashMap4) {
            public final /* synthetic */ HashMap f$1;
            public final /* synthetic */ ArrayList f$2;
            public final /* synthetic */ HashMap f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                ContactsController.this.lambda$null$14$ContactsController(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$14 */
    public /* synthetic */ void lambda$null$14$ContactsController(HashMap hashMap, ArrayList arrayList, HashMap hashMap2) {
        lambda$null$22(hashMap, arrayList, hashMap2);
        updateUnregisteredContacts();
        getNotificationCenter().postNotificationName(NotificationCenter.contactsDidLoad, new Object[0]);
        getNotificationCenter().postNotificationName(NotificationCenter.contactsImported, new Object[0]);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$19 */
    public /* synthetic */ void lambda$null$19$ContactsController(HashMap hashMap, SparseArray sparseArray, boolean[] zArr, HashMap hashMap2, TLRPC$TL_contacts_importContacts tLRPC$TL_contacts_importContacts, int i, HashMap hashMap3, boolean z, HashMap hashMap4, ArrayList arrayList, HashMap hashMap5, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
            Utilities.stageQueue.postRunnable(new Runnable(hashMap3, hashMap2, z, hashMap4, arrayList, hashMap5, zArr) {
                public final /* synthetic */ HashMap f$1;
                public final /* synthetic */ HashMap f$2;
                public final /* synthetic */ boolean f$3;
                public final /* synthetic */ HashMap f$4;
                public final /* synthetic */ ArrayList f$5;
                public final /* synthetic */ HashMap f$6;
                public final /* synthetic */ boolean[] f$7;

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
                    ContactsController.this.lambda$null$18$ContactsController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$18 */
    public /* synthetic */ void lambda$null$18$ContactsController(HashMap hashMap, HashMap hashMap2, boolean z, HashMap hashMap3, ArrayList arrayList, HashMap hashMap4, boolean[] zArr) {
        this.contactsBookSPhones = hashMap;
        this.contactsBook = hashMap2;
        this.contactsSyncInProgress = false;
        this.contactsBookLoaded = true;
        if (z) {
            this.contactsLoaded = true;
        }
        if (!this.delayedContactsUpdate.isEmpty() && this.contactsLoaded) {
            applyContactsUpdates(this.delayedContactsUpdate, (ConcurrentHashMap<Integer, TLRPC$User>) null, (ArrayList<TLRPC$TL_contact>) null, (ArrayList<Integer>) null);
            this.delayedContactsUpdate.clear();
        }
        AndroidUtilities.runOnUIThread(new Runnable(hashMap3, arrayList, hashMap4) {
            public final /* synthetic */ HashMap f$1;
            public final /* synthetic */ ArrayList f$2;
            public final /* synthetic */ HashMap f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                ContactsController.this.lambda$null$16$ContactsController(this.f$1, this.f$2, this.f$3);
            }
        });
        if (zArr[0]) {
            Utilities.globalQueue.postRunnable(new Runnable() {
                public final void run() {
                    ContactsController.this.lambda$null$17$ContactsController();
                }
            }, 300000);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$16 */
    public /* synthetic */ void lambda$null$16$ContactsController(HashMap hashMap, ArrayList arrayList, HashMap hashMap2) {
        lambda$null$22(hashMap, arrayList, hashMap2);
        getNotificationCenter().postNotificationName(NotificationCenter.contactsImported, new Object[0]);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$17 */
    public /* synthetic */ void lambda$null$17$ContactsController() {
        getMessagesStorage().getCachedPhoneBook(true);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$21 */
    public /* synthetic */ void lambda$null$21$ContactsController(HashMap hashMap, HashMap hashMap2, boolean z, HashMap hashMap3, ArrayList arrayList, HashMap hashMap4) {
        this.contactsBookSPhones = hashMap;
        this.contactsBook = hashMap2;
        this.contactsSyncInProgress = false;
        this.contactsBookLoaded = true;
        if (z) {
            this.contactsLoaded = true;
        }
        if (!this.delayedContactsUpdate.isEmpty() && this.contactsLoaded) {
            applyContactsUpdates(this.delayedContactsUpdate, (ConcurrentHashMap<Integer, TLRPC$User>) null, (ArrayList<TLRPC$TL_contact>) null, (ArrayList<Integer>) null);
            this.delayedContactsUpdate.clear();
        }
        AndroidUtilities.runOnUIThread(new Runnable(hashMap3, arrayList, hashMap4) {
            public final /* synthetic */ HashMap f$1;
            public final /* synthetic */ ArrayList f$2;
            public final /* synthetic */ HashMap f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                ContactsController.this.lambda$null$20$ContactsController(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$20 */
    public /* synthetic */ void lambda$null$20$ContactsController(HashMap hashMap, ArrayList arrayList, HashMap hashMap2) {
        lambda$null$22(hashMap, arrayList, hashMap2);
        updateUnregisteredContacts();
        getNotificationCenter().postNotificationName(NotificationCenter.contactsDidLoad, new Object[0]);
        getNotificationCenter().postNotificationName(NotificationCenter.contactsImported, new Object[0]);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$23 */
    public /* synthetic */ void lambda$null$23$ContactsController(HashMap hashMap, HashMap hashMap2, boolean z, HashMap hashMap3, ArrayList arrayList, HashMap hashMap4) {
        this.contactsBookSPhones = hashMap;
        this.contactsBook = hashMap2;
        this.contactsSyncInProgress = false;
        this.contactsBookLoaded = true;
        if (z) {
            this.contactsLoaded = true;
        }
        if (!this.delayedContactsUpdate.isEmpty() && this.contactsLoaded && this.contactsBookLoaded) {
            applyContactsUpdates(this.delayedContactsUpdate, (ConcurrentHashMap<Integer, TLRPC$User>) null, (ArrayList<TLRPC$TL_contact>) null, (ArrayList<Integer>) null);
            this.delayedContactsUpdate.clear();
        }
        AndroidUtilities.runOnUIThread(new Runnable(hashMap3, arrayList, hashMap4) {
            public final /* synthetic */ HashMap f$1;
            public final /* synthetic */ ArrayList f$2;
            public final /* synthetic */ HashMap f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                ContactsController.this.lambda$null$22$ContactsController(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public boolean isLoadingContacts() {
        boolean z;
        synchronized (this.loadContactsSync) {
            z = this.loadingContacts;
        }
        return z;
    }

    private int getContactsHash(ArrayList<TLRPC$TL_contact> arrayList) {
        int i;
        long j;
        ArrayList arrayList2 = new ArrayList(arrayList);
        Collections.sort(arrayList2, $$Lambda$ContactsController$mH4xxb5muBm5TmYbFkAFgSTQ.INSTANCE);
        int size = arrayList2.size();
        long j2 = 0;
        for (int i2 = -1; i2 < size; i2++) {
            if (i2 == -1) {
                j = (j2 * 20261) + 2147483648L;
                i = getUserConfig().contactsSavedCount;
            } else {
                j = (j2 * 20261) + 2147483648L;
                i = ((TLRPC$TL_contact) arrayList2.get(i2)).user_id;
            }
            j2 = (j + ((long) i)) % 2147483648L;
        }
        return (int) j2;
    }

    static /* synthetic */ int lambda$getContactsHash$25(TLRPC$TL_contact tLRPC$TL_contact, TLRPC$TL_contact tLRPC$TL_contact2) {
        int i = tLRPC$TL_contact.user_id;
        int i2 = tLRPC$TL_contact2.user_id;
        if (i > i2) {
            return 1;
        }
        return i < i2 ? -1 : 0;
    }

    public void loadContacts(boolean z, int i) {
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
        tLRPC$TL_contacts_getContacts.hash = i;
        getConnectionsManager().sendRequest(tLRPC$TL_contacts_getContacts, new RequestDelegate(i) {
            public final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                ContactsController.this.lambda$loadContacts$27$ContactsController(this.f$1, tLObject, tLRPC$TL_error);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$loadContacts$27 */
    public /* synthetic */ void lambda$loadContacts$27$ContactsController(int i, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            TLRPC$contacts_Contacts tLRPC$contacts_Contacts = (TLRPC$contacts_Contacts) tLObject;
            if (i == 0 || !(tLRPC$contacts_Contacts instanceof TLRPC$TL_contacts_contactsNotModified)) {
                getUserConfig().contactsSavedCount = tLRPC$contacts_Contacts.saved_count;
                getUserConfig().saveConfig(false);
                processLoadedContacts(tLRPC$contacts_Contacts.contacts, tLRPC$contacts_Contacts.users, 0);
                return;
            }
            this.contactsLoaded = true;
            if (!this.delayedContactsUpdate.isEmpty() && this.contactsBookLoaded) {
                applyContactsUpdates(this.delayedContactsUpdate, (ConcurrentHashMap<Integer, TLRPC$User>) null, (ArrayList<TLRPC$TL_contact>) null, (ArrayList<Integer>) null);
                this.delayedContactsUpdate.clear();
            }
            getUserConfig().lastContactsSyncTime = (int) (System.currentTimeMillis() / 1000);
            getUserConfig().saveConfig(false);
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    ContactsController.this.lambda$null$26$ContactsController();
                }
            });
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("load contacts don't change");
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$26 */
    public /* synthetic */ void lambda$null$26$ContactsController() {
        synchronized (this.loadContactsSync) {
            this.loadingContacts = false;
        }
        getNotificationCenter().postNotificationName(NotificationCenter.contactsDidLoad, new Object[0]);
    }

    public void processLoadedContacts(ArrayList<TLRPC$TL_contact> arrayList, ArrayList<TLRPC$User> arrayList2, int i) {
        AndroidUtilities.runOnUIThread(new Runnable(arrayList2, i, arrayList) {
            public final /* synthetic */ ArrayList f$1;
            public final /* synthetic */ int f$2;
            public final /* synthetic */ ArrayList f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                ContactsController.this.lambda$processLoadedContacts$35$ContactsController(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$processLoadedContacts$35 */
    public /* synthetic */ void lambda$processLoadedContacts$35$ContactsController(ArrayList arrayList, int i, ArrayList arrayList2) {
        getMessagesController().putUsers(arrayList, i == 1);
        SparseArray sparseArray = new SparseArray();
        boolean isEmpty = arrayList2.isEmpty();
        if (!this.contacts.isEmpty()) {
            int i2 = 0;
            while (i2 < arrayList2.size()) {
                if (this.contactsDict.get(Integer.valueOf(((TLRPC$TL_contact) arrayList2.get(i2)).user_id)) != null) {
                    arrayList2.remove(i2);
                    i2--;
                }
                i2++;
            }
            arrayList2.addAll(this.contacts);
        }
        for (int i3 = 0; i3 < arrayList2.size(); i3++) {
            TLRPC$User user = getMessagesController().getUser(Integer.valueOf(((TLRPC$TL_contact) arrayList2.get(i3)).user_id));
            if (user != null) {
                sparseArray.put(user.id, user);
            }
        }
        Utilities.stageQueue.postRunnable(new Runnable(i, arrayList2, sparseArray, arrayList, isEmpty) {
            public final /* synthetic */ int f$1;
            public final /* synthetic */ ArrayList f$2;
            public final /* synthetic */ SparseArray f$3;
            public final /* synthetic */ ArrayList f$4;
            public final /* synthetic */ boolean f$5;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
            }

            public final void run() {
                ContactsController.this.lambda$null$34$ContactsController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$34 */
    public /* synthetic */ void lambda$null$34$ContactsController(int i, ArrayList arrayList, SparseArray sparseArray, ArrayList arrayList2, boolean z) {
        HashMap hashMap;
        HashMap hashMap2;
        String str;
        int i2 = i;
        ArrayList arrayList3 = arrayList;
        SparseArray sparseArray2 = sparseArray;
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("done loading contacts");
        }
        if (i2 == 1 && (arrayList.isEmpty() || Math.abs((System.currentTimeMillis() / 1000) - ((long) getUserConfig().lastContactsSyncTime)) >= 86400)) {
            loadContacts(false, getContactsHash(arrayList3));
            if (arrayList.isEmpty()) {
                return;
            }
        }
        if (i2 == 0) {
            getUserConfig().lastContactsSyncTime = (int) (System.currentTimeMillis() / 1000);
            getUserConfig().saveConfig(false);
        }
        int i3 = 0;
        while (i3 < arrayList.size()) {
            TLRPC$TL_contact tLRPC$TL_contact = (TLRPC$TL_contact) arrayList3.get(i3);
            if (sparseArray2.get(tLRPC$TL_contact.user_id) != null || tLRPC$TL_contact.user_id == getUserConfig().getClientUserId()) {
                i3++;
            } else {
                loadContacts(false, 0);
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("contacts are broken, load from server");
                    return;
                }
                return;
            }
        }
        if (i2 != 1) {
            getMessagesStorage().putUsersAndChats(arrayList2, (ArrayList<TLRPC$Chat>) null, true, true);
            getMessagesStorage().putContacts(arrayList3, i2 != 2);
        }
        Collections.sort(arrayList3, new Object(sparseArray2) {
            public final /* synthetic */ SparseArray f$0;

            {
                this.f$0 = r1;
            }

            public final int compare(Object obj, Object obj2) {
                return UserObject.getFirstName((TLRPC$User) this.f$0.get(((TLRPC$TL_contact) obj).user_id)).compareTo(UserObject.getFirstName((TLRPC$User) this.f$0.get(((TLRPC$TL_contact) obj2).user_id)));
            }

            public /* synthetic */ Comparator reversed() {
                return Comparator.CC.$default$reversed(this);
            }

            public /* synthetic */ java.util.Comparator thenComparing(Function function) {
                return Comparator.CC.$default$thenComparing((java.util.Comparator) this, function);
            }

            public /* synthetic */ java.util.Comparator thenComparing(Function function, java.util.Comparator comparator) {
                return Comparator.CC.$default$thenComparing(this, function, comparator);
            }

            public /* synthetic */ java.util.Comparator thenComparing(java.util.Comparator comparator) {
                return Comparator.CC.$default$thenComparing((java.util.Comparator) this, comparator);
            }

            public /* synthetic */ java.util.Comparator thenComparingDouble(ToDoubleFunction toDoubleFunction) {
                return Comparator.CC.$default$thenComparingDouble(this, toDoubleFunction);
            }

            public /* synthetic */ java.util.Comparator thenComparingInt(ToIntFunction toIntFunction) {
                return Comparator.CC.$default$thenComparingInt(this, toIntFunction);
            }

            public /* synthetic */ java.util.Comparator thenComparingLong(ToLongFunction toLongFunction) {
                return Comparator.CC.$default$thenComparingLong(this, toLongFunction);
            }
        });
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
        int i4 = 0;
        while (i4 < arrayList.size()) {
            TLRPC$TL_contact tLRPC$TL_contact2 = (TLRPC$TL_contact) arrayList3.get(i4);
            TLRPC$User tLRPC$User = (TLRPC$User) sparseArray2.get(tLRPC$TL_contact2.user_id);
            if (tLRPC$User != null) {
                concurrentHashMap.put(Integer.valueOf(tLRPC$TL_contact2.user_id), tLRPC$TL_contact2);
                if (hashMap2 != null && !TextUtils.isEmpty(tLRPC$User.phone)) {
                    hashMap2.put(tLRPC$User.phone, tLRPC$TL_contact2);
                    String str2 = tLRPC$User.phone;
                    hashMap.put(str2.substring(Math.max(0, str2.length() - 7)), tLRPC$TL_contact2);
                }
                String firstName = UserObject.getFirstName(tLRPC$User);
                if (firstName.length() > 1) {
                    firstName = firstName.substring(0, 1);
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
            i4++;
            arrayList3 = arrayList;
            sparseArray2 = sparseArray;
        }
        Collections.sort(arrayList4, $$Lambda$ContactsController$BV6es6lxe0Ly6fbOJ8V98gKPM4.INSTANCE);
        Collections.sort(arrayList5, $$Lambda$ContactsController$tEO0yd03CNAH5pPgcR0OR27nbTw.INSTANCE);
        AndroidUtilities.runOnUIThread(new Runnable(arrayList, concurrentHashMap, hashMap3, hashMap4, arrayList4, arrayList5, i, z) {
            public final /* synthetic */ ArrayList f$1;
            public final /* synthetic */ ConcurrentHashMap f$2;
            public final /* synthetic */ HashMap f$3;
            public final /* synthetic */ HashMap f$4;
            public final /* synthetic */ ArrayList f$5;
            public final /* synthetic */ ArrayList f$6;
            public final /* synthetic */ int f$7;
            public final /* synthetic */ boolean f$8;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
                this.f$6 = r7;
                this.f$7 = r8;
                this.f$8 = r9;
            }

            public final void run() {
                ContactsController.this.lambda$null$31$ContactsController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8);
            }
        });
        if (!this.delayedContactsUpdate.isEmpty() && this.contactsLoaded && this.contactsBookLoaded) {
            applyContactsUpdates(this.delayedContactsUpdate, (ConcurrentHashMap<Integer, TLRPC$User>) null, (ArrayList<TLRPC$TL_contact>) null, (ArrayList<Integer>) null);
            this.delayedContactsUpdate.clear();
        }
        if (hashMap2 != null) {
            AndroidUtilities.runOnUIThread(new Runnable(hashMap2, hashMap) {
                public final /* synthetic */ HashMap f$1;
                public final /* synthetic */ HashMap f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    ContactsController.this.lambda$null$33$ContactsController(this.f$1, this.f$2);
                }
            });
        } else {
            this.contactsLoaded = true;
        }
    }

    static /* synthetic */ int lambda$null$29(String str, String str2) {
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

    static /* synthetic */ int lambda$null$30(String str, String str2) {
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
    /* renamed from: lambda$null$31 */
    public /* synthetic */ void lambda$null$31$ContactsController(ArrayList arrayList, ConcurrentHashMap concurrentHashMap, HashMap hashMap, HashMap hashMap2, ArrayList arrayList2, ArrayList arrayList3, int i, boolean z) {
        this.contacts = arrayList;
        this.contactsDict = concurrentHashMap;
        this.usersSectionsDict = hashMap;
        this.usersMutualSectionsDict = hashMap2;
        this.sortedUsersSectionsArray = arrayList2;
        this.sortedUsersMutualSectionsArray = arrayList3;
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
    /* renamed from: lambda$null$33 */
    public /* synthetic */ void lambda$null$33$ContactsController(HashMap hashMap, HashMap hashMap2) {
        Utilities.globalQueue.postRunnable(new Runnable(hashMap, hashMap2) {
            public final /* synthetic */ HashMap f$1;
            public final /* synthetic */ HashMap f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                ContactsController.this.lambda$null$32$ContactsController(this.f$1, this.f$2);
            }
        });
        if (!this.contactsSyncInProgress) {
            this.contactsSyncInProgress = true;
            getMessagesStorage().getCachedPhoneBook(false);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$32 */
    public /* synthetic */ void lambda$null$32$ContactsController(HashMap hashMap, HashMap hashMap2) {
        this.contactsByPhone = hashMap;
        this.contactsByShortPhone = hashMap2;
    }

    public boolean isContact(int i) {
        return this.contactsDict.get(Integer.valueOf(i)) != null;
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
    public void lambda$null$22(HashMap<String, ArrayList<Object>> hashMap, ArrayList<String> arrayList, HashMap<String, Contact> hashMap2) {
        Utilities.globalQueue.postRunnable(new Runnable(new ArrayList(this.contacts), hashMap2, hashMap, arrayList) {
            public final /* synthetic */ ArrayList f$1;
            public final /* synthetic */ HashMap f$2;
            public final /* synthetic */ HashMap f$3;
            public final /* synthetic */ ArrayList f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void run() {
                ContactsController.this.lambda$mergePhonebookAndTelegramContacts$39$ContactsController(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$mergePhonebookAndTelegramContacts$39 */
    public /* synthetic */ void lambda$mergePhonebookAndTelegramContacts$39$ContactsController(ArrayList arrayList, HashMap hashMap, HashMap hashMap2, ArrayList arrayList2) {
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            TLRPC$User user = getMessagesController().getUser(Integer.valueOf(((TLRPC$TL_contact) arrayList.get(i)).user_id));
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
            Collections.sort(sort, $$Lambda$ContactsController$8P3stei348KZG2cBlV4DKISbW5M.INSTANCE);
        }
        Collections.sort(arrayList2, $$Lambda$ContactsController$2Yeubi1RasX5hXqD73Nsh8k82LY.INSTANCE);
        AndroidUtilities.runOnUIThread(new Runnable(arrayList2, hashMap2) {
            public final /* synthetic */ ArrayList f$1;
            public final /* synthetic */ HashMap f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                ContactsController.this.lambda$null$38$ContactsController(this.f$1, this.f$2);
            }
        });
    }

    static /* synthetic */ int lambda$null$36(Object obj, Object obj2) {
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

    static /* synthetic */ int lambda$null$37(String str, String str2) {
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
    /* renamed from: lambda$null$38 */
    public /* synthetic */ void lambda$null$38$ContactsController(ArrayList arrayList, HashMap hashMap) {
        this.phoneBookSectionsArray = arrayList;
        this.phoneBookSectionsDict = hashMap;
    }

    private void updateUnregisteredContacts() {
        boolean z;
        HashMap hashMap = new HashMap();
        int size = this.contacts.size();
        for (int i = 0; i < size; i++) {
            TLRPC$TL_contact tLRPC$TL_contact = this.contacts.get(i);
            TLRPC$User user = getMessagesController().getUser(Integer.valueOf(tLRPC$TL_contact.user_id));
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
        Collections.sort(arrayList, $$Lambda$ContactsController$blJ94lXmSDb1Y3assaubI1Ozn4.INSTANCE);
        this.phoneBookContacts = arrayList;
    }

    static /* synthetic */ int lambda$updateUnregisteredContacts$40(Contact contact, Contact contact2) {
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
            Collections.sort(this.contacts, new Object() {
                public final int compare(Object obj, Object obj2) {
                    return ContactsController.this.lambda$buildContactsSectionsArrays$41$ContactsController((TLRPC$TL_contact) obj, (TLRPC$TL_contact) obj2);
                }

                public /* synthetic */ java.util.Comparator reversed() {
                    return Comparator.CC.$default$reversed(this);
                }

                public /* synthetic */ java.util.Comparator thenComparing(Function function) {
                    return Comparator.CC.$default$thenComparing((java.util.Comparator) this, function);
                }

                public /* synthetic */ java.util.Comparator thenComparing(Function function, java.util.Comparator comparator) {
                    return Comparator.CC.$default$thenComparing(this, function, comparator);
                }

                public /* synthetic */ java.util.Comparator thenComparing(java.util.Comparator comparator) {
                    return Comparator.CC.$default$thenComparing((java.util.Comparator) this, comparator);
                }

                public /* synthetic */ java.util.Comparator thenComparingDouble(ToDoubleFunction toDoubleFunction) {
                    return Comparator.CC.$default$thenComparingDouble(this, toDoubleFunction);
                }

                public /* synthetic */ java.util.Comparator thenComparingInt(ToIntFunction toIntFunction) {
                    return Comparator.CC.$default$thenComparingInt(this, toIntFunction);
                }

                public /* synthetic */ java.util.Comparator thenComparingLong(ToLongFunction toLongFunction) {
                    return Comparator.CC.$default$thenComparingLong(this, toLongFunction);
                }
            });
        }
        HashMap<String, ArrayList<TLRPC$TL_contact>> hashMap = new HashMap<>();
        ArrayList<String> arrayList = new ArrayList<>();
        for (int i = 0; i < this.contacts.size(); i++) {
            TLRPC$TL_contact tLRPC$TL_contact = this.contacts.get(i);
            TLRPC$User user = getMessagesController().getUser(Integer.valueOf(tLRPC$TL_contact.user_id));
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
        Collections.sort(arrayList, $$Lambda$ContactsController$QqlAenaErhTcPu5I5AMYLh3HlQ.INSTANCE);
        this.usersSectionsDict = hashMap;
        this.sortedUsersSectionsArray = arrayList;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$buildContactsSectionsArrays$41 */
    public /* synthetic */ int lambda$buildContactsSectionsArrays$41$ContactsController(TLRPC$TL_contact tLRPC$TL_contact, TLRPC$TL_contact tLRPC$TL_contact2) {
        return UserObject.getFirstName(getMessagesController().getUser(Integer.valueOf(tLRPC$TL_contact.user_id))).compareTo(UserObject.getFirstName(getMessagesController().getUser(Integer.valueOf(tLRPC$TL_contact2.user_id))));
    }

    static /* synthetic */ int lambda$buildContactsSectionsArrays$42(String str, String str2) {
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
                if (query != null) {
                    try {
                        query.close();
                    } catch (Exception e2) {
                        FileLog.e((Throwable) e2);
                    }
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
    /* JADX WARNING: Removed duplicated region for block: B:38:0x00b8  */
    /* JADX WARNING: Removed duplicated region for block: B:46:? A[RETURN, SYNTHETIC] */
    /* renamed from: performWriteContactsToPhoneBookInternal */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void lambda$performWriteContactsToPhoneBook$43(java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_contact> r13) {
        /*
            r12 = this;
            java.lang.String r0 = "contacts_updated_v7"
            r1 = 0
            boolean r2 = r12.hasContactsPermission()     // Catch:{ Exception -> 0x00ac }
            if (r2 != 0) goto L_0x000a
            return
        L_0x000a:
            int r2 = r12.currentAccount     // Catch:{ Exception -> 0x00ac }
            android.content.SharedPreferences r2 = org.telegram.messenger.MessagesController.getMainSettings(r2)     // Catch:{ Exception -> 0x00ac }
            r3 = 0
            boolean r4 = r2.getBoolean(r0, r3)     // Catch:{ Exception -> 0x00ac }
            r5 = 1
            r4 = r4 ^ r5
            if (r4 == 0) goto L_0x0024
            android.content.SharedPreferences$Editor r2 = r2.edit()     // Catch:{ Exception -> 0x00ac }
            android.content.SharedPreferences$Editor r0 = r2.putBoolean(r0, r5)     // Catch:{ Exception -> 0x00ac }
            r0.commit()     // Catch:{ Exception -> 0x00ac }
        L_0x0024:
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x00ac }
            android.content.ContentResolver r6 = r0.getContentResolver()     // Catch:{ Exception -> 0x00ac }
            android.net.Uri r0 = android.provider.ContactsContract.RawContacts.CONTENT_URI     // Catch:{ Exception -> 0x00ac }
            android.net.Uri$Builder r0 = r0.buildUpon()     // Catch:{ Exception -> 0x00ac }
            java.lang.String r2 = "account_name"
            android.accounts.Account r7 = r12.systemAccount     // Catch:{ Exception -> 0x00ac }
            java.lang.String r7 = r7.name     // Catch:{ Exception -> 0x00ac }
            android.net.Uri$Builder r0 = r0.appendQueryParameter(r2, r7)     // Catch:{ Exception -> 0x00ac }
            java.lang.String r2 = "account_type"
            android.accounts.Account r7 = r12.systemAccount     // Catch:{ Exception -> 0x00ac }
            java.lang.String r7 = r7.type     // Catch:{ Exception -> 0x00ac }
            android.net.Uri$Builder r0 = r0.appendQueryParameter(r2, r7)     // Catch:{ Exception -> 0x00ac }
            android.net.Uri r7 = r0.build()     // Catch:{ Exception -> 0x00ac }
            r0 = 2
            java.lang.String[] r8 = new java.lang.String[r0]     // Catch:{ Exception -> 0x00ac }
            java.lang.String r0 = "_id"
            r8[r3] = r0     // Catch:{ Exception -> 0x00ac }
            java.lang.String r0 = "sync2"
            r8[r5] = r0     // Catch:{ Exception -> 0x00ac }
            r9 = 0
            r10 = 0
            r11 = 0
            android.database.Cursor r0 = r6.query(r7, r8, r9, r10, r11)     // Catch:{ Exception -> 0x00ac }
            org.telegram.messenger.support.SparseLongArray r2 = new org.telegram.messenger.support.SparseLongArray     // Catch:{ Exception -> 0x00a7, all -> 0x00a4 }
            r2.<init>()     // Catch:{ Exception -> 0x00a7, all -> 0x00a4 }
            if (r0 == 0) goto L_0x00a0
        L_0x0061:
            boolean r6 = r0.moveToNext()     // Catch:{ Exception -> 0x00a7, all -> 0x00a4 }
            if (r6 == 0) goto L_0x0073
            int r6 = r0.getInt(r5)     // Catch:{ Exception -> 0x00a7, all -> 0x00a4 }
            long r7 = r0.getLong(r3)     // Catch:{ Exception -> 0x00a7, all -> 0x00a4 }
            r2.put(r6, r7)     // Catch:{ Exception -> 0x00a7, all -> 0x00a4 }
            goto L_0x0061
        L_0x0073:
            r0.close()     // Catch:{ Exception -> 0x00a7, all -> 0x00a4 }
        L_0x0076:
            int r0 = r13.size()     // Catch:{ Exception -> 0x00ac }
            if (r3 >= r0) goto L_0x00a1
            java.lang.Object r0 = r13.get(r3)     // Catch:{ Exception -> 0x00ac }
            org.telegram.tgnet.TLRPC$TL_contact r0 = (org.telegram.tgnet.TLRPC$TL_contact) r0     // Catch:{ Exception -> 0x00ac }
            if (r4 != 0) goto L_0x008c
            int r5 = r0.user_id     // Catch:{ Exception -> 0x00ac }
            int r5 = r2.indexOfKey(r5)     // Catch:{ Exception -> 0x00ac }
            if (r5 >= 0) goto L_0x009d
        L_0x008c:
            org.telegram.messenger.MessagesController r5 = r12.getMessagesController()     // Catch:{ Exception -> 0x00ac }
            int r0 = r0.user_id     // Catch:{ Exception -> 0x00ac }
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)     // Catch:{ Exception -> 0x00ac }
            org.telegram.tgnet.TLRPC$User r0 = r5.getUser(r0)     // Catch:{ Exception -> 0x00ac }
            r12.addContactToPhoneBook(r0, r4)     // Catch:{ Exception -> 0x00ac }
        L_0x009d:
            int r3 = r3 + 1
            goto L_0x0076
        L_0x00a0:
            r1 = r0
        L_0x00a1:
            if (r1 == 0) goto L_0x00b5
            goto L_0x00b2
        L_0x00a4:
            r13 = move-exception
            r1 = r0
            goto L_0x00b6
        L_0x00a7:
            r13 = move-exception
            r1 = r0
            goto L_0x00ad
        L_0x00aa:
            r13 = move-exception
            goto L_0x00b6
        L_0x00ac:
            r13 = move-exception
        L_0x00ad:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r13)     // Catch:{ all -> 0x00aa }
            if (r1 == 0) goto L_0x00b5
        L_0x00b2:
            r1.close()
        L_0x00b5:
            return
        L_0x00b6:
            if (r1 == 0) goto L_0x00bb
            r1.close()
        L_0x00bb:
            goto L_0x00bd
        L_0x00bc:
            throw r13
        L_0x00bd:
            goto L_0x00bc
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ContactsController.lambda$performWriteContactsToPhoneBook$43(java.util.ArrayList):void");
    }

    private void performWriteContactsToPhoneBook() {
        Utilities.phoneBookQueue.postRunnable(new Runnable(new ArrayList(this.contacts)) {
            public final /* synthetic */ ArrayList f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                ContactsController.this.lambda$performWriteContactsToPhoneBook$43$ContactsController(this.f$1);
            }
        });
    }

    private void applyContactsUpdates(ArrayList<Integer> arrayList, ConcurrentHashMap<Integer, TLRPC$User> concurrentHashMap, ArrayList<TLRPC$TL_contact> arrayList2, ArrayList<Integer> arrayList3) {
        int indexOf;
        int indexOf2;
        if (arrayList2 == null || arrayList3 == null) {
            arrayList2 = new ArrayList<>();
            arrayList3 = new ArrayList<>();
            for (int i = 0; i < arrayList.size(); i++) {
                Integer num = arrayList.get(i);
                if (num.intValue() > 0) {
                    TLRPC$TL_contact tLRPC$TL_contact = new TLRPC$TL_contact();
                    tLRPC$TL_contact.user_id = num.intValue();
                    arrayList2.add(tLRPC$TL_contact);
                } else if (num.intValue() < 0) {
                    arrayList3.add(Integer.valueOf(-num.intValue()));
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
                tLRPC$User = (TLRPC$User) concurrentHashMap.get(Integer.valueOf(tLRPC$TL_contact2.user_id));
            }
            if (tLRPC$User == null) {
                tLRPC$User = getMessagesController().getUser(Integer.valueOf(tLRPC$TL_contact2.user_id));
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
            Integer num2 = arrayList3.get(i3);
            Utilities.phoneBookQueue.postRunnable(new Runnable(num2) {
                public final /* synthetic */ Integer f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    ContactsController.this.lambda$applyContactsUpdates$44$ContactsController(this.f$1);
                }
            });
            TLRPC$User tLRPC$User2 = concurrentHashMap != null ? (TLRPC$User) concurrentHashMap.get(num2) : null;
            if (tLRPC$User2 == null) {
                tLRPC$User2 = getMessagesController().getUser(num2);
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
            Utilities.stageQueue.postRunnable(new Runnable() {
                public final void run() {
                    ContactsController.this.lambda$applyContactsUpdates$45$ContactsController();
                }
            });
        } else {
            AndroidUtilities.runOnUIThread(new Runnable(arrayList2, arrayList3) {
                public final /* synthetic */ ArrayList f$1;
                public final /* synthetic */ ArrayList f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    ContactsController.this.lambda$applyContactsUpdates$46$ContactsController(this.f$1, this.f$2);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$applyContactsUpdates$44 */
    public /* synthetic */ void lambda$applyContactsUpdates$44$ContactsController(Integer num) {
        deleteContactFromPhoneBook(num.intValue());
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$applyContactsUpdates$45 */
    public /* synthetic */ void lambda$applyContactsUpdates$45$ContactsController() {
        loadContacts(false, 0);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$applyContactsUpdates$46 */
    public /* synthetic */ void lambda$applyContactsUpdates$46$ContactsController(ArrayList arrayList, ArrayList arrayList2) {
        for (int i = 0; i < arrayList.size(); i++) {
            TLRPC$TL_contact tLRPC$TL_contact = (TLRPC$TL_contact) arrayList.get(i);
            if (this.contactsDict.get(Integer.valueOf(tLRPC$TL_contact.user_id)) == null) {
                this.contacts.add(tLRPC$TL_contact);
                this.contactsDict.put(Integer.valueOf(tLRPC$TL_contact.user_id), tLRPC$TL_contact);
            }
        }
        for (int i2 = 0; i2 < arrayList2.size(); i2++) {
            Integer num = (Integer) arrayList2.get(i2);
            TLRPC$TL_contact tLRPC$TL_contact2 = (TLRPC$TL_contact) this.contactsDict.get(num);
            if (tLRPC$TL_contact2 != null) {
                this.contacts.remove(tLRPC$TL_contact2);
                this.contactsDict.remove(num);
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

    public void processContactsUpdates(ArrayList<Integer> arrayList, ConcurrentHashMap<Integer, TLRPC$User> concurrentHashMap) {
        int indexOf;
        int indexOf2;
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        Iterator<Integer> it = arrayList.iterator();
        while (it.hasNext()) {
            Integer next = it.next();
            if (next.intValue() > 0) {
                TLRPC$TL_contact tLRPC$TL_contact = new TLRPC$TL_contact();
                tLRPC$TL_contact.user_id = next.intValue();
                arrayList2.add(tLRPC$TL_contact);
                if (!this.delayedContactsUpdate.isEmpty() && (indexOf2 = this.delayedContactsUpdate.indexOf(Integer.valueOf(-next.intValue()))) != -1) {
                    this.delayedContactsUpdate.remove(indexOf2);
                }
            } else if (next.intValue() < 0) {
                arrayList3.add(Integer.valueOf(-next.intValue()));
                if (!this.delayedContactsUpdate.isEmpty() && (indexOf = this.delayedContactsUpdate.indexOf(Integer.valueOf(-next.intValue()))) != -1) {
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
        newInsert.withValue("sync2", Integer.valueOf(tLRPC$User.id));
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
        newInsert3.withValue("data1", Integer.valueOf(tLRPC$User.id));
        newInsert3.withValue("data2", "Telegram Profile");
        newInsert3.withValue("data3", LocaleController.formatString("ContactShortcutMessage", NUM, str));
        newInsert3.withValue("data4", Integer.valueOf(tLRPC$User.id));
        arrayList.add(newInsert3.build());
        ContentProviderOperation.Builder newInsert4 = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI);
        newInsert4.withValueBackReference("raw_contact_id", 0);
        newInsert4.withValue("mimetype", "vnd.android.cursor.item/vnd.org.telegram.messenger.android.call");
        newInsert4.withValue("data1", Integer.valueOf(tLRPC$User.id));
        newInsert4.withValue("data2", "Telegram Voice Call");
        newInsert4.withValue("data3", LocaleController.formatString("ContactShortcutVoiceCall", NUM, str));
        newInsert4.withValue("data4", Integer.valueOf(tLRPC$User.id));
        arrayList.add(newInsert4.build());
        ContentProviderOperation.Builder newInsert5 = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI);
        newInsert5.withValueBackReference("raw_contact_id", 0);
        newInsert5.withValue("mimetype", "vnd.android.cursor.item/vnd.org.telegram.messenger.android.call.video");
        newInsert5.withValue("data1", Integer.valueOf(tLRPC$User.id));
        newInsert5.withValue("data2", "Telegram Video Call");
        newInsert5.withValue("data3", LocaleController.formatString("ContactShortcutVideoCall", NUM, str));
        newInsert5.withValue("data4", Integer.valueOf(tLRPC$User.id));
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

    private void deleteContactFromPhoneBook(int i) {
        if (hasContactsPermission()) {
            synchronized (this.observerLock) {
                this.ignoreChanges = true;
            }
            try {
                ContentResolver contentResolver = ApplicationLoader.applicationContext.getContentResolver();
                Uri build = ContactsContract.RawContacts.CONTENT_URI.buildUpon().appendQueryParameter("caller_is_syncadapter", "true").appendQueryParameter("account_name", this.systemAccount.name).appendQueryParameter("account_type", this.systemAccount.type).build();
                contentResolver.delete(build, "sync2 = " + i, (String[]) null);
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
            Utilities.phoneBookQueue.postRunnable(new Runnable(str) {
                public final /* synthetic */ String f$0;

                {
                    this.f$0 = r1;
                }

                public final void run() {
                    ContactsController.lambda$markAsContacted$47(this.f$0);
                }
            });
        }
    }

    static /* synthetic */ void lambda$markAsContacted$47(String str) {
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
            getConnectionsManager().sendRequest(tLRPC$TL_contacts_addContact, new RequestDelegate(tLRPC$User) {
                public final /* synthetic */ TLRPC$User f$1;

                {
                    this.f$1 = r2;
                }

                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    ContactsController.this.lambda$addContact$50$ContactsController(this.f$1, tLObject, tLRPC$TL_error);
                }
            }, 6);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$addContact$50 */
    public /* synthetic */ void lambda$addContact$50$ContactsController(TLRPC$User tLRPC$User, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        int indexOf;
        if (tLRPC$TL_error == null) {
            TLRPC$Updates tLRPC$Updates = (TLRPC$Updates) tLObject;
            getMessagesController().processUpdates(tLRPC$Updates, false);
            for (int i = 0; i < tLRPC$Updates.users.size(); i++) {
                TLRPC$User tLRPC$User2 = tLRPC$Updates.users.get(i);
                if (tLRPC$User2.id == tLRPC$User.id) {
                    Utilities.phoneBookQueue.postRunnable(new Runnable(tLRPC$User2) {
                        public final /* synthetic */ TLRPC$User f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run() {
                            ContactsController.this.lambda$null$48$ContactsController(this.f$1);
                        }
                    });
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
            AndroidUtilities.runOnUIThread(new Runnable(tLRPC$Updates) {
                public final /* synthetic */ TLRPC$Updates f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    ContactsController.this.lambda$null$49$ContactsController(this.f$1);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$48 */
    public /* synthetic */ void lambda$null$48$ContactsController(TLRPC$User tLRPC$User) {
        addContactToPhoneBook(tLRPC$User, true);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$49 */
    public /* synthetic */ void lambda$null$49$ContactsController(TLRPC$Updates tLRPC$Updates) {
        for (int i = 0; i < tLRPC$Updates.users.size(); i++) {
            TLRPC$User tLRPC$User = tLRPC$Updates.users.get(i);
            if (tLRPC$User.contact && this.contactsDict.get(Integer.valueOf(tLRPC$User.id)) == null) {
                TLRPC$TL_contact tLRPC$TL_contact = new TLRPC$TL_contact();
                tLRPC$TL_contact.user_id = tLRPC$User.id;
                this.contacts.add(tLRPC$TL_contact);
                this.contactsDict.put(Integer.valueOf(tLRPC$TL_contact.user_id), tLRPC$TL_contact);
            }
        }
        buildContactsSectionsArrays(true);
        getNotificationCenter().postNotificationName(NotificationCenter.contactsDidLoad, new Object[0]);
    }

    public void deleteContact(ArrayList<TLRPC$User> arrayList, boolean z) {
        if (arrayList != null && !arrayList.isEmpty()) {
            TLRPC$TL_contacts_deleteContacts tLRPC$TL_contacts_deleteContacts = new TLRPC$TL_contacts_deleteContacts();
            ArrayList arrayList2 = new ArrayList();
            Iterator<TLRPC$User> it = arrayList.iterator();
            while (it.hasNext()) {
                TLRPC$User next = it.next();
                TLRPC$InputUser inputUser = getMessagesController().getInputUser(next);
                if (inputUser != null) {
                    next.contact = false;
                    arrayList2.add(Integer.valueOf(next.id));
                    tLRPC$TL_contacts_deleteContacts.id.add(inputUser);
                }
            }
            getConnectionsManager().sendRequest(tLRPC$TL_contacts_deleteContacts, new RequestDelegate(arrayList2, arrayList, z, arrayList.get(0).first_name) {
                public final /* synthetic */ ArrayList f$1;
                public final /* synthetic */ ArrayList f$2;
                public final /* synthetic */ boolean f$3;
                public final /* synthetic */ String f$4;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                }

                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    ContactsController.this.lambda$deleteContact$53$ContactsController(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tLRPC$TL_error);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$deleteContact$53 */
    public /* synthetic */ void lambda$deleteContact$53$ContactsController(ArrayList arrayList, ArrayList arrayList2, boolean z, String str, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        int indexOf;
        if (tLRPC$TL_error == null) {
            getMessagesController().processUpdates((TLRPC$Updates) tLObject, false);
            getMessagesStorage().deleteContacts(arrayList);
            Utilities.phoneBookQueue.postRunnable(new Runnable(arrayList2) {
                public final /* synthetic */ ArrayList f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    ContactsController.this.lambda$null$51$ContactsController(this.f$1);
                }
            });
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
            AndroidUtilities.runOnUIThread(new Runnable(arrayList2, z, str) {
                public final /* synthetic */ ArrayList f$1;
                public final /* synthetic */ boolean f$2;
                public final /* synthetic */ String f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    ContactsController.this.lambda$null$52$ContactsController(this.f$1, this.f$2, this.f$3);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$51 */
    public /* synthetic */ void lambda$null$51$ContactsController(ArrayList arrayList) {
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            deleteContactFromPhoneBook(((TLRPC$User) it.next()).id);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$52 */
    public /* synthetic */ void lambda$null$52$ContactsController(ArrayList arrayList, boolean z, String str) {
        Iterator it = arrayList.iterator();
        boolean z2 = false;
        while (it.hasNext()) {
            TLRPC$User tLRPC$User = (TLRPC$User) it.next();
            TLRPC$TL_contact tLRPC$TL_contact = (TLRPC$TL_contact) this.contactsDict.get(Integer.valueOf(tLRPC$User.id));
            if (tLRPC$TL_contact != null) {
                this.contacts.remove(tLRPC$TL_contact);
                this.contactsDict.remove(Integer.valueOf(tLRPC$User.id));
                z2 = true;
            }
        }
        if (z2) {
            buildContactsSectionsArrays(false);
        }
        getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, 1);
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
        getConnectionsManager().sendRequest(new TLRPC$TL_contacts_getStatuses(), new RequestDelegate(edit) {
            public final /* synthetic */ SharedPreferences.Editor f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                ContactsController.this.lambda$reloadContactsStatuses$55$ContactsController(this.f$1, tLObject, tLRPC$TL_error);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$reloadContactsStatuses$55 */
    public /* synthetic */ void lambda$reloadContactsStatuses$55$ContactsController(SharedPreferences.Editor editor, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            AndroidUtilities.runOnUIThread(new Runnable(editor, tLObject) {
                public final /* synthetic */ SharedPreferences.Editor f$1;
                public final /* synthetic */ TLObject f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    ContactsController.this.lambda$null$54$ContactsController(this.f$1, this.f$2);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$54 */
    public /* synthetic */ void lambda$null$54$ContactsController(SharedPreferences.Editor editor, TLObject tLObject) {
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
                    TLRPC$User user = getMessagesController().getUser(Integer.valueOf(tLRPC$TL_contactStatus.user_id));
                    if (user != null) {
                        user.status = tLRPC$TL_contactStatus.status;
                    }
                    tLRPC$TL_user.status = tLRPC$TL_contactStatus.status;
                    arrayList.add(tLRPC$TL_user);
                }
            }
            getMessagesStorage().updateUsers(arrayList, true, true, true);
        }
        getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, 4);
    }

    public void loadPrivacySettings() {
        if (this.loadingDeleteInfo == 0) {
            this.loadingDeleteInfo = 1;
            getConnectionsManager().sendRequest(new TLRPC$TL_account_getAccountTTL(), new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    ContactsController.this.lambda$loadPrivacySettings$57$ContactsController(tLObject, tLRPC$TL_error);
                }
            });
        }
        if (this.loadingGlobalSettings == 0) {
            this.loadingGlobalSettings = 1;
            getConnectionsManager().sendRequest(new TLRPC$TL_account_getGlobalPrivacySettings(), new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    ContactsController.this.lambda$loadPrivacySettings$59$ContactsController(tLObject, tLRPC$TL_error);
                }
            });
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
                    getConnectionsManager().sendRequest(tLRPC$TL_account_getPrivacy, new RequestDelegate(i) {
                        public final /* synthetic */ int f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                            ContactsController.this.lambda$loadPrivacySettings$61$ContactsController(this.f$1, tLObject, tLRPC$TL_error);
                        }
                    });
                }
                i++;
            } else {
                getNotificationCenter().postNotificationName(NotificationCenter.privacyRulesUpdated, new Object[0]);
                return;
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$loadPrivacySettings$57 */
    public /* synthetic */ void lambda$loadPrivacySettings$57$ContactsController(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, tLObject) {
            public final /* synthetic */ TLRPC$TL_error f$1;
            public final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                ContactsController.this.lambda$null$56$ContactsController(this.f$1, this.f$2);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$56 */
    public /* synthetic */ void lambda$null$56$ContactsController(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
        if (tLRPC$TL_error == null) {
            this.deleteAccountTTL = ((TLRPC$TL_accountDaysTTL) tLObject).days;
            this.loadingDeleteInfo = 2;
        } else {
            this.loadingDeleteInfo = 0;
        }
        getNotificationCenter().postNotificationName(NotificationCenter.privacyRulesUpdated, new Object[0]);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$loadPrivacySettings$59 */
    public /* synthetic */ void lambda$loadPrivacySettings$59$ContactsController(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, tLObject) {
            public final /* synthetic */ TLRPC$TL_error f$1;
            public final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                ContactsController.this.lambda$null$58$ContactsController(this.f$1, this.f$2);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$58 */
    public /* synthetic */ void lambda$null$58$ContactsController(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
        if (tLRPC$TL_error == null) {
            this.globalPrivacySettings = (TLRPC$TL_globalPrivacySettings) tLObject;
            this.loadingGlobalSettings = 2;
        } else {
            this.loadingGlobalSettings = 0;
        }
        getNotificationCenter().postNotificationName(NotificationCenter.privacyRulesUpdated, new Object[0]);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$loadPrivacySettings$61 */
    public /* synthetic */ void lambda$loadPrivacySettings$61$ContactsController(int i, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, tLObject, i) {
            public final /* synthetic */ TLRPC$TL_error f$1;
            public final /* synthetic */ TLObject f$2;
            public final /* synthetic */ int f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                ContactsController.this.lambda$null$60$ContactsController(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$60 */
    public /* synthetic */ void lambda$null$60$ContactsController(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, int i) {
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

    /* JADX WARNING: Removed duplicated region for block: B:13:0x00ce A[Catch:{ Exception -> 0x027f }] */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x011d A[SYNTHETIC, Splitter:B:17:0x011d] */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x0274 A[Catch:{ Exception -> 0x027f }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void createOrUpdateConnectionServiceContact(int r25, java.lang.String r26, java.lang.String r27) {
        /*
            r24 = this;
            r1 = r24
            r0 = r25
            r2 = r26
            r3 = r27
            java.lang.String r4 = "raw_contact_id=? AND mimetype=?"
            java.lang.String r5 = "vnd.android.cursor.item/group_membership"
            java.lang.String r6 = "TelegramConnectionService"
            java.lang.String r7 = "true"
            java.lang.String r8 = "caller_is_syncadapter"
            java.lang.String r9 = "mimetype"
            java.lang.String r10 = ""
            java.lang.String r11 = "raw_contact_id"
            boolean r12 = r24.hasContactsPermission()
            if (r12 != 0) goto L_0x0021
            return
        L_0x0021:
            android.content.Context r12 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x027f }
            android.content.ContentResolver r12 = r12.getContentResolver()     // Catch:{ Exception -> 0x027f }
            java.util.ArrayList r15 = new java.util.ArrayList     // Catch:{ Exception -> 0x027f }
            r15.<init>()     // Catch:{ Exception -> 0x027f }
            android.net.Uri r13 = android.provider.ContactsContract.Groups.CONTENT_URI     // Catch:{ Exception -> 0x027f }
            android.net.Uri$Builder r13 = r13.buildUpon()     // Catch:{ Exception -> 0x027f }
            android.net.Uri$Builder r13 = r13.appendQueryParameter(r8, r7)     // Catch:{ Exception -> 0x027f }
            android.net.Uri r14 = r13.build()     // Catch:{ Exception -> 0x027f }
            android.net.Uri r13 = android.provider.ContactsContract.RawContacts.CONTENT_URI     // Catch:{ Exception -> 0x027f }
            android.net.Uri$Builder r13 = r13.buildUpon()     // Catch:{ Exception -> 0x027f }
            android.net.Uri$Builder r7 = r13.appendQueryParameter(r8, r7)     // Catch:{ Exception -> 0x027f }
            android.net.Uri r7 = r7.build()     // Catch:{ Exception -> 0x027f }
            r8 = 1
            java.lang.String[] r13 = new java.lang.String[r8]     // Catch:{ Exception -> 0x027f }
            java.lang.String r16 = "_id"
            r8 = 0
            r13[r8] = r16     // Catch:{ Exception -> 0x027f }
            java.lang.String r16 = "title=? AND account_type=? AND account_name=?"
            r8 = 3
            r19 = r9
            java.lang.String[] r9 = new java.lang.String[r8]     // Catch:{ Exception -> 0x027f }
            r17 = 0
            r9[r17] = r6     // Catch:{ Exception -> 0x027f }
            android.accounts.Account r8 = r1.systemAccount     // Catch:{ Exception -> 0x027f }
            r17 = r13
            java.lang.String r13 = r8.type     // Catch:{ Exception -> 0x027f }
            r18 = 1
            r9[r18] = r13     // Catch:{ Exception -> 0x027f }
            java.lang.String r8 = r8.name     // Catch:{ Exception -> 0x027f }
            r13 = 2
            r9[r13] = r8     // Catch:{ Exception -> 0x027f }
            r18 = 0
            r8 = r17
            r3 = 2
            r13 = r12
            r20 = r14
            r21 = r15
            r15 = r8
            r17 = r9
            android.database.Cursor r8 = r13.query(r14, r15, r16, r17, r18)     // Catch:{ Exception -> 0x027f }
            java.lang.String r9 = "account_name"
            java.lang.String r15 = "account_type"
            if (r8 == 0) goto L_0x008f
            boolean r13 = r8.moveToFirst()     // Catch:{ Exception -> 0x027f }
            if (r13 == 0) goto L_0x008f
            r13 = 0
            int r6 = r8.getInt(r13)     // Catch:{ Exception -> 0x027f }
            r16 = r15
            goto L_0x00cc
        L_0x008f:
            android.content.ContentValues r13 = new android.content.ContentValues     // Catch:{ Exception -> 0x027f }
            r13.<init>()     // Catch:{ Exception -> 0x027f }
            android.accounts.Account r14 = r1.systemAccount     // Catch:{ Exception -> 0x027f }
            java.lang.String r14 = r14.type     // Catch:{ Exception -> 0x027f }
            r13.put(r15, r14)     // Catch:{ Exception -> 0x027f }
            android.accounts.Account r14 = r1.systemAccount     // Catch:{ Exception -> 0x027f }
            java.lang.String r14 = r14.name     // Catch:{ Exception -> 0x027f }
            r13.put(r9, r14)     // Catch:{ Exception -> 0x027f }
            java.lang.String r14 = "group_visible"
            r16 = 0
            java.lang.Integer r3 = java.lang.Integer.valueOf(r16)     // Catch:{ Exception -> 0x027f }
            r13.put(r14, r3)     // Catch:{ Exception -> 0x027f }
            java.lang.String r3 = "group_is_read_only"
            r16 = r15
            r14 = 1
            java.lang.Integer r15 = java.lang.Integer.valueOf(r14)     // Catch:{ Exception -> 0x027f }
            r13.put(r3, r15)     // Catch:{ Exception -> 0x027f }
            java.lang.String r3 = "title"
            r13.put(r3, r6)     // Catch:{ Exception -> 0x027f }
            r3 = r20
            android.net.Uri r3 = r12.insert(r3, r13)     // Catch:{ Exception -> 0x027f }
            java.lang.String r3 = r3.getLastPathSegment()     // Catch:{ Exception -> 0x027f }
            int r6 = java.lang.Integer.parseInt(r3)     // Catch:{ Exception -> 0x027f }
        L_0x00cc:
            if (r8 == 0) goto L_0x00d1
            r8.close()     // Catch:{ Exception -> 0x027f }
        L_0x00d1:
            android.net.Uri r14 = android.provider.ContactsContract.Data.CONTENT_URI     // Catch:{ Exception -> 0x027f }
            r3 = 1
            java.lang.String[] r15 = new java.lang.String[r3]     // Catch:{ Exception -> 0x027f }
            r3 = 0
            r15[r3] = r11     // Catch:{ Exception -> 0x027f }
            java.lang.String r8 = "mimetype=? AND data1=?"
            r13 = 2
            java.lang.String[] r3 = new java.lang.String[r13]     // Catch:{ Exception -> 0x027f }
            r13 = 0
            r3[r13] = r5     // Catch:{ Exception -> 0x027f }
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x027f }
            r13.<init>()     // Catch:{ Exception -> 0x027f }
            r13.append(r6)     // Catch:{ Exception -> 0x027f }
            r13.append(r10)     // Catch:{ Exception -> 0x027f }
            java.lang.String r13 = r13.toString()     // Catch:{ Exception -> 0x027f }
            r17 = 1
            r3[r17] = r13     // Catch:{ Exception -> 0x027f }
            r18 = 0
            r13 = r12
            r20 = r12
            r12 = r16
            r16 = r8
            r17 = r3
            android.database.Cursor r3 = r13.query(r14, r15, r16, r17, r18)     // Catch:{ Exception -> 0x027f }
            int r8 = r21.size()     // Catch:{ Exception -> 0x027f }
            java.lang.String r13 = "+99084"
            java.lang.String r14 = "vnd.android.cursor.item/phone_v2"
            java.lang.String r15 = "data3"
            r16 = r6
            java.lang.String r6 = "data2"
            r17 = r5
            java.lang.String r5 = "vnd.android.cursor.item/name"
            r18 = r8
            java.lang.String r8 = "data1"
            if (r3 == 0) goto L_0x01cf
            boolean r22 = r3.moveToFirst()     // Catch:{ Exception -> 0x027f }
            if (r22 == 0) goto L_0x01cf
            r11 = 0
            int r9 = r3.getInt(r11)     // Catch:{ Exception -> 0x027f }
            android.content.ContentProviderOperation$Builder r7 = android.content.ContentProviderOperation.newUpdate(r7)     // Catch:{ Exception -> 0x027f }
            java.lang.String r11 = "_id=?"
            r23 = r3
            r12 = 1
            java.lang.String[] r3 = new java.lang.String[r12]     // Catch:{ Exception -> 0x027f }
            java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x027f }
            r12.<init>()     // Catch:{ Exception -> 0x027f }
            r12.append(r9)     // Catch:{ Exception -> 0x027f }
            r12.append(r10)     // Catch:{ Exception -> 0x027f }
            java.lang.String r12 = r12.toString()     // Catch:{ Exception -> 0x027f }
            r16 = 0
            r3[r16] = r12     // Catch:{ Exception -> 0x027f }
            android.content.ContentProviderOperation$Builder r3 = r7.withSelection(r11, r3)     // Catch:{ Exception -> 0x027f }
            java.lang.String r7 = "deleted"
            java.lang.Integer r11 = java.lang.Integer.valueOf(r16)     // Catch:{ Exception -> 0x027f }
            android.content.ContentProviderOperation$Builder r3 = r3.withValue(r7, r11)     // Catch:{ Exception -> 0x027f }
            android.content.ContentProviderOperation r3 = r3.build()     // Catch:{ Exception -> 0x027f }
            r11 = r21
            r11.add(r3)     // Catch:{ Exception -> 0x027f }
            android.net.Uri r3 = android.provider.ContactsContract.Data.CONTENT_URI     // Catch:{ Exception -> 0x027f }
            android.content.ContentProviderOperation$Builder r3 = android.content.ContentProviderOperation.newUpdate(r3)     // Catch:{ Exception -> 0x027f }
            r7 = 2
            java.lang.String[] r12 = new java.lang.String[r7]     // Catch:{ Exception -> 0x027f }
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x027f }
            r7.<init>()     // Catch:{ Exception -> 0x027f }
            r7.append(r9)     // Catch:{ Exception -> 0x027f }
            r7.append(r10)     // Catch:{ Exception -> 0x027f }
            java.lang.String r7 = r7.toString()     // Catch:{ Exception -> 0x027f }
            r16 = 0
            r12[r16] = r7     // Catch:{ Exception -> 0x027f }
            r7 = 1
            r12[r7] = r14     // Catch:{ Exception -> 0x027f }
            android.content.ContentProviderOperation$Builder r3 = r3.withSelection(r4, r12)     // Catch:{ Exception -> 0x027f }
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x027f }
            r7.<init>()     // Catch:{ Exception -> 0x027f }
            r7.append(r13)     // Catch:{ Exception -> 0x027f }
            r7.append(r0)     // Catch:{ Exception -> 0x027f }
            java.lang.String r0 = r7.toString()     // Catch:{ Exception -> 0x027f }
            android.content.ContentProviderOperation$Builder r0 = r3.withValue(r8, r0)     // Catch:{ Exception -> 0x027f }
            android.content.ContentProviderOperation r0 = r0.build()     // Catch:{ Exception -> 0x027f }
            r11.add(r0)     // Catch:{ Exception -> 0x027f }
            android.net.Uri r0 = android.provider.ContactsContract.Data.CONTENT_URI     // Catch:{ Exception -> 0x027f }
            android.content.ContentProviderOperation$Builder r0 = android.content.ContentProviderOperation.newUpdate(r0)     // Catch:{ Exception -> 0x027f }
            r3 = 2
            java.lang.String[] r3 = new java.lang.String[r3]     // Catch:{ Exception -> 0x027f }
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x027f }
            r7.<init>()     // Catch:{ Exception -> 0x027f }
            r7.append(r9)     // Catch:{ Exception -> 0x027f }
            r7.append(r10)     // Catch:{ Exception -> 0x027f }
            java.lang.String r7 = r7.toString()     // Catch:{ Exception -> 0x027f }
            r8 = 0
            r3[r8] = r7     // Catch:{ Exception -> 0x027f }
            r7 = 1
            r3[r7] = r5     // Catch:{ Exception -> 0x027f }
            android.content.ContentProviderOperation$Builder r0 = r0.withSelection(r4, r3)     // Catch:{ Exception -> 0x027f }
            android.content.ContentProviderOperation$Builder r0 = r0.withValue(r6, r2)     // Catch:{ Exception -> 0x027f }
            r3 = r27
            android.content.ContentProviderOperation$Builder r0 = r0.withValue(r15, r3)     // Catch:{ Exception -> 0x027f }
            android.content.ContentProviderOperation r0 = r0.build()     // Catch:{ Exception -> 0x027f }
            r11.add(r0)     // Catch:{ Exception -> 0x027f }
            goto L_0x0272
        L_0x01cf:
            r23 = r3
            r22 = r11
            r11 = r21
            r3 = r27
            android.content.ContentProviderOperation$Builder r4 = android.content.ContentProviderOperation.newInsert(r7)     // Catch:{ Exception -> 0x027f }
            android.accounts.Account r7 = r1.systemAccount     // Catch:{ Exception -> 0x027f }
            java.lang.String r7 = r7.type     // Catch:{ Exception -> 0x027f }
            android.content.ContentProviderOperation$Builder r4 = r4.withValue(r12, r7)     // Catch:{ Exception -> 0x027f }
            android.accounts.Account r7 = r1.systemAccount     // Catch:{ Exception -> 0x027f }
            java.lang.String r7 = r7.name     // Catch:{ Exception -> 0x027f }
            android.content.ContentProviderOperation$Builder r4 = r4.withValue(r9, r7)     // Catch:{ Exception -> 0x027f }
            java.lang.String r7 = "raw_contact_is_read_only"
            r9 = 1
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)     // Catch:{ Exception -> 0x027f }
            android.content.ContentProviderOperation$Builder r4 = r4.withValue(r7, r9)     // Catch:{ Exception -> 0x027f }
            java.lang.String r7 = "aggregation_mode"
            r9 = 3
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)     // Catch:{ Exception -> 0x027f }
            android.content.ContentProviderOperation$Builder r4 = r4.withValue(r7, r9)     // Catch:{ Exception -> 0x027f }
            android.content.ContentProviderOperation r4 = r4.build()     // Catch:{ Exception -> 0x027f }
            r11.add(r4)     // Catch:{ Exception -> 0x027f }
            android.net.Uri r4 = android.provider.ContactsContract.Data.CONTENT_URI     // Catch:{ Exception -> 0x027f }
            android.content.ContentProviderOperation$Builder r4 = android.content.ContentProviderOperation.newInsert(r4)     // Catch:{ Exception -> 0x027f }
            r7 = r18
            r9 = r22
            android.content.ContentProviderOperation$Builder r4 = r4.withValueBackReference(r9, r7)     // Catch:{ Exception -> 0x027f }
            r10 = r19
            android.content.ContentProviderOperation$Builder r4 = r4.withValue(r10, r5)     // Catch:{ Exception -> 0x027f }
            android.content.ContentProviderOperation$Builder r2 = r4.withValue(r6, r2)     // Catch:{ Exception -> 0x027f }
            android.content.ContentProviderOperation$Builder r2 = r2.withValue(r15, r3)     // Catch:{ Exception -> 0x027f }
            android.content.ContentProviderOperation r2 = r2.build()     // Catch:{ Exception -> 0x027f }
            r11.add(r2)     // Catch:{ Exception -> 0x027f }
            android.net.Uri r2 = android.provider.ContactsContract.Data.CONTENT_URI     // Catch:{ Exception -> 0x027f }
            android.content.ContentProviderOperation$Builder r2 = android.content.ContentProviderOperation.newInsert(r2)     // Catch:{ Exception -> 0x027f }
            android.content.ContentProviderOperation$Builder r2 = r2.withValueBackReference(r9, r7)     // Catch:{ Exception -> 0x027f }
            android.content.ContentProviderOperation$Builder r2 = r2.withValue(r10, r14)     // Catch:{ Exception -> 0x027f }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x027f }
            r3.<init>()     // Catch:{ Exception -> 0x027f }
            r3.append(r13)     // Catch:{ Exception -> 0x027f }
            r3.append(r0)     // Catch:{ Exception -> 0x027f }
            java.lang.String r0 = r3.toString()     // Catch:{ Exception -> 0x027f }
            android.content.ContentProviderOperation$Builder r0 = r2.withValue(r8, r0)     // Catch:{ Exception -> 0x027f }
            android.content.ContentProviderOperation r0 = r0.build()     // Catch:{ Exception -> 0x027f }
            r11.add(r0)     // Catch:{ Exception -> 0x027f }
            android.net.Uri r0 = android.provider.ContactsContract.Data.CONTENT_URI     // Catch:{ Exception -> 0x027f }
            android.content.ContentProviderOperation$Builder r0 = android.content.ContentProviderOperation.newInsert(r0)     // Catch:{ Exception -> 0x027f }
            android.content.ContentProviderOperation$Builder r0 = r0.withValueBackReference(r9, r7)     // Catch:{ Exception -> 0x027f }
            r2 = r17
            android.content.ContentProviderOperation$Builder r0 = r0.withValue(r10, r2)     // Catch:{ Exception -> 0x027f }
            java.lang.Integer r2 = java.lang.Integer.valueOf(r16)     // Catch:{ Exception -> 0x027f }
            android.content.ContentProviderOperation$Builder r0 = r0.withValue(r8, r2)     // Catch:{ Exception -> 0x027f }
            android.content.ContentProviderOperation r0 = r0.build()     // Catch:{ Exception -> 0x027f }
            r11.add(r0)     // Catch:{ Exception -> 0x027f }
        L_0x0272:
            if (r23 == 0) goto L_0x0277
            r23.close()     // Catch:{ Exception -> 0x027f }
        L_0x0277:
            java.lang.String r0 = "com.android.contacts"
            r2 = r20
            r2.applyBatch(r0, r11)     // Catch:{ Exception -> 0x027f }
            goto L_0x0283
        L_0x027f:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0283:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ContactsController.createOrUpdateConnectionServiceContact(int, java.lang.String, java.lang.String):void");
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
