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

import java.util.Collection;

/**
 * Variance estimator.
 */
public class VarianceEstimator<T extends Number> extends NumericEstimator<T>
{
    private double sum = 0.0;
    private double squared = 0.0;
    private double average = 0.0;
    private int n = 0;
    
    public VarianceEstimator()
    {
        super();
    }
    
    public VarianceEstimator(Collection<T> data)
    {
        add(data);
    }

    /* 
     * (non-Javadoc)
     * @see jp.co.d_itlab.math.NumericEstimator#addDouble(double)
     */
    @Override
    public NumericEstimator<T> addDouble(double value)
    {
        sum += value;
        squared += value * value;
        ++n;

        return this;
    }

    /* (non-Javadoc)
     * @see jp.co.d_itlab.commons.model.math.IEstimator#eval()
     */
    @Override
    public VarianceEstimator<T> eval()
    {
        if (!isEvaluated)
        {
            isEvaluated = true;
            average = sum / n;
            value = squared / n - average * average;
        }
        return this;
    }

    /*
     * (non-Javadoc)
     * @see jp.co.d_itlab.math.NumericEstimator#clear()
     */
    @Override
    public NumericEstimator<T> clear()
    {
        super.clear();
        sum = 0.0;
        squared = 0.0;
        average = 0.0;
        n = 0;
        return this;
    }

    public T getAverage()
    {
        eval();
        return (T)Double.valueOf(average);
    }
    
    public T getVariance()
    {
        return getValue();
    }
    
    /**
     * 標本分散を返す
     * @return 標本分散
     */
    public T getUnbiasedVariance()
    {
        return (T)Double.valueOf((n/(n -1)) * value);
    }
    
    /**
     * 標本分散を返す
     * @return 標本分散
     */
    public T getUnbiasedDeviation()
    {
        if (n > 1)
        {
            return (T)Double.valueOf(Math.sqrt((n/(n -1)) * value));
        }
        else
        {
            return (T)new Double(0);
        }
    }
       
}
