package dongdang.homework.MultiChatClient.model.bl;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;


import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import dongdang.homework.MultiChatClient.model.dto.ChatBubbleDTO;
import dongdang.homework.MultiChatClient.model.dto.Message;
import dongdang.homework.MultiChatClient.model.dto.Status;
import dongdang.homework.MultiChatClient.model.dto.UserInfoDTO;

public class ClientService {
    private Socket clientSock;
    private boolean state;
    private BufferedReader br;
    private BufferedWriter bw;
    private UserInfoDTO user;
    HashMap<String, ChatBubbleDTO> typeMap;
    public ClientService() {
        state=false;
        typeMap = new HashMap<>();
        typeMap.put("broadcast",new ChatBubbleDTO(Gravity.LEFT,"broadcast", Color.rgb(179, 214, 218)));
        typeMap.put("unicast",new ChatBubbleDTO(Gravity.LEFT,"unicast",Color.rgb(249 , 223, 228)));
        typeMap.put("multicast",new ChatBubbleDTO(Gravity.LEFT,"multicast",Color.rgb(214,165,131)));
        typeMap.put("system",new ChatBubbleDTO(Gravity.CENTER,"system",Color.rgb(140,140,140)));
        typeMap.put("my",new ChatBubbleDTO(Gravity.RIGHT,"my",Color.rgb(180,162,242)));
    }

    public String read() throws IOException {
       return br.readLine();
    }

    public void write(String message) throws IOException {
        if(message==null) return;
        bw.write(message+"\n");
        bw.flush();
    }
    public void close() {
        try {
            if(bw!=null)  bw.close();
            if(br!=null)br.close();
            if(clientSock!=null)clientSock.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean connect(String ip, int port, String id, String name) {

        user = new UserInfoDTO(ip,port,id,name);

        Thread t = new Thread(){
            @Override
            public void run() {
                super.run();

                try {
                    clientSock = new Socket(user.getIpAdr(),user.getPort());
                    bw = new BufferedWriter(new OutputStreamWriter(clientSock.getOutputStream()));
                    br = new BufferedReader(new InputStreamReader(clientSock.getInputStream()));
                } catch (IOException e) {
                    e.printStackTrace();
                    state=false;
                }
                try {
                    String message = new Gson().toJson(user)+"\n";
                    write(message);
                    String readMsg = read();
                    System.out.println(readMsg);
                    if(new Gson().fromJson(readMsg, Status.class).getStatus()==200) {
                        state=true;
                    }else {
                        state=false;
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return state;
    }

    public Socket getClientSock() {
        return clientSock;
    }

    public BufferedReader getBr() {
        return br;
    }

    public BufferedWriter getBw() {
        return bw;
    }

    public UserInfoDTO getUser() {
        return user;
    }

    public HashMap<String, ChatBubbleDTO> getTypeMap() {
        return typeMap;
    }
    public void send(String content) {
        Message msg = new Message();
        msg.setType("broadcast");
        msg.setSender(user.getName());
        msg.setMessage(content);
        msg.setReceiver(null);
        final String m = new Gson().toJson(msg);
        System.out.println(m);
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    write(m);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        };
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void receive(ListView chatListView, ChatAdapter chatAdapter, ArrayList<ChatBubbleDTO> chatList, AppCompatActivity context) {
        new ClientReceiverThread(clientSock,chatListView,chatAdapter,chatList,context).start();
    }
}
