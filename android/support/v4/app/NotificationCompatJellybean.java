package android.support.v4.app;

import android.app.Notification;
import android.app.Notification.Builder;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

class NotificationCompatJellybean
{
  private static final Object sActionsLock = new Object();
  private static Field sExtrasField;
  private static boolean sExtrasFieldAccessFailed;
  private static final Object sExtrasLock = new Object();
  
  public static SparseArray<Bundle> buildActionExtrasMap(List<Bundle> paramList)
  {
    Object localObject1 = null;
    int i = 0;
    int j = paramList.size();
    while (i < j)
    {
      Bundle localBundle = (Bundle)paramList.get(i);
      Object localObject2 = localObject1;
      if (localBundle != null)
      {
        localObject2 = localObject1;
        if (localObject1 == null) {
          localObject2 = new SparseArray();
        }
        ((SparseArray)localObject2).put(i, localBundle);
      }
      i++;
      localObject1 = localObject2;
    }
    return (SparseArray<Bundle>)localObject1;
  }
  
  static Bundle getBundleForAction(NotificationCompat.Action paramAction)
  {
    Bundle localBundle1 = new Bundle();
    localBundle1.putInt("icon", paramAction.getIcon());
    localBundle1.putCharSequence("title", paramAction.getTitle());
    localBundle1.putParcelable("actionIntent", paramAction.getActionIntent());
    if (paramAction.getExtras() != null) {}
    for (Bundle localBundle2 = new Bundle(paramAction.getExtras());; localBundle2 = new Bundle())
    {
      localBundle2.putBoolean("android.support.allowGeneratedReplies", paramAction.getAllowGeneratedReplies());
      localBundle1.putBundle("extras", localBundle2);
      localBundle1.putParcelableArray("remoteInputs", toBundleArray(paramAction.getRemoteInputs()));
      return localBundle1;
    }
  }
  
  public static Bundle getExtras(Notification paramNotification)
  {
    for (;;)
    {
      synchronized (sExtrasLock)
      {
        if (sExtrasFieldAccessFailed)
        {
          paramNotification = null;
          return paramNotification;
        }
      }
      try
      {
        if (sExtrasField == null)
        {
          localObject2 = Notification.class.getDeclaredField("extras");
          if (!Bundle.class.isAssignableFrom(((Field)localObject2).getType()))
          {
            Log.e("NotificationCompat", "Notification.extras field is not of type Bundle");
            sExtrasFieldAccessFailed = true;
            paramNotification = null;
            continue;
          }
          ((Field)localObject2).setAccessible(true);
          sExtrasField = (Field)localObject2;
        }
        Bundle localBundle = (Bundle)sExtrasField.get(paramNotification);
        Object localObject2 = localBundle;
        if (localBundle == null)
        {
          localObject2 = new android/os/Bundle;
          ((Bundle)localObject2).<init>();
          sExtrasField.set(paramNotification, localObject2);
        }
        paramNotification = (Notification)localObject2;
        continue;
        paramNotification = finally;
        throw paramNotification;
      }
      catch (IllegalAccessException paramNotification)
      {
        Log.e("NotificationCompat", "Unable to access notification extras", paramNotification);
        sExtrasFieldAccessFailed = true;
        paramNotification = null;
      }
      catch (NoSuchFieldException paramNotification)
      {
        for (;;)
        {
          Log.e("NotificationCompat", "Unable to access notification extras", paramNotification);
        }
      }
    }
  }
  
  private static Bundle toBundle(RemoteInput paramRemoteInput)
  {
    Bundle localBundle = new Bundle();
    localBundle.putString("resultKey", paramRemoteInput.getResultKey());
    localBundle.putCharSequence("label", paramRemoteInput.getLabel());
    localBundle.putCharSequenceArray("choices", paramRemoteInput.getChoices());
    localBundle.putBoolean("allowFreeFormInput", paramRemoteInput.getAllowFreeFormInput());
    localBundle.putBundle("extras", paramRemoteInput.getExtras());
    Object localObject = paramRemoteInput.getAllowedDataTypes();
    if ((localObject != null) && (!((Set)localObject).isEmpty()))
    {
      paramRemoteInput = new ArrayList(((Set)localObject).size());
      localObject = ((Set)localObject).iterator();
      while (((Iterator)localObject).hasNext()) {
        paramRemoteInput.add((String)((Iterator)localObject).next());
      }
      localBundle.putStringArrayList("allowedDataTypes", paramRemoteInput);
    }
    return localBundle;
  }
  
  private static Bundle[] toBundleArray(RemoteInput[] paramArrayOfRemoteInput)
  {
    Object localObject;
    if (paramArrayOfRemoteInput == null)
    {
      localObject = null;
      return (Bundle[])localObject;
    }
    Bundle[] arrayOfBundle = new Bundle[paramArrayOfRemoteInput.length];
    for (int i = 0;; i++)
    {
      localObject = arrayOfBundle;
      if (i >= paramArrayOfRemoteInput.length) {
        break;
      }
      arrayOfBundle[i] = toBundle(paramArrayOfRemoteInput[i]);
    }
  }
  
  public static Bundle writeActionAndGetExtras(Notification.Builder paramBuilder, NotificationCompat.Action paramAction)
  {
    paramBuilder.addAction(paramAction.getIcon(), paramAction.getTitle(), paramAction.getActionIntent());
    paramBuilder = new Bundle(paramAction.getExtras());
    if (paramAction.getRemoteInputs() != null) {
      paramBuilder.putParcelableArray("android.support.remoteInputs", toBundleArray(paramAction.getRemoteInputs()));
    }
    if (paramAction.getDataOnlyRemoteInputs() != null) {
      paramBuilder.putParcelableArray("android.support.dataRemoteInputs", toBundleArray(paramAction.getDataOnlyRemoteInputs()));
    }
    paramBuilder.putBoolean("android.support.allowGeneratedReplies", paramAction.getAllowGeneratedReplies());
    return paramBuilder;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v4/app/NotificationCompatJellybean.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */