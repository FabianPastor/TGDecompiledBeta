package android.support.v4.app;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.text.BidiFormatter;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.widget.RemoteViews;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.ui.ActionBar.Theme;

public class NotificationCompat {

    public static class Action {
        public PendingIntent actionIntent;
        public int icon;
        private boolean mAllowGeneratedReplies;
        private final RemoteInput[] mDataOnlyRemoteInputs;
        final Bundle mExtras;
        private final RemoteInput[] mRemoteInputs;
        public CharSequence title;

        public static final class Builder {
            private boolean mAllowGeneratedReplies;
            private final Bundle mExtras;
            private final int mIcon;
            private final PendingIntent mIntent;
            private ArrayList<RemoteInput> mRemoteInputs;
            private final CharSequence mTitle;

            public Builder(int icon, CharSequence title, PendingIntent intent) {
                this(icon, title, intent, new Bundle(), null, true);
            }

            private Builder(int icon, CharSequence title, PendingIntent intent, Bundle extras, RemoteInput[] remoteInputs, boolean allowGeneratedReplies) {
                ArrayList arrayList;
                this.mAllowGeneratedReplies = true;
                this.mIcon = icon;
                this.mTitle = Builder.limitCharSequenceLength(title);
                this.mIntent = intent;
                this.mExtras = extras;
                if (remoteInputs == null) {
                    arrayList = null;
                } else {
                    arrayList = new ArrayList(Arrays.asList(remoteInputs));
                }
                this.mRemoteInputs = arrayList;
                this.mAllowGeneratedReplies = allowGeneratedReplies;
            }

            public Builder addRemoteInput(RemoteInput remoteInput) {
                if (this.mRemoteInputs == null) {
                    this.mRemoteInputs = new ArrayList();
                }
                this.mRemoteInputs.add(remoteInput);
                return this;
            }

            public Builder setAllowGeneratedReplies(boolean allowGeneratedReplies) {
                this.mAllowGeneratedReplies = allowGeneratedReplies;
                return this;
            }

            public Action build() {
                RemoteInput[] dataOnlyInputsArr;
                RemoteInput[] textInputsArr;
                List<RemoteInput> dataOnlyInputs = new ArrayList();
                List<RemoteInput> textInputs = new ArrayList();
                if (this.mRemoteInputs != null) {
                    Iterator it = this.mRemoteInputs.iterator();
                    while (it.hasNext()) {
                        RemoteInput input = (RemoteInput) it.next();
                        if (input.isDataOnly()) {
                            dataOnlyInputs.add(input);
                        } else {
                            textInputs.add(input);
                        }
                    }
                }
                if (dataOnlyInputs.isEmpty()) {
                    dataOnlyInputsArr = null;
                } else {
                    dataOnlyInputsArr = (RemoteInput[]) dataOnlyInputs.toArray(new RemoteInput[dataOnlyInputs.size()]);
                }
                if (textInputs.isEmpty()) {
                    textInputsArr = null;
                } else {
                    textInputsArr = (RemoteInput[]) textInputs.toArray(new RemoteInput[textInputs.size()]);
                }
                return new Action(this.mIcon, this.mTitle, this.mIntent, this.mExtras, textInputsArr, dataOnlyInputsArr, this.mAllowGeneratedReplies);
            }
        }

        public Action(int icon, CharSequence title, PendingIntent intent) {
            this(icon, title, intent, new Bundle(), null, null, true);
        }

        Action(int icon, CharSequence title, PendingIntent intent, Bundle extras, RemoteInput[] remoteInputs, RemoteInput[] dataOnlyRemoteInputs, boolean allowGeneratedReplies) {
            this.icon = icon;
            this.title = Builder.limitCharSequenceLength(title);
            this.actionIntent = intent;
            if (extras == null) {
                extras = new Bundle();
            }
            this.mExtras = extras;
            this.mRemoteInputs = remoteInputs;
            this.mDataOnlyRemoteInputs = dataOnlyRemoteInputs;
            this.mAllowGeneratedReplies = allowGeneratedReplies;
        }

        public int getIcon() {
            return this.icon;
        }

        public CharSequence getTitle() {
            return this.title;
        }

        public PendingIntent getActionIntent() {
            return this.actionIntent;
        }

        public Bundle getExtras() {
            return this.mExtras;
        }

        public boolean getAllowGeneratedReplies() {
            return this.mAllowGeneratedReplies;
        }

        public RemoteInput[] getRemoteInputs() {
            return this.mRemoteInputs;
        }

        public RemoteInput[] getDataOnlyRemoteInputs() {
            return this.mDataOnlyRemoteInputs;
        }
    }

    public static class Builder {
        public ArrayList<Action> mActions;
        int mBadgeIcon;
        RemoteViews mBigContentView;
        String mCategory;
        String mChannelId;
        int mColor;
        boolean mColorized;
        boolean mColorizedSet;
        CharSequence mContentInfo;
        PendingIntent mContentIntent;
        CharSequence mContentText;
        CharSequence mContentTitle;
        RemoteViews mContentView;
        public Context mContext;
        Bundle mExtras;
        PendingIntent mFullScreenIntent;
        int mGroupAlertBehavior;
        String mGroupKey;
        boolean mGroupSummary;
        RemoteViews mHeadsUpContentView;
        Bitmap mLargeIcon;
        boolean mLocalOnly;
        Notification mNotification;
        int mNumber;
        @Deprecated
        public ArrayList<String> mPeople;
        int mPriority;
        int mProgress;
        boolean mProgressIndeterminate;
        int mProgressMax;
        Notification mPublicVersion;
        CharSequence[] mRemoteInputHistory;
        String mShortcutId;
        boolean mShowWhen;
        String mSortKey;
        Style mStyle;
        CharSequence mSubText;
        RemoteViews mTickerView;
        long mTimeout;
        boolean mUseChronometer;
        int mVisibility;

        public Builder(Context context, String channelId) {
            this.mActions = new ArrayList();
            this.mShowWhen = true;
            this.mLocalOnly = false;
            this.mColor = 0;
            this.mVisibility = 0;
            this.mBadgeIcon = 0;
            this.mGroupAlertBehavior = 0;
            this.mNotification = new Notification();
            this.mContext = context;
            this.mChannelId = channelId;
            this.mNotification.when = System.currentTimeMillis();
            this.mNotification.audioStreamType = -1;
            this.mPriority = 0;
            this.mPeople = new ArrayList();
        }

        @Deprecated
        public Builder(Context context) {
            this(context, null);
        }

        public Builder setWhen(long when) {
            this.mNotification.when = when;
            return this;
        }

        public Builder setSmallIcon(int icon) {
            this.mNotification.icon = icon;
            return this;
        }

        public Builder setContentTitle(CharSequence title) {
            this.mContentTitle = limitCharSequenceLength(title);
            return this;
        }

        public Builder setContentText(CharSequence text) {
            this.mContentText = limitCharSequenceLength(text);
            return this;
        }

        public Builder setSubText(CharSequence text) {
            this.mSubText = limitCharSequenceLength(text);
            return this;
        }

        public Builder setNumber(int number) {
            this.mNumber = number;
            return this;
        }

        public Builder setProgress(int max, int progress, boolean indeterminate) {
            this.mProgressMax = max;
            this.mProgress = progress;
            this.mProgressIndeterminate = indeterminate;
            return this;
        }

        public Builder setContentIntent(PendingIntent intent) {
            this.mContentIntent = intent;
            return this;
        }

        public Builder setDeleteIntent(PendingIntent intent) {
            this.mNotification.deleteIntent = intent;
            return this;
        }

        public Builder setTicker(CharSequence tickerText) {
            this.mNotification.tickerText = limitCharSequenceLength(tickerText);
            return this;
        }

        public Builder setLargeIcon(Bitmap icon) {
            this.mLargeIcon = icon;
            return this;
        }

        public Builder setSound(Uri sound) {
            this.mNotification.sound = sound;
            this.mNotification.audioStreamType = -1;
            return this;
        }

