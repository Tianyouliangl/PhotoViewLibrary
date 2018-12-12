package com.dm.photo_view.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.dm.photo_view.R;
import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.List;

public class PhotoViewAdapter {
    public static class PhotoAdapter extends PagerAdapter {
        private List<String> pList;
        private Context mContext;
        private onItemClick mOnItemClick;

        public PhotoAdapter(List<String> pList, Context mContext) {
            this.pList = pList;
            this.mContext = mContext;
        }

        @Override
        public int getCount() {
            return pList.size();
        }
        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view == o;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            if (object instanceof View){
                container.removeView((View)object);
            }
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, final int position) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.layout_photoview_item,container,false);
            PhotoView im = view.findViewById(R.id.iv_item_show);
            im.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (mOnItemClick != null) {
                         mOnItemClick.onLongItem(pList.get(position));
                         return true;
                    }
                    return false;
                }
            });
            im.setOnPhotoTapListener(new OnPhotoTapListener() {
                @Override
                public void onPhotoTap(ImageView view, float x, float y) {
                    if (mOnItemClick != null){
                        mOnItemClick.onItemClick();
                    }
                }
            });
            Glide.with(mContext).load(pList.get(position)).placeholder(R.mipmap.loading).error(R.mipmap.error).into(im);
            container.addView(view);
            return view;
        }
        public interface onItemClick{
            void onItemClick();
            void onLongItem(String imageUrl);
        }
        public void setOnItemClick(onItemClick onItemClick){
            this.mOnItemClick = onItemClick;
        }
    }
}
