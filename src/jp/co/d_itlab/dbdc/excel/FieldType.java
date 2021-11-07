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

import org.apache.poi.ss.usermodel.Cell;

public enum FieldType
{
    String,
    Integer,
    Double,
    Date;
    
    public int getEcelCellType()
    {
        switch (this)
        {
            case String:
                return Cell.CELL_TYPE_STRING;
            case Integer:
                return Cell.CELL_TYPE_NUMERIC;
            case Double:
                return Cell.CELL_TYPE_NUMERIC;
            case Date:
                return Cell.CELL_TYPE_STRING;
            default:
                return Cell.CELL_TYPE_STRING;
        }
    }
}
