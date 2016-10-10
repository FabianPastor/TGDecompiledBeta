package net.hockeyapp.android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import net.hockeyapp.android.adapters.MessagesAdapter;
import net.hockeyapp.android.objects.ErrorObject;
import net.hockeyapp.android.objects.Feedback;
import net.hockeyapp.android.objects.FeedbackMessage;
import net.hockeyapp.android.objects.FeedbackResponse;
import net.hockeyapp.android.objects.FeedbackUserDataElement;
import net.hockeyapp.android.tasks.ParseFeedbackTask;
import net.hockeyapp.android.tasks.SendFeedbackTask;
import net.hockeyapp.android.utils.AsyncTaskUtils;
import net.hockeyapp.android.utils.HockeyLog;
import net.hockeyapp.android.utils.PrefsUtil;
import net.hockeyapp.android.utils.Util;
import net.hockeyapp.android.views.AttachmentListView;
import net.hockeyapp.android.views.AttachmentView;

public class FeedbackActivity
  extends Activity
  implements View.OnClickListener
{
  private static final int ATTACH_FILE = 2;
  private static final int ATTACH_PICTURE = 1;
  private static final int DIALOG_ERROR_ID = 0;
  public static final String EXTRA_INITIAL_ATTACHMENTS = "initialAttachments";
  public static final String EXTRA_INITIAL_USER_EMAIL = "initialUserEmail";
  public static final String EXTRA_INITIAL_USER_NAME = "initialUserName";
  public static final String EXTRA_URL = "url";
  private static final int MAX_ATTACHMENTS_PER_MSG = 3;
  private static final int PAINT_IMAGE = 3;
  private String initialUserEmail;
  private String initialUserName;
  private Button mAddAttachmentButton;
  private Button mAddResponseButton;
  private Context mContext;
  private EditText mEmailInput;
  private ErrorObject mError;
  private Handler mFeedbackHandler;
  private ArrayList<FeedbackMessage> mFeedbackMessages;
  private ScrollView mFeedbackScrollview;
  private boolean mFeedbackViewInitialized;
  private boolean mInSendFeedback;
  private List<Uri> mInitialAttachments;
  private TextView mLastUpdatedTextView;
  private MessagesAdapter mMessagesAdapter;
  private ListView mMessagesListView;
  private EditText mNameInput;
  private Handler mParseFeedbackHandler;
  private ParseFeedbackTask mParseFeedbackTask;
  private Button mRefreshButton;
  private Button mSendFeedbackButton;
  private SendFeedbackTask mSendFeedbackTask;
  private EditText mSubjectInput;
  private EditText mTextInput;
  private String mToken;
  private String mUrl;
  private LinearLayout mWrapperLayoutFeedbackAndMessages;
  
  private boolean addAttachment(int paramInt)
  {
    Intent localIntent;
    if (paramInt == 2)
    {
      localIntent = new Intent();
      localIntent.setType("*/*");
      localIntent.setAction("android.intent.action.GET_CONTENT");
      startActivityForResult(Intent.createChooser(localIntent, getString(R.string.hockeyapp_feedback_select_file)), 2);
      return true;
    }
    if (paramInt == 1)
    {
      localIntent = new Intent();
      localIntent.setType("image/*");
      localIntent.setAction("android.intent.action.GET_CONTENT");
      startActivityForResult(Intent.createChooser(localIntent, getString(R.string.hockeyapp_feedback_select_picture)), 1);
      return true;
    }
    return false;
  }
  
  private void configureAppropriateView()
  {
    this.mToken = PrefsUtil.getInstance().getFeedbackTokenFromPrefs(this);
    if ((this.mToken == null) || (this.mInSendFeedback))
    {
      configureFeedbackView(false);
      return;
    }
    configureFeedbackView(true);
    sendFetchFeedback(this.mUrl, null, null, null, null, null, this.mToken, this.mFeedbackHandler, true);
  }
  
  private void createParseFeedbackTask(String paramString1, String paramString2)
  {
    this.mParseFeedbackTask = new ParseFeedbackTask(this, paramString1, this.mParseFeedbackHandler, paramString2);
  }
  
  private void hideKeyboard()
  {
    if (this.mTextInput != null) {
      ((InputMethodManager)getSystemService("input_method")).hideSoftInputFromWindow(this.mTextInput.getWindowToken(), 0);
    }
  }
  
  private void initFeedbackHandler()
  {
    this.mFeedbackHandler = new FeedbackHandler(this);
  }
  
  private void initParseFeedbackHandler()
  {
    this.mParseFeedbackHandler = new ParseFeedbackHandler(this);
  }
  
  @SuppressLint({"SimpleDateFormat"})
  private void loadFeedbackMessages(final FeedbackResponse paramFeedbackResponse)
  {
    runOnUiThread(new Runnable()
    {
      public void run()
      {
        FeedbackActivity.this.configureFeedbackView(true);
        Object localObject = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("d MMM h:mm a");
        if ((paramFeedbackResponse != null) && (paramFeedbackResponse.getFeedback() != null) && (paramFeedbackResponse.getFeedback().getMessages() != null) && (paramFeedbackResponse.getFeedback().getMessages().size() > 0))
        {
          FeedbackActivity.access$102(FeedbackActivity.this, paramFeedbackResponse.getFeedback().getMessages());
          Collections.reverse(FeedbackActivity.this.mFeedbackMessages);
        }
        try
        {
          localObject = ((SimpleDateFormat)localObject).parse(((FeedbackMessage)FeedbackActivity.this.mFeedbackMessages.get(0)).getCreatedAt());
          FeedbackActivity.this.mLastUpdatedTextView.setText(FeedbackActivity.this.getString(R.string.hockeyapp_feedback_last_updated_text, new Object[] { localSimpleDateFormat.format((Date)localObject) }));
          if (FeedbackActivity.this.mMessagesAdapter == null)
          {
            FeedbackActivity.access$302(FeedbackActivity.this, new MessagesAdapter(FeedbackActivity.this.mContext, FeedbackActivity.this.mFeedbackMessages));
            FeedbackActivity.this.mMessagesListView.setAdapter(FeedbackActivity.this.mMessagesAdapter);
            return;
          }
        }
        catch (ParseException localParseException)
        {
          for (;;)
          {
            localParseException.printStackTrace();
            continue;
            FeedbackActivity.this.mMessagesAdapter.clear();
            Iterator localIterator = FeedbackActivity.this.mFeedbackMessages.iterator();
            while (localIterator.hasNext())
            {
              localObject = (FeedbackMessage)localIterator.next();
              FeedbackActivity.this.mMessagesAdapter.add((FeedbackMessage)localObject);
            }
            FeedbackActivity.this.mMessagesAdapter.notifyDataSetChanged();
          }
        }
      }
    });
  }
  
  private void resetFeedbackView()
  {
    runOnUiThread(new Runnable()
    {
      public void run()
      {
        PrefsUtil.getInstance().saveFeedbackTokenToPrefs(FeedbackActivity.this, null);
        FeedbackActivity.this.getSharedPreferences("net.hockeyapp.android.feedback", 0).edit().remove("idLastMessageSend").remove("idLastMessageProcessed").apply();
        FeedbackActivity.this.configureFeedbackView(false);
      }
    });
  }
  
  private void sendFeedback()
  {
    if (!Util.isConnectedToNetwork(this))
    {
      Toast.makeText(this, R.string.hockeyapp_error_no_network_message, 1).show();
      return;
    }
    enableDisableSendFeedbackButton(false);
    hideKeyboard();
    String str1 = PrefsUtil.getInstance().getFeedbackTokenFromPrefs(this.mContext);
    String str2 = this.mNameInput.getText().toString().trim();
    String str3 = this.mEmailInput.getText().toString().trim();
    String str4 = this.mSubjectInput.getText().toString().trim();
    String str5 = this.mTextInput.getText().toString().trim();
    if (TextUtils.isEmpty(str4))
    {
      this.mSubjectInput.setVisibility(0);
      setError(this.mSubjectInput, R.string.hockeyapp_feedback_validate_subject_error);
      return;
    }
    if ((FeedbackManager.getRequireUserName() == FeedbackUserDataElement.REQUIRED) && (TextUtils.isEmpty(str2)))
    {
      setError(this.mNameInput, R.string.hockeyapp_feedback_validate_name_error);
      return;
    }
    if ((FeedbackManager.getRequireUserEmail() == FeedbackUserDataElement.REQUIRED) && (TextUtils.isEmpty(str3)))
    {
      setError(this.mEmailInput, R.string.hockeyapp_feedback_validate_email_empty);
      return;
    }
    if (TextUtils.isEmpty(str5))
    {
      setError(this.mTextInput, R.string.hockeyapp_feedback_validate_text_error);
      return;
    }
    if ((FeedbackManager.getRequireUserEmail() == FeedbackUserDataElement.REQUIRED) && (!Util.isValidEmail(str3)))
    {
      setError(this.mEmailInput, R.string.hockeyapp_feedback_validate_email_error);
      return;
    }
    PrefsUtil.getInstance().saveNameEmailSubjectToPrefs(this.mContext, str2, str3, str4);
    ArrayList localArrayList = ((AttachmentListView)findViewById(R.id.wrapper_attachments)).getAttachments();
    sendFetchFeedback(this.mUrl, str2, str3, str4, str5, localArrayList, str1, this.mFeedbackHandler, false);
  }
  
  private void sendFetchFeedback(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, List<Uri> paramList, String paramString6, Handler paramHandler, boolean paramBoolean)
  {
    this.mSendFeedbackTask = new SendFeedbackTask(this.mContext, paramString1, paramString2, paramString3, paramString4, paramString5, paramList, paramString6, paramHandler, paramBoolean);
    AsyncTaskUtils.execute(this.mSendFeedbackTask);
  }
  
  private void setError(EditText paramEditText, int paramInt)
  {
    paramEditText.setError(getString(paramInt));
    enableDisableSendFeedbackButton(true);
  }
  
  private void startParseFeedbackTask(String paramString1, String paramString2)
  {
    createParseFeedbackTask(paramString1, paramString2);
    AsyncTaskUtils.execute(this.mParseFeedbackTask);
  }
  
  protected void configureFeedbackView(boolean paramBoolean)
  {
    this.mFeedbackScrollview = ((ScrollView)findViewById(R.id.wrapper_feedback_scroll));
    this.mWrapperLayoutFeedbackAndMessages = ((LinearLayout)findViewById(R.id.wrapper_messages));
    this.mMessagesListView = ((ListView)findViewById(R.id.list_feedback_messages));
    if (paramBoolean)
    {
      this.mWrapperLayoutFeedbackAndMessages.setVisibility(0);
      this.mFeedbackScrollview.setVisibility(8);
      this.mLastUpdatedTextView = ((TextView)findViewById(R.id.label_last_updated));
      this.mAddResponseButton = ((Button)findViewById(R.id.button_add_response));
      this.mAddResponseButton.setOnClickListener(this);
      this.mRefreshButton = ((Button)findViewById(R.id.button_refresh));
      this.mRefreshButton.setOnClickListener(this);
      return;
    }
    this.mWrapperLayoutFeedbackAndMessages.setVisibility(8);
    this.mFeedbackScrollview.setVisibility(0);
    this.mNameInput = ((EditText)findViewById(R.id.input_name));
    this.mEmailInput = ((EditText)findViewById(R.id.input_email));
    this.mSubjectInput = ((EditText)findViewById(R.id.input_subject));
    this.mTextInput = ((EditText)findViewById(R.id.input_message));
    Object localObject;
    if (!this.mFeedbackViewInitialized)
    {
      localObject = PrefsUtil.getInstance().getNameEmailFromPrefs(this.mContext);
      if (localObject == null) {
        break label394;
      }
      localObject = ((String)localObject).split("\\|");
      if ((localObject != null) && (localObject.length >= 2))
      {
        this.mNameInput.setText(localObject[0]);
        this.mEmailInput.setText(localObject[1]);
        if (localObject.length >= 3)
        {
          this.mSubjectInput.setText(localObject[2]);
          this.mTextInput.requestFocus();
        }
      }
      else
      {
        this.mFeedbackViewInitialized = true;
      }
    }
    else
    {
      this.mTextInput.setText("");
      if (PrefsUtil.getInstance().getFeedbackTokenFromPrefs(this.mContext) == null) {
        break label479;
      }
      this.mSubjectInput.setVisibility(8);
    }
    for (;;)
    {
      localObject = (ViewGroup)findViewById(R.id.wrapper_attachments);
      ((ViewGroup)localObject).removeAllViews();
      if (this.mInitialAttachments == null) {
        break label490;
      }
      Iterator localIterator = this.mInitialAttachments.iterator();
      while (localIterator.hasNext()) {
        ((ViewGroup)localObject).addView(new AttachmentView(this, (ViewGroup)localObject, (Uri)localIterator.next(), true));
      }
      this.mSubjectInput.requestFocus();
      break;
      label394:
      this.mNameInput.setText(this.initialUserName);
      this.mEmailInput.setText(this.initialUserEmail);
      this.mSubjectInput.setText("");
      if (TextUtils.isEmpty(this.initialUserName))
      {
        this.mNameInput.requestFocus();
        break;
      }
      if (TextUtils.isEmpty(this.initialUserEmail))
      {
        this.mEmailInput.requestFocus();
        break;
      }
      this.mSubjectInput.requestFocus();
      break;
      label479:
      this.mSubjectInput.setVisibility(0);
    }
    label490:
    this.mAddAttachmentButton = ((Button)findViewById(R.id.button_attachment));
    this.mAddAttachmentButton.setOnClickListener(this);
    registerForContextMenu(this.mAddAttachmentButton);
    this.mSendFeedbackButton = ((Button)findViewById(R.id.button_send));
    this.mSendFeedbackButton.setOnClickListener(this);
  }
  
  public void enableDisableSendFeedbackButton(boolean paramBoolean)
  {
    if (this.mSendFeedbackButton != null) {
      this.mSendFeedbackButton.setEnabled(paramBoolean);
    }
  }
  
  @SuppressLint({"InflateParams"})
  public View getLayoutView()
  {
    return getLayoutInflater().inflate(R.layout.hockeyapp_activity_feedback, null);
  }
  
  protected void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    if (paramInt2 != -1) {}
    do
    {
      do
      {
        do
        {
          do
          {
            return;
            if (paramInt1 != 2) {
              break;
            }
            paramIntent = paramIntent.getData();
          } while (paramIntent == null);
          localObject = (ViewGroup)findViewById(R.id.wrapper_attachments);
          ((ViewGroup)localObject).addView(new AttachmentView(this, (ViewGroup)localObject, paramIntent, true));
          return;
          if (paramInt1 != 1) {
            break;
          }
          paramIntent = paramIntent.getData();
        } while (paramIntent == null);
        try
        {
          localObject = new Intent(this, PaintActivity.class);
          ((Intent)localObject).putExtra("imageUri", paramIntent);
          startActivityForResult((Intent)localObject, 3);
          return;
        }
        catch (ActivityNotFoundException paramIntent)
        {
          HockeyLog.error("HockeyApp", "Paint activity not declared!", paramIntent);
          return;
        }
      } while (paramInt1 != 3);
      paramIntent = (Uri)paramIntent.getParcelableExtra("imageUri");
    } while (paramIntent == null);
    Object localObject = (ViewGroup)findViewById(R.id.wrapper_attachments);
    ((ViewGroup)localObject).addView(new AttachmentView(this, (ViewGroup)localObject, paramIntent, true));
  }
  
  public void onClick(View paramView)
  {
    int i = paramView.getId();
    if (i == R.id.button_send) {
      sendFeedback();
    }
    do
    {
      return;
      if (i == R.id.button_attachment)
      {
        if (((ViewGroup)findViewById(R.id.wrapper_attachments)).getChildCount() >= 3)
        {
          Toast.makeText(this, String.valueOf(3), 0).show();
          return;
        }
        openContextMenu(paramView);
        return;
      }
      if (i == R.id.button_add_response)
      {
        configureFeedbackView(false);
        this.mInSendFeedback = true;
        return;
      }
    } while (i != R.id.button_refresh);
    sendFetchFeedback(this.mUrl, null, null, null, null, null, PrefsUtil.getInstance().getFeedbackTokenFromPrefs(this.mContext), this.mFeedbackHandler, true);
  }
  
  public boolean onContextItemSelected(MenuItem paramMenuItem)
  {
    switch (paramMenuItem.getItemId())
    {
    default: 
      return super.onContextItemSelected(paramMenuItem);
    }
    return addAttachment(paramMenuItem.getItemId());
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(getLayoutView());
    setTitle(getString(R.string.hockeyapp_feedback_title));
    this.mContext = this;
    Object localObject1 = getIntent().getExtras();
    if (localObject1 != null)
    {
      this.mUrl = ((Bundle)localObject1).getString("url");
      this.initialUserName = ((Bundle)localObject1).getString("initialUserName");
      this.initialUserEmail = ((Bundle)localObject1).getString("initialUserEmail");
      localObject1 = ((Bundle)localObject1).getParcelableArray("initialAttachments");
      if (localObject1 != null)
      {
        this.mInitialAttachments = new ArrayList();
        int j = localObject1.length;
        int i = 0;
        while (i < j)
        {
          Object localObject2 = localObject1[i];
          this.mInitialAttachments.add((Uri)localObject2);
          i += 1;
        }
      }
    }
    if (paramBundle != null)
    {
      this.mFeedbackViewInitialized = paramBundle.getBoolean("feedbackViewInitialized");
      this.mInSendFeedback = paramBundle.getBoolean("inSendFeedback");
    }
    for (;;)
    {
      ((NotificationManager)getSystemService("notification")).cancel(2);
      initFeedbackHandler();
      initParseFeedbackHandler();
      configureAppropriateView();
      return;
      this.mInSendFeedback = false;
      this.mFeedbackViewInitialized = false;
    }
  }
  
  public void onCreateContextMenu(ContextMenu paramContextMenu, View paramView, ContextMenu.ContextMenuInfo paramContextMenuInfo)
  {
    super.onCreateContextMenu(paramContextMenu, paramView, paramContextMenuInfo);
    paramContextMenu.add(0, 2, 0, getString(R.string.hockeyapp_feedback_attach_file));
    paramContextMenu.add(0, 1, 0, getString(R.string.hockeyapp_feedback_attach_picture));
  }
  
  protected Dialog onCreateDialog(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return null;
    }
    new AlertDialog.Builder(this).setMessage(getString(R.string.hockeyapp_dialog_error_message)).setCancelable(false).setTitle(getString(R.string.hockeyapp_dialog_error_title)).setIcon(17301543).setPositiveButton(getString(R.string.hockeyapp_dialog_positive_button), new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        FeedbackActivity.access$002(FeedbackActivity.this, null);
        paramAnonymousDialogInterface.cancel();
      }
    }).create();
  }
  
  public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
  {
    if (paramInt == 4)
    {
      if (this.mInSendFeedback)
      {
        this.mInSendFeedback = false;
        configureAppropriateView();
      }
      for (;;)
      {
        return true;
        finish();
      }
    }
    return super.onKeyDown(paramInt, paramKeyEvent);
  }
  
  protected void onPrepareDialog(int paramInt, Dialog paramDialog)
  {
    switch (paramInt)
    {
    default: 
      return;
    }
    paramDialog = (AlertDialog)paramDialog;
    if (this.mError != null)
    {
      paramDialog.setMessage(this.mError.getMessage());
      return;
    }
    paramDialog.setMessage(getString(R.string.hockeyapp_feedback_generic_error));
  }
  
  protected void onRestoreInstanceState(Bundle paramBundle)
  {
    if (paramBundle != null)
    {
      ViewGroup localViewGroup = (ViewGroup)findViewById(R.id.wrapper_attachments);
      Iterator localIterator = paramBundle.getParcelableArrayList("attachments").iterator();
      while (localIterator.hasNext())
      {
        Uri localUri = (Uri)localIterator.next();
        if (!this.mInitialAttachments.contains(localUri)) {
          localViewGroup.addView(new AttachmentView(this, localViewGroup, localUri, true));
        }
      }
      this.mFeedbackViewInitialized = paramBundle.getBoolean("feedbackViewInitialized");
    }
    super.onRestoreInstanceState(paramBundle);
  }
  
  public Object onRetainNonConfigurationInstance()
  {
    if (this.mSendFeedbackTask != null) {
      this.mSendFeedbackTask.detach();
    }
    return this.mSendFeedbackTask;
  }
  
  protected void onSaveInstanceState(Bundle paramBundle)
  {
    paramBundle.putParcelableArrayList("attachments", ((AttachmentListView)findViewById(R.id.wrapper_attachments)).getAttachments());
    paramBundle.putBoolean("feedbackViewInitialized", this.mFeedbackViewInitialized);
    paramBundle.putBoolean("inSendFeedback", this.mInSendFeedback);
    super.onSaveInstanceState(paramBundle);
  }
  
  protected void onSendFeedbackResult(boolean paramBoolean) {}
  
  protected void onStop()
  {
    super.onStop();
    if (this.mSendFeedbackTask != null) {
      this.mSendFeedbackTask.detach();
    }
  }
  
  private static class FeedbackHandler
    extends Handler
  {
    private final WeakReference<FeedbackActivity> mWeakFeedbackActivity;
    
    public FeedbackHandler(FeedbackActivity paramFeedbackActivity)
    {
      this.mWeakFeedbackActivity = new WeakReference(paramFeedbackActivity);
    }
    
    public void handleMessage(Message paramMessage)
    {
      boolean bool = false;
      ErrorObject localErrorObject = new ErrorObject();
      final FeedbackActivity localFeedbackActivity = (FeedbackActivity)this.mWeakFeedbackActivity.get();
      if (localFeedbackActivity == null) {
        return;
      }
      Object localObject;
      String str;
      if ((paramMessage != null) && (paramMessage.getData() != null))
      {
        localObject = paramMessage.getData();
        paramMessage = ((Bundle)localObject).getString("feedback_response");
        str = ((Bundle)localObject).getString("feedback_status");
        localObject = ((Bundle)localObject).getString("request_type");
        if ((((String)localObject).equals("send")) && ((paramMessage == null) || (Integer.parseInt(str) != 201))) {
          localErrorObject.setMessage(localFeedbackActivity.getString(R.string.hockeyapp_feedback_send_generic_error));
        }
      }
      for (;;)
      {
        FeedbackActivity.access$002(localFeedbackActivity, localErrorObject);
        if (!bool) {
          localFeedbackActivity.runOnUiThread(new Runnable()
          {
            public void run()
            {
              localFeedbackActivity.enableDisableSendFeedbackButton(true);
              localFeedbackActivity.showDialog(0);
            }
          });
        }
        localFeedbackActivity.onSendFeedbackResult(bool);
        return;
        if ((((String)localObject).equals("fetch")) && (str != null) && ((Integer.parseInt(str) == 404) || (Integer.parseInt(str) == 422)))
        {
          localFeedbackActivity.resetFeedbackView();
          bool = true;
        }
        else if (paramMessage != null)
        {
          localFeedbackActivity.startParseFeedbackTask(paramMessage, (String)localObject);
          bool = true;
        }
        else
        {
          localErrorObject.setMessage(localFeedbackActivity.getString(R.string.hockeyapp_feedback_send_network_error));
          continue;
          localErrorObject.setMessage(localFeedbackActivity.getString(R.string.hockeyapp_feedback_send_generic_error));
        }
      }
    }
  }
  
  private static class ParseFeedbackHandler
    extends Handler
  {
    private final WeakReference<FeedbackActivity> mWeakFeedbackActivity;
    
    public ParseFeedbackHandler(FeedbackActivity paramFeedbackActivity)
    {
      this.mWeakFeedbackActivity = new WeakReference(paramFeedbackActivity);
    }
    
    public void handleMessage(Message paramMessage)
    {
      int j = 0;
      final FeedbackActivity localFeedbackActivity = (FeedbackActivity)this.mWeakFeedbackActivity.get();
      if (localFeedbackActivity == null) {
        return;
      }
      FeedbackActivity.access$002(localFeedbackActivity, new ErrorObject());
      int i = j;
      if (paramMessage != null)
      {
        i = j;
        if (paramMessage.getData() != null)
        {
          paramMessage = (FeedbackResponse)paramMessage.getData().getSerializable("parse_feedback_response");
          i = j;
          if (paramMessage != null)
          {
            if (!paramMessage.getStatus().equalsIgnoreCase("success")) {
              break label143;
            }
            j = 1;
            i = j;
            if (paramMessage.getToken() != null)
            {
              PrefsUtil.getInstance().saveFeedbackTokenToPrefs(localFeedbackActivity, paramMessage.getToken());
              localFeedbackActivity.loadFeedbackMessages(paramMessage);
              FeedbackActivity.access$902(localFeedbackActivity, false);
            }
          }
        }
      }
      label143:
      for (i = j;; i = 0)
      {
        if (i == 0) {
          localFeedbackActivity.runOnUiThread(new Runnable()
          {
            public void run()
            {
              localFeedbackActivity.showDialog(0);
            }
          });
        }
        localFeedbackActivity.enableDisableSendFeedbackButton(true);
        return;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/FeedbackActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */