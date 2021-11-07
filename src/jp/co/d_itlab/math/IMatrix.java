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
 * The interface for matrices whose their row and column indices let be generic.
 * 
 * @param <L> - Type of row index
 * @param <M> - Type of column index
 * @param <V> - Type of values
 */
public interface IMatrix<L, M, V>
{
	V get(L l, M m);
	
	void set(L l, M m, V v);
	
	List<L> rowIndices();
	
	List<M> columnIndices();
	
	Map<L, Map<M, V>> getValues();
	
	Map<Integer, Map<Integer, V>> getIndexedMatrix();
}
