package com.rock.alarmclock.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;

import com.rock.alarmclock.data.model.Category;

import com.rock.alarmclock.main.R;
import android.app.Activity;
import android.util.Log;


public class ChannelConfig
{
    private static ChannelConfig instance = null;
    private static Activity activity;
    private static String xml = "";
    
    private static Map<String, Map<String, String>> channels = null; // Map<category, List<channel, playurl>>
    private static List<Category> catlist = null;
    private ChannelConfig()
    {
    	channels = new HashMap<String, Map<String, String>>();
    	catlist = new ArrayList<Category>();
    }

    public static synchronized ChannelConfig getInstance(Activity ctx)
    {
        if (instance == null)
        {
            instance = new ChannelConfig();
            activity = ctx;
        }
        return instance;
    }
    
    public static Map<String, String> getChannels(String key) {
    	if(channels != null) {
    		return channels.get(key);
    	} else return null;
    }
    
    public synchronized Map<String, Map<String, String>> getChannelMap() {
    	return channels;
    }
    
    private void initCatList() {
    	if(catlist != null && channels != null) {
    		
    	}
    	
    }
    
    public synchronized void init()
    {
		InputStream is = activity.getResources().openRawResource(R.raw.channels);
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int len = 0;
			byte[] b = new byte[10240];
			while ((len = is.read(b, 0, b.length)) != -1) {
				baos.write(b, 0, len);
			}
			byte[] buffer = baos.toByteArray();
			xml = new String(buffer, "utf8");
			Document doc = XMLUtils.string2Doc(xml);
	        Element docRoot = doc.getRootElement();
	        
	        List<Element> categories = docRoot.getChildren("category");
	        Iterator<Element> catIter = categories.iterator();
	        
	        while(catIter.hasNext()) {
	        	
	        	Element catNode = catIter.next();

	            String catName  = catNode.getAttributeValue("name").trim();
	        	Map<String, String> channellist;
	        	if(channels.containsKey(catName)) {
	        		channellist = channels.get(catName);
	        	} else {
	        		channellist = new HashMap<String, String>();
	        	}
	        	
	            List<Element> channelsElements = catNode.getChildren("channel");

		        Iterator<Element> channelIter = channelsElements.iterator();
		        
		        while (channelIter.hasNext()) {
		        	
		        	Element channelNode = channelIter.next();
		        	Element nameNode = channelNode.getChild("name");
		        	
		        	Element playUrl = channelNode.getChild("playurl");

		        	if(nameNode != null && playUrl != null) {

		        		channellist.put(nameNode.getText().trim(), playUrl.getText().trim());
		        	}
		        }
		        if(channellist.size() != 0) {
		        	channels.put(catName, channellist);
		        }
	            
	        }
		} catch (Exception e) {
			Log.e("init channel list failed", e.getMessage());
		}
    }
}
