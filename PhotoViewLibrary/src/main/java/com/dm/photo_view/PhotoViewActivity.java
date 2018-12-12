package com.dm.photo_view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dm.photo_view.adapter.PhotoViewAdapter;
import com.dm.photo_view.custom.ViewPagerFixed;
import com.dm.photo_view.widget.CommonPopupWindow;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PhotoViewActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener, PhotoViewAdapter.PhotoAdapter.onItemClick {

    private ImageView iv_back;  // 返回按钮
    private TextView tv_hint;   // 右上角提示
    private LinearLayout linear_top;  // 头部 布局
    private static List<String> pList = new ArrayList<>(); //承载图片的集合
    private ViewPagerFixed photo_vp;  // ViewPager
    private ImageView iv_hint;   // 集合为空时 用来提示的图片
    private static int mLayoutId = R.layout.layout_popwindow_item; // 布局ID
    private static int mHeight = 300;  // popwindow 高
    public static CommonPopupWindow commonPopupWindow; //popWindow实例
    private static float mLuminance = 0.5f;  // PopWindow 透明度
    private static int mLocation = Gravity.BOTTOM; // PopWindow位置
    private static boolean mShowPopWindow = true;  // 是否显示PopWindow
    private static View upView;  // PopWindow view 实例
    private String saveurl = null; // 保存的地址
    private String imgurl = null;  // 图片地址
    private MyHandle handle;     // 用来隐藏头布局

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置为全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.photoview_main);
        initView();
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        handle = new MyHandle();
        PhotoViewAdapter.PhotoAdapter adapter = new PhotoViewAdapter.PhotoAdapter(pList, this);
        if (pList.size() > 0){
            tv_hint.setText((photo_vp.getCurrentItem()+1)+"/"+pList.size());
            photo_vp.setAdapter(adapter);
            adapter.setOnItemClick(this);
            iv_hint.setVisibility(View.GONE);
            photo_vp.setVisibility(View.VISIBLE);
            handle.sendEmptyMessageDelayed(1,800);
        }else if (pList.size() <= 0){
            iv_hint.setVisibility(View.VISIBLE);
            photo_vp.setVisibility(View.GONE);
        }
    }

    /**
     * 初始化控件
     */
    private void initView() {
        iv_back = findViewById(R.id.iv_back);
        tv_hint = findViewById(R.id.tv_hint);
        linear_top = findViewById(R.id.linear_top);
        photo_vp = findViewById(R.id.photo_vp);
        iv_hint = findViewById(R.id.iv_hint);
        upView = View.inflate(PhotoViewActivity.this,mLayoutId, null);
        iv_back.setOnClickListener(this);
        photo_vp.setOnPageChangeListener(this);
    }

    /**
     * 点击事件
     * @param view
     */
    @Override
    public void onClick(View view) {
        int i = view.getId();
        /**
         * back
         */
        if (i == R.id.iv_back) {
            finish();
        }
        /**
         * 保存图片
         */
        if (i == R.id.tv_save){
            saveImage();
        }
        /**
         * 关闭popWindow
         */
        if (i == R.id.tv_cancel){
            disMiss();
        }
    }

    /**
     * 关闭弹窗
     */
    private void disMiss() {
        if (commonPopupWindow != null){
            if (commonPopupWindow.isShowing()){
                commonPopupWindow.dismiss();
            }
        }
    }

    /**
     * 保存图片到手机根目录下
     */
    private void saveImage() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    saveurl = System.currentTimeMillis()/1000 + "save";
                    HttpURLConnection conn = (HttpURLConnection) new URL(imgurl).openConnection();
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    FileOutputStream fos = new FileOutputStream(
                            new File(Environment.getExternalStorageDirectory() + "/" + saveurl + ".jpg"));
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.flush();
                    fos.close();
                    runOnUiMain("图片保存成功至:" + Environment.getExternalStorageDirectory() + "/" + saveurl + ".jpg");
                } catch (IOException e1) {
                    e1.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                          runOnUiMain("图片保存失败!");
                        }
                    });
                }
            }
        }).start();
        disMiss();
    }

    /**
     * 添加数据
     * @param mList
     */
    public static void setData(List<String> mList){
        if (mList.size() <= 0){ return;}
        pList = mList;
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    /**
     * 运行在主线程
     * @param msg
     */
    private void runOnUiMain(final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(PhotoViewActivity.this,msg,Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onPageSelected(int i) {
        tv_hint.setText((i+1)+"/"+pList.size());
        linear_top.setVisibility(View.VISIBLE);
        handle.sendEmptyMessageDelayed(1,800);
        if (commonPopupWindow != null){
            if (commonPopupWindow.isShowing()){
                commonPopupWindow.dismiss();
            }
        }
    }

    private class  MyHandle extends Handler{
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1){
                if (linear_top.getVisibility() == View.VISIBLE){
                    linear_top.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    @Override
    public void onItemClick() {
        if (linear_top.getVisibility() == View.VISIBLE){
            linear_top.setVisibility(View.GONE);
        }else if (linear_top.getVisibility() == View.GONE){
            linear_top.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onLongItem(String imageUrl) {
        if (!mShowPopWindow){return;}
        if (TextUtils.isEmpty(imageUrl)){ Toast.makeText(PhotoViewActivity.this,"获取图片地址失败!",Toast.LENGTH_SHORT).show();return;}
        this.imgurl = imageUrl;
        showPopWindow();
    }

    private void showPopWindow() {
        upView.setMinimumHeight(mHeight);
        commonPopupWindow.measureWidthAndHeight(upView);
        commonPopupWindow = new CommonPopupWindow.Builder(PhotoViewActivity.this)
                .setView(upView)
                .setWidthAndHeight(ViewGroup.LayoutParams.MATCH_PARENT, upView.getMeasuredHeight())
                .setBackGroundLevel(mLuminance)
                .setOutsideTouchable(false)
                .create();
        if (mLayoutId == R.layout.layout_popwindow_item){
            TextView tv_save = upView.findViewById(R.id.tv_save);
            TextView tv_cancel = upView.findViewById(R.id.tv_cancel);
            tv_save.setOnClickListener(this);
            tv_cancel.setOnClickListener(this);
        }
        commonPopupWindow.showAtLocation(getLayoutInflater().inflate(R.layout.photoview_main, null), mLocation, 0, 0);
    }

    /**
     * 设置 PopWindow 的布局  默认为R.layout.layout_popwindow_item
     * @param layoutId
     */
//    public static void setPopWindowLayoutId(int layoutId){
//        if (layoutId <= 0){ return;}
//        mLayoutId = layoutId;
//    }

    /**
     * 设置 popWindow 的高度   默认600
     * @param height
     */
    public static void setPopWindowHeight(int height){
        if (height <= 0){return;}
        mHeight = height;
    }

    /**
     * 设置弹窗透明度   0.0f~1.0f   值越小越暗   默认0.5
     * @param luminance
     */
    public static void setLuminance(float luminance){
         if (luminance <= 0.0 || luminance > 1.0){return;}
         mLuminance = luminance;
    }

    /**
     * 设置PopWindow 的位置  默认BOTTOM
     * @param location
     */
    public static void setPopWindowLocation(int location){
        mLocation = location;
    }

    /**
     * 设置是否显示弹窗   默认显示
     * @param isShowPopWindow
     */
    public static void setIsShowPopWindow(boolean isShowPopWindow){
        mShowPopWindow = isShowPopWindow;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handle.removeMessages(1);
    }
}
