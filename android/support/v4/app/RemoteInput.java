package android.support.v4.app;

import android.content.ClipData;
import android.content.ClipData.Item;
import android.content.ClipDescription;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.util.Log;
import java.util.HashSet;
import java.util.Set;

public final class RemoteInput
{
  private final boolean mAllowFreeFormTextInput;
  private final Set<String> mAllowedDataTypes;
  private final CharSequence[] mChoices;
  private final Bundle mExtras;
  private final CharSequence mLabel;
  private final String mResultKey;
  
  RemoteInput(String paramString, CharSequence paramCharSequence, CharSequence[] paramArrayOfCharSequence, boolean paramBoolean, Bundle paramBundle, Set<String> paramSet)
  {
    this.mResultKey = paramString;
    this.mLabel = paramCharSequence;
    this.mChoices = paramArrayOfCharSequence;
    this.mAllowFreeFormTextInput = paramBoolean;
    this.mExtras = paramBundle;
    this.mAllowedDataTypes = paramSet;
  }
  
  static android.app.RemoteInput fromCompat(RemoteInput paramRemoteInput)
  {
    return new android.app.RemoteInput.Builder(paramRemoteInput.getResultKey()).setLabel(paramRemoteInput.getLabel()).setChoices(paramRemoteInput.getChoices()).setAllowFreeFormInput(paramRemoteInput.getAllowFreeFormInput()).addExtras(paramRemoteInput.getExtras()).build();
  }
  
  static android.app.RemoteInput[] fromCompat(RemoteInput[] paramArrayOfRemoteInput)
  {
    Object localObject;
    if (paramArrayOfRemoteInput == null)
    {
      localObject = null;
      return (android.app.RemoteInput[])localObject;
    }
    android.app.RemoteInput[] arrayOfRemoteInput = new android.app.RemoteInput[paramArrayOfRemoteInput.length];
    for (int i = 0;; i++)
    {
      localObject = arrayOfRemoteInput;
      if (i >= paramArrayOfRemoteInput.length) {
        break;
      }
      arrayOfRemoteInput[i] = fromCompat(paramArrayOfRemoteInput[i]);
    }
  }
  
  private static Intent getClipDataIntentFromIntent(Intent paramIntent)
  {
    Object localObject = null;
    ClipData localClipData = paramIntent.getClipData();
    if (localClipData == null) {
      paramIntent = (Intent)localObject;
    }
    for (;;)
    {
      return paramIntent;
      ClipDescription localClipDescription = localClipData.getDescription();
      paramIntent = (Intent)localObject;
      if (localClipDescription.hasMimeType("text/vnd.android.intent"))
      {
        paramIntent = (Intent)localObject;
        if (localClipDescription.getLabel().equals("android.remoteinput.results")) {
          paramIntent = localClipData.getItemAt(0).getIntent();
        }
      }
    }
  }
  
  public static Bundle getResultsFromIntent(Intent paramIntent)
  {
    Object localObject = null;
    if (Build.VERSION.SDK_INT >= 20) {
      paramIntent = android.app.RemoteInput.getResultsFromIntent(paramIntent);
    }
    for (;;)
    {
      return paramIntent;
      if (Build.VERSION.SDK_INT >= 16)
      {
        Intent localIntent = getClipDataIntentFromIntent(paramIntent);
        paramIntent = (Intent)localObject;
        if (localIntent != null) {
          paramIntent = (Bundle)localIntent.getExtras().getParcelable("android.remoteinput.resultsData");
        }
      }
      else
      {
        Log.w("RemoteInput", "RemoteInput is only supported from API Level 16");
        paramIntent = (Intent)localObject;
      }
    }
  }
  
  public boolean getAllowFreeFormInput()
  {
    return this.mAllowFreeFormTextInput;
  }
  
  public Set<String> getAllowedDataTypes()
  {
    return this.mAllowedDataTypes;
  }
  
  public CharSequence[] getChoices()
  {
    return this.mChoices;
  }
  
  public Bundle getExtras()
  {
    return this.mExtras;
  }
  
  public CharSequence getLabel()
  {
    return this.mLabel;
  }
  
  public String getResultKey()
  {
    return this.mResultKey;
  }
  
  public boolean isDataOnly()
  {
    if ((!getAllowFreeFormInput()) && ((getChoices() == null) || (getChoices().length == 0)) && (getAllowedDataTypes() != null) && (!getAllowedDataTypes().isEmpty())) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public static final class Builder
  {
    private boolean mAllowFreeFormTextInput = true;
    private final Set<String> mAllowedDataTypes = new HashSet();
    private CharSequence[] mChoices;
    private Bundle mExtras = new Bundle();
    private CharSequence mLabel;
    private final String mResultKey;
    
    public Builder(String paramString)
    {
      if (paramString == null) {
        throw new IllegalArgumentException("Result key can't be null");
      }
      this.mResultKey = paramString;
    }
    
    public RemoteInput build()
    {
      return new RemoteInput(this.mResultKey, this.mLabel, this.mChoices, this.mAllowFreeFormTextInput, this.mExtras, this.mAllowedDataTypes);
    }
    
    public Builder setLabel(CharSequence paramCharSequence)
    {
      this.mLabel = paramCharSequence;
      return this;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v4/app/RemoteInput.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */