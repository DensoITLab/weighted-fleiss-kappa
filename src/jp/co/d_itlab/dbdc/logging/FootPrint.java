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

import java.io.File;
import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Utility class for foot prints.
 */
public final class FootPrint {

	/**
	 * System property name.
	 */
	public static final String SYSTEM_PROPERTY_LEVEL = "FootPrint.level";
	public static final String SYSTEM_PROPERTY_TARGETS = "FootPrint.targets";
	
	public static final String COMMA = ",";
	public static final String INFO_HEADER = "[INFO] ";
	public static final String WARN_HEADER = "[WARN] ";
	public static final String DEBUG_HEADER = "[DEBUG] ";

	
	// lock object for synchronizing
	public static volatile byte[] lock = new byte[0];
	
	// singleton
	private static FootPrint footPrint = null;
	
	private boolean isShowFootPrint = false;
	private FootPrintLevel level = null; 
	private List<Class<?>> targets = new ArrayList<Class<?>>();
	private PrintStream ps = System.out;
	
	private static Map<Object, Long> times = new HashMap<Object, Long>();
	
	/**
	 * Hides the constructor.
	 */
	private FootPrint()
	{
		setLevel();
		 setTargets();
	}
	
	private void setLevel()
	{
		String level = System.getProperty(SYSTEM_PROPERTY_LEVEL);
		if (level != null)
		{
			level = level.toUpperCase();
			if (level.equals(FootPrintLevel.INFO.toString()))
			{
				isShowFootPrint = true;
				this.level = FootPrintLevel.INFO;
			}
			if (level.equals(FootPrintLevel.DEBUG.toString()))
			{
				isShowFootPrint = true;
				this.level = FootPrintLevel.DEBUG;
			}
		}
	}
	
	private void setTargets()
	{
		String targets = System.getProperty(SYSTEM_PROPERTY_TARGETS);
		
		if (targets != null)
		{
			for (String target : targets.split(COMMA))
			{
				try
				{
					Class<?> c = Class.forName(target);
					this.targets.add(c);
				} catch (ClassNotFoundException e)
				{
					warn("Unknown target is Specified. [{0}]", target);
				}
			}
		}
	}
	
	/**
	 * Getter method for obtaining the singleton instance.
	 * @return
	 */
	public static FootPrint getFootPrint()
	{
		synchronized(lock)
		{
			if (footPrint == null)
			{
				footPrint = new FootPrint();
			}
			return footPrint;
		}
	}
	
	/**
	 * Sets a specific output stream for foot prints.
	 * @param ps - new output stream
	 * @return previous output stream.
	 */
	public static PrintStream setPrintStream(PrintStream ps)
	{
		synchronized(lock)
		{
			FootPrint fp = getFootPrint();
			PrintStream psOld= fp.ps;
			fp.ps = ps;
			return psOld;
		}
	}
	
	/**
	 * Returns true if the foot print system property is set.
	 * @return
	 */
	public static boolean isShowFootPrint()
	{
		FootPrint fp = getFootPrint();
		return fp.isShowFootPrint;
	}
	
	/**
	 * Sets the foot print flag.
	 * @param isShowFootPrint - flag
	 * @return previous setting
	 */
	public static boolean showFootPrint(boolean isShowFootPrint)
	{
		boolean previous = getFootPrint().isShowFootPrint;
		getFootPrint().isShowFootPrint = isShowFootPrint;
		return previous;
	}
	
	/**
	 * Sets the foot print flag and level.
	 * @param isShowFootPrint - flag.
	 * @param level - level.
	 * @return previous flag setting.
	 */
	public static boolean showFootPrint(boolean isShowFootPrint, FootPrintLevel level)
	{
		setLevel(level);
		return showFootPrint(isShowFootPrint);
	}
	
	public static void printCurrentDirectory()
	{
	    debug(new File(".").getAbsolutePath());
	}
	
	/**
	 * Foot print method to record a method is staring.
	 */
	public static void entering()
	{
		if (isShowFootPrint() && getLevel() == FootPrintLevel.DEBUG)
		{
			String time = DateTimeFormat.getCurrentTime();			
			StackTraceElement stack = StackTrace.getPreviousStack();
			println(time, stack, "Entering...");	
		}
	}
	
	/**
	 * Foot print method to record a method is ending.
	 */
	public static void exiting()
	{
		if (isShowFootPrint() && getLevel() == FootPrintLevel.DEBUG)
		{
			String time = DateTimeFormat.getCurrentTime();			
			StackTraceElement stack = StackTrace.getPreviousStack();
			println(time, stack, "Exiting...");	
		}
	}
	
	/**
	 * This is used for stopping at the case where a given condition is not satisfied.
	 * @param condition - condition to be satisfied.
	 * @param message
	 */
	public static boolean check(boolean condition, String message)
	{
		if (isShowFootPrint() && getLevel() == FootPrintLevel.DEBUG && !condition)
		{
			String time = DateTimeFormat.getCurrentTime();			
			StackTraceElement stack = StackTrace.getPreviousStack();
			println(time, stack, message);
			return false;
		}
		return true;
	}
	
	public static boolean isPrintDebug()
	{
		return isShowFootPrint() && FootPrintLevel.DEBUG.compareTo(getLevel()) >= 0; 
	}
	
	public static boolean isPrintInfo()
	{
		return isShowFootPrint() && FootPrintLevel.INFO.compareTo(getLevel()) >= 0; 
	}
	
	/**
	 * Shows foot print regardless of the message level settings.
	 * 
	 * @param message
	 */
	public static void show(String message)
	{
	    if (isPrintDebug())
        {
	        show(message, StackTrace.getPreviousStack(), true);
        }
	    else
	    {
	        show(message, null, false);
	    }
	}
	
	public static void show(String format, Object... params)
    {
        if (isPrintDebug())
        {
            show(MessageFormat.format(format, params), StackTrace.getPreviousStack(), true);
        }
        else
        {
            show(MessageFormat.format(format, params), null, false);
        }
    }
	
	public static void show(String message, StackTraceElement stack, boolean isShowStackTrance)
    {
	    if (isShowStackTrance)
	    {
            String time = DateTimeFormat.getCurrentTime();          
            println(time, stack, message);  
	    }
	    else
	    {
	        println(message);
	    }
    }
	
	/**
	 * Foot print method to record a message.
	 * 
	 * @param message - message for foot print
	 */
	public static void info(String message)
	{
		if (isPrintInfo())
		{
		    if (isPrintDebug())
		    {
		        String time = DateTimeFormat.getCurrentTime();          
	            StackTraceElement stack = StackTrace.getPreviousStack();
	            println(time, stack, INFO_HEADER + message);  
		    }
		    else
		    {
		        println(INFO_HEADER + message);	
		    }
		}
	}
	
	public static void info(Class<?> target, String message)
	{
		if (getFootPrint().targets.contains(target))
		{
			if (isPrintInfo())
			{
			    if (isPrintDebug())
	            {
	                String time = DateTimeFormat.getCurrentTime();          
	                StackTraceElement stack = StackTrace.getPreviousStack();
	                println(time, stack, INFO_HEADER + message);  
	            }
	            else
	            {
	                println(INFO_HEADER + message); 
	            }
			}
		}
	}
	
	/**
	 * Foot print method to record a message.
	 * @param format message format
	 * @param params parameters for the message
	 */
	public static void info(String format, Object... params)
    {
		if (isPrintInfo())
        {
		    String message = MessageFormat.format(format, params);
		    if (isPrintDebug())
            {
                String time = DateTimeFormat.getCurrentTime();          
                StackTraceElement stack = StackTrace.getPreviousStack();
                println(time, stack, INFO_HEADER + message);  
            }
            else
            {
                println(INFO_HEADER + message);  
            }
        }
    }
	
	public static void info(Class<?> target, String format, Object... params)
    {
		if (getFootPrint().targets.contains(target))
		{
			if (isPrintInfo())
	        {
			    String message = MessageFormat.format(format, params);
	            if (isPrintDebug())
	            {
	                String time = DateTimeFormat.getCurrentTime();          
	                StackTraceElement stack = StackTrace.getPreviousStack();
	                println(time, stack, INFO_HEADER + message);  
	            }
	            else
	            {
	                println(INFO_HEADER + message);  
	            }
	        }
		}
    }
	
	public static void warn(String message)
    {
        if (isPrintInfo())
        {
            String time = DateTimeFormat.getCurrentTime();          
            StackTraceElement stack = StackTrace.getPreviousStack();
            println(time, stack, WARN_HEADER + message);  
        }
    }
    
    public static void warn(Class<?> target, String message)
    {
        if (getFootPrint().targets.contains(target))
        {
            if (isPrintInfo())
            {
                String time = DateTimeFormat.getCurrentTime();          
                StackTraceElement stack = StackTrace.getPreviousStack();
                println(time, stack, WARN_HEADER + message);  
            }
        }
    }
    
    public static void warn(String format, Object... params)
    {
        if (isPrintInfo())
        {
            String time = DateTimeFormat.getCurrentTime();          
            StackTraceElement stack = StackTrace.getPreviousStack();
            println(time, stack, WARN_HEADER + MessageFormat.format(format, params));  
        }
    }
    
    public static void warn(Class<?> target, String format, Object... params)
    {
        if (getFootPrint().targets.contains(target))
        {
            if (isPrintInfo())
            {
                String time = DateTimeFormat.getCurrentTime();          
                StackTraceElement stack = StackTrace.getPreviousStack();
                println(time, stack, WARN_HEADER + MessageFormat.format(format, params));  
            }
        }
    }
	
	/**
	 * Foot print method to record a debugging message.
	 * @param message - message for foot print
	 */
	public static void debug(String message)
	{
		if (isPrintDebug())
		{
			String time = DateTimeFormat.getCurrentTime();			
			StackTraceElement stack = StackTrace.getPreviousStack();
			println(time, stack, DEBUG_HEADER + message);	
		}
	}
	
	public static void debug(Class<?> target, String message)
	{
		if (getFootPrint().targets.contains(target))
		{
			if (isPrintDebug())
			{
				String time = DateTimeFormat.getCurrentTime();			
				StackTraceElement stack = StackTrace.getPreviousStack();
				println(time, stack, DEBUG_HEADER +message);	
			}
		}
	}
	
	/**
	 * Foot print method to record a debugging message.
	 * @param format message format
	 * @param params parameters for the message
	 */
	public static void debug(String format, Object... params)
    {
        //if (isShowFootPrint() && FootPrintLevel.DEBUG.compareTo(getLevel()) >= 0)
		if (isPrintDebug())
        {
            String time = DateTimeFormat.getCurrentTime();          
            StackTraceElement stack = StackTrace.getPreviousStack();
            println(time, stack, DEBUG_HEADER +MessageFormat.format(format, params));  
        }
    }
	
	public static void debug(Class<?> target, String format, Object... params)
    {
		if (getFootPrint().targets.contains(target))
		{
			if (isPrintDebug())
	        {
	            String time = DateTimeFormat.getCurrentTime();          
	            StackTraceElement stack = StackTrace.getPreviousStack();
	            println(time, stack, DEBUG_HEADER +MessageFormat.format(format, params));  
	        }
		}
    }
	
	public static <T> void debug(Function<T, String> function, T param)
	{
	    if (isPrintDebug())
        {
            String time = DateTimeFormat.getCurrentTime();          
            StackTraceElement stack = StackTrace.getPreviousStack();
            println(time, stack, DEBUG_HEADER +function.apply(param));  
        }
	}
	
	/**
	 * Prints a foot print.
	 * @param time
	 * @param stack
	 * @param message
	 */
	public static void println(String time, StackTraceElement stack, String message)
	{
		String s = MessageFormat.format("{0} {1}\n{2}\n", time, StackTraceAdapter.getClassMethodName(stack), message);
		getFootPrint().ps.println(s);
	}
	
	public static void println(String message)
    {
        String s = MessageFormat.format("{0}\n", message);
        getFootPrint().ps.println(s);
    }
	
	/**
	 * Returns the foot print system property setting.
	 * @return foot print level
	 */
	public static FootPrintLevel getLevel()
	{
		return getFootPrint().level;
	}
	
	/**
	 * Sets the foot print level.
	 * @param level - level
	 */
	public static FootPrintLevel setLevel(FootPrintLevel level)
	{
		FootPrintLevel previous = getFootPrint().level;
		getFootPrint().level = level;
		return previous;
	}
	
	public static void setTime(Object key)
	{
		long time = System.nanoTime();
		if (key != null)
		{
			times.put(key, time);
		}
	}
	
	public static long getNanoTime(Object key)
	{
		long end = System.nanoTime();
		long start = end;
		if (times.containsKey(key))
		{
			start = times.get(key);
		}
		return end - start;
	}
	
	public static long getMilliTime(Object key)
	{
		long nanoTime = getNanoTime(key);
		return (long)((double)nanoTime / 1000000f);
	}
	
	public static void execTime(Object key)
	{
		if (isPrintInfo())
		{
			String time = DateTimeFormat.getCurrentTime();			
			StackTraceElement stack = StackTrace.getPreviousStack();
			String message = MessageFormat.format("Execution Time: [{0}] ms", getMilliTime(key));
			println(time, stack, message);	
		}
	}
}
