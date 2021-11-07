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
package jp.co.d_itlab.math;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Sparse matrix.
 */
public class NumericMatrix extends Matrix<Integer, Integer, Double>
{
	public NumericMatrix(List<Integer> indices)
    {
        super(indices, indices, 0.0);
    }
	
	public NumericMatrix(double[][] values)
    {
        super(range(0, values.length), range(0, values[0].length), 0.0);
        for (Integer i : rowIndices())
        {
        	for (Integer j : columnIndices())
        	{
        		set(i, j, values[i][j]);
        	}
        }
    }
	
	public NumericMatrix(Map<Integer, Map<Integer, Double>> values)
    {
        super(range(0, values.keySet().size()), range(0, values.values().size()), 0.0);
        for (Integer i : rowIndices())
        {
            for (Integer j : columnIndices())
            {
                set(i, j, values.get(i).get(j));
            }
        }
    }
	
	public NumericMatrix(List<Integer> rows, List<Integer> columns)
    {
        super(rows, columns, 0.0);
    }
    
    public void add(Integer r, Integer c, Double v)
    {
        double m = get(r, c);
        set(r, c, m + v);
    }
    
    public double[][] getDoubleArray()
    {
        double[][] doubleValues = new double[rowIndices().size()][columnIndices().size()];
        
        for (int i = 0; i < rowIndices().size(); i++)
        {
            Integer r = rowIndices().get(i);
            for (int j = 0; j < columnIndices().size(); j++)
            {
            	Integer c = columnIndices().get(j);
                doubleValues[i][j] = get(r, c);
            }
        }
        
        return doubleValues;
    }
    
    public static List<Integer> range(int start, int length)
    {
        List<Integer> ret = new ArrayList<>(length);
        for (int i = 0; i < length; i++)
        {
            ret.add(start + i);
        }
        return ret;
    }
    
    public static List<Double> zeros(int length)
    {
        List<Double> ret = new ArrayList<>(length);
        for (int i = 0; i < length; i++)
        {
            ret.add(0.0);
        }
        return ret;
    }
    
}
