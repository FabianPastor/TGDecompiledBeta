package net.hockeyapp.android.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.hockeyapp.android.UpdateFragment;
import net.hockeyapp.android.objects.Feedback;
import net.hockeyapp.android.objects.FeedbackAttachment;
import net.hockeyapp.android.objects.FeedbackMessage;
import net.hockeyapp.android.objects.FeedbackResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.messenger.exoplayer2.util.MimeTypes;

public class FeedbackParser {

    private static class FeedbackParserHolder {
        static final FeedbackParser INSTANCE = new FeedbackParser();
    }

    private FeedbackParser() {
    }

    public static FeedbackParser getInstance() {
        return FeedbackParserHolder.INSTANCE;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public FeedbackResponse parseFeedbackResponse(String feedbackResponseJson) {
        FeedbackResponse feedbackResponse = null;
        if (feedbackResponseJson == null) {
            return null;
        }
        JSONObject jSONObject = new JSONObject(feedbackResponseJson);
        JSONObject feedbackObject = jSONObject.getJSONObject("feedback");
        Feedback feedback = new Feedback();
        JSONArray messagesArray = feedbackObject.getJSONArray("messages");
        ArrayList<FeedbackMessage> messages = null;
        if (messagesArray.length() > 0) {
            messages = new ArrayList();
            for (int i = 0; i < messagesArray.length(); i++) {
                String subject = messagesArray.getJSONObject(i).getString("subject");
                String text = messagesArray.getJSONObject(i).getString(MimeTypes.BASE_TYPE_TEXT);
                String oem = messagesArray.getJSONObject(i).getString("oem");
                String model = messagesArray.getJSONObject(i).getString("model");
                String osVersion = messagesArray.getJSONObject(i).getString("os_version");
                String createdAt = messagesArray.getJSONObject(i).getString("created_at");
                int id = messagesArray.getJSONObject(i).getInt(TtmlNode.ATTR_ID);
                String token = messagesArray.getJSONObject(i).getString("token");
                int via = messagesArray.getJSONObject(i).getInt("via");
                String userString = messagesArray.getJSONObject(i).getString("user_string");
                String cleanText = messagesArray.getJSONObject(i).getString("clean_text");
                String name = messagesArray.getJSONObject(i).getString("name");
                String appId = messagesArray.getJSONObject(i).getString("app_id");
                JSONArray jsonAttachments = messagesArray.getJSONObject(i).optJSONArray("attachments");
                List<FeedbackAttachment> feedbackAttachments = Collections.emptyList();
                if (jsonAttachments != null) {
                    feedbackAttachments = new ArrayList();
                    for (int j = 0; j < jsonAttachments.length(); j++) {
                        int attachmentId = jsonAttachments.getJSONObject(j).getInt(TtmlNode.ATTR_ID);
                        int attachmentMessageId = jsonAttachments.getJSONObject(j).getInt("feedback_message_id");
                        String filename = jsonAttachments.getJSONObject(j).getString("file_name");
                        String url = jsonAttachments.getJSONObject(j).getString(UpdateFragment.FRAGMENT_URL);
                        String attachmentCreatedAt = jsonAttachments.getJSONObject(j).getString("created_at");
                        String attachmentUpdatedAt = jsonAttachments.getJSONObject(j).getString("updated_at");
                        FeedbackAttachment feedbackAttachment = new FeedbackAttachment();
                        feedbackAttachment.setId(attachmentId);
                        feedbackAttachment.setMessageId(attachmentMessageId);
                        feedbackAttachment.setFilename(filename);
                        feedbackAttachment.setUrl(url);
                        feedbackAttachment.setCreatedAt(attachmentCreatedAt);
                        feedbackAttachment.setUpdatedAt(attachmentUpdatedAt);
                        feedbackAttachments.add(feedbackAttachment);
                    }
                }
                FeedbackMessage feedbackMessage = new FeedbackMessage();
                feedbackMessage.setAppId(appId);
                feedbackMessage.setCleanText(cleanText);
                feedbackMessage.setCreatedAt(createdAt);
                feedbackMessage.setId(id);
                feedbackMessage.setModel(model);
                feedbackMessage.setName(name);
                feedbackMessage.setOem(oem);
                feedbackMessage.setOsVersion(osVersion);
                feedbackMessage.setSubject(subject);
                feedbackMessage.setText(text);
                feedbackMessage.setToken(token);
                feedbackMessage.setUserString(userString);
                feedbackMessage.setVia(via);
                feedbackMessage.setFeedbackAttachments(feedbackAttachments);
                messages.add(feedbackMessage);
            }
        }
        feedback.setMessages(messages);
        try {
            feedback.setName(feedbackObject.getString("name"));
        } catch (Throwable e) {
            Throwable e2;
            HockeyLog.error("Failed to parse \"name\" in feedback response", e2);
        }
        try {
            feedback.setEmail(feedbackObject.getString("email"));
        } catch (Throwable e22) {
            HockeyLog.error("Failed to parse \"email\" in feedback response", e22);
        }
        try {
            feedback.setId(feedbackObject.getInt(TtmlNode.ATTR_ID));
        } catch (Throwable e222) {
            HockeyLog.error("Failed to parse \"id\" in feedback response", e222);
        }
        try {
            feedback.setCreatedAt(feedbackObject.getString("created_at"));
        } catch (Throwable e2222) {
            HockeyLog.error("Failed to parse \"created_at\" in feedback response", e2222);
        }
        FeedbackResponse feedbackResponse2 = new FeedbackResponse();
        try {
            feedbackResponse2.setFeedback(feedback);
            try {
                feedbackResponse2.setStatus(jSONObject.getString("status"));
            } catch (Throwable e22222) {
                HockeyLog.error("Failed to parse \"status\" in feedback response", e22222);
            }
            try {
                feedbackResponse2.setToken(jSONObject.getString("token"));
            } catch (Throwable e222222) {
                HockeyLog.error("Failed to parse \"token\" in feedback response", e222222);
            }
            return feedbackResponse2;
        } catch (JSONException e3) {
            e222222 = e3;
            feedbackResponse = feedbackResponse2;
            HockeyLog.error("Failed to parse feedback response", e222222);
            return feedbackResponse;
        }
    }
}
