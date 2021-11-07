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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Generic matrix class.
 *
 * @param <L> - type of row index
 * @param <M> - type of column index
 * @param <T> - type of matrix values.
 */
public class Matrix<L, M, V> implements IMatrix<L, M, V>
{
	private Map<L, Map<M, V>> values;
	private List<L> rowIndices;
	private List<M> columnIndices;
	
	
	public Matrix()
	{
		values = new HashMap<>();
		rowIndices = new ArrayList<>();
		columnIndices = new ArrayList<>();
	}
	
	public Matrix(List<L> rowIndices, List<M> columnIndices, V v)
	{
		this();
		for (L r : rowIndices)
		{
			this.rowIndices.add(r);
			values.put(r, new HashMap<>());
		}
		
		for (M m : columnIndices)
		{
			this.columnIndices.add(m);
		}
		
		for (L r : rowIndices)
		{
			values.put(r, new HashMap<>());
			for (M m : columnIndices)
			{
				values.get(r).put(m, v);
			}
		}
	}

	@Override
	public V get(L l, M m) 
	{
		if (!values.containsKey(l))
		{
			return null;
		}
		
		Map<M, V> row = values.get(l);
		if (!row.containsKey(m))
		{
			return null;
		}
		else
		{
			return row.get(m);
		}
	}

	@Override
	public void set(L l, M m, V v) 
	{
		if (!values.containsKey(l))
		{
			values.put(l, new HashMap<>());
		}
		Map<M, V> row = values.get(l);
		row.put(m, v);
	}

	@Override
	public List<L> rowIndices() 
	{
		return rowIndices;
	}

	@Override
	public List<M> columnIndices() 
	{
		return columnIndices;
	}
	
    @Override
    public Map<L, Map<M, V>> getValues()
    {
        return values;
    }

    @Override
    public Map<Integer, Map<Integer, V>> getIndexedMatrix()
    {
        Map<Integer, Map<Integer, V>> values = new HashMap<>();
        for (int i = 0; i < rowIndices().size(); i++)
        {
            L r = rowIndices().get(i);
            values.put(i, new HashMap<>());
            for (int j = 0; j < columnIndices().size(); j++)
            {
                M l = columnIndices().get(j);
                values.get(i).put(j, get(r, l));
            }
        }
        return values;
    }
	

}
