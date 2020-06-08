package dongdang.homework.MultiChatClient.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import java.io.File;
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
    Button btnSend, btnPlus;
    ArrayList<ChatBubbleDTO> bubbleDTOList;
    ChatAdapter chatAdapter;
    ImageButton btnBack, btnMenu;
    TextView tvNvMyUser;
    DrawerLayout drawer;
    private final long FINISH_INTERVAL_TIME = 2000;
    private final int GALLERY_CODE = 101;
    private long backPressedTime = 0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        lvChat = (ListView)findViewById(R.id.chatListView);
        etMessage = (EditText)findViewById(R.id.chatEtMessage);
        btnSend = (Button)findViewById(R.id.chatBtnSend);
        btnPlus = (Button)findViewById(R.id.chatBtnpPlus);
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
        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,GALLERY_CODE);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_CODE) {
            if(resultCode == RESULT_OK) {
                try {
                    View dialog = (View)View.inflate(ChatActivity.this, R.layout.dialog_send_image,null);
                    ImageView iv = dialog.findViewById(R.id.dialogIv);
                    final Uri imageUri = data.getData();
                    iv.setImageURI(imageUri);
                    final AlertDialog.Builder dlg= new AlertDialog.Builder(ChatActivity.this);
                    final AlertDialog ad = dlg.create();

                    dlg.setTitle("전송할 이미지");
                    dlg.setView(dialog);
                    dlg.setPositiveButton("전송", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            File file = new File(imageUri.getPath());

                            Toast.makeText(ChatActivity.this,"전송되었습니다.",Toast.LENGTH_SHORT).show();

                        }
                    });
                    dlg.setNegativeButton("나가기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                           ad.dismiss();
                        }
                    });
                    dlg.show();
                }catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
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
