package dongdang.homework.MultiChatClient.view;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import dongdang.homework.MultiChatClient.R;
import dongdang.homework.MultiChatClient.controller.Controller;

public class JoinActivity extends AppCompatActivity {
    EditText etID, etPW, etName, etConfirmPW;
    Button btnJoin, btnBack;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        etID = (EditText)findViewById(R.id.joinEtID);
        etPW = (EditText)findViewById(R.id.joinEtPW);
        etConfirmPW = (EditText)findViewById(R.id.joinEtPWConfirm);
        etName = (EditText)findViewById(R.id.joinEtName);
        btnJoin = (Button)findViewById(R.id.joinBtnJoin);
        btnBack = (Button)findViewById(R.id.joinBtnBack);

        btnJoin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String id = etID.getText().toString();
                String pw = etPW.getText().toString();
                String confirmPW = etConfirmPW.getText().toString();
                String name = etName.getText().toString();
                if(!effectiveness(id,pw,confirmPW,name)) return;

                //클라이언트로 보내기.
                Controller.join(id,name,pw,JoinActivity.this);
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        etName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch(actionId) {
                    case EditorInfo.IME_ACTION_GO:
                        btnJoin.performClick();
                        break;
                }
                return true;
            }
        });
        etName.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    //SEND
                    btnJoin.performClick();
                    return true;
                }
                return false;
            }
        });
    }

    //유효성 검사
    public boolean effectiveness(String id, String pw, String confirmPW, String name) {
        boolean flag = true;
        if (id.equals("") || pw.equals("") || confirmPW.equals("") || name.equals("")) {
            Toast.makeText(JoinActivity.this, "공백이 존재합니다.", Toast.LENGTH_SHORT).show();
            flag=false;
        }
        if (pw.length() < 6) {
            Toast.makeText(JoinActivity.this, "비밀번호는 6자리 이상이어야 합니다.", Toast.LENGTH_SHORT).show();
            flag=false;
        }
        if (!pw.equals(confirmPW)) {
            Toast.makeText(JoinActivity.this, "입력하신 비밀번호가 서로 다릅니다.", Toast.LENGTH_SHORT).show();
            flag=false;
        }
        return flag;
    }
}
