package com.example.jipark.memorycache.notification;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.jipark.memorycache.R;
import com.example.jipark.memorycache.Utils;
import com.example.jipark.memorycache.models.Memory;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity implements IInformation {

    MemoryAdapter mMemoryAdapter;
    RecyclerView mNotificationRecyclerView;
    TextView mNoMemoriesTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        mNotificationRecyclerView = findViewById(R.id.notification_recycler_view);
        mNoMemoriesTextView = findViewById(R.id.no_memories_text);

        if (Utils.getInstance().getNotificationMemories().size() > 0) {
            mNoMemoriesTextView.setVisibility(View.INVISIBLE);
        }
        else {
            mNoMemoriesTextView.setVisibility(View.VISIBLE);
        }

        mMemoryAdapter = new MemoryAdapter(Utils.getInstance().getNotificationMemories());

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mNotificationRecyclerView.setLayoutManager(mLayoutManager);
        mNotificationRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mNotificationRecyclerView.setAdapter(mMemoryAdapter);
    }
}
