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
package jp.co.d_itlab.dbdc.excel;

import java.util.ArrayList;
import java.util.List;

public class BreakdownCategoryAnnotation<L>
{
    private String annotator;
    private List<L> labels;
    private String remark;
    /**
     * 
     */
    public BreakdownCategoryAnnotation()
    {
    }
    
    public BreakdownCategoryAnnotation(String annotator, String slabel, String remark)
    {
        this.annotator = annotator;
        this.remark = remark;
        
        if (slabel != null)
        {
            String[] labels = slabel.split("\\|");
            for (String label : labels)
            {
                addLabel((L)label.trim());
            }
        }
    }

    public String getAnnotator()
    {
        return annotator;
    }
    
    public List<L> getLabels()
    {
        return labels;
    }
    
    public void addLabel(L label)
    {
        if (labels == null)
        {
            labels = new ArrayList<>();
        }
        labels.add(label);
    }
    
    public String getRemark()
    {
        return remark;
    }
}
