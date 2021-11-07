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
package jp.co.d_itlab.dbdc.tool;

import java.util.Map;

public interface ICommandHandler
{
	/**
	 * Performs the command.
	 * @param parameters - Parameters of the command.
	 */
	void perform(Map<String, Object> parameters);
	
	/**
	 * Returns the description of the command.
	 * @return
	 */
	String getDesc();
	
	/**
	 * Returns the description of the command.
	 * @return
	 */
	void setDesc(String desc);
	
}
