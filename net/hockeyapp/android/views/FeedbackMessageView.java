package net.hockeyapp.android.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import net.hockeyapp.android.R.color;
import net.hockeyapp.android.R.id;
import net.hockeyapp.android.R.layout;
import net.hockeyapp.android.objects.FeedbackAttachment;
import net.hockeyapp.android.objects.FeedbackMessage;
import net.hockeyapp.android.tasks.AttachmentDownloader;

public class FeedbackMessageView
  extends LinearLayout
{
  @SuppressLint({"SimpleDateFormat"})
  private static final SimpleDateFormat DATE_FORMAT_IN = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
  @SuppressLint({"SimpleDateFormat"})
  private static final SimpleDateFormat DATE_FORMAT_OUT = new SimpleDateFormat("d MMM h:mm a");
  private AttachmentListView mAttachmentListView;
  private TextView mAuthorTextView;
  private final Context mContext;
  private TextView mDateTextView;
  private FeedbackMessage mFeedbackMessage;
  private TextView mMessageTextView;
  @Deprecated
  private boolean ownMessage;
  
  public FeedbackMessageView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this.mContext = paramContext;
    LayoutInflater.from(paramContext).inflate(R.layout.hockeyapp_view_feedback_message, this);
    this.mAuthorTextView = ((TextView)findViewById(R.id.label_author));
    this.mDateTextView = ((TextView)findViewById(R.id.label_date));
    this.mMessageTextView = ((TextView)findViewById(R.id.label_text));
    this.mAttachmentListView = ((AttachmentListView)findViewById(R.id.list_attachments));
  }
  
  public void setFeedbackMessage(FeedbackMessage paramFeedbackMessage)
  {
    this.mFeedbackMessage = paramFeedbackMessage;
    try
    {
      paramFeedbackMessage = DATE_FORMAT_IN.parse(this.mFeedbackMessage.getCreatedAt());
      this.mDateTextView.setText(DATE_FORMAT_OUT.format(paramFeedbackMessage));
      this.mAuthorTextView.setText(this.mFeedbackMessage.getName());
      this.mMessageTextView.setText(this.mFeedbackMessage.getText());
      this.mAttachmentListView.removeAllViews();
      paramFeedbackMessage = this.mFeedbackMessage.getFeedbackAttachments().iterator();
      while (paramFeedbackMessage.hasNext())
      {
        FeedbackAttachment localFeedbackAttachment = (FeedbackAttachment)paramFeedbackMessage.next();
        AttachmentView localAttachmentView = new AttachmentView(this.mContext, this.mAttachmentListView, localFeedbackAttachment, false);
        AttachmentDownloader.getInstance().download(localFeedbackAttachment, localAttachmentView);
        this.mAttachmentListView.addView(localAttachmentView);
      }
    }
    catch (ParseException paramFeedbackMessage)
    {
      for (;;)
      {
        paramFeedbackMessage.printStackTrace();
      }
    }
  }
  
  public void setIndex(int paramInt)
  {
    if (paramInt % 2 == 0)
    {
      setBackgroundColor(getResources().getColor(R.color.hockeyapp_background_light));
      this.mAuthorTextView.setTextColor(getResources().getColor(R.color.hockeyapp_text_white));
      this.mDateTextView.setTextColor(getResources().getColor(R.color.hockeyapp_text_white));
    }
    for (;;)
    {
      this.mMessageTextView.setTextColor(getResources().getColor(R.color.hockeyapp_text_black));
      return;
      setBackgroundColor(getResources().getColor(R.color.hockeyapp_background_white));
      this.mAuthorTextView.setTextColor(getResources().getColor(R.color.hockeyapp_text_light));
      this.mDateTextView.setTextColor(getResources().getColor(R.color.hockeyapp_text_light));
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/views/FeedbackMessageView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */