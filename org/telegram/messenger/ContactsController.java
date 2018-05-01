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
import android.database.DatabaseUtils;
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

public class ContactsController
{
  private static volatile ContactsController[] Instance = new ContactsController[3];
  private ArrayList<TLRPC.PrivacyRule> callPrivacyRules;
  private int completedRequestsCount;
  public ArrayList<TLRPC.TL_contact> contacts = new ArrayList();
  public HashMap<String, Contact> contactsBook = new HashMap();
  private boolean contactsBookLoaded;
  public HashMap<String, Contact> contactsBookSPhones = new HashMap();
  public HashMap<String, TLRPC.TL_contact> contactsByPhone = new HashMap();
  public HashMap<String, TLRPC.TL_contact> contactsByShortPhone = new HashMap();
  public ConcurrentHashMap<Integer, TLRPC.TL_contact> contactsDict = new ConcurrentHashMap(20, 1.0F, 2);
  public boolean contactsLoaded;
  private boolean contactsSyncInProgress;
  private int currentAccount;
  private ArrayList<Integer> delayedContactsUpdate = new ArrayList();
  private int deleteAccountTTL;
  private ArrayList<TLRPC.PrivacyRule> groupPrivacyRules;
  private boolean ignoreChanges;
  private String inviteLink;
  private String lastContactsVersions = "";
  private final Object loadContactsSync = new Object();
  private int loadingCallsInfo;
  private boolean loadingContacts;
  private int loadingDeleteInfo;
  private int loadingGroupInfo;
  private int loadingLastSeenInfo;
  private boolean migratingContacts;
  private final Object observerLock = new Object();
  public ArrayList<Contact> phoneBookContacts = new ArrayList();
  private ArrayList<TLRPC.PrivacyRule> privacyRules;
  private String[] projectionNames = { "lookup", "data2", "data3", "data5" };
  private String[] projectionPhones = { "lookup", "data1", "data2", "data3", "display_name", "account_type" };
  private HashMap<String, String> sectionsToReplace = new HashMap();
  public ArrayList<String> sortedUsersMutualSectionsArray = new ArrayList();
  public ArrayList<String> sortedUsersSectionsArray = new ArrayList();
  private Account systemAccount;
  private boolean updatingInviteLink;
  public HashMap<String, ArrayList<TLRPC.TL_contact>> usersMutualSectionsDict = new HashMap();
  public HashMap<String, ArrayList<TLRPC.TL_contact>> usersSectionsDict = new HashMap();
  
