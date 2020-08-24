package cn.poco.transitions;

public class Strong implements TweenBase
{
	@Override
	public float EaseIn(float t, float b, float c, float d)
	{
		return c * (t /= d) * t * t * t * t + b;
	}

	@Override
	public float EaseOut(float t, float b, float c, float d)
	{
		return c * ((t = t / d - 1) * t * t * t * t + 1) + b;
	}

	@Override
	public float EaseInOut(float t, float b, float c, float d)
	{
		if((t /= d * 0.5) < 1)
		{
			return c * 0.5f * t * t * t * t * t + b;
		}
		else
		{
			return c * 0.5f * ((t -= 2) * t * t * t * t + 2) + b;
		}
	}
}
