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
 * Provides wrapper methods for <code>StackTraceElement</code>.
 */
public final class StackTraceAdapter {

	/**
	 * Returns the class name without extension.
	 * @param stack - stack trace element.
	 * @return the class name without extension.
	 */
	public static String getClassName(StackTraceElement stack)
	{
		String name = stack.getClassName();
		//return name.substring(0, name.length()-4);
		return name;
	}
	
	/**
	 * Returns the class name concatenated with the method name.
	 * @param stack - stack trace element.
	 * @return the class name concatenated with the method name.
	 */
	public static String getClassMethodName(StackTraceElement stack)
	{
		StringBuilder sb = new StringBuilder(getClassName(stack));
		sb.append(".");
		sb.append(stack.getMethodName());
		sb.append("()");
		sb.append("(").append(stack.getFileName()).append(":").append(stack.getLineNumber()).append(")");
		return sb.toString();
	}
	
	/**
	 * Returns the class name concatenated with the method name and the line number.
	 * @param stack
	 * @return the class name concatenated with the method name and the line number.
	 */
	public static String getClassMethodLine(StackTraceElement stack)
	{
		StringBuilder sb = new StringBuilder(getClassName(stack));
		sb.append(".");
		sb.append(stack.getMethodName());
		sb.append("(): ");
		sb.append(stack.getLineNumber());
		return sb.toString();
	}
}
