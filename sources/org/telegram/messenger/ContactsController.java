package org.telegram.messenger;

import android.accounts.Account;
import android.content.ContentProviderOperation;
import android.content.ContentProviderOperation.Builder;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
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
import org.telegram.messenger.support.SparseLongArray;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.InputUser;
import org.telegram.tgnet.TLRPC.PrivacyRule;
import org.telegram.tgnet.TLRPC.TL_accountDaysTTL;
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
    class C00811 implements Runnable {
        C00811() {
        }

        public void run() {
            ContactsController.this.migratingContacts = false;
            ContactsController.this.completedRequestsCount = 0;
        }
    }

    /* renamed from: org.telegram.messenger.ContactsController$3 */
    class C00923 implements Runnable {
        C00923() {
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
    class C00934 implements Runnable {
        C00934() {
        }

        public void run() {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.m0d("force import contacts");
            }
            ContactsController.this.performSyncPhoneBook(new HashMap(), true, true, true, true, false, false);
        }
    }

    /* renamed from: org.telegram.messenger.ContactsController$7 */
    class C00957 implements Runnable {
        C00957() {
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
    class C17892 implements RequestDelegate {
        C17892() {
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            if (tLObject != null) {
                final TL_help_inviteText tL_help_inviteText = (TL_help_inviteText) tLObject;
                if (tL_help_inviteText.message.length() != null) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            ContactsController.this.updatingInviteLink = false;
                            Editor edit = MessagesController.getMainSettings(ContactsController.this.currentAccount).edit();
                            edit.putString("invitelink", ContactsController.this.inviteLink = tL_help_inviteText.message);
                            edit.putInt("invitelinktime", (int) (System.currentTimeMillis() / 1000));
                            edit.commit();
                        }
                    });
                }
            }
        }
    }

    /* renamed from: org.telegram.messenger.ContactsController$6 */
    class C17906 implements RequestDelegate {
        public void run(TLObject tLObject, TL_error tL_error) {
        }

        C17906() {
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
        this.currentAccount = i;
        if (MessagesController.getMainSettings(this.currentAccount).getBoolean("needGetStatuses", false) != 0) {
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
        Utilities.globalQueue.postRunnable(new C00811());
        this.privacyRules = null;
    }

    public void checkInviteText() {
        SharedPreferences mainSettings = MessagesController.getMainSettings(this.currentAccount);
        this.inviteLink = mainSettings.getString("invitelink", null);
        int i = mainSettings.getInt("invitelinktime", 0);
        if (!this.updatingInviteLink) {
            if (this.inviteLink == null || Math.abs((System.currentTimeMillis() / 1000) - ((long) i)) >= 86400) {
                this.updatingInviteLink = true;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_help_getInviteText(), new C17892(), 2);
            }
        }
    }

    public java.lang.String getInviteText(int r7) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r6 = this;
        r0 = r6.inviteLink;
        if (r0 != 0) goto L_0x0007;
    L_0x0004:
        r0 = "https://telegram.org/dl";
        goto L_0x0009;
    L_0x0007:
        r0 = r6.inviteLink;
    L_0x0009:
        r1 = NUM; // 0x7f0c0338 float:1.8610863E38 double:1.0530978056E-314;
        r2 = 0;
        r3 = 1;
        if (r7 > r3) goto L_0x001b;
    L_0x0010:
        r7 = "InviteText2";
        r3 = new java.lang.Object[r3];
        r3[r2] = r0;
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r1, r3);
        return r7;
    L_0x001b:
        r4 = "InviteTextNum";	 Catch:{ Exception -> 0x0031 }
        r4 = org.telegram.messenger.LocaleController.getPluralString(r4, r7);	 Catch:{ Exception -> 0x0031 }
        r5 = 2;	 Catch:{ Exception -> 0x0031 }
        r5 = new java.lang.Object[r5];	 Catch:{ Exception -> 0x0031 }
        r7 = java.lang.Integer.valueOf(r7);	 Catch:{ Exception -> 0x0031 }
        r5[r2] = r7;	 Catch:{ Exception -> 0x0031 }
        r5[r3] = r0;	 Catch:{ Exception -> 0x0031 }
        r7 = java.lang.String.format(r4, r5);	 Catch:{ Exception -> 0x0031 }
        return r7;
    L_0x0031:
        r7 = "InviteText2";
        r3 = new java.lang.Object[r3];
        r3[r2] = r0;
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r1, r3);
        return r7;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ContactsController.getInviteText(int):java.lang.String");
    }

    public void checkAppAccount() {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r11 = this;
        r0 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r0 = android.accounts.AccountManager.get(r0);
        r1 = 0;
        r2 = "org.telegram.messenger";	 Catch:{ Throwable -> 0x0055 }
        r2 = r0.getAccountsByType(r2);	 Catch:{ Throwable -> 0x0055 }
        r11.systemAccount = r1;	 Catch:{ Throwable -> 0x0055 }
        r3 = 0;	 Catch:{ Throwable -> 0x0055 }
        r4 = r3;	 Catch:{ Throwable -> 0x0055 }
    L_0x0011:
        r5 = r2.length;	 Catch:{ Throwable -> 0x0055 }
        if (r4 >= r5) goto L_0x0055;	 Catch:{ Throwable -> 0x0055 }
    L_0x0014:
        r5 = r2[r4];	 Catch:{ Throwable -> 0x0055 }
        r6 = r3;	 Catch:{ Throwable -> 0x0055 }
    L_0x0017:
        r7 = 3;	 Catch:{ Throwable -> 0x0055 }
        if (r6 >= r7) goto L_0x004a;	 Catch:{ Throwable -> 0x0055 }
    L_0x001a:
        r7 = org.telegram.messenger.UserConfig.getInstance(r6);	 Catch:{ Throwable -> 0x0055 }
        r7 = r7.getCurrentUser();	 Catch:{ Throwable -> 0x0055 }
        if (r7 == 0) goto L_0x0047;	 Catch:{ Throwable -> 0x0055 }
    L_0x0024:
        r8 = r5.name;	 Catch:{ Throwable -> 0x0055 }
        r9 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x0055 }
        r9.<init>();	 Catch:{ Throwable -> 0x0055 }
        r10 = "";	 Catch:{ Throwable -> 0x0055 }
        r9.append(r10);	 Catch:{ Throwable -> 0x0055 }
        r7 = r7.id;	 Catch:{ Throwable -> 0x0055 }
        r9.append(r7);	 Catch:{ Throwable -> 0x0055 }
        r7 = r9.toString();	 Catch:{ Throwable -> 0x0055 }
        r7 = r8.equals(r7);	 Catch:{ Throwable -> 0x0055 }
        if (r7 == 0) goto L_0x0047;	 Catch:{ Throwable -> 0x0055 }
    L_0x003f:
        r7 = r11.currentAccount;	 Catch:{ Throwable -> 0x0055 }
        if (r6 != r7) goto L_0x0045;	 Catch:{ Throwable -> 0x0055 }
    L_0x0043:
        r11.systemAccount = r5;	 Catch:{ Throwable -> 0x0055 }
    L_0x0045:
        r5 = 1;
        goto L_0x004b;
    L_0x0047:
        r6 = r6 + 1;
        goto L_0x0017;
    L_0x004a:
        r5 = r3;
    L_0x004b:
        if (r5 != 0) goto L_0x0052;
    L_0x004d:
        r5 = r2[r4];	 Catch:{ Exception -> 0x0052 }
        r0.removeAccount(r5, r1, r1);	 Catch:{ Exception -> 0x0052 }
    L_0x0052:
        r4 = r4 + 1;
        goto L_0x0011;
    L_0x0055:
        r2 = r11.currentAccount;
        r2 = org.telegram.messenger.UserConfig.getInstance(r2);
        r2 = r2.isClientActivated();
        if (r2 == 0) goto L_0x0093;
    L_0x0061:
        r11.readContacts();
        r2 = r11.systemAccount;
        if (r2 != 0) goto L_0x0093;
    L_0x0068:
        r2 = new android.accounts.Account;	 Catch:{ Exception -> 0x0093 }
        r3 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0093 }
        r3.<init>();	 Catch:{ Exception -> 0x0093 }
        r4 = "";	 Catch:{ Exception -> 0x0093 }
        r3.append(r4);	 Catch:{ Exception -> 0x0093 }
        r4 = r11.currentAccount;	 Catch:{ Exception -> 0x0093 }
        r4 = org.telegram.messenger.UserConfig.getInstance(r4);	 Catch:{ Exception -> 0x0093 }
        r4 = r4.getClientUserId();	 Catch:{ Exception -> 0x0093 }
        r3.append(r4);	 Catch:{ Exception -> 0x0093 }
        r3 = r3.toString();	 Catch:{ Exception -> 0x0093 }
        r4 = "org.telegram.messenger";	 Catch:{ Exception -> 0x0093 }
        r2.<init>(r3, r4);	 Catch:{ Exception -> 0x0093 }
        r11.systemAccount = r2;	 Catch:{ Exception -> 0x0093 }
        r2 = r11.systemAccount;	 Catch:{ Exception -> 0x0093 }
        r3 = "";	 Catch:{ Exception -> 0x0093 }
        r0.addAccountExplicitly(r2, r3, r1);	 Catch:{ Exception -> 0x0093 }
    L_0x0093:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ContactsController.checkAppAccount():void");
    }

    public void deleteUnknownAppAccounts() {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r11 = this;
        r0 = 0;
        r11.systemAccount = r0;	 Catch:{ Exception -> 0x004f }
        r1 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x004f }
        r1 = android.accounts.AccountManager.get(r1);	 Catch:{ Exception -> 0x004f }
        r2 = "org.telegram.messenger";	 Catch:{ Exception -> 0x004f }
        r2 = r1.getAccountsByType(r2);	 Catch:{ Exception -> 0x004f }
        r3 = 0;	 Catch:{ Exception -> 0x004f }
        r4 = r3;	 Catch:{ Exception -> 0x004f }
    L_0x0011:
        r5 = r2.length;	 Catch:{ Exception -> 0x004f }
        if (r4 >= r5) goto L_0x0053;	 Catch:{ Exception -> 0x004f }
    L_0x0014:
        r5 = r2[r4];	 Catch:{ Exception -> 0x004f }
        r6 = r3;	 Catch:{ Exception -> 0x004f }
    L_0x0017:
        r7 = 3;	 Catch:{ Exception -> 0x004f }
        if (r6 >= r7) goto L_0x0044;	 Catch:{ Exception -> 0x004f }
    L_0x001a:
        r7 = org.telegram.messenger.UserConfig.getInstance(r6);	 Catch:{ Exception -> 0x004f }
        r7 = r7.getCurrentUser();	 Catch:{ Exception -> 0x004f }
        if (r7 == 0) goto L_0x0041;	 Catch:{ Exception -> 0x004f }
    L_0x0024:
        r8 = r5.name;	 Catch:{ Exception -> 0x004f }
        r9 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x004f }
        r9.<init>();	 Catch:{ Exception -> 0x004f }
        r10 = "";	 Catch:{ Exception -> 0x004f }
        r9.append(r10);	 Catch:{ Exception -> 0x004f }
        r7 = r7.id;	 Catch:{ Exception -> 0x004f }
        r9.append(r7);	 Catch:{ Exception -> 0x004f }
        r7 = r9.toString();	 Catch:{ Exception -> 0x004f }
        r7 = r8.equals(r7);	 Catch:{ Exception -> 0x004f }
        if (r7 == 0) goto L_0x0041;
    L_0x003f:
        r5 = 1;
        goto L_0x0045;
    L_0x0041:
        r6 = r6 + 1;
        goto L_0x0017;
    L_0x0044:
        r5 = r3;
    L_0x0045:
        if (r5 != 0) goto L_0x004c;
    L_0x0047:
        r5 = r2[r4];	 Catch:{ Exception -> 0x004c }
        r1.removeAccount(r5, r0, r0);	 Catch:{ Exception -> 0x004c }
    L_0x004c:
        r4 = r4 + 1;
        goto L_0x0011;
    L_0x004f:
        r0 = move-exception;
        r0.printStackTrace();
    L_0x0053:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ContactsController.deleteUnknownAppAccounts():void");
    }

    public void checkContacts() {
        Utilities.globalQueue.postRunnable(new C00923());
    }

    public void forceImportContacts() {
        Utilities.globalQueue.postRunnable(new C00934());
    }

    public void syncPhoneBookByAlert(HashMap<String, Contact> hashMap, boolean z, boolean z2, boolean z3) {
        final HashMap<String, Contact> hashMap2 = hashMap;
        final boolean z4 = z;
        final boolean z5 = z2;
        final boolean z6 = z3;
        Utilities.globalQueue.postRunnable(new Runnable() {
            public void run() {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.m0d("sync contacts by alert");
                }
                ContactsController.this.performSyncPhoneBook(hashMap2, true, z4, z5, false, false, z6);
            }
        });
    }

    public void resetImportedContacts() {
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_contacts_resetSaved(), new C17906());
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean checkContactsInternal() {
        Throwable e;
        Throwable th;
        boolean z = false;
        try {
            if (!hasContactsPermission()) {
                return false;
            }
            ContentResolver contentResolver = ApplicationLoader.applicationContext.getContentResolver();
            Cursor query;
            try {
                query = contentResolver.query(RawContacts.CONTENT_URI, new String[]{"version"}, null, null, null);
                if (query != null) {
                    try {
                        StringBuilder stringBuilder = new StringBuilder();
                        while (query.moveToNext()) {
                            stringBuilder.append(query.getString(query.getColumnIndex("version")));
                        }
                        String stringBuilder2 = stringBuilder.toString();
                        if (!(this.lastContactsVersions.length() == 0 || this.lastContactsVersions.equals(stringBuilder2))) {
                            z = true;
                        }
                        this.lastContactsVersions = stringBuilder2;
                    } catch (Exception e2) {
                        e = e2;
                        try {
                            FileLog.m3e(e);
                            if (query != null) {
                                query.close();
                            }
                            return z;
                        } catch (Throwable th2) {
                            e = th2;
                            if (query != null) {
                                query.close();
                            }
                            throw e;
                        }
                    }
                }
            } catch (Throwable e3) {
                th = e3;
                query = null;
                e = th;
                FileLog.m3e(e);
                if (query != null) {
                    query.close();
                }
                return z;
            } catch (Throwable e32) {
                th = e32;
                query = null;
                e = th;
                if (query != null) {
                    query.close();
                }
                throw e;
            }
        } catch (Throwable e4) {
            FileLog.m3e(e4);
        }
    }

    public void readContacts() {
        synchronized (this.loadContactsSync) {
            if (this.loadingContacts) {
                return;
            }
            this.loadingContacts = true;
            Utilities.stageQueue.postRunnable(new C00957());
        }
    }

    private boolean isNotValidNameString(String str) {
        boolean z = true;
        if (TextUtils.isEmpty(str)) {
            return true;
        }
        int length = str.length();
        int i = 0;
        int i2 = i;
        while (i < length) {
            char charAt = str.charAt(i);
            if (charAt >= '0' && charAt <= '9') {
                i2++;
            }
            i++;
        }
        if (i2 <= 3) {
            z = false;
        }
        return z;
    }

    private java.util.HashMap<java.lang.String, org.telegram.messenger.ContactsController.Contact> readContactsFromPhoneBook() {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r20 = this;
        r1 = r20;
        r2 = r1.currentAccount;
        r2 = org.telegram.messenger.UserConfig.getInstance(r2);
        r2 = r2.syncContacts;
        if (r2 != 0) goto L_0x001b;
    L_0x000c:
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r2 == 0) goto L_0x0015;
    L_0x0010:
        r2 = "contacts sync disabled";
        org.telegram.messenger.FileLog.m0d(r2);
    L_0x0015:
        r2 = new java.util.HashMap;
        r2.<init>();
        return r2;
    L_0x001b:
        r2 = r20.hasContactsPermission();
        if (r2 != 0) goto L_0x0030;
    L_0x0021:
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r2 == 0) goto L_0x002a;
    L_0x0025:
        r2 = "app has no contacts permissions";
        org.telegram.messenger.FileLog.m0d(r2);
    L_0x002a:
        r2 = new java.util.HashMap;
        r2.<init>();
        return r2;
    L_0x0030:
        r3 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x0369, all -> 0x0364 }
        r3.<init>();	 Catch:{ Throwable -> 0x0369, all -> 0x0364 }
        r4 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x0369, all -> 0x0364 }
        r4 = r4.getContentResolver();	 Catch:{ Throwable -> 0x0369, all -> 0x0364 }
        r11 = new java.util.HashMap;	 Catch:{ Throwable -> 0x0369, all -> 0x0364 }
        r11.<init>();	 Catch:{ Throwable -> 0x0369, all -> 0x0364 }
        r12 = new java.util.ArrayList;	 Catch:{ Throwable -> 0x0369, all -> 0x0364 }
        r12.<init>();	 Catch:{ Throwable -> 0x0369, all -> 0x0364 }
        r6 = android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI;	 Catch:{ Throwable -> 0x0369, all -> 0x0364 }
        r7 = r1.projectionPhones;	 Catch:{ Throwable -> 0x0369, all -> 0x0364 }
        r8 = 0;	 Catch:{ Throwable -> 0x0369, all -> 0x0364 }
        r9 = 0;	 Catch:{ Throwable -> 0x0369, all -> 0x0364 }
        r10 = 0;	 Catch:{ Throwable -> 0x0369, all -> 0x0364 }
        r5 = r4;	 Catch:{ Throwable -> 0x0369, all -> 0x0364 }
        r5 = r5.query(r6, r7, r8, r9, r10);	 Catch:{ Throwable -> 0x0369, all -> 0x0364 }
        r15 = 0;
        r10 = 1;
        if (r5 == 0) goto L_0x0221;
    L_0x0055:
        r6 = r5.getCount();	 Catch:{ Throwable -> 0x021a, all -> 0x0215 }
        if (r6 <= 0) goto L_0x01fb;	 Catch:{ Throwable -> 0x021a, all -> 0x0215 }
    L_0x005b:
        r7 = new java.util.HashMap;	 Catch:{ Throwable -> 0x021a, all -> 0x0215 }
        r7.<init>(r6);	 Catch:{ Throwable -> 0x021a, all -> 0x0215 }
        r6 = r10;
    L_0x0061:
        r8 = r5.moveToNext();	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
        if (r8 == 0) goto L_0x01f1;	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
    L_0x0067:
        r8 = r5.getString(r10);	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
        r9 = 5;	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
        r9 = r5.getString(r9);	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
        if (r9 != 0) goto L_0x0074;	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
    L_0x0072:
        r9 = "";	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
    L_0x0074:
        r2 = ".sim";	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
        r2 = r9.indexOf(r2);	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
        if (r2 == 0) goto L_0x007e;	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
    L_0x007c:
        r2 = r10;	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
        goto L_0x007f;	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
    L_0x007e:
        r2 = r15;	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
    L_0x007f:
        r16 = android.text.TextUtils.isEmpty(r8);	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
        if (r16 == 0) goto L_0x0086;	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
    L_0x0085:
        goto L_0x00dc;	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
    L_0x0086:
        r8 = org.telegram.PhoneFormat.PhoneFormat.stripExceptNumbers(r8, r10);	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
        r16 = android.text.TextUtils.isEmpty(r8);	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
        if (r16 == 0) goto L_0x0091;	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
    L_0x0090:
        goto L_0x00dc;	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
    L_0x0091:
        r13 = "+";	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
        r13 = r8.startsWith(r13);	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
        if (r13 == 0) goto L_0x009e;	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
    L_0x0099:
        r13 = r8.substring(r10);	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
        goto L_0x009f;	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
    L_0x009e:
        r13 = r8;	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
    L_0x009f:
        r10 = r5.getString(r15);	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
        r3.setLength(r15);	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
        android.database.DatabaseUtils.appendEscapedSQLString(r3, r10);	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
        r14 = r3.toString();	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
        r16 = r11.get(r13);	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
        r15 = r16;	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
        r15 = (org.telegram.messenger.ContactsController.Contact) r15;	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
        if (r15 == 0) goto L_0x00df;	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
    L_0x00b7:
        r8 = r15.isGoodProvider;	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
        if (r8 != 0) goto L_0x00dc;	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
    L_0x00bb:
        r8 = r15.provider;	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
        if (r8 != 0) goto L_0x00dc;	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
    L_0x00c3:
        r8 = 0;	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
        r3.setLength(r8);	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
        r8 = r15.key;	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
        android.database.DatabaseUtils.appendEscapedSQLString(r3, r8);	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
        r8 = r3.toString();	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
        r12.remove(r8);	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
        r12.add(r14);	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
        r15.key = r10;	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
        r15.isGoodProvider = r2;	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
        r15.provider = r9;	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
    L_0x00dc:
        r10 = 1;	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
    L_0x00dd:
        r15 = 0;	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
        goto L_0x0061;	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
    L_0x00df:
        r15 = r12.contains(r14);	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
        if (r15 != 0) goto L_0x00e8;	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
    L_0x00e5:
        r12.add(r14);	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
    L_0x00e8:
        r14 = 2;	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
        r15 = r5.getInt(r14);	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
        r14 = r7.get(r10);	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
        r14 = (org.telegram.messenger.ContactsController.Contact) r14;	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
        if (r14 != 0) goto L_0x015b;	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
    L_0x00f5:
        r14 = new org.telegram.messenger.ContactsController$Contact;	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
        r14.<init>();	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
        r17 = r3;	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
        r3 = 4;	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
        r3 = r5.getString(r3);	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
        if (r3 != 0) goto L_0x0106;	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
    L_0x0103:
        r3 = "";	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
        goto L_0x010a;	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
    L_0x0106:
        r3 = r3.trim();	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
    L_0x010a:
        r16 = r1.isNotValidNameString(r3);	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
        if (r16 == 0) goto L_0x011b;	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
    L_0x0110:
        r14.first_name = r3;	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
        r3 = "";	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
        r14.last_name = r3;	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
        r18 = r4;	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
        r19 = r12;	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
        goto L_0x014c;	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
    L_0x011b:
        r18 = r4;	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
        r4 = 32;	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
        r4 = r3.lastIndexOf(r4);	 Catch:{ Throwable -> 0x01f7, all -> 0x0215 }
        r1 = -1;
        if (r4 == r1) goto L_0x0144;
    L_0x0126:
        r19 = r12;
        r1 = 0;
        r12 = r3.substring(r1, r4);	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
        r1 = r12.trim();	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
        r14.first_name = r1;	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
        r4 = r4 + 1;	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
        r1 = r3.length();	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
        r1 = r3.substring(r4, r1);	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
        r1 = r1.trim();	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
        r14.last_name = r1;	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
        goto L_0x014c;	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
    L_0x0144:
        r19 = r12;	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
        r14.first_name = r3;	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
        r1 = "";	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
        r14.last_name = r1;	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
    L_0x014c:
        r14.provider = r9;	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
        r14.isGoodProvider = r2;	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
        r14.key = r10;	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
        r1 = r6 + 1;	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
        r14.contact_id = r6;	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
        r7.put(r10, r14);	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
        r6 = r1;	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
        goto L_0x0161;	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
    L_0x015b:
        r17 = r3;	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
        r18 = r4;	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
        r19 = r12;	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
    L_0x0161:
        r1 = r14.shortPhones;	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
        r1.add(r13);	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
        r1 = r14.phones;	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
        r1.add(r8);	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
        r1 = r14.phoneDeleted;	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
        r2 = 0;	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
        r3 = java.lang.Integer.valueOf(r2);	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
        r1.add(r3);	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
        r1 = NUM; // 0x7f0c0508 float:1.8611804E38 double:1.053098035E-314;	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
        if (r15 != 0) goto L_0x018f;	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
    L_0x017a:
        r2 = 3;	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
        r3 = r5.getString(r2);	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
        r2 = r14.phoneTypes;	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
        if (r3 == 0) goto L_0x0184;	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
    L_0x0183:
        goto L_0x018a;	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
    L_0x0184:
        r3 = "PhoneMobile";	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
        r3 = org.telegram.messenger.LocaleController.getString(r3, r1);	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
    L_0x018a:
        r2.add(r3);	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
        r2 = 1;	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
        goto L_0x01e3;	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
    L_0x018f:
        r2 = 1;	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
        if (r15 != r2) goto L_0x01a1;	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
    L_0x0192:
        r1 = r14.phoneTypes;	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
        r3 = "PhoneHome";	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
        r4 = NUM; // 0x7f0c0506 float:1.86118E38 double:1.053098034E-314;	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
        r3 = org.telegram.messenger.LocaleController.getString(r3, r4);	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
        r1.add(r3);	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
        goto L_0x01e3;	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
    L_0x01a1:
        r3 = 2;	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
        if (r15 != r3) goto L_0x01b0;	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
    L_0x01a4:
        r3 = r14.phoneTypes;	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
        r4 = "PhoneMobile";	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
        r1 = org.telegram.messenger.LocaleController.getString(r4, r1);	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
        r3.add(r1);	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
        goto L_0x01e3;	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
    L_0x01b0:
        r1 = 3;	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
        if (r15 != r1) goto L_0x01c2;	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
    L_0x01b3:
        r1 = r14.phoneTypes;	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
        r3 = "PhoneWork";	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
        r4 = NUM; // 0x7f0c050e float:1.8611816E38 double:1.053098038E-314;	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
        r3 = org.telegram.messenger.LocaleController.getString(r3, r4);	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
        r1.add(r3);	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
        goto L_0x01e3;	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
    L_0x01c2:
        r1 = 12;	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
        if (r15 != r1) goto L_0x01d5;	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
    L_0x01c6:
        r1 = r14.phoneTypes;	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
        r3 = "PhoneMain";	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
        r4 = NUM; // 0x7f0c0507 float:1.8611802E38 double:1.0530980343E-314;	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
        r3 = org.telegram.messenger.LocaleController.getString(r3, r4);	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
        r1.add(r3);	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
        goto L_0x01e3;	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
    L_0x01d5:
        r1 = r14.phoneTypes;	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
        r3 = "PhoneOther";	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
        r4 = NUM; // 0x7f0c050d float:1.8611814E38 double:1.0530980373E-314;	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
        r3 = org.telegram.messenger.LocaleController.getString(r3, r4);	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
        r1.add(r3);	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
    L_0x01e3:
        r11.put(r13, r14);	 Catch:{ Throwable -> 0x020b, all -> 0x0205 }
        r10 = r2;
        r3 = r17;
        r4 = r18;
        r12 = r19;
        r1 = r20;
        goto L_0x00dd;
    L_0x01f1:
        r18 = r4;
        r2 = r10;
        r19 = r12;
        goto L_0x0201;
    L_0x01f7:
        r0 = move-exception;
        r11 = r1;
        r2 = r5;
        goto L_0x021e;
    L_0x01fb:
        r18 = r4;
        r2 = r10;
        r19 = r12;
        r7 = 0;
    L_0x0201:
        r5.close();	 Catch:{ Exception -> 0x0212 }
        goto L_0x0212;
    L_0x0205:
        r0 = move-exception;
        r1 = r0;
        r11 = r20;
        goto L_0x038d;
    L_0x020b:
        r0 = move-exception;
        r1 = r0;
        r2 = r5;
        r11 = r20;
        goto L_0x036e;
    L_0x0212:
        r1 = r7;
        r3 = 0;
        goto L_0x0228;
    L_0x0215:
        r0 = move-exception;
        r11 = r1;
        r1 = r0;
        goto L_0x038d;
    L_0x021a:
        r0 = move-exception;
        r11 = r1;
        r2 = r5;
        r7 = 0;
    L_0x021e:
        r1 = r0;
        goto L_0x036e;
    L_0x0221:
        r18 = r4;
        r2 = r10;
        r19 = r12;
        r3 = r5;
        r1 = 0;
    L_0x0228:
        r4 = ",";	 Catch:{ Throwable -> 0x035d, all -> 0x0357 }
        r5 = r19;	 Catch:{ Throwable -> 0x035d, all -> 0x0357 }
        r4 = android.text.TextUtils.join(r4, r5);	 Catch:{ Throwable -> 0x035d, all -> 0x0357 }
        r6 = android.provider.ContactsContract.Data.CONTENT_URI;	 Catch:{ Throwable -> 0x035d, all -> 0x0357 }
        r11 = r20;
        r7 = r11.projectionNames;	 Catch:{ Throwable -> 0x0355, all -> 0x0353 }
        r5 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x0355, all -> 0x0353 }
        r5.<init>();	 Catch:{ Throwable -> 0x0355, all -> 0x0353 }
        r8 = "lookup IN (";	 Catch:{ Throwable -> 0x0355, all -> 0x0353 }
        r5.append(r8);	 Catch:{ Throwable -> 0x0355, all -> 0x0353 }
        r5.append(r4);	 Catch:{ Throwable -> 0x0355, all -> 0x0353 }
        r4 = ") AND ";	 Catch:{ Throwable -> 0x0355, all -> 0x0353 }
        r5.append(r4);	 Catch:{ Throwable -> 0x0355, all -> 0x0353 }
        r4 = "mimetype";	 Catch:{ Throwable -> 0x0355, all -> 0x0353 }
        r5.append(r4);	 Catch:{ Throwable -> 0x0355, all -> 0x0353 }
        r4 = " = '";	 Catch:{ Throwable -> 0x0355, all -> 0x0353 }
        r5.append(r4);	 Catch:{ Throwable -> 0x0355, all -> 0x0353 }
        r4 = "vnd.android.cursor.item/name";	 Catch:{ Throwable -> 0x0355, all -> 0x0353 }
        r5.append(r4);	 Catch:{ Throwable -> 0x0355, all -> 0x0353 }
        r4 = "'";	 Catch:{ Throwable -> 0x0355, all -> 0x0353 }
        r5.append(r4);	 Catch:{ Throwable -> 0x0355, all -> 0x0353 }
        r8 = r5.toString();	 Catch:{ Throwable -> 0x0355, all -> 0x0353 }
        r9 = 0;	 Catch:{ Throwable -> 0x0355, all -> 0x0353 }
        r10 = 0;	 Catch:{ Throwable -> 0x0355, all -> 0x0353 }
        r5 = r18;	 Catch:{ Throwable -> 0x0355, all -> 0x0353 }
        r4 = r5.query(r6, r7, r8, r9, r10);	 Catch:{ Throwable -> 0x0355, all -> 0x0353 }
        if (r4 == 0) goto L_0x0348;
    L_0x026a:
        r3 = r4.moveToNext();	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
        if (r3 == 0) goto L_0x033a;	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
    L_0x0270:
        r3 = 0;	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
        r5 = r4.getString(r3);	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
        r6 = r4.getString(r2);	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
        r7 = 2;	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
        r8 = r4.getString(r7);	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
        r9 = 3;	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
        r10 = r4.getString(r9);	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
        r5 = r1.get(r5);	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
        r5 = (org.telegram.messenger.ContactsController.Contact) r5;	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
        if (r5 == 0) goto L_0x026a;	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
    L_0x028b:
        r12 = r5.namesFilled;	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
        if (r12 != 0) goto L_0x026a;	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
    L_0x028f:
        r12 = r5.isGoodProvider;	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
        if (r12 == 0) goto L_0x02cf;	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
    L_0x0293:
        if (r6 == 0) goto L_0x0298;	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
    L_0x0295:
        r5.first_name = r6;	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
        goto L_0x029c;	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
    L_0x0298:
        r6 = "";	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
        r5.first_name = r6;	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
    L_0x029c:
        if (r8 == 0) goto L_0x02a1;	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
    L_0x029e:
        r5.last_name = r8;	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
        goto L_0x02a5;	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
    L_0x02a1:
        r6 = "";	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
        r5.last_name = r6;	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
    L_0x02a5:
        r6 = android.text.TextUtils.isEmpty(r10);	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
        if (r6 != 0) goto L_0x0336;	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
    L_0x02ab:
        r6 = r5.first_name;	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
        r6 = android.text.TextUtils.isEmpty(r6);	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
        if (r6 != 0) goto L_0x02cc;	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
    L_0x02b3:
        r6 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
        r6.<init>();	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
        r8 = r5.first_name;	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
        r6.append(r8);	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
        r8 = " ";	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
        r6.append(r8);	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
        r6.append(r10);	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
        r6 = r6.toString();	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
        r5.first_name = r6;	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
        goto L_0x0336;	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
    L_0x02cc:
        r5.first_name = r10;	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
        goto L_0x0336;	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
    L_0x02cf:
        r12 = r11.isNotValidNameString(r6);	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
        if (r12 != 0) goto L_0x02e5;	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
    L_0x02d5:
        r12 = r5.first_name;	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
        r12 = r12.contains(r6);	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
        if (r12 != 0) goto L_0x02fb;	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
    L_0x02dd:
        r12 = r5.first_name;	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
        r12 = r6.contains(r12);	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
        if (r12 != 0) goto L_0x02fb;	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
    L_0x02e5:
        r12 = r11.isNotValidNameString(r8);	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
        if (r12 != 0) goto L_0x0336;	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
    L_0x02eb:
        r12 = r5.last_name;	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
        r12 = r12.contains(r8);	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
        if (r12 != 0) goto L_0x02fb;	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
    L_0x02f3:
        r12 = r5.last_name;	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
        r12 = r6.contains(r12);	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
        if (r12 == 0) goto L_0x0336;	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
    L_0x02fb:
        if (r6 == 0) goto L_0x0300;	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
    L_0x02fd:
        r5.first_name = r6;	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
        goto L_0x0304;	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
    L_0x0300:
        r6 = "";	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
        r5.first_name = r6;	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
    L_0x0304:
        r6 = android.text.TextUtils.isEmpty(r10);	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
        if (r6 != 0) goto L_0x032d;	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
    L_0x030a:
        r6 = r5.first_name;	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
        r6 = android.text.TextUtils.isEmpty(r6);	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
        if (r6 != 0) goto L_0x032b;	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
    L_0x0312:
        r6 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
        r6.<init>();	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
        r12 = r5.first_name;	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
        r6.append(r12);	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
        r12 = " ";	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
        r6.append(r12);	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
        r6.append(r10);	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
        r6 = r6.toString();	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
        r5.first_name = r6;	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
        goto L_0x032d;	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
    L_0x032b:
        r5.first_name = r10;	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
    L_0x032d:
        if (r8 == 0) goto L_0x0332;	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
    L_0x032f:
        r5.last_name = r8;	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
        goto L_0x0336;	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
    L_0x0332:
        r6 = "";	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
        r5.last_name = r6;	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
    L_0x0336:
        r5.namesFilled = r2;	 Catch:{ Throwable -> 0x0343, all -> 0x033f }
        goto L_0x026a;
    L_0x033a:
        r4.close();	 Catch:{ Exception -> 0x033d }
    L_0x033d:
        r4 = 0;
        goto L_0x0348;
    L_0x033f:
        r0 = move-exception;
        r1 = r0;
        r5 = r4;
        goto L_0x038d;
    L_0x0343:
        r0 = move-exception;
        r7 = r1;
        r2 = r4;
        goto L_0x021e;
    L_0x0348:
        if (r4 == 0) goto L_0x0381;
    L_0x034a:
        r4.close();	 Catch:{ Exception -> 0x034e }
        goto L_0x0381;
    L_0x034e:
        r0 = move-exception;
        org.telegram.messenger.FileLog.m3e(r0);
        goto L_0x0381;
    L_0x0353:
        r0 = move-exception;
        goto L_0x035a;
    L_0x0355:
        r0 = move-exception;
        goto L_0x0360;
    L_0x0357:
        r0 = move-exception;
        r11 = r20;
    L_0x035a:
        r1 = r0;
        r5 = r3;
        goto L_0x038d;
    L_0x035d:
        r0 = move-exception;
        r11 = r20;
    L_0x0360:
        r7 = r1;
        r2 = r3;
        goto L_0x021e;
    L_0x0364:
        r0 = move-exception;
        r11 = r1;
        r1 = r0;
        r5 = 0;
        goto L_0x038d;
    L_0x0369:
        r0 = move-exception;
        r11 = r1;
        r1 = r0;
        r2 = 0;
        r7 = 0;
    L_0x036e:
        org.telegram.messenger.FileLog.m3e(r1);	 Catch:{ all -> 0x038a }
        if (r7 == 0) goto L_0x0376;	 Catch:{ all -> 0x038a }
    L_0x0373:
        r7.clear();	 Catch:{ all -> 0x038a }
    L_0x0376:
        if (r2 == 0) goto L_0x0380;
    L_0x0378:
        r2.close();	 Catch:{ Exception -> 0x037c }
        goto L_0x0380;
    L_0x037c:
        r0 = move-exception;
        org.telegram.messenger.FileLog.m3e(r0);
    L_0x0380:
        r1 = r7;
    L_0x0381:
        if (r1 == 0) goto L_0x0384;
    L_0x0383:
        goto L_0x0389;
    L_0x0384:
        r1 = new java.util.HashMap;
        r1.<init>();
    L_0x0389:
        return r1;
    L_0x038a:
        r0 = move-exception;
        r1 = r0;
        r5 = r2;
    L_0x038d:
        if (r5 == 0) goto L_0x0397;
    L_0x038f:
        r5.close();	 Catch:{ Exception -> 0x0393 }
        goto L_0x0397;
    L_0x0393:
        r0 = move-exception;
        org.telegram.messenger.FileLog.m3e(r0);
    L_0x0397:
        throw r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ContactsController.readContactsFromPhoneBook():java.util.HashMap<java.lang.String, org.telegram.messenger.ContactsController$Contact>");
    }

    public HashMap<String, Contact> getContactsCopy(HashMap<String, Contact> hashMap) {
        HashMap<String, Contact> hashMap2 = new HashMap();
        hashMap = hashMap.entrySet().iterator();
        while (hashMap.hasNext()) {
            Entry entry = (Entry) hashMap.next();
            Contact contact = new Contact();
            Contact contact2 = (Contact) entry.getValue();
            contact.phoneDeleted.addAll(contact2.phoneDeleted);
            contact.phones.addAll(contact2.phones);
            contact.phoneTypes.addAll(contact2.phoneTypes);
            contact.shortPhones.addAll(contact2.shortPhones);
            contact.first_name = contact2.first_name;
            contact.last_name = contact2.last_name;
            contact.contact_id = contact2.contact_id;
            contact.key = contact2.key;
            hashMap2.put(contact.key, contact);
        }
        return hashMap2;
    }

    protected void migratePhoneBookToV7(final SparseArray<Contact> sparseArray) {
        Utilities.globalQueue.postRunnable(new Runnable() {
            public void run() {
                if (!ContactsController.this.migratingContacts) {
                    Contact contact;
                    ContactsController.this.migratingContacts = true;
                    HashMap hashMap = new HashMap();
                    HashMap access$800 = ContactsController.this.readContactsFromPhoneBook();
                    HashMap hashMap2 = new HashMap();
                    Iterator it = access$800.entrySet().iterator();
                    while (true) {
                        int i = 0;
                        if (!it.hasNext()) {
                            break;
                        }
                        contact = (Contact) ((Entry) it.next()).getValue();
                        while (i < contact.shortPhones.size()) {
                            hashMap2.put(contact.shortPhones.get(i), contact.key);
                            i++;
                        }
                    }
                    for (int i2 = 0; i2 < sparseArray.size(); i2++) {
                        contact = (Contact) sparseArray.valueAt(i2);
                        for (int i3 = 0; i3 < contact.shortPhones.size(); i3++) {
                            String str = (String) hashMap2.get((String) contact.shortPhones.get(i3));
                            if (str != null) {
                                contact.key = str;
                                hashMap.put(str, contact);
                                break;
                            }
                        }
                    }
                    if (BuildVars.LOGS_ENABLED) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("migrated contacts ");
                        stringBuilder.append(hashMap.size());
                        stringBuilder.append(" of ");
                        stringBuilder.append(sparseArray.size());
                        FileLog.m0d(stringBuilder.toString());
                    }
                    MessagesStorage.getInstance(ContactsController.this.currentAccount).putCachedPhoneBook(hashMap, true);
                }
            }
        });
    }

    protected void performSyncPhoneBook(HashMap<String, Contact> hashMap, boolean z, boolean z2, boolean z3, boolean z4, boolean z5, boolean z6) {
        ContactsController contactsController;
        if (z2) {
            contactsController = this;
        } else if (!this.contactsBookLoaded) {
            return;
        }
        final HashMap<String, Contact> hashMap2 = hashMap;
        final boolean z7 = z3;
        final boolean z8 = z;
        final boolean z9 = z2;
        final boolean z10 = z4;
        final boolean z11 = z5;
        final boolean z12 = z6;
        Utilities.globalQueue.postRunnable(new Runnable() {

            /* renamed from: org.telegram.messenger.ContactsController$9$1 */
            class C00971 implements Runnable {
                C00971() {
                }

                public void run() {
                    ArrayList arrayList = new ArrayList();
                    if (!(hashMap2 == null || hashMap2.isEmpty())) {
                        try {
                            HashMap hashMap = new HashMap();
                            for (int i = 0; i < ContactsController.this.contacts.size(); i++) {
                                User user = MessagesController.getInstance(ContactsController.this.currentAccount).getUser(Integer.valueOf(((TL_contact) ContactsController.this.contacts.get(i)).user_id));
                                if (user != null) {
                                    if (!TextUtils.isEmpty(user.phone)) {
                                        hashMap.put(user.phone, user);
                                    }
                                }
                            }
                            for (Entry value : hashMap2.entrySet()) {
                                Contact contact = (Contact) value.getValue();
                                int i2 = 0;
                                int i3 = i2;
                                while (i2 < contact.shortPhones.size()) {
                                    User user2 = (User) hashMap.get((String) contact.shortPhones.get(i2));
                                    if (user2 != null) {
                                        arrayList.add(user2);
                                        contact.shortPhones.remove(i2);
                                        i2--;
                                        i3 = 1;
                                    }
                                    i2++;
                                }
                                if (i3 != 0) {
                                    int size = contact.shortPhones.size();
                                }
                            }
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                        }
                    }
                    if (!arrayList.isEmpty()) {
                        ContactsController.this.deleteContact(arrayList);
                    }
                }
            }

            /* JADX WARNING: inconsistent code. */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void run() {
                C01079 c01079;
                HashMap hashMap;
                int i;
                int i2;
                int i3;
                int i4;
                HashMap hashMap2 = new HashMap();
                Iterator it = hashMap2.entrySet().iterator();
                while (true) {
                    int i5 = 0;
                    if (!it.hasNext()) {
                        break;
                    }
                    Contact contact = (Contact) ((Entry) it.next()).getValue();
                    while (i5 < contact.shortPhones.size()) {
                        hashMap2.put(contact.shortPhones.get(i5), contact);
                        i5++;
                    }
                }
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.m0d("start read contacts from phone");
                }
                if (!z7) {
                    ContactsController.this.checkContactsInternal();
                }
                HashMap access$800 = ContactsController.this.readContactsFromPhoneBook();
                HashMap hashMap3 = new HashMap();
                int size = hashMap2.size();
                ArrayList arrayList = new ArrayList();
                String str;
                if (hashMap2.isEmpty()) {
                    hashMap = hashMap3;
                    if (z8) {
                        i5 = 0;
                        for (Entry entry : access$800.entrySet()) {
                            Contact contact2 = (Contact) entry.getValue();
                            str = (String) entry.getKey();
                            i = 0;
                            while (i < contact2.phones.size()) {
                                if (!z10) {
                                    String str2 = (String) contact2.shortPhones.get(i);
                                    String substring = str2.substring(Math.max(0, str2.length() - 7));
                                    TL_contact tL_contact = (TL_contact) ContactsController.this.contactsByPhone.get(str2);
                                    if (tL_contact != null) {
                                        User user = MessagesController.getInstance(ContactsController.this.currentAccount).getUser(Integer.valueOf(tL_contact.user_id));
                                        if (user != null) {
                                            i5++;
                                            substring = user.first_name != null ? user.first_name : TtmlNode.ANONYMOUS_REGION_ID;
                                            str2 = user.last_name != null ? user.last_name : TtmlNode.ANONYMOUS_REGION_ID;
                                            if (substring.equals(contact2.first_name) && str2.equals(contact2.last_name)) {
                                                i++;
                                                c01079 = this;
                                            } else if (TextUtils.isEmpty(contact2.first_name) && TextUtils.isEmpty(contact2.last_name)) {
                                                i++;
                                                c01079 = this;
                                            }
                                        }
                                    } else if (ContactsController.this.contactsByShortPhone.containsKey(substring)) {
                                        i5++;
                                    }
                                }
                                TL_inputPhoneContact tL_inputPhoneContact = new TL_inputPhoneContact();
                                tL_inputPhoneContact.client_id = (long) contact2.contact_id;
                                tL_inputPhoneContact.client_id |= ((long) i) << 32;
                                tL_inputPhoneContact.first_name = contact2.first_name;
                                tL_inputPhoneContact.last_name = contact2.last_name;
                                tL_inputPhoneContact.phone = (String) contact2.phones.get(i);
                                arrayList.add(tL_inputPhoneContact);
                                i++;
                                c01079 = this;
                            }
                        }
                    } else {
                        i5 = 0;
                    }
                    i2 = 0;
                } else {
                    HashMap hashMap4;
                    Iterator it2 = access$800.entrySet().iterator();
                    i2 = 0;
                    i3 = i2;
                    while (it2.hasNext()) {
                        HashMap hashMap5;
                        Iterator it3;
                        HashMap hashMap6;
                        Entry entry2 = (Entry) it2.next();
                        Object obj = (String) entry2.getKey();
                        Contact contact3 = (Contact) entry2.getValue();
                        Contact contact4 = (Contact) hashMap2.get(obj);
                        if (contact4 == null) {
                            for (int i6 = i5; i6 < contact3.shortPhones.size(); i6++) {
                                Contact contact5 = (Contact) hashMap2.get(contact3.shortPhones.get(i6));
                                if (contact5 != null) {
                                    obj = contact5.key;
                                    contact4 = contact5;
                                    break;
                                }
                            }
                        }
                        if (contact4 != null) {
                            contact3.imported = contact4.imported;
                        }
                        int i7 = (contact4 == null || ((TextUtils.isEmpty(contact3.first_name) || contact4.first_name.equals(contact3.first_name)) && (TextUtils.isEmpty(contact3.last_name) || contact4.last_name.equals(contact3.last_name)))) ? i5 : 1;
                        if (contact4 != null) {
                            if (i7 == 0) {
                                i7 = i5;
                                while (i7 < contact3.phones.size()) {
                                    TL_contact tL_contact2;
                                    User user2;
                                    Object obj2;
                                    String str3;
                                    TL_inputPhoneContact tL_inputPhoneContact2;
                                    int i8;
                                    int i9;
                                    String str4 = (String) contact3.shortPhones.get(i7);
                                    String substring2 = str4.substring(Math.max(i5, str4.length() - 7));
                                    hashMap3.put(str4, contact3);
                                    i5 = contact4.shortPhones.indexOf(str4);
                                    hashMap5 = hashMap2;
                                    if (z8) {
                                        tL_contact2 = (TL_contact) ContactsController.this.contactsByPhone.get(str4);
                                        if (tL_contact2 != null) {
                                            it3 = it2;
                                            user2 = MessagesController.getInstance(ContactsController.this.currentAccount).getUser(Integer.valueOf(tL_contact2.user_id));
                                            if (user2 != null) {
                                                i3++;
                                                if (TextUtils.isEmpty(user2.first_name) && TextUtils.isEmpty(user2.last_name) && !(TextUtils.isEmpty(contact3.first_name) && TextUtils.isEmpty(contact3.last_name))) {
                                                    obj2 = 1;
                                                    i5 = -1;
                                                    if (i5 != -1) {
                                                        if (z8) {
                                                            if (obj2 == null) {
                                                                tL_contact2 = (TL_contact) ContactsController.this.contactsByPhone.get(str4);
                                                                if (tL_contact2 != null) {
                                                                    user2 = MessagesController.getInstance(ContactsController.this.currentAccount).getUser(Integer.valueOf(tL_contact2.user_id));
                                                                    if (user2 == null) {
                                                                        i3++;
                                                                        str = user2.first_name == null ? user2.first_name : TtmlNode.ANONYMOUS_REGION_ID;
                                                                        str3 = user2.last_name == null ? user2.last_name : TtmlNode.ANONYMOUS_REGION_ID;
                                                                        if (str.equals(contact3.first_name)) {
                                                                        }
                                                                        if (TextUtils.isEmpty(contact3.first_name)) {
                                                                        }
                                                                    } else {
                                                                        i2++;
                                                                    }
                                                                } else if (ContactsController.this.contactsByShortPhone.containsKey(substring2)) {
                                                                    i3++;
                                                                }
                                                            }
                                                            tL_inputPhoneContact2 = new TL_inputPhoneContact();
                                                            tL_inputPhoneContact2.client_id = (long) contact3.contact_id;
                                                            i8 = i2;
                                                            i9 = i3;
                                                            hashMap4 = access$800;
                                                            hashMap6 = hashMap3;
                                                            tL_inputPhoneContact2.client_id |= ((long) i7) << 32;
                                                            tL_inputPhoneContact2.first_name = contact3.first_name;
                                                            tL_inputPhoneContact2.last_name = contact3.last_name;
                                                            tL_inputPhoneContact2.phone = (String) contact3.phones.get(i7);
                                                            arrayList.add(tL_inputPhoneContact2);
                                                            i2 = i8;
                                                            i3 = i9;
                                                        }
                                                        hashMap4 = access$800;
                                                        hashMap6 = hashMap3;
                                                    } else {
                                                        hashMap4 = access$800;
                                                        hashMap6 = hashMap3;
                                                        contact3.phoneDeleted.set(i7, contact4.phoneDeleted.get(i5));
                                                        contact4.phones.remove(i5);
                                                        contact4.shortPhones.remove(i5);
                                                        contact4.phoneDeleted.remove(i5);
                                                        contact4.phoneTypes.remove(i5);
                                                    }
                                                    i7++;
                                                    hashMap2 = hashMap5;
                                                    it2 = it3;
                                                    access$800 = hashMap4;
                                                    hashMap3 = hashMap6;
                                                    i5 = 0;
                                                }
                                            }
                                        } else {
                                            it3 = it2;
                                            if (ContactsController.this.contactsByShortPhone.containsKey(substring2)) {
                                                i3++;
                                            }
                                        }
                                    } else {
                                        it3 = it2;
                                    }
                                    obj2 = null;
                                    if (i5 != -1) {
                                        hashMap4 = access$800;
                                        hashMap6 = hashMap3;
                                        contact3.phoneDeleted.set(i7, contact4.phoneDeleted.get(i5));
                                        contact4.phones.remove(i5);
                                        contact4.shortPhones.remove(i5);
                                        contact4.phoneDeleted.remove(i5);
                                        contact4.phoneTypes.remove(i5);
                                    } else {
                                        if (z8) {
                                            if (obj2 == null) {
                                                tL_contact2 = (TL_contact) ContactsController.this.contactsByPhone.get(str4);
                                                if (tL_contact2 != null) {
                                                    user2 = MessagesController.getInstance(ContactsController.this.currentAccount).getUser(Integer.valueOf(tL_contact2.user_id));
                                                    if (user2 == null) {
                                                        i2++;
                                                    } else {
                                                        i3++;
                                                        if (user2.first_name == null) {
                                                        }
                                                        if (user2.last_name == null) {
                                                        }
                                                        if (str.equals(contact3.first_name)) {
                                                        }
                                                        if (TextUtils.isEmpty(contact3.first_name)) {
                                                        }
                                                    }
                                                } else if (ContactsController.this.contactsByShortPhone.containsKey(substring2)) {
                                                    i3++;
                                                }
                                            }
                                            tL_inputPhoneContact2 = new TL_inputPhoneContact();
                                            tL_inputPhoneContact2.client_id = (long) contact3.contact_id;
                                            i8 = i2;
                                            i9 = i3;
                                            hashMap4 = access$800;
                                            hashMap6 = hashMap3;
                                            tL_inputPhoneContact2.client_id |= ((long) i7) << 32;
                                            tL_inputPhoneContact2.first_name = contact3.first_name;
                                            tL_inputPhoneContact2.last_name = contact3.last_name;
                                            tL_inputPhoneContact2.phone = (String) contact3.phones.get(i7);
                                            arrayList.add(tL_inputPhoneContact2);
                                            i2 = i8;
                                            i3 = i9;
                                        }
                                        hashMap4 = access$800;
                                        hashMap6 = hashMap3;
                                    }
                                    i7++;
                                    hashMap2 = hashMap5;
                                    it2 = it3;
                                    access$800 = hashMap4;
                                    hashMap3 = hashMap6;
                                    i5 = 0;
                                }
                                hashMap5 = hashMap2;
                                it3 = it2;
                                hashMap4 = access$800;
                                hashMap6 = hashMap3;
                                if (contact4.phones.isEmpty()) {
                                    hashMap2.remove(obj);
                                }
                                hashMap = hashMap6;
                                hashMap2 = hashMap5;
                                it2 = it3;
                                access$800 = hashMap4;
                                hashMap3 = hashMap;
                                i5 = 0;
                            }
                        }
                        hashMap5 = hashMap2;
                        it3 = it2;
                        hashMap4 = access$800;
                        hashMap6 = hashMap3;
                        i4 = 0;
                        while (i4 < contact3.phones.size()) {
                            int i10;
                            str = (String) contact3.shortPhones.get(i4);
                            str.substring(Math.max(0, str.length() - 7));
                            access$800 = hashMap6;
                            access$800.put(str, contact3);
                            if (contact4 != null) {
                                i5 = contact4.shortPhones.indexOf(str);
                                if (i5 != -1) {
                                    Integer num = (Integer) contact4.phoneDeleted.get(i5);
                                    contact3.phoneDeleted.set(i4, num);
                                    if (num.intValue() == 1) {
                                        i10 = i7;
                                        hashMap = access$800;
                                        i4++;
                                        i7 = i10;
                                        hashMap6 = hashMap;
                                    }
                                }
                            }
                            if (z8) {
                                if (i7 == 0) {
                                    if (ContactsController.this.contactsByPhone.containsKey(str)) {
                                        i3++;
                                    } else {
                                        i2++;
                                    }
                                }
                                TL_inputPhoneContact tL_inputPhoneContact3 = new TL_inputPhoneContact();
                                i10 = i7;
                                tL_inputPhoneContact3.client_id = (long) contact3.contact_id;
                                int i11 = i2;
                                hashMap = access$800;
                                tL_inputPhoneContact3.client_id |= ((long) i4) << 32;
                                tL_inputPhoneContact3.first_name = contact3.first_name;
                                tL_inputPhoneContact3.last_name = contact3.last_name;
                                tL_inputPhoneContact3.phone = (String) contact3.phones.get(i4);
                                arrayList.add(tL_inputPhoneContact3);
                                i2 = i11;
                                i4++;
                                i7 = i10;
                                hashMap6 = hashMap;
                            }
                            i10 = i7;
                            hashMap = access$800;
                            i4++;
                            i7 = i10;
                            hashMap6 = hashMap;
                        }
                        hashMap = hashMap6;
                        if (contact4 != null) {
                            hashMap2.remove(obj);
                        }
                        hashMap2 = hashMap5;
                        it2 = it3;
                        access$800 = hashMap4;
                        hashMap3 = hashMap;
                        i5 = 0;
                    }
                    hashMap4 = access$800;
                    hashMap = hashMap3;
                    if (!z9 && hashMap2.isEmpty() && arrayList.isEmpty()) {
                        access$800 = hashMap4;
                        if (size == access$800.size()) {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.m0d("contacts not changed!");
                            }
                            return;
                        }
                    }
                    access$800 = hashMap4;
                    if (z8 && !hashMap2.isEmpty() && !access$800.isEmpty() && arrayList.isEmpty()) {
                        MessagesStorage.getInstance(ContactsController.this.currentAccount).putCachedPhoneBook(access$800, false);
                    }
                    i5 = i3;
                }
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.m0d("done processing contacts");
                }
                if (!z8) {
                    hashMap3 = hashMap;
                    Utilities.stageQueue.postRunnable(new Runnable() {
                        public void run() {
                            ContactsController.this.contactsBookSPhones = hashMap3;
                            ContactsController.this.contactsBook = access$800;
                            ContactsController.this.contactsSyncInProgress = false;
                            ContactsController.this.contactsBookLoaded = true;
                            if (z9) {
                                ContactsController.this.contactsLoaded = true;
                            }
                            if (!ContactsController.this.delayedContactsUpdate.isEmpty() && ContactsController.this.contactsLoaded && ContactsController.this.contactsBookLoaded) {
                                ContactsController.this.applyContactsUpdates(ContactsController.this.delayedContactsUpdate, null, null, null);
                                ContactsController.this.delayedContactsUpdate.clear();
                            }
                        }
                    });
                    if (!access$800.isEmpty()) {
                        MessagesStorage.getInstance(ContactsController.this.currentAccount).putCachedPhoneBook(access$800, false);
                    }
                } else if (arrayList.isEmpty()) {
                    hashMap3 = hashMap;
                    Utilities.stageQueue.postRunnable(new Runnable() {

                        /* renamed from: org.telegram.messenger.ContactsController$9$5$1 */
                        class C01041 implements Runnable {
                            C01041() {
                            }

                            public void run() {
                                ContactsController.this.updateUnregisteredContacts(ContactsController.this.contacts);
                                NotificationCenter.getInstance(ContactsController.this.currentAccount).postNotificationName(NotificationCenter.contactsDidLoaded, new Object[0]);
                                NotificationCenter.getInstance(ContactsController.this.currentAccount).postNotificationName(NotificationCenter.contactsImported, new Object[0]);
                            }
                        }

                        public void run() {
                            ContactsController.this.contactsBookSPhones = hashMap3;
                            ContactsController.this.contactsBook = access$800;
                            ContactsController.this.contactsSyncInProgress = false;
                            ContactsController.this.contactsBookLoaded = true;
                            if (z9) {
                                ContactsController.this.contactsLoaded = true;
                            }
                            if (!ContactsController.this.delayedContactsUpdate.isEmpty() && ContactsController.this.contactsLoaded) {
                                ContactsController.this.applyContactsUpdates(ContactsController.this.delayedContactsUpdate, null, null, null);
                                ContactsController.this.delayedContactsUpdate.clear();
                            }
                            AndroidUtilities.runOnUIThread(new C01041());
                        }
                    });
                } else {
                    StringBuilder stringBuilder;
                    boolean[] zArr;
                    HashMap hashMap7;
                    SparseArray sparseArray;
                    Contact contact6;
                    int ceil;
                    final TLObject tL_contacts_importContacts;
                    final HashMap hashMap8;
                    final SparseArray sparseArray2;
                    ArrayList arrayList2;
                    C17914 c17914;
                    C17914 c179142;
                    final boolean[] zArr2;
                    boolean[] zArr3;
                    ConnectionsManager instance;
                    final HashMap hashMap9;
                    TLObject tLObject;
                    int i12;
                    int i13;
                    final HashMap hashMap10;
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.m1e("start import contacts");
                    }
                    i = 2;
                    if (z11 && i2 != 0) {
                        if (i2 >= 30) {
                            i = 1;
                        } else if (z9 && size == 0 && ContactsController.this.contactsByPhone.size() - i5 > (ContactsController.this.contactsByPhone.size() / 3) * 2) {
                        }
                        if (BuildVars.LOGS_ENABLED) {
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("new phone book contacts ");
                            stringBuilder.append(i2);
                            stringBuilder.append(" serverContactsInPhonebook ");
                            stringBuilder.append(i5);
                            stringBuilder.append(" totalContacts ");
                            stringBuilder.append(ContactsController.this.contactsByPhone.size());
                            FileLog.m0d(stringBuilder.toString());
                        }
                        if (i != 0) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    NotificationCenter.getInstance(ContactsController.this.currentAccount).postNotificationName(NotificationCenter.hasNewContactsToImport, Integer.valueOf(i), hashMap2, Boolean.valueOf(z9), Boolean.valueOf(z7));
                                }
                            });
                        } else if (z12) {
                            hashMap3 = hashMap;
                            zArr = new boolean[]{false};
                            hashMap7 = new HashMap(access$800);
                            sparseArray = new SparseArray();
                            for (Entry value : hashMap7.entrySet()) {
                                contact6 = (Contact) value.getValue();
                                sparseArray.put(contact6.contact_id, contact6.key);
                            }
                            ContactsController.this.completedRequestsCount = 0;
                            ceil = (int) Math.ceil(((double) arrayList.size()) / 500.0d);
                            i3 = 0;
                            while (i3 < ceil) {
                                tL_contacts_importContacts = new TL_contacts_importContacts();
                                i4 = i3 * 500;
                                tL_contacts_importContacts.contacts = new ArrayList(arrayList.subList(i4, Math.min(i4 + 500, arrayList.size())));
                                hashMap8 = hashMap7;
                                sparseArray2 = sparseArray;
                                arrayList2 = arrayList;
                                c17914 = c179142;
                                zArr2 = zArr;
                                zArr3 = zArr;
                                instance = ConnectionsManager.getInstance(ContactsController.this.currentAccount);
                                hashMap9 = access$800;
                                tLObject = tL_contacts_importContacts;
                                i12 = i3;
                                i3 = ceil;
                                i13 = ceil;
                                hashMap10 = hashMap3;
                                c179142 = new RequestDelegate() {

                                    /* renamed from: org.telegram.messenger.ContactsController$9$4$1 */
                                    class C01031 implements Runnable {

                                        /* renamed from: org.telegram.messenger.ContactsController$9$4$1$1 */
                                        class C01011 implements Runnable {
                                            C01011() {
                                            }

                                            public void run() {
                                                NotificationCenter.getInstance(ContactsController.this.currentAccount).postNotificationName(NotificationCenter.contactsImported, new Object[0]);
                                            }
                                        }

                                        /* renamed from: org.telegram.messenger.ContactsController$9$4$1$2 */
                                        class C01022 implements Runnable {
                                            C01022() {
                                            }

                                            public void run() {
                                                MessagesStorage.getInstance(ContactsController.this.currentAccount).getCachedPhoneBook(true);
                                            }
                                        }

                                        C01031() {
                                        }

                                        public void run() {
                                            ContactsController.this.contactsBookSPhones = hashMap10;
                                            ContactsController.this.contactsBook = hashMap9;
                                            ContactsController.this.contactsSyncInProgress = false;
                                            ContactsController.this.contactsBookLoaded = true;
                                            if (z9) {
                                                ContactsController.this.contactsLoaded = true;
                                            }
                                            if (!ContactsController.this.delayedContactsUpdate.isEmpty() && ContactsController.this.contactsLoaded) {
                                                ContactsController.this.applyContactsUpdates(ContactsController.this.delayedContactsUpdate, null, null, null);
                                                ContactsController.this.delayedContactsUpdate.clear();
                                            }
                                            AndroidUtilities.runOnUIThread(new C01011());
                                            if (zArr2[0]) {
                                                Utilities.globalQueue.postRunnable(new C01022(), 1800000);
                                            }
                                        }
                                    }

                                    public void run(TLObject tLObject, TL_error tL_error) {
                                        ContactsController.this.completedRequestsCount = ContactsController.this.completedRequestsCount + 1;
                                        if (tL_error == null) {
                                            if (BuildVars.LOGS_ENABLED != null) {
                                                FileLog.m0d("contacts imported");
                                            }
                                            TL_contacts_importedContacts tL_contacts_importedContacts = (TL_contacts_importedContacts) tLObject;
                                            if (tL_contacts_importedContacts.retry_contacts.isEmpty() == null) {
                                                for (tL_error = null; tL_error < tL_contacts_importedContacts.retry_contacts.size(); tL_error++) {
                                                    hashMap8.remove(sparseArray2.get((int) ((Long) tL_contacts_importedContacts.retry_contacts.get(tL_error)).longValue()));
                                                }
                                                zArr2[0] = 1;
                                                if (BuildVars.LOGS_ENABLED != null) {
                                                    FileLog.m0d("result has retry contacts");
                                                }
                                            }
                                            for (tL_error = null; tL_error < tL_contacts_importedContacts.popular_invites.size(); tL_error++) {
                                                TL_popularContact tL_popularContact = (TL_popularContact) tL_contacts_importedContacts.popular_invites.get(tL_error);
                                                Contact contact = (Contact) hashMap9.get(sparseArray2.get((int) tL_popularContact.client_id));
                                                if (contact != null) {
                                                    contact.imported = tL_popularContact.importers;
                                                }
                                            }
                                            MessagesStorage.getInstance(ContactsController.this.currentAccount).putUsersAndChats(tL_contacts_importedContacts.users, null, true, true);
                                            tL_error = new ArrayList();
                                            for (int i = 0; i < tL_contacts_importedContacts.imported.size(); i++) {
                                                TL_contact tL_contact = new TL_contact();
                                                tL_contact.user_id = ((TL_importedContact) tL_contacts_importedContacts.imported.get(i)).user_id;
                                                tL_error.add(tL_contact);
                                            }
                                            ContactsController.this.processLoadedContacts(tL_error, tL_contacts_importedContacts.users, 2);
                                        } else {
                                            for (tLObject = null; tLObject < tL_contacts_importContacts.contacts.size(); tLObject++) {
                                                hashMap8.remove(sparseArray2.get((int) ((TL_inputPhoneContact) tL_contacts_importContacts.contacts.get(tLObject)).client_id));
                                            }
                                            zArr2[0] = 1;
                                            if (BuildVars.LOGS_ENABLED != null) {
                                                tLObject = new StringBuilder();
                                                tLObject.append("import contacts error ");
                                                tLObject.append(tL_error.text);
                                                FileLog.m0d(tLObject.toString());
                                            }
                                        }
                                        if (ContactsController.this.completedRequestsCount == i3) {
                                            if (hashMap8.isEmpty() == null) {
                                                MessagesStorage.getInstance(ContactsController.this.currentAccount).putCachedPhoneBook(hashMap8, false);
                                            }
                                            Utilities.stageQueue.postRunnable(new C01031());
                                        }
                                    }
                                };
                                instance.sendRequest(tLObject, c17914, 6);
                                i3 = i12 + 1;
                                ceil = i13;
                                arrayList = arrayList2;
                                zArr = zArr3;
                            }
                        } else {
                            hashMap3 = hashMap;
                            Utilities.stageQueue.postRunnable(new Runnable() {

                                /* renamed from: org.telegram.messenger.ContactsController$9$3$1 */
                                class C00991 implements Runnable {
                                    C00991() {
                                    }

                                    public void run() {
                                        ContactsController.this.updateUnregisteredContacts(ContactsController.this.contacts);
                                        NotificationCenter.getInstance(ContactsController.this.currentAccount).postNotificationName(NotificationCenter.contactsDidLoaded, new Object[0]);
                                        NotificationCenter.getInstance(ContactsController.this.currentAccount).postNotificationName(NotificationCenter.contactsImported, new Object[0]);
                                    }
                                }

                                public void run() {
                                    ContactsController.this.contactsBookSPhones = hashMap3;
                                    ContactsController.this.contactsBook = access$800;
                                    ContactsController.this.contactsSyncInProgress = false;
                                    ContactsController.this.contactsBookLoaded = true;
                                    if (z9) {
                                        ContactsController.this.contactsLoaded = true;
                                    }
                                    if (!ContactsController.this.delayedContactsUpdate.isEmpty() && ContactsController.this.contactsLoaded) {
                                        ContactsController.this.applyContactsUpdates(ContactsController.this.delayedContactsUpdate, null, null, null);
                                        ContactsController.this.delayedContactsUpdate.clear();
                                    }
                                    MessagesStorage.getInstance(ContactsController.this.currentAccount).putCachedPhoneBook(access$800, false);
                                    AndroidUtilities.runOnUIThread(new C00991());
                                }
                            });
                        }
                    }
                    i = 0;
                    if (BuildVars.LOGS_ENABLED) {
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("new phone book contacts ");
                        stringBuilder.append(i2);
                        stringBuilder.append(" serverContactsInPhonebook ");
                        stringBuilder.append(i5);
                        stringBuilder.append(" totalContacts ");
                        stringBuilder.append(ContactsController.this.contactsByPhone.size());
                        FileLog.m0d(stringBuilder.toString());
                    }
                    if (i != 0) {
                        AndroidUtilities.runOnUIThread(/* anonymous class already generated */);
                    } else if (z12) {
                        hashMap3 = hashMap;
                        zArr = new boolean[]{false};
                        hashMap7 = new HashMap(access$800);
                        sparseArray = new SparseArray();
                        while (r0.hasNext()) {
                            contact6 = (Contact) value.getValue();
                            sparseArray.put(contact6.contact_id, contact6.key);
                        }
                        ContactsController.this.completedRequestsCount = 0;
                        ceil = (int) Math.ceil(((double) arrayList.size()) / 500.0d);
                        i3 = 0;
                        while (i3 < ceil) {
                            tL_contacts_importContacts = new TL_contacts_importContacts();
                            i4 = i3 * 500;
                            tL_contacts_importContacts.contacts = new ArrayList(arrayList.subList(i4, Math.min(i4 + 500, arrayList.size())));
                            hashMap8 = hashMap7;
                            sparseArray2 = sparseArray;
                            arrayList2 = arrayList;
                            c17914 = c179142;
                            zArr2 = zArr;
                            zArr3 = zArr;
                            instance = ConnectionsManager.getInstance(ContactsController.this.currentAccount);
                            hashMap9 = access$800;
                            tLObject = tL_contacts_importContacts;
                            i12 = i3;
                            i3 = ceil;
                            i13 = ceil;
                            hashMap10 = hashMap3;
                            c179142 = /* anonymous class already generated */;
                            instance.sendRequest(tLObject, c17914, 6);
                            i3 = i12 + 1;
                            ceil = i13;
                            arrayList = arrayList2;
                            zArr = zArr3;
                        }
                    } else {
                        hashMap3 = hashMap;
                        Utilities.stageQueue.postRunnable(/* anonymous class already generated */);
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

    private int getContactsHash(ArrayList<TL_contact> arrayList) {
        ArrayList arrayList2 = new ArrayList(arrayList);
        Collections.sort(arrayList2, new Comparator<TL_contact>() {
            public int compare(TL_contact tL_contact, TL_contact tL_contact2) {
                if (tL_contact.user_id > tL_contact2.user_id) {
                    return 1;
                }
                return tL_contact.user_id < tL_contact2.user_id ? -1 : null;
            }
        });
        arrayList = arrayList2.size();
        long j = 0;
        for (int i = -1; i < arrayList; i++) {
            long j2;
            if (i == -1) {
                j2 = (((j * 20261) + 2147483648L) + ((long) UserConfig.getInstance(this.currentAccount).contactsSavedCount)) % 2147483648L;
            } else {
                j2 = (((j * 20261) + 2147483648L) + ((long) ((TL_contact) arrayList2.get(i)).user_id)) % 2147483648L;
            }
            j = j2;
        }
        return (int) j;
    }

    public void loadContacts(boolean z, final int i) {
        synchronized (this.loadContactsSync) {
            this.loadingContacts = true;
        }
        if (z) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.m0d("load contacts from cache");
            }
            MessagesStorage.getInstance(this.currentAccount).getContacts();
            return;
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.m0d("load contacts from server");
        }
        z = new TL_contacts_getContacts();
        z.hash = i;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(z, new RequestDelegate() {

            /* renamed from: org.telegram.messenger.ContactsController$11$1 */
            class C00731 implements Runnable {
                C00731() {
                }

                public void run() {
                    synchronized (ContactsController.this.loadContactsSync) {
                        ContactsController.this.loadingContacts = false;
                    }
                    NotificationCenter.getInstance(ContactsController.this.currentAccount).postNotificationName(NotificationCenter.contactsDidLoaded, new Object[0]);
                }
            }

            public void run(TLObject tLObject, TL_error tL_error) {
                if (tL_error == null) {
                    contacts_Contacts contacts_contacts = (contacts_Contacts) tLObject;
                    if (i == null || (contacts_contacts instanceof TL_contacts_contactsNotModified) == null) {
                        UserConfig.getInstance(ContactsController.this.currentAccount).contactsSavedCount = contacts_contacts.saved_count;
                        UserConfig.getInstance(ContactsController.this.currentAccount).saveConfig(false);
                        ContactsController.this.processLoadedContacts(contacts_contacts.contacts, contacts_contacts.users, 0);
                    } else {
                        ContactsController.this.contactsLoaded = true;
                        if (ContactsController.this.delayedContactsUpdate.isEmpty() == null && ContactsController.this.contactsBookLoaded != null) {
                            ContactsController.this.applyContactsUpdates(ContactsController.this.delayedContactsUpdate, null, null, null);
                            ContactsController.this.delayedContactsUpdate.clear();
                        }
                        UserConfig.getInstance(ContactsController.this.currentAccount).lastContactsSyncTime = (int) (System.currentTimeMillis() / 1000);
                        UserConfig.getInstance(ContactsController.this.currentAccount).saveConfig(false);
                        AndroidUtilities.runOnUIThread(new C00731());
                        if (BuildVars.LOGS_ENABLED != null) {
                            FileLog.m0d("load contacts don't change");
                        }
                    }
                }
            }
        });
    }

    public void processLoadedContacts(final ArrayList<TL_contact> arrayList, final ArrayList<User> arrayList2, final int i) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                int i = 0;
                MessagesController.getInstance(ContactsController.this.currentAccount).putUsers(arrayList2, i == 1);
                final SparseArray sparseArray = new SparseArray();
                final boolean isEmpty = arrayList.isEmpty();
                if (!ContactsController.this.contacts.isEmpty()) {
                    int i2 = 0;
                    while (i2 < arrayList.size()) {
                        if (ContactsController.this.contactsDict.get(Integer.valueOf(((TL_contact) arrayList.get(i2)).user_id)) != null) {
                            arrayList.remove(i2);
                            i2--;
                        }
                        i2++;
                    }
                    arrayList.addAll(ContactsController.this.contacts);
                }
                while (i < arrayList.size()) {
                    User user = MessagesController.getInstance(ContactsController.this.currentAccount).getUser(Integer.valueOf(((TL_contact) arrayList.get(i)).user_id));
                    if (user != null) {
                        sparseArray.put(user.id, user);
                    }
                    i++;
                }
                Utilities.stageQueue.postRunnable(new Runnable() {

                    /* renamed from: org.telegram.messenger.ContactsController$12$1$1 */
                    class C00741 implements Comparator<TL_contact> {
                        C00741() {
                        }

                        public int compare(TL_contact tL_contact, TL_contact tL_contact2) {
                            return UserObject.getFirstName((User) sparseArray.get(tL_contact.user_id)).compareTo(UserObject.getFirstName((User) sparseArray.get(tL_contact2.user_id)));
                        }
                    }

                    /* renamed from: org.telegram.messenger.ContactsController$12$1$2 */
                    class C00752 implements Comparator<String> {
                        C00752() {
                        }

                        public int compare(String str, String str2) {
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
                    }

                    /* renamed from: org.telegram.messenger.ContactsController$12$1$3 */
                    class C00763 implements Comparator<String> {
                        C00763() {
                        }

                        public int compare(String str, String str2) {
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
                    }

                    public void run() {
                        HashMap hashMap;
                        HashMap hashMap2;
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.m0d("done loading contacts");
                        }
                        if (i == 1 && (arrayList.isEmpty() || Math.abs((System.currentTimeMillis() / 1000) - ((long) UserConfig.getInstance(ContactsController.this.currentAccount).lastContactsSyncTime)) >= 86400)) {
                            ContactsController.this.loadContacts(false, ContactsController.this.getContactsHash(arrayList));
                            if (arrayList.isEmpty()) {
                                return;
                            }
                        }
                        if (i == 0) {
                            UserConfig.getInstance(ContactsController.this.currentAccount).lastContactsSyncTime = (int) (System.currentTimeMillis() / 1000);
                            UserConfig.getInstance(ContactsController.this.currentAccount).saveConfig(false);
                        }
                        int i = 0;
                        while (i < arrayList.size()) {
                            TL_contact tL_contact = (TL_contact) arrayList.get(i);
                            if (sparseArray.get(tL_contact.user_id) != null || tL_contact.user_id == UserConfig.getInstance(ContactsController.this.currentAccount).getClientUserId()) {
                                i++;
                            } else {
                                ContactsController.this.loadContacts(false, 0);
                                if (BuildVars.LOGS_ENABLED) {
                                    FileLog.m0d("contacts are broken, load from server");
                                }
                                return;
                            }
                        }
                        if (i != 1) {
                            MessagesStorage.getInstance(ContactsController.this.currentAccount).putUsersAndChats(arrayList2, null, true, true);
                            MessagesStorage.getInstance(ContactsController.this.currentAccount).putContacts(arrayList, i != 2);
                        }
                        Collections.sort(arrayList, new C00741());
                        final ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap(20, 1.0f, 2);
                        final HashMap hashMap3 = new HashMap();
                        final HashMap hashMap4 = new HashMap();
                        final Object arrayList = new ArrayList();
                        final Object arrayList2 = new ArrayList();
                        if (ContactsController.this.contactsBookLoaded) {
                            hashMap = null;
                            hashMap2 = hashMap;
                        } else {
                            hashMap = new HashMap();
                            hashMap2 = new HashMap();
                        }
                        for (int i2 = 0; i2 < arrayList.size(); i2++) {
                            TL_contact tL_contact2 = (TL_contact) arrayList.get(i2);
                            User user = (User) sparseArray.get(tL_contact2.user_id);
                            if (user != null) {
                                Object obj;
                                concurrentHashMap.put(Integer.valueOf(tL_contact2.user_id), tL_contact2);
                                if (!(hashMap == null || TextUtils.isEmpty(user.phone))) {
                                    hashMap.put(user.phone, tL_contact2);
                                    hashMap2.put(user.phone.substring(Math.max(0, user.phone.length() - 7)), tL_contact2);
                                }
                                String firstName = UserObject.getFirstName(user);
                                if (firstName.length() > 1) {
                                    firstName = firstName.substring(0, 1);
                                }
                                if (firstName.length() == 0) {
                                    obj = "#";
                                } else {
                                    obj = firstName.toUpperCase();
                                }
                                String str = (String) ContactsController.this.sectionsToReplace.get(obj);
                                if (str != null) {
                                    obj = str;
                                }
                                ArrayList arrayList3 = (ArrayList) hashMap3.get(obj);
                                if (arrayList3 == null) {
                                    arrayList3 = new ArrayList();
                                    hashMap3.put(obj, arrayList3);
                                    arrayList.add(obj);
                                }
                                arrayList3.add(tL_contact2);
                                if (user.mutual_contact) {
                                    ArrayList arrayList4 = (ArrayList) hashMap4.get(obj);
                                    if (arrayList4 == null) {
                                        arrayList4 = new ArrayList();
                                        hashMap4.put(obj, arrayList4);
                                        arrayList2.add(obj);
                                    }
                                    arrayList4.add(tL_contact2);
                                }
                            }
                        }
                        Collections.sort(arrayList, new C00752());
                        Collections.sort(arrayList2, new C00763());
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                ContactsController.this.contacts = arrayList;
                                ContactsController.this.contactsDict = concurrentHashMap;
                                ContactsController.this.usersSectionsDict = hashMap3;
                                ContactsController.this.usersMutualSectionsDict = hashMap4;
                                ContactsController.this.sortedUsersSectionsArray = arrayList;
                                ContactsController.this.sortedUsersMutualSectionsArray = arrayList2;
                                if (i != 2) {
                                    synchronized (ContactsController.this.loadContactsSync) {
                                        ContactsController.this.loadingContacts = false;
                                    }
                                }
                                ContactsController.this.performWriteContactsToPhoneBook();
                                ContactsController.this.updateUnregisteredContacts(arrayList);
                                NotificationCenter.getInstance(ContactsController.this.currentAccount).postNotificationName(NotificationCenter.contactsDidLoaded, new Object[0]);
                                if (i == 1 || isEmpty) {
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
                        if (hashMap != null) {
                            AndroidUtilities.runOnUIThread(new Runnable() {

                                /* renamed from: org.telegram.messenger.ContactsController$12$1$5$1 */
                                class C00781 implements Runnable {
                                    C00781() {
                                    }

                                    public void run() {
                                        ContactsController.this.contactsByPhone = hashMap;
                                        ContactsController.this.contactsByShortPhone = hashMap2;
                                    }
                                }

                                public void run() {
                                    Utilities.globalQueue.postRunnable(new C00781());
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

    private void updateUnregisteredContacts(ArrayList<TL_contact> arrayList) {
        HashMap hashMap = new HashMap();
        for (int i = 0; i < arrayList.size(); i++) {
            TL_contact tL_contact = (TL_contact) arrayList.get(i);
            User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(tL_contact.user_id));
            if (user != null) {
                if (!TextUtils.isEmpty(user.phone)) {
                    hashMap.put(user.phone, tL_contact);
                }
            }
        }
        arrayList = new ArrayList();
        for (Entry value : this.contactsBook.entrySet()) {
            Object obj;
            Contact contact = (Contact) value.getValue();
            int i2 = 0;
            while (true) {
                obj = 1;
                if (i2 >= contact.phones.size()) {
                    break;
                } else if (hashMap.containsKey((String) contact.shortPhones.get(i2))) {
                    break;
                } else if (((Integer) contact.phoneDeleted.get(i2)).intValue() == 1) {
                    break;
                } else {
                    i2++;
                }
            }
            obj = null;
            if (obj == null) {
                arrayList.add(contact);
            }
        }
        Collections.sort(arrayList, new Comparator<Contact>() {
            public int compare(Contact contact, Contact contact2) {
                String str = contact.first_name;
                if (str.length() == 0) {
                    str = contact.last_name;
                }
                contact = contact2.first_name;
                if (contact.length() == 0) {
                    contact = contact2.last_name;
                }
                return str.compareTo(contact);
            }
        });
        this.phoneBookContacts = arrayList;
    }

    private void buildContactsSectionsArrays(boolean z) {
        if (z) {
            Collections.sort(this.contacts, new Comparator<TL_contact>() {
                public int compare(TL_contact tL_contact, TL_contact tL_contact2) {
                    return UserObject.getFirstName(MessagesController.getInstance(ContactsController.this.currentAccount).getUser(Integer.valueOf(tL_contact.user_id))).compareTo(UserObject.getFirstName(MessagesController.getInstance(ContactsController.this.currentAccount).getUser(Integer.valueOf(tL_contact2.user_id))));
                }
            });
        }
        z = new HashMap();
        Object arrayList = new ArrayList();
        for (int i = 0; i < this.contacts.size(); i++) {
            TL_contact tL_contact = (TL_contact) this.contacts.get(i);
            User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(tL_contact.user_id));
            if (user != null) {
                Object obj;
                String firstName = UserObject.getFirstName(user);
                if (firstName.length() > 1) {
                    firstName = firstName.substring(0, 1);
                }
                if (firstName.length() == 0) {
                    obj = "#";
                } else {
                    obj = firstName.toUpperCase();
                }
                String str = (String) this.sectionsToReplace.get(obj);
                if (str != null) {
                    obj = str;
                }
                ArrayList arrayList2 = (ArrayList) z.get(obj);
                if (arrayList2 == null) {
                    arrayList2 = new ArrayList();
                    z.put(obj, arrayList2);
                    arrayList.add(obj);
                }
                arrayList2.add(tL_contact);
            }
        }
        Collections.sort(arrayList, new Comparator<String>() {
            public int compare(String str, String str2) {
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
        });
        this.usersSectionsDict = z;
        this.sortedUsersSectionsArray = arrayList;
    }

    private boolean hasContactsPermission() {
        Throwable th;
        boolean z = false;
        if (VERSION.SDK_INT >= 23) {
            if (ApplicationLoader.applicationContext.checkSelfPermission("android.permission.READ_CONTACTS") == 0) {
                z = true;
            }
            return z;
        }
        Cursor cursor = null;
        try {
            Cursor query = ApplicationLoader.applicationContext.getContentResolver().query(Phone.CONTENT_URI, this.projectionPhones, null, null, null);
            if (query != null) {
                try {
                    if (query.getCount() != 0) {
                        if (query != null) {
                            try {
                                query.close();
                            } catch (Throwable e) {
                                FileLog.m3e(e);
                            }
                        }
                        return true;
                    }
                } catch (Throwable e2) {
                    th = e2;
                    cursor = query;
                    if (cursor != null) {
                        cursor.close();
                    }
                    throw th;
                }
            }
            if (query != null) {
                try {
                    query.close();
                } catch (Throwable e22) {
                    FileLog.m3e(e22);
                }
            }
            return false;
        } catch (Throwable th2) {
            th = th2;
            FileLog.m3e(th);
            if (cursor != null) {
                cursor.close();
            }
            return true;
        }
    }

    private void performWriteContactsToPhoneBookInternal(ArrayList<TL_contact> arrayList) {
        try {
            if (hasContactsPermission()) {
                Uri build = RawContacts.CONTENT_URI.buildUpon().appendQueryParameter("account_name", this.systemAccount.name).appendQueryParameter("account_type", this.systemAccount.type).build();
                Cursor query = ApplicationLoader.applicationContext.getContentResolver().query(build, new String[]{"_id", "sync2"}, null, null, null);
                SparseLongArray sparseLongArray = new SparseLongArray();
                if (query != null) {
                    while (query.moveToNext()) {
                        sparseLongArray.put(query.getInt(1), query.getLong(0));
                    }
                    query.close();
                    for (int i = 0; i < arrayList.size(); i++) {
                        TL_contact tL_contact = (TL_contact) arrayList.get(i);
                        if (sparseLongArray.indexOfKey(tL_contact.user_id) < 0) {
                            addContactToPhoneBook(MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(tL_contact.user_id)), false);
                        }
                    }
                }
            }
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
    }

    private void performWriteContactsToPhoneBook() {
        final ArrayList arrayList = new ArrayList();
        arrayList.addAll(this.contacts);
        Utilities.phoneBookQueue.postRunnable(new Runnable() {
            public void run() {
                ContactsController.this.performWriteContactsToPhoneBookInternal(arrayList);
            }
        });
    }

    private void applyContactsUpdates(ArrayList<Integer> arrayList, ConcurrentHashMap<Integer, User> concurrentHashMap, ArrayList<TL_contact> arrayList2, ArrayList<Integer> arrayList3) {
        Integer num;
        int i = 0;
        if (arrayList2 == null || arrayList3 == null) {
            arrayList2 = new ArrayList();
            arrayList3 = new ArrayList();
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                num = (Integer) arrayList.get(i2);
                if (num.intValue() > 0) {
                    TL_contact tL_contact = new TL_contact();
                    tL_contact.user_id = num.intValue();
                    arrayList2.add(tL_contact);
                } else if (num.intValue() < 0) {
                    arrayList3.add(Integer.valueOf(-num.intValue()));
                }
            }
        }
        if (BuildVars.LOGS_ENABLED != null) {
            arrayList = new StringBuilder();
            arrayList.append("process update - contacts add = ");
            arrayList.append(arrayList2.size());
            arrayList.append(" delete = ");
            arrayList.append(arrayList3.size());
            FileLog.m0d(arrayList.toString());
        }
        arrayList = new StringBuilder();
        StringBuilder stringBuilder = new StringBuilder();
        int i3 = 0;
        int i4 = i3;
        while (true) {
            User user = null;
            if (i3 >= arrayList2.size()) {
                break;
            }
            TL_contact tL_contact2 = (TL_contact) arrayList2.get(i3);
            if (concurrentHashMap != null) {
                user = (User) concurrentHashMap.get(Integer.valueOf(tL_contact2.user_id));
            }
            if (user == null) {
                user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(tL_contact2.user_id));
            } else {
                MessagesController.getInstance(this.currentAccount).putUser(user, true);
            }
            if (user != null) {
                if (!TextUtils.isEmpty(user.phone)) {
                    Contact contact = (Contact) this.contactsBookSPhones.get(user.phone);
                    if (contact != null) {
                        int indexOf = contact.shortPhones.indexOf(user.phone);
                        if (indexOf != -1) {
                            contact.phoneDeleted.set(indexOf, Integer.valueOf(0));
                        }
                    }
                    if (arrayList.length() != 0) {
                        arrayList.append(",");
                    }
                    arrayList.append(user.phone);
                    i3++;
                }
            }
            i4 = true;
            i3++;
        }
        while (i < arrayList3.size()) {
            num = (Integer) arrayList3.get(i);
            Utilities.phoneBookQueue.postRunnable(new Runnable() {
                public void run() {
                    ContactsController.this.deleteContactFromPhoneBook(num.intValue());
                }
            });
            User user2 = concurrentHashMap != null ? (User) concurrentHashMap.get(num) : null;
            if (user2 == null) {
                user2 = MessagesController.getInstance(this.currentAccount).getUser(num);
            } else {
                MessagesController.getInstance(this.currentAccount).putUser(user2, true);
            }
            if (user2 == null) {
                i4 = true;
            } else if (!TextUtils.isEmpty(user2.phone)) {
                Contact contact2 = (Contact) this.contactsBookSPhones.get(user2.phone);
                if (contact2 != null) {
                    int indexOf2 = contact2.shortPhones.indexOf(user2.phone);
                    if (indexOf2 != -1) {
                        contact2.phoneDeleted.set(indexOf2, Integer.valueOf(1));
                    }
                }
                if (stringBuilder.length() != 0) {
                    stringBuilder.append(",");
                }
                stringBuilder.append(user2.phone);
            }
            i++;
        }
        if (!(arrayList.length() == null && stringBuilder.length() == null)) {
            MessagesStorage.getInstance(this.currentAccount).applyPhoneBookUpdates(arrayList.toString(), stringBuilder.toString());
        }
        if (i4 != 0) {
            Utilities.stageQueue.postRunnable(new Runnable() {
                public void run() {
                    ContactsController.this.loadContacts(false, 0);
                }
            });
        } else {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    int i;
                    for (i = 0; i < arrayList2.size(); i++) {
                        TL_contact tL_contact = (TL_contact) arrayList2.get(i);
                        if (ContactsController.this.contactsDict.get(Integer.valueOf(tL_contact.user_id)) == null) {
                            ContactsController.this.contacts.add(tL_contact);
                            ContactsController.this.contactsDict.put(Integer.valueOf(tL_contact.user_id), tL_contact);
                        }
                    }
                    for (i = 0; i < arrayList3.size(); i++) {
                        Integer num = (Integer) arrayList3.get(i);
                        TL_contact tL_contact2 = (TL_contact) ContactsController.this.contactsDict.get(num);
                        if (tL_contact2 != null) {
                            ContactsController.this.contacts.remove(tL_contact2);
                            ContactsController.this.contactsDict.remove(num);
                        }
                    }
                    if (!arrayList2.isEmpty()) {
                        ContactsController.this.updateUnregisteredContacts(ContactsController.this.contacts);
                        ContactsController.this.performWriteContactsToPhoneBook();
                    }
                    ContactsController.this.performSyncPhoneBook(ContactsController.this.getContactsCopy(ContactsController.this.contactsBook), false, false, false, false, true, false);
                    ContactsController.this.buildContactsSectionsArrays(arrayList2.isEmpty() ^ 1);
                    NotificationCenter.getInstance(ContactsController.this.currentAccount).postNotificationName(NotificationCenter.contactsDidLoaded, new Object[0]);
                }
            });
        }
    }

    public void processContactsUpdates(ArrayList<Integer> arrayList, ConcurrentHashMap<Integer, User> concurrentHashMap) {
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            Integer num = (Integer) it.next();
            int indexOf;
            if (num.intValue() > 0) {
                TL_contact tL_contact = new TL_contact();
                tL_contact.user_id = num.intValue();
                arrayList2.add(tL_contact);
                if (!this.delayedContactsUpdate.isEmpty()) {
                    indexOf = this.delayedContactsUpdate.indexOf(Integer.valueOf(-num.intValue()));
                    if (indexOf != -1) {
                        this.delayedContactsUpdate.remove(indexOf);
                    }
                }
            } else if (num.intValue() < 0) {
                arrayList3.add(Integer.valueOf(-num.intValue()));
                if (!this.delayedContactsUpdate.isEmpty()) {
                    indexOf = this.delayedContactsUpdate.indexOf(Integer.valueOf(-num.intValue()));
                    if (indexOf != -1) {
                        this.delayedContactsUpdate.remove(indexOf);
                    }
                }
            }
        }
        if (!arrayList3.isEmpty()) {
            MessagesStorage.getInstance(this.currentAccount).deleteContacts(arrayList3);
        }
        if (!arrayList2.isEmpty()) {
            MessagesStorage.getInstance(this.currentAccount).putContacts(arrayList2, false);
        }
        if (this.contactsLoaded) {
            if (this.contactsBookLoaded) {
                applyContactsUpdates(arrayList, concurrentHashMap, arrayList2, arrayList3);
                return;
            }
        }
        this.delayedContactsUpdate.addAll(arrayList);
        if (BuildVars.LOGS_ENABLED != null) {
            arrayList = new StringBuilder();
            arrayList.append("delay update - contacts add = ");
            arrayList.append(arrayList2.size());
            arrayList.append(" delete = ");
            arrayList.append(arrayList3.size());
            FileLog.m0d(arrayList.toString());
        }
    }

    public long addContactToPhoneBook(User user, boolean z) {
        long j = -1;
        if (!(this.systemAccount == null || user == null)) {
            if (!TextUtils.isEmpty(user.phone)) {
                if (!hasContactsPermission()) {
                    return -1;
                }
                synchronized (this.observerLock) {
                    this.ignoreChanges = true;
                }
                ContentResolver contentResolver = ApplicationLoader.applicationContext.getContentResolver();
                if (z) {
                    try {
                        z = RawContacts.CONTENT_URI.buildUpon().appendQueryParameter("caller_is_syncadapter", "true").appendQueryParameter("account_name", this.systemAccount.name).appendQueryParameter("account_type", this.systemAccount.type).build();
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("sync2 = ");
                        stringBuilder.append(user.id);
                        contentResolver.delete(z, stringBuilder.toString(), null);
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                }
                z = new ArrayList();
                Builder newInsert = ContentProviderOperation.newInsert(RawContacts.CONTENT_URI);
                newInsert.withValue("account_name", this.systemAccount.name);
                newInsert.withValue("account_type", this.systemAccount.type);
                newInsert.withValue("sync1", user.phone);
                newInsert.withValue("sync2", Integer.valueOf(user.id));
                z.add(newInsert.build());
                newInsert = ContentProviderOperation.newInsert(Data.CONTENT_URI);
                newInsert.withValueBackReference("raw_contact_id", 0);
                newInsert.withValue("mimetype", "vnd.android.cursor.item/name");
                newInsert.withValue("data2", user.first_name);
                newInsert.withValue("data3", user.last_name);
                z.add(newInsert.build());
                newInsert = ContentProviderOperation.newInsert(Data.CONTENT_URI);
                newInsert.withValueBackReference("raw_contact_id", 0);
                newInsert.withValue("mimetype", "vnd.android.cursor.item/vnd.org.telegram.messenger.android.profile");
                newInsert.withValue("data1", Integer.valueOf(user.id));
                newInsert.withValue("data2", "Telegram Profile");
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("+");
                stringBuilder2.append(user.phone);
                newInsert.withValue("data3", stringBuilder2.toString());
                newInsert.withValue("data4", Integer.valueOf(user.id));
                z.add(newInsert.build());
                try {
                    user = contentResolver.applyBatch("com.android.contacts", z);
                    if (user != null && user.length <= false && user[0].uri) {
                        j = Long.parseLong(user[0].uri.getLastPathSegment());
                    }
                } catch (Throwable e2) {
                    FileLog.m3e(e2);
                }
                synchronized (this.observerLock) {
                    this.ignoreChanges = false;
                }
                return j;
            }
        }
        return -1;
    }

    private void deleteContactFromPhoneBook(int i) {
        if (hasContactsPermission()) {
            synchronized (this.observerLock) {
                this.ignoreChanges = true;
            }
            try {
                ContentResolver contentResolver = ApplicationLoader.applicationContext.getContentResolver();
                Uri build = RawContacts.CONTENT_URI.buildUpon().appendQueryParameter("caller_is_syncadapter", "true").appendQueryParameter("account_name", this.systemAccount.name).appendQueryParameter("account_type", this.systemAccount.type).build();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("sync2 = ");
                stringBuilder.append(i);
                contentResolver.delete(build, stringBuilder.toString(), null);
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
            synchronized (this.observerLock) {
                this.ignoreChanges = false;
            }
        }
    }

    protected void markAsContacted(final String str) {
        if (str != null) {
            Utilities.phoneBookQueue.postRunnable(new Runnable() {
                public void run() {
                    Uri parse = Uri.parse(str);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("last_time_contacted", Long.valueOf(System.currentTimeMillis()));
                    ApplicationLoader.applicationContext.getContentResolver().update(parse, contentValues, null, null);
                }
            });
        }
    }

    public void addContact(User user) {
        if (user != null) {
            if (!TextUtils.isEmpty(user.phone)) {
                TLObject tL_contacts_importContacts = new TL_contacts_importContacts();
                ArrayList arrayList = new ArrayList();
                TL_inputPhoneContact tL_inputPhoneContact = new TL_inputPhoneContact();
                tL_inputPhoneContact.phone = user.phone;
                if (!tL_inputPhoneContact.phone.startsWith("+")) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("+");
                    stringBuilder.append(tL_inputPhoneContact.phone);
                    tL_inputPhoneContact.phone = stringBuilder.toString();
                }
                tL_inputPhoneContact.first_name = user.first_name;
                tL_inputPhoneContact.last_name = user.last_name;
                tL_inputPhoneContact.client_id = 0;
                arrayList.add(tL_inputPhoneContact);
                tL_contacts_importContacts.contacts = arrayList;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_contacts_importContacts, new RequestDelegate() {
                    public void run(TLObject tLObject, TL_error tL_error) {
                        if (tL_error == null) {
                            final TL_contacts_importedContacts tL_contacts_importedContacts = (TL_contacts_importedContacts) tLObject;
                            MessagesStorage.getInstance(ContactsController.this.currentAccount).putUsersAndChats(tL_contacts_importedContacts.users, null, true, true);
                            for (int i = 0; i < tL_contacts_importedContacts.users.size(); i++) {
                                final User user = (User) tL_contacts_importedContacts.users.get(i);
                                Utilities.phoneBookQueue.postRunnable(new Runnable() {
                                    public void run() {
                                        ContactsController.this.addContactToPhoneBook(user, true);
                                    }
                                });
                                TL_contact tL_contact = new TL_contact();
                                tL_contact.user_id = user.id;
                                ArrayList arrayList = new ArrayList();
                                arrayList.add(tL_contact);
                                MessagesStorage.getInstance(ContactsController.this.currentAccount).putContacts(arrayList, false);
                                if (!TextUtils.isEmpty(user.phone)) {
                                    ContactsController.formatName(user.first_name, user.last_name);
                                    MessagesStorage.getInstance(ContactsController.this.currentAccount).applyPhoneBookUpdates(user.phone, TtmlNode.ANONYMOUS_REGION_ID);
                                    Contact contact = (Contact) ContactsController.this.contactsBookSPhones.get(user.phone);
                                    if (contact != null) {
                                        int indexOf = contact.shortPhones.indexOf(user.phone);
                                        if (indexOf != -1) {
                                            contact.phoneDeleted.set(indexOf, Integer.valueOf(0));
                                        }
                                    }
                                }
                            }
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    Iterator it = tL_contacts_importedContacts.users.iterator();
                                    while (it.hasNext()) {
                                        User user = (User) it.next();
                                        MessagesController.getInstance(ContactsController.this.currentAccount).putUser(user, false);
                                        if (ContactsController.this.contactsDict.get(Integer.valueOf(user.id)) == null) {
                                            TL_contact tL_contact = new TL_contact();
                                            tL_contact.user_id = user.id;
                                            ContactsController.this.contacts.add(tL_contact);
                                            ContactsController.this.contactsDict.put(Integer.valueOf(tL_contact.user_id), tL_contact);
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

    public void deleteContact(final ArrayList<User> arrayList) {
        if (arrayList != null) {
            if (!arrayList.isEmpty()) {
                TLObject tL_contacts_deleteContacts = new TL_contacts_deleteContacts();
                final ArrayList arrayList2 = new ArrayList();
                Iterator it = arrayList.iterator();
                while (it.hasNext()) {
                    User user = (User) it.next();
                    InputUser inputUser = MessagesController.getInstance(this.currentAccount).getInputUser(user);
                    if (inputUser != null) {
                        arrayList2.add(Integer.valueOf(user.id));
                        tL_contacts_deleteContacts.id.add(inputUser);
                    }
                }
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_contacts_deleteContacts, new RequestDelegate() {

                    /* renamed from: org.telegram.messenger.ContactsController$22$1 */
                    class C00851 implements Runnable {
                        C00851() {
                        }

                        public void run() {
                            Iterator it = arrayList.iterator();
                            while (it.hasNext()) {
                                ContactsController.this.deleteContactFromPhoneBook(((User) it.next()).id);
                            }
                        }
                    }

                    /* renamed from: org.telegram.messenger.ContactsController$22$2 */
                    class C00862 implements Runnable {
                        C00862() {
                        }

                        public void run() {
                            Iterator it = arrayList.iterator();
                            boolean z = false;
                            while (it.hasNext()) {
                                User user = (User) it.next();
                                TL_contact tL_contact = (TL_contact) ContactsController.this.contactsDict.get(Integer.valueOf(user.id));
                                if (tL_contact != null) {
                                    ContactsController.this.contacts.remove(tL_contact);
                                    ContactsController.this.contactsDict.remove(Integer.valueOf(user.id));
                                    z = true;
                                }
                            }
                            if (z) {
                                ContactsController.this.buildContactsSectionsArrays(false);
                            }
                            NotificationCenter.getInstance(ContactsController.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(1));
                            NotificationCenter.getInstance(ContactsController.this.currentAccount).postNotificationName(NotificationCenter.contactsDidLoaded, new Object[0]);
                        }
                    }

                    public void run(TLObject tLObject, TL_error tL_error) {
                        if (tL_error == null) {
                            MessagesStorage.getInstance(ContactsController.this.currentAccount).deleteContacts(arrayList2);
                            Utilities.phoneBookQueue.postRunnable(new C00851());
                            for (tLObject = null; tLObject < arrayList.size(); tLObject++) {
                                User user = (User) arrayList.get(tLObject);
                                if (!TextUtils.isEmpty(user.phone)) {
                                    UserObject.getUserName(user);
                                    MessagesStorage.getInstance(ContactsController.this.currentAccount).applyPhoneBookUpdates(user.phone, TtmlNode.ANONYMOUS_REGION_ID);
                                    Contact contact = (Contact) ContactsController.this.contactsBookSPhones.get(user.phone);
                                    if (contact != null) {
                                        tL_error = contact.shortPhones.indexOf(user.phone);
                                        if (tL_error != -1) {
                                            contact.phoneDeleted.set(tL_error, Integer.valueOf(1));
                                        }
                                    }
                                }
                            }
                            AndroidUtilities.runOnUIThread(new C00862());
                        }
                    }
                });
            }
        }
    }

    public void reloadContactsStatuses() {
        saveContactsLoadTime();
        MessagesController.getInstance(this.currentAccount).clearFullUsers();
        final Editor edit = MessagesController.getMainSettings(this.currentAccount).edit();
        edit.putBoolean("needGetStatuses", true).commit();
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_contacts_getStatuses(), new RequestDelegate() {
            public void run(final TLObject tLObject, TL_error tL_error) {
                if (tL_error == null) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            edit.remove("needGetStatuses").commit();
                            Vector vector = (Vector) tLObject;
                            if (!vector.objects.isEmpty()) {
                                ArrayList arrayList = new ArrayList();
                                Iterator it = vector.objects.iterator();
                                while (it.hasNext()) {
                                    Object next = it.next();
                                    User tL_user = new TL_user();
                                    TL_contactStatus tL_contactStatus = (TL_contactStatus) next;
                                    if (tL_contactStatus != null) {
                                        if (tL_contactStatus.status instanceof TL_userStatusRecently) {
                                            tL_contactStatus.status.expires = -100;
                                        } else if (tL_contactStatus.status instanceof TL_userStatusLastWeek) {
                                            tL_contactStatus.status.expires = -101;
                                        } else if (tL_contactStatus.status instanceof TL_userStatusLastMonth) {
                                            tL_contactStatus.status.expires = -102;
                                        }
                                        User user = MessagesController.getInstance(ContactsController.this.currentAccount).getUser(Integer.valueOf(tL_contactStatus.user_id));
                                        if (user != null) {
                                            user.status = tL_contactStatus.status;
                                        }
                                        tL_user.status = tL_contactStatus.status;
                                        arrayList.add(tL_user);
                                    }
                                }
                                MessagesStorage.getInstance(ContactsController.this.currentAccount).updateUsers(arrayList, true, true, true);
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
                public void run(final TLObject tLObject, final TL_error tL_error) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            if (tL_error == null) {
                                ContactsController.this.deleteAccountTTL = ((TL_accountDaysTTL) tLObject).days;
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
            TLObject tL_account_getPrivacy = new TL_account_getPrivacy();
            tL_account_getPrivacy.key = new TL_inputPrivacyKeyStatusTimestamp();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_getPrivacy, new RequestDelegate() {
                public void run(final TLObject tLObject, final TL_error tL_error) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            if (tL_error == null) {
                                TL_account_privacyRules tL_account_privacyRules = (TL_account_privacyRules) tLObject;
                                MessagesController.getInstance(ContactsController.this.currentAccount).putUsers(tL_account_privacyRules.users, false);
                                ContactsController.this.privacyRules = tL_account_privacyRules.rules;
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
            tL_account_getPrivacy = new TL_account_getPrivacy();
            tL_account_getPrivacy.key = new TL_inputPrivacyKeyPhoneCall();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_getPrivacy, new RequestDelegate() {
                public void run(final TLObject tLObject, final TL_error tL_error) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            if (tL_error == null) {
                                TL_account_privacyRules tL_account_privacyRules = (TL_account_privacyRules) tLObject;
                                MessagesController.getInstance(ContactsController.this.currentAccount).putUsers(tL_account_privacyRules.users, false);
                                ContactsController.this.callPrivacyRules = tL_account_privacyRules.rules;
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
            tL_account_getPrivacy = new TL_account_getPrivacy();
            tL_account_getPrivacy.key = new TL_inputPrivacyKeyChatInvite();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_getPrivacy, new RequestDelegate() {
                public void run(final TLObject tLObject, final TL_error tL_error) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            if (tL_error == null) {
                                TL_account_privacyRules tL_account_privacyRules = (TL_account_privacyRules) tLObject;
                                MessagesController.getInstance(ContactsController.this.currentAccount).putUsers(tL_account_privacyRules.users, false);
                                ContactsController.this.groupPrivacyRules = tL_account_privacyRules.rules;
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

    public void setDeleteAccountTTL(int i) {
        this.deleteAccountTTL = i;
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

    public ArrayList<PrivacyRule> getPrivacyRules(int i) {
        if (i == 2) {
            return this.callPrivacyRules;
        }
        if (i == 1) {
            return this.groupPrivacyRules;
        }
        return this.privacyRules;
    }

    public void setPrivacyRules(ArrayList<PrivacyRule> arrayList, int i) {
        if (i == 2) {
            this.callPrivacyRules = arrayList;
        } else if (i == 1) {
            this.groupPrivacyRules = arrayList;
        } else {
            this.privacyRules = arrayList;
        }
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.privacyRulesUpdated, new Object[0]);
        reloadContactsStatuses();
    }

    public static String formatName(String str, String str2) {
        if (str != null) {
            str = str.trim();
        }
        if (str2 != null) {
            str2 = str2.trim();
        }
        int i = 0;
        int length = str != null ? str.length() : 0;
        if (str2 != null) {
            i = str2.length();
        }
        StringBuilder stringBuilder = new StringBuilder((length + i) + 1);
        if (LocaleController.nameDisplayOrder == 1) {
            if (str != null && str.length() > 0) {
                stringBuilder.append(str);
                if (str2 != null && str2.length() > null) {
                    stringBuilder.append(" ");
                    stringBuilder.append(str2);
                }
            } else if (str2 != null && str2.length() > null) {
                stringBuilder.append(str2);
            }
        } else if (str2 != null && str2.length() > 0) {
            stringBuilder.append(str2);
            if (str != null && str.length() > null) {
                stringBuilder.append(" ");
                stringBuilder.append(str);
            }
        } else if (str != null && str.length() > null) {
            stringBuilder.append(str);
        }
        return stringBuilder.toString();
    }
}