        public Builder setSound(Uri sound, int streamType) {
            this.mNotification.sound = sound;
            this.mNotification.audioStreamType = streamType;
            return this;
        }

        public Builder setVibrate(long[] pattern) {
            this.mNotification.vibrate = pattern;
            return this;
        }

        public Builder setLights(int argb, int onMs, int offMs) {
            boolean showLights;
            int i = 1;
            this.mNotification.ledARGB = argb;
            this.mNotification.ledOnMS = onMs;
            this.mNotification.ledOffMS = offMs;
            if (this.mNotification.ledOnMS == 0 || this.mNotification.ledOffMS == 0) {
                showLights = false;
            } else {
                showLights = true;
            }
            Notification notification = this.mNotification;
            int i2 = this.mNotification.flags & -2;
            if (!showLights) {
                i = 0;
            }
            notification.flags = i | i2;
            return this;
        }

        public Builder setAutoCancel(boolean autoCancel) {
            setFlag(16, autoCancel);
            return this;
        }

        public Builder setLocalOnly(boolean b) {
            this.mLocalOnly = b;
            return this;
        }

        public Builder setCategory(String category) {
            this.mCategory = category;
            return this;
        }

        public Builder setDefaults(int defaults) {
            this.mNotification.defaults = defaults;
            if ((defaults & 4) != 0) {
                Notification notification = this.mNotification;
                notification.flags |= 1;
            }
            return this;
        }

        private void setFlag(int mask, boolean value) {
            if (value) {
                Notification notification = this.mNotification;
                notification.flags |= mask;
                return;
            }
            notification = this.mNotification;
            notification.flags &= mask ^ -1;
        }

        public Builder setPriority(int pri) {
            this.mPriority = pri;
            return this;
        }

        public Builder addPerson(String uri) {
            this.mPeople.add(uri);
            return this;
        }

        public Builder setGroup(String groupKey) {
            this.mGroupKey = groupKey;
            return this;
        }

        public Builder setGroupSummary(boolean isGroupSummary) {
            this.mGroupSummary = isGroupSummary;
            return this;
        }

        public Builder setSortKey(String sortKey) {
            this.mSortKey = sortKey;
            return this;
        }

        public Bundle getExtras() {
            if (this.mExtras == null) {
                this.mExtras = new Bundle();
            }
            return this.mExtras;
        }

        public Builder addAction(int icon, CharSequence title, PendingIntent intent) {
            this.mActions.add(new Action(icon, title, intent));
            return this;
        }

        public Builder setStyle(Style style) {
            if (this.mStyle != style) {
                this.mStyle = style;
                if (this.mStyle != null) {
                    this.mStyle.setBuilder(this);
                }
            }
            return this;
        }

        public Builder setColor(int argb) {
            this.mColor = argb;
            return this;
        }

        public Builder setChannelId(String channelId) {
            this.mChannelId = channelId;
            return this;
        }

        public Builder setShortcutId(String shortcutId) {
            this.mShortcutId = shortcutId;
            return this;
        }

        public Builder setGroupAlertBehavior(int groupAlertBehavior) {
            this.mGroupAlertBehavior = groupAlertBehavior;
            return this;
        }

        public Builder extend(Extender extender) {
            extender.extend(this);
            return this;
        }

        public Notification build() {
            return new NotificationCompatBuilder(this).build();
        }

        protected static CharSequence limitCharSequenceLength(CharSequence cs) {
            if (cs != null && cs.length() > 5120) {
                return cs.subSequence(0, 5120);
            }
            return cs;
        }

        public int getColor() {
            return this.mColor;
        }
    }

    public interface Extender {
        Builder extend(Builder builder);
    }

    public static abstract class Style {
        CharSequence mBigContentTitle;
        protected Builder mBuilder;
        CharSequence mSummaryText;
        boolean mSummaryTextSet = false;

        public void setBuilder(Builder builder) {
            if (this.mBuilder != builder) {
                this.mBuilder = builder;
                if (this.mBuilder != null) {
                    this.mBuilder.setStyle(this);
                }
            }
        }

