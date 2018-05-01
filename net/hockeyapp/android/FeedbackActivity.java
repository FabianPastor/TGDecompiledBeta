package net.hockeyapp.android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import net.hockeyapp.android.adapters.MessagesAdapter;
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
  implements View.OnClickListener, View.OnFocusChangeListener
{
  private AttachmentListView mAttachmentListView;
  private Context mContext;
  private EditText mEmailInput;
  private Handler mFeedbackHandler;
  private boolean mFeedbackViewInitialized;
  private boolean mForceNewThread;
  private boolean mInSendFeedback;
  private List<Uri> mInitialAttachments = new ArrayList();
  private String mInitialUserEmail;
  private String mInitialUserName;
  private String mInitialUserSubject;
  private TextView mLastUpdatedTextView;
  private MessagesAdapter mMessagesAdapter;
  private ListView mMessagesListView;
  private EditText mNameInput;
  private Handler mParseFeedbackHandler;
  private ParseFeedbackTask mParseFeedbackTask;
  private Button mSendFeedbackButton;
  private SendFeedbackTask mSendFeedbackTask;
  private EditText mSubjectInput;
  private EditText mTextInput;
  private String mToken;
  private String mUrl;
  
  private boolean addAttachment(int paramInt)
  {
    boolean bool = true;
    Intent localIntent;
    if (paramInt == 2)
    {
      localIntent = new Intent();
      localIntent.setType("*/*");
      localIntent.setAction("android.intent.action.GET_CONTENT");
      startActivityForResult(Intent.createChooser(localIntent, getString(R.string.hockeyapp_feedback_select_file)), 2);
    }
    for (;;)
    {
      return bool;
      if (paramInt == 1)
      {
        localIntent = new Intent();
        localIntent.setType("image/*");
        localIntent.setAction("android.intent.action.GET_CONTENT");
        startActivityForResult(Intent.createChooser(localIntent, getString(R.string.hockeyapp_feedback_select_picture)), 1);
      }
      else
      {
        bool = false;
      }
    }
  }
  
  private void configureAppropriateView()
  {
    if ((this.mToken == null) || (this.mInSendFeedback)) {
      configureFeedbackView(false);
    }
    for (;;)
    {
      return;
      configureFeedbackView(true);
      sendFetchFeedback(this.mUrl, null, null, null, null, null, this.mToken, this.mFeedbackHandler, true);
    }
  }
  
  private void configureHints()
  {
    if (FeedbackManager.getRequireUserName() == FeedbackUserDataElement.REQUIRED) {
      this.mNameInput.setHint(getString(R.string.hockeyapp_feedback_name_hint_required));
    }
    if (FeedbackManager.getRequireUserEmail() == FeedbackUserDataElement.REQUIRED) {
      this.mEmailInput.setHint(getString(R.string.hockeyapp_feedback_email_hint_required));
    }
    this.mSubjectInput.setHint(getString(R.string.hockeyapp_feedback_subject_hint_required));
    this.mTextInput.setHint(getString(R.string.hockeyapp_feedback_message_hint_required));
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
  private void loadFeedbackMessages(FeedbackResponse paramFeedbackResponse)
  {
    configureFeedbackView(true);
    if ((paramFeedbackResponse != null) && (paramFeedbackResponse.getFeedback() != null) && (paramFeedbackResponse.getFeedback().getMessages() != null) && (paramFeedbackResponse.getFeedback().getMessages().size() > 0))
    {
      paramFeedbackResponse = paramFeedbackResponse.getFeedback().getMessages();
      Collections.reverse(paramFeedbackResponse);
    }
    try
    {
      Object localObject = new java/text/SimpleDateFormat;
      ((SimpleDateFormat)localObject).<init>("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
      ((DateFormat)localObject).setTimeZone(TimeZone.getTimeZone("UTC"));
      DateFormat localDateFormat = DateFormat.getDateTimeInstance(3, 3);
      localObject = ((DateFormat)localObject).parse(((FeedbackMessage)paramFeedbackResponse.get(0)).getCreatedAt());
      this.mLastUpdatedTextView.setText(String.format(getString(R.string.hockeyapp_feedback_last_updated_text), new Object[] { localDateFormat.format((Date)localObject) }));
      this.mLastUpdatedTextView.setContentDescription(this.mLastUpdatedTextView.getText());
      this.mLastUpdatedTextView.setVisibility(0);
      if (this.mMessagesAdapter == null)
      {
        this.mMessagesAdapter = new MessagesAdapter(this.mContext, paramFeedbackResponse);
        this.mMessagesListView.setAdapter(this.mMessagesAdapter);
        return;
      }
    }
    catch (ParseException localParseException)
    {
      for (;;)
      {
        HockeyLog.error("Failed to parse feedback", localParseException);
        continue;
        this.mMessagesAdapter.clear();
        Iterator localIterator = paramFeedbackResponse.iterator();
        while (localIterator.hasNext())
        {
          paramFeedbackResponse = (FeedbackMessage)localIterator.next();
          this.mMessagesAdapter.add(paramFeedbackResponse);
        }
        this.mMessagesAdapter.notifyDataSetChanged();
      }
    }
  }
  
  @SuppressLint({"StaticFieldLeak"})
  private void resetFeedbackView()
  {
    this.mToken = null;
    AsyncTaskUtils.execute(new AsyncTask()
    {
      protected Object doInBackground(Void... paramAnonymousVarArgs)
      {
        PrefsUtil.getInstance().saveFeedbackTokenToPrefs(FeedbackActivity.this, null);
        FeedbackActivity.this.getSharedPreferences("net.hockeyapp.android.feedback", 0).edit().remove("idLastMessageSend").remove("idLastMessageProcessed").apply();
        return null;
      }
    });
    configureFeedbackView(false);
  }
  
  private void restoreSendFeedbackTask()
  {
    Object localObject = getLastNonConfigurationInstance();
    if ((localObject != null) && ((localObject instanceof SendFeedbackTask)))
    {
      this.mSendFeedbackTask = ((SendFeedbackTask)localObject);
      this.mSendFeedbackTask.setHandler(this.mFeedbackHandler);
    }
  }
  
  @SuppressLint({"StaticFieldLeak"})
  private void sendFeedback()
  {
    if (!Util.isConnectedToNetwork(this)) {
      Toast.makeText(this, R.string.hockeyapp_error_no_network_message, 1).show();
    }
    for (;;)
    {
      return;
      enableDisableSendFeedbackButton(false);
      if ((this.mForceNewThread) && (!this.mInSendFeedback)) {}
      final String str2;
      final String str3;
      final String str4;
      String str5;
      for (String str1 = null;; str1 = this.mToken)
      {
        str2 = this.mNameInput.getText().toString().trim();
        str3 = this.mEmailInput.getText().toString().trim();
        str4 = this.mSubjectInput.getText().toString().trim();
        str5 = this.mTextInput.getText().toString().trim();
        if (!TextUtils.isEmpty(str4)) {
          break label136;
        }
        this.mSubjectInput.setVisibility(0);
        setError(this.mSubjectInput, R.string.hockeyapp_feedback_validate_subject_error);
        break;
      }
      label136:
      if ((FeedbackManager.getRequireUserName() == FeedbackUserDataElement.REQUIRED) && (TextUtils.isEmpty(str2)))
      {
        setError(this.mNameInput, R.string.hockeyapp_feedback_validate_name_error);
      }
      else if ((FeedbackManager.getRequireUserEmail() == FeedbackUserDataElement.REQUIRED) && (TextUtils.isEmpty(str3)))
      {
        setError(this.mEmailInput, R.string.hockeyapp_feedback_validate_email_empty);
      }
      else if (TextUtils.isEmpty(str5))
      {
        setError(this.mTextInput, R.string.hockeyapp_feedback_validate_text_error);
      }
      else if ((FeedbackManager.getRequireUserEmail() == FeedbackUserDataElement.REQUIRED) && (!Util.isValidEmail(str3)))
      {
        setError(this.mEmailInput, R.string.hockeyapp_feedback_validate_email_error);
      }
      else
      {
        AsyncTaskUtils.execute(new AsyncTask()
        {
          protected Object doInBackground(Void... paramAnonymousVarArgs)
          {
            PrefsUtil.getInstance().saveNameEmailSubjectToPrefs(FeedbackActivity.this.mContext, str2, str3, str4);
            return null;
          }
        });
        ArrayList localArrayList = this.mAttachmentListView.getAttachments();
        sendFetchFeedback(this.mUrl, str2, str3, str4, str5, localArrayList, str1, this.mFeedbackHandler, false);
        hideKeyboard();
      }
    }
  }
  
  private void sendFetchFeedback(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, List<Uri> paramList, String paramString6, Handler paramHandler, boolean paramBoolean)
  {
    this.mSendFeedbackTask = new SendFeedbackTask(this.mContext, paramString1, paramString2, paramString3, paramString4, paramString5, paramList, paramString6, paramHandler, paramBoolean);
    AsyncTaskUtils.execute(this.mSendFeedbackTask);
  }
  
  private void setError(final EditText paramEditText, int paramInt)
  {
    paramEditText.setError(getString(paramInt));
    new Handler(Looper.getMainLooper()).post(new Runnable()
    {
      public void run()
      {
        paramEditText.requestFocus();
      }
    });
    enableDisableSendFeedbackButton(true);
  }
  
  private void showError(int paramInt)
  {
    new AlertDialog.Builder(this).setTitle(R.string.hockeyapp_dialog_error_title).setMessage(paramInt).setCancelable(false).setPositiveButton(R.string.hockeyapp_dialog_positive_button, null).create().show();
  }
  
  private void showKeyboard(View paramView)
  {
    ((InputMethodManager)getSystemService("input_method")).showSoftInput(paramView, 1);
  }
  
  private void startParseFeedbackTask(String paramString1, String paramString2)
  {
    createParseFeedbackTask(paramString1, paramString2);
    AsyncTaskUtils.execute(this.mParseFeedbackTask);
  }
  
  protected void configureFeedbackView(boolean paramBoolean)
  {
    Object localObject1 = (ScrollView)findViewById(R.id.wrapper_feedback_scroll);
    Object localObject2 = (LinearLayout)findViewById(R.id.wrapper_messages);
    this.mMessagesListView = ((ListView)findViewById(R.id.list_feedback_messages));
    this.mAttachmentListView = ((AttachmentListView)findViewById(R.id.wrapper_attachments));
    if (paramBoolean)
    {
      ((LinearLayout)localObject2).setVisibility(0);
      ((ScrollView)localObject1).setVisibility(8);
      this.mLastUpdatedTextView = ((TextView)findViewById(R.id.label_last_updated));
      this.mLastUpdatedTextView.setVisibility(4);
      localObject1 = (Button)findViewById(R.id.button_add_response);
      ((Button)localObject1).setOnClickListener(this);
      ((Button)localObject1).setOnFocusChangeListener(this);
      localObject1 = (Button)findViewById(R.id.button_refresh);
      ((Button)localObject1).setOnClickListener(this);
      ((Button)localObject1).setOnFocusChangeListener(this);
    }
    for (;;)
    {
      return;
      ((LinearLayout)localObject2).setVisibility(8);
      ((ScrollView)localObject1).setVisibility(0);
      this.mNameInput = ((EditText)findViewById(R.id.input_name));
      this.mNameInput.setOnFocusChangeListener(this);
      this.mEmailInput = ((EditText)findViewById(R.id.input_email));
      this.mEmailInput.setOnFocusChangeListener(this);
      this.mSubjectInput = ((EditText)findViewById(R.id.input_subject));
      this.mSubjectInput.setOnFocusChangeListener(this);
      this.mTextInput = ((EditText)findViewById(R.id.input_message));
      this.mTextInput.setOnFocusChangeListener(this);
      configureHints();
      int i;
      if (!this.mFeedbackViewInitialized)
      {
        this.mNameInput.setText(this.mInitialUserName);
        this.mEmailInput.setText(this.mInitialUserEmail);
        this.mSubjectInput.setText(this.mInitialUserSubject);
        if (TextUtils.isEmpty(this.mInitialUserName))
        {
          this.mNameInput.requestFocus();
          this.mFeedbackViewInitialized = true;
        }
      }
      else
      {
        localObject1 = this.mNameInput;
        if (FeedbackManager.getRequireUserName() != FeedbackUserDataElement.DONT_SHOW) {
          break label497;
        }
        i = 8;
        label314:
        ((EditText)localObject1).setVisibility(i);
        localObject1 = this.mEmailInput;
        if (FeedbackManager.getRequireUserEmail() != FeedbackUserDataElement.DONT_SHOW) {
          break label503;
        }
        i = 8;
        label338:
        ((EditText)localObject1).setVisibility(i);
        this.mTextInput.setText("");
        if (((this.mForceNewThread) && (!this.mInSendFeedback)) || (this.mToken == null)) {
          break label509;
        }
        this.mSubjectInput.setVisibility(8);
      }
      for (;;)
      {
        this.mAttachmentListView.removeAllViews();
        localObject1 = this.mInitialAttachments.iterator();
        while (((Iterator)localObject1).hasNext())
        {
          localObject2 = (Uri)((Iterator)localObject1).next();
          this.mAttachmentListView.addView(new AttachmentView(this, this.mAttachmentListView, (Uri)localObject2, true));
        }
        if (TextUtils.isEmpty(this.mInitialUserEmail))
        {
          this.mEmailInput.requestFocus();
          break;
        }
        if (TextUtils.isEmpty(this.mInitialUserSubject))
        {
          this.mSubjectInput.requestFocus();
          break;
        }
        this.mTextInput.requestFocus();
        break;
        label497:
        i = 0;
        break label314;
        label503:
        i = 0;
        break label338;
        label509:
        this.mSubjectInput.setVisibility(0);
      }
      localObject1 = (Button)findViewById(R.id.button_attachment);
      ((Button)localObject1).setOnClickListener(this);
      ((Button)localObject1).setOnFocusChangeListener(this);
      registerForContextMenu((View)localObject1);
      this.mSendFeedbackButton = ((Button)findViewById(R.id.button_send));
      this.mSendFeedbackButton.setOnClickListener(this);
      ((Button)localObject1).setOnFocusChangeListener(this);
    }
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
    for (;;)
    {
      return;
      if (paramInt1 == 2)
      {
        paramIntent = paramIntent.getData();
        if (paramIntent != null)
        {
          this.mAttachmentListView.addView(new AttachmentView(this, this.mAttachmentListView, paramIntent, true));
          Util.announceForAccessibility(this.mAttachmentListView, getString(R.string.hockeyapp_feedback_attachment_added));
        }
      }
      else if (paramInt1 == 1)
      {
        Uri localUri = paramIntent.getData();
        if (localUri != null) {
          try
          {
            paramIntent = new android/content/Intent;
            paramIntent.<init>(this, PaintActivity.class);
            paramIntent.putExtra("imageUri", localUri);
            startActivityForResult(paramIntent, 3);
          }
          catch (ActivityNotFoundException paramIntent)
          {
            HockeyLog.error("Paint activity not declared!", paramIntent);
          }
        }
      }
      else if (paramInt1 == 3)
      {
        paramIntent = (Uri)paramIntent.getParcelableExtra("imageUri");
        if (paramIntent != null)
        {
          this.mAttachmentListView.addView(new AttachmentView(this, this.mAttachmentListView, paramIntent, true));
          Util.announceForAccessibility(this.mAttachmentListView, getString(R.string.hockeyapp_feedback_attachment_added));
        }
      }
    }
  }
  
  public void onClick(View paramView)
  {
    int i = paramView.getId();
    if (i == R.id.button_send) {
      sendFeedback();
    }
    for (;;)
    {
      return;
      if (i == R.id.button_attachment)
      {
        if (this.mAttachmentListView.getChildCount() >= 3) {
          Toast.makeText(this, getString(R.string.hockeyapp_feedback_max_attachments_allowed, new Object[] { Integer.valueOf(3) }), 0).show();
        } else {
          openContextMenu(paramView);
        }
      }
      else if (i == R.id.button_add_response)
      {
        this.mInSendFeedback = true;
        configureFeedbackView(false);
      }
      else if (i == R.id.button_refresh)
      {
        sendFetchFeedback(this.mUrl, null, null, null, null, null, this.mToken, this.mFeedbackHandler, true);
      }
    }
  }
  
  public boolean onContextItemSelected(MenuItem paramMenuItem)
  {
    switch (paramMenuItem.getItemId())
    {
    }
    for (boolean bool = super.onContextItemSelected(paramMenuItem);; bool = addAttachment(paramMenuItem.getItemId())) {
      return bool;
    }
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(getLayoutView());
    setTitle(R.string.hockeyapp_feedback_title);
    this.mContext = this;
    Object localObject1 = getIntent().getExtras();
    if (localObject1 != null)
    {
      this.mUrl = ((Bundle)localObject1).getString("url");
      this.mToken = ((Bundle)localObject1).getString("token");
      this.mForceNewThread = ((Bundle)localObject1).getBoolean("forceNewThread");
      this.mInitialUserName = ((Bundle)localObject1).getString("initialUserName");
      this.mInitialUserEmail = ((Bundle)localObject1).getString("initialUserEmail");
      this.mInitialUserSubject = ((Bundle)localObject1).getString("initialUserSubject");
      localObject1 = ((Bundle)localObject1).getParcelableArray("initialAttachments");
      if (localObject1 != null)
      {
        this.mInitialAttachments.clear();
        int i = localObject1.length;
        for (int j = 0; j < i; j++)
        {
          Object localObject2 = localObject1[j];
          this.mInitialAttachments.add((Uri)localObject2);
        }
      }
    }
    if (paramBundle != null)
    {
      this.mFeedbackViewInitialized = paramBundle.getBoolean("feedbackViewInitialized");
      this.mInSendFeedback = paramBundle.getBoolean("inSendFeedback");
      this.mToken = paramBundle.getString("token");
    }
    for (;;)
    {
      Util.cancelNotification(this, 2);
      initFeedbackHandler();
      initParseFeedbackHandler();
      restoreSendFeedbackTask();
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
  
  public void onFocusChange(View paramView, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      if (!(paramView instanceof EditText)) {
        break label17;
      }
      showKeyboard(paramView);
    }
    for (;;)
    {
      return;
      label17:
      if (((paramView instanceof Button)) || ((paramView instanceof ImageButton))) {
        hideKeyboard();
      }
    }
  }
  
  public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
  {
    if (paramInt == 4) {
      if (this.mInSendFeedback)
      {
        this.mInSendFeedback = false;
        configureAppropriateView();
      }
    }
    for (boolean bool = true;; bool = super.onKeyDown(paramInt, paramKeyEvent))
    {
      return bool;
      finish();
      break;
    }
  }
  
  protected void onRestoreInstanceState(Bundle paramBundle)
  {
    if (paramBundle != null)
    {
      Object localObject = paramBundle.getParcelableArrayList("attachments");
      if (localObject != null)
      {
        localObject = ((ArrayList)localObject).iterator();
        while (((Iterator)localObject).hasNext())
        {
          Uri localUri = (Uri)((Iterator)localObject).next();
          if (!this.mInitialAttachments.contains(localUri)) {
            this.mAttachmentListView.addView(new AttachmentView(this, this.mAttachmentListView, localUri, true));
          }
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
    paramBundle.putParcelableArrayList("attachments", this.mAttachmentListView.getAttachments());
    paramBundle.putBoolean("feedbackViewInitialized", this.mFeedbackViewInitialized);
    paramBundle.putBoolean("inSendFeedback", this.mInSendFeedback);
    paramBundle.putString("token", this.mToken);
    super.onSaveInstanceState(paramBundle);
  }
  
  protected void onSendFeedbackResult(boolean paramBoolean) {}
  
  protected void onStart()
  {
    super.onStart();
    if (this.mSendFeedbackTask != null) {
      this.mSendFeedbackTask.attach(this);
    }
  }
  
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
    
    FeedbackHandler(FeedbackActivity paramFeedbackActivity)
    {
      this.mWeakFeedbackActivity = new WeakReference(paramFeedbackActivity);
    }
    
    public void handleMessage(Message paramMessage)
    {
      boolean bool = false;
      int i = 0;
      FeedbackActivity localFeedbackActivity = (FeedbackActivity)this.mWeakFeedbackActivity.get();
      if (localFeedbackActivity == null) {
        return;
      }
      Object localObject;
      String str;
      if ((paramMessage != null) && (paramMessage.getData() != null))
      {
        localObject = paramMessage.getData();
        str = ((Bundle)localObject).getString("feedback_response");
        paramMessage = ((Bundle)localObject).getString("feedback_status");
        localObject = ((Bundle)localObject).getString("request_type");
        if (("send".equals(localObject)) && ((str == null) || (Integer.parseInt(paramMessage) != 201))) {
          i = R.string.hockeyapp_feedback_send_generic_error;
        }
      }
      for (;;)
      {
        if (!bool) {
          localFeedbackActivity.showError(i);
        }
        localFeedbackActivity.onSendFeedbackResult(bool);
        break;
        if (("fetch".equals(localObject)) && (paramMessage != null) && ((Integer.parseInt(paramMessage) == 404) || (Integer.parseInt(paramMessage) == 422)))
        {
          localFeedbackActivity.resetFeedbackView();
          bool = true;
        }
        else if (str != null)
        {
          localFeedbackActivity.startParseFeedbackTask(str, (String)localObject);
          if ("send".equals(localObject))
          {
            paramMessage = localFeedbackActivity.mAttachmentListView.getAttachments();
            localFeedbackActivity.mInitialAttachments.removeAll(paramMessage);
            Toast.makeText(localFeedbackActivity, R.string.hockeyapp_feedback_sent_toast, 1).show();
          }
          bool = true;
        }
        else
        {
          i = R.string.hockeyapp_feedback_send_network_error;
          continue;
          i = R.string.hockeyapp_feedback_send_generic_error;
        }
      }
    }
  }
  
  private static class ParseFeedbackHandler
    extends Handler
  {
    private final WeakReference<FeedbackActivity> mWeakFeedbackActivity;
    
    ParseFeedbackHandler(FeedbackActivity paramFeedbackActivity)
    {
      this.mWeakFeedbackActivity = new WeakReference(paramFeedbackActivity);
    }
    
    @SuppressLint({"StaticFieldLeak"})
    public void handleMessage(final Message paramMessage)
    {
      int i = 0;
      final FeedbackActivity localFeedbackActivity = (FeedbackActivity)this.mWeakFeedbackActivity.get();
      if (localFeedbackActivity == null) {
        return;
      }
      int j = i;
      if (paramMessage != null)
      {
        j = i;
        if (paramMessage.getData() != null)
        {
          paramMessage = (FeedbackResponse)paramMessage.getData().getSerializable("parse_feedback_response");
          j = i;
          if (paramMessage != null)
          {
            if (!paramMessage.getStatus().equalsIgnoreCase("success")) {
              break label135;
            }
            i = 1;
            j = i;
            if (paramMessage.getToken() != null)
            {
              FeedbackActivity.access$602(localFeedbackActivity, paramMessage.getToken());
              AsyncTaskUtils.execute(new AsyncTask()
              {
                protected Object doInBackground(Void... paramAnonymousVarArgs)
                {
                  PrefsUtil.getInstance().saveFeedbackTokenToPrefs(localFeedbackActivity, paramMessage.getToken());
                  return null;
                }
              });
              localFeedbackActivity.loadFeedbackMessages(paramMessage);
              FeedbackActivity.access$802(localFeedbackActivity, false);
            }
          }
        }
      }
      label135:
      for (j = i;; j = 0)
      {
        if (j == 0) {
          localFeedbackActivity.showError(R.string.hockeyapp_dialog_error_message);
        }
        localFeedbackActivity.enableDisableSendFeedbackButton(true);
        break;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/FeedbackActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */