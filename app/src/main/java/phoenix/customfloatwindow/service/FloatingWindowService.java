package phoenix.customfloatwindow.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import phoenix.customfloatwindow.R;
import phoenix.customfloatwindow.utils.GlobalUtils;
import phoenix.customfloatwindow.view.ExToast;

/**
 * Created by zhenghui on 2016/7/27.
 */
public class FloatingWindowService extends Service {

    private static final String TAG = "FloatingWindowService";

    private static final int DURATION = 5;

    private boolean isAdded = false; // 是否已增加悬浮窗

    private static WindowManager wm;

    private static WindowManager.LayoutParams sParams;

    private View mFloatView;
    private ExToast mFloatViewMIUI;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
        if(GlobalUtils.isLargeMIUIv8()) {
            createFloatViewForMiUi();
        } else {
            createFloatView();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        if (intent != null) {

            if (!isAdded) {
                if(GlobalUtils.isLargeMIUIv8()) {
                    mFloatViewMIUI.show();
                } else {
                    wm.addView(mFloatView, sParams);
                }
                isAdded = true;
            }
            if(GlobalUtils.isLargeMIUIv8()) {
                setupCellViewMIUI8(mFloatViewMIUI);
            } else {
                setupCellView(mFloatView);
            }
        }
    }

    /**
     * 创建 MIUI8 悬浮窗
     */
    private void createFloatViewForMiUi() {
        wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        //计算高度
        DisplayMetrics metric = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metric);
        int yPosition = Math.round(wm.getDefaultDisplay().getHeight() / 3);
        mFloatViewMIUI = new ExToast(getApplicationContext());
        mFloatViewMIUI.setDuration(DURATION);
        mFloatViewMIUI.setAnimations(R.style.float_search);
        mFloatViewMIUI.setGravity(Gravity.RIGHT | Gravity.TOP, 0, yPosition);
        mFloatViewMIUI.show();
        isAdded = true;
    }

    /**
     * 创建悬浮窗
     */
    private void createFloatView() {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mFloatView = layoutInflater.inflate(R.layout.floating_entrance, null);

        wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        sParams = new WindowManager.LayoutParams();

        // 设置window type
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            sParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        } else {
            sParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }


		/*
		 * 如果设置为params.type = WindowManager.LayoutParams.TYPE_PHONE; 那么优先级会降低一些,
		 * 即拉下通知栏不可见
		 */
        sParams.format = PixelFormat.RGBA_8888; // 设置图片格式，效果为背景透明

        // 设置Window flag
        sParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        // 设置悬浮窗的长得宽
        sParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        sParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        //计算高度
        DisplayMetrics metric = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metric);
        int yPosition = Math.round(wm.getDefaultDisplay().getHeight() / 3);

        sParams.gravity = Gravity.RIGHT | Gravity.TOP;
        sParams.x = 0;
        sParams.y = yPosition;

        AnimationDrawable drawable = (AnimationDrawable) ((ImageView) mFloatView.findViewById(R.id.floating_entrance_anim)).getDrawable();
        drawable.start();

        wm.addView(mFloatView, sParams);
        mFloatView.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    wm.removeView(mFloatView);
                } catch (Exception e) {
                    //incase floating window has already dismissed
                }
                FloatingWindowService.this.stopSelf();
            }
        }, DURATION * 1000);
        isAdded = true;
    }

    /**
     * 设置浮窗view内部子控件
     * @param rootview
     */
    private void setupCellView(View rootview) {
        mFloatView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "mFloatView onClick: ");

                wm.removeView(mFloatView);
                isAdded = false;
                launchSomething();
                FloatingWindowService.this.stopSelf();
            }
        });
    }

    /**
     * 设置浮窗view内部子控件 MIUI8
     * @param rootview
     */
    private void setupCellViewMIUI8(ExToast rootview) {
        mFloatViewMIUI.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "mFloatView onClick: ");

                mFloatViewMIUI.hide();
                isAdded = false;
                launchSomething();
                FloatingWindowService.this.stopSelf();
            }
        });

    }

    /**
     * 响应用户点击事件
     */
    private void launchSomething() {
        //TODO do something
        Log.d(TAG, "launchSomething");
    }
}