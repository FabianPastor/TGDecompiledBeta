package android.support.v4.app;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import java.lang.reflect.Method;

@TargetApi(9)
@RequiresApi(9)
@RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
public class NotificationCompatBase
{
  private static Method sSetLatestEventInfo;
  
  /* Error */
  public static android.app.Notification add(android.app.Notification paramNotification, android.content.Context paramContext, CharSequence paramCharSequence1, CharSequence paramCharSequence2, PendingIntent paramPendingIntent1, PendingIntent paramPendingIntent2)
  {
    // Byte code:
    //   0: getstatic 39	android/support/v4/app/NotificationCompatBase:sSetLatestEventInfo	Ljava/lang/reflect/Method;
    //   3: ifnonnull +37 -> 40
    //   6: ldc 41
    //   8: ldc 43
    //   10: iconst_4
    //   11: anewarray 45	java/lang/Class
    //   14: dup
    //   15: iconst_0
    //   16: ldc 47
    //   18: aastore
    //   19: dup
    //   20: iconst_1
    //   21: ldc 49
    //   23: aastore
    //   24: dup
    //   25: iconst_2
    //   26: ldc 49
    //   28: aastore
    //   29: dup
    //   30: iconst_3
    //   31: ldc 51
    //   33: aastore
    //   34: invokevirtual 55	java/lang/Class:getMethod	(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
    //   37: putstatic 39	android/support/v4/app/NotificationCompatBase:sSetLatestEventInfo	Ljava/lang/reflect/Method;
    //   40: getstatic 39	android/support/v4/app/NotificationCompatBase:sSetLatestEventInfo	Ljava/lang/reflect/Method;
    //   43: aload_0
    //   44: iconst_4
    //   45: anewarray 4	java/lang/Object
    //   48: dup
    //   49: iconst_0
    //   50: aload_1
    //   51: aastore
    //   52: dup
    //   53: iconst_1
    //   54: aload_2
    //   55: aastore
    //   56: dup
    //   57: iconst_2
    //   58: aload_3
    //   59: aastore
    //   60: dup
    //   61: iconst_3
    //   62: aload 4
    //   64: aastore
    //   65: invokevirtual 61	java/lang/reflect/Method:invoke	(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    //   68: pop
    //   69: aload_0
    //   70: aload 5
    //   72: putfield 65	android/app/Notification:fullScreenIntent	Landroid/app/PendingIntent;
    //   75: aload_0
    //   76: areturn
    //   77: astore_0
    //   78: new 67	java/lang/RuntimeException
    //   81: dup
    //   82: aload_0
    //   83: invokespecial 70	java/lang/RuntimeException:<init>	(Ljava/lang/Throwable;)V
    //   86: athrow
    //   87: astore_0
    //   88: new 67	java/lang/RuntimeException
    //   91: dup
    //   92: aload_0
    //   93: invokespecial 70	java/lang/RuntimeException:<init>	(Ljava/lang/Throwable;)V
    //   96: athrow
    //   97: astore_0
    //   98: goto -10 -> 88
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	101	0	paramNotification	android.app.Notification
    //   0	101	1	paramContext	android.content.Context
    //   0	101	2	paramCharSequence1	CharSequence
    //   0	101	3	paramCharSequence2	CharSequence
    //   0	101	4	paramPendingIntent1	PendingIntent
    //   0	101	5	paramPendingIntent2	PendingIntent
    // Exception table:
    //   from	to	target	type
    //   6	40	77	java/lang/NoSuchMethodException
    //   40	69	87	java/lang/IllegalAccessException
    //   40	69	97	java/lang/reflect/InvocationTargetException
  }
  
  public static abstract class Action
  {
    public abstract PendingIntent getActionIntent();
    
    public abstract boolean getAllowGeneratedReplies();
    
    public abstract Bundle getExtras();
    
    public abstract int getIcon();
    
    public abstract RemoteInputCompatBase.RemoteInput[] getRemoteInputs();
    
    public abstract CharSequence getTitle();
    
    public static abstract interface Factory
    {
      public abstract NotificationCompatBase.Action build(int paramInt, CharSequence paramCharSequence, PendingIntent paramPendingIntent, Bundle paramBundle, RemoteInputCompatBase.RemoteInput[] paramArrayOfRemoteInput, boolean paramBoolean);
      
      public abstract NotificationCompatBase.Action[] newArray(int paramInt);
    }
  }
  
  public static abstract class UnreadConversation
  {
    abstract long getLatestTimestamp();
    
    abstract String[] getMessages();
    
    abstract String getParticipant();
    
    abstract String[] getParticipants();
    
    abstract PendingIntent getReadPendingIntent();
    
    abstract RemoteInputCompatBase.RemoteInput getRemoteInput();
    
    abstract PendingIntent getReplyPendingIntent();
    
    public static abstract interface Factory
    {
      public abstract NotificationCompatBase.UnreadConversation build(String[] paramArrayOfString1, RemoteInputCompatBase.RemoteInput paramRemoteInput, PendingIntent paramPendingIntent1, PendingIntent paramPendingIntent2, String[] paramArrayOfString2, long paramLong);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v4/app/NotificationCompatBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */