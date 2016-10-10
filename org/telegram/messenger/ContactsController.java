package org.telegram.messenger;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentProviderOperation;
import android.content.ContentProviderOperation.Builder;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.net.Uri.Builder;
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
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.telegram.PhoneFormat.PhoneFormat;
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
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_help_getInviteText;
import org.telegram.tgnet.TLRPC.TL_help_inviteText;
import org.telegram.tgnet.TLRPC.TL_importedContact;
import org.telegram.tgnet.TLRPC.TL_inputPhoneContact;
import org.telegram.tgnet.TLRPC.TL_inputPrivacyKeyChatInvite;
import org.telegram.tgnet.TLRPC.TL_inputPrivacyKeyStatusTimestamp;
import org.telegram.tgnet.TLRPC.TL_userStatusLastMonth;
import org.telegram.tgnet.TLRPC.TL_userStatusLastWeek;
import org.telegram.tgnet.TLRPC.TL_userStatusRecently;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.Vector;
import org.telegram.tgnet.TLRPC.contacts_Contacts;

public class ContactsController
{
  private static volatile ContactsController Instance = null;
  private static final Object loadContactsSync = new Object();
  private int completedRequestsCount;
  public ArrayList<TLRPC.TL_contact> contacts = new ArrayList();
  public HashMap<Integer, Contact> contactsBook = new HashMap();
  private boolean contactsBookLoaded = false;
  public HashMap<String, Contact> contactsBookSPhones = new HashMap();
  public HashMap<String, TLRPC.TL_contact> contactsByPhone = new HashMap();
  public SparseArray<TLRPC.TL_contact> contactsDict = new SparseArray();
  public boolean contactsLoaded = false;
  private boolean contactsSyncInProgress = false;
  private Account currentAccount;
  private ArrayList<Integer> delayedContactsUpdate = new ArrayList();
  private int deleteAccountTTL;
  private ArrayList<TLRPC.PrivacyRule> groupPrivacyRules = null;
  private boolean ignoreChanges = false;
  private String inviteText;
  private String lastContactsVersions = "";
  private boolean loadingContacts = false;
  private int loadingDeleteInfo = 0;
  private int loadingGroupInfo = 0;
  private int loadingLastSeenInfo = 0;
  private final Object observerLock = new Object();
  public ArrayList<Contact> phoneBookContacts = new ArrayList();
  private ArrayList<TLRPC.PrivacyRule> privacyRules = null;
  private String[] projectionNames = { "contact_id", "data2", "data3", "display_name", "data5" };
  private String[] projectionPhones = { "contact_id", "data1", "data2", "data3" };
  private HashMap<String, String> sectionsToReplace = new HashMap();
  public ArrayList<String> sortedUsersMutualSectionsArray = new ArrayList();
  public ArrayList<String> sortedUsersSectionsArray = new ArrayList();
  private boolean updatingInviteText = false;
  public HashMap<String, ArrayList<TLRPC.TL_contact>> usersMutualSectionsDict = new HashMap();
  public HashMap<String, ArrayList<TLRPC.TL_contact>> usersSectionsDict = new HashMap();
  
  public ContactsController()
  {
    if (ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).getBoolean("needGetStatuses", false)) {
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
  }
  
