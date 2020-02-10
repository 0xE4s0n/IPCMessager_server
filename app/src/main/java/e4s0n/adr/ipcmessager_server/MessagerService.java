package e4s0n.adr.ipcmessager_server;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import androidx.annotation.NonNull;

public class MessagerService extends Service {
    private final static int MES_FROM_CLIENT = 0;
    private static final int MES_FROM_SERVER = 1;
    private Callback callback;
    private boolean firstconnect = true;
    private Messenger client;
    private final Messenger messenger = new Messenger(new MessengerHandler());
    private static MessagerService instance = null;

    public static MessagerService getinstance()
    {
        return instance;
    }

    public MessagerService(){
        instance = this;
    }

    private class MessengerHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case MES_FROM_CLIENT:
                    callback.showMessage(msg.getData().getString("service"));
                    if (firstconnect) {
                        client = msg.replyTo;
                        Message replyMessage = Message.obtain(null, MES_FROM_SERVER);
                        Bundle bundle = new Bundle();
                        bundle.putString("client", "connected to server!");
                        replyMessage.setData(bundle);
                        try {
                            client.send(replyMessage);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        firstconnect = false;
                    }
                default:
                    super.handleMessage(msg);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }

    interface Callback {
        void showMessage(String mes);
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    protected void sendMessage(String mes) {
        Message replyMessage = Message.obtain(null, MES_FROM_SERVER);
        Bundle bundle = new Bundle();
        bundle.putString("client", mes);
        replyMessage.setData(bundle);
        try {
            client.send(replyMessage);
            callback.showMessage(bundle.getString("client"));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
