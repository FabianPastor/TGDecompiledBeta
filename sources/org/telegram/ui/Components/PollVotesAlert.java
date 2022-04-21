package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AnimationProperties;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.ProfileActivity;

public class PollVotesAlert extends BottomSheet {
    public static final Property<UserCell, Float> USER_CELL_PROPERTY = new AnimationProperties.FloatProperty<UserCell>("placeholderAlpha") {
        public void setValue(UserCell object, float value) {
            object.setPlaceholderAlpha(value);
        }

        public Float get(UserCell object) {
            return Float.valueOf(object.getPlaceholderAlpha());
        }
    };
    /* access modifiers changed from: private */
    public ActionBar actionBar;
    /* access modifiers changed from: private */
    public AnimatorSet actionBarAnimation;
    /* access modifiers changed from: private */
    public View actionBarShadow;
    /* access modifiers changed from: private */
    public ChatActivity chatActivity;
    /* access modifiers changed from: private */
    public float gradientWidth;
    /* access modifiers changed from: private */
    public Adapter listAdapter;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    private HashSet<VotesList> loadingMore = new HashSet<>();
    /* access modifiers changed from: private */
    public boolean loadingResults = true;
    private MessageObject messageObject;
    private TLRPC.InputPeer peer;
    /* access modifiers changed from: private */
    public LinearGradient placeholderGradient;
    /* access modifiers changed from: private */
    public Matrix placeholderMatrix;
    /* access modifiers changed from: private */
    public Paint placeholderPaint = new Paint(1);
    /* access modifiers changed from: private */
    public TLRPC.Poll poll;
    /* access modifiers changed from: private */
    public ArrayList<Integer> queries = new ArrayList<>();
    /* access modifiers changed from: private */
    public RectF rect = new RectF();
    /* access modifiers changed from: private */
    public int scrollOffsetY;
    /* access modifiers changed from: private */
    public Drawable shadowDrawable;
    /* access modifiers changed from: private */
    public TextView titleTextView;
    private int topBeforeSwitch;
    /* access modifiers changed from: private */
    public float totalTranslation;
    /* access modifiers changed from: private */
    public ArrayList<VotesList> voters = new ArrayList<>();
    /* access modifiers changed from: private */
    public HashMap<VotesList, Button> votesPercents = new HashMap<>();

    static /* synthetic */ float access$3016(PollVotesAlert x0, float x1) {
        float f = x0.totalTranslation + x1;
        x0.totalTranslation = f;
        return f;
    }

    static /* synthetic */ float access$3024(PollVotesAlert x0, float x1) {
        float f = x0.totalTranslation - x1;
        x0.totalTranslation = f;
        return f;
    }

    private static class VotesList {
        public boolean collapsed;
        public int collapsedCount = 10;
        public int count;
        public String next_offset;
        public byte[] option;
        public ArrayList<TLRPC.User> users;
        public ArrayList<TLRPC.MessageUserVote> votes;

        public VotesList(TLRPC.TL_messages_votesList votesList, byte[] o) {
            this.count = votesList.count;
            this.votes = votesList.votes;
            this.users = votesList.users;
            this.next_offset = votesList.next_offset;
            this.option = o;
        }

        public int getCount() {
            if (this.collapsed) {
                return Math.min(this.collapsedCount, this.votes.size());
            }
            return this.votes.size();
        }

        public int getCollapsed() {
            if (this.votes.size() <= 15) {
                return 0;
            }
            if (this.collapsed) {
                return 1;
            }
            return 2;
        }
    }

    public class SectionCell extends FrameLayout {
        private TextView middleTextView;
        private TextView righTextView;
        private TextView textView;
        final /* synthetic */ PollVotesAlert this$0;

        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        public SectionCell(PollVotesAlert this$02, Context context) {
            super(context);
            final PollVotesAlert pollVotesAlert = this$02;
            this.this$0 = pollVotesAlert;
            setBackgroundColor(Theme.getColor("graySection"));
            TextView textView2 = new TextView(getContext());
            this.textView = textView2;
            textView2.setTextSize(1, 14.0f);
            this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.textView.setTextColor(Theme.getColor("key_graySectionText"));
            this.textView.setSingleLine(true);
            this.textView.setEllipsize(TextUtils.TruncateAt.END);
            int i = 5;
            int i2 = 16;
            this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            TextView textView3 = new TextView(getContext());
            this.middleTextView = textView3;
            textView3.setTextSize(1, 14.0f);
            this.middleTextView.setTextColor(Theme.getColor("key_graySectionText"));
            this.middleTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            AnonymousClass1 r3 = new TextView(getContext()) {
                public boolean post(Runnable action) {
                    return SectionCell.this.this$0.containerView.post(action);
                }

                public boolean postDelayed(Runnable action, long delayMillis) {
                    return SectionCell.this.this$0.containerView.postDelayed(action, delayMillis);
                }
            };
            this.righTextView = r3;
            r3.setTextSize(1, 14.0f);
            this.righTextView.setTextColor(Theme.getColor("key_graySectionText"));
            this.righTextView.setGravity((LocaleController.isRTL ? 3 : 5) | 16);
            this.righTextView.setOnClickListener(new PollVotesAlert$SectionCell$$ExternalSyntheticLambda0(this));
            addView(this.textView, LayoutHelper.createFrame(-2, -1.0f, (LocaleController.isRTL ? 5 : 3) | 48, (float) (LocaleController.isRTL ? 0 : 16), 0.0f, (float) (!LocaleController.isRTL ? 0 : i2), 0.0f));
            addView(this.middleTextView, LayoutHelper.createFrame(-2, -1.0f, (LocaleController.isRTL ? 5 : 3) | 48, 0.0f, 0.0f, 0.0f, 0.0f));
            addView(this.righTextView, LayoutHelper.createFrame(-2, -1.0f, (LocaleController.isRTL ? 3 : i) | 48, 16.0f, 0.0f, 16.0f, 0.0f));
        }

        /* renamed from: lambda$new$0$org-telegram-ui-Components-PollVotesAlert$SectionCell  reason: not valid java name */
        public /* synthetic */ void m4245xe45e52c1(View v) {
            onCollapseClick();
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int i = widthMeasureSpec;
            int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(32.0f), NUM);
            measureChildWithMargins(this.middleTextView, i, 0, makeMeasureSpec, 0);
            measureChildWithMargins(this.righTextView, i, 0, makeMeasureSpec, 0);
            measureChildWithMargins(this.textView, i, this.middleTextView.getMeasuredWidth() + this.righTextView.getMeasuredWidth() + AndroidUtilities.dp(32.0f), makeMeasureSpec, 0);
            setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), AndroidUtilities.dp(32.0f));
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean changed, int left, int top, int right, int bottom) {
            super.onLayout(changed, left, top, right, bottom);
            if (LocaleController.isRTL) {
                int l = this.textView.getLeft() - this.middleTextView.getMeasuredWidth();
                TextView textView2 = this.middleTextView;
                textView2.layout(l, textView2.getTop(), this.middleTextView.getMeasuredWidth() + l, this.middleTextView.getBottom());
                return;
            }
            int l2 = this.textView.getRight();
            TextView textView3 = this.middleTextView;
            textView3.layout(l2, textView3.getTop(), this.middleTextView.getMeasuredWidth() + l2, this.middleTextView.getBottom());
        }

        /* access modifiers changed from: protected */
        public void onCollapseClick() {
        }

        public void setText(String left, int percent, int votesCount, int collapsed) {
            SpannableStringBuilder builder;
            TextView textView2 = this.textView;
            textView2.setText(Emoji.replaceEmoji(left, textView2.getPaint().getFontMetricsInt(), AndroidUtilities.dp(14.0f), false));
            String p = String.format("%d", new Object[]{Integer.valueOf(percent)});
            if (LocaleController.isRTL) {
                builder = new SpannableStringBuilder(String.format("%s%% – ", new Object[]{Integer.valueOf(percent)}));
            } else {
                builder = new SpannableStringBuilder(String.format(" – %s%%", new Object[]{Integer.valueOf(percent)}));
            }
            builder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf")), 3, p.length() + 3, 33);
            this.middleTextView.setText(builder);
            if (collapsed == 0) {
                if (this.this$0.poll.quiz) {
                    this.righTextView.setText(LocaleController.formatPluralString("Answer", votesCount));
                } else {
                    this.righTextView.setText(LocaleController.formatPluralString("Vote", votesCount));
                }
            } else if (collapsed == 1) {
                this.righTextView.setText(LocaleController.getString("PollExpand", NUM));
            } else {
                this.righTextView.setText(LocaleController.getString("PollCollapse", NUM));
            }
        }
    }

    public class UserCell extends FrameLayout {
        /* access modifiers changed from: private */
        public ArrayList<Animator> animators;
        private AvatarDrawable avatarDrawable;
        private BackupImageView avatarImageView;
        private int currentAccount = UserConfig.selectedAccount;
        /* access modifiers changed from: private */
        public TLRPC.User currentUser;
        private boolean drawPlaceholder;
        private TLRPC.FileLocation lastAvatar;
        private String lastName;
        private int lastStatus;
        private SimpleTextView nameTextView;
        private boolean needDivider;
        private float placeholderAlpha = 1.0f;
        private int placeholderNum;

        public UserCell(Context context) {
            super(context);
            setWillNotDraw(false);
            this.avatarDrawable = new AvatarDrawable();
            BackupImageView backupImageView = new BackupImageView(context);
            this.avatarImageView = backupImageView;
            backupImageView.setRoundRadius(AndroidUtilities.dp(18.0f));
            int i = 5;
            addView(this.avatarImageView, LayoutHelper.createFrame(36, 36.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 14.0f, 6.0f, LocaleController.isRTL ? 14.0f : 0.0f, 0.0f));
            SimpleTextView simpleTextView = new SimpleTextView(context);
            this.nameTextView = simpleTextView;
            simpleTextView.setTextColor(Theme.getColor("dialogTextBlack"));
            this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.nameTextView.setTextSize(16);
            this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            addView(this.nameTextView, LayoutHelper.createFrame(-1, 20.0f, (!LocaleController.isRTL ? 3 : i) | 48, LocaleController.isRTL ? 28.0f : 65.0f, 14.0f, LocaleController.isRTL ? 65.0f : 28.0f, 0.0f));
        }

        public void setData(TLRPC.User user, int num, boolean divider) {
            this.currentUser = user;
            this.needDivider = divider;
            this.drawPlaceholder = user == null;
            this.placeholderNum = num;
            if (user == null) {
                this.nameTextView.setText("");
                this.avatarImageView.setImageDrawable((Drawable) null);
            } else {
                update(0);
            }
            ArrayList<Animator> arrayList = this.animators;
            if (arrayList != null) {
                arrayList.add(ObjectAnimator.ofFloat(this.avatarImageView, View.ALPHA, new float[]{0.0f, 1.0f}));
                this.animators.add(ObjectAnimator.ofFloat(this.nameTextView, View.ALPHA, new float[]{0.0f, 1.0f}));
                this.animators.add(ObjectAnimator.ofFloat(this, PollVotesAlert.USER_CELL_PROPERTY, new float[]{1.0f, 0.0f}));
            } else if (!this.drawPlaceholder) {
                this.placeholderAlpha = 0.0f;
            }
        }

        public void setPlaceholderAlpha(float value) {
            this.placeholderAlpha = value;
            invalidate();
        }

        public float getPlaceholderAlpha() {
            return this.placeholderAlpha;
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f) + (this.needDivider ? 1 : 0), NUM));
        }

        public void update(int mask) {
            TLRPC.FileLocation fileLocation;
            TLRPC.FileLocation photo = null;
            String newName = null;
            TLRPC.User user = this.currentUser;
            if (!(user == null || user.photo == null)) {
                photo = this.currentUser.photo.photo_small;
            }
            if (mask != 0) {
                boolean continueUpdate = false;
                if ((MessagesController.UPDATE_MASK_AVATAR & mask) != 0 && (((fileLocation = this.lastAvatar) != null && photo == null) || ((fileLocation == null && photo != null) || !(fileLocation == null || photo == null || (fileLocation.volume_id == photo.volume_id && this.lastAvatar.local_id == photo.local_id))))) {
                    continueUpdate = true;
                }
                if (!(this.currentUser == null || continueUpdate || (MessagesController.UPDATE_MASK_STATUS & mask) == 0)) {
                    int newStatus = 0;
                    if (this.currentUser.status != null) {
                        newStatus = this.currentUser.status.expires;
                    }
                    if (newStatus != this.lastStatus) {
                        continueUpdate = true;
                    }
                }
                if (!(continueUpdate || this.lastName == null || (MessagesController.UPDATE_MASK_NAME & mask) == 0)) {
                    TLRPC.User user2 = this.currentUser;
                    if (user2 != null) {
                        newName = UserObject.getUserName(user2);
                    }
                    if (!newName.equals(this.lastName)) {
                        continueUpdate = true;
                    }
                }
                if (!continueUpdate) {
                    return;
                }
            }
            this.avatarDrawable.setInfo(this.currentUser);
            if (this.currentUser.status != null) {
                this.lastStatus = this.currentUser.status.expires;
            } else {
                this.lastStatus = 0;
            }
            TLRPC.User user3 = this.currentUser;
            if (user3 != null) {
                this.lastName = newName == null ? UserObject.getUserName(user3) : newName;
            } else {
                this.lastName = "";
            }
            this.nameTextView.setText(this.lastName);
            this.lastAvatar = photo;
            TLRPC.User user4 = this.currentUser;
            if (user4 != null) {
                this.avatarImageView.setForUserOrChat(user4, this.avatarDrawable);
            } else {
                this.avatarImageView.setImageDrawable(this.avatarDrawable);
            }
        }

        public boolean hasOverlappingRendering() {
            return false;
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            int w;
            int cx;
            int w2;
            int cx2;
            if (this.drawPlaceholder || this.placeholderAlpha != 0.0f) {
                PollVotesAlert.this.placeholderPaint.setAlpha((int) (this.placeholderAlpha * 255.0f));
                int cx3 = this.avatarImageView.getLeft() + (this.avatarImageView.getMeasuredWidth() / 2);
                int cy = this.avatarImageView.getTop() + (this.avatarImageView.getMeasuredHeight() / 2);
                canvas.drawCircle((float) cx3, (float) cy, (float) (this.avatarImageView.getMeasuredWidth() / 2), PollVotesAlert.this.placeholderPaint);
                if (this.placeholderNum % 2 == 0) {
                    cx = AndroidUtilities.dp(65.0f);
                    w = AndroidUtilities.dp(48.0f);
                } else {
                    cx = AndroidUtilities.dp(65.0f);
                    w = AndroidUtilities.dp(60.0f);
                }
                if (LocaleController.isRTL) {
                    cx = (getMeasuredWidth() - cx) - w;
                }
                PollVotesAlert.this.rect.set((float) cx, (float) (cy - AndroidUtilities.dp(4.0f)), (float) (cx + w), (float) (AndroidUtilities.dp(4.0f) + cy));
                canvas.drawRoundRect(PollVotesAlert.this.rect, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), PollVotesAlert.this.placeholderPaint);
                if (this.placeholderNum % 2 == 0) {
                    cx2 = AndroidUtilities.dp(119.0f);
                    w2 = AndroidUtilities.dp(60.0f);
                } else {
                    cx2 = AndroidUtilities.dp(131.0f);
                    w2 = AndroidUtilities.dp(80.0f);
                }
                if (LocaleController.isRTL) {
                    cx2 = (getMeasuredWidth() - cx2) - w2;
                }
                PollVotesAlert.this.rect.set((float) cx2, (float) (cy - AndroidUtilities.dp(4.0f)), (float) (cx2 + w2), (float) (AndroidUtilities.dp(4.0f) + cy));
                canvas.drawRoundRect(PollVotesAlert.this.rect, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), PollVotesAlert.this.placeholderPaint);
            }
            if (this.needDivider != 0) {
                canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(64.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(64.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
            }
        }
    }

    public static void showForPoll(ChatActivity parentFragment, MessageObject messageObject2) {
        if (parentFragment != null && parentFragment.getParentActivity() != null) {
            parentFragment.showDialog(new PollVotesAlert(parentFragment, messageObject2));
        }
    }

    private static class Button {
        /* access modifiers changed from: private */
        public float decimal;
        /* access modifiers changed from: private */
        public int percent;
        /* access modifiers changed from: private */
        public int votesCount;

        private Button() {
        }

        static /* synthetic */ float access$3924(Button x0, float x1) {
            float f = x0.decimal - x1;
            x0.decimal = f;
            return f;
        }

        static /* synthetic */ int access$4012(Button x0, int x1) {
            int i = x0.percent + x1;
            x0.percent = i;
            return i;
        }
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public PollVotesAlert(ChatActivity parentFragment, MessageObject message) {
        super(parentFragment.getParentActivity(), true);
        int count;
        ChatActivity chatActivity2 = parentFragment;
        MessageObject messageObject2 = message;
        this.messageObject = messageObject2;
        this.chatActivity = chatActivity2;
        TLRPC.TL_messageMediaPoll mediaPoll = (TLRPC.TL_messageMediaPoll) messageObject2.messageOwner.media;
        this.poll = mediaPoll.poll;
        Context context = parentFragment.getParentActivity();
        this.peer = parentFragment.getMessagesController().getInputPeer((long) ((int) message.getDialogId()));
        ArrayList<VotesList> loadedVoters = new ArrayList<>();
        int count2 = mediaPoll.results.results.size();
        Integer[] reqIds = new Integer[count2];
        int a = 0;
        while (a < count2) {
            TLRPC.TL_pollAnswerVoters answerVoters = mediaPoll.results.results.get(a);
            if (answerVoters.voters == 0) {
                count = count2;
            } else {
                TLRPC.TL_messages_votesList votesList = new TLRPC.TL_messages_votesList();
                int i = 15;
                int N = answerVoters.voters <= 15 ? answerVoters.voters : 10;
                for (int b = 0; b < N; b++) {
                    votesList.votes.add(new TLRPC.TL_messageUserVoteInputOption());
                }
                votesList.next_offset = N < answerVoters.voters ? "empty" : null;
                votesList.count = answerVoters.voters;
                VotesList list = new VotesList(votesList, answerVoters.option);
                this.voters.add(list);
                TLRPC.TL_messages_getPollVotes req = new TLRPC.TL_messages_getPollVotes();
                req.peer = this.peer;
                req.id = this.messageObject.getId();
                req.limit = answerVoters.voters > 15 ? 10 : i;
                req.flags |= 1;
                req.option = answerVoters.option;
                int i2 = N;
                VotesList votesList2 = list;
                count = count2;
                TLRPC.TL_messages_votesList tL_messages_votesList = votesList;
                TLRPC.TL_pollAnswerVoters tL_pollAnswerVoters = answerVoters;
                reqIds[a] = Integer.valueOf(parentFragment.getConnectionsManager().sendRequest(req, new PollVotesAlert$$ExternalSyntheticLambda4(this, reqIds, a, parentFragment, loadedVoters, answerVoters)));
                this.queries.add(reqIds[a]);
            }
            a++;
            MessageObject messageObject3 = message;
            count2 = count;
        }
        updateButtons();
        Collections.sort(this.voters, new Comparator<VotesList>() {
            private int getIndex(VotesList votesList) {
                int N = PollVotesAlert.this.poll.answers.size();
                for (int a = 0; a < N; a++) {
                    if (Arrays.equals(PollVotesAlert.this.poll.answers.get(a).option, votesList.option)) {
                        return a;
                    }
                }
                return 0;
            }

            public int compare(VotesList o1, VotesList o2) {
                int i1 = getIndex(o1);
                int i2 = getIndex(o2);
                if (i1 > i2) {
                    return 1;
                }
                if (i1 < i2) {
                    return -1;
                }
                return 0;
            }
        });
        updatePlaceholder();
        Drawable mutate = context.getResources().getDrawable(NUM).mutate();
        this.shadowDrawable = mutate;
        mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogBackground"), PorterDuff.Mode.MULTIPLY));
        this.containerView = new FrameLayout(context) {
            private boolean ignoreLayout = false;
            private RectF rect = new RectF();

            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int totalHeight = View.MeasureSpec.getSize(heightMeasureSpec);
                if (Build.VERSION.SDK_INT >= 21 && !PollVotesAlert.this.isFullscreen) {
                    this.ignoreLayout = true;
                    setPadding(PollVotesAlert.this.backgroundPaddingLeft, AndroidUtilities.statusBarHeight, PollVotesAlert.this.backgroundPaddingLeft, 0);
                    this.ignoreLayout = false;
                }
                int availableHeight = totalHeight - getPaddingTop();
                ((FrameLayout.LayoutParams) PollVotesAlert.this.listView.getLayoutParams()).topMargin = ActionBar.getCurrentActionBarHeight();
                ((FrameLayout.LayoutParams) PollVotesAlert.this.actionBarShadow.getLayoutParams()).topMargin = ActionBar.getCurrentActionBarHeight();
                int contentSize = PollVotesAlert.this.backgroundPaddingTop + AndroidUtilities.dp(15.0f) + AndroidUtilities.statusBarHeight;
                int sectionCount = PollVotesAlert.this.listAdapter.getSectionCount();
                for (int a = 0; a < sectionCount; a++) {
                    if (a == 0) {
                        PollVotesAlert.this.titleTextView.measure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec - (PollVotesAlert.this.backgroundPaddingLeft * 2)), NUM), heightMeasureSpec);
                        contentSize += PollVotesAlert.this.titleTextView.getMeasuredHeight();
                    } else {
                        contentSize += AndroidUtilities.dp(32.0f) + (AndroidUtilities.dp(50.0f) * (PollVotesAlert.this.listAdapter.getCountForSection(a) - 1));
                    }
                }
                int padding = (contentSize < availableHeight ? availableHeight - contentSize : availableHeight - ((availableHeight / 5) * 3)) + AndroidUtilities.dp(8.0f);
                if (PollVotesAlert.this.listView.getPaddingTop() != padding) {
                    this.ignoreLayout = true;
                    PollVotesAlert.this.listView.setPinnedSectionOffsetY(-padding);
                    PollVotesAlert.this.listView.setPadding(0, padding, 0, 0);
                    this.ignoreLayout = false;
                }
                super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(totalHeight, NUM));
            }

            /* access modifiers changed from: protected */
            public void onLayout(boolean changed, int l, int t, int r, int b) {
                super.onLayout(changed, l, t, r, b);
                PollVotesAlert.this.updateLayout(false);
            }

            public boolean onInterceptTouchEvent(MotionEvent ev) {
                if (ev.getAction() != 0 || PollVotesAlert.this.scrollOffsetY == 0 || ev.getY() >= ((float) (PollVotesAlert.this.scrollOffsetY + AndroidUtilities.dp(12.0f))) || PollVotesAlert.this.actionBar.getAlpha() != 0.0f) {
                    return super.onInterceptTouchEvent(ev);
                }
                PollVotesAlert.this.dismiss();
                return true;
            }

            public boolean onTouchEvent(MotionEvent e) {
                return !PollVotesAlert.this.isDismissed() && super.onTouchEvent(e);
            }

            public void requestLayout() {
                if (!this.ignoreLayout) {
                    super.requestLayout();
                }
            }

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                int offset = AndroidUtilities.dp(13.0f);
                int top = (PollVotesAlert.this.scrollOffsetY - PollVotesAlert.this.backgroundPaddingTop) - offset;
                if (PollVotesAlert.this.currentSheetAnimationType == 1) {
                    top = (int) (((float) top) + PollVotesAlert.this.listView.getTranslationY());
                }
                int y = AndroidUtilities.dp(20.0f) + top;
                int height = getMeasuredHeight() + AndroidUtilities.dp(15.0f) + PollVotesAlert.this.backgroundPaddingTop;
                float rad = 1.0f;
                if (PollVotesAlert.this.backgroundPaddingTop + top < ActionBar.getCurrentActionBarHeight()) {
                    float toMove = (float) (AndroidUtilities.dp(4.0f) + offset);
                    float moveProgress = Math.min(1.0f, ((float) ((ActionBar.getCurrentActionBarHeight() - top) - PollVotesAlert.this.backgroundPaddingTop)) / toMove);
                    int diff = (int) ((((float) ActionBar.getCurrentActionBarHeight()) - toMove) * moveProgress);
                    top -= diff;
                    y -= diff;
                    height += diff;
                    rad = 1.0f - moveProgress;
                }
                if (Build.VERSION.SDK_INT >= 21) {
                    top += AndroidUtilities.statusBarHeight;
                    y += AndroidUtilities.statusBarHeight;
                }
                PollVotesAlert.this.shadowDrawable.setBounds(0, top, getMeasuredWidth(), height);
                PollVotesAlert.this.shadowDrawable.draw(canvas);
                if (rad != 1.0f) {
                    Theme.dialogs_onlineCirclePaint.setColor(Theme.getColor("dialogBackground"));
                    this.rect.set((float) PollVotesAlert.this.backgroundPaddingLeft, (float) (PollVotesAlert.this.backgroundPaddingTop + top), (float) (getMeasuredWidth() - PollVotesAlert.this.backgroundPaddingLeft), (float) (PollVotesAlert.this.backgroundPaddingTop + top + AndroidUtilities.dp(24.0f)));
                    canvas.drawRoundRect(this.rect, ((float) AndroidUtilities.dp(12.0f)) * rad, ((float) AndroidUtilities.dp(12.0f)) * rad, Theme.dialogs_onlineCirclePaint);
                }
                if (rad != 0.0f) {
                    int w = AndroidUtilities.dp(36.0f);
                    this.rect.set((float) ((getMeasuredWidth() - w) / 2), (float) y, (float) ((getMeasuredWidth() + w) / 2), (float) (AndroidUtilities.dp(4.0f) + y));
                    int color = Theme.getColor("key_sheet_scrollUp");
                    int alpha = Color.alpha(color);
                    Theme.dialogs_onlineCirclePaint.setColor(color);
                    Theme.dialogs_onlineCirclePaint.setAlpha((int) (((float) alpha) * 1.0f * rad));
                    canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(2.0f), Theme.dialogs_onlineCirclePaint);
                }
                int color1 = Theme.getColor("dialogBackground");
                Theme.dialogs_onlineCirclePaint.setColor(Color.argb((int) (PollVotesAlert.this.actionBar.getAlpha() * 255.0f), (int) (((float) Color.red(color1)) * 0.8f), (int) (((float) Color.green(color1)) * 0.8f), (int) (((float) Color.blue(color1)) * 0.8f)));
                canvas.drawRect((float) PollVotesAlert.this.backgroundPaddingLeft, 0.0f, (float) (getMeasuredWidth() - PollVotesAlert.this.backgroundPaddingLeft), (float) AndroidUtilities.statusBarHeight, Theme.dialogs_onlineCirclePaint);
            }
        };
        this.containerView.setWillNotDraw(false);
        this.containerView.setPadding(this.backgroundPaddingLeft, 0, this.backgroundPaddingLeft, 0);
        AnonymousClass4 r0 = new RecyclerListView(context) {
            long lastUpdateTime;

            /* access modifiers changed from: protected */
            public boolean allowSelectChildAtPosition(float x, float y) {
                return y >= ((float) (PollVotesAlert.this.scrollOffsetY + (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0)));
            }

            /* access modifiers changed from: protected */
            public void dispatchDraw(Canvas canvas) {
                if (PollVotesAlert.this.loadingResults) {
                    long newUpdateTime = SystemClock.elapsedRealtime();
                    long dt = Math.abs(this.lastUpdateTime - newUpdateTime);
                    if (dt > 17) {
                        dt = 16;
                    }
                    this.lastUpdateTime = newUpdateTime;
                    PollVotesAlert pollVotesAlert = PollVotesAlert.this;
                    PollVotesAlert.access$3016(pollVotesAlert, (((float) dt) * pollVotesAlert.gradientWidth) / 1800.0f);
                    while (PollVotesAlert.this.totalTranslation >= PollVotesAlert.this.gradientWidth * 2.0f) {
                        PollVotesAlert pollVotesAlert2 = PollVotesAlert.this;
                        PollVotesAlert.access$3024(pollVotesAlert2, pollVotesAlert2.gradientWidth * 2.0f);
                    }
                    PollVotesAlert.this.placeholderMatrix.setTranslate(PollVotesAlert.this.totalTranslation, 0.0f);
                    PollVotesAlert.this.placeholderGradient.setLocalMatrix(PollVotesAlert.this.placeholderMatrix);
                    invalidateViews();
                    invalidate();
                }
                super.dispatchDraw(canvas);
            }
        };
        this.listView = r0;
        r0.setClipToPadding(false);
        this.listView.setLayoutManager(new LinearLayoutManager(getContext(), 1, false));
        this.listView.setHorizontalScrollBarEnabled(false);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setSectionsType(2);
        this.containerView.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        RecyclerListView recyclerListView = this.listView;
        Adapter adapter = new Adapter(context);
        this.listAdapter = adapter;
        recyclerListView.setAdapter(adapter);
        this.listView.setGlowColor(Theme.getColor("dialogScrollGlow"));
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new PollVotesAlert$$ExternalSyntheticLambda6(this, chatActivity2));
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (PollVotesAlert.this.listView.getChildCount() > 0) {
                    PollVotesAlert.this.updateLayout(true);
                }
            }

            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 0) {
                    int offset = AndroidUtilities.dp(13.0f);
                    if (PollVotesAlert.this.backgroundPaddingTop + ((PollVotesAlert.this.scrollOffsetY - PollVotesAlert.this.backgroundPaddingTop) - offset) < ActionBar.getCurrentActionBarHeight() && PollVotesAlert.this.listView.canScrollVertically(1)) {
                        View childAt = PollVotesAlert.this.listView.getChildAt(0);
                        RecyclerListView.Holder holder = (RecyclerListView.Holder) PollVotesAlert.this.listView.findViewHolderForAdapterPosition(0);
                        if (holder != null && holder.itemView.getTop() > AndroidUtilities.dp(7.0f)) {
                            PollVotesAlert.this.listView.smoothScrollBy(0, holder.itemView.getTop() - AndroidUtilities.dp(7.0f));
                        }
                    }
                }
            }
        });
        TextView textView = new TextView(context);
        this.titleTextView = textView;
        textView.setTextSize(1, 18.0f);
        this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.titleTextView.setPadding(AndroidUtilities.dp(21.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(14.0f), AndroidUtilities.dp(21.0f));
        this.titleTextView.setTextColor(Theme.getColor("dialogTextBlack"));
        this.titleTextView.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
        this.titleTextView.setText(Emoji.replaceEmoji(this.poll.question, this.titleTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(18.0f), false));
        AnonymousClass6 r02 = new ActionBar(context) {
            public void setAlpha(float alpha) {
                super.setAlpha(alpha);
                PollVotesAlert.this.containerView.invalidate();
            }
        };
        this.actionBar = r02;
        r02.setBackgroundColor(Theme.getColor("dialogBackground"));
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setItemsColor(Theme.getColor("dialogTextBlack"), false);
        this.actionBar.setItemsBackgroundColor(Theme.getColor("dialogButtonSelector"), false);
        this.actionBar.setTitleColor(Theme.getColor("dialogTextBlack"));
        this.actionBar.setSubtitleColor(Theme.getColor("player_actionBarSubtitle"));
        this.actionBar.setOccupyStatusBar(false);
        this.actionBar.setAlpha(0.0f);
        this.actionBar.setTitle(LocaleController.getString("PollResults", NUM));
        if (this.poll.quiz) {
            this.actionBar.setSubtitle(LocaleController.formatPluralString("Answer", mediaPoll.results.total_voters));
        } else {
            this.actionBar.setSubtitle(LocaleController.formatPluralString("Vote", mediaPoll.results.total_voters));
        }
        this.containerView.addView(this.actionBar, LayoutHelper.createFrame(-1, -2.0f));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    PollVotesAlert.this.dismiss();
                }
            }
        });
        View view = new View(context);
        this.actionBarShadow = view;
        view.setAlpha(0.0f);
        this.actionBarShadow.setBackgroundColor(Theme.getColor("dialogShadowLine"));
        this.containerView.addView(this.actionBarShadow, LayoutHelper.createFrame(-1, 1.0f));
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-PollVotesAlert  reason: not valid java name */
    public /* synthetic */ void m4241lambda$new$1$orgtelegramuiComponentsPollVotesAlert(Integer[] reqIds, int num, ChatActivity parentFragment, ArrayList loadedVoters, TLRPC.TL_pollAnswerVoters answerVoters, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new PollVotesAlert$$ExternalSyntheticLambda1(this, reqIds, num, response, parentFragment, loadedVoters, answerVoters));
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-PollVotesAlert  reason: not valid java name */
    public /* synthetic */ void m4240lambda$new$0$orgtelegramuiComponentsPollVotesAlert(Integer[] reqIds, int num, TLObject response, ChatActivity parentFragment, ArrayList loadedVoters, TLRPC.TL_pollAnswerVoters answerVoters) {
        RecyclerView.ViewHolder holder;
        ArrayList arrayList = loadedVoters;
        this.queries.remove(reqIds[num]);
        if (response != null) {
            TLRPC.TL_messages_votesList res = (TLRPC.TL_messages_votesList) response;
            parentFragment.getMessagesController().putUsers(res.users, false);
            if (!res.votes.isEmpty()) {
                arrayList.add(new VotesList(res, answerVoters.option));
            } else {
                TLRPC.TL_pollAnswerVoters tL_pollAnswerVoters = answerVoters;
            }
            if (this.queries.isEmpty()) {
                boolean countChanged = false;
                int N2 = loadedVoters.size();
                for (int b = 0; b < N2; b++) {
                    VotesList votesList1 = (VotesList) arrayList.get(b);
                    int c = 0;
                    int N3 = this.voters.size();
                    while (true) {
                        if (c >= N3) {
                            break;
                        }
                        VotesList votesList2 = this.voters.get(c);
                        if (Arrays.equals(votesList1.option, votesList2.option)) {
                            votesList2.next_offset = votesList1.next_offset;
                            if (!(votesList2.count == votesList1.count && votesList2.votes.size() == votesList1.votes.size())) {
                                countChanged = true;
                            }
                            votesList2.count = votesList1.count;
                            votesList2.users = votesList1.users;
                            votesList2.votes = votesList1.votes;
                        } else {
                            c++;
                        }
                    }
                }
                this.loadingResults = false;
                if (this.listView == null) {
                    return;
                }
                if (this.currentSheetAnimationType == 0 && this.startAnimationRunnable == null && !countChanged) {
                    int c2 = this.listView.getChildCount();
                    ArrayList<Animator> animators = new ArrayList<>();
                    for (int b2 = 0; b2 < c2; b2++) {
                        View child = this.listView.getChildAt(b2);
                        if ((child instanceof UserCell) && (holder = this.listView.findContainingViewHolder(child)) != null) {
                            UserCell cell = (UserCell) child;
                            ArrayList unused = cell.animators = animators;
                            cell.setEnabled(true);
                            this.listAdapter.onViewAttachedToWindow(holder);
                            ArrayList unused2 = cell.animators = null;
                        }
                    }
                    if (animators.isEmpty() == 0) {
                        AnimatorSet animatorSet = new AnimatorSet();
                        animatorSet.playTogether(animators);
                        animatorSet.setDuration(180);
                        animatorSet.start();
                    }
                    this.loadingResults = false;
                    return;
                }
                if (countChanged) {
                    updateButtons();
                }
                this.listAdapter.notifyDataSetChanged();
                return;
            }
            return;
        }
        TLRPC.TL_pollAnswerVoters tL_pollAnswerVoters2 = answerVoters;
        dismiss();
    }

    /* renamed from: lambda$new$4$org-telegram-ui-Components-PollVotesAlert  reason: not valid java name */
    public /* synthetic */ void m4244lambda$new$4$orgtelegramuiComponentsPollVotesAlert(ChatActivity parentFragment, View view, int position) {
        if (parentFragment != null && parentFragment.getParentActivity() != null) {
            ArrayList<Integer> arrayList = this.queries;
            if (arrayList == null || arrayList.isEmpty()) {
                int i = 0;
                if (view instanceof TextCell) {
                    int section = this.listAdapter.getSectionForPosition(position) - 1;
                    int row = this.listAdapter.getPositionInSectionForPosition(position) - 1;
                    if (row > 0 && section >= 0) {
                        VotesList votesList = this.voters.get(section);
                        if (row == votesList.getCount() && !this.loadingMore.contains(votesList)) {
                            if (!votesList.collapsed || votesList.collapsedCount >= votesList.votes.size()) {
                                this.loadingMore.add(votesList);
                                TLRPC.TL_messages_getPollVotes req = new TLRPC.TL_messages_getPollVotes();
                                req.peer = this.peer;
                                req.id = this.messageObject.getId();
                                req.limit = 50;
                                req.flags = 1 | req.flags;
                                req.option = votesList.option;
                                req.flags |= 2;
                                req.offset = votesList.next_offset;
                                this.chatActivity.getConnectionsManager().sendRequest(req, new PollVotesAlert$$ExternalSyntheticLambda3(this, votesList, parentFragment));
                                return;
                            }
                            votesList.collapsedCount = Math.min(votesList.collapsedCount + 50, votesList.votes.size());
                            if (votesList.collapsedCount == votesList.votes.size()) {
                                votesList.collapsed = false;
                            }
                            this.listAdapter.notifyDataSetChanged();
                        }
                    }
                } else if ((view instanceof UserCell) != 0) {
                    UserCell userCell = (UserCell) view;
                    if (userCell.currentUser != null) {
                        TLRPC.User currentUser = parentFragment.getCurrentUser();
                        Bundle args = new Bundle();
                        args.putLong("user_id", userCell.currentUser.id);
                        dismiss();
                        ProfileActivity fragment = new ProfileActivity(args);
                        if (currentUser != null && currentUser.id == userCell.currentUser.id) {
                            i = 1;
                        }
                        fragment.setPlayProfileAnimation(i);
                        parentFragment.presentFragment(fragment);
                    }
                }
            }
        }
    }

    /* renamed from: lambda$new$3$org-telegram-ui-Components-PollVotesAlert  reason: not valid java name */
    public /* synthetic */ void m4243lambda$new$3$orgtelegramuiComponentsPollVotesAlert(VotesList votesList, ChatActivity parentFragment, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new PollVotesAlert$$ExternalSyntheticLambda0(this, votesList, response, parentFragment));
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-PollVotesAlert  reason: not valid java name */
    public /* synthetic */ void m4242lambda$new$2$orgtelegramuiComponentsPollVotesAlert(VotesList votesList, TLObject response, ChatActivity parentFragment) {
        if (isShowing()) {
            this.loadingMore.remove(votesList);
            if (response != null) {
                TLRPC.TL_messages_votesList res = (TLRPC.TL_messages_votesList) response;
                parentFragment.getMessagesController().putUsers(res.users, false);
                votesList.votes.addAll(res.votes);
                votesList.next_offset = res.next_offset;
                this.listAdapter.notifyDataSetChanged();
            }
        }
    }

    private int getCurrentTop() {
        if (this.listView.getChildCount() == 0) {
            return -1000;
        }
        int i = 0;
        View child = this.listView.getChildAt(0);
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(child);
        if (holder == null) {
            return -1000;
        }
        int paddingTop = this.listView.getPaddingTop();
        if (holder.getAdapterPosition() == 0 && child.getTop() >= 0) {
            i = child.getTop();
        }
        return paddingTop - i;
    }

    private void updateButtons() {
        this.votesPercents.clear();
        int restPercent = 100;
        boolean hasDifferent = false;
        int previousPercent = 0;
        TLRPC.TL_messageMediaPoll media = (TLRPC.TL_messageMediaPoll) this.messageObject.messageOwner.media;
        ArrayList<Button> sortedPollButtons = new ArrayList<>();
        int maxVote = 0;
        int N = this.voters.size();
        for (int a = 0; a < N; a++) {
            VotesList list = this.voters.get(a);
            Button button = new Button();
            sortedPollButtons.add(button);
            this.votesPercents.put(list, button);
            if (!media.results.results.isEmpty()) {
                int b = 0;
                int N2 = media.results.results.size();
                while (true) {
                    if (b >= N2) {
                        break;
                    }
                    TLRPC.TL_pollAnswerVoters answer = media.results.results.get(b);
                    if (Arrays.equals(list.option, answer.option)) {
                        int unused = button.votesCount = answer.voters;
                        float unused2 = button.decimal = (((float) answer.voters) / ((float) media.results.total_voters)) * 100.0f;
                        int unused3 = button.percent = (int) button.decimal;
                        Button.access$3924(button, (float) button.percent);
                        if (previousPercent == 0) {
                            previousPercent = button.percent;
                        } else if (!(button.percent == 0 || previousPercent == button.percent)) {
                            hasDifferent = true;
                        }
                        restPercent -= button.percent;
                        maxVote = Math.max(button.percent, maxVote);
                    } else {
                        b++;
                    }
                }
            }
        }
        if (hasDifferent && restPercent != 0) {
            Collections.sort(sortedPollButtons, PollVotesAlert$$ExternalSyntheticLambda2.INSTANCE);
            int N3 = Math.min(restPercent, sortedPollButtons.size());
            for (int a2 = 0; a2 < N3; a2++) {
                Button.access$4012(sortedPollButtons.get(a2), 1);
            }
        }
    }

    static /* synthetic */ int lambda$updateButtons$5(Button o1, Button o2) {
        if (o1.decimal > o2.decimal) {
            return -1;
        }
        if (o1.decimal < o2.decimal) {
            return 1;
        }
        return 0;
    }

    /* access modifiers changed from: protected */
    public boolean canDismissWithSwipe() {
        return false;
    }

    public void dismissInternal() {
        int N = this.queries.size();
        for (int a = 0; a < N; a++) {
            this.chatActivity.getConnectionsManager().cancelRequest(this.queries.get(a).intValue(), true);
        }
        super.dismissInternal();
    }

    /* access modifiers changed from: private */
    public void updateLayout(boolean animated) {
        if (this.listView.getChildCount() <= 0) {
            RecyclerListView recyclerListView = this.listView;
            int paddingTop = recyclerListView.getPaddingTop();
            this.scrollOffsetY = paddingTop;
            recyclerListView.setTopGlowOffset(paddingTop);
            this.containerView.invalidate();
            return;
        }
        View child = this.listView.getChildAt(0);
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(child);
        int top = child.getTop();
        int newOffset = AndroidUtilities.dp(7.0f);
        if (top >= AndroidUtilities.dp(7.0f) && holder != null && holder.getAdapterPosition() == 0) {
            newOffset = top;
        }
        boolean show = newOffset <= AndroidUtilities.dp(12.0f);
        if ((show && this.actionBar.getTag() == null) || (!show && this.actionBar.getTag() != null)) {
            this.actionBar.setTag(show ? 1 : null);
            AnimatorSet animatorSet = this.actionBarAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.actionBarAnimation = null;
            }
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.actionBarAnimation = animatorSet2;
            animatorSet2.setDuration(180);
            AnimatorSet animatorSet3 = this.actionBarAnimation;
            Animator[] animatorArr = new Animator[2];
            ActionBar actionBar2 = this.actionBar;
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            float f = 1.0f;
            fArr[0] = show ? 1.0f : 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(actionBar2, property, fArr);
            View view = this.actionBarShadow;
            Property property2 = View.ALPHA;
            float[] fArr2 = new float[1];
            if (!show) {
                f = 0.0f;
            }
            fArr2[0] = f;
            animatorArr[1] = ObjectAnimator.ofFloat(view, property2, fArr2);
            animatorSet3.playTogether(animatorArr);
            this.actionBarAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                }

                public void onAnimationCancel(Animator animation) {
                    AnimatorSet unused = PollVotesAlert.this.actionBarAnimation = null;
                }
            });
            this.actionBarAnimation.start();
        }
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.listView.getLayoutParams();
        int newOffset2 = newOffset + (layoutParams.topMargin - AndroidUtilities.dp(11.0f));
        if (this.scrollOffsetY != newOffset2) {
            RecyclerListView recyclerListView2 = this.listView;
            this.scrollOffsetY = newOffset2;
            recyclerListView2.setTopGlowOffset(newOffset2 - layoutParams.topMargin);
            this.containerView.invalidate();
        }
    }

    /* access modifiers changed from: private */
    public void updatePlaceholder() {
        if (this.placeholderPaint != null) {
            int color0 = Theme.getColor("dialogBackground");
            int color1 = Theme.getColor("dialogBackgroundGray");
            int color02 = AndroidUtilities.getAverageColor(color1, color0);
            this.placeholderPaint.setColor(color1);
            float dp = (float) AndroidUtilities.dp(500.0f);
            this.gradientWidth = dp;
            LinearGradient linearGradient = new LinearGradient(0.0f, 0.0f, dp, 0.0f, new int[]{color1, color02, color1}, new float[]{0.0f, 0.18f, 0.36f}, Shader.TileMode.REPEAT);
            this.placeholderGradient = linearGradient;
            this.placeholderPaint.setShader(linearGradient);
            Matrix matrix = new Matrix();
            this.placeholderMatrix = matrix;
            this.placeholderGradient.setLocalMatrix(matrix);
        }
    }

    public class Adapter extends RecyclerListView.SectionsAdapter {
        private int currentAccount = UserConfig.selectedAccount;
        private Context mContext;

        public Adapter(Context context) {
            this.mContext = context;
        }

        public Object getItem(int section, int position) {
            return null;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder, int section, int row) {
            if (section == 0 || row == 0) {
                return false;
            }
            if (PollVotesAlert.this.queries == null || PollVotesAlert.this.queries.isEmpty()) {
                return true;
            }
            return false;
        }

        public int getSectionCount() {
            return PollVotesAlert.this.voters.size() + 1;
        }

        public int getCountForSection(int section) {
            int i = 1;
            if (section == 0) {
                return 1;
            }
            VotesList votesList = (VotesList) PollVotesAlert.this.voters.get(section - 1);
            int count = votesList.getCount() + 1;
            if (TextUtils.isEmpty(votesList.next_offset) && !votesList.collapsed) {
                i = 0;
            }
            return count + i;
        }

        private SectionCell createSectionCell() {
            return new SectionCell(this.mContext) {
                /* access modifiers changed from: protected */
                public void onCollapseClick() {
                    VotesList list = (VotesList) getTag(NUM);
                    if (list.votes.size() > 15) {
                        list.collapsed = !list.collapsed;
                        if (list.collapsed) {
                            list.collapsedCount = 10;
                        }
                        PollVotesAlert.this.listAdapter.notifyDataSetChanged();
                    }
                }
            };
        }

        public View getSectionHeaderView(int section, View view) {
            if (view == null) {
                view = createSectionCell();
            }
            SectionCell sectionCell = (SectionCell) view;
            if (section != 0) {
                view.setAlpha(1.0f);
                VotesList votesList = (VotesList) PollVotesAlert.this.voters.get(section - 1);
                TLRPC.MessageUserVote messageUserVote = votesList.votes.get(0);
                int a = 0;
                int N = PollVotesAlert.this.poll.answers.size();
                while (true) {
                    if (a >= N) {
                        break;
                    }
                    TLRPC.TL_pollAnswer answer = PollVotesAlert.this.poll.answers.get(a);
                    if (Arrays.equals(answer.option, votesList.option)) {
                        Button button = (Button) PollVotesAlert.this.votesPercents.get(votesList);
                        sectionCell.setText(answer.text, button.percent, button.votesCount, votesList.getCollapsed());
                        sectionCell.setTag(NUM, votesList);
                        break;
                    }
                    a++;
                }
            } else {
                sectionCell.setAlpha(0.0f);
            }
            return view;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new UserCell(this.mContext);
                    break;
                case 1:
                    if (PollVotesAlert.this.titleTextView.getParent() != null) {
                        ((ViewGroup) PollVotesAlert.this.titleTextView.getParent()).removeView(PollVotesAlert.this.titleTextView);
                    }
                    view = PollVotesAlert.this.titleTextView;
                    break;
                case 2:
                    view = createSectionCell();
                    break;
                default:
                    TextCell textCell = new TextCell(this.mContext, 23, true);
                    textCell.setOffsetFromImage(65);
                    textCell.setColors("switchTrackChecked", "windowBackgroundWhiteBlueText4");
                    TextCell textCell2 = textCell;
                    view = textCell;
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(int section, int position, RecyclerView.ViewHolder holder) {
            switch (holder.getItemViewType()) {
                case 2:
                    SectionCell sectionCell = (SectionCell) holder.itemView;
                    VotesList votesList = (VotesList) PollVotesAlert.this.voters.get(section - 1);
                    TLRPC.MessageUserVote messageUserVote = votesList.votes.get(0);
                    int N = PollVotesAlert.this.poll.answers.size();
                    for (int a = 0; a < N; a++) {
                        TLRPC.TL_pollAnswer answer = PollVotesAlert.this.poll.answers.get(a);
                        if (Arrays.equals(answer.option, votesList.option)) {
                            Button button = (Button) PollVotesAlert.this.votesPercents.get(votesList);
                            sectionCell.setText(answer.text, button.percent, button.votesCount, votesList.getCollapsed());
                            sectionCell.setTag(NUM, votesList);
                            return;
                        }
                    }
                    return;
                case 3:
                    VotesList votesList2 = (VotesList) PollVotesAlert.this.voters.get(section - 1);
                    ((TextCell) holder.itemView).setTextAndIcon(LocaleController.formatPluralString("ShowVotes", votesList2.count - votesList2.getCount()), NUM, false);
                    return;
                default:
                    return;
            }
        }

        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            TLRPC.User user;
            if (holder.getItemViewType() == 0) {
                int position = holder.getAdapterPosition();
                int section = getSectionForPosition(position);
                int position2 = getPositionInSectionForPosition(position) - 1;
                UserCell userCell = (UserCell) holder.itemView;
                VotesList votesList = (VotesList) PollVotesAlert.this.voters.get(section - 1);
                TLRPC.MessageUserVote vote = votesList.votes.get(position2);
                if (vote.user_id != 0) {
                    user = PollVotesAlert.this.chatActivity.getMessagesController().getUser(Long.valueOf(vote.user_id));
                } else {
                    user = null;
                }
                boolean z = true;
                if (position2 == votesList.getCount() - 1 && TextUtils.isEmpty(votesList.next_offset) && !votesList.collapsed) {
                    z = false;
                }
                userCell.setData(user, position2, z);
            }
        }

        public int getItemViewType(int section, int position) {
            if (section == 0) {
                return 1;
            }
            if (position == 0) {
                return 2;
            }
            if (position - 1 < ((VotesList) PollVotesAlert.this.voters.get(section - 1)).getCount()) {
                return 0;
            }
            return 3;
        }

        public String getLetter(int position) {
            return null;
        }

        public void getPositionForScrollProgress(RecyclerListView listView, float progress, int[] position) {
            position[0] = 0;
            position[1] = 0;
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        ThemeDescription.ThemeDescriptionDelegate delegate = new PollVotesAlert$$ExternalSyntheticLambda5(this);
        themeDescriptions.add(new ThemeDescription(this.containerView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "key_sheet_scrollUp"));
        themeDescriptions.add(new ThemeDescription(this.containerView, 0, (Class[]) null, (Paint) null, new Drawable[]{this.shadowDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogBackground"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogBackground"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogScrollGlow"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlack"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlack"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBTITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_actionBarSubtitle"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlack"));
        themeDescriptions.add(new ThemeDescription(this.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlack"));
        themeDescriptions.add(new ThemeDescription(this.actionBarShadow, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogShadowLine"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{View.class}, (String[]) null, (Paint[]) null, (Drawable[]) null, delegate, "dialogBackground"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{View.class}, (String[]) null, (Paint[]) null, (Drawable[]) null, delegate, "dialogBackgroundGray"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_SECTIONS, new Class[]{SectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "key_graySectionText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_SECTIONS, new Class[]{SectionCell.class}, new String[]{"middleTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "key_graySectionText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_SECTIONS, new Class[]{SectionCell.class}, new String[]{"righTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "key_graySectionText"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR | ThemeDescription.FLAG_SECTIONS, new Class[]{SectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "graySection"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlack"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText4"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackChecked"));
        return themeDescriptions;
    }
}