  public ContactsController(int paramInt)
  {
    this.currentAccount = paramInt;
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
          i++;
          break;
          if (paramArrayList.intValue() < 0) {
            ((ArrayList)localObject2).add(Integer.valueOf(-paramArrayList.intValue()));
          }
        }
      }
    }
    if (BuildVars.LOGS_ENABLED) {
      FileLog.d("process update - contacts add = " + paramArrayList.size() + " delete = " + ((ArrayList)localObject1).size());
    }
    Object localObject2 = new StringBuilder();
    paramArrayList2 = new StringBuilder();
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
        paramArrayList1 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(((TLRPC.TL_contact)localObject3).user_id));
        label258:
        if ((paramArrayList1 != null) && (!TextUtils.isEmpty(paramArrayList1.phone))) {
          break label297;
        }
        i = 1;
      }
      for (;;)
      {
        j++;
        break;
        MessagesController.getInstance(this.currentAccount).putUser(paramArrayList1, true);
        break label258;
        label297:
        localObject3 = (Contact)this.contactsBookSPhones.get(paramArrayList1.phone);
        if (localObject3 != null)
        {
          k = ((Contact)localObject3).shortPhones.indexOf(paramArrayList1.phone);
          if (k != -1) {
            ((Contact)localObject3).phoneDeleted.set(k, Integer.valueOf(0));
          }
        }
        if (((StringBuilder)localObject2).length() != 0) {
          ((StringBuilder)localObject2).append(",");
        }
        ((StringBuilder)localObject2).append(paramArrayList1.phone);
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
        paramArrayList1 = MessagesController.getInstance(this.currentAccount).getUser((Integer)localObject3);
        label465:
        if (paramArrayList1 != null) {
          break label498;
        }
        k = 1;
      }
      for (;;)
      {
        i++;
        j = k;
        break;
        MessagesController.getInstance(this.currentAccount).putUser(paramArrayList1, true);
        break label465;
        label498:
        k = j;
        if (!TextUtils.isEmpty(paramArrayList1.phone))
        {
          localObject3 = (Contact)this.contactsBookSPhones.get(paramArrayList1.phone);
          if (localObject3 != null)
          {
            k = ((Contact)localObject3).shortPhones.indexOf(paramArrayList1.phone);
            if (k != -1) {
              ((Contact)localObject3).phoneDeleted.set(k, Integer.valueOf(1));
            }
          }
          if (paramArrayList2.length() != 0) {
            paramArrayList2.append(",");
          }
          paramArrayList2.append(paramArrayList1.phone);
          k = j;
        }
      }
    }
    if ((((StringBuilder)localObject2).length() != 0) || (paramArrayList2.length() != 0)) {
      MessagesStorage.getInstance(this.currentAccount).applyPhoneBookUpdates(((StringBuilder)localObject2).toString(), paramArrayList2.toString());
    }
    if (j != 0) {
      Utilities.stageQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          ContactsController.this.loadContacts(false, 0);
        }
      });
    }
    for (;;)
    {
      return;
      AndroidUtilities.runOnUIThread(new Runnable()
      {
        public void run()
        {
          boolean bool = true;
          for (int i = 0; i < paramArrayList.size(); i++)
          {
            localObject = (TLRPC.TL_contact)paramArrayList.get(i);
            if (ContactsController.this.contactsDict.get(Integer.valueOf(((TLRPC.TL_contact)localObject).user_id)) == null)
            {
              ContactsController.this.contacts.add(localObject);
              ContactsController.this.contactsDict.put(Integer.valueOf(((TLRPC.TL_contact)localObject).user_id), localObject);
            }
          }
          for (i = 0; i < localObject1.size(); i++)
          {
            Integer localInteger = (Integer)localObject1.get(i);
            localObject = (TLRPC.TL_contact)ContactsController.this.contactsDict.get(localInteger);
            if (localObject != null)
            {
              ContactsController.this.contacts.remove(localObject);
              ContactsController.this.contactsDict.remove(localInteger);
            }
          }
          if (!paramArrayList.isEmpty())
          {
            ContactsController.this.updateUnregisteredContacts(ContactsController.this.contacts);
            ContactsController.this.performWriteContactsToPhoneBook();
          }
          ContactsController.this.performSyncPhoneBook(ContactsController.this.getContactsCopy(ContactsController.this.contactsBook), false, false, false, false, true, false);
          Object localObject = ContactsController.this;
          if (!paramArrayList.isEmpty()) {}
          for (;;)
          {
            ((ContactsController)localObject).buildContactsSectionsArrays(bool);
            NotificationCenter.getInstance(ContactsController.this.currentAccount).postNotificationName(NotificationCenter.contactsDidLoaded, new Object[0]);
            return;
            bool = false;
          }
        }
      });
    }
  }
  
  private void buildContactsSectionsArrays(boolean paramBoolean)
  {
    if (paramBoolean) {
      Collections.sort(this.contacts, new Comparator()
      {
        public int compare(TLRPC.TL_contact paramAnonymousTL_contact1, TLRPC.TL_contact paramAnonymousTL_contact2)
        {
          paramAnonymousTL_contact1 = MessagesController.getInstance(ContactsController.this.currentAccount).getUser(Integer.valueOf(paramAnonymousTL_contact1.user_id));
          paramAnonymousTL_contact2 = MessagesController.getInstance(ContactsController.this.currentAccount).getUser(Integer.valueOf(paramAnonymousTL_contact2.user_id));
          return UserObject.getFirstName(paramAnonymousTL_contact1).compareTo(UserObject.getFirstName(paramAnonymousTL_contact2));
        }
      });
    }
    HashMap localHashMap = new HashMap();
    ArrayList localArrayList1 = new ArrayList();
    int i = 0;
    while (i < this.contacts.size())
    {
      TLRPC.TL_contact localTL_contact = (TLRPC.TL_contact)this.contacts.get(i);
      Object localObject1 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(localTL_contact.user_id));
      if (localObject1 == null)
      {
        i++;
      }
      else
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
          ArrayList localArrayList2 = (ArrayList)localHashMap.get(localObject1);
          localObject2 = localArrayList2;
          if (localArrayList2 == null)
          {
            localObject2 = new ArrayList();
            localHashMap.put(localObject1, localObject2);
            localArrayList1.add(localObject1);
          }
          ((ArrayList)localObject2).add(localTL_contact);
          break;
        }
      }
    }
    Collections.sort(localArrayList1, new Comparator()
    {
      public int compare(String paramAnonymousString1, String paramAnonymousString2)
      {
        int i = paramAnonymousString1.charAt(0);
        int j = paramAnonymousString2.charAt(0);
        if (i == 35) {
          j = 1;
        }
        for (;;)
        {
          return j;
          if (j == 35) {
            j = -1;
          } else {
            j = paramAnonymousString1.compareTo(paramAnonymousString2);
          }
        }
      }
    });
    this.usersSectionsDict = localHashMap;
    this.sortedUsersSectionsArray = localArrayList1;
  }
  
  private boolean checkContactsInternal()
  {
    bool1 = false;
    bool2 = false;
    bool3 = false;
    bool4 = false;
    bool5 = false;
    bool6 = false;
    bool7 = bool4;
    try
    {
      if (hasContactsPermission()) {
        break label32;
      }
      bool7 = bool1;
    }
    catch (Exception localException2)
    {
      try
      {
        label32:
        Object localObject1 = ((ContentResolver)localObject1).query(ContactsContract.RawContacts.CONTENT_URI, new String[] { "version" }, null, null, null);
        bool1 = bool3;
        if (localObject1 == null) {
          break label338;
        }
        localObject3 = localObject1;
        bool1 = bool6;
        localObject2 = localObject1;
        bool4 = bool5;
        localObject5 = new java/lang/StringBuilder;
        localObject3 = localObject1;
        bool1 = bool6;
        localObject2 = localObject1;
        bool4 = bool5;
        ((StringBuilder)localObject5).<init>();
        for (;;)
        {
          localObject3 = localObject1;
          bool1 = bool6;
          localObject2 = localObject1;
          bool4 = bool5;
          if (!((Cursor)localObject1).moveToNext()) {
            break;
          }
          localObject3 = localObject1;
          bool1 = bool6;
          localObject2 = localObject1;
          bool4 = bool5;
          ((StringBuilder)localObject5).append(((Cursor)localObject1).getString(((Cursor)localObject1).getColumnIndex("version")));
        }
      }
      catch (Exception localException1)
      {
        Object localObject3;
        Object localObject5;
        Object localObject2 = localObject3;
        bool4 = bool1;
        FileLog.e(localException1);
        bool7 = bool1;
        if (localObject3 == null) {
          break label228;
        }
        bool7 = bool1;
        ((Cursor)localObject3).close();
        bool7 = bool1;
        for (;;)
        {
          break;
          localObject3 = localException1;
          bool1 = bool6;
          localObject2 = localException1;
          bool4 = bool5;
          localObject5 = ((StringBuilder)localObject5).toString();
          localObject3 = localException1;
          bool1 = bool6;
          bool7 = bool2;
          localObject2 = localException1;
          bool4 = bool5;
          if (this.lastContactsVersions.length() != 0)
          {
            localObject3 = localException1;
            bool1 = bool6;
            bool7 = bool2;
            localObject2 = localException1;
            bool4 = bool5;
            if (!this.lastContactsVersions.equals(localObject5)) {
              bool7 = true;
            }
          }
          localObject3 = localException1;
          bool1 = bool7;
          localObject2 = localException1;
          bool4 = bool7;
          this.lastContactsVersions = ((String)localObject5);
          bool1 = bool7;
          bool7 = bool1;
          if (localException1 != null)
          {
            bool7 = bool1;
            localException1.close();
            bool7 = bool1;
            continue;
            localException2 = localException2;
            FileLog.e(localException2);
          }
        }
      }
      finally
      {
        if (localException2 == null) {
          break label390;
        }
        bool7 = bool4;
        localException2.close();
        bool7 = bool4;
      }
    }
    return bool7;
    bool7 = bool4;
    localObject1 = ApplicationLoader.applicationContext.getContentResolver();
    localObject2 = null;
    localObject3 = null;
    bool1 = bool6;
    bool4 = bool5;
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
      ??? = ApplicationLoader.applicationContext.getContentResolver();
      Uri localUri = ContactsContract.RawContacts.CONTENT_URI.buildUpon().appendQueryParameter("caller_is_syncadapter", "true").appendQueryParameter("account_name", this.systemAccount.name).appendQueryParameter("account_type", this.systemAccount.type).build();
      StringBuilder localStringBuilder = new java/lang/StringBuilder;
      localStringBuilder.<init>();
      ((ContentResolver)???).delete(localUri, "sync2 = " + paramInt, null);
      synchronized (this.observerLock)
      {
        this.ignoreChanges = false;
      }
      localObject3 = finally;
      throw ((Throwable)localObject3);
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e(localException);
      }
    }
  }
  
  public static String formatName(String paramString1, String paramString2)
  {
    int i = 0;
    String str = paramString1;
    if (paramString1 != null) {
      str = paramString1.trim();
    }
    paramString1 = paramString2;
    if (paramString2 != null) {
      paramString1 = paramString2.trim();
    }
    int j;
    if (str != null)
    {
      j = str.length();
      if (paramString1 != null) {
        i = paramString1.length();
      }
      paramString2 = new StringBuilder(i + j + 1);
      if (LocaleController.nameDisplayOrder != 1) {
        break label137;
      }
      if ((str == null) || (str.length() <= 0)) {
        break label117;
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
      j = 0;
      break;
      label117:
      if ((paramString1 != null) && (paramString1.length() > 0))
      {
        paramString2.append(paramString1);
        continue;
        label137:
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
  
  private int getContactsHash(ArrayList<TLRPC.TL_contact> paramArrayList)
  {
    long l = 0L;
    paramArrayList = new ArrayList(paramArrayList);
    Collections.sort(paramArrayList, new Comparator()
    {
      public int compare(TLRPC.TL_contact paramAnonymousTL_contact1, TLRPC.TL_contact paramAnonymousTL_contact2)
      {
        int i;
        if (paramAnonymousTL_contact1.user_id > paramAnonymousTL_contact2.user_id) {
          i = 1;
        }
        for (;;)
        {
          return i;
          if (paramAnonymousTL_contact1.user_id < paramAnonymousTL_contact2.user_id) {
            i = -1;
          } else {
            i = 0;
          }
        }
      }
    });
    int i = paramArrayList.size();
    int j = -1;
    if (j < i)
    {
      if (j == -1) {}
      for (l = (l * 20261L + 2147483648L + UserConfig.getInstance(this.currentAccount).contactsSavedCount) % 2147483648L;; l = (l * 20261L + 2147483648L + ((TLRPC.TL_contact)paramArrayList.get(j)).user_id) % 2147483648L)
      {
        j++;
        break;
      }
    }
    return (int)l;
  }
  
  public static ContactsController getInstance(int paramInt)
  {
    Object localObject1 = Instance[paramInt];
    Object localObject2 = localObject1;
    if (localObject1 == null) {}
    try
    {
      localObject1 = Instance[paramInt];
      localObject2 = localObject1;
      if (localObject1 == null)
      {
        localObject1 = Instance;
        localObject2 = new org/telegram/messenger/ContactsController;
        ((ContactsController)localObject2).<init>(paramInt);
        localObject1[paramInt] = localObject2;
      }
      return (ContactsController)localObject2;
    }
    finally {}
  }
  
  private boolean hasContactsPermission()
  {
    boolean bool;
    if (Build.VERSION.SDK_INT >= 23) {
      if (ApplicationLoader.applicationContext.checkSelfPermission("android.permission.READ_CONTACTS") == 0) {
        bool = true;
      }
    }
    Object localObject1;
    for (;;)
    {
      return bool;
      bool = false;
      continue;
      localObject1 = null;
      Object localObject2 = null;
      for (;;)
      {
        try
        {
          localCursor = ApplicationLoader.applicationContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, this.projectionPhones, null, null, null);
          if (localCursor != null)
          {
            localObject2 = localCursor;
            localObject1 = localCursor;
            int i = localCursor.getCount();
            if (i != 0) {}
          }
          else
          {
            if (localCursor != null) {}
            try
            {
              localCursor.close();
              bool = false;
            }
            catch (Exception localException2)
            {
              FileLog.e(localException2);
              continue;
            }
          }
        }
        catch (Throwable localThrowable)
        {
          Cursor localCursor;
          localObject1 = localException3;
          FileLog.e(localThrowable);
          if (localException3 == null) {
            continue;
          }
          try
          {
            localException3.close();
          }
          catch (Exception localException4)
          {
            FileLog.e(localException4);
          }
          continue;
        }
        finally
        {
          if (localObject1 == null) {
            break label170;
          }
        }
        try
        {
          localCursor.close();
          bool = true;
        }
        catch (Exception localException3)
        {
          FileLog.e(localException3);
        }
      }
    }
    try
    {
      ((Cursor)localObject1).close();
      label170:
      throw ((Throwable)localObject3);
    }
    catch (Exception localException1)
    {
      for (;;)
      {
        FileLog.e(localException1);
      }
    }
  }
  
  private boolean isNotValidNameString(String paramString)
  {
    boolean bool = true;
    if (TextUtils.isEmpty(paramString)) {}
    for (;;)
    {
      return bool;
      int i = 0;
      int j = 0;
      int k = paramString.length();
      while (j < k)
      {
        int m = paramString.charAt(j);
        int n = i;
        if (m >= 48)
        {
          n = i;
          if (m <= 57) {
            n = i + 1;
          }
        }
        j++;
        i = n;
      }
      if (i <= 3) {
        bool = false;
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
    int i;
    try
    {
      if (!hasContactsPermission()) {}
      do
      {
        return;
        localObject1 = ContactsContract.RawContacts.CONTENT_URI.buildUpon().appendQueryParameter("account_name", this.systemAccount.name).appendQueryParameter("account_type", this.systemAccount.type).build();
        localObject2 = ApplicationLoader.applicationContext.getContentResolver().query((Uri)localObject1, new String[] { "_id", "sync2" }, null, null, null);
        localObject1 = new org/telegram/messenger/support/SparseLongArray;
        ((SparseLongArray)localObject1).<init>();
      } while (localObject2 == null);
      while (((Cursor)localObject2).moveToNext()) {
        ((SparseLongArray)localObject1).put(((Cursor)localObject2).getInt(1), ((Cursor)localObject2).getLong(0));
      }
    }
    catch (Exception paramArrayList)
    {
      for (;;)
      {
        FileLog.e(paramArrayList);
      }
      ((Cursor)localObject2).close();
      i = 0;
    }
    while (i < paramArrayList.size())
    {
      localObject2 = (TLRPC.TL_contact)paramArrayList.get(i);
      if (((SparseLongArray)localObject1).indexOfKey(((TLRPC.TL_contact)localObject2).user_id) < 0) {
        addContactToPhoneBook(MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(((TLRPC.TL_contact)localObject2).user_id)), false);
      }
      i++;
    }
  }
  
  private HashMap<String, Contact> readContactsFromPhoneBook()
  {
    Object localObject1;
    if (!UserConfig.getInstance(this.currentAccount).syncContacts)
    {
      if (BuildVars.LOGS_ENABLED) {
        FileLog.d("contacts sync disabled");
      }
      localObject1 = new HashMap();
    }
    for (;;)
    {
      return (HashMap<String, Contact>)localObject1;
      if (!hasContactsPermission())
      {
        if (BuildVars.LOGS_ENABLED) {
          FileLog.d("app has no contacts permissions");
        }
        localObject1 = new HashMap();
        continue;
      }
      Object localObject4 = null;
      Object localObject5 = null;
      Object localObject6 = null;
      Object localObject7 = null;
      Object localObject8 = null;
      Object localObject10 = null;
      localObject1 = localObject6;
      Object localObject11 = localObject5;
      Object localObject12 = localObject4;
      try
      {
        StringBuilder localStringBuilder = new java/lang/StringBuilder;
        localObject1 = localObject6;
        localObject11 = localObject5;
        localObject12 = localObject4;
        localStringBuilder.<init>();
        localObject1 = localObject6;
        localObject11 = localObject5;
        localObject12 = localObject4;
        localContentResolver = ApplicationLoader.applicationContext.getContentResolver();
        localObject1 = localObject6;
        localObject11 = localObject5;
        localObject12 = localObject4;
        localHashMap = new java/util/HashMap;
        localObject1 = localObject6;
        localObject11 = localObject5;
        localObject12 = localObject4;
        localHashMap.<init>();
        localObject1 = localObject6;
        localObject11 = localObject5;
        localObject12 = localObject4;
        localArrayList = new java/util/ArrayList;
        localObject1 = localObject6;
        localObject11 = localObject5;
        localObject12 = localObject4;
        localArrayList.<init>();
        localObject1 = localObject6;
        localObject11 = localObject5;
        localObject12 = localObject4;
        localObject4 = localContentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, this.projectionPhones, null, null, null);
        localObject5 = localObject4;
        if (localObject4 != null)
        {
          localObject1 = localObject6;
          localObject11 = localObject4;
          localObject12 = localObject4;
          i = ((Cursor)localObject4).getCount();
          localObject8 = localObject7;
          if (i > 0)
          {
            if (0 != 0) {
              break label2488;
            }
            localObject1 = localObject6;
            localObject11 = localObject4;
            localObject12 = localObject4;
            localObject8 = new java/util/HashMap;
            localObject1 = localObject6;
            localObject11 = localObject4;
            localObject12 = localObject4;
            ((HashMap)localObject8).<init>(i);
            i = 1;
            for (;;)
            {
              localObject1 = localObject8;
              localObject11 = localObject4;
              localObject12 = localObject4;
              if (!((Cursor)localObject4).moveToNext()) {
                break label1545;
              }
              localObject1 = localObject8;
              localObject11 = localObject4;
              localObject12 = localObject4;
              localObject10 = ((Cursor)localObject4).getString(1);
              localObject1 = localObject8;
              localObject11 = localObject4;
              localObject12 = localObject4;
              localObject5 = ((Cursor)localObject4).getString(5);
              localObject6 = localObject5;
              if (localObject5 == null) {
                localObject6 = "";
              }
              localObject1 = localObject8;
              localObject11 = localObject4;
              localObject12 = localObject4;
              if (((String)localObject6).indexOf(".sim") == 0) {
                break;
              }
              bool = true;
              localObject1 = localObject8;
              localObject11 = localObject4;
              localObject12 = localObject4;
              if (!TextUtils.isEmpty((CharSequence)localObject10))
              {
                localObject1 = localObject8;
                localObject11 = localObject4;
                localObject12 = localObject4;
                localObject7 = PhoneFormat.stripExceptNumbers((String)localObject10, true);
                localObject1 = localObject8;
                localObject11 = localObject4;
                localObject12 = localObject4;
                if (!TextUtils.isEmpty((CharSequence)localObject7))
                {
                  localObject5 = localObject7;
                  localObject1 = localObject8;
                  localObject11 = localObject4;
                  localObject12 = localObject4;
                  if (((String)localObject7).startsWith("+"))
                  {
                    localObject1 = localObject8;
                    localObject11 = localObject4;
                    localObject12 = localObject4;
                    localObject5 = ((String)localObject7).substring(1);
                  }
                  localObject1 = localObject8;
                  localObject11 = localObject4;
                  localObject12 = localObject4;
                  str = ((Cursor)localObject4).getString(0);
                  localObject1 = localObject8;
                  localObject11 = localObject4;
                  localObject12 = localObject4;
                  localStringBuilder.setLength(0);
                  localObject1 = localObject8;
                  localObject11 = localObject4;
                  localObject12 = localObject4;
                  DatabaseUtils.appendEscapedSQLString(localStringBuilder, str);
                  localObject1 = localObject8;
                  localObject11 = localObject4;
                  localObject12 = localObject4;
                  localObject10 = localStringBuilder.toString();
                  localObject1 = localObject8;
                  localObject11 = localObject4;
                  localObject12 = localObject4;
                  localContact = (Contact)localHashMap.get(localObject5);
                  if (localContact == null) {
                    break label770;
                  }
                  localObject1 = localObject8;
                  localObject11 = localObject4;
                  localObject12 = localObject4;
                  if (!localContact.isGoodProvider)
                  {
                    localObject1 = localObject8;
                    localObject11 = localObject4;
                    localObject12 = localObject4;
                    if (!((String)localObject6).equals(localContact.provider))
                    {
                      localObject1 = localObject8;
                      localObject11 = localObject4;
                      localObject12 = localObject4;
                      localStringBuilder.setLength(0);
                      localObject1 = localObject8;
                      localObject11 = localObject4;
                      localObject12 = localObject4;
                      DatabaseUtils.appendEscapedSQLString(localStringBuilder, localContact.key);
                      localObject1 = localObject8;
                      localObject11 = localObject4;
                      localObject12 = localObject4;
                      localArrayList.remove(localStringBuilder.toString());
                      localObject1 = localObject8;
                      localObject11 = localObject4;
                      localObject12 = localObject4;
                      localArrayList.add(localObject10);
                      localObject1 = localObject8;
                      localObject11 = localObject4;
                      localObject12 = localObject4;
                      localContact.key = str;
                      localObject1 = localObject8;
                      localObject11 = localObject4;
                      localObject12 = localObject4;
                      localContact.isGoodProvider = bool;
                      localObject1 = localObject8;
                      localObject11 = localObject4;
                      localObject12 = localObject4;
                      localContact.provider = ((String)localObject6);
                    }
                  }
                }
              }
            }
          }
        }
      }
      catch (Throwable localThrowable)
      {
        HashMap localHashMap;
        boolean bool;
        String str;
        for (;;)
        {
          localObject12 = localObject11;
          FileLog.e(localThrowable);
          if (localObject1 != null)
          {
            localObject12 = localObject11;
            ((HashMap)localObject1).clear();
          }
          localObject12 = localObject1;
          if (localObject11 != null) {}
          try
          {
            ((Cursor)localObject11).close();
            localObject12 = localObject1;
          }
          catch (Exception localException2)
          {
            for (;;)
            {
              try
              {
                int j;
                int k;
                ((Cursor)localObject12).close();
                throw ((Throwable)localObject2);
                localObject3 = localThrowable;
                localObject11 = localObject4;
                localObject12 = localObject4;
                localContact.first_name = ((String)localObject10);
                localObject3 = localThrowable;
                localObject11 = localObject4;
                localObject12 = localObject4;
                localContact.last_name = "";
                continue;
                localObject3 = localThrowable;
                localObject11 = localObject4;
                localObject12 = localObject4;
                localObject10 = LocaleController.getString("PhoneMobile", NUM);
                continue;
                if (j == 1)
                {
                  localObject3 = localThrowable;
                  localObject11 = localObject4;
                  localObject12 = localObject4;
                  ((Contact)localObject6).phoneTypes.add(LocaleController.getString("PhoneHome", NUM));
                  continue;
                }
                if (j == 2)
                {
                  localObject3 = localThrowable;
                  localObject11 = localObject4;
                  localObject12 = localObject4;
                  ((Contact)localObject6).phoneTypes.add(LocaleController.getString("PhoneMobile", NUM));
                  continue;
                }
                if (j == 3)
                {
                  localObject3 = localThrowable;
                  localObject11 = localObject4;
                  localObject12 = localObject4;
                  ((Contact)localObject6).phoneTypes.add(LocaleController.getString("PhoneWork", NUM));
                  continue;
                }
                if (j == 12)
                {
                  localObject3 = localThrowable;
                  localObject11 = localObject4;
                  localObject12 = localObject4;
                  ((Contact)localObject6).phoneTypes.add(LocaleController.getString("PhoneMain", NUM));
                  continue;
                }
                localObject3 = localThrowable;
                localObject11 = localObject4;
                localObject12 = localObject4;
                ((Contact)localObject6).phoneTypes.add(LocaleController.getString("PhoneOther", NUM));
                continue;
                localObject3 = localThrowable;
                localObject11 = localObject4;
                localObject12 = localObject4;
              }
              catch (Exception localException2)
              {
                try
                {
                  ((Cursor)localObject4).close();
                  localObject5 = null;
                  localObject3 = localThrowable;
                  localObject11 = localObject5;
                  localObject12 = localObject5;
                  localObject7 = TextUtils.join(",", localArrayList);
                  localObject3 = localThrowable;
                  localObject11 = localObject5;
                  localObject12 = localObject5;
                  localObject10 = ContactsContract.Data.CONTENT_URI;
                  localObject3 = localThrowable;
                  localObject11 = localObject5;
                  localObject12 = localObject5;
                  localObject6 = this.projectionNames;
                  localObject3 = localThrowable;
                  localObject11 = localObject5;
                  localObject12 = localObject5;
                  localObject4 = new java/lang/StringBuilder;
                  localObject3 = localThrowable;
                  localObject11 = localObject5;
                  localObject12 = localObject5;
                  ((StringBuilder)localObject4).<init>();
                  localObject3 = localThrowable;
                  localObject11 = localObject5;
                  localObject12 = localObject5;
                  localObject5 = localContentResolver.query((Uri)localObject10, (String[])localObject6, "lookup IN (" + (String)localObject7 + ") AND " + "mimetype" + " = '" + "vnd.android.cursor.item/name" + "'", null, null);
                  localObject3 = localObject5;
                  if (localObject5 != null)
                  {
                    localObject3 = localThrowable;
                    localObject11 = localObject5;
                    localObject12 = localObject5;
                    if (((Cursor)localObject5).moveToNext())
                    {
                      localObject3 = localThrowable;
                      localObject11 = localObject5;
                      localObject12 = localObject5;
                      localObject10 = ((Cursor)localObject5).getString(0);
                      localObject3 = localThrowable;
                      localObject11 = localObject5;
                      localObject12 = localObject5;
                      localObject7 = ((Cursor)localObject5).getString(1);
                      localObject3 = localThrowable;
                      localObject11 = localObject5;
                      localObject12 = localObject5;
                      localObject6 = ((Cursor)localObject5).getString(2);
                      localObject3 = localThrowable;
                      localObject11 = localObject5;
                      localObject12 = localObject5;
                      localObject4 = ((Cursor)localObject5).getString(3);
                      localObject3 = localThrowable;
                      localObject11 = localObject5;
                      localObject12 = localObject5;
                      localObject10 = (Contact)localThrowable.get(localObject10);
                      if (localObject10 == null) {
                        continue;
                      }
                      localObject3 = localThrowable;
                      localObject11 = localObject5;
                      localObject12 = localObject5;
                      if (((Contact)localObject10).namesFilled) {
                        continue;
                      }
                      localObject3 = localThrowable;
                      localObject11 = localObject5;
                      localObject12 = localObject5;
                      if (((Contact)localObject10).isGoodProvider)
                      {
                        if (localObject7 != null)
                        {
                          localObject3 = localThrowable;
                          localObject11 = localObject5;
                          localObject12 = localObject5;
                          ((Contact)localObject10).first_name = ((String)localObject7);
                          if (localObject6 != null)
                          {
                            localObject3 = localThrowable;
                            localObject11 = localObject5;
                            localObject12 = localObject5;
                            ((Contact)localObject10).last_name = ((String)localObject6);
                            localObject3 = localThrowable;
                            localObject11 = localObject5;
                            localObject12 = localObject5;
                            if (!TextUtils.isEmpty((CharSequence)localObject4))
                            {
                              localObject3 = localThrowable;
                              localObject11 = localObject5;
                              localObject12 = localObject5;
                              if (TextUtils.isEmpty(((Contact)localObject10).first_name)) {
                                continue;
                              }
                              localObject3 = localThrowable;
                              localObject11 = localObject5;
                              localObject12 = localObject5;
                              localObject6 = new java/lang/StringBuilder;
                              localObject3 = localThrowable;
                              localObject11 = localObject5;
                              localObject12 = localObject5;
                              ((StringBuilder)localObject6).<init>();
                              localObject3 = localThrowable;
                              localObject11 = localObject5;
                              localObject12 = localObject5;
                              ((Contact)localObject10).first_name = (((Contact)localObject10).first_name + " " + (String)localObject4);
                            }
                            localObject3 = localThrowable;
                            localObject11 = localObject5;
                            localObject12 = localObject5;
                            ((Contact)localObject10).namesFilled = true;
                          }
                        }
                        else
                        {
                          localObject3 = localThrowable;
                          localObject11 = localObject5;
                          localObject12 = localObject5;
                          ((Contact)localObject10).first_name = "";
                          continue;
                        }
                        localObject3 = localThrowable;
                        localObject11 = localObject5;
                        localObject12 = localObject5;
                        ((Contact)localObject10).last_name = "";
                        continue;
                        localObject3 = localThrowable;
                        localObject11 = localObject5;
                        localObject12 = localObject5;
                        ((Contact)localObject10).first_name = ((String)localObject4);
                        continue;
                      }
                      localObject3 = localThrowable;
                      localObject11 = localObject5;
                      localObject12 = localObject5;
                      if (!isNotValidNameString((String)localObject7))
                      {
                        localObject3 = localThrowable;
                        localObject11 = localObject5;
                        localObject12 = localObject5;
                        if (!((Contact)localObject10).first_name.contains((CharSequence)localObject7))
                        {
                          localObject3 = localThrowable;
                          localObject11 = localObject5;
                          localObject12 = localObject5;
                          if (((String)localObject7).contains(((Contact)localObject10).first_name)) {}
                        }
                      }
                      else
                      {
                        localObject3 = localThrowable;
                        localObject11 = localObject5;
                        localObject12 = localObject5;
                        if (isNotValidNameString((String)localObject6)) {
                          continue;
                        }
                        localObject3 = localThrowable;
                        localObject11 = localObject5;
                        localObject12 = localObject5;
                        if (!((Contact)localObject10).last_name.contains((CharSequence)localObject6))
                        {
                          localObject3 = localThrowable;
                          localObject11 = localObject5;
                          localObject12 = localObject5;
                          if (!((String)localObject7).contains(((Contact)localObject10).last_name)) {
                            continue;
                          }
                        }
                      }
                      if (localObject7 != null)
                      {
                        localObject3 = localThrowable;
                        localObject11 = localObject5;
                        localObject12 = localObject5;
                        ((Contact)localObject10).first_name = ((String)localObject7);
                        localObject3 = localThrowable;
                        localObject11 = localObject5;
                        localObject12 = localObject5;
                        if (!TextUtils.isEmpty((CharSequence)localObject4))
                        {
                          localObject3 = localThrowable;
                          localObject11 = localObject5;
                          localObject12 = localObject5;
                          if (!TextUtils.isEmpty(((Contact)localObject10).first_name))
                          {
                            localObject3 = localThrowable;
                            localObject11 = localObject5;
                            localObject12 = localObject5;
                            localObject7 = new java/lang/StringBuilder;
                            localObject3 = localThrowable;
                            localObject11 = localObject5;
                            localObject12 = localObject5;
                            ((StringBuilder)localObject7).<init>();
                            localObject3 = localThrowable;
                            localObject11 = localObject5;
                            localObject12 = localObject5;
                            ((Contact)localObject10).first_name = (((Contact)localObject10).first_name + " " + (String)localObject4);
                          }
                        }
                        else
                        {
                          if (localObject6 == null) {
                            continue;
                          }
                          localObject3 = localThrowable;
                          localObject11 = localObject5;
                          localObject12 = localObject5;
                          ((Contact)localObject10).last_name = ((String)localObject6);
                        }
                      }
                      else
                      {
                        localObject3 = localThrowable;
                        localObject11 = localObject5;
                        localObject12 = localObject5;
                        ((Contact)localObject10).first_name = "";
                        continue;
                      }
                      localObject3 = localThrowable;
                      localObject11 = localObject5;
                      localObject12 = localObject5;
                      ((Contact)localObject10).first_name = ((String)localObject4);
                      continue;
                      localObject3 = localThrowable;
                      localObject11 = localObject5;
                      localObject12 = localObject5;
                      ((Contact)localObject10).last_name = "";
                      continue;
                    }
                    localObject3 = localThrowable;
                    localObject11 = localObject5;
                    localObject12 = localObject5;
                  }
                }
                catch (Exception localException2)
                {
                  try
                  {
                    ((Cursor)localObject5).close();
                    Object localObject3 = null;
                    localObject12 = localThrowable;
                    if (localObject3 == null) {
                      continue;
                    }
                    try
                    {
                      ((Cursor)localObject3).close();
                      localObject12 = localThrowable;
                    }
                    catch (Exception localException1)
                    {
                      FileLog.e(localException1);
                      localObject12 = localThrowable;
                    }
                    continue;
                    localException4 = localException4;
                    FileLog.e(localException4);
                    Exception localException5 = localException1;
                    continue;
                    localException6 = localException6;
                    FileLog.e(localException6);
                    continue;
                    localException2 = localException2;
                  }
                  catch (Exception localException3)
                  {
                    continue;
                  }
                }
              }
              localObject6 = localObject10;
            }
          }
          localObject1 = localObject12;
          if (localObject12 != null) {
            break;
          }
          localObject1 = new HashMap();
          break;
          bool = false;
        }
        label770:
        localObject1 = localThrowable;
        localObject11 = localObject4;
        localObject12 = localObject4;
        if (!localArrayList.contains(localObject10))
        {
          localObject1 = localThrowable;
          localObject11 = localObject4;
          localObject12 = localObject4;
          localArrayList.add(localObject10);
        }
        localObject1 = localThrowable;
        localObject11 = localObject4;
        localObject12 = localObject4;
        j = ((Cursor)localObject4).getInt(2);
        localObject1 = localThrowable;
        localObject11 = localObject4;
        localObject12 = localObject4;
        localObject10 = (Contact)localThrowable.get(str);
        if (localObject10 == null)
        {
          localObject1 = localThrowable;
          localObject11 = localObject4;
          localObject12 = localObject4;
          localContact = new org/telegram/messenger/ContactsController$Contact;
          localObject1 = localThrowable;
          localObject11 = localObject4;
          localObject12 = localObject4;
          localContact.<init>();
          localObject1 = localThrowable;
          localObject11 = localObject4;
          localObject12 = localObject4;
          localObject10 = ((Cursor)localObject4).getString(4);
          if (localObject10 == null)
          {
            localObject10 = "";
            label905:
            localObject1 = localThrowable;
            localObject11 = localObject4;
            localObject12 = localObject4;
            if (!isNotValidNameString((String)localObject10)) {
              break label1214;
            }
            localObject1 = localThrowable;
            localObject11 = localObject4;
            localObject12 = localObject4;
            localContact.first_name = ((String)localObject10);
            localObject1 = localThrowable;
            localObject11 = localObject4;
            localObject12 = localObject4;
          }
          for (localContact.last_name = "";; localContact.last_name = ((String)localObject10).substring(k + 1, ((String)localObject10).length()).trim())
          {
            localObject1 = localThrowable;
            localObject11 = localObject4;
            localObject12 = localObject4;
            localContact.provider = ((String)localObject6);
            localObject1 = localThrowable;
            localObject11 = localObject4;
            localObject12 = localObject4;
            localContact.isGoodProvider = bool;
            localObject1 = localThrowable;
            localObject11 = localObject4;
            localObject12 = localObject4;
            localContact.key = str;
            k = i + 1;
            localObject1 = localThrowable;
            localObject11 = localObject4;
            localObject12 = localObject4;
            localContact.contact_id = i;
            localObject1 = localThrowable;
            localObject11 = localObject4;
            localObject12 = localObject4;
            localThrowable.put(str, localContact);
            i = k;
            localObject6 = localContact;
            localObject1 = localThrowable;
            localObject11 = localObject4;
            localObject12 = localObject4;
            ((Contact)localObject6).shortPhones.add(localObject5);
            localObject1 = localThrowable;
            localObject11 = localObject4;
            localObject12 = localObject4;
            ((Contact)localObject6).phones.add(localObject7);
            localObject1 = localThrowable;
            localObject11 = localObject4;
            localObject12 = localObject4;
            ((Contact)localObject6).phoneDeleted.add(Integer.valueOf(0));
            if (j != 0) {
              break label1370;
            }
            localObject1 = localThrowable;
            localObject11 = localObject4;
            localObject12 = localObject4;
            localObject10 = ((Cursor)localObject4).getString(3);
            localObject1 = localThrowable;
            localObject11 = localObject4;
            localObject12 = localObject4;
            localObject7 = ((Contact)localObject6).phoneTypes;
            if (localObject10 == null) {
              break label1347;
            }
            localObject1 = localThrowable;
            localObject11 = localObject4;
            localObject12 = localObject4;
            ((ArrayList)localObject7).add(localObject10);
            localObject1 = localThrowable;
            localObject11 = localObject4;
            localObject12 = localObject4;
            localHashMap.put(localObject5, localObject6);
            break;
            localObject1 = localThrowable;
            localObject11 = localObject4;
            localObject12 = localObject4;
            localObject10 = ((String)localObject10).trim();
            break label905;
            label1214:
            localObject1 = localThrowable;
            localObject11 = localObject4;
            localObject12 = localObject4;
            k = ((String)localObject10).lastIndexOf(' ');
            if (k == -1) {
              break label1312;
            }
            localObject1 = localThrowable;
            localObject11 = localObject4;
            localObject12 = localObject4;
            localContact.first_name = ((String)localObject10).substring(0, k).trim();
            localObject1 = localThrowable;
            localObject11 = localObject4;
            localObject12 = localObject4;
          }
        }
      }
      finally
      {
        for (;;)
        {
          ContentResolver localContentResolver;
          ArrayList localArrayList;
          Contact localContact;
          if (localObject12 != null) {}
          label1312:
          label1347:
          label1370:
          label1545:
          label2488:
          int i = 1;
          Object localObject9 = localObject10;
        }
      }
    }
  }
  
  private void reloadContactsStatusesMaybe()
  {
    try
    {
      if (MessagesController.getMainSettings(this.currentAccount).getLong("lastReloadStatusTime", 0L) < System.currentTimeMillis() - 86400000L) {
        reloadContactsStatuses();
      }
      return;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e(localException);
      }
    }
  }
  
  private void saveContactsLoadTime()
  {
    try
    {
      MessagesController.getMainSettings(this.currentAccount).edit().putLong("lastReloadStatusTime", System.currentTimeMillis()).commit();
      return;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e(localException);
      }
    }
  }
  
  private void updateUnregisteredContacts(ArrayList<TLRPC.TL_contact> paramArrayList)
  {
    HashMap localHashMap = new HashMap();
    int i = 0;
    Object localObject2;
    if (i < paramArrayList.size())
    {
      localObject1 = (TLRPC.TL_contact)paramArrayList.get(i);
      localObject2 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(((TLRPC.TL_contact)localObject1).user_id));
      if ((localObject2 == null) || (TextUtils.isEmpty(((TLRPC.User)localObject2).phone))) {}
      for (;;)
      {
        i++;
        break;
        localHashMap.put(((TLRPC.User)localObject2).phone, localObject1);
      }
    }
    paramArrayList = new ArrayList();
    Object localObject1 = this.contactsBook.entrySet().iterator();
    if (((Iterator)localObject1).hasNext())
    {
      localObject2 = (Contact)((Map.Entry)((Iterator)localObject1).next()).getValue();
      int j = 0;
      for (i = 0;; i++)
      {
        int k = j;
        if (i < ((Contact)localObject2).phones.size())
        {
          if ((localHashMap.containsKey((String)((Contact)localObject2).shortPhones.get(i))) || (((Integer)((Contact)localObject2).phoneDeleted.get(i)).intValue() == 1)) {
            k = 1;
          }
        }
        else
        {
          if (k != 0) {
            break;
          }
          paramArrayList.add(localObject2);
          break;
        }
      }
    }
    Collections.sort(paramArrayList, new Comparator()
    {
      public int compare(ContactsController.Contact paramAnonymousContact1, ContactsController.Contact paramAnonymousContact2)
      {
        String str1 = paramAnonymousContact1.first_name;
        String str2 = str1;
        if (str1.length() == 0) {
          str2 = paramAnonymousContact1.last_name;
        }
        str1 = paramAnonymousContact2.first_name;
        paramAnonymousContact1 = str1;
        if (str1.length() == 0) {
          paramAnonymousContact1 = paramAnonymousContact2.last_name;
        }
        return str2.compareTo(paramAnonymousContact1);
      }
    });
    this.phoneBookContacts = paramArrayList;
  }
  
  public void addContact(TLRPC.User paramUser)
  {
    if ((paramUser == null) || (TextUtils.isEmpty(paramUser.phone))) {}
    for (;;)
    {
      return;
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
      ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_contacts_importContacts, new RequestDelegate()
      {
        public void run(final TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
        {
          if (paramAnonymousTL_error != null) {}
          for (;;)
          {
            return;
            paramAnonymousTLObject = (TLRPC.TL_contacts_importedContacts)paramAnonymousTLObject;
            MessagesStorage.getInstance(ContactsController.this.currentAccount).putUsersAndChats(paramAnonymousTLObject.users, null, true, true);
            for (int i = 0; i < paramAnonymousTLObject.users.size(); i++)
            {
              paramAnonymousTL_error = (TLRPC.User)paramAnonymousTLObject.users.get(i);
              Utilities.phoneBookQueue.postRunnable(new Runnable()
              {
                public void run()
                {
                  ContactsController.this.addContactToPhoneBook(paramAnonymousTL_error, true);
                }
              });
              TLRPC.TL_contact localTL_contact = new TLRPC.TL_contact();
              localTL_contact.user_id = paramAnonymousTL_error.id;
              Object localObject = new ArrayList();
              ((ArrayList)localObject).add(localTL_contact);
              MessagesStorage.getInstance(ContactsController.this.currentAccount).putContacts((ArrayList)localObject, false);
              if (!TextUtils.isEmpty(paramAnonymousTL_error.phone))
              {
                ContactsController.formatName(paramAnonymousTL_error.first_name, paramAnonymousTL_error.last_name);
                MessagesStorage.getInstance(ContactsController.this.currentAccount).applyPhoneBookUpdates(paramAnonymousTL_error.phone, "");
                localObject = (ContactsController.Contact)ContactsController.this.contactsBookSPhones.get(paramAnonymousTL_error.phone);
                if (localObject != null)
                {
                  int j = ((ContactsController.Contact)localObject).shortPhones.indexOf(paramAnonymousTL_error.phone);
                  if (j != -1) {
                    ((ContactsController.Contact)localObject).phoneDeleted.set(j, Integer.valueOf(0));
                  }
                }
              }
            }
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                Iterator localIterator = paramAnonymousTLObject.users.iterator();
                while (localIterator.hasNext())
                {
                  TLRPC.User localUser = (TLRPC.User)localIterator.next();
                  MessagesController.getInstance(ContactsController.this.currentAccount).putUser(localUser, false);
                  if (ContactsController.this.contactsDict.get(Integer.valueOf(localUser.id)) == null)
                  {
                    TLRPC.TL_contact localTL_contact = new TLRPC.TL_contact();
                    localTL_contact.user_id = localUser.id;
                    ContactsController.this.contacts.add(localTL_contact);
                    ContactsController.this.contactsDict.put(Integer.valueOf(localTL_contact.user_id), localTL_contact);
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
  
  public long addContactToPhoneBook(TLRPC.User paramUser, boolean paramBoolean)
  {
    l1 = -1L;
    l2 = l1;
    if (this.systemAccount != null)
    {
      l2 = l1;
      if (paramUser != null)
      {
        if (!TextUtils.isEmpty(paramUser.phone)) {
          break label37;
        }
        l2 = l1;
      }
    }
    label37:
    do
    {
      return l2;
      l2 = l1;
    } while (!hasContactsPermission());
    l1 = -1L;
    synchronized (this.observerLock)
    {
      this.ignoreChanges = true;
      ??? = ApplicationLoader.applicationContext.getContentResolver();
      if (!paramBoolean) {}
    }
    try
    {
      Object localObject2 = ContactsContract.RawContacts.CONTENT_URI.buildUpon().appendQueryParameter("caller_is_syncadapter", "true").appendQueryParameter("account_name", this.systemAccount.name).appendQueryParameter("account_type", this.systemAccount.type).build();
      Object localObject3 = new java/lang/StringBuilder;
      ((StringBuilder)localObject3).<init>();
      ((ContentResolver)???).delete((Uri)localObject2, "sync2 = " + paramUser.id, null);
      localObject2 = new ArrayList();
      localObject3 = ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI);
      ((ContentProviderOperation.Builder)localObject3).withValue("account_name", this.systemAccount.name);
      ((ContentProviderOperation.Builder)localObject3).withValue("account_type", this.systemAccount.type);
      ((ContentProviderOperation.Builder)localObject3).withValue("sync1", paramUser.phone);
      ((ContentProviderOperation.Builder)localObject3).withValue("sync2", Integer.valueOf(paramUser.id));
      ((ArrayList)localObject2).add(((ContentProviderOperation.Builder)localObject3).build());
      localObject3 = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI);
      ((ContentProviderOperation.Builder)localObject3).withValueBackReference("raw_contact_id", 0);
      ((ContentProviderOperation.Builder)localObject3).withValue("mimetype", "vnd.android.cursor.item/name");
      ((ContentProviderOperation.Builder)localObject3).withValue("data2", paramUser.first_name);
      ((ContentProviderOperation.Builder)localObject3).withValue("data3", paramUser.last_name);
      ((ArrayList)localObject2).add(((ContentProviderOperation.Builder)localObject3).build());
      localObject3 = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI);
      ((ContentProviderOperation.Builder)localObject3).withValueBackReference("raw_contact_id", 0);
      ((ContentProviderOperation.Builder)localObject3).withValue("mimetype", "vnd.android.cursor.item/vnd.org.telegram.messenger.android.profile");
      ((ContentProviderOperation.Builder)localObject3).withValue("data1", Integer.valueOf(paramUser.id));
      ((ContentProviderOperation.Builder)localObject3).withValue("data2", "Telegram Profile");
      ((ContentProviderOperation.Builder)localObject3).withValue("data3", "+" + paramUser.phone);
      ((ContentProviderOperation.Builder)localObject3).withValue("data4", Integer.valueOf(paramUser.id));
      ((ArrayList)localObject2).add(((ContentProviderOperation.Builder)localObject3).build());
      try
      {
        paramUser = ((ContentResolver)???).applyBatch("com.android.contacts", (ArrayList)localObject2);
        l2 = l1;
        if (paramUser != null)
        {
          l2 = l1;
          if (paramUser.length > 0)
          {
            l2 = l1;
            if (paramUser[0].uri != null) {
              l2 = Long.parseLong(paramUser[0].uri.getLastPathSegment());
            }
          }
        }
      }
      catch (Exception paramUser)
      {
        for (;;)
        {
          FileLog.e(paramUser);
          l2 = l1;
        }
      }
      synchronized (this.observerLock)
      {
        this.ignoreChanges = false;
      }
      paramUser = finally;
      throw paramUser;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e(localException);
      }
    }
  }
  
  public void checkAppAccount()
  {
    localAccountManager = AccountManager.get(ApplicationLoader.applicationContext);
    try
    {
      arrayOfAccount = localAccountManager.getAccountsByType("org.telegram.messenger");
      this.systemAccount = null;
      i = 0;
    }
    catch (Throwable localThrowable)
    {
      try
      {
        Object localObject;
        for (;;)
        {
          Account[] arrayOfAccount;
          int i;
          int j;
          int k;
          int m;
          TLRPC.User localUser;
          String str;
          StringBuilder localStringBuilder;
          localAccountManager.removeAccount(arrayOfAccount[i], null, null);
          i++;
          continue;
          k++;
        }
        localThrowable = localThrowable;
        if (!UserConfig.getInstance(this.currentAccount).isClientActivated()) {
          break label238;
        }
        readContacts();
        if (this.systemAccount != null) {
          break label238;
        }
        try
        {
          Account localAccount = new android/accounts/Account;
          localObject = new java/lang/StringBuilder;
          ((StringBuilder)localObject).<init>();
          localAccount.<init>("" + UserConfig.getInstance(this.currentAccount).getClientUserId(), "org.telegram.messenger");
          this.systemAccount = localAccount;
          localAccountManager.addAccountExplicitly(this.systemAccount, "", null);
          return;
        }
        catch (Exception localException1)
        {
          for (;;) {}
        }
      }
      catch (Exception localException2)
      {
        for (;;) {}
      }
    }
    if (i < arrayOfAccount.length)
    {
      localObject = arrayOfAccount[i];
      j = 0;
      k = 0;
      m = j;
      if (k < 3)
      {
        localUser = UserConfig.getInstance(k).getCurrentUser();
        if (localUser != null)
        {
          str = ((Account)localObject).name;
          localStringBuilder = new java/lang/StringBuilder;
          localStringBuilder.<init>();
          if (str.equals("" + localUser.id))
          {
            if (k == this.currentAccount) {
              this.systemAccount = ((Account)localObject);
            }
            m = 1;
          }
        }
      }
      else if (m != 0) {}
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
          if (BuildVars.LOGS_ENABLED) {
            FileLog.d("detected contacts change");
          }
          ContactsController.this.performSyncPhoneBook(ContactsController.this.getContactsCopy(ContactsController.this.contactsBook), true, false, true, false, true, false);
        }
      }
    });
  }
  
  public void checkInviteText()
  {
    Object localObject = MessagesController.getMainSettings(this.currentAccount);
    this.inviteLink = ((SharedPreferences)localObject).getString("invitelink", null);
    int i = ((SharedPreferences)localObject).getInt("invitelinktime", 0);
    if ((!this.updatingInviteLink) && ((this.inviteLink == null) || (Math.abs(System.currentTimeMillis() / 1000L - i) >= 86400L)))
    {
      this.updatingInviteLink = true;
      localObject = new TLRPC.TL_help_getInviteText();
      ConnectionsManager.getInstance(this.currentAccount).sendRequest((TLObject)localObject, new RequestDelegate()
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
                  ContactsController.access$202(ContactsController.this, false);
                  SharedPreferences.Editor localEditor = MessagesController.getMainSettings(ContactsController.this.currentAccount).edit();
                  localEditor.putString("invitelink", ContactsController.access$402(ContactsController.this, paramAnonymousTLObject.message));
                  localEditor.putInt("invitelinktime", (int)(System.currentTimeMillis() / 1000L));
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
    this.contactsByShortPhone.clear();
    this.loadingContacts = false;
    this.contactsSyncInProgress = false;
    this.contactsLoaded = false;
    this.contactsBookLoaded = false;
    this.lastContactsVersions = "";
    this.loadingDeleteInfo = 0;
    this.deleteAccountTTL = 0;
    this.loadingLastSeenInfo = 0;
    this.loadingGroupInfo = 0;
    this.loadingCallsInfo = 0;
    Utilities.globalQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        ContactsController.access$002(ContactsController.this, false);
        ContactsController.access$102(ContactsController.this, 0);
      }
    });
    this.privacyRules = null;
  }
  
  public void deleteContact(final ArrayList<TLRPC.User> paramArrayList)
  {
    if ((paramArrayList == null) || (paramArrayList.isEmpty())) {}
    for (;;)
    {
      return;
      TLRPC.TL_contacts_deleteContacts localTL_contacts_deleteContacts = new TLRPC.TL_contacts_deleteContacts();
      final ArrayList localArrayList = new ArrayList();
      Iterator localIterator = paramArrayList.iterator();
      while (localIterator.hasNext())
      {
        TLRPC.User localUser = (TLRPC.User)localIterator.next();
        TLRPC.InputUser localInputUser = MessagesController.getInstance(this.currentAccount).getInputUser(localUser);
        if (localInputUser != null)
        {
          localArrayList.add(Integer.valueOf(localUser.id));
          localTL_contacts_deleteContacts.id.add(localInputUser);
        }
      }
      ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_contacts_deleteContacts, new RequestDelegate()
      {
        public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
        {
          if (paramAnonymousTL_error != null) {}
          for (;;)
          {
            return;
            MessagesStorage.getInstance(ContactsController.this.currentAccount).deleteContacts(localArrayList);
            Utilities.phoneBookQueue.postRunnable(new Runnable()
            {
              public void run()
              {
                Iterator localIterator = ContactsController.22.this.val$users.iterator();
                while (localIterator.hasNext())
                {
                  TLRPC.User localUser = (TLRPC.User)localIterator.next();
                  ContactsController.this.deleteContactFromPhoneBook(localUser.id);
                }
              }
            });
            int i = 0;
            if (i < paramArrayList.size())
            {
              paramAnonymousTL_error = (TLRPC.User)paramArrayList.get(i);
              if (TextUtils.isEmpty(paramAnonymousTL_error.phone)) {}
              for (;;)
              {
                i++;
                break;
                UserObject.getUserName(paramAnonymousTL_error);
                MessagesStorage.getInstance(ContactsController.this.currentAccount).applyPhoneBookUpdates(paramAnonymousTL_error.phone, "");
                paramAnonymousTLObject = (ContactsController.Contact)ContactsController.this.contactsBookSPhones.get(paramAnonymousTL_error.phone);
                if (paramAnonymousTLObject != null)
                {
                  int j = paramAnonymousTLObject.shortPhones.indexOf(paramAnonymousTL_error.phone);
                  if (j != -1) {
                    paramAnonymousTLObject.phoneDeleted.set(j, Integer.valueOf(1));
                  }
                }
              }
            }
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                int i = 0;
                Iterator localIterator = ContactsController.22.this.val$users.iterator();
                while (localIterator.hasNext())
                {
                  TLRPC.User localUser = (TLRPC.User)localIterator.next();
                  TLRPC.TL_contact localTL_contact = (TLRPC.TL_contact)ContactsController.this.contactsDict.get(Integer.valueOf(localUser.id));
                  if (localTL_contact != null)
                  {
                    i = 1;
                    ContactsController.this.contacts.remove(localTL_contact);
                    ContactsController.this.contactsDict.remove(Integer.valueOf(localUser.id));
                  }
                }
                if (i != 0) {
                  ContactsController.this.buildContactsSectionsArrays(false);
                }
                NotificationCenter.getInstance(ContactsController.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(1) });
                NotificationCenter.getInstance(ContactsController.this.currentAccount).postNotificationName(NotificationCenter.contactsDidLoaded, new Object[0]);
              }
            });
          }
        }
      });
    }
  }
  
  /* Error */
  public void deleteUnknownAppAccounts()
  {
    // Byte code:
    //   0: aload_0
    //   1: aconst_null
    //   2: putfield 691	org/telegram/messenger/ContactsController:systemAccount	Landroid/accounts/Account;
    //   5: getstatic 626	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   8: invokestatic 1056	android/accounts/AccountManager:get	(Landroid/content/Context;)Landroid/accounts/AccountManager;
    //   11: astore_1
    //   12: aload_1
    //   13: ldc_w 1058
    //   16: invokevirtual 1062	android/accounts/AccountManager:getAccountsByType	(Ljava/lang/String;)[Landroid/accounts/Account;
    //   19: astore_2
    //   20: iconst_0
    //   21: istore_3
    //   22: iload_3
    //   23: aload_2
    //   24: arraylength
    //   25: if_icmpge +121 -> 146
    //   28: aload_2
    //   29: iload_3
    //   30: aaload
    //   31: astore 4
    //   33: iconst_0
    //   34: istore 5
    //   36: iconst_0
    //   37: istore 6
    //   39: iload 5
    //   41: istore 7
    //   43: iload 6
    //   45: iconst_3
    //   46: if_icmpge +68 -> 114
    //   49: iload 6
    //   51: invokestatic 736	org/telegram/messenger/UserConfig:getInstance	(I)Lorg/telegram/messenger/UserConfig;
    //   54: invokevirtual 1066	org/telegram/messenger/UserConfig:getCurrentUser	()Lorg/telegram/tgnet/TLRPC$User;
    //   57: astore 8
    //   59: aload 8
    //   61: ifnull +74 -> 135
    //   64: aload 4
    //   66: getfield 696	android/accounts/Account:name	Ljava/lang/String;
    //   69: astore 9
    //   71: new 484	java/lang/StringBuilder
    //   74: astore 10
    //   76: aload 10
    //   78: invokespecial 485	java/lang/StringBuilder:<init>	()V
    //   81: aload 9
    //   83: aload 10
    //   85: ldc -64
    //   87: invokevirtual 491	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   90: aload 8
    //   92: getfield 1001	org/telegram/tgnet/TLRPC$User:id	I
    //   95: invokevirtual 494	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   98: invokevirtual 500	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   101: invokevirtual 669	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   104: istore 11
    //   106: iload 11
    //   108: ifeq +27 -> 135
    //   111: iconst_1
    //   112: istore 7
    //   114: iload 7
    //   116: ifne +13 -> 129
    //   119: aload_1
    //   120: aload_2
    //   121: iload_3
    //   122: aaload
    //   123: aconst_null
    //   124: aconst_null
    //   125: invokevirtual 1070	android/accounts/AccountManager:removeAccount	(Landroid/accounts/Account;Landroid/accounts/AccountManagerCallback;Landroid/os/Handler;)Landroid/accounts/AccountManagerFuture;
    //   128: pop
    //   129: iinc 3 1
    //   132: goto -110 -> 22
    //   135: iinc 6 1
    //   138: goto -99 -> 39
    //   141: astore_2
    //   142: aload_2
    //   143: invokevirtual 1142	java/lang/Exception:printStackTrace	()V
    //   146: return
    //   147: astore 4
    //   149: goto -20 -> 129
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	152	0	this	ContactsController
    //   11	109	1	localAccountManager	AccountManager
    //   19	102	2	arrayOfAccount	Account[]
    //   141	2	2	localException1	Exception
    //   21	109	3	i	int
    //   31	34	4	localAccount	Account
    //   147	1	4	localException2	Exception
    //   34	6	5	j	int
    //   37	99	6	k	int
    //   41	74	7	m	int
    //   57	34	8	localUser	TLRPC.User
    //   69	13	9	str	String
    //   74	10	10	localStringBuilder	StringBuilder
    //   104	3	11	bool	boolean
    // Exception table:
    //   from	to	target	type
    //   0	20	141	java/lang/Exception
    //   22	28	141	java/lang/Exception
    //   49	59	141	java/lang/Exception
    //   64	106	141	java/lang/Exception
    //   119	129	147	java/lang/Exception
  }
  
  public void forceImportContacts()
  {
    Utilities.globalQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        if (BuildVars.LOGS_ENABLED) {
          FileLog.d("force import contacts");
        }
        ContactsController.this.performSyncPhoneBook(new HashMap(), true, true, true, true, false, false);
      }
    });
  }
  
  public HashMap<String, Contact> getContactsCopy(HashMap<String, Contact> paramHashMap)
  {
    HashMap localHashMap = new HashMap();
    Iterator localIterator = paramHashMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Object localObject = (Map.Entry)localIterator.next();
      paramHashMap = new Contact();
      localObject = (Contact)((Map.Entry)localObject).getValue();
      paramHashMap.phoneDeleted.addAll(((Contact)localObject).phoneDeleted);
      paramHashMap.phones.addAll(((Contact)localObject).phones);
      paramHashMap.phoneTypes.addAll(((Contact)localObject).phoneTypes);
      paramHashMap.shortPhones.addAll(((Contact)localObject).shortPhones);
      paramHashMap.first_name = ((Contact)localObject).first_name;
      paramHashMap.last_name = ((Contact)localObject).last_name;
      paramHashMap.contact_id = ((Contact)localObject).contact_id;
      paramHashMap.key = ((Contact)localObject).key;
      localHashMap.put(paramHashMap.key, paramHashMap);
    }
    return localHashMap;
  }
  
  public int getDeleteAccountTTL()
  {
    return this.deleteAccountTTL;
  }
  
  public String getInviteText(int paramInt)
  {
    Object localObject;
    if (this.inviteLink == null)
    {
      localObject = "https://telegram.org/dl";
      if (paramInt > 1) {
        break label44;
      }
      localObject = LocaleController.formatString("InviteText2", NUM, new Object[] { localObject });
    }
    for (;;)
    {
      return (String)localObject;
      localObject = this.inviteLink;
      break;
      try
      {
        label44:
        String str = String.format(LocaleController.getPluralString("InviteTextNum", paramInt), new Object[] { Integer.valueOf(paramInt), localObject });
        localObject = str;
      }
      catch (Exception localException)
      {
        localObject = LocaleController.formatString("InviteText2", NUM, new Object[] { localObject });
      }
    }
  }
  
  public boolean getLoadingCallsInfo()
  {
    if (this.loadingCallsInfo != 2) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean getLoadingDeleteInfo()
  {
    if (this.loadingDeleteInfo != 2) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean getLoadingGroupInfo()
  {
    if (this.loadingGroupInfo != 2) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean getLoadingLastSeenInfo()
  {
    if (this.loadingLastSeenInfo != 2) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public ArrayList<TLRPC.PrivacyRule> getPrivacyRules(int paramInt)
  {
    ArrayList localArrayList;
    if (paramInt == 2) {
      localArrayList = this.callPrivacyRules;
    }
    for (;;)
    {
      return localArrayList;
      if (paramInt == 1) {
        localArrayList = this.groupPrivacyRules;
      } else {
        localArrayList = this.privacyRules;
      }
    }
  }
  
  public boolean isLoadingContacts()
  {
    synchronized (this.loadContactsSync)
    {
      boolean bool = this.loadingContacts;
      return bool;
    }
  }
  
  public void loadContacts(boolean paramBoolean, final int paramInt)
  {
    for (;;)
    {
      synchronized (this.loadContactsSync)
      {
        this.loadingContacts = true;
        if (paramBoolean)
        {
          if (BuildVars.LOGS_ENABLED) {
            FileLog.d("load contacts from cache");
          }
          MessagesStorage.getInstance(this.currentAccount).getContacts();
          return;
        }
      }
      if (BuildVars.LOGS_ENABLED) {
        FileLog.d("load contacts from server");
      }
      TLRPC.TL_contacts_getContacts localTL_contacts_getContacts = new TLRPC.TL_contacts_getContacts();
      localTL_contacts_getContacts.hash = paramInt;
      ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_contacts_getContacts, new RequestDelegate()
      {
        public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
        {
          if (paramAnonymousTL_error == null)
          {
            paramAnonymousTLObject = (TLRPC.contacts_Contacts)paramAnonymousTLObject;
            if ((paramInt == 0) || (!(paramAnonymousTLObject instanceof TLRPC.TL_contacts_contactsNotModified))) {
              break label139;
            }
            ContactsController.this.contactsLoaded = true;
            if ((!ContactsController.this.delayedContactsUpdate.isEmpty()) && (ContactsController.this.contactsBookLoaded))
            {
              ContactsController.this.applyContactsUpdates(ContactsController.this.delayedContactsUpdate, null, null, null);
              ContactsController.this.delayedContactsUpdate.clear();
            }
            UserConfig.getInstance(ContactsController.this.currentAccount).lastContactsSyncTime = ((int)(System.currentTimeMillis() / 1000L));
            UserConfig.getInstance(ContactsController.this.currentAccount).saveConfig(false);
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                synchronized (ContactsController.this.loadContactsSync)
                {
                  ContactsController.access$702(ContactsController.this, false);
                  NotificationCenter.getInstance(ContactsController.this.currentAccount).postNotificationName(NotificationCenter.contactsDidLoaded, new Object[0]);
                  return;
                }
              }
            });
            if (BuildVars.LOGS_ENABLED) {
              FileLog.d("load contacts don't change");
            }
          }
          for (;;)
          {
            return;
            label139:
            UserConfig.getInstance(ContactsController.this.currentAccount).contactsSavedCount = paramAnonymousTLObject.saved_count;
            UserConfig.getInstance(ContactsController.this.currentAccount).saveConfig(false);
            ContactsController.this.processLoadedContacts(paramAnonymousTLObject.contacts, paramAnonymousTLObject.users, 0);
          }
        }
      });
    }
  }
  
  public void loadPrivacySettings()
  {
    Object localObject;
    if (this.loadingDeleteInfo == 0)
    {
      this.loadingDeleteInfo = 1;
      localObject = new TLRPC.TL_account_getAccountTTL();
      ConnectionsManager.getInstance(this.currentAccount).sendRequest((TLObject)localObject, new RequestDelegate()
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
                ContactsController.access$2202(ContactsController.this, localTL_accountDaysTTL.days);
                ContactsController.access$2302(ContactsController.this, 2);
              }
              for (;;)
              {
                NotificationCenter.getInstance(ContactsController.this.currentAccount).postNotificationName(NotificationCenter.privacyRulesUpdated, new Object[0]);
                return;
                ContactsController.access$2302(ContactsController.this, 0);
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
      ConnectionsManager.getInstance(this.currentAccount).sendRequest((TLObject)localObject, new RequestDelegate()
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
                MessagesController.getInstance(ContactsController.this.currentAccount).putUsers(localTL_account_privacyRules.users, false);
                ContactsController.access$2402(ContactsController.this, localTL_account_privacyRules.rules);
                ContactsController.access$2502(ContactsController.this, 2);
              }
              for (;;)
              {
                NotificationCenter.getInstance(ContactsController.this.currentAccount).postNotificationName(NotificationCenter.privacyRulesUpdated, new Object[0]);
                return;
                ContactsController.access$2502(ContactsController.this, 0);
              }
            }
          });
        }
      });
    }
    if (this.loadingCallsInfo == 0)
    {
      this.loadingCallsInfo = 1;
      localObject = new TLRPC.TL_account_getPrivacy();
      ((TLRPC.TL_account_getPrivacy)localObject).key = new TLRPC.TL_inputPrivacyKeyPhoneCall();
      ConnectionsManager.getInstance(this.currentAccount).sendRequest((TLObject)localObject, new RequestDelegate()
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
                MessagesController.getInstance(ContactsController.this.currentAccount).putUsers(localTL_account_privacyRules.users, false);
                ContactsController.access$2602(ContactsController.this, localTL_account_privacyRules.rules);
                ContactsController.access$2702(ContactsController.this, 2);
              }
              for (;;)
              {
                NotificationCenter.getInstance(ContactsController.this.currentAccount).postNotificationName(NotificationCenter.privacyRulesUpdated, new Object[0]);
                return;
                ContactsController.access$2702(ContactsController.this, 0);
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
      ConnectionsManager.getInstance(this.currentAccount).sendRequest((TLObject)localObject, new RequestDelegate()
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
                MessagesController.getInstance(ContactsController.this.currentAccount).putUsers(localTL_account_privacyRules.users, false);
                ContactsController.access$2802(ContactsController.this, localTL_account_privacyRules.rules);
                ContactsController.access$2902(ContactsController.this, 2);
              }
              for (;;)
              {
                NotificationCenter.getInstance(ContactsController.this.currentAccount).postNotificationName(NotificationCenter.privacyRulesUpdated, new Object[0]);
                return;
                ContactsController.access$2902(ContactsController.this, 0);
              }
            }
          });
        }
      });
    }
    NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.privacyRulesUpdated, new Object[0]);
  }
  
  protected void markAsContacted(final String paramString)
  {
    if (paramString == null) {}
    for (;;)
    {
      return;
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
  }
  
  protected void migratePhoneBookToV7(final SparseArray<Contact> paramSparseArray)
  {
    Utilities.globalQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        if (ContactsController.this.migratingContacts) {}
        for (;;)
        {
          return;
          ContactsController.access$002(ContactsController.this, true);
          HashMap localHashMap1 = new HashMap();
          Object localObject = ContactsController.this.readContactsFromPhoneBook();
          HashMap localHashMap2 = new HashMap();
          localObject = ((HashMap)localObject).entrySet().iterator();
          ContactsController.Contact localContact;
          while (((Iterator)localObject).hasNext())
          {
            localContact = (ContactsController.Contact)((Map.Entry)((Iterator)localObject).next()).getValue();
            for (i = 0; i < localContact.shortPhones.size(); i++) {
              localHashMap2.put(localContact.shortPhones.get(i), localContact.key);
            }
          }
          int i = 0;
          if (i < paramSparseArray.size())
          {
            localContact = (ContactsController.Contact)paramSparseArray.valueAt(i);
            for (int j = 0;; j++) {
              if (j < localContact.shortPhones.size())
              {
                localObject = (String)localHashMap2.get((String)localContact.shortPhones.get(j));
                if (localObject != null)
                {
                  localContact.key = ((String)localObject);
                  localHashMap1.put(localObject, localContact);
                }
              }
              else
              {
                i++;
                break;
              }
            }
          }
          if (BuildVars.LOGS_ENABLED) {
            FileLog.d("migrated contacts " + localHashMap1.size() + " of " + paramSparseArray.size());
          }
          MessagesStorage.getInstance(ContactsController.this.currentAccount).putCachedPhoneBook(localHashMap1, true);
        }
      }
    });
  }
  
  protected void performSyncPhoneBook(final HashMap<String, Contact> paramHashMap, final boolean paramBoolean1, final boolean paramBoolean2, final boolean paramBoolean3, final boolean paramBoolean4, final boolean paramBoolean5, final boolean paramBoolean6)
  {
    if ((!paramBoolean2) && (!this.contactsBookLoaded)) {}
    for (;;)
    {
      return;
      Utilities.globalQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          int i = 0;
          int j = 0;
          int k = 0;
          final int m = 0;
          int n = 0;
          HashMap localHashMap1 = new HashMap();
          final Object localObject1 = paramHashMap.entrySet().iterator();
          final Object localObject2;
          final int i1;
          while (((Iterator)localObject1).hasNext())
          {
            localObject2 = (ContactsController.Contact)((Map.Entry)((Iterator)localObject1).next()).getValue();
            for (i1 = 0; i1 < ((ContactsController.Contact)localObject2).shortPhones.size(); i1++) {
              localHashMap1.put(((ContactsController.Contact)localObject2).shortPhones.get(i1), localObject2);
            }
          }
          if (BuildVars.LOGS_ENABLED) {
            FileLog.d("start read contacts from phone");
          }
          if (!paramBoolean3) {
            ContactsController.this.checkContactsInternal();
          }
          final HashMap localHashMap2 = ContactsController.this.readContactsFromPhoneBook();
          final HashMap localHashMap3 = new HashMap();
          int i2 = paramHashMap.size();
          ArrayList localArrayList = new ArrayList();
          final Object localObject3;
          Object localObject4;
          if (!paramHashMap.isEmpty())
          {
            Iterator localIterator = localHashMap2.entrySet().iterator();
            i1 = n;
            m = j;
            while (localIterator.hasNext())
            {
              localObject1 = (Map.Entry)localIterator.next();
              localObject3 = (String)((Map.Entry)localObject1).getKey();
              ContactsController.Contact localContact = (ContactsController.Contact)((Map.Entry)localObject1).getValue();
              localObject4 = (ContactsController.Contact)paramHashMap.get(localObject3);
              localObject2 = localObject4;
              localObject1 = localObject3;
              if (localObject4 == null)
              {
                j = 0;
                localObject2 = localObject4;
                localObject1 = localObject3;
                if (j < localContact.shortPhones.size())
                {
                  localObject2 = (ContactsController.Contact)localHashMap1.get(localContact.shortPhones.get(j));
                  if (localObject2 == null) {
                    break label552;
                  }
                  localObject1 = ((ContactsController.Contact)localObject2).key;
                }
              }
              if (localObject2 != null) {
                localContact.imported = ((ContactsController.Contact)localObject2).imported;
              }
              if ((localObject2 != null) && (((!TextUtils.isEmpty(localContact.first_name)) && (!((ContactsController.Contact)localObject2).first_name.equals(localContact.first_name))) || ((!TextUtils.isEmpty(localContact.last_name)) && (!((ContactsController.Contact)localObject2).last_name.equals(localContact.last_name)))))
              {
                n = 1;
                label399:
                if ((localObject2 != null) && (n == 0)) {
                  break label732;
                }
                k = 0;
                i = i1;
                j = m;
                m = k;
                label420:
                if (m >= localContact.phones.size()) {
                  break label702;
                }
                localObject3 = (String)localContact.shortPhones.get(m);
                ((String)localObject3).substring(Math.max(0, ((String)localObject3).length() - 7));
                localHashMap3.put(localObject3, localContact);
                if (localObject2 == null) {
                  break label564;
                }
                i1 = ((ContactsController.Contact)localObject2).shortPhones.indexOf(localObject3);
                if (i1 == -1) {
                  break label564;
                }
                localObject4 = (Integer)((ContactsController.Contact)localObject2).phoneDeleted.get(i1);
                localContact.phoneDeleted.set(m, localObject4);
                if (((Integer)localObject4).intValue() != 1) {
                  break label564;
                }
                k = i;
                i1 = j;
              }
              for (;;)
              {
                m++;
                j = i1;
                i = k;
                break label420;
                label552:
                j++;
                break;
                n = 0;
                break label399;
                label564:
                i1 = j;
                k = i;
                if (paramBoolean1)
                {
                  i1 = j;
                  if (n == 0)
                  {
                    if (ContactsController.this.contactsByPhone.containsKey(localObject3))
                    {
                      k = i + 1;
                      i1 = j;
                    }
                    else
                    {
                      i1 = j + 1;
                    }
                  }
                  else
                  {
                    localObject3 = new TLRPC.TL_inputPhoneContact();
                    ((TLRPC.TL_inputPhoneContact)localObject3).client_id = localContact.contact_id;
                    ((TLRPC.TL_inputPhoneContact)localObject3).client_id |= m << 32;
                    ((TLRPC.TL_inputPhoneContact)localObject3).first_name = localContact.first_name;
                    ((TLRPC.TL_inputPhoneContact)localObject3).last_name = localContact.last_name;
                    ((TLRPC.TL_inputPhoneContact)localObject3).phone = ((String)localContact.phones.get(m));
                    localArrayList.add(localObject3);
                    k = i;
                  }
                }
              }
              label702:
              m = j;
              i1 = i;
              if (localObject2 != null)
              {
                paramHashMap.remove(localObject1);
                m = j;
                i1 = i;
                continue;
                label732:
                i = 0;
                n = i1;
                j = m;
                if (i < localContact.phones.size())
                {
                  localObject4 = (String)localContact.shortPhones.get(i);
                  localObject3 = ((String)localObject4).substring(Math.max(0, ((String)localObject4).length() - 7));
                  localHashMap3.put(localObject4, localContact);
                  int i3 = ((ContactsController.Contact)localObject2).shortPhones.indexOf(localObject4);
                  int i4 = 0;
                  k = i4;
                  m = i3;
                  i1 = n;
                  if (paramBoolean1)
                  {
                    Object localObject5 = (TLRPC.TL_contact)ContactsController.this.contactsByPhone.get(localObject4);
                    if (localObject5 == null) {
                      break label1182;
                    }
                    localObject5 = MessagesController.getInstance(ContactsController.this.currentAccount).getUser(Integer.valueOf(((TLRPC.TL_contact)localObject5).user_id));
                    k = i4;
                    m = i3;
                    i1 = n;
                    if (localObject5 != null)
                    {
                      n++;
                      k = i4;
                      m = i3;
                      i1 = n;
                      if (TextUtils.isEmpty(((TLRPC.User)localObject5).first_name))
                      {
                        k = i4;
                        m = i3;
                        i1 = n;
                        if (TextUtils.isEmpty(((TLRPC.User)localObject5).last_name)) {
                          if (TextUtils.isEmpty(localContact.first_name))
                          {
                            k = i4;
                            m = i3;
                            i1 = n;
                            if (TextUtils.isEmpty(localContact.last_name)) {}
                          }
                          else
                          {
                            m = -1;
                            k = 1;
                            i1 = n;
                          }
                        }
                      }
                    }
                  }
                  label979:
                  if (m == -1)
                  {
                    n = j;
                    m = i1;
                    if (paramBoolean1)
                    {
                      n = j;
                      m = i1;
                      if (k != 0) {
                        break label1249;
                      }
                      localObject4 = (TLRPC.TL_contact)ContactsController.this.contactsByPhone.get(localObject4);
                      if (localObject4 == null) {
                        break label1333;
                      }
                      localObject4 = MessagesController.getInstance(ContactsController.this.currentAccount).getUser(Integer.valueOf(((TLRPC.TL_contact)localObject4).user_id));
                      if (localObject4 == null) {
                        break label1240;
                      }
                      i1++;
                      if (((TLRPC.User)localObject4).first_name == null) {
                        break label1224;
                      }
                      localObject3 = ((TLRPC.User)localObject4).first_name;
                      label1078:
                      if (((TLRPC.User)localObject4).last_name == null) {
                        break label1232;
                      }
                      localObject4 = ((TLRPC.User)localObject4).last_name;
                      label1093:
                      if (((String)localObject3).equals(localContact.first_name))
                      {
                        n = j;
                        m = i1;
                        if (((String)localObject4).equals(localContact.last_name)) {}
                      }
                      else
                      {
                        n = j;
                        m = i1;
                        if (!TextUtils.isEmpty(localContact.first_name)) {
                          break label1249;
                        }
                        n = j;
                        m = i1;
                        if (!TextUtils.isEmpty(localContact.last_name)) {
                          break label1249;
                        }
                        m = i1;
                        n = j;
                      }
                    }
                  }
                  for (;;)
                  {
                    i++;
                    j = n;
                    n = m;
                    break;
                    label1182:
                    k = i4;
                    m = i3;
                    i1 = n;
                    if (!ContactsController.this.contactsByShortPhone.containsKey(localObject3)) {
                      break label979;
                    }
                    i1 = n + 1;
                    k = i4;
                    m = i3;
                    break label979;
                    label1224:
                    localObject3 = "";
                    break label1078;
                    label1232:
                    localObject4 = "";
                    break label1093;
                    label1240:
                    n = j + 1;
                    m = i1;
                    for (;;)
                    {
                      label1249:
                      localObject3 = new TLRPC.TL_inputPhoneContact();
                      ((TLRPC.TL_inputPhoneContact)localObject3).client_id = localContact.contact_id;
                      ((TLRPC.TL_inputPhoneContact)localObject3).client_id |= i << 32;
                      ((TLRPC.TL_inputPhoneContact)localObject3).first_name = localContact.first_name;
                      ((TLRPC.TL_inputPhoneContact)localObject3).last_name = localContact.last_name;
                      ((TLRPC.TL_inputPhoneContact)localObject3).phone = ((String)localContact.phones.get(i));
                      localArrayList.add(localObject3);
                      break;
                      label1333:
                      n = j;
                      m = i1;
                      if (ContactsController.this.contactsByShortPhone.containsKey(localObject3))
                      {
                        m = i1 + 1;
                        n = j;
                      }
                    }
                    localContact.phoneDeleted.set(i, ((ContactsController.Contact)localObject2).phoneDeleted.get(m));
                    ((ContactsController.Contact)localObject2).phones.remove(m);
                    ((ContactsController.Contact)localObject2).shortPhones.remove(m);
                    ((ContactsController.Contact)localObject2).phoneDeleted.remove(m);
                    ((ContactsController.Contact)localObject2).phoneTypes.remove(m);
                    n = j;
                    m = i1;
                  }
                }
                m = j;
                i1 = n;
                if (((ContactsController.Contact)localObject2).phones.isEmpty())
                {
                  paramHashMap.remove(localObject1);
                  m = j;
                  i1 = n;
                }
              }
            }
            if ((!paramBoolean2) && (paramHashMap.isEmpty()) && (localArrayList.isEmpty()) && (i2 == localHashMap2.size())) {
              if (BuildVars.LOGS_ENABLED) {
                FileLog.d("contacts not changed!");
              }
            }
          }
          for (;;)
          {
            return;
            n = m;
            j = i1;
            if (paramBoolean1)
            {
              n = m;
              j = i1;
              if (!paramHashMap.isEmpty())
              {
                n = m;
                j = i1;
                if (!localHashMap2.isEmpty())
                {
                  if (localArrayList.isEmpty()) {
                    MessagesStorage.getInstance(ContactsController.this.currentAccount).putCachedPhoneBook(localHashMap2, false);
                  }
                  n = m;
                  j = i1;
                  if (1 == 0)
                  {
                    n = m;
                    j = i1;
                    if (!paramHashMap.isEmpty())
                    {
                      AndroidUtilities.runOnUIThread(new Runnable()
                      {
                        public void run()
                        {
                          ArrayList localArrayList = new ArrayList();
                          int i;
                          Object localObject;
                          if ((ContactsController.9.this.val$contactHashMap != null) && (!ContactsController.9.this.val$contactHashMap.isEmpty())) {
                            try
                            {
                              HashMap localHashMap = new java/util/HashMap;
                              localHashMap.<init>();
                              i = 0;
                              if (i < ContactsController.this.contacts.size())
                              {
                                localObject = (TLRPC.TL_contact)ContactsController.this.contacts.get(i);
                                localObject = MessagesController.getInstance(ContactsController.this.currentAccount).getUser(Integer.valueOf(((TLRPC.TL_contact)localObject).user_id));
                                if ((localObject == null) || (TextUtils.isEmpty(((TLRPC.User)localObject).phone))) {}
                                for (;;)
                                {
                                  i++;
                                  break;
                                  localHashMap.put(((TLRPC.User)localObject).phone, localObject);
                                }
                                if (localArrayList.isEmpty()) {}
                              }
                            }
                            catch (Exception localException)
                            {
                              FileLog.e(localException);
                            }
                          }
                          for (;;)
                          {
                            ContactsController.this.deleteContact(localArrayList);
                            return;
                            int j = 0;
                            Iterator localIterator = ContactsController.9.this.val$contactHashMap.entrySet().iterator();
                            while (localIterator.hasNext())
                            {
                              localObject = (ContactsController.Contact)((Map.Entry)localIterator.next()).getValue();
                              int k = 0;
                              int m;
                              for (i = 0; i < ((ContactsController.Contact)localObject).shortPhones.size(); i = m + 1)
                              {
                                TLRPC.User localUser = (TLRPC.User)localException.get((String)((ContactsController.Contact)localObject).shortPhones.get(i));
                                m = i;
                                if (localUser != null)
                                {
                                  k = 1;
                                  localArrayList.add(localUser);
                                  ((ContactsController.Contact)localObject).shortPhones.remove(i);
                                  m = i - 1;
                                }
                              }
                              if (k != 0)
                              {
                                i = ((ContactsController.Contact)localObject).shortPhones.size();
                                if (i != 0) {}
                              }
                              else
                              {
                                j++;
                              }
                            }
                          }
                        }
                      });
                      j = i1;
                      n = m;
                    }
                  }
                }
              }
            }
            label1643:
            if (BuildVars.LOGS_ENABLED) {
              FileLog.d("done processing contacts");
            }
            if (paramBoolean1)
            {
              if (!localArrayList.isEmpty())
              {
                if (BuildVars.LOGS_ENABLED) {
                  FileLog.e("start import contacts");
                }
                if ((paramBoolean5) && (n != 0)) {
                  if (n >= 30) {
                    i1 = 1;
                  }
                }
                for (;;)
                {
                  if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("new phone book contacts " + n + " serverContactsInPhonebook " + j + " totalContacts " + ContactsController.this.contactsByPhone.size());
                  }
                  if (i1 == 0) {
                    break label2275;
                  }
                  AndroidUtilities.runOnUIThread(new Runnable()
                  {
                    public void run()
                    {
                      NotificationCenter.getInstance(ContactsController.this.currentAccount).postNotificationName(NotificationCenter.hasNewContactsToImport, new Object[] { Integer.valueOf(i1), ContactsController.9.this.val$contactHashMap, Boolean.valueOf(ContactsController.9.this.val$first), Boolean.valueOf(ContactsController.9.this.val$schedule) });
                    }
                  });
                  break;
                  n = i;
                  j = k;
                  if (!paramBoolean1) {
                    break label1643;
                  }
                  localObject3 = localHashMap2.entrySet().iterator();
                  i1 = m;
                  do
                  {
                    n = i;
                    j = i1;
                    if (!((Iterator)localObject3).hasNext()) {
                      break;
                    }
                    localObject1 = (Map.Entry)((Iterator)localObject3).next();
                    localObject4 = (ContactsController.Contact)((Map.Entry)localObject1).getValue();
                    localObject1 = (String)((Map.Entry)localObject1).getKey();
                    j = 0;
                    m = i1;
                    i1 = m;
                  } while (j >= ((ContactsController.Contact)localObject4).phones.size());
                  i1 = m;
                  if (!paramBoolean4)
                  {
                    localObject2 = (String)((ContactsController.Contact)localObject4).shortPhones.get(j);
                    localObject1 = ((String)localObject2).substring(Math.max(0, ((String)localObject2).length() - 7));
                    localObject2 = (TLRPC.TL_contact)ContactsController.this.contactsByPhone.get(localObject2);
                    if (localObject2 != null)
                    {
                      localObject2 = MessagesController.getInstance(ContactsController.this.currentAccount).getUser(Integer.valueOf(((TLRPC.TL_contact)localObject2).user_id));
                      i1 = m;
                      if (localObject2 == null) {
                        break label2132;
                      }
                      m++;
                      if (((TLRPC.User)localObject2).first_name != null)
                      {
                        localObject1 = ((TLRPC.User)localObject2).first_name;
                        label2002:
                        if (((TLRPC.User)localObject2).last_name == null) {
                          break label2099;
                        }
                        localObject2 = ((TLRPC.User)localObject2).last_name;
                        label2017:
                        if (((String)localObject1).equals(((ContactsController.Contact)localObject4).first_name))
                        {
                          i1 = m;
                          if (((String)localObject2).equals(((ContactsController.Contact)localObject4).last_name)) {}
                        }
                        else
                        {
                          i1 = m;
                          if (!TextUtils.isEmpty(((ContactsController.Contact)localObject4).first_name)) {
                            break label2132;
                          }
                          i1 = m;
                          if (!TextUtils.isEmpty(((ContactsController.Contact)localObject4).last_name)) {
                            break label2132;
                          }
                          i1 = m;
                        }
                      }
                    }
                  }
                  for (;;)
                  {
                    j++;
                    m = i1;
                    break;
                    localObject1 = "";
                    break label2002;
                    label2099:
                    localObject2 = "";
                    break label2017;
                    i1 = m;
                    if (ContactsController.this.contactsByShortPhone.containsKey(localObject1)) {
                      i1 = m + 1;
                    }
                    label2132:
                    localObject1 = new TLRPC.TL_inputPhoneContact();
                    ((TLRPC.TL_inputPhoneContact)localObject1).client_id = ((ContactsController.Contact)localObject4).contact_id;
                    ((TLRPC.TL_inputPhoneContact)localObject1).client_id |= j << 32;
                    ((TLRPC.TL_inputPhoneContact)localObject1).first_name = ((ContactsController.Contact)localObject4).first_name;
                    ((TLRPC.TL_inputPhoneContact)localObject1).last_name = ((ContactsController.Contact)localObject4).last_name;
                    ((TLRPC.TL_inputPhoneContact)localObject1).phone = ((String)((ContactsController.Contact)localObject4).phones.get(j));
                    localArrayList.add(localObject1);
                  }
                  if ((paramBoolean2) && (i2 == 0) && (ContactsController.this.contactsByPhone.size() - j > ContactsController.this.contactsByPhone.size() / 3 * 2))
                  {
                    i1 = 2;
                  }
                  else
                  {
                    i1 = 0;
                    continue;
                    i1 = 0;
                  }
                }
                label2275:
                if (paramBoolean6)
                {
                  Utilities.stageQueue.postRunnable(new Runnable()
                  {
                    public void run()
                    {
                      ContactsController.this.contactsBookSPhones = localHashMap3;
                      ContactsController.this.contactsBook = localHashMap2;
                      ContactsController.access$902(ContactsController.this, false);
                      ContactsController.access$1002(ContactsController.this, true);
                      if (ContactsController.9.this.val$first) {
                        ContactsController.this.contactsLoaded = true;
                      }
                      if ((!ContactsController.this.delayedContactsUpdate.isEmpty()) && (ContactsController.this.contactsLoaded))
                      {
                        ContactsController.this.applyContactsUpdates(ContactsController.this.delayedContactsUpdate, null, null, null);
                        ContactsController.this.delayedContactsUpdate.clear();
                      }
                      MessagesStorage.getInstance(ContactsController.this.currentAccount).putCachedPhoneBook(localHashMap2, false);
                      AndroidUtilities.runOnUIThread(new Runnable()
                      {
                        public void run()
                        {
                          ContactsController.this.updateUnregisteredContacts(ContactsController.this.contacts);
                          NotificationCenter.getInstance(ContactsController.this.currentAccount).postNotificationName(NotificationCenter.contactsDidLoaded, new Object[0]);
                          NotificationCenter.getInstance(ContactsController.this.currentAccount).postNotificationName(NotificationCenter.contactsImported, new Object[0]);
                        }
                      });
                    }
                  });
                }
                else
                {
                  localObject1 = new HashMap(localHashMap2);
                  localObject2 = new SparseArray();
                  localObject3 = ((HashMap)localObject1).entrySet().iterator();
                  while (((Iterator)localObject3).hasNext())
                  {
                    localObject4 = (ContactsController.Contact)((Map.Entry)((Iterator)localObject3).next()).getValue();
                    ((SparseArray)localObject2).put(((ContactsController.Contact)localObject4).contact_id, ((ContactsController.Contact)localObject4).key);
                  }
                  ContactsController.access$102(ContactsController.this, 0);
                  m = (int)Math.ceil(localArrayList.size() / 500.0D);
                  for (i1 = 0; i1 < m; i1++)
                  {
                    localObject3 = new TLRPC.TL_contacts_importContacts();
                    j = i1 * 500;
                    ((TLRPC.TL_contacts_importContacts)localObject3).contacts = new ArrayList(localArrayList.subList(j, Math.min(j + 500, localArrayList.size())));
                    ConnectionsManager.getInstance(ContactsController.this.currentAccount).sendRequest((TLObject)localObject3, new RequestDelegate()
                    {
                      public void run(TLObject paramAnonymous2TLObject, TLRPC.TL_error paramAnonymous2TL_error)
                      {
                        ContactsController.access$108(ContactsController.this);
                        int i;
                        if (paramAnonymous2TL_error == null)
                        {
                          if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("contacts imported");
                          }
                          paramAnonymous2TLObject = (TLRPC.TL_contacts_importedContacts)paramAnonymous2TLObject;
                          if (!paramAnonymous2TLObject.retry_contacts.isEmpty())
                          {
                            for (i = 0; i < paramAnonymous2TLObject.retry_contacts.size(); i++)
                            {
                              long l = ((Long)paramAnonymous2TLObject.retry_contacts.get(i)).longValue();
                              localObject1.remove(localObject2.get((int)l));
                            }
                            this.val$hasErrors[0] = true;
                            if (BuildVars.LOGS_ENABLED) {
                              FileLog.d("result has retry contacts");
                            }
                          }
                          for (i = 0; i < paramAnonymous2TLObject.popular_invites.size(); i++)
                          {
                            paramAnonymous2TL_error = (TLRPC.TL_popularContact)paramAnonymous2TLObject.popular_invites.get(i);
                            localObject = (ContactsController.Contact)localHashMap2.get(localObject2.get((int)paramAnonymous2TL_error.client_id));
                            if (localObject != null) {
                              ((ContactsController.Contact)localObject).imported = paramAnonymous2TL_error.importers;
                            }
                          }
                          MessagesStorage.getInstance(ContactsController.this.currentAccount).putUsersAndChats(paramAnonymous2TLObject.users, null, true, true);
                          Object localObject = new ArrayList();
                          for (i = 0; i < paramAnonymous2TLObject.imported.size(); i++)
                          {
                            paramAnonymous2TL_error = new TLRPC.TL_contact();
                            paramAnonymous2TL_error.user_id = ((TLRPC.TL_importedContact)paramAnonymous2TLObject.imported.get(i)).user_id;
                            ((ArrayList)localObject).add(paramAnonymous2TL_error);
                          }
                          ContactsController.this.processLoadedContacts((ArrayList)localObject, paramAnonymous2TLObject.users, 2);
                        }
                        for (;;)
                        {
                          if (ContactsController.this.completedRequestsCount == m)
                          {
                            if (!localObject1.isEmpty()) {
                              MessagesStorage.getInstance(ContactsController.this.currentAccount).putCachedPhoneBook(localObject1, false);
                            }
                            Utilities.stageQueue.postRunnable(new Runnable()
                            {
                              public void run()
                              {
                                ContactsController.this.contactsBookSPhones = ContactsController.9.4.this.val$contactsBookShort;
                                ContactsController.this.contactsBook = ContactsController.9.4.this.val$contactsMap;
                                ContactsController.access$902(ContactsController.this, false);
                                ContactsController.access$1002(ContactsController.this, true);
                                if (ContactsController.9.this.val$first) {
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
                                    NotificationCenter.getInstance(ContactsController.this.currentAccount).postNotificationName(NotificationCenter.contactsImported, new Object[0]);
                                  }
                                });
                                if (ContactsController.9.4.this.val$hasErrors[0] != 0) {
                                  Utilities.globalQueue.postRunnable(new Runnable()
                                  {
                                    public void run()
                                    {
                                      MessagesStorage.getInstance(ContactsController.this.currentAccount).getCachedPhoneBook(true);
                                    }
                                  }, 1800000L);
                                }
                              }
                            });
                          }
                          return;
                          for (i = 0; i < localObject3.contacts.size(); i++)
                          {
                            paramAnonymous2TLObject = (TLRPC.TL_inputPhoneContact)localObject3.contacts.get(i);
                            localObject1.remove(localObject2.get((int)paramAnonymous2TLObject.client_id));
                          }
                          this.val$hasErrors[0] = true;
                          if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("import contacts error " + paramAnonymous2TL_error.text);
                          }
                        }
                      }
                    }, 6);
                  }
                }
              }
              else
              {
                Utilities.stageQueue.postRunnable(new Runnable()
                {
                  public void run()
                  {
                    ContactsController.this.contactsBookSPhones = localHashMap3;
                    ContactsController.this.contactsBook = localHashMap2;
                    ContactsController.access$902(ContactsController.this, false);
                    ContactsController.access$1002(ContactsController.this, true);
                    if (ContactsController.9.this.val$first) {
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
                        NotificationCenter.getInstance(ContactsController.this.currentAccount).postNotificationName(NotificationCenter.contactsDidLoaded, new Object[0]);
                        NotificationCenter.getInstance(ContactsController.this.currentAccount).postNotificationName(NotificationCenter.contactsImported, new Object[0]);
                      }
                    });
                  }
                });
              }
            }
            else
            {
              Utilities.stageQueue.postRunnable(new Runnable()
              {
                public void run()
                {
                  ContactsController.this.contactsBookSPhones = localHashMap3;
                  ContactsController.this.contactsBook = localHashMap2;
                  ContactsController.access$902(ContactsController.this, false);
                  ContactsController.access$1002(ContactsController.this, true);
                  if (ContactsController.9.this.val$first) {
                    ContactsController.this.contactsLoaded = true;
                  }
                  if ((!ContactsController.this.delayedContactsUpdate.isEmpty()) && (ContactsController.this.contactsLoaded) && (ContactsController.this.contactsBookLoaded))
                  {
                    ContactsController.this.applyContactsUpdates(ContactsController.this.delayedContactsUpdate, null, null, null);
                    ContactsController.this.delayedContactsUpdate.clear();
                  }
                }
              });
              if (!localHashMap2.isEmpty()) {
                MessagesStorage.getInstance(ContactsController.this.currentAccount).putCachedPhoneBook(localHashMap2, false);
              }
            }
          }
        }
      });
    }
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
      MessagesStorage.getInstance(this.currentAccount).deleteContacts(localArrayList2);
    }
    if (!localArrayList1.isEmpty()) {
      MessagesStorage.getInstance(this.currentAccount).putContacts(localArrayList1, false);
    }
    if ((!this.contactsLoaded) || (!this.contactsBookLoaded))
    {
      this.delayedContactsUpdate.addAll(paramArrayList);
      if (BuildVars.LOGS_ENABLED) {
        FileLog.d("delay update - contacts add = " + localArrayList1.size() + " delete = " + localArrayList2.size());
      }
    }
    for (;;)
    {
      return;
      applyContactsUpdates(paramArrayList, paramConcurrentHashMap, localArrayList1, localArrayList2);
    }
  }
  
  public void processLoadedContacts(final ArrayList<TLRPC.TL_contact> paramArrayList, final ArrayList<TLRPC.User> paramArrayList1, final int paramInt)
  {
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        final boolean bool = true;
        final Object localObject1 = MessagesController.getInstance(ContactsController.this.currentAccount);
        Object localObject2 = paramArrayList1;
        if (paramInt == 1) {}
        for (;;)
        {
          ((MessagesController)localObject1).putUsers((ArrayList)localObject2, bool);
          localObject1 = new SparseArray();
          bool = paramArrayList.isEmpty();
          if (ContactsController.this.contacts.isEmpty()) {
            break label158;
          }
          int j;
          for (i = 0; i < paramArrayList.size(); i = j + 1)
          {
            localObject2 = (TLRPC.TL_contact)paramArrayList.get(i);
            j = i;
            if (ContactsController.this.contactsDict.get(Integer.valueOf(((TLRPC.TL_contact)localObject2).user_id)) != null)
            {
              paramArrayList.remove(i);
              j = i - 1;
            }
          }
          bool = false;
        }
        paramArrayList.addAll(ContactsController.this.contacts);
        label158:
        for (int i = 0; i < paramArrayList.size(); i++)
        {
          localObject2 = MessagesController.getInstance(ContactsController.this.currentAccount).getUser(Integer.valueOf(((TLRPC.TL_contact)paramArrayList.get(i)).user_id));
          if (localObject2 != null) {
            ((SparseArray)localObject1).put(((TLRPC.User)localObject2).id, localObject2);
          }
        }
        Utilities.stageQueue.postRunnable(new Runnable()
        {
          public void run()
          {
            if (BuildVars.LOGS_ENABLED) {
              FileLog.d("done loading contacts");
            }
            if ((ContactsController.12.this.val$from == 1) && ((ContactsController.12.this.val$contactsArr.isEmpty()) || (Math.abs(System.currentTimeMillis() / 1000L - UserConfig.getInstance(ContactsController.this.currentAccount).lastContactsSyncTime) >= 86400L)))
            {
              ContactsController.this.loadContacts(false, ContactsController.this.getContactsHash(ContactsController.12.this.val$contactsArr));
              if (!ContactsController.12.this.val$contactsArr.isEmpty()) {}
            }
            for (;;)
            {
              return;
              if (ContactsController.12.this.val$from == 0)
              {
                UserConfig.getInstance(ContactsController.this.currentAccount).lastContactsSyncTime = ((int)(System.currentTimeMillis() / 1000L));
                UserConfig.getInstance(ContactsController.this.currentAccount).saveConfig(false);
              }
              for (int i = 0;; i++)
              {
                if (i >= ContactsController.12.this.val$contactsArr.size()) {
                  break label263;
                }
                localObject1 = (TLRPC.TL_contact)ContactsController.12.this.val$contactsArr.get(i);
                if ((localObject1.get(((TLRPC.TL_contact)localObject1).user_id) == null) && (((TLRPC.TL_contact)localObject1).user_id != UserConfig.getInstance(ContactsController.this.currentAccount).getClientUserId()))
                {
                  ContactsController.this.loadContacts(false, 0);
                  if (!BuildVars.LOGS_ENABLED) {
                    break;
                  }
                  FileLog.d("contacts are broken, load from server");
                  break;
                }
              }
              label263:
              final Object localObject2;
              if (ContactsController.12.this.val$from != 1)
              {
                MessagesStorage.getInstance(ContactsController.this.currentAccount).putUsersAndChats(ContactsController.12.this.val$usersArr, null, true, true);
                localObject2 = MessagesStorage.getInstance(ContactsController.this.currentAccount);
                localObject1 = ContactsController.12.this.val$contactsArr;
                if (ContactsController.12.this.val$from == 2) {
                  break label505;
                }
              }
              final ConcurrentHashMap localConcurrentHashMap;
              final HashMap localHashMap1;
              final HashMap localHashMap2;
              final ArrayList localArrayList1;
              final ArrayList localArrayList2;
              final HashMap localHashMap3;
              TLRPC.TL_contact localTL_contact;
              TLRPC.User localUser;
              label505:
              for (boolean bool = true;; bool = false)
              {
                ((MessagesStorage)localObject2).putContacts((ArrayList)localObject1, bool);
                Collections.sort(ContactsController.12.this.val$contactsArr, new Comparator()
                {
                  public int compare(TLRPC.TL_contact paramAnonymous3TL_contact1, TLRPC.TL_contact paramAnonymous3TL_contact2)
                  {
                    paramAnonymous3TL_contact1 = (TLRPC.User)ContactsController.12.1.this.val$usersDict.get(paramAnonymous3TL_contact1.user_id);
                    paramAnonymous3TL_contact2 = (TLRPC.User)ContactsController.12.1.this.val$usersDict.get(paramAnonymous3TL_contact2.user_id);
                    return UserObject.getFirstName(paramAnonymous3TL_contact1).compareTo(UserObject.getFirstName(paramAnonymous3TL_contact2));
                  }
                });
                localConcurrentHashMap = new ConcurrentHashMap(20, 1.0F, 2);
                localHashMap1 = new HashMap();
                localHashMap2 = new HashMap();
                localArrayList1 = new ArrayList();
                localArrayList2 = new ArrayList();
                localObject2 = null;
                localHashMap3 = null;
                if (!ContactsController.this.contactsBookLoaded)
                {
                  localObject2 = new HashMap();
                  localHashMap3 = new HashMap();
                }
                for (i = 0;; i++)
                {
                  if (i >= ContactsController.12.this.val$contactsArr.size()) {
                    break label774;
                  }
                  localTL_contact = (TLRPC.TL_contact)ContactsController.12.this.val$contactsArr.get(i);
                  localUser = (TLRPC.User)localObject1.get(localTL_contact.user_id);
                  if (localUser != null) {
                    break;
                  }
                }
              }
              localConcurrentHashMap.put(Integer.valueOf(localTL_contact.user_id), localTL_contact);
              if ((localObject2 != null) && (!TextUtils.isEmpty(localUser.phone)))
              {
                ((HashMap)localObject2).put(localUser.phone, localTL_contact);
                localHashMap3.put(localUser.phone.substring(Math.max(0, localUser.phone.length() - 7)), localTL_contact);
              }
              Object localObject3 = UserObject.getFirstName(localUser);
              Object localObject1 = localObject3;
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
                ArrayList localArrayList3 = (ArrayList)localHashMap1.get(localObject1);
                localObject3 = localArrayList3;
                if (localArrayList3 == null)
                {
                  localObject3 = new ArrayList();
                  localHashMap1.put(localObject1, localObject3);
                  localArrayList1.add(localObject1);
                }
                ((ArrayList)localObject3).add(localTL_contact);
                if (!localUser.mutual_contact) {
                  break;
                }
                localArrayList3 = (ArrayList)localHashMap2.get(localObject1);
                localObject3 = localArrayList3;
                if (localArrayList3 == null)
                {
                  localObject3 = new ArrayList();
                  localHashMap2.put(localObject1, localObject3);
                  localArrayList2.add(localObject1);
                }
                ((ArrayList)localObject3).add(localTL_contact);
                break;
              }
              label774:
              Collections.sort(localArrayList1, new Comparator()
              {
                public int compare(String paramAnonymous3String1, String paramAnonymous3String2)
                {
                  int i = paramAnonymous3String1.charAt(0);
                  int j = paramAnonymous3String2.charAt(0);
                  if (i == 35) {
                    j = 1;
                  }
                  for (;;)
                  {
                    return j;
                    if (j == 35) {
                      j = -1;
                    } else {
                      j = paramAnonymous3String1.compareTo(paramAnonymous3String2);
                    }
                  }
                }
              });
              Collections.sort(localArrayList2, new Comparator()
              {
                public int compare(String paramAnonymous3String1, String paramAnonymous3String2)
                {
                  int i = paramAnonymous3String1.charAt(0);
                  int j = paramAnonymous3String2.charAt(0);
                  if (i == 35) {
                    i = 1;
                  }
                  for (;;)
                  {
                    return i;
                    if (j == 35) {
                      i = -1;
                    } else {
                      i = paramAnonymous3String1.compareTo(paramAnonymous3String2);
                    }
                  }
                }
              });
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  ContactsController.this.contacts = ContactsController.12.this.val$contactsArr;
                  ContactsController.this.contactsDict = localConcurrentHashMap;
                  ContactsController.this.usersSectionsDict = localHashMap1;
                  ContactsController.this.usersMutualSectionsDict = localHashMap2;
                  ContactsController.this.sortedUsersSectionsArray = localArrayList1;
                  ContactsController.this.sortedUsersMutualSectionsArray = localArrayList2;
                  if (ContactsController.12.this.val$from != 2) {}
                  for (;;)
                  {
                    synchronized (ContactsController.this.loadContactsSync)
                    {
                      ContactsController.access$702(ContactsController.this, false);
                      ContactsController.this.performWriteContactsToPhoneBook();
                      ContactsController.this.updateUnregisteredContacts(ContactsController.12.this.val$contactsArr);
                      NotificationCenter.getInstance(ContactsController.this.currentAccount).postNotificationName(NotificationCenter.contactsDidLoaded, new Object[0]);
                      if ((ContactsController.12.this.val$from != 1) && (!ContactsController.12.1.this.val$isEmpty))
                      {
                        ContactsController.this.saveContactsLoadTime();
                        return;
                      }
                    }
                    ContactsController.this.reloadContactsStatusesMaybe();
                  }
                }
              });
              if ((!ContactsController.this.delayedContactsUpdate.isEmpty()) && (ContactsController.this.contactsLoaded) && (ContactsController.this.contactsBookLoaded))
              {
                ContactsController.this.applyContactsUpdates(ContactsController.this.delayedContactsUpdate, null, null, null);
                ContactsController.this.delayedContactsUpdate.clear();
              }
              if (localObject2 != null) {
                AndroidUtilities.runOnUIThread(new Runnable()
                {
                  public void run()
                  {
                    Utilities.globalQueue.postRunnable(new Runnable()
                    {
                      public void run()
                      {
                        ContactsController.this.contactsByPhone = ContactsController.12.1.5.this.val$contactsByPhonesDictFinal;
                        ContactsController.this.contactsByShortPhone = ContactsController.12.1.5.this.val$contactsByPhonesShortDictFinal;
                      }
                    });
                    if (ContactsController.this.contactsSyncInProgress) {}
                    for (;;)
                    {
                      return;
                      ContactsController.access$902(ContactsController.this, true);
                      MessagesStorage.getInstance(ContactsController.this.currentAccount).getCachedPhoneBook(false);
                    }
                  }
                });
              } else {
                ContactsController.this.contactsLoaded = true;
              }
            }
          }
        });
      }
    });
  }
  
  public void readContacts()
  {
    synchronized (this.loadContactsSync)
    {
      if (this.loadingContacts) {
        return;
      }
      this.loadingContacts = true;
      Utilities.stageQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          if ((!ContactsController.this.contacts.isEmpty()) || (ContactsController.this.contactsLoaded)) {}
          for (;;)
          {
            synchronized (ContactsController.this.loadContactsSync)
            {
              ContactsController.access$702(ContactsController.this, false);
              return;
            }
            ContactsController.this.loadContacts(true, 0);
          }
        }
      });
    }
  }
  
  public void reloadContactsStatuses()
  {
    saveContactsLoadTime();
    MessagesController.getInstance(this.currentAccount).clearFullUsers();
    final SharedPreferences.Editor localEditor = MessagesController.getMainSettings(this.currentAccount).edit();
    localEditor.putBoolean("needGetStatuses", true).commit();
    TLRPC.TL_contacts_getStatuses localTL_contacts_getStatuses = new TLRPC.TL_contacts_getStatuses();
    ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_contacts_getStatuses, new RequestDelegate()
    {
      public void run(final TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
      {
        if (paramAnonymousTL_error == null) {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              ContactsController.23.this.val$editor.remove("needGetStatuses").commit();
              Object localObject1 = (TLRPC.Vector)paramAnonymousTLObject;
              if (!((TLRPC.Vector)localObject1).objects.isEmpty())
              {
                ArrayList localArrayList = new ArrayList();
                localObject1 = ((TLRPC.Vector)localObject1).objects.iterator();
                while (((Iterator)localObject1).hasNext())
                {
                  Object localObject2 = ((Iterator)localObject1).next();
                  TLRPC.TL_user localTL_user = new TLRPC.TL_user();
                  TLRPC.TL_contactStatus localTL_contactStatus = (TLRPC.TL_contactStatus)localObject2;
                  if (localTL_contactStatus != null)
                  {
                    if ((localTL_contactStatus.status instanceof TLRPC.TL_userStatusRecently)) {
                      localTL_contactStatus.status.expires = -100;
                    }
                    for (;;)
                    {
                      localObject2 = MessagesController.getInstance(ContactsController.this.currentAccount).getUser(Integer.valueOf(localTL_contactStatus.user_id));
                      if (localObject2 != null) {
                        ((TLRPC.User)localObject2).status = localTL_contactStatus.status;
                      }
                      localTL_user.status = localTL_contactStatus.status;
                      localArrayList.add(localTL_user);
                      break;
                      if ((localTL_contactStatus.status instanceof TLRPC.TL_userStatusLastWeek)) {
                        localTL_contactStatus.status.expires = -101;
                      } else if ((localTL_contactStatus.status instanceof TLRPC.TL_userStatusLastMonth)) {
                        localTL_contactStatus.status.expires = -102;
                      }
                    }
                  }
                }
                MessagesStorage.getInstance(ContactsController.this.currentAccount).updateUsers(localArrayList, true, true, true);
              }
              NotificationCenter.getInstance(ContactsController.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(4) });
            }
          });
        }
      }
    });
  }
  
  public void resetImportedContacts()
  {
    TLRPC.TL_contacts_resetSaved localTL_contacts_resetSaved = new TLRPC.TL_contacts_resetSaved();
    ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_contacts_resetSaved, new RequestDelegate()
    {
      public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error) {}
    });
  }
  
  public void setDeleteAccountTTL(int paramInt)
  {
    this.deleteAccountTTL = paramInt;
  }
  
  public void setPrivacyRules(ArrayList<TLRPC.PrivacyRule> paramArrayList, int paramInt)
  {
    if (paramInt == 2) {
      this.callPrivacyRules = paramArrayList;
    }
    for (;;)
    {
      NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.privacyRulesUpdated, new Object[0]);
      reloadContactsStatuses();
      return;
      if (paramInt == 1) {
        this.groupPrivacyRules = paramArrayList;
      } else {
        this.privacyRules = paramArrayList;
      }
    }
  }
  
  public void syncPhoneBookByAlert(final HashMap<String, Contact> paramHashMap, final boolean paramBoolean1, final boolean paramBoolean2, final boolean paramBoolean3)
  {
    Utilities.globalQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        if (BuildVars.LOGS_ENABLED) {
          FileLog.d("sync contacts by alert");
        }
        ContactsController.this.performSyncPhoneBook(paramHashMap, true, paramBoolean1, paramBoolean2, false, false, paramBoolean3);
      }
    });
  }
  
  public static class Contact
  {
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
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/ContactsController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */