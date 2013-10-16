package com.rock.alarmclock.preferences;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.rock.alarmclock.main.R;

import com.rock.alarmclock.utils.ChannelConfig;


import android.app.Activity;
import android.content.Context;
import android.graphics.ColorMatrixColorFilter;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;



public class LazyAdapter extends BaseAdapter {
    
    private Activity activity;
    //private ArrayList<HashMap<String, String>> data;
    private static Map<String, Map<String, String>> channels = null;
 
    public LazyAdapter(Activity a) {
        activity = a;
        if(channels == null) {
        	channels = ChannelConfig.getInstance(a).getChannelMap();
        }
        
	}

	public int getCount() {
		if (channels != null) {
			return channels.size();
		} else {
			return 0;
		}
	}

    public Object getItem(int position) {
    	if(channels == null) return null;
    	Set<String> keys = channels.keySet();
    	Iterator iter = keys.iterator();
    	int i = 0;
    	while(iter.hasNext() && i < position) {
    		i++;
    		iter.next();
    	}
    	if(iter.hasNext()) return iter.next();
    	return null;
    }

    public long getItemId(int position) {
        return position;
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = LayoutInflater.from(activity).inflate(R.layout.list_row, null);

        TextView title = (TextView)vi.findViewById(R.id.category); 
        title.setText((String)getItem(position));
        
        return vi;
    }
}