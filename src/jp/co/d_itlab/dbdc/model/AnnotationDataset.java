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
package jp.co.d_itlab.dbdc.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.co.d_itlab.dbdc.excel.ErrorCategoryAnnotatedUtterance;
import jp.co.d_itlab.dbdc.excel.FieldName;
import jp.co.d_itlab.dbdc.logging.FootPrint;
import jp.co.d_itlab.iaa.AnnotationMatrix;

/**
 * Annotation dataset
 * 
 * @param <L> - Type of labels
 */
public class AnnotationDataset<L>
{
    private Map<String, Map<String, ErrorCategoryAnnotatedUtterance<L>>> annotations;
    private List<String> uids;
    private ErrorCategory<L> category;
    private List<Double> weights;
    
    /**
     * Weights are put on each labels equally.
     */
    public AnnotationDataset(ErrorCategory<L> category)
    {
        this(category, null);
    }
    
    /**
     *  Weights are put on labels in accord with given weight design.
     * @param category
     * @param weights
     */
    public AnnotationDataset(ErrorCategory<L> category, List<Double> weights)
    {
        this.category = category;
        this.weights = weights;
        annotations = new HashMap<>();
        uids = new ArrayList<>();
    }
    
    public int getNumAnnotators()
    {
        return annotations.size();
    }
    
    public String[] getAnnotatorIds()
    {
        return annotations.keySet().toArray(new String[0]);
    }
    
    public String[] getAnnotatedUtteranceIds()
    {
        return uids.toArray(new String[0]);
    }
    
    public Map<String, ErrorCategoryAnnotatedUtterance<L>> getAnnotations(String annotatorId)
    {
        if (annotations.containsKey(annotatorId))
        {
            return annotations.get(annotatorId);
        }
        else
        {
            return null;
        }
    }
    
    /**'
     * Returns the number of annotated data.
     */
    public Map<String, Integer> getNumAnnotations()
    {
        if (annotations.size() > 0)
        {
            Map<String, Integer> ret = new HashMap<>();
            for (String aid : annotations.keySet())
            {
                ret.put(aid, annotations.get(aid).size());
            }
            return ret;
        }
        else
        {
            return null;
        }
    }
    
    /**
     * Adds a given data if it is a broken down utterance.
     */
    public boolean add(ErrorCategoryAnnotatedUtterance<L> u)
    {
        boolean isBreakdown = BreakdownClassifier.isBreakdown(u);
        if (isBreakdown)
        {
            if (!annotations.containsKey(u.getValue(FieldName.Annotator)))
            {
                annotations.put(u.getValue(FieldName.Annotator), new HashMap<>());
            }
            annotations.get(u.getValue(FieldName.Annotator)).put(u.getId(), u);
            if (!uids.contains(u.getId()))
            {
                uids.add(u.getId());
            }
            
            // Validation on annotation data
            if (u.getBdLabels() == null)
            {
                FootPrint.warn("Invalid annotation data: [Annotator: {0}, File: {1}, Dialogue ID:{2}, Group ID:{3}, Turn Index: {4}]", u.getValue(FieldName.Annotator), u.getValue(FieldName.FileName), u.getValue(FieldName.DialogueId), u.getValue(FieldName.GroupId), u.getValue(FieldName.TurnIndex));
            }
        }
        return isBreakdown;
    }
    
    public Map<String, AnnotationMatrix<String, L>> getAnnotationMatrices(List<String> aids)
    {
        Map<String, AnnotationMatrix<String, L>> ret = new HashMap<>();
        Map<String, AnnotationMatrix<String, L>> all = getAnnotationMatrices();
        for (String aid : aids)
        {
            if (all.containsKey(aid))
            {
                ret.put(aid, all.get(aid));
            }
        }
        return ret;
    }
    
   
    public Map<String, AnnotationMatrix<String, L>> getAnnotationMatrices()
    {
        Map<String, AnnotationMatrix<String, L>> ret = new HashMap<>();
        for (String aid : annotations.keySet())
        {
            AnnotationMatrix<String, L> m = new AnnotationMatrix<>(aid, uids, category);
            
            Map<String, ErrorCategoryAnnotatedUtterance<L>> labels = annotations.get(aid);
            for (String uid : labels.keySet())
            {
                if (weights == null)
                {
                    // put weights equally on each labels
                    double weight = 1.0 / labels.get(uid).getBdLabels().size();
                    for (L c : labels.get(uid).getBdLabels())
                    {
                        m.add(uid, c, weight);
                    }
                }
                else
                {
                    int numLabels = labels.get(uid).getBdLabels().size();
                    for (int i = 0; i < numLabels; i++)
                    {
                        L c = labels.get(uid).getBdLabels().get(i);
                        double weight = weights.get(i);
                        m.add(uid, c, weight);
                    }
                }
                
                Map<L, Double> row = m.row(uid);
                double sum = 0.0;
                for (L c : row.keySet())
                {
                    sum += row.get(c);
                }
                if (Math.abs(sum - 1.0) > 0.000001)
                {
                    FootPrint.warn("Inconsistent Value");
                }
            }

            ret.put(aid, m);
        }
        return ret;
    }
}