        public void apply(NotificationBuilderWithBuilderAccessor builder) {
        }

        public RemoteViews makeContentView(NotificationBuilderWithBuilderAccessor builder) {
            return null;
        }

        public RemoteViews makeBigContentView(NotificationBuilderWithBuilderAccessor builder) {
            return null;
        }

        public RemoteViews makeHeadsUpContentView(NotificationBuilderWithBuilderAccessor builder) {
            return null;
        }

        public void addCompatExtras(Bundle extras) {
        }
    }

    public static class BigTextStyle extends Style {
        private CharSequence mBigText;

        public BigTextStyle bigText(CharSequence cs) {
            this.mBigText = Builder.limitCharSequenceLength(cs);
            return this;
        }

        public void apply(NotificationBuilderWithBuilderAccessor builder) {
            if (VERSION.SDK_INT >= 16) {
                android.app.Notification.BigTextStyle style = new android.app.Notification.BigTextStyle(builder.getBuilder()).setBigContentTitle(this.mBigContentTitle).bigText(this.mBigText);
                if (this.mSummaryTextSet) {
                    style.setSummaryText(this.mSummaryText);
                }
            }
        }
    }

    public static final class CarExtender implements Extender {
        private int mColor = 0;
        private Bitmap mLargeIcon;
        private UnreadConversation mUnreadConversation;

        public static class UnreadConversation {
            private final long mLatestTimestamp;
            private final String[] mMessages;
            private final String[] mParticipants;
            private final PendingIntent mReadPendingIntent;
            private final RemoteInput mRemoteInput;
            private final PendingIntent mReplyPendingIntent;

            public static class Builder {
                private long mLatestTimestamp;
                private final List<String> mMessages = new ArrayList();
                private final String mParticipant;
                private PendingIntent mReadPendingIntent;
                private RemoteInput mRemoteInput;
                private PendingIntent mReplyPendingIntent;

                public Builder(String name) {
                    this.mParticipant = name;
                }

                public Builder addMessage(String message) {
                    this.mMessages.add(message);
                    return this;
                }

                public Builder setReplyAction(PendingIntent pendingIntent, RemoteInput remoteInput) {
                    this.mRemoteInput = remoteInput;
                    this.mReplyPendingIntent = pendingIntent;
                    return this;
                }

                public Builder setReadPendingIntent(PendingIntent pendingIntent) {
                    this.mReadPendingIntent = pendingIntent;
                    return this;
                }

                public Builder setLatestTimestamp(long timestamp) {
                    this.mLatestTimestamp = timestamp;
                    return this;
                }

                public UnreadConversation build() {
                    return new UnreadConversation((String[]) this.mMessages.toArray(new String[this.mMessages.size()]), this.mRemoteInput, this.mReplyPendingIntent, this.mReadPendingIntent, new String[]{this.mParticipant}, this.mLatestTimestamp);
                }
            }

            UnreadConversation(String[] messages, RemoteInput remoteInput, PendingIntent replyPendingIntent, PendingIntent readPendingIntent, String[] participants, long latestTimestamp) {
                this.mMessages = messages;
                this.mRemoteInput = remoteInput;
                this.mReadPendingIntent = readPendingIntent;
                this.mReplyPendingIntent = replyPendingIntent;
                this.mParticipants = participants;
                this.mLatestTimestamp = latestTimestamp;
            }

            public String[] getMessages() {
                return this.mMessages;
            }

            public RemoteInput getRemoteInput() {
                return this.mRemoteInput;
            }

            public PendingIntent getReplyPendingIntent() {
                return this.mReplyPendingIntent;
            }

            public PendingIntent getReadPendingIntent() {
                return this.mReadPendingIntent;
            }

            public String[] getParticipants() {
                return this.mParticipants;
            }

            public long getLatestTimestamp() {
                return this.mLatestTimestamp;
            }
        }

