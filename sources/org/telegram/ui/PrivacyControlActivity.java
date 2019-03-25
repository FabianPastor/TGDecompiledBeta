package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.style.CharacterStyle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collections;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.InputUser;
import org.telegram.tgnet.TLRPC.KeyboardButton;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.PrivacyRule;
import org.telegram.tgnet.TLRPC.TL_account_privacyRules;
import org.telegram.tgnet.TLRPC.TL_account_setPrivacy;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputPrivacyKeyChatInvite;
import org.telegram.tgnet.TLRPC.TL_inputPrivacyKeyForwards;
import org.telegram.tgnet.TLRPC.TL_inputPrivacyKeyPhoneCall;
import org.telegram.tgnet.TLRPC.TL_inputPrivacyKeyPhoneP2P;
import org.telegram.tgnet.TLRPC.TL_inputPrivacyKeyProfilePhoto;
import org.telegram.tgnet.TLRPC.TL_inputPrivacyKeyStatusTimestamp;
import org.telegram.tgnet.TLRPC.TL_inputPrivacyValueAllowAll;
import org.telegram.tgnet.TLRPC.TL_inputPrivacyValueAllowContacts;
import org.telegram.tgnet.TLRPC.TL_inputPrivacyValueAllowUsers;
import org.telegram.tgnet.TLRPC.TL_inputPrivacyValueDisallowAll;
import org.telegram.tgnet.TLRPC.TL_inputPrivacyValueDisallowUsers;
import org.telegram.tgnet.TLRPC.TL_message;
import org.telegram.tgnet.TLRPC.TL_messageFwdHeader;
import org.telegram.tgnet.TLRPC.TL_messageMediaEmpty;
import org.telegram.tgnet.TLRPC.TL_peerUser;
import org.telegram.tgnet.TLRPC.TL_pollAnswer;
import org.telegram.tgnet.TLRPC.TL_privacyValueAllowAll;
import org.telegram.tgnet.TLRPC.TL_privacyValueAllowUsers;
import org.telegram.tgnet.TLRPC.TL_privacyValueDisallowAll;
import org.telegram.tgnet.TLRPC.TL_privacyValueDisallowUsers;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Cells.ChatMessageCell$ChatMessageCellDelegate$$CC;
import org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.RadioCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.HintView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class PrivacyControlActivity extends BaseFragment implements NotificationCenterDelegate {
    private static final int done_button = 1;
    private int alwaysShareRow;
    private ArrayList<Integer> currentMinus;
    private ArrayList<Integer> currentPlus;
    private int currentType;
    private int detailRow;
    private View doneButton;
    private boolean enableAnimation;
    private int everybodyRow;
    private ArrayList<Integer> initialMinus;
    private ArrayList<Integer> initialPlus;
    private int initialRulesType;
    private int lastCheckedType;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private MessageCell messageCell;
    private int messageRow;
    private int myContactsRow;
    private int neverShareRow;
    private int nobodyRow;
    private int rowCount;
    private int rulesType;
    private int sectionRow;
    private int shareDetailRow;
    private int shareSectionRow;

    private static class LinkMovementMethodMy extends LinkMovementMethod {
        private LinkMovementMethodMy() {
        }

        public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
            try {
                return super.onTouchEvent(widget, buffer, event);
            } catch (Exception e) {
                FileLog.e(e);
                return false;
            }
        }
    }

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return position == PrivacyControlActivity.this.nobodyRow || position == PrivacyControlActivity.this.everybodyRow || position == PrivacyControlActivity.this.myContactsRow || position == PrivacyControlActivity.this.neverShareRow || position == PrivacyControlActivity.this.alwaysShareRow;
        }

        public int getItemCount() {
            return PrivacyControlActivity.this.rowCount;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new TextSettingsCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                case 1:
                    view = new TextInfoPrivacyCell(this.mContext);
                    break;
                case 2:
                    view = new HeaderCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                case 3:
                    view = new RadioCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                default:
                    view = PrivacyControlActivity.this.messageCell;
                    break;
            }
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            boolean z = true;
            switch (holder.getItemViewType()) {
                case 0:
                    TextSettingsCell textCell = holder.itemView;
                    String value;
                    if (position == PrivacyControlActivity.this.alwaysShareRow) {
                        if (PrivacyControlActivity.this.currentPlus.size() != 0) {
                            value = LocaleController.formatPluralString("Users", PrivacyControlActivity.this.currentPlus.size());
                        } else {
                            value = LocaleController.getString("EmpryUsersPlaceholder", NUM);
                        }
                        String string;
                        if (PrivacyControlActivity.this.rulesType != 0) {
                            string = LocaleController.getString("AlwaysAllow", NUM);
                            if (PrivacyControlActivity.this.neverShareRow == -1) {
                                z = false;
                            }
                            textCell.setTextAndValue(string, value, z);
                            return;
                        }
                        string = LocaleController.getString("AlwaysShareWith", NUM);
                        if (PrivacyControlActivity.this.neverShareRow == -1) {
                            z = false;
                        }
                        textCell.setTextAndValue(string, value, z);
                        return;
                    } else if (position == PrivacyControlActivity.this.neverShareRow) {
                        if (PrivacyControlActivity.this.currentMinus.size() != 0) {
                            value = LocaleController.formatPluralString("Users", PrivacyControlActivity.this.currentMinus.size());
                        } else {
                            value = LocaleController.getString("EmpryUsersPlaceholder", NUM);
                        }
                        if (PrivacyControlActivity.this.rulesType != 0) {
                            textCell.setTextAndValue(LocaleController.getString("NeverAllow", NUM), value, false);
                            return;
                        } else {
                            textCell.setTextAndValue(LocaleController.getString("NeverShareWith", NUM), value, false);
                            return;
                        }
                    } else {
                        return;
                    }
                case 1:
                    TextInfoPrivacyCell privacyCell = holder.itemView;
                    if (position == PrivacyControlActivity.this.detailRow) {
                        if (PrivacyControlActivity.this.rulesType == 5) {
                            privacyCell.setText(LocaleController.getString("PrivacyForwardsInfo", NUM));
                        } else if (PrivacyControlActivity.this.rulesType == 4) {
                            privacyCell.setText(LocaleController.getString("PrivacyProfilePhotoInfo", NUM));
                        } else if (PrivacyControlActivity.this.rulesType == 3) {
                            privacyCell.setText(LocaleController.getString("PrivacyCallsP2PHelp", NUM));
                        } else if (PrivacyControlActivity.this.rulesType == 2) {
                            privacyCell.setText(LocaleController.getString("WhoCanCallMeInfo", NUM));
                        } else if (PrivacyControlActivity.this.rulesType == 1) {
                            privacyCell.setText(LocaleController.getString("WhoCanAddMeInfo", NUM));
                        } else {
                            privacyCell.setText(LocaleController.getString("CustomHelp", NUM));
                        }
                        privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                        return;
                    } else if (position == PrivacyControlActivity.this.shareDetailRow) {
                        if (PrivacyControlActivity.this.rulesType == 5) {
                            privacyCell.setText(LocaleController.getString("PrivacyForwardsInfo2", NUM));
                        } else if (PrivacyControlActivity.this.rulesType == 4) {
                            privacyCell.setText(LocaleController.getString("PrivacyProfilePhotoInfo2", NUM));
                        } else if (PrivacyControlActivity.this.rulesType == 3) {
                            privacyCell.setText(LocaleController.getString("CustomP2PInfo", NUM));
                        } else if (PrivacyControlActivity.this.rulesType == 2) {
                            privacyCell.setText(LocaleController.getString("CustomCallInfo", NUM));
                        } else if (PrivacyControlActivity.this.rulesType == 1) {
                            privacyCell.setText(LocaleController.getString("CustomShareInfo", NUM));
                        } else {
                            privacyCell.setText(LocaleController.getString("CustomShareSettingsHelp", NUM));
                        }
                        privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                        return;
                    } else {
                        return;
                    }
                case 2:
                    HeaderCell headerCell = holder.itemView;
                    if (position == PrivacyControlActivity.this.sectionRow) {
                        if (PrivacyControlActivity.this.rulesType == 5) {
                            headerCell.setText(LocaleController.getString("PrivacyForwardsTitle", NUM));
                            return;
                        } else if (PrivacyControlActivity.this.rulesType == 4) {
                            headerCell.setText(LocaleController.getString("PrivacyProfilePhotoTitle", NUM));
                            return;
                        } else if (PrivacyControlActivity.this.rulesType == 3) {
                            headerCell.setText(LocaleController.getString("P2PEnabledWith", NUM));
                            return;
                        } else if (PrivacyControlActivity.this.rulesType == 2) {
                            headerCell.setText(LocaleController.getString("WhoCanCallMe", NUM));
                            return;
                        } else if (PrivacyControlActivity.this.rulesType == 1) {
                            headerCell.setText(LocaleController.getString("WhoCanAddMe", NUM));
                            return;
                        } else {
                            headerCell.setText(LocaleController.getString("LastSeenTitle", NUM));
                            return;
                        }
                    } else if (position == PrivacyControlActivity.this.shareSectionRow) {
                        headerCell.setText(LocaleController.getString("AddExceptions", NUM));
                        return;
                    } else {
                        return;
                    }
                case 3:
                    RadioCell radioCell = holder.itemView;
                    int checkedType = 0;
                    boolean z2;
                    if (position == PrivacyControlActivity.this.everybodyRow) {
                        if (PrivacyControlActivity.this.rulesType == 3) {
                            radioCell.setText(LocaleController.getString("P2PEverybody", NUM), PrivacyControlActivity.this.lastCheckedType == 0, true);
                        } else {
                            radioCell.setText(LocaleController.getString("LastSeenEverybody", NUM), PrivacyControlActivity.this.lastCheckedType == 0, true);
                        }
                        checkedType = 0;
                    } else if (position == PrivacyControlActivity.this.myContactsRow) {
                        if (PrivacyControlActivity.this.rulesType == 3) {
                            String string2 = LocaleController.getString("P2PContacts", NUM);
                            if (PrivacyControlActivity.this.lastCheckedType == 2) {
                                z2 = true;
                            } else {
                                z2 = false;
                            }
                            radioCell.setText(string2, z2, PrivacyControlActivity.this.nobodyRow != -1);
                        } else {
                            radioCell.setText(LocaleController.getString("LastSeenContacts", NUM), PrivacyControlActivity.this.lastCheckedType == 2, PrivacyControlActivity.this.nobodyRow != -1);
                        }
                        checkedType = 2;
                    } else if (position == PrivacyControlActivity.this.nobodyRow) {
                        if (PrivacyControlActivity.this.rulesType == 3) {
                            String string3 = LocaleController.getString("P2PNobody", NUM);
                            if (PrivacyControlActivity.this.lastCheckedType == 1) {
                                z2 = true;
                            } else {
                                z2 = false;
                            }
                            radioCell.setText(string3, z2, false);
                        } else {
                            radioCell.setText(LocaleController.getString("LastSeenNobody", NUM), PrivacyControlActivity.this.lastCheckedType == 1, false);
                        }
                        checkedType = 1;
                    }
                    if (PrivacyControlActivity.this.lastCheckedType == checkedType) {
                        radioCell.setChecked(false, PrivacyControlActivity.this.enableAnimation);
                        return;
                    } else if (PrivacyControlActivity.this.currentType == checkedType) {
                        radioCell.setChecked(true, PrivacyControlActivity.this.enableAnimation);
                        return;
                    } else {
                        return;
                    }
                default:
                    return;
            }
        }

        public int getItemViewType(int position) {
            if (position == PrivacyControlActivity.this.alwaysShareRow || position == PrivacyControlActivity.this.neverShareRow) {
                return 0;
            }
            if (position == PrivacyControlActivity.this.shareDetailRow || position == PrivacyControlActivity.this.detailRow) {
                return 1;
            }
            if (position == PrivacyControlActivity.this.sectionRow || position == PrivacyControlActivity.this.shareSectionRow) {
                return 2;
            }
            if (position == PrivacyControlActivity.this.everybodyRow || position == PrivacyControlActivity.this.myContactsRow || position == PrivacyControlActivity.this.nobodyRow) {
                return 3;
            }
            if (position == PrivacyControlActivity.this.messageRow) {
                return 4;
            }
            return 0;
        }
    }

    private class MessageCell extends FrameLayout {
        private Drawable backgroundDrawable;
        private ChatMessageCell cell;
        private HintView hintView;
        private MessageObject messageObject;
        private Drawable shadowDrawable;

        public MessageCell(Context context) {
            super(context);
            setWillNotDraw(false);
            this.shadowDrawable = Theme.getThemedDrawable(context, NUM, "windowBackgroundGrayShadow");
            setPadding(0, AndroidUtilities.dp(11.0f), 0, AndroidUtilities.dp(11.0f));
            int date = ((int) (System.currentTimeMillis() / 1000)) - 3600;
            User currentUser = MessagesController.getInstance(PrivacyControlActivity.this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(PrivacyControlActivity.this.currentAccount).getClientUserId()));
            Message message = new TL_message();
            message.message = LocaleController.getString("PrivacyForwardsMessageLine", NUM);
            message.date = date + 60;
            message.fwd_from = new TL_messageFwdHeader();
            message.fwd_from.from_name = UserObject.getFirstName(currentUser);
            message.media = new TL_messageMediaEmpty();
            message.out = false;
            message.to_id = new TL_peerUser();
            message.to_id.user_id = UserConfig.getInstance(PrivacyControlActivity.this.currentAccount).getClientUserId();
            this.messageObject = new MessageObject(PrivacyControlActivity.this.currentAccount, message, true);
            this.messageObject.eventId = 1;
            this.messageObject.resetLayout();
            this.cell = new ChatMessageCell(context);
            this.cell.setDelegate(new ChatMessageCellDelegate(PrivacyControlActivity.this) {
                public boolean canPerformActions() {
                    return ChatMessageCell$ChatMessageCellDelegate$$CC.canPerformActions(this);
                }

                public void didLongPress(ChatMessageCell chatMessageCell) {
                    ChatMessageCell$ChatMessageCellDelegate$$CC.didLongPress(this, chatMessageCell);
                }

                public void didPressBotButton(ChatMessageCell chatMessageCell, KeyboardButton keyboardButton) {
                    ChatMessageCell$ChatMessageCellDelegate$$CC.didPressBotButton(this, chatMessageCell, keyboardButton);
                }

                public void didPressCancelSendButton(ChatMessageCell chatMessageCell) {
                    ChatMessageCell$ChatMessageCellDelegate$$CC.didPressCancelSendButton(this, chatMessageCell);
                }

                public void didPressChannelAvatar(ChatMessageCell chatMessageCell, Chat chat, int i) {
                    ChatMessageCell$ChatMessageCellDelegate$$CC.didPressChannelAvatar(this, chatMessageCell, chat, i);
                }

                public void didPressHiddenForward(ChatMessageCell chatMessageCell) {
                    ChatMessageCell$ChatMessageCellDelegate$$CC.didPressHiddenForward(this, chatMessageCell);
                }

                public void didPressImage(ChatMessageCell chatMessageCell) {
                    ChatMessageCell$ChatMessageCellDelegate$$CC.didPressImage(this, chatMessageCell);
                }

                public void didPressInstantButton(ChatMessageCell chatMessageCell, int i) {
                    ChatMessageCell$ChatMessageCellDelegate$$CC.didPressInstantButton(this, chatMessageCell, i);
                }

                public void didPressOther(ChatMessageCell chatMessageCell) {
                    ChatMessageCell$ChatMessageCellDelegate$$CC.didPressOther(this, chatMessageCell);
                }

                public void didPressReplyMessage(ChatMessageCell chatMessageCell, int i) {
                    ChatMessageCell$ChatMessageCellDelegate$$CC.didPressReplyMessage(this, chatMessageCell, i);
                }

                public void didPressShare(ChatMessageCell chatMessageCell) {
                    ChatMessageCell$ChatMessageCellDelegate$$CC.didPressShare(this, chatMessageCell);
                }

                public void didPressUrl(MessageObject messageObject, CharacterStyle characterStyle, boolean z) {
                    ChatMessageCell$ChatMessageCellDelegate$$CC.didPressUrl(this, messageObject, characterStyle, z);
                }

                public void didPressUserAvatar(ChatMessageCell chatMessageCell, User user) {
                    ChatMessageCell$ChatMessageCellDelegate$$CC.didPressUserAvatar(this, chatMessageCell, user);
                }

                public void didPressViaBot(ChatMessageCell chatMessageCell, String str) {
                    ChatMessageCell$ChatMessageCellDelegate$$CC.didPressViaBot(this, chatMessageCell, str);
                }

                public void didPressVoteButton(ChatMessageCell chatMessageCell, TL_pollAnswer tL_pollAnswer) {
                    ChatMessageCell$ChatMessageCellDelegate$$CC.didPressVoteButton(this, chatMessageCell, tL_pollAnswer);
                }

                public boolean isChatAdminCell(int i) {
                    return ChatMessageCell$ChatMessageCellDelegate$$CC.isChatAdminCell(this, i);
                }

                public void needOpenWebView(String str, String str2, String str3, String str4, int i, int i2) {
                    ChatMessageCell$ChatMessageCellDelegate$$CC.needOpenWebView(this, str, str2, str3, str4, i, i2);
                }

                public boolean needPlayMessage(MessageObject messageObject) {
                    return ChatMessageCell$ChatMessageCellDelegate$$CC.needPlayMessage(this, messageObject);
                }

                public void videoTimerReached() {
                    ChatMessageCell$ChatMessageCellDelegate$$CC.videoTimerReached(this);
                }
            });
            this.cell.isChat = false;
            this.cell.setFullyDraw(true);
            this.cell.setMessageObject(this.messageObject, null, false, false);
            addView(this.cell, LayoutHelper.createLinear(-1, -2));
            this.hintView = new HintView(context, 1, true);
            addView(this.hintView, LayoutHelper.createFrame(-2, -2.0f, 51, 19.0f, 0.0f, 19.0f, 0.0f));
        }

        /* Access modifiers changed, original: protected */
        public void dispatchDraw(Canvas canvas) {
            super.dispatchDraw(canvas);
            this.hintView.showForMessageCell(this.cell, false);
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            Drawable newDrawable = Theme.getCachedWallpaperNonBlocking();
            if (newDrawable != null) {
                this.backgroundDrawable = newDrawable;
            }
            float scale;
            if (this.backgroundDrawable instanceof ColorDrawable) {
                this.backgroundDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
                this.backgroundDrawable.draw(canvas);
            } else if (!(this.backgroundDrawable instanceof BitmapDrawable)) {
                super.onDraw(canvas);
            } else if (this.backgroundDrawable.getTileModeX() == TileMode.REPEAT) {
                canvas.save();
                scale = 2.0f / AndroidUtilities.density;
                canvas.scale(scale, scale);
                this.backgroundDrawable.setBounds(0, 0, (int) Math.ceil((double) (((float) getMeasuredWidth()) / scale)), (int) Math.ceil((double) (((float) getMeasuredHeight()) / scale)));
                this.backgroundDrawable.draw(canvas);
                canvas.restore();
            } else {
                int viewHeight = getMeasuredHeight();
                float scaleX = ((float) getMeasuredWidth()) / ((float) this.backgroundDrawable.getIntrinsicWidth());
                float scaleY = ((float) viewHeight) / ((float) this.backgroundDrawable.getIntrinsicHeight());
                if (scaleX < scaleY) {
                    scale = scaleY;
                } else {
                    scale = scaleX;
                }
                int width = (int) Math.ceil((double) (((float) this.backgroundDrawable.getIntrinsicWidth()) * scale));
                int height = (int) Math.ceil((double) (((float) this.backgroundDrawable.getIntrinsicHeight()) * scale));
                int x = (getMeasuredWidth() - width) / 2;
                int y = (viewHeight - height) / 2;
                canvas.save();
                canvas.clipRect(0, 0, width, getMeasuredHeight());
                this.backgroundDrawable.setBounds(x, y, x + width, y + height);
                this.backgroundDrawable.draw(canvas);
                canvas.restore();
            }
            this.shadowDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
            this.shadowDrawable.draw(canvas);
        }

        public boolean onInterceptTouchEvent(MotionEvent ev) {
            return false;
        }

        public boolean dispatchTouchEvent(MotionEvent ev) {
            return false;
        }

        /* Access modifiers changed, original: protected */
        public void dispatchSetPressed(boolean pressed) {
        }

        public boolean onTouchEvent(MotionEvent event) {
            return false;
        }

        public void invalidate() {
            super.invalidate();
            this.cell.invalidate();
        }
    }

    public PrivacyControlActivity(int type) {
        this(type, false);
    }

    public PrivacyControlActivity(int type, boolean load) {
        this.initialPlus = new ArrayList();
        this.initialMinus = new ArrayList();
        this.lastCheckedType = -1;
        this.rulesType = type;
        if (load) {
            ContactsController.getInstance(this.currentAccount).loadPrivacySettings();
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        checkPrivacy();
        updateRows();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.privacyRulesUpdated);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiDidLoad);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.privacyRulesUpdated);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiDidLoad);
    }

    public View createView(Context context) {
        if (this.rulesType == 5) {
            this.messageCell = new MessageCell(context);
        }
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        if (this.rulesType == 5) {
            this.actionBar.setTitle(LocaleController.getString("PrivacyForwards", NUM));
        } else if (this.rulesType == 4) {
            this.actionBar.setTitle(LocaleController.getString("PrivacyProfilePhoto", NUM));
        } else if (this.rulesType == 3) {
            this.actionBar.setTitle(LocaleController.getString("PrivacyP2P", NUM));
        } else if (this.rulesType == 2) {
            this.actionBar.setTitle(LocaleController.getString("Calls", NUM));
        } else if (this.rulesType == 1) {
            this.actionBar.setTitle(LocaleController.getString("GroupsAndChannels", NUM));
        } else {
            this.actionBar.setTitle(LocaleController.getString("PrivacyLastSeen", NUM));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    if (PrivacyControlActivity.this.checkDiscard()) {
                        PrivacyControlActivity.this.finishFragment();
                    }
                } else if (id == 1) {
                    PrivacyControlActivity.this.processDone();
                }
            }
        });
        int visibility = this.doneButton != null ? this.doneButton.getVisibility() : 8;
        this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, NUM, AndroidUtilities.dp(56.0f));
        this.doneButton.setVisibility(visibility);
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        this.listView = new RecyclerListView(context);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        this.listView.setVerticalScrollBarEnabled(false);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener(new PrivacyControlActivity$$Lambda$0(this));
        setMessageText();
        return this.fragmentView;
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$createView$2$PrivacyControlActivity(View view, int position) {
        boolean z = false;
        boolean z2 = true;
        if (position == this.nobodyRow || position == this.everybodyRow || position == this.myContactsRow) {
            int newType = this.currentType;
            if (position == this.nobodyRow) {
                newType = 1;
            } else if (position == this.everybodyRow) {
                newType = 0;
            } else if (position == this.myContactsRow) {
                newType = 2;
            }
            if (newType != this.currentType) {
                int i;
                this.enableAnimation = true;
                this.lastCheckedType = this.currentType;
                this.currentType = newType;
                View view2 = this.doneButton;
                if (!hasChanges()) {
                    i = 8;
                }
                view2.setVisibility(i);
                updateRows();
            }
        } else if (position == this.neverShareRow || position == this.alwaysShareRow) {
            ArrayList<Integer> createFromArray;
            if (position == this.neverShareRow) {
                createFromArray = this.currentMinus;
            } else {
                createFromArray = this.currentPlus;
            }
            if (createFromArray.isEmpty()) {
                Bundle args = new Bundle();
                args.putBoolean(position == this.neverShareRow ? "isNeverShare" : "isAlwaysShare", true);
                String str = "isGroup";
                if (this.rulesType != 0) {
                    z = true;
                }
                args.putBoolean(str, z);
                GroupCreateActivity fragment = new GroupCreateActivity(args);
                fragment.setDelegate(new PrivacyControlActivity$$Lambda$6(this, position));
                presentFragment(fragment);
                return;
            }
            boolean z3;
            if (this.rulesType != 0) {
                z3 = true;
            } else {
                z3 = false;
            }
            if (position != this.alwaysShareRow) {
                z2 = false;
            }
            PrivacyUsersActivity fragment2 = new PrivacyUsersActivity(createFromArray, z3, z2);
            fragment2.setDelegate(new PrivacyControlActivity$$Lambda$7(this, position));
            presentFragment(fragment2);
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$0$PrivacyControlActivity(int position, ArrayList ids) {
        int a;
        if (position == this.neverShareRow) {
            this.currentMinus = ids;
            for (a = 0; a < this.currentMinus.size(); a++) {
                this.currentPlus.remove(this.currentMinus.get(a));
            }
        } else {
            this.currentPlus = ids;
            for (a = 0; a < this.currentPlus.size(); a++) {
                this.currentMinus.remove(this.currentPlus.get(a));
            }
        }
        this.lastCheckedType = -1;
        this.doneButton.setVisibility(hasChanges() ? 0 : 8);
        this.listAdapter.notifyDataSetChanged();
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$1$PrivacyControlActivity(int position, ArrayList ids, boolean added) {
        int a;
        if (position == this.neverShareRow) {
            this.currentMinus = ids;
            if (added) {
                for (a = 0; a < this.currentMinus.size(); a++) {
                    this.currentPlus.remove(this.currentMinus.get(a));
                }
            }
        } else {
            this.currentPlus = ids;
            if (added) {
                for (a = 0; a < this.currentPlus.size(); a++) {
                    this.currentMinus.remove(this.currentPlus.get(a));
                }
            }
        }
        this.doneButton.setVisibility(hasChanges() ? 0 : 8);
        this.listAdapter.notifyDataSetChanged();
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.privacyRulesUpdated) {
            checkPrivacy();
        } else if (id == NotificationCenter.emojiDidLoad) {
            this.listView.invalidateViews();
        }
    }

    private void applyCurrentPrivacySettings() {
        int a;
        User user;
        InputUser inputUser;
        TL_account_setPrivacy req = new TL_account_setPrivacy();
        if (this.rulesType == 5) {
            req.key = new TL_inputPrivacyKeyForwards();
        } else if (this.rulesType == 4) {
            req.key = new TL_inputPrivacyKeyProfilePhoto();
        } else if (this.rulesType == 3) {
            req.key = new TL_inputPrivacyKeyPhoneP2P();
        } else if (this.rulesType == 2) {
            req.key = new TL_inputPrivacyKeyPhoneCall();
        } else if (this.rulesType == 1) {
            req.key = new TL_inputPrivacyKeyChatInvite();
        } else {
            req.key = new TL_inputPrivacyKeyStatusTimestamp();
        }
        if (this.currentType != 0 && this.currentPlus.size() > 0) {
            TL_inputPrivacyValueAllowUsers rule = new TL_inputPrivacyValueAllowUsers();
            for (a = 0; a < this.currentPlus.size(); a++) {
                user = MessagesController.getInstance(this.currentAccount).getUser((Integer) this.currentPlus.get(a));
                if (user != null) {
                    inputUser = MessagesController.getInstance(this.currentAccount).getInputUser(user);
                    if (inputUser != null) {
                        rule.users.add(inputUser);
                    }
                }
            }
            req.rules.add(rule);
        }
        if (this.currentType != 1 && this.currentMinus.size() > 0) {
            TL_inputPrivacyValueDisallowUsers rule2 = new TL_inputPrivacyValueDisallowUsers();
            for (a = 0; a < this.currentMinus.size(); a++) {
                user = MessagesController.getInstance(this.currentAccount).getUser((Integer) this.currentMinus.get(a));
                if (user != null) {
                    inputUser = MessagesController.getInstance(this.currentAccount).getInputUser(user);
                    if (inputUser != null) {
                        rule2.users.add(inputUser);
                    }
                }
            }
            req.rules.add(rule2);
        }
        if (this.currentType == 0) {
            req.rules.add(new TL_inputPrivacyValueAllowAll());
        } else if (this.currentType == 1) {
            req.rules.add(new TL_inputPrivacyValueDisallowAll());
        } else if (this.currentType == 2) {
            req.rules.add(new TL_inputPrivacyValueAllowContacts());
        }
        AlertDialog progressDialog = null;
        if (getParentActivity() != null) {
            progressDialog = new AlertDialog(getParentActivity(), 3);
            progressDialog.setCanCacnel(false);
            progressDialog.show();
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new PrivacyControlActivity$$Lambda$1(this, progressDialog), 2);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$applyCurrentPrivacySettings$4$PrivacyControlActivity(AlertDialog progressDialogFinal, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new PrivacyControlActivity$$Lambda$5(this, progressDialogFinal, error, response));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$3$PrivacyControlActivity(AlertDialog progressDialogFinal, TL_error error, TLObject response) {
        if (progressDialogFinal != null) {
            try {
                progressDialogFinal.dismiss();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        if (error == null) {
            TL_account_privacyRules privacyRules = (TL_account_privacyRules) response;
            MessagesController.getInstance(this.currentAccount).putUsers(privacyRules.users, false);
            ContactsController.getInstance(this.currentAccount).setPrivacyRules(privacyRules.rules, this.rulesType);
            finishFragment();
            return;
        }
        showErrorAlert();
    }

    private void showErrorAlert() {
        if (getParentActivity() != null) {
            Builder builder = new Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", NUM));
            builder.setMessage(LocaleController.getString("PrivacyFloodControlError", NUM));
            builder.setPositiveButton(LocaleController.getString("OK", NUM), null);
            showDialog(builder.create());
        }
    }

    private void checkPrivacy() {
        this.currentPlus = new ArrayList();
        this.currentMinus = new ArrayList();
        ArrayList<PrivacyRule> privacyRules = ContactsController.getInstance(this.currentAccount).getPrivacyRules(this.rulesType);
        if (privacyRules == null || privacyRules.size() == 0) {
            this.currentType = 1;
        } else {
            int type = -1;
            for (int a = 0; a < privacyRules.size(); a++) {
                PrivacyRule rule = (PrivacyRule) privacyRules.get(a);
                if (rule instanceof TL_privacyValueAllowUsers) {
                    this.currentPlus.addAll(rule.users);
                } else if (rule instanceof TL_privacyValueDisallowUsers) {
                    this.currentMinus.addAll(rule.users);
                } else if (rule instanceof TL_privacyValueAllowAll) {
                    type = 0;
                } else if (rule instanceof TL_privacyValueDisallowAll) {
                    type = 1;
                } else {
                    type = 2;
                }
            }
            if (type == 0 || (type == -1 && this.currentMinus.size() > 0)) {
                this.currentType = 0;
            } else if (type == 2 || (type == -1 && this.currentMinus.size() > 0 && this.currentPlus.size() > 0)) {
                this.currentType = 2;
            } else if (type == 1 || (type == -1 && this.currentPlus.size() > 0)) {
                this.currentType = 1;
            }
            if (this.doneButton != null) {
                this.doneButton.setVisibility(8);
            }
        }
        this.initialPlus.clear();
        this.initialMinus.clear();
        this.initialRulesType = this.currentType;
        this.initialPlus.addAll(this.currentPlus);
        this.initialMinus.addAll(this.currentMinus);
        updateRows();
    }

    private boolean hasChanges() {
        if (this.initialRulesType != this.currentType || this.initialMinus.size() != this.currentMinus.size() || this.initialPlus.size() != this.currentPlus.size()) {
            return true;
        }
        Collections.sort(this.initialPlus);
        Collections.sort(this.currentPlus);
        if (!this.initialPlus.equals(this.currentPlus)) {
            return true;
        }
        Collections.sort(this.initialMinus);
        Collections.sort(this.currentMinus);
        if (this.initialMinus.equals(this.currentMinus)) {
            return false;
        }
        return true;
    }

    private void updateRows() {
        int i;
        this.rowCount = 0;
        if (this.rulesType == 5) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.messageRow = i;
        } else {
            this.messageRow = -1;
        }
        i = this.rowCount;
        this.rowCount = i + 1;
        this.sectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.everybodyRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.myContactsRow = i;
        if (this.rulesType == 0 || this.rulesType == 2 || this.rulesType == 3 || this.rulesType == 5) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.nobodyRow = i;
        } else {
            this.nobodyRow = -1;
        }
        i = this.rowCount;
        this.rowCount = i + 1;
        this.detailRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.shareSectionRow = i;
        if (this.currentType == 1 || this.currentType == 2) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.alwaysShareRow = i;
        } else {
            this.alwaysShareRow = -1;
        }
        if (this.currentType == 0 || this.currentType == 2) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.neverShareRow = i;
        } else {
            this.neverShareRow = -1;
        }
        i = this.rowCount;
        this.rowCount = i + 1;
        this.shareDetailRow = i;
        setMessageText();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
    }

    private void setMessageText() {
        if (this.messageCell != null) {
            if (this.currentType == 0) {
                this.messageCell.hintView.setOverrideText(LocaleController.getString("PrivacyForwardsEverybody", NUM));
                this.messageCell.messageObject.messageOwner.fwd_from.from_id = 1;
            } else if (this.currentType == 1) {
                this.messageCell.hintView.setOverrideText(LocaleController.getString("PrivacyForwardsNobody", NUM));
                this.messageCell.messageObject.messageOwner.fwd_from.from_id = 0;
            } else {
                this.messageCell.hintView.setOverrideText(LocaleController.getString("PrivacyForwardsContacts", NUM));
                this.messageCell.messageObject.messageOwner.fwd_from.from_id = 1;
            }
            this.messageCell.cell.forceResetMessageObject();
        }
    }

    public void onResume() {
        super.onResume();
        this.lastCheckedType = -1;
        this.enableAnimation = false;
    }

    public boolean onBackPressed() {
        return checkDiscard();
    }

    private void processDone() {
        if (getParentActivity() != null) {
            if (this.currentType != 0 && this.rulesType == 0) {
                SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                if (!preferences.getBoolean("privacyAlertShowed", false)) {
                    Builder builder = new Builder(getParentActivity());
                    if (this.rulesType == 1) {
                        builder.setMessage(LocaleController.getString("WhoCanAddMeInfo", NUM));
                    } else {
                        builder.setMessage(LocaleController.getString("CustomHelp", NUM));
                    }
                    builder.setTitle(LocaleController.getString("AppName", NUM));
                    builder.setPositiveButton(LocaleController.getString("OK", NUM), new PrivacyControlActivity$$Lambda$2(this, preferences));
                    builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
                    showDialog(builder.create());
                    return;
                }
            }
            applyCurrentPrivacySettings();
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$processDone$5$PrivacyControlActivity(SharedPreferences preferences, DialogInterface dialogInterface, int i) {
        applyCurrentPrivacySettings();
        preferences.edit().putBoolean("privacyAlertShowed", true).commit();
    }

    private boolean checkDiscard() {
        if (this.doneButton.getVisibility() != 0) {
            return true;
        }
        Builder builder = new Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("UserRestrictionsApplyChanges", NUM));
        builder.setMessage(LocaleController.getString("PrivacySettingsChangedAlert", NUM));
        builder.setPositiveButton(LocaleController.getString("ApplyTheme", NUM), new PrivacyControlActivity$$Lambda$3(this));
        builder.setNegativeButton(LocaleController.getString("PassportDiscard", NUM), new PrivacyControlActivity$$Lambda$4(this));
        showDialog(builder.create());
        return false;
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$checkDiscard$6$PrivacyControlActivity(DialogInterface dialogInterface, int i) {
        processDone();
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$checkDiscard$7$PrivacyControlActivity(DialogInterface dialog, int which) {
        finishFragment();
    }

    public boolean canBeginSlide() {
        return checkDiscard();
    }

    public ThemeDescription[] getThemeDescriptions() {
        r9 = new ThemeDescription[40];
        r9[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, HeaderCell.class, RadioCell.class}, null, null, null, "windowBackgroundWhite");
        r9[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray");
        r9[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
        r9[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
        r9[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
        r9[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
        r9[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
        r9[7] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        r9[8] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider");
        r9[9] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, "windowBackgroundGrayShadow");
        r9[10] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        r9[11] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, "windowBackgroundWhiteValueText");
        r9[12] = new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteGrayText4");
        r9[13] = new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlueHeader");
        r9[14] = new ThemeDescription(this.listView, 0, new Class[]{RadioCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        r9[15] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioCell.class}, new String[]{"radioButton"}, null, null, null, "radioBackground");
        r9[16] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioCell.class}, new String[]{"radioButton"}, null, null, null, "radioBackgroundChecked");
        r9[17] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgInDrawable, Theme.chat_msgInMediaDrawable}, null, "chat_inBubble");
        r9[18] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgInSelectedDrawable, Theme.chat_msgInMediaSelectedDrawable}, null, "chat_inBubbleSelected");
        r9[19] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgInShadowDrawable, Theme.chat_msgInMediaShadowDrawable}, null, "chat_inBubbleShadow");
        r9[20] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, null, "chat_outBubble");
        r9[21] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutSelectedDrawable, Theme.chat_msgOutMediaSelectedDrawable}, null, "chat_outBubbleSelected");
        r9[22] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutShadowDrawable, Theme.chat_msgOutMediaShadowDrawable}, null, "chat_outBubbleShadow");
        r9[23] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_messageTextIn");
        r9[24] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_messageTextOut");
        r9[25] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutCheckDrawable, Theme.chat_msgOutHalfCheckDrawable}, null, "chat_outSentCheck");
        r9[26] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutCheckSelectedDrawable, Theme.chat_msgOutHalfCheckSelectedDrawable}, null, "chat_outSentCheckSelected");
        r9[27] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgMediaCheckDrawable, Theme.chat_msgMediaHalfCheckDrawable}, null, "chat_mediaSentCheck");
        r9[28] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_inReplyLine");
        r9[29] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_outReplyLine");
        r9[30] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_inReplyNameText");
        r9[31] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_outReplyNameText");
        r9[32] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_inReplyMessageText");
        r9[33] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_outReplyMessageText");
        r9[34] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_inReplyMediaMessageSelectedText");
        r9[35] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_outReplyMediaMessageSelectedText");
        r9[36] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_inTimeText");
        r9[37] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_outTimeText");
        r9[38] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_inTimeSelectedText");
        r9[39] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_outTimeSelectedText");
        return r9;
    }
}
