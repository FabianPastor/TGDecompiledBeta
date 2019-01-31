package org.telegram.messenger;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentProviderOperation;
import android.content.ContentProviderOperation.Builder;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build.VERSION;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.Groups;
import android.provider.ContactsContract.RawContacts;
import android.text.TextUtils;
import android.util.SparseArray;
import com.google.devtools.build.android.desugar.runtime.ThrowableExtension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.InputUser;
import org.telegram.tgnet.TLRPC.PrivacyRule;
import org.telegram.tgnet.TLRPC.TL_accountDaysTTL;
import org.telegram.tgnet.TLRPC.TL_account_getAccountTTL;
import org.telegram.tgnet.TLRPC.TL_account_getPrivacy;
import org.telegram.tgnet.TLRPC.TL_account_privacyRules;
import org.telegram.tgnet.TLRPC.TL_boolTrue;
import org.telegram.tgnet.TLRPC.TL_contact;
import org.telegram.tgnet.TLRPC.TL_contactStatus;
import org.telegram.tgnet.TLRPC.TL_contacts_contactsNotModified;
import org.telegram.tgnet.TLRPC.TL_contacts_deleteContacts;
import org.telegram.tgnet.TLRPC.TL_contacts_getContacts;
import org.telegram.tgnet.TLRPC.TL_contacts_getStatuses;
import org.telegram.tgnet.TLRPC.TL_contacts_importContacts;
import org.telegram.tgnet.TLRPC.TL_contacts_importedContacts;
import org.telegram.tgnet.TLRPC.TL_contacts_resetSaved;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_help_getInviteText;
import org.telegram.tgnet.TLRPC.TL_help_inviteText;
import org.telegram.tgnet.TLRPC.TL_importedContact;
import org.telegram.tgnet.TLRPC.TL_inputPhoneContact;
import org.telegram.tgnet.TLRPC.TL_inputPrivacyKeyChatInvite;
import org.telegram.tgnet.TLRPC.TL_inputPrivacyKeyPhoneCall;
import org.telegram.tgnet.TLRPC.TL_inputPrivacyKeyPhoneP2P;
import org.telegram.tgnet.TLRPC.TL_inputPrivacyKeyStatusTimestamp;
import org.telegram.tgnet.TLRPC.TL_popularContact;
import org.telegram.tgnet.TLRPC.TL_user;
import org.telegram.tgnet.TLRPC.TL_userStatusLastMonth;
import org.telegram.tgnet.TLRPC.TL_userStatusLastWeek;
import org.telegram.tgnet.TLRPC.TL_userStatusRecently;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.Vector;
import org.telegram.tgnet.TLRPC.contacts_Contacts;

public class ContactsController {
    private static volatile ContactsController[] Instance = new ContactsController[3];
    private ArrayList<PrivacyRule> callPrivacyRules;
    private int completedRequestsCount;
    public ArrayList<TL_contact> contacts = new ArrayList();
    public HashMap<String, Contact> contactsBook = new HashMap();
    private boolean contactsBookLoaded;
    public HashMap<String, Contact> contactsBookSPhones = new HashMap();
    public HashMap<String, TL_contact> contactsByPhone = new HashMap();
    public HashMap<String, TL_contact> contactsByShortPhone = new HashMap();
    public ConcurrentHashMap<Integer, TL_contact> contactsDict = new ConcurrentHashMap(20, 1.0f, 2);
    public boolean contactsLoaded;
    private boolean contactsSyncInProgress;
    private int currentAccount;
    private ArrayList<Integer> delayedContactsUpdate = new ArrayList();
    private int deleteAccountTTL;
    private ArrayList<PrivacyRule> groupPrivacyRules;
    private boolean ignoreChanges;
    private String inviteLink;
    private String lastContactsVersions = "";
    private final Object loadContactsSync = new Object();
    private boolean loadingContacts;
    private int loadingDeleteInfo;
    private int[] loadingPrivacyInfo = new int[4];
    private boolean migratingContacts;
    private final Object observerLock = new Object();
    private ArrayList<PrivacyRule> p2pPrivacyRules;
    public ArrayList<Contact> phoneBookContacts = new ArrayList();
    public ArrayList<String> phoneBookSectionsArray = new ArrayList();
    public HashMap<String, ArrayList<Object>> phoneBookSectionsDict = new HashMap();
    private ArrayList<PrivacyRule> privacyRules;
    private String[] projectionNames = new String[]{"lookup", "data2", "data3", "data5"};
    private String[] projectionPhones = new String[]{"lookup", "data1", "data2", "data3", "display_name", "account_type"};
    private HashMap<String, String> sectionsToReplace = new HashMap();
    public ArrayList<String> sortedUsersMutualSectionsArray = new ArrayList();
    public ArrayList<String> sortedUsersSectionsArray = new ArrayList();
    private Account systemAccount;
    private boolean updatingInviteLink;
    public HashMap<String, ArrayList<TL_contact>> usersMutualSectionsDict = new HashMap();
    public HashMap<String, ArrayList<TL_contact>> usersSectionsDict = new HashMap();

    public static class Contact {
        public int contact_id;
        public String first_name;
        public int imported;
        public boolean isGoodProvider;
        public String key;
        public String last_name;
        public boolean namesFilled;
        public ArrayList<Integer> phoneDeleted = new ArrayList(4);
        public ArrayList<String> phoneTypes = new ArrayList(4);
        public ArrayList<String> phones = new ArrayList(4);
        public String provider;
        public ArrayList<String> shortPhones = new ArrayList(4);
        public User user;

        public String getLetter() {
            return getLetter(this.first_name, this.last_name);
        }

        public static String getLetter(String first_name, String last_name) {
            if (!TextUtils.isEmpty(first_name)) {
                return first_name.substring(0, 1);
            }
            if (TextUtils.isEmpty(last_name)) {
                return "#";
            }
            return last_name.substring(0, 1);
        }
    }

    private class MyContentObserver extends ContentObserver {
        private Runnable checkRunnable = ContactsController$MyContentObserver$$Lambda$0.$instance;

        static final /* synthetic */ void lambda$new$0$ContactsController$MyContentObserver() {
            for (int a = 0; a < 3; a++) {
                if (UserConfig.getInstance(a).isClientActivated()) {
                    ConnectionsManager.getInstance(a).resumeNetworkMaybe();
                    ContactsController.getInstance(a).checkContacts();
                }
            }
        }

        public MyContentObserver() {
            super(null);
        }

        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            synchronized (ContactsController.this.observerLock) {
                if (ContactsController.this.ignoreChanges) {
                    return;
                }
                Utilities.globalQueue.cancelRunnable(this.checkRunnable);
                Utilities.globalQueue.postRunnable(this.checkRunnable, 500);
            }
        }

