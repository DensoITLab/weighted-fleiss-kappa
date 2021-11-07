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

public class Field<T>
{
    private String name;
    private FieldType type;
    private T value;
    
    public Field(String name, FieldType type)
    {
        this.name = name;
        this.type = type;
    }
    
    public Field(FieldName name, FieldType type)
    {
        this.name = name.getName();
        this.type = type;
    }
    
    public String getName()
    {
        return name;
    }
    
    public FieldType gettype()
    {
        return type;
    }
    
    public void setValue(Object value)
    {
        this.value = (T)value;
    }
    
    public T getValue()
    {
        return value;
    }
}