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
import java.util.Map;

/**
 * Calculates inter-annotator agreements.
 * 
 * @param <K>- Type of data indices
 * @param <L>- Type of labels
 */
public class FrequencyStatistics<K, L>
{
    private AnnotationMatrix<K, L> m1;
    private AnnotationMatrix<K, L> m2;
    private Map<String, AnnotationMatrix<K, L>> data;
    private Map<String, Map<L, Double>> freqs;
    Map<L, Double> freq1 = null;
    Map<L, Double> freq2 = null;
    private Double agreement = null;

    public FrequencyStatistics(Map<String, AnnotationMatrix<K, L>> data)
    {
        this.data = data;
        if (data.size() != 2)
        {
            throw new RuntimeException("Weighted Kappa can be calculated for annotation data of a pair of annotators.");
        }
        else 
        {
            freqs = new HashMap<>();
            
            int i = 0;
            for (String aid : data.keySet())
            {
                if (++i == 1)
                {
                    m1 = data.get(aid);
                    freqs.put(aid, freq1);
                }
                else
                {
                    m2 = data.get(aid);
                    freqs.put(aid, freq2);
                }
            }
        }
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
    
    private void caculate()
    {
        // calculate IAA
        agreement = 0.0;
        freq1 = new HashMap<>();
        freq2 = new HashMap<>();
        for (L c : m1.getCategory())
        {
            freq1.put(c, 0.0);
            freq2.put(c, 0.0);
        }
        
        int N = m1.getData().size();
        for (K k : m1.getData())
        {
            for (L l : m1.getCategory())
            {
                double score1 = 0.0;
                if (m1.contains(k, l))
                {
                    score1 = m1.get(k, l);
                    freq1.put(l, freq1.get(l) + score1);
                }
                double score2 = 0;
                if (m2.contains(k, l))
                {
                    score2 = m2.get(k, l);
                    freq2.put(l, freq2.get(l) + score2);
                }
                agreement += score1 * score2; 
            }
        }

        agreement  = agreement / N;
        
        double pe = 0.0;
        for (L c : m1.getCategory())
        {
            pe += freq1.get(c) * freq2.get(c);
        }
        pe /= Math.pow(N, 2);
        
    }
}