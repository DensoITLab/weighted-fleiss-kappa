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
import java.util.List;

/**
 * The interface for calculating estimators.
 * @author Hiroshi Tsukahara
 *
 */
public interface IEstimator<T, S>
{   
    /**
     * Adds an item to be evaluated.
     * @param value
     * @return
     */
    IEstimator<T, S> add(T value);
    
    /**
     * Adds a list of items to be evaluated.
     * @param value
     * @return
     */
    IEstimator<T, S> add(Collection<T> data);
    
    /**
     * Returns the number of added items.
     * @return
     */
    IEstimator<T, S> eval();
    
    /**
     * Clear added items.
     * @return
     */
    IEstimator<T, S> clear();
    
    /**
     * Returns the estimator value.
     * @return
     */
    S getValue();
    
    /**
     * Returns the data given to the estimator.
     * @return
     */
    List<T> getData();
}
