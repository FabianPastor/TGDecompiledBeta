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
import org.telegram.tgnet.TLRPC;

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
    private ArrayList<TLRPC.PrivacyRule> addedByPhonePrivacyRules;
    private ArrayList<TLRPC.PrivacyRule> callPrivacyRules;
    private int completedRequestsCount;
    public ArrayList<TLRPC.TL_contact> contacts = new ArrayList<>();
    public HashMap<String, Contact> contactsBook = new HashMap<>();
    private boolean contactsBookLoaded;
    public HashMap<String, Contact> contactsBookSPhones = new HashMap<>();
    public HashMap<String, TLRPC.TL_contact> contactsByPhone = new HashMap<>();
    public HashMap<String, TLRPC.TL_contact> contactsByShortPhone = new HashMap<>();
    public ConcurrentHashMap<Long, TLRPC.TL_contact> contactsDict = new ConcurrentHashMap<>(20, 1.0f, 2);
    public boolean contactsLoaded;
    private boolean contactsSyncInProgress;
    private ArrayList<Long> delayedContactsUpdate = new ArrayList<>();
    private int deleteAccountTTL;
    public boolean doneLoadingContacts;
    private ArrayList<TLRPC.PrivacyRule> forwardsPrivacyRules;
    private TLRPC.TL_globalPrivacySettings globalPrivacySettings;
    private ArrayList<TLRPC.PrivacyRule> groupPrivacyRules;
    /* access modifiers changed from: private */
    public boolean ignoreChanges;
    private String inviteLink;
    private String lastContactsVersions = "";
    private ArrayList<TLRPC.PrivacyRule> lastseenPrivacyRules;
    private final Object loadContactsSync = new Object();
    private boolean loadingContacts;
    private int loadingDeleteInfo;
    private int loadingGlobalSettings;
    private int[] loadingPrivacyInfo = new int[8];
    private boolean migratingContacts;
    /* access modifiers changed from: private */
    public final Object observerLock = new Object();
    private ArrayList<TLRPC.PrivacyRule> p2pPrivacyRules;
    public ArrayList<Contact> phoneBookContacts = new ArrayList<>();
    public ArrayList<String> phoneBookSectionsArray = new ArrayList<>();
    public HashMap<String, ArrayList<Object>> phoneBookSectionsDict = new HashMap<>();
    private ArrayList<TLRPC.PrivacyRule> phonePrivacyRules;
    private ArrayList<TLRPC.PrivacyRule> profilePhotoPrivacyRules;
    private String[] projectionNames = {"lookup", "data2", "data3", "data5"};
    private String[] projectionPhones = {"lookup", "data1", "data2", "data3", "display_name", "account_type"};
    private HashMap<String, String> sectionsToReplace = new HashMap<>();
    public ArrayList<String> sortedUsersMutualSectionsArray = new ArrayList<>();
    public ArrayList<String> sortedUsersSectionsArray = new ArrayList<>();
    private Account systemAccount;
    private boolean updatingInviteLink;
    public HashMap<String, ArrayList<TLRPC.TL_contact>> usersMutualSectionsDict = new HashMap<>();
    public HashMap<String, ArrayList<TLRPC.TL_contact>> usersSectionsDict = new HashMap<>();

    private class MyContentObserver extends ContentObserver {
        private Runnable checkRunnable = ContactsController$MyContentObserver$$ExternalSyntheticLambda0.INSTANCE;

        static /* synthetic */ void lambda$new$0() {
            for (int a = 0; a < 3; a++) {
                if (UserConfig.getInstance(a).isClientActivated()) {
                    ConnectionsManager.getInstance(a).resumeNetworkMaybe();
                    ContactsController.getInstance(a).checkContacts();
                }
            }
        }

        public MyContentObserver() {
            super((Handler) null);
        }

        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            synchronized (ContactsController.this.observerLock) {
                if (!ContactsController.this.ignoreChanges) {
                    Utilities.globalQueue.cancelRunnable(this.checkRunnable);
                    Utilities.globalQueue.postRunnable(this.checkRunnable, 500);
                }
            }
        }

        public boolean deliverSelfNotifications() {
            return false;
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
        public TLRPC.User user;

        public String getLetter() {
            return getLetter(this.first_name, this.last_name);
        }

        public static String getLetter(String first_name2, String last_name2) {
            if (!TextUtils.isEmpty(first_name2)) {
                return first_name2.substring(0, 1);
            }
            if (!TextUtils.isEmpty(last_name2)) {
                return last_name2.substring(0, 1);
            }
            return "#";
        }
    }

    public static ContactsController getInstance(int num) {
        ContactsController localInstance = Instance[num];
        if (localInstance == null) {
            synchronized (ContactsController.class) {
                localInstance = Instance[num];
                if (localInstance == null) {
                    ContactsController[] contactsControllerArr = Instance;
                    ContactsController contactsController = new ContactsController(num);
                    localInstance = contactsController;
                    contactsControllerArr[num] = contactsController;
                }
            }
        }
        return localInstance;
    }

    public ContactsController(int instance) {
        super(instance);
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
        if (instance == 0) {
            Utilities.globalQueue.postRunnable(new ContactsController$$ExternalSyntheticLambda60(this));
        }
    }

    /* renamed from: lambda$new$0$org-telegram-messenger-ContactsController  reason: not valid java name */
    public /* synthetic */ void m29lambda$new$0$orgtelegrammessengerContactsController() {
        try {
            if (hasContactsPermission()) {
                ApplicationLoader.applicationContext.getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true, new MyContentObserver());
            }
        } catch (Throwable th) {
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
        Utilities.globalQueue.postRunnable(new ContactsController$$ExternalSyntheticLambda33(this));
    }

    /* renamed from: lambda$cleanup$1$org-telegram-messenger-ContactsController  reason: not valid java name */
    public /* synthetic */ void m11lambda$cleanup$1$orgtelegrammessengerContactsController() {
        this.migratingContacts = false;
        this.completedRequestsCount = 0;
    }

    public void checkInviteText() {
        SharedPreferences preferences = MessagesController.getMainSettings(this.currentAccount);
        this.inviteLink = preferences.getString("invitelink", (String) null);
        int time = preferences.getInt("invitelinktime", 0);
        if (this.updatingInviteLink) {
            return;
        }
        if (this.inviteLink == null || Math.abs((System.currentTimeMillis() / 1000) - ((long) time)) >= 86400) {
            this.updatingInviteLink = true;
            getConnectionsManager().sendRequest(new TLRPC.TL_help_getInviteText(), new ContactsController$$ExternalSyntheticLambda48(this), 2);
        }
    }

    /* renamed from: lambda$checkInviteText$3$org-telegram-messenger-ContactsController  reason: not valid java name */
    public /* synthetic */ void m10x699e1var_(TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            TLRPC.TL_help_inviteText res = (TLRPC.TL_help_inviteText) response;
            if (res.message.length() != 0) {
                AndroidUtilities.runOnUIThread(new ContactsController$$ExternalSyntheticLambda35(this, res));
            }
        }
    }

    /* renamed from: lambda$checkInviteText$2$org-telegram-messenger-ContactsController  reason: not valid java name */
    public /* synthetic */ void m9x845cb097(TLRPC.TL_help_inviteText res) {
        this.updatingInviteLink = false;
        SharedPreferences.Editor editor = MessagesController.getMainSettings(this.currentAccount).edit();
        String str = res.message;
        this.inviteLink = str;
        editor.putString("invitelink", str);
        editor.putInt("invitelinktime", (int) (System.currentTimeMillis() / 1000));
        editor.commit();
    }

    public String getInviteText(int contacts2) {
        String link = this.inviteLink;
        if (link == null) {
            link = "https://telegram.org/dl";
        }
        if (contacts2 <= 1) {
            return LocaleController.formatString("InviteText2", NUM, link);
        }
        try {
            return String.format(LocaleController.getPluralString("InviteTextNum", contacts2), new Object[]{Integer.valueOf(contacts2), link});
        } catch (Exception e) {
            return LocaleController.formatString("InviteText2", NUM, link);
        }
    }

    public void checkAppAccount() {
        AccountManager am = AccountManager.get(ApplicationLoader.applicationContext);
        try {
            Account[] accounts = am.getAccountsByType("org.telegram.messenger");
            this.systemAccount = null;
            for (int a = 0; a < accounts.length; a++) {
                Account acc = accounts[a];
                boolean found = false;
                int b = 0;
                while (true) {
                    if (b >= 3) {
                        break;
                    }
                    TLRPC.User user = UserConfig.getInstance(b).getCurrentUser();
                    if (user != null) {
                        String str = acc.name;
                        if (str.equals("" + user.id)) {
                            if (b == this.currentAccount) {
                                this.systemAccount = acc;
                            }
                            found = true;
                        }
                    }
                    b++;
                }
                if (!found) {
                    try {
                        am.removeAccount(accounts[a], (AccountManagerCallback) null, (Handler) null);
                    } catch (Exception e) {
                    }
                }
            }
        } catch (Throwable th) {
        }
        if (getUserConfig().isClientActivated()) {
            readContacts();
            if (this.systemAccount == null) {
                try {
                    Account account = new Account("" + getUserConfig().getClientUserId(), "org.telegram.messenger");
                    this.systemAccount = account;
                    am.addAccountExplicitly(account, "", (Bundle) null);
                } catch (Exception e2) {
                }
            }
        }
    }

    public void deleteUnknownAppAccounts() {
        try {
            this.systemAccount = null;
            AccountManager am = AccountManager.get(ApplicationLoader.applicationContext);
            Account[] accounts = am.getAccountsByType("org.telegram.messenger");
            for (int a = 0; a < accounts.length; a++) {
                Account acc = accounts[a];
                boolean found = false;
                int b = 0;
                while (true) {
                    if (b >= 3) {
                        break;
                    }
                    TLRPC.User user = UserConfig.getInstance(b).getCurrentUser();
                    if (user != null) {
                        String str = acc.name;
                        if (str.equals("" + user.id)) {
                            found = true;
                            break;
                        }
                    }
                    b++;
                }
                if (!found) {
                    try {
                        am.removeAccount(accounts[a], (AccountManagerCallback) null, (Handler) null);
                    } catch (Exception e) {
                    }
                }
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public void checkContacts() {
        Utilities.globalQueue.postRunnable(new ContactsController$$ExternalSyntheticLambda22(this));
    }

    /* renamed from: lambda$checkContacts$4$org-telegram-messenger-ContactsController  reason: not valid java name */
    public /* synthetic */ void m8lambda$checkContacts$4$orgtelegrammessengerContactsController() {
        if (checkContactsInternal()) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("detected contacts change");
            }
            performSyncPhoneBook(getContactsCopy(this.contactsBook), true, false, true, false, true, false);
        }
    }

    public void forceImportContacts() {
        Utilities.globalQueue.postRunnable(new ContactsController$$ExternalSyntheticLambda44(this));
    }

    /* renamed from: lambda$forceImportContacts$5$org-telegram-messenger-ContactsController  reason: not valid java name */
    public /* synthetic */ void m17xCLASSNAMEfbf() {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("force import contacts");
        }
        performSyncPhoneBook(new HashMap(), true, true, true, true, false, false);
    }

    public void syncPhoneBookByAlert(HashMap<String, Contact> contacts2, boolean first, boolean schedule, boolean cancel) {
        Utilities.globalQueue.postRunnable(new ContactsController$$ExternalSyntheticLambda29(this, contacts2, first, schedule, cancel));
    }

    /* renamed from: lambda$syncPhoneBookByAlert$6$org-telegram-messenger-ContactsController  reason: not valid java name */
    public /* synthetic */ void m54xe9e28340(HashMap contacts2, boolean first, boolean schedule, boolean cancel) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("sync contacts by alert");
        }
        performSyncPhoneBook(contacts2, true, first, schedule, false, false, cancel);
    }

    public void deleteAllContacts(Runnable runnable) {
        resetImportedContacts();
        TLRPC.TL_contacts_deleteContacts req = new TLRPC.TL_contacts_deleteContacts();
        int size = this.contacts.size();
        for (int a = 0; a < size; a++) {
            req.id.add(getMessagesController().getInputUser(this.contacts.get(a).user_id));
        }
        getConnectionsManager().sendRequest(req, new ContactsController$$ExternalSyntheticLambda54(this, runnable));
    }

    /* renamed from: lambda$deleteAllContacts$8$org-telegram-messenger-ContactsController  reason: not valid java name */
    public /* synthetic */ void m13x3b83ab48(Runnable runnable, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            this.contactsBookSPhones.clear();
            this.contactsBook.clear();
            this.completedRequestsCount = 0;
            this.migratingContacts = false;
            this.contactsSyncInProgress = false;
            this.contactsLoaded = false;
            this.loadingContacts = false;
            this.contactsBookLoaded = false;
            this.lastContactsVersions = "";
            AndroidUtilities.runOnUIThread(new ContactsController$$ExternalSyntheticLambda7(this, runnable));
            return;
        }
        AndroidUtilities.runOnUIThread(runnable);
    }

    /* renamed from: lambda$deleteAllContacts$7$org-telegram-messenger-ContactsController  reason: not valid java name */
    public /* synthetic */ void m12x56423CLASSNAME(Runnable runnable) {
        AccountManager am = AccountManager.get(ApplicationLoader.applicationContext);
        try {
            Account[] accounts = am.getAccountsByType("org.telegram.messenger");
            this.systemAccount = null;
            for (Account acc : accounts) {
                int b = 0;
                while (true) {
                    if (b >= 3) {
                        break;
                    }
                    TLRPC.User user = UserConfig.getInstance(b).getCurrentUser();
                    if (user != null) {
                        if (acc.name.equals("" + user.id)) {
                            am.removeAccount(acc, (AccountManagerCallback) null, (Handler) null);
                            break;
                        }
                    }
                    b++;
                }
            }
        } catch (Throwable th) {
        }
        try {
            Account account = new Account("" + getUserConfig().getClientUserId(), "org.telegram.messenger");
            this.systemAccount = account;
            am.addAccountExplicitly(account, "", (Bundle) null);
        } catch (Exception e) {
        }
        getMessagesStorage().putCachedPhoneBook(new HashMap(), false, true);
        getMessagesStorage().putContacts(new ArrayList(), true);
        this.phoneBookContacts.clear();
        this.contacts.clear();
        this.contactsDict.clear();
        this.usersSectionsDict.clear();
        this.usersMutualSectionsDict.clear();
        this.sortedUsersSectionsArray.clear();
        this.phoneBookSectionsDict.clear();
        this.phoneBookSectionsArray.clear();
        this.delayedContactsUpdate.clear();
        this.sortedUsersMutualSectionsArray.clear();
        this.contactsByPhone.clear();
        this.contactsByShortPhone.clear();
        getNotificationCenter().postNotificationName(NotificationCenter.contactsDidLoad, new Object[0]);
        loadContacts(false, 0);
        runnable.run();
    }

    public void resetImportedContacts() {
        getConnectionsManager().sendRequest(new TLRPC.TL_contacts_resetSaved(), ContactsController$$ExternalSyntheticLambda59.INSTANCE);
    }

    static /* synthetic */ void lambda$resetImportedContacts$9(TLObject response, TLRPC.TL_error error) {
    }

    private boolean checkContactsInternal() {
        Cursor pCur;
        boolean reload = false;
        try {
            if (!hasContactsPermission()) {
                return false;
            }
            ContentResolver cr = ApplicationLoader.applicationContext.getContentResolver();
            try {
                pCur = cr.query(ContactsContract.RawContacts.CONTENT_URI, new String[]{"version"}, (String) null, (String[]) null, (String) null);
                if (pCur != null) {
                    StringBuilder currentVersion = new StringBuilder();
                    while (pCur.moveToNext()) {
                        currentVersion.append(pCur.getString(pCur.getColumnIndex("version")));
                    }
                    String newContactsVersion = currentVersion.toString();
                    if (this.lastContactsVersions.length() != 0 && !this.lastContactsVersions.equals(newContactsVersion)) {
                        reload = true;
                    }
                    this.lastContactsVersions = newContactsVersion;
                }
                if (pCur != null) {
                    pCur.close();
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            } catch (Throwable th) {
            }
            return reload;
            throw th;
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
        }
    }

    public void readContacts() {
        synchronized (this.loadContactsSync) {
            if (!this.loadingContacts) {
                this.loadingContacts = true;
                Utilities.stageQueue.postRunnable(new ContactsController$$ExternalSyntheticLambda1(this));
            }
        }
    }

    /* renamed from: lambda$readContacts$10$org-telegram-messenger-ContactsController  reason: not valid java name */
    public /* synthetic */ void m51lambda$readContacts$10$orgtelegrammessengerContactsController() {
        if (!this.contacts.isEmpty() || this.contactsLoaded) {
            synchronized (this.loadContactsSync) {
                this.loadingContacts = false;
            }
            return;
        }
        loadContacts(true, 0);
    }

    private boolean isNotValidNameString(String src) {
        if (TextUtils.isEmpty(src)) {
            return true;
        }
        int count = 0;
        int len = src.length();
        for (int a = 0; a < len; a++) {
            char c = src.charAt(a);
            if (c >= '0' && c <= '9') {
                count++;
            }
        }
        if (count > 3) {
            return true;
        }
        return false;
    }

    /* JADX WARNING: Removed duplicated region for block: B:194:0x038d A[Catch:{ all -> 0x03a0 }] */
    /* JADX WARNING: Removed duplicated region for block: B:196:0x0392 A[SYNTHETIC, Splitter:B:196:0x0392] */
    /* JADX WARNING: Removed duplicated region for block: B:199:0x0398  */
    /* JADX WARNING: Removed duplicated region for block: B:200:0x039a  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.util.HashMap<java.lang.String, org.telegram.messenger.ContactsController.Contact> readContactsFromPhoneBook() {
        /*
            r32 = this;
            r1 = r32
            org.telegram.messenger.UserConfig r0 = r32.getUserConfig()
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
            boolean r0 = r32.hasContactsPermission()
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
            r2 = 0
            r3 = 0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x0386 }
            r0.<init>()     // Catch:{ all -> 0x0386 }
            r4 = r0
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0386 }
            android.content.ContentResolver r5 = r0.getContentResolver()     // Catch:{ all -> 0x0386 }
            java.util.HashMap r0 = new java.util.HashMap     // Catch:{ all -> 0x0386 }
            r0.<init>()     // Catch:{ all -> 0x0386 }
            r11 = r0
            java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ all -> 0x0386 }
            r0.<init>()     // Catch:{ all -> 0x0386 }
            r12 = r0
            android.net.Uri r6 = android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI     // Catch:{ all -> 0x0386 }
            java.lang.String[] r7 = r1.projectionPhones     // Catch:{ all -> 0x0386 }
            r8 = 0
            r9 = 0
            r10 = 0
            android.database.Cursor r0 = r5.query(r6, r7, r8, r9, r10)     // Catch:{ all -> 0x0386 }
            r2 = r0
            r0 = 1
            r8 = 0
            java.lang.String r9 = ""
            r10 = 1
            if (r2 == 0) goto L_0x023f
            int r13 = r2.getCount()     // Catch:{ all -> 0x0382 }
            if (r13 <= 0) goto L_0x0230
            java.util.HashMap r14 = new java.util.HashMap     // Catch:{ all -> 0x0382 }
            r14.<init>(r13)     // Catch:{ all -> 0x0382 }
            r3 = r14
        L_0x0067:
            boolean r14 = r2.moveToNext()     // Catch:{ all -> 0x0382 }
            if (r14 == 0) goto L_0x0228
            java.lang.String r14 = r2.getString(r10)     // Catch:{ all -> 0x0382 }
            r15 = 5
            java.lang.String r15 = r2.getString(r15)     // Catch:{ all -> 0x0382 }
            if (r15 != 0) goto L_0x0079
            r15 = r9
        L_0x0079:
            java.lang.String r6 = ".sim"
            int r6 = r15.indexOf(r6)     // Catch:{ all -> 0x0382 }
            if (r6 == 0) goto L_0x0083
            r6 = 1
            goto L_0x0084
        L_0x0083:
            r6 = 0
        L_0x0084:
            boolean r17 = android.text.TextUtils.isEmpty(r14)     // Catch:{ all -> 0x0382 }
            if (r17 == 0) goto L_0x008e
            r21 = r13
            goto L_0x00fc
        L_0x008e:
            java.lang.String r17 = org.telegram.PhoneFormat.PhoneFormat.stripExceptNumbers(r14, r10)     // Catch:{ all -> 0x0382 }
            r14 = r17
            boolean r17 = android.text.TextUtils.isEmpty(r14)     // Catch:{ all -> 0x0382 }
            if (r17 == 0) goto L_0x009d
            r21 = r13
            goto L_0x00fc
        L_0x009d:
            r17 = r14
            java.lang.String r7 = "+"
            boolean r7 = r14.startsWith(r7)     // Catch:{ all -> 0x0382 }
            if (r7 == 0) goto L_0x00ae
            java.lang.String r7 = r14.substring(r10)     // Catch:{ all -> 0x0386 }
            r17 = r7
            goto L_0x00b0
        L_0x00ae:
            r7 = r17
        L_0x00b0:
            java.lang.String r17 = r2.getString(r8)     // Catch:{ all -> 0x0382 }
            r19 = r17
            r4.setLength(r8)     // Catch:{ all -> 0x0382 }
            r10 = r19
            android.database.DatabaseUtils.appendEscapedSQLString(r4, r10)     // Catch:{ all -> 0x0382 }
            java.lang.String r19 = r4.toString()     // Catch:{ all -> 0x0382 }
            r20 = r19
            java.lang.Object r19 = r11.get(r7)     // Catch:{ all -> 0x0382 }
            org.telegram.messenger.ContactsController$Contact r19 = (org.telegram.messenger.ContactsController.Contact) r19     // Catch:{ all -> 0x0382 }
            r21 = r19
            r8 = r21
            if (r8 == 0) goto L_0x0102
            r21 = r13
            boolean r13 = r8.isGoodProvider     // Catch:{ all -> 0x0386 }
            if (r13 != 0) goto L_0x00fa
            java.lang.String r13 = r8.provider     // Catch:{ all -> 0x0386 }
            boolean r13 = r15.equals(r13)     // Catch:{ all -> 0x0386 }
            if (r13 != 0) goto L_0x00fa
            r13 = 0
            r4.setLength(r13)     // Catch:{ all -> 0x0386 }
            java.lang.String r13 = r8.key     // Catch:{ all -> 0x0386 }
            android.database.DatabaseUtils.appendEscapedSQLString(r4, r13)     // Catch:{ all -> 0x0386 }
            java.lang.String r13 = r4.toString()     // Catch:{ all -> 0x0386 }
            r12.remove(r13)     // Catch:{ all -> 0x0386 }
            r13 = r20
            r12.add(r13)     // Catch:{ all -> 0x0386 }
            r8.key = r10     // Catch:{ all -> 0x0386 }
            r8.isGoodProvider = r6     // Catch:{ all -> 0x0386 }
            r8.provider = r15     // Catch:{ all -> 0x0386 }
            goto L_0x00fc
        L_0x00fa:
            r13 = r20
        L_0x00fc:
            r13 = r21
            r8 = 0
            r10 = 1
            goto L_0x0067
        L_0x0102:
            r21 = r13
            r13 = r20
            boolean r20 = r12.contains(r13)     // Catch:{ all -> 0x0382 }
            if (r20 != 0) goto L_0x010f
            r12.add(r13)     // Catch:{ all -> 0x0386 }
        L_0x010f:
            r20 = r4
            r4 = 2
            int r22 = r2.getInt(r4)     // Catch:{ all -> 0x0382 }
            r4 = r22
            java.lang.Object r22 = r3.get(r10)     // Catch:{ all -> 0x0382 }
            org.telegram.messenger.ContactsController$Contact r22 = (org.telegram.messenger.ContactsController.Contact) r22     // Catch:{ all -> 0x0382 }
            if (r22 != 0) goto L_0x0183
            org.telegram.messenger.ContactsController$Contact r23 = new org.telegram.messenger.ContactsController$Contact     // Catch:{ all -> 0x0382 }
            r23.<init>()     // Catch:{ all -> 0x0382 }
            r22 = r23
            r23 = r8
            r8 = 4
            java.lang.String r8 = r2.getString(r8)     // Catch:{ all -> 0x0382 }
            if (r8 != 0) goto L_0x0132
            r8 = r9
            goto L_0x0138
        L_0x0132:
            java.lang.String r24 = r8.trim()     // Catch:{ all -> 0x0382 }
            r8 = r24
        L_0x0138:
            boolean r24 = r1.isNotValidNameString(r8)     // Catch:{ all -> 0x0382 }
            if (r24 == 0) goto L_0x0149
            r24 = r13
            r13 = r22
            r13.first_name = r8     // Catch:{ all -> 0x0386 }
            r13.last_name = r9     // Catch:{ all -> 0x0386 }
            r25 = r5
            goto L_0x0174
        L_0x0149:
            r24 = r13
            r13 = r22
            r25 = r5
            r5 = 32
            int r5 = r8.lastIndexOf(r5)     // Catch:{ all -> 0x0382 }
            r1 = -1
            if (r5 == r1) goto L_0x0170
            r1 = 0
            java.lang.String r22 = r8.substring(r1, r5)     // Catch:{ all -> 0x0382 }
            java.lang.String r1 = r22.trim()     // Catch:{ all -> 0x0382 }
            r13.first_name = r1     // Catch:{ all -> 0x0382 }
            int r1 = r5 + 1
            java.lang.String r1 = r8.substring(r1)     // Catch:{ all -> 0x0382 }
            java.lang.String r1 = r1.trim()     // Catch:{ all -> 0x0382 }
            r13.last_name = r1     // Catch:{ all -> 0x0382 }
            goto L_0x0174
        L_0x0170:
            r13.first_name = r8     // Catch:{ all -> 0x0382 }
            r13.last_name = r9     // Catch:{ all -> 0x0382 }
        L_0x0174:
            r13.provider = r15     // Catch:{ all -> 0x0382 }
            r13.isGoodProvider = r6     // Catch:{ all -> 0x0382 }
            r13.key = r10     // Catch:{ all -> 0x0382 }
            int r1 = r0 + 1
            r13.contact_id = r0     // Catch:{ all -> 0x0382 }
            r3.put(r10, r13)     // Catch:{ all -> 0x0382 }
            r0 = r1
            goto L_0x018b
        L_0x0183:
            r25 = r5
            r23 = r8
            r24 = r13
            r13 = r22
        L_0x018b:
            java.util.ArrayList<java.lang.String> r1 = r13.shortPhones     // Catch:{ all -> 0x0382 }
            r1.add(r7)     // Catch:{ all -> 0x0382 }
            java.util.ArrayList<java.lang.String> r1 = r13.phones     // Catch:{ all -> 0x0382 }
            r1.add(r14)     // Catch:{ all -> 0x0382 }
            java.util.ArrayList<java.lang.Integer> r1 = r13.phoneDeleted     // Catch:{ all -> 0x0382 }
            r5 = 0
            java.lang.Integer r8 = java.lang.Integer.valueOf(r5)     // Catch:{ all -> 0x0382 }
            r1.add(r8)     // Catch:{ all -> 0x0382 }
            java.lang.String r5 = "PhoneMobile"
            if (r4 != 0) goto L_0x01c0
            r8 = 3
            java.lang.String r22 = r2.getString(r8)     // Catch:{ all -> 0x0382 }
            r8 = r22
            java.util.ArrayList<java.lang.String> r1 = r13.phoneTypes     // Catch:{ all -> 0x0382 }
            if (r8 == 0) goto L_0x01b2
            r26 = r0
            r0 = r8
            goto L_0x01bb
        L_0x01b2:
            r26 = r0
            r0 = 2131627331(0x7f0e0d43, float:1.8881923E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r5, r0)     // Catch:{ all -> 0x0382 }
        L_0x01bb:
            r1.add(r0)     // Catch:{ all -> 0x0382 }
            goto L_0x0217
        L_0x01c0:
            r26 = r0
            r1 = 1
            if (r4 != r1) goto L_0x01d4
            java.util.ArrayList<java.lang.String> r0 = r13.phoneTypes     // Catch:{ all -> 0x0382 }
            java.lang.String r1 = "PhoneHome"
            r5 = 2131627329(0x7f0e0d41, float:1.888192E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r5)     // Catch:{ all -> 0x0382 }
            r0.add(r1)     // Catch:{ all -> 0x0382 }
            goto L_0x0217
        L_0x01d4:
            r1 = 2
            if (r4 != r1) goto L_0x01e4
            java.util.ArrayList<java.lang.String> r0 = r13.phoneTypes     // Catch:{ all -> 0x0382 }
            r1 = 2131627331(0x7f0e0d43, float:1.8881923E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r5, r1)     // Catch:{ all -> 0x0382 }
            r0.add(r1)     // Catch:{ all -> 0x0382 }
            goto L_0x0217
        L_0x01e4:
            r1 = 3
            if (r4 != r1) goto L_0x01f6
            java.util.ArrayList<java.lang.String> r0 = r13.phoneTypes     // Catch:{ all -> 0x0382 }
            java.lang.String r1 = "PhoneWork"
            r5 = 2131627341(0x7f0e0d4d, float:1.8881944E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r5)     // Catch:{ all -> 0x0382 }
            r0.add(r1)     // Catch:{ all -> 0x0382 }
            goto L_0x0217
        L_0x01f6:
            r0 = 12
            if (r4 != r0) goto L_0x0209
            java.util.ArrayList<java.lang.String> r0 = r13.phoneTypes     // Catch:{ all -> 0x0382 }
            java.lang.String r1 = "PhoneMain"
            r5 = 2131627330(0x7f0e0d42, float:1.8881921E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r5)     // Catch:{ all -> 0x0382 }
            r0.add(r1)     // Catch:{ all -> 0x0382 }
            goto L_0x0217
        L_0x0209:
            java.util.ArrayList<java.lang.String> r0 = r13.phoneTypes     // Catch:{ all -> 0x0382 }
            java.lang.String r1 = "PhoneOther"
            r5 = 2131627340(0x7f0e0d4c, float:1.8881942E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r5)     // Catch:{ all -> 0x0382 }
            r0.add(r1)     // Catch:{ all -> 0x0382 }
        L_0x0217:
            r11.put(r7, r13)     // Catch:{ all -> 0x0382 }
            r1 = r32
            r4 = r20
            r13 = r21
            r5 = r25
            r0 = r26
            r8 = 0
            r10 = 1
            goto L_0x0067
        L_0x0228:
            r20 = r4
            r25 = r5
            r21 = r13
            r1 = r0
            goto L_0x0237
        L_0x0230:
            r20 = r4
            r25 = r5
            r21 = r13
            r1 = r0
        L_0x0237:
            r2.close()     // Catch:{ Exception -> 0x023b }
            goto L_0x023c
        L_0x023b:
            r0 = move-exception
        L_0x023c:
            r0 = 0
            r2 = r0
            goto L_0x0244
        L_0x023f:
            r20 = r4
            r25 = r5
            r1 = r0
        L_0x0244:
            java.lang.String r0 = ","
            java.lang.String r0 = android.text.TextUtils.join(r0, r12)     // Catch:{ all -> 0x0382 }
            r4 = r0
            android.net.Uri r27 = android.provider.ContactsContract.Data.CONTENT_URI     // Catch:{ all -> 0x0382 }
            r6 = r32
            java.lang.String[] r0 = r6.projectionNames     // Catch:{ all -> 0x0380 }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0380 }
            r5.<init>()     // Catch:{ all -> 0x0380 }
            java.lang.String r7 = "lookup IN ("
            r5.append(r7)     // Catch:{ all -> 0x0380 }
            r5.append(r4)     // Catch:{ all -> 0x0380 }
            java.lang.String r7 = ") AND "
            r5.append(r7)     // Catch:{ all -> 0x0380 }
            java.lang.String r7 = "mimetype"
            r5.append(r7)     // Catch:{ all -> 0x0380 }
            java.lang.String r7 = " = '"
            r5.append(r7)     // Catch:{ all -> 0x0380 }
            java.lang.String r7 = "vnd.android.cursor.item/name"
            r5.append(r7)     // Catch:{ all -> 0x0380 }
            java.lang.String r7 = "'"
            r5.append(r7)     // Catch:{ all -> 0x0380 }
            java.lang.String r29 = r5.toString()     // Catch:{ all -> 0x0380 }
            r30 = 0
            r31 = 0
            r5 = r25
            r26 = r5
            r28 = r0
            android.database.Cursor r0 = r26.query(r27, r28, r29, r30, r31)     // Catch:{ all -> 0x0380 }
            r2 = r0
            if (r2 == 0) goto L_0x0372
        L_0x028c:
            boolean r0 = r2.moveToNext()     // Catch:{ all -> 0x0380 }
            if (r0 == 0) goto L_0x036c
            r7 = 0
            java.lang.String r0 = r2.getString(r7)     // Catch:{ all -> 0x0380 }
            r8 = 1
            java.lang.String r10 = r2.getString(r8)     // Catch:{ all -> 0x0380 }
            r8 = r10
            r10 = 2
            java.lang.String r13 = r2.getString(r10)     // Catch:{ all -> 0x0380 }
            r14 = 3
            java.lang.String r15 = r2.getString(r14)     // Catch:{ all -> 0x0380 }
            if (r3 == 0) goto L_0x02b0
            java.lang.Object r16 = r3.get(r0)     // Catch:{ all -> 0x0380 }
            org.telegram.messenger.ContactsController$Contact r16 = (org.telegram.messenger.ContactsController.Contact) r16     // Catch:{ all -> 0x0380 }
            goto L_0x02b2
        L_0x02b0:
            r16 = 0
        L_0x02b2:
            r18 = r16
            r7 = r18
            if (r7 == 0) goto L_0x0367
            boolean r10 = r7.namesFilled     // Catch:{ all -> 0x0380 }
            if (r10 != 0) goto L_0x0367
            boolean r10 = r7.isGoodProvider     // Catch:{ all -> 0x0380 }
            java.lang.String r14 = " "
            if (r10 == 0) goto L_0x0300
            if (r8 == 0) goto L_0x02c7
            r7.first_name = r8     // Catch:{ all -> 0x0380 }
            goto L_0x02c9
        L_0x02c7:
            r7.first_name = r9     // Catch:{ all -> 0x0380 }
        L_0x02c9:
            if (r13 == 0) goto L_0x02ce
            r7.last_name = r13     // Catch:{ all -> 0x0380 }
            goto L_0x02d0
        L_0x02ce:
            r7.last_name = r9     // Catch:{ all -> 0x0380 }
        L_0x02d0:
            boolean r10 = android.text.TextUtils.isEmpty(r15)     // Catch:{ all -> 0x0380 }
            if (r10 != 0) goto L_0x02fd
            java.lang.String r10 = r7.first_name     // Catch:{ all -> 0x0380 }
            boolean r10 = android.text.TextUtils.isEmpty(r10)     // Catch:{ all -> 0x0380 }
            if (r10 != 0) goto L_0x02f8
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ all -> 0x0380 }
            r10.<init>()     // Catch:{ all -> 0x0380 }
            r21 = r0
            java.lang.String r0 = r7.first_name     // Catch:{ all -> 0x0380 }
            r10.append(r0)     // Catch:{ all -> 0x0380 }
            r10.append(r14)     // Catch:{ all -> 0x0380 }
            r10.append(r15)     // Catch:{ all -> 0x0380 }
            java.lang.String r0 = r10.toString()     // Catch:{ all -> 0x0380 }
            r7.first_name = r0     // Catch:{ all -> 0x0380 }
            goto L_0x0363
        L_0x02f8:
            r21 = r0
            r7.first_name = r15     // Catch:{ all -> 0x0380 }
            goto L_0x0363
        L_0x02fd:
            r21 = r0
            goto L_0x0363
        L_0x0300:
            r21 = r0
            boolean r0 = r6.isNotValidNameString(r8)     // Catch:{ all -> 0x0380 }
            if (r0 != 0) goto L_0x0318
            java.lang.String r0 = r7.first_name     // Catch:{ all -> 0x0380 }
            boolean r0 = r0.contains(r8)     // Catch:{ all -> 0x0380 }
            if (r0 != 0) goto L_0x032e
            java.lang.String r0 = r7.first_name     // Catch:{ all -> 0x0380 }
            boolean r0 = r8.contains(r0)     // Catch:{ all -> 0x0380 }
            if (r0 != 0) goto L_0x032e
        L_0x0318:
            boolean r0 = r6.isNotValidNameString(r13)     // Catch:{ all -> 0x0380 }
            if (r0 != 0) goto L_0x0363
            java.lang.String r0 = r7.last_name     // Catch:{ all -> 0x0380 }
            boolean r0 = r0.contains(r13)     // Catch:{ all -> 0x0380 }
            if (r0 != 0) goto L_0x032e
            java.lang.String r0 = r7.last_name     // Catch:{ all -> 0x0380 }
            boolean r0 = r8.contains(r0)     // Catch:{ all -> 0x0380 }
            if (r0 == 0) goto L_0x0363
        L_0x032e:
            if (r8 == 0) goto L_0x0333
            r7.first_name = r8     // Catch:{ all -> 0x0380 }
            goto L_0x0335
        L_0x0333:
            r7.first_name = r9     // Catch:{ all -> 0x0380 }
        L_0x0335:
            boolean r0 = android.text.TextUtils.isEmpty(r15)     // Catch:{ all -> 0x0380 }
            if (r0 != 0) goto L_0x035c
            java.lang.String r0 = r7.first_name     // Catch:{ all -> 0x0380 }
            boolean r0 = android.text.TextUtils.isEmpty(r0)     // Catch:{ all -> 0x0380 }
            if (r0 != 0) goto L_0x035a
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x0380 }
            r0.<init>()     // Catch:{ all -> 0x0380 }
            java.lang.String r10 = r7.first_name     // Catch:{ all -> 0x0380 }
            r0.append(r10)     // Catch:{ all -> 0x0380 }
            r0.append(r14)     // Catch:{ all -> 0x0380 }
            r0.append(r15)     // Catch:{ all -> 0x0380 }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x0380 }
            r7.first_name = r0     // Catch:{ all -> 0x0380 }
            goto L_0x035c
        L_0x035a:
            r7.first_name = r15     // Catch:{ all -> 0x0380 }
        L_0x035c:
            if (r13 == 0) goto L_0x0361
            r7.last_name = r13     // Catch:{ all -> 0x0380 }
            goto L_0x0363
        L_0x0361:
            r7.last_name = r9     // Catch:{ all -> 0x0380 }
        L_0x0363:
            r10 = 1
            r7.namesFilled = r10     // Catch:{ all -> 0x0380 }
            goto L_0x036a
        L_0x0367:
            r21 = r0
            r10 = 1
        L_0x036a:
            goto L_0x028c
        L_0x036c:
            r2.close()     // Catch:{ Exception -> 0x0370 }
            goto L_0x0371
        L_0x0370:
            r0 = move-exception
        L_0x0371:
            r2 = 0
        L_0x0372:
            if (r2 == 0) goto L_0x037f
            r2.close()     // Catch:{ Exception -> 0x0378 }
            goto L_0x037f
        L_0x0378:
            r0 = move-exception
            r1 = r0
            r0 = r1
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0396
        L_0x037f:
            goto L_0x0396
        L_0x0380:
            r0 = move-exception
            goto L_0x0388
        L_0x0382:
            r0 = move-exception
            r6 = r32
            goto L_0x0388
        L_0x0386:
            r0 = move-exception
            r6 = r1
        L_0x0388:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x03a0 }
            if (r3 == 0) goto L_0x0390
            r3.clear()     // Catch:{ all -> 0x03a0 }
        L_0x0390:
            if (r2 == 0) goto L_0x037f
            r2.close()     // Catch:{ Exception -> 0x0378 }
            goto L_0x037f
        L_0x0396:
            if (r3 == 0) goto L_0x039a
            r0 = r3
            goto L_0x039f
        L_0x039a:
            java.util.HashMap r0 = new java.util.HashMap
            r0.<init>()
        L_0x039f:
            return r0
        L_0x03a0:
            r0 = move-exception
            r1 = r0
            if (r2 == 0) goto L_0x03af
            r2.close()     // Catch:{ Exception -> 0x03a8 }
            goto L_0x03af
        L_0x03a8:
            r0 = move-exception
            r4 = r0
            r0 = r4
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x03b0
        L_0x03af:
        L_0x03b0:
            goto L_0x03b2
        L_0x03b1:
            throw r1
        L_0x03b2:
            goto L_0x03b1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ContactsController.readContactsFromPhoneBook():java.util.HashMap");
    }

    public HashMap<String, Contact> getContactsCopy(HashMap<String, Contact> original) {
        HashMap<String, Contact> ret = new HashMap<>();
        for (Map.Entry<String, Contact> entry : original.entrySet()) {
            Contact copyContact = new Contact();
            Contact originalContact = entry.getValue();
            copyContact.phoneDeleted.addAll(originalContact.phoneDeleted);
            copyContact.phones.addAll(originalContact.phones);
            copyContact.phoneTypes.addAll(originalContact.phoneTypes);
            copyContact.shortPhones.addAll(originalContact.shortPhones);
            copyContact.first_name = originalContact.first_name;
            copyContact.last_name = originalContact.last_name;
            copyContact.contact_id = originalContact.contact_id;
            copyContact.key = originalContact.key;
            ret.put(copyContact.key, copyContact);
        }
        return ret;
    }

    /* access modifiers changed from: protected */
    public void migratePhoneBookToV7(SparseArray<Contact> contactHashMap) {
        Utilities.globalQueue.postRunnable(new ContactsController$$ExternalSyntheticLambda5(this, contactHashMap));
    }

    /* renamed from: lambda$migratePhoneBookToV7$11$org-telegram-messenger-ContactsController  reason: not valid java name */
    public /* synthetic */ void m28x39var_a07(SparseArray contactHashMap) {
        if (!this.migratingContacts) {
            this.migratingContacts = true;
            HashMap<String, Contact> migratedMap = new HashMap<>();
            HashMap<String, Contact> contactsMap = readContactsFromPhoneBook();
            HashMap<String, String> contactsBookShort = new HashMap<>();
            for (Map.Entry<String, Contact> entry : contactsMap.entrySet()) {
                Contact value = entry.getValue();
                for (int a = 0; a < value.shortPhones.size(); a++) {
                    contactsBookShort.put(value.shortPhones.get(a), value.key);
                }
            }
            for (int b = 0; b < contactHashMap.size(); b++) {
                Contact value2 = (Contact) contactHashMap.valueAt(b);
                int a2 = 0;
                while (true) {
                    if (a2 >= value2.shortPhones.size()) {
                        break;
                    }
                    String key = contactsBookShort.get(value2.shortPhones.get(a2));
                    if (key != null) {
                        value2.key = key;
                        migratedMap.put(key, value2);
                        break;
                    }
                    a2++;
                }
            }
            if (BuildVars.LOGS_ENABLED != 0) {
                FileLog.d("migrated contacts " + migratedMap.size() + " of " + contactHashMap.size());
            }
            getMessagesStorage().putCachedPhoneBook(migratedMap, true, false);
        }
    }

    /* access modifiers changed from: protected */
    public void performSyncPhoneBook(HashMap<String, Contact> contactHashMap, boolean request, boolean first, boolean schedule, boolean force, boolean checkCount, boolean canceled) {
        if (!first) {
            if (!this.contactsBookLoaded) {
                return;
            }
        }
        Utilities.globalQueue.postRunnable(new ContactsController$$ExternalSyntheticLambda30(this, contactHashMap, schedule, request, first, force, checkCount, canceled));
    }

    /* JADX WARNING: Code restructure failed: missing block: B:44:0x0163, code lost:
        if (r2.first_name.equals(r1.first_name) != false) goto L_0x0168;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:49:0x0178, code lost:
        if (r2.last_name.equals(r1.last_name) == false) goto L_0x017a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:50:0x017a, code lost:
        r0 = true;
     */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x02d6  */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x0236  */
    /* renamed from: lambda$performSyncPhoneBook$24$org-telegram-messenger-ContactsController  reason: not valid java name */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void m42x3cf1c3d6(java.util.HashMap r35, boolean r36, boolean r37, boolean r38, boolean r39, boolean r40, boolean r41) {
        /*
            r34 = this;
            r13 = r34
            r14 = r35
            r0 = 0
            r1 = 0
            r15 = 1
            java.util.HashMap r2 = new java.util.HashMap
            r2.<init>()
            r12 = r2
            java.util.Set r2 = r35.entrySet()
            java.util.Iterator r2 = r2.iterator()
        L_0x0015:
            boolean r3 = r2.hasNext()
            if (r3 == 0) goto L_0x003f
            java.lang.Object r3 = r2.next()
            java.util.Map$Entry r3 = (java.util.Map.Entry) r3
            java.lang.Object r4 = r3.getValue()
            org.telegram.messenger.ContactsController$Contact r4 = (org.telegram.messenger.ContactsController.Contact) r4
            r5 = 0
        L_0x0028:
            java.util.ArrayList<java.lang.String> r6 = r4.shortPhones
            int r6 = r6.size()
            if (r5 >= r6) goto L_0x003e
            java.util.ArrayList<java.lang.String> r6 = r4.shortPhones
            java.lang.Object r6 = r6.get(r5)
            java.lang.String r6 = (java.lang.String) r6
            r12.put(r6, r4)
            int r5 = r5 + 1
            goto L_0x0028
        L_0x003e:
            goto L_0x0015
        L_0x003f:
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r2 == 0) goto L_0x0048
            java.lang.String r2 = "start read contacts from phone"
            org.telegram.messenger.FileLog.d(r2)
        L_0x0048:
            if (r36 != 0) goto L_0x004d
            r34.checkContactsInternal()
        L_0x004d:
            java.util.HashMap r11 = r34.readContactsFromPhoneBook()
            java.util.HashMap r2 = new java.util.HashMap
            r2.<init>()
            r10 = r2
            java.util.HashMap r2 = new java.util.HashMap
            r2.<init>()
            r9 = r2
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            r8 = r2
            java.util.Set r2 = r11.entrySet()
            java.util.Iterator r2 = r2.iterator()
        L_0x006b:
            boolean r3 = r2.hasNext()
            if (r3 == 0) goto L_0x00cf
            java.lang.Object r3 = r2.next()
            java.util.Map$Entry r3 = (java.util.Map.Entry) r3
            java.lang.Object r4 = r3.getValue()
            org.telegram.messenger.ContactsController$Contact r4 = (org.telegram.messenger.ContactsController.Contact) r4
            r5 = 0
            java.util.ArrayList<java.lang.String> r6 = r4.shortPhones
            int r6 = r6.size()
        L_0x0084:
            if (r5 >= r6) goto L_0x00ab
            java.util.ArrayList<java.lang.String> r7 = r4.shortPhones
            java.lang.Object r7 = r7.get(r5)
            java.lang.String r7 = (java.lang.String) r7
            int r17 = r7.length()
            r18 = r0
            int r0 = r17 + -7
            r17 = r1
            r1 = 0
            int r0 = java.lang.Math.max(r1, r0)
            java.lang.String r0 = r7.substring(r0)
            r9.put(r0, r4)
            int r5 = r5 + 1
            r1 = r17
            r0 = r18
            goto L_0x0084
        L_0x00ab:
            r18 = r0
            r17 = r1
            java.lang.String r0 = r4.getLetter()
            java.lang.Object r1 = r10.get(r0)
            java.util.ArrayList r1 = (java.util.ArrayList) r1
            if (r1 != 0) goto L_0x00c7
            java.util.ArrayList r5 = new java.util.ArrayList
            r5.<init>()
            r1 = r5
            r10.put(r0, r1)
            r8.add(r0)
        L_0x00c7:
            r1.add(r4)
            r1 = r17
            r0 = r18
            goto L_0x006b
        L_0x00cf:
            r18 = r0
            r17 = r1
            java.util.HashMap r0 = new java.util.HashMap
            r0.<init>()
            r7 = r0
            int r6 = r35.size()
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r5 = r0
            boolean r0 = r35.isEmpty()
            java.lang.String r2 = ""
            if (r0 != 0) goto L_0x0447
            java.util.Set r0 = r11.entrySet()
            java.util.Iterator r0 = r0.iterator()
        L_0x00f3:
            boolean r4 = r0.hasNext()
            if (r4 == 0) goto L_0x03dc
            java.lang.Object r4 = r0.next()
            java.util.Map$Entry r4 = (java.util.Map.Entry) r4
            java.lang.Object r19 = r4.getKey()
            r3 = r19
            java.lang.String r3 = (java.lang.String) r3
            java.lang.Object r19 = r4.getValue()
            r1 = r19
            org.telegram.messenger.ContactsController$Contact r1 = (org.telegram.messenger.ContactsController.Contact) r1
            java.lang.Object r19 = r14.get(r3)
            org.telegram.messenger.ContactsController$Contact r19 = (org.telegram.messenger.ContactsController.Contact) r19
            if (r19 != 0) goto L_0x0143
            r22 = 0
            r23 = r0
            r0 = r22
        L_0x011d:
            r22 = r2
            java.util.ArrayList<java.lang.String> r2 = r1.shortPhones
            int r2 = r2.size()
            if (r0 >= r2) goto L_0x0147
            java.util.ArrayList<java.lang.String> r2 = r1.shortPhones
            java.lang.Object r2 = r2.get(r0)
            java.lang.Object r2 = r12.get(r2)
            org.telegram.messenger.ContactsController$Contact r2 = (org.telegram.messenger.ContactsController.Contact) r2
            if (r2 == 0) goto L_0x013c
            r19 = r2
            r24 = r2
            java.lang.String r3 = r2.key
            goto L_0x0149
        L_0x013c:
            r24 = r2
            int r0 = r0 + 1
            r2 = r22
            goto L_0x011d
        L_0x0143:
            r23 = r0
            r22 = r2
        L_0x0147:
            r2 = r19
        L_0x0149:
            if (r2 == 0) goto L_0x014f
            int r0 = r2.imported
            r1.imported = r0
        L_0x014f:
            if (r2 == 0) goto L_0x017c
            java.lang.String r0 = r1.first_name
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0166
            java.lang.String r0 = r2.first_name
            r19 = r4
            java.lang.String r4 = r1.first_name
            boolean r0 = r0.equals(r4)
            if (r0 == 0) goto L_0x017a
            goto L_0x0168
        L_0x0166:
            r19 = r4
        L_0x0168:
            java.lang.String r0 = r1.last_name
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x017e
            java.lang.String r0 = r2.last_name
            java.lang.String r4 = r1.last_name
            boolean r0 = r0.equals(r4)
            if (r0 != 0) goto L_0x017e
        L_0x017a:
            r0 = 1
            goto L_0x017f
        L_0x017c:
            r19 = r4
        L_0x017e:
            r0 = 0
        L_0x017f:
            if (r2 == 0) goto L_0x0328
            if (r0 == 0) goto L_0x0193
            r32 = r6
            r31 = r7
            r26 = r8
            r27 = r9
            r30 = r10
            r29 = r11
            r25 = r12
            goto L_0x0336
        L_0x0193:
            r24 = 0
            r4 = r24
        L_0x0197:
            r25 = r12
            java.util.ArrayList<java.lang.String> r12 = r1.phones
            int r12 = r12.size()
            if (r4 >= r12) goto L_0x030d
            java.util.ArrayList<java.lang.String> r12 = r1.shortPhones
            java.lang.Object r12 = r12.get(r4)
            java.lang.String r12 = (java.lang.String) r12
            int r26 = r12.length()
            r27 = r9
            int r9 = r26 + -7
            r26 = r8
            r8 = 0
            int r9 = java.lang.Math.max(r8, r9)
            java.lang.String r8 = r12.substring(r9)
            r7.put(r12, r1)
            java.util.ArrayList<java.lang.String> r9 = r2.shortPhones
            int r9 = r9.indexOf(r12)
            r28 = 0
            if (r37 == 0) goto L_0x0229
            r29 = r9
            java.util.HashMap<java.lang.String, org.telegram.tgnet.TLRPC$TL_contact> r9 = r13.contactsByPhone
            java.lang.Object r9 = r9.get(r12)
            org.telegram.tgnet.TLRPC$TL_contact r9 = (org.telegram.tgnet.TLRPC.TL_contact) r9
            if (r9 == 0) goto L_0x0216
            r30 = r10
            org.telegram.messenger.MessagesController r10 = r34.getMessagesController()
            r32 = r6
            r31 = r7
            long r6 = r9.user_id
            java.lang.Long r6 = java.lang.Long.valueOf(r6)
            org.telegram.tgnet.TLRPC$User r6 = r10.getUser(r6)
            if (r6 == 0) goto L_0x0213
            int r17 = r17 + 1
            java.lang.String r7 = r6.first_name
            boolean r7 = android.text.TextUtils.isEmpty(r7)
            if (r7 == 0) goto L_0x0213
            java.lang.String r7 = r6.last_name
            boolean r7 = android.text.TextUtils.isEmpty(r7)
            if (r7 == 0) goto L_0x0213
            java.lang.String r7 = r1.first_name
            boolean r7 = android.text.TextUtils.isEmpty(r7)
            if (r7 == 0) goto L_0x020d
            java.lang.String r7 = r1.last_name
            boolean r7 = android.text.TextUtils.isEmpty(r7)
            if (r7 != 0) goto L_0x0213
        L_0x020d:
            r7 = -1
            r10 = 1
            r29 = r7
            r28 = r10
        L_0x0213:
            r9 = r29
            goto L_0x0233
        L_0x0216:
            r32 = r6
            r31 = r7
            r30 = r10
            java.util.HashMap<java.lang.String, org.telegram.tgnet.TLRPC$TL_contact> r6 = r13.contactsByShortPhone
            boolean r6 = r6.containsKey(r8)
            if (r6 == 0) goto L_0x0231
            int r17 = r17 + 1
            r9 = r29
            goto L_0x0233
        L_0x0229:
            r32 = r6
            r31 = r7
            r29 = r9
            r30 = r10
        L_0x0231:
            r9 = r29
        L_0x0233:
            r6 = -1
            if (r9 != r6) goto L_0x02d6
            if (r37 == 0) goto L_0x02d1
            if (r28 != 0) goto L_0x02a3
            java.util.HashMap<java.lang.String, org.telegram.tgnet.TLRPC$TL_contact> r6 = r13.contactsByPhone
            java.lang.Object r6 = r6.get(r12)
            org.telegram.tgnet.TLRPC$TL_contact r6 = (org.telegram.tgnet.TLRPC.TL_contact) r6
            if (r6 == 0) goto L_0x0294
            org.telegram.messenger.MessagesController r7 = r34.getMessagesController()
            r29 = r11
            long r10 = r6.user_id
            java.lang.Long r10 = java.lang.Long.valueOf(r10)
            org.telegram.tgnet.TLRPC$User r7 = r7.getUser(r10)
            if (r7 == 0) goto L_0x028f
            int r17 = r17 + 1
            java.lang.String r10 = r7.first_name
            if (r10 == 0) goto L_0x025f
            java.lang.String r10 = r7.first_name
            goto L_0x0261
        L_0x025f:
            r10 = r22
        L_0x0261:
            java.lang.String r11 = r7.last_name
            if (r11 == 0) goto L_0x0268
            java.lang.String r11 = r7.last_name
            goto L_0x026a
        L_0x0268:
            r11 = r22
        L_0x026a:
            r33 = r6
            java.lang.String r6 = r1.first_name
            boolean r6 = r10.equals(r6)
            if (r6 == 0) goto L_0x027c
            java.lang.String r6 = r1.last_name
            boolean r6 = r11.equals(r6)
            if (r6 != 0) goto L_0x02fb
        L_0x027c:
            java.lang.String r6 = r1.first_name
            boolean r6 = android.text.TextUtils.isEmpty(r6)
            if (r6 == 0) goto L_0x028e
            java.lang.String r6 = r1.last_name
            boolean r6 = android.text.TextUtils.isEmpty(r6)
            if (r6 == 0) goto L_0x028e
            goto L_0x02fb
        L_0x028e:
            goto L_0x0293
        L_0x028f:
            r33 = r6
            int r18 = r18 + 1
        L_0x0293:
            goto L_0x02a5
        L_0x0294:
            r33 = r6
            r29 = r11
            java.util.HashMap<java.lang.String, org.telegram.tgnet.TLRPC$TL_contact> r6 = r13.contactsByShortPhone
            boolean r6 = r6.containsKey(r8)
            if (r6 == 0) goto L_0x0293
            int r17 = r17 + 1
            goto L_0x02a5
        L_0x02a3:
            r29 = r11
        L_0x02a5:
            org.telegram.tgnet.TLRPC$TL_inputPhoneContact r6 = new org.telegram.tgnet.TLRPC$TL_inputPhoneContact
            r6.<init>()
            int r7 = r1.contact_id
            long r10 = (long) r7
            r6.client_id = r10
            long r10 = r6.client_id
            r33 = r8
            long r7 = (long) r4
            r21 = 32
            long r7 = r7 << r21
            long r7 = r7 | r10
            r6.client_id = r7
            java.lang.String r7 = r1.first_name
            r6.first_name = r7
            java.lang.String r7 = r1.last_name
            r6.last_name = r7
            java.util.ArrayList<java.lang.String> r7 = r1.phones
            java.lang.Object r7 = r7.get(r4)
            java.lang.String r7 = (java.lang.String) r7
            r6.phone = r7
            r5.add(r6)
            goto L_0x02fb
        L_0x02d1:
            r33 = r8
            r29 = r11
            goto L_0x02fb
        L_0x02d6:
            r33 = r8
            r29 = r11
            java.util.ArrayList<java.lang.Integer> r6 = r1.phoneDeleted
            java.util.ArrayList<java.lang.Integer> r7 = r2.phoneDeleted
            java.lang.Object r7 = r7.get(r9)
            java.lang.Integer r7 = (java.lang.Integer) r7
            r6.set(r4, r7)
            java.util.ArrayList<java.lang.String> r6 = r2.phones
            r6.remove(r9)
            java.util.ArrayList<java.lang.String> r6 = r2.shortPhones
            r6.remove(r9)
            java.util.ArrayList<java.lang.Integer> r6 = r2.phoneDeleted
            r6.remove(r9)
            java.util.ArrayList<java.lang.String> r6 = r2.phoneTypes
            r6.remove(r9)
        L_0x02fb:
            int r4 = r4 + 1
            r12 = r25
            r8 = r26
            r9 = r27
            r11 = r29
            r10 = r30
            r7 = r31
            r6 = r32
            goto L_0x0197
        L_0x030d:
            r32 = r6
            r31 = r7
            r26 = r8
            r27 = r9
            r30 = r10
            r29 = r11
            java.util.ArrayList<java.lang.String> r4 = r2.phones
            boolean r4 = r4.isEmpty()
            if (r4 == 0) goto L_0x0324
            r14.remove(r3)
        L_0x0324:
            r8 = r31
            goto L_0x03c9
        L_0x0328:
            r32 = r6
            r31 = r7
            r26 = r8
            r27 = r9
            r30 = r10
            r29 = r11
            r25 = r12
        L_0x0336:
            r4 = 0
        L_0x0337:
            java.util.ArrayList<java.lang.String> r6 = r1.phones
            int r6 = r6.size()
            if (r4 >= r6) goto L_0x03c2
            java.util.ArrayList<java.lang.String> r6 = r1.shortPhones
            java.lang.Object r6 = r6.get(r4)
            java.lang.String r6 = (java.lang.String) r6
            int r7 = r6.length()
            int r7 = r7 + -7
            r8 = 0
            int r7 = java.lang.Math.max(r8, r7)
            java.lang.String r7 = r6.substring(r7)
            r8 = r31
            r8.put(r6, r1)
            if (r2 == 0) goto L_0x037b
            java.util.ArrayList<java.lang.String> r9 = r2.shortPhones
            int r9 = r9.indexOf(r6)
            r10 = -1
            if (r9 == r10) goto L_0x037b
            java.util.ArrayList<java.lang.Integer> r11 = r2.phoneDeleted
            java.lang.Object r11 = r11.get(r9)
            java.lang.Integer r11 = (java.lang.Integer) r11
            java.util.ArrayList<java.lang.Integer> r12 = r1.phoneDeleted
            r12.set(r4, r11)
            int r12 = r11.intValue()
            r10 = 1
            if (r12 != r10) goto L_0x037b
            goto L_0x03bc
        L_0x037b:
            if (r37 == 0) goto L_0x03b9
            if (r0 != 0) goto L_0x038c
            java.util.HashMap<java.lang.String, org.telegram.tgnet.TLRPC$TL_contact> r9 = r13.contactsByPhone
            boolean r9 = r9.containsKey(r6)
            if (r9 == 0) goto L_0x038a
            int r17 = r17 + 1
            goto L_0x03bc
        L_0x038a:
            int r18 = r18 + 1
        L_0x038c:
            org.telegram.tgnet.TLRPC$TL_inputPhoneContact r9 = new org.telegram.tgnet.TLRPC$TL_inputPhoneContact
            r9.<init>()
            int r10 = r1.contact_id
            long r10 = (long) r10
            r9.client_id = r10
            long r10 = r9.client_id
            r12 = r6
            r28 = r7
            long r6 = (long) r4
            r21 = 32
            long r6 = r6 << r21
            long r6 = r6 | r10
            r9.client_id = r6
            java.lang.String r6 = r1.first_name
            r9.first_name = r6
            java.lang.String r6 = r1.last_name
            r9.last_name = r6
            java.util.ArrayList<java.lang.String> r6 = r1.phones
            java.lang.Object r6 = r6.get(r4)
            java.lang.String r6 = (java.lang.String) r6
            r9.phone = r6
            r5.add(r9)
            goto L_0x03bc
        L_0x03b9:
            r12 = r6
            r28 = r7
        L_0x03bc:
            int r4 = r4 + 1
            r31 = r8
            goto L_0x0337
        L_0x03c2:
            r8 = r31
            if (r2 == 0) goto L_0x03c9
            r14.remove(r3)
        L_0x03c9:
            r7 = r8
            r2 = r22
            r0 = r23
            r12 = r25
            r8 = r26
            r9 = r27
            r11 = r29
            r10 = r30
            r6 = r32
            goto L_0x00f3
        L_0x03dc:
            r32 = r6
            r26 = r8
            r27 = r9
            r30 = r10
            r29 = r11
            r25 = r12
            r8 = r7
            if (r38 != 0) goto L_0x0409
            boolean r0 = r35.isEmpty()
            if (r0 == 0) goto L_0x0409
            boolean r0 = r5.isEmpty()
            if (r0 == 0) goto L_0x0409
            int r0 = r29.size()
            r6 = r32
            if (r6 != r0) goto L_0x040b
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0408
            java.lang.String r0 = "contacts not changed!"
            org.telegram.messenger.FileLog.d(r0)
        L_0x0408:
            return
        L_0x0409:
            r6 = r32
        L_0x040b:
            if (r37 == 0) goto L_0x043d
            boolean r0 = r35.isEmpty()
            if (r0 != 0) goto L_0x043d
            boolean r0 = r29.isEmpty()
            if (r0 != 0) goto L_0x043d
            boolean r0 = r5.isEmpty()
            if (r0 == 0) goto L_0x042a
            org.telegram.messenger.MessagesStorage r0 = r34.getMessagesStorage()
            r11 = r29
            r1 = 0
            r0.putCachedPhoneBook(r11, r1, r1)
            goto L_0x042c
        L_0x042a:
            r11 = r29
        L_0x042c:
            if (r15 != 0) goto L_0x043f
            boolean r0 = r35.isEmpty()
            if (r0 != 0) goto L_0x043f
            org.telegram.messenger.ContactsController$$ExternalSyntheticLambda17 r0 = new org.telegram.messenger.ContactsController$$ExternalSyntheticLambda17
            r0.<init>(r13, r14)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
            goto L_0x043f
        L_0x043d:
            r11 = r29
        L_0x043f:
            r23 = r15
            r15 = r17
            r14 = r18
            goto L_0x054e
        L_0x0447:
            r22 = r2
            r26 = r8
            r27 = r9
            r30 = r10
            r25 = r12
            r8 = r7
            if (r37 == 0) goto L_0x0548
            java.util.Set r0 = r11.entrySet()
            java.util.Iterator r0 = r0.iterator()
            r1 = r17
        L_0x045e:
            boolean r2 = r0.hasNext()
            if (r2 == 0) goto L_0x0542
            java.lang.Object r2 = r0.next()
            java.util.Map$Entry r2 = (java.util.Map.Entry) r2
            java.lang.Object r3 = r2.getValue()
            org.telegram.messenger.ContactsController$Contact r3 = (org.telegram.messenger.ContactsController.Contact) r3
            java.lang.Object r4 = r2.getKey()
            java.lang.String r4 = (java.lang.String) r4
            r7 = 0
        L_0x0477:
            java.util.ArrayList<java.lang.String> r9 = r3.phones
            int r9 = r9.size()
            if (r7 >= r9) goto L_0x0538
            if (r39 != 0) goto L_0x0502
            java.util.ArrayList<java.lang.String> r9 = r3.shortPhones
            java.lang.Object r9 = r9.get(r7)
            java.lang.String r9 = (java.lang.String) r9
            int r10 = r9.length()
            int r10 = r10 + -7
            r12 = 0
            int r10 = java.lang.Math.max(r12, r10)
            java.lang.String r10 = r9.substring(r10)
            java.util.HashMap<java.lang.String, org.telegram.tgnet.TLRPC$TL_contact> r12 = r13.contactsByPhone
            java.lang.Object r12 = r12.get(r9)
            org.telegram.tgnet.TLRPC$TL_contact r12 = (org.telegram.tgnet.TLRPC.TL_contact) r12
            if (r12 == 0) goto L_0x04f3
            r19 = r0
            org.telegram.messenger.MessagesController r0 = r34.getMessagesController()
            r23 = r15
            long r14 = r12.user_id
            java.lang.Long r14 = java.lang.Long.valueOf(r14)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r14)
            if (r0 == 0) goto L_0x04f0
            int r1 = r1 + 1
            java.lang.String r14 = r0.first_name
            if (r14 == 0) goto L_0x04bf
            java.lang.String r14 = r0.first_name
            goto L_0x04c1
        L_0x04bf:
            r14 = r22
        L_0x04c1:
            java.lang.String r15 = r0.last_name
            if (r15 == 0) goto L_0x04c8
            java.lang.String r15 = r0.last_name
            goto L_0x04ca
        L_0x04c8:
            r15 = r22
        L_0x04ca:
            r17 = r0
            java.lang.String r0 = r3.first_name
            boolean r0 = r14.equals(r0)
            if (r0 == 0) goto L_0x04dc
            java.lang.String r0 = r3.last_name
            boolean r0 = r15.equals(r0)
            if (r0 != 0) goto L_0x04ed
        L_0x04dc:
            java.lang.String r0 = r3.first_name
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x04f2
            java.lang.String r0 = r3.last_name
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x04f2
        L_0x04ed:
            r12 = 32
            goto L_0x052e
        L_0x04f0:
            r17 = r0
        L_0x04f2:
            goto L_0x0506
        L_0x04f3:
            r19 = r0
            r23 = r15
            java.util.HashMap<java.lang.String, org.telegram.tgnet.TLRPC$TL_contact> r0 = r13.contactsByShortPhone
            boolean r0 = r0.containsKey(r10)
            if (r0 == 0) goto L_0x04f2
            int r1 = r1 + 1
            goto L_0x0506
        L_0x0502:
            r19 = r0
            r23 = r15
        L_0x0506:
            org.telegram.tgnet.TLRPC$TL_inputPhoneContact r0 = new org.telegram.tgnet.TLRPC$TL_inputPhoneContact
            r0.<init>()
            int r9 = r3.contact_id
            long r9 = (long) r9
            r0.client_id = r9
            long r9 = r0.client_id
            long r14 = (long) r7
            r12 = 32
            long r14 = r14 << r12
            long r9 = r9 | r14
            r0.client_id = r9
            java.lang.String r9 = r3.first_name
            r0.first_name = r9
            java.lang.String r9 = r3.last_name
            r0.last_name = r9
            java.util.ArrayList<java.lang.String> r9 = r3.phones
            java.lang.Object r9 = r9.get(r7)
            java.lang.String r9 = (java.lang.String) r9
            r0.phone = r9
            r5.add(r0)
        L_0x052e:
            int r7 = r7 + 1
            r14 = r35
            r0 = r19
            r15 = r23
            goto L_0x0477
        L_0x0538:
            r19 = r0
            r23 = r15
            r12 = 32
            r14 = r35
            goto L_0x045e
        L_0x0542:
            r23 = r15
            r15 = r1
            r14 = r18
            goto L_0x054e
        L_0x0548:
            r23 = r15
            r15 = r17
            r14 = r18
        L_0x054e:
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0557
            java.lang.String r0 = "done processing contacts"
            org.telegram.messenger.FileLog.d(r0)
        L_0x0557:
            if (r37 == 0) goto L_0x06f6
            boolean r0 = r5.isEmpty()
            if (r0 != 0) goto L_0x06ca
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0568
            java.lang.String r0 = "start import contacts"
            org.telegram.messenger.FileLog.e((java.lang.String) r0)
        L_0x0568:
            if (r40 == 0) goto L_0x0593
            if (r14 == 0) goto L_0x0593
            r0 = 30
            if (r14 < r0) goto L_0x0574
            r0 = 1
            r17 = r0
            goto L_0x0596
        L_0x0574:
            if (r38 == 0) goto L_0x058f
            if (r6 != 0) goto L_0x058f
            java.util.HashMap<java.lang.String, org.telegram.tgnet.TLRPC$TL_contact> r0 = r13.contactsByPhone
            int r0 = r0.size()
            int r0 = r0 - r15
            java.util.HashMap<java.lang.String, org.telegram.tgnet.TLRPC$TL_contact> r1 = r13.contactsByPhone
            int r1 = r1.size()
            int r1 = r1 / 3
            int r1 = r1 * 2
            if (r0 <= r1) goto L_0x058f
            r0 = 2
            r17 = r0
            goto L_0x0596
        L_0x058f:
            r0 = 0
            r17 = r0
            goto L_0x0596
        L_0x0593:
            r0 = 0
            r17 = r0
        L_0x0596:
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x05c4
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "new phone book contacts "
            r0.append(r1)
            r0.append(r14)
            java.lang.String r1 = " serverContactsInPhonebook "
            r0.append(r1)
            r0.append(r15)
            java.lang.String r1 = " totalContacts "
            r0.append(r1)
            java.util.HashMap<java.lang.String, org.telegram.tgnet.TLRPC$TL_contact> r1 = r13.contactsByPhone
            int r1 = r1.size()
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
        L_0x05c4:
            if (r17 == 0) goto L_0x05db
            org.telegram.messenger.ContactsController$$ExternalSyntheticLambda3 r7 = new org.telegram.messenger.ContactsController$$ExternalSyntheticLambda3
            r0 = r7
            r1 = r34
            r2 = r17
            r3 = r35
            r4 = r38
            r12 = r5
            r5 = r36
            r0.<init>(r1, r2, r3, r4, r5)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r7)
            return
        L_0x05db:
            r12 = r5
            if (r41 == 0) goto L_0x05fa
            org.telegram.messenger.DispatchQueue r9 = org.telegram.messenger.Utilities.stageQueue
            org.telegram.messenger.ContactsController$$ExternalSyntheticLambda25 r10 = new org.telegram.messenger.ContactsController$$ExternalSyntheticLambda25
            r0 = r10
            r1 = r34
            r2 = r8
            r3 = r11
            r4 = r38
            r5 = r30
            r18 = r6
            r6 = r26
            r19 = r8
            r7 = r27
            r0.<init>(r1, r2, r3, r4, r5, r6, r7)
            r9.postRunnable(r10)
            return
        L_0x05fa:
            r18 = r6
            r19 = r8
            r0 = 1
            boolean[] r4 = new boolean[r0]
            r0 = 0
            r4[r0] = r0
            java.util.HashMap r2 = new java.util.HashMap
            r2.<init>(r11)
            android.util.SparseArray r0 = new android.util.SparseArray
            r0.<init>()
            r10 = r0
            java.util.Set r0 = r2.entrySet()
            java.util.Iterator r0 = r0.iterator()
        L_0x0617:
            boolean r1 = r0.hasNext()
            if (r1 == 0) goto L_0x0631
            java.lang.Object r1 = r0.next()
            java.util.Map$Entry r1 = (java.util.Map.Entry) r1
            java.lang.Object r3 = r1.getValue()
            org.telegram.messenger.ContactsController$Contact r3 = (org.telegram.messenger.ContactsController.Contact) r3
            int r5 = r3.contact_id
            java.lang.String r6 = r3.key
            r10.put(r5, r6)
            goto L_0x0617
        L_0x0631:
            r7 = 0
            r13.completedRequestsCount = r7
            int r0 = r12.size()
            double r0 = (double) r0
            r5 = 4647503709213818880(0x407fNUM, double:500.0)
            java.lang.Double.isNaN(r0)
            double r0 = r0 / r5
            double r0 = java.lang.Math.ceil(r0)
            int r9 = (int) r0
            r0 = 0
            r8 = r0
        L_0x0649:
            if (r8 >= r9) goto L_0x06b4
            org.telegram.tgnet.TLRPC$TL_contacts_importContacts r0 = new org.telegram.tgnet.TLRPC$TL_contacts_importContacts
            r0.<init>()
            r7 = r0
            int r6 = r8 * 500
            int r0 = r6 + 500
            int r1 = r12.size()
            int r5 = java.lang.Math.min(r0, r1)
            java.util.ArrayList r0 = new java.util.ArrayList
            java.util.List r1 = r12.subList(r6, r5)
            r0.<init>(r1)
            r7.contacts = r0
            org.telegram.tgnet.ConnectionsManager r3 = r34.getConnectionsManager()
            org.telegram.messenger.ContactsController$$ExternalSyntheticLambda57 r1 = new org.telegram.messenger.ContactsController$$ExternalSyntheticLambda57
            r0 = r1
            r13 = r1
            r1 = r34
            r16 = r14
            r14 = r3
            r3 = r10
            r20 = r5
            r5 = r11
            r21 = r6
            r6 = r7
            r22 = r15
            r15 = r7
            r7 = r9
            r24 = r26
            r26 = r8
            r8 = r19
            r28 = r9
            r9 = r38
            r29 = r30
            r30 = r10
            r10 = r29
            r31 = r11
            r11 = r24
            r32 = r12
            r12 = r27
            r0.<init>(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12)
            r0 = 6
            r14.sendRequest(r15, r13, r0)
            int r8 = r26 + 1
            r13 = r34
            r14 = r16
            r15 = r22
            r26 = r24
            r9 = r28
            r10 = r30
            r11 = r31
            r12 = r32
            r30 = r29
            goto L_0x0649
        L_0x06b4:
            r28 = r9
            r31 = r11
            r32 = r12
            r16 = r14
            r22 = r15
            r24 = r26
            r29 = r30
            r26 = r8
            r30 = r10
            r1 = r31
            goto L_0x0733
        L_0x06ca:
            r32 = r5
            r18 = r6
            r19 = r8
            r31 = r11
            r16 = r14
            r22 = r15
            r24 = r26
            r29 = r30
            org.telegram.messenger.DispatchQueue r8 = org.telegram.messenger.Utilities.stageQueue
            org.telegram.messenger.ContactsController$$ExternalSyntheticLambda26 r9 = new org.telegram.messenger.ContactsController$$ExternalSyntheticLambda26
            r0 = r9
            r1 = r34
            r2 = r19
            r3 = r31
            r4 = r38
            r5 = r29
            r6 = r24
            r7 = r27
            r0.<init>(r1, r2, r3, r4, r5, r6, r7)
            r8.postRunnable(r9)
            r1 = r31
            goto L_0x0733
        L_0x06f6:
            r32 = r5
            r18 = r6
            r19 = r8
            r31 = r11
            r16 = r14
            r22 = r15
            r24 = r26
            r29 = r30
            r7 = 0
            org.telegram.messenger.DispatchQueue r8 = org.telegram.messenger.Utilities.stageQueue
            org.telegram.messenger.ContactsController$$ExternalSyntheticLambda27 r9 = new org.telegram.messenger.ContactsController$$ExternalSyntheticLambda27
            r0 = r9
            r1 = r34
            r2 = r19
            r3 = r31
            r4 = r38
            r5 = r29
            r6 = r24
            r10 = 0
            r7 = r27
            r0.<init>(r1, r2, r3, r4, r5, r6, r7)
            r8.postRunnable(r9)
            boolean r0 = r31.isEmpty()
            if (r0 != 0) goto L_0x0731
            org.telegram.messenger.MessagesStorage r0 = r34.getMessagesStorage()
            r1 = r31
            r0.putCachedPhoneBook(r1, r10, r10)
            goto L_0x0733
        L_0x0731:
            r1 = r31
        L_0x0733:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ContactsController.m42x3cf1c3d6(java.util.HashMap, boolean, boolean, boolean, boolean, boolean, boolean):void");
    }

    /* renamed from: lambda$performSyncPhoneBook$12$org-telegram-messenger-ContactsController  reason: not valid java name */
    public /* synthetic */ void m30xavar_cf5(HashMap contactHashMap) {
        ArrayList<TLRPC.User> toDelete = new ArrayList<>();
        if (contactHashMap != null && !contactHashMap.isEmpty()) {
            try {
                HashMap<String, TLRPC.User> contactsPhonesShort = new HashMap<>();
                for (int a = 0; a < this.contacts.size(); a++) {
                    TLRPC.User user = getMessagesController().getUser(Long.valueOf(this.contacts.get(a).user_id));
                    if (user != null) {
                        if (!TextUtils.isEmpty(user.phone)) {
                            contactsPhonesShort.put(user.phone, user);
                        }
                    }
                }
                int removed = 0;
                for (Map.Entry<String, Contact> entry : contactHashMap.entrySet()) {
                    Contact contact = entry.getValue();
                    boolean was = false;
                    int a2 = 0;
                    while (a2 < contact.shortPhones.size()) {
                        TLRPC.User user2 = contactsPhonesShort.get(contact.shortPhones.get(a2));
                        if (user2 != null) {
                            was = true;
                            toDelete.add(user2);
                            contact.shortPhones.remove(a2);
                            a2--;
                        }
                        a2++;
                    }
                    if (!was || contact.shortPhones.size() == 0) {
                        removed++;
                    }
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
        if (!toDelete.isEmpty()) {
            deleteContact(toDelete, false);
        }
    }

    /* renamed from: lambda$performSyncPhoneBook$13$org-telegram-messenger-ContactsController  reason: not valid java name */
    public /* synthetic */ void m31x94c3ebb6(int checkType, HashMap contactHashMap, boolean first, boolean schedule) {
        getNotificationCenter().postNotificationName(NotificationCenter.hasNewContactsToImport, Integer.valueOf(checkType), contactHashMap, Boolean.valueOf(first), Boolean.valueOf(schedule));
    }

    /* renamed from: lambda$performSyncPhoneBook$15$org-telegram-messenger-ContactsController  reason: not valid java name */
    public /* synthetic */ void m33x5var_CLASSNAME(HashMap contactsBookShort, HashMap contactsMap, boolean first, HashMap phoneBookSectionsDictFinal, ArrayList phoneBookSectionsArrayFinal, HashMap phoneBookByShortPhonesFinal) {
        this.contactsBookSPhones = contactsBookShort;
        this.contactsBook = contactsMap;
        this.contactsSyncInProgress = false;
        this.contactsBookLoaded = true;
        if (first) {
            this.contactsLoaded = true;
        }
        if (!this.delayedContactsUpdate.isEmpty() && this.contactsLoaded) {
            applyContactsUpdates(this.delayedContactsUpdate, (ConcurrentHashMap<Long, TLRPC.User>) null, (ArrayList<TLRPC.TL_contact>) null, (ArrayList<Long>) null);
            this.delayedContactsUpdate.clear();
        }
        getMessagesStorage().putCachedPhoneBook(contactsMap, false, false);
        AndroidUtilities.runOnUIThread(new ContactsController$$ExternalSyntheticLambda18(this, phoneBookSectionsDictFinal, phoneBookSectionsArrayFinal, phoneBookByShortPhonesFinal));
    }

    /* renamed from: lambda$performSyncPhoneBook$14$org-telegram-messenger-ContactsController  reason: not valid java name */
    public /* synthetic */ void m32x7a055a77(HashMap phoneBookSectionsDictFinal, ArrayList phoneBookSectionsArrayFinal, HashMap phoneBookByShortPhonesFinal) {
        m40x726ee654(phoneBookSectionsDictFinal, phoneBookSectionsArrayFinal, phoneBookByShortPhonesFinal);
        updateUnregisteredContacts();
        getNotificationCenter().postNotificationName(NotificationCenter.contactsDidLoad, new Object[0]);
        getNotificationCenter().postNotificationName(NotificationCenter.contactsImported, new Object[0]);
    }

    /* renamed from: lambda$performSyncPhoneBook$19$org-telegram-messenger-ContactsController  reason: not valid java name */
    public /* synthetic */ void m37xvar_CLASSNAMEc(HashMap contactsMapToSave, SparseArray contactIdToKey, boolean[] hasErrors, HashMap contactsMap, TLRPC.TL_contacts_importContacts req, int count, HashMap contactsBookShort, boolean first, HashMap phoneBookSectionsDictFinal, ArrayList phoneBookSectionsArrayFinal, HashMap phoneBookByShortPhonesFinal, TLObject response, TLRPC.TL_error error) {
        HashMap hashMap = contactsMapToSave;
        SparseArray sparseArray = contactIdToKey;
        TLRPC.TL_contacts_importContacts tL_contacts_importContacts = req;
        TLRPC.TL_error tL_error = error;
        this.completedRequestsCount++;
        if (tL_error == null) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("contacts imported");
            }
            TLRPC.TL_contacts_importedContacts res = (TLRPC.TL_contacts_importedContacts) response;
            if (!res.retry_contacts.isEmpty()) {
                for (int a1 = 0; a1 < res.retry_contacts.size(); a1++) {
                    hashMap.remove(sparseArray.get((int) res.retry_contacts.get(a1).longValue()));
                }
                hasErrors[0] = true;
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("result has retry contacts");
                }
            }
            for (int a12 = 0; a12 < res.popular_invites.size(); a12++) {
                TLRPC.TL_popularContact popularContact = res.popular_invites.get(a12);
                Contact contact = (Contact) contactsMap.get(sparseArray.get((int) popularContact.client_id));
                if (contact != null) {
                    contact.imported = popularContact.importers;
                }
            }
            HashMap hashMap2 = contactsMap;
            getMessagesStorage().putUsersAndChats(res.users, (ArrayList<TLRPC.Chat>) null, true, true);
            ArrayList<TLRPC.TL_contact> cArr = new ArrayList<>();
            for (int a13 = 0; a13 < res.imported.size(); a13++) {
                TLRPC.TL_contact contact2 = new TLRPC.TL_contact();
                contact2.user_id = res.imported.get(a13).user_id;
                cArr.add(contact2);
            }
            processLoadedContacts(cArr, res.users, 2);
        } else {
            HashMap hashMap3 = contactsMap;
            for (int a14 = 0; a14 < tL_contacts_importContacts.contacts.size(); a14++) {
                hashMap.remove(sparseArray.get((int) tL_contacts_importContacts.contacts.get(a14).client_id));
            }
            hasErrors[0] = true;
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("import contacts error " + tL_error.text);
            }
        }
        if (this.completedRequestsCount == count) {
            if (!contactsMapToSave.isEmpty()) {
                getMessagesStorage().putCachedPhoneBook(hashMap, false, false);
            }
            ContactsController$$ExternalSyntheticLambda28 contactsController$$ExternalSyntheticLambda28 = r0;
            DispatchQueue dispatchQueue = Utilities.stageQueue;
            ContactsController$$ExternalSyntheticLambda28 contactsController$$ExternalSyntheticLambda282 = new ContactsController$$ExternalSyntheticLambda28(this, contactsBookShort, contactsMap, first, phoneBookSectionsDictFinal, phoneBookSectionsArrayFinal, phoneBookByShortPhonesFinal, hasErrors);
            dispatchQueue.postRunnable(contactsController$$ExternalSyntheticLambda28);
        }
    }

    /* renamed from: lambda$performSyncPhoneBook$18$org-telegram-messenger-ContactsController  reason: not valid java name */
    public /* synthetic */ void m36xf0b157b(HashMap contactsBookShort, HashMap contactsMap, boolean first, HashMap phoneBookSectionsDictFinal, ArrayList phoneBookSectionsArrayFinal, HashMap phoneBookByShortPhonesFinal, boolean[] hasErrors) {
        this.contactsBookSPhones = contactsBookShort;
        this.contactsBook = contactsMap;
        this.contactsSyncInProgress = false;
        this.contactsBookLoaded = true;
        if (first) {
            this.contactsLoaded = true;
        }
        if (!this.delayedContactsUpdate.isEmpty() && this.contactsLoaded) {
            applyContactsUpdates(this.delayedContactsUpdate, (ConcurrentHashMap<Long, TLRPC.User>) null, (ArrayList<TLRPC.TL_contact>) null, (ArrayList<Long>) null);
            this.delayedContactsUpdate.clear();
        }
        AndroidUtilities.runOnUIThread(new ContactsController$$ExternalSyntheticLambda19(this, phoneBookSectionsDictFinal, phoneBookSectionsArrayFinal, phoneBookByShortPhonesFinal));
        if (hasErrors[0]) {
            Utilities.globalQueue.postRunnable(new ContactsController$$ExternalSyntheticLambda61(this), 300000);
        }
    }

    /* renamed from: lambda$performSyncPhoneBook$16$org-telegram-messenger-ContactsController  reason: not valid java name */
    public /* synthetic */ void m34x448837f9(HashMap phoneBookSectionsDictFinal, ArrayList phoneBookSectionsArrayFinal, HashMap phoneBookByShortPhonesFinal) {
        m40x726ee654(phoneBookSectionsDictFinal, phoneBookSectionsArrayFinal, phoneBookByShortPhonesFinal);
        getNotificationCenter().postNotificationName(NotificationCenter.contactsImported, new Object[0]);
    }

    /* renamed from: lambda$performSyncPhoneBook$17$org-telegram-messenger-ContactsController  reason: not valid java name */
    public /* synthetic */ void m35x29c9a6ba() {
        getMessagesStorage().getCachedPhoneBook(true);
    }

    /* renamed from: lambda$performSyncPhoneBook$21$org-telegram-messenger-ContactsController  reason: not valid java name */
    public /* synthetic */ void m39x8d2d7793(HashMap contactsBookShort, HashMap contactsMap, boolean first, HashMap phoneBookSectionsDictFinal, ArrayList phoneBookSectionsArrayFinal, HashMap phoneBookByShortPhonesFinal) {
        this.contactsBookSPhones = contactsBookShort;
        this.contactsBook = contactsMap;
        this.contactsSyncInProgress = false;
        this.contactsBookLoaded = true;
        if (first) {
            this.contactsLoaded = true;
        }
        if (!this.delayedContactsUpdate.isEmpty() && this.contactsLoaded) {
            applyContactsUpdates(this.delayedContactsUpdate, (ConcurrentHashMap<Long, TLRPC.User>) null, (ArrayList<TLRPC.TL_contact>) null, (ArrayList<Long>) null);
            this.delayedContactsUpdate.clear();
        }
        AndroidUtilities.runOnUIThread(new ContactsController$$ExternalSyntheticLambda20(this, phoneBookSectionsDictFinal, phoneBookSectionsArrayFinal, phoneBookByShortPhonesFinal));
    }

    /* renamed from: lambda$performSyncPhoneBook$20$org-telegram-messenger-ContactsController  reason: not valid java name */
    public /* synthetic */ void m38xa7eCLASSNAMEd2(HashMap phoneBookSectionsDictFinal, ArrayList phoneBookSectionsArrayFinal, HashMap phoneBookByShortPhonesFinal) {
        m40x726ee654(phoneBookSectionsDictFinal, phoneBookSectionsArrayFinal, phoneBookByShortPhonesFinal);
        updateUnregisteredContacts();
        getNotificationCenter().postNotificationName(NotificationCenter.contactsDidLoad, new Object[0]);
        getNotificationCenter().postNotificationName(NotificationCenter.contactsImported, new Object[0]);
    }

    /* renamed from: lambda$performSyncPhoneBook$23$org-telegram-messenger-ContactsController  reason: not valid java name */
    public /* synthetic */ void m41x57b05515(HashMap contactsBookShort, HashMap contactsMap, boolean first, HashMap phoneBookSectionsDictFinal, ArrayList phoneBookSectionsArrayFinal, HashMap phoneBookByShortPhonesFinal) {
        this.contactsBookSPhones = contactsBookShort;
        this.contactsBook = contactsMap;
        this.contactsSyncInProgress = false;
        this.contactsBookLoaded = true;
        if (first) {
            this.contactsLoaded = true;
        }
        if (!this.delayedContactsUpdate.isEmpty() && this.contactsLoaded) {
            applyContactsUpdates(this.delayedContactsUpdate, (ConcurrentHashMap<Long, TLRPC.User>) null, (ArrayList<TLRPC.TL_contact>) null, (ArrayList<Long>) null);
            this.delayedContactsUpdate.clear();
        }
        AndroidUtilities.runOnUIThread(new ContactsController$$ExternalSyntheticLambda21(this, phoneBookSectionsDictFinal, phoneBookSectionsArrayFinal, phoneBookByShortPhonesFinal));
    }

    public boolean isLoadingContacts() {
        boolean z;
        synchronized (this.loadContactsSync) {
            z = this.loadingContacts;
        }
        return z;
    }

    private long getContactsHash(ArrayList<TLRPC.TL_contact> contacts2) {
        long acc = 0;
        ArrayList arrayList = new ArrayList(contacts2);
        Collections.sort(arrayList, ContactsController$$ExternalSyntheticLambda46.INSTANCE);
        int count = arrayList.size();
        for (int a = -1; a < count; a++) {
            if (a == -1) {
                acc = MediaDataController.calcHash(acc, (long) getUserConfig().contactsSavedCount);
            } else {
                acc = MediaDataController.calcHash(acc, ((TLRPC.TL_contact) arrayList.get(a)).user_id);
            }
        }
        return acc;
    }

    static /* synthetic */ int lambda$getContactsHash$25(TLRPC.TL_contact tl_contact, TLRPC.TL_contact tl_contact2) {
        if (tl_contact.user_id > tl_contact2.user_id) {
            return 1;
        }
        if (tl_contact.user_id < tl_contact2.user_id) {
            return -1;
        }
        return 0;
    }

    public void loadContacts(boolean fromCache, long hash) {
        synchronized (this.loadContactsSync) {
            this.loadingContacts = true;
        }
        if (fromCache) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("load contacts from cache");
            }
            getMessagesStorage().getContacts();
            return;
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("load contacts from server");
        }
        TLRPC.TL_contacts_getContacts req = new TLRPC.TL_contacts_getContacts();
        req.hash = hash;
        getConnectionsManager().sendRequest(req, new ContactsController$$ExternalSyntheticLambda52(this, hash));
    }

    /* renamed from: lambda$loadContacts$27$org-telegram-messenger-ContactsController  reason: not valid java name */
    public /* synthetic */ void m19lambda$loadContacts$27$orgtelegrammessengerContactsController(long hash, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            TLRPC.contacts_Contacts res = (TLRPC.contacts_Contacts) response;
            if (hash == 0 || !(res instanceof TLRPC.TL_contacts_contactsNotModified)) {
                getUserConfig().contactsSavedCount = res.saved_count;
                getUserConfig().saveConfig(false);
                processLoadedContacts(res.contacts, res.users, 0);
                return;
            }
            this.contactsLoaded = true;
            if (!this.delayedContactsUpdate.isEmpty() && this.contactsBookLoaded) {
                applyContactsUpdates(this.delayedContactsUpdate, (ConcurrentHashMap<Long, TLRPC.User>) null, (ArrayList<TLRPC.TL_contact>) null, (ArrayList<Long>) null);
                this.delayedContactsUpdate.clear();
            }
            getUserConfig().lastContactsSyncTime = (int) (System.currentTimeMillis() / 1000);
            getUserConfig().saveConfig(false);
            AndroidUtilities.runOnUIThread(new ContactsController$$ExternalSyntheticLambda55(this));
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("load contacts don't change");
            }
        }
    }

    /* renamed from: lambda$loadContacts$26$org-telegram-messenger-ContactsController  reason: not valid java name */
    public /* synthetic */ void m18lambda$loadContacts$26$orgtelegrammessengerContactsController() {
        synchronized (this.loadContactsSync) {
            this.loadingContacts = false;
        }
        getNotificationCenter().postNotificationName(NotificationCenter.contactsDidLoad, new Object[0]);
    }

    public void processLoadedContacts(ArrayList<TLRPC.TL_contact> contactsArr, ArrayList<TLRPC.User> usersArr, int from) {
        AndroidUtilities.runOnUIThread(new ContactsController$$ExternalSyntheticLambda10(this, usersArr, from, contactsArr));
    }

    /* renamed from: lambda$processLoadedContacts$37$org-telegram-messenger-ContactsController  reason: not valid java name */
    public /* synthetic */ void m50x3bbda006(ArrayList usersArr, int from, ArrayList contactsArr) {
        getMessagesController().putUsers(usersArr, from == 1);
        LongSparseArray<TLRPC.User> usersDict = new LongSparseArray<>();
        boolean isEmpty = contactsArr.isEmpty();
        if (from == 2 && !this.contacts.isEmpty()) {
            int a = 0;
            while (a < contactsArr.size()) {
                if (this.contactsDict.get(Long.valueOf(((TLRPC.TL_contact) contactsArr.get(a)).user_id)) != null) {
                    contactsArr.remove(a);
                    a--;
                }
                a++;
            }
            contactsArr.addAll(this.contacts);
        }
        for (int a2 = 0; a2 < contactsArr.size(); a2++) {
            TLRPC.User user = getMessagesController().getUser(Long.valueOf(((TLRPC.TL_contact) contactsArr.get(a2)).user_id));
            if (user != null) {
                usersDict.put(user.id, user);
            }
        }
        Utilities.stageQueue.postRunnable(new ContactsController$$ExternalSyntheticLambda2(this, from, contactsArr, usersDict, usersArr, isEmpty));
    }

    /* renamed from: lambda$processLoadedContacts$36$org-telegram-messenger-ContactsController  reason: not valid java name */
    public /* synthetic */ void m49x567CLASSNAME(int from, ArrayList contactsArr, LongSparseArray usersDict, ArrayList usersArr, boolean isEmpty) {
        HashMap<String, TLRPC.TL_contact> contactsByPhonesDict;
        HashMap<String, TLRPC.TL_contact> contactsByPhonesDict2;
        HashMap<String, TLRPC.TL_contact> contactsByPhonesShortDict;
        HashMap<String, TLRPC.TL_contact> contactsByPhonesDict3;
        String key;
        ArrayList<TLRPC.TL_contact> arr;
        int i = from;
        ArrayList arrayList = contactsArr;
        LongSparseArray longSparseArray = usersDict;
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("done loading contacts");
        }
        if (i == 1 && (contactsArr.isEmpty() || Math.abs((System.currentTimeMillis() / 1000) - ((long) getUserConfig().lastContactsSyncTime)) >= 86400)) {
            loadContacts(false, getContactsHash(arrayList));
            if (contactsArr.isEmpty()) {
                AndroidUtilities.runOnUIThread(new ContactsController$$ExternalSyntheticLambda62(this));
                return;
            }
        }
        if (i == 0) {
            getUserConfig().lastContactsSyncTime = (int) (System.currentTimeMillis() / 1000);
            getUserConfig().saveConfig(false);
        }
        int a = 0;
        while (a < contactsArr.size()) {
            TLRPC.TL_contact contact = (TLRPC.TL_contact) arrayList.get(a);
            if (longSparseArray.get(contact.user_id) != null || contact.user_id == getUserConfig().getClientUserId()) {
                a++;
            } else {
                loadContacts(false, 0);
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("contacts are broken, load from server");
                }
                AndroidUtilities.runOnUIThread(new ContactsController$$ExternalSyntheticLambda63(this));
                return;
            }
        }
        if (i != 1) {
            getMessagesStorage().putUsersAndChats(usersArr, (ArrayList<TLRPC.Chat>) null, true, true);
            getMessagesStorage().putContacts(arrayList, i != 2);
        } else {
            ArrayList arrayList2 = usersArr;
        }
        Collections.sort(arrayList, new ContactsController$$ExternalSyntheticLambda38(longSparseArray));
        ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap(20, 1.0f, 2);
        HashMap<String, ArrayList<TLRPC.TL_contact>> sectionsDict = new HashMap<>();
        HashMap<String, ArrayList<TLRPC.TL_contact>> sectionsDictMutual = new HashMap<>();
        ArrayList<String> sortedSectionsArray = new ArrayList<>();
        ArrayList<String> sortedSectionsArrayMutual = new ArrayList<>();
        HashMap<String, TLRPC.TL_contact> contactsByPhonesShortDict2 = null;
        if (!this.contactsBookLoaded) {
            HashMap<String, TLRPC.TL_contact> contactsByPhonesDict4 = new HashMap<>();
            contactsByPhonesShortDict2 = new HashMap<>();
            contactsByPhonesDict = contactsByPhonesDict4;
        } else {
            contactsByPhonesDict = null;
        }
        HashMap<String, TLRPC.TL_contact> contactsByPhonesDictFinal = contactsByPhonesDict;
        HashMap<String, TLRPC.TL_contact> contactsByPhonesShortDictFinal = contactsByPhonesShortDict2;
        int a2 = 0;
        while (a2 < contactsArr.size()) {
            TLRPC.TL_contact value = (TLRPC.TL_contact) arrayList.get(a2);
            HashMap<String, TLRPC.TL_contact> contactsByPhonesDict5 = contactsByPhonesDict;
            TLRPC.User user = (TLRPC.User) longSparseArray.get(value.user_id);
            if (user == null) {
                contactsByPhonesShortDict = contactsByPhonesShortDict2;
                contactsByPhonesDict3 = contactsByPhonesDict5;
                contactsByPhonesDict2 = contactsByPhonesDictFinal;
            } else {
                concurrentHashMap.put(Long.valueOf(value.user_id), value);
                if (contactsByPhonesDict5 == null || TextUtils.isEmpty(user.phone)) {
                    contactsByPhonesDict3 = contactsByPhonesDict5;
                    contactsByPhonesDict2 = contactsByPhonesDictFinal;
                } else {
                    contactsByPhonesDict3 = contactsByPhonesDict5;
                    contactsByPhonesDict3.put(user.phone, value);
                    contactsByPhonesDict2 = contactsByPhonesDictFinal;
                    contactsByPhonesShortDict2.put(user.phone.substring(Math.max(0, user.phone.length() - 7)), value);
                }
                String key2 = UserObject.getFirstName(user);
                if (key2.length() > 1) {
                    key2 = key2.substring(0, 1);
                }
                if (key2.length() == 0) {
                    key = "#";
                } else {
                    key = key2.toUpperCase();
                }
                String replace = this.sectionsToReplace.get(key);
                if (replace != null) {
                    key = replace;
                }
                ArrayList<TLRPC.TL_contact> arr2 = sectionsDict.get(key);
                if (arr2 == null) {
                    arr = new ArrayList<>();
                    sectionsDict.put(key, arr);
                    sortedSectionsArray.add(key);
                } else {
                    arr = arr2;
                }
                arr.add(value);
                contactsByPhonesShortDict = contactsByPhonesShortDict2;
                if (user.mutual_contact) {
                    ArrayList<TLRPC.TL_contact> arr3 = sectionsDictMutual.get(key);
                    if (arr3 == null) {
                        arr3 = new ArrayList<>();
                        sectionsDictMutual.put(key, arr3);
                        sortedSectionsArrayMutual.add(key);
                    }
                    arr3.add(value);
                }
            }
            a2++;
            arrayList = contactsArr;
            contactsByPhonesDict = contactsByPhonesDict3;
            contactsByPhonesShortDict2 = contactsByPhonesShortDict;
            contactsByPhonesDictFinal = contactsByPhonesDict2;
            int i2 = from;
        }
        HashMap<String, TLRPC.TL_contact> hashMap = contactsByPhonesDict;
        Collections.sort(sortedSectionsArray, ContactsController$$ExternalSyntheticLambda42.INSTANCE);
        Collections.sort(sortedSectionsArrayMutual, ContactsController$$ExternalSyntheticLambda43.INSTANCE);
        HashMap<String, TLRPC.TL_contact> contactsByPhonesDictFinal2 = contactsByPhonesDictFinal;
        HashMap<String, TLRPC.TL_contact> hashMap2 = contactsByPhonesShortDict2;
        HashMap<String, ArrayList<TLRPC.TL_contact>> hashMap3 = sectionsDictMutual;
        HashMap<String, ArrayList<TLRPC.TL_contact>> hashMap4 = sectionsDict;
        ConcurrentHashMap concurrentHashMap2 = concurrentHashMap;
        AndroidUtilities.runOnUIThread(new ContactsController$$ExternalSyntheticLambda15(this, contactsArr, concurrentHashMap, sectionsDict, sectionsDictMutual, sortedSectionsArray, sortedSectionsArrayMutual, from, isEmpty));
        if (!this.delayedContactsUpdate.isEmpty() && this.contactsLoaded && this.contactsBookLoaded) {
            applyContactsUpdates(this.delayedContactsUpdate, (ConcurrentHashMap<Long, TLRPC.User>) null, (ArrayList<TLRPC.TL_contact>) null, (ArrayList<Long>) null);
            this.delayedContactsUpdate.clear();
        }
        if (contactsByPhonesDictFinal2 != null) {
            AndroidUtilities.runOnUIThread(new ContactsController$$ExternalSyntheticLambda24(this, contactsByPhonesDictFinal2, contactsByPhonesShortDictFinal));
            return;
        }
        this.contactsLoaded = true;
    }

    /* renamed from: lambda$processLoadedContacts$28$org-telegram-messenger-ContactsController  reason: not valid java name */
    public /* synthetic */ void m44x5e12a568() {
        this.doneLoadingContacts = true;
        getNotificationCenter().postNotificationName(NotificationCenter.contactsDidLoad, new Object[0]);
    }

    /* renamed from: lambda$processLoadedContacts$29$org-telegram-messenger-ContactsController  reason: not valid java name */
    public /* synthetic */ void m45x43541429() {
        this.doneLoadingContacts = true;
        getNotificationCenter().postNotificationName(NotificationCenter.contactsDidLoad, new Object[0]);
    }

    static /* synthetic */ int lambda$processLoadedContacts$31(String s, String s2) {
        char cv1 = s.charAt(0);
        char cv2 = s2.charAt(0);
        if (cv1 == '#') {
            return 1;
        }
        if (cv2 == '#') {
            return -1;
        }
        return s.compareTo(s2);
    }

    static /* synthetic */ int lambda$processLoadedContacts$32(String s, String s2) {
        char cv1 = s.charAt(0);
        char cv2 = s2.charAt(0);
        if (cv1 == '#') {
            return 1;
        }
        if (cv2 == '#') {
            return -1;
        }
        return s.compareTo(s2);
    }

    /* renamed from: lambda$processLoadedContacts$33$org-telegram-messenger-ContactsController  reason: not valid java name */
    public /* synthetic */ void m46xa6b7e502(ArrayList contactsArr, ConcurrentHashMap contactsDictionary, HashMap sectionsDict, HashMap sectionsDictMutual, ArrayList sortedSectionsArray, ArrayList sortedSectionsArrayMutual, int from, boolean isEmpty) {
        this.contacts = contactsArr;
        this.contactsDict = contactsDictionary;
        this.usersSectionsDict = sectionsDict;
        this.usersMutualSectionsDict = sectionsDictMutual;
        this.sortedUsersSectionsArray = sortedSectionsArray;
        this.sortedUsersMutualSectionsArray = sortedSectionsArrayMutual;
        this.doneLoadingContacts = true;
        if (from != 2) {
            synchronized (this.loadContactsSync) {
                this.loadingContacts = false;
            }
        }
        performWriteContactsToPhoneBook();
        updateUnregisteredContacts();
        getNotificationCenter().postNotificationName(NotificationCenter.contactsDidLoad, new Object[0]);
        if (from == 1 || isEmpty) {
            reloadContactsStatusesMaybe();
        } else {
            saveContactsLoadTime();
        }
    }

    /* renamed from: lambda$processLoadedContacts$35$org-telegram-messenger-ContactsController  reason: not valid java name */
    public /* synthetic */ void m48x713aCLASSNAME(HashMap contactsByPhonesDictFinal, HashMap contactsByPhonesShortDictFinal) {
        Utilities.globalQueue.postRunnable(new ContactsController$$ExternalSyntheticLambda23(this, contactsByPhonesDictFinal, contactsByPhonesShortDictFinal));
        if (!this.contactsSyncInProgress) {
            this.contactsSyncInProgress = true;
            getMessagesStorage().getCachedPhoneBook(false);
        }
    }

    /* renamed from: lambda$processLoadedContacts$34$org-telegram-messenger-ContactsController  reason: not valid java name */
    public /* synthetic */ void m47x8bvar_c3(HashMap contactsByPhonesDictFinal, HashMap contactsByPhonesShortDictFinal) {
        this.contactsByPhone = contactsByPhonesDictFinal;
        this.contactsByShortPhone = contactsByPhonesShortDictFinal;
    }

    public boolean isContact(long userId) {
        return this.contactsDict.get(Long.valueOf(userId)) != null;
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
    public void m40x726ee654(HashMap<String, ArrayList<Object>> phoneBookSectionsDictFinal, ArrayList<String> phoneBookSectionsArrayFinal, HashMap<String, Contact> phoneBookByShortPhonesFinal) {
        Utilities.globalQueue.postRunnable(new ContactsController$$ExternalSyntheticLambda14(this, new ArrayList<>(this.contacts), phoneBookByShortPhonesFinal, phoneBookSectionsDictFinal, phoneBookSectionsArrayFinal));
    }

    /* renamed from: lambda$mergePhonebookAndTelegramContacts$41$org-telegram-messenger-ContactsController  reason: not valid java name */
    public /* synthetic */ void m27xcadb3f5a(ArrayList contactsCopy, HashMap phoneBookByShortPhonesFinal, HashMap phoneBookSectionsDictFinal, ArrayList phoneBookSectionsArrayFinal) {
        int size = contactsCopy.size();
        for (int a = 0; a < size; a++) {
            TLRPC.User user = getMessagesController().getUser(Long.valueOf(((TLRPC.TL_contact) contactsCopy.get(a)).user_id));
            if (user != null && !TextUtils.isEmpty(user.phone)) {
                Contact contact = (Contact) phoneBookByShortPhonesFinal.get(user.phone.substring(Math.max(0, user.phone.length() - 7)));
                if (contact == null) {
                    String key = Contact.getLetter(user.first_name, user.last_name);
                    ArrayList<Object> arrayList = (ArrayList) phoneBookSectionsDictFinal.get(key);
                    if (arrayList == null) {
                        arrayList = new ArrayList<>();
                        phoneBookSectionsDictFinal.put(key, arrayList);
                        phoneBookSectionsArrayFinal.add(key);
                    }
                    arrayList.add(user);
                } else if (contact.user == null) {
                    contact.user = user;
                }
            }
        }
        for (ArrayList<Object> arrayList2 : phoneBookSectionsDictFinal.values()) {
            Collections.sort(arrayList2, ContactsController$$ExternalSyntheticLambda47.INSTANCE);
        }
        Collections.sort(phoneBookSectionsArrayFinal, ContactsController$$ExternalSyntheticLambda41.INSTANCE);
        AndroidUtilities.runOnUIThread(new ContactsController$$ExternalSyntheticLambda13(this, phoneBookSectionsArrayFinal, phoneBookSectionsDictFinal));
    }

    static /* synthetic */ int lambda$mergePhonebookAndTelegramContacts$38(Object o1, Object o2) {
        String name1;
        String name2;
        if (o1 instanceof TLRPC.User) {
            TLRPC.User user = (TLRPC.User) o1;
            name1 = formatName(user.first_name, user.last_name);
        } else if (o1 instanceof Contact) {
            Contact contact = (Contact) o1;
            if (contact.user != null) {
                name1 = formatName(contact.user.first_name, contact.user.last_name);
            } else {
                name1 = formatName(contact.first_name, contact.last_name);
            }
        } else {
            name1 = "";
        }
        if (o2 instanceof TLRPC.User) {
            TLRPC.User user2 = (TLRPC.User) o2;
            name2 = formatName(user2.first_name, user2.last_name);
        } else if (o2 instanceof Contact) {
            Contact contact2 = (Contact) o2;
            if (contact2.user != null) {
                name2 = formatName(contact2.user.first_name, contact2.user.last_name);
            } else {
                name2 = formatName(contact2.first_name, contact2.last_name);
            }
        } else {
            name2 = "";
        }
        return name1.compareTo(name2);
    }

    static /* synthetic */ int lambda$mergePhonebookAndTelegramContacts$39(String s, String s2) {
        char cv1 = s.charAt(0);
        char cv2 = s2.charAt(0);
        if (cv1 == '#') {
            return 1;
        }
        if (cv2 == '#') {
            return -1;
        }
        return s.compareTo(s2);
    }

    /* renamed from: lambda$mergePhonebookAndTelegramContacts$40$org-telegram-messenger-ContactsController  reason: not valid java name */
    public /* synthetic */ void m26xe599d099(ArrayList phoneBookSectionsArrayFinal, HashMap phoneBookSectionsDictFinal) {
        this.phoneBookSectionsArray = phoneBookSectionsArrayFinal;
        this.phoneBookSectionsDict = phoneBookSectionsDictFinal;
    }

    private void updateUnregisteredContacts() {
        HashMap<String, TLRPC.TL_contact> contactsPhonesShort = new HashMap<>();
        int size = this.contacts.size();
        for (int a = 0; a < size; a++) {
            TLRPC.TL_contact value = this.contacts.get(a);
            TLRPC.User user = getMessagesController().getUser(Long.valueOf(value.user_id));
            if (user != null && !TextUtils.isEmpty(user.phone)) {
                contactsPhonesShort.put(user.phone, value);
            }
        }
        ArrayList<Contact> sortedPhoneBookContacts = new ArrayList<>();
        for (Map.Entry<String, Contact> pair : this.contactsBook.entrySet()) {
            Contact value2 = pair.getValue();
            boolean skip = false;
            int a2 = 0;
            while (true) {
                if (a2 >= value2.phones.size()) {
                    break;
                } else if (contactsPhonesShort.containsKey(value2.shortPhones.get(a2)) || value2.phoneDeleted.get(a2).intValue() == 1) {
                    skip = true;
                } else {
                    a2++;
                }
            }
            skip = true;
            if (!skip) {
                sortedPhoneBookContacts.add(value2);
            }
        }
        Collections.sort(sortedPhoneBookContacts, ContactsController$$ExternalSyntheticLambda45.INSTANCE);
        this.phoneBookContacts = sortedPhoneBookContacts;
    }

    static /* synthetic */ int lambda$updateUnregisteredContacts$42(Contact contact, Contact contact2) {
        String toComapre1 = contact.first_name;
        if (toComapre1.length() == 0) {
            toComapre1 = contact.last_name;
        }
        String toComapre2 = contact2.first_name;
        if (toComapre2.length() == 0) {
            toComapre2 = contact2.last_name;
        }
        return toComapre1.compareTo(toComapre2);
    }

    private void buildContactsSectionsArrays(boolean sort) {
        String key;
        if (sort) {
            Collections.sort(this.contacts, new ContactsController$$ExternalSyntheticLambda39(this));
        }
        HashMap<String, ArrayList<TLRPC.TL_contact>> sectionsDict = new HashMap<>();
        ArrayList<String> sortedSectionsArray = new ArrayList<>();
        for (int a = 0; a < this.contacts.size(); a++) {
            TLRPC.TL_contact value = this.contacts.get(a);
            TLRPC.User user = getMessagesController().getUser(Long.valueOf(value.user_id));
            if (user != null) {
                String key2 = UserObject.getFirstName(user);
                if (key2.length() > 1) {
                    key2 = key2.substring(0, 1);
                }
                if (key2.length() == 0) {
                    key = "#";
                } else {
                    key = key2.toUpperCase();
                }
                String replace = this.sectionsToReplace.get(key);
                if (replace != null) {
                    key = replace;
                }
                ArrayList<TLRPC.TL_contact> arr = sectionsDict.get(key);
                if (arr == null) {
                    arr = new ArrayList<>();
                    sectionsDict.put(key, arr);
                    sortedSectionsArray.add(key);
                }
                arr.add(value);
            }
        }
        Collections.sort(sortedSectionsArray, ContactsController$$ExternalSyntheticLambda40.INSTANCE);
        this.usersSectionsDict = sectionsDict;
        this.sortedUsersSectionsArray = sortedSectionsArray;
    }

    /* renamed from: lambda$buildContactsSectionsArrays$43$org-telegram-messenger-ContactsController  reason: not valid java name */
    public /* synthetic */ int m7x277ef5f(TLRPC.TL_contact tl_contact, TLRPC.TL_contact tl_contact2) {
        return UserObject.getFirstName(getMessagesController().getUser(Long.valueOf(tl_contact.user_id))).compareTo(UserObject.getFirstName(getMessagesController().getUser(Long.valueOf(tl_contact2.user_id))));
    }

    static /* synthetic */ int lambda$buildContactsSectionsArrays$44(String s, String s2) {
        char cv1 = s.charAt(0);
        char cv2 = s2.charAt(0);
        if (cv1 == '#') {
            return 1;
        }
        if (cv2 == '#') {
            return -1;
        }
        return s.compareTo(s2);
    }

    private boolean hasContactsPermission() {
        if (Build.VERSION.SDK_INT < 23) {
            Cursor cursor = null;
            try {
                Cursor cursor2 = ApplicationLoader.applicationContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, this.projectionPhones, (String) null, (String[]) null, (String) null);
                if (cursor2 == null || cursor2.getCount() == 0) {
                    if (cursor2 != null) {
                        try {
                            cursor2.close();
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                        }
                    }
                    return false;
                }
                if (cursor2 != null) {
                    try {
                        cursor2.close();
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
    /* renamed from: performWriteContactsToPhoneBookInternal */
    public void m43x8296d0df(ArrayList<TLRPC.TL_contact> contactsArray) {
        Cursor cursor = null;
        try {
            if (hasContactsPermission()) {
                SharedPreferences settings = MessagesController.getMainSettings(this.currentAccount);
                boolean forceUpdate = !settings.getBoolean("contacts_updated_v7", false);
                if (forceUpdate) {
                    settings.edit().putBoolean("contacts_updated_v7", true).commit();
                }
                cursor = ApplicationLoader.applicationContext.getContentResolver().query(ContactsContract.RawContacts.CONTENT_URI.buildUpon().appendQueryParameter("account_name", this.systemAccount.name).appendQueryParameter("account_type", this.systemAccount.type).build(), new String[]{"_id", "sync2"}, (String) null, (String[]) null, (String) null);
                LongSparseArray<Long> bookContacts = new LongSparseArray<>();
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        bookContacts.put(cursor.getLong(1), Long.valueOf(cursor.getLong(0)));
                    }
                    cursor.close();
                    cursor = null;
                    for (int a = 0; a < contactsArray.size(); a++) {
                        TLRPC.TL_contact u = contactsArray.get(a);
                        if (forceUpdate || bookContacts.indexOfKey(u.user_id) < 0) {
                            addContactToPhoneBook(getMessagesController().getUser(Long.valueOf(u.user_id)), forceUpdate);
                        }
                    }
                }
                if (cursor == null) {
                    return;
                }
                cursor.close();
            } else if (cursor != null) {
                cursor.close();
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            if (cursor == null) {
            }
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
            throw th;
        }
    }

    private void performWriteContactsToPhoneBook() {
        Utilities.phoneBookQueue.postRunnable(new ContactsController$$ExternalSyntheticLambda9(this, new ArrayList<>(this.contacts)));
    }

    private void applyContactsUpdates(ArrayList<Long> ids, ConcurrentHashMap<Long, TLRPC.User> concurrentHashMap, ArrayList<TLRPC.TL_contact> newC, ArrayList<Long> contactsTD) {
        ArrayList<Long> contactsTD2;
        ArrayList<TLRPC.TL_contact> newC2;
        int i;
        boolean z;
        int index;
        ConcurrentHashMap<Long, TLRPC.User> concurrentHashMap2 = concurrentHashMap;
        if (newC == null || contactsTD == null) {
            newC2 = new ArrayList<>();
            contactsTD2 = new ArrayList<>();
            for (int a = 0; a < ids.size(); a++) {
                Long uid = ids.get(a);
                if (uid.longValue() > 0) {
                    TLRPC.TL_contact contact = new TLRPC.TL_contact();
                    contact.user_id = uid.longValue();
                    newC2.add(contact);
                } else if (uid.longValue() < 0) {
                    contactsTD2.add(Long.valueOf(-uid.longValue()));
                }
            }
            ArrayList<Long> arrayList = ids;
        } else {
            ArrayList<Long> arrayList2 = ids;
            newC2 = newC;
            contactsTD2 = contactsTD;
        }
        if (BuildVars.LOGS_ENABLED != 0) {
            FileLog.d("process update - contacts add = " + newC2.size() + " delete = " + contactsTD2.size());
        }
        StringBuilder toAdd = new StringBuilder();
        StringBuilder toDelete = new StringBuilder();
        boolean reloadContacts = false;
        int a2 = 0;
        while (true) {
            i = -1;
            z = true;
            if (a2 >= newC2.size()) {
                break;
            }
            TLRPC.TL_contact newContact = newC2.get(a2);
            TLRPC.User user = null;
            if (concurrentHashMap2 != null) {
                user = concurrentHashMap2.get(Long.valueOf(newContact.user_id));
            }
            if (user == null) {
                user = getMessagesController().getUser(Long.valueOf(newContact.user_id));
            } else {
                getMessagesController().putUser(user, true);
            }
            if (user == null || TextUtils.isEmpty(user.phone)) {
                reloadContacts = true;
            } else {
                Contact contact2 = this.contactsBookSPhones.get(user.phone);
                if (!(contact2 == null || (index = contact2.shortPhones.indexOf(user.phone)) == -1)) {
                    contact2.phoneDeleted.set(index, 0);
                }
                if (toAdd.length() != 0) {
                    toAdd.append(",");
                }
                toAdd.append(user.phone);
            }
            a2++;
        }
        int a3 = 0;
        while (a3 < contactsTD2.size()) {
            Long uid2 = contactsTD2.get(a3);
            Utilities.phoneBookQueue.postRunnable(new ContactsController$$ExternalSyntheticLambda6(this, uid2));
            TLRPC.User user2 = null;
            if (concurrentHashMap2 != null) {
                user2 = concurrentHashMap2.get(uid2);
            }
            if (user2 == null) {
                user2 = getMessagesController().getUser(uid2);
            } else {
                getMessagesController().putUser(user2, z);
            }
            if (user2 == null) {
                reloadContacts = true;
            } else if (!TextUtils.isEmpty(user2.phone)) {
                Contact contact3 = this.contactsBookSPhones.get(user2.phone);
                if (contact3 != null) {
                    int index2 = contact3.shortPhones.indexOf(user2.phone);
                    if (index2 != i) {
                        contact3.phoneDeleted.set(index2, 1);
                    }
                }
                if (toDelete.length() != 0) {
                    toDelete.append(",");
                }
                toDelete.append(user2.phone);
            }
            a3++;
            i = -1;
            z = true;
        }
        if (!(toAdd.length() == 0 && toDelete.length() == 0)) {
            getMessagesStorage().applyPhoneBookUpdates(toAdd.toString(), toDelete.toString());
        }
        if (reloadContacts) {
            Utilities.stageQueue.postRunnable(new ContactsController$$ExternalSyntheticLambda11(this));
        } else {
            AndroidUtilities.runOnUIThread(new ContactsController$$ExternalSyntheticLambda12(this, newC2, contactsTD2));
        }
    }

    /* renamed from: lambda$applyContactsUpdates$46$org-telegram-messenger-ContactsController  reason: not valid java name */
    public /* synthetic */ void m4x52aad668(Long uid) {
        deleteContactFromPhoneBook(uid.longValue());
    }

    /* renamed from: lambda$applyContactsUpdates$47$org-telegram-messenger-ContactsController  reason: not valid java name */
    public /* synthetic */ void m5x37eCLASSNAME() {
        loadContacts(false, 0);
    }

    /* renamed from: lambda$applyContactsUpdates$48$org-telegram-messenger-ContactsController  reason: not valid java name */
    public /* synthetic */ void m6x1d2db3ea(ArrayList newContacts, ArrayList contactsToDelete) {
        for (int a = 0; a < newContacts.size(); a++) {
            TLRPC.TL_contact contact = (TLRPC.TL_contact) newContacts.get(a);
            if (this.contactsDict.get(Long.valueOf(contact.user_id)) == null) {
                this.contacts.add(contact);
                this.contactsDict.put(Long.valueOf(contact.user_id), contact);
            }
        }
        for (int a2 = 0; a2 < contactsToDelete.size(); a2++) {
            Long uid = (Long) contactsToDelete.get(a2);
            TLRPC.TL_contact contact2 = this.contactsDict.get(uid);
            if (contact2 != null) {
                this.contacts.remove(contact2);
                this.contactsDict.remove(uid);
            }
        }
        if (newContacts.isEmpty() == 0) {
            updateUnregisteredContacts();
            performWriteContactsToPhoneBook();
        }
        performSyncPhoneBook(getContactsCopy(this.contactsBook), false, false, false, false, true, false);
        buildContactsSectionsArrays(!newContacts.isEmpty());
        getNotificationCenter().postNotificationName(NotificationCenter.contactsDidLoad, new Object[0]);
    }

    public void processContactsUpdates(ArrayList<Long> ids, ConcurrentHashMap<Long, TLRPC.User> concurrentHashMap) {
        int idx;
        int idx2;
        ArrayList<TLRPC.TL_contact> newContacts = new ArrayList<>();
        ArrayList<Long> contactsToDelete = new ArrayList<>();
        Iterator<Long> it = ids.iterator();
        while (it.hasNext()) {
            Long uid = it.next();
            if (uid.longValue() > 0) {
                TLRPC.TL_contact contact = new TLRPC.TL_contact();
                contact.user_id = uid.longValue();
                newContacts.add(contact);
                if (!this.delayedContactsUpdate.isEmpty() && (idx2 = this.delayedContactsUpdate.indexOf(Long.valueOf(-uid.longValue()))) != -1) {
                    this.delayedContactsUpdate.remove(idx2);
                }
            } else if (uid.longValue() < 0) {
                contactsToDelete.add(Long.valueOf(-uid.longValue()));
                if (!this.delayedContactsUpdate.isEmpty() && (idx = this.delayedContactsUpdate.indexOf(Long.valueOf(-uid.longValue()))) != -1) {
                    this.delayedContactsUpdate.remove(idx);
                }
            }
        }
        if (!contactsToDelete.isEmpty()) {
            getMessagesStorage().deleteContacts(contactsToDelete);
        }
        if (!newContacts.isEmpty()) {
            getMessagesStorage().putContacts(newContacts, false);
        }
        if (!this.contactsLoaded || !this.contactsBookLoaded) {
            this.delayedContactsUpdate.addAll(ids);
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("delay update - contacts add = " + newContacts.size() + " delete = " + contactsToDelete.size());
                return;
            }
            return;
        }
        applyContactsUpdates(ids, concurrentHashMap, newContacts, contactsToDelete);
    }

    public long addContactToPhoneBook(TLRPC.User user, boolean check) {
        String phoneOrName;
        long res;
        if (this.systemAccount == null || user == null || !hasContactsPermission()) {
            return -1;
        }
        long res2 = -1;
        synchronized (this.observerLock) {
            this.ignoreChanges = true;
        }
        ContentResolver contentResolver = ApplicationLoader.applicationContext.getContentResolver();
        if (check) {
            try {
                contentResolver.delete(ContactsContract.RawContacts.CONTENT_URI.buildUpon().appendQueryParameter("caller_is_syncadapter", "true").appendQueryParameter("account_name", this.systemAccount.name).appendQueryParameter("account_type", this.systemAccount.type).build(), "sync2 = " + user.id, (String[]) null);
            } catch (Exception e) {
            }
        }
        ArrayList arrayList = new ArrayList();
        ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI);
        builder.withValue("account_name", this.systemAccount.name);
        builder.withValue("account_type", this.systemAccount.type);
        builder.withValue("sync1", TextUtils.isEmpty(user.phone) ? "" : user.phone);
        builder.withValue("sync2", Long.valueOf(user.id));
        arrayList.add(builder.build());
        ContentProviderOperation.Builder builder2 = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI);
        builder2.withValueBackReference("raw_contact_id", 0);
        builder2.withValue("mimetype", "vnd.android.cursor.item/name");
        builder2.withValue("data2", user.first_name);
        builder2.withValue("data3", user.last_name);
        arrayList.add(builder2.build());
        if (TextUtils.isEmpty(user.phone)) {
            phoneOrName = formatName(user.first_name, user.last_name);
        } else {
            phoneOrName = "+" + user.phone;
        }
        ContentProviderOperation.Builder builder3 = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI);
        builder3.withValueBackReference("raw_contact_id", 0);
        builder3.withValue("mimetype", "vnd.android.cursor.item/vnd.org.telegram.messenger.android.profile");
        builder3.withValue("data1", Long.valueOf(user.id));
        builder3.withValue("data2", "Telegram Profile");
        builder3.withValue("data3", LocaleController.formatString("ContactShortcutMessage", NUM, phoneOrName));
        builder3.withValue("data4", Long.valueOf(user.id));
        arrayList.add(builder3.build());
        ContentProviderOperation.Builder builder4 = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI);
        builder4.withValueBackReference("raw_contact_id", 0);
        builder4.withValue("mimetype", "vnd.android.cursor.item/vnd.org.telegram.messenger.android.call");
        builder4.withValue("data1", Long.valueOf(user.id));
        builder4.withValue("data2", "Telegram Voice Call");
        builder4.withValue("data3", LocaleController.formatString("ContactShortcutVoiceCall", NUM, phoneOrName));
        builder4.withValue("data4", Long.valueOf(user.id));
        arrayList.add(builder4.build());
        ContentProviderOperation.Builder builder5 = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI);
        builder5.withValueBackReference("raw_contact_id", 0);
        builder5.withValue("mimetype", "vnd.android.cursor.item/vnd.org.telegram.messenger.android.call.video");
        builder5.withValue("data1", Long.valueOf(user.id));
        builder5.withValue("data2", "Telegram Video Call");
        builder5.withValue("data3", LocaleController.formatString("ContactShortcutVideoCall", NUM, phoneOrName));
        builder5.withValue("data4", Long.valueOf(user.id));
        arrayList.add(builder5.build());
        try {
            ContentProviderResult[] result = contentResolver.applyBatch("com.android.contacts", arrayList);
            if (!(result == null || result.length <= 0 || result[0].uri == null)) {
                res2 = Long.parseLong(result[0].uri.getLastPathSegment());
            }
            res = res2;
        } catch (Exception e2) {
            res = -1;
        }
        synchronized (this.observerLock) {
            this.ignoreChanges = false;
        }
        return res;
    }

    private void deleteContactFromPhoneBook(long uid) {
        if (hasContactsPermission()) {
            synchronized (this.observerLock) {
                this.ignoreChanges = true;
            }
            try {
                ContentResolver contentResolver = ApplicationLoader.applicationContext.getContentResolver();
                Uri rawContactUri = ContactsContract.RawContacts.CONTENT_URI.buildUpon().appendQueryParameter("caller_is_syncadapter", "true").appendQueryParameter("account_name", this.systemAccount.name).appendQueryParameter("account_type", this.systemAccount.type).build();
                contentResolver.delete(rawContactUri, "sync2 = " + uid, (String[]) null);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            synchronized (this.observerLock) {
                this.ignoreChanges = false;
            }
        }
    }

    /* access modifiers changed from: protected */
    public void markAsContacted(String contactId) {
        if (contactId != null) {
            Utilities.phoneBookQueue.postRunnable(new ContactsController$$ExternalSyntheticLambda0(contactId));
        }
    }

    static /* synthetic */ void lambda$markAsContacted$49(String contactId) {
        Uri uri = Uri.parse(contactId);
        ContentValues values = new ContentValues();
        values.put("last_time_contacted", Long.valueOf(System.currentTimeMillis()));
        ApplicationLoader.applicationContext.getContentResolver().update(uri, values, (String) null, (String[]) null);
    }

    public void addContact(TLRPC.User user, boolean exception) {
        if (user != null) {
            TLRPC.TL_contacts_addContact req = new TLRPC.TL_contacts_addContact();
            req.id = getMessagesController().getInputUser(user);
            req.first_name = user.first_name;
            req.last_name = user.last_name;
            req.phone = user.phone;
            req.add_phone_privacy_exception = exception;
            if (req.phone == null) {
                req.phone = "";
            } else if (req.phone.length() > 0 && !req.phone.startsWith("+")) {
                req.phone = "+" + req.phone;
            }
            getConnectionsManager().sendRequest(req, new ContactsController$$ExternalSyntheticLambda58(this, user), 6);
        }
    }

    /* renamed from: lambda$addContact$52$org-telegram-messenger-ContactsController  reason: not valid java name */
    public /* synthetic */ void m3lambda$addContact$52$orgtelegrammessengerContactsController(TLRPC.User user, TLObject response, TLRPC.TL_error error) {
        int index;
        if (error == null) {
            TLRPC.Updates res = (TLRPC.Updates) response;
            getMessagesController().processUpdates(res, false);
            for (int a = 0; a < res.users.size(); a++) {
                TLRPC.User u = res.users.get(a);
                if (u.id == user.id) {
                    Utilities.phoneBookQueue.postRunnable(new ContactsController$$ExternalSyntheticLambda37(this, u));
                    TLRPC.TL_contact newContact = new TLRPC.TL_contact();
                    newContact.user_id = u.id;
                    ArrayList<TLRPC.TL_contact> arrayList = new ArrayList<>();
                    arrayList.add(newContact);
                    getMessagesStorage().putContacts(arrayList, false);
                    if (!TextUtils.isEmpty(u.phone)) {
                        String formatName = formatName(u.first_name, u.last_name);
                        getMessagesStorage().applyPhoneBookUpdates(u.phone, "");
                        Contact contact = this.contactsBookSPhones.get(u.phone);
                        if (!(contact == null || (index = contact.shortPhones.indexOf(u.phone)) == -1)) {
                            contact.phoneDeleted.set(index, 0);
                        }
                    }
                }
            }
            AndroidUtilities.runOnUIThread(new ContactsController$$ExternalSyntheticLambda36(this, res));
        }
    }

    /* renamed from: lambda$addContact$50$org-telegram-messenger-ContactsController  reason: not valid java name */
    public /* synthetic */ void m1lambda$addContact$50$orgtelegrammessengerContactsController(TLRPC.User u) {
        addContactToPhoneBook(u, true);
    }

    /* renamed from: lambda$addContact$51$org-telegram-messenger-ContactsController  reason: not valid java name */
    public /* synthetic */ void m2lambda$addContact$51$orgtelegrammessengerContactsController(TLRPC.Updates res) {
        for (int a = 0; a < res.users.size(); a++) {
            TLRPC.User u = res.users.get(a);
            if (u.contact && this.contactsDict.get(Long.valueOf(u.id)) == null) {
                TLRPC.TL_contact newContact = new TLRPC.TL_contact();
                newContact.user_id = u.id;
                this.contacts.add(newContact);
                this.contactsDict.put(Long.valueOf(newContact.user_id), newContact);
            }
        }
        buildContactsSectionsArrays(true);
        getNotificationCenter().postNotificationName(NotificationCenter.contactsDidLoad, new Object[0]);
    }

    public void deleteContact(ArrayList<TLRPC.User> users, boolean showBulletin) {
        if (users != null && !users.isEmpty()) {
            TLRPC.TL_contacts_deleteContacts req = new TLRPC.TL_contacts_deleteContacts();
            ArrayList<Long> uids = new ArrayList<>();
            int N = users.size();
            for (int a = 0; a < N; a++) {
                TLRPC.User user = users.get(a);
                TLRPC.InputUser inputUser = getMessagesController().getInputUser(user);
                if (inputUser != null) {
                    user.contact = false;
                    uids.add(Long.valueOf(user.id));
                    req.id.add(inputUser);
                }
            }
            getConnectionsManager().sendRequest(req, new ContactsController$$ExternalSyntheticLambda56(this, uids, users, showBulletin, users.get(0).first_name));
        }
    }

    /* renamed from: lambda$deleteContact$55$org-telegram-messenger-ContactsController  reason: not valid java name */
    public /* synthetic */ void m16x6de2e0d4(ArrayList uids, ArrayList users, boolean showBulletin, String userName, TLObject response, TLRPC.TL_error error) {
        int index;
        if (error == null) {
            getMessagesController().processUpdates((TLRPC.Updates) response, false);
            getMessagesStorage().deleteContacts(uids);
            Utilities.phoneBookQueue.postRunnable(new ContactsController$$ExternalSyntheticLambda8(this, users));
            for (int a = 0; a < users.size(); a++) {
                TLRPC.User user = (TLRPC.User) users.get(a);
                if (!TextUtils.isEmpty(user.phone)) {
                    getMessagesStorage().applyPhoneBookUpdates(user.phone, "");
                    Contact contact = this.contactsBookSPhones.get(user.phone);
                    if (!(contact == null || (index = contact.shortPhones.indexOf(user.phone)) == -1)) {
                        contact.phoneDeleted.set(index, 1);
                    }
                }
            }
            AndroidUtilities.runOnUIThread(new ContactsController$$ExternalSyntheticLambda16(this, users, showBulletin, userName));
        }
    }

    /* renamed from: lambda$deleteContact$53$org-telegram-messenger-ContactsController  reason: not valid java name */
    public /* synthetic */ void m14xa3600352(ArrayList users) {
        Iterator it = users.iterator();
        while (it.hasNext()) {
            deleteContactFromPhoneBook(((TLRPC.User) it.next()).id);
        }
    }

    /* renamed from: lambda$deleteContact$54$org-telegram-messenger-ContactsController  reason: not valid java name */
    public /* synthetic */ void m15x88a17213(ArrayList users, boolean showBulletin, String userName) {
        boolean remove = false;
        Iterator it = users.iterator();
        while (it.hasNext()) {
            TLRPC.User user = (TLRPC.User) it.next();
            TLRPC.TL_contact contact = this.contactsDict.get(Long.valueOf(user.id));
            if (contact != null) {
                remove = true;
                this.contacts.remove(contact);
                this.contactsDict.remove(Long.valueOf(user.id));
            }
        }
        if (remove) {
            buildContactsSectionsArrays(false);
        }
        getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(MessagesController.UPDATE_MASK_NAME));
        getNotificationCenter().postNotificationName(NotificationCenter.contactsDidLoad, new Object[0]);
        if (showBulletin) {
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showBulletin, 1, LocaleController.formatString("DeletedFromYourContacts", NUM, userName));
        }
    }

    private void reloadContactsStatuses() {
        saveContactsLoadTime();
        getMessagesController().clearFullUsers();
        SharedPreferences.Editor editor = MessagesController.getMainSettings(this.currentAccount).edit();
        editor.putBoolean("needGetStatuses", true).commit();
        getConnectionsManager().sendRequest(new TLRPC.TL_contacts_getStatuses(), new ContactsController$$ExternalSyntheticLambda53(this, editor));
    }

    /* renamed from: lambda$reloadContactsStatuses$57$org-telegram-messenger-ContactsController  reason: not valid java name */
    public /* synthetic */ void m53xfCLASSNAMEe105(SharedPreferences.Editor editor, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            AndroidUtilities.runOnUIThread(new ContactsController$$ExternalSyntheticLambda4(this, editor, response));
        }
    }

    /* renamed from: lambda$reloadContactsStatuses$56$org-telegram-messenger-ContactsController  reason: not valid java name */
    public /* synthetic */ void m52x16d87244(SharedPreferences.Editor editor, TLObject response) {
        editor.remove("needGetStatuses").commit();
        TLRPC.Vector vector = (TLRPC.Vector) response;
        if (!vector.objects.isEmpty()) {
            ArrayList<TLRPC.User> dbUsersStatus = new ArrayList<>();
            Iterator<Object> it = vector.objects.iterator();
            while (it.hasNext()) {
                Object object = it.next();
                TLRPC.User toDbUser = new TLRPC.TL_user();
                TLRPC.TL_contactStatus status = (TLRPC.TL_contactStatus) object;
                if (status != null) {
                    if (status.status instanceof TLRPC.TL_userStatusRecently) {
                        status.status.expires = -100;
                    } else if (status.status instanceof TLRPC.TL_userStatusLastWeek) {
                        status.status.expires = -101;
                    } else if (status.status instanceof TLRPC.TL_userStatusLastMonth) {
                        status.status.expires = -102;
                    }
                    TLRPC.User user = getMessagesController().getUser(Long.valueOf(status.user_id));
                    if (user != null) {
                        user.status = status.status;
                    }
                    toDbUser.status = status.status;
                    dbUsersStatus.add(toDbUser);
                }
            }
            getMessagesStorage().updateUsers(dbUsersStatus, true, true, true);
        }
        getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(MessagesController.UPDATE_MASK_STATUS));
    }

    public void loadPrivacySettings() {
        if (this.loadingDeleteInfo == 0) {
            this.loadingDeleteInfo = 1;
            getConnectionsManager().sendRequest(new TLRPC.TL_account_getAccountTTL(), new ContactsController$$ExternalSyntheticLambda49(this));
        }
        if (this.loadingGlobalSettings == 0) {
            this.loadingGlobalSettings = 1;
            getConnectionsManager().sendRequest(new TLRPC.TL_account_getGlobalPrivacySettings(), new ContactsController$$ExternalSyntheticLambda50(this));
        }
        int a = 0;
        while (true) {
            int[] iArr = this.loadingPrivacyInfo;
            if (a < iArr.length) {
                if (iArr[a] == 0) {
                    iArr[a] = 1;
                    int num = a;
                    TLRPC.TL_account_getPrivacy req = new TLRPC.TL_account_getPrivacy();
                    switch (num) {
                        case 0:
                            req.key = new TLRPC.TL_inputPrivacyKeyStatusTimestamp();
                            break;
                        case 1:
                            req.key = new TLRPC.TL_inputPrivacyKeyChatInvite();
                            break;
                        case 2:
                            req.key = new TLRPC.TL_inputPrivacyKeyPhoneCall();
                            break;
                        case 3:
                            req.key = new TLRPC.TL_inputPrivacyKeyPhoneP2P();
                            break;
                        case 4:
                            req.key = new TLRPC.TL_inputPrivacyKeyProfilePhoto();
                            break;
                        case 5:
                            req.key = new TLRPC.TL_inputPrivacyKeyForwards();
                            break;
                        case 6:
                            req.key = new TLRPC.TL_inputPrivacyKeyPhoneNumber();
                            break;
                        default:
                            req.key = new TLRPC.TL_inputPrivacyKeyAddedByPhone();
                            break;
                    }
                    getConnectionsManager().sendRequest(req, new ContactsController$$ExternalSyntheticLambda51(this, num));
                }
                a++;
            } else {
                getNotificationCenter().postNotificationName(NotificationCenter.privacyRulesUpdated, new Object[0]);
                return;
            }
        }
    }

    /* renamed from: lambda$loadPrivacySettings$59$org-telegram-messenger-ContactsController  reason: not valid java name */
    public /* synthetic */ void m21x4746ce08(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new ContactsController$$ExternalSyntheticLambda31(this, error, response));
    }

    /* renamed from: lambda$loadPrivacySettings$58$org-telegram-messenger-ContactsController  reason: not valid java name */
    public /* synthetic */ void m20x62055var_(TLRPC.TL_error error, TLObject response) {
        if (error == null) {
            this.deleteAccountTTL = ((TLRPC.TL_accountDaysTTL) response).days;
            this.loadingDeleteInfo = 2;
        } else {
            this.loadingDeleteInfo = 0;
        }
        getNotificationCenter().postNotificationName(NotificationCenter.privacyRulesUpdated, new Object[0]);
    }

    /* renamed from: lambda$loadPrivacySettings$61$org-telegram-messenger-ContactsController  reason: not valid java name */
    public /* synthetic */ void m23xe027CLASSNAMEf(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new ContactsController$$ExternalSyntheticLambda32(this, error, response));
    }

    /* renamed from: lambda$loadPrivacySettings$60$org-telegram-messenger-ContactsController  reason: not valid java name */
    public /* synthetic */ void m22xfae6529e(TLRPC.TL_error error, TLObject response) {
        if (error == null) {
            this.globalPrivacySettings = (TLRPC.TL_globalPrivacySettings) response;
            this.loadingGlobalSettings = 2;
        } else {
            this.loadingGlobalSettings = 0;
        }
        getNotificationCenter().postNotificationName(NotificationCenter.privacyRulesUpdated, new Object[0]);
    }

    /* renamed from: lambda$loadPrivacySettings$63$org-telegram-messenger-ContactsController  reason: not valid java name */
    public /* synthetic */ void m25xaaaa9ee1(int num, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new ContactsController$$ExternalSyntheticLambda34(this, error, response, num));
    }

    /* renamed from: lambda$loadPrivacySettings$62$org-telegram-messenger-ContactsController  reason: not valid java name */
    public /* synthetic */ void m24xCLASSNAME(TLRPC.TL_error error, TLObject response, int num) {
        if (error == null) {
            TLRPC.TL_account_privacyRules rules = (TLRPC.TL_account_privacyRules) response;
            getMessagesController().putUsers(rules.users, false);
            getMessagesController().putChats(rules.chats, false);
            switch (num) {
                case 0:
                    this.lastseenPrivacyRules = rules.rules;
                    break;
                case 1:
                    this.groupPrivacyRules = rules.rules;
                    break;
                case 2:
                    this.callPrivacyRules = rules.rules;
                    break;
                case 3:
                    this.p2pPrivacyRules = rules.rules;
                    break;
                case 4:
                    this.profilePhotoPrivacyRules = rules.rules;
                    break;
                case 5:
                    this.forwardsPrivacyRules = rules.rules;
                    break;
                case 6:
                    this.phonePrivacyRules = rules.rules;
                    break;
                default:
                    this.addedByPhonePrivacyRules = rules.rules;
                    break;
            }
            this.loadingPrivacyInfo[num] = 2;
        } else {
            this.loadingPrivacyInfo[num] = 0;
        }
        getNotificationCenter().postNotificationName(NotificationCenter.privacyRulesUpdated, new Object[0]);
    }

    public void setDeleteAccountTTL(int ttl) {
        this.deleteAccountTTL = ttl;
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

    public boolean getLoadingPrivicyInfo(int type) {
        return this.loadingPrivacyInfo[type] != 2;
    }

    public TLRPC.TL_globalPrivacySettings getGlobalPrivacySettings() {
        return this.globalPrivacySettings;
    }

    public ArrayList<TLRPC.PrivacyRule> getPrivacyRules(int type) {
        switch (type) {
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

    public void setPrivacyRules(ArrayList<TLRPC.PrivacyRule> rules, int type) {
        switch (type) {
            case 0:
                this.lastseenPrivacyRules = rules;
                break;
            case 1:
                this.groupPrivacyRules = rules;
                break;
            case 2:
                this.callPrivacyRules = rules;
                break;
            case 3:
                this.p2pPrivacyRules = rules;
                break;
            case 4:
                this.profilePhotoPrivacyRules = rules;
                break;
            case 5:
                this.forwardsPrivacyRules = rules;
                break;
            case 6:
                this.phonePrivacyRules = rules;
                break;
            case 7:
                this.addedByPhonePrivacyRules = rules;
                break;
        }
        getNotificationCenter().postNotificationName(NotificationCenter.privacyRulesUpdated, new Object[0]);
        reloadContactsStatuses();
    }

    /* JADX WARNING: Removed duplicated region for block: B:19:0x00d8 A[Catch:{ Exception -> 0x02ae }] */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x012d A[SYNTHETIC, Splitter:B:23:0x012d] */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x029c A[Catch:{ Exception -> 0x02a8 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void createOrUpdateConnectionServiceContact(long r26, java.lang.String r28, java.lang.String r29) {
        /*
            r25 = this;
            r1 = r25
            r2 = r26
            r4 = r28
            r5 = r29
            java.lang.String r0 = "raw_contact_id=? AND mimetype=?"
            java.lang.String r6 = "vnd.android.cursor.item/group_membership"
            java.lang.String r7 = "TelegramConnectionService"
            java.lang.String r8 = "true"
            java.lang.String r9 = "caller_is_syncadapter"
            java.lang.String r10 = "mimetype"
            java.lang.String r11 = ""
            java.lang.String r12 = "raw_contact_id"
            boolean r13 = r25.hasContactsPermission()
            if (r13 != 0) goto L_0x001f
            return
        L_0x001f:
            android.content.Context r13 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x02bb }
            android.content.ContentResolver r13 = r13.getContentResolver()     // Catch:{ Exception -> 0x02bb }
            java.util.ArrayList r14 = new java.util.ArrayList     // Catch:{ Exception -> 0x02bb }
            r14.<init>()     // Catch:{ Exception -> 0x02bb }
            r15 = r14
            android.net.Uri r14 = android.provider.ContactsContract.Groups.CONTENT_URI     // Catch:{ Exception -> 0x02bb }
            android.net.Uri$Builder r14 = r14.buildUpon()     // Catch:{ Exception -> 0x02bb }
            android.net.Uri$Builder r14 = r14.appendQueryParameter(r9, r8)     // Catch:{ Exception -> 0x02bb }
            android.net.Uri r14 = r14.build()     // Catch:{ Exception -> 0x02bb }
            android.net.Uri r16 = android.provider.ContactsContract.RawContacts.CONTENT_URI     // Catch:{ Exception -> 0x02bb }
            r17 = r14
            android.net.Uri$Builder r14 = r16.buildUpon()     // Catch:{ Exception -> 0x02bb }
            android.net.Uri$Builder r8 = r14.appendQueryParameter(r9, r8)     // Catch:{ Exception -> 0x02bb }
            android.net.Uri r8 = r8.build()     // Catch:{ Exception -> 0x02bb }
            r9 = 1
            java.lang.String[] r14 = new java.lang.String[r9]     // Catch:{ Exception -> 0x02bb }
            java.lang.String r16 = "_id"
            r9 = 0
            r14[r9] = r16     // Catch:{ Exception -> 0x02bb }
            java.lang.String r18 = "title=? AND account_type=? AND account_name=?"
            r9 = 3
            r21 = r10
            java.lang.String[] r10 = new java.lang.String[r9]     // Catch:{ Exception -> 0x02bb }
            r16 = 0
            r10[r16] = r7     // Catch:{ Exception -> 0x02bb }
            android.accounts.Account r9 = r1.systemAccount     // Catch:{ Exception -> 0x02bb }
            java.lang.String r9 = r9.type     // Catch:{ Exception -> 0x02bb }
            r16 = 1
            r10[r16] = r9     // Catch:{ Exception -> 0x02bb }
            android.accounts.Account r9 = r1.systemAccount     // Catch:{ Exception -> 0x02bb }
            java.lang.String r9 = r9.name     // Catch:{ Exception -> 0x02bb }
            r5 = 2
            r10[r5] = r9     // Catch:{ Exception -> 0x02b5 }
            r19 = 0
            r16 = r14
            r9 = r17
            r14 = r13
            r22 = r15
            r15 = r9
            r17 = r18
            r18 = r10
            android.database.Cursor r10 = r14.query(r15, r16, r17, r18, r19)     // Catch:{ Exception -> 0x02b5 }
            java.lang.String r15 = "account_name"
            java.lang.String r14 = "account_type"
            if (r10 == 0) goto L_0x0095
            boolean r16 = r10.moveToFirst()     // Catch:{ Exception -> 0x02b5 }
            if (r16 == 0) goto L_0x0095
            r7 = 0
            int r16 = r10.getInt(r7)     // Catch:{ Exception -> 0x02b5 }
            r7 = r16
            r17 = r14
            r16 = r15
            goto L_0x00d6
        L_0x0095:
            android.content.ContentValues r16 = new android.content.ContentValues     // Catch:{ Exception -> 0x02b5 }
            r16.<init>()     // Catch:{ Exception -> 0x02b5 }
            r17 = r16
            android.accounts.Account r5 = r1.systemAccount     // Catch:{ Exception -> 0x02b5 }
            java.lang.String r5 = r5.type     // Catch:{ Exception -> 0x02b5 }
            r4 = r17
            r4.put(r14, r5)     // Catch:{ Exception -> 0x02ae }
            android.accounts.Account r5 = r1.systemAccount     // Catch:{ Exception -> 0x02ae }
            java.lang.String r5 = r5.name     // Catch:{ Exception -> 0x02ae }
            r4.put(r15, r5)     // Catch:{ Exception -> 0x02ae }
            java.lang.String r5 = "group_visible"
            r17 = r14
            r16 = 0
            java.lang.Integer r14 = java.lang.Integer.valueOf(r16)     // Catch:{ Exception -> 0x02ae }
            r4.put(r5, r14)     // Catch:{ Exception -> 0x02ae }
            java.lang.String r5 = "group_is_read_only"
            r16 = r15
            r14 = 1
            java.lang.Integer r15 = java.lang.Integer.valueOf(r14)     // Catch:{ Exception -> 0x02ae }
            r4.put(r5, r15)     // Catch:{ Exception -> 0x02ae }
            java.lang.String r5 = "title"
            r4.put(r5, r7)     // Catch:{ Exception -> 0x02ae }
            android.net.Uri r5 = r13.insert(r9, r4)     // Catch:{ Exception -> 0x02ae }
            java.lang.String r7 = r5.getLastPathSegment()     // Catch:{ Exception -> 0x02ae }
            int r7 = java.lang.Integer.parseInt(r7)     // Catch:{ Exception -> 0x02ae }
        L_0x00d6:
            if (r10 == 0) goto L_0x00db
            r10.close()     // Catch:{ Exception -> 0x02ae }
        L_0x00db:
            android.net.Uri r15 = android.provider.ContactsContract.Data.CONTENT_URI     // Catch:{ Exception -> 0x02ae }
            r4 = 1
            java.lang.String[] r5 = new java.lang.String[r4]     // Catch:{ Exception -> 0x02ae }
            r4 = 0
            r5[r4] = r12     // Catch:{ Exception -> 0x02ae }
            java.lang.String r18 = "mimetype=? AND data1=?"
            r14 = 2
            java.lang.String[] r4 = new java.lang.String[r14]     // Catch:{ Exception -> 0x02ae }
            r14 = 0
            r4[r14] = r6     // Catch:{ Exception -> 0x02ae }
            java.lang.StringBuilder r14 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x02ae }
            r14.<init>()     // Catch:{ Exception -> 0x02ae }
            r14.append(r7)     // Catch:{ Exception -> 0x02ae }
            r14.append(r11)     // Catch:{ Exception -> 0x02ae }
            java.lang.String r14 = r14.toString()     // Catch:{ Exception -> 0x02ae }
            r19 = 1
            r4[r19] = r14     // Catch:{ Exception -> 0x02ae }
            r19 = 0
            r23 = r9
            r9 = r17
            r14 = r13
            r24 = r10
            r10 = r16
            r16 = r5
            r17 = r18
            r18 = r4
            android.database.Cursor r4 = r14.query(r15, r16, r17, r18, r19)     // Catch:{ Exception -> 0x02ae }
            int r5 = r22.size()     // Catch:{ Exception -> 0x02ae }
            java.lang.String r14 = "+99084"
            java.lang.String r15 = "vnd.android.cursor.item/phone_v2"
            r16 = r13
            java.lang.String r13 = "data3"
            r17 = r7
            java.lang.String r7 = "data2"
            r18 = r6
            java.lang.String r6 = "vnd.android.cursor.item/name"
            r19 = r5
            java.lang.String r5 = "data1"
            if (r4 == 0) goto L_0x01f1
            boolean r24 = r4.moveToFirst()     // Catch:{ Exception -> 0x01ea }
            if (r24 == 0) goto L_0x01f1
            r9 = 0
            int r10 = r4.getInt(r9)     // Catch:{ Exception -> 0x01ea }
            r9 = r10
            android.content.ContentProviderOperation$Builder r10 = android.content.ContentProviderOperation.newUpdate(r8)     // Catch:{ Exception -> 0x01ea }
            java.lang.String r12 = "_id=?"
            r24 = r4
            r4 = 1
            java.lang.String[] r1 = new java.lang.String[r4]     // Catch:{ Exception -> 0x01ea }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x01ea }
            r4.<init>()     // Catch:{ Exception -> 0x01ea }
            r4.append(r9)     // Catch:{ Exception -> 0x01ea }
            r4.append(r11)     // Catch:{ Exception -> 0x01ea }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x01ea }
            r18 = 0
            r1[r18] = r4     // Catch:{ Exception -> 0x01ea }
            android.content.ContentProviderOperation$Builder r1 = r10.withSelection(r12, r1)     // Catch:{ Exception -> 0x01ea }
            java.lang.String r4 = "deleted"
            java.lang.Integer r10 = java.lang.Integer.valueOf(r18)     // Catch:{ Exception -> 0x01ea }
            android.content.ContentProviderOperation$Builder r1 = r1.withValue(r4, r10)     // Catch:{ Exception -> 0x01ea }
            android.content.ContentProviderOperation r1 = r1.build()     // Catch:{ Exception -> 0x01ea }
            r4 = r22
            r4.add(r1)     // Catch:{ Exception -> 0x01ea }
            android.net.Uri r1 = android.provider.ContactsContract.Data.CONTENT_URI     // Catch:{ Exception -> 0x01ea }
            android.content.ContentProviderOperation$Builder r1 = android.content.ContentProviderOperation.newUpdate(r1)     // Catch:{ Exception -> 0x01ea }
            r10 = 2
            java.lang.String[] r12 = new java.lang.String[r10]     // Catch:{ Exception -> 0x01ea }
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x01ea }
            r10.<init>()     // Catch:{ Exception -> 0x01ea }
            r10.append(r9)     // Catch:{ Exception -> 0x01ea }
            r10.append(r11)     // Catch:{ Exception -> 0x01ea }
            java.lang.String r10 = r10.toString()     // Catch:{ Exception -> 0x01ea }
            r18 = 0
            r12[r18] = r10     // Catch:{ Exception -> 0x01ea }
            r10 = 1
            r12[r10] = r15     // Catch:{ Exception -> 0x01ea }
            android.content.ContentProviderOperation$Builder r1 = r1.withSelection(r0, r12)     // Catch:{ Exception -> 0x01ea }
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x01ea }
            r10.<init>()     // Catch:{ Exception -> 0x01ea }
            r10.append(r14)     // Catch:{ Exception -> 0x01ea }
            r10.append(r2)     // Catch:{ Exception -> 0x01ea }
            java.lang.String r10 = r10.toString()     // Catch:{ Exception -> 0x01ea }
            android.content.ContentProviderOperation$Builder r1 = r1.withValue(r5, r10)     // Catch:{ Exception -> 0x01ea }
            android.content.ContentProviderOperation r1 = r1.build()     // Catch:{ Exception -> 0x01ea }
            r4.add(r1)     // Catch:{ Exception -> 0x01ea }
            android.net.Uri r1 = android.provider.ContactsContract.Data.CONTENT_URI     // Catch:{ Exception -> 0x01ea }
            android.content.ContentProviderOperation$Builder r1 = android.content.ContentProviderOperation.newUpdate(r1)     // Catch:{ Exception -> 0x01ea }
            r5 = 2
            java.lang.String[] r5 = new java.lang.String[r5]     // Catch:{ Exception -> 0x01ea }
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x01ea }
            r10.<init>()     // Catch:{ Exception -> 0x01ea }
            r10.append(r9)     // Catch:{ Exception -> 0x01ea }
            r10.append(r11)     // Catch:{ Exception -> 0x01ea }
            java.lang.String r10 = r10.toString()     // Catch:{ Exception -> 0x01ea }
            r11 = 0
            r5[r11] = r10     // Catch:{ Exception -> 0x01ea }
            r10 = 1
            r5[r10] = r6     // Catch:{ Exception -> 0x01ea }
            android.content.ContentProviderOperation$Builder r0 = r1.withSelection(r0, r5)     // Catch:{ Exception -> 0x01ea }
            r1 = r28
            android.content.ContentProviderOperation$Builder r0 = r0.withValue(r7, r1)     // Catch:{ Exception -> 0x01e8 }
            r11 = r29
            android.content.ContentProviderOperation$Builder r0 = r0.withValue(r13, r11)     // Catch:{ Exception -> 0x02aa }
            android.content.ContentProviderOperation r0 = r0.build()     // Catch:{ Exception -> 0x02aa }
            r4.add(r0)     // Catch:{ Exception -> 0x02aa }
            r20 = r8
            r5 = r19
            r8 = r25
            goto L_0x029a
        L_0x01e8:
            r0 = move-exception
            goto L_0x01ed
        L_0x01ea:
            r0 = move-exception
            r1 = r28
        L_0x01ed:
            r11 = r29
            goto L_0x02ab
        L_0x01f1:
            r1 = r28
            r11 = r29
            r24 = r4
            r4 = r22
            android.content.ContentProviderOperation$Builder r0 = android.content.ContentProviderOperation.newInsert(r8)     // Catch:{ Exception -> 0x02aa }
            r22 = r5
            r20 = r8
            r8 = r25
            android.accounts.Account r5 = r8.systemAccount     // Catch:{ Exception -> 0x02a8 }
            java.lang.String r5 = r5.type     // Catch:{ Exception -> 0x02a8 }
            android.content.ContentProviderOperation$Builder r0 = r0.withValue(r9, r5)     // Catch:{ Exception -> 0x02a8 }
            android.accounts.Account r5 = r8.systemAccount     // Catch:{ Exception -> 0x02a8 }
            java.lang.String r5 = r5.name     // Catch:{ Exception -> 0x02a8 }
            android.content.ContentProviderOperation$Builder r0 = r0.withValue(r10, r5)     // Catch:{ Exception -> 0x02a8 }
            java.lang.String r5 = "raw_contact_is_read_only"
            r9 = 1
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)     // Catch:{ Exception -> 0x02a8 }
            android.content.ContentProviderOperation$Builder r0 = r0.withValue(r5, r9)     // Catch:{ Exception -> 0x02a8 }
            java.lang.String r5 = "aggregation_mode"
            r9 = 3
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)     // Catch:{ Exception -> 0x02a8 }
            android.content.ContentProviderOperation$Builder r0 = r0.withValue(r5, r9)     // Catch:{ Exception -> 0x02a8 }
            android.content.ContentProviderOperation r0 = r0.build()     // Catch:{ Exception -> 0x02a8 }
            r4.add(r0)     // Catch:{ Exception -> 0x02a8 }
            android.net.Uri r0 = android.provider.ContactsContract.Data.CONTENT_URI     // Catch:{ Exception -> 0x02a8 }
            android.content.ContentProviderOperation$Builder r0 = android.content.ContentProviderOperation.newInsert(r0)     // Catch:{ Exception -> 0x02a8 }
            r5 = r19
            android.content.ContentProviderOperation$Builder r0 = r0.withValueBackReference(r12, r5)     // Catch:{ Exception -> 0x02a8 }
            r9 = r21
            android.content.ContentProviderOperation$Builder r0 = r0.withValue(r9, r6)     // Catch:{ Exception -> 0x02a8 }
            android.content.ContentProviderOperation$Builder r0 = r0.withValue(r7, r1)     // Catch:{ Exception -> 0x02a8 }
            android.content.ContentProviderOperation$Builder r0 = r0.withValue(r13, r11)     // Catch:{ Exception -> 0x02a8 }
            android.content.ContentProviderOperation r0 = r0.build()     // Catch:{ Exception -> 0x02a8 }
            r4.add(r0)     // Catch:{ Exception -> 0x02a8 }
            android.net.Uri r0 = android.provider.ContactsContract.Data.CONTENT_URI     // Catch:{ Exception -> 0x02a8 }
            android.content.ContentProviderOperation$Builder r0 = android.content.ContentProviderOperation.newInsert(r0)     // Catch:{ Exception -> 0x02a8 }
            android.content.ContentProviderOperation$Builder r0 = r0.withValueBackReference(r12, r5)     // Catch:{ Exception -> 0x02a8 }
            android.content.ContentProviderOperation$Builder r0 = r0.withValue(r9, r15)     // Catch:{ Exception -> 0x02a8 }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x02a8 }
            r6.<init>()     // Catch:{ Exception -> 0x02a8 }
            r6.append(r14)     // Catch:{ Exception -> 0x02a8 }
            r6.append(r2)     // Catch:{ Exception -> 0x02a8 }
            java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x02a8 }
            r7 = r22
            android.content.ContentProviderOperation$Builder r0 = r0.withValue(r7, r6)     // Catch:{ Exception -> 0x02a8 }
            android.content.ContentProviderOperation r0 = r0.build()     // Catch:{ Exception -> 0x02a8 }
            r4.add(r0)     // Catch:{ Exception -> 0x02a8 }
            android.net.Uri r0 = android.provider.ContactsContract.Data.CONTENT_URI     // Catch:{ Exception -> 0x02a8 }
            android.content.ContentProviderOperation$Builder r0 = android.content.ContentProviderOperation.newInsert(r0)     // Catch:{ Exception -> 0x02a8 }
            android.content.ContentProviderOperation$Builder r0 = r0.withValueBackReference(r12, r5)     // Catch:{ Exception -> 0x02a8 }
            r6 = r18
            android.content.ContentProviderOperation$Builder r0 = r0.withValue(r9, r6)     // Catch:{ Exception -> 0x02a8 }
            java.lang.Integer r6 = java.lang.Integer.valueOf(r17)     // Catch:{ Exception -> 0x02a8 }
            android.content.ContentProviderOperation$Builder r0 = r0.withValue(r7, r6)     // Catch:{ Exception -> 0x02a8 }
            android.content.ContentProviderOperation r0 = r0.build()     // Catch:{ Exception -> 0x02a8 }
            r4.add(r0)     // Catch:{ Exception -> 0x02a8 }
        L_0x029a:
            if (r24 == 0) goto L_0x029f
            r24.close()     // Catch:{ Exception -> 0x02a8 }
        L_0x029f:
            java.lang.String r0 = "com.android.contacts"
            r6 = r16
            r6.applyBatch(r0, r4)     // Catch:{ Exception -> 0x02a8 }
            goto L_0x02c2
        L_0x02a8:
            r0 = move-exception
            goto L_0x02bf
        L_0x02aa:
            r0 = move-exception
        L_0x02ab:
            r8 = r25
            goto L_0x02bf
        L_0x02ae:
            r0 = move-exception
            r11 = r29
            r8 = r1
            r1 = r28
            goto L_0x02bf
        L_0x02b5:
            r0 = move-exception
            r11 = r29
            r8 = r1
            r1 = r4
            goto L_0x02bf
        L_0x02bb:
            r0 = move-exception
            r8 = r1
            r1 = r4
            r11 = r5
        L_0x02bf:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x02c2:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ContactsController.createOrUpdateConnectionServiceContact(long, java.lang.String, java.lang.String):void");
    }

    public void deleteConnectionServiceContact() {
        if (hasContactsPermission()) {
            try {
                ContentResolver resolver = ApplicationLoader.applicationContext.getContentResolver();
                Cursor cursor = resolver.query(ContactsContract.Groups.CONTENT_URI, new String[]{"_id"}, "title=? AND account_type=? AND account_name=?", new String[]{"TelegramConnectionService", this.systemAccount.type, this.systemAccount.name}, (String) null);
                if (cursor != null && cursor.moveToFirst()) {
                    int groupID = cursor.getInt(0);
                    cursor.close();
                    Cursor cursor2 = resolver.query(ContactsContract.Data.CONTENT_URI, new String[]{"raw_contact_id"}, "mimetype=? AND data1=?", new String[]{"vnd.android.cursor.item/group_membership", groupID + ""}, (String) null);
                    if (cursor2 != null && cursor2.moveToFirst()) {
                        int contactID = cursor2.getInt(0);
                        cursor2.close();
                        Uri uri = ContactsContract.RawContacts.CONTENT_URI;
                        resolver.delete(uri, "_id=?", new String[]{contactID + ""});
                    } else if (cursor2 != null) {
                        cursor2.close();
                    }
                } else if (cursor != null) {
                    cursor.close();
                }
            } catch (Exception x) {
                FileLog.e((Throwable) x);
            }
        }
    }

    public static String formatName(String firstName, String lastName) {
        return formatName(firstName, lastName, 0);
    }

    public static String formatName(String firstName, String lastName, int maxLength) {
        if (firstName != null) {
            firstName = firstName.trim();
        }
        if (lastName != null) {
            lastName = lastName.trim();
        }
        StringBuilder result = new StringBuilder((firstName != null ? firstName.length() : 0) + (lastName != null ? lastName.length() : 0) + 1);
        if (LocaleController.nameDisplayOrder == 1) {
            if (firstName == null || firstName.length() <= 0) {
                if (lastName != null && lastName.length() > 0) {
                    if (maxLength > 0 && lastName.length() > maxLength + 2) {
                        return lastName.substring(0, maxLength);
                    }
                    result.append(lastName);
                }
            } else if (maxLength > 0 && firstName.length() > maxLength + 2) {
                return firstName.substring(0, maxLength);
            } else {
                result.append(firstName);
                if (lastName != null && lastName.length() > 0) {
                    result.append(" ");
                    if (maxLength <= 0 || result.length() + lastName.length() <= maxLength) {
                        result.append(lastName);
                    } else {
                        result.append(lastName.charAt(0));
                    }
                }
            }
        } else if (lastName == null || lastName.length() <= 0) {
            if (firstName != null && firstName.length() > 0) {
                if (maxLength > 0 && firstName.length() > maxLength + 2) {
                    return firstName.substring(0, maxLength);
                }
                result.append(firstName);
            }
        } else if (maxLength > 0 && lastName.length() > maxLength + 2) {
            return lastName.substring(0, maxLength);
        } else {
            result.append(lastName);
            if (firstName != null && firstName.length() > 0) {
                result.append(" ");
                if (maxLength <= 0 || result.length() + firstName.length() <= maxLength) {
                    result.append(firstName);
                } else {
                    result.append(firstName.charAt(0));
                }
            }
        }
        return result.toString();
    }
}
