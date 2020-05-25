package dongdang.homework.MultiChatClient.model.bl;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import dongdang.homework.MultiChatClient.R;
import dongdang.homework.MultiChatClient.controller.Controller;
import dongdang.homework.MultiChatClient.model.dto.ChatBubbleDTO;
import dongdang.homework.MultiChatClient.model.dto.UserInfoDTO;

//채팅 리스트뷰 어댑터
public class ChatAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<ChatBubbleDTO> items;

    public ChatAdapter(Context context, ArrayList<ChatBubbleDTO> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).
                    inflate(R.layout.item_bubble, parent, false);
        }
        LinearLayout bubbleLayout = convertView.findViewById(R.id.bubbleLayout);
        LinearLayout cardInLayout = convertView.findViewById(R.id.cardInLayout);
        TextView bubbleTVUser = convertView.findViewById(R.id.bubbleName);
        TextView bubbleContent = convertView.findViewById(R.id.bubbleContent);

        ChatBubbleDTO item = items.get(position);
        UserInfoDTO myUser = Controller.getClientService().getUser();

        bubbleLayout.setGravity(item.getAlign());
        cardInLayout.setGravity(item.getAlign());

        bubbleTVUser.setText(item.getSender());
        bubbleContent.setText(item.getContent());
        GradientDrawable bgShape = (GradientDrawable)bubbleContent.getBackground();
        bgShape.setColor(item.getBackgroundColor());
        return convertView;
    }
}
