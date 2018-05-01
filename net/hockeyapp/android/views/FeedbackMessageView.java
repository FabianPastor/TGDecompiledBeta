package net.hockeyapp.android.views;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import net.hockeyapp.android.R.color;
import net.hockeyapp.android.R.id;
import net.hockeyapp.android.R.layout;
import net.hockeyapp.android.objects.FeedbackAttachment;
import net.hockeyapp.android.objects.FeedbackMessage;
import net.hockeyapp.android.tasks.AttachmentDownloader;
import net.hockeyapp.android.utils.HockeyLog;

public class FeedbackMessageView
  extends LinearLayout
{
  private AttachmentListView mAttachmentListView;
  private TextView mAuthorTextView;
  private final Context mContext;
  private TextView mDateTextView;
  private TextView mMessageTextView;
  
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
    try
    {
      Object localObject1 = new java/text/SimpleDateFormat;
      ((SimpleDateFormat)localObject1).<init>("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
      ((DateFormat)localObject1).setTimeZone(TimeZone.getTimeZone("UTC"));
      Object localObject2 = DateFormat.getDateTimeInstance(3, 3);
      localObject1 = ((DateFormat)localObject1).parse(paramFeedbackMessage.getCreatedAt());
      this.mDateTextView.setText(((DateFormat)localObject2).format((Date)localObject1));
      this.mDateTextView.setContentDescription(((DateFormat)localObject2).format((Date)localObject1));
      this.mAuthorTextView.setText(paramFeedbackMessage.getName());
      this.mAuthorTextView.setContentDescription(paramFeedbackMessage.getName());
      this.mMessageTextView.setText(paramFeedbackMessage.getText());
      this.mMessageTextView.setContentDescription(paramFeedbackMessage.getText());
      this.mAttachmentListView.removeAllViews();
      localObject2 = paramFeedbackMessage.getFeedbackAttachments().iterator();
      while (((Iterator)localObject2).hasNext())
      {
        localObject1 = (FeedbackAttachment)((Iterator)localObject2).next();
        paramFeedbackMessage = new AttachmentView(this.mContext, this.mAttachmentListView, (FeedbackAttachment)localObject1, false);
        AttachmentDownloader.getInstance().download((FeedbackAttachment)localObject1, paramFeedbackMessage);
        this.mAttachmentListView.addView(paramFeedbackMessage);
      }
    }
    catch (ParseException localParseException)
    {
      for (;;)
      {
        HockeyLog.error("Failed to set feedback message", localParseException);
      }
    }
  }
  
  public void setIndex(int paramInt)
  {
    if (paramInt % 2 == 0) {
      setBackgroundColor(getResources().getColor(R.color.hockeyapp_background_light));
    }
    for (;;)
    {
      return;
      setBackgroundColor(getResources().getColor(R.color.hockeyapp_background_white));
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/views/FeedbackMessageView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */