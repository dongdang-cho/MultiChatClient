package dongdang.homework.MultiChatClient.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import dongdang.homework.MultiChatClient.R;
import dongdang.homework.MultiChatClient.controller.Controller;
import dongdang.homework.MultiChatClient.util.MetaDataLoader;

public class MainActivity extends AppCompatActivity {
    private final int MY_PERMISSIONS_REQUEST_INTERNET=1001;

    EditText etIP,etPort,etID,etPW;
    Button btnConnect, btnJoin, btnExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //권한 체크
        permissionCheck();

        //메타 데이터 로딩
        MetaDataLoader.loading(getResources().getAssets());

        etIP = (EditText)findViewById(R.id.mainEtIP);
        etPort = (EditText)findViewById(R.id.mainEtPort);
        etID = (EditText)findViewById(R.id.mainEtID);
        etPW = (EditText)findViewById(R.id.mainEtPW);

        btnConnect = (Button)findViewById(R.id.mainBtnConnect);
        btnJoin = (Button)findViewById(R.id.mainBtnJoin);
        btnExit = (Button)findViewById(R.id.mainBtnExit);

        etIP.setText(Controller.SERVER_IP);
        etPort.setText(Controller.PORT+"");

        //이벤트
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = etID.getText().toString();
                String pw = etPW.getText().toString();
                if(id==null|| pw==null) return;
                if(id.equals("")||pw.equals("")) return;
                Controller.serverConncect(id,pw,MainActivity.this);

            }
        });

        btnJoin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, JoinActivity.class);
                startActivity(intent);
            }
        });

        etPW.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch(actionId) {
                    case EditorInfo.IME_ACTION_GO:
                        btnConnect.performClick();
                        break;
                }
                return true;

            }
        });
        etPW.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    //SEND
                    btnConnect.performClick();
                    return true;
                }
                return false;
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
