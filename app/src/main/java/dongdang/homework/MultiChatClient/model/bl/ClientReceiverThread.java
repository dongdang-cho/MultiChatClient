package dongdang.homework.MultiChatClient.model.bl;


import android.content.Context;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import dongdang.homework.MultiChatClient.controller.Controller;
import dongdang.homework.MultiChatClient.model.dto.ChatBubbleDTO;
import dongdang.homework.MultiChatClient.model.dto.Message;

public class ClientReceiverThread extends Thread {
    private Socket socket;
    private ListView chatListView;
    private ChatAdapter chatAdapter;
    private ArrayList<ChatBubbleDTO> chatList;
    private AppCompatActivity context;

    public ClientReceiverThread() {
    }

    public ClientReceiverThread(Socket socket, ListView chatListView, ChatAdapter chatAdapter,ArrayList<ChatBubbleDTO> chatList, AppCompatActivity context) {
        this.socket = socket;
        this.chatListView = chatListView;
        this.chatAdapter = chatAdapter;
        this.chatList = chatList;
        this.context = context;
    }

    public void run() {
        try {

            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //수신
            while(true) {
                String message= br.readLine();
                System.out.println("수신 : "+message);
                final Message msg = new Gson().fromJson(message,Message.class);
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

    //말풍선 추가
    void addChatList(Message msg) {
        try {
            ChatBubbleDTO chatBubble = null;
            if(msg.getSender().equals(Controller.getClientService().getUser().getName())) {
                chatBubble = Controller.getClientService().getTypeMap().get("my").clone();
            }else {
               chatBubble = Controller.getClientService().getTypeMap().get(msg.getType()).clone();
            }

            chatBubble.setSender(msg.getSender());
            chatBubble.setContent(msg.getMessage());
            chatList.add(chatBubble);
            chatAdapter.notifyDataSetChanged();
            chatListView.setSelection(chatAdapter.getCount() - 1);

        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

    }
}
