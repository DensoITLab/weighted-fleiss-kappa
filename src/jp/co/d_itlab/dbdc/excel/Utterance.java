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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This corresponds with a row in an annotation data file.
 */
public class Utterance
{   
    protected static List<FieldName> fieldNames = new ArrayList<>();
    protected Map<String, Field<?>> fieldName2Field;
    //public String fileName;
    
    static
    {
        fieldNames.add(FieldName.FileName);
        fieldNames.add(FieldName.DialogueId);
        fieldNames.add(FieldName.GroupId);
        fieldNames.add(FieldName.SpeakerId);
        fieldNames.add(FieldName.Speaker);
        fieldNames.add(FieldName.Time);
        fieldNames.add(FieldName.TurnIndex);
        fieldNames.add(FieldName.Utterance);
    }

    public Utterance()
    {
        fieldName2Field = new HashMap<>();
        addField(new Field<String>(FieldName.FileName, FieldType.String));
        addField(new Field<String>(FieldName.DialogueId, FieldType.String));
        addField(new Field<String>(FieldName.GroupId, FieldType.String));
        addField(new Field<String>(FieldName.SpeakerId, FieldType.String));
        addField(new Field<String>(FieldName.Speaker, FieldType.String));
        addField(new Field<String>(FieldName.Time, FieldType.Date));
        addField(new Field<Integer>(FieldName.TurnIndex, FieldType.Integer));
        addField(new Field<String>(FieldName.Utterance, FieldType.String));
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

    protected void addField(Field<?> f)
    {
        fieldName2Field.put(f.getName(), f);
    }
    
    public Field<?> getField(String fieldName)
    {
        if (fieldName2Field.containsKey(fieldName))
        {
            return fieldName2Field.get(fieldName);
        }
        else
        {
            return null;
        }
    }
    
    public Field<?> getField(FieldName fieldName)
    {
        return getField(fieldName.getName());
    }
    
    public void setValue(FieldName fieldName, Object value)
    {
        if (fieldName2Field.containsKey(fieldName.getName()))
        {
            Field<?> field = fieldName2Field.get(fieldName.getName());
            switch (field.gettype())
            {
                case String:
                    field.setValue(value);
                    break;
                case Integer:
                    if (value instanceof String)
                    {
                        Integer i = Integer.parseInt((String)value);
                        field.setValue(i);
                    }
                    else
                    {
                        field.setValue(value);
                    }
                    break;
                case Double:
                    if (value instanceof String)
                    {
                        Double d = Double.parseDouble((String)value);
                        field.setValue(d);
                    }
                    else
                    {
                        field.setValue(value);
                    }
                    break;
                default:
                    field.setValue(value);
                    break;
            }
        }
    }
    
    public <T> T getValue(String fieldName)
    {
        if (fieldName2Field.containsKey(fieldName))
        {
            return (T)fieldName2Field.get(fieldName).getValue();
        }
        else
        {
            return null;
        }
    }
    
    public <T> T getValue(FieldName fieldName)
    {
        return getValue(fieldName.getName());
    }
    

     
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof Utterance)
        {
            Utterance u = (Utterance)obj;
            String dialogueId = getValue(FieldName.DialogueId);
            if (!dialogueId.equals(u.getValue(FieldName.DialogueId)))
            {
                return false;
            }
            String turnIndex = getValue(FieldName.TurnIndex);
            if (!turnIndex.equals(u.getValue(FieldName.TurnIndex)))
            {
                return false;
            }
            return true;
        }
        else
        {
            return false;
        }
    }
    
    /**
     * Returns the local ID of an utterance
     */
    public String getId()
    {
        return MessageFormat.format("{0}-{1}-{2}-{3}", getValue(FieldName.FileName), getValue(FieldName.DialogueId), getValue(FieldName.GroupId), getValue(FieldName.TurnIndex));
    }
    
    public String getDialogueId()
    {
        return MessageFormat.format("{0}-{1}-{2}", getValue(FieldName.FileName), getValue(FieldName.DialogueId), getValue(FieldName.GroupId));
    }
    
    public Integer getTurnIndex()
    {
        return getValue(FieldName.TurnIndex);
    }
    
    /**
     * Returns the global ID of an utterance
     */
    public String getKey()
    {
        String filename = ((String)getValue(FieldName.FileName)).replaceAll("\\([^\\)]\\)", "").toUpperCase();
        String key = MessageFormat.format("{0}-{1}", filename, getId());
        return key;
    }
}
