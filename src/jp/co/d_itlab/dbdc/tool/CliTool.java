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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import picocli.CommandLine.Option;

import jp.co.d_itlab.dbdc.logging.FootPrint;

/**
 * This is an abstract class for tool classes.
 * 
 * Following options are defined as default:
 * -s : command
 * 
 * 
 * @author Hiroshi Tsukahara
 */
public class CliTool implements Runnable
{
	private Map<String, Object> parameters = new HashMap<String, Object>();
	private Map<String, ICommandHandler> commands = new HashMap<String, ICommandHandler>();
	
	public final static String OPT_COMMAND = "-s";
	@Option(names = {OPT_COMMAND}, required = true, description = "command option")
    protected String command;

	private String executingCommand = null;
	
	public CliTool()
    {
        setParameter(OPT_COMMAND, command);
    }
	
	protected String getVersion()
    {
        return "No versions defined.";
    }
	
	public String getExecutingCommand()
	{
		return executingCommand;
	}
	
	public void setCommand(String name, ICommandHandler command)
	{
		commands.put(name, command);
	}
	
	public boolean isCommand(String command)
	{
	    return this.command.equals(command);
	}
	
	public ICommandHandler getCommand(String name)
	{
		return commands.get(name);
	}
	
	public void setParameter(String name, Object value)
	{
		parameters.put(name, value);
	}
	
	protected Map<String, Object> getParameters()
	{
		return parameters;
	}
	
	protected Object getParameterValue(String name)
	{
		return parameters.get(name);
	}
	
	
    public static PrintWriter openPrintWriter(String path)
    {
        File file = new File(path);
        try
        {
        	return new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8")));
        }
        catch (IOException ex)
        {
        	System.out.println("Exception: " + ex.getMessage());
        	return null;
        }
    }
    
    public void preprocess()
    {
    	// implement in an inherited class
    }
    
    public void postprocess()
    {
    	// implement in an inherited class
    }
    
    public void run()
    {        
        try
        {
            for (String name : commands.keySet())
            {
            	if (name.equals(command))
                {
            		executingCommand = name;
            		
            		FootPrint.show(MessageFormat.format("Starting command: {0}", command));
            		long start = System.currentTimeMillis();
                    preprocess();
                	getCommand(name).perform(getParameters());
                	postprocess();
                	long end = System.currentTimeMillis();
                	FootPrint.show(MessageFormat.format("Finished command: {0} - Exec Time: {1} sec.", command, (end - start)/1000.0));
                	return;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return;
        }
        
        // unknown command was given
        System.out.println(MessageFormat.format("Unknown command was given [{0}].", command));
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
	
	public static List<File> getFiles(Path path)
    {  
		if (path != null)
		{
			return getFiles(path.toString());
		}
		else
		{
			FootPrint.warn("Given path is null.");
			return null;
		}
    }
	
	
	public static List<File> getFiles(String path)
	{   
	    List<File> files = new ArrayList<File>();
	    
	    if (path != null)
	    {
	        File dir = new File(path);
	        for (File f : dir.listFiles())
	        {
	            if (!f.isDirectory())
	            {
	                files.add(f);
	            }
	        }
	    }
	    
	    return files;
	}
	
	public static List<File> getDirectories(Path path)
    {  
		if (path != null)
		{
			return getDirectories(path.toString());
		}
		else
		{
			FootPrint.warn("Given path is null.");
			return null;
		}
    }
	
	public static List<File> getDirectories(String path)
    {   
        List<File> files = new ArrayList<File>();
        
        if (path != null)
        {
            File dir = new File(path);
            for (File f : dir.listFiles())
            {
                if (f.isDirectory())
                {
                    files.add(f);
                }
            }
        }
        
        return files;
    }
}
