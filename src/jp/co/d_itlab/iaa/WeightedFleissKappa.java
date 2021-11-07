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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.co.d_itlab.dbdc.logging.FootPrint;
import jp.co.d_itlab.dbdc.logging.FootPrint;
import jp.co.d_itlab.math.VarianceEstimator;

/**
 * Calculates the Fleiss' kappa coefficient.
 */
public class WeightedFleissKappa<K, L>
{
    private Map<String, AnnotationMatrix<K, L>> data;
    private ConfusionMatrix<L> confusion;
    private Map<K, Map<L, Double>> x;
    private Map<L, Double> q;
    private Map<String, Map<L, Double>> freqs;
    private Map<L, Double> cofreq;
    private Map<String, Map<L, Double>> wfreqs;
    private Double agreement = null;
    private Double kappa = null;
    private Map<String, Double> labelCardinalities;
    private Map<String, Double> labelDensities;
    
    // Judges (Annotators)
    private int A = 0;
    
    // Category (Label) set
    private List<L> category;
    private int Q = 0;
    
    // Subjects (Instances)
    private List<K> subjects;
    private int N = 0;
    
    public WeightedFleissKappa(Map<String, AnnotationMatrix<K, L>> data, List<L> category)
    {
        this.data = data;
        this.category = category;
        if (data.size() < 2)
        {
            throw new RuntimeException("Weighted Fleiss' Kappa can not be calculated for annotation data with less than two annotators.");
        }
        else 
        {
            freqs = new HashMap<>();
            cofreq = new HashMap<>();
            wfreqs = new HashMap<>();
            x = new HashMap<>();
            q = new HashMap<>();
            labelCardinalities = new HashMap<>();
            labelDensities = new HashMap<>();
            A = data.keySet().size();
            for (String aid : data.keySet())
            {
                Map<L, Double> freq = new HashMap<>();
                freqs.put(aid, freq);
                
                Map<L, Double> wfreq = new HashMap<>();
                wfreqs.put(aid, wfreq);
                
                AnnotationMatrix<K, L> m = data.get(aid);
                if (N == 0)
                {
                    subjects = m.getData();
                    N = subjects.size();
                }
                else
                {
                    if (N != m.getNumData())
                    {
                        System.out.println(MessageFormat.format("[WARN] Inconsistent data size. [N: {0} vs {1}", N, m.getNumData()));
                    }
                }
                
                labelCardinalities.put(aid, 0.0);
                labelDensities.put(aid, 0.0);
                if (Q == 0)
                {
                    Q = this.category.size();
                }
            }
            confusion = new ConfusionMatrix<>(category);
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
    
    /**
     * Returns the frequency of categories for a specified annotator.
     * 
     * @param aid - annotator ID
     */
    public Map<L, Double> getFreq(String aid)
    {
        Map<L, Double> ret = freqs.get(aid);
        if (ret == null)
        {
            caculate();
            ret = freqs.get(aid);
        }
        return ret;
    }
    
    /**
     * Returns a frequency of categories averaged over all annotators.
     */
    public Map<L, Double> getAveragedFreq()
    {
        Map<L, Double> ret = new HashMap<>();
        for (String aid : freqs.keySet())
        {
            Map<L, Double> freq = freqs.get(aid);
            if (freq == null)
            {
                caculate();
                freq = freqs.get(aid);
            }
            
            for (L l : freq.keySet())
            {
                if (!ret.containsKey(l))
                {
                    ret.put(l, 0.0);
                }
                ret.put(l, ret.get(l) + freq.get(l));
            }
        }
        
        for (L l : ret.keySet())
        {
            ret.put(l, ret.get(l) / freqs.size());
        }
        
        return ret;
    }
    
    /**
     * 全ユーザについて各カテゴリについて出現した頻度の分布を返す
     * @return
     */
    public Map<L, Double> getCofreq()
    {
        if (cofreq == null)
        {
            caculate();
        }
        
        return cofreq;
    }
    
    public Map<L, Double> getWeightedFreq(String aid)
    {
        Map<L, Double> ret = wfreqs.get(aid);
        if (ret == null)
        {
            caculate();
            ret = wfreqs.get(aid);
        }
        return ret;
        //return normalize(ret);
    }
    
    public Map<L, Double> getAverageWeightedFreq()
    {
        Map<L, Double> ret = new HashMap<>();
        for (String aid : wfreqs.keySet())
        {
            Map<L, Double> wfreq = wfreqs.get(aid);
            if (wfreq == null)
            {
                caculate();
                wfreq = wfreqs.get(aid);
            }
            
            for (L l : wfreq.keySet())
            {
                if (!ret.containsKey(l))
                {
                    ret.put(l, 0.0);
                }
                ret.put(l, ret.get(l) + wfreq.get(l));
            }
        }
        
        for (L l : ret.keySet())
        {
            ret.put(l, ret.get(l) / wfreqs.size());
        }
        return ret;
        //return normalize(ret);
    }
    
    private Double integrate(Map<L, Double> freq)
    {
        double s = 0.0;
        for (L l : freq.keySet())
        {
            s +=  freq.get(l);
        }
        return s;
    }
    
    private Map<L, Double> normalize(Map<L, Double> freq)
    {
        double s = integrate(freq);
        for (L l : freq.keySet())
        {
            freq.put(l,  freq.get(l) / s);
        }
        return freq;
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
    
    public VarianceEstimator<Double> getLabelCardinarity()
    {
        return  new VarianceEstimator(labelCardinalities.values());
    }
    
    public VarianceEstimator<Double> getLabelDensity()
    {
        return  new VarianceEstimator(labelDensities.values());
    }
    
    private void caculate()
    {
        // Counter for debugging
        int numInconsistent = 0;

        // calculate IAA
        Map<K, Double> agreements = new HashMap<>();
        Map<K, Double> norms = new HashMap<>();
        List<List<String>> pairs = getPairComb(data.keySet());
        Set<String> judges = data.keySet();
        
        // initialization
        for (L c : category)
        {
            for (Map<L, Double> freq : freqs.values())
            {
                freq.put(c, 0.0);
            }
            
            cofreq.put(c, 0.0);
            
            for (Map<L, Double> wfreq : wfreqs.values())
            {
                wfreq.put(c, 0.0);
            }
            
            for (L c2 : category)
            {
                confusion.set(c, c2, 0.0);
            }
        } 
        
        // For debugging
        Map<String, Double> offsets = new HashMap<>();
        for (String a : data.keySet())
        {
            offsets.put(a, 0.0);
        }
        
        double n = 0.0;
        for (K k : subjects)
        {
            n += 1.0;
            agreements.put(k, 0.0);
            x.put(k, new HashMap<>());
            for (L l : category)
            {
                x.get(k).put(l, 0.0);
            }
            
            Map<String, Double> sumOfScores = new HashMap<>();
            Map<String, Map<L, Double>> dists = new HashMap<>();
            for (String a : judges)
            {
                sumOfScores.put(a, 0.0);
                dists.put(a,  new HashMap<>());
                
                int numLabels = 0;
                for (L l : category)
                {
                    double score = data.get(a).get(k, l);
                    if (score > 0.0)
                    {
                        ++numLabels;
                        freqs.get(a).put(l, freqs.get(a).get(l) + 1.0);   
                    }
                    x.get(k).put(l, x.get(k).get(l) + score);
                    dists.get(a).put(l, 0.0);
                    wfreqs.get(a).put(l, wfreqs.get(a).get(l) + score);    
                }
                
                labelCardinalities.put(a, labelCardinalities.get(a) + numLabels);
                labelDensities.put(a, labelDensities.get(a) + (double)numLabels/Q);
            }
            
            norms.put(k, 0.0);
            for (L l : category)
            {
                for (String a : judges)
                {
                    dists.get(a).put(l, dists.get(a).get(l) + data.get(a).get(k, l));    
                }
                
                for (List<String> pair : pairs)
                {
                    String a1 = pair.get(0);
                    String a2 = pair.get(1);
                    
                    double score1 = data.get(a1).get(k, l);
                    double score2 = data.get(a2).get(k, l);
                    double prod = score1 * score2;
                    agreements.put(k,  agreements.get(k) + prod);
                    norms.put(k, norms.get(k) + 0.5  *(score1 * score1 +  score2 * score2));
                    
                    if (prod > 0.0)
                    {
                        cofreq.put(l, cofreq.get(l) + 1.0);
                    }
                }
            }
            
            // Confusion matrix
            List<List<String>> symPairs = getSymmetricPairComb(data.keySet());
            for (L l1 : category)
            {
                for (L l2 : category)
                {
                    for (List<String> pair : symPairs)
                    {
                        double v1 = dists.get(pair.get(0)).get(l1);
                        double v2 = dists.get(pair.get(1)).get(l2);
                        //double v = v1 * v2 / (N * symPairs.size());
                        double v = v1 * v2 / symPairs.size();
                        confusion.add(l1, l2, v);
                    }
                }
            }
        }
        
        FootPrint.debug("#Inconsistents: " + numInconsistent);
        
        for (String aid : labelCardinalities.keySet())
        {
            labelCardinalities.put(aid, labelCardinalities.get(aid) / N);
            labelDensities.put(aid, labelDensities.get(aid) / N);
        }
        
        for (L l : category)
        {
            q.put(l, 0.0);
            for (K k : subjects)
            {
                q.put(l, q.get(l) +x.get(k).get(l));
            }
        }
        double normQ = q.values().stream().mapToDouble(Double::doubleValue).sum();
        if (Math.abs(normQ - N * category.size()) > 0.1)
        {
            FootPrint.warn("Inconsistent normalization.");
            FootPrint.debug("normQ: " + normQ);
            FootPrint.debug("IK: " + N * category.size());
        }
        for (L l : category)
        {
            q.put(l, q.get(l) / normQ);
        }

        // Calculation by proposed weighted Fleiss' kappa coefficient.
        for (K k : subjects)
        {
            double agreement = agreements.get(k) / norms.get(k);
            if (Double.isNaN(agreement))
            {
                agreement = 0.0; // Pending
            }
            agreements.put(k,  agreements.get(k) / norms.get(k));
        }
        agreement = agreements.values().stream().mapToDouble(Double::doubleValue).sum() / N;
        // Calculation by the ordinary Fleiss' kappa coefficient.
        /*
        for (K k : subjects)
        {
            double agreement = agreements.get(k);
            if (Double.isNaN(agreement))
            {
                agreement = 0.0; // Pending
            }
            agreements.put(k,  agreement);
        }
        agreement = agreements.values().stream().mapToDouble(Double::doubleValue).sum() / N;
        agreement /= judges.size() * (judges.size() - 1) / 2;
         */
        
        for (String aid : judges)
        {
            double normFreq = 0.0;
            for (L c : category)
            {
                normFreq += wfreqs.get(aid).get(c);
            }
            for (L c : category)
            {
                wfreqs.get(aid).put(c, wfreqs.get(aid).get(c) / normFreq);
            }
        }
        
        double pe = 0.0;
        // Calculation of pe (part 1)
        // Use the distribution averaged over all annotators.
        /*
        for (L l : category)
        {
            pe += q.get(l) * q.get(l);
        }
        */
        // Calculation of pe (part 2)
        // Calculates the average of chance of agreement over all pairs of annotators.
        for (List<String> pair : pairs)
        {
            String a1 = pair.get(0);
            String a2 = pair.get(1);
            for (L c : category)
            {
                pe += wfreqs.get(a1).get(c) * wfreqs.get(a2).get(c);
            }
        }
        pe /= pairs.size();
        
        kappa = (agreement - pe) / (1 - pe);
    }
    
    private List<List<String>> getPairComb(Set<String> annotators)
    {
        List<List<String>> pairs = new ArrayList<>();
        
        String[] annotatorArray = new String[annotators.size()];
        annotators.toArray(annotatorArray);
        
        for (int i = 0; i < annotatorArray.length; i++)
        {
            for (int j = i + 1; j < annotatorArray.length; j++)
            {
                pairs.add(Arrays.asList(new String[] {annotatorArray[i], annotatorArray[j]}));
            }
        }
        
        return pairs;
    }
    
    private List<List<String>> getSymmetricPairComb(Set<String> annotators)
    {
        List<List<String>> pairs = new ArrayList<>();
        
        String[] annotatorArray = new String[annotators.size()];
        annotators.toArray(annotatorArray);
        
        for (int i = 0; i < annotatorArray.length; i++)
        {
            for (int j =0; j < annotatorArray.length; j++)
            {
                if (i == j)
                {
                    continue;
                }
                pairs.add(Arrays.asList(new String[] {annotatorArray[i], annotatorArray[j]}));
            }
        }
        
        return pairs;
    }
    
    private double sum(Map<String, Map<L, Double>> norms)
    {
        double sum = 0.0;
        
        for (String a : norms.keySet())
        {
            Map <L, Double>  norm = norms.get(a);
            for (L c : category)
            {
                sum += norm.get(c);
            }
        }
        
        return sum;
    }
    
}
