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
import android.view.View.OnClickListener;
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
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.audioinfo.AudioInfo;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.LayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC.TL_messageActionEmpty;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemDelegate;
import org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.AudioPlayerCell;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.Components.SeekBarView.SeekBarViewDelegate;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.DialogsActivity.DialogsActivityDelegate;
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
    private boolean hasNoCover;
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
    private ArrayList<MessageObject> playlist = new ArrayList();
    private LineProgressView progressView;
    private ImageView repeatButton;
    private int scrollOffsetY = ConnectionsManager.DEFAULT_DATACENTER_ID;
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

    /* renamed from: org.telegram.ui.Components.AudioPlayerAlert$5 */
    class C10865 implements OnClickListener {

        /* renamed from: org.telegram.ui.Components.AudioPlayerAlert$5$1 */
        class C10851 extends AnimatorListenerAdapter {
            C10851() {
            }

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
        }

        C10865() {
        }

        public void onClick(View view) {
            if (AudioPlayerAlert.this.animatorSet != null) {
                AudioPlayerAlert.this.animatorSet.cancel();
                AudioPlayerAlert.this.animatorSet = null;
            }
            AudioPlayerAlert.this.animatorSet = new AnimatorSet();
            float f = 1.0f;
            AnimatorSet access$3100;
            Animator[] animatorArr;
            AudioPlayerAlert audioPlayerAlert;
            String str;
            float[] fArr;
            if (AudioPlayerAlert.this.scrollOffsetY <= AudioPlayerAlert.this.actionBar.getMeasuredHeight()) {
                access$3100 = AudioPlayerAlert.this.animatorSet;
                animatorArr = new Animator[1];
                audioPlayerAlert = AudioPlayerAlert.this;
                str = "fullAnimationProgress";
                fArr = new float[1];
                if (AudioPlayerAlert.this.isInFullMode) {
                    f = 0.0f;
                }
                fArr[0] = f;
                animatorArr[0] = ObjectAnimator.ofFloat(audioPlayerAlert, str, fArr);
                access$3100.playTogether(animatorArr);
            } else {
                access$3100 = AudioPlayerAlert.this.animatorSet;
                animatorArr = new Animator[4];
                audioPlayerAlert = AudioPlayerAlert.this;
                str = "fullAnimationProgress";
                fArr = new float[1];
                fArr[0] = AudioPlayerAlert.this.isInFullMode ? 0.0f : 1.0f;
                animatorArr[0] = ObjectAnimator.ofFloat(audioPlayerAlert, str, fArr);
                ActionBar access$1300 = AudioPlayerAlert.this.actionBar;
                str = "alpha";
                fArr = new float[1];
                fArr[0] = AudioPlayerAlert.this.isInFullMode ? 0.0f : 1.0f;
                animatorArr[1] = ObjectAnimator.ofFloat(access$1300, str, fArr);
                View access$1400 = AudioPlayerAlert.this.shadow;
                String str2 = "alpha";
                float[] fArr2 = new float[1];
                fArr2[0] = AudioPlayerAlert.this.isInFullMode ? 0.0f : 1.0f;
                animatorArr[2] = ObjectAnimator.ofFloat(access$1400, str2, fArr2);
                access$1400 = AudioPlayerAlert.this.shadow2;
                str2 = "alpha";
                fArr2 = new float[1];
                if (AudioPlayerAlert.this.isInFullMode) {
                    f = 0.0f;
                }
                fArr2[0] = f;
                animatorArr[3] = ObjectAnimator.ofFloat(access$1400, str2, fArr2);
                access$3100.playTogether(animatorArr);
            }
            AudioPlayerAlert.this.animatorSet.setInterpolator(new DecelerateInterpolator());
            AudioPlayerAlert.this.animatorSet.setDuration(250);
            AudioPlayerAlert.this.animatorSet.addListener(new C10851());
            AudioPlayerAlert.this.animatorSet.start();
            if (AudioPlayerAlert.this.hasOptions) {
                AudioPlayerAlert.this.menuItem.setVisibility(0);
            }
            AudioPlayerAlert.this.searchItem.setVisibility(0);
            AudioPlayerAlert.this.isInFullMode = AudioPlayerAlert.this.isInFullMode ^ true;
            AudioPlayerAlert.this.listView.setScrollEnabled(false);
            if (AudioPlayerAlert.this.isInFullMode) {
                AudioPlayerAlert.this.shuffleButton.setAdditionalOffset(-AndroidUtilities.dp(68.0f));
            } else {
                AudioPlayerAlert.this.shuffleButton.setAdditionalOffset(-AndroidUtilities.dp(10.0f));
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.AudioPlayerAlert$6 */
    class C10876 implements OnClickListener {
        C10876() {
        }

        public void onClick(View v) {
            AudioPlayerAlert.this.optionsButton.toggleSubMenu();
        }
    }

    /* renamed from: org.telegram.ui.Components.AudioPlayerAlert$2 */
    class C20372 extends ActionBarMenuItemSearchListener {
        C20372() {
        }

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
    }

    /* renamed from: org.telegram.ui.Components.AudioPlayerAlert$3 */
    class C20383 extends ActionBarMenuOnItemClick {
        C20383() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                AudioPlayerAlert.this.dismiss();
            } else {
                AudioPlayerAlert.this.onSubItemClick(id);
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.AudioPlayerAlert$7 */
    class C20407 implements ActionBarMenuItemDelegate {
        C20407() {
        }

        public void onItemClick(int id) {
            AudioPlayerAlert.this.onSubItemClick(id);
        }
    }

    /* renamed from: org.telegram.ui.Components.AudioPlayerAlert$8 */
    class C20418 implements SeekBarViewDelegate {
        C20418() {
        }

        public void onSeekBarDrag(float progress) {
            MediaController.getInstance().seekToProgress(MediaController.getInstance().getPlayingMessageObject(), progress);
        }
    }

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
            return 1 + AudioPlayerAlert.this.playlist.size();
        }

        public boolean isEnabled(ViewHolder holder) {
            if (!AudioPlayerAlert.this.searchWas) {
                if (holder.getAdapterPosition() <= 0) {
                    return false;
                }
            }
            return true;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (viewType != 0) {
                view = new AudioPlayerCell(this.context);
            } else {
                view = new View(this.context);
                view.setLayoutParams(new LayoutParams(-1, AndroidUtilities.dp(178.0f)));
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
            if (!AudioPlayerAlert.this.searchWas) {
                if (!AudioPlayerAlert.this.searching) {
                    if (i == 0) {
                        return 0;
                    }
                    return 1;
                }
            }
            return 1;
        }

        public void search(final String query) {
            try {
                if (this.searchTimer != null) {
                    this.searchTimer.cancel();
                }
            } catch (Throwable e) {
                FileLog.m3e(e);
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
                        FileLog.m3e(e);
                    }
                    ListAdapter.this.processSearch(query);
                }
            }, 200, 300);
        }

        private void processSearch(final String query) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    final ArrayList<MessageObject> copy = new ArrayList(AudioPlayerAlert.this.playlist);
                    Utilities.searchQueue.postRunnable(new Runnable() {
                        public void run() {
                            String search1 = query.trim().toLowerCase();
                            if (search1.length() == 0) {
                                ListAdapter.this.updateSearchResults(new ArrayList());
                                return;
                            }
                            String search2 = LocaleController.getInstance().getTranslitString(search1);
                            if (search1.equals(search2) || search2.length() == 0) {
                                search2 = null;
                            }
                            String[] search = new String[((search2 != null ? 1 : 0) + 1)];
                            search[0] = search1;
                            if (search2 != null) {
                                search[1] = search2;
                            }
                            ArrayList<MessageObject> resultArray = new ArrayList();
                            for (int a = 0; a < copy.size(); a++) {
                                MessageObject messageObject = (MessageObject) copy.get(a);
                                for (String q : search) {
                                    String name = messageObject.getDocumentName();
                                    if (name != null) {
                                        if (name.length() != 0) {
                                            if (!name.toLowerCase().contains(q)) {
                                                Document document;
                                                if (messageObject.type == 0) {
                                                    document = messageObject.messageOwner.media.webpage.document;
                                                } else {
                                                    document = messageObject.messageOwner.media.document;
                                                }
                                                boolean ok = false;
                                                int c = 0;
                                                while (c < document.attributes.size()) {
                                                    DocumentAttribute attribute = (DocumentAttribute) document.attributes.get(c);
                                                    if (attribute instanceof TL_documentAttributeAudio) {
                                                        if (attribute.performer != null) {
                                                            ok = attribute.performer.toLowerCase().contains(q);
                                                        }
                                                        if (!(ok || attribute.title == null)) {
                                                            ok = attribute.title.toLowerCase().contains(q);
                                                        }
                                                        if (ok) {
                                                            resultArray.add(messageObject);
                                                            break;
                                                        }
                                                    } else {
                                                        c++;
                                                    }
                                                }
                                                if (ok) {
                                                    resultArray.add(messageObject);
                                                    break;
                                                }
                                            } else {
                                                resultArray.add(messageObject);
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                            ListAdapter.this.updateSearchResults(resultArray);
                        }
                    });
                }
            });
        }

        private void updateSearchResults(final ArrayList<MessageObject> documents) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    AudioPlayerAlert.this.searchWas = true;
                    ListAdapter.this.searchResult = documents;
                    ListAdapter.this.notifyDataSetChanged();
                    AudioPlayerAlert.this.layoutManager.scrollToPosition(0);
                }
            });
        }
    }

    public AudioPlayerAlert(Context context) {
        Context context2 = context;
        super(context2, true);
        MessageObject messageObject = MediaController.getInstance().getPlayingMessageObject();
        if (messageObject != null) {
            r0.currentAccount = messageObject.currentAccount;
        } else {
            r0.currentAccount = UserConfig.selectedAccount;
        }
        r0.parentActivity = (LaunchActivity) context2;
        r0.noCoverDrawable = context.getResources().getDrawable(R.drawable.nocover).mutate();
        r0.noCoverDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_player_placeholder), Mode.MULTIPLY));
        r0.TAG = DownloadController.getInstance(r0.currentAccount).generateObserverTag();
        NotificationCenter.getInstance(r0.currentAccount).addObserver(r0, NotificationCenter.messagePlayingDidReset);
        NotificationCenter.getInstance(r0.currentAccount).addObserver(r0, NotificationCenter.messagePlayingPlayStateChanged);
        NotificationCenter.getInstance(r0.currentAccount).addObserver(r0, NotificationCenter.messagePlayingDidStarted);
        NotificationCenter.getInstance(r0.currentAccount).addObserver(r0, NotificationCenter.messagePlayingProgressDidChanged);
        NotificationCenter.getInstance(r0.currentAccount).addObserver(r0, NotificationCenter.musicDidLoaded);
        r0.shadowDrawable = context.getResources().getDrawable(R.drawable.sheet_shadow).mutate();
        r0.shadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_player_background), Mode.MULTIPLY));
        r0.paint.setColor(Theme.getColor(Theme.key_player_placeholderBackground));
        r0.containerView = new FrameLayout(context2) {
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
                int i = 0;
                if (AudioPlayerAlert.this.searching) {
                    padding = (AndroidUtilities.dp(178.0f) + ActionBar.getCurrentActionBarHeight()) + (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0);
                } else {
                    padding = contentSize < height ? height - contentSize : contentSize < height ? 0 : height - ((height / 5) * 3);
                    padding += ActionBar.getCurrentActionBarHeight() + (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0);
                }
                boolean z = true;
                if (AudioPlayerAlert.this.listView.getPaddingTop() != padding) {
                    this.ignoreLayout = true;
                    AudioPlayerAlert.this.listView.setPadding(0, padding, 0, AndroidUtilities.dp(8.0f));
                    this.ignoreLayout = false;
                }
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                AudioPlayerAlert audioPlayerAlert = AudioPlayerAlert.this;
                if (getMeasuredHeight() < height) {
                    z = false;
                }
                audioPlayerAlert.inFullSize = z;
                int currentActionBarHeight = height - ActionBar.getCurrentActionBarHeight();
                if (VERSION.SDK_INT >= 21) {
                    i = AndroidUtilities.statusBarHeight;
                }
                currentActionBarHeight = (currentActionBarHeight - i) - AndroidUtilities.dp(120.0f);
                int maxSize = Math.max(currentActionBarHeight, getMeasuredWidth());
                AudioPlayerAlert.this.thumbMaxX = ((getMeasuredWidth() - maxSize) / 2) - AndroidUtilities.dp(17.0f);
                AudioPlayerAlert.this.thumbMaxY = AndroidUtilities.dp(19.0f);
                AudioPlayerAlert.this.panelEndTranslation = (float) (getMeasuredHeight() - AudioPlayerAlert.this.playerLayout.getMeasuredHeight());
                AudioPlayerAlert.this.thumbMaxScale = (((float) maxSize) / ((float) AudioPlayerAlert.this.placeholderImageView.getMeasuredWidth())) - 1.0f;
                AudioPlayerAlert.this.endTranslation = (float) (ActionBar.getCurrentActionBarHeight() + AndroidUtilities.dp(5.0f));
                i = (int) Math.ceil((double) (((float) AudioPlayerAlert.this.placeholderImageView.getMeasuredHeight()) * (1.0f + AudioPlayerAlert.this.thumbMaxScale)));
                if (i > currentActionBarHeight) {
                    AudioPlayerAlert.this.endTranslation = AudioPlayerAlert.this.endTranslation - ((float) (i - currentActionBarHeight));
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
        r0.containerView.setWillNotDraw(false);
        r0.containerView.setPadding(backgroundPaddingLeft, 0, backgroundPaddingLeft, 0);
        r0.actionBar = new ActionBar(context2);
        r0.actionBar.setBackgroundColor(Theme.getColor(Theme.key_player_actionBar));
        r0.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        r0.actionBar.setItemsColor(Theme.getColor(Theme.key_player_actionBarItems), false);
        r0.actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_player_actionBarSelector), false);
        r0.actionBar.setTitleColor(Theme.getColor(Theme.key_player_actionBarTitle));
        r0.actionBar.setSubtitleColor(Theme.getColor(Theme.key_player_actionBarSubtitle));
        r0.actionBar.setAlpha(0.0f);
        r0.actionBar.setTitle("1");
        r0.actionBar.setSubtitle("1");
        r0.actionBar.getTitleTextView().setAlpha(0.0f);
        r0.actionBar.getSubtitleTextView().setAlpha(0.0f);
        r0.avatarContainer = new ChatAvatarContainer(context2, null, false);
        r0.avatarContainer.setEnabled(false);
        r0.avatarContainer.setTitleColors(Theme.getColor(Theme.key_player_actionBarTitle), Theme.getColor(Theme.key_player_actionBarSubtitle));
        if (messageObject != null) {
            long did = messageObject.getDialogId();
            int lower_id = (int) did;
            int high_id = (int) (did >> 32);
            User user;
            if (lower_id == 0) {
                EncryptedChat encryptedChat = MessagesController.getInstance(r0.currentAccount).getEncryptedChat(Integer.valueOf(high_id));
                if (encryptedChat != null) {
                    user = MessagesController.getInstance(r0.currentAccount).getUser(Integer.valueOf(encryptedChat.user_id));
                    if (user != null) {
                        r0.avatarContainer.setTitle(ContactsController.formatName(user.first_name, user.last_name));
                        r0.avatarContainer.setUserAvatar(user);
                    }
                }
            } else if (lower_id > 0) {
                user = MessagesController.getInstance(r0.currentAccount).getUser(Integer.valueOf(lower_id));
                if (user != null) {
                    r0.avatarContainer.setTitle(ContactsController.formatName(user.first_name, user.last_name));
                    r0.avatarContainer.setUserAvatar(user);
                }
            } else {
                Chat chat = MessagesController.getInstance(r0.currentAccount).getChat(Integer.valueOf(-lower_id));
                if (chat != null) {
                    r0.avatarContainer.setTitle(chat.title);
                    r0.avatarContainer.setChatAvatar(chat);
                }
            }
        }
        r0.avatarContainer.setSubtitle(LocaleController.getString("AudioTitle", R.string.AudioTitle));
        r0.actionBar.addView(r0.avatarContainer, 0, LayoutHelper.createFrame(-2, -1.0f, 51, 56.0f, 0.0f, 40.0f, 0.0f));
        ActionBarMenu menu = r0.actionBar.createMenu();
        r0.menuItem = menu.addItem(0, (int) R.drawable.ic_ab_other);
        r0.menuItem.addSubItem(1, LocaleController.getString("Forward", R.string.Forward));
        r0.menuItem.addSubItem(2, LocaleController.getString("ShareFile", R.string.ShareFile));
        r0.menuItem.addSubItem(4, LocaleController.getString("ShowInChat", R.string.ShowInChat));
        r0.menuItem.setTranslationX((float) AndroidUtilities.dp(48.0f));
        r0.menuItem.setAlpha(0.0f);
        r0.searchItem = menu.addItem(0, (int) R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new C20372());
        EditTextBoldCursor editText = r0.searchItem.getSearchField();
        editText.setHint(LocaleController.getString("Search", R.string.Search));
        editText.setTextColor(Theme.getColor(Theme.key_player_actionBarTitle));
        editText.setHintTextColor(Theme.getColor(Theme.key_player_time));
        editText.setCursorColor(Theme.getColor(Theme.key_player_actionBarTitle));
        if (!AndroidUtilities.isTablet()) {
            r0.actionBar.showActionModeTop();
            r0.actionBar.setActionModeTopColor(Theme.getColor(Theme.key_player_actionBarTop));
        }
        r0.actionBar.setActionBarMenuOnItemClick(new C20383());
        r0.shadow = new View(context2);
        r0.shadow.setAlpha(0.0f);
        r0.shadow.setBackgroundResource(R.drawable.header_shadow);
        r0.shadow2 = new View(context2);
        r0.shadow2.setAlpha(0.0f);
        r0.shadow2.setBackgroundResource(R.drawable.header_shadow);
        r0.playerLayout = new FrameLayout(context2);
        r0.playerLayout.setBackgroundColor(Theme.getColor(Theme.key_player_background));
        r0.placeholderImageView = new BackupImageView(context2) {
            private RectF rect = new RectF();

            protected void onDraw(Canvas canvas) {
                if (AudioPlayerAlert.this.hasNoCover) {
                    this.rect.set(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight());
                    canvas.drawRoundRect(this.rect, (float) getRoundRadius(), (float) getRoundRadius(), AudioPlayerAlert.this.paint);
                    int s = (int) (((float) AndroidUtilities.dp(63.0f)) * Math.max(((AudioPlayerAlert.this.thumbMaxScale / getScaleX()) / 3.0f) / AudioPlayerAlert.this.thumbMaxScale, 1.0f / AudioPlayerAlert.this.thumbMaxScale));
                    int x = (int) (this.rect.centerX() - ((float) (s / 2)));
                    int y = (int) (this.rect.centerY() - ((float) (s / 2)));
                    AudioPlayerAlert.this.noCoverDrawable.setBounds(x, y, x + s, y + s);
                    AudioPlayerAlert.this.noCoverDrawable.draw(canvas);
                    return;
                }
                super.onDraw(canvas);
            }
        };
        r0.placeholderImageView.setRoundRadius(AndroidUtilities.dp(20.0f));
        r0.placeholderImageView.setPivotX(0.0f);
        r0.placeholderImageView.setPivotY(0.0f);
        r0.placeholderImageView.setOnClickListener(new C10865());
        r0.titleTextView = new TextView(context2);
        r0.titleTextView.setTextColor(Theme.getColor(Theme.key_player_actionBarTitle));
        r0.titleTextView.setTextSize(1, 15.0f);
        r0.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        r0.titleTextView.setEllipsize(TruncateAt.END);
        r0.titleTextView.setSingleLine(true);
        r0.playerLayout.addView(r0.titleTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 72.0f, 18.0f, 60.0f, 0.0f));
        r0.authorTextView = new TextView(context2);
        r0.authorTextView.setTextColor(Theme.getColor(Theme.key_player_time));
        r0.authorTextView.setTextSize(1, 14.0f);
        r0.authorTextView.setEllipsize(TruncateAt.END);
        r0.authorTextView.setSingleLine(true);
        r0.playerLayout.addView(r0.authorTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 72.0f, 40.0f, 60.0f, 0.0f));
        r0.optionsButton = new ActionBarMenuItem(context2, null, 0, Theme.getColor(Theme.key_player_actionBarItems));
        r0.optionsButton.setLongClickEnabled(false);
        r0.optionsButton.setIcon((int) R.drawable.ic_ab_other);
        r0.optionsButton.setAdditionalOffset(-AndroidUtilities.dp(120.0f));
        r0.playerLayout.addView(r0.optionsButton, LayoutHelper.createFrame(40, 40.0f, 53, 0.0f, 19.0f, 10.0f, 0.0f));
        r0.optionsButton.addSubItem(1, LocaleController.getString("Forward", R.string.Forward));
        r0.optionsButton.addSubItem(2, LocaleController.getString("ShareFile", R.string.ShareFile));
        r0.optionsButton.addSubItem(4, LocaleController.getString("ShowInChat", R.string.ShowInChat));
        r0.optionsButton.setOnClickListener(new C10876());
        r0.optionsButton.setDelegate(new C20407());
        r0.seekBarView = new SeekBarView(context2);
        r0.seekBarView.setDelegate(new C20418());
        r0.playerLayout.addView(r0.seekBarView, LayoutHelper.createFrame(-1, 30.0f, 51, 8.0f, 62.0f, 8.0f, 0.0f));
        r0.progressView = new LineProgressView(context2);
        r0.progressView.setVisibility(4);
        r0.progressView.setBackgroundColor(Theme.getColor(Theme.key_player_progressBackground));
        r0.progressView.setProgressColor(Theme.getColor(Theme.key_player_progress));
        r0.playerLayout.addView(r0.progressView, LayoutHelper.createFrame(-1, 2.0f, 51, 20.0f, 78.0f, 20.0f, 0.0f));
        r0.timeTextView = new SimpleTextView(context2);
        r0.timeTextView.setTextSize(12);
        r0.timeTextView.setTextColor(Theme.getColor(Theme.key_player_time));
        r0.playerLayout.addView(r0.timeTextView, LayoutHelper.createFrame(100, -2.0f, 51, 20.0f, 92.0f, 0.0f, 0.0f));
        r0.durationTextView = new TextView(context2);
        r0.durationTextView.setTextSize(1, 12.0f);
        r0.durationTextView.setTextColor(Theme.getColor(Theme.key_player_time));
        r0.durationTextView.setGravity(17);
        r0.playerLayout.addView(r0.durationTextView, LayoutHelper.createFrame(-2, -2.0f, 53, 0.0f, 90.0f, 20.0f, 0.0f));
        FrameLayout bottomView = new FrameLayout(context2) {
            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                int dist = ((right - left) - AndroidUtilities.dp(248.0f)) / 4;
                for (int a = 0; a < 5; a++) {
                    int l = AndroidUtilities.dp((float) ((48 * a) + 4)) + (dist * a);
                    int t = AndroidUtilities.dp(NUM);
                    AudioPlayerAlert.this.buttons[a].layout(l, t, AudioPlayerAlert.this.buttons[a].getMeasuredWidth() + l, AudioPlayerAlert.this.buttons[a].getMeasuredHeight() + t);
                }
            }
        };
        r0.playerLayout.addView(bottomView, LayoutHelper.createFrame(-1, 66.0f, 51, 0.0f, 106.0f, 0.0f, 0.0f));
        View[] viewArr = r0.buttons;
        ActionBarMenuItem actionBarMenuItem = new ActionBarMenuItem(context2, null, 0, 0);
        r0.shuffleButton = actionBarMenuItem;
        viewArr[0] = actionBarMenuItem;
        r0.shuffleButton.setLongClickEnabled(false);
        r0.shuffleButton.setAdditionalOffset(-AndroidUtilities.dp(10.0f));
        bottomView.addView(r0.shuffleButton, LayoutHelper.createFrame(48, 48, 51));
        r0.shuffleButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                AudioPlayerAlert.this.shuffleButton.toggleSubMenu();
            }
        });
        TextView textView = r0.shuffleButton.addSubItem(1, LocaleController.getString("ReverseOrder", R.string.ReverseOrder));
        textView.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(16.0f), 0);
        r0.playOrderButtons[0] = context.getResources().getDrawable(R.drawable.music_reverse).mutate();
        textView.setCompoundDrawablePadding(AndroidUtilities.dp(8.0f));
        textView.setCompoundDrawablesWithIntrinsicBounds(r0.playOrderButtons[0], null, null, null);
        textView = r0.shuffleButton.addSubItem(2, LocaleController.getString("Shuffle", R.string.Shuffle));
        textView.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(16.0f), 0);
        r0.playOrderButtons[1] = context.getResources().getDrawable(R.drawable.pl_shuffle).mutate();
        textView.setCompoundDrawablePadding(AndroidUtilities.dp(8.0f));
        textView.setCompoundDrawablesWithIntrinsicBounds(r0.playOrderButtons[1], null, null, null);
        r0.shuffleButton.setDelegate(new ActionBarMenuItemDelegate() {
            public void onItemClick(int id) {
                MediaController.getInstance().toggleShuffleMusic(id);
                AudioPlayerAlert.this.updateShuffleButton();
                AudioPlayerAlert.this.listAdapter.notifyDataSetChanged();
            }
        });
        View[] viewArr2 = r0.buttons;
        ImageView imageView = new ImageView(context2);
        ImageView prevButton = imageView;
        viewArr2[1] = imageView;
        prevButton.setScaleType(ScaleType.CENTER);
        prevButton.setImageDrawable(Theme.createSimpleSelectorDrawable(context2, R.drawable.pl_previous, Theme.getColor(Theme.key_player_button), Theme.getColor(Theme.key_player_buttonActive)));
        bottomView.addView(prevButton, LayoutHelper.createFrame(48, 48, 51));
        prevButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                MediaController.getInstance().playPreviousMessage();
            }
        });
        View[] viewArr3 = r0.buttons;
        ImageView imageView2 = new ImageView(context2);
        r0.playButton = imageView2;
        viewArr3[2] = imageView2;
        r0.playButton.setScaleType(ScaleType.CENTER);
        r0.playButton.setImageDrawable(Theme.createSimpleSelectorDrawable(context2, R.drawable.pl_play, Theme.getColor(Theme.key_player_button), Theme.getColor(Theme.key_player_buttonActive)));
        bottomView.addView(r0.playButton, LayoutHelper.createFrame(48, 48, 51));
        r0.playButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (!MediaController.getInstance().isDownloadingCurrentMessage()) {
                    if (MediaController.getInstance().isMessagePaused()) {
                        MediaController.getInstance().playMessage(MediaController.getInstance().getPlayingMessageObject());
                    } else {
                        MediaController.getInstance().pauseMessage(MediaController.getInstance().getPlayingMessageObject());
                    }
                }
            }
        });
        viewArr3 = r0.buttons;
        imageView2 = new ImageView(context2);
        imageView = imageView2;
        viewArr3[3] = imageView2;
        imageView.setScaleType(ScaleType.CENTER);
        imageView.setImageDrawable(Theme.createSimpleSelectorDrawable(context2, R.drawable.pl_next, Theme.getColor(Theme.key_player_button), Theme.getColor(Theme.key_player_buttonActive)));
        bottomView.addView(imageView, LayoutHelper.createFrame(48, 48, 51));
        imageView.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                MediaController.getInstance().playNextMessage();
            }
        });
        viewArr3 = r0.buttons;
        ImageView imageView3 = new ImageView(context2);
        r0.repeatButton = imageView3;
        viewArr3[4] = imageView3;
        r0.repeatButton.setScaleType(ScaleType.CENTER);
        r0.repeatButton.setPadding(0, 0, AndroidUtilities.dp(8.0f), 0);
        bottomView.addView(r0.repeatButton, LayoutHelper.createFrame(50, 48, 51));
        r0.repeatButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                SharedConfig.toggleRepeatMode();
                AudioPlayerAlert.this.updateRepeatButton();
            }
        });
        r0.listView = new RecyclerListView(context2) {
            boolean ignoreLayout;

            protected void onLayout(boolean changed, int l, int t, int r, int b) {
                super.onLayout(changed, l, t, r, b);
                if (AudioPlayerAlert.this.searchOpenPosition != -1 && !AudioPlayerAlert.this.actionBar.isSearchFieldVisible()) {
                    r6.ignoreLayout = true;
                    AudioPlayerAlert.this.layoutManager.scrollToPositionWithOffset(AudioPlayerAlert.this.searchOpenPosition, AudioPlayerAlert.this.searchOpenOffset);
                    super.onLayout(false, l, t, r, b);
                    r6.ignoreLayout = false;
                    AudioPlayerAlert.this.searchOpenPosition = -1;
                } else if (AudioPlayerAlert.this.scrollToSong) {
                    AudioPlayerAlert.this.scrollToSong = false;
                    boolean found = false;
                    MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
                    if (playingMessageObject != null) {
                        int idx;
                        int count = AudioPlayerAlert.this.listView.getChildCount();
                        for (int a = 0; a < count; a++) {
                            View child = AudioPlayerAlert.this.listView.getChildAt(a);
                            if ((child instanceof AudioPlayerCell) && ((AudioPlayerCell) child).getMessageObject() == playingMessageObject) {
                                if (child.getBottom() <= getMeasuredHeight()) {
                                    found = true;
                                }
                                if (!found) {
                                    idx = AudioPlayerAlert.this.playlist.indexOf(playingMessageObject);
                                    if (idx >= 0) {
                                        r6.ignoreLayout = true;
                                        if (SharedConfig.playOrderReversed) {
                                            AudioPlayerAlert.this.layoutManager.scrollToPosition(AudioPlayerAlert.this.playlist.size() - idx);
                                        } else {
                                            AudioPlayerAlert.this.layoutManager.scrollToPosition(idx);
                                        }
                                        super.onLayout(false, l, t, r, b);
                                        r6.ignoreLayout = false;
                                    }
                                }
                            }
                        }
                        if (!found) {
                            idx = AudioPlayerAlert.this.playlist.indexOf(playingMessageObject);
                            if (idx >= 0) {
                                r6.ignoreLayout = true;
                                if (SharedConfig.playOrderReversed) {
                                    AudioPlayerAlert.this.layoutManager.scrollToPosition(AudioPlayerAlert.this.playlist.size() - idx);
                                } else {
                                    AudioPlayerAlert.this.layoutManager.scrollToPosition(idx);
                                }
                                super.onLayout(false, l, t, r, b);
                                r6.ignoreLayout = false;
                            }
                        }
                    }
                }
            }

            public void requestLayout() {
                if (!this.ignoreLayout) {
                    super.requestLayout();
                }
            }

            protected boolean allowSelectChildAtPosition(float x, float y) {
                float p = AudioPlayerAlert.this.playerLayout.getY() + ((float) AudioPlayerAlert.this.playerLayout.getMeasuredHeight());
                if (AudioPlayerAlert.this.playerLayout != null) {
                    if (y <= AudioPlayerAlert.this.playerLayout.getY() + ((float) AudioPlayerAlert.this.playerLayout.getMeasuredHeight())) {
                        return false;
                    }
                }
                return true;
            }

            public boolean drawChild(Canvas canvas, View child, long drawingTime) {
                canvas.save();
                canvas.clipRect(0, (AudioPlayerAlert.this.actionBar != null ? AudioPlayerAlert.this.actionBar.getMeasuredHeight() : 0) + AndroidUtilities.dp(50.0f), getMeasuredWidth(), getMeasuredHeight());
                boolean result = super.drawChild(canvas, child, drawingTime);
                canvas.restore();
                return result;
            }
        };
        r0.listView.setPadding(0, 0, 0, AndroidUtilities.dp(8.0f));
        r0.listView.setClipToPadding(false);
        RecyclerListView recyclerListView = r0.listView;
        LayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), 1, false);
        r0.layoutManager = linearLayoutManager;
        recyclerListView.setLayoutManager(linearLayoutManager);
        r0.listView.setHorizontalScrollBarEnabled(false);
        r0.listView.setVerticalScrollBarEnabled(false);
        r0.containerView.addView(r0.listView, LayoutHelper.createFrame(-1, -1, 51));
        recyclerListView = r0.listView;
        Adapter listAdapter = new ListAdapter(context2);
        r0.listAdapter = listAdapter;
        recyclerListView.setAdapter(listAdapter);
        r0.listView.setGlowColor(Theme.getColor(Theme.key_dialogScrollGlow));
        r0.listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(View view, int position) {
                if (view instanceof AudioPlayerCell) {
                    ((AudioPlayerCell) view).didPressedButton();
                }
            }
        });
        r0.listView.setOnScrollListener(new OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 1 && AudioPlayerAlert.this.searching && AudioPlayerAlert.this.searchWas) {
                    AndroidUtilities.hideKeyboard(AudioPlayerAlert.this.getCurrentFocus());
                }
            }

            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                AudioPlayerAlert.this.updateLayout();
            }
        });
        r0.playlist = MediaController.getInstance().getPlaylist();
        r0.listAdapter.notifyDataSetChanged();
        r0.containerView.addView(r0.playerLayout, LayoutHelper.createFrame(-1, 178.0f));
        r0.containerView.addView(r0.shadow2, LayoutHelper.createFrame(-1, 3.0f));
        r0.containerView.addView(r0.placeholderImageView, LayoutHelper.createFrame(40, 40.0f, 51, 17.0f, 19.0f, 0.0f, 0.0f));
        r0.containerView.addView(r0.shadow, LayoutHelper.createFrame(-1, 3.0f));
        r0.containerView.addView(r0.actionBar);
        updateTitle(false);
        updateRepeatButton();
        updateShuffleButton();
    }

    @Keep
    public void setFullAnimationProgress(float value) {
        this.fullAnimationProgress = value;
        this.placeholderImageView.setRoundRadius(AndroidUtilities.dp(20.0f * (1.0f - this.fullAnimationProgress)));
        float scale = (this.thumbMaxScale * this.fullAnimationProgress) + 1.0f;
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
        AudioPlayerAlert audioPlayerAlert = this;
        int i = id;
        final MessageObject messageObject = MediaController.getInstance().getPlayingMessageObject();
        if (messageObject != null) {
            if (audioPlayerAlert.parentActivity != null) {
                if (i == 1) {
                    if (UserConfig.selectedAccount != audioPlayerAlert.currentAccount) {
                        audioPlayerAlert.parentActivity.switchToAccount(audioPlayerAlert.currentAccount, true);
                    }
                    Bundle args = new Bundle();
                    args.putBoolean("onlySelect", true);
                    args.putInt("dialogsType", 3);
                    DialogsActivity fragment = new DialogsActivity(args);
                    final ArrayList<MessageObject> fmessages = new ArrayList();
                    fmessages.add(messageObject);
                    fragment.setDelegate(new DialogsActivityDelegate() {
                        public void didSelectDialogs(DialogsActivity fragment, ArrayList<Long> dids, CharSequence message, boolean param) {
                            int lower_part;
                            AnonymousClass19 anonymousClass19 = this;
                            ArrayList arrayList = dids;
                            int a = 0;
                            if (dids.size() <= 1 && ((Long) arrayList.get(0)).longValue() != ((long) UserConfig.getInstance(AudioPlayerAlert.this.currentAccount).getClientUserId())) {
                                if (message == null) {
                                    long did = ((Long) arrayList.get(0)).longValue();
                                    lower_part = (int) did;
                                    int high_part = (int) (did >> 32);
                                    Bundle args = new Bundle();
                                    args.putBoolean("scrollToTopOnResume", true);
                                    if (lower_part == 0) {
                                        args.putInt("enc_id", high_part);
                                    } else if (lower_part > 0) {
                                        args.putInt("user_id", lower_part);
                                    } else if (lower_part < 0) {
                                        args.putInt("chat_id", -lower_part);
                                    }
                                    NotificationCenter.getInstance(AudioPlayerAlert.this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                                    ChatActivity chatActivity = new ChatActivity(args);
                                    if (AudioPlayerAlert.this.parentActivity.presentFragment(chatActivity, true, false)) {
                                        chatActivity.showReplyPanel(true, null, fmessages, null, false);
                                        return;
                                    }
                                    fragment.finishFragment();
                                    return;
                                }
                            }
                            while (true) {
                                lower_part = a;
                                if (lower_part < dids.size()) {
                                    long did2;
                                    long did3 = ((Long) arrayList.get(lower_part)).longValue();
                                    if (message != null) {
                                        did2 = did3;
                                        SendMessagesHelper.getInstance(AudioPlayerAlert.this.currentAccount).sendMessage(message.toString(), did3, null, null, true, null, null, null);
                                    } else {
                                        did2 = did3;
                                    }
                                    SendMessagesHelper.getInstance(AudioPlayerAlert.this.currentAccount).sendMessage(fmessages, did2);
                                    a = lower_part + 1;
                                    ArrayList<Long> arrayList2 = dids;
                                } else {
                                    fragment.finishFragment();
                                    return;
                                }
                            }
                        }
                    });
                    audioPlayerAlert.parentActivity.presentFragment(fragment);
                    dismiss();
                } else if (i == 2) {
                    File f = null;
                    try {
                        if (!TextUtils.isEmpty(messageObject.messageOwner.attachPath)) {
                            f = new File(messageObject.messageOwner.attachPath);
                            if (!f.exists()) {
                                f = null;
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
                                    intent.putExtra("android.intent.extra.STREAM", FileProvider.getUriForFile(ApplicationLoader.applicationContext, "org.telegram.messenger.beta.provider", f));
                                    intent.setFlags(1);
                                } catch (Exception e) {
                                    Exception ignore = e;
                                    intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(f));
                                }
                            } else {
                                intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(f));
                            }
                            audioPlayerAlert.parentActivity.startActivityForResult(Intent.createChooser(intent, LocaleController.getString("ShareFile", R.string.ShareFile)), 500);
                        } else {
                            Builder builder = new Builder(audioPlayerAlert.parentActivity);
                            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                            builder.setMessage(LocaleController.getString("PleaseDownload", R.string.PleaseDownload));
                            builder.show();
                        }
                    } catch (Throwable e2) {
                        FileLog.m3e(e2);
                    }
                } else if (i == 3) {
                    Builder builder2 = new Builder(audioPlayerAlert.parentActivity);
                    builder2.setTitle(LocaleController.getString("AppName", R.string.AppName));
                    final boolean[] deleteForAll = new boolean[1];
                    int lower_id = (int) messageObject.getDialogId();
                    if (lower_id != 0) {
                        User currentUser;
                        Chat currentChat;
                        int i2;
                        if (lower_id > 0) {
                            currentUser = MessagesController.getInstance(audioPlayerAlert.currentAccount).getUser(Integer.valueOf(lower_id));
                            currentChat = null;
                        } else {
                            currentUser = null;
                            currentChat = MessagesController.getInstance(audioPlayerAlert.currentAccount).getChat(Integer.valueOf(-lower_id));
                        }
                        if (currentUser == null) {
                            if (!ChatObject.isChannel(currentChat)) {
                            }
                            i2 = lower_id;
                            builder2.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    AudioPlayerAlert.this.dismiss();
                                    ArrayList<Integer> arr = new ArrayList();
                                    arr.add(Integer.valueOf(messageObject.getId()));
                                    ArrayList<Long> random_ids = null;
                                    EncryptedChat encryptedChat = null;
                                    if (((int) messageObject.getDialogId()) == 0 && messageObject.messageOwner.random_id != 0) {
                                        random_ids = new ArrayList();
                                        random_ids.add(Long.valueOf(messageObject.messageOwner.random_id));
                                        encryptedChat = MessagesController.getInstance(AudioPlayerAlert.this.currentAccount).getEncryptedChat(Integer.valueOf((int) (messageObject.getDialogId() >> 32)));
                                    }
                                    ArrayList<Long> random_ids2 = random_ids;
                                    EncryptedChat encryptedChat2 = encryptedChat;
                                    MessagesController.getInstance(AudioPlayerAlert.this.currentAccount).deleteMessages(arr, random_ids2, encryptedChat2, messageObject.messageOwner.to_id.channel_id, deleteForAll[0]);
                                }
                            });
                            builder2.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                            builder2.show();
                        }
                        int currentDate = ConnectionsManager.getInstance(audioPlayerAlert.currentAccount).getCurrentTime();
                        if (!((currentUser == null || currentUser.id == UserConfig.getInstance(audioPlayerAlert.currentAccount).getClientUserId()) && currentChat == null)) {
                            if (messageObject.messageOwner.action == null || (messageObject.messageOwner.action instanceof TL_messageActionEmpty)) {
                                if (messageObject.isOut() && currentDate - messageObject.messageOwner.date <= 172800) {
                                    FrameLayout frameLayout = new FrameLayout(audioPlayerAlert.parentActivity);
                                    CheckBoxCell cell = new CheckBoxCell(audioPlayerAlert.parentActivity, 1);
                                    cell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                                    if (currentChat != null) {
                                        cell.setText(LocaleController.getString("DeleteForAll", R.string.DeleteForAll), TtmlNode.ANONYMOUS_REGION_ID, false, false);
                                        i2 = lower_id;
                                    } else {
                                        Object[] objArr = new Object[1];
                                        objArr[0] = UserObject.getFirstName(currentUser);
                                        cell.setText(LocaleController.formatString("DeleteForUser", R.string.DeleteForUser, objArr), TtmlNode.ANONYMOUS_REGION_ID, false, false);
                                    }
                                    cell.setPadding(LocaleController.isRTL ? AndroidUtilities.dp(16.0f) : AndroidUtilities.dp(8.0f), 0, LocaleController.isRTL ? AndroidUtilities.dp(8.0f) : AndroidUtilities.dp(16.0f), 0);
                                    frameLayout.addView(cell, LayoutHelper.createFrame(-1, 48.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
                                    cell.setOnClickListener(new OnClickListener() {
                                        public void onClick(View v) {
                                            CheckBoxCell cell = (CheckBoxCell) v;
                                            deleteForAll[0] = deleteForAll[0] ^ true;
                                            cell.setChecked(deleteForAll[0], true);
                                        }
                                    });
                                    builder2.setView(frameLayout);
                                    builder2.setPositiveButton(LocaleController.getString("OK", R.string.OK), /* anonymous class already generated */);
                                    builder2.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                                    builder2.show();
                                }
                            }
                            i2 = lower_id;
                            builder2.setPositiveButton(LocaleController.getString("OK", R.string.OK), /* anonymous class already generated */);
                            builder2.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                            builder2.show();
                        }
                    }
                    builder2.setPositiveButton(LocaleController.getString("OK", R.string.OK), /* anonymous class already generated */);
                    builder2.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                    builder2.show();
                } else if (i == 4) {
                    if (UserConfig.selectedAccount != audioPlayerAlert.currentAccount) {
                        audioPlayerAlert.parentActivity.switchToAccount(audioPlayerAlert.currentAccount, true);
                    }
                    Bundle args2 = new Bundle();
                    long did = messageObject.getDialogId();
                    int lower_part = (int) did;
                    int high_id = (int) (did >> 32);
                    if (lower_part == 0) {
                        args2.putInt("enc_id", high_id);
                    } else if (high_id == 1) {
                        args2.putInt("chat_id", lower_part);
                    } else if (lower_part > 0) {
                        args2.putInt("user_id", lower_part);
                    } else if (lower_part < 0) {
                        Chat chat = MessagesController.getInstance(audioPlayerAlert.currentAccount).getChat(Integer.valueOf(-lower_part));
                        if (!(chat == null || chat.migrated_to == null)) {
                            args2.putInt("migrated_to", lower_part);
                            lower_part = -chat.migrated_to.channel_id;
                        }
                        args2.putInt("chat_id", -lower_part);
                    }
                    args2.putInt("message_id", messageObject.getId());
                    NotificationCenter.getInstance(audioPlayerAlert.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                    audioPlayerAlert.parentActivity.presentFragment(new ChatActivity(args2), false, false);
                    dismiss();
                }
            }
        }
    }

    private int getCurrentTop() {
        if (this.listView.getChildCount() != 0) {
            int i = 0;
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
        return C0542C.PRIORITY_DOWNLOAD;
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        int count;
        if (!(id == NotificationCenter.messagePlayingDidStarted || id == NotificationCenter.messagePlayingPlayStateChanged)) {
            if (id != NotificationCenter.messagePlayingDidReset) {
                if (id == NotificationCenter.messagePlayingProgressDidChanged) {
                    MessageObject messageObject = MediaController.getInstance().getPlayingMessageObject();
                    if (messageObject != null && messageObject.isMusic()) {
                        updateProgress(messageObject);
                    }
                } else if (id == NotificationCenter.musicDidLoaded) {
                    this.playlist = MediaController.getInstance().getPlaylist();
                    this.listAdapter.notifyDataSetChanged();
                }
            }
        }
        boolean z = true;
        if (id != NotificationCenter.messagePlayingDidReset || !((Boolean) args[1]).booleanValue()) {
            z = false;
        }
        updateTitle(z);
        if (id != NotificationCenter.messagePlayingDidReset) {
            if (id != NotificationCenter.messagePlayingPlayStateChanged) {
                if (id == NotificationCenter.messagePlayingDidStarted && ((MessageObject) args[0]).eventId == 0) {
                    count = this.listView.getChildCount();
                    for (int a = 0; a < count; a++) {
                        View view = this.listView.getChildAt(a);
                        if (view instanceof AudioPlayerCell) {
                            AudioPlayerCell cell = (AudioPlayerCell) view;
                            MessageObject messageObject1 = cell.getMessageObject();
                            if (messageObject1 != null && (messageObject1.isVoice() || messageObject1.isMusic())) {
                                cell.updateButtonState(false);
                            }
                        }
                    }
                }
                return;
            }
        }
        int count2 = this.listView.getChildCount();
        for (count = 0; count < count2; count++) {
            View view2 = this.listView.getChildAt(count);
            if (view2 instanceof AudioPlayerCell) {
                AudioPlayerCell cell2 = (AudioPlayerCell) view2;
                MessageObject messageObject2 = cell2.getMessageObject();
                if (messageObject2 != null && (messageObject2.isVoice() || messageObject2.isMusic())) {
                    cell2.updateButtonState(false);
                }
            }
        }
    }

    protected boolean canDismissWithSwipe() {
        return false;
    }

    private void updateLayout() {
        if (this.listView.getChildCount() > 0) {
            View child = this.listView.getChildAt(0);
            Holder holder = (Holder) this.listView.findContainingViewHolder(child);
            int top = child.getTop();
            int newOffset = (top <= 0 || holder == null || holder.getAdapterPosition() != 0) ? 0 : top;
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
        super.dismiss();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidReset);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidStarted);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.musicDidLoaded);
        DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
    }

    public void onBackPressed() {
        if (this.actionBar == null || !this.actionBar.isSearchFieldVisible()) {
            super.onBackPressed();
        } else {
            this.actionBar.closeSearchField();
        }
    }

    public void onFailedDownload(String fileName) {
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
            drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_player_buttonActive), Mode.MULTIPLY));
            this.shuffleButton.setIcon(drawable);
        } else {
            drawable = getContext().getResources().getDrawable(R.drawable.music_reverse).mutate();
            if (SharedConfig.playOrderReversed) {
                drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_player_buttonActive), Mode.MULTIPLY));
            } else {
                drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_player_button), Mode.MULTIPLY));
            }
            this.shuffleButton.setIcon(drawable);
        }
        this.playOrderButtons[0].setColorFilter(new PorterDuffColorFilter(Theme.getColor(SharedConfig.playOrderReversed ? Theme.key_player_buttonActive : Theme.key_player_button), Mode.MULTIPLY));
        this.playOrderButtons[1].setColorFilter(new PorterDuffColorFilter(Theme.getColor(SharedConfig.shuffleMusic ? Theme.key_player_buttonActive : Theme.key_player_button), Mode.MULTIPLY));
    }

    private void updateRepeatButton() {
        int mode = SharedConfig.repeatMode;
        if (mode == 0) {
            this.repeatButton.setImageResource(R.drawable.pl_repeat);
            this.repeatButton.setTag(Theme.key_player_button);
            this.repeatButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_player_button), Mode.MULTIPLY));
        } else if (mode == 1) {
            this.repeatButton.setImageResource(R.drawable.pl_repeat);
            this.repeatButton.setTag(Theme.key_player_buttonActive);
            this.repeatButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_player_buttonActive), Mode.MULTIPLY));
        } else if (mode == 2) {
            this.repeatButton.setImageResource(R.drawable.pl_repeat1);
            this.repeatButton.setTag(Theme.key_player_buttonActive);
            this.repeatButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_player_buttonActive), Mode.MULTIPLY));
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
        boolean canStream = SharedConfig.streamMedia && ((int) messageObject.getDialogId()) != 0 && messageObject.isMusic();
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
            if (messageObject.eventId != 0) {
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
                this.playButton.setImageDrawable(Theme.createSimpleSelectorDrawable(this.playButton.getContext(), R.drawable.pl_play, Theme.getColor(Theme.key_player_button), Theme.getColor(Theme.key_player_buttonActive)));
            } else {
                this.playButton.setImageDrawable(Theme.createSimpleSelectorDrawable(this.playButton.getContext(), R.drawable.pl_pause, Theme.getColor(Theme.key_player_button), Theme.getColor(Theme.key_player_buttonActive)));
            }
            String title = messageObject.getMusicTitle();
            String author = messageObject.getMusicAuthor();
            this.titleTextView.setText(title);
            this.authorTextView.setText(author);
            this.actionBar.setTitle(title);
            this.actionBar.setSubtitle(author);
            AudioInfo audioInfo = MediaController.getInstance().getAudioInfo();
            if (audioInfo == null || audioInfo.getCover() == null) {
                this.hasNoCover = true;
                this.placeholderImageView.invalidate();
                this.placeholderImageView.setImageDrawable(null);
            } else {
                this.hasNoCover = false;
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
