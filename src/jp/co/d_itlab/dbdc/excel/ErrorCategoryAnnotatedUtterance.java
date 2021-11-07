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

import jp.co.d_itlab.dbdc.logging.FootPrint;

/**
 * @param <L> - Type of labels
 */
public class ErrorCategoryAnnotatedUtterance<L> extends BreakDownAnnotatedUtterance
{
    protected static List<FieldName> fieldNames = new ArrayList<>();
    private List<L> bdLables;
    
    private List<BreakdownCategoryAnnotation<L>> multiAnnotations;
    
    static
    {
        fieldNames.addAll(BreakDownAnnotatedUtterance.fieldNames);
        fieldNames.add(FieldName.Annotator);
        fieldNames.add(FieldName.BreakdownCategory);
        fieldNames.add(FieldName.Remark);
    }
    
    public ErrorCategoryAnnotatedUtterance()
    {
        addField(new Field<String>(FieldName.Annotator, FieldType.String));
        addField(new Field<L>(FieldName.BreakdownCategory, FieldType.String));
        addField(new Field<String>(FieldName.Remark, FieldType.String));
        multiAnnotations = new ArrayList<>();
    }
    
    public static List<String> getFieldNames()
    {
        List<String> ret = new ArrayList<>();
        for (FieldName f : fieldNames)
        {
            ret.add(f.getName());
        }
        return ret;
    }
    
    public static List<String> getErrorCategoryAnnotationFieldNames()
    {
        List<String> names =  new ArrayList<>();
        names.add("annotator-id");
        names.add("breakdown_category");
        names.add("Remark");
        return names;
    }

    public BreakDownAnnotatedUtterance truncateErrorCategoryAnnotation()
    {
        BreakDownAnnotatedUtterance u = this;
        return u;
    }
    
    @Override
    public void setValue(FieldName fieldName, Object value)
    {
        if (FieldName.BreakdownCategory == fieldName)
        {
            // PENDING
            setBdLabel((L)value);
        }
        try
        {
            super.setValue(fieldName, value);
        }
        catch (Exception e)
        {
            FootPrint.debug("Invalid format: Filed Name[{0}], Value[{1}]", fieldName, value);
        }
    }
    
    public List<L> getBdLabels()
    {
        return bdLables;
    }

    public void setBdLabel(L bdLabel)
    {
        if (bdLabel != null)
        {
            // PENDING
            FootPrint.debug("Label: [{0}]", bdLabel);
            List<L> labels = new ArrayList<>();
            for (String label : ((String)bdLabel).split("\\|"))
            {
                label = label.trim();
                FootPrint.debug("Added: [{0}]", label);
                labels.add((L)label);
                if (labels.size() > 1) 
                {
                    FootPrint.debug(((String)label));
                }
            }
            bdLables = labels;
        }
    }
    
    public boolean isMultiAnnotators()
    {
        return multiAnnotations.size() > 1;
    }
    
    public List<BreakdownCategoryAnnotation<L>> getAnnotations()
    {
        return multiAnnotations;
    }
    
    public void addAnnotation(BreakdownCategoryAnnotation<L> a)
    {
        multiAnnotations.add(a);
    }
}
