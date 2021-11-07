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

import java.util.HashMap;
import java.util.Map;

public class DialogueDataset
{
    private Map<String, Dialogue> dialogues;
    
    public DialogueDataset()
    {
        dialogues = new HashMap<>();
    }
    
    public void add(Dialogue d)
    {
        if (!dialogues.containsKey(d.getId()))
        {
            dialogues.put(d.getId(), d);
        }
    }
    
    public void update(Dialogue d)
    {
        if (dialogues.containsKey(d.getId()))
        {
            dialogues.put(d.getId(), d);
        }
    }
    
    public String[] getDialogueIds()
    {
        return dialogues.keySet().toArray(new String[0]);
    }
    
    public Dialogue getDialogue(String id)
    {
        if (dialogues.containsKey(id))
        {
            return dialogues.get(id);
        }
        else
        {
            return null;
        }
    }
    
    public Dialogue getDialogueWithUtteranceId(String uid)
    {
        Dialogue found = null;
        
        for (Dialogue d : dialogues.values())
        {
            if (d.getUtterance(uid) != null)
            {
                found = d;
            }
        }
        
        return found;
    }
}
