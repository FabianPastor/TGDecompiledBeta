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
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import j$.util.Comparator;
import j$.util.function.Function;
import j$.util.function.ToDoubleFunction;
import j$.util.function.ToIntFunction;
import j$.util.function.ToLongFunction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.RequestDelegate;
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
import org.telegram.ui.ChatUsersActivity;
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
    private SparseArray<TLObject> botsMap = new SparseArray<>();
    /* access modifiers changed from: private */
    public int changeInfoRow;
    /* access modifiers changed from: private */
    public int chatId = this.arguments.getInt("chat_id");
    /* access modifiers changed from: private */
    public ArrayList<TLObject> contacts = new ArrayList<>();
    private boolean contactsEndReached;
    /* access modifiers changed from: private */
    public int contactsEndRow;
    /* access modifiers changed from: private */
    public int contactsHeaderRow;
    /* access modifiers changed from: private */
    public SparseArray<TLObject> contactsMap = new SparseArray<>();
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
    private SparseArray<TLRPC$TL_groupCallParticipant> ignoredUsers;
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
    public SparseArray<TLObject> participantsMap = new SparseArray<>();
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

            public static void $default$didSelectUser(ChatUsersActivityDelegate chatUsersActivityDelegate, int i) {
            }

            public static void $default$didUserKicked(ChatUsersActivityDelegate chatUsersActivityDelegate, int i) {
            }
        }

        void didAddParticipantToList(int i, TLObject tLObject);

        void didChangeOwner(TLRPC$User tLRPC$User);

        void didSelectUser(int i);

        void didUserKicked(int i);
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
        TLRPC$Chat chat = getMessagesController().getChat(Integer.valueOf(this.chatId));
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
    /* JADX WARNING: Removed duplicated region for block: B:48:0x0157  */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x0167  */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x017b  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateRows() {
        /*
            r6 = this;
            org.telegram.messenger.MessagesController r0 = r6.getMessagesController()
            int r1 = r6.chatId
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
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
            if (r3 != r4) goto L_0x01a4
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
            if (r0 == 0) goto L_0x015f
            int r0 = r6.rowCount
            int r2 = r0 + 1
            r6.rowCount = r2
            r6.addNewRow = r0
        L_0x015f:
            boolean r0 = r6.loadingUsers
            if (r0 == 0) goto L_0x017b
            boolean r0 = r6.firstLoaded
            if (r0 != 0) goto L_0x017b
            if (r0 != 0) goto L_0x033f
            org.telegram.tgnet.TLRPC$ChatFull r0 = r6.info
            if (r0 == 0) goto L_0x033f
            int r0 = r0.banned_count
            if (r0 <= 0) goto L_0x033f
            int r0 = r6.rowCount
            int r1 = r0 + 1
            r6.rowCount = r1
            r6.loadingUserCellRow = r0
            goto L_0x033f
        L_0x017b:
            java.util.ArrayList<org.telegram.tgnet.TLObject> r0 = r6.participants
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x0192
            int r0 = r6.rowCount
            r6.participantsStartRow = r0
            java.util.ArrayList<org.telegram.tgnet.TLObject> r2 = r6.participants
            int r2 = r2.size()
            int r0 = r0 + r2
            r6.rowCount = r0
            r6.participantsEndRow = r0
        L_0x0192:
            int r0 = r6.addNewRow
            if (r0 != r1) goto L_0x019a
            int r0 = r6.participantsStartRow
            if (r0 == r1) goto L_0x033f
        L_0x019a:
            int r0 = r6.rowCount
            int r1 = r0 + 1
            r6.rowCount = r1
            r6.addNewSectionRow = r0
            goto L_0x033f
        L_0x01a4:
            if (r3 != 0) goto L_0x0232
            boolean r0 = org.telegram.messenger.ChatObject.canBlockUsers(r0)
            if (r0 == 0) goto L_0x01d4
            int r0 = r6.rowCount
            int r2 = r0 + 1
            r6.rowCount = r2
            r6.addNewRow = r0
            java.util.ArrayList<org.telegram.tgnet.TLObject> r0 = r6.participants
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x01cc
            boolean r0 = r6.loadingUsers
            if (r0 == 0) goto L_0x01d4
            boolean r0 = r6.firstLoaded
            if (r0 != 0) goto L_0x01d4
            org.telegram.tgnet.TLRPC$ChatFull r0 = r6.info
            if (r0 == 0) goto L_0x01d4
            int r0 = r0.kicked_count
            if (r0 <= 0) goto L_0x01d4
        L_0x01cc:
            int r0 = r6.rowCount
            int r2 = r0 + 1
            r6.rowCount = r2
            r6.participantsInfoRow = r0
        L_0x01d4:
            boolean r0 = r6.loadingUsers
            if (r0 == 0) goto L_0x01ef
            boolean r0 = r6.firstLoaded
            if (r0 == 0) goto L_0x01dd
            goto L_0x01ef
        L_0x01dd:
            if (r0 != 0) goto L_0x033f
            int r0 = r6.rowCount
            int r1 = r0 + 1
            r6.rowCount = r1
            r6.restricted1SectionRow = r0
            int r0 = r1 + 1
            r6.rowCount = r0
            r6.loadingUserCellRow = r1
            goto L_0x033f
        L_0x01ef:
            java.util.ArrayList<org.telegram.tgnet.TLObject> r0 = r6.participants
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x020c
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
        L_0x020c:
            int r0 = r6.participantsStartRow
            if (r0 == r1) goto L_0x0228
            int r0 = r6.participantsInfoRow
            if (r0 != r1) goto L_0x021e
            int r0 = r6.rowCount
            int r1 = r0 + 1
            r6.rowCount = r1
            r6.participantsInfoRow = r0
            goto L_0x033f
        L_0x021e:
            int r0 = r6.rowCount
            int r1 = r0 + 1
            r6.rowCount = r1
            r6.addNewSectionRow = r0
            goto L_0x033f
        L_0x0228:
            int r0 = r6.rowCount
            int r1 = r0 + 1
            r6.rowCount = r1
            r6.blockedEmptyRow = r0
            goto L_0x033f
        L_0x0232:
            if (r3 != r5) goto L_0x02a2
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r0 == 0) goto L_0x025c
            org.telegram.tgnet.TLRPC$Chat r0 = r6.currentChat
            boolean r1 = r0.megagroup
            if (r1 == 0) goto L_0x025c
            boolean r0 = r0.gigagroup
            if (r0 != 0) goto L_0x025c
            org.telegram.tgnet.TLRPC$ChatFull r0 = r6.info
            if (r0 == 0) goto L_0x024e
            int r0 = r0.participants_count
            r1 = 200(0xc8, float:2.8E-43)
            if (r0 > r1) goto L_0x025c
        L_0x024e:
            int r0 = r6.rowCount
            int r1 = r0 + 1
            r6.rowCount = r1
            r6.recentActionsRow = r0
            int r0 = r1 + 1
            r6.rowCount = r0
            r6.addNewSectionRow = r1
        L_0x025c:
            org.telegram.tgnet.TLRPC$Chat r0 = r6.currentChat
            boolean r0 = org.telegram.messenger.ChatObject.canAddAdmins(r0)
            if (r0 == 0) goto L_0x026c
            int r0 = r6.rowCount
            int r1 = r0 + 1
            r6.rowCount = r1
            r6.addNewRow = r0
        L_0x026c:
            boolean r0 = r6.loadingUsers
            if (r0 == 0) goto L_0x0281
            boolean r0 = r6.firstLoaded
            if (r0 == 0) goto L_0x0275
            goto L_0x0281
        L_0x0275:
            if (r0 != 0) goto L_0x033f
            int r0 = r6.rowCount
            int r1 = r0 + 1
            r6.rowCount = r1
            r6.loadingUserCellRow = r0
            goto L_0x033f
        L_0x0281:
            java.util.ArrayList<org.telegram.tgnet.TLObject> r0 = r6.participants
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x0298
            int r0 = r6.rowCount
            r6.participantsStartRow = r0
            java.util.ArrayList<org.telegram.tgnet.TLObject> r1 = r6.participants
            int r1 = r1.size()
            int r0 = r0 + r1
            r6.rowCount = r0
            r6.participantsEndRow = r0
        L_0x0298:
            int r0 = r6.rowCount
            int r1 = r0 + 1
            r6.rowCount = r1
            r6.participantsInfoRow = r0
            goto L_0x033f
        L_0x02a2:
            r1 = 2
            if (r3 != r1) goto L_0x033f
            int r1 = r6.selectType
            if (r1 != 0) goto L_0x02b7
            boolean r0 = org.telegram.messenger.ChatObject.canAddUsers(r0)
            if (r0 == 0) goto L_0x02b7
            int r0 = r6.rowCount
            int r1 = r0 + 1
            r6.rowCount = r1
            r6.addNewRow = r0
        L_0x02b7:
            boolean r0 = r6.loadingUsers
            if (r0 == 0) goto L_0x02d7
            boolean r0 = r6.firstLoaded
            if (r0 == 0) goto L_0x02c0
            goto L_0x02d7
        L_0x02c0:
            if (r0 != 0) goto L_0x033f
            int r0 = r6.selectType
            if (r0 != 0) goto L_0x02ce
            int r0 = r6.rowCount
            int r1 = r0 + 1
            r6.rowCount = r1
            r6.loadingHeaderRow = r0
        L_0x02ce:
            int r0 = r6.rowCount
            int r1 = r0 + 1
            r6.rowCount = r1
            r6.loadingUserCellRow = r0
            goto L_0x033f
        L_0x02d7:
            java.util.ArrayList<org.telegram.tgnet.TLObject> r0 = r6.contacts
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x02f5
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
        L_0x02f5:
            java.util.ArrayList<org.telegram.tgnet.TLObject> r0 = r6.bots
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x0313
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
            goto L_0x0314
        L_0x0313:
            r5 = r2
        L_0x0314:
            java.util.ArrayList<org.telegram.tgnet.TLObject> r0 = r6.participants
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x0335
            if (r5 == 0) goto L_0x0326
            int r0 = r6.rowCount
            int r1 = r0 + 1
            r6.rowCount = r1
            r6.membersHeaderRow = r0
        L_0x0326:
            int r0 = r6.rowCount
            r6.participantsStartRow = r0
            java.util.ArrayList<org.telegram.tgnet.TLObject> r1 = r6.participants
            int r1 = r1.size()
            int r0 = r0 + r1
            r6.rowCount = r0
            r6.participantsEndRow = r0
        L_0x0335:
            int r0 = r6.rowCount
            if (r0 == 0) goto L_0x033f
            int r1 = r0 + 1
            r6.rowCount = r1
            r6.participantsInfoRow = r0
        L_0x033f:
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
            ActionBarMenuItem addItem = createMenu.addItem(0, NUM);
            addItem.setIsSearchField(true);
            addItem.setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
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
            this.searchItem = addItem;
            if (this.type == 3) {
                addItem.setSearchFieldHint(LocaleController.getString("ChannelSearchException", NUM));
            } else {
                addItem.setSearchFieldHint(LocaleController.getString("Search", NUM));
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
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                ChatUsersActivity.this.lambda$createView$1$ChatUsersActivity(view, i);
            }
        });
        this.listView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new RecyclerListView.OnItemLongClickListener() {
            public final boolean onItemClick(View view, int i) {
                return ChatUsersActivity.this.lambda$createView$2$ChatUsersActivity(view, i);
            }
        });
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

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:201:0x03b1  */
    /* JADX WARNING: Removed duplicated region for block: B:256:0x04d9 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:257:0x04da  */
    /* JADX WARNING: Removed duplicated region for block: B:276:? A[RETURN, SYNTHETIC] */
    /* renamed from: lambda$createView$1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$createView$1$ChatUsersActivity(android.view.View r22, int r23) {
        /*
            r21 = this;
            r9 = r21
            r0 = r23
            org.telegram.ui.Components.RecyclerListView r1 = r9.listView
            androidx.recyclerview.widget.RecyclerView$Adapter r1 = r1.getAdapter()
            org.telegram.ui.ChatUsersActivity$ListAdapter r2 = r9.listViewAdapter
            r3 = 0
            r4 = 1
            if (r1 != r2) goto L_0x0012
            r1 = 1
            goto L_0x0013
        L_0x0012:
            r1 = 0
        L_0x0013:
            r2 = 3
            r5 = 2
            if (r1 == 0) goto L_0x02a8
            int r6 = r9.addNewRow
            java.lang.String r7 = "type"
            java.lang.String r8 = "chat_id"
            if (r0 != r6) goto L_0x00bf
            int r0 = r9.type
            java.lang.String r1 = "selectType"
            if (r0 == 0) goto L_0x0094
            if (r0 != r2) goto L_0x0028
            goto L_0x0094
        L_0x0028:
            if (r0 != r4) goto L_0x0050
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            int r2 = r9.chatId
            r0.putInt(r8, r2)
            r0.putInt(r7, r5)
            r0.putInt(r1, r4)
            org.telegram.ui.ChatUsersActivity r1 = new org.telegram.ui.ChatUsersActivity
            r1.<init>(r0)
            org.telegram.ui.ChatUsersActivity$8 r0 = new org.telegram.ui.ChatUsersActivity$8
            r0.<init>()
            r1.setDelegate(r0)
            org.telegram.tgnet.TLRPC$ChatFull r0 = r9.info
            r1.setInfo(r0)
            r9.presentFragment(r1)
            goto L_0x00be
        L_0x0050:
            if (r0 != r5) goto L_0x00be
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.lang.String r1 = "addToGroup"
            r0.putBoolean(r1, r4)
            boolean r1 = r9.isChannel
            if (r1 == 0) goto L_0x0063
            java.lang.String r1 = "channelId"
            goto L_0x0065
        L_0x0063:
            java.lang.String r1 = "chatId"
        L_0x0065:
            org.telegram.tgnet.TLRPC$Chat r2 = r9.currentChat
            int r2 = r2.id
            r0.putInt(r1, r2)
            org.telegram.ui.GroupCreateActivity r1 = new org.telegram.ui.GroupCreateActivity
            r1.<init>(r0)
            org.telegram.tgnet.TLRPC$ChatFull r0 = r9.info
            r1.setInfo(r0)
            android.util.SparseArray<org.telegram.tgnet.TLObject> r0 = r9.contactsMap
            if (r0 == 0) goto L_0x0083
            int r0 = r0.size()
            if (r0 == 0) goto L_0x0083
            android.util.SparseArray<org.telegram.tgnet.TLObject> r0 = r9.contactsMap
            goto L_0x0085
        L_0x0083:
            android.util.SparseArray<org.telegram.tgnet.TLObject> r0 = r9.participantsMap
        L_0x0085:
            r1.setIgnoreUsers(r0)
            org.telegram.ui.ChatUsersActivity$9 r0 = new org.telegram.ui.ChatUsersActivity$9
            r0.<init>()
            r1.setDelegate((org.telegram.ui.GroupCreateActivity.ContactsAddActivityDelegate) r0)
            r9.presentFragment(r1)
            goto L_0x00be
        L_0x0094:
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            int r3 = r9.chatId
            r0.putInt(r8, r3)
            r0.putInt(r7, r5)
            int r3 = r9.type
            if (r3 != 0) goto L_0x00a6
            r2 = 2
        L_0x00a6:
            r0.putInt(r1, r2)
            org.telegram.ui.ChatUsersActivity r1 = new org.telegram.ui.ChatUsersActivity
            r1.<init>(r0)
            org.telegram.tgnet.TLRPC$ChatFull r0 = r9.info
            r1.setInfo(r0)
            org.telegram.ui.ChatUsersActivity$7 r0 = new org.telegram.ui.ChatUsersActivity$7
            r0.<init>()
            r1.setDelegate(r0)
            r9.presentFragment(r1)
        L_0x00be:
            return
        L_0x00bf:
            int r6 = r9.recentActionsRow
            if (r0 != r6) goto L_0x00ce
            org.telegram.ui.ChannelAdminLogActivity r0 = new org.telegram.ui.ChannelAdminLogActivity
            org.telegram.tgnet.TLRPC$Chat r1 = r9.currentChat
            r0.<init>(r1)
            r9.presentFragment(r0)
            return
        L_0x00ce:
            int r6 = r9.removedUsersRow
            if (r0 != r6) goto L_0x00ed
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            int r1 = r9.chatId
            r0.putInt(r8, r1)
            r0.putInt(r7, r3)
            org.telegram.ui.ChatUsersActivity r1 = new org.telegram.ui.ChatUsersActivity
            r1.<init>(r0)
            org.telegram.tgnet.TLRPC$ChatFull r0 = r9.info
            r1.setInfo(r0)
            r9.presentFragment(r1)
            return
        L_0x00ed:
            int r6 = r9.gigaConvertRow
            if (r0 != r6) goto L_0x00ff
            org.telegram.ui.ChatUsersActivity$10 r6 = new org.telegram.ui.ChatUsersActivity$10
            android.app.Activity r7 = r21.getParentActivity()
            r6.<init>(r7, r9)
            r9.showDialog(r6)
            goto L_0x02a8
        L_0x00ff:
            int r6 = r9.addNew2Row
            if (r0 != r6) goto L_0x010e
            org.telegram.ui.GroupInviteActivity r0 = new org.telegram.ui.GroupInviteActivity
            int r1 = r9.chatId
            r0.<init>(r1)
            r9.presentFragment(r0)
            return
        L_0x010e:
            int r6 = r9.permissionsSectionRow
            if (r0 <= r6) goto L_0x02a8
            int r6 = r9.changeInfoRow
            if (r0 > r6) goto L_0x02a8
            r1 = r22
            org.telegram.ui.Cells.TextCheckCell2 r1 = (org.telegram.ui.Cells.TextCheckCell2) r1
            boolean r2 = r1.isEnabled()
            if (r2 != 0) goto L_0x0121
            return
        L_0x0121:
            boolean r2 = r1.hasIcon()
            if (r2 == 0) goto L_0x0163
            org.telegram.tgnet.TLRPC$Chat r1 = r9.currentChat
            java.lang.String r1 = r1.username
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x014e
            int r1 = r9.pinMessagesRow
            if (r0 == r1) goto L_0x0139
            int r1 = r9.changeInfoRow
            if (r0 != r1) goto L_0x014e
        L_0x0139:
            org.telegram.ui.Components.BulletinFactory r0 = org.telegram.ui.Components.BulletinFactory.of((org.telegram.ui.ActionBar.BaseFragment) r21)
            r1 = 2131625234(0x7f0e0512, float:1.887767E38)
            java.lang.String r2 = "EditCantEditPermissionsPublic"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            org.telegram.ui.Components.Bulletin r0 = r0.createErrorBulletin(r1)
            r0.show()
            goto L_0x0162
        L_0x014e:
            org.telegram.ui.Components.BulletinFactory r0 = org.telegram.ui.Components.BulletinFactory.of((org.telegram.ui.ActionBar.BaseFragment) r21)
            r1 = 2131625233(0x7f0e0511, float:1.8877668E38)
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
            int r2 = r9.changeInfoRow
            if (r0 != r2) goto L_0x0178
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r0 = r9.defaultBannedRights
            boolean r1 = r0.change_info
            r1 = r1 ^ r4
            r0.change_info = r1
            goto L_0x02a7
        L_0x0178:
            int r2 = r9.addUsersRow
            if (r0 != r2) goto L_0x0185
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r0 = r9.defaultBannedRights
            boolean r1 = r0.invite_users
            r1 = r1 ^ r4
            r0.invite_users = r1
            goto L_0x02a7
        L_0x0185:
            int r2 = r9.pinMessagesRow
            if (r0 != r2) goto L_0x0192
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r0 = r9.defaultBannedRights
            boolean r1 = r0.pin_messages
            r1 = r1 ^ r4
            r0.pin_messages = r1
            goto L_0x02a7
        L_0x0192:
            boolean r1 = r1.isChecked()
            r1 = r1 ^ r4
            int r2 = r9.sendMessagesRow
            if (r0 != r2) goto L_0x01a3
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r0 = r9.defaultBannedRights
            boolean r5 = r0.send_messages
            r5 = r5 ^ r4
            r0.send_messages = r5
            goto L_0x01d8
        L_0x01a3:
            int r5 = r9.sendMediaRow
            if (r0 != r5) goto L_0x01af
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r0 = r9.defaultBannedRights
            boolean r5 = r0.send_media
            r5 = r5 ^ r4
            r0.send_media = r5
            goto L_0x01d8
        L_0x01af:
            int r5 = r9.sendStickersRow
            if (r0 != r5) goto L_0x01c1
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r0 = r9.defaultBannedRights
            boolean r5 = r0.send_stickers
            r5 = r5 ^ r4
            r0.send_inline = r5
            r0.send_gifs = r5
            r0.send_games = r5
            r0.send_stickers = r5
            goto L_0x01d8
        L_0x01c1:
            int r5 = r9.embedLinksRow
            if (r0 != r5) goto L_0x01cd
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r0 = r9.defaultBannedRights
            boolean r5 = r0.embed_links
            r5 = r5 ^ r4
            r0.embed_links = r5
            goto L_0x01d8
        L_0x01cd:
            int r5 = r9.sendPollsRow
            if (r0 != r5) goto L_0x01d8
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r0 = r9.defaultBannedRights
            boolean r5 = r0.send_polls
            r5 = r5 ^ r4
            r0.send_polls = r5
        L_0x01d8:
            if (r1 == 0) goto L_0x0280
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r0 = r9.defaultBannedRights
            boolean r1 = r0.view_messages
            if (r1 == 0) goto L_0x01f5
            boolean r1 = r0.send_messages
            if (r1 != 0) goto L_0x01f5
            r0.send_messages = r4
            org.telegram.ui.Components.RecyclerListView r0 = r9.listView
            androidx.recyclerview.widget.RecyclerView$ViewHolder r0 = r0.findViewHolderForAdapterPosition(r2)
            if (r0 == 0) goto L_0x01f5
            android.view.View r0 = r0.itemView
            org.telegram.ui.Cells.TextCheckCell2 r0 = (org.telegram.ui.Cells.TextCheckCell2) r0
            r0.setChecked(r3)
        L_0x01f5:
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r0 = r9.defaultBannedRights
            boolean r1 = r0.view_messages
            if (r1 != 0) goto L_0x01ff
            boolean r1 = r0.send_messages
            if (r1 == 0) goto L_0x0216
        L_0x01ff:
            boolean r1 = r0.send_media
            if (r1 != 0) goto L_0x0216
            r0.send_media = r4
            org.telegram.ui.Components.RecyclerListView r0 = r9.listView
            int r1 = r9.sendMediaRow
            androidx.recyclerview.widget.RecyclerView$ViewHolder r0 = r0.findViewHolderForAdapterPosition(r1)
            if (r0 == 0) goto L_0x0216
            android.view.View r0 = r0.itemView
            org.telegram.ui.Cells.TextCheckCell2 r0 = (org.telegram.ui.Cells.TextCheckCell2) r0
            r0.setChecked(r3)
        L_0x0216:
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r0 = r9.defaultBannedRights
            boolean r1 = r0.view_messages
            if (r1 != 0) goto L_0x0220
            boolean r1 = r0.send_messages
            if (r1 == 0) goto L_0x0237
        L_0x0220:
            boolean r1 = r0.send_polls
            if (r1 != 0) goto L_0x0237
            r0.send_polls = r4
            org.telegram.ui.Components.RecyclerListView r0 = r9.listView
            int r1 = r9.sendPollsRow
            androidx.recyclerview.widget.RecyclerView$ViewHolder r0 = r0.findViewHolderForAdapterPosition(r1)
            if (r0 == 0) goto L_0x0237
            android.view.View r0 = r0.itemView
            org.telegram.ui.Cells.TextCheckCell2 r0 = (org.telegram.ui.Cells.TextCheckCell2) r0
            r0.setChecked(r3)
        L_0x0237:
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r0 = r9.defaultBannedRights
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
            org.telegram.ui.Components.RecyclerListView r0 = r9.listView
            int r1 = r9.sendStickersRow
            androidx.recyclerview.widget.RecyclerView$ViewHolder r0 = r0.findViewHolderForAdapterPosition(r1)
            if (r0 == 0) goto L_0x025e
            android.view.View r0 = r0.itemView
            org.telegram.ui.Cells.TextCheckCell2 r0 = (org.telegram.ui.Cells.TextCheckCell2) r0
            r0.setChecked(r3)
        L_0x025e:
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r0 = r9.defaultBannedRights
            boolean r1 = r0.view_messages
            if (r1 != 0) goto L_0x0268
            boolean r1 = r0.send_messages
            if (r1 == 0) goto L_0x02a7
        L_0x0268:
            boolean r1 = r0.embed_links
            if (r1 != 0) goto L_0x02a7
            r0.embed_links = r4
            org.telegram.ui.Components.RecyclerListView r0 = r9.listView
            int r1 = r9.embedLinksRow
            androidx.recyclerview.widget.RecyclerView$ViewHolder r0 = r0.findViewHolderForAdapterPosition(r1)
            if (r0 == 0) goto L_0x02a7
            android.view.View r0 = r0.itemView
            org.telegram.ui.Cells.TextCheckCell2 r0 = (org.telegram.ui.Cells.TextCheckCell2) r0
            r0.setChecked(r3)
            goto L_0x02a7
        L_0x0280:
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r0 = r9.defaultBannedRights
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
            org.telegram.ui.Components.RecyclerListView r0 = r9.listView
            androidx.recyclerview.widget.RecyclerView$ViewHolder r0 = r0.findViewHolderForAdapterPosition(r2)
            if (r0 == 0) goto L_0x02a7
            android.view.View r0 = r0.itemView
            org.telegram.ui.Cells.TextCheckCell2 r0 = (org.telegram.ui.Cells.TextCheckCell2) r0
            r0.setChecked(r4)
        L_0x02a7:
            return
        L_0x02a8:
            r8 = 0
            java.lang.String r6 = ""
            if (r1 == 0) goto L_0x033d
            org.telegram.ui.ChatUsersActivity$ListAdapter r1 = r9.listViewAdapter
            org.telegram.tgnet.TLObject r0 = r1.getItem(r0)
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$ChannelParticipant
            if (r1 == 0) goto L_0x02fd
            r1 = r0
            org.telegram.tgnet.TLRPC$ChannelParticipant r1 = (org.telegram.tgnet.TLRPC$ChannelParticipant) r1
            int r6 = r1.user_id
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r7 = r1.banned_rights
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r10 = r1.admin_rights
            java.lang.String r11 = r1.rank
            boolean r12 = r1 instanceof org.telegram.tgnet.TLRPC$TL_channelParticipantAdmin
            if (r12 != 0) goto L_0x02ca
            boolean r12 = r1 instanceof org.telegram.tgnet.TLRPC$TL_channelParticipantCreator
            if (r12 == 0) goto L_0x02ce
        L_0x02ca:
            boolean r1 = r1.can_edit
            if (r1 == 0) goto L_0x02d0
        L_0x02ce:
            r1 = 1
            goto L_0x02d1
        L_0x02d0:
            r1 = 0
        L_0x02d1:
            boolean r12 = r0 instanceof org.telegram.tgnet.TLRPC$TL_channelParticipantCreator
            if (r12 == 0) goto L_0x02f7
            r10 = r0
            org.telegram.tgnet.TLRPC$TL_channelParticipantCreator r10 = (org.telegram.tgnet.TLRPC$TL_channelParticipantCreator) r10
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r10 = r10.admin_rights
            if (r10 != 0) goto L_0x02f7
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r10 = new org.telegram.tgnet.TLRPC$TL_chatAdminRights
            r10.<init>()
            r10.add_admins = r4
            r10.pin_messages = r4
            r10.invite_users = r4
            r10.ban_users = r4
            r10.delete_messages = r4
            r10.edit_messages = r4
            r10.post_messages = r4
            r10.change_info = r4
            boolean r12 = r9.isChannel
            if (r12 != 0) goto L_0x02f7
            r10.manage_call = r4
        L_0x02f7:
            r13 = r10
            r16 = r11
            r10 = r1
            r11 = r6
            goto L_0x033a
        L_0x02fd:
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$ChatParticipant
            if (r1 == 0) goto L_0x0334
            r1 = r0
            org.telegram.tgnet.TLRPC$ChatParticipant r1 = (org.telegram.tgnet.TLRPC$ChatParticipant) r1
            int r1 = r1.user_id
            org.telegram.tgnet.TLRPC$Chat r7 = r9.currentChat
            boolean r7 = r7.creator
            boolean r10 = r0 instanceof org.telegram.tgnet.TLRPC$TL_chatParticipantCreator
            if (r10 == 0) goto L_0x032a
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r10 = new org.telegram.tgnet.TLRPC$TL_chatAdminRights
            r10.<init>()
            r10.add_admins = r4
            r10.pin_messages = r4
            r10.invite_users = r4
            r10.ban_users = r4
            r10.delete_messages = r4
            r10.edit_messages = r4
            r10.post_messages = r4
            r10.change_info = r4
            boolean r11 = r9.isChannel
            if (r11 != 0) goto L_0x032b
            r10.manage_call = r4
            goto L_0x032b
        L_0x032a:
            r10 = r8
        L_0x032b:
            r11 = r1
            r16 = r6
            r13 = r10
            r6 = r0
            r10 = r7
            r7 = r8
            goto L_0x03af
        L_0x0334:
            r16 = r6
            r7 = r8
            r13 = r7
            r10 = 0
            r11 = 0
        L_0x033a:
            r6 = r0
            goto L_0x03af
        L_0x033d:
            org.telegram.ui.ChatUsersActivity$SearchAdapter r1 = r9.searchListViewAdapter
            org.telegram.tgnet.TLObject r0 = r1.getItem(r0)
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$User
            if (r1 == 0) goto L_0x035c
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            org.telegram.messenger.MessagesController r1 = r21.getMessagesController()
            r1.putUser(r0, r3)
            int r0 = r0.id
            org.telegram.tgnet.TLObject r1 = r9.getAnyParticipant(r0)
            r20 = r1
            r1 = r0
            r0 = r20
            goto L_0x0367
        L_0x035c:
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$ChannelParticipant
            if (r1 != 0) goto L_0x0366
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$ChatParticipant
            if (r1 == 0) goto L_0x0365
            goto L_0x0366
        L_0x0365:
            r0 = r8
        L_0x0366:
            r1 = 0
        L_0x0367:
            boolean r7 = r0 instanceof org.telegram.tgnet.TLRPC$ChannelParticipant
            if (r7 == 0) goto L_0x0390
            r1 = r0
            org.telegram.tgnet.TLRPC$ChannelParticipant r1 = (org.telegram.tgnet.TLRPC$ChannelParticipant) r1
            int r6 = r1.user_id
            boolean r7 = r1 instanceof org.telegram.tgnet.TLRPC$TL_channelParticipantAdmin
            if (r7 != 0) goto L_0x0378
            boolean r7 = r1 instanceof org.telegram.tgnet.TLRPC$TL_channelParticipantCreator
            if (r7 == 0) goto L_0x037c
        L_0x0378:
            boolean r7 = r1.can_edit
            if (r7 == 0) goto L_0x037e
        L_0x037c:
            r7 = 1
            goto L_0x037f
        L_0x037e:
            r7 = 0
        L_0x037f:
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r10 = r1.banned_rights
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r11 = r1.admin_rights
            java.lang.String r1 = r1.rank
            r16 = r1
            r13 = r11
            r11 = r6
            r6 = r0
            r20 = r10
            r10 = r7
            r7 = r20
            goto L_0x03af
        L_0x0390:
            boolean r7 = r0 instanceof org.telegram.tgnet.TLRPC$ChatParticipant
            if (r7 == 0) goto L_0x03a4
            r1 = r0
            org.telegram.tgnet.TLRPC$ChatParticipant r1 = (org.telegram.tgnet.TLRPC$ChatParticipant) r1
            int r1 = r1.user_id
            org.telegram.tgnet.TLRPC$Chat r7 = r9.currentChat
            boolean r7 = r7.creator
            r11 = r1
            r16 = r6
            r10 = r7
            r7 = r8
            r13 = r7
            goto L_0x033a
        L_0x03a4:
            r11 = r1
            r16 = r6
            r7 = r8
            r13 = r7
            if (r0 != 0) goto L_0x03ad
            r10 = 1
            goto L_0x033a
        L_0x03ad:
            r10 = 0
            goto L_0x033a
        L_0x03af:
            if (r11 == 0) goto L_0x04ed
            int r0 = r9.selectType
            if (r0 == 0) goto L_0x044c
            if (r0 == r2) goto L_0x03bf
            if (r0 != r4) goto L_0x03ba
            goto L_0x03bf
        L_0x03ba:
            r9.removeUser(r11)
            goto L_0x04ed
        L_0x03bf:
            if (r0 == r4) goto L_0x042f
            if (r10 == 0) goto L_0x042f
            boolean r1 = r6 instanceof org.telegram.tgnet.TLRPC$TL_channelParticipantAdmin
            if (r1 != 0) goto L_0x03cb
            boolean r1 = r6 instanceof org.telegram.tgnet.TLRPC$TL_chatParticipantAdmin
            if (r1 == 0) goto L_0x042f
        L_0x03cb:
            org.telegram.messenger.MessagesController r0 = r21.getMessagesController()
            java.lang.Integer r1 = java.lang.Integer.valueOf(r11)
            org.telegram.tgnet.TLRPC$User r2 = r0.getUser(r1)
            org.telegram.ui.ActionBar.AlertDialog$Builder r11 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r0 = r21.getParentActivity()
            r11.<init>((android.content.Context) r0)
            r0 = 2131624280(0x7f0e0158, float:1.8875735E38)
            java.lang.String r1 = "AppName"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r11.setTitle(r0)
            r0 = 2131624232(0x7f0e0128, float:1.8875638E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            java.lang.String r4 = org.telegram.messenger.UserObject.getUserName(r2)
            r1[r3] = r4
            java.lang.String r3 = "AdminWillBeRemoved"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r3, r0, r1)
            r11.setMessage(r0)
            r0 = 2131626473(0x7f0e09e9, float:1.8880183E38)
            java.lang.String r1 = "OK"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r1, r0)
            org.telegram.ui.-$$Lambda$ChatUsersActivity$GDkHXwoamCn6kq8XE81r3lcS8vE r14 = new org.telegram.ui.-$$Lambda$ChatUsersActivity$GDkHXwoamCn6kq8XE81r3lcS8vE
            r0 = r14
            r1 = r21
            r3 = r6
            r4 = r13
            r5 = r7
            r6 = r16
            r7 = r10
            r0.<init>(r2, r3, r4, r5, r6, r7)
            r11.setPositiveButton(r12, r14)
            r0 = 2131624637(0x7f0e02bd, float:1.887646E38)
            java.lang.String r1 = "Cancel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r11.setNegativeButton(r0, r8)
            org.telegram.ui.ActionBar.AlertDialog r0 = r11.create()
            r9.showDialog(r0)
            goto L_0x04ed
        L_0x042f:
            if (r0 != r4) goto L_0x0433
            r8 = 0
            goto L_0x0434
        L_0x0433:
            r8 = 1
        L_0x0434:
            if (r0 == r4) goto L_0x043b
            if (r0 != r2) goto L_0x0439
            goto L_0x043b
        L_0x0439:
            r12 = 0
            goto L_0x043c
        L_0x043b:
            r12 = 1
        L_0x043c:
            r0 = r21
            r1 = r11
            r2 = r6
            r3 = r13
            r4 = r7
            r5 = r16
            r6 = r10
            r7 = r8
            r8 = r12
            r0.openRightsEdit(r1, r2, r3, r4, r5, r6, r7, r8)
            goto L_0x04ed
        L_0x044c:
            int r0 = r9.type
            if (r0 != r4) goto L_0x0466
            org.telegram.messenger.UserConfig r0 = r21.getUserConfig()
            int r0 = r0.getClientUserId()
            if (r11 == r0) goto L_0x0464
            org.telegram.tgnet.TLRPC$Chat r0 = r9.currentChat
            boolean r0 = r0.creator
            if (r0 != 0) goto L_0x0462
            if (r10 == 0) goto L_0x0464
        L_0x0462:
            r0 = 1
            goto L_0x0474
        L_0x0464:
            r0 = 0
            goto L_0x0474
        L_0x0466:
            if (r0 == 0) goto L_0x046e
            if (r0 != r2) goto L_0x046b
            goto L_0x046e
        L_0x046b:
            r18 = 0
            goto L_0x0476
        L_0x046e:
            org.telegram.tgnet.TLRPC$Chat r0 = r9.currentChat
            boolean r0 = org.telegram.messenger.ChatObject.canBlockUsers(r0)
        L_0x0474:
            r18 = r0
        L_0x0476:
            int r0 = r9.type
            if (r0 == 0) goto L_0x04cf
            if (r0 == r4) goto L_0x0480
            boolean r1 = r9.isChannel
            if (r1 != 0) goto L_0x04cf
        L_0x0480:
            if (r0 != r5) goto L_0x0487
            int r0 = r9.selectType
            if (r0 != 0) goto L_0x0487
            goto L_0x04cf
        L_0x0487:
            if (r7 != 0) goto L_0x04a8
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
            r15 = r0
            goto L_0x04a9
        L_0x04a8:
            r15 = r7
        L_0x04a9:
            org.telegram.ui.ChatRightsEditActivity r0 = new org.telegram.ui.ChatRightsEditActivity
            int r12 = r9.chatId
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r14 = r9.defaultBannedRights
            int r1 = r9.type
            if (r1 != r4) goto L_0x04b6
            r17 = 0
            goto L_0x04b8
        L_0x04b6:
            r17 = 1
        L_0x04b8:
            if (r6 != 0) goto L_0x04bd
            r19 = 1
            goto L_0x04bf
        L_0x04bd:
            r19 = 0
        L_0x04bf:
            r10 = r0
            r10.<init>(r11, r12, r13, r14, r15, r16, r17, r18, r19)
            org.telegram.ui.ChatUsersActivity$11 r1 = new org.telegram.ui.ChatUsersActivity$11
            r1.<init>(r6)
            r0.setDelegate(r1)
            r9.presentFragment(r0)
            goto L_0x04ed
        L_0x04cf:
            org.telegram.messenger.UserConfig r0 = r21.getUserConfig()
            int r0 = r0.getClientUserId()
            if (r11 != r0) goto L_0x04da
            return
        L_0x04da:
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.lang.String r1 = "user_id"
            r0.putInt(r1, r11)
            org.telegram.ui.ProfileActivity r1 = new org.telegram.ui.ProfileActivity
            r1.<init>(r0)
            r9.presentFragment(r1)
        L_0x04ed:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatUsersActivity.lambda$createView$1$ChatUsersActivity(android.view.View, int):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$0 */
    public /* synthetic */ void lambda$null$0$ChatUsersActivity(TLRPC$User tLRPC$User, TLObject tLObject, TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights, TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights, String str, boolean z, DialogInterface dialogInterface, int i) {
        openRightsEdit(tLRPC$User.id, tLObject, tLRPC$TL_chatAdminRights, tLRPC$TL_chatBannedRights, str, z, this.selectType == 1 ? 0 : 1, false);
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0007, code lost:
        r3 = r2.listView.getAdapter();
        r1 = r2.listViewAdapter;
     */
    /* renamed from: lambda$createView$2 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ boolean lambda$createView$2$ChatUsersActivity(android.view.View r3, int r4) {
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
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatUsersActivity.lambda$createView$2$ChatUsersActivity(android.view.View, int):boolean");
    }

    /* access modifiers changed from: private */
    public void sortAdmins(ArrayList<TLObject> arrayList) {
        Collections.sort(arrayList, new Object() {
            public final int compare(Object obj, Object obj2) {
                return ChatUsersActivity.this.lambda$sortAdmins$3$ChatUsersActivity((TLObject) obj, (TLObject) obj2);
            }

            public /* synthetic */ Comparator reversed() {
                return Comparator.CC.$default$reversed(this);
            }

            public /* synthetic */ java.util.Comparator thenComparing(Function function) {
                return Comparator.CC.$default$thenComparing((java.util.Comparator) this, function);
            }

            public /* synthetic */ java.util.Comparator thenComparing(Function function, java.util.Comparator comparator) {
                return Comparator.CC.$default$thenComparing(this, function, comparator);
            }

            public /* synthetic */ java.util.Comparator thenComparing(java.util.Comparator comparator) {
                return Comparator.CC.$default$thenComparing((java.util.Comparator) this, comparator);
            }

            public /* synthetic */ java.util.Comparator thenComparingDouble(ToDoubleFunction toDoubleFunction) {
                return Comparator.CC.$default$thenComparingDouble(this, toDoubleFunction);
            }

            public /* synthetic */ java.util.Comparator thenComparingInt(ToIntFunction toIntFunction) {
                return Comparator.CC.$default$thenComparingInt(this, toIntFunction);
            }

            public /* synthetic */ java.util.Comparator thenComparingLong(ToLongFunction toLongFunction) {
                return Comparator.CC.$default$thenComparingLong(this, toLongFunction);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$sortAdmins$3 */
    public /* synthetic */ int lambda$sortAdmins$3$ChatUsersActivity(TLObject tLObject, TLObject tLObject2) {
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
        return ((TLRPC$ChannelParticipant) tLObject).user_id - ((TLRPC$ChannelParticipant) tLObject2).user_id;
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
        SparseArray<TLObject> sparseArray;
        boolean z;
        this.undoView.showWithAction((long) (-this.chatId), this.isChannel ? 9 : 10, (Object) tLRPC$User);
        this.currentChat.creator = false;
        boolean z2 = false;
        for (int i = 0; i < 3; i++) {
            boolean z3 = true;
            if (i == 0) {
                sparseArray = this.contactsMap;
                arrayList = this.contacts;
            } else if (i == 1) {
                sparseArray = this.botsMap;
                arrayList = this.bots;
            } else {
                sparseArray = this.participantsMap;
                arrayList = this.participants;
            }
            TLObject tLObject = sparseArray.get(tLRPC$User.id);
            if (tLObject instanceof TLRPC$ChannelParticipant) {
                TLRPC$TL_channelParticipantCreator tLRPC$TL_channelParticipantCreator = new TLRPC$TL_channelParticipantCreator();
                int i2 = tLRPC$User.id;
                tLRPC$TL_channelParticipantCreator.user_id = i2;
                sparseArray.put(i2, tLRPC$TL_channelParticipantCreator);
                int indexOf = arrayList.indexOf(tLObject);
                if (indexOf >= 0) {
                    arrayList.set(indexOf, tLRPC$TL_channelParticipantCreator);
                }
                z2 = true;
                z = true;
            } else {
                z = false;
            }
            int clientUserId = getUserConfig().getClientUserId();
            TLObject tLObject2 = sparseArray.get(clientUserId);
            if (tLObject2 instanceof TLRPC$ChannelParticipant) {
                TLRPC$TL_channelParticipantAdmin tLRPC$TL_channelParticipantAdmin = new TLRPC$TL_channelParticipantAdmin();
                tLRPC$TL_channelParticipantAdmin.user_id = clientUserId;
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
                sparseArray.put(clientUserId, tLRPC$TL_channelParticipantAdmin);
                int indexOf2 = arrayList.indexOf(tLObject2);
                if (indexOf2 >= 0) {
                    arrayList.set(indexOf2, tLRPC$TL_channelParticipantAdmin);
                }
            } else {
                z3 = z;
            }
            if (z3) {
                Collections.sort(arrayList, new Object() {
                    public final int compare(Object obj, Object obj2) {
                        return ChatUsersActivity.this.lambda$onOwnerChaged$4$ChatUsersActivity((TLObject) obj, (TLObject) obj2);
                    }

                    public /* synthetic */ java.util.Comparator reversed() {
                        return Comparator.CC.$default$reversed(this);
                    }

                    public /* synthetic */ java.util.Comparator thenComparing(Function function) {
                        return Comparator.CC.$default$thenComparing((java.util.Comparator) this, function);
                    }

                    public /* synthetic */ java.util.Comparator thenComparing(Function function, java.util.Comparator comparator) {
                        return Comparator.CC.$default$thenComparing(this, function, comparator);
                    }

                    public /* synthetic */ java.util.Comparator thenComparing(java.util.Comparator comparator) {
                        return Comparator.CC.$default$thenComparing((java.util.Comparator) this, comparator);
                    }

                    public /* synthetic */ java.util.Comparator thenComparingDouble(ToDoubleFunction toDoubleFunction) {
                        return Comparator.CC.$default$thenComparingDouble(this, toDoubleFunction);
                    }

                    public /* synthetic */ java.util.Comparator thenComparingInt(ToIntFunction toIntFunction) {
                        return Comparator.CC.$default$thenComparingInt(this, toIntFunction);
                    }

                    public /* synthetic */ java.util.Comparator thenComparingLong(ToLongFunction toLongFunction) {
                        return Comparator.CC.$default$thenComparingLong(this, toLongFunction);
                    }
                });
            }
        }
        if (!z2) {
            TLRPC$TL_channelParticipantCreator tLRPC$TL_channelParticipantCreator2 = new TLRPC$TL_channelParticipantCreator();
            int i3 = tLRPC$User.id;
            tLRPC$TL_channelParticipantCreator2.user_id = i3;
            this.participantsMap.put(i3, tLRPC$TL_channelParticipantCreator2);
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
    /* renamed from: lambda$onOwnerChaged$4 */
    public /* synthetic */ int lambda$onOwnerChaged$4$ChatUsersActivity(TLObject tLObject, TLObject tLObject2) {
        int channelAdminParticipantType = getChannelAdminParticipantType(tLObject);
        int channelAdminParticipantType2 = getChannelAdminParticipantType(tLObject2);
        if (channelAdminParticipantType > channelAdminParticipantType2) {
            return 1;
        }
        return channelAdminParticipantType < channelAdminParticipantType2 ? -1 : 0;
    }

    private void openRightsEdit2(int i, int i2, TLObject tLObject, TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights, TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights, String str, boolean z, int i3, boolean z2) {
        TLObject tLObject2 = tLObject;
        boolean[] zArr = new boolean[1];
        boolean z3 = (tLObject2 instanceof TLRPC$TL_channelParticipantAdmin) || (tLObject2 instanceof TLRPC$TL_chatParticipantAdmin);
        final boolean[] zArr2 = zArr;
        AnonymousClass14 r13 = r0;
        final int i4 = i;
        AnonymousClass14 r0 = new ChatRightsEditActivity(this, i, this.chatId, tLRPC$TL_chatAdminRights, this.defaultBannedRights, tLRPC$TL_chatBannedRights, str, i3, true, false) {
            final /* synthetic */ ChatUsersActivity this$0;

            {
                this.this$0 = r12;
            }

            /* access modifiers changed from: protected */
            public void onTransitionAnimationEnd(boolean z, boolean z2) {
                TLRPC$User user;
                if (!z && z2 && zArr2[0] && BulletinFactory.canShowBulletin(this.this$0) && (user = getMessagesController().getUser(Integer.valueOf(i4))) != null) {
                    BulletinFactory.createPromoteToAdminBulletin(this.this$0, user.first_name).show();
                }
            }
        };
        final int i5 = i3;
        final int i6 = i;
        final int i7 = i2;
        final boolean z4 = z3;
        final boolean[] zArr3 = zArr;
        r13.setDelegate(new ChatRightsEditActivity.ChatRightsEditActivityDelegate() {
            public void didSetRights(int i, TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights, TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights, String str) {
                TLRPC$ChatParticipant tLRPC$ChatParticipant;
                TLRPC$ChannelParticipant tLRPC$ChannelParticipant;
                int i2 = i5;
                if (i2 == 0) {
                    int i3 = 0;
                    while (true) {
                        if (i3 >= ChatUsersActivity.this.participants.size()) {
                            break;
                        }
                        TLObject tLObject = (TLObject) ChatUsersActivity.this.participants.get(i3);
                        if (tLObject instanceof TLRPC$ChannelParticipant) {
                            if (((TLRPC$ChannelParticipant) tLObject).user_id == i6) {
                                if (i == 1) {
                                    tLRPC$ChannelParticipant = new TLRPC$TL_channelParticipantAdmin();
                                } else {
                                    tLRPC$ChannelParticipant = new TLRPC$TL_channelParticipant();
                                }
                                tLRPC$ChannelParticipant.admin_rights = tLRPC$TL_chatAdminRights;
                                tLRPC$ChannelParticipant.banned_rights = tLRPC$TL_chatBannedRights;
                                tLRPC$ChannelParticipant.inviter_id = ChatUsersActivity.this.getUserConfig().getClientUserId();
                                tLRPC$ChannelParticipant.user_id = i6;
                                tLRPC$ChannelParticipant.date = i7;
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
                    ChatUsersActivity.this.removeParticipants(i6);
                }
            }

            public void didChangeOwner(TLRPC$User tLRPC$User) {
                ChatUsersActivity.this.onOwnerChaged(tLRPC$User);
            }
        });
        presentFragment(r13);
    }

    public boolean canBeginSlide() {
        return checkDiscard();
    }

    /* access modifiers changed from: private */
    public void openRightsEdit(final int i, TLObject tLObject, TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights, TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights, String str, boolean z, int i2, boolean z2) {
        final TLObject tLObject2 = tLObject;
        final boolean z3 = z2;
        ChatRightsEditActivity chatRightsEditActivity = new ChatRightsEditActivity(i, this.chatId, tLRPC$TL_chatAdminRights, this.defaultBannedRights, tLRPC$TL_chatBannedRights, str, i2, z, tLObject2 == null);
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
                    ChatUsersActivity.this.delegate.didSelectUser(i);
                } else if (ChatUsersActivity.this.delegate != null) {
                    ChatUsersActivity.this.delegate.didAddParticipantToList(i, tLObject2);
                }
                if (z3) {
                    ChatUsersActivity.this.removeSelfFromStack();
                }
            }

            public void didChangeOwner(TLRPC$User tLRPC$User) {
                ChatUsersActivity.this.onOwnerChaged(tLRPC$User);
            }
        });
        presentFragment(chatRightsEditActivity, z3);
    }

    private void removeUser(int i) {
        if (ChatObject.isChannel(this.currentChat)) {
            getMessagesController().deleteUserFromChat(this.chatId, getMessagesController().getUser(Integer.valueOf(i)), (TLRPC$ChatFull) null);
            ChatUsersActivityDelegate chatUsersActivityDelegate = this.delegate;
            if (chatUsersActivityDelegate != null) {
                chatUsersActivityDelegate.didUserKicked(i);
            }
            finishFragment();
        }
    }

    private TLObject getAnyParticipant(int i) {
        SparseArray<TLObject> sparseArray;
        for (int i2 = 0; i2 < 3; i2++) {
            if (i2 == 0) {
                sparseArray = this.contactsMap;
            } else if (i2 == 1) {
                sparseArray = this.botsMap;
            } else {
                sparseArray = this.participantsMap;
            }
            TLObject tLObject = sparseArray.get(i);
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
            removeParticipants(((TLRPC$ChannelParticipant) tLObject).user_id);
        }
    }

    /* access modifiers changed from: private */
    public void removeParticipants(int i) {
        ArrayList<TLObject> arrayList;
        SparseArray<TLObject> sparseArray;
        DiffCallback saveState = saveState();
        boolean z = false;
        for (int i2 = 0; i2 < 3; i2++) {
            if (i2 == 0) {
                sparseArray = this.contactsMap;
                arrayList = this.contacts;
            } else if (i2 == 1) {
                sparseArray = this.botsMap;
                arrayList = this.bots;
            } else {
                sparseArray = this.participantsMap;
                arrayList = this.participants;
            }
            TLObject tLObject = sparseArray.get(i);
            if (tLObject != null) {
                sparseArray.remove(i);
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
            searchAdapter.removeUserId(i);
        }
    }

    /* access modifiers changed from: private */
    public void updateParticipantWithRights(TLRPC$ChannelParticipant tLRPC$ChannelParticipant, TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights, TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights, int i, boolean z) {
        SparseArray<TLObject> sparseArray;
        ChatUsersActivityDelegate chatUsersActivityDelegate;
        boolean z2 = false;
        for (int i2 = 0; i2 < 3; i2++) {
            if (i2 == 0) {
                sparseArray = this.contactsMap;
            } else if (i2 == 1) {
                sparseArray = this.botsMap;
            } else {
                sparseArray = this.participantsMap;
            }
            TLObject tLObject = sparseArray.get(tLRPC$ChannelParticipant.user_id);
            if (tLObject instanceof TLRPC$ChannelParticipant) {
                tLRPC$ChannelParticipant = (TLRPC$ChannelParticipant) tLObject;
                tLRPC$ChannelParticipant.admin_rights = tLRPC$TL_chatAdminRights;
                tLRPC$ChannelParticipant.banned_rights = tLRPC$TL_chatBannedRights;
                if (z) {
                    tLRPC$ChannelParticipant.promoted_by = getUserConfig().getClientUserId();
                }
            }
            if (z && tLObject != null && !z2 && (chatUsersActivityDelegate = this.delegate) != null) {
                chatUsersActivityDelegate.didAddParticipantToList(i, tLObject);
                z2 = true;
            }
        }
    }

    /* JADX WARNING: type inference failed for: r5v6 */
    /* JADX WARNING: type inference failed for: r5v7 */
    /* JADX WARNING: type inference failed for: r5v9 */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:120:0x029a A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:121:0x029b  */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean createMenuForParticipant(org.telegram.tgnet.TLObject r23, boolean r24) {
        /*
            r22 = this;
            r11 = r22
            r6 = r23
            if (r6 == 0) goto L_0x02d2
            int r1 = r11.selectType
            if (r1 == 0) goto L_0x000c
            goto L_0x02d2
        L_0x000c:
            boolean r1 = r6 instanceof org.telegram.tgnet.TLRPC$ChannelParticipant
            if (r1 == 0) goto L_0x0029
            r1 = r6
            org.telegram.tgnet.TLRPC$ChannelParticipant r1 = (org.telegram.tgnet.TLRPC$ChannelParticipant) r1
            int r3 = r1.user_id
            boolean r4 = r1.can_edit
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r5 = r1.banned_rights
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r7 = r1.admin_rights
            int r8 = r1.date
            java.lang.String r1 = r1.rank
            r10 = r1
            r1 = r4
            r9 = r5
            r4 = r3
            r21 = r8
            r8 = r7
            r7 = r21
            goto L_0x0049
        L_0x0029:
            boolean r1 = r6 instanceof org.telegram.tgnet.TLRPC$ChatParticipant
            if (r1 == 0) goto L_0x0043
            r1 = r6
            org.telegram.tgnet.TLRPC$ChatParticipant r1 = (org.telegram.tgnet.TLRPC$ChatParticipant) r1
            int r3 = r1.user_id
            int r1 = r1.date
            org.telegram.tgnet.TLRPC$Chat r4 = r11.currentChat
            boolean r4 = org.telegram.messenger.ChatObject.canAddAdmins(r4)
            java.lang.String r5 = ""
            r7 = r1
            r1 = r4
            r10 = r5
            r8 = 0
            r9 = 0
            r4 = r3
            goto L_0x0049
        L_0x0043:
            r1 = 0
            r4 = 0
            r7 = 0
            r8 = 0
            r9 = 0
            r10 = 0
        L_0x0049:
            if (r4 == 0) goto L_0x02d0
            org.telegram.messenger.UserConfig r3 = r22.getUserConfig()
            int r3 = r3.getClientUserId()
            if (r4 != r3) goto L_0x0057
            goto L_0x02d0
        L_0x0057:
            int r3 = r11.type
            java.lang.String r13 = "EditAdminRights"
            java.lang.String r14 = "dialogRedIcon"
            java.lang.String r15 = "dialogTextRed2"
            r2 = 2
            r5 = 1
            if (r3 != r2) goto L_0x01c7
            org.telegram.messenger.MessagesController r3 = r22.getMessagesController()
            java.lang.Integer r2 = java.lang.Integer.valueOf(r4)
            org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r2)
            org.telegram.tgnet.TLRPC$Chat r2 = r11.currentChat
            boolean r2 = org.telegram.messenger.ChatObject.canAddAdmins(r2)
            if (r2 == 0) goto L_0x0087
            boolean r2 = r6 instanceof org.telegram.tgnet.TLRPC$TL_channelParticipant
            if (r2 != 0) goto L_0x0085
            boolean r2 = r6 instanceof org.telegram.tgnet.TLRPC$TL_channelParticipantBanned
            if (r2 != 0) goto L_0x0085
            boolean r2 = r6 instanceof org.telegram.tgnet.TLRPC$TL_chatParticipant
            if (r2 != 0) goto L_0x0085
            if (r1 == 0) goto L_0x0087
        L_0x0085:
            r2 = 1
            goto L_0x0088
        L_0x0087:
            r2 = 0
        L_0x0088:
            boolean r0 = r6 instanceof org.telegram.tgnet.TLRPC$TL_channelParticipantAdmin
            if (r0 != 0) goto L_0x0098
            boolean r12 = r6 instanceof org.telegram.tgnet.TLRPC$TL_channelParticipantCreator
            if (r12 != 0) goto L_0x0098
            boolean r12 = r6 instanceof org.telegram.tgnet.TLRPC$TL_chatParticipantCreator
            if (r12 != 0) goto L_0x0098
            boolean r12 = r6 instanceof org.telegram.tgnet.TLRPC$TL_chatParticipantAdmin
            if (r12 == 0) goto L_0x009a
        L_0x0098:
            if (r1 == 0) goto L_0x009c
        L_0x009a:
            r12 = 1
            goto L_0x009d
        L_0x009c:
            r12 = 0
        L_0x009d:
            if (r0 != 0) goto L_0x00a6
            boolean r0 = r6 instanceof org.telegram.tgnet.TLRPC$TL_chatParticipantAdmin
            if (r0 == 0) goto L_0x00a4
            goto L_0x00a6
        L_0x00a4:
            r0 = 0
            goto L_0x00a7
        L_0x00a6:
            r0 = 1
        L_0x00a7:
            int r1 = r11.selectType
            if (r1 != 0) goto L_0x00b1
            boolean r1 = org.telegram.messenger.UserObject.isDeleted(r3)
            r1 = r1 ^ r5
            r2 = r2 & r1
        L_0x00b1:
            if (r24 != 0) goto L_0x00c7
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            java.util.ArrayList r16 = new java.util.ArrayList
            r16.<init>()
            java.util.ArrayList r18 = new java.util.ArrayList
            r18.<init>()
            r20 = r16
            r19 = r18
            goto L_0x00cc
        L_0x00c7:
            r1 = 0
            r19 = 0
            r20 = 0
        L_0x00cc:
            if (r2 == 0) goto L_0x00fe
            if (r24 == 0) goto L_0x00d1
            return r5
        L_0x00d1:
            if (r0 == 0) goto L_0x00db
            r0 = 2131625220(0x7f0e0504, float:1.8877642E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r13, r0)
            goto L_0x00e4
        L_0x00db:
            r0 = 2131627346(0x7f0e0d52, float:1.8881954E38)
            java.lang.String r2 = "SetAsAdmin"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
        L_0x00e4:
            r1.add(r0)
            r0 = 2131165247(0x7var_f, float:1.7944706E38)
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            r2 = r19
            r2.add(r0)
            r0 = 0
            java.lang.Integer r13 = java.lang.Integer.valueOf(r0)
            r0 = r20
            r0.add(r13)
            goto L_0x0102
        L_0x00fe:
            r2 = r19
            r0 = r20
        L_0x0102:
            org.telegram.tgnet.TLRPC$Chat r13 = r11.currentChat
            boolean r13 = org.telegram.messenger.ChatObject.canBlockUsers(r13)
            if (r13 == 0) goto L_0x0166
            if (r12 == 0) goto L_0x0166
            if (r24 == 0) goto L_0x010f
            return r5
        L_0x010f:
            boolean r13 = r11.isChannel
            if (r13 != 0) goto L_0x0146
            org.telegram.tgnet.TLRPC$Chat r13 = r11.currentChat
            boolean r13 = org.telegram.messenger.ChatObject.isChannel(r13)
            if (r13 == 0) goto L_0x0139
            r13 = 2131624659(0x7f0e02d3, float:1.8876504E38)
            java.lang.String r5 = "ChangePermissions"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r13)
            r1.add(r5)
            r5 = 2131165253(0x7var_, float:1.7944718E38)
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r2.add(r5)
            r5 = 1
            java.lang.Integer r13 = java.lang.Integer.valueOf(r5)
            r0.add(r13)
        L_0x0139:
            r13 = 2131625873(0x7f0e0791, float:1.8878966E38)
            java.lang.String r5 = "KickFromGroup"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r13)
            r1.add(r5)
            goto L_0x0152
        L_0x0146:
            r5 = 2131624750(0x7f0e032e, float:1.8876689E38)
            java.lang.String r13 = "ChannelRemoveUser"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r13, r5)
            r1.add(r5)
        L_0x0152:
            r5 = 2131165254(0x7var_, float:1.794472E38)
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r2.add(r5)
            r5 = 2
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r0.add(r5)
            r13 = 1
            goto L_0x0167
        L_0x0166:
            r13 = 0
        L_0x0167:
            if (r0 == 0) goto L_0x01c5
            boolean r5 = r0.isEmpty()
            if (r5 == 0) goto L_0x0170
            goto L_0x01c5
        L_0x0170:
            org.telegram.ui.ActionBar.AlertDialog$Builder r5 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r6 = r22.getParentActivity()
            r5.<init>((android.content.Context) r6)
            int r6 = r0.size()
            java.lang.CharSequence[] r6 = new java.lang.CharSequence[r6]
            java.lang.Object[] r6 = r1.toArray(r6)
            java.lang.CharSequence[] r6 = (java.lang.CharSequence[]) r6
            int[] r2 = org.telegram.messenger.AndroidUtilities.toIntArray(r2)
            r18 = r14
            org.telegram.ui.-$$Lambda$ChatUsersActivity$FYCkFs-9aIiSUBmSNl2UMWZwZ7k r14 = new org.telegram.ui.-$$Lambda$ChatUsersActivity$FYCkFs-9aIiSUBmSNl2UMWZwZ7k
            r16 = r0
            r0 = r14
            r17 = r1
            r1 = r22
            r19 = r15
            r15 = r2
            r2 = r16
            r24 = r13
            r13 = r5
            r5 = r12
            r12 = r6
            r6 = r23
            r0.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10)
            r13.setItems(r12, r15, r14)
            org.telegram.ui.ActionBar.AlertDialog r0 = r13.create()
            r11.showDialog(r0)
            if (r24 == 0) goto L_0x01c2
            int r1 = r17.size()
            r12 = 1
            int r1 = r1 - r12
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r19)
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r18)
            r0.setItemColor(r1, r2, r3)
            goto L_0x02cf
        L_0x01c2:
            r12 = 1
            goto L_0x02cf
        L_0x01c5:
            r0 = 0
            return r0
        L_0x01c7:
            r18 = r14
            r19 = r15
            r12 = 1
            r0 = 3
            r2 = 2131624697(0x7f0e02f9, float:1.8876581E38)
            java.lang.String r5 = "ChannelDeleteFromList"
            if (r3 != r0) goto L_0x0200
            org.telegram.tgnet.TLRPC$Chat r0 = r11.currentChat
            boolean r0 = org.telegram.messenger.ChatObject.canBlockUsers(r0)
            if (r0 == 0) goto L_0x0200
            if (r24 == 0) goto L_0x01df
            return r12
        L_0x01df:
            r0 = 2
            java.lang.CharSequence[] r1 = new java.lang.CharSequence[r0]
            r3 = 2131624703(0x7f0e02ff, float:1.8876593E38)
            java.lang.String r6 = "ChannelEditPermissions"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r6, r3)
            r6 = 0
            r1[r6] = r3
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r5, r2)
            r1[r12] = r2
            int[] r2 = new int[r0]
            r2 = {NUM, NUM} // fill-array
        L_0x01f9:
            r6 = r23
            r13 = r1
            r14 = r2
            r5 = 0
            goto L_0x0298
        L_0x0200:
            int r0 = r11.type
            if (r0 != 0) goto L_0x023f
            org.telegram.tgnet.TLRPC$Chat r0 = r11.currentChat
            boolean r0 = org.telegram.messenger.ChatObject.canBlockUsers(r0)
            if (r0 == 0) goto L_0x023f
            if (r24 == 0) goto L_0x020f
            return r12
        L_0x020f:
            r0 = 2
            java.lang.CharSequence[] r1 = new java.lang.CharSequence[r0]
            org.telegram.tgnet.TLRPC$Chat r0 = r11.currentChat
            boolean r0 = org.telegram.messenger.ChatObject.canAddUsers(r0)
            if (r0 == 0) goto L_0x022e
            boolean r0 = r11.isChannel
            if (r0 == 0) goto L_0x0224
            r0 = 2131624672(0x7f0e02e0, float:1.887653E38)
            java.lang.String r3 = "ChannelAddToChannel"
            goto L_0x0229
        L_0x0224:
            r0 = 2131624673(0x7f0e02e1, float:1.8876532E38)
            java.lang.String r3 = "ChannelAddToGroup"
        L_0x0229:
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
            goto L_0x022f
        L_0x022e:
            r0 = 0
        L_0x022f:
            r3 = 0
            r1[r3] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r5, r2)
            r1[r12] = r0
            r0 = 2
            int[] r2 = new int[r0]
            r2 = {NUM, NUM} // fill-array
            goto L_0x01f9
        L_0x023f:
            int r0 = r11.type
            if (r0 != r12) goto L_0x0293
            org.telegram.tgnet.TLRPC$Chat r0 = r11.currentChat
            boolean r0 = org.telegram.messenger.ChatObject.canAddAdmins(r0)
            if (r0 == 0) goto L_0x0293
            if (r1 == 0) goto L_0x0293
            if (r24 == 0) goto L_0x0250
            return r12
        L_0x0250:
            org.telegram.tgnet.TLRPC$Chat r0 = r11.currentChat
            boolean r0 = r0.creator
            r2 = 2131624751(0x7f0e032f, float:1.887669E38)
            java.lang.String r3 = "ChannelRemoveUserAdmin"
            r6 = r23
            if (r0 != 0) goto L_0x0278
            boolean r0 = r6 instanceof org.telegram.tgnet.TLRPC$TL_channelParticipantCreator
            if (r0 != 0) goto L_0x0266
            if (r1 == 0) goto L_0x0266
            r0 = 2
            r5 = 0
            goto L_0x027a
        L_0x0266:
            java.lang.CharSequence[] r0 = new java.lang.CharSequence[r12]
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r5 = 0
            r0[r5] = r1
            int[] r2 = new int[r12]
            r1 = 2131165254(0x7var_, float:1.794472E38)
            r2[r5] = r1
            r13 = r0
            goto L_0x0291
        L_0x0278:
            r5 = 0
            r0 = 2
        L_0x027a:
            java.lang.CharSequence[] r1 = new java.lang.CharSequence[r0]
            r7 = 2131625220(0x7f0e0504, float:1.8877642E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r13, r7)
            r1[r5] = r7
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1[r12] = r2
            int[] r2 = new int[r0]
            r2 = {NUM, NUM} // fill-array
            r13 = r1
        L_0x0291:
            r14 = r2
            goto L_0x0298
        L_0x0293:
            r6 = r23
            r5 = 0
            r13 = 0
            r14 = 0
        L_0x0298:
            if (r13 != 0) goto L_0x029b
            return r5
        L_0x029b:
            org.telegram.ui.ActionBar.AlertDialog$Builder r15 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r0 = r22.getParentActivity()
            r15.<init>((android.content.Context) r0)
            org.telegram.ui.-$$Lambda$ChatUsersActivity$CMPkGN1P58LT7rgqq6PCZ4xET3E r7 = new org.telegram.ui.-$$Lambda$ChatUsersActivity$CMPkGN1P58LT7rgqq6PCZ4xET3E
            r0 = r7
            r1 = r22
            r2 = r13
            r3 = r4
            r4 = r8
            r5 = r10
            r6 = r23
            r8 = r7
            r7 = r9
            r0.<init>(r2, r3, r4, r5, r6, r7)
            r15.setItems(r13, r14, r8)
            org.telegram.ui.ActionBar.AlertDialog r0 = r15.create()
            r11.showDialog(r0)
            int r1 = r11.type
            if (r1 != r12) goto L_0x02cf
            int r1 = r13.length
            int r1 = r1 - r12
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r19)
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r18)
            r0.setItemColor(r1, r2, r3)
        L_0x02cf:
            return r12
        L_0x02d0:
            r0 = 0
            return r0
        L_0x02d2:
            r0 = 0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatUsersActivity.createMenuForParticipant(org.telegram.tgnet.TLObject, boolean):boolean");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createMenuForParticipant$6 */
    public /* synthetic */ void lambda$createMenuForParticipant$6$ChatUsersActivity(ArrayList arrayList, TLRPC$User tLRPC$User, int i, boolean z, TLObject tLObject, int i2, TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights, TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights, String str, DialogInterface dialogInterface, int i3) {
        ArrayList arrayList2 = arrayList;
        TLRPC$User tLRPC$User2 = tLRPC$User;
        TLObject tLObject2 = tLObject;
        int i4 = i3;
        if (((Integer) arrayList2.get(i4)).intValue() == 2) {
            getMessagesController().deleteUserFromChat(this.chatId, tLRPC$User2, (TLRPC$ChatFull) null);
            removeParticipants(i);
            if (this.currentChat != null && tLRPC$User2 != null && BulletinFactory.canShowBulletin(this)) {
                BulletinFactory.createRemoveFromChatBulletin(this, tLRPC$User2, this.currentChat.title).show();
                return;
            }
            return;
        }
        int i5 = i;
        if (((Integer) arrayList2.get(i4)).intValue() != 1 || !z || (!(tLObject2 instanceof TLRPC$TL_channelParticipantAdmin) && !(tLObject2 instanceof TLRPC$TL_chatParticipantAdmin))) {
            openRightsEdit2(i, i2, tLObject, tLRPC$TL_chatAdminRights, tLRPC$TL_chatBannedRights, str, z, ((Integer) arrayList2.get(i4)).intValue(), false);
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setTitle(LocaleController.getString("AppName", NUM));
        builder.setMessage(LocaleController.formatString("AdminWillBeRemoved", NUM, UserObject.getUserName(tLRPC$User)));
        builder.setPositiveButton(LocaleController.getString("OK", NUM), new DialogInterface.OnClickListener(i, i2, tLObject, tLRPC$TL_chatAdminRights, tLRPC$TL_chatBannedRights, str, z, arrayList, i3) {
            public final /* synthetic */ int f$1;
            public final /* synthetic */ int f$2;
            public final /* synthetic */ TLObject f$3;
            public final /* synthetic */ TLRPC$TL_chatAdminRights f$4;
            public final /* synthetic */ TLRPC$TL_chatBannedRights f$5;
            public final /* synthetic */ String f$6;
            public final /* synthetic */ boolean f$7;
            public final /* synthetic */ ArrayList f$8;
            public final /* synthetic */ int f$9;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
                this.f$6 = r7;
                this.f$7 = r8;
                this.f$8 = r9;
                this.f$9 = r10;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                ChatUsersActivity.this.lambda$null$5$ChatUsersActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        showDialog(builder.create());
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$5 */
    public /* synthetic */ void lambda$null$5$ChatUsersActivity(int i, int i2, TLObject tLObject, TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights, TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights, String str, boolean z, ArrayList arrayList, int i3, DialogInterface dialogInterface, int i4) {
        openRightsEdit2(i, i2, tLObject, tLRPC$TL_chatAdminRights, tLRPC$TL_chatBannedRights, str, z, ((Integer) arrayList.get(i3)).intValue(), false);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createMenuForParticipant$9 */
    public /* synthetic */ void lambda$createMenuForParticipant$9$ChatUsersActivity(CharSequence[] charSequenceArr, int i, TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights, String str, TLObject tLObject, TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights, DialogInterface dialogInterface, int i2) {
        int i3;
        int i4;
        int i5 = i;
        final TLObject tLObject2 = tLObject;
        int i6 = i2;
        int i7 = this.type;
        if (i7 == 1) {
            if (i6 == 0 && charSequenceArr.length == 2) {
                ChatRightsEditActivity chatRightsEditActivity = new ChatRightsEditActivity(i, this.chatId, tLRPC$TL_chatAdminRights, (TLRPC$TL_chatBannedRights) null, (TLRPC$TL_chatBannedRights) null, str, 0, true, false);
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
            getMessagesController().setUserAdminRole(this.chatId, getMessagesController().getUser(Integer.valueOf(i)), new TLRPC$TL_chatAdminRights(), "", true ^ this.isChannel, this, false);
            removeParticipants(i5);
        } else if (i7 == 0 || i7 == 3) {
            if (i6 == 0) {
                if (i7 == 3) {
                    ChatRightsEditActivity chatRightsEditActivity2 = new ChatRightsEditActivity(i, this.chatId, (TLRPC$TL_chatAdminRights) null, this.defaultBannedRights, tLRPC$TL_chatBannedRights, str, 1, true, false);
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
                } else if (i7 == 0) {
                    i4 = 1;
                    i3 = i6;
                    getMessagesController().addUserToChat(this.chatId, getMessagesController().getUser(Integer.valueOf(i)), 0, (String) null, this, (Runnable) null);
                }
                i3 = i6;
                i4 = 1;
            } else {
                i3 = i6;
                i4 = 1;
                if (i3 == 1) {
                    TLRPC$TL_channels_editBanned tLRPC$TL_channels_editBanned = new TLRPC$TL_channels_editBanned();
                    tLRPC$TL_channels_editBanned.user_id = getMessagesController().getInputUser(i5);
                    tLRPC$TL_channels_editBanned.channel = getMessagesController().getInputChannel(this.chatId);
                    tLRPC$TL_channels_editBanned.banned_rights = new TLRPC$TL_chatBannedRights();
                    getConnectionsManager().sendRequest(tLRPC$TL_channels_editBanned, new RequestDelegate() {
                        public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                            ChatUsersActivity.this.lambda$null$8$ChatUsersActivity(tLObject, tLRPC$TL_error);
                        }
                    });
                }
            }
            if ((i3 == 0 && this.type == 0) || i3 == i4) {
                removeParticipants(tLObject2);
            }
        } else if (i6 == 0) {
            getMessagesController().deleteUserFromChat(this.chatId, getMessagesController().getUser(Integer.valueOf(i)), (TLRPC$ChatFull) null);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$8 */
    public /* synthetic */ void lambda$null$8$ChatUsersActivity(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            TLRPC$Updates tLRPC$Updates = (TLRPC$Updates) tLObject;
            getMessagesController().processUpdates(tLRPC$Updates, false);
            if (!tLRPC$Updates.chats.isEmpty()) {
                AndroidUtilities.runOnUIThread(new Runnable(tLRPC$Updates) {
                    public final /* synthetic */ TLRPC$Updates f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        ChatUsersActivity.this.lambda$null$7$ChatUsersActivity(this.f$1);
                    }
                }, 1000);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$7 */
    public /* synthetic */ void lambda$null$7$ChatUsersActivity(TLRPC$Updates tLRPC$Updates) {
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
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public final void run() {
                        ChatUsersActivity.this.lambda$didReceivedNotification$10$ChatUsersActivity();
                    }
                });
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$didReceivedNotification$10 */
    public /* synthetic */ void lambda$didReceivedNotification$10$ChatUsersActivity() {
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
        builder.setPositiveButton(LocaleController.getString("ApplyTheme", NUM), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                ChatUsersActivity.this.lambda$checkDiscard$11$ChatUsersActivity(dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("PassportDiscard", NUM), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                ChatUsersActivity.this.lambda$checkDiscard$12$ChatUsersActivity(dialogInterface, i);
            }
        });
        showDialog(builder.create());
        return false;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$checkDiscard$11 */
    public /* synthetic */ void lambda$checkDiscard$11$ChatUsersActivity(DialogInterface dialogInterface, int i) {
        processDone();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$checkDiscard$12 */
    public /* synthetic */ void lambda$checkDiscard$12$ChatUsersActivity(DialogInterface dialogInterface, int i) {
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
            if (ChatObject.isChannel(this.currentChat) || this.selectedSlowmode == this.initialSlowmode || this.info == null) {
                if (!ChatObject.getBannedRightsString(this.defaultBannedRights).equals(this.initialBannedRights)) {
                    getMessagesController().setDefaultBannedRole(this.chatId, this.defaultBannedRights, ChatObject.isChannel(this.currentChat), this);
                    TLRPC$Chat chat = getMessagesController().getChat(Integer.valueOf(this.chatId));
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
            MessagesController.getInstance(this.currentAccount).convertToMegaGroup(getParentActivity(), this.chatId, this, new MessagesStorage.IntCallback() {
                public final void run(int i) {
                    ChatUsersActivity.this.lambda$processDone$13$ChatUsersActivity(i);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$processDone$13 */
    public /* synthetic */ void lambda$processDone$13$ChatUsersActivity(int i) {
        if (i != 0) {
            this.chatId = i;
            this.currentChat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(i));
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
        SparseArray<TLRPC$TL_groupCallParticipant> sparseArray;
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
                int i5 = getUserConfig().clientUserId;
                int size2 = this.info.participants.participants.size();
                while (i3 < size2) {
                    TLRPC$ChatParticipant tLRPC$ChatParticipant2 = this.info.participants.participants.get(i3);
                    if ((this.selectType == 0 || tLRPC$ChatParticipant2.user_id != i5) && ((sparseArray = this.ignoredUsers) == null || sparseArray.indexOfKey(tLRPC$ChatParticipant2.user_id) < 0)) {
                        if (this.selectType == 1) {
                            if (getContactsController().isContact(tLRPC$ChatParticipant2.user_id)) {
                                this.contacts.add(tLRPC$ChatParticipant2);
                                this.contactsMap.put(tLRPC$ChatParticipant2.user_id, tLRPC$ChatParticipant2);
                            } else if (!UserObject.isDeleted(getMessagesController().getUser(Integer.valueOf(tLRPC$ChatParticipant2.user_id)))) {
                                this.participants.add(tLRPC$ChatParticipant2);
                                this.participantsMap.put(tLRPC$ChatParticipant2.user_id, tLRPC$ChatParticipant2);
                            }
                        } else if (getContactsController().isContact(tLRPC$ChatParticipant2.user_id)) {
                            this.contacts.add(tLRPC$ChatParticipant2);
                            this.contactsMap.put(tLRPC$ChatParticipant2.user_id, tLRPC$ChatParticipant2);
                        } else {
                            TLRPC$User user = getMessagesController().getUser(Integer.valueOf(tLRPC$ChatParticipant2.user_id));
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
        int i6 = this.type;
        if (i6 == 0) {
            tLRPC$TL_channels_getParticipants.filter = new TLRPC$TL_channelParticipantsKicked();
        } else if (i6 == 1) {
            tLRPC$TL_channels_getParticipants.filter = new TLRPC$TL_channelParticipantsAdmins();
        } else if (i6 == 2) {
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
        } else if (i6 == 3) {
            tLRPC$TL_channels_getParticipants.filter = new TLRPC$TL_channelParticipantsBanned();
        }
        tLRPC$TL_channels_getParticipants.filter.q = "";
        tLRPC$TL_channels_getParticipants.offset = i;
        tLRPC$TL_channels_getParticipants.limit = i2;
        getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(tLRPC$TL_channels_getParticipants, new RequestDelegate(tLRPC$TL_channels_getParticipants) {
            public final /* synthetic */ TLRPC$TL_channels_getParticipants f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                ChatUsersActivity.this.lambda$loadChatParticipants$15$ChatUsersActivity(this.f$1, tLObject, tLRPC$TL_error);
            }
        }), this.classGuid);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$loadChatParticipants$15 */
    public /* synthetic */ void lambda$loadChatParticipants$15$ChatUsersActivity(TLRPC$TL_channels_getParticipants tLRPC$TL_channels_getParticipants, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, tLObject, tLRPC$TL_channels_getParticipants) {
            public final /* synthetic */ TLRPC$TL_error f$1;
            public final /* synthetic */ TLObject f$2;
            public final /* synthetic */ TLRPC$TL_channels_getParticipants f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                ChatUsersActivity.this.lambda$null$14$ChatUsersActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x00e4, code lost:
        r4 = r7.ignoredUsers;
     */
    /* renamed from: lambda$null$14 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$null$14$ChatUsersActivity(org.telegram.tgnet.TLRPC$TL_error r8, org.telegram.tgnet.TLObject r9, org.telegram.tgnet.TLRPC$TL_channels_getParticipants r10) {
        /*
            r7 = this;
            r0 = 2
            r1 = 0
            r2 = 1
            if (r8 != 0) goto L_0x0132
            org.telegram.tgnet.TLRPC$TL_channels_channelParticipants r9 = (org.telegram.tgnet.TLRPC$TL_channels_channelParticipants) r9
            int r8 = r7.type
            if (r8 != r2) goto L_0x0014
            org.telegram.messenger.MessagesController r8 = r7.getMessagesController()
            int r3 = r7.chatId
            r8.processLoadedAdminsResponse(r3, r9)
        L_0x0014:
            org.telegram.messenger.MessagesController r8 = r7.getMessagesController()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r3 = r9.users
            r8.putUsers(r3, r1)
            org.telegram.messenger.UserConfig r8 = r7.getUserConfig()
            int r8 = r8.getClientUserId()
            int r3 = r7.selectType
            if (r3 == 0) goto L_0x0047
            r3 = 0
        L_0x002a:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChannelParticipant> r4 = r9.participants
            int r4 = r4.size()
            if (r3 >= r4) goto L_0x0047
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChannelParticipant> r4 = r9.participants
            java.lang.Object r4 = r4.get(r3)
            org.telegram.tgnet.TLRPC$ChannelParticipant r4 = (org.telegram.tgnet.TLRPC$ChannelParticipant) r4
            int r4 = r4.user_id
            if (r4 != r8) goto L_0x0044
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChannelParticipant> r8 = r9.participants
            r8.remove(r3)
            goto L_0x0047
        L_0x0044:
            int r3 = r3 + 1
            goto L_0x002a
        L_0x0047:
            int r8 = r7.type
            if (r8 != r0) goto L_0x0069
            int r8 = r7.delayResults
            int r8 = r8 - r2
            r7.delayResults = r8
            org.telegram.tgnet.TLRPC$ChannelParticipantsFilter r8 = r10.filter
            boolean r10 = r8 instanceof org.telegram.tgnet.TLRPC$TL_channelParticipantsContacts
            if (r10 == 0) goto L_0x005b
            java.util.ArrayList<org.telegram.tgnet.TLObject> r8 = r7.contacts
            android.util.SparseArray<org.telegram.tgnet.TLObject> r10 = r7.contactsMap
            goto L_0x0070
        L_0x005b:
            boolean r8 = r8 instanceof org.telegram.tgnet.TLRPC$TL_channelParticipantsBots
            if (r8 == 0) goto L_0x0064
            java.util.ArrayList<org.telegram.tgnet.TLObject> r8 = r7.bots
            android.util.SparseArray<org.telegram.tgnet.TLObject> r10 = r7.botsMap
            goto L_0x0070
        L_0x0064:
            java.util.ArrayList<org.telegram.tgnet.TLObject> r8 = r7.participants
            android.util.SparseArray<org.telegram.tgnet.TLObject> r10 = r7.participantsMap
            goto L_0x0070
        L_0x0069:
            java.util.ArrayList<org.telegram.tgnet.TLObject> r8 = r7.participants
            android.util.SparseArray<org.telegram.tgnet.TLObject> r10 = r7.participantsMap
            r10.clear()
        L_0x0070:
            r8.clear()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChannelParticipant> r3 = r9.participants
            r8.addAll(r3)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChannelParticipant> r3 = r9.participants
            int r3 = r3.size()
            r4 = 0
        L_0x007f:
            if (r4 >= r3) goto L_0x0091
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChannelParticipant> r5 = r9.participants
            java.lang.Object r5 = r5.get(r4)
            org.telegram.tgnet.TLRPC$ChannelParticipant r5 = (org.telegram.tgnet.TLRPC$ChannelParticipant) r5
            int r6 = r5.user_id
            r10.put(r6, r5)
            int r4 = r4 + 1
            goto L_0x007f
        L_0x0091:
            int r9 = r7.type
            if (r9 != r0) goto L_0x0105
            java.util.ArrayList<org.telegram.tgnet.TLObject> r9 = r7.participants
            int r9 = r9.size()
            r10 = 0
        L_0x009c:
            if (r10 >= r9) goto L_0x0105
            java.util.ArrayList<org.telegram.tgnet.TLObject> r3 = r7.participants
            java.lang.Object r3 = r3.get(r10)
            org.telegram.tgnet.TLObject r3 = (org.telegram.tgnet.TLObject) r3
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$ChannelParticipant
            if (r4 != 0) goto L_0x00b4
            java.util.ArrayList<org.telegram.tgnet.TLObject> r3 = r7.participants
            r3.remove(r10)
        L_0x00af:
            int r10 = r10 + -1
            int r9 = r9 + -1
            goto L_0x0103
        L_0x00b4:
            org.telegram.tgnet.TLRPC$ChannelParticipant r3 = (org.telegram.tgnet.TLRPC$ChannelParticipant) r3
            android.util.SparseArray<org.telegram.tgnet.TLObject> r4 = r7.contactsMap
            int r5 = r3.user_id
            java.lang.Object r4 = r4.get(r5)
            if (r4 != 0) goto L_0x00f3
            android.util.SparseArray<org.telegram.tgnet.TLObject> r4 = r7.botsMap
            int r5 = r3.user_id
            java.lang.Object r4 = r4.get(r5)
            if (r4 == 0) goto L_0x00cb
            goto L_0x00f3
        L_0x00cb:
            int r4 = r7.selectType
            if (r4 != r2) goto L_0x00e4
            org.telegram.messenger.MessagesController r4 = r7.getMessagesController()
            int r5 = r3.user_id
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            org.telegram.tgnet.TLRPC$User r4 = r4.getUser(r5)
            boolean r4 = org.telegram.messenger.UserObject.isDeleted(r4)
            if (r4 == 0) goto L_0x00e4
            goto L_0x00f3
        L_0x00e4:
            android.util.SparseArray<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r4 = r7.ignoredUsers
            if (r4 == 0) goto L_0x00f1
            int r5 = r3.user_id
            int r4 = r4.indexOfKey(r5)
            if (r4 < 0) goto L_0x00f1
            goto L_0x00f3
        L_0x00f1:
            r4 = 0
            goto L_0x00f4
        L_0x00f3:
            r4 = 1
        L_0x00f4:
            if (r4 == 0) goto L_0x0103
            java.util.ArrayList<org.telegram.tgnet.TLObject> r4 = r7.participants
            r4.remove(r10)
            android.util.SparseArray<org.telegram.tgnet.TLObject> r4 = r7.participantsMap
            int r3 = r3.user_id
            r4.remove(r3)
            goto L_0x00af
        L_0x0103:
            int r10 = r10 + r2
            goto L_0x009c
        L_0x0105:
            int r9 = r7.type     // Catch:{ Exception -> 0x012e }
            if (r9 == 0) goto L_0x010e
            r10 = 3
            if (r9 == r10) goto L_0x010e
            if (r9 != r0) goto L_0x0126
        L_0x010e:
            org.telegram.tgnet.TLRPC$Chat r10 = r7.currentChat     // Catch:{ Exception -> 0x012e }
            if (r10 == 0) goto L_0x0126
            boolean r10 = r10.megagroup     // Catch:{ Exception -> 0x012e }
            if (r10 == 0) goto L_0x0126
            org.telegram.tgnet.TLRPC$ChatFull r10 = r7.info     // Catch:{ Exception -> 0x012e }
            boolean r3 = r10 instanceof org.telegram.tgnet.TLRPC$TL_channelFull     // Catch:{ Exception -> 0x012e }
            if (r3 == 0) goto L_0x0126
            int r10 = r10.participants_count     // Catch:{ Exception -> 0x012e }
            r3 = 200(0xc8, float:2.8E-43)
            if (r10 > r3) goto L_0x0126
            r7.sortUsers(r8)     // Catch:{ Exception -> 0x012e }
            goto L_0x0132
        L_0x0126:
            if (r9 != r2) goto L_0x0132
            java.util.ArrayList<org.telegram.tgnet.TLObject> r8 = r7.participants     // Catch:{ Exception -> 0x012e }
            r7.sortAdmins(r8)     // Catch:{ Exception -> 0x012e }
            goto L_0x0132
        L_0x012e:
            r8 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r8)
        L_0x0132:
            int r8 = r7.type
            if (r8 != r0) goto L_0x013a
            int r8 = r7.delayResults
            if (r8 > 0) goto L_0x014b
        L_0x013a:
            org.telegram.ui.ChatUsersActivity$ListAdapter r8 = r7.listViewAdapter
            if (r8 == 0) goto L_0x0143
            int r8 = r8.getItemCount()
            goto L_0x0144
        L_0x0143:
            r8 = 0
        L_0x0144:
            r7.showItemsAnimated(r8)
            r7.loadingUsers = r1
            r7.firstLoaded = r2
        L_0x014b:
            r7.updateRows()
            org.telegram.ui.ChatUsersActivity$ListAdapter r8 = r7.listViewAdapter
            if (r8 == 0) goto L_0x0173
            org.telegram.ui.Components.RecyclerListView r8 = r7.listView
            boolean r9 = r7.openTransitionStarted
            r8.setAnimateEmptyView(r9, r1)
            org.telegram.ui.ChatUsersActivity$ListAdapter r8 = r7.listViewAdapter
            r8.notifyDataSetChanged()
            org.telegram.ui.Components.StickerEmptyView r8 = r7.emptyView
            if (r8 == 0) goto L_0x0173
            org.telegram.ui.ChatUsersActivity$ListAdapter r8 = r7.listViewAdapter
            int r8 = r8.getItemCount()
            if (r8 != 0) goto L_0x0173
            boolean r8 = r7.firstLoaded
            if (r8 == 0) goto L_0x0173
            org.telegram.ui.Components.StickerEmptyView r8 = r7.emptyView
            r8.showProgress(r1, r2)
        L_0x0173:
            r7.resumeDelayedFragmentAnimation()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatUsersActivity.lambda$null$14$ChatUsersActivity(org.telegram.tgnet.TLRPC$TL_error, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_channels_getParticipants):void");
    }

    /* access modifiers changed from: private */
    public void sortUsers(ArrayList<TLObject> arrayList) {
        Collections.sort(arrayList, new Object(getConnectionsManager().getCurrentTime()) {
            public final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final int compare(Object obj, Object obj2) {
                return ChatUsersActivity.this.lambda$sortUsers$16$ChatUsersActivity(this.f$1, (TLObject) obj, (TLObject) obj2);
            }

            public /* synthetic */ java.util.Comparator reversed() {
                return Comparator.CC.$default$reversed(this);
            }

            public /* synthetic */ java.util.Comparator thenComparing(Function function) {
                return Comparator.CC.$default$thenComparing((java.util.Comparator) this, function);
            }

            public /* synthetic */ java.util.Comparator thenComparing(Function function, java.util.Comparator comparator) {
                return Comparator.CC.$default$thenComparing(this, function, comparator);
            }

            public /* synthetic */ java.util.Comparator thenComparing(java.util.Comparator comparator) {
                return Comparator.CC.$default$thenComparing((java.util.Comparator) this, comparator);
            }

            public /* synthetic */ java.util.Comparator thenComparingDouble(ToDoubleFunction toDoubleFunction) {
                return Comparator.CC.$default$thenComparingDouble(this, toDoubleFunction);
            }

            public /* synthetic */ java.util.Comparator thenComparingInt(ToIntFunction toIntFunction) {
                return Comparator.CC.$default$thenComparingInt(this, toIntFunction);
            }

            public /* synthetic */ java.util.Comparator thenComparingLong(ToLongFunction toLongFunction) {
                return Comparator.CC.$default$thenComparingLong(this, toLongFunction);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$sortUsers$16 */
    public /* synthetic */ int lambda$sortUsers$16$ChatUsersActivity(int i, TLObject tLObject, TLObject tLObject2) {
        int i2;
        int i3;
        TLRPC$UserStatus tLRPC$UserStatus;
        TLRPC$UserStatus tLRPC$UserStatus2;
        TLRPC$User user = getMessagesController().getUser(Integer.valueOf(((TLRPC$ChannelParticipant) tLObject).user_id));
        TLRPC$User user2 = getMessagesController().getUser(Integer.valueOf(((TLRPC$ChannelParticipant) tLObject2).user_id));
        if (user == null || (tLRPC$UserStatus2 = user.status) == null) {
            i2 = 0;
        } else {
            i2 = user.self ? i + 50000 : tLRPC$UserStatus2.expires;
        }
        if (user2 == null || (tLRPC$UserStatus = user2.status) == null) {
            i3 = 0;
        } else {
            i3 = user2.self ? i + 50000 : tLRPC$UserStatus.expires;
        }
        if (i2 <= 0 || i3 <= 0) {
            if (i2 >= 0 || i3 >= 0) {
                if ((i2 < 0 && i3 > 0) || (i2 == 0 && i3 != 0)) {
                    return -1;
                }
                if ((i3 >= 0 || i2 <= 0) && (i3 != 0 || i2 == 0)) {
                    return 0;
                }
                return 1;
            } else if (i2 > i3) {
                return 1;
            } else {
                if (i2 < i3) {
                    return -1;
                }
                return 0;
            }
        } else if (i2 > i3) {
            return 1;
        } else {
            if (i2 < i3) {
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
        private ArrayList<TLObject> searchResult = new ArrayList<>();
        private SparseArray<TLObject> searchResultMap = new SparseArray<>();
        private ArrayList<CharSequence> searchResultNames = new ArrayList<>();
        private Runnable searchRunnable;
        private int totalCount = 0;

        public SearchAdapter(Context context) {
            this.mContext = context;
            SearchAdapterHelper searchAdapterHelper2 = new SearchAdapterHelper(true);
            this.searchAdapterHelper = searchAdapterHelper2;
            searchAdapterHelper2.setDelegate(new SearchAdapterHelper.SearchAdapterHelperDelegate() {
                public /* synthetic */ boolean canApplySearchResults(int i) {
                    return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$canApplySearchResults(this, i);
                }

                public /* synthetic */ SparseArray getExcludeCallParticipants() {
                    return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$getExcludeCallParticipants(this);
                }

                public /* synthetic */ SparseArray getExcludeUsers() {
                    return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$getExcludeUsers(this);
                }

                public final void onDataSetChanged(int i) {
                    ChatUsersActivity.SearchAdapter.this.lambda$new$0$ChatUsersActivity$SearchAdapter(i);
                }

                public /* synthetic */ void onSetHashtags(ArrayList arrayList, HashMap hashMap) {
                    SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$onSetHashtags(this, arrayList, hashMap);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$0 */
        public /* synthetic */ void lambda$new$0$ChatUsersActivity$SearchAdapter(int i) {
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
            this.searchAdapterHelper.mergeResults((ArrayList<TLObject>) null);
            this.searchAdapterHelper.queryServerSearch((String) null, ChatUsersActivity.this.type != 0, false, true, false, false, ChatObject.isChannel(ChatUsersActivity.this.currentChat) ? ChatUsersActivity.this.chatId : 0, false, ChatUsersActivity.this.type, 0);
            notifyDataSetChanged();
            if (!TextUtils.isEmpty(str)) {
                this.searchInProgress = true;
                ChatUsersActivity.this.emptyView.showProgress(true, true);
                DispatchQueue dispatchQueue = Utilities.searchQueue;
                $$Lambda$ChatUsersActivity$SearchAdapter$LXhjIzHbgCIIMdRWdvbJxozIxsw r1 = new Runnable(str) {
                    public final /* synthetic */ String f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        ChatUsersActivity.SearchAdapter.this.lambda$searchUsers$1$ChatUsersActivity$SearchAdapter(this.f$1);
                    }
                };
                this.searchRunnable = r1;
                dispatchQueue.postRunnable(r1, 300);
            }
        }

        /* access modifiers changed from: private */
        /* renamed from: processSearch */
        public void lambda$searchUsers$1(String str) {
            AndroidUtilities.runOnUIThread(new Runnable(str) {
                public final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    ChatUsersActivity.SearchAdapter.this.lambda$processSearch$3$ChatUsersActivity$SearchAdapter(this.f$1);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$processSearch$3 */
        public /* synthetic */ void lambda$processSearch$3$ChatUsersActivity$SearchAdapter(String str) {
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
                Utilities.searchQueue.postRunnable(new Runnable(str, arrayList2, arrayList) {
                    public final /* synthetic */ String f$1;
                    public final /* synthetic */ ArrayList f$2;
                    public final /* synthetic */ ArrayList f$3;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                    }

                    public final void run() {
                        ChatUsersActivity.SearchAdapter.this.lambda$null$2$ChatUsersActivity$SearchAdapter(this.f$1, this.f$2, this.f$3);
                    }
                });
            }
            this.searchAdapterHelper.queryServerSearch(str, ChatUsersActivity.this.selectType != 0, false, true, false, false, ChatObject.isChannel(ChatUsersActivity.this.currentChat) ? ChatUsersActivity.this.chatId : 0, false, ChatUsersActivity.this.type, 1);
        }

        /* JADX WARNING: type inference failed for: r3v13 */
        /* JADX WARNING: type inference failed for: r3v19 */
        /* JADX WARNING: type inference failed for: r3v24 */
        /* JADX WARNING: type inference failed for: r3v26 */
        /* access modifiers changed from: private */
        /* JADX WARNING: Code restructure failed: missing block: B:41:0x0104, code lost:
            if (r7.contains(" " + r4) != false) goto L_0x0118;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:81:0x0204, code lost:
            if (r5.contains(" " + r12) != false) goto L_0x0213;
         */
        /* JADX WARNING: Multi-variable type inference failed */
        /* JADX WARNING: Removed duplicated region for block: B:103:0x011b A[SYNTHETIC] */
        /* JADX WARNING: Removed duplicated region for block: B:108:0x0216 A[SYNTHETIC] */
        /* JADX WARNING: Removed duplicated region for block: B:56:0x0156 A[LOOP:1: B:32:0x00c6->B:56:0x0156, LOOP_END] */
        /* JADX WARNING: Removed duplicated region for block: B:94:0x025f A[LOOP:3: B:72:0x01ca->B:94:0x025f, LOOP_END] */
        /* JADX WARNING: Unknown variable types count: 1 */
        /* renamed from: lambda$null$2 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public /* synthetic */ void lambda$null$2$ChatUsersActivity$SearchAdapter(java.lang.String r22, java.util.ArrayList r23, java.util.ArrayList r24) {
            /*
                r21 = this;
                r0 = r21
                r1 = r23
                r2 = r24
                java.lang.String r3 = r22.trim()
                java.lang.String r3 = r3.toLowerCase()
                int r4 = r3.length()
                if (r4 != 0) goto L_0x002c
                java.util.ArrayList r1 = new java.util.ArrayList
                r1.<init>()
                android.util.SparseArray r2 = new android.util.SparseArray
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
                android.util.SparseArray r4 = new android.util.SparseArray
                r4.<init>()
                java.util.ArrayList r10 = new java.util.ArrayList
                r10.<init>()
                java.util.ArrayList r11 = new java.util.ArrayList
                r11.<init>()
                java.lang.String r13 = "@"
                java.lang.String r14 = " "
                if (r1 == 0) goto L_0x017a
                int r15 = r23.size()
            L_0x006f:
                if (r5 >= r15) goto L_0x017a
                java.lang.Object r16 = r1.get(r5)
                r12 = r16
                org.telegram.tgnet.TLObject r12 = (org.telegram.tgnet.TLObject) r12
                boolean r6 = r12 instanceof org.telegram.tgnet.TLRPC$ChatParticipant
                if (r6 == 0) goto L_0x0083
                r6 = r12
                org.telegram.tgnet.TLRPC$ChatParticipant r6 = (org.telegram.tgnet.TLRPC$ChatParticipant) r6
                int r6 = r6.user_id
                goto L_0x008c
            L_0x0083:
                boolean r6 = r12 instanceof org.telegram.tgnet.TLRPC$ChannelParticipant
                if (r6 == 0) goto L_0x0167
                r6 = r12
                org.telegram.tgnet.TLRPC$ChannelParticipant r6 = (org.telegram.tgnet.TLRPC$ChannelParticipant) r6
                int r6 = r6.user_id
            L_0x008c:
                org.telegram.ui.ChatUsersActivity r7 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.messenger.MessagesController r7 = r7.getMessagesController()
                java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
                org.telegram.tgnet.TLRPC$User r6 = r7.getUser(r6)
                int r7 = r6.id
                org.telegram.ui.ChatUsersActivity r1 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.messenger.UserConfig r1 = r1.getUserConfig()
                int r1 = r1.getClientUserId()
                if (r7 != r1) goto L_0x00aa
                goto L_0x0167
            L_0x00aa:
                java.lang.String r1 = org.telegram.messenger.UserObject.getUserName(r6)
                java.lang.String r1 = r1.toLowerCase()
                org.telegram.messenger.LocaleController r7 = org.telegram.messenger.LocaleController.getInstance()
                java.lang.String r7 = r7.getTranslitString(r1)
                boolean r17 = r1.equals(r7)
                if (r17 == 0) goto L_0x00c1
                r7 = 0
            L_0x00c1:
                r18 = r15
                r15 = 0
                r17 = 0
            L_0x00c6:
                if (r15 >= r8) goto L_0x0162
                r19 = r4
                r4 = r9[r15]
                boolean r20 = r1.startsWith(r4)
                if (r20 != 0) goto L_0x0116
                r20 = r3
                java.lang.StringBuilder r3 = new java.lang.StringBuilder
                r3.<init>()
                r3.append(r14)
                r3.append(r4)
                java.lang.String r3 = r3.toString()
                boolean r3 = r1.contains(r3)
                if (r3 != 0) goto L_0x0118
                if (r7 == 0) goto L_0x0107
                boolean r3 = r7.startsWith(r4)
                if (r3 != 0) goto L_0x0118
                java.lang.StringBuilder r3 = new java.lang.StringBuilder
                r3.<init>()
                r3.append(r14)
                r3.append(r4)
                java.lang.String r3 = r3.toString()
                boolean r3 = r7.contains(r3)
                if (r3 == 0) goto L_0x0107
                goto L_0x0118
            L_0x0107:
                java.lang.String r3 = r6.username
                if (r3 == 0) goto L_0x0113
                boolean r3 = r3.startsWith(r4)
                if (r3 == 0) goto L_0x0113
                r3 = 2
                goto L_0x0119
            L_0x0113:
                r3 = r17
                goto L_0x0119
            L_0x0116:
                r20 = r3
            L_0x0118:
                r3 = 1
            L_0x0119:
                if (r3 == 0) goto L_0x0156
                r1 = 1
                if (r3 != r1) goto L_0x012a
                java.lang.String r1 = r6.first_name
                java.lang.String r3 = r6.last_name
                java.lang.CharSequence r1 = org.telegram.messenger.AndroidUtilities.generateSearchName(r1, r3, r4)
                r10.add(r1)
                goto L_0x0152
            L_0x012a:
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r1.<init>()
                r1.append(r13)
                java.lang.String r3 = r6.username
                r1.append(r3)
                java.lang.String r1 = r1.toString()
                java.lang.StringBuilder r3 = new java.lang.StringBuilder
                r3.<init>()
                r3.append(r13)
                r3.append(r4)
                java.lang.String r3 = r3.toString()
                r4 = 0
                java.lang.CharSequence r1 = org.telegram.messenger.AndroidUtilities.generateSearchName(r1, r4, r3)
                r10.add(r1)
            L_0x0152:
                r11.add(r12)
                goto L_0x016d
            L_0x0156:
                r17 = r1
                int r15 = r15 + 1
                r4 = r19
                r17 = r3
                r3 = r20
                goto L_0x00c6
            L_0x0162:
                r20 = r3
                r19 = r4
                goto L_0x016d
            L_0x0167:
                r20 = r3
                r19 = r4
                r18 = r15
            L_0x016d:
                int r5 = r5 + 1
                r1 = r23
                r15 = r18
                r4 = r19
                r3 = r20
                r7 = 1
                goto L_0x006f
            L_0x017a:
                r20 = r3
                r19 = r4
                if (r2 == 0) goto L_0x026f
                r1 = 0
            L_0x0181:
                int r3 = r24.size()
                if (r1 >= r3) goto L_0x026f
                java.lang.Object r3 = r2.get(r1)
                org.telegram.tgnet.TLRPC$TL_contact r3 = (org.telegram.tgnet.TLRPC$TL_contact) r3
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.messenger.MessagesController r4 = r4.getMessagesController()
                int r3 = r3.user_id
                java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
                org.telegram.tgnet.TLRPC$User r3 = r4.getUser(r3)
                int r4 = r3.id
                org.telegram.ui.ChatUsersActivity r5 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.messenger.UserConfig r5 = r5.getUserConfig()
                int r5 = r5.getClientUserId()
                if (r4 != r5) goto L_0x01b1
            L_0x01ab:
                r15 = r19
                r12 = r20
                goto L_0x0267
            L_0x01b1:
                java.lang.String r4 = org.telegram.messenger.UserObject.getUserName(r3)
                java.lang.String r4 = r4.toLowerCase()
                org.telegram.messenger.LocaleController r5 = org.telegram.messenger.LocaleController.getInstance()
                java.lang.String r5 = r5.getTranslitString(r4)
                boolean r6 = r4.equals(r5)
                if (r6 == 0) goto L_0x01c8
                r5 = 0
            L_0x01c8:
                r6 = 0
                r7 = 0
            L_0x01ca:
                if (r7 >= r8) goto L_0x01ab
                r12 = r9[r7]
                boolean r15 = r4.startsWith(r12)
                if (r15 != 0) goto L_0x0213
                java.lang.StringBuilder r15 = new java.lang.StringBuilder
                r15.<init>()
                r15.append(r14)
                r15.append(r12)
                java.lang.String r15 = r15.toString()
                boolean r15 = r4.contains(r15)
                if (r15 != 0) goto L_0x0213
                if (r5 == 0) goto L_0x0207
                boolean r15 = r5.startsWith(r12)
                if (r15 != 0) goto L_0x0213
                java.lang.StringBuilder r15 = new java.lang.StringBuilder
                r15.<init>()
                r15.append(r14)
                r15.append(r12)
                java.lang.String r15 = r15.toString()
                boolean r15 = r5.contains(r15)
                if (r15 == 0) goto L_0x0207
                goto L_0x0213
            L_0x0207:
                java.lang.String r15 = r3.username
                if (r15 == 0) goto L_0x0214
                boolean r15 = r15.startsWith(r12)
                if (r15 == 0) goto L_0x0214
                r6 = 2
                goto L_0x0214
            L_0x0213:
                r6 = 1
            L_0x0214:
                if (r6 == 0) goto L_0x025f
                r15 = 1
                if (r6 != r15) goto L_0x0228
                java.lang.String r4 = r3.first_name
                java.lang.String r5 = r3.last_name
                java.lang.CharSequence r4 = org.telegram.messenger.AndroidUtilities.generateSearchName(r4, r5, r12)
                r10.add(r4)
                r4 = r20
                r12 = 0
                goto L_0x0252
            L_0x0228:
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                r4.append(r13)
                java.lang.String r5 = r3.username
                r4.append(r5)
                java.lang.String r4 = r4.toString()
                java.lang.StringBuilder r5 = new java.lang.StringBuilder
                r5.<init>()
                r5.append(r13)
                r5.append(r12)
                java.lang.String r5 = r5.toString()
                r12 = 0
                java.lang.CharSequence r4 = org.telegram.messenger.AndroidUtilities.generateSearchName(r4, r12, r5)
                r10.add(r4)
                r4 = r20
            L_0x0252:
                r4.add(r3)
                int r5 = r3.id
                r6 = r19
                r6.put(r5, r3)
                r12 = r4
                r15 = r6
                goto L_0x0267
            L_0x025f:
                r15 = r19
                r12 = r20
                int r7 = r7 + 1
                goto L_0x01ca
            L_0x0267:
                int r1 = r1 + 1
                r20 = r12
                r19 = r15
                goto L_0x0181
            L_0x026f:
                r15 = r19
                r12 = r20
                r0.updateSearchResults(r12, r15, r10, r11)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatUsersActivity.SearchAdapter.lambda$null$2$ChatUsersActivity$SearchAdapter(java.lang.String, java.util.ArrayList, java.util.ArrayList):void");
        }

        private void updateSearchResults(ArrayList<TLObject> arrayList, SparseArray<TLObject> sparseArray, ArrayList<CharSequence> arrayList2, ArrayList<TLObject> arrayList3) {
            AndroidUtilities.runOnUIThread(new Runnable(arrayList, sparseArray, arrayList2, arrayList3) {
                public final /* synthetic */ ArrayList f$1;
                public final /* synthetic */ SparseArray f$2;
                public final /* synthetic */ ArrayList f$3;
                public final /* synthetic */ ArrayList f$4;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                }

                public final void run() {
                    ChatUsersActivity.SearchAdapter.this.lambda$updateSearchResults$4$ChatUsersActivity$SearchAdapter(this.f$1, this.f$2, this.f$3, this.f$4);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$updateSearchResults$4 */
        public /* synthetic */ void lambda$updateSearchResults$4$ChatUsersActivity$SearchAdapter(ArrayList arrayList, SparseArray sparseArray, ArrayList arrayList2, ArrayList arrayList3) {
            if (ChatUsersActivity.this.searching) {
                this.searchInProgress = false;
                this.searchResult = arrayList;
                this.searchResultMap = sparseArray;
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

        public void removeUserId(int i) {
            this.searchAdapterHelper.removeUserId(i);
            TLObject tLObject = this.searchResultMap.get(i);
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
                    return this.searchResult.get(i - 1);
                }
            }
            int size3 = this.searchAdapterHelper.getGlobalSearch().size();
            if (size3 == 0 || size3 + 1 <= i || i == 0) {
                return null;
            }
            return this.searchAdapterHelper.getGlobalSearch().get(i - 1);
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onCreateViewHolder$5 */
        public /* synthetic */ boolean lambda$onCreateViewHolder$5$ChatUsersActivity$SearchAdapter(ManageChatUserCell manageChatUserCell, boolean z) {
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
                manageChatUserCell.setDelegate(new ManageChatUserCell.ManageChatUserCellDelegate() {
                    public final boolean onOptionsButtonCheck(ManageChatUserCell manageChatUserCell, boolean z) {
                        return ChatUsersActivity.SearchAdapter.this.lambda$onCreateViewHolder$5$ChatUsersActivity$SearchAdapter(manageChatUserCell, z);
                    }
                });
                graySectionCell = manageChatUserCell;
            }
            return new RecyclerListView.Holder(graySectionCell);
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v1, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v0, resolved type: android.text.SpannableStringBuilder} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v2, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v1, resolved type: android.text.SpannableStringBuilder} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v2, resolved type: android.text.SpannableStringBuilder} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v3, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v2, resolved type: android.text.SpannableStringBuilder} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v4, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v3, resolved type: android.text.SpannableStringBuilder} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v10, resolved type: java.lang.CharSequence} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v8, resolved type: android.text.SpannableStringBuilder} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v11, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v9, resolved type: android.text.SpannableStringBuilder} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v12, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v13, resolved type: android.text.SpannableStringBuilder} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v15, resolved type: java.lang.String} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* JADX WARNING: Removed duplicated region for block: B:43:0x00f3  */
        /* JADX WARNING: Removed duplicated region for block: B:52:0x0125  */
        /* JADX WARNING: Removed duplicated region for block: B:56:0x0132 A[ADDED_TO_REGION] */
        /* JADX WARNING: Removed duplicated region for block: B:77:0x0186  */
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
                goto L_0x01b4
            L_0x0011:
                android.view.View r2 = r2.itemView
                org.telegram.ui.Cells.GraySectionCell r2 = (org.telegram.ui.Cells.GraySectionCell) r2
                int r3 = r1.groupStartRow
                if (r0 != r3) goto L_0x006a
                org.telegram.ui.ChatUsersActivity r0 = org.telegram.ui.ChatUsersActivity.this
                int r0 = r0.type
                if (r0 != 0) goto L_0x002f
                r0 = 2131624685(0x7f0e02ed, float:1.8876557E38)
                java.lang.String r3 = "ChannelBlockedUsers"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
                r2.setText(r0)
                goto L_0x01b4
            L_0x002f:
                org.telegram.ui.ChatUsersActivity r0 = org.telegram.ui.ChatUsersActivity.this
                int r0 = r0.type
                r3 = 3
                if (r0 != r3) goto L_0x0046
                r0 = 2131624752(0x7f0e0330, float:1.8876693E38)
                java.lang.String r3 = "ChannelRestrictedUsers"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
                r2.setText(r0)
                goto L_0x01b4
            L_0x0046:
                org.telegram.ui.ChatUsersActivity r0 = org.telegram.ui.ChatUsersActivity.this
                boolean r0 = r0.isChannel
                if (r0 == 0) goto L_0x005c
                r0 = 2131624760(0x7f0e0338, float:1.8876709E38)
                java.lang.String r3 = "ChannelSubscribers"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
                r2.setText(r0)
                goto L_0x01b4
            L_0x005c:
                r0 = 2131624714(0x7f0e030a, float:1.8876616E38)
                java.lang.String r3 = "ChannelMembers"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
                r2.setText(r0)
                goto L_0x01b4
            L_0x006a:
                int r3 = r1.globalStartRow
                if (r0 != r3) goto L_0x007c
                r0 = 2131625679(0x7f0e06cf, float:1.8878573E38)
                java.lang.String r3 = "GlobalSearch"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
                r2.setText(r0)
                goto L_0x01b4
            L_0x007c:
                int r3 = r1.contactsStartRow
                if (r0 != r3) goto L_0x01b4
                r0 = 2131624973(0x7f0e040d, float:1.887714E38)
                java.lang.String r3 = "Contacts"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
                r2.setText(r0)
                goto L_0x01b4
            L_0x008e:
                org.telegram.tgnet.TLObject r3 = r1.getItem(r0)
                boolean r5 = r3 instanceof org.telegram.tgnet.TLRPC$User
                if (r5 == 0) goto L_0x0099
                org.telegram.tgnet.TLRPC$User r3 = (org.telegram.tgnet.TLRPC$User) r3
                goto L_0x00c6
            L_0x0099:
                boolean r5 = r3 instanceof org.telegram.tgnet.TLRPC$ChannelParticipant
                if (r5 == 0) goto L_0x00b0
                org.telegram.ui.ChatUsersActivity r5 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.messenger.MessagesController r5 = r5.getMessagesController()
                org.telegram.tgnet.TLRPC$ChannelParticipant r3 = (org.telegram.tgnet.TLRPC$ChannelParticipant) r3
                int r3 = r3.user_id
                java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
                org.telegram.tgnet.TLRPC$User r3 = r5.getUser(r3)
                goto L_0x00c6
            L_0x00b0:
                boolean r5 = r3 instanceof org.telegram.tgnet.TLRPC$ChatParticipant
                if (r5 == 0) goto L_0x01b4
                org.telegram.ui.ChatUsersActivity r5 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.messenger.MessagesController r5 = r5.getMessagesController()
                org.telegram.tgnet.TLRPC$ChatParticipant r3 = (org.telegram.tgnet.TLRPC$ChatParticipant) r3
                int r3 = r3.user_id
                java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
                org.telegram.tgnet.TLRPC$User r3 = r5.getUser(r3)
            L_0x00c6:
                java.lang.String r5 = r3.username
                org.telegram.ui.Adapters.SearchAdapterHelper r6 = r1.searchAdapterHelper
                java.util.ArrayList r6 = r6.getGroupSearch()
                int r6 = r6.size()
                r7 = 0
                r8 = 0
                if (r6 == 0) goto L_0x00e2
                int r6 = r6 + r4
                if (r6 <= r0) goto L_0x00e1
                org.telegram.ui.Adapters.SearchAdapterHelper r6 = r1.searchAdapterHelper
                java.lang.String r6 = r6.getLastFoundChannel()
                r9 = 1
                goto L_0x00e4
            L_0x00e1:
                int r0 = r0 - r6
            L_0x00e2:
                r6 = r8
                r9 = 0
            L_0x00e4:
                java.lang.String r10 = "@"
                if (r9 != 0) goto L_0x0126
                java.util.ArrayList<org.telegram.tgnet.TLObject> r11 = r1.searchResult
                int r11 = r11.size()
                if (r11 == 0) goto L_0x0126
                int r11 = r11 + r4
                if (r11 <= r0) goto L_0x0125
                java.util.ArrayList<java.lang.CharSequence> r9 = r1.searchResultNames
                int r11 = r0 + -1
                java.lang.Object r9 = r9.get(r11)
                java.lang.CharSequence r9 = (java.lang.CharSequence) r9
                if (r9 == 0) goto L_0x0120
                boolean r11 = android.text.TextUtils.isEmpty(r5)
                if (r11 != 0) goto L_0x0120
                java.lang.String r11 = r9.toString()
                java.lang.StringBuilder r12 = new java.lang.StringBuilder
                r12.<init>()
                r12.append(r10)
                r12.append(r5)
                java.lang.String r12 = r12.toString()
                boolean r11 = r11.startsWith(r12)
                if (r11 == 0) goto L_0x0120
                r11 = r8
                goto L_0x0122
            L_0x0120:
                r11 = r9
                r9 = r8
            L_0x0122:
                r8 = r0
                r0 = 1
                goto L_0x012a
            L_0x0125:
                int r0 = r0 - r11
            L_0x0126:
                r11 = r8
                r8 = r0
                r0 = r9
                r9 = r11
            L_0x012a:
                r12 = 33
                java.lang.String r13 = "windowBackgroundWhiteBlueText4"
                r14 = -1
                if (r0 != 0) goto L_0x0183
                if (r5 == 0) goto L_0x0183
                org.telegram.ui.Adapters.SearchAdapterHelper r0 = r1.searchAdapterHelper
                java.util.ArrayList r0 = r0.getGlobalSearch()
                int r0 = r0.size()
                if (r0 == 0) goto L_0x0183
                int r0 = r0 + r4
                if (r0 <= r8) goto L_0x0183
                org.telegram.ui.Adapters.SearchAdapterHelper r0 = r1.searchAdapterHelper
                java.lang.String r0 = r0.getLastFoundUsername()
                boolean r9 = r0.startsWith(r10)
                if (r9 == 0) goto L_0x0153
                java.lang.String r0 = r0.substring(r4)
            L_0x0153:
                android.text.SpannableStringBuilder r4 = new android.text.SpannableStringBuilder     // Catch:{ Exception -> 0x017e }
                r4.<init>()     // Catch:{ Exception -> 0x017e }
                r4.append(r10)     // Catch:{ Exception -> 0x017e }
                r4.append(r5)     // Catch:{ Exception -> 0x017e }
                int r9 = org.telegram.messenger.AndroidUtilities.indexOfIgnoreCase(r5, r0)     // Catch:{ Exception -> 0x017e }
                if (r9 == r14) goto L_0x017c
                int r0 = r0.length()     // Catch:{ Exception -> 0x017e }
                if (r9 != 0) goto L_0x016d
                int r0 = r0 + 1
                goto L_0x016f
            L_0x016d:
                int r9 = r9 + 1
            L_0x016f:
                android.text.style.ForegroundColorSpan r10 = new android.text.style.ForegroundColorSpan     // Catch:{ Exception -> 0x017e }
                int r15 = org.telegram.ui.ActionBar.Theme.getColor(r13)     // Catch:{ Exception -> 0x017e }
                r10.<init>(r15)     // Catch:{ Exception -> 0x017e }
                int r0 = r0 + r9
                r4.setSpan(r10, r9, r0, r12)     // Catch:{ Exception -> 0x017e }
            L_0x017c:
                r5 = r4
                goto L_0x0184
            L_0x017e:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
                goto L_0x0184
            L_0x0183:
                r5 = r9
            L_0x0184:
                if (r6 == 0) goto L_0x01a6
                java.lang.String r0 = org.telegram.messenger.UserObject.getUserName(r3)
                android.text.SpannableStringBuilder r11 = new android.text.SpannableStringBuilder
                r11.<init>(r0)
                int r0 = org.telegram.messenger.AndroidUtilities.indexOfIgnoreCase(r0, r6)
                if (r0 == r14) goto L_0x01a6
                android.text.style.ForegroundColorSpan r4 = new android.text.style.ForegroundColorSpan
                int r9 = org.telegram.ui.ActionBar.Theme.getColor(r13)
                r4.<init>(r9)
                int r6 = r6.length()
                int r6 = r6 + r0
                r11.setSpan(r4, r0, r6, r12)
            L_0x01a6:
                android.view.View r0 = r2.itemView
                org.telegram.ui.Cells.ManageChatUserCell r0 = (org.telegram.ui.Cells.ManageChatUserCell) r0
                java.lang.Integer r2 = java.lang.Integer.valueOf(r8)
                r0.setTag(r2)
                r0.setData(r3, r11, r5, r7)
            L_0x01b4:
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
        /* renamed from: lambda$onCreateViewHolder$0 */
        public /* synthetic */ boolean lambda$onCreateViewHolder$0$ChatUsersActivity$ListAdapter(ManageChatUserCell manageChatUserCell, boolean z) {
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
                    case 0: goto L_0x01aa;
                    case 1: goto L_0x01a2;
                    case 2: goto L_0x0193;
                    case 3: goto L_0x018b;
                    case 4: goto L_0x00a6;
                    case 5: goto L_0x0088;
                    case 6: goto L_0x0078;
                    case 7: goto L_0x0068;
                    case 8: goto L_0x005b;
                    case 9: goto L_0x000a;
                    case 10: goto L_0x0046;
                    case 11: goto L_0x001c;
                    default: goto L_0x000a;
                }
            L_0x000a:
                org.telegram.ui.ChatUsersActivity$ChooseView r14 = new org.telegram.ui.ChatUsersActivity$ChooseView
                org.telegram.ui.ChatUsersActivity r13 = org.telegram.ui.ChatUsersActivity.this
                android.content.Context r0 = r12.mContext
                r14.<init>(r0)
                int r13 = org.telegram.ui.ActionBar.Theme.getColor(r2)
                r14.setBackgroundColor(r13)
                goto L_0x01f0
            L_0x001c:
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
                goto L_0x01f0
            L_0x0046:
                org.telegram.ui.Cells.LoadingCell r14 = new org.telegram.ui.Cells.LoadingCell
                android.content.Context r13 = r12.mContext
                r0 = 1109393408(0x42200000, float:40.0)
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
                r1 = 1123024896(0x42var_, float:120.0)
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                r14.<init>(r13, r0, r1)
                goto L_0x01f0
            L_0x005b:
                org.telegram.ui.Cells.GraySectionCell r14 = new org.telegram.ui.Cells.GraySectionCell
                android.content.Context r13 = r12.mContext
                r14.<init>(r13)
                r13 = 0
                r14.setBackground(r13)
                goto L_0x01f0
            L_0x0068:
                org.telegram.ui.Cells.TextCheckCell2 r14 = new org.telegram.ui.Cells.TextCheckCell2
                android.content.Context r13 = r12.mContext
                r14.<init>(r13)
                int r13 = org.telegram.ui.ActionBar.Theme.getColor(r2)
                r14.setBackgroundColor(r13)
                goto L_0x01f0
            L_0x0078:
                org.telegram.ui.Cells.TextSettingsCell r14 = new org.telegram.ui.Cells.TextSettingsCell
                android.content.Context r13 = r12.mContext
                r14.<init>(r13)
                int r13 = org.telegram.ui.ActionBar.Theme.getColor(r2)
                r14.setBackgroundColor(r13)
                goto L_0x01f0
            L_0x0088:
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
                goto L_0x01f0
            L_0x00a6:
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
                r2 = 2131626227(0x7f0e08f3, float:1.8879684E38)
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
                if (r2 == 0) goto L_0x0158
                r2 = 2131626225(0x7f0e08f1, float:1.887968E38)
                java.lang.String r5 = "NoBlockedChannel2"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r5, r2)
                r0.setText(r2)
                goto L_0x0164
            L_0x0158:
                r2 = 2131626226(0x7f0e08f2, float:1.8879682E38)
                java.lang.String r5 = "NoBlockedGroup2"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r5, r2)
                r0.setText(r2)
            L_0x0164:
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
                goto L_0x01f0
            L_0x018b:
                org.telegram.ui.Cells.ShadowSectionCell r14 = new org.telegram.ui.Cells.ShadowSectionCell
                android.content.Context r13 = r12.mContext
                r14.<init>(r13)
                goto L_0x01f0
            L_0x0193:
                org.telegram.ui.Cells.ManageChatTextCell r14 = new org.telegram.ui.Cells.ManageChatTextCell
                android.content.Context r13 = r12.mContext
                r14.<init>(r13)
                int r13 = org.telegram.ui.ActionBar.Theme.getColor(r2)
                r14.setBackgroundColor(r13)
                goto L_0x01f0
            L_0x01a2:
                org.telegram.ui.Cells.TextInfoPrivacyCell r14 = new org.telegram.ui.Cells.TextInfoPrivacyCell
                android.content.Context r13 = r12.mContext
                r14.<init>(r13)
                goto L_0x01f0
            L_0x01aa:
                org.telegram.ui.Cells.ManageChatUserCell r14 = new org.telegram.ui.Cells.ManageChatUserCell
                android.content.Context r1 = r12.mContext
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                int r4 = r4.type
                r5 = 3
                if (r4 == 0) goto L_0x01c2
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                int r4 = r4.type
                if (r4 != r5) goto L_0x01c0
                goto L_0x01c2
            L_0x01c0:
                r4 = 6
                goto L_0x01c3
            L_0x01c2:
                r4 = 7
            L_0x01c3:
                org.telegram.ui.ChatUsersActivity r6 = org.telegram.ui.ChatUsersActivity.this
                int r6 = r6.type
                if (r6 == 0) goto L_0x01d5
                org.telegram.ui.ChatUsersActivity r6 = org.telegram.ui.ChatUsersActivity.this
                int r6 = r6.type
                if (r6 != r5) goto L_0x01d4
                goto L_0x01d5
            L_0x01d4:
                r0 = 2
            L_0x01d5:
                org.telegram.ui.ChatUsersActivity r5 = org.telegram.ui.ChatUsersActivity.this
                int r5 = r5.selectType
                if (r5 != 0) goto L_0x01de
                r13 = 1
            L_0x01de:
                r14.<init>(r1, r4, r0, r13)
                int r13 = org.telegram.ui.ActionBar.Theme.getColor(r2)
                r14.setBackgroundColor(r13)
                org.telegram.ui.-$$Lambda$ChatUsersActivity$ListAdapter$Xr7PIkQyIu3_GPO5ta7IQSFNZ7U r13 = new org.telegram.ui.-$$Lambda$ChatUsersActivity$ListAdapter$Xr7PIkQyIu3_GPO5ta7IQSFNZ7U
                r13.<init>()
                r14.setDelegate(r13)
            L_0x01f0:
                org.telegram.ui.Components.RecyclerListView$Holder r13 = new org.telegram.ui.Components.RecyclerListView$Holder
                r13.<init>(r14)
                return r13
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatUsersActivity.ListAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        /* JADX WARNING: Code restructure failed: missing block: B:250:0x066b, code lost:
            if (org.telegram.ui.ChatUsersActivity.access$2600(r0.this$0).megagroup == false) goto L_0x066d;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:259:0x0699, code lost:
            if (org.telegram.ui.ChatUsersActivity.access$2600(r0.this$0).megagroup == false) goto L_0x066d;
         */
        /* JADX WARNING: Removed duplicated region for block: B:264:0x06a7  */
        /* JADX WARNING: Removed duplicated region for block: B:265:0x06ba  */
        /* JADX WARNING: Removed duplicated region for block: B:270:0x06dc  */
        /* JADX WARNING: Removed duplicated region for block: B:304:0x0781  */
        /* JADX WARNING: Removed duplicated region for block: B:305:0x0784  */
        /* JADX WARNING: Removed duplicated region for block: B:360:? A[RETURN, SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r17, int r18) {
            /*
                r16 = this;
                r0 = r16
                r1 = r17
                r2 = r18
                int r3 = r17.getItemViewType()
                r4 = 2
                r5 = 3
                r6 = 0
                r7 = 0
                r8 = 1
                if (r3 == 0) goto L_0x0632
                r9 = 2131165448(0x7var_, float:1.7945113E38)
                java.lang.String r10 = ""
                r11 = 2131165449(0x7var_, float:1.7945115E38)
                r12 = -1
                java.lang.String r13 = "windowBackgroundGrayShadow"
                if (r3 == r8) goto L_0x0512
                if (r3 == r4) goto L_0x03ea
                if (r3 == r5) goto L_0x03a7
                r4 = 5
                if (r3 == r4) goto L_0x030d
                r4 = 6
                if (r3 == r4) goto L_0x02db
                r4 = 7
                if (r3 == r4) goto L_0x00f8
                r4 = 8
                if (r3 == r4) goto L_0x005d
                r2 = 11
                if (r3 == r2) goto L_0x0036
                goto L_0x07a8
            L_0x0036:
                android.view.View r1 = r1.itemView
                org.telegram.ui.Components.FlickerLoadingView r1 = (org.telegram.ui.Components.FlickerLoadingView) r1
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                int r2 = r2.type
                if (r2 != 0) goto L_0x0058
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r2 = r2.info
                if (r2 != 0) goto L_0x004b
                goto L_0x0053
            L_0x004b:
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r2 = r2.info
                int r8 = r2.kicked_count
            L_0x0053:
                r1.setItemsCount(r8)
                goto L_0x07a8
            L_0x0058:
                r1.setItemsCount(r8)
                goto L_0x07a8
            L_0x005d:
                android.view.View r1 = r1.itemView
                org.telegram.ui.Cells.GraySectionCell r1 = (org.telegram.ui.Cells.GraySectionCell) r1
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.membersHeaderRow
                if (r2 != r3) goto L_0x009b
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$Chat r2 = r2.currentChat
                boolean r2 = org.telegram.messenger.ChatObject.isChannel(r2)
                if (r2 == 0) goto L_0x008d
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$Chat r2 = r2.currentChat
                boolean r2 = r2.megagroup
                if (r2 != 0) goto L_0x008d
                r2 = 2131624738(0x7f0e0322, float:1.8876664E38)
                java.lang.String r3 = "ChannelOtherSubscribers"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2)
                goto L_0x07a8
            L_0x008d:
                r2 = 2131624736(0x7f0e0320, float:1.887666E38)
                java.lang.String r3 = "ChannelOtherMembers"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2)
                goto L_0x07a8
            L_0x009b:
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.botHeaderRow
                if (r2 != r3) goto L_0x00b1
                r2 = 2131624686(0x7f0e02ee, float:1.8876559E38)
                java.lang.String r3 = "ChannelBots"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2)
                goto L_0x07a8
            L_0x00b1:
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.contactsHeaderRow
                if (r2 != r3) goto L_0x00eb
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$Chat r2 = r2.currentChat
                boolean r2 = org.telegram.messenger.ChatObject.isChannel(r2)
                if (r2 == 0) goto L_0x00dd
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$Chat r2 = r2.currentChat
                boolean r2 = r2.megagroup
                if (r2 != 0) goto L_0x00dd
                r2 = 2131624693(0x7f0e02f5, float:1.8876573E38)
                java.lang.String r3 = "ChannelContacts"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2)
                goto L_0x07a8
            L_0x00dd:
                r2 = 2131625689(0x7f0e06d9, float:1.8878593E38)
                java.lang.String r3 = "GroupContacts"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2)
                goto L_0x07a8
            L_0x00eb:
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.loadingHeaderRow
                if (r2 != r3) goto L_0x07a8
                r1.setText(r10)
                goto L_0x07a8
            L_0x00f8:
                android.view.View r1 = r1.itemView
                org.telegram.ui.Cells.TextCheckCell2 r1 = (org.telegram.ui.Cells.TextCheckCell2) r1
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.changeInfoRow
                if (r2 != r3) goto L_0x012d
                r3 = 2131627794(0x7f0e0var_, float:1.8882862E38)
                java.lang.String r4 = "UserRestrictionsChangeInfo"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$TL_chatBannedRights r4 = r4.defaultBannedRights
                boolean r4 = r4.change_info
                if (r4 != 0) goto L_0x0127
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$Chat r4 = r4.currentChat
                java.lang.String r4 = r4.username
                boolean r4 = android.text.TextUtils.isEmpty(r4)
                if (r4 == 0) goto L_0x0127
                r4 = 1
                goto L_0x0128
            L_0x0127:
                r4 = 0
            L_0x0128:
                r1.setTextAndCheck(r3, r4, r7)
                goto L_0x0213
            L_0x012d:
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.addUsersRow
                if (r2 != r3) goto L_0x014c
                r3 = 2131627799(0x7f0e0var_, float:1.8882873E38)
                java.lang.String r4 = "UserRestrictionsInviteUsers"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$TL_chatBannedRights r4 = r4.defaultBannedRights
                boolean r4 = r4.invite_users
                r4 = r4 ^ r8
                r1.setTextAndCheck(r3, r4, r8)
                goto L_0x0213
            L_0x014c:
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.pinMessagesRow
                if (r2 != r3) goto L_0x017d
                r3 = 2131627809(0x7f0e0var_, float:1.8882893E38)
                java.lang.String r4 = "UserRestrictionsPinMessages"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$TL_chatBannedRights r4 = r4.defaultBannedRights
                boolean r4 = r4.pin_messages
                if (r4 != 0) goto L_0x0177
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$Chat r4 = r4.currentChat
                java.lang.String r4 = r4.username
                boolean r4 = android.text.TextUtils.isEmpty(r4)
                if (r4 == 0) goto L_0x0177
                r4 = 1
                goto L_0x0178
            L_0x0177:
                r4 = 0
            L_0x0178:
                r1.setTextAndCheck(r3, r4, r8)
                goto L_0x0213
            L_0x017d:
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.sendMessagesRow
                if (r2 != r3) goto L_0x019c
                r3 = 2131627811(0x7f0e0var_, float:1.8882897E38)
                java.lang.String r4 = "UserRestrictionsSend"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$TL_chatBannedRights r4 = r4.defaultBannedRights
                boolean r4 = r4.send_messages
                r4 = r4 ^ r8
                r1.setTextAndCheck(r3, r4, r8)
                goto L_0x0213
            L_0x019c:
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.sendMediaRow
                if (r2 != r3) goto L_0x01ba
                r3 = 2131627812(0x7f0e0var_, float:1.88829E38)
                java.lang.String r4 = "UserRestrictionsSendMedia"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$TL_chatBannedRights r4 = r4.defaultBannedRights
                boolean r4 = r4.send_media
                r4 = r4 ^ r8
                r1.setTextAndCheck(r3, r4, r8)
                goto L_0x0213
            L_0x01ba:
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.sendStickersRow
                if (r2 != r3) goto L_0x01d8
                r3 = 2131627814(0x7f0e0var_, float:1.8882903E38)
                java.lang.String r4 = "UserRestrictionsSendStickers"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$TL_chatBannedRights r4 = r4.defaultBannedRights
                boolean r4 = r4.send_stickers
                r4 = r4 ^ r8
                r1.setTextAndCheck(r3, r4, r8)
                goto L_0x0213
            L_0x01d8:
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.embedLinksRow
                if (r2 != r3) goto L_0x01f6
                r3 = 2131627798(0x7f0e0var_, float:1.888287E38)
                java.lang.String r4 = "UserRestrictionsEmbedLinks"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$TL_chatBannedRights r4 = r4.defaultBannedRights
                boolean r4 = r4.embed_links
                r4 = r4 ^ r8
                r1.setTextAndCheck(r3, r4, r8)
                goto L_0x0213
            L_0x01f6:
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.sendPollsRow
                if (r2 != r3) goto L_0x0213
                r3 = 2131627813(0x7f0e0var_, float:1.8882901E38)
                java.lang.String r4 = "UserRestrictionsSendPolls"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$TL_chatBannedRights r4 = r4.defaultBannedRights
                boolean r4 = r4.send_polls
                r4 = r4 ^ r8
                r1.setTextAndCheck(r3, r4, r8)
            L_0x0213:
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.sendMediaRow
                if (r2 == r3) goto L_0x0249
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.sendStickersRow
                if (r2 == r3) goto L_0x0249
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.embedLinksRow
                if (r2 == r3) goto L_0x0249
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.sendPollsRow
                if (r2 != r3) goto L_0x0234
                goto L_0x0249
            L_0x0234:
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.sendMessagesRow
                if (r2 != r3) goto L_0x0263
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$TL_chatBannedRights r3 = r3.defaultBannedRights
                boolean r3 = r3.view_messages
                r3 = r3 ^ r8
                r1.setEnabled(r3)
                goto L_0x0263
            L_0x0249:
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$TL_chatBannedRights r3 = r3.defaultBannedRights
                boolean r3 = r3.send_messages
                if (r3 != 0) goto L_0x025f
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$TL_chatBannedRights r3 = r3.defaultBannedRights
                boolean r3 = r3.view_messages
                if (r3 != 0) goto L_0x025f
                r3 = 1
                goto L_0x0260
            L_0x025f:
                r3 = 0
            L_0x0260:
                r1.setEnabled(r3)
            L_0x0263:
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$Chat r3 = r3.currentChat
                boolean r3 = org.telegram.messenger.ChatObject.canBlockUsers(r3)
                if (r3 == 0) goto L_0x02d6
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.addUsersRow
                if (r2 != r3) goto L_0x0283
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$Chat r3 = r3.currentChat
                boolean r3 = org.telegram.messenger.ChatObject.canUserDoAdminAction(r3, r5)
                if (r3 == 0) goto L_0x02c9
            L_0x0283:
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.pinMessagesRow
                if (r2 != r3) goto L_0x0297
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$Chat r3 = r3.currentChat
                boolean r3 = org.telegram.messenger.ChatObject.canUserDoAdminAction(r3, r7)
                if (r3 == 0) goto L_0x02c9
            L_0x0297:
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.changeInfoRow
                if (r2 != r3) goto L_0x02ab
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$Chat r3 = r3.currentChat
                boolean r3 = org.telegram.messenger.ChatObject.canUserDoAdminAction(r3, r8)
                if (r3 == 0) goto L_0x02c9
            L_0x02ab:
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$Chat r3 = r3.currentChat
                java.lang.String r3 = r3.username
                boolean r3 = android.text.TextUtils.isEmpty(r3)
                if (r3 != 0) goto L_0x02d1
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.pinMessagesRow
                if (r2 == r3) goto L_0x02c9
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.changeInfoRow
                if (r2 != r3) goto L_0x02d1
            L_0x02c9:
                r2 = 2131165887(0x7var_bf, float:1.7946004E38)
                r1.setIcon(r2)
                goto L_0x07a8
            L_0x02d1:
                r1.setIcon(r7)
                goto L_0x07a8
            L_0x02d6:
                r1.setIcon(r7)
                goto L_0x07a8
            L_0x02db:
                android.view.View r1 = r1.itemView
                org.telegram.ui.Cells.TextSettingsCell r1 = (org.telegram.ui.Cells.TextSettingsCell) r1
                r2 = 2131624683(0x7f0e02eb, float:1.8876553E38)
                java.lang.String r3 = "ChannelBlacklist"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                java.lang.Object[] r3 = new java.lang.Object[r8]
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r4 = r4.info
                if (r4 == 0) goto L_0x02fb
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r4 = r4.info
                int r4 = r4.kicked_count
                goto L_0x02fc
            L_0x02fb:
                r4 = 0
            L_0x02fc:
                java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
                r3[r7] = r4
                java.lang.String r4 = "%d"
                java.lang.String r3 = java.lang.String.format(r4, r3)
                r1.setTextAndValue(r2, r3, r7)
                goto L_0x07a8
            L_0x030d:
                android.view.View r1 = r1.itemView
                org.telegram.ui.Cells.HeaderCell r1 = (org.telegram.ui.Cells.HeaderCell) r1
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.restricted1SectionRow
                if (r2 != r3) goto L_0x0365
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                int r2 = r2.type
                if (r2 != 0) goto L_0x0357
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r2 = r2.info
                if (r2 == 0) goto L_0x0332
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r2 = r2.info
                int r2 = r2.kicked_count
                goto L_0x033c
            L_0x0332:
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                java.util.ArrayList r2 = r2.participants
                int r2 = r2.size()
            L_0x033c:
                if (r2 == 0) goto L_0x0349
                java.lang.String r3 = "RemovedUser"
                java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r3, r2)
                r1.setText(r2)
                goto L_0x07a8
            L_0x0349:
                r2 = 2131624685(0x7f0e02ed, float:1.8876557E38)
                java.lang.String r3 = "ChannelBlockedUsers"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2)
                goto L_0x07a8
            L_0x0357:
                r2 = 2131624752(0x7f0e0330, float:1.8876693E38)
                java.lang.String r3 = "ChannelRestrictedUsers"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2)
                goto L_0x07a8
            L_0x0365:
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.permissionsSectionRow
                if (r2 != r3) goto L_0x037b
                r2 = 2131624740(0x7f0e0324, float:1.8876668E38)
                java.lang.String r3 = "ChannelPermissionsHeader"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2)
                goto L_0x07a8
            L_0x037b:
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.slowmodeRow
                if (r2 != r3) goto L_0x0391
                r2 = 2131627452(0x7f0e0dbc, float:1.8882169E38)
                java.lang.String r3 = "Slowmode"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2)
                goto L_0x07a8
            L_0x0391:
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.gigaHeaderRow
                if (r2 != r3) goto L_0x07a8
                r2 = 2131624587(0x7f0e028b, float:1.8876358E38)
                java.lang.String r3 = "BroadcastGroup"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2)
                goto L_0x07a8
            L_0x03a7:
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.addNewSectionRow
                if (r2 == r3) goto L_0x03dd
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.type
                if (r3 != r5) goto L_0x03d0
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.participantsDividerRow
                if (r2 != r3) goto L_0x03d0
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                int r2 = r2.addNewRow
                if (r2 != r12) goto L_0x03d0
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                int r2 = r2.participantsStartRow
                if (r2 != r12) goto L_0x03d0
                goto L_0x03dd
            L_0x03d0:
                android.view.View r1 = r1.itemView
                android.content.Context r2 = r0.mContext
                android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r2, (int) r9, (java.lang.String) r13)
                r1.setBackgroundDrawable(r2)
                goto L_0x07a8
            L_0x03dd:
                android.view.View r1 = r1.itemView
                android.content.Context r2 = r0.mContext
                android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r2, (int) r11, (java.lang.String) r13)
                r1.setBackgroundDrawable(r2)
                goto L_0x07a8
            L_0x03ea:
                android.view.View r1 = r1.itemView
                org.telegram.ui.Cells.ManageChatTextCell r1 = (org.telegram.ui.Cells.ManageChatTextCell) r1
                java.lang.String r3 = "windowBackgroundWhiteGrayIcon"
                java.lang.String r9 = "windowBackgroundWhiteBlackText"
                r1.setColors(r3, r9)
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.addNewRow
                java.lang.String r9 = "windowBackgroundWhiteBlueButton"
                java.lang.String r10 = "windowBackgroundWhiteBlueIcon"
                if (r2 != r3) goto L_0x04c4
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                int r2 = r2.type
                r3 = 2131165249(0x7var_, float:1.794471E38)
                if (r2 != r5) goto L_0x042a
                r1.setColors(r10, r9)
                r2 = 2131624669(0x7f0e02dd, float:1.8876524E38)
                java.lang.String r4 = "ChannelAddException"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r2)
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                int r4 = r4.participantsStartRow
                if (r4 == r12) goto L_0x0425
                r7 = 1
            L_0x0425:
                r1.setText(r2, r6, r3, r7)
                goto L_0x07a8
            L_0x042a:
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                int r2 = r2.type
                if (r2 != 0) goto L_0x0443
                r2 = 2131624684(0x7f0e02ec, float:1.8876555E38)
                java.lang.String r3 = "ChannelBlockUser"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r3 = 2131165255(0x7var_, float:1.7944722E38)
                r1.setText(r2, r6, r3, r7)
                goto L_0x07a8
            L_0x0443:
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                int r2 = r2.type
                if (r2 != r8) goto L_0x0470
                r1.setColors(r10, r9)
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                boolean r2 = r2.loadingUsers
                if (r2 == 0) goto L_0x045e
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                boolean r2 = r2.firstLoaded
                if (r2 == 0) goto L_0x045f
            L_0x045e:
                r7 = 1
            L_0x045f:
                r2 = 2131624668(0x7f0e02dc, float:1.8876522E38)
                java.lang.String r3 = "ChannelAddAdmin"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r3 = 2131165259(0x7var_b, float:1.794473E38)
                r1.setText(r2, r6, r3, r7)
                goto L_0x07a8
            L_0x0470:
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                int r2 = r2.type
                if (r2 != r4) goto L_0x07a8
                r1.setColors(r10, r9)
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                boolean r2 = r2.loadingUsers
                if (r2 == 0) goto L_0x048b
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                boolean r2 = r2.firstLoaded
                if (r2 == 0) goto L_0x04a0
            L_0x048b:
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                int r2 = r2.membersHeaderRow
                if (r2 != r12) goto L_0x04a0
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                java.util.ArrayList r2 = r2.participants
                boolean r2 = r2.isEmpty()
                if (r2 != 0) goto L_0x04a0
                r7 = 1
            L_0x04a0:
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                boolean r2 = r2.isChannel
                if (r2 == 0) goto L_0x04b6
                r2 = 2131624219(0x7f0e011b, float:1.8875612E38)
                java.lang.String r4 = "AddSubscriber"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r2)
                r1.setText(r2, r6, r3, r7)
                goto L_0x07a8
            L_0x04b6:
                r2 = 2131624201(0x7f0e0109, float:1.8875575E38)
                java.lang.String r4 = "AddMember"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r2)
                r1.setText(r2, r6, r3, r7)
                goto L_0x07a8
            L_0x04c4:
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.recentActionsRow
                if (r2 != r3) goto L_0x04dd
                r2 = 2131625315(0x7f0e0563, float:1.8877834E38)
                java.lang.String r3 = "EventLog"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r3 = 2131165459(0x7var_, float:1.7945136E38)
                r1.setText(r2, r6, r3, r7)
                goto L_0x07a8
            L_0x04dd:
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.addNew2Row
                if (r2 != r3) goto L_0x04f6
                r2 = 2131624705(0x7f0e0301, float:1.8876597E38)
                java.lang.String r3 = "ChannelInviteViaLink"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r3 = 2131165957(0x7var_, float:1.7946146E38)
                r1.setText(r2, r6, r3, r8)
                goto L_0x07a8
            L_0x04f6:
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.gigaConvertRow
                if (r2 != r3) goto L_0x07a8
                r1.setColors(r10, r9)
                r2 = 2131624588(0x7f0e028c, float:1.887636E38)
                java.lang.String r3 = "BroadcastGroupConvert"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r3 = 2131165722(0x7var_a, float:1.794567E38)
                r1.setText(r2, r6, r3, r7)
                goto L_0x07a8
            L_0x0512:
                android.view.View r1 = r1.itemView
                org.telegram.ui.Cells.TextInfoPrivacyCell r1 = (org.telegram.ui.Cells.TextInfoPrivacyCell) r1
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.participantsInfoRow
                if (r2 != r3) goto L_0x05d0
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                int r2 = r2.type
                if (r2 == 0) goto L_0x05a4
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                int r2 = r2.type
                if (r2 != r5) goto L_0x0530
                goto L_0x05a4
            L_0x0530:
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                int r2 = r2.type
                if (r2 != r8) goto L_0x0570
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                int r2 = r2.addNewRow
                if (r2 == r12) goto L_0x0562
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                boolean r2 = r2.isChannel
                if (r2 == 0) goto L_0x0555
                r2 = 2131624679(0x7f0e02e7, float:1.8876545E38)
                java.lang.String r3 = "ChannelAdminsInfo"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2)
                goto L_0x0565
            L_0x0555:
                r2 = 2131626053(0x7f0e0845, float:1.8879331E38)
                java.lang.String r3 = "MegaAdminsInfo"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2)
                goto L_0x0565
            L_0x0562:
                r1.setText(r10)
            L_0x0565:
                android.content.Context r2 = r0.mContext
                android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r2, (int) r11, (java.lang.String) r13)
                r1.setBackgroundDrawable(r2)
                goto L_0x07a8
            L_0x0570:
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                int r2 = r2.type
                if (r2 != r4) goto L_0x07a8
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                boolean r2 = r2.isChannel
                if (r2 == 0) goto L_0x0596
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                int r2 = r2.selectType
                if (r2 == 0) goto L_0x0589
                goto L_0x0596
            L_0x0589:
                r2 = 2131624715(0x7f0e030b, float:1.8876618E38)
                java.lang.String r3 = "ChannelMembersInfo"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2)
                goto L_0x0599
            L_0x0596:
                r1.setText(r10)
            L_0x0599:
                android.content.Context r2 = r0.mContext
                android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r2, (int) r11, (java.lang.String) r13)
                r1.setBackgroundDrawable(r2)
                goto L_0x07a8
            L_0x05a4:
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                boolean r2 = r2.isChannel
                if (r2 == 0) goto L_0x05b9
                r2 = 2131626225(0x7f0e08f1, float:1.887968E38)
                java.lang.String r3 = "NoBlockedChannel2"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2)
                goto L_0x05c5
            L_0x05b9:
                r2 = 2131626226(0x7f0e08f2, float:1.8879682E38)
                java.lang.String r3 = "NoBlockedGroup2"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2)
            L_0x05c5:
                android.content.Context r2 = r0.mContext
                android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r2, (int) r11, (java.lang.String) r13)
                r1.setBackgroundDrawable(r2)
                goto L_0x07a8
            L_0x05d0:
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.slowmodeInfoRow
                if (r2 != r3) goto L_0x061c
                android.content.Context r2 = r0.mContext
                android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r2, (int) r9, (java.lang.String) r13)
                r1.setBackgroundDrawable(r2)
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r2.selectedSlowmode
                int r2 = r2.getSecondsForIndex(r3)
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r3 = r3.info
                if (r3 == 0) goto L_0x060e
                if (r2 != 0) goto L_0x05f6
                goto L_0x060e
            L_0x05f6:
                r3 = 2131627455(0x7f0e0dbf, float:1.8882175E38)
                java.lang.Object[] r4 = new java.lang.Object[r8]
                org.telegram.ui.ChatUsersActivity r5 = org.telegram.ui.ChatUsersActivity.this
                java.lang.String r2 = r5.formatSeconds(r2)
                r4[r7] = r2
                java.lang.String r2 = "SlowmodeInfoSelected"
                java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)
                r1.setText(r2)
                goto L_0x07a8
            L_0x060e:
                r2 = 2131627454(0x7f0e0dbe, float:1.8882173E38)
                java.lang.String r3 = "SlowmodeInfoOff"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2)
                goto L_0x07a8
            L_0x061c:
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.gigaInfoRow
                if (r2 != r3) goto L_0x07a8
                r2 = 2131624589(0x7f0e028d, float:1.8876362E38)
                java.lang.String r3 = "BroadcastGroupConvertInfo"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2)
                goto L_0x07a8
            L_0x0632:
                android.view.View r1 = r1.itemView
                org.telegram.ui.Cells.ManageChatUserCell r1 = (org.telegram.ui.Cells.ManageChatUserCell) r1
                java.lang.Integer r3 = java.lang.Integer.valueOf(r18)
                r1.setTag(r3)
                org.telegram.tgnet.TLObject r3 = r0.getItem(r2)
                org.telegram.ui.ChatUsersActivity r9 = org.telegram.ui.ChatUsersActivity.this
                int r9 = r9.participantsStartRow
                if (r2 < r9) goto L_0x066f
                org.telegram.ui.ChatUsersActivity r9 = org.telegram.ui.ChatUsersActivity.this
                int r9 = r9.participantsEndRow
                if (r2 >= r9) goto L_0x066f
                org.telegram.ui.ChatUsersActivity r9 = org.telegram.ui.ChatUsersActivity.this
                int r9 = r9.participantsEndRow
                org.telegram.ui.ChatUsersActivity r10 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$Chat r10 = r10.currentChat
                boolean r10 = org.telegram.messenger.ChatObject.isChannel(r10)
                if (r10 == 0) goto L_0x06a2
                org.telegram.ui.ChatUsersActivity r10 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$Chat r10 = r10.currentChat
                boolean r10 = r10.megagroup
                if (r10 != 0) goto L_0x06a2
            L_0x066d:
                r10 = 1
                goto L_0x06a3
            L_0x066f:
                org.telegram.ui.ChatUsersActivity r9 = org.telegram.ui.ChatUsersActivity.this
                int r9 = r9.contactsStartRow
                if (r2 < r9) goto L_0x069c
                org.telegram.ui.ChatUsersActivity r9 = org.telegram.ui.ChatUsersActivity.this
                int r9 = r9.contactsEndRow
                if (r2 >= r9) goto L_0x069c
                org.telegram.ui.ChatUsersActivity r9 = org.telegram.ui.ChatUsersActivity.this
                int r9 = r9.contactsEndRow
                org.telegram.ui.ChatUsersActivity r10 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$Chat r10 = r10.currentChat
                boolean r10 = org.telegram.messenger.ChatObject.isChannel(r10)
                if (r10 == 0) goto L_0x06a2
                org.telegram.ui.ChatUsersActivity r10 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$Chat r10 = r10.currentChat
                boolean r10 = r10.megagroup
                if (r10 != 0) goto L_0x06a2
                goto L_0x066d
            L_0x069c:
                org.telegram.ui.ChatUsersActivity r9 = org.telegram.ui.ChatUsersActivity.this
                int r9 = r9.botEndRow
            L_0x06a2:
                r10 = 0
            L_0x06a3:
                boolean r11 = r3 instanceof org.telegram.tgnet.TLRPC$ChannelParticipant
                if (r11 == 0) goto L_0x06ba
                org.telegram.tgnet.TLRPC$ChannelParticipant r3 = (org.telegram.tgnet.TLRPC$ChannelParticipant) r3
                int r11 = r3.user_id
                int r12 = r3.kicked_by
                int r13 = r3.promoted_by
                org.telegram.tgnet.TLRPC$TL_chatBannedRights r14 = r3.banned_rights
                int r15 = r3.date
                boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_channelParticipantBanned
                boolean r7 = r3 instanceof org.telegram.tgnet.TLRPC$TL_channelParticipantCreator
                boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC$TL_channelParticipantAdmin
                goto L_0x06cc
            L_0x06ba:
                boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$ChatParticipant
                if (r4 == 0) goto L_0x07a8
                org.telegram.tgnet.TLRPC$ChatParticipant r3 = (org.telegram.tgnet.TLRPC$ChatParticipant) r3
                int r11 = r3.user_id
                int r15 = r3.date
                boolean r7 = r3 instanceof org.telegram.tgnet.TLRPC$TL_chatParticipantCreator
                boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC$TL_chatParticipantAdmin
                r14 = r6
                r4 = 0
                r12 = 0
                r13 = 0
            L_0x06cc:
                org.telegram.ui.ChatUsersActivity r6 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.messenger.MessagesController r6 = r6.getMessagesController()
                java.lang.Integer r11 = java.lang.Integer.valueOf(r11)
                org.telegram.tgnet.TLRPC$User r6 = r6.getUser(r11)
                if (r6 == 0) goto L_0x07a8
                org.telegram.ui.ChatUsersActivity r11 = org.telegram.ui.ChatUsersActivity.this
                int r11 = r11.type
                if (r11 != r5) goto L_0x06f7
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                java.lang.String r3 = r3.formatUserPermissions(r14)
                int r9 = r9 - r8
                if (r2 == r9) goto L_0x06f0
                r2 = 0
                r7 = 1
                goto L_0x06f2
            L_0x06f0:
                r2 = 0
                r7 = 0
            L_0x06f2:
                r1.setData(r6, r2, r3, r7)
                goto L_0x07a8
            L_0x06f7:
                org.telegram.ui.ChatUsersActivity r5 = org.telegram.ui.ChatUsersActivity.this
                int r5 = r5.type
                if (r5 != 0) goto L_0x0732
                if (r4 == 0) goto L_0x0724
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.messenger.MessagesController r3 = r3.getMessagesController()
                java.lang.Integer r4 = java.lang.Integer.valueOf(r12)
                org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r4)
                if (r3 == 0) goto L_0x0724
                r4 = 2131627786(0x7f0e0f0a, float:1.8882846E38)
                java.lang.Object[] r5 = new java.lang.Object[r8]
                java.lang.String r3 = org.telegram.messenger.UserObject.getUserName(r3)
                r7 = 0
                r5[r7] = r3
                java.lang.String r3 = "UserRemovedBy"
                java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r5)
                goto L_0x0725
            L_0x0724:
                r3 = 0
            L_0x0725:
                int r9 = r9 - r8
                if (r2 == r9) goto L_0x072b
                r2 = 0
                r7 = 1
                goto L_0x072d
            L_0x072b:
                r2 = 0
                r7 = 0
            L_0x072d:
                r1.setData(r6, r2, r3, r7)
                goto L_0x07a8
            L_0x0732:
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                int r4 = r4.type
                if (r4 != r8) goto L_0x0789
                if (r7 == 0) goto L_0x0747
                r3 = 2131624694(0x7f0e02f6, float:1.8876575E38)
                java.lang.String r4 = "ChannelCreator"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            L_0x0745:
                r7 = 0
                goto L_0x077e
            L_0x0747:
                if (r3 == 0) goto L_0x077c
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.messenger.MessagesController r3 = r3.getMessagesController()
                java.lang.Integer r4 = java.lang.Integer.valueOf(r13)
                org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r4)
                if (r3 == 0) goto L_0x077c
                int r4 = r3.id
                int r5 = r6.id
                if (r4 != r5) goto L_0x0769
                r3 = 2131624677(0x7f0e02e5, float:1.887654E38)
                java.lang.String r4 = "ChannelAdministrator"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                goto L_0x0745
            L_0x0769:
                r4 = 2131625216(0x7f0e0500, float:1.8877634E38)
                java.lang.Object[] r5 = new java.lang.Object[r8]
                java.lang.String r3 = org.telegram.messenger.UserObject.getUserName(r3)
                r7 = 0
                r5[r7] = r3
                java.lang.String r3 = "EditAdminPromotedBy"
                java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r5)
                goto L_0x077e
            L_0x077c:
                r7 = 0
                r3 = 0
            L_0x077e:
                int r9 = r9 - r8
                if (r2 == r9) goto L_0x0784
                r2 = 0
                r7 = 1
                goto L_0x0785
            L_0x0784:
                r2 = 0
            L_0x0785:
                r1.setData(r6, r2, r3, r7)
                goto L_0x07a8
            L_0x0789:
                r7 = 0
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.type
                r4 = 2
                if (r3 != r4) goto L_0x07a8
                if (r10 == 0) goto L_0x079d
                if (r15 == 0) goto L_0x079d
                long r3 = (long) r15
                java.lang.String r3 = org.telegram.messenger.LocaleController.formatJoined(r3)
                goto L_0x079e
            L_0x079d:
                r3 = 0
            L_0x079e:
                int r9 = r9 - r8
                if (r2 == r9) goto L_0x07a4
                r2 = 0
                r7 = 1
                goto L_0x07a5
            L_0x07a4:
                r2 = 0
            L_0x07a5:
                r1.setData(r6, r2, r3, r7)
            L_0x07a8:
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
        $$Lambda$ChatUsersActivity$hcXc6gzWLyRGy4CysvKxgmSZwbA r11 = new ThemeDescription.ThemeDescriptionDelegate() {
            public final void didSetColor() {
                ChatUsersActivity.this.lambda$getThemeDescriptions$17$ChatUsersActivity();
            }
        };
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
        $$Lambda$ChatUsersActivity$hcXc6gzWLyRGy4CysvKxgmSZwbA r9 = r11;
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) r9, "windowBackgroundWhiteGrayText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) r9, "windowBackgroundWhiteBlueText"));
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
        $$Lambda$ChatUsersActivity$hcXc6gzWLyRGy4CysvKxgmSZwbA r8 = r11;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundRed"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundOrange"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundViolet"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundGreen"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundCyan"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundBlue"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundPink"));
        return arrayList;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$getThemeDescriptions$17 */
    public /* synthetic */ void lambda$getThemeDescriptions$17$ChatUsersActivity() {
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