        private static Bundle getBundleForUnreadConversation(UnreadConversation uc) {
            Bundle b = new Bundle();
            String author = null;
            if (uc.getParticipants() != null && uc.getParticipants().length > 1) {
                author = uc.getParticipants()[0];
            }
            Parcelable[] messages = new Parcelable[uc.getMessages().length];
            for (int i = 0; i < messages.length; i++) {
                Bundle m = new Bundle();
                m.putString(MimeTypes.BASE_TYPE_TEXT, uc.getMessages()[i]);
                m.putString("author", author);
                messages[i] = m;
            }
            b.putParcelableArray("messages", messages);
            RemoteInput remoteInputCompat = uc.getRemoteInput();
            if (remoteInputCompat != null) {
                b.putParcelable("remote_input", new android.app.RemoteInput.Builder(remoteInputCompat.getResultKey()).setLabel(remoteInputCompat.getLabel()).setChoices(remoteInputCompat.getChoices()).setAllowFreeFormInput(remoteInputCompat.getAllowFreeFormInput()).addExtras(remoteInputCompat.getExtras()).build());
            }
            b.putParcelable("on_reply", uc.getReplyPendingIntent());
            b.putParcelable("on_read", uc.getReadPendingIntent());
            b.putStringArray("participants", uc.getParticipants());
            b.putLong("timestamp", uc.getLatestTimestamp());
            return b;
        }

        public Builder extend(Builder builder) {
            if (VERSION.SDK_INT >= 21) {
                Bundle carExtensions = new Bundle();
                if (this.mLargeIcon != null) {
                    carExtensions.putParcelable("large_icon", this.mLargeIcon);
                }
                if (this.mColor != 0) {
                    carExtensions.putInt("app_color", this.mColor);
                }
                if (this.mUnreadConversation != null) {
                    carExtensions.putBundle("car_conversation", getBundleForUnreadConversation(this.mUnreadConversation));
                }
                builder.getExtras().putBundle("android.car.EXTENSIONS", carExtensions);
            }
            return builder;
        }

        public CarExtender setUnreadConversation(UnreadConversation unreadConversation) {
            this.mUnreadConversation = unreadConversation;
            return this;
        }
    }

    public static class InboxStyle extends Style {
        private ArrayList<CharSequence> mTexts = new ArrayList();

        public InboxStyle setBigContentTitle(CharSequence title) {
            this.mBigContentTitle = Builder.limitCharSequenceLength(title);
            return this;
        }

        public InboxStyle setSummaryText(CharSequence cs) {
            this.mSummaryText = Builder.limitCharSequenceLength(cs);
            this.mSummaryTextSet = true;
            return this;
        }

        public InboxStyle addLine(CharSequence cs) {
            this.mTexts.add(Builder.limitCharSequenceLength(cs));
            return this;
        }

        public void apply(NotificationBuilderWithBuilderAccessor builder) {
            if (VERSION.SDK_INT >= 16) {
                android.app.Notification.InboxStyle style = new android.app.Notification.InboxStyle(builder.getBuilder()).setBigContentTitle(this.mBigContentTitle);
                if (this.mSummaryTextSet) {
                    style.setSummaryText(this.mSummaryText);
                }
                Iterator it = this.mTexts.iterator();
                while (it.hasNext()) {
                    style.addLine((CharSequence) it.next());
                }
            }
        }
    }

    public static class MessagingStyle extends Style {
        CharSequence mConversationTitle;
        List<Message> mMessages = new ArrayList();
        CharSequence mUserDisplayName;

        public static final class Message {
            private String mDataMimeType;
            private Uri mDataUri;
            private Bundle mExtras = new Bundle();
            private final CharSequence mSender;
            private final CharSequence mText;
            private final long mTimestamp;

            public Message(CharSequence text, long timestamp, CharSequence sender) {
                this.mText = text;
                this.mTimestamp = timestamp;
                this.mSender = sender;
            }

            public CharSequence getText() {
                return this.mText;
            }

            public long getTimestamp() {
                return this.mTimestamp;
            }

            public CharSequence getSender() {
                return this.mSender;
            }

            public String getDataMimeType() {
                return this.mDataMimeType;
            }

            public Uri getDataUri() {
                return this.mDataUri;
            }

