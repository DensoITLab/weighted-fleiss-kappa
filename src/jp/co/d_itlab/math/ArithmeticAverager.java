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

/**
 * Class for calculating arithmetic average.
 * 
 * @author Hiroshi Tsukahara
 *
 */
public class ArithmeticAverager<T extends Number> extends NumericEstimator<T>
{
    private double sum = 0.0;
    private int n = 0;

    @Override
    public NumericEstimator<T> addDouble(double value)
    {
        sum += value;
        ++n;
        
        return this;
    }

    @Override
    public ArithmeticAverager<T> eval()
    {
        if (!isEvaluated)
        {
            isEvaluated = true;
            value = sum / n;
        }
        return this;
    }

    @Override
    public NumericEstimator<T> clear()
    {
        super.clear();
        sum = 0.0;
        n = 0;
        return this;
    }

}
