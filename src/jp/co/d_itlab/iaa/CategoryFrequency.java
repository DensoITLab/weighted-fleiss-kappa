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
package jp.co.d_itlab.iaa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Counts the frequency of categories.
 * 
 * @param <K> - label type
 */
public class CategoryFrequency<L>
{
    private Map<L, Double> freq;
    
    public CategoryFrequency(AnnotationMatrix<?, L> matrix)
    {
        freq = zeroFreq(matrix.getCategory());

        for (Map<L, Double> row : matrix.rows())
        {
            for (L l : row.keySet())
            {
                add(l, row.get(l));
            }
        }
    }
    
   public double get(L l)
   {
       return freq.get(l);
   }
   
   public double add(L l)
   {
       return add(l, 1.0);
   }
   
   public double add(L l, double d)
   {
       double value = freq.get(l) +d;
       freq.put(l, value);
       return value;
   }
    
    public Map<L, Double> zeroFreq(List<L> category)
    {
        Map<L, Double> freq = new HashMap<>(category.size());
        for (L l : category)
        {
            freq.put(l, 0.0);
        }
        return freq;
    }
}