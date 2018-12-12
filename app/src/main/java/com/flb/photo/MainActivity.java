package com.flb.photo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.dm.photo_view.PhotoViewActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn;
    private List<String> mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initData() {
        mList = new ArrayList<>(4);
        mList.add("https://img02.sogoucdn.com/app/a/100520093/f2828110856c942d-3e2569e1b5cad122-0646cb94776fe4005b98afd24d717a18.jpg");
        mList.add("https://img04.sogoucdn.com/app/a/100520093/d218eb16d1924d4d-d370603e483134c6-c8d2b7fac06d3d504f0dec35c31d19ea.jpg");
        mList.add("https://img01.sogoucdn.com/app/a/100520093/803d8006b5d521bb-2eb356b9e8bc4ae6-3a6939cc9a0645afbe8c80a008fc9c7a.jpg");
        mList.add("http://img4.imgtn.bdimg.com/it/u=2310514390,3580363630&fm=26&gp=0.jpg");
    }

    private void initView() {
        btn = findViewById(R.id.btn);
        btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn){
            PhotoViewActivity.setData(mList);
            startActivity(new Intent(this,PhotoViewActivity.class));
        }
    }
}
