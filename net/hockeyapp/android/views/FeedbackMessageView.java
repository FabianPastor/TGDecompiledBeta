package net.hockeyapp.android.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import net.hockeyapp.android.R;
import net.hockeyapp.android.objects.FeedbackAttachment;
import net.hockeyapp.android.objects.FeedbackMessage;
import net.hockeyapp.android.tasks.AttachmentDownloader;
import net.hockeyapp.android.utils.HockeyLog;

public class FeedbackMessageView extends LinearLayout {
    private AttachmentListView mAttachmentListView = ((AttachmentListView) findViewById(R.id.list_attachments));
    private TextView mAuthorTextView = ((TextView) findViewById(R.id.label_author));
    private final Context mContext;
    private TextView mDateTextView = ((TextView) findViewById(R.id.label_date));
    private TextView mMessageTextView = ((TextView) findViewById(R.id.label_text));

    public FeedbackMessageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        LayoutInflater.from(context).inflate(R.layout.hockeyapp_view_feedback_message, this);
    }

    public void setFeedbackMessage(FeedbackMessage feedbackMessage) {
        try {
            DateFormat dateFormatIn = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
            dateFormatIn.setTimeZone(TimeZone.getTimeZone("UTC"));
            DateFormat dateFormatOut = DateFormat.getDateTimeInstance(3, 3);
            Date date = dateFormatIn.parse(feedbackMessage.getCreatedAt());
            this.mDateTextView.setText(dateFormatOut.format(date));
            this.mDateTextView.setContentDescription(dateFormatOut.format(date));
        } catch (Throwable e) {
            HockeyLog.error("Failed to set feedback message", e);
        }
        this.mAuthorTextView.setText(feedbackMessage.getName());
        this.mAuthorTextView.setContentDescription(feedbackMessage.getName());
        this.mMessageTextView.setText(feedbackMessage.getText());
        this.mMessageTextView.setContentDescription(feedbackMessage.getText());
        this.mAttachmentListView.removeAllViews();
        for (FeedbackAttachment feedbackAttachment : feedbackMessage.getFeedbackAttachments()) {
            AttachmentView attachmentView = new AttachmentView(this.mContext, this.mAttachmentListView, feedbackAttachment, false);
            AttachmentDownloader.getInstance().download(feedbackAttachment, attachmentView);
            this.mAttachmentListView.addView(attachmentView);
        }
    }

    public void setIndex(int index) {
        if (index % 2 == 0) {
            setBackgroundColor(getResources().getColor(R.color.hockeyapp_background_light));
        } else {
            setBackgroundColor(getResources().getColor(R.color.hockeyapp_background_white));
        }
    }
}
