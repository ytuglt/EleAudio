package com.shaoxia.eleaudio;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.shaoxia.eleaudio.adapter.AudiosAdapter;
import com.shaoxia.eleaudio.log.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gonglt1 on 2018/1/23.
 */

public class AudioSelectActivity extends BaseActivity {
    private static final String TAG = "AudioSelectActivity";

    private RecyclerView mOutRecycleView;
    private AudiosAdapter mAudioAdapter;

    private List<String> mAudioNames = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_select);
        initRecycleView();
        CursorLoader loader = new CursorLoader(this,
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                MediaStore.Video.Media.DEFAULT_SORT_ORDER);
        MediaLoaderCallBacks mMediaLoaderCallBacks = new MediaLoaderCallBacks(loader);
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(1, null, mMediaLoaderCallBacks);
    }

    private void initRecycleView() {
        mOutRecycleView = findViewById(R.id.out_call_list);
        mOutRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mOutRecycleView.addItemDecoration(new RecycleViewDivider(this));

        mAudioAdapter = new AudiosAdapter(mAudioNames);
        mOutRecycleView.setAdapter(mAudioAdapter);
        mAudioAdapter.setOnItemClickListener(new AudiosAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
            }
        });
    }


    /**
     * Load alarm, ringtone, music and record
     */
    private class MediaLoaderCallBacks implements LoaderManager.LoaderCallbacks<Cursor> {
        private CursorLoader mLoader;

        MediaLoaderCallBacks(CursorLoader loader) {
            mLoader = loader;
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return mLoader;
        }

        @Override
        public void onLoadFinished(Loader loader, Cursor cursor) {
            // For the reason that Phone ring change will cause Music/Record reload
            if (null != cursor && cursor.moveToFirst()) {
                do {
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                    Logger.d(TAG, "onLoadFinished: path is  :" + path);
                    mAudioNames.add(new File(path).getName());
                } while (cursor.moveToNext());

                mAudioAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onLoaderReset(Loader loader) {
        }

    }

}
