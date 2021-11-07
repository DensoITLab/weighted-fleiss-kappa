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
package jp.co.d_itlab.math;

import java.text.MessageFormat;

/**
 * Class of named variables.
 */
public class Variable<T> extends Parameter<T> implements IVariable<T>
{
    private boolean hasValue;
    
    /**
     * @param name
     */
    public Variable(String name)
    {
        super(name);
        hasValue = false;
    }

    /*
     * (non-Javadoc)
     * @see jp.co.d_itlab.math.IVariable#hasValue()
     */
    @Override
    public boolean hasValue()
    {
        return hasValue;
    }

    /*
     * (non-Javadoc)
     * @see jp.co.d_itlab.math.IVariable#getName()
     */
    @Override
    public String getName()
    {
        String name = getKeyName();
        return name;
    }

    /*
     * (non-Javadoc)
     * @see jp.co.d_itlab.math.IVariable#value()
     */
    @Override
    public T value()
    {
        return getValue();
    }

    /*
     * (non-Javadoc)
     * @see jp.co.d_itlab.math.IVariable#set(java.lang.Object)
     */
    @Override
    public <S> IVariable<T> set(S v)
    {
        setValue((T)v);
        hasValue = true;
        return this;
    }

    /*
     * (non-Javadoc)
     * @see jp.co.d_itlab.math.Parameter#setValue(java.lang.Object)
     */
    @Override
    public T setValue(T v)
    {
        T old = null;
        old = super.setValue((T)v);
        hasValue = true;
        return old;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        
        boolean isEqual = true;
        if (obj != null)
        {
            if (this.getClass().equals(obj.getClass()))
            {
                IVariable<?> v = (IVariable<?>)obj; 
                String name = getName();
                if (name != null)
                {
                    if (name.equals(v.getName()))
                    {
                        if (this.hasValue)
                        {
                            if (!this.getValue().equals(v.getValue()))
                            {
                                isEqual = false;
                            }
                        }
                    }
                    else
                    {
                        isEqual = false;
                    }
                }
            }
            else
            {
                isEqual = false;
            }
        }
        else
        {
            isEqual = false;
        }
        return isEqual;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder(MessageFormat.format("[Name: {0}, Value: {1}]", getName(), value()));
        return sb.toString();
    }

    

}
