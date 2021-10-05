package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import androidx.collection.LongSparseArray;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collections;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$ChannelParticipant;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$ChatParticipant;
import org.telegram.tgnet.TLRPC$TL_channelParticipant;
import org.telegram.tgnet.TLRPC$TL_channelParticipantAdmin;
import org.telegram.tgnet.TLRPC$TL_channelParticipantCreator;
import org.telegram.tgnet.TLRPC$TL_channelParticipantSelf;
import org.telegram.tgnet.TLRPC$TL_channelParticipantsAdmins;
import org.telegram.tgnet.TLRPC$TL_channelParticipantsBanned;
import org.telegram.tgnet.TLRPC$TL_channelParticipantsBots;
import org.telegram.tgnet.TLRPC$TL_channelParticipantsContacts;
import org.telegram.tgnet.TLRPC$TL_channelParticipantsKicked;
import org.telegram.tgnet.TLRPC$TL_channelParticipantsRecent;
import org.telegram.tgnet.TLRPC$TL_channels_editBanned;
import org.telegram.tgnet.TLRPC$TL_channels_getParticipants;
import org.telegram.tgnet.TLRPC$TL_chatAdminRights;
import org.telegram.tgnet.TLRPC$TL_chatBannedRights;
import org.telegram.tgnet.TLRPC$TL_chatParticipant;
import org.telegram.tgnet.TLRPC$TL_chatParticipantAdmin;
import org.telegram.tgnet.TLRPC$TL_chatParticipantCreator;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_groupCallParticipant;
import org.telegram.tgnet.TLRPC$TL_peerChannel;
import org.telegram.tgnet.TLRPC$TL_peerUser;
import org.telegram.tgnet.TLRPC$Updates;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$UserStatus;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Adapters.SearchAdapterHelper;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ManageChatTextCell;
import org.telegram.ui.Cells.ManageChatUserCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell2;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.ChatRightsEditActivity;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.IntSeekBarAccessibilityDelegate;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SeekBarAccessibilityDelegate;
import org.telegram.ui.Components.StickerEmptyView;
import org.telegram.ui.Components.UndoView;

public class ChatUsersActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public int addNew2Row;
    /* access modifiers changed from: private */
    public int addNewRow;
    /* access modifiers changed from: private */
    public int addNewSectionRow;
    /* access modifiers changed from: private */
    public int addUsersRow;
    /* access modifiers changed from: private */
    public int blockedEmptyRow;
    /* access modifiers changed from: private */
    public int botEndRow;
    /* access modifiers changed from: private */
    public int botHeaderRow;
    /* access modifiers changed from: private */
    public int botStartRow;
    /* access modifiers changed from: private */
    public ArrayList<TLObject> bots = new ArrayList<>();
    private boolean botsEndReached;
    private LongSparseArray<TLObject> botsMap = new LongSparseArray<>();
    /* access modifiers changed from: private */
    public int changeInfoRow;
    /* access modifiers changed from: private */
    public long chatId = this.arguments.getLong("chat_id");
    /* access modifiers changed from: private */
    public ArrayList<TLObject> contacts = new ArrayList<>();
    private boolean contactsEndReached;
    /* access modifiers changed from: private */
    public int contactsEndRow;
    /* access modifiers changed from: private */
    public int contactsHeaderRow;
    /* access modifiers changed from: private */
    public LongSparseArray<TLObject> contactsMap = new LongSparseArray<>();
    /* access modifiers changed from: private */
    public int contactsStartRow;
    /* access modifiers changed from: private */
    public TLRPC$Chat currentChat;
    /* access modifiers changed from: private */
    public TLRPC$TL_chatBannedRights defaultBannedRights = new TLRPC$TL_chatBannedRights();
    private int delayResults;
    /* access modifiers changed from: private */
    public ChatUsersActivityDelegate delegate;
    /* access modifiers changed from: private */
    public ActionBarMenuItem doneItem;
    /* access modifiers changed from: private */
    public int embedLinksRow;
    /* access modifiers changed from: private */
    public StickerEmptyView emptyView;
    /* access modifiers changed from: private */
    public boolean firstLoaded;
    /* access modifiers changed from: private */
    public FlickerLoadingView flickerLoadingView;
    /* access modifiers changed from: private */
    public int gigaConvertRow;
    /* access modifiers changed from: private */
    public int gigaHeaderRow;
    /* access modifiers changed from: private */
    public int gigaInfoRow;
    private LongSparseArray<TLRPC$TL_groupCallParticipant> ignoredUsers;
    /* access modifiers changed from: private */
    public TLRPC$ChatFull info;
    private String initialBannedRights;
    private int initialSlowmode;
    /* access modifiers changed from: private */
    public boolean isChannel;
    private LinearLayoutManager layoutManager;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    /* access modifiers changed from: private */
    public ListAdapter listViewAdapter;
    /* access modifiers changed from: private */
    public int loadingHeaderRow;
    /* access modifiers changed from: private */
    public int loadingProgressRow;
    /* access modifiers changed from: private */
    public int loadingUserCellRow;
    /* access modifiers changed from: private */
    public boolean loadingUsers;
    /* access modifiers changed from: private */
    public int membersHeaderRow;
    private boolean needOpenSearch = this.arguments.getBoolean("open_search");
    private boolean openTransitionStarted;
    /* access modifiers changed from: private */
    public ArrayList<TLObject> participants = new ArrayList<>();
    /* access modifiers changed from: private */
    public int participantsDivider2Row;
    /* access modifiers changed from: private */
    public int participantsDividerRow;
    /* access modifiers changed from: private */
    public int participantsEndRow;
    /* access modifiers changed from: private */
    public int participantsInfoRow;
    /* access modifiers changed from: private */
    public LongSparseArray<TLObject> participantsMap = new LongSparseArray<>();
    /* access modifiers changed from: private */
    public int participantsStartRow;
    /* access modifiers changed from: private */
    public int permissionsSectionRow;
    /* access modifiers changed from: private */
    public int pinMessagesRow;
    /* access modifiers changed from: private */
    public View progressBar;
    /* access modifiers changed from: private */
    public int recentActionsRow;
    /* access modifiers changed from: private */
    public int removedUsersRow;
    /* access modifiers changed from: private */
    public int restricted1SectionRow;
    /* access modifiers changed from: private */
    public int rowCount;
    private ActionBarMenuItem searchItem;
    /* access modifiers changed from: private */
    public SearchAdapter searchListViewAdapter;
    /* access modifiers changed from: private */
    public boolean searching;
    /* access modifiers changed from: private */
    public int selectType = this.arguments.getInt("selectType");
    /* access modifiers changed from: private */
    public int selectedSlowmode;
    /* access modifiers changed from: private */
    public int sendMediaRow;
    /* access modifiers changed from: private */
    public int sendMessagesRow;
    /* access modifiers changed from: private */
    public int sendPollsRow;
    /* access modifiers changed from: private */
    public int sendStickersRow;
    /* access modifiers changed from: private */
    public int slowmodeInfoRow;
    /* access modifiers changed from: private */
    public int slowmodeRow;
    /* access modifiers changed from: private */
    public int slowmodeSelectRow;
    /* access modifiers changed from: private */
    public int type = this.arguments.getInt("type");
    private UndoView undoView;

    public interface ChatUsersActivityDelegate {

        /* renamed from: org.telegram.ui.ChatUsersActivity$ChatUsersActivityDelegate$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static void $default$didChangeOwner(ChatUsersActivityDelegate chatUsersActivityDelegate, TLRPC$User tLRPC$User) {
            }

            public static void $default$didKickParticipant(ChatUsersActivityDelegate chatUsersActivityDelegate, long j) {
            }

            public static void $default$didSelectUser(ChatUsersActivityDelegate chatUsersActivityDelegate, long j) {
            }
        }

        void didAddParticipantToList(long j, TLObject tLObject);

        void didChangeOwner(TLRPC$User tLRPC$User);

        void didKickParticipant(long j);

        void didSelectUser(long j);
    }

    /* access modifiers changed from: private */
    public int getSecondsForIndex(int i) {
        if (i == 1) {
            return 10;
        }
        if (i == 2) {
            return 30;
        }
        if (i == 3) {
            return 60;
        }
        if (i == 4) {
            return 300;
        }
        if (i == 5) {
            return 900;
        }
        return i == 6 ? 3600 : 0;
    }

    public boolean needDelayOpenAnimation() {
        return true;
    }

    private class ChooseView extends View {
        private final SeekBarAccessibilityDelegate accessibilityDelegate;
        private int circleSize;
        private int gapSize;
        private int lineSize;
        private boolean moving;
        private final Paint paint = new Paint(1);
        private int sideSide;
        private ArrayList<Integer> sizes = new ArrayList<>();
        private boolean startMoving;
        private int startMovingItem;
        private float startX;
        /* access modifiers changed from: private */
        public ArrayList<String> strings = new ArrayList<>();
        private final TextPaint textPaint;

        public ChooseView(Context context) {
            super(context);
            String str;
            TextPaint textPaint2 = new TextPaint(1);
            this.textPaint = textPaint2;
            textPaint2.setTextSize((float) AndroidUtilities.dp(13.0f));
            for (int i = 0; i < 7; i++) {
                if (i == 0) {
                    str = LocaleController.getString("SlowmodeOff", NUM);
                } else if (i == 1) {
                    str = LocaleController.formatString("SlowmodeSeconds", NUM, 10);
                } else if (i == 2) {
                    str = LocaleController.formatString("SlowmodeSeconds", NUM, 30);
                } else if (i == 3) {
                    str = LocaleController.formatString("SlowmodeMinutes", NUM, 1);
                } else if (i == 4) {
                    str = LocaleController.formatString("SlowmodeMinutes", NUM, 5);
                } else if (i != 5) {
                    str = LocaleController.formatString("SlowmodeHours", NUM, 1);
                } else {
                    str = LocaleController.formatString("SlowmodeMinutes", NUM, 15);
                }
                this.strings.add(str);
                this.sizes.add(Integer.valueOf((int) Math.ceil((double) this.textPaint.measureText(str))));
            }
            this.accessibilityDelegate = new IntSeekBarAccessibilityDelegate(ChatUsersActivity.this) {
                public int getProgress() {
                    return ChatUsersActivity.this.selectedSlowmode;
                }

                public void setProgress(int i) {
                    ChooseView.this.setItem(i);
                }

                public int getMaxValue() {
                    return ChooseView.this.strings.size() - 1;
                }

                /* access modifiers changed from: protected */
                public CharSequence getContentDescription(View view) {
                    if (ChatUsersActivity.this.selectedSlowmode == 0) {
                        return LocaleController.getString("SlowmodeOff", NUM);
                    }
                    ChatUsersActivity chatUsersActivity = ChatUsersActivity.this;
                    return chatUsersActivity.formatSeconds(chatUsersActivity.getSecondsForIndex(chatUsersActivity.selectedSlowmode));
                }
            };
        }

        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            this.accessibilityDelegate.onInitializeAccessibilityNodeInfoInternal(this, accessibilityNodeInfo);
        }

        public boolean performAccessibilityAction(int i, Bundle bundle) {
            return super.performAccessibilityAction(i, bundle) || this.accessibilityDelegate.performAccessibilityActionInternal(this, i, bundle);
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v3, resolved type: boolean} */
        /* JADX WARNING: type inference failed for: r3v0 */
        /* JADX WARNING: type inference failed for: r3v1, types: [int] */
        /* JADX WARNING: type inference failed for: r3v4 */
        /* JADX WARNING: type inference failed for: r3v5 */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onTouchEvent(android.view.MotionEvent r9) {
            /*
                r8 = this;
                float r0 = r9.getX()
                int r1 = r9.getAction()
                r2 = 1097859072(0x41700000, float:15.0)
                r3 = 0
                r4 = 1
                r5 = 2
                if (r1 != 0) goto L_0x005f
                android.view.ViewParent r9 = r8.getParent()
                r9.requestDisallowInterceptTouchEvent(r4)
                r9 = 0
            L_0x0017:
                java.util.ArrayList<java.lang.String> r1 = r8.strings
                int r1 = r1.size()
                if (r9 >= r1) goto L_0x0124
                int r1 = r8.sideSide
                int r6 = r8.lineSize
                int r7 = r8.gapSize
                int r7 = r7 * 2
                int r6 = r6 + r7
                int r7 = r8.circleSize
                int r6 = r6 + r7
                int r6 = r6 * r9
                int r1 = r1 + r6
                int r7 = r7 / r5
                int r1 = r1 + r7
                int r6 = org.telegram.messenger.AndroidUtilities.dp(r2)
                int r6 = r1 - r6
                float r6 = (float) r6
                int r6 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1))
                if (r6 <= 0) goto L_0x005c
                int r6 = org.telegram.messenger.AndroidUtilities.dp(r2)
                int r1 = r1 + r6
                float r1 = (float) r1
                int r1 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
                if (r1 >= 0) goto L_0x005c
                org.telegram.ui.ChatUsersActivity r1 = org.telegram.ui.ChatUsersActivity.this
                int r1 = r1.selectedSlowmode
                if (r9 != r1) goto L_0x004e
                r3 = 1
            L_0x004e:
                r8.startMoving = r3
                r8.startX = r0
                org.telegram.ui.ChatUsersActivity r9 = org.telegram.ui.ChatUsersActivity.this
                int r9 = r9.selectedSlowmode
                r8.startMovingItem = r9
                goto L_0x0124
            L_0x005c:
                int r9 = r9 + 1
                goto L_0x0017
            L_0x005f:
                int r1 = r9.getAction()
                if (r1 != r5) goto L_0x00be
                boolean r9 = r8.startMoving
                if (r9 == 0) goto L_0x0080
                float r9 = r8.startX
                float r9 = r9 - r0
                float r9 = java.lang.Math.abs(r9)
                r0 = 1056964608(0x3var_, float:0.5)
                float r0 = org.telegram.messenger.AndroidUtilities.getPixelsInCM(r0, r4)
                int r9 = (r9 > r0 ? 1 : (r9 == r0 ? 0 : -1))
                if (r9 < 0) goto L_0x0124
                r8.moving = r4
                r8.startMoving = r3
                goto L_0x0124
            L_0x0080:
                boolean r9 = r8.moving
                if (r9 == 0) goto L_0x0124
            L_0x0084:
                java.util.ArrayList<java.lang.String> r9 = r8.strings
                int r9 = r9.size()
                if (r3 >= r9) goto L_0x0124
                int r9 = r8.sideSide
                int r1 = r8.lineSize
                int r2 = r8.gapSize
                int r6 = r2 * 2
                int r6 = r6 + r1
                int r7 = r8.circleSize
                int r6 = r6 + r7
                int r6 = r6 * r3
                int r9 = r9 + r6
                int r6 = r7 / 2
                int r9 = r9 + r6
                int r1 = r1 / r5
                int r7 = r7 / r5
                int r1 = r1 + r7
                int r1 = r1 + r2
                int r2 = r9 - r1
                float r2 = (float) r2
                int r2 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
                if (r2 <= 0) goto L_0x00bb
                int r9 = r9 + r1
                float r9 = (float) r9
                int r9 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
                if (r9 >= 0) goto L_0x00bb
                org.telegram.ui.ChatUsersActivity r9 = org.telegram.ui.ChatUsersActivity.this
                int r9 = r9.selectedSlowmode
                if (r9 == r3) goto L_0x0124
                r8.setItem(r3)
                goto L_0x0124
            L_0x00bb:
                int r3 = r3 + 1
                goto L_0x0084
            L_0x00be:
                int r1 = r9.getAction()
                if (r1 == r4) goto L_0x00cb
                int r9 = r9.getAction()
                r1 = 3
                if (r9 != r1) goto L_0x0124
            L_0x00cb:
                boolean r9 = r8.moving
                if (r9 != 0) goto L_0x010d
                r9 = 0
            L_0x00d0:
                java.util.ArrayList<java.lang.String> r1 = r8.strings
                int r1 = r1.size()
                if (r9 >= r1) goto L_0x0120
                int r1 = r8.sideSide
                int r6 = r8.lineSize
                int r7 = r8.gapSize
                int r7 = r7 * 2
                int r6 = r6 + r7
                int r7 = r8.circleSize
                int r6 = r6 + r7
                int r6 = r6 * r9
                int r1 = r1 + r6
                int r7 = r7 / r5
                int r1 = r1 + r7
                int r6 = org.telegram.messenger.AndroidUtilities.dp(r2)
                int r6 = r1 - r6
                float r6 = (float) r6
                int r6 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1))
                if (r6 <= 0) goto L_0x010a
                int r6 = org.telegram.messenger.AndroidUtilities.dp(r2)
                int r1 = r1 + r6
                float r1 = (float) r1
                int r1 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
                if (r1 >= 0) goto L_0x010a
                org.telegram.ui.ChatUsersActivity r0 = org.telegram.ui.ChatUsersActivity.this
                int r0 = r0.selectedSlowmode
                if (r0 == r9) goto L_0x0120
                r8.setItem(r9)
                goto L_0x0120
            L_0x010a:
                int r9 = r9 + 1
                goto L_0x00d0
            L_0x010d:
                org.telegram.ui.ChatUsersActivity r9 = org.telegram.ui.ChatUsersActivity.this
                int r9 = r9.selectedSlowmode
                int r0 = r8.startMovingItem
                if (r9 == r0) goto L_0x0120
                org.telegram.ui.ChatUsersActivity r9 = org.telegram.ui.ChatUsersActivity.this
                int r9 = r9.selectedSlowmode
                r8.setItem(r9)
            L_0x0120:
                r8.startMoving = r3
                r8.moving = r3
            L_0x0124:
                return r4
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatUsersActivity.ChooseView.onTouchEvent(android.view.MotionEvent):boolean");
        }

        /* access modifiers changed from: private */
        public void setItem(int i) {
            if (ChatUsersActivity.this.info != null) {
                int unused = ChatUsersActivity.this.selectedSlowmode = i;
                ChatUsersActivity.this.listViewAdapter.notifyItemChanged(ChatUsersActivity.this.slowmodeInfoRow);
                invalidate();
            }
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(74.0f), NUM));
            this.circleSize = AndroidUtilities.dp(6.0f);
            this.gapSize = AndroidUtilities.dp(2.0f);
            this.sideSide = AndroidUtilities.dp(22.0f);
            this.lineSize = (((getMeasuredWidth() - (this.circleSize * this.strings.size())) - ((this.gapSize * 2) * (this.strings.size() - 1))) - (this.sideSide * 2)) / (this.strings.size() - 1);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            this.textPaint.setColor(Theme.getColor("windowBackgroundWhiteGrayText"));
            int measuredHeight = (getMeasuredHeight() / 2) + AndroidUtilities.dp(11.0f);
            int i = 0;
            while (i < this.strings.size()) {
                int i2 = this.sideSide;
                int i3 = this.lineSize + (this.gapSize * 2);
                int i4 = this.circleSize;
                int i5 = i2 + ((i3 + i4) * i) + (i4 / 2);
                if (i <= ChatUsersActivity.this.selectedSlowmode) {
                    this.paint.setColor(Theme.getColor("switchTrackChecked"));
                } else {
                    this.paint.setColor(Theme.getColor("switchTrack"));
                }
                canvas.drawCircle((float) i5, (float) measuredHeight, (float) (i == ChatUsersActivity.this.selectedSlowmode ? AndroidUtilities.dp(6.0f) : this.circleSize / 2), this.paint);
                if (i != 0) {
                    int i6 = (i5 - (this.circleSize / 2)) - this.gapSize;
                    int i7 = this.lineSize;
                    int i8 = i6 - i7;
                    if (i == ChatUsersActivity.this.selectedSlowmode || i == ChatUsersActivity.this.selectedSlowmode + 1) {
                        i7 -= AndroidUtilities.dp(3.0f);
                    }
                    if (i == ChatUsersActivity.this.selectedSlowmode + 1) {
                        i8 += AndroidUtilities.dp(3.0f);
                    }
                    canvas.drawRect((float) i8, (float) (measuredHeight - AndroidUtilities.dp(1.0f)), (float) (i8 + i7), (float) (AndroidUtilities.dp(1.0f) + measuredHeight), this.paint);
                }
                int intValue = this.sizes.get(i).intValue();
                String str = this.strings.get(i);
                if (i == 0) {
                    canvas.drawText(str, (float) AndroidUtilities.dp(22.0f), (float) AndroidUtilities.dp(28.0f), this.textPaint);
                } else if (i == this.strings.size() - 1) {
                    canvas.drawText(str, (float) ((getMeasuredWidth() - intValue) - AndroidUtilities.dp(22.0f)), (float) AndroidUtilities.dp(28.0f), this.textPaint);
                } else {
                    canvas.drawText(str, (float) (i5 - (intValue / 2)), (float) AndroidUtilities.dp(28.0f), this.textPaint);
                }
                i++;
            }
        }
    }

    public ChatUsersActivity(Bundle bundle) {
        super(bundle);
        TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights;
        TLRPC$Chat chat = getMessagesController().getChat(Long.valueOf(this.chatId));
        this.currentChat = chat;
        if (!(chat == null || (tLRPC$TL_chatBannedRights = chat.default_banned_rights) == null)) {
            TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights2 = this.defaultBannedRights;
            tLRPC$TL_chatBannedRights2.view_messages = tLRPC$TL_chatBannedRights.view_messages;
            tLRPC$TL_chatBannedRights2.send_stickers = tLRPC$TL_chatBannedRights.send_stickers;
            tLRPC$TL_chatBannedRights2.send_media = tLRPC$TL_chatBannedRights.send_media;
            tLRPC$TL_chatBannedRights2.embed_links = tLRPC$TL_chatBannedRights.embed_links;
            tLRPC$TL_chatBannedRights2.send_messages = tLRPC$TL_chatBannedRights.send_messages;
            tLRPC$TL_chatBannedRights2.send_games = tLRPC$TL_chatBannedRights.send_games;
            tLRPC$TL_chatBannedRights2.send_inline = tLRPC$TL_chatBannedRights.send_inline;
            tLRPC$TL_chatBannedRights2.send_gifs = tLRPC$TL_chatBannedRights.send_gifs;
            tLRPC$TL_chatBannedRights2.pin_messages = tLRPC$TL_chatBannedRights.pin_messages;
            tLRPC$TL_chatBannedRights2.send_polls = tLRPC$TL_chatBannedRights.send_polls;
            tLRPC$TL_chatBannedRights2.invite_users = tLRPC$TL_chatBannedRights.invite_users;
            tLRPC$TL_chatBannedRights2.change_info = tLRPC$TL_chatBannedRights.change_info;
        }
        this.initialBannedRights = ChatObject.getBannedRightsString(this.defaultBannedRights);
        this.isChannel = ChatObject.isChannel(this.currentChat) && !this.currentChat.megagroup;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:28:0x00fd, code lost:
        if (org.telegram.messenger.ChatObject.canBlockUsers(r0) != false) goto L_0x00ff;
     */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x0127  */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x0175  */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x0189  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateRows() {
        /*
            r6 = this;
            org.telegram.messenger.MessagesController r0 = r6.getMessagesController()
            long r1 = r6.chatId
            java.lang.Long r1 = java.lang.Long.valueOf(r1)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r1)
            r6.currentChat = r0
            if (r0 != 0) goto L_0x0013
            return
        L_0x0013:
            r1 = -1
            r6.recentActionsRow = r1
            r6.addNewRow = r1
            r6.addNew2Row = r1
            r6.addNewSectionRow = r1
            r6.restricted1SectionRow = r1
            r6.participantsStartRow = r1
            r6.participantsDividerRow = r1
            r6.participantsDivider2Row = r1
            r6.gigaInfoRow = r1
            r6.gigaConvertRow = r1
            r6.gigaHeaderRow = r1
            r6.participantsEndRow = r1
            r6.participantsInfoRow = r1
            r6.blockedEmptyRow = r1
            r6.permissionsSectionRow = r1
            r6.sendMessagesRow = r1
            r6.sendMediaRow = r1
            r6.sendStickersRow = r1
            r6.sendPollsRow = r1
            r6.embedLinksRow = r1
            r6.addUsersRow = r1
            r6.pinMessagesRow = r1
            r6.changeInfoRow = r1
            r6.removedUsersRow = r1
            r6.contactsHeaderRow = r1
            r6.contactsStartRow = r1
            r6.contactsEndRow = r1
            r6.botHeaderRow = r1
            r6.botStartRow = r1
            r6.botEndRow = r1
            r6.membersHeaderRow = r1
            r6.slowmodeRow = r1
            r6.slowmodeSelectRow = r1
            r6.slowmodeInfoRow = r1
            r6.loadingProgressRow = r1
            r6.loadingUserCellRow = r1
            r6.loadingHeaderRow = r1
            r2 = 0
            r6.rowCount = r2
            int r3 = r6.type
            r4 = 3
            r5 = 1
            if (r3 != r4) goto L_0x01b2
            r3 = 0
            int r3 = r3 + r5
            r6.rowCount = r3
            r6.permissionsSectionRow = r2
            int r4 = r3 + 1
            r6.rowCount = r4
            r6.sendMessagesRow = r3
            int r3 = r4 + 1
            r6.rowCount = r3
            r6.sendMediaRow = r4
            int r4 = r3 + 1
            r6.rowCount = r4
            r6.sendStickersRow = r3
            int r3 = r4 + 1
            r6.rowCount = r3
            r6.sendPollsRow = r4
            int r4 = r3 + 1
            r6.rowCount = r4
            r6.embedLinksRow = r3
            int r3 = r4 + 1
            r6.rowCount = r3
            r6.addUsersRow = r4
            int r4 = r3 + 1
            r6.rowCount = r4
            r6.pinMessagesRow = r3
            int r3 = r4 + 1
            r6.rowCount = r3
            r6.changeInfoRow = r4
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r0 == 0) goto L_0x00e1
            org.telegram.tgnet.TLRPC$Chat r0 = r6.currentChat
            boolean r3 = r0.creator
            if (r3 == 0) goto L_0x00e1
            boolean r3 = r0.megagroup
            if (r3 == 0) goto L_0x00e1
            boolean r3 = r0.gigagroup
            if (r3 != 0) goto L_0x00e1
            int r0 = r0.participants_count
            org.telegram.tgnet.TLRPC$ChatFull r3 = r6.info
            if (r3 == 0) goto L_0x00b9
            int r2 = r3.participants_count
        L_0x00b9:
            int r0 = java.lang.Math.max(r0, r2)
            org.telegram.messenger.MessagesController r2 = r6.getMessagesController()
            int r2 = r2.maxMegagroupCount
            int r2 = r2 + -1000
            if (r0 < r2) goto L_0x00e1
            int r0 = r6.rowCount
            int r2 = r0 + 1
            r6.rowCount = r2
            r6.participantsDivider2Row = r0
            int r0 = r2 + 1
            r6.rowCount = r0
            r6.gigaHeaderRow = r2
            int r2 = r0 + 1
            r6.rowCount = r2
            r6.gigaConvertRow = r0
            int r0 = r2 + 1
            r6.rowCount = r0
            r6.gigaInfoRow = r2
        L_0x00e1:
            org.telegram.tgnet.TLRPC$Chat r0 = r6.currentChat
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r0 != 0) goto L_0x00ef
            org.telegram.tgnet.TLRPC$Chat r0 = r6.currentChat
            boolean r0 = r0.creator
            if (r0 != 0) goto L_0x00ff
        L_0x00ef:
            org.telegram.tgnet.TLRPC$Chat r0 = r6.currentChat
            boolean r2 = r0.megagroup
            if (r2 == 0) goto L_0x011f
            boolean r2 = r0.gigagroup
            if (r2 != 0) goto L_0x011f
            boolean r0 = org.telegram.messenger.ChatObject.canBlockUsers(r0)
            if (r0 == 0) goto L_0x011f
        L_0x00ff:
            int r0 = r6.participantsDivider2Row
            if (r0 != r1) goto L_0x010b
            int r0 = r6.rowCount
            int r2 = r0 + 1
            r6.rowCount = r2
            r6.participantsDivider2Row = r0
        L_0x010b:
            int r0 = r6.rowCount
            int r2 = r0 + 1
            r6.rowCount = r2
            r6.slowmodeRow = r0
            int r0 = r2 + 1
            r6.rowCount = r0
            r6.slowmodeSelectRow = r2
            int r2 = r0 + 1
            r6.rowCount = r2
            r6.slowmodeInfoRow = r0
        L_0x011f:
            org.telegram.tgnet.TLRPC$Chat r0 = r6.currentChat
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r0 == 0) goto L_0x013b
            int r0 = r6.participantsDivider2Row
            if (r0 != r1) goto L_0x0133
            int r0 = r6.rowCount
            int r2 = r0 + 1
            r6.rowCount = r2
            r6.participantsDivider2Row = r0
        L_0x0133:
            int r0 = r6.rowCount
            int r2 = r0 + 1
            r6.rowCount = r2
            r6.removedUsersRow = r0
        L_0x013b:
            int r0 = r6.slowmodeInfoRow
            if (r0 != r1) goto L_0x0143
            int r0 = r6.gigaHeaderRow
            if (r0 == r1) goto L_0x0147
        L_0x0143:
            int r0 = r6.removedUsersRow
            if (r0 == r1) goto L_0x014f
        L_0x0147:
            int r0 = r6.rowCount
            int r2 = r0 + 1
            r6.rowCount = r2
            r6.participantsDividerRow = r0
        L_0x014f:
            org.telegram.tgnet.TLRPC$Chat r0 = r6.currentChat
            boolean r0 = org.telegram.messenger.ChatObject.canBlockUsers(r0)
            if (r0 == 0) goto L_0x016d
            org.telegram.tgnet.TLRPC$Chat r0 = r6.currentChat
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r0 != 0) goto L_0x0165
            org.telegram.tgnet.TLRPC$Chat r0 = r6.currentChat
            boolean r0 = r0.creator
            if (r0 == 0) goto L_0x016d
        L_0x0165:
            int r0 = r6.rowCount
            int r2 = r0 + 1
            r6.rowCount = r2
            r6.addNewRow = r0
        L_0x016d:
            boolean r0 = r6.loadingUsers
            if (r0 == 0) goto L_0x0189
            boolean r0 = r6.firstLoaded
            if (r0 != 0) goto L_0x0189
            if (r0 != 0) goto L_0x034d
            org.telegram.tgnet.TLRPC$ChatFull r0 = r6.info
            if (r0 == 0) goto L_0x034d
            int r0 = r0.banned_count
            if (r0 <= 0) goto L_0x034d
            int r0 = r6.rowCount
            int r1 = r0 + 1
            r6.rowCount = r1
            r6.loadingUserCellRow = r0
            goto L_0x034d
        L_0x0189:
            java.util.ArrayList<org.telegram.tgnet.TLObject> r0 = r6.participants
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x01a0
            int r0 = r6.rowCount
            r6.participantsStartRow = r0
            java.util.ArrayList<org.telegram.tgnet.TLObject> r2 = r6.participants
            int r2 = r2.size()
            int r0 = r0 + r2
            r6.rowCount = r0
            r6.participantsEndRow = r0
        L_0x01a0:
            int r0 = r6.addNewRow
            if (r0 != r1) goto L_0x01a8
            int r0 = r6.participantsStartRow
            if (r0 == r1) goto L_0x034d
        L_0x01a8:
            int r0 = r6.rowCount
            int r1 = r0 + 1
            r6.rowCount = r1
            r6.addNewSectionRow = r0
            goto L_0x034d
        L_0x01b2:
            if (r3 != 0) goto L_0x0240
            boolean r0 = org.telegram.messenger.ChatObject.canBlockUsers(r0)
            if (r0 == 0) goto L_0x01e2
            int r0 = r6.rowCount
            int r2 = r0 + 1
            r6.rowCount = r2
            r6.addNewRow = r0
            java.util.ArrayList<org.telegram.tgnet.TLObject> r0 = r6.participants
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x01da
            boolean r0 = r6.loadingUsers
            if (r0 == 0) goto L_0x01e2
            boolean r0 = r6.firstLoaded
            if (r0 != 0) goto L_0x01e2
            org.telegram.tgnet.TLRPC$ChatFull r0 = r6.info
            if (r0 == 0) goto L_0x01e2
            int r0 = r0.kicked_count
            if (r0 <= 0) goto L_0x01e2
        L_0x01da:
            int r0 = r6.rowCount
            int r2 = r0 + 1
            r6.rowCount = r2
            r6.participantsInfoRow = r0
        L_0x01e2:
            boolean r0 = r6.loadingUsers
            if (r0 == 0) goto L_0x01fd
            boolean r0 = r6.firstLoaded
            if (r0 == 0) goto L_0x01eb
            goto L_0x01fd
        L_0x01eb:
            if (r0 != 0) goto L_0x034d
            int r0 = r6.rowCount
            int r1 = r0 + 1
            r6.rowCount = r1
            r6.restricted1SectionRow = r0
            int r0 = r1 + 1
            r6.rowCount = r0
            r6.loadingUserCellRow = r1
            goto L_0x034d
        L_0x01fd:
            java.util.ArrayList<org.telegram.tgnet.TLObject> r0 = r6.participants
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x021a
            int r0 = r6.rowCount
            int r2 = r0 + 1
            r6.rowCount = r2
            r6.restricted1SectionRow = r0
            r6.participantsStartRow = r2
            java.util.ArrayList<org.telegram.tgnet.TLObject> r0 = r6.participants
            int r0 = r0.size()
            int r2 = r2 + r0
            r6.rowCount = r2
            r6.participantsEndRow = r2
        L_0x021a:
            int r0 = r6.participantsStartRow
            if (r0 == r1) goto L_0x0236
            int r0 = r6.participantsInfoRow
            if (r0 != r1) goto L_0x022c
            int r0 = r6.rowCount
            int r1 = r0 + 1
            r6.rowCount = r1
            r6.participantsInfoRow = r0
            goto L_0x034d
        L_0x022c:
            int r0 = r6.rowCount
            int r1 = r0 + 1
            r6.rowCount = r1
            r6.addNewSectionRow = r0
            goto L_0x034d
        L_0x0236:
            int r0 = r6.rowCount
            int r1 = r0 + 1
            r6.rowCount = r1
            r6.blockedEmptyRow = r0
            goto L_0x034d
        L_0x0240:
            if (r3 != r5) goto L_0x02b0
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r0 == 0) goto L_0x026a
            org.telegram.tgnet.TLRPC$Chat r0 = r6.currentChat
            boolean r1 = r0.megagroup
            if (r1 == 0) goto L_0x026a
            boolean r0 = r0.gigagroup
            if (r0 != 0) goto L_0x026a
            org.telegram.tgnet.TLRPC$ChatFull r0 = r6.info
            if (r0 == 0) goto L_0x025c
            int r0 = r0.participants_count
            r1 = 200(0xc8, float:2.8E-43)
            if (r0 > r1) goto L_0x026a
        L_0x025c:
            int r0 = r6.rowCount
            int r1 = r0 + 1
            r6.rowCount = r1
            r6.recentActionsRow = r0
            int r0 = r1 + 1
            r6.rowCount = r0
            r6.addNewSectionRow = r1
        L_0x026a:
            org.telegram.tgnet.TLRPC$Chat r0 = r6.currentChat
            boolean r0 = org.telegram.messenger.ChatObject.canAddAdmins(r0)
            if (r0 == 0) goto L_0x027a
            int r0 = r6.rowCount
            int r1 = r0 + 1
            r6.rowCount = r1
            r6.addNewRow = r0
        L_0x027a:
            boolean r0 = r6.loadingUsers
            if (r0 == 0) goto L_0x028f
            boolean r0 = r6.firstLoaded
            if (r0 == 0) goto L_0x0283
            goto L_0x028f
        L_0x0283:
            if (r0 != 0) goto L_0x034d
            int r0 = r6.rowCount
            int r1 = r0 + 1
            r6.rowCount = r1
            r6.loadingUserCellRow = r0
            goto L_0x034d
        L_0x028f:
            java.util.ArrayList<org.telegram.tgnet.TLObject> r0 = r6.participants
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x02a6
            int r0 = r6.rowCount
            r6.participantsStartRow = r0
            java.util.ArrayList<org.telegram.tgnet.TLObject> r1 = r6.participants
            int r1 = r1.size()
            int r0 = r0 + r1
            r6.rowCount = r0
            r6.participantsEndRow = r0
        L_0x02a6:
            int r0 = r6.rowCount
            int r1 = r0 + 1
            r6.rowCount = r1
            r6.participantsInfoRow = r0
            goto L_0x034d
        L_0x02b0:
            r1 = 2
            if (r3 != r1) goto L_0x034d
            int r1 = r6.selectType
            if (r1 != 0) goto L_0x02c5
            boolean r0 = org.telegram.messenger.ChatObject.canAddUsers(r0)
            if (r0 == 0) goto L_0x02c5
            int r0 = r6.rowCount
            int r1 = r0 + 1
            r6.rowCount = r1
            r6.addNewRow = r0
        L_0x02c5:
            boolean r0 = r6.loadingUsers
            if (r0 == 0) goto L_0x02e5
            boolean r0 = r6.firstLoaded
            if (r0 == 0) goto L_0x02ce
            goto L_0x02e5
        L_0x02ce:
            if (r0 != 0) goto L_0x034d
            int r0 = r6.selectType
            if (r0 != 0) goto L_0x02dc
            int r0 = r6.rowCount
            int r1 = r0 + 1
            r6.rowCount = r1
            r6.loadingHeaderRow = r0
        L_0x02dc:
            int r0 = r6.rowCount
            int r1 = r0 + 1
            r6.rowCount = r1
            r6.loadingUserCellRow = r0
            goto L_0x034d
        L_0x02e5:
            java.util.ArrayList<org.telegram.tgnet.TLObject> r0 = r6.contacts
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x0303
            int r0 = r6.rowCount
            int r1 = r0 + 1
            r6.rowCount = r1
            r6.contactsHeaderRow = r0
            r6.contactsStartRow = r1
            java.util.ArrayList<org.telegram.tgnet.TLObject> r0 = r6.contacts
            int r0 = r0.size()
            int r1 = r1 + r0
            r6.rowCount = r1
            r6.contactsEndRow = r1
            r2 = 1
        L_0x0303:
            java.util.ArrayList<org.telegram.tgnet.TLObject> r0 = r6.bots
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x0321
            int r0 = r6.rowCount
            int r1 = r0 + 1
            r6.rowCount = r1
            r6.botHeaderRow = r0
            r6.botStartRow = r1
            java.util.ArrayList<org.telegram.tgnet.TLObject> r0 = r6.bots
            int r0 = r0.size()
            int r1 = r1 + r0
            r6.rowCount = r1
            r6.botEndRow = r1
            goto L_0x0322
        L_0x0321:
            r5 = r2
        L_0x0322:
            java.util.ArrayList<org.telegram.tgnet.TLObject> r0 = r6.participants
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x0343
            if (r5 == 0) goto L_0x0334
            int r0 = r6.rowCount
            int r1 = r0 + 1
            r6.rowCount = r1
            r6.membersHeaderRow = r0
        L_0x0334:
            int r0 = r6.rowCount
            r6.participantsStartRow = r0
            java.util.ArrayList<org.telegram.tgnet.TLObject> r1 = r6.participants
            int r1 = r1.size()
            int r0 = r0 + r1
            r6.rowCount = r0
            r6.participantsEndRow = r0
        L_0x0343:
            int r0 = r6.rowCount
            if (r0 == 0) goto L_0x034d
            int r1 = r0 + 1
            r6.rowCount = r1
            r6.participantsInfoRow = r0
        L_0x034d:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatUsersActivity.updateRows():void");
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        getNotificationCenter().addObserver(this, NotificationCenter.chatInfoDidLoad);
        loadChatParticipants(0, 200);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        getNotificationCenter().removeObserver(this, NotificationCenter.chatInfoDidLoad);
    }

    public View createView(Context context) {
        int i;
        this.searching = false;
        this.actionBar.setBackButtonImage(NUM);
        int i2 = 1;
        this.actionBar.setAllowOverlayTitle(true);
        int i3 = this.type;
        if (i3 == 3) {
            this.actionBar.setTitle(LocaleController.getString("ChannelPermissions", NUM));
        } else if (i3 == 0) {
            this.actionBar.setTitle(LocaleController.getString("ChannelBlacklist", NUM));
        } else if (i3 == 1) {
            this.actionBar.setTitle(LocaleController.getString("ChannelAdministrators", NUM));
        } else if (i3 == 2) {
            int i4 = this.selectType;
            if (i4 == 0) {
                if (this.isChannel) {
                    this.actionBar.setTitle(LocaleController.getString("ChannelSubscribers", NUM));
                } else {
                    this.actionBar.setTitle(LocaleController.getString("ChannelMembers", NUM));
                }
            } else if (i4 == 1) {
                this.actionBar.setTitle(LocaleController.getString("ChannelAddAdmin", NUM));
            } else if (i4 == 2) {
                this.actionBar.setTitle(LocaleController.getString("ChannelBlockUser", NUM));
            } else if (i4 == 3) {
                this.actionBar.setTitle(LocaleController.getString("ChannelAddException", NUM));
            }
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    if (ChatUsersActivity.this.checkDiscard()) {
                        ChatUsersActivity.this.finishFragment();
                    }
                } else if (i == 1) {
                    ChatUsersActivity.this.processDone();
                }
            }
        });
        if (this.selectType != 0 || (i = this.type) == 2 || i == 0 || i == 3) {
            this.searchListViewAdapter = new SearchAdapter(context);
            ActionBarMenu createMenu = this.actionBar.createMenu();
            ActionBarMenuItem actionBarMenuItemSearchListener = createMenu.addItem(0, NUM).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
                public void onSearchExpand() {
                    boolean unused = ChatUsersActivity.this.searching = true;
                    if (ChatUsersActivity.this.doneItem != null) {
                        ChatUsersActivity.this.doneItem.setVisibility(8);
                    }
                }

                public void onSearchCollapse() {
                    ChatUsersActivity.this.searchListViewAdapter.searchUsers((String) null);
                    boolean unused = ChatUsersActivity.this.searching = false;
                    ChatUsersActivity.this.listView.setAnimateEmptyView(false, 0);
                    ChatUsersActivity.this.listView.setAdapter(ChatUsersActivity.this.listViewAdapter);
                    ChatUsersActivity.this.listViewAdapter.notifyDataSetChanged();
                    ChatUsersActivity.this.listView.setFastScrollVisible(true);
                    ChatUsersActivity.this.listView.setVerticalScrollBarEnabled(false);
                    if (ChatUsersActivity.this.doneItem != null) {
                        ChatUsersActivity.this.doneItem.setVisibility(0);
                    }
                }

                public void onTextChanged(EditText editText) {
                    if (ChatUsersActivity.this.searchListViewAdapter != null) {
                        String obj = editText.getText().toString();
                        int itemCount = ChatUsersActivity.this.listView.getAdapter() == null ? 0 : ChatUsersActivity.this.listView.getAdapter().getItemCount();
                        ChatUsersActivity.this.searchListViewAdapter.searchUsers(obj);
                        if (!(!TextUtils.isEmpty(obj) || ChatUsersActivity.this.listView == null || ChatUsersActivity.this.listView.getAdapter() == ChatUsersActivity.this.listViewAdapter)) {
                            ChatUsersActivity.this.listView.setAnimateEmptyView(false, 0);
                            ChatUsersActivity.this.listView.setAdapter(ChatUsersActivity.this.listViewAdapter);
                            if (itemCount == 0) {
                                ChatUsersActivity.this.showItemsAnimated(0);
                            }
                        }
                        ChatUsersActivity.this.progressBar.setVisibility(8);
                        ChatUsersActivity.this.flickerLoadingView.setVisibility(0);
                    }
                }
            });
            this.searchItem = actionBarMenuItemSearchListener;
            if (this.type == 3) {
                actionBarMenuItemSearchListener.setSearchFieldHint(LocaleController.getString("ChannelSearchException", NUM));
            } else {
                actionBarMenuItemSearchListener.setSearchFieldHint(LocaleController.getString("Search", NUM));
            }
            if (!ChatObject.isChannel(this.currentChat) && !this.currentChat.creator) {
                this.searchItem.setVisibility(8);
            }
            if (this.type == 3) {
                this.doneItem = createMenu.addItemWithWidth(1, NUM, AndroidUtilities.dp(56.0f), LocaleController.getString("Done", NUM));
            }
        }
        AnonymousClass3 r1 = new FrameLayout(context) {
            /* access modifiers changed from: protected */
            public void dispatchDraw(Canvas canvas) {
                canvas.drawColor(Theme.getColor(ChatUsersActivity.this.listView.getAdapter() == ChatUsersActivity.this.searchListViewAdapter ? "windowBackgroundWhite" : "windowBackgroundGray"));
                super.dispatchDraw(canvas);
            }
        };
        this.fragmentView = r1;
        FrameLayout frameLayout = r1;
        FrameLayout frameLayout2 = new FrameLayout(context);
        FlickerLoadingView flickerLoadingView2 = new FlickerLoadingView(context);
        this.flickerLoadingView = flickerLoadingView2;
        flickerLoadingView2.setViewType(6);
        this.flickerLoadingView.showDate(false);
        this.flickerLoadingView.setUseHeaderOffset(true);
        frameLayout2.addView(this.flickerLoadingView);
        RadialProgressView radialProgressView = new RadialProgressView(context);
        this.progressBar = radialProgressView;
        frameLayout2.addView(radialProgressView, LayoutHelper.createFrame(-2, -2, 17));
        this.flickerLoadingView.setVisibility(8);
        this.progressBar.setVisibility(8);
        StickerEmptyView stickerEmptyView = new StickerEmptyView(context, frameLayout2, 1);
        this.emptyView = stickerEmptyView;
        stickerEmptyView.title.setText(LocaleController.getString("NoResult", NUM));
        this.emptyView.subtitle.setText(LocaleController.getString("SearchEmptyViewFilteredSubtitle2", NUM));
        this.emptyView.setVisibility(8);
        this.emptyView.setAnimateLayoutChange(true);
        this.emptyView.showProgress(true, false);
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        this.emptyView.addView(frameLayout2, 0);
        AnonymousClass4 r4 = new RecyclerListView(context) {
            public void invalidate() {
                super.invalidate();
                if (ChatUsersActivity.this.fragmentView != null) {
                    ChatUsersActivity.this.fragmentView.invalidate();
                }
            }
        };
        this.listView = r4;
        AnonymousClass5 r5 = new LinearLayoutManager(context, 1, false) {
            public int scrollVerticallyBy(int i, RecyclerView.Recycler recycler, RecyclerView.State state) {
                if (!ChatUsersActivity.this.firstLoaded && ChatUsersActivity.this.type == 0 && ChatUsersActivity.this.participants.size() == 0) {
                    return 0;
                }
                return super.scrollVerticallyBy(i, recycler, state);
            }
        };
        this.layoutManager = r5;
        r4.setLayoutManager(r5);
        AnonymousClass6 r42 = new DefaultItemAnimator() {
            int animationIndex = -1;

            /* access modifiers changed from: protected */
            public long getAddAnimationDelay(long j, long j2, long j3) {
                return 0;
            }

            public long getAddDuration() {
                return 220;
            }

            /* access modifiers changed from: protected */
            public long getMoveAnimationDelay() {
                return 0;
            }

            public long getMoveDuration() {
                return 220;
            }

            public long getRemoveDuration() {
                return 220;
            }

            /* access modifiers changed from: protected */
            public void onAllAnimationsDone() {
                super.onAllAnimationsDone();
                ChatUsersActivity.this.getNotificationCenter().onAnimationFinish(this.animationIndex);
            }

            public void runPendingAnimations() {
                boolean z = !this.mPendingRemovals.isEmpty();
                boolean z2 = !this.mPendingMoves.isEmpty();
                boolean z3 = !this.mPendingChanges.isEmpty();
                boolean z4 = !this.mPendingAdditions.isEmpty();
                if (z || z2 || z4 || z3) {
                    this.animationIndex = ChatUsersActivity.this.getNotificationCenter().setAnimationInProgress(this.animationIndex, (int[]) null);
                }
                super.runPendingAnimations();
            }
        };
        this.listView.setItemAnimator(r42);
        r42.setSupportsChangeAnimations(false);
        this.listView.setAnimateEmptyView(true, 0);
        RecyclerListView recyclerListView = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.listViewAdapter = listAdapter;
        recyclerListView.setAdapter(listAdapter);
        RecyclerListView recyclerListView2 = this.listView;
        if (!LocaleController.isRTL) {
            i2 = 2;
        }
        recyclerListView2.setVerticalScrollbarPosition(i2);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new ChatUsersActivity$$ExternalSyntheticLambda16(this));
        this.listView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new ChatUsersActivity$$ExternalSyntheticLambda17(this));
        if (this.searchItem != null) {
            this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                }

                public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                    if (i == 1) {
                        AndroidUtilities.hideKeyboard(ChatUsersActivity.this.getParentActivity().getCurrentFocus());
                    }
                }
            });
        }
        UndoView undoView2 = new UndoView(context);
        this.undoView = undoView2;
        frameLayout.addView(undoView2, LayoutHelper.createFrame(-1, -2.0f, 83, 8.0f, 0.0f, 8.0f, 8.0f));
        updateRows();
        this.listView.setEmptyView(this.emptyView);
        this.listView.setAnimateEmptyView(false, 0);
        if (this.needOpenSearch) {
            this.searchItem.openSearch(false);
        }
        return this.fragmentView;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v1, resolved type: org.telegram.tgnet.TLObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v0, resolved type: org.telegram.tgnet.TLRPC$TL_chatBannedRights} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v1, resolved type: org.telegram.tgnet.TLRPC$TL_chatBannedRights} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v2, resolved type: org.telegram.tgnet.TLRPC$TL_chatBannedRights} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v3, resolved type: org.telegram.tgnet.TLRPC$TL_chatBannedRights} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v4, resolved type: org.telegram.tgnet.TLRPC$TL_chatBannedRights} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v3, resolved type: org.telegram.tgnet.TLObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v5, resolved type: org.telegram.tgnet.TLObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v6, resolved type: org.telegram.tgnet.TLObject} */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:203:0x03c0  */
    /* JADX WARNING: Removed duplicated region for block: B:259:0x04f6 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:260:0x04f7  */
    /* JADX WARNING: Removed duplicated region for block: B:283:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$createView$1(android.view.View r31, int r32) {
        /*
            r30 = this;
            r10 = r30
            r0 = r32
            org.telegram.ui.Components.RecyclerListView r1 = r10.listView
            androidx.recyclerview.widget.RecyclerView$Adapter r1 = r1.getAdapter()
            org.telegram.ui.ChatUsersActivity$ListAdapter r2 = r10.listViewAdapter
            r3 = 0
            r4 = 1
            if (r1 != r2) goto L_0x0012
            r1 = 1
            goto L_0x0013
        L_0x0012:
            r1 = 0
        L_0x0013:
            java.lang.String r2 = "chat_id"
            r5 = 3
            r6 = 2
            if (r1 == 0) goto L_0x02a8
            int r7 = r10.addNewRow
            java.lang.String r8 = "type"
            if (r0 != r7) goto L_0x00bf
            int r0 = r10.type
            java.lang.String r1 = "selectType"
            if (r0 == 0) goto L_0x0094
            if (r0 != r5) goto L_0x0028
            goto L_0x0094
        L_0x0028:
            if (r0 != r4) goto L_0x0050
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            long r11 = r10.chatId
            r0.putLong(r2, r11)
            r0.putInt(r8, r6)
            r0.putInt(r1, r4)
            org.telegram.ui.ChatUsersActivity r1 = new org.telegram.ui.ChatUsersActivity
            r1.<init>(r0)
            org.telegram.ui.ChatUsersActivity$8 r0 = new org.telegram.ui.ChatUsersActivity$8
            r0.<init>()
            r1.setDelegate(r0)
            org.telegram.tgnet.TLRPC$ChatFull r0 = r10.info
            r1.setInfo(r0)
            r10.presentFragment(r1)
            goto L_0x00be
        L_0x0050:
            if (r0 != r6) goto L_0x00be
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.lang.String r1 = "addToGroup"
            r0.putBoolean(r1, r4)
            boolean r1 = r10.isChannel
            if (r1 == 0) goto L_0x0063
            java.lang.String r1 = "channelId"
            goto L_0x0065
        L_0x0063:
            java.lang.String r1 = "chatId"
        L_0x0065:
            org.telegram.tgnet.TLRPC$Chat r2 = r10.currentChat
            long r2 = r2.id
            r0.putLong(r1, r2)
            org.telegram.ui.GroupCreateActivity r1 = new org.telegram.ui.GroupCreateActivity
            r1.<init>(r0)
            org.telegram.tgnet.TLRPC$ChatFull r0 = r10.info
            r1.setInfo(r0)
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLObject> r0 = r10.contactsMap
            if (r0 == 0) goto L_0x0083
            int r0 = r0.size()
            if (r0 == 0) goto L_0x0083
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLObject> r0 = r10.contactsMap
            goto L_0x0085
        L_0x0083:
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLObject> r0 = r10.participantsMap
        L_0x0085:
            r1.setIgnoreUsers(r0)
            org.telegram.ui.ChatUsersActivity$9 r0 = new org.telegram.ui.ChatUsersActivity$9
            r0.<init>()
            r1.setDelegate((org.telegram.ui.GroupCreateActivity.ContactsAddActivityDelegate) r0)
            r10.presentFragment(r1)
            goto L_0x00be
        L_0x0094:
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            long r3 = r10.chatId
            r0.putLong(r2, r3)
            r0.putInt(r8, r6)
            int r2 = r10.type
            if (r2 != 0) goto L_0x00a6
            r5 = 2
        L_0x00a6:
            r0.putInt(r1, r5)
            org.telegram.ui.ChatUsersActivity r1 = new org.telegram.ui.ChatUsersActivity
            r1.<init>(r0)
            org.telegram.tgnet.TLRPC$ChatFull r0 = r10.info
            r1.setInfo(r0)
            org.telegram.ui.ChatUsersActivity$7 r0 = new org.telegram.ui.ChatUsersActivity$7
            r0.<init>()
            r1.setDelegate(r0)
            r10.presentFragment(r1)
        L_0x00be:
            return
        L_0x00bf:
            int r7 = r10.recentActionsRow
            if (r0 != r7) goto L_0x00ce
            org.telegram.ui.ChannelAdminLogActivity r0 = new org.telegram.ui.ChannelAdminLogActivity
            org.telegram.tgnet.TLRPC$Chat r1 = r10.currentChat
            r0.<init>(r1)
            r10.presentFragment(r0)
            return
        L_0x00ce:
            int r7 = r10.removedUsersRow
            if (r0 != r7) goto L_0x00ed
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            long r4 = r10.chatId
            r0.putLong(r2, r4)
            r0.putInt(r8, r3)
            org.telegram.ui.ChatUsersActivity r1 = new org.telegram.ui.ChatUsersActivity
            r1.<init>(r0)
            org.telegram.tgnet.TLRPC$ChatFull r0 = r10.info
            r1.setInfo(r0)
            r10.presentFragment(r1)
            return
        L_0x00ed:
            int r7 = r10.gigaConvertRow
            if (r0 != r7) goto L_0x00ff
            org.telegram.ui.ChatUsersActivity$10 r7 = new org.telegram.ui.ChatUsersActivity$10
            android.app.Activity r8 = r30.getParentActivity()
            r7.<init>(r8, r10)
            r10.showDialog(r7)
            goto L_0x02a8
        L_0x00ff:
            int r7 = r10.addNew2Row
            if (r0 != r7) goto L_0x010e
            org.telegram.ui.GroupInviteActivity r0 = new org.telegram.ui.GroupInviteActivity
            long r1 = r10.chatId
            r0.<init>(r1)
            r10.presentFragment(r0)
            return
        L_0x010e:
            int r7 = r10.permissionsSectionRow
            if (r0 <= r7) goto L_0x02a8
            int r7 = r10.changeInfoRow
            if (r0 > r7) goto L_0x02a8
            r1 = r31
            org.telegram.ui.Cells.TextCheckCell2 r1 = (org.telegram.ui.Cells.TextCheckCell2) r1
            boolean r2 = r1.isEnabled()
            if (r2 != 0) goto L_0x0121
            return
        L_0x0121:
            boolean r2 = r1.hasIcon()
            if (r2 == 0) goto L_0x0163
            org.telegram.tgnet.TLRPC$Chat r1 = r10.currentChat
            java.lang.String r1 = r1.username
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x014e
            int r1 = r10.pinMessagesRow
            if (r0 == r1) goto L_0x0139
            int r1 = r10.changeInfoRow
            if (r0 != r1) goto L_0x014e
        L_0x0139:
            org.telegram.ui.Components.BulletinFactory r0 = org.telegram.ui.Components.BulletinFactory.of(r30)
            r1 = 2131625311(0x7f0e055f, float:1.8877826E38)
            java.lang.String r2 = "EditCantEditPermissionsPublic"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            org.telegram.ui.Components.Bulletin r0 = r0.createErrorBulletin(r1)
            r0.show()
            goto L_0x0162
        L_0x014e:
            org.telegram.ui.Components.BulletinFactory r0 = org.telegram.ui.Components.BulletinFactory.of(r30)
            r1 = 2131625310(0x7f0e055e, float:1.8877824E38)
            java.lang.String r2 = "EditCantEditPermissions"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            org.telegram.ui.Components.Bulletin r0 = r0.createErrorBulletin(r1)
            r0.show()
        L_0x0162:
            return
        L_0x0163:
            boolean r2 = r1.isChecked()
            r2 = r2 ^ r4
            r1.setChecked(r2)
            int r2 = r10.changeInfoRow
            if (r0 != r2) goto L_0x0178
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r0 = r10.defaultBannedRights
            boolean r1 = r0.change_info
            r1 = r1 ^ r4
            r0.change_info = r1
            goto L_0x02a7
        L_0x0178:
            int r2 = r10.addUsersRow
            if (r0 != r2) goto L_0x0185
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r0 = r10.defaultBannedRights
            boolean r1 = r0.invite_users
            r1 = r1 ^ r4
            r0.invite_users = r1
            goto L_0x02a7
        L_0x0185:
            int r2 = r10.pinMessagesRow
            if (r0 != r2) goto L_0x0192
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r0 = r10.defaultBannedRights
            boolean r1 = r0.pin_messages
            r1 = r1 ^ r4
            r0.pin_messages = r1
            goto L_0x02a7
        L_0x0192:
            boolean r1 = r1.isChecked()
            r1 = r1 ^ r4
            int r2 = r10.sendMessagesRow
            if (r0 != r2) goto L_0x01a3
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r0 = r10.defaultBannedRights
            boolean r5 = r0.send_messages
            r5 = r5 ^ r4
            r0.send_messages = r5
            goto L_0x01d8
        L_0x01a3:
            int r5 = r10.sendMediaRow
            if (r0 != r5) goto L_0x01af
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r0 = r10.defaultBannedRights
            boolean r5 = r0.send_media
            r5 = r5 ^ r4
            r0.send_media = r5
            goto L_0x01d8
        L_0x01af:
            int r5 = r10.sendStickersRow
            if (r0 != r5) goto L_0x01c1
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r0 = r10.defaultBannedRights
            boolean r5 = r0.send_stickers
            r5 = r5 ^ r4
            r0.send_inline = r5
            r0.send_gifs = r5
            r0.send_games = r5
            r0.send_stickers = r5
            goto L_0x01d8
        L_0x01c1:
            int r5 = r10.embedLinksRow
            if (r0 != r5) goto L_0x01cd
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r0 = r10.defaultBannedRights
            boolean r5 = r0.embed_links
            r5 = r5 ^ r4
            r0.embed_links = r5
            goto L_0x01d8
        L_0x01cd:
            int r5 = r10.sendPollsRow
            if (r0 != r5) goto L_0x01d8
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r0 = r10.defaultBannedRights
            boolean r5 = r0.send_polls
            r5 = r5 ^ r4
            r0.send_polls = r5
        L_0x01d8:
            if (r1 == 0) goto L_0x0280
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r0 = r10.defaultBannedRights
            boolean r1 = r0.view_messages
            if (r1 == 0) goto L_0x01f5
            boolean r1 = r0.send_messages
            if (r1 != 0) goto L_0x01f5
            r0.send_messages = r4
            org.telegram.ui.Components.RecyclerListView r0 = r10.listView
            androidx.recyclerview.widget.RecyclerView$ViewHolder r0 = r0.findViewHolderForAdapterPosition(r2)
            if (r0 == 0) goto L_0x01f5
            android.view.View r0 = r0.itemView
            org.telegram.ui.Cells.TextCheckCell2 r0 = (org.telegram.ui.Cells.TextCheckCell2) r0
            r0.setChecked(r3)
        L_0x01f5:
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r0 = r10.defaultBannedRights
            boolean r1 = r0.view_messages
            if (r1 != 0) goto L_0x01ff
            boolean r1 = r0.send_messages
            if (r1 == 0) goto L_0x0216
        L_0x01ff:
            boolean r1 = r0.send_media
            if (r1 != 0) goto L_0x0216
            r0.send_media = r4
            org.telegram.ui.Components.RecyclerListView r0 = r10.listView
            int r1 = r10.sendMediaRow
            androidx.recyclerview.widget.RecyclerView$ViewHolder r0 = r0.findViewHolderForAdapterPosition(r1)
            if (r0 == 0) goto L_0x0216
            android.view.View r0 = r0.itemView
            org.telegram.ui.Cells.TextCheckCell2 r0 = (org.telegram.ui.Cells.TextCheckCell2) r0
            r0.setChecked(r3)
        L_0x0216:
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r0 = r10.defaultBannedRights
            boolean r1 = r0.view_messages
            if (r1 != 0) goto L_0x0220
            boolean r1 = r0.send_messages
            if (r1 == 0) goto L_0x0237
        L_0x0220:
            boolean r1 = r0.send_polls
            if (r1 != 0) goto L_0x0237
            r0.send_polls = r4
            org.telegram.ui.Components.RecyclerListView r0 = r10.listView
            int r1 = r10.sendPollsRow
            androidx.recyclerview.widget.RecyclerView$ViewHolder r0 = r0.findViewHolderForAdapterPosition(r1)
            if (r0 == 0) goto L_0x0237
            android.view.View r0 = r0.itemView
            org.telegram.ui.Cells.TextCheckCell2 r0 = (org.telegram.ui.Cells.TextCheckCell2) r0
            r0.setChecked(r3)
        L_0x0237:
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r0 = r10.defaultBannedRights
            boolean r1 = r0.view_messages
            if (r1 != 0) goto L_0x0241
            boolean r1 = r0.send_messages
            if (r1 == 0) goto L_0x025e
        L_0x0241:
            boolean r1 = r0.send_stickers
            if (r1 != 0) goto L_0x025e
            r0.send_inline = r4
            r0.send_gifs = r4
            r0.send_games = r4
            r0.send_stickers = r4
            org.telegram.ui.Components.RecyclerListView r0 = r10.listView
            int r1 = r10.sendStickersRow
            androidx.recyclerview.widget.RecyclerView$ViewHolder r0 = r0.findViewHolderForAdapterPosition(r1)
            if (r0 == 0) goto L_0x025e
            android.view.View r0 = r0.itemView
            org.telegram.ui.Cells.TextCheckCell2 r0 = (org.telegram.ui.Cells.TextCheckCell2) r0
            r0.setChecked(r3)
        L_0x025e:
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r0 = r10.defaultBannedRights
            boolean r1 = r0.view_messages
            if (r1 != 0) goto L_0x0268
            boolean r1 = r0.send_messages
            if (r1 == 0) goto L_0x02a7
        L_0x0268:
            boolean r1 = r0.embed_links
            if (r1 != 0) goto L_0x02a7
            r0.embed_links = r4
            org.telegram.ui.Components.RecyclerListView r0 = r10.listView
            int r1 = r10.embedLinksRow
            androidx.recyclerview.widget.RecyclerView$ViewHolder r0 = r0.findViewHolderForAdapterPosition(r1)
            if (r0 == 0) goto L_0x02a7
            android.view.View r0 = r0.itemView
            org.telegram.ui.Cells.TextCheckCell2 r0 = (org.telegram.ui.Cells.TextCheckCell2) r0
            r0.setChecked(r3)
            goto L_0x02a7
        L_0x0280:
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r0 = r10.defaultBannedRights
            boolean r1 = r0.embed_links
            if (r1 == 0) goto L_0x0292
            boolean r1 = r0.send_inline
            if (r1 == 0) goto L_0x0292
            boolean r1 = r0.send_media
            if (r1 == 0) goto L_0x0292
            boolean r1 = r0.send_polls
            if (r1 != 0) goto L_0x02a7
        L_0x0292:
            boolean r1 = r0.send_messages
            if (r1 == 0) goto L_0x02a7
            r0.send_messages = r3
            org.telegram.ui.Components.RecyclerListView r0 = r10.listView
            androidx.recyclerview.widget.RecyclerView$ViewHolder r0 = r0.findViewHolderForAdapterPosition(r2)
            if (r0 == 0) goto L_0x02a7
            android.view.View r0 = r0.itemView
            org.telegram.ui.Cells.TextCheckCell2 r0 = (org.telegram.ui.Cells.TextCheckCell2) r0
            r0.setChecked(r4)
        L_0x02a7:
            return
        L_0x02a8:
            r7 = 0
            r9 = 0
            java.lang.String r11 = ""
            if (r1 == 0) goto L_0x034c
            org.telegram.ui.ChatUsersActivity$ListAdapter r1 = r10.listViewAdapter
            org.telegram.tgnet.TLObject r0 = r1.getItem(r0)
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$ChannelParticipant
            if (r1 == 0) goto L_0x030a
            r1 = r0
            org.telegram.tgnet.TLRPC$ChannelParticipant r1 = (org.telegram.tgnet.TLRPC$ChannelParticipant) r1
            org.telegram.tgnet.TLRPC$Peer r11 = r1.peer
            long r11 = org.telegram.messenger.MessageObject.getPeerId(r11)
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r13 = r1.banned_rights
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r14 = r1.admin_rights
            java.lang.String r15 = r1.rank
            boolean r6 = r1 instanceof org.telegram.tgnet.TLRPC$TL_channelParticipantAdmin
            if (r6 != 0) goto L_0x02d0
            boolean r6 = r1 instanceof org.telegram.tgnet.TLRPC$TL_channelParticipantCreator
            if (r6 == 0) goto L_0x02d4
        L_0x02d0:
            boolean r1 = r1.can_edit
            if (r1 == 0) goto L_0x02d6
        L_0x02d4:
            r1 = 1
            goto L_0x02d7
        L_0x02d6:
            r1 = 0
        L_0x02d7:
            boolean r6 = r0 instanceof org.telegram.tgnet.TLRPC$TL_channelParticipantCreator
            if (r6 == 0) goto L_0x02fd
            r6 = r0
            org.telegram.tgnet.TLRPC$TL_channelParticipantCreator r6 = (org.telegram.tgnet.TLRPC$TL_channelParticipantCreator) r6
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r14 = r6.admin_rights
            if (r14 != 0) goto L_0x02fd
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r14 = new org.telegram.tgnet.TLRPC$TL_chatAdminRights
            r14.<init>()
            r14.add_admins = r4
            r14.pin_messages = r4
            r14.invite_users = r4
            r14.ban_users = r4
            r14.delete_messages = r4
            r14.edit_messages = r4
            r14.post_messages = r4
            r14.change_info = r4
            boolean r6 = r10.isChannel
            if (r6 != 0) goto L_0x02fd
            r14.manage_call = r4
        L_0x02fd:
            r6 = r0
            r21 = r14
            r24 = r15
            r14 = r1
        L_0x0303:
            r28 = r11
            r11 = r13
            r12 = r28
            goto L_0x03bc
        L_0x030a:
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$ChatParticipant
            if (r1 == 0) goto L_0x0341
            r1 = r0
            org.telegram.tgnet.TLRPC$ChatParticipant r1 = (org.telegram.tgnet.TLRPC$ChatParticipant) r1
            long r12 = r1.user_id
            org.telegram.tgnet.TLRPC$Chat r1 = r10.currentChat
            boolean r1 = r1.creator
            boolean r6 = r0 instanceof org.telegram.tgnet.TLRPC$TL_chatParticipantCreator
            if (r6 == 0) goto L_0x0337
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r6 = new org.telegram.tgnet.TLRPC$TL_chatAdminRights
            r6.<init>()
            r6.add_admins = r4
            r6.pin_messages = r4
            r6.invite_users = r4
            r6.ban_users = r4
            r6.delete_messages = r4
            r6.edit_messages = r4
            r6.post_messages = r4
            r6.change_info = r4
            boolean r14 = r10.isChannel
            if (r14 != 0) goto L_0x0338
            r6.manage_call = r4
            goto L_0x0338
        L_0x0337:
            r6 = r9
        L_0x0338:
            r14 = r1
            r21 = r6
            r24 = r11
            r6 = r0
            r11 = r9
            goto L_0x03bc
        L_0x0341:
            r6 = r0
            r12 = r7
            r21 = r9
            r24 = r11
        L_0x0347:
            r14 = 0
        L_0x0348:
            r11 = r21
            goto L_0x03bc
        L_0x034c:
            org.telegram.ui.ChatUsersActivity$SearchAdapter r1 = r10.searchListViewAdapter
            org.telegram.tgnet.TLObject r0 = r1.getItem(r0)
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$User
            if (r1 == 0) goto L_0x0368
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            org.telegram.messenger.MessagesController r1 = r30.getMessagesController()
            r1.putUser(r0, r3)
            long r0 = r0.id
            org.telegram.tgnet.TLObject r6 = r10.getAnyParticipant(r0)
            r12 = r0
            r0 = r6
            goto L_0x0375
        L_0x0368:
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$ChannelParticipant
            if (r1 != 0) goto L_0x0374
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$ChatParticipant
            if (r1 == 0) goto L_0x0371
            goto L_0x0374
        L_0x0371:
            r12 = r7
            r0 = r9
            goto L_0x0375
        L_0x0374:
            r12 = r7
        L_0x0375:
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$ChannelParticipant
            if (r1 == 0) goto L_0x039f
            r1 = r0
            org.telegram.tgnet.TLRPC$ChannelParticipant r1 = (org.telegram.tgnet.TLRPC$ChannelParticipant) r1
            org.telegram.tgnet.TLRPC$Peer r6 = r1.peer
            long r11 = org.telegram.messenger.MessageObject.getPeerId(r6)
            boolean r6 = r1 instanceof org.telegram.tgnet.TLRPC$TL_channelParticipantAdmin
            if (r6 != 0) goto L_0x038a
            boolean r6 = r1 instanceof org.telegram.tgnet.TLRPC$TL_channelParticipantCreator
            if (r6 == 0) goto L_0x038e
        L_0x038a:
            boolean r6 = r1.can_edit
            if (r6 == 0) goto L_0x0390
        L_0x038e:
            r6 = 1
            goto L_0x0391
        L_0x0390:
            r6 = 0
        L_0x0391:
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r13 = r1.banned_rights
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r14 = r1.admin_rights
            java.lang.String r1 = r1.rank
            r24 = r1
            r21 = r14
            r14 = r6
            r6 = r0
            goto L_0x0303
        L_0x039f:
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$ChatParticipant
            if (r1 == 0) goto L_0x03b3
            r1 = r0
            org.telegram.tgnet.TLRPC$ChatParticipant r1 = (org.telegram.tgnet.TLRPC$ChatParticipant) r1
            long r12 = r1.user_id
            org.telegram.tgnet.TLRPC$Chat r1 = r10.currentChat
            boolean r1 = r1.creator
            r6 = r0
            r14 = r1
            r21 = r9
            r24 = r11
            goto L_0x0348
        L_0x03b3:
            r6 = r0
            r21 = r9
            r24 = r11
            if (r0 != 0) goto L_0x0347
            r14 = 1
            goto L_0x0348
        L_0x03bc:
            int r0 = (r12 > r7 ? 1 : (r12 == r7 ? 0 : -1))
            if (r0 == 0) goto L_0x0512
            int r0 = r10.selectType
            if (r0 == 0) goto L_0x045b
            if (r0 == r5) goto L_0x03ce
            if (r0 != r4) goto L_0x03c9
            goto L_0x03ce
        L_0x03c9:
            r10.removeParticipant(r12)
            goto L_0x0512
        L_0x03ce:
            if (r0 == r4) goto L_0x043f
            if (r14 == 0) goto L_0x043f
            boolean r1 = r6 instanceof org.telegram.tgnet.TLRPC$TL_channelParticipantAdmin
            if (r1 != 0) goto L_0x03da
            boolean r1 = r6 instanceof org.telegram.tgnet.TLRPC$TL_chatParticipantAdmin
            if (r1 == 0) goto L_0x043f
        L_0x03da:
            org.telegram.messenger.MessagesController r0 = r30.getMessagesController()
            java.lang.Long r1 = java.lang.Long.valueOf(r12)
            org.telegram.tgnet.TLRPC$User r2 = r0.getUser(r1)
            org.telegram.ui.ActionBar.AlertDialog$Builder r8 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r0 = r30.getParentActivity()
            r8.<init>((android.content.Context) r0)
            r0 = 2131624288(0x7f0e0160, float:1.8875751E38)
            java.lang.String r1 = "AppName"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r8.setTitle(r0)
            r0 = 2131624240(0x7f0e0130, float:1.8875654E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            java.lang.String r4 = org.telegram.messenger.UserObject.getUserName(r2)
            r1[r3] = r4
            java.lang.String r3 = "AdminWillBeRemoved"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r3, r0, r1)
            r8.setMessage(r0)
            r0 = 2131626643(0x7f0e0a93, float:1.8880528E38)
            java.lang.String r1 = "OK"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r1, r0)
            org.telegram.ui.ChatUsersActivity$$ExternalSyntheticLambda4 r13 = new org.telegram.ui.ChatUsersActivity$$ExternalSyntheticLambda4
            r0 = r13
            r1 = r30
            r3 = r6
            r4 = r21
            r5 = r11
            r6 = r24
            r7 = r14
            r0.<init>(r1, r2, r3, r4, r5, r6, r7)
            r8.setPositiveButton(r12, r13)
            r0 = 2131624665(0x7f0e02d9, float:1.8876516E38)
            java.lang.String r1 = "Cancel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r8.setNegativeButton(r0, r9)
            org.telegram.ui.ActionBar.AlertDialog r0 = r8.create()
            r10.showDialog(r0)
            goto L_0x0512
        L_0x043f:
            if (r0 != r4) goto L_0x0443
            r8 = 0
            goto L_0x0444
        L_0x0443:
            r8 = 1
        L_0x0444:
            if (r0 == r4) goto L_0x044b
            if (r0 != r5) goto L_0x0449
            goto L_0x044b
        L_0x0449:
            r9 = 0
            goto L_0x044c
        L_0x044b:
            r9 = 1
        L_0x044c:
            r0 = r30
            r1 = r12
            r3 = r6
            r4 = r21
            r5 = r11
            r6 = r24
            r7 = r14
            r0.openRightsEdit(r1, r3, r4, r5, r6, r7, r8, r9)
            goto L_0x0512
        L_0x045b:
            int r0 = r10.type
            if (r0 != r4) goto L_0x0477
            org.telegram.messenger.UserConfig r0 = r30.getUserConfig()
            long r0 = r0.getClientUserId()
            int r5 = (r12 > r0 ? 1 : (r12 == r0 ? 0 : -1))
            if (r5 == 0) goto L_0x0475
            org.telegram.tgnet.TLRPC$Chat r0 = r10.currentChat
            boolean r0 = r0.creator
            if (r0 != 0) goto L_0x0473
            if (r14 == 0) goto L_0x0475
        L_0x0473:
            r0 = 1
            goto L_0x0485
        L_0x0475:
            r0 = 0
            goto L_0x0485
        L_0x0477:
            if (r0 == 0) goto L_0x047f
            if (r0 != r5) goto L_0x047c
            goto L_0x047f
        L_0x047c:
            r26 = 0
            goto L_0x0487
        L_0x047f:
            org.telegram.tgnet.TLRPC$Chat r0 = r10.currentChat
            boolean r0 = org.telegram.messenger.ChatObject.canBlockUsers(r0)
        L_0x0485:
            r26 = r0
        L_0x0487:
            int r0 = r10.type
            if (r0 == 0) goto L_0x04ea
            if (r0 == r4) goto L_0x0491
            boolean r1 = r10.isChannel
            if (r1 != 0) goto L_0x04ea
        L_0x0491:
            r1 = 2
            if (r0 != r1) goto L_0x0499
            int r0 = r10.selectType
            if (r0 != 0) goto L_0x0499
            goto L_0x04ea
        L_0x0499:
            if (r11 != 0) goto L_0x04bb
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r0 = new org.telegram.tgnet.TLRPC$TL_chatBannedRights
            r0.<init>()
            r0.view_messages = r4
            r0.send_stickers = r4
            r0.send_media = r4
            r0.embed_links = r4
            r0.send_messages = r4
            r0.send_games = r4
            r0.send_inline = r4
            r0.send_gifs = r4
            r0.pin_messages = r4
            r0.send_polls = r4
            r0.invite_users = r4
            r0.change_info = r4
            r23 = r0
            goto L_0x04bd
        L_0x04bb:
            r23 = r11
        L_0x04bd:
            org.telegram.ui.ChatRightsEditActivity r0 = new org.telegram.ui.ChatRightsEditActivity
            long r1 = r10.chatId
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r5 = r10.defaultBannedRights
            int r7 = r10.type
            if (r7 != r4) goto L_0x04ca
            r25 = 0
            goto L_0x04cc
        L_0x04ca:
            r25 = 1
        L_0x04cc:
            if (r6 != 0) goto L_0x04d1
            r27 = 1
            goto L_0x04d3
        L_0x04d1:
            r27 = 0
        L_0x04d3:
            r16 = r0
            r17 = r12
            r19 = r1
            r22 = r5
            r16.<init>(r17, r19, r21, r22, r23, r24, r25, r26, r27)
            org.telegram.ui.ChatUsersActivity$11 r1 = new org.telegram.ui.ChatUsersActivity$11
            r1.<init>(r6)
            r0.setDelegate(r1)
            r10.presentFragment(r0)
            goto L_0x0512
        L_0x04ea:
            org.telegram.messenger.UserConfig r0 = r30.getUserConfig()
            long r0 = r0.getClientUserId()
            int r3 = (r12 > r0 ? 1 : (r12 == r0 ? 0 : -1))
            if (r3 != 0) goto L_0x04f7
            return
        L_0x04f7:
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            int r1 = (r12 > r7 ? 1 : (r12 == r7 ? 0 : -1))
            if (r1 <= 0) goto L_0x0506
            java.lang.String r1 = "user_id"
            r0.putLong(r1, r12)
            goto L_0x050a
        L_0x0506:
            long r3 = -r12
            r0.putLong(r2, r3)
        L_0x050a:
            org.telegram.ui.ProfileActivity r1 = new org.telegram.ui.ProfileActivity
            r1.<init>(r0)
            r10.presentFragment(r1)
        L_0x0512:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatUsersActivity.lambda$createView$1(android.view.View, int):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$0(TLRPC$User tLRPC$User, TLObject tLObject, TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights, TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights, String str, boolean z, DialogInterface dialogInterface, int i) {
        openRightsEdit(tLRPC$User.id, tLObject, tLRPC$TL_chatAdminRights, tLRPC$TL_chatBannedRights, str, z, this.selectType == 1 ? 0 : 1, false);
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0007, code lost:
        r3 = r2.listView.getAdapter();
        r1 = r2.listViewAdapter;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ boolean lambda$createView$2(android.view.View r3, int r4) {
        /*
            r2 = this;
            android.app.Activity r3 = r2.getParentActivity()
            r0 = 0
            if (r3 == 0) goto L_0x001c
            org.telegram.ui.Components.RecyclerListView r3 = r2.listView
            androidx.recyclerview.widget.RecyclerView$Adapter r3 = r3.getAdapter()
            org.telegram.ui.ChatUsersActivity$ListAdapter r1 = r2.listViewAdapter
            if (r3 != r1) goto L_0x001c
            org.telegram.tgnet.TLObject r3 = r1.getItem(r4)
            boolean r3 = r2.createMenuForParticipant(r3, r0)
            if (r3 == 0) goto L_0x001c
            r0 = 1
        L_0x001c:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatUsersActivity.lambda$createView$2(android.view.View, int):boolean");
    }

    /* access modifiers changed from: private */
    public void sortAdmins(ArrayList<TLObject> arrayList) {
        Collections.sort(arrayList, new ChatUsersActivity$$ExternalSyntheticLambda10(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ int lambda$sortAdmins$3(TLObject tLObject, TLObject tLObject2) {
        int channelAdminParticipantType = getChannelAdminParticipantType(tLObject);
        int channelAdminParticipantType2 = getChannelAdminParticipantType(tLObject2);
        if (channelAdminParticipantType > channelAdminParticipantType2) {
            return 1;
        }
        if (channelAdminParticipantType < channelAdminParticipantType2) {
            return -1;
        }
        if (!(tLObject instanceof TLRPC$ChannelParticipant) || !(tLObject2 instanceof TLRPC$ChannelParticipant)) {
            return 0;
        }
        return (int) (MessageObject.getPeerId(((TLRPC$ChannelParticipant) tLObject).peer) - MessageObject.getPeerId(((TLRPC$ChannelParticipant) tLObject2).peer));
    }

    /* access modifiers changed from: private */
    public void showItemsAnimated(final int i) {
        if (!this.isPaused && this.openTransitionStarted) {
            if (this.listView.getAdapter() != this.listViewAdapter || !this.firstLoaded) {
                final View view = null;
                for (int i2 = 0; i2 < this.listView.getChildCount(); i2++) {
                    View childAt = this.listView.getChildAt(i2);
                    if (childAt instanceof FlickerLoadingView) {
                        view = childAt;
                    }
                }
                if (view != null) {
                    this.listView.removeView(view);
                    i--;
                }
                this.listView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    public boolean onPreDraw() {
                        ChatUsersActivity.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
                        int childCount = ChatUsersActivity.this.listView.getChildCount();
                        AnimatorSet animatorSet = new AnimatorSet();
                        for (int i = 0; i < childCount; i++) {
                            View childAt = ChatUsersActivity.this.listView.getChildAt(i);
                            if (childAt != view && ChatUsersActivity.this.listView.getChildAdapterPosition(childAt) >= i) {
                                childAt.setAlpha(0.0f);
                                ObjectAnimator ofFloat = ObjectAnimator.ofFloat(childAt, View.ALPHA, new float[]{0.0f, 1.0f});
                                ofFloat.setStartDelay((long) ((int) ((((float) Math.min(ChatUsersActivity.this.listView.getMeasuredHeight(), Math.max(0, childAt.getTop()))) / ((float) ChatUsersActivity.this.listView.getMeasuredHeight())) * 100.0f)));
                                ofFloat.setDuration(200);
                                animatorSet.playTogether(new Animator[]{ofFloat});
                            }
                        }
                        View view = view;
                        if (view != null && view.getParent() == null) {
                            ChatUsersActivity.this.listView.addView(view);
                            final RecyclerView.LayoutManager layoutManager = ChatUsersActivity.this.listView.getLayoutManager();
                            if (layoutManager != null) {
                                layoutManager.ignoreView(view);
                                View view2 = view;
                                ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(view2, View.ALPHA, new float[]{view2.getAlpha(), 0.0f});
                                ofFloat2.addListener(new AnimatorListenerAdapter() {
                                    public void onAnimationEnd(Animator animator) {
                                        view.setAlpha(1.0f);
                                        layoutManager.stopIgnoringView(view);
                                        ChatUsersActivity.this.listView.removeView(view);
                                    }
                                });
                                ofFloat2.start();
                            }
                        }
                        animatorSet.start();
                        return true;
                    }
                });
            }
        }
    }

    /* access modifiers changed from: private */
    public void onOwnerChaged(TLRPC$User tLRPC$User) {
        ArrayList<TLObject> arrayList;
        LongSparseArray<TLObject> longSparseArray;
        boolean z;
        this.undoView.showWithAction(-this.chatId, this.isChannel ? 9 : 10, (Object) tLRPC$User);
        this.currentChat.creator = false;
        boolean z2 = false;
        for (int i = 0; i < 3; i++) {
            boolean z3 = true;
            if (i == 0) {
                longSparseArray = this.contactsMap;
                arrayList = this.contacts;
            } else if (i == 1) {
                longSparseArray = this.botsMap;
                arrayList = this.bots;
            } else {
                longSparseArray = this.participantsMap;
                arrayList = this.participants;
            }
            TLObject tLObject = longSparseArray.get(tLRPC$User.id);
            if (tLObject instanceof TLRPC$ChannelParticipant) {
                TLRPC$TL_channelParticipantCreator tLRPC$TL_channelParticipantCreator = new TLRPC$TL_channelParticipantCreator();
                TLRPC$TL_peerUser tLRPC$TL_peerUser = new TLRPC$TL_peerUser();
                tLRPC$TL_channelParticipantCreator.peer = tLRPC$TL_peerUser;
                long j = tLRPC$User.id;
                tLRPC$TL_peerUser.user_id = j;
                longSparseArray.put(j, tLRPC$TL_channelParticipantCreator);
                int indexOf = arrayList.indexOf(tLObject);
                if (indexOf >= 0) {
                    arrayList.set(indexOf, tLRPC$TL_channelParticipantCreator);
                }
                z2 = true;
                z = true;
            } else {
                z = false;
            }
            long clientUserId = getUserConfig().getClientUserId();
            TLObject tLObject2 = longSparseArray.get(clientUserId);
            if (tLObject2 instanceof TLRPC$ChannelParticipant) {
                TLRPC$TL_channelParticipantAdmin tLRPC$TL_channelParticipantAdmin = new TLRPC$TL_channelParticipantAdmin();
                TLRPC$TL_peerUser tLRPC$TL_peerUser2 = new TLRPC$TL_peerUser();
                tLRPC$TL_channelParticipantAdmin.peer = tLRPC$TL_peerUser2;
                tLRPC$TL_peerUser2.user_id = clientUserId;
                tLRPC$TL_channelParticipantAdmin.self = true;
                tLRPC$TL_channelParticipantAdmin.inviter_id = clientUserId;
                tLRPC$TL_channelParticipantAdmin.promoted_by = clientUserId;
                tLRPC$TL_channelParticipantAdmin.date = (int) (System.currentTimeMillis() / 1000);
                TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights = new TLRPC$TL_chatAdminRights();
                tLRPC$TL_channelParticipantAdmin.admin_rights = tLRPC$TL_chatAdminRights;
                tLRPC$TL_chatAdminRights.add_admins = true;
                tLRPC$TL_chatAdminRights.pin_messages = true;
                tLRPC$TL_chatAdminRights.invite_users = true;
                tLRPC$TL_chatAdminRights.ban_users = true;
                tLRPC$TL_chatAdminRights.delete_messages = true;
                tLRPC$TL_chatAdminRights.edit_messages = true;
                tLRPC$TL_chatAdminRights.post_messages = true;
                tLRPC$TL_chatAdminRights.change_info = true;
                if (!this.isChannel) {
                    tLRPC$TL_chatAdminRights.manage_call = true;
                }
                longSparseArray.put(clientUserId, tLRPC$TL_channelParticipantAdmin);
                int indexOf2 = arrayList.indexOf(tLObject2);
                if (indexOf2 >= 0) {
                    arrayList.set(indexOf2, tLRPC$TL_channelParticipantAdmin);
                }
            } else {
                z3 = z;
            }
            if (z3) {
                Collections.sort(arrayList, new ChatUsersActivity$$ExternalSyntheticLambda9(this));
            }
        }
        if (!z2) {
            TLRPC$TL_channelParticipantCreator tLRPC$TL_channelParticipantCreator2 = new TLRPC$TL_channelParticipantCreator();
            TLRPC$TL_peerUser tLRPC$TL_peerUser3 = new TLRPC$TL_peerUser();
            tLRPC$TL_channelParticipantCreator2.peer = tLRPC$TL_peerUser3;
            long j2 = tLRPC$User.id;
            tLRPC$TL_peerUser3.user_id = j2;
            this.participantsMap.put(j2, tLRPC$TL_channelParticipantCreator2);
            this.participants.add(tLRPC$TL_channelParticipantCreator2);
            sortAdmins(this.participants);
            updateRows();
        }
        this.listViewAdapter.notifyDataSetChanged();
        ChatUsersActivityDelegate chatUsersActivityDelegate = this.delegate;
        if (chatUsersActivityDelegate != null) {
            chatUsersActivityDelegate.didChangeOwner(tLRPC$User);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ int lambda$onOwnerChaged$4(TLObject tLObject, TLObject tLObject2) {
        int channelAdminParticipantType = getChannelAdminParticipantType(tLObject);
        int channelAdminParticipantType2 = getChannelAdminParticipantType(tLObject2);
        if (channelAdminParticipantType > channelAdminParticipantType2) {
            return 1;
        }
        return channelAdminParticipantType < channelAdminParticipantType2 ? -1 : 0;
    }

    private void openRightsEdit2(long j, int i, TLObject tLObject, TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights, TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights, String str, boolean z, int i2, boolean z2) {
        TLObject tLObject2 = tLObject;
        boolean[] zArr = new boolean[1];
        boolean z3 = (tLObject2 instanceof TLRPC$TL_channelParticipantAdmin) || (tLObject2 instanceof TLRPC$TL_chatParticipantAdmin);
        AnonymousClass14 r17 = r0;
        final boolean[] zArr2 = zArr;
        final long j2 = j;
        AnonymousClass14 r0 = new ChatRightsEditActivity(this, j, this.chatId, tLRPC$TL_chatAdminRights, this.defaultBannedRights, tLRPC$TL_chatBannedRights, str, i2, true, false) {
            final /* synthetic */ ChatUsersActivity this$0;

            {
                this.this$0 = r14;
            }

            /* access modifiers changed from: protected */
            public void onTransitionAnimationEnd(boolean z, boolean z2) {
                if (!z && z2 && zArr2[0] && BulletinFactory.canShowBulletin(this.this$0)) {
                    if (j2 > 0) {
                        TLRPC$User user = getMessagesController().getUser(Long.valueOf(j2));
                        if (user != null) {
                            BulletinFactory.createPromoteToAdminBulletin(this.this$0, user.first_name).show();
                            return;
                        }
                        return;
                    }
                    TLRPC$Chat chat = getMessagesController().getChat(Long.valueOf(-j2));
                    if (chat != null) {
                        BulletinFactory.createPromoteToAdminBulletin(this.this$0, chat.title).show();
                    }
                }
            }
        };
        final int i3 = i2;
        final long j3 = j;
        final int i4 = i;
        final boolean z4 = z3;
        final boolean[] zArr3 = zArr;
        AnonymousClass14 r02 = r17;
        r02.setDelegate(new ChatRightsEditActivity.ChatRightsEditActivityDelegate() {
            public void didSetRights(int i, TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights, TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights, String str) {
                TLRPC$ChatParticipant tLRPC$ChatParticipant;
                TLRPC$ChannelParticipant tLRPC$ChannelParticipant;
                int i2 = i3;
                if (i2 == 0) {
                    int i3 = 0;
                    while (true) {
                        if (i3 >= ChatUsersActivity.this.participants.size()) {
                            break;
                        }
                        TLObject tLObject = (TLObject) ChatUsersActivity.this.participants.get(i3);
                        if (tLObject instanceof TLRPC$ChannelParticipant) {
                            if (MessageObject.getPeerId(((TLRPC$ChannelParticipant) tLObject).peer) == j3) {
                                if (i == 1) {
                                    tLRPC$ChannelParticipant = new TLRPC$TL_channelParticipantAdmin();
                                } else {
                                    tLRPC$ChannelParticipant = new TLRPC$TL_channelParticipant();
                                }
                                tLRPC$ChannelParticipant.admin_rights = tLRPC$TL_chatAdminRights;
                                tLRPC$ChannelParticipant.banned_rights = tLRPC$TL_chatBannedRights;
                                tLRPC$ChannelParticipant.inviter_id = ChatUsersActivity.this.getUserConfig().getClientUserId();
                                if (j3 > 0) {
                                    TLRPC$TL_peerUser tLRPC$TL_peerUser = new TLRPC$TL_peerUser();
                                    tLRPC$ChannelParticipant.peer = tLRPC$TL_peerUser;
                                    tLRPC$TL_peerUser.user_id = j3;
                                } else {
                                    TLRPC$TL_peerChannel tLRPC$TL_peerChannel = new TLRPC$TL_peerChannel();
                                    tLRPC$ChannelParticipant.peer = tLRPC$TL_peerChannel;
                                    tLRPC$TL_peerChannel.channel_id = -j3;
                                }
                                tLRPC$ChannelParticipant.date = i4;
                                tLRPC$ChannelParticipant.flags |= 4;
                                tLRPC$ChannelParticipant.rank = str;
                                ChatUsersActivity.this.participants.set(i3, tLRPC$ChannelParticipant);
                            }
                        } else if (tLObject instanceof TLRPC$ChatParticipant) {
                            TLRPC$ChatParticipant tLRPC$ChatParticipant2 = (TLRPC$ChatParticipant) tLObject;
                            if (i == 1) {
                                tLRPC$ChatParticipant = new TLRPC$TL_chatParticipantAdmin();
                            } else {
                                tLRPC$ChatParticipant = new TLRPC$TL_chatParticipant();
                            }
                            tLRPC$ChatParticipant.user_id = tLRPC$ChatParticipant2.user_id;
                            tLRPC$ChatParticipant.date = tLRPC$ChatParticipant2.date;
                            tLRPC$ChatParticipant.inviter_id = tLRPC$ChatParticipant2.inviter_id;
                            int indexOf = ChatUsersActivity.this.info.participants.participants.indexOf(tLRPC$ChatParticipant2);
                            if (indexOf >= 0) {
                                ChatUsersActivity.this.info.participants.participants.set(indexOf, tLRPC$ChatParticipant);
                            }
                            ChatUsersActivity.this.loadChatParticipants(0, 200);
                        }
                        i3++;
                    }
                    if (i == 1 && !z4) {
                        zArr3[0] = true;
                    }
                } else if (i2 == 1 && i == 0) {
                    ChatUsersActivity.this.removeParticipants(j3);
                }
            }

            public void didChangeOwner(TLRPC$User tLRPC$User) {
                ChatUsersActivity.this.onOwnerChaged(tLRPC$User);
            }
        });
        presentFragment(r02);
    }

    public boolean canBeginSlide() {
        return checkDiscard();
    }

    /* access modifiers changed from: private */
    public void openRightsEdit(long j, TLObject tLObject, TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights, TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights, String str, boolean z, int i, boolean z2) {
        final TLObject tLObject2 = tLObject;
        final long j2 = j;
        ChatRightsEditActivity chatRightsEditActivity = new ChatRightsEditActivity(j, this.chatId, tLRPC$TL_chatAdminRights, this.defaultBannedRights, tLRPC$TL_chatBannedRights, str, i, z, tLObject == null);
        final boolean z3 = z2;
        chatRightsEditActivity.setDelegate(new ChatRightsEditActivity.ChatRightsEditActivityDelegate() {
            public void didSetRights(int i, TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights, TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights, String str) {
                TLObject tLObject = tLObject2;
                if (tLObject instanceof TLRPC$ChannelParticipant) {
                    TLRPC$ChannelParticipant tLRPC$ChannelParticipant = (TLRPC$ChannelParticipant) tLObject;
                    tLRPC$ChannelParticipant.admin_rights = tLRPC$TL_chatAdminRights;
                    tLRPC$ChannelParticipant.banned_rights = tLRPC$TL_chatBannedRights;
                    tLRPC$ChannelParticipant.rank = str;
                }
                if (ChatUsersActivity.this.delegate != null && i == 1) {
                    ChatUsersActivity.this.delegate.didSelectUser(j2);
                } else if (ChatUsersActivity.this.delegate != null) {
                    ChatUsersActivity.this.delegate.didAddParticipantToList(j2, tLObject2);
                }
                if (z3) {
                    ChatUsersActivity.this.removeSelfFromStack();
                }
            }

            public void didChangeOwner(TLRPC$User tLRPC$User) {
                ChatUsersActivity.this.onOwnerChaged(tLRPC$User);
            }
        });
        presentFragment(chatRightsEditActivity, z2);
    }

    private void removeParticipant(long j) {
        if (ChatObject.isChannel(this.currentChat)) {
            getMessagesController().deleteParticipantFromChat(this.chatId, getMessagesController().getUser(Long.valueOf(j)), (TLRPC$ChatFull) null);
            ChatUsersActivityDelegate chatUsersActivityDelegate = this.delegate;
            if (chatUsersActivityDelegate != null) {
                chatUsersActivityDelegate.didKickParticipant(j);
            }
            finishFragment();
        }
    }

    private TLObject getAnyParticipant(long j) {
        LongSparseArray<TLObject> longSparseArray;
        for (int i = 0; i < 3; i++) {
            if (i == 0) {
                longSparseArray = this.contactsMap;
            } else if (i == 1) {
                longSparseArray = this.botsMap;
            } else {
                longSparseArray = this.participantsMap;
            }
            TLObject tLObject = longSparseArray.get(j);
            if (tLObject != null) {
                return tLObject;
            }
        }
        return null;
    }

    private void removeParticipants(TLObject tLObject) {
        if (tLObject instanceof TLRPC$ChatParticipant) {
            removeParticipants(((TLRPC$ChatParticipant) tLObject).user_id);
        } else if (tLObject instanceof TLRPC$ChannelParticipant) {
            removeParticipants(MessageObject.getPeerId(((TLRPC$ChannelParticipant) tLObject).peer));
        }
    }

    /* access modifiers changed from: private */
    public void removeParticipants(long j) {
        ArrayList<TLObject> arrayList;
        LongSparseArray<TLObject> longSparseArray;
        DiffCallback saveState = saveState();
        boolean z = false;
        for (int i = 0; i < 3; i++) {
            if (i == 0) {
                longSparseArray = this.contactsMap;
                arrayList = this.contacts;
            } else if (i == 1) {
                longSparseArray = this.botsMap;
                arrayList = this.bots;
            } else {
                longSparseArray = this.participantsMap;
                arrayList = this.participants;
            }
            TLObject tLObject = longSparseArray.get(j);
            if (tLObject != null) {
                longSparseArray.remove(j);
                arrayList.remove(tLObject);
                if (this.type == 0) {
                    this.info.kicked_count--;
                }
                z = true;
            }
        }
        if (z) {
            updateListAnimated(saveState);
        }
        RecyclerView.Adapter adapter = this.listView.getAdapter();
        SearchAdapter searchAdapter = this.searchListViewAdapter;
        if (adapter == searchAdapter) {
            searchAdapter.removeUserId(j);
        }
    }

    /* access modifiers changed from: private */
    public void updateParticipantWithRights(TLRPC$ChannelParticipant tLRPC$ChannelParticipant, TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights, TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights, long j, boolean z) {
        LongSparseArray<TLObject> longSparseArray;
        ChatUsersActivityDelegate chatUsersActivityDelegate;
        boolean z2 = false;
        for (int i = 0; i < 3; i++) {
            if (i == 0) {
                longSparseArray = this.contactsMap;
            } else if (i == 1) {
                longSparseArray = this.botsMap;
            } else {
                longSparseArray = this.participantsMap;
            }
            TLObject tLObject = longSparseArray.get(MessageObject.getPeerId(tLRPC$ChannelParticipant.peer));
            if (tLObject instanceof TLRPC$ChannelParticipant) {
                tLRPC$ChannelParticipant = (TLRPC$ChannelParticipant) tLObject;
                tLRPC$ChannelParticipant.admin_rights = tLRPC$TL_chatAdminRights;
                tLRPC$ChannelParticipant.banned_rights = tLRPC$TL_chatBannedRights;
                if (z) {
                    tLRPC$ChannelParticipant.promoted_by = getUserConfig().getClientUserId();
                }
            }
            if (z && tLObject != null && !z2 && (chatUsersActivityDelegate = this.delegate) != null) {
                chatUsersActivityDelegate.didAddParticipantToList(j, tLObject);
                z2 = true;
            }
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x02a3 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:125:0x02a4  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean createMenuForParticipant(org.telegram.tgnet.TLObject r23, boolean r24) {
        /*
            r22 = this;
            r12 = r22
            r7 = r23
            if (r7 == 0) goto L_0x02dd
            int r1 = r12.selectType
            if (r1 == 0) goto L_0x000c
            goto L_0x02dd
        L_0x000c:
            boolean r1 = r7 instanceof org.telegram.tgnet.TLRPC$ChannelParticipant
            r2 = 0
            if (r1 == 0) goto L_0x0029
            r1 = r7
            org.telegram.tgnet.TLRPC$ChannelParticipant r1 = (org.telegram.tgnet.TLRPC$ChannelParticipant) r1
            org.telegram.tgnet.TLRPC$Peer r5 = r1.peer
            long r5 = org.telegram.messenger.MessageObject.getPeerId(r5)
            boolean r8 = r1.can_edit
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r9 = r1.banned_rights
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r10 = r1.admin_rights
            int r11 = r1.date
            java.lang.String r1 = r1.rank
            r13 = r9
            r9 = r11
            r11 = r1
            goto L_0x0047
        L_0x0029:
            boolean r1 = r7 instanceof org.telegram.tgnet.TLRPC$ChatParticipant
            if (r1 == 0) goto L_0x0041
            r1 = r7
            org.telegram.tgnet.TLRPC$ChatParticipant r1 = (org.telegram.tgnet.TLRPC$ChatParticipant) r1
            long r5 = r1.user_id
            int r1 = r1.date
            org.telegram.tgnet.TLRPC$Chat r8 = r12.currentChat
            boolean r8 = org.telegram.messenger.ChatObject.canAddAdmins(r8)
            java.lang.String r9 = ""
            r11 = r9
            r10 = 0
            r13 = 0
            r9 = r1
            goto L_0x0047
        L_0x0041:
            r5 = r2
            r8 = 0
            r9 = 0
            r10 = 0
            r11 = 0
            r13 = 0
        L_0x0047:
            int r1 = (r5 > r2 ? 1 : (r5 == r2 ? 0 : -1))
            if (r1 == 0) goto L_0x02da
            org.telegram.messenger.UserConfig r1 = r22.getUserConfig()
            long r14 = r1.getClientUserId()
            int r1 = (r5 > r14 ? 1 : (r5 == r14 ? 0 : -1))
            if (r1 != 0) goto L_0x0059
            goto L_0x02da
        L_0x0059:
            int r1 = r12.type
            r15 = 2131625297(0x7f0e0551, float:1.8877798E38)
            java.lang.String r4 = "EditAdminRights"
            java.lang.String r17 = "dialogRedIcon"
            java.lang.String r18 = "dialogTextRed2"
            r2 = 2
            r3 = 1
            if (r1 != r2) goto L_0x01cf
            org.telegram.messenger.MessagesController r1 = r22.getMessagesController()
            java.lang.Long r2 = java.lang.Long.valueOf(r5)
            org.telegram.tgnet.TLRPC$User r19 = r1.getUser(r2)
            org.telegram.tgnet.TLRPC$Chat r1 = r12.currentChat
            boolean r1 = org.telegram.messenger.ChatObject.canAddAdmins(r1)
            if (r1 == 0) goto L_0x008c
            boolean r1 = r7 instanceof org.telegram.tgnet.TLRPC$TL_channelParticipant
            if (r1 != 0) goto L_0x008a
            boolean r1 = r7 instanceof org.telegram.tgnet.TLRPC$TL_channelParticipantBanned
            if (r1 != 0) goto L_0x008a
            boolean r1 = r7 instanceof org.telegram.tgnet.TLRPC$TL_chatParticipant
            if (r1 != 0) goto L_0x008a
            if (r8 == 0) goto L_0x008c
        L_0x008a:
            r1 = 1
            goto L_0x008d
        L_0x008c:
            r1 = 0
        L_0x008d:
            boolean r2 = r7 instanceof org.telegram.tgnet.TLRPC$TL_channelParticipantAdmin
            if (r2 != 0) goto L_0x009d
            boolean r14 = r7 instanceof org.telegram.tgnet.TLRPC$TL_channelParticipantCreator
            if (r14 != 0) goto L_0x009d
            boolean r14 = r7 instanceof org.telegram.tgnet.TLRPC$TL_chatParticipantCreator
            if (r14 != 0) goto L_0x009d
            boolean r14 = r7 instanceof org.telegram.tgnet.TLRPC$TL_chatParticipantAdmin
            if (r14 == 0) goto L_0x009f
        L_0x009d:
            if (r8 == 0) goto L_0x00a1
        L_0x009f:
            r8 = 1
            goto L_0x00a2
        L_0x00a1:
            r8 = 0
        L_0x00a2:
            if (r2 != 0) goto L_0x00ab
            boolean r2 = r7 instanceof org.telegram.tgnet.TLRPC$TL_chatParticipantAdmin
            if (r2 == 0) goto L_0x00a9
            goto L_0x00ab
        L_0x00a9:
            r2 = 0
            goto L_0x00ac
        L_0x00ab:
            r2 = 1
        L_0x00ac:
            int r14 = r12.selectType
            if (r14 != 0) goto L_0x00b6
            boolean r14 = org.telegram.messenger.UserObject.isDeleted(r19)
            r14 = r14 ^ r3
            r1 = r1 & r14
        L_0x00b6:
            if (r24 != 0) goto L_0x00cd
            java.util.ArrayList r14 = new java.util.ArrayList
            r14.<init>()
            java.util.ArrayList r16 = new java.util.ArrayList
            r16.<init>()
            java.util.ArrayList r20 = new java.util.ArrayList
            r20.<init>()
            r0 = r14
            r14 = r16
            r21 = r20
            goto L_0x00d1
        L_0x00cd:
            r0 = 0
            r14 = 0
            r21 = 0
        L_0x00d1:
            if (r1 == 0) goto L_0x00fe
            if (r24 == 0) goto L_0x00d6
            return r3
        L_0x00d6:
            if (r2 == 0) goto L_0x00dd
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r4, r15)
            goto L_0x00e6
        L_0x00dd:
            r1 = 2131627628(0x7f0e0e6c, float:1.8882526E38)
            java.lang.String r2 = "SetAsAdmin"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
        L_0x00e6:
            r0.add(r1)
            r1 = 2131165247(0x7var_f, float:1.7944706E38)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r2 = r21
            r2.add(r1)
            r1 = 0
            java.lang.Integer r4 = java.lang.Integer.valueOf(r1)
            r14.add(r4)
            goto L_0x0100
        L_0x00fe:
            r2 = r21
        L_0x0100:
            org.telegram.tgnet.TLRPC$Chat r1 = r12.currentChat
            boolean r1 = org.telegram.messenger.ChatObject.canBlockUsers(r1)
            if (r1 == 0) goto L_0x0169
            if (r8 == 0) goto L_0x0169
            if (r24 == 0) goto L_0x010d
            return r3
        L_0x010d:
            boolean r1 = r12.isChannel
            if (r1 != 0) goto L_0x0149
            org.telegram.tgnet.TLRPC$Chat r1 = r12.currentChat
            boolean r1 = org.telegram.messenger.ChatObject.isChannel(r1)
            if (r1 == 0) goto L_0x013c
            org.telegram.tgnet.TLRPC$Chat r1 = r12.currentChat
            boolean r1 = r1.gigagroup
            if (r1 != 0) goto L_0x013c
            r1 = 2131624698(0x7f0e02fa, float:1.8876583E38)
            java.lang.String r4 = "ChangePermissions"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r4, r1)
            r0.add(r1)
            r1 = 2131165253(0x7var_, float:1.7944718E38)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r2.add(r1)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r3)
            r14.add(r1)
        L_0x013c:
            r1 = 2131626009(0x7f0e0819, float:1.8879242E38)
            java.lang.String r4 = "KickFromGroup"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r4, r1)
            r0.add(r1)
            goto L_0x0155
        L_0x0149:
            r1 = 2131624791(0x7f0e0357, float:1.8876772E38)
            java.lang.String r4 = "ChannelRemoveUser"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r4, r1)
            r0.add(r1)
        L_0x0155:
            r1 = 2131165254(0x7var_, float:1.794472E38)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r2.add(r1)
            r1 = 2
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r14.add(r1)
            r15 = 1
            goto L_0x016a
        L_0x0169:
            r15 = 0
        L_0x016a:
            if (r14 == 0) goto L_0x01cc
            boolean r1 = r14.isEmpty()
            if (r1 == 0) goto L_0x0173
            goto L_0x01cc
        L_0x0173:
            org.telegram.ui.ActionBar.AlertDialog$Builder r4 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r1 = r22.getParentActivity()
            r4.<init>((android.content.Context) r1)
            int r1 = r14.size()
            java.lang.CharSequence[] r1 = new java.lang.CharSequence[r1]
            java.lang.Object[] r1 = r0.toArray(r1)
            java.lang.CharSequence[] r1 = (java.lang.CharSequence[]) r1
            int[] r2 = org.telegram.messenger.AndroidUtilities.toIntArray(r2)
            r24 = r15
            org.telegram.ui.ChatUsersActivity$$ExternalSyntheticLambda3 r15 = new org.telegram.ui.ChatUsersActivity$$ExternalSyntheticLambda3
            r16 = r0
            r0 = r15
            r12 = r1
            r1 = r22
            r20 = r12
            r12 = r2
            r2 = r14
            r14 = 1
            r3 = r19
            r14 = r4
            r4 = r5
            r6 = r8
            r7 = r23
            r8 = r9
            r9 = r10
            r10 = r13
            r0.<init>(r1, r2, r3, r4, r6, r7, r8, r9, r10, r11)
            r1 = r20
            r14.setItems(r1, r12, r15)
            org.telegram.ui.ActionBar.AlertDialog r0 = r14.create()
            r9 = r22
            r9.showDialog(r0)
            if (r24 == 0) goto L_0x01c9
            int r1 = r16.size()
            r2 = 1
            int r1 = r1 - r2
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r18)
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r17)
            r0.setItemColor(r1, r2, r3)
        L_0x01c9:
            r2 = 1
            goto L_0x02d9
        L_0x01cc:
            r9 = r12
            r0 = 0
            return r0
        L_0x01cf:
            r9 = r12
            r0 = 3
            r2 = 2131624737(0x7f0e0321, float:1.8876662E38)
            java.lang.String r3 = "ChannelDeleteFromList"
            if (r1 != r0) goto L_0x0204
            org.telegram.tgnet.TLRPC$Chat r0 = r9.currentChat
            boolean r0 = org.telegram.messenger.ChatObject.canBlockUsers(r0)
            if (r0 == 0) goto L_0x0204
            if (r24 == 0) goto L_0x01e4
            r0 = 1
            return r0
        L_0x01e4:
            r0 = 1
            r1 = 2
            java.lang.CharSequence[] r4 = new java.lang.CharSequence[r1]
            r8 = 2131624744(0x7f0e0328, float:1.8876676E38)
            java.lang.String r12 = "ChannelEditPermissions"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r12, r8)
            r12 = 0
            r4[r12] = r8
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r4[r0] = r2
            int[] r0 = new int[r1]
            r0 = {NUM, NUM} // fill-array
            r14 = r0
            r12 = r4
        L_0x0201:
            r3 = 0
            goto L_0x02a1
        L_0x0204:
            int r0 = r9.type
            if (r0 != 0) goto L_0x024e
            org.telegram.tgnet.TLRPC$Chat r0 = r9.currentChat
            boolean r0 = org.telegram.messenger.ChatObject.canBlockUsers(r0)
            if (r0 == 0) goto L_0x024e
            if (r24 == 0) goto L_0x0214
            r0 = 1
            return r0
        L_0x0214:
            r0 = 2
            java.lang.CharSequence[] r1 = new java.lang.CharSequence[r0]
            org.telegram.tgnet.TLRPC$Chat r0 = r9.currentChat
            boolean r0 = org.telegram.messenger.ChatObject.canAddUsers(r0)
            if (r0 == 0) goto L_0x023a
            r14 = 0
            int r0 = (r5 > r14 ? 1 : (r5 == r14 ? 0 : -1))
            if (r0 <= 0) goto L_0x023a
            boolean r0 = r9.isChannel
            if (r0 == 0) goto L_0x022f
            r0 = 2131624712(0x7f0e0308, float:1.8876611E38)
            java.lang.String r4 = "ChannelAddToChannel"
            goto L_0x0234
        L_0x022f:
            r0 = 2131624713(0x7f0e0309, float:1.8876613E38)
            java.lang.String r4 = "ChannelAddToGroup"
        L_0x0234:
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r0)
            r0 = 0
            goto L_0x023c
        L_0x023a:
            r0 = 0
            r4 = 0
        L_0x023c:
            r1[r0] = r4
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r2 = 1
            r1[r2] = r0
            r0 = 2
            int[] r4 = new int[r0]
            r4 = {NUM, NUM} // fill-array
            r12 = r1
            r14 = r4
            goto L_0x0201
        L_0x024e:
            r2 = 1
            int r0 = r9.type
            if (r0 != r2) goto L_0x029e
            org.telegram.tgnet.TLRPC$Chat r0 = r9.currentChat
            boolean r0 = org.telegram.messenger.ChatObject.canAddAdmins(r0)
            if (r0 == 0) goto L_0x029e
            if (r8 == 0) goto L_0x029e
            if (r24 == 0) goto L_0x0260
            return r2
        L_0x0260:
            org.telegram.tgnet.TLRPC$Chat r0 = r9.currentChat
            boolean r0 = r0.creator
            r1 = 2131624792(0x7f0e0358, float:1.8876774E38)
            java.lang.String r2 = "ChannelRemoveUserAdmin"
            if (r0 != 0) goto L_0x0286
            boolean r0 = r7 instanceof org.telegram.tgnet.TLRPC$TL_channelParticipantCreator
            if (r0 != 0) goto L_0x0272
            if (r8 == 0) goto L_0x0272
            goto L_0x0286
        L_0x0272:
            r0 = 1
            java.lang.CharSequence[] r4 = new java.lang.CharSequence[r0]
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r3 = 0
            r4[r3] = r1
            int[] r1 = new int[r0]
            r2 = 2131165254(0x7var_, float:1.794472E38)
            r1[r3] = r2
            r14 = r1
            r12 = r4
            goto L_0x02a1
        L_0x0286:
            r0 = 1
            r3 = 0
            r8 = 2
            java.lang.CharSequence[] r12 = new java.lang.CharSequence[r8]
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r15)
            r12[r3] = r4
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r12[r0] = r1
            int[] r4 = new int[r8]
            r4 = {NUM, NUM} // fill-array
            r14 = r4
            goto L_0x02a1
        L_0x029e:
            r3 = 0
            r12 = 0
            r14 = 0
        L_0x02a1:
            if (r12 != 0) goto L_0x02a4
            return r3
        L_0x02a4:
            org.telegram.ui.ActionBar.AlertDialog$Builder r15 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r0 = r22.getParentActivity()
            r15.<init>((android.content.Context) r0)
            org.telegram.ui.ChatUsersActivity$$ExternalSyntheticLambda5 r8 = new org.telegram.ui.ChatUsersActivity$$ExternalSyntheticLambda5
            r0 = r8
            r1 = r22
            r2 = r12
            r3 = r5
            r5 = r10
            r6 = r11
            r7 = r23
            r10 = r8
            r8 = r13
            r0.<init>(r1, r2, r3, r5, r6, r7, r8)
            r15.setItems(r12, r14, r10)
            org.telegram.ui.ActionBar.AlertDialog r0 = r15.create()
            r9.showDialog(r0)
            int r1 = r9.type
            r2 = 1
            if (r1 != r2) goto L_0x02d9
            int r1 = r12.length
            int r1 = r1 - r2
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r18)
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r17)
            r0.setItemColor(r1, r3, r4)
        L_0x02d9:
            return r2
        L_0x02da:
            r9 = r12
            r0 = 0
            return r0
        L_0x02dd:
            r9 = r12
            r0 = 0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatUsersActivity.createMenuForParticipant(org.telegram.tgnet.TLObject, boolean):boolean");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createMenuForParticipant$6(ArrayList arrayList, TLRPC$User tLRPC$User, long j, boolean z, TLObject tLObject, int i, TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights, TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights, String str, DialogInterface dialogInterface, int i2) {
        ArrayList arrayList2 = arrayList;
        TLRPC$User tLRPC$User2 = tLRPC$User;
        TLObject tLObject2 = tLObject;
        int i3 = i2;
        if (((Integer) arrayList2.get(i3)).intValue() == 2) {
            getMessagesController().deleteParticipantFromChat(this.chatId, tLRPC$User2, (TLRPC$ChatFull) null);
            removeParticipants(j);
            if (this.currentChat != null && tLRPC$User2 != null && BulletinFactory.canShowBulletin(this)) {
                BulletinFactory.createRemoveFromChatBulletin(this, tLRPC$User2, this.currentChat.title).show();
                return;
            }
            return;
        }
        long j2 = j;
        if (((Integer) arrayList2.get(i3)).intValue() != 1 || !z || (!(tLObject2 instanceof TLRPC$TL_channelParticipantAdmin) && !(tLObject2 instanceof TLRPC$TL_chatParticipantAdmin))) {
            openRightsEdit2(j, i, tLObject, tLRPC$TL_chatAdminRights, tLRPC$TL_chatBannedRights, str, z, ((Integer) arrayList2.get(i3)).intValue(), false);
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setTitle(LocaleController.getString("AppName", NUM));
        builder.setMessage(LocaleController.formatString("AdminWillBeRemoved", NUM, UserObject.getUserName(tLRPC$User)));
        String string = LocaleController.getString("OK", NUM);
        ChatUsersActivity$$ExternalSyntheticLambda2 chatUsersActivity$$ExternalSyntheticLambda2 = r0;
        ChatUsersActivity$$ExternalSyntheticLambda2 chatUsersActivity$$ExternalSyntheticLambda22 = new ChatUsersActivity$$ExternalSyntheticLambda2(this, j, i, tLObject, tLRPC$TL_chatAdminRights, tLRPC$TL_chatBannedRights, str, z, arrayList, i2);
        builder.setPositiveButton(string, chatUsersActivity$$ExternalSyntheticLambda2);
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        showDialog(builder.create());
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createMenuForParticipant$5(long j, int i, TLObject tLObject, TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights, TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights, String str, boolean z, ArrayList arrayList, int i2, DialogInterface dialogInterface, int i3) {
        openRightsEdit2(j, i, tLObject, tLRPC$TL_chatAdminRights, tLRPC$TL_chatBannedRights, str, z, ((Integer) arrayList.get(i2)).intValue(), false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createMenuForParticipant$9(CharSequence[] charSequenceArr, long j, TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights, String str, TLObject tLObject, TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights, DialogInterface dialogInterface, int i) {
        int i2;
        int i3;
        TLRPC$Chat tLRPC$Chat;
        long j2 = j;
        final TLObject tLObject2 = tLObject;
        int i4 = i;
        int i5 = this.type;
        if (i5 == 1) {
            if (i4 == 0 && charSequenceArr.length == 2) {
                ChatRightsEditActivity chatRightsEditActivity = new ChatRightsEditActivity(j, this.chatId, tLRPC$TL_chatAdminRights, (TLRPC$TL_chatBannedRights) null, (TLRPC$TL_chatBannedRights) null, str, 0, true, false);
                chatRightsEditActivity.setDelegate(new ChatRightsEditActivity.ChatRightsEditActivityDelegate() {
                    public void didSetRights(int i, TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights, TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights, String str) {
                        TLObject tLObject = tLObject2;
                        if (tLObject instanceof TLRPC$ChannelParticipant) {
                            TLRPC$ChannelParticipant tLRPC$ChannelParticipant = (TLRPC$ChannelParticipant) tLObject;
                            tLRPC$ChannelParticipant.admin_rights = tLRPC$TL_chatAdminRights;
                            tLRPC$ChannelParticipant.banned_rights = tLRPC$TL_chatBannedRights;
                            tLRPC$ChannelParticipant.rank = str;
                            ChatUsersActivity.this.updateParticipantWithRights(tLRPC$ChannelParticipant, tLRPC$TL_chatAdminRights, tLRPC$TL_chatBannedRights, 0, false);
                        }
                    }

                    public void didChangeOwner(TLRPC$User tLRPC$User) {
                        ChatUsersActivity.this.onOwnerChaged(tLRPC$User);
                    }
                });
                presentFragment(chatRightsEditActivity);
                return;
            }
            getMessagesController().setUserAdminRole(this.chatId, getMessagesController().getUser(Long.valueOf(j)), new TLRPC$TL_chatAdminRights(), "", true ^ this.isChannel, this, false);
            removeParticipants(j2);
        } else if (i5 == 0 || i5 == 3) {
            if (i4 == 0) {
                if (i5 == 3) {
                    ChatRightsEditActivity chatRightsEditActivity2 = new ChatRightsEditActivity(j, this.chatId, (TLRPC$TL_chatAdminRights) null, this.defaultBannedRights, tLRPC$TL_chatBannedRights, str, 1, true, false);
                    chatRightsEditActivity2.setDelegate(new ChatRightsEditActivity.ChatRightsEditActivityDelegate() {
                        public void didSetRights(int i, TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights, TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights, String str) {
                            TLObject tLObject = tLObject2;
                            if (tLObject instanceof TLRPC$ChannelParticipant) {
                                TLRPC$ChannelParticipant tLRPC$ChannelParticipant = (TLRPC$ChannelParticipant) tLObject;
                                tLRPC$ChannelParticipant.admin_rights = tLRPC$TL_chatAdminRights;
                                tLRPC$ChannelParticipant.banned_rights = tLRPC$TL_chatBannedRights;
                                tLRPC$ChannelParticipant.rank = str;
                                ChatUsersActivity.this.updateParticipantWithRights(tLRPC$ChannelParticipant, tLRPC$TL_chatAdminRights, tLRPC$TL_chatBannedRights, 0, false);
                            }
                        }

                        public void didChangeOwner(TLRPC$User tLRPC$User) {
                            ChatUsersActivity.this.onOwnerChaged(tLRPC$User);
                        }
                    });
                    presentFragment(chatRightsEditActivity2);
                } else if (i5 == 0 && j2 > 0) {
                    i3 = 1;
                    i2 = i4;
                    getMessagesController().addUserToChat(this.chatId, getMessagesController().getUser(Long.valueOf(j)), 0, (String) null, this, (Runnable) null);
                }
                i2 = i4;
                i3 = 1;
            } else {
                i2 = i4;
                i3 = 1;
                if (i2 == 1) {
                    TLRPC$TL_channels_editBanned tLRPC$TL_channels_editBanned = new TLRPC$TL_channels_editBanned();
                    tLRPC$TL_channels_editBanned.participant = getMessagesController().getInputPeer(j2);
                    tLRPC$TL_channels_editBanned.channel = getMessagesController().getInputChannel(this.chatId);
                    tLRPC$TL_channels_editBanned.banned_rights = new TLRPC$TL_chatBannedRights();
                    getConnectionsManager().sendRequest(tLRPC$TL_channels_editBanned, new ChatUsersActivity$$ExternalSyntheticLambda13(this));
                }
            }
            if ((i2 == 0 && this.type == 0) || i2 == i3) {
                removeParticipants(tLObject2);
            }
        } else if (i4 == 0) {
            TLRPC$User tLRPC$User = null;
            if (j2 > 0) {
                tLRPC$Chat = null;
                tLRPC$User = getMessagesController().getUser(Long.valueOf(j));
            } else {
                tLRPC$Chat = getMessagesController().getChat(Long.valueOf(-j2));
            }
            getMessagesController().deleteParticipantFromChat(this.chatId, tLRPC$User, tLRPC$Chat, (TLRPC$ChatFull) null, false, false);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createMenuForParticipant$8(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            TLRPC$Updates tLRPC$Updates = (TLRPC$Updates) tLObject;
            getMessagesController().processUpdates(tLRPC$Updates, false);
            if (!tLRPC$Updates.chats.isEmpty()) {
                AndroidUtilities.runOnUIThread(new ChatUsersActivity$$ExternalSyntheticLambda8(this, tLRPC$Updates), 1000);
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createMenuForParticipant$7(TLRPC$Updates tLRPC$Updates) {
        getMessagesController().loadFullChat(tLRPC$Updates.chats.get(0).id, 0, true);
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.chatInfoDidLoad) {
            boolean z = false;
            TLRPC$ChatFull tLRPC$ChatFull = objArr[0];
            boolean booleanValue = objArr[2].booleanValue();
            if (tLRPC$ChatFull.id != this.chatId) {
                return;
            }
            if (!booleanValue || !ChatObject.isChannel(this.currentChat)) {
                if (this.info != null) {
                    z = true;
                }
                this.info = tLRPC$ChatFull;
                if (!z) {
                    int currentSlowmode = getCurrentSlowmode();
                    this.initialSlowmode = currentSlowmode;
                    this.selectedSlowmode = currentSlowmode;
                }
                AndroidUtilities.runOnUIThread(new ChatUsersActivity$$ExternalSyntheticLambda6(this));
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$didReceivedNotification$10() {
        loadChatParticipants(0, 200);
    }

    public boolean onBackPressed() {
        return checkDiscard();
    }

    public void setDelegate(ChatUsersActivityDelegate chatUsersActivityDelegate) {
        this.delegate = chatUsersActivityDelegate;
    }

    private int getCurrentSlowmode() {
        TLRPC$ChatFull tLRPC$ChatFull = this.info;
        if (tLRPC$ChatFull == null) {
            return 0;
        }
        int i = tLRPC$ChatFull.slowmode_seconds;
        if (i == 10) {
            return 1;
        }
        if (i == 30) {
            return 2;
        }
        if (i == 60) {
            return 3;
        }
        if (i == 300) {
            return 4;
        }
        if (i == 900) {
            return 5;
        }
        return i == 3600 ? 6 : 0;
    }

    /* access modifiers changed from: private */
    public String formatSeconds(int i) {
        if (i < 60) {
            return LocaleController.formatPluralString("Seconds", i);
        }
        if (i < 3600) {
            return LocaleController.formatPluralString("Minutes", i / 60);
        }
        return LocaleController.formatPluralString("Hours", (i / 60) / 60);
    }

    /* access modifiers changed from: private */
    public boolean checkDiscard() {
        if (ChatObject.getBannedRightsString(this.defaultBannedRights).equals(this.initialBannedRights) && this.initialSlowmode == this.selectedSlowmode) {
            return true;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setTitle(LocaleController.getString("UserRestrictionsApplyChanges", NUM));
        if (this.isChannel) {
            builder.setMessage(LocaleController.getString("ChannelSettingsChangedAlert", NUM));
        } else {
            builder.setMessage(LocaleController.getString("GroupSettingsChangedAlert", NUM));
        }
        builder.setPositiveButton(LocaleController.getString("ApplyTheme", NUM), new ChatUsersActivity$$ExternalSyntheticLambda1(this));
        builder.setNegativeButton(LocaleController.getString("PassportDiscard", NUM), new ChatUsersActivity$$ExternalSyntheticLambda0(this));
        showDialog(builder.create());
        return false;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$checkDiscard$11(DialogInterface dialogInterface, int i) {
        processDone();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$checkDiscard$12(DialogInterface dialogInterface, int i) {
        finishFragment();
    }

    public boolean hasSelectType() {
        return this.selectType != 0;
    }

    /* access modifiers changed from: private */
    public String formatUserPermissions(TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights) {
        if (tLRPC$TL_chatBannedRights == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        boolean z = tLRPC$TL_chatBannedRights.view_messages;
        if (z && this.defaultBannedRights.view_messages != z) {
            sb.append(LocaleController.getString("UserRestrictionsNoRead", NUM));
        }
        boolean z2 = tLRPC$TL_chatBannedRights.send_messages;
        if (z2 && this.defaultBannedRights.send_messages != z2) {
            if (sb.length() != 0) {
                sb.append(", ");
            }
            sb.append(LocaleController.getString("UserRestrictionsNoSend", NUM));
        }
        boolean z3 = tLRPC$TL_chatBannedRights.send_media;
        if (z3 && this.defaultBannedRights.send_media != z3) {
            if (sb.length() != 0) {
                sb.append(", ");
            }
            sb.append(LocaleController.getString("UserRestrictionsNoSendMedia", NUM));
        }
        boolean z4 = tLRPC$TL_chatBannedRights.send_stickers;
        if (z4 && this.defaultBannedRights.send_stickers != z4) {
            if (sb.length() != 0) {
                sb.append(", ");
            }
            sb.append(LocaleController.getString("UserRestrictionsNoSendStickers", NUM));
        }
        boolean z5 = tLRPC$TL_chatBannedRights.send_polls;
        if (z5 && this.defaultBannedRights.send_polls != z5) {
            if (sb.length() != 0) {
                sb.append(", ");
            }
            sb.append(LocaleController.getString("UserRestrictionsNoSendPolls", NUM));
        }
        boolean z6 = tLRPC$TL_chatBannedRights.embed_links;
        if (z6 && this.defaultBannedRights.embed_links != z6) {
            if (sb.length() != 0) {
                sb.append(", ");
            }
            sb.append(LocaleController.getString("UserRestrictionsNoEmbedLinks", NUM));
        }
        boolean z7 = tLRPC$TL_chatBannedRights.invite_users;
        if (z7 && this.defaultBannedRights.invite_users != z7) {
            if (sb.length() != 0) {
                sb.append(", ");
            }
            sb.append(LocaleController.getString("UserRestrictionsNoInviteUsers", NUM));
        }
        boolean z8 = tLRPC$TL_chatBannedRights.pin_messages;
        if (z8 && this.defaultBannedRights.pin_messages != z8) {
            if (sb.length() != 0) {
                sb.append(", ");
            }
            sb.append(LocaleController.getString("UserRestrictionsNoPinMessages", NUM));
        }
        boolean z9 = tLRPC$TL_chatBannedRights.change_info;
        if (z9 && this.defaultBannedRights.change_info != z9) {
            if (sb.length() != 0) {
                sb.append(", ");
            }
            sb.append(LocaleController.getString("UserRestrictionsNoChangeInfo", NUM));
        }
        if (sb.length() != 0) {
            sb.replace(0, 1, sb.substring(0, 1).toUpperCase());
            sb.append('.');
        }
        return sb.toString();
    }

    /* access modifiers changed from: private */
    public void processDone() {
        TLRPC$ChatFull tLRPC$ChatFull;
        if (this.type == 3) {
            TLRPC$Chat tLRPC$Chat = this.currentChat;
            if (!tLRPC$Chat.creator || ChatObject.isChannel(tLRPC$Chat) || this.selectedSlowmode == this.initialSlowmode || this.info == null) {
                if (!ChatObject.getBannedRightsString(this.defaultBannedRights).equals(this.initialBannedRights)) {
                    getMessagesController().setDefaultBannedRole(this.chatId, this.defaultBannedRights, ChatObject.isChannel(this.currentChat), this);
                    TLRPC$Chat chat = getMessagesController().getChat(Long.valueOf(this.chatId));
                    if (chat != null) {
                        chat.default_banned_rights = this.defaultBannedRights;
                    }
                }
                int i = this.selectedSlowmode;
                if (!(i == this.initialSlowmode || (tLRPC$ChatFull = this.info) == null)) {
                    tLRPC$ChatFull.slowmode_seconds = getSecondsForIndex(i);
                    this.info.flags |= 131072;
                    getMessagesController().setChannelSlowMode(this.chatId, this.info.slowmode_seconds);
                }
                finishFragment();
                return;
            }
            MessagesController.getInstance(this.currentAccount).convertToMegaGroup(getParentActivity(), this.chatId, this, new ChatUsersActivity$$ExternalSyntheticLambda12(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processDone$13(long j) {
        if (j != 0) {
            this.chatId = j;
            this.currentChat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(j));
            processDone();
        }
    }

    public void setInfo(TLRPC$ChatFull tLRPC$ChatFull) {
        this.info = tLRPC$ChatFull;
        if (tLRPC$ChatFull != null) {
            int currentSlowmode = getCurrentSlowmode();
            this.initialSlowmode = currentSlowmode;
            this.selectedSlowmode = currentSlowmode;
        }
    }

    private int getChannelAdminParticipantType(TLObject tLObject) {
        if ((tLObject instanceof TLRPC$TL_channelParticipantCreator) || (tLObject instanceof TLRPC$TL_channelParticipantSelf)) {
            return 0;
        }
        return ((tLObject instanceof TLRPC$TL_channelParticipantAdmin) || (tLObject instanceof TLRPC$TL_channelParticipant)) ? 1 : 2;
    }

    /* access modifiers changed from: private */
    public void loadChatParticipants(int i, int i2) {
        if (!this.loadingUsers) {
            this.contactsEndReached = false;
            this.botsEndReached = false;
            loadChatParticipants(i, i2, true);
        }
    }

    private void loadChatParticipants(int i, int i2, boolean z) {
        TLRPC$Chat tLRPC$Chat;
        LongSparseArray<TLRPC$TL_groupCallParticipant> longSparseArray;
        int i3 = 0;
        if (!ChatObject.isChannel(this.currentChat)) {
            this.loadingUsers = false;
            this.participants.clear();
            this.bots.clear();
            this.contacts.clear();
            this.participantsMap.clear();
            this.contactsMap.clear();
            this.botsMap.clear();
            int i4 = this.type;
            if (i4 == 1) {
                TLRPC$ChatFull tLRPC$ChatFull = this.info;
                if (tLRPC$ChatFull != null) {
                    int size = tLRPC$ChatFull.participants.participants.size();
                    while (i3 < size) {
                        TLRPC$ChatParticipant tLRPC$ChatParticipant = this.info.participants.participants.get(i3);
                        if ((tLRPC$ChatParticipant instanceof TLRPC$TL_chatParticipantCreator) || (tLRPC$ChatParticipant instanceof TLRPC$TL_chatParticipantAdmin)) {
                            this.participants.add(tLRPC$ChatParticipant);
                        }
                        this.participantsMap.put(tLRPC$ChatParticipant.user_id, tLRPC$ChatParticipant);
                        i3++;
                    }
                }
            } else if (i4 == 2 && this.info != null) {
                long j = getUserConfig().clientUserId;
                int size2 = this.info.participants.participants.size();
                while (i3 < size2) {
                    TLRPC$ChatParticipant tLRPC$ChatParticipant2 = this.info.participants.participants.get(i3);
                    if ((this.selectType == 0 || tLRPC$ChatParticipant2.user_id != j) && ((longSparseArray = this.ignoredUsers) == null || longSparseArray.indexOfKey(tLRPC$ChatParticipant2.user_id) < 0)) {
                        if (this.selectType == 1) {
                            if (getContactsController().isContact(tLRPC$ChatParticipant2.user_id)) {
                                this.contacts.add(tLRPC$ChatParticipant2);
                                this.contactsMap.put(tLRPC$ChatParticipant2.user_id, tLRPC$ChatParticipant2);
                            } else if (!UserObject.isDeleted(getMessagesController().getUser(Long.valueOf(tLRPC$ChatParticipant2.user_id)))) {
                                this.participants.add(tLRPC$ChatParticipant2);
                                this.participantsMap.put(tLRPC$ChatParticipant2.user_id, tLRPC$ChatParticipant2);
                            }
                        } else if (getContactsController().isContact(tLRPC$ChatParticipant2.user_id)) {
                            this.contacts.add(tLRPC$ChatParticipant2);
                            this.contactsMap.put(tLRPC$ChatParticipant2.user_id, tLRPC$ChatParticipant2);
                        } else {
                            TLRPC$User user = getMessagesController().getUser(Long.valueOf(tLRPC$ChatParticipant2.user_id));
                            if (user == null || !user.bot) {
                                this.participants.add(tLRPC$ChatParticipant2);
                                this.participantsMap.put(tLRPC$ChatParticipant2.user_id, tLRPC$ChatParticipant2);
                            } else {
                                this.bots.add(tLRPC$ChatParticipant2);
                                this.botsMap.put(tLRPC$ChatParticipant2.user_id, tLRPC$ChatParticipant2);
                            }
                        }
                    }
                    i3++;
                }
            }
            ListAdapter listAdapter = this.listViewAdapter;
            if (listAdapter != null) {
                listAdapter.notifyDataSetChanged();
            }
            updateRows();
            ListAdapter listAdapter2 = this.listViewAdapter;
            if (listAdapter2 != null) {
                listAdapter2.notifyDataSetChanged();
                return;
            }
            return;
        }
        this.loadingUsers = true;
        StickerEmptyView stickerEmptyView = this.emptyView;
        if (stickerEmptyView != null) {
            stickerEmptyView.showProgress(true, false);
        }
        ListAdapter listAdapter3 = this.listViewAdapter;
        if (listAdapter3 != null) {
            listAdapter3.notifyDataSetChanged();
        }
        TLRPC$TL_channels_getParticipants tLRPC$TL_channels_getParticipants = new TLRPC$TL_channels_getParticipants();
        tLRPC$TL_channels_getParticipants.channel = getMessagesController().getInputChannel(this.chatId);
        int i5 = this.type;
        if (i5 == 0) {
            tLRPC$TL_channels_getParticipants.filter = new TLRPC$TL_channelParticipantsKicked();
        } else if (i5 == 1) {
            tLRPC$TL_channels_getParticipants.filter = new TLRPC$TL_channelParticipantsAdmins();
        } else if (i5 == 2) {
            TLRPC$ChatFull tLRPC$ChatFull2 = this.info;
            if (tLRPC$ChatFull2 != null && tLRPC$ChatFull2.participants_count <= 200 && (tLRPC$Chat = this.currentChat) != null && tLRPC$Chat.megagroup) {
                tLRPC$TL_channels_getParticipants.filter = new TLRPC$TL_channelParticipantsRecent();
            } else if (this.selectType == 1) {
                if (!this.contactsEndReached) {
                    this.delayResults = 2;
                    tLRPC$TL_channels_getParticipants.filter = new TLRPC$TL_channelParticipantsContacts();
                    this.contactsEndReached = true;
                    loadChatParticipants(0, 200, false);
                } else {
                    tLRPC$TL_channels_getParticipants.filter = new TLRPC$TL_channelParticipantsRecent();
                }
            } else if (!this.contactsEndReached) {
                this.delayResults = 3;
                tLRPC$TL_channels_getParticipants.filter = new TLRPC$TL_channelParticipantsContacts();
                this.contactsEndReached = true;
                loadChatParticipants(0, 200, false);
            } else if (!this.botsEndReached) {
                tLRPC$TL_channels_getParticipants.filter = new TLRPC$TL_channelParticipantsBots();
                this.botsEndReached = true;
                loadChatParticipants(0, 200, false);
            } else {
                tLRPC$TL_channels_getParticipants.filter = new TLRPC$TL_channelParticipantsRecent();
            }
        } else if (i5 == 3) {
            tLRPC$TL_channels_getParticipants.filter = new TLRPC$TL_channelParticipantsBanned();
        }
        tLRPC$TL_channels_getParticipants.filter.q = "";
        tLRPC$TL_channels_getParticipants.offset = i;
        tLRPC$TL_channels_getParticipants.limit = i2;
        getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(tLRPC$TL_channels_getParticipants, new ChatUsersActivity$$ExternalSyntheticLambda14(this, tLRPC$TL_channels_getParticipants)), this.classGuid);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadChatParticipants$15(TLRPC$TL_channels_getParticipants tLRPC$TL_channels_getParticipants, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new ChatUsersActivity$$ExternalSyntheticLambda7(this, tLRPC$TL_error, tLObject, tLRPC$TL_channels_getParticipants));
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:45:0x00fd, code lost:
        r5 = r8.ignoredUsers;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$loadChatParticipants$14(org.telegram.tgnet.TLRPC$TL_error r9, org.telegram.tgnet.TLObject r10, org.telegram.tgnet.TLRPC$TL_channels_getParticipants r11) {
        /*
            r8 = this;
            r0 = 2
            r1 = 0
            r2 = 1
            if (r9 != 0) goto L_0x0147
            org.telegram.tgnet.TLRPC$TL_channels_channelParticipants r10 = (org.telegram.tgnet.TLRPC$TL_channels_channelParticipants) r10
            int r9 = r8.type
            if (r9 != r2) goto L_0x0014
            org.telegram.messenger.MessagesController r9 = r8.getMessagesController()
            long r3 = r8.chatId
            r9.processLoadedAdminsResponse(r3, r10)
        L_0x0014:
            org.telegram.messenger.MessagesController r9 = r8.getMessagesController()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r3 = r10.users
            r9.putUsers(r3, r1)
            org.telegram.messenger.MessagesController r9 = r8.getMessagesController()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r3 = r10.chats
            r9.putChats(r3, r1)
            org.telegram.messenger.UserConfig r9 = r8.getUserConfig()
            long r3 = r9.getClientUserId()
            int r9 = r8.selectType
            if (r9 == 0) goto L_0x0056
            r9 = 0
        L_0x0033:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChannelParticipant> r5 = r10.participants
            int r5 = r5.size()
            if (r9 >= r5) goto L_0x0056
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChannelParticipant> r5 = r10.participants
            java.lang.Object r5 = r5.get(r9)
            org.telegram.tgnet.TLRPC$ChannelParticipant r5 = (org.telegram.tgnet.TLRPC$ChannelParticipant) r5
            org.telegram.tgnet.TLRPC$Peer r5 = r5.peer
            long r5 = org.telegram.messenger.MessageObject.getPeerId(r5)
            int r7 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1))
            if (r7 != 0) goto L_0x0053
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChannelParticipant> r3 = r10.participants
            r3.remove(r9)
            goto L_0x0056
        L_0x0053:
            int r9 = r9 + 1
            goto L_0x0033
        L_0x0056:
            int r9 = r8.type
            if (r9 != r0) goto L_0x0078
            int r9 = r8.delayResults
            int r9 = r9 - r2
            r8.delayResults = r9
            org.telegram.tgnet.TLRPC$ChannelParticipantsFilter r9 = r11.filter
            boolean r11 = r9 instanceof org.telegram.tgnet.TLRPC$TL_channelParticipantsContacts
            if (r11 == 0) goto L_0x006a
            java.util.ArrayList<org.telegram.tgnet.TLObject> r9 = r8.contacts
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLObject> r11 = r8.contactsMap
            goto L_0x007f
        L_0x006a:
            boolean r9 = r9 instanceof org.telegram.tgnet.TLRPC$TL_channelParticipantsBots
            if (r9 == 0) goto L_0x0073
            java.util.ArrayList<org.telegram.tgnet.TLObject> r9 = r8.bots
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLObject> r11 = r8.botsMap
            goto L_0x007f
        L_0x0073:
            java.util.ArrayList<org.telegram.tgnet.TLObject> r9 = r8.participants
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLObject> r11 = r8.participantsMap
            goto L_0x007f
        L_0x0078:
            java.util.ArrayList<org.telegram.tgnet.TLObject> r9 = r8.participants
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLObject> r11 = r8.participantsMap
            r11.clear()
        L_0x007f:
            r9.clear()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChannelParticipant> r3 = r10.participants
            r9.addAll(r3)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChannelParticipant> r3 = r10.participants
            int r3 = r3.size()
            r4 = 0
        L_0x008e:
            if (r4 >= r3) goto L_0x00a4
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChannelParticipant> r5 = r10.participants
            java.lang.Object r5 = r5.get(r4)
            org.telegram.tgnet.TLRPC$ChannelParticipant r5 = (org.telegram.tgnet.TLRPC$ChannelParticipant) r5
            org.telegram.tgnet.TLRPC$Peer r6 = r5.peer
            long r6 = org.telegram.messenger.MessageObject.getPeerId(r6)
            r11.put(r6, r5)
            int r4 = r4 + 1
            goto L_0x008e
        L_0x00a4:
            int r10 = r8.type
            if (r10 != r0) goto L_0x011a
            java.util.ArrayList<org.telegram.tgnet.TLObject> r10 = r8.participants
            int r10 = r10.size()
            r11 = 0
        L_0x00af:
            if (r11 >= r10) goto L_0x011a
            java.util.ArrayList<org.telegram.tgnet.TLObject> r3 = r8.participants
            java.lang.Object r3 = r3.get(r11)
            org.telegram.tgnet.TLObject r3 = (org.telegram.tgnet.TLObject) r3
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$ChannelParticipant
            if (r4 != 0) goto L_0x00c7
            java.util.ArrayList<org.telegram.tgnet.TLObject> r3 = r8.participants
            r3.remove(r11)
        L_0x00c2:
            int r11 = r11 + -1
            int r10 = r10 + -1
            goto L_0x0118
        L_0x00c7:
            org.telegram.tgnet.TLRPC$ChannelParticipant r3 = (org.telegram.tgnet.TLRPC$ChannelParticipant) r3
            org.telegram.tgnet.TLRPC$Peer r3 = r3.peer
            long r3 = org.telegram.messenger.MessageObject.getPeerId(r3)
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLObject> r5 = r8.contactsMap
            java.lang.Object r5 = r5.get(r3)
            if (r5 != 0) goto L_0x010a
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLObject> r5 = r8.botsMap
            java.lang.Object r5 = r5.get(r3)
            if (r5 == 0) goto L_0x00e0
            goto L_0x010a
        L_0x00e0:
            int r5 = r8.selectType
            if (r5 != r2) goto L_0x00fd
            r5 = 0
            int r7 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r7 <= 0) goto L_0x00fd
            org.telegram.messenger.MessagesController r5 = r8.getMessagesController()
            java.lang.Long r6 = java.lang.Long.valueOf(r3)
            org.telegram.tgnet.TLRPC$User r5 = r5.getUser(r6)
            boolean r5 = org.telegram.messenger.UserObject.isDeleted(r5)
            if (r5 == 0) goto L_0x00fd
            goto L_0x010a
        L_0x00fd:
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r5 = r8.ignoredUsers
            if (r5 == 0) goto L_0x0108
            int r5 = r5.indexOfKey(r3)
            if (r5 < 0) goto L_0x0108
            goto L_0x010a
        L_0x0108:
            r5 = 0
            goto L_0x010b
        L_0x010a:
            r5 = 1
        L_0x010b:
            if (r5 == 0) goto L_0x0118
            java.util.ArrayList<org.telegram.tgnet.TLObject> r5 = r8.participants
            r5.remove(r11)
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLObject> r5 = r8.participantsMap
            r5.remove(r3)
            goto L_0x00c2
        L_0x0118:
            int r11 = r11 + r2
            goto L_0x00af
        L_0x011a:
            int r10 = r8.type     // Catch:{ Exception -> 0x0143 }
            if (r10 == 0) goto L_0x0123
            r11 = 3
            if (r10 == r11) goto L_0x0123
            if (r10 != r0) goto L_0x013b
        L_0x0123:
            org.telegram.tgnet.TLRPC$Chat r11 = r8.currentChat     // Catch:{ Exception -> 0x0143 }
            if (r11 == 0) goto L_0x013b
            boolean r11 = r11.megagroup     // Catch:{ Exception -> 0x0143 }
            if (r11 == 0) goto L_0x013b
            org.telegram.tgnet.TLRPC$ChatFull r11 = r8.info     // Catch:{ Exception -> 0x0143 }
            boolean r3 = r11 instanceof org.telegram.tgnet.TLRPC$TL_channelFull     // Catch:{ Exception -> 0x0143 }
            if (r3 == 0) goto L_0x013b
            int r11 = r11.participants_count     // Catch:{ Exception -> 0x0143 }
            r3 = 200(0xc8, float:2.8E-43)
            if (r11 > r3) goto L_0x013b
            r8.sortUsers(r9)     // Catch:{ Exception -> 0x0143 }
            goto L_0x0147
        L_0x013b:
            if (r10 != r2) goto L_0x0147
            java.util.ArrayList<org.telegram.tgnet.TLObject> r9 = r8.participants     // Catch:{ Exception -> 0x0143 }
            r8.sortAdmins(r9)     // Catch:{ Exception -> 0x0143 }
            goto L_0x0147
        L_0x0143:
            r9 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r9)
        L_0x0147:
            int r9 = r8.type
            if (r9 != r0) goto L_0x014f
            int r9 = r8.delayResults
            if (r9 > 0) goto L_0x0160
        L_0x014f:
            org.telegram.ui.ChatUsersActivity$ListAdapter r9 = r8.listViewAdapter
            if (r9 == 0) goto L_0x0158
            int r9 = r9.getItemCount()
            goto L_0x0159
        L_0x0158:
            r9 = 0
        L_0x0159:
            r8.showItemsAnimated(r9)
            r8.loadingUsers = r1
            r8.firstLoaded = r2
        L_0x0160:
            r8.updateRows()
            org.telegram.ui.ChatUsersActivity$ListAdapter r9 = r8.listViewAdapter
            if (r9 == 0) goto L_0x0188
            org.telegram.ui.Components.RecyclerListView r9 = r8.listView
            boolean r10 = r8.openTransitionStarted
            r9.setAnimateEmptyView(r10, r1)
            org.telegram.ui.ChatUsersActivity$ListAdapter r9 = r8.listViewAdapter
            r9.notifyDataSetChanged()
            org.telegram.ui.Components.StickerEmptyView r9 = r8.emptyView
            if (r9 == 0) goto L_0x0188
            org.telegram.ui.ChatUsersActivity$ListAdapter r9 = r8.listViewAdapter
            int r9 = r9.getItemCount()
            if (r9 != 0) goto L_0x0188
            boolean r9 = r8.firstLoaded
            if (r9 == 0) goto L_0x0188
            org.telegram.ui.Components.StickerEmptyView r9 = r8.emptyView
            r9.showProgress(r1, r2)
        L_0x0188:
            r8.resumeDelayedFragmentAnimation()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatUsersActivity.lambda$loadChatParticipants$14(org.telegram.tgnet.TLRPC$TL_error, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_channels_getParticipants):void");
    }

    /* access modifiers changed from: private */
    public void sortUsers(ArrayList<TLObject> arrayList) {
        Collections.sort(arrayList, new ChatUsersActivity$$ExternalSyntheticLambda11(this, getConnectionsManager().getCurrentTime()));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ int lambda$sortUsers$16(int i, TLObject tLObject, TLObject tLObject2) {
        int i2;
        TLRPC$UserStatus tLRPC$UserStatus;
        int i3;
        TLRPC$UserStatus tLRPC$UserStatus2;
        TLRPC$ChannelParticipant tLRPC$ChannelParticipant = (TLRPC$ChannelParticipant) tLObject;
        TLRPC$ChannelParticipant tLRPC$ChannelParticipant2 = (TLRPC$ChannelParticipant) tLObject2;
        long peerId = MessageObject.getPeerId(tLRPC$ChannelParticipant.peer);
        long peerId2 = MessageObject.getPeerId(tLRPC$ChannelParticipant2.peer);
        int i4 = -100;
        if (peerId > 0) {
            TLRPC$User user = getMessagesController().getUser(Long.valueOf(MessageObject.getPeerId(tLRPC$ChannelParticipant.peer)));
            i2 = (user == null || (tLRPC$UserStatus2 = user.status) == null) ? 0 : user.self ? i + 50000 : tLRPC$UserStatus2.expires;
        } else {
            i2 = -100;
        }
        if (peerId2 > 0) {
            TLRPC$User user2 = getMessagesController().getUser(Long.valueOf(MessageObject.getPeerId(tLRPC$ChannelParticipant2.peer)));
            if (user2 == null || (tLRPC$UserStatus = user2.status) == null) {
                i4 = 0;
            } else {
                if (user2.self) {
                    i3 = i + 50000;
                } else {
                    i3 = tLRPC$UserStatus.expires;
                }
                i4 = i3;
            }
        }
        if (i2 <= 0 || i4 <= 0) {
            if (i2 >= 0 || i4 >= 0) {
                if ((i2 < 0 && i4 > 0) || (i2 == 0 && i4 != 0)) {
                    return -1;
                }
                if ((i4 >= 0 || i2 <= 0) && (i4 != 0 || i2 == 0)) {
                    return 0;
                }
                return 1;
            } else if (i2 > i4) {
                return 1;
            } else {
                if (i2 < i4) {
                    return -1;
                }
                return 0;
            }
        } else if (i2 > i4) {
            return 1;
        } else {
            if (i2 < i4) {
                return -1;
            }
            return 0;
        }
    }

    public void onResume() {
        super.onResume();
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        ListAdapter listAdapter = this.listViewAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
        StickerEmptyView stickerEmptyView = this.emptyView;
        if (stickerEmptyView != null) {
            stickerEmptyView.requestLayout();
        }
    }

    public void onPause() {
        super.onPause();
        UndoView undoView2 = this.undoView;
        if (undoView2 != null) {
            undoView2.hide(true, 0);
        }
    }

    /* access modifiers changed from: protected */
    public void onBecomeFullyHidden() {
        UndoView undoView2 = this.undoView;
        if (undoView2 != null) {
            undoView2.hide(true, 0);
        }
    }

    /* access modifiers changed from: protected */
    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z) {
            this.openTransitionStarted = true;
        }
        if (z && !z2 && this.needOpenSearch) {
            this.searchItem.getSearchField().requestFocus();
            AndroidUtilities.showKeyboard(this.searchItem.getSearchField());
        }
    }

    private class SearchAdapter extends RecyclerListView.SelectionAdapter {
        private int contactsStartRow;
        private int globalStartRow;
        private int groupStartRow;
        private Context mContext;
        private SearchAdapterHelper searchAdapterHelper;
        private boolean searchInProgress;
        private ArrayList<Object> searchResult = new ArrayList<>();
        private LongSparseArray<TLObject> searchResultMap = new LongSparseArray<>();
        private ArrayList<CharSequence> searchResultNames = new ArrayList<>();
        private Runnable searchRunnable;
        private int totalCount = 0;

        public SearchAdapter(Context context) {
            this.mContext = context;
            SearchAdapterHelper searchAdapterHelper2 = new SearchAdapterHelper(true);
            this.searchAdapterHelper = searchAdapterHelper2;
            searchAdapterHelper2.setDelegate(new ChatUsersActivity$SearchAdapter$$ExternalSyntheticLambda4(this));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0(int i) {
            if (!this.searchAdapterHelper.isSearchInProgress()) {
                int itemCount = getItemCount();
                notifyDataSetChanged();
                if (getItemCount() > itemCount) {
                    ChatUsersActivity.this.showItemsAnimated(itemCount);
                }
                if (!this.searchInProgress && getItemCount() == 0 && i != 0) {
                    ChatUsersActivity.this.emptyView.showProgress(false, true);
                }
            }
        }

        public void searchUsers(String str) {
            if (this.searchRunnable != null) {
                Utilities.searchQueue.cancelRunnable(this.searchRunnable);
                this.searchRunnable = null;
            }
            this.searchResult.clear();
            this.searchResultMap.clear();
            this.searchResultNames.clear();
            this.searchAdapterHelper.mergeResults((ArrayList<Object>) null);
            this.searchAdapterHelper.queryServerSearch((String) null, ChatUsersActivity.this.type != 0, false, true, false, false, ChatObject.isChannel(ChatUsersActivity.this.currentChat) ? ChatUsersActivity.this.chatId : 0, false, ChatUsersActivity.this.type, 0);
            notifyDataSetChanged();
            if (!TextUtils.isEmpty(str)) {
                this.searchInProgress = true;
                ChatUsersActivity.this.emptyView.showProgress(true, true);
                DispatchQueue dispatchQueue = Utilities.searchQueue;
                ChatUsersActivity$SearchAdapter$$ExternalSyntheticLambda1 chatUsersActivity$SearchAdapter$$ExternalSyntheticLambda1 = new ChatUsersActivity$SearchAdapter$$ExternalSyntheticLambda1(this, str);
                this.searchRunnable = chatUsersActivity$SearchAdapter$$ExternalSyntheticLambda1;
                dispatchQueue.postRunnable(chatUsersActivity$SearchAdapter$$ExternalSyntheticLambda1, 300);
            }
        }

        /* access modifiers changed from: private */
        /* renamed from: processSearch */
        public void lambda$searchUsers$1(String str) {
            AndroidUtilities.runOnUIThread(new ChatUsersActivity$SearchAdapter$$ExternalSyntheticLambda0(this, str));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$processSearch$3(String str) {
            ArrayList arrayList = null;
            this.searchRunnable = null;
            ArrayList arrayList2 = (ChatObject.isChannel(ChatUsersActivity.this.currentChat) || ChatUsersActivity.this.info == null) ? null : new ArrayList(ChatUsersActivity.this.info.participants.participants);
            if (ChatUsersActivity.this.selectType == 1) {
                arrayList = new ArrayList(ChatUsersActivity.this.getContactsController().contacts);
            }
            if (arrayList2 == null && arrayList == null) {
                this.searchInProgress = false;
                String str2 = str;
            } else {
                Utilities.searchQueue.postRunnable(new ChatUsersActivity$SearchAdapter$$ExternalSyntheticLambda2(this, str, arrayList2, arrayList));
            }
            this.searchAdapterHelper.queryServerSearch(str, ChatUsersActivity.this.selectType != 0, false, true, false, false, ChatObject.isChannel(ChatUsersActivity.this.currentChat) ? ChatUsersActivity.this.chatId : 0, false, ChatUsersActivity.this.type, 1);
        }

        /* JADX WARNING: type inference failed for: r9v15 */
        /* JADX WARNING: type inference failed for: r9v17 */
        /* JADX WARNING: type inference failed for: r9v21 */
        /* JADX WARNING: type inference failed for: r9v23 */
        /* access modifiers changed from: private */
        /* JADX WARNING: Code restructure failed: missing block: B:46:0x013e, code lost:
            if (r15.contains(" " + r4) != false) goto L_0x0150;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:86:0x0247, code lost:
            if (r5.contains(" " + r9) != false) goto L_0x0256;
         */
        /* JADX WARNING: Multi-variable type inference failed */
        /* JADX WARNING: Removed duplicated region for block: B:109:0x0153 A[SYNTHETIC] */
        /* JADX WARNING: Removed duplicated region for block: B:114:0x0259 A[SYNTHETIC] */
        /* JADX WARNING: Removed duplicated region for block: B:60:0x0188 A[LOOP:1: B:36:0x0100->B:60:0x0188, LOOP_END] */
        /* JADX WARNING: Removed duplicated region for block: B:99:0x02a1 A[LOOP:3: B:77:0x020d->B:99:0x02a1, LOOP_END] */
        /* JADX WARNING: Unknown variable types count: 1 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public /* synthetic */ void lambda$processSearch$2(java.lang.String r24, java.util.ArrayList r25, java.util.ArrayList r26) {
            /*
                r23 = this;
                r0 = r23
                r1 = r25
                r2 = r26
                java.lang.String r3 = r24.trim()
                java.lang.String r3 = r3.toLowerCase()
                int r4 = r3.length()
                if (r4 != 0) goto L_0x002c
                java.util.ArrayList r1 = new java.util.ArrayList
                r1.<init>()
                androidx.collection.LongSparseArray r2 = new androidx.collection.LongSparseArray
                r2.<init>()
                java.util.ArrayList r3 = new java.util.ArrayList
                r3.<init>()
                java.util.ArrayList r4 = new java.util.ArrayList
                r4.<init>()
                r0.updateSearchResults(r1, r2, r3, r4)
                return
            L_0x002c:
                org.telegram.messenger.LocaleController r4 = org.telegram.messenger.LocaleController.getInstance()
                java.lang.String r4 = r4.getTranslitString(r3)
                boolean r5 = r3.equals(r4)
                if (r5 != 0) goto L_0x0040
                int r5 = r4.length()
                if (r5 != 0) goto L_0x0041
            L_0x0040:
                r4 = 0
            L_0x0041:
                r5 = 0
                r7 = 1
                if (r4 == 0) goto L_0x0047
                r8 = 1
                goto L_0x0048
            L_0x0047:
                r8 = 0
            L_0x0048:
                int r8 = r8 + r7
                java.lang.String[] r9 = new java.lang.String[r8]
                r9[r5] = r3
                if (r4 == 0) goto L_0x0051
                r9[r7] = r4
            L_0x0051:
                java.util.ArrayList r3 = new java.util.ArrayList
                r3.<init>()
                androidx.collection.LongSparseArray r4 = new androidx.collection.LongSparseArray
                r4.<init>()
                java.util.ArrayList r10 = new java.util.ArrayList
                r10.<init>()
                java.util.ArrayList r11 = new java.util.ArrayList
                r11.<init>()
                java.lang.String r13 = "@"
                java.lang.String r14 = " "
                if (r1 == 0) goto L_0x01b2
                int r15 = r25.size()
            L_0x006f:
                if (r5 >= r15) goto L_0x01b2
                java.lang.Object r16 = r1.get(r5)
                r12 = r16
                org.telegram.tgnet.TLObject r12 = (org.telegram.tgnet.TLObject) r12
                boolean r6 = r12 instanceof org.telegram.tgnet.TLRPC$ChatParticipant
                if (r6 == 0) goto L_0x0085
                r6 = r12
                org.telegram.tgnet.TLRPC$ChatParticipant r6 = (org.telegram.tgnet.TLRPC$ChatParticipant) r6
                r17 = r8
                long r7 = r6.user_id
                goto L_0x0094
            L_0x0085:
                r17 = r8
                boolean r6 = r12 instanceof org.telegram.tgnet.TLRPC$ChannelParticipant
                if (r6 == 0) goto L_0x0197
                r6 = r12
                org.telegram.tgnet.TLRPC$ChannelParticipant r6 = (org.telegram.tgnet.TLRPC$ChannelParticipant) r6
                org.telegram.tgnet.TLRPC$Peer r6 = r6.peer
                long r7 = org.telegram.messenger.MessageObject.getPeerId(r6)
            L_0x0094:
                r18 = 0
                int r6 = (r7 > r18 ? 1 : (r7 == r18 ? 0 : -1))
                if (r6 <= 0) goto L_0x00cb
                org.telegram.ui.ChatUsersActivity r6 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.messenger.MessagesController r6 = r6.getMessagesController()
                java.lang.Long r7 = java.lang.Long.valueOf(r7)
                org.telegram.tgnet.TLRPC$User r6 = r6.getUser(r7)
                long r7 = r6.id
                org.telegram.ui.ChatUsersActivity r1 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.messenger.UserConfig r1 = r1.getUserConfig()
                long r18 = r1.getClientUserId()
                int r1 = (r7 > r18 ? 1 : (r7 == r18 ? 0 : -1))
                if (r1 != 0) goto L_0x00ba
                goto L_0x0197
            L_0x00ba:
                java.lang.String r1 = org.telegram.messenger.UserObject.getUserName(r6)
                java.lang.String r1 = r1.toLowerCase()
                java.lang.String r7 = r6.username
                java.lang.String r8 = r6.first_name
                java.lang.String r6 = r6.last_name
                r18 = r15
                goto L_0x00e8
            L_0x00cb:
                org.telegram.ui.ChatUsersActivity r1 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.messenger.MessagesController r1 = r1.getMessagesController()
                long r6 = -r7
                java.lang.Long r6 = java.lang.Long.valueOf(r6)
                org.telegram.tgnet.TLRPC$Chat r1 = r1.getChat(r6)
                java.lang.String r6 = r1.title
                java.lang.String r6 = r6.toLowerCase()
                java.lang.String r7 = r1.username
                java.lang.String r8 = r1.title
                r1 = r6
                r18 = r15
                r6 = 0
            L_0x00e8:
                org.telegram.messenger.LocaleController r15 = org.telegram.messenger.LocaleController.getInstance()
                java.lang.String r15 = r15.getTranslitString(r1)
                boolean r19 = r1.equals(r15)
                if (r19 == 0) goto L_0x00f7
                r15 = 0
            L_0x00f7:
                r20 = r3
                r19 = r4
                r4 = r17
                r3 = 0
                r17 = 0
            L_0x0100:
                r21 = r4
                if (r3 >= r4) goto L_0x0194
                r4 = r9[r3]
                boolean r22 = r1.startsWith(r4)
                if (r22 != 0) goto L_0x014e
                r22 = r9
                java.lang.StringBuilder r9 = new java.lang.StringBuilder
                r9.<init>()
                r9.append(r14)
                r9.append(r4)
                java.lang.String r9 = r9.toString()
                boolean r9 = r1.contains(r9)
                if (r9 != 0) goto L_0x0150
                if (r15 == 0) goto L_0x0141
                boolean r9 = r15.startsWith(r4)
                if (r9 != 0) goto L_0x0150
                java.lang.StringBuilder r9 = new java.lang.StringBuilder
                r9.<init>()
                r9.append(r14)
                r9.append(r4)
                java.lang.String r9 = r9.toString()
                boolean r9 = r15.contains(r9)
                if (r9 == 0) goto L_0x0141
                goto L_0x0150
            L_0x0141:
                if (r7 == 0) goto L_0x014b
                boolean r9 = r7.startsWith(r4)
                if (r9 == 0) goto L_0x014b
                r9 = 2
                goto L_0x0151
            L_0x014b:
                r9 = r17
                goto L_0x0151
            L_0x014e:
                r22 = r9
            L_0x0150:
                r9 = 1
            L_0x0151:
                if (r9 == 0) goto L_0x0188
                r1 = 1
                if (r9 != r1) goto L_0x015e
                java.lang.CharSequence r1 = org.telegram.messenger.AndroidUtilities.generateSearchName(r8, r6, r4)
                r10.add(r1)
                goto L_0x0184
            L_0x015e:
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r1.<init>()
                r1.append(r13)
                r1.append(r7)
                java.lang.String r1 = r1.toString()
                java.lang.StringBuilder r3 = new java.lang.StringBuilder
                r3.<init>()
                r3.append(r13)
                r3.append(r4)
                java.lang.String r3 = r3.toString()
                r4 = 0
                java.lang.CharSequence r1 = org.telegram.messenger.AndroidUtilities.generateSearchName(r1, r4, r3)
                r10.add(r1)
            L_0x0184:
                r11.add(r12)
                goto L_0x01a1
            L_0x0188:
                r17 = r1
                int r3 = r3 + 1
                r4 = r21
                r17 = r9
                r9 = r22
                goto L_0x0100
            L_0x0194:
                r22 = r9
                goto L_0x01a1
            L_0x0197:
                r20 = r3
                r19 = r4
                r22 = r9
                r18 = r15
                r21 = r17
            L_0x01a1:
                int r5 = r5 + 1
                r1 = r25
                r15 = r18
                r4 = r19
                r3 = r20
                r8 = r21
                r9 = r22
                r7 = 1
                goto L_0x006f
            L_0x01b2:
                r20 = r3
                r19 = r4
                r21 = r8
                r22 = r9
                if (r2 == 0) goto L_0x02ba
                r1 = 0
            L_0x01bd:
                int r3 = r26.size()
                if (r1 >= r3) goto L_0x02ba
                java.lang.Object r3 = r2.get(r1)
                org.telegram.tgnet.TLRPC$TL_contact r3 = (org.telegram.tgnet.TLRPC$TL_contact) r3
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.messenger.MessagesController r4 = r4.getMessagesController()
                long r5 = r3.user_id
                java.lang.Long r3 = java.lang.Long.valueOf(r5)
                org.telegram.tgnet.TLRPC$User r3 = r4.getUser(r3)
                long r4 = r3.id
                org.telegram.ui.ChatUsersActivity r6 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.messenger.UserConfig r6 = r6.getUserConfig()
                long r6 = r6.getClientUserId()
                int r8 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
                if (r8 != 0) goto L_0x01f2
                r9 = r19
                r15 = r20
                r8 = r21
            L_0x01ef:
                r12 = 1
                goto L_0x02b0
            L_0x01f2:
                java.lang.String r4 = org.telegram.messenger.UserObject.getUserName(r3)
                java.lang.String r4 = r4.toLowerCase()
                org.telegram.messenger.LocaleController r5 = org.telegram.messenger.LocaleController.getInstance()
                java.lang.String r5 = r5.getTranslitString(r4)
                boolean r6 = r4.equals(r5)
                if (r6 == 0) goto L_0x0209
                r5 = 0
            L_0x0209:
                r8 = r21
                r6 = 0
                r7 = 0
            L_0x020d:
                if (r7 >= r8) goto L_0x02aa
                r9 = r22[r7]
                boolean r12 = r4.startsWith(r9)
                if (r12 != 0) goto L_0x0256
                java.lang.StringBuilder r12 = new java.lang.StringBuilder
                r12.<init>()
                r12.append(r14)
                r12.append(r9)
                java.lang.String r12 = r12.toString()
                boolean r12 = r4.contains(r12)
                if (r12 != 0) goto L_0x0256
                if (r5 == 0) goto L_0x024a
                boolean r12 = r5.startsWith(r9)
                if (r12 != 0) goto L_0x0256
                java.lang.StringBuilder r12 = new java.lang.StringBuilder
                r12.<init>()
                r12.append(r14)
                r12.append(r9)
                java.lang.String r12 = r12.toString()
                boolean r12 = r5.contains(r12)
                if (r12 == 0) goto L_0x024a
                goto L_0x0256
            L_0x024a:
                java.lang.String r12 = r3.username
                if (r12 == 0) goto L_0x0257
                boolean r12 = r12.startsWith(r9)
                if (r12 == 0) goto L_0x0257
                r6 = 2
                goto L_0x0257
            L_0x0256:
                r6 = 1
            L_0x0257:
                if (r6 == 0) goto L_0x02a1
                r12 = 1
                if (r6 != r12) goto L_0x026b
                java.lang.String r4 = r3.first_name
                java.lang.String r5 = r3.last_name
                java.lang.CharSequence r4 = org.telegram.messenger.AndroidUtilities.generateSearchName(r4, r5, r9)
                r10.add(r4)
                r15 = r20
                r9 = 0
                goto L_0x0295
            L_0x026b:
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                r4.append(r13)
                java.lang.String r5 = r3.username
                r4.append(r5)
                java.lang.String r4 = r4.toString()
                java.lang.StringBuilder r5 = new java.lang.StringBuilder
                r5.<init>()
                r5.append(r13)
                r5.append(r9)
                java.lang.String r5 = r5.toString()
                r9 = 0
                java.lang.CharSequence r4 = org.telegram.messenger.AndroidUtilities.generateSearchName(r4, r9, r5)
                r10.add(r4)
                r15 = r20
            L_0x0295:
                r15.add(r3)
                long r4 = r3.id
                r6 = r19
                r6.put(r4, r3)
                r9 = r6
                goto L_0x02b0
            L_0x02a1:
                r9 = r19
                r15 = r20
                r12 = 1
                int r7 = r7 + 1
                goto L_0x020d
            L_0x02aa:
                r9 = r19
                r15 = r20
                goto L_0x01ef
            L_0x02b0:
                int r1 = r1 + 1
                r21 = r8
                r19 = r9
                r20 = r15
                goto L_0x01bd
            L_0x02ba:
                r9 = r19
                r15 = r20
                r0.updateSearchResults(r15, r9, r10, r11)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatUsersActivity.SearchAdapter.lambda$processSearch$2(java.lang.String, java.util.ArrayList, java.util.ArrayList):void");
        }

        private void updateSearchResults(ArrayList<Object> arrayList, LongSparseArray<TLObject> longSparseArray, ArrayList<CharSequence> arrayList2, ArrayList<TLObject> arrayList3) {
            AndroidUtilities.runOnUIThread(new ChatUsersActivity$SearchAdapter$$ExternalSyntheticLambda3(this, arrayList, longSparseArray, arrayList2, arrayList3));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$updateSearchResults$4(ArrayList arrayList, LongSparseArray longSparseArray, ArrayList arrayList2, ArrayList arrayList3) {
            if (ChatUsersActivity.this.searching) {
                this.searchInProgress = false;
                this.searchResult = arrayList;
                this.searchResultMap = longSparseArray;
                this.searchResultNames = arrayList2;
                this.searchAdapterHelper.mergeResults(arrayList);
                if (!ChatObject.isChannel(ChatUsersActivity.this.currentChat)) {
                    ArrayList<TLObject> groupSearch = this.searchAdapterHelper.getGroupSearch();
                    groupSearch.clear();
                    groupSearch.addAll(arrayList3);
                }
                int itemCount = getItemCount();
                notifyDataSetChanged();
                if (getItemCount() > itemCount) {
                    ChatUsersActivity.this.showItemsAnimated(itemCount);
                }
                if (!this.searchAdapterHelper.isSearchInProgress() && getItemCount() == 0) {
                    ChatUsersActivity.this.emptyView.showProgress(false, true);
                }
            }
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() != 1;
        }

        public int getItemCount() {
            return this.totalCount;
        }

        public void notifyDataSetChanged() {
            this.totalCount = 0;
            int size = this.searchAdapterHelper.getGroupSearch().size();
            if (size != 0) {
                this.groupStartRow = 0;
                this.totalCount += size + 1;
            } else {
                this.groupStartRow = -1;
            }
            int size2 = this.searchResult.size();
            if (size2 != 0) {
                int i = this.totalCount;
                this.contactsStartRow = i;
                this.totalCount = i + size2 + 1;
            } else {
                this.contactsStartRow = -1;
            }
            int size3 = this.searchAdapterHelper.getGlobalSearch().size();
            if (size3 != 0) {
                int i2 = this.totalCount;
                this.globalStartRow = i2;
                this.totalCount = i2 + size3 + 1;
            } else {
                this.globalStartRow = -1;
            }
            if (!(!ChatUsersActivity.this.searching || ChatUsersActivity.this.listView == null || ChatUsersActivity.this.listView.getAdapter() == ChatUsersActivity.this.searchListViewAdapter)) {
                ChatUsersActivity.this.listView.setAnimateEmptyView(true, 0);
                ChatUsersActivity.this.listView.setAdapter(ChatUsersActivity.this.searchListViewAdapter);
                ChatUsersActivity.this.listView.setFastScrollVisible(false);
                ChatUsersActivity.this.listView.setVerticalScrollBarEnabled(true);
            }
            super.notifyDataSetChanged();
        }

        public void removeUserId(long j) {
            this.searchAdapterHelper.removeUserId(j);
            TLObject tLObject = this.searchResultMap.get(j);
            if (tLObject != null) {
                this.searchResult.remove(tLObject);
            }
            notifyDataSetChanged();
        }

        public TLObject getItem(int i) {
            int size = this.searchAdapterHelper.getGroupSearch().size();
            if (size != 0) {
                int i2 = size + 1;
                if (i2 <= i) {
                    i -= i2;
                } else if (i == 0) {
                    return null;
                } else {
                    return this.searchAdapterHelper.getGroupSearch().get(i - 1);
                }
            }
            int size2 = this.searchResult.size();
            if (size2 != 0) {
                int i3 = size2 + 1;
                if (i3 <= i) {
                    i -= i3;
                } else if (i == 0) {
                    return null;
                } else {
                    return (TLObject) this.searchResult.get(i - 1);
                }
            }
            int size3 = this.searchAdapterHelper.getGlobalSearch().size();
            if (size3 == 0 || size3 + 1 <= i || i == 0) {
                return null;
            }
            return this.searchAdapterHelper.getGlobalSearch().get(i - 1);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ boolean lambda$onCreateViewHolder$5(ManageChatUserCell manageChatUserCell, boolean z) {
            TLObject item = getItem(((Integer) manageChatUserCell.getTag()).intValue());
            if (!(item instanceof TLRPC$ChannelParticipant)) {
                return false;
            }
            return ChatUsersActivity.this.createMenuForParticipant((TLRPC$ChannelParticipant) item, !z);
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            GraySectionCell graySectionCell;
            if (i != 0) {
                graySectionCell = new GraySectionCell(this.mContext);
            } else {
                ManageChatUserCell manageChatUserCell = new ManageChatUserCell(this.mContext, 2, 2, ChatUsersActivity.this.selectType == 0);
                manageChatUserCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                manageChatUserCell.setDelegate(new ChatUsersActivity$SearchAdapter$$ExternalSyntheticLambda5(this));
                graySectionCell = manageChatUserCell;
            }
            return new RecyclerListView.Holder(graySectionCell);
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v2, resolved type: org.telegram.tgnet.TLRPC$Chat} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v0, resolved type: android.text.SpannableStringBuilder} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v2, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v1, resolved type: android.text.SpannableStringBuilder} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v2, resolved type: android.text.SpannableStringBuilder} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v4, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v5, resolved type: android.text.SpannableStringBuilder} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v3, resolved type: android.text.SpannableStringBuilder} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v9, resolved type: java.lang.CharSequence} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v8, resolved type: android.text.SpannableStringBuilder} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v10, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v9, resolved type: android.text.SpannableStringBuilder} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v11, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v13, resolved type: android.text.SpannableStringBuilder} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v14, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v15, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v16, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v17, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v18, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v19, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v20, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v27, resolved type: org.telegram.tgnet.TLRPC$User} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v28, resolved type: org.telegram.tgnet.TLRPC$Chat} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v29, resolved type: org.telegram.tgnet.TLRPC$Chat} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* JADX WARNING: Removed duplicated region for block: B:35:0x00f2  */
        /* JADX WARNING: Removed duplicated region for block: B:46:0x010f  */
        /* JADX WARNING: Removed duplicated region for block: B:55:0x0141  */
        /* JADX WARNING: Removed duplicated region for block: B:60:0x014f  */
        /* JADX WARNING: Removed duplicated region for block: B:81:0x01ac  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r17, int r18) {
            /*
                r16 = this;
                r1 = r16
                r2 = r17
                r0 = r18
                int r3 = r17.getItemViewType()
                r4 = 1
                if (r3 == 0) goto L_0x008e
                if (r3 == r4) goto L_0x0011
                goto L_0x01cb
            L_0x0011:
                android.view.View r2 = r2.itemView
                org.telegram.ui.Cells.GraySectionCell r2 = (org.telegram.ui.Cells.GraySectionCell) r2
                int r3 = r1.groupStartRow
                if (r0 != r3) goto L_0x006a
                org.telegram.ui.ChatUsersActivity r0 = org.telegram.ui.ChatUsersActivity.this
                int r0 = r0.type
                if (r0 != 0) goto L_0x002f
                r0 = 2131624725(0x7f0e0315, float:1.8876638E38)
                java.lang.String r3 = "ChannelBlockedUsers"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
                r2.setText(r0)
                goto L_0x01cb
            L_0x002f:
                org.telegram.ui.ChatUsersActivity r0 = org.telegram.ui.ChatUsersActivity.this
                int r0 = r0.type
                r3 = 3
                if (r0 != r3) goto L_0x0046
                r0 = 2131624793(0x7f0e0359, float:1.8876776E38)
                java.lang.String r3 = "ChannelRestrictedUsers"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
                r2.setText(r0)
                goto L_0x01cb
            L_0x0046:
                org.telegram.ui.ChatUsersActivity r0 = org.telegram.ui.ChatUsersActivity.this
                boolean r0 = r0.isChannel
                if (r0 == 0) goto L_0x005c
                r0 = 2131624801(0x7f0e0361, float:1.8876792E38)
                java.lang.String r3 = "ChannelSubscribers"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
                r2.setText(r0)
                goto L_0x01cb
            L_0x005c:
                r0 = 2131624755(0x7f0e0333, float:1.8876699E38)
                java.lang.String r3 = "ChannelMembers"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
                r2.setText(r0)
                goto L_0x01cb
            L_0x006a:
                int r3 = r1.globalStartRow
                if (r0 != r3) goto L_0x007c
                r0 = 2131625777(0x7f0e0731, float:1.8878772E38)
                java.lang.String r3 = "GlobalSearch"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
                r2.setText(r0)
                goto L_0x01cb
            L_0x007c:
                int r3 = r1.contactsStartRow
                if (r0 != r3) goto L_0x01cb
                r0 = 2131625041(0x7f0e0451, float:1.8877279E38)
                java.lang.String r3 = "Contacts"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
                r2.setText(r0)
                goto L_0x01cb
            L_0x008e:
                org.telegram.tgnet.TLObject r3 = r1.getItem(r0)
                boolean r5 = r3 instanceof org.telegram.tgnet.TLRPC$User
                r6 = 0
                if (r5 == 0) goto L_0x0099
            L_0x0097:
                r5 = r6
                goto L_0x00e5
            L_0x0099:
                boolean r5 = r3 instanceof org.telegram.tgnet.TLRPC$ChannelParticipant
                if (r5 == 0) goto L_0x00ce
                org.telegram.tgnet.TLRPC$ChannelParticipant r3 = (org.telegram.tgnet.TLRPC$ChannelParticipant) r3
                org.telegram.tgnet.TLRPC$Peer r3 = r3.peer
                long r7 = org.telegram.messenger.MessageObject.getPeerId(r3)
                r9 = 0
                int r3 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
                if (r3 <= 0) goto L_0x00bc
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.messenger.MessagesController r3 = r3.getMessagesController()
                java.lang.Long r5 = java.lang.Long.valueOf(r7)
                org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r5)
                java.lang.String r5 = r3.username
                goto L_0x00e5
            L_0x00bc:
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.messenger.MessagesController r3 = r3.getMessagesController()
                long r7 = -r7
                java.lang.Long r5 = java.lang.Long.valueOf(r7)
                org.telegram.tgnet.TLRPC$Chat r3 = r3.getChat(r5)
                java.lang.String r5 = r3.username
                goto L_0x00e5
            L_0x00ce:
                boolean r5 = r3 instanceof org.telegram.tgnet.TLRPC$ChatParticipant
                if (r5 == 0) goto L_0x01cb
                org.telegram.ui.ChatUsersActivity r5 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.messenger.MessagesController r5 = r5.getMessagesController()
                org.telegram.tgnet.TLRPC$ChatParticipant r3 = (org.telegram.tgnet.TLRPC$ChatParticipant) r3
                long r7 = r3.user_id
                java.lang.Long r3 = java.lang.Long.valueOf(r7)
                org.telegram.tgnet.TLRPC$User r3 = r5.getUser(r3)
                goto L_0x0097
            L_0x00e5:
                org.telegram.ui.Adapters.SearchAdapterHelper r7 = r1.searchAdapterHelper
                java.util.ArrayList r7 = r7.getGroupSearch()
                int r7 = r7.size()
                r8 = 0
                if (r7 == 0) goto L_0x00fe
                int r7 = r7 + r4
                if (r7 <= r0) goto L_0x00fd
                org.telegram.ui.Adapters.SearchAdapterHelper r7 = r1.searchAdapterHelper
                java.lang.String r7 = r7.getLastFoundChannel()
                r9 = 1
                goto L_0x0100
            L_0x00fd:
                int r0 = r0 - r7
            L_0x00fe:
                r7 = r6
                r9 = 0
            L_0x0100:
                java.lang.String r10 = "@"
                if (r9 != 0) goto L_0x0142
                java.util.ArrayList<java.lang.Object> r11 = r1.searchResult
                int r11 = r11.size()
                if (r11 == 0) goto L_0x0142
                int r11 = r11 + r4
                if (r11 <= r0) goto L_0x0141
                java.util.ArrayList<java.lang.CharSequence> r9 = r1.searchResultNames
                int r11 = r0 + -1
                java.lang.Object r9 = r9.get(r11)
                java.lang.CharSequence r9 = (java.lang.CharSequence) r9
                if (r9 == 0) goto L_0x013c
                boolean r11 = android.text.TextUtils.isEmpty(r5)
                if (r11 != 0) goto L_0x013c
                java.lang.String r11 = r9.toString()
                java.lang.StringBuilder r12 = new java.lang.StringBuilder
                r12.<init>()
                r12.append(r10)
                r12.append(r5)
                java.lang.String r12 = r12.toString()
                boolean r11 = r11.startsWith(r12)
                if (r11 == 0) goto L_0x013c
                r11 = r6
                goto L_0x013e
            L_0x013c:
                r11 = r9
                r9 = r6
            L_0x013e:
                r6 = r0
                r0 = 1
                goto L_0x0146
            L_0x0141:
                int r0 = r0 - r11
            L_0x0142:
                r11 = r6
                r6 = r0
                r0 = r9
                r9 = r11
            L_0x0146:
                r12 = 33
                java.lang.String r13 = "windowBackgroundWhiteBlueText4"
                r14 = -1
                if (r0 != 0) goto L_0x019d
                if (r5 == 0) goto L_0x019d
                org.telegram.ui.Adapters.SearchAdapterHelper r0 = r1.searchAdapterHelper
                java.util.ArrayList r0 = r0.getGlobalSearch()
                int r0 = r0.size()
                if (r0 == 0) goto L_0x019d
                int r0 = r0 + r4
                if (r0 <= r6) goto L_0x019d
                org.telegram.ui.Adapters.SearchAdapterHelper r0 = r1.searchAdapterHelper
                java.lang.String r0 = r0.getLastFoundUsername()
                boolean r9 = r0.startsWith(r10)
                if (r9 == 0) goto L_0x016e
                java.lang.String r0 = r0.substring(r4)
            L_0x016e:
                android.text.SpannableStringBuilder r9 = new android.text.SpannableStringBuilder     // Catch:{ Exception -> 0x0198 }
                r9.<init>()     // Catch:{ Exception -> 0x0198 }
                r9.append(r10)     // Catch:{ Exception -> 0x0198 }
                r9.append(r5)     // Catch:{ Exception -> 0x0198 }
                int r4 = org.telegram.messenger.AndroidUtilities.indexOfIgnoreCase(r5, r0)     // Catch:{ Exception -> 0x0198 }
                if (r4 == r14) goto L_0x019d
                int r0 = r0.length()     // Catch:{ Exception -> 0x0198 }
                if (r4 != 0) goto L_0x0188
                int r0 = r0 + 1
                goto L_0x018a
            L_0x0188:
                int r4 = r4 + 1
            L_0x018a:
                android.text.style.ForegroundColorSpan r10 = new android.text.style.ForegroundColorSpan     // Catch:{ Exception -> 0x0198 }
                int r15 = org.telegram.ui.ActionBar.Theme.getColor(r13)     // Catch:{ Exception -> 0x0198 }
                r10.<init>(r15)     // Catch:{ Exception -> 0x0198 }
                int r0 = r0 + r4
                r9.setSpan(r10, r4, r0, r12)     // Catch:{ Exception -> 0x0198 }
                goto L_0x019d
            L_0x0198:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
                r9 = r5
            L_0x019d:
                if (r7 == 0) goto L_0x01bd
                if (r5 == 0) goto L_0x01bd
                android.text.SpannableStringBuilder r11 = new android.text.SpannableStringBuilder
                r11.<init>(r5)
                int r0 = org.telegram.messenger.AndroidUtilities.indexOfIgnoreCase(r5, r7)
                if (r0 == r14) goto L_0x01bd
                android.text.style.ForegroundColorSpan r4 = new android.text.style.ForegroundColorSpan
                int r5 = org.telegram.ui.ActionBar.Theme.getColor(r13)
                r4.<init>(r5)
                int r5 = r7.length()
                int r5 = r5 + r0
                r11.setSpan(r4, r0, r5, r12)
            L_0x01bd:
                android.view.View r0 = r2.itemView
                org.telegram.ui.Cells.ManageChatUserCell r0 = (org.telegram.ui.Cells.ManageChatUserCell) r0
                java.lang.Integer r2 = java.lang.Integer.valueOf(r6)
                r0.setTag(r2)
                r0.setData(r3, r11, r9, r8)
            L_0x01cb:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatUsersActivity.SearchAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        public void onViewRecycled(RecyclerView.ViewHolder viewHolder) {
            View view = viewHolder.itemView;
            if (view instanceof ManageChatUserCell) {
                ((ManageChatUserCell) view).recycle();
            }
        }

        public int getItemViewType(int i) {
            return (i == this.globalStartRow || i == this.groupStartRow || i == this.contactsStartRow) ? 1 : 0;
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 7) {
                return ChatObject.canBlockUsers(ChatUsersActivity.this.currentChat);
            }
            if (itemViewType == 0) {
                Object currentObject = ((ManageChatUserCell) viewHolder.itemView).getCurrentObject();
                return ChatUsersActivity.this.type == 1 || !(currentObject instanceof TLRPC$User) || !((TLRPC$User) currentObject).self;
            } else if (itemViewType == 0 || itemViewType == 2 || itemViewType == 6) {
                return true;
            } else {
                return false;
            }
        }

        public int getItemCount() {
            return ChatUsersActivity.this.rowCount;
        }

        /* access modifiers changed from: private */
        public /* synthetic */ boolean lambda$onCreateViewHolder$0(ManageChatUserCell manageChatUserCell, boolean z) {
            return ChatUsersActivity.this.createMenuForParticipant(ChatUsersActivity.this.listViewAdapter.getItem(((Integer) manageChatUserCell.getTag()).intValue()), !z);
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v2, resolved type: org.telegram.ui.Cells.ManageChatUserCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v3, resolved type: org.telegram.ui.Cells.TextInfoPrivacyCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v4, resolved type: org.telegram.ui.Cells.ManageChatTextCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v5, resolved type: org.telegram.ui.Cells.ShadowSectionCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v6, resolved type: org.telegram.ui.ChatUsersActivity$ListAdapter$1} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v8, resolved type: org.telegram.ui.Cells.TextSettingsCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v9, resolved type: org.telegram.ui.Cells.TextCheckCell2} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v10, resolved type: org.telegram.ui.Cells.GraySectionCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v11, resolved type: org.telegram.ui.Cells.LoadingCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v12, resolved type: org.telegram.ui.Components.FlickerLoadingView} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v13, resolved type: org.telegram.ui.ChatUsersActivity$ChooseView} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v14, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v15, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v16, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v17, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v18, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v2, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v19, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v20, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v21, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v22, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v23, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v24, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX WARNING: type inference failed for: r14v1, types: [android.view.View] */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r13, int r14) {
            /*
                r12 = this;
                r13 = 0
                r0 = 6
                r1 = -1
                java.lang.String r2 = "windowBackgroundWhite"
                r3 = 1
                switch(r14) {
                    case 0: goto L_0x01a7;
                    case 1: goto L_0x019f;
                    case 2: goto L_0x0190;
                    case 3: goto L_0x0188;
                    case 4: goto L_0x00a4;
                    case 5: goto L_0x0087;
                    case 6: goto L_0x0077;
                    case 7: goto L_0x0067;
                    case 8: goto L_0x005a;
                    case 9: goto L_0x0009;
                    case 10: goto L_0x0045;
                    case 11: goto L_0x001b;
                    default: goto L_0x0009;
                }
            L_0x0009:
                org.telegram.ui.ChatUsersActivity$ChooseView r14 = new org.telegram.ui.ChatUsersActivity$ChooseView
                org.telegram.ui.ChatUsersActivity r13 = org.telegram.ui.ChatUsersActivity.this
                android.content.Context r0 = r12.mContext
                r14.<init>(r0)
                int r13 = org.telegram.ui.ActionBar.Theme.getColor(r2)
                r14.setBackgroundColor(r13)
                goto L_0x01ed
            L_0x001b:
                org.telegram.ui.Components.FlickerLoadingView r14 = new org.telegram.ui.Components.FlickerLoadingView
                android.content.Context r4 = r12.mContext
                r14.<init>(r4)
                r14.setIsSingleCell(r3)
                r14.setViewType(r0)
                r14.showDate(r13)
                r13 = 1084227584(0x40a00000, float:5.0)
                int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
                r14.setPaddingLeft(r13)
                int r13 = org.telegram.ui.ActionBar.Theme.getColor(r2)
                r14.setBackgroundColor(r13)
                androidx.recyclerview.widget.RecyclerView$LayoutParams r13 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r13.<init>((int) r1, (int) r1)
                r14.setLayoutParams(r13)
                goto L_0x01ed
            L_0x0045:
                org.telegram.ui.Cells.LoadingCell r14 = new org.telegram.ui.Cells.LoadingCell
                android.content.Context r13 = r12.mContext
                r0 = 1109393408(0x42200000, float:40.0)
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
                r1 = 1123024896(0x42var_, float:120.0)
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                r14.<init>(r13, r0, r1)
                goto L_0x01ed
            L_0x005a:
                org.telegram.ui.Cells.GraySectionCell r14 = new org.telegram.ui.Cells.GraySectionCell
                android.content.Context r13 = r12.mContext
                r14.<init>(r13)
                r13 = 0
                r14.setBackground(r13)
                goto L_0x01ed
            L_0x0067:
                org.telegram.ui.Cells.TextCheckCell2 r14 = new org.telegram.ui.Cells.TextCheckCell2
                android.content.Context r13 = r12.mContext
                r14.<init>(r13)
                int r13 = org.telegram.ui.ActionBar.Theme.getColor(r2)
                r14.setBackgroundColor(r13)
                goto L_0x01ed
            L_0x0077:
                org.telegram.ui.Cells.TextSettingsCell r14 = new org.telegram.ui.Cells.TextSettingsCell
                android.content.Context r13 = r12.mContext
                r14.<init>(r13)
                int r13 = org.telegram.ui.ActionBar.Theme.getColor(r2)
                r14.setBackgroundColor(r13)
                goto L_0x01ed
            L_0x0087:
                org.telegram.ui.Cells.HeaderCell r14 = new org.telegram.ui.Cells.HeaderCell
                android.content.Context r4 = r12.mContext
                r6 = 21
                r7 = 11
                r8 = 0
                java.lang.String r5 = "windowBackgroundWhiteBlueHeader"
                r3 = r14
                r3.<init>(r4, r5, r6, r7, r8)
                int r13 = org.telegram.ui.ActionBar.Theme.getColor(r2)
                r14.setBackgroundColor(r13)
                r13 = 43
                r14.setHeight(r13)
                goto L_0x01ed
            L_0x00a4:
                org.telegram.ui.ChatUsersActivity$ListAdapter$1 r14 = new org.telegram.ui.ChatUsersActivity$ListAdapter$1
                android.content.Context r13 = r12.mContext
                r14.<init>(r12, r13)
                android.content.Context r13 = r12.mContext
                r0 = 2131165449(0x7var_, float:1.7945115E38)
                java.lang.String r2 = "windowBackgroundGrayShadow"
                android.graphics.drawable.Drawable r13 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r13, (int) r0, (java.lang.String) r2)
                r14.setBackgroundDrawable(r13)
                android.widget.LinearLayout r13 = new android.widget.LinearLayout
                android.content.Context r0 = r12.mContext
                r13.<init>(r0)
                r13.setOrientation(r3)
                r4 = -2
                r5 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
                r6 = 17
                r7 = 1101004800(0x41a00000, float:20.0)
                r8 = 0
                r9 = 1101004800(0x41a00000, float:20.0)
                r10 = 0
                android.widget.FrameLayout$LayoutParams r0 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r5, r6, r7, r8, r9, r10)
                r14.addView(r13, r0)
                android.widget.ImageView r0 = new android.widget.ImageView
                android.content.Context r2 = r12.mContext
                r0.<init>(r2)
                r2 = 2131165454(0x7var_e, float:1.7945126E38)
                r0.setImageResource(r2)
                android.widget.ImageView$ScaleType r2 = android.widget.ImageView.ScaleType.CENTER
                r0.setScaleType(r2)
                android.graphics.PorterDuffColorFilter r2 = new android.graphics.PorterDuffColorFilter
                java.lang.String r4 = "emptyListPlaceholder"
                int r5 = org.telegram.ui.ActionBar.Theme.getColor(r4)
                android.graphics.PorterDuff$Mode r6 = android.graphics.PorterDuff.Mode.MULTIPLY
                r2.<init>(r5, r6)
                r0.setColorFilter(r2)
                r2 = -2
                android.widget.LinearLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r2, (int) r2, (int) r3)
                r13.addView(r0, r2)
                android.widget.TextView r0 = new android.widget.TextView
                android.content.Context r2 = r12.mContext
                r0.<init>(r2)
                r2 = 2131626396(0x7f0e099c, float:1.8880027E38)
                java.lang.String r5 = "NoBlockedUsers"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r5, r2)
                r0.setText(r2)
                int r2 = org.telegram.ui.ActionBar.Theme.getColor(r4)
                r0.setTextColor(r2)
                r2 = 1098907648(0x41800000, float:16.0)
                r0.setTextSize(r3, r2)
                r0.setGravity(r3)
                java.lang.String r2 = "fonts/rmedium.ttf"
                android.graphics.Typeface r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r2)
                r0.setTypeface(r2)
                r5 = -2
                r6 = -2
                r7 = 1
                r8 = 0
                r9 = 10
                r10 = 0
                r11 = 0
                android.widget.LinearLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r5, (int) r6, (int) r7, (int) r8, (int) r9, (int) r10, (int) r11)
                r13.addView(r0, r2)
                android.widget.TextView r0 = new android.widget.TextView
                android.content.Context r2 = r12.mContext
                r0.<init>(r2)
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                boolean r2 = r2.isChannel
                if (r2 == 0) goto L_0x0155
                r2 = 2131626394(0x7f0e099a, float:1.8880023E38)
                java.lang.String r5 = "NoBlockedChannel2"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r5, r2)
                r0.setText(r2)
                goto L_0x0161
            L_0x0155:
                r2 = 2131626395(0x7f0e099b, float:1.8880025E38)
                java.lang.String r5 = "NoBlockedGroup2"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r5, r2)
                r0.setText(r2)
            L_0x0161:
                int r2 = org.telegram.ui.ActionBar.Theme.getColor(r4)
                r0.setTextColor(r2)
                r2 = 1097859072(0x41700000, float:15.0)
                r0.setTextSize(r3, r2)
                r0.setGravity(r3)
                r4 = -2
                r5 = -2
                r6 = 1
                r7 = 0
                r8 = 10
                r9 = 0
                r10 = 0
                android.widget.LinearLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r4, (int) r5, (int) r6, (int) r7, (int) r8, (int) r9, (int) r10)
                r13.addView(r0, r2)
                androidx.recyclerview.widget.RecyclerView$LayoutParams r13 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r13.<init>((int) r1, (int) r1)
                r14.setLayoutParams(r13)
                goto L_0x01ed
            L_0x0188:
                org.telegram.ui.Cells.ShadowSectionCell r14 = new org.telegram.ui.Cells.ShadowSectionCell
                android.content.Context r13 = r12.mContext
                r14.<init>(r13)
                goto L_0x01ed
            L_0x0190:
                org.telegram.ui.Cells.ManageChatTextCell r14 = new org.telegram.ui.Cells.ManageChatTextCell
                android.content.Context r13 = r12.mContext
                r14.<init>(r13)
                int r13 = org.telegram.ui.ActionBar.Theme.getColor(r2)
                r14.setBackgroundColor(r13)
                goto L_0x01ed
            L_0x019f:
                org.telegram.ui.Cells.TextInfoPrivacyCell r14 = new org.telegram.ui.Cells.TextInfoPrivacyCell
                android.content.Context r13 = r12.mContext
                r14.<init>(r13)
                goto L_0x01ed
            L_0x01a7:
                org.telegram.ui.Cells.ManageChatUserCell r14 = new org.telegram.ui.Cells.ManageChatUserCell
                android.content.Context r1 = r12.mContext
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                int r4 = r4.type
                r5 = 3
                if (r4 == 0) goto L_0x01bf
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                int r4 = r4.type
                if (r4 != r5) goto L_0x01bd
                goto L_0x01bf
            L_0x01bd:
                r4 = 6
                goto L_0x01c0
            L_0x01bf:
                r4 = 7
            L_0x01c0:
                org.telegram.ui.ChatUsersActivity r6 = org.telegram.ui.ChatUsersActivity.this
                int r6 = r6.type
                if (r6 == 0) goto L_0x01d2
                org.telegram.ui.ChatUsersActivity r6 = org.telegram.ui.ChatUsersActivity.this
                int r6 = r6.type
                if (r6 != r5) goto L_0x01d1
                goto L_0x01d2
            L_0x01d1:
                r0 = 2
            L_0x01d2:
                org.telegram.ui.ChatUsersActivity r5 = org.telegram.ui.ChatUsersActivity.this
                int r5 = r5.selectType
                if (r5 != 0) goto L_0x01db
                r13 = 1
            L_0x01db:
                r14.<init>(r1, r4, r0, r13)
                int r13 = org.telegram.ui.ActionBar.Theme.getColor(r2)
                r14.setBackgroundColor(r13)
                org.telegram.ui.ChatUsersActivity$ListAdapter$$ExternalSyntheticLambda0 r13 = new org.telegram.ui.ChatUsersActivity$ListAdapter$$ExternalSyntheticLambda0
                r13.<init>(r12)
                r14.setDelegate(r13)
            L_0x01ed:
                org.telegram.ui.Components.RecyclerListView$Holder r13 = new org.telegram.ui.Components.RecyclerListView$Holder
                r13.<init>(r14)
                return r13
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatUsersActivity.ListAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        /* JADX WARNING: Code restructure failed: missing block: B:250:0x0666, code lost:
            if (org.telegram.ui.ChatUsersActivity.access$2600(r0.this$0).megagroup == false) goto L_0x0668;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:259:0x0694, code lost:
            if (org.telegram.ui.ChatUsersActivity.access$2600(r0.this$0).megagroup == false) goto L_0x0668;
         */
        /* JADX WARNING: Removed duplicated region for block: B:264:0x06a2  */
        /* JADX WARNING: Removed duplicated region for block: B:266:0x06bd  */
        /* JADX WARNING: Removed duplicated region for block: B:271:0x06d8  */
        /* JADX WARNING: Removed duplicated region for block: B:272:0x06eb  */
        /* JADX WARNING: Removed duplicated region for block: B:274:0x0700  */
        /* JADX WARNING: Removed duplicated region for block: B:308:0x07b0  */
        /* JADX WARNING: Removed duplicated region for block: B:309:0x07b3  */
        /* JADX WARNING: Removed duplicated region for block: B:364:? A[RETURN, SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r21, int r22) {
            /*
                r20 = this;
                r0 = r20
                r1 = r21
                r2 = r22
                int r3 = r21.getItemViewType()
                r4 = 2
                r5 = 3
                r6 = 0
                r7 = 0
                r8 = 1
                if (r3 == 0) goto L_0x062d
                r9 = 2131165448(0x7var_, float:1.7945113E38)
                java.lang.String r10 = ""
                r11 = 2131165449(0x7var_, float:1.7945115E38)
                r12 = -1
                java.lang.String r13 = "windowBackgroundGrayShadow"
                if (r3 == r8) goto L_0x050d
                if (r3 == r4) goto L_0x03e9
                if (r3 == r5) goto L_0x03a6
                r4 = 5
                if (r3 == r4) goto L_0x030c
                r4 = 6
                if (r3 == r4) goto L_0x02da
                r4 = 7
                if (r3 == r4) goto L_0x00f7
                r4 = 8
                if (r3 == r4) goto L_0x005c
                r2 = 11
                if (r3 == r2) goto L_0x0035
                goto L_0x07df
            L_0x0035:
                android.view.View r1 = r1.itemView
                org.telegram.ui.Components.FlickerLoadingView r1 = (org.telegram.ui.Components.FlickerLoadingView) r1
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                int r2 = r2.type
                if (r2 != 0) goto L_0x0057
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r2 = r2.info
                if (r2 != 0) goto L_0x004a
                goto L_0x0052
            L_0x004a:
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r2 = r2.info
                int r8 = r2.kicked_count
            L_0x0052:
                r1.setItemsCount(r8)
                goto L_0x07df
            L_0x0057:
                r1.setItemsCount(r8)
                goto L_0x07df
            L_0x005c:
                android.view.View r1 = r1.itemView
                org.telegram.ui.Cells.GraySectionCell r1 = (org.telegram.ui.Cells.GraySectionCell) r1
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.membersHeaderRow
                if (r2 != r3) goto L_0x009a
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$Chat r2 = r2.currentChat
                boolean r2 = org.telegram.messenger.ChatObject.isChannel(r2)
                if (r2 == 0) goto L_0x008c
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$Chat r2 = r2.currentChat
                boolean r2 = r2.megagroup
                if (r2 != 0) goto L_0x008c
                r2 = 2131624779(0x7f0e034b, float:1.8876747E38)
                java.lang.String r3 = "ChannelOtherSubscribers"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2)
                goto L_0x07df
            L_0x008c:
                r2 = 2131624777(0x7f0e0349, float:1.8876743E38)
                java.lang.String r3 = "ChannelOtherMembers"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2)
                goto L_0x07df
            L_0x009a:
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.botHeaderRow
                if (r2 != r3) goto L_0x00b0
                r2 = 2131624726(0x7f0e0316, float:1.887664E38)
                java.lang.String r3 = "ChannelBots"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2)
                goto L_0x07df
            L_0x00b0:
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.contactsHeaderRow
                if (r2 != r3) goto L_0x00ea
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$Chat r2 = r2.currentChat
                boolean r2 = org.telegram.messenger.ChatObject.isChannel(r2)
                if (r2 == 0) goto L_0x00dc
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$Chat r2 = r2.currentChat
                boolean r2 = r2.megagroup
                if (r2 != 0) goto L_0x00dc
                r2 = 2131624733(0x7f0e031d, float:1.8876654E38)
                java.lang.String r3 = "ChannelContacts"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2)
                goto L_0x07df
            L_0x00dc:
                r2 = 2131625787(0x7f0e073b, float:1.8878792E38)
                java.lang.String r3 = "GroupContacts"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2)
                goto L_0x07df
            L_0x00ea:
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.loadingHeaderRow
                if (r2 != r3) goto L_0x07df
                r1.setText(r10)
                goto L_0x07df
            L_0x00f7:
                android.view.View r1 = r1.itemView
                org.telegram.ui.Cells.TextCheckCell2 r1 = (org.telegram.ui.Cells.TextCheckCell2) r1
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.changeInfoRow
                if (r2 != r3) goto L_0x012c
                r3 = 2131628143(0x7f0e106f, float:1.888357E38)
                java.lang.String r4 = "UserRestrictionsChangeInfo"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$TL_chatBannedRights r4 = r4.defaultBannedRights
                boolean r4 = r4.change_info
                if (r4 != 0) goto L_0x0126
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$Chat r4 = r4.currentChat
                java.lang.String r4 = r4.username
                boolean r4 = android.text.TextUtils.isEmpty(r4)
                if (r4 == 0) goto L_0x0126
                r4 = 1
                goto L_0x0127
            L_0x0126:
                r4 = 0
            L_0x0127:
                r1.setTextAndCheck(r3, r4, r7)
                goto L_0x0212
            L_0x012c:
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.addUsersRow
                if (r2 != r3) goto L_0x014b
                r3 = 2131628148(0x7f0e1074, float:1.888358E38)
                java.lang.String r4 = "UserRestrictionsInviteUsers"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$TL_chatBannedRights r4 = r4.defaultBannedRights
                boolean r4 = r4.invite_users
                r4 = r4 ^ r8
                r1.setTextAndCheck(r3, r4, r8)
                goto L_0x0212
            L_0x014b:
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.pinMessagesRow
                if (r2 != r3) goto L_0x017c
                r3 = 2131628158(0x7f0e107e, float:1.88836E38)
                java.lang.String r4 = "UserRestrictionsPinMessages"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$TL_chatBannedRights r4 = r4.defaultBannedRights
                boolean r4 = r4.pin_messages
                if (r4 != 0) goto L_0x0176
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$Chat r4 = r4.currentChat
                java.lang.String r4 = r4.username
                boolean r4 = android.text.TextUtils.isEmpty(r4)
                if (r4 == 0) goto L_0x0176
                r4 = 1
                goto L_0x0177
            L_0x0176:
                r4 = 0
            L_0x0177:
                r1.setTextAndCheck(r3, r4, r8)
                goto L_0x0212
            L_0x017c:
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.sendMessagesRow
                if (r2 != r3) goto L_0x019b
                r3 = 2131628160(0x7f0e1080, float:1.8883605E38)
                java.lang.String r4 = "UserRestrictionsSend"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$TL_chatBannedRights r4 = r4.defaultBannedRights
                boolean r4 = r4.send_messages
                r4 = r4 ^ r8
                r1.setTextAndCheck(r3, r4, r8)
                goto L_0x0212
            L_0x019b:
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.sendMediaRow
                if (r2 != r3) goto L_0x01b9
                r3 = 2131628161(0x7f0e1081, float:1.8883607E38)
                java.lang.String r4 = "UserRestrictionsSendMedia"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$TL_chatBannedRights r4 = r4.defaultBannedRights
                boolean r4 = r4.send_media
                r4 = r4 ^ r8
                r1.setTextAndCheck(r3, r4, r8)
                goto L_0x0212
            L_0x01b9:
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.sendStickersRow
                if (r2 != r3) goto L_0x01d7
                r3 = 2131628163(0x7f0e1083, float:1.888361E38)
                java.lang.String r4 = "UserRestrictionsSendStickers"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$TL_chatBannedRights r4 = r4.defaultBannedRights
                boolean r4 = r4.send_stickers
                r4 = r4 ^ r8
                r1.setTextAndCheck(r3, r4, r8)
                goto L_0x0212
            L_0x01d7:
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.embedLinksRow
                if (r2 != r3) goto L_0x01f5
                r3 = 2131628147(0x7f0e1073, float:1.8883578E38)
                java.lang.String r4 = "UserRestrictionsEmbedLinks"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$TL_chatBannedRights r4 = r4.defaultBannedRights
                boolean r4 = r4.embed_links
                r4 = r4 ^ r8
                r1.setTextAndCheck(r3, r4, r8)
                goto L_0x0212
            L_0x01f5:
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.sendPollsRow
                if (r2 != r3) goto L_0x0212
                r3 = 2131628162(0x7f0e1082, float:1.8883609E38)
                java.lang.String r4 = "UserRestrictionsSendPolls"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$TL_chatBannedRights r4 = r4.defaultBannedRights
                boolean r4 = r4.send_polls
                r4 = r4 ^ r8
                r1.setTextAndCheck(r3, r4, r8)
            L_0x0212:
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.sendMediaRow
                if (r2 == r3) goto L_0x0248
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.sendStickersRow
                if (r2 == r3) goto L_0x0248
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.embedLinksRow
                if (r2 == r3) goto L_0x0248
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.sendPollsRow
                if (r2 != r3) goto L_0x0233
                goto L_0x0248
            L_0x0233:
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.sendMessagesRow
                if (r2 != r3) goto L_0x0262
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$TL_chatBannedRights r3 = r3.defaultBannedRights
                boolean r3 = r3.view_messages
                r3 = r3 ^ r8
                r1.setEnabled(r3)
                goto L_0x0262
            L_0x0248:
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$TL_chatBannedRights r3 = r3.defaultBannedRights
                boolean r3 = r3.send_messages
                if (r3 != 0) goto L_0x025e
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$TL_chatBannedRights r3 = r3.defaultBannedRights
                boolean r3 = r3.view_messages
                if (r3 != 0) goto L_0x025e
                r3 = 1
                goto L_0x025f
            L_0x025e:
                r3 = 0
            L_0x025f:
                r1.setEnabled(r3)
            L_0x0262:
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$Chat r3 = r3.currentChat
                boolean r3 = org.telegram.messenger.ChatObject.canBlockUsers(r3)
                if (r3 == 0) goto L_0x02d5
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.addUsersRow
                if (r2 != r3) goto L_0x0282
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$Chat r3 = r3.currentChat
                boolean r3 = org.telegram.messenger.ChatObject.canUserDoAdminAction(r3, r5)
                if (r3 == 0) goto L_0x02c8
            L_0x0282:
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.pinMessagesRow
                if (r2 != r3) goto L_0x0296
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$Chat r3 = r3.currentChat
                boolean r3 = org.telegram.messenger.ChatObject.canUserDoAdminAction(r3, r7)
                if (r3 == 0) goto L_0x02c8
            L_0x0296:
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.changeInfoRow
                if (r2 != r3) goto L_0x02aa
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$Chat r3 = r3.currentChat
                boolean r3 = org.telegram.messenger.ChatObject.canUserDoAdminAction(r3, r8)
                if (r3 == 0) goto L_0x02c8
            L_0x02aa:
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$Chat r3 = r3.currentChat
                java.lang.String r3 = r3.username
                boolean r3 = android.text.TextUtils.isEmpty(r3)
                if (r3 != 0) goto L_0x02d0
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.pinMessagesRow
                if (r2 == r3) goto L_0x02c8
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.changeInfoRow
                if (r2 != r3) goto L_0x02d0
            L_0x02c8:
                r2 = 2131165928(0x7var_e8, float:1.7946087E38)
                r1.setIcon(r2)
                goto L_0x07df
            L_0x02d0:
                r1.setIcon(r7)
                goto L_0x07df
            L_0x02d5:
                r1.setIcon(r7)
                goto L_0x07df
            L_0x02da:
                android.view.View r1 = r1.itemView
                org.telegram.ui.Cells.TextSettingsCell r1 = (org.telegram.ui.Cells.TextSettingsCell) r1
                r2 = 2131624723(0x7f0e0313, float:1.8876634E38)
                java.lang.String r3 = "ChannelBlacklist"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                java.lang.Object[] r3 = new java.lang.Object[r8]
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r4 = r4.info
                if (r4 == 0) goto L_0x02fa
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r4 = r4.info
                int r4 = r4.kicked_count
                goto L_0x02fb
            L_0x02fa:
                r4 = 0
            L_0x02fb:
                java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
                r3[r7] = r4
                java.lang.String r4 = "%d"
                java.lang.String r3 = java.lang.String.format(r4, r3)
                r1.setTextAndValue(r2, r3, r7)
                goto L_0x07df
            L_0x030c:
                android.view.View r1 = r1.itemView
                org.telegram.ui.Cells.HeaderCell r1 = (org.telegram.ui.Cells.HeaderCell) r1
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.restricted1SectionRow
                if (r2 != r3) goto L_0x0364
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                int r2 = r2.type
                if (r2 != 0) goto L_0x0356
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r2 = r2.info
                if (r2 == 0) goto L_0x0331
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r2 = r2.info
                int r2 = r2.kicked_count
                goto L_0x033b
            L_0x0331:
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                java.util.ArrayList r2 = r2.participants
                int r2 = r2.size()
            L_0x033b:
                if (r2 == 0) goto L_0x0348
                java.lang.String r3 = "RemovedUser"
                java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r3, r2)
                r1.setText(r2)
                goto L_0x07df
            L_0x0348:
                r2 = 2131624725(0x7f0e0315, float:1.8876638E38)
                java.lang.String r3 = "ChannelBlockedUsers"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2)
                goto L_0x07df
            L_0x0356:
                r2 = 2131624793(0x7f0e0359, float:1.8876776E38)
                java.lang.String r3 = "ChannelRestrictedUsers"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2)
                goto L_0x07df
            L_0x0364:
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.permissionsSectionRow
                if (r2 != r3) goto L_0x037a
                r2 = 2131624781(0x7f0e034d, float:1.8876751E38)
                java.lang.String r3 = "ChannelPermissionsHeader"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2)
                goto L_0x07df
            L_0x037a:
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.slowmodeRow
                if (r2 != r3) goto L_0x0390
                r2 = 2131627739(0x7f0e0edb, float:1.888275E38)
                java.lang.String r3 = "Slowmode"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2)
                goto L_0x07df
            L_0x0390:
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.gigaHeaderRow
                if (r2 != r3) goto L_0x07df
                r2 = 2131624611(0x7f0e02a3, float:1.8876407E38)
                java.lang.String r3 = "BroadcastGroup"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2)
                goto L_0x07df
            L_0x03a6:
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.addNewSectionRow
                if (r2 == r3) goto L_0x03dc
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.type
                if (r3 != r5) goto L_0x03cf
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.participantsDividerRow
                if (r2 != r3) goto L_0x03cf
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                int r2 = r2.addNewRow
                if (r2 != r12) goto L_0x03cf
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                int r2 = r2.participantsStartRow
                if (r2 != r12) goto L_0x03cf
                goto L_0x03dc
            L_0x03cf:
                android.view.View r1 = r1.itemView
                android.content.Context r2 = r0.mContext
                android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r2, (int) r9, (java.lang.String) r13)
                r1.setBackgroundDrawable(r2)
                goto L_0x07df
            L_0x03dc:
                android.view.View r1 = r1.itemView
                android.content.Context r2 = r0.mContext
                android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r2, (int) r11, (java.lang.String) r13)
                r1.setBackgroundDrawable(r2)
                goto L_0x07df
            L_0x03e9:
                android.view.View r1 = r1.itemView
                org.telegram.ui.Cells.ManageChatTextCell r1 = (org.telegram.ui.Cells.ManageChatTextCell) r1
                java.lang.String r3 = "windowBackgroundWhiteGrayIcon"
                java.lang.String r9 = "windowBackgroundWhiteBlackText"
                r1.setColors(r3, r9)
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.addNewRow
                java.lang.String r9 = "windowBackgroundWhiteBlueButton"
                java.lang.String r10 = "windowBackgroundWhiteBlueIcon"
                if (r2 != r3) goto L_0x04bf
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                int r2 = r2.type
                r3 = 2131165249(0x7var_, float:1.794471E38)
                if (r2 != r5) goto L_0x0425
                r1.setColors(r10, r9)
                r2 = 2131624709(0x7f0e0305, float:1.8876605E38)
                java.lang.String r4 = "ChannelAddException"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r2)
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                int r4 = r4.participantsStartRow
                if (r4 == r12) goto L_0x0420
                r7 = 1
            L_0x0420:
                r1.setText(r2, r6, r3, r7)
                goto L_0x07df
            L_0x0425:
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                int r2 = r2.type
                if (r2 != 0) goto L_0x043e
                r2 = 2131624724(0x7f0e0314, float:1.8876636E38)
                java.lang.String r3 = "ChannelBlockUser"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r3 = 2131165255(0x7var_, float:1.7944722E38)
                r1.setText(r2, r6, r3, r7)
                goto L_0x07df
            L_0x043e:
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                int r2 = r2.type
                if (r2 != r8) goto L_0x046b
                r1.setColors(r10, r9)
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                boolean r2 = r2.loadingUsers
                if (r2 == 0) goto L_0x0459
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                boolean r2 = r2.firstLoaded
                if (r2 == 0) goto L_0x045a
            L_0x0459:
                r7 = 1
            L_0x045a:
                r2 = 2131624708(0x7f0e0304, float:1.8876603E38)
                java.lang.String r3 = "ChannelAddAdmin"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r3 = 2131165259(0x7var_b, float:1.794473E38)
                r1.setText(r2, r6, r3, r7)
                goto L_0x07df
            L_0x046b:
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                int r2 = r2.type
                if (r2 != r4) goto L_0x07df
                r1.setColors(r10, r9)
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                boolean r2 = r2.loadingUsers
                if (r2 == 0) goto L_0x0486
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                boolean r2 = r2.firstLoaded
                if (r2 == 0) goto L_0x049b
            L_0x0486:
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                int r2 = r2.membersHeaderRow
                if (r2 != r12) goto L_0x049b
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                java.util.ArrayList r2 = r2.participants
                boolean r2 = r2.isEmpty()
                if (r2 != 0) goto L_0x049b
                r7 = 1
            L_0x049b:
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                boolean r2 = r2.isChannel
                if (r2 == 0) goto L_0x04b1
                r2 = 2131624227(0x7f0e0123, float:1.8875628E38)
                java.lang.String r4 = "AddSubscriber"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r2)
                r1.setText(r2, r6, r3, r7)
                goto L_0x07df
            L_0x04b1:
                r2 = 2131624209(0x7f0e0111, float:1.8875591E38)
                java.lang.String r4 = "AddMember"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r2)
                r1.setText(r2, r6, r3, r7)
                goto L_0x07df
            L_0x04bf:
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.recentActionsRow
                if (r2 != r3) goto L_0x04d8
                r2 = 2131625395(0x7f0e05b3, float:1.8877997E38)
                java.lang.String r3 = "EventLog"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r3 = 2131165459(0x7var_, float:1.7945136E38)
                r1.setText(r2, r6, r3, r7)
                goto L_0x07df
            L_0x04d8:
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.addNew2Row
                if (r2 != r3) goto L_0x04f1
                r2 = 2131624746(0x7f0e032a, float:1.887668E38)
                java.lang.String r3 = "ChannelInviteViaLink"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r3 = 2131165999(0x7var_f, float:1.794623E38)
                r1.setText(r2, r6, r3, r8)
                goto L_0x07df
            L_0x04f1:
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.gigaConvertRow
                if (r2 != r3) goto L_0x07df
                r1.setColors(r10, r9)
                r2 = 2131624612(0x7f0e02a4, float:1.8876409E38)
                java.lang.String r3 = "BroadcastGroupConvert"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r3 = 2131165728(0x7var_, float:1.7945681E38)
                r1.setText(r2, r6, r3, r7)
                goto L_0x07df
            L_0x050d:
                android.view.View r1 = r1.itemView
                org.telegram.ui.Cells.TextInfoPrivacyCell r1 = (org.telegram.ui.Cells.TextInfoPrivacyCell) r1
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.participantsInfoRow
                if (r2 != r3) goto L_0x05cb
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                int r2 = r2.type
                if (r2 == 0) goto L_0x059f
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                int r2 = r2.type
                if (r2 != r5) goto L_0x052b
                goto L_0x059f
            L_0x052b:
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                int r2 = r2.type
                if (r2 != r8) goto L_0x056b
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                int r2 = r2.addNewRow
                if (r2 == r12) goto L_0x055d
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                boolean r2 = r2.isChannel
                if (r2 == 0) goto L_0x0550
                r2 = 2131624719(0x7f0e030f, float:1.8876626E38)
                java.lang.String r3 = "ChannelAdminsInfo"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2)
                goto L_0x0560
            L_0x0550:
                r2 = 2131626190(0x7f0e08ce, float:1.887961E38)
                java.lang.String r3 = "MegaAdminsInfo"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2)
                goto L_0x0560
            L_0x055d:
                r1.setText(r10)
            L_0x0560:
                android.content.Context r2 = r0.mContext
                android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r2, (int) r11, (java.lang.String) r13)
                r1.setBackgroundDrawable(r2)
                goto L_0x07df
            L_0x056b:
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                int r2 = r2.type
                if (r2 != r4) goto L_0x07df
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                boolean r2 = r2.isChannel
                if (r2 == 0) goto L_0x0591
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                int r2 = r2.selectType
                if (r2 == 0) goto L_0x0584
                goto L_0x0591
            L_0x0584:
                r2 = 2131624756(0x7f0e0334, float:1.88767E38)
                java.lang.String r3 = "ChannelMembersInfo"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2)
                goto L_0x0594
            L_0x0591:
                r1.setText(r10)
            L_0x0594:
                android.content.Context r2 = r0.mContext
                android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r2, (int) r11, (java.lang.String) r13)
                r1.setBackgroundDrawable(r2)
                goto L_0x07df
            L_0x059f:
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                boolean r2 = r2.isChannel
                if (r2 == 0) goto L_0x05b4
                r2 = 2131626394(0x7f0e099a, float:1.8880023E38)
                java.lang.String r3 = "NoBlockedChannel2"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2)
                goto L_0x05c0
            L_0x05b4:
                r2 = 2131626395(0x7f0e099b, float:1.8880025E38)
                java.lang.String r3 = "NoBlockedGroup2"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2)
            L_0x05c0:
                android.content.Context r2 = r0.mContext
                android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r2, (int) r11, (java.lang.String) r13)
                r1.setBackgroundDrawable(r2)
                goto L_0x07df
            L_0x05cb:
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.slowmodeInfoRow
                if (r2 != r3) goto L_0x0617
                android.content.Context r2 = r0.mContext
                android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r2, (int) r9, (java.lang.String) r13)
                r1.setBackgroundDrawable(r2)
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r2.selectedSlowmode
                int r2 = r2.getSecondsForIndex(r3)
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r3 = r3.info
                if (r3 == 0) goto L_0x0609
                if (r2 != 0) goto L_0x05f1
                goto L_0x0609
            L_0x05f1:
                r3 = 2131627742(0x7f0e0ede, float:1.8882757E38)
                java.lang.Object[] r4 = new java.lang.Object[r8]
                org.telegram.ui.ChatUsersActivity r5 = org.telegram.ui.ChatUsersActivity.this
                java.lang.String r2 = r5.formatSeconds(r2)
                r4[r7] = r2
                java.lang.String r2 = "SlowmodeInfoSelected"
                java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)
                r1.setText(r2)
                goto L_0x07df
            L_0x0609:
                r2 = 2131627741(0x7f0e0edd, float:1.8882755E38)
                java.lang.String r3 = "SlowmodeInfoOff"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2)
                goto L_0x07df
            L_0x0617:
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.gigaInfoRow
                if (r2 != r3) goto L_0x07df
                r2 = 2131624613(0x7f0e02a5, float:1.887641E38)
                java.lang.String r3 = "BroadcastGroupConvertInfo"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2)
                goto L_0x07df
            L_0x062d:
                android.view.View r1 = r1.itemView
                org.telegram.ui.Cells.ManageChatUserCell r1 = (org.telegram.ui.Cells.ManageChatUserCell) r1
                java.lang.Integer r3 = java.lang.Integer.valueOf(r22)
                r1.setTag(r3)
                org.telegram.tgnet.TLObject r3 = r0.getItem(r2)
                org.telegram.ui.ChatUsersActivity r9 = org.telegram.ui.ChatUsersActivity.this
                int r9 = r9.participantsStartRow
                if (r2 < r9) goto L_0x066a
                org.telegram.ui.ChatUsersActivity r9 = org.telegram.ui.ChatUsersActivity.this
                int r9 = r9.participantsEndRow
                if (r2 >= r9) goto L_0x066a
                org.telegram.ui.ChatUsersActivity r9 = org.telegram.ui.ChatUsersActivity.this
                int r9 = r9.participantsEndRow
                org.telegram.ui.ChatUsersActivity r10 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$Chat r10 = r10.currentChat
                boolean r10 = org.telegram.messenger.ChatObject.isChannel(r10)
                if (r10 == 0) goto L_0x069d
                org.telegram.ui.ChatUsersActivity r10 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$Chat r10 = r10.currentChat
                boolean r10 = r10.megagroup
                if (r10 != 0) goto L_0x069d
            L_0x0668:
                r10 = 1
                goto L_0x069e
            L_0x066a:
                org.telegram.ui.ChatUsersActivity r9 = org.telegram.ui.ChatUsersActivity.this
                int r9 = r9.contactsStartRow
                if (r2 < r9) goto L_0x0697
                org.telegram.ui.ChatUsersActivity r9 = org.telegram.ui.ChatUsersActivity.this
                int r9 = r9.contactsEndRow
                if (r2 >= r9) goto L_0x0697
                org.telegram.ui.ChatUsersActivity r9 = org.telegram.ui.ChatUsersActivity.this
                int r9 = r9.contactsEndRow
                org.telegram.ui.ChatUsersActivity r10 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$Chat r10 = r10.currentChat
                boolean r10 = org.telegram.messenger.ChatObject.isChannel(r10)
                if (r10 == 0) goto L_0x069d
                org.telegram.ui.ChatUsersActivity r10 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$Chat r10 = r10.currentChat
                boolean r10 = r10.megagroup
                if (r10 != 0) goto L_0x069d
                goto L_0x0668
            L_0x0697:
                org.telegram.ui.ChatUsersActivity r9 = org.telegram.ui.ChatUsersActivity.this
                int r9 = r9.botEndRow
            L_0x069d:
                r10 = 0
            L_0x069e:
                boolean r11 = r3 instanceof org.telegram.tgnet.TLRPC$ChannelParticipant
                if (r11 == 0) goto L_0x06bd
                org.telegram.tgnet.TLRPC$ChannelParticipant r3 = (org.telegram.tgnet.TLRPC$ChannelParticipant) r3
                org.telegram.tgnet.TLRPC$Peer r11 = r3.peer
                long r14 = org.telegram.messenger.MessageObject.getPeerId(r11)
                long r6 = r3.kicked_by
                r21 = r9
                long r8 = r3.promoted_by
                org.telegram.tgnet.TLRPC$TL_chatBannedRights r11 = r3.banned_rights
                int r4 = r3.date
                boolean r5 = r3 instanceof org.telegram.tgnet.TLRPC$TL_channelParticipantBanned
                boolean r12 = r3 instanceof org.telegram.tgnet.TLRPC$TL_channelParticipantCreator
                boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC$TL_channelParticipantAdmin
            L_0x06ba:
                r17 = 0
                goto L_0x06d4
            L_0x06bd:
                r21 = r9
                boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$ChatParticipant
                if (r4 == 0) goto L_0x07df
                org.telegram.tgnet.TLRPC$ChatParticipant r3 = (org.telegram.tgnet.TLRPC$ChatParticipant) r3
                long r14 = r3.user_id
                int r4 = r3.date
                boolean r12 = r3 instanceof org.telegram.tgnet.TLRPC$TL_chatParticipantCreator
                boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC$TL_chatParticipantAdmin
                r5 = 0
                r6 = 0
                r8 = 0
                r11 = 0
                goto L_0x06ba
            L_0x06d4:
                int r13 = (r14 > r17 ? 1 : (r14 == r17 ? 0 : -1))
                if (r13 <= 0) goto L_0x06eb
                org.telegram.ui.ChatUsersActivity r13 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.messenger.MessagesController r13 = r13.getMessagesController()
                r17 = r4
                java.lang.Long r4 = java.lang.Long.valueOf(r14)
                org.telegram.tgnet.TLRPC$User r4 = r13.getUser(r4)
                r18 = r8
                goto L_0x06fe
            L_0x06eb:
                r17 = r4
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.messenger.MessagesController r4 = r4.getMessagesController()
                r18 = r8
                long r8 = -r14
                java.lang.Long r8 = java.lang.Long.valueOf(r8)
                org.telegram.tgnet.TLRPC$Chat r4 = r4.getChat(r8)
            L_0x06fe:
                if (r4 == 0) goto L_0x07df
                org.telegram.ui.ChatUsersActivity r8 = org.telegram.ui.ChatUsersActivity.this
                int r8 = r8.type
                r9 = 3
                if (r8 != r9) goto L_0x071e
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                java.lang.String r3 = r3.formatUserPermissions(r11)
                r5 = 1
                int r9 = r21 + -1
                if (r2 == r9) goto L_0x0717
                r2 = 0
                r7 = 1
                goto L_0x0719
            L_0x0717:
                r2 = 0
                r7 = 0
            L_0x0719:
                r1.setData(r4, r2, r3, r7)
                goto L_0x07df
            L_0x071e:
                org.telegram.ui.ChatUsersActivity r8 = org.telegram.ui.ChatUsersActivity.this
                int r8 = r8.type
                if (r8 != 0) goto L_0x075c
                if (r5 == 0) goto L_0x074c
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.messenger.MessagesController r3 = r3.getMessagesController()
                java.lang.Long r5 = java.lang.Long.valueOf(r6)
                org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r5)
                if (r3 == 0) goto L_0x074c
                r5 = 2131628135(0x7f0e1067, float:1.8883554E38)
                r6 = 1
                java.lang.Object[] r7 = new java.lang.Object[r6]
                java.lang.String r3 = org.telegram.messenger.UserObject.getUserName(r3)
                r8 = 0
                r7[r8] = r3
                java.lang.String r3 = "UserRemovedBy"
                java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r5, r7)
                goto L_0x074e
            L_0x074c:
                r6 = 1
                r3 = 0
            L_0x074e:
                int r9 = r21 + -1
                if (r2 == r9) goto L_0x0755
                r2 = 0
                r7 = 1
                goto L_0x0757
            L_0x0755:
                r2 = 0
                r7 = 0
            L_0x0757:
                r1.setData(r4, r2, r3, r7)
                goto L_0x07df
            L_0x075c:
                r6 = 1
                org.telegram.ui.ChatUsersActivity r5 = org.telegram.ui.ChatUsersActivity.this
                int r5 = r5.type
                if (r5 != r6) goto L_0x07b9
                if (r12 == 0) goto L_0x0773
                r3 = 2131624734(0x7f0e031e, float:1.8876656E38)
                java.lang.String r5 = "ChannelCreator"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r5, r3)
            L_0x0770:
                r6 = 1
                r8 = 0
                goto L_0x07ac
            L_0x0773:
                if (r3 == 0) goto L_0x07a9
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.messenger.MessagesController r3 = r3.getMessagesController()
                java.lang.Long r5 = java.lang.Long.valueOf(r18)
                org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r5)
                if (r3 == 0) goto L_0x07a9
                long r5 = r3.id
                int r7 = (r5 > r14 ? 1 : (r5 == r14 ? 0 : -1))
                if (r7 != 0) goto L_0x0795
                r3 = 2131624717(0x7f0e030d, float:1.8876622E38)
                java.lang.String r5 = "ChannelAdministrator"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r5, r3)
                goto L_0x0770
            L_0x0795:
                r5 = 2131625293(0x7f0e054d, float:1.887779E38)
                r6 = 1
                java.lang.Object[] r7 = new java.lang.Object[r6]
                java.lang.String r3 = org.telegram.messenger.UserObject.getUserName(r3)
                r8 = 0
                r7[r8] = r3
                java.lang.String r3 = "EditAdminPromotedBy"
                java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r5, r7)
                goto L_0x07ac
            L_0x07a9:
                r6 = 1
                r8 = 0
                r3 = 0
            L_0x07ac:
                int r9 = r21 + -1
                if (r2 == r9) goto L_0x07b3
                r2 = 0
                r7 = 1
                goto L_0x07b5
            L_0x07b3:
                r2 = 0
                r7 = 0
            L_0x07b5:
                r1.setData(r4, r2, r3, r7)
                goto L_0x07df
            L_0x07b9:
                r8 = 0
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.type
                r5 = 2
                if (r3 != r5) goto L_0x07df
                if (r10 == 0) goto L_0x07d1
                if (r17 == 0) goto L_0x07d1
                r3 = r17
                long r5 = (long) r3
                java.lang.String r16 = org.telegram.messenger.LocaleController.formatJoined(r5)
                r3 = r16
                goto L_0x07d2
            L_0x07d1:
                r3 = 0
            L_0x07d2:
                r5 = 1
                int r9 = r21 + -1
                if (r2 == r9) goto L_0x07da
                r2 = 0
                r7 = 1
                goto L_0x07dc
            L_0x07da:
                r2 = 0
                r7 = 0
            L_0x07dc:
                r1.setData(r4, r2, r3, r7)
            L_0x07df:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatUsersActivity.ListAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        public void onViewRecycled(RecyclerView.ViewHolder viewHolder) {
            View view = viewHolder.itemView;
            if (view instanceof ManageChatUserCell) {
                ((ManageChatUserCell) view).recycle();
            }
        }

        public int getItemViewType(int i) {
            if (i == ChatUsersActivity.this.addNewRow || i == ChatUsersActivity.this.addNew2Row || i == ChatUsersActivity.this.recentActionsRow || i == ChatUsersActivity.this.gigaConvertRow) {
                return 2;
            }
            if ((i >= ChatUsersActivity.this.participantsStartRow && i < ChatUsersActivity.this.participantsEndRow) || ((i >= ChatUsersActivity.this.botStartRow && i < ChatUsersActivity.this.botEndRow) || (i >= ChatUsersActivity.this.contactsStartRow && i < ChatUsersActivity.this.contactsEndRow))) {
                return 0;
            }
            if (i == ChatUsersActivity.this.addNewSectionRow || i == ChatUsersActivity.this.participantsDividerRow || i == ChatUsersActivity.this.participantsDivider2Row) {
                return 3;
            }
            if (i == ChatUsersActivity.this.restricted1SectionRow || i == ChatUsersActivity.this.permissionsSectionRow || i == ChatUsersActivity.this.slowmodeRow || i == ChatUsersActivity.this.gigaHeaderRow) {
                return 5;
            }
            if (i == ChatUsersActivity.this.participantsInfoRow || i == ChatUsersActivity.this.slowmodeInfoRow || i == ChatUsersActivity.this.gigaInfoRow) {
                return 1;
            }
            if (i == ChatUsersActivity.this.blockedEmptyRow) {
                return 4;
            }
            if (i == ChatUsersActivity.this.removedUsersRow) {
                return 6;
            }
            if (i == ChatUsersActivity.this.changeInfoRow || i == ChatUsersActivity.this.addUsersRow || i == ChatUsersActivity.this.pinMessagesRow || i == ChatUsersActivity.this.sendMessagesRow || i == ChatUsersActivity.this.sendMediaRow || i == ChatUsersActivity.this.sendStickersRow || i == ChatUsersActivity.this.embedLinksRow || i == ChatUsersActivity.this.sendPollsRow) {
                return 7;
            }
            if (i == ChatUsersActivity.this.membersHeaderRow || i == ChatUsersActivity.this.contactsHeaderRow || i == ChatUsersActivity.this.botHeaderRow || i == ChatUsersActivity.this.loadingHeaderRow) {
                return 8;
            }
            if (i == ChatUsersActivity.this.slowmodeSelectRow) {
                return 9;
            }
            if (i == ChatUsersActivity.this.loadingProgressRow) {
                return 10;
            }
            if (i == ChatUsersActivity.this.loadingUserCellRow) {
                return 11;
            }
            return 0;
        }

        public TLObject getItem(int i) {
            if (i >= ChatUsersActivity.this.participantsStartRow && i < ChatUsersActivity.this.participantsEndRow) {
                return (TLObject) ChatUsersActivity.this.participants.get(i - ChatUsersActivity.this.participantsStartRow);
            }
            if (i >= ChatUsersActivity.this.contactsStartRow && i < ChatUsersActivity.this.contactsEndRow) {
                return (TLObject) ChatUsersActivity.this.contacts.get(i - ChatUsersActivity.this.contactsStartRow);
            }
            if (i < ChatUsersActivity.this.botStartRow || i >= ChatUsersActivity.this.botEndRow) {
                return null;
            }
            return (TLObject) ChatUsersActivity.this.bots.get(i - ChatUsersActivity.this.botStartRow);
        }
    }

    public DiffCallback saveState() {
        DiffCallback diffCallback = new DiffCallback();
        diffCallback.oldRowCount = this.rowCount;
        diffCallback.oldBotStartRow = this.botStartRow;
        diffCallback.oldBotEndRow = this.botEndRow;
        diffCallback.oldBots.clear();
        diffCallback.oldBots.addAll(this.bots);
        diffCallback.oldContactsEndRow = this.contactsEndRow;
        diffCallback.oldContactsStartRow = this.contactsStartRow;
        diffCallback.oldContacts.clear();
        diffCallback.oldContacts.addAll(this.contacts);
        diffCallback.oldParticipantsStartRow = this.participantsStartRow;
        diffCallback.oldParticipantsEndRow = this.participantsEndRow;
        diffCallback.oldParticipants.clear();
        diffCallback.oldParticipants.addAll(this.participants);
        diffCallback.fillPositions(diffCallback.oldPositionToItem);
        return diffCallback;
    }

    public void updateListAnimated(DiffCallback diffCallback) {
        if (this.listViewAdapter == null) {
            updateRows();
            return;
        }
        updateRows();
        diffCallback.fillPositions(diffCallback.newPositionToItem);
        DiffUtil.calculateDiff(diffCallback).dispatchUpdatesTo((RecyclerView.Adapter) this.listViewAdapter);
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null && this.layoutManager != null && recyclerListView.getChildCount() > 0) {
            View view = null;
            int i = 0;
            int i2 = -1;
            while (true) {
                if (i >= this.listView.getChildCount()) {
                    break;
                }
                RecyclerListView recyclerListView2 = this.listView;
                i2 = recyclerListView2.getChildAdapterPosition(recyclerListView2.getChildAt(i));
                if (i2 != -1) {
                    view = this.listView.getChildAt(i);
                    break;
                }
                i++;
            }
            if (view != null) {
                this.layoutManager.scrollToPositionWithOffset(i2, view.getTop() - this.listView.getPaddingTop());
            }
        }
    }

    private class DiffCallback extends DiffUtil.Callback {
        SparseIntArray newPositionToItem;
        int oldBotEndRow;
        int oldBotStartRow;
        /* access modifiers changed from: private */
        public ArrayList<TLObject> oldBots;
        /* access modifiers changed from: private */
        public ArrayList<TLObject> oldContacts;
        int oldContactsEndRow;
        int oldContactsStartRow;
        /* access modifiers changed from: private */
        public ArrayList<TLObject> oldParticipants;
        int oldParticipantsEndRow;
        int oldParticipantsStartRow;
        SparseIntArray oldPositionToItem;
        int oldRowCount;

        private DiffCallback() {
            this.oldPositionToItem = new SparseIntArray();
            this.newPositionToItem = new SparseIntArray();
            this.oldParticipants = new ArrayList<>();
            this.oldBots = new ArrayList<>();
            this.oldContacts = new ArrayList<>();
        }

        public int getOldListSize() {
            return this.oldRowCount;
        }

        public int getNewListSize() {
            return ChatUsersActivity.this.rowCount;
        }

        public boolean areItemsTheSame(int i, int i2) {
            if (i >= this.oldBotStartRow && i < this.oldBotEndRow && i2 >= ChatUsersActivity.this.botStartRow && i2 < ChatUsersActivity.this.botEndRow) {
                return this.oldBots.get(i - this.oldBotStartRow).equals(ChatUsersActivity.this.bots.get(i2 - ChatUsersActivity.this.botStartRow));
            }
            if (i >= this.oldContactsStartRow && i < this.oldContactsEndRow && i2 >= ChatUsersActivity.this.contactsStartRow && i2 < ChatUsersActivity.this.contactsEndRow) {
                return this.oldContacts.get(i - this.oldContactsStartRow).equals(ChatUsersActivity.this.contacts.get(i2 - ChatUsersActivity.this.contactsStartRow));
            }
            if (i < this.oldParticipantsStartRow || i >= this.oldParticipantsEndRow || i2 < ChatUsersActivity.this.participantsStartRow || i2 >= ChatUsersActivity.this.participantsEndRow) {
                return this.oldPositionToItem.get(i) == this.newPositionToItem.get(i2);
            }
            return this.oldParticipants.get(i - this.oldParticipantsStartRow).equals(ChatUsersActivity.this.participants.get(i2 - ChatUsersActivity.this.participantsStartRow));
        }

        public boolean areContentsTheSame(int i, int i2) {
            if (!areItemsTheSame(i, i2) || ChatUsersActivity.this.restricted1SectionRow == i2) {
                return false;
            }
            return true;
        }

        public void fillPositions(SparseIntArray sparseIntArray) {
            sparseIntArray.clear();
            put(1, ChatUsersActivity.this.recentActionsRow, sparseIntArray);
            put(2, ChatUsersActivity.this.addNewRow, sparseIntArray);
            put(3, ChatUsersActivity.this.addNew2Row, sparseIntArray);
            put(4, ChatUsersActivity.this.addNewSectionRow, sparseIntArray);
            put(5, ChatUsersActivity.this.restricted1SectionRow, sparseIntArray);
            put(6, ChatUsersActivity.this.participantsDividerRow, sparseIntArray);
            put(7, ChatUsersActivity.this.participantsDivider2Row, sparseIntArray);
            put(8, ChatUsersActivity.this.gigaHeaderRow, sparseIntArray);
            put(9, ChatUsersActivity.this.gigaConvertRow, sparseIntArray);
            put(10, ChatUsersActivity.this.gigaInfoRow, sparseIntArray);
            put(11, ChatUsersActivity.this.participantsInfoRow, sparseIntArray);
            put(12, ChatUsersActivity.this.blockedEmptyRow, sparseIntArray);
            put(13, ChatUsersActivity.this.permissionsSectionRow, sparseIntArray);
            put(14, ChatUsersActivity.this.sendMessagesRow, sparseIntArray);
            put(15, ChatUsersActivity.this.sendMediaRow, sparseIntArray);
            put(16, ChatUsersActivity.this.sendStickersRow, sparseIntArray);
            put(17, ChatUsersActivity.this.sendPollsRow, sparseIntArray);
            put(18, ChatUsersActivity.this.embedLinksRow, sparseIntArray);
            put(19, ChatUsersActivity.this.addUsersRow, sparseIntArray);
            put(20, ChatUsersActivity.this.pinMessagesRow, sparseIntArray);
            put(21, ChatUsersActivity.this.changeInfoRow, sparseIntArray);
            put(22, ChatUsersActivity.this.removedUsersRow, sparseIntArray);
            put(23, ChatUsersActivity.this.contactsHeaderRow, sparseIntArray);
            put(24, ChatUsersActivity.this.botHeaderRow, sparseIntArray);
            put(25, ChatUsersActivity.this.membersHeaderRow, sparseIntArray);
            put(26, ChatUsersActivity.this.slowmodeRow, sparseIntArray);
            put(27, ChatUsersActivity.this.slowmodeSelectRow, sparseIntArray);
            put(28, ChatUsersActivity.this.slowmodeInfoRow, sparseIntArray);
            put(29, ChatUsersActivity.this.loadingProgressRow, sparseIntArray);
            put(30, ChatUsersActivity.this.loadingUserCellRow, sparseIntArray);
            put(31, ChatUsersActivity.this.loadingHeaderRow, sparseIntArray);
        }

        private void put(int i, int i2, SparseIntArray sparseIntArray) {
            if (i2 >= 0) {
                sparseIntArray.put(i2, i);
            }
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        ChatUsersActivity$$ExternalSyntheticLambda15 chatUsersActivity$$ExternalSyntheticLambda15 = new ChatUsersActivity$$ExternalSyntheticLambda15(this);
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class, ManageChatUserCell.class, ManageChatTextCell.class, TextCheckCell2.class, TextSettingsCell.class, ChooseView.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{GraySectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "key_graySectionText"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GraySectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "graySection"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteValueText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell2.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell2.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell2.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switch2Track"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell2.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switch2TrackChecked"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        ChatUsersActivity$$ExternalSyntheticLambda15 chatUsersActivity$$ExternalSyntheticLambda152 = chatUsersActivity$$ExternalSyntheticLambda15;
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) chatUsersActivity$$ExternalSyntheticLambda152, "windowBackgroundWhiteGrayText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) chatUsersActivity$$ExternalSyntheticLambda152, "windowBackgroundWhiteBlueText"));
        arrayList.add(new ThemeDescription(this.undoView, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_background"));
        arrayList.add(new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"undoImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_cancelColor"));
        arrayList.add(new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"undoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_cancelColor"));
        arrayList.add(new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"infoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
        arrayList.add(new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"textPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
        arrayList.add(new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"progressPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
        arrayList.add(new ThemeDescription((View) this.undoView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{UndoView.class}, new String[]{"leftImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueButton"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueIcon"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{StickerEmptyView.class}, new String[]{"title"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{StickerEmptyView.class}, new String[]{"subtitle"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.emptyView.title, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.emptyView.subtitle, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, (Paint) null, Theme.avatarDrawables, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_text"));
        ChatUsersActivity$$ExternalSyntheticLambda15 chatUsersActivity$$ExternalSyntheticLambda153 = chatUsersActivity$$ExternalSyntheticLambda15;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, chatUsersActivity$$ExternalSyntheticLambda153, "avatar_backgroundRed"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, chatUsersActivity$$ExternalSyntheticLambda153, "avatar_backgroundOrange"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, chatUsersActivity$$ExternalSyntheticLambda153, "avatar_backgroundViolet"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, chatUsersActivity$$ExternalSyntheticLambda153, "avatar_backgroundGreen"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, chatUsersActivity$$ExternalSyntheticLambda153, "avatar_backgroundCyan"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, chatUsersActivity$$ExternalSyntheticLambda153, "avatar_backgroundBlue"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, chatUsersActivity$$ExternalSyntheticLambda153, "avatar_backgroundPink"));
        return arrayList;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$getThemeDescriptions$17() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int childCount = recyclerListView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.listView.getChildAt(i);
                if (childAt instanceof ManageChatUserCell) {
                    ((ManageChatUserCell) childAt).update(0);
                }
            }
        }
    }
}
