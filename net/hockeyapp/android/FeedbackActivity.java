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
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
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

public class FeedbackActivity extends Activity implements OnClickListener {
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
    private boolean mForceNewThread;
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

    private static class FeedbackHandler extends Handler {
        private final WeakReference<FeedbackActivity> mWeakFeedbackActivity;

        public FeedbackHandler(FeedbackActivity feedbackActivity) {
            this.mWeakFeedbackActivity = new WeakReference(feedbackActivity);
        }

        public void handleMessage(Message msg) {
            boolean success = false;
            ErrorObject error = new ErrorObject();
            final FeedbackActivity feedbackActivity = (FeedbackActivity) this.mWeakFeedbackActivity.get();
            if (feedbackActivity != null) {
                if (msg == null || msg.getData() == null) {
                    error.setMessage(feedbackActivity.getString(R.string.hockeyapp_feedback_send_generic_error));
                } else {
                    Bundle bundle = msg.getData();
                    String responseString = bundle.getString("feedback_response");
                    String statusCode = bundle.getString("feedback_status");
                    String requestType = bundle.getString("request_type");
                    if ("send".equals(requestType) && (responseString == null || Integer.parseInt(statusCode) != 201)) {
                        error.setMessage(feedbackActivity.getString(R.string.hockeyapp_feedback_send_generic_error));
                    } else if ("fetch".equals(requestType) && statusCode != null && (Integer.parseInt(statusCode) == 404 || Integer.parseInt(statusCode) == 422)) {
                        feedbackActivity.resetFeedbackView();
                        success = true;
                    } else if (responseString != null) {
                        feedbackActivity.startParseFeedbackTask(responseString, requestType);
                        success = true;
                    } else {
                        error.setMessage(feedbackActivity.getString(R.string.hockeyapp_feedback_send_network_error));
                    }
                }
                feedbackActivity.mError = error;
                if (!success) {
                    feedbackActivity.runOnUiThread(new Runnable() {
                        public void run() {
                            feedbackActivity.enableDisableSendFeedbackButton(true);
                            feedbackActivity.showDialog(0);
                        }
                    });
                }
                feedbackActivity.onSendFeedbackResult(success);
            }
        }
    }

    private static class ParseFeedbackHandler extends Handler {
        private final WeakReference<FeedbackActivity> mWeakFeedbackActivity;

        public ParseFeedbackHandler(FeedbackActivity feedbackActivity) {
            this.mWeakFeedbackActivity = new WeakReference(feedbackActivity);
        }

        public void handleMessage(Message msg) {
            boolean success = false;
            final FeedbackActivity feedbackActivity = (FeedbackActivity) this.mWeakFeedbackActivity.get();
            if (feedbackActivity != null) {
                feedbackActivity.mError = new ErrorObject();
                if (!(msg == null || msg.getData() == null)) {
                    FeedbackResponse feedbackResponse = (FeedbackResponse) msg.getData().getSerializable("parse_feedback_response");
                    if (feedbackResponse != null) {
                        if (feedbackResponse.getStatus().equalsIgnoreCase("success")) {
                            success = true;
                            if (feedbackResponse.getToken() != null) {
                                PrefsUtil.getInstance().saveFeedbackTokenToPrefs(feedbackActivity, feedbackResponse.getToken());
                                feedbackActivity.loadFeedbackMessages(feedbackResponse);
                                feedbackActivity.mInSendFeedback = false;
                            }
                        } else {
                            success = false;
                        }
                    }
                }
                if (!success) {
                    feedbackActivity.runOnUiThread(new Runnable() {
                        public void run() {
                            feedbackActivity.showDialog(0);
                        }
                    });
                }
                feedbackActivity.enableDisableSendFeedbackButton(true);
            }
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutView());
        setTitle(getString(R.string.hockeyapp_feedback_title));
        this.mContext = this;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.mUrl = extras.getString(UpdateFragment.FRAGMENT_URL);
            this.mForceNewThread = extras.getBoolean("forceNewThread");
            this.initialUserName = extras.getString("initialUserName");
            this.initialUserEmail = extras.getString("initialUserEmail");
            Parcelable[] initialAttachmentsArray = extras.getParcelableArray("initialAttachments");
            if (initialAttachmentsArray != null) {
                this.mInitialAttachments = new ArrayList();
                for (Parcelable parcelable : initialAttachmentsArray) {
                    this.mInitialAttachments.add((Uri) parcelable);
                }
            }
        }
        if (savedInstanceState != null) {
            this.mFeedbackViewInitialized = savedInstanceState.getBoolean("feedbackViewInitialized");
            this.mInSendFeedback = savedInstanceState.getBoolean("inSendFeedback");
        } else {
            this.mInSendFeedback = false;
            this.mFeedbackViewInitialized = false;
        }
        ((NotificationManager) getSystemService("notification")).cancel(2);
        initFeedbackHandler();
        initParseFeedbackHandler();
        configureAppropriateView();
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            ViewGroup attachmentList = (ViewGroup) findViewById(R.id.wrapper_attachments);
            Iterator it = savedInstanceState.getParcelableArrayList("attachments").iterator();
            while (it.hasNext()) {
                Uri attachmentUri = (Uri) it.next();
                if (!this.mInitialAttachments.contains(attachmentUri)) {
                    attachmentList.addView(new AttachmentView((Context) this, attachmentList, attachmentUri, true));
                }
            }
            this.mFeedbackViewInitialized = savedInstanceState.getBoolean("feedbackViewInitialized");
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("attachments", ((AttachmentListView) findViewById(R.id.wrapper_attachments)).getAttachments());
        outState.putBoolean("feedbackViewInitialized", this.mFeedbackViewInitialized);
        outState.putBoolean("inSendFeedback", this.mInSendFeedback);
        super.onSaveInstanceState(outState);
    }

    protected void onStop() {
        super.onStop();
        if (this.mSendFeedbackTask != null) {
            this.mSendFeedbackTask.detach();
        }
    }

    public Object onRetainNonConfigurationInstance() {
        if (this.mSendFeedbackTask != null) {
            this.mSendFeedbackTask.detach();
        }
        return this.mSendFeedbackTask;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != 4) {
            return super.onKeyDown(keyCode, event);
        }
        if (this.mInSendFeedback) {
            this.mInSendFeedback = false;
            configureAppropriateView();
        } else {
            finish();
        }
        return true;
    }

    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.button_send) {
            sendFeedback();
        } else if (viewId == R.id.button_attachment) {
            if (((ViewGroup) findViewById(R.id.wrapper_attachments)).getChildCount() >= 3) {
                Toast.makeText(this, String.valueOf(3), 0).show();
            } else {
                openContextMenu(v);
            }
        } else if (viewId == R.id.button_add_response) {
            this.mInSendFeedback = true;
            configureFeedbackView(false);
        } else if (viewId == R.id.button_refresh) {
            sendFetchFeedback(this.mUrl, null, null, null, null, null, PrefsUtil.getInstance().getFeedbackTokenFromPrefs(this.mContext), this.mFeedbackHandler, true);
        }
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, 2, 0, getString(R.string.hockeyapp_feedback_attach_file));
        menu.add(0, 1, 0, getString(R.string.hockeyapp_feedback_attach_picture));
    }

    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
            case 2:
                return addAttachment(item.getItemId());
            default:
                return super.onContextItemSelected(item);
        }
    }

    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case 0:
                return new Builder(this).setMessage(getString(R.string.hockeyapp_dialog_error_message)).setCancelable(false).setTitle(getString(R.string.hockeyapp_dialog_error_title)).setIcon(17301543).setPositiveButton(getString(R.string.hockeyapp_dialog_positive_button), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        FeedbackActivity.this.mError = null;
                        dialog.cancel();
                    }
                }).create();
            default:
                return null;
        }
    }

    protected void onPrepareDialog(int id, Dialog dialog) {
        switch (id) {
            case 0:
                AlertDialog messageDialogError = (AlertDialog) dialog;
                if (this.mError != null) {
                    messageDialogError.setMessage(this.mError.getMessage());
                    return;
                } else {
                    messageDialogError.setMessage(getString(R.string.hockeyapp_feedback_generic_error));
                    return;
                }
            default:
                return;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == -1) {
            Uri uri;
            ViewGroup attachments;
            if (requestCode == 2) {
                uri = data.getData();
                if (uri != null) {
                    attachments = (ViewGroup) findViewById(R.id.wrapper_attachments);
                    attachments.addView(new AttachmentView((Context) this, attachments, uri, true));
                }
            } else if (requestCode == 1) {
                uri = data.getData();
                if (uri != null) {
                    try {
                        Intent intent = new Intent(this, PaintActivity.class);
                        intent.putExtra("imageUri", uri);
                        startActivityForResult(intent, 3);
                    } catch (ActivityNotFoundException e) {
                        HockeyLog.error("HockeyApp", "Paint activity not declared!", e);
                    }
                }
            } else if (requestCode == 3) {
                uri = (Uri) data.getParcelableExtra("imageUri");
                if (uri != null) {
                    attachments = (ViewGroup) findViewById(R.id.wrapper_attachments);
                    attachments.addView(new AttachmentView((Context) this, attachments, uri, true));
                }
            }
        }
    }

    @SuppressLint({"InflateParams"})
    public View getLayoutView() {
        return getLayoutInflater().inflate(R.layout.hockeyapp_activity_feedback, null);
    }

    public void enableDisableSendFeedbackButton(boolean isEnable) {
        if (this.mSendFeedbackButton != null) {
            this.mSendFeedbackButton.setEnabled(isEnable);
        }
    }

    protected void configureFeedbackView(boolean haveToken) {
        this.mFeedbackScrollview = (ScrollView) findViewById(R.id.wrapper_feedback_scroll);
        this.mWrapperLayoutFeedbackAndMessages = (LinearLayout) findViewById(R.id.wrapper_messages);
        this.mMessagesListView = (ListView) findViewById(R.id.list_feedback_messages);
        if (haveToken) {
            this.mWrapperLayoutFeedbackAndMessages.setVisibility(0);
            this.mFeedbackScrollview.setVisibility(8);
            this.mLastUpdatedTextView = (TextView) findViewById(R.id.label_last_updated);
            this.mLastUpdatedTextView.setVisibility(4);
            this.mAddResponseButton = (Button) findViewById(R.id.button_add_response);
            this.mAddResponseButton.setOnClickListener(this);
            this.mRefreshButton = (Button) findViewById(R.id.button_refresh);
            this.mRefreshButton.setOnClickListener(this);
            return;
        }
        int i;
        this.mWrapperLayoutFeedbackAndMessages.setVisibility(8);
        this.mFeedbackScrollview.setVisibility(0);
        this.mNameInput = (EditText) findViewById(R.id.input_name);
        this.mEmailInput = (EditText) findViewById(R.id.input_email);
        this.mSubjectInput = (EditText) findViewById(R.id.input_subject);
        this.mTextInput = (EditText) findViewById(R.id.input_message);
        if (!this.mFeedbackViewInitialized) {
            String nameEmailSubject = PrefsUtil.getInstance().getNameEmailFromPrefs(this.mContext);
            if (nameEmailSubject != null) {
                String[] nameEmailSubjectArray = nameEmailSubject.split("\\|");
                if (nameEmailSubjectArray != null && nameEmailSubjectArray.length >= 2) {
                    this.mNameInput.setText(nameEmailSubjectArray[0]);
                    this.mEmailInput.setText(nameEmailSubjectArray[1]);
                    if (this.mForceNewThread || nameEmailSubjectArray.length < 3) {
                        this.mSubjectInput.requestFocus();
                    } else {
                        this.mSubjectInput.setText(nameEmailSubjectArray[2]);
                        this.mTextInput.requestFocus();
                    }
                }
            } else {
                this.mNameInput.setText(this.initialUserName);
                this.mEmailInput.setText(this.initialUserEmail);
                this.mSubjectInput.setText(TtmlNode.ANONYMOUS_REGION_ID);
                if (TextUtils.isEmpty(this.initialUserName)) {
                    this.mNameInput.requestFocus();
                } else if (TextUtils.isEmpty(this.initialUserEmail)) {
                    this.mEmailInput.requestFocus();
                } else {
                    this.mSubjectInput.requestFocus();
                }
            }
            this.mFeedbackViewInitialized = true;
        }
        EditText editText = this.mNameInput;
        if (FeedbackManager.getRequireUserName() == FeedbackUserDataElement.DONT_SHOW) {
            i = 8;
        } else {
            i = 0;
        }
        editText.setVisibility(i);
        editText = this.mEmailInput;
        if (FeedbackManager.getRequireUserEmail() == FeedbackUserDataElement.DONT_SHOW) {
            i = 8;
        } else {
            i = 0;
        }
        editText.setVisibility(i);
        this.mTextInput.setText(TtmlNode.ANONYMOUS_REGION_ID);
        if ((!this.mForceNewThread || this.mInSendFeedback) && PrefsUtil.getInstance().getFeedbackTokenFromPrefs(this.mContext) != null) {
            this.mSubjectInput.setVisibility(8);
        } else {
            this.mSubjectInput.setVisibility(0);
        }
        ViewGroup attachmentListView = (ViewGroup) findViewById(R.id.wrapper_attachments);
        attachmentListView.removeAllViews();
        if (this.mInitialAttachments != null) {
            for (Uri attachmentUri : this.mInitialAttachments) {
                attachmentListView.addView(new AttachmentView((Context) this, attachmentListView, attachmentUri, true));
            }
        }
        this.mAddAttachmentButton = (Button) findViewById(R.id.button_attachment);
        this.mAddAttachmentButton.setOnClickListener(this);
        registerForContextMenu(this.mAddAttachmentButton);
        this.mSendFeedbackButton = (Button) findViewById(R.id.button_send);
        this.mSendFeedbackButton.setOnClickListener(this);
    }

    protected void onSendFeedbackResult(boolean success) {
    }

    private boolean addAttachment(int request) {
        Intent intent;
        if (request == 2) {
            intent = new Intent();
            intent.setType("*/*");
            intent.setAction("android.intent.action.GET_CONTENT");
            startActivityForResult(Intent.createChooser(intent, getString(R.string.hockeyapp_feedback_select_file)), 2);
            return true;
        } else if (request != 1) {
            return false;
        } else {
            intent = new Intent();
            intent.setType("image/*");
            intent.setAction("android.intent.action.GET_CONTENT");
            startActivityForResult(Intent.createChooser(intent, getString(R.string.hockeyapp_feedback_select_picture)), 1);
            return true;
        }
    }

    private void configureAppropriateView() {
        if (!this.mForceNewThread || this.mInSendFeedback) {
            this.mToken = PrefsUtil.getInstance().getFeedbackTokenFromPrefs(this);
        }
        if (this.mToken == null || this.mInSendFeedback) {
            configureFeedbackView(false);
            return;
        }
        configureFeedbackView(true);
        sendFetchFeedback(this.mUrl, null, null, null, null, null, this.mToken, this.mFeedbackHandler, true);
    }

    private void createParseFeedbackTask(String feedbackResponseString, String requestType) {
        this.mParseFeedbackTask = new ParseFeedbackTask(this, feedbackResponseString, this.mParseFeedbackHandler, requestType);
    }

    private void hideKeyboard() {
        if (this.mTextInput != null) {
            ((InputMethodManager) getSystemService("input_method")).hideSoftInputFromWindow(this.mTextInput.getWindowToken(), 0);
        }
    }

    private void initFeedbackHandler() {
        this.mFeedbackHandler = new FeedbackHandler(this);
    }

    private void initParseFeedbackHandler() {
        this.mParseFeedbackHandler = new ParseFeedbackHandler(this);
    }

    @SuppressLint({"SimpleDateFormat"})
    private void loadFeedbackMessages(final FeedbackResponse feedbackResponse) {
        runOnUiThread(new Runnable() {
            public void run() {
                FeedbackActivity.this.configureFeedbackView(true);
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                SimpleDateFormat formatNew = new SimpleDateFormat("d MMM h:mm a");
                if (feedbackResponse != null && feedbackResponse.getFeedback() != null && feedbackResponse.getFeedback().getMessages() != null && feedbackResponse.getFeedback().getMessages().size() > 0) {
                    FeedbackActivity.this.mFeedbackMessages = feedbackResponse.getFeedback().getMessages();
                    Collections.reverse(FeedbackActivity.this.mFeedbackMessages);
                    try {
                        Date date = format.parse(((FeedbackMessage) FeedbackActivity.this.mFeedbackMessages.get(0)).getCreatedAt());
                        FeedbackActivity.this.mLastUpdatedTextView.setText(String.format(FeedbackActivity.this.getString(R.string.hockeyapp_feedback_last_updated_text), new Object[]{formatNew.format(date)}));
                        FeedbackActivity.this.mLastUpdatedTextView.setVisibility(0);
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                    if (FeedbackActivity.this.mMessagesAdapter == null) {
                        FeedbackActivity.this.mMessagesAdapter = new MessagesAdapter(FeedbackActivity.this.mContext, FeedbackActivity.this.mFeedbackMessages);
                    } else {
                        FeedbackActivity.this.mMessagesAdapter.clear();
                        Iterator it = FeedbackActivity.this.mFeedbackMessages.iterator();
                        while (it.hasNext()) {
                            FeedbackActivity.this.mMessagesAdapter.add((FeedbackMessage) it.next());
                        }
                        FeedbackActivity.this.mMessagesAdapter.notifyDataSetChanged();
                    }
                    FeedbackActivity.this.mMessagesListView.setAdapter(FeedbackActivity.this.mMessagesAdapter);
                }
            }
        });
    }

    private void resetFeedbackView() {
        runOnUiThread(new Runnable() {
            public void run() {
                PrefsUtil.getInstance().saveFeedbackTokenToPrefs(FeedbackActivity.this, null);
                FeedbackActivity.this.getSharedPreferences("net.hockeyapp.android.feedback", 0).edit().remove("idLastMessageSend").remove("idLastMessageProcessed").apply();
                FeedbackActivity.this.configureFeedbackView(false);
            }
        });
    }

    private void sendFeedback() {
        if (Util.isConnectedToNetwork(this)) {
            enableDisableSendFeedbackButton(false);
            hideKeyboard();
            String token = (!this.mForceNewThread || this.mInSendFeedback) ? PrefsUtil.getInstance().getFeedbackTokenFromPrefs(this.mContext) : null;
            String name = this.mNameInput.getText().toString().trim();
            String email = this.mEmailInput.getText().toString().trim();
            String subject = this.mSubjectInput.getText().toString().trim();
            String text = this.mTextInput.getText().toString().trim();
            if (TextUtils.isEmpty(subject)) {
                this.mSubjectInput.setVisibility(0);
                setError(this.mSubjectInput, R.string.hockeyapp_feedback_validate_subject_error);
                return;
            } else if (FeedbackManager.getRequireUserName() == FeedbackUserDataElement.REQUIRED && TextUtils.isEmpty(name)) {
                setError(this.mNameInput, R.string.hockeyapp_feedback_validate_name_error);
                return;
            } else if (FeedbackManager.getRequireUserEmail() == FeedbackUserDataElement.REQUIRED && TextUtils.isEmpty(email)) {
                setError(this.mEmailInput, R.string.hockeyapp_feedback_validate_email_empty);
                return;
            } else if (TextUtils.isEmpty(text)) {
                setError(this.mTextInput, R.string.hockeyapp_feedback_validate_text_error);
                return;
            } else if (FeedbackManager.getRequireUserEmail() != FeedbackUserDataElement.REQUIRED || Util.isValidEmail(email)) {
                PrefsUtil.getInstance().saveNameEmailSubjectToPrefs(this.mContext, name, email, subject);
                sendFetchFeedback(this.mUrl, name, email, subject, text, ((AttachmentListView) findViewById(R.id.wrapper_attachments)).getAttachments(), token, this.mFeedbackHandler, false);
                return;
            } else {
                setError(this.mEmailInput, R.string.hockeyapp_feedback_validate_email_error);
                return;
            }
        }
        Toast.makeText(this, R.string.hockeyapp_error_no_network_message, 1).show();
    }

    private void setError(EditText inputField, int feedbackStringId) {
        inputField.setError(getString(feedbackStringId));
        enableDisableSendFeedbackButton(true);
    }

    private void sendFetchFeedback(String url, String name, String email, String subject, String text, List<Uri> attachmentUris, String token, Handler feedbackHandler, boolean isFetchMessages) {
        this.mSendFeedbackTask = new SendFeedbackTask(this.mContext, url, name, email, subject, text, attachmentUris, token, feedbackHandler, isFetchMessages);
        AsyncTaskUtils.execute(this.mSendFeedbackTask);
    }

    private void startParseFeedbackTask(String feedbackResponseString, String requestType) {
        createParseFeedbackTask(feedbackResponseString, requestType);
        AsyncTaskUtils.execute(this.mParseFeedbackTask);
    }
}
