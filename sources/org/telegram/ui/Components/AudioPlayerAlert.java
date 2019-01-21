package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.DownloadController.FileDownloadProgressListener;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.R;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.audioinfo.AudioInfo;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.LayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.TL_messageActionEmpty;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.AudioPlayerCell;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.LaunchActivity;

public class AudioPlayerAlert extends BottomSheet implements FileDownloadProgressListener, NotificationCenterDelegate {
    private int TAG;
    private ActionBar actionBar;
    private AnimatorSet actionBarAnimation;
    private AnimatorSet animatorSet;
    private TextView authorTextView;
    private ChatAvatarContainer avatarContainer;
    private View[] buttons = new View[5];
    private TextView durationTextView;
    private float endTranslation;
    private float fullAnimationProgress;
    private int hasNoCover;
    private boolean hasOptions = true;
    private boolean inFullSize;
    private boolean isInFullMode;
    private int lastTime;
    private LinearLayoutManager layoutManager;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private ActionBarMenuItem menuItem;
    private Drawable noCoverDrawable;
    private ActionBarMenuItem optionsButton;
    private Paint paint = new Paint(1);
    private float panelEndTranslation;
    private float panelStartTranslation;
    private LaunchActivity parentActivity;
    private BackupImageView placeholderImageView;
    private ImageView playButton;
    private Drawable[] playOrderButtons = new Drawable[2];
    private FrameLayout playerLayout;
    private ArrayList<MessageObject> playlist;
    private LineProgressView progressView;
    private ImageView repeatButton;
    private int scrollOffsetY = Integer.MAX_VALUE;
    private boolean scrollToSong = true;
    private ActionBarMenuItem searchItem;
    private int searchOpenOffset;
    private int searchOpenPosition = -1;
    private boolean searchWas;
    private boolean searching;
    private SeekBarView seekBarView;
    private View shadow;
    private View shadow2;
    private Drawable shadowDrawable;
    private ActionBarMenuItem shuffleButton;
    private float startTranslation;
    private float thumbMaxScale;
    private int thumbMaxX;
    private int thumbMaxY;
    private SimpleTextView timeTextView;
    private TextView titleTextView;
    private int topBeforeSwitch;

    private class ListAdapter extends SelectionAdapter {
        private Context context;
        private ArrayList<MessageObject> searchResult = new ArrayList();
        private Timer searchTimer;

        public ListAdapter(Context context) {
            this.context = context;
        }

        public int getItemCount() {
            if (AudioPlayerAlert.this.searchWas) {
                return this.searchResult.size();
            }
            if (AudioPlayerAlert.this.searching) {
                return AudioPlayerAlert.this.playlist.size();
            }
            return AudioPlayerAlert.this.playlist.size() + 1;
        }

        public boolean isEnabled(ViewHolder holder) {
            return AudioPlayerAlert.this.searchWas || holder.getAdapterPosition() > 0;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new View(this.context);
                    view.setLayoutParams(new LayoutParams(-1, AndroidUtilities.dp(178.0f)));
                    break;
                default:
                    view = new AudioPlayerCell(this.context);
                    break;
            }
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            if (holder.getItemViewType() == 1) {
                AudioPlayerCell cell = holder.itemView;
                if (AudioPlayerAlert.this.searchWas) {
                    cell.setMessageObject((MessageObject) this.searchResult.get(position));
                } else if (AudioPlayerAlert.this.searching) {
                    if (SharedConfig.playOrderReversed) {
                        cell.setMessageObject((MessageObject) AudioPlayerAlert.this.playlist.get(position));
                    } else {
                        cell.setMessageObject((MessageObject) AudioPlayerAlert.this.playlist.get((AudioPlayerAlert.this.playlist.size() - position) - 1));
                    }
                } else if (position <= 0) {
                } else {
                    if (SharedConfig.playOrderReversed) {
                        cell.setMessageObject((MessageObject) AudioPlayerAlert.this.playlist.get(position - 1));
                    } else {
                        cell.setMessageObject((MessageObject) AudioPlayerAlert.this.playlist.get(AudioPlayerAlert.this.playlist.size() - position));
                    }
                }
            }
        }

        public int getItemViewType(int i) {
            if (AudioPlayerAlert.this.searchWas || AudioPlayerAlert.this.searching || i != 0) {
                return 1;
            }
            return 0;
        }

        public void search(final String query) {
            try {
                if (this.searchTimer != null) {
                    this.searchTimer.cancel();
                }
            } catch (Throwable e) {
                FileLog.e(e);
            }
            if (query == null) {
                this.searchResult.clear();
                notifyDataSetChanged();
                return;
            }
            this.searchTimer = new Timer();
            this.searchTimer.schedule(new TimerTask() {
                public void run() {
                    try {
                        ListAdapter.this.searchTimer.cancel();
                        ListAdapter.this.searchTimer = null;
                    } catch (Throwable e) {
                        FileLog.e(e);
                    }
                    ListAdapter.this.processSearch(query);
                }
            }, 200, 300);
        }

        private void processSearch(String query) {
            AndroidUtilities.runOnUIThread(new AudioPlayerAlert$ListAdapter$$Lambda$0(this, query));
        }

        final /* synthetic */ void lambda$processSearch$1$AudioPlayerAlert$ListAdapter(String query) {
            Utilities.searchQueue.postRunnable(new AudioPlayerAlert$ListAdapter$$Lambda$2(this, query, new ArrayList(AudioPlayerAlert.this.playlist)));
        }

        /* JADX WARNING: Removed duplicated region for block: B:57:0x0061 A:{SYNTHETIC} */
        /* JADX WARNING: Removed duplicated region for block: B:54:0x00b8 A:{SYNTHETIC} */
        final /* synthetic */ void lambda$null$0$AudioPlayerAlert$ListAdapter(java.lang.String r16, java.util.ArrayList r17) {
            /*
            r15 = this;
            r14 = r16.trim();
            r12 = r14.toLowerCase();
            r14 = r12.length();
            if (r14 != 0) goto L_0x0017;
        L_0x000e:
            r14 = new java.util.ArrayList;
            r14.<init>();
            r15.updateSearchResults(r14);
        L_0x0016:
            return;
        L_0x0017:
            r14 = org.telegram.messenger.LocaleController.getInstance();
            r13 = r14.getTranslitString(r12);
            r14 = r12.equals(r13);
            if (r14 != 0) goto L_0x002b;
        L_0x0025:
            r14 = r13.length();
            if (r14 != 0) goto L_0x002c;
        L_0x002b:
            r13 = 0;
        L_0x002c:
            if (r13 == 0) goto L_0x0064;
        L_0x002e:
            r14 = 1;
        L_0x002f:
            r14 = r14 + 1;
            r11 = new java.lang.String[r14];
            r14 = 0;
            r11[r14] = r12;
            if (r13 == 0) goto L_0x003b;
        L_0x0038:
            r14 = 1;
            r11[r14] = r13;
        L_0x003b:
            r10 = new java.util.ArrayList;
            r10.<init>();
            r1 = 0;
        L_0x0041:
            r14 = r17.size();
            if (r1 >= r14) goto L_0x00c6;
        L_0x0047:
            r0 = r17;
            r6 = r0.get(r1);
            r6 = (org.telegram.messenger.MessageObject) r6;
            r3 = 0;
        L_0x0050:
            r14 = r11.length;
            if (r3 >= r14) goto L_0x0073;
        L_0x0053:
            r9 = r11[r3];
            r7 = r6.getDocumentName();
            if (r7 == 0) goto L_0x0061;
        L_0x005b:
            r14 = r7.length();
            if (r14 != 0) goto L_0x0066;
        L_0x0061:
            r3 = r3 + 1;
            goto L_0x0050;
        L_0x0064:
            r14 = 0;
            goto L_0x002f;
        L_0x0066:
            r7 = r7.toLowerCase();
            r14 = r7.contains(r9);
            if (r14 == 0) goto L_0x0076;
        L_0x0070:
            r10.add(r6);
        L_0x0073:
            r1 = r1 + 1;
            goto L_0x0041;
        L_0x0076:
            r14 = r6.type;
            if (r14 != 0) goto L_0x00bc;
        L_0x007a:
            r14 = r6.messageOwner;
            r14 = r14.media;
            r14 = r14.webpage;
            r5 = r14.document;
        L_0x0082:
            r8 = 0;
            r4 = 0;
        L_0x0084:
            r14 = r5.attributes;
            r14 = r14.size();
            if (r4 >= r14) goto L_0x00b6;
        L_0x008c:
            r14 = r5.attributes;
            r2 = r14.get(r4);
            r2 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r2;
            r14 = r2 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
            if (r14 == 0) goto L_0x00c3;
        L_0x0098:
            r14 = r2.performer;
            if (r14 == 0) goto L_0x00a6;
        L_0x009c:
            r14 = r2.performer;
            r14 = r14.toLowerCase();
            r8 = r14.contains(r9);
        L_0x00a6:
            if (r8 != 0) goto L_0x00b6;
        L_0x00a8:
            r14 = r2.title;
            if (r14 == 0) goto L_0x00b6;
        L_0x00ac:
            r14 = r2.title;
            r14 = r14.toLowerCase();
            r8 = r14.contains(r9);
        L_0x00b6:
            if (r8 == 0) goto L_0x0061;
        L_0x00b8:
            r10.add(r6);
            goto L_0x0073;
        L_0x00bc:
            r14 = r6.messageOwner;
            r14 = r14.media;
            r5 = r14.document;
            goto L_0x0082;
        L_0x00c3:
            r4 = r4 + 1;
            goto L_0x0084;
        L_0x00c6:
            r15.updateSearchResults(r10);
            goto L_0x0016;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AudioPlayerAlert.ListAdapter.lambda$null$0$AudioPlayerAlert$ListAdapter(java.lang.String, java.util.ArrayList):void");
        }

        private void updateSearchResults(ArrayList<MessageObject> documents) {
            AndroidUtilities.runOnUIThread(new AudioPlayerAlert$ListAdapter$$Lambda$1(this, documents));
        }

        final /* synthetic */ void lambda$updateSearchResults$2$AudioPlayerAlert$ListAdapter(ArrayList documents) {
            AudioPlayerAlert.this.searchWas = true;
            this.searchResult = documents;
            notifyDataSetChanged();
            AudioPlayerAlert.this.layoutManager.scrollToPosition(0);
        }
    }

