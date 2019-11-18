package com.replon.www.replonhomy.Nearby;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class getNearbyPlacesData extends AsyncTask<Object, String, String> {


    String googlePlacesData;

    String url,id;
    String places_id[] = new String[100];
    List<HashMap<String, String>> nearbyplaceList;
    DownloadURL downloadURL;

    static String pl_name;
    List<ContentsNearbyPlaces> placesName=new ArrayList<>();
    String pl_vicinity="address";
    String pl_rating = "rating";
    String pl_phoneNumber = "--NA--";
//
//    private Task mTask;
//
//    public void startTask() {
//        if (mTask != null) {
//            mTask.cancel(true);
//        }
//        mTask = new mTask();
//        mTask.execute();
//    }
//
//    @Override
//    protected void onPreExecute() {
//        super.onPreExecute();
//        cancel(true);
//    }

    @Override
    protected String doInBackground(Object... objects) {

        url = (String) objects[0];

        downloadURL = new DownloadURL();
        try {
            googlePlacesData = downloadURL.readURL(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(isCancelled()){
            return "ENDED";
        }

        return googlePlacesData;
    }


    @Override
    protected void onPostExecute(String s) {


        DataParser parser = new DataParser();
        nearbyplaceList = parser.parse(s);
        Log.d("nearbyplacesdata", "called parse method");
//        showNearbyPlaces1();

    }


    protected void showNearbyPlaces1() {

        for (int i = 0; i < nearbyplaceList.size(); i++) {
            HashMap<String, String> googlePlace = nearbyplaceList.get(i);

            id = googlePlace.get("place_id");
            places_id[i] = id;

            String ans = placeDetails(id);
            String pl[] = ans.split("\n");

            placesName.add(new ContentsNearbyPlaces(pl[0],pl[1],pl[3],pl[2],id));
            Log.i("PLACES ID: ",id);
            if (i==4){
                break;
            }

        }


    }

    protected void showNearbyPlaces2() {

        for (int i = 4; i < nearbyplaceList.size(); i++) {
            HashMap<String, String> googlePlace = nearbyplaceList.get(i);

            id = googlePlace.get("place_id");
            places_id[i] = id;

            String ans = placeDetails(id);
            String pl[] = ans.split("\n");

            placesName.add(new ContentsNearbyPlaces(pl[0],pl[1],pl[3],pl[2],id));
            Log.i("PLACES ID: ",id);

        }


    }


    public String getPlaceUrl(String place_id){

        StringBuilder googlePlaceDetailsUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/details/json?");
        googlePlaceDetailsUrl.append("placeid=" + place_id);
        googlePlaceDetailsUrl.append("&fields=name,rating,international_phone_number,vicinity");
        googlePlaceDetailsUrl.append("&key=" + "AIzaSyCdbPZ0gQb-IvBuXCm2txQpxhV0wAHoVj0");
        Log.d("MapsActivity", "url = "+googlePlaceDetailsUrl.toString());
        return googlePlaceDetailsUrl.toString();
    }

    public String placeDetails(final String id){

        Object dataTransfer2[] = new Object[1];
        dataTransfer2[0] = getPlaceUrl(id);
        String helloans = "hello";

        try {
            helloans = new getPlaceDetails().execute(getPlaceUrl(id),"",pl_name).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return helloans;
    }

}


