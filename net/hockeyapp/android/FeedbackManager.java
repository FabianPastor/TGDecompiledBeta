package net.hockeyapp.android;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import net.hockeyapp.android.objects.FeedbackUserDataElement;
import net.hockeyapp.android.tasks.ParseFeedbackTask;
import net.hockeyapp.android.tasks.SendFeedbackTask;
import net.hockeyapp.android.utils.AsyncTaskUtils;
import net.hockeyapp.android.utils.HockeyLog;
import net.hockeyapp.android.utils.PrefsUtil;
import net.hockeyapp.android.utils.Util;

public class FeedbackManager
{
  private static final String BROADCAST_ACTION = "net.hockeyapp.android.SCREENSHOT";
  private static final int BROADCAST_REQUEST_CODE = 1;
  private static final int SCREENSHOT_NOTIFICATION_ID = 1;
  private static Activity currentActivity;
  private static String identifier;
  private static FeedbackManagerListener lastListener = null;
  private static boolean notificationActive;
  private static BroadcastReceiver receiver = null;
  private static FeedbackUserDataElement requireUserEmail;
  private static FeedbackUserDataElement requireUserName;
  private static String urlString;
  private static String userEmail;
  private static String userName;
  
  static
  {
    notificationActive = false;
    identifier = null;
    urlString = null;
  }
  
  public static void checkForAnswersAndNotify(Context paramContext)
  {
    String str = PrefsUtil.getInstance().getFeedbackTokenFromPrefs(paramContext);
    if (str == null) {
      return;
    }
    int i = paramContext.getSharedPreferences("net.hockeyapp.android.feedback", 0).getInt("idLastMessageSend", -1);
    paramContext = new SendFeedbackTask(paramContext, getURLString(paramContext), null, null, null, null, null, str, new Handler()
    {
      public void handleMessage(Message paramAnonymousMessage)
      {
        paramAnonymousMessage = paramAnonymousMessage.getData().getString("feedback_response");
        if (paramAnonymousMessage != null)
        {
          paramAnonymousMessage = new ParseFeedbackTask(this.val$context, paramAnonymousMessage, null, "fetch");
          paramAnonymousMessage.setUrlString(FeedbackManager.getURLString(this.val$context));
          AsyncTaskUtils.execute(paramAnonymousMessage);
        }
      }
    }, true);
    paramContext.setShowProgressDialog(false);
    paramContext.setLastMessageId(i);
    AsyncTaskUtils.execute(paramContext);
  }
  
  private static void endNotification()
  {
    notificationActive = false;
    currentActivity.unregisterReceiver(receiver);
    ((NotificationManager)currentActivity.getSystemService("notification")).cancel(1);
  }
  
  public static FeedbackManagerListener getLastListener()
  {
    return lastListener;
  }
  
  public static FeedbackUserDataElement getRequireUserEmail()
  {
    return requireUserEmail;
  }
  
  public static FeedbackUserDataElement getRequireUserName()
  {
    return requireUserName;
  }
  
  private static String getURLString(Context paramContext)
  {
    return urlString + "api/2/apps/" + identifier + "/feedback/";
  }
  
  public static void register(Context paramContext)
  {
    String str = Util.getAppIdentifier(paramContext);
    if ((str == null) || (str.length() == 0)) {
      throw new IllegalArgumentException("HockeyApp app identifier was not configured correctly in manifest or build configuration.");
    }
    register(paramContext, str);
  }
  
  public static void register(Context paramContext, String paramString)
  {
    register(paramContext, paramString, null);
  }
  
  public static void register(Context paramContext, String paramString1, String paramString2, FeedbackManagerListener paramFeedbackManagerListener)
  {
    if (paramContext != null)
    {
      identifier = Util.sanitizeAppIdentifier(paramString2);
      urlString = paramString1;
      lastListener = paramFeedbackManagerListener;
      Constants.loadFromContext(paramContext);
    }
  }
  
  public static void register(Context paramContext, String paramString, FeedbackManagerListener paramFeedbackManagerListener)
  {
    register(paramContext, "https://sdk.hockeyapp.net/", paramString, paramFeedbackManagerListener);
  }
  
  public static void setActivityForScreenshot(Activity paramActivity)
  {
    currentActivity = paramActivity;
    if (!notificationActive) {
      startNotification();
    }
  }
  
  public static void setRequireUserEmail(FeedbackUserDataElement paramFeedbackUserDataElement)
  {
    requireUserEmail = paramFeedbackUserDataElement;
  }
  
  public static void setRequireUserName(FeedbackUserDataElement paramFeedbackUserDataElement)
  {
    requireUserName = paramFeedbackUserDataElement;
  }
  
  public static void setUserEmail(String paramString)
  {
    userEmail = paramString;
  }
  
  public static void setUserName(String paramString)
  {
    userName = paramString;
  }
  
