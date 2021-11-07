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

public class BreakDownAnnotatedUtterance extends Utterance
{
    protected static List<FieldName> fieldNames = new ArrayList<>();
    public String categoryType;
    
    static
    {
        fieldNames.addAll(Utterance.fieldNames);
        fieldNames.add(FieldName.NumAnnotation);
        fieldNames.add(FieldName.NumO);
        fieldNames.add(FieldName.NumT);
        fieldNames.add(FieldName.NumX);
        fieldNames.add(FieldName.Breakdown); // [001]
    }
    
    public BreakDownAnnotatedUtterance()
    {
        addField(new Field<Integer>(FieldName.NumAnnotation, FieldType.Integer));
        addField(new Field<Integer>(FieldName.NumO, FieldType.Integer));
        addField(new Field<Integer>(FieldName.NumT, FieldType.Integer));
        addField(new Field<Integer>(FieldName.NumX, FieldType.Integer));
        addField(new Field<String>(FieldName.Breakdown, FieldType.String)); // [001]
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
    
    public static int getLevel()
    {
        return 1;
    }

    public int getNumAnnotation()
    {
        return getValue(FieldName.NumAnnotation);
    }
    
    public double getNumO()
    {
        Integer v = getValue(FieldName.NumO);
        return v.doubleValue();
    }
    
    public double getNumT()
    {
        Integer v = getValue(FieldName.NumT);
        return v.doubleValue();
    }
    
    public double getNumX()
    {
        Integer v = getValue(FieldName.NumX);
        return v.doubleValue();
    }
    
    public Utterance truncateAnnotation()
    {
        Utterance u = this;
        return u;
    }
}
