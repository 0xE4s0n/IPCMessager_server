package e4s0n.adr.ipcmessager_server;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,MessagerService.Callback{

    private Button bu;
    private EditText ed;
    private TextView tv;
    private StringBuilder sb;
    private MessagerService messagerService = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bu = findViewById(R.id.button);
        bu.setEnabled(false);
        ed = findViewById(R.id.editText);
        tv = findViewById(R.id.textView);
        bu.setOnClickListener(this);
        sb = new StringBuilder();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (messagerService == null)
                {
                    messagerService = MessagerService.getinstance();
                }
                messagerService.setCallback(MainActivity.this);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        bu.setEnabled(true);
                    }
                });
            }
        }).start();
    }

    @Override
    public void onClick(View view) {
        if(messagerService == null)
        {
            Toast.makeText(getApplicationContext(),"no client connected!",Toast.LENGTH_SHORT).show();
        }else {
            String msg = ed.getText().toString();
            ed.setText("");
            messagerService.sendMessage(msg);
        }
    }

    @Override
    public void showMessage(String mes) {
        sb.append(mes+"\n");
        tv.setText(sb.toString());
    }
}

