package net.hockeyapp.android;

import android.content.Context;
import java.util.Date;
import net.hockeyapp.android.utils.Util;
import org.json.JSONArray;

public abstract class UpdateManagerListener
{
  public boolean canUpdateInMarket()
  {
    return false;
  }
  
  public Date getExpiryDate()
  {
    return null;
  }
  
  public Class<? extends UpdateFragment> getUpdateFragmentClass()
  {
    return UpdateFragment.class;
  }
  
  public boolean onBuildExpired()
  {
    return true;
  }
  
  public void onCancel() {}
  
  public void onNoUpdateAvailable() {}
  
  public void onUpdateAvailable() {}
  
  public void onUpdateAvailable(JSONArray paramJSONArray, String paramString)
  {
    onUpdateAvailable();
  }
  
  public boolean useUpdateDialog(Context paramContext)
  {
    return Util.runsOnTablet(paramContext).booleanValue();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/UpdateManagerListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */