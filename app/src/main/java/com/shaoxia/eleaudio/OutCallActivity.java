package com.shaoxia.eleaudio;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.shaoxia.eleaudio.log.Logger;
import com.shaoxia.eleaudio.utils.CRCUtils;
import com.shaoxia.eleaudio.utils.FileUtils;

import java.io.File;
import java.util.HashMap;

/**
 * Created by gonglt1 on 2018/1/21.
 */

public class OutCallActivity extends BaseBleComActivity implements View.OnClickListener {
    private static final String TAG = "OutCallActivity";
    public static final int FRAME_LENGTH = 20;

    private Button mBtnSel;
    private TextView mTvFileName;

    private String mPath;
    private byte[] mFileContent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_out_call);
        TextView titleView = findViewById(R.id.title);
        titleView.setText(R.string.call_title);

        mTvFileName = findViewById(R.id.file_name);
        mBtnSel = findViewById(R.id.sel_ok);
        mBtnSel.setOnClickListener(this);

        Button sendBtn = findViewById(R.id.send);
        sendBtn.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sel_ok:
                Intent intent = new Intent(this, AudioSelectActivity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.send:
                sendFile();
                break;
        }
    }


    private void sendFile() {
        if (mFileContent == null) {
            Logger.d(TAG, "sendFile: file content is null");
            return;
        }
        byte[] request = {(byte) 0xB4, 0x05, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00};
        byte[] data = {0x00};
        int check = getCrc(data);
        request[7] = (byte) ((check & 0xff00) >> 8);
        request[8] = (byte) (check & 0x00ff);
        sendData(request);
    }

    @Override
    protected void onReceiveData(byte[] array) {
        super.onReceiveData(array);
        if (array.length < 9) {
            Logger.d(TAG, "onReceiveData: receive data lenth < 9");
            return;
        }
        if (array[0] != (byte) 0xEB) {
            Logger.d(TAG, "onReceiveData: Head data error");
            return;
        }

        int index = 1;
        int len = 0;
        byte[] data;
        int crc;
        switch (array[1]) {
            case 0x05:
                data = new byte[FRAME_LENGTH];
                data[0] = (byte) 0xb4;
                data[1] = 0x06;

                index = 1;

                data[2] = 0x00;
                data[3] = 0x01;

                len = mFrames.get(index).length;

                data[4] = (byte) ((len & 0xff00) >> 8);
                data[5] = (byte) (len & 0x00ff);

                System.arraycopy(mFrames.get(index), 0, data, 6, len);

                crc = getCrc(mFrames.get(index));

                data[FRAME_LENGTH - 2] = (byte) ((crc & 0xff00) >> 8);
                data[FRAME_LENGTH - 1] = (byte) (crc & 0x00ff);
                sendData(data);
                break;
            case 0x06:
                if (array[6] == (byte) 0x55) {
                    Toast.makeText(this, "数据传输错误，请重新发送", Toast.LENGTH_SHORT).show();
                    Logger.e(TAG, "onReceiveData: transfort error");
                    return;
                }

                index = (array[2] << 8) + array[3] + 1;

                if (index > mFrames.size()) {
                    sendEnd();
                    return;
                }

                data = new byte[FRAME_LENGTH];
                data[0] = (byte) 0xb4;
                data[1] = 0x06;

                data[2] = (byte) ((index & 0xff00) >> 8);
                data[3] =  (byte) (index & 0x00ff);

                len = mFrames.get(index).length;

                data[4] = (byte) ((len & 0xff00) >> 8);
                data[5] = (byte) (len & 0x00ff);

                System.arraycopy(mFrames.get(index), 0, data, 6, len);

                crc = getCrc(mFrames.get(index));

                data[FRAME_LENGTH - 2] = (byte) ((crc & 0xff00) >> 8);
                data[FRAME_LENGTH - 1] = (byte) (crc & 0x00ff);
                sendData(data);
                break;
            case 0x07:
                Toast.makeText(this,"传输完成",Toast.LENGTH_SHORT).show();
                break;
        }

    }

    private void sendEnd() {
        byte[] request = {(byte) 0xB4, 0x07, 0x00, 0x01, 0x00, 0x01, 0x00, 0x00, 0x00};
        byte[] data = {0x00};
        int check = getCrc(data);
        request[7] = (byte) ((check & 0xff00) >> 8);
        request[8] = (byte) (check & 0x00ff);
        sendData(request);
    }

    private int getCrc(byte[] data) {
        return CRCUtils.calCRC(data);
    }

    private HashMap<Integer, byte[]> mFrames = new HashMap<>();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                mPath = data.getStringExtra("path");
                mTvFileName.setText(new File(mPath).getName());
                Logger.d(TAG, "onActivityResult: select path is : " + mPath);
                mFileContent = FileUtils.readFile(mPath);
                int len = FRAME_LENGTH - 8;
                int nums = mFileContent.length / len;
                for (int i = 0; i < nums; i++) {
                    byte[] frame = new byte[len];
                    System.arraycopy(mFileContent, i * len, frame, 0, len);
                    mFrames.put(i, frame);
                }

                if (mFileContent.length % len > 0) {
                    byte[] frame = new byte[len];
                    System.arraycopy(mFileContent, nums * len, frame, 0, mFileContent.length % len);
                    mFrames.put(nums, frame);
                }
            }
        }
    }
}
