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

public class DoublyStochasticMatrix extends NumericMatrix 
{
	final static double PRECESION = 0.0001; // pending
	final static int MAX_ITER = 50;                // pending
	
	public DoublyStochasticMatrix(NumericMatrix m)
	{
		super(m.rowIndices(), m.columnIndices());
		
		// TODO Validate: non-negativity
		
		sinkhorn();
	}
	
	public DoublyStochasticMatrix(double[][] values)
    {
		super(values);
		
		// TODO Validate: non-negativity
		
		sinkhorn();
    }
	
	public DoublyStochasticMatrix(Map<Integer, Map<Integer, Double>> values)
    {
        super(values);
        
        // TODO Validate: non-negativity
        
        sinkhorn();
    }
	
	/**
	 * Creates a doubly stochastic matrix using Sinkhorn algorithm.
	 */
	private void sinkhorn()
	{
		int n = 0;
		double error = getError();
		while (error > PRECESION)
		{
			if (++n > MAX_ITER)
			{
				// PENDING warning
				break;
			}
			
			List<Double> r = zeros(rowIndices().size());
			for (Integer i : rowIndices())
			{
				double s = 0.0;
				for (Integer j : columnIndices())
				{
					s += get(i, j);
				}
				r.set(i, s);
			}
			
			for (Integer i : rowIndices())
			{
				for (Integer j : columnIndices())
				{
				    if (r.get(i) != 0.0)
				    {
    					//set(i, j, get(i, j)/Math.sqrt(r.get(i)));
    					set(i, j, get(i, j)/r.get(i));
				    }
				}
			}
			
			List<Double> c = zeros(columnIndices().size());
			for (Integer j : columnIndices())
			{
				double s = 0.0;
				for (Integer i : rowIndices())
				{
					s += get(i, j);
				}
				c.set(j, s);
			}
			
			for (Integer j : columnIndices())
			{
				for (Integer i : rowIndices())
				{
				    if (c.get(i) != 0.0)
                    {
    					//set(i, j, get(i, j)/Math.sqrt(c.get(j)));
    					set(i, j, get(i, j)/c.get(j));
                    }
				}
			}
			
			error = getError();
		}	
	}
	
	private double getError()
	{
		double error = 0.0;
		for (Integer i : rowIndices())
		{
			double s = 0.0;
			for (Integer j : columnIndices())
			{
				s += get(i, j);
			}
			error += Math.abs(s - 1.0);
		}
		for (Integer j : columnIndices())
		{
			double s = 0.0;
			for (Integer i : rowIndices())
			{
				s += get(i, j);
			}
			error += Math.abs(s - 1.0);
		}
		return error;
	}
}
