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
package jp.co.d_itlab.dbdc.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * Multi-Labeled error category
 * @param <L>- Type of labels
 */
public class ErrorCategory<L> extends ArrayList<L>
{
    private String name;
    
    public ErrorCategory(String name)
    {
        this.name = name;
    }
    
    public ErrorCategory(String name, int size)
    {
        super(size);
        this.name = name;
    }
    
    public  ErrorCategory(String name, String path)
    {
        this(name);
        
        try
        {
            File f = new File(path);
            // Can specify the encoding here if JDK13 is used.
            BufferedReader reader = new BufferedReader(new FileReader(f));
            String line;
            while ((line = reader.readLine()) != null)
            {
                line = line.trim();
                if (line.length() == 0 || line.startsWith("#"))
                {
                    continue;
                }
                add(parse(line));
            }
            reader.close();
        }
        catch (Exception e)
        {
            throw new RuntimeException("failed in loading the error category dictionary.");
        }
    }
    
    public L parse(String line)
    {
        if (line != null)
        {
            // PENDING
            return (L)line;
        }
        else
        {
            return null;
        }
    }
    
    public String getName()
    {
        return name;
    }
}
