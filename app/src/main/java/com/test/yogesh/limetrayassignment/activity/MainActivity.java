package com.test.yogesh.limetrayassignment.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources.Theme;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.test.yogesh.limetrayassignment.R;
import com.test.yogesh.limetrayassignment.adapter.TransactionRecyclerViewAdapter;
import com.test.yogesh.limetrayassignment.model.Expenses;
import com.test.yogesh.limetrayassignment.utils.ConnectionDetector;
import com.test.yogesh.limetrayassignment.utils.Constants;
import com.test.yogesh.limetrayassignment.volley.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "test";

    private String toolbarTitle;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout emptyView;
    private TextView statusMessageTextView;
    private RecyclerView recyclerView;
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private ProgressDialog progressDialog;
    private TransactionRecyclerViewAdapter transactionRecyclerViewAdapter;

    private List<Expenses> expensesList = new ArrayList<>();

    // flag for Internet connection status
    private Boolean isInternetPresent = false;

    // Connection detector class
    private ConnectionDetector connectionDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbarTitle = getResources().getString(R.string.app_name);
        getSupportActionBar().setTitle(toolbarTitle);

        emptyView = (LinearLayout) findViewById(R.id.emptyView);
        statusMessageTextView = (TextView) findViewById(R.id.emptyViewTextView);
        recyclerView = (RecyclerView) findViewById(R.id.itemsRecyclerView);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW);
        swipeRefreshLayout.setEnabled(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);

        connectionDetector = new ConnectionDetector(this);
        fetchtransaction();

        // Setup spinner
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(new MyAdapter(
                toolbar.getContext(),
                new String[]{
                        "Recharge",
                        "Taxi",
                }));

        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // When the given dropdown item is selected, show its contents in the
                // container view.
                switch (position) {

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchtransaction();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private static class MyAdapter extends ArrayAdapter<String> implements ThemedSpinnerAdapter {
        private final ThemedSpinnerAdapter.Helper mDropDownHelper;

        public MyAdapter(Context context, String[] objects) {
            super(context, android.R.layout.simple_list_item_1, objects);
            mDropDownHelper = new ThemedSpinnerAdapter.Helper(context);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            View view;

            if (convertView == null) {
                // Inflate the drop down using the helper's LayoutInflater
                LayoutInflater inflater = mDropDownHelper.getDropDownViewInflater();
                view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            } else {
                view = convertView;
            }

            TextView textView = (TextView) view.findViewById(android.R.id.text1);
            textView.setText(getItem(position));

            return view;
        }

        @Override
        public Theme getDropDownViewTheme() {
            return mDropDownHelper.getDropDownViewTheme();
        }

        @Override
        public void setDropDownViewTheme(Theme theme) {
            mDropDownHelper.setDropDownViewTheme(theme);
        }
    }

    private void getTransactions() {
        Log.v(TAG, "getTransaction is called");
        volleySingleton = VolleySingleton.getMyInstance();
        requestQueue = volleySingleton.getRequestQueue();
        showProgressDialog();

        StringRequest jsonObjectRequest = new StringRequest(Request.Method.GET, Constants.URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                if (response == null) {
                    emptyView.setVisibility(View.VISIBLE);
                    statusMessageTextView.setText("Not response from server");
                }
                expensesList = parseJson(response);
                transactionRecyclerViewAdapter = new TransactionRecyclerViewAdapter(MainActivity.this, expensesList);
                recyclerView.setAdapter(transactionRecyclerViewAdapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Log.v(TAG, "Response = " + "timeOut");
                } else if (error instanceof AuthFailureError) {
                    Log.v(TAG, "Response = " + "AuthFail");
                } else if (error instanceof ServerError) {
                    Log.v(TAG, "Response = " + "ServerError");
                } else if (error instanceof NetworkError) {
                    Log.v(TAG, "Response = " + "NetworkError");
                } else if (error instanceof ParseError) {
                    Log.v(TAG, "Response = " + "ParseError");
                }
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    public void showProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Getting transactions...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
    }

    private List<Expenses> parseJson(String json) {
        List<Expenses> list = new ArrayList<>();

        JSONObject mainObject = null;
        try {
            mainObject = new JSONObject(json);

            JSONArray jsonArray = mainObject.getJSONArray(Constants.EXPENSES);

            for (int i = 0; i < jsonArray.length(); i++) {
                Expenses expenses = new Expenses();
                // Creating JSONObject from JSONArray
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                expenses.setAmount(Integer.valueOf(jsonObject.getString(Constants.AMOUNT)));
                expenses.setCategory(jsonObject.getString(Constants.CATEGORIES));
                expenses.setDescription(jsonObject.getString(Constants.DESCRIPTION));
                expenses.setId(jsonObject.getString(Constants.ID));
                expenses.setState(jsonObject.getString(Constants.STATE));
                expenses.setTime(jsonObject.getString(Constants.TIME));
                list.add(expenses);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.v(TAG, "list  = " + String.valueOf(list));
        return list;
    }

    /**
     * fetches transaction after checking internet connection
     **/
    private void fetchtransaction() {
        isInternetPresent = connectionDetector.isConnectingToInternet();
        // check for Internet status
        if (isInternetPresent) {
            // Internet Connection is Present
            // make HTTP requests
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            getTransactions();

        } else {
            // Internet connection is not present
            // Ask user to connect to Internet
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            statusMessageTextView.setText("Not connected to internet");
        }
    }

}