    public AudioPlayerAlert(Context context) {
        super(context, true);
        MessageObject messageObject = MediaController.getInstance().getPlayingMessageObject();
        if (messageObject != null) {
            this.currentAccount = messageObject.currentAccount;
        } else {
            this.currentAccount = UserConfig.selectedAccount;
        }
        this.parentActivity = (LaunchActivity) context;
        this.noCoverDrawable = context.getResources().getDrawable(R.drawable.nocover).mutate();
        this.noCoverDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("player_placeholder"), Mode.MULTIPLY));
        this.TAG = DownloadController.getInstance(this.currentAccount).generateObserverTag();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingDidReset);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingDidStart);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.musicDidLoad);
        this.shadowDrawable = context.getResources().getDrawable(R.drawable.sheet_shadow).mutate();
        this.shadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("player_background"), Mode.MULTIPLY));
        this.paint.setColor(Theme.getColor("player_placeholderBackground"));
        this.containerView = new FrameLayout(context) {
            private boolean ignoreLayout = false;

            public boolean onInterceptTouchEvent(MotionEvent ev) {
                if (ev.getAction() != 0 || AudioPlayerAlert.this.scrollOffsetY == 0 || ev.getY() >= ((float) AudioPlayerAlert.this.scrollOffsetY) || AudioPlayerAlert.this.placeholderImageView.getTranslationX() != 0.0f) {
                    return super.onInterceptTouchEvent(ev);
                }
                AudioPlayerAlert.this.dismiss();
                return true;
            }

            public boolean onTouchEvent(MotionEvent e) {
                return !AudioPlayerAlert.this.isDismissed() && super.onTouchEvent(e);
            }

            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int padding;
                int height = MeasureSpec.getSize(heightMeasureSpec);
                int contentSize = (((AndroidUtilities.dp(178.0f) + (AudioPlayerAlert.this.playlist.size() * AndroidUtilities.dp(56.0f))) + AudioPlayerAlert.backgroundPaddingTop) + ActionBar.getCurrentActionBarHeight()) + AndroidUtilities.statusBarHeight;
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, NUM);
                if (AudioPlayerAlert.this.searching) {
                    padding = (ActionBar.getCurrentActionBarHeight() + AndroidUtilities.dp(178.0f)) + (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0);
                } else {
                    padding = contentSize < height ? height - contentSize : contentSize < height ? 0 : height - ((height / 5) * 3);
                    padding += (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight();
                }
                if (AudioPlayerAlert.this.listView.getPaddingTop() != padding) {
                    this.ignoreLayout = true;
                    AudioPlayerAlert.this.listView.setPadding(0, padding, 0, AndroidUtilities.dp(8.0f));
                    this.ignoreLayout = false;
                }
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                AudioPlayerAlert.this.inFullSize = getMeasuredHeight() >= height;
                int availableHeight = ((height - ActionBar.getCurrentActionBarHeight()) - (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0)) - AndroidUtilities.dp(120.0f);
                int maxSize = Math.max(availableHeight, getMeasuredWidth());
                AudioPlayerAlert.this.thumbMaxX = ((getMeasuredWidth() - maxSize) / 2) - AndroidUtilities.dp(17.0f);
                AudioPlayerAlert.this.thumbMaxY = AndroidUtilities.dp(19.0f);
                AudioPlayerAlert.this.panelEndTranslation = (float) (getMeasuredHeight() - AudioPlayerAlert.this.playerLayout.getMeasuredHeight());
                AudioPlayerAlert.this.thumbMaxScale = (((float) maxSize) / ((float) AudioPlayerAlert.this.placeholderImageView.getMeasuredWidth())) - 1.0f;
                AudioPlayerAlert.this.endTranslation = (float) (ActionBar.getCurrentActionBarHeight() + AndroidUtilities.dp(5.0f));
                int scaledHeight = (int) Math.ceil((double) (((float) AudioPlayerAlert.this.placeholderImageView.getMeasuredHeight()) * (1.0f + AudioPlayerAlert.this.thumbMaxScale)));
                if (scaledHeight > availableHeight) {
                    AudioPlayerAlert.this.endTranslation = AudioPlayerAlert.this.endTranslation - ((float) (scaledHeight - availableHeight));
                }
            }

            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                int y = AudioPlayerAlert.this.actionBar.getMeasuredHeight();
                AudioPlayerAlert.this.shadow.layout(AudioPlayerAlert.this.shadow.getLeft(), y, AudioPlayerAlert.this.shadow.getRight(), AudioPlayerAlert.this.shadow.getMeasuredHeight() + y);
                AudioPlayerAlert.this.updateLayout();
                AudioPlayerAlert.this.setFullAnimationProgress(AudioPlayerAlert.this.fullAnimationProgress);
            }

            public void requestLayout() {
                if (!this.ignoreLayout) {
                    super.requestLayout();
                }
            }

            protected void onDraw(Canvas canvas) {
                AudioPlayerAlert.this.shadowDrawable.setBounds(0, Math.max(AudioPlayerAlert.this.actionBar.getMeasuredHeight(), AudioPlayerAlert.this.scrollOffsetY) - AudioPlayerAlert.backgroundPaddingTop, getMeasuredWidth(), getMeasuredHeight());
                AudioPlayerAlert.this.shadowDrawable.draw(canvas);
            }
        };
        this.containerView.setWillNotDraw(false);
        this.containerView.setPadding(backgroundPaddingLeft, 0, backgroundPaddingLeft, 0);
        this.actionBar = new ActionBar(context);
        this.actionBar.setBackgroundColor(Theme.getColor("player_actionBar"));
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setItemsColor(Theme.getColor("player_actionBarItems"), false);
        this.actionBar.setItemsBackgroundColor(Theme.getColor("player_actionBarSelector"), false);
        this.actionBar.setTitleColor(Theme.getColor("player_actionBarTitle"));
        this.actionBar.setSubtitleColor(Theme.getColor("player_actionBarSubtitle"));
        this.actionBar.setAlpha(0.0f);
        this.actionBar.setTitle("1");
        this.actionBar.setSubtitle("1");
        this.actionBar.getTitleTextView().setAlpha(0.0f);
        this.actionBar.getSubtitleTextView().setAlpha(0.0f);
        this.avatarContainer = new ChatAvatarContainer(context, null, false);
        this.avatarContainer.setEnabled(false);
        this.avatarContainer.setTitleColors(Theme.getColor("player_actionBarTitle"), Theme.getColor("player_actionBarSubtitle"));
        if (messageObject != null) {
            long did = messageObject.getDialogId();
            int lower_id = (int) did;
            int high_id = (int) (did >> 32);
            User user;
            if (lower_id == 0) {
                EncryptedChat encryptedChat = MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf(high_id));
                if (encryptedChat != null) {
                    user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(encryptedChat.user_id));
                    if (user != null) {
                        this.avatarContainer.setTitle(ContactsController.formatName(user.first_name, user.last_name));
                        this.avatarContainer.setUserAvatar(user);
                    }
                }
            } else if (lower_id > 0) {
                user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(lower_id));
                if (user != null) {
                    this.avatarContainer.setTitle(ContactsController.formatName(user.first_name, user.last_name));
                    this.avatarContainer.setUserAvatar(user);
                }
            } else {
                Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-lower_id));
                if (chat != null) {
                    this.avatarContainer.setTitle(chat.title);
                    this.avatarContainer.setChatAvatar(chat);
                }
            }
        }
        this.avatarContainer.setSubtitle(LocaleController.getString("AudioTitle", R.string.AudioTitle));
        ActionBar actionBar = this.actionBar;
        actionBar.addView(this.avatarContainer, 0, LayoutHelper.createFrame(-2, -1.0f, 51, 56.0f, 0.0f, 40.0f, 0.0f));
        ActionBarMenu menu = this.actionBar.createMenu();
        this.menuItem = menu.addItem(0, (int) R.drawable.ic_ab_other);
        this.menuItem.addSubItem(1, LocaleController.getString("Forward", R.string.Forward));
        this.menuItem.addSubItem(2, LocaleController.getString("ShareFile", R.string.ShareFile));
        this.menuItem.addSubItem(4, LocaleController.getString("ShowInChat", R.string.ShowInChat));
        this.menuItem.setTranslationX((float) AndroidUtilities.dp(48.0f));
        this.menuItem.setAlpha(0.0f);
        this.searchItem = menu.addItem(0, (int) R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItemSearchListener() {
            public void onSearchCollapse() {
                AudioPlayerAlert.this.avatarContainer.setVisibility(0);
                if (AudioPlayerAlert.this.hasOptions) {
                    AudioPlayerAlert.this.menuItem.setVisibility(4);
                }
                if (AudioPlayerAlert.this.searching) {
                    AudioPlayerAlert.this.searchWas = false;
                    AudioPlayerAlert.this.searching = false;
                    AudioPlayerAlert.this.setAllowNestedScroll(true);
                    AudioPlayerAlert.this.listAdapter.search(null);
                }
            }

            public void onSearchExpand() {
                AudioPlayerAlert.this.searchOpenPosition = AudioPlayerAlert.this.layoutManager.findLastVisibleItemPosition();
                View firstVisView = AudioPlayerAlert.this.layoutManager.findViewByPosition(AudioPlayerAlert.this.searchOpenPosition);
                AudioPlayerAlert.this.searchOpenOffset = (firstVisView == null ? 0 : firstVisView.getTop()) - AudioPlayerAlert.this.listView.getPaddingTop();
                AudioPlayerAlert.this.avatarContainer.setVisibility(8);
                if (AudioPlayerAlert.this.hasOptions) {
                    AudioPlayerAlert.this.menuItem.setVisibility(8);
                }
                AudioPlayerAlert.this.searching = true;
                AudioPlayerAlert.this.setAllowNestedScroll(false);
                AudioPlayerAlert.this.listAdapter.notifyDataSetChanged();
            }

            public void onTextChanged(EditText editText) {
                if (editText.length() > 0) {
                    AudioPlayerAlert.this.listAdapter.search(editText.getText().toString());
                    return;
                }
                AudioPlayerAlert.this.searchWas = false;
                AudioPlayerAlert.this.listAdapter.search(null);
            }
        });
        EditTextBoldCursor editText = this.searchItem.getSearchField();
        editText.setHint(LocaleController.getString("Search", R.string.Search));
        editText.setTextColor(Theme.getColor("player_actionBarTitle"));
        editText.setHintTextColor(Theme.getColor("player_time"));
        editText.setCursorColor(Theme.getColor("player_actionBarTitle"));
        if (!AndroidUtilities.isTablet()) {
            this.actionBar.showActionModeTop();
            this.actionBar.setActionModeTopColor(Theme.getColor("player_actionBarTop"));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    AudioPlayerAlert.this.dismiss();
                } else {
                    AudioPlayerAlert.this.onSubItemClick(id);
                }
            }
        });
        this.shadow = new View(context);
        this.shadow.setAlpha(0.0f);
        this.shadow.setBackgroundResource(R.drawable.header_shadow);
        this.shadow2 = new View(context);
        this.shadow2.setAlpha(0.0f);
        this.shadow2.setBackgroundResource(R.drawable.header_shadow);
        this.playerLayout = new FrameLayout(context);
        this.playerLayout.setBackgroundColor(Theme.getColor("player_background"));
        this.placeholderImageView = new BackupImageView(context) {
            private RectF rect = new RectF();

            protected void onDraw(Canvas canvas) {
                if (AudioPlayerAlert.this.hasNoCover == 1 || (AudioPlayerAlert.this.hasNoCover == 2 && !(getImageReceiver().hasBitmapImage() && getImageReceiver().getCurrentAlpha() == 1.0f))) {
                    this.rect.set(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight());
                    canvas.drawRoundRect(this.rect, (float) getRoundRadius(), (float) getRoundRadius(), AudioPlayerAlert.this.paint);
                    int s = (int) (((float) AndroidUtilities.dp(63.0f)) * Math.max(((AudioPlayerAlert.this.thumbMaxScale / getScaleX()) / 3.0f) / AudioPlayerAlert.this.thumbMaxScale, 1.0f / AudioPlayerAlert.this.thumbMaxScale));
                    int x = (int) (this.rect.centerX() - ((float) (s / 2)));
                    int y = (int) (this.rect.centerY() - ((float) (s / 2)));
                    AudioPlayerAlert.this.noCoverDrawable.setBounds(x, y, x + s, y + s);
                    AudioPlayerAlert.this.noCoverDrawable.draw(canvas);
                }
                if (AudioPlayerAlert.this.hasNoCover != 1) {
                    super.onDraw(canvas);
                }
            }
        };
        this.placeholderImageView.setRoundRadius(AndroidUtilities.dp(20.0f));
        this.placeholderImageView.setPivotX(0.0f);
        this.placeholderImageView.setPivotY(0.0f);
        this.placeholderImageView.setOnClickListener(new AudioPlayerAlert$$Lambda$0(this));
        this.titleTextView = new TextView(context);
        this.titleTextView.setTextColor(Theme.getColor("player_actionBarTitle"));
        this.titleTextView.setTextSize(1, 15.0f);
        this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.titleTextView.setEllipsize(TruncateAt.END);
        this.titleTextView.setSingleLine(true);
        FrameLayout frameLayout = this.playerLayout;
        frameLayout.addView(this.titleTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 72.0f, 18.0f, 60.0f, 0.0f));
        this.authorTextView = new TextView(context);
        this.authorTextView.setTextColor(Theme.getColor("player_time"));
        this.authorTextView.setTextSize(1, 14.0f);
        this.authorTextView.setEllipsize(TruncateAt.END);
        this.authorTextView.setSingleLine(true);
        frameLayout = this.playerLayout;
        frameLayout.addView(this.authorTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 72.0f, 40.0f, 60.0f, 0.0f));
        this.optionsButton = new ActionBarMenuItem(context, null, 0, Theme.getColor("player_actionBarItems"));
        this.optionsButton.setLongClickEnabled(false);
        this.optionsButton.setIcon((int) R.drawable.ic_ab_other);
        this.optionsButton.setAdditionalOffset(-AndroidUtilities.dp(120.0f));
        frameLayout = this.playerLayout;
        frameLayout.addView(this.optionsButton, LayoutHelper.createFrame(40, 40.0f, 53, 0.0f, 19.0f, 10.0f, 0.0f));
        this.optionsButton.addSubItem(1, LocaleController.getString("Forward", R.string.Forward));
        this.optionsButton.addSubItem(2, LocaleController.getString("ShareFile", R.string.ShareFile));
        this.optionsButton.addSubItem(4, LocaleController.getString("ShowInChat", R.string.ShowInChat));
        this.optionsButton.setOnClickListener(new AudioPlayerAlert$$Lambda$1(this));
        this.optionsButton.setDelegate(new AudioPlayerAlert$$Lambda$2(this));
        this.seekBarView = new SeekBarView(context);
        this.seekBarView.setDelegate(AudioPlayerAlert$$Lambda$3.$instance);
        frameLayout = this.playerLayout;
        frameLayout.addView(this.seekBarView, LayoutHelper.createFrame(-1, 30.0f, 51, 8.0f, 62.0f, 8.0f, 0.0f));
        this.progressView = new LineProgressView(context);
        this.progressView.setVisibility(4);
        this.progressView.setBackgroundColor(Theme.getColor("player_progressBackground"));
        this.progressView.setProgressColor(Theme.getColor("player_progress"));
        frameLayout = this.playerLayout;
        frameLayout.addView(this.progressView, LayoutHelper.createFrame(-1, 2.0f, 51, 20.0f, 78.0f, 20.0f, 0.0f));
        this.timeTextView = new SimpleTextView(context);
        this.timeTextView.setTextSize(12);
        this.timeTextView.setText("0:00");
        this.timeTextView.setTextColor(Theme.getColor("player_time"));
        frameLayout = this.playerLayout;
        frameLayout.addView(this.timeTextView, LayoutHelper.createFrame(100, -2.0f, 51, 20.0f, 92.0f, 0.0f, 0.0f));
        this.durationTextView = new TextView(context);
        this.durationTextView.setTextSize(1, 12.0f);
        this.durationTextView.setTextColor(Theme.getColor("player_time"));
        this.durationTextView.setGravity(17);
        frameLayout = this.playerLayout;
        frameLayout.addView(this.durationTextView, LayoutHelper.createFrame(-2, -2.0f, 53, 0.0f, 90.0f, 20.0f, 0.0f));
        FrameLayout bottomView = new FrameLayout(context) {
            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                int dist = ((right - left) - AndroidUtilities.dp(248.0f)) / 4;
                for (int a = 0; a < 5; a++) {
                    int l = AndroidUtilities.dp((float) ((a * 48) + 4)) + (dist * a);
                    int t = AndroidUtilities.dp(9.0f);
                    AudioPlayerAlert.this.buttons[a].layout(l, t, AudioPlayerAlert.this.buttons[a].getMeasuredWidth() + l, AudioPlayerAlert.this.buttons[a].getMeasuredHeight() + t);
                }
            }
        };
        this.playerLayout.addView(bottomView, LayoutHelper.createFrame(-1, 66.0f, 51, 0.0f, 106.0f, 0.0f, 0.0f));
        View[] viewArr = this.buttons;
        ActionBarMenuItem actionBarMenuItem = new ActionBarMenuItem(context, null, 0, 0);
        this.shuffleButton = actionBarMenuItem;
        viewArr[0] = actionBarMenuItem;
        this.shuffleButton.setLongClickEnabled(false);
        this.shuffleButton.setAdditionalOffset(-AndroidUtilities.dp(10.0f));
        bottomView.addView(this.shuffleButton, LayoutHelper.createFrame(48, 48, 51));
        this.shuffleButton.setOnClickListener(new AudioPlayerAlert$$Lambda$4(this));
        TextView textView = this.shuffleButton.addSubItem(1, LocaleController.getString("ReverseOrder", R.string.ReverseOrder));
        textView.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(16.0f), 0);
        this.playOrderButtons[0] = context.getResources().getDrawable(R.drawable.music_reverse).mutate();
        textView.setCompoundDrawablePadding(AndroidUtilities.dp(8.0f));
        textView.setCompoundDrawablesWithIntrinsicBounds(this.playOrderButtons[0], null, null, null);
        textView = this.shuffleButton.addSubItem(2, LocaleController.getString("Shuffle", R.string.Shuffle));
        textView.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(16.0f), 0);
        this.playOrderButtons[1] = context.getResources().getDrawable(R.drawable.pl_shuffle).mutate();
        textView.setCompoundDrawablePadding(AndroidUtilities.dp(8.0f));
        textView.setCompoundDrawablesWithIntrinsicBounds(this.playOrderButtons[1], null, null, null);
        this.shuffleButton.setDelegate(new AudioPlayerAlert$$Lambda$5(this));
        viewArr = this.buttons;
        View imageView = new ImageView(context);
        viewArr[1] = imageView;
        imageView.setScaleType(ScaleType.CENTER);
        imageView.setImageDrawable(Theme.createSimpleSelectorDrawable(context, R.drawable.pl_previous, Theme.getColor("player_button"), Theme.getColor("player_buttonActive")));
        bottomView.addView(imageView, LayoutHelper.createFrame(48, 48, 51));
        imageView.setOnClickListener(AudioPlayerAlert$$Lambda$6.$instance);
        viewArr = this.buttons;
        ImageView imageView2 = new ImageView(context);
        this.playButton = imageView2;
        viewArr[2] = imageView2;
        this.playButton.setScaleType(ScaleType.CENTER);
        this.playButton.setImageDrawable(Theme.createSimpleSelectorDrawable(context, R.drawable.pl_play, Theme.getColor("player_button"), Theme.getColor("player_buttonActive")));
        bottomView.addView(this.playButton, LayoutHelper.createFrame(48, 48, 51));
        this.playButton.setOnClickListener(AudioPlayerAlert$$Lambda$7.$instance);
        viewArr = this.buttons;
        imageView = new ImageView(context);
        viewArr[3] = imageView;
        imageView.setScaleType(ScaleType.CENTER);
        imageView.setImageDrawable(Theme.createSimpleSelectorDrawable(context, R.drawable.pl_next, Theme.getColor("player_button"), Theme.getColor("player_buttonActive")));
        bottomView.addView(imageView, LayoutHelper.createFrame(48, 48, 51));
        imageView.setOnClickListener(AudioPlayerAlert$$Lambda$8.$instance);
        viewArr = this.buttons;
        imageView2 = new ImageView(context);
        this.repeatButton = imageView2;
        viewArr[4] = imageView2;
        this.repeatButton.setScaleType(ScaleType.CENTER);
        this.repeatButton.setPadding(0, 0, AndroidUtilities.dp(8.0f), 0);
        bottomView.addView(this.repeatButton, LayoutHelper.createFrame(50, 48, 51));
        this.repeatButton.setOnClickListener(new AudioPlayerAlert$$Lambda$9(this));
        this.listView = new RecyclerListView(context) {
            boolean ignoreLayout;

            /* JADX WARNING: Removed duplicated region for block: B:34:? A:{SYNTHETIC, RETURN} */
            /* JADX WARNING: Removed duplicated region for block: B:19:0x008f  */
            protected void onLayout(boolean r13, int r14, int r15, int r16, int r17) {
                /*
                r12 = this;
                super.onLayout(r13, r14, r15, r16, r17);
                r0 = org.telegram.ui.Components.AudioPlayerAlert.this;
                r0 = r0.searchOpenPosition;
                r1 = -1;
                if (r0 == r1) goto L_0x0045;
            L_0x000c:
                r0 = org.telegram.ui.Components.AudioPlayerAlert.this;
                r0 = r0.actionBar;
                r0 = r0.isSearchFieldVisible();
                if (r0 != 0) goto L_0x0045;
            L_0x0018:
                r0 = 1;
                r12.ignoreLayout = r0;
                r0 = org.telegram.ui.Components.AudioPlayerAlert.this;
                r0 = r0.layoutManager;
                r1 = org.telegram.ui.Components.AudioPlayerAlert.this;
                r1 = r1.searchOpenPosition;
                r2 = org.telegram.ui.Components.AudioPlayerAlert.this;
                r2 = r2.searchOpenOffset;
                r0.scrollToPositionWithOffset(r1, r2);
                r1 = 0;
                r0 = r12;
                r2 = r14;
                r3 = r15;
                r4 = r16;
                r5 = r17;
                super.onLayout(r1, r2, r3, r4, r5);
                r0 = 0;
                r12.ignoreLayout = r0;
                r0 = org.telegram.ui.Components.AudioPlayerAlert.this;
                r1 = -1;
                r0.searchOpenPosition = r1;
            L_0x0044:
                return;
            L_0x0045:
                r0 = org.telegram.ui.Components.AudioPlayerAlert.this;
                r0 = r0.scrollToSong;
                if (r0 == 0) goto L_0x0044;
            L_0x004d:
                r0 = org.telegram.ui.Components.AudioPlayerAlert.this;
                r1 = 0;
                r0.scrollToSong = r1;
                r9 = 0;
                r0 = org.telegram.messenger.MediaController.getInstance();
                r11 = r0.getPlayingMessageObject();
                if (r11 == 0) goto L_0x0044;
            L_0x005e:
                r0 = org.telegram.ui.Components.AudioPlayerAlert.this;
                r0 = r0.listView;
                r8 = r0.getChildCount();
                r6 = 0;
            L_0x0069:
                if (r6 >= r8) goto L_0x008d;
            L_0x006b:
                r0 = org.telegram.ui.Components.AudioPlayerAlert.this;
                r0 = r0.listView;
                r7 = r0.getChildAt(r6);
                r0 = r7 instanceof org.telegram.ui.Cells.AudioPlayerCell;
                if (r0 == 0) goto L_0x00ba;
            L_0x0079:
                r0 = r7;
                r0 = (org.telegram.ui.Cells.AudioPlayerCell) r0;
                r0 = r0.getMessageObject();
                if (r0 != r11) goto L_0x00ba;
            L_0x0082:
                r0 = r7.getBottom();
                r1 = r12.getMeasuredHeight();
                if (r0 > r1) goto L_0x008d;
            L_0x008c:
                r9 = 1;
            L_0x008d:
                if (r9 != 0) goto L_0x0044;
            L_0x008f:
                r0 = org.telegram.ui.Components.AudioPlayerAlert.this;
                r0 = r0.playlist;
                r10 = r0.indexOf(r11);
                if (r10 < 0) goto L_0x0044;
            L_0x009b:
                r0 = 1;
                r12.ignoreLayout = r0;
                r0 = org.telegram.messenger.SharedConfig.playOrderReversed;
                if (r0 == 0) goto L_0x00bd;
            L_0x00a2:
                r0 = org.telegram.ui.Components.AudioPlayerAlert.this;
                r0 = r0.layoutManager;
                r0.scrollToPosition(r10);
            L_0x00ab:
                r1 = 0;
                r0 = r12;
                r2 = r14;
                r3 = r15;
                r4 = r16;
                r5 = r17;
                super.onLayout(r1, r2, r3, r4, r5);
                r0 = 0;
                r12.ignoreLayout = r0;
                goto L_0x0044;
            L_0x00ba:
                r6 = r6 + 1;
                goto L_0x0069;
            L_0x00bd:
                r0 = org.telegram.ui.Components.AudioPlayerAlert.this;
                r0 = r0.layoutManager;
                r1 = org.telegram.ui.Components.AudioPlayerAlert.this;
                r1 = r1.playlist;
                r1 = r1.size();
                r1 = r1 - r10;
                r0.scrollToPosition(r1);
                goto L_0x00ab;
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AudioPlayerAlert.7.onLayout(boolean, int, int, int, int):void");
            }

            public void requestLayout() {
                if (!this.ignoreLayout) {
                    super.requestLayout();
                }
            }

            protected boolean allowSelectChildAtPosition(float x, float y) {
                float p = AudioPlayerAlert.this.playerLayout.getY() + ((float) AudioPlayerAlert.this.playerLayout.getMeasuredHeight());
                return AudioPlayerAlert.this.playerLayout == null || y > AudioPlayerAlert.this.playerLayout.getY() + ((float) AudioPlayerAlert.this.playerLayout.getMeasuredHeight());
            }

            public boolean drawChild(Canvas canvas, View child, long drawingTime) {
                int measuredHeight;
                canvas.save();
                if (AudioPlayerAlert.this.actionBar != null) {
                    measuredHeight = AudioPlayerAlert.this.actionBar.getMeasuredHeight();
                } else {
                    measuredHeight = 0;
                }
                canvas.clipRect(0, measuredHeight + AndroidUtilities.dp(50.0f), getMeasuredWidth(), getMeasuredHeight());
                boolean result = super.drawChild(canvas, child, drawingTime);
                canvas.restore();
                return result;
            }
        };
        this.listView.setPadding(0, 0, 0, AndroidUtilities.dp(8.0f));
        this.listView.setClipToPadding(false);
        RecyclerListView recyclerListView = this.listView;
        LayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView.setLayoutManager(linearLayoutManager);
        this.listView.setHorizontalScrollBarEnabled(false);
        this.listView.setVerticalScrollBarEnabled(false);
        this.containerView.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        recyclerListView = this.listView;
        Adapter listAdapter = new ListAdapter(context);
        this.listAdapter = listAdapter;
        recyclerListView.setAdapter(listAdapter);
        this.listView.setGlowColor(Theme.getColor("dialogScrollGlow"));
        this.listView.setOnItemClickListener(AudioPlayerAlert$$Lambda$10.$instance);
        this.listView.setOnScrollListener(new OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 1 && AudioPlayerAlert.this.searching && AudioPlayerAlert.this.searchWas) {
                    AndroidUtilities.hideKeyboard(AudioPlayerAlert.this.getCurrentFocus());
                }
            }

            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                AudioPlayerAlert.this.updateLayout();
            }
        });
        this.playlist = MediaController.getInstance().getPlaylist();
        this.listAdapter.notifyDataSetChanged();
        this.containerView.addView(this.playerLayout, LayoutHelper.createFrame(-1, 178.0f));
        this.containerView.addView(this.shadow2, LayoutHelper.createFrame(-1, 3.0f));
        ViewGroup viewGroup = this.containerView;
        viewGroup.addView(this.placeholderImageView, LayoutHelper.createFrame(40, 40.0f, 51, 17.0f, 19.0f, 0.0f, 0.0f));
        this.containerView.addView(this.shadow, LayoutHelper.createFrame(-1, 3.0f));
        this.containerView.addView(this.actionBar);
        updateTitle(false);
        updateRepeatButton();
        updateShuffleButton();
    }

    final /* synthetic */ void lambda$new$0$AudioPlayerAlert(View view) {
        float f = 0.0f;
        if (this.animatorSet != null) {
            this.animatorSet.cancel();
            this.animatorSet = null;
        }
        this.animatorSet = new AnimatorSet();
        if (this.scrollOffsetY <= this.actionBar.getMeasuredHeight()) {
            AnimatorSet animatorSet = this.animatorSet;
            Animator[] animatorArr = new Animator[1];
            String str = "fullAnimationProgress";
            float[] fArr = new float[1];
            if (!this.isInFullMode) {
                f = 1.0f;
            }
            fArr[0] = f;
            animatorArr[0] = ObjectAnimator.ofFloat(this, str, fArr);
            animatorSet.playTogether(animatorArr);
        } else {
            float f2;
            AnimatorSet animatorSet2 = this.animatorSet;
            Animator[] animatorArr2 = new Animator[4];
            String str2 = "fullAnimationProgress";
            float[] fArr2 = new float[1];
            fArr2[0] = this.isInFullMode ? 0.0f : 1.0f;
            animatorArr2[0] = ObjectAnimator.ofFloat(this, str2, fArr2);
            ActionBar actionBar = this.actionBar;
            String str3 = "alpha";
            float[] fArr3 = new float[1];
            fArr3[0] = this.isInFullMode ? 0.0f : 1.0f;
            animatorArr2[1] = ObjectAnimator.ofFloat(actionBar, str3, fArr3);
            View view2 = this.shadow;
            String str4 = "alpha";
            float[] fArr4 = new float[1];
            if (this.isInFullMode) {
                f2 = 0.0f;
            } else {
                f2 = 1.0f;
            }
            fArr4[0] = f2;
            animatorArr2[2] = ObjectAnimator.ofFloat(view2, str4, fArr4);
            View view3 = this.shadow2;
            str3 = "alpha";
            fArr3 = new float[1];
            if (!this.isInFullMode) {
                f = 1.0f;
            }
            fArr3[0] = f;
            animatorArr2[3] = ObjectAnimator.ofFloat(view3, str3, fArr3);
            animatorSet2.playTogether(animatorArr2);
        }
        this.animatorSet.setInterpolator(new DecelerateInterpolator());
        this.animatorSet.setDuration(250);
        this.animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                if (animation.equals(AudioPlayerAlert.this.animatorSet)) {
                    if (AudioPlayerAlert.this.isInFullMode) {
                        if (AudioPlayerAlert.this.hasOptions) {
                            AudioPlayerAlert.this.menuItem.setVisibility(0);
                        }
                        AudioPlayerAlert.this.searchItem.setVisibility(4);
                    } else {
                        AudioPlayerAlert.this.listView.setScrollEnabled(true);
                        if (AudioPlayerAlert.this.hasOptions) {
                            AudioPlayerAlert.this.menuItem.setVisibility(4);
                        }
                        AudioPlayerAlert.this.searchItem.setVisibility(0);
                    }
                    AudioPlayerAlert.this.animatorSet = null;
                }
            }
        });
        this.animatorSet.start();
        if (this.hasOptions) {
            this.menuItem.setVisibility(0);
        }
        this.searchItem.setVisibility(0);
        this.isInFullMode = !this.isInFullMode;
        this.listView.setScrollEnabled(false);
        if (this.isInFullMode) {
            this.shuffleButton.setAdditionalOffset(-AndroidUtilities.dp(68.0f));
        } else {
            this.shuffleButton.setAdditionalOffset(-AndroidUtilities.dp(10.0f));
        }
    }

    final /* synthetic */ void lambda$new$1$AudioPlayerAlert(View v) {
        this.optionsButton.toggleSubMenu();
    }

    final /* synthetic */ void lambda$new$3$AudioPlayerAlert(View v) {
        this.shuffleButton.toggleSubMenu();
    }

    final /* synthetic */ void lambda$new$4$AudioPlayerAlert(int id) {
        MediaController.getInstance().toggleShuffleMusic(id);
        updateShuffleButton();
        this.listAdapter.notifyDataSetChanged();
    }

    static final /* synthetic */ void lambda$new$6$AudioPlayerAlert(View v) {
        if (!MediaController.getInstance().isDownloadingCurrentMessage()) {
            if (MediaController.getInstance().isMessagePaused()) {
                MediaController.getInstance().playMessage(MediaController.getInstance().getPlayingMessageObject());
            } else {
                MediaController.getInstance().lambda$startAudioAgain$5$MediaController(MediaController.getInstance().getPlayingMessageObject());
            }
        }
    }

    final /* synthetic */ void lambda$new$8$AudioPlayerAlert(View v) {
        SharedConfig.toggleRepeatMode();
        updateRepeatButton();
    }

    static final /* synthetic */ void lambda$new$9$AudioPlayerAlert(View view, int position) {
        if (view instanceof AudioPlayerCell) {
            ((AudioPlayerCell) view).didPressedButton();
        }
    }

    @Keep
    public void setFullAnimationProgress(float value) {
        this.fullAnimationProgress = value;
        this.placeholderImageView.setRoundRadius(AndroidUtilities.dp(20.0f * (1.0f - this.fullAnimationProgress)));
        float scale = 1.0f + (this.thumbMaxScale * this.fullAnimationProgress);
        this.placeholderImageView.setScaleX(scale);
        this.placeholderImageView.setScaleY(scale);
        float translationY = this.placeholderImageView.getTranslationY();
        this.placeholderImageView.setTranslationX(((float) this.thumbMaxX) * this.fullAnimationProgress);
        this.placeholderImageView.setTranslationY(this.startTranslation + ((this.endTranslation - this.startTranslation) * this.fullAnimationProgress));
        this.playerLayout.setTranslationY(this.panelStartTranslation + ((this.panelEndTranslation - this.panelStartTranslation) * this.fullAnimationProgress));
        this.shadow2.setTranslationY((this.panelStartTranslation + ((this.panelEndTranslation - this.panelStartTranslation) * this.fullAnimationProgress)) + ((float) this.playerLayout.getMeasuredHeight()));
        this.menuItem.setAlpha(this.fullAnimationProgress);
        this.searchItem.setAlpha(1.0f - this.fullAnimationProgress);
        this.avatarContainer.setAlpha(1.0f - this.fullAnimationProgress);
        this.actionBar.getTitleTextView().setAlpha(this.fullAnimationProgress);
        this.actionBar.getSubtitleTextView().setAlpha(this.fullAnimationProgress);
    }

    @Keep
    public float getFullAnimationProgress() {
        return this.fullAnimationProgress;
    }

    private void onSubItemClick(int id) {
        Throwable e;
        MessageObject messageObject = MediaController.getInstance().getPlayingMessageObject();
        if (messageObject != null && this.parentActivity != null) {
            Bundle args;
            Builder builder;
            if (id == 1) {
                if (UserConfig.selectedAccount != this.currentAccount) {
                    this.parentActivity.switchToAccount(this.currentAccount, true);
                }
                args = new Bundle();
                args.putBoolean("onlySelect", true);
                args.putInt("dialogsType", 3);
                BaseFragment dialogsActivity = new DialogsActivity(args);
                ArrayList<MessageObject> fmessages = new ArrayList();
                fmessages.add(messageObject);
                dialogsActivity.setDelegate(new AudioPlayerAlert$$Lambda$11(this, fmessages));
                this.parentActivity.presentFragment(dialogsActivity);
                dismiss();
                return;
            } else if (id == 2) {
                File f = null;
                try {
                    if (!TextUtils.isEmpty(messageObject.messageOwner.attachPath)) {
                        File file = new File(messageObject.messageOwner.attachPath);
                        try {
                            if (file.exists()) {
                                f = file;
                            } else {
                                f = null;
                            }
                        } catch (Exception e2) {
                            e = e2;
                            f = file;
                        }
                    }
                    if (f == null) {
                        f = FileLoader.getPathToMessage(messageObject.messageOwner);
                    }
                    if (f.exists()) {
                        Intent intent = new Intent("android.intent.action.SEND");
                        if (messageObject != null) {
                            intent.setType(messageObject.getMimeType());
                        } else {
                            intent.setType("audio/mp3");
                        }
                        if (VERSION.SDK_INT >= 24) {
                            try {
                                intent = intent;
                                intent.putExtra("android.intent.extra.STREAM", FileProvider.getUriForFile(ApplicationLoader.applicationContext, "org.telegram.messenger.provider", f));
                                intent.setFlags(1);
                            } catch (Exception e3) {
                                intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(f));
                            }
                        } else {
                            intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(f));
                        }
                        this.parentActivity.startActivityForResult(Intent.createChooser(intent, LocaleController.getString("ShareFile", R.string.ShareFile)), 500);
                        return;
                    }
                    builder = new Builder(this.parentActivity);
                    builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                    builder.setMessage(LocaleController.getString("PleaseDownload", R.string.PleaseDownload));
                    builder.show();
                    return;
                } catch (Exception e4) {
                    e = e4;
                }
            } else if (id == 3) {
                builder = new Builder(this.parentActivity);
                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                boolean[] deleteForAll = new boolean[1];
                int lower_id = (int) messageObject.getDialogId();
                if (lower_id != 0) {
                    User currentUser;
                    Chat currentChat;
                    if (lower_id > 0) {
                        currentUser = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(lower_id));
                        currentChat = null;
                    } else {
                        currentUser = null;
                        currentChat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-lower_id));
                    }
                    if (!(currentUser == null && ChatObject.isChannel(currentChat))) {
                        int currentDate = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
                        if (!((currentUser == null || currentUser.id == UserConfig.getInstance(this.currentAccount).getClientUserId()) && currentChat == null) && ((messageObject.messageOwner.action == null || (messageObject.messageOwner.action instanceof TL_messageActionEmpty)) && messageObject.isOut() && currentDate - messageObject.messageOwner.date <= 172800)) {
                            int dp;
                            int dp2;
                            View frameLayout = new FrameLayout(this.parentActivity);
                            CheckBoxCell cell = new CheckBoxCell(this.parentActivity, 1);
                            cell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                            if (currentChat != null) {
                                cell.setText(LocaleController.getString("DeleteForAll", R.string.DeleteForAll), "", false, false);
                            } else {
                                cell.setText(LocaleController.formatString("DeleteForUser", R.string.DeleteForUser, UserObject.getFirstName(currentUser)), "", false, false);
                            }
                            if (LocaleController.isRTL) {
                                dp = AndroidUtilities.dp(16.0f);
                            } else {
                                dp = AndroidUtilities.dp(8.0f);
                            }
                            if (LocaleController.isRTL) {
                                dp2 = AndroidUtilities.dp(8.0f);
                            } else {
                                dp2 = AndroidUtilities.dp(16.0f);
                            }
                            cell.setPadding(dp, 0, dp2, 0);
                            frameLayout.addView(cell, LayoutHelper.createFrame(-1, 48.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
                            cell.setOnClickListener(new AudioPlayerAlert$$Lambda$12(deleteForAll));
                            builder.setView(frameLayout);
                        }
                    }
                }
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new AudioPlayerAlert$$Lambda$13(this, messageObject, deleteForAll));
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                builder.show();
                return;
            } else if (id == 4) {
                if (UserConfig.selectedAccount != this.currentAccount) {
                    this.parentActivity.switchToAccount(this.currentAccount, true);
                }
                args = new Bundle();
                long did = messageObject.getDialogId();
                int lower_part = (int) did;
                int high_id = (int) (did >> 32);
                if (lower_part == 0) {
                    args.putInt("enc_id", high_id);
                } else if (high_id == 1) {
                    args.putInt("chat_id", lower_part);
                } else if (lower_part > 0) {
                    args.putInt("user_id", lower_part);
                } else if (lower_part < 0) {
                    Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-lower_part));
                    if (!(chat == null || chat.migrated_to == null)) {
                        args.putInt("migrated_to", lower_part);
                        lower_part = -chat.migrated_to.channel_id;
                    }
                    args.putInt("chat_id", -lower_part);
                }
                args.putInt("message_id", messageObject.getId());
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                this.parentActivity.presentFragment(new ChatActivity(args), false, false);
                dismiss();
                return;
            } else {
                return;
            }
        }
        return;
        FileLog.e(e);
    }

    final /* synthetic */ void lambda$onSubItemClick$10$AudioPlayerAlert(ArrayList fmessages, DialogsActivity fragment1, ArrayList dids, CharSequence message, boolean param) {
        long did;
        if (dids.size() > 1 || ((Long) dids.get(0)).longValue() == ((long) UserConfig.getInstance(this.currentAccount).getClientUserId()) || message != null) {
            for (int a = 0; a < dids.size(); a++) {
                did = ((Long) dids.get(a)).longValue();
                if (message != null) {
                    SendMessagesHelper.getInstance(this.currentAccount).sendMessage(message.toString(), did, null, null, true, null, null, null);
                }
                SendMessagesHelper.getInstance(this.currentAccount).sendMessage(fmessages, did);
            }
            fragment1.lambda$checkDiscard$18$ChatUsersActivity();
            return;
        }
        did = ((Long) dids.get(0)).longValue();
        int lower_part = (int) did;
        int high_part = (int) (did >> 32);
        Bundle args1 = new Bundle();
        args1.putBoolean("scrollToTopOnResume", true);
        if (lower_part == 0) {
            args1.putInt("enc_id", high_part);
        } else if (lower_part > 0) {
            args1.putInt("user_id", lower_part);
        } else if (lower_part < 0) {
            args1.putInt("chat_id", -lower_part);
        }
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
        ChatActivity chatActivity = new ChatActivity(args1);
        if (this.parentActivity.presentFragment(chatActivity, true, false)) {
            chatActivity.showFieldPanelForForward(true, fmessages);
        } else {
            fragment1.lambda$checkDiscard$18$ChatUsersActivity();
        }
    }

    static final /* synthetic */ void lambda$onSubItemClick$11$AudioPlayerAlert(boolean[] deleteForAll, View v) {
        boolean z;
        CheckBoxCell cell1 = (CheckBoxCell) v;
        if (deleteForAll[0]) {
            z = false;
        } else {
            z = true;
        }
        deleteForAll[0] = z;
        cell1.setChecked(deleteForAll[0], true);
    }

    final /* synthetic */ void lambda$onSubItemClick$12$AudioPlayerAlert(MessageObject messageObject, boolean[] deleteForAll, DialogInterface dialogInterface, int i) {
        dismiss();
        ArrayList<Integer> arr = new ArrayList();
        arr.add(Integer.valueOf(messageObject.getId()));
        ArrayList<Long> random_ids = null;
        EncryptedChat encryptedChat = null;
        if (((int) messageObject.getDialogId()) == 0 && messageObject.messageOwner.random_id != 0) {
            random_ids = new ArrayList();
            random_ids.add(Long.valueOf(messageObject.messageOwner.random_id));
            encryptedChat = MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf((int) (messageObject.getDialogId() >> 32)));
        }
        MessagesController.getInstance(this.currentAccount).deleteMessages(arr, random_ids, encryptedChat, messageObject.messageOwner.to_id.channel_id, deleteForAll[0]);
    }

    private int getCurrentTop() {
        int i = 0;
        if (this.listView.getChildCount() != 0) {
            View child = this.listView.getChildAt(0);
            Holder holder = (Holder) this.listView.findContainingViewHolder(child);
            if (holder != null) {
                int paddingTop = this.listView.getPaddingTop();
                if (holder.getAdapterPosition() == 0 && child.getTop() >= 0) {
                    i = child.getTop();
                }
                return paddingTop - i;
            }
        }
        return -1000;
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        MessageObject messageObject;
        if (id == NotificationCenter.messagePlayingDidStart || id == NotificationCenter.messagePlayingPlayStateChanged || id == NotificationCenter.messagePlayingDidReset) {
            boolean z = id == NotificationCenter.messagePlayingDidReset && ((Boolean) args[1]).booleanValue();
            updateTitle(z);
            int count;
            int a;
            View view;
            AudioPlayerCell cell;
            if (id == NotificationCenter.messagePlayingDidReset || id == NotificationCenter.messagePlayingPlayStateChanged) {
                count = this.listView.getChildCount();
                for (a = 0; a < count; a++) {
                    view = this.listView.getChildAt(a);
                    if (view instanceof AudioPlayerCell) {
                        cell = (AudioPlayerCell) view;
                        messageObject = cell.getMessageObject();
                        if (messageObject != null && (messageObject.isVoice() || messageObject.isMusic())) {
                            cell.updateButtonState(false, true);
                        }
                    }
                }
            } else if (id == NotificationCenter.messagePlayingDidStart && ((MessageObject) args[0]).eventId == 0) {
                count = this.listView.getChildCount();
                for (a = 0; a < count; a++) {
                    view = this.listView.getChildAt(a);
                    if (view instanceof AudioPlayerCell) {
                        cell = (AudioPlayerCell) view;
                        MessageObject messageObject1 = cell.getMessageObject();
                        if (messageObject1 != null && (messageObject1.isVoice() || messageObject1.isMusic())) {
                            cell.updateButtonState(false, true);
                        }
                    }
                }
            }
        } else if (id == NotificationCenter.messagePlayingProgressDidChanged) {
            messageObject = MediaController.getInstance().getPlayingMessageObject();
            if (messageObject != null && messageObject.isMusic()) {
                updateProgress(messageObject);
            }
        } else if (id == NotificationCenter.musicDidLoad) {
            this.playlist = MediaController.getInstance().getPlaylist();
            this.listAdapter.notifyDataSetChanged();
        }
    }

    protected boolean canDismissWithSwipe() {
        return false;
    }

    private void updateLayout() {
        if (this.listView.getChildCount() > 0) {
            int newOffset;
            View child = this.listView.getChildAt(0);
            Holder holder = (Holder) this.listView.findContainingViewHolder(child);
            int top = child.getTop();
            if (top <= 0 || holder == null || holder.getAdapterPosition() != 0) {
                newOffset = 0;
            } else {
                newOffset = top;
            }
            if (this.searchWas || this.searching) {
                newOffset = 0;
            }
            if (this.scrollOffsetY != newOffset) {
                RecyclerListView recyclerListView = this.listView;
                this.scrollOffsetY = newOffset;
                recyclerListView.setTopGlowOffset(newOffset);
                this.playerLayout.setTranslationY((float) Math.max(this.actionBar.getMeasuredHeight(), this.scrollOffsetY));
                this.placeholderImageView.setTranslationY((float) Math.max(this.actionBar.getMeasuredHeight(), this.scrollOffsetY));
                this.shadow2.setTranslationY((float) (Math.max(this.actionBar.getMeasuredHeight(), this.scrollOffsetY) + this.playerLayout.getMeasuredHeight()));
                this.containerView.invalidate();
                AnimatorSet animatorSet;
                Animator[] animatorArr;
                if ((!this.inFullSize || this.scrollOffsetY > this.actionBar.getMeasuredHeight()) && !this.searchWas) {
                    if (this.actionBar.getTag() != null) {
                        if (this.actionBarAnimation != null) {
                            this.actionBarAnimation.cancel();
                        }
                        this.actionBar.setTag(null);
                        this.actionBarAnimation = new AnimatorSet();
                        animatorSet = this.actionBarAnimation;
                        animatorArr = new Animator[3];
                        animatorArr[0] = ObjectAnimator.ofFloat(this.actionBar, "alpha", new float[]{0.0f});
                        animatorArr[1] = ObjectAnimator.ofFloat(this.shadow, "alpha", new float[]{0.0f});
                        animatorArr[2] = ObjectAnimator.ofFloat(this.shadow2, "alpha", new float[]{0.0f});
                        animatorSet.playTogether(animatorArr);
                        this.actionBarAnimation.setDuration(180);
                        this.actionBarAnimation.start();
                    }
                } else if (this.actionBar.getTag() == null) {
                    if (this.actionBarAnimation != null) {
                        this.actionBarAnimation.cancel();
                    }
                    this.actionBar.setTag(Integer.valueOf(1));
                    this.actionBarAnimation = new AnimatorSet();
                    animatorSet = this.actionBarAnimation;
                    animatorArr = new Animator[3];
                    animatorArr[0] = ObjectAnimator.ofFloat(this.actionBar, "alpha", new float[]{1.0f});
                    animatorArr[1] = ObjectAnimator.ofFloat(this.shadow, "alpha", new float[]{1.0f});
                    animatorArr[2] = ObjectAnimator.ofFloat(this.shadow2, "alpha", new float[]{1.0f});
                    animatorSet.playTogether(animatorArr);
                    this.actionBarAnimation.setDuration(180);
                    this.actionBarAnimation.start();
                }
            }
            this.startTranslation = (float) Math.max(this.actionBar.getMeasuredHeight(), this.scrollOffsetY);
            this.panelStartTranslation = (float) Math.max(this.actionBar.getMeasuredHeight(), this.scrollOffsetY);
        }
    }

    public void dismiss() {
        super.lambda$new$4$EmbedBottomSheet();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidReset);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidStart);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.musicDidLoad);
        DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
    }

    public void onBackPressed() {
        if (this.actionBar == null || !this.actionBar.isSearchFieldVisible()) {
            super.onBackPressed();
        } else {
            this.actionBar.closeSearchField();
        }
    }

    public void onFailedDownload(String fileName, boolean canceled) {
    }

    public void onSuccessDownload(String fileName) {
    }

    public void onProgressDownload(String fileName, float progress) {
        this.progressView.setProgress(progress, true);
    }

    public void onProgressUpload(String fileName, float progress, boolean isEncrypted) {
    }

    public int getObserverTag() {
        return this.TAG;
    }

    private void updateShuffleButton() {
        Drawable drawable;
        if (SharedConfig.shuffleMusic) {
            drawable = getContext().getResources().getDrawable(R.drawable.pl_shuffle).mutate();
            drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("player_buttonActive"), Mode.MULTIPLY));
            this.shuffleButton.setIcon(drawable);
        } else {
            drawable = getContext().getResources().getDrawable(R.drawable.music_reverse).mutate();
            if (SharedConfig.playOrderReversed) {
                drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("player_buttonActive"), Mode.MULTIPLY));
            } else {
                drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("player_button"), Mode.MULTIPLY));
            }
            this.shuffleButton.setIcon(drawable);
        }
        this.playOrderButtons[0].setColorFilter(new PorterDuffColorFilter(Theme.getColor(SharedConfig.playOrderReversed ? "player_buttonActive" : "player_button"), Mode.MULTIPLY));
        this.playOrderButtons[1].setColorFilter(new PorterDuffColorFilter(Theme.getColor(SharedConfig.shuffleMusic ? "player_buttonActive" : "player_button"), Mode.MULTIPLY));
    }

    private void updateRepeatButton() {
        int mode = SharedConfig.repeatMode;
        if (mode == 0) {
            this.repeatButton.setImageResource(R.drawable.pl_repeat);
            this.repeatButton.setTag("player_button");
            this.repeatButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("player_button"), Mode.MULTIPLY));
        } else if (mode == 1) {
            this.repeatButton.setImageResource(R.drawable.pl_repeat);
            this.repeatButton.setTag("player_buttonActive");
            this.repeatButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("player_buttonActive"), Mode.MULTIPLY));
        } else if (mode == 2) {
            this.repeatButton.setImageResource(R.drawable.pl_repeat1);
            this.repeatButton.setTag("player_buttonActive");
            this.repeatButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("player_buttonActive"), Mode.MULTIPLY));
        }
    }

    private void updateProgress(MessageObject messageObject) {
        if (this.seekBarView != null) {
            if (!this.seekBarView.isDragging()) {
                this.seekBarView.setProgress(messageObject.audioProgress);
                this.seekBarView.setBufferedProgress(messageObject.bufferedProgress);
            }
            if (this.lastTime != messageObject.audioProgressSec) {
                this.lastTime = messageObject.audioProgressSec;
                this.timeTextView.setText(String.format("%d:%02d", new Object[]{Integer.valueOf(messageObject.audioProgressSec / 60), Integer.valueOf(messageObject.audioProgressSec % 60)}));
            }
        }
    }

    private void checkIfMusicDownloaded(MessageObject messageObject) {
        File cacheFile = null;
        if (messageObject.messageOwner.attachPath != null && messageObject.messageOwner.attachPath.length() > 0) {
            cacheFile = new File(messageObject.messageOwner.attachPath);
            if (!cacheFile.exists()) {
                cacheFile = null;
            }
        }
        if (cacheFile == null) {
            cacheFile = FileLoader.getPathToMessage(messageObject.messageOwner);
        }
        boolean canStream;
        if (SharedConfig.streamMedia && ((int) messageObject.getDialogId()) != 0 && messageObject.isMusic()) {
            canStream = true;
        } else {
            canStream = false;
        }
        if (cacheFile.exists() || canStream) {
            DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
            this.progressView.setVisibility(4);
            this.seekBarView.setVisibility(0);
            this.playButton.setEnabled(true);
            return;
        }
        String fileName = messageObject.getFileName();
        DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(fileName, this);
        Float progress = ImageLoader.getInstance().getFileProgress(fileName);
        this.progressView.setProgress(progress != null ? progress.floatValue() : 0.0f, false);
        this.progressView.setVisibility(0);
        this.seekBarView.setVisibility(4);
        this.playButton.setEnabled(false);
    }

    private void updateTitle(boolean shutdown) {
        MessageObject messageObject = MediaController.getInstance().getPlayingMessageObject();
        if ((messageObject == null && shutdown) || (messageObject != null && !messageObject.isMusic())) {
            dismiss();
        } else if (messageObject != null) {
            if (messageObject.eventId != 0 || messageObject.getId() <= -NUM) {
                this.hasOptions = false;
                this.menuItem.setVisibility(4);
                this.optionsButton.setVisibility(4);
            } else {
                this.hasOptions = true;
                if (!this.actionBar.isSearchFieldVisible()) {
                    this.menuItem.setVisibility(0);
                }
                this.optionsButton.setVisibility(0);
            }
            checkIfMusicDownloaded(messageObject);
            updateProgress(messageObject);
            if (MediaController.getInstance().isMessagePaused()) {
                this.playButton.setImageDrawable(Theme.createSimpleSelectorDrawable(this.playButton.getContext(), R.drawable.pl_play, Theme.getColor("player_button"), Theme.getColor("player_buttonActive")));
            } else {
                this.playButton.setImageDrawable(Theme.createSimpleSelectorDrawable(this.playButton.getContext(), R.drawable.pl_pause, Theme.getColor("player_button"), Theme.getColor("player_buttonActive")));
            }
            String title = messageObject.getMusicTitle();
            String author = messageObject.getMusicAuthor();
            this.titleTextView.setText(title);
            this.authorTextView.setText(author);
            this.actionBar.setTitle(title);
            this.actionBar.setSubtitle(author);
            String loadTitle = author + " " + title;
            AudioInfo audioInfo = MediaController.getInstance().getAudioInfo();
            if (audioInfo == null || audioInfo.getCover() == null) {
                String artworkUrl = messageObject.getArtworkUrl(false);
                if (TextUtils.isEmpty(artworkUrl)) {
                    this.placeholderImageView.setImageDrawable(null);
                    this.hasNoCover = 1;
                } else {
                    this.placeholderImageView.setImage(artworkUrl, null, null);
                    this.hasNoCover = 2;
                }
                this.placeholderImageView.invalidate();
            } else {
                this.hasNoCover = 0;
                this.placeholderImageView.setImageBitmap(audioInfo.getCover());
            }
            if (this.durationTextView != null) {
                CharSequence format;
                int duration = messageObject.getDuration();
                TextView textView = this.durationTextView;
                if (duration != 0) {
                    format = String.format("%d:%02d", new Object[]{Integer.valueOf(duration / 60), Integer.valueOf(duration % 60)});
                } else {
                    format = "-:--";
                }
                textView.setText(format);
            }
        }
    }
}
