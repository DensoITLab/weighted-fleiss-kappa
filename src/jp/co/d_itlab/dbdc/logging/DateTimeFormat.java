///////////////////////////////////////////////////////////////////////////////////////////////////////
/// Inter-Annotator Agreement
/// Copyright (c) 2021 DENSO IT LABORATORY, INC. All rights reserved.
///
/// Unless required by applicable law or agreed to in writing, 
/// software distributed under the License is distributed on an "AS IS" BASIS,
/// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
///////////////////////////////////////////////////////////////////////////////////////////////////////

/// History:
/// [000] 2021/10/14, Hiroshi Tsukahara, Created.
///
package jp.co.d_itlab.dbdc.logging;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Date formatter 
 */
public final class DateTimeFormat {
    
    private static Object sycObj = new Object();
    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
    private static SimpleDateFormat format6 = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
	
	public static String getCurrentTime()
	{
		return format(new Date());
	}
	
	/**
	 * Format a Date instance in "yyyy/MM/dd hh:mm:ss.SSS" form.
	 */
	public static String format(Date date)
	{
		synchronized (sycObj)
        {
		    return format.format(date);
        }	
	}
	
	/**
	 * formats a Calendar instance in "yyyy/MM/dd hh:mm:ss.SSS" form.
	 */
	public static String format(Calendar calendar)
	{
	    synchronized (sycObj)
        {
	        return format(calendar.getTime());
        }
	}
	
	public static Date parseDDMMYYYYHHMMSS(String date)
    {
	    synchronized (sycObj)
        {
	        try
            {
                return format6.parse(date);
            }
            catch (Exception e)
            {
                // TODO
                return new Date();
            }
        }
    }
	
	
}
