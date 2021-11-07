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

import java.util.List;
import java.util.Map;

import jp.co.d_itlab.dbdc.logging.DoubleFormat;
import jp.co.d_itlab.dbdc.logging.FootPrint;

public class IndexedMatrix<L, M> extends Matrix<L, M, Double>
{
    private double[][] doubleValues;
    
    public IndexedMatrix(List<L> rowIndices, List<M> columnIndices)
    {
        super(rowIndices, columnIndices, 0.0);
    }
    
    public IndexedMatrix(List<L> rowIndices, List<M> columnIndices, double[][] values)
    {
        this(rowIndices, columnIndices);
        doubleValues = values;
        for (int i = 0; i < rowIndices().size(); i++)
        {
            L r = rowIndices().get(i);
            for (int j = 0; j < columnIndices().size(); j++)
            {
                M l = columnIndices().get(j);
                set(r, l, values[i][j]);
            }
        }
    }
    
    public IndexedMatrix(List<L> rowIndices, List<M> columnIndices, Map<Integer, Map<Integer, Double>> values)
    {
        this(rowIndices, columnIndices);
        
        doubleValues = new double[values.size()][values.values().size()];
        
        for (int i = 0; i < rowIndices().size(); i++)
        {
            L r = rowIndices().get(i);
            for (int j = 0; j < columnIndices().size(); j++)
            {
                M l = columnIndices().get(j);
                set(r, l, values.get(i).get(j));
                doubleValues[i][j] =  values.get(i).get(j);
            }
        }
    }
    
    public double[][] getDoubleArray()
    {
        return doubleValues;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return toString(-3);
    }
    
    public String toString(int decimals)
    {
        StringBuilder sb = new StringBuilder("*, ");
        for (L r : rowIndices())
        {
            sb.append(r).append(", ");
        }
        for (L r : rowIndices())
        {
            sb.append("\n").append(r).append(", ");
            for (M c : columnIndices())
            {
                sb.append(DoubleFormat.round(get(r, c), decimals)).append(", ");
            }
        }
        return sb.toString();
    }

    public void show(int decimals)
    {
        FootPrint.show(toString(decimals));
    }
}
