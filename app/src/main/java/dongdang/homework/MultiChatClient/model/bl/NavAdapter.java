package dongdang.homework.MultiChatClient.model.bl;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;

import android.widget.TextView;

import java.util.List;

import dongdang.homework.MultiChatClient.R;
import dongdang.homework.MultiChatClient.model.dto.UserInfoDTO;

public class NavAdapter extends BaseAdapter {
    private Context context;
    private List<UserInfoDTO> items;

    public NavAdapter(Context context, List<UserInfoDTO> items) {
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
                    inflate(R.layout.item_nav, parent, false);
        }
        TextView navName =(TextView)convertView.findViewById(R.id.navName);
        TextView navID = (TextView)convertView.findViewById(R.id.navID);
        UserInfoDTO user = items.get(position);
        navName.setText(user.getName());
        navID.setText(user.getId());
        return convertView;
    }
}
