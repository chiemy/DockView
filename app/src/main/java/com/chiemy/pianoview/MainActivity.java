package com.chiemy.pianoview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chiemy.piano.PianoMenuView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int[] ORIENTATION = {
            PianoMenuView.BOTTOM_TO_TOP,
            PianoMenuView.TOP_TO_BOTTOM,
            PianoMenuView.LEFT_TO_RIGHT,
            PianoMenuView.RIGHT_TO_LEFT
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                TextView textView = new TextView(parent.getContext());
                textView.setPadding(20, 20, 20, 20);
                textView.setTextSize(20);
                RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(ViewGroup
                        .LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                textView.setLayoutParams(params);
                return new RecyclerView.ViewHolder(textView) {};
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                int orientation = ORIENTATION[position];
                TextView textView = (TextView) holder.itemView;
                textView.setText(orientationDesc(orientation));
                textView.setOnClickListener(MainActivity.this);
                textView.setTag(orientation);
            }

            @Override
            public int getItemCount() {
                return ORIENTATION.length;
            }
        });
    }

    private String orientationDesc(int orientation) {
        String desc = null;
        switch (orientation) {
            case PianoMenuView.BOTTOM_TO_TOP:
                desc = "Bottom Menu";
                break;
            case PianoMenuView.TOP_TO_BOTTOM:
                desc = "Top Menu";
                break;
            case PianoMenuView.LEFT_TO_RIGHT:
                desc = "Left Menu";
                break;
            case PianoMenuView.RIGHT_TO_LEFT:
                desc = "Right Menu";
                break;
        }
        return desc;
    }

    @Override
    public void onClick(View v) {
        int orientation = Integer.parseInt(v.getTag().toString());
        com.chiemy.pianoview.AppListActivity.start(this, orientation);
    }
}
