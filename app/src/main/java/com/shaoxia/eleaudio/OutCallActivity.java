package com.shaoxia.eleaudio;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by gonglt1 on 2018/1/21.
 */

public class OutCallActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "OutCallActivity";
    private Button mBtnSel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_out_call);
        TextView titleView = findViewById(R.id.title);
        titleView.setText(R.string.call_title);

        mBtnSel = findViewById(R.id.sel_ok);
        mBtnSel.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sel_ok:
                Intent intent = new Intent(this, AudioSelectActivity.class);
                startActivityForResult(intent, 1);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
