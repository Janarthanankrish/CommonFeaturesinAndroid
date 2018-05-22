package com.commonfeaturelib.SelectImage.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.commonfeaturelib.Common.RuntimePermissionsActivity;
import com.commonfeaturelib.R;
import com.commonfeaturelib.SelectImage.PhotoSelectPojo;
import com.commonfeaturelib.SelectImage.adapters.InstantImageAdapter;
import com.commonfeaturelib.SelectImage.adapters.MainImageAdapter;
import com.commonfeaturelib.SelectImage.interfaces.FastScrollStateChangeListener;
import com.commonfeaturelib.SelectImage.interfaces.OnSelectionListner;
import com.commonfeaturelib.SelectImage.interfaces.WorkFinish;
import com.commonfeaturelib.SelectImage.utility.Constants;
import com.commonfeaturelib.SelectImage.utility.HeaderItemDecoration;
import com.commonfeaturelib.SelectImage.utility.ImageFetcher;
import com.commonfeaturelib.SelectImage.utility.PermUtil;
import com.commonfeaturelib.SelectImage.utility.Utility;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.wonderkiln.camerakit.CameraKit;
import com.wonderkiln.camerakit.CameraKitEventCallback;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraView;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by janarthananr on 16/5/18.
 */

public class SelectMultipleImages extends RuntimePermissionsActivity implements View.OnTouchListener {

    private static final int sBubbleAnimDuration = 1000;
    private static final int sScrollbarHideDelay = 1000;
    private static final String SELECTION = "selection";
    private static final int sTrackSnapRange = 5;
    public static String IMAGE_RESULTS = "image_results";
    public static float TOPBAR_HEIGHT;
    int BottomBarHeight = 0;
    int colorPrimaryDark;
    private Handler handler = new Handler();
    private FastScrollStateChangeListener mFastScrollStateChangeListener;
    private CameraView mCamera;
    private RecyclerView recyclerView, instantRecyclerView;
    private BottomSheetBehavior mBottomSheetBehavior;
    private InstantImageAdapter initaliseadapter;
    private GridLayoutManager mLayoutManager;
    private View status_bar_bg, mScrollbar, topbar, mainFrameLayout, bottomButtons, sendButton;
    private TextView mBubbleView, selection_ok, img_count;
    private ImageView mHandleView, clickme, selection_back, selection_check;
    private ViewPropertyAnimator mScrollbarAnimator;
    private ViewPropertyAnimator mBubbleAnimator;
    private Set<PhotoSelectPojo> selectionList = new HashSet<PhotoSelectPojo>();
    private Runnable mScrollbarHider = new Runnable() {
        @Override
        public void run() {
            hideScrollbar();
        }
    };
    private MainImageAdapter mainImageAdapter;
    private float mViewHeight;
    private boolean mHideScrollbar = true;
    private boolean LongSelection = false;
    private int SelectionCount = 1;
    /*int colorPrimary = ResourcesCompat.getColor(getResources(), R.color.colorPrimary, getTheme());
    int colorAccent = ResourcesCompat.getColor(getResources(), R.color.colorAccent, getTheme());*/
    private RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (!mHandleView.isSelected() && recyclerView.isEnabled()) {
                setViewPositions(getScrollProportion(recyclerView));
            }
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);

            if (recyclerView.isEnabled()) {
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        handler.removeCallbacks(mScrollbarHider);
                        Utility.cancelAnimation(mScrollbarAnimator);
                        if (!Utility.isViewVisible(mScrollbar) && (recyclerView.computeVerticalScrollRange() - mViewHeight > 0)) {
                            mScrollbarAnimator = Utility.showScrollbar(mScrollbar, SelectMultipleImages.this);
                        }
                        break;
                    case RecyclerView.SCROLL_STATE_IDLE:
                        if (mHideScrollbar && !mHandleView.isSelected()) {
                            handler.postDelayed(mScrollbarHider, sScrollbarHideDelay);
                        }
                        break;
                }
            }
        }
    };
    private TextView selection_count;
    private OnSelectionListner onSelectionListner = new OnSelectionListner() {
        @Override
        public void OnClick(PhotoSelectPojo photoSelectPojo, View view, int position) {
            Log.e("OnClick", "OnClick");
            if (LongSelection) {
                if (selectionList.contains(photoSelectPojo)) {
                    selectionList.remove(photoSelectPojo);
                    initaliseadapter.Select(false, position);
                    mainImageAdapter.Select(false, position);
                } else {
                    if (SelectionCount <= selectionList.size()) {
                        Toast.makeText(SelectMultipleImages.this, String.format(getResources().getString(R.string.selection_limiter_pix), selectionList.size()), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    photoSelectPojo.position = position;
                    selectionList.add(photoSelectPojo);
                    initaliseadapter.Select(true, position);
                    mainImageAdapter.Select(true, position);
                }
                if (selectionList.size() == 0) {
                    LongSelection = false;
                    selection_check.setVisibility(View.VISIBLE);
                    DrawableCompat.setTint(selection_back.getDrawable(), colorPrimaryDark);
                    topbar.setBackgroundColor(Color.parseColor("#ffffff"));
                    Animation anim = new ScaleAnimation(
                            1f, 0f, // Start and end values for the X axis scaling
                            1f, 0f, // Start and end values for the Y axis scaling
                            Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                            Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling
                    anim.setFillAfter(true); // Needed to keep the result of the animation
                    anim.setDuration(300);
                    anim.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            sendButton.setVisibility(View.GONE);
                            sendButton.clearAnimation();
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    sendButton.startAnimation(anim);

                }
                selection_count.setText("Selected " + selectionList.size());
                img_count.setText("" + selectionList.size());

                //DrawableCompat.setTint(selection_back.getDrawable(), Color.parseColor("#ffffff "));
            } else {
                photoSelectPojo.position = position;
                selectionList.add(photoSelectPojo);
                returnObjects();
                DrawableCompat.setTint(selection_back.getDrawable(), colorPrimaryDark);
                topbar.setBackgroundColor(Color.parseColor("#ffffff"));
            }
        }

        @Override
        public void OnLongClick(PhotoSelectPojo photoSelectPojo, View view, int position) {
            if (SelectionCount > 1) {
                Utility.vibe(SelectMultipleImages.this, 50);
                Log.e("OnLongClick", "OnLongClick");
                LongSelection = true;
                if (selectionList.size() == 0) {
                    if (mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                        sendButton.setVisibility(View.VISIBLE);
                        Animation anim = new ScaleAnimation(
                                0f, 1f, // Start and end values for the X axis scaling
                                0f, 1f, // Start and end values for the Y axis scaling
                                Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                                Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling
                        anim.setFillAfter(true); // Needed to keep the result of the animation
                        anim.setDuration(300);
                        sendButton.startAnimation(anim);
                    }
                    //sendButton.animate().scaleX(1).scaleY(1).setDuration(800).start();
                }
                if (selectionList.contains(photoSelectPojo)) {
                    selectionList.remove(photoSelectPojo);
                    initaliseadapter.Select(false, position);
                    mainImageAdapter.Select(false, position);
                } else {
                    photoSelectPojo.position = position;
                    selectionList.add(photoSelectPojo);
                    initaliseadapter.Select(true, position);
                    mainImageAdapter.Select(true, position);
                }
                selection_check.setVisibility(View.GONE);
                topbar.setBackgroundColor(colorPrimaryDark);
                selection_count.setText("Selected " + selectionList.size());
                img_count.setText("" + selectionList.size());
                DrawableCompat.setTint(selection_back.getDrawable(), Color.parseColor("#ffffff"));
            }

        }
    };
    private FrameLayout flash;
    private ImageView front;

    public static void start(Activity context, int requestCode, int selectionCount) {
        Intent i = new Intent(context, SelectMultipleImages.class);
        i.putExtra(SELECTION, selectionCount);
        context.startActivityForResult(i, requestCode);
    }



    private void hideScrollbar() {
        float transX = getResources().getDimensionPixelSize(R.dimen.fastscroll_scrollbar_padding_end);
        mScrollbarAnimator = mScrollbar.animate().translationX(transX).alpha(0f)
                .setDuration(Constants.sScrollbarAnimDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mScrollbar.setVisibility(View.GONE);
                        mScrollbarAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        super.onAnimationCancel(animation);
                        mScrollbar.setVisibility(View.GONE);
                        mScrollbarAnimator = null;
                    }
                });
    }

    public void returnObjects() {
        ArrayList<String> list = new ArrayList<>();
        for (PhotoSelectPojo i : selectionList) {
            list.add(i.url);
        }
        Intent resultIntent = new Intent();
        resultIntent.putStringArrayListExtra(IMAGE_RESULTS, list);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            SelectMultipleImages.super.requestAppPermissions(new
                            String[]{Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.RECORD_AUDIO}, R.string
                            .runtime_permissions_txt
                    , 123);
            return;
        } else {
            Utility.SetupStatusBarHiden(this);
            Utility.hideStatusBar(this);
            setContentView(R.layout.activity_main_lib);
            Fresco.initialize(this);
            initialize();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCamera != null)
            mCamera.start();
    }

    @Override
    protected void onPause() {
        if (mCamera != null) mCamera.stop();
        super.onPause();
    }

    private void initialize() {
        Utility.getScreensize(this);
        getSupportActionBar().hide();
        try {
            SelectionCount = getIntent().getIntExtra(SELECTION, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        colorPrimaryDark = ResourcesCompat.getColor(getResources(), R.color.colorPrimaryPix, getTheme());
        mCamera = findViewById(R.id.camera);
        mCamera.start();
        mCamera.setFocus(CameraKit.Constants.FOCUS_TAP_WITH_MARKER);
        mCamera.setZoom(CameraKit.Constants.ZOOM_PINCH);
        clickme = findViewById(R.id.clickme);
        flash = findViewById(R.id.flash);
        front = findViewById(R.id.front);
        topbar = findViewById(R.id.topbar);
        selection_count = findViewById(R.id.selection_count);
        selection_ok = findViewById(R.id.selection_ok);
        selection_back = findViewById(R.id.selection_back);
        selection_check = findViewById(R.id.selection_check);
        selection_check.setVisibility((SelectionCount > 1) ? View.VISIBLE : View.GONE);
        sendButton = findViewById(R.id.sendButton);
        img_count = findViewById(R.id.img_count);
        mBubbleView = findViewById(R.id.fastscroll_bubble);
        mHandleView = findViewById(R.id.fastscroll_handle);
        mScrollbar = findViewById(R.id.fastscroll_scrollbar);
        mScrollbar.setVisibility(View.GONE);
        mBubbleView.setVisibility(View.GONE);
        bottomButtons = findViewById(R.id.bottomButtons);
        TOPBAR_HEIGHT = Utility.convertDpToPixel(56, SelectMultipleImages.this);
        status_bar_bg = findViewById(R.id.status_bar_bg);
        instantRecyclerView = findViewById(R.id.instantRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        instantRecyclerView.setLayoutManager(linearLayoutManager);
        initaliseadapter = new InstantImageAdapter(this);
        initaliseadapter.AddOnSelectionListner(onSelectionListner);
        instantRecyclerView.setAdapter(initaliseadapter);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.addOnScrollListener(mScrollListener);
        mainFrameLayout = findViewById(R.id.mainFrameLayout);
        BottomBarHeight = Utility.getSoftButtonsBarSizePort(this);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(0, 0, 0, BottomBarHeight);
        mainFrameLayout.setLayoutParams(lp);
        FrameLayout.LayoutParams fllp = (FrameLayout.LayoutParams) sendButton.getLayoutParams();
        fllp.setMargins(0, 0, (int) (Utility.convertDpToPixel(16, this)),
                (int) (Utility.convertDpToPixel(174, this)));
        sendButton.setLayoutParams(fllp);
        mainImageAdapter = new MainImageAdapter(this);
        mLayoutManager = new GridLayoutManager(this, MainImageAdapter.spanCount);
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (mainImageAdapter.getItemViewType(position)) {
                    case MainImageAdapter.HEADER:
                        return MainImageAdapter.spanCount;
                    case MainImageAdapter.ITEM:
                        return 1;
                    default:
                        return 1;
                }
            }
        });
        recyclerView.setLayoutManager(mLayoutManager);
        mainImageAdapter.AddOnSelectionListner(onSelectionListner);
        recyclerView.setAdapter(mainImageAdapter);
        recyclerView.addItemDecoration(new HeaderItemDecoration(this, recyclerView, mainImageAdapter));
        mHandleView.setOnTouchListener(this);
        clickme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCamera.captureImage(new CameraKitEventCallback<CameraKitImage>() {
                    @Override
                    public void callback(CameraKitImage cameraKitImage) {
                        synchronized (cameraKitImage) {
                            File photo = Utility.writeImage(cameraKitImage.getJpeg());
                            selectionList.clear();
                            PhotoSelectPojo photoSelectPojo = new PhotoSelectPojo();
                            photoSelectPojo.headerDate = "";
                            photoSelectPojo.contentUrl = "";
                            photoSelectPojo.url = photo.getAbsolutePath();
                            photoSelectPojo.scrollerDate = "";
                            selectionList.add(photoSelectPojo);
                            returnObjects();
                        }

                    }
                });
                mCamera.captureImage();

            }
        });
        selection_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnObjects();
            }
        });
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnObjects();
            }
        });
        selection_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
        selection_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                topbar.setBackgroundColor(colorPrimaryDark);
                selection_count.setText("Tap photo to select");
                img_count.setText("" + selectionList.size());
                DrawableCompat.setTint(selection_back.getDrawable(), Color.parseColor("#ffffff"));
                LongSelection = true;
                selection_check.setVisibility(View.GONE);
            }
        });
        final ImageView iv = (ImageView) flash.getChildAt(0);
        mCamera.setFlash(CameraKit.Constants.FLASH_AUTO);
        flash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int height = flash.getHeight();
                iv.animate().translationY(height).setDuration(100).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        iv.setTranslationY(-(height / 2));
                        int image_id = 0;
                        switch (mCamera.getFlash()) {
                            case CameraKit.Constants.FLASH_ON: {
                                iv.setImageResource(R.drawable.ic_flash_auto_black_24dp);
                                mCamera.setFlash(CameraKit.Constants.FLASH_AUTO);
                            }
                            break;
                            case CameraKit.Constants.FLASH_AUTO: {
                                iv.setImageResource(R.drawable.ic_flash_off_black_24dp);
                                mCamera.setFlash(CameraKit.Constants.FLASH_OFF);
                            }
                            break;
                            default: {
                                iv.setImageResource(R.drawable.ic_flash_on_black_24dp);
                                mCamera.setFlash(CameraKit.Constants.FLASH_ON);
                            }
                            break;

                        }

                        iv.animate().translationY(0).setDuration(50).setListener(null).start();
                    }
                }).start();
            }
        });

        front.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ObjectAnimator oa1 = ObjectAnimator.ofFloat(front, "scaleX", 1f, 0f).setDuration(150);
                final ObjectAnimator oa2 = ObjectAnimator.ofFloat(front, "scaleX", 0f, 1f).setDuration(150);
                oa1.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        front.setImageResource(R.drawable.ic_photo_camera);
                        oa2.start();
                    }
                });
                oa1.start();
                mCamera.setFacing((mCamera.getFacing() == CameraKit.Constants.FACING_FRONT) ? CameraKit.Constants.FACING_BACK : CameraKit.Constants.FACING_FRONT);
            }
        });
        DrawableCompat.setTint(selection_back.getDrawable(), colorPrimaryDark);

        updateImages();
    }

    private void updateImages() {
        mainImageAdapter.ClearList();
        new ImageFetcher() {
            @Override
            protected void onPostExecute(ArrayList<PhotoSelectPojo> photoSelectPojos) {
                super.onPostExecute(photoSelectPojos);
                initaliseadapter.addImageList(photoSelectPojos);
                mainImageAdapter.addImageList(photoSelectPojos);
                setBottomSheetBehavior();
            }
        }.execute(Utility.getCursor(SelectMultipleImages.this));
    }

    private void setBottomSheetBehavior() {
        View bottomSheet = findViewById(R.id.bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setPeekHeight((int) (Utility.convertDpToPixel(194, this)));
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(View bottomSheet, int newState) {
            }

            @Override
            public void onSlide(View bottomSheet, float slideOffset) {
                Utility.manupulateVisibility(SelectMultipleImages.this, slideOffset,
                        instantRecyclerView, recyclerView, status_bar_bg,
                        topbar, bottomButtons, sendButton, LongSelection);
                if (slideOffset == 1) {
                    Utility.showScrollbar(mScrollbar, SelectMultipleImages.this);
                    mainImageAdapter.notifyDataSetChanged();
                    mViewHeight = mScrollbar.getMeasuredHeight();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            setViewPositions(getScrollProportion(recyclerView));
                        }
                    });
                    sendButton.setVisibility(View.GONE);
                } else if (slideOffset == 0) {

                    initaliseadapter.notifyDataSetChanged();
                    hideScrollbar();
                    img_count.setText("" + selectionList.size());
                }
            }
        });
    }

    //
    private float getScrollProportion(RecyclerView recyclerView) {
        final int verticalScrollOffset = recyclerView.computeVerticalScrollOffset();
        final int verticalScrollRange = recyclerView.computeVerticalScrollRange();
        final float rangeDiff = verticalScrollRange - mViewHeight;
        float proportion = (float) verticalScrollOffset / (rangeDiff > 0 ? rangeDiff : 1f);
        return mViewHeight * proportion;
    }

    private void setViewPositions(float y) {
        int handleY = Utility.getValueInRange(0, (int) (mViewHeight - mHandleView.getHeight()), (int) (y - mHandleView.getHeight() / 2));
        mBubbleView.setY(handleY + Utility.convertDpToPixel((56), SelectMultipleImages.this));
        mHandleView.setY(handleY);
    }


    private void setRecyclerViewPosition(float y) {
        if (recyclerView != null && recyclerView.getAdapter() != null) {
            int itemCount = recyclerView.getAdapter().getItemCount();
            float proportion;

            if (mHandleView.getY() == 0) {
                proportion = 0f;
            } else if (mHandleView.getY() + mHandleView.getHeight() >= mViewHeight - sTrackSnapRange) {
                proportion = 1f;
            } else {
                proportion = y / mViewHeight;
            }

            int scrolledItemCount = Math.round(proportion * itemCount);
            int targetPos = Utility.getValueInRange(0, itemCount - 1, scrolledItemCount);
            recyclerView.getLayoutManager().scrollToPosition(targetPos);

            if (mainImageAdapter != null) {
                mBubbleView.setText(mainImageAdapter.getSectionMonthYearText(targetPos));
            }
        }
    }

    private void showBubble() {
        if (!Utility.isViewVisible(mBubbleView)) {
            mBubbleView.setVisibility(View.VISIBLE);
            mBubbleView.setAlpha(0f);
            mBubbleAnimator = mBubbleView.animate().alpha(1f)
                    .setDuration(sBubbleAnimDuration)
                    .setListener(new AnimatorListenerAdapter() {
                        // adapter required for new alpha value to stick
                    });
            mBubbleAnimator.start();
        }
    }

    private void hideBubble() {
        if (Utility.isViewVisible(mBubbleView)) {
            mBubbleAnimator = mBubbleView.animate().alpha(0f)
                    .setDuration(sBubbleAnimDuration)
                    .setListener(new AnimatorListenerAdapter() {

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mBubbleView.setVisibility(View.GONE);
                            mBubbleAnimator = null;
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                            super.onAnimationCancel(animation);
                            mBubbleView.setVisibility(View.GONE);
                            mBubbleAnimator = null;
                        }
                    });
            mBubbleAnimator.start();
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (event.getX() < mHandleView.getX() - ViewCompat.getPaddingStart(mHandleView)) {
                    return false;
                }
                mHandleView.setSelected(true);
                handler.removeCallbacks(mScrollbarHider);
                Utility.cancelAnimation(mScrollbarAnimator);
                Utility.cancelAnimation(mBubbleAnimator);

                if (!Utility.isViewVisible(mScrollbar) && (recyclerView.computeVerticalScrollRange() - mViewHeight > 0)) {
                    mScrollbarAnimator = Utility.showScrollbar(mScrollbar, SelectMultipleImages.this);
                }

                if (mainImageAdapter != null) {
                    showBubble();
                }

                if (mFastScrollStateChangeListener != null) {
                    mFastScrollStateChangeListener.onFastScrollStart(this);
                }
            case MotionEvent.ACTION_MOVE:
                final float y = event.getRawY();
                mBubbleView.setText(mainImageAdapter.getSectionText(recyclerView.getVerticalScrollbarPosition()));
                setViewPositions(y - TOPBAR_HEIGHT);
                setRecyclerViewPosition(y);
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mHandleView.setSelected(false);
                if (mHideScrollbar) {
                    handler.postDelayed(mScrollbarHider, sScrollbarHideDelay);
                }
                hideBubble();
                if (mFastScrollStateChangeListener != null) {
                    mFastScrollStateChangeListener.onFastScrollStop(this);
                }
                return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onBackPressed() {
        selectionList.clear();
//        } else
        if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            super.onBackPressed();
        }

    }


    @Override
    public void onPermissionsGranted(int requestCode) {
        Utility.SetupStatusBarHiden(this);
        Utility.hideStatusBar(this);
        setContentView(R.layout.activity_main_lib);
        Fresco.initialize(this);
        initialize();
    }


}