        public boolean deliverSelfNotifications() {
            return false;
        }
    }

    public static ContactsController getInstance(int num) {
        Throwable th;
        ContactsController localInstance = Instance[num];
        if (localInstance == null) {
            synchronized (ContactsController.class) {
                try {
                    localInstance = Instance[num];
                    if (localInstance == null) {
                        ContactsController[] contactsControllerArr = Instance;
                        ContactsController localInstance2 = new ContactsController(num);
                        try {
                            contactsControllerArr[num] = localInstance2;
                            localInstance = localInstance2;
                        } catch (Throwable th2) {
                            th = th2;
                            localInstance = localInstance2;
                            throw th;
                        }
                    }
                } catch (Throwable th3) {
                    th = th3;
                    throw th;
                }
            }
        }
        return localInstance;
    }

    public ContactsController(int instance) {
        this.currentAccount = instance;
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
            Utilities.globalQueue.postRunnable(new ContactsController$$Lambda$0(this));
        }
    }

    final /* synthetic */ void lambda$new$0$ContactsController() {
        try {
            if (hasContactsPermission()) {
                ApplicationLoader.applicationContext.getContentResolver().registerContentObserver(Contacts.CONTENT_URI, true, new MyContentObserver());
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
        this.contactsLoaded = false;
        this.contactsBookLoaded = false;
        this.lastContactsVersions = "";
        this.loadingDeleteInfo = 0;
        this.deleteAccountTTL = 0;
        for (int a = 0; a < this.loadingPrivacyInfo.length; a++) {
            this.loadingPrivacyInfo[a] = 0;
        }
        this.privacyRules = null;
        this.groupPrivacyRules = null;
        this.callPrivacyRules = null;
        this.p2pPrivacyRules = null;
        Utilities.globalQueue.postRunnable(new ContactsController$$Lambda$1(this));
    }

    final /* synthetic */ void lambda$cleanup$1$ContactsController() {
        this.migratingContacts = false;
        this.completedRequestsCount = 0;
    }

    public void checkInviteText() {
        SharedPreferences preferences = MessagesController.getMainSettings(this.currentAccount);
        this.inviteLink = preferences.getString("invitelink", null);
        int time = preferences.getInt("invitelinktime", 0);
        if (!this.updatingInviteLink) {
            if (this.inviteLink == null || Math.abs((System.currentTimeMillis() / 1000) - ((long) time)) >= 86400) {
                this.updatingInviteLink = true;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_help_getInviteText(), new ContactsController$$Lambda$2(this), 2);
            }
        }
    }

    final /* synthetic */ void lambda$checkInviteText$3$ContactsController(TLObject response, TL_error error) {
        if (response != null) {
            TL_help_inviteText res = (TL_help_inviteText) response;
            if (res.message.length() != 0) {
                AndroidUtilities.runOnUIThread(new ContactsController$$Lambda$59(this, res));
            }
        }
    }

    final /* synthetic */ void lambda$null$2$ContactsController(TL_help_inviteText res) {
        this.updatingInviteLink = false;
        Editor editor = MessagesController.getMainSettings(this.currentAccount).edit();
        String str = res.message;
        this.inviteLink = str;
        editor.putString("invitelink", str);
        editor.putInt("invitelinktime", (int) (System.currentTimeMillis() / 1000));
        editor.commit();
    }

    public String getInviteText(int contacts) {
        String link = this.inviteLink == null ? "https://telegram.org/dl" : this.inviteLink;
        if (contacts <= 1) {
            return LocaleController.formatString("InviteText2", R.string.InviteText2, link);
        }
        try {
            return String.format(LocaleController.getPluralString("InviteTextNum", contacts), new Object[]{Integer.valueOf(contacts), link});
        } catch (Exception e) {
            return LocaleController.formatString("InviteText2", R.string.InviteText2, link);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:32:0x0053 A:{SYNTHETIC} */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x004c A:{SYNTHETIC, Splitter: B:17:0x004c} */
    public void checkAppAccount() {
        /*
        r10 = this;
        r7 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r3 = android.accounts.AccountManager.get(r7);
        r7 = "org.telegram.messenger";
        r2 = r3.getAccountsByType(r7);	 Catch:{ Throwable -> 0x0059 }
        r7 = 0;
        r10.systemAccount = r7;	 Catch:{ Throwable -> 0x0059 }
        r0 = 0;
    L_0x0011:
        r7 = r2.length;	 Catch:{ Throwable -> 0x0059 }
        if (r0 >= r7) goto L_0x005a;
    L_0x0014:
        r1 = r2[r0];	 Catch:{ Throwable -> 0x0059 }
        r5 = 0;
        r4 = 0;
    L_0x0018:
        r7 = 3;
        if (r4 >= r7) goto L_0x004a;
    L_0x001b:
        r7 = org.telegram.messenger.UserConfig.getInstance(r4);	 Catch:{ Throwable -> 0x0059 }
        r6 = r7.getCurrentUser();	 Catch:{ Throwable -> 0x0059 }
        if (r6 == 0) goto L_0x0056;
    L_0x0025:
        r7 = r1.name;	 Catch:{ Throwable -> 0x0059 }
        r8 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x0059 }
        r8.<init>();	 Catch:{ Throwable -> 0x0059 }
        r9 = "";
        r8 = r8.append(r9);	 Catch:{ Throwable -> 0x0059 }
        r9 = r6.id;	 Catch:{ Throwable -> 0x0059 }
        r8 = r8.append(r9);	 Catch:{ Throwable -> 0x0059 }
        r8 = r8.toString();	 Catch:{ Throwable -> 0x0059 }
        r7 = r7.equals(r8);	 Catch:{ Throwable -> 0x0059 }
        if (r7 == 0) goto L_0x0056;
    L_0x0043:
        r7 = r10.currentAccount;	 Catch:{ Throwable -> 0x0059 }
        if (r4 != r7) goto L_0x0049;
    L_0x0047:
        r10.systemAccount = r1;	 Catch:{ Throwable -> 0x0059 }
    L_0x0049:
        r5 = 1;
    L_0x004a:
        if (r5 != 0) goto L_0x0053;
    L_0x004c:
        r7 = r2[r0];	 Catch:{ Exception -> 0x00a1 }
        r8 = 0;
        r9 = 0;
        r3.removeAccount(r7, r8, r9);	 Catch:{ Exception -> 0x00a1 }
    L_0x0053:
        r0 = r0 + 1;
        goto L_0x0011;
    L_0x0056:
        r4 = r4 + 1;
        goto L_0x0018;
    L_0x0059:
        r7 = move-exception;
    L_0x005a:
        r7 = r10.currentAccount;
        r7 = org.telegram.messenger.UserConfig.getInstance(r7);
        r7 = r7.isClientActivated();
        if (r7 == 0) goto L_0x009e;
    L_0x0066:
        r10.readContacts();
        r7 = r10.systemAccount;
        if (r7 != 0) goto L_0x009e;
    L_0x006d:
        r7 = new android.accounts.Account;	 Catch:{ Exception -> 0x009f }
        r8 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x009f }
        r8.<init>();	 Catch:{ Exception -> 0x009f }
        r9 = "";
        r8 = r8.append(r9);	 Catch:{ Exception -> 0x009f }
        r9 = r10.currentAccount;	 Catch:{ Exception -> 0x009f }
        r9 = org.telegram.messenger.UserConfig.getInstance(r9);	 Catch:{ Exception -> 0x009f }
        r9 = r9.getClientUserId();	 Catch:{ Exception -> 0x009f }
        r8 = r8.append(r9);	 Catch:{ Exception -> 0x009f }
        r8 = r8.toString();	 Catch:{ Exception -> 0x009f }
        r9 = "org.telegram.messenger";
        r7.<init>(r8, r9);	 Catch:{ Exception -> 0x009f }
        r10.systemAccount = r7;	 Catch:{ Exception -> 0x009f }
        r7 = r10.systemAccount;	 Catch:{ Exception -> 0x009f }
        r8 = "";
        r9 = 0;
        r3.addAccountExplicitly(r7, r8, r9);	 Catch:{ Exception -> 0x009f }
    L_0x009e:
        return;
    L_0x009f:
        r7 = move-exception;
        goto L_0x009e;
    L_0x00a1:
        r7 = move-exception;
        goto L_0x0053;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ContactsController.checkAppAccount():void");
    }

    public void deleteUnknownAppAccounts() {
        try {
            this.systemAccount = null;
            AccountManager am = AccountManager.get(ApplicationLoader.applicationContext);
            Account[] accounts = am.getAccountsByType("org.telegram.messenger");
            for (int a = 0; a < accounts.length; a++) {
                Account acc = accounts[a];
                boolean found = false;
                for (int b = 0; b < 3; b++) {
                    User user = UserConfig.getInstance(b).getCurrentUser();
                    if (user != null && acc.name.equals("" + user.id)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    try {
                        am.removeAccount(accounts[a], null, null);
                    } catch (Exception e) {
                    }
                }
            }
        } catch (Exception e2) {
            ThrowableExtension.printStackTrace(e2);
        }
    }

    public void checkContacts() {
        Utilities.globalQueue.postRunnable(new ContactsController$$Lambda$3(this));
    }

    final /* synthetic */ void lambda$checkContacts$4$ContactsController() {
        if (checkContactsInternal()) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("detected contacts change");
            }
            performSyncPhoneBook(getContactsCopy(this.contactsBook), true, false, true, false, true, false);
        }
    }

    public void forceImportContacts() {
        Utilities.globalQueue.postRunnable(new ContactsController$$Lambda$4(this));
    }

    final /* synthetic */ void lambda$forceImportContacts$5$ContactsController() {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("force import contacts");
        }
        performSyncPhoneBook(new HashMap(), true, true, true, true, false, false);
    }

    public void syncPhoneBookByAlert(HashMap<String, Contact> contacts, boolean first, boolean schedule, boolean cancel) {
        Utilities.globalQueue.postRunnable(new ContactsController$$Lambda$5(this, contacts, first, schedule, cancel));
    }

    final /* synthetic */ void lambda$syncPhoneBookByAlert$6$ContactsController(HashMap contacts, boolean first, boolean schedule, boolean cancel) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("sync contacts by alert");
        }
        performSyncPhoneBook(contacts, true, first, schedule, false, false, cancel);
    }

    public void deleteAllContacts(Runnable runnable) {
        resetImportedContacts();
        TL_contacts_deleteContacts req = new TL_contacts_deleteContacts();
        int size = this.contacts.size();
        for (int a = 0; a < size; a++) {
            req.id.add(MessagesController.getInstance(this.currentAccount).getInputUser(((TL_contact) this.contacts.get(a)).user_id));
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new ContactsController$$Lambda$6(this, runnable));
    }

    final /* synthetic */ void lambda$deleteAllContacts$8$ContactsController(Runnable runnable, TLObject response, TL_error error) {
        if (response instanceof TL_boolTrue) {
            this.contactsBookSPhones.clear();
            this.contactsBook.clear();
            this.completedRequestsCount = 0;
            this.migratingContacts = false;
            this.contactsSyncInProgress = false;
            this.contactsLoaded = false;
            this.loadingContacts = false;
            this.contactsBookLoaded = false;
            this.lastContactsVersions = "";
            AndroidUtilities.runOnUIThread(new ContactsController$$Lambda$58(this, runnable));
        }
    }

    final /* synthetic */ void lambda$null$7$ContactsController(Runnable runnable) {
        AccountManager am = AccountManager.get(ApplicationLoader.applicationContext);
        try {
            Account[] accounts = am.getAccountsByType("org.telegram.messenger");
            this.systemAccount = null;
            for (Account acc : accounts) {
                for (int b = 0; b < 3; b++) {
                    User user = UserConfig.getInstance(b).getCurrentUser();
                    if (user != null && acc.name.equals("" + user.id)) {
                        am.removeAccount(acc, null, null);
                        break;
                    }
                }
            }
        } catch (Throwable th) {
        }
        try {
            this.systemAccount = new Account("" + UserConfig.getInstance(this.currentAccount).getClientUserId(), "org.telegram.messenger");
            am.addAccountExplicitly(this.systemAccount, "", null);
        } catch (Exception e) {
        }
        MessagesStorage.getInstance(this.currentAccount).putCachedPhoneBook(new HashMap(), false, true);
        MessagesStorage.getInstance(this.currentAccount).putContacts(new ArrayList(), true);
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
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.contactsDidLoad, new Object[0]);
        loadContacts(false, 0);
        runnable.run();
    }

    public void resetImportedContacts() {
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_contacts_resetSaved(), ContactsController$$Lambda$7.$instance);
    }

    static final /* synthetic */ void lambda$resetImportedContacts$9$ContactsController(TLObject response, TL_error error) {
    }

    private boolean checkContactsInternal() {
        boolean reload = false;
        try {
            boolean z;
            if (hasContactsPermission()) {
                ContentResolver cr = ApplicationLoader.applicationContext.getContentResolver();
                Cursor pCur = null;
                try {
                    pCur = cr.query(RawContacts.CONTENT_URI, new String[]{"version"}, null, null, null);
                    if (pCur != null) {
                        StringBuilder currentVersion = new StringBuilder();
                        while (pCur.moveToNext()) {
                            currentVersion.append(pCur.getString(pCur.getColumnIndex("version")));
                        }
                        String newContactsVersion = currentVersion.toString();
                        if (!(this.lastContactsVersions.length() == 0 || this.lastContactsVersions.equals(newContactsVersion))) {
                            reload = true;
                        }
                        this.lastContactsVersions = newContactsVersion;
                    }
                    if (pCur != null) {
                        pCur.close();
                    }
                } catch (Throwable e) {
                    FileLog.e(e);
                    if (pCur != null) {
                        pCur.close();
                    }
                } catch (Throwable th) {
                    if (pCur != null) {
                        pCur.close();
                    }
                }
                z = reload;
                return reload;
            }
            z = false;
            return false;
        } catch (Throwable e2) {
            FileLog.e(e2);
        }
    }

    public void readContacts() {
        synchronized (this.loadContactsSync) {
            if (this.loadingContacts) {
                return;
            }
            this.loadingContacts = true;
            Utilities.stageQueue.postRunnable(new ContactsController$$Lambda$8(this));
        }
    }

    final /* synthetic */ void lambda$readContacts$10$ContactsController() {
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
        if (count <= 3) {
            return false;
        }
        return true;
    }

    /* JADX WARNING: Removed duplicated region for block: B:147:0x03bb A:{Catch:{ Throwable -> 0x0117, all -> 0x01ea }} */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x0383 A:{Catch:{ Throwable -> 0x0117, all -> 0x01ea }} */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x038d A:{Catch:{ Throwable -> 0x0117, all -> 0x01ea }} */
    /* JADX WARNING: Removed duplicated region for block: B:149:0x03c6 A:{Catch:{ Throwable -> 0x0117, all -> 0x01ea }} */
    /* JADX WARNING: Removed duplicated region for block: B:146:0x03b5 A:{Catch:{ Throwable -> 0x0117, all -> 0x01ea }} */
    /* JADX WARNING: Missing block: B:131:0x0361, code:
            if (r18.contains(r9.first_name) == false) goto L_0x0363;
     */
    /* JADX WARNING: Missing block: B:137:0x037f, code:
            if (r18.contains(r9.last_name) != false) goto L_0x0381;
     */
    private java.util.HashMap<java.lang.String, org.telegram.messenger.ContactsController.Contact> readContactsFromPhoneBook() {
        /*
        r34 = this;
        r0 = r34;
        r3 = r0.currentAccount;
        r3 = org.telegram.messenger.UserConfig.getInstance(r3);
        r3 = r3.syncContacts;
        if (r3 != 0) goto L_0x001c;
    L_0x000c:
        r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r3 == 0) goto L_0x0016;
    L_0x0010:
        r3 = "contacts sync disabled";
        org.telegram.messenger.FileLog.d(r3);
    L_0x0016:
        r10 = new java.util.HashMap;
        r10.<init>();
    L_0x001b:
        return r10;
    L_0x001c:
        r3 = r34.hasContactsPermission();
        if (r3 != 0) goto L_0x0032;
    L_0x0022:
        r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r3 == 0) goto L_0x002c;
    L_0x0026:
        r3 = "app has no contacts permissions";
        org.telegram.messenger.FileLog.d(r3);
    L_0x002c:
        r10 = new java.util.HashMap;
        r10.<init>();
        goto L_0x001b;
    L_0x0032:
        r28 = 0;
        r10 = 0;
        r16 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x0117 }
        r16.<init>();	 Catch:{ Throwable -> 0x0117 }
        r3 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x0117 }
        r2 = r3.getContentResolver();	 Catch:{ Throwable -> 0x0117 }
        r29 = new java.util.HashMap;	 Catch:{ Throwable -> 0x0117 }
        r29.<init>();	 Catch:{ Throwable -> 0x0117 }
        r20 = new java.util.ArrayList;	 Catch:{ Throwable -> 0x0117 }
        r20.<init>();	 Catch:{ Throwable -> 0x0117 }
        r3 = android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI;	 Catch:{ Throwable -> 0x0117 }
        r0 = r34;
        r4 = r0.projectionPhones;	 Catch:{ Throwable -> 0x0117 }
        r5 = 0;
        r6 = 0;
        r7 = 0;
        r28 = r2.query(r3, r4, r5, r6, r7);	 Catch:{ Throwable -> 0x0117 }
        r23 = 1;
        if (r28 == 0) goto L_0x0274;
    L_0x005b:
        r12 = r28.getCount();	 Catch:{ Throwable -> 0x0117 }
        if (r12 <= 0) goto L_0x026f;
    L_0x0061:
        if (r10 != 0) goto L_0x03f4;
    L_0x0063:
        r11 = new java.util.HashMap;	 Catch:{ Throwable -> 0x0117 }
        r11.<init>(r12);	 Catch:{ Throwable -> 0x0117 }
        r24 = r23;
        r10 = r11;
    L_0x006b:
        r3 = r28.moveToNext();	 Catch:{ Throwable -> 0x0117 }
        if (r3 == 0) goto L_0x026d;
    L_0x0071:
        r3 = 1;
        r0 = r28;
        r27 = r0.getString(r3);	 Catch:{ Throwable -> 0x0117 }
        r3 = 5;
        r0 = r28;
        r8 = r0.getString(r3);	 Catch:{ Throwable -> 0x0117 }
        if (r8 != 0) goto L_0x0084;
    L_0x0081:
        r8 = "";
    L_0x0084:
        r3 = ".sim";
        r3 = r8.indexOf(r3);	 Catch:{ Throwable -> 0x0117 }
        if (r3 == 0) goto L_0x012e;
    L_0x008d:
        r21 = 1;
    L_0x008f:
        r3 = android.text.TextUtils.isEmpty(r27);	 Catch:{ Throwable -> 0x0117 }
        if (r3 != 0) goto L_0x006b;
    L_0x0095:
        r3 = 1;
        r0 = r27;
        r27 = org.telegram.PhoneFormat.PhoneFormat.stripExceptNumbers(r0, r3);	 Catch:{ Throwable -> 0x0117 }
        r3 = android.text.TextUtils.isEmpty(r27);	 Catch:{ Throwable -> 0x0117 }
        if (r3 != 0) goto L_0x006b;
    L_0x00a2:
        r30 = r27;
        r3 = "+";
        r0 = r27;
        r3 = r0.startsWith(r3);	 Catch:{ Throwable -> 0x0117 }
        if (r3 == 0) goto L_0x00b6;
    L_0x00af:
        r3 = 1;
        r0 = r27;
        r30 = r0.substring(r3);	 Catch:{ Throwable -> 0x0117 }
    L_0x00b6:
        r3 = 0;
        r0 = r28;
        r25 = r0.getString(r3);	 Catch:{ Throwable -> 0x0117 }
        r3 = 0;
        r0 = r16;
        r0.setLength(r3);	 Catch:{ Throwable -> 0x0117 }
        r0 = r16;
        r1 = r25;
        android.database.DatabaseUtils.appendEscapedSQLString(r0, r1);	 Catch:{ Throwable -> 0x0117 }
        r22 = r16.toString();	 Catch:{ Throwable -> 0x0117 }
        r17 = r29.get(r30);	 Catch:{ Throwable -> 0x0117 }
        r17 = (org.telegram.messenger.ContactsController.Contact) r17;	 Catch:{ Throwable -> 0x0117 }
        if (r17 == 0) goto L_0x0132;
    L_0x00d6:
        r0 = r17;
        r3 = r0.isGoodProvider;	 Catch:{ Throwable -> 0x0117 }
        if (r3 != 0) goto L_0x006b;
    L_0x00dc:
        r0 = r17;
        r3 = r0.provider;	 Catch:{ Throwable -> 0x0117 }
        r3 = r8.equals(r3);	 Catch:{ Throwable -> 0x0117 }
        if (r3 != 0) goto L_0x006b;
    L_0x00e6:
        r3 = 0;
        r0 = r16;
        r0.setLength(r3);	 Catch:{ Throwable -> 0x0117 }
        r0 = r17;
        r3 = r0.key;	 Catch:{ Throwable -> 0x0117 }
        r0 = r16;
        android.database.DatabaseUtils.appendEscapedSQLString(r0, r3);	 Catch:{ Throwable -> 0x0117 }
        r3 = r16.toString();	 Catch:{ Throwable -> 0x0117 }
        r0 = r20;
        r0.remove(r3);	 Catch:{ Throwable -> 0x0117 }
        r0 = r20;
        r1 = r22;
        r0.add(r1);	 Catch:{ Throwable -> 0x0117 }
        r0 = r25;
        r1 = r17;
        r1.key = r0;	 Catch:{ Throwable -> 0x0117 }
        r0 = r21;
        r1 = r17;
        r1.isGoodProvider = r0;	 Catch:{ Throwable -> 0x0117 }
        r0 = r17;
        r0.provider = r8;	 Catch:{ Throwable -> 0x0117 }
        goto L_0x006b;
    L_0x0117:
        r15 = move-exception;
        org.telegram.messenger.FileLog.e(r15);	 Catch:{ all -> 0x01ea }
        if (r10 == 0) goto L_0x0120;
    L_0x011d:
        r10.clear();	 Catch:{ all -> 0x01ea }
    L_0x0120:
        if (r28 == 0) goto L_0x0125;
    L_0x0122:
        r28.close();	 Catch:{ Exception -> 0x03df }
    L_0x0125:
        if (r10 != 0) goto L_0x001b;
    L_0x0127:
        r10 = new java.util.HashMap;
        r10.<init>();
        goto L_0x001b;
    L_0x012e:
        r21 = 0;
        goto L_0x008f;
    L_0x0132:
        r0 = r20;
        r1 = r22;
        r3 = r0.contains(r1);	 Catch:{ Throwable -> 0x0117 }
        if (r3 != 0) goto L_0x0143;
    L_0x013c:
        r0 = r20;
        r1 = r22;
        r0.add(r1);	 Catch:{ Throwable -> 0x0117 }
    L_0x0143:
        r3 = 2;
        r0 = r28;
        r33 = r0.getInt(r3);	 Catch:{ Throwable -> 0x0117 }
        r0 = r25;
        r9 = r10.get(r0);	 Catch:{ Throwable -> 0x0117 }
        r9 = (org.telegram.messenger.ContactsController.Contact) r9;	 Catch:{ Throwable -> 0x0117 }
        if (r9 != 0) goto L_0x03f0;
    L_0x0154:
        r9 = new org.telegram.messenger.ContactsController$Contact;	 Catch:{ Throwable -> 0x0117 }
        r9.<init>();	 Catch:{ Throwable -> 0x0117 }
        r3 = 4;
        r0 = r28;
        r14 = r0.getString(r3);	 Catch:{ Throwable -> 0x0117 }
        if (r14 != 0) goto L_0x01bc;
    L_0x0162:
        r14 = "";
    L_0x0165:
        r0 = r34;
        r3 = r0.isNotValidNameString(r14);	 Catch:{ Throwable -> 0x0117 }
        if (r3 == 0) goto L_0x01c1;
    L_0x016d:
        r9.first_name = r14;	 Catch:{ Throwable -> 0x0117 }
        r3 = "";
        r9.last_name = r3;	 Catch:{ Throwable -> 0x0117 }
    L_0x0174:
        r9.provider = r8;	 Catch:{ Throwable -> 0x0117 }
        r0 = r21;
        r9.isGoodProvider = r0;	 Catch:{ Throwable -> 0x0117 }
        r0 = r25;
        r9.key = r0;	 Catch:{ Throwable -> 0x0117 }
        r23 = r24 + 1;
        r0 = r24;
        r9.contact_id = r0;	 Catch:{ Throwable -> 0x0117 }
        r0 = r25;
        r10.put(r0, r9);	 Catch:{ Throwable -> 0x0117 }
    L_0x0189:
        r3 = r9.shortPhones;	 Catch:{ Throwable -> 0x0117 }
        r0 = r30;
        r3.add(r0);	 Catch:{ Throwable -> 0x0117 }
        r3 = r9.phones;	 Catch:{ Throwable -> 0x0117 }
        r0 = r27;
        r3.add(r0);	 Catch:{ Throwable -> 0x0117 }
        r3 = r9.phoneDeleted;	 Catch:{ Throwable -> 0x0117 }
        r4 = 0;
        r4 = java.lang.Integer.valueOf(r4);	 Catch:{ Throwable -> 0x0117 }
        r3.add(r4);	 Catch:{ Throwable -> 0x0117 }
        if (r33 != 0) goto L_0x0205;
    L_0x01a3:
        r3 = 3;
        r0 = r28;
        r13 = r0.getString(r3);	 Catch:{ Throwable -> 0x0117 }
        r3 = r9.phoneTypes;	 Catch:{ Throwable -> 0x0117 }
        if (r13 == 0) goto L_0x01fa;
    L_0x01ae:
        r3.add(r13);	 Catch:{ Throwable -> 0x0117 }
    L_0x01b1:
        r0 = r29;
        r1 = r30;
        r0.put(r1, r9);	 Catch:{ Throwable -> 0x0117 }
        r24 = r23;
        goto L_0x006b;
    L_0x01bc:
        r14 = r14.trim();	 Catch:{ Throwable -> 0x0117 }
        goto L_0x0165;
    L_0x01c1:
        r3 = 32;
        r32 = r14.lastIndexOf(r3);	 Catch:{ Throwable -> 0x0117 }
        r3 = -1;
        r0 = r32;
        if (r0 == r3) goto L_0x01f1;
    L_0x01cc:
        r3 = 0;
        r0 = r32;
        r3 = r14.substring(r3, r0);	 Catch:{ Throwable -> 0x0117 }
        r3 = r3.trim();	 Catch:{ Throwable -> 0x0117 }
        r9.first_name = r3;	 Catch:{ Throwable -> 0x0117 }
        r3 = r32 + 1;
        r4 = r14.length();	 Catch:{ Throwable -> 0x0117 }
        r3 = r14.substring(r3, r4);	 Catch:{ Throwable -> 0x0117 }
        r3 = r3.trim();	 Catch:{ Throwable -> 0x0117 }
        r9.last_name = r3;	 Catch:{ Throwable -> 0x0117 }
        goto L_0x0174;
    L_0x01ea:
        r3 = move-exception;
        if (r28 == 0) goto L_0x01f0;
    L_0x01ed:
        r28.close();	 Catch:{ Exception -> 0x03e5 }
    L_0x01f0:
        throw r3;
    L_0x01f1:
        r9.first_name = r14;	 Catch:{ Throwable -> 0x0117 }
        r3 = "";
        r9.last_name = r3;	 Catch:{ Throwable -> 0x0117 }
        goto L_0x0174;
    L_0x01fa:
        r4 = "PhoneMobile";
        r5 = NUM; // 0x7f0CLASSNAMEce float:1.8612725E38 double:1.053098259E-314;
        r13 = org.telegram.messenger.LocaleController.getString(r4, r5);	 Catch:{ Throwable -> 0x0117 }
        goto L_0x01ae;
    L_0x0205:
        r3 = 1;
        r0 = r33;
        if (r0 != r3) goto L_0x021a;
    L_0x020a:
        r3 = r9.phoneTypes;	 Catch:{ Throwable -> 0x0117 }
        r4 = "PhoneHome";
        r5 = NUM; // 0x7f0CLASSNAMEcc float:1.8612721E38 double:1.053098258E-314;
        r4 = org.telegram.messenger.LocaleController.getString(r4, r5);	 Catch:{ Throwable -> 0x0117 }
        r3.add(r4);	 Catch:{ Throwable -> 0x0117 }
        goto L_0x01b1;
    L_0x021a:
        r3 = 2;
        r0 = r33;
        if (r0 != r3) goto L_0x022f;
    L_0x021f:
        r3 = r9.phoneTypes;	 Catch:{ Throwable -> 0x0117 }
        r4 = "PhoneMobile";
        r5 = NUM; // 0x7f0CLASSNAMEce float:1.8612725E38 double:1.053098259E-314;
        r4 = org.telegram.messenger.LocaleController.getString(r4, r5);	 Catch:{ Throwable -> 0x0117 }
        r3.add(r4);	 Catch:{ Throwable -> 0x0117 }
        goto L_0x01b1;
    L_0x022f:
        r3 = 3;
        r0 = r33;
        if (r0 != r3) goto L_0x0245;
    L_0x0234:
        r3 = r9.phoneTypes;	 Catch:{ Throwable -> 0x0117 }
        r4 = "PhoneWork";
        r5 = NUM; // 0x7f0CLASSNAMEd4 float:1.8612737E38 double:1.053098262E-314;
        r4 = org.telegram.messenger.LocaleController.getString(r4, r5);	 Catch:{ Throwable -> 0x0117 }
        r3.add(r4);	 Catch:{ Throwable -> 0x0117 }
        goto L_0x01b1;
    L_0x0245:
        r3 = 12;
        r0 = r33;
        if (r0 != r3) goto L_0x025c;
    L_0x024b:
        r3 = r9.phoneTypes;	 Catch:{ Throwable -> 0x0117 }
        r4 = "PhoneMain";
        r5 = NUM; // 0x7f0CLASSNAMEcd float:1.8612723E38 double:1.0530982586E-314;
        r4 = org.telegram.messenger.LocaleController.getString(r4, r5);	 Catch:{ Throwable -> 0x0117 }
        r3.add(r4);	 Catch:{ Throwable -> 0x0117 }
        goto L_0x01b1;
    L_0x025c:
        r3 = r9.phoneTypes;	 Catch:{ Throwable -> 0x0117 }
        r4 = "PhoneOther";
        r5 = NUM; // 0x7f0CLASSNAMEd3 float:1.8612735E38 double:1.0530982616E-314;
        r4 = org.telegram.messenger.LocaleController.getString(r4, r5);	 Catch:{ Throwable -> 0x0117 }
        r3.add(r4);	 Catch:{ Throwable -> 0x0117 }
        goto L_0x01b1;
    L_0x026d:
        r23 = r24;
    L_0x026f:
        r28.close();	 Catch:{ Exception -> 0x03eb }
    L_0x0272:
        r28 = 0;
    L_0x0274:
        r3 = ",";
        r0 = r20;
        r19 = android.text.TextUtils.join(r3, r0);	 Catch:{ Throwable -> 0x0117 }
        r3 = android.provider.ContactsContract.Data.CONTENT_URI;	 Catch:{ Throwable -> 0x0117 }
        r0 = r34;
        r4 = r0.projectionNames;	 Catch:{ Throwable -> 0x0117 }
        r5 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x0117 }
        r5.<init>();	 Catch:{ Throwable -> 0x0117 }
        r6 = "lookup IN (";
        r5 = r5.append(r6);	 Catch:{ Throwable -> 0x0117 }
        r0 = r19;
        r5 = r5.append(r0);	 Catch:{ Throwable -> 0x0117 }
        r6 = ") AND ";
        r5 = r5.append(r6);	 Catch:{ Throwable -> 0x0117 }
        r6 = "mimetype";
        r5 = r5.append(r6);	 Catch:{ Throwable -> 0x0117 }
        r6 = " = '";
        r5 = r5.append(r6);	 Catch:{ Throwable -> 0x0117 }
        r6 = "vnd.android.cursor.item/name";
        r5 = r5.append(r6);	 Catch:{ Throwable -> 0x0117 }
        r6 = "'";
        r5 = r5.append(r6);	 Catch:{ Throwable -> 0x0117 }
        r5 = r5.toString();	 Catch:{ Throwable -> 0x0117 }
        r6 = 0;
        r7 = 0;
        r28 = r2.query(r3, r4, r5, r6, r7);	 Catch:{ Throwable -> 0x0117 }
        if (r28 == 0) goto L_0x03d2;
    L_0x02c4:
        r3 = r28.moveToNext();	 Catch:{ Throwable -> 0x0117 }
        if (r3 == 0) goto L_0x03cd;
    L_0x02ca:
        r3 = 0;
        r0 = r28;
        r25 = r0.getString(r3);	 Catch:{ Throwable -> 0x0117 }
        r3 = 1;
        r0 = r28;
        r18 = r0.getString(r3);	 Catch:{ Throwable -> 0x0117 }
        r3 = 2;
        r0 = r28;
        r31 = r0.getString(r3);	 Catch:{ Throwable -> 0x0117 }
        r3 = 3;
        r0 = r28;
        r26 = r0.getString(r3);	 Catch:{ Throwable -> 0x0117 }
        r0 = r25;
        r9 = r10.get(r0);	 Catch:{ Throwable -> 0x0117 }
        r9 = (org.telegram.messenger.ContactsController.Contact) r9;	 Catch:{ Throwable -> 0x0117 }
        if (r9 == 0) goto L_0x02c4;
    L_0x02f0:
        r3 = r9.namesFilled;	 Catch:{ Throwable -> 0x0117 }
        if (r3 != 0) goto L_0x02c4;
    L_0x02f4:
        r3 = r9.isGoodProvider;	 Catch:{ Throwable -> 0x0117 }
        if (r3 == 0) goto L_0x0345;
    L_0x02f8:
        if (r18 == 0) goto L_0x0334;
    L_0x02fa:
        r0 = r18;
        r9.first_name = r0;	 Catch:{ Throwable -> 0x0117 }
    L_0x02fe:
        if (r31 == 0) goto L_0x033a;
    L_0x0300:
        r0 = r31;
        r9.last_name = r0;	 Catch:{ Throwable -> 0x0117 }
    L_0x0304:
        r3 = android.text.TextUtils.isEmpty(r26);	 Catch:{ Throwable -> 0x0117 }
        if (r3 != 0) goto L_0x0330;
    L_0x030a:
        r3 = r9.first_name;	 Catch:{ Throwable -> 0x0117 }
        r3 = android.text.TextUtils.isEmpty(r3);	 Catch:{ Throwable -> 0x0117 }
        if (r3 != 0) goto L_0x0340;
    L_0x0312:
        r3 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x0117 }
        r3.<init>();	 Catch:{ Throwable -> 0x0117 }
        r4 = r9.first_name;	 Catch:{ Throwable -> 0x0117 }
        r3 = r3.append(r4);	 Catch:{ Throwable -> 0x0117 }
        r4 = " ";
        r3 = r3.append(r4);	 Catch:{ Throwable -> 0x0117 }
        r0 = r26;
        r3 = r3.append(r0);	 Catch:{ Throwable -> 0x0117 }
        r3 = r3.toString();	 Catch:{ Throwable -> 0x0117 }
        r9.first_name = r3;	 Catch:{ Throwable -> 0x0117 }
    L_0x0330:
        r3 = 1;
        r9.namesFilled = r3;	 Catch:{ Throwable -> 0x0117 }
        goto L_0x02c4;
    L_0x0334:
        r3 = "";
        r9.first_name = r3;	 Catch:{ Throwable -> 0x0117 }
        goto L_0x02fe;
    L_0x033a:
        r3 = "";
        r9.last_name = r3;	 Catch:{ Throwable -> 0x0117 }
        goto L_0x0304;
    L_0x0340:
        r0 = r26;
        r9.first_name = r0;	 Catch:{ Throwable -> 0x0117 }
        goto L_0x0330;
    L_0x0345:
        r0 = r34;
        r1 = r18;
        r3 = r0.isNotValidNameString(r1);	 Catch:{ Throwable -> 0x0117 }
        if (r3 != 0) goto L_0x0363;
    L_0x034f:
        r3 = r9.first_name;	 Catch:{ Throwable -> 0x0117 }
        r0 = r18;
        r3 = r3.contains(r0);	 Catch:{ Throwable -> 0x0117 }
        if (r3 != 0) goto L_0x0381;
    L_0x0359:
        r3 = r9.first_name;	 Catch:{ Throwable -> 0x0117 }
        r0 = r18;
        r3 = r0.contains(r3);	 Catch:{ Throwable -> 0x0117 }
        if (r3 != 0) goto L_0x0381;
    L_0x0363:
        r0 = r34;
        r1 = r31;
        r3 = r0.isNotValidNameString(r1);	 Catch:{ Throwable -> 0x0117 }
        if (r3 != 0) goto L_0x0330;
    L_0x036d:
        r3 = r9.last_name;	 Catch:{ Throwable -> 0x0117 }
        r0 = r31;
        r3 = r3.contains(r0);	 Catch:{ Throwable -> 0x0117 }
        if (r3 != 0) goto L_0x0381;
    L_0x0377:
        r3 = r9.last_name;	 Catch:{ Throwable -> 0x0117 }
        r0 = r18;
        r3 = r0.contains(r3);	 Catch:{ Throwable -> 0x0117 }
        if (r3 == 0) goto L_0x0330;
    L_0x0381:
        if (r18 == 0) goto L_0x03bb;
    L_0x0383:
        r0 = r18;
        r9.first_name = r0;	 Catch:{ Throwable -> 0x0117 }
    L_0x0387:
        r3 = android.text.TextUtils.isEmpty(r26);	 Catch:{ Throwable -> 0x0117 }
        if (r3 != 0) goto L_0x03b3;
    L_0x038d:
        r3 = r9.first_name;	 Catch:{ Throwable -> 0x0117 }
        r3 = android.text.TextUtils.isEmpty(r3);	 Catch:{ Throwable -> 0x0117 }
        if (r3 != 0) goto L_0x03c1;
    L_0x0395:
        r3 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x0117 }
        r3.<init>();	 Catch:{ Throwable -> 0x0117 }
        r4 = r9.first_name;	 Catch:{ Throwable -> 0x0117 }
        r3 = r3.append(r4);	 Catch:{ Throwable -> 0x0117 }
        r4 = " ";
        r3 = r3.append(r4);	 Catch:{ Throwable -> 0x0117 }
        r0 = r26;
        r3 = r3.append(r0);	 Catch:{ Throwable -> 0x0117 }
        r3 = r3.toString();	 Catch:{ Throwable -> 0x0117 }
        r9.first_name = r3;	 Catch:{ Throwable -> 0x0117 }
    L_0x03b3:
        if (r31 == 0) goto L_0x03c6;
    L_0x03b5:
        r0 = r31;
        r9.last_name = r0;	 Catch:{ Throwable -> 0x0117 }
        goto L_0x0330;
    L_0x03bb:
        r3 = "";
        r9.first_name = r3;	 Catch:{ Throwable -> 0x0117 }
        goto L_0x0387;
    L_0x03c1:
        r0 = r26;
        r9.first_name = r0;	 Catch:{ Throwable -> 0x0117 }
        goto L_0x03b3;
    L_0x03c6:
        r3 = "";
        r9.last_name = r3;	 Catch:{ Throwable -> 0x0117 }
        goto L_0x0330;
    L_0x03cd:
        r28.close();	 Catch:{ Exception -> 0x03ee }
    L_0x03d0:
        r28 = 0;
    L_0x03d2:
        if (r28 == 0) goto L_0x0125;
    L_0x03d4:
        r28.close();	 Catch:{ Exception -> 0x03d9 }
        goto L_0x0125;
    L_0x03d9:
        r15 = move-exception;
        org.telegram.messenger.FileLog.e(r15);
        goto L_0x0125;
    L_0x03df:
        r15 = move-exception;
        org.telegram.messenger.FileLog.e(r15);
        goto L_0x0125;
    L_0x03e5:
        r15 = move-exception;
        org.telegram.messenger.FileLog.e(r15);
        goto L_0x01f0;
    L_0x03eb:
        r3 = move-exception;
        goto L_0x0272;
    L_0x03ee:
        r3 = move-exception;
        goto L_0x03d0;
    L_0x03f0:
        r23 = r24;
        goto L_0x0189;
    L_0x03f4:
        r24 = r23;
        goto L_0x006b;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ContactsController.readContactsFromPhoneBook():java.util.HashMap<java.lang.String, org.telegram.messenger.ContactsController$Contact>");
    }

    public HashMap<String, Contact> getContactsCopy(HashMap<String, Contact> original) {
        HashMap<String, Contact> ret = new HashMap();
        for (Entry<String, Contact> entry : original.entrySet()) {
            Contact copyContact = new Contact();
            Contact originalContact = (Contact) entry.getValue();
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

    protected void migratePhoneBookToV7(SparseArray<Contact> contactHashMap) {
        Utilities.globalQueue.postRunnable(new ContactsController$$Lambda$9(this, contactHashMap));
    }

    final /* synthetic */ void lambda$migratePhoneBookToV7$11$ContactsController(SparseArray contactHashMap) {
        if (!this.migratingContacts) {
            Contact value;
            int a;
            this.migratingContacts = true;
            HashMap<String, Contact> migratedMap = new HashMap();
            HashMap<String, Contact> contactsMap = readContactsFromPhoneBook();
            HashMap<String, String> contactsBookShort = new HashMap();
            for (Entry<String, Contact> entry : contactsMap.entrySet()) {
                value = (Contact) entry.getValue();
                for (a = 0; a < value.shortPhones.size(); a++) {
                    contactsBookShort.put(value.shortPhones.get(a), value.key);
                }
            }
            for (int b = 0; b < contactHashMap.size(); b++) {
                value = (Contact) contactHashMap.valueAt(b);
                for (a = 0; a < value.shortPhones.size(); a++) {
                    String key = (String) contactsBookShort.get((String) value.shortPhones.get(a));
                    if (key != null) {
                        value.key = key;
                        migratedMap.put(key, value);
                        break;
                    }
                }
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("migrated contacts " + migratedMap.size() + " of " + contactHashMap.size());
            }
            MessagesStorage.getInstance(this.currentAccount).putCachedPhoneBook(migratedMap, true, false);
        }
    }

    protected void performSyncPhoneBook(HashMap<String, Contact> contactHashMap, boolean request, boolean first, boolean schedule, boolean force, boolean checkCount, boolean canceled) {
        if (first || this.contactsBookLoaded) {
            Utilities.globalQueue.postRunnable(new ContactsController$$Lambda$10(this, contactHashMap, schedule, request, first, force, checkCount, canceled));
        }
    }

    final /* synthetic */ void lambda$performSyncPhoneBook$24$ContactsController(HashMap contactHashMap, boolean schedule, boolean request, boolean first, boolean force, boolean checkCount, boolean canceled) {
        Contact c;
        int a;
        String key;
        Contact value;
        int newPhonebookContacts = 0;
        int serverContactsInPhonebook = 0;
        HashMap<String, Contact> contactShortHashMap = new HashMap();
        for (Entry<String, Contact> entry : contactHashMap.entrySet()) {
            c = (Contact) entry.getValue();
            for (a = 0; a < c.shortPhones.size(); a++) {
                contactShortHashMap.put(c.shortPhones.get(a), c);
            }
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("start read contacts from phone");
        }
        if (!schedule) {
            checkContactsInternal();
        }
        HashMap<String, Contact> contactsMap = readContactsFromPhoneBook();
        HashMap<String, ArrayList<Object>> phoneBookSectionsDictFinal = new HashMap();
        HashMap<String, Contact> phoneBookByShortPhonesFinal = new HashMap();
        ArrayList<String> phoneBookSectionsArrayFinal = new ArrayList();
        for (Entry<String, Contact> entry2 : contactsMap.entrySet()) {
            Contact contact = (Contact) entry2.getValue();
            int size = contact.shortPhones.size();
            for (a = 0; a < size; a++) {
                String phone = (String) contact.shortPhones.get(a);
                phoneBookByShortPhonesFinal.put(phone.substring(Math.max(0, phone.length() - 7)), contact);
            }
            key = contact.getLetter();
            ArrayList<Object> arrayList = (ArrayList) phoneBookSectionsDictFinal.get(key);
            if (arrayList == null) {
                arrayList = new ArrayList();
                phoneBookSectionsDictFinal.put(key, arrayList);
                phoneBookSectionsArrayFinal.add(key);
            }
            arrayList.add(contact);
        }
        HashMap<String, Contact> contactsBookShort = new HashMap();
        int alreadyImportedContacts = contactHashMap.size();
        ArrayList<TL_inputPhoneContact> toImport = new ArrayList();
        String sphone;
        String sphone9;
        TL_inputPhoneContact imp;
        TL_contact contact2;
        User user;
        String firstName;
        String lastName;
        if (!contactHashMap.isEmpty()) {
            for (Entry<String, Contact> pair : contactsMap.entrySet()) {
                String id = (String) pair.getKey();
                value = (Contact) pair.getValue();
                Contact existing = (Contact) contactHashMap.get(id);
                if (existing == null) {
                    for (a = 0; a < value.shortPhones.size(); a++) {
                        c = (Contact) contactShortHashMap.get(value.shortPhones.get(a));
                        if (c != null) {
                            existing = c;
                            id = existing.key;
                            break;
                        }
                    }
                }
                if (existing != null) {
                    value.imported = existing.imported;
                }
                boolean nameChanged = (existing == null || ((TextUtils.isEmpty(value.first_name) || existing.first_name.equals(value.first_name)) && (TextUtils.isEmpty(value.last_name) || existing.last_name.equals(value.last_name)))) ? false : true;
                int index;
                if (existing == null || nameChanged) {
                    for (a = 0; a < value.phones.size(); a++) {
                        sphone = (String) value.shortPhones.get(a);
                        sphone9 = sphone.substring(Math.max(0, sphone.length() - 7));
                        contactsBookShort.put(sphone, value);
                        if (existing != null) {
                            index = existing.shortPhones.indexOf(sphone);
                            if (index != -1) {
                                Integer deleted = (Integer) existing.phoneDeleted.get(index);
                                value.phoneDeleted.set(a, deleted);
                                if (deleted.intValue() == 1) {
                                }
                            }
                        }
                        if (request) {
                            if (!nameChanged) {
                                if (this.contactsByPhone.containsKey(sphone)) {
                                    serverContactsInPhonebook++;
                                } else {
                                    newPhonebookContacts++;
                                }
                            }
                            imp = new TL_inputPhoneContact();
                            imp.client_id = (long) value.contact_id;
                            imp.client_id |= ((long) a) << 32;
                            imp.first_name = value.first_name;
                            imp.last_name = value.last_name;
                            imp.phone = (String) value.phones.get(a);
                            toImport.add(imp);
                        }
                    }
                    if (existing != null) {
                        contactHashMap.remove(id);
                    }
                } else {
                    for (a = 0; a < value.phones.size(); a++) {
                        sphone = (String) value.shortPhones.get(a);
                        sphone9 = sphone.substring(Math.max(0, sphone.length() - 7));
                        contactsBookShort.put(sphone, value);
                        index = existing.shortPhones.indexOf(sphone);
                        boolean emptyNameReimport = false;
                        if (request) {
                            contact2 = (TL_contact) this.contactsByPhone.get(sphone);
                            if (contact2 != null) {
                                user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(contact2.user_id));
                                if (user != null) {
                                    serverContactsInPhonebook++;
                                    if (TextUtils.isEmpty(user.first_name) && TextUtils.isEmpty(user.last_name) && !(TextUtils.isEmpty(value.first_name) && TextUtils.isEmpty(value.last_name))) {
                                        index = -1;
                                        emptyNameReimport = true;
                                    }
                                }
                            } else if (this.contactsByShortPhone.containsKey(sphone9)) {
                                serverContactsInPhonebook++;
                            }
                        }
                        if (index != -1) {
                            value.phoneDeleted.set(a, existing.phoneDeleted.get(index));
                            existing.phones.remove(index);
                            existing.shortPhones.remove(index);
                            existing.phoneDeleted.remove(index);
                            existing.phoneTypes.remove(index);
                        } else if (request) {
                            if (!emptyNameReimport) {
                                contact2 = (TL_contact) this.contactsByPhone.get(sphone);
                                if (contact2 != null) {
                                    user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(contact2.user_id));
                                    if (user != null) {
                                        serverContactsInPhonebook++;
                                        firstName = user.first_name != null ? user.first_name : "";
                                        lastName = user.last_name != null ? user.last_name : "";
                                        if (firstName.equals(value.first_name)) {
                                            if (lastName.equals(value.last_name)) {
                                            }
                                        }
                                        if (TextUtils.isEmpty(value.first_name) && TextUtils.isEmpty(value.last_name)) {
                                        }
                                    } else {
                                        newPhonebookContacts++;
                                    }
                                } else if (this.contactsByShortPhone.containsKey(sphone9)) {
                                    serverContactsInPhonebook++;
                                }
                            }
                            imp = new TL_inputPhoneContact();
                            imp.client_id = (long) value.contact_id;
                            imp.client_id |= ((long) a) << 32;
                            imp.first_name = value.first_name;
                            imp.last_name = value.last_name;
                            imp.phone = (String) value.phones.get(a);
                            toImport.add(imp);
                        }
                    }
                    if (existing.phones.isEmpty()) {
                        contactHashMap.remove(id);
                    }
                }
            }
            if (!first && contactHashMap.isEmpty() && toImport.isEmpty() && alreadyImportedContacts == contactsMap.size()) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("contacts not changed!");
                    return;
                }
                return;
            } else if (!(!request || contactHashMap.isEmpty() || contactsMap.isEmpty())) {
                if (toImport.isEmpty()) {
                    MessagesStorage.getInstance(this.currentAccount).putCachedPhoneBook(contactsMap, false, false);
                }
                if (!(true || contactHashMap.isEmpty())) {
                    AndroidUtilities.runOnUIThread(new ContactsController$$Lambda$46(this, contactHashMap));
                }
            }
        } else if (request) {
            for (Entry<String, Contact> pair2 : contactsMap.entrySet()) {
                value = (Contact) pair2.getValue();
                key = (String) pair2.getKey();
                for (a = 0; a < value.phones.size(); a++) {
                    if (!force) {
                        sphone = (String) value.shortPhones.get(a);
                        sphone9 = sphone.substring(Math.max(0, sphone.length() - 7));
                        contact2 = (TL_contact) this.contactsByPhone.get(sphone);
                        if (contact2 != null) {
                            user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(contact2.user_id));
                            if (user != null) {
                                serverContactsInPhonebook++;
                                firstName = user.first_name != null ? user.first_name : "";
                                lastName = user.last_name != null ? user.last_name : "";
                                if (firstName.equals(value.first_name)) {
                                    if (lastName.equals(value.last_name)) {
                                    }
                                }
                                if (TextUtils.isEmpty(value.first_name) && TextUtils.isEmpty(value.last_name)) {
                                }
                            }
                        } else if (this.contactsByShortPhone.containsKey(sphone9)) {
                            serverContactsInPhonebook++;
                        }
                    }
                    imp = new TL_inputPhoneContact();
                    imp.client_id = (long) value.contact_id;
                    imp.client_id |= ((long) a) << 32;
                    imp.first_name = value.first_name;
                    imp.last_name = value.last_name;
                    imp.phone = (String) value.phones.get(a);
                    toImport.add(imp);
                }
            }
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("done processing contacts");
        }
        if (!request) {
            Utilities.stageQueue.postRunnable(new ContactsController$$Lambda$51(this, contactsBookShort, contactsMap, first, phoneBookSectionsDictFinal, phoneBookSectionsArrayFinal, phoneBookByShortPhonesFinal));
            if (!contactsMap.isEmpty()) {
                MessagesStorage.getInstance(this.currentAccount).putCachedPhoneBook(contactsMap, false, false);
            }
        } else if (toImport.isEmpty()) {
            Utilities.stageQueue.postRunnable(new ContactsController$$Lambda$50(this, contactsBookShort, contactsMap, first, phoneBookSectionsDictFinal, phoneBookSectionsArrayFinal, phoneBookByShortPhonesFinal));
        } else {
            int checkType;
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("start import contacts");
            }
            if (!checkCount || newPhonebookContacts == 0) {
                checkType = 0;
            } else if (newPhonebookContacts >= 30) {
                checkType = 1;
            } else if (first && alreadyImportedContacts == 0 && this.contactsByPhone.size() - serverContactsInPhonebook > (this.contactsByPhone.size() / 3) * 2) {
                checkType = 2;
            } else {
                checkType = 0;
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("new phone book contacts " + newPhonebookContacts + " serverContactsInPhonebook " + serverContactsInPhonebook + " totalContacts " + this.contactsByPhone.size());
            }
            if (checkType != 0) {
                AndroidUtilities.runOnUIThread(new ContactsController$$Lambda$47(this, checkType, contactHashMap, first, schedule));
            } else if (canceled) {
                Utilities.stageQueue.postRunnable(new ContactsController$$Lambda$48(this, contactsBookShort, contactsMap, first, phoneBookSectionsDictFinal, phoneBookSectionsArrayFinal, phoneBookByShortPhonesFinal));
            } else {
                boolean[] hasErrors = new boolean[]{false};
                HashMap<String, Contact> hashMap = new HashMap(contactsMap);
                SparseArray<String> contactIdToKey = new SparseArray();
                for (Entry<String, Contact> entry22 : hashMap.entrySet()) {
                    value = (Contact) entry22.getValue();
                    contactIdToKey.put(value.contact_id, value.key);
                }
                this.completedRequestsCount = 0;
                int count = (int) Math.ceil(((double) toImport.size()) / 500.0d);
                for (a = 0; a < count; a++) {
                    TLObject req = new TL_contacts_importContacts();
                    int start = a * 500;
                    req.contacts = new ArrayList(toImport.subList(start, Math.min(start + 500, toImport.size())));
                    ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new ContactsController$$Lambda$49(this, hashMap, contactIdToKey, hasErrors, contactsMap, req, count, contactsBookShort, first, phoneBookSectionsDictFinal, phoneBookSectionsArrayFinal, phoneBookByShortPhonesFinal), 6);
                }
            }
        }
    }

    final /* synthetic */ void lambda$null$12$ContactsController(HashMap contactHashMap) {
        ArrayList<User> toDelete = new ArrayList();
        if (!(contactHashMap == null || contactHashMap.isEmpty())) {
            try {
                int a;
                User user;
                HashMap<String, User> contactsPhonesShort = new HashMap();
                for (a = 0; a < this.contacts.size(); a++) {
                    user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(((TL_contact) this.contacts.get(a)).user_id));
                    if (!(user == null || TextUtils.isEmpty(user.phone))) {
                        contactsPhonesShort.put(user.phone, user);
                    }
                }
                int removed = 0;
                for (Entry<String, Contact> entry : contactHashMap.entrySet()) {
                    Contact contact = (Contact) entry.getValue();
                    boolean was = false;
                    a = 0;
                    while (a < contact.shortPhones.size()) {
                        user = (User) contactsPhonesShort.get((String) contact.shortPhones.get(a));
                        if (user != null) {
                            was = true;
                            toDelete.add(user);
                            contact.shortPhones.remove(a);
                            a--;
                        }
                        a++;
                    }
                    if (!was || contact.shortPhones.size() == 0) {
                        removed++;
                    }
                }
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }
        if (!toDelete.isEmpty()) {
            deleteContact(toDelete);
        }
    }

    final /* synthetic */ void lambda$null$13$ContactsController(int checkType, HashMap contactHashMap, boolean first, boolean schedule) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.hasNewContactsToImport, Integer.valueOf(checkType), contactHashMap, Boolean.valueOf(first), Boolean.valueOf(schedule));
    }

    final /* synthetic */ void lambda$null$15$ContactsController(HashMap contactsBookShort, HashMap contactsMap, boolean first, HashMap phoneBookSectionsDictFinal, ArrayList phoneBookSectionsArrayFinal, HashMap phoneBookByShortPhonesFinal) {
        this.contactsBookSPhones = contactsBookShort;
        this.contactsBook = contactsMap;
        this.contactsSyncInProgress = false;
        this.contactsBookLoaded = true;
        if (first) {
            this.contactsLoaded = true;
        }
        if (!this.delayedContactsUpdate.isEmpty() && this.contactsLoaded) {
            applyContactsUpdates(this.delayedContactsUpdate, null, null, null);
            this.delayedContactsUpdate.clear();
        }
        MessagesStorage.getInstance(this.currentAccount).putCachedPhoneBook(contactsMap, false, false);
        AndroidUtilities.runOnUIThread(new ContactsController$$Lambda$57(this, phoneBookSectionsDictFinal, phoneBookSectionsArrayFinal, phoneBookByShortPhonesFinal));
    }

    final /* synthetic */ void lambda$null$14$ContactsController(HashMap phoneBookSectionsDictFinal, ArrayList phoneBookSectionsArrayFinal, HashMap phoneBookByShortPhonesFinal) {
        mergePhonebookAndTelegramContacts(phoneBookSectionsDictFinal, phoneBookSectionsArrayFinal, phoneBookByShortPhonesFinal);
        updateUnregisteredContacts();
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.contactsDidLoad, new Object[0]);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.contactsImported, new Object[0]);
    }

    final /* synthetic */ void lambda$null$19$ContactsController(HashMap contactsMapToSave, SparseArray contactIdToKey, boolean[] hasErrors, HashMap contactsMap, TL_contacts_importContacts req, int count, HashMap contactsBookShort, boolean first, HashMap phoneBookSectionsDictFinal, ArrayList phoneBookSectionsArrayFinal, HashMap phoneBookByShortPhonesFinal, TLObject response, TL_error error) {
        this.completedRequestsCount++;
        int a1;
        if (error == null) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("contacts imported");
            }
            TL_contacts_importedContacts res = (TL_contacts_importedContacts) response;
            if (!res.retry_contacts.isEmpty()) {
                for (a1 = 0; a1 < res.retry_contacts.size(); a1++) {
                    contactsMapToSave.remove(contactIdToKey.get((int) ((Long) res.retry_contacts.get(a1)).longValue()));
                }
                hasErrors[0] = true;
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("result has retry contacts");
                }
            }
            for (a1 = 0; a1 < res.popular_invites.size(); a1++) {
                TL_popularContact popularContact = (TL_popularContact) res.popular_invites.get(a1);
                Contact contact = (Contact) contactsMap.get(contactIdToKey.get((int) popularContact.client_id));
                if (contact != null) {
                    contact.imported = popularContact.importers;
                }
            }
            MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(res.users, null, true, true);
            ArrayList<TL_contact> cArr = new ArrayList();
            for (a1 = 0; a1 < res.imported.size(); a1++) {
                TL_contact contact2 = new TL_contact();
                contact2.user_id = ((TL_importedContact) res.imported.get(a1)).user_id;
                cArr.add(contact2);
            }
            processLoadedContacts(cArr, res.users, 2);
        } else {
            for (a1 = 0; a1 < req.contacts.size(); a1++) {
                contactsMapToSave.remove(contactIdToKey.get((int) ((TL_inputPhoneContact) req.contacts.get(a1)).client_id));
            }
            hasErrors[0] = true;
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("import contacts error " + error.text);
            }
        }
        if (this.completedRequestsCount == count) {
            if (!contactsMapToSave.isEmpty()) {
                MessagesStorage.getInstance(this.currentAccount).putCachedPhoneBook(contactsMapToSave, false, false);
            }
            Utilities.stageQueue.postRunnable(new ContactsController$$Lambda$54(this, contactsBookShort, contactsMap, first, phoneBookSectionsDictFinal, phoneBookSectionsArrayFinal, phoneBookByShortPhonesFinal, hasErrors));
        }
    }

    final /* synthetic */ void lambda$null$18$ContactsController(HashMap contactsBookShort, HashMap contactsMap, boolean first, HashMap phoneBookSectionsDictFinal, ArrayList phoneBookSectionsArrayFinal, HashMap phoneBookByShortPhonesFinal, boolean[] hasErrors) {
        this.contactsBookSPhones = contactsBookShort;
        this.contactsBook = contactsMap;
        this.contactsSyncInProgress = false;
        this.contactsBookLoaded = true;
        if (first) {
            this.contactsLoaded = true;
        }
        if (!this.delayedContactsUpdate.isEmpty() && this.contactsLoaded) {
            applyContactsUpdates(this.delayedContactsUpdate, null, null, null);
            this.delayedContactsUpdate.clear();
        }
        AndroidUtilities.runOnUIThread(new ContactsController$$Lambda$55(this, phoneBookSectionsDictFinal, phoneBookSectionsArrayFinal, phoneBookByShortPhonesFinal));
        if (hasErrors[0]) {
            Utilities.globalQueue.postRunnable(new ContactsController$$Lambda$56(this), 300000);
        }
    }

    final /* synthetic */ void lambda$null$16$ContactsController(HashMap phoneBookSectionsDictFinal, ArrayList phoneBookSectionsArrayFinal, HashMap phoneBookByShortPhonesFinal) {
        mergePhonebookAndTelegramContacts(phoneBookSectionsDictFinal, phoneBookSectionsArrayFinal, phoneBookByShortPhonesFinal);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.contactsImported, new Object[0]);
    }

    final /* synthetic */ void lambda$null$17$ContactsController() {
        MessagesStorage.getInstance(this.currentAccount).getCachedPhoneBook(true);
    }

    final /* synthetic */ void lambda$null$21$ContactsController(HashMap contactsBookShort, HashMap contactsMap, boolean first, HashMap phoneBookSectionsDictFinal, ArrayList phoneBookSectionsArrayFinal, HashMap phoneBookByShortPhonesFinal) {
        this.contactsBookSPhones = contactsBookShort;
        this.contactsBook = contactsMap;
        this.contactsSyncInProgress = false;
        this.contactsBookLoaded = true;
        if (first) {
            this.contactsLoaded = true;
        }
        if (!this.delayedContactsUpdate.isEmpty() && this.contactsLoaded) {
            applyContactsUpdates(this.delayedContactsUpdate, null, null, null);
            this.delayedContactsUpdate.clear();
        }
        AndroidUtilities.runOnUIThread(new ContactsController$$Lambda$53(this, phoneBookSectionsDictFinal, phoneBookSectionsArrayFinal, phoneBookByShortPhonesFinal));
    }

    final /* synthetic */ void lambda$null$20$ContactsController(HashMap phoneBookSectionsDictFinal, ArrayList phoneBookSectionsArrayFinal, HashMap phoneBookByShortPhonesFinal) {
        mergePhonebookAndTelegramContacts(phoneBookSectionsDictFinal, phoneBookSectionsArrayFinal, phoneBookByShortPhonesFinal);
        updateUnregisteredContacts();
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.contactsDidLoad, new Object[0]);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.contactsImported, new Object[0]);
    }

    final /* synthetic */ void lambda$null$23$ContactsController(HashMap contactsBookShort, HashMap contactsMap, boolean first, HashMap phoneBookSectionsDictFinal, ArrayList phoneBookSectionsArrayFinal, HashMap phoneBookByShortPhonesFinal) {
        this.contactsBookSPhones = contactsBookShort;
        this.contactsBook = contactsMap;
        this.contactsSyncInProgress = false;
        this.contactsBookLoaded = true;
        if (first) {
            this.contactsLoaded = true;
        }
        if (!this.delayedContactsUpdate.isEmpty() && this.contactsLoaded && this.contactsBookLoaded) {
            applyContactsUpdates(this.delayedContactsUpdate, null, null, null);
            this.delayedContactsUpdate.clear();
        }
        AndroidUtilities.runOnUIThread(new ContactsController$$Lambda$52(this, phoneBookSectionsDictFinal, phoneBookSectionsArrayFinal, phoneBookByShortPhonesFinal));
    }

    public boolean isLoadingContacts() {
        boolean z;
        synchronized (this.loadContactsSync) {
            z = this.loadingContacts;
        }
        return z;
    }

    private int getContactsHash(ArrayList<TL_contact> contacts) {
        long acc = 0;
        ArrayList<TL_contact> contacts2 = new ArrayList(contacts);
        Collections.sort(contacts2, ContactsController$$Lambda$11.$instance);
        int count = contacts2.size();
        for (int a = -1; a < count; a++) {
            long j;
            int i;
            if (a == -1) {
                j = (acc * 20261) + 2147483648L;
                i = UserConfig.getInstance(this.currentAccount).contactsSavedCount;
            } else {
                j = (acc * 20261) + 2147483648L;
                i = ((TL_contact) contacts2.get(a)).user_id;
            }
            acc = (j + ((long) i)) % 2147483648L;
        }
        return (int) acc;
    }

    static final /* synthetic */ int lambda$getContactsHash$25$ContactsController(TL_contact tl_contact, TL_contact tl_contact2) {
        if (tl_contact.user_id > tl_contact2.user_id) {
            return 1;
        }
        if (tl_contact.user_id < tl_contact2.user_id) {
            return -1;
        }
        return 0;
    }

    public void loadContacts(boolean fromCache, int hash) {
        synchronized (this.loadContactsSync) {
            this.loadingContacts = true;
        }
        if (fromCache) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("load contacts from cache");
            }
            MessagesStorage.getInstance(this.currentAccount).getContacts();
            return;
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("load contacts from server");
        }
        TL_contacts_getContacts req = new TL_contacts_getContacts();
        req.hash = hash;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new ContactsController$$Lambda$12(this, hash));
    }

    final /* synthetic */ void lambda$loadContacts$27$ContactsController(int hash, TLObject response, TL_error error) {
        if (error == null) {
            contacts_Contacts res = (contacts_Contacts) response;
            if (hash == 0 || !(res instanceof TL_contacts_contactsNotModified)) {
                UserConfig.getInstance(this.currentAccount).contactsSavedCount = res.saved_count;
                UserConfig.getInstance(this.currentAccount).saveConfig(false);
                processLoadedContacts(res.contacts, res.users, 0);
                return;
            }
            this.contactsLoaded = true;
            if (!this.delayedContactsUpdate.isEmpty() && this.contactsBookLoaded) {
                applyContactsUpdates(this.delayedContactsUpdate, null, null, null);
                this.delayedContactsUpdate.clear();
            }
            UserConfig.getInstance(this.currentAccount).lastContactsSyncTime = (int) (System.currentTimeMillis() / 1000);
            UserConfig.getInstance(this.currentAccount).saveConfig(false);
            AndroidUtilities.runOnUIThread(new ContactsController$$Lambda$45(this));
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("load contacts don't change");
            }
        }
    }

    final /* synthetic */ void lambda$null$26$ContactsController() {
        synchronized (this.loadContactsSync) {
            this.loadingContacts = false;
        }
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.contactsDidLoad, new Object[0]);
    }

    public void processLoadedContacts(ArrayList<TL_contact> contactsArr, ArrayList<User> usersArr, int from) {
        AndroidUtilities.runOnUIThread(new ContactsController$$Lambda$13(this, usersArr, from, contactsArr));
    }

    final /* synthetic */ void lambda$processLoadedContacts$35$ContactsController(ArrayList usersArr, int from, ArrayList contactsArr) {
        int a;
        boolean z = true;
        MessagesController instance = MessagesController.getInstance(this.currentAccount);
        if (from != 1) {
            z = false;
        }
        instance.putUsers(usersArr, z);
        SparseArray<User> usersDict = new SparseArray();
        boolean isEmpty = contactsArr.isEmpty();
        if (!this.contacts.isEmpty()) {
            a = 0;
            while (a < contactsArr.size()) {
                if (this.contactsDict.get(Integer.valueOf(((TL_contact) contactsArr.get(a)).user_id)) != null) {
                    contactsArr.remove(a);
                    a--;
                }
                a++;
            }
            contactsArr.addAll(this.contacts);
        }
        for (a = 0; a < contactsArr.size(); a++) {
            User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(((TL_contact) contactsArr.get(a)).user_id));
            if (user != null) {
                usersDict.put(user.id, user);
            }
        }
        Utilities.stageQueue.postRunnable(new ContactsController$$Lambda$38(this, from, contactsArr, usersDict, usersArr, isEmpty));
    }

    final /* synthetic */ void lambda$null$34$ContactsController(int from, ArrayList contactsArr, SparseArray usersDict, ArrayList usersArr, boolean isEmpty) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("done loading contacts");
        }
        if (from == 1 && (contactsArr.isEmpty() || Math.abs((System.currentTimeMillis() / 1000) - ((long) UserConfig.getInstance(this.currentAccount).lastContactsSyncTime)) >= 86400)) {
            loadContacts(false, getContactsHash(contactsArr));
            if (contactsArr.isEmpty()) {
                return;
            }
        }
        if (from == 0) {
            UserConfig.getInstance(this.currentAccount).lastContactsSyncTime = (int) (System.currentTimeMillis() / 1000);
            UserConfig.getInstance(this.currentAccount).saveConfig(false);
        }
        int a = 0;
        while (a < contactsArr.size()) {
            TL_contact contact = (TL_contact) contactsArr.get(a);
            if (usersDict.get(contact.user_id) != null || contact.user_id == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                a++;
            } else {
                loadContacts(false, 0);
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("contacts are broken, load from server");
                    return;
                }
                return;
            }
        }
        if (from != 1) {
            MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(usersArr, null, true, true);
            MessagesStorage.getInstance(this.currentAccount).putContacts(contactsArr, from != 2);
        }
        Collections.sort(contactsArr, new ContactsController$$Lambda$39(usersDict));
        ConcurrentHashMap<Integer, TL_contact> contactsDictionary = new ConcurrentHashMap(20, 1.0f, 2);
        HashMap<String, ArrayList<TL_contact>> sectionsDict = new HashMap();
        HashMap<String, ArrayList<TL_contact>> sectionsDictMutual = new HashMap();
        ArrayList<String> sortedSectionsArray = new ArrayList();
        ArrayList<String> sortedSectionsArrayMutual = new ArrayList();
        HashMap<String, TL_contact> contactsByPhonesDict = null;
        HashMap<String, TL_contact> contactsByPhonesShortDict = null;
        if (!this.contactsBookLoaded) {
            contactsByPhonesDict = new HashMap();
            contactsByPhonesShortDict = new HashMap();
        }
        HashMap<String, TL_contact> contactsByPhonesDictFinal = contactsByPhonesDict;
        HashMap<String, TL_contact> contactsByPhonesShortDictFinal = contactsByPhonesShortDict;
        for (a = 0; a < contactsArr.size(); a++) {
            TL_contact value = (TL_contact) contactsArr.get(a);
            User user = (User) usersDict.get(value.user_id);
            if (user != null) {
                contactsDictionary.put(Integer.valueOf(value.user_id), value);
                if (!(contactsByPhonesDict == null || TextUtils.isEmpty(user.phone))) {
                    contactsByPhonesDict.put(user.phone, value);
                    contactsByPhonesShortDict.put(user.phone.substring(Math.max(0, user.phone.length() - 7)), value);
                }
                String key = UserObject.getFirstName(user);
                if (key.length() > 1) {
                    key = key.substring(0, 1);
                }
                if (key.length() == 0) {
                    key = "#";
                } else {
                    key = key.toUpperCase();
                }
                String replace = (String) this.sectionsToReplace.get(key);
                if (replace != null) {
                    key = replace;
                }
                ArrayList<TL_contact> arr = (ArrayList) sectionsDict.get(key);
                if (arr == null) {
                    arr = new ArrayList();
                    sectionsDict.put(key, arr);
                    sortedSectionsArray.add(key);
                }
                arr.add(value);
                if (user.mutual_contact) {
                    arr = (ArrayList) sectionsDictMutual.get(key);
                    if (arr == null) {
                        arr = new ArrayList();
                        sectionsDictMutual.put(key, arr);
                        sortedSectionsArrayMutual.add(key);
                    }
                    arr.add(value);
                }
            }
        }
        Collections.sort(sortedSectionsArray, ContactsController$$Lambda$40.$instance);
        Collections.sort(sortedSectionsArrayMutual, ContactsController$$Lambda$41.$instance);
        AndroidUtilities.runOnUIThread(new ContactsController$$Lambda$42(this, contactsArr, contactsDictionary, sectionsDict, sectionsDictMutual, sortedSectionsArray, sortedSectionsArrayMutual, from, isEmpty));
        if (!this.delayedContactsUpdate.isEmpty() && this.contactsLoaded && this.contactsBookLoaded) {
            applyContactsUpdates(this.delayedContactsUpdate, null, null, null);
            this.delayedContactsUpdate.clear();
        }
        if (contactsByPhonesDictFinal != null) {
            AndroidUtilities.runOnUIThread(new ContactsController$$Lambda$43(this, contactsByPhonesDictFinal, contactsByPhonesShortDictFinal));
        } else {
            this.contactsLoaded = true;
        }
    }

    static final /* synthetic */ int lambda$null$29$ContactsController(String s, String s2) {
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

    static final /* synthetic */ int lambda$null$30$ContactsController(String s, String s2) {
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

    final /* synthetic */ void lambda$null$31$ContactsController(ArrayList contactsArr, ConcurrentHashMap contactsDictionary, HashMap sectionsDict, HashMap sectionsDictMutual, ArrayList sortedSectionsArray, ArrayList sortedSectionsArrayMutual, int from, boolean isEmpty) {
        this.contacts = contactsArr;
        this.contactsDict = contactsDictionary;
        this.usersSectionsDict = sectionsDict;
        this.usersMutualSectionsDict = sectionsDictMutual;
        this.sortedUsersSectionsArray = sortedSectionsArray;
        this.sortedUsersMutualSectionsArray = sortedSectionsArrayMutual;
        if (from != 2) {
            synchronized (this.loadContactsSync) {
                this.loadingContacts = false;
            }
        }
        performWriteContactsToPhoneBook();
        updateUnregisteredContacts();
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.contactsDidLoad, new Object[0]);
        if (from == 1 || isEmpty) {
            reloadContactsStatusesMaybe();
        } else {
            saveContactsLoadTime();
        }
    }

    final /* synthetic */ void lambda$null$33$ContactsController(HashMap contactsByPhonesDictFinal, HashMap contactsByPhonesShortDictFinal) {
        Utilities.globalQueue.postRunnable(new ContactsController$$Lambda$44(this, contactsByPhonesDictFinal, contactsByPhonesShortDictFinal));
        if (!this.contactsSyncInProgress) {
            this.contactsSyncInProgress = true;
            MessagesStorage.getInstance(this.currentAccount).getCachedPhoneBook(false);
        }
    }

    final /* synthetic */ void lambda$null$32$ContactsController(HashMap contactsByPhonesDictFinal, HashMap contactsByPhonesShortDictFinal) {
        this.contactsByPhone = contactsByPhonesDictFinal;
        this.contactsByShortPhone = contactsByPhonesShortDictFinal;
    }

    private void reloadContactsStatusesMaybe() {
        try {
            if (MessagesController.getMainSettings(this.currentAccount).getLong("lastReloadStatusTime", 0) < System.currentTimeMillis() - 86400000) {
                reloadContactsStatuses();
            }
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    private void saveContactsLoadTime() {
        try {
            MessagesController.getMainSettings(this.currentAccount).edit().putLong("lastReloadStatusTime", System.currentTimeMillis()).commit();
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    private void mergePhonebookAndTelegramContacts(HashMap<String, ArrayList<Object>> phoneBookSectionsDictFinal, ArrayList<String> phoneBookSectionsArrayFinal, HashMap<String, Contact> phoneBookByShortPhonesFinal) {
        Utilities.globalQueue.postRunnable(new ContactsController$$Lambda$14(this, new ArrayList(this.contacts), phoneBookByShortPhonesFinal, phoneBookSectionsDictFinal, phoneBookSectionsArrayFinal));
    }

    final /* synthetic */ void lambda$mergePhonebookAndTelegramContacts$39$ContactsController(ArrayList contactsCopy, HashMap phoneBookByShortPhonesFinal, HashMap phoneBookSectionsDictFinal, ArrayList phoneBookSectionsArrayFinal) {
        ArrayList<Object> arrayList;
        int size = contactsCopy.size();
        for (int a = 0; a < size; a++) {
            User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(((TL_contact) contactsCopy.get(a)).user_id));
            if (!(user == null || TextUtils.isEmpty(user.phone))) {
                Contact contact = (Contact) phoneBookByShortPhonesFinal.get(user.phone.substring(Math.max(0, user.phone.length() - 7)));
                if (contact == null) {
                    String key = Contact.getLetter(user.first_name, user.last_name);
                    arrayList = (ArrayList) phoneBookSectionsDictFinal.get(key);
                    if (arrayList == null) {
                        arrayList = new ArrayList();
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
            Collections.sort(arrayList2, ContactsController$$Lambda$35.$instance);
        }
        Collections.sort(phoneBookSectionsArrayFinal, ContactsController$$Lambda$36.$instance);
        AndroidUtilities.runOnUIThread(new ContactsController$$Lambda$37(this, phoneBookSectionsArrayFinal, phoneBookSectionsDictFinal));
    }

    static final /* synthetic */ int lambda$null$36$ContactsController(Object o1, Object o2) {
        User user;
        String name1;
        Contact contact;
        String name2;
        if (o1 instanceof User) {
            user = (User) o1;
            name1 = formatName(user.first_name, user.last_name);
        } else if (o1 instanceof Contact) {
            contact = (Contact) o1;
            if (contact.user != null) {
                name1 = formatName(contact.user.first_name, contact.user.last_name);
            } else {
                name1 = formatName(contact.first_name, contact.last_name);
            }
        } else {
            name1 = "";
        }
        if (o2 instanceof User) {
            user = (User) o2;
            name2 = formatName(user.first_name, user.last_name);
        } else if (o2 instanceof Contact) {
            contact = (Contact) o2;
            if (contact.user != null) {
                name2 = formatName(contact.user.first_name, contact.user.last_name);
            } else {
                name2 = formatName(contact.first_name, contact.last_name);
            }
        } else {
            name2 = "";
        }
        return name1.compareTo(name2);
    }

    static final /* synthetic */ int lambda$null$37$ContactsController(String s, String s2) {
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

    final /* synthetic */ void lambda$null$38$ContactsController(ArrayList phoneBookSectionsArrayFinal, HashMap phoneBookSectionsDictFinal) {
        this.phoneBookSectionsArray = phoneBookSectionsArrayFinal;
        this.phoneBookSectionsDict = phoneBookSectionsDictFinal;
    }

    private void updateUnregisteredContacts() {
        int a;
        HashMap<String, TL_contact> contactsPhonesShort = new HashMap();
        int size = this.contacts.size();
        for (a = 0; a < size; a++) {
            TL_contact value = (TL_contact) this.contacts.get(a);
            User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(value.user_id));
            if (!(user == null || TextUtils.isEmpty(user.phone))) {
                contactsPhonesShort.put(user.phone, value);
            }
        }
        ArrayList<Contact> sortedPhoneBookContacts = new ArrayList();
        for (Entry<String, Contact> pair : this.contactsBook.entrySet()) {
            Contact value2 = (Contact) pair.getValue();
            boolean skip = false;
            a = 0;
            while (a < value2.phones.size()) {
                if (contactsPhonesShort.containsKey((String) value2.shortPhones.get(a)) || ((Integer) value2.phoneDeleted.get(a)).intValue() == 1) {
                    skip = true;
                    break;
                }
                a++;
            }
            if (!skip) {
                sortedPhoneBookContacts.add(value2);
            }
        }
        Collections.sort(sortedPhoneBookContacts, ContactsController$$Lambda$15.$instance);
        this.phoneBookContacts = sortedPhoneBookContacts;
    }

    static final /* synthetic */ int lambda$updateUnregisteredContacts$40$ContactsController(Contact contact, Contact contact2) {
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
        if (sort) {
            Collections.sort(this.contacts, new ContactsController$$Lambda$16(this));
        }
        HashMap<String, ArrayList<TL_contact>> sectionsDict = new HashMap();
        ArrayList<String> sortedSectionsArray = new ArrayList();
        for (int a = 0; a < this.contacts.size(); a++) {
            TL_contact value = (TL_contact) this.contacts.get(a);
            User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(value.user_id));
            if (user != null) {
                String key = UserObject.getFirstName(user);
                if (key.length() > 1) {
                    key = key.substring(0, 1);
                }
                if (key.length() == 0) {
                    key = "#";
                } else {
                    key = key.toUpperCase();
                }
                String replace = (String) this.sectionsToReplace.get(key);
                if (replace != null) {
                    key = replace;
                }
                ArrayList<TL_contact> arr = (ArrayList) sectionsDict.get(key);
                if (arr == null) {
                    arr = new ArrayList();
                    sectionsDict.put(key, arr);
                    sortedSectionsArray.add(key);
                }
                arr.add(value);
            }
        }
        Collections.sort(sortedSectionsArray, ContactsController$$Lambda$17.$instance);
        this.usersSectionsDict = sectionsDict;
        this.sortedUsersSectionsArray = sortedSectionsArray;
    }

    final /* synthetic */ int lambda$buildContactsSectionsArrays$41$ContactsController(TL_contact tl_contact, TL_contact tl_contact2) {
        return UserObject.getFirstName(MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(tl_contact.user_id))).compareTo(UserObject.getFirstName(MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(tl_contact2.user_id))));
    }

    static final /* synthetic */ int lambda$buildContactsSectionsArrays$42$ContactsController(String s, String s2) {
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
        if (VERSION.SDK_INT < 23) {
            Cursor cursor = null;
            try {
                cursor = ApplicationLoader.applicationContext.getContentResolver().query(Phone.CONTENT_URI, this.projectionPhones, null, null, null);
                if (cursor == null || cursor.getCount() == 0) {
                    if (cursor != null) {
                        try {
                            cursor.close();
                        } catch (Throwable e) {
                            FileLog.e(e);
                        }
                    }
                    return false;
                }
                if (cursor != null) {
                    try {
                        cursor.close();
                    } catch (Throwable e2) {
                        FileLog.e(e2);
                    }
                }
                return true;
            } catch (Throwable th) {
                if (cursor != null) {
                    try {
                        cursor.close();
                    } catch (Throwable e22) {
                        FileLog.e(e22);
                    }
                }
            }
        } else if (ApplicationLoader.applicationContext.checkSelfPermission("android.permission.READ_CONTACTS") == 0) {
            return true;
        } else {
            return false;
        }
    }

    /* JADX WARNING: Failed to extract finally block: empty outs */
    private void performWriteContactsToPhoneBookInternal(java.util.ArrayList<org.telegram.tgnet.TLRPC.TL_contact> r13) {
        /*
        r12 = this;
        r8 = 0;
        r0 = r12.hasContactsPermission();	 Catch:{ Exception -> 0x0064 }
        if (r0 != 0) goto L_0x000d;
    L_0x0007:
        if (r8 == 0) goto L_0x000c;
    L_0x0009:
        r8.close();
    L_0x000c:
        return;
    L_0x000d:
        r0 = android.provider.ContactsContract.RawContacts.CONTENT_URI;	 Catch:{ Exception -> 0x0064 }
        r0 = r0.buildUpon();	 Catch:{ Exception -> 0x0064 }
        r2 = "account_name";
        r3 = r12.systemAccount;	 Catch:{ Exception -> 0x0064 }
        r3 = r3.name;	 Catch:{ Exception -> 0x0064 }
        r0 = r0.appendQueryParameter(r2, r3);	 Catch:{ Exception -> 0x0064 }
        r2 = "account_type";
        r3 = r12.systemAccount;	 Catch:{ Exception -> 0x0064 }
        r3 = r3.type;	 Catch:{ Exception -> 0x0064 }
        r0 = r0.appendQueryParameter(r2, r3);	 Catch:{ Exception -> 0x0064 }
        r1 = r0.build();	 Catch:{ Exception -> 0x0064 }
        r0 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0064 }
        r0 = r0.getContentResolver();	 Catch:{ Exception -> 0x0064 }
        r2 = 2;
        r2 = new java.lang.String[r2];	 Catch:{ Exception -> 0x0064 }
        r3 = 0;
        r4 = "_id";
        r2[r3] = r4;	 Catch:{ Exception -> 0x0064 }
        r3 = 1;
        r4 = "sync2";
        r2[r3] = r4;	 Catch:{ Exception -> 0x0064 }
        r3 = 0;
        r4 = 0;
        r5 = 0;
        r8 = r0.query(r1, r2, r3, r4, r5);	 Catch:{ Exception -> 0x0064 }
        r7 = new org.telegram.messenger.support.SparseLongArray;	 Catch:{ Exception -> 0x0064 }
        r7.<init>();	 Catch:{ Exception -> 0x0064 }
        if (r8 == 0) goto L_0x009e;
    L_0x0050:
        r0 = r8.moveToNext();	 Catch:{ Exception -> 0x0064 }
        if (r0 == 0) goto L_0x006e;
    L_0x0056:
        r0 = 1;
        r0 = r8.getInt(r0);	 Catch:{ Exception -> 0x0064 }
        r2 = 0;
        r2 = r8.getLong(r2);	 Catch:{ Exception -> 0x0064 }
        r7.put(r0, r2);	 Catch:{ Exception -> 0x0064 }
        goto L_0x0050;
    L_0x0064:
        r9 = move-exception;
        org.telegram.messenger.FileLog.e(r9);	 Catch:{ all -> 0x00a5 }
        if (r8 == 0) goto L_0x000c;
    L_0x006a:
        r8.close();
        goto L_0x000c;
    L_0x006e:
        r8.close();	 Catch:{ Exception -> 0x0064 }
        r8 = 0;
        r6 = 0;
    L_0x0073:
        r0 = r13.size();	 Catch:{ Exception -> 0x0064 }
        if (r6 >= r0) goto L_0x009e;
    L_0x0079:
        r10 = r13.get(r6);	 Catch:{ Exception -> 0x0064 }
        r10 = (org.telegram.tgnet.TLRPC.TL_contact) r10;	 Catch:{ Exception -> 0x0064 }
        r0 = r10.user_id;	 Catch:{ Exception -> 0x0064 }
        r0 = r7.indexOfKey(r0);	 Catch:{ Exception -> 0x0064 }
        if (r0 >= 0) goto L_0x009b;
    L_0x0087:
        r0 = r12.currentAccount;	 Catch:{ Exception -> 0x0064 }
        r0 = org.telegram.messenger.MessagesController.getInstance(r0);	 Catch:{ Exception -> 0x0064 }
        r2 = r10.user_id;	 Catch:{ Exception -> 0x0064 }
        r2 = java.lang.Integer.valueOf(r2);	 Catch:{ Exception -> 0x0064 }
        r11 = r0.getUser(r2);	 Catch:{ Exception -> 0x0064 }
        r0 = 0;
        r12.addContactToPhoneBook(r11, r0);	 Catch:{ Exception -> 0x0064 }
    L_0x009b:
        r6 = r6 + 1;
        goto L_0x0073;
    L_0x009e:
        if (r8 == 0) goto L_0x000c;
    L_0x00a0:
        r8.close();
        goto L_0x000c;
    L_0x00a5:
        r0 = move-exception;
        if (r8 == 0) goto L_0x00ab;
    L_0x00a8:
        r8.close();
    L_0x00ab:
        throw r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ContactsController.performWriteContactsToPhoneBookInternal(java.util.ArrayList):void");
    }

    private void performWriteContactsToPhoneBook() {
        Utilities.phoneBookQueue.postRunnable(new ContactsController$$Lambda$18(this, new ArrayList(this.contacts)));
    }

    private void applyContactsUpdates(ArrayList<Integer> ids, ConcurrentHashMap<Integer, User> userDict, ArrayList<TL_contact> newC, ArrayList<Integer> contactsTD) {
        int a;
        Integer uid;
        User user;
        Contact contact;
        int index;
        if (newC == null || contactsTD == null) {
            newC = new ArrayList();
            contactsTD = new ArrayList();
            for (a = 0; a < ids.size(); a++) {
                uid = (Integer) ids.get(a);
                if (uid.intValue() > 0) {
                    TL_contact contact2 = new TL_contact();
                    contact2.user_id = uid.intValue();
                    newC.add(contact2);
                } else if (uid.intValue() < 0) {
                    contactsTD.add(Integer.valueOf(-uid.intValue()));
                }
            }
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("process update - contacts add = " + newC.size() + " delete = " + contactsTD.size());
        }
        StringBuilder toAdd = new StringBuilder();
        StringBuilder toDelete = new StringBuilder();
        boolean reloadContacts = false;
        for (a = 0; a < newC.size(); a++) {
            TL_contact newContact = (TL_contact) newC.get(a);
            user = null;
            if (userDict != null) {
                user = (User) userDict.get(Integer.valueOf(newContact.user_id));
            }
            if (user == null) {
                user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(newContact.user_id));
            } else {
                MessagesController.getInstance(this.currentAccount).putUser(user, true);
            }
            if (user == null || TextUtils.isEmpty(user.phone)) {
                reloadContacts = true;
            } else {
                contact = (Contact) this.contactsBookSPhones.get(user.phone);
                if (contact != null) {
                    index = contact.shortPhones.indexOf(user.phone);
                    if (index != -1) {
                        contact.phoneDeleted.set(index, Integer.valueOf(0));
                    }
                }
                if (toAdd.length() != 0) {
                    toAdd.append(",");
                }
                toAdd.append(user.phone);
            }
        }
        for (a = 0; a < contactsTD.size(); a++) {
            uid = (Integer) contactsTD.get(a);
            Utilities.phoneBookQueue.postRunnable(new ContactsController$$Lambda$19(this, uid));
            user = null;
            if (userDict != null) {
                user = (User) userDict.get(uid);
            }
            if (user == null) {
                user = MessagesController.getInstance(this.currentAccount).getUser(uid);
            } else {
                MessagesController.getInstance(this.currentAccount).putUser(user, true);
            }
            if (user == null) {
                reloadContacts = true;
            } else if (!TextUtils.isEmpty(user.phone)) {
                contact = (Contact) this.contactsBookSPhones.get(user.phone);
                if (contact != null) {
                    index = contact.shortPhones.indexOf(user.phone);
                    if (index != -1) {
                        contact.phoneDeleted.set(index, Integer.valueOf(1));
                    }
                }
                if (toDelete.length() != 0) {
                    toDelete.append(",");
                }
                toDelete.append(user.phone);
            }
        }
        if (!(toAdd.length() == 0 && toDelete.length() == 0)) {
            MessagesStorage.getInstance(this.currentAccount).applyPhoneBookUpdates(toAdd.toString(), toDelete.toString());
        }
        if (reloadContacts) {
            Utilities.stageQueue.postRunnable(new ContactsController$$Lambda$20(this));
        } else {
            AndroidUtilities.runOnUIThread(new ContactsController$$Lambda$21(this, newC, contactsTD));
        }
    }

    final /* synthetic */ void lambda$applyContactsUpdates$44$ContactsController(Integer uid) {
        deleteContactFromPhoneBook(uid.intValue());
    }

    final /* synthetic */ void lambda$applyContactsUpdates$45$ContactsController() {
        loadContacts(false, 0);
    }

    final /* synthetic */ void lambda$applyContactsUpdates$46$ContactsController(ArrayList newContacts, ArrayList contactsToDelete) {
        int a;
        TL_contact contact;
        boolean z = true;
        for (a = 0; a < newContacts.size(); a++) {
            contact = (TL_contact) newContacts.get(a);
            if (this.contactsDict.get(Integer.valueOf(contact.user_id)) == null) {
                this.contacts.add(contact);
                this.contactsDict.put(Integer.valueOf(contact.user_id), contact);
            }
        }
        for (a = 0; a < contactsToDelete.size(); a++) {
            Integer uid = (Integer) contactsToDelete.get(a);
            contact = (TL_contact) this.contactsDict.get(uid);
            if (contact != null) {
                this.contacts.remove(contact);
                this.contactsDict.remove(uid);
            }
        }
        if (!newContacts.isEmpty()) {
            updateUnregisteredContacts();
            performWriteContactsToPhoneBook();
        }
        performSyncPhoneBook(getContactsCopy(this.contactsBook), false, false, false, false, true, false);
        if (newContacts.isEmpty()) {
            z = false;
        }
        buildContactsSectionsArrays(z);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.contactsDidLoad, new Object[0]);
    }

    public void processContactsUpdates(ArrayList<Integer> ids, ConcurrentHashMap<Integer, User> userDict) {
        ArrayList<TL_contact> newContacts = new ArrayList();
        ArrayList<Integer> contactsToDelete = new ArrayList();
        Iterator it = ids.iterator();
        while (it.hasNext()) {
            Integer uid = (Integer) it.next();
            int idx;
            if (uid.intValue() > 0) {
                TL_contact contact = new TL_contact();
                contact.user_id = uid.intValue();
                newContacts.add(contact);
                if (!this.delayedContactsUpdate.isEmpty()) {
                    idx = this.delayedContactsUpdate.indexOf(Integer.valueOf(-uid.intValue()));
                    if (idx != -1) {
                        this.delayedContactsUpdate.remove(idx);
                    }
                }
            } else if (uid.intValue() < 0) {
                contactsToDelete.add(Integer.valueOf(-uid.intValue()));
                if (!this.delayedContactsUpdate.isEmpty()) {
                    idx = this.delayedContactsUpdate.indexOf(Integer.valueOf(-uid.intValue()));
                    if (idx != -1) {
                        this.delayedContactsUpdate.remove(idx);
                    }
                }
            }
        }
        if (!contactsToDelete.isEmpty()) {
            MessagesStorage.getInstance(this.currentAccount).deleteContacts(contactsToDelete);
        }
        if (!newContacts.isEmpty()) {
            MessagesStorage.getInstance(this.currentAccount).putContacts(newContacts, false);
        }
        if (this.contactsLoaded && this.contactsBookLoaded) {
            applyContactsUpdates(ids, userDict, newContacts, contactsToDelete);
            return;
        }
        this.delayedContactsUpdate.addAll(ids);
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("delay update - contacts add = " + newContacts.size() + " delete = " + contactsToDelete.size());
        }
    }

    public long addContactToPhoneBook(User user, boolean check) {
        long res = -1;
        if (!(this.systemAccount == null || user == null || TextUtils.isEmpty(user.phone) || !hasContactsPermission())) {
            res = -1;
            synchronized (this.observerLock) {
                this.ignoreChanges = true;
            }
            ContentResolver contentResolver = ApplicationLoader.applicationContext.getContentResolver();
            if (check) {
                try {
                    contentResolver.delete(RawContacts.CONTENT_URI.buildUpon().appendQueryParameter("caller_is_syncadapter", "true").appendQueryParameter("account_name", this.systemAccount.name).appendQueryParameter("account_type", this.systemAccount.type).build(), "sync2 = " + user.id, null);
                } catch (Throwable e) {
                    FileLog.e(e);
                }
            }
            ArrayList<ContentProviderOperation> query = new ArrayList();
            Builder builder = ContentProviderOperation.newInsert(RawContacts.CONTENT_URI);
            builder.withValue("account_name", this.systemAccount.name);
            builder.withValue("account_type", this.systemAccount.type);
            builder.withValue("sync1", user.phone);
            builder.withValue("sync2", Integer.valueOf(user.id));
            query.add(builder.build());
            builder = ContentProviderOperation.newInsert(Data.CONTENT_URI);
            builder.withValueBackReference("raw_contact_id", 0);
            builder.withValue("mimetype", "vnd.android.cursor.item/name");
            builder.withValue("data2", user.first_name);
            builder.withValue("data3", user.last_name);
            query.add(builder.build());
            builder = ContentProviderOperation.newInsert(Data.CONTENT_URI);
            builder.withValueBackReference("raw_contact_id", 0);
            builder.withValue("mimetype", "vnd.android.cursor.item/vnd.org.telegram.messenger.android.profile");
            builder.withValue("data1", Integer.valueOf(user.id));
            builder.withValue("data2", "Telegram Profile");
            builder.withValue("data3", "+" + user.phone);
            builder.withValue("data4", Integer.valueOf(user.id));
            query.add(builder.build());
            try {
                ContentProviderResult[] result = contentResolver.applyBatch("com.android.contacts", query);
                if (!(result == null || result.length <= 0 || result[0].uri == null)) {
                    res = Long.parseLong(result[0].uri.getLastPathSegment());
                }
            } catch (Throwable e2) {
                FileLog.e(e2);
            }
            synchronized (this.observerLock) {
                this.ignoreChanges = false;
            }
        }
        return res;
    }

    private void deleteContactFromPhoneBook(int uid) {
        if (hasContactsPermission()) {
            synchronized (this.observerLock) {
                this.ignoreChanges = true;
            }
            try {
                ApplicationLoader.applicationContext.getContentResolver().delete(RawContacts.CONTENT_URI.buildUpon().appendQueryParameter("caller_is_syncadapter", "true").appendQueryParameter("account_name", this.systemAccount.name).appendQueryParameter("account_type", this.systemAccount.type).build(), "sync2 = " + uid, null);
            } catch (Throwable e) {
                FileLog.e(e);
            }
            synchronized (this.observerLock) {
                this.ignoreChanges = false;
            }
        }
    }

    protected void markAsContacted(String contactId) {
        if (contactId != null) {
            Utilities.phoneBookQueue.postRunnable(new ContactsController$$Lambda$22(contactId));
        }
    }

    static final /* synthetic */ void lambda$markAsContacted$47$ContactsController(String contactId) {
        Uri uri = Uri.parse(contactId);
        ContentValues values = new ContentValues();
        values.put("last_time_contacted", Long.valueOf(System.currentTimeMillis()));
        ApplicationLoader.applicationContext.getContentResolver().update(uri, values, null, null);
    }

    public void addContact(User user) {
        if (user != null && !TextUtils.isEmpty(user.phone)) {
            TL_contacts_importContacts req = new TL_contacts_importContacts();
            ArrayList<TL_inputPhoneContact> contactsParams = new ArrayList();
            TL_inputPhoneContact c = new TL_inputPhoneContact();
            c.phone = user.phone;
            if (!c.phone.startsWith("+")) {
                c.phone = "+" + c.phone;
            }
            c.first_name = user.first_name;
            c.last_name = user.last_name;
            c.client_id = 0;
            contactsParams.add(c);
            req.contacts = contactsParams;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new ContactsController$$Lambda$23(this), 6);
        }
    }

    final /* synthetic */ void lambda$addContact$50$ContactsController(TLObject response, TL_error error) {
        if (error == null) {
            TL_contacts_importedContacts res = (TL_contacts_importedContacts) response;
            MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(res.users, null, true, true);
            for (int a = 0; a < res.users.size(); a++) {
                User u = (User) res.users.get(a);
                Utilities.phoneBookQueue.postRunnable(new ContactsController$$Lambda$33(this, u));
                TL_contact newContact = new TL_contact();
                newContact.user_id = u.id;
                ArrayList<TL_contact> arrayList = new ArrayList();
                arrayList.add(newContact);
                MessagesStorage.getInstance(this.currentAccount).putContacts(arrayList, false);
                if (!TextUtils.isEmpty(u.phone)) {
                    CharSequence name = formatName(u.first_name, u.last_name);
                    MessagesStorage.getInstance(this.currentAccount).applyPhoneBookUpdates(u.phone, "");
                    Contact contact = (Contact) this.contactsBookSPhones.get(u.phone);
                    if (contact != null) {
                        int index = contact.shortPhones.indexOf(u.phone);
                        if (index != -1) {
                            contact.phoneDeleted.set(index, Integer.valueOf(0));
                        }
                    }
                }
            }
            AndroidUtilities.runOnUIThread(new ContactsController$$Lambda$34(this, res));
        }
    }

    final /* synthetic */ void lambda$null$48$ContactsController(User u) {
        addContactToPhoneBook(u, true);
    }

    final /* synthetic */ void lambda$null$49$ContactsController(TL_contacts_importedContacts res) {
        Iterator it = res.users.iterator();
        while (it.hasNext()) {
            User u = (User) it.next();
            MessagesController.getInstance(this.currentAccount).putUser(u, false);
            if (this.contactsDict.get(Integer.valueOf(u.id)) == null) {
                TL_contact newContact = new TL_contact();
                newContact.user_id = u.id;
                this.contacts.add(newContact);
                this.contactsDict.put(Integer.valueOf(newContact.user_id), newContact);
            }
        }
        buildContactsSectionsArrays(true);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.contactsDidLoad, new Object[0]);
    }

    public void deleteContact(ArrayList<User> users) {
        if (users != null && !users.isEmpty()) {
            TL_contacts_deleteContacts req = new TL_contacts_deleteContacts();
            ArrayList<Integer> uids = new ArrayList();
            Iterator it = users.iterator();
            while (it.hasNext()) {
                User user = (User) it.next();
                InputUser inputUser = MessagesController.getInstance(this.currentAccount).getInputUser(user);
                if (inputUser != null) {
                    uids.add(Integer.valueOf(user.id));
                    req.id.add(inputUser);
                }
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new ContactsController$$Lambda$24(this, uids, users));
        }
    }

    final /* synthetic */ void lambda$deleteContact$53$ContactsController(ArrayList uids, ArrayList users, TLObject response, TL_error error) {
        if (error == null) {
            MessagesStorage.getInstance(this.currentAccount).deleteContacts(uids);
            Utilities.phoneBookQueue.postRunnable(new ContactsController$$Lambda$31(this, users));
            for (int a = 0; a < users.size(); a++) {
                User user = (User) users.get(a);
                if (!TextUtils.isEmpty(user.phone)) {
                    CharSequence name = UserObject.getUserName(user);
                    MessagesStorage.getInstance(this.currentAccount).applyPhoneBookUpdates(user.phone, "");
                    Contact contact = (Contact) this.contactsBookSPhones.get(user.phone);
                    if (contact != null) {
                        int index = contact.shortPhones.indexOf(user.phone);
                        if (index != -1) {
                            contact.phoneDeleted.set(index, Integer.valueOf(1));
                        }
                    }
                }
            }
            AndroidUtilities.runOnUIThread(new ContactsController$$Lambda$32(this, users));
        }
    }

    final /* synthetic */ void lambda$null$51$ContactsController(ArrayList users) {
        Iterator it = users.iterator();
        while (it.hasNext()) {
            deleteContactFromPhoneBook(((User) it.next()).id);
        }
    }

    final /* synthetic */ void lambda$null$52$ContactsController(ArrayList users) {
        boolean remove = false;
        Iterator it = users.iterator();
        while (it.hasNext()) {
            User user = (User) it.next();
            TL_contact contact = (TL_contact) this.contactsDict.get(Integer.valueOf(user.id));
            if (contact != null) {
                remove = true;
                this.contacts.remove(contact);
                this.contactsDict.remove(Integer.valueOf(user.id));
            }
        }
        if (remove) {
            buildContactsSectionsArrays(false);
        }
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(1));
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.contactsDidLoad, new Object[0]);
    }

    public void reloadContactsStatuses() {
        saveContactsLoadTime();
        MessagesController.getInstance(this.currentAccount).clearFullUsers();
        Editor editor = MessagesController.getMainSettings(this.currentAccount).edit();
        editor.putBoolean("needGetStatuses", true).commit();
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_contacts_getStatuses(), new ContactsController$$Lambda$25(this, editor));
    }

    final /* synthetic */ void lambda$reloadContactsStatuses$55$ContactsController(Editor editor, TLObject response, TL_error error) {
        if (error == null) {
            AndroidUtilities.runOnUIThread(new ContactsController$$Lambda$30(this, editor, response));
        }
    }

    final /* synthetic */ void lambda$null$54$ContactsController(Editor editor, TLObject response) {
        editor.remove("needGetStatuses").commit();
        Vector vector = (Vector) response;
        if (!vector.objects.isEmpty()) {
            ArrayList<User> dbUsersStatus = new ArrayList();
            Iterator it = vector.objects.iterator();
            while (it.hasNext()) {
                TL_contactStatus object = it.next();
                User toDbUser = new TL_user();
                TL_contactStatus status = object;
                if (status != null) {
                    if (status.status instanceof TL_userStatusRecently) {
                        status.status.expires = -100;
                    } else if (status.status instanceof TL_userStatusLastWeek) {
                        status.status.expires = -101;
                    } else if (status.status instanceof TL_userStatusLastMonth) {
                        status.status.expires = -102;
                    }
                    User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(status.user_id));
                    if (user != null) {
                        user.status = status.status;
                    }
                    toDbUser.status = status.status;
                    dbUsersStatus.add(toDbUser);
                }
            }
            MessagesStorage.getInstance(this.currentAccount).updateUsers(dbUsersStatus, true, true, true);
        }
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(4));
    }

    public void loadPrivacySettings() {
        if (this.loadingDeleteInfo == 0) {
            this.loadingDeleteInfo = 1;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_account_getAccountTTL(), new ContactsController$$Lambda$26(this));
        }
        for (int a = 0; a < this.loadingPrivacyInfo.length; a++) {
            if (this.loadingPrivacyInfo[a] == 0) {
                this.loadingPrivacyInfo[a] = 1;
                int num = a;
                TL_account_getPrivacy req = new TL_account_getPrivacy();
                switch (num) {
                    case 0:
                        req.key = new TL_inputPrivacyKeyStatusTimestamp();
                        break;
                    case 1:
                        req.key = new TL_inputPrivacyKeyChatInvite();
                        break;
                    case 2:
                        req.key = new TL_inputPrivacyKeyPhoneCall();
                        break;
                    default:
                        req.key = new TL_inputPrivacyKeyPhoneP2P();
                        break;
                }
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new ContactsController$$Lambda$27(this, num));
            }
        }
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.privacyRulesUpdated, new Object[0]);
    }

    final /* synthetic */ void lambda$loadPrivacySettings$57$ContactsController(TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new ContactsController$$Lambda$29(this, error, response));
    }

    final /* synthetic */ void lambda$null$56$ContactsController(TL_error error, TLObject response) {
        if (error == null) {
            this.deleteAccountTTL = ((TL_accountDaysTTL) response).days;
            this.loadingDeleteInfo = 2;
        } else {
            this.loadingDeleteInfo = 0;
        }
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.privacyRulesUpdated, new Object[0]);
    }

    final /* synthetic */ void lambda$loadPrivacySettings$59$ContactsController(int num, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new ContactsController$$Lambda$28(this, error, response, num));
    }

    final /* synthetic */ void lambda$null$58$ContactsController(TL_error error, TLObject response, int num) {
        if (error == null) {
            TL_account_privacyRules rules = (TL_account_privacyRules) response;
            MessagesController.getInstance(this.currentAccount).putUsers(rules.users, false);
            switch (num) {
                case 0:
                    this.privacyRules = rules.rules;
                    break;
                case 1:
                    this.groupPrivacyRules = rules.rules;
                    break;
                case 2:
                    this.callPrivacyRules = rules.rules;
                    break;
                default:
                    this.p2pPrivacyRules = rules.rules;
                    break;
            }
            this.loadingPrivacyInfo[num] = 2;
        } else {
            this.loadingPrivacyInfo[num] = 0;
        }
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.privacyRulesUpdated, new Object[0]);
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

    public boolean getLoadingPrivicyInfo(int type) {
        return this.loadingPrivacyInfo[type] != 2;
    }

    public ArrayList<PrivacyRule> getPrivacyRules(int type) {
        if (type == 3) {
            return this.p2pPrivacyRules;
        }
        if (type == 2) {
            return this.callPrivacyRules;
        }
        if (type == 1) {
            return this.groupPrivacyRules;
        }
        return this.privacyRules;
    }

    public void setPrivacyRules(ArrayList<PrivacyRule> rules, int type) {
        if (type == 3) {
            this.p2pPrivacyRules = rules;
        } else if (type == 2) {
            this.callPrivacyRules = rules;
        } else if (type == 1) {
            this.groupPrivacyRules = rules;
        } else {
            this.privacyRules = rules;
        }
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.privacyRulesUpdated, new Object[0]);
        reloadContactsStatuses();
    }

    public void createOrUpdateConnectionServiceContact(int id, String firstName, String lastName) {
        if (hasContactsPermission()) {
            try {
                int groupID;
                ContentResolver resolver = ApplicationLoader.applicationContext.getContentResolver();
                ArrayList<ContentProviderOperation> ops = new ArrayList();
                Uri groupsURI = Groups.CONTENT_URI.buildUpon().appendQueryParameter("caller_is_syncadapter", "true").build();
                Uri rawContactsURI = RawContacts.CONTENT_URI.buildUpon().appendQueryParameter("caller_is_syncadapter", "true").build();
                Cursor cursor = resolver.query(groupsURI, new String[]{"_id"}, "title=? AND account_type=? AND account_name=?", new String[]{"TelegramConnectionService", this.systemAccount.type, this.systemAccount.name}, null);
                if (cursor == null || !cursor.moveToFirst()) {
                    ContentValues values = new ContentValues();
                    values.put("account_type", this.systemAccount.type);
                    values.put("account_name", this.systemAccount.name);
                    values.put("group_visible", Integer.valueOf(0));
                    values.put("group_is_read_only", Integer.valueOf(1));
                    values.put("title", "TelegramConnectionService");
                    groupID = Integer.parseInt(resolver.insert(groupsURI, values).getLastPathSegment());
                } else {
                    groupID = cursor.getInt(0);
                }
                if (cursor != null) {
                    cursor.close();
                }
                ContentResolver contentResolver = resolver;
                cursor = contentResolver.query(Data.CONTENT_URI, new String[]{"raw_contact_id"}, "mimetype=? AND data1=?", new String[]{"vnd.android.cursor.item/group_membership", groupID + ""}, null);
                int backRef = ops.size();
                if (cursor == null || !cursor.moveToFirst()) {
                    ops.add(ContentProviderOperation.newInsert(rawContactsURI).withValue("account_type", this.systemAccount.type).withValue("account_name", this.systemAccount.name).withValue("raw_contact_is_read_only", Integer.valueOf(1)).withValue("aggregation_mode", Integer.valueOf(3)).build());
                    ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI).withValueBackReference("raw_contact_id", backRef).withValue("mimetype", "vnd.android.cursor.item/name").withValue("data2", firstName).withValue("data3", lastName).build());
                    ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI).withValueBackReference("raw_contact_id", backRef).withValue("mimetype", "vnd.android.cursor.item/phone_v2").withValue("data1", "+99084" + id).build());
                    ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI).withValueBackReference("raw_contact_id", backRef).withValue("mimetype", "vnd.android.cursor.item/group_membership").withValue("data1", Integer.valueOf(groupID)).build());
                } else {
                    int contactID = cursor.getInt(0);
                    ops.add(ContentProviderOperation.newUpdate(rawContactsURI).withSelection("_id=?", new String[]{contactID + ""}).withValue("deleted", Integer.valueOf(0)).build());
                    ops.add(ContentProviderOperation.newUpdate(Data.CONTENT_URI).withSelection("raw_contact_id=? AND mimetype=?", new String[]{contactID + "", "vnd.android.cursor.item/phone_v2"}).withValue("data1", "+99084" + id).build());
                    ops.add(ContentProviderOperation.newUpdate(Data.CONTENT_URI).withSelection("raw_contact_id=? AND mimetype=?", new String[]{contactID + "", "vnd.android.cursor.item/name"}).withValue("data2", firstName).withValue("data3", lastName).build());
                }
                if (cursor != null) {
                    cursor.close();
                }
                resolver.applyBatch("com.android.contacts", ops);
            } catch (Throwable x) {
                FileLog.e(x);
            }
        }
    }

    public void deleteConnectionServiceContact() {
        if (hasContactsPermission()) {
            try {
                ContentResolver resolver = ApplicationLoader.applicationContext.getContentResolver();
                Cursor cursor = resolver.query(Groups.CONTENT_URI, new String[]{"_id"}, "title=? AND account_type=? AND account_name=?", new String[]{"TelegramConnectionService", this.systemAccount.type, this.systemAccount.name}, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int groupID = cursor.getInt(0);
                    cursor.close();
                    cursor = resolver.query(Data.CONTENT_URI, new String[]{"raw_contact_id"}, "mimetype=? AND data1=?", new String[]{"vnd.android.cursor.item/group_membership", groupID + ""}, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        int contactID = cursor.getInt(0);
                        cursor.close();
                        resolver.delete(RawContacts.CONTENT_URI, "_id=?", new String[]{contactID + ""});
                    } else if (cursor != null) {
                        cursor.close();
                    }
                } else if (cursor != null) {
                    cursor.close();
                }
            } catch (Throwable x) {
                FileLog.e(x);
            }
        }
    }

    public static String formatName(String firstName, String lastName) {
        int length;
        int i = 0;
        if (firstName != null) {
            firstName = firstName.trim();
        }
        if (lastName != null) {
            lastName = lastName.trim();
        }
        if (firstName != null) {
            length = firstName.length();
        } else {
            length = 0;
        }
        if (lastName != null) {
            i = lastName.length();
        }
        StringBuilder result = new StringBuilder((i + length) + 1);
        if (LocaleController.nameDisplayOrder == 1) {
            if (firstName != null && firstName.length() > 0) {
                result.append(firstName);
                if (lastName != null && lastName.length() > 0) {
                    result.append(" ");
                    result.append(lastName);
                }
            } else if (lastName != null && lastName.length() > 0) {
                result.append(lastName);
            }
        } else if (lastName != null && lastName.length() > 0) {
            result.append(lastName);
            if (firstName != null && firstName.length() > 0) {
                result.append(" ");
                result.append(firstName);
            }
        } else if (firstName != null && firstName.length() > 0) {
            result.append(firstName);
        }
        return result.toString();
    }
}
