package nammapolice.theateam.com.ak.nammapolice.nammapolicerakshak;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by adarshasaraff on 04/10/15.
 */
public class VolleySingleton {
    private static VolleySingleton ourInstance;

    private static Context mContext;


    public static synchronized VolleySingleton getInstance(Context context) {
        mContext = context;
        if (ourInstance == null) {
            ourInstance = new VolleySingleton();
        }
        return ourInstance;
    }


    private RequestQueue mRequestQueue;


    private VolleySingleton() {
        mRequestQueue = getRequestQueue();
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext);
            mRequestQueue.start();
        }
        return mRequestQueue;
    }

    public <T> void addToRequest(com.android.volley.Request<T> request) {
        request.setRetryPolicy(new DefaultRetryPolicy(2000, 1, 1.0f));
        getRequestQueue().add(request);

    }
}
