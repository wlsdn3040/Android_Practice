package kimjinwoo.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.geometry.LatLngBounds;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.PolygonOverlay;
import com.naver.maps.map.util.FusedLocationSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback
{

    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    private NaverMap mNaverMap;
    private MapView mapView;
    private FusedLocationSource mLocationSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_main);

        mapView = (MapView) findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        mLocationSource =  new FusedLocationSource(this, PERMISSION_REQUEST_CODE);
    }
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {

        ArrayList<LatLng> location_lists = new ArrayList<>();
        location_lists.add(new LatLng(35.94621092327648, 126.68224089988206));
        location_lists.add(new LatLng(35.968461912640315, 126.73801254036266));
        location_lists.add(new LatLng(35.97080453502983, 126.61704180848953));
        ArrayList<Marker> Marker_lists = new ArrayList<>();
        Marker_lists.add(new Marker(location_lists.get(0)));
        Marker_lists.add(new Marker(location_lists.get(1)));
        Marker_lists.add(new Marker(location_lists.get(2)));

        for(int i =0; i< 3; i++){
            Marker_lists.get(i).setPosition(location_lists.get(i));
        }
        for(Marker mak : Marker_lists){
            mak.setMap(naverMap);
        }
        CameraUpdate initial_location= CameraUpdate.scrollTo(location_lists.get(0));
        naverMap.moveCamera(initial_location);

        PolygonOverlay polygon = new PolygonOverlay();
        polygon.setCoords(location_lists);
        polygon.setColor(0x80ff0000);
        polygon.setMap(naverMap);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.Maps, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) naverMap.setMapType(NaverMap.MapType.Basic);
                if (position == 1) naverMap.setMapType(NaverMap.MapType.Satellite);
                if (position == 2) naverMap.setMapType(NaverMap.MapType.Terrain);
                if (position == 3) naverMap.setMapType(NaverMap.MapType.Hybrid);
                Toast.makeText(MainActivity.this,"????????? ???: "
                        +spinner.getItemAtPosition(position),Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        ArrayList<LatLng> location_lists2 = new ArrayList<>();
        ArrayList<Marker> Marker_lists2 = new ArrayList<>();
        PolygonOverlay polygon2 = new PolygonOverlay();

        naverMap.setOnMapClickListener((pointF, latLng) -> {
            Toast.makeText(MainActivity.this, latLng.latitude + "," + latLng.longitude, Toast.LENGTH_SHORT)
                    .show();

            location_lists2.add(new LatLng(latLng.latitude, latLng.longitude));
            for (int i = 0; i < location_lists2.size(); i++) {
                Marker_lists2.add(new Marker(location_lists2.get(i)));
            }
            for (int i = 0; i < location_lists2.size(); i++) {
                Marker_lists2.get(i).setPosition(location_lists2.get(i));
            }
            sortPointsClockwise(location_lists2);
            for (Marker mak : Marker_lists2) {
                mak.setMap(naverMap);
            }
            if (location_lists2.size()>2) {
                polygon2.setCoords(location_lists2);
                polygon2.setMap(naverMap);
           }
        });

        Button button1 = (Button) findViewById(R.id.button1);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i=0; i < location_lists2.size(); i++){
                    for (Marker mak : Marker_lists2){
                        mak.setMap(null);
                    }
                    polygon2.setMap(null);
                    location_lists2.clear();
                    Marker_lists2.clear();;
                }
            }
        });

        Button button2 = (Button) findViewById(R.id.button2);

        button2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                naverMap.setExtent(new LatLngBounds(new LatLng(35.97080453502983, 126.73801254036266),
                        new LatLng(35.94621092327648, 126.61704180848953)));
                naverMap.setMinZoom(5.0);
            }
        });
        mNaverMap = naverMap;
        mNaverMap.setLocationSource(mLocationSource);

        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE);

        mNaverMap.setOnMapLongClickListener((point, coord) -> {
            RequsetHTTP http = new RequsetHTTP();
            http.execute(coord.latitude, coord.longitude);
        });

        Button button3 = (Button) findViewById(R.id.button3);


        button3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                EditText editText = (EditText)findViewById(R.id.editText);
                String ltlg = editText.getText().toString();
                RequsetAdr http2 = new RequsetAdr();
                http2.execute(ltlg);
            }
        });
    }
    public ArrayList<LatLng> sortPointsClockwise(ArrayList<LatLng> location_lists2) {

        float averageX = 0;
        float averageY = 0;

        for (LatLng latLng : location_lists2) {
            averageX += latLng.latitude;
            averageY += latLng.longitude;
        }
        final float finalAverageX = averageX / location_lists2.size();;
        final float finalAverageY = averageY / location_lists2.size();;

        Comparator<LatLng> comparator = new Comparator<LatLng>() {
            @Override
            public int compare(LatLng lhs, LatLng rhs) {
                double latAngle = Math.atan2(lhs.longitude - finalAverageY, rhs.longitude - finalAverageX);
                double lngAngle = Math.atan2(rhs.longitude - finalAverageY, lhs.longitude - finalAverageX);

                if (latAngle < lngAngle) return -1;
                if (lngAngle < latAngle) return 1;

                return 0;
            }
        };
        Collections.sort(location_lists2, comparator);

        return location_lists2;
    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mNaverMap.setLocationTrackingMode(LocationTrackingMode.Follow);
            }
        }
    }

    public void onCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        if (view.getId() == R.id.check1) {
            if (checked)
                mNaverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_CADASTRAL, true);
            else
                mNaverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_CADASTRAL, false);
        }
    }
    public class RequsetHTTP extends AsyncTask<Double, Void, String> {

        private String line, receiveMsg;
        Double latitude,longtitude;

        @Override
        protected String doInBackground(Double... params) {
            try {
                latitude = params[0];
                longtitude = params[1];
                String url = "https://naveropenapi.apigw.ntruss.com/map-reversegeocode/v2/gc?coords=" + params[1] + "," + params[0] + "&orders=addr&output=json";
                URL obj = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("X-NCP-APIGW-API-KEY-ID", "l2kna1ctsa");
                connection.setRequestProperty("X-NCP-APIGW-API-KEY", "kdeKLX1RDCbM5gJa4fNNSs7M6V9COYeArhR8Zfzn");

                InputStream is = connection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                StringBuffer buffer = new StringBuffer();
                while ((line = br.readLine()) != null) {
                    buffer.append(line);
                }
                receiveMsg = buffer.toString();
                Log.i("receiveMsg: ", receiveMsg);
                br.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return receiveMsg;
        }

        @Override
        protected void onPostExecute(String jsonStr) {
            super.onPostExecute(jsonStr);
            String adr = getAdr(jsonStr);
            Marker mak = new Marker();
            InfoWindow infoWindow = new InfoWindow();
            infoWindow.setAdapter(new InfoWindow.DefaultTextAdapter(getApplicationContext()) {
                @NonNull
                @Override
                public CharSequence getText(@NonNull InfoWindow infoWindow) {
                    return adr;
                }
            });

            mak.setPosition(new LatLng(latitude,longtitude));
            mak.setMap(mNaverMap);
            infoWindow.open(mak);
        }

        private String getAdr(String jsonStr) {
            JsonParser jsonParser = new JsonParser();

            JsonObject jsonObj = (JsonObject) jsonParser.parse(jsonStr);
            JsonArray jsonArray = (JsonArray) jsonObj.get("results");
            jsonObj = (JsonObject) jsonArray.get(0);
            jsonObj = (JsonObject) jsonObj.get("region");
            jsonObj = (JsonObject) jsonObj.get("area1");
            String adr = jsonObj.get("name").getAsString();
            adr = adr + " ";

            jsonObj = (JsonObject) jsonParser.parse(jsonStr);
            jsonArray = (JsonArray) jsonObj.get("results");
            jsonObj = (JsonObject) jsonArray.get(0);
            jsonObj = (JsonObject) jsonObj.get("region");
            jsonObj = (JsonObject) jsonObj.get("area2");
            adr = adr + jsonObj.get("name").getAsString();
            adr = adr + " ";

            jsonObj = (JsonObject) jsonParser.parse(jsonStr);
            jsonArray = (JsonArray) jsonObj.get("results");
            jsonObj = (JsonObject) jsonArray.get(0);
            jsonObj = (JsonObject) jsonObj.get("region");
            jsonObj = (JsonObject) jsonObj.get("area3");
            adr = adr + jsonObj.get("name").getAsString();
            adr = adr + " ";

            jsonObj = (JsonObject) jsonParser.parse(jsonStr);
            jsonArray = (JsonArray) jsonObj.get("results");
            jsonObj = (JsonObject) jsonArray.get(0);
            jsonObj = (JsonObject) jsonObj.get("land");
            adr = adr + jsonObj.get("number1").getAsString();
            adr = adr + "-";

            jsonObj = (JsonObject) jsonParser.parse(jsonStr);
            jsonArray = (JsonArray) jsonObj.get("results");
            jsonObj = (JsonObject) jsonArray.get(0);
            jsonObj = (JsonObject) jsonObj.get("land");
            adr = adr + jsonObj.get("number2").getAsString();

            return adr;
        }
    }
    public class RequsetAdr extends AsyncTask<String, Void, String> {

        private String line, receiveMsg;

        @Override
        protected String doInBackground(String... Strings) {

            try {
                String url = "https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode?query=" + Strings[0];
                URL obj = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("X-NCP-APIGW-API-KEY-ID", "l2kna1ctsa");
                connection.setRequestProperty("X-NCP-APIGW-API-KEY", "kdeKLX1RDCbM5gJa4fNNSs7M6V9COYeArhR8Zfzn");

                InputStream is = connection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                StringBuffer buffer = new StringBuffer();
                while ((line = br.readLine()) != null) {
                    buffer.append(line);
                }
                receiveMsg = buffer.toString();
                Log.i("receiveMsg: ", receiveMsg);
                br.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return receiveMsg;
        }

        private LatLng getAdr(String jsonStr) {
            JsonParser jsonParser = new JsonParser();

            JsonObject jsonObj = (JsonObject) jsonParser.parse(jsonStr);
            JsonArray jsonArray = (JsonArray) jsonObj.get("addresses");
            jsonObj = (JsonObject) jsonArray.get(0);
            double adr2 = jsonObj.get("x").getAsDouble();
            double adr = jsonObj.get("y").getAsDouble();

            return new LatLng(adr,adr2);
        }

        @Override
        protected void onPostExecute(String jsonStr) {
            super.onPostExecute(jsonStr);
            LatLng adr = getAdr(jsonStr);

            Marker mak = new Marker();

            mak.setPosition(getAdr(jsonStr));
            mak.setMap(mNaverMap);
        }
    }


}
