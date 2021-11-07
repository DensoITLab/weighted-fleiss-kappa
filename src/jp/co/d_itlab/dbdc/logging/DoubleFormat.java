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

import java.text.MessageFormat;

public class DoubleFormat
{
    public static <T extends Number> String division(T n)
    {
        return MessageFormat.format("{0, number}", n.doubleValue());
    }
    
    public static <T extends Number> String round(T n, int lower)
    {
        double d = n.doubleValue();
        
        StringBuilder template = new StringBuilder();

        if (lower < 0)
        {
            template.append("0.");
            for (int i = 0; i < -lower; i++)
            {
                d *= 10;
                template.append("0");
            }
            d = Math.round(d);
            for (int i = 0; i < -lower; i++)
            {
                d /= 10;
            }
        }
        else if (lower == 0)
        {
            template.append("0");
            d = (int)d;
        }
        else
        {
            for (int i = 0; i < lower; i++)
            {
                d /= 10;
                template.append("0");
            }
            d = Math.round(d);
            for (int i = 0; i < lower; i++)
            {
                d *= 10;
            }
        }
        
        return MessageFormat.format("{0, number," + template + "}", d);
    }
    
    public static <T extends Number> String format(T n, int digits, int decimals)
    {
        double d = n.doubleValue();
        
        StringBuilder template = new StringBuilder();
        if (digits > 0)
        {
            for (int i = 0; i < digits; ++i)
            {
                d /=10;
                template.append("0");
            }
            d = d - (int)d;
            for (int i = 0; i < digits; ++i)
            {
                d *=10;
            }
        }
        else
        {
            // regards digits == 0 
            template.append("0");
        }
        
        template.append(".");
        
        if (decimals > 0)
        {
            
            for (int i = 0; i < decimals; i++)
            {
                template.append("0");
            }
        }
        else
        {
            // regards decimals == 0
            template.append("0");
        }
        return MessageFormat.format("{0, number, " + template +"}", d);
    }
    
    public static void main(String[] args)
    {
        double d = 123456789.123456789;
        System.out.println(DoubleFormat.division(d));
        System.out.println(DoubleFormat.round(d, 7));
        System.out.println(DoubleFormat.round(d, 2));
        System.out.println(DoubleFormat.round(d, 0));
        System.out.println(DoubleFormat.round(d, -2));
        System.out.println(DoubleFormat.round(d, -8));
        System.out.println(DoubleFormat.format(d, 15, 1));
        System.out.println(DoubleFormat.format(d, 5, 3));
    }

}
