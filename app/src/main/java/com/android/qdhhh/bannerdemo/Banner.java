package com.android.qdhhh.bannerdemo;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by qdhhh on 2016/10/28.
 */

public class Banner extends FrameLayout {


    private Context context;   //上下文

    private ViewPager banner_vp_id;//显示图片的ViewPager

    private TextView banner_tv_id;//显示标题

    private LinearLayout banner_point_id; // 显示指示器

    private int SWITCH = 100; // 轮转

    private int UNSWITCH = 200; // 不轮转

    private List<View> views; //图片的ImageView的集合

    private boolean isTouching = false; // 是否在手动滑动V

    private boolean isSwitch = true; // 是否进行轮播

    private int delayTime = 2000; // 轮播转换时间

    private int currentPosition = 0; // 当前ViewPager的位置

    private long releaseTime = 0; // 上次轮播的事件

    private int count;

    private ViewPagerAdapter adapter;

    private PageChangeListener pageChangeListener;

    final Runnable runnable = new Runnable() {

        @Override
        public void run() {
            if (context != null && isSwitch) {
                long currentTimeMillis = System.currentTimeMillis();
                if (currentTimeMillis - releaseTime > delayTime) {
                    handler.sendEmptyMessage(SWITCH);
                } else {
                    handler.sendEmptyMessage(UNSWITCH);
                }
            }
        }
    };

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (msg.what == SWITCH) {
                if (!isTouching) {
                    int posttion = currentPosition + 1;
                    banner_vp_id.setCurrentItem(posttion, true);
                }
                releaseTime = System.currentTimeMillis();
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, delayTime);
                return;

            }
            if (msg.what == UNSWITCH && views.size() > 0) {
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, delayTime);
            }
            super.handleMessage(msg);
        }
    };


    public Banner(Context context) {
        this(context, null);
    }

    public Banner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Banner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView();
        setData();
    }


    /**
     * 初始化控件
     */
    private void initView() {
        LayoutInflater.from(context).inflate(R.layout.banner_layout, this, true);
        banner_vp_id = (ViewPager) findViewById(R.id.banner_vp_id);
        banner_tv_id = (TextView) findViewById(R.id.banner_tv_id);
        banner_point_id = (LinearLayout) findViewById(R.id.banner_point_id);
    }


    /**
     * 对控件进行设置
     */
    public void setData() {

        // 设置轮播图
        ImageView imageView = null;
        views = new LinkedList<>();
        for (int i = 0; i < count; i++) {
            imageView = new ImageView(context);
            imageView.setImageResource(R.drawable.qwe);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            views.add(imageView);
        }

        // 设置指示器
        ImageView point = null;
        ClickListener clickListener = new ClickListener();
        for (int i = 0; i < count; i++) {
            point = new ImageView(context);
            point.setTag(i);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    20, 20);
            lp.setMargins(10, 0, 10, 0);
            point.setLayoutParams(lp);
            point.setOnClickListener(clickListener);
            point.setBackgroundResource(R.drawable.selector_banner_point);
            banner_point_id.addView(point, i);
        }

        adapter = new ViewPagerAdapter();

        pageChangeListener = new PageChangeListener();
        banner_vp_id.addOnPageChangeListener(pageChangeListener);
        banner_vp_id.setAdapter(adapter);
        currentPosition = Integer.MAX_VALUE / 2 - (Integer.MAX_VALUE / 2) % count;
        banner_vp_id.setCurrentItem(currentPosition);
        setIndicator(0);
        setWheel(true);
    }


    private final class ClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            int position = banner_vp_id.getCurrentItem() -
                    (banner_vp_id.getCurrentItem() % count) + (int) v.getTag();
            banner_vp_id.setCurrentItem(position);
        }
    }


    /**
     * 设置指示器，和文字内容
     *
     * @param position 指示器位置
     */
    private void setIndicator(int position) {

        for (int i = 0; i < count; i++) {
            (banner_point_id.getChildAt(i)).setEnabled(true);
        }
        (banner_point_id.getChildAt(position % count)).setEnabled(false);
    }


    /**
     * 页面适配器 返回对应的view
     *
     * @author Yuedong Li
     */
    private class ViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public View instantiateItem(ViewGroup container, final int position) {
            View view = views.get(position % count);
            container.addView(view);
            return view;
        }

    }

    private final class PageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            setIndicator(position);
            setTextView(position, null);
            currentPosition = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {

            if (state == 1) { // viewPager在滚动
                isTouching = true;
                return;
            } else if (state == 0) { // viewPager滚动结束
                releaseTime = System.currentTimeMillis();
            }
            isTouching = false;
        }
    }


    private void setTextView(int position, String text) {
        banner_tv_id.setText(position + "hahahahaha");
    }

    /**
     * 设置是否轮播，默认轮播,轮播一定是循环的
     *
     * @param isSwitch
     */
    public void setWheel(boolean isSwitch) {
        this.isSwitch = isSwitch;
        if (isSwitch) {
            handler.postDelayed(runnable, delayTime);
        }
    }


    /**
     * 刷新数据
     */
    public void refreshData() {
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    /**
     * 是否处于轮播状态
     *
     * @return
     */
    public boolean isSwitch() {
        return isSwitch;
    }

    /**
     * 设置轮播暂停时间,单位毫秒（默认3000毫秒）
     *
     * @param delayTime
     */
    public void setDelayT(int delayTime) {
        this.delayTime = delayTime;
    }


    /**
     * 轮播控件的监听事件接口
     *
     * @author minking
     */
    public static interface ImageCycleViewListener {


    }
}