  public static void showFeedbackActivity(Context paramContext, Bundle paramBundle, Uri... paramVarArgs)
  {
    if (paramContext != null)
    {
      Object localObject1 = null;
      if (lastListener != null) {
        localObject1 = lastListener.getFeedbackActivityClass();
      }
      Object localObject2 = localObject1;
      if (localObject1 == null) {
        localObject2 = FeedbackActivity.class;
      }
      localObject1 = new Intent();
      if ((paramBundle != null) && (!paramBundle.isEmpty())) {
        ((Intent)localObject1).putExtras(paramBundle);
      }
      ((Intent)localObject1).setFlags(268435456);
      ((Intent)localObject1).setClass(paramContext, (Class)localObject2);
      ((Intent)localObject1).putExtra("url", getURLString(paramContext));
      ((Intent)localObject1).putExtra("initialUserName", userName);
      ((Intent)localObject1).putExtra("initialUserEmail", userEmail);
      ((Intent)localObject1).putExtra("initialAttachments", paramVarArgs);
      paramContext.startActivity((Intent)localObject1);
    }
  }
  
  public static void showFeedbackActivity(Context paramContext, Uri... paramVarArgs)
  {
    showFeedbackActivity(paramContext, null, paramVarArgs);
  }
  
  private static void startNotification()
  {
    notificationActive = true;
    NotificationManager localNotificationManager = (NotificationManager)currentActivity.getSystemService("notification");
    int i = currentActivity.getResources().getIdentifier("ic_menu_camera", "drawable", "android");
    Object localObject = new Intent();
    ((Intent)localObject).setAction("net.hockeyapp.android.SCREENSHOT");
    localObject = PendingIntent.getBroadcast(currentActivity, 1, (Intent)localObject, 1073741824);
    localNotificationManager.notify(1, Util.createNotification(currentActivity, (PendingIntent)localObject, "HockeyApp Feedback", "Take a screenshot for your feedback.", i));
    if (receiver == null) {
      receiver = new BroadcastReceiver()
      {
        public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
        {
          FeedbackManager.takeScreenshot(paramAnonymousContext);
        }
      };
    }
    currentActivity.registerReceiver(receiver, new IntentFilter("net.hockeyapp.android.SCREENSHOT"));
  }
  
  public static void takeScreenshot(final Context paramContext)
  {
    Object localObject1 = currentActivity.getWindow().getDecorView();
    ((View)localObject1).setDrawingCacheEnabled(true);
    Object localObject2 = ((View)localObject1).getDrawingCache();
    Object localObject3 = currentActivity.getLocalClassName();
    File localFile = Constants.getHockeyAppStorageDir();
    localObject1 = new File(localFile, (String)localObject3 + ".jpg");
    int i = 1;
    while (((File)localObject1).exists())
    {
      localObject1 = new File(localFile, (String)localObject3 + "_" + i + ".jpg");
      i += 1;
    }
    new AsyncTask()
    {
      protected Boolean doInBackground(File... paramAnonymousVarArgs)
      {
        try
        {
          paramAnonymousVarArgs = new FileOutputStream(paramAnonymousVarArgs[0]);
          this.val$bitmap.compress(Bitmap.CompressFormat.JPEG, 100, paramAnonymousVarArgs);
          paramAnonymousVarArgs.close();
          return Boolean.valueOf(true);
        }
        catch (IOException paramAnonymousVarArgs)
        {
          HockeyLog.error("Could not save screenshot.", paramAnonymousVarArgs);
        }
        return Boolean.valueOf(false);
      }
      
      protected void onPostExecute(Boolean paramAnonymousBoolean)
      {
        if (!paramAnonymousBoolean.booleanValue()) {
          Toast.makeText(paramContext, "Screenshot could not be created. Sorry.", 1).show();
        }
      }
    }.execute(new File[] { localObject1 });
    localObject2 = new MediaScannerClient(((File)localObject1).getAbsolutePath(), null);
    localObject3 = new MediaScannerConnection(currentActivity, (MediaScannerConnection.MediaScannerConnectionClient)localObject2);
    ((MediaScannerClient)localObject2).setConnection((MediaScannerConnection)localObject3);
    ((MediaScannerConnection)localObject3).connect();
    Toast.makeText(paramContext, "Screenshot '" + ((File)localObject1).getName() + "' is available in gallery.", 1).show();
  }
  
  public static void unregister()
  {
    lastListener = null;
  }
  
  public static void unsetCurrentActivityForScreenshot(Activity paramActivity)
  {
    if ((currentActivity == null) || (currentActivity != paramActivity)) {
      return;
    }
    endNotification();
    currentActivity = null;
  }
  
  private static class MediaScannerClient
    implements MediaScannerConnection.MediaScannerConnectionClient
  {
    private MediaScannerConnection connection = null;
    private String path;
    
    private MediaScannerClient(String paramString)
    {
      this.path = paramString;
    }
    
    public void onMediaScannerConnected()
    {
      if (this.connection != null) {
        this.connection.scanFile(this.path, null);
      }
    }
    
    public void onScanCompleted(String paramString, Uri paramUri)
    {
      HockeyLog.verbose(String.format("Scanned path %s -> URI = %s", new Object[] { paramString, paramUri.toString() }));
      this.connection.disconnect();
    }
    
    public void setConnection(MediaScannerConnection paramMediaScannerConnection)
    {
      this.connection = paramMediaScannerConnection;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/FeedbackManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */