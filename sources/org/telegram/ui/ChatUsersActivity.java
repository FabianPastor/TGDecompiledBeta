package org.telegram.ui;

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
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$ChannelParticipant;
import org.telegram.tgnet.TLRPC$ChannelParticipantsFilter;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$ChatParticipant;
import org.telegram.tgnet.TLRPC$TL_channelFull;
import org.telegram.tgnet.TLRPC$TL_channelParticipant;
import org.telegram.tgnet.TLRPC$TL_channelParticipantAdmin;
import org.telegram.tgnet.TLRPC$TL_channelParticipantBanned;
import org.telegram.tgnet.TLRPC$TL_channelParticipantCreator;
import org.telegram.tgnet.TLRPC$TL_channelParticipantSelf;
import org.telegram.tgnet.TLRPC$TL_channelParticipantsAdmins;
import org.telegram.tgnet.TLRPC$TL_channelParticipantsBanned;
import org.telegram.tgnet.TLRPC$TL_channelParticipantsBots;
import org.telegram.tgnet.TLRPC$TL_channelParticipantsContacts;
import org.telegram.tgnet.TLRPC$TL_channelParticipantsKicked;
import org.telegram.tgnet.TLRPC$TL_channelParticipantsRecent;
import org.telegram.tgnet.TLRPC$TL_channels_channelParticipants;
import org.telegram.tgnet.TLRPC$TL_channels_editBanned;
import org.telegram.tgnet.TLRPC$TL_channels_getParticipants;
import org.telegram.tgnet.TLRPC$TL_chatAdminRights;
import org.telegram.tgnet.TLRPC$TL_chatBannedRights;
import org.telegram.tgnet.TLRPC$TL_chatParticipant;
import org.telegram.tgnet.TLRPC$TL_chatParticipantAdmin;
import org.telegram.tgnet.TLRPC$TL_chatParticipantCreator;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$Updates;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$UserStatus;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarLayout;
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
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.IntSeekBarAccessibilityDelegate;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SeekBarAccessibilityDelegate;
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
    public EmptyTextProgressView emptyView;
    /* access modifiers changed from: private */
    public boolean firstLoaded;
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
        void didAddParticipantToList(int i, TLObject tLObject);

        void didChangeOwner(TLRPC$User tLRPC$User);
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

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x00a9, code lost:
        if (org.telegram.messenger.ChatObject.canBlockUsers(r0) != false) goto L_0x00ab;
     */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x00cd  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x00f9  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x0109  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x0113  */
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
            if (r3 != r4) goto L_0x013c
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
            if (r0 == 0) goto L_0x0113
            boolean r0 = r6.firstLoaded
            if (r0 != 0) goto L_0x0113
            int r0 = r6.rowCount
            int r1 = r0 + 1
            r6.rowCount = r1
            r6.loadingProgressRow = r0
            goto L_0x02a8
        L_0x0113:
            java.util.ArrayList<org.telegram.tgnet.TLObject> r0 = r6.participants
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x012a
            int r0 = r6.rowCount
            r6.participantsStartRow = r0
            java.util.ArrayList<org.telegram.tgnet.TLObject> r2 = r6.participants
            int r2 = r2.size()
            int r0 = r0 + r2
            r6.rowCount = r0
            r6.participantsEndRow = r0
        L_0x012a:
            int r0 = r6.addNewRow
            if (r0 != r1) goto L_0x0132
            int r0 = r6.participantsStartRow
            if (r0 == r1) goto L_0x02a8
        L_0x0132:
            int r0 = r6.rowCount
            int r1 = r0 + 1
            r6.rowCount = r1
            r6.addNewSectionRow = r0
            goto L_0x02a8
        L_0x013c:
            if (r3 != 0) goto L_0x01b1
            boolean r0 = org.telegram.messenger.ChatObject.canBlockUsers(r0)
            if (r0 == 0) goto L_0x015c
            int r0 = r6.rowCount
            int r2 = r0 + 1
            r6.rowCount = r2
            r6.addNewRow = r0
            java.util.ArrayList<org.telegram.tgnet.TLObject> r0 = r6.participants
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x015c
            int r0 = r6.rowCount
            int r2 = r0 + 1
            r6.rowCount = r2
            r6.participantsInfoRow = r0
        L_0x015c:
            boolean r0 = r6.loadingUsers
            if (r0 == 0) goto L_0x016e
            boolean r0 = r6.firstLoaded
            if (r0 != 0) goto L_0x016e
            int r0 = r6.rowCount
            int r1 = r0 + 1
            r6.rowCount = r1
            r6.loadingProgressRow = r0
            goto L_0x02a8
        L_0x016e:
            java.util.ArrayList<org.telegram.tgnet.TLObject> r0 = r6.participants
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x018b
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
        L_0x018b:
            int r0 = r6.participantsStartRow
            if (r0 == r1) goto L_0x01a7
            int r0 = r6.participantsInfoRow
            if (r0 != r1) goto L_0x019d
            int r0 = r6.rowCount
            int r1 = r0 + 1
            r6.rowCount = r1
            r6.participantsInfoRow = r0
            goto L_0x02a8
        L_0x019d:
            int r0 = r6.rowCount
            int r1 = r0 + 1
            r6.rowCount = r1
            r6.addNewSectionRow = r0
            goto L_0x02a8
        L_0x01a7:
            int r0 = r6.rowCount
            int r1 = r0 + 1
            r6.rowCount = r1
            r6.blockedEmptyRow = r0
            goto L_0x02a8
        L_0x01b1:
            if (r3 != r5) goto L_0x021a
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r0 == 0) goto L_0x01d7
            org.telegram.tgnet.TLRPC$Chat r0 = r6.currentChat
            boolean r0 = r0.megagroup
            if (r0 == 0) goto L_0x01d7
            org.telegram.tgnet.TLRPC$ChatFull r0 = r6.info
            if (r0 == 0) goto L_0x01c9
            int r0 = r0.participants_count
            r1 = 200(0xc8, float:2.8E-43)
            if (r0 > r1) goto L_0x01d7
        L_0x01c9:
            int r0 = r6.rowCount
            int r1 = r0 + 1
            r6.rowCount = r1
            r6.recentActionsRow = r0
            int r0 = r1 + 1
            r6.rowCount = r0
            r6.addNewSectionRow = r1
        L_0x01d7:
            org.telegram.tgnet.TLRPC$Chat r0 = r6.currentChat
            boolean r0 = org.telegram.messenger.ChatObject.canAddAdmins(r0)
            if (r0 == 0) goto L_0x01e7
            int r0 = r6.rowCount
            int r1 = r0 + 1
            r6.rowCount = r1
            r6.addNewRow = r0
        L_0x01e7:
            boolean r0 = r6.loadingUsers
            if (r0 == 0) goto L_0x01f9
            boolean r0 = r6.firstLoaded
            if (r0 != 0) goto L_0x01f9
            int r0 = r6.rowCount
            int r1 = r0 + 1
            r6.rowCount = r1
            r6.loadingProgressRow = r0
            goto L_0x02a8
        L_0x01f9:
            java.util.ArrayList<org.telegram.tgnet.TLObject> r0 = r6.participants
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x0210
            int r0 = r6.rowCount
            r6.participantsStartRow = r0
            java.util.ArrayList<org.telegram.tgnet.TLObject> r1 = r6.participants
            int r1 = r1.size()
            int r0 = r0 + r1
            r6.rowCount = r0
            r6.participantsEndRow = r0
        L_0x0210:
            int r0 = r6.rowCount
            int r1 = r0 + 1
            r6.rowCount = r1
            r6.participantsInfoRow = r0
            goto L_0x02a8
        L_0x021a:
            r1 = 2
            if (r3 != r1) goto L_0x02a8
            int r1 = r6.selectType
            if (r1 != 0) goto L_0x022f
            boolean r0 = org.telegram.messenger.ChatObject.canAddUsers(r0)
            if (r0 == 0) goto L_0x022f
            int r0 = r6.rowCount
            int r1 = r0 + 1
            r6.rowCount = r1
            r6.addNewRow = r0
        L_0x022f:
            boolean r0 = r6.loadingUsers
            if (r0 == 0) goto L_0x0240
            boolean r0 = r6.firstLoaded
            if (r0 != 0) goto L_0x0240
            int r0 = r6.rowCount
            int r1 = r0 + 1
            r6.rowCount = r1
            r6.loadingProgressRow = r0
            goto L_0x02a8
        L_0x0240:
            java.util.ArrayList<org.telegram.tgnet.TLObject> r0 = r6.contacts
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x025e
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
        L_0x025e:
            java.util.ArrayList<org.telegram.tgnet.TLObject> r0 = r6.bots
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x027c
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
            goto L_0x027d
        L_0x027c:
            r5 = r2
        L_0x027d:
            java.util.ArrayList<org.telegram.tgnet.TLObject> r0 = r6.participants
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x029e
            if (r5 == 0) goto L_0x028f
            int r0 = r6.rowCount
            int r1 = r0 + 1
            r6.rowCount = r1
            r6.membersHeaderRow = r0
        L_0x028f:
            int r0 = r6.rowCount
            r6.participantsStartRow = r0
            java.util.ArrayList<org.telegram.tgnet.TLObject> r1 = r6.participants
            int r1 = r1.size()
            int r0 = r0 + r1
            r6.rowCount = r0
            r6.participantsEndRow = r0
        L_0x029e:
            int r0 = r6.rowCount
            if (r0 == 0) goto L_0x02a8
            int r1 = r0 + 1
            r6.rowCount = r1
            r6.participantsInfoRow = r0
        L_0x02a8:
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
                    ChatUsersActivity.this.emptyView.setShowAtCenter(true);
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
                    ChatUsersActivity.this.emptyView.setShowAtCenter(false);
                    if (ChatUsersActivity.this.doneItem != null) {
                        ChatUsersActivity.this.doneItem.setVisibility(0);
                    }
                }

                public void onTextChanged(EditText editText) {
                    if (ChatUsersActivity.this.searchListViewAdapter != null) {
                        ChatUsersActivity.this.searchListViewAdapter.searchUsers(editText.getText().toString());
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
        frameLayout.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        FrameLayout frameLayout2 = (FrameLayout) this.fragmentView;
        this.emptyView = new EmptyTextProgressView(context);
        int i5 = this.type;
        if (i5 == 0 || i5 == 2 || i5 == 3) {
            this.emptyView.setText(LocaleController.getString("NoResult", NUM));
        }
        this.emptyView.setShowAtCenter(true);
        this.emptyView.setVisibility(8);
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
        if (!LocaleController.isRTL) {
            i2 = 2;
        }
        recyclerListView3.setVerticalScrollbarPosition(i2);
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
        if (this.loadingUsers) {
            this.emptyView.showProgress();
        } else {
            this.emptyView.showTextView();
        }
        updateRows();
        this.listView.setEmptyView(this.emptyView);
        if (this.needOpenSearch) {
            this.searchItem.openSearch(false);
        }
        return this.fragmentView;
    }

    /* JADX WARNING: Removed duplicated region for block: B:198:0x039c  */
    /* JADX WARNING: Removed duplicated region for block: B:257:0x04cc A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:258:0x04cd  */
    /* JADX WARNING: Removed duplicated region for block: B:277:? A[RETURN, SYNTHETIC] */
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
            if (r1 == 0) goto L_0x029c
            int r6 = r9.addNewRow
            java.lang.String r7 = "type"
            java.lang.String r8 = "chat_id"
            if (r0 != r6) goto L_0x00b7
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
            org.telegram.ui.ChatUsersActivity$3 r0 = new org.telegram.ui.ChatUsersActivity$3
            r0.<init>()
            r1.setDelegate(r0)
            org.telegram.tgnet.TLRPC$ChatFull r0 = r9.info
            r1.setInfo(r0)
            r9.presentFragment(r1)
            goto L_0x00b6
        L_0x0050:
            if (r0 != r5) goto L_0x00b6
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
            r9.presentFragment(r1)
        L_0x00b6:
            return
        L_0x00b7:
            int r6 = r9.recentActionsRow
            if (r0 != r6) goto L_0x00c6
            org.telegram.ui.ChannelAdminLogActivity r0 = new org.telegram.ui.ChannelAdminLogActivity
            org.telegram.tgnet.TLRPC$Chat r1 = r9.currentChat
            r0.<init>(r1)
            r9.presentFragment(r0)
            return
        L_0x00c6:
            int r6 = r9.removedUsersRow
            if (r0 != r6) goto L_0x00e5
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
        L_0x00e5:
            int r6 = r9.addNew2Row
            if (r0 != r6) goto L_0x00f4
            org.telegram.ui.GroupInviteActivity r0 = new org.telegram.ui.GroupInviteActivity
            int r1 = r9.chatId
            r0.<init>(r1)
            r9.presentFragment(r0)
            return
        L_0x00f4:
            int r6 = r9.permissionsSectionRow
            if (r0 <= r6) goto L_0x029c
            int r6 = r9.changeInfoRow
            if (r0 > r6) goto L_0x029c
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
            r1 = 2131625065(0x7f0e0469, float:1.8877327E38)
            java.lang.String r2 = "EditCantEditPermissionsPublic"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            android.widget.Toast r0 = android.widget.Toast.makeText(r0, r1, r3)
            r0.show()
            goto L_0x0148
        L_0x0134:
            android.app.Activity r0 = r21.getParentActivity()
            r1 = 2131625064(0x7f0e0468, float:1.8877325E38)
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
            goto L_0x029b
        L_0x015e:
            int r2 = r9.addUsersRow
            if (r0 != r2) goto L_0x016b
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r0 = r9.defaultBannedRights
            boolean r1 = r0.invite_users
            r1 = r1 ^ r4
            r0.invite_users = r1
            goto L_0x029b
        L_0x016b:
            int r2 = r9.pinMessagesRow
            if (r0 != r2) goto L_0x0178
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r0 = r9.defaultBannedRights
            boolean r1 = r0.pin_messages
            r1 = r1 ^ r4
            r0.pin_messages = r1
            goto L_0x029b
        L_0x0178:
            boolean r1 = r1.isChecked()
            r1 = r1 ^ r4
            int r2 = r9.sendMessagesRow
            if (r0 != r2) goto L_0x0189
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r0 = r9.defaultBannedRights
            boolean r2 = r0.send_messages
            r2 = r2 ^ r4
            r0.send_messages = r2
            goto L_0x01be
        L_0x0189:
            int r2 = r9.sendMediaRow
            if (r0 != r2) goto L_0x0195
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r0 = r9.defaultBannedRights
            boolean r2 = r0.send_media
            r2 = r2 ^ r4
            r0.send_media = r2
            goto L_0x01be
        L_0x0195:
            int r2 = r9.sendStickersRow
            if (r0 != r2) goto L_0x01a7
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r0 = r9.defaultBannedRights
            boolean r2 = r0.send_stickers
            r2 = r2 ^ r4
            r0.send_inline = r2
            r0.send_gifs = r2
            r0.send_games = r2
            r0.send_stickers = r2
            goto L_0x01be
        L_0x01a7:
            int r2 = r9.embedLinksRow
            if (r0 != r2) goto L_0x01b3
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r0 = r9.defaultBannedRights
            boolean r2 = r0.embed_links
            r2 = r2 ^ r4
            r0.embed_links = r2
            goto L_0x01be
        L_0x01b3:
            int r2 = r9.sendPollsRow
            if (r0 != r2) goto L_0x01be
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r0 = r9.defaultBannedRights
            boolean r2 = r0.send_polls
            r2 = r2 ^ r4
            r0.send_polls = r2
        L_0x01be:
            if (r1 == 0) goto L_0x0270
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r0 = r9.defaultBannedRights
            boolean r1 = r0.view_messages
            if (r1 == 0) goto L_0x01dd
            boolean r1 = r0.send_messages
            if (r1 != 0) goto L_0x01dd
            r0.send_messages = r4
            org.telegram.ui.Components.RecyclerListView r0 = r9.listView
            int r1 = r9.sendMessagesRow
            androidx.recyclerview.widget.RecyclerView$ViewHolder r0 = r0.findViewHolderForAdapterPosition(r1)
            if (r0 == 0) goto L_0x01dd
            android.view.View r0 = r0.itemView
            org.telegram.ui.Cells.TextCheckCell2 r0 = (org.telegram.ui.Cells.TextCheckCell2) r0
            r0.setChecked(r3)
        L_0x01dd:
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r0 = r9.defaultBannedRights
            boolean r1 = r0.view_messages
            if (r1 != 0) goto L_0x01e7
            boolean r0 = r0.send_messages
            if (r0 == 0) goto L_0x0200
        L_0x01e7:
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r0 = r9.defaultBannedRights
            boolean r1 = r0.send_media
            if (r1 != 0) goto L_0x0200
            r0.send_media = r4
            org.telegram.ui.Components.RecyclerListView r0 = r9.listView
            int r1 = r9.sendMediaRow
            androidx.recyclerview.widget.RecyclerView$ViewHolder r0 = r0.findViewHolderForAdapterPosition(r1)
            if (r0 == 0) goto L_0x0200
            android.view.View r0 = r0.itemView
            org.telegram.ui.Cells.TextCheckCell2 r0 = (org.telegram.ui.Cells.TextCheckCell2) r0
            r0.setChecked(r3)
        L_0x0200:
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r0 = r9.defaultBannedRights
            boolean r1 = r0.view_messages
            if (r1 != 0) goto L_0x020a
            boolean r0 = r0.send_messages
            if (r0 == 0) goto L_0x0223
        L_0x020a:
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r0 = r9.defaultBannedRights
            boolean r1 = r0.send_polls
            if (r1 != 0) goto L_0x0223
            r0.send_polls = r4
            org.telegram.ui.Components.RecyclerListView r0 = r9.listView
            int r1 = r9.sendPollsRow
            androidx.recyclerview.widget.RecyclerView$ViewHolder r0 = r0.findViewHolderForAdapterPosition(r1)
            if (r0 == 0) goto L_0x0223
            android.view.View r0 = r0.itemView
            org.telegram.ui.Cells.TextCheckCell2 r0 = (org.telegram.ui.Cells.TextCheckCell2) r0
            r0.setChecked(r3)
        L_0x0223:
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r0 = r9.defaultBannedRights
            boolean r1 = r0.view_messages
            if (r1 != 0) goto L_0x022d
            boolean r0 = r0.send_messages
            if (r0 == 0) goto L_0x024c
        L_0x022d:
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r0 = r9.defaultBannedRights
            boolean r1 = r0.send_stickers
            if (r1 != 0) goto L_0x024c
            r0.send_inline = r4
            r0.send_gifs = r4
            r0.send_games = r4
            r0.send_stickers = r4
            org.telegram.ui.Components.RecyclerListView r0 = r9.listView
            int r1 = r9.sendStickersRow
            androidx.recyclerview.widget.RecyclerView$ViewHolder r0 = r0.findViewHolderForAdapterPosition(r1)
            if (r0 == 0) goto L_0x024c
            android.view.View r0 = r0.itemView
            org.telegram.ui.Cells.TextCheckCell2 r0 = (org.telegram.ui.Cells.TextCheckCell2) r0
            r0.setChecked(r3)
        L_0x024c:
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r0 = r9.defaultBannedRights
            boolean r1 = r0.view_messages
            if (r1 != 0) goto L_0x0256
            boolean r0 = r0.send_messages
            if (r0 == 0) goto L_0x029b
        L_0x0256:
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r0 = r9.defaultBannedRights
            boolean r1 = r0.embed_links
            if (r1 != 0) goto L_0x029b
            r0.embed_links = r4
            org.telegram.ui.Components.RecyclerListView r0 = r9.listView
            int r1 = r9.embedLinksRow
            androidx.recyclerview.widget.RecyclerView$ViewHolder r0 = r0.findViewHolderForAdapterPosition(r1)
            if (r0 == 0) goto L_0x029b
            android.view.View r0 = r0.itemView
            org.telegram.ui.Cells.TextCheckCell2 r0 = (org.telegram.ui.Cells.TextCheckCell2) r0
            r0.setChecked(r3)
            goto L_0x029b
        L_0x0270:
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r0 = r9.defaultBannedRights
            boolean r1 = r0.embed_links
            if (r1 == 0) goto L_0x0282
            boolean r1 = r0.send_inline
            if (r1 == 0) goto L_0x0282
            boolean r1 = r0.send_media
            if (r1 == 0) goto L_0x0282
            boolean r0 = r0.send_polls
            if (r0 != 0) goto L_0x029b
        L_0x0282:
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r0 = r9.defaultBannedRights
            boolean r1 = r0.send_messages
            if (r1 == 0) goto L_0x029b
            r0.send_messages = r3
            org.telegram.ui.Components.RecyclerListView r0 = r9.listView
            int r1 = r9.sendMessagesRow
            androidx.recyclerview.widget.RecyclerView$ViewHolder r0 = r0.findViewHolderForAdapterPosition(r1)
            if (r0 == 0) goto L_0x029b
            android.view.View r0 = r0.itemView
            org.telegram.ui.Cells.TextCheckCell2 r0 = (org.telegram.ui.Cells.TextCheckCell2) r0
            r0.setChecked(r4)
        L_0x029b:
            return
        L_0x029c:
            r8 = 0
            java.lang.String r6 = ""
            if (r1 == 0) goto L_0x031e
            org.telegram.ui.ChatUsersActivity$ListAdapter r1 = r9.listViewAdapter
            org.telegram.tgnet.TLObject r0 = r1.getItem(r0)
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$ChannelParticipant
            if (r1 == 0) goto L_0x02e4
            r1 = r0
            org.telegram.tgnet.TLRPC$ChannelParticipant r1 = (org.telegram.tgnet.TLRPC$ChannelParticipant) r1
            int r6 = r1.user_id
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r7 = r1.banned_rights
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r10 = r1.admin_rights
            java.lang.String r11 = r1.rank
            boolean r12 = r1 instanceof org.telegram.tgnet.TLRPC$TL_channelParticipantAdmin
            if (r12 != 0) goto L_0x02be
            boolean r12 = r1 instanceof org.telegram.tgnet.TLRPC$TL_channelParticipantCreator
            if (r12 == 0) goto L_0x02c2
        L_0x02be:
            boolean r1 = r1.can_edit
            if (r1 == 0) goto L_0x02c4
        L_0x02c2:
            r1 = 1
            goto L_0x02c5
        L_0x02c4:
            r1 = 0
        L_0x02c5:
            boolean r12 = r0 instanceof org.telegram.tgnet.TLRPC$TL_channelParticipantCreator
            if (r12 == 0) goto L_0x02de
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
        L_0x02de:
            r13 = r10
            r16 = r11
            r10 = r1
            r11 = r6
            goto L_0x031b
        L_0x02e4:
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$ChatParticipant
            if (r1 == 0) goto L_0x0315
            r1 = r0
            org.telegram.tgnet.TLRPC$ChatParticipant r1 = (org.telegram.tgnet.TLRPC$ChatParticipant) r1
            int r1 = r1.user_id
            org.telegram.tgnet.TLRPC$Chat r7 = r9.currentChat
            boolean r7 = r7.creator
            boolean r10 = r0 instanceof org.telegram.tgnet.TLRPC$TL_chatParticipantCreator
            if (r10 == 0) goto L_0x030b
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
            goto L_0x030c
        L_0x030b:
            r10 = r8
        L_0x030c:
            r11 = r1
            r16 = r6
            r13 = r10
            r6 = r0
            r10 = r7
            r7 = r8
            goto L_0x039a
        L_0x0315:
            r16 = r6
            r7 = r8
            r13 = r7
            r10 = 0
            r11 = 0
        L_0x031b:
            r6 = r0
            goto L_0x039a
        L_0x031e:
            org.telegram.ui.ChatUsersActivity$SearchAdapter r1 = r9.searchListViewAdapter
            org.telegram.tgnet.TLObject r0 = r1.getItem(r0)
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$User
            if (r1 == 0) goto L_0x033d
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            org.telegram.messenger.MessagesController r1 = r21.getMessagesController()
            r1.putUser(r0, r3)
            int r0 = r0.id
            org.telegram.tgnet.TLObject r1 = r9.getAnyParticipant(r0)
            r20 = r1
            r1 = r0
            r0 = r20
            goto L_0x0348
        L_0x033d:
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$ChannelParticipant
            if (r1 != 0) goto L_0x0347
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$ChatParticipant
            if (r1 == 0) goto L_0x0346
            goto L_0x0347
        L_0x0346:
            r0 = r8
        L_0x0347:
            r1 = 0
        L_0x0348:
            boolean r7 = r0 instanceof org.telegram.tgnet.TLRPC$ChannelParticipant
            if (r7 == 0) goto L_0x0376
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$TL_channelParticipantCreator
            if (r1 == 0) goto L_0x0351
            return
        L_0x0351:
            r1 = r0
            org.telegram.tgnet.TLRPC$ChannelParticipant r1 = (org.telegram.tgnet.TLRPC$ChannelParticipant) r1
            int r6 = r1.user_id
            boolean r7 = r1 instanceof org.telegram.tgnet.TLRPC$TL_channelParticipantAdmin
            if (r7 != 0) goto L_0x035e
            boolean r7 = r1 instanceof org.telegram.tgnet.TLRPC$TL_channelParticipantCreator
            if (r7 == 0) goto L_0x0362
        L_0x035e:
            boolean r7 = r1.can_edit
            if (r7 == 0) goto L_0x0364
        L_0x0362:
            r7 = 1
            goto L_0x0365
        L_0x0364:
            r7 = 0
        L_0x0365:
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
            goto L_0x039a
        L_0x0376:
            boolean r7 = r0 instanceof org.telegram.tgnet.TLRPC$ChatParticipant
            if (r7 == 0) goto L_0x038f
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$TL_chatParticipantCreator
            if (r1 == 0) goto L_0x037f
            return
        L_0x037f:
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
            goto L_0x031b
        L_0x038f:
            r11 = r1
            r16 = r6
            r7 = r8
            r13 = r7
            if (r0 != 0) goto L_0x0398
            r10 = 1
            goto L_0x031b
        L_0x0398:
            r10 = 0
            goto L_0x031b
        L_0x039a:
            if (r11 == 0) goto L_0x04df
            int r0 = r9.selectType
            if (r0 == 0) goto L_0x043d
            if (r0 == r2) goto L_0x03aa
            if (r0 != r4) goto L_0x03a5
            goto L_0x03aa
        L_0x03a5:
            r9.removeUser(r11)
            goto L_0x04df
        L_0x03aa:
            int r0 = r9.selectType
            if (r0 == r4) goto L_0x041c
            if (r10 == 0) goto L_0x041c
            boolean r0 = r6 instanceof org.telegram.tgnet.TLRPC$TL_channelParticipantAdmin
            if (r0 != 0) goto L_0x03b8
            boolean r0 = r6 instanceof org.telegram.tgnet.TLRPC$TL_chatParticipantAdmin
            if (r0 == 0) goto L_0x041c
        L_0x03b8:
            org.telegram.messenger.MessagesController r0 = r21.getMessagesController()
            java.lang.Integer r1 = java.lang.Integer.valueOf(r11)
            org.telegram.tgnet.TLRPC$User r2 = r0.getUser(r1)
            org.telegram.ui.ActionBar.AlertDialog$Builder r11 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r0 = r21.getParentActivity()
            r11.<init>((android.content.Context) r0)
            r0 = 2131624216(0x7f0e0118, float:1.8875605E38)
            java.lang.String r1 = "AppName"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r11.setTitle(r0)
            r0 = 2131624169(0x7f0e00e9, float:1.887551E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            java.lang.String r4 = org.telegram.messenger.UserObject.getUserName(r2)
            r1[r3] = r4
            java.lang.String r3 = "AdminWillBeRemoved"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r3, r0, r1)
            r11.setMessage(r0)
            r0 = 2131626099(0x7f0e0873, float:1.8879425E38)
            java.lang.String r1 = "OK"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r1, r0)
            org.telegram.ui.-$$Lambda$ChatUsersActivity$QBAiFB7vQBVLCLASSNAMElkUtoQa9r6rI r14 = new org.telegram.ui.-$$Lambda$ChatUsersActivity$QBAiFB7vQBVLCLASSNAMElkUtoQa9r6rI
            r0 = r14
            r1 = r21
            r3 = r6
            r4 = r13
            r5 = r7
            r6 = r16
            r7 = r10
            r0.<init>(r2, r3, r4, r5, r6, r7)
            r11.setPositiveButton(r12, r14)
            r0 = 2131624518(0x7f0e0246, float:1.8876218E38)
            java.lang.String r1 = "Cancel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r11.setNegativeButton(r0, r8)
            org.telegram.ui.ActionBar.AlertDialog r0 = r11.create()
            r9.showDialog(r0)
            goto L_0x04df
        L_0x041c:
            int r0 = r9.selectType
            if (r0 != r4) goto L_0x0422
            r8 = 0
            goto L_0x0423
        L_0x0422:
            r8 = 1
        L_0x0423:
            int r0 = r9.selectType
            if (r0 == r4) goto L_0x042c
            if (r0 != r2) goto L_0x042a
            goto L_0x042c
        L_0x042a:
            r12 = 0
            goto L_0x042d
        L_0x042c:
            r12 = 1
        L_0x042d:
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
            goto L_0x04df
        L_0x043d:
            int r0 = r9.type
            if (r0 != r4) goto L_0x0457
            org.telegram.messenger.UserConfig r0 = r21.getUserConfig()
            int r0 = r0.getClientUserId()
            if (r11 == r0) goto L_0x0455
            org.telegram.tgnet.TLRPC$Chat r0 = r9.currentChat
            boolean r0 = r0.creator
            if (r0 != 0) goto L_0x0453
            if (r10 == 0) goto L_0x0455
        L_0x0453:
            r0 = 1
            goto L_0x0465
        L_0x0455:
            r0 = 0
            goto L_0x0465
        L_0x0457:
            if (r0 == 0) goto L_0x045f
            if (r0 != r2) goto L_0x045c
            goto L_0x045f
        L_0x045c:
            r18 = 0
            goto L_0x0467
        L_0x045f:
            org.telegram.tgnet.TLRPC$Chat r0 = r9.currentChat
            boolean r0 = org.telegram.messenger.ChatObject.canBlockUsers(r0)
        L_0x0465:
            r18 = r0
        L_0x0467:
            int r0 = r9.type
            if (r0 == 0) goto L_0x04c2
            if (r0 == r4) goto L_0x0471
            boolean r0 = r9.isChannel
            if (r0 != 0) goto L_0x04c2
        L_0x0471:
            int r0 = r9.type
            if (r0 != r5) goto L_0x047a
            int r0 = r9.selectType
            if (r0 != 0) goto L_0x047a
            goto L_0x04c2
        L_0x047a:
            if (r7 != 0) goto L_0x049b
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
            goto L_0x049c
        L_0x049b:
            r15 = r7
        L_0x049c:
            org.telegram.ui.ChatRightsEditActivity r0 = new org.telegram.ui.ChatRightsEditActivity
            int r12 = r9.chatId
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r14 = r9.defaultBannedRights
            int r1 = r9.type
            if (r1 != r4) goto L_0x04a9
            r17 = 0
            goto L_0x04ab
        L_0x04a9:
            r17 = 1
        L_0x04ab:
            if (r6 != 0) goto L_0x04b0
            r19 = 1
            goto L_0x04b2
        L_0x04b0:
            r19 = 0
        L_0x04b2:
            r10 = r0
            r10.<init>(r11, r12, r13, r14, r15, r16, r17, r18, r19)
            org.telegram.ui.ChatUsersActivity$5 r1 = new org.telegram.ui.ChatUsersActivity$5
            r1.<init>(r6)
            r0.setDelegate(r1)
            r9.presentFragment(r0)
            goto L_0x04df
        L_0x04c2:
            org.telegram.messenger.UserConfig r0 = r21.getUserConfig()
            int r0 = r0.getClientUserId()
            if (r11 != r0) goto L_0x04cd
            return
        L_0x04cd:
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.lang.String r1 = "user_id"
            r0.putInt(r1, r11)
            org.telegram.ui.ProfileActivity r1 = new org.telegram.ui.ProfileActivity
            r1.<init>(r0)
            r9.presentFragment(r1)
        L_0x04df:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatUsersActivity.lambda$createView$1$ChatUsersActivity(android.view.View, int):void");
    }

    public /* synthetic */ void lambda$null$0$ChatUsersActivity(TLRPC$User tLRPC$User, TLObject tLObject, TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights, TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights, String str, boolean z, DialogInterface dialogInterface, int i) {
        openRightsEdit(tLRPC$User.id, tLObject, tLRPC$TL_chatAdminRights, tLRPC$TL_chatBannedRights, str, z, this.selectType == 1 ? 0 : 1, false);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0007, code lost:
        r3 = r2.listView.getAdapter();
        r1 = r2.listViewAdapter;
     */
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
                sparseArray.put(clientUserId, tLRPC$TL_channelParticipantAdmin);
                int indexOf2 = arrayList.indexOf(tLObject2);
                if (indexOf2 >= 0) {
                    arrayList.set(indexOf2, tLRPC$TL_channelParticipantAdmin);
                }
            } else {
                z3 = z;
            }
            if (z3) {
                Collections.sort(arrayList, new Comparator() {
                    public final int compare(Object obj, Object obj2) {
                        return ChatUsersActivity.this.lambda$onOwnerChaged$3$ChatUsersActivity((TLObject) obj, (TLObject) obj2);
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
            Collections.sort(this.participants, new Comparator() {
                public final int compare(Object obj, Object obj2) {
                    return ChatUsersActivity.this.lambda$onOwnerChaged$4$ChatUsersActivity((TLObject) obj, (TLObject) obj2);
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

    public /* synthetic */ int lambda$onOwnerChaged$3$ChatUsersActivity(TLObject tLObject, TLObject tLObject2) {
        int channelAdminParticipantType = getChannelAdminParticipantType(tLObject);
        int channelAdminParticipantType2 = getChannelAdminParticipantType(tLObject2);
        if (channelAdminParticipantType > channelAdminParticipantType2) {
            return 1;
        }
        return channelAdminParticipantType < channelAdminParticipantType2 ? -1 : 0;
    }

    public /* synthetic */ int lambda$onOwnerChaged$4$ChatUsersActivity(TLObject tLObject, TLObject tLObject2) {
        int channelAdminParticipantType = getChannelAdminParticipantType(tLObject);
        int channelAdminParticipantType2 = getChannelAdminParticipantType(tLObject2);
        if (channelAdminParticipantType > channelAdminParticipantType2) {
            return 1;
        }
        return channelAdminParticipantType < channelAdminParticipantType2 ? -1 : 0;
    }

    private void openRightsEdit2(final int i, final int i2, TLObject tLObject, TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights, TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights, String str, boolean z, int i3, boolean z2) {
        ChatRightsEditActivity chatRightsEditActivity = new ChatRightsEditActivity(i, this.chatId, tLRPC$TL_chatAdminRights, this.defaultBannedRights, tLRPC$TL_chatBannedRights, str, i3, true, false);
        int i4 = i2;
        final int i5 = i3;
        chatRightsEditActivity.setDelegate(new ChatRightsEditActivity.ChatRightsEditActivityDelegate() {
            public void didSetRights(int i, TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights, TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights, String str) {
                TLRPC$ChatParticipant tLRPC$ChatParticipant;
                TLRPC$ChannelParticipant tLRPC$ChannelParticipant;
                int i2 = i5;
                if (i2 == 0) {
                    for (int i3 = 0; i3 < ChatUsersActivity.this.participants.size(); i3++) {
                        TLObject tLObject = (TLObject) ChatUsersActivity.this.participants.get(i3);
                        if (tLObject instanceof TLRPC$ChannelParticipant) {
                            if (((TLRPC$ChannelParticipant) tLObject).user_id == i) {
                                if (i == 1) {
                                    tLRPC$ChannelParticipant = new TLRPC$TL_channelParticipantAdmin();
                                } else {
                                    tLRPC$ChannelParticipant = new TLRPC$TL_channelParticipant();
                                }
                                tLRPC$ChannelParticipant.admin_rights = tLRPC$TL_chatAdminRights;
                                tLRPC$ChannelParticipant.banned_rights = tLRPC$TL_chatBannedRights;
                                tLRPC$ChannelParticipant.inviter_id = ChatUsersActivity.this.getUserConfig().getClientUserId();
                                tLRPC$ChannelParticipant.user_id = i;
                                tLRPC$ChannelParticipant.date = i2;
                                tLRPC$ChannelParticipant.flags |= 4;
                                tLRPC$ChannelParticipant.rank = str;
                                ChatUsersActivity.this.participants.set(i3, tLRPC$ChannelParticipant);
                                return;
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
                    }
                } else if (i2 == 1 && i == 0) {
                    ChatUsersActivity.this.removeParticipants(i);
                }
            }

            public void didChangeOwner(TLRPC$User tLRPC$User) {
                ChatUsersActivity.this.onOwnerChaged(tLRPC$User);
            }
        });
        presentFragment(chatRightsEditActivity);
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

    /* JADX WARNING: type inference failed for: r5v4 */
    /* JADX WARNING: type inference failed for: r5v5 */
    /* JADX WARNING: type inference failed for: r5v7 */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:119:0x02b7 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:120:0x02b8  */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean createMenuForParticipant(org.telegram.tgnet.TLObject r23, boolean r24) {
        /*
            r22 = this;
            r11 = r22
            r6 = r23
            if (r6 == 0) goto L_0x02ef
            int r1 = r11.selectType
            if (r1 == 0) goto L_0x000c
            goto L_0x02ef
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
            if (r4 == 0) goto L_0x02ed
            org.telegram.messenger.UserConfig r3 = r22.getUserConfig()
            int r3 = r3.getClientUserId()
            if (r4 != r3) goto L_0x0057
            goto L_0x02ed
        L_0x0057:
            int r3 = r11.type
            java.lang.String r12 = "EditAdminRights"
            java.lang.String r13 = "dialogRedIcon"
            java.lang.String r14 = "dialogTextRed2"
            r2 = 2
            r15 = 1
            if (r3 != r2) goto L_0x01d8
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
            boolean r5 = r6 instanceof org.telegram.tgnet.TLRPC$TL_channelParticipantCreator
            if (r5 != 0) goto L_0x0098
            boolean r5 = r6 instanceof org.telegram.tgnet.TLRPC$TL_chatParticipantCreator
            if (r5 != 0) goto L_0x0098
            boolean r5 = r6 instanceof org.telegram.tgnet.TLRPC$TL_chatParticipantAdmin
            if (r5 == 0) goto L_0x009a
        L_0x0098:
            if (r1 == 0) goto L_0x009c
        L_0x009a:
            r5 = 1
            goto L_0x009d
        L_0x009c:
            r5 = 0
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
            r1 = r1 ^ r15
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
            return r15
        L_0x00d1:
            if (r0 == 0) goto L_0x00db
            r0 = 2131625052(0x7f0e045c, float:1.8877301E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r12, r0)
            goto L_0x00e4
        L_0x00db:
            r0 = 2131626857(0x7f0e0b69, float:1.8880962E38)
            java.lang.String r2 = "SetAsAdmin"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
        L_0x00e4:
            r1.add(r0)
            r0 = 2131165247(0x7var_f, float:1.7944706E38)
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            r2 = r19
            r2.add(r0)
            r0 = 0
            java.lang.Integer r12 = java.lang.Integer.valueOf(r0)
            r0 = r20
            r0.add(r12)
            goto L_0x0102
        L_0x00fe:
            r2 = r19
            r0 = r20
        L_0x0102:
            org.telegram.tgnet.TLRPC$Chat r12 = r11.currentChat
            boolean r12 = org.telegram.messenger.ChatObject.canBlockUsers(r12)
            if (r12 == 0) goto L_0x017b
            if (r5 == 0) goto L_0x017b
            if (r24 == 0) goto L_0x010f
            return r15
        L_0x010f:
            boolean r12 = r11.isChannel
            if (r12 != 0) goto L_0x0158
            org.telegram.tgnet.TLRPC$Chat r12 = r11.currentChat
            boolean r12 = org.telegram.messenger.ChatObject.isChannel(r12)
            if (r12 == 0) goto L_0x0139
            r12 = 2131624541(0x7f0e025d, float:1.8876265E38)
            java.lang.String r15 = "ChangePermissions"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r15, r12)
            r1.add(r12)
            r12 = 2131165252(0x7var_, float:1.7944716E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            r2.add(r12)
            r12 = 1
            java.lang.Integer r15 = java.lang.Integer.valueOf(r12)
            r0.add(r15)
        L_0x0139:
            r12 = 2131625599(0x7f0e067f, float:1.887841E38)
            java.lang.String r15 = "KickFromGroup"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r15, r12)
            r1.add(r12)
            r12 = 2131165253(0x7var_, float:1.7944718E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            r2.add(r12)
            r15 = 2
            java.lang.Integer r12 = java.lang.Integer.valueOf(r15)
            r0.add(r12)
            goto L_0x0179
        L_0x0158:
            r12 = 2131165253(0x7var_, float:1.7944718E38)
            r15 = 2131624629(0x7f0e02b5, float:1.8876443E38)
            java.lang.String r12 = "ChannelRemoveUser"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r12, r15)
            r1.add(r12)
            r12 = 2131165253(0x7var_, float:1.7944718E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            r2.add(r12)
            r12 = 2
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            r0.add(r12)
        L_0x0179:
            r12 = 1
            goto L_0x017c
        L_0x017b:
            r12 = 0
        L_0x017c:
            if (r0 == 0) goto L_0x01d6
            boolean r15 = r0.isEmpty()
            if (r15 == 0) goto L_0x0185
            goto L_0x01d6
        L_0x0185:
            org.telegram.ui.ActionBar.AlertDialog$Builder r15 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r6 = r22.getParentActivity()
            r15.<init>((android.content.Context) r6)
            int r6 = r0.size()
            java.lang.CharSequence[] r6 = new java.lang.CharSequence[r6]
            java.lang.Object[] r6 = r1.toArray(r6)
            java.lang.CharSequence[] r6 = (java.lang.CharSequence[]) r6
            int[] r2 = org.telegram.messenger.AndroidUtilities.toIntArray(r2)
            r19 = r13
            org.telegram.ui.-$$Lambda$ChatUsersActivity$YZqfKsthwpHL5NIaJ1wlFL2xzKY r13 = new org.telegram.ui.-$$Lambda$ChatUsersActivity$YZqfKsthwpHL5NIaJ1wlFL2xzKY
            r16 = r0
            r0 = r13
            r17 = r1
            r1 = r22
            r20 = r14
            r14 = r2
            r2 = r16
            r24 = r12
            r12 = r6
            r6 = r23
            r0.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10)
            r15.setItems(r12, r14, r13)
            org.telegram.ui.ActionBar.AlertDialog r0 = r15.create()
            r11.showDialog(r0)
            if (r24 == 0) goto L_0x01d3
            int r1 = r17.size()
            r2 = 1
            int r1 = r1 - r2
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r20)
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r19)
            r0.setItemColor(r1, r2, r3)
        L_0x01d3:
            r2 = 1
            goto L_0x02ec
        L_0x01d6:
            r0 = 0
            return r0
        L_0x01d8:
            r19 = r13
            r20 = r14
            r0 = 3
            r2 = 2131624578(0x7f0e0282, float:1.887634E38)
            java.lang.String r5 = "ChannelDeleteFromList"
            if (r3 != r0) goto L_0x0212
            org.telegram.tgnet.TLRPC$Chat r0 = r11.currentChat
            boolean r0 = org.telegram.messenger.ChatObject.canBlockUsers(r0)
            if (r0 == 0) goto L_0x0212
            if (r24 == 0) goto L_0x01f0
            r0 = 1
            return r0
        L_0x01f0:
            r0 = 1
            r1 = 2
            java.lang.CharSequence[] r3 = new java.lang.CharSequence[r1]
            r6 = 2131624584(0x7f0e0288, float:1.8876352E38)
            java.lang.String r7 = "ChannelEditPermissions"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r7 = 0
            r3[r7] = r6
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r5, r2)
            r3[r0] = r2
            int[] r2 = new int[r1]
            r2 = {NUM, NUM} // fill-array
            r6 = r23
            r13 = r2
            r12 = r3
        L_0x020f:
            r5 = 0
            goto L_0x02b5
        L_0x0212:
            int r0 = r11.type
            if (r0 != 0) goto L_0x0257
            org.telegram.tgnet.TLRPC$Chat r0 = r11.currentChat
            boolean r0 = org.telegram.messenger.ChatObject.canBlockUsers(r0)
            if (r0 == 0) goto L_0x0257
            if (r24 == 0) goto L_0x0222
            r0 = 1
            return r0
        L_0x0222:
            r0 = 2
            java.lang.CharSequence[] r1 = new java.lang.CharSequence[r0]
            org.telegram.tgnet.TLRPC$Chat r0 = r11.currentChat
            boolean r0 = org.telegram.messenger.ChatObject.canAddUsers(r0)
            if (r0 == 0) goto L_0x0241
            boolean r0 = r11.isChannel
            if (r0 == 0) goto L_0x0237
            r0 = 2131624554(0x7f0e026a, float:1.887629E38)
            java.lang.String r3 = "ChannelAddToChannel"
            goto L_0x023c
        L_0x0237:
            r0 = 2131624555(0x7f0e026b, float:1.8876293E38)
            java.lang.String r3 = "ChannelAddToGroup"
        L_0x023c:
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
            goto L_0x0242
        L_0x0241:
            r0 = 0
        L_0x0242:
            r3 = 0
            r1[r3] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r5, r2)
            r2 = 1
            r1[r2] = r0
            r0 = 2
            int[] r0 = new int[r0]
            r0 = {NUM, NUM} // fill-array
            r6 = r23
            r13 = r0
            r12 = r1
            goto L_0x020f
        L_0x0257:
            r2 = 1
            int r0 = r11.type
            if (r0 != r2) goto L_0x02b0
            org.telegram.tgnet.TLRPC$Chat r0 = r11.currentChat
            boolean r0 = org.telegram.messenger.ChatObject.canAddAdmins(r0)
            if (r0 == 0) goto L_0x02b0
            if (r1 == 0) goto L_0x02b0
            if (r24 == 0) goto L_0x0269
            return r2
        L_0x0269:
            org.telegram.tgnet.TLRPC$Chat r0 = r11.currentChat
            boolean r0 = r0.creator
            r2 = 2131624630(0x7f0e02b6, float:1.8876445E38)
            java.lang.String r3 = "ChannelRemoveUserAdmin"
            r6 = r23
            if (r0 != 0) goto L_0x0294
            boolean r0 = r6 instanceof org.telegram.tgnet.TLRPC$TL_channelParticipantCreator
            if (r0 != 0) goto L_0x0280
            if (r1 == 0) goto L_0x0280
            r0 = 1
            r1 = 2
            r5 = 0
            goto L_0x0297
        L_0x0280:
            r0 = 1
            java.lang.CharSequence[] r1 = new java.lang.CharSequence[r0]
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r5 = 0
            r1[r5] = r2
            int[] r2 = new int[r0]
            r3 = 2131165253(0x7var_, float:1.7944718E38)
            r2[r5] = r3
            r12 = r1
            r13 = r2
            goto L_0x02b5
        L_0x0294:
            r0 = 1
            r5 = 0
            r1 = 2
        L_0x0297:
            java.lang.CharSequence[] r7 = new java.lang.CharSequence[r1]
            r13 = 2131625052(0x7f0e045c, float:1.8877301E38)
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r12, r13)
            r7[r5] = r12
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r7[r0] = r2
            int[] r2 = new int[r1]
            r2 = {NUM, NUM} // fill-array
            r13 = r2
            r12 = r7
            goto L_0x02b5
        L_0x02b0:
            r6 = r23
            r5 = 0
            r12 = 0
            r13 = 0
        L_0x02b5:
            if (r12 != 0) goto L_0x02b8
            return r5
        L_0x02b8:
            org.telegram.ui.ActionBar.AlertDialog$Builder r14 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r0 = r22.getParentActivity()
            r14.<init>((android.content.Context) r0)
            org.telegram.ui.-$$Lambda$ChatUsersActivity$Zq3W07Usva2ue5xRNvv7Md_sKfM r15 = new org.telegram.ui.-$$Lambda$ChatUsersActivity$Zq3W07Usva2ue5xRNvv7Md_sKfM
            r0 = r15
            r1 = r22
            r2 = r12
            r3 = r4
            r4 = r8
            r5 = r10
            r6 = r23
            r7 = r9
            r0.<init>(r2, r3, r4, r5, r6, r7)
            r14.setItems(r12, r13, r15)
            org.telegram.ui.ActionBar.AlertDialog r0 = r14.create()
            r11.showDialog(r0)
            int r1 = r11.type
            r2 = 1
            if (r1 != r2) goto L_0x02ec
            int r1 = r12.length
            int r1 = r1 - r2
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r20)
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r19)
            r0.setItemColor(r1, r3, r4)
        L_0x02ec:
            return r2
        L_0x02ed:
            r0 = 0
            return r0
        L_0x02ef:
            r0 = 0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatUsersActivity.createMenuForParticipant(org.telegram.tgnet.TLObject, boolean):boolean");
    }

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

    public /* synthetic */ void lambda$null$5$ChatUsersActivity(int i, int i2, TLObject tLObject, TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights, TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights, String str, boolean z, ArrayList arrayList, int i3, DialogInterface dialogInterface, int i4) {
        openRightsEdit2(i, i2, tLObject, tLRPC$TL_chatAdminRights, tLRPC$TL_chatBannedRights, str, z, ((Integer) arrayList.get(i3)).intValue(), false);
    }

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
                int i8 = this.type;
                if (i8 == 3) {
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
                } else if (i8 == 0) {
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

    public /* synthetic */ void lambda$checkDiscard$11$ChatUsersActivity(DialogInterface dialogInterface, int i) {
        processDone();
    }

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
            this.contactsEndReached = false;
            this.botsEndReached = false;
            loadChatParticipants(i, i2, true);
        }
    }

    private void loadChatParticipants(int i, int i2, boolean z) {
        TLRPC$Chat tLRPC$Chat;
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
                    if (this.selectType == 0 || tLRPC$ChatParticipant2.user_id != i5) {
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
        EmptyTextProgressView emptyTextProgressView = this.emptyView;
        if (emptyTextProgressView != null && !this.firstLoaded) {
            emptyTextProgressView.showProgress();
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

    public /* synthetic */ void lambda$null$16$ChatUsersActivity(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, TLRPC$TL_channels_getParticipants tLRPC$TL_channels_getParticipants) {
        SparseArray<TLObject> sparseArray;
        ArrayList<TLObject> arrayList;
        EmptyTextProgressView emptyTextProgressView;
        ActionBarLayout actionBarLayout = this.parentLayout;
        if (actionBarLayout != null) {
            actionBarLayout.resumeDelayedFragmentAnimation();
        }
        if (tLRPC$TL_error == null) {
            TLRPC$TL_channels_channelParticipants tLRPC$TL_channels_channelParticipants = (TLRPC$TL_channels_channelParticipants) tLObject;
            if (this.type == 1) {
                getMessagesController().processLoadedAdminsResponse(this.chatId, tLRPC$TL_channels_channelParticipants);
            }
            getMessagesController().putUsers(tLRPC$TL_channels_channelParticipants.users, false);
            int clientUserId = getUserConfig().getClientUserId();
            if (this.selectType != 0) {
                int i = 0;
                while (true) {
                    if (i >= tLRPC$TL_channels_channelParticipants.participants.size()) {
                        break;
                    } else if (tLRPC$TL_channels_channelParticipants.participants.get(i).user_id == clientUserId) {
                        tLRPC$TL_channels_channelParticipants.participants.remove(i);
                        break;
                    } else {
                        i++;
                    }
                }
            }
            if (this.type == 2) {
                this.delayResults--;
                TLRPC$ChannelParticipantsFilter tLRPC$ChannelParticipantsFilter = tLRPC$TL_channels_getParticipants.filter;
                if (tLRPC$ChannelParticipantsFilter instanceof TLRPC$TL_channelParticipantsContacts) {
                    arrayList = this.contacts;
                    sparseArray = this.contactsMap;
                } else if (tLRPC$ChannelParticipantsFilter instanceof TLRPC$TL_channelParticipantsBots) {
                    arrayList = this.bots;
                    sparseArray = this.botsMap;
                } else {
                    arrayList = this.participants;
                    sparseArray = this.participantsMap;
                }
                if (this.delayResults <= 0 && (emptyTextProgressView = this.emptyView) != null) {
                    emptyTextProgressView.showTextView();
                }
            } else {
                arrayList = this.participants;
                sparseArray = this.participantsMap;
                sparseArray.clear();
                EmptyTextProgressView emptyTextProgressView2 = this.emptyView;
                if (emptyTextProgressView2 != null) {
                    emptyTextProgressView2.showTextView();
                }
            }
            arrayList.clear();
            arrayList.addAll(tLRPC$TL_channels_channelParticipants.participants);
            int size = tLRPC$TL_channels_channelParticipants.participants.size();
            for (int i2 = 0; i2 < size; i2++) {
                TLRPC$ChannelParticipant tLRPC$ChannelParticipant = tLRPC$TL_channels_channelParticipants.participants.get(i2);
                sparseArray.put(tLRPC$ChannelParticipant.user_id, tLRPC$ChannelParticipant);
            }
            if (this.type == 2) {
                int size2 = this.participants.size();
                int i3 = 0;
                while (i3 < size2) {
                    TLRPC$ChannelParticipant tLRPC$ChannelParticipant2 = (TLRPC$ChannelParticipant) this.participants.get(i3);
                    if ((this.contactsMap.get(tLRPC$ChannelParticipant2.user_id) == null && this.botsMap.get(tLRPC$ChannelParticipant2.user_id) == null && (this.selectType != 1 || !UserObject.isDeleted(getMessagesController().getUser(Integer.valueOf(tLRPC$ChannelParticipant2.user_id))))) ? false : true) {
                        this.participants.remove(i3);
                        this.participantsMap.remove(tLRPC$ChannelParticipant2.user_id);
                        i3--;
                        size2--;
                    }
                    i3++;
                }
            }
            try {
                if ((this.type == 0 || this.type == 3 || this.type == 2) && this.currentChat != null && this.currentChat.megagroup && (this.info instanceof TLRPC$TL_channelFull) && this.info.participants_count <= 200) {
                    Collections.sort(arrayList, new Comparator(getConnectionsManager().getCurrentTime()) {
                        public final /* synthetic */ int f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final int compare(Object obj, Object obj2) {
                            return ChatUsersActivity.this.lambda$null$14$ChatUsersActivity(this.f$1, (TLObject) obj, (TLObject) obj2);
                        }
                    });
                } else if (this.type == 1) {
                    Collections.sort(this.participants, new Comparator() {
                        public final int compare(Object obj, Object obj2) {
                            return ChatUsersActivity.this.lambda$null$15$ChatUsersActivity((TLObject) obj, (TLObject) obj2);
                        }
                    });
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
        if (this.type != 2 || this.delayResults <= 0) {
            this.loadingUsers = false;
            this.firstLoaded = true;
        }
        updateRows();
        ListAdapter listAdapter = this.listViewAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

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

                public /* synthetic */ SparseArray<TLRPC$User> getExcludeUsers() {
                    return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$getExcludeUsers(this);
                }

                public final void onDataSetChanged(int i) {
                    ChatUsersActivity.SearchAdapter.this.lambda$new$0$ChatUsersActivity$SearchAdapter(i);
                }

                public /* synthetic */ void onSetHashtags(ArrayList<SearchAdapterHelper.HashtagObject> arrayList, HashMap<String, SearchAdapterHelper.HashtagObject> hashMap) {
                    SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$onSetHashtags(this, arrayList, hashMap);
                }
            });
        }

        public /* synthetic */ void lambda$new$0$ChatUsersActivity$SearchAdapter(int i) {
            notifyDataSetChanged();
        }

        public void searchUsers(String str) {
            if (this.searchRunnable != null) {
                Utilities.searchQueue.cancelRunnable(this.searchRunnable);
                this.searchRunnable = null;
            }
            if (TextUtils.isEmpty(str)) {
                this.searchResult.clear();
                this.searchResultMap.clear();
                this.searchResultNames.clear();
                this.searchAdapterHelper.mergeResults((ArrayList<TLObject>) null);
                this.searchAdapterHelper.queryServerSearch((String) null, ChatUsersActivity.this.type != 0, false, true, false, false, ChatObject.isChannel(ChatUsersActivity.this.currentChat) ? ChatUsersActivity.this.chatId : 0, false, ChatUsersActivity.this.type, 0);
                notifyDataSetChanged();
                return;
            }
            DispatchQueue dispatchQueue = Utilities.searchQueue;
            $$Lambda$ChatUsersActivity$SearchAdapter$AmcgAizpk7RD_0P1gB9DzNaYag r1 = new Runnable(str) {
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

        /* access modifiers changed from: private */
        /* renamed from: processSearch */
        public void lambda$searchUsers$1$ChatUsersActivity$SearchAdapter(String str) {
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

        public /* synthetic */ void lambda$processSearch$3$ChatUsersActivity$SearchAdapter(String str) {
            ArrayList arrayList = null;
            this.searchRunnable = null;
            ArrayList arrayList2 = (ChatObject.isChannel(ChatUsersActivity.this.currentChat) || ChatUsersActivity.this.info == null) ? null : new ArrayList(ChatUsersActivity.this.info.participants.participants);
            if (ChatUsersActivity.this.selectType == 1) {
                arrayList = new ArrayList(ChatUsersActivity.this.getContactsController().contacts);
            }
            this.searchAdapterHelper.queryServerSearch(str, ChatUsersActivity.this.selectType != 0, false, true, false, false, ChatObject.isChannel(ChatUsersActivity.this.currentChat) ? ChatUsersActivity.this.chatId : 0, false, ChatUsersActivity.this.type, 0);
            if (arrayList2 != null || arrayList != null) {
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
        }

        /* JADX WARNING: type inference failed for: r3v13 */
        /* JADX WARNING: type inference failed for: r3v19 */
        /* JADX WARNING: type inference failed for: r3v24 */
        /* JADX WARNING: type inference failed for: r3v26 */
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

        public /* synthetic */ void lambda$updateSearchResults$4$ChatUsersActivity$SearchAdapter(ArrayList arrayList, SparseArray sparseArray, ArrayList arrayList2, ArrayList arrayList3) {
            if (ChatUsersActivity.this.searching) {
                this.searchResult = arrayList;
                this.searchResultMap = sparseArray;
                this.searchResultNames = arrayList2;
                this.searchAdapterHelper.mergeResults(arrayList);
                if (!ChatObject.isChannel(ChatUsersActivity.this.currentChat)) {
                    ArrayList<TLObject> groupSearch = this.searchAdapterHelper.getGroupSearch();
                    groupSearch.clear();
                    groupSearch.addAll(arrayList3);
                }
                notifyDataSetChanged();
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
        /* JADX WARNING: Removed duplicated region for block: B:56:0x0131 A[ADDED_TO_REGION] */
        /* JADX WARNING: Removed duplicated region for block: B:77:0x0185  */
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
                goto L_0x01b3
            L_0x0011:
                android.view.View r2 = r2.itemView
                org.telegram.ui.Cells.GraySectionCell r2 = (org.telegram.ui.Cells.GraySectionCell) r2
                int r3 = r1.groupStartRow
                if (r0 != r3) goto L_0x006a
                org.telegram.ui.ChatUsersActivity r0 = org.telegram.ui.ChatUsersActivity.this
                int r0 = r0.type
                if (r0 != 0) goto L_0x002f
                r0 = 2131624567(0x7f0e0277, float:1.8876317E38)
                java.lang.String r3 = "ChannelBlockedUsers"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
                r2.setText(r0)
                goto L_0x01b3
            L_0x002f:
                org.telegram.ui.ChatUsersActivity r0 = org.telegram.ui.ChatUsersActivity.this
                int r0 = r0.type
                r3 = 3
                if (r0 != r3) goto L_0x0046
                r0 = 2131624631(0x7f0e02b7, float:1.8876447E38)
                java.lang.String r3 = "ChannelRestrictedUsers"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
                r2.setText(r0)
                goto L_0x01b3
            L_0x0046:
                org.telegram.ui.ChatUsersActivity r0 = org.telegram.ui.ChatUsersActivity.this
                boolean r0 = r0.isChannel
                if (r0 == 0) goto L_0x005c
                r0 = 2131624639(0x7f0e02bf, float:1.8876463E38)
                java.lang.String r3 = "ChannelSubscribers"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
                r2.setText(r0)
                goto L_0x01b3
            L_0x005c:
                r0 = 2131624595(0x7f0e0293, float:1.8876374E38)
                java.lang.String r3 = "ChannelMembers"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
                r2.setText(r0)
                goto L_0x01b3
            L_0x006a:
                int r3 = r1.globalStartRow
                if (r0 != r3) goto L_0x007c
                r0 = 2131625451(0x7f0e05eb, float:1.887811E38)
                java.lang.String r3 = "GlobalSearch"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
                r2.setText(r0)
                goto L_0x01b3
            L_0x007c:
                int r3 = r1.contactsStartRow
                if (r0 != r3) goto L_0x01b3
                r0 = 2131624828(0x7f0e037c, float:1.8876847E38)
                java.lang.String r3 = "Contacts"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
                r2.setText(r0)
                goto L_0x01b3
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
                if (r5 == 0) goto L_0x01b3
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
                if (r0 != 0) goto L_0x0182
                if (r5 == 0) goto L_0x0182
                org.telegram.ui.Adapters.SearchAdapterHelper r0 = r1.searchAdapterHelper
                java.util.ArrayList r0 = r0.getGlobalSearch()
                int r0 = r0.size()
                if (r0 == 0) goto L_0x0182
                int r0 = r0 + r4
                if (r0 <= r8) goto L_0x0182
                org.telegram.ui.Adapters.SearchAdapterHelper r0 = r1.searchAdapterHelper
                java.lang.String r0 = r0.getLastFoundUsername()
                boolean r9 = r0.startsWith(r10)
                if (r9 == 0) goto L_0x0152
                java.lang.String r0 = r0.substring(r4)
            L_0x0152:
                android.text.SpannableStringBuilder r4 = new android.text.SpannableStringBuilder     // Catch:{ Exception -> 0x017d }
                r4.<init>()     // Catch:{ Exception -> 0x017d }
                r4.append(r10)     // Catch:{ Exception -> 0x017d }
                r4.append(r5)     // Catch:{ Exception -> 0x017d }
                int r9 = org.telegram.messenger.AndroidUtilities.indexOfIgnoreCase(r5, r0)     // Catch:{ Exception -> 0x017d }
                if (r9 == r14) goto L_0x017b
                int r0 = r0.length()     // Catch:{ Exception -> 0x017d }
                if (r9 != 0) goto L_0x016c
                int r0 = r0 + 1
                goto L_0x016e
            L_0x016c:
                int r9 = r9 + 1
            L_0x016e:
                android.text.style.ForegroundColorSpan r10 = new android.text.style.ForegroundColorSpan     // Catch:{ Exception -> 0x017d }
                int r15 = org.telegram.ui.ActionBar.Theme.getColor(r13)     // Catch:{ Exception -> 0x017d }
                r10.<init>(r15)     // Catch:{ Exception -> 0x017d }
                int r0 = r0 + r9
                r4.setSpan(r10, r9, r0, r12)     // Catch:{ Exception -> 0x017d }
            L_0x017b:
                r5 = r4
                goto L_0x0183
            L_0x017d:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
                goto L_0x0183
            L_0x0182:
                r5 = r9
            L_0x0183:
                if (r6 == 0) goto L_0x01a5
                java.lang.String r0 = org.telegram.messenger.UserObject.getUserName(r3)
                android.text.SpannableStringBuilder r11 = new android.text.SpannableStringBuilder
                r11.<init>(r0)
                int r0 = org.telegram.messenger.AndroidUtilities.indexOfIgnoreCase(r0, r6)
                if (r0 == r14) goto L_0x01a5
                android.text.style.ForegroundColorSpan r4 = new android.text.style.ForegroundColorSpan
                int r9 = org.telegram.ui.ActionBar.Theme.getColor(r13)
                r4.<init>(r9)
                int r6 = r6.length()
                int r6 = r6 + r0
                r11.setSpan(r4, r0, r6, r12)
            L_0x01a5:
                android.view.View r0 = r2.itemView
                org.telegram.ui.Cells.ManageChatUserCell r0 = (org.telegram.ui.Cells.ManageChatUserCell) r0
                java.lang.Integer r2 = java.lang.Integer.valueOf(r8)
                r0.setTag(r2)
                r0.setData(r3, r11, r5, r7)
            L_0x01b3:
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
                TLObject currentObject = ((ManageChatUserCell) viewHolder.itemView).getCurrentObject();
                return !(currentObject instanceof TLRPC$User) || !((TLRPC$User) currentObject).self;
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

        public /* synthetic */ boolean lambda$onCreateViewHolder$0$ChatUsersActivity$ListAdapter(ManageChatUserCell manageChatUserCell, boolean z) {
            return ChatUsersActivity.this.createMenuForParticipant(ChatUsersActivity.this.listViewAdapter.getItem(((Integer) manageChatUserCell.getTag()).intValue()), !z);
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v2, resolved type: org.telegram.ui.Cells.LoadingCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v2, resolved type: org.telegram.ui.Cells.ManageChatUserCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v6, resolved type: org.telegram.ui.Cells.LoadingCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v5, resolved type: org.telegram.ui.Cells.ManageChatTextCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v13, resolved type: org.telegram.ui.Cells.TextSettingsCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v14, resolved type: org.telegram.ui.Cells.TextCheckCell2} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v17, resolved type: org.telegram.ui.ChatUsersActivity$ChooseView} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v18, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v17, resolved type: org.telegram.ui.Cells.TextInfoPrivacyCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v19, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v18, resolved type: org.telegram.ui.Cells.ShadowSectionCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v19, resolved type: org.telegram.ui.ChatUsersActivity$ListAdapter$1} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v12, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v20, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v21, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v20, resolved type: org.telegram.ui.Cells.GraySectionCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v21, resolved type: org.telegram.ui.Cells.LoadingCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v22, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX WARNING: type inference failed for: r13v4 */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r12, int r13) {
            /*
                r11 = this;
                java.lang.String r12 = "windowBackgroundWhite"
                r0 = 1
                switch(r13) {
                    case 0: goto L_0x0179;
                    case 1: goto L_0x0171;
                    case 2: goto L_0x0161;
                    case 3: goto L_0x0159;
                    case 4: goto L_0x0073;
                    case 5: goto L_0x0056;
                    case 6: goto L_0x0046;
                    case 7: goto L_0x0036;
                    case 8: goto L_0x002d;
                    case 9: goto L_0x0006;
                    case 10: goto L_0x0018;
                    default: goto L_0x0006;
                }
            L_0x0006:
                org.telegram.ui.ChatUsersActivity$ChooseView r13 = new org.telegram.ui.ChatUsersActivity$ChooseView
                org.telegram.ui.ChatUsersActivity r0 = org.telegram.ui.ChatUsersActivity.this
                android.content.Context r1 = r11.mContext
                r13.<init>(r1)
                int r12 = org.telegram.ui.ActionBar.Theme.getColor(r12)
                r13.setBackgroundColor(r12)
                goto L_0x016f
            L_0x0018:
                org.telegram.ui.Cells.LoadingCell r12 = new org.telegram.ui.Cells.LoadingCell
                android.content.Context r13 = r11.mContext
                r0 = 1109393408(0x42200000, float:40.0)
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
                r1 = 1123024896(0x42var_, float:120.0)
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                r12.<init>(r13, r0, r1)
                goto L_0x01c2
            L_0x002d:
                org.telegram.ui.Cells.GraySectionCell r12 = new org.telegram.ui.Cells.GraySectionCell
                android.content.Context r13 = r11.mContext
                r12.<init>(r13)
                goto L_0x01c2
            L_0x0036:
                org.telegram.ui.Cells.TextCheckCell2 r13 = new org.telegram.ui.Cells.TextCheckCell2
                android.content.Context r0 = r11.mContext
                r13.<init>(r0)
                int r12 = org.telegram.ui.ActionBar.Theme.getColor(r12)
                r13.setBackgroundColor(r12)
                goto L_0x016f
            L_0x0046:
                org.telegram.ui.Cells.TextSettingsCell r13 = new org.telegram.ui.Cells.TextSettingsCell
                android.content.Context r0 = r11.mContext
                r13.<init>(r0)
                int r12 = org.telegram.ui.ActionBar.Theme.getColor(r12)
                r13.setBackgroundColor(r12)
                goto L_0x016f
            L_0x0056:
                org.telegram.ui.Cells.HeaderCell r13 = new org.telegram.ui.Cells.HeaderCell
                android.content.Context r1 = r11.mContext
                r3 = 21
                r4 = 11
                r5 = 0
                java.lang.String r2 = "windowBackgroundWhiteBlueHeader"
                r0 = r13
                r0.<init>(r1, r2, r3, r4, r5)
                int r12 = org.telegram.ui.ActionBar.Theme.getColor(r12)
                r13.setBackgroundColor(r12)
                r12 = 43
                r13.setHeight(r12)
                goto L_0x016f
            L_0x0073:
                org.telegram.ui.ChatUsersActivity$ListAdapter$1 r12 = new org.telegram.ui.ChatUsersActivity$ListAdapter$1
                android.content.Context r13 = r11.mContext
                r12.<init>(r11, r13)
                android.content.Context r13 = r11.mContext
                r1 = 2131165413(0x7var_e5, float:1.7945042E38)
                java.lang.String r2 = "windowBackgroundGrayShadow"
                android.graphics.drawable.Drawable r13 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r13, (int) r1, (java.lang.String) r2)
                r12.setBackgroundDrawable(r13)
                android.widget.LinearLayout r13 = new android.widget.LinearLayout
                android.content.Context r1 = r11.mContext
                r13.<init>(r1)
                r13.setOrientation(r0)
                r2 = -2
                r3 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
                r4 = 17
                r5 = 1101004800(0x41a00000, float:20.0)
                r6 = 0
                r7 = 1101004800(0x41a00000, float:20.0)
                r8 = 0
                android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r2, r3, r4, r5, r6, r7, r8)
                r12.addView(r13, r1)
                android.widget.ImageView r1 = new android.widget.ImageView
                android.content.Context r2 = r11.mContext
                r1.<init>(r2)
                r2 = 2131165418(0x7var_ea, float:1.7945053E38)
                r1.setImageResource(r2)
                android.widget.ImageView$ScaleType r2 = android.widget.ImageView.ScaleType.CENTER
                r1.setScaleType(r2)
                android.graphics.PorterDuffColorFilter r2 = new android.graphics.PorterDuffColorFilter
                java.lang.String r3 = "emptyListPlaceholder"
                int r4 = org.telegram.ui.ActionBar.Theme.getColor(r3)
                android.graphics.PorterDuff$Mode r5 = android.graphics.PorterDuff.Mode.MULTIPLY
                r2.<init>(r4, r5)
                r1.setColorFilter(r2)
                r2 = -2
                android.widget.LinearLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r2, (int) r2, (int) r0)
                r13.addView(r1, r2)
                android.widget.TextView r1 = new android.widget.TextView
                android.content.Context r2 = r11.mContext
                r1.<init>(r2)
                r2 = 2131625893(0x7f0e07a5, float:1.8879007E38)
                java.lang.String r4 = "NoBlockedUsers"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r2)
                r1.setText(r2)
                int r2 = org.telegram.ui.ActionBar.Theme.getColor(r3)
                r1.setTextColor(r2)
                r2 = 1098907648(0x41800000, float:16.0)
                r1.setTextSize(r0, r2)
                r1.setGravity(r0)
                java.lang.String r2 = "fonts/rmedium.ttf"
                android.graphics.Typeface r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r2)
                r1.setTypeface(r2)
                r4 = -2
                r5 = -2
                r6 = 1
                r7 = 0
                r8 = 10
                r9 = 0
                r10 = 0
                android.widget.LinearLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r4, (int) r5, (int) r6, (int) r7, (int) r8, (int) r9, (int) r10)
                r13.addView(r1, r2)
                android.widget.TextView r1 = new android.widget.TextView
                android.content.Context r2 = r11.mContext
                r1.<init>(r2)
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                boolean r2 = r2.isChannel
                if (r2 == 0) goto L_0x0124
                r2 = 2131625891(0x7f0e07a3, float:1.8879003E38)
                java.lang.String r4 = "NoBlockedChannel2"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r2)
                r1.setText(r2)
                goto L_0x0130
            L_0x0124:
                r2 = 2131625892(0x7f0e07a4, float:1.8879005E38)
                java.lang.String r4 = "NoBlockedGroup2"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r2)
                r1.setText(r2)
            L_0x0130:
                int r2 = org.telegram.ui.ActionBar.Theme.getColor(r3)
                r1.setTextColor(r2)
                r2 = 1097859072(0x41700000, float:15.0)
                r1.setTextSize(r0, r2)
                r1.setGravity(r0)
                r3 = -2
                r4 = -2
                r5 = 1
                r6 = 0
                r7 = 10
                r8 = 0
                r9 = 0
                android.widget.LinearLayout$LayoutParams r0 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r3, (int) r4, (int) r5, (int) r6, (int) r7, (int) r8, (int) r9)
                r13.addView(r1, r0)
                androidx.recyclerview.widget.RecyclerView$LayoutParams r13 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r0 = -1
                r13.<init>((int) r0, (int) r0)
                r12.setLayoutParams(r13)
                goto L_0x01c2
            L_0x0159:
                org.telegram.ui.Cells.ShadowSectionCell r12 = new org.telegram.ui.Cells.ShadowSectionCell
                android.content.Context r13 = r11.mContext
                r12.<init>(r13)
                goto L_0x01c2
            L_0x0161:
                org.telegram.ui.Cells.ManageChatTextCell r13 = new org.telegram.ui.Cells.ManageChatTextCell
                android.content.Context r0 = r11.mContext
                r13.<init>(r0)
                int r12 = org.telegram.ui.ActionBar.Theme.getColor(r12)
                r13.setBackgroundColor(r12)
            L_0x016f:
                r12 = r13
                goto L_0x01c2
            L_0x0171:
                org.telegram.ui.Cells.TextInfoPrivacyCell r12 = new org.telegram.ui.Cells.TextInfoPrivacyCell
                android.content.Context r13 = r11.mContext
                r12.<init>(r13)
                goto L_0x01c2
            L_0x0179:
                org.telegram.ui.Cells.ManageChatUserCell r13 = new org.telegram.ui.Cells.ManageChatUserCell
                android.content.Context r1 = r11.mContext
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                int r2 = r2.type
                r3 = 6
                r4 = 3
                if (r2 == 0) goto L_0x0192
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                int r2 = r2.type
                if (r2 != r4) goto L_0x0190
                goto L_0x0192
            L_0x0190:
                r2 = 6
                goto L_0x0193
            L_0x0192:
                r2 = 7
            L_0x0193:
                org.telegram.ui.ChatUsersActivity r5 = org.telegram.ui.ChatUsersActivity.this
                int r5 = r5.type
                if (r5 == 0) goto L_0x01a5
                org.telegram.ui.ChatUsersActivity r5 = org.telegram.ui.ChatUsersActivity.this
                int r5 = r5.type
                if (r5 != r4) goto L_0x01a4
                goto L_0x01a5
            L_0x01a4:
                r3 = 2
            L_0x01a5:
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                int r4 = r4.selectType
                if (r4 != 0) goto L_0x01ae
                goto L_0x01af
            L_0x01ae:
                r0 = 0
            L_0x01af:
                r13.<init>(r1, r2, r3, r0)
                int r12 = org.telegram.ui.ActionBar.Theme.getColor(r12)
                r13.setBackgroundColor(r12)
                org.telegram.ui.-$$Lambda$ChatUsersActivity$ListAdapter$eixJJWW-1mDLHNoI-EEjSfsGLFc r12 = new org.telegram.ui.-$$Lambda$ChatUsersActivity$ListAdapter$eixJJWW-1mDLHNoI-EEjSfsGLFc
                r12.<init>()
                r13.setDelegate(r12)
                goto L_0x016f
            L_0x01c2:
                org.telegram.ui.Components.RecyclerListView$Holder r13 = new org.telegram.ui.Components.RecyclerListView$Holder
                r13.<init>(r12)
                return r13
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatUsersActivity.ListAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            int i2;
            boolean z;
            boolean z2;
            TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights;
            int i3;
            int i4;
            int i5;
            boolean z3;
            String str;
            TLRPC$User user;
            TLRPC$User user2;
            RecyclerView.ViewHolder viewHolder2 = viewHolder;
            int i6 = i;
            boolean z4 = false;
            switch (viewHolder.getItemViewType()) {
                case 0:
                    ManageChatUserCell manageChatUserCell = (ManageChatUserCell) viewHolder2.itemView;
                    manageChatUserCell.setTag(Integer.valueOf(i));
                    TLObject item = getItem(i6);
                    if (i6 >= ChatUsersActivity.this.participantsStartRow && i6 < ChatUsersActivity.this.participantsEndRow) {
                        i2 = ChatUsersActivity.this.participantsEndRow;
                    } else if (i6 < ChatUsersActivity.this.contactsStartRow || i6 >= ChatUsersActivity.this.contactsEndRow) {
                        i2 = ChatUsersActivity.this.botEndRow;
                    } else {
                        i2 = ChatUsersActivity.this.contactsEndRow;
                    }
                    if (item instanceof TLRPC$ChannelParticipant) {
                        TLRPC$ChannelParticipant tLRPC$ChannelParticipant = (TLRPC$ChannelParticipant) item;
                        i5 = tLRPC$ChannelParticipant.user_id;
                        i4 = tLRPC$ChannelParticipant.kicked_by;
                        i3 = tLRPC$ChannelParticipant.promoted_by;
                        tLRPC$TL_chatBannedRights = tLRPC$ChannelParticipant.banned_rights;
                        z2 = tLRPC$ChannelParticipant instanceof TLRPC$TL_channelParticipantBanned;
                        z = tLRPC$ChannelParticipant instanceof TLRPC$TL_channelParticipantCreator;
                        z3 = tLRPC$ChannelParticipant instanceof TLRPC$TL_channelParticipantAdmin;
                    } else {
                        TLRPC$ChatParticipant tLRPC$ChatParticipant = (TLRPC$ChatParticipant) item;
                        i5 = tLRPC$ChatParticipant.user_id;
                        z = tLRPC$ChatParticipant instanceof TLRPC$TL_chatParticipantCreator;
                        z3 = tLRPC$ChatParticipant instanceof TLRPC$TL_chatParticipantAdmin;
                        tLRPC$TL_chatBannedRights = null;
                        i4 = 0;
                        i3 = 0;
                        z2 = false;
                    }
                    TLRPC$User user3 = ChatUsersActivity.this.getMessagesController().getUser(Integer.valueOf(i5));
                    if (user3 == null) {
                        return;
                    }
                    if (ChatUsersActivity.this.type == 3) {
                        String access$3400 = ChatUsersActivity.this.formatUserPermissions(tLRPC$TL_chatBannedRights);
                        if (i6 != i2 - 1) {
                            z4 = true;
                        }
                        manageChatUserCell.setData(user3, (CharSequence) null, access$3400, z4);
                        return;
                    } else if (ChatUsersActivity.this.type == 0) {
                        String formatString = (!z2 || (user2 = ChatUsersActivity.this.getMessagesController().getUser(Integer.valueOf(i4))) == null) ? null : LocaleController.formatString("UserRemovedBy", NUM, UserObject.getUserName(user2));
                        if (i6 != i2 - 1) {
                            z4 = true;
                        }
                        manageChatUserCell.setData(user3, (CharSequence) null, formatString, z4);
                        return;
                    } else if (ChatUsersActivity.this.type == 1) {
                        if (z) {
                            str = LocaleController.getString("ChannelCreator", NUM);
                        } else if (!z3 || (user = ChatUsersActivity.this.getMessagesController().getUser(Integer.valueOf(i3))) == null) {
                            str = null;
                        } else {
                            str = user.id == user3.id ? LocaleController.getString("ChannelAdministrator", NUM) : LocaleController.formatString("EditAdminPromotedBy", NUM, UserObject.getUserName(user));
                        }
                        if (i6 != i2 - 1) {
                            z4 = true;
                        }
                        manageChatUserCell.setData(user3, (CharSequence) null, str, z4);
                        return;
                    } else if (ChatUsersActivity.this.type == 2) {
                        if (i6 != i2 - 1) {
                            z4 = true;
                        }
                        manageChatUserCell.setData(user3, (CharSequence) null, (CharSequence) null, z4);
                        return;
                    } else {
                        return;
                    }
                case 1:
                    TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder2.itemView;
                    if (i6 == ChatUsersActivity.this.participantsInfoRow) {
                        if (ChatUsersActivity.this.type == 0 || ChatUsersActivity.this.type == 3) {
                            if (ChatObject.canBlockUsers(ChatUsersActivity.this.currentChat)) {
                                if (ChatUsersActivity.this.isChannel) {
                                    textInfoPrivacyCell.setText(LocaleController.getString("NoBlockedChannel2", NUM));
                                } else {
                                    textInfoPrivacyCell.setText(LocaleController.getString("NoBlockedGroup2", NUM));
                                }
                            } else if (ChatUsersActivity.this.isChannel) {
                                textInfoPrivacyCell.setText(LocaleController.getString("NoBlockedChannel2", NUM));
                            } else {
                                textInfoPrivacyCell.setText(LocaleController.getString("NoBlockedGroup2", NUM));
                            }
                            textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                            return;
                        } else if (ChatUsersActivity.this.type == 1) {
                            if (ChatUsersActivity.this.addNewRow != -1) {
                                if (ChatUsersActivity.this.isChannel) {
                                    textInfoPrivacyCell.setText(LocaleController.getString("ChannelAdminsInfo", NUM));
                                } else {
                                    textInfoPrivacyCell.setText(LocaleController.getString("MegaAdminsInfo", NUM));
                                }
                                textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                                return;
                            }
                            textInfoPrivacyCell.setText("");
                            textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                            return;
                        } else if (ChatUsersActivity.this.type == 2) {
                            if (!ChatUsersActivity.this.isChannel || ChatUsersActivity.this.selectType != 0) {
                                textInfoPrivacyCell.setText("");
                            } else {
                                textInfoPrivacyCell.setText(LocaleController.getString("ChannelMembersInfo", NUM));
                            }
                            textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                            return;
                        } else {
                            return;
                        }
                    } else if (i6 == ChatUsersActivity.this.slowmodeInfoRow) {
                        textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                        ChatUsersActivity chatUsersActivity = ChatUsersActivity.this;
                        int access$300 = chatUsersActivity.getSecondsForIndex(chatUsersActivity.selectedSlowmode);
                        if (ChatUsersActivity.this.info == null || access$300 == 0) {
                            textInfoPrivacyCell.setText(LocaleController.getString("SlowmodeInfoOff", NUM));
                            return;
                        } else {
                            textInfoPrivacyCell.setText(LocaleController.formatString("SlowmodeInfoSelected", NUM, ChatUsersActivity.this.formatSeconds(access$300)));
                            return;
                        }
                    } else {
                        return;
                    }
                case 2:
                    ManageChatTextCell manageChatTextCell = (ManageChatTextCell) viewHolder2.itemView;
                    manageChatTextCell.setColors("windowBackgroundWhiteGrayIcon", "windowBackgroundWhiteBlackText");
                    if (i6 == ChatUsersActivity.this.addNewRow) {
                        if (ChatUsersActivity.this.type == 3) {
                            manageChatTextCell.setColors("windowBackgroundWhiteBlueIcon", "windowBackgroundWhiteBlueButton");
                            String string = LocaleController.getString("ChannelAddException", NUM);
                            if (ChatUsersActivity.this.participantsStartRow != -1) {
                                z4 = true;
                            }
                            manageChatTextCell.setText(string, (String) null, NUM, z4);
                            return;
                        } else if (ChatUsersActivity.this.type == 0) {
                            manageChatTextCell.setText(LocaleController.getString("ChannelBlockUser", NUM), (String) null, NUM, false);
                            return;
                        } else if (ChatUsersActivity.this.type == 1) {
                            manageChatTextCell.setColors("windowBackgroundWhiteBlueIcon", "windowBackgroundWhiteBlueButton");
                            if (!ChatUsersActivity.this.loadingUsers || ChatUsersActivity.this.firstLoaded) {
                                z4 = true;
                            }
                            manageChatTextCell.setText(LocaleController.getString("ChannelAddAdmin", NUM), (String) null, NUM, z4);
                            return;
                        } else if (ChatUsersActivity.this.type == 2) {
                            manageChatTextCell.setColors("windowBackgroundWhiteBlueIcon", "windowBackgroundWhiteBlueButton");
                            if ((!ChatUsersActivity.this.loadingUsers || ChatUsersActivity.this.firstLoaded) && ChatUsersActivity.this.membersHeaderRow == -1 && !ChatUsersActivity.this.participants.isEmpty()) {
                                z4 = true;
                            }
                            if (ChatUsersActivity.this.isChannel) {
                                manageChatTextCell.setText(LocaleController.getString("AddSubscriber", NUM), (String) null, NUM, z4);
                                return;
                            } else {
                                manageChatTextCell.setText(LocaleController.getString("AddMember", NUM), (String) null, NUM, z4);
                                return;
                            }
                        } else {
                            return;
                        }
                    } else if (i6 == ChatUsersActivity.this.recentActionsRow) {
                        manageChatTextCell.setText(LocaleController.getString("EventLog", NUM), (String) null, NUM, false);
                        return;
                    } else if (i6 == ChatUsersActivity.this.addNew2Row) {
                        manageChatTextCell.setText(LocaleController.getString("ChannelInviteViaLink", NUM), (String) null, NUM, true);
                        return;
                    } else {
                        return;
                    }
                case 3:
                    if (i6 == ChatUsersActivity.this.addNewSectionRow || (ChatUsersActivity.this.type == 3 && i6 == ChatUsersActivity.this.participantsDividerRow && ChatUsersActivity.this.addNewRow == -1 && ChatUsersActivity.this.participantsStartRow == -1)) {
                        viewHolder2.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                        return;
                    } else {
                        viewHolder2.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                        return;
                    }
                case 5:
                    HeaderCell headerCell = (HeaderCell) viewHolder2.itemView;
                    if (i6 == ChatUsersActivity.this.restricted1SectionRow) {
                        if (ChatUsersActivity.this.type == 0) {
                            int size = ChatUsersActivity.this.info != null ? ChatUsersActivity.this.info.kicked_count : ChatUsersActivity.this.participants.size();
                            if (size != 0) {
                                headerCell.setText(LocaleController.formatPluralString("RemovedUser", size));
                                return;
                            } else {
                                headerCell.setText(LocaleController.getString("ChannelBlockedUsers", NUM));
                                return;
                            }
                        } else {
                            headerCell.setText(LocaleController.getString("ChannelRestrictedUsers", NUM));
                            return;
                        }
                    } else if (i6 == ChatUsersActivity.this.permissionsSectionRow) {
                        headerCell.setText(LocaleController.getString("ChannelPermissionsHeader", NUM));
                        return;
                    } else if (i6 == ChatUsersActivity.this.slowmodeRow) {
                        headerCell.setText(LocaleController.getString("Slowmode", NUM));
                        return;
                    } else {
                        return;
                    }
                case 6:
                    TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder2.itemView;
                    String string2 = LocaleController.getString("ChannelBlacklist", NUM);
                    Object[] objArr = new Object[1];
                    objArr[0] = Integer.valueOf(ChatUsersActivity.this.info != null ? ChatUsersActivity.this.info.kicked_count : 0);
                    textSettingsCell.setTextAndValue(string2, String.format("%d", objArr), false);
                    return;
                case 7:
                    TextCheckCell2 textCheckCell2 = (TextCheckCell2) viewHolder2.itemView;
                    if (i6 == ChatUsersActivity.this.changeInfoRow) {
                        textCheckCell2.setTextAndCheck(LocaleController.getString("UserRestrictionsChangeInfo", NUM), !ChatUsersActivity.this.defaultBannedRights.change_info && TextUtils.isEmpty(ChatUsersActivity.this.currentChat.username), false);
                    } else if (i6 == ChatUsersActivity.this.addUsersRow) {
                        textCheckCell2.setTextAndCheck(LocaleController.getString("UserRestrictionsInviteUsers", NUM), !ChatUsersActivity.this.defaultBannedRights.invite_users, true);
                    } else if (i6 == ChatUsersActivity.this.pinMessagesRow) {
                        textCheckCell2.setTextAndCheck(LocaleController.getString("UserRestrictionsPinMessages", NUM), !ChatUsersActivity.this.defaultBannedRights.pin_messages && TextUtils.isEmpty(ChatUsersActivity.this.currentChat.username), true);
                    } else if (i6 == ChatUsersActivity.this.sendMessagesRow) {
                        textCheckCell2.setTextAndCheck(LocaleController.getString("UserRestrictionsSend", NUM), !ChatUsersActivity.this.defaultBannedRights.send_messages, true);
                    } else if (i6 == ChatUsersActivity.this.sendMediaRow) {
                        textCheckCell2.setTextAndCheck(LocaleController.getString("UserRestrictionsSendMedia", NUM), !ChatUsersActivity.this.defaultBannedRights.send_media, true);
                    } else if (i6 == ChatUsersActivity.this.sendStickersRow) {
                        textCheckCell2.setTextAndCheck(LocaleController.getString("UserRestrictionsSendStickers", NUM), !ChatUsersActivity.this.defaultBannedRights.send_stickers, true);
                    } else if (i6 == ChatUsersActivity.this.embedLinksRow) {
                        textCheckCell2.setTextAndCheck(LocaleController.getString("UserRestrictionsEmbedLinks", NUM), !ChatUsersActivity.this.defaultBannedRights.embed_links, true);
                    } else if (i6 == ChatUsersActivity.this.sendPollsRow) {
                        textCheckCell2.setTextAndCheck(LocaleController.getString("UserRestrictionsSendPolls", NUM), !ChatUsersActivity.this.defaultBannedRights.send_polls, true);
                    }
                    if (i6 == ChatUsersActivity.this.sendMediaRow || i6 == ChatUsersActivity.this.sendStickersRow || i6 == ChatUsersActivity.this.embedLinksRow || i6 == ChatUsersActivity.this.sendPollsRow) {
                        textCheckCell2.setEnabled(!ChatUsersActivity.this.defaultBannedRights.send_messages && !ChatUsersActivity.this.defaultBannedRights.view_messages);
                    } else if (i6 == ChatUsersActivity.this.sendMessagesRow) {
                        textCheckCell2.setEnabled(!ChatUsersActivity.this.defaultBannedRights.view_messages);
                    }
                    if (!ChatObject.canBlockUsers(ChatUsersActivity.this.currentChat)) {
                        textCheckCell2.setIcon(0);
                        return;
                    } else if ((i6 != ChatUsersActivity.this.addUsersRow || ChatObject.canUserDoAdminAction(ChatUsersActivity.this.currentChat, 3)) && ((i6 != ChatUsersActivity.this.pinMessagesRow || ChatObject.canUserDoAdminAction(ChatUsersActivity.this.currentChat, 0)) && ((i6 != ChatUsersActivity.this.changeInfoRow || ChatObject.canUserDoAdminAction(ChatUsersActivity.this.currentChat, 1)) && (TextUtils.isEmpty(ChatUsersActivity.this.currentChat.username) || !(i6 == ChatUsersActivity.this.pinMessagesRow || i6 == ChatUsersActivity.this.changeInfoRow))))) {
                        textCheckCell2.setIcon(0);
                        return;
                    } else {
                        textCheckCell2.setIcon(NUM);
                        return;
                    }
                case 8:
                    GraySectionCell graySectionCell = (GraySectionCell) viewHolder2.itemView;
                    if (i6 == ChatUsersActivity.this.membersHeaderRow) {
                        if (!ChatObject.isChannel(ChatUsersActivity.this.currentChat) || ChatUsersActivity.this.currentChat.megagroup) {
                            graySectionCell.setText(LocaleController.getString("ChannelOtherMembers", NUM));
                            return;
                        } else {
                            graySectionCell.setText(LocaleController.getString("ChannelOtherSubscribers", NUM));
                            return;
                        }
                    } else if (i6 == ChatUsersActivity.this.botHeaderRow) {
                        graySectionCell.setText(LocaleController.getString("ChannelBots", NUM));
                        return;
                    } else if (i6 != ChatUsersActivity.this.contactsHeaderRow) {
                        return;
                    } else {
                        if (!ChatObject.isChannel(ChatUsersActivity.this.currentChat) || ChatUsersActivity.this.currentChat.megagroup) {
                            graySectionCell.setText(LocaleController.getString("GroupContacts", NUM));
                            return;
                        } else {
                            graySectionCell.setText(LocaleController.getString("ChannelContacts", NUM));
                            return;
                        }
                    }
                default:
                    return;
            }
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
        $$Lambda$ChatUsersActivity$_DQU8FkLhj_sFqftd480SdstddQ r11 = new ThemeDescription.ThemeDescriptionDelegate() {
            public final void didSetColor() {
                ChatUsersActivity.this.lambda$getThemeDescriptions$18$ChatUsersActivity();
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
        $$Lambda$ChatUsersActivity$_DQU8FkLhj_sFqftd480SdstddQ r9 = r11;
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) r9, "windowBackgroundWhiteGrayText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) r9, "windowBackgroundWhiteBlueText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, (Paint) null, Theme.avatarDrawables, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_text"));
        $$Lambda$ChatUsersActivity$_DQU8FkLhj_sFqftd480SdstddQ r8 = r11;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundRed"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundOrange"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundViolet"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundGreen"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundCyan"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundBlue"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundPink"));
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
        return arrayList;
    }

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
