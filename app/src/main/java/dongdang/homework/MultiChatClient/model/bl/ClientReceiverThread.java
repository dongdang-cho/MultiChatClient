package dongdang.homework.MultiChatClient.model.bl;


import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dongdang.homework.MultiChatClient.R;
import dongdang.homework.MultiChatClient.controller.Controller;
import dongdang.homework.MultiChatClient.model.dto.ChatBubbleDTO;
import dongdang.homework.MultiChatClient.model.dto.Message;
import dongdang.homework.MultiChatClient.model.dto.UserInfoDTO;

public class ClientReceiverThread extends Thread {
    private Socket socket;
    private ListView chatListView, navListView;
    private ChatAdapter chatAdapter;
    private NavAdapter navAdapter;
    private ArrayList<ChatBubbleDTO> chatList;
    private AppCompatActivity context;

    public ClientReceiverThread() {
    }

    public ClientReceiverThread(Socket socket, ListView chatListView, ChatAdapter chatAdapter,ArrayList<ChatBubbleDTO> chatList,ListView navListView, AppCompatActivity context) {
        this.socket = socket;
        this.chatListView = chatListView;
        this.chatAdapter = chatAdapter;
        this.chatList = chatList;
        this.context = context;
        this.navListView = navListView;

    }

    public void run() {
        try {

            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //수신
            while(true) {
                String message= br.readLine();
                if(message==null) return;
                System.out.println("수신 : "+message);
                final Message msg = new Gson().fromJson(message,Message.class);

                context.runOnUiThread(new Runnable() {
                      @Override
                      public void run() {
                          filtering(msg);
                      }
                  });
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        addChatList(msg);
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Message filtering(Message m) {
        String type = m.getType();
        if(type.equals("system-visit")) {
            m.setType("system");
            ClientService cs = Controller.getClientService();

            if(navAdapter==null) {
                cs.setVisitors(m.getVisitor());
                this.navAdapter = new NavAdapter(context, cs.getVisitors());
                navListView.setAdapter(navAdapter);
            }else {
                cs.getVisitors().clear();
                cs.getVisitors().addAll(m.getVisitor());
            }
            navAdapter.notifyDataSetChanged();
        }else if(type.equals("system-exit")){
            m.setType("system");
            if(navAdapter!=null) {
                ClientService cs = Controller.getClientService();
                for(int i=0; i<cs.getVisitors().size(); i++) {
                    String exit = cs.getVisitors().get(i).getId();
                    if(exit.equals(m.getExit()))  {
                        System.out.println(exit+":"+m.getExit());
                        cs.getVisitors().remove(i);
                    }
                }
                navAdapter.notifyDataSetChanged();

            }

        }
        return m;
    }
    //말풍선 추가
    public void addChatList(Message msg) {
        try {
            ChatBubbleDTO chatBubble = null;
            if(msg.getSender().equals(Controller.getClientService().getUser().getName())) {
                chatBubble = Controller.getClientService().getTypeMap().get("my").clone();

            }else {
               chatBubble = Controller.getClientService().getTypeMap().get(msg.getType()).clone();
            }

            if(msg.getType().equals("multicast") || msg.getType().equals("unicast")) {
                //자신이 보낸 데이터일 때

                if(msg.getSender().equals(Controller.getClientService().getUser().getName())) {
                    String id = Controller.getClientService().getUser().getId();
                    for(int i=0; i<msg.getReceiver().size();i++) {
                        String temp = msg.getReceiver().get(i);

                        if(temp.equals(id)) msg.getReceiver().remove(i);
                    }
                    System.out.println("자신--------------------"+msg);
                }else {
                //자신이 보낸 데이터가 아닐 때
                    String id = null;
                    for(UserInfoDTO dto : Controller.getClientService().getVisitors()) {
                        if(dto.getName().equals(msg.getSender())) {
                           id = dto.getId();
                        }

                    }
                    for(int i=0; i<msg.getReceiver().size();i++) {
                        String temp = msg.getReceiver().get(i);
                        if(temp.equals(id)) msg.getReceiver().remove(i);
                    }
                }
                chatBubble.setSender(msg.getSender()+"→"+msg.getReceiver());
            }else {
                chatBubble.setSender(msg.getSender());
            }

            chatBubble.setContent(msg.getMessage());
            chatList.add(chatBubble);
            chatAdapter.notifyDataSetChanged();
            chatListView.setSelection(chatAdapter.getCount() - 1);

        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

    }
}
