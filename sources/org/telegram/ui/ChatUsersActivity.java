package org.telegram.ui;

import android.animation.Animator;
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
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
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
    private SparseArray<TLObject> contactsMap = new SparseArray<>();
    /* access modifiers changed from: private */
    public int contactsStartRow;
    /* access modifiers changed from: private */
    public TLRPC$Chat currentChat;
    /* access modifiers changed from: private */
    public TLRPC$TL_chatBannedRights defaultBannedRights = new TLRPC$TL_chatBannedRights();
    private int delayResults;
    private ChatUsersActivityDelegate delegate;
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
    private SparseArray<TLRPC$TL_groupCallParticipant> ignoredUsers;
    /* access modifiers changed from: private */
    public TLRPC$ChatFull info;
    private String initialBannedRights;
    private int initialSlowmode;
    /* access modifiers changed from: private */
    public boolean isChannel;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    /* access modifiers changed from: private */
    public ListAdapter listViewAdapter;
    /* access modifiers changed from: private */
    public int loadingProgressRow;
    /* access modifiers changed from: private */
    public boolean loadingUsers;
    /* access modifiers changed from: private */
    public int membersHeaderRow;
    private boolean needOpenSearch = this.arguments.getBoolean("open_search");
    private boolean openTransitionEnded;
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
            public static void $default$didAddParticipantToList(ChatUsersActivityDelegate chatUsersActivityDelegate, int i, TLObject tLObject) {
            }

            public static void $default$didChangeOwner(ChatUsersActivityDelegate chatUsersActivityDelegate, TLRPC$User tLRPC$User) {
            }

            public static void $default$didSelectUser(ChatUsersActivityDelegate chatUsersActivityDelegate, int i) {
            }
        }

        void didAddParticipantToList(int i, TLObject tLObject);

        void didChangeOwner(TLRPC$User tLRPC$User);

        void didSelectUser(int i);
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
            String str;
            String str2;
            this.textPaint.setColor(Theme.getColor(ChatUsersActivity.this.selectType == 4 ? "voipgroup_listViewBackground" : "windowBackgroundWhiteGrayText"));
            int measuredHeight = (getMeasuredHeight() / 2) + AndroidUtilities.dp(11.0f);
            int i = 0;
            while (i < this.strings.size()) {
                int i2 = this.sideSide;
                int i3 = this.lineSize + (this.gapSize * 2);
                int i4 = this.circleSize;
                int i5 = i2 + ((i3 + i4) * i) + (i4 / 2);
                if (i <= ChatUsersActivity.this.selectedSlowmode) {
                    Paint paint2 = this.paint;
                    if (ChatUsersActivity.this.selectType == 4) {
                        str2 = "voipgroup_listViewBackground";
                    } else {
                        str2 = "switchTrackChecked";
                    }
                    paint2.setColor(Theme.getColor(str2));
                } else {
                    Paint paint3 = this.paint;
                    if (ChatUsersActivity.this.selectType == 4) {
                        str = "voipgroup_listViewBackground";
                    } else {
                        str = "switchTrack";
                    }
                    paint3.setColor(Theme.getColor(str));
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
                String str3 = this.strings.get(i);
                if (i == 0) {
                    canvas.drawText(str3, (float) AndroidUtilities.dp(22.0f), (float) AndroidUtilities.dp(28.0f), this.textPaint);
                } else if (i == this.strings.size() - 1) {
                    canvas.drawText(str3, (float) ((getMeasuredWidth() - intValue) - AndroidUtilities.dp(22.0f)), (float) AndroidUtilities.dp(28.0f), this.textPaint);
                } else {
                    canvas.drawText(str3, (float) (i5 - (intValue / 2)), (float) AndroidUtilities.dp(28.0f), this.textPaint);
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

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x00a9, code lost:
        if (org.telegram.messenger.ChatObject.canBlockUsers(r0) != false) goto L_0x00ab;
     */
    /* JADX WARNING: Removed duplicated region for block: B:116:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x00cd  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x00f9  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x010b  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateRows() {
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
            r2 = 0
            r6.rowCount = r2
            int r3 = r6.type
            r4 = 3
            r5 = 1
            if (r3 != r4) goto L_0x0134
            r3 = 0
            int r3 = r3 + r5
            r6.rowCount = r3
            r6.permissionsSectionRow = r2
            int r2 = r3 + 1
            r6.rowCount = r2
            r6.sendMessagesRow = r3
            int r3 = r2 + 1
            r6.rowCount = r3
            r6.sendMediaRow = r2
            int r2 = r3 + 1
            r6.rowCount = r2
            r6.sendStickersRow = r3
            int r3 = r2 + 1
            r6.rowCount = r3
            r6.sendPollsRow = r2
            int r2 = r3 + 1
            r6.rowCount = r2
            r6.embedLinksRow = r3
            int r3 = r2 + 1
            r6.rowCount = r3
            r6.addUsersRow = r2
            int r2 = r3 + 1
            r6.rowCount = r2
            r6.pinMessagesRow = r3
            int r3 = r2 + 1
            r6.rowCount = r3
            r6.changeInfoRow = r2
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r0 != 0) goto L_0x009f
            org.telegram.tgnet.TLRPC$Chat r0 = r6.currentChat
            boolean r0 = r0.creator
            if (r0 != 0) goto L_0x00ab
        L_0x009f:
            org.telegram.tgnet.TLRPC$Chat r0 = r6.currentChat
            boolean r2 = r0.megagroup
            if (r2 == 0) goto L_0x00c5
            boolean r0 = org.telegram.messenger.ChatObject.canBlockUsers(r0)
            if (r0 == 0) goto L_0x00c5
        L_0x00ab:
            int r0 = r6.rowCount
            int r2 = r0 + 1
            r6.rowCount = r2
            r6.participantsDivider2Row = r0
            int r0 = r2 + 1
            r6.rowCount = r0
            r6.slowmodeRow = r2
            int r2 = r0 + 1
            r6.rowCount = r2
            r6.slowmodeSelectRow = r0
            int r0 = r2 + 1
            r6.rowCount = r0
            r6.slowmodeInfoRow = r2
        L_0x00c5:
            org.telegram.tgnet.TLRPC$Chat r0 = r6.currentChat
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r0 == 0) goto L_0x00e1
            int r0 = r6.participantsDivider2Row
            if (r0 != r1) goto L_0x00d9
            int r0 = r6.rowCount
            int r2 = r0 + 1
            r6.rowCount = r2
            r6.participantsDivider2Row = r0
        L_0x00d9:
            int r0 = r6.rowCount
            int r2 = r0 + 1
            r6.rowCount = r2
            r6.removedUsersRow = r0
        L_0x00e1:
            int r0 = r6.slowmodeInfoRow
            if (r0 == r1) goto L_0x00e9
            int r0 = r6.removedUsersRow
            if (r0 == r1) goto L_0x00f1
        L_0x00e9:
            int r0 = r6.rowCount
            int r2 = r0 + 1
            r6.rowCount = r2
            r6.participantsDividerRow = r0
        L_0x00f1:
            org.telegram.tgnet.TLRPC$Chat r0 = r6.currentChat
            boolean r0 = org.telegram.messenger.ChatObject.canBlockUsers(r0)
            if (r0 == 0) goto L_0x0101
            int r0 = r6.rowCount
            int r2 = r0 + 1
            r6.rowCount = r2
            r6.addNewRow = r0
        L_0x0101:
            boolean r0 = r6.loadingUsers
            if (r0 == 0) goto L_0x010b
            boolean r0 = r6.firstLoaded
            if (r0 != 0) goto L_0x010b
            goto L_0x0288
        L_0x010b:
            java.util.ArrayList<org.telegram.tgnet.TLObject> r0 = r6.participants
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x0122
            int r0 = r6.rowCount
            r6.participantsStartRow = r0
            java.util.ArrayList<org.telegram.tgnet.TLObject> r2 = r6.participants
            int r2 = r2.size()
            int r0 = r0 + r2
            r6.rowCount = r0
            r6.participantsEndRow = r0
        L_0x0122:
            int r0 = r6.addNewRow
            if (r0 != r1) goto L_0x012a
            int r0 = r6.participantsStartRow
            if (r0 == r1) goto L_0x0288
        L_0x012a:
            int r0 = r6.rowCount
            int r1 = r0 + 1
            r6.rowCount = r1
            r6.addNewSectionRow = r0
            goto L_0x0288
        L_0x0134:
            if (r3 != 0) goto L_0x01a1
            boolean r0 = org.telegram.messenger.ChatObject.canBlockUsers(r0)
            if (r0 == 0) goto L_0x0154
            int r0 = r6.rowCount
            int r2 = r0 + 1
            r6.rowCount = r2
            r6.addNewRow = r0
            java.util.ArrayList<org.telegram.tgnet.TLObject> r0 = r6.participants
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x0154
            int r0 = r6.rowCount
            int r2 = r0 + 1
            r6.rowCount = r2
            r6.participantsInfoRow = r0
        L_0x0154:
            boolean r0 = r6.loadingUsers
            if (r0 == 0) goto L_0x015e
            boolean r0 = r6.firstLoaded
            if (r0 != 0) goto L_0x015e
            goto L_0x0288
        L_0x015e:
            java.util.ArrayList<org.telegram.tgnet.TLObject> r0 = r6.participants
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x017b
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
        L_0x017b:
            int r0 = r6.participantsStartRow
            if (r0 == r1) goto L_0x0197
            int r0 = r6.participantsInfoRow
            if (r0 != r1) goto L_0x018d
            int r0 = r6.rowCount
            int r1 = r0 + 1
            r6.rowCount = r1
            r6.participantsInfoRow = r0
            goto L_0x0288
        L_0x018d:
            int r0 = r6.rowCount
            int r1 = r0 + 1
            r6.rowCount = r1
            r6.addNewSectionRow = r0
            goto L_0x0288
        L_0x0197:
            int r0 = r6.rowCount
            int r1 = r0 + 1
            r6.rowCount = r1
            r6.blockedEmptyRow = r0
            goto L_0x0288
        L_0x01a1:
            if (r3 != r5) goto L_0x0202
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r0 == 0) goto L_0x01c7
            org.telegram.tgnet.TLRPC$Chat r0 = r6.currentChat
            boolean r0 = r0.megagroup
            if (r0 == 0) goto L_0x01c7
            org.telegram.tgnet.TLRPC$ChatFull r0 = r6.info
            if (r0 == 0) goto L_0x01b9
            int r0 = r0.participants_count
            r1 = 200(0xc8, float:2.8E-43)
            if (r0 > r1) goto L_0x01c7
        L_0x01b9:
            int r0 = r6.rowCount
            int r1 = r0 + 1
            r6.rowCount = r1
            r6.recentActionsRow = r0
            int r0 = r1 + 1
            r6.rowCount = r0
            r6.addNewSectionRow = r1
        L_0x01c7:
            org.telegram.tgnet.TLRPC$Chat r0 = r6.currentChat
            boolean r0 = org.telegram.messenger.ChatObject.canAddAdmins(r0)
            if (r0 == 0) goto L_0x01d7
            int r0 = r6.rowCount
            int r1 = r0 + 1
            r6.rowCount = r1
            r6.addNewRow = r0
        L_0x01d7:
            boolean r0 = r6.loadingUsers
            if (r0 == 0) goto L_0x01e1
            boolean r0 = r6.firstLoaded
            if (r0 != 0) goto L_0x01e1
            goto L_0x0288
        L_0x01e1:
            java.util.ArrayList<org.telegram.tgnet.TLObject> r0 = r6.participants
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x01f8
            int r0 = r6.rowCount
            r6.participantsStartRow = r0
            java.util.ArrayList<org.telegram.tgnet.TLObject> r1 = r6.participants
            int r1 = r1.size()
            int r0 = r0 + r1
            r6.rowCount = r0
            r6.participantsEndRow = r0
        L_0x01f8:
            int r0 = r6.rowCount
            int r1 = r0 + 1
            r6.rowCount = r1
            r6.participantsInfoRow = r0
            goto L_0x0288
        L_0x0202:
            r1 = 2
            if (r3 != r1) goto L_0x0288
            int r1 = r6.selectType
            if (r1 != 0) goto L_0x0217
            boolean r0 = org.telegram.messenger.ChatObject.canAddUsers(r0)
            if (r0 == 0) goto L_0x0217
            int r0 = r6.rowCount
            int r1 = r0 + 1
            r6.rowCount = r1
            r6.addNewRow = r0
        L_0x0217:
            boolean r0 = r6.loadingUsers
            if (r0 == 0) goto L_0x0220
            boolean r0 = r6.firstLoaded
            if (r0 != 0) goto L_0x0220
            goto L_0x0288
        L_0x0220:
            java.util.ArrayList<org.telegram.tgnet.TLObject> r0 = r6.contacts
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x023e
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
        L_0x023e:
            java.util.ArrayList<org.telegram.tgnet.TLObject> r0 = r6.bots
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x025c
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
            goto L_0x025d
        L_0x025c:
            r5 = r2
        L_0x025d:
            java.util.ArrayList<org.telegram.tgnet.TLObject> r0 = r6.participants
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x027e
            if (r5 == 0) goto L_0x026f
            int r0 = r6.rowCount
            int r1 = r0 + 1
            r6.rowCount = r1
            r6.membersHeaderRow = r0
        L_0x026f:
            int r0 = r6.rowCount
            r6.participantsStartRow = r0
            java.util.ArrayList<org.telegram.tgnet.TLObject> r1 = r6.participants
            int r1 = r1.size()
            int r0 = r0 + r1
            r6.rowCount = r0
            r6.participantsEndRow = r0
        L_0x027e:
            int r0 = r6.rowCount
            if (r0 == 0) goto L_0x0288
            int r1 = r0 + 1
            r6.rowCount = r1
            r6.participantsInfoRow = r0
        L_0x0288:
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
        this.actionBar.setAllowOverlayTitle(true);
        int i2 = this.type;
        int i3 = 2;
        if (i2 == 3) {
            this.actionBar.setTitle(LocaleController.getString("ChannelPermissions", NUM));
        } else if (i2 == 0) {
            this.actionBar.setTitle(LocaleController.getString("ChannelBlacklist", NUM));
        } else if (i2 == 1) {
            this.actionBar.setTitle(LocaleController.getString("ChannelAdministrators", NUM));
        } else if (i2 == 2) {
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
            } else if (i4 == 4) {
                this.actionBar.setTitle(LocaleController.getString("AddMember", NUM));
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
        FrameLayout frameLayout = new FrameLayout(context);
        this.fragmentView = frameLayout;
        if (this.selectType == 4) {
            this.actionBar.setBackgroundColor(Theme.getColor("voipgroup_listViewBackground"));
            this.actionBar.setItemsColor(Theme.getColor("voipgroup_actionBarItems"), false);
            this.actionBar.setItemsBackgroundColor(Theme.getColor("voipgroup_actionBarItemsSelector"), false);
            this.actionBar.setTitleColor(Theme.getColor("voipgroup_actionBarItems"));
            this.actionBar.setSearchTextColor(Theme.getColor("voipgroup_actionBarItems"), true);
            this.actionBar.setSearchTextColor(Theme.getColor("voipgroup_actionBarItems"), false);
            this.fragmentView.setBackgroundColor(Theme.getColor("voipgroup_actionBar"));
        } else {
            frameLayout.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        }
        FrameLayout frameLayout2 = (FrameLayout) this.fragmentView;
        FrameLayout frameLayout3 = new FrameLayout(context);
        FlickerLoadingView flickerLoadingView2 = new FlickerLoadingView(context);
        this.flickerLoadingView = flickerLoadingView2;
        flickerLoadingView2.setViewType(6);
        this.flickerLoadingView.showDate(false);
        this.flickerLoadingView.setUseHeaderOffset(true);
        if (this.selectType == 4) {
            this.flickerLoadingView.setColors("voipgroup_actionBar", "voipgroup_emptyView");
        }
        frameLayout3.addView(this.flickerLoadingView);
        RadialProgressView radialProgressView = new RadialProgressView(context);
        this.progressBar = radialProgressView;
        frameLayout3.addView(radialProgressView, LayoutHelper.createFrame(-2, -2, 17));
        frameLayout2.addView(frameLayout3);
        if (this.type == 3) {
            this.flickerLoadingView.setVisibility(8);
        } else {
            this.progressBar.setVisibility(8);
        }
        StickerEmptyView stickerEmptyView = new StickerEmptyView(context, frameLayout3, 1);
        this.emptyView = stickerEmptyView;
        stickerEmptyView.title.setText(LocaleController.getString("NoResult", NUM));
        this.emptyView.subtitle.setText(LocaleController.getString("SearchEmptyViewFilteredSubtitle2", NUM));
        this.emptyView.setVisibility(8);
        this.emptyView.setAnimateLayoutChange(true);
        this.emptyView.showProgress(true, false);
        frameLayout2.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        ((SimpleItemAnimator) this.listView.getItemAnimator()).setSupportsChangeAnimations(false);
        RecyclerListView recyclerListView2 = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.listViewAdapter = listAdapter;
        recyclerListView2.setAdapter(listAdapter);
        RecyclerListView recyclerListView3 = this.listView;
        if (LocaleController.isRTL) {
            i3 = 1;
        }
        recyclerListView3.setVerticalScrollbarPosition(i3);
        if (this.selectType == 4) {
            this.listView.setGlowColor(Theme.getColor("voipgroup_actionBar"));
        }
        frameLayout2.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
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
        frameLayout2.addView(undoView2, LayoutHelper.createFrame(-1, -2.0f, 83, 8.0f, 0.0f, 8.0f, 8.0f));
        updateRows();
        this.listView.setEmptyView(this.emptyView);
        this.listView.setAnimateEmptyView(true, 0);
        if (this.needOpenSearch) {
            this.searchItem.openSearch(false);
        }
        return this.fragmentView;
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:198:0x0390  */
    /* JADX WARNING: Removed duplicated region for block: B:256:0x04c5 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:257:0x04c6  */
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
            r5 = 3
            r6 = 2
            if (r1 == 0) goto L_0x028e
            int r7 = r9.addNewRow
            java.lang.String r8 = "type"
            java.lang.String r10 = "chat_id"
            if (r0 != r7) goto L_0x00b7
            int r0 = r9.type
            java.lang.String r1 = "selectType"
            if (r0 == 0) goto L_0x0094
            if (r0 != r5) goto L_0x0028
            goto L_0x0094
        L_0x0028:
            if (r0 != r4) goto L_0x0050
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            int r2 = r9.chatId
            r0.putInt(r10, r2)
            r0.putInt(r8, r6)
            r0.putInt(r1, r4)
            org.telegram.ui.ChatUsersActivity r1 = new org.telegram.ui.ChatUsersActivity
            r1.<init>(r0)
            org.telegram.ui.ChatUsersActivity$3 r0 = new org.telegram.ui.ChatUsersActivity$3
            r0.<init>()
            r1.setDelegate(r0)
            org.telegram.tgnet.TLRPC$ChatFull r0 = r9.info
            r1.setInfo(r0)
            r9.presentFragment(r1)
            goto L_0x00b6
        L_0x0050:
            if (r0 != r6) goto L_0x00b6
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
            org.telegram.ui.ChatUsersActivity$4 r0 = new org.telegram.ui.ChatUsersActivity$4
            r0.<init>()
            r1.setDelegate((org.telegram.ui.GroupCreateActivity.ContactsAddActivityDelegate) r0)
            r9.presentFragment(r1)
            goto L_0x00b6
        L_0x0094:
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            int r2 = r9.chatId
            r0.putInt(r10, r2)
            r0.putInt(r8, r6)
            int r2 = r9.type
            if (r2 != 0) goto L_0x00a6
            r5 = 2
        L_0x00a6:
            r0.putInt(r1, r5)
            org.telegram.ui.ChatUsersActivity r1 = new org.telegram.ui.ChatUsersActivity
            r1.<init>(r0)
            org.telegram.tgnet.TLRPC$ChatFull r0 = r9.info
            r1.setInfo(r0)
            r9.presentFragment(r1)
        L_0x00b6:
            return
        L_0x00b7:
            int r7 = r9.recentActionsRow
            if (r0 != r7) goto L_0x00c6
            org.telegram.ui.ChannelAdminLogActivity r0 = new org.telegram.ui.ChannelAdminLogActivity
            org.telegram.tgnet.TLRPC$Chat r1 = r9.currentChat
            r0.<init>(r1)
            r9.presentFragment(r0)
            return
        L_0x00c6:
            int r7 = r9.removedUsersRow
            if (r0 != r7) goto L_0x00e5
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            int r1 = r9.chatId
            r0.putInt(r10, r1)
            r0.putInt(r8, r3)
            org.telegram.ui.ChatUsersActivity r1 = new org.telegram.ui.ChatUsersActivity
            r1.<init>(r0)
            org.telegram.tgnet.TLRPC$ChatFull r0 = r9.info
            r1.setInfo(r0)
            r9.presentFragment(r1)
            return
        L_0x00e5:
            int r7 = r9.addNew2Row
            if (r0 != r7) goto L_0x00f4
            org.telegram.ui.GroupInviteActivity r0 = new org.telegram.ui.GroupInviteActivity
            int r1 = r9.chatId
            r0.<init>(r1)
            r9.presentFragment(r0)
            return
        L_0x00f4:
            int r7 = r9.permissionsSectionRow
            if (r0 <= r7) goto L_0x028e
            int r7 = r9.changeInfoRow
            if (r0 > r7) goto L_0x028e
            r1 = r22
            org.telegram.ui.Cells.TextCheckCell2 r1 = (org.telegram.ui.Cells.TextCheckCell2) r1
            boolean r2 = r1.isEnabled()
            if (r2 != 0) goto L_0x0107
            return
        L_0x0107:
            boolean r2 = r1.hasIcon()
            if (r2 == 0) goto L_0x0149
            org.telegram.tgnet.TLRPC$Chat r1 = r9.currentChat
            java.lang.String r1 = r1.username
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0134
            int r1 = r9.pinMessagesRow
            if (r0 == r1) goto L_0x011f
            int r1 = r9.changeInfoRow
            if (r0 != r1) goto L_0x0134
        L_0x011f:
            android.app.Activity r0 = r21.getParentActivity()
            r1 = 2131625159(0x7f0e04c7, float:1.8877518E38)
            java.lang.String r2 = "EditCantEditPermissionsPublic"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            android.widget.Toast r0 = android.widget.Toast.makeText(r0, r1, r3)
            r0.show()
            goto L_0x0148
        L_0x0134:
            android.app.Activity r0 = r21.getParentActivity()
            r1 = 2131625158(0x7f0e04c6, float:1.8877516E38)
            java.lang.String r2 = "EditCantEditPermissions"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            android.widget.Toast r0 = android.widget.Toast.makeText(r0, r1, r3)
            r0.show()
        L_0x0148:
            return
        L_0x0149:
            boolean r2 = r1.isChecked()
            r2 = r2 ^ r4
            r1.setChecked(r2)
            int r2 = r9.changeInfoRow
            if (r0 != r2) goto L_0x015e
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r0 = r9.defaultBannedRights
            boolean r1 = r0.change_info
            r1 = r1 ^ r4
            r0.change_info = r1
            goto L_0x028d
        L_0x015e:
            int r2 = r9.addUsersRow
            if (r0 != r2) goto L_0x016b
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r0 = r9.defaultBannedRights
            boolean r1 = r0.invite_users
            r1 = r1 ^ r4
            r0.invite_users = r1
            goto L_0x028d
        L_0x016b:
            int r2 = r9.pinMessagesRow
            if (r0 != r2) goto L_0x0178
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r0 = r9.defaultBannedRights
            boolean r1 = r0.pin_messages
            r1 = r1 ^ r4
            r0.pin_messages = r1
            goto L_0x028d
        L_0x0178:
            boolean r1 = r1.isChecked()
            r1 = r1 ^ r4
            int r2 = r9.sendMessagesRow
            if (r0 != r2) goto L_0x0189
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r0 = r9.defaultBannedRights
            boolean r5 = r0.send_messages
            r5 = r5 ^ r4
            r0.send_messages = r5
            goto L_0x01be
        L_0x0189:
            int r5 = r9.sendMediaRow
            if (r0 != r5) goto L_0x0195
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r0 = r9.defaultBannedRights
            boolean r5 = r0.send_media
            r5 = r5 ^ r4
            r0.send_media = r5
            goto L_0x01be
        L_0x0195:
            int r5 = r9.sendStickersRow
            if (r0 != r5) goto L_0x01a7
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r0 = r9.defaultBannedRights
            boolean r5 = r0.send_stickers
            r5 = r5 ^ r4
            r0.send_inline = r5
            r0.send_gifs = r5
            r0.send_games = r5
            r0.send_stickers = r5
            goto L_0x01be
        L_0x01a7:
            int r5 = r9.embedLinksRow
            if (r0 != r5) goto L_0x01b3
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r0 = r9.defaultBannedRights
            boolean r5 = r0.embed_links
            r5 = r5 ^ r4
            r0.embed_links = r5
            goto L_0x01be
        L_0x01b3:
            int r5 = r9.sendPollsRow
            if (r0 != r5) goto L_0x01be
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r0 = r9.defaultBannedRights
            boolean r5 = r0.send_polls
            r5 = r5 ^ r4
            r0.send_polls = r5
        L_0x01be:
            if (r1 == 0) goto L_0x0266
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r0 = r9.defaultBannedRights
            boolean r1 = r0.view_messages
            if (r1 == 0) goto L_0x01db
            boolean r1 = r0.send_messages
            if (r1 != 0) goto L_0x01db
            r0.send_messages = r4
            org.telegram.ui.Components.RecyclerListView r0 = r9.listView
            androidx.recyclerview.widget.RecyclerView$ViewHolder r0 = r0.findViewHolderForAdapterPosition(r2)
            if (r0 == 0) goto L_0x01db
            android.view.View r0 = r0.itemView
            org.telegram.ui.Cells.TextCheckCell2 r0 = (org.telegram.ui.Cells.TextCheckCell2) r0
            r0.setChecked(r3)
        L_0x01db:
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r0 = r9.defaultBannedRights
            boolean r1 = r0.view_messages
            if (r1 != 0) goto L_0x01e5
            boolean r1 = r0.send_messages
            if (r1 == 0) goto L_0x01fc
        L_0x01e5:
            boolean r1 = r0.send_media
            if (r1 != 0) goto L_0x01fc
            r0.send_media = r4
            org.telegram.ui.Components.RecyclerListView r0 = r9.listView
            int r1 = r9.sendMediaRow
            androidx.recyclerview.widget.RecyclerView$ViewHolder r0 = r0.findViewHolderForAdapterPosition(r1)
            if (r0 == 0) goto L_0x01fc
            android.view.View r0 = r0.itemView
            org.telegram.ui.Cells.TextCheckCell2 r0 = (org.telegram.ui.Cells.TextCheckCell2) r0
            r0.setChecked(r3)
        L_0x01fc:
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r0 = r9.defaultBannedRights
            boolean r1 = r0.view_messages
            if (r1 != 0) goto L_0x0206
            boolean r1 = r0.send_messages
            if (r1 == 0) goto L_0x021d
        L_0x0206:
            boolean r1 = r0.send_polls
            if (r1 != 0) goto L_0x021d
            r0.send_polls = r4
            org.telegram.ui.Components.RecyclerListView r0 = r9.listView
            int r1 = r9.sendPollsRow
            androidx.recyclerview.widget.RecyclerView$ViewHolder r0 = r0.findViewHolderForAdapterPosition(r1)
            if (r0 == 0) goto L_0x021d
            android.view.View r0 = r0.itemView
            org.telegram.ui.Cells.TextCheckCell2 r0 = (org.telegram.ui.Cells.TextCheckCell2) r0
            r0.setChecked(r3)
        L_0x021d:
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r0 = r9.defaultBannedRights
            boolean r1 = r0.view_messages
            if (r1 != 0) goto L_0x0227
            boolean r1 = r0.send_messages
            if (r1 == 0) goto L_0x0244
        L_0x0227:
            boolean r1 = r0.send_stickers
            if (r1 != 0) goto L_0x0244
            r0.send_inline = r4
            r0.send_gifs = r4
            r0.send_games = r4
            r0.send_stickers = r4
            org.telegram.ui.Components.RecyclerListView r0 = r9.listView
            int r1 = r9.sendStickersRow
            androidx.recyclerview.widget.RecyclerView$ViewHolder r0 = r0.findViewHolderForAdapterPosition(r1)
            if (r0 == 0) goto L_0x0244
            android.view.View r0 = r0.itemView
            org.telegram.ui.Cells.TextCheckCell2 r0 = (org.telegram.ui.Cells.TextCheckCell2) r0
            r0.setChecked(r3)
        L_0x0244:
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r0 = r9.defaultBannedRights
            boolean r1 = r0.view_messages
            if (r1 != 0) goto L_0x024e
            boolean r1 = r0.send_messages
            if (r1 == 0) goto L_0x028d
        L_0x024e:
            boolean r1 = r0.embed_links
            if (r1 != 0) goto L_0x028d
            r0.embed_links = r4
            org.telegram.ui.Components.RecyclerListView r0 = r9.listView
            int r1 = r9.embedLinksRow
            androidx.recyclerview.widget.RecyclerView$ViewHolder r0 = r0.findViewHolderForAdapterPosition(r1)
            if (r0 == 0) goto L_0x028d
            android.view.View r0 = r0.itemView
            org.telegram.ui.Cells.TextCheckCell2 r0 = (org.telegram.ui.Cells.TextCheckCell2) r0
            r0.setChecked(r3)
            goto L_0x028d
        L_0x0266:
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r0 = r9.defaultBannedRights
            boolean r1 = r0.embed_links
            if (r1 == 0) goto L_0x0278
            boolean r1 = r0.send_inline
            if (r1 == 0) goto L_0x0278
            boolean r1 = r0.send_media
            if (r1 == 0) goto L_0x0278
            boolean r1 = r0.send_polls
            if (r1 != 0) goto L_0x028d
        L_0x0278:
            boolean r1 = r0.send_messages
            if (r1 == 0) goto L_0x028d
            r0.send_messages = r3
            org.telegram.ui.Components.RecyclerListView r0 = r9.listView
            androidx.recyclerview.widget.RecyclerView$ViewHolder r0 = r0.findViewHolderForAdapterPosition(r2)
            if (r0 == 0) goto L_0x028d
            android.view.View r0 = r0.itemView
            org.telegram.ui.Cells.TextCheckCell2 r0 = (org.telegram.ui.Cells.TextCheckCell2) r0
            r0.setChecked(r4)
        L_0x028d:
            return
        L_0x028e:
            r8 = 0
            java.lang.String r7 = ""
            if (r1 == 0) goto L_0x0322
            org.telegram.tgnet.TLObject r0 = r2.getItem(r0)
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$ChannelParticipant
            if (r1 == 0) goto L_0x02e2
            r1 = r0
            org.telegram.tgnet.TLRPC$ChannelParticipant r1 = (org.telegram.tgnet.TLRPC$ChannelParticipant) r1
            int r2 = r1.user_id
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r7 = r1.banned_rights
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r10 = r1.admin_rights
            java.lang.String r11 = r1.rank
            boolean r12 = r1 instanceof org.telegram.tgnet.TLRPC$TL_channelParticipantAdmin
            if (r12 != 0) goto L_0x02ae
            boolean r12 = r1 instanceof org.telegram.tgnet.TLRPC$TL_channelParticipantCreator
            if (r12 == 0) goto L_0x02b2
        L_0x02ae:
            boolean r1 = r1.can_edit
            if (r1 == 0) goto L_0x02b4
        L_0x02b2:
            r1 = 1
            goto L_0x02b5
        L_0x02b4:
            r1 = 0
        L_0x02b5:
            boolean r12 = r0 instanceof org.telegram.tgnet.TLRPC$TL_channelParticipantCreator
            if (r12 == 0) goto L_0x02db
            r10 = r0
            org.telegram.tgnet.TLRPC$TL_channelParticipantCreator r10 = (org.telegram.tgnet.TLRPC$TL_channelParticipantCreator) r10
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r10 = r10.admin_rights
            if (r10 != 0) goto L_0x02db
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
            if (r12 != 0) goto L_0x02db
            r10.manage_call = r4
        L_0x02db:
            r12 = r1
            r13 = r10
            r16 = r11
            r11 = r2
            r10 = r7
            goto L_0x031f
        L_0x02e2:
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$ChatParticipant
            if (r1 == 0) goto L_0x0319
            r1 = r0
            org.telegram.tgnet.TLRPC$ChatParticipant r1 = (org.telegram.tgnet.TLRPC$ChatParticipant) r1
            int r1 = r1.user_id
            org.telegram.tgnet.TLRPC$Chat r2 = r9.currentChat
            boolean r2 = r2.creator
            boolean r10 = r0 instanceof org.telegram.tgnet.TLRPC$TL_chatParticipantCreator
            if (r10 == 0) goto L_0x030f
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
            if (r11 != 0) goto L_0x0310
            r10.manage_call = r4
            goto L_0x0310
        L_0x030f:
            r10 = r8
        L_0x0310:
            r11 = r1
            r12 = r2
            r16 = r7
            r13 = r10
            r7 = r0
            r10 = r8
            goto L_0x038e
        L_0x0319:
            r16 = r7
            r10 = r8
            r13 = r10
            r11 = 0
        L_0x031e:
            r12 = 0
        L_0x031f:
            r7 = r0
            goto L_0x038e
        L_0x0322:
            org.telegram.ui.ChatUsersActivity$SearchAdapter r1 = r9.searchListViewAdapter
            org.telegram.tgnet.TLObject r0 = r1.getItem(r0)
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$User
            if (r1 == 0) goto L_0x0341
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            org.telegram.messenger.MessagesController r1 = r21.getMessagesController()
            r1.putUser(r0, r3)
            int r0 = r0.id
            org.telegram.tgnet.TLObject r1 = r9.getAnyParticipant(r0)
            r20 = r1
            r1 = r0
            r0 = r20
            goto L_0x034c
        L_0x0341:
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$ChannelParticipant
            if (r1 != 0) goto L_0x034b
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$ChatParticipant
            if (r1 == 0) goto L_0x034a
            goto L_0x034b
        L_0x034a:
            r0 = r8
        L_0x034b:
            r1 = 0
        L_0x034c:
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$ChannelParticipant
            if (r2 == 0) goto L_0x0371
            r1 = r0
            org.telegram.tgnet.TLRPC$ChannelParticipant r1 = (org.telegram.tgnet.TLRPC$ChannelParticipant) r1
            int r2 = r1.user_id
            boolean r7 = r1 instanceof org.telegram.tgnet.TLRPC$TL_channelParticipantAdmin
            if (r7 != 0) goto L_0x035d
            boolean r7 = r1 instanceof org.telegram.tgnet.TLRPC$TL_channelParticipantCreator
            if (r7 == 0) goto L_0x0361
        L_0x035d:
            boolean r7 = r1.can_edit
            if (r7 == 0) goto L_0x0363
        L_0x0361:
            r7 = 1
            goto L_0x0364
        L_0x0363:
            r7 = 0
        L_0x0364:
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r10 = r1.banned_rights
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r11 = r1.admin_rights
            java.lang.String r1 = r1.rank
            r16 = r1
            r12 = r7
            r13 = r11
            r7 = r0
            r11 = r2
            goto L_0x038e
        L_0x0371:
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$ChatParticipant
            if (r2 == 0) goto L_0x0385
            r1 = r0
            org.telegram.tgnet.TLRPC$ChatParticipant r1 = (org.telegram.tgnet.TLRPC$ChatParticipant) r1
            int r1 = r1.user_id
            org.telegram.tgnet.TLRPC$Chat r2 = r9.currentChat
            boolean r2 = r2.creator
            r11 = r1
            r12 = r2
            r16 = r7
            r10 = r8
            r13 = r10
            goto L_0x031f
        L_0x0385:
            r11 = r1
            r16 = r7
            r10 = r8
            r13 = r10
            if (r0 != 0) goto L_0x031e
            r12 = 1
            goto L_0x031f
        L_0x038e:
            if (r11 == 0) goto L_0x04d8
            int r0 = r9.selectType
            if (r0 == 0) goto L_0x0438
            r1 = 4
            if (r0 != r1) goto L_0x03a1
            org.telegram.ui.ChatUsersActivity$ChatUsersActivityDelegate r0 = r9.delegate
            r0.didSelectUser(r11)
            r21.finishFragment()
            goto L_0x04d8
        L_0x03a1:
            if (r0 == r5) goto L_0x03ab
            if (r0 != r4) goto L_0x03a6
            goto L_0x03ab
        L_0x03a6:
            r9.removeUser(r11)
            goto L_0x04d8
        L_0x03ab:
            if (r0 == r4) goto L_0x041b
            if (r12 == 0) goto L_0x041b
            boolean r1 = r7 instanceof org.telegram.tgnet.TLRPC$TL_channelParticipantAdmin
            if (r1 != 0) goto L_0x03b7
            boolean r1 = r7 instanceof org.telegram.tgnet.TLRPC$TL_chatParticipantAdmin
            if (r1 == 0) goto L_0x041b
        L_0x03b7:
            org.telegram.messenger.MessagesController r0 = r21.getMessagesController()
            java.lang.Integer r1 = java.lang.Integer.valueOf(r11)
            org.telegram.tgnet.TLRPC$User r2 = r0.getUser(r1)
            org.telegram.ui.ActionBar.AlertDialog$Builder r11 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r0 = r21.getParentActivity()
            r11.<init>((android.content.Context) r0)
            r0 = 2131624257(0x7f0e0141, float:1.8875689E38)
            java.lang.String r1 = "AppName"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r11.setTitle(r0)
            r0 = 2131624210(0x7f0e0112, float:1.8875593E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            java.lang.String r4 = org.telegram.messenger.UserObject.getUserName(r2)
            r1[r3] = r4
            java.lang.String r3 = "AdminWillBeRemoved"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r3, r0, r1)
            r11.setMessage(r0)
            r0 = 2131626289(0x7f0e0931, float:1.887981E38)
            java.lang.String r1 = "OK"
            java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r1, r0)
            org.telegram.ui.-$$Lambda$ChatUsersActivity$GDkHXwoamCn6kq8XE81r3lcS8vE r15 = new org.telegram.ui.-$$Lambda$ChatUsersActivity$GDkHXwoamCn6kq8XE81r3lcS8vE
            r0 = r15
            r1 = r21
            r3 = r7
            r4 = r13
            r5 = r10
            r6 = r16
            r7 = r12
            r0.<init>(r2, r3, r4, r5, r6, r7)
            r11.setPositiveButton(r14, r15)
            r0 = 2131624583(0x7f0e0287, float:1.887635E38)
            java.lang.String r1 = "Cancel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r11.setNegativeButton(r0, r8)
            org.telegram.ui.ActionBar.AlertDialog r0 = r11.create()
            r9.showDialog(r0)
            goto L_0x04d8
        L_0x041b:
            if (r0 != r4) goto L_0x041f
            r8 = 0
            goto L_0x0420
        L_0x041f:
            r8 = 1
        L_0x0420:
            if (r0 == r4) goto L_0x0427
            if (r0 != r5) goto L_0x0425
            goto L_0x0427
        L_0x0425:
            r14 = 0
            goto L_0x0428
        L_0x0427:
            r14 = 1
        L_0x0428:
            r0 = r21
            r1 = r11
            r2 = r7
            r3 = r13
            r4 = r10
            r5 = r16
            r6 = r12
            r7 = r8
            r8 = r14
            r0.openRightsEdit(r1, r2, r3, r4, r5, r6, r7, r8)
            goto L_0x04d8
        L_0x0438:
            int r0 = r9.type
            if (r0 != r4) goto L_0x0452
            org.telegram.messenger.UserConfig r0 = r21.getUserConfig()
            int r0 = r0.getClientUserId()
            if (r11 == r0) goto L_0x0450
            org.telegram.tgnet.TLRPC$Chat r0 = r9.currentChat
            boolean r0 = r0.creator
            if (r0 != 0) goto L_0x044e
            if (r12 == 0) goto L_0x0450
        L_0x044e:
            r0 = 1
            goto L_0x0460
        L_0x0450:
            r0 = 0
            goto L_0x0460
        L_0x0452:
            if (r0 == 0) goto L_0x045a
            if (r0 != r5) goto L_0x0457
            goto L_0x045a
        L_0x0457:
            r18 = 0
            goto L_0x0462
        L_0x045a:
            org.telegram.tgnet.TLRPC$Chat r0 = r9.currentChat
            boolean r0 = org.telegram.messenger.ChatObject.canBlockUsers(r0)
        L_0x0460:
            r18 = r0
        L_0x0462:
            int r0 = r9.type
            if (r0 == 0) goto L_0x04bb
            if (r0 == r4) goto L_0x046c
            boolean r1 = r9.isChannel
            if (r1 != 0) goto L_0x04bb
        L_0x046c:
            if (r0 != r6) goto L_0x0473
            int r0 = r9.selectType
            if (r0 != 0) goto L_0x0473
            goto L_0x04bb
        L_0x0473:
            if (r10 != 0) goto L_0x0494
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
            goto L_0x0495
        L_0x0494:
            r15 = r10
        L_0x0495:
            org.telegram.ui.ChatRightsEditActivity r0 = new org.telegram.ui.ChatRightsEditActivity
            int r12 = r9.chatId
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r14 = r9.defaultBannedRights
            int r1 = r9.type
            if (r1 != r4) goto L_0x04a2
            r17 = 0
            goto L_0x04a4
        L_0x04a2:
            r17 = 1
        L_0x04a4:
            if (r7 != 0) goto L_0x04a9
            r19 = 1
            goto L_0x04ab
        L_0x04a9:
            r19 = 0
        L_0x04ab:
            r10 = r0
            r10.<init>(r11, r12, r13, r14, r15, r16, r17, r18, r19)
            org.telegram.ui.ChatUsersActivity$5 r1 = new org.telegram.ui.ChatUsersActivity$5
            r1.<init>(r7)
            r0.setDelegate(r1)
            r9.presentFragment(r0)
            goto L_0x04d8
        L_0x04bb:
            org.telegram.messenger.UserConfig r0 = r21.getUserConfig()
            int r0 = r0.getClientUserId()
            if (r11 != r0) goto L_0x04c6
            return
        L_0x04c6:
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.lang.String r1 = "user_id"
            r0.putInt(r1, r11)
            org.telegram.ui.ProfileActivity r1 = new org.telegram.ui.ProfileActivity
            r1.<init>(r0)
            r9.presentFragment(r1)
        L_0x04d8:
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
    public void showItemsAnimated(final int i) {
        if (!this.isPaused && this.openTransitionEnded) {
            this.listView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    ChatUsersActivity.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
                    int childCount = ChatUsersActivity.this.listView.getChildCount();
                    AnimatorSet animatorSet = new AnimatorSet();
                    for (int i = 0; i < childCount; i++) {
                        View childAt = ChatUsersActivity.this.listView.getChildAt(i);
                        if (ChatUsersActivity.this.listView.getChildAdapterPosition(childAt) >= i) {
                            childAt.setAlpha(0.0f);
                            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(childAt, View.ALPHA, new float[]{0.0f, 1.0f});
                            ofFloat.setStartDelay((long) ((int) ((((float) Math.min(ChatUsersActivity.this.listView.getMeasuredHeight(), Math.max(0, childAt.getTop()))) / ((float) ChatUsersActivity.this.listView.getMeasuredHeight())) * 100.0f)));
                            ofFloat.setDuration(200);
                            animatorSet.playTogether(new Animator[]{ofFloat});
                        }
                    }
                    animatorSet.start();
                    return true;
                }
            });
        }
    }

    public void setIgnoresUsers(SparseArray<TLRPC$TL_groupCallParticipant> sparseArray) {
        this.ignoredUsers = sparseArray;
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
                        return ChatUsersActivity.this.lambda$onOwnerChaged$3$ChatUsersActivity((TLObject) obj, (TLObject) obj2);
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
        }
        if (!z2) {
            TLRPC$TL_channelParticipantCreator tLRPC$TL_channelParticipantCreator2 = new TLRPC$TL_channelParticipantCreator();
            int i3 = tLRPC$User.id;
            tLRPC$TL_channelParticipantCreator2.user_id = i3;
            this.participantsMap.put(i3, tLRPC$TL_channelParticipantCreator2);
            this.participants.add(tLRPC$TL_channelParticipantCreator2);
            Collections.sort(this.participants, new Object() {
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
            updateRows();
        }
        this.listViewAdapter.notifyDataSetChanged();
        ChatUsersActivityDelegate chatUsersActivityDelegate = this.delegate;
        if (chatUsersActivityDelegate != null) {
            chatUsersActivityDelegate.didChangeOwner(tLRPC$User);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onOwnerChaged$3 */
    public /* synthetic */ int lambda$onOwnerChaged$3$ChatUsersActivity(TLObject tLObject, TLObject tLObject2) {
        int channelAdminParticipantType = getChannelAdminParticipantType(tLObject);
        int channelAdminParticipantType2 = getChannelAdminParticipantType(tLObject2);
        if (channelAdminParticipantType > channelAdminParticipantType2) {
            return 1;
        }
        return channelAdminParticipantType < channelAdminParticipantType2 ? -1 : 0;
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
        AnonymousClass8 r13 = r0;
        final int i4 = i;
        AnonymousClass8 r0 = new ChatRightsEditActivity(this, i, this.chatId, tLRPC$TL_chatAdminRights, this.defaultBannedRights, tLRPC$TL_chatBannedRights, str, i3, true, false) {
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
    public void openRightsEdit(int i, TLObject tLObject, TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights, TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights, String str, boolean z, int i2, boolean z2) {
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
                z = true;
            }
        }
        if (z) {
            updateRows();
            this.listViewAdapter.notifyDataSetChanged();
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
            r0 = 2131625145(0x7f0e04b9, float:1.887749E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r13, r0)
            goto L_0x00e4
        L_0x00db:
            r0 = 2131627109(0x7f0e0CLASSNAME, float:1.8881473E38)
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
            r13 = 2131624605(0x7f0e029d, float:1.8876394E38)
            java.lang.String r5 = "ChangePermissions"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r13)
            r1.add(r5)
            r5 = 2131165252(0x7var_, float:1.7944716E38)
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r2.add(r5)
            r5 = 1
            java.lang.Integer r13 = java.lang.Integer.valueOf(r5)
            r0.add(r13)
        L_0x0139:
            r13 = 2131625713(0x7f0e06f1, float:1.8878642E38)
            java.lang.String r5 = "KickFromGroup"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r13)
            r1.add(r5)
            goto L_0x0152
        L_0x0146:
            r5 = 2131624696(0x7f0e02f8, float:1.887658E38)
            java.lang.String r13 = "ChannelRemoveUser"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r13, r5)
            r1.add(r5)
        L_0x0152:
            r5 = 2131165253(0x7var_, float:1.7944718E38)
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
            r2 = 2131624643(0x7f0e02c3, float:1.8876472E38)
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
            r3 = 2131624649(0x7f0e02c9, float:1.8876484E38)
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
            r0 = 2131624618(0x7f0e02aa, float:1.887642E38)
            java.lang.String r3 = "ChannelAddToChannel"
            goto L_0x0229
        L_0x0224:
            r0 = 2131624619(0x7f0e02ab, float:1.8876423E38)
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
            r2 = 2131624697(0x7f0e02f9, float:1.8876581E38)
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
            r1 = 2131165253(0x7var_, float:1.7944718E38)
            r2[r5] = r1
            r13 = r0
            goto L_0x0291
        L_0x0278:
            r5 = 0
            r0 = 2
        L_0x027a:
            java.lang.CharSequence[] r1 = new java.lang.CharSequence[r0]
            r7 = 2131625145(0x7f0e04b9, float:1.887749E38)
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
        TLObject tLObject2 = tLObject;
        int i4 = i3;
        if (((Integer) arrayList2.get(i4)).intValue() == 2) {
            getMessagesController().deleteUserFromChat(this.chatId, tLRPC$User, (TLRPC$ChatFull) null);
            removeParticipants(i);
            return;
        }
        TLRPC$User tLRPC$User2 = tLRPC$User;
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
        TLObject tLObject2;
        int i3;
        int i4;
        int i5 = i;
        final TLObject tLObject3 = tLObject;
        int i6 = i2;
        int i7 = this.type;
        if (i7 == 1) {
            if (i6 == 0 && charSequenceArr.length == 2) {
                ChatRightsEditActivity chatRightsEditActivity = new ChatRightsEditActivity(i, this.chatId, tLRPC$TL_chatAdminRights, (TLRPC$TL_chatBannedRights) null, (TLRPC$TL_chatBannedRights) null, str, 0, true, false);
                chatRightsEditActivity.setDelegate(new ChatRightsEditActivity.ChatRightsEditActivityDelegate() {
                    public void didSetRights(int i, TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights, TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights, String str) {
                        TLObject tLObject = tLObject3;
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
                            TLObject tLObject = tLObject3;
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
                    i3 = 1;
                    i4 = i6;
                    tLObject2 = tLObject3;
                    getMessagesController().addUserToChat(this.chatId, getMessagesController().getUser(Integer.valueOf(i)), (TLRPC$ChatFull) null, 0, (String) null, this, (Runnable) null);
                }
                i4 = i6;
                tLObject2 = tLObject3;
                i3 = 1;
            } else {
                i4 = i6;
                tLObject2 = tLObject3;
                i3 = 1;
                if (i4 == 1) {
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
            if ((i4 == 0 && this.type == 0) || i4 == i3) {
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

    /* access modifiers changed from: private */
    public int getChannelAdminParticipantType(TLObject tLObject) {
        if ((tLObject instanceof TLRPC$TL_channelParticipantCreator) || (tLObject instanceof TLRPC$TL_channelParticipantSelf)) {
            return 0;
        }
        return ((tLObject instanceof TLRPC$TL_channelParticipantAdmin) || (tLObject instanceof TLRPC$TL_channelParticipant)) ? 1 : 2;
    }

    /* access modifiers changed from: private */
    public void loadChatParticipants(int i, int i2) {
        if (!this.loadingUsers) {
            boolean z = false;
            this.contactsEndReached = false;
            if (this.selectType != 4) {
                z = true;
            }
            this.botsEndReached = z;
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
                            } else if (this.selectType != 4) {
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
                ChatUsersActivity.this.lambda$loadChatParticipants$17$ChatUsersActivity(this.f$1, tLObject, tLRPC$TL_error);
            }
        }), this.classGuid);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$loadChatParticipants$17 */
    public /* synthetic */ void lambda$loadChatParticipants$17$ChatUsersActivity(TLRPC$TL_channels_getParticipants tLRPC$TL_channels_getParticipants, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
                ChatUsersActivity.this.lambda$null$16$ChatUsersActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x00d7, code lost:
        r4 = r7.ignoredUsers;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:0x00e9, code lost:
        r4 = getMessagesController().getUser(java.lang.Integer.valueOf(r3.user_id));
     */
    /* renamed from: lambda$null$16 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$null$16$ChatUsersActivity(org.telegram.tgnet.TLRPC$TL_error r8, org.telegram.tgnet.TLObject r9, org.telegram.tgnet.TLRPC$TL_channels_getParticipants r10) {
        /*
            r7 = this;
            r7.resumeDelayedFragmentAnimation()
            r0 = 2
            r1 = 0
            r2 = 1
            if (r8 != 0) goto L_0x0154
            org.telegram.tgnet.TLRPC$TL_channels_channelParticipants r9 = (org.telegram.tgnet.TLRPC$TL_channels_channelParticipants) r9
            int r8 = r7.type
            if (r8 != r2) goto L_0x0017
            org.telegram.messenger.MessagesController r8 = r7.getMessagesController()
            int r3 = r7.chatId
            r8.processLoadedAdminsResponse(r3, r9)
        L_0x0017:
            org.telegram.messenger.MessagesController r8 = r7.getMessagesController()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r3 = r9.users
            r8.putUsers(r3, r1)
            org.telegram.messenger.UserConfig r8 = r7.getUserConfig()
            int r8 = r8.getClientUserId()
            int r3 = r7.selectType
            if (r3 == 0) goto L_0x004a
            r3 = 0
        L_0x002d:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChannelParticipant> r4 = r9.participants
            int r4 = r4.size()
            if (r3 >= r4) goto L_0x004a
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChannelParticipant> r4 = r9.participants
            java.lang.Object r4 = r4.get(r3)
            org.telegram.tgnet.TLRPC$ChannelParticipant r4 = (org.telegram.tgnet.TLRPC$ChannelParticipant) r4
            int r4 = r4.user_id
            if (r4 != r8) goto L_0x0047
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChannelParticipant> r8 = r9.participants
            r8.remove(r3)
            goto L_0x004a
        L_0x0047:
            int r3 = r3 + 1
            goto L_0x002d
        L_0x004a:
            int r8 = r7.type
            if (r8 != r0) goto L_0x006c
            int r8 = r7.delayResults
            int r8 = r8 - r2
            r7.delayResults = r8
            org.telegram.tgnet.TLRPC$ChannelParticipantsFilter r8 = r10.filter
            boolean r10 = r8 instanceof org.telegram.tgnet.TLRPC$TL_channelParticipantsContacts
            if (r10 == 0) goto L_0x005e
            java.util.ArrayList<org.telegram.tgnet.TLObject> r8 = r7.contacts
            android.util.SparseArray<org.telegram.tgnet.TLObject> r10 = r7.contactsMap
            goto L_0x0073
        L_0x005e:
            boolean r8 = r8 instanceof org.telegram.tgnet.TLRPC$TL_channelParticipantsBots
            if (r8 == 0) goto L_0x0067
            java.util.ArrayList<org.telegram.tgnet.TLObject> r8 = r7.bots
            android.util.SparseArray<org.telegram.tgnet.TLObject> r10 = r7.botsMap
            goto L_0x0073
        L_0x0067:
            java.util.ArrayList<org.telegram.tgnet.TLObject> r8 = r7.participants
            android.util.SparseArray<org.telegram.tgnet.TLObject> r10 = r7.participantsMap
            goto L_0x0073
        L_0x006c:
            java.util.ArrayList<org.telegram.tgnet.TLObject> r8 = r7.participants
            android.util.SparseArray<org.telegram.tgnet.TLObject> r10 = r7.participantsMap
            r10.clear()
        L_0x0073:
            r8.clear()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChannelParticipant> r3 = r9.participants
            r8.addAll(r3)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChannelParticipant> r3 = r9.participants
            int r3 = r3.size()
            r4 = 0
        L_0x0082:
            if (r4 >= r3) goto L_0x0094
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChannelParticipant> r5 = r9.participants
            java.lang.Object r5 = r5.get(r4)
            org.telegram.tgnet.TLRPC$ChannelParticipant r5 = (org.telegram.tgnet.TLRPC$ChannelParticipant) r5
            int r6 = r5.user_id
            r10.put(r6, r5)
            int r4 = r4 + 1
            goto L_0x0082
        L_0x0094:
            int r9 = r7.type
            if (r9 != r0) goto L_0x0115
            java.util.ArrayList<org.telegram.tgnet.TLObject> r9 = r7.participants
            int r9 = r9.size()
            r10 = 0
        L_0x009f:
            if (r10 >= r9) goto L_0x0115
            java.util.ArrayList<org.telegram.tgnet.TLObject> r3 = r7.participants
            java.lang.Object r3 = r3.get(r10)
            org.telegram.tgnet.TLRPC$ChannelParticipant r3 = (org.telegram.tgnet.TLRPC$ChannelParticipant) r3
            android.util.SparseArray<org.telegram.tgnet.TLObject> r4 = r7.contactsMap
            int r5 = r3.user_id
            java.lang.Object r4 = r4.get(r5)
            if (r4 != 0) goto L_0x0100
            android.util.SparseArray<org.telegram.tgnet.TLObject> r4 = r7.botsMap
            int r5 = r3.user_id
            java.lang.Object r4 = r4.get(r5)
            if (r4 == 0) goto L_0x00be
            goto L_0x0100
        L_0x00be:
            int r4 = r7.selectType
            if (r4 != r2) goto L_0x00d7
            org.telegram.messenger.MessagesController r4 = r7.getMessagesController()
            int r5 = r3.user_id
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            org.telegram.tgnet.TLRPC$User r4 = r4.getUser(r5)
            boolean r4 = org.telegram.messenger.UserObject.isDeleted(r4)
            if (r4 == 0) goto L_0x00d7
            goto L_0x0100
        L_0x00d7:
            android.util.SparseArray<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r4 = r7.ignoredUsers
            if (r4 == 0) goto L_0x00e4
            int r5 = r3.user_id
            int r4 = r4.indexOfKey(r5)
            if (r4 < 0) goto L_0x00e4
            goto L_0x0100
        L_0x00e4:
            int r4 = r7.selectType
            r5 = 4
            if (r4 != r5) goto L_0x00fe
            org.telegram.messenger.MessagesController r4 = r7.getMessagesController()
            int r5 = r3.user_id
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            org.telegram.tgnet.TLRPC$User r4 = r4.getUser(r5)
            if (r4 == 0) goto L_0x00fe
            boolean r4 = r4.bot
            if (r4 == 0) goto L_0x00fe
            goto L_0x0100
        L_0x00fe:
            r4 = 0
            goto L_0x0101
        L_0x0100:
            r4 = 1
        L_0x0101:
            if (r4 == 0) goto L_0x0113
            java.util.ArrayList<org.telegram.tgnet.TLObject> r4 = r7.participants
            r4.remove(r10)
            android.util.SparseArray<org.telegram.tgnet.TLObject> r4 = r7.participantsMap
            int r3 = r3.user_id
            r4.remove(r3)
            int r10 = r10 + -1
            int r9 = r9 + -1
        L_0x0113:
            int r10 = r10 + r2
            goto L_0x009f
        L_0x0115:
            int r9 = r7.type     // Catch:{ Exception -> 0x0150 }
            if (r9 == 0) goto L_0x011e
            r10 = 3
            if (r9 == r10) goto L_0x011e
            if (r9 != r0) goto L_0x0143
        L_0x011e:
            org.telegram.tgnet.TLRPC$Chat r10 = r7.currentChat     // Catch:{ Exception -> 0x0150 }
            if (r10 == 0) goto L_0x0143
            boolean r10 = r10.megagroup     // Catch:{ Exception -> 0x0150 }
            if (r10 == 0) goto L_0x0143
            org.telegram.tgnet.TLRPC$ChatFull r10 = r7.info     // Catch:{ Exception -> 0x0150 }
            boolean r3 = r10 instanceof org.telegram.tgnet.TLRPC$TL_channelFull     // Catch:{ Exception -> 0x0150 }
            if (r3 == 0) goto L_0x0143
            int r10 = r10.participants_count     // Catch:{ Exception -> 0x0150 }
            r3 = 200(0xc8, float:2.8E-43)
            if (r10 > r3) goto L_0x0143
            org.telegram.tgnet.ConnectionsManager r9 = r7.getConnectionsManager()     // Catch:{ Exception -> 0x0150 }
            int r9 = r9.getCurrentTime()     // Catch:{ Exception -> 0x0150 }
            org.telegram.ui.-$$Lambda$ChatUsersActivity$-h3yIg648caM9Bg0BVACKqxyAnQ r10 = new org.telegram.ui.-$$Lambda$ChatUsersActivity$-h3yIg648caM9Bg0BVACKqxyAnQ     // Catch:{ Exception -> 0x0150 }
            r10.<init>(r9)     // Catch:{ Exception -> 0x0150 }
            java.util.Collections.sort(r8, r10)     // Catch:{ Exception -> 0x0150 }
            goto L_0x0154
        L_0x0143:
            if (r9 != r2) goto L_0x0154
            java.util.ArrayList<org.telegram.tgnet.TLObject> r8 = r7.participants     // Catch:{ Exception -> 0x0150 }
            org.telegram.ui.-$$Lambda$ChatUsersActivity$cGmJcZCCgFCQ7DbAsk9R8Q3uIq0 r9 = new org.telegram.ui.-$$Lambda$ChatUsersActivity$cGmJcZCCgFCQ7DbAsk9R8Q3uIq0     // Catch:{ Exception -> 0x0150 }
            r9.<init>()     // Catch:{ Exception -> 0x0150 }
            java.util.Collections.sort(r8, r9)     // Catch:{ Exception -> 0x0150 }
            goto L_0x0154
        L_0x0150:
            r8 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r8)
        L_0x0154:
            int r8 = r7.type
            if (r8 != r0) goto L_0x015c
            int r8 = r7.delayResults
            if (r8 > 0) goto L_0x016d
        L_0x015c:
            r7.loadingUsers = r1
            r7.firstLoaded = r2
            org.telegram.ui.ChatUsersActivity$ListAdapter r8 = r7.listViewAdapter
            if (r8 == 0) goto L_0x0169
            int r8 = r8.getItemCount()
            goto L_0x016a
        L_0x0169:
            r8 = 0
        L_0x016a:
            r7.showItemsAnimated(r8)
        L_0x016d:
            r7.updateRows()
            org.telegram.ui.ChatUsersActivity$ListAdapter r8 = r7.listViewAdapter
            if (r8 == 0) goto L_0x018c
            r8.notifyDataSetChanged()
            org.telegram.ui.Components.StickerEmptyView r8 = r7.emptyView
            if (r8 == 0) goto L_0x018c
            org.telegram.ui.ChatUsersActivity$ListAdapter r8 = r7.listViewAdapter
            int r8 = r8.getItemCount()
            if (r8 != 0) goto L_0x018c
            boolean r8 = r7.firstLoaded
            if (r8 == 0) goto L_0x018c
            org.telegram.ui.Components.StickerEmptyView r8 = r7.emptyView
            r8.showProgress(r1, r2)
        L_0x018c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatUsersActivity.lambda$null$16$ChatUsersActivity(org.telegram.tgnet.TLRPC$TL_error, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_channels_getParticipants):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$14 */
    public /* synthetic */ int lambda$null$14$ChatUsersActivity(int i, TLObject tLObject, TLObject tLObject2) {
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$15 */
    public /* synthetic */ int lambda$null$15$ChatUsersActivity(TLObject tLObject, TLObject tLObject2) {
        int channelAdminParticipantType = getChannelAdminParticipantType(tLObject);
        int channelAdminParticipantType2 = getChannelAdminParticipantType(tLObject2);
        if (channelAdminParticipantType > channelAdminParticipantType2) {
            return 1;
        }
        return channelAdminParticipantType < channelAdminParticipantType2 ? -1 : 0;
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
            this.openTransitionEnded = true;
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
            this.searchAdapterHelper.queryServerSearch(str, (ChatUsersActivity.this.selectType == 0 || ChatUsersActivity.this.selectType == 4) ? false : true, false, true, false, false, ChatObject.isChannel(ChatUsersActivity.this.currentChat) ? ChatUsersActivity.this.chatId : 0, false, ChatUsersActivity.this.type, 1);
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

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v1, resolved type: org.telegram.ui.Cells.ManageChatUserCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v3, resolved type: org.telegram.ui.Cells.GraySectionCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v4, resolved type: org.telegram.ui.Cells.ManageChatUserCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v5, resolved type: org.telegram.ui.Cells.ManageChatUserCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v6, resolved type: org.telegram.ui.Cells.ManageChatUserCell} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r5, int r6) {
            /*
                r4 = this;
                r5 = 4
                java.lang.String r0 = "voipgroup_nameText"
                if (r6 == 0) goto L_0x0023
                org.telegram.ui.Cells.GraySectionCell r6 = new org.telegram.ui.Cells.GraySectionCell
                android.content.Context r1 = r4.mContext
                r6.<init>(r1)
                org.telegram.ui.ChatUsersActivity r1 = org.telegram.ui.ChatUsersActivity.this
                int r1 = r1.selectType
                if (r1 != r5) goto L_0x006c
                java.lang.String r5 = "voipgroup_emptyView"
                int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
                r6.setBackgroundColor(r5)
                r6.setTextColor(r0)
                goto L_0x006c
            L_0x0023:
                org.telegram.ui.Cells.ManageChatUserCell r6 = new org.telegram.ui.Cells.ManageChatUserCell
                android.content.Context r1 = r4.mContext
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                int r2 = r2.selectType
                if (r2 != 0) goto L_0x0031
                r2 = 1
                goto L_0x0032
            L_0x0031:
                r2 = 0
            L_0x0032:
                r3 = 2
                r6.<init>(r1, r3, r3, r2)
                org.telegram.ui.ChatUsersActivity r1 = org.telegram.ui.ChatUsersActivity.this
                int r1 = r1.selectType
                if (r1 == r5) goto L_0x0049
                java.lang.String r5 = "windowBackgroundWhite"
                int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
                r6.setBackgroundColor(r5)
                goto L_0x0064
            L_0x0049:
                int r5 = org.telegram.ui.ActionBar.Theme.getColor(r0)
                r6.setNameColor(r5)
                int r5 = org.telegram.ui.ActionBar.Theme.getColor(r0)
                java.lang.String r0 = "voipgroup_blueText"
                int r0 = org.telegram.ui.ActionBar.Theme.getColor(r0)
                r6.setStatusColors(r5, r0)
                java.lang.String r5 = "voipgroup_listViewBackground"
                r6.setDividerColor(r5)
            L_0x0064:
                org.telegram.ui.-$$Lambda$ChatUsersActivity$SearchAdapter$hhK0S9w9nUX9PfJaYpauTXgjTnU r5 = new org.telegram.ui.-$$Lambda$ChatUsersActivity$SearchAdapter$hhK0S9w9nUX9PfJaYpauTXgjTnU
                r5.<init>()
                r6.setDelegate(r5)
            L_0x006c:
                org.telegram.ui.Components.RecyclerListView$Holder r5 = new org.telegram.ui.Components.RecyclerListView$Holder
                r5.<init>(r6)
                return r5
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatUsersActivity.SearchAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v1, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v0, resolved type: android.text.SpannableStringBuilder} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v2, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v1, resolved type: android.text.SpannableStringBuilder} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v2, resolved type: android.text.SpannableStringBuilder} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v3, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v3, resolved type: android.text.SpannableStringBuilder} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v4, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v3, resolved type: android.text.SpannableStringBuilder} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v9, resolved type: java.lang.CharSequence} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v8, resolved type: android.text.SpannableStringBuilder} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v10, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v9, resolved type: android.text.SpannableStringBuilder} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v11, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v13, resolved type: android.text.SpannableStringBuilder} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v14, resolved type: java.lang.String} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* JADX WARNING: Removed duplicated region for block: B:43:0x00f2  */
        /* JADX WARNING: Removed duplicated region for block: B:52:0x0124  */
        /* JADX WARNING: Removed duplicated region for block: B:56:0x0135 A[ADDED_TO_REGION] */
        /* JADX WARNING: Removed duplicated region for block: B:81:0x0194  */
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
                goto L_0x01ce
            L_0x0011:
                android.view.View r2 = r2.itemView
                org.telegram.ui.Cells.GraySectionCell r2 = (org.telegram.ui.Cells.GraySectionCell) r2
                int r3 = r1.groupStartRow
                if (r0 != r3) goto L_0x006a
                org.telegram.ui.ChatUsersActivity r0 = org.telegram.ui.ChatUsersActivity.this
                int r0 = r0.type
                if (r0 != 0) goto L_0x002f
                r0 = 2131624631(0x7f0e02b7, float:1.8876447E38)
                java.lang.String r3 = "ChannelBlockedUsers"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
                r2.setText(r0)
                goto L_0x01ce
            L_0x002f:
                org.telegram.ui.ChatUsersActivity r0 = org.telegram.ui.ChatUsersActivity.this
                int r0 = r0.type
                r3 = 3
                if (r0 != r3) goto L_0x0046
                r0 = 2131624698(0x7f0e02fa, float:1.8876583E38)
                java.lang.String r3 = "ChannelRestrictedUsers"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
                r2.setText(r0)
                goto L_0x01ce
            L_0x0046:
                org.telegram.ui.ChatUsersActivity r0 = org.telegram.ui.ChatUsersActivity.this
                boolean r0 = r0.isChannel
                if (r0 == 0) goto L_0x005c
                r0 = 2131624706(0x7f0e0302, float:1.88766E38)
                java.lang.String r3 = "ChannelSubscribers"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
                r2.setText(r0)
                goto L_0x01ce
            L_0x005c:
                r0 = 2131624660(0x7f0e02d4, float:1.8876506E38)
                java.lang.String r3 = "ChannelMembers"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
                r2.setText(r0)
                goto L_0x01ce
            L_0x006a:
                int r3 = r1.globalStartRow
                if (r0 != r3) goto L_0x007c
                r0 = 2131625563(0x7f0e065b, float:1.8878337E38)
                java.lang.String r3 = "GlobalSearch"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
                r2.setText(r0)
                goto L_0x01ce
            L_0x007c:
                int r3 = r1.contactsStartRow
                if (r0 != r3) goto L_0x01ce
                r0 = 2131624919(0x7f0e03d7, float:1.8877031E38)
                java.lang.String r3 = "Contacts"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
                r2.setText(r0)
                goto L_0x01ce
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
                if (r5 == 0) goto L_0x01ce
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
                r8 = 0
                if (r6 == 0) goto L_0x00e1
                int r6 = r6 + r4
                if (r6 <= r0) goto L_0x00e0
                org.telegram.ui.Adapters.SearchAdapterHelper r6 = r1.searchAdapterHelper
                java.lang.String r6 = r6.getLastFoundChannel()
                r9 = 1
                goto L_0x00e3
            L_0x00e0:
                int r0 = r0 - r6
            L_0x00e1:
                r6 = r8
                r9 = 0
            L_0x00e3:
                java.lang.String r10 = "@"
                if (r9 != 0) goto L_0x0125
                java.util.ArrayList<org.telegram.tgnet.TLObject> r11 = r1.searchResult
                int r11 = r11.size()
                if (r11 == 0) goto L_0x0125
                int r11 = r11 + r4
                if (r11 <= r0) goto L_0x0124
                java.util.ArrayList<java.lang.CharSequence> r9 = r1.searchResultNames
                int r11 = r0 + -1
                java.lang.Object r9 = r9.get(r11)
                java.lang.CharSequence r9 = (java.lang.CharSequence) r9
                if (r9 == 0) goto L_0x011f
                boolean r11 = android.text.TextUtils.isEmpty(r5)
                if (r11 != 0) goto L_0x011f
                java.lang.String r11 = r9.toString()
                java.lang.StringBuilder r12 = new java.lang.StringBuilder
                r12.<init>()
                r12.append(r10)
                r12.append(r5)
                java.lang.String r12 = r12.toString()
                boolean r11 = r11.startsWith(r12)
                if (r11 == 0) goto L_0x011f
                r11 = r8
                goto L_0x0121
            L_0x011f:
                r11 = r9
                r9 = r8
            L_0x0121:
                r8 = r0
                r0 = 1
                goto L_0x0129
            L_0x0124:
                int r0 = r0 - r11
            L_0x0125:
                r11 = r8
                r8 = r0
                r0 = r9
                r9 = r11
            L_0x0129:
                r12 = 33
                java.lang.String r13 = "voipgroup_blueText"
                java.lang.String r14 = "windowBackgroundWhiteBlueText4"
                r15 = 4
                r7 = -1
                if (r0 != 0) goto L_0x0191
                if (r5 == 0) goto L_0x0191
                org.telegram.ui.Adapters.SearchAdapterHelper r0 = r1.searchAdapterHelper
                java.util.ArrayList r0 = r0.getGlobalSearch()
                int r0 = r0.size()
                if (r0 == 0) goto L_0x0191
                int r0 = r0 + r4
                if (r0 <= r8) goto L_0x0191
                org.telegram.ui.Adapters.SearchAdapterHelper r0 = r1.searchAdapterHelper
                java.lang.String r0 = r0.getLastFoundUsername()
                boolean r9 = r0.startsWith(r10)
                if (r9 == 0) goto L_0x0156
                java.lang.String r0 = r0.substring(r4)
            L_0x0156:
                android.text.SpannableStringBuilder r4 = new android.text.SpannableStringBuilder     // Catch:{ Exception -> 0x018c }
                r4.<init>()     // Catch:{ Exception -> 0x018c }
                r4.append(r10)     // Catch:{ Exception -> 0x018c }
                r4.append(r5)     // Catch:{ Exception -> 0x018c }
                int r9 = org.telegram.messenger.AndroidUtilities.indexOfIgnoreCase(r5, r0)     // Catch:{ Exception -> 0x018c }
                if (r9 == r7) goto L_0x018a
                int r0 = r0.length()     // Catch:{ Exception -> 0x018c }
                if (r9 != 0) goto L_0x0170
                int r0 = r0 + 1
                goto L_0x0172
            L_0x0170:
                int r9 = r9 + 1
            L_0x0172:
                android.text.style.ForegroundColorSpan r10 = new android.text.style.ForegroundColorSpan     // Catch:{ Exception -> 0x018c }
                org.telegram.ui.ChatUsersActivity r7 = org.telegram.ui.ChatUsersActivity.this     // Catch:{ Exception -> 0x018c }
                int r7 = r7.selectType     // Catch:{ Exception -> 0x018c }
                if (r7 != r15) goto L_0x017e
                r7 = r13
                goto L_0x017f
            L_0x017e:
                r7 = r14
            L_0x017f:
                int r7 = org.telegram.ui.ActionBar.Theme.getColor(r7)     // Catch:{ Exception -> 0x018c }
                r10.<init>(r7)     // Catch:{ Exception -> 0x018c }
                int r0 = r0 + r9
                r4.setSpan(r10, r9, r0, r12)     // Catch:{ Exception -> 0x018c }
            L_0x018a:
                r5 = r4
                goto L_0x0192
            L_0x018c:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
                goto L_0x0192
            L_0x0191:
                r5 = r9
            L_0x0192:
                if (r6 == 0) goto L_0x01bf
                java.lang.String r0 = org.telegram.messenger.UserObject.getUserName(r3)
                android.text.SpannableStringBuilder r11 = new android.text.SpannableStringBuilder
                r11.<init>(r0)
                int r0 = org.telegram.messenger.AndroidUtilities.indexOfIgnoreCase(r0, r6)
                r4 = -1
                if (r0 == r4) goto L_0x01bf
                android.text.style.ForegroundColorSpan r4 = new android.text.style.ForegroundColorSpan
                org.telegram.ui.ChatUsersActivity r7 = org.telegram.ui.ChatUsersActivity.this
                int r7 = r7.selectType
                if (r7 != r15) goto L_0x01af
                goto L_0x01b0
            L_0x01af:
                r13 = r14
            L_0x01b0:
                int r7 = org.telegram.ui.ActionBar.Theme.getColor(r13)
                r4.<init>(r7)
                int r6 = r6.length()
                int r6 = r6 + r0
                r11.setSpan(r4, r0, r6, r12)
            L_0x01bf:
                android.view.View r0 = r2.itemView
                org.telegram.ui.Cells.ManageChatUserCell r0 = (org.telegram.ui.Cells.ManageChatUserCell) r0
                java.lang.Integer r2 = java.lang.Integer.valueOf(r8)
                r0.setTag(r2)
                r2 = 0
                r0.setData(r3, r11, r5, r2)
            L_0x01ce:
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
            if (ChatUsersActivity.this.type != 3 || !ChatUsersActivity.this.loadingUsers || ChatUsersActivity.this.firstLoaded) {
                return ChatUsersActivity.this.rowCount;
            }
            return 0;
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onCreateViewHolder$0 */
        public /* synthetic */ boolean lambda$onCreateViewHolder$0$ChatUsersActivity$ListAdapter(ManageChatUserCell manageChatUserCell, boolean z) {
            return ChatUsersActivity.this.createMenuForParticipant(ChatUsersActivity.this.listViewAdapter.getItem(((Integer) manageChatUserCell.getTag()).intValue()), !z);
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v9, resolved type: org.telegram.ui.Cells.TextInfoPrivacyCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v10, resolved type: org.telegram.ui.Cells.ManageChatTextCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v11, resolved type: org.telegram.ui.Cells.ShadowSectionCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v12, resolved type: org.telegram.ui.ChatUsersActivity$ListAdapter$1} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v14, resolved type: org.telegram.ui.Cells.TextSettingsCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v15, resolved type: org.telegram.ui.Cells.TextCheckCell2} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v32, resolved type: org.telegram.ui.Cells.ManageChatUserCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v16, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v33, resolved type: org.telegram.ui.Cells.GraySectionCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v17, resolved type: org.telegram.ui.Cells.LoadingCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v18, resolved type: org.telegram.ui.ChatUsersActivity$ChooseView} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v39, resolved type: org.telegram.ui.Cells.ManageChatUserCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v19, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v20, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v21, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v22, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v23, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v24, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v25, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v15, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v26, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v27, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v28, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v29, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v40, resolved type: org.telegram.ui.Cells.ManageChatUserCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v41, resolved type: org.telegram.ui.Cells.ManageChatUserCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v30, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v31, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v32, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX WARNING: type inference failed for: r12v2, types: [android.view.View] */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r12, int r13) {
            /*
                r11 = this;
                java.lang.String r12 = "voipgroup_nameText"
                java.lang.String r0 = "windowBackgroundGray"
                java.lang.String r1 = "windowBackgroundWhite"
                r2 = 1
                r3 = 4
                switch(r13) {
                    case 0: goto L_0x01e1;
                    case 1: goto L_0x01c9;
                    case 2: goto L_0x01b1;
                    case 3: goto L_0x0199;
                    case 4: goto L_0x00b2;
                    case 5: goto L_0x008c;
                    case 6: goto L_0x0074;
                    case 7: goto L_0x005c;
                    case 8: goto L_0x003d;
                    case 9: goto L_0x000e;
                    case 10: goto L_0x0028;
                    default: goto L_0x000e;
                }
            L_0x000e:
                org.telegram.ui.ChatUsersActivity$ChooseView r12 = new org.telegram.ui.ChatUsersActivity$ChooseView
                org.telegram.ui.ChatUsersActivity r13 = org.telegram.ui.ChatUsersActivity.this
                android.content.Context r0 = r11.mContext
                r12.<init>(r0)
                org.telegram.ui.ChatUsersActivity r13 = org.telegram.ui.ChatUsersActivity.this
                int r13 = r13.selectType
                if (r13 == r3) goto L_0x0252
                int r13 = org.telegram.ui.ActionBar.Theme.getColor(r1)
                r12.setBackgroundColor(r13)
                goto L_0x0252
            L_0x0028:
                org.telegram.ui.Cells.LoadingCell r12 = new org.telegram.ui.Cells.LoadingCell
                android.content.Context r13 = r11.mContext
                r0 = 1109393408(0x42200000, float:40.0)
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
                r1 = 1123024896(0x42var_, float:120.0)
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                r12.<init>(r13, r0, r1)
                goto L_0x0252
            L_0x003d:
                org.telegram.ui.Cells.GraySectionCell r13 = new org.telegram.ui.Cells.GraySectionCell
                android.content.Context r0 = r11.mContext
                r13.<init>(r0)
                org.telegram.ui.ChatUsersActivity r0 = org.telegram.ui.ChatUsersActivity.this
                int r0 = r0.selectType
                if (r0 != r3) goto L_0x0059
                java.lang.String r0 = "voipgroup_emptyView"
                int r0 = org.telegram.ui.ActionBar.Theme.getColor(r0)
                r13.setBackgroundColor(r0)
                r13.setTextColor(r12)
            L_0x0059:
                r12 = r13
                goto L_0x0252
            L_0x005c:
                org.telegram.ui.Cells.TextCheckCell2 r12 = new org.telegram.ui.Cells.TextCheckCell2
                android.content.Context r13 = r11.mContext
                r12.<init>(r13)
                org.telegram.ui.ChatUsersActivity r13 = org.telegram.ui.ChatUsersActivity.this
                int r13 = r13.selectType
                if (r13 == r3) goto L_0x0252
                int r13 = org.telegram.ui.ActionBar.Theme.getColor(r1)
                r12.setBackgroundColor(r13)
                goto L_0x0252
            L_0x0074:
                org.telegram.ui.Cells.TextSettingsCell r12 = new org.telegram.ui.Cells.TextSettingsCell
                android.content.Context r13 = r11.mContext
                r12.<init>(r13)
                org.telegram.ui.ChatUsersActivity r13 = org.telegram.ui.ChatUsersActivity.this
                int r13 = r13.selectType
                if (r13 == r3) goto L_0x0252
                int r13 = org.telegram.ui.ActionBar.Theme.getColor(r1)
                r12.setBackgroundColor(r13)
                goto L_0x0252
            L_0x008c:
                org.telegram.ui.Cells.HeaderCell r12 = new org.telegram.ui.Cells.HeaderCell
                android.content.Context r5 = r11.mContext
                r7 = 21
                r8 = 11
                r9 = 0
                java.lang.String r6 = "windowBackgroundWhiteBlueHeader"
                r4 = r12
                r4.<init>(r5, r6, r7, r8, r9)
                org.telegram.ui.ChatUsersActivity r13 = org.telegram.ui.ChatUsersActivity.this
                int r13 = r13.selectType
                if (r13 == r3) goto L_0x00ab
                int r13 = org.telegram.ui.ActionBar.Theme.getColor(r1)
                r12.setBackgroundColor(r13)
            L_0x00ab:
                r13 = 43
                r12.setHeight(r13)
                goto L_0x0252
            L_0x00b2:
                org.telegram.ui.ChatUsersActivity$ListAdapter$1 r12 = new org.telegram.ui.ChatUsersActivity$ListAdapter$1
                android.content.Context r13 = r11.mContext
                r12.<init>(r13)
                android.content.Context r13 = r11.mContext
                r0 = 2131165447(0x7var_, float:1.7945111E38)
                java.lang.String r1 = "windowBackgroundGrayShadow"
                android.graphics.drawable.Drawable r13 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r13, (int) r0, (java.lang.String) r1)
                r12.setBackgroundDrawable(r13)
                android.widget.LinearLayout r13 = new android.widget.LinearLayout
                android.content.Context r0 = r11.mContext
                r13.<init>(r0)
                r13.setOrientation(r2)
                r3 = -2
                r4 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
                r5 = 17
                r6 = 1101004800(0x41a00000, float:20.0)
                r7 = 0
                r8 = 1101004800(0x41a00000, float:20.0)
                r9 = 0
                android.widget.FrameLayout$LayoutParams r0 = org.telegram.ui.Components.LayoutHelper.createFrame(r3, r4, r5, r6, r7, r8, r9)
                r12.addView(r13, r0)
                android.widget.ImageView r0 = new android.widget.ImageView
                android.content.Context r1 = r11.mContext
                r0.<init>(r1)
                r1 = 2131165452(0x7var_c, float:1.7945122E38)
                r0.setImageResource(r1)
                android.widget.ImageView$ScaleType r1 = android.widget.ImageView.ScaleType.CENTER
                r0.setScaleType(r1)
                android.graphics.PorterDuffColorFilter r1 = new android.graphics.PorterDuffColorFilter
                java.lang.String r3 = "emptyListPlaceholder"
                int r4 = org.telegram.ui.ActionBar.Theme.getColor(r3)
                android.graphics.PorterDuff$Mode r5 = android.graphics.PorterDuff.Mode.MULTIPLY
                r1.<init>(r4, r5)
                r0.setColorFilter(r1)
                r1 = -2
                android.widget.LinearLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r1, (int) r1, (int) r2)
                r13.addView(r0, r1)
                android.widget.TextView r0 = new android.widget.TextView
                android.content.Context r1 = r11.mContext
                r0.<init>(r1)
                r1 = 2131626048(0x7f0e0840, float:1.8879321E38)
                java.lang.String r4 = "NoBlockedUsers"
                java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r4, r1)
                r0.setText(r1)
                int r1 = org.telegram.ui.ActionBar.Theme.getColor(r3)
                r0.setTextColor(r1)
                r1 = 1098907648(0x41800000, float:16.0)
                r0.setTextSize(r2, r1)
                r0.setGravity(r2)
                java.lang.String r1 = "fonts/rmedium.ttf"
                android.graphics.Typeface r1 = org.telegram.messenger.AndroidUtilities.getTypeface(r1)
                r0.setTypeface(r1)
                r4 = -2
                r5 = -2
                r6 = 1
                r7 = 0
                r8 = 10
                r9 = 0
                r10 = 0
                android.widget.LinearLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r4, (int) r5, (int) r6, (int) r7, (int) r8, (int) r9, (int) r10)
                r13.addView(r0, r1)
                android.widget.TextView r0 = new android.widget.TextView
                android.content.Context r1 = r11.mContext
                r0.<init>(r1)
                org.telegram.ui.ChatUsersActivity r1 = org.telegram.ui.ChatUsersActivity.this
                boolean r1 = r1.isChannel
                if (r1 == 0) goto L_0x0164
                r1 = 2131626046(0x7f0e083e, float:1.8879317E38)
                java.lang.String r4 = "NoBlockedChannel2"
                java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r4, r1)
                r0.setText(r1)
                goto L_0x0170
            L_0x0164:
                r1 = 2131626047(0x7f0e083f, float:1.887932E38)
                java.lang.String r4 = "NoBlockedGroup2"
                java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r4, r1)
                r0.setText(r1)
            L_0x0170:
                int r1 = org.telegram.ui.ActionBar.Theme.getColor(r3)
                r0.setTextColor(r1)
                r1 = 1097859072(0x41700000, float:15.0)
                r0.setTextSize(r2, r1)
                r0.setGravity(r2)
                r3 = -2
                r4 = -2
                r5 = 1
                r6 = 0
                r7 = 10
                r8 = 0
                r9 = 0
                android.widget.LinearLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r3, (int) r4, (int) r5, (int) r6, (int) r7, (int) r8, (int) r9)
                r13.addView(r0, r1)
                androidx.recyclerview.widget.RecyclerView$LayoutParams r13 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r0 = -1
                r13.<init>((int) r0, (int) r0)
                r12.setLayoutParams(r13)
                goto L_0x0252
            L_0x0199:
                org.telegram.ui.Cells.ShadowSectionCell r12 = new org.telegram.ui.Cells.ShadowSectionCell
                android.content.Context r13 = r11.mContext
                r12.<init>(r13)
                org.telegram.ui.ChatUsersActivity r13 = org.telegram.ui.ChatUsersActivity.this
                int r13 = r13.selectType
                if (r13 == r3) goto L_0x0252
                int r13 = org.telegram.ui.ActionBar.Theme.getColor(r0)
                r12.setBackgroundColor(r13)
                goto L_0x0252
            L_0x01b1:
                org.telegram.ui.Cells.ManageChatTextCell r12 = new org.telegram.ui.Cells.ManageChatTextCell
                android.content.Context r13 = r11.mContext
                r12.<init>(r13)
                org.telegram.ui.ChatUsersActivity r13 = org.telegram.ui.ChatUsersActivity.this
                int r13 = r13.selectType
                if (r13 == r3) goto L_0x0252
                int r13 = org.telegram.ui.ActionBar.Theme.getColor(r1)
                r12.setBackgroundColor(r13)
                goto L_0x0252
            L_0x01c9:
                org.telegram.ui.Cells.TextInfoPrivacyCell r12 = new org.telegram.ui.Cells.TextInfoPrivacyCell
                android.content.Context r13 = r11.mContext
                r12.<init>(r13)
                org.telegram.ui.ChatUsersActivity r13 = org.telegram.ui.ChatUsersActivity.this
                int r13 = r13.selectType
                if (r13 == r3) goto L_0x0252
                int r13 = org.telegram.ui.ActionBar.Theme.getColor(r0)
                r12.setBackgroundColor(r13)
                goto L_0x0252
            L_0x01e1:
                org.telegram.ui.Cells.ManageChatUserCell r13 = new org.telegram.ui.Cells.ManageChatUserCell
                android.content.Context r0 = r11.mContext
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                int r4 = r4.type
                r5 = 6
                r6 = 3
                if (r4 == 0) goto L_0x01fa
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                int r4 = r4.type
                if (r4 != r6) goto L_0x01f8
                goto L_0x01fa
            L_0x01f8:
                r4 = 6
                goto L_0x01fb
            L_0x01fa:
                r4 = 7
            L_0x01fb:
                org.telegram.ui.ChatUsersActivity r7 = org.telegram.ui.ChatUsersActivity.this
                int r7 = r7.type
                if (r7 == 0) goto L_0x020d
                org.telegram.ui.ChatUsersActivity r7 = org.telegram.ui.ChatUsersActivity.this
                int r7 = r7.type
                if (r7 != r6) goto L_0x020c
                goto L_0x020d
            L_0x020c:
                r5 = 2
            L_0x020d:
                org.telegram.ui.ChatUsersActivity r6 = org.telegram.ui.ChatUsersActivity.this
                int r6 = r6.selectType
                if (r6 != 0) goto L_0x0216
                goto L_0x0217
            L_0x0216:
                r2 = 0
            L_0x0217:
                r13.<init>(r0, r4, r5, r2)
                org.telegram.ui.ChatUsersActivity r0 = org.telegram.ui.ChatUsersActivity.this
                int r0 = r0.selectType
                if (r0 == r3) goto L_0x022a
                int r12 = org.telegram.ui.ActionBar.Theme.getColor(r1)
                r13.setBackgroundColor(r12)
                goto L_0x0248
            L_0x022a:
                int r12 = org.telegram.ui.ActionBar.Theme.getColor(r12)
                r13.setNameColor(r12)
                java.lang.String r12 = "voipgroup_lastSeenText"
                int r12 = org.telegram.ui.ActionBar.Theme.getColor(r12)
                java.lang.String r0 = "voipgroup_blueText"
                int r0 = org.telegram.ui.ActionBar.Theme.getColor(r0)
                r13.setStatusColors(r12, r0)
                java.lang.String r12 = "voipgroup_listViewBackground"
                r13.setDividerColor(r12)
            L_0x0248:
                org.telegram.ui.-$$Lambda$ChatUsersActivity$ListAdapter$Xr7PIkQyIu3_GPO5ta7IQSFNZ7U r12 = new org.telegram.ui.-$$Lambda$ChatUsersActivity$ListAdapter$Xr7PIkQyIu3_GPO5ta7IQSFNZ7U
                r12.<init>()
                r13.setDelegate(r12)
                goto L_0x0059
            L_0x0252:
                org.telegram.ui.Components.RecyclerListView$Holder r13 = new org.telegram.ui.Components.RecyclerListView$Holder
                r13.<init>(r12)
                return r13
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatUsersActivity.ListAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        /* JADX WARNING: Code restructure failed: missing block: B:217:0x05d9, code lost:
            if (org.telegram.ui.ChatUsersActivity.access$2300(r0.this$0).megagroup == false) goto L_0x05db;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:226:0x0607, code lost:
            if (org.telegram.ui.ChatUsersActivity.access$2300(r0.this$0).megagroup == false) goto L_0x05db;
         */
        /* JADX WARNING: Removed duplicated region for block: B:231:0x0615  */
        /* JADX WARNING: Removed duplicated region for block: B:232:0x0628  */
        /* JADX WARNING: Removed duplicated region for block: B:235:0x0646  */
        /* JADX WARNING: Removed duplicated region for block: B:269:0x06eb  */
        /* JADX WARNING: Removed duplicated region for block: B:270:0x06ee  */
        /* JADX WARNING: Removed duplicated region for block: B:318:? A[RETURN, SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r17, int r18) {
            /*
                r16 = this;
                r0 = r16
                r1 = r17
                r2 = r18
                int r3 = r17.getItemViewType()
                r4 = 2131165446(0x7var_, float:1.794511E38)
                r5 = 2
                r6 = 2131165447(0x7var_, float:1.7945111E38)
                r7 = -1
                r8 = 3
                java.lang.String r9 = "windowBackgroundGrayShadow"
                r10 = 0
                r11 = 0
                r12 = 1
                switch(r3) {
                    case 0: goto L_0x05a0;
                    case 1: goto L_0x0494;
                    case 2: goto L_0x0388;
                    case 3: goto L_0x0345;
                    case 4: goto L_0x001c;
                    case 5: goto L_0x02c1;
                    case 6: goto L_0x028f;
                    case 7: goto L_0x00ac;
                    case 8: goto L_0x001e;
                    default: goto L_0x001c;
                }
            L_0x001c:
                goto L_0x0714
            L_0x001e:
                android.view.View r1 = r1.itemView
                org.telegram.ui.Cells.GraySectionCell r1 = (org.telegram.ui.Cells.GraySectionCell) r1
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.membersHeaderRow
                if (r2 != r3) goto L_0x005c
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$Chat r2 = r2.currentChat
                boolean r2 = org.telegram.messenger.ChatObject.isChannel(r2)
                if (r2 == 0) goto L_0x004e
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$Chat r2 = r2.currentChat
                boolean r2 = r2.megagroup
                if (r2 != 0) goto L_0x004e
                r2 = 2131624684(0x7f0e02ec, float:1.8876555E38)
                java.lang.String r3 = "ChannelOtherSubscribers"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2)
                goto L_0x0714
            L_0x004e:
                r2 = 2131624682(0x7f0e02ea, float:1.887655E38)
                java.lang.String r3 = "ChannelOtherMembers"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2)
                goto L_0x0714
            L_0x005c:
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.botHeaderRow
                if (r2 != r3) goto L_0x0072
                r2 = 2131624632(0x7f0e02b8, float:1.887645E38)
                java.lang.String r3 = "ChannelBots"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2)
                goto L_0x0714
            L_0x0072:
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.contactsHeaderRow
                if (r2 != r3) goto L_0x0714
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$Chat r2 = r2.currentChat
                boolean r2 = org.telegram.messenger.ChatObject.isChannel(r2)
                if (r2 == 0) goto L_0x009e
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$Chat r2 = r2.currentChat
                boolean r2 = r2.megagroup
                if (r2 != 0) goto L_0x009e
                r2 = 2131624639(0x7f0e02bf, float:1.8876463E38)
                java.lang.String r3 = "ChannelContacts"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2)
                goto L_0x0714
            L_0x009e:
                r2 = 2131625573(0x7f0e0665, float:1.8878358E38)
                java.lang.String r3 = "GroupContacts"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2)
                goto L_0x0714
            L_0x00ac:
                android.view.View r1 = r1.itemView
                org.telegram.ui.Cells.TextCheckCell2 r1 = (org.telegram.ui.Cells.TextCheckCell2) r1
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.changeInfoRow
                if (r2 != r3) goto L_0x00e1
                r3 = 2131627548(0x7f0e0e1c, float:1.8882364E38)
                java.lang.String r4 = "UserRestrictionsChangeInfo"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$TL_chatBannedRights r4 = r4.defaultBannedRights
                boolean r4 = r4.change_info
                if (r4 != 0) goto L_0x00db
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$Chat r4 = r4.currentChat
                java.lang.String r4 = r4.username
                boolean r4 = android.text.TextUtils.isEmpty(r4)
                if (r4 == 0) goto L_0x00db
                r4 = 1
                goto L_0x00dc
            L_0x00db:
                r4 = 0
            L_0x00dc:
                r1.setTextAndCheck(r3, r4, r11)
                goto L_0x01c7
            L_0x00e1:
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.addUsersRow
                if (r2 != r3) goto L_0x0100
                r3 = 2131627553(0x7f0e0e21, float:1.8882374E38)
                java.lang.String r4 = "UserRestrictionsInviteUsers"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$TL_chatBannedRights r4 = r4.defaultBannedRights
                boolean r4 = r4.invite_users
                r4 = r4 ^ r12
                r1.setTextAndCheck(r3, r4, r12)
                goto L_0x01c7
            L_0x0100:
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.pinMessagesRow
                if (r2 != r3) goto L_0x0131
                r3 = 2131627563(0x7f0e0e2b, float:1.8882394E38)
                java.lang.String r4 = "UserRestrictionsPinMessages"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$TL_chatBannedRights r4 = r4.defaultBannedRights
                boolean r4 = r4.pin_messages
                if (r4 != 0) goto L_0x012b
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$Chat r4 = r4.currentChat
                java.lang.String r4 = r4.username
                boolean r4 = android.text.TextUtils.isEmpty(r4)
                if (r4 == 0) goto L_0x012b
                r4 = 1
                goto L_0x012c
            L_0x012b:
                r4 = 0
            L_0x012c:
                r1.setTextAndCheck(r3, r4, r12)
                goto L_0x01c7
            L_0x0131:
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.sendMessagesRow
                if (r2 != r3) goto L_0x0150
                r3 = 2131627565(0x7f0e0e2d, float:1.8882398E38)
                java.lang.String r4 = "UserRestrictionsSend"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$TL_chatBannedRights r4 = r4.defaultBannedRights
                boolean r4 = r4.send_messages
                r4 = r4 ^ r12
                r1.setTextAndCheck(r3, r4, r12)
                goto L_0x01c7
            L_0x0150:
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.sendMediaRow
                if (r2 != r3) goto L_0x016e
                r3 = 2131627566(0x7f0e0e2e, float:1.88824E38)
                java.lang.String r4 = "UserRestrictionsSendMedia"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$TL_chatBannedRights r4 = r4.defaultBannedRights
                boolean r4 = r4.send_media
                r4 = r4 ^ r12
                r1.setTextAndCheck(r3, r4, r12)
                goto L_0x01c7
            L_0x016e:
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.sendStickersRow
                if (r2 != r3) goto L_0x018c
                r3 = 2131627568(0x7f0e0e30, float:1.8882404E38)
                java.lang.String r4 = "UserRestrictionsSendStickers"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$TL_chatBannedRights r4 = r4.defaultBannedRights
                boolean r4 = r4.send_stickers
                r4 = r4 ^ r12
                r1.setTextAndCheck(r3, r4, r12)
                goto L_0x01c7
            L_0x018c:
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.embedLinksRow
                if (r2 != r3) goto L_0x01aa
                r3 = 2131627552(0x7f0e0e20, float:1.8882372E38)
                java.lang.String r4 = "UserRestrictionsEmbedLinks"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$TL_chatBannedRights r4 = r4.defaultBannedRights
                boolean r4 = r4.embed_links
                r4 = r4 ^ r12
                r1.setTextAndCheck(r3, r4, r12)
                goto L_0x01c7
            L_0x01aa:
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.sendPollsRow
                if (r2 != r3) goto L_0x01c7
                r3 = 2131627567(0x7f0e0e2f, float:1.8882402E38)
                java.lang.String r4 = "UserRestrictionsSendPolls"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$TL_chatBannedRights r4 = r4.defaultBannedRights
                boolean r4 = r4.send_polls
                r4 = r4 ^ r12
                r1.setTextAndCheck(r3, r4, r12)
            L_0x01c7:
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.sendMediaRow
                if (r2 == r3) goto L_0x01fd
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.sendStickersRow
                if (r2 == r3) goto L_0x01fd
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.embedLinksRow
                if (r2 == r3) goto L_0x01fd
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.sendPollsRow
                if (r2 != r3) goto L_0x01e8
                goto L_0x01fd
            L_0x01e8:
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.sendMessagesRow
                if (r2 != r3) goto L_0x0217
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$TL_chatBannedRights r3 = r3.defaultBannedRights
                boolean r3 = r3.view_messages
                r3 = r3 ^ r12
                r1.setEnabled(r3)
                goto L_0x0217
            L_0x01fd:
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$TL_chatBannedRights r3 = r3.defaultBannedRights
                boolean r3 = r3.send_messages
                if (r3 != 0) goto L_0x0213
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$TL_chatBannedRights r3 = r3.defaultBannedRights
                boolean r3 = r3.view_messages
                if (r3 != 0) goto L_0x0213
                r3 = 1
                goto L_0x0214
            L_0x0213:
                r3 = 0
            L_0x0214:
                r1.setEnabled(r3)
            L_0x0217:
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$Chat r3 = r3.currentChat
                boolean r3 = org.telegram.messenger.ChatObject.canBlockUsers(r3)
                if (r3 == 0) goto L_0x028a
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.addUsersRow
                if (r2 != r3) goto L_0x0237
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$Chat r3 = r3.currentChat
                boolean r3 = org.telegram.messenger.ChatObject.canUserDoAdminAction(r3, r8)
                if (r3 == 0) goto L_0x027d
            L_0x0237:
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.pinMessagesRow
                if (r2 != r3) goto L_0x024b
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$Chat r3 = r3.currentChat
                boolean r3 = org.telegram.messenger.ChatObject.canUserDoAdminAction(r3, r11)
                if (r3 == 0) goto L_0x027d
            L_0x024b:
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.changeInfoRow
                if (r2 != r3) goto L_0x025f
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$Chat r3 = r3.currentChat
                boolean r3 = org.telegram.messenger.ChatObject.canUserDoAdminAction(r3, r12)
                if (r3 == 0) goto L_0x027d
            L_0x025f:
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$Chat r3 = r3.currentChat
                java.lang.String r3 = r3.username
                boolean r3 = android.text.TextUtils.isEmpty(r3)
                if (r3 != 0) goto L_0x0285
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.pinMessagesRow
                if (r2 == r3) goto L_0x027d
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.changeInfoRow
                if (r2 != r3) goto L_0x0285
            L_0x027d:
                r2 = 2131165856(0x7var_a0, float:1.794594E38)
                r1.setIcon(r2)
                goto L_0x0714
            L_0x0285:
                r1.setIcon(r11)
                goto L_0x0714
            L_0x028a:
                r1.setIcon(r11)
                goto L_0x0714
            L_0x028f:
                android.view.View r1 = r1.itemView
                org.telegram.ui.Cells.TextSettingsCell r1 = (org.telegram.ui.Cells.TextSettingsCell) r1
                r2 = 2131624629(0x7f0e02b5, float:1.8876443E38)
                java.lang.String r3 = "ChannelBlacklist"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                java.lang.Object[] r3 = new java.lang.Object[r12]
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r4 = r4.info
                if (r4 == 0) goto L_0x02af
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r4 = r4.info
                int r4 = r4.kicked_count
                goto L_0x02b0
            L_0x02af:
                r4 = 0
            L_0x02b0:
                java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
                r3[r11] = r4
                java.lang.String r4 = "%d"
                java.lang.String r3 = java.lang.String.format(r4, r3)
                r1.setTextAndValue(r2, r3, r11)
                goto L_0x0714
            L_0x02c1:
                android.view.View r1 = r1.itemView
                org.telegram.ui.Cells.HeaderCell r1 = (org.telegram.ui.Cells.HeaderCell) r1
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.restricted1SectionRow
                if (r2 != r3) goto L_0x0319
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                int r2 = r2.type
                if (r2 != 0) goto L_0x030b
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r2 = r2.info
                if (r2 == 0) goto L_0x02e6
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r2 = r2.info
                int r2 = r2.kicked_count
                goto L_0x02f0
            L_0x02e6:
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                java.util.ArrayList r2 = r2.participants
                int r2 = r2.size()
            L_0x02f0:
                if (r2 == 0) goto L_0x02fd
                java.lang.String r3 = "RemovedUser"
                java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r3, r2)
                r1.setText(r2)
                goto L_0x0714
            L_0x02fd:
                r2 = 2131624631(0x7f0e02b7, float:1.8876447E38)
                java.lang.String r3 = "ChannelBlockedUsers"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2)
                goto L_0x0714
            L_0x030b:
                r2 = 2131624698(0x7f0e02fa, float:1.8876583E38)
                java.lang.String r3 = "ChannelRestrictedUsers"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2)
                goto L_0x0714
            L_0x0319:
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.permissionsSectionRow
                if (r2 != r3) goto L_0x032f
                r2 = 2131624686(0x7f0e02ee, float:1.8876559E38)
                java.lang.String r3 = "ChannelPermissionsHeader"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2)
                goto L_0x0714
            L_0x032f:
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.slowmodeRow
                if (r2 != r3) goto L_0x0714
                r2 = 2131627213(0x7f0e0ccd, float:1.8881684E38)
                java.lang.String r3 = "Slowmode"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2)
                goto L_0x0714
            L_0x0345:
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.addNewSectionRow
                if (r2 == r3) goto L_0x037b
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.type
                if (r3 != r8) goto L_0x036e
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.participantsDividerRow
                if (r2 != r3) goto L_0x036e
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                int r2 = r2.addNewRow
                if (r2 != r7) goto L_0x036e
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                int r2 = r2.participantsStartRow
                if (r2 != r7) goto L_0x036e
                goto L_0x037b
            L_0x036e:
                android.view.View r1 = r1.itemView
                android.content.Context r2 = r0.mContext
                android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r2, (int) r4, (java.lang.String) r9)
                r1.setBackgroundDrawable(r2)
                goto L_0x0714
            L_0x037b:
                android.view.View r1 = r1.itemView
                android.content.Context r2 = r0.mContext
                android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r2, (int) r6, (java.lang.String) r9)
                r1.setBackgroundDrawable(r2)
                goto L_0x0714
            L_0x0388:
                android.view.View r1 = r1.itemView
                org.telegram.ui.Cells.ManageChatTextCell r1 = (org.telegram.ui.Cells.ManageChatTextCell) r1
                java.lang.String r3 = "windowBackgroundWhiteGrayIcon"
                java.lang.String r4 = "windowBackgroundWhiteBlackText"
                r1.setColors(r3, r4)
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.addNewRow
                if (r2 != r3) goto L_0x0462
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                int r2 = r2.type
                r3 = 2131165249(0x7var_, float:1.794471E38)
                java.lang.String r4 = "windowBackgroundWhiteBlueButton"
                java.lang.String r6 = "windowBackgroundWhiteBlueIcon"
                if (r2 != r8) goto L_0x03c8
                r1.setColors(r6, r4)
                r2 = 2131624615(0x7f0e02a7, float:1.8876415E38)
                java.lang.String r4 = "ChannelAddException"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r2)
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                int r4 = r4.participantsStartRow
                if (r4 == r7) goto L_0x03c3
                r11 = 1
            L_0x03c3:
                r1.setText(r2, r10, r3, r11)
                goto L_0x0714
            L_0x03c8:
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                int r2 = r2.type
                if (r2 != 0) goto L_0x03e1
                r2 = 2131624630(0x7f0e02b6, float:1.8876445E38)
                java.lang.String r3 = "ChannelBlockUser"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r3 = 2131165254(0x7var_, float:1.794472E38)
                r1.setText(r2, r10, r3, r11)
                goto L_0x0714
            L_0x03e1:
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                int r2 = r2.type
                if (r2 != r12) goto L_0x040e
                r1.setColors(r6, r4)
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                boolean r2 = r2.loadingUsers
                if (r2 == 0) goto L_0x03fc
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                boolean r2 = r2.firstLoaded
                if (r2 == 0) goto L_0x03fd
            L_0x03fc:
                r11 = 1
            L_0x03fd:
                r2 = 2131624614(0x7f0e02a6, float:1.8876413E38)
                java.lang.String r3 = "ChannelAddAdmin"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r3 = 2131165258(0x7var_a, float:1.7944728E38)
                r1.setText(r2, r10, r3, r11)
                goto L_0x0714
            L_0x040e:
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                int r2 = r2.type
                if (r2 != r5) goto L_0x0714
                r1.setColors(r6, r4)
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                boolean r2 = r2.loadingUsers
                if (r2 == 0) goto L_0x0429
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                boolean r2 = r2.firstLoaded
                if (r2 == 0) goto L_0x043e
            L_0x0429:
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                int r2 = r2.membersHeaderRow
                if (r2 != r7) goto L_0x043e
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                java.util.ArrayList r2 = r2.participants
                boolean r2 = r2.isEmpty()
                if (r2 != 0) goto L_0x043e
                r11 = 1
            L_0x043e:
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                boolean r2 = r2.isChannel
                if (r2 == 0) goto L_0x0454
                r2 = 2131624197(0x7f0e0105, float:1.8875567E38)
                java.lang.String r4 = "AddSubscriber"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r2)
                r1.setText(r2, r10, r3, r11)
                goto L_0x0714
            L_0x0454:
                r2 = 2131624179(0x7f0e00f3, float:1.887553E38)
                java.lang.String r4 = "AddMember"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r2)
                r1.setText(r2, r10, r3, r11)
                goto L_0x0714
            L_0x0462:
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.recentActionsRow
                if (r2 != r3) goto L_0x047b
                r2 = 2131625234(0x7f0e0512, float:1.887767E38)
                java.lang.String r3 = "EventLog"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r3 = 2131165457(0x7var_, float:1.7945132E38)
                r1.setText(r2, r10, r3, r11)
                goto L_0x0714
            L_0x047b:
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.addNew2Row
                if (r2 != r3) goto L_0x0714
                r2 = 2131624651(0x7f0e02cb, float:1.8876488E38)
                java.lang.String r3 = "ChannelInviteViaLink"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r3 = 2131165926(0x7var_e6, float:1.7946083E38)
                r1.setText(r2, r10, r3, r12)
                goto L_0x0714
            L_0x0494:
                android.view.View r1 = r1.itemView
                org.telegram.ui.Cells.TextInfoPrivacyCell r1 = (org.telegram.ui.Cells.TextInfoPrivacyCell) r1
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.participantsInfoRow
                if (r2 != r3) goto L_0x0554
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                int r2 = r2.type
                if (r2 == 0) goto L_0x0528
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                int r2 = r2.type
                if (r2 != r8) goto L_0x04b2
                goto L_0x0528
            L_0x04b2:
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                int r2 = r2.type
                java.lang.String r3 = ""
                if (r2 != r12) goto L_0x04f4
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                int r2 = r2.addNewRow
                if (r2 == r7) goto L_0x04e6
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                boolean r2 = r2.isChannel
                if (r2 == 0) goto L_0x04d9
                r2 = 2131624625(0x7f0e02b1, float:1.8876435E38)
                java.lang.String r3 = "ChannelAdminsInfo"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2)
                goto L_0x04e9
            L_0x04d9:
                r2 = 2131625876(0x7f0e0794, float:1.8878972E38)
                java.lang.String r3 = "MegaAdminsInfo"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2)
                goto L_0x04e9
            L_0x04e6:
                r1.setText(r3)
            L_0x04e9:
                android.content.Context r2 = r0.mContext
                android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r2, (int) r6, (java.lang.String) r9)
                r1.setBackgroundDrawable(r2)
                goto L_0x0714
            L_0x04f4:
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                int r2 = r2.type
                if (r2 != r5) goto L_0x0714
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                boolean r2 = r2.isChannel
                if (r2 == 0) goto L_0x051a
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                int r2 = r2.selectType
                if (r2 == 0) goto L_0x050d
                goto L_0x051a
            L_0x050d:
                r2 = 2131624661(0x7f0e02d5, float:1.8876508E38)
                java.lang.String r3 = "ChannelMembersInfo"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2)
                goto L_0x051d
            L_0x051a:
                r1.setText(r3)
            L_0x051d:
                android.content.Context r2 = r0.mContext
                android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r2, (int) r6, (java.lang.String) r9)
                r1.setBackgroundDrawable(r2)
                goto L_0x0714
            L_0x0528:
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                boolean r2 = r2.isChannel
                if (r2 == 0) goto L_0x053d
                r2 = 2131626046(0x7f0e083e, float:1.8879317E38)
                java.lang.String r3 = "NoBlockedChannel2"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2)
                goto L_0x0549
            L_0x053d:
                r2 = 2131626047(0x7f0e083f, float:1.887932E38)
                java.lang.String r3 = "NoBlockedGroup2"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2)
            L_0x0549:
                android.content.Context r2 = r0.mContext
                android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r2, (int) r6, (java.lang.String) r9)
                r1.setBackgroundDrawable(r2)
                goto L_0x0714
            L_0x0554:
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.slowmodeInfoRow
                if (r2 != r3) goto L_0x0714
                android.content.Context r2 = r0.mContext
                android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r2, (int) r4, (java.lang.String) r9)
                r1.setBackgroundDrawable(r2)
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r2.selectedSlowmode
                int r2 = r2.getSecondsForIndex(r3)
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r3 = r3.info
                if (r3 == 0) goto L_0x0592
                if (r2 != 0) goto L_0x057a
                goto L_0x0592
            L_0x057a:
                r3 = 2131627216(0x7f0e0cd0, float:1.888169E38)
                java.lang.Object[] r4 = new java.lang.Object[r12]
                org.telegram.ui.ChatUsersActivity r5 = org.telegram.ui.ChatUsersActivity.this
                java.lang.String r2 = r5.formatSeconds(r2)
                r4[r11] = r2
                java.lang.String r2 = "SlowmodeInfoSelected"
                java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)
                r1.setText(r2)
                goto L_0x0714
            L_0x0592:
                r2 = 2131627215(0x7f0e0ccf, float:1.8881688E38)
                java.lang.String r3 = "SlowmodeInfoOff"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2)
                goto L_0x0714
            L_0x05a0:
                android.view.View r1 = r1.itemView
                org.telegram.ui.Cells.ManageChatUserCell r1 = (org.telegram.ui.Cells.ManageChatUserCell) r1
                java.lang.Integer r3 = java.lang.Integer.valueOf(r18)
                r1.setTag(r3)
                org.telegram.tgnet.TLObject r3 = r0.getItem(r2)
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                int r4 = r4.participantsStartRow
                if (r2 < r4) goto L_0x05dd
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                int r4 = r4.participantsEndRow
                if (r2 >= r4) goto L_0x05dd
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                int r4 = r4.participantsEndRow
                org.telegram.ui.ChatUsersActivity r6 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$Chat r6 = r6.currentChat
                boolean r6 = org.telegram.messenger.ChatObject.isChannel(r6)
                if (r6 == 0) goto L_0x0610
                org.telegram.ui.ChatUsersActivity r6 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$Chat r6 = r6.currentChat
                boolean r6 = r6.megagroup
                if (r6 != 0) goto L_0x0610
            L_0x05db:
                r6 = 1
                goto L_0x0611
            L_0x05dd:
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                int r4 = r4.contactsStartRow
                if (r2 < r4) goto L_0x060a
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                int r4 = r4.contactsEndRow
                if (r2 >= r4) goto L_0x060a
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                int r4 = r4.contactsEndRow
                org.telegram.ui.ChatUsersActivity r6 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$Chat r6 = r6.currentChat
                boolean r6 = org.telegram.messenger.ChatObject.isChannel(r6)
                if (r6 == 0) goto L_0x0610
                org.telegram.ui.ChatUsersActivity r6 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$Chat r6 = r6.currentChat
                boolean r6 = r6.megagroup
                if (r6 != 0) goto L_0x0610
                goto L_0x05db
            L_0x060a:
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                int r4 = r4.botEndRow
            L_0x0610:
                r6 = 0
            L_0x0611:
                boolean r7 = r3 instanceof org.telegram.tgnet.TLRPC$ChannelParticipant
                if (r7 == 0) goto L_0x0628
                org.telegram.tgnet.TLRPC$ChannelParticipant r3 = (org.telegram.tgnet.TLRPC$ChannelParticipant) r3
                int r7 = r3.user_id
                int r9 = r3.kicked_by
                int r13 = r3.promoted_by
                org.telegram.tgnet.TLRPC$TL_chatBannedRights r14 = r3.banned_rights
                int r15 = r3.date
                boolean r5 = r3 instanceof org.telegram.tgnet.TLRPC$TL_channelParticipantBanned
                boolean r11 = r3 instanceof org.telegram.tgnet.TLRPC$TL_channelParticipantCreator
                boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC$TL_channelParticipantAdmin
                goto L_0x0636
            L_0x0628:
                org.telegram.tgnet.TLRPC$ChatParticipant r3 = (org.telegram.tgnet.TLRPC$ChatParticipant) r3
                int r7 = r3.user_id
                int r15 = r3.date
                boolean r11 = r3 instanceof org.telegram.tgnet.TLRPC$TL_chatParticipantCreator
                boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC$TL_chatParticipantAdmin
                r14 = r10
                r5 = 0
                r9 = 0
                r13 = 0
            L_0x0636:
                org.telegram.ui.ChatUsersActivity r10 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.messenger.MessagesController r10 = r10.getMessagesController()
                java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
                org.telegram.tgnet.TLRPC$User r7 = r10.getUser(r7)
                if (r7 == 0) goto L_0x0714
                org.telegram.ui.ChatUsersActivity r10 = org.telegram.ui.ChatUsersActivity.this
                int r10 = r10.type
                if (r10 != r8) goto L_0x0661
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                java.lang.String r3 = r3.formatUserPermissions(r14)
                int r4 = r4 - r12
                if (r2 == r4) goto L_0x065a
                r2 = 0
                r11 = 1
                goto L_0x065c
            L_0x065a:
                r2 = 0
                r11 = 0
            L_0x065c:
                r1.setData(r7, r2, r3, r11)
                goto L_0x0714
            L_0x0661:
                org.telegram.ui.ChatUsersActivity r8 = org.telegram.ui.ChatUsersActivity.this
                int r8 = r8.type
                if (r8 != 0) goto L_0x069c
                if (r5 == 0) goto L_0x068e
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.messenger.MessagesController r3 = r3.getMessagesController()
                java.lang.Integer r5 = java.lang.Integer.valueOf(r9)
                org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r5)
                if (r3 == 0) goto L_0x068e
                r5 = 2131627541(0x7f0e0e15, float:1.888235E38)
                java.lang.Object[] r6 = new java.lang.Object[r12]
                java.lang.String r3 = org.telegram.messenger.UserObject.getUserName(r3)
                r8 = 0
                r6[r8] = r3
                java.lang.String r3 = "UserRemovedBy"
                java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r5, r6)
                goto L_0x068f
            L_0x068e:
                r3 = 0
            L_0x068f:
                int r4 = r4 - r12
                if (r2 == r4) goto L_0x0695
                r2 = 0
                r11 = 1
                goto L_0x0697
            L_0x0695:
                r2 = 0
                r11 = 0
            L_0x0697:
                r1.setData(r7, r2, r3, r11)
                goto L_0x0714
            L_0x069c:
                org.telegram.ui.ChatUsersActivity r5 = org.telegram.ui.ChatUsersActivity.this
                int r5 = r5.type
                if (r5 != r12) goto L_0x06f4
                if (r11 == 0) goto L_0x06b1
                r3 = 2131624640(0x7f0e02c0, float:1.8876465E38)
                java.lang.String r5 = "ChannelCreator"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r5, r3)
            L_0x06af:
                r8 = 0
                goto L_0x06e8
            L_0x06b1:
                if (r3 == 0) goto L_0x06e6
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.messenger.MessagesController r3 = r3.getMessagesController()
                java.lang.Integer r5 = java.lang.Integer.valueOf(r13)
                org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r5)
                if (r3 == 0) goto L_0x06e6
                int r5 = r3.id
                int r6 = r7.id
                if (r5 != r6) goto L_0x06d3
                r3 = 2131624623(0x7f0e02af, float:1.887643E38)
                java.lang.String r5 = "ChannelAdministrator"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r5, r3)
                goto L_0x06af
            L_0x06d3:
                r5 = 2131625141(0x7f0e04b5, float:1.8877482E38)
                java.lang.Object[] r6 = new java.lang.Object[r12]
                java.lang.String r3 = org.telegram.messenger.UserObject.getUserName(r3)
                r8 = 0
                r6[r8] = r3
                java.lang.String r3 = "EditAdminPromotedBy"
                java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r5, r6)
                goto L_0x06e8
            L_0x06e6:
                r8 = 0
                r3 = 0
            L_0x06e8:
                int r4 = r4 - r12
                if (r2 == r4) goto L_0x06ee
                r2 = 0
                r11 = 1
                goto L_0x06f0
            L_0x06ee:
                r2 = 0
                r11 = 0
            L_0x06f0:
                r1.setData(r7, r2, r3, r11)
                goto L_0x0714
            L_0x06f4:
                r8 = 0
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.type
                r5 = 2
                if (r3 != r5) goto L_0x0714
                if (r6 == 0) goto L_0x0708
                if (r15 == 0) goto L_0x0708
                long r5 = (long) r15
                java.lang.String r3 = org.telegram.messenger.LocaleController.formatJoined(r5)
                goto L_0x0709
            L_0x0708:
                r3 = 0
            L_0x0709:
                int r4 = r4 - r12
                if (r2 == r4) goto L_0x070f
                r2 = 0
                r11 = 1
                goto L_0x0711
            L_0x070f:
                r2 = 0
                r11 = 0
            L_0x0711:
                r1.setData(r7, r2, r3, r11)
            L_0x0714:
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
            if (i == ChatUsersActivity.this.addNewRow || i == ChatUsersActivity.this.addNew2Row || i == ChatUsersActivity.this.recentActionsRow) {
                return 2;
            }
            if ((i >= ChatUsersActivity.this.participantsStartRow && i < ChatUsersActivity.this.participantsEndRow) || ((i >= ChatUsersActivity.this.botStartRow && i < ChatUsersActivity.this.botEndRow) || (i >= ChatUsersActivity.this.contactsStartRow && i < ChatUsersActivity.this.contactsEndRow))) {
                return 0;
            }
            if (i == ChatUsersActivity.this.addNewSectionRow || i == ChatUsersActivity.this.participantsDividerRow || i == ChatUsersActivity.this.participantsDivider2Row) {
                return 3;
            }
            if (i == ChatUsersActivity.this.restricted1SectionRow || i == ChatUsersActivity.this.permissionsSectionRow || i == ChatUsersActivity.this.slowmodeRow) {
                return 5;
            }
            if (i == ChatUsersActivity.this.participantsInfoRow || i == ChatUsersActivity.this.slowmodeInfoRow) {
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
            if (i == ChatUsersActivity.this.membersHeaderRow || i == ChatUsersActivity.this.contactsHeaderRow || i == ChatUsersActivity.this.botHeaderRow) {
                return 8;
            }
            if (i == ChatUsersActivity.this.slowmodeSelectRow) {
                return 9;
            }
            if (i == ChatUsersActivity.this.loadingProgressRow) {
                return 10;
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

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        $$Lambda$ChatUsersActivity$Tj71o_VdBhFaF8SqZxr8Ps6aDVc r11 = new ThemeDescription.ThemeDescriptionDelegate() {
            public final void didSetColor() {
                ChatUsersActivity.this.lambda$getThemeDescriptions$18$ChatUsersActivity();
            }
        };
        if (this.selectType == 4) {
            arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "voipgroup_actionBar"));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "voipgroup_listViewBackground"));
            arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "voipgroup_actionBar"));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "voipgroup_actionBarItems"));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "voipgroup_actionBarItems"));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "voipgroup_actionBarItemsSelector"));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "voipgroup_actionBarItems"));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "voipgroup_actionBarItems"));
            arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{GraySectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "voipgroup_nameText"));
            arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GraySectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "voipgroup_emptyView"));
            arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "voipgroup_nameText"));
            $$Lambda$ChatUsersActivity$Tj71o_VdBhFaF8SqZxr8Ps6aDVc r9 = r11;
            arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) r9, "voipgroup_nameText"));
            arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) r9, "voipgroup_blueText"));
            arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerExtraPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "voipgroup_listViewBackground"));
        } else {
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
            ThemeDescription themeDescription = r2;
            ThemeDescription themeDescription2 = new ThemeDescription((View) this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) r11, "windowBackgroundWhiteGrayText");
            arrayList.add(themeDescription);
            arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) r11, "windowBackgroundWhiteBlueText"));
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
        }
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, (Paint) null, Theme.avatarDrawables, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_text"));
        $$Lambda$ChatUsersActivity$Tj71o_VdBhFaF8SqZxr8Ps6aDVc r8 = r11;
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
    /* renamed from: lambda$getThemeDescriptions$18 */
    public /* synthetic */ void lambda$getThemeDescriptions$18$ChatUsersActivity() {
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
