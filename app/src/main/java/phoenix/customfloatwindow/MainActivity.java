package phoenix.customfloatwindow;

import android.content.ComponentName;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import phoenix.customfloatwindow.service.FloatingWindowService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initService();
    }

    private void initService() {
        Intent intent = new Intent();
        ComponentName cn = new ComponentName(this, FloatingWindowService.class);
        intent.setComponent(cn);
        this.startService(intent);
    }
}
