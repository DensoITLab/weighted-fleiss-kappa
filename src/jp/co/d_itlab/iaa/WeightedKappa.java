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

import jp.co.d_itlab.dbdc.logging.FootPrint;

/**
 * Calculates weighted kappa coefficients.
 *
 * @param <K>- data index type
 * @param <L> - label type
 */
public class WeightedKappa<K, L>
{
    private AnnotationMatrix<K, L> m1;
    private AnnotationMatrix<K, L> m2;
    private Map<String, AnnotationMatrix<K, L>> data;
    private ConfusionMatrix<L> confusion;
    private Map<String, Map<L, Double>> freqs;
    Map<L, Double> freq1 = null;
    Map<L, Double> freq2 = null;
    private Double agreement = null;
    private Double kappa = null;
    private List<L> category;
    
    public WeightedKappa(Map<String, AnnotationMatrix<K, L>> data)
    {
        this.data = data;
        if (data.size() != 2)
        {
            throw new RuntimeException("Weighted Kappa can be calculated for annotation data of a pair of annotators.");
        }
        else 
        {
            freqs = new HashMap<>();
            freq1 = new HashMap<>();
            freq2 = new HashMap<>();
            
            int i = 0;
            for (String aid : data.keySet())
            {
                if (++i == 1)
                {
                    m1 = data.get(aid);
                    freqs.put(aid, freq1);
                    category = m1.getCategory();
                }
                else
                {
                    m2 = data.get(aid);
                    freqs.put(aid, freq2);
                }
            }
            confusion = new ConfusionMatrix<>(m1.getCategory());
        }
    }
    
    public List<L> getCategory()
    {
        return category;
    }
    
    public Map<String, AnnotationMatrix<K, L>> getData()
    {
        return data;
    }
    
    public Map<L, Double> getFreq(String aid)
    {
        Map<L, Double> ret = freqs.get(aid);
        if (ret == null)
        {
            caculate();
        }
        return ret;
    }
    
    public double getAgreement()
    {
        if (agreement == null)
        {
            caculate();
        }
        return agreement;
    }
    
    public double getKappa()
    {
        if (kappa == null)
        {
            caculate();
        }
        return kappa;
    }
    
    public ConfusionMatrix<L> getConfusionMatrix()
    {
        return confusion;
    }
    
    private void caculate()
    {
        // 一致率の規格化用
        Map<K, Double> agreements = new HashMap<>();
        Map<K, Double> norms = new HashMap<>();
        
        // デバッグ用カウンター
        int numInconsistent = 0;
        
        // calculate IAA
        agreement = 0.0;
        
        for (L c : m1.getCategory())
        {
            freq1.put(c, 0.0);
            freq2.put(c, 0.0);
            
            for (L c2 : m1.getCategory())
            {
                confusion.set(c, c2, 0.0);
            }
        }
        
        int N = m1.getData().size();
        double n = 0.0;
        double offset1 = 0.0;
        double offset2 = 0.0;
        for (K k : m1.getData())
        {
            agreements.put(k, 0.0); 
            norms.put(k, 0.0);         
            
            n += 1.0;
            double s1 = 0.0;
            double s2 = 0.0;
            Map<L, Double> dist1 = new HashMap<L, Double>();
            Map<L, Double> dist2 = new HashMap<L, Double>();
            for (L l : m1.getCategory())
            {
                double score1 = 0.0;
                if (m1.contains(k, l))
                {
                    score1 = m1.get(k, l);
                    s1 += score1;
                    freq1.put(l, freq1.get(l) + score1);    
                }
                dist1.put(l, score1);
                
                double score2 = 0;
                if (m2.contains(k, l))
                {
                    score2 = m2.get(k, l);
                    s2 += score2;
                    freq2.put(l, freq2.get(l) + score2);
                }
                dist2.put(l, score2);

                agreements.put(k,  agreements.get(k) + score1 * score2);
                norms.put(k, norms.get(k) + 0.5  *(score1 * score1 +  score2 * score2)); 
            }
            
            if (s1 < 1.0 || s2 < 1.0)
            {
                Map<L, Double> row1 = m1.row(k);
                Map<L, Double> row2 = m2.row(k);
                FootPrint.warn("Inconsistent labelling.");
            }
            
            double sum1 = 0.0;
            double sum2 = 0.0;
            for (L l : m1.getCategory())
            {
                sum1 += freq1.get(l);
                sum2 += freq2.get(l);
            }
            
            if (!(Math.abs(sum1 - n - offset1) < 0.5 && Math.abs(sum2 - n - offset2) < 0.5))
            {
                Map<L, Double> row1 = m1.row(k);
                Map<L, Double> row2 = m2.row(k);
                FootPrint.warn("Inconsistent Frequency.");
                ++numInconsistent;
                offset1 += sum1 - n - offset1;
                offset2 += sum2 - n - offset2;
            }   
            
            for (L l1 : m1.getCategory())
            {
                for (L l2 : m1.getCategory())
                {
                    double v1 = dist1.get(l1);
                    double v2 = dist2.get(l2);
                    confusion.add(l1, l2, v1 * v2);
                }
            }
        }
        
        FootPrint.debug("#Inconsistents: " + numInconsistent);

        for (K k : m1.getData())
        {
            double agreement = agreements.get(k) / norms.get(k);
            if (Double.isNaN(agreement))
            {
                agreement = 0.0; // Pending
            }
            agreements.put(k,  agreements.get(k) / norms.get(k));
        }
        agreement = agreements.values().stream().mapToDouble(Double::doubleValue).sum() / N;
        
        double pe = 0.0;
        for (L c : m1.getCategory())
        {
            pe += freq1.get(c) * freq2.get(c);
        }
        pe /= Math.pow(N, 2);
        
        kappa = (agreement - pe) / (1 - pe);
    }
}
