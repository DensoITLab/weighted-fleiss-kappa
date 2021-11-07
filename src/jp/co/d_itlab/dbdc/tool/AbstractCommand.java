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

import java.io.File;

public abstract class AbstractCommand implements ICommandHandler
{
	private String desc = null;
	private CliTool tools = null;
	
	/**
	 * 
	 */
	public AbstractCommand()
	{ 
	}
	
	/**
	 * 
	 */
	public AbstractCommand(CliTool tools)
	{
		this();
		attachTo(tools);
	}
	
	/**
	 * 
	 */
	public AbstractCommand(String desc)
	{
		setDesc(desc);
	}
	
	public void attachTo(CliTool tools)
	{
		this.tools = tools;
	}
	
	public CliTool getTools()
	{
		return this.tools;
	}

	@Override
	public String getDesc()
	{
		if (desc == null)
		{
			return "";
		}
		else
		{
			return desc;
		}
	}

	@Override
	public void setDesc(String desc)
	{
		this.desc = desc;
	}

    public static void preparePath(String dirPath)
	{
		File dir = new File(dirPath);
		if (!dir.exists())
		{
			dir.mkdirs();
		}
	}
    
    public static File prepareFile(String path, String filename)
	{
		File f = new File(path, filename);
		if (f.exists())
		{
			f.delete();
		}
		else
		{
			try
			{
				f.createNewFile();
			}
			catch (Exception e)
			{
				f = null;
				// TODO error handling
			}
		}
		
		return f;
	}
}
