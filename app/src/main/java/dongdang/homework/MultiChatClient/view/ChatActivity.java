package dongdang.homework.MultiChatClient.view;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dongdang.homework.MultiChatClient.R;
import dongdang.homework.MultiChatClient.controller.Controller;
import dongdang.homework.MultiChatClient.model.bl.ChatAdapter;
import dongdang.homework.MultiChatClient.model.bl.ClientService;
import dongdang.homework.MultiChatClient.model.dto.ChatBubbleDTO;

public class ChatActivity extends AppCompatActivity {
    ListView lvChat, lvNvUser;
    EditText etMessage;
    Button btnSend;
    ArrayList<ChatBubbleDTO> bubbleDTOList;
    ChatAdapter chatAdapter;
    ImageButton btnBack, btnMenu;
    TextView tvNvMyUser;
    DrawerLayout drawer;
    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        lvChat = (ListView)findViewById(R.id.chatListView);
        etMessage = (EditText)findViewById(R.id.chatEtMessage);
        btnSend = (Button)findViewById(R.id.chatBtnSend);
        btnBack = (ImageButton)findViewById(R.id.chatBtnBack);
        btnMenu = (ImageButton)findViewById(R.id.chatBtnMenu);
        lvNvUser = (ListView)findViewById(R.id.navLvUser);
        tvNvMyUser = (TextView)findViewById(R.id.navTvUser);

        bubbleDTOList = new ArrayList<ChatBubbleDTO>();
        chatAdapter = new ChatAdapter(this,bubbleDTOList);

        lvChat.setAdapter(chatAdapter);
        drawer= (DrawerLayout)findViewById(R.id.chatDrawerLayout);

        tvNvMyUser.setText(Controller.getClientService().getUser().getName()+"님");
        //수신 스레드 시작.
        Controller.getClientService().receive(lvChat,chatAdapter,bubbleDTOList,lvNvUser,ChatActivity.this);

        etMessage.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    //SEND
                    btnSend.performClick();
                    return true;
                }
                return false;
            }
        });
        etMessage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch(actionId) {
                    case EditorInfo.IME_ACTION_GO:
                        btnSend.performClick();
                        break;
                }
                    return true;

            }
        });
        btnSend.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //SEND
                if(etMessage.getText().toString().equals("")) return;
                Controller.getClientService().send(etMessage.getText().toString());
                etMessage.setText("");
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(Gravity.RIGHT);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(etMessage.getWindowToken(), 0);

            }
        });

        lvNvUser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String userID = Controller.getClientService().getVisitors().get(position).getId();
                etMessage.append("@"+userID+" ");

                if (drawer.isDrawerOpen(GravityCompat.END)) {
                    drawer.closeDrawer(GravityCompat.END);
                }

            }
        });

    }

    //2번 눌러야 뒤로가기.
    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime)
        {
            Controller.disconnect();
            super.onBackPressed();
        }
        else
        {
            backPressedTime = tempTime;
            Toast.makeText(getApplicationContext(), "한번 더 누르시면 채팅이 종료됩니다.", Toast.LENGTH_SHORT).show();
        }


        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        }
    }

}
