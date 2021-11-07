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

import java.util.List;
import java.util.Map;

import jp.co.d_itlab.dbdc.logging.DoubleFormat;
import jp.co.d_itlab.math.DoublyStochasticMatrix;
import jp.co.d_itlab.math.IndexedMatrix;
import jp.co.d_itlab.math.Matrix;
import jp.co.d_itlab.math.VariationMatrix;

/**
 * Confusion mamtrix.
 *
 * @param <L> - Type of labels
 */
public class ConfusionMatrix<L> extends Matrix<L, L, Double>
{
    private Double dialognalSum = null;
    private Double nonDialognalSum = null;
    private Double entireSum = null;
    
    public ConfusionMatrix(List<L> indices)
    {
        super(indices, indices, 0.0);
    }
    
    public void add(L r, L c, double v)
    {
        double m = get(r, c);
        set(r, c, m + v);
    }
    
    public IndexedMatrix<L, L> getDoublyStochasticMatrix()
    {
        Map<Integer, Map<Integer, Double>> values = getIndexedMatrix();
        DoublyStochasticMatrix dsm = new DoublyStochasticMatrix(values);
        return new IndexedMatrix<L, L>(rowIndices(), columnIndices(), dsm.getDoubleArray());
    }
    
    public IndexedMatrix<L, L> getVariationMatrix()
    {
        Map<Integer, Map<Integer, Double>> values = getIndexedMatrix();
        VariationMatrix vm = new VariationMatrix(values);
        return new IndexedMatrix<L, L>(rowIndices(), columnIndices(), vm.getDoubleArray());
    }
    
    public double getDiagonalSum()
    {
        if (dialognalSum == null)
        {
            dialognalSum = 0.0;
            for (L r : rowIndices())
            {
                dialognalSum += get(r, r);
            }
        }
        return dialognalSum;
    }
    
    public double getNonDiagonalSum()
    {
        if (nonDialognalSum == null)
        {
            nonDialognalSum = getEntireSum();
            nonDialognalSum -= getDiagonalSum();
            nonDialognalSum /= 2.0;
        }
        return nonDialognalSum;
    }
    
    public double getEntireSum()
    {
        if (entireSum == null)
        {
            entireSum = 0.0;
            for (L r : rowIndices())
            {
                for (L c : columnIndices())
                {
                    entireSum += get(r, c);
                }
            }
        }
        return entireSum;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return toString(-3);
    }
    
    public String toString(int decimals)
    {
        StringBuilder sb = new StringBuilder("*, ");
        for (L r : rowIndices())
        {
            sb.append(r).append(", ");
        }
        for (L r : rowIndices())
        {
            sb.append("\n").append(r).append(", ");
            for (L c : columnIndices())
            {
                sb.append(DoubleFormat.round(get(r, c), -3)).append(", ");
            }
        }
        return sb.toString();
    }

    public void show()
    {
        System.out.println(this);
    }
}
