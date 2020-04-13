package com.coronavirus.precovid;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.comprehend.AmazonComprehendClient;
import com.amazonaws.services.comprehend.model.DetectKeyPhrasesRequest;
import com.amazonaws.services.comprehend.model.DetectKeyPhrasesResult;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class Coronanews extends AppCompatActivity {
private int glob_i=1;
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        MaterialButton btn1=(MaterialButton) findViewById(R.id.btn_news1);
        btn1.setText("Initializing...");
        MaterialButton btn2=(MaterialButton) findViewById(R.id.btn_news2);
        btn2.setText("Initializing...");
        MaterialButton btn3=(MaterialButton) findViewById(R.id.btn_news3);
        btn3.setText("Initializing...");
        MaterialButton btn_dash=(MaterialButton) findViewById(R.id.btn_dashboard);
        AWSCredentials credentials = new BasicAWSCredentials("YOUR_ACCESS_KEY", "YOUR_SECRET_KEY");
//        MaterialButton btn_add=(MaterialButton) findViewById(R.id.btn_add);
        AmazonComprehendClient comprehendClient = new AmazonComprehendClient(credentials);
        MaterialButton btn_review=(MaterialButton) findViewById(R.id.btn_review);
//        btn_add.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                TextView tv2 = new TextView(Coronanews.this);
//                RelativeLayout rLayout = (RelativeLayout) findViewById(R.id.rlayout);
//                RelativeLayout.LayoutParams newParams = new RelativeLayout.LayoutParams(
//                        RelativeLayout.LayoutParams.WRAP_CONTENT,
//                        RelativeLayout.LayoutParams.WRAP_CONTENT);
//                newParams.addRule(RelativeLayout.CENTER_HORIZONTAL, 1000289);
//                if(glob_i==1) {
//                    Log.d("msg","Inside what i want");
//                    newParams.addRule(RelativeLayout.BELOW, 1000155);
//
//                }
//                else
//                    newParams.addRule(RelativeLayout.BELOW, (glob_i-1));
//                tv2.setBackgroundColor(Color.parseColor("#66ccaa"));
//                tv2.setTextColor(Color.WHITE);
//                tv2.setLayoutParams(newParams);
//                tv2.setId(glob_i);
//                ;
//                tv2.setText(((TextInputEditText)findViewById(R.id.input_comments)).getText().toString());
//                rLayout.addView(tv2,newParams);
//                glob_i++;
//
//            }});
        btn_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comments=((TextInputEditText)findViewById(R.id.input_comments)).getText().toString();
                Thread thread = new Thread(new Runnable(){
                    @Override
                    public void run() {

                        String res="";
                        DetectKeyPhrasesRequest detectKeyPhrasesRequest = new DetectKeyPhrasesRequest().withText(comments).withLanguageCode("en");
                        DetectKeyPhrasesResult detectKeyPhrasesResult = comprehendClient.detectKeyPhrases(detectKeyPhrasesRequest);
                        for(int i=0;i<detectKeyPhrasesResult.getKeyPhrases().size();i++)
                        {

                            res=res.concat(String.valueOf(detectKeyPhrasesResult.getKeyPhrases().get(i).getText()));
                            Log.d("mess","Final result is "+res+comments);
                        };
                        String url="https://o53ei5d578.execute-api.us-east-2.amazonaws.com/keywords/";
                        RequestQueue queue1 = Volley.newRequestQueue(getApplicationContext());
//                        String transmit_url="https://dbt0dtctah.execute-api.us-east-2.amazonaws.com/transmit";
                        Map < String, String > keywords_params = new HashMap < String, String > ();
                        keywords_params.put("User", "admin");
                        Log.d("mess","Final result is "+res+comments+detectKeyPhrasesResult.getKeyPhrases());
                        String finalRes = res;
                        JsonObjectRequest stringRequest1 = new JsonObjectRequest(url,new JSONObject(keywords_params),
                                new Response.Listener < JSONObject> () {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                                        String url="https://o53ei5d578.execute-api.us-east-2.amazonaws.com/keywords/keywords";
                                        Map < String, String > keywords_put_params = new HashMap < String, String > ();

                                        try {
                                            Log.d("msg","Inside the get keywords "+response+": "+response.get("Keyword").toString()+" finalres "+finalRes);
                                            keywords_put_params.put("Keyword",response.get("Keyword").toString().concat(finalRes));
                                            keywords_put_params.put("User","admin");
                                            JsonObjectRequest stringRequest1 = new JsonObjectRequest(url,new JSONObject(keywords_put_params),
                                                    new Response.Listener < JSONObject> () {
                                                        @Override
                                                        public void onResponse(JSONObject response) {

                                                        }},new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    Log.d("msg", "Error in get request");
                                                }
                                            });
                                            stringRequest1.setRetryPolicy(new DefaultRetryPolicy(
                                                    10000,
                                                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                                            queue.add(stringRequest1);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("msg", "Error in get request");
                            }
                        });
                        stringRequest1.setRetryPolicy(new DefaultRetryPolicy(
                                10000,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                        queue1.add(stringRequest1);
                    }
                });
                thread.start();
            }});

        // Call detectKeyPhrases API
//        System.out.println("Calling DetectKeyPhrases");
//        Thread thread = new Thread(new Runnable(){
//            @Override
//            public void run() {
//
//
//        DetectKeyPhrasesRequest detectKeyPhrasesRequest = new DetectKeyPhrasesRequest().withText("I donot have shortage of food and water in my place").withLanguageCode("en");
//        DetectKeyPhrasesResult detectKeyPhrasesResult = comprehendClient.detectKeyPhrases(detectKeyPhrasesRequest);
//      for(int i=0;i<detectKeyPhrasesResult.getKeyPhrases().size();i++)
//                {
//
//                        Log.d("masg","detect"+String.valueOf(detectKeyPhrasesResult.getKeyPhrases().get(i).getText()));
//
//                };
//        System.out.println("End of DetectKeyPhrases\n");
//            }
//        });
//        thread.start();
        btn_dash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://bing.com/covid/local/india"); // missing 'http://' will cause crashed


                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        MaterialButton btn_back=(MaterialButton) findViewById(R.id.btn_back);
        MaterialButton btn_news=(MaterialButton) findViewById(R.id.button_news);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MapsActivityCurrentPlace.class);

                startActivity(intent);

            }
        });
        String url= "https://2j19urchik.execute-api.us-east-2.amazonaws.com/testing/";


        RequestQueue[] queue= {
                Volley.newRequestQueue(getApplicationContext())
        };

        Map< String, String > params = new HashMap< String, String >();
        final JsonObjectRequest stringRequest = new JsonObjectRequest(url, new JSONObject(params),
                new Response.Listener < JSONObject > () {
                    @Override
                    public void onResponse(JSONObject response) {
Log.d("msg","response is"+response.toString());

                        try {
                            JSONArray arr=new JSONArray(response.get("body").toString());
                            for(int i=0;i<arr.length();i++)
                            {
                                if(i==3)
                                    break;
                                MaterialButton btn3=(MaterialButton) findViewById(R.id.btn_news1);
                                if(i==0) {
                                    btn3 = (MaterialButton) findViewById(R.id.btn_news1);
                                }
                                if(i==1) {
                                    btn3 = (MaterialButton) findViewById(R.id.btn_news2);
                                }
                                if(i==2) {
                                    btn3 = (MaterialButton) findViewById(R.id.btn_news3);
                                }
                                btn3.setText(arr.getJSONObject(i).get("heading").toString());
                                int finalI = i;
                                btn3.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Uri uri = null; // missing 'http://' will cause crashed
//
                                        try {
                                            uri = Uri.parse(arr.getJSONObject(finalI).get("link").toString());
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                        startActivity(intent);
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }}, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("msg", "news corona didn't work!"+error);
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue[0].add(stringRequest);
    }
}
