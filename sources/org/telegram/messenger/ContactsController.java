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
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Build.VERSION;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.text.TextUtils;
import android.util.SparseArray;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.support.SparseLongArray;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.InputUser;
import org.telegram.tgnet.TLRPC.PrivacyRule;
import org.telegram.tgnet.TLRPC.TL_account_getAccountTTL;
import org.telegram.tgnet.TLRPC.TL_account_getPrivacy;
import org.telegram.tgnet.TLRPC.TL_account_privacyRules;
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
    private String lastContactsVersions = TtmlNode.ANONYMOUS_REGION_ID;
    private final Object loadContactsSync = new Object();
    private int loadingCallsInfo;
    private boolean loadingContacts;
    private int loadingDeleteInfo;
    private int loadingGroupInfo;
    private int loadingLastSeenInfo;
    private boolean migratingContacts;
    private final Object observerLock = new Object();
    public ArrayList<Contact> phoneBookContacts = new ArrayList();
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

    /* renamed from: org.telegram.messenger.ContactsController$1 */
    class C00771 implements Runnable {
        C00771() {
        }

        public void run() {
            ContactsController.this.migratingContacts = false;
            ContactsController.this.completedRequestsCount = 0;
        }
    }

    /* renamed from: org.telegram.messenger.ContactsController$3 */
    class C00883 implements Runnable {
        C00883() {
        }

        public void run() {
            if (ContactsController.this.checkContactsInternal()) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.m0d("detected contacts change");
                }
                ContactsController.this.performSyncPhoneBook(ContactsController.this.getContactsCopy(ContactsController.this.contactsBook), true, false, true, false, true, false);
            }
        }
    }

    /* renamed from: org.telegram.messenger.ContactsController$4 */
    class C00894 implements Runnable {
        C00894() {
        }

        public void run() {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.m0d("force import contacts");
            }
            ContactsController.this.performSyncPhoneBook(new HashMap(), true, true, true, true, false, false);
        }
    }

    /* renamed from: org.telegram.messenger.ContactsController$7 */
    class C00917 implements Runnable {
        C00917() {
        }

        public void run() {
            if (ContactsController.this.contacts.isEmpty()) {
                if (!ContactsController.this.contactsLoaded) {
                    ContactsController.this.loadContacts(true, 0);
                    return;
                }
            }
            synchronized (ContactsController.this.loadContactsSync) {
                ContactsController.this.loadingContacts = false;
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
        public ArrayList<Integer> phoneDeleted = new ArrayList(4);
        public ArrayList<String> phoneTypes = new ArrayList(4);
        public ArrayList<String> phones = new ArrayList(4);
        public String provider;
        public ArrayList<String> shortPhones = new ArrayList(4);
    }

    /* renamed from: org.telegram.messenger.ContactsController$2 */
    class C17832 implements RequestDelegate {
        C17832() {
        }

        public void run(TLObject response, TL_error error) {
            if (response != null) {
                final TL_help_inviteText res = (TL_help_inviteText) response;
                if (res.message.length() != 0) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            ContactsController.this.updatingInviteLink = false;
                            Editor editor = MessagesController.getMainSettings(ContactsController.this.currentAccount).edit();
                            editor.putString("invitelink", ContactsController.this.inviteLink = res.message);
                            editor.putInt("invitelinktime", (int) (System.currentTimeMillis() / 1000));
                            editor.commit();
                        }
                    });
                }
            }
        }
    }

    /* renamed from: org.telegram.messenger.ContactsController$6 */
    class C17846 implements RequestDelegate {
        C17846() {
        }

        public void run(TLObject response, TL_error error) {
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
        this.currentAccount = instance;
        if (MessagesController.getMainSettings(this.currentAccount).getBoolean("needGetStatuses", false)) {
            reloadContactsStatuses();
        }
        this.sectionsToReplace.put("\u00c0", "A");
        this.sectionsToReplace.put("\u00c1", "A");
        this.sectionsToReplace.put("\u00c4", "A");
        this.sectionsToReplace.put("\u00d9", "U");
        this.sectionsToReplace.put("\u00da", "U");
        this.sectionsToReplace.put("\u00dc", "U");
        this.sectionsToReplace.put("\u00cc", "I");
        this.sectionsToReplace.put("\u00cd", "I");
        this.sectionsToReplace.put("\u00cf", "I");
        this.sectionsToReplace.put("\u00c8", "E");
        this.sectionsToReplace.put("\u00c9", "E");
        this.sectionsToReplace.put("\u00ca", "E");
        this.sectionsToReplace.put("\u00cb", "E");
        this.sectionsToReplace.put("\u00d2", "O");
        this.sectionsToReplace.put("\u00d3", "O");
        this.sectionsToReplace.put("\u00d6", "O");
        this.sectionsToReplace.put("\u00c7", "C");
        this.sectionsToReplace.put("\u00d1", "N");
        this.sectionsToReplace.put("\u0178", "Y");
        this.sectionsToReplace.put("\u00dd", "Y");
        this.sectionsToReplace.put("\u0162", "Y");
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
        this.loadingContacts = false;
        this.contactsSyncInProgress = false;
        this.contactsLoaded = false;
        this.contactsBookLoaded = false;
        this.lastContactsVersions = TtmlNode.ANONYMOUS_REGION_ID;
        this.loadingDeleteInfo = 0;
        this.deleteAccountTTL = 0;
        this.loadingLastSeenInfo = 0;
        this.loadingGroupInfo = 0;
        this.loadingCallsInfo = 0;
        Utilities.globalQueue.postRunnable(new C00771());
        this.privacyRules = null;
    }

    public void checkInviteText() {
        SharedPreferences preferences = MessagesController.getMainSettings(this.currentAccount);
        this.inviteLink = preferences.getString("invitelink", null);
        int time = preferences.getInt("invitelinktime", 0);
        if (!this.updatingInviteLink) {
            if (this.inviteLink == null || Math.abs((System.currentTimeMillis() / 1000) - ((long) time)) >= 86400) {
                this.updatingInviteLink = true;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_help_getInviteText(), new C17832(), 2);
            }
        }
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

    public void checkAppAccount() {
        AccountManager am = AccountManager.get(ApplicationLoader.applicationContext);
        try {
            Account[] accounts = am.getAccountsByType("org.telegram.messenger");
            this.systemAccount = null;
            for (int a = 0; a < accounts.length; a++) {
                Account acc = accounts[a];
                boolean found = false;
                for (int b = 0; b < 3; b++) {
                    User user = UserConfig.getInstance(b).getCurrentUser();
                    if (user != null) {
                        String str = acc.name;
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(TtmlNode.ANONYMOUS_REGION_ID);
                        stringBuilder.append(user.id);
                        if (str.equals(stringBuilder.toString())) {
                            if (b == this.currentAccount) {
                                this.systemAccount = acc;
                            }
                            found = true;
                            if (!found) {
                                try {
                                    am.removeAccount(accounts[a], null, null);
                                } catch (Exception e) {
                                }
                            }
                        }
                    }
                }
                if (!found) {
                    am.removeAccount(accounts[a], null, null);
                }
            }
        } catch (Throwable th) {
        }
        if (UserConfig.getInstance(this.currentAccount).isClientActivated()) {
            readContacts();
            if (this.systemAccount == null) {
                try {
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append(TtmlNode.ANONYMOUS_REGION_ID);
                    stringBuilder2.append(UserConfig.getInstance(this.currentAccount).getClientUserId());
                    this.systemAccount = new Account(stringBuilder2.toString(), "org.telegram.messenger");
                    am.addAccountExplicitly(this.systemAccount, TtmlNode.ANONYMOUS_REGION_ID, null);
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
                for (int b = 0; b < 3; b++) {
                    User user = UserConfig.getInstance(b).getCurrentUser();
                    if (user != null) {
                        String str = acc.name;
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(TtmlNode.ANONYMOUS_REGION_ID);
                        stringBuilder.append(user.id);
                        if (str.equals(stringBuilder.toString())) {
                            found = true;
                            break;
                        }
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
            e2.printStackTrace();
        }
    }

    public void checkContacts() {
        Utilities.globalQueue.postRunnable(new C00883());
    }

    public void forceImportContacts() {
        Utilities.globalQueue.postRunnable(new C00894());
    }

    public void syncPhoneBookByAlert(HashMap<String, Contact> contacts, boolean first, boolean schedule, boolean cancel) {
        final HashMap<String, Contact> hashMap = contacts;
        final boolean z = first;
        final boolean z2 = schedule;
        final boolean z3 = cancel;
        Utilities.globalQueue.postRunnable(new Runnable() {
            public void run() {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.m0d("sync contacts by alert");
                }
                ContactsController.this.performSyncPhoneBook(hashMap, true, z, z2, false, false, z3);
            }
        });
    }

    public void resetImportedContacts() {
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_contacts_resetSaved(), new C17846());
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean checkContactsInternal() {
        boolean reload = false;
        Cursor pCur;
        try {
            if (!hasContactsPermission()) {
                return false;
            }
            ContentResolver cr = ApplicationLoader.applicationContext.getContentResolver();
            pCur = null;
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
            } catch (Throwable e) {
                FileLog.m3e(e);
                if (pCur != null) {
                    pCur.close();
                }
                return reload;
            }
        } catch (Throwable e2) {
            FileLog.m3e(e2);
        } catch (Throwable th) {
            if (pCur != null) {
                pCur.close();
            }
        }
    }

    public void readContacts() {
        synchronized (this.loadContactsSync) {
            if (this.loadingContacts) {
                return;
            }
            this.loadingContacts = true;
            Utilities.stageQueue.postRunnable(new C00917());
        }
    }

    private boolean isNotValidNameString(String src) {
        boolean z = true;
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
            z = false;
        }
        return z;
    }

    private HashMap<String, Contact> readContactsFromPhoneBook() {
        Throwable e;
        Throwable th;
        if (!UserConfig.getInstance(this.currentAccount).syncContacts) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.m0d("contacts sync disabled");
            }
            return new HashMap();
        } else if (hasContactsPermission()) {
            Cursor pCur = null;
            HashMap<String, Contact> contactsMap = null;
            ContactsController contactsController;
            ContactsController contactsController2;
            try {
                String accountType;
                String shortNumber;
                ContentResolver contentResolver;
                ArrayList<String> arrayList;
                String ids;
                Uri uri;
                StringBuilder escaper = new StringBuilder();
                ContentResolver cr = ApplicationLoader.applicationContext.getContentResolver();
                HashMap<String, Contact> shortContacts = new HashMap();
                ArrayList<String> idsArr = new ArrayList();
                pCur = cr.query(Phone.CONTENT_URI, contactsController.projectionPhones, null, null, null);
                int lastContactId = 1;
                boolean z = false;
                boolean z2 = true;
                if (pCur != null) {
                    int count;
                    int count2 = pCur.getCount();
                    if (count2 > 0) {
                        if (null == null) {
                            contactsMap = new HashMap(count2);
                        }
                        while (pCur.moveToNext()) {
                            String number = pCur.getString(z2);
                            accountType = pCur.getString(5);
                            if (accountType == null) {
                                accountType = TtmlNode.ANONYMOUS_REGION_ID;
                            }
                            boolean isGoodAccountType = accountType.indexOf(".sim") != 0 ? z2 : z;
                            if (!TextUtils.isEmpty(number)) {
                                number = PhoneFormat.stripExceptNumbers(number, z2);
                                if (!TextUtils.isEmpty(number)) {
                                    String shortNumber2 = number;
                                    if (number.startsWith("+")) {
                                        shortNumber = number.substring(z2);
                                    } else {
                                        shortNumber = shortNumber2;
                                    }
                                    String lookup_key = pCur.getString(z);
                                    escaper.setLength(z);
                                    String lookup_key2 = lookup_key;
                                    DatabaseUtils.appendEscapedSQLString(escaper, lookup_key2);
                                    String key = escaper.toString();
                                    Contact existingContact = (Contact) shortContacts.get(shortNumber);
                                    ArrayList idsArr2;
                                    if (existingContact != null) {
                                        count = count2;
                                        if (existingContact.isGoodProvider == 0 && !accountType.equals(existingContact.provider)) {
                                            escaper.setLength(0);
                                            DatabaseUtils.appendEscapedSQLString(escaper, existingContact.key);
                                            idsArr2.remove(escaper.toString());
                                            idsArr2.add(key);
                                            existingContact.key = lookup_key2;
                                            existingContact.isGoodProvider = isGoodAccountType;
                                            existingContact.provider = accountType;
                                        }
                                        count2 = count;
                                        z = false;
                                        z2 = true;
                                    } else {
                                        count = count2;
                                        String key2 = key;
                                        if (!idsArr2.contains(key2)) {
                                            idsArr2.add(key2);
                                        }
                                        StringBuilder escaper2 = escaper;
                                        int type = pCur.getInt(2);
                                        Contact contact = (Contact) contactsMap.get(lookup_key2);
                                        if (contact == null) {
                                            existingContact = new Contact();
                                            key2 = pCur.getString(4);
                                            if (key2 == null) {
                                                key2 = TtmlNode.ANONYMOUS_REGION_ID;
                                            } else {
                                                key2 = key2.trim();
                                            }
                                            if (contactsController.isNotValidNameString(key2)) {
                                                existingContact.first_name = key2;
                                                contentResolver = cr;
                                                existingContact.last_name = TtmlNode.ANONYMOUS_REGION_ID;
                                                arrayList = idsArr2;
                                            } else {
                                                contentResolver = cr;
                                                cr = key2.lastIndexOf(32);
                                                if (cr != -1) {
                                                    arrayList = idsArr2;
                                                    try {
                                                        existingContact.first_name = key2.substring(0, cr).trim();
                                                        existingContact.last_name = key2.substring(cr + 1, key2.length()).trim();
                                                    } catch (Throwable th2) {
                                                        e = th2;
                                                        contactsController2 = this;
                                                    }
                                                } else {
                                                    arrayList = idsArr2;
                                                    existingContact.first_name = key2;
                                                    existingContact.last_name = TtmlNode.ANONYMOUS_REGION_ID;
                                                }
                                            }
                                            existingContact.provider = accountType;
                                            existingContact.isGoodProvider = isGoodAccountType;
                                            existingContact.key = lookup_key2;
                                            int lastContactId2 = lastContactId + 1;
                                            existingContact.contact_id = lastContactId;
                                            contactsMap.put(lookup_key2, existingContact);
                                            lastContactId = lastContactId2;
                                        } else {
                                            contentResolver = cr;
                                            Contact contact2 = existingContact;
                                            arrayList = idsArr2;
                                            String str = key2;
                                            existingContact = contact;
                                        }
                                        existingContact.shortPhones.add(shortNumber);
                                        existingContact.phones.add(number);
                                        existingContact.phoneDeleted.add(Integer.valueOf(0));
                                        if (type == 0) {
                                            String custom = pCur.getString(3);
                                            existingContact.phoneTypes.add(custom != null ? custom : LocaleController.getString("PhoneMobile", R.string.PhoneMobile));
                                        } else if (type == 1) {
                                            existingContact.phoneTypes.add(LocaleController.getString("PhoneHome", R.string.PhoneHome));
                                        } else if (type == 2) {
                                            existingContact.phoneTypes.add(LocaleController.getString("PhoneMobile", R.string.PhoneMobile));
                                        } else if (type == 3) {
                                            existingContact.phoneTypes.add(LocaleController.getString("PhoneWork", R.string.PhoneWork));
                                        } else if (type == 12) {
                                            existingContact.phoneTypes.add(LocaleController.getString("PhoneMain", R.string.PhoneMain));
                                        } else {
                                            existingContact.phoneTypes.add(LocaleController.getString("PhoneOther", R.string.PhoneOther));
                                        }
                                        shortContacts.put(shortNumber, existingContact);
                                        count2 = count;
                                        escaper = escaper2;
                                        cr = contentResolver;
                                        idsArr2 = arrayList;
                                        contactsController = this;
                                        z = false;
                                        z2 = true;
                                    }
                                }
                            }
                            count = count2;
                            count2 = count;
                            z = false;
                            z2 = true;
                        }
                    }
                    contentResolver = cr;
                    arrayList = idsArr;
                    count = count2;
                    try {
                        pCur.close();
                    } catch (Exception e2) {
                    }
                    pCur = null;
                } else {
                    contentResolver = cr;
                    arrayList = idsArr;
                }
                try {
                    ids = TextUtils.join(",", arrayList);
                    uri = Data.CONTENT_URI;
                } catch (Throwable th3) {
                    th2 = th3;
                    contactsController2 = this;
                    e = th2;
                    if (pCur != null) {
                        pCur.close();
                    }
                    throw e;
                }
                try {
                    String[] strArr = this.projectionNames;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("lookup IN (");
                    stringBuilder.append(ids);
                    stringBuilder.append(") AND ");
                    stringBuilder.append("mimetype");
                    stringBuilder.append(" = '");
                    stringBuilder.append("vnd.android.cursor.item/name");
                    stringBuilder.append("'");
                    pCur = contentResolver.query(uri, strArr, stringBuilder.toString(), null, null);
                    if (pCur != null) {
                        while (pCur.moveToNext()) {
                            shortNumber = pCur.getString(0);
                            String fname = pCur.getString(1);
                            String sname = pCur.getString(2);
                            accountType = pCur.getString(3);
                            Contact contact3 = (Contact) contactsMap.get(shortNumber);
                            if (contact3 != null && !contact3.namesFilled) {
                                StringBuilder stringBuilder2;
                                if (contact3.isGoodProvider) {
                                    if (fname != null) {
                                        contact3.first_name = fname;
                                    } else {
                                        contact3.first_name = TtmlNode.ANONYMOUS_REGION_ID;
                                    }
                                    if (sname != null) {
                                        contact3.last_name = sname;
                                    } else {
                                        contact3.last_name = TtmlNode.ANONYMOUS_REGION_ID;
                                    }
                                    if (!TextUtils.isEmpty(accountType)) {
                                        if (TextUtils.isEmpty(contact3.first_name)) {
                                            contact3.first_name = accountType;
                                        } else {
                                            stringBuilder2 = new StringBuilder();
                                            stringBuilder2.append(contact3.first_name);
                                            stringBuilder2.append(" ");
                                            stringBuilder2.append(accountType);
                                            contact3.first_name = stringBuilder2.toString();
                                        }
                                    }
                                } else if ((!isNotValidNameString(fname) && (contact3.first_name.contains(fname) || fname.contains(contact3.first_name))) || (!isNotValidNameString(sname) && (contact3.last_name.contains(sname) || fname.contains(contact3.last_name)))) {
                                    if (fname != null) {
                                        contact3.first_name = fname;
                                    } else {
                                        contact3.first_name = TtmlNode.ANONYMOUS_REGION_ID;
                                    }
                                    if (!TextUtils.isEmpty(accountType)) {
                                        if (TextUtils.isEmpty(contact3.first_name)) {
                                            contact3.first_name = accountType;
                                        } else {
                                            stringBuilder2 = new StringBuilder();
                                            stringBuilder2.append(contact3.first_name);
                                            stringBuilder2.append(" ");
                                            stringBuilder2.append(accountType);
                                            contact3.first_name = stringBuilder2.toString();
                                        }
                                    }
                                    if (sname != null) {
                                        contact3.last_name = sname;
                                    } else {
                                        contact3.last_name = TtmlNode.ANONYMOUS_REGION_ID;
                                    }
                                }
                                contact3.namesFilled = true;
                            }
                        }
                        try {
                            pCur.close();
                        } catch (Exception e3) {
                        }
                        pCur = null;
                    }
                    if (pCur != null) {
                        try {
                            pCur.close();
                        } catch (Throwable th22) {
                            FileLog.m3e(th22);
                        }
                    }
                } catch (Throwable th4) {
                    th22 = th4;
                    e = th22;
                    FileLog.m3e(e);
                    if (contactsMap != null) {
                        contactsMap.clear();
                    }
                    if (pCur != null) {
                        pCur.close();
                    }
                    if (contactsMap == null) {
                    }
                    return contactsMap == null ? contactsMap : new HashMap();
                }
            } catch (Throwable th5) {
                th22 = th5;
                contactsController2 = contactsController;
                e = th22;
                if (pCur != null) {
                    pCur.close();
                }
                throw e;
            }
            if (contactsMap == null) {
            }
            return contactsMap == null ? contactsMap : new HashMap();
        } else {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.m0d("app has no contacts permissions");
            }
            return new HashMap();
        }
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

    protected void migratePhoneBookToV7(final SparseArray<Contact> contactHashMap) {
        Utilities.globalQueue.postRunnable(new Runnable() {
            public void run() {
                if (!ContactsController.this.migratingContacts) {
                    ContactsController.this.migratingContacts = true;
                    HashMap<String, Contact> migratedMap = new HashMap();
                    HashMap<String, Contact> contactsMap = ContactsController.this.readContactsFromPhoneBook();
                    HashMap<String, String> contactsBookShort = new HashMap();
                    Iterator it = contactsMap.entrySet().iterator();
                    while (true) {
                        int a = 0;
                        if (!it.hasNext()) {
                            break;
                        }
                        Contact value = (Contact) ((Entry) it.next()).getValue();
                        while (a < value.shortPhones.size()) {
                            contactsBookShort.put(value.shortPhones.get(a), value.key);
                            a++;
                        }
                    }
                    for (int b = 0; b < contactHashMap.size(); b++) {
                        Contact value2 = (Contact) contactHashMap.valueAt(b);
                        for (int a2 = 0; a2 < value2.shortPhones.size(); a2++) {
                            String key = (String) contactsBookShort.get((String) value2.shortPhones.get(a2));
                            if (key != null) {
                                value2.key = key;
                                migratedMap.put(key, value2);
                                break;
                            }
                        }
                    }
                    if (BuildVars.LOGS_ENABLED) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("migrated contacts ");
                        stringBuilder.append(migratedMap.size());
                        stringBuilder.append(" of ");
                        stringBuilder.append(contactHashMap.size());
                        FileLog.m0d(stringBuilder.toString());
                    }
                    MessagesStorage.getInstance(ContactsController.this.currentAccount).putCachedPhoneBook(migratedMap, true);
                }
            }
        });
    }

    protected void performSyncPhoneBook(HashMap<String, Contact> contactHashMap, boolean request, boolean first, boolean schedule, boolean force, boolean checkCount, boolean canceled) {
        ContactsController contactsController;
        if (first) {
            contactsController = this;
        } else if (!this.contactsBookLoaded) {
            return;
        }
        final HashMap<String, Contact> hashMap = contactHashMap;
        final boolean z = schedule;
        final boolean z2 = request;
        final boolean z3 = first;
        final boolean z4 = force;
        final boolean z5 = checkCount;
        final boolean z6 = canceled;
        Utilities.globalQueue.postRunnable(new Runnable() {

            /* renamed from: org.telegram.messenger.ContactsController$9$1 */
            class C00931 implements Runnable {
                C00931() {
                }

                public void run() {
                    ArrayList<User> toDelete = new ArrayList();
                    if (!(hashMap == null || hashMap.isEmpty())) {
                        try {
                            int a;
                            HashMap<String, User> contactsPhonesShort = new HashMap();
                            for (a = 0; a < ContactsController.this.contacts.size(); a++) {
                                User user = MessagesController.getInstance(ContactsController.this.currentAccount).getUser(Integer.valueOf(((TL_contact) ContactsController.this.contacts.get(a)).user_id));
                                if (user != null) {
                                    if (!TextUtils.isEmpty(user.phone)) {
                                        contactsPhonesShort.put(user.phone, user);
                                    }
                                }
                            }
                            a = 0;
                            for (Entry<String, Contact> entry : hashMap.entrySet()) {
                                Contact contact = (Contact) entry.getValue();
                                boolean was = false;
                                int a2 = 0;
                                while (a2 < contact.shortPhones.size()) {
                                    User user2 = (User) contactsPhonesShort.get((String) contact.shortPhones.get(a2));
                                    if (user2 != null) {
                                        was = true;
                                        toDelete.add(user2);
                                        contact.shortPhones.remove(a2);
                                        a2--;
                                    }
                                    a2++;
                                }
                                if (!was || contact.shortPhones.size() == 0) {
                                    a++;
                                }
                            }
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                        }
                    }
                    if (!toDelete.isEmpty()) {
                        ContactsController.this.deleteContact(toDelete);
                    }
                }
            }

            /* JADX WARNING: inconsistent code. */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void run() {
                Iterator it;
                HashMap<String, Contact> contactsBookShort;
                int alreadyImportedContacts;
                int newPhonebookContacts;
                int a;
                int newPhonebookContacts2 = 0;
                int serverContactsInPhonebook = 0;
                boolean disableDeletion = true;
                HashMap<String, Contact> contactShortHashMap = new HashMap();
                for (Entry<String, Contact> entry : hashMap.entrySet()) {
                    Entry<String, Contact> entry2;
                    int a2;
                    Contact c = (Contact) entry2.getValue();
                    for (a2 = 0; a2 < c.shortPhones.size(); a2++) {
                        contactShortHashMap.put(c.shortPhones.get(a2), c);
                    }
                }
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.m0d("start read contacts from phone");
                }
                if (!z) {
                    ContactsController.this.checkContactsInternal();
                }
                HashMap<String, Contact> contactsMap = ContactsController.this.readContactsFromPhoneBook();
                HashMap<String, Contact> contactsBookShort2 = new HashMap();
                int alreadyImportedContacts2 = hashMap.size();
                ArrayList<TL_inputPhoneContact> toImport = new ArrayList();
                boolean disableDeletion2;
                HashMap<String, Contact> hashMap;
                String key;
                String sphone;
                String lastName;
                if (hashMap.isEmpty()) {
                    disableDeletion2 = true;
                    hashMap = contactShortHashMap;
                    contactShortHashMap = contactsMap;
                    contactsBookShort = contactsBookShort2;
                    alreadyImportedContacts = alreadyImportedContacts2;
                    if (z2) {
                        it = contactShortHashMap.entrySet().iterator();
                        while (it.hasNext()) {
                            Iterator it2;
                            entry2 = (Entry) it.next();
                            c = (Contact) entry2.getValue();
                            key = (String) entry2.getKey();
                            int serverContactsInPhonebook2 = serverContactsInPhonebook;
                            serverContactsInPhonebook = 0;
                            while (serverContactsInPhonebook < c.phones.size()) {
                                int i;
                                Entry<String, Contact> pair;
                                if (z4) {
                                    i = newPhonebookContacts2;
                                    it2 = it;
                                } else {
                                    sphone = (String) c.shortPhones.get(serverContactsInPhonebook);
                                    String sphone9 = sphone.substring(Math.max(0, sphone.length() - 7));
                                    TL_contact contact = (TL_contact) ContactsController.this.contactsByPhone.get(sphone);
                                    if (contact != null) {
                                        i = newPhonebookContacts2;
                                        newPhonebookContacts2 = MessagesController.getInstance(ContactsController.this.currentAccount).getUser(Integer.valueOf(contact.user_id));
                                        if (newPhonebookContacts2 != 0) {
                                            serverContactsInPhonebook2++;
                                            String firstName = newPhonebookContacts2.first_name != null ? newPhonebookContacts2.first_name : TtmlNode.ANONYMOUS_REGION_ID;
                                            it2 = it;
                                            lastName = newPhonebookContacts2.last_name != null ? newPhonebookContacts2.last_name : TtmlNode.ANONYMOUS_REGION_ID;
                                            User user = newPhonebookContacts2;
                                            if (firstName.equals(c.first_name) == 0 || lastName.equals(c.last_name) == 0) {
                                                if (!(TextUtils.isEmpty(c.first_name) == 0 || TextUtils.isEmpty(c.last_name) == 0)) {
                                                }
                                            }
                                            pair = entry2;
                                            serverContactsInPhonebook++;
                                            newPhonebookContacts2 = i;
                                            it = it2;
                                            entry2 = pair;
                                        } else {
                                            it2 = it;
                                        }
                                    } else {
                                        i = newPhonebookContacts2;
                                        it2 = it;
                                        if (ContactsController.this.contactsByShortPhone.containsKey(sphone9) != 0) {
                                            serverContactsInPhonebook2++;
                                        }
                                    }
                                }
                                newPhonebookContacts2 = new TL_inputPhoneContact();
                                newPhonebookContacts2.client_id = (long) c.contact_id;
                                pair = entry2;
                                newPhonebookContacts2.client_id |= ((long) serverContactsInPhonebook) << 32;
                                newPhonebookContacts2.first_name = c.first_name;
                                newPhonebookContacts2.last_name = c.last_name;
                                newPhonebookContacts2.phone = (String) c.phones.get(serverContactsInPhonebook);
                                toImport.add(newPhonebookContacts2);
                                serverContactsInPhonebook++;
                                newPhonebookContacts2 = i;
                                it = it2;
                                entry2 = pair;
                            }
                            it2 = it;
                            serverContactsInPhonebook = serverContactsInPhonebook2;
                        }
                    }
                    alreadyImportedContacts2 = serverContactsInPhonebook;
                    newPhonebookContacts = newPhonebookContacts2;
                } else {
                    HashMap<String, Contact> contactsMap2;
                    int alreadyImportedContacts3;
                    it = contactsMap.entrySet().iterator();
                    while (it.hasNext()) {
                        Iterator it3;
                        HashMap<String, Contact> hashMap2;
                        int serverContactsInPhonebook3;
                        Entry<String, Contact> pair2 = (Entry) it.next();
                        sphone = (String) pair2.getKey();
                        Contact value = (Contact) pair2.getValue();
                        c = (Contact) hashMap.get(sphone);
                        if (c == null) {
                            a = 0;
                            while (true) {
                                a2 = a;
                                if (a2 >= value.shortPhones.size()) {
                                    break;
                                }
                                Contact c2 = (Contact) contactShortHashMap.get(value.shortPhones.get(a2));
                                if (c2 != null) {
                                    break;
                                }
                                a = a2 + 1;
                            }
                        }
                        if (c != null) {
                            value.imported = c.imported;
                        }
                        boolean nameChanged = (c == null || ((TextUtils.isEmpty(value.first_name) || c.first_name.equals(value.first_name)) && (TextUtils.isEmpty(value.last_name) || c.last_name.equals(value.last_name)))) ? false : true;
                        Entry<String, Contact> entry3;
                        if (c == null) {
                            it3 = it;
                            entry3 = pair2;
                            disableDeletion2 = disableDeletion;
                            hashMap = contactShortHashMap;
                            contactsMap2 = contactsMap;
                            hashMap2 = contactsBookShort2;
                        } else if (nameChanged) {
                            it3 = it;
                            entry3 = pair2;
                            disableDeletion2 = disableDeletion;
                            hashMap = contactShortHashMap;
                            contactsMap2 = contactsMap;
                            hashMap2 = contactsBookShort2;
                        } else {
                            a = serverContactsInPhonebook;
                            serverContactsInPhonebook = newPhonebookContacts2;
                            newPhonebookContacts2 = 0;
                            while (newPhonebookContacts2 < value.phones.size()) {
                                key = (String) value.shortPhones.get(newPhonebookContacts2);
                                it3 = it;
                                entry3 = pair2;
                                lastName = key.substring(Math.max(null, key.length() - 7));
                                contactsBookShort2.put(key, value);
                                boolean emptyNameReimport = false;
                                boolean index = c.shortPhones.indexOf(key);
                                if (z2 != 0) {
                                    TL_contact contact2 = (TL_contact) ContactsController.this.contactsByPhone.get(key);
                                    if (contact2 != null) {
                                        hashMap = contactShortHashMap;
                                        disableDeletion2 = disableDeletion;
                                        disableDeletion = MessagesController.getInstance(ContactsController.this.currentAccount).getUser(Integer.valueOf(contact2.user_id));
                                        if (disableDeletion) {
                                            a++;
                                            if (TextUtils.isEmpty(disableDeletion.first_name) && TextUtils.isEmpty(disableDeletion.last_name) && !(TextUtils.isEmpty(value.first_name) && TextUtils.isEmpty(value.last_name))) {
                                                emptyNameReimport = true;
                                                index = true;
                                            }
                                        }
                                    } else {
                                        disableDeletion2 = disableDeletion;
                                        hashMap = contactShortHashMap;
                                        if (ContactsController.this.contactsByShortPhone.containsKey(lastName)) {
                                            a++;
                                        }
                                    }
                                } else {
                                    disableDeletion2 = disableDeletion;
                                    hashMap = contactShortHashMap;
                                }
                                boolean index2 = index;
                                if (!index2) {
                                    String str = lastName;
                                    String str2 = key;
                                    contactsMap2 = contactsMap;
                                    hashMap2 = contactsBookShort2;
                                    value.phoneDeleted.set(newPhonebookContacts2, c.phoneDeleted.get(index2));
                                    c.phones.remove(index2);
                                    c.shortPhones.remove(index2);
                                    c.phoneDeleted.remove(index2);
                                    c.phoneTypes.remove(index2);
                                } else if (z2) {
                                    if (emptyNameReimport) {
                                    } else {
                                        TL_contact disableDeletion3 = (TL_contact) ContactsController.this.contactsByPhone.get(key);
                                        TL_contact contact3;
                                        if (disableDeletion3 != null) {
                                            key = MessagesController.getInstance(ContactsController.this.currentAccount).getUser(Integer.valueOf(disableDeletion3.user_id));
                                            String user2;
                                            if (key != null) {
                                                a++;
                                                String firstName2 = key.first_name != null ? key.first_name : TtmlNode.ANONYMOUS_REGION_ID;
                                                contact3 = disableDeletion3;
                                                disableDeletion = key.last_name ? key.last_name : TtmlNode.ANONYMOUS_REGION_ID;
                                                user2 = key;
                                                if (firstName2.equals(value.first_name) == null || disableDeletion.equals(value.last_name) == null) {
                                                    if (TextUtils.isEmpty(value.first_name) == null || TextUtils.isEmpty(value.last_name) == null) {
                                                    }
                                                }
                                                contactsMap2 = contactsMap;
                                                hashMap2 = contactsBookShort2;
                                            } else {
                                                user2 = key;
                                                contact3 = disableDeletion3;
                                                serverContactsInPhonebook++;
                                            }
                                        } else {
                                            contact3 = disableDeletion3;
                                            if (ContactsController.this.contactsByShortPhone.containsKey(lastName)) {
                                                a++;
                                            }
                                        }
                                    }
                                    key = new TL_inputPhoneContact();
                                    key.client_id = (long) value.contact_id;
                                    int newPhonebookContacts3 = serverContactsInPhonebook;
                                    contactsMap2 = contactsMap;
                                    hashMap2 = contactsBookShort2;
                                    key.client_id |= ((long) newPhonebookContacts2) << 32;
                                    key.first_name = value.first_name;
                                    key.last_name = value.last_name;
                                    key.phone = (String) value.phones.get(newPhonebookContacts2);
                                    toImport.add(key);
                                    serverContactsInPhonebook = newPhonebookContacts3;
                                } else {
                                    contactsMap2 = contactsMap;
                                    hashMap2 = contactsBookShort2;
                                }
                                newPhonebookContacts2++;
                                it = it3;
                                pair2 = entry3;
                                contactShortHashMap = hashMap;
                                disableDeletion = disableDeletion2;
                                contactsMap = contactsMap2;
                                contactsBookShort2 = hashMap2;
                            }
                            it3 = it;
                            entry3 = pair2;
                            disableDeletion2 = disableDeletion;
                            hashMap = contactShortHashMap;
                            contactsMap2 = contactsMap;
                            hashMap2 = contactsBookShort2;
                            if (c.phones.isEmpty()) {
                                hashMap.remove(sphone);
                            }
                            newPhonebookContacts2 = serverContactsInPhonebook;
                            alreadyImportedContacts3 = alreadyImportedContacts2;
                            serverContactsInPhonebook = a;
                            disableDeletion = hashMap2;
                            contactsBookShort2 = disableDeletion;
                            it = it3;
                            contactShortHashMap = hashMap;
                            disableDeletion = disableDeletion2;
                            contactsMap = contactsMap2;
                            alreadyImportedContacts2 = alreadyImportedContacts3;
                        }
                        int serverContactsInPhonebook4 = serverContactsInPhonebook;
                        serverContactsInPhonebook = newPhonebookContacts2;
                        newPhonebookContacts2 = 0;
                        while (newPhonebookContacts2 < value.phones.size()) {
                            TL_inputPhoneContact imp;
                            int newPhonebookContacts4;
                            key = (String) value.shortPhones.get(newPhonebookContacts2);
                            String sphone92 = key.substring(Math.max(false, key.length() - 7));
                            disableDeletion = hashMap2;
                            disableDeletion.put(key, value);
                            if (c != null) {
                                int index3 = c.shortPhones.indexOf(key);
                                if (index3 != -1) {
                                    Integer deleted = (Integer) c.phoneDeleted.get(index3);
                                    value.phoneDeleted.set(newPhonebookContacts2, deleted);
                                    if (deleted.intValue() == 1) {
                                        serverContactsInPhonebook3 = serverContactsInPhonebook4;
                                        alreadyImportedContacts3 = alreadyImportedContacts2;
                                        serverContactsInPhonebook4 = serverContactsInPhonebook3;
                                        newPhonebookContacts2++;
                                        hashMap2 = disableDeletion;
                                        alreadyImportedContacts2 = alreadyImportedContacts3;
                                    }
                                    if (z2) {
                                        serverContactsInPhonebook3 = serverContactsInPhonebook4;
                                        alreadyImportedContacts3 = alreadyImportedContacts2;
                                        serverContactsInPhonebook4 = serverContactsInPhonebook3;
                                        newPhonebookContacts2++;
                                        hashMap2 = disableDeletion;
                                        alreadyImportedContacts2 = alreadyImportedContacts3;
                                    } else {
                                        if (!nameChanged) {
                                            if (ContactsController.this.contactsByPhone.containsKey(key)) {
                                                serverContactsInPhonebook++;
                                            } else {
                                                serverContactsInPhonebook4++;
                                                alreadyImportedContacts3 = alreadyImportedContacts2;
                                                newPhonebookContacts2++;
                                                hashMap2 = disableDeletion;
                                                alreadyImportedContacts2 = alreadyImportedContacts3;
                                            }
                                        }
                                        imp = new TL_inputPhoneContact();
                                        imp.client_id = (long) value.contact_id;
                                        newPhonebookContacts4 = serverContactsInPhonebook;
                                        serverContactsInPhonebook3 = serverContactsInPhonebook4;
                                        alreadyImportedContacts3 = alreadyImportedContacts2;
                                        imp.client_id |= ((long) newPhonebookContacts2) << 32;
                                        imp.first_name = value.first_name;
                                        imp.last_name = value.last_name;
                                        imp.phone = (String) value.phones.get(newPhonebookContacts2);
                                        toImport.add(imp);
                                        serverContactsInPhonebook4 = serverContactsInPhonebook3;
                                        serverContactsInPhonebook = newPhonebookContacts4;
                                        newPhonebookContacts2++;
                                        hashMap2 = disableDeletion;
                                        alreadyImportedContacts2 = alreadyImportedContacts3;
                                    }
                                }
                            }
                            if (z2) {
                                serverContactsInPhonebook3 = serverContactsInPhonebook4;
                                alreadyImportedContacts3 = alreadyImportedContacts2;
                                serverContactsInPhonebook4 = serverContactsInPhonebook3;
                                newPhonebookContacts2++;
                                hashMap2 = disableDeletion;
                                alreadyImportedContacts2 = alreadyImportedContacts3;
                            } else {
                                if (nameChanged) {
                                    if (ContactsController.this.contactsByPhone.containsKey(key)) {
                                        serverContactsInPhonebook++;
                                    } else {
                                        serverContactsInPhonebook4++;
                                        alreadyImportedContacts3 = alreadyImportedContacts2;
                                        newPhonebookContacts2++;
                                        hashMap2 = disableDeletion;
                                        alreadyImportedContacts2 = alreadyImportedContacts3;
                                    }
                                }
                                imp = new TL_inputPhoneContact();
                                imp.client_id = (long) value.contact_id;
                                newPhonebookContacts4 = serverContactsInPhonebook;
                                serverContactsInPhonebook3 = serverContactsInPhonebook4;
                                alreadyImportedContacts3 = alreadyImportedContacts2;
                                imp.client_id |= ((long) newPhonebookContacts2) << 32;
                                imp.first_name = value.first_name;
                                imp.last_name = value.last_name;
                                imp.phone = (String) value.phones.get(newPhonebookContacts2);
                                toImport.add(imp);
                                serverContactsInPhonebook4 = serverContactsInPhonebook3;
                                serverContactsInPhonebook = newPhonebookContacts4;
                                newPhonebookContacts2++;
                                hashMap2 = disableDeletion;
                                alreadyImportedContacts2 = alreadyImportedContacts3;
                            }
                        }
                        serverContactsInPhonebook3 = serverContactsInPhonebook4;
                        alreadyImportedContacts3 = alreadyImportedContacts2;
                        disableDeletion = hashMap2;
                        if (c != null) {
                            hashMap.remove(sphone);
                        }
                        newPhonebookContacts2 = serverContactsInPhonebook;
                        serverContactsInPhonebook = serverContactsInPhonebook3;
                        contactsBookShort2 = disableDeletion;
                        it = it3;
                        contactShortHashMap = hashMap;
                        disableDeletion = disableDeletion2;
                        contactsMap = contactsMap2;
                        alreadyImportedContacts2 = alreadyImportedContacts3;
                    }
                    disableDeletion2 = disableDeletion;
                    hashMap = contactShortHashMap;
                    contactsMap2 = contactsMap;
                    contactsBookShort = contactsBookShort2;
                    alreadyImportedContacts3 = alreadyImportedContacts2;
                    if (!z3 && hashMap.isEmpty() && toImport.isEmpty()) {
                        contactShortHashMap = contactsMap2;
                        alreadyImportedContacts = alreadyImportedContacts3;
                        if (alreadyImportedContacts == contactShortHashMap.size()) {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.m0d("contacts not changed!");
                            }
                            return;
                        }
                    }
                    contactShortHashMap = contactsMap2;
                    alreadyImportedContacts = alreadyImportedContacts3;
                    if (!(!z2 || hashMap.isEmpty() || contactShortHashMap.isEmpty())) {
                        if (toImport.isEmpty()) {
                            MessagesStorage.getInstance(ContactsController.this.currentAccount).putCachedPhoneBook(contactShortHashMap, false);
                        }
                        if (!(disableDeletion2 || hashMap.isEmpty())) {
                            AndroidUtilities.runOnUIThread(new C00931());
                        }
                    }
                    newPhonebookContacts = newPhonebookContacts2;
                    alreadyImportedContacts2 = serverContactsInPhonebook;
                }
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.m0d("done processing contacts");
                }
                if (!z2) {
                    Utilities.stageQueue.postRunnable(new Runnable() {
                        public void run() {
                            ContactsController.this.contactsBookSPhones = contactsBookShort;
                            ContactsController.this.contactsBook = contactShortHashMap;
                            ContactsController.this.contactsSyncInProgress = false;
                            ContactsController.this.contactsBookLoaded = true;
                            if (z3) {
                                ContactsController.this.contactsLoaded = true;
                            }
                            if (!ContactsController.this.delayedContactsUpdate.isEmpty() && ContactsController.this.contactsLoaded && ContactsController.this.contactsBookLoaded) {
                                ContactsController.this.applyContactsUpdates(ContactsController.this.delayedContactsUpdate, null, null, null);
                                ContactsController.this.delayedContactsUpdate.clear();
                            }
                        }
                    });
                    if (!contactShortHashMap.isEmpty()) {
                        MessagesStorage.getInstance(ContactsController.this.currentAccount).putCachedPhoneBook(contactShortHashMap, false);
                    }
                } else if (toImport.isEmpty()) {
                    Utilities.stageQueue.postRunnable(new Runnable() {

                        /* renamed from: org.telegram.messenger.ContactsController$9$5$1 */
                        class C01001 implements Runnable {
                            C01001() {
                            }

                            public void run() {
                                ContactsController.this.updateUnregisteredContacts(ContactsController.this.contacts);
                                NotificationCenter.getInstance(ContactsController.this.currentAccount).postNotificationName(NotificationCenter.contactsDidLoaded, new Object[0]);
                                NotificationCenter.getInstance(ContactsController.this.currentAccount).postNotificationName(NotificationCenter.contactsImported, new Object[0]);
                            }
                        }

                        public void run() {
                            ContactsController.this.contactsBookSPhones = contactsBookShort;
                            ContactsController.this.contactsBook = contactShortHashMap;
                            ContactsController.this.contactsSyncInProgress = false;
                            ContactsController.this.contactsBookLoaded = true;
                            if (z3) {
                                ContactsController.this.contactsLoaded = true;
                            }
                            if (!ContactsController.this.delayedContactsUpdate.isEmpty() && ContactsController.this.contactsLoaded) {
                                ContactsController.this.applyContactsUpdates(ContactsController.this.delayedContactsUpdate, null, null, null);
                                ContactsController.this.delayedContactsUpdate.clear();
                            }
                            AndroidUtilities.runOnUIThread(new C01001());
                        }
                    });
                } else {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.m1e("start import contacts");
                    }
                    if (!z5 || newPhonebookContacts == 0) {
                        a2 = 0;
                    } else if (newPhonebookContacts >= 30) {
                        a2 = 1;
                    } else if (z3 && alreadyImportedContacts == 0 && ContactsController.this.contactsByPhone.size() - alreadyImportedContacts2 > (ContactsController.this.contactsByPhone.size() / 3) * 2) {
                        a2 = 2;
                    } else {
                        a2 = 0;
                    }
                    final int checkType = a2;
                    if (BuildVars.LOGS_ENABLED) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("new phone book contacts ");
                        stringBuilder.append(newPhonebookContacts);
                        stringBuilder.append(" serverContactsInPhonebook ");
                        stringBuilder.append(alreadyImportedContacts2);
                        stringBuilder.append(" totalContacts ");
                        stringBuilder.append(ContactsController.this.contactsByPhone.size());
                        FileLog.m0d(stringBuilder.toString());
                    }
                    if (checkType != 0) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                NotificationCenter.getInstance(ContactsController.this.currentAccount).postNotificationName(NotificationCenter.hasNewContactsToImport, Integer.valueOf(checkType), hashMap, Boolean.valueOf(z3), Boolean.valueOf(z));
                            }
                        });
                    } else if (z6) {
                        Utilities.stageQueue.postRunnable(new Runnable() {

                            /* renamed from: org.telegram.messenger.ContactsController$9$3$1 */
                            class C00951 implements Runnable {
                                C00951() {
                                }

                                public void run() {
                                    ContactsController.this.updateUnregisteredContacts(ContactsController.this.contacts);
                                    NotificationCenter.getInstance(ContactsController.this.currentAccount).postNotificationName(NotificationCenter.contactsDidLoaded, new Object[0]);
                                    NotificationCenter.getInstance(ContactsController.this.currentAccount).postNotificationName(NotificationCenter.contactsImported, new Object[0]);
                                }
                            }

                            public void run() {
                                ContactsController.this.contactsBookSPhones = contactsBookShort;
                                ContactsController.this.contactsBook = contactShortHashMap;
                                ContactsController.this.contactsSyncInProgress = false;
                                ContactsController.this.contactsBookLoaded = true;
                                if (z3) {
                                    ContactsController.this.contactsLoaded = true;
                                }
                                if (!ContactsController.this.delayedContactsUpdate.isEmpty() && ContactsController.this.contactsLoaded) {
                                    ContactsController.this.applyContactsUpdates(ContactsController.this.delayedContactsUpdate, null, null, null);
                                    ContactsController.this.delayedContactsUpdate.clear();
                                }
                                MessagesStorage.getInstance(ContactsController.this.currentAccount).putCachedPhoneBook(contactShortHashMap, false);
                                AndroidUtilities.runOnUIThread(new C00951());
                            }
                        });
                    } else {
                        final boolean[] hasErrors = new boolean[]{false};
                        HashMap<String, Contact> contactsMapToSave = new HashMap(contactShortHashMap);
                        SparseArray<String> contactIdToKey = new SparseArray();
                        for (Entry<String, Contact> entry4 : contactsMapToSave.entrySet()) {
                            Contact value2 = (Contact) entry4.getValue();
                            contactIdToKey.put(value2.contact_id, value2.key);
                        }
                        ContactsController.this.completedRequestsCount = 0;
                        a2 = (int) Math.ceil(((double) toImport.size()) / 500.0d);
                        int a3 = 0;
                        while (true) {
                            int a4 = a3;
                            if (a4 >= a2) {
                                break;
                            }
                            TLObject req = new TL_contacts_importContacts();
                            serverContactsInPhonebook = a4 * 500;
                            int a5 = a4;
                            a4 = Math.min(serverContactsInPhonebook + 500, toImport.size());
                            int count = a2;
                            req.contacts = new ArrayList(toImport.subList(serverContactsInPhonebook, a4));
                            C17854 c17854 = r0;
                            TLObject req2 = req;
                            final HashMap<String, Contact> hashMap3 = contactsMapToSave;
                            a = a5;
                            final SparseArray<String> sparseArray = contactIdToKey;
                            int alreadyImportedContacts4 = alreadyImportedContacts;
                            int count2 = count;
                            ConnectionsManager instance = ConnectionsManager.getInstance(ContactsController.this.currentAccount);
                            final HashMap<String, Contact> hashMap4 = contactShortHashMap;
                            SparseArray<String> contactIdToKey2 = contactIdToKey;
                            final TLObject tLObject = req2;
                            HashMap<String, Contact> contactsMapToSave2 = contactsMapToSave;
                            final int i2 = count2;
                            int checkType2 = checkType;
                            checkType = contactsBookShort;
                            C17854 c178542 = new RequestDelegate() {

                                /* renamed from: org.telegram.messenger.ContactsController$9$4$1 */
                                class C00991 implements Runnable {

                                    /* renamed from: org.telegram.messenger.ContactsController$9$4$1$1 */
                                    class C00971 implements Runnable {
                                        C00971() {
                                        }

                                        public void run() {
                                            NotificationCenter.getInstance(ContactsController.this.currentAccount).postNotificationName(NotificationCenter.contactsImported, new Object[0]);
                                        }
                                    }

                                    /* renamed from: org.telegram.messenger.ContactsController$9$4$1$2 */
                                    class C00982 implements Runnable {
                                        C00982() {
                                        }

                                        public void run() {
                                            MessagesStorage.getInstance(ContactsController.this.currentAccount).getCachedPhoneBook(true);
                                        }
                                    }

                                    C00991() {
                                    }

                                    public void run() {
                                        ContactsController.this.contactsBookSPhones = checkType;
                                        ContactsController.this.contactsBook = hashMap4;
                                        ContactsController.this.contactsSyncInProgress = false;
                                        ContactsController.this.contactsBookLoaded = true;
                                        if (z3) {
                                            ContactsController.this.contactsLoaded = true;
                                        }
                                        if (!ContactsController.this.delayedContactsUpdate.isEmpty() && ContactsController.this.contactsLoaded) {
                                            ContactsController.this.applyContactsUpdates(ContactsController.this.delayedContactsUpdate, null, null, null);
                                            ContactsController.this.delayedContactsUpdate.clear();
                                        }
                                        AndroidUtilities.runOnUIThread(new C00971());
                                        if (hasErrors[0]) {
                                            Utilities.globalQueue.postRunnable(new C00982(), 1800000);
                                        }
                                    }
                                }

                                public void run(TLObject response, TL_error error) {
                                    ContactsController.this.completedRequestsCount = ContactsController.this.completedRequestsCount + 1;
                                    if (error == null) {
                                        int a;
                                        if (BuildVars.LOGS_ENABLED) {
                                            FileLog.m0d("contacts imported");
                                        }
                                        TL_contacts_importedContacts res = (TL_contacts_importedContacts) response;
                                        if (!res.retry_contacts.isEmpty()) {
                                            for (a = 0; a < res.retry_contacts.size(); a++) {
                                                hashMap3.remove(sparseArray.get((int) ((Long) res.retry_contacts.get(a)).longValue()));
                                            }
                                            hasErrors[0] = true;
                                            if (BuildVars.LOGS_ENABLED) {
                                                FileLog.m0d("result has retry contacts");
                                            }
                                        }
                                        for (a = 0; a < res.popular_invites.size(); a++) {
                                            TL_popularContact popularContact = (TL_popularContact) res.popular_invites.get(a);
                                            Contact contact = (Contact) hashMap4.get(sparseArray.get((int) popularContact.client_id));
                                            if (contact != null) {
                                                contact.imported = popularContact.importers;
                                            }
                                        }
                                        MessagesStorage.getInstance(ContactsController.this.currentAccount).putUsersAndChats(res.users, null, true, true);
                                        ArrayList<TL_contact> cArr = new ArrayList();
                                        for (a = 0; a < res.imported.size(); a++) {
                                            TL_contact contact2 = new TL_contact();
                                            contact2.user_id = ((TL_importedContact) res.imported.get(a)).user_id;
                                            cArr.add(contact2);
                                        }
                                        ContactsController.this.processLoadedContacts(cArr, res.users, 2);
                                    } else {
                                        for (int a2 = 0; a2 < tLObject.contacts.size(); a2++) {
                                            hashMap3.remove(sparseArray.get((int) ((TL_inputPhoneContact) tLObject.contacts.get(a2)).client_id));
                                        }
                                        hasErrors[0] = true;
                                        if (BuildVars.LOGS_ENABLED) {
                                            StringBuilder stringBuilder = new StringBuilder();
                                            stringBuilder.append("import contacts error ");
                                            stringBuilder.append(error.text);
                                            FileLog.m0d(stringBuilder.toString());
                                        }
                                    }
                                    if (ContactsController.this.completedRequestsCount == i2) {
                                        if (!hashMap3.isEmpty()) {
                                            MessagesStorage.getInstance(ContactsController.this.currentAccount).putCachedPhoneBook(hashMap3, false);
                                        }
                                        Utilities.stageQueue.postRunnable(new C00991());
                                    }
                                }
                            };
                            instance.sendRequest(req2, c17854, 6);
                            a2 = count2;
                            contactIdToKey = contactIdToKey2;
                            contactsMapToSave = contactsMapToSave2;
                            checkType = checkType2;
                            alreadyImportedContacts = alreadyImportedContacts4;
                            a3 = a + 1;
                        }
                    }
                }
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

    private int getContactsHash(ArrayList<TL_contact> contacts) {
        contacts = new ArrayList(contacts);
        Collections.sort(contacts, new Comparator<TL_contact>() {
            public int compare(TL_contact tl_contact, TL_contact tl_contact2) {
                if (tl_contact.user_id > tl_contact2.user_id) {
                    return 1;
                }
                if (tl_contact.user_id < tl_contact2.user_id) {
                    return -1;
                }
                return 0;
            }
        });
        int count = contacts.size();
        long acc = 0;
        for (int a = -1; a < count; a++) {
            long j;
            if (a == -1) {
                j = (((20261 * acc) + 2147483648L) + ((long) UserConfig.getInstance(this.currentAccount).contactsSavedCount)) % 2147483648L;
            } else {
                j = (((20261 * acc) + 2147483648L) + ((long) ((TL_contact) contacts.get(a)).user_id)) % 2147483648L;
            }
            acc = j;
        }
        return (int) acc;
    }

    public void loadContacts(boolean fromCache, final int hash) {
        synchronized (this.loadContactsSync) {
            this.loadingContacts = true;
        }
        if (fromCache) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.m0d("load contacts from cache");
            }
            MessagesStorage.getInstance(this.currentAccount).getContacts();
            return;
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.m0d("load contacts from server");
        }
        TL_contacts_getContacts req = new TL_contacts_getContacts();
        req.hash = hash;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {

            /* renamed from: org.telegram.messenger.ContactsController$11$1 */
            class C00691 implements Runnable {
                C00691() {
                }

                public void run() {
                    synchronized (ContactsController.this.loadContactsSync) {
                        ContactsController.this.loadingContacts = false;
                    }
                    NotificationCenter.getInstance(ContactsController.this.currentAccount).postNotificationName(NotificationCenter.contactsDidLoaded, new Object[0]);
                }
            }

            public void run(TLObject response, TL_error error) {
                if (error == null) {
                    contacts_Contacts res = (contacts_Contacts) response;
                    if (hash == 0 || !(res instanceof TL_contacts_contactsNotModified)) {
                        UserConfig.getInstance(ContactsController.this.currentAccount).contactsSavedCount = res.saved_count;
                        UserConfig.getInstance(ContactsController.this.currentAccount).saveConfig(false);
                        ContactsController.this.processLoadedContacts(res.contacts, res.users, 0);
                    } else {
                        ContactsController.this.contactsLoaded = true;
                        if (!ContactsController.this.delayedContactsUpdate.isEmpty() && ContactsController.this.contactsBookLoaded) {
                            ContactsController.this.applyContactsUpdates(ContactsController.this.delayedContactsUpdate, null, null, null);
                            ContactsController.this.delayedContactsUpdate.clear();
                        }
                        UserConfig.getInstance(ContactsController.this.currentAccount).lastContactsSyncTime = (int) (System.currentTimeMillis() / 1000);
                        UserConfig.getInstance(ContactsController.this.currentAccount).saveConfig(false);
                        AndroidUtilities.runOnUIThread(new C00691());
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.m0d("load contacts don't change");
                        }
                    }
                }
            }
        });
    }

    public void processLoadedContacts(final ArrayList<TL_contact> contactsArr, final ArrayList<User> usersArr, final int from) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                int a;
                int a2 = 0;
                MessagesController.getInstance(ContactsController.this.currentAccount).putUsers(usersArr, from == 1);
                final SparseArray<User> usersDict = new SparseArray();
                final boolean isEmpty = contactsArr.isEmpty();
                if (!ContactsController.this.contacts.isEmpty()) {
                    a = 0;
                    while (a < contactsArr.size()) {
                        if (ContactsController.this.contactsDict.get(Integer.valueOf(((TL_contact) contactsArr.get(a)).user_id)) != null) {
                            contactsArr.remove(a);
                            a--;
                        }
                        a++;
                    }
                    contactsArr.addAll(ContactsController.this.contacts);
                }
                while (true) {
                    a = a2;
                    if (a < contactsArr.size()) {
                        User user = MessagesController.getInstance(ContactsController.this.currentAccount).getUser(Integer.valueOf(((TL_contact) contactsArr.get(a)).user_id));
                        if (user != null) {
                            usersDict.put(user.id, user);
                        }
                        a2 = a + 1;
                    } else {
                        Utilities.stageQueue.postRunnable(new Runnable() {

                            /* renamed from: org.telegram.messenger.ContactsController$12$1$1 */
                            class C00701 implements Comparator<TL_contact> {
                                C00701() {
                                }

                                public int compare(TL_contact tl_contact, TL_contact tl_contact2) {
                                    return UserObject.getFirstName((User) usersDict.get(tl_contact.user_id)).compareTo(UserObject.getFirstName((User) usersDict.get(tl_contact2.user_id)));
                                }
                            }

                            /* renamed from: org.telegram.messenger.ContactsController$12$1$2 */
                            class C00712 implements Comparator<String> {
                                C00712() {
                                }

                                public int compare(String s, String s2) {
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
                            }

                            /* renamed from: org.telegram.messenger.ContactsController$12$1$3 */
                            class C00723 implements Comparator<String> {
                                C00723() {
                                }

                                public int compare(String s, String s2) {
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
                            }

                            public void run() {
                                HashMap<String, TL_contact> contactsByPhonesShortDictFinal;
                                C00761 c00761 = this;
                                if (BuildVars.LOGS_ENABLED) {
                                    FileLog.m0d("done loading contacts");
                                }
                                int i = 0;
                                if (from == 1 && (contactsArr.isEmpty() || Math.abs((System.currentTimeMillis() / 1000) - ((long) UserConfig.getInstance(ContactsController.this.currentAccount).lastContactsSyncTime)) >= 86400)) {
                                    ContactsController.this.loadContacts(false, ContactsController.this.getContactsHash(contactsArr));
                                    if (contactsArr.isEmpty()) {
                                        return;
                                    }
                                }
                                if (from == 0) {
                                    UserConfig.getInstance(ContactsController.this.currentAccount).lastContactsSyncTime = (int) (System.currentTimeMillis() / 1000);
                                    UserConfig.getInstance(ContactsController.this.currentAccount).saveConfig(false);
                                }
                                int a = 0;
                                while (a < contactsArr.size()) {
                                    TL_contact contact = (TL_contact) contactsArr.get(a);
                                    if (usersDict.get(contact.user_id) != null || contact.user_id == UserConfig.getInstance(ContactsController.this.currentAccount).getClientUserId()) {
                                        a++;
                                    } else {
                                        ContactsController.this.loadContacts(false, 0);
                                        if (BuildVars.LOGS_ENABLED) {
                                            FileLog.m0d("contacts are broken, load from server");
                                        }
                                        return;
                                    }
                                }
                                if (from != 1) {
                                    MessagesStorage.getInstance(ContactsController.this.currentAccount).putUsersAndChats(usersArr, null, true, true);
                                    MessagesStorage.getInstance(ContactsController.this.currentAccount).putContacts(contactsArr, from != 2);
                                }
                                Collections.sort(contactsArr, new C00701());
                                ConcurrentHashMap<Integer, TL_contact> contactsDictionary = new ConcurrentHashMap(20, 1.0f, 2);
                                HashMap<String, ArrayList<TL_contact>> sectionsDict = new HashMap();
                                HashMap<String, ArrayList<TL_contact>> sectionsDictMutual = new HashMap();
                                ArrayList<String> sortedSectionsArray = new ArrayList();
                                ArrayList<String> sortedSectionsArrayMutual = new ArrayList();
                                HashMap<String, TL_contact> contactsByPhonesDict = null;
                                HashMap<String, TL_contact> contactsByPhonesShortDict = null;
                                if (!ContactsController.this.contactsBookLoaded) {
                                    contactsByPhonesDict = new HashMap();
                                    contactsByPhonesShortDict = new HashMap();
                                }
                                HashMap<String, TL_contact> contactsByPhonesDict2 = contactsByPhonesDict;
                                HashMap<String, TL_contact> contactsByPhonesShortDict2 = contactsByPhonesShortDict;
                                HashMap<String, TL_contact> contactsByPhonesDictFinal = contactsByPhonesDict2;
                                HashMap<String, TL_contact> contactsByPhonesShortDictFinal2 = contactsByPhonesShortDict2;
                                a = 0;
                                while (a < contactsArr.size()) {
                                    contact = (TL_contact) contactsArr.get(a);
                                    User user = (User) usersDict.get(contact.user_id);
                                    if (user == null) {
                                        contactsByPhonesShortDictFinal = contactsByPhonesShortDictFinal2;
                                    } else {
                                        String key;
                                        ArrayList<TL_contact> arr;
                                        contactsDictionary.put(Integer.valueOf(contact.user_id), contact);
                                        if (!(contactsByPhonesDict2 == null || TextUtils.isEmpty(user.phone))) {
                                            contactsByPhonesDict2.put(user.phone, contact);
                                            contactsByPhonesShortDict2.put(user.phone.substring(Math.max(i, user.phone.length() - 7)), contact);
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
                                        key2 = (String) ContactsController.this.sectionsToReplace.get(key);
                                        if (key2 != null) {
                                            key = key2;
                                        }
                                        ArrayList<TL_contact> arr2 = (ArrayList) sectionsDict.get(key);
                                        if (arr2 == null) {
                                            arr = new ArrayList();
                                            sectionsDict.put(key, arr);
                                            sortedSectionsArray.add(key);
                                        } else {
                                            arr = arr2;
                                        }
                                        arr.add(contact);
                                        contactsByPhonesShortDictFinal = contactsByPhonesShortDictFinal2;
                                        if (user.mutual_contact != null) {
                                            contactsByPhonesShortDictFinal2 = (ArrayList) sectionsDictMutual.get(key);
                                            if (contactsByPhonesShortDictFinal2 == null) {
                                                contactsByPhonesShortDictFinal2 = new ArrayList();
                                                sectionsDictMutual.put(key, contactsByPhonesShortDictFinal2);
                                                sortedSectionsArrayMutual.add(key);
                                            }
                                            contactsByPhonesShortDictFinal2.add(contact);
                                        }
                                    }
                                    a++;
                                    contactsByPhonesShortDictFinal2 = contactsByPhonesShortDictFinal;
                                    i = 0;
                                }
                                contactsByPhonesShortDictFinal = contactsByPhonesShortDictFinal2;
                                Collections.sort(sortedSectionsArray, new C00712());
                                Collections.sort(sortedSectionsArrayMutual, new C00723());
                                final ConcurrentHashMap<Integer, TL_contact> concurrentHashMap = contactsDictionary;
                                final HashMap<String, ArrayList<TL_contact>> hashMap = sectionsDict;
                                final HashMap<String, TL_contact> contactsByPhonesShortDictFinal3 = contactsByPhonesShortDictFinal;
                                final HashMap<String, ArrayList<TL_contact>> hashMap2 = sectionsDictMutual;
                                final HashMap<String, TL_contact> contactsByPhonesDictFinal2 = contactsByPhonesDictFinal;
                                final ArrayList<String> arrayList = sortedSectionsArray;
                                final ArrayList<String> contactsByPhonesShortDict3 = sortedSectionsArrayMutual;
                                AndroidUtilities.runOnUIThread(new Runnable() {
                                    public void run() {
                                        ContactsController.this.contacts = contactsArr;
                                        ContactsController.this.contactsDict = concurrentHashMap;
                                        ContactsController.this.usersSectionsDict = hashMap;
                                        ContactsController.this.usersMutualSectionsDict = hashMap2;
                                        ContactsController.this.sortedUsersSectionsArray = arrayList;
                                        ContactsController.this.sortedUsersMutualSectionsArray = contactsByPhonesShortDict3;
                                        if (from != 2) {
                                            synchronized (ContactsController.this.loadContactsSync) {
                                                ContactsController.this.loadingContacts = false;
                                            }
                                        }
                                        ContactsController.this.performWriteContactsToPhoneBook();
                                        ContactsController.this.updateUnregisteredContacts(contactsArr);
                                        NotificationCenter.getInstance(ContactsController.this.currentAccount).postNotificationName(NotificationCenter.contactsDidLoaded, new Object[0]);
                                        if (from == 1 || isEmpty) {
                                            ContactsController.this.reloadContactsStatusesMaybe();
                                        } else {
                                            ContactsController.this.saveContactsLoadTime();
                                        }
                                    }
                                });
                                if (!ContactsController.this.delayedContactsUpdate.isEmpty() && ContactsController.this.contactsLoaded && ContactsController.this.contactsBookLoaded) {
                                    ContactsController.this.applyContactsUpdates(ContactsController.this.delayedContactsUpdate, null, null, null);
                                    ContactsController.this.delayedContactsUpdate.clear();
                                }
                                if (contactsByPhonesDictFinal2 != null) {
                                    AndroidUtilities.runOnUIThread(new Runnable() {

                                        /* renamed from: org.telegram.messenger.ContactsController$12$1$5$1 */
                                        class C00741 implements Runnable {
                                            C00741() {
                                            }

                                            public void run() {
                                                ContactsController.this.contactsByPhone = contactsByPhonesDictFinal2;
                                                ContactsController.this.contactsByShortPhone = contactsByPhonesShortDictFinal3;
                                            }
                                        }

                                        public void run() {
                                            Utilities.globalQueue.postRunnable(new C00741());
                                            if (!ContactsController.this.contactsSyncInProgress) {
                                                ContactsController.this.contactsSyncInProgress = true;
                                                MessagesStorage.getInstance(ContactsController.this.currentAccount).getCachedPhoneBook(false);
                                            }
                                        }
                                    });
                                } else {
                                    ContactsController.this.contactsLoaded = true;
                                }
                            }
                        });
                        return;
                    }
                }
            }
        });
    }

    private void reloadContactsStatusesMaybe() {
        try {
            if (MessagesController.getMainSettings(this.currentAccount).getLong("lastReloadStatusTime", 0) < System.currentTimeMillis() - 86400000) {
                reloadContactsStatuses();
            }
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
    }

    private void saveContactsLoadTime() {
        try {
            MessagesController.getMainSettings(this.currentAccount).edit().putLong("lastReloadStatusTime", System.currentTimeMillis()).commit();
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
    }

    private void updateUnregisteredContacts(ArrayList<TL_contact> contactsArr) {
        HashMap<String, TL_contact> contactsPhonesShort = new HashMap();
        for (int a = 0; a < contactsArr.size(); a++) {
            TL_contact value = (TL_contact) contactsArr.get(a);
            User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(value.user_id));
            if (user != null) {
                if (!TextUtils.isEmpty(user.phone)) {
                    contactsPhonesShort.put(user.phone, value);
                }
            }
        }
        ArrayList<Contact> sortedPhoneBookContacts = new ArrayList();
        for (Entry<String, Contact> pair : this.contactsBook.entrySet()) {
            Contact value2 = (Contact) pair.getValue();
            boolean skip = false;
            int a2 = 0;
            while (a2 < value2.phones.size()) {
                if (!contactsPhonesShort.containsKey((String) value2.shortPhones.get(a2))) {
                    if (((Integer) value2.phoneDeleted.get(a2)).intValue() != 1) {
                        a2++;
                    }
                }
                skip = true;
            }
            if (!skip) {
                sortedPhoneBookContacts.add(value2);
            }
        }
        Collections.sort(sortedPhoneBookContacts, new Comparator<Contact>() {
            public int compare(Contact contact, Contact contact2) {
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
        });
        this.phoneBookContacts = sortedPhoneBookContacts;
    }

    private void buildContactsSectionsArrays(boolean sort) {
        if (sort) {
            Collections.sort(this.contacts, new Comparator<TL_contact>() {
                public int compare(TL_contact tl_contact, TL_contact tl_contact2) {
                    return UserObject.getFirstName(MessagesController.getInstance(ContactsController.this.currentAccount).getUser(Integer.valueOf(tl_contact.user_id))).compareTo(UserObject.getFirstName(MessagesController.getInstance(ContactsController.this.currentAccount).getUser(Integer.valueOf(tl_contact2.user_id))));
                }
            });
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
        Collections.sort(sortedSectionsArray, new Comparator<String>() {
            public int compare(String s, String s2) {
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
        });
        this.usersSectionsDict = sectionsDict;
        this.sortedUsersSectionsArray = sortedSectionsArray;
    }

    private boolean hasContactsPermission() {
        boolean z = false;
        if (VERSION.SDK_INT >= 23) {
            if (ApplicationLoader.applicationContext.checkSelfPermission("android.permission.READ_CONTACTS") == 0) {
                z = true;
            }
            return z;
        }
        Cursor cursor = null;
        try {
            cursor = ApplicationLoader.applicationContext.getContentResolver().query(Phone.CONTENT_URI, this.projectionPhones, null, null, null);
            if (cursor != null) {
                if (cursor.getCount() != 0) {
                    if (cursor != null) {
                        try {
                            cursor.close();
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                        }
                    }
                    return true;
                }
            }
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Throwable e2) {
                    FileLog.m3e(e2);
                }
            }
            return false;
        } catch (Throwable th) {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Throwable e22) {
                    FileLog.m3e(e22);
                }
            }
        }
    }

    private void performWriteContactsToPhoneBookInternal(ArrayList<TL_contact> contactsArray) {
        try {
            if (hasContactsPermission()) {
                Uri rawContactUri = RawContacts.CONTENT_URI.buildUpon().appendQueryParameter("account_name", this.systemAccount.name).appendQueryParameter("account_type", this.systemAccount.type).build();
                Cursor c1 = ApplicationLoader.applicationContext.getContentResolver().query(rawContactUri, new String[]{"_id", "sync2"}, null, null, null);
                SparseLongArray bookContacts = new SparseLongArray();
                if (c1 != null) {
                    while (c1.moveToNext()) {
                        bookContacts.put(c1.getInt(1), c1.getLong(0));
                    }
                    c1.close();
                    for (int a = 0; a < contactsArray.size(); a++) {
                        TL_contact u = (TL_contact) contactsArray.get(a);
                        if (bookContacts.indexOfKey(u.user_id) < 0) {
                            addContactToPhoneBook(MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(u.user_id)), false);
                        }
                    }
                }
            }
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
    }

    private void performWriteContactsToPhoneBook() {
        final ArrayList<TL_contact> contactsArray = new ArrayList();
        contactsArray.addAll(this.contacts);
        Utilities.phoneBookQueue.postRunnable(new Runnable() {
            public void run() {
                ContactsController.this.performWriteContactsToPhoneBookInternal(contactsArray);
            }
        });
    }

    private void applyContactsUpdates(ArrayList<Integer> ids, ConcurrentHashMap<Integer, User> userDict, ArrayList<TL_contact> newC, ArrayList<Integer> contactsTD) {
        ArrayList<Integer> arrayList;
        ArrayList<TL_contact> newC2;
        ArrayList<Integer> contactsTD2;
        StringBuilder stringBuilder;
        StringBuilder toDelete;
        boolean reloadContacts;
        int a;
        int i;
        TL_contact newContact;
        User user;
        Contact contact;
        int index;
        final Integer uid;
        User user2;
        Contact contact2;
        ContactsController contactsController = this;
        ConcurrentHashMap<Integer, User> concurrentHashMap = userDict;
        int a2 = 0;
        if (newC != null) {
            if (contactsTD != null) {
                arrayList = ids;
                newC2 = newC;
                contactsTD2 = contactsTD;
                if (BuildVars.LOGS_ENABLED) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("process update - contacts add = ");
                    stringBuilder.append(newC2.size());
                    stringBuilder.append(" delete = ");
                    stringBuilder.append(contactsTD2.size());
                    FileLog.m0d(stringBuilder.toString());
                }
                stringBuilder = new StringBuilder();
                toDelete = new StringBuilder();
                reloadContacts = false;
                a = 0;
                while (true) {
                    i = -1;
                    if (a < newC2.size()) {
                        break;
                    }
                    newContact = (TL_contact) newC2.get(a);
                    user = null;
                    if (concurrentHashMap != null) {
                        user = (User) concurrentHashMap.get(Integer.valueOf(newContact.user_id));
                    }
                    if (user != null) {
                        user = MessagesController.getInstance(contactsController.currentAccount).getUser(Integer.valueOf(newContact.user_id));
                    } else {
                        MessagesController.getInstance(contactsController.currentAccount).putUser(user, true);
                    }
                    if (user != null) {
                        if (TextUtils.isEmpty(user.phone)) {
                            contact = (Contact) contactsController.contactsBookSPhones.get(user.phone);
                            if (contact != null) {
                                index = contact.shortPhones.indexOf(user.phone);
                                if (index != -1) {
                                    contact.phoneDeleted.set(index, Integer.valueOf(0));
                                }
                            }
                            if (stringBuilder.length() != 0) {
                                stringBuilder.append(",");
                            }
                            stringBuilder.append(user.phone);
                            a++;
                        }
                    }
                    reloadContacts = true;
                    a++;
                }
                while (a2 < contactsTD2.size()) {
                    uid = (Integer) contactsTD2.get(a2);
                    Utilities.phoneBookQueue.postRunnable(new Runnable() {
                        public void run() {
                            ContactsController.this.deleteContactFromPhoneBook(uid.intValue());
                        }
                    });
                    user2 = null;
                    if (concurrentHashMap != null) {
                        user2 = (User) concurrentHashMap.get(uid);
                    }
                    if (user2 != null) {
                        user2 = MessagesController.getInstance(contactsController.currentAccount).getUser(uid);
                    } else {
                        MessagesController.getInstance(contactsController.currentAccount).putUser(user2, true);
                    }
                    if (user2 == null) {
                        reloadContacts = true;
                    } else if (!TextUtils.isEmpty(user2.phone)) {
                        contact2 = (Contact) contactsController.contactsBookSPhones.get(user2.phone);
                        if (contact2 != null) {
                            index = contact2.shortPhones.indexOf(user2.phone);
                            if (index != i) {
                                contact2.phoneDeleted.set(index, Integer.valueOf(1));
                            }
                        }
                        if (toDelete.length() != 0) {
                            toDelete.append(",");
                        }
                        toDelete.append(user2.phone);
                    }
                    a2++;
                    i = -1;
                }
                if (!(stringBuilder.length() == 0 && toDelete.length() == 0)) {
                    MessagesStorage.getInstance(contactsController.currentAccount).applyPhoneBookUpdates(stringBuilder.toString(), toDelete.toString());
                }
                if (reloadContacts) {
                    final ArrayList<TL_contact> newContacts = newC2;
                    final ArrayList<Integer> contactsToDelete = contactsTD2;
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            int a;
                            for (a = 0; a < newContacts.size(); a++) {
                                TL_contact contact = (TL_contact) newContacts.get(a);
                                if (ContactsController.this.contactsDict.get(Integer.valueOf(contact.user_id)) == null) {
                                    ContactsController.this.contacts.add(contact);
                                    ContactsController.this.contactsDict.put(Integer.valueOf(contact.user_id), contact);
                                }
                            }
                            for (a = 0; a < contactsToDelete.size(); a++) {
                                Integer uid = (Integer) contactsToDelete.get(a);
                                TL_contact contact2 = (TL_contact) ContactsController.this.contactsDict.get(uid);
                                if (contact2 != null) {
                                    ContactsController.this.contacts.remove(contact2);
                                    ContactsController.this.contactsDict.remove(uid);
                                }
                            }
                            if (!newContacts.isEmpty()) {
                                ContactsController.this.updateUnregisteredContacts(ContactsController.this.contacts);
                                ContactsController.this.performWriteContactsToPhoneBook();
                            }
                            ContactsController.this.performSyncPhoneBook(ContactsController.this.getContactsCopy(ContactsController.this.contactsBook), false, false, false, false, true, false);
                            ContactsController.this.buildContactsSectionsArrays(newContacts.isEmpty() ^ 1);
                            NotificationCenter.getInstance(ContactsController.this.currentAccount).postNotificationName(NotificationCenter.contactsDidLoaded, new Object[0]);
                        }
                    });
                    return;
                }
                Utilities.stageQueue.postRunnable(new Runnable() {
                    public void run() {
                        ContactsController.this.loadContacts(false, 0);
                    }
                });
            }
        }
        newC2 = new ArrayList();
        contactsTD2 = new ArrayList();
        for (int a3 = 0; a3 < ids.size(); a3++) {
            Integer uid2 = (Integer) ids.get(a3);
            if (uid2.intValue() > 0) {
                TL_contact contact3 = new TL_contact();
                contact3.user_id = uid2.intValue();
                newC2.add(contact3);
            } else if (uid2.intValue() < 0) {
                contactsTD2.add(Integer.valueOf(-uid2.intValue()));
            }
        }
        arrayList = ids;
        if (BuildVars.LOGS_ENABLED) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("process update - contacts add = ");
            stringBuilder.append(newC2.size());
            stringBuilder.append(" delete = ");
            stringBuilder.append(contactsTD2.size());
            FileLog.m0d(stringBuilder.toString());
        }
        stringBuilder = new StringBuilder();
        toDelete = new StringBuilder();
        reloadContacts = false;
        a = 0;
        while (true) {
            i = -1;
            if (a < newC2.size()) {
                break;
            }
            newContact = (TL_contact) newC2.get(a);
            user = null;
            if (concurrentHashMap != null) {
                user = (User) concurrentHashMap.get(Integer.valueOf(newContact.user_id));
            }
            if (user != null) {
                MessagesController.getInstance(contactsController.currentAccount).putUser(user, true);
            } else {
                user = MessagesController.getInstance(contactsController.currentAccount).getUser(Integer.valueOf(newContact.user_id));
            }
            if (user != null) {
                if (TextUtils.isEmpty(user.phone)) {
                    contact = (Contact) contactsController.contactsBookSPhones.get(user.phone);
                    if (contact != null) {
                        index = contact.shortPhones.indexOf(user.phone);
                        if (index != -1) {
                            contact.phoneDeleted.set(index, Integer.valueOf(0));
                        }
                    }
                    if (stringBuilder.length() != 0) {
                        stringBuilder.append(",");
                    }
                    stringBuilder.append(user.phone);
                    a++;
                }
            }
            reloadContacts = true;
            a++;
        }
        while (a2 < contactsTD2.size()) {
            uid = (Integer) contactsTD2.get(a2);
            Utilities.phoneBookQueue.postRunnable(/* anonymous class already generated */);
            user2 = null;
            if (concurrentHashMap != null) {
                user2 = (User) concurrentHashMap.get(uid);
            }
            if (user2 != null) {
                MessagesController.getInstance(contactsController.currentAccount).putUser(user2, true);
            } else {
                user2 = MessagesController.getInstance(contactsController.currentAccount).getUser(uid);
            }
            if (user2 == null) {
                reloadContacts = true;
            } else if (!TextUtils.isEmpty(user2.phone)) {
                contact2 = (Contact) contactsController.contactsBookSPhones.get(user2.phone);
                if (contact2 != null) {
                    index = contact2.shortPhones.indexOf(user2.phone);
                    if (index != i) {
                        contact2.phoneDeleted.set(index, Integer.valueOf(1));
                    }
                }
                if (toDelete.length() != 0) {
                    toDelete.append(",");
                }
                toDelete.append(user2.phone);
            }
            a2++;
            i = -1;
        }
        MessagesStorage.getInstance(contactsController.currentAccount).applyPhoneBookUpdates(stringBuilder.toString(), toDelete.toString());
        if (reloadContacts) {
            final ArrayList<TL_contact> newContacts2 = newC2;
            final ArrayList<Integer> contactsToDelete2 = contactsTD2;
            AndroidUtilities.runOnUIThread(/* anonymous class already generated */);
            return;
        }
        Utilities.stageQueue.postRunnable(/* anonymous class already generated */);
    }

    public void processContactsUpdates(ArrayList<Integer> ids, ConcurrentHashMap<Integer, User> userDict) {
        ArrayList<TL_contact> newContacts = new ArrayList();
        ArrayList<Integer> contactsToDelete = new ArrayList();
        Iterator it = ids.iterator();
        while (it.hasNext()) {
            Integer uid = (Integer) it.next();
            if (uid.intValue() > 0) {
                TL_contact contact = new TL_contact();
                contact.user_id = uid.intValue();
                newContacts.add(contact);
                if (!this.delayedContactsUpdate.isEmpty()) {
                    int idx = this.delayedContactsUpdate.indexOf(Integer.valueOf(-uid.intValue()));
                    if (idx != -1) {
                        this.delayedContactsUpdate.remove(idx);
                    }
                }
            } else if (uid.intValue() < 0) {
                contactsToDelete.add(Integer.valueOf(-uid.intValue()));
                if (!this.delayedContactsUpdate.isEmpty()) {
                    int idx2 = this.delayedContactsUpdate.indexOf(Integer.valueOf(-uid.intValue()));
                    if (idx2 != -1) {
                        this.delayedContactsUpdate.remove(idx2);
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
        if (this.contactsLoaded) {
            if (this.contactsBookLoaded) {
                applyContactsUpdates(ids, userDict, newContacts, contactsToDelete);
                return;
            }
        }
        this.delayedContactsUpdate.addAll(ids);
        if (BuildVars.LOGS_ENABLED) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("delay update - contacts add = ");
            stringBuilder.append(newContacts.size());
            stringBuilder.append(" delete = ");
            stringBuilder.append(contactsToDelete.size());
            FileLog.m0d(stringBuilder.toString());
        }
    }

    public long addContactToPhoneBook(User user, boolean check) {
        if (!(this.systemAccount == null || user == null)) {
            if (!TextUtils.isEmpty(user.phone)) {
                if (!hasContactsPermission()) {
                    return -1;
                }
                long res = -1;
                synchronized (this.observerLock) {
                    this.ignoreChanges = true;
                }
                ContentResolver contentResolver = ApplicationLoader.applicationContext.getContentResolver();
                if (check) {
                    try {
                        Uri rawContactUri = RawContacts.CONTENT_URI.buildUpon().appendQueryParameter("caller_is_syncadapter", "true").appendQueryParameter("account_name", this.systemAccount.name).appendQueryParameter("account_type", this.systemAccount.type).build();
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("sync2 = ");
                        stringBuilder.append(user.id);
                        contentResolver.delete(rawContactUri, stringBuilder.toString(), null);
                    } catch (Throwable e) {
                        FileLog.m3e(e);
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
                Builder builder2 = ContentProviderOperation.newInsert(Data.CONTENT_URI);
                builder2.withValueBackReference("raw_contact_id", 0);
                builder2.withValue("mimetype", "vnd.android.cursor.item/vnd.org.telegram.messenger.android.profile");
                builder2.withValue("data1", Integer.valueOf(user.id));
                builder2.withValue("data2", "Telegram Profile");
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("+");
                stringBuilder2.append(user.phone);
                builder2.withValue("data3", stringBuilder2.toString());
                builder2.withValue("data4", Integer.valueOf(user.id));
                query.add(builder2.build());
                try {
                    ContentProviderResult[] result = contentResolver.applyBatch("com.android.contacts", query);
                    if (!(result == null || result.length <= 0 || result[0].uri == null)) {
                        res = Long.parseLong(result[0].uri.getLastPathSegment());
                    }
                } catch (Throwable e2) {
                    FileLog.m3e(e2);
                }
                long res2 = res;
                synchronized (this.observerLock) {
                    this.ignoreChanges = false;
                }
                return res2;
            }
        }
        return -1;
    }

    private void deleteContactFromPhoneBook(int uid) {
        if (hasContactsPermission()) {
            synchronized (this.observerLock) {
                this.ignoreChanges = true;
            }
            try {
                ContentResolver contentResolver = ApplicationLoader.applicationContext.getContentResolver();
                Uri rawContactUri = RawContacts.CONTENT_URI.buildUpon().appendQueryParameter("caller_is_syncadapter", "true").appendQueryParameter("account_name", this.systemAccount.name).appendQueryParameter("account_type", this.systemAccount.type).build();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("sync2 = ");
                stringBuilder.append(uid);
                contentResolver.delete(rawContactUri, stringBuilder.toString(), null);
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
            synchronized (this.observerLock) {
                this.ignoreChanges = false;
            }
        }
    }

    protected void markAsContacted(final String contactId) {
        if (contactId != null) {
            Utilities.phoneBookQueue.postRunnable(new Runnable() {
                public void run() {
                    Uri uri = Uri.parse(contactId);
                    ContentValues values = new ContentValues();
                    values.put("last_time_contacted", Long.valueOf(System.currentTimeMillis()));
                    ApplicationLoader.applicationContext.getContentResolver().update(uri, values, null, null);
                }
            });
        }
    }

    public void addContact(User user) {
        if (user != null) {
            if (!TextUtils.isEmpty(user.phone)) {
                TL_contacts_importContacts req = new TL_contacts_importContacts();
                ArrayList<TL_inputPhoneContact> contactsParams = new ArrayList();
                TL_inputPhoneContact c = new TL_inputPhoneContact();
                c.phone = user.phone;
                if (!c.phone.startsWith("+")) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("+");
                    stringBuilder.append(c.phone);
                    c.phone = stringBuilder.toString();
                }
                c.first_name = user.first_name;
                c.last_name = user.last_name;
                c.client_id = 0;
                contactsParams.add(c);
                req.contacts = contactsParams;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                    public void run(TLObject response, TL_error error) {
                        if (error == null) {
                            final TL_contacts_importedContacts res = (TL_contacts_importedContacts) response;
                            MessagesStorage.getInstance(ContactsController.this.currentAccount).putUsersAndChats(res.users, null, true, true);
                            for (int a = 0; a < res.users.size(); a++) {
                                final User u = (User) res.users.get(a);
                                Utilities.phoneBookQueue.postRunnable(new Runnable() {
                                    public void run() {
                                        ContactsController.this.addContactToPhoneBook(u, true);
                                    }
                                });
                                TL_contact newContact = new TL_contact();
                                newContact.user_id = u.id;
                                ArrayList<TL_contact> arrayList = new ArrayList();
                                arrayList.add(newContact);
                                MessagesStorage.getInstance(ContactsController.this.currentAccount).putContacts(arrayList, false);
                                if (!TextUtils.isEmpty(u.phone)) {
                                    CharSequence name = ContactsController.formatName(u.first_name, u.last_name);
                                    MessagesStorage.getInstance(ContactsController.this.currentAccount).applyPhoneBookUpdates(u.phone, TtmlNode.ANONYMOUS_REGION_ID);
                                    Contact contact = (Contact) ContactsController.this.contactsBookSPhones.get(u.phone);
                                    if (contact != null) {
                                        int index = contact.shortPhones.indexOf(u.phone);
                                        if (index != -1) {
                                            contact.phoneDeleted.set(index, Integer.valueOf(0));
                                        }
                                    }
                                }
                            }
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    Iterator it = res.users.iterator();
                                    while (it.hasNext()) {
                                        User u = (User) it.next();
                                        MessagesController.getInstance(ContactsController.this.currentAccount).putUser(u, false);
                                        if (ContactsController.this.contactsDict.get(Integer.valueOf(u.id)) == null) {
                                            TL_contact newContact = new TL_contact();
                                            newContact.user_id = u.id;
                                            ContactsController.this.contacts.add(newContact);
                                            ContactsController.this.contactsDict.put(Integer.valueOf(newContact.user_id), newContact);
                                        }
                                    }
                                    ContactsController.this.buildContactsSectionsArrays(true);
                                    NotificationCenter.getInstance(ContactsController.this.currentAccount).postNotificationName(NotificationCenter.contactsDidLoaded, new Object[0]);
                                }
                            });
                        }
                    }
                }, 6);
            }
        }
    }

    public void deleteContact(final ArrayList<User> users) {
        if (users != null) {
            if (!users.isEmpty()) {
                TL_contacts_deleteContacts req = new TL_contacts_deleteContacts();
                final ArrayList<Integer> uids = new ArrayList();
                Iterator it = users.iterator();
                while (it.hasNext()) {
                    User user = (User) it.next();
                    InputUser inputUser = MessagesController.getInstance(this.currentAccount).getInputUser(user);
                    if (inputUser != null) {
                        uids.add(Integer.valueOf(user.id));
                        req.id.add(inputUser);
                    }
                }
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {

                    /* renamed from: org.telegram.messenger.ContactsController$22$1 */
                    class C00811 implements Runnable {
                        C00811() {
                        }

                        public void run() {
                            Iterator it = users.iterator();
                            while (it.hasNext()) {
                                ContactsController.this.deleteContactFromPhoneBook(((User) it.next()).id);
                            }
                        }
                    }

                    /* renamed from: org.telegram.messenger.ContactsController$22$2 */
                    class C00822 implements Runnable {
                        C00822() {
                        }

                        public void run() {
                            boolean remove = false;
                            Iterator it = users.iterator();
                            while (it.hasNext()) {
                                User user = (User) it.next();
                                TL_contact contact = (TL_contact) ContactsController.this.contactsDict.get(Integer.valueOf(user.id));
                                if (contact != null) {
                                    remove = true;
                                    ContactsController.this.contacts.remove(contact);
                                    ContactsController.this.contactsDict.remove(Integer.valueOf(user.id));
                                }
                            }
                            if (remove) {
                                ContactsController.this.buildContactsSectionsArrays(false);
                            }
                            NotificationCenter.getInstance(ContactsController.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(1));
                            NotificationCenter.getInstance(ContactsController.this.currentAccount).postNotificationName(NotificationCenter.contactsDidLoaded, new Object[0]);
                        }
                    }

                    public void run(TLObject response, TL_error error) {
                        if (error == null) {
                            MessagesStorage.getInstance(ContactsController.this.currentAccount).deleteContacts(uids);
                            Utilities.phoneBookQueue.postRunnable(new C00811());
                            for (int a = 0; a < users.size(); a++) {
                                User user = (User) users.get(a);
                                if (!TextUtils.isEmpty(user.phone)) {
                                    CharSequence name = UserObject.getUserName(user);
                                    MessagesStorage.getInstance(ContactsController.this.currentAccount).applyPhoneBookUpdates(user.phone, TtmlNode.ANONYMOUS_REGION_ID);
                                    Contact contact = (Contact) ContactsController.this.contactsBookSPhones.get(user.phone);
                                    if (contact != null) {
                                        int index = contact.shortPhones.indexOf(user.phone);
                                        if (index != -1) {
                                            contact.phoneDeleted.set(index, Integer.valueOf(1));
                                        }
                                    }
                                }
                            }
                            AndroidUtilities.runOnUIThread(new C00822());
                        }
                    }
                });
            }
        }
    }

    public void reloadContactsStatuses() {
        saveContactsLoadTime();
        MessagesController.getInstance(this.currentAccount).clearFullUsers();
        final Editor editor = MessagesController.getMainSettings(this.currentAccount).edit();
        editor.putBoolean("needGetStatuses", true).commit();
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_contacts_getStatuses(), new RequestDelegate() {
            public void run(final TLObject response, TL_error error) {
                if (error == null) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            editor.remove("needGetStatuses").commit();
                            Vector vector = response;
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
                                        User user = MessagesController.getInstance(ContactsController.this.currentAccount).getUser(Integer.valueOf(status.user_id));
                                        if (user != null) {
                                            user.status = status.status;
                                        }
                                        toDbUser.status = status.status;
                                        dbUsersStatus.add(toDbUser);
                                    }
                                }
                                MessagesStorage.getInstance(ContactsController.this.currentAccount).updateUsers(dbUsersStatus, true, true, true);
                            }
                            NotificationCenter.getInstance(ContactsController.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(4));
                        }
                    });
                }
            }
        });
    }

    public void loadPrivacySettings() {
        if (this.loadingDeleteInfo == 0) {
            this.loadingDeleteInfo = 1;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_account_getAccountTTL(), new RequestDelegate() {
                public void run(final TLObject response, final TL_error error) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            if (error == null) {
                                ContactsController.this.deleteAccountTTL = response.days;
                                ContactsController.this.loadingDeleteInfo = 2;
                            } else {
                                ContactsController.this.loadingDeleteInfo = 0;
                            }
                            NotificationCenter.getInstance(ContactsController.this.currentAccount).postNotificationName(NotificationCenter.privacyRulesUpdated, new Object[0]);
                        }
                    });
                }
            });
        }
        if (this.loadingLastSeenInfo == 0) {
            this.loadingLastSeenInfo = 1;
            TL_account_getPrivacy req = new TL_account_getPrivacy();
            req.key = new TL_inputPrivacyKeyStatusTimestamp();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                public void run(final TLObject response, final TL_error error) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            if (error == null) {
                                TL_account_privacyRules rules = response;
                                MessagesController.getInstance(ContactsController.this.currentAccount).putUsers(rules.users, false);
                                ContactsController.this.privacyRules = rules.rules;
                                ContactsController.this.loadingLastSeenInfo = 2;
                            } else {
                                ContactsController.this.loadingLastSeenInfo = 0;
                            }
                            NotificationCenter.getInstance(ContactsController.this.currentAccount).postNotificationName(NotificationCenter.privacyRulesUpdated, new Object[0]);
                        }
                    });
                }
            });
        }
        if (this.loadingCallsInfo == 0) {
            this.loadingCallsInfo = 1;
            req = new TL_account_getPrivacy();
            req.key = new TL_inputPrivacyKeyPhoneCall();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                public void run(final TLObject response, final TL_error error) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            if (error == null) {
                                TL_account_privacyRules rules = response;
                                MessagesController.getInstance(ContactsController.this.currentAccount).putUsers(rules.users, false);
                                ContactsController.this.callPrivacyRules = rules.rules;
                                ContactsController.this.loadingCallsInfo = 2;
                            } else {
                                ContactsController.this.loadingCallsInfo = 0;
                            }
                            NotificationCenter.getInstance(ContactsController.this.currentAccount).postNotificationName(NotificationCenter.privacyRulesUpdated, new Object[0]);
                        }
                    });
                }
            });
        }
        if (this.loadingGroupInfo == 0) {
            this.loadingGroupInfo = 1;
            req = new TL_account_getPrivacy();
            req.key = new TL_inputPrivacyKeyChatInvite();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                public void run(final TLObject response, final TL_error error) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            if (error == null) {
                                TL_account_privacyRules rules = response;
                                MessagesController.getInstance(ContactsController.this.currentAccount).putUsers(rules.users, false);
                                ContactsController.this.groupPrivacyRules = rules.rules;
                                ContactsController.this.loadingGroupInfo = 2;
                            } else {
                                ContactsController.this.loadingGroupInfo = 0;
                            }
                            NotificationCenter.getInstance(ContactsController.this.currentAccount).postNotificationName(NotificationCenter.privacyRulesUpdated, new Object[0]);
                        }
                    });
                }
            });
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

    public boolean getLoadingLastSeenInfo() {
        return this.loadingLastSeenInfo != 2;
    }

    public boolean getLoadingCallsInfo() {
        return this.loadingCallsInfo != 2;
    }

    public boolean getLoadingGroupInfo() {
        return this.loadingGroupInfo != 2;
    }

    public ArrayList<PrivacyRule> getPrivacyRules(int type) {
        if (type == 2) {
            return this.callPrivacyRules;
        }
        if (type == 1) {
            return this.groupPrivacyRules;
        }
        return this.privacyRules;
    }

    public void setPrivacyRules(ArrayList<PrivacyRule> rules, int type) {
        if (type == 2) {
            this.callPrivacyRules = rules;
        } else if (type == 1) {
            this.groupPrivacyRules = rules;
        } else {
            this.privacyRules = rules;
        }
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.privacyRulesUpdated, new Object[0]);
        reloadContactsStatuses();
    }

    public static String formatName(String firstName, String lastName) {
        if (firstName != null) {
            firstName = firstName.trim();
        }
        if (lastName != null) {
            lastName = lastName.trim();
        }
        int i = 0;
        int length = firstName != null ? firstName.length() : 0;
        if (lastName != null) {
            i = lastName.length();
        }
        StringBuilder result = new StringBuilder((length + i) + 1);
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