            private Bundle toBundle() {
                Bundle bundle = new Bundle();
                if (this.mText != null) {
                    bundle.putCharSequence(MimeTypes.BASE_TYPE_TEXT, this.mText);
                }
                bundle.putLong("time", this.mTimestamp);
                if (this.mSender != null) {
                    bundle.putCharSequence("sender", this.mSender);
                }
                if (this.mDataMimeType != null) {
                    bundle.putString("type", this.mDataMimeType);
                }
                if (this.mDataUri != null) {
                    bundle.putParcelable("uri", this.mDataUri);
                }
                if (this.mExtras != null) {
                    bundle.putBundle("extras", this.mExtras);
                }
                return bundle;
            }

            static Bundle[] getBundleArrayForMessages(List<Message> messages) {
                Bundle[] bundles = new Bundle[messages.size()];
                int N = messages.size();
                for (int i = 0; i < N; i++) {
                    bundles[i] = ((Message) messages.get(i)).toBundle();
                }
                return bundles;
            }
        }

        MessagingStyle() {
        }

        public MessagingStyle(CharSequence userDisplayName) {
            this.mUserDisplayName = userDisplayName;
        }

        public MessagingStyle setConversationTitle(CharSequence conversationTitle) {
            this.mConversationTitle = conversationTitle;
            return this;
        }

        public MessagingStyle addMessage(CharSequence text, long timestamp, CharSequence sender) {
            this.mMessages.add(new Message(text, timestamp, sender));
            if (this.mMessages.size() > 25) {
                this.mMessages.remove(0);
            }
            return this;
        }

        public void apply(NotificationBuilderWithBuilderAccessor builder) {
            if (VERSION.SDK_INT >= 24) {
                android.app.Notification.MessagingStyle style = new android.app.Notification.MessagingStyle(this.mUserDisplayName).setConversationTitle(this.mConversationTitle);
                for (Message message : this.mMessages) {
                    Message message2;
                    android.app.Notification.MessagingStyle.Message frameworkMessage = new android.app.Notification.MessagingStyle.Message(message2.getText(), message2.getTimestamp(), message2.getSender());
                    if (message2.getDataMimeType() != null) {
                        frameworkMessage.setData(message2.getDataMimeType(), message2.getDataUri());
                    }
                    style.addMessage(frameworkMessage);
                }
                style.setBuilder(builder.getBuilder());
                return;
            }
            Message latestIncomingMessage = findLatestIncomingMessage();
            if (this.mConversationTitle != null) {
                builder.getBuilder().setContentTitle(this.mConversationTitle);
            } else if (latestIncomingMessage != null) {
                builder.getBuilder().setContentTitle(latestIncomingMessage.getSender());
            }
            if (latestIncomingMessage != null) {
                CharSequence makeMessageLine;
                android.app.Notification.Builder builder2 = builder.getBuilder();
                if (this.mConversationTitle != null) {
                    makeMessageLine = makeMessageLine(latestIncomingMessage);
                } else {
                    makeMessageLine = latestIncomingMessage.getText();
                }
                builder2.setContentText(makeMessageLine);
            }
            if (VERSION.SDK_INT >= 16) {
                boolean showNames;
                SpannableStringBuilder completeMessage = new SpannableStringBuilder();
                if (this.mConversationTitle != null || hasMessagesWithoutSender()) {
                    showNames = true;
                } else {
                    showNames = false;
                }
                for (int i = this.mMessages.size() - 1; i >= 0; i--) {
                    message2 = (Message) this.mMessages.get(i);
                    CharSequence line = showNames ? makeMessageLine(message2) : message2.getText();
                    if (i != this.mMessages.size() - 1) {
                        completeMessage.insert(0, "\n");
                    }
                    completeMessage.insert(0, line);
                }
                new android.app.Notification.BigTextStyle(builder.getBuilder()).setBigContentTitle(null).bigText(completeMessage);
            }
        }

