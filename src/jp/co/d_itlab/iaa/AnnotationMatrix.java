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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Correspondance between data entry and annotations.
 * 
 *
 * @param <K> - data index type
 * @param <L> - label type
 */
public class AnnotationMatrix<K, L>
{
    private String annotator;
    private List<K> data;
    private List<L> category;
    private Map<K, Map<L, Double>> matrix;
    
    public AnnotationMatrix(String annotator, List<K> data, List<L> category)
    {
        this.annotator = annotator;
        this.data = data;
        this.category = category;
        matrix = new HashMap<K, Map<L, Double>>(data.size());
        for (K k : data)
        {
            matrix.put(k, new HashMap<>());
        }
    }
    
    public String getAnnotator()
    {
        return annotator;
    }
    
    public boolean contains(K k, L l)
    {
        boolean found = false;
        
        if (matrix.containsKey(k))
        {
            if (matrix.get(k).containsKey(l))
            {
                found = true;
            }
        }
        
        return found;
    }
    
    public List<L> getCategory()
    {
        return category;
    }
    
    public List<K> getData()
    {
        return data;
    }
    
    public int getNumData()
    {
        return data.size();
    }
    
    public int getNumCategory()
    {
        return category.size();
    }
    
    public boolean exists(K k, L l)
    {
        boolean found =false;
        Map<L, Double> row = matrix.get(k);
        if (row.containsKey(l))
        {
            found = true;
        }
        return found;
    }
    
    public double get(K k, L l)
    {
        double value = 0.0;
        Map<L, Double> row = matrix.get(k);
        if (exists(k, l))
        {
            value = row.get(l);
        }
        return value;
    }
    
    public double add(K k, L l, double d)
    {
        Map<L, Double> row = matrix.get(k);
        
        double value = get(k, l);
        if (value > 0.0)
        {
            value = value + d;
        }
        else
        {
            value = d;
        }
        row.put(l, value);
        
        return value;
    }
    
    public double countUp(K k, L l)
    {
        return add(k, l , 1.0);
    }
    
    public Map<L, Double> row(K k)
    {
        if (matrix.containsKey(k))
        {
            return matrix.get(k);
        }
        else
        {
            return null;
        }
    }
    
    public List<Map<L, Double>> rows()
    {
        List<Map<L, Double>> rows = new ArrayList<>();
        for (K k : matrix.keySet())
        {
            rows.add(matrix.get(k));
        }
        return rows;
    }
}