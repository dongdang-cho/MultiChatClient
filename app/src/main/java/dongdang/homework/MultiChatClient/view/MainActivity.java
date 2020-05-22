package dongdang.homework.MultiChatClient.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import dongdang.homework.MultiChatClient.R;
import dongdang.homework.MultiChatClient.controller.Controller;

public class MainActivity extends AppCompatActivity {
    private final int MY_PERMISSIONS_REQUEST_INTERNET=1001;

    EditText etIP,etPort,etID,etName;
    Button btnConnect, btnExit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        permissionCheck();

        etIP = (EditText)findViewById(R.id.mainEtIP);
        etPort = (EditText)findViewById(R.id.mainEtPort);
        etID = (EditText)findViewById(R.id.mainEtID);
        etName = (EditText)findViewById(R.id.mainEtName);

        btnConnect = (Button)findViewById(R.id.mainBtnConnect);
        btnExit = (Button)findViewById(R.id.mainBtnExit);

        //이벤트
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ip = etIP.getText().toString();
                int port = Integer.parseInt(etPort.getText().toString());
                String id = etPort.getText().toString();
                String name = etName.getText().toString();
                if(ip==null || id==null|| name==null) return;
                if(ip.equals("")||id.equals("")||name.equals("")) return;

                Controller.serverConncect(ip,port,id,name,MainActivity.this);

            }
        });

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //권한 체크
    public void permissionCheck() {
        String[] permissions = new String[]{Manifest.permission.INTERNET};
        List<String> listPermisssionNeeded = new ArrayList<>();

        for(String permission : permissions) {
            if(ContextCompat.checkSelfPermission(this,permission)==PackageManager.PERMISSION_DENIED) {
                listPermisssionNeeded.add(permission);
            }
        }
        if(!listPermisssionNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,listPermisssionNeeded.toArray(new String[listPermisssionNeeded.size()]),MY_PERMISSIONS_REQUEST_INTERNET);
        }
        Toast.makeText(this,"통과",Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_INTERNET: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this,"승인이 허가되어 있습니다.",Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(this,"아직 승인받지 않았습니다.",Toast.LENGTH_LONG).show();
                }
                return;
            }

        }
    }
}
