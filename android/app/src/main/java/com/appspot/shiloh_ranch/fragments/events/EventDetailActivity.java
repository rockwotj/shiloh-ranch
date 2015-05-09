package com.appspot.shiloh_ranch.fragments.events;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.appspot.shiloh_ranch.R;
import com.appspot.shiloh_ranch.api.model.Event;
import com.appspot.shiloh_ranch.database.Database;

public class EventDetailActivity extends ActionBarActivity {

    private Event mEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String entityKey = getIntent().getStringExtra("ENTITY_KEY");
        mEvent = Database.getDatabase(this).getEvent(entityKey);
        TextView titleView = ((TextView) findViewById(R.id.event_title));
//        TextView timeView = ((TextView) findViewById(R.id.event_time));
//        TextView locationView = ((TextView) findViewById(R.id.event_location));
        titleView.setText(Html.fromHtml(mEvent.getTitle()));
//        if (mEvent.getTime() != null && !mEvent.getTime().isEmpty()) {
//            timeView.setText(mEvent.getTime());
//        } else {
//            String dateTimePublished = Html.fromHtml(mEvent.getDatePublished()).toString();
//            timeView.setText(DateTimeUtils.reformatDateString(dateTimePublished.split("T")[0]));
//        }
//        if (mEvent.getLocation() != null && !mEvent.getLocation().isEmpty()) {
//            locationView.setText(Html.fromHtml(mEvent.getLocation()));
//        } else {
//            locationView.setVisibility(View.GONE);
//        }
        WebView eventContent = (WebView) findViewById(R.id.event_content);
        eventContent.loadData(mEvent.getContent(), "text/html", "utf-8");
        eventContent.setBackgroundColor(Color.TRANSPARENT);
        eventContent.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
