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
 * Defines the foot print levels.
 */
public enum FootPrintLevel 
{
	/**
	 * Outputs a foot print by FootPrint.debug().
	 */
	DEBUG,
	
	/**
	 *  Outputs a foot print by FootPrint.info() or FootPrint.debug().
	 */
	INFO
}
