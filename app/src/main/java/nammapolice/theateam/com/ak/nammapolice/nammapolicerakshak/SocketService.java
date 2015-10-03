package nammapolice.theateam.com.ak.nammapolice.nammapolicerakshak;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by adarshasaraff on 04/10/15.
 */
public class SocketService extends Service implements Emitter.Listener {

    public static final String BROADCAST_ACTION = "com.nammapolice.socket";

    private static final String EVENT = "";

    private SocketBinder binder = new SocketBinder();

    private Socket mSocket;
    private Intent intent;

    public SocketService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    class SocketBinder extends Binder {
        public SocketService getService() {
            return SocketService.this;
        }
    }

    @Override
    public void onCreate() {
        intent = new Intent(BROADCAST_ACTION);
        try {
            mSocket = IO.socket(NammaPoliceRakshak.SERVER_URL + "");
            mSocket.connect();
        } catch (Exception ex) {
        }
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        try {
            disConnect();
            mSocket.disconnect();
        } catch (Exception ex) {
        }
        super.onDestroy();
    }

    @Override
    public void call(Object... args) {

    }

    public void connect(String id) {
        try {
            if (mSocket.hasListeners(EVENT)) {
                disConnect();
            }
            mSocket.emit("", id);
            mSocket.on(EVENT, this);
        } catch (Exception ex) {
        }
    }

    public void disConnect() {
        try {
            mSocket.off(EVENT);
        } catch (Exception ex) {
        }
    }
}
