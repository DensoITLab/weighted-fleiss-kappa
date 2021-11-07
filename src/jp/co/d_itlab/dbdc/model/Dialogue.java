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
import java.util.List;

import jp.co.d_itlab.dbdc.excel.FieldName;
import jp.co.d_itlab.dbdc.excel.Utterance;

public class Dialogue
{
    private String id;
    private String group_id;
    private String speaker_id;
    private List<Utterance>utterances;
    
    private String systemId;
    
    public Dialogue(String dialogueId)
    {
        this.id = dialogueId;
        utterances = new ArrayList<>();
    }
    
    public void add(Utterance u)
    {
        if (group_id == null)
        {
            group_id = u.getValue(FieldName.GroupId);
        }
        if (speaker_id == null)
        {
            speaker_id = u.getValue(FieldName.SpeakerId);
        }
        utterances.add(u);
    }
    
    public String getId()
    {
        return id;
    }
    
    public String getGroupId()
    {
        return group_id;
    }
    
    public String getSpeakerId()
    {
        return speaker_id;
    }

    public boolean isEqual(String id)
    {
        if (this.id != null)
        {
            return this.id.equals(id);
        }
        else
        {
            return false;
        }
    }
    
    public List<Utterance> getUtterances()
    {
        return utterances;
    }
    
    public Utterance getUtterance(String uid)
    {
        Utterance found = null;
        for (Utterance u : utterances)
        {
            if (u.getId().equals(uid))
            {
                found = u;
                break;
            }
        }
        return found;
    }
    
    public void setSystemId(String sid)
    {
        systemId = sid;
    }
    
    public String getSystemId()
    {
        return systemId;
    }
}
