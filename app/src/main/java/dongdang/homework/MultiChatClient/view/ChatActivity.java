package dongdang.homework.MultiChatClient.view;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import dongdang.homework.MultiChatClient.R;
import dongdang.homework.MultiChatClient.controller.Controller;
import dongdang.homework.MultiChatClient.model.bl.ChatAdapter;
import dongdang.homework.MultiChatClient.model.dto.ChatBubbleDTO;

public class ChatActivity extends AppCompatActivity {
    ListView lvChat;
    EditText etMessage;
    Button btnSend;
    ArrayList<ChatBubbleDTO> bubbleDTOList;
    ChatAdapter chatAdapter;
    ImageButton btnBack, btnMenu;

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

        bubbleDTOList = new ArrayList<ChatBubbleDTO>();
        chatAdapter = new ChatAdapter(this,bubbleDTOList);
        lvChat.setAdapter(chatAdapter);

        Controller.getClientService().receive(lvChat,chatAdapter,bubbleDTOList,ChatActivity.this);

        etMessage.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    //SEND
                    if(etMessage.getText().toString().equals("")) return true;
                    Controller.getClientService().send(etMessage.getText().toString());
                    etMessage.setText("");
                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(etMessage.getWindowToken(), 0);

                    return true;
                }
                return false;
            }
        });
        btnSend.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //SEND
                if(etMessage.getText().toString().equals("")) return;
                Controller.getClientService().send(etMessage.getText().toString());
                etMessage.setText("");
                InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(etMessage.getWindowToken(), 0);
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

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
    }

}