        private Message findLatestIncomingMessage() {
            for (int i = this.mMessages.size() - 1; i >= 0; i--) {
                Message message = (Message) this.mMessages.get(i);
                if (!TextUtils.isEmpty(message.getSender())) {
                    return message;
                }
            }
            if (this.mMessages.isEmpty()) {
                return null;
            }
            return (Message) this.mMessages.get(this.mMessages.size() - 1);
        }

        private boolean hasMessagesWithoutSender() {
            for (int i = this.mMessages.size() - 1; i >= 0; i--) {
                if (((Message) this.mMessages.get(i)).getSender() == null) {
                    return true;
                }
            }
            return false;
        }

        private CharSequence makeMessageLine(Message message) {
            BidiFormatter bidi = BidiFormatter.getInstance();
            SpannableStringBuilder sb = new SpannableStringBuilder();
            boolean afterLollipop = VERSION.SDK_INT >= 21;
            int color = afterLollipop ? Theme.ACTION_BAR_VIDEO_EDIT_COLOR : -1;
            CharSequence replyName = message.getSender();
            if (TextUtils.isEmpty(message.getSender())) {
                replyName = this.mUserDisplayName == null ? TtmlNode.ANONYMOUS_REGION_ID : this.mUserDisplayName;
                if (afterLollipop && this.mBuilder.getColor() != 0) {
                    color = this.mBuilder.getColor();
                }
            }
            CharSequence senderText = bidi.unicodeWrap(replyName);
            sb.append(senderText);
            sb.setSpan(makeFontColorSpan(color), sb.length() - senderText.length(), sb.length(), 33);
            sb.append("  ").append(bidi.unicodeWrap(message.getText() == null ? TtmlNode.ANONYMOUS_REGION_ID : message.getText()));
            return sb;
        }

        private TextAppearanceSpan makeFontColorSpan(int color) {
            return new TextAppearanceSpan(null, 0, 0, ColorStateList.valueOf(color), null);
        }

        public void addCompatExtras(Bundle extras) {
            super.addCompatExtras(extras);
            if (this.mUserDisplayName != null) {
                extras.putCharSequence("android.selfDisplayName", this.mUserDisplayName);
            }
            if (this.mConversationTitle != null) {
                extras.putCharSequence("android.conversationTitle", this.mConversationTitle);
            }
            if (!this.mMessages.isEmpty()) {
                extras.putParcelableArray("android.messages", Message.getBundleArrayForMessages(this.mMessages));
            }
        }
    }

    public static final class WearableExtender implements Extender {
        private ArrayList<Action> mActions = new ArrayList();
        private Bitmap mBackground;
        private String mBridgeTag;
        private int mContentActionIndex = -1;
        private int mContentIcon;
        private int mContentIconGravity = 8388613;
        private int mCustomContentHeight;
        private int mCustomSizePreset = 0;
        private String mDismissalId;
        private PendingIntent mDisplayIntent;
        private int mFlags = 1;
        private int mGravity = 80;
        private int mHintScreenTimeout;
        private ArrayList<Notification> mPages = new ArrayList();

