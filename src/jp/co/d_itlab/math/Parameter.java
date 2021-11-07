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

/**
 * Class of named parameters
 */
public class Parameter<T> implements IParameter<T>, Comparable<IParameter<T>>
{
	private String keyName = null;
	private T value = null;
	
	public Parameter(String name)
	{
		setKeyName(name);
	}
	
	public Parameter(String name, T value)
	{
		this(name);
		this.value = value;
	}

	@Override
	public String getKeyName()
	{
		return keyName;
	}

	@Override
	public T getValue()
	{
		return value;
	}

	@Override
	public String setKeyName(String keyName)
	{
		if (keyName == null)
		{
			throw new RuntimeException("Null is not applicable to Paramter as the key name.");
		}
		
		String old = this.keyName;
		this.keyName = keyName;
		return old;
	}

	@Override
	public T setValue(T value)
	{
		T old = this.value;
		this.value = value;
		return old;
	}

	@Override
	public int compareTo(IParameter<T> o)
	{
		return 0;
	}

}
