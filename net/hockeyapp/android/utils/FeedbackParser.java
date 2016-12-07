package net.hockeyapp.android.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.hockeyapp.android.objects.Feedback;
import net.hockeyapp.android.objects.FeedbackAttachment;
import net.hockeyapp.android.objects.FeedbackMessage;
import net.hockeyapp.android.objects.FeedbackResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FeedbackParser {

    private static class FeedbackParserHolder {
        public static final FeedbackParser INSTANCE = new FeedbackParser();

        private FeedbackParserHolder() {
        }
    }

    private FeedbackParser() {
    }

    public static FeedbackParser getInstance() {
        return FeedbackParserHolder.INSTANCE;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public FeedbackResponse parseFeedbackResponse(String feedbackResponseJson) {
        JSONException e;
        FeedbackResponse feedbackResponse = null;
        if (feedbackResponseJson == null) {
            return null;
        }
        try {
            FeedbackResponse feedbackResponse2;
            Feedback feedback;
            JSONObject jSONObject = new JSONObject(feedbackResponseJson);
            JSONObject feedbackObject = jSONObject.getJSONObject("feedback");
            Feedback feedback2 = new Feedback();
            JSONArray messagesArray = feedbackObject.getJSONArray("messages");
            ArrayList<FeedbackMessage> messages = null;
            if (messagesArray.length() > 0) {
                messages = new ArrayList();
                for (int i = 0; i < messagesArray.length(); i++) {
                    String subject = messagesArray.getJSONObject(i).getString("subject").toString();
                    String text = messagesArray.getJSONObject(i).getString("text").toString();
                    String oem = messagesArray.getJSONObject(i).getString("oem").toString();
                    String model = messagesArray.getJSONObject(i).getString("model").toString();
                    String osVersion = messagesArray.getJSONObject(i).getString("os_version").toString();
                    String createdAt = messagesArray.getJSONObject(i).getString("created_at").toString();
                    int id = messagesArray.getJSONObject(i).getInt(TtmlNode.ATTR_ID);
                    String token = messagesArray.getJSONObject(i).getString("token").toString();
                    int via = messagesArray.getJSONObject(i).getInt("via");
                    String userString = messagesArray.getJSONObject(i).getString("user_string").toString();
                    String cleanText = messagesArray.getJSONObject(i).getString("clean_text").toString();
                    String name = messagesArray.getJSONObject(i).getString("name").toString();
                    String appId = messagesArray.getJSONObject(i).getString("app_id").toString();
                    JSONArray jsonAttachments = messagesArray.getJSONObject(i).optJSONArray("attachments");
                    List<FeedbackAttachment> feedbackAttachments = Collections.emptyList();
                    if (jsonAttachments != null) {
                        feedbackAttachments = new ArrayList();
                        for (int j = 0; j < jsonAttachments.length(); j++) {
                            int attachmentId = jsonAttachments.getJSONObject(j).getInt(TtmlNode.ATTR_ID);
                            int attachmentMessageId = jsonAttachments.getJSONObject(j).getInt("feedback_message_id");
                            String filename = jsonAttachments.getJSONObject(j).getString("file_name");
                            String url = jsonAttachments.getJSONObject(j).getString("url");
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
                    feedbackMessage.setSubjec(subject);
                    feedbackMessage.setText(text);
                    feedbackMessage.setToken(token);
                    feedbackMessage.setUserString(userString);
                    feedbackMessage.setVia(via);
                    feedbackMessage.setFeedbackAttachments(feedbackAttachments);
                    messages.add(feedbackMessage);
                }
            }
            feedback2.setMessages(messages);
            try {
                feedback2.setName(feedbackObject.getString("name").toString());
            } catch (JSONException e2) {
                e2.printStackTrace();
            }
            try {
                feedback2.setEmail(feedbackObject.getString("email").toString());
            } catch (JSONException e22) {
                e22.printStackTrace();
            }
            try {
                feedback2.setId(feedbackObject.getInt(TtmlNode.ATTR_ID));
                try {
                    feedback2.setCreatedAt(feedbackObject.getString("created_at").toString());
                } catch (JSONException e222) {
                    e222.printStackTrace();
                }
                feedbackResponse2 = new FeedbackResponse();
            } catch (JSONException e3) {
                e222 = e3;
                feedback = feedback2;
                e222.printStackTrace();
                return feedbackResponse;
            }
            try {
                feedbackResponse2.setFeedback(feedback2);
                try {
                    feedbackResponse2.setStatus(jSONObject.getString("status").toString());
                } catch (JSONException e2222) {
                    e2222.printStackTrace();
                }
                try {
                    feedbackResponse2.setToken(jSONObject.getString("token").toString());
                } catch (JSONException e22222) {
                    e22222.printStackTrace();
                }
                feedback = feedback2;
                return feedbackResponse2;
            } catch (JSONException e4) {
                e22222 = e4;
                feedback = feedback2;
                feedbackResponse = feedbackResponse2;
                e22222.printStackTrace();
                return feedbackResponse;
            }
        } catch (JSONException e5) {
            e22222 = e5;
            e22222.printStackTrace();
            return feedbackResponse;
        }
    }
}
