package com.replon.www.replonhomy.Nearby;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

public class getPlaceDetails  extends AsyncTask<String, String, String>
{

    String googlePlacesData;

    String place_result;
    String url;
    String place;
    String rating="0.0",phoneNumber="--NA--",vicinity="--NA--",name="";



    @Override
    protected String doInBackground(String... params) {
        try {
            Log.d("GetPlaceDetails", "doInBackground entered");
            url = params[0];
            DownloadURL downloadUrl = new DownloadURL();
            googlePlacesData = downloadUrl.readURL(url);
            place_result = parseDetails(googlePlacesData);
            Log.d("GooglePlacesReadTask", "doInBackground Exit");
        } catch (Exception e) {
            Log.d("GooglePlacesReadTask", e.toString());
        }
        return place_result;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }

    public String parseDetails(String jsonData)
    {
        JSONArray jsonArray = null;
        JSONObject jsonObject, jsonObject2;

        try {
            Log.d("Places", "parse");
            jsonObject = new JSONObject((String) jsonData);
            jsonObject2 = new JSONObject(jsonObject.getString("result"));
            //jsonArray = jsonObject.getJSONArray("result");
            // JSONObject googlePlace=(JSONObject)jsonArray.get(0);
            // String rating = googlePlace.toString();
            //Toast.makeText(c,jsonObject2.toString(),Toast.LENGTH_SHORT).show();


            /*JSONObject openingHours = new JSONObject(jsonObject2.getString("opening_hours"));

            String is_open_now;
            if(openingHours.getString("open_now").equals("true"))
                is_open_now="Open Now";
            else
                is_open_now="Closed Now";*/

            if(! jsonObject2.isNull("name")){

                name = jsonObject2.getString("name");
            }


            if(! jsonObject2.isNull("rating")){

                rating = jsonObject2.getString("rating");
            }

            if(! jsonObject2.isNull("vicinity")){

                vicinity = jsonObject2.getString("vicinity");
            }
            if(! jsonObject2.isNull("international_phone_number")){

                phoneNumber = jsonObject2.getString("international_phone_number");
            }



            place = name+"\n"+vicinity +"\n"+rating +"\n"+phoneNumber;



        } catch (Exception e) {
            Log.d("Places", "parse error");
            e.printStackTrace();
        }
        return place;
    }


}