  private void applyContactsUpdates(ArrayList<Integer> paramArrayList1, ConcurrentHashMap<Integer, TLRPC.User> paramConcurrentHashMap, final ArrayList<TLRPC.TL_contact> paramArrayList, ArrayList<Integer> paramArrayList2)
  {
    final Object localObject1;
    if (paramArrayList != null)
    {
      localObject1 = paramArrayList2;
      if (paramArrayList2 != null) {}
    }
    else
    {
      paramArrayList2 = new ArrayList();
      localObject2 = new ArrayList();
      i = 0;
      paramArrayList = paramArrayList2;
      localObject1 = localObject2;
      if (i < paramArrayList1.size())
      {
        paramArrayList = (Integer)paramArrayList1.get(i);
        if (paramArrayList.intValue() > 0)
        {
          localObject1 = new TLRPC.TL_contact();
          ((TLRPC.TL_contact)localObject1).user_id = paramArrayList.intValue();
          paramArrayList2.add(localObject1);
        }
        for (;;)
        {
          i += 1;
          break;
          if (paramArrayList.intValue() < 0) {
            ((ArrayList)localObject2).add(Integer.valueOf(-paramArrayList.intValue()));
          }
        }
      }
    }
    FileLog.e("tmessages", "process update - contacts add = " + paramArrayList.size() + " delete = " + ((ArrayList)localObject1).size());
    paramArrayList2 = new StringBuilder();
    Object localObject2 = new StringBuilder();
    int i = 0;
    int j = 0;
    final Object localObject3;
    if (j < paramArrayList.size())
    {
      localObject3 = (TLRPC.TL_contact)paramArrayList.get(j);
      paramArrayList1 = null;
      if (paramConcurrentHashMap != null) {
        paramArrayList1 = (TLRPC.User)paramConcurrentHashMap.get(Integer.valueOf(((TLRPC.TL_contact)localObject3).user_id));
      }
      if (paramArrayList1 == null)
      {
        paramArrayList1 = MessagesController.getInstance().getUser(Integer.valueOf(((TLRPC.TL_contact)localObject3).user_id));
        label254:
        if ((paramArrayList1 != null) && (paramArrayList1.phone != null) && (paramArrayList1.phone.length() != 0)) {
          break label299;
        }
        i = 1;
      }
      for (;;)
      {
        j += 1;
        break;
        MessagesController.getInstance().putUser(paramArrayList1, true);
        break label254;
        label299:
        localObject3 = (Contact)this.contactsBookSPhones.get(paramArrayList1.phone);
        if (localObject3 != null)
        {
          k = ((Contact)localObject3).shortPhones.indexOf(paramArrayList1.phone);
          if (k != -1) {
            ((Contact)localObject3).phoneDeleted.set(k, Integer.valueOf(0));
          }
        }
        if (paramArrayList2.length() != 0) {
          paramArrayList2.append(",");
        }
        paramArrayList2.append(paramArrayList1.phone);
      }
    }
    int k = 0;
    j = i;
    i = k;
    if (i < ((ArrayList)localObject1).size())
    {
      localObject3 = (Integer)((ArrayList)localObject1).get(i);
      Utilities.phoneBookQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          ContactsController.this.deleteContactFromPhoneBook(localObject3.intValue());
        }
      });
      paramArrayList1 = null;
      if (paramConcurrentHashMap != null) {
        paramArrayList1 = (TLRPC.User)paramConcurrentHashMap.get(localObject3);
      }
      if (paramArrayList1 == null)
      {
        paramArrayList1 = MessagesController.getInstance().getUser((Integer)localObject3);
        label463:
        if (paramArrayList1 != null) {
          break label495;
        }
        k = 1;
      }
      for (;;)
      {
        i += 1;
        j = k;
        break;
        MessagesController.getInstance().putUser(paramArrayList1, true);
        break label463;
        label495:
        k = j;
        if (paramArrayList1.phone != null)
        {
          k = j;
          if (paramArrayList1.phone.length() > 0)
          {
            localObject3 = (Contact)this.contactsBookSPhones.get(paramArrayList1.phone);
            if (localObject3 != null)
            {
              k = ((Contact)localObject3).shortPhones.indexOf(paramArrayList1.phone);
              if (k != -1) {
                ((Contact)localObject3).phoneDeleted.set(k, Integer.valueOf(1));
              }
            }
            if (((StringBuilder)localObject2).length() != 0) {
              ((StringBuilder)localObject2).append(",");
            }
            ((StringBuilder)localObject2).append(paramArrayList1.phone);
            k = j;
          }
        }
      }
    }
    if ((paramArrayList2.length() != 0) || (((StringBuilder)localObject2).length() != 0)) {
      MessagesStorage.getInstance().applyPhoneBookUpdates(paramArrayList2.toString(), ((StringBuilder)localObject2).toString());
    }
    if (j != 0)
    {
      Utilities.stageQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          ContactsController.this.loadContacts(false, true);
        }
      });
      return;
    }
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        int i = 0;
        while (i < paramArrayList.size())
        {
          localObject = (TLRPC.TL_contact)paramArrayList.get(i);
          if (ContactsController.this.contactsDict.get(((TLRPC.TL_contact)localObject).user_id) == null)
          {
            ContactsController.this.contacts.add(localObject);
            ContactsController.this.contactsDict.put(((TLRPC.TL_contact)localObject).user_id, localObject);
          }
          i += 1;
        }
        i = 0;
        while (i < localObject1.size())
        {
          localObject = (Integer)localObject1.get(i);
          TLRPC.TL_contact localTL_contact = (TLRPC.TL_contact)ContactsController.this.contactsDict.get(((Integer)localObject).intValue());
          if (localTL_contact != null)
          {
            ContactsController.this.contacts.remove(localTL_contact);
            ContactsController.this.contactsDict.remove(((Integer)localObject).intValue());
          }
          i += 1;
        }
        if (!paramArrayList.isEmpty())
        {
          ContactsController.this.updateUnregisteredContacts(ContactsController.this.contacts);
          ContactsController.this.performWriteContactsToPhoneBook();
        }
        ContactsController.this.performSyncPhoneBook(ContactsController.this.getContactsCopy(ContactsController.this.contactsBook), false, false, false, false);
        Object localObject = ContactsController.this;
        if (!paramArrayList.isEmpty()) {}
        for (boolean bool = true;; bool = false)
        {
          ((ContactsController)localObject).buildContactsSectionsArrays(bool);
          NotificationCenter.getInstance().postNotificationName(NotificationCenter.contactsDidLoaded, new Object[0]);
          return;
        }
      }
    });
  }
  
  private void buildContactsSectionsArrays(boolean paramBoolean)
  {
    if (paramBoolean) {
      Collections.sort(this.contacts, new Comparator()
      {
        public int compare(TLRPC.TL_contact paramAnonymousTL_contact1, TLRPC.TL_contact paramAnonymousTL_contact2)
        {
          paramAnonymousTL_contact1 = MessagesController.getInstance().getUser(Integer.valueOf(paramAnonymousTL_contact1.user_id));
          paramAnonymousTL_contact2 = MessagesController.getInstance().getUser(Integer.valueOf(paramAnonymousTL_contact2.user_id));
          return UserObject.getFirstName(paramAnonymousTL_contact1).compareTo(UserObject.getFirstName(paramAnonymousTL_contact2));
        }
      });
    }
    StringBuilder localStringBuilder = new StringBuilder();
    HashMap localHashMap = new HashMap();
    ArrayList localArrayList2 = new ArrayList();
    Iterator localIterator = this.contacts.iterator();
    while (localIterator.hasNext())
    {
      TLRPC.TL_contact localTL_contact = (TLRPC.TL_contact)localIterator.next();
      Object localObject1 = MessagesController.getInstance().getUser(Integer.valueOf(localTL_contact.user_id));
      if (localObject1 != null)
      {
        Object localObject2 = UserObject.getFirstName((TLRPC.User)localObject1);
        localObject1 = localObject2;
        if (((String)localObject2).length() > 1) {
          localObject1 = ((String)localObject2).substring(0, 1);
        }
        if (((String)localObject1).length() == 0) {}
        for (localObject1 = "#";; localObject1 = ((String)localObject1).toUpperCase())
        {
          localObject2 = (String)this.sectionsToReplace.get(localObject1);
          if (localObject2 != null) {
            localObject1 = localObject2;
          }
          ArrayList localArrayList1 = (ArrayList)localHashMap.get(localObject1);
          localObject2 = localArrayList1;
          if (localArrayList1 == null)
          {
            localObject2 = new ArrayList();
            localHashMap.put(localObject1, localObject2);
            localArrayList2.add(localObject1);
          }
          ((ArrayList)localObject2).add(localTL_contact);
          if (localStringBuilder.length() != 0) {
            localStringBuilder.append(",");
          }
          localStringBuilder.append(localTL_contact.user_id);
          break;
        }
      }
    }
    UserConfig.contactsHash = Utilities.MD5(localStringBuilder.toString());
    UserConfig.saveConfig(false);
    Collections.sort(localArrayList2, new Comparator()
    {
      public int compare(String paramAnonymousString1, String paramAnonymousString2)
      {
        int i = paramAnonymousString1.charAt(0);
        int j = paramAnonymousString2.charAt(0);
        if (i == 35) {
          return 1;
        }
        if (j == 35) {
          return -1;
        }
        return paramAnonymousString1.compareTo(paramAnonymousString2);
      }
    });
    this.usersSectionsDict = localHashMap;
    this.sortedUsersSectionsArray = localArrayList2;
  }
  
  /* Error */
  private boolean checkContactsInternal()
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore 5
    //   3: iconst_0
    //   4: istore 7
    //   6: iconst_0
    //   7: istore_1
    //   8: iconst_0
    //   9: istore 6
    //   11: iconst_0
    //   12: istore 4
    //   14: iload_1
    //   15: istore_2
    //   16: aload_0
    //   17: invokespecial 597	org/telegram/messenger/ContactsController:hasContactsPermission	()Z
    //   20: ifne +5 -> 25
    //   23: iconst_0
    //   24: ireturn
    //   25: iload_1
    //   26: istore_2
    //   27: getstatic 248	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   30: invokevirtual 601	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   33: astore 10
    //   35: aconst_null
    //   36: astore 9
    //   38: aconst_null
    //   39: astore 8
    //   41: iload 4
    //   43: istore_1
    //   44: iload 6
    //   46: istore_3
    //   47: aload 10
    //   49: getstatic 607	android/provider/ContactsContract$RawContacts:CONTENT_URI	Landroid/net/Uri;
    //   52: iconst_1
    //   53: anewarray 203	java/lang/String
    //   56: dup
    //   57: iconst_0
    //   58: ldc_w 609
    //   61: aastore
    //   62: aconst_null
    //   63: aconst_null
    //   64: aconst_null
    //   65: invokevirtual 615	android/content/ContentResolver:query	(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   68: astore 10
    //   70: iload 7
    //   72: istore_1
    //   73: aload 10
    //   75: ifnull +225 -> 300
    //   78: aload 10
    //   80: astore 8
    //   82: iload 4
    //   84: istore_1
    //   85: aload 10
    //   87: astore 9
    //   89: iload 6
    //   91: istore_3
    //   92: new 440	java/lang/StringBuilder
    //   95: dup
    //   96: invokespecial 441	java/lang/StringBuilder:<init>	()V
    //   99: astore 11
    //   101: aload 10
    //   103: astore 8
    //   105: iload 4
    //   107: istore_1
    //   108: aload 10
    //   110: astore 9
    //   112: iload 6
    //   114: istore_3
    //   115: aload 10
    //   117: invokeinterface 620 1 0
    //   122: ifeq +79 -> 201
    //   125: aload 10
    //   127: astore 8
    //   129: iload 4
    //   131: istore_1
    //   132: aload 10
    //   134: astore 9
    //   136: iload 6
    //   138: istore_3
    //   139: aload 11
    //   141: aload 10
    //   143: aload 10
    //   145: ldc_w 609
    //   148: invokeinterface 624 2 0
    //   153: invokeinterface 628 2 0
    //   158: invokevirtual 447	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   161: pop
    //   162: goto -61 -> 101
    //   165: astore 10
    //   167: aload 8
    //   169: astore 9
    //   171: iload_1
    //   172: istore_3
    //   173: ldc_w 438
    //   176: aload 10
    //   178: invokestatic 631	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   181: iload_1
    //   182: istore_2
    //   183: aload 8
    //   185: ifnull +14 -> 199
    //   188: iload_1
    //   189: istore_2
    //   190: aload 8
    //   192: invokeinterface 634 1 0
    //   197: iload_1
    //   198: istore_2
    //   199: iload_2
    //   200: ireturn
    //   201: aload 10
    //   203: astore 8
    //   205: iload 4
    //   207: istore_1
    //   208: aload 10
    //   210: astore 9
    //   212: iload 6
    //   214: istore_3
    //   215: aload 11
    //   217: invokevirtual 456	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   220: astore 11
    //   222: aload 10
    //   224: astore 8
    //   226: iload 4
    //   228: istore_1
    //   229: iload 5
    //   231: istore_2
    //   232: aload 10
    //   234: astore 9
    //   236: iload 6
    //   238: istore_3
    //   239: aload_0
    //   240: getfield 179	org/telegram/messenger/ContactsController:lastContactsVersions	Ljava/lang/String;
    //   243: invokevirtual 485	java/lang/String:length	()I
    //   246: ifeq +34 -> 280
    //   249: aload 10
    //   251: astore 8
    //   253: iload 4
    //   255: istore_1
    //   256: iload 5
    //   258: istore_2
    //   259: aload 10
    //   261: astore 9
    //   263: iload 6
    //   265: istore_3
    //   266: aload_0
    //   267: getfield 179	org/telegram/messenger/ContactsController:lastContactsVersions	Ljava/lang/String;
    //   270: aload 11
    //   272: invokevirtual 637	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   275: ifne +5 -> 280
    //   278: iconst_1
    //   279: istore_2
    //   280: aload 10
    //   282: astore 8
    //   284: iload_2
    //   285: istore_1
    //   286: aload 10
    //   288: astore 9
    //   290: iload_2
    //   291: istore_3
    //   292: aload_0
    //   293: aload 11
    //   295: putfield 179	org/telegram/messenger/ContactsController:lastContactsVersions	Ljava/lang/String;
    //   298: iload_2
    //   299: istore_1
    //   300: iload_1
    //   301: istore_2
    //   302: aload 10
    //   304: ifnull -105 -> 199
    //   307: iload_1
    //   308: istore_2
    //   309: aload 10
    //   311: invokeinterface 634 1 0
    //   316: iload_1
    //   317: istore_2
    //   318: goto -119 -> 199
    //   321: astore 8
    //   323: ldc_w 438
    //   326: aload 8
    //   328: invokestatic 631	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   331: goto -132 -> 199
    //   334: astore 8
    //   336: aload 9
    //   338: ifnull +12 -> 350
    //   341: iload_3
    //   342: istore_2
    //   343: aload 9
    //   345: invokeinterface 634 1 0
    //   350: iload_3
    //   351: istore_2
    //   352: aload 8
    //   354: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	355	0	this	ContactsController
    //   7	310	1	bool1	boolean
    //   15	337	2	bool2	boolean
    //   46	305	3	bool3	boolean
    //   12	242	4	bool4	boolean
    //   1	256	5	bool5	boolean
    //   9	255	6	bool6	boolean
    //   4	67	7	bool7	boolean
    //   39	244	8	localObject1	Object
    //   321	6	8	localException1	Exception
    //   334	19	8	localObject2	Object
    //   36	308	9	localObject3	Object
    //   33	111	10	localObject4	Object
    //   165	145	10	localException2	Exception
    //   99	195	11	localObject5	Object
    // Exception table:
    //   from	to	target	type
    //   47	70	165	java/lang/Exception
    //   92	101	165	java/lang/Exception
    //   115	125	165	java/lang/Exception
    //   139	162	165	java/lang/Exception
    //   215	222	165	java/lang/Exception
    //   239	249	165	java/lang/Exception
    //   266	278	165	java/lang/Exception
    //   292	298	165	java/lang/Exception
    //   16	23	321	java/lang/Exception
    //   27	35	321	java/lang/Exception
    //   190	197	321	java/lang/Exception
    //   309	316	321	java/lang/Exception
    //   343	350	321	java/lang/Exception
    //   352	355	321	java/lang/Exception
    //   47	70	334	finally
    //   92	101	334	finally
    //   115	125	334	finally
    //   139	162	334	finally
    //   173	181	334	finally
    //   215	222	334	finally
    //   239	249	334	finally
    //   266	278	334	finally
    //   292	298	334	finally
  }
  
  private void deleteContactFromPhoneBook(int paramInt)
  {
    if (!hasContactsPermission()) {
      return;
    }
    synchronized (this.observerLock)
    {
      this.ignoreChanges = true;
    }
    try
    {
      ApplicationLoader.applicationContext.getContentResolver().delete(ContactsContract.RawContacts.CONTENT_URI.buildUpon().appendQueryParameter("caller_is_syncadapter", "true").appendQueryParameter("account_name", this.currentAccount.name).appendQueryParameter("account_type", this.currentAccount.type).build(), "sync2 = " + paramInt, null);
      synchronized (this.observerLock)
      {
        this.ignoreChanges = false;
        return;
      }
      localObject3 = finally;
      throw ((Throwable)localObject3);
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e("tmessages", localException);
      }
    }
  }
  
  public static String formatName(String paramString1, String paramString2)
  {
    int j = 0;
    String str = paramString1;
    if (paramString1 != null) {
      str = paramString1.trim();
    }
    paramString1 = paramString2;
    if (paramString2 != null) {
      paramString1 = paramString2.trim();
    }
    int i;
    if (str != null)
    {
      i = str.length();
      if (paramString1 != null) {
        j = paramString1.length();
      }
      paramString2 = new StringBuilder(j + i + 1);
      if (LocaleController.nameDisplayOrder != 1) {
        break label141;
      }
      if ((str == null) || (str.length() <= 0)) {
        break label121;
      }
      paramString2.append(str);
      if ((paramString1 != null) && (paramString1.length() > 0))
      {
        paramString2.append(" ");
        paramString2.append(paramString1);
      }
    }
    for (;;)
    {
      return paramString2.toString();
      i = 0;
      break;
      label121:
      if ((paramString1 != null) && (paramString1.length() > 0))
      {
        paramString2.append(paramString1);
        continue;
        label141:
        if ((paramString1 != null) && (paramString1.length() > 0))
        {
          paramString2.append(paramString1);
          if ((str != null) && (str.length() > 0))
          {
            paramString2.append(" ");
            paramString2.append(str);
          }
        }
        else if ((str != null) && (str.length() > 0))
        {
          paramString2.append(str);
        }
      }
    }
  }
  
  public static ContactsController getInstance()
  {
    Object localObject1 = Instance;
    if (localObject1 == null)
    {
      for (;;)
      {
        try
        {
          ContactsController localContactsController2 = Instance;
          localObject1 = localContactsController2;
          if (localContactsController2 == null) {
            localObject1 = new ContactsController();
          }
        }
        finally
        {
          continue;
        }
        try
        {
          Instance = (ContactsController)localObject1;
          return (ContactsController)localObject1;
        }
        finally {}
      }
      throw ((Throwable)localObject1);
    }
    return localContactsController1;
  }
  
  private boolean hasContactsPermission()
  {
    if (Build.VERSION.SDK_INT >= 23) {
      return ApplicationLoader.applicationContext.checkSelfPermission("android.permission.READ_CONTACTS") == 0;
    }
    Object localObject3 = null;
    Object localObject1 = null;
    for (;;)
    {
      try
      {
        localCursor = ApplicationLoader.applicationContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, this.projectionPhones, null, null, null);
        if (localCursor != null)
        {
          localObject1 = localCursor;
          localObject3 = localCursor;
          int i = localCursor.getCount();
          if (i != 0) {}
        }
        else
        {
          if (localCursor != null) {}
          try
          {
            localCursor.close();
            return false;
          }
          catch (Exception localException1)
          {
            FileLog.e("tmessages", localException1);
            continue;
          }
        }
      }
      catch (Throwable localThrowable)
      {
        Cursor localCursor;
        localObject3 = localException2;
        FileLog.e("tmessages", localThrowable);
        if (localException2 == null) {
          continue;
        }
        try
        {
          localException2.close();
        }
        catch (Exception localException3)
        {
          FileLog.e("tmessages", localException3);
        }
        continue;
      }
      finally
      {
        if (localObject3 == null) {
          break label169;
        }
      }
      try
      {
        localCursor.close();
        return true;
      }
      catch (Exception localException2)
      {
        FileLog.e("tmessages", localException2);
      }
    }
    try
    {
      ((Cursor)localObject3).close();
      label169:
      throw ((Throwable)localObject2);
    }
    catch (Exception localException4)
    {
      for (;;)
      {
        FileLog.e("tmessages", localException4);
      }
    }
  }
  
  private void performWriteContactsToPhoneBook()
  {
    final ArrayList localArrayList = new ArrayList();
    localArrayList.addAll(this.contacts);
    Utilities.phoneBookQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        ContactsController.this.performWriteContactsToPhoneBookInternal(localArrayList);
      }
    });
  }
  
  private void performWriteContactsToPhoneBookInternal(ArrayList<TLRPC.TL_contact> paramArrayList)
  {
    Object localObject1;
    Object localObject2;
    try
    {
      if (!hasContactsPermission()) {
        return;
      }
      localObject1 = ContactsContract.RawContacts.CONTENT_URI.buildUpon().appendQueryParameter("account_name", this.currentAccount.name).appendQueryParameter("account_type", this.currentAccount.type).build();
      localObject2 = ApplicationLoader.applicationContext.getContentResolver().query((Uri)localObject1, new String[] { "_id", "sync2" }, null, null, null);
      localObject1 = new HashMap();
      if (localObject2 == null) {
        return;
      }
      while (((Cursor)localObject2).moveToNext()) {
        ((HashMap)localObject1).put(Integer.valueOf(((Cursor)localObject2).getInt(1)), Long.valueOf(((Cursor)localObject2).getLong(0)));
      }
      ((Cursor)localObject2).close();
    }
    catch (Exception paramArrayList)
    {
      FileLog.e("tmessages", paramArrayList);
      return;
    }
    int i = 0;
    while (i < paramArrayList.size())
    {
      localObject2 = (TLRPC.TL_contact)paramArrayList.get(i);
      if (!((HashMap)localObject1).containsKey(Integer.valueOf(((TLRPC.TL_contact)localObject2).user_id))) {
        addContactToPhoneBook(MessagesController.getInstance().getUser(Integer.valueOf(((TLRPC.TL_contact)localObject2).user_id)), false);
      }
      i += 1;
    }
  }
  
  private HashMap<Integer, Contact> readContactsFromPhoneBook()
  {
    HashMap localHashMap1 = new HashMap();
    Object localObject6;
    HashMap localHashMap2;
    Object localObject7;
    Object localObject8;
    Object localObject4;
    int i;
    Object localObject5;
    Object localObject3;
    for (;;)
    {
      try
      {
        if (!hasContactsPermission()) {
          return localHashMap1;
        }
        localObject6 = ApplicationLoader.applicationContext.getContentResolver();
        localHashMap2 = new HashMap();
        localObject7 = new ArrayList();
        localObject8 = ((ContentResolver)localObject6).query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, this.projectionPhones, null, null, null);
        if (localObject8 == null) {
          break label472;
        }
        if (((Cursor)localObject8).getCount() <= 0) {
          break;
        }
        if (!((Cursor)localObject8).moveToNext()) {
          break;
        }
        Object localObject1 = ((Cursor)localObject8).getString(1);
        if ((localObject1 == null) || (((String)localObject1).length() == 0)) {
          continue;
        }
        localObject4 = PhoneFormat.stripExceptNumbers((String)localObject1, true);
        if (((String)localObject4).length() == 0) {
          continue;
        }
        localObject1 = localObject4;
        if (((String)localObject4).startsWith("+")) {
          localObject1 = ((String)localObject4).substring(1);
        }
        if (localHashMap2.containsKey(localObject1)) {
          continue;
        }
        Integer localInteger = Integer.valueOf(((Cursor)localObject8).getInt(0));
        if (!((ArrayList)localObject7).contains(localInteger)) {
          ((ArrayList)localObject7).add(localInteger);
        }
        i = ((Cursor)localObject8).getInt(2);
        localObject5 = (Contact)localHashMap1.get(localInteger);
        localObject3 = localObject5;
        if (localObject5 == null)
        {
          localObject3 = new Contact();
          ((Contact)localObject3).first_name = "";
          ((Contact)localObject3).last_name = "";
          ((Contact)localObject3).id = localInteger.intValue();
          localHashMap1.put(localInteger, localObject3);
        }
        ((Contact)localObject3).shortPhones.add(localObject1);
        ((Contact)localObject3).phones.add(localObject4);
        ((Contact)localObject3).phoneDeleted.add(Integer.valueOf(0));
        if (i == 0)
        {
          ((Contact)localObject3).phoneTypes.add(((Cursor)localObject8).getString(3));
          localHashMap2.put(localObject1, localObject3);
          continue;
        }
        if (i != 1) {
          break label365;
        }
      }
      catch (Exception localException1)
      {
        FileLog.e("tmessages", localException1);
        localHashMap1.clear();
        return localHashMap1;
      }
      ((Contact)localObject3).phoneTypes.add(LocaleController.getString("PhoneHome", 2131166104));
      continue;
      label365:
      if (i == 2) {
        ((Contact)localObject3).phoneTypes.add(LocaleController.getString("PhoneMobile", 2131166106));
      } else if (i == 3) {
        ((Contact)localObject3).phoneTypes.add(LocaleController.getString("PhoneWork", 2131166111));
      } else if (i == 12) {
        ((Contact)localObject3).phoneTypes.add(LocaleController.getString("PhoneMain", 2131166105));
      } else {
        ((Contact)localObject3).phoneTypes.add(LocaleController.getString("PhoneOther", 2131166110));
      }
    }
    ((Cursor)localObject8).close();
    label472:
    Object localObject2 = TextUtils.join(",", (Iterable)localObject7);
    localObject2 = ((ContentResolver)localObject6).query(ContactsContract.Data.CONTENT_URI, this.projectionNames, "contact_id IN (" + (String)localObject2 + ") AND " + "mimetype" + " = '" + "vnd.android.cursor.item/name" + "'", null, null);
    if ((localObject2 != null) && (((Cursor)localObject2).getCount() > 0))
    {
      while (((Cursor)localObject2).moveToNext())
      {
        i = ((Cursor)localObject2).getInt(0);
        localObject3 = ((Cursor)localObject2).getString(1);
        localObject4 = ((Cursor)localObject2).getString(2);
        localObject5 = ((Cursor)localObject2).getString(3);
        localObject7 = ((Cursor)localObject2).getString(4);
        localObject8 = (Contact)localHashMap1.get(Integer.valueOf(i));
        if ((localObject8 != null) && (((Contact)localObject8).first_name.length() == 0) && (((Contact)localObject8).last_name.length() == 0))
        {
          ((Contact)localObject8).first_name = ((String)localObject3);
          ((Contact)localObject8).last_name = ((String)localObject4);
          if (((Contact)localObject8).first_name == null) {
            ((Contact)localObject8).first_name = "";
          }
          if ((localObject7 != null) && (((String)localObject7).length() != 0)) {
            if (((Contact)localObject8).first_name.length() == 0) {
              break label800;
            }
          }
          label800:
          for (((Contact)localObject8).first_name = (((Contact)localObject8).first_name + " " + (String)localObject7);; ((Contact)localObject8).first_name = ((String)localObject7))
          {
            if (((Contact)localObject8).last_name == null) {
              ((Contact)localObject8).last_name = "";
            }
            if ((((Contact)localObject8).last_name.length() != 0) || (((Contact)localObject8).first_name.length() != 0) || (localObject5 == null) || (((String)localObject5).length() == 0)) {
              break;
            }
            ((Contact)localObject8).first_name = ((String)localObject5);
            break;
          }
        }
      }
      ((Cursor)localObject2).close();
    }
    try
    {
      localObject4 = ((ContentResolver)localObject6).query(ContactsContract.RawContacts.CONTENT_URI, new String[] { "display_name", "sync1", "contact_id" }, "account_type = 'com.whatsapp'", null, null);
      if (localObject4 != null)
      {
        while (((Cursor)localObject4).moveToNext())
        {
          localObject2 = ((Cursor)localObject4).getString(1);
          if ((localObject2 != null) && (((String)localObject2).length() != 0))
          {
            boolean bool = ((String)localObject2).startsWith("+");
            localObject3 = Utilities.parseIntToString((String)localObject2);
            if ((localObject3 != null) && (((String)localObject3).length() != 0))
            {
              localObject2 = localObject3;
              if (!bool) {
                localObject2 = "+" + (String)localObject3;
              }
              if (!localHashMap2.containsKey(localObject3))
              {
                localObject5 = ((Cursor)localObject4).getString(0);
                if (!TextUtils.isEmpty((CharSequence)localObject5))
                {
                  localObject6 = new Contact();
                  ((Contact)localObject6).first_name = ((String)localObject5);
                  ((Contact)localObject6).last_name = "";
                  ((Contact)localObject6).id = ((Cursor)localObject4).getInt(2);
                  localHashMap1.put(Integer.valueOf(((Contact)localObject6).id), localObject6);
                  ((Contact)localObject6).phoneDeleted.add(Integer.valueOf(0));
                  ((Contact)localObject6).shortPhones.add(localObject3);
                  ((Contact)localObject6).phones.add(localObject2);
                  ((Contact)localObject6).phoneTypes.add(LocaleController.getString("PhoneMobile", 2131166106));
                  localHashMap2.put(localObject3, localObject6);
                }
              }
            }
          }
        }
        ((Cursor)localObject4).close();
      }
    }
    catch (Exception localException2)
    {
      FileLog.e("tmessages", localException2);
      return localHashMap1;
    }
    return localHashMap1;
  }
  
  private void reloadContactsStatusesMaybe()
  {
    try
    {
      if (ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).getLong("lastReloadStatusTime", 0L) < System.currentTimeMillis() - 86400000L) {
        reloadContactsStatuses();
      }
      return;
    }
    catch (Exception localException)
    {
      FileLog.e("tmessages", localException);
    }
  }
  
  private void saveContactsLoadTime()
  {
    try
    {
      ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putLong("lastReloadStatusTime", System.currentTimeMillis()).commit();
      return;
    }
    catch (Exception localException)
    {
      FileLog.e("tmessages", localException);
    }
  }
  
  private void updateUnregisteredContacts(ArrayList<TLRPC.TL_contact> paramArrayList)
  {
    HashMap localHashMap = new HashMap();
    paramArrayList = paramArrayList.iterator();
    Object localObject2;
    while (paramArrayList.hasNext())
    {
      localObject1 = (TLRPC.TL_contact)paramArrayList.next();
      localObject2 = MessagesController.getInstance().getUser(Integer.valueOf(((TLRPC.TL_contact)localObject1).user_id));
      if ((localObject2 != null) && (((TLRPC.User)localObject2).phone != null) && (((TLRPC.User)localObject2).phone.length() != 0)) {
        localHashMap.put(((TLRPC.User)localObject2).phone, localObject1);
      }
    }
    paramArrayList = new ArrayList();
    Object localObject1 = this.contactsBook.entrySet().iterator();
    if (((Iterator)localObject1).hasNext())
    {
      localObject2 = (Map.Entry)((Iterator)localObject1).next();
      Contact localContact = (Contact)((Map.Entry)localObject2).getValue();
      ((Integer)((Map.Entry)localObject2).getKey()).intValue();
      int k = 0;
      int i = 0;
      for (;;)
      {
        int j = k;
        if (i < localContact.phones.size())
        {
          if ((localHashMap.containsKey((String)localContact.shortPhones.get(i))) || (((Integer)localContact.phoneDeleted.get(i)).intValue() == 1)) {
            j = 1;
          }
        }
        else
        {
          if (j != 0) {
            break;
          }
          paramArrayList.add(localContact);
          break;
        }
        i += 1;
      }
    }
    Collections.sort(paramArrayList, new Comparator()
    {
      public int compare(ContactsController.Contact paramAnonymousContact1, ContactsController.Contact paramAnonymousContact2)
      {
        String str2 = paramAnonymousContact1.first_name;
        String str1 = str2;
        if (str2.length() == 0) {
          str1 = paramAnonymousContact1.last_name;
        }
        str2 = paramAnonymousContact2.first_name;
        paramAnonymousContact1 = str2;
        if (str2.length() == 0) {
          paramAnonymousContact1 = paramAnonymousContact2.last_name;
        }
        return str1.compareTo(paramAnonymousContact1);
      }
    });
    this.phoneBookContacts = paramArrayList;
  }
  
  public void addContact(TLRPC.User paramUser)
  {
    if ((paramUser == null) || (paramUser.phone == null)) {
      return;
    }
    TLRPC.TL_contacts_importContacts localTL_contacts_importContacts = new TLRPC.TL_contacts_importContacts();
    ArrayList localArrayList = new ArrayList();
    TLRPC.TL_inputPhoneContact localTL_inputPhoneContact = new TLRPC.TL_inputPhoneContact();
    localTL_inputPhoneContact.phone = paramUser.phone;
    if (!localTL_inputPhoneContact.phone.startsWith("+")) {
      localTL_inputPhoneContact.phone = ("+" + localTL_inputPhoneContact.phone);
    }
    localTL_inputPhoneContact.first_name = paramUser.first_name;
    localTL_inputPhoneContact.last_name = paramUser.last_name;
    localTL_inputPhoneContact.client_id = 0L;
    localArrayList.add(localTL_inputPhoneContact);
    localTL_contacts_importContacts.contacts = localArrayList;
    localTL_contacts_importContacts.replace = false;
    ConnectionsManager.getInstance().sendRequest(localTL_contacts_importContacts, new RequestDelegate()
    {
      public void run(final TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
      {
        if (paramAnonymousTL_error != null) {
          return;
        }
        paramAnonymousTLObject = (TLRPC.TL_contacts_importedContacts)paramAnonymousTLObject;
        MessagesStorage.getInstance().putUsersAndChats(paramAnonymousTLObject.users, null, true, true);
        int i = 0;
        while (i < paramAnonymousTLObject.users.size())
        {
          paramAnonymousTL_error = (TLRPC.User)paramAnonymousTLObject.users.get(i);
          Utilities.phoneBookQueue.postRunnable(new Runnable()
          {
            public void run()
            {
              ContactsController.this.addContactToPhoneBook(paramAnonymousTL_error, true);
            }
          });
          Object localObject = new TLRPC.TL_contact();
          ((TLRPC.TL_contact)localObject).user_id = paramAnonymousTL_error.id;
          ArrayList localArrayList = new ArrayList();
          localArrayList.add(localObject);
          MessagesStorage.getInstance().putContacts(localArrayList, false);
          if ((paramAnonymousTL_error.phone != null) && (paramAnonymousTL_error.phone.length() > 0))
          {
            ContactsController.formatName(paramAnonymousTL_error.first_name, paramAnonymousTL_error.last_name);
            MessagesStorage.getInstance().applyPhoneBookUpdates(paramAnonymousTL_error.phone, "");
            localObject = (ContactsController.Contact)ContactsController.this.contactsBookSPhones.get(paramAnonymousTL_error.phone);
            if (localObject != null)
            {
              int j = ((ContactsController.Contact)localObject).shortPhones.indexOf(paramAnonymousTL_error.phone);
              if (j != -1) {
                ((ContactsController.Contact)localObject).phoneDeleted.set(j, Integer.valueOf(0));
              }
            }
          }
          i += 1;
        }
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            Iterator localIterator = paramAnonymousTLObject.users.iterator();
            while (localIterator.hasNext())
            {
              TLRPC.User localUser = (TLRPC.User)localIterator.next();
              MessagesController.getInstance().putUser(localUser, false);
              if (ContactsController.this.contactsDict.get(localUser.id) == null)
              {
                TLRPC.TL_contact localTL_contact = new TLRPC.TL_contact();
                localTL_contact.user_id = localUser.id;
                ContactsController.this.contacts.add(localTL_contact);
                ContactsController.this.contactsDict.put(localTL_contact.user_id, localTL_contact);
              }
            }
            ContactsController.this.buildContactsSectionsArrays(true);
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.contactsDidLoaded, new Object[0]);
          }
        });
      }
    }, 6);
  }
  
  public long addContactToPhoneBook(TLRPC.User arg1, boolean paramBoolean)
  {
    if ((this.currentAccount == null) || (??? == null) || (???.phone == null) || (???.phone.length() == 0)) {}
    while (!hasContactsPermission()) {
      return -1L;
    }
    l2 = -1L;
    synchronized (this.observerLock)
    {
      this.ignoreChanges = true;
      ??? = ApplicationLoader.applicationContext.getContentResolver();
      if (!paramBoolean) {}
    }
    try
    {
      ((ContentResolver)???).delete(ContactsContract.RawContacts.CONTENT_URI.buildUpon().appendQueryParameter("caller_is_syncadapter", "true").appendQueryParameter("account_name", this.currentAccount.name).appendQueryParameter("account_type", this.currentAccount.type).build(), "sync2 = " + ???.id, null);
      ArrayList localArrayList = new ArrayList();
      ContentProviderOperation.Builder localBuilder = ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI);
      localBuilder.withValue("account_name", this.currentAccount.name);
      localBuilder.withValue("account_type", this.currentAccount.type);
      localBuilder.withValue("sync1", ???.phone);
      localBuilder.withValue("sync2", Integer.valueOf(???.id));
      localArrayList.add(localBuilder.build());
      localBuilder = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI);
      localBuilder.withValueBackReference("raw_contact_id", 0);
      localBuilder.withValue("mimetype", "vnd.android.cursor.item/name");
      localBuilder.withValue("data2", ???.first_name);
      localBuilder.withValue("data3", ???.last_name);
      localArrayList.add(localBuilder.build());
      localBuilder = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI);
      localBuilder.withValueBackReference("raw_contact_id", 0);
      localBuilder.withValue("mimetype", "vnd.android.cursor.item/vnd.org.telegram.messenger.android.profile");
      localBuilder.withValue("data1", Integer.valueOf(???.id));
      localBuilder.withValue("data2", "Telegram Profile");
      localBuilder.withValue("data3", "+" + ???.phone);
      localBuilder.withValue("data4", Integer.valueOf(???.id));
      localArrayList.add(localBuilder.build());
      for (;;)
      {
        try
        {
          ??? = ((ContentResolver)???).applyBatch("com.android.contacts", localArrayList);
          l1 = l2;
          if (??? != null)
          {
            l1 = l2;
            if (???.length > 0)
            {
              l1 = l2;
              if (???[0].uri != null) {
                l1 = Long.parseLong(???[0].uri.getLastPathSegment());
              }
            }
          }
        }
        catch (Exception ???)
        {
          FileLog.e("tmessages", ???);
          long l1 = l2;
          continue;
        }
        synchronized (this.observerLock)
        {
          this.ignoreChanges = false;
          return l1;
        }
      }
      ??? = finally;
      throw ???;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e("tmessages", localException);
      }
    }
  }
  
  public void checkAppAccount()
  {
    AccountManager localAccountManager = AccountManager.get(ApplicationLoader.applicationContext);
    try
    {
      Account[] arrayOfAccount1 = localAccountManager.getAccountsByType("org.telegram.account");
      int i;
      if ((arrayOfAccount1 != null) && (arrayOfAccount1.length > 0))
      {
        i = 0;
        while (i < arrayOfAccount1.length)
        {
          localAccountManager.removeAccount(arrayOfAccount1[i], null, null);
          i += 1;
        }
      }
      Account[] arrayOfAccount2;
      int j;
      Account localAccount;
      label255:
      return;
    }
    catch (Exception localException2)
    {
      FileLog.e("tmessages", localException2);
      arrayOfAccount2 = localAccountManager.getAccountsByType("org.telegram.messenger");
      j = 0;
      i = 0;
      if (UserConfig.isClientActivated()) {
        if (arrayOfAccount2.length == 1)
        {
          localAccount = arrayOfAccount2[0];
          if (!localAccount.name.equals("" + UserConfig.getClientUserId()))
          {
            i = 1;
            readContacts();
          }
        }
      }
      for (;;)
      {
        if (i == 0) {
          break label255;
        }
        i = 0;
        try
        {
          while (i < arrayOfAccount2.length)
          {
            localAccountManager.removeAccount(arrayOfAccount2[i], null, null);
            i += 1;
          }
          this.currentAccount = localAccount;
        }
        catch (Exception localException3)
        {
          FileLog.e("tmessages", localException3);
          if (!UserConfig.isClientActivated()) {
            break label255;
          }
        }
        i = 1;
        break;
        i = j;
        if (arrayOfAccount2.length > 0) {
          i = 1;
        }
      }
      try
      {
        this.currentAccount = new Account("" + UserConfig.getClientUserId(), "org.telegram.messenger");
        localAccountManager.addAccountExplicitly(this.currentAccount, "", null);
        return;
      }
      catch (Exception localException1)
      {
        FileLog.e("tmessages", localException1);
      }
    }
  }
  
  public void checkContacts()
  {
    Utilities.globalQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        if (ContactsController.this.checkContactsInternal())
        {
          FileLog.e("tmessages", "detected contacts change");
          ContactsController.getInstance().performSyncPhoneBook(ContactsController.getInstance().getContactsCopy(ContactsController.getInstance().contactsBook), true, false, true, false);
        }
      }
    });
  }
  
  public void checkInviteText()
  {
    Object localObject = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
    this.inviteText = ((SharedPreferences)localObject).getString("invitetext", null);
    int i = ((SharedPreferences)localObject).getInt("invitetexttime", 0);
    if ((!this.updatingInviteText) && ((this.inviteText == null) || (86400 + i < (int)(System.currentTimeMillis() / 1000L))))
    {
      this.updatingInviteText = true;
      localObject = new TLRPC.TL_help_getInviteText();
      ConnectionsManager.getInstance().sendRequest((TLObject)localObject, new RequestDelegate()
      {
        public void run(final TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
        {
          if (paramAnonymousTLObject != null)
          {
            paramAnonymousTLObject = (TLRPC.TL_help_inviteText)paramAnonymousTLObject;
            if (paramAnonymousTLObject.message.length() != 0) {
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  ContactsController.access$102(ContactsController.this, false);
                  SharedPreferences.Editor localEditor = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
                  localEditor.putString("invitetext", paramAnonymousTLObject.message);
                  localEditor.putInt("invitetexttime", (int)(System.currentTimeMillis() / 1000L));
                  localEditor.commit();
                }
              });
            }
          }
        }
      }, 2);
    }
  }
  
  public void cleanup()
  {
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
    this.loadingContacts = false;
    this.contactsSyncInProgress = false;
    this.contactsLoaded = false;
    this.contactsBookLoaded = false;
    this.lastContactsVersions = "";
    this.loadingDeleteInfo = 0;
    this.deleteAccountTTL = 0;
    this.loadingLastSeenInfo = 0;
    this.loadingGroupInfo = 0;
    Utilities.globalQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        ContactsController.access$002(ContactsController.this, 0);
      }
    });
    this.privacyRules = null;
  }
  
  public void deleteAllAppAccounts()
  {
    try
    {
      AccountManager localAccountManager = AccountManager.get(ApplicationLoader.applicationContext);
      Account[] arrayOfAccount = localAccountManager.getAccountsByType("org.telegram.messenger");
      int i = 0;
      while (i < arrayOfAccount.length)
      {
        localAccountManager.removeAccount(arrayOfAccount[i], null, null);
        i += 1;
      }
      return;
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }
  
  public void deleteContact(final ArrayList<TLRPC.User> paramArrayList)
  {
    if ((paramArrayList == null) || (paramArrayList.isEmpty())) {
      return;
    }
    TLRPC.TL_contacts_deleteContacts localTL_contacts_deleteContacts = new TLRPC.TL_contacts_deleteContacts();
    final ArrayList localArrayList = new ArrayList();
    Iterator localIterator = paramArrayList.iterator();
    while (localIterator.hasNext())
    {
      TLRPC.User localUser = (TLRPC.User)localIterator.next();
      TLRPC.InputUser localInputUser = MessagesController.getInputUser(localUser);
      if (localInputUser != null)
      {
        localArrayList.add(Integer.valueOf(localUser.id));
        localTL_contacts_deleteContacts.id.add(localInputUser);
      }
    }
    ConnectionsManager.getInstance().sendRequest(localTL_contacts_deleteContacts, new RequestDelegate()
    {
      public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
      {
        if (paramAnonymousTL_error != null) {
          return;
        }
        MessagesStorage.getInstance().deleteContacts(localArrayList);
        Utilities.phoneBookQueue.postRunnable(new Runnable()
        {
          public void run()
          {
            Iterator localIterator = ContactsController.18.this.val$users.iterator();
            while (localIterator.hasNext())
            {
              TLRPC.User localUser = (TLRPC.User)localIterator.next();
              ContactsController.this.deleteContactFromPhoneBook(localUser.id);
            }
          }
        });
        paramAnonymousTLObject = paramArrayList.iterator();
        while (paramAnonymousTLObject.hasNext())
        {
          paramAnonymousTL_error = (TLRPC.User)paramAnonymousTLObject.next();
          if ((paramAnonymousTL_error.phone != null) && (paramAnonymousTL_error.phone.length() > 0))
          {
            UserObject.getUserName(paramAnonymousTL_error);
            MessagesStorage.getInstance().applyPhoneBookUpdates(paramAnonymousTL_error.phone, "");
            ContactsController.Contact localContact = (ContactsController.Contact)ContactsController.this.contactsBookSPhones.get(paramAnonymousTL_error.phone);
            if (localContact != null)
            {
              int i = localContact.shortPhones.indexOf(paramAnonymousTL_error.phone);
              if (i != -1) {
                localContact.phoneDeleted.set(i, Integer.valueOf(1));
              }
            }
          }
        }
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            int i = 0;
            Iterator localIterator = ContactsController.18.this.val$users.iterator();
            while (localIterator.hasNext())
            {
              TLRPC.User localUser = (TLRPC.User)localIterator.next();
              TLRPC.TL_contact localTL_contact = (TLRPC.TL_contact)ContactsController.this.contactsDict.get(localUser.id);
              if (localTL_contact != null)
              {
                i = 1;
                ContactsController.this.contacts.remove(localTL_contact);
                ContactsController.this.contactsDict.remove(localUser.id);
              }
            }
            if (i != 0) {
              ContactsController.this.buildContactsSectionsArrays(false);
            }
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(1) });
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.contactsDidLoaded, new Object[0]);
          }
        });
      }
    });
  }
  
  public void forceImportContacts()
  {
    Utilities.globalQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        ContactsController.getInstance().performSyncPhoneBook(new HashMap(), true, true, true, true);
      }
    });
  }
  
  public HashMap<Integer, Contact> getContactsCopy(HashMap<Integer, Contact> paramHashMap)
  {
    HashMap localHashMap = new HashMap();
    paramHashMap = paramHashMap.entrySet().iterator();
    while (paramHashMap.hasNext())
    {
      Object localObject = (Map.Entry)paramHashMap.next();
      Contact localContact = new Contact();
      localObject = (Contact)((Map.Entry)localObject).getValue();
      localContact.phoneDeleted.addAll(((Contact)localObject).phoneDeleted);
      localContact.phones.addAll(((Contact)localObject).phones);
      localContact.phoneTypes.addAll(((Contact)localObject).phoneTypes);
      localContact.shortPhones.addAll(((Contact)localObject).shortPhones);
      localContact.first_name = ((Contact)localObject).first_name;
      localContact.last_name = ((Contact)localObject).last_name;
      localContact.id = ((Contact)localObject).id;
      localHashMap.put(Integer.valueOf(localContact.id), localContact);
    }
    return localHashMap;
  }
  
  public int getDeleteAccountTTL()
  {
    return this.deleteAccountTTL;
  }
  
  public String getInviteText()
  {
    if (this.inviteText != null) {
      return this.inviteText;
    }
    return LocaleController.getString("InviteText", 2131165763);
  }
  
  public boolean getLoadingDeleteInfo()
  {
    return this.loadingDeleteInfo != 2;
  }
  
  public boolean getLoadingGroupInfo()
  {
    return this.loadingGroupInfo != 2;
  }
  
  public boolean getLoadingLastSeenInfo()
  {
    return this.loadingLastSeenInfo != 2;
  }
  
  public ArrayList<TLRPC.PrivacyRule> getPrivacyRules(boolean paramBoolean)
  {
    if (paramBoolean) {
      return this.groupPrivacyRules;
    }
    return this.privacyRules;
  }
  
  public boolean isLoadingContacts()
  {
    synchronized (loadContactsSync)
    {
      boolean bool = this.loadingContacts;
      return bool;
    }
  }
  
  public void loadContacts(boolean paramBoolean1, boolean paramBoolean2)
  {
    synchronized (loadContactsSync)
    {
      this.loadingContacts = true;
      if (paramBoolean1)
      {
        FileLog.e("tmessages", "load contacts from cache");
        MessagesStorage.getInstance().getContacts();
        return;
      }
    }
    FileLog.e("tmessages", "load contacts from server");
    TLRPC.TL_contacts_getContacts localTL_contacts_getContacts = new TLRPC.TL_contacts_getContacts();
    if (paramBoolean2) {}
    for (??? = "";; ??? = UserConfig.contactsHash)
    {
      localTL_contacts_getContacts.hash = ((String)???);
      ConnectionsManager.getInstance().sendRequest(localTL_contacts_getContacts, new RequestDelegate()
      {
        public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
        {
          if (paramAnonymousTL_error == null)
          {
            paramAnonymousTLObject = (TLRPC.contacts_Contacts)paramAnonymousTLObject;
            if ((paramAnonymousTLObject instanceof TLRPC.TL_contacts_contactsNotModified))
            {
              ContactsController.this.contactsLoaded = true;
              if ((!ContactsController.this.delayedContactsUpdate.isEmpty()) && (ContactsController.this.contactsBookLoaded))
              {
                ContactsController.this.applyContactsUpdates(ContactsController.this.delayedContactsUpdate, null, null, null);
                ContactsController.this.delayedContactsUpdate.clear();
              }
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  synchronized (ContactsController.loadContactsSync)
                  {
                    ContactsController.access$402(ContactsController.this, false);
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.contactsDidLoaded, new Object[0]);
                    return;
                  }
                }
              });
              FileLog.e("tmessages", "load contacts don't change");
            }
          }
          else
          {
            return;
          }
          ContactsController.this.processLoadedContacts(paramAnonymousTLObject.contacts, paramAnonymousTLObject.users, 0);
        }
      });
      return;
    }
  }
  
  public void loadPrivacySettings()
  {
    Object localObject;
    if (this.loadingDeleteInfo == 0)
    {
      this.loadingDeleteInfo = 1;
      localObject = new TLRPC.TL_account_getAccountTTL();
      ConnectionsManager.getInstance().sendRequest((TLObject)localObject, new RequestDelegate()
      {
        public void run(final TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
        {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              if (paramAnonymousTL_error == null)
              {
                TLRPC.TL_accountDaysTTL localTL_accountDaysTTL = (TLRPC.TL_accountDaysTTL)paramAnonymousTLObject;
                ContactsController.access$1802(ContactsController.this, localTL_accountDaysTTL.days);
                ContactsController.access$1902(ContactsController.this, 2);
              }
              for (;;)
              {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.privacyRulesUpdated, new Object[0]);
                return;
                ContactsController.access$1902(ContactsController.this, 0);
              }
            }
          });
        }
      });
    }
    if (this.loadingLastSeenInfo == 0)
    {
      this.loadingLastSeenInfo = 1;
      localObject = new TLRPC.TL_account_getPrivacy();
      ((TLRPC.TL_account_getPrivacy)localObject).key = new TLRPC.TL_inputPrivacyKeyStatusTimestamp();
      ConnectionsManager.getInstance().sendRequest((TLObject)localObject, new RequestDelegate()
      {
        public void run(final TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
        {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              if (paramAnonymousTL_error == null)
              {
                TLRPC.TL_account_privacyRules localTL_account_privacyRules = (TLRPC.TL_account_privacyRules)paramAnonymousTLObject;
                MessagesController.getInstance().putUsers(localTL_account_privacyRules.users, false);
                ContactsController.access$2002(ContactsController.this, localTL_account_privacyRules.rules);
                ContactsController.access$2102(ContactsController.this, 2);
              }
              for (;;)
              {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.privacyRulesUpdated, new Object[0]);
                return;
                ContactsController.access$2102(ContactsController.this, 0);
              }
            }
          });
        }
      });
    }
    if (this.loadingGroupInfo == 0)
    {
      this.loadingGroupInfo = 1;
      localObject = new TLRPC.TL_account_getPrivacy();
      ((TLRPC.TL_account_getPrivacy)localObject).key = new TLRPC.TL_inputPrivacyKeyChatInvite();
      ConnectionsManager.getInstance().sendRequest((TLObject)localObject, new RequestDelegate()
      {
        public void run(final TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
        {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              if (paramAnonymousTL_error == null)
              {
                TLRPC.TL_account_privacyRules localTL_account_privacyRules = (TLRPC.TL_account_privacyRules)paramAnonymousTLObject;
                MessagesController.getInstance().putUsers(localTL_account_privacyRules.users, false);
                ContactsController.access$2202(ContactsController.this, localTL_account_privacyRules.rules);
                ContactsController.access$2302(ContactsController.this, 2);
              }
              for (;;)
              {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.privacyRulesUpdated, new Object[0]);
                return;
                ContactsController.access$2302(ContactsController.this, 0);
              }
            }
          });
        }
      });
    }
    NotificationCenter.getInstance().postNotificationName(NotificationCenter.privacyRulesUpdated, new Object[0]);
  }
  
  protected void markAsContacted(final String paramString)
  {
    if (paramString == null) {
      return;
    }
    Utilities.phoneBookQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        Uri localUri = Uri.parse(paramString);
        ContentValues localContentValues = new ContentValues();
        localContentValues.put("last_time_contacted", Long.valueOf(System.currentTimeMillis()));
        ApplicationLoader.applicationContext.getContentResolver().update(localUri, localContentValues, null, null);
      }
    });
  }
  
  protected void performSyncPhoneBook(final HashMap<Integer, Contact> paramHashMap, final boolean paramBoolean1, final boolean paramBoolean2, final boolean paramBoolean3, final boolean paramBoolean4)
  {
    if ((!paramBoolean2) && (!this.contactsBookLoaded)) {
      return;
    }
    Utilities.globalQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        Object localObject5 = new HashMap();
        final Object localObject1 = paramHashMap.entrySet().iterator();
        Object localObject2;
        int i;
        while (((Iterator)localObject1).hasNext())
        {
          localObject2 = (ContactsController.Contact)((Map.Entry)((Iterator)localObject1).next()).getValue();
          i = 0;
          while (i < ((ContactsController.Contact)localObject2).shortPhones.size())
          {
            ((HashMap)localObject5).put(((ContactsController.Contact)localObject2).shortPhones.get(i), localObject2);
            i += 1;
          }
        }
        FileLog.e("tmessages", "start read contacts from phone");
        if (!paramBoolean3) {
          ContactsController.this.checkContactsInternal();
        }
        final HashMap localHashMap1 = ContactsController.this.readContactsFromPhoneBook();
        final HashMap localHashMap2 = new HashMap();
        int k = paramHashMap.size();
        ArrayList localArrayList = new ArrayList();
        Object localObject3;
        Object localObject4;
        label360:
        final int j;
        if (!paramHashMap.isEmpty())
        {
          Iterator localIterator = localHashMap1.entrySet().iterator();
          while (localIterator.hasNext())
          {
            localObject1 = (Map.Entry)localIterator.next();
            localObject3 = (Integer)((Map.Entry)localObject1).getKey();
            ContactsController.Contact localContact = (ContactsController.Contact)((Map.Entry)localObject1).getValue();
            localObject4 = (ContactsController.Contact)paramHashMap.get(localObject3);
            localObject2 = localObject4;
            localObject1 = localObject3;
            if (localObject4 == null)
            {
              i = 0;
              localObject2 = localObject4;
              localObject1 = localObject3;
              if (i < localContact.shortPhones.size())
              {
                localObject2 = (ContactsController.Contact)((HashMap)localObject5).get(localContact.shortPhones.get(i));
                if (localObject2 == null) {
                  break label473;
                }
                localObject1 = Integer.valueOf(((ContactsController.Contact)localObject2).id);
              }
            }
            if ((localObject2 != null) && (((TextUtils.isEmpty(localContact.first_name)) && (!((ContactsController.Contact)localObject2).first_name.equals(localContact.first_name))) || ((!TextUtils.isEmpty(localContact.last_name)) && (!((ContactsController.Contact)localObject2).last_name.equals(localContact.last_name)))))
            {
              i = 1;
              if ((localObject2 != null) && (i == 0)) {
                break label613;
              }
              j = 0;
              label371:
              if (j >= localContact.phones.size()) {
                break label595;
              }
              localObject3 = (String)localContact.shortPhones.get(j);
              localHashMap2.put(localObject3, localContact);
              if (localObject2 == null) {
                break label485;
              }
              int m = ((ContactsController.Contact)localObject2).shortPhones.indexOf(localObject3);
              if (m == -1) {
                break label485;
              }
              localObject4 = (Integer)((ContactsController.Contact)localObject2).phoneDeleted.get(m);
              localContact.phoneDeleted.set(j, localObject4);
              if (((Integer)localObject4).intValue() != 1) {
                break label485;
              }
            }
            for (;;)
            {
              j += 1;
              break label371;
              label473:
              i += 1;
              break;
              i = 0;
              break label360;
              label485:
              if ((paramBoolean1) && ((i != 0) || (!ContactsController.this.contactsByPhone.containsKey(localObject3))))
              {
                localObject3 = new TLRPC.TL_inputPhoneContact();
                ((TLRPC.TL_inputPhoneContact)localObject3).client_id = localContact.id;
                ((TLRPC.TL_inputPhoneContact)localObject3).client_id |= j << 32;
                ((TLRPC.TL_inputPhoneContact)localObject3).first_name = localContact.first_name;
                ((TLRPC.TL_inputPhoneContact)localObject3).last_name = localContact.last_name;
                ((TLRPC.TL_inputPhoneContact)localObject3).phone = ((String)localContact.phones.get(j));
                localArrayList.add(localObject3);
              }
            }
            label595:
            if (localObject2 != null)
            {
              paramHashMap.remove(localObject1);
              continue;
              label613:
              i = 0;
              if (i < localContact.phones.size())
              {
                localObject3 = (String)localContact.shortPhones.get(i);
                localHashMap2.put(localObject3, localContact);
                j = ((ContactsController.Contact)localObject2).shortPhones.indexOf(localObject3);
                if (j == -1) {
                  if (paramBoolean1)
                  {
                    localObject3 = (TLRPC.TL_contact)ContactsController.this.contactsByPhone.get(localObject3);
                    if (localObject3 == null) {
                      break label821;
                    }
                    TLRPC.User localUser = MessagesController.getInstance().getUser(Integer.valueOf(((TLRPC.TL_contact)localObject3).user_id));
                    if (localUser == null) {
                      break label821;
                    }
                    if (localUser.first_name == null) {
                      break label807;
                    }
                    localObject3 = localUser.first_name;
                    label732:
                    if (localUser.last_name == null) {
                      break label814;
                    }
                    localObject4 = localUser.last_name;
                    label747:
                    if ((localUser == null) || (((!((String)localObject3).equals(localContact.first_name)) || (!((String)localObject4).equals(localContact.last_name))) && ((!TextUtils.isEmpty(localContact.first_name)) || (!TextUtils.isEmpty(localContact.last_name))))) {
                      break label821;
                    }
                  }
                }
                for (;;)
                {
                  i += 1;
                  break;
                  label807:
                  localObject3 = "";
                  break label732;
                  label814:
                  localObject4 = "";
                  break label747;
                  label821:
                  localObject3 = new TLRPC.TL_inputPhoneContact();
                  ((TLRPC.TL_inputPhoneContact)localObject3).client_id = localContact.id;
                  ((TLRPC.TL_inputPhoneContact)localObject3).client_id |= i << 32;
                  ((TLRPC.TL_inputPhoneContact)localObject3).first_name = localContact.first_name;
                  ((TLRPC.TL_inputPhoneContact)localObject3).last_name = localContact.last_name;
                  ((TLRPC.TL_inputPhoneContact)localObject3).phone = ((String)localContact.phones.get(i));
                  localArrayList.add(localObject3);
                  continue;
                  localContact.phoneDeleted.set(i, ((ContactsController.Contact)localObject2).phoneDeleted.get(j));
                  ((ContactsController.Contact)localObject2).phones.remove(j);
                  ((ContactsController.Contact)localObject2).shortPhones.remove(j);
                  ((ContactsController.Contact)localObject2).phoneDeleted.remove(j);
                  ((ContactsController.Contact)localObject2).phoneTypes.remove(j);
                }
              }
              if (((ContactsController.Contact)localObject2).phones.isEmpty()) {
                paramHashMap.remove(localObject1);
              }
            }
          }
          if ((!paramBoolean2) && (paramHashMap.isEmpty()) && (localArrayList.isEmpty()) && (k == localHashMap1.size())) {
            FileLog.e("tmessages", "contacts not changed!");
          }
        }
        label1277:
        label1327:
        label1418:
        label1433:
        label1493:
        label1500:
        label1507:
        label1587:
        label1606:
        do
        {
          return;
          if ((paramBoolean1) && (!paramHashMap.isEmpty()) && (!localHashMap1.isEmpty()))
          {
            if (localArrayList.isEmpty()) {
              MessagesStorage.getInstance().putCachedPhoneBook(localHashMap1);
            }
            if ((1 == 0) && (!paramHashMap.isEmpty())) {
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  ArrayList localArrayList = new ArrayList();
                  if ((ContactsController.6.this.val$contactHashMap != null) && (!ContactsController.6.this.val$contactHashMap.isEmpty())) {
                    try
                    {
                      HashMap localHashMap = new HashMap();
                      i = 0;
                      if (i >= ContactsController.this.contacts.size()) {
                        break label168;
                      }
                      localObject = (TLRPC.TL_contact)ContactsController.this.contacts.get(i);
                      localObject = MessagesController.getInstance().getUser(Integer.valueOf(((TLRPC.TL_contact)localObject).user_id));
                      if ((localObject == null) || (((TLRPC.User)localObject).phone == null) || (((TLRPC.User)localObject).phone.length() == 0)) {
                        break label316;
                      }
                      localHashMap.put(((TLRPC.User)localObject).phone, localObject);
                    }
                    catch (Exception localException)
                    {
                      FileLog.e("tmessages", localException);
                    }
                  }
                  label147:
                  if (!localArrayList.isEmpty()) {
                    ContactsController.this.deleteContact(localArrayList);
                  }
                  return;
                  label168:
                  int j = 0;
                  Object localObject = ContactsController.6.this.val$contactHashMap.entrySet().iterator();
                  label187:
                  ContactsController.Contact localContact;
                  int m;
                  if (((Iterator)localObject).hasNext())
                  {
                    localContact = (ContactsController.Contact)((Map.Entry)((Iterator)localObject).next()).getValue();
                    m = 0;
                  }
                  int k;
                  for (int i = 0;; i = k + 1) {
                    if (i < localContact.shortPhones.size())
                    {
                      TLRPC.User localUser = (TLRPC.User)localException.get((String)localContact.shortPhones.get(i));
                      k = i;
                      if (localUser != null)
                      {
                        m = 1;
                        localArrayList.add(localUser);
                        localContact.shortPhones.remove(i);
                        k = i - 1;
                      }
                    }
                    else
                    {
                      if (m != 0)
                      {
                        i = localContact.shortPhones.size();
                        if (i != 0) {
                          break label187;
                        }
                      }
                      j += 1;
                      break label187;
                      break label147;
                      label316:
                      i += 1;
                      break;
                    }
                  }
                }
              });
            }
          }
          do
          {
            FileLog.e("tmessages", "done processing contacts");
            if (!paramBoolean1) {
              break label1606;
            }
            if (localArrayList.isEmpty()) {
              break label1587;
            }
            localObject1 = new HashMap(localHashMap1);
            ContactsController.access$002(ContactsController.this, 0);
            j = (int)Math.ceil(localArrayList.size() / 500.0F);
            i = 0;
            while (i < j)
            {
              localObject2 = new ArrayList();
              ((ArrayList)localObject2).addAll(localArrayList.subList(i * 500, Math.min((i + 1) * 500, localArrayList.size())));
              localObject3 = new TLRPC.TL_contacts_importContacts();
              ((TLRPC.TL_contacts_importContacts)localObject3).contacts = ((ArrayList)localObject2);
              ((TLRPC.TL_contacts_importContacts)localObject3).replace = false;
              ConnectionsManager.getInstance().sendRequest((TLObject)localObject3, new RequestDelegate()
              {
                public void run(TLObject paramAnonymous2TLObject, TLRPC.TL_error paramAnonymous2TL_error)
                {
                  ContactsController.access$008(ContactsController.this);
                  if (paramAnonymous2TL_error == null)
                  {
                    FileLog.e("tmessages", "contacts imported");
                    paramAnonymous2TLObject = (TLRPC.TL_contacts_importedContacts)paramAnonymous2TLObject;
                    if (!paramAnonymous2TLObject.retry_contacts.isEmpty())
                    {
                      i = 0;
                      while (i < paramAnonymous2TLObject.retry_contacts.size())
                      {
                        long l = ((Long)paramAnonymous2TLObject.retry_contacts.get(i)).longValue();
                        localObject1.remove(Integer.valueOf((int)l));
                        i += 1;
                      }
                    }
                    if ((ContactsController.this.completedRequestsCount == j) && (!localObject1.isEmpty())) {
                      MessagesStorage.getInstance().putCachedPhoneBook(localObject1);
                    }
                    MessagesStorage.getInstance().putUsersAndChats(paramAnonymous2TLObject.users, null, true, true);
                    paramAnonymous2TL_error = new ArrayList();
                    int i = 0;
                    while (i < paramAnonymous2TLObject.imported.size())
                    {
                      TLRPC.TL_contact localTL_contact = new TLRPC.TL_contact();
                      localTL_contact.user_id = ((TLRPC.TL_importedContact)paramAnonymous2TLObject.imported.get(i)).user_id;
                      paramAnonymous2TL_error.add(localTL_contact);
                      i += 1;
                    }
                    ContactsController.this.processLoadedContacts(paramAnonymous2TL_error, paramAnonymous2TLObject.users, 2);
                  }
                  for (;;)
                  {
                    if (ContactsController.this.completedRequestsCount == j) {
                      Utilities.stageQueue.postRunnable(new Runnable()
                      {
                        public void run()
                        {
                          ContactsController.this.contactsBookSPhones = ContactsController.6.2.this.val$contactsBookShort;
                          ContactsController.this.contactsBook = ContactsController.6.2.this.val$contactsMap;
                          ContactsController.access$602(ContactsController.this, false);
                          ContactsController.access$702(ContactsController.this, true);
                          if (ContactsController.6.this.val$first) {
                            ContactsController.this.contactsLoaded = true;
                          }
                          if ((!ContactsController.this.delayedContactsUpdate.isEmpty()) && (ContactsController.this.contactsLoaded))
                          {
                            ContactsController.this.applyContactsUpdates(ContactsController.this.delayedContactsUpdate, null, null, null);
                            ContactsController.this.delayedContactsUpdate.clear();
                          }
                        }
                      });
                    }
                    return;
                    FileLog.e("tmessages", "import contacts error " + paramAnonymous2TL_error.text);
                  }
                }
              }, 6);
              i += 1;
            }
            break;
          } while (!paramBoolean1);
          localObject3 = localHashMap1.entrySet().iterator();
          if (((Iterator)localObject3).hasNext())
          {
            localObject1 = (Map.Entry)((Iterator)localObject3).next();
            localObject4 = (ContactsController.Contact)((Map.Entry)localObject1).getValue();
            j = ((Integer)((Map.Entry)localObject1).getKey()).intValue();
            i = 0;
            if (i < ((ContactsController.Contact)localObject4).phones.size())
            {
              if (paramBoolean4) {
                break label1507;
              }
              localObject1 = (String)((ContactsController.Contact)localObject4).shortPhones.get(i);
              localObject1 = (TLRPC.TL_contact)ContactsController.this.contactsByPhone.get(localObject1);
              if (localObject1 == null) {
                break label1507;
              }
              localObject5 = MessagesController.getInstance().getUser(Integer.valueOf(((TLRPC.TL_contact)localObject1).user_id));
              if (localObject5 == null) {
                break label1507;
              }
              if (((TLRPC.User)localObject5).first_name == null) {
                break label1493;
              }
              localObject1 = ((TLRPC.User)localObject5).first_name;
              if (((TLRPC.User)localObject5).last_name == null) {
                break label1500;
              }
              localObject2 = ((TLRPC.User)localObject5).last_name;
              if ((localObject5 == null) || (((!((String)localObject1).equals(((ContactsController.Contact)localObject4).first_name)) || (!((String)localObject2).equals(((ContactsController.Contact)localObject4).last_name))) && ((!TextUtils.isEmpty(((ContactsController.Contact)localObject4).first_name)) || (!TextUtils.isEmpty(((ContactsController.Contact)localObject4).last_name))))) {
                break label1507;
              }
            }
          }
          for (;;)
          {
            i += 1;
            break label1327;
            break label1277;
            break;
            localObject1 = "";
            break label1418;
            localObject2 = "";
            break label1433;
            localObject1 = new TLRPC.TL_inputPhoneContact();
            ((TLRPC.TL_inputPhoneContact)localObject1).client_id = j;
            ((TLRPC.TL_inputPhoneContact)localObject1).client_id |= i << 32;
            ((TLRPC.TL_inputPhoneContact)localObject1).first_name = ((ContactsController.Contact)localObject4).first_name;
            ((TLRPC.TL_inputPhoneContact)localObject1).last_name = ((ContactsController.Contact)localObject4).last_name;
            ((TLRPC.TL_inputPhoneContact)localObject1).phone = ((String)((ContactsController.Contact)localObject4).phones.get(i));
            localArrayList.add(localObject1);
          }
          Utilities.stageQueue.postRunnable(new Runnable()
          {
            public void run()
            {
              ContactsController.this.contactsBookSPhones = localHashMap2;
              ContactsController.this.contactsBook = localHashMap1;
              ContactsController.access$602(ContactsController.this, false);
              ContactsController.access$702(ContactsController.this, true);
              if (ContactsController.6.this.val$first) {
                ContactsController.this.contactsLoaded = true;
              }
              if ((!ContactsController.this.delayedContactsUpdate.isEmpty()) && (ContactsController.this.contactsLoaded))
              {
                ContactsController.this.applyContactsUpdates(ContactsController.this.delayedContactsUpdate, null, null, null);
                ContactsController.this.delayedContactsUpdate.clear();
              }
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  ContactsController.this.updateUnregisteredContacts(ContactsController.this.contacts);
                  NotificationCenter.getInstance().postNotificationName(NotificationCenter.contactsDidLoaded, new Object[0]);
                }
              });
            }
          });
          return;
          Utilities.stageQueue.postRunnable(new Runnable()
          {
            public void run()
            {
              ContactsController.this.contactsBookSPhones = localHashMap2;
              ContactsController.this.contactsBook = localHashMap1;
              ContactsController.access$602(ContactsController.this, false);
              ContactsController.access$702(ContactsController.this, true);
              if (ContactsController.6.this.val$first) {
                ContactsController.this.contactsLoaded = true;
              }
              if ((!ContactsController.this.delayedContactsUpdate.isEmpty()) && (ContactsController.this.contactsLoaded) && (ContactsController.this.contactsBookLoaded))
              {
                ContactsController.this.applyContactsUpdates(ContactsController.this.delayedContactsUpdate, null, null, null);
                ContactsController.this.delayedContactsUpdate.clear();
              }
            }
          });
        } while (localHashMap1.isEmpty());
        MessagesStorage.getInstance().putCachedPhoneBook(localHashMap1);
      }
    });
  }
  
  public void processContactsUpdates(ArrayList<Integer> paramArrayList, ConcurrentHashMap<Integer, TLRPC.User> paramConcurrentHashMap)
  {
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = new ArrayList();
    Iterator localIterator = paramArrayList.iterator();
    while (localIterator.hasNext())
    {
      Integer localInteger = (Integer)localIterator.next();
      int i;
      if (localInteger.intValue() > 0)
      {
        TLRPC.TL_contact localTL_contact = new TLRPC.TL_contact();
        localTL_contact.user_id = localInteger.intValue();
        localArrayList1.add(localTL_contact);
        if (!this.delayedContactsUpdate.isEmpty())
        {
          i = this.delayedContactsUpdate.indexOf(Integer.valueOf(-localInteger.intValue()));
          if (i != -1) {
            this.delayedContactsUpdate.remove(i);
          }
        }
      }
      else if (localInteger.intValue() < 0)
      {
        localArrayList2.add(Integer.valueOf(-localInteger.intValue()));
        if (!this.delayedContactsUpdate.isEmpty())
        {
          i = this.delayedContactsUpdate.indexOf(Integer.valueOf(-localInteger.intValue()));
          if (i != -1) {
            this.delayedContactsUpdate.remove(i);
          }
        }
      }
    }
    if (!localArrayList2.isEmpty()) {
      MessagesStorage.getInstance().deleteContacts(localArrayList2);
    }
    if (!localArrayList1.isEmpty()) {
      MessagesStorage.getInstance().putContacts(localArrayList1, false);
    }
    if ((!this.contactsLoaded) || (!this.contactsBookLoaded))
    {
      this.delayedContactsUpdate.addAll(paramArrayList);
      FileLog.e("tmessages", "delay update - contacts add = " + localArrayList1.size() + " delete = " + localArrayList2.size());
      return;
    }
    applyContactsUpdates(paramArrayList, paramConcurrentHashMap, localArrayList1, localArrayList2);
  }
  
  public void processLoadedContacts(final ArrayList<TLRPC.TL_contact> paramArrayList, final ArrayList<TLRPC.User> paramArrayList1, final int paramInt)
  {
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        final boolean bool = true;
        final Object localObject1 = MessagesController.getInstance();
        Object localObject2 = paramArrayList1;
        if (paramInt == 1) {}
        for (;;)
        {
          ((MessagesController)localObject1).putUsers((ArrayList)localObject2, bool);
          localObject1 = new HashMap();
          bool = paramArrayList.isEmpty();
          if (ContactsController.this.contacts.isEmpty()) {
            break label145;
          }
          int j;
          for (i = 0; i < paramArrayList.size(); i = j + 1)
          {
            localObject2 = (TLRPC.TL_contact)paramArrayList.get(i);
            j = i;
            if (ContactsController.this.contactsDict.get(((TLRPC.TL_contact)localObject2).user_id) != null)
            {
              paramArrayList.remove(i);
              j = i - 1;
            }
          }
          bool = false;
        }
        paramArrayList.addAll(ContactsController.this.contacts);
        label145:
        int i = 0;
        while (i < paramArrayList.size())
        {
          localObject2 = MessagesController.getInstance().getUser(Integer.valueOf(((TLRPC.TL_contact)paramArrayList.get(i)).user_id));
          if (localObject2 != null) {
            ((HashMap)localObject1).put(Integer.valueOf(((TLRPC.User)localObject2).id), localObject2);
          }
          i += 1;
        }
        Utilities.stageQueue.postRunnable(new Runnable()
        {
          public void run()
          {
            FileLog.e("tmessages", "done loading contacts");
            if ((ContactsController.8.this.val$from == 1) && ((ContactsController.8.this.val$contactsArr.isEmpty()) || (Math.abs(System.currentTimeMillis() / 1000L - UserConfig.lastContactsSyncTime) >= 86400L)))
            {
              ContactsController.this.loadContacts(false, true);
              return;
            }
            if (ContactsController.8.this.val$from == 0)
            {
              UserConfig.lastContactsSyncTime = (int)(System.currentTimeMillis() / 1000L);
              UserConfig.saveConfig(false);
            }
            Object localObject1 = ContactsController.8.this.val$contactsArr.iterator();
            while (((Iterator)localObject1).hasNext())
            {
              localObject2 = (TLRPC.TL_contact)((Iterator)localObject1).next();
              if ((localObject1.get(Integer.valueOf(((TLRPC.TL_contact)localObject2).user_id)) == null) && (((TLRPC.TL_contact)localObject2).user_id != UserConfig.getClientUserId()))
              {
                ContactsController.this.loadContacts(false, true);
                FileLog.e("tmessages", "contacts are broken, load from server");
                return;
              }
            }
            Object localObject3;
            if (ContactsController.8.this.val$from != 1)
            {
              MessagesStorage.getInstance().putUsersAndChats(ContactsController.8.this.val$usersArr, null, true, true);
              localObject1 = MessagesStorage.getInstance();
              localObject2 = ContactsController.8.this.val$contactsArr;
              if (ContactsController.8.this.val$from != 2) {}
              for (boolean bool = true;; bool = false)
              {
                ((MessagesStorage)localObject1).putContacts((ArrayList)localObject2, bool);
                Collections.sort(ContactsController.8.this.val$contactsArr, new Comparator()
                {
                  public int compare(TLRPC.TL_contact paramAnonymous3TL_contact1, TLRPC.TL_contact paramAnonymous3TL_contact2)
                  {
                    if (paramAnonymous3TL_contact1.user_id > paramAnonymous3TL_contact2.user_id) {
                      return 1;
                    }
                    if (paramAnonymous3TL_contact1.user_id < paramAnonymous3TL_contact2.user_id) {
                      return -1;
                    }
                    return 0;
                  }
                });
                localObject1 = new StringBuilder();
                localObject2 = ContactsController.8.this.val$contactsArr.iterator();
                while (((Iterator)localObject2).hasNext())
                {
                  localObject3 = (TLRPC.TL_contact)((Iterator)localObject2).next();
                  if (((StringBuilder)localObject1).length() != 0) {
                    ((StringBuilder)localObject1).append(",");
                  }
                  ((StringBuilder)localObject1).append(((TLRPC.TL_contact)localObject3).user_id);
                }
              }
              UserConfig.contactsHash = Utilities.MD5(((StringBuilder)localObject1).toString());
              UserConfig.saveConfig(false);
            }
            Collections.sort(ContactsController.8.this.val$contactsArr, new Comparator()
            {
              public int compare(TLRPC.TL_contact paramAnonymous3TL_contact1, TLRPC.TL_contact paramAnonymous3TL_contact2)
              {
                paramAnonymous3TL_contact1 = (TLRPC.User)ContactsController.8.1.this.val$usersDict.get(Integer.valueOf(paramAnonymous3TL_contact1.user_id));
                paramAnonymous3TL_contact2 = (TLRPC.User)ContactsController.8.1.this.val$usersDict.get(Integer.valueOf(paramAnonymous3TL_contact2.user_id));
                return UserObject.getFirstName(paramAnonymous3TL_contact1).compareTo(UserObject.getFirstName(paramAnonymous3TL_contact2));
              }
            });
            final SparseArray localSparseArray = new SparseArray();
            final HashMap localHashMap1 = new HashMap();
            final HashMap localHashMap2 = new HashMap();
            final ArrayList localArrayList2 = new ArrayList();
            final ArrayList localArrayList3 = new ArrayList();
            final Object localObject2 = null;
            if (!ContactsController.this.contactsBookLoaded) {
              localObject2 = new HashMap();
            }
            int i = 0;
            while (i < ContactsController.8.this.val$contactsArr.size())
            {
              TLRPC.TL_contact localTL_contact = (TLRPC.TL_contact)ContactsController.8.this.val$contactsArr.get(i);
              TLRPC.User localUser = (TLRPC.User)localObject1.get(Integer.valueOf(localTL_contact.user_id));
              if (localUser == null)
              {
                i += 1;
              }
              else
              {
                localSparseArray.put(localTL_contact.user_id, localTL_contact);
                if (localObject2 != null) {
                  ((HashMap)localObject2).put(localUser.phone, localTL_contact);
                }
                localObject3 = UserObject.getFirstName(localUser);
                localObject1 = localObject3;
                if (((String)localObject3).length() > 1) {
                  localObject1 = ((String)localObject3).substring(0, 1);
                }
                if (((String)localObject1).length() == 0) {}
                for (localObject1 = "#";; localObject1 = ((String)localObject1).toUpperCase())
                {
                  localObject3 = (String)ContactsController.this.sectionsToReplace.get(localObject1);
                  if (localObject3 != null) {
                    localObject1 = localObject3;
                  }
                  ArrayList localArrayList1 = (ArrayList)localHashMap1.get(localObject1);
                  localObject3 = localArrayList1;
                  if (localArrayList1 == null)
                  {
                    localObject3 = new ArrayList();
                    localHashMap1.put(localObject1, localObject3);
                    localArrayList2.add(localObject1);
                  }
                  ((ArrayList)localObject3).add(localTL_contact);
                  if (!localUser.mutual_contact) {
                    break;
                  }
                  localArrayList1 = (ArrayList)localHashMap2.get(localObject1);
                  localObject3 = localArrayList1;
                  if (localArrayList1 == null)
                  {
                    localObject3 = new ArrayList();
                    localHashMap2.put(localObject1, localObject3);
                    localArrayList3.add(localObject1);
                  }
                  ((ArrayList)localObject3).add(localTL_contact);
                  break;
                }
              }
            }
            Collections.sort(localArrayList2, new Comparator()
            {
              public int compare(String paramAnonymous3String1, String paramAnonymous3String2)
              {
                int i = paramAnonymous3String1.charAt(0);
                int j = paramAnonymous3String2.charAt(0);
                if (i == 35) {
                  return 1;
                }
                if (j == 35) {
                  return -1;
                }
                return paramAnonymous3String1.compareTo(paramAnonymous3String2);
              }
            });
            Collections.sort(localArrayList3, new Comparator()
            {
              public int compare(String paramAnonymous3String1, String paramAnonymous3String2)
              {
                int i = paramAnonymous3String1.charAt(0);
                int j = paramAnonymous3String2.charAt(0);
                if (i == 35) {
                  return 1;
                }
                if (j == 35) {
                  return -1;
                }
                return paramAnonymous3String1.compareTo(paramAnonymous3String2);
              }
            });
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                ContactsController.this.contacts = ContactsController.8.this.val$contactsArr;
                ContactsController.this.contactsDict = localSparseArray;
                ContactsController.this.usersSectionsDict = localHashMap1;
                ContactsController.this.usersMutualSectionsDict = localHashMap2;
                ContactsController.this.sortedUsersSectionsArray = localArrayList2;
                ContactsController.this.sortedUsersMutualSectionsArray = localArrayList3;
                if (ContactsController.8.this.val$from != 2) {}
                synchronized (ContactsController.loadContactsSync)
                {
                  ContactsController.access$402(ContactsController.this, false);
                  ContactsController.this.performWriteContactsToPhoneBook();
                  ContactsController.this.updateUnregisteredContacts(ContactsController.8.this.val$contactsArr);
                  NotificationCenter.getInstance().postNotificationName(NotificationCenter.contactsDidLoaded, new Object[0]);
                  if ((ContactsController.8.this.val$from != 1) && (!ContactsController.8.1.this.val$isEmpty))
                  {
                    ContactsController.this.saveContactsLoadTime();
                    return;
                  }
                }
                ContactsController.this.reloadContactsStatusesMaybe();
              }
            });
            if ((!ContactsController.this.delayedContactsUpdate.isEmpty()) && (ContactsController.this.contactsLoaded) && (ContactsController.this.contactsBookLoaded))
            {
              ContactsController.this.applyContactsUpdates(ContactsController.this.delayedContactsUpdate, null, null, null);
              ContactsController.this.delayedContactsUpdate.clear();
            }
            if (localObject2 != null)
            {
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  Utilities.globalQueue.postRunnable(new Runnable()
                  {
                    public void run()
                    {
                      ContactsController.this.contactsByPhone = ContactsController.8.1.6.this.val$contactsByPhonesDictFinal;
                    }
                  });
                  if (ContactsController.this.contactsSyncInProgress) {
                    return;
                  }
                  ContactsController.access$602(ContactsController.this, true);
                  MessagesStorage.getInstance().getCachedPhoneBook();
                }
              });
              return;
            }
            ContactsController.this.contactsLoaded = true;
          }
        });
      }
    });
  }
  
  public void readContacts()
  {
    synchronized (loadContactsSync)
    {
      if (this.loadingContacts) {
        return;
      }
      this.loadingContacts = true;
      Utilities.stageQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          if ((!ContactsController.this.contacts.isEmpty()) || (ContactsController.this.contactsLoaded)) {
            synchronized (ContactsController.loadContactsSync)
            {
              ContactsController.access$402(ContactsController.this, false);
              return;
            }
          }
          ContactsController.this.loadContacts(true, false);
        }
      });
      return;
    }
  }
  
  public void reloadContactsStatuses()
  {
    saveContactsLoadTime();
    MessagesController.getInstance().clearFullUsers();
    final SharedPreferences.Editor localEditor = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
    localEditor.putBoolean("needGetStatuses", true).commit();
    TLRPC.TL_contacts_getStatuses localTL_contacts_getStatuses = new TLRPC.TL_contacts_getStatuses();
    ConnectionsManager.getInstance().sendRequest(localTL_contacts_getStatuses, new RequestDelegate()
    {
      public void run(final TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
      {
        if (paramAnonymousTL_error == null) {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              ContactsController.19.this.val$editor.remove("needGetStatuses").commit();
              Object localObject1 = (TLRPC.Vector)paramAnonymousTLObject;
              if (!((TLRPC.Vector)localObject1).objects.isEmpty())
              {
                ArrayList localArrayList = new ArrayList();
                localObject1 = ((TLRPC.Vector)localObject1).objects.iterator();
                while (((Iterator)localObject1).hasNext())
                {
                  Object localObject2 = ((Iterator)localObject1).next();
                  TLRPC.User localUser1 = new TLRPC.User();
                  localObject2 = (TLRPC.TL_contactStatus)localObject2;
                  if (localObject2 != null)
                  {
                    if ((((TLRPC.TL_contactStatus)localObject2).status instanceof TLRPC.TL_userStatusRecently)) {
                      ((TLRPC.TL_contactStatus)localObject2).status.expires = -100;
                    }
                    for (;;)
                    {
                      TLRPC.User localUser2 = MessagesController.getInstance().getUser(Integer.valueOf(((TLRPC.TL_contactStatus)localObject2).user_id));
                      if (localUser2 != null) {
                        localUser2.status = ((TLRPC.TL_contactStatus)localObject2).status;
                      }
                      localUser1.status = ((TLRPC.TL_contactStatus)localObject2).status;
                      localArrayList.add(localUser1);
                      break;
                      if ((((TLRPC.TL_contactStatus)localObject2).status instanceof TLRPC.TL_userStatusLastWeek)) {
                        ((TLRPC.TL_contactStatus)localObject2).status.expires = -101;
                      } else if ((((TLRPC.TL_contactStatus)localObject2).status instanceof TLRPC.TL_userStatusLastMonth)) {
                        ((TLRPC.TL_contactStatus)localObject2).status.expires = -102;
                      }
                    }
                  }
                }
                MessagesStorage.getInstance().updateUsers(localArrayList, true, true, true);
              }
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(4) });
            }
          });
        }
      }
    });
  }
  
  public void setDeleteAccountTTL(int paramInt)
  {
    this.deleteAccountTTL = paramInt;
  }
  
  public void setPrivacyRules(ArrayList<TLRPC.PrivacyRule> paramArrayList, boolean paramBoolean)
  {
    if (paramBoolean) {
      this.groupPrivacyRules = paramArrayList;
    }
    for (;;)
    {
      NotificationCenter.getInstance().postNotificationName(NotificationCenter.privacyRulesUpdated, new Object[0]);
      reloadContactsStatuses();
      return;
      this.privacyRules = paramArrayList;
    }
  }
  
  public static class Contact
  {
    public String first_name;
    public int id;
    public String last_name;
    public ArrayList<Integer> phoneDeleted = new ArrayList();
    public ArrayList<String> phoneTypes = new ArrayList();
    public ArrayList<String> phones = new ArrayList();
    public ArrayList<String> shortPhones = new ArrayList();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/ContactsController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */