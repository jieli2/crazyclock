package com.rock.alarmclock.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

public class XMLUtils
{
	public static Document string2Doc(String xml)
	{
		Document doc = null;
		InputStream is = null;
		SAXBuilder sb = new SAXBuilder();
		try
		{
			is = new ByteArrayInputStream(xml.getBytes());
			doc = sb.build(is);
		}
		catch (OutOfMemoryError e)
		{
			System.gc();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			// if (is != null)
			// {
			// try
			// {
			// is.close();
			// }
			// catch (Exception e)
			// {
			// e.printStackTrace();
			// }
			// }
		}

		return doc;
	}

	public static String xml2String(Document doc)
	{
		return dom2String(doc);
	}

	public static String xml2String(Element element)
	{
		return dom2String(element);
	}

	private static String dom2String(Object dom)
	{
		XMLOutputter xo = new XMLOutputter();
		OutputStream os = new ByteArrayOutputStream();
		try
		{
			if (dom instanceof Document)
			{
				xo.output((Document) dom, os);
			}
			else if (dom instanceof Element)
			{
				xo.output((Element) dom, os);
			}
		}
		catch (IOException e)
		{
			return null;
		}
		return os.toString();
	}
}
