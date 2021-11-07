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

/**
 * Utility class for obtaining stack trace information.
 */
public final class StackTrace {
	
	/**
	 * Returns the stack information where this method is called.
	 * @return stack trace element.
	 */
	public static StackTraceElement getCurrentStack()
	{
		Throwable t = new Throwable();
		StackTraceElement[] ste = t.getStackTrace();
		if (ste.length > 1)
		{
			return ste[1];
		}
		else
		{
			return ste[0];
		}
	}
	
	/**
	 * Returns the stack trace information where calling the method that calling this method.
	 * @return stack trace element.
	 */
	public static StackTraceElement getPreviousStack()
	{
		Throwable t = new Throwable();
		StackTraceElement[] ste = t.getStackTrace();
		if (ste.length > 2)
		{
			return ste[2];
		}
		else
		{
			return ste[0];
		}
	}
	
	/**
	 * Returns the stack trace information where this method is called.
	 * @return stack trace elements.
	 */
	public static StackTraceElement[] getParentStackTrace()
	{
		Throwable t = new Throwable();
		StackTraceElement[] ste = t.getStackTrace();
		return truncate(ste);
	}
	
	/**
	 * Creates an exception instance whose stack trace information is truncated.
	 * @param e - exception instance. null object is not allowed.
	 * @return created exception instance.
	 */
	public static Exception truncate(Exception e)
	{
		Exception e2 = new Exception(e);
		StackTraceElement[] ste = e2.getStackTrace();
		e2.setStackTrace(truncate(ste));
		return e2;
	}
	
	/**
	 * Truncates a given array if it has the length larger than 1,
	 * i.e., the first element of the array is removed.
	 * @param ste - stack trace array.
	 * @return truncated array.
	 */
	public static StackTraceElement[] truncate(StackTraceElement[] ste)
	{
		if (ste.length <= 1)
		{
			return ste;
		}
		else
		{
			StackTraceElement[] ste2 = new StackTraceElement[ste.length-1];
			for (int i = 0; i < ste2.length; i++)
			{
				ste2[i] = ste[i+1];
			}
			return ste2;
		}
	}
}
