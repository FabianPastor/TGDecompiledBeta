package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
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
import androidx.annotation.Keep;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$FileLocation;
import org.telegram.tgnet.TLRPC$InputPeer;
import org.telegram.tgnet.TLRPC$MessageUserVote;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_inputPeerChannel;
import org.telegram.tgnet.TLRPC$TL_inputPeerChat;
import org.telegram.tgnet.TLRPC$TL_inputPeerUser;
import org.telegram.tgnet.TLRPC$TL_messageMediaPoll;
import org.telegram.tgnet.TLRPC$TL_messageUserVoteInputOption;
import org.telegram.tgnet.TLRPC$TL_messages_getPollVotes;
import org.telegram.tgnet.TLRPC$TL_messages_votesList;
import org.telegram.tgnet.TLRPC$TL_poll;
import org.telegram.tgnet.TLRPC$TL_pollAnswer;
import org.telegram.tgnet.TLRPC$TL_pollAnswerVoters;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AnimationProperties;
import org.telegram.ui.Components.PollVotesAlert;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.ProfileActivity;

public class PollVotesAlert extends BottomSheet {
    public static final Property<UserCell, Float> USER_CELL_PROPERTY = new AnimationProperties.FloatProperty<UserCell>("placeholderAlpha(") {
        public void setValue(UserCell userCell, float f) {
            userCell.setPlaceholderAlpha(f);
        }

        public Float get(UserCell userCell) {
            return Float.valueOf(userCell.getPlaceholderAlpha());
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
    private TLRPC$InputPeer peer;
    /* access modifiers changed from: private */
    public LinearGradient placeholderGradient;
    /* access modifiers changed from: private */
    public Matrix placeholderMatrix;
    /* access modifiers changed from: private */
    public Paint placeholderPaint = new Paint(1);
    /* access modifiers changed from: private */
    public TLRPC$TL_poll poll;
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
    /* access modifiers changed from: private */
    public float totalTranslation;
    /* access modifiers changed from: private */
    public ArrayList<VotesList> voters = new ArrayList<>();
    /* access modifiers changed from: private */
    public HashMap<VotesList, Button> votesPercents = new HashMap<>();

    /* access modifiers changed from: protected */
    public boolean canDismissWithSwipe() {
        return false;
    }

    private static class VotesList {
        public boolean collapsed;
        public int collapsedCount = 10;
        public int count;
        public String next_offset;
        public byte[] option;
        public ArrayList<TLRPC$User> users;
        public ArrayList<TLRPC$MessageUserVote> votes;

        public VotesList(TLRPC$TL_messages_votesList tLRPC$TL_messages_votesList, byte[] bArr) {
            this.count = tLRPC$TL_messages_votesList.count;
            this.votes = tLRPC$TL_messages_votesList.votes;
            this.users = tLRPC$TL_messages_votesList.users;
            this.next_offset = tLRPC$TL_messages_votesList.next_offset;
            this.option = bArr;
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
            return this.collapsed ? 1 : 2;
        }
    }

    public class SectionCell extends FrameLayout {
        private TextView middleTextView;
        private TextView righTextView;
        private TextView textView;
        final /* synthetic */ PollVotesAlert this$0;

        /* access modifiers changed from: protected */
        public void onCollapseClick() {
        }

        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        public SectionCell(PollVotesAlert pollVotesAlert, Context context) {
            super(context);
            PollVotesAlert pollVotesAlert2 = pollVotesAlert;
            this.this$0 = pollVotesAlert2;
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
            AnonymousClass1 r2 = new TextView(getContext(), pollVotesAlert2) {
                public boolean post(Runnable runnable) {
                    return SectionCell.this.this$0.containerView.post(runnable);
                }

                public boolean postDelayed(Runnable runnable, long j) {
                    return SectionCell.this.this$0.containerView.postDelayed(runnable, j);
                }
            };
            this.righTextView = r2;
            r2.setTextSize(1, 14.0f);
            this.righTextView.setTextColor(Theme.getColor("key_graySectionText"));
            this.righTextView.setGravity((LocaleController.isRTL ? 3 : 5) | 16);
            this.righTextView.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PollVotesAlert.SectionCell.this.lambda$new$0$PollVotesAlert$SectionCell(view);
                }
            });
            addView(this.textView, LayoutHelper.createFrame(-2, -1.0f, (LocaleController.isRTL ? 5 : 3) | 48, (float) (LocaleController.isRTL ? 0 : 16), 0.0f, (float) (!LocaleController.isRTL ? 0 : i2), 0.0f));
            addView(this.middleTextView, LayoutHelper.createFrame(-2, -1.0f, (LocaleController.isRTL ? 5 : 3) | 48, 0.0f, 0.0f, 0.0f, 0.0f));
            addView(this.righTextView, LayoutHelper.createFrame(-2, -1.0f, (LocaleController.isRTL ? 3 : i) | 48, 16.0f, 0.0f, 16.0f, 0.0f));
        }

        public /* synthetic */ void lambda$new$0$PollVotesAlert$SectionCell(View view) {
            onCollapseClick();
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            int i3 = i;
            int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(32.0f), NUM);
            measureChildWithMargins(this.middleTextView, i3, 0, makeMeasureSpec, 0);
            measureChildWithMargins(this.righTextView, i3, 0, makeMeasureSpec, 0);
            measureChildWithMargins(this.textView, i3, this.middleTextView.getMeasuredWidth() + this.righTextView.getMeasuredWidth() + AndroidUtilities.dp(32.0f), makeMeasureSpec, 0);
            setMeasuredDimension(View.MeasureSpec.getSize(i), AndroidUtilities.dp(32.0f));
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
            if (LocaleController.isRTL) {
                int left = this.textView.getLeft() - this.middleTextView.getMeasuredWidth();
                TextView textView2 = this.middleTextView;
                textView2.layout(left, textView2.getTop(), this.middleTextView.getMeasuredWidth() + left, this.middleTextView.getBottom());
                return;
            }
            int right = this.textView.getRight();
            TextView textView3 = this.middleTextView;
            textView3.layout(right, textView3.getTop(), this.middleTextView.getMeasuredWidth() + right, this.middleTextView.getBottom());
        }

        public void setText(String str, int i, int i2, int i3) {
            SpannableStringBuilder spannableStringBuilder;
            TextView textView2 = this.textView;
            textView2.setText(Emoji.replaceEmoji(str, textView2.getPaint().getFontMetricsInt(), AndroidUtilities.dp(14.0f), false));
            String format = String.format("%d", new Object[]{Integer.valueOf(i)});
            if (LocaleController.isRTL) {
                spannableStringBuilder = new SpannableStringBuilder(String.format("%s%% – ", new Object[]{Integer.valueOf(i)}));
            } else {
                spannableStringBuilder = new SpannableStringBuilder(String.format(" – %s%%", new Object[]{Integer.valueOf(i)}));
            }
            spannableStringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf")), 3, format.length() + 3, 33);
            this.middleTextView.setText(spannableStringBuilder);
            if (i3 == 0) {
                if (this.this$0.poll.quiz) {
                    this.righTextView.setText(LocaleController.formatPluralString("Answer", i2));
                } else {
                    this.righTextView.setText(LocaleController.formatPluralString("Vote", i2));
                }
            } else if (i3 == 1) {
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
        /* access modifiers changed from: private */
        public TLRPC$User currentUser;
        private boolean drawPlaceholder;
        private TLRPC$FileLocation lastAvatar;
        private String lastName;
        private int lastStatus;
        private SimpleTextView nameTextView;
        private boolean needDivider;
        private float placeholderAlpha = 1.0f;
        private int placeholderNum;

        public boolean hasOverlappingRendering() {
            return false;
        }

        public UserCell(Context context) {
            super(context);
            int i = UserConfig.selectedAccount;
            setWillNotDraw(false);
            this.avatarDrawable = new AvatarDrawable();
            BackupImageView backupImageView = new BackupImageView(context);
            this.avatarImageView = backupImageView;
            backupImageView.setRoundRadius(AndroidUtilities.dp(18.0f));
            int i2 = 5;
            addView(this.avatarImageView, LayoutHelper.createFrame(36, 36.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 14.0f, 6.0f, LocaleController.isRTL ? 14.0f : 0.0f, 0.0f));
            SimpleTextView simpleTextView = new SimpleTextView(context);
            this.nameTextView = simpleTextView;
            simpleTextView.setTextColor(Theme.getColor("dialogTextBlack"));
            this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.nameTextView.setTextSize(16);
            this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            addView(this.nameTextView, LayoutHelper.createFrame(-1, 20.0f, (!LocaleController.isRTL ? 3 : i2) | 48, LocaleController.isRTL ? 28.0f : 65.0f, 14.0f, LocaleController.isRTL ? 65.0f : 28.0f, 0.0f));
        }

        public void setData(TLRPC$User tLRPC$User, int i, boolean z) {
            this.currentUser = tLRPC$User;
            this.needDivider = z;
            this.drawPlaceholder = tLRPC$User == null;
            this.placeholderNum = i;
            if (tLRPC$User == null) {
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

        @Keep
        public void setPlaceholderAlpha(float f) {
            this.placeholderAlpha = f;
            invalidate();
        }

        @Keep
        public float getPlaceholderAlpha() {
            return this.placeholderAlpha;
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f) + (this.needDivider ? 1 : 0), NUM));
        }

        /* JADX WARNING: Code restructure failed: missing block: B:2:0x0005, code lost:
            r0 = r0.photo;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:46:0x0066, code lost:
            if (r1.equals(r10.lastName) == false) goto L_0x006a;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void update(int r11) {
            /*
                r10 = this;
                org.telegram.tgnet.TLRPC$User r0 = r10.currentUser
                r1 = 0
                if (r0 == 0) goto L_0x000c
                org.telegram.tgnet.TLRPC$UserProfilePhoto r0 = r0.photo
                if (r0 == 0) goto L_0x000c
                org.telegram.tgnet.TLRPC$FileLocation r0 = r0.photo_small
                goto L_0x000d
            L_0x000c:
                r0 = r1
            L_0x000d:
                r2 = 0
                if (r11 == 0) goto L_0x006d
                r3 = r11 & 2
                r4 = 1
                if (r3 == 0) goto L_0x0037
                org.telegram.tgnet.TLRPC$FileLocation r3 = r10.lastAvatar
                if (r3 == 0) goto L_0x001b
                if (r0 == 0) goto L_0x0035
            L_0x001b:
                org.telegram.tgnet.TLRPC$FileLocation r3 = r10.lastAvatar
                if (r3 != 0) goto L_0x0021
                if (r0 != 0) goto L_0x0035
            L_0x0021:
                org.telegram.tgnet.TLRPC$FileLocation r3 = r10.lastAvatar
                if (r3 == 0) goto L_0x0037
                if (r0 == 0) goto L_0x0037
                long r5 = r3.volume_id
                long r7 = r0.volume_id
                int r9 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
                if (r9 != 0) goto L_0x0035
                int r3 = r3.local_id
                int r5 = r0.local_id
                if (r3 == r5) goto L_0x0037
            L_0x0035:
                r3 = 1
                goto L_0x0038
            L_0x0037:
                r3 = 0
            L_0x0038:
                org.telegram.tgnet.TLRPC$User r5 = r10.currentUser
                if (r5 == 0) goto L_0x004f
                if (r3 != 0) goto L_0x004f
                r6 = r11 & 4
                if (r6 == 0) goto L_0x004f
                org.telegram.tgnet.TLRPC$UserStatus r5 = r5.status
                if (r5 == 0) goto L_0x0049
                int r5 = r5.expires
                goto L_0x004a
            L_0x0049:
                r5 = 0
            L_0x004a:
                int r6 = r10.lastStatus
                if (r5 == r6) goto L_0x004f
                r3 = 1
            L_0x004f:
                if (r3 != 0) goto L_0x0069
                java.lang.String r5 = r10.lastName
                if (r5 == 0) goto L_0x0069
                r11 = r11 & r4
                if (r11 == 0) goto L_0x0069
                org.telegram.tgnet.TLRPC$User r11 = r10.currentUser
                if (r11 == 0) goto L_0x0060
                java.lang.String r1 = org.telegram.messenger.UserObject.getUserName(r11)
            L_0x0060:
                java.lang.String r11 = r10.lastName
                boolean r11 = r1.equals(r11)
                if (r11 != 0) goto L_0x0069
                goto L_0x006a
            L_0x0069:
                r4 = r3
            L_0x006a:
                if (r4 != 0) goto L_0x006d
                return
            L_0x006d:
                org.telegram.ui.Components.AvatarDrawable r11 = r10.avatarDrawable
                org.telegram.tgnet.TLRPC$User r3 = r10.currentUser
                r11.setInfo((org.telegram.tgnet.TLRPC$User) r3)
                org.telegram.tgnet.TLRPC$User r11 = r10.currentUser
                org.telegram.tgnet.TLRPC$UserStatus r11 = r11.status
                if (r11 == 0) goto L_0x007f
                int r11 = r11.expires
                r10.lastStatus = r11
                goto L_0x0081
            L_0x007f:
                r10.lastStatus = r2
            L_0x0081:
                org.telegram.tgnet.TLRPC$User r11 = r10.currentUser
                if (r11 == 0) goto L_0x008e
                if (r1 != 0) goto L_0x008b
                java.lang.String r1 = org.telegram.messenger.UserObject.getUserName(r11)
            L_0x008b:
                r10.lastName = r1
                goto L_0x0092
            L_0x008e:
                java.lang.String r11 = ""
                r10.lastName = r11
            L_0x0092:
                org.telegram.ui.ActionBar.SimpleTextView r11 = r10.nameTextView
                java.lang.String r1 = r10.lastName
                r11.setText(r1)
                r10.lastAvatar = r0
                org.telegram.tgnet.TLRPC$User r11 = r10.currentUser
                if (r11 == 0) goto L_0x00af
                org.telegram.ui.Components.BackupImageView r0 = r10.avatarImageView
                org.telegram.messenger.ImageLocation r11 = org.telegram.messenger.ImageLocation.getForUser(r11, r2)
                org.telegram.ui.Components.AvatarDrawable r1 = r10.avatarDrawable
                org.telegram.tgnet.TLRPC$User r2 = r10.currentUser
                java.lang.String r3 = "50_50"
                r0.setImage((org.telegram.messenger.ImageLocation) r11, (java.lang.String) r3, (android.graphics.drawable.Drawable) r1, (java.lang.Object) r2)
                goto L_0x00b6
            L_0x00af:
                org.telegram.ui.Components.BackupImageView r11 = r10.avatarImageView
                org.telegram.ui.Components.AvatarDrawable r0 = r10.avatarDrawable
                r11.setImageDrawable(r0)
            L_0x00b6:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.PollVotesAlert.UserCell.update(int):void");
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            int i;
            int i2;
            int i3;
            int i4;
            if (this.drawPlaceholder || this.placeholderAlpha != 0.0f) {
                PollVotesAlert.this.placeholderPaint.setAlpha((int) (this.placeholderAlpha * 255.0f));
                int left = this.avatarImageView.getLeft() + (this.avatarImageView.getMeasuredWidth() / 2);
                int top = this.avatarImageView.getTop() + (this.avatarImageView.getMeasuredHeight() / 2);
                canvas.drawCircle((float) left, (float) top, (float) (this.avatarImageView.getMeasuredWidth() / 2), PollVotesAlert.this.placeholderPaint);
                if (this.placeholderNum % 2 == 0) {
                    i2 = AndroidUtilities.dp(65.0f);
                    i = AndroidUtilities.dp(48.0f);
                } else {
                    i2 = AndroidUtilities.dp(65.0f);
                    i = AndroidUtilities.dp(60.0f);
                }
                if (LocaleController.isRTL) {
                    i2 = (getMeasuredWidth() - i2) - i;
                }
                PollVotesAlert.this.rect.set((float) i2, (float) (top - AndroidUtilities.dp(4.0f)), (float) (i2 + i), (float) (AndroidUtilities.dp(4.0f) + top));
                canvas.drawRoundRect(PollVotesAlert.this.rect, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), PollVotesAlert.this.placeholderPaint);
                if (this.placeholderNum % 2 == 0) {
                    i4 = AndroidUtilities.dp(119.0f);
                    i3 = AndroidUtilities.dp(60.0f);
                } else {
                    i4 = AndroidUtilities.dp(131.0f);
                    i3 = AndroidUtilities.dp(80.0f);
                }
                if (LocaleController.isRTL) {
                    i4 = (getMeasuredWidth() - i4) - i3;
                }
                PollVotesAlert.this.rect.set((float) i4, (float) (top - AndroidUtilities.dp(4.0f)), (float) (i4 + i3), (float) (top + AndroidUtilities.dp(4.0f)));
                canvas.drawRoundRect(PollVotesAlert.this.rect, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), PollVotesAlert.this.placeholderPaint);
            }
            if (this.needDivider) {
                canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(64.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(64.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
            }
        }
    }

    public static void showForPoll(ChatActivity chatActivity2, MessageObject messageObject2) {
        if (chatActivity2 != null && chatActivity2.getParentActivity() != null) {
            chatActivity2.showDialog(new PollVotesAlert(chatActivity2, messageObject2));
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
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public PollVotesAlert(ChatActivity chatActivity2, MessageObject messageObject2) {
        super(chatActivity2.getParentActivity(), true);
        int i;
        int i2;
        ChatActivity chatActivity3 = chatActivity2;
        MessageObject messageObject3 = messageObject2;
        int i3 = 1;
        this.messageObject = messageObject3;
        this.chatActivity = chatActivity3;
        TLRPC$TL_messageMediaPoll tLRPC$TL_messageMediaPoll = (TLRPC$TL_messageMediaPoll) messageObject3.messageOwner.media;
        this.poll = tLRPC$TL_messageMediaPoll.poll;
        Activity parentActivity = chatActivity2.getParentActivity();
        TLRPC$Chat currentChat = chatActivity2.getCurrentChat();
        TLRPC$User currentUser = chatActivity2.getCurrentUser();
        if (ChatObject.isChannel(currentChat)) {
            TLRPC$TL_inputPeerChannel tLRPC$TL_inputPeerChannel = new TLRPC$TL_inputPeerChannel();
            this.peer = tLRPC$TL_inputPeerChannel;
            tLRPC$TL_inputPeerChannel.channel_id = currentChat.id;
            tLRPC$TL_inputPeerChannel.access_hash = currentChat.access_hash;
        } else if (currentChat != null) {
            TLRPC$TL_inputPeerChat tLRPC$TL_inputPeerChat = new TLRPC$TL_inputPeerChat();
            this.peer = tLRPC$TL_inputPeerChat;
            tLRPC$TL_inputPeerChat.chat_id = currentChat.id;
        } else {
            TLRPC$TL_inputPeerUser tLRPC$TL_inputPeerUser = new TLRPC$TL_inputPeerUser();
            this.peer = tLRPC$TL_inputPeerUser;
            tLRPC$TL_inputPeerUser.user_id = currentUser.id;
            tLRPC$TL_inputPeerUser.access_hash = currentUser.access_hash;
        }
        ArrayList arrayList = new ArrayList();
        int size = tLRPC$TL_messageMediaPoll.results.results.size();
        Integer[] numArr = new Integer[size];
        int i4 = 0;
        while (i4 < size) {
            TLRPC$TL_pollAnswerVoters tLRPC$TL_pollAnswerVoters = tLRPC$TL_messageMediaPoll.results.results.get(i4);
            if (tLRPC$TL_pollAnswerVoters.voters == 0) {
                i2 = i4;
                i = size;
            } else {
                TLRPC$TL_messages_votesList tLRPC$TL_messages_votesList = new TLRPC$TL_messages_votesList();
                int i5 = tLRPC$TL_pollAnswerVoters.voters;
                i5 = i5 > 15 ? 10 : i5;
                for (int i6 = 0; i6 < i5; i6++) {
                    tLRPC$TL_messages_votesList.votes.add(new TLRPC$TL_messageUserVoteInputOption());
                }
                tLRPC$TL_messages_votesList.next_offset = i5 < tLRPC$TL_pollAnswerVoters.voters ? "empty" : null;
                tLRPC$TL_messages_votesList.count = tLRPC$TL_pollAnswerVoters.voters;
                this.voters.add(new VotesList(tLRPC$TL_messages_votesList, tLRPC$TL_pollAnswerVoters.option));
                TLRPC$TL_messages_getPollVotes tLRPC$TL_messages_getPollVotes = new TLRPC$TL_messages_getPollVotes();
                tLRPC$TL_messages_getPollVotes.peer = this.peer;
                tLRPC$TL_messages_getPollVotes.id = this.messageObject.getId();
                tLRPC$TL_messages_getPollVotes.limit = tLRPC$TL_pollAnswerVoters.voters <= 15 ? 15 : 10;
                tLRPC$TL_messages_getPollVotes.flags |= i3;
                tLRPC$TL_messages_getPollVotes.option = tLRPC$TL_pollAnswerVoters.option;
                $$Lambda$PollVotesAlert$lpnnrQFn8IKhhS43T4ZDEfqstlU r9 = r0;
                i = size;
                ConnectionsManager connectionsManager = chatActivity2.getConnectionsManager();
                i2 = i4;
                $$Lambda$PollVotesAlert$lpnnrQFn8IKhhS43T4ZDEfqstlU r0 = new RequestDelegate(numArr, i4, chatActivity2, arrayList, tLRPC$TL_pollAnswerVoters) {
                    private final /* synthetic */ Integer[] f$1;
                    private final /* synthetic */ int f$2;
                    private final /* synthetic */ ChatActivity f$3;
                    private final /* synthetic */ ArrayList f$4;
                    private final /* synthetic */ TLRPC$TL_pollAnswerVoters f$5;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                        this.f$4 = r5;
                        this.f$5 = r6;
                    }

                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        PollVotesAlert.this.lambda$new$1$PollVotesAlert(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, tLObject, tLRPC$TL_error);
                    }
                };
                numArr[i2] = Integer.valueOf(connectionsManager.sendRequest(tLRPC$TL_messages_getPollVotes, r9));
                this.queries.add(numArr[i2]);
            }
            i4 = i2 + 1;
            size = i;
            i3 = 1;
        }
        updateButtons();
        Collections.sort(this.voters, new Comparator<VotesList>() {
            private int getIndex(VotesList votesList) {
                int size = PollVotesAlert.this.poll.answers.size();
                for (int i = 0; i < size; i++) {
                    if (Arrays.equals(PollVotesAlert.this.poll.answers.get(i).option, votesList.option)) {
                        return i;
                    }
                }
                return 0;
            }

            public int compare(VotesList votesList, VotesList votesList2) {
                int index = getIndex(votesList);
                int index2 = getIndex(votesList2);
                if (index > index2) {
                    return 1;
                }
                return index < index2 ? -1 : 0;
            }
        });
        updatePlaceholder();
        Drawable mutate = parentActivity.getResources().getDrawable(NUM).mutate();
        this.shadowDrawable = mutate;
        mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogBackground"), PorterDuff.Mode.MULTIPLY));
        AnonymousClass3 r02 = new FrameLayout(parentActivity) {
            private boolean ignoreLayout = false;
            private RectF rect = new RectF();

            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                int i3;
                int size = View.MeasureSpec.getSize(i2);
                if (Build.VERSION.SDK_INT >= 21 && !PollVotesAlert.this.isFullscreen) {
                    this.ignoreLayout = true;
                    setPadding(PollVotesAlert.this.backgroundPaddingLeft, AndroidUtilities.statusBarHeight, PollVotesAlert.this.backgroundPaddingLeft, 0);
                    this.ignoreLayout = false;
                }
                int paddingTop = size - getPaddingTop();
                ((FrameLayout.LayoutParams) PollVotesAlert.this.listView.getLayoutParams()).topMargin = ActionBar.getCurrentActionBarHeight();
                ((FrameLayout.LayoutParams) PollVotesAlert.this.actionBarShadow.getLayoutParams()).topMargin = ActionBar.getCurrentActionBarHeight();
                int access$1000 = PollVotesAlert.this.backgroundPaddingTop + AndroidUtilities.dp(15.0f) + AndroidUtilities.statusBarHeight;
                int sectionCount = PollVotesAlert.this.listAdapter.getSectionCount();
                for (int i4 = 0; i4 < sectionCount; i4++) {
                    if (i4 == 0) {
                        PollVotesAlert.this.titleTextView.measure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i - (PollVotesAlert.this.backgroundPaddingLeft * 2)), NUM), i2);
                        access$1000 += PollVotesAlert.this.titleTextView.getMeasuredHeight();
                    } else {
                        access$1000 += AndroidUtilities.dp(32.0f) + (AndroidUtilities.dp(50.0f) * (PollVotesAlert.this.listAdapter.getCountForSection(i4) - 1));
                    }
                }
                if (access$1000 < paddingTop) {
                    i3 = paddingTop - access$1000;
                } else {
                    i3 = paddingTop - ((paddingTop / 5) * 3);
                }
                int dp = i3 + AndroidUtilities.dp(8.0f);
                if (PollVotesAlert.this.listView.getPaddingTop() != dp) {
                    this.ignoreLayout = true;
                    PollVotesAlert.this.listView.setPinnedSectionOffsetY(-dp);
                    PollVotesAlert.this.listView.setPadding(0, dp, 0, 0);
                    this.ignoreLayout = false;
                }
                super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(size, NUM));
            }

            /* access modifiers changed from: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                super.onLayout(z, i, i2, i3, i4);
                PollVotesAlert.this.updateLayout(false);
            }

            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getAction() != 0 || PollVotesAlert.this.scrollOffsetY == 0 || motionEvent.getY() >= ((float) (PollVotesAlert.this.scrollOffsetY + AndroidUtilities.dp(12.0f))) || PollVotesAlert.this.actionBar.getAlpha() != 0.0f) {
                    return super.onInterceptTouchEvent(motionEvent);
                }
                PollVotesAlert.this.dismiss();
                return true;
            }

            public boolean onTouchEvent(MotionEvent motionEvent) {
                return !PollVotesAlert.this.isDismissed() && super.onTouchEvent(motionEvent);
            }

            public void requestLayout() {
                if (!this.ignoreLayout) {
                    super.requestLayout();
                }
            }

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                float f;
                int dp = AndroidUtilities.dp(13.0f);
                int access$1500 = (PollVotesAlert.this.scrollOffsetY - PollVotesAlert.this.backgroundPaddingTop) - dp;
                if (PollVotesAlert.this.currentSheetAnimationType == 1) {
                    access$1500 = (int) (((float) access$1500) + PollVotesAlert.this.listView.getTranslationY());
                }
                int dp2 = AndroidUtilities.dp(20.0f) + access$1500;
                int measuredHeight = getMeasuredHeight() + AndroidUtilities.dp(15.0f) + PollVotesAlert.this.backgroundPaddingTop;
                if (PollVotesAlert.this.backgroundPaddingTop + access$1500 < ActionBar.getCurrentActionBarHeight()) {
                    float dp3 = (float) (dp + AndroidUtilities.dp(4.0f));
                    float min = Math.min(1.0f, ((float) ((ActionBar.getCurrentActionBarHeight() - access$1500) - PollVotesAlert.this.backgroundPaddingTop)) / dp3);
                    int currentActionBarHeight = (int) ((((float) ActionBar.getCurrentActionBarHeight()) - dp3) * min);
                    access$1500 -= currentActionBarHeight;
                    dp2 -= currentActionBarHeight;
                    measuredHeight += currentActionBarHeight;
                    f = 1.0f - min;
                } else {
                    f = 1.0f;
                }
                if (Build.VERSION.SDK_INT >= 21) {
                    int i = AndroidUtilities.statusBarHeight;
                    access$1500 += i;
                    dp2 += i;
                }
                PollVotesAlert.this.shadowDrawable.setBounds(0, access$1500, getMeasuredWidth(), measuredHeight);
                PollVotesAlert.this.shadowDrawable.draw(canvas);
                if (f != 1.0f) {
                    Theme.dialogs_onlineCirclePaint.setColor(Theme.getColor("dialogBackground"));
                    this.rect.set((float) PollVotesAlert.this.backgroundPaddingLeft, (float) (PollVotesAlert.this.backgroundPaddingTop + access$1500), (float) (getMeasuredWidth() - PollVotesAlert.this.backgroundPaddingLeft), (float) (PollVotesAlert.this.backgroundPaddingTop + access$1500 + AndroidUtilities.dp(24.0f)));
                    canvas.drawRoundRect(this.rect, ((float) AndroidUtilities.dp(12.0f)) * f, ((float) AndroidUtilities.dp(12.0f)) * f, Theme.dialogs_onlineCirclePaint);
                }
                if (f != 0.0f) {
                    int dp4 = AndroidUtilities.dp(36.0f);
                    this.rect.set((float) ((getMeasuredWidth() - dp4) / 2), (float) dp2, (float) ((getMeasuredWidth() + dp4) / 2), (float) (dp2 + AndroidUtilities.dp(4.0f)));
                    int color = Theme.getColor("key_sheet_scrollUp");
                    int alpha = Color.alpha(color);
                    Theme.dialogs_onlineCirclePaint.setColor(color);
                    Theme.dialogs_onlineCirclePaint.setAlpha((int) (((float) alpha) * 1.0f * f));
                    canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(2.0f), Theme.dialogs_onlineCirclePaint);
                }
                int color2 = Theme.getColor("dialogBackground");
                Theme.dialogs_onlineCirclePaint.setColor(Color.argb((int) (PollVotesAlert.this.actionBar.getAlpha() * 255.0f), (int) (((float) Color.red(color2)) * 0.8f), (int) (((float) Color.green(color2)) * 0.8f), (int) (((float) Color.blue(color2)) * 0.8f)));
                canvas.drawRect((float) PollVotesAlert.this.backgroundPaddingLeft, 0.0f, (float) (getMeasuredWidth() - PollVotesAlert.this.backgroundPaddingLeft), (float) AndroidUtilities.statusBarHeight, Theme.dialogs_onlineCirclePaint);
            }
        };
        this.containerView = r02;
        r02.setWillNotDraw(false);
        ViewGroup viewGroup = this.containerView;
        int i7 = this.backgroundPaddingLeft;
        viewGroup.setPadding(i7, 0, i7, 0);
        AnonymousClass4 r03 = new RecyclerListView(parentActivity) {
            long lastUpdateTime;

            /* access modifiers changed from: protected */
            public boolean allowSelectChildAtPosition(float f, float f2) {
                return f2 >= ((float) (PollVotesAlert.this.scrollOffsetY + (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0)));
            }

            /* access modifiers changed from: protected */
            public void dispatchDraw(Canvas canvas) {
                if (PollVotesAlert.this.loadingResults) {
                    long elapsedRealtime = SystemClock.elapsedRealtime();
                    long abs = Math.abs(this.lastUpdateTime - elapsedRealtime);
                    if (abs > 17) {
                        abs = 16;
                    }
                    this.lastUpdateTime = elapsedRealtime;
                    PollVotesAlert pollVotesAlert = PollVotesAlert.this;
                    float unused = pollVotesAlert.totalTranslation = pollVotesAlert.totalTranslation + ((((float) abs) * PollVotesAlert.this.gradientWidth) / 1800.0f);
                    while (PollVotesAlert.this.totalTranslation >= PollVotesAlert.this.gradientWidth * 2.0f) {
                        PollVotesAlert pollVotesAlert2 = PollVotesAlert.this;
                        float unused2 = pollVotesAlert2.totalTranslation = pollVotesAlert2.totalTranslation - (PollVotesAlert.this.gradientWidth * 2.0f);
                    }
                    PollVotesAlert.this.placeholderMatrix.setTranslate(PollVotesAlert.this.totalTranslation, 0.0f);
                    PollVotesAlert.this.placeholderGradient.setLocalMatrix(PollVotesAlert.this.placeholderMatrix);
                    invalidateViews();
                    invalidate();
                }
                super.dispatchDraw(canvas);
            }
        };
        this.listView = r03;
        r03.setClipToPadding(false);
        this.listView.setLayoutManager(new LinearLayoutManager(getContext(), 1, false));
        this.listView.setHorizontalScrollBarEnabled(false);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setSectionsType(2);
        this.containerView.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        RecyclerListView recyclerListView = this.listView;
        Adapter adapter = new Adapter(parentActivity);
        this.listAdapter = adapter;
        recyclerListView.setAdapter(adapter);
        this.listView.setGlowColor(Theme.getColor("dialogScrollGlow"));
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener(chatActivity3) {
            private final /* synthetic */ ChatActivity f$1;

            {
                this.f$1 = r2;
            }

            public final void onItemClick(View view, int i) {
                PollVotesAlert.this.lambda$new$4$PollVotesAlert(this.f$1, view, i);
            }
        });
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                if (PollVotesAlert.this.listView.getChildCount() > 0) {
                    PollVotesAlert.this.updateLayout(true);
                }
            }

            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                if (i == 0) {
                    if (((PollVotesAlert.this.scrollOffsetY - PollVotesAlert.this.backgroundPaddingTop) - AndroidUtilities.dp(13.0f)) + PollVotesAlert.this.backgroundPaddingTop < ActionBar.getCurrentActionBarHeight() && PollVotesAlert.this.listView.canScrollVertically(1)) {
                        PollVotesAlert.this.listView.getChildAt(0);
                        RecyclerListView.Holder holder = (RecyclerListView.Holder) PollVotesAlert.this.listView.findViewHolderForAdapterPosition(0);
                        if (holder != null && holder.itemView.getTop() > AndroidUtilities.dp(7.0f)) {
                            PollVotesAlert.this.listView.smoothScrollBy(0, holder.itemView.getTop() - AndroidUtilities.dp(7.0f));
                        }
                    }
                }
            }
        });
        TextView textView = new TextView(parentActivity);
        this.titleTextView = textView;
        textView.setTextSize(1, 18.0f);
        this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.titleTextView.setPadding(AndroidUtilities.dp(21.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(14.0f), AndroidUtilities.dp(21.0f));
        this.titleTextView.setTextColor(Theme.getColor("dialogTextBlack"));
        this.titleTextView.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
        TextView textView2 = this.titleTextView;
        textView2.setText(Emoji.replaceEmoji(this.poll.question, textView2.getPaint().getFontMetricsInt(), AndroidUtilities.dp(18.0f), false));
        AnonymousClass6 r04 = new ActionBar(parentActivity) {
            public void setAlpha(float f) {
                super.setAlpha(f);
                PollVotesAlert.this.containerView.invalidate();
            }
        };
        this.actionBar = r04;
        r04.setBackgroundColor(Theme.getColor("dialogBackground"));
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setItemsColor(Theme.getColor("dialogTextBlack"), false);
        this.actionBar.setItemsBackgroundColor(Theme.getColor("dialogButtonSelector"), false);
        this.actionBar.setTitleColor(Theme.getColor("dialogTextBlack"));
        this.actionBar.setSubtitleColor(Theme.getColor("player_actionBarSubtitle"));
        this.actionBar.setOccupyStatusBar(false);
        this.actionBar.setAlpha(0.0f);
        this.actionBar.setTitle(LocaleController.getString("PollResults", NUM));
        if (this.poll.quiz) {
            this.actionBar.setSubtitle(LocaleController.formatPluralString("Answer", tLRPC$TL_messageMediaPoll.results.total_voters));
        } else {
            this.actionBar.setSubtitle(LocaleController.formatPluralString("Vote", tLRPC$TL_messageMediaPoll.results.total_voters));
        }
        this.containerView.addView(this.actionBar, LayoutHelper.createFrame(-1, -2.0f));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    PollVotesAlert.this.dismiss();
                }
            }
        });
        View view = new View(parentActivity);
        this.actionBarShadow = view;
        view.setAlpha(0.0f);
        this.actionBarShadow.setBackgroundColor(Theme.getColor("dialogShadowLine"));
        this.containerView.addView(this.actionBarShadow, LayoutHelper.createFrame(-1, 1.0f));
    }

    public /* synthetic */ void lambda$new$1$PollVotesAlert(Integer[] numArr, int i, ChatActivity chatActivity2, ArrayList arrayList, TLRPC$TL_pollAnswerVoters tLRPC$TL_pollAnswerVoters, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(numArr, i, tLObject, chatActivity2, arrayList, tLRPC$TL_pollAnswerVoters) {
            private final /* synthetic */ Integer[] f$1;
            private final /* synthetic */ int f$2;
            private final /* synthetic */ TLObject f$3;
            private final /* synthetic */ ChatActivity f$4;
            private final /* synthetic */ ArrayList f$5;
            private final /* synthetic */ TLRPC$TL_pollAnswerVoters f$6;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
                this.f$6 = r7;
            }

            public final void run() {
                PollVotesAlert.this.lambda$null$0$PollVotesAlert(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
            }
        });
    }

    public /* synthetic */ void lambda$null$0$PollVotesAlert(Integer[] numArr, int i, TLObject tLObject, ChatActivity chatActivity2, ArrayList arrayList, TLRPC$TL_pollAnswerVoters tLRPC$TL_pollAnswerVoters) {
        RecyclerView.ViewHolder findContainingViewHolder;
        this.queries.remove(numArr[i]);
        if (tLObject != null) {
            TLRPC$TL_messages_votesList tLRPC$TL_messages_votesList = (TLRPC$TL_messages_votesList) tLObject;
            chatActivity2.getMessagesController().putUsers(tLRPC$TL_messages_votesList.users, false);
            if (!tLRPC$TL_messages_votesList.votes.isEmpty()) {
                arrayList.add(new VotesList(tLRPC$TL_messages_votesList, tLRPC$TL_pollAnswerVoters.option));
            }
            if (this.queries.isEmpty()) {
                int size = arrayList.size();
                boolean z = false;
                for (int i2 = 0; i2 < size; i2++) {
                    VotesList votesList = (VotesList) arrayList.get(i2);
                    int size2 = this.voters.size();
                    int i3 = 0;
                    while (true) {
                        if (i3 >= size2) {
                            break;
                        }
                        VotesList votesList2 = this.voters.get(i3);
                        if (Arrays.equals(votesList.option, votesList2.option)) {
                            votesList2.next_offset = votesList.next_offset;
                            if (!(votesList2.count == votesList.count && votesList2.votes.size() == votesList.votes.size())) {
                                z = true;
                            }
                            votesList2.count = votesList.count;
                            votesList2.users = votesList.users;
                            votesList2.votes = votesList.votes;
                        } else {
                            i3++;
                        }
                    }
                }
                this.loadingResults = false;
                RecyclerListView recyclerListView = this.listView;
                if (recyclerListView == null) {
                    return;
                }
                if (this.currentSheetAnimationType == 0 && this.startAnimationRunnable == null && !z) {
                    int childCount = recyclerListView.getChildCount();
                    ArrayList arrayList2 = new ArrayList();
                    for (int i4 = 0; i4 < childCount; i4++) {
                        View childAt = this.listView.getChildAt(i4);
                        if ((childAt instanceof UserCell) && (findContainingViewHolder = this.listView.findContainingViewHolder(childAt)) != null) {
                            UserCell userCell = (UserCell) childAt;
                            ArrayList unused = userCell.animators = arrayList2;
                            userCell.setEnabled(true);
                            this.listAdapter.onViewAttachedToWindow(findContainingViewHolder);
                            ArrayList unused2 = userCell.animators = null;
                        }
                    }
                    if (!arrayList2.isEmpty()) {
                        AnimatorSet animatorSet = new AnimatorSet();
                        animatorSet.playTogether(arrayList2);
                        animatorSet.setDuration(180);
                        animatorSet.start();
                    }
                    this.loadingResults = false;
                    return;
                }
                if (z) {
                    updateButtons();
                }
                this.listAdapter.notifyDataSetChanged();
                return;
            }
            return;
        }
        dismiss();
    }

    public /* synthetic */ void lambda$new$4$PollVotesAlert(ChatActivity chatActivity2, View view, int i) {
        if (chatActivity2 != null && chatActivity2.getParentActivity() != null) {
            ArrayList<Integer> arrayList = this.queries;
            if (arrayList == null || arrayList.isEmpty()) {
                int i2 = 0;
                if (view instanceof TextCell) {
                    int sectionForPosition = this.listAdapter.getSectionForPosition(i) - 1;
                    int positionInSectionForPosition = this.listAdapter.getPositionInSectionForPosition(i) - 1;
                    if (positionInSectionForPosition > 0 && sectionForPosition >= 0) {
                        VotesList votesList = this.voters.get(sectionForPosition);
                        if (positionInSectionForPosition == votesList.getCount() && !this.loadingMore.contains(votesList)) {
                            if (!votesList.collapsed || votesList.collapsedCount >= votesList.votes.size()) {
                                this.loadingMore.add(votesList);
                                TLRPC$TL_messages_getPollVotes tLRPC$TL_messages_getPollVotes = new TLRPC$TL_messages_getPollVotes();
                                tLRPC$TL_messages_getPollVotes.peer = this.peer;
                                tLRPC$TL_messages_getPollVotes.id = this.messageObject.getId();
                                tLRPC$TL_messages_getPollVotes.limit = 50;
                                int i3 = tLRPC$TL_messages_getPollVotes.flags | 1;
                                tLRPC$TL_messages_getPollVotes.flags = i3;
                                tLRPC$TL_messages_getPollVotes.option = votesList.option;
                                tLRPC$TL_messages_getPollVotes.flags = i3 | 2;
                                tLRPC$TL_messages_getPollVotes.offset = votesList.next_offset;
                                this.chatActivity.getConnectionsManager().sendRequest(tLRPC$TL_messages_getPollVotes, new RequestDelegate(votesList, chatActivity2) {
                                    private final /* synthetic */ PollVotesAlert.VotesList f$1;
                                    private final /* synthetic */ ChatActivity f$2;

                                    {
                                        this.f$1 = r2;
                                        this.f$2 = r3;
                                    }

                                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                                        PollVotesAlert.this.lambda$null$3$PollVotesAlert(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
                                    }
                                });
                                return;
                            }
                            int min = Math.min(votesList.collapsedCount + 50, votesList.votes.size());
                            votesList.collapsedCount = min;
                            if (min == votesList.votes.size()) {
                                votesList.collapsed = false;
                            }
                            this.listAdapter.notifyDataSetChanged();
                        }
                    }
                } else if (view instanceof UserCell) {
                    UserCell userCell = (UserCell) view;
                    if (userCell.currentUser != null) {
                        TLRPC$User currentUser = chatActivity2.getCurrentUser();
                        Bundle bundle = new Bundle();
                        bundle.putInt("user_id", userCell.currentUser.id);
                        dismiss();
                        ProfileActivity profileActivity = new ProfileActivity(bundle);
                        if (currentUser != null && currentUser.id == userCell.currentUser.id) {
                            i2 = 1;
                        }
                        profileActivity.setPlayProfileAnimation(i2);
                        chatActivity2.presentFragment(profileActivity);
                    }
                }
            }
        }
    }

    public /* synthetic */ void lambda$null$3$PollVotesAlert(VotesList votesList, ChatActivity chatActivity2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(votesList, tLObject, chatActivity2) {
            private final /* synthetic */ PollVotesAlert.VotesList f$1;
            private final /* synthetic */ TLObject f$2;
            private final /* synthetic */ ChatActivity f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                PollVotesAlert.this.lambda$null$2$PollVotesAlert(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$2$PollVotesAlert(VotesList votesList, TLObject tLObject, ChatActivity chatActivity2) {
        if (isShowing()) {
            this.loadingMore.remove(votesList);
            if (tLObject != null) {
                TLRPC$TL_messages_votesList tLRPC$TL_messages_votesList = (TLRPC$TL_messages_votesList) tLObject;
                chatActivity2.getMessagesController().putUsers(tLRPC$TL_messages_votesList.users, false);
                votesList.votes.addAll(tLRPC$TL_messages_votesList.votes);
                votesList.next_offset = tLRPC$TL_messages_votesList.next_offset;
                this.listAdapter.notifyDataSetChanged();
            }
        }
    }

    private void updateButtons() {
        this.votesPercents.clear();
        TLRPC$TL_messageMediaPoll tLRPC$TL_messageMediaPoll = (TLRPC$TL_messageMediaPoll) this.messageObject.messageOwner.media;
        ArrayList arrayList = new ArrayList();
        int size = this.voters.size();
        int i = 100;
        boolean z = false;
        int i2 = 0;
        int i3 = 0;
        for (int i4 = 0; i4 < size; i4++) {
            VotesList votesList = this.voters.get(i4);
            Button button = new Button();
            arrayList.add(button);
            this.votesPercents.put(votesList, button);
            if (!tLRPC$TL_messageMediaPoll.results.results.isEmpty()) {
                int size2 = tLRPC$TL_messageMediaPoll.results.results.size();
                int i5 = 0;
                while (true) {
                    if (i5 >= size2) {
                        break;
                    }
                    TLRPC$TL_pollAnswerVoters tLRPC$TL_pollAnswerVoters = tLRPC$TL_messageMediaPoll.results.results.get(i5);
                    if (Arrays.equals(votesList.option, tLRPC$TL_pollAnswerVoters.option)) {
                        int unused = button.votesCount = tLRPC$TL_pollAnswerVoters.voters;
                        float unused2 = button.decimal = (((float) tLRPC$TL_pollAnswerVoters.voters) / ((float) tLRPC$TL_messageMediaPoll.results.total_voters)) * 100.0f;
                        int unused3 = button.percent = (int) button.decimal;
                        float unused4 = button.decimal = button.decimal - ((float) button.percent);
                        if (i2 == 0) {
                            i2 = button.percent;
                        } else if (!(button.percent == 0 || i2 == button.percent)) {
                            z = true;
                        }
                        i -= button.percent;
                        i3 = Math.max(button.percent, i3);
                    } else {
                        i5++;
                    }
                }
            }
        }
        if (z && i != 0) {
            Collections.sort(arrayList, $$Lambda$PollVotesAlert$fCGvRabJVpryl8mZxJiM3wcqaoY.INSTANCE);
            int min = Math.min(i, arrayList.size());
            for (int i6 = 0; i6 < min; i6++) {
                Button button2 = (Button) arrayList.get(i6);
                int unused5 = button2.percent = button2.percent + 1;
            }
        }
    }

    static /* synthetic */ int lambda$updateButtons$5(Button button, Button button2) {
        if (button.decimal > button2.decimal) {
            return -1;
        }
        return button.decimal < button2.decimal ? 1 : 0;
    }

    public void dismissInternal() {
        int size = this.queries.size();
        for (int i = 0; i < size; i++) {
            this.chatActivity.getConnectionsManager().cancelRequest(this.queries.get(i).intValue(), true);
        }
        super.dismissInternal();
    }

    /* access modifiers changed from: private */
    @SuppressLint({"NewApi"})
    public void updateLayout(boolean z) {
        if (this.listView.getChildCount() <= 0) {
            RecyclerListView recyclerListView = this.listView;
            int paddingTop = recyclerListView.getPaddingTop();
            this.scrollOffsetY = paddingTop;
            recyclerListView.setTopGlowOffset(paddingTop);
            this.containerView.invalidate();
            return;
        }
        View childAt = this.listView.getChildAt(0);
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(childAt);
        int top = childAt.getTop();
        int dp = AndroidUtilities.dp(7.0f);
        if (top < AndroidUtilities.dp(7.0f) || holder == null || holder.getAdapterPosition() != 0) {
            top = dp;
        }
        boolean z2 = top <= AndroidUtilities.dp(12.0f);
        if ((z2 && this.actionBar.getTag() == null) || (!z2 && this.actionBar.getTag() != null)) {
            this.actionBar.setTag(z2 ? 1 : null);
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
            fArr[0] = z2 ? 1.0f : 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(actionBar2, property, fArr);
            View view = this.actionBarShadow;
            Property property2 = View.ALPHA;
            float[] fArr2 = new float[1];
            if (!z2) {
                f = 0.0f;
            }
            fArr2[0] = f;
            animatorArr[1] = ObjectAnimator.ofFloat(view, property2, fArr2);
            animatorSet3.playTogether(animatorArr);
            this.actionBarAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                }

                public void onAnimationCancel(Animator animator) {
                    AnimatorSet unused = PollVotesAlert.this.actionBarAnimation = null;
                }
            });
            this.actionBarAnimation.start();
        }
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.listView.getLayoutParams();
        int dp2 = top + (layoutParams.topMargin - AndroidUtilities.dp(11.0f));
        if (this.scrollOffsetY != dp2) {
            RecyclerListView recyclerListView2 = this.listView;
            this.scrollOffsetY = dp2;
            recyclerListView2.setTopGlowOffset(dp2 - layoutParams.topMargin);
            this.containerView.invalidate();
        }
    }

    /* access modifiers changed from: private */
    public void updatePlaceholder() {
        if (this.placeholderPaint != null) {
            int color = Theme.getColor("dialogBackground");
            int color2 = Theme.getColor("dialogBackgroundGray");
            int averageColor = AndroidUtilities.getAverageColor(color2, color);
            this.placeholderPaint.setColor(color2);
            float dp = (float) AndroidUtilities.dp(500.0f);
            this.gradientWidth = dp;
            LinearGradient linearGradient = new LinearGradient(0.0f, 0.0f, dp, 0.0f, new int[]{color2, averageColor, color2}, new float[]{0.0f, 0.18f, 0.36f}, Shader.TileMode.REPEAT);
            this.placeholderGradient = linearGradient;
            this.placeholderPaint.setShader(linearGradient);
            Matrix matrix = new Matrix();
            this.placeholderMatrix = matrix;
            this.placeholderGradient.setLocalMatrix(matrix);
        }
    }

    public class Adapter extends RecyclerListView.SectionsAdapter {
        private Context mContext;

        public String getLetter(int i) {
            return null;
        }

        public int getPositionForScrollProgress(float f) {
            return 0;
        }

        public Adapter(Context context) {
            int i = UserConfig.selectedAccount;
            this.mContext = context;
        }

        public boolean isEnabled(int i, int i2) {
            if (i == 0 || i2 == 0) {
                return false;
            }
            return PollVotesAlert.this.queries == null || PollVotesAlert.this.queries.isEmpty();
        }

        public int getSectionCount() {
            return PollVotesAlert.this.voters.size() + 1;
        }

        public int getCountForSection(int i) {
            int i2 = 1;
            if (i == 0) {
                return 1;
            }
            VotesList votesList = (VotesList) PollVotesAlert.this.voters.get(i - 1);
            int count = votesList.getCount() + 1;
            if (TextUtils.isEmpty(votesList.next_offset) && !votesList.collapsed) {
                i2 = 0;
            }
            return count + i2;
        }

        private SectionCell createSectionCell() {
            return new SectionCell(this.mContext) {
                /* access modifiers changed from: protected */
                public void onCollapseClick() {
                    VotesList votesList = (VotesList) getTag(NUM);
                    if (votesList.votes.size() > 15) {
                        boolean z = !votesList.collapsed;
                        votesList.collapsed = z;
                        if (z) {
                            votesList.collapsedCount = 10;
                        }
                        PollVotesAlert.this.listAdapter.notifyDataSetChanged();
                    }
                }
            };
        }

        public View getSectionHeaderView(int i, View view) {
            if (view == null) {
                view = createSectionCell();
            }
            SectionCell sectionCell = (SectionCell) view;
            if (i != 0) {
                view.setAlpha(1.0f);
                VotesList votesList = (VotesList) PollVotesAlert.this.voters.get(i - 1);
                int i2 = 0;
                TLRPC$MessageUserVote tLRPC$MessageUserVote = votesList.votes.get(0);
                int size = PollVotesAlert.this.poll.answers.size();
                while (true) {
                    if (i2 >= size) {
                        break;
                    }
                    TLRPC$TL_pollAnswer tLRPC$TL_pollAnswer = PollVotesAlert.this.poll.answers.get(i2);
                    if (Arrays.equals(tLRPC$TL_pollAnswer.option, votesList.option)) {
                        Button button = (Button) PollVotesAlert.this.votesPercents.get(votesList);
                        sectionCell.setText(tLRPC$TL_pollAnswer.text, button.percent, button.votesCount, votesList.getCollapsed());
                        sectionCell.setTag(NUM, votesList);
                        break;
                    }
                    i2++;
                }
            } else {
                sectionCell.setAlpha(0.0f);
            }
            return view;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            TextCell textCell;
            if (i == 0) {
                textCell = new UserCell(this.mContext);
            } else if (i == 1) {
                if (PollVotesAlert.this.titleTextView.getParent() != null) {
                    ((ViewGroup) PollVotesAlert.this.titleTextView.getParent()).removeView(PollVotesAlert.this.titleTextView);
                }
                textCell = PollVotesAlert.this.titleTextView;
            } else if (i != 2) {
                TextCell textCell2 = new TextCell(this.mContext, 23, true);
                textCell2.setOffsetFromImage(65);
                textCell2.setColors("switchTrackChecked", "windowBackgroundWhiteBlueText4");
                textCell = textCell2;
            } else {
                textCell = createSectionCell();
            }
            return new RecyclerListView.Holder(textCell);
        }

        public void onBindViewHolder(int i, int i2, RecyclerView.ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 2) {
                SectionCell sectionCell = (SectionCell) viewHolder.itemView;
                VotesList votesList = (VotesList) PollVotesAlert.this.voters.get(i - 1);
                TLRPC$MessageUserVote tLRPC$MessageUserVote = votesList.votes.get(0);
                int size = PollVotesAlert.this.poll.answers.size();
                for (int i3 = 0; i3 < size; i3++) {
                    TLRPC$TL_pollAnswer tLRPC$TL_pollAnswer = PollVotesAlert.this.poll.answers.get(i3);
                    if (Arrays.equals(tLRPC$TL_pollAnswer.option, votesList.option)) {
                        Button button = (Button) PollVotesAlert.this.votesPercents.get(votesList);
                        sectionCell.setText(tLRPC$TL_pollAnswer.text, button.percent, button.votesCount, votesList.getCollapsed());
                        sectionCell.setTag(NUM, votesList);
                        return;
                    }
                }
            } else if (itemViewType == 3) {
                VotesList votesList2 = (VotesList) PollVotesAlert.this.voters.get(i - 1);
                ((TextCell) viewHolder.itemView).setTextAndIcon(LocaleController.formatPluralString("ShowVotes", votesList2.count - votesList2.getCount()), NUM, false);
            }
        }

        public void onViewAttachedToWindow(RecyclerView.ViewHolder viewHolder) {
            if (viewHolder.getItemViewType() == 0) {
                int adapterPosition = viewHolder.getAdapterPosition();
                int sectionForPosition = getSectionForPosition(adapterPosition);
                int positionInSectionForPosition = getPositionInSectionForPosition(adapterPosition) - 1;
                UserCell userCell = (UserCell) viewHolder.itemView;
                VotesList votesList = (VotesList) PollVotesAlert.this.voters.get(sectionForPosition - 1);
                TLRPC$MessageUserVote tLRPC$MessageUserVote = votesList.votes.get(positionInSectionForPosition);
                TLRPC$User user = tLRPC$MessageUserVote.user_id != 0 ? PollVotesAlert.this.chatActivity.getMessagesController().getUser(Integer.valueOf(tLRPC$MessageUserVote.user_id)) : null;
                boolean z = true;
                if (positionInSectionForPosition == votesList.getCount() - 1 && TextUtils.isEmpty(votesList.next_offset) && !votesList.collapsed) {
                    z = false;
                }
                userCell.setData(user, positionInSectionForPosition, z);
            }
        }

        public int getItemViewType(int i, int i2) {
            if (i == 0) {
                return 1;
            }
            if (i2 == 0) {
                return 2;
            }
            return i2 + -1 < ((VotesList) PollVotesAlert.this.voters.get(i + -1)).getCount() ? 0 : 3;
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        $$Lambda$PollVotesAlert$duMvUriWRtWBbdEJKBEaIhPp5ng r10 = new ThemeDescription.ThemeDescriptionDelegate() {
            public final void didSetColor() {
                PollVotesAlert.this.updatePlaceholder();
            }
        };
        $$Lambda$PollVotesAlert$duMvUriWRtWBbdEJKBEaIhPp5ng r8 = r10;
        return new ThemeDescription[]{new ThemeDescription(this.containerView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "key_sheet_scrollUp"), new ThemeDescription(this.containerView, 0, (Class[]) null, (Paint) null, new Drawable[]{this.shadowDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogBackground"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogBackground"), new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogScrollGlow"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlack"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlack"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBTITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_actionBarSubtitle"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlack"), new ThemeDescription(this.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlack"), new ThemeDescription(this.actionBarShadow, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogShadowLine"), new ThemeDescription((View) this.listView, 0, new Class[]{View.class}, (String[]) null, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) r8, "dialogBackground"), new ThemeDescription((View) this.listView, 0, new Class[]{View.class}, (String[]) null, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) r8, "dialogBackgroundGray"), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_SECTIONS, new Class[]{SectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "key_graySectionText"), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_SECTIONS, new Class[]{SectionCell.class}, new String[]{"middleTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "key_graySectionText"), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_SECTIONS, new Class[]{SectionCell.class}, new String[]{"righTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "key_graySectionText"), new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR | ThemeDescription.FLAG_SECTIONS, new Class[]{SectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "graySection"), new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlack"), new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"), new ThemeDescription((View) this.listView, 0, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText4"), new ThemeDescription((View) this.listView, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackChecked")};
    }
}
