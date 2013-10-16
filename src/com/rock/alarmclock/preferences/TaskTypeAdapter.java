package com.rock.alarmclock.preferences;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class TaskTypeAdapter extends ArrayAdapter<String> {

	public TaskTypeAdapter(Context context, int textViewResourceId, List<String> objects) {  
        super(context, textViewResourceId, objects); 
    }  
      
    @Override  
    public View getView(int position, View convertView, ViewGroup parent){  
//        User user = getItem(position);  
//        LinearLayout userListItem = new LinearLayout(getContext());  
//        String inflater = Context.LAYOUT_INFLATER_SERVICE;   
//        LayoutInflater vi = (LayoutInflater)getContext().getSystemService(inflater);   
//        vi.inflate(resourceId, userListItem, true);  
//        TextView tvUsername = (TextView)userListItem.findViewById(R.id.tv_user_list_username);  
//        TextView tvAskedNum = (TextView)userListItem.findViewById(R.id.tv_user_list_askednum);  
//        TextView tvLastMsg = (TextView)userListItem.findViewById(R.id.tv_user_list_lastmsg);  
//        tvUsername.setText(user.getUsername());  
//        tvAskedNum.setText(String.valueOf(user.getAskedNum()));  
//        tvLastMsg.setText(user.getLastMsg());  
//        return userListItem;  
    	return convertView;
    }  

}
