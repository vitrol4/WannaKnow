package br.com.vitrol4.wannaknow;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.com.vitrol4.wannaknow.data.Place;
import br.com.vitrol4.wannaknow.data.PlacePersistence;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private FloatingActionButton fabListPlaces;

    public static final int RESULT_LIST_PLACES = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fabListPlaces = (FloatingActionButton) findViewById(R.id.fab_list_places);
        fabListPlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ListPlacesActivity.class);
                startActivityForResult(intent, RESULT_LIST_PLACES);
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

//        if (PlacePersistence.places.size() == 0) {
//            PlacePersistence.generateData();
//        }

        mAdapter = new PlaceAdapter(PlacePersistence.places);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == RESULT_LIST_PLACES) {

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private class PlaceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<Place> places;

        public PlaceAdapter(List<Place> places) {
            this.places = places;
        }

        public String dateFormat(Date date) {
            return DateUtils.getRelativeTimeSpanString(
                    date.getTime(),
                    Calendar.getInstance().getTimeInMillis(),
                    0).toString();
        }

        public class PlaceViewHolder extends RecyclerView.ViewHolder {

            public TextView title;
            public TextView description;
            public TextView created;

            public PlaceViewHolder(View v) {
                super(v);
                title = (TextView) v.findViewById(R.id.text_title);
                description = (TextView) v.findViewById(R.id.text_description);
                created = (TextView) v.findViewById(R.id.text_created);
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_places, parent, false);
            return new PlaceViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            PlaceViewHolder placeViewHolder = (PlaceViewHolder) holder;
            Place place = places.get(position);

            placeViewHolder.title.setText(place.getTitle());
            placeViewHolder.description.setText(place.getDescription());
            placeViewHolder.created.setText(dateFormat(place.getCreated()));
        }

        @Override
        public int getItemCount() {
            return places.size();
        }
    }

}
