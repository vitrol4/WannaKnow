package br.com.vitrol4.wannaknow;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Formatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import br.com.vitrol4.wannaknow.data.Place;
import br.com.vitrol4.wannaknow.data.PlacePersistence;

public class ListPlacesActivity extends ManagerLocationActivity {

    private MyListAdapter adapter;
    private List<Place> list;
    private ProgressDialog progressDialog;

    @Override
    protected void setLocation(Location location) {
        String clienId = "3CX0442VU1HVADWPIFRWRKIANJ0CYDSXPPNQV3QLV0R51K3I";
        String clienSecret = "GGS0VL53WWV11BBL0NSBWFCUVXRYBKPL503ZPGWNNNGBGII4";
        String v = "20130815";
        String urlFormat = "https://api.foursquare.com/v2/venues/search?client_id=%s&client_secret=%s&v=%s&ll=%f,%f";

        String url = new Formatter(Locale.US).format(urlFormat, clienId, clienSecret, v, location.getLatitude(), location.getLongitude()).toString();
        Log.d("list_places", url);

        RequestQueue queue = Volley.newRequestQueue(this);
        Request request = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("list_places", response.toString());
                        try {
                            JSONArray venues = response.getJSONObject("response").getJSONArray("venues");
                            for (int i = 0; i < venues.length(); i++) {
                                JSONObject placeObject = venues.getJSONObject(i);
                                list.add(
                                        new Place(placeObject.getString("name"),
                                                placeObject.getString("id"),
                                                new Date()));
                            }
                            adapter.notifyDataSetChanged();
                            progressDialog.dismiss();
                        } catch (JSONException e) {
                            Log.e("list_places", e.getMessage(), e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Ocorreu um erro. Tente novamente.", Toast.LENGTH_SHORT).show();
            }
        });
        progressDialog.show();
        queue.add(request);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Aguarde");
        progressDialog.setMessage("Buscando locais próximos ...");

        list = new LinkedList<>();
        adapter = new MyListAdapter(this, list);
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Place place = (Place) getListAdapter().getItem(position);

        if (PlacePersistence.places.size() == 0) {
            PlacePersistence.places.add(place);
            Toast.makeText(getApplicationContext(), place.getTitle() + " foi adicionado a sua lista", Toast.LENGTH_SHORT).show();
            adapter.notifyDataSetChanged();
            return;
        }

        for (Place p : PlacePersistence.places) {
            if (p.getTitle().equalsIgnoreCase(place.getTitle())) {
                PlacePersistence.places.remove(p);
                Toast.makeText(getApplicationContext(), place.getTitle() + " foi removido a sua lista", Toast.LENGTH_SHORT).show();
                adapter.notifyDataSetChanged();
            } else {
                PlacePersistence.places.add(place);
                Toast.makeText(getApplicationContext(), place.getTitle() + " foi adicionado a sua lista", Toast.LENGTH_SHORT).show();
                adapter.notifyDataSetChanged();
            }
        }

    }

    private class MyListAdapter extends ArrayAdapter<Place> {

        LayoutInflater inflater;

        public MyListAdapter(Activity activity, List<Place> objects) {
            super(activity, R.layout.item_new_places, objects);
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            final ViewHolder holder;
            Place place = getItem(position);

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_new_places, null);
                holder = new ViewHolder();
                holder.title = (TextView) convertView.findViewById(R.id.text_title);
                holder.description = (TextView) convertView.findViewById(R.id.text_description);
                holder.star = (ImageView) convertView.findViewById(R.id.star);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.title.setText(place.getTitle());
            holder.description.setText(place.getDescription());

            for (Place p : PlacePersistence.places) {
                if (p.getTitle().equalsIgnoreCase(place.getTitle())) {
                    holder.star.setImageResource(R.drawable.fill_star);
                }
            }

            return convertView;
        }

        private class ViewHolder {
            public TextView title;
            public TextView description;
            public ImageView star;
        }
    }
}
