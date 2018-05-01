package com.google.firebase.messaging;

import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.iid.zzb;
import com.google.firebase.iid.zzk;
import com.google.firebase.iid.zzz;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class FirebaseMessagingService
  extends zzb
{
  private static final Queue<String> zzbss = new ArrayDeque(10);
  
  static void zzp(Bundle paramBundle)
  {
    Iterator localIterator = paramBundle.keySet().iterator();
    while (localIterator.hasNext())
    {
      paramBundle = (String)localIterator.next();
      if ((paramBundle != null) && (paramBundle.startsWith("google.c."))) {
        localIterator.remove();
      }
    }
  }
  
  static boolean zzq(Bundle paramBundle)
  {
    if (paramBundle == null) {}
    for (boolean bool = false;; bool = "1".equals(paramBundle.getString("google.c.a.e"))) {
      return bool;
    }
  }
  
  public void onDeletedMessages() {}
  
  public void onMessageReceived(RemoteMessage paramRemoteMessage) {}
  
  public void onMessageSent(String paramString) {}
  
  public void onSendError(String paramString, Exception paramException) {}
  
  protected final Intent zzf(Intent paramIntent)
  {
    return zzz.zzta().zztb();
  }
  
  public final boolean zzg(Intent paramIntent)
  {
    PendingIntent localPendingIntent;
    if ("com.google.firebase.messaging.NOTIFICATION_OPEN".equals(paramIntent.getAction()))
    {
      localPendingIntent = (PendingIntent)paramIntent.getParcelableExtra("pending_intent");
      if (localPendingIntent == null) {}
    }
    for (;;)
    {
      try
      {
        localPendingIntent.send();
        if (zzq(paramIntent.getExtras())) {
          zzd.zzd(this, paramIntent);
        }
        bool = true;
        return bool;
      }
      catch (PendingIntent.CanceledException localCanceledException)
      {
        Log.e("FirebaseMessaging", "Notification pending intent canceled");
        continue;
      }
      boolean bool = false;
    }
  }
  
  public final void zzh(Intent paramIntent)
  {
    int i = 0;
    Object localObject1 = paramIntent.getAction();
    Object localObject2 = localObject1;
    if (localObject1 == null) {
      localObject2 = "";
    }
    int j;
    switch (((String)localObject2).hashCode())
    {
    default: 
      j = -1;
      switch (j)
      {
      default: 
        label51:
        paramIntent = String.valueOf(paramIntent.getAction());
        if (paramIntent.length() != 0) {
          paramIntent = "Unknown intent action: ".concat(paramIntent);
        }
        break;
      }
      break;
    }
    for (;;)
    {
      Log.d("FirebaseMessaging", paramIntent);
      label105:
      return;
      if (!((String)localObject2).equals("com.google.android.c2dm.intent.RECEIVE")) {
        break;
      }
      j = 0;
      break label51;
      if (!((String)localObject2).equals("com.google.firebase.messaging.NOTIFICATION_DISMISS")) {
        break;
      }
      j = 1;
      break label51;
      localObject1 = paramIntent.getStringExtra("google.message_id");
      label158:
      label168:
      Object localObject3;
      if (TextUtils.isEmpty((CharSequence)localObject1))
      {
        localObject2 = Tasks.forResult(null);
        if (!TextUtils.isEmpty((CharSequence)localObject1)) {
          break label391;
        }
        j = 0;
        if (j == 0)
        {
          localObject3 = paramIntent.getStringExtra("message_type");
          localObject1 = localObject3;
          if (localObject3 == null) {
            localObject1 = "gcm";
          }
          switch (((String)localObject1).hashCode())
          {
          default: 
            label240:
            j = -1;
            switch (j)
            {
            default: 
              label243:
              paramIntent = String.valueOf(localObject1);
              if (paramIntent.length() != 0)
              {
                paramIntent = "Received message with unknown type: ".concat(paramIntent);
                label295:
                Log.w("FirebaseMessaging", paramIntent);
              }
              break;
            }
            break;
          }
        }
      }
      try
      {
        Tasks.await((Task)localObject2, 1L, TimeUnit.SECONDS);
      }
      catch (ExecutionException paramIntent)
      {
        for (;;)
        {
          paramIntent = String.valueOf(paramIntent);
          Log.w("FirebaseMessaging", String.valueOf(paramIntent).length() + 20 + "Message ack failed: " + paramIntent);
          break;
          localObject2 = new Bundle();
          ((Bundle)localObject2).putString("google.message_id", (String)localObject1);
          localObject2 = zzk.zzv(this).zza(2, (Bundle)localObject2);
          break label158;
          if (zzbss.contains(localObject1))
          {
            if (Log.isLoggable("FirebaseMessaging", 3))
            {
              localObject1 = String.valueOf(localObject1);
              if (((String)localObject1).length() == 0) {
                break label444;
              }
            }
            for (localObject1 = "Received duplicate message: ".concat((String)localObject1);; localObject1 = new String("Received duplicate message: "))
            {
              Log.d("FirebaseMessaging", (String)localObject1);
              j = 1;
              break;
            }
          }
          if (zzbss.size() >= 10) {
            zzbss.remove();
          }
          zzbss.add(localObject1);
          j = 0;
          break label168;
          if (!((String)localObject1).equals("gcm")) {
            break label240;
          }
          j = i;
          break label243;
          if (!((String)localObject1).equals("deleted_messages")) {
            break label240;
          }
          j = 1;
          break label243;
          if (!((String)localObject1).equals("send_event")) {
            break label240;
          }
          j = 2;
          break label243;
          if (!((String)localObject1).equals("send_error")) {
            break label240;
          }
          j = 3;
          break label243;
          if (zzq(paramIntent.getExtras())) {
            zzd.zzc(this, paramIntent);
          }
          localObject3 = paramIntent.getExtras();
          localObject1 = localObject3;
          if (localObject3 == null) {
            localObject1 = new Bundle();
          }
          ((Bundle)localObject1).remove("android.support.content.wakelockid");
          if (zza.zzl((Bundle)localObject1))
          {
            if (zza.zzw(this).zzn((Bundle)localObject1)) {
              continue;
            }
            if (zzq((Bundle)localObject1)) {
              zzd.zzf(this, paramIntent);
            }
          }
          onMessageReceived(new RemoteMessage((Bundle)localObject1));
          continue;
          onDeletedMessages();
          continue;
          onMessageSent(paramIntent.getStringExtra("google.message_id"));
          continue;
          localObject3 = paramIntent.getStringExtra("google.message_id");
          localObject1 = localObject3;
          if (localObject3 == null) {
            localObject1 = paramIntent.getStringExtra("message_id");
          }
          onSendError((String)localObject1, new SendException(paramIntent.getStringExtra("error")));
        }
        paramIntent = new String("Received message with unknown type: ");
        break label295;
        if (!zzq(paramIntent.getExtras())) {
          break label105;
        }
        zzd.zze(this, paramIntent);
        break label105;
        paramIntent = new String("Unknown intent action: ");
      }
      catch (InterruptedException paramIntent)
      {
        for (;;) {}
      }
      catch (TimeoutException paramIntent)
      {
        label391:
        label444:
        for (;;) {}
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/firebase/messaging/FirebaseMessagingService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */