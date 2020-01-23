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
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView;
import androidx.annotation.Keep;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
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
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputPeer;
import org.telegram.tgnet.TLRPC.MessageUserVote;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputPeerChannel;
import org.telegram.tgnet.TLRPC.TL_inputPeerChat;
import org.telegram.tgnet.TLRPC.TL_inputPeerUser;
import org.telegram.tgnet.TLRPC.TL_messageMediaPoll;
import org.telegram.tgnet.TLRPC.TL_messageUserVoteInputOption;
import org.telegram.tgnet.TLRPC.TL_messages_getPollVotes;
import org.telegram.tgnet.TLRPC.TL_messages_votesList;
import org.telegram.tgnet.TLRPC.TL_poll;
import org.telegram.tgnet.TLRPC.TL_pollAnswer;
import org.telegram.tgnet.TLRPC.TL_pollAnswerVoters;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AnimationProperties.FloatProperty;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SectionsAdapter;
import org.telegram.ui.ProfileActivity;

public class PollVotesAlert extends BottomSheet {
    public static final Property<UserCell, Float> USER_CELL_PROPERTY = new FloatProperty<UserCell>("placeholderAlpha(") {
        public void setValue(UserCell userCell, float f) {
            userCell.setPlaceholderAlpha(f);
        }

        public Float get(UserCell userCell) {
            return Float.valueOf(userCell.getPlaceholderAlpha());
        }
    };
    private ActionBar actionBar;
    private AnimatorSet actionBarAnimation;
    private View actionBarShadow;
    private ChatActivity chatActivity;
    private float gradientWidth;
    private Adapter listAdapter;
    private RecyclerListView listView;
    private HashSet<VotesList> loadingMore = new HashSet();
    private boolean loadingResults = true;
    private MessageObject messageObject;
    private InputPeer peer;
    private LinearGradient placeholderGradient;
    private Matrix placeholderMatrix;
    private Paint placeholderPaint = new Paint(1);
    private TL_poll poll;
    private ArrayList<Integer> queries = new ArrayList();
    private RectF rect = new RectF();
    private int scrollOffsetY;
    private Drawable shadowDrawable;
    private TextView titleTextView;
    private int topBeforeSwitch;
    private float totalTranslation;
    private ArrayList<VotesList> voters = new ArrayList();
    private HashMap<VotesList, Button> votesPercents = new HashMap();

    private class Button {
        private float decimal;
        private int percent;
        private int votesCount;

        private Button() {
        }

        /* synthetic */ Button(PollVotesAlert pollVotesAlert, AnonymousClass1 anonymousClass1) {
            this();
        }
    }

    public class SectionCell extends FrameLayout {
        private TextView middleTextView;
        private TextView righTextView;
        private TextView textView = new TextView(getContext());
        final /* synthetic */ PollVotesAlert this$0;

        /* Access modifiers changed, original: protected */
        public void onCollapseClick() {
        }

        public SectionCell(PollVotesAlert pollVotesAlert, Context context) {
            final PollVotesAlert pollVotesAlert2 = pollVotesAlert;
            this.this$0 = pollVotesAlert2;
            super(context);
            setBackgroundColor(Theme.getColor("graySection"));
            this.textView.setTextSize(1, 14.0f);
            this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            String str = "key_graySectionText";
            this.textView.setTextColor(Theme.getColor(str));
            this.textView.setSingleLine(true);
            this.textView.setEllipsize(TruncateAt.END);
            int i = 5;
            this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            this.middleTextView = new TextView(getContext());
            this.middleTextView.setTextSize(1, 14.0f);
            this.middleTextView.setTextColor(Theme.getColor(str));
            this.middleTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            this.righTextView = new TextView(getContext()) {
                public boolean post(Runnable runnable) {
                    return SectionCell.this.this$0.containerView.post(runnable);
                }

                public boolean postDelayed(Runnable runnable, long j) {
                    return SectionCell.this.this$0.containerView.postDelayed(runnable, j);
                }
            };
            this.righTextView.setTextSize(1, 14.0f);
            this.righTextView.setTextColor(Theme.getColor(str));
            this.righTextView.setGravity((LocaleController.isRTL ? 3 : 5) | 16);
            this.righTextView.setOnClickListener(new -$$Lambda$PollVotesAlert$SectionCell$qoRvyHB-RWAzZbBE11wKsHpuV5U(this));
            TextView textView = this.textView;
            int i2 = (LocaleController.isRTL ? 5 : 3) | 48;
            int i3 = 0;
            float f = (float) (LocaleController.isRTL ? 0 : 16);
            if (LocaleController.isRTL) {
                i3 = 16;
            }
            addView(textView, LayoutHelper.createFrame(-2, -1.0f, i2, f, 0.0f, (float) i3, 0.0f));
            addView(this.middleTextView, LayoutHelper.createFrame(-2, -1.0f, (LocaleController.isRTL ? 5 : 3) | 48, 0.0f, 0.0f, 0.0f, 0.0f));
            textView = this.righTextView;
            if (LocaleController.isRTL) {
                i = 3;
            }
            addView(textView, LayoutHelper.createFrame(-2, -1.0f, i | 48, 16.0f, 0.0f, 16.0f, 0.0f));
        }

        public /* synthetic */ void lambda$new$0$PollVotesAlert$SectionCell(View view) {
            onCollapseClick();
        }

        /* Access modifiers changed, original: protected */
        public void onMeasure(int i, int i2) {
            int i3 = i;
            int makeMeasureSpec = MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(32.0f), NUM);
            measureChildWithMargins(this.middleTextView, i3, 0, makeMeasureSpec, 0);
            measureChildWithMargins(this.righTextView, i3, 0, makeMeasureSpec, 0);
            measureChildWithMargins(this.textView, i3, (this.middleTextView.getMeasuredWidth() + this.righTextView.getMeasuredWidth()) + AndroidUtilities.dp(32.0f), makeMeasureSpec, 0);
            setMeasuredDimension(MeasureSpec.getSize(i), AndroidUtilities.dp(32.0f));
        }

        /* Access modifiers changed, original: protected */
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
            int left;
            TextView textView;
            if (LocaleController.isRTL) {
                left = this.textView.getLeft() - this.middleTextView.getMeasuredWidth();
                textView = this.middleTextView;
                textView.layout(left, textView.getTop(), this.middleTextView.getMeasuredWidth() + left, this.middleTextView.getBottom());
                return;
            }
            left = this.textView.getRight();
            textView = this.middleTextView;
            textView.layout(left, textView.getTop(), this.middleTextView.getMeasuredWidth() + left, this.middleTextView.getBottom());
        }

        public void setText(String str, int i, int i2, int i3) {
            SpannableStringBuilder spannableStringBuilder;
            TextView textView = this.textView;
            textView.setText(Emoji.replaceEmoji(str, textView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(14.0f), false));
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
        private ArrayList<Animator> animators;
        private AvatarDrawable avatarDrawable;
        private BackupImageView avatarImageView;
        private int currentAccount = UserConfig.selectedAccount;
        private User currentUser;
        private boolean drawPlaceholder;
        private FileLocation lastAvatar;
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
            setWillNotDraw(false);
            this.avatarDrawable = new AvatarDrawable();
            this.avatarImageView = new BackupImageView(context);
            this.avatarImageView.setRoundRadius(AndroidUtilities.dp(18.0f));
            int i = 5;
            addView(this.avatarImageView, LayoutHelper.createFrame(36, 36.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 14.0f, 6.0f, LocaleController.isRTL ? 14.0f : 0.0f, 0.0f));
            this.nameTextView = new SimpleTextView(context);
            this.nameTextView.setTextColor(Theme.getColor("dialogTextBlack"));
            this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.nameTextView.setTextSize(16);
            this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            SimpleTextView simpleTextView = this.nameTextView;
            if (!LocaleController.isRTL) {
                i = 3;
            }
            addView(simpleTextView, LayoutHelper.createFrame(-1, 20.0f, i | 48, LocaleController.isRTL ? 28.0f : 65.0f, 14.0f, LocaleController.isRTL ? 65.0f : 28.0f, 0.0f));
        }

        public void setData(User user, int i, boolean z) {
            this.currentUser = user;
            this.needDivider = z;
            this.drawPlaceholder = user == null;
            this.placeholderNum = i;
            if (user == null) {
                this.nameTextView.setText("");
                this.avatarImageView.setImageDrawable(null);
            } else {
                update(0);
            }
            ArrayList arrayList = this.animators;
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

        /* Access modifiers changed, original: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f) + this.needDivider, NUM));
        }

        /* JADX WARNING: Removed duplicated region for block: B:33:0x0049  */
        /* JADX WARNING: Removed duplicated region for block: B:32:0x0046  */
        /* JADX WARNING: Removed duplicated region for block: B:36:0x004e  */
        /* JADX WARNING: Removed duplicated region for block: B:44:0x005c  */
        /* JADX WARNING: Removed duplicated region for block: B:47:0x0068  */
        /* JADX WARNING: Removed duplicated region for block: B:49:0x006b A:{RETURN} */
        /* JADX WARNING: Removed duplicated region for block: B:8:0x0010  */
        /* JADX WARNING: Removed duplicated region for block: B:53:0x007e  */
        /* JADX WARNING: Removed duplicated region for block: B:52:0x0079  */
        /* JADX WARNING: Removed duplicated region for block: B:59:0x008d  */
        /* JADX WARNING: Removed duplicated region for block: B:56:0x0084  */
        /* JADX WARNING: Removed duplicated region for block: B:63:0x00ae  */
        /* JADX WARNING: Removed duplicated region for block: B:62:0x009e  */
        /* JADX WARNING: Missing block: B:22:0x0033, code skipped:
            if (r3.local_id != r0.local_id) goto L_0x0035;
     */
        public void update(int r11) {
            /*
            r10 = this;
            r0 = r10.currentUser;
            r1 = 0;
            if (r0 == 0) goto L_0x000c;
        L_0x0005:
            r0 = r0.photo;
            if (r0 == 0) goto L_0x000c;
        L_0x0009:
            r0 = r0.photo_small;
            goto L_0x000d;
        L_0x000c:
            r0 = r1;
        L_0x000d:
            r2 = 0;
            if (r11 == 0) goto L_0x006c;
        L_0x0010:
            r3 = r11 & 2;
            r4 = 1;
            if (r3 == 0) goto L_0x0037;
        L_0x0015:
            r3 = r10.lastAvatar;
            if (r3 == 0) goto L_0x001b;
        L_0x0019:
            if (r0 == 0) goto L_0x0035;
        L_0x001b:
            r3 = r10.lastAvatar;
            if (r3 != 0) goto L_0x0021;
        L_0x001f:
            if (r0 != 0) goto L_0x0035;
        L_0x0021:
            r3 = r10.lastAvatar;
            if (r3 == 0) goto L_0x0037;
        L_0x0025:
            if (r0 == 0) goto L_0x0037;
        L_0x0027:
            r5 = r3.volume_id;
            r7 = r0.volume_id;
            r9 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1));
            if (r9 != 0) goto L_0x0035;
        L_0x002f:
            r3 = r3.local_id;
            r5 = r0.local_id;
            if (r3 == r5) goto L_0x0037;
        L_0x0035:
            r3 = 1;
            goto L_0x0038;
        L_0x0037:
            r3 = 0;
        L_0x0038:
            r5 = r10.currentUser;
            if (r5 == 0) goto L_0x004f;
        L_0x003c:
            if (r3 != 0) goto L_0x004f;
        L_0x003e:
            r6 = r11 & 4;
            if (r6 == 0) goto L_0x004f;
        L_0x0042:
            r5 = r5.status;
            if (r5 == 0) goto L_0x0049;
        L_0x0046:
            r5 = r5.expires;
            goto L_0x004a;
        L_0x0049:
            r5 = 0;
        L_0x004a:
            r6 = r10.lastStatus;
            if (r5 == r6) goto L_0x004f;
        L_0x004e:
            r3 = 1;
        L_0x004f:
            if (r3 != 0) goto L_0x0069;
        L_0x0051:
            r5 = r10.lastName;
            if (r5 == 0) goto L_0x0069;
        L_0x0055:
            r11 = r11 & r4;
            if (r11 == 0) goto L_0x0069;
        L_0x0058:
            r11 = r10.currentUser;
            if (r11 == 0) goto L_0x0060;
        L_0x005c:
            r1 = org.telegram.messenger.UserObject.getUserName(r11);
        L_0x0060:
            r11 = r10.lastName;
            r11 = r1.equals(r11);
            if (r11 != 0) goto L_0x0069;
        L_0x0068:
            r3 = 1;
        L_0x0069:
            if (r3 != 0) goto L_0x006c;
        L_0x006b:
            return;
        L_0x006c:
            r11 = r10.avatarDrawable;
            r3 = r10.currentUser;
            r11.setInfo(r3);
            r11 = r10.currentUser;
            r11 = r11.status;
            if (r11 == 0) goto L_0x007e;
        L_0x0079:
            r11 = r11.expires;
            r10.lastStatus = r11;
            goto L_0x0080;
        L_0x007e:
            r10.lastStatus = r2;
        L_0x0080:
            r11 = r10.currentUser;
            if (r11 == 0) goto L_0x008d;
        L_0x0084:
            if (r1 != 0) goto L_0x008a;
        L_0x0086:
            r1 = org.telegram.messenger.UserObject.getUserName(r11);
        L_0x008a:
            r10.lastName = r1;
            goto L_0x0091;
        L_0x008d:
            r11 = "";
            r10.lastName = r11;
        L_0x0091:
            r11 = r10.nameTextView;
            r1 = r10.lastName;
            r11.setText(r1);
            r10.lastAvatar = r0;
            r11 = r10.currentUser;
            if (r11 == 0) goto L_0x00ae;
        L_0x009e:
            r0 = r10.avatarImageView;
            r11 = org.telegram.messenger.ImageLocation.getForUser(r11, r2);
            r1 = r10.avatarDrawable;
            r2 = r10.currentUser;
            r3 = "50_50";
            r0.setImage(r11, r3, r1, r2);
            goto L_0x00b5;
        L_0x00ae:
            r11 = r10.avatarImageView;
            r0 = r10.avatarDrawable;
            r11.setImageDrawable(r0);
        L_0x00b5:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.PollVotesAlert$UserCell.update(int):void");
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            if (this.drawPlaceholder || this.placeholderAlpha != 0.0f) {
                int dp;
                int dp2;
                int dp3;
                PollVotesAlert.this.placeholderPaint.setAlpha((int) (this.placeholderAlpha * 255.0f));
                int top = this.avatarImageView.getTop() + (this.avatarImageView.getMeasuredHeight() / 2);
                canvas.drawCircle((float) (this.avatarImageView.getLeft() + (this.avatarImageView.getMeasuredWidth() / 2)), (float) top, (float) (this.avatarImageView.getMeasuredWidth() / 2), PollVotesAlert.this.placeholderPaint);
                if (this.placeholderNum % 2 == 0) {
                    dp = AndroidUtilities.dp(65.0f);
                    dp2 = AndroidUtilities.dp(48.0f);
                } else {
                    dp = AndroidUtilities.dp(65.0f);
                    dp2 = AndroidUtilities.dp(60.0f);
                }
                if (LocaleController.isRTL) {
                    dp = (getMeasuredWidth() - dp) - dp2;
                }
                PollVotesAlert.this.rect.set((float) dp, (float) (top - AndroidUtilities.dp(4.0f)), (float) (dp + dp2), (float) (AndroidUtilities.dp(4.0f) + top));
                canvas.drawRoundRect(PollVotesAlert.this.rect, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), PollVotesAlert.this.placeholderPaint);
                if (this.placeholderNum % 2 == 0) {
                    dp = AndroidUtilities.dp(119.0f);
                    dp3 = AndroidUtilities.dp(60.0f);
                } else {
                    dp = AndroidUtilities.dp(131.0f);
                    dp3 = AndroidUtilities.dp(80.0f);
                }
                if (LocaleController.isRTL) {
                    dp = (getMeasuredWidth() - dp) - dp3;
                }
                PollVotesAlert.this.rect.set((float) dp, (float) (top - AndroidUtilities.dp(4.0f)), (float) (dp + dp3), (float) (top + AndroidUtilities.dp(4.0f)));
                canvas.drawRoundRect(PollVotesAlert.this.rect, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), PollVotesAlert.this.placeholderPaint);
            }
            if (this.needDivider) {
                canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(64.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(64.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
            }
        }
    }

    private static class VotesList {
        public boolean collapsed;
        public int collapsedCount = 10;
        public int count;
        public String next_offset;
        public byte[] option;
        public ArrayList<User> users;
        public ArrayList<MessageUserVote> votes;

        public VotesList(TL_messages_votesList tL_messages_votesList, byte[] bArr) {
            this.count = tL_messages_votesList.count;
            this.votes = tL_messages_votesList.votes;
            this.users = tL_messages_votesList.users;
            this.next_offset = tL_messages_votesList.next_offset;
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

    public class Adapter extends SectionsAdapter {
        private int currentAccount = UserConfig.selectedAccount;
        private Context mContext;

        public Object getItem(int i, int i2) {
            return null;
        }

        public String getLetter(int i) {
            return null;
        }

        public int getPositionForScrollProgress(float f) {
            return 0;
        }

        public Adapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(int i, int i2) {
            return (i == 0 || i2 == 0 || (PollVotesAlert.this.queries != null && !PollVotesAlert.this.queries.isEmpty())) ? false : true;
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
                /* Access modifiers changed, original: protected */
                public void onCollapseClick() {
                    VotesList votesList = (VotesList) getTag(NUM);
                    if (votesList.votes.size() > 15) {
                        votesList.collapsed ^= 1;
                        if (votesList.collapsed) {
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
            if (i == 0) {
                sectionCell.setAlpha(0.0f);
            } else {
                i--;
                view.setAlpha(1.0f);
                VotesList votesList = (VotesList) PollVotesAlert.this.voters.get(i);
                int i2 = 0;
                MessageUserVote messageUserVote = (MessageUserVote) votesList.votes.get(0);
                int size = PollVotesAlert.this.poll.answers.size();
                while (i2 < size) {
                    TL_pollAnswer tL_pollAnswer = (TL_pollAnswer) PollVotesAlert.this.poll.answers.get(i2);
                    if (Arrays.equals(tL_pollAnswer.option, votesList.option)) {
                        Button button = (Button) PollVotesAlert.this.votesPercents.get(votesList);
                        sectionCell.setText(tL_pollAnswer.text, button.percent, button.votesCount, votesList.getCollapsed());
                        sectionCell.setTag(NUM, votesList);
                        break;
                    }
                    i2++;
                }
            }
            return view;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View userCell;
            if (i == 0) {
                userCell = new UserCell(this.mContext);
            } else if (i == 1) {
                if (PollVotesAlert.this.titleTextView.getParent() != null) {
                    ((ViewGroup) PollVotesAlert.this.titleTextView.getParent()).removeView(PollVotesAlert.this.titleTextView);
                }
                userCell = PollVotesAlert.this.titleTextView;
            } else if (i != 2) {
                userCell = new TextCell(this.mContext, 23, true);
                userCell.setOffsetFromImage(65);
                userCell.setColors("switchTrackChecked", "windowBackgroundWhiteBlueText4");
            } else {
                userCell = createSectionCell();
            }
            return new Holder(userCell);
        }

        public void onBindViewHolder(int i, int i2, ViewHolder viewHolder) {
            i2 = viewHolder.getItemViewType();
            int i3 = 0;
            VotesList votesList;
            if (i2 == 2) {
                SectionCell sectionCell = (SectionCell) viewHolder.itemView;
                votesList = (VotesList) PollVotesAlert.this.voters.get(i - 1);
                MessageUserVote messageUserVote = (MessageUserVote) votesList.votes.get(0);
                int size = PollVotesAlert.this.poll.answers.size();
                while (i3 < size) {
                    TL_pollAnswer tL_pollAnswer = (TL_pollAnswer) PollVotesAlert.this.poll.answers.get(i3);
                    if (Arrays.equals(tL_pollAnswer.option, votesList.option)) {
                        Button button = (Button) PollVotesAlert.this.votesPercents.get(votesList);
                        sectionCell.setText(tL_pollAnswer.text, button.percent, button.votesCount, votesList.getCollapsed());
                        sectionCell.setTag(NUM, votesList);
                        return;
                    }
                    i3++;
                }
            } else if (i2 == 3) {
                votesList = (VotesList) PollVotesAlert.this.voters.get(i - 1);
                ((TextCell) viewHolder.itemView).setTextAndIcon(LocaleController.formatPluralString("ShowVotes", votesList.count - votesList.getCount()), NUM, false);
            }
        }

        public void onViewAttachedToWindow(ViewHolder viewHolder) {
            if (viewHolder.getItemViewType() == 0) {
                int adapterPosition = viewHolder.getAdapterPosition();
                adapterPosition = getPositionInSectionForPosition(adapterPosition) - 1;
                UserCell userCell = (UserCell) viewHolder.itemView;
                VotesList votesList = (VotesList) PollVotesAlert.this.voters.get(getSectionForPosition(adapterPosition) - 1);
                MessageUserVote messageUserVote = (MessageUserVote) votesList.votes.get(adapterPosition);
                User user = messageUserVote.user_id != 0 ? PollVotesAlert.this.chatActivity.getMessagesController().getUser(Integer.valueOf(messageUserVote.user_id)) : null;
                boolean z = true;
                if (adapterPosition == votesList.getCount() - 1 && TextUtils.isEmpty(votesList.next_offset) && !votesList.collapsed) {
                    z = false;
                }
                userCell.setData(user, adapterPosition, z);
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

    /* Access modifiers changed, original: protected */
    public boolean canDismissWithSwipe() {
        return false;
    }

    public static void showForPoll(ChatActivity chatActivity, MessageObject messageObject) {
        if (chatActivity != null && chatActivity.getParentActivity() != null) {
            chatActivity.showDialog(new PollVotesAlert(chatActivity, messageObject));
        }
    }

    public PollVotesAlert(ChatActivity chatActivity, MessageObject messageObject) {
        int i;
        ChatActivity chatActivity2 = chatActivity;
        int i2 = 1;
        super(chatActivity.getParentActivity(), true);
        this.messageObject = messageObject;
        this.chatActivity = chatActivity2;
        TL_messageMediaPoll tL_messageMediaPoll = (TL_messageMediaPoll) this.messageObject.messageOwner.media;
        this.poll = tL_messageMediaPoll.poll;
        Activity parentActivity = chatActivity.getParentActivity();
        Chat currentChat = chatActivity.getCurrentChat();
        User currentUser = chatActivity.getCurrentUser();
        if (ChatObject.isChannel(currentChat)) {
            this.peer = new TL_inputPeerChannel();
            InputPeer inputPeer = this.peer;
            inputPeer.channel_id = currentChat.id;
            inputPeer.access_hash = currentChat.access_hash;
        } else if (currentChat != null) {
            this.peer = new TL_inputPeerChat();
            this.peer.chat_id = currentChat.id;
        } else {
            this.peer = new TL_inputPeerUser();
            InputPeer inputPeer2 = this.peer;
            inputPeer2.user_id = currentUser.id;
            inputPeer2.access_hash = currentUser.access_hash;
        }
        ArrayList arrayList = new ArrayList();
        int size = tL_messageMediaPoll.results.results.size();
        Integer[] numArr = new Integer[size];
        int i3 = 0;
        while (i3 < size) {
            int i4;
            int i5;
            TL_pollAnswerVoters tL_pollAnswerVoters = (TL_pollAnswerVoters) tL_messageMediaPoll.results.results.get(i3);
            if (tL_pollAnswerVoters.voters == 0) {
                i4 = i3;
                i5 = size;
            } else {
                TL_messages_votesList tL_messages_votesList = new TL_messages_votesList();
                int i6 = tL_pollAnswerVoters.voters;
                i = 15;
                if (i6 > 15) {
                    i6 = 10;
                }
                for (int i7 = 0; i7 < i6; i7++) {
                    tL_messages_votesList.votes.add(new TL_messageUserVoteInputOption());
                }
                tL_messages_votesList.next_offset = i6 < tL_pollAnswerVoters.voters ? "empty" : null;
                tL_messages_votesList.count = tL_pollAnswerVoters.voters;
                this.voters.add(new VotesList(tL_messages_votesList, tL_pollAnswerVoters.option));
                TL_messages_getPollVotes tL_messages_getPollVotes = new TL_messages_getPollVotes();
                tL_messages_getPollVotes.peer = this.peer;
                tL_messages_getPollVotes.id = this.messageObject.getId();
                if (tL_pollAnswerVoters.voters > 15) {
                    i = 10;
                }
                tL_messages_getPollVotes.limit = i;
                tL_messages_getPollVotes.flags |= i2;
                tL_messages_getPollVotes.option = tL_pollAnswerVoters.option;
                -$$Lambda$PollVotesAlert$lpnnrQFn8IKhhS43T4ZDEfqstlU -__lambda_pollvotesalert_lpnnrqfn8ikhhs43t4zdefqstlu = r0;
                i5 = size;
                ConnectionsManager connectionsManager = chatActivity.getConnectionsManager();
                i4 = i3;
                -$$Lambda$PollVotesAlert$lpnnrQFn8IKhhS43T4ZDEfqstlU -__lambda_pollvotesalert_lpnnrqfn8ikhhs43t4zdefqstlu2 = new -$$Lambda$PollVotesAlert$lpnnrQFn8IKhhS43T4ZDEfqstlU(this, numArr, i3, chatActivity, arrayList, tL_pollAnswerVoters);
                numArr[i4] = Integer.valueOf(connectionsManager.sendRequest(tL_messages_getPollVotes, -__lambda_pollvotesalert_lpnnrqfn8ikhhs43t4zdefqstlu));
                this.queries.add(numArr[i4]);
            }
            i3 = i4 + 1;
            size = i5;
            i2 = 1;
        }
        updateButtons();
        Collections.sort(this.voters, new Comparator<VotesList>() {
            private int getIndex(VotesList votesList) {
                int size = PollVotesAlert.this.poll.answers.size();
                for (int i = 0; i < size; i++) {
                    if (Arrays.equals(((TL_pollAnswer) PollVotesAlert.this.poll.answers.get(i)).option, votesList.option)) {
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
        this.shadowDrawable = parentActivity.getResources().getDrawable(NUM).mutate();
        String str = "dialogBackground";
        this.shadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str), Mode.MULTIPLY));
        this.containerView = new FrameLayout(parentActivity) {
            private boolean fullHeight;
            private boolean ignoreLayout = false;
            private RectF rect = new RectF();

            /* Access modifiers changed, original: protected */
            public void onMeasure(int i, int i2) {
                int size = MeasureSpec.getSize(i2);
                boolean z = true;
                if (VERSION.SDK_INT >= 21 && !PollVotesAlert.this.isFullscreen) {
                    this.ignoreLayout = true;
                    setPadding(PollVotesAlert.this.backgroundPaddingLeft, AndroidUtilities.statusBarHeight, PollVotesAlert.this.backgroundPaddingLeft, 0);
                    this.ignoreLayout = false;
                }
                int paddingTop = size - getPaddingTop();
                ((LayoutParams) PollVotesAlert.this.listView.getLayoutParams()).topMargin = ActionBar.getCurrentActionBarHeight();
                ((LayoutParams) PollVotesAlert.this.actionBarShadow.getLayoutParams()).topMargin = ActionBar.getCurrentActionBarHeight();
                int access$1000 = (PollVotesAlert.this.backgroundPaddingTop + AndroidUtilities.dp(15.0f)) + AndroidUtilities.statusBarHeight;
                int sectionCount = PollVotesAlert.this.listAdapter.getSectionCount();
                int i3 = access$1000;
                for (access$1000 = 0; access$1000 < sectionCount; access$1000++) {
                    if (access$1000 == 0) {
                        PollVotesAlert.this.titleTextView.measure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i - (PollVotesAlert.this.backgroundPaddingLeft * 2)), NUM), i2);
                        i3 += PollVotesAlert.this.titleTextView.getMeasuredHeight();
                    } else {
                        i3 += AndroidUtilities.dp(32.0f) + (AndroidUtilities.dp(50.0f) * (PollVotesAlert.this.listAdapter.getCountForSection(access$1000) - 1));
                    }
                }
                if (i3 < paddingTop) {
                    paddingTop -= i3;
                } else {
                    paddingTop -= (paddingTop / 5) * 3;
                }
                paddingTop += AndroidUtilities.dp(8.0f);
                if (PollVotesAlert.this.listView.getPaddingTop() != paddingTop) {
                    this.ignoreLayout = true;
                    PollVotesAlert.this.listView.setPinnedSectionOffsetY(-paddingTop);
                    PollVotesAlert.this.listView.setPadding(0, paddingTop, 0, 0);
                    this.ignoreLayout = false;
                }
                if (i3 < size) {
                    z = false;
                }
                this.fullHeight = z;
                super.onMeasure(i, MeasureSpec.makeMeasureSpec(size, NUM));
            }

            /* Access modifiers changed, original: protected */
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

            /* Access modifiers changed, original: protected */
            public void onDraw(Canvas canvas) {
                float dp;
                int dp2 = AndroidUtilities.dp(13.0f);
                int access$1500 = (PollVotesAlert.this.scrollOffsetY - PollVotesAlert.this.backgroundPaddingTop) - dp2;
                if (PollVotesAlert.this.currentSheetAnimationType == 1) {
                    access$1500 = (int) (((float) access$1500) + PollVotesAlert.this.listView.getTranslationY());
                }
                int dp3 = AndroidUtilities.dp(20.0f) + access$1500;
                int measuredHeight = (getMeasuredHeight() + AndroidUtilities.dp(15.0f)) + PollVotesAlert.this.backgroundPaddingTop;
                if (PollVotesAlert.this.backgroundPaddingTop + access$1500 < ActionBar.getCurrentActionBarHeight()) {
                    dp = (float) (dp2 + AndroidUtilities.dp(4.0f));
                    float min = Math.min(1.0f, ((float) ((ActionBar.getCurrentActionBarHeight() - access$1500) - PollVotesAlert.this.backgroundPaddingTop)) / dp);
                    dp2 = (int) ((((float) ActionBar.getCurrentActionBarHeight()) - dp) * min);
                    access$1500 -= dp2;
                    dp3 -= dp2;
                    measuredHeight += dp2;
                    dp = 1.0f - min;
                } else {
                    dp = 1.0f;
                }
                if (VERSION.SDK_INT >= 21) {
                    int i = AndroidUtilities.statusBarHeight;
                    access$1500 += i;
                    dp3 += i;
                }
                PollVotesAlert.this.shadowDrawable.setBounds(0, access$1500, getMeasuredWidth(), measuredHeight);
                PollVotesAlert.this.shadowDrawable.draw(canvas);
                String str = "dialogBackground";
                if (dp != 1.0f) {
                    Theme.dialogs_onlineCirclePaint.setColor(Theme.getColor(str));
                    this.rect.set((float) PollVotesAlert.this.backgroundPaddingLeft, (float) (PollVotesAlert.this.backgroundPaddingTop + access$1500), (float) (getMeasuredWidth() - PollVotesAlert.this.backgroundPaddingLeft), (float) ((PollVotesAlert.this.backgroundPaddingTop + access$1500) + AndroidUtilities.dp(24.0f)));
                    canvas.drawRoundRect(this.rect, ((float) AndroidUtilities.dp(12.0f)) * dp, ((float) AndroidUtilities.dp(12.0f)) * dp, Theme.dialogs_onlineCirclePaint);
                }
                if (dp != 0.0f) {
                    access$1500 = AndroidUtilities.dp(36.0f);
                    this.rect.set((float) ((getMeasuredWidth() - access$1500) / 2), (float) dp3, (float) ((getMeasuredWidth() + access$1500) / 2), (float) (dp3 + AndroidUtilities.dp(4.0f)));
                    access$1500 = Theme.getColor("key_sheet_scrollUp");
                    dp3 = Color.alpha(access$1500);
                    Theme.dialogs_onlineCirclePaint.setColor(access$1500);
                    Theme.dialogs_onlineCirclePaint.setAlpha((int) ((((float) dp3) * 1.0f) * dp));
                    canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(2.0f), Theme.dialogs_onlineCirclePaint);
                }
                dp2 = Theme.getColor(str);
                Theme.dialogs_onlineCirclePaint.setColor(Color.argb((int) (PollVotesAlert.this.actionBar.getAlpha() * 255.0f), (int) (((float) Color.red(dp2)) * 0.8f), (int) (((float) Color.green(dp2)) * 0.8f), (int) (((float) Color.blue(dp2)) * 0.8f)));
                canvas.drawRect((float) PollVotesAlert.this.backgroundPaddingLeft, 0.0f, (float) (getMeasuredWidth() - PollVotesAlert.this.backgroundPaddingLeft), (float) AndroidUtilities.statusBarHeight, Theme.dialogs_onlineCirclePaint);
            }
        };
        this.containerView.setWillNotDraw(false);
        ViewGroup viewGroup = this.containerView;
        i = this.backgroundPaddingLeft;
        viewGroup.setPadding(i, 0, i, 0);
        this.listView = new RecyclerListView(parentActivity) {
            long lastUpdateTime;

            /* Access modifiers changed, original: protected */
            public boolean allowSelectChildAtPosition(float f, float f2) {
                return f2 >= ((float) (PollVotesAlert.this.scrollOffsetY + (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0)));
            }

            /* Access modifiers changed, original: protected */
            public void dispatchDraw(Canvas canvas) {
                if (PollVotesAlert.this.loadingResults) {
                    long elapsedRealtime = SystemClock.elapsedRealtime();
                    long abs = Math.abs(this.lastUpdateTime - elapsedRealtime);
                    if (abs > 17) {
                        abs = 16;
                    }
                    this.lastUpdateTime = elapsedRealtime;
                    PollVotesAlert pollVotesAlert = PollVotesAlert.this;
                    pollVotesAlert.totalTranslation = pollVotesAlert.totalTranslation + ((((float) abs) * PollVotesAlert.this.gradientWidth) / 1800.0f);
                    while (PollVotesAlert.this.totalTranslation >= PollVotesAlert.this.gradientWidth * 2.0f) {
                        pollVotesAlert = PollVotesAlert.this;
                        pollVotesAlert.totalTranslation = pollVotesAlert.totalTranslation - (PollVotesAlert.this.gradientWidth * 2.0f);
                    }
                    PollVotesAlert.this.placeholderMatrix.setTranslate(PollVotesAlert.this.totalTranslation, 0.0f);
                    PollVotesAlert.this.placeholderGradient.setLocalMatrix(PollVotesAlert.this.placeholderMatrix);
                    invalidateViews();
                    invalidate();
                }
                super.dispatchDraw(canvas);
            }
        };
        this.listView.setClipToPadding(false);
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
        this.listView.setOnItemClickListener(new -$$Lambda$PollVotesAlert$xtwTVH3ZM7FGeLNsGo5ocCvVGLY(this, chatActivity2));
        this.listView.setOnScrollListener(new OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                if (PollVotesAlert.this.listView.getChildCount() > 0) {
                    PollVotesAlert.this.updateLayout(true);
                }
            }

            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                if (i == 0) {
                    if (((PollVotesAlert.this.scrollOffsetY - PollVotesAlert.this.backgroundPaddingTop) - AndroidUtilities.dp(13.0f)) + PollVotesAlert.this.backgroundPaddingTop < ActionBar.getCurrentActionBarHeight() && PollVotesAlert.this.listView.canScrollVertically(1)) {
                        PollVotesAlert.this.listView.getChildAt(0);
                        Holder holder = (Holder) PollVotesAlert.this.listView.findViewHolderForAdapterPosition(0);
                        if (holder != null && holder.itemView.getTop() > AndroidUtilities.dp(7.0f)) {
                            PollVotesAlert.this.listView.smoothScrollBy(0, holder.itemView.getTop() - AndroidUtilities.dp(7.0f));
                        }
                    }
                }
            }
        });
        this.titleTextView = new TextView(parentActivity);
        this.titleTextView.setTextSize(1, 18.0f);
        this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.titleTextView.setPadding(AndroidUtilities.dp(21.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(14.0f), AndroidUtilities.dp(21.0f));
        String str2 = "dialogTextBlack";
        this.titleTextView.setTextColor(Theme.getColor(str2));
        this.titleTextView.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
        TextView textView = this.titleTextView;
        textView.setText(Emoji.replaceEmoji(this.poll.question, textView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(18.0f), false));
        this.actionBar = new ActionBar(parentActivity) {
            public void setAlpha(float f) {
                super.setAlpha(f);
                PollVotesAlert.this.containerView.invalidate();
            }
        };
        this.actionBar.setBackgroundColor(Theme.getColor(str));
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setItemsColor(Theme.getColor(str2), false);
        this.actionBar.setItemsBackgroundColor(Theme.getColor("dialogButtonSelector"), false);
        this.actionBar.setTitleColor(Theme.getColor(str2));
        this.actionBar.setSubtitleColor(Theme.getColor("player_actionBarSubtitle"));
        this.actionBar.setOccupyStatusBar(false);
        this.actionBar.setAlpha(0.0f);
        this.actionBar.setTitle(LocaleController.getString("PollResults", NUM));
        if (this.poll.quiz) {
            this.actionBar.setSubtitle(LocaleController.formatPluralString("Answer", tL_messageMediaPoll.results.total_voters));
        } else {
            this.actionBar.setSubtitle(LocaleController.formatPluralString("Vote", tL_messageMediaPoll.results.total_voters));
        }
        this.containerView.addView(this.actionBar, LayoutHelper.createFrame(-1, -2.0f));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    PollVotesAlert.this.dismiss();
                }
            }
        });
        this.actionBarShadow = new View(parentActivity);
        this.actionBarShadow.setAlpha(0.0f);
        this.actionBarShadow.setBackgroundColor(Theme.getColor("dialogShadowLine"));
        this.containerView.addView(this.actionBarShadow, LayoutHelper.createFrame(-1, 1.0f));
    }

    public /* synthetic */ void lambda$new$1$PollVotesAlert(Integer[] numArr, int i, ChatActivity chatActivity, ArrayList arrayList, TL_pollAnswerVoters tL_pollAnswerVoters, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$PollVotesAlert$yPn91rkNoAqao_B1yJm5Z5cWO1g(this, numArr, i, tLObject, chatActivity, arrayList, tL_pollAnswerVoters));
    }

    public /* synthetic */ void lambda$null$0$PollVotesAlert(Integer[] numArr, int i, TLObject tLObject, ChatActivity chatActivity, ArrayList arrayList, TL_pollAnswerVoters tL_pollAnswerVoters) {
        this.queries.remove(numArr[i]);
        if (tLObject != null) {
            TL_messages_votesList tL_messages_votesList = (TL_messages_votesList) tLObject;
            chatActivity.getMessagesController().putUsers(tL_messages_votesList.users, false);
            if (!tL_messages_votesList.votes.isEmpty()) {
                arrayList.add(new VotesList(tL_messages_votesList, tL_pollAnswerVoters.option));
            }
            if (this.queries.isEmpty()) {
                int size = arrayList.size();
                Object obj = null;
                for (i = 0; i < size; i++) {
                    VotesList votesList = (VotesList) arrayList.get(i);
                    int size2 = this.voters.size();
                    int i2 = 0;
                    while (i2 < size2) {
                        VotesList votesList2 = (VotesList) this.voters.get(i2);
                        if (Arrays.equals(votesList.option, votesList2.option)) {
                            votesList2.next_offset = votesList.next_offset;
                            if (!(votesList2.count == votesList.count && votesList2.votes.size() == votesList.votes.size())) {
                                obj = 1;
                            }
                            votesList2.count = votesList.count;
                            votesList2.users = votesList.users;
                            votesList2.votes = votesList.votes;
                        } else {
                            i2++;
                        }
                    }
                }
                this.loadingResults = false;
                RecyclerListView recyclerListView = this.listView;
                if (recyclerListView == null) {
                    return;
                }
                if (this.currentSheetAnimationType == 0 && this.startAnimationRunnable == null && obj == null) {
                    size = recyclerListView.getChildCount();
                    ArrayList arrayList2 = new ArrayList();
                    for (int i3 = 0; i3 < size; i3++) {
                        View childAt = this.listView.getChildAt(i3);
                        if (childAt instanceof UserCell) {
                            ViewHolder findContainingViewHolder = this.listView.findContainingViewHolder(childAt);
                            if (findContainingViewHolder != null) {
                                UserCell userCell = (UserCell) childAt;
                                userCell.animators = arrayList2;
                                userCell.setEnabled(true);
                                this.listAdapter.onViewAttachedToWindow(findContainingViewHolder);
                                userCell.animators = null;
                            }
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
                if (obj != null) {
                    updateButtons();
                }
                this.listAdapter.notifyDataSetChanged();
                return;
            }
            return;
        }
        dismiss();
    }

    public /* synthetic */ void lambda$new$4$PollVotesAlert(ChatActivity chatActivity, View view, int i) {
        if (!(chatActivity == null || chatActivity.getParentActivity() == null)) {
            ArrayList arrayList = this.queries;
            if (arrayList == null || arrayList.isEmpty()) {
                boolean z = false;
                if (view instanceof TextCell) {
                    int sectionForPosition = this.listAdapter.getSectionForPosition(i) - 1;
                    i = this.listAdapter.getPositionInSectionForPosition(i) - 1;
                    if (i > 0 && sectionForPosition >= 0) {
                        VotesList votesList = (VotesList) this.voters.get(sectionForPosition);
                        if (i == votesList.getCount() && !this.loadingMore.contains(votesList)) {
                            if (!votesList.collapsed || votesList.collapsedCount >= votesList.votes.size()) {
                                this.loadingMore.add(votesList);
                                TL_messages_getPollVotes tL_messages_getPollVotes = new TL_messages_getPollVotes();
                                tL_messages_getPollVotes.peer = this.peer;
                                tL_messages_getPollVotes.id = this.messageObject.getId();
                                tL_messages_getPollVotes.limit = 50;
                                tL_messages_getPollVotes.flags |= 1;
                                tL_messages_getPollVotes.option = votesList.option;
                                tL_messages_getPollVotes.flags |= 2;
                                tL_messages_getPollVotes.offset = votesList.next_offset;
                                this.chatActivity.getConnectionsManager().sendRequest(tL_messages_getPollVotes, new -$$Lambda$PollVotesAlert$MLgzZkUd_TJaMZiZkzIe8uAHttU(this, votesList, chatActivity));
                            } else {
                                votesList.collapsedCount = Math.min(votesList.collapsedCount + 50, votesList.votes.size());
                                if (votesList.collapsedCount == votesList.votes.size()) {
                                    votesList.collapsed = false;
                                }
                                this.listAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                } else if (view instanceof UserCell) {
                    UserCell userCell = (UserCell) view;
                    User currentUser = chatActivity.getCurrentUser();
                    Bundle bundle = new Bundle();
                    bundle.putInt("user_id", userCell.currentUser.id);
                    dismiss();
                    ProfileActivity profileActivity = new ProfileActivity(bundle);
                    if (currentUser != null && currentUser.id == userCell.currentUser.id) {
                        z = true;
                    }
                    profileActivity.setPlayProfileAnimation(z);
                    chatActivity.presentFragment(profileActivity);
                }
            }
        }
    }

    public /* synthetic */ void lambda$null$3$PollVotesAlert(VotesList votesList, ChatActivity chatActivity, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$PollVotesAlert$5oPmIpGzdH0uSEtkOoKX6wVw78w(this, votesList, tLObject, chatActivity));
    }

    public /* synthetic */ void lambda$null$2$PollVotesAlert(VotesList votesList, TLObject tLObject, ChatActivity chatActivity) {
        if (isShowing()) {
            this.loadingMore.remove(votesList);
            if (tLObject != null) {
                TL_messages_votesList tL_messages_votesList = (TL_messages_votesList) tLObject;
                chatActivity.getMessagesController().putUsers(tL_messages_votesList.users, false);
                votesList.votes.addAll(tL_messages_votesList.votes);
                votesList.next_offset = tL_messages_votesList.next_offset;
                this.listAdapter.notifyDataSetChanged();
            }
        }
    }

    private int getCurrentTop() {
        if (this.listView.getChildCount() != 0) {
            int i = 0;
            View childAt = this.listView.getChildAt(0);
            Holder holder = (Holder) this.listView.findContainingViewHolder(childAt);
            if (holder != null) {
                int paddingTop = this.listView.getPaddingTop();
                if (holder.getAdapterPosition() == 0 && childAt.getTop() >= 0) {
                    i = childAt.getTop();
                }
                return paddingTop - i;
            }
        }
        return -1000;
    }

    private void updateButtons() {
        this.votesPercents.clear();
        TL_messageMediaPoll tL_messageMediaPoll = (TL_messageMediaPoll) this.messageObject.messageOwner.media;
        ArrayList arrayList = new ArrayList();
        int size = this.voters.size();
        Object obj = null;
        int i = 100;
        int i2 = 0;
        int i3 = 0;
        for (int i4 = 0; i4 < size; i4++) {
            VotesList votesList = (VotesList) this.voters.get(i4);
            Button button = new Button(this, null);
            arrayList.add(button);
            this.votesPercents.put(votesList, button);
            if (!tL_messageMediaPoll.results.results.isEmpty()) {
                int size2 = tL_messageMediaPoll.results.results.size();
                int i5 = 0;
                while (i5 < size2) {
                    TL_pollAnswerVoters tL_pollAnswerVoters = (TL_pollAnswerVoters) tL_messageMediaPoll.results.results.get(i5);
                    if (Arrays.equals(votesList.option, tL_pollAnswerVoters.option)) {
                        button.votesCount = tL_pollAnswerVoters.voters;
                        button.decimal = (((float) tL_pollAnswerVoters.voters) / ((float) tL_messageMediaPoll.results.total_voters)) * 100.0f;
                        button.percent = (int) button.decimal;
                        button.decimal = button.decimal - ((float) button.percent);
                        if (i2 == 0) {
                            i2 = button.percent;
                        } else if (!(button.percent == 0 || i2 == button.percent)) {
                            obj = 1;
                        }
                        i -= button.percent;
                        i3 = Math.max(button.percent, i3);
                    } else {
                        i5++;
                    }
                }
            }
        }
        if (obj != null && i != 0) {
            Collections.sort(arrayList, -$$Lambda$PollVotesAlert$fCGvRabJVpryl8mZxJiM3wcqaoY.INSTANCE);
            int min = Math.min(i, arrayList.size());
            for (size = 0; size < min; size++) {
                Button button2 = (Button) arrayList.get(size);
                button2.percent = button2.percent + 1;
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
            this.chatActivity.getConnectionsManager().cancelRequest(((Integer) this.queries.get(i)).intValue(), true);
        }
        super.dismissInternal();
    }

    @SuppressLint({"NewApi"})
    private void updateLayout(boolean z) {
        if (this.listView.getChildCount() <= 0) {
            RecyclerListView recyclerListView = this.listView;
            int paddingTop = recyclerListView.getPaddingTop();
            this.scrollOffsetY = paddingTop;
            recyclerListView.setTopGlowOffset(paddingTop);
            this.containerView.invalidate();
            return;
        }
        View childAt = this.listView.getChildAt(0);
        Holder holder = (Holder) this.listView.findContainingViewHolder(childAt);
        int top = childAt.getTop();
        int dp = AndroidUtilities.dp(7.0f);
        if (top < AndroidUtilities.dp(7.0f) || holder == null || holder.getAdapterPosition() != 0) {
            top = dp;
        }
        Object obj = top <= AndroidUtilities.dp(12.0f) ? 1 : null;
        if ((obj != null && this.actionBar.getTag() == null) || (obj == null && this.actionBar.getTag() != null)) {
            this.actionBar.setTag(obj != null ? Integer.valueOf(1) : null);
            AnimatorSet animatorSet = this.actionBarAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.actionBarAnimation = null;
            }
            this.actionBarAnimation = new AnimatorSet();
            this.actionBarAnimation.setDuration(180);
            animatorSet = this.actionBarAnimation;
            Animator[] animatorArr = new Animator[2];
            ActionBar actionBar = this.actionBar;
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            float f = 1.0f;
            fArr[0] = obj != null ? 1.0f : 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(actionBar, property, fArr);
            View view = this.actionBarShadow;
            property = View.ALPHA;
            fArr = new float[1];
            if (obj == null) {
                f = 0.0f;
            }
            fArr[0] = f;
            animatorArr[1] = ObjectAnimator.ofFloat(view, property, fArr);
            animatorSet.playTogether(animatorArr);
            this.actionBarAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                }

                public void onAnimationCancel(Animator animator) {
                    PollVotesAlert.this.actionBarAnimation = null;
                }
            });
            this.actionBarAnimation.start();
        }
        LayoutParams layoutParams = (LayoutParams) this.listView.getLayoutParams();
        top += layoutParams.topMargin - AndroidUtilities.dp(11.0f);
        if (this.scrollOffsetY != top) {
            RecyclerListView recyclerListView2 = this.listView;
            this.scrollOffsetY = top;
            recyclerListView2.setTopGlowOffset(top - layoutParams.topMargin);
            this.containerView.invalidate();
        }
    }

    private void updatePlaceholder() {
        if (this.placeholderPaint != null) {
            int color = Theme.getColor("dialogBackground");
            int color2 = Theme.getColor("dialogBackgroundGray");
            color = AndroidUtilities.getAverageColor(color2, color);
            this.placeholderPaint.setColor(color2);
            float dp = (float) AndroidUtilities.dp(500.0f);
            this.gradientWidth = dp;
            this.placeholderGradient = new LinearGradient(0.0f, 0.0f, dp, 0.0f, new int[]{color2, color, color2}, new float[]{0.0f, 0.18f, 0.36f}, TileMode.REPEAT);
            this.placeholderPaint.setShader(this.placeholderGradient);
            this.placeholderMatrix = new Matrix();
            this.placeholderGradient.setLocalMatrix(this.placeholderMatrix);
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        -$$Lambda$PollVotesAlert$duMvUriWRtWBbdEJKBEaIhPp5ng -__lambda_pollvotesalert_dumvuriwrtwbbdejkbeaihpp5ng = new -$$Lambda$PollVotesAlert$duMvUriWRtWBbdEJKBEaIhPp5ng(this);
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[20];
        themeDescriptionArr[0] = new ThemeDescription(this.containerView, 0, null, null, null, null, "key_sheet_scrollUp");
        themeDescriptionArr[1] = new ThemeDescription(this.containerView, 0, null, null, new Drawable[]{this.shadowDrawable}, null, "dialogBackground");
        themeDescriptionArr[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "dialogBackground");
        themeDescriptionArr[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "dialogScrollGlow");
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "dialogTextBlack");
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "dialogTextBlack");
        themeDescriptionArr[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBTITLECOLOR, null, null, null, null, "player_actionBarSubtitle");
        themeDescriptionArr[7] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "dialogTextBlack");
        themeDescriptionArr[8] = new ThemeDescription(this.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "dialogTextBlack");
        themeDescriptionArr[9] = new ThemeDescription(this.actionBarShadow, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "dialogShadowLine");
        ThemeDescriptionDelegate themeDescriptionDelegate = -__lambda_pollvotesalert_dumvuriwrtwbbdejkbeaihpp5ng;
        themeDescriptionArr[10] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, null, null, null, themeDescriptionDelegate, "dialogBackground");
        themeDescriptionArr[11] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, null, null, null, themeDescriptionDelegate, "dialogBackgroundGray");
        View view = this.listView;
        int i = ThemeDescription.FLAG_SECTIONS;
        Class[] clsArr = new Class[]{SectionCell.class};
        String[] strArr = new String[1];
        strArr[0] = "textView";
        themeDescriptionArr[12] = new ThemeDescription(view, i, clsArr, strArr, null, null, null, "key_graySectionText");
        View view2 = this.listView;
        View view3 = view2;
        themeDescriptionArr[13] = new ThemeDescription(view3, ThemeDescription.FLAG_SECTIONS, new Class[]{SectionCell.class}, new String[]{"middleTextView"}, null, null, null, "key_graySectionText");
        view2 = this.listView;
        view3 = view2;
        themeDescriptionArr[14] = new ThemeDescription(view3, ThemeDescription.FLAG_SECTIONS, new Class[]{SectionCell.class}, new String[]{"righTextView"}, null, null, null, "key_graySectionText");
        themeDescriptionArr[15] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR | ThemeDescription.FLAG_SECTIONS, new Class[]{SectionCell.class}, null, null, null, "graySection");
        themeDescriptionArr[16] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"nameTextView"}, null, null, null, "dialogTextBlack");
        themeDescriptionArr[17] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider");
        themeDescriptionArr[18] = new ThemeDescription(this.listView, 0, new Class[]{TextCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlueText4");
        themeDescriptionArr[19] = new ThemeDescription(this.listView, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, null, null, null, "switchTrackChecked");
        return themeDescriptionArr;
    }
}
