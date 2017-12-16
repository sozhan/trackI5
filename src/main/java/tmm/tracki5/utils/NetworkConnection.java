package tmm.tracki5.utils;

/**
 * Created by Arun on 19/02/16.
 */
import android.content.Context;
import android.net.ConnectivityManager;

public class NetworkConnection {
    private Context _context;

    public NetworkConnection(Context context){
        this._context = context;
    }

    public boolean isNetworkAvailable() {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
}
