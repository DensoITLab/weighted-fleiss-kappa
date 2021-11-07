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

import jp.co.d_itlab.dbdc.excel.BreakDownAnnotatedUtterance;

public class BreakdownClassifier
{
    /**
     * Recognize a given utterance whether it is broken down or not.
     */
    public static boolean isBreakdown(BreakDownAnnotatedUtterance u)
    {
        boolean found = false;
        
        double n = u.getNumT() + u.getNumX();
        if (u.getNumAnnotation() > 0.0 && 2 * n >= u.getNumAnnotation() && n > 1)
        {
            found = true;
        }
        
        return found;
    }
}
