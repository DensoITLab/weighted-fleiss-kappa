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

/**
 * Transforms a given matrix into a matrix whose rows and columns are normalized by the square root of their diagonal elements.
 */
public class VariationMatrix extends NumericMatrix
{
    final static double PRECESION = Double.MIN_VALUE;
    final static double REGULARIZATION_CONSTANT = 0.0001;
            
    public VariationMatrix(NumericMatrix m)
    {
        super(m.rowIndices(), m.columnIndices());
        
        // Validate: nonnegativity
        
        process();
    }
    
    public VariationMatrix(double[][] values)
    {
        super(values);
        
        // Validate: nonnegativity
        
        process();
    }
    
    public VariationMatrix(Map<Integer, Map<Integer, Double>> values)
    {
        super(values);
        
        // Validate: nonnegativity
        
        process();
    }
    
    private void process()
    {
        List<Double> diag = zeros(rowIndices().size());
        for (Integer i : rowIndices())
        {
            double d = get(i,i);
            if (d < PRECESION)
            {
                d = REGULARIZATION_CONSTANT;
            }
            diag.set(i, Math.sqrt(d));
        }
        
        for (Integer i : rowIndices())
        {
            for (Integer j : columnIndices())
            {
                set(i, j, get(i, j)/diag.get(i));
            }
        }
        
        for (Integer j : columnIndices())
        {
            for (Integer i : rowIndices())
            {
                set(i, j, get(i, j)/diag.get(j));
            }
        }
        
        for (Integer i : rowIndices())
        {
            set(i, i, 0.0);
        }
    }
}