        public Builder extend(Builder builder) {
            Bundle wearableBundle = new Bundle();
            if (!this.mActions.isEmpty()) {
                if (VERSION.SDK_INT >= 16) {
                    ArrayList<Parcelable> parcelables = new ArrayList(this.mActions.size());
                    Iterator it = this.mActions.iterator();
                    while (it.hasNext()) {
                        Action action = (Action) it.next();
                        if (VERSION.SDK_INT >= 20) {
                            parcelables.add(getActionFromActionCompat(action));
                        } else if (VERSION.SDK_INT >= 16) {
                            parcelables.add(NotificationCompatJellybean.getBundleForAction(action));
                        }
                    }
                    wearableBundle.putParcelableArrayList("actions", parcelables);
                } else {
                    wearableBundle.putParcelableArrayList("actions", null);
                }
            }
            if (this.mFlags != 1) {
                wearableBundle.putInt("flags", this.mFlags);
            }
            if (this.mDisplayIntent != null) {
                wearableBundle.putParcelable("displayIntent", this.mDisplayIntent);
            }
            if (!this.mPages.isEmpty()) {
                wearableBundle.putParcelableArray("pages", (Parcelable[]) this.mPages.toArray(new Notification[this.mPages.size()]));
            }
            if (this.mBackground != null) {
                wearableBundle.putParcelable("background", this.mBackground);
            }
            if (this.mContentIcon != 0) {
                wearableBundle.putInt("contentIcon", this.mContentIcon);
            }
            if (this.mContentIconGravity != 8388613) {
                wearableBundle.putInt("contentIconGravity", this.mContentIconGravity);
            }
            if (this.mContentActionIndex != -1) {
                wearableBundle.putInt("contentActionIndex", this.mContentActionIndex);
            }
            if (this.mCustomSizePreset != 0) {
                wearableBundle.putInt("customSizePreset", this.mCustomSizePreset);
            }
            if (this.mCustomContentHeight != 0) {
                wearableBundle.putInt("customContentHeight", this.mCustomContentHeight);
            }
            if (this.mGravity != 80) {
                wearableBundle.putInt("gravity", this.mGravity);
            }
            if (this.mHintScreenTimeout != 0) {
                wearableBundle.putInt("hintScreenTimeout", this.mHintScreenTimeout);
            }
            if (this.mDismissalId != null) {
                wearableBundle.putString("dismissalId", this.mDismissalId);
            }
            if (this.mBridgeTag != null) {
                wearableBundle.putString("bridgeTag", this.mBridgeTag);
            }
            builder.getExtras().putBundle("android.wearable.EXTENSIONS", wearableBundle);
            return builder;
        }

        private static android.app.Notification.Action getActionFromActionCompat(Action actionCompat) {
            Bundle actionExtras;
            android.app.Notification.Action.Builder actionBuilder = new android.app.Notification.Action.Builder(actionCompat.getIcon(), actionCompat.getTitle(), actionCompat.getActionIntent());
            if (actionCompat.getExtras() != null) {
                actionExtras = new Bundle(actionCompat.getExtras());
            } else {
                actionExtras = new Bundle();
            }
            actionExtras.putBoolean("android.support.allowGeneratedReplies", actionCompat.getAllowGeneratedReplies());
            if (VERSION.SDK_INT >= 24) {
                actionBuilder.setAllowGeneratedReplies(actionCompat.getAllowGeneratedReplies());
            }
            actionBuilder.addExtras(actionExtras);
            RemoteInput[] remoteInputCompats = actionCompat.getRemoteInputs();
            if (remoteInputCompats != null) {
                for (RemoteInput remoteInput : RemoteInput.fromCompat(remoteInputCompats)) {
                    actionBuilder.addRemoteInput(remoteInput);
                }
            }
            return actionBuilder.build();
        }

        public WearableExtender clone() {
            WearableExtender that = new WearableExtender();
            that.mActions = new ArrayList(this.mActions);
            that.mFlags = this.mFlags;
            that.mDisplayIntent = this.mDisplayIntent;
            that.mPages = new ArrayList(this.mPages);
            that.mBackground = this.mBackground;
            that.mContentIcon = this.mContentIcon;
            that.mContentIconGravity = this.mContentIconGravity;
            that.mContentActionIndex = this.mContentActionIndex;
            that.mCustomSizePreset = this.mCustomSizePreset;
            that.mCustomContentHeight = this.mCustomContentHeight;
            that.mGravity = this.mGravity;
            that.mHintScreenTimeout = this.mHintScreenTimeout;
            that.mDismissalId = this.mDismissalId;
            that.mBridgeTag = this.mBridgeTag;
            return that;
        }

        public WearableExtender addAction(Action action) {
            this.mActions.add(action);
            return this;
        }

        public WearableExtender setDismissalId(String dismissalId) {
            this.mDismissalId = dismissalId;
            return this;
        }
    }

    public static Bundle getExtras(Notification notification) {
        if (VERSION.SDK_INT >= 19) {
            return notification.extras;
        }
        if (VERSION.SDK_INT >= 16) {
            return NotificationCompatJellybean.getExtras(notification);
        }
        return null;
    }
}
