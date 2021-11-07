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
import java.util.Collection;
import java.util.List;

public abstract class NumericEstimator<T extends Number> implements IEstimator<T, T>
{
    protected boolean isEvaluated = true;
    protected double value = 0.0;
    private List<T> data = new ArrayList<T>();
    
    public NumericEstimator()
    {
    }
    
     /**
      * Add an item to be evaluated.
      * @param value
      */
     public abstract NumericEstimator<T> addDouble(double value);
    
    @Override
    public NumericEstimator<T> add(T value)
    {
        if (value != null)
        {
            isEvaluated = false;
            data.add(value);
            return addDouble(value.doubleValue());
        }
        else
        {
            return this;
        }
    }

    @Override
    public NumericEstimator<T> add(Collection<T> value)
    {
        if (value != null)
        {
            for (T item : value)
            {
                add(item);
            }
        }
        return this;
    }

    @Override
    public IEstimator<T, T> clear()
    {
        isEvaluated = true;
        value = 0.0;
        data.clear();
        return this;
    }

    @Override
    public T getValue()
    {
        eval();
        return (T)Double.valueOf(value);
    }

    @Override
    public List<T> getData()
    {
        return data;
    }
    
    
}
