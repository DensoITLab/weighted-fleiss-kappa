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

public enum FieldName
{
    FileName("file-name"),
    DialogueId("dialogue-id"),
    GroupId("group-id"),
    SpeakerId("speaker-id"),
    Speaker("speaker"),
    Time("time"),
    TurnIndex("turn-index"),
    Utterance("utterance"),
    NumAnnotation("#annotation"),
    NumO("#O"),
    NumT("#T"),
    NumX("#X"),
    Annotator("annotator-id"),
    Breakdown("breakdown"), 
    BreakdownCategory("breakdown_category"),
    Remark("Remark");
    
    private String name;
    
    private FieldName(String name)
    {
        this.name = name;
    }
    
    public String getName()
    {
        return name;
    }
}
