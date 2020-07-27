package cn.poco.transitions;

public class Quint implements TweenBase
{
	@Override
	public float EaseIn(float t, float b, float c, float d)
	{
		return c * (t /= d) * t * t * t * t + b;
	}

	@Override
	public float EaseOut(float t, float b, float c, float d)
	{
		t = t / d - 1;
		return c * (t * t * t * t * t + 1) + b;
	}

	@Override
	public float EaseInOut(float t, float b, float c, float d)
	{
		if((t /= d * 0.5f) < 1)
		{
			return c * 0.5f * t * t * t * t * t + b;
		}

		t -= 2;
		return c * 0.5f * (t * t * t * t * t + 2) + b;
	}

}
